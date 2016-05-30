
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
import cn.bmob.v3.listener.FindListener;

public class MainActivity extends BaseFragmentActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String FRAGMENT_TAGS = "fragmentTags";
    private static final String CURR_INDEX = "currIndex";
    private static int currIndex = 0;
    private boolean isApplied;
    private boolean isComplete;
    private User currentUser;

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
    public void init(){
        currentUser = User.getCurrentUser(this,User.class);
        if (currentUser != null){
            BmobQuery<AppliedWorker> query = new BmobQuery<AppliedWorker>();
            query.addWhereEqualTo("worker", currentUser);
            query.findObjects(this, new FindListener<AppliedWorker>() {
                @Override
                public void onSuccess(List<AppliedWorker> list) {
                    if (list.size() == 0) {
                        isApplied = false;
//                        LogUtils.e("isApplied","没有申请工作");
                    } else {
                        isApplied = true;
//                        LogUtils.e("isApplied","申请了工作");
                    }
                }

                @Override
                public void onError(int i, String s) {
//                    LogUtils.e("isApplied",s);
                }
            });
            //查询订单是否已经完成
            BmobQuery<Order> queryOrder = new BmobQuery<Order>();
            if(currentUser.getIsCustomer()){
                queryOrder.addWhereEqualTo("customer", currentUser);
            }else{
                queryOrder.addWhereEqualTo("worker", currentUser);
            }
            queryOrder.addWhereEqualTo("isComplete", false);
            queryOrder.findObjects(this, new FindListener<Order>() {
                @Override
                public void onSuccess(List<Order> list) {
                    if (list.size() == 0) {
                        isComplete = true;
//                        LogUtils.e("test 请求后",String.valueOf(isComplete));
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
        fragmentTags = new ArrayList<>(Arrays.asList("MainPagerFragment","MemberFragment"));
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
                bundle.putBoolean("ISAPPLIED",isApplied);
                bundle.putBoolean("ISCOMPLETE",isComplete);
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
}
