package com.example.lockscreen;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * @author 涂成
 * @version 1.0
 * @updateAuthor 涂成
 * @Description
 * @updateDes 2019/08/15:19
 */
public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
    }

    /**
     * 点击事件，返回到StyleChangeActivity
     *
     * @param view
     */
    public void backToChange(View view) {
        finish();
    }
}
