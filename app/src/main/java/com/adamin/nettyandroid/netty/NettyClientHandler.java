package com.adamin.nettyandroid.netty;

import android.text.TextUtils;
import android.util.Log;

import com.adamin.nettyandroid.netty.bean.SocketBean;
import com.adamin.nettyandroid.service.NettyService;
import com.blankj.utilcode.util.DeviceUtils;
import com.google.gson.GsonBuilder;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class NettyClientHandler<T> extends SimpleChannelInboundHandler<String> {

    private static final String TAG = "NettyClientHandler";
    private final boolean isSendheartBeat;
    private NettyClientListener listener;
    private int index;
    private Object heartBeatData;
    private String packetSeparator;
    private int followId = 1;
    //    private static final ByteBuf HEARTBEAT_SEQUENCE = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Heartbeat"+System.getProperty("line.separator"),
//            CharsetUtil.UTF_8));
    byte[] requestBody = {(byte) 0xFE, (byte) 0xED, (byte) 0xFE, 5, 4, (byte) 0xFF, 0x0a};


    public NettyClientHandler(NettyClientListener listener, int index, boolean isSendheartBeat, Object heartBeatData) {
        this(listener, index, isSendheartBeat, heartBeatData, null);
    }

    public NettyClientHandler(NettyClientListener listener, int index, boolean isSendheartBeat, Object heartBeatData, String separator) {
        this.listener = listener;
        this.index = index;
        this.isSendheartBeat = isSendheartBeat;
        this.heartBeatData = heartBeatData;
        this.packetSeparator = TextUtils.isEmpty(separator) ? System.getProperty("line.separator") : separator;
    }

    /**
     * <p>设定IdleStateHandler心跳检测每x秒进行一次读检测，
     * 如果x秒内ChannelRead()方法未被调用则触发一次userEventTrigger()方法 </p>
     *
     * @param ctx ChannelHandlerContext
     * @param evt IdleStateEvent
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.WRITER_IDLE) {   //发送心跳
                Log.e("写空闲：","发送心跳"+event.state());
//                ctx.channel().writeAndFlush("Heartbeat" + System.getProperty("line.separator"));
                if (isSendheartBeat) {
                    if (heartBeatData == null) {
                        SocketBean socketBean=new SocketBean();
                        socketBean.setMessagge("ping");
                        socketBean.setData("heartBeat");
                        socketBean.setSerialNumber(1L);
                        socketBean.setSn(DeviceUtils.getUniqueDeviceId(true));
                        socketBean.setCmdType(CmdType.TYPE_HEART_BEAT);
                        ctx.channel().writeAndFlush(new GsonBuilder().disableHtmlEscaping().create().toJson(socketBean) + packetSeparator);
                    } else {
                        if (heartBeatData instanceof String) {
                             Log.e("心跳内容：",new GsonBuilder().disableHtmlEscaping().create().toJson(heartBeatData));
                            ctx.channel().writeAndFlush(new GsonBuilder().disableHtmlEscaping().create().toJson(heartBeatData) + packetSeparator);
                        } else if (heartBeatData instanceof byte[]) {
//                            Log.d(TAG, "userEventTriggered: byte");
                            ByteBuf buf = Unpooled.copiedBuffer((byte[]) heartBeatData);
                            ctx.channel().writeAndFlush(buf);
                        } else {
                            Log.e(TAG, "userEventTriggered: heartBeatData type error");
                        }
                    }
                } else {
                    Log.e(TAG, "不发送心跳");
                }
            }
        }
    }

    /**
     * <p>客户端上线</p>
     *
     * @param ctx ChannelHandlerContext
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        Log.e(TAG, "channelActive");
        listener.onClientStatusConnectChanged(ConnectState.STATUS_CONNECT_SUCCESS, index);
        NettyService.getInstance().setState(true);
    }

    /**
     * <p>客户端下线</p>
     *
     * @param ctx ChannelHandlerContext
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        Log.e(TAG, "channelInactive");
        listener.onClientStatusConnectChanged(ConnectState.STATUS_CONNECT_CLOSED,index);
        NettyService.getInstance().setState(false);

    }

    /**
     * 客户端收到消息
     *
     * @param channelHandlerContext ChannelHandlerContext
     * @param msg                   消息
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String msg) {
         Log.e(TAG, "channelRead0:"+msg);
         listener.onMessageResponseClient(msg,index);

    }

    /**
     * @param ctx   ChannelHandlerContext
     * @param cause 异常
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {

        Log.e(TAG, "exceptionCaught:" + cause.getMessage());
        listener.onClientStatusConnectChanged(ConnectState.STATUS_CONNECT_ERROR, index);
        cause.printStackTrace();
        ctx.close();

    }

}
