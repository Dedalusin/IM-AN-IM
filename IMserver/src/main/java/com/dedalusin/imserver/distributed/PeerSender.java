package com.dedalusin.imserver.distributed;

import chatBean.Notification;
import chatBean.msg.ProtoMsg;
import codec.ProtobufDecoder;
import com.dedalusin.imserver.handler.ImNodeHeartBeatClientHandler;
import com.dedalusin.imserver.handler.ServerExceptionHandler;
import com.dedalusin.imserver.protoBuilder.NotificationMsgBuilder;
import entity.ImNode;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import util.JsonUtil;

import java.util.Date;
import java.util.concurrent.TimeUnit;


/**
 * 封装与其他节点通信
 */
@Data
@Slf4j
public class PeerSender {
    //重连接次数
    private int reConnectCount = 0;

    private Channel channel;

    private ImNode rmNode;

    private boolean connectFlag = false;

    GenericFutureListener<ChannelFuture> closeListener = (ChannelFuture f) -> {
        log.info("分布式连接已经断开......{}", rmNode.toString());
        channel = null;
        connectFlag = false;
    };

    private GenericFutureListener<ChannelFuture> connectedListener = (ChannelFuture f) -> {
        final EventLoop eventLoop = f.channel().eventLoop();
        if (!f.isSuccess() && ++reConnectCount < 3) {
            log.info("连接失败！ 在10s后准备尝试第{}次重连", reConnectCount);
            eventLoop.schedule(() -> PeerSender.this.doConnect(), 10, TimeUnit.SECONDS);
            connectFlag = false;
        } else {
            connectFlag = true;
            log.info(new Date() + "分布式节点连接成功:{}", rmNode.toString());
            channel = f.channel();
            channel.closeFuture().addListener(closeListener);
            //连接成功发送通知
            Notification<ImNode> notification = new Notification<ImNode>(ImWorker.getInst().getLocalNode());
            notification.setType(Notification.CONNECT_FINISHED);
            String json = JsonUtil.pojoToJson(notification);
            ProtoMsg.Message pkg = NotificationMsgBuilder.buildNotification(json);
            writeAndFlush(pkg);
        }
    };

    private Bootstrap b;
    private EventLoopGroup g;

    public PeerSender(ImNode n) {
        //每一个PeerSender代表一个服务端
        this.rmNode = n;
        //作为客户端连接其它服务端
        b = new Bootstrap();
        g = new NioEventLoopGroup();
    }

    public void doConnect() {
        String host = rmNode.getHost();
        int port = rmNode.getPort();
        try {
            if (b != null && b.group() == null) {
                b.group(g);
                b.channel(NioSocketChannel.class);
                b.option(ChannelOption.SO_KEEPALIVE, true);
                b.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
                b.remoteAddress(host, port);
                //设置通道
                b.handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) {
                        //protobuf的编码解码器、心跳、异常
                        ch.pipeline().addLast("decoder", new ProtobufDecoder());
                        ch.pipeline().addLast("encoder", new ProtobufDecoder());
                        ch.pipeline().addLast("imNodeHeartBeatClientHandler", new ImNodeHeartBeatClientHandler());
                        ch.pipeline().addLast("exceptionHandler", new ServerExceptionHandler());
                    }
                });
                log.info(new Date() + "开始连接分布式节点:{}", rmNode.toString());
                ChannelFuture f = b.connect();
                f.addListener(connectedListener);

            } else if (b.group() != null) {
                log.info(new Date() + "再一次开始连接分布式节点: {}", rmNode.toString());
                ChannelFuture f = b.connect();
                f.addListener(connectedListener);
            }

        } catch (Exception e) {
            log.info("客户端连接失败！" + e.getMessage());
        }
    }
    public void stopConnecting() {
        g.shutdownGracefully();
        connectFlag = false;
    }

    public void writeAndFlush(Object pkg) {
        if (!connectFlag) {
            log.error("分布式节点未连接：{}", rmNode.toString());
            return;
        }
        channel.writeAndFlush(pkg);
    }

}
