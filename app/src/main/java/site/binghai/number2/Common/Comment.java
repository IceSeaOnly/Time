package site.binghai.number2.Common;
/**
 * Created by Administrator on 2016/10/23.
 * 通用评论类
 */

public class Comment {
    public static int FOR_DYNAMIC = 0;
    public static int FOR_SHOW_YOUR_LOVE = 1;

    public static int TEXT_COMMENT = 0;
    public static int VOICE_COMMENT = 1;
    private Long id;
    private int type;/** 哪个个实体的评论*/
    private int ctype;/** 评论类型，语音 or 文字*/
    private Long entityId;
    private Long userid;
    private String username;
    private String avatar;
    private String comments;
    private Long commitTime;

    private String voiceUrl;
    private int voice_long;//语音长度

    private Boolean hasDelete;

    public Comment(Long entityId, Long userid, String username, String avatar, String comments, int t) {
        this.entityId = entityId;
        this.userid = userid;
        this.username = username;
        this.comments = comments;
        this.commitTime = System.currentTimeMillis();
        this.hasDelete = false;
        this.avatar = avatar;
        this.type = t;
        this.voiceUrl = "";
        this.voice_long = 0;
        this.ctype = TEXT_COMMENT;
    }

    public Comment(Long entityId, Long userid, String username, String avatar, int voice_long, String voiceUrl) {
        this.entityId = entityId;
        this.userid = userid;
        this.username = username;
        this.avatar = avatar;
        this.commitTime = System.currentTimeMillis();
        this.voiceUrl = voiceUrl;
        this.voice_long = voice_long;
        this.hasDelete = false;
        this.comments = "";
        this.ctype = VOICE_COMMENT;
    }

    public Comment() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long dynamicId) {
        this.entityId = dynamicId;
    }

    public Long getUserid() {
        return userid;
    }

    public void setUserid(Long userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Long getCommitTime() {
        return commitTime;
    }

    public void setCommitTime(Long commitTime) {
        this.commitTime = commitTime;
    }

    public Boolean getHasDelete() {
        return hasDelete;
    }

    public void setHasDelete(Boolean hasDelete) {
        this.hasDelete = hasDelete;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getCtype() {
        return ctype;
    }

    public void setCtype(int ctype) {
        this.ctype = ctype;
    }

    public String getVoiceUrl() {
        return voiceUrl;
    }

    public void setVoiceUrl(String voiceUrl) {
        this.voiceUrl = voiceUrl;
    }

    public int getVoice_long() {
        return voice_long;
    }

    public void setVoice_long(int voice_long) {
        this.voice_long = voice_long;
    }
}
