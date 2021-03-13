package com.adamin.nettyandroid.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.adamin.nettyandroid.netty.NettyClient;
import com.adamin.nettyandroid.netty.NettyClientListener;

public class NettyService extends Service implements NettyClientListener<String> {
    private static NettyService mService;
    private NettyClient nettyClient;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    public static NettyService getInstance() {
        if (mService == null) {
            synchronized (NettyService.class) {
                if (mService == null) {
                    mService = new NettyService();
                }
            }
        }
        return mService;
    }

    @Override
    public void onMessageResponseClient(String msg, int index) {
        Log.e("接受消息",msg);

    }

    @Override
    public void onClientStatusConnectChanged(int statusCode, int index) {
        Log.e("状态改变",statusCode+"");
    }


    /**
     * 初始化tcp并连接
     */
    public void initTcp() {


        if (nettyClient != null) {
            return;
        }


        nettyClient = new NettyClient.Builder()
                .setHost("192.168.3.7")    //设置服务端地址
                .setTcpPort(1024) //设置服务端端口号
                .setMaxReconnectTimes(Integer.MAX_VALUE)    //设置最大重连次数
                .setReconnectIntervalTime(5000)    //设置重连间隔时间。单位：毫秒
                .setSendheartBeat(true) //设置是否发送心跳
                .setHeartBeatInterval(5000)    //设置心跳间隔时间。单位：秒
                .setHeartBeatData("heartBeat$$") //设置心跳数据，可以是String类型，也可以是byte[]，以后设置的为准
                .setIndex(0)    //设置客户端标识.(因为可能存在多个tcp连接)
                .setPacketSeparator("$$")//用特殊字符，作为分隔符，解决粘包问题，默认是用换行符作为分隔符
                .setMaxPacketLong(4096 * 100)//设置一次发送数据的最大长度，默认是1024
                .build();
        nettyClient.setListener(this);
        nettyClient.connect();//连接服务器
    }
}