package com.live.room.anchor.component;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameRecorder;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.opencv_core.IplImage;
import javax.swing.*;


public class Camera {

    public static void record(String outputFile) throws FrameGrabber.Exception, FrameRecorder.Exception {
        FrameGrabber grabber = FrameGrabber.createDefault(0);
        grabber.start();
        OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();
        IplImage image = converter.convert(grabber.grab());
        int width = image.width();
        int height = image.height();

        FrameRecorder recorder = FrameRecorder.createDefault(outputFile, width, height);
        recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
        recorder.setFormat("flv");
        recorder.setFrameRate(25);
        recorder.start();

        CanvasFrame frame = new CanvasFrame("主播", CanvasFrame.getDefaultGamma());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setAlwaysOnTop(true);
        Frame rotatedFrame = converter.convert(image);
        long startTime = 0;
        long videoTS = 0;
        while (frame.isVisible() && null != (image = converter.convert(grabber.grab()))) {
            rotatedFrame = converter.convert(image);
            frame.showImage(rotatedFrame);
            if (startTime == 0) {
                startTime = System.currentTimeMillis();
            }
            videoTS = 1000 * (System.currentTimeMillis() - startTime);
            recorder.setTimestamp(videoTS);
            recorder.record(rotatedFrame);
        }

        frame.dispose();
        recorder.close();
        grabber.close();

    }

    public static void main(String[] args) {
        try {
            record("rtmp://192.168.0.100/live");
        } catch (FrameGrabber.Exception e) {
            e.printStackTrace();
        } catch (FrameRecorder.Exception e) {
            e.printStackTrace();
        }
    }
}
