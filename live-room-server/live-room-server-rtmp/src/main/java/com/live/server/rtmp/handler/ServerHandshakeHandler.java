package com.live.server.rtmp.handler;


import com.live.server.rtmp.util.ByteUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ServerHandshakeHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private static final Map<Integer, Integer> clientVersionToValidationTypeMap;

    static {
        Map<Integer, Integer> map = new HashMap<Integer, Integer>();
        map.put(0x09007c02, 1);
        map.put(0x09009702, 1);
        map.put(0x09009f02, 1);
        map.put(0x0900f602, 1);
        map.put(0x0a000202, 1);
        map.put(0x0a000c02, 1);
        map.put(0x80000102, 1);
        map.put(0x80000302, 2);
        map.put(0x0a002002, 2);
        clientVersionToValidationTypeMap = map;
    }


    public static final int HANDSHAKE_SIZE = 1536;

    private boolean rtmpe;

    private byte[] time;

    private byte[] version;

    private ByteBuf onePart;

    private boolean oneStepDone;

    private boolean handshakeDone;

    private int validationType;


    private void decodeC2(ByteBuf in) {
        ByteBuf byteBuf = in.readBytes(HANDSHAKE_SIZE);
        byte[] c2 = new byte[HANDSHAKE_SIZE];
        byteBuf.readBytes(c2);
        if (validationType == 0) {
            return;
        }
    }

    private void decodeClientC0AndC1(ChannelHandlerContext ctx, ByteBuf in) {
        //decode C0
        byte b = in.readByte();
        rtmpe = b == 0x06;
        //decode C1
        ByteBuf byteBuf = in.readBytes(HANDSHAKE_SIZE);
        time = new byte[4];
        byteBuf.getBytes(0, time);
        version = new byte[4];
        byteBuf.getBytes(4, version);
        validationType = getValidationTypeForClientVersion(version);
        byte[] total = new byte[HANDSHAKE_SIZE - 8];
        byteBuf.getBytes(8, total);
        onePart = byteBuf.copy();
    }

    private byte encodeS0() {
        return (byte) (rtmpe ? 0x06 : 0x03);
    }

    private byte[] encodeS1() {
        ByteBuf byteBuf = generateRandomHandshake();
        int current = (int) (new Date().getTime());
        byteBuf.setInt(0, current);
        byteBuf.setInt(4, 0);
        byte[] bytes = new byte[HANDSHAKE_SIZE];
        byteBuf.readBytes(bytes);
        return bytes;
    }

    private byte[] encodeS2() {

        ByteBuf out = generateRandomHandshake();
        int current = (int) (new Date().getTime());
        out.setBytes(0, time);
        out.setInt(4, current);
        byte[] bytes = new byte[HANDSHAKE_SIZE];
        onePart.readBytes(bytes);
        return bytes;
    }

    protected static int getValidationTypeForClientVersion(byte[] version) {
        final int intValue = ByteUtil.toInt(version);
        Integer type = clientVersionToValidationTypeMap.get(intValue);
        if (type == null) {
            return 0;
        }
        return type;
    }

    private ByteBuf generateRandomHandshake() {
        byte[] randomBytes = new byte[HANDSHAKE_SIZE];
        Random random = new Random();
        random.nextBytes(randomBytes);
        return Unpooled.wrappedBuffer(randomBytes);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        if (!oneStepDone) {
            if (in.readableBytes() < HANDSHAKE_SIZE + 1) {
                return;
            }
            decodeClientC0AndC1(ctx, in);
            ctx.writeAndFlush(Unpooled.wrappedBuffer(new byte[]{encodeS0()}));
            ctx.writeAndFlush(Unpooled.wrappedBuffer(encodeS1()));
            ctx.writeAndFlush(Unpooled.wrappedBuffer(encodeS2()));
            oneStepDone = true;
        }
        if (!handshakeDone) {
            if (in.readableBytes() < HANDSHAKE_SIZE) {
                return;
            }
            decodeC2(in);
            handshakeDone = true;
        }
        if (oneStepDone && handshakeDone && in.readableBytes() > 0) {
            ctx.fireChannelRead(in);
        }
    }
}
