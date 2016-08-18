package utility;

import android.annotation.TargetApi;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;

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
            Log.d("ParseUtility-1", jo.getString("date"));
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
    public synchronized static String parseNewsHTMLResponse(String response) throws JSONException {
        String data = "";
        if (! TextUtils.isEmpty(response)){
            JSONObject jo = new JSONObject(response);
            data = jo.getString("body");
        }
        return data;
    }
}
