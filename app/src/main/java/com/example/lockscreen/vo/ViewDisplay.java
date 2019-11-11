package com.example.lockscreen.vo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.lockscreen.R;
import com.example.lockscreen.myview.BoxView;
import com.example.lockscreen.myview.CheckView;
import com.example.lockscreen.myview.EnvelopeView;
import com.example.lockscreen.myview.LockScreenView;
import com.example.lockscreen.myview.MarkView;
import com.example.lockscreen.myview.UploadView;

public class ViewDisplay implements Observer{
    private int mCode;
    private View mView;
    private CodeData mCodeData;
    private Context mContext;
    private UploadView mUploadView;
    private EnvelopeView mEnvolopeView;
    private CheckView mCheckView;
    private BoxView mBoxView;
    private MarkView mMarkView;
    private LockScreenView mLockScreenView;


    public ViewDisplay(CodeData codeData , Context context) {
        this.mCodeData = codeData;
        this.mContext = context;
        mCodeData.registerObserver(this);

//        mEnvolopeView = new EnvelopeView(mContext);
//        mUploadView = new UploadView(mContext);
//        mCheckView = new CheckView(mContext);
//        mBoxView = new BoxView(mContext);
//        mMarkView = new MarkView(mContext);
//        mLockScreenView = new LockScreenView(mContext);
    }

    public ViewDisplay(CodeData codeData) {
        this.mCodeData = codeData;
        mCodeData.registerObserver(this);

    }


    @Override
    public void update(int code) {
        mCode = code;
    }

    //设置自定义View的交互规则
//    public View viewShow(){
//        if(mCode == 12){
//            mView = mUploadView;
//        }else if(mCode == 22){
//            mView = mEnvolopeView;
//        }else if(mCode == 32){
//            mView = mCheckView;
//        }else if(mCode == 42){
//            mView = mBoxView;
//        }else if(mCode == 52){
//            mView = mMarkView;
//        }else{
//            mView = mLockScreenView;
//        }
//        return mView;
//    }

    public float offsetShow(float x1 , float y1 , float x2 , float y2){
        float offset = 0;
        switch (mCode){
            //向上滑动解锁
                case 0:
                case 10:
                case 20:
                case 30:
                case 40:
                case 50:
                    offset = y1 - y2;
                    break;
                //向右滑动解锁
                case 1:
                case 11:
                case 21:
                case 31:
                case 41:
                case 51:
                    offset = x2 - x1;
                    break;
            }
            return offset;
        }
    }
