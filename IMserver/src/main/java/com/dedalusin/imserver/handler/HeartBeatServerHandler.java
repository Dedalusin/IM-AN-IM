package com.dedalusin.imserver.handler;

import chatBean.msg.ProtoMsg;
import cocurrent.FutureTaskScheduler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;
@Slf4j
@ChannelHandler.Sharable
public class HeartBeatServerHandler extends IdleStateHandler {

    private static final int READ_IDLE_GAP = 1500;

    public HeartBeatServerHandler() {
        super(READ_IDLE_GAP, 0, 0, TimeUnit.SECONDS);
    }

    /**
     *
     * @param ctx
     * @param msg
     * @throws Exception
     * 将读到的心跳信息直接回复给客户端
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (null == msg || !(msg instanceof ProtoMsg.Message)) {
            super.channelRead(ctx, msg);
            return;
        }
        ProtoMsg.Message pkg = (ProtoMsg.Message) msg;
        if (pkg.getType().equals(ProtoMsg.HeadType.HEART_BEAT)) {
            FutureTaskScheduler.add(() -> {
                if (ctx.channel().isActive()) {
                    ctx.writeAndFlush(msg);
                }
            });
        }
        super.channelRead(ctx, msg);
    }

    /**
     *
     * @param ctx
     * @param evt
     * @throws Exception
     * 达到空闲条件，关闭连接
     */
    @Override
    protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
        log.info(READ_IDLE_GAP + "秒内未读到数据，关闭连接");
        //TODO 关闭连接
        //SessionManager.inst().closeSession(ctx);
    }
}
