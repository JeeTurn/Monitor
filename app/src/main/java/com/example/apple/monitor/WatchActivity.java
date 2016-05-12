package com.example.apple.monitor;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
//
//import com.googlecode.javacv.FFmpegFrameRecorder;
//import com.googlecode.javacv.cpp.opencv_core;
//import com.googlecode.javacv.cpp.opencv_imgproc.*;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import pojo.ServerTable;
import pojo.User;

//import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;

/**
 * Created by apple on 16/4/29.
 */
public class WatchActivity extends Activity {

    ImageView watch_image;
    Button watch_getpic,watch_rec;
    User user;
    ServerTable serverTable;
    InputStream inputStream;
    byte bytes[];
    Bitmap bitmap;
    MyHandler myHandler;
    int REC_STATUS;//1是正在录制 0是未录制



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch);
        REC_STATUS = 0;
        Bundle b = getIntent().getExtras();
        user = (User)b.get("user");
        serverTable = (ServerTable)b.get("server");

        myHandler = new MyHandler();

        watch_image = (ImageView)findViewById(R.id.watch_image);
        watch_getpic = (Button)findViewById(R.id.watch_getpic);
        watch_rec = (Button)findViewById(R.id.watch_rec);


        WatchThread watchThread = new WatchThread();
        watchThread.start();


        //截屏功能
        watch_getpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //获取bitmap byte数组
                if(bitmap != null) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);

                    //获取时间当文件名
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
                    Date curDate = new Date(System.currentTimeMillis());
                    String temptime = formatter.format(curDate)+ new Random().nextInt(1000);
                    FileOutputStream outputStream;
                    //写入文件中
                    try {
                        File picDir = new File(getFilesDir().getPath().toString()+"picDir");
                        if (!picDir.exists()) {
                            picDir.mkdirs();
                        }
                        File file = new File(getFilesDir().getPath().toString()+"picDir/"+temptime+".jpg");
                        if(!file.exists())
                            file.createNewFile();
                        Log.e("","file path:"+file.getPath());
                        outputStream = new FileOutputStream(file);
                        outputStream.write(baos.toByteArray());
                        outputStream.flush();
                        outputStream.close();


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else
                    Toast.makeText(WatchActivity.this,"未获取图像 无法保存",Toast.LENGTH_SHORT).show();
            }
        });


        //录像功能
        watch_rec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                File picDir = new File(getFilesDir().getPath().toString()+"recDir");
                if (!picDir.exists()) {
                    picDir.mkdirs();
                }

                if(REC_STATUS==0){
                    SaveVedioPic saveVedioPic = new SaveVedioPic();
                    saveVedioPic.start();
                    REC_STATUS =1;
                    watch_rec.setText("正在录像");
                }
                else {
                    REC_STATUS = 0;
                    watch_rec.setText("正在录像");
                }
            }
        });

    }

    private class SaveVedioPic extends Thread{
        @Override
        public void run() {
            int count = 0;
            FileOutputStream outputStream;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            Date curDate = new Date(System.currentTimeMillis());
            String temptime = formatter.format(curDate) + new Random().nextInt(1000);
            int hight=0,width=0;
            File picDir = null;
            while(REC_STATUS==1){
                if(bitmap!=null) {
                    try {

                        picDir = new File(getFilesDir().getPath().toString()+"recDir/"+temptime);
                        if (!picDir.exists()) {
                            picDir.mkdirs();
                        }
                        File file = new File(getFilesDir().getPath().toString() + "recDir/" + temptime+ "/" + count + ".jpg");
                        if (!file.exists())
                            file.createNewFile();
                        outputStream = new FileOutputStream(file);
                        outputStream.write(baos.toByteArray());
                        outputStream.flush();
                        outputStream.close();
//                        hight = bitmap.getHeight();
//                        width = bitmap.getWidth();
                        count++;
                        sleep(50);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
//            try {
//                File file = new File(getFilesDir().getPath().toString() + "recDir/" + temptime + ".mp4");
//                if (!file.exists())
//                    file.createNewFile();
//                FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(file.getPath(), width, hight);
//                recorder.setFormat("mp4");
//                recorder.setFrameRate(2f);//录像帧率
//                recorder.start();
//
//                File picFiles[] = picDir.listFiles();
//                for (File f:picFiles) {
//                    opencv_core.IplImage image = cvLoadImage(f.getPath());
//                    recorder.record(image);
//                    f.delete();
//                }
//                picDir.delete();
//                recorder.stop();
//
//
//            }catch (Exception e){
//                e.printStackTrace();
//            }
        }
    }



    private class WatchThread extends Thread{
        @Override
        public void run() {
            try {
                while (true) {
                    Socket socket = new Socket(serverTable.getsInnerIp(), 5000);
                    inputStream = socket.getInputStream();
                    Bitmap bitmap1 = BitmapFactory.decodeStream(inputStream);
                    bytes  =  InputToBytes(inputStream);
                    Matrix m = new Matrix();
                    m.setRotate(90,
                            (float) bitmap1.getWidth() / 2, (float) bitmap1.getHeight() / 2);
                    bitmap = Bitmap.createBitmap(bitmap1,0,0,bitmap1.getWidth(),bitmap1.getHeight(),m,true);
                    bitmap1.recycle();
                    Message msg = new Message();
                    msg.what = 1;
                    myHandler.sendMessage(msg);
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
            switch (msg.what){
                case 1:
                    watch_image.setImageBitmap(bitmap);
                    break;
            }
        }
    }


    private byte[] InputToBytes(InputStream inputStream){
        try {
            ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
            byte[] buff = new byte[100]; //buff用于存放循环读取的临时数据
            int rc = 0;
            while ((rc = inputStream.read(buff, 0, 100)) > 0) {
                swapStream.write(buff, 0, rc);
            }
            return  swapStream.toByteArray();
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }







}
