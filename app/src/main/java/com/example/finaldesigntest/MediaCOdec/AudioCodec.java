package com.example.finaldesigntest.MediaCOdec;

import android.media.AudioFormat;
import android.media.MediaCodecInfo;

/**
 * Created by 达明 on 2018/4/9.
 */

/**
 * @author zhangsutao
 * @file AudioCodec.java
 * @brief 音频编解码器基类
 * @date 2016/8/7
 */

public interface AudioCodec {
    String MIME_TYPE="audio/mp4a-latm";
    int KEY_CHANNEL_COUNT=2;
    int KEY_SAMPLE_RATE=44100;
    int KEY_BIT_RATE=64000;
    int KEY_AAC_PROFILE= MediaCodecInfo.CodecProfileLevel.AACObjectLC;
    int WAIT_TIME=10000;

    int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    int CHANNEL_MODE = AudioFormat.CHANNEL_IN_STEREO;

    int BUFFFER_SIZE=2048;
}
