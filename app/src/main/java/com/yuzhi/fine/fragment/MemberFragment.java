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
import android.widget.Toast;

import com.yuzhi.fine.R;
import com.yuzhi.fine.activity.BaseActivity;
import com.yuzhi.fine.activity.LoginActivity;
import com.yuzhi.fine.activity.PersonalProfileActivity;
import com.yuzhi.fine.activity.PlaceOrderActivity;
import com.yuzhi.fine.activity.SalaryActivity;
import com.yuzhi.fine.activity.SplashActivity;
import com.yuzhi.fine.model.AppliedWorker;
import com.yuzhi.fine.model.Order;
import com.yuzhi.fine.model.User;
import com.yuzhi.fine.model.UserInfo;
import com.yuzhi.fine.ui.pulltozoomview.PullToZoomScrollViewEx;
import com.yuzhi.fine.utils.LogUtils;
import com.yuzhi.fine.utils.SharedPreferences;
import com.yuzhi.fine.utils.StringUtils;
import com.yuzhi.fine.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.FindStatisticsListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class MemberFragment extends Fragment {
    private static final String SCORE = "score";
    private static final String COUNT = "count";
    private static final String MONTH = "month";

    private User currentUser;
    private Activity context;
    private View root;
    private PullToZoomScrollViewEx scrollView;
    private LinearLayout linearLayout;
    private String name;
    private int level;
    private LinearLayout contentView;
    private TextView btn_apply;
    TextView leveltext;
    private boolean isApplied;
    private boolean isComplete;
    private double score;
    private String formatScore;
    private int count;
    private int month;
    private int orderCount;

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
        level = bundle.getInt("LEVEL");
        LogUtils.e("level", level + "");
        initData();
        LogUtils.e("test Create", String.valueOf(isComplete));
        initView();
    }

    void initView() {
        scrollView = (PullToZoomScrollViewEx) root.findViewById(R.id.scrollView);
        View headView = LayoutInflater.from(context).inflate(R.layout.member_head_view, null, false);
        linearLayout = (LinearLayout) headView.findViewById(R.id.head_content);
        TextView text_user_name = (TextView) linearLayout.findViewById(R.id.user_name);
        leveltext = (TextView) linearLayout.findViewById(R.id.level);
        leveltext.setText("Lv " + level);
//        levelUp(orderCount);

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
                            //传递等级
                            list.setLevel(level);
                            list.setName(currentUser.getName());
                            //传评分
                            list.setScore(formatScore);
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
            scrollView.getPullRootView().findViewById(R.id.month_salary).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), SalaryActivity.class);
                    intent.putExtra(COUNT, count);
                    intent.putExtra(SCORE, formatScore);
                    intent.putExtra(MONTH, month);
                    getActivity().startActivity(intent);
                }
            });
        } else {
            scrollView.getPullRootView().findViewById(R.id.month_salary).setVisibility(View.GONE);
            scrollView.getPullRootView().findViewById(R.id.apply_work).setVisibility(View.GONE);
            scrollView.getPullRootView().findViewById(R.id.line_1).setVisibility(View.GONE);
            scrollView.getPullRootView().findViewById(R.id.line_3).setVisibility(View.GONE);
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
                    if (isComplete) {
                        isComplete = false;
                        Intent intent = new Intent(getActivity(), PlaceOrderActivity.class);
                        getActivity().startActivity(intent);
                    } else {
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
        if (currentUser != null) {
            name = currentUser.getName();
//            level = currentUser.getLevel().intValue();
            queryScore();
            queryCount();
            queryOrderCount();
//            updateLevel(level);
        }

    }

    public void queryScore() {
        BmobQuery<UserInfo> queryScore = new BmobQuery<UserInfo>();
        queryScore.addWhereEqualTo("worker", currentUser);
        queryScore.average(new String[]{"score"});
//        queryScore.groupby(new String[]{"createdAt"});
        queryScore.findStatistics(getActivity(), UserInfo.class, new FindStatisticsListener() {
            @Override
            public void onSuccess(Object o) {
                JSONArray arr = (JSONArray) o;
                if (arr != null) {
                    try {
                        JSONObject obj = arr.getJSONObject(0);
                        score = obj.getDouble("_avgScore");
                        DecimalFormat df = new DecimalFormat("######0.0");
                        formatScore = df.format(score);
                        LogUtils.e("平均评分", formatScore);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    LogUtils.e("平均评分", "查询成功，但无该人数据。");
//                    Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int i, String s) {
                Toast.makeText(getActivity(), "查询失败:" + s, Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void queryCount() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH) + 1;
        int days = c.get(Calendar.DAY_OF_MONTH);
        String start, end;
        LogUtils.e("Month", month + " " + year);
        BmobQuery<Order> query = new BmobQuery<>();
        List<BmobQuery<Order>> and = new ArrayList<>();
        //大于该月第一天
        BmobQuery<Order> q1 = new BmobQuery<>();
//        q1.addWhereEqualTo("worker",currentUser);
        if (month >= 1 && month <= 9) {
            start = year + "-0" + month + "-01 00:00:00";
        } else {
            start = year + "-" + month + "-01 00:00:00";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = sdf.parse(start);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        q1.addWhereGreaterThanOrEqualTo("createdAt", new BmobDate(date));
        and.add(q1);
        //小于该月的最后一天23:59:59
        BmobQuery<Order> q2 = new BmobQuery<>();
//        q2.addWhereEqualTo("worker",currentUser);
        if (month >= 1 && month <= 9) {
            end = year + "-0" + month + "-" + days + " 23:59:59";
        } else {
            end = year + "-" + month + "-" + days + " 23:59:59";
        }
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date1 = null;
        try {
            date1 = sdf1.parse(end);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        q2.addWhereLessThanOrEqualTo("createdAt", new BmobDate(date1));
        and.add(q2);
        query.addWhereEqualTo("worker", currentUser);
        query.and(and);
        query.count(getActivity(), Order.class, new CountListener() {
            @Override
            public void onSuccess(int i) {
                count = i;
                LogUtils.e("count", i + "");
            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
    }

    public void queryOrderCount() {
        BmobQuery<Order> query = new BmobQuery<>();
        query.addWhereEqualTo("worker", currentUser);
        query.count(getActivity(), Order.class, new CountListener() {
            @Override
            public void onSuccess(int i) {
                orderCount = i;
//                levelUp();
                LogUtils.e("总数", i + "");
            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
    }


}