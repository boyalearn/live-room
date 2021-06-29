package com.live.server.rtmp.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

public class MultimediaServer {
    public static void main(String[] args) {
        EventLoopGroup acceptGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(3);
        final ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.childOption(ChannelOption.TCP_NODELAY,true);
        bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);

        bootstrap.group(acceptGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ServerPipelineInitializer());
        try {
            ChannelFuture future = bootstrap.bind(new InetSocketAddress(1935)).sync();
            future.channel().closeFuture().sync();
        } catch (Exception e) {

        } finally {
            acceptGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
