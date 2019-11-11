package com.example.lockscreen.myview;

import android.animation.Animator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextClock;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.lockscreen.R;
import com.example.lockscreen.StyleDetailActivity;
import com.example.lockscreen.vo.CodeData;
import com.example.lockscreen.vo.ViewDisplay;
import com.example.lockscreen.vo.WordDisplay;

import androidx.appcompat.app.AppCompatActivity;

/**
 * @author 涂成
 * @version 1.0
 * @updateAuthor 涂成
 * @Description
 * @updateDes 2019/08/10:11
 */
public class LockScreenView extends RelativeLayout {


    private ProgressBar mProgressBar;
    private LottieAnimationView mLottieView;
    private RelativeLayout.LayoutParams mProgressBarLayout;

    private CodeData mCodeData;
    private ViewDisplay mViewDisplay;
    private Context mContext;

    //此视图布局
    private View mView;

    //屏幕宽和高
    private int mWidth;
    private int mHeight;

    //手指按下时的坐标
    private float mStartX;
    private float mStartY;

    //展示该View的Activity
    private AppCompatActivity mActivity;

    //显示进度条
    private final float SHOW_PROGRESS = 1f;

    //-1代表相对于父容器
    private final int SUBJECT = -1;

    //解锁方式
    private int mInteractionCode = 1;

    //正在播放动画的文件名(assets下加载)
    private String mLottieName;

    //正在播放动画的json字符串(本地加载)
    private String mLottieJson;

    public LockScreenView(Context context) {
        this(context, null);
    }


    public LockScreenView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LockScreenView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    /**
     * 初始化
     *
     * @param context
     * @param attrs
     */
    private void init(Context context, AttributeSet attrs) {
        mView = LayoutInflater.from(context).inflate(R.layout.lock_screen,this, true);
        mProgressBar = mView.findViewById(R.id.progressBar);
        mProgressBarLayout = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        mProgressBarLayout.addRule(RelativeLayout.CENTER_HORIZONTAL);
        mProgressBarLayout.addRule(RelativeLayout.ALIGN_BOTTOM, SUBJECT);

        //加载视图布局
        mLottieView = mView.findViewById(R.id.lock_screen_lottie);

        mLottieView.addAnimatorListener(myLottieListener);

        mActivity = (AppCompatActivity) context;


        //mContext = getContext();
        mCodeData = new CodeData();
        mViewDisplay = new ViewDisplay(mCodeData);

    }

    /**
     * 测量
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //测量子view
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        //获取屏幕宽高
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
    }

    /**
     * 布局
     *
     * @param changed
     * @param l
     * @param t
     * @param r
     * @param b
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        //设置滚动条的相对布局位置和相关属性
        mProgressBar.setMax(mWidth / 5 * 2);
        mProgressBarLayout.topMargin = mHeight / 10 * 9;
        mProgressBarLayout.width = mWidth / 5 * 2;
        mProgressBarLayout.height = mHeight / 100;
        mProgressBar.setLayoutParams(mProgressBarLayout);
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
        }

        @Override
        public void onAnimationCancel(Animator animation) {
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
            if (mLottieView.getSpeed() < 0) {
                //用户滑动屏幕后将速度恢复正常
                mLottieView.setSpeed(1f);
            }
        }
    };

    /**
     * 此方法会在所有的控件都从xml中加载完毕后加载
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    /**
     * 监听屏幕点击和移动事件
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final float x = event.getRawX();
        final float y = event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartX = x;
                mStartY = y;
                mLottieView.pauseAnimation();
                break;
            case MotionEvent.ACTION_MOVE:
                setProgress(x, y);
                break;
            case MotionEvent.ACTION_UP: // 手指抬起时和异常取消时都是使用同一种方式判断是否解锁
            case MotionEvent.ACTION_CANCEL:
                handlerMoveEvent(x, y);
                break;
        }
        return true;
    }

    /**
     * 根据手指触摸的情况更改进度条的进度和Lottie动画的播放状态
     *
     * @param x
     */
    private void setProgress(float x, float y) {
        float offset = getOffset(x, y);
        int progressScale = (int) (mProgressBar.getMax() * offset / mProgressBar.getMax());//进度条进度
        float lottieScale = offset / mProgressBar.getMax();//动画播放进度
        if (offset > 0) {
            //设置进度条进度
            mProgressBar.setAlpha(SHOW_PROGRESS);
            mProgressBar.setProgress(progressScale);
            //设置lottie播放进度
            if (lottieScale <= 1) {
                mLottieView.setProgress(lottieScale);
            } else {
                mLottieView.setProgress(1);
            }
        } else {
            mProgressBar.setAlpha(0);
        }
    }

    /**
     * 根据进度条进度判断是否解锁成功
     *
     * @param
     */
    private void handlerMoveEvent(float x, float y) {
        if (mProgressBar.getMax() == mProgressBar.getProgress()) {
            //解锁成功
            mActivity.finish();
        } else {
            float offset = getOffset(x, y);
            if (offset >= 0) {
                //未解锁
                //重置进度条
                mProgressBar.setProgress(0);
                mProgressBar.setAlpha(0);
                //倒放动画至初始位置
                mLottieView.setSpeed(-1f);
                mLottieView.resumeAnimation();
            }
        }
    }

    /**
     * 通过用户传进的交互码，设置屏幕解锁方式
     * @return
     */
    public void setDirection(int code) {
        mCodeData.setCode(code);
    }


    /**
     * 通过x，y坐标计算出想要的偏移量
     * @param x
     * @param y
     * @return
     */
    private float getOffset(float x, float y){
        float offset = 0;
        offset = mViewDisplay.offsetShow(mStartX , mStartY , x , y);
        return offset;
    }


    /**
     * 暴露此接口，用于播放从assets文件夹下加载的动画
     *
     * @param lottieName
     */
    public void setLottieName(String lottieName) {
        mLottieName = lottieName;
        mLottieView.setAnimation(mLottieName);
        invalidate();
    }

    /**
     * 暴露此接口，用于播放从本地加载的Lottie动画
     *
     * @param lottieJson
     */
    public void setLottieFromJson(String lottieJson) {
        mLottieJson = lottieJson;
        mLottieView.setAnimationFromJson(mLottieJson, null);
        invalidate();
    }

    /**
     * 暴露此接口，用于获取当前正在播放的动画名(assets下加载时)
     *
     * @return
     */
    public String getLottieName() {
        return mLottieName;
    }

    /**
     * 暴露此接口，用于获取当前正在播放的json字符串(从本地加载时)
     * @return
     */
    public String getLottieJson() {
        return mLottieJson;
    }
}
