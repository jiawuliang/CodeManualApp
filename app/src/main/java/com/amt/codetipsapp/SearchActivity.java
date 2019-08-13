package com.amt.codetipsapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amt.codetipsapp.adapter.CodesAdapter;
import com.amt.codetipsapp.adapter.TipsAdapter;
import com.amt.codetipsapp.bean.ErrorTips;
import com.amt.codetipsapp.util.DBUtil;
import com.amt.codetipsapp.util.SharedPreferencesUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener,
        NavigationView.OnNavigationItemSelectedListener {

    private List<ErrorTips> tipsList = null;
    private List<ErrorTips> codesList = null;

    private ListView leftList = null;
    private ListView rightList = null;

    private ArrayAdapter<ErrorTips> leftAdapter = null;
    private ArrayAdapter<ErrorTips> rightAdapter = null;

    private Button searchBtn = null;
    private EditText searchTxt = null;

    private Toolbar toolbar = null;

    private NavigationView navView = null;

    private DrawerLayout mDrawerLayout = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = findViewById(R.id.layout_drawer);
        navView = findViewById(R.id.nav_view);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // 显示导航按钮
            actionBar.setDisplayHomeAsUpEnabled(true);
            // 设置导航按钮图标
            actionBar.setHomeAsUpIndicator(R.drawable.menu_drawer);
//            actionBar.setIcon(R.drawable.menu_drawer);
        }

        // 设置哪个链接被默认选中
//        navView.setCheckedItem(R.id.nav_call);
        navView.setNavigationItemSelectedListener(this);

        tipsList = new ArrayList<>();
        codesList = new ArrayList<>();

        leftList = (ListView) findViewById(R.id.list_left);
        rightList = (ListView) findViewById(R.id.list_right);

        searchTxt = (EditText) findViewById(R.id.txt_search);
        // 设置EditText的回车键功能
        searchTxt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND
                        || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    getCodesLikeCode(searchTxt.getText().toString().trim());
                    return true;
                }
                return false;
            }
        });

        searchBtn = (Button) findViewById(R.id.btn_search);
        searchBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_search:
                getCodesLikeCode(searchTxt.getText().toString().trim());
                break;
            default:
                break;
        }
    }

    // 加载toolbar.xml的menu菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);

        return true;
    }

    // 处理toolbar标题栏的各个按钮的点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // HomeAsUp按钮的id永远都是R.id.home
                // openDrawer方法显示滑动菜单
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.toolbar_logout:
                // 关闭自动登录
                SharedPreferencesUtil.clear(getApplicationContext(), "autoLogin");
                Intent intent = new Intent(SearchActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.toolbar_exit:
                // 关闭应用
                finish();
                break;
            default:
                break;
        }

        return true;
    }

    // 处理DrawerLayout的各个模板的点击事件
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_call:
                Toast.makeText(this, "You Click Call", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_email:
                Toast.makeText(this, "You Click Email", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_friends:
                Toast.makeText(this, "You Click Friends", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_logout:
                // 关闭自动登录
                SharedPreferencesUtil.clear(getApplicationContext(), "autoLogin");
                Intent intent = new Intent(SearchActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.nav_exit:
                finish();
                break;
            default:
                break;
        }
        // 关闭滑动菜单
        mDrawerLayout.closeDrawers();
        return true;
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                leftList.setVisibility(View.VISIBLE);
                rightList.setVisibility(View.VISIBLE);
                showLeft();
                showRight();
                leftList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        ErrorTips errorTips = codesList.get(position);
                        tipsList.clear();
                        tipsList.add(errorTips);
                        showRight();
                    }
                });
            } else if (msg.what == 0) {
                leftList.setVisibility(View.GONE);
                rightList.setVisibility(View.GONE);
            }
        }
    };

    private void getCodesLikeCode(final String searchCode) {
        if ("".equals(searchCode) || searchCode == null) {
            Toast.makeText(this, "对不起，请先输入错误码！", Toast.LENGTH_SHORT).show();
            return;
        }

        // 创建一个线程来连接数据库并获取数据库中对应表的数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 调用数据库工具类DBUtils的getInfoByName方法获取数据库表中数据

                String selectSql = "select * from error_tips where code like '" + searchCode + "%' or code like '%" + searchCode + "'";
//                String selectSql = "select * from error_tips where code like '%" + searchCode + "%'";

                ResultSet rs = DBUtil.getInstance().executeQuery(selectSql);

                if (rs != null) {
                    Message msg = new Message();
                    try {
                        if (rs.next()) {
                            codesList.clear();
                            ErrorTips errorTips;
                            do {
                                errorTips = new ErrorTips();
                                // 注意：下标是从1开始的
                                errorTips.setCode(rs.getString("code"));
                                errorTips.setUsedBy(rs.getString("usedBy"));
                                errorTips.setDisplay(rs.getString("display"));
                                errorTips.setCause(rs.getString("cause"));
                                errorTips.setRemedy(rs.getString("remedy"));

                                codesList.add(errorTips);
                            } while (rs.next());
                            tipsList.clear();
                            tipsList.add(codesList.get(0));
                            msg.what = 1;
                        } else {
                            msg.what = 0;
                        }
                    } catch (SQLException e) {
                        msg.what = -1;
                        Log.d(TAG, "run: CodesFragment.getTipsWithCode错误！");
                        e.printStackTrace();
                    }
                    //发消息到主线程
                    handler.sendMessage(msg);
                } else {
                    Log.d(TAG, "CodesFragment run: 数据库rs为空");
                }
            }
        }).start();
    }

    private void showLeft() {
        leftAdapter = new CodesAdapter(SearchActivity.this, R.layout.item_codes, codesList);
        leftList.setAdapter(leftAdapter);
    }

    private void showRight() {
        rightAdapter = new TipsAdapter(SearchActivity.this, R.layout.item_tips, tipsList);
        rightList.setAdapter(rightAdapter);
    }

}
