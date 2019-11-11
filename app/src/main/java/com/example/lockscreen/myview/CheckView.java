package com.example.lockscreen.myview;

import android.animation.Animator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.example.lockscreen.R;

public class CheckView extends RelativeLayout {

    private LottieAnimationView mLottieView;

    //此视图布局
    private View mView;

    //手指按下时的坐标
    private float mStartX;
    private float mStartY;

    //屏幕宽和高
    private int mWidth;
    private int mHeight;

    //json文件名
    private String mFilename;

    //画勾的第一个点的坐标
    private float mFirstX;
    private float mFirstY;

    //画勾的第二个点的坐标
    private float mSecondX;
    private float mSecondY;

    //画勾的第三个点的坐标
    private float mThirdX;
    private float mThirdY;

    //点击次数
    private int mTemp;

    //json字符串1
    private String mLottieJson1;

    //json字符串2
    private String mLottieJson2;

    //json字符串3
    private String mLottieJson3;

    //json字符串4
    private String mLottieJson4;

    //json字符串5
    private String mLottieJson5;

    //json字符串6
    private String mLottieJson6;

    //json字符串7
    private String mLottieJson7;

    //拼接而成的json的字符串
    private String mAppendLottieJson;

    //展示该View的Activity
    private AppCompatActivity mActivity;

    //是否点击屏幕的标志
    private boolean mTouchFlag;

    //点击区域的上边界
    private float mFieldTop;

    //点击区域的下边界
    private float mFieldButtom;

    public CheckView(Context context) {
        this(context, null);
    }

    public CheckView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CheckView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        //加载视图布局
        mView = LayoutInflater.from(context).inflate(R.layout.lock_screen_interaction, this, true);
        mLottieView = mView.findViewById(R.id.lottie_interaction);
        mFilename = getResources().getString(R.string.check_lottie);
        mLottieView.setAnimation(mFilename);
        mLottieView.playAnimation();
        mLottieView.addAnimatorListener(myLottieListener);

        mActivity = (AppCompatActivity)context;

        mLottieJson1 = getResources().getString(R.string.check_lottiejson1);
        mLottieJson3 = getResources().getString(R.string.check_lottiejson3);
        mLottieJson5 = getResources().getString(R.string.check_lottiejson5);
        mLottieJson7 = getResources().getString(R.string.check_lottiejson7);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //获取屏幕宽高
        mWidth = getWidth();
        mHeight = getHeight();
        mFieldTop = mHeight / 7 * 2;
        mFieldButtom = mHeight / 9 * 7;
    }

    public boolean onTouchEvent(MotionEvent event) {
        final float x = event.getRawX();
        final float y = event.getRawY();

        // 手指位置与圆点之间的距离
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartX = x;
                mStartY = y;
                if(mStartY >= mFieldTop && mStartY <= mFieldButtom){
                    mLottieView.setFrame(0);
                    mLottieView.pauseAnimation();
                    mTemp++;
                    setLocation(mTemp);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP: // 手指抬起时和异常取消时都是使用同一种方式判断是否解锁
            case MotionEvent.ACTION_CANCEL:
                handlerMoveEvent();
                break;

        }
        return true;
    }

    /*
    * 设置画勾的三个点的坐标
     */
    private void setLocation(int temp){
        switch(temp){
            case 1:
                mFirstX = changeX(mStartX);
                mFirstY = changeY(mStartY);
                mLottieJson2 = "" + mFirstX + "," + mFirstY;
                break;
            case 2:
                mSecondX = changeX(mStartX);
                mSecondY = changeY(mStartY);
                mLottieJson4 = "" + mSecondX + "," + mSecondY;
                break;
            case 3:
                mThirdX = changeX(mStartX);
                mThirdY = changeY(mStartY);
                mLottieJson6 = "" + mThirdX + "," +mThirdY;
                break;
        }
    }

    /*
    *  手指抬起事件
     */
    private void handlerMoveEvent() {
        if(mTemp == 3){
            mAppendLottieJson = mLottieJson1 + mLottieJson2 + mLottieJson3 + mLottieJson4 + mLottieJson5 + mLottieJson6 + mLottieJson7;
            mLottieView.setAnimationFromJson(mAppendLottieJson,null);
            mLottieView.playAnimation();
            mTouchFlag = true;
            mTemp = 0;
        }
    }

    /*
    *将手机坐标系的左边转化为Lottie坐标系的坐标
     */
    private float changeX(float x){
        float lottieX;
        lottieX = (x - mWidth / 2) / 10;
        return lottieX;
    }

    private float changeY(float y){
        float lottieY;
        lottieY = (y - (mHeight / 2 - mHeight / 15)) / 10;
        return lottieY;
    }

    /**
     * 动画播放进度监听器
     */
    private Animator.AnimatorListener myLottieListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            if(mTouchFlag){
                mTouchFlag = false;
                mActivity.finish();
            }
        }

        @Override
        public void onAnimationCancel(Animator animation) {
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
        }
    };

}
