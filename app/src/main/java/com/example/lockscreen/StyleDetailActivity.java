package com.example.lockscreen;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.lockscreen.myview.BoxView;
import com.example.lockscreen.myview.CheckView;
import com.example.lockscreen.myview.EnvelopeView;
import com.example.lockscreen.myview.LockScreenView;
import com.example.lockscreen.myview.MarkView;
import com.example.lockscreen.myview.UploadView;
import com.example.lockscreen.vo.CodeData;
import com.example.lockscreen.vo.Observer;
import com.example.lockscreen.vo.ViewDisplay;
import com.example.lockscreen.vo.WordDisplay;


public class StyleDetailActivity extends BaseActivity {

    private IntentFilter mIntentFilter;
    private HomeReceiver mHomeReceiver;
    private LockScreenView mLockScreenView;
    private SharedPreferences mSharedPreferences;


    private UploadView mUploadView;
    private EnvelopeView mEnvolopeView;
    private CheckView mCheckView;
    private BoxView mBoxView;
    private MarkView mMarkView;

    private RelativeLayout mRelativeLayout;
    private Context mContext;

    private CodeData mCodeData;
    private WordDisplay mWordDisplay;
    private ViewDisplay mViewDisplay;

    private View minterView;


    //提示文字组件
    private TextView mTextView;

    //此视图布局
    private View mView;

    //交互码
    private int mInteracCode;

    //屏幕下方提示文字
    private String mText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_style_detail);
        mSharedPreferences = getSharedPreferences(StylePreviewActivity.SHAREED_NAME, MODE_PRIVATE);
        mView = LayoutInflater.from(this).inflate(R.layout.activity_style_detail,null, true);


        //初始化各可交互的自定义View，动态替换View
        mContext = this;
        mRelativeLayout = (RelativeLayout)findViewById(R.id.activity_main_detail);
        mEnvolopeView = new EnvelopeView(mContext);
        mUploadView = new UploadView(mContext);
        mCheckView = new CheckView(mContext);
        mBoxView = new BoxView(mContext);
        mMarkView = new MarkView(mContext);

        mCodeData = new CodeData();
        mWordDisplay = new WordDisplay(mCodeData , mContext);
        mViewDisplay = new ViewDisplay(mCodeData , mContext);

        mLockScreenView = new LockScreenView(mContext);
        mTextView = findViewById(R.id.lock_text);


        //设置解锁方式
        setLottieAndDirection();

        //屏蔽Home键，注册广播
        mIntentFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        mHomeReceiver = new HomeReceiver();
        registerReceiver(mHomeReceiver, mIntentFilter);
    }


    /**
     * 设置要播放的Lottie，并设置解锁方式
     */
    private void setLottieAndDirection() {
        Bundle bundle = getIntent().getExtras();
        String lottieName;
        if (bundle != null) {
            mInteracCode = bundle.getInt(StylePreviewActivity.Interaction_code);
            if(mInteracCode == 12){
                mRelativeLayout.addView(mUploadView);
            }else if(mInteracCode == 22){
                mRelativeLayout.addView(mEnvolopeView);
            }else if(mInteracCode == 32){
                mRelativeLayout.addView(mCheckView);
            }else if(mInteracCode == 42){
                mRelativeLayout.addView(mBoxView);
            }else if(mInteracCode == 52){
                mRelativeLayout.addView(mMarkView);
            }else{
                boolean isFromLocal = bundle.getBoolean(StylePreviewActivity.IS_FROM_LOCAL);
                if (isFromLocal) {
                    //从本地读取的lottie
                    String lottieJson = bundle.getString(StylePreviewActivity.LOCAL_LOTTIE_JSON);
                    mLockScreenView.setLottieFromJson(lottieJson);
                } else {
                    //从assets文件下读取的lottie
                    lottieName = bundle.getString(StylePreviewActivity.LOTTIE_NAME);
                    mLockScreenView.setLottieName(lottieName);
                }
                mRelativeLayout.addView(mLockScreenView);
            }
            //设置解锁方式
            mLockScreenView.setDirection(mInteracCode);
            //设置提示文字
            setTips(mInteracCode);
        } else {
            mInteracCode = mSharedPreferences.getInt(StylePreviewActivity.SHAREED_INTERACTION_CODE ,1);
            //此时为正常的锁屏启动
            //如果对于可交互的Lottie，选择了可交互的解锁方式
            if(mInteracCode == 12){
                mRelativeLayout.addView(mUploadView);
            }else if(mInteracCode == 22){
                mRelativeLayout.addView(mEnvolopeView);
            }else if(mInteracCode == 32){
                mRelativeLayout.addView(mCheckView);
            }else if(mInteracCode == 42){
                mRelativeLayout.addView(mBoxView);
            }else if(mInteracCode == 52){
                mRelativeLayout.addView(mMarkView);
            }else{
                boolean isFromLocal = mSharedPreferences.getBoolean(StylePreviewActivity.SHAREED_IS_FROM_LOCAL, false);//默认从assets下读取
                if (isFromLocal) {
                    //此时为从本地读取的lottie文件
                    String lottieJson = mSharedPreferences.getString(StylePreviewActivity.SHAREED_LOTTIE_JSON, null);//默认为空
                    mLockScreenView.setLottieFromJson(lottieJson);
                } else {
                    //此时为从assets读取的lottie文件
                    lottieName = mSharedPreferences.getString(StylePreviewActivity.SHAREED_LOTTIE_NAME, getResources().getString(R.string.default_lottie));
                    mLockScreenView.setLottieName(lottieName);
                }
                mRelativeLayout.addView(mLockScreenView);
            }
            //设置解锁方式
            mLockScreenView.setDirection(mInteracCode);
            //设置提示文字
            setTips(mInteracCode);
        }
    }

    /**
     * 提示文字
     * @param code
     */
    public void setTips(int code) {
        mCodeData.setCode(code);
        mText = mWordDisplay.setText();
        mTextView.setText(mText);
        mView.invalidate();
    }

    /**
     * 设置自定义View的交互规则
     * @param code
     */
    public void setInteraction(int code) {
        mCodeData.setCode(code);
        //mViewDisplay.viewShow();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //关闭导航栏等
        initWindow();
    }

    //屏蔽返回键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mHomeReceiver);
    }


    //Home键的广播
    class HomeReceiver extends BroadcastReceiver {
        static public final String SYSTEM_DIALOG_REASON_KEY = "reason";
        static public final String SYSTEM_DIALOG_REASON_GLOBAL_ACTIONS = "globalactions";
        static public final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
        static public final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
        static public final String SYSTEM_DIALOG_REASON_ASSIST = "assist";
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //按下Home键会发送ACTION_CLOSE_SYSTEM_DIALOGS的广播
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                if (reason != null) {
                    if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
                        //用循环防止上下文被弹出
                        for (int j = 0; j < 10; j++) {
                            Intent newIntent = new Intent(context, StyleDetailActivity.class);
                            //利用PendingIntent来配置Intent ，以实现立马跳转，无延迟跳转，且不会产生新的activity
                            newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, newIntent, 0);
                            try {
                                pendingIntent.send();
                            } catch (PendingIntent.CanceledException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }
}
