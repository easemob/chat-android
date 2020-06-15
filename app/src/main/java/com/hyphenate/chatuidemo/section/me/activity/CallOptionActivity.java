package com.hyphenate.chatuidemo.section.me.activity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.common.utils.PreferenceManager;
import com.hyphenate.chatuidemo.common.widget.SwitchItemView;
import com.hyphenate.chatuidemo.section.base.BaseInitActivity;
import com.hyphenate.easeui.widget.EaseTitleBar;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by linan on 16/11/29.
 */
public class CallOptionActivity extends BaseInitActivity implements SwitchItemView.OnCheckedChangeListener {
    private EaseTitleBar titleBar;
    private EditText editMinBitRate;
    private EditText editMaxBitRate;
    private EditText editMaxFrameRate;
    private SwitchItemView rlSwitchExternalAudioInputResolution;
    private Spinner spinnerAudioSampleRate;
    private Spinner spinnerVideoResolutionBack;
    private Spinner spinnerVideoResolutionFront;
    private SwitchItemView rlSwitchFixVideoResolution;
    private SwitchItemView rlSwitchOfflineCallPush;
    private SwitchItemView rlSwitchRecordOnServer;
    private SwitchItemView rlSwitchMergeStream;
    private SwitchItemView rlSwitchWaterMark;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, CallOptionActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.demo_activity_call_option;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        titleBar = findViewById(R.id.title_bar);
        // min video kbps
        editMinBitRate = (EditText)findViewById(R.id.edit_min_bit_rate);
        // max video kbps
        editMaxBitRate = (EditText)findViewById(R.id.edit_max_bit_rate);
        // max frame rate
        editMaxFrameRate = (EditText)findViewById(R.id.edit_max_frame_rate);
        rlSwitchExternalAudioInputResolution = findViewById(R.id.rl_switch_external_audioInput_resolution);
        spinnerAudioSampleRate = findViewById(R.id.spinner_audio_sample_rate);
        spinnerVideoResolutionBack = findViewById(R.id.spinner_video_resolution_back);
        spinnerVideoResolutionFront = findViewById(R.id.spinner_video_resolution_front);
        rlSwitchFixVideoResolution = findViewById(R.id.rl_switch_fix_video_resolution);
        rlSwitchOfflineCallPush = findViewById(R.id.rl_switch_offline_call_push);
        rlSwitchRecordOnServer = findViewById(R.id.rl_switch_record_on_server);
        rlSwitchMergeStream = findViewById(R.id.rl_switch_merge_stream);
        rlSwitchWaterMark = findViewById(R.id.rl_switch_water_mark);
    }

    @Override
    protected void initListener() {
        super.initListener();
        titleBar.setOnBackPressListener(new EaseTitleBar.OnBackPressListener() {
            @Override
            public void onBackPress(View view) {
                onBackPressed();
            }
        });
        editMinBitRate.addTextChangedListener(new MyTextChangedListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    EMClient.getInstance().callManager().getCallOptions().setMinVideoKbps(new Integer(s.toString()).intValue());
                    PreferenceManager.getInstance().setCallMinVideoKbps(new Integer(s.toString()).intValue());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        editMaxBitRate.addTextChangedListener(new MyTextChangedListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    EMClient.getInstance().callManager().getCallOptions().setMaxVideoKbps(new Integer(s.toString()).intValue());
                    PreferenceManager.getInstance().setCallMaxVideoKbps(new Integer(s.toString()).intValue());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        editMaxFrameRate.addTextChangedListener(new MyTextChangedListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    EMClient.getInstance().callManager().getCallOptions().setMaxVideoFrameRate(new Integer(s.toString()).intValue());
                    PreferenceManager.getInstance().setCallMaxFrameRate(new Integer(s.toString()).intValue());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        rlSwitchExternalAudioInputResolution.setOnCheckedChangeListener(this);
        rlSwitchFixVideoResolution.setOnCheckedChangeListener(this);
        rlSwitchOfflineCallPush.setOnCheckedChangeListener(this);
        rlSwitchRecordOnServer.setOnCheckedChangeListener(this);
        rlSwitchMergeStream.setOnCheckedChangeListener(this);
        rlSwitchWaterMark.setOnCheckedChangeListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        editMinBitRate.setText("" + PreferenceManager.getInstance().getCallMinVideoKbps());
        editMaxBitRate.setText("" + PreferenceManager.getInstance().getCallMaxVideoKbps());
        editMaxFrameRate.setText("" + PreferenceManager.getInstance().getCallMaxFrameRate());
        // audio sample rate
        initAudioSampleRateSpinner(R.id.spinner_audio_sample_rate);

        /**
         * Back camera and front camera share the same API: EMCallOptions.setVideoResolution(w, h);
         */
        initCameraResolutionSpinner(Camera.CameraInfo.CAMERA_FACING_BACK, R.id.spinner_video_resolution_back);
        initCameraResolutionSpinner(Camera.CameraInfo.CAMERA_FACING_FRONT, R.id.spinner_video_resolution_front);

        rlSwitchExternalAudioInputResolution.getSwitch().setChecked(PreferenceManager.getInstance().isExternalAudioInputResolution());
        rlSwitchWaterMark.getSwitch().setChecked(PreferenceManager.getInstance().isWatermarkResolution());
        rlSwitchFixVideoResolution.getSwitch().setChecked(PreferenceManager.getInstance().isCallFixedVideoResolution());
        rlSwitchOfflineCallPush.getSwitch().setChecked(PreferenceManager.getInstance().isPushCall());
        rlSwitchRecordOnServer.getSwitch().setChecked(PreferenceManager.getInstance().isRecordOnServer());
        rlSwitchMergeStream.getSwitch().setChecked(PreferenceManager.getInstance().isMergeStream());
    }

    void initCameraResolutionSpinner(final int cameraId, final int spinnerId) {
        // for simulator which doesn't has camera, open will fail
        Camera mCameraDevice = null;
        try {
            mCameraDevice = Camera.open(cameraId);
            Camera.Parameters parameters = mCameraDevice.getParameters();

            final List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
            List<String> strSizes = new ArrayList<String>();
            strSizes.add("Not Set");

            for (Camera.Size size : sizes) {
                String str = "" + size.width + "x" + size.height;
                strSizes.add(str);
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, strSizes);
            adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
            final Spinner spinnerVideoResolution = (Spinner) findViewById(spinnerId);
            spinnerVideoResolution.setAdapter(adapter);

            // update selection
            int selection = 0;
            String resolution = cameraId == Camera.CameraInfo.CAMERA_FACING_BACK ?
                    PreferenceManager.getInstance().getCallBackCameraResolution() :
                    PreferenceManager.getInstance().getCallFrontCameraResolution();
            if (resolution.equals("") || resolution.equals("Not Set")) {
                selection = 0;
            } else {
                for (int i = 1; i < strSizes.size(); i++) {
                    if (resolution.equals(strSizes.get(i))) {
                        selection = i;
                        break;
                    }
                }
            }
            if (selection < strSizes.size()) {
                spinnerVideoResolution.setSelection(selection);
            }

            /**
             * Spinner onItemSelected is obscure
             * setSelection will trigger onItemSelected
             * if spinner.setSelection(newValue)'s newValue == spinner.getSelectionPosition(), it will not trigger onItemSelected
             *
             * The target we want to archive are:
             * 1. select one spinner, clear another
             * 2. set common text
             * 3. if another spinner is already at position 0, ignore it
             * 4. Use disableOnce AtomicBoolean to record whether is spinner.setSelected(x) triggered action, which should be ignored
             */
            AtomicBoolean disableOnce = new AtomicBoolean(false);
            spinnerVideoResolution.setTag(disableOnce);
            spinnerVideoResolution.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    AtomicBoolean disable = (AtomicBoolean)spinnerVideoResolution.getTag();
                    if (disable.get() == true) {
                        disable.set(false);
                        return;
                    }

                    if (position == 0) {
                        TextView textVideoResolution = (TextView)findViewById(R.id.text_video_resolution);
                        textVideoResolution.setText("Set video resolution:" + " Not Set");

                        PreferenceManager.getInstance().setCallBackCameraResolution("");
                        PreferenceManager.getInstance().setCallFrontCameraResolution("");
                        return;
                    }
                    Camera.Size size = sizes.get(position - 1);
                    if (size != null) {
                        EMClient.getInstance().callManager().getCallOptions().setVideoResolution(size.width, size.height);
                        TextView textVideoResolution = (TextView)findViewById(R.id.text_video_resolution);
                        textVideoResolution.setText("Set video resolution:" + size.width + "x" + size.height);

                        if (cameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
                            PreferenceManager.getInstance().setCallBackCameraResolution(size.width + "x" + size.height);
                            PreferenceManager.getInstance().setCallFrontCameraResolution("");
                            Spinner frontSpinner = (Spinner) findViewById(R.id.spinner_video_resolution_front);
                            if (frontSpinner.getSelectedItemPosition() != 0) {
                                AtomicBoolean disableOnce = (AtomicBoolean) frontSpinner.getTag();
                                disableOnce.set(true);
                                frontSpinner.setSelection(0);
                            }

                        } else {
                            PreferenceManager.getInstance().setCallFrontCameraResolution(size.width + "x" + size.height);
                            PreferenceManager.getInstance().setCallBackCameraResolution("");
                            Spinner backSpinner = (Spinner) findViewById(R.id.spinner_video_resolution_back);
                            if (backSpinner.getSelectedItemPosition() != 0) {
                                AtomicBoolean disableOnce = (AtomicBoolean) backSpinner.getTag();
                                disableOnce.set(true);
                                backSpinner.setSelection(0);
                            }
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mCameraDevice != null) {
                mCameraDevice.release();
            }
        }
    }

    void initAudioSampleRateSpinner(int spinnerId) {
        final List<String> sampleRateList = new ArrayList<String>();
        // Notice: some of devices may not support 48KHz, but 16KHz is widely accepted
        sampleRateList.add("Not set(prefer 16KHz)");
        sampleRateList.add("8000Hz");
        sampleRateList.add("11025Hz");
        sampleRateList.add("22050Hz");
        sampleRateList.add("16000Hz");
        sampleRateList.add("44100Hz");
        sampleRateList.add("48000Hz");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, sampleRateList);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        Spinner spinnerAudioSampleRate = (Spinner) findViewById(spinnerId);
        spinnerAudioSampleRate.setAdapter(adapter);

        spinnerAudioSampleRate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
             @Override
             public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                 // Not set
                 if (position == 0) {
                     return;
                 }
                 String audioSampleRate = sampleRateList.get(position);
                 if (audioSampleRate != null) {
                     try {
                         String data = audioSampleRate.substring(0, audioSampleRate.length() - 2);
                         int hz = new Integer(data).intValue();
                         PreferenceManager.getInstance().setCallAudioSampleRate(hz);

                         boolean externalAudioflag  = PreferenceManager.getInstance().isExternalAudioInputResolution();
                         EMClient.getInstance().callManager().getCallOptions().setExternalAudioParam(externalAudioflag,hz,1);
                     } catch (Exception e) {
                         e.printStackTrace();
                     }
                 }
             }

             @Override
             public void onNothingSelected(AdapterView<?> parent) {}
         });

        // update selection
        int selection = 0;
        int audioSampleRate = PreferenceManager.getInstance().getCallAudioSampleRate();
        if (audioSampleRate == -1) {
            selection = 0;
        } else {
            String selText = "" + audioSampleRate + "Hz";
            for (int i = 1; i < sampleRateList.size(); i++) {
                if (selText.equals(sampleRateList.get(i))) {
                    selection = i;
                    break;
                }
            }
        }
        if (selection < sampleRateList.size()) {
            spinnerAudioSampleRate.setSelection(selection);
        }
    }

    @Override
    public void onCheckedChanged(SwitchItemView buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.rl_switch_fix_video_resolution:
                EMClient.getInstance().callManager().getCallOptions().enableFixedVideoResolution(isChecked);
                PreferenceManager.getInstance().setCallFixedVideoResolution(isChecked);
                break;
            case R.id.rl_switch_offline_call_push:
                EMClient.getInstance().callManager().getCallOptions().setIsSendPushIfOffline(isChecked);
                PreferenceManager.getInstance().setPushCall(isChecked);
                break;
            case R.id.rl_switch_record_on_server:
                PreferenceManager.getInstance().setRecordOnServer(isChecked);
                break;
            case R.id.rl_switch_merge_stream:
                PreferenceManager.getInstance().setMergeStream(isChecked);
                break;
            case R.id.rl_switch_external_audioInput_resolution:
                PreferenceManager.getInstance().setExternalAudioInputResolution(isChecked);
                int hz = PreferenceManager.getInstance().getCallAudioSampleRate();
                if(hz == -1){
                    hz = 16000;
                }
                EMClient.getInstance().callManager().getCallOptions().setExternalAudioParam(isChecked,hz,1);
            case R.id.rl_switch_water_mark:
                PreferenceManager.getInstance().setWatermarkResolution(isChecked);
            default:
                break;
        }
    }

    private abstract class MyTextChangedListener implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }
}
