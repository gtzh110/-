package com.yuzhi.fine.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.yuzhi.fine.R;
import com.yuzhi.fine.model.User;

import cn.bmob.v3.listener.SaveListener;

/**
 * Created by lemon on 2016/4/1.
 */
public class RegisterActivity extends BaseActivity {
    private View textViewRegister;
    private ImageView image_back;
    private EditText username;
    private EditText password;
    private EditText passwordConfirm;
    private EditText edit_name;
    private EditText edit_phoneNumber;
    private String mUser = null;
    private String mPsd = null;
    private String mComfirmPsd = null;
    private String mPhone = null;
    private String mName = null;
    private String identity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Intent intent = getIntent();
        if (intent != null) {
            identity = intent.getStringExtra("TAG");
        }
        initView();
        textViewRegister.setOnClickListener(this);
        image_back.setOnClickListener(this);
    }

    private void initView() {
//        View view = View.inflate(this,R.layout.layout_register_button,null);
        image_back = (ImageView) findViewById(R.id.back);
        textViewRegister = findViewById(R.id.login_btn);
        username = (EditText) findViewById(R.id.mail_mobile);
        password = (EditText) findViewById(R.id.password);
        passwordConfirm = (EditText) findViewById(R.id.password_confirm);
        edit_name = (EditText) findViewById(R.id.text_name);
        edit_phoneNumber = (EditText) findViewById(R.id.text_phoneNumber);

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.header);
        //标题栏
        TextView textView = (TextView) linearLayout.findViewById(R.id.confirm);
        textView.setVisibility(View.INVISIBLE);
        TextView title = (TextView) linearLayout.findViewById(R.id.title);
        if (TextUtils.equals(identity, CUSTOMER)) {
            title.setText(R.string.register_title_customer);
        } else {
            title.setText(R.string.register_title_worker);
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_btn:
                mUser = username.getText().toString();
                mPsd = password.getText().toString();
                mComfirmPsd = passwordConfirm.getText().toString();
                mName = edit_name.getText().toString();
                mPhone = edit_phoneNumber.getText().toString();
                if (!isNetworkConnected(this)) {
                    toast("网络连接失败");
                    break;
                } else if (mUser.equals("") || mPsd.equals("")
                        || mComfirmPsd.equals("") || mName.equals("") || mPhone.equals("")) {
                    toast("请填写完整");
                    break;
                } else if (!isPhoneNumberValid(mPhone)) {
                    toast("请输入正确的手机号码");
                    break;
                } else if (!TextUtils.equals(mComfirmPsd, mPsd)) {
                    toast("两次密码输入不一致");
                    break;
                } else {
                    // 开始提交注册信息
                    User bu = new User();
                    bu.setUsername(mUser);
                    bu.setPassword(mPsd);
                    bu.setPhoneNumber(mPhone);
                    bu.setName(mName);
                    bu.setSelfIntroduce("客户您好，选择我就是选择放心。");
                    bu.setScore("0");
                    bu.setLevel(Integer.valueOf(1));
                    if (TextUtils.equals(WOKER, identity)) {
                        bu.setIsCustomer(false);
                    } else {
                        bu.setIsCustomer(true);
                    }
                    bu.signUp(this, new SaveListener() {
                        @Override
                        public void onSuccess() {
                            toast("注册成功");
                            setResult(RESULT_OK);
                            Intent backLogin = new Intent(RegisterActivity.this,
                                    LoginActivity.class);
                            startActivity(backLogin);
                            RegisterActivity.this.finish();
                        }

                        @Override
                        public void onFailure(int arg0, String msg) {
                            toast(msg);
                        }
                    });
                }
                break;
            case R.id.back:
                RegisterActivity.this.finish();
                break;
            default:
                break;

        }

    }
}
