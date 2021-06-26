package com.live.room.anchor;

import com.live.room.anchor.component.Camera;

public class Application {
    public static void main(String[] args) throws Exception {
        Camera.record("rtmp://192.168.0.102:1935/live/room");
    }
}
