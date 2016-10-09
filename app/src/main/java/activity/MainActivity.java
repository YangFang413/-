package activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yf.myzhihu.R;

import org.json.JSONException;

import java.util.ArrayList;
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
    private List<News> newsList = new ArrayList<>(); // 新闻列表
    private ZhihuDB db; // 数据库
    private ProgressDialog progressDialog; // 提示框

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.homepage);

        // 获取数据库的实例
        db = ZhihuDB.getInstance(this);

        // 获取各个控件的实例
        dateText = (TextView) findViewById(R.id.name);
        favorite_button = (Button) findViewById(R.id.favorite_button);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);
        newsListView = (ListView) findViewById(R.id.newslist);

        // 点击收藏按钮启动收藏活动
        favorite_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, FavoriteActivity.class);
                startActivity(intent);
            }
        });

        // 处理listview
        initData();

        // 下拉刷新的实现
        refreshLayout.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_red_light,android.R.color.holo_orange_light,
                android.R.color.holo_green_light);  // 设置加载圈颜色，最多四种
         refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(true);  // 显示刷新进度条
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                        initData();
                        newsLoadAdapter.notifyDataSetChanged();
                    }
                }, 3000);  //开启线程进行操作，设置延迟3s显示
            }
        });
    }

    // 初始化adpter中需要的news数据，先查询数据库中是否有当日的数据，
    // 如果有就直接加载，如果没有就请求服务器
    private void initData() {
        List<News> list = db.loadNews();
        if (list.size() > 0) {
            newsList.clear();
            newsList = list;
            // 显示ListView新闻列表
            newsLoadAdapter = new NewsLoadAdapter(MainActivity.this, R.layout.listview_item, newsList);
            newsLoadAdapter.notifyDataSetChanged();
            newsListView.setAdapter(newsLoadAdapter);
            newsListView.setSelection(0);
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
        } else {
            queryFromServer();
        }
    }

    private void queryFromServer(){
        showProgressDialog();

        String address = "http://news-at.zhihu.com/api/4/news/latest";
        HttpUtil.sendHttpRequest(address, new HttpCallBackListener() {

            @Override
            public void onFinish(String response, Bitmap bitmap) {
                try {
                    ParseUtility.parseNewsJSONResponse(db, response);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            initData();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                closeProgressDialog();
                Toast.makeText(MainActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showProgressDialog(){
        if (progressDialog == null){
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void closeProgressDialog(){
        if (progressDialog != null){
            progressDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
