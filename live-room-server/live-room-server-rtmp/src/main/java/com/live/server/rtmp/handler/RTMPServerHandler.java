package com.live.server.rtmp.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class RTMPServerHandler extends SimpleChannelInboundHandler<ByteBuf> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf buf) throws Exception {
        byte b = buf.readByte();
        System.out.println(b);
        String tString = Integer.toBinaryString((b & 0xFF) + 0x100).substring(1);
        System.out.println("tString:" + tString);
    }
}
