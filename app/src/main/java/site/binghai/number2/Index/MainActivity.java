package site.binghai.number2.Index;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.wireless.security.jaq.JAQException;
import com.alibaba.wireless.security.jaq.SecurityInit;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.bumptech.glide.Glide;
import com.dalong.library.view.LoopRotarySwitchView;
import com.flyco.dialog.entity.DialogMenuItem;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.NormalListDialog;
import com.kevin.loopview.AdLoopView;
import com.kevin.loopview.internal.BaseLoopAdapter;
import com.kevin.loopview.internal.LoopData;
import com.kevin.loopview.utils.JsonTool;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.ramotion.foldingcell.FoldingCell;
import com.soundcloud.android.crop.Crop;
import com.yalantis.phoenix.PullToRefreshView;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import cn.bingoogolapple.refreshlayout.BGARefreshViewHolder;
import cn.pedant.SweetAlert.SweetAlertDialog;
import cz.msebera.android.httpclient.Header;

import static site.binghai.number2.Dynamic.PostNewDynamic.mTempDir;
import static site.binghai.number2.Dynamic.PostNewDynamic.mCurrentPhotoPath;

import site.binghai.number2.AccountUtil;
import site.binghai.number2.Application.IMessageReceviver;
import site.binghai.number2.Common.Comment;
import site.binghai.number2.Dynamic.DynamicActionCallBack;
import site.binghai.number2.Dynamic.DynamicContentEntity;
import site.binghai.number2.Dynamic.DynamicEntity;
import site.binghai.number2.Dynamic.DynamicAdapter;
import site.binghai.number2.Dynamic.DynamicEntityLoader;
import site.binghai.number2.Dynamic.DynamicIndexGetCallback;
import site.binghai.number2.Dynamic.NetTalker;
import site.binghai.number2.Common.PostCommonComment;
import site.binghai.number2.Dynamic.PostNewDynamic;
import site.binghai.number2.LoginLogout.AfterLogOut;
import site.binghai.number2.LoginLogout.Login;
import site.binghai.number2.Messages.AfterRefreshFriendList;
import site.binghai.number2.Messages.ChatRoom;
import site.binghai.number2.Messages.Friend;
import site.binghai.number2.Messages.FriendListAdapter;
import site.binghai.number2.Messages.FriendTool;
import site.binghai.number2.Messages.MyFriends;
import site.binghai.number2.MyNetStateReceiver;
import site.binghai.number2.MyPagerAdapter;
import site.binghai.number2.Question.QuestionIndex;
import site.binghai.number2.R;
import site.binghai.number2.ShowYourLove.ShowYourLoveActivity;
import site.binghai.number2.TreeHole.TreeHoleActivity;
import site.binghai.number2.Utils.AfterUpload;
import site.binghai.number2.Utils.Config;
import site.binghai.number2.Utils.HttpUtil;
import site.binghai.number2.Utils.MessageDB;
import site.binghai.number2.Utils.PictureUtil;
import site.binghai.number2.Utils.ScreenUtil;
import site.binghai.number2.Utils.SetListViewHeight;
import site.binghai.number2.Utils.SignUtil;
import site.binghai.number2.Utils.ToastUtil;
import site.binghai.number2.Utils.VoicePlayer;
import site.binghai.number2.WebView.WebViewActivity;

