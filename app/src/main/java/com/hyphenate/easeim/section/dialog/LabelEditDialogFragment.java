package com.hyphenate.easeim.section.dialog;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

import com.hyphenate.easeim.R;
import com.hyphenate.easeim.section.base.BaseActivity;
import com.hyphenate.easeim.section.base.BaseDialogFragment;
import com.hyphenate.util.EMLog;

public class LabelEditDialogFragment extends BaseDialogFragment {
    private EditText etContent;
    private Button mConfirm;
    private Button mCancel;
    private OnConfirmClickListener listener;
    private Window dialogWindow;

    public interface OnGetSoftHeightListener {
        void onShowed(int height);
    }
    public interface OnSoftKeyWordShowListener {
        void hasShow(boolean isShow);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.AppTheme);
    }

    @Override
    public int getLayoutId() {
        return R.layout.demo_label_popwindow;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        etContent = findViewById(R.id.et_content);
        mConfirm = findViewById(R.id.confirm);
        mCancel = findViewById(R.id.cancel);
    }


    @Override
    public void initListener() {
        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = etContent.getText().toString().trim();
                if(listener != null) {
                    listener.onConfirm(v,content);
                }
                dismiss();
            }
        });

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        KeyboardStateObserver.getKeyboardStateObserver(getActivity()).
                setKeyboardVisibilityListener(new KeyboardStateObserver.OnKeyboardVisibilityListener() {
                    @Override
                    public void onKeyboardShow() {
                        EMLog.d("onGlobalLayout", "listener: onKeyboardShow");
                        if (getActivity() == null) return;
                        getSoftKeyboardHeight(getActivity().getCurrentFocus(), new OnGetSoftHeightListener() {
                            @Override
                            public void onShowed(int height) {
                              EMLog.d("onGlobalLayout", "onShowed: " + height );
                              try {
                                   WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                                   lp.dimAmount = 0.6f;
                                   lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
                                   lp.height = height;
                                   lp.gravity =  Gravity.TOP;
                                   if (getDialog() != null)
                                       setDialogParams(lp);
                              } catch (Exception e) {
                                    e.printStackTrace();
                              }
                            }
                        });
                    }

                    @Override
                    public void onKeyboardHide() {
                        EMLog.d("onGlobalLayout", "listener: onKeyboardHide");
                            try {
                                WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                                lp.dimAmount = 0.6f;
                                lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
                                lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
                                lp.gravity =  Gravity.CENTER;
                                if (getDialog() != null)
                                    setDialogParams(lp);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                });
    }

    @Override
    public void onStart() {
        super.onStart();
        setDialogFullParams();
        dialogWindow = getDialog().getWindow();
    }

    @Override
    public void initArgument() {
        super.initArgument();

    }

    public void setOnConfirmClickListener(OnConfirmClickListener listener) {
        this.listener = listener;
    }

    public interface OnConfirmClickListener {
        void onConfirm(View view, String content);
    }


    /** * 获取软键盘的高度 * *
     @param rootView *
     @param listener
     */
    public static void getSoftKeyboardHeight(final View rootView, final OnGetSoftHeightListener listener) {
        final ViewTreeObserver.OnGlobalLayoutListener layoutListener
                = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                final Rect rect = new Rect();
                rootView.getWindowVisibleDisplayFrame(rect);
                final int screenHeight = rootView.getRootView().getHeight();
                EMLog.d("onGlobalLayout", "screenHeight: " + screenHeight);
                final int heightDifference = screenHeight - rect.bottom;
                EMLog.d("onGlobalLayout", rect.bottom + "#" + screenHeight);
                EMLog.d("onGlobalLayout", "heightDifference: " + heightDifference);
                //设置一个阀值来判断软键盘是否弹出
                boolean visible = heightDifference > screenHeight / 4;
                if (visible) {
                    if (listener != null) {
                        listener.onShowed(screenHeight - heightDifference);
                        EMLog.d("onGlobalLayout", "listener: onShowed");
                    }
                    rootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        };
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);
    }


    public static class Builder {
        OnConfirmClickListener listener;
        private BaseActivity context;

        public Builder(BaseActivity context) {
            this.context = context;
        }

        public Builder setOnConfirmClickListener( OnConfirmClickListener listener) {
            this.listener = listener;
            return this;
        }

        public LabelEditDialogFragment build() {
            LabelEditDialogFragment fragment = new LabelEditDialogFragment();
            fragment.setOnConfirmClickListener(this.listener);
            return fragment;
        }

        public LabelEditDialogFragment show() {
            LabelEditDialogFragment fragment = build();
            FragmentTransaction transaction = context.getSupportFragmentManager().beginTransaction();
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            fragment.show(transaction, null);
            return fragment;
        }

    }


    public static class KeyboardStateObserver {

        private static final String TAG = KeyboardStateObserver.class.getSimpleName();

        public static KeyboardStateObserver getKeyboardStateObserver(Activity activity) {
            return new KeyboardStateObserver(activity);
        }

        private View mChildOfContent;
        private int usableHeightPrevious;
        private OnKeyboardVisibilityListener listener;

        public void setKeyboardVisibilityListener(OnKeyboardVisibilityListener listener) {
            this.listener = listener;
        }

        private KeyboardStateObserver(Activity activity) {
            FrameLayout content = (FrameLayout) activity.findViewById(android.R.id.content);
            mChildOfContent = content.getChildAt(0);
            mChildOfContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                public void onGlobalLayout() {
                    possiblyResizeChildOfContent();
                }
            });
        }

        private void possiblyResizeChildOfContent() {
            int usableHeightNow = computeUsableHeight();
            if (usableHeightNow != usableHeightPrevious) {
                int usableHeightSansKeyboard = mChildOfContent.getRootView().getHeight();
                int heightDifference = usableHeightSansKeyboard - usableHeightNow;
                if (heightDifference > (usableHeightSansKeyboard / 4)) {
                    if (listener != null) {
                        listener.onKeyboardShow();
                    }
                } else {
                    if (listener != null) {
                        listener.onKeyboardHide();
                    }
                }
                usableHeightPrevious = usableHeightNow;
                EMLog.d(TAG,"usableHeightNow: " + usableHeightNow + " | usableHeightSansKeyboard:" + usableHeightSansKeyboard + " | heightDifference:" + heightDifference);
            }
        }

        private int computeUsableHeight() {
            Rect r = new Rect();
            mChildOfContent.getWindowVisibleDisplayFrame(r);

            EMLog.d(TAG,"rec bottom>" + r.bottom + " | rec top>" + r.top);
            return (r.bottom - r.top);// 全屏模式下： return r.bottom
        }

        public interface OnKeyboardVisibilityListener {
            //软键盘弹出
            void onKeyboardShow();
            //软键盘隐藏
            void onKeyboardHide();
        }
    }

}
