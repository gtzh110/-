package com.yuzhi.fine.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import com.yuzhi.fine.R;
import com.yuzhi.fine.model.User;
import com.yuzhi.fine.ui.UIHelper;
import com.yuzhi.fine.ui.viewpagerindicator.CirclePageIndicator;
import com.yuzhi.fine.utils.SharedPreferences;

import cn.bmob.v3.Bmob;

/**
 * Created by tiansj on 15/7/29.
 */
public class SplashActivity extends BaseFragmentActivity {
    private static final String APPID = "7debe406bed840eb3f20625b3a819860";
    private Button btn_worker;
    private Button btn_customer;
    private User currentUser;

    //第一次启动的轮播图
    /*  private CirclePageIndicator indicator;
    private ViewPager pager;
    private GalleryPagerAdapter adapter;
    private int[] images = {
            R.drawable.newer01,
            R.drawable.newer02,
            R.drawable.newer03,
            R.drawable.newer04
    };
*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Bmob.initialize(this, APPID);
        currentUser = User.getCurrentUser(this, User.class);
        if (currentUser != null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
      /*  boolean firstTimeUse = SharedPreferences.getInstance().getBoolean("first-time-use", true);
        if(firstTimeUse) {
            initGuideGallery();
        } else {
            initLaunchLogo();
        }*/
        initLaunchLogo();
    }

    private void initLaunchLogo() {
        ImageView guideImage = (ImageView) findViewById(R.id.guideImage);
        guideImage.setVisibility(View.VISIBLE);
        btn_worker = (Button) findViewById(R.id.btn_worker);
        btn_customer = (Button) findViewById(R.id.btn_customer);
        /*btn_worker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.showLogin(SplashActivity.this, WOKER);
            }
        });*/
        btn_worker.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                UIHelper.showMap(SplashActivity.this);
            }
        });
        btn_customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.showLogin(SplashActivity.this, CUSTOMER);
            }
        });

        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                UIHelper.showHome(SplashActivity.this);
            }
        }, 500);*/
    }

   /* private void initGuideGallery() {
        final Animation fadeIn= AnimationUtils.loadAnimation(this, R.anim.fadein);
        btnHome = (Button) findViewById(R.id.btnHome);
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.getInstance().putBoolean("first-time-use", false);
                UIHelper.showHome(SplashActivity.this);
            }
        });

        indicator = (CirclePageIndicator) findViewById(R.id.indicator);
        indicator.setVisibility(View.VISIBLE);
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setVisibility(View.VISIBLE);

        adapter = new GalleryPagerAdapter();
        pager.setAdapter(adapter);
        indicator.setViewPager(pager);

        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == images.length - 1) {
                    btnHome.setVisibility(View.VISIBLE);
                    btnHome.startAnimation(fadeIn);
                } else {
                    btnHome.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    public class GalleryPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return images.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView item = new ImageView(SplashActivity.this);
            item.setScaleType(ImageView.ScaleType.CENTER_CROP);
            item.setImageResource(images[position]);
            container.addView(item);
            return item;
        }

        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
            collection.removeView((View) view);
        }
    }
*/
}