public class MainActivity extends AppCompatActivity implements
        BGARefreshLayout.BGARefreshLayoutDelegate {

    private AdLoopView mLoopView = null;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private LayoutInflater mInflater;
    private List<String> mTitleList = new ArrayList<>();//页卡标题集合
    private View message_page, dynamic_page, index_page, my_page;//页卡视图
    private List<View> mViewList = new ArrayList<>();//页卡视图集合
    private FoldingCell fcell_in_my_paper;
    private LoopRotarySwitchView mLoopRotarySwitchView;
    private PullToRefreshView pull_to_refresh;
    private int cur_page = 0; // 当前页面
    private LoadToast loadToast;
    /**
     * 网络状态监听器
     */
    private MyNetStateReceiver myReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mViewPager = (ViewPager) findViewById(R.id.viewpager_view);
        mTabLayout = (TabLayout) findViewById(R.id.tabs);

        mInflater = LayoutInflater.from(this);

        message_page = mInflater.inflate(R.layout.message_chat_list, null);
        dynamic_page = mInflater.inflate(R.layout.dynamic_paper, null);
        index_page = mInflater.inflate(R.layout.new_index_view, null);
        my_page = mInflater.inflate(R.layout.my_paper_view, null);

        loadToast = new LoadToast(MainActivity.this);

        //添加页卡视图
        mViewList.add(message_page);
        mViewList.add(dynamic_page);
        mViewList.add(index_page);
        mViewList.add(my_page);

        //添加页卡标题
        mTitleList.add("消息");
        mTitleList.add("动态");
        mTitleList.add("首页");
        mTitleList.add("我的");

        mTabLayout.setTabMode(TabLayout.MODE_FIXED);//设置tab模式，当前为系统默认模式
        mTabLayout.addTab(mTabLayout.newTab().setText(mTitleList.get(0)));//添加tab选项卡
        mTabLayout.addTab(mTabLayout.newTab().setText(mTitleList.get(1)));//添加tab选项卡
        mTabLayout.addTab(mTabLayout.newTab().setText(mTitleList.get(2)));//添加tab选项卡
        mTabLayout.addTab(mTabLayout.newTab().setText(mTitleList.get(3)));//添加tab选项卡

        MyPagerAdapter mAdapter = new MyPagerAdapter(mViewList, mTitleList);
        mViewPager.setAdapter(mAdapter);//给ViewPager设置适配器
        mTabLayout.setupWithViewPager(mViewPager);//将TabLayout和ViewPager关联起来。
        mTabLayout.setTabsFromPagerAdapter(mAdapter);//给Tabs设置适配器

        InitAliSecurity();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.index, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        ArrayList<DialogMenuItem> ml = new ArrayList<>();
        ml.add(new DialogMenuItem("切换学校",R.mipmap.school));
        ml.add(new DialogMenuItem("添加好友",R.mipmap.friends));
        ml.add(new DialogMenuItem("发布新鲜",R.mipmap.sunny));
        ml.add(new DialogMenuItem("扫一扫",R.mipmap.camera));
        final NormalListDialog normalListDialog = new NormalListDialog(MainActivity.this,ml);
        normalListDialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        Toast.makeText(MainActivity.this,"切换学校",Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
                        intent.putExtra("url", Config.SearchFriend + "?" + SignUtil.CommonUserTokenSIGN(null));
                        startActivity(intent);
                        break;
                    case 2:
                        PostNewDynamic pd = new PostNewDynamic();
                        pd.setCallback(new ActivityCallback() {
                            @Override
                            public void onError(String msg) {
                                Toast.makeText(MainActivity.this,msg,Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onSuccess(String msg, Long dynamicId) {
                                dynamic_pull_tofresh.beginRefreshing();
                            }
                        });
                        pd.show(getSupportFragmentManager());
                        break;
                    case 3:
                        Toast.makeText(MainActivity.this,"扫一扫",Toast.LENGTH_SHORT).show();
                        break;
                }
                normalListDialog.dismiss();
            }
        });


        normalListDialog.title("(=@__@=)");
        normalListDialog.titleBgColor(ContextCompat.getColor(MainActivity.this, R.color.APPColor));
        normalListDialog.show();
        return true;
    }


    /**
     * 全局初始化
     */
    public void GlobalInitialization() {
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                freshPage(position);
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        InitIndexView();
        InitMyPaperView();
        InitDynamicPaper();
        InitMessagePage();
        /** 初始化ChatRoom MessageDB*/
        ChatRoom.messageDBInstance = messageDB;
        /** 注册MainActivity实例*/
        IMessageReceviver.mainActivity = this;
        /** 注册网络状态侦听器*/
        registerNetStateReceiver();
        /** 刷新好友列表*/
        FriendTool.RefreshFriendsList(this, new AfterRefreshFriendList() {
            @Override
            public void onFreshSuccess() {
                RefreshMessage(0);
            }

            @Override
            public void onFreshFailed(String msg) {
                new ToastUtil(MainActivity.this).errorNotice("抱歉", "因为网络原因，好友列表手动刷新失败，请手动重试。");
            }
        });
    }


    /**
     * 注册网络状态侦听器
     */
    private void registerNetStateReceiver() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        try {
            this.unregisterReceiver(myReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            this.registerReceiver(myReceiver, filter);
            Log.i("注册广播：", "完毕");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 初始化阿里安全
     */
    private void InitAliSecurity() {
        try {
            SecurityInit.Initialize(getApplicationContext());
            SignUtil.InitAliSecurity(getApplicationContext());
        } catch (JAQException e) {
            Log.e("阿里安全初始化失败", "errorCode =" + e.getErrorCode());
            new ToastUtil(this).errorNotice("安全错误", "您的手机存在不安全因素，无法运行。");
            finish();
        }
    }

    /**
     * 初始化消息界面
     */

    FloatingActionButton fbtn;
    SwipeMenuListView message_chatlist_listview;
    public static PullToRefreshView pull_to_refresh_message;

    private void InitMessagePage() {
        fbtn = (FloatingActionButton) message_page.findViewById(R.id.floadting_btn);
        message_chatlist_listview = (SwipeMenuListView) message_page.findViewById(R.id.message_chatlist_listview);
        pull_to_refresh_message = (PullToRefreshView) message_page.findViewById(R.id.pull_to_refresh_message);

        // set creator
        message_chatlist_listview.setMenuCreator(creator);
        message_chatlist_listview.setSwipeDirection(SwipeMenuListView.DIRECTION_RIGHT);
        message_chatlist_listview.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                return false;
            }
        });


        message_chatlist_listview.setOnTouchListener(new View.OnTouchListener() {
            int lastX;
            int lastY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                /**
                 * 拒绝父容器打断滑动事件
                 * */
                message_chatlist_listview.getParent().requestDisallowInterceptTouchEvent(true);
                int x = (int) event.getRawX();
                int y = (int) event.getRawY();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lastX = x;
                        lastY = y;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int deltaY = y - lastY;
                        int deltaX = x - lastX;
                        if (Math.abs(deltaX) < Math.abs(deltaY)) {
                            message_chatlist_listview.getParent().requestDisallowInterceptTouchEvent(false);
                        } else {
                            message_chatlist_listview.getParent().requestDisallowInterceptTouchEvent(true);
                        }
                        if (deltaX < 0)
                            message_chatlist_listview.getParent().requestDisallowInterceptTouchEvent(false);
                    default:
                        break;
                }
                return false;
            }
        });

        InitMessageAction();
    }

    /**
     * 初始化消息动作
     */
    private void InitMessageAction() {
        pull_to_refresh_message.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                RefreshMessage(1);
            }
        });
        /**
         * 打开联系人列表
         * */
        fbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MyFriends.class);
                startActivity(intent);
            }
        });

        message_chatlist_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /**
                 * 启动会话
                 * */
                Intent intent = null;
                intent = new Intent(MainActivity.this, ChatRoom.class);
                intent.putExtra("hid", String.valueOf(id));
                intent.putExtra("hname", friends.get(position).getName());
                intent.putExtra("havatar", friends.get(position).getAvatar());
                startActivity(intent);
            }
        });
        RefreshMessage(0);
    }


    private ArrayList<Friend> friends = new ArrayList<>();
    private FriendListAdapter friendsadapter;


    /**
     * 把不需要显示的好友剔除
     */
    private ArrayList<Friend> hideFriends(ArrayList<Friend> f) {
        ArrayList<Friend> res = new ArrayList<>();
        String configStr = AccountUtil.userid + "_friends_config";
        SharedPreferences sp = this.getSharedPreferences(configStr, MODE_PRIVATE);
        String hiden = sp.getString("hiden", "");

        for (int i = 0; i < f.size(); i++) {
            String str = String.valueOf(f.get(i).getFriendid());
            if (!hiden.contains(str))
                res.add(f.get(i));
        }
        return res;
    }


    private SwipeMenuCreator creator = new SwipeMenuCreator() {
        @Override
        public void create(SwipeMenu menu) {
            // create "delete" item
            SwipeMenuItem deleteItem = new SwipeMenuItem(
                    getApplicationContext());
            // set item background
            deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                    0x3F, 0x25)));
            // set item width
            deleteItem.setWidth(dp2px(90));
            // set a icon
            deleteItem.setIcon(R.drawable.ic_delete);
            // add to menu
            menu.addMenuItem(deleteItem);
        }
    };

    /**
     * 从数据库中拉取每个对话的最新消息显示到列表中
     */
    public void RefreshMessage(int OperatType) {
        new messageTask().execute(AccountUtil.userid);
        if (OperatType != 0) //手动调用
            HttpUtil.post(Config.APP_NET_CONNECT, SignUtil.CommonUserTokenSIGN(null), new JsonHttpResponseHandler());
    }


    /**
     * 刷新消息列表的子进程
     */
    private class messageTask extends AsyncTask<Long, Void, ArrayList<Friend>> {

        @Override
        protected ArrayList<Friend> doInBackground(Long... params) {
            Long uid = params[0];
            return messageDB.getChatingFriend(uid);
        }

        @Override
        protected void onPostExecute(ArrayList<Friend> f) {
            friends = hideFriends(f);
            friendsadapter = new FriendListAdapter(friends, MainActivity.this);
            message_chatlist_listview.setAdapter(friendsadapter);
            SetListViewHeight.set(message_chatlist_listview);
            pull_to_refresh_message.setRefreshing(false);
            friendsadapter.notifyDataSetChanged();
        }
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    /**
     * 刷新某一页的信息
     */
    private void freshPage(int position) {
        if (position != cur_page)
            switch (position == -1 ? cur_page : position) {
                case 0:
                    RefreshMessage(0);
                    break;
                case 1:
                    if (dynamicEntities.size() == 0)
                        dynamic_pull_tofresh.beginRefreshing();
//                    onBGARefreshLayoutBeginRefreshing(null);
                    break;
                case 2:
                    break;
                case 3:
                    RefreshMyPageData();
                    break;
            }
        if (position != -1)
            cur_page = position;
    }

    ListView dynamic_list_view;
    BGARefreshLayout dynamic_pull_tofresh;
    DynamicAdapter dAdapter;
    ArrayList<DynamicEntity> dynamicEntities = new ArrayList<>();

    private void InitDynamicPaper() {
        dynamic_list_view = (ListView) dynamic_page.findViewById(R.id.dynamic_list_view);
        dynamic_pull_tofresh = (BGARefreshLayout) dynamic_page.findViewById(R.id.dynamic_pull_tofresh);

        // 为BGARefreshLayout设置代理
        dynamic_pull_tofresh.setDelegate(this);
        // 设置下拉刷新和上拉加载更多的风格     参数1：应用程序上下文，参数2：是否具有上拉加载更多功能
        BGARefreshViewHolder refreshViewHolder = new BGANormalRefreshViewHolder(this, true);
        // 设置下拉刷新和上拉加载更多的风格
        dynamic_pull_tofresh.setRefreshViewHolder(refreshViewHolder);
        dynamic_pull_tofresh.setIsShowLoadingMoreView(true);

        dAdapter = new DynamicAdapter(dynamicEntities, this, new DynamicActionCallBack() {
            @Override
            public void postComment(Long dynamicId) {
                post_comment_for_dynamic(dynamicId, null, null, Comment.FOR_DYNAMIC);
            }

            @Override
            public void DataChanged() {
                dynamicHandler.sendMessage(new Message());
            }

            @Override
            public void postReplyComment(Long dynamicId, Long replyid, String replyname) {
                post_comment_for_dynamic(dynamicId, replyid, replyname,Comment.FOR_DYNAMIC);
            }

            @Override
            public void playVoiceCall(final String url) {
                new VoicePlayer(url,MainActivity.this).play();
            }


        });
        dynamic_list_view.setAdapter(dAdapter);
//        onBGARefreshLayoutBeginRefreshing(dynamic_pull_tofresh);
    }

    private List<Long> dynamicIndexs = new ArrayList<>();

    /**
     * 刷新动态
     */
    @Override
    public void onBGARefreshLayoutBeginRefreshing(final BGARefreshLayout refreshLayout) {
        new NetTalker(MainActivity.this, new DynamicIndexGetCallback() {
            @Override
            public void indexDownloadComplete(ArrayList<Long> indexs) {
                dynamicIndexs.clear();
                dynamicIndexs.addAll(indexs);
                dynamicEntities.clear();
                dynamicHandler.handleMessage(new Message());
                NextDynamicPage();
            }

            @Override
            public void downloadFailed() {
                dynamic_pull_tofresh.endRefreshing();
            }
        }, null).getDynamicIndexs();
    }

    /**
     * 加载下一页数据
     */
    private void NextDynamicPage() {
        /** 确定步长*/
        int step = dynamicIndexs.size() > 9 ? 9 : dynamicIndexs.size();
        if (dynamicIndexs.size() == 0) {
            Toast.makeText(this, "没有更多了", Toast.LENGTH_SHORT).show();
            dynamic_pull_tofresh.endRefreshing();
            dynamic_pull_tofresh.endLoadingMore();
            return;
        }

        /** 加载数据*/
        new NetTalker(this, null, new DynamicEntityLoader() {
            @Override
            public void loadDynamicData(ArrayList<DynamicContentEntity> entities) {
                dynamicEntities.addAll(entities);
                dynamicHandler.sendMessage(new Message());
                dynamic_pull_tofresh.endRefreshing();
                dynamic_pull_tofresh.endLoadingMore();
            }

            @Override
            public void loadFailed() {
                dynamic_pull_tofresh.endRefreshing();
                dynamic_pull_tofresh.endLoadingMore();
            }
        }).getDynamicEntities(dynamicIndexs.subList(0, step));
        dynamicIndexs = dynamicIndexs.subList(step, dynamicIndexs.size());
    }

    /**
     * 下载更多, 返回true时显示正在加载
     */
    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        if (dynamicIndexs.size() == 0) {
            dynamic_pull_tofresh.endRefreshing();
            dynamic_pull_tofresh.endLoadingMore();
            StartPostNewDynamic("英雄留步！前面已经没有动态了，不如自己发一条吧！");
            return false;
        } else
            NextDynamicPage();
        return true;
    }

    /**
     * 发布评论时拉起的输入框
     */
    private void post_comment_for_dynamic(Long entityId, Long replyid, String replyname,int etype) {
        PostCommonComment pcfd = new PostCommonComment();
        pcfd.InitFirst(entityId,replyid,replyname,etype,new ActivityCallback() {
            @Override
            public void onError(String msg) {
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onSuccess(String msg, Long dynamicId) {
//                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                NotifyDynamicChangedById(dynamicId);
            }
        });
        pcfd.show(getSupportFragmentManager());
    }

    /**
     * 通知该动态内容变化了
     */
    private void NotifyDynamicChangedById(final Long dynamicId) {
        ArrayList<Long> tmp = new ArrayList<>();
        tmp.add(dynamicId);
        new NetTalker(this, null, new DynamicEntityLoader() {
            @Override
            public void loadDynamicData(ArrayList<DynamicContentEntity> entities) {
                for (int i = 0; i < dynamicEntities.size(); i++) {
                    if (dynamicEntities.get(i).getId() == dynamicId) {
                        dynamicEntities.set(i, entities.get(0));
                        Message msg = new Message();
                        dynamicHandler.sendMessage(msg);
                        break;
                    }
                }
            }

            @Override
            public void loadFailed() {

            }
        }).getDynamicEntities(tmp);
    }

    private Handler dynamicHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            dAdapter.notifyDataSetChanged();
            super.handleMessage(msg);
        }
    };
    private Button my_btn_fans;
    private Button my_btn_order_state;
    private Button my_btn_leavemsg;
    private Button my_btn_aboutUs;
    private Button my_btn_quit;
    private ImageView closed_avatar;
    private TextView closed_nick_name;
    private ImageView edit_myinfo;
    private ImageView my_income;

    /**
     * 刷新“我的”页面中的数据
     */
    private void RefreshMyPageData() {

        if (closed_nick_name != null)
            closed_nick_name.setText(AccountUtil.nick_name);
        if (closed_avatar != null) {
            Glide.with(this)
                    .load(AccountUtil.my_avatar)
                    .centerCrop()
                    .placeholder(R.drawable.default_avatar)
                    .into(closed_avatar);
        }
        if (opened_avatar != null) {
            Glide.with(this)
                    .load(AccountUtil.my_avatar)
                    .centerCrop()
                    .placeholder(R.drawable.default_avatar)
                    .into(opened_avatar);
        }
    }

    MessageDB messageDB = new MessageDB(MainActivity.this, "MessageDatabase", null, 1);

    private void InitMyViewActions() {

        /**
         * 系统消息监听器
         * */
        my_sys_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;
                intent = new Intent(MainActivity.this, WebViewActivity.class);
                intent.putExtra("url", Config.SystemMsgServer + "?" + SignUtil.CommonUserTokenSIGN(null));
                startActivity(intent);
            }
        });
        /**
         * 我的粉丝 监听器
         * */
        my_btn_fans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File sd = Environment.getExternalStorageDirectory();
                new ToastUtil(MainActivity.this).unDefinedErrro();
            }
        });

        /**
         * 退出登录监听器
         * */
        my_btn_quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccountUtil.LogOut(MainActivity.this, new AfterLogOut() {
                    @Override
                    public void afterLogOut() {
                        freshPage(cur_page);
                        startActivity(new Intent(MainActivity.this, Login.class));
                    }
                });
            }
        });

        /**
         * 点击折叠状态的头像
         * */
        opened_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AccountUtil.login_state)
                    startActivity(new Intent(MainActivity.this, Login.class));
                else
                    startActivity(new Intent(MainActivity.this, ChangeAvatar.class));
            }
        });

        /**
         * 点击展开状态无内容区域
         * */
        fcell_in_my_paper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fcell_in_my_paper.toggle(false);
            }
        });

        /** 修改资料*/
        edit_myinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
                intent.putExtra("url", Config.editMyInfo + "?" + SignUtil.CommonUserTokenSIGN(null));
                startActivity(intent);
            }
        });

        /** 我的收入*/
        my_income.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
                intent.putExtra("url", Config.my_incomes + "?" + SignUtil.CommonUserTokenSIGN(null));
                startActivity(intent);
            }
        });

        my_btn_leavemsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
                intent.putExtra("url", Config.suggestion);
                startActivity(intent);
            }
        });
    }


    private ImageView opened_avatar;
    private ImageView my_sys_msg;

    private void InitMyPaperView() {
        my_sys_msg = (ImageView) my_page.findViewById(R.id.my_sys_msg);
        fcell_in_my_paper = (FoldingCell) my_page.findViewById(R.id.folding_cell);
        closed_avatar = (ImageView) my_page.findViewById(R.id.closed_avatar);
        closed_nick_name = (TextView) my_page.findViewById(R.id.closed_nick_name);
        opened_avatar = (ImageView) my_page.findViewById(R.id.opened_avatar);
        edit_myinfo = (ImageView) my_page.findViewById(R.id.edit_myinfo);
        my_income = (ImageView) my_page.findViewById(R.id.my_income);

        fcell_in_my_paper.initialize(1000, R.color.APPColor, 1);
        InitButtonList();
        InitMyViewActions();
    }


    private ImageView iv_show_your_love;
    private ImageView tree_hole_img;

    private void InitIndexView() {
        tree_hole_img = (ImageView) index_page.findViewById(R.id.tree_hole_img);
        iv_show_your_love = (ImageView) index_page.findViewById(R.id.show_your_love);
        pull_to_refresh = (PullToRefreshView) index_page.findViewById(R.id.pull_to_refresh);
        pull_to_refresh.setScrollContainer(true);
        pull_to_refresh.setVisibility(View.VISIBLE);
        mLoopRotarySwitchView = (LoopRotarySwitchView) index_page.findViewById(R.id.mLoopRotarySwitchView);
        mLoopRotarySwitchView
                .setR(getSuitableR())//设置R的大小
                .setAutoRotation(false)//是否自动切换
                .setAutoScrollDirection(LoopRotarySwitchView.AutoScrollDirection.right)//切换方向
                .setAutoRotationTime(2000);//自动切换的时间  单位毫秒


        mLoopView = (AdLoopView) index_page.findViewById(R.id.main_act_adloopview);

        LoadTreeHoldImgAndShowYourLoveImg();
        InitNiurenByNet();
        setLoopViewByNet();
        InitPulltoRefresh();
        InitShowYourLove();
        InitTreeHole();
    }

    /**
     * 更新树洞和表白墙的图片
     */
    private void LoadTreeHoldImgAndShowYourLoveImg() {
        Glide.with(this)
                .load(Config.UpdateShowYourLoveFace)
                .centerCrop()
                .into(iv_show_your_love);
        Glide.with(this)
                .load(Config.UpdateTreeHoleFace)
                .centerCrop()
                .into(tree_hole_img);
    }

    private void InitTreeHole() {
        tree_hole_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, TreeHoleActivity.class);
