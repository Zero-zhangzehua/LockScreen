package com.example.lockscreen.myview;

import android.animation.Animator;
import android.content.Context;
import android.content.res.AssetManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.example.lockscreen.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class BoxView extends RelativeLayout {

    private LottieAnimationView mLottieView;

    //上下文
    private Context mContext;

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

    //展示该View的Activity
    private AppCompatActivity mActivity;

    //拉绳子图层的结束帧
    private int mEndFrame;

    //拉绳子的上边界
    private float mRopeTop;

    //拉绳子的下边界
    private float mRopeButtom;

    //触碰到绳子的标志
    private boolean mTouchFlag;

    //在绳子上滑动的标志
    private boolean mSlipFlag;

    //json字符串
    private String mLottieJson;

    //BaseView类
    private BaseView mBaseView;


    public BoxView(Context context) {
        this(context, null);
    }

    public BoxView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BoxView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        //加载视图布局
        mView = LayoutInflater.from(context).inflate(R.layout.lock_screen_interaction, this, true);
        mLottieView = mView.findViewById(R.id.lottie_interaction);
        mBaseView = new BaseView();

        mFilename = getResources().getString(R.string.giftbox_lottie);
        mContext = getContext();
        mLottieView.setAnimation(mFilename);
        mLottieJson = mBaseView.getJson(mFilename,mContext);
        datafromJson(mLottieJson);

        mActivity = (AppCompatActivity)context;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //获取屏幕宽高
        mWidth = getWidth();
        mHeight = getHeight();
        mRopeTop = mHeight / 20 * 11;
        mRopeButtom = mHeight / 13 * 8;
    }

    /*
     * 从json文件中获取数据
     */
    private void datafromJson(String lottiejson) {
        try {
            //创建一个包含原始json串的json
            JSONObject dataJson = new JSONObject(lottiejson);
            // 找到layers的json数组
            JSONArray layers = dataJson.getJSONArray("layers");
            // 获取layers数组的第1个json对象
            JSONObject info1 = layers.getJSONObject(1);
            // 读取info1对象里的op字段值
            int op = (int)info1.getDouble("op");
            //比结束帧稍小一点，手指离开时可以有缓冲播放
            mEndFrame = op - 5;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        final float x = event.getRawX();
        final float y = event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartX = x;
                mStartY = y;
                if(mStartY >= mRopeTop && mStartY <= mRopeButtom ){
                    mTouchFlag = true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if(y >= mRopeTop && y <= mRopeButtom) {
                    mSlipFlag = true;
                    }else{
                        mSlipFlag = false;
                    }
                setProgress(x);
                break;
            case MotionEvent.ACTION_UP: // 手指抬起时和异常取消时都是使用同一种方式判断是否解锁
            case MotionEvent.ACTION_CANCEL:
                handlerMoveEvent();
                break;
        }
        return true;
    }

    /*
    * 计算滑动时Lottie播放的对应帧数
     */
    private void setProgress(float x) {
        if(mTouchFlag){
            if(mSlipFlag){//手指在绳子附近滑动
                if(x - mStartX > 20){//设置误触距离，防止误触和向左移动
                    //手指滑动的距离
                    float length;
                    length = x - mStartX;
                    //Lottie播放比例
                    float playRate;
                    playRate = length / (mWidth / 2);
                    //当前播放的帧数
                    int frame;
                    frame = (int) (mEndFrame  * playRate);
                    //防止实时移动的帧大于最大值
                    if(frame > mEndFrame){
                        frame = mEndFrame;
                    }
                    mLottieView.setFrame(frame);
                }
            }else{//手指不在绳子附近滑动
                mLottieView.setSpeed(-0.5f);
                mLottieView.resumeAnimation();
                mTouchFlag = false;
            }
        }
    }

    /*
     *  手指抬起事件
     */
    private void handlerMoveEvent() {
        if(mTouchFlag && mSlipFlag){
            //还没到达阈值时，让Lottie倒放
            if(mLottieView.getFrame() < mEndFrame){//设置误触距离
                if(mLottieView.getFrame() > 3){
                    mLottieView.setSpeed(-0.5f);
                    mLottieView.resumeAnimation();
                }
            }else{
                mLottieView.setSpeed(0.1f);
                mLottieView.resumeAnimation();
                mLottieView.addAnimatorListener(myLottieListener);
            }
        }
        mTouchFlag = false;
        mSlipFlag = false;
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
            mActivity.finish();
        }

        @Override
        public void onAnimationCancel(Animator animation) {
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
        }
    };
}
