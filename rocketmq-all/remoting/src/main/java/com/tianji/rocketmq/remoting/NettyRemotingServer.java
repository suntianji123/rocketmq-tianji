package com.tianji.rocketmq.remoting;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultEventExecutorGroup;

import java.net.InetSocketAddress;
import java.util.concurrent.ThreadFactory;

public class NettyRemotingServer {

    /**
     * 服务器启动配置
     */
    private final ServerBootstrap serverBootstrap;

    /**
     * 轮训NioServerSocketChannel的轮训器工作线程组
     */
    private final EventLoopGroup eventLoopGroupBoos;

    /**
     * 客户端NioSocketChannel的轮训器工作线程
     */
    private final EventLoopGroup eventloopGroupSelector;

    /**
     * NioSocketChannel io事件执行器
     */
    private final DefaultEventExecutorGroup defaultEventExecutorGroup;

    public NettyRemotingServer(){
        serverBootstrap = new ServerBootstrap();
        eventLoopGroupBoos =  new NioEventLoopGroup(1);
        eventloopGroupSelector = new NioEventLoopGroup(3);
        defaultEventExecutorGroup = new DefaultEventExecutorGroup(8);
    }

    public void start(){
        serverBootstrap.group(eventLoopGroupBoos,eventloopGroupSelector)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG,1024)
                .option(ChannelOption.SO_REUSEADDR,true)
                .option(ChannelOption.SO_KEEPALIVE,false)
                .option(ChannelOption.TCP_NODELAY,true)
                .option(ChannelOption.SO_SNDBUF,65535)
                .option(ChannelOption.SO_RCVBUF,65535)
                .localAddress(new InetSocketAddress(8888))
                .childHandler(new ChannelInitializer<SocketChannel>() {//添加处理读取客户端channel中的字节流
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(defaultEventExecutorGroup,new NettyDecoder());
                    }
                });

        try{
            serverBootstrap.bind().addListener(new ChannelFutureListener() {
                public void operationComplete(ChannelFuture future) throws Exception {
                    Throwable casue = future.cause();
                    if(casue != null){
                        System.out.println("启动服务器失败，"+casue.getMessage());
                    }else{
                        System.out.println("启动服务器器成功");
                    }
                }
            }).sync();
        }catch (InterruptedException e){
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        NettyRemotingServer server = new NettyRemotingServer();
        server.start();
    }
}
