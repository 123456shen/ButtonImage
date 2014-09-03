package com.zero.vp_lbb_cbg.selfview;

import java.util.ArrayList;
import java.util.List;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.zero.vp_lbb_cbg.R;

public class LoopButtons extends RelativeLayout {


    // 上下文对象
    private Context context;
    // 显示Button中第一个，既btnList的第二个元素的值,控制移动
    private int position = 0;
    // 选中Button的值,控制升降
    private int selectPosition = 0;
    // 按钮的高度
    private int btn_up, btn_down;
    // 上一个选中的Buttom
    private ImageView lastSeleteButton;
    // Image的数量
    private int btnCount = 7;
    // Image的占位宽度
    private int imageHoldWidth;
    // Image的显示宽度
    private int imageShowWidth;
    //接口对象，用于会掉点击事件
    private static LoopButtonClickListener listener;
    // Button的集合
    private ArrayList<ImageView> btnList = new ArrayList<>();

    private int screenHeight;

    // 数据源
    private ArrayList<Integer> imgs = new ArrayList<Integer>();

    public LoopButtons(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();
    }

    // 用来给底部的小button设置图片
    public void setImages(List<Integer> imgResource) {
        imgs.clear();
        addImages(imgResource);
        initImage();
    }

    public void addImages(List<Integer> imgResource) {
        imgs.addAll(imgResource);
    }

    //初始化图片的加载
    public void initImage() {
        for (int i = 1; i < btnList.size(); i++) {
            ImageView img = btnList.get(i);
            int posi = (int) img.getTag();
            if (posi >= 0 && posi < imgs.size()) {
                //TODO 图片的加载
                img.setImageResource(imgs.get(posi));
            }
        }
    }

    /***
     * 初始化视图
     */
    private void initView() {
        int width = getScreenWidth();
        // 各宽度设置
        imageHoldWidth = (int) width / btnCount;
        imageShowWidth = imageHoldWidth - 3;

        // 给高度制定之赋值
        btn_down = dip2px(context, 50);
        btn_up = dip2px(context, 0);
        // Button顺序添加
        for (int i = 0; i < btnCount; i++) {
            ImageView button = new ImageView(context);
            button.setBackgroundResource(R.mipmap.ic_launcher);
            LayoutParams params1 = new LayoutParams(
                    imageShowWidth, LayoutParams.MATCH_PARENT);
            params1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, -1);
            params1.leftMargin = imageHoldWidth * i + 5;
            button.setClickable(true);
            button.setTag(i);
            button.setOnClickListener(new MyOnClickListener());
            button.setLayoutParams(params1);
            button.setAdjustViewBounds(true);
            btnList.add(button);
            this.addView(button);
            down(button, 1);// 初始化后立马下移
        }

        // 设置左边屏幕外的Button
        LayoutParams params2 = new LayoutParams(imageShowWidth,
                LayoutParams.MATCH_PARENT);
        ImageView lastButton = new ImageView(context);
        lastButton.setBackgroundResource(R.mipmap.ic_launcher);
        lastButton.setTag(-1);
        lastButton.setClickable(true);
        lastButton.setAdjustViewBounds(true);
        lastButton.setOnClickListener(new MyOnClickListener());
        lastButton.setLayoutParams(params2);
        btnList.add(0, lastButton);
        this.addView(lastButton);
        ObjectAnimator moveLeft = ObjectAnimator.ofFloat(lastButton, "x", 0,
                -imageHoldWidth - 10);
        moveLeft.start();
        down(lastButton, 1);

