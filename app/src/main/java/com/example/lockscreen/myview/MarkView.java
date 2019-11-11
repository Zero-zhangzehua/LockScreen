package com.example.lockscreen.myview;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;

public class MarkView extends RelativeLayout {

    private LottieAnimationView mLottieView;

    //上下文
    private Context mContext;

    private Calendar mCalendar;

    //此视图布局
    private View mView;

    //json文件名
    private String mFilename;

    //展示该View的Activity
    private AppCompatActivity mActivity;

    //json字符串
    private String mLottieJson;

    //手指按压开始的时间
    private long mStartTime;

    //手指按压结束的时间
    private long mEndTime;

    //Lottie图层的时长
    private double mDuration;

    //图层结束的帧数
    private int mEndFrame;

    //图层开始的帧数
    private int mBeginFrame;

    //设置Lottie的起始帧
    private int mLottieStartFrame;

    //Lottie的帧率
    private double mFrameRate;

    //毫秒数
    private int mMilliSecond;

    //BaseView类
    private BaseView mBaseView;

    public MarkView(Context context) {
        this(context, null);
    }

    public MarkView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MarkView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        //加载视图布局
        mView = LayoutInflater.from(context).inflate(R.layout.lock_screen_interaction, this, true);
        mLottieView = mView.findViewById(R.id.lottie_interaction);
        mBaseView = new BaseView();

        mFilename = getResources().getString(R.string.mark_lottie);
        mLottieView.setAnimation(mFilename);
        mLottieStartFrame = getResources().getInteger(R.integer.mark_lottieStartFrame);
        mLottieView.setMinFrame(mLottieStartFrame);

        mContext = getContext();
        mLottieJson = mBaseView.getJson(mFilename,mContext);
        datafromJson(mLottieJson);

        mMilliSecond = getResources().getInteger(R.integer.mark_milliSecond);
        mDuration = ((mEndFrame - mBeginFrame) / (mFrameRate)) * mMilliSecond;

        mActivity = (AppCompatActivity)context;

    }

    public boolean onTouchEvent(MotionEvent event) {
        final float x = event.getRawX();
        final float y = event.getRawY();

        // 手指位置与圆点之间的距离
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLottieView.setSpeed(1f);
                mCalendar = Calendar.getInstance();
                mStartTime = mCalendar.getTimeInMillis();
                break;
            case MotionEvent.ACTION_MOVE:
                setProgress();
                break;
            case MotionEvent.ACTION_UP: // 手指抬起时和异常取消时都是使用同一种方式判断是否解锁
            case MotionEvent.ACTION_CANCEL:
                handlerMoveEvent();
                break;
        }
        return true;
    }

    /*
    *Lottie播放帧数的设置
    */
    private void setProgress() {
        mCalendar = Calendar.getInstance();
        mEndTime = mCalendar.getTimeInMillis();
        //lottie播放比例
        float playrate;
        playrate = (float) ((mEndTime - mStartTime) / mDuration);
        mLottieView.setProgress(playrate);
    }

    /*
     *  手指抬起事件
     */
    private void handlerMoveEvent() {
        //没达到阈值就回放
        if(mLottieView.getFrame() < mEndFrame - 7){
            if(mLottieView.getFrame()  > mLottieStartFrame + 2){//给于误触，简单的点击不响应
                mLottieView.setSpeed(-1f);
                mLottieView.resumeAnimation();
            }
        }else{
            mActivity.finish();
        }
    }

    /*
     * 从json文件中获取数据
     */
    private void datafromJson(String lottiejson) {
        try {
            //创建一个包含原始json串的json
            JSONObject dataJson = new JSONObject(lottiejson);
            // 读取对象里的字段值
            mBeginFrame = dataJson.getInt("ip");
            mEndFrame = (int) dataJson.getDouble("op");
            mFrameRate = dataJson.getDouble("fr");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
