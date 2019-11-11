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


public class UploadView extends RelativeLayout
{

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

    //圆心坐标
    private float mCenterX;
    private float mCenterY;

    //圆的半径
    private double mRadius;

    //json文件名
    private String mFilename;

    //json字符串
    private String mLottieJson;

    //相对转动的角度
    private double mDeltaAngle;

    //手指是否触碰起点的标志
    private boolean mFlag;

    //手指转动的圈数
    private int mTemp;

    //画勾图层开始的帧数,即进度条图层结束的帧数
    private int mFrame;

    //触碰起点的左边界坐标
    private float mStartLeft;

    //触碰起点的右边界坐标
    private float mStartRight;

    //触碰起点的下边界坐标
    private float mStartButtom;

    //触碰起点的上边界坐标
    private float mStartTop;

    //是否滑动的标志
    private boolean mSlip;

    //手指可以滑动的最大距离
    private double mMaxDistance;

    //距离可允许的偏移量
    private int mDistanceDeviation;

    //手指可以滑动的最小距离
    private double mMinDistance;

    //设置Lottie开始展示的帧
    private int mLottieStartFrame;

    //是否为顺时针滑动的标志
    private boolean mClockWiseFlag;

    //展示该View的Activity
    private AppCompatActivity mActivity;

    //手触碰原点的偏移量
    private int mOriginDeviation;

    //BaseView类
    private BaseView mBaseView;

    public UploadView(Context context) {
        this(context, null);
    }

    public UploadView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UploadView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        //加载视图布局
        mView = LayoutInflater.from(context).inflate(R.layout.lock_screen_interaction, this, true);
        mLottieView = mView.findViewById(R.id.lottie_interaction);
        mBaseView = new BaseView();

        mFilename = getResources().getString(R.string.upload_lottie);
        mLottieView.setAnimation(mFilename);
        mLottieStartFrame = getResources().getInteger(R.integer.upload_lottieStartFrame);
        mLottieView.setMinFrame(mLottieStartFrame);

        mContext = getContext();
        mLottieJson = mBaseView.getJson(mFilename,mContext);
        datafromJson(mLottieJson);

        mDistanceDeviation = getResources().getInteger(R.integer.upload_distancediviation);
        mMaxDistance = mRadius + mDistanceDeviation;
        mMinDistance = mRadius - mDistanceDeviation;

        mActivity = (AppCompatActivity) context;

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
            // 获取layers数组的第0个json对象
            JSONObject info1 = layers.getJSONObject(0);
            // 读取info1对象里的ip字段值
            mFrame = info1.getInt("ip");
            // 获取layers数组的第10个json对象
            JSONObject info2 = layers.getJSONObject(9);
            // 找到shapes的json数组
            JSONArray shapes = info2.getJSONArray("shapes");
            // 获取shapes数组的第0个json对象
            JSONObject info3 = shapes.getJSONObject(0);
            // 找到it的json数组
            JSONArray it = info3.getJSONArray("it");
            // 获取it数组的第1个json对象
            JSONObject info4 = it.getJSONObject(0);
            // 获取ks的json对象
            JSONObject ks = info4.getJSONObject("ks");
            // 获取ks的json对象
            JSONObject k = ks.getJSONObject("k");
            // 找到i的json数组i
            JSONArray i = k.getJSONArray("i");
            //找到i数组内的第0个json数组
            JSONArray i0 = i.getJSONArray(0);
            //获取数组中的第1个元素
            mRadius = i0.getDouble(1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //获取屏幕宽高
        mWidth = getWidth();
        mHeight = getHeight();
        mCenterX = mWidth / 2;
        mCenterY = mHeight / 2;

        mOriginDeviation = getResources().getInteger(R.integer.upload_origindiviation);
        mStartLeft = mCenterX - mOriginDeviation;
        mStartRight = mCenterX + mOriginDeviation;
        mStartTop = (float) (mCenterY - mRadius - mOriginDeviation);
        mStartButtom = (float) (mCenterY - mRadius +mOriginDeviation);
    }

    /*
    * 计算时相对旋转的角度的方法
     */
    private double calculateDegree(float downX,float downY){
        double angle;
        double length1 =Math.sqrt((downX - mCenterX) * (downX - mCenterX) + (downY - mCenterY) * (downY - mCenterY)) ;
        double length2 = Math.sqrt((downX - mCenterX) *(downX - mCenterX) + (downY - mCenterY + mRadius) * (downY - mCenterY + mRadius));
        double cos = ((mRadius * mRadius) + (length1 * length1) - (length2 * length2)) / (2 * mRadius * length1);
        angle = (Math.acos(cos) * 180) / Math.PI;
        //如果是钝角的情况
        if(downX < mCenterX){
            angle = 360 -  angle;
        }
        //转一圈的情况
        if(angle >= 357) {
            mTemp++;
            return (360 * mTemp + 360 - angle);
        }
        return 360 * mTemp + angle;
    }

    public boolean onTouchEvent(MotionEvent event) {
        final float x = event.getRawX();
        final float y = event.getRawY();

        // 手指位置与圆点之间的距离
        double distance = 0;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartX = x;
                mStartY = y;
                if(((mStartX <= mStartRight) && (mStartX >= mStartLeft)) && ((mStartY <= mStartButtom) && (mStartY >= mStartTop))){
                    mFlag = true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if(x > mStartX){
                    mClockWiseFlag = true;
                }
                if(mFlag && mClockWiseFlag){
                    distance = Math.sqrt((x - mCenterX) * (x - mCenterX) + (y - mCenterY) * (y - mCenterY));
                    mSlip = true;
                    if((distance <= mMaxDistance) && (distance >= mMinDistance)){
                        setProgress(x, y);
                    }else{//如果手指偏离太远，提前触发handlerMoveEvent();
                        handlerMoveEvent();
                    }
                }
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
    private void setProgress(float x, float y) {
        mDeltaAngle = calculateDegree(x, y);
        //当转动的角度没有到达阈值时，根据手指位置设置进度条图层的帧数
        if(mDeltaAngle <= 330){
            double lottieScale = mDeltaAngle / 330;
            int lottieFrame = (int)(lottieScale * mFrame);
            mLottieView.setFrame(lottieFrame);
        } else {//到达阈值时，让整个Lottie按照原来的进度继续播放
            mLottieView.setSpeed(1f);
            mLottieView.resumeAnimation();

        }
    }

    private void handlerMoveEvent() {
        //如果有滑动
        if(mSlip && mFlag){
            //当没有到达解锁的阈值，让Lottie倒放
            if(mLottieView.getFrame() < mFrame - 5) {
                if(mLottieView.getFrame() > mLottieStartFrame) {//防止误触，帧数与起始帧比有变化时才会回放
                    mLottieView.setSpeed(-1f);
                    mLottieView.resumeAnimation();
                }else{
                    mLottieView.pauseAnimation();
                }
            }else{
                mLottieView.addAnimatorListener(myLottieListener);
            }
            mSlip = false;
        }
        mFlag = false;
        mClockWiseFlag = false;
        mTemp = 0;
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
            mLottieView.setSpeed(1f);
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


