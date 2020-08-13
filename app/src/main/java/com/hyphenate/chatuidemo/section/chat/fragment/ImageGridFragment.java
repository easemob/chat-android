package com.hyphenate.chatuidemo.section.chat.fragment;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.hyphenate.chatuidemo.BuildConfig;
import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.common.interfaceOrImplement.OnResourceParseCallback;
import com.hyphenate.chatuidemo.common.utils.ThreadManager;
import com.hyphenate.chatuidemo.common.utils.video.ImageCache;
import com.hyphenate.chatuidemo.common.utils.video.ImageResizer;
import com.hyphenate.chatuidemo.common.utils.video.Utils;
import com.hyphenate.chatuidemo.common.widget.DividerGridItemDecoration;
import com.hyphenate.chatuidemo.common.widget.RecyclingImageView;
import com.hyphenate.chatuidemo.section.base.BaseFragment;
import com.hyphenate.chatuidemo.section.chat.viewmodel.VideoListViewModel;
import com.hyphenate.easeui.interfaces.OnItemClickListener;
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

public class ImageGridFragment extends BaseFragment implements OnItemClickListener {

	private static final String TAG = "ImageGridFragment";
	private int mImageThumbSize;
	private int mImageThumbSpacing;
	private ImageAdapter2 mAdapter;
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
		mAdapter = new ImageAdapter2();
		
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
		final RecyclerView rvVideoGrid = v.findViewById(R.id.rv_video_grid);
		rvVideoGrid.setAdapter(mAdapter);
		DividerGridItemDecoration itemDecoration = new DividerGridItemDecoration(mContext, R.drawable.demo_divider_video_list, false);
		rvVideoGrid.addItemDecoration(itemDecoration);
		mAdapter.setOnItemClickListener(this);
		rvVideoGrid.addOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
				super.onScrollStateChanged(recyclerView, newState);
				// Pause fetcher to ensure smoother scrolling when flinging
				if(newState == RecyclerView.SCROLL_STATE_DRAGGING) {
					// Before Honeycomb pause image loading on scroll to help
					// with performance
					if (!Utils.hasHoneycomb()) {
						mImageResizer.setPauseWork(true);
					}
				}else {
					mImageResizer.setPauseWork(false);
				}
			}
		});

		// This listener is used to get the final width of the GridView and then
		// calculate the
		// number of columns and the width of each column. The width of each
		// column is variable
		// as the GridView has stretchMode=columnWidth. The column width is used
		// to set the height
		// of each view so we get nice square thumbnails.
		rvVideoGrid.getViewTreeObserver().addOnGlobalLayoutListener(
				new ViewTreeObserver.OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						Log.e("TAG", "current Thread  = "+Thread.currentThread().getName());
						final int numColumns = (int) Math.floor(rvVideoGrid
								.getWidth()
								/ (mImageThumbSize + mImageThumbSpacing));
						if (numColumns > 0) {
							final int columnWidth = (rvVideoGrid.getWidth() / numColumns)
									- mImageThumbSpacing;
							mAdapter.setItemHeight(columnWidth);
							if (BuildConfig.DEBUG) {
								Log.d(TAG,
										"onCreateView - numColumns set to "
												+ numColumns);
							}
							rvVideoGrid.getViewTreeObserver()
									.removeOnGlobalLayoutListener(this);
						}
					}
				});
		return v;
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		getVideoFile();
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
	public void onItemClick(View view, int position) {
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

	private class ImageAdapter2 extends RecyclerView.Adapter<ImageAdapter2.ViewHolder> {
		private int mItemHeight;
		private ViewGroup.LayoutParams mImageViewLayoutParams;
		private List<VideoEntity> mData;
		private OnItemClickListener mListener;

		public ImageAdapter2() {
			mImageViewLayoutParams = new ViewGroup.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		}

		@NonNull
		@Override
		public ImageAdapter2.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
			return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.demo_choose_griditem, parent,false));
		}

		@Override
		public void onBindViewHolder(@NonNull ImageAdapter2.ViewHolder holder, int position) {
			holder.itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(mListener != null) {
					    mListener.onItemClick(v, position);
					}
				}
			});
			// Check the height matches our calculated column width
			if (holder.itemView.getLayoutParams().height != mItemHeight) {
				holder.itemView.setLayoutParams(mImageViewLayoutParams);
			}
			if(position==0) {
				holder.icon.setVisibility(View.GONE);
				holder.tvDur.setVisibility(View.GONE);
				holder.llTakeVideo.setVisibility(View.VISIBLE);
				holder.imageView.setImageDrawable(null);
				holder.imageView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.demo_bg_take_video));
				holder.videoDataArea.setVisibility(View.GONE);
				return;
			}
			VideoEntity entity = mData.get(position - 1);
			holder.icon.setVisibility(View.VISIBLE);
			holder.llTakeVideo.setVisibility(View.GONE);
			holder.videoDataArea.setVisibility(View.VISIBLE);
			holder.tvDur.setVisibility(View.VISIBLE);

			holder.tvDur.setText(DateUtils.toTime(entity.duration));
			holder.tvSize.setText(TextFormater.getDataSize(entity.size));
			holder.imageView.setBackground(null);
			holder.imageView.setImageResource(R.drawable.em_empty_photo);
			mImageResizer.loadImage(entity.filePath, holder.imageView);
		}

		@Override
		public int getItemCount() {
			return (mData == null || mData.isEmpty()) ? 1 : mData.size() + 1;
		}

		public void setData(List<VideoEntity> data) {
			this.mData = data;
			notifyDataSetChanged();
		}

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

		public void setOnItemClickListener(OnItemClickListener listener) {
			this.mListener = listener;
		}

		public class ViewHolder extends RecyclerView.ViewHolder {
			LinearLayout llTakeVideo;
			LinearLayout videoDataArea;
			RecyclingImageView imageView;
			ImageView icon;
			TextView tvDur;
			TextView tvSize;

			public ViewHolder(@NonNull View itemView) {
				super(itemView);
				itemView.setLayoutParams(mImageViewLayoutParams);
				imageView = itemView.findViewById(R.id.imageView);
				icon = itemView.findViewById(R.id.video_icon);
				llTakeVideo = itemView.findViewById(R.id.ll_take_video);
				videoDataArea = itemView.findViewById(R.id.video_data_area);
				tvDur = itemView.findViewById(R.id.chatting_length_iv);
				tvSize = itemView.findViewById(R.id.chatting_size_iv);
				imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
			}
		}
	}

	private void getVideoFile() {
		VideoListViewModel viewModel = new ViewModelProvider(mContext).get(VideoListViewModel.class);
		viewModel.getVideoListObservable().observe(getViewLifecycleOwner(), response -> {
			parseResource(response, new OnResourceParseCallback<List<VideoEntity>>() {
				@Override
				public void onSuccess(List<VideoEntity> data) {
					mAdapter.setData(data);
					mImageResizer.setPauseWork(false);
				}
			});
		});
		viewModel.getVideoList(mContext);
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
