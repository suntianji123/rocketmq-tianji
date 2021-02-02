package com.tianji.rocketmq.remoting;

import com.alibaba.fastjson.JSON;

import java.nio.charset.Charset;

/**
 * 远程序列化方式
 */
public abstract class RemotingSerializable {

    /**
     * UTF-8字符集
     */
    private static final Charset CHARSET_UTF8 = Charset.forName("UTF-8");

    /**
     * 解码
     * @param data 字节数组
     * @param clazz Class类型
     * @param <T> 对象类型
     * @return
     */
    public static <T> T decode(byte[] data,Class<T> clazz){
        String json = new String(data,CHARSET_UTF8);
        return fromJson(json,clazz);
    }

    /**
     * 将对象编码为字节数组
     * @param object 对象
     * @return
     */
    public static byte[] encode(Object object){
        String json = toJson(object,false);
        if(json != null){
            return json.getBytes(CHARSET_UTF8);
        }
        return null;
    }

    private static String toJson(final Object object,boolean prettyFormat){
        return JSON.toJSONString(object,prettyFormat);
    }

    /**
     * 将字符串
     * @param json
     * @param clazz
     * @param <T>
     * @return
     */
    private static <T> T fromJson(String json,Class<T> clazz){
        return JSON.parseObject(json,clazz);
    }


}
