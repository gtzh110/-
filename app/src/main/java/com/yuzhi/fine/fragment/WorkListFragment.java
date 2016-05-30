package com.yuzhi.fine.fragment;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yuzhi.fine.R;
import com.yuzhi.fine.activity.BaseActivity;
import com.yuzhi.fine.model.AppliedWorker;
import com.yuzhi.fine.model.Order;
import com.yuzhi.fine.model.SearchParam;
import com.yuzhi.fine.model.User;
import com.yuzhi.fine.ui.pulltorefresh.PullToRefreshBase;
import com.yuzhi.fine.ui.pulltorefresh.PullToRefreshListView;
import com.yuzhi.fine.ui.quickadapter.BaseAdapterHelper;
import com.yuzhi.fine.ui.quickadapter.QuickAdapter;
import com.yuzhi.fine.utils.LogUtils;
import com.yuzhi.fine.utils.SharedPreferences;
import com.yuzhi.fine.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;


public class WorkListFragment extends Fragment {

    private Activity context;
    private boolean isLoadAll = false;
    //    private View sort;
    private LinearLayout sort_layout;
    protected LinearLayout sort_and_filter_scroll, sort_area;
    protected TextView sort_text;
    protected ImageView sort_image;
    ImageView sort_arrow;
    RotateAnimation rotateAnimation;
    protected int sort = 0;
    private static final int STATE_REFRESH = 0;// 下拉刷新
    private static final int STATE_MORE = 1;// 加载更多
    private int limit = 50;
    private int curPage = 0;
//    private SharedPreferences sp;
//    private int mIndex = 0;

    View view;

