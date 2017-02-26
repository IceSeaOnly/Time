package site.binghai.number2.Question;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/8/29.
 * 问题填充的实体
 */
public class QuestionEntity {
    private Long id;
    private String imgBtn_avatar;
    private String mainContent;
    private String item_leftImg;
    private String item_CenterImg;
    private String item_RightImg;
    private String item_show_comment_good_low;
    private ArrayList<String> comments_names;
    private ArrayList<String> comments;

    public QuestionEntity(){}

    public QuestionEntity(Long id,
                          String imgBtn_avatar,
                          String mainContent,
                          String item_leftImg,
                          String item_CenterImg,
                          String item_RightImg,
                          int L,int G,int C,
                          ArrayList<String> comments_names,
                          ArrayList<String> comments) {
        this.id = id;
        this.imgBtn_avatar = imgBtn_avatar;
        this.mainContent = mainContent;
        this.item_leftImg = item_leftImg;
        this.item_CenterImg = item_CenterImg;
        this.item_RightImg = item_RightImg;
        this.item_show_comment_good_low = C+"评 "+G+"赞 "+L+"呸";
        this.comments_names = comments_names;
        this.comments = comments;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImgBtn_avatar() {
        return imgBtn_avatar;
    }

    public void setImgBtn_avatar(String imgBtn_avatar) {
        this.imgBtn_avatar = imgBtn_avatar;
    }

    public String getMainContent() {
        return mainContent;
    }

    public void setMainContent(String mainContent) {
        this.mainContent = mainContent;
    }

    public String getItem_leftImg() {
        return item_leftImg;
    }

    public void setItem_leftImg(String item_leftImg) {
        this.item_leftImg = item_leftImg;
    }

    public String getItem_CenterImg() {
        return item_CenterImg;
    }

    public void setItem_CenterImg(String item_CenterImg) {
        this.item_CenterImg = item_CenterImg;
    }

    public String getItem_RightImg() {
        return item_RightImg;
    }

    public void setItem_RightImg(String item_RightImg) {
        this.item_RightImg = item_RightImg;
    }

    public String getItem_show_comment_good_low() {
        return item_show_comment_good_low;
    }

    public void setItem_show_comment_good_low(String item_show_comment_good_low) {
        this.item_show_comment_good_low = item_show_comment_good_low;
    }

    public ArrayList<String> getComments_names() {
        return comments_names;
    }

    public void setComments_names(ArrayList<String> comments_names) {
        this.comments_names = comments_names;
    }

    public ArrayList<String> getComments() {
        return comments;
    }

    public void setComments(ArrayList<String> comments) {
        this.comments = comments;
    }
}
