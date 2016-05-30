package com.yuzhi.fine.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yuzhi.fine.R;
import com.yuzhi.fine.model.User;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by lemon on 2016/4/5.
 */
public class PersonalProfileActivity extends BaseActivity {
    private View mEditName;
    private View mEditLevel;
    private View mEditId;
    private View mEditPhone;
    private View mEditAddress;
    private View mEditIntroduction;
    private View mEditCetify;
    private ImageView image_back;
    private View mBtn_submit;

    private User currentUser;


    /*  Handler mHandler = new Handler() {
          @Override
          public void handleMessage(Message msg) {
              switch (msg.what) {
                  case 200:
  //                    case 300:
                      initData();
                      break;

                  default:
                      break;
              }
          }
      };
  */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_profile);
        initView();
        new Thread(new Runnable() {
            @Override
            public void run() {
                currentUser = User.getCurrentUser(PersonalProfileActivity.this, User.class);
                if (currentUser != null) {
                    PersonalProfileActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initData();
                        }
                    });
                }
            }
        }).start();
        bindClickListener();
    }

    public void bindClickListener() {
        mBtn_submit.setOnClickListener(this);
        image_back.setOnClickListener(this);
        mEditName.setOnClickListener(this);
        mEditLevel.setOnClickListener(this);
        mEditId.setOnClickListener(this);
        mEditPhone.setOnClickListener(this);
        mEditAddress.setOnClickListener(this);
        mEditIntroduction.setOnClickListener(this);
        mEditCetify.setOnClickListener(this);
    }

    public void initView() {
        mEditName = findViewById(R.id.name);
        mEditLevel = findViewById(R.id.level);
        mEditId = findViewById(R.id.id);
        mEditPhone = findViewById(R.id.phone);
        mEditAddress = findViewById(R.id.address);
        mEditIntroduction = findViewById(R.id.introduction);
        mEditCetify = findViewById(R.id.certification);
        mBtn_submit = findViewById(R.id.button_submit);
        ((TextView) mEditName.findViewById(R.id.title_name)).setText("姓名");
        ((TextView) mEditLevel.findViewById(R.id.title_name)).setText("等级");
        ((TextView) mEditId.findViewById(R.id.title_name)).setText("身份证号");
        ((TextView) mEditPhone.findViewById(R.id.title_name)).setText("手机号");
        ((TextView) mEditAddress.findViewById(R.id.title_name)).setText("地址");
        ((TextView) mEditIntroduction.findViewById(R.id.title_name)).setText("自我介绍");
        ((ImageView) mEditIntroduction.findViewById(R.id.bottom_line)).setVisibility(View.GONE);
        ((TextView) mEditCetify.findViewById(R.id.title_name)).setText("证书编号");
        ((TextView) mBtn_submit.findViewById(R.id.text)).setText("提交修改");
        setUnchanged();
        //标题栏
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.header);
        image_back = (ImageView) findViewById(R.id.back);
        TextView textView = (TextView) linearLayout.findViewById(R.id.confirm);
        textView.setVisibility(View.INVISIBLE);
        TextView title = (TextView) linearLayout.findViewById(R.id.title);
        title.setText("个人资料");
    }

    public void initData() {
        currentUser = User.getCurrentUser(this, User.class);
        ((EditText) mEditName.findViewById(R.id.edit_now)).setText(currentUser.getName());
        ((EditText) mEditLevel.findViewById(R.id.edit_now)).setText(String.valueOf(currentUser.getLevel()));
        ((EditText) mEditId.findViewById(R.id.edit_now)).setText(currentUser.getIdentity());
        ((EditText) mEditPhone.findViewById(R.id.edit_now)).setText(currentUser.getPhoneNumber());
        ((EditText) mEditAddress.findViewById(R.id.edit_now)).setText(currentUser.getAddress());
        ((EditText) mEditIntroduction.findViewById(R.id.edit_now)).setText(currentUser.getSelfIntroduce());
        ((EditText) mEditCetify.findViewById(R.id.edit_now)).setText(currentUser.getCertification());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_submit:
                //新建一个User实例,此方法只会更新你提交的用户信息
                // 而不会将本地存储的用户信息也提交到后台更新。
                User updateUser = new User();
                updateUser.setIdentity(((EditText) mEditId.findViewById(R.id.edit_now)).getText().toString());
                if (!isPhoneNumberValid(((EditText) mEditPhone.findViewById(R.id.edit_now)).getText().toString())){
                    toast("请输入正确的手机号码");
                    break;
                }
                updateUser.setPhoneNumber(((EditText) mEditPhone.findViewById(R.id.edit_now)).getText().toString());
                updateUser.setAddress(((EditText) mEditAddress.findViewById(R.id.edit_now)).getText().toString());
                updateUser.setSelfIntroduce(((EditText) mEditIntroduction.findViewById(R.id.edit_now)).getText().toString());
                updateUser.setCertification(((EditText) mEditCetify.findViewById(R.id.edit_now)).getText().toString());
//                updateUser.setIsApply(currentUser.getIsApply());
//                updateUser.setIsOccupied(currentUser.getIsOccupied());
                ((TextView) mBtn_submit.findViewById(R.id.text)).setText("提交中...");
                updateUser.update(this, currentUser.getObjectId(), new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        toast("个人资料修改成功");
                        ((TextView) mBtn_submit.findViewById(R.id.text)).setText("提交资料");
                        PersonalProfileActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                initData();
                            }
                        });
                    }

                    @Override
                    public void onFailure(int arg0, String arg1) {
                        toast("修改失败");
                        ((TextView) mBtn_submit.findViewById(R.id.text)).setText("提交修改");
                    }
                });
                break;
            case R.id.back:
                finish();
                break;
            case R.id.id:
                bindEditText((EditText) mEditId.findViewById(R.id.edit_now));
                break;
            case R.id.phone:
                bindEditText((EditText) mEditPhone.findViewById(R.id.edit_now));
                break;
            case R.id.address:
                bindEditText((EditText) mEditAddress.findViewById(R.id.edit_now));
                break;
            case R.id.introduction:
                bindEditText((EditText) mEditIntroduction.findViewById(R.id.edit_now));
                break;
            case R.id.certification:
                bindEditText((EditText) mEditCetify.findViewById(R.id.edit_now));
                break;
            default:
                break;
        }
    }

    /* private void setCurUser() {
         User bmobUser = User.getCurrentUser(this, User.class);
         BmobQuery<User> query = new BmobQuery<User>();
         query.addWhereEqualTo("objectId", bmobUser.getObjectId());
         query.findObjects(this, new FindListener<User>() {

             @Override
             public void onSuccess(List<User> object) {
                 currentUser = object.get(0);
                 //toast("查询到用户  " + object.size());
                 Message msg = new Message();
                 msg.what = 200;
                 mHandler.sendMessage(msg);
             }

             @Override
             public void onError(int arg0, String arg1) {
                 toast("获取当前用户失败");
             }
         });
     }*/
    //设置姓名，等级不可更改。
    public void setUnchanged() {
        mEditName.findViewById(R.id.edit_now).setEnabled(false);
        mEditName.findViewById(R.id.edit_now).setFocusable(false);
        mEditLevel.findViewById(R.id.edit_now).setEnabled(false);
        mEditLevel.findViewById(R.id.edit_now).setFocusable(false);
    }

    public void bindEditText(EditText text) {
        text.requestFocusFromTouch();
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.showSoftInput(text, InputMethodManager.SHOW_IMPLICIT);
        text.setSelection(text.getText().length());

    }
}
