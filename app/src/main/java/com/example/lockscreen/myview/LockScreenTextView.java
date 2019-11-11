package com.example.lockscreen.myview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

/**
 * @author 涂成
 * @version 1.0
 * @updateAuthor 涂成
 * @Description 自定义view
 * @updateDes 2019/08/14:19
 */
public class LockScreenTextView extends AppCompatTextView {

    private int mViewWidth;
    private LinearGradient mLinearGradient;
    private Paint mPaint;
    private Matrix mGradientMatrix;
    private int mTranslate;

    public LockScreenTextView(Context context) {
        this(context, null);
    }

    public LockScreenTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LockScreenTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 测量View
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mViewWidth = getMeasuredWidth();
    }

    /**
     * 先在onSizeChanged()方法中进行一些初始化工作，根据View的宽来设置一个LinearGradient渐变渲染器
     * 此方法在onMeasure()方法调用完成后调用
     *
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mViewWidth > 0) {
            //获取当前绘制TextView的Paint对象
            mPaint = getPaint();
            //自定义渐变渲染器
            mLinearGradient = new LinearGradient(
                    0, 0, mViewWidth, 0, //渲染的起止坐标
                    new int[]{Color.BLACK, Color.WHITE, Color.BLACK},//定义渐变颜色
                    new float[]{0.25F, 0.5F, 1.0F},//不同渲染渲染阶段渲染不同的颜色
                    Shader.TileMode.CLAMP);//Shader渲染器
            //为画笔设置渐变渲染器
            mPaint.setShader(mLinearGradient);
            //创建一个单位矩阵
            mGradientMatrix = new Matrix();
        }
    }

    /**
     * 在onDraw方法中，通过矩阵的方法来不断平移渐变效果，使绘制文字时产生动态的闪动效果
     *
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mLinearGradient != null) {
            //每次向右平移字体长度的二十分之一
            mTranslate += mViewWidth / 20;
            if (mTranslate > 2 * mViewWidth) {
                mTranslate = -mViewWidth;
            }
            //设置单位矩阵平移
            mGradientMatrix.setTranslate(mTranslate, 0);
            //为着色器设置矩阵
            mLinearGradient.setLocalMatrix(mGradientMatrix);
            //循环等待时间
            postInvalidateDelayed(100);
        }
    }

}
