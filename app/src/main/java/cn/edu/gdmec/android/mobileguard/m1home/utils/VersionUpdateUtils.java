package cn.edu.gdmec.android.mobileguard.m1home.utils;

import android.app.Activity;

import android.content.DialogInterface;
import android.content.Intent;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import cn.edu.gdmec.android.mobileguard.R;
import cn.edu.gdmec.android.mobileguard.m1home.HomeActivity;
import cn.edu.gdmec.android.mobileguard.m1home.entity.VersionEntity;

/**
 * Created by zhuang zhu on 2017-09-24.
 */

public class VersionUpdateUtils {
    private String mVersion;
    private Activity context;
    private VersionEntity versionEntity;

    private static final int MESSAGE_IO_ERROR=102;
    private static final int MESSAGE_JSON_ERROR=103;
    private static final int MESSAGE_SHOW_DIALOG=104;
    private static final int MESSAGE_ENTER_HOME=105;
    //这里的Handerler引用自不同的包
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case MESSAGE_IO_ERROR:
                    Toast.makeText(context,"IO错误",Toast.LENGTH_LONG).show();
                    break;
                case MESSAGE_JSON_ERROR:
                    Toast.makeText(context,"JSON错误",Toast.LENGTH_LONG).show();
                    break;
                case MESSAGE_SHOW_DIALOG:
                    showUpdateDialog(versionEntity);
                    break;
                case MESSAGE_ENTER_HOME:
                    Intent intent = new Intent(context, HomeActivity.class);
                    context.startActivity(intent);
                    context.finish();
                    break;

            }
        }
    };

    public VersionUpdateUtils(String mVersion, Activity context) {
        this.mVersion = mVersion;
        this.context = context;
    }
    public void getCloudVersion(){
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpConnectionParams.setConnectionTimeout(httpClient.getParams(),5000);
            HttpConnectionParams.setSoTimeout(httpClient.getParams(),5000);
            HttpGet httpGet = new HttpGet("http://android2017.duapp.com/updateinfo.html");
            HttpResponse execute = httpClient.execute(httpGet);
            if(execute.getStatusLine().getStatusCode()==200){
                HttpEntity httpEntity = execute.getEntity();
                String result = EntityUtils.toString(httpEntity,"utf-8");
                JSONObject jsonObject = new JSONObject(result);
                versionEntity = new VersionEntity();
                versionEntity.versioncode=jsonObject.getString("code");
                versionEntity.description=jsonObject.getString("des");
                versionEntity.apkurl=jsonObject.getString("apkurl");
                if(!mVersion.equals(versionEntity.versioncode)){
                    //版本不同，需要升级
                    handler.sendEmptyMessage(MESSAGE_SHOW_DIALOG);
                    System.out.println("版本不同，需要升级");
                }
            }
        }catch(IOException e){
            e.printStackTrace();
            handler.sendEmptyMessage(MESSAGE_IO_ERROR);
        }catch(JSONException e){
            e.printStackTrace();
            handler.sendEmptyMessage(MESSAGE_JSON_ERROR);
        }
    }

    private void showUpdateDialog(final VersionEntity versionEntity){
        //这里引用宝也有不同选项
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("检查到有新版本"+versionEntity.versioncode);
        builder.setMessage(versionEntity.description);
        builder.setCancelable(false);
        builder.setIcon(R.mipmap.ic_launcher_round);
        System.out.println("检查到有新版本");
        builder.setPositiveButton("立刻升级",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface,int i){
                downloadNewApk(versionEntity.apkurl);
                //Toast.makeText(context,"肯能下载饿了",Toast.LENGTH_LONG).show();
            }
        });
        builder.setNegativeButton("暂不升级", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                enterHome();
            }
        });
        builder.show();
    }
    private void enterHome(){
        handler.sendEmptyMessage(MESSAGE_ENTER_HOME);
    }
    private void downloadNewApk(String apkurl){
        DownloadUtils downloadUtils = new DownloadUtils();
        downloadUtils.downloadApk(apkurl,"mobileguard.apk",context);
    }
}