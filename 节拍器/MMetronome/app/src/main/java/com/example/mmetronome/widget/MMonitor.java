package com.example.mmetronome.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import com.orhanobut.logger.Logger;

import java.util.Calendar;

import androidx.annotation.Nullable;

public class MMonitor extends View {
    //mBorderMiddlePaint 中间边框画笔 mBorderCentrePaint 最里面边框画笔
    private Paint mTextPaint,mPlayerTextPaint,mBorderPaint,mBorderMiddlePaint,mBorderCentrePaint;
    private float mWidth,mHeight;
    private float mCentreX,mCentreY;
    private RectF mBorderRectF,mBorderMiddleRectF,mBorderCentreRectF,mBorderRect4Rectf;
    //中间黑色边框距离控件的margin
    private float mBorderMarginLeft,mBorderMarginTop,mBorderMarginBottom;
    //最里面的红色边框距离黑色边框的距离
    private float mCentreMarginLeft,mCentreMarginTop,mCentreMarginBottom;

    private int mAngle = 0;

    private int mBorderColor = Color.parseColor("#90827f");//外边框的颜色
    private int mBorderMiddleColor = Color.parseColor("#000000");//中间边框的颜色 黑
    private int mBorderCentreColor = Color.parseColor("#877982");//最里面边框的颜色 褐
    private int mBorderRect4Color = Color.parseColor("#e43175");//最里面边框4个tap的颜色 红


    private int mAppTextColor = Color.parseColor("#eee224");//外边框app名字的颜色
    private int mPlayerTextColor = Color.parseColor("#00f4ff");//里边框 bpm的颜色 蓝色

    public MMonitor(Context context) {
        this(context,null);
    }

    public MMonitor(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MMonitor(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();

    }

    private void initView() {
        mBorderPaint = new Paint();
        mBorderPaint .setAntiAlias(true);
        mBorderPaint.setColor(mBorderColor);

        mTextPaint = new Paint();
        mTextPaint .setAntiAlias(true);
        mTextPaint.setColor(mAppTextColor);

        mPlayerTextPaint = new Paint();
        mPlayerTextPaint .setAntiAlias(true);
        mPlayerTextPaint.setColor(mPlayerTextColor);

        mBorderMiddlePaint = new Paint();
        mBorderMiddlePaint .setAntiAlias(true);
        mBorderMiddlePaint.setColor(mBorderMiddleColor);

        mBorderCentrePaint = new Paint();
        mBorderCentrePaint .setAntiAlias(true);
        mBorderCentrePaint.setColor(mBorderCentreColor);
        //mBorderMarginTop = dpToPx(15);
        //mBorderMarginBottom = dpToPx(4);
    }
    //bpm数值
    public void setAngle(float mAngle) {
        this.mAngle = (int) mAngle;
        invalidate();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed){
            initMPlayerArea();
        }

    }
    //初始化显示器尺寸信息
    private void initMPlayerArea() {
        mWidth = getWidth();
        mHeight = getHeight();
        Logger.i("onSizeChanged mWidth="+mWidth +":mHeight="+mHeight);
        mCentreX = mWidth / 2;
        mCentreY = mHeight / 2;
        mBorderMarginLeft = dpToPx(8);
        mBorderMarginTop = mHeight * 1 / 5;
        mBorderMarginBottom = mBorderMarginTop * 1 / 5;

        mCentreMarginLeft = dpToPx(12);
        mCentreMarginTop = dpToPx(5);
        mCentreMarginBottom = dpToPx(6);

        mBorderRectF = new RectF();
        mBorderRectF.set(0,0,mWidth,mHeight);

        mBorderMiddleRectF = new RectF(mBorderMarginLeft, mBorderMarginTop, mWidth - mBorderMarginLeft, mHeight - mBorderMarginBottom);

        mBorderCentreRectF = new RectF(mBorderMarginLeft + mCentreMarginLeft, mBorderMarginTop + mCentreMarginTop,
                mWidth - mBorderMarginLeft - mCentreMarginLeft, mHeight - mBorderMarginBottom - mCentreMarginBottom);

        mBorderRect4Rectf = new RectF(mBorderMarginLeft + mCentreMarginLeft + mCentreMarginLeft, (mBorderMarginTop + mCentreMarginTop) * 2,
                mWidth - mBorderMarginLeft - mCentreMarginLeft - mCentreMarginLeft, mHeight - mBorderMarginBottom - mCentreMarginBottom - mCentreMarginBottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //画最外层的黄色边框
        canvas.drawRect(mBorderRectF,mBorderPaint);
        //画最外层的文字和时间等
        //drawborderText(canvas);
        //drawBorderTime(canvas);
        //画中间的黑色边框
        canvas.drawRect(mBorderMiddleRectF,mBorderMiddlePaint);

        canvas.drawRect(mBorderCentreRectF,mBorderCentrePaint);

        mBorderCentrePaint.setColor(mBorderRect4Color);
        canvas.drawRect(mBorderRect4Rectf,mBorderCentrePaint);

        mPlayerTextPaint.setTextSize(50);
        canvas.drawText("&"+mAngle,mBorderMarginLeft + mCentreMarginLeft +dpToPx(8),
                mBorderMarginTop + mCentreMarginTop+ dpToPx(10),mPlayerTextPaint);
    }

    private void drawborderText(Canvas canvas){
        mTextPaint.setTextSize(30);
        //设置字符间距
        mTextPaint.setLetterSpacing(0.2f);
        //粗体
        mTextPaint.setFakeBoldText(true);
        canvas.drawText("MMetronome",mBorderMarginLeft,mBorderMarginTop*1/2,mTextPaint);
    }

    private void drawBorderTime(Canvas canvas) {
        Calendar calendar = Calendar.getInstance();
        calendar.get(Calendar.HOUR_OF_DAY);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        //分钟
        int minute = calendar.get(Calendar.MINUTE);
        mTextPaint.setTextSize(20);
        canvas.drawText(hour+":"+minute,mBorderMarginLeft,mBorderMarginTop*1/2,mTextPaint);
    }




    /**
     * 转换 dp 至 px
     *
     * @param dpValue dp值
     * @return px值
     */
    protected int dpToPx(float dpValue) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        return (int) (dpValue * metrics.density + 0.5f);
    }
}
