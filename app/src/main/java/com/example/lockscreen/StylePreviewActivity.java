package com.example.lockscreen;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


public class StylePreviewActivity extends AppCompatActivity {

    public static final String LOTTIE_NAME = "name";
    public static final String IS_FROM_LOCAL = "is_from_local";
    //标志交互方式的交互码
    public static final String Interaction_code = "interaction_code";
    public static final String LOCAL_LOTTIE_JSON = "local_lottie_json";
    public static final String SHAREED_NAME = "config";
    public static final String SHAREED_LOTTIE_NAME = "lottie_name";
    public static final String SHAREED_INTERACTION_CODE = "interaction_code";
    public static final String SHAREED_LOTTIE_JSON = "lottie_json";
    public static final String SHAREED_IS_FROM_LOCAL = "is_from_local";


    public static final String[] mItemsNormal = new String[]{"向上滑动","向右滑动"};
    public static final String[] mItemsInteraction = new String[]{"向上滑动","向右滑动","手势交互"};
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};


    //Lottie 动画
    private LottieAnimationView mLottie;

    private SharedPreferences.Editor mEditor;
    private String mFileName;

    //标记当前动画是否是从本地读取的
    private boolean mIsFromLocal;

    //标记当前动画是否可交互
    private boolean mIsInteraction;

    //从本地读取到的json字符串
    private String mLottieJson;

    //可交互的Lottie的交互码
    private int mInteractionCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_style_preview);
        //获取Lottie文件的名字
        mFileName = getIntent().getStringExtra(LOTTIE_NAME);
        //获取控件
        mLottie = findViewById(R.id.preview_lottie);
        //获取数据库编辑器
        SharedPreferences sharedPreferences = getSharedPreferences(SHAREED_NAME, MODE_PRIVATE);
        mEditor = sharedPreferences.edit();
        //根据assets目录下的动画json文件名加载Lottie动画
        mLottie.setAnimation(mFileName);
        //获取读取sd卡权限
        int permission = ActivityCompat.checkSelfPermission(StylePreviewActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(StylePreviewActivity.this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        chooseInteraction();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("111","后台");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("111","后台");
    }

    //判断选中的是否为可交互的Lottie，并设置交互码
    private void chooseInteraction(){
        switch (mFileName){
            case "upload.json":
                mIsInteraction = true;
                mInteractionCode = 10;
                break;
            case "envelope.json":
                mIsInteraction = true;
                mInteractionCode = 20;
                break;
            case "check.json":
                mIsInteraction = true;
                mInteractionCode = 30;
                break;
            case "giftbox.json":
                mIsInteraction = true;
                mInteractionCode = 40;
                break;
            case "mark.json":
                mIsInteraction = true;
                mInteractionCode = 50;
                break;
        }
    }
    /**
     * 点击事件,返回到选择页面
     *
     * @param view
     */
    public void backToChange(View view) {
        finish();
    }

    /**
     * 点击事件,预览锁屏效果,跳转到styleDetailActivity
     *
     * @param view
     */
    public void previewDetail(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(StylePreviewActivity.this);
         builder.setTitle("选择预览的解锁方式");
         builder.setIcon(R.mipmap.ic_launcher);
        //如果是可交互的动画，添加可交互的解锁方式
        if(mIsInteraction){
            //添加列表
            builder.setItems(mItemsInteraction, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    jumpToPreview(i);

                }
            });
        }else{
            //添加列表
            builder.setItems(mItemsNormal, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    jumpToPreview(i);

                }
            });
        }
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * 跳转到锁屏页面预览
     *
     * @param code
     */
    private void jumpToPreview(int code) {
        Intent previewIntent = new Intent(StylePreviewActivity.this, StyleDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(Interaction_code,code + mInteractionCode);
        bundle.putString(LOTTIE_NAME, mFileName);
        if (mIsFromLocal) {
            bundle.putBoolean(IS_FROM_LOCAL, true);
            bundle.putString(LOCAL_LOTTIE_JSON, mLottieJson);
        }
        previewIntent.putExtras(bundle);
        startActivity(previewIntent);
    }

    /**
     * 点击事件,点击后弹出选择框选择框选择解锁方式
     * @param view
     */
    public void chooseDirection(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(StylePreviewActivity.this);
        builder.setTitle("选择你的解锁方式");
        builder.setIcon(R.mipmap.ic_launcher);
        //如果是可交互的动画，添加可交互的解锁方式
        if(mIsInteraction){
            //添加列表
            builder.setItems(mItemsInteraction, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    changeAnimation(i);
                }
            });
        }else{
            //添加列表
            builder.setItems(mItemsNormal, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    changeAnimation(i);
                }
            });
        }
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    /**
     * 修改要播放的动画和解锁方式，保存到SharedPreferences数据库中
     *
     * @param code
     */
    private void changeAnimation(int code) {
        if (mIsFromLocal) {
            //此时为从本地读取的lottie文件
            mEditor.putString(SHAREED_LOTTIE_JSON, mLottieJson);
            mEditor.putBoolean(SHAREED_IS_FROM_LOCAL, true);
        } else {
            //此时为从assets文件下读取的lottie文件
            mEditor.putString(SHAREED_LOTTIE_NAME, mFileName);
            mEditor.putBoolean(SHAREED_IS_FROM_LOCAL, false);
        }
        mEditor.putInt(SHAREED_INTERACTION_CODE, code + mInteractionCode);
        mEditor.commit();
    }

    /**
     * 点击事件，点击后从本地读取Lottie的json文件
     *
     * @param view
     */
    public void addFromLocal(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        //系统调用Action属性
        intent.setType("*/*");

        //设置文件类型
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // 添加Category属性
        try {
            startActivityForResult(intent, REQUEST_EXTERNAL_STORAGE);
        } catch (Exception e) {
            Toast.makeText(StylePreviewActivity.this, R.string.preview_activity_error_open, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //是否选择，没选择就不会继续
        if (resultCode == Activity.RESULT_OK) {
            //得到uri
            Uri uri = data.getData();
            String path = uri.getPath();
            String filePath = path;

            BufferedReader bufferedReader = null;
            try {
                bufferedReader = new BufferedReader(new FileReader(filePath));
                String content = null;
                StringBuilder stringBuilder = new StringBuilder();
                while ((content = bufferedReader.readLine()) != null) {
                    stringBuilder.append(content);
                }
                mLottieJson = stringBuilder.toString();
                mLottie.setAnimationFromJson(mLottieJson, null);
                mIsFromLocal = true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 释放资源
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLottie.cancelAnimation();
    }
}