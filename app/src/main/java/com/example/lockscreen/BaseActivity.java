package com.example.lockscreen;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * @Description
 * @updateDes 2019/08/14:48
 */
public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * 隐藏导航栏、标题栏透明、取消系统锁屏
     */
    public void initWindow() {
        final Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD // 取消系统锁屏
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED); // 锁屏时仍显示
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE // 防止系统栏隐藏时activity大小发生变化
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY // 沉浸式
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION // 隐藏导航栏
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // 隐藏导航栏
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN); // 全屏
        window.setNavigationBarColor(Color.TRANSPARENT);
        window.setStatusBarColor(Color.TRANSPARENT);

        // 设置标题栏透明
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(0);
        }
    }
}
