package utility;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import database.ZhihuDB;
import model.News;

/**
 * Created by Administrator on 2016/8/14.
 * 用来解析和处理服务器返回的数据
 */
public class ParseUtility {

    // 解析服务器返回的JSON类型的当日News数据
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public synchronized static void parseNewsJSONResponse (ZhihuDB db, String response) throws JSONException {
        if (!TextUtils.isEmpty(response)){
            JSONObject jo = new JSONObject(response);
            String date = jo.getString("date");
            JSONArray jsonArray = jo.getJSONArray("stories");
            for (int i=0; i < jsonArray.length(); i++){
                News news = new News();
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                JSONArray array = jsonObject.getJSONArray("images");
                String[] url = array.get(0).toString().split("\"");
                news.setDate(date);
                news.setImageUrl(url[0]);
                news.setId(jsonObject.getInt("id"));
                news.setTitle(jsonObject.getString("title"));
                news.setIsLike(0);
                db.saveNews(news);
            }
        }
    }

    // 解析服务器返回的HTML类型的新闻详情数据
    public synchronized static String parseNewsDetailJSONResponse(String response, final Context context) throws JSONException {
        String data = "";
        String cssUrl = "";
        if (! TextUtils.isEmpty(response)){
            JSONObject jo = new JSONObject(response);
            data = jo.getString("body");
            String[] csses = jo.getJSONArray("css").get(0).toString().split("\"");
            cssUrl = csses[0];
            HttpUtil.sendHttpRequest(cssUrl, new HttpCallBackListener() {
                @Override
                public void onFinish(String response, Bitmap bitmap) {
                    try {
                        File file = new File(context.getFilesDir(), "style.css");
                        if (file.exists()){
                            return;
                        } else {
                            FileOutputStream outputStream = new FileOutputStream(file);
                            byte[] b = response.getBytes();
                            outputStream.write(b);
                            outputStream.close();
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(Exception e) {

                }
            });

        }
        return data;
    }
}
