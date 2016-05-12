package com.example.apple.monitor;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonToken;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import Tools.PdaLink;
import pojo.ServerTable;
import pojo.User;

public class MainActivity extends Activity {

    Button shexiangtouButton,jiankongButton,fileButton,logoutButton;
    User user;
    Gson gson;
    String request;
    EditText Monitorame;
    TextView StartStatus;
    int tries = 0;
    MyHandler myHandler;
//    MyHandler1 myHandler1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        request = getIntent().getStringExtra("request");
        gson = new Gson();
        user = gson.fromJson(request, User.class);

        shexiangtouButton = (Button) findViewById(R.id.shexiangtouButton);
        jiankongButton = (Button) findViewById(R.id.jiankongButton);
        fileButton = (Button) findViewById(R.id.fileButton);
        logoutButton = (Button) findViewById(R.id.logoutButton);


        myHandler = new MyHandler();

        shexiangtouButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateDialog();
            }
        });

        jiankongButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(){
                    @Override
                    public void run() {
                        String result = PdaLink.getPDAServerData("serverService/getServerByUid/"+user.getUid());
                        if(!result.equals("0")){
                            List<ServerTable> slist = new Gson().fromJson(result,new TypeToken<List<ServerTable>>(){}.getType());
                            CreateShowDialog(slist);
                        }
                        else
                            SendMsg(4);
                    }
                }.start();
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences mySharedPreferences= getSharedPreferences("log",
                        Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = mySharedPreferences.edit();
                editor.putString("request", "");
                editor.commit();
                Intent intent = new Intent(MainActivity.this,LogRegActivity.class);
                startActivity(intent);
            }
        });

        fileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,FileActivity.class);
                startActivity(intent);
            }
        });

    }


    public void CreateShowDialog(final List<ServerTable> slist){
        List<String> nlist = new ArrayList<String>();
        for (ServerTable s: slist) {
            nlist.add(s.getsName());
        }
        String name[] = (String[])nlist.toArray(new String[nlist.size()]);
        getMainLooper().prepare();
        new AlertDialog.Builder(MainActivity.this).setTitle("列表对话框")
                .setItems(name, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(MainActivity.this,WatchActivity.class);
                        Bundle b = new Bundle();
                       b.putSerializable("server",slist.get(which));
                       b.putSerializable("user",user);
                      intent.putExtras(b);
                       startActivity(intent);
                    }

                }).setNegativeButton("取消", null).show();
        getMainLooper().loop();
    }


//*********************摄像按钮***************************

    private void CreateDialog(){
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.create_dialog,
        (ViewGroup) findViewById(R.id.Createdialog));
        Monitorame = (EditText)layout.findViewById(R.id.Monitorame);
        StartStatus = (TextView)layout.findViewById(R.id.StartStatus);
        new AlertDialog.Builder(this).setTitle("创建监控源").setView(layout)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        jiankongButton.setText("正在发送请求....");
                      //  shexiangtouButton.setEnabled(false);
                        /* 1: 向服务器发请求，发送监控名和用户id 建立与服务器的udp连接
                           2：服务器获取ip地址后 比较内ip和外ip
                           3：相应的写入数据库
                           4：打开摄像头
                           5：建立socketserver 等待连入客户端
                         */
                        CameraSocketThread socketThread = new CameraSocketThread(user.getUid()+","+Monitorame.getText().toString());
                        socketThread.start();
                    }
                })
                .setNegativeButton("取消", null).show();
    }

    private class CameraSocketThread extends Thread{
        String stringToSend;
        public CameraSocketThread(String s){
            stringToSend = s;
        }
        @Override
        public void run() {
            try{
                DatagramSocket socket = new DatagramSocket();
                socket.setSoTimeout(3000);
                byte[] bytesToSend = (stringToSend).getBytes();
                byte[] bytesToRead = new byte[10];
                InetAddress serverAddress = InetAddress.getByName(PdaLink.ServerIp);
                DatagramPacket sendPacket = new DatagramPacket(bytesToSend,bytesToSend.length,serverAddress,6000);
                DatagramPacket receivePacket = new DatagramPacket(bytesToRead,10,serverAddress,6000);
                boolean receivedResponse = false;
                do {
                    SendMsg(1);
                    socket.send(sendPacket);
                    SendMsg(2);
                    socket.receive(receivePacket);
                    if (!receivePacket.getAddress().equals(serverAddress)) {
                        Log.e("", "接收到了一个未知来源的包");
                    }
                    receivedResponse = true;
                }while((!receivedResponse)&&(tries > 5));

                if(receivedResponse){
                    SendMsg(3);
                    Intent intent = new Intent(MainActivity.this,CameraActivity.class);
                    intent.putExtra("request",request);
                    intent.putExtra("sid",bytesToRead);
                    startActivity(intent);
                }else{
                    SendMsg(4);
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    shexiangtouButton.setText("第"+tries+"次尝试，正在发送数据.....");
                    break;
                case 2:
                    shexiangtouButton.setText("第"+tries+"次尝试，正在接收数据.....");
                    break;
                case 3:
                    shexiangtouButton.setText("已收到数据");
                    break;
                case 4:
                    shexiangtouButton.setText("网络异常,请重试");
                    break;
            }
        }
    }



    private void SendMsg(int what){
        Message msg = new Message();
        msg.what = what;
        myHandler.sendMessage(msg);
    }


    @Override
    public void onBackPressed() {

    }




}
