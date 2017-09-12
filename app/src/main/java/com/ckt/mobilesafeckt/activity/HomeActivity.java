package com.ckt.mobilesafeckt.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.ckt.mobilesafeckt.R;

public class HomeActivity extends Activity {

    GridView gridView;
    TextView tvItem;
    ImageView ivItem;
    private  String[] decription={"手机防盗","通讯卫士","软件管理",
            "进程管理","流量统计","手机杀毒",
            "缓存清理","高级工具","设置中心"};
    private int []imageID={R.mipmap.home_safe,R.mipmap.home_callmsgsafe,R.mipmap.home_apps,
            R.mipmap.home_taskmanager,R.mipmap.home_netmanager,R.mipmap.home_trojan,
            R.mipmap.home_sysoptimize,R.mipmap.home_tools,R.mipmap.home_settings
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        findViewById();
        initData();
    }

    private void initData() {

        gridView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return decription.length;
            }

            @Override
            public Object getItem(int i) {
                return  null;
            }

            @Override
            public long getItemId(int i) {
                return 0;
            }

            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {

                View v=View.inflate(HomeActivity.this,R.layout.home_item,null);
                tvItem=v.findViewById(R.id.tv_item);
                ivItem=v.findViewById(R.id.iv_item);

                tvItem.setText(decription[i]);
                ivItem.setImageResource(imageID[i]);
                return v;
            }
        });
    }

    private void findViewById() {
     gridView=findViewById(R.id.gv_home);
        tvItem=findViewById(R.id.tv_item);
        ivItem=findViewById(R.id.iv_item);
    }
}
