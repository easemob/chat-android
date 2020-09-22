package com.hyphenate.easeim.section.chat.fragment;

import android.content.Intent;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Group;

import com.hyphenate.chat.EMCallSession;
import com.hyphenate.chat.EMCallStateChangeListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeim.DemoApplication;
import com.hyphenate.easeim.R;
import com.hyphenate.easeim.section.conference.CallFloatWindow;
import com.hyphenate.easeui.EaseUI;
import com.hyphenate.easeui.manager.PhoneStateManager;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.EMLog;

import java.util.UUID;

public class VoiceCallFragment extends EaseCallFragment implements View.OnClickListener {
    private Group comingBtnContainer;
    private ImageButton hangupBtn;
    private ImageButton refuseBtn;
    private ImageButton answerBtn;
    private ImageButton closeBtn;
    private ImageView muteImage;
    private ImageView handsFreeImage;
    private TextView callStateTextView;
    private TextView netwrokStatusVeiw;
    private Group groupHangUp;

    private boolean isMuteState;
    private boolean isHandsfreeState;
    private boolean endCallTriggerByMe = false;
    private boolean monitor = false;

    private Chronometer chronometer;
    String st1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null) {
            mContext.finish();
            return;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.demo_activity_voice_call, null);
    }

    @Override
    protected void initArguments() {
        super.initArguments();
        msgid = UUID.randomUUID().toString();
        Bundle bundle = getArguments();
        if(bundle != null) {
            username = bundle.getString("username");
            isInComingCall = bundle.getBoolean("isComingCall", false);
        }
    }

    @Override
    protected void initView() {
        super.initView();
        EaseUI.getInstance().isVoiceCalling = true;
        callType = 0;

        comingBtnContainer = findViewById(R.id.ll_coming_call);
        refuseBtn = findViewById(R.id.btn_refuse_call);
        answerBtn = findViewById(R.id.btn_answer_call);
        hangupBtn = findViewById(R.id.btn_hangup_call);
        closeBtn = (ImageButton)findViewById(R.id.btn_small_call);
        muteImage = (ImageView) findViewById(R.id.iv_mute);
        handsFreeImage = (ImageView) findViewById(R.id.iv_handsfree);
        callStateTextView = (TextView) findViewById(R.id.tv_call_state);
        TextView nickTextView = (TextView) findViewById(R.id.tv_nick);
        chronometer = (Chronometer) findViewById(R.id.chronometer);
        netwrokStatusVeiw = (TextView) findViewById(R.id.tv_network_status);
        groupHangUp = findViewById(R.id.group_hang_up);

        nickTextView.setText(username);
    }

    @Override
    protected void initListener() {
        super.initListener();
        refuseBtn.setOnClickListener(this);
        answerBtn.setOnClickListener(this);
        hangupBtn.setOnClickListener(this);
        closeBtn.setOnClickListener(this);
        muteImage.setOnClickListener(this);
        handsFreeImage.setOnClickListener(this);
        addCallStateListener();
    }

    @Override
    protected void initData() {
        super.initData();
        if (!isInComingCall) {// outgoing call
            soundPool = new SoundPool(1, AudioManager.STREAM_RING, 0);
            outgoing = soundPool.load(mContext, R.raw.em_outgoing, 1);

            comingBtnContainer.setVisibility(View.INVISIBLE);
            groupHangUp.setVisibility(View.VISIBLE);
            st1 = getResources().getString(R.string.Are_connected_to_each_other);
            callStateTextView.setText(st1);
            handler.sendEmptyMessage(MSG_CALL_MAKE_VOICE);
            handler.postDelayed(new Runnable() {
                public void run() {
                    streamID = playMakeCallSounds();
                }
            }, 300);
        } else { // incoming call
            groupHangUp.setVisibility(View.INVISIBLE);
            comingBtnContainer.setVisibility(View.VISIBLE);
            Uri ringUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            audioManager.setMode(AudioManager.MODE_RINGTONE);
            audioManager.setSpeakerphoneOn(true);
            ringtone = RingtoneManager.getRingtone(mContext, ringUri);
            ringtone.play();
        }
        final int MAKE_CALL_TIMEOUT = 50 * 1000;
        handler.removeCallbacks(timeoutHangup);
        handler.postDelayed(timeoutHangup, MAKE_CALL_TIMEOUT);

        //设置小窗口悬浮类型
        CallFloatWindow.getInstance(DemoApplication.getInstance()).setCallType(CallFloatWindow.CallWindowType.VOICECALL);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onDestroy() {
        EaseUI.getInstance().isVoiceCalling = false;
        stopMonitor();
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
        if (id == R.id.btn_refuse_call) {
            isRefused = true;
            refuseBtn.setEnabled(false);
            handler.sendEmptyMessage(MSG_CALL_REJECT);
        } else if (id == R.id.btn_answer_call) {
            answerBtn.setEnabled(false);
            closeSpeakerOn();
            callStateTextView.setText(R.string.answering);
            comingBtnContainer.setVisibility(View.INVISIBLE);
            groupHangUp.setVisibility(View.VISIBLE);
            comingBtnContainer.requestLayout();
            groupHangUp.requestLayout();
            handler.sendEmptyMessage(MSG_CALL_ANSWER);
        } else if (id == R.id.btn_hangup_call) {
            hangupBtn.setEnabled(false);
            chronometer.stop();
            endCallTriggerByMe = true;
            callStateTextView.setText(getResources().getString(R.string.hanging_up));
            handler.sendEmptyMessage(MSG_CALL_END);
        }else if(id == R.id.btn_small_call) {
            floatState = 0;
            showFloatWindow();
        } else if (id == R.id.iv_mute) {
            if (isMuteState) {
                muteImage.setImageResource(R.drawable.em_icon_mute_normal);
                try {
                    EMClient.getInstance().callManager().resumeVoiceTransfer();
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
                isMuteState = false;
            } else {
                muteImage.setImageResource(R.drawable.em_icon_mute_on);
                try {
                    EMClient.getInstance().callManager().pauseVoiceTransfer();
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
                isMuteState = true;
            }
        } else if (id == R.id.iv_handsfree) {
            if (isHandsfreeState) {
                handsFreeImage.setImageResource(R.drawable.em_icon_speaker_normal);
                closeSpeakerOn();
                isHandsfreeState = false;
            } else {
                handsFreeImage.setImageResource(R.drawable.em_icon_speaker_on);
                openSpeakerOn();
                isHandsfreeState = true;
            }
        }
    }

    /**
     * for debug & testing, you can remove this when release
     */
    void startMonitor(){
        monitor = true;
        EMCallSession callSession = EMClient.getInstance().callManager().getCurrentCallSession();
        final boolean isRecord = callSession.isRecordOnServer();
        final String serverRecordId = callSession.getServerRecordId();

        EMLog.e(TAG, "server record: " + isRecord );
        if (isRecord) {
            EMLog.e(TAG, "server record id: " + serverRecordId);
        }

        new Thread(new Runnable() {
            public void run() {
                if(isActivityDisable()) {
                    return;
                }
                mContext.runOnUiThread(new Runnable() {
                    public void run() {
                        String status = mContext.getString(EMClient.getInstance().callManager().isDirectCall()
                                ? R.string.direct_call : R.string.relay_call);
                        status += " record? " + isRecord;
                        if(isRecord) {
                            status += " id: " + serverRecordId;
                        }
                        ((TextView)findViewById(R.id.tv_is_p2p)).setText(status);
                    }
                });
                while(monitor){
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }, "CallMonitor").start();
    }


    void stopMonitor() {

    }

    /**
     * set call state listener
     */
    void addCallStateListener() {
        callStateListener = new EMCallStateChangeListener() {
            @Override
            public void onCallStateChanged(CallState callState, final CallError error) {
                // Message msg = handler.obtainMessage();
                EMLog.d("EMCallManager", "onCallStateChanged:" + callState);
                if(isActivityDisable()) {
                    return;
                }
                mContext.runOnUiThread(()-> {
                    switch (callState) {

                        case CONNECTING:
                            callStateTextView.setText(st1);
                            break;
                        case CONNECTED:
                            String con = getString(R.string.em_call_voice_request, username);
                            callStateTextView.setText(con);
                            break;
                        case ACCEPTED:
                            handler.removeCallbacks(timeoutHangup);
                            try {
                                if (soundPool != null)
                                    soundPool.stop(streamID);
                            } catch (Exception e) {
                            }
                            if(!isHandsfreeState)
                                closeSpeakerOn();
                            //show relay or direct call, for testing purpose
                            ((TextView)findViewById(R.id.tv_is_p2p)).setText(EMClient.getInstance().callManager().isDirectCall()
                                    ? R.string.direct_call : R.string.relay_call);
                            chronometer.setVisibility(View.VISIBLE);
                            chronometer.setBase(SystemClock.elapsedRealtime());
                            // duration start
                            chronometer.start();
                            String str4 = getResources().getString(R.string.In_the_call);
                            callStateTextView.setText(str4);
                            callingState = CallingState.NORMAL;
                            startMonitor();
                            // Start to watch the phone call state.
                            PhoneStateManager.get(mContext).addStateCallback(phoneStateCallback);
                            break;
                        case NETWORK_UNSTABLE:
                            netwrokStatusVeiw.setVisibility(View.VISIBLE);
                            if(error == CallError.ERROR_NO_DATA){
                                netwrokStatusVeiw.setText(R.string.no_call_data);
                            }else{
                                netwrokStatusVeiw.setText(R.string.network_unstable);
                            }
                            break;
                        case NETWORK_NORMAL:
                            netwrokStatusVeiw.setVisibility(View.INVISIBLE);
                            break;
                        case VOICE_PAUSE:
                            Toast.makeText(mContext.getApplicationContext(), "VOICE_PAUSE", Toast.LENGTH_SHORT).show();
                            break;
                        case VOICE_RESUME:
                            Toast.makeText(mContext.getApplicationContext(), "VOICE_RESUME", Toast.LENGTH_SHORT).show();
                            break;
                        case DISCONNECTED:
                            handler.removeCallbacks(timeoutHangup);
                            @SuppressWarnings("UnnecessaryLocalVariable") final CallError fError = error;
                            chronometer.stop();
                            callDruationText = chronometer.getText().toString();
                            String st1 = getResources().getString(R.string.Refused);
                            String st2 = getResources().getString(R.string.The_other_party_refused_to_accept);
                            String st3 = getResources().getString(R.string.Connection_failure);
                            String st4 = getResources().getString(R.string.The_other_party_is_not_online);
                            String st5 = getResources().getString(R.string.The_other_is_on_the_phone_please);

                            String st6 = getResources().getString(R.string.The_other_party_did_not_answer_new);
                            String st7 = getResources().getString(R.string.hang_up);
                            String st8 = getResources().getString(R.string.The_other_is_hang_up);

                            String st9 = getResources().getString(R.string.did_not_answer);
                            String st10 = getResources().getString(R.string.Has_been_cancelled);
                            String st11 = getResources().getString(R.string.hang_up);
                            String st12 = "service not enable";
                            String st13 = "service arrearages";
                            String st14 = "service forbidden";

                            if (fError == CallError.REJECTED) {
                                callingState = CallingState.BEREFUSED;
                                callStateTextView.setText(st2);
                            } else if (fError == CallError.ERROR_TRANSPORT) {
                                callStateTextView.setText(st3);
                            } else if (fError == CallError.ERROR_UNAVAILABLE) {
                                callingState = CallingState.OFFLINE;
                                callStateTextView.setText(st4);
                            } else if (fError == CallError.ERROR_BUSY) {
                                callingState = CallingState.BUSY;
                                callStateTextView.setText(st5);
                            } else if (fError == CallError.ERROR_NORESPONSE) {
                                callingState = CallingState.NO_RESPONSE;
                                callStateTextView.setText(st6);
                            } else if (fError == CallError.ERROR_LOCAL_SDK_VERSION_OUTDATED || fError == CallError.ERROR_REMOTE_SDK_VERSION_OUTDATED){
                                callingState = CallingState.VERSION_NOT_SAME;
                                callStateTextView.setText(R.string.call_version_inconsistent);
                            } else if(fError == CallError.ERROR_SERVICE_NOT_ENABLE) {
                                callingState = CallingState.SERVICE_NOT_ENABLE;
                                callStateTextView.setText(st12);
                            } else if(fError == CallError.ERROR_SERVICE_ARREARAGES) {
                                callingState = CallingState.SERVICE_ARREARAGES;
                                callStateTextView.setText(st13);
                            } else if(fError == CallError.ERROR_SERVICE_FORBIDDEN) {
                                callingState = CallingState.SERVICE_NOT_ENABLE;
                                callStateTextView.setText(st14);
                            }
                            else {
                                if (isRefused) {
                                    callingState = CallingState.REFUSED;
                                    callStateTextView.setText(st1);
                                }
                                else if (isAnswered) {
                                    callingState = CallingState.NORMAL;
                                    if (endCallTriggerByMe) {
//                                        callStateTextView.setText(st7);
                                    } else {
                                        callStateTextView.setText(st8);
                                    }
                                } else {
                                    if (isInComingCall) {
                                        callingState = CallingState.UNANSWERED;
                                        callStateTextView.setText(st9);
                                    } else {
                                        if (callingState != CallingState.NORMAL) {
                                            callingState = CallingState.CANCELLED;
                                            callStateTextView.setText(st10);
                                        }else {
                                            callStateTextView.setText(st11);
                                        }
                                    }
                                }
                            }
                            CallFloatWindow.getInstance(DemoApplication.getInstance()).dismiss();
                            postDelayedCloseMsg();

                            break;

                        default:
                            break;
                    }
                });


            }
        };
        EMClient.getInstance().callManager().addCallStateChangeListener(callStateListener);
    }

    private void postDelayedCloseMsg() {
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                if(isActivityDisable()) {
                    return;
                }
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("AAA", "CALL DISCONNETED");
                        removeCallStateListener();

                        // Stop to watch the phone call state.
                        PhoneStateManager.get(mContext).removeStateCallback(phoneStateCallback);

                        saveCallRecord();
                        Animation animation = new AlphaAnimation(1.0f, 0.0f);
                        animation.setDuration(800);
                        findViewById(R.id.root_layout).startAnimation(animation);
                        mContext.finish();
                    }
                });
            }
        }, 200);
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
                    }
                    break;
            }
        }
    };

    public void  onNewIntent(Intent intent) {
        // 防止activity在后台被start至前台导致window还存在
        CallFloatWindow.getInstance(DemoApplication.getInstance()).dismiss();
    }
}
