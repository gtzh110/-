package com.yuzhi.fine.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yuzhi.fine.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by lemon on 2016/6/5.
 */
public class SalaryActivity extends BaseActivity {
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.back)
    ImageView back;
    @Bind(R.id.confirm)
    TextView confirm;
    @Bind(R.id.month)
    TextView month;

    @Bind(R.id.salary_base)
    TextView baseMoney;
    @Bind(R.id.salary_extra)
    TextView extraMoney;

    @Bind(R.id.count)
    TextView count;
    @Bind(R.id.remain_nums)
    TextView remainNums;
    @Bind(R.id.remain_nums1)
    TextView remainNums1;
    @Bind(R.id.score)
    TextView score;
    private static final int BASESALARY = 3000;
    private static final int EXTRASALARY = 2000;
    private static final int BASECOUNT = 15;
    private static final int EXTRACOUNT = 15;

    private int countText;
    private String scoreText;
    private int monthText;
    private int baseMoneyText;
    private int extraMoneyText;
    private int baseCount;
    private int extraCount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salary);
        ButterKnife.bind(this);
        initData();
        initView();
    }

    private void initView() {
        //标题栏
        confirm.setVisibility(View.INVISIBLE);
        title.setText("本月工资详情");
        month.setText(monthText + " 月可领工资");
        baseMoney.setText(baseMoneyText + " 元");
        extraMoney.setText(extraMoneyText+" 元");
        count.setText(countText + "");
        if (baseCount==0){
            remainNums.setText("已达标");
        }else{
            remainNums.setText(baseCount + "");
        }
        remainNums1.setText(extraCount + "");

        score.setText(scoreText + "");

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initData() {
        if (getIntent() != null) {
            Intent i = getIntent();
            countText = i.getIntExtra("count", 0);
            scoreText = i.getStringExtra("score");
            monthText = i.getIntExtra("month", 0);
        }
        calculateBaseSalary();
    }

    private void calculateBaseSalary() {
        if (countText < BASECOUNT) {
            baseMoneyText = 0;
            extraMoneyText = 0;
            baseCount = BASECOUNT - countText;
            extraCount = 0;
        } else {
            baseMoneyText = BASESALARY;
            baseCount = 0;
            extraCount = countText - BASECOUNT;
            extraMoneyText = extraCount * 100;
        }
    }

}
