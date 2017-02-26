package site.binghai.number2.Messages;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.yalantis.phoenix.PullToRefreshView;

import net.steamcrafted.loadtoast.LoadToast;

import java.util.ArrayList;

import site.binghai.number2.AccountUtil;
import site.binghai.number2.R;
import site.binghai.number2.Utils.MessageDB;
import site.binghai.number2.Utils.SetListViewHeight;
import site.binghai.number2.Utils.ToastUtil;

public class MyFriends extends AppCompatActivity {

    private PullToRefreshView pull_to_refresh_friends;
    private ListView message_friendlist_listview;
    private LoadToast loadToast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_friends);
        InitView();
        InitAction();
        InitData();
    }
    private ArrayList<Friend>friends;
    private FriendListAdapter fadapter;

    private MessageDB messageDB = new MessageDB(this,"MessageDatabase",null,1);

    private void InitData() {
        friends = messageDB.getAllFriend(AccountUtil.userid);
        fadapter = new FriendListAdapter(friends,this);
        message_friendlist_listview.setAdapter(fadapter);
        fadapter.notifyDataSetChanged();
    }

    private void InitAction() {

        pull_to_refresh_friends.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(loadToast == null)
                    loadToast = new LoadToast(MyFriends.this);
                loadToast.setTranslationY(200);
                loadToast.setText("正在更新好友列表...");
                loadToast.show();
                RefreshFriends();
            }
        });

        SetListViewHeight.set(message_friendlist_listview);

        message_friendlist_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /**
                 * 启动会话
                 * */
                Intent intent = new Intent(MyFriends.this,ChatRoom.class);
                intent.putExtra("hid",String.valueOf(id));
                intent.putExtra("hname", friends.get(position).getName());
                intent.putExtra("havatar",friends.get(position).getAvatar());
                startActivity(intent);
            }
        });
    }

    /**
     * 刷新好友列表
     * */
    private void RefreshFriends() {
        FriendTool.RefreshFriendsList(this, new AfterRefreshFriendList() {
            @Override
            public void onFreshSuccess() {
                pull_to_refresh_friends.setRefreshing(false);
                loadToast.success();
                InitData();
            }

            @Override
            public void onFreshFailed(String msg) {
                pull_to_refresh_friends.setRefreshing(false);
                loadToast.error();
                new ToastUtil(MyFriends.this).errorNotice("失败","请稍后重试");
            }
        });

    }

    private void InitView() {
        pull_to_refresh_friends = (PullToRefreshView) findViewById(R.id.pull_to_refresh_friends);
        message_friendlist_listview = (ListView) findViewById(R.id.message_friendlist_listview);
    }
}
