package com.example.lockscreen;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.airbnb.lottie.LottieAnimationView;
import com.example.lockscreen.service.LockScreenService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * 启动动画，播放随机Lottie，然后跳转
 */
public class WelcomeActivity extends BaseActivity {

    public static final String LOTTIE_NAMES = "Lottie_names";

    private LottieAnimationView mLottie;
    private View mPreLoadMainHome;

    private ArrayList<String> mList;
    private String[] mLotties;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        //启动服务
        startService(new Intent(this, LockScreenService.class));

        //关闭导航栏等
        initWindow();

        //获取Assets文件夹下所有的Lottie文件名
        getLottieNames();

        //随机选取Lottie动画播放
        randomPlay();

        //预加载主界面
        preLoadResource();

    }

    /**
     * 获取Assets文件夹下所有的Lottie文件名
     */
    private void getLottieNames() {
        //获取Lottie名字数组
        mList = new ArrayList<>();
        try {
            mLotties = getAssets().list("");
        } catch (IOException e) {
            e.printStackTrace();
        }
        //去除掉隐藏的文件夹名
        for (int i = 0; i < mLotties.length; i++) {
            if (mLotties[i].endsWith(".json")) {
                mList.add(mLotties[i]);
            }
        }
    }

    /**
     * 随机选取Lottie动画播放
     */
    private void randomPlay() {
        //通过随机数获取json文件名
        Random random = new Random();
        int temp = random.nextInt(mList.size());
        String filename = mList.get(temp);
        //设置动画属性
        mLottie = findViewById(R.id.welcome_lottie);
        mLottie.setAnimation(filename);

        //播放动画
        mLottie.playAnimation();
        mLottie.addAnimatorUpdateListener(myUpdateListener);
    }

    /**
     * 跳转到StyleChangeActivity,并携带Lottie文件名list
     */
    private void toStyleChangeActivity() {
        Intent jumpToChangeIntent = new Intent(this, StyleChangeActivity.class);
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(LOTTIE_NAMES, mList);
        jumpToChangeIntent.putExtras(bundle);
        startActivity(jumpToChangeIntent);
        finish();
    }

    /**
     * 动画更新进度的监听
     */
    private ValueAnimator.AnimatorUpdateListener myUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            //动画播放完毕，跳转StylePreview界面
            if (valueAnimator.getAnimatedFraction() == 1) {
                toStyleChangeActivity();
            }
        }
    };

    /**
     * 点击按钮跳过动画播放
     *
     * @param view
     */
    public void jump(View view) {
        mLottie.cancelAnimation();
        toStyleChangeActivity();
    }

    /**
     * 实现预加载主界面的Activity
     */
    private void preLoadResource() {
        if (mPreLoadMainHome == null) {
            mPreLoadMainHome = View.inflate(this, R.layout.activity_main, null);
        }
    }
}
