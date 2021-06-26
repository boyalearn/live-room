package com.live.room.component;

import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;

import javax.swing.*;

public class Player {

    public static void play(String playUrl) throws FFmpegFrameGrabber.Exception {
        FFmpegFrameGrabber grabber = FFmpegFrameGrabber.createDefault(playUrl);
        grabber.setOption("probesize", String.valueOf(100 * 1024));
        grabber.setOption("rtsp_transport", "tcp");
        grabber.setOption("max_analyze_duration", String.valueOf(1000));
        grabber.start();
        CanvasFrame canvasFrame = new CanvasFrame("摄像机");
        canvasFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        canvasFrame.setAlwaysOnTop(false);
        while (true) {
            Frame frame = grabber.grabImage();
            if (null != frame) {
                canvasFrame.showImage(frame);
            }
        }
    }

}