    @Bind(R.id.listView)
    PullToRefreshListView listView;
    QuickAdapter<AppliedWorker> adapter;
    private int pno;//当前页码编号？
    SearchParam param;
    List<AppliedWorker> workList;
    User currentUser;
    Order myOrder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.recommend_shop_list, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
        currentUser = User.getCurrentUser(context, User.class);
//        initData();
        initView();
        loadData(0, STATE_REFRESH, sort);
    }

    void initView() {
        rotateAnimation = new RotateAnimation(-180, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(300);
        rotateAnimation.setFillAfter(true);
        initSort();
        adapter = new QuickAdapter<AppliedWorker>(context, R.layout.recommend_shop_list_item) {
            @Override
            protected void convert(BaseAdapterHelper helper, AppliedWorker user) {
                helper.setText(R.id.name, user.getName())
                        .setText(R.id.level, "等级 " + String.valueOf(user.getLevel())).
                        setText(R.id.score, "评分 " + user.getScore()).
                        setText(R.id.introduction, "自我介绍：" + user.getWorker().getSelfIntroduce());
//                        .setImageUrl(R.id.logo, user.getSelfIntroduce()); // 自动异步加载图片

            }
        };

        listView.withLoadMoreView();
        listView.setAdapter(adapter);
        // 下拉刷新
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
//                initData();
                loadData(0, STATE_REFRESH, sort);

            }
        });
        // 加载更多
        listView.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {
                loadData(curPage, STATE_MORE, sort);
            }
        });
        // 点击事件
        listView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                if (currentUser.getIsCustomer()) {
                    workList = adapter.getData();
                    if ((i - 1) != workList.size() && workList != null) {
                        Utils.dialogWithActions(context, "选择工人", "您确定要选择 " + workList.get(i - 1).getName(), "确定", "取消", null, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
//                        if (workList.size()!=)
                                final AppliedWorker worker = workList.get(i - 1);
                                if (myOrder != null && myOrder.getWorker() == null) {
                                    myOrder.setWorker(worker.getWorker());
                                    myOrder.update(context, myOrder.getObjectId(), new UpdateListener() {
                                        @Override
                                        public void onSuccess() {
                                            BaseActivity.toast(context, "已选择工人，订单已生效");
                                            myOrder = null;
                                            AppliedWorker deleteWorker = new AppliedWorker();
                                            deleteWorker.setObjectId(worker.getObjectId());
                                            deleteWorker.delete(context, new DeleteListener() {
                                                @Override
                                                public void onSuccess() {
                                                    LogUtils.e("log", "工人已从列表中移除");
//                                                sp = SharedPreferences.getInstance();
//                                                sp.putBoolean("ISAPPLIED",false);
                                                }

                                                @Override
                                                public void onFailure(int i, String s) {
                                                    LogUtils.e("log", s);
                                                }
                                            });
                                        }

                                        @Override
                                        public void onFailure(int i, String s) {
                                            BaseActivity.toast(context, "工人选择失败");
                                        }
                                    });
                                } else if (myOrder != null && myOrder.getWorker() != null) {
                                    BaseActivity.toast(context, "已经选择工人，不能更改！");
                                } else {
                                    BaseActivity.toast(context, "尚未下单，不能选择工人！");
                                }

                            }
                        });
                    }
                } else {

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

   /* private void initData() {
//        param = new SearchParam();
        pno = 1;
        isLoadAll = false;
    }*/

    private void loadData(final int page, final int actionType, int index) {
//        if (isLoadAll) {
//
//            return;
//        }
//       进度条+正在加载
        listView.setLoadMoreViewTextLoading();
        //查询工人列表
       /* BmobQuery<User> query = new BmobQuery<User>();
        query.addWhereEqualTo("isApply", true);
        query.addWhereEqualTo("isOccupied", false);*/
        BmobQuery<AppliedWorker> query = new BmobQuery<AppliedWorker>();
        if (index == 0) {
            query.order("-createdAt");
        } else if (index == 1) {
            query.order("-score");
        } else {
            query.order("-level");
        }
        query.include("worker");
        //如果不加上这条语句，默认返回10条数据
        query.setLimit(limit);

        if (actionType == STATE_MORE) {
            query.setSkip(page * limit);
        } else {
            query.setSkip(0);
        }
        query.findObjects(context, new FindListener<AppliedWorker>() {
            @Override
            public void onSuccess(List<AppliedWorker> list) {
                // TODO Auto-generated method stub
//                BaseActivity.toast(context,"查询成功：共" + list.size() + "条数据");
//                workList = list;
                listView.onRefreshComplete();
                listView.updateLoadMoreViewText(list);
//                isLoadAll = list.size() < 2;

                if (actionType == STATE_REFRESH) {
                    curPage = 0;
                    adapter.clear();
                }
                adapter.addAll(list);
//                pno++;
                curPage++;
            }

            @Override
            public void onError(int code, String msg) {
                // TODO Auto-generated method stub
                BaseActivity.toast(context, "查询工人列表失败:" + msg);

            }
        });

        //查询当前登录用户的第一个订单
//        myOrder = new Order();
        BmobQuery<Order> queryOrder = new BmobQuery<Order>();
        queryOrder.addWhereEqualTo("customer", currentUser);
        queryOrder.addWhereEqualTo("isComplete", false);
//        queryOrder.addWhereEqualTo("worker", null);
        queryOrder.order("-updatedAt");
        queryOrder.setLimit(1);
        queryOrder.findObjects(context, new FindListener<Order>() {
            @Override
            public void onSuccess(List<Order> list) {
                if (list.size() > 0) {
                    myOrder = list.get(0);
                } else {
                    myOrder = null;
                }
//                BaseActivity.toast(context,list.get(0).getName());
            }

            @Override
            public void onError(int code, String msg) {
                BaseActivity.toast(context, msg);

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

    //排序
    protected void initSort() {
        sort_layout = (LinearLayout) view.findViewById(R.id.sort_layout);
        sort_layout.setEnabled(true);
        sort_text = (TextView) view.findViewById(R.id.sort_text);
        sort_image = (ImageView) view.findViewById(R.id.sort_image);
        sort_arrow = (ImageView) view.findViewById(R.id.sort_arrow);
//        sort = SharedPreferences.getInstance().getInt("remember", 0);
        // 排序的下拉区域
        sort_and_filter_scroll = (LinearLayout) view.findViewById(R.id.sort_and_screen_scroll);
        sort_area = (LinearLayout) view.findViewById(R.id.sort_area);
        // 初始化排序下拉区域里的内容
        final ArrayList<String> strings = new ArrayList<>();
        strings.add("按申请时间降序");
        strings.add("按用户评分降序");
        strings.add("按工人等级降序");
        initSortArea(strings);
        sort_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    if (sort_area.getVisibility() == View.VISIBLE
                            && sort_and_filter_scroll.getVisibility() == View.VISIBLE) {
                        setSortAreaGone();
                        setSortUnpressed();
                    } else {
                        sort_and_filter_scroll.setVisibility(View.VISIBLE);
                        sort_area.setVisibility(View.VISIBLE);
                        int height = sort_area.getHeight();
                        if (height == 0 && strings.size() > 0) {
                            height = getResources().getDimensionPixelSize(
                                    R.dimen.dp_45)
                                    * strings.size();
                        }
                        TranslateAnimation translateAnimation = new TranslateAnimation(
                                0, 0, -height, 0);
                        translateAnimation.setDuration(300);
                        sort_area.startAnimation(translateAnimation);
                        setSortPressed();
                    }
                } catch (Exception e) {

                }
            }
        });

        sort_and_filter_scroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (sort_area.getVisibility() == View.VISIBLE) {
                        setSortAreaGone();
                        setSortUnpressed();
                    }
                } catch (Exception e) {

                }
            }
        });


    }

    //给每个选项绑定监听事件
    protected void initSortArea(final ArrayList<String> strings) {
        sort_area.removeAllViews();
        int index = 0;
        for (final String string : strings) {
            final LinearLayout layout = (LinearLayout) View.inflate(context,
                    R.layout.sort_item, null);
            final TextView content = (TextView) layout
                    .findViewById(R.id.content);
            content.setText(string);
            final ImageView selected = (ImageView) layout
                    .findViewById(R.id.selected);
            if (index == sort) {
                selected.setVisibility(View.VISIBLE);
                content.setTextColor(getResources().getColor(
                        R.color.dialog_text_blue));
            } else {
                selected.setVisibility(View.GONE);
                content.setTextColor(Color.parseColor("#FF292F33"));
            }
            final int finalIndex = index;
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
//                        if (finalIndex != sort) {
                        for (int i = 0; i < sort_area.getChildCount(); i++) {
                            sort_area.getChildAt(i).findViewById(R.id.selected)
                                    .setVisibility(View.GONE);
                            TextView contentText = (TextView) sort_area
                                    .getChildAt(i).findViewById(R.id.content);
                            contentText.setTextColor(Color
                                    .parseColor("#FF292F33"));
                        }
                        selected.setVisibility(View.VISIBLE);
                        content.setTextColor(getResources().getColor(
                                R.color.dialog_text_blue));
                        sort_text.setText(string);
                        sort = finalIndex;
                        sort_text.setText(strings.get(sort));

//                            SharedPreferences.getInstance().putInt("remember", sort);
                        //在这里请求数据
                        loadData(0, STATE_REFRESH, sort);
//                        }

                        setSortUnpressed();
                        setSortAreaGone();
                        // sort_and_filter_scroll.setVisibility(View.GONE);
                        // sort_area.setVisibility(View.GONE);
                    } catch (Exception e) {

                    }
                }
            });
            sort_area.addView(layout);
            index++;
        }
    }

    public void setSortAreaGone() {
        if (sort_area == null) {
            return;
        }
        if (sort_area.getVisibility() != View.VISIBLE) {
            return;
        }
        int height = sort_area.getHeight();
        TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, 0,
                -height);
        translateAnimation.setDuration(300);
        translateAnimation
                .setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        sort_and_filter_scroll.setVisibility(View.GONE);
                        sort_area.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                        // To change body of implemented methods use File |
                        // Settings | File Templates.
                    }
                });
        sort_area.startAnimation(translateAnimation);
    }

    public void setSortUnpressed() {
        if (sort_area.getVisibility() != View.VISIBLE) {
            return;
        }
        sort_text.setTextColor(Color.parseColor("#FF292F33"));
        sort_image.setImageResource(R.drawable.sort);
        sort_arrow.setImageResource(R.drawable.arrow_down);
        if (rotateAnimation != null) {
            sort_arrow.startAnimation(rotateAnimation);
        }
    }

    public void setSortPressed() {
        sort_text.setTextColor(Color.parseColor("#FF55ACEE"));
        sort_image.setImageResource(R.drawable.sort_pressed);
        sort_arrow.setImageResource(R.drawable.arrow_up);
        if (rotateAnimation != null) {
            sort_arrow.startAnimation(rotateAnimation);
        }
    }


}