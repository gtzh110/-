package com.yuzhi.fine.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yuzhi.fine.R;
import com.yuzhi.fine.activity.BaseActivity;
import com.yuzhi.fine.activity.MapActivity;
import com.yuzhi.fine.model.Order;
import com.yuzhi.fine.model.SearchParam;
import com.yuzhi.fine.model.User;
import com.yuzhi.fine.ui.UIHelper;
import com.yuzhi.fine.ui.pulltorefresh.PullToRefreshBase;
import com.yuzhi.fine.ui.pulltorefresh.PullToRefreshListView;
import com.yuzhi.fine.ui.pulltorefresh.extras.SoundPullEventListener;
import com.yuzhi.fine.ui.quickadapter.BaseAdapterHelper;
import com.yuzhi.fine.ui.quickadapter.QuickAdapter;
import com.yuzhi.fine.utils.LogUtils;
import com.yuzhi.fine.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetServerTimeListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by lemon on 2016/4/8.
 */
public class UndoneOrderFragment extends Fragment {
    private Activity context;

    private SearchParam param;
    private int pno = 1;
    private boolean isLoadAll;
    private User currentUser;
    private List<Order> orderList;
    private int limit = 50;
    private int curPage = 0;
    private static final int STATE_REFRESH = 0;// 下拉刷新
    private static final int STATE_MORE = 1;// 加载更多
    String times;

    public TextView status;
    View no_result;
    @Bind(R.id.listView)
    PullToRefreshListView listView;
    QuickAdapter<Order> adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recommend_shop_list, container, false);
        ButterKnife.bind(this, view);
        view.findViewById(R.id.top_sort_and_screen).setVisibility(View.GONE);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
        initView();
        loadData(0, STATE_REFRESH, 0);
    }

    void initView() {

        adapter = new QuickAdapter<Order>(context, R.layout.expense_account_list_item) {
            @Override
            protected void convert(BaseAdapterHelper helper, Order order) {
                helper.setText(R.id.create_time, "下单时间：" + order.getCreatedAt())
                        .setText(R.id.address, "地址：" + order.getAddress())
                        .setText(R.id.name, "客户：" + order.getName())
                        .setText(R.id.phone, "客户手机号：" + order.getPhoneNumber());
                if (order.getWorker() != null) {
                    helper.setText(R.id.worker, "工人：" + order.getWorker().getName()).setText(R.id.worker_phone, "工人手机号：" + order.getWorker().getPhoneNumber());
                } else {
                    helper.setText(R.id.worker, "工人：未指定");
                }
                setStatus(order, (TextView) helper.getView(R.id.status));
            }
        };

        listView.withLoadMoreView();
        //添加音效
        SoundPullEventListener<ListView> soundPullEventListener=new SoundPullEventListener<ListView>(getActivity());
        soundPullEventListener.addSoundEvent(PullToRefreshBase.State.PULL_TO_REFRESH, R.raw.pull_to_refresh);
        listView.setOnPullEventListener(soundPullEventListener);
        listView.setAdapter(adapter);
        // 下拉刷新
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                loadData(0, STATE_REFRESH, 0);
            }
        });
        // 加载更多
        listView.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {
                loadData(curPage, STATE_MORE, 0);
            }
        });
        // 点击事件
        listView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                orderList = adapter.getData();
                if (currentUser.getIsCustomer()) {
                    if (orderList.size() > 0 && (i - 1) != orderList.size()) {
                        final Order order = orderList.get(i - 1);
                        final User worker = order.getWorker();
                        if ((i - 1) != orderList.size() && orderList != null) {
                            if (worker != null) {
                                Utils.dialogWithActions(context, "提示", "您确定该订单已经完成了吗？", "确定", "取消", null, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
//                                        getTime(order);
                                        Bmob.getServerTime(context, new GetServerTimeListener() {
                                            @Override
                                            public void onSuccess(long time) {
                                                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                                                times = formatter.format(new Date(time * 1000L));
                                                LogUtils.e("TIME", times);
                                                order.setEndDate(times);
                                                order.setIsComplete(true);
                                                order.update(context, order.getObjectId(), new UpdateListener() {
                                                    @Override
                                                    public void onSuccess() {
                                                        loadData(0, STATE_REFRESH, 0);
                                                        BaseActivity.toast(context, "订单已完成，请评价");
                                                    }

                                                    @Override
                                                    public void onFailure(int i, String s) {
                                                        BaseActivity.toast(context, s);
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onFailure(int code, String msg) {
                                            }
                                        });

                                    }
                                });
                            } else {
                                Utils.dialogWithConfirmAction(context, "提示", "您还未指定工人，请在工人列表中选择一个工人", "确定", null);
                            }
                        }
                    }
                } else {
                    if (orderList.size() > 0 && (i - 1) != orderList.size()) {
                        Intent intent = new Intent(getActivity(), MapActivity.class);
                        Order order = orderList.get(i - 1);
                        intent.putExtra("address", order.getAddress());
                        getActivity().startActivity(intent);
                    }
                }

            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                    Picasso.with(context).pauseTag(context);
                } else {
                    Picasso.with(context).resumeTag(context);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
    }


    private void loadData(final int page, final int actionType, int index) {
//       进度条+正在加载
        listView.setLoadMoreViewTextLoading();
        //查询工人列表
        currentUser = User.getCurrentUser(context, User.class);

        BmobQuery<Order> query = new BmobQuery<Order>();
        if (currentUser.getIsCustomer()) {
            query.addWhereEqualTo("customer", currentUser);
        } else {
            query.addWhereEqualTo("worker", currentUser);
        }

        query.addWhereEqualTo("isComplete", false);
        query.order("-updatedAt");
        //查询订单时 返回工人的信息
        query.include("worker");
        //如果不加上这条语句，默认返回10条数据
        query.setLimit(limit);

        if (actionType == STATE_MORE) {
            query.setSkip(page * limit);
        } else {
            query.setSkip(0);
        }
        query.findObjects(context, new FindListener<Order>() {
            @Override
            public void onSuccess(List<Order> list) {
                listView.onRefreshComplete();
                listView.updateLoadMoreViewText(list);
                if (actionType == STATE_REFRESH) {
                    curPage = 0;
                    adapter.clear();
                }
                adapter.addAll(list);
                curPage++;
            }

            @Override
            public void onError(int code, String msg) {
                // TODO Auto-generated method stub
                BaseActivity.toast(context, "查询工人列表失败:" + msg);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Picasso.with(context).resumeTag(context);
    }

    @Override
    public void onPause() {
        super.onPause();
        Picasso.with(context).pauseTag(context);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Picasso.with(context).cancelTag(context);
    }

    public void setStatus(Order order, TextView status) {
        User worker = order.getWorker();
        Boolean isCompleted = order.getIsComplete();
        if (worker == null) {
            status.setTextColor(Color.parseColor("#FFb2bdc5"));
            status.setBackgroundResource(R.drawable.expense_account_status_grey_background);
            status.setText("未选人");
        } else if (!isCompleted) {
            status.setTextColor(Color.parseColor("#ffff0f0f"));
            status.setBackgroundResource(R.drawable.expense_account_status_red_background);
            status.setText("施工中");
        } else {
            status.setTextColor(Color.parseColor("#FF80d283"));
            status.setBackgroundResource(R.drawable.expense_account_status_green_background);
            status.setText("待评价");
        }

    }


}
