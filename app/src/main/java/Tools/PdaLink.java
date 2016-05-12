package Tools;

import android.content.Context;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.util.EntityUtils;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

/**
 * Created by apple on 15-2-23.
 */
public class PdaLink {
    public static final String ServerIp = "192.168.155.3";
    public static final String url = "http://192.168.155.3:8080/MonitorServer/jaxrs/";
    public static String getPDAServerData(String turl){
        HttpClient client = new DefaultHttpClient();
        HttpGet request;
        try {

            request = new HttpGet(new URI(url+turl));
            request.addHeader("Content-Type", "text/html");    //这行很重要
//            request.addHeader("charset", HTTP.UTF_8);
            HttpResponse response = client.execute(request);
            Log.e("", "post request-----------");
            if(response.getStatusLine().getStatusCode() == 200){
                HttpEntity entity = response.getEntity();

                if(entity != null){
                    return EntityUtils.toString(entity);
                }
            }
            else{
                return "error";
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static String postPDAServerData(String turl){
        HttpClient client = new DefaultHttpClient();
        HttpPost request;
        try {

            request = new HttpPost(new URI(url+turl));
            request.addHeader("Content-Type", "text/html");    //这行很重要
//            request.addHeader("charset", HTTP.UTF_8);
            HttpResponse response = client.execute(request);
            Log.e("", "post request-----------");
            if(response.getStatusLine().getStatusCode() == 200){
                HttpEntity entity = response.getEntity();

                if(entity != null){
                    return EntityUtils.toString(entity);
                }
            }
            else{
                return "error";
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }




//    public static File download(Context context, String validateURL) {
//        InputStream is = null;
//        BufferedInputStream bis = null;
//        FileOutputStream fos = null;
//        BufferedOutputStream bos = null;
//        DefaultHttpClient httpClient = null;
//        File tfile = null;
//
//        String filename = validateURL.split("upload")[1];
//        filename = filename.substring(1);
//        try {
//
//            httpClient = new DefaultHttpClient(new BasicHttpParams());
//            HttpPost httpRequest = new HttpPost(validateURL);//validateURL是的请求地址
//            HttpResponse response = httpClient.execute(httpRequest);
//            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
//                throw new Exception("请求失败");
//            }
//            HttpEntity resEntity = response.getEntity();
//            is = resEntity.getContent();//获得文件的输入流
//            bis = new BufferedInputStream(is);
//            fos = context.openFileOutput(filename, context.MODE_PRIVATE);
//            bos = new BufferedOutputStream(fos);
//            byte[] bytes = new byte[1024];
//            int len = 0;//最后一次的长度可能不足4096
//            while ((len = bis.read(bytes)) > 0) {
//                bos.write(bytes, 0, len);
//            }
//            bos.flush();
//            tfile = context.getFileStreamPath(filename);
// //           bitmap = BitmapFactory.decodeFile(tfile.getPath());
//            is.close();
//        }
//        catch (Exception e){
//            e.printStackTrace();}
//        finally
//         {
//
//             try {
//                 if (bis != null) bis.close();
//                 if (bos != null) bos.close();
//                 if (fos != null) fos.close();
//                 httpClient.getConnectionManager().shutdown();
//             }
//             catch (IOException e){
//                 e.printStackTrace();
//             }
//        }
//        return tfile;
//    }




}
