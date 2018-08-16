package com.example.administrator.myapplication;

import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import android.os.Handler;

import static android.content.ContentValues.TAG;

/**
 * Created by Administrator on 2018/8/15.
 */

public class UdpSocket {
    private int localPort = 8600;
    private static int BUF_SIZE = 1500;
    //private RecvThread recvThread = null;
//    private UdpClientActivity activityParent = null;
    private Handler handler;


    UdpSocket(Handler handler, int localPort){
        this.localPort = localPort;
        this.handler = handler;
    }

    //接受外部使用发送接口
    public void send(String message, String ip, int port){
        //向服务端发送消息，创建线程
        SendThread thread = new SendThread();
        thread.write(message, ip, port);
        thread.start();
    }

    //发送消息线程类
    private class SendThread extends Thread{
        private DatagramSocket socket;
        private InetAddress serverIP;
        private int serverPort;
        private byte[] outData;

        SendThread(){
            try {
                //设置固定的发送、接受端口
                socket = new DatagramSocket(null);
                socket.setReuseAddress(true);
                socket.bind(new InetSocketAddress(localPort));
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run(){
            DatagramPacket packet = new DatagramPacket(outData,outData.length,
                    serverIP,serverPort);
            try {
                socket.send(packet);

                ////将消息发送给handler以更新界面
                Message message = handler.obtainMessage();
                message.what = UdpClientActivity.SEND_MESSAGE_TYPE;
                message.obj = packet.getData();
                handler.sendMessage(message);
                Log.d(TAG, "run: deubg send");

                //接收回复
                byte[] data = new byte[BUF_SIZE];
                DatagramPacket packetRecv = new DatagramPacket(data, data.length);

//              后期while 加入处理接收多个回包
                socket.receive(packetRecv);
                //更新界面
                Message messageRecv = handler.obtainMessage();
                messageRecv.what = UdpClientActivity.RECV_MESSAGE_TYPE;
                messageRecv.obj = packetRecv.getData();
                messageRecv.arg1 = packetRecv.getOffset();
                messageRecv.arg2 = packetRecv.getLength();
                handler.sendMessage(messageRecv);
                Log.d(TAG, "run: deubg recive");


            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void write(String data, String ip, int port){
            try {
                //把数据，ip，端口写入发送线程中
                serverIP = InetAddress.getByName(ip);
                serverPort = port;
                outData = data.getBytes();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
    }
    /*接受消息线程
    //外部开启接受任务接口
    public void startRecv(){
        recvThread = new RecvThread();
        recvThread.start();
        Log.d(TAG, "New RecvThread start");
    }

    private class RecvThread extends Thread{
        private DatagramSocket socket;
        private boolean stopFlag = false;
        RecvThread(){
            try {
                //设置固定的发送、接受端口
                socket = new DatagramSocket(null);
                socket.setReuseAddress(true);
                socket.bind(new InetSocketAddress(localPort));
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run(){
            while (!stopFlag){
                byte[] data = new byte[BUF_SIZE];
                DatagramPacket packet = new DatagramPacket(data, data.length);
                try {
                    socket.receive(packet);
                    //更新界面
                    Message message = handler.obtainMessage();
                    message.what = UdpClientActivity.RECV_MESSAGE_TYPE;
                    message.obj = packet.getData();
                    message.arg1 = packet.getOffset();
                    message.arg2 = packet.getLength();
                    handler.getHandler().sendMessage(message);
                    Log.d(TAG, "run: deubg recive");

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void stopRecv(){
            stopFlag = true;
        }
    }
    */
}
