package site.binghai.number2.Messages;

import java.util.Date;

/**
 * Created by Administrator on 2016/9/24.
 * 好友类
 */
public class Friend {
    private Long friendid;
    private String name;
    private String avatar;
    private String cur_message;
    private String cur_time;

    public Friend(Long friendid, String name, String avatar, String cur_message, String cur_time,int unread) {
        this.friendid = friendid;
        this.name = name;
        this.avatar = avatar;
        this.cur_message = cur_message;
        this.cur_time = cur_time;
    }

    public Long getFriendid() {
        return friendid;
    }

    public void setFriendid(Long friendid) {
        this.friendid = friendid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getCur_message() {
        return cur_message;
    }

    public void setCur_message(String cur_message) {
        this.cur_message = cur_message;
    }

    public String getCur_time() {
        return cur_time;
    }

    public void setCur_time(String cur_time) {
        this.cur_time = cur_time;
    }


    @Override
    public String toString() {
        return "Friend{" +
                "friendid=" + friendid +
                ", name='" + name + '\'' +
                ", avatar='" + avatar + '\'' +
                ", cur_message='" + cur_message + '\'' +
                ", cur_time='" + cur_time +
                '}';
    }
}
