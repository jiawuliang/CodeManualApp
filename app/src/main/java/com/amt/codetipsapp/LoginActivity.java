package com.amt.codetipsapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.amt.codetipsapp.util.DBUtil;
import com.amt.codetipsapp.util.SharedPreferencesUtil;

import java.sql.ResultSet;
import java.sql.SQLException;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText userName;
    private EditText userPwd;

    private Button loginBtn = null;

    private CheckBox autoLoginBox = null;
    private CheckBox rememberPwdBox = null;

    private ImageView companyImage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        companyImage = findViewById(R.id.iv_company);
        companyImage.setImageResource(R.drawable.bg_login01);

        userName = (EditText) findViewById(R.id.userName);
        userPwd = (EditText) findViewById(R.id.userPwd);

        autoLoginBox = findViewById(R.id.cb_autoLogin);
        autoLogin();

        rememberPwdBox = (CheckBox) findViewById(R.id.cb_rememberPwd);
        // 是否记住了密码
        rememberPwd();

        loginBtn = (Button) findViewById(R.id.btn_login);

        loginBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                String userNameStr = userName.getText().toString();
                String userPwdStr = userPwd.getText().toString();
                if ("".equals(userNameStr.trim()) || userNameStr.equals(null)) {
                    Toast.makeText(this, "请输入用户名！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if ("".equals(userPwdStr.trim()) || userPwdStr.equals(null)) {
                    Toast.makeText(this, "请输入密码！", Toast.LENGTH_SHORT).show();
                    return;
                }
                login(userNameStr, userPwdStr);
                break;
            default:
                break;
        }
    }

    private void autoLogin() {
        boolean isAutoLogin = (boolean) SharedPreferencesUtil.getParam(this, "autoLogin", false);
        if (isAutoLogin) {
            autoLoginBox.setChecked(true);
            Intent intent = new Intent(LoginActivity.this, SearchActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void rememberPwd() {
        boolean isRemember = (boolean) SharedPreferencesUtil.getParam(this, "rememberPwd", false);
        if (isRemember) {
            userName.setText((String) SharedPreferencesUtil.getParam(this, "userName", ""));
            userPwd.setText((String) SharedPreferencesUtil.getParam(this, "userPwd", ""));
            rememberPwdBox.setChecked(true);
        }
    }

    private void login(final String userName, final String userPwd) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                String selectSql = "select userName, userPwd, userStatus from error_user where "
                        + "userName = '" + userName.trim()
                        + "' and userPwd = '" + userPwd.trim() + "'";
                ResultSet rs = DBUtil.getInstance().executeQuery(selectSql);
                Message msg = new Message();
                try {
                    if (rs.next()) {
                        if ("0".equals(rs.getString("userStatus"))) {
                            msg.what = 0;
                        } else {
                            if (autoLoginBox.isChecked()) {
                                SharedPreferencesUtil.setParam(getApplicationContext(), "autoLogin", true);
                            } else {
                                SharedPreferencesUtil.setParam(getApplicationContext(), "autoLogin", false);
                            }

                            if (rememberPwdBox.isChecked()) {
                                SharedPreferencesUtil.setParam(getApplicationContext(), "rememberPwd", true);
                                SharedPreferencesUtil.setParam(getApplicationContext(), "userName", userName);
                                SharedPreferencesUtil.setParam(getApplicationContext(), "userPwd", userPwd);
                            } else {
                                SharedPreferencesUtil.clear(getApplicationContext(), "rememberPwd");
                                SharedPreferencesUtil.clear(getApplicationContext(), "userName");
                                SharedPreferencesUtil.clear(getApplicationContext(), "userPwd");
//                                SharedPreferencesUtil.clearAll(getApplicationContext());
                            }

                            rs.close();
                            msg.what = 1;
                        }

                    } else {
                        msg.what = -1;
                    }
                } catch (SQLException e) {
                    msg.what = -2;
                    e.printStackTrace();
                }
                //发消息到主线程
                handler.sendMessage(msg);
            }
        }).start();

    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                Intent intent = new Intent(LoginActivity.this, SearchActivity.class);
                startActivity(intent);
                finish();
            } else if (msg.what == 0) {
                Toast.makeText(LoginActivity.this, "对不起，你的账号已经锁住，请联系管理员激活！", Toast.LENGTH_SHORT).show();
            } else if (msg.what == -1) {
                Toast.makeText(LoginActivity.this, "对不起，用户名或密码错误，请重新输入！", Toast.LENGTH_SHORT).show();
            } else if (msg.what == -2) {
                Toast.makeText(LoginActivity.this, "数据库连接失败，请检查数据库工具库！", Toast.LENGTH_SHORT).show();
            }
        }
    };

}
