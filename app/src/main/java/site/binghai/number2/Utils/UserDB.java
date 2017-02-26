package site.binghai.number2.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import site.binghai.number2.AccountUtil;

/**
 * Created by Administrator on 2016/9/27.
 */
public class UserDB extends SQLiteOpenHelper{


    public UserDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists UserTable(" +
                "userid long primary key," +
                "avatar text," +
                "nickname text," +
                "phone long," +
                "school text," +
                "sex integer," +
                "grade integer," +
                "interest text," +
                "token text)");
        Log.i("用户表：","创建成功");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private SQLiteDatabase rdb;
    private SQLiteDatabase wdb;

    /**
     * 插入用户信息
     * */
    public void insertOrUpdateUser(){
        /**
         * 只存一个用户的信息，当登录新账号时，删除旧信息
         * */
        if(getUserId() != AccountUtil.getAccountId())
            cleanUserTable();

        if (wdb == null) wdb = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put("userid", AccountUtil.getAccountId());
        v.put("avatar",AccountUtil.my_avatar);
        v.put("nickname",AccountUtil.nick_name);
        v.put("phone",AccountUtil.phone);
        v.put("school",AccountUtil.school);
        v.put("sex",AccountUtil.sex);
        v.put("grade",AccountUtil.grade);
        v.put("interest",AccountUtil.interest);
        v.put("token",AccountUtil.token);
        Long res = wdb.replace("UserTable",null,v);
        Log.i("保存用户信息到数据库:","完毕,res = "+res);
    }

    /**
     * 清空用户表
     * */
    public void cleanUserTable(){
        if (wdb == null) wdb = getWritableDatabase();
        wdb.execSQL("delete from UserTable");
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        Log.i("清空用户表：",df.format(new Date(System.currentTimeMillis())));
    }

    /**
     * 查询表中用户信息
     * */
    public Long getUserId() {
        if(rdb == null) rdb = getReadableDatabase();
        Cursor cursor = rdb.rawQuery("select * from UserTable", null, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                return cursor.getLong(0);
            }
        }
        return -1L;
    }

}
