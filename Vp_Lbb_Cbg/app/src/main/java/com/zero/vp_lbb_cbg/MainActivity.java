package com.zero.vp_lbb_cbg;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.zero.vp_lbb_cbg.selfview.LoopButtons;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoopButtons.LoopButtonClickListener {

    private LoopButtons lb_show;
    private List<Integer> imgResource = new ArrayList<>();
    private Button btn_autoLoad;
    private boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        flag = getIntent().getBooleanExtra("load", false);
        initView();
        setButton();
        getData();
    }

    private void setButton() {
        if (flag) {
            //关闭状态
            btn_autoLoad.setText("关闭自动加载数据");
        } else {
            btn_autoLoad.setText("开启自动加载数据");
        }
    }

    //获取数据
    private void getData() {
        imgResource.clear();
        imgResource.add(R.mipmap.num_1);
        imgResource.add(R.mipmap.num_2);
        imgResource.add(R.mipmap.num_3);
        imgResource.add(R.mipmap.num_4);
        imgResource.add(R.mipmap.num_5);
        imgResource.add(R.mipmap.num_6);
        imgResource.add(R.mipmap.num_7);
        imgResource.add(R.mipmap.num_8);
        imgResource.add(R.mipmap.num_9);
        imgResource.add(R.mipmap.num_10);
        lb_show.setImages(imgResource);
    }

    //初始化显示控件
    private void initView() {
        lb_show = (LoopButtons) findViewById(R.id.lb_show);
        lb_show.setLoopButtonCount(7);
        lb_show.setLoopButtonClickListener(this);
        btn_autoLoad = (Button) findViewById(R.id.btn_autoLoad);
    }

    public void myClick(View view) {
        switch (view.getId()) {
            case R.id.btn_last:
                lb_show.last();
                break;
            case R.id.btn_next:
                lb_show.next();
                break;
            case R.id.btn_autoLoad:
                restart();
                break;
        }
    }

    public void restart() {
        Intent intent = new Intent(this, MainActivity.class);
        flag = flag ? false : true;
        intent.putExtra("load", flag);
        startActivity(intent);
        finish();
    }

    @Override
    public void getItem(int position) {
        Toast.makeText(this, "" + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void isLoadData(boolean isLoadData) {
        if (flag) {
            lb_show.addImages(imgResource);
        }
    }
}
