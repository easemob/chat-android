package com.hyphenate.chatuidemo.common.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConferenceStream;
import com.hyphenate.chat.EMStreamStatistics;
import com.hyphenate.chatuidemo.R;
import com.hyphenate.util.EMLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class DebugPanelView extends LinearLayout implements View.OnClickListener {
    private static final String TAG = DebugPanelView.class.getSimpleName();
    private TextView tv_version;
    private ImageButton btn_close;
    private ListView list_stream;
    private TextView tv_stream_id;
    private TextView tv_username_debug;
    private TextView tv_resolution;
    private TextView tv_video_fps;
    private TextView tv_video_bitrate;
    private TextView tv_video_pack_loss;
    private TextView tv_audio_bitrate;
    private TextView tv_audio_pack_loss;
    private OnButtonClickListener onButtonClickListener;
    private List<EMConferenceStream> streamList = new ArrayList<>();
    private EMConferenceStream currentStream;
    private HashMap<String, EMStreamStatistics> streamStatisticsMap = new HashMap<>();
    private StreamAdapter streamAdapter;

    public DebugPanelView(Context context) {
        this(context, null);
    }

    public DebugPanelView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DebugPanelView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.demo_layout_debug_panel, this);
        tv_version = findViewById(R.id.tv_version);
        btn_close = findViewById(R.id.btn_close);
        list_stream = findViewById(R.id.list_stream);
        tv_stream_id = findViewById(R.id.tv_stream_id);
        tv_username_debug = findViewById(R.id.tv_username_debug);
        tv_resolution = findViewById(R.id.tv_resolution);
        tv_video_fps = findViewById(R.id.tv_video_fps);
        tv_video_bitrate = findViewById(R.id.tv_video_bitrate);
        tv_video_pack_loss = findViewById(R.id.tv_video_pack_loss);
        tv_audio_bitrate = findViewById(R.id.tv_audio_bitrate);
        tv_audio_pack_loss = findViewById(R.id.tv_audio_pack_loss);

        tv_version.setText("video debug panel version." + EMClient.VERSION);
        btn_close.setOnClickListener(this);
        streamAdapter = new StreamAdapter(getContext(), streamList);
        list_stream.setAdapter(streamAdapter);
        list_stream.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EMConferenceStream stream = streamList.get(position);
                list_stream.setItemChecked(position, true);
                showDebugInfo(stream);
            }
        });
    }

    private void showDebugInfo(EMConferenceStream stream) {
        currentStream = stream;
        tv_stream_id.setText("Stream Id: " + currentStream.getStreamId());
        tv_username_debug.setText("Username: " + stream.getUsername());
        String targetKey = null;
        if(!streamStatisticsMap.keySet().isEmpty()) {
            Iterator<String> iterator = streamStatisticsMap.keySet().iterator();
            while (iterator.hasNext()) {
                if(iterator.next().startsWith(currentStream.getMemberName())) {
                    targetKey = iterator.next();
                    break;
                }
            }
        }
        if(!TextUtils.isEmpty(targetKey)) {
            EMStreamStatistics statistics = streamStatisticsMap.get(targetKey);
            if(statistics == null) {
                return;
            }
            if(TextUtils.equals(stream.getUsername(), EMClient.getInstance().getCurrentUser())) {
                EMLog.i(TAG, "showDebugInfo, local statistics: " + statistics.toString());
                tv_resolution.setText("Encode Resolution: " + statistics.getLocalEncodedWidth() + " x " + statistics.getLocalEncodedHeight());
                tv_video_fps.setText("Video Encode Fps: " + statistics.getLocalEncodedFps());
                tv_video_bitrate.setText("Video Bitrate: " + statistics.getLocalVideoActualBps());
                tv_video_pack_loss.setText("Video Package Loss: " + statistics.getLocalVideoPacketsLost());
                tv_audio_bitrate.setText("Audio Bitrate: " + statistics.getLocalAudioBps());
                tv_audio_pack_loss.setText("Audio Package Loss: " + statistics.getLocalAudioPacketsLostrate());
            } else {
                EMLog.i(TAG, "showDebugInfo, remote statistics: " + statistics.toString());
                tv_resolution.setText("Resolution: " + statistics.getRemoteHeight() + " x " + statistics.getRemoteWidth());
                tv_video_fps.setText("Video Fps: " + statistics.getRemoteFps());
                tv_video_bitrate.setText("Video Bitrate: " + statistics.getRemoteVideoBps());
                tv_video_pack_loss.setText("Video Package Loss: " + statistics.getRemoteVideoPacketsLost());
                tv_audio_bitrate.setText("Audio Bitrate: " + statistics.getRemoteAudioBps());
                tv_audio_pack_loss.setText("Audio Package Loss: " + statistics.getRemoteAudioPacketsLost());
            }
        }
    }

    public void onStreamStatisticsChange(EMStreamStatistics statistics) {
        streamStatisticsMap.put(statistics.getStreamId(), statistics);
        if(currentStream != null && currentStream.getStreamId() != null && statistics.getStreamId().startsWith(currentStream.getStreamId())) {
            post(()-> {
                showDebugInfo(currentStream);
            });
        }
    }

    public void setStreamListAndNotify(List<EMConferenceStream> streamList) {
        this.streamList.clear();
        this.streamList.addAll(streamList);
        post(()-> {
            streamAdapter.notifyDataSetChanged();
            if(list_stream.getCheckedItemPosition() >= streamList.size()) {
                int index = streamList.indexOf(currentStream);
                if(index > -1) {
                    list_stream.setItemChecked(index, true);
                }else {
                    currentStream = null;
                }

                if(currentStream == null) {
                    currentStream = streamList.get(0);
                    list_stream.setItemChecked(0, true);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_close) {
            EMLog.i(TAG, "btn_close clicked.");
            if(onButtonClickListener != null) {
                onButtonClickListener.onCloseClick(v);
            }
        }
    }

    private class StreamAdapter extends BaseAdapter {
        private Context context;
        private List<EMConferenceStream> streamList;

        public StreamAdapter(Context context, List<EMConferenceStream> streamList) {
            this.context = context;
            this.streamList = streamList;
        }

        @Override
        public int getCount() {
            return streamList == null ? 0 : streamList.size();
        }

        @Override
        public Object getItem(int position) {
            return streamList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return Long.valueOf(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View contentView = convertView;
            ViewHolder viewHolder = null;
            if(contentView == null) {
                contentView = LayoutInflater.from(getContext()).inflate(R.layout.demo_item_layout_debug, null);
                viewHolder = new ViewHolder(contentView);
                contentView.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder) contentView.getTag();
            }
            viewHolder.userNameTV.setText(streamList.get(position).getUsername());

            return contentView;
        }

        private class ViewHolder {
            private TextView userNameTV;

            public ViewHolder(View v) {
                userNameTV = v.findViewById(R.id.tv_username);
            }
        }
    }

    public void setOnButtonClickListener(OnButtonClickListener listener) {
        onButtonClickListener = listener;
    }

    public interface OnButtonClickListener {
        void onCloseClick(View v);
    }
}
