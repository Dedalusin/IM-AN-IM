package com.dedalusin.imserver.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Data;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import util.IPUtil;

import java.net.InetSocketAddress;

@Data
@Service
@Slf4j
public class ChatServer {
    @Value("${server.port}")
    private int port;

    //连接和处理
    private EventLoopGroup bg;
    private EventLoopGroup wg;

    private ServerBootstrap b = new ServerBootstrap();

    //各类handler


    public void run() {
        //1.设置reactor线程
        bg = new NioEventLoopGroup(1);
        wg = new NioEventLoopGroup();
        b.group(bg, wg);
        //2.设置channel类型
        b.channel(NioServerSocketChannel.class);
        //3.设置监听端口
        String ip = IPUtil.getHostAddress();
        b.localAddress(new InetSocketAddress(ip, port));
        //4.设置通道选项
        b.option(ChannelOption.SO_KEEPALIVE, true);
        b.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        //5.装配流水线
        b.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                //添加filter

            }
        });
        //6.绑定server
        ChannelFuture channelFuture = null;
        boolean isStart = false;
        //防止端口占用
        while (!isStart) {
            try {
                channelFuture = b.bind().sync();
                log.info("server启动成功，绑定端口：" + channelFuture.channel().localAddress());
                isStart = true;
            } catch (Exception e) {
                log.error("启动异常 "+e);
                port++;
                log.info("尝试继续绑定下一个端口：" + port);
                b.localAddress(new InetSocketAddress(port));
            }
        }
    }


}
