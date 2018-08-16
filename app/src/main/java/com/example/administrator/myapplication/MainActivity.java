package com.example.administrator.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    public static final String DEFAULT_SERVER_IP = "127.0.0.1";
    public static final int DEFAULT_SERVER_PORT = 8888;

    private EditText editText_serverIP;
    private EditText editText_serverPort;

    private Button button_setAddr;
    private Button button_setDefault;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("Life_Circle","Create");
        setServerAddrUI();
    }

    private void setServerAddrUI(){
        editText_serverIP = (EditText) findViewById(R.id.editText_severIP);
        editText_serverPort = (EditText) findViewById(R.id.editText_serverPort);

        OnClick onClick = new OnClick();
        button_setAddr = (Button) findViewById(R.id.button_setAddr);
        button_setAddr.setOnClickListener(onClick);
        button_setDefault = (Button) findViewById(R.id.button_setDefault);
        button_setDefault.setOnClickListener(onClick);
    }

    class OnClick implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            //更多的button点击操作在这里添加
            switch (v.getId()){
                case R.id.button_setAddr:
                    Intent intent1 = new Intent(MainActivity.this,UdpClientActivity.class);
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("serverIP",editText_serverIP.getText().toString());
                    bundle1.putInt("Port",Integer.parseInt(editText_serverPort.getText().toString()));
                    intent1.putExtras(bundle1);
                    startActivity(intent1);
                    break;
                case R.id.button_setDefault:
                    Intent intent2 = new Intent(MainActivity.this,UdpClientActivity.class);
                    Bundle bundle2 = new Bundle();
                    bundle2.putString("serverIP",DEFAULT_SERVER_IP);
                    bundle2.putInt("Port",DEFAULT_SERVER_PORT);
                    intent2.putExtras(bundle2);
                    startActivity(intent2);
                    break;
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("Life_Circle","Start");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Life_Circle","Resume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("Life_Circle","Pause");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("Life_Circle","Restart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("Life_Circle","Stop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("Life_Circle","Destory");
    }
}
