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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class EnvelopeView extends RelativeLayout {

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

    //触碰屏幕的次数
    private int mTemp;

    //设置Lotties的起始展示帧
    private int mLottieStartFrame;

    //飞机滑动解锁的终点Y坐标
    private float mTerminalY;

    //手指触碰飞机的左边界
    private float mLeft;

    //手指触碰飞机的右边界
    private float mRight;

    //飞机触碰起点的上边界
    private float mTop;

    //飞机触碰起点的下边界
    private float mButtom;

    //是否触碰到起点的标志
    private boolean mTouchFlag;

    //飞机是否装备完成
    private boolean mEquipFlag;

    //飞机装备好的帧数
    private int mEquipFrame;

    //飞机到达终点的帧数
    private int mTerminalFrame;

    //手指在轨道上滑动的标志
    private boolean mSlipFlag;

    //展示该View的Activity
    private AppCompatActivity mActivity;

    //BaseView类
    private BaseView mBaseView;

    public EnvelopeView(Context context) {
        this(context, null);
    }

    public EnvelopeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EnvelopeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        //加载视图布局
        mView = LayoutInflater.from(context).inflate(R.layout.lock_screen_interaction, this, true);
        mLottieView = mView.findViewById(R.id.lottie_interaction);
        mBaseView = new BaseView();

        mFilename = getResources().getString(R.string.envelope_lottie);
        mLottieStartFrame = getResources().getInteger(R.integer.envolope_lottieStartFrame);
        mLottieView.setAnimation(mFilename);
        mLottieView.setFrame(mLottieStartFrame);

        mContext = getContext();
        mLottieJson = mBaseView.getJson(mFilename,mContext);
        datafromJson(mLottieJson);

        mTouchFlag = false;
        mSlipFlag = false;
        mEquipFlag = false;

        mActivity =(AppCompatActivity)context;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //获取屏幕宽高
        mWidth = getWidth();
        mHeight = getHeight();

        //以屏幕大小适配各个边界
        mLeft = mWidth / 3;
        mRight = mWidth / 3 * 2;
        mTop = mHeight / 16 * 11;
        mButtom = mHeight / 20 * 19;
        mTerminalY = mHeight / 4;
    }

    public boolean onTouchEvent(MotionEvent event) {
        final float x = event.getRawX();
        final float y = event.getRawY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartX = x;
                mStartY = y;
                setEquipProgress();
                break;
            case MotionEvent.ACTION_MOVE:
                setFlyProgress(x,y);
                break;
            case MotionEvent.ACTION_UP: // 手指抬起时和异常取消时都是使用同一种方式判断是否解锁
            case MotionEvent.ACTION_CANCEL:
                handlerMoveEvent();
                break;

        }
        return true;
    }

    /*
     * 计算飞机装备时Lottie播放的对应帧数
     */
    private void setEquipProgress() {
        //判断是否触碰到飞机
        if(((mStartX >= mLeft) && (mStartX <= mRight)) && ((mStartY >= mTop) && (mStartY <= mButtom))) {
            mTouchFlag = true;
        }
        //判断是否达到装备好的那个帧
        if(mTouchFlag){
            mTemp++;
            if((mTemp <= (mEquipFrame - mLottieStartFrame) / 2)){
                int currentFrame = mLottieStartFrame + mTemp * 2;
                mLottieView.setFrame(currentFrame);
            }else{
                mEquipFlag = true;
            }
        }
    }

    /*
     * 计算飞机飞行时Lottie播放的对应帧数
     */
    private void setFlyProgress(float x,float y) {
        //判断手指是否在轨道上滑动
        if((x >= mLeft) && (x <= mRight)){
            mSlipFlag = true;
        }else{
           handlerMoveEvent();

        }
        //飞机装备完成且手指在轨道上滑动时
        if(mTouchFlag && mEquipFlag && mSlipFlag){
            //当飞机图层没有播放完时，根据手指位置设置飞机飞行图层的帧数
            if(mLottieView.getFrame() < mTerminalFrame){
                //计算滑动的距离
                float movelength = mStartY - y;
                //计算到达终点的路经长
                float terminallength = mStartY - mTerminalY;
                //播放比例
                double rate = movelength / terminallength;
                int currentFrame = (int) (rate * (mTerminalFrame - mEquipFrame) + mEquipFrame);
                mLottieView.setFrame(currentFrame);
            }else{//当飞机图层播放完时，让整个Lottie按照原来的进度继续播放
                mLottieView.setSpeed(1f);
                mLottieView.resumeAnimation();
            }
        }else{
            handlerMoveEvent();
        }
    }

    private void handlerMoveEvent() {
        //如果有滑动
        if(mSlipFlag && mEquipFlag){
            //当没有到达解锁的阈值，让Lottie倒放
            if(mLottieView.getFrame() < mTerminalFrame) {
                //只有当前帧与飞机装备好的帧有一定差距时，认为是有效的滑动，如果与飞机装备好的帧相差太小，认为是误触，不做任何处理
                if(mLottieView.getFrame() - mEquipFrame > 2){
                    mLottieView.setSpeed(-1.5f);
                    mLottieView.resumeAnimation();
                    mSlipFlag = false;
                    mEquipFlag = false;
                    mTemp = 0;
                }
            } else {//到达阈值即解锁
                mActivity.finish();
            }
        }
        mTouchFlag = false;
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
            // 获取layers数组的第5个json对象
            JSONObject info1 = layers.getJSONObject(5);
            // 读取info1对象里的ip字段值
            mEquipFrame = info1.getInt("ip") + mLottieStartFrame;
            // 读取info1对象里的op字段值
            mTerminalFrame = info1.getInt("op");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
