package com.yuzhi.fine.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yuzhi.fine.R;
import com.yuzhi.fine.activity.BaseActivity;
import com.yuzhi.fine.activity.LoginActivity;
import com.yuzhi.fine.activity.PersonalProfileActivity;
import com.yuzhi.fine.activity.PlaceOrderActivity;
import com.yuzhi.fine.activity.SplashActivity;
import com.yuzhi.fine.model.AppliedWorker;
import com.yuzhi.fine.model.Order;
import com.yuzhi.fine.model.User;
import com.yuzhi.fine.ui.pulltozoomview.PullToZoomScrollViewEx;
import com.yuzhi.fine.utils.LogUtils;
import com.yuzhi.fine.utils.SharedPreferences;
import com.yuzhi.fine.utils.StringUtils;
import com.yuzhi.fine.utils.Utils;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class MemberFragment extends Fragment {
    private User currentUser;
    private Activity context;
    private View root;
    private PullToZoomScrollViewEx scrollView;
    private LinearLayout linearLayout;
    private String name;
    private int level;
    private LinearLayout contentView;
    private TextView btn_apply;
    private boolean isApplied;
    private boolean isComplete;
    Bundle bundle;

//    SharedPreferences sp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return root = inflater.inflate(R.layout.fragment_member, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
        bundle = getArguments();
        isApplied = bundle.getBoolean("ISAPPLIED");
        isComplete = bundle.getBoolean("ISCOMPLETE");
        initData();
        LogUtils.e("test Create", String.valueOf(isComplete));
        initView();
    }

    void initView() {
        scrollView = (PullToZoomScrollViewEx) root.findViewById(R.id.scrollView);
        View headView = LayoutInflater.from(context).inflate(R.layout.member_head_view, null, false);
        linearLayout = (LinearLayout) headView.findViewById(R.id.head_content);
        TextView text_user_name = (TextView) linearLayout.findViewById(R.id.user_name);
        TextView leveltext = (TextView) linearLayout.findViewById(R.id.level);
        leveltext.setText("Lv " + level);
        text_user_name.setText(name);

        View zoomView = LayoutInflater.from(context).inflate(R.layout.member_zoom_view, null, false);
        contentView = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.member_content_view, null, false);
        scrollView.setHeaderView(headView);
        scrollView.setZoomView(zoomView);
        scrollView.setScrollContentView(contentView);

       /* headView.findViewById(R.id.tv_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.showRegister(getActivity());
            }
        });

        headView.findViewById(R.id.tv_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.showLogin(getActivity());
            }
        });*/
        LogUtils.e("test外面", String.valueOf(isComplete));
        if (currentUser != null && !currentUser.getIsCustomer()) {
//            LogUtils.e("test里面", String.valueOf(isComplete));
            if ((!isApplied) && isComplete) {
                ((TextView) scrollView.getPullRootView().findViewById(R.id.apply_work)).setText("申请工作 ");
                scrollView.getPullRootView().findViewById(R.id.apply_work).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (currentUser != null) {
                            AppliedWorker list = new AppliedWorker();
                            list.setWorker(currentUser);
                            list.setLevel(currentUser.getLevel());
                            list.setName(currentUser.getName());
                            list.setScore(currentUser.getScore());
                            list.save(context, new SaveListener() {
                                @Override
                                public void onSuccess() {
                                    BaseActivity.toast(getActivity(), "申请成功");
                                    isApplied = true;
//                                    sp = SharedPreferences.getInstance();
//                                    sp.putBoolean("ISAPPLIED", isApplied);
                                    initView();
                                }

                                @Override
                                public void onFailure(int i, String s) {
                                    BaseActivity.toast(getActivity(), "申请失败" + s);
                                }
                            });
                        }
                    }
                });
            } else {
                ((TextView) scrollView.getPullRootView().findViewById(R.id.apply_work)).setText("撤销申请");
                scrollView.getPullRootView().findViewById(R.id.apply_work).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (currentUser != null) {
                            BmobQuery<AppliedWorker> query = new BmobQuery<AppliedWorker>();
                            query.addWhereEqualTo("worker", currentUser);
                            query.findObjects(context, new FindListener<AppliedWorker>() {
                                @Override
                                public void onSuccess(List<AppliedWorker> list) {
                                    AppliedWorker deleteWorker = new AppliedWorker();
                                    if (list.size() != 0) {
                                        deleteWorker.setObjectId(list.get(0).getObjectId());
                                        deleteWorker.delete(context, new DeleteListener() {
                                            @Override
                                            public void onSuccess() {
                                                BaseActivity.toast(getActivity(), "撤销成功");
                                                isApplied = false;
//                                                sp = SharedPreferences.getInstance();
//                                                sp.putBoolean("ISAPPLIED", isApplied);
                                                initView();
                                            }

                                            @Override
                                            public void onFailure(int i, String s) {
                                                BaseActivity.toast(getActivity(), "从申请工作表中删除失败" + s);
                                            }
                                        });
                                    } else {
                                        BaseActivity.toast(context, "您有未完工的订单，暂时不能撤销申请！");
                                    }

                                }

                                @Override
                                public void onError(int i, String s) {
                                    BaseActivity.toast(getActivity(), "查询申请工作表失败" + s);
                                }
                            });
                        }
                    }
                });

            }
        } else {
            scrollView.getPullRootView().findViewById(R.id.apply_work).setVisibility(View.GONE);
            scrollView.getPullRootView().findViewById(R.id.line_1).setVisibility(View.GONE);
        }


        scrollView.getPullRootView().findViewById(R.id.text_profile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PersonalProfileActivity.class);
                getActivity().startActivity(intent);
            }
        });

        if (currentUser != null && currentUser.getIsCustomer()) {
            scrollView.getPullRootView().findViewById(R.id.place_order).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isComplete){
                        isComplete = false;
                        Intent intent = new Intent(getActivity(), PlaceOrderActivity.class);
                        getActivity().startActivity(intent);
                    }else{
                        BaseActivity.toast(getActivity(), "您当前还有未完成的订单，暂不能下单");
                    }

                }
            });
        } else {
            scrollView.getPullRootView().findViewById(R.id.place_order).setVisibility(View.GONE);
            scrollView.getPullRootView().findViewById(R.id.line_2).setVisibility(View.GONE);

        }

        /*scrollView.getPullRootView().findViewById(R.id.textHelp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });*/
     /*   scrollView.getPullRootView().findViewById(R.id.textCalculator).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });*/

        scrollView.getPullRootView().findViewById(R.id.logoff).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.dialogWithActions(getActivity(), "退出登录", "您确定要退出吗", "确定", "取消", null, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        User.logOut(context);
                        /*Intent intent = new Intent(getActivity(), SplashActivity.class);
                        getActivity().startActivity(intent);*/
                        getActivity().finish();
                    }
                });

            }
        });

    }

    private void initData() {
        currentUser = User.getCurrentUser(getActivity(), User.class);
     /*   if (currentUser.getIsCustomer()) {
//            query.addWhereEqualTo("customer", currentUser);
        } else {
            //查询是否申请工作
            BmobQuery<AppliedWorker> query = new BmobQuery<AppliedWorker>();
            query.addWhereEqualTo("worker", currentUser);
            query.findObjects(context, new FindListener<AppliedWorker>() {
                @Override
                public void onSuccess(List<AppliedWorker> list) {
                    if (list.size() == 0) {
                        isApplied = false;
                        LogUtils.e("isApplied","没有申请工作");
                    } else {
                        isApplied = true;
                        LogUtils.e("isApplied","申请了工作");
                    }
                }

                @Override
                public void onError(int i, String s) {
                    LogUtils.e("isApplied",s);
                }
            });
            //查询订单是否已经完成
            BmobQuery<Order> queryOrder = new BmobQuery<Order>();
            queryOrder.addWhereEqualTo("worker", currentUser);
            queryOrder.addWhereEqualTo("isComplete", false);
            queryOrder.findObjects(context, new FindListener<Order>() {
                @Override
                public void onSuccess(List<Order> list) {
                    if (list.size() == 0) {
                        isComplete = true;
                        LogUtils.e("test 请求后",String.valueOf(isComplete));
//                        LogUtils.e("isComplete", String.valueOf(isComplete));
                    } else {
                        isComplete = false;
//                        LogUtils.e("isComplete", String.valueOf(isComplete));
                    }
                }
                @Override
                public void onError(int i, String s) {

                }
            });
//            LogUtils.e("test 请求后",String.valueOf(isComplete));

        }*/
//        sp = SharedPreferences.getInstance();
//        isApplied = sp.getBoolean("ISAPPLIED", false);
//        isApplied = false;
        if (currentUser != null) {
            name = currentUser.getName();
            level = currentUser.getLevel().intValue();
        }

    }
}