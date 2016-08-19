package activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.yf.myzhihu.R;

import org.json.JSONException;

import java.util.List;

import adapter.NewsLoadAdapter;
import database.ZhihuDB;
import model.News;
import utility.HttpCallBackListener;
import utility.HttpUtil;
import utility.ParseUtility;

/**
 * Created by Administrator on 2016/8/14.
 * 打开APP首页的活动内容。
 * 需要完成如下任务：
 * 1. 设置首页左上角的当前时间显示
 * 2. 设置首页右上角的Button点击的响应事件。点击就跳转到收藏夹活动。
 * 3. 下拉刷新的实现
 * 4. 动态加载数据库中的News详情
 */
public class MainActivity extends Activity {

    private TextView dateText; // 左上角的时间显示
    private Button favorite_button; // 右上角的收藏夹按钮
    private SwipeRefreshLayout refreshLayout; // 下拉刷新的控件
    private ListView newsListView; // 新闻的列表
    private NewsLoadAdapter newsLoadAdapter; //加载新闻的适配器
    private List<News> newsList; // 新闻列表
    private ZhihuDB db; // 数据库

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.homepage);

        // 获取各个控件的实例
        dateText = (TextView) findViewById(R.id.date);
        favorite_button = (Button) findViewById(R.id.favorite_button);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);

        favorite_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, FavoriteActivity.class);
                startActivity(intent);
            }
        });

        db = ZhihuDB.getInstance(this);
        newsList = db.loadNews();
        if (newsList.size() == 0){
            newsList = initData();
        }
        newsListView = (ListView) findViewById(R.id.newslist);
        newsLoadAdapter = new NewsLoadAdapter(MainActivity.this, R.layout.listview_item, newsList);
        newsListView.setAdapter(newsLoadAdapter);
        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                final News news = newsList.get(position);
                String address = "http://news-at.zhihu.com/api/4/news/" + news.getId();
                HttpUtil.sendHttpRequest(address, new HttpCallBackListener() {
                    @Override
                    public void onFinish(String response, Bitmap bitmap) {
                        String data = null;
                        try {
                            data = ParseUtility.parseNewsDetailJSONResponse(response, getApplicationContext());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Intent intent = new Intent(MainActivity.this, NewsDetailActivity.class);
                        intent.putExtra("response", data);
                        intent.putExtra("id", news.getId());
                        startActivity(intent);
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
            }
        });
    }

    // 如果从数据库中获取到的数据为空，那么访问网络获取当日的新闻数据
    private List<News> initData(){
        final List<News> list;
        String address = "http://news-at.zhihu.com/api/4/news/latest";
        HttpUtil.sendHttpRequest(address, new HttpCallBackListener() {

            @Override
            public void onFinish(String response, Bitmap bitmap) {
                try {
                    ParseUtility.parseNewsJSONResponse(db, response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
        list = db.loadNews();
        return list;
    }
}
