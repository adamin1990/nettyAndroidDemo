package com.adamin.nettyandroid.netty.bean; /**
 * Copyright (C), 2020-2021
 * FileName: SocketBean
 * Date:     2021/3/16 12:30
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 *
 * @author adamin
 * @site https://www.lixiaopeng.top
 * @create 2021/3/16 12:30
 */
public class SocketBean implements Serializable {
    @SerializedName("message")
    private String messagge;
    @SerializedName("serialNumber")
    private long serialNumber;
    @SerializedName("data")
    private Object data; //数据
    @SerializedName("sn")
    private String sn;  //设备唯一id，客户端生成
    @SerializedName("cmdType")
    private int cmdType;

    public String getMessagge() {
        return messagge;
    }

    public void setMessagge(String messagge) {
        this.messagge = messagge;
    }

    public long getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(long serialNumber) {
        this.serialNumber = serialNumber;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public int getCmdType() {
        return cmdType;
    }

    public void setCmdType(int cmdType) {
        this.cmdType = cmdType;
    }

    @Override
    public String toString() {
        return "SocketBean{" +
                "messagge='" + messagge + '\'' +
                ", serialNumber=" + serialNumber +
                ", data=" + data +
                ", sn='" + sn + '\'' +
                ", cmdType=" + cmdType +
                '}';
    }
}
