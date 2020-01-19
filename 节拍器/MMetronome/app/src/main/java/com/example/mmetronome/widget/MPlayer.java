package com.example.mmetronome.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import androidx.annotation.Nullable;

public class MPlayer extends View {
    private Paint mMarginCirclePaint,mMiddleCirclePaint,mCentreCirclePaint,mPaint;
    private Path mMarginCirclePath,mMiddleCirclePath,mCentreCirclePath;
    private float mCentreX,mCentreY;
    private float mWidth,mHeight;
    private float mMarginCircleRadius,mMiddleCircleRadius, mCentreCircleRadius;
    private int mMarginCirclColor = Color.parseColor("#778d9195");//外圆的颜色
    private int mMiddleCirclColor = Color.parseColor("#e8e9ea");//中间圆的颜色
    private int mMiddleCirclColorBlue = Color.parseColor("#2571ba"); //中间圆的环
    private int mCentreCirclColor = Color.parseColor("#ffffff"); //中间圆的环
    private float mStartX, mStartY;
    //旋转的角度
    private float mAngle = 0;

    /**
     * 滑动速度追踪
     */
    private VelocityTracker mVelocityTracker;
    //中心圆里的三角形的顶点到圆心的距离
    private float mTrianglelength;

    // 路径测量
    private PathMeasure mPathMeasure;
    // PathMeasure 测量过程中的坐标
    private float mPos[];
    // PathMeasure 测量过程中的正切
    private float mTan[];

    private OnSlidingListener mSlidingListener;
    private boolean isShouldBeGetY;
    public MPlayer(Context context) {
        this(context,null);
    }

