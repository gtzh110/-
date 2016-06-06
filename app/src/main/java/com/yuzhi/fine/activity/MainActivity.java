
package com.yuzhi.fine.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.widget.RadioGroup;

import com.yuzhi.fine.R;
import com.yuzhi.fine.fragment.MainPagerFragment;
import com.yuzhi.fine.fragment.MemberFragment;
import com.yuzhi.fine.model.AppliedWorker;
import com.yuzhi.fine.model.Order;
import com.yuzhi.fine.model.User;
import com.yuzhi.fine.utils.LogUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class MainActivity extends BaseFragmentActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String FRAGMENT_TAGS = "fragmentTags";
    private static final String CURR_INDEX = "currIndex";
    private static int currIndex = 0;
    private boolean isApplied;
    private boolean isComplete;
    private User currentUser;
    private int level;

    private RadioGroup group;

    private ArrayList<String> fragmentTags;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        fragmentManager = getSupportFragmentManager();
        if (savedInstanceState == null) {
            initData();
            initView();
        } else {
            initFromSavedInstantsState(savedInstanceState);
        }
    }

    public void init() {
        currentUser = User.getCurrentUser(this, User.class);
        if (currentUser != null) {
            queryIsAppied();
            queryIsCompleted();
            queryOrderCount();
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CURR_INDEX, currIndex);
        outState.putStringArrayList(FRAGMENT_TAGS, fragmentTags);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        initFromSavedInstantsState(savedInstanceState);
    }

    private void initFromSavedInstantsState(Bundle savedInstanceState) {
        currIndex = savedInstanceState.getInt(CURR_INDEX);
        fragmentTags = savedInstanceState.getStringArrayList(FRAGMENT_TAGS);
        showFragment();
    }

    private void initData() {
        currIndex = 0;
        fragmentTags = new ArrayList<>(Arrays.asList("MainPagerFragment", "MemberFragment"));
    }

    private void initView() {
        group = (RadioGroup) findViewById(R.id.group);
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
//                    case R.id.foot_bar_home: currIndex = 0; break;
                    case R.id.foot_bar_im:
                        currIndex = 0;
                        break;
//                    case R.id.foot_bar_interest: currIndex = 2; break;
                    case R.id.main_footbar_user:
                        currIndex = 1;
                        break;

                    default:
                        break;
                }
                showFragment();
            }
        });
        showFragment();
    }

    private void showFragment() {
      /*  if (currIndex == 3) {
            UIHelper.showLogin(MainActivity.this);
        }*/

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = fragmentManager.findFragmentByTag(fragmentTags.get(currIndex));
        if (fragment == null) {
            fragment = instantFragment(currIndex);
        }
        for (int i = 0; i < fragmentTags.size(); i++) {
            Fragment f = fragmentManager.findFragmentByTag(fragmentTags.get(i));
            if (f != null && f.isAdded()) {
                fragmentTransaction.hide(f);
            }
        }
        if (fragment.isAdded()) {
            fragmentTransaction.show(fragment);
        } else {
            fragmentTransaction.add(R.id.fragment_container, fragment, fragmentTags.get(currIndex));
        }
        fragmentTransaction.commitAllowingStateLoss();
        fragmentManager.executePendingTransactions();
    }

    private Fragment instantFragment(int currIndex) {
        switch (currIndex) {
            case 0:
                MainPagerFragment fragmentA = new MainPagerFragment();
                return fragmentA;
//            case 1: return new BufferKnifeFragment();
//            case 2: return new BufferKnifeFragment();
            case 1:
                MemberFragment fragmentB = new MemberFragment();
                Bundle bundle = new Bundle();
                bundle.putBoolean("ISAPPLIED", isApplied);
                bundle.putBoolean("ISCOMPLETE", isComplete);
                bundle.putInt("LEVEL", level);
                updateLevel(level);
                fragmentB.setArguments(bundle);
                return fragmentB;
            default:
                return null;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //查询当前是否申请工作
    public void queryIsAppied() {
        BmobQuery<AppliedWorker> query = new BmobQuery<AppliedWorker>();
        query.addWhereEqualTo("worker", currentUser);
        query.findObjects(this, new FindListener<AppliedWorker>() {
            @Override
            public void onSuccess(List<AppliedWorker> list) {
                if (list.size() == 0) {
                    isApplied = false;
                } else {
                    isApplied = true;
                }
            }

            @Override
            public void onError(int i, String s) {
            }
        });
    }

    //查询是否完成订单
    public void queryIsCompleted() {
        BmobQuery<Order> queryOrder = new BmobQuery<Order>();
        if (currentUser.getIsCustomer()) {
            queryOrder.addWhereEqualTo("customer", currentUser);
        } else {
            queryOrder.addWhereEqualTo("worker", currentUser);
        }
        queryOrder.addWhereEqualTo("isComplete", false);
        queryOrder.findObjects(this, new FindListener<Order>() {
            @Override
            public void onSuccess(List<Order> list) {
                if (list.size() == 0) {
                    isComplete = true;
                } else {
                    isComplete = false;
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    //查询登录用户累计完成的订单数量
    public void queryOrderCount() {
        BmobQuery<Order> query = new BmobQuery<>();
        query.addWhereEqualTo("worker", currentUser);
        query.count(this, Order.class, new CountListener() {
            @Override
            public void onSuccess(int i) {
                int orderCount = i;
                levelUp(orderCount);
                LogUtils.e("总数", i + "");
            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
    }

    /*
   * 1级 0-2
   * 2级 3-5
   * 3级 6-10
   * 4级 11-18
   * 5级 19-30
   * 6级 31-50
   * 7级 51-80
   * 8级 暂无吧
   * */
    private void levelUp(int orderCount) {
        if (orderCount <= 2) {
            level = 1;
        } else if (orderCount <= 5) {
            level = 2;
        } else if (orderCount <= 10) {
            level = 3;
        } else if (orderCount <= 18) {
            level = 4;
        } else if (orderCount <= 30) {
            level = 5;
        } else if (orderCount <= 50) {
            level = 6;
        } else if (orderCount <= 80) {
            level = 7;
        } else {
            level = 8;
        }
    }
    private void updateLevel(int level) {
//        currentUser.setLevel(level);
        User updateUser = new User();
        updateUser.setLevel(level);
        updateUser.update(this, currentUser.getObjectId(), new UpdateListener() {
            @Override
            public void onSuccess() {
                LogUtils.e("UpdateLevel", "等级更新成功!");
            }

            @Override
            public void onFailure(int i, String s) {
                LogUtils.e("UpdateLevel", "等级更新失败:" + s);

            }
        });
    }
}
