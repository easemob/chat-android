package com.hyphenate.chatuidemo.section.chat.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Group;

import com.hyphenate.chat.EMCallSession;
import com.hyphenate.chat.EMCallStateChangeListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMirror;
import com.hyphenate.chat.EMVideoCallHelper;
import com.hyphenate.chat.EMWaterMarkOption;
import com.hyphenate.chat.EMWaterMarkPosition;
import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.common.utils.PreferenceManager;
import com.hyphenate.easeui.EaseUI;
import com.hyphenate.easeui.utils.PhoneStateManager;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.media.EMCallSurfaceView;
import com.hyphenate.util.EMLog;
import com.superrtc.sdk.VideoView;

import java.io.InputStream;
import java.util.UUID;

public class EaseVideoCallFragment extends EaseCallFragment implements View.OnClickListener {
    private TextView callStateTextView;
    private Group comingBtnContainer;
    private ImageButton refuseBtn;
    private ImageButton answerBtn;
    private ImageButton hangupBtn;
    private ImageView muteImage;
    private ImageView handsFreeImage;
    private TextView nickTextView;
    private Chronometer chronometer;
    private Group voiceContronlLayout;
    private ConstraintLayout rootContainer;
    private LinearLayout topContainer;
    private ConstraintLayout bottomContainer;
    private TextView monitorTextView;
    private TextView netwrokStatusVeiw;
    private Button switchCameraBtn;
    // 视频通话画面显示控件，这里在新版中使用同一类型的控件，方便本地和远端视图切换
    protected EMCallSurfaceView localSurface;
    protected EMCallSurfaceView oppositeSurface;
    private Group groupHangUp;

    private EMVideoCallHelper callHelper;
    private int surfaceState = -1;
    private boolean isMuteState;
    private boolean isHandsfreeState;
    private boolean isAnswered;
    private boolean endCallTriggerByMe = false;
    private boolean monitor = true;
    private boolean isInCalling;

    private Handler uiHandler;