        // 设置右边屏幕外的Button
        ImageView nextButton = new ImageView(context);
        nextButton.setClickable(true);
        nextButton.setTag(btnCount);
        nextButton.setBackgroundResource(R.mipmap.ic_launcher);
        nextButton.setAdjustViewBounds(true);
        nextButton.setOnClickListener(new MyOnClickListener());
        nextButton.setLayoutParams(params2);
        btnList.add(nextButton);
        this.addView(nextButton);
        ObjectAnimator moveRight = ObjectAnimator.ofFloat(nextButton, "x",
                width, btnCount * imageHoldWidth + 10);
        moveRight.start();
        down(nextButton, 1);
        // 开启默认第一个被选中的动画
        up(btnList.get(1));
        lastSeleteButton = btnList.get(1);
    }


    /***
     * 具体移动特效 -- 上一个
     */
    private void moveLast() {
        ObjectAnimator moveLeft = ObjectAnimator.ofFloat(
                btnList.get(btnList.size() - 1), "x", 2 * (-imageHoldWidth),
                -imageHoldWidth - 5);
        moveLeft.setDuration(1);
        moveLeft.start();
        for (int i = 0; i < btnList.size() - 1; i++) {
            ObjectAnimator move = ObjectAnimator.ofFloat(btnList.get(i), "x",
                    (i - 1) * imageHoldWidth, i * imageHoldWidth + 5);
            move.setDuration(1000);
            move.start();
        }
        btnList.add(0, btnList.get(btnList.size() - 1));
        btnList.remove(btnList.size() - 1);
    }

    /***
     * 具体移动特效 -- 下一个
     */
    private void moveNext() {
        ObjectAnimator moveRight = ObjectAnimator.ofFloat(btnList.get(0), "x",
                ((btnList.size() - 1) * imageHoldWidth), (btnList.size() - 2)
                        * imageHoldWidth + 5);
        moveRight.setDuration(1);
        moveRight.start();
        for (int i = 1; i < btnList.size(); i++) {
            ObjectAnimator move = ObjectAnimator.ofFloat(btnList.get(i), "x",
                    (i - 1) * imageHoldWidth, (i - 2) * imageHoldWidth + 3);
            move.setDuration(1000);
            move.start();
        }

        btnList.add(btnList.get(0));
        btnList.remove(0);
    }

    /***
     * 上下动作方法
     */
    private void jump() {
        ImageView currentButton = null;
        for (ImageView btn : btnList) {
            if (selectPosition == (int) btn.getTag()) {
                currentButton = btn;
            }
        }
        up(currentButton);
        down(lastSeleteButton, 1000);
        lastSeleteButton = currentButton;
    }

    // 进行移动操作 -- up
    private void up(ImageView currentButton) {
        ObjectAnimator up = ObjectAnimator.ofFloat(currentButton, "y",
                btn_down, btn_up);
        up.setDuration(1000);
        up.start();
    }

    // 进行移动操作 -- down
    private void down(ImageView lastSeleteButton2, int time) {
        ObjectAnimator down = ObjectAnimator.ofFloat(lastSeleteButton2, "y",
                btn_up, btn_down);
        down.setDuration(time);
        down.start();
    }

    /***
     * 获取屏幕宽度的方法
     */
    private int getScreenWidth() {
        DisplayMetrics dm = new DisplayMetrics();
        dm = getResources().getDisplayMetrics();
        // float density = dm.density;// 屏幕密度（像素比例：0.75/1.0/1.5/2.0）
        // int densityDPI = dm.densityDpi; // 屏幕密度（每寸像素：120/160/240/320）
        // float xdpi = dm.xdpi;
        // float ydpi = dm.ydpi;
        int screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
        return screenWidth;
    }

    /**
     * 自定义内部类点击监听
     */
    private class MyOnClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            int clickPosition = (int) v.getTag();
            //监听对象发送回掉
            if (LoopButtons.listener != null) {
                LoopButtons.listener.getItem(clickPosition);
            }
            //调用内部动画
            if (clickPosition - selectPosition > 0) {
                int num = clickPosition - selectPosition;
                for (int i = 0; i < num; i++) {
                    next();
                }
            } else {
                int num = selectPosition - clickPosition;
                for (int i = 0; i < num; i++) {
                    last();
                }
            }
        }
    }

    //TODO 对外公开方法和接口
    public interface LoopButtonClickListener {
        void getItem(int position);

        void isLoadData(boolean isLoadData);
    }

    public void setLoopButtonClickListener(LoopButtonClickListener listener) {
        this.listener = listener;
    }

    /***
     * 上一个移动
     */
    public void last() {
        if (selectPosition == 0) {
            Toast.makeText(context, "已经是第一个了", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectPosition <= btnCount / 2 || selectPosition > imgs.size() - 1 - btnCount / 2) {
            selectPosition--;
            jump();
            return;
        }
        selectPosition--;
        position--;
        btnList.get(btnList.size() - 1).setTag((position - 1));
        int posi = (int) btnList.get(btnList.size() - 1).getTag();
        if (posi >= 0 && posi < imgs.size()) {
            //TODO 图片的加载
            btnList.get(btnList.size() - 1).setImageResource(imgs.get(posi));
        }
        moveLast();
        jump();
    }

    /***
     * 下一个移动
     */
    public void next() {
        if (selectPosition == imgs.size() - btnCount) {
            this.listener.isLoadData(true);
        }
        if (selectPosition == imgs.size() - 1) {
            Toast.makeText(context, "已经是最后一个了", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectPosition >= imgs.size() - (btnCount / 2 + 1) || selectPosition < btnCount / 2) {
            selectPosition++;
            jump();
            return;
        }
        selectPosition++;
        position++;
        // 这里要减去添加的两个缓冲Button,否则会漏掉数据
        btnList.get(0).setTag(position + btnList.size() - 2);
        int posi = (int) btnList.get(0).getTag();
        if (posi >= 0 && posi < imgs.size()) {
            //TODO 图片的加载
            btnList.get(0).setImageResource(imgs.get(posi));
        }
        moveNext();
        jump();
    }

    public void setLoopButtonCount(int count) {
        this.btnCount = count;

    }

    /***
     * dip 转换为 px
     */
    private int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        int pxValue = (int) (dipValue * scale + 0.5f);
        return pxValue;
    }
}
