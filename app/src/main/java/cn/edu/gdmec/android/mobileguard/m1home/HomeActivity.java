package cn.edu.gdmec.android.mobileguard.m1home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import cn.edu.gdmec.android.mobileguard.R;
import cn.edu.gdmec.android.mobileguard.m1home.adapter.HomeAdapter;
import cn.edu.gdmec.android.mobileguard.m2theftguard.Dialog.InterPasswordDialog;
import cn.edu.gdmec.android.mobileguard.m2theftguard.Dialog.SetUpPasswordDialog;
import cn.edu.gdmec.android.mobileguard.m2theftguard.utils.MD5Utils;

public class HomeActivity extends AppCompatActivity {
    private GridView gv_home;
    public long mExitTime;//点击时间
    private SharedPreferences msharedPerferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getSupportActionBar().hide();
        msharedPerferences=getSharedPreferences("config",MODE_PRIVATE);
        gv_home = (GridView) findViewById(R.id.gv_home);
        gv_home.setAdapter(new HomeAdapter(HomeActivity.this));
        gv_home.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        if(isSetUpPassword()){
                            showInterPswdDialog();
                            //Toast.makeText(HomeActivity.this,"打开登录窗口",Toast.LENGTH_SHORT).show();
                        }else{
                            showSetUpPswdDialog();
                            //Toast.makeText(HomeActivity.this,"打开设置窗口",Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
            }
        });
    }
    public void startActivity(Class<?> cls) {
        Intent intent = new Intent(HomeActivity.this, cls);
        startActivity(intent);
    }

    /*判断是否设置过手机防盗密码*/
    private boolean isSetUpPassword() {
        String password = msharedPerferences.getString("PhoneAntiTheftPWD", null);
        if (TextUtils.isEmpty(password)) {
            return false;
        }
        return true;
    }

    /*保存密码*/
    private void savePwsd(String affirmPwsd) {
        SharedPreferences.Editor editor = msharedPerferences.edit();
        //加密密码
        editor.putString("PhoneAntiTheftPWD", MD5Utils.encode(affirmPwsd));
        editor.commit();
    }

    /*获取密码*/
    private String getPassword() {
        String password = msharedPerferences.getString("PhoneAntiTheftPWD", null);
        if (TextUtils.isEmpty(password)) {
            return "";
        }
        return password;
    }

    /*弹出密码设置框*/
    private void showSetUpPswdDialog() {
        final SetUpPasswordDialog setUpPasswordDialog = new SetUpPasswordDialog(HomeActivity.this);
        setUpPasswordDialog.setCallBack(new SetUpPasswordDialog.MyCallBack() {
            @Override
            public void ok() {
                String firstPswd = setUpPasswordDialog.mFirstPWDET.getText().toString().trim(); //去掉前后空白
                String affirmPswd = setUpPasswordDialog.mAffirmET.getText().toString().trim();
                if (!TextUtils.isEmpty(firstPswd) && !TextUtils.isEmpty(affirmPswd)) {
                    if (firstPswd.equals(affirmPswd)) {
                        savePwsd(affirmPswd);
                        setUpPasswordDialog.dismiss();
                        showInterPswdDialog();
                    } else {
                        Toast.makeText(HomeActivity.this, "两次密码不一致", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(HomeActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void cancel() {
                setUpPasswordDialog.dismiss();
            }
        });
        setUpPasswordDialog.setCancelable(true);//按返回键可退出
        setUpPasswordDialog.show();
    }

    /*弹出输入密码框*/
    private void showInterPswdDialog() {
        final String password = getPassword();
        final InterPasswordDialog mInPswdDialog = new InterPasswordDialog(HomeActivity.this);
        mInPswdDialog.setCallBack(new InterPasswordDialog.MyCallBack() {

            @Override
            public void confirm() {
                if (TextUtils.isEmpty(mInPswdDialog.getPassword())) {
                    Toast.makeText(HomeActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                } else if (password.equals(MD5Utils.encode(mInPswdDialog.getPassword()))) {
                    mInPswdDialog.dismiss();
                    Toast.makeText(HomeActivity.this, "可以进入手机防盗模块", Toast.LENGTH_SHORT).show();
                } else {
                    mInPswdDialog.dismiss();
                    Toast.makeText(HomeActivity.this, "密码有误，请重新输入", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void cancel() {
                mInPswdDialog.dismiss();
            }
        });
        mInPswdDialog.setCancelable(true);
        mInPswdDialog.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(HomeActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            } else {
                System.exit(0);
            }
            return true;
        }
        //执行系统子自带的onKeyDown方法
        return super.onKeyDown(keyCode, event);
    }
}
