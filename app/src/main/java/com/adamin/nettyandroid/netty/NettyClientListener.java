package com.adamin.nettyandroid.netty;

/*
    ___        __                   _        ____   ____ 
   /   |  ____/ /____ _ ____ ___   (_)____  / __ \ / __ \
  / /| | / __  // __ `// __ `__ \ / // __ \/ /_/ // / / /
 / ___ |/ /_/ // /_/ // / / / / // // / / /\__, // /_/ / 
/_/  |_|\__,_/ \__,_//_/ /_/ /_//_//_/ /_//____/ \____/  
                                                         
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
                 做一款产品       愉悦自己      		   
****************Powered by Adamin90********************
* @email: 14846869@qq.com
* Date: 2020/3/20
* Time: 11:06
* Desc: TCP状态监听
* @link: https://www.lixiaopeng.top
*******************************************************
*/
public interface NettyClientListener<T> {

        /**
         * 当接收到系统消息
         * @param msg 消息
         * @param index tcp 客户端的标识，因为一个应用程序可能有很多个长链接
         */
        void onMessageResponseClient(T msg, int index);

/**
 * 当服务状态发生变化时触发
 * @param statusCode 状态变化
 * @param index tcp 客户端的标识，因为一个应用程序可能有很多个长链接
 */
public void onClientStatusConnectChanged(int statusCode, int index);
        }
