package com.action.screenmirror.model;

import java.util.ArrayList;
import java.util.List;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import android.media.projection.MediaProjection;
import android.util.Log;

import com.action.screenmirror.interf.IOnEncodeData;
import com.action.screenmirror.utils.ByteUtils;
import com.action.screenmirror.utils.Config;
import com.action.screenmirror.bean.DeviceInfo;

public class UdpSocketSend {
    protected static final String TAG = "UdpSocketSend";
    private MediaEncoder mediaEncoder;
    private DatagramSocket mDatagramSocket;
    private String mIp = "";
    private List<DeviceInfo> mDevListInfo;
    private boolean mSendAll = false;

    public void startReceord(MediaProjection mediaProjection) {
        if (null == mediaEncoder) {
            mediaEncoder = new MediaEncoder();
            mediaEncoder.startForUdp(mediaProjection, new IOnEncodeData() {
                @Override
                public void OnData(byte[] buf) {
                    //Log.i(TAG, "hdb--------buf:"+buf.length);
                    sendData(buf);
                }
            });
            
            if (mDatagramSocket == null) {
                try {
                    mDatagramSocket = new DatagramSocket(Config.PortGlob.DATAPORT);
                } catch (SocketException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private int packageCount = 0;
    private synchronized void sendData(byte[] buf){
        packageCount ++;
        if (packageCount > 50000) {
            packageCount = 1;
        }
        try {
            
            int length = buf.length;
            int count = (length / 1000);
            int offsize = length % 1000;
            if (offsize > 0) {
                count ++ ;
            }
            //Log.i(TAG, "hdb------length:"+length+"  count:"+count+"  offsize:"+offsize);
            byte[] data = null;
            int dataLenth = 0;
            for (int i = 0; i < count; i++) {
                if (i == count - 1) {
                    dataLenth = length - (i * 1000);
                    data = new byte[dataLenth + 11];
                    
                }else {
                    dataLenth = 1000;
                    data = new byte[1000 + 11];
                }

                byte[] len = ByteUtils.intToBuffer(dataLenth);
                byte[] pLen = ByteUtils.intToBuffer(length);
                byte[] pCount = ByteUtils.intToBuffer(packageCount);
                byte index = (byte) (i & 0xFF);
                //Log.i(TAG, "hdb------i:"+i+"  packageCount:"+packageCount+"  dataLenth:"+dataLenth);

                data[0] = index;         //拆分包位置
                data[1] = (byte) (count & 0xFF);    //拆分包个数
                System.arraycopy(pCount, 0, data, 2, pCount.length);//整包序号
                System.arraycopy(pLen, 0, data, (2 + pCount.length), pLen.length);//整包长度
                System.arraycopy(len, 0, data, (2 + pCount.length + pLen.length), len.length);//拆分包长度
                System.arraycopy(buf, i*1000, data, (2 + pCount.length + pLen.length + len.length), dataLenth);//拆分包内容

                if (mSendAll && mDevListInfo.size() > 0) {
                    //Send to all clients
                    for (DeviceInfo deviceInfo : mDevListInfo) {
                        String remoteAddress = deviceInfo.getValidAddress();
                        DatagramPacket datagramPacket = new DatagramPacket(data, data.length, InetAddress.getByName(remoteAddress),
                                                                Config.PortGlob.DATAPORT);
                        mDatagramSocket.send(datagramPacket);
                    }
                } else {
                    //Send to the selected client
                    DatagramPacket datagramPacket = new DatagramPacket(data, data.length, InetAddress.getByName(mIp),
                                                            Config.PortGlob.DATAPORT);
                    mDatagramSocket.send(datagramPacket);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setIpAddr(String ip){
        mIp = ip;
    }

    public void setDevListInfo(List<DeviceInfo> devListInfo, boolean sendALL){
        mDevListInfo = devListInfo;
        mSendAll = sendALL;
    }

    public void stopEncode(){
        if (mediaEncoder != null) {
            mediaEncoder.stopScreen();
            mediaEncoder = null;
        }
    }

    public boolean hasStart(){
        if (mediaEncoder != null) {
            return mediaEncoder.hasStart();
        }
        return false;
        
    }
    
}
