package site.binghai.number2.ShowYourLove;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import com.soundcloud.android.crop.Crop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import cn.bingoogolapple.refreshlayout.BGARefreshViewHolder;
import cn.pedant.SweetAlert.SweetAlertDialog;
import site.binghai.number2.AccountUtil;
import site.binghai.number2.Common.CommentOpt;
import site.binghai.number2.Common.UpdateCallBack;
import site.binghai.number2.Index.ActivityCallback;
import site.binghai.number2.R;
import site.binghai.number2.Utils.AfterDownload;
import site.binghai.number2.Utils.AfterUpload;
import site.binghai.number2.Utils.PictureUtil;
import site.binghai.number2.Utils.SetListViewHeight;
import site.binghai.number2.Utils.ToastUtil;

public class ShowYourLoveActivity extends AppCompatActivity implements BGARefreshLayout.BGARefreshLayoutDelegate{

    private static String TAG = "MyCard";
    private ListView love_entity_list;
    private LoveAdapter loveAdapter;
    private ArrayList<ShowYourLoveEntity>entities;
    private BGARefreshLayout mRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_your_love);

        setTitle(AccountUtil.school+"表白墙");

        initRefreshLayout();
        initUI();
        intiData();
    }

    private void initRefreshLayout() {
        mRefreshLayout = (BGARefreshLayout) findViewById(R.id.love_refresh);
        // 为BGARefreshLayout设置代理
        mRefreshLayout.setDelegate(this);
        // 设置下拉刷新和上拉加载更多的风格     参数1：应用程序上下文，参数2：是否具有上拉加载更多功能
        BGARefreshViewHolder refreshViewHolder = new BGANormalRefreshViewHolder(this, true);
        // 设置下拉刷新和上拉加载更多的风格
        mRefreshLayout.setRefreshViewHolder(refreshViewHolder);
        mRefreshLayout.setIsShowLoadingMoreView(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.only_add, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.actionBar_add:
                StartPostNew();
                return true;
        }
        return true;
    }

    private void intiData() {
        entities = new ArrayList<>();
        loveAdapter = new LoveAdapter(entities,ShowYourLoveActivity.this);
        loveAdapter.setUpdateCallBack(new UpdateCallBack() {
            @Override
            public void onUpdate(Object info) {
                Long eid = (Long) info;
                for (int i = 0;i < entities.size();i++){
                    if(entities.get(i).getId()==eid){
                        entities.get(i).setIlike(true);
                        entities.get(i).setGoodNumber(entities.get(i).getGoodNumber()+1);
                        break;
                    }
                }
                loveAdapter.notifyDataSetChanged();
            }
        });
        love_entity_list.setAdapter(loveAdapter);
        love_entity_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ShowYourLoveActivity.this,ShowYourLoveDetailActivity.class);
                intent.putExtra("obj",entities.get(position));
                startActivity(intent);
            }
        });
        love_entity_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                if(entities.get(position).getPosterId().equals(AccountUtil.userid)){
                    new ToastUtil(ShowYourLoveActivity.this)
                            .warningNotice("删除",
                                    "你确定要删除这条表白吗?",
                                    "我点错了",
                                    "是的，删除",
                                    "已删除",
                                    "删除成功！",
                                    "好的",
                                    new UpdateCallBack() {
                                        @Override
                                        public void onUpdate(Object info) {
                                            new CommentOpt().DeleteEntity(entities.get(position).getId(),"showyourlove");
                                            entities.remove(position);
                                            loveAdapter.notifyDataSetChanged();
                                        }
                                    });
                }else{
                    new ToastUtil(ShowYourLoveActivity.this)
                            .warningNotice("举报",
                                    "你确定要举报这条表白吗?",
                                    "我点错了",
                                    "我要举报",
                                    "结果",
                                    "举报成功，我们处理之后将会告知您处理结果，感谢您自觉维护Time环境！",
                                    "好的",
                                    new UpdateCallBack() {
                                        @Override
                                        public void onUpdate(Object info) {
                                            new CommentOpt().Report_JuBao(entities.get(position).getId(),"showyourlove",entities.get(position).getText());
                                            entities.remove(position);
                                            loveAdapter.notifyDataSetChanged();
                                        }
                                    });
                }

                return true;
            }
        });
        mRefreshLayout.beginRefreshing();
    }

    private void initUI() {
        love_entity_list = (ListView) findViewById(R.id.love_entity_list);
    }

    /**
     * 发布新的
     */
    private void StartPostNew() {
        startActivity(new Intent(ShowYourLoveActivity.this,PostYourLoveCard.class));
    }

    private Handler hander = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mRefreshLayout.beginRefreshing();
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        //hander.sendMessage(new Message());
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        entities.clear();
        nextData();
    }

    private void nextData(){
        new LoveTalker(new AfterDownload() {
            @Override
            public void afterDownLoad(Object info) {
                if(info != null){
                    ArrayList<ShowYourLoveEntity> data = (ArrayList<ShowYourLoveEntity>) info;
                    if(data.size() == 0){
                        Toast.makeText(ShowYourLoveActivity.this,"已经显示了全部",Toast.LENGTH_SHORT).show();
                    }else{
                        entities.addAll(data);
                        loveAdapter.notifyDataSetChanged();
                    }
                }
                mRefreshLayout.endLoadingMore();
                mRefreshLayout.endRefreshing();
            }
            @Override
            public void afterDownload(boolean result) {
                mRefreshLayout.endLoadingMore();
                mRefreshLayout.endRefreshing();
            }
            @Override
            public void downloadProgress(String url, long totalSize, float progress) {}
        }).next(entities.size()>0?entities.get(entities.size()-1).getPostTime()-1:System.currentTimeMillis(),10);
    }
    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        nextData();
        return true;
    }
}
