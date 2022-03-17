package com.adamin.nettyandroid.service;

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.adamin.nettyandroid.netty.CmdType;
import com.adamin.nettyandroid.netty.ConnectState;
import com.adamin.nettyandroid.netty.NettyClient;
import com.adamin.nettyandroid.netty.NettyClientListener;
import com.adamin.nettyandroid.netty.bean.SocketBean;
import com.adamin.nettyandroid.util.DeviceUtil;
import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.LogUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class NettyService extends Service implements NettyClientListener<String> {
    private static NettyService mService;
    private NettyClient nettyClient;
    private  NettyTimer nettyTimer;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mService=this;
        if(nettyTimer==null){
            nettyTimer=new NettyTimer(1000*20,1000);
            nettyTimer.start();
        }
        initTcp();
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
    public void onDestroy() {
        super.onDestroy();
        if(nettyTimer!=null){
            nettyTimer.cancel();
        }
    }

    @Override
    public void onMessageResponseClient(String msg, int index) {
        Gson gson=new Gson();
        SocketBean socketBean = gson.fromJson(msg, new TypeToken<SocketBean>() {
        }.getType());

        Log.e("接受消息",msg+"-------"+socketBean.getCmdType());
        sendSocketMsg(socketBean);

    }

    @Override
    public void onClientStatusConnectChanged(int statusCode, int index) {
        Log.e("状态改变",statusCode+"");
       if(statusCode== ConnectState.STATUS_CONNECT_SUCCESS){  //连接成功,鉴权
           deviceAuth();



       }
    }

    private void deviceAuth() {
        SocketBean socketBean=new SocketBean();
        socketBean.setCmdType(CmdType.TYPE_AUTH);
        socketBean.setSn(DeviceUtils.getUniqueDeviceId(true));
        socketBean.setMessagge("auth");
        socketBean.setData("adamin");
        socketBean.setSerialNumber(DeviceUtil.getFlowId());
        sendSocketMsg(socketBean);


    }


    /**
     * 初始化tcp并连接
     */
    public void initTcp() {


        if (nettyClient != null) {
            return;
        }

        SocketBean socketBean=new SocketBean();
        socketBean.setMessagge("ping");
        socketBean.setData("heartBeat");
        socketBean.setSerialNumber(1L);
        socketBean.setSn(DeviceUtils.getUniqueDeviceId(true));
        socketBean.setCmdType(CmdType.TYPE_HEART_BEAT);
        nettyClient = new NettyClient.Builder()
                .setHost("192.168.3.7")    //设置服务端地址
                .setTcpPort(1024) //设置服务端端口号
                .setMaxReconnectTimes(Integer.MAX_VALUE)    //设置最大重连次数
                .setReconnectIntervalTime(5000)    //设置重连间隔时间。单位：毫秒
                .setSendheartBeat(true) //设置是否发送心跳
                .setHeartBeatInterval(5)    //设置心跳间隔时间。单位：秒
                .setHeartBeatData(new GsonBuilder().disableHtmlEscaping().create().toJson(socketBean)) //设置心跳数据，可以是String类型，也可以是byte[]，以后设置的为准
                .setIndex(0)    //设置客户端标识.(因为可能存在多个tcp连接)
                .setPacketSeparator("$$")//用特殊字符，作为分隔符，解决粘包问题，默认是用换行符作为分隔符
                .setMaxPacketLong(4096 * 100)//设置一次发送数据的最大长度，默认是1024
                .build();
        nettyClient.setListener(this);
        nettyClient.connect();//连接服务器
    }

    /**
     * 向服务端发送数据
     * @param data
     */
    public void sendSocketMsg(SocketBean data) {

        if (nettyClient != null && nettyClient.getConnectStatus()) {
            nettyClient.sendMsgToServer(""+new Gson().toJson(data));
        } else {

        }

    }

    /**
     * 连接netty服务
     */
    public void connect(){
        if(nettyClient==null){
            initTcp();
            return;
        }
        nettyClient.connect();
    }

    public void setState(boolean state){
        LogUtils.e("----set connectt status---"+state+"---"+nettyClient);

        synchronized (NettyService.this){
            LogUtils.e("----set connect status---"+state);
            if(nettyClient!=null){
                nettyClient.setConnectStatus(state);
            }else{
                LogUtils.e("----netty is null---"+nettyClient);
            }

        }
    }

    /**
     * 定时检测长链接
     */
    class NettyTimer extends CountDownTimer{

        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public NettyTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            if(nettyTimer!=null){
                nettyTimer.start();
            }
            LogUtils.e("------timer finish----"+nettyClient);
            if(nettyClient!=null){
                //未连接去连接
                if(!nettyClient.getConnectStatus()&&!nettyClient.isConnecting()){
                 connect();

                }
            }

        }
    }

}
