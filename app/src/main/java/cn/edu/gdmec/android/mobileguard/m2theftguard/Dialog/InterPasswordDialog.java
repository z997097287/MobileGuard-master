package cn.edu.gdmec.android.mobileguard.m2theftguard.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import cn.edu.gdmec.android.mobileguard.R;

/**
 * Created by zhuang zhu on 2017-10-07.
 */

public class InterPasswordDialog extends Dialog implements View.OnClickListener {
    private TextView mTitleTV;
    private EditText mInterET;
    private Button mOkBtn;
    private Button mCancelBtn;
    private MyCallBack myCallBack;
    public InterPasswordDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        setContentView(R.layout.inter_password_dialog);
        super.onCreate(savedInstanceState);
        initView();
    }
    private void initView(){
        mTitleTV = (TextView)findViewById(R.id.tv_interpwd_title);
        mInterET = (EditText)findViewById(R.id.et_inter_password);
        mOkBtn = (Button)findViewById(R.id.btn_comfirm);
        mCancelBtn = (Button)findViewById(R.id.btn_cancel);
        mOkBtn.setOnClickListener(this);
        mCancelBtn.setOnClickListener(this);
    }
    public void setTitle(String title){
        if(!TextUtils.isEmpty(title)){
            mTitleTV.setText(title);
        }
    }
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_comfirm:
                myCallBack.confirm();
                break;
            case R.id.btn_cancel:
                myCallBack.cancel();
                break;
        }
    }
    public String getPassword(){
        return mInterET.getText().toString();
    }
    public void setCallBack(MyCallBack myCallBack){
        this.myCallBack = myCallBack;
    }
    public interface MyCallBack{
        void confirm();
        void cancel();
    }
}
