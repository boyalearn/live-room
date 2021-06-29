package com.live.server.rtmp.server;


import com.live.server.rtmp.handler.RTMPDecoder;
import com.live.server.rtmp.handler.RTMPEncoder;
import com.live.server.rtmp.handler.RTMPServerHandler;
import com.live.server.rtmp.handler.ServerHandshakeHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class ServerPipelineInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast("handshake", new ServerHandshakeHandler());
        pipeline.addLast("handler", new RTMPServerHandler());
    }
}
