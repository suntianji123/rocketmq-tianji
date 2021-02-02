package com.tianji.rocketmq.remoting;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.nio.ByteBuffer;
import java.util.List;

public class NettyEncoder extends MessageToByteEncoder<RemotingCommand> {

    protected void encode(ChannelHandlerContext ctx, RemotingCommand msg, ByteBuf out) throws Exception {
        try{
            ByteBuffer byteBuffer = msg.encodeHeader();
            out.writeBytes(byteBuffer);
            byte[] body = msg.getBody();
            if(body != null){
                out.writeBytes(msg.getBody());
            }
        }catch (Exception e){
            ctx.channel().close();
        }
    }
}
