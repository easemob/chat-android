package com.hyphenate.chatuidemo.section.chat.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import androidx.core.content.ContextCompat;

import com.hyphenate.chatuidemo.BuildConfig;
import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.common.utils.ThreadManager;
import com.hyphenate.chatuidemo.common.utils.video.ImageCache;
import com.hyphenate.chatuidemo.common.utils.video.ImageResizer;
import com.hyphenate.chatuidemo.common.utils.video.Utils;
import com.hyphenate.chatuidemo.common.widget.RecyclingImageView;
import com.hyphenate.chatuidemo.section.base.BaseFragment;
import com.hyphenate.chatuidemo.section.chat.RecorderVideoActivity;
import com.hyphenate.easeui.model.VideoEntity;
import com.hyphenate.easeui.utils.EaseCompat;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.hyphenate.util.DateUtils;
import com.hyphenate.util.PathUtil;
import com.hyphenate.util.TextFormater;
import com.hyphenate.util.VersionUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ImageGridFragment extends BaseFragment implements AdapterView.OnItemClickListener {

	private static final String TAG = "ImageGridFragment";
	private int mImageThumbSize;
	private int mImageThumbSpacing;
	private ImageAdapter mAdapter;
	private ImageResizer mImageResizer;
	List<VideoEntity> mList;
	private File videoFile;

	/**
	 * Empty constructor as per the Fragment documentation
	 */
	public ImageGridFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mImageThumbSize = getResources().getDimensionPixelSize(
				R.dimen.image_thumbnail_size);
		mImageThumbSpacing = getResources().getDimensionPixelSize(
				R.dimen.image_thumbnail_spacing);
		mList=new ArrayList<VideoEntity>();
		getVideoFile();
		mAdapter = new ImageAdapter(getActivity());
		
		ImageCache.ImageCacheParams cacheParams=new ImageCache.ImageCacheParams();

		cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of
													// app memory

		// The ImageFetcher takes care of loading images into our ImageView
		// children asynchronously
		mImageResizer = new ImageResizer(getActivity(), mImageThumbSize);
		mImageResizer.setLoadingImage(R.drawable.em_empty_photo);
		mImageResizer.addImageCache(getActivity().getSupportFragmentManager(),
				cacheParams);
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			 ViewGroup container,  Bundle savedInstanceState) {
		final View v = inflater.inflate(R.layout.demo_image_grid_fragment,
				container, false);
		EaseTitleBar title_bar = v.findViewById(R.id.title_bar);
		title_bar.setOnBackPressListener(new EaseTitleBar.OnBackPressListener() {
			@Override
			public void onBackPress(View view) {
				mContext.onBackPressed();
			}
		});
		final GridView mGridView = (GridView) v.findViewById(R.id.gridView);
		mGridView.setAdapter(mAdapter);
		mGridView.setOnItemClickListener(this);
		mGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView absListView,
											 int scrollState) {
				// Pause fetcher to ensure smoother scrolling when flinging
				if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
					// Before Honeycomb pause image loading on scroll to help
					// with performance
					if (!Utils.hasHoneycomb()) {
						mImageResizer.setPauseWork(true);
					}
				} else {
					mImageResizer.setPauseWork(false);
				}
			}

			@Override
			public void onScroll(AbsListView absListView, int firstVisibleItem,
								 int visibleItemCount, int totalItemCount) {
			}
		});

		// This listener is used to get the final width of the GridView and then
		// calculate the
		// number of columns and the width of each column. The width of each
		// column is variable
		// as the GridView has stretchMode=columnWidth. The column width is used
		// to set the height
		// of each view so we get nice square thumbnails.
		mGridView.getViewTreeObserver().addOnGlobalLayoutListener(
				new ViewTreeObserver.OnGlobalLayoutListener() {
					@TargetApi(VERSION_CODES.JELLY_BEAN)
					@Override
					public void onGlobalLayout() {
						final int numColumns = (int) Math.floor(mGridView
								.getWidth()
								/ (mImageThumbSize + mImageThumbSpacing));
						if (numColumns > 0) {
							final int columnWidth = (mGridView.getWidth() / numColumns)
									- mImageThumbSpacing;
							mAdapter.setItemHeight(columnWidth);
							if (BuildConfig.DEBUG) {
								Log.d(TAG,
										"onCreateView - numColumns set to "
												+ numColumns);
							}
							mGridView.getViewTreeObserver()
									.removeOnGlobalLayoutListener(this);
						}
					}
				});
		return v;

	}

	@Override
	public void onResume() {
		super.onResume();
		mImageResizer.setExitTasksEarly(false);
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onPause() {
		super.onPause();
		if(mContext.isFinishing()) {
			mImageResizer.closeCache();
			mImageResizer.clearCache();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View v, final int position, long id) {

		mImageResizer.setPauseWork(true);

		if(position==0)
		{
			videoFile = EaseCompat.takeVideo(this, 100);
		}else{
			VideoEntity vEntty=mList.get(position-1);
			Intent intent;
			if(VersionUtils.isTargetQ(getContext())) {
				intent=getActivity().getIntent().putExtra("uri", vEntty.uri.toString()).putExtra("dur", vEntty.duration);
			}else {
				intent=getActivity().getIntent().putExtra("path", vEntty.filePath).putExtra("dur", vEntty.duration);
			}
			getActivity().setResult(Activity.RESULT_OK, intent);
			getActivity().finish();
		}
	}

	private class ImageAdapter extends BaseAdapter {

		private final Context mContext;
		private int mItemHeight = 0;
		private RelativeLayout.LayoutParams mImageViewLayoutParams;

		public ImageAdapter(Context context) {
			super();
			mContext = context;
			mImageViewLayoutParams = new RelativeLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		}

		@Override
		public int getCount() {
			return mList.size()+1;
		}

		@Override
		public Object getItem(int position) {
			return (position==0)?null:mList.get(position-1);
		}

		@Override
		public long getItemId(int position) {
			return position ;
		}


		@Override
		public View getView(int position, View convertView, ViewGroup container) {
			ViewHolder holder=null;
			if(convertView==null)
			{
				holder=new ViewHolder();
				convertView=LayoutInflater.from(mContext).inflate(R.layout.demo_choose_griditem, container,false);
				holder.imageView=(RecyclingImageView) convertView.findViewById(R.id.imageView);
				holder.icon = (ImageView) convertView.findViewById(R.id.video_icon);
				holder.llTakeVideo = convertView.findViewById(R.id.ll_take_video);
				holder.videoDataArea = convertView.findViewById(R.id.video_data_area);
				holder.tvDur=(TextView)convertView.findViewById(R.id.chatting_length_iv);
				holder.tvSize=(TextView)convertView.findViewById(R.id.chatting_size_iv);
				holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
				holder.imageView.setLayoutParams(mImageViewLayoutParams);
				convertView.setTag(holder);
			}else{
				holder=(ViewHolder) convertView.getTag();
			}

			// Check the height matches our calculated column width
			if (holder.imageView.getLayoutParams().height != mItemHeight) {
				holder.imageView.setLayoutParams(mImageViewLayoutParams);
			}

			// Finally load the image asynchronously into the ImageView, this
			// also takes care of
			// setting a placeholder image while the background thread runs
			String st1 = getResources().getString(R.string.Video_footage);
			if(position==0)
			{
				holder.icon.setVisibility(View.GONE);
				holder.tvDur.setVisibility(View.GONE);
				holder.llTakeVideo.setVisibility(View.VISIBLE);
				holder.imageView.setImageDrawable(null);
				holder.imageView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.demo_bg_take_video));
				holder.videoDataArea.setVisibility(View.GONE);
			}else{
				holder.icon.setVisibility(View.VISIBLE);
				holder.llTakeVideo.setVisibility(View.GONE);
				holder.videoDataArea.setVisibility(View.VISIBLE);
				VideoEntity entty=mList.get(position-1);
				holder.tvDur.setVisibility(View.VISIBLE);

				holder.tvDur.setText(DateUtils.toTime(entty.duration));
				holder.tvSize.setText(TextFormater.getDataSize(entty.size));
				holder.imageView.setBackground(null);
				holder.imageView.setImageResource(R.drawable.em_empty_photo);
				mImageResizer.loadImage(entty.filePath, holder.imageView);
			}
			return convertView;
			// END_INCLUDE(load_gridview_item)
		}

		/**
		 * Sets the item height. Useful for when we know the column width so the
		 * height can be set to match.
		 *
		 * @param height
		 */
		public void setItemHeight(int height) {
			if (height == mItemHeight) {
				return;
			}
			mItemHeight = height;
			mImageViewLayoutParams = new RelativeLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, mItemHeight);
			mImageResizer.setImageSize(height);
			notifyDataSetChanged();
		}

		class ViewHolder{
			LinearLayout llTakeVideo;
			LinearLayout videoDataArea;
			RecyclingImageView imageView;
			ImageView icon;
			TextView tvDur;
			TextView tvSize;
		}
	}

	private void getVideoFile() {
		ThreadManager.getInstance().runOnIOThread(() -> {
			ContentResolver mContentResolver = mContext.getContentResolver();
			Cursor cursor = mContentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI
					, null, null, null, null);
			if (cursor != null && cursor.moveToFirst()) {
				do {
					// ID:MediaStore.Audio.Media._ID
					int id = cursor.getInt(cursor
							.getColumnIndexOrThrow(MediaStore.Video.Media._ID));

					// title：MediaStore.Audio.Media.TITLE
					String title = cursor.getString(cursor
							.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
					// path：MediaStore.Audio.Media.DATA
					String url = null;
					if(!VersionUtils.isTargetQ(getContext())) {
						url = cursor.getString(cursor
								.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
					}

					// duration：MediaStore.Audio.Media.DURATION
					int duration = cursor
							.getInt(cursor
									.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));

					// 大小：MediaStore.Audio.Media.SIZE
					int size = (int) cursor.getLong(cursor
							.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));

					// 最近一次修改时间：MediaStore.Audio.DATE_MODIFIED
					long lastModified = cursor.getLong(cursor
							.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_MODIFIED));

					Uri uri = Uri.parse(MediaStore.Video.Media.EXTERNAL_CONTENT_URI.toString() + File.separator + id);

					VideoEntity entty = new VideoEntity();
					entty.ID = id;
					entty.title = title;
					entty.filePath = url;
					entty.duration = duration;
					entty.size = size;
					entty.uri = uri;
					entty.lastModified = lastModified;
					mList.add(entty);
				} while (cursor.moveToNext());

			}
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}

			getSelfVideoFiles();

			if(mList != null && !mList.isEmpty()) {
				sortVideoEntities();
				mAdapter.notifyDataSetChanged();
			}
		});
	}

	private void getSelfVideoFiles() {
		File videoFolder = PathUtil.getInstance().getVideoPath();
		if(videoFolder.exists() && videoFolder.isDirectory()) {
			File[] files = videoFolder.listFiles();
			if(files != null && files.length > 0) {
				VideoEntity entty;
				for(int i = 0; i < files.length; i++) {
					entty = new VideoEntity();
					File file = files[i];
					if(!EaseCompat.isVideoFile(mContext, file.getName())) {
						continue;
					}
					entty.filePath = file.getAbsolutePath();
					entty.size = (int) file.length();
					entty.title = file.getName();
					entty.lastModified = file.lastModified();
					MediaPlayer player = new MediaPlayer();
					try {
						player.setDataSource(file.getPath());
						player.prepare();
						entty.duration = player.getDuration();
					} catch (IOException e) {
						e.printStackTrace();
					}
					mList.add(entty);
				}
			}
		}

	}

	private void sortVideoEntities() {
		Collections.sort(mList, new Comparator<VideoEntity>() {
			@Override
			public int compare(VideoEntity o1, VideoEntity o2) {
				return (int) (o2.lastModified - o1.lastModified);
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode==Activity.RESULT_OK)
		{
			if(requestCode==100) {
				if(videoFile != null && videoFile.exists()) {
					int duration = 0;
					MediaPlayer player = new MediaPlayer();
					try {
						player.setDataSource(videoFile.getPath());
						player.prepare();
						duration = player.getDuration();
					} catch (IOException e) {
						e.printStackTrace();
					}
					Intent intent = new Intent();
					intent.putExtra("path", videoFile.getAbsolutePath());
					intent.putExtra("dur", duration);
					mContext.setResult(Activity.RESULT_OK, intent);
				}
				mContext.finish();
			}
		}	
	}
}
