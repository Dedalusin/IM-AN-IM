package com.dedalusin.imserver.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import Exception.InvalidFrameException;
@Slf4j
@ChannelHandler.Sharable
public class ServerExceptionHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (!(cause instanceof InvalidFrameException)) {
            cause.printStackTrace();
        }
        log.error(cause.getMessage());
        //TODO 关闭连接
    }
}