    public MPlayer(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MPlayer(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }
    private void initView(){
        //Logger.i("initView");


        mMarginCirclePaint = new Paint();
        mMarginCirclePaint .setAntiAlias(true);
        mMarginCirclePaint.setColor(mMarginCirclColor);

        mMiddleCirclePaint = new Paint();
        mMiddleCirclePaint .setAntiAlias(true);


        mCentreCirclePaint = new Paint();
        mCentreCirclePaint .setAntiAlias(true);


        mPaint = new Paint();
        mPaint .setAntiAlias(true);
        //mMarginCirclePaint.setColor(mMarginCirclColor);

        mMarginCirclePath = new Path();

        mMiddleCirclePath = new Path();
        mCentreCirclePath = new Path();

        // 初始化 装载 坐标 和 正余弦 的数组
        mPos = new float[2];
        mTan = new float[2];

        // 初始化 PathMeasure 并且关联 圆路径
        mPathMeasure = new PathMeasure();

    }

    public void setSlidingListener(OnSlidingListener listener){
        mSlidingListener = listener;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //Logger.i("onsizeChanged");
//        mWidth = getMeasuredWidth();
//        mHeight = getMeasuredHeight();
//        Logger.i("onSizeChanged mWidth="+mWidth +":mHeight="+mHeight);
//        mCentreX = mWidth / 2;
//        mCentreY = mHeight / 2;
//        mMarginCircleRadius = mWidth / 3;
//        mMiddleCircleRadius = mMarginCircleRadius - dpToPx(20);
//        mCentreCircleRadius = mMarginCircleRadius * 1/3;
//
//        mTrianglelength = mCentreCircleRadius * 1 / 4;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x,y;
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
        //以控件左上角为原点
        x = event.getX();
        y = event.getY();
        //Logger.i("onTouchEvent="+x+":y:="+y);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //Logger.i("onTouchEvent ACTION_DOWN"+rawX+"::"+rawY);
                Logger.i("mCentreX="+mCentreX+": mCentreY="+mCentreY);
                //Logger.i("x="+x+"y="+y);
                //点击位置x坐标与圆心的x坐标的距离
                float distanceX = Math.abs(x-mCentreX);
                //点击位置y坐标与圆心的y坐标的距离
                float distanceY = Math.abs(y-mCentreY);
                //点击位置与圆心的直线距离
                boolean isInCircle = (int) Math.sqrt(Math.pow(distanceX,2)+Math.pow(distanceY,2)) > mMarginCircleRadius;
                //Toast.makeText(getContext(), "isInCircle="+isInCircle, Toast.LENGTH_SHORT).show();
                //如果点击位置与圆心的距离大于圆的半径，证明点击位置没有在圆内
                if(isInCircle){
                    //不处理此次点击事件
                    return false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                Logger.i("onTouchEvent ACTION_MOVE");
                 handleActionMove(x, y);
                 break;
            case MotionEvent.ACTION_UP:
                Logger.i("onTouchEvent ACTION_UP");
                break;
            default:break;
        }
        mStartX = x;
        mStartY = y;
            return true;
    }
    //旋转轮播
    private void handleActionMove(float x, float y) {
        //用来求斜边,因为第一个点不一定比第二个点小,这四个点相当与取这两个点的绝对值
        float pStartX,pStartY,pEndX,pEndY;
        if (mStartX < x) { // 最后的一个点在第一个点的右边
            pStartX = mStartX;
            pEndX = x;
        } else {
            pStartX = x;
            pEndX = mStartX;
        }
        if (mStartY < y) {
            pStartY = mStartY;
            pEndY = y;
        } else {
            pStartY = y;
            pEndY = mStartY;
        }
        //pA为第一个点
        float pA1 = Math.abs(mStartX - mCentreX);
        float pA2 = Math.abs(mStartY - mCentreY);
        //pB为第二个点
        float pB1 = Math.abs(x - mCentreX);
        float pB2 = Math.abs(y - mCentreY);
        //斜边
        float hypotenuse = (float) Math.sqrt(Math.pow(pEndX - pStartX, 2) + Math.pow(pEndY - pStartY, 2));
        //第一个点到中点的连线
        float lineA = (float) Math.sqrt(Math.pow(pA1, 2) + Math.pow(pA2, 2));
        //第二个点到中点的连线
        float lineB = (float) Math.sqrt(Math.pow(pB1, 2) + Math.pow(pB2, 2));
        //Logger.i("hypotenuse:lineA:lineB="+hypotenuse+"::"+lineA+"::"+lineB);
        if (hypotenuse > 0 && lineA > 0 && lineB > 0) {
            //求角度 余弦定理cosC = (a² + b² - c²) / 2ab
            mAngle += fixAngle((float) Math.toDegrees(Math.acos((Math.pow(lineA, 2) + Math.pow(lineB, 2) - Math.pow(hypotenuse, 2)) / (2 * lineA * lineB))));

            if (!Float.isNaN(mAngle)) {
                mSlidingListener.onSliding((isClockwise(x, y)) ? mAngle : -mAngle);
                invalidate();
                // postInvalidate();
                Logger.i("mAngle" + mAngle+":isClockwise="+isClockwise(x, y));
            }

        }


    }
    /**
     * 调整角度，使其在0-360之间
     *
     * @param rotation 当前角度
     * @return 调整后的角度
     */
    private float fixAngle(float rotation) {
        float angle = 360f;
        if (rotation < 0) {
            rotation += angle;
        }
        if (rotation > 0) {
            rotation %= angle;
        }
        return rotation;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed){
            initMPlayerArea();
        }

    }
    //初始化轮播尺寸信息
    private void initMPlayerArea() {
        mWidth = getWidth();
        mHeight = getHeight();
        Logger.i("onSizeChanged mWidth="+mWidth +":mHeight="+mHeight);
        mCentreX = mWidth / 2;
        mCentreY = mHeight / 2;
        mMarginCircleRadius = mWidth / 3;
        mMiddleCircleRadius = mMarginCircleRadius - dpToPx(20);
        mCentreCircleRadius = mMarginCircleRadius * 1/3;
        mTrianglelength = mCentreCircleRadius * 1 / 4;

    }


    @Override
    protected void onDraw(Canvas canvas) {
        Logger.i("onDraw");

        canvas.translate(mCentreX,mCentreY);
        //画外圆
        drawMarginCircle(canvas);
        //画外圆的边缘
        drawMarginCircleMarginal(canvas);
        //画中间的圆
        drawMiddleCircle(canvas);
        //画中心的圆
        //drawCentreCircle(canvas);
        canvas.drawText("mAngle"+mAngle,0,0,mCentreCirclePaint);
    }

    private void drawMarginCircle(Canvas canvas){
        mMarginCirclePaint.setColor(mMarginCirclColor);
        mMarginCirclePaint.setStyle(Paint.Style.FILL);
        mMarginCirclePath.addCircle(0,0,mMarginCircleRadius, Path.Direction.CW);
        canvas.drawPath(mMarginCirclePath,mMarginCirclePaint);
    }

