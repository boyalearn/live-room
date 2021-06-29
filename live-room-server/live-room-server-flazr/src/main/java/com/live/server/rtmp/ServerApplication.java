package com.live.server.rtmp;

import com.flazr.rtmp.RtmpHandshake;
import com.flazr.rtmp.client.RtmpClient;
import com.flazr.rtmp.server.RtmpServer;
import org.jboss.netty.buffer.ChannelBuffer;

import java.util.Formatter;

public class ServerApplication {

    public static void main(String[] args) throws Exception {
        RtmpHandshake rtmpHandshake=new RtmpHandshake();
        ChannelBuffer channelBuffer = rtmpHandshake.encodeServer1();
        byte[] bytes=new byte[rtmpHandshake.HANDSHAKE_SIZE];
        channelBuffer.readBytes(bytes);
        System.out.println(bytesToHex(bytes));
        RtmpServer.main(args);
    }

    public static String bytesToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }
}
