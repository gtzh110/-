package com.yuzhi.fine.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.yuzhi.fine.R;
import com.yuzhi.fine.model.Order;
import com.yuzhi.fine.model.User;
import com.yuzhi.fine.model.UserInfo;
import com.yuzhi.fine.utils.LogUtils;

import org.w3c.dom.Text;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by lemon on 2016/6/2.
 */
public class FeedBackActivity extends BaseActivity {
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.back)
    ImageView back;
    @Bind(R.id.confirm)
    TextView confirm;
    @Bind(R.id.info_key1)
    TextView infoKey1;
    @Bind(R.id.info_value1)
    TextView infoValue1;
    @Bind(R.id.info_key2)
    TextView infoKey2;
    @Bind(R.id.info_value2)
    TextView infoValue2;
    @Bind(R.id.info_key3)
    TextView infoKey3;
    @Bind(R.id.info_value3)
    TextView infoValue3;
    @Bind(R.id.info_key4)
    TextView infoKey4;
    @Bind(R.id.info_value4)
    TextView infoValue4;
    @Bind(R.id.rating_bar)
    RatingBar ratingBar;
    @Bind(R.id.edit_content)
    EditText editContent;
    @Bind(R.id.info_key5)
    TextView textView;
    @Bind(R.id.title_comment)
    TextView titleComment;
    @Bind(R.id.view_customer)
    LinearLayout mLinearLayout;

    private View mBtn_submit;
    private Order mOrder;
    private User mWorker;
    private User mCustomer;
    private float mScore;
    private String mComment;
    private UserInfo info;
    private boolean isCommment;
    private User mCurrentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        ButterKnife.bind(this);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
//                toast(rating + "");
                mScore = rating;
            }
        });

        initView();
        initData();
        mBtn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commit();
            }
        });

    }

    private void initData() {
        if (getIntent() != null) {
            Intent intent = getIntent();
            mOrder = (Order) intent.getSerializableExtra("ORDER");
            mWorker = mOrder.getWorker();
            mCustomer = mOrder.getCustomer();
            isCommment = mOrder.getIsComment();
        }
        mCurrentUser = User.getCurrentUser(this, User.class);

        infoValue1.setText(mOrder.getCreatedAt());
        infoValue2.setText(mOrder.getEndDate());
        infoValue3.setText(mWorker.getName());
        infoValue4.setText(mWorker.getPhoneNumber());

        if (isCommment && mCurrentUser.getIsCustomer()) {
            ratingBar.setRating(mOrder.getScore());
            editContent.setText(mOrder.getComments());
            textView.setText("  您已经打过分了");
            ratingBar.setIsIndicator(true);
            editContent.setEnabled(false);
            mBtn_submit.setEnabled(false);
            ((TextView) mBtn_submit.findViewById(R.id.text)).setText("已提交");
        } else if (isCommment && !mCurrentUser.getIsCustomer()) {
            ratingBar.setRating(mOrder.getScore());
            editContent.setText(mOrder.getComments());
            textView.setText("客户为您评分：" + mOrder.getScore() + "分");
            ratingBar.setIsIndicator(true);
            editContent.setEnabled(false);
            mBtn_submit.setEnabled(false);
            mBtn_submit.setVisibility(View.GONE);
        } else if (!isCommment && !mCurrentUser.getIsCustomer()) {
            titleComment.setText("客户尚未给出评价哦");
            mLinearLayout.setVisibility(View.GONE);
        } else {
            //xml中已经初始化
        }


    }

    public void initView() {
        //标题栏
        confirm.setVisibility(View.INVISIBLE);
        title.setText("订单详情");
        //内容
        infoKey1.setText("下单时间");
        infoKey2.setText("结单时间");
        infoKey3.setText("姓名");
        infoKey4.setText("手机号");
        //提交Btn
        mBtn_submit = findViewById(R.id.commit);
        ((TextView) mBtn_submit.findViewById(R.id.text)).setText("提交评价");

    }

    private void commit() {
        if (!isCommment) {
            if (mScore == 0 || TextUtils.isEmpty(editContent.getText().toString())) {
                toast(this, "别忘了给师傅打分哦!");
            } else {
//            mWorker.setScore(String.valueOf(mScore));
                info = new UserInfo();
                info.setWorker(mWorker);
                mComment = editContent.getText().toString();
                LogUtils.e("text", mComment);
                info.setComment(mComment);
                info.setOrder(mOrder);
                info.setScore((int) mScore);
                info.save(this, new SaveListener() {
                    @Override
                    public void onSuccess() {
                        toast("打分成功");
                        mOrder.setScore((int) mScore);
                        mOrder.setComments(mComment);
                        mOrder.setIsComment(true);
                        mOrder.update(FeedBackActivity.this, mOrder.getObjectId(), new UpdateListener() {
                            @Override
                            public void onSuccess() {
                                Intent i = new Intent();
                                setResult(1, i);
                                finish();
                            }

                            @Override
                            public void onFailure(int i, String s) {
                                toast("内层" + s);
                            }
                        });
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        toast("外层" + s);
                    }
                });
            }

        } else {

        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.title, R.id.back, R.id.cancel, R.id.confirm, R.id.info_value1, R.id.info_value2, R.id.info_value3,
            R.id.info_value4})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title:
                break;
            case R.id.back:
                finish();
                break;
            case R.id.cancel:
                break;
            case R.id.confirm:
                break;
            case R.id.info_value1:
                break;
            case R.id.info_value2:
                break;
            case R.id.info_value3:
                break;
            case R.id.info_value4:
                break;
        }
    }
}
