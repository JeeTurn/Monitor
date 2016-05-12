package Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.apple.monitor.MainActivity;
import com.example.apple.monitor.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import Tools.MD5;
import Tools.PdaLink;


public class ReginFragment extends Fragment {

    private String out;
    private MyHandler handler;
    private MyOnCLickListener myOnCLickListener;
    private Context context;
    private View view;
    private EditText reupwd,uname,upwd;
    private Button submit;


    @SuppressLint("ValidFragment")
    public ReginFragment(Context context){
        this.context = context;
    }
    public ReginFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new MyHandler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.regin,null);
        ini();
        return view;
    }

    private void ini(){

        submit = (Button)view.findViewById(R.id.submit);
        reupwd = (EditText)view. findViewById(R.id.reupwd);
        uname = (EditText)view.findViewById(R.id.uname);
        upwd = (EditText) view.findViewById(R.id.upwd);
        myOnCLickListener = new MyOnCLickListener();
        submit.setOnClickListener(myOnCLickListener);
    }

    private class MyOnCLickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.submit:           //提交按钮事件
                    if(!TextUtils.isEmpty(uname.getText().toString().trim())) {
                        if(!TextUtils.isEmpty(upwd.getText().toString().trim())) {
                            if(reupwd.getText().toString().equals(upwd.getText().toString())) {
                                CheckThread checkThread = new CheckThread();
                                checkThread.start();
                            }
                            else
                                Toast.makeText(context,"重复密码不符", Toast.LENGTH_SHORT).show();
                        }
                        else
                            Toast.makeText(context,"请输入密码", Toast.LENGTH_SHORT).show();
                    }
                    else
                    Toast.makeText(context,"请输入用户名", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    private class CheckThread extends Thread {
        @Override
        public void run() {
            Looper.prepare();
            Log.e("","threadrun----------------------");
            String isEmpty = PdaLink.getPDAServerData("userService/checkUname/"+ MD5.getMD5Str(uname.getText().toString().trim()));
            Log.e("","message-----------"+isEmpty);
            if(("1").equals(isEmpty)){
                    String s = "userService/Regin/"+MD5.getMD5Str(uname.getText().toString().trim())+","+
                            MD5.getMD5Str(upwd.getText().toString().trim());
                    String reginFlag = PdaLink.postPDAServerData(s);
                    if(!"0".equals(reginFlag)){
                        Message msg1 = new Message();
                        msg1.what = 1;
                        handler.sendMessage(msg1);
                        Intent intent = new Intent(getActivity(),MainActivity.class);
//                        JSONTokener jt = new JSONTokener(loginFlag);
//                        try {
//                            JSONObject jo = (JSONObject)jt.nextValue();
                        SharedPreferences mySharedPreferences= context.getSharedPreferences("log",
                                Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = mySharedPreferences.edit();
                        editor.putString("request", reginFlag);
                        editor.commit();
                            intent.putExtra("request",reginFlag);
                            startActivity(intent);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
                    }
                    else {
                        Message msg1 = new Message();
                        msg1.what = 1;
                        handler.sendMessage(msg1);
                        Toast.makeText(getActivity(), "网络异常，请重试", Toast.LENGTH_SHORT).show();
                    }
                }
            else{
                Message msg = new Message();
                msg.what=3;
                handler.sendMessage(msg);
            }
        }
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:                   //进度条
                    submit.setVisibility(View.VISIBLE);
                    break;
                case 3:                    //checkuname
                    submit.setVisibility(View.VISIBLE);
                    Toast.makeText(context,"用户名已存在，请重试", Toast.LENGTH_SHORT).show();
                    break;
                case 4:                    //checkuname
                    Toast.makeText(context,"可以注册", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}





