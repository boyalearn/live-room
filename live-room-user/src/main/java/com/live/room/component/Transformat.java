package com.live.room.component;

import org.bytedeco.ffmpeg.avcodec.AVPacket;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;

public class Transformat {

    public static void main(String[] args) throws FFmpegFrameRecorder.Exception, FFmpegFrameGrabber.Exception {

        String filePath = "rtmp://192.168.0.100:1935/live";
        String ext = filePath.substring(filePath.lastIndexOf("."));
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(filePath);
        grabber.start();
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder("D://move.mp4", grabber.getImageWidth(),
                grabber.getImageHeight(), grabber.getAudioChannels());
        recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
        recorder.setFormat("mp4");
        recorder.setPixelFormat(avutil.AV_PIX_FMT_YUV420P);
        recorder.setFrameRate(grabber.getFrameRate());
        int bitrate = grabber.getVideoBitrate();
        if (bitrate == 0) {
            bitrate = grabber.getAudioBitrate();
        }
        recorder.setVideoBitrate(bitrate);
        recorder.start(grabber.getFormatContext());
        AVPacket packet;
        long dts = 0;
        while ((packet = grabber.grabPacket()) != null) {
            long currentDts = packet.dts();
            if (currentDts >= dts) {
                recorder.recordPacket(packet);
            }
            dts = currentDts;
        }
        recorder.stop();
        recorder.release();
        grabber.stop();
    }
}
