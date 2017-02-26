package site.binghai.number2.ShowYourLove;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/9/9.
 * 表白墙的数据类
 */
public class ShowYourLoveEntity implements Serializable {
    private static final long serialVersionUID = 5401759029566243516L;
    private Long id;
    private String avatarImg;
    private String picUrl;
    private String text;
    private Long posterId;
    private String username;
    private String short_desc; //简述
    private Long postTime;
    private boolean ilike; //我是否选中了喜欢
    private int commentSum;
    private boolean nameMask; //匿名
    private int goodNumber;
    private int commentNumber;
    private String bg;//背景图

    public ShowYourLoveEntity() {
    }

    public ShowYourLoveEntity(String avatarImg, String picUrl, String text, Long posterId, String username, String short_desc, Long postTime, boolean ilike, int commentSum,boolean mask,String bg) {
        this.avatarImg = avatarImg;
        this.picUrl = picUrl;
        this.text = text;
        this.posterId = posterId;
        this.username = username;
        this.short_desc = short_desc;
        this.postTime = postTime;
        this.ilike = ilike;
        this.commentSum = commentSum;
        this.nameMask = mask;
        this.bg = bg;
    }

    public int getCommentSum() {
        return commentSum;
    }

    public void setCommentSum(int commentSum) {
        this.commentSum = commentSum;
    }

    public boolean isIlike() {
        return ilike;
    }

    public void setIlike(boolean ilike) {
        this.ilike = ilike;
    }

    public Long getPostTime() {
        return postTime;
    }

    public void setPostTime(Long postTime) {
        this.postTime = postTime;
    }

    public String getShort_desc() {
        return short_desc;
    }

    public void setShort_desc(String short_desc) {
        this.short_desc = short_desc;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAvatarImg() {
        return avatarImg;
    }

    public void setAvatarImg(String avatarImg) {
        this.avatarImg = avatarImg;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getPosterId() {
        return posterId;
    }

    public void setPosterId(Long posterId) {
        this.posterId = posterId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isNameMask() {
        return nameMask;
    }

    public void setNameMask(boolean nameMask) {
        this.nameMask = nameMask;
    }

    public int getGoodNumber() {
        return goodNumber;
    }

    public void setGoodNumber(int goodNumber) {
        this.goodNumber = goodNumber;
    }

    public int getCommentNumber() {
        return commentNumber;
    }

    public void setCommentNumber(int commentNumber) {
        this.commentNumber = commentNumber;
    }

    public String getBg() {
        return bg;
    }

    public void setBg(String bg) {
        this.bg = bg;
    }
}
