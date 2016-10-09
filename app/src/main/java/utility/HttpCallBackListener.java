package utility;

import android.graphics.Bitmap;

import org.json.JSONException;

import java.util.List;

import model.News;

/**
 * Created by Administrator on 2016/8/14.
 * 方法回调的监听器接口
 */
public interface HttpCallBackListener {
    void onFinish(String response, Bitmap bitmap);
    void onError(Exception e);
}
