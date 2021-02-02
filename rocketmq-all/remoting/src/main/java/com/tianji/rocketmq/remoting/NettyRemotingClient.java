package com.tianji.rocketmq.remoting;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.net.InetSocketAddress;

public class NettyRemotingClient {

    /**
     * 客户端启动配置
     */
    private final Bootstrap bootstrap = new Bootstrap();

    /**
     * NioSocketChannel的轮训器的工作线程组
     */
    private final EventLoopGroup eventLoopGroupWorker;

    /**
     * 处理NioSocketChannel io事件的执行器组
     */
    private final DefaultEventExecutorGroup defaultEventExecutorGroup;

    public NettyRemotingClient(){
        eventLoopGroupWorker = new NioEventLoopGroup(1);
        defaultEventExecutorGroup = new DefaultEventExecutorGroup(8);
    }

    public void start(){
        bootstrap.group(eventLoopGroupWorker)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY,true)
                .option(ChannelOption.SO_KEEPALIVE,false)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,3000)
                .option(ChannelOption.SO_SNDBUF,65535)
                .option(ChannelOption.SO_RCVBUF,65535)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(defaultEventExecutorGroup,new NettyEncoder());
                    }
                });
    }

    public static void main(String[] args) throws InterruptedException {
        NettyRemotingClient client = new NettyRemotingClient();
        client.start();
        ChannelFuture future = client.bootstrap.connect(new InetSocketAddress("127.0.01",8888))
                .addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) throws Exception {
                Throwable cause = future.cause();
                if(cause != null){
                    System.out.println("连接服务器失败，"+cause.getMessage());
                }else{
                    System.out.println("连接服务器成功");

                    RemotingCommand cmd = new RemotingCommand();
                    cmd.setType(1);
                    cmd.setBody(new byte[]{0x00,0x01,0x02,0x03});
                    future.channel().writeAndFlush(cmd);
                }
            }
        });
    }
}
