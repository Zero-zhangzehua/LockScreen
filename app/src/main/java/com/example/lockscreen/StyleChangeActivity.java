package com.example.lockscreen;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.PathShape;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.lockscreen.adapter.GridAdapter;
import com.example.lockscreen.adapter.GridDividerItemDecoration;
import com.example.lockscreen.adapter.MenuContentAdapter;
import com.example.lockscreen.vo.MenuContent;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class StyleChangeActivity extends AppCompatActivity {

    private List<MenuContent> mList = new ArrayList<>();
    private ListView mListView;
    private DrawerLayout mDrawerLayout;
    //左侧目录
    private ImageButton mLeftmenu;
    private RecyclerView mRecyclerView;
    private GridAdapter mGridAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //获取控件
        mRecyclerView = findViewById(R.id.recycler);
        mLeftmenu = findViewById(R.id.left_menu);
        mDrawerLayout = findViewById(R.id.drawerlayout);
        mListView = findViewById(R.id.left_listview);

        //通过ShapeDrawable画导航栏左边详情图片
        drawDetailImage();

        //获取从WelcomeActivity传过来的Lottie名字list
        final List<String> lottieNames = getIntent().getExtras().getStringArrayList(WelcomeActivity.LOTTIE_NAMES);

        //设置布局方式,3列,垂直
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3, RecyclerView.VERTICAL, false);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mGridAdapter = new GridAdapter(StyleChangeActivity.this, lottieNames);
        //分割线设置
        mRecyclerView.addItemDecoration(new GridDividerItemDecoration(3, 3, Color.WHITE));

        //RecyclerView优化，避免整个布局绘制的测量
        mRecyclerView.setHasFixedSize(true);
        //RecyclerVeiw优化， 二级缓存，设置缓存的大小，保证加载过的复用
        mRecyclerView.setItemViewCacheSize(20);

        mRecyclerView.setAdapter(mGridAdapter);

        //RecyclerView点击事件,跳转到Lottie详情界面
        mGridAdapter.setItemClickListener(new GridAdapter.MyItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(StyleChangeActivity.this, StylePreviewActivity.class);
                intent.putExtra(StylePreviewActivity.LOTTIE_NAME, lottieNames.get(position));
                startActivity(intent);
            }
        });



        //初始化侧边栏中的数据
        initData();
        //给左侧菜单栏设置点击事件
        mListView.setOnItemClickListener(mOnItemClickListener);
        MenuContentAdapter menuContentAdapter = new MenuContentAdapter(StyleChangeActivity.this, mList);
        mListView.setAdapter(menuContentAdapter);
    }

    /**
     * 左侧菜单栏每个item的点击事件
     */
    private AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (position) {
                //item点击响应
                case 0:
                    //弹出弹框，提示用户是否去设置页面关闭系统锁屏
                    showAlertDialog();
                    break;
                case 1:
                    //切换关于的页面
                    Intent aboutIntent = new Intent(StyleChangeActivity.this, AboutActivity.class);
                    startActivity(aboutIntent);
                    break;
                default:
                    break;
            }
            //点击任一项item项，都关闭左侧菜单
            mDrawerLayout.closeDrawer(Gravity.LEFT);
        }
    };

    /**
     * 弹出弹框，提示用户是否去设置页面关闭系统锁屏
     */
    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.change_to_setting_text);
        builder.setPositiveButton(R.string.set_positive_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent settingIntent = new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);
                startActivity(settingIntent);
            }
        });
        builder.setNegativeButton(R.string.set_negative_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * 初始化侧边栏中的数据
     */
    private void initData() {
        mList.add(new MenuContent(R.drawable.setting_drawerlayout, getResources().getString(R.string.change_drawer_layout_setting)));
        mList.add(new MenuContent(R.drawable.about_drawerlayout, getResources().getString(R.string.change_drawer_layout_about)));
    }

    /**
     * 通过ShapeDrawable画导航栏左边详情图片
     */
    private void drawDetailImage() {
        Path path = new Path();
        path.moveTo(35, 35);
        path.lineTo(85, 35);
        path.moveTo(35, 55);
        path.lineTo(85, 55);
        path.moveTo(35, 75);
        path.lineTo(85, 75);
        path.close();

        PathShape pathShape = new PathShape(path, 100, 100);

        ShapeDrawable shapeDrawable = new ShapeDrawable(pathShape);
        shapeDrawable.getPaint().setStyle(Paint.Style.STROKE);
        shapeDrawable.getPaint().setColor(Color.BLACK);
        shapeDrawable.getPaint().setStrokeWidth(3f);
        mLeftmenu.setBackground(shapeDrawable);
    }

    /**
     * 左上角详情点击事件，点击弹出侧滑菜单
     *
     * @param view
     */
    public void jumpToLeft(View view) {
        mDrawerLayout.openDrawer(Gravity.LEFT);
    }
}
