package com.example.apple.monitor;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.view.Window;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import Tools.PdaLink;

import static android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN;

/**
 * Created by apple on 16/4/27.
 */
public class CameraActivity extends Activity {
    SurfaceView sView;
    SurfaceHolder surfaceHolder;
    int screenWidth=0, screenHeight=0;
    Camera camera;                    // ����ϵͳ���õ������
    boolean isPreview = false;        //�Ƿ��������
    private String ipname;
    ServerSocket serverSocket;
    String sid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            // 全屏显示
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(FLAG_FULLSCREEN, FLAG_FULLSCREEN);
            setContentView(R.layout.activity_camera);
            sView = (SurfaceView) findViewById(R.id.sView);                  // ��ȡ������SurfaceView���
            sid = new String(getIntent().getByteArrayExtra("sid"));
            Log.e("CameraActivity","sid="+sid);
            surfaceHolder = sView.getHolder();                               // ���SurfaceView��SurfaceHolder
        try {
            serverSocket = new ServerSocket(5000);
        } catch (IOException e) {
            e.printStackTrace();
        }


        // ΪsurfaceHolder���һ���ص�������
            surfaceHolder.addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    initCamera();
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    if (camera != null) {
                        if (isPreview)
                            camera.stopPreview();
                        camera.release();
                        camera = null;
                    }
                    System.exit(0);
                }
            });
            // 兼容低版本
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        }

    private void initCamera() {
        if (!isPreview) {
            camera = Camera.open();
        }
        if (camera != null && !isPreview) {
            try{
                Camera.Parameters parameters = camera.getParameters();
                List<Camera.Size> sizeList = parameters.getSupportedPreviewSizes();
                if (sizeList.size() > 1) {
                    Iterator<Camera.Size> itor = sizeList.iterator();
                    while (itor.hasNext()) {
                        Camera.Size cur = itor.next();
                        if (cur.width >= screenWidth
                                && cur.height >= screenHeight) {
                            screenWidth = cur.width;
                            screenHeight = cur.height;
                            break;
                        }
                    }
                }
                else{
                    screenWidth = ((Camera.Size)sizeList.get(0)).width;
                    screenHeight = ((Camera.Size)sizeList.get(0)).height;
                }
                ViewGroup.LayoutParams lp = sView.getLayoutParams();

                //因为是翻转90度 所以宽就是高 高就是宽
                lp.height = screenWidth;
                lp.width = screenHeight;
                sView.setLayoutParams(lp);
                parameters.setPreviewSize(screenHeight, screenWidth);
                parameters.setPreviewFpsRange(20,30);
                parameters.setPictureFormat(ImageFormat.NV21);
           //     camera.setParameters(parameters);
                camera.setPreviewDisplay(surfaceHolder);
                camera.setPreviewCallback(new StreamIt(ipname));
       //         camera.setPreviewCallbackWithBuffer(this);
                camera.setDisplayOrientation(90);
                camera.startPreview();
                camera.autoFocus(null);

            } catch (Exception e) {
                e.printStackTrace();
            }
            isPreview = true;
        }
    }

//    @Override
//    public void onPreviewFrame(byte[] data, Camera camera) {
//        if (frameListener != null) {
//            frameListener.onFrame(data, 0, data.length, 0);
//        }
//        cam.addCallbackBuffer(buf);
//    }

    class MyThread extends Thread {
        private byte byteBuffer[] = new byte[1024];
        private OutputStream outsocket;
        private ByteArrayOutputStream myoutputstream;
        private String ipname;

        public MyThread(ByteArrayOutputStream myoutputstream, String ipname) {
            this.myoutputstream = myoutputstream;
            this.ipname = ipname;
            try {
                myoutputstream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            try {

                Socket tempSocket = serverSocket.accept();
                //  BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(tempSocket.getOutputStream()));
                outsocket = tempSocket.getOutputStream();
                ByteArrayInputStream inputstream = new ByteArrayInputStream(myoutputstream.toByteArray());
                int amount;
                while ((amount = inputstream.read(byteBuffer)) != -1) {
                    outsocket.write(byteBuffer, 0, amount);
                }
                myoutputstream.flush();
                myoutputstream.close();
                tempSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }




    }
    class StreamIt implements Camera.PreviewCallback {
        private String ipname;
        public StreamIt(String ipname){
            this.ipname = ipname;
        }

        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            Camera.Size size = camera.getParameters().getPreviewSize();
            try{
                YuvImage image = new YuvImage(data, ImageFormat.NV21, size.width, size.height, null);
                if(image!=null){
                    ByteArrayOutputStream outstream = new ByteArrayOutputStream();
                    image.compressToJpeg(new Rect(0, 0, size.width, size.height), 50, outstream);
                    outstream.flush();
                    //todo  过多图片导致oom 利用垃圾回收机制
                    image = null;

                    System.gc();
                    Thread th = new MyThread(outstream,ipname);
                    th.start();
                }
            }catch(Exception ex){
                Log.e("Sys", "Error:" + ex.getMessage());
            }
        }
    }


    @Override
    protected void onStop() {
        CloseThread closeThread = new CloseThread();
        closeThread.start();
        super.onStop();
    }

    class CloseThread extends Thread{
        @Override
        public void run() {
            String flag = "0";
            while(!flag.equals("1"));
               flag = PdaLink.getPDAServerData("serverService/delServer/"+sid);
        }
    }






}




