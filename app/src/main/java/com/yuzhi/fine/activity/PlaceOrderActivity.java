package com.yuzhi.fine.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yuzhi.fine.R;
import com.yuzhi.fine.model.Order;
import com.yuzhi.fine.model.User;

import org.w3c.dom.Text;

import cn.bmob.v3.listener.SaveListener;

/**
 * Created by lemon on 2016/4/7.
 */
public class PlaceOrderActivity extends BaseActivity {
    private User customer;
    private View nameArea;
    private View phoneArea;
    private View addressArea;
    private EditText name;
    private EditText phone;
    private EditText address;
    private String userName;
    private String userPhone;
    private EditText content;
    private ImageView image_back;
    private View btn_submit;
    private Order placeOrder;
    private EditText unClicked;
    private User currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);
        initData();
        initView();
        bindClickListener();
    }

    public void initData() {
        currentUser = User.getCurrentUser(this, User.class);
        if (currentUser != null) {
            userName = currentUser.getName();
            userPhone = currentUser.getPhoneNumber();
        }
    }


    public void bindClickListener() {
        nameArea.setOnClickListener(this);
        phoneArea.setOnClickListener(this);
        addressArea.setOnClickListener(this);
        btn_submit.setOnClickListener(this);
        image_back.setOnClickListener(this);
        unClicked.setEnabled(false);
        unClicked.setFocusable(false);
        content.setOnClickListener(this);
    }

    public void initView() {
        //标题栏
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.header);
        image_back = (ImageView) findViewById(R.id.back);
        TextView textView = (TextView) linearLayout.findViewById(R.id.confirm);
        textView.setVisibility(View.INVISIBLE);
        TextView title = (TextView) linearLayout.findViewById(R.id.title);
        title.setText("我要下单");
        //内容区域
        nameArea = findViewById(R.id.name);
        phoneArea = findViewById(R.id.phone);
        addressArea = findViewById(R.id.address);
        btn_submit = findViewById(R.id.button_submit);
        unClicked = (EditText) findViewById(R.id.unClicked);
        ((TextView) btn_submit.findViewById(R.id.text)).setText("提交");
        ((ImageView) nameArea.findViewById(R.id.icon)).setImageResource(R.drawable.img_name);
        ((ImageView) nameArea.findViewById(R.id.icon)).setVisibility(View.VISIBLE);
        ((ImageView) phoneArea.findViewById(R.id.icon)).setImageResource(R.drawable.img_phone);
        ((ImageView) phoneArea.findViewById(R.id.icon)).setVisibility(View.VISIBLE);
        ((ImageView) addressArea.findViewById(R.id.icon)).setImageResource(R.drawable.img_address);
        ((ImageView) addressArea.findViewById(R.id.icon)).setVisibility(View.VISIBLE);
        ((TextView) nameArea.findViewById(R.id.title_name)).setText("客户姓名");
        ((TextView) phoneArea.findViewById(R.id.title_name)).setText("客户电话");
        ((TextView) addressArea.findViewById(R.id.title_name)).setText("家庭地址");
        name = (EditText) nameArea.findViewById(R.id.edit_now);
        phone = (EditText) phoneArea.findViewById(R.id.edit_now);
        phone.setInputType(InputType.TYPE_CLASS_NUMBER);
        address = (EditText) addressArea.findViewById(R.id.edit_now);
        content = (EditText) findViewById(R.id.edit_content);

        name.setText(userName);
        phone.setText(userPhone);
    }

    public void bindEditText(EditText text) {
        text.requestFocusFromTouch();
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.showSoftInput(text, InputMethodManager.SHOW_IMPLICIT);
        text.setSelection(text.getText().length());

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_submit:
                if (TextUtils.isEmpty(name.getText().toString()) || TextUtils.isEmpty(phone.getText().toString()) ||
                        TextUtils.isEmpty(address.getText().toString())) {
                    toast(this, "请填写完整");
                } else {
                    placeOrder = new Order();
                    User currentUser = User.getCurrentUser(this, User.class);
                    placeOrder.setName(name.getText().toString());
                    if (!isPhoneNumberValid(phone.getText().toString())) {
                        toast("请输入正确的手机号码");
                        break;
                    }
                    placeOrder.setEndDate("2016-06-04 10:00");
                    placeOrder.setPhoneNumber(phone.getText().toString());
                    placeOrder.setAddress(address.getText().toString());
                    if (!TextUtils.isEmpty(content.getText().toString())) {
                        placeOrder.setContent(content.getText().toString());
                    }
//                    placeOrder.setEndDate(null);
                    placeOrder.setIsComplete(false);
                    placeOrder.setCustomer(currentUser);
                    placeOrder.save(this, new SaveListener() {
                        @Override
                        public void onSuccess() {
                            toast("订单提交成功，请选择工人");
                            finish();
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            toast("订单提交失败");
                        }
                    });
                }

                break;
            case R.id.name:
                bindEditText(name);
                break;
            case R.id.phone:
                bindEditText(phone);
                break;
            case R.id.address:
                bindEditText(address);
                break;
            case R.id.edit_content:
                bindEditText(content);
                break;
            case R.id.back:
                finish();
                break;
            default:
                break;
        }

    }

}
