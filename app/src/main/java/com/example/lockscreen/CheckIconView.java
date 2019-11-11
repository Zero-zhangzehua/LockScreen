package com.example.lockscreen;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.airbnb.lottie.LottieAnimationView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CheckIconView extends RelativeLayout {
    private LottieAnimationView mLottieView;

    //上下文
    private Context mContext;

    //此视图布局
    private View mView;

    //json文件名
    private String mFilename;

    //json字符串
    private String mLottieJson;

    //手指按下时的坐标
    private float mStartX;
    private float mStartY;

    //屏幕宽和高
    private int mWidth;
    private int mHeight;


    public CheckIconView(Context context) {
        this(context, null);
    }

    public CheckIconView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CheckIconView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        //加载视图布局
        mView = LayoutInflater.from(context).inflate(R.layout.lock_screen, this, true);
        //mLottieView = mView.findViewById(R.id.lottie);
        mFilename = "check.json";
        mLottieView.setAnimation(mFilename);
        mContext = getContext();
        mLottieJson = getJson(mFilename,mContext);
    }

    //将json数据变成字符串
    public static String getJson(String fileName,Context context) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            //获取assets资源管理器
            AssetManager assetManager = context.getAssets();
            //通过管理器打开文件并读取
            BufferedReader bf = new BufferedReader(new InputStreamReader(assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //获取屏幕宽高
        mWidth = getWidth();
        mHeight = getHeight();

    }

    public boolean onTouchEvent(MotionEvent event) {
        final float x = event.getRawX();
        final float y = event.getRawY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartX = x;
                mStartY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP: // 手指抬起时和异常取消时都是使用同一种方式判断是否解锁
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return true;
    }
}