    private Button toggleVideoBtn;
    private Bitmap watermarkbitmap;
    private EMWaterMarkOption watermark;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null) {
            mContext.finish();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.demo_activity_video_call, null);
    }

    @Override
    protected void initArguments() {
        msgid = UUID.randomUUID().toString();
        Bundle bundle = getArguments();
        if(bundle != null) {
            isInComingCall = bundle.getBoolean("isComingCall", false);
            username = bundle.getString("username");
        }
    }

    @Override
    protected void initView() {
        EaseUI.getInstance().isVideoCalling = true;
        callType = 1;

        callStateTextView = (TextView) findViewById(R.id.tv_call_state);
        comingBtnContainer =  findViewById(R.id.ll_coming_call);
        rootContainer =  findViewById(R.id.root_layout);
        refuseBtn = findViewById(R.id.btn_refuse_call);
        answerBtn = findViewById(R.id.btn_answer_call);
        hangupBtn = findViewById(R.id.btn_hangup_call);
        muteImage = (ImageView) findViewById(R.id.iv_mute);
        handsFreeImage = (ImageView) findViewById(R.id.iv_handsfree);
        callStateTextView = (TextView) findViewById(R.id.tv_call_state);
        nickTextView = (TextView) findViewById(R.id.tv_nick);
        chronometer = (Chronometer) findViewById(R.id.chronometer);
        voiceContronlLayout = findViewById(R.id.ll_voice_control);
        topContainer = (LinearLayout) findViewById(R.id.ll_top_container);
        bottomContainer = findViewById(R.id.ll_bottom_container);
        monitorTextView = (TextView) findViewById(R.id.tv_call_monitor);
        netwrokStatusVeiw = (TextView) findViewById(R.id.tv_network_status);
        switchCameraBtn = (Button) findViewById(R.id.btn_switch_camera);
        // local surfaceview
        localSurface = (EMCallSurfaceView) findViewById(R.id.local_surface);
        // remote surfaceview
        oppositeSurface = (EMCallSurfaceView) findViewById(R.id.opposite_surface);
        groupHangUp = findViewById(R.id.group_hang_up);

        nickTextView.setText(username);

        localSurface.setOnClickListener(this);
        localSurface.setZOrderMediaOverlay(true);
        localSurface.setZOrderOnTop(true);

        uiHandler = new Handler();
    }

    @Override
    protected void initListener() {
        refuseBtn.setOnClickListener(this);
        answerBtn.setOnClickListener(this);
        hangupBtn.setOnClickListener(this);
        muteImage.setOnClickListener(this);
        handsFreeImage.setOnClickListener(this);
        rootContainer.setOnClickListener(this);
        switchCameraBtn.setOnClickListener(this);
        // set call state listener
        addCallStateListener();
    }

    @Override
    protected void initData() {
        //获取水印图片
        if(PreferenceManager.getInstance().isWatermarkResolution()) {
            try {
                InputStream in = this.getResources().getAssets().open("watermark.png");
                watermarkbitmap = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            watermark = new EMWaterMarkOption(watermarkbitmap, 75, 25, EMWaterMarkPosition.TOP_RIGHT, 8, 8);
        }
        if (!isInComingCall) {// outgoing call
            soundPool = new SoundPool(1, AudioManager.STREAM_RING, 0);
            outgoing = soundPool.load(mContext, R.raw.em_outgoing, 1);

            comingBtnContainer.setVisibility(View.INVISIBLE);
            groupHangUp.setVisibility(View.VISIBLE);
            groupRequestLayout();
            String st = getResources().getString(R.string.Are_connected_to_each_other);
            callStateTextView.setText(st);
            EMClient.getInstance().callManager().setSurfaceView(localSurface, oppositeSurface);
            handler.sendEmptyMessage(MSG_CALL_MAKE_VIDEO);
            handler.postDelayed(new Runnable() {
                public void run() {
                    streamID = playMakeCallSounds();
                }
            }, 300);
        } else { // incoming call

            callStateTextView.setText("Ringing");
            if(EMClient.getInstance().callManager().getCallState() == EMCallStateChangeListener.CallState.IDLE
                    || EMClient.getInstance().callManager().getCallState() == EMCallStateChangeListener.CallState.DISCONNECTED) {
                // the call has ended
                mContext.finish();
                return;
            }
            voiceContronlLayout.setVisibility(View.INVISIBLE);
            groupRequestLayout();
            localSurface.setVisibility(View.INVISIBLE);
            Uri ringUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            audioManager.setMode(AudioManager.MODE_RINGTONE);
            audioManager.setSpeakerphoneOn(true);
            ringtone = RingtoneManager.getRingtone(mContext, ringUri);
            ringtone.play();
            EMClient.getInstance().callManager().setSurfaceView(localSurface, oppositeSurface);
        }

        final int MAKE_CALL_TIMEOUT = 50 * 1000;
        handler.removeCallbacks(timeoutHangup);
        handler.postDelayed(timeoutHangup, MAKE_CALL_TIMEOUT);

        // get instance of call helper, should be called after setSurfaceView was called
        callHelper = EMClient.getInstance().callManager().getVideoCallHelper();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(isInCalling){
            try {
                EMClient.getInstance().callManager().resumeVideoTransfer();
            } catch (HyphenateException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(isInCalling){
            try {
                EMClient.getInstance().callManager().pauseVideoTransfer();
            } catch (HyphenateException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
        EaseUI.getInstance().isVideoCalling = false;
        stopMonitor();
        localSurface.getRenderer().dispose();
        localSurface = null;
        oppositeSurface.getRenderer().dispose();
        oppositeSurface = null;
        super.onDestroy();
    }

    @Override
    public void onBackPress() {
        callDruationText = chronometer.getText().toString();
        super.onBackPress();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.local_surface) {
            changeCallView();
        } else if (id == R.id.btn_refuse_call) { // decline the call
            isRefused = true;
            refuseBtn.setEnabled(false);
            handler.sendEmptyMessage(MSG_CALL_REJECT);
        } else if (id == R.id.btn_answer_call) { // answer the call
            EMLog.d(TAG, "btn_answer_call clicked");
            answerBtn.setEnabled(false);
            openSpeakerOn();
            if (ringtone != null)
                ringtone.stop();

            callStateTextView.setText("answering...");
            handler.sendEmptyMessage(MSG_CALL_ANSWER);
            handsFreeImage.setImageResource(R.drawable.em_icon_speaker_on);
            isAnswered = true;
            isHandsfreeState = true;
            comingBtnContainer.setVisibility(View.INVISIBLE);
            groupHangUp.setVisibility(View.VISIBLE);
            voiceContronlLayout.setVisibility(View.VISIBLE);
            localSurface.setVisibility(View.VISIBLE);
            groupRequestLayout();
        } else if (id == R.id.btn_hangup_call) { // hangup
            hangupBtn.setEnabled(false);
            chronometer.stop();
            endCallTriggerByMe = true;
            callStateTextView.setText(getResources().getString(R.string.hanging_up));
            EMLog.d(TAG, "btn_hangup_call");
            handler.sendEmptyMessage(MSG_CALL_END);
        } else if (id == R.id.iv_mute) { // mute
            if (isMuteState) {
                // resume voice transfer
                muteImage.setImageResource(R.drawable.em_icon_mute_normal);
                try {
                    EMClient.getInstance().callManager().resumeVoiceTransfer();
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
                isMuteState = false;
            } else {
                // pause voice transfer
                muteImage.setImageResource(R.drawable.em_icon_mute_on);
                try {
                    EMClient.getInstance().callManager().pauseVoiceTransfer();
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
                isMuteState = true;
            }
        } else if (id == R.id.iv_handsfree) { // handsfree
            if (isHandsfreeState) {
                // turn off speaker
                handsFreeImage.setImageResource(R.drawable.em_icon_speaker_normal);
                closeSpeakerOn();
                isHandsfreeState = false;
            } else {
                handsFreeImage.setImageResource(R.drawable.em_icon_speaker_on);
                openSpeakerOn();
                isHandsfreeState = true;
            }
            /*
        case R.id.btn_record_video: //record the video
            if(!isRecording){
//                callHelper.startVideoRecord(PathUtil.getInstance().getVideoPath().getAbsolutePath());
                callHelper.startVideoRecord("/sdcard/");
                EMLog.d(TAG, "startVideoRecord:" + PathUtil.getInstance().getVideoPath().getAbsolutePath());
                isRecording = true;
                recordBtn.setText(R.string.stop_record);
            }else{
                String filepath = callHelper.stopVideoRecord();
                isRecording = false;
                recordBtn.setText(R.string.recording_video);
                Toast.makeText(getApplicationContext(), String.format(getString(R.string.record_finish_toast), filepath), Toast.LENGTH_LONG).show();
            }
            break;
        */
        } else if (id == R.id.root_layout) {
            if (callingState == CallingState.NORMAL) {
                if (bottomContainer.getVisibility() == View.VISIBLE) {
                    bottomContainer.setVisibility(View.INVISIBLE);
                    topContainer.setVisibility(View.INVISIBLE);
                    oppositeSurface.setScaleMode(VideoView.EMCallViewScaleMode.EMCallViewScaleModeAspectFill);

                } else {
                    bottomContainer.setVisibility(View.VISIBLE);
                    topContainer.setVisibility(View.VISIBLE);
                    oppositeSurface.setScaleMode(VideoView.EMCallViewScaleMode.EMCallViewScaleModeAspectFit);
                }
            }
        } else if (id == R.id.btn_switch_camera) { //switch camera
            handler.sendEmptyMessage(MSG_CALL_SWITCH_CAMERA);
        }
    }

    private void groupRequestLayout() {
        comingBtnContainer.requestLayout();
        voiceContronlLayout.requestLayout();
        groupHangUp.requestLayout();
    }

    /**
     * 切换通话界面，这里就是交换本地和远端画面控件设置，以达到通话大小画面的切换
     */
    private void changeCallView() {
        if (surfaceState == 0) {
            surfaceState = 1;
            EMClient.getInstance().callManager().setSurfaceView(oppositeSurface, localSurface);
        } else {
            surfaceState = 0;
            EMClient.getInstance().callManager().setSurfaceView(localSurface, oppositeSurface);
        }
    }

    void stopMonitor(){
        monitor = false;
    }

    /**
     * set call state listener
     */
    void addCallStateListener() {
        callStateListener = new EMCallStateChangeListener() {

            @Override
            public void onCallStateChanged(final CallState callState, final CallError error) {
                if(isActivityDisable()) {
                    return;
                }
                mContext.runOnUiThread(() -> {
                    switch (callState) {
                        case CONNECTING: // is connecting
                            callStateTextView.setText(R.string.Are_connected_to_each_other);
                            break;
                        case CONNECTED: // connected

                            break;
                        case ACCEPTED: // call is accepted
                            surfaceState = 0;
                            handler.removeCallbacks(timeoutHangup);

                            String callId = EMClient.getInstance().callManager().getCurrentCallSession().getCallId();

                            if(!EMClient.getInstance().callManager().getCurrentCallSession().getIscaller()){
                                //推流时设置水印图片
                                if(PreferenceManager.getInstance().isWatermarkResolution()){
                                    EMClient.getInstance().callManager().setWaterMark(watermark);

                                    //开启水印时候本地不开启镜像显示
                                    EMClient.getInstance().callManager().getCallOptions().
                                            setLocalVideoViewMirror(EMMirror.OFF);
                                }else{
                                    EMClient.getInstance().callManager().getCallOptions().
                                            setLocalVideoViewMirror(EMMirror.ON);
                                }
                            }
                            try {
                                if (soundPool != null)
                                    soundPool.stop(streamID);
                                EMLog.d("EMCallManager", "soundPool stop ACCEPTED");
                            } catch (Exception e) {
                            }
                            openSpeakerOn();
                            ((TextView) findViewById(R.id.tv_is_p2p)).setText(EMClient.getInstance().callManager().isDirectCall()
                                    ? R.string.direct_call : R.string.relay_call);
                            handsFreeImage.setImageResource(R.drawable.em_icon_speaker_on);
                            isHandsfreeState = true;
                            isInCalling = true;
                            chronometer.setVisibility(View.VISIBLE);
                            chronometer.setBase(SystemClock.elapsedRealtime());
                            // call durations start
                            chronometer.start();
                            nickTextView.setVisibility(View.INVISIBLE);
                            callStateTextView.setText(R.string.In_the_call);
//                            recordBtn.setVisibility(View.VISIBLE);
                            callingState = CallingState.NORMAL;
                            startMonitor();
                            // Start to watch the phone call state.
                            PhoneStateManager.get(mContext).addStateCallback(phoneStateCallback);

                            break;
                        case NETWORK_DISCONNECTED:
                            netwrokStatusVeiw.setVisibility(View.VISIBLE);
                            netwrokStatusVeiw.setText(R.string.network_unavailable);
                            break;
                        case NETWORK_UNSTABLE:
                            netwrokStatusVeiw.setVisibility(View.VISIBLE);
                            if (error == CallError.ERROR_NO_DATA) {
                                netwrokStatusVeiw.setText(R.string.no_call_data);
                            } else {
                                netwrokStatusVeiw.setText(R.string.network_unstable);
                            }
                            break;
                        case NETWORK_NORMAL:
                            netwrokStatusVeiw.setVisibility(View.INVISIBLE);
                            break;
                        case VIDEO_PAUSE:
                            Toast.makeText(mContext.getApplicationContext(), "VIDEO_PAUSE", Toast.LENGTH_SHORT).show();
                            break;
                        case VIDEO_RESUME:
                            Toast.makeText(mContext.getApplicationContext(), "VIDEO_RESUME", Toast.LENGTH_SHORT).show();

                            break;
                        case VOICE_PAUSE:
                            Toast.makeText(mContext.getApplicationContext(), "VOICE_PAUSE", Toast.LENGTH_SHORT).show();

                            break;
                        case VOICE_RESUME:
                            Toast.makeText(mContext.getApplicationContext(), "VOICE_RESUME", Toast.LENGTH_SHORT).show();

                            break;
                        case DISCONNECTED: // call is disconnected
                            handler.removeCallbacks(timeoutHangup);
                            @SuppressWarnings("UnnecessaryLocalVariable") final CallError fError = error;

                            chronometer.stop();
                            callDruationText = chronometer.getText().toString();
                            String s1 = getResources().getString(R.string.The_other_party_refused_to_accept);
                            String s2 = getResources().getString(R.string.Connection_failure);
                            String s3 = getResources().getString(R.string.The_other_party_is_not_online);
                            String s4 = getResources().getString(R.string.The_other_is_on_the_phone_please);
                            String s5 = getResources().getString(R.string.The_other_party_did_not_answer);

                            String s6 = getResources().getString(R.string.hang_up);
                            String s7 = getResources().getString(R.string.The_other_is_hang_up);
                            String s8 = getResources().getString(R.string.did_not_answer);
                            String s9 = getResources().getString(R.string.Has_been_cancelled);
                            String s10 = getResources().getString(R.string.Refused);
                            String st12 = "service not enable";
                            String st13 = "service arrearages";
                            String st14 = "service forbidden";

                            if (fError == CallError.REJECTED) {
                                callingState = CallingState.BEREFUSED;
                                callStateTextView.setText(s1);
                            } else if (fError == CallError.ERROR_TRANSPORT) {
                                callStateTextView.setText(s2);
                            } else if (fError == CallError.ERROR_UNAVAILABLE) {
                                callingState = CallingState.OFFLINE;
                                callStateTextView.setText(s3);
                            } else if (fError == CallError.ERROR_BUSY) {
                                callingState = CallingState.BUSY;
                                callStateTextView.setText(s4);
                            } else if (fError == CallError.ERROR_NORESPONSE) {
                                callingState = CallingState.NO_RESPONSE;
                                callStateTextView.setText(s5);
                            } else if (fError == CallError.ERROR_LOCAL_SDK_VERSION_OUTDATED || fError == CallError.ERROR_REMOTE_SDK_VERSION_OUTDATED) {
                                callingState = CallingState.VERSION_NOT_SAME;
                                callStateTextView.setText(R.string.call_version_inconsistent);
                            } else if (fError == CallError.ERROR_SERVICE_NOT_ENABLE) {
                                callingState = CallingState.SERVICE_NOT_ENABLE;
                                callStateTextView.setText(st12);
                            } else if (fError == CallError.ERROR_SERVICE_ARREARAGES) {
                                callingState = CallingState.SERVICE_ARREARAGES;
                                callStateTextView.setText(st13);
                            } else if (fError == CallError.ERROR_SERVICE_FORBIDDEN) {
                                callingState = CallingState.SERVICE_NOT_ENABLE;
                                callStateTextView.setText(st14);
                            } else {
                                if (isRefused) {
                                    callingState = CallingState.REFUSED;
                                    callStateTextView.setText(s10);
                                } else if (isAnswered) {
                                    callingState = CallingState.NORMAL;
                                    if (endCallTriggerByMe) {
//                                        callStateTextView.setText(s6);
                                    } else {
                                        callStateTextView.setText(s7);
                                    }
                                } else {
                                    if (isInComingCall) {
                                        callingState = CallingState.UNANSWERED;
                                        callStateTextView.setText(s8);
                                    } else {
                                        if (callingState != CallingState.NORMAL) {
                                            callingState = CallingState.CANCELLED;
                                            callStateTextView.setText(s9);
                                        } else {
                                            callStateTextView.setText(s6);
                                        }
                                    }
                                }
                            }
                            Toast.makeText(mContext, callStateTextView.getText(), Toast.LENGTH_SHORT).show();
                            postDelayedCloseMsg();
                            break;
                    }
                });
            }
        };
        EMClient.getInstance().callManager().addCallStateChangeListener(callStateListener);
    }

    private void postDelayedCloseMsg() {
        uiHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                removeCallStateListener();

                // Stop to watch the phone call state.
                PhoneStateManager.get(mContext).removeStateCallback(phoneStateCallback);

                saveCallRecord();
                Animation animation = new AlphaAnimation(1.0f, 0.0f);
                animation.setDuration(1200);
                rootContainer.startAnimation(animation);
                mContext.finish();
            }

        }, 200);
    }

    /**
     * for debug & testing, you can remove this when release
     */
    void startMonitor(){
        monitor = true;
        EMCallSession callSession = EMClient.getInstance().callManager().getCurrentCallSession();
        final boolean isRecord = callSession.isRecordOnServer();
        final String serverRecordId = callSession.getServerRecordId();

        EMLog.e(TAG, "server record: " + isRecord);
        if (isRecord) {
            EMLog.e(TAG, "server record id: " + serverRecordId);
        }
        final String recordString = " record? " + isRecord + " id: " + serverRecordId;
        new Thread(new Runnable() {
            public void run() {
                while(monitor){
                    if(isActivityDisable()) {
                        return;
                    }
                    mContext.runOnUiThread(new Runnable() {
                        public void run() {
                            monitorTextView.setText("WidthxHeight："+callHelper.getVideoWidth()+"x"+callHelper.getVideoHeight()
                                    + "\nDelay：" + callHelper.getVideoLatency()
                                    + "\nFramerate：" + callHelper.getVideoFrameRate()
                                    + "\nLost：" + callHelper.getVideoLostRate()
                                    + "\nLocalBitrate：" + callHelper.getLocalBitrate()
                                    + "\nRemoteBitrate：" + callHelper.getRemoteBitrate()
                                    + "\n" + recordString);

                            ((TextView)findViewById(R.id.tv_is_p2p)).setText(EMClient.getInstance().callManager().isDirectCall()
                                    ? R.string.direct_call : R.string.relay_call);
                        }
                    });
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }, "CallMonitor").start();
    }

    void removeCallStateListener() {
        EMClient.getInstance().callManager().removeCallStateChangeListener(callStateListener);
    }

    PhoneStateManager.PhoneStateCallback phoneStateCallback = new PhoneStateManager.PhoneStateCallback() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:   // 电话响铃
                    break;
                case TelephonyManager.CALL_STATE_IDLE:      // 电话挂断
                    // resume current voice conference.
                    if (isMuteState) {
                        try {
                            EMClient.getInstance().callManager().resumeVoiceTransfer();
                        } catch (HyphenateException e) {
                            e.printStackTrace();
                        }
                        try {
                            EMClient.getInstance().callManager().resumeVideoTransfer();
                        } catch (HyphenateException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:   // 来电接通 或者 去电，去电接通  但是没法区分
                    // pause current voice conference.
                    if (!isMuteState) {
                        try {
                            EMClient.getInstance().callManager().pauseVoiceTransfer();
                        } catch (HyphenateException e) {
                            e.printStackTrace();
                        }
                        try {
                            EMClient.getInstance().callManager().pauseVideoTransfer();
                        } catch (HyphenateException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }
        }
    };
}