    private void drawMarginCircleMarginal(Canvas canvas){
        mMarginCirclePaint.setStyle(Paint.Style.STROKE);
        mMarginCirclePaint.setStrokeWidth(3);
        mMarginCirclePaint.setColor(mMiddleCirclColor);

        mPathMeasure.setPath(mMarginCirclePath, false);

        canvas.save();
        if (!Float.isNaN(mAngle)) {
            Toast.makeText(getContext(), "mAngle="+mAngle , Toast.LENGTH_SHORT).show();
            canvas.rotate(mAngle);

        }
        for (int i = 1; i <= 12; i++) {
            mPathMeasure.getPosTan(mPathMeasure.getLength() * i / 12, mPos, mTan);
            canvas.drawLine(0,0,mPos[0],mPos[1],mMarginCirclePaint);
            //Logger.i("------------pos[0] = " + mPos[0] + "; pos[1] = " + mPos[1]);
            // Logger.i("------------tan[0](cos) = " + mTan[0] + "; tan[1](sin) = " + mTan[1]);
        }
        canvas.drawText("1",0,mMarginCircleRadius,mCentreCirclePaint);
        canvas.restore();
    }

    private void drawMiddleCircle(Canvas canvas){
        mMiddleCirclePaint.setColor(mMiddleCirclColor);
        mMiddleCirclePaint.setStyle(Paint.Style.FILL);
        mMiddleCirclePath.addCircle(0,0,mMiddleCircleRadius, Path.Direction.CW);
        canvas.drawPath(mMiddleCirclePath,mMiddleCirclePaint);
        mMiddleCirclePaint.setStyle(Paint.Style.STROKE);
        mMiddleCirclePaint.setStrokeWidth(4);
        mMiddleCirclePaint.setColor(mCentreCirclColor);
        //右边的三角
        mMiddleCirclePath.moveTo(mMiddleCircleRadius-45,-20);
        mMiddleCirclePath.lineTo(mMiddleCircleRadius-20,0);
        mMiddleCirclePath.lineTo(mMiddleCircleRadius-45,20);
        //左边的三角
        mMiddleCirclePath.moveTo(-mMiddleCircleRadius+45,-20);
        mMiddleCirclePath.lineTo(-mMiddleCircleRadius+20,0);
        mMiddleCirclePath.lineTo(-mMiddleCircleRadius+45,20);
        canvas.drawPath(mMiddleCirclePath,mMiddleCirclePaint);
        //设置中间圆的画笔 画蓝色的环
        mMiddleCirclePaint.setStrokeWidth(5);
        mMiddleCirclePaint.setColor(mMiddleCirclColorBlue);
        canvas.drawCircle(0,0,mMiddleCircleRadius,mMiddleCirclePaint);
    }

    private void drawCentreCircle(Canvas canvas) {
        mCentreCirclePaint.setColor(mCentreCirclColor);
        canvas.drawCircle(0,0,mCentreCircleRadius,mCentreCirclePaint);

        //Logger.i("mTrianglelength="+ mTrianglelength +"::"+(float) Math.pow(mTrianglelength, 2)+"::"+(float)Math.cos(120*2*Math.PI/360));
        //余弦定理求第三边 c*c = a*a +b*b -2abcos(ab的夹角),即hypotenuse为斜边(等边三角形的边长)的平方
        float hypotenuse =(float) Math.pow(mTrianglelength, 2)+ (float) Math.pow(mTrianglelength, 2) - 2*mTrianglelength*mTrianglelength*(float)Math.cos(120*2*Math.PI/360);
        float y = (float)Math.sqrt(hypotenuse)  * (float) Math.sin(Math.PI/6);
        float x = (float)Math.sqrt(hypotenuse) * (float) Math.cos(Math.PI / 6)-mTrianglelength;
        //Logger.i("hypotenuse="+ (float)Math.sqrt(hypotenuse) +" :y="+y+":x="+x);
        //mCentreCirclePaint.setStyle(Paint.Style.FILL);
        mCentreCirclePaint.setColor(mMiddleCirclColorBlue);
        //画等边三角形
        mCentreCirclePath.moveTo(-x, -y);
        mCentreCirclePath.lineTo(mTrianglelength, 0);
        mCentreCirclePath.lineTo(-x,y);
        mCentreCirclePath.close();
        canvas.drawPath(mCentreCirclePath,mCentreCirclePaint);
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

    /**
     * 检测手指是否顺时针滑动
     *
     * @param x 当前手指的x坐标
     * @param y 当前手指的y坐标
     * @return 是否顺时针
     */
    private boolean isClockwise(float x, float y) {
        return (isShouldBeGetY = Math.abs(y - mStartY) > Math.abs(x - mStartX)) ?
                x < mCentreX != y > mStartY : y < mCentreY == x > mStartX;
    }
    /**
     * 开始弧形滑动
     */
    public interface OnSlidingListener {
        /**
         * @param angle 本次滑动的角度
         */
        void onSliding(float angle);
    }


}
