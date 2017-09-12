package com.ckt.mobilesafeckt.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ckt.mobilesafeckt.R;
import com.ckt.mobilesafeckt.util.StreamUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SplashActivity  extends Activity{

    private TextView tvVersion;
    private TextView tvProgress;
    int mVersionCode;
    String mVersionDesc;
    private String mVersioname;
    private String mUrl;
    private  static final int CODE_UPDATE_DIALOG=0;
    private  static final int CODE_JESON_ERROR=1;
    private  static final int CODE_NETWORK_ERROR=2;
    private  static final int CODE_URL_ERROR=3;
    private  static final int CODE_ENTER_HOME=4;


    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case CODE_UPDATE_DIALOG:
                    showDialogUI();
                    break;
                case CODE_JESON_ERROR:
                    showToast("Json数据异常");
                    break;
                case CODE_NETWORK_ERROR:
                    showToast("网络异常");
                    break;
                case CODE_URL_ERROR:
                    showToast("URL异常");
                    break;
                case  CODE_ENTER_HOME:
                enterHome();
                break;
            }
        }
    };


    private void showToast(String message) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvVersion =findViewById(R.id.tv_version);
        tvProgress=findViewById(R.id.tv_Progress);
        tvVersion.setText("版本号："+getVersionCode());
        checkVersion();
    }

    private void checkVersion() {
        final long startTime=System.currentTimeMillis();
           new Thread(){

               HttpURLConnection urlConnection;
               Message msg;

               @Override
               public void run() {
                   super.run();
                   try {
                       msg = Message.obtain();
                       URL url=new URL("http://192.168.56.1/update.json");
                       urlConnection = (HttpURLConnection) url.openConnection();
                       urlConnection.setRequestMethod("GET");
                       urlConnection.setConnectTimeout(5000);
                       urlConnection.setReadTimeout(5000);
                       urlConnection.connect();
                       System.out.println("connect");
                       if(urlConnection.getResponseCode()==200){
                           InputStream inputStream = urlConnection.getInputStream();
                           String result=StreamUtils.readFromStream(inputStream);
                           System.out.println("读取网络数据 result="+result);
                           JSONObject object=new JSONObject(result);
                           mVersionCode = object.getInt("versioncode");
                           mVersionDesc = object.getString("description");
                           mVersioname =object.getString("versioname");
                           mUrl=object.getString("downloadurl");
                           System.out.println("description ="+ mVersionDesc);
                           if(mVersionCode>getVersionCode()){
                            msg.what=CODE_UPDATE_DIALOG;
                           }else{
                               msg.what=CODE_ENTER_HOME;
                           }
                       }else{
                           System.out.println("网络超时");
                           enterHome();
                           msg.what=CODE_NETWORK_ERROR;
                       }
                   }
                   catch (MalformedURLException e) {
                       e.printStackTrace();
                       msg.what=CODE_URL_ERROR;
                       System.out.println("url错误异常");
                       enterHome();
                   }
                   catch (IOException e) {
                       msg.what=CODE_NETWORK_ERROR;
                       System.out.println("网络异常");
                       e.printStackTrace();
                       enterHome();
                   } catch (JSONException e) {
                       e.printStackTrace();
                       msg.what=CODE_JESON_ERROR;
                       enterHome();
                       System.out.println("JSON 数据异常");
                   }finally {
                       long endTime=System.currentTimeMillis();
                       long usedTime=endTime-startTime;
                       if(usedTime<2000){
                           try {
                               Thread.sleep(2000-usedTime);
                           } catch (InterruptedException e) {
                               e.printStackTrace();
                           }
                       }
                       handler.sendMessage(msg);
                       if(urlConnection!=null){
                           urlConnection.disconnect();
                       }
                   }
               }
           }.start();
    }

    private void enterHome() {
        Intent intent=new Intent(this,HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void showDialogUI() {
    AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("最新版本"+mVersionCode);
        builder.setMessage(mVersionDesc);
        builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                System.out.println("立即更新");
                downloadApk();
            }
        });
        builder.setNegativeButton("以后再说", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                enterHome();
            }
        });
        builder.show();
    }

    private void downloadApk() {
        final String target= Environment.getExternalStorageDirectory().getPath()+"/update.apk";
        System.out.println("mUrl===="+mUrl);
        HttpUtils util=new HttpUtils();
        util.download(mUrl, target, new RequestCallBack<File>() {

            @Override
            public void onLoading(long total, long current, boolean isUploading) {
                super.onLoading(total, current, isUploading);
                System.out.println("正在下载");
                tvProgress.setText("已经下载 "+current*100/total+"%");
            }

            @Override
            public void onSuccess(ResponseInfo<File> responseInfo) {
                System.out.println("下载成功");
                tvProgress.setVisibility(View.VISIBLE);
                Intent intent=new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(new File(target)),"application/vnd.android.package-archive");
                startActivityForResult(intent,0);
                showToast("下载成功");
            }

            @Override
            public void onFailure(HttpException e, String s) {
                System.out.println("下载失败"+e.toString());
             showToast("下载失败");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        enterHome();
        super.onActivityResult(requestCode, resultCode, data);
    }

    public  int getVersionCode() {
        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            int version=packageInfo.versionCode;
            return version;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
