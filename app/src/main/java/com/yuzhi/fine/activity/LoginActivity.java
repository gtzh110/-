package com.yuzhi.fine.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yuzhi.fine.R;
import com.yuzhi.fine.model.User;
import com.yuzhi.fine.ui.UIHelper;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.listener.SaveListener;

public class LoginActivity extends BaseActivity {
    private ImageView image_back;
    private EditText username;
    private EditText password;
    private View loginBtn;
    private TextView registerBtn;

    private String user = null;
    private String psd = null;
    private String identity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Intent intent = getIntent();
        User currentUser = User.getCurrentUser(this, User.class);
        if (intent != null) {
            identity =  intent.getStringExtra("TAG");
        }
        initView();
        if (currentUser != null) {
            if (TextUtils.equals(identity,WOKER)){
                loginToMain(this, currentUser,WOKER);
            }else{
                loginToMain(this, currentUser,CUSTOMER);
            }
        } else {
            loginBtn.setOnClickListener(this);
            registerBtn.setOnClickListener(this);
            image_back.setOnClickListener(this);
        }

    }

    public void initView() {
        image_back = (ImageView) findViewById(R.id.back);
        username = (EditText) findViewById(R.id.mail_mobile);
        password = (EditText) findViewById(R.id.password);
        loginBtn = findViewById(R.id.login_btn);
        registerBtn = (TextView) findViewById(R.id.confirm);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.header);
        TextView title = (TextView) linearLayout.findViewById(R.id.title);
        if (TextUtils.equals(identity,CUSTOMER)){
            title.setText(R.string.login_title_customer);
        }else{
            title.setText(R.string.login_title_worker);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_btn:
                user = username.getText().toString();
                psd = password.getText().toString();
                if (!isNetworkConnected(this)) {
                    toast("网络连接异常");
                } else if (TextUtils.equals(user, "") || TextUtils.equals(psd, "")) {
                    toast("请输入用户名和密码");
                    break;
                } else {
                    User userInfo = new User();
                    userInfo.setUsername(user);
                    userInfo.setPassword(psd);
                    userInfo.login(this, new SaveListener() {
                        @Override
                        public void onSuccess() {
                            toast("登录成功");
                            UIHelper.showHome(LoginActivity.this);
                            finish();
                            /*Intent intent = new Intent(this,);
                            startActivity(intent);
                            finish();*/
                        }
                        @Override
                        public void onFailure(int i, String s) {
                            toast("用户名或密码错误");
                        }
                    });

                }
                break;
            //跳转到注册界面
            case R.id.confirm:
                Intent intent = new Intent(this, RegisterActivity.class);
                intent.putExtra("TAG",identity);
                startActivityForResult(intent, REQUEST_CODE);
                break;
            case R.id.back:
                LoginActivity.this.finish();
                break;
            default:
                break;
        }

    }

    public void loginToMain(Activity activity, User currentUser,String tag) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("USER", currentUser);
//        intent.putExtra("TAG",tag);
        activity.startActivity(intent);
        activity.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE) {
                LoginActivity.this.finish();
            }

        }
    }
}
