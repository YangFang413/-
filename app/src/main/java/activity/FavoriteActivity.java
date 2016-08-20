package activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;

import com.yf.myzhihu.R;

import java.util.List;

import adapter.NewsLoadAdapter;
import database.ZhihuDB;
import model.News;

/**
 * Created by Administrator on 2016/8/14.
 * 收藏夹页面的活动类
 * 需要实现的是：
 * 1. 查询数据库中的哪些内容被收藏了。通过islike属性
 * 2. 将这些内容展示出来。
 */
public class FavoriteActivity extends Activity implements View.OnClickListener{

    private ZhihuDB db;
    private List<News> list;
    private Button backButton;
    private ListView listView;
    private NewsLoadAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.favorite_layout);

        db = ZhihuDB.getInstance(this);
        list = db.isFavorite();

        backButton = (Button) findViewById(R.id.back_button);
        backButton.setOnClickListener(this);

        listView = (ListView) findViewById(R.id.favorite_list);
        adapter = new NewsLoadAdapter(this, R.layout.listview_item, list);
        listView.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back_button:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
