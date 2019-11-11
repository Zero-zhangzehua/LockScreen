package com.example.lockscreen.service;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import com.example.lockscreen.StyleDetailActivity;

/**
 * Created by S9023192 on 2019/8/7.
 * 作用：锁屏内部的服务
 */
public class LockScreenService extends Service {
    private IntentFilter mIntentFilter;
    private LockScreenReceiver mLockScreenReceiver;
    public LockScreenService() {
    }

    //内部类，锁屏的广播接收器
    class LockScreenReceiver extends BroadcastReceiver {
        //锁屏广播到来时，把页面覆盖上来
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("Service1", "onReceive:   接收到");
            //跳转到StyleDetailActivity
            Intent styleDetailActivityIntent = new Intent(LockScreenService.this, StyleDetailActivity.class);
            styleDetailActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, styleDetailActivityIntent, 0);
            try {
                pendingIntent.send();
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //注册锁屏广播
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        mLockScreenReceiver = new LockScreenReceiver();
        registerReceiver(mLockScreenReceiver, mIntentFilter);
        Log.i("Service1", "service ");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return   super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //服务被销毁时取消注册
        Log.d("Service1", "onDestroy: 服务被销毁 ");
        unregisterReceiver(mLockScreenReceiver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
