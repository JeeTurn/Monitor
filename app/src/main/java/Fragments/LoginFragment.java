package Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.apple.monitor.LogRegActivity;
import com.example.apple.monitor.MainActivity;
import com.example.apple.monitor.R;


import org.json.JSONObject;

import Tools.*;

/**
 * Created by apple on 15-2-23.
 */
public class LoginFragment extends Fragment {

    private EditText uname,upwd;
    private Button login;
    private MyHandler myHandler;
    private String request;
    private Context context;
    private ProgressBar loginProgress ;

    public LoginFragment(){}

    @SuppressLint("ValidFragment")
    public LoginFragment(Context context){
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login,null);
        myHandler = new MyHandler();
        uname = (EditText)view.findViewById(R.id.uname);
        upwd = (EditText)view.findViewById(R.id.upwd);
        login = (Button)view.findViewById(R.id.login);
        loginProgress = (ProgressBar)view.findViewById(R.id.loginProgress);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(uname.getText().toString().trim())) {
                    if (!TextUtils.isEmpty(upwd.getText().toString().trim())) {
                        loginProgress.setVisibility(View.VISIBLE);
                      //  login.setVisibility(View.GONE);
                        new Thread(){
                            @Override
                            public void run() {
                                Looper.prepare();
                                try {
                                    request = PdaLink.getPDAServerData("userService/getUser/" +
                                            MD5.getMD5Str(uname.getText().toString().trim()) + "," +
                                            MD5.getMD5Str(upwd.getText().toString().trim()));
                                    if (request.equals("0")||request.equals("error")) {
                                        Message msg1 = new Message();
                                        msg1.what = 1004;
                                        myHandler.sendMessage(msg1);
                                    } else {
                                        Message msg1 = new Message();
                                        msg1.what = 1000;
                                        myHandler.sendMessage(msg1);
                                    }
                                }
                                catch (Exception e){
                                    e.printStackTrace();
                                }
                            }

                        }
                                .start();
                    }
                    else{
                        Toast.makeText(context,"请输入密码", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(context,"请输入用户名", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private class MyHandler extends Handler {        //跳转到成功页面
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1000:
                    SharedPreferences mySharedPreferences= context.getSharedPreferences("log",
                            Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = mySharedPreferences.edit();
                    editor.putString("request", request);
                    editor.commit();
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("request", request);
                startActivity(intent);
                    break;

                case 1003:
                    loginProgress.setVisibility(View.GONE);
                    login.setVisibility(View.VISIBLE);
                    break;
                case 1004:
                    loginProgress.setVisibility(View.GONE);
                    login.setVisibility(View.VISIBLE);
                    Toast.makeText(context, "登录失败，请重试", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }


}
