package com.example.administrator.myapplication;

import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class UdpClientActivity extends AppCompatActivity {
    public static final int RECV_MESSAGE_TYPE = 0x001;
    public static final int SEND_MESSAGE_TYPE = 0x002;
    public static final int LOCAL_PORT = 8600;      //本地的端口，接受和发送信息都使用此端口

    private EditText editText_sendMessage;
    private Button button_sendMessage;

    //显示对话内容视图
    private RecyclerView recyView_conversation;
    private LinearAdapter linearApt;

    private String  serverIP;
    private int serverPort;
    private TextView text_address;

    private UdpSocket clientSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_udp_client);

        //创建自定义UdpSocket类,实现接受，发送信息两个子线程
        clientSocket = new UdpSocket(this, LOCAL_PORT);
//        clientSocket.startRecv();   //设置可接受消息

        setTalkUI();
    }

    private void setTalkUI(){
        editText_sendMessage = (EditText) findViewById(R.id.editText_userSaying);
        text_address = (TextView) findViewById(R.id.textView);
        //对话信息列表
        recyView_conversation = (RecyclerView) findViewById(R.id.recyView);
        recyView_conversation.setLayoutManager(new LinearLayoutManager(UdpClientActivity.this));
        recyView_conversation.addItemDecoration(new MyDecoration());
        linearApt = new LinearAdapter(UdpClientActivity.this, new LinearAdapter.OnItemClickListener(){
            @Override
            public void onClick(int pos) {
                Toast.makeText(UdpClientActivity.this, "click " + pos, Toast.LENGTH_SHORT).show();
            }
        });
        recyView_conversation.setAdapter(linearApt);

        //获取配置服务器的IP与PORT
        Bundle bundle = getIntent().getExtras();
        serverIP = bundle.getString("serverIP");
        serverPort = bundle.getInt("Port");
        text_address.setText(String.format("Server(IP: %s,Port: %d)\nClient(IP: %s,Port: %d)",
                    serverIP,serverPort,getLocalIP(),LOCAL_PORT));

        button_sendMessage = (Button) findViewById(R.id.button_sendMessage);
        button_sendMessage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //创建线程发送消息
                String saying = editText_sendMessage.getText().toString();
                if(saying != null && saying.length() != 0){
                    clientSocket.send(saying,
                            serverIP,serverPort);
                }
                else {
                    Toast.makeText(getApplicationContext(),"您什么都没说",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // handler用于Activity之间传递消息
    private final Handler handler= new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            switch (msg.what){
                case SEND_MESSAGE_TYPE:     //发送消息，更新UI
                    byte[] sayingBuf =(byte[])msg.obj;
                    String saying=new String(sayingBuf);
                    linearApt.append("ME: " + saying);

                    Toast.makeText(getApplicationContext(),"发送成功",Toast.LENGTH_SHORT).show();
                    break;

                case RECV_MESSAGE_TYPE:     //接受消息，更新UI
                    byte[] readBuf =(byte[])msg.obj;
                    String resMessage=new String(readBuf,msg.arg1,msg.arg2);
                    linearApt.append("Bot: "+resMessage);
                    break;
            }
        }
    };

    public Handler getHandler(){
        return handler;
    }

//  自制下划线
    private class MyDecoration extends RecyclerView.ItemDecoration{
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state){
            super.getItemOffsets(outRect, view, parent, state);
            outRect.set(0,0,0,getResources().getDimensionPixelOffset(R.dimen.dividerHeight));
        }
    }

    public static String getLocalIP() {
        try {
            for (Enumeration<NetworkInterface> enNetI = NetworkInterface
                    .getNetworkInterfaces(); enNetI.hasMoreElements(); ) {
                NetworkInterface netI = enNetI.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = netI
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (inetAddress instanceof Inet4Address && !inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "";
    }
}
