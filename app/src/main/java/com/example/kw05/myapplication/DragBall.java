package com.example.kw05.myapplication;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;

/**
 * Created by kw05 on 16/6/2.
 */
public class DragBall extends View {

    private Paint paint;//画笔
    private Canvas mCanvas;//画布
    //定义四个点和两个圆心,p1,p2,o1 属于固定圆
    private PointF p1, p2, p3, p4, o1, o2;
    private float a, b;//连线三角形的直角边,斜边为两个圆心的距离
    private PointF centerPoint;//贝塞尔曲线的支撑点
    private Path path;
    private boolean isDrag;

    private int radius = 20;

    public DragBall(Context context) {
        this(context, null);
    }

    public DragBall(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragBall(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //初始化操作
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.RED);//设置红色画笔
        //定义四个点和两个圆心,p1,p2,o1 属于固定圆
//        p1 = new PointF();
//        p2 = new PointF();
//        p3 = new PointF();
//        p4 = new PointF();
        centerPoint = new PointF();
        path = new Path();
        o1 = new PointF(100.0f, 100.0f);//固定圆圆心
        o2 = new PointF(100f, 100.0f);//移动圆初始位置


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        if (isDrag) {
            //拖动时的相关操作
            canvas.drawCircle(100.0f, 100.0f, radius, paint);
            canvas.drawCircle(o2.x, o2.y, radius, paint);
//        } else {
//            mCanvas = canvas;
            //画圆
//            canvas.drawCircle(100.0f, 100.0f, radius, paint);
//            mCanvas.drawCircle(100.0f, 100.0f, radius, paint);
            //使用path 画出自定义曲线

//            path.moveTo(200f, 120f);
//            path.quadTo(150f, 100, 100f, 120);//画曲线
//            path.lineTo(100f, 80f);
//            path.quadTo(150f, 100, 200, 80);//画曲线
//            path.close();
//        }
        canvas.drawPath(path, paint);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
       final float x=event.getX();
        final float y=event.getY();
//        Log.d("TAG","MotionEvent");
        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                System.out.print("ACTION_DOWN");
                Log.d("TAG","ACTION_DOWN");
                isDrag=true;
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d("TAG","ACTION_MOVE");
                //获取当前坐标

                getPoints(x,y);
                break;
            case MotionEvent.ACTION_UP:
                Log.d("TAG","ACTION_UP");
                //松手后执行回弹动画
                ValueAnimator animator=ValueAnimator.ofFloat(1.0f);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                     float percent = animation.getAnimatedFraction();
                        Log.d("percent",percent+"");
                        float endX=x-(x-100)*percent;
                        float endY=y-(y-100)*percent;
                        getPoints(endX,endY);
                    }
                });
                animator.setInterpolator(new OvershootInterpolator());
                animator.setDuration(1000);
                animator.start();
                isDrag=false;
                break;
        }
        return true;
    }


    private void getPoints(float x, float y) {

        // 根据角度算出四角形的四个点
        float offsetX = (float) (radius * Math.sin(Math.atan((y - o1.y) / (x - o1.x))));
        float offsetY = (float) (radius * Math.cos(Math.atan((y - o1.y) / (x - o1.x))));

        float x1 = o1.x - offsetX;
        float y1 = o1.y + offsetY;


        float x2 = x - offsetX;
        float y2 = y + offsetY;

        float x3 = x + offsetX;
        float y3 = y - offsetY;

        float x4 = o1.x + offsetX;
        float y4 = o1.y - offsetY;
        o2.x=x;
        o2.y=y;

        path.reset();
        path.moveTo(x3, y3);
        path.quadTo(getCenterPoint(100f, 100f, o2.x, o2.y).x, getCenterPoint(100f, 100f, o2.x, o2.y).y, x4, y4);//画曲线
        path.lineTo(x1, y1);
        path.quadTo(getCenterPoint(100f, 100f, o2.x, o2.y).x, getCenterPoint(100f, 100f, o2.x, o2.y).y, x2, y2);//画曲线
        path.close();

        invalidate();//
    }

    /***
     * 贝塞尔曲线的支撑点 圆心连线的中点
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     */
    private PointF getCenterPoint(float x1, float y1, float x2, float y2) {
        float offsetX = x1 + (x2 - x1) / 2;
        float offsetY = y1 + (y2 - y1) / 2;
        return new PointF(offsetX, offsetY);
    }

}
