package com.hyphenate.easeim.section.me.headImage;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.hyphenate.easeim.R;
import com.hyphenate.easeui.adapter.EaseBaseRecyclerViewAdapter;
import com.hyphenate.easeui.utils.EaseCommonUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import androidx.annotation.NonNull;

/**
 * author lijian
 * email: Allenlee@easemob.com
 * date: 03/16/2021
 */

public class HeadImageAdapter extends EaseBaseRecyclerViewAdapter<HeadImageInfo> {
    public static int chooseIndex = -1;
    @Override
    public ViewHolder getViewHolder(ViewGroup parent, int viewType) {
        return new HeadImageAdapter.HeadImageViewHolder(LayoutInflater.from(mContext).inflate(R.layout.content_head_image_item, parent, false));
    }

    private class HeadImageViewHolder extends ViewHolder<HeadImageInfo> {
        private ImageView ivAvatar;
        private ImageView ivShowSelect;
        private TextView tvAvatar;

        public HeadImageViewHolder(@NonNull View itemView) {
            super(itemView);
            int height = (int)EaseCommonUtils.dip2px(mContext,100);
            itemView.setMinimumHeight(height);
        }

        @Override
        public void initView(View itemView) {
            ivAvatar = itemView.findViewById(R.id.iv_headImage);
            ivShowSelect = itemView.findViewById(R.id.iv_showSelect);
            tvAvatar = itemView.findViewById(R.id.tv_headImage);
        }

        @Override
        public void setData(HeadImageInfo item, int position) {
            tvAvatar.setText(item.getDescribe());
            if(item.getBitmap() == null){
                loadHeadImage(ivAvatar,item,position);
            }else{
                ivAvatar.setImageBitmap(item.getBitmap());
            }
            if(item.getBitmap() != null){
                if(chooseIndex == position){
                    ivAvatar.setImageBitmap(item.getBitmap());
                    ivShowSelect.setVisibility(View.VISIBLE);
                    ivShowSelect.setBackgroundResource(R.drawable.headimage_checked);
              ivAvatar.setImageBitmap(getTransparentBitmap(item.getBitmap(),70));
                }else{
                    ivShowSelect.setVisibility(View.GONE);
                }
            }
        }


        public Bitmap getTransparentBitmap(Bitmap sourceImg, int number){
            int[] argb = new int[sourceImg.getWidth() * sourceImg.getHeight()];

            sourceImg.getPixels(argb, 0, sourceImg.getWidth(), 0, 0, sourceImg

                    .getWidth(), sourceImg.getHeight());// 获得图片的ARGB值
            number = number * 255 / 100;
            for (int i = 0; i < argb.length; i++) {
                if(argb[i]!=0)
                { argb[i] = (number << 24) | (argb[i] & 0x000091FF);
                }
            }
            sourceImg = Bitmap.createBitmap(argb, sourceImg.getWidth(), sourceImg

                    .getHeight(), Bitmap.Config.ARGB_8888);
            return sourceImg;
        }



        private void loadHeadImage(ImageView iv ,HeadImageInfo item,int position) {
            new AsyncTask<String, Void, Bitmap>() {
                @Override
                protected Bitmap doInBackground(String... params) {
                    Bitmap bitmap = null;
                    FutureTarget<Bitmap> futureTarget =
                            Glide.with(mContext)
                                    .asBitmap()
                                    .load(item.getUrl())
                                    .submit(500, 500);
                    try {
                        bitmap = futureTarget.get();
                    }catch (Exception e){
                        e.getStackTrace();
                    }
                    return  bitmap;
                }

                @Override
                protected void onPostExecute(Bitmap bitmap) {
                    if(bitmap != null){
                        item.setBitmap(bitmap);
                        if(chooseIndex == position) {
                            ivShowSelect.setVisibility(View.VISIBLE);
                            ivShowSelect.setBackgroundResource(R.drawable.headimage_checked);
                            ivAvatar.setImageBitmap(getTransparentBitmap(item.getBitmap(), 70));
                        }else{
                            iv.setImageBitmap(bitmap);
                        }
                    }
                }
            }.execute(item.getUrl());
        }
    }
}


