package activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;

import com.yf.myzhihu.R;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import database.ZhihuDB;

/**
 * Created by Administrator on 2016/8/18.
 * 新闻详情的活动类
 * 需要实现的功能：
 * 1. 显示网页形式的新闻
 * 2. 点击收藏按钮更新数据库
 *    点击单数次收藏，点击双数次取消收藏
 */
public class NewsDetailActivity extends Activity implements View.OnClickListener {

    private Button backButton;
    private Button isLikeButton;
    private WebView newsDetails;
    int flag = 0;
    private ZhihuDB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.news_details);

        // 初始化各控件
        backButton = (Button) findViewById(R.id.back_button);
        isLikeButton = (Button) findViewById(R.id.favorite_button);
        newsDetails = (WebView) findViewById(R.id.news_details);

        // 为按钮绑定监听器
        backButton.setOnClickListener(this);
        isLikeButton.setOnClickListener(this);

        String text = getIntent().getStringExtra("response");
        try {
            newsDetails.loadData(formatString(text), "text/html", "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back_button:
                Intent i = new Intent(NewsDetailActivity.this, MainActivity.class);
                startActivity(i);
                break;
            case R.id.favorite_button:
                flag++;
                if (flag % 2 == 0){

                } else {

                }
                break;
            default:
                break;
        }
    }

    // 解析从服务器端返回的HTML数据
    private String formatString(String data) throws UnsupportedEncodingException {
        String s = "";
        s = URLEncoder.encode(data, "utf-8");
        return s;
    }
}
