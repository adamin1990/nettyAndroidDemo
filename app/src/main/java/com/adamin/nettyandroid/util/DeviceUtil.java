package com.adamin.nettyandroid.util;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;


import com.blankj.utilcode.util.LogUtils;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.UUID;


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
* Date: 2020/3/19
* Time: 10:42
* Desc: 
* @link: https://www.lixiaopeng.top
*******************************************************
*/
public class DeviceUtil {

    public static int flowId = 0;


    public static int getFlowId() {
        flowId = (flowId + 1) & 9999;
        return flowId;
    }

    public static String getDeviceIdCommon() {
//        return "005C201D93AF";
        String onlyCode2 = getMac();
        LogUtils.e("设备id"+onlyCode2);
        if (onlyCode2 == null || onlyCode2.length() < 3) {
            String id=getDeviceUUID();

            return id;
        }
        String onlyCode3 = onlyCode2.replace(":", "");
        if(TextUtils.isEmpty(onlyCode3)){
            onlyCode3=getDeviceUUID();

        }
        return onlyCode3;
    }


    /**
     * 获得设备硬件uuid
     * 使用硬件信息，计算出一个随机数
     *
     * @return 设备硬件uuid
     */
    private static String getDeviceUUID() {
        try {
            String dev = "3883756" +
                    Build.BOARD.length() % 10 +
                    Build.BRAND.length() % 10 +
                    Build.DEVICE.length() % 10 +
                    Build.HARDWARE.length() % 10 +
                    Build.ID.length() % 10 +
                    Build.MODEL.length() % 10 +
                    Build.PRODUCT.length() % 10 +
                    Build.SERIAL.length() % 10;
            return new UUID(dev.hashCode(),
                    Build.SERIAL.hashCode()).toString();
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }

    public static String getEthMAC() {
        String macSerial = null;
        String str = "";
        try {
            LineNumberReader input = new LineNumberReader(new InputStreamReader(Runtime.getRuntime().exec("cat /sys/class/net/eth0/address").getInputStream()));
            while (true) {
                if (str == null) {
                    break;
                }
                str = input.readLine();
                if (str != null) {
                    macSerial = str.trim();
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        String str2 = "";
        return (macSerial == null || macSerial.equals(str2) || macSerial.length() != 17) ? str2 : macSerial.toUpperCase();
    }


    /**
     * 获取手机的MAC地址
     *
     * @return
     */
    public static String getMac() {
        String str = "";
        String macSerial = "";
        macSerial = getEthMAC();
        if (!TextUtils.isEmpty(macSerial)) {
            return macSerial;
        }
        try {
            Process pp = Runtime.getRuntime().exec(
                    "cat /sys/class/net/wlan0/address");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);

            for (; null != str; ) {
                str = input.readLine();
                if (str != null) {
                    macSerial = str.trim();// 去空格
                    break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return macSerial;
    }

    public static String loadFileAsString(String fileName) throws Exception {
        FileReader reader = new FileReader(fileName);
        String text = loadReaderAsString(reader);
        reader.close();
        return text;
    }

    public static String loadReaderAsString(Reader reader) throws Exception {
        StringBuilder builder = new StringBuilder();
        char[] buffer = new char[4096];
        int readLength = reader.read(buffer);
        while (readLength >= 0) {
            builder.append(buffer, 0, readLength);
            readLength = reader.read(buffer);
        }
        return builder.toString();
    }



    public static void reboot() throws IOException {
        Runtime.getRuntime().exec("su -c reboot");
    }


    /**
     * 获取wifi的mac地址，适配到android Q
     * @param paramContext
     * @return
     */
    public static String getMac(Context paramContext) {
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                String str = getMacMoreThanM(paramContext);
                if (!TextUtils.isEmpty(str))
                    return str;
            }
            // 6.0以下手机直接获取wifi的mac地址即可
            WifiManager wifiManager = (WifiManager)paramContext.getSystemService("wifi");
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (wifiInfo != null)
                return wifiInfo.getMacAddress();
        } catch (Throwable throwable) {}
        return null;
    }
    /**
     * android 6.0+获取wifi的mac地址
     * @param paramContext
     * @return
     */
    private static String getMacMoreThanM(Context paramContext) {
        try {
            //获取本机器所有的网络接口
            Enumeration enumeration = NetworkInterface.getNetworkInterfaces();
            while (enumeration.hasMoreElements()) {
                NetworkInterface networkInterface = (NetworkInterface)enumeration.nextElement();
                //获取硬件地址，一般是MAC
                byte[] arrayOfByte = networkInterface.getHardwareAddress();
                if (arrayOfByte == null || arrayOfByte.length == 0) {
                    continue;
                }

                StringBuilder stringBuilder = new StringBuilder();
                for (byte b : arrayOfByte) {
                    //格式化为：两位十六进制加冒号的格式，若是不足两位，补0
                    stringBuilder.append(String.format("%02X:", new Object[] { Byte.valueOf(b) }));
                }
                if (stringBuilder.length() > 0) {
                    //删除后面多余的冒号
                    stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                }
                String str = stringBuilder.toString();
                // wlan0:无线网卡 eth0：以太网卡
                if (networkInterface.getName().equals("wlan0")) {
                    return str;
                }
            }
        } catch (SocketException socketException) {
            return null;
        }
        return null;
    }

}
