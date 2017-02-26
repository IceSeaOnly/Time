package site.binghai.number2.Utils;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import site.binghai.number2.Messages.Friend;
import site.binghai.number2.Messages.MessageEntityImpl;
import site.binghai.number2.Messages.TextMessage;

/**
 * Created by Administrator on 2016/9/16.
 */
public class MessageDB extends SQLiteOpenHelper {

    public MessageDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }


    // 获取用户的代理秘钥
    public static String getAccountKey() {
        return null;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        /**
         * 联系人数据表
         * id 序列号，无意义
         * userid 用户id
         * friendid 用户的好友id
         * onshow 是否在对话列表中展示
         * username 好友名称
         * avatar 好友头像
         * cur_message 最新消息
         * cur_time 最新消息的时间
         * mtype  消息类型，true为接收，false为发送
         * */
        db.execSQL("create table if not exists Friends(" +
                "id integer primary key AUTOINCREMENT," +
                "userid long," +
                "friendid long," +
                "onshow boolean," +
                "username text," +
                "avatar text," +
                "cur_message text," +
                "cur_time text," +
                "mtype boolean)");
        /**
         * 消息数据表
         * */
        db.execSQL("create table if not exists Messages(" +
                "id integer primary key AUTOINCREMENT," +
                "userid long," +
                "friendid long," +
                "content text," +
                "mtime text," +
                "mtype boolean," +
                "readed boolean," +
                "timestamp long," +
                "msgtype int)"); // 消息类型

        Log.i("创建数据库Message Database", "已创建");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private SQLiteDatabase wdb;
    private SQLiteDatabase rdb;
    private SimpleDateFormat sd = new SimpleDateFormat("HH:mm");

    /**
     * 好友有则更新无则插入
     */
    public void InsertORupdateFriend(Long uid, Long fid, String avatar, String username) {
        if (wdb == null) wdb = getWritableDatabase();

        ContentValues v = new ContentValues();
        v.put("userid", uid);
        v.put("friendid", fid);
        v.put("onshow", true);
        v.put("username", username);
        v.put("avatar", avatar);
        v.put("cur_message", "");
        v.put("cur_time", sd.format(new Date(System.currentTimeMillis())));
        v.put("mtype", true);

        wdb.replace("Friends", null, v);
    }

    /**
     * 清除所有好友
     */
    public void clearFriends() {
        if (wdb == null) wdb = getWritableDatabase();
        wdb.execSQL("delete from Friends");
    }

    /**
     * 插入数据聊天信息
     */
    public long insertMessage(Long uid, Long fid, String content, Long timestamp, boolean mtype, int msgType, boolean readed) {

        if (wdb == null) wdb = getWritableDatabase();

        ContentValues v = new ContentValues();
        v.put("userid", uid);
        v.put("friendid", fid);
        v.put("content", content);
        v.put("mtime", sd.format(new Date(timestamp)));
        v.put("mtype", mtype);
        v.put("readed", readed);
        v.put("timestamp", timestamp);
        v.put("msgtype", msgType);
        long id = wdb.insert("Messages", null, v);

        updateFriend(uid, fid, content, sd.format(new Date(timestamp)), mtype);
        return id;
    }


    /**
     * 更新聊天列表里的信息
     */
    public void updateFriend(Long uid, Long fid, String content, String time, boolean mtype) {

        if (wdb == null) wdb = getWritableDatabase();

        ContentValues v = new ContentValues();
        v.put("userid", uid);
        v.put("friendid", fid);
        v.put("cur_message", content);
        v.put("cur_time", time);
        v.put("mtype", mtype);
        v.put("onshow", true);
        wdb.update("Friends", v, "userid=? and friendid=?", new String[]{String.valueOf(uid), String.valueOf(fid)});
    }

    /**
     * 取出所有好友
     */

    public ArrayList<Friend> getAllFriend(Long uid) {

        if (rdb == null) rdb = getReadableDatabase();

        ArrayList<Friend> res = new ArrayList<>();
        Cursor cursor = rdb.query("Friends", null, "userid=?", new String[]{String.valueOf(uid)}, null, null, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                Friend f = new Friend(
                        cursor.getLong(2),//id
                        cursor.getString(4),//name
                        cursor.getString(5),//avatar
                        cursor.getString(6),//cur_message
                        cursor.getString(7),//cur_time
                        cursor.getInt(8)//cur_time
                );
                res.add(f);
                cursor.moveToNext();
            }
        }
        return res;
    }

    /**
     * 取出可以显示在对话列表的好友
     */
    public ArrayList<Friend> getChatingFriend(Long uid) {

        if (rdb == null) rdb = getReadableDatabase();

        ArrayList<Friend> res = new ArrayList<>();
        Cursor cursor = rdb.query("Friends", null, "userid=? and onshow=?", new String[]{String.valueOf(uid), "1"}, null, null, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                Friend f = new Friend(
                        cursor.getLong(2),//id
                        cursor.getString(4),//name
                        cursor.getString(5),//avatar
                        cursor.getString(6),//cur_message
                        cursor.getString(7),//cur_time
                        cursor.getInt(8)//mtype
                );
                res.add(f);
                cursor.moveToNext();
            }
        }

        /**
         * 对每一个Friend查询最近一条消息
         * */
        for (int i = 0; i < res.size(); i++) {
            Long fid = res.get(i).getFriendid();
            dataGroup dg = queryCurMsg(uid, fid);
            res.get(i).setCur_message(dg.cur_msg);
            res.get(i).setCur_time(dg.timestamp);
        }
        return res;
    }

    private dataGroup queryCurMsg(Long uid, Long fid) {
        if (rdb == null) rdb = getReadableDatabase();

        Cursor cursor = rdb.rawQuery("select content,timestamp from Messages where userid=? and friendid=? order by timestamp desc limit 0,1", new String[]{String.valueOf(uid), String.valueOf(fid)});
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                return new dataGroup(cursor.getString(0), TimeFormater.format_hh_mm(cursor.getLong(1)));
            }
        }
        return new dataGroup("- 无消息 -", "");
    }

    public void deleteMsg(Long id) {
        if (wdb == null) wdb = getWritableDatabase();
        wdb.execSQL("delete from Messages where id = "+id);
        Log.i("ChatRoom","delete from Messages where id = "+id);
    }

    private class dataGroup {
        public String cur_msg;
        public String timestamp;

        public dataGroup(String cur_msg, String timestamp) {
            this.cur_msg = cur_msg;
            this.timestamp = timestamp;
        }
    }

    /**
     * 取出与好友的所有对话
     * mid:我的id
     * hid：好友id
     */
    public ArrayList<MessageEntityImpl> getAllMessage(String mid, String hid, String mAvatar, String hAvatar) {
        ArrayList<MessageEntityImpl> msgs = new ArrayList<MessageEntityImpl>();

        if (rdb == null) rdb = getReadableDatabase();

        Cursor cursor = rdb.query("Messages", null, "userid=? and friendid=?", new String[]{mid, hid}, null, null, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                if (cursor.getInt(8) == MessageEntityImpl.TEXT_MSG) {
                    TextMessage tm = new TextMessage(
                            cursor.getLong(0),
                            cursor.getString(3),
                            cursor.getString(4),
                            cursor.getInt(5) == 0 ? mAvatar : hAvatar,
                            cursor.getInt(5) == 0 ? false : true,
                            cursor.getLong(7),
                            cursor.getLong(2)
                    );
                    msgs.add(tm);
                } else {

                }
                Log.i("ChatRoom","消息id="+cursor.getLong(0));
                cursor.moveToNext();
            }
        }
        setAllReaded(mid, hid);
        return msgs;
    }


    /**
     * 把与该好友的消息设为已读
     */
    private void setAllReaded(String mid, String hid) {
        if (wdb == null) wdb = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put("readed", true);
        wdb.update("Messages", v, "userid=? and friendid=?", new String[]{mid, hid});
    }


}