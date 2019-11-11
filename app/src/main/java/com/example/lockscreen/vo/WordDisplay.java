package com.example.lockscreen.vo;

import android.content.Context;

import com.example.lockscreen.R;

public class WordDisplay implements Observer {
    private int mCode;
    private CodeData mCodeData;
    private String mText;
    private Context mContext;

    public WordDisplay(CodeData codeData , Context context) {
        this.mCodeData = codeData;
        this.mContext = context;
        mCodeData.registerObserver(this);

    }
    @Override
    public void update(int code) {
        mCode = code;
        setText();
    }

    //改变提示文字
    public String setText(){
        switch (mCode){
            case 0:
            case 10:
            case 20:
            case 30:
            case 40:
            case 50:
                mText = mContext.getResources().getString(R.string.lock_text_up);
                break;
            case 1:
            case 11:
            case 21:
            case 31:
            case 41:
            case 51:
                mText = mContext.getResources().getString(R.string.lock_text_right);
                break;
            case 12:
                mText = mContext.getResources().getString(R.string.lock_text_upload);
                break;
            case 22:
                mText = mContext.getResources().getString(R.string.lock_text_envolope);
                break;
            case 32:
                mText = mContext.getResources().getString(R.string.lock_text_check);
                break;
            case 42:
                mText = mContext.getResources().getString(R.string.lock_text_box);
                break;
            case 52:
                mText = mContext.getResources().getString(R.string.lock_text_mark);
                break;
        }
        return mText;
    }
}