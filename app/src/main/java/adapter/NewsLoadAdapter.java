package adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yf.myzhihu.R;

import org.json.JSONException;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import model.News;
import utility.HttpCallBackListener;
import utility.HttpUtil;

/**
 * Created by Administrator on 2016/8/14.
 * 为ListView设置Adapter
 */

public class NewsLoadAdapter extends ArrayAdapter<News> {

    private int resourceId; // 布局文件的id

    public NewsLoadAdapter(Context context, int resource, List<News> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    // 每个子项在滚动到屏幕内的时候调用。
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final News news = getItem(position); // 获取当前位置的News对象
        View view;
        final ViewHolder viewHolder;
        if (convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.newsTitle = (TextView) view.findViewById(R.id.news_title);
            viewHolder.newsImage = (ImageView) view.findViewById(R.id.news_image);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.newsTitle.setText(news.getTitle());

        // 请求网络，获取图片
        HttpUtil.sendImageRequest(news.getImageUrl(), new HttpCallBackListener() {

            // 将回调后的bitmap进行裁剪，放到imageView中
            @Override
            public void onFinish(String response, Bitmap bitmap) {
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();
                int newWidth = viewHolder.newsImage.getWidth();
                int newHeight = viewHolder.newsImage.getHeight();

                float scaleWidth = ((float) newWidth) / width;
                float scaleHeight = ((float) newHeight) / height;

                Matrix matrix = new Matrix();
                matrix.postScale(scaleWidth, scaleHeight);

                final Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width,
                        height, matrix, true);
                ((Activity) getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        viewHolder.newsImage.setImageBitmap(resizedBitmap);
                    }
                });
            }

            @Override
            public void onError(Exception e) {

            }
        });

        return view;
    }

    private class ViewHolder{
        TextView newsTitle;
        ImageView newsImage;
    }
}

