/**
 * Copyright (C), 2020-2021
 * FileName: CmdType
 * Date:     2021/3/16 12:24
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.adamin.nettyandroid.netty;


/**
 *指令类型
 * @author adamin
 * @site https://www.lixiaopeng.top
 * @create 2021/3/16 12:24
 */
public class CmdType {
    public static final int  TYPE_HEART_BEAT=100; //客户端心跳上传
    public static final int TYPE_AUTH=101; //客户端注册鉴权指令
    public static final int   TYPE_HOOK=500; //下发指令，hook数据
    public static final int TYPE_PROXY=501; //下发指令，开启代理
}
