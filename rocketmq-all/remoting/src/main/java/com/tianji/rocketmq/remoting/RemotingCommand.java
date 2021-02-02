package com.tianji.rocketmq.remoting;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class RemotingCommand {

    /**
     * 序列化类型
     */
    private static SerializeType serializeTypeConfigInThisServer = SerializeType.JSON;

    private int type;

    private transient byte[] body;

    private SerializeType serializeTypeCurrentRPC = serializeTypeConfigInThisServer;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public ByteBuffer encodeHeader(){
        return encodeHeader(body != null?body.length:0);
    }

    public ByteBuffer encodeHeader(int bodyLength){
        int length = 4;
        byte[] headerData = headerData();
        length +=  headerData.length;
        length += bodyLength;
        ByteBuffer byteBuffer = ByteBuffer.allocate(4 + length - bodyLength);
        byteBuffer.putInt(length);
        byteBuffer.put(markProtocolType(headerData.length,serializeTypeCurrentRPC));
        byteBuffer.put(headerData);
        byteBuffer.flip();
        return byteBuffer;
    }

    public byte[] headerData(){
        if(serializeTypeCurrentRPC == SerializeType.ROCKETMQ){

        }else if(serializeTypeCurrentRPC == SerializeType.JSON){
            return RemotingSerializable.encode(this);
        }
        return null;
    }

    @Override
    public String toString() {
        return "type:"+type+",body:"+ Arrays.toString(body);
    }

    public static RemotingCommand decode(ByteBuffer byteBuffer){
        //获取消息的总长度
        int length = byteBuffer.limit();
        int originalHeaderLength = byteBuffer.getInt();
        int headerLength = getHeaderLength(originalHeaderLength);
        byte[] headerData = new byte[headerLength];
        byteBuffer.get(headerData);
        RemotingCommand cmd = headerDecode(headerData,getSerializeType(originalHeaderLength));

        int bodyLength = length - 4 - headerLength;
        if(bodyLength > 0){
            byte[] body = new byte[bodyLength];
            byteBuffer.get(body);
            cmd.body = body;
        }

        return cmd;
    }

    /**
     * 解码消息头
     * @param headerData 消息头
     * @return
     */
    private static RemotingCommand headerDecode(byte[] headerData,SerializeType serializeType){
        switch (serializeType){
            case JSON:
                RemotingCommand cmd = RemotingSerializable.decode(headerData,RemotingCommand.class);
                cmd.serializeTypeCurrentRPC = serializeType;
                return cmd;
            case ROCKETMQ:
                break;
                default:
                    System.out.println("error");
                    break;
        }
        return JSON.parseObject(headerData,RemotingCommand.class);
    }

    /**
     * 将序列化方式写入 返回字节数组
     * @param headerLength 消息头长度
     * @param serializeTypeCurrentRPC 序列化类型
     * @return
     */
    private static byte[] markProtocolType(int headerLength,SerializeType serializeTypeCurrentRPC){
        byte[] result = new byte[4];
        result[0] = serializeTypeCurrentRPC.getCode();
        result[1] = (byte)((headerLength >> 16) & 0xFF);
        result[2] = (byte)((headerLength >> 8) & 0xFF);
        result[3] = (byte)(headerLength & 0xFF);
        return result;
    }

    private static int getHeaderLength(int originalHeaderLength){
        return originalHeaderLength & 0xFFFFFF;
    }

    private static SerializeType getSerializeType(int headerLength){
        return SerializeType.valueOf((byte)((headerLength >> 24)&0xFF));
    }
}
