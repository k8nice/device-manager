package com.device.config;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

/**
 * netty服务
 *
 * @author 宁海博
 */
@Component
@Slf4j
public class NettyServer {

    /**
     * 端口号
     */
    @Value("${netty_port}")
    private static Integer port;

    private static NettyServer nettyServer = new NettyServer();

    public NettyServer() {

    }


    public void setPort(Integer port) {
        this.port = port;
    }

    /**
     * 开启netty server
     *
     * @throws Exception
     */
    public void start() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            ServerBootstrap sb = new ServerBootstrap();
            sb.option(ChannelOption.SO_BACKLOG, 1024);
            sb.option(ChannelOption.TCP_NODELAY, true);
            //开启长连接
            sb.childOption(ChannelOption.SO_KEEPALIVE, true);
            // 绑定线程池
            sb.group(group, bossGroup);
            // 指定使用的channel
            sb.channel(NioServerSocketChannel.class);
            // 绑定监听端口
            sb.localAddress(this.port);
            // 绑定客户端连接时候触发操作
            sb.childHandler(new ChannelInitializer<SocketChannel>() {

                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    LogPrint.print("初始化通道 IP: " + ch.localAddress().getHostName() + "  Port:" + ch.localAddress().getPort());
                    ByteBuf end = Unpooled.copiedBuffer("&&*$$".getBytes());
                    ch.pipeline().addLast(new DelimiterBasedFrameDecoder(32768, end));
                    ch.pipeline().addLast(new IdleStateHandler(720, 720, 720, TimeUnit.SECONDS));
                    ch.pipeline().addLast(new StringEncoder(Charset.forName("UTF-8")));
                    ch.pipeline().addLast(new ByteArrayDecoder());
                    // 客户端触发操作
                    ch.pipeline().addLast(new EchoServerHandler());

                }
            });
            // 服务器异步创建绑定
            ChannelFuture cf = sb.bind().sync();
            LogPrint.print(NettyServer.class + " 启动正在监听,当前地址为:" + cf.channel().localAddress());
            // 关闭服务器通道
            cf.channel().closeFuture().sync();
        } finally {
            // 释放线程池资源
            group.shutdownGracefully().sync();
            bossGroup.shutdownGracefully().sync();
        }
    }

    public void run() throws Exception {
        new NettyServer().start();
    }

    public static void main(String[] args) throws Exception {
        NettyServer nettyServer = new NettyServer();
        nettyServer.setPort(909);
        nettyServer.start();
    }
}