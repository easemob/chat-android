package com.hyphenate.easeui.widget.chatrow;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMFileMessageBody;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.utils.EaseImageCache;
import com.hyphenate.easeui.utils.EaseImageUtils;

import java.io.File;

/**
 * image for row
 */
public class EaseChatRowImage extends EaseChatRowFile {
    protected ImageView imageView;
    private EMImageMessageBody imgBody;
    private int maxWidth;
    private int maxHeight;
    private float mRadio;

    public EaseChatRowImage(Context context, boolean isSender) {
        super(context, isSender);
        getScreenInfo(context);
    }

    public EaseChatRowImage(Context context, EMMessage message, int position, Object adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflateView() {
        inflater.inflate(!isSender ? R.layout.ease_row_received_picture
                : R.layout.ease_row_sent_picture, this);
    }

    @Override
    protected void onFindViewById() {
        percentageView = (TextView) findViewById(R.id.percentage);
        imageView = (ImageView) findViewById(R.id.image);
    }

    
    @Override
    protected void onSetUpView() {
        imgBody = (EMImageMessageBody) message.getBody();
        // received messages
        if (message.direct() == EMMessage.Direct.RECEIVE) {
            return;
        }
        String filePath = imgBody.getLocalUrl();
        String thumbPath = EaseImageUtils.getThumbnailImagePath(imgBody.getLocalUrl());
        showImageView(thumbPath, filePath, message);
    }

    @Override
    protected void onViewUpdate(EMMessage msg) {
        if (msg.direct() == EMMessage.Direct.SEND) {
            if(EMClient.getInstance().getOptions().getAutodownloadThumbnail()){
                super.onViewUpdate(msg);
            }else{
                if (imgBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.DOWNLOADING ||
                        imgBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.PENDING ||
                            imgBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.FAILED) {
                    progressBar.setVisibility(View.INVISIBLE);
                    percentageView.setVisibility(View.INVISIBLE);
                    imageView.setImageResource(R.drawable.ease_default_image);
                } else {
                    progressBar.setVisibility(View.GONE);
                    percentageView.setVisibility(View.GONE);
                    imageView.setImageResource(R.drawable.ease_default_image);
                    String thumbPath = imgBody.thumbnailLocalPath();
                    if (!new File(thumbPath).exists()) {
                        // to make it compatible with thumbnail received in previous version
                        thumbPath = EaseImageUtils.getThumbnailImagePath(imgBody.getLocalUrl());
                    }
                    showImageView(thumbPath, imgBody.getLocalUrl(), message);
                }
            }
            return;
        }

        // received messages
        if (imgBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.DOWNLOADING ||
                imgBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.PENDING) {
            if(EMClient.getInstance().getOptions().getAutodownloadThumbnail()){
                imageView.setImageResource(R.drawable.ease_default_image);
            }else {
                progressBar.setVisibility(View.INVISIBLE);
                percentageView.setVisibility(View.INVISIBLE);
                imageView.setImageResource(R.drawable.ease_default_image);
            }
        } else if(imgBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.FAILED){
            if(EMClient.getInstance().getOptions().getAutodownloadThumbnail()){
                progressBar.setVisibility(View.VISIBLE);
                percentageView.setVisibility(View.VISIBLE);
            }else {
                progressBar.setVisibility(View.INVISIBLE);
                percentageView.setVisibility(View.INVISIBLE);
            }
        } else {
            progressBar.setVisibility(View.GONE);
            percentageView.setVisibility(View.GONE);
            imageView.setImageResource(R.drawable.ease_default_image);
            String thumbPath = imgBody.thumbnailLocalPath();
            if (!new File(thumbPath).exists()) {
                // to make it compatible with thumbnail received in previous version
                thumbPath = EaseImageUtils.getThumbnailImagePath(imgBody.getLocalUrl());
            }
            showImageView(thumbPath, imgBody.getLocalUrl(), message);
        }
    }

    /**
     * load image into image view
     * @param thumbernailPath
     * @param localFullSizePath
     * @param message
     */
    private void showImageView(final String thumbernailPath, final String localFullSizePath,final EMMessage message) {
        // first check if the thumbnail image already loaded into cache s
        Bitmap bitmap = EaseImageCache.getInstance().get(thumbernailPath);

        if (bitmap != null) {
            // thumbnail image is already loaded, reuse the drawable
            showImage(bitmap);
        } else {
            imageView.setImageResource(R.drawable.ease_default_image);
            new AsyncTask<Object, Void, Bitmap>() {

                @Override
                protected Bitmap doInBackground(Object... args) {
                    File file = new File(thumbernailPath);
                    if (file.exists()) {
                        return EaseImageUtils.decodeScaleImage(thumbernailPath, maxWidth, maxHeight);
                    } else if (new File(imgBody.thumbnailLocalPath()).exists()) {
                        return EaseImageUtils.decodeScaleImage(imgBody.thumbnailLocalPath(), maxWidth, maxHeight);
                    }
                    else {
                        if (message.direct() == EMMessage.Direct.SEND) {
                            if (localFullSizePath != null && new File(localFullSizePath).exists()) {
                                return EaseImageUtils.decodeScaleImage(localFullSizePath, maxWidth, maxHeight);
                            } else {
                                return null;
                            }
                        } else {
                            return null;
                        }
                    }
                }

                protected void onPostExecute(Bitmap image) {
                    if (image != null) {
                        showImage(image);
                        EaseImageCache.getInstance().put(thumbernailPath, image);
                    }
                }
            }.execute();
        }
    }

    private void getScreenInfo(Context context) {
        float[] screenInfo = EaseCommonUtils.getScreenInfo(context);
        if(screenInfo != null) {
            maxWidth = (int) (screenInfo[0] / 3);
            maxHeight = (int) (screenInfo[1] / 2);
            mRadio = maxWidth * 1.0f / maxHeight;
        }
    }

    /**
     * 展示图片的逻辑如下：
     * 1、图片的宽度不超过屏幕宽度的1/3，高度不超过屏幕宽度1/2，这样的话，图片的长宽比位3：2
     * 2、如果图片的长宽比大于3：2，则选择高度方向与规定一致，宽度方向按比例缩放
     * 3、如果图片的长宽比小于3：2，则选择宽度方向与规定一致，高度方向按比例缩放
     * 4、如果图片的长和宽都小的话，就按照图片的大小展示就好
     * @param bitmap
     */
    private void showImage(Bitmap bitmap) {
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        //获取图片的长和宽
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float radio  = width * 1.0f / height;
        //按原图展示的情况
        if((maxHeight == 0 && maxWidth == 0) || (width <= maxWidth && height <= maxHeight)) {
            imageView.setImageBitmap(bitmap);
            return;
        }
        ViewGroup.LayoutParams params = imageView.getLayoutParams();
        //如果宽度方向大于最大值，且宽高比过大,将图片设置为centerCrop类型
        //宽度方向设置为最大值，高度的话设置为宽度的1/2
        if(mRadio / radio < 0.1f) {
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            params.width = maxWidth;
            params.height = maxWidth / 2;
        }else if(mRadio / radio > 4) {
            //如果高度方向大于最大值，且宽高比过大,将图片设置为centerCrop类型
            //高度方向设置为最大值，宽度的话设置为宽度的1/2
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            params.width = maxHeight / 2;
            params.height = maxHeight;
        }else {
            //对比图片的宽高比，找到最接近最大值的，其余方向，按比例缩放
            if(radio < mRadio) {
                //说明高度方向上更大
                params.height = maxHeight;
                params.width = (int) (maxHeight * radio);
            }else {
                //宽度方向上更大
                params.height = maxHeight;
                params.width = (int) (maxWidth / radio);
            }
        }
        imageView.setImageBitmap(bitmap);
    }

}
