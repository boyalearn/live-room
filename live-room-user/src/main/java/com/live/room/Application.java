package com.live.room;

import com.live.room.component.Player;
import org.bytedeco.javacv.FrameGrabber;

public class Application {
    public static void main(String[] args) throws FrameGrabber.Exception, InterruptedException {
        Player.play("rtsp://192.168.0.100:8554/1");
        //Player.play("rtmp://58.200.131.2:1935/livetv/hunantv");
    }
}
