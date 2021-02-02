package com.tianji.rocketmq.remoting;

/**
 * 序列化类型
 */
public enum  SerializeType {
    JSON((byte)0),
    ROCKETMQ((byte)1);

    private byte code;

    SerializeType(byte code){
        this.code = code;
    }

    public byte getCode(){
        return code;
    }

    /**
     * 获取序列化类型
     * @param code
     * @return
     */
    public static SerializeType valueOf(byte code){
        for(SerializeType serializeType : SerializeType.values()){
            if(serializeType.code == code){
                return serializeType;
            }
        }
        return null;
    }
}