//                startActivity(intent);
                Intent itent = new Intent(MainActivity.this,WebViewActivity.class);
                itent.putExtra("url","http://118.192.140.147/demo.html");
                startActivity(itent);
            }
        });
    }

    private void InitShowYourLove() {
        iv_show_your_love.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ShowYourLoveActivity.class);
                startActivity(intent);
            }
        });
    }

    private void InitPulltoRefresh() {
        pull_to_refresh.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadToast.setTranslationY(200);
                loadToast.setText("正在刷新...");
                loadToast.show();
                setLoopViewByNet();
            }
        });
    }

    /**
     * 下载牛人画廊
     */
    private void InitNiurenByNet() {
        HttpUtil.get(Config.niuren + Config.randomSign(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                ArrayList<String> urls = new ArrayList<String>();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        urls.add(response.getJSONObject(i).getString("url"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                InitNiuren(urls);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(MainActivity.this, "网络异常...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Toast.makeText(MainActivity.this, "网络异常...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(MainActivity.this, "网络异常...", Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * 更新牛人界面
     */
    private void InitNiuren(ArrayList<String> urls) {
        NiurenViewMaker maker = new NiurenViewMaker(urls, MainActivity.this);
        for (int i = 0; i < maker.getSize(); i++) {
            mLoopRotarySwitchView.addView(maker.getView(i));
        }
    }

    private float getSuitableR() {
        int W = ScreenUtil.getScreenWidth(this);
        float res = (float) (W * 0.74);
        Log.i("适配的宽度是:", String.valueOf(res));
        return res;
    }


    /**
     * 是否进行了全局初始化
     */
    private boolean globalInit = false;

    @Override
    protected void onResume() {
        super.onResume();

        if (!AccountUtil.login_state && Login.exitApp) {
            Login.exitApp = false;
            finish();
        } else if (!AccountUtil.login_state) { /** 如果未登录，则进行登陆逻辑*/
            globalInit = false;
            Intent intent = new Intent(MainActivity.this, Login.class);
            startActivity(intent);
        } else if (!globalInit) { /** 如果已经登录，但未进行全局初始化，则进行全局初始化*/
            GlobalInitialization();
            globalInit = true;
        } else { /** 已经登录且完成了全局初始化，则刷新某一页*/
            freshPage(-1);
        }

        mTempDir = new File(Environment.getExternalStorageDirectory(), "Temp");
        if (!mTempDir.exists()) {
            mTempDir.mkdirs();
        }

    }


    private void setLoopViewByNet() {
        HttpUtil.post(Config.IndexSlide,SignUtil.CommonUserTokenSIGN(null),new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                setLoopView(response.toString());
                pull_to_refresh.setRefreshing(false);
                loadToast.success();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(MainActivity.this, "网络链接异常...", Toast.LENGTH_SHORT).show();
                pull_to_refresh.setRefreshing(false);
                loadToast.error();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(MainActivity.this, "网络链接异常...", Toast.LENGTH_SHORT).show();
                pull_to_refresh.setRefreshing(false);
                loadToast.error();
            }
        });
    }


    private void setLoopView(String json) {


        // Use JsonTool to parse JSON data to entity
        LoopData loopData = JsonTool.toBean(json, LoopData.class);
        // set AdLoopView date use entity
        mLoopView.refreshData(loopData);
        // begin to loop
        mLoopView.startAutoLoop();

        mLoopView.setOnClickListener(new BaseLoopAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(PagerAdapter parent, View view,
                                    int position, int realPosition) {
                startActivity(new Intent(MainActivity.this, QuestionIndex.class));
            }
        });
    }


    private void InitButtonList() {
        my_btn_fans = (Button) my_page.findViewById(R.id.my_btn_fans);
        Drawable FansPic = getResources().getDrawable(R.drawable.fans);
        FansPic.setBounds(15, 0, 115, 100);//第一0是距左边距离，第二0是距上边距离，40分别是长宽
        my_btn_fans.setCompoundDrawables(FansPic, null, null, null);//只放左边


        my_btn_order_state = (Button) my_page.findViewById(R.id.my_btn_order_state);
        Drawable OrderStatePic = getResources().getDrawable(R.drawable.order_state);
        OrderStatePic.setBounds(15, 0, 115, 100);//第一0是距左边距离，第二0是距上边距离，40分别是长宽
        my_btn_order_state.setCompoundDrawables(OrderStatePic, null, null, null);//只放左边

        my_btn_leavemsg = (Button) my_page.findViewById(R.id.my_btn_leavemsg);
        Drawable LeaveMSG = getResources().getDrawable(R.drawable.leave_msg);
        LeaveMSG.setBounds(15, 0, 115, 100);//第一0是距左边距离，第二0是距上边距离，40分别是长宽
        my_btn_leavemsg.setCompoundDrawables(LeaveMSG, null, null, null);//只放左边

        my_btn_aboutUs = (Button) my_page.findViewById(R.id.my_btn_aboutUs);
        Drawable AboutUS = getResources().getDrawable(R.drawable.about_us);
        AboutUS.setBounds(15, 0, 115, 100);//第一0是距左边距离，第二0是距上边距离，40分别是长宽
        my_btn_aboutUs.setCompoundDrawables(AboutUS, null, null, null);//只放左边

        my_btn_quit = (Button) my_page.findViewById(R.id.my_btn_quit);
        Drawable Quit = getResources().getDrawable(R.drawable.quit);
        Quit.setBounds(15, 0, 115, 100);//第一0是距左边距离，第二0是距上边距离，40分别是长宽
        my_btn_quit.setCompoundDrawables(Quit, null, null, null);//只放左边
    }

    @Override
    protected void onDestroy() {
        System.gc();
        try {
            unregisterReceiver(myReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    private void StartPostNewDynamic(String hint) {
        PostNewDynamic postNewDynamic = new PostNewDynamic();
        if (hint != null)
            postNewDynamic.setHint(hint);
        postNewDynamic.setCallback(new ActivityCallback() {
            @Override
            public void onError(String msg) {
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(String msg, Long dynamicId) {
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                freshPage(-1);
            }
        });
        postNewDynamic.show(getSupportFragmentManager());
    }

    /**
     * 发布动态使用的参数
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent result) {
        super.onActivityResult(requestCode, resultCode, result);
        Log.i("onActivityResult", requestCode + ":" + resultCode);
        if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
            post_new_dynamic_pic_selected = true;
            beginCrop(result.getData());
        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, result);
        } else //if (requestCode == REQUEST_CODE_CAPTURE_CAMEIA) {
        {
            if (mCurrentPhotoPath != null) {
                post_new_dynamic_pic_selected = true;
//                File __f = PictureUtil.compression(mCurrentPhotoPath);
                File __f = new File(mCurrentPhotoPath);
                beginCrop(Uri.fromFile(__f));
            } else
                Log.e("PhotoPathError", "mCurrentPhotoPath Is null");
        }
    }

    private void beginCrop(Uri data) {
        String fileName = "Temp_" + String.valueOf(System.currentTimeMillis());
        File cropFile = new File(mTempDir, fileName);
        Uri outputUri = Uri.fromFile(cropFile);
//        new Crop(data).output(outputUri).setCropType(true).start(this);

        new Crop(PictureUtil.compression(getApplicationContext(), data)).
                output(outputUri).withAspect(1, 1).start(this);
    }

    SweetAlertDialog pDialog;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (pDialog != null) pDialog.dismiss();
        }
    };

    public static String post_new_dynamic_picture_state = null;
    public static boolean post_new_dynamic_pic_selected = false;

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            Uri _uri = Crop.getOutput(result);
            String _tName = System.currentTimeMillis() + "_png_avatar";
            /**万象优图上传*/
            pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setTitleText("图片正在上传...");
            pDialog.setCancelable(false);
            pDialog.show();
            try {
                final String pngPath = PictureUtil.saveAsPng(this, _uri, _tName);
                if (pngPath != null) {
                    PictureUtil.uploadPic(pngPath, this, new AfterUpload() {
                        @Override
                        public void afterUpload(String url) {
                            handler.handleMessage(new Message());
                            if (url != null) {
                                /** 更新本地，并保存链接到服务器*/
                                post_new_dynamic_picture_state = url.toString();
                            }
                        }

                        @Override
                        public void uploadProgress(long total, long send) {

                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (resultCode == Crop.RESULT_ERROR) {
        }
    }
}
