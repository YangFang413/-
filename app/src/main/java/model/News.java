package model;

/**
 * Created by Administrator on 2016/8/13.
 * 收藏的新闻的实体类
 */
public class News {
    public int id; // 新闻id
    public String title; // 新闻标题
    public String imageUrl; // 新闻配图的url
    public int isLike; // 是否收藏
    public String date; // 新闻的时间

    public void setId(int id){
        this.id = id;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public void setImageUrl(String imageUrl){
        this.imageUrl = imageUrl;
    }

    public void setIsLike(int isLike) { this.isLike = isLike; }

    public void setDate(String date){ this.date = date; }

    public int getId() { return id; }

    public String getTitle(){
        return title;
    }

    public String getImageUrl(){ return imageUrl; }

    public String getDate(){ return date; }

    public int getIsLike() { return isLike; }
}
