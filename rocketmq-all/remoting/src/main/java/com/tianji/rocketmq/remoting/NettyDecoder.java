package com.tianji.rocketmq.remoting;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.nio.ByteBuffer;

/**
 * 解码器
 */
public class NettyDecoder extends LengthFieldBasedFrameDecoder {
    public NettyDecoder() {
        super(16777216, 0, 4,0,4);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf frame = null;
        try{
            frame = (ByteBuf) super.decode(ctx, in);
            if(frame == null){
                return null;
            }

            ByteBuffer byteBuffer = frame.nioBuffer();
            RemotingCommand result =  RemotingCommand.decode(byteBuffer);
            System.out.println("接收消息："+result.toString());
            return result;
        }catch (Exception e){
            ctx.channel().close();
        }finally {
            if(frame != null){
                frame.release();
            }
        }
        return null;
    }
}
