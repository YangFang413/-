package utility;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Administrator on 2016/8/14.
 * 该类为访问网络的操作类。实现访问网络，获取服务器返回的数据
 */
public class HttpUtil {

    // 请求网络，获取数据
    public static void sendHttpRequest(final String address, final HttpCallBackListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(address);
                    Log.d("sendHttpRequest-1", url.toString());
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setDoInput(true);

                    InputStream in = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    if (listener != null) {
                        // 回调onFinish()方法
                        listener.onFinish(stringBuilder.toString(), null);
                        Log.d("sendHttpRequest-2", stringBuilder.toString());
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onError(e);
                    }
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }

                }
            }
        }).start();
    }

    // 请求网络，获取图片
    public static void sendImageRequest(final String address, final HttpCallBackListener listener){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    connection = (HttpURLConnection) new URL(address).openConnection();
                    connection.setRequestMethod("GET");
                    connection.setDoInput(true);

                    InputStream in = connection.getInputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(in);

                    if (listener != null){
                        listener.onFinish(null, bitmap);
                    }

                } catch (Exception e) {
                    if (listener != null){
                        listener.onError(e);
                    }
                    e.printStackTrace();
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }
}