package me.nice.locationviewdemo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.Objects;

public class LocationView extends View {


    private int outColor;
    private int insideColor;
    private int lineColor;
    private int jumpStyle;
    private int zoomStyle;
    private int DEFAULT_DURING = 1000;
    private boolean waterWave = true;

    private Paint outPaint;
    private Paint insidePaint;
    private Paint linePaint;
    private Paint wavePaint;
    private int outRadius;
    private int inSideRadius;
    private int waveRadius = 100;
    private int centerX;
    private int centerY;
    private int lineHeight = 150;

    private final double goldenSection = 0.618;


    public LocationView(Context context) {
        this(context, null);
    }

    public LocationView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LocationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final Resources resources = getResources();
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.LocationView, defStyleAttr, defStyleAttr);
        int indexCount = typedArray.getIndexCount();

        for (int i = 0; i < indexCount; i++) {
            int attr = typedArray.getIndex(i);

            switch (attr) {

                case R.styleable.LocationView_outColor:
                    outColor = Objects
                            .requireNonNull(typedArray.getColorStateList(i))
                            .getColorForState(getDrawableState(), 0);
                    break;
                case R.styleable.LocationView_insideColor:
                    insideColor = Objects
                            .requireNonNull(typedArray.getColorStateList(i))
                            .getColorForState(getDrawableState(), 0);
                    break;

                case R.styleable.LocationView_lineColor:
                    lineColor = Objects
                            .requireNonNull(typedArray.getColorStateList(i))
                            .getColorForState(getDrawableState(), 0);
                    break;

                case R.styleable.LocationView_waterWave:
                    waterWave = typedArray.getBoolean(i, waterWave);
                    break;
                case R.styleable.LocationView_jumpStyle:
                    jumpStyle = typedArray.getInt(i, context.getResources().getInteger(R.integer.DIDI));
                    break;

                case R.styleable.LocationView_zoomStyle:
                    zoomStyle = typedArray.getInt(i, context.getResources().getInteger(R.integer.MO_BAI));
                    break;
                default:
                    break;
            }
        }
        initPaint();
    }


    /**
     * 初始化画笔
     */
    private void initPaint() {
        outPaint = new Paint();
        insidePaint = new Paint();
        linePaint = new Paint();
        wavePaint = new Paint();
        linePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        outPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        insidePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        wavePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        linePaint.setStrokeWidth(10);
        linePaint.setColor(lineColor);
        outPaint.setColor(outColor);
        insidePaint.setColor(insideColor);
        wavePaint.setColor(outColor);
        wavePaint.setAlpha(100);
    }

    private int lineStarY;
    private int lineEndY;
    private int waveMaxRadius;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        outRadius = (int) (w / 4 * goldenSection);
        waveRadius = (int) (outRadius * goldenSection);
        waveMaxRadius = (int) (w * goldenSection);

        inSideRadius = outRadius / 4;
        centerX = w / 2;
        centerY = h / 2;
        lineHeight = (int) (w * goldenSection);
        lineStarY = centerX + getPaddingTop();
        lineEndY = centerX + getPaddingTop() + lineHeight;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int withMode = MeasureSpec.getMode(widthMeasureSpec);
        int withSpecSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        if (withMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST) {
            width = dip2px(getContext(), 120);
            height = (int) (width / goldenSection);
        }else if (withMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.EXACTLY){
            height = heightSpecSize;
            width = (int) (height * goldenSection);
        }else if (heightMode == MeasureSpec.AT_MOST && withMode == MeasureSpec.EXACTLY) {
            width = withSpecSize;
            height = (int) (width / goldenSection);
        }else {
            width = withSpecSize;
            height = heightSpecSize;
        }

        setMeasuredDimension(width, height);
    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawLine(canvas);

        canvas.drawCircle(centerX + getPaddingLeft(), centerX + getPaddingTop(), outRadius, outPaint);
        canvas.drawCircle(centerX + getPaddingLeft(), centerX + getPaddingTop(),  inSideRadius, insidePaint);

        drawWave(canvas);
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }


    public int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

//    public int px2dip(Context context, float pxValue) {
//        final float scale = context.getResources().getDisplayMetrics().density;
//        return (int) (pxValue / scale + 0.5f);
//    }

    /**
     * 画线
     * @param canvas
     */
    private void drawLine(Canvas canvas) {
        canvas.drawLine(centerX + getPaddingLeft(), lineStarY,
                centerX + getPaddingLeft(), lineEndY, linePaint);
    }

    /**
     * 绘制水波纹
     * @param canvas
     */
    private void drawWave(Canvas canvas) {

        canvas.drawCircle(centerX + getPaddingLeft(), lineEndY, waveRadius, wavePaint);

    }

    ValueAnimator outValueAnimator;

    /**
     * 开始外圆动画
     */
    public void startOutAnimation() {
        if (outValueAnimator!=null&&outValueAnimator.isRunning()) {
            return;
        }
        outValueAnimator = ValueAnimator.ofInt(outRadius / 4, outRadius - 10);
        outValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                inSideRadius = (int) animation.getAnimatedValue();
                invalidate();
                Log.d(LocationView.class.getSimpleName(), "动画的值 " + String.valueOf(animation.getAnimatedValue()));
            }

        });

        outValueAnimator.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
//                startWaveAnimation();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                startInsideAnimation();
            }
        });
        ValueAnimator.setFrameDelay(1000);
        outValueAnimator.start();
    }

//    private void startJump() {
//        ValueAnimator valueAnimator = ValueAnimator.ofInt(circleHeigh,circleHeigh - 25, circleHeigh);
//        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                circleHeigh = (int) animation.getAnimatedValue();
//                Log.d("deling", "circleHeigh动画的值 " + String.valueOf(animation.getAnimatedValue()));
//                invalidate();
//            }
//        });
//        valueAnimator.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                super.onAnimationEnd(animation);
//            }
//
//            @Override
//            public void onAnimationStart(Animator animation) {
//                super.onAnimationStart(animation);
//            }
//        });
//
//        ValueAnimator.setFrameDelay(1000);
//        valueAnimator.start();
//    }

    /**
     * 开始内圆动画
     */
    public void startInsideAnimation() {

        ValueAnimator valueAnimator = ValueAnimator.ofInt(outRadius - 10, outRadius / 4);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                inSideRadius = (int) animation.getAnimatedValue();
                invalidate();
            }
        });

        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
//                startJump();
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }
        });

        ValueAnimator.setFrameDelay(800);
        valueAnimator.start();

    }


    /**
     * 开始水波纹动画
     */
    public void startWaveAnimation() {

        ValueAnimator valueAnimator = ValueAnimator.ofInt(waveRadius, waveMaxRadius);

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                waveRadius = (int) animation.getAnimatedValue();
                invalidate();
            }
        });

        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                startWaveColorAnimation();
            }
        });
        valueAnimator.setDuration(800);
        valueAnimator.start();
    }

    /**
     * 开始水波纹颜色动画
     */
    public void startWaveColorAnimation() {

        ValueAnimator valueAnimator = ValueAnimator.ofInt(100, 0);

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                wavePaint.setAlpha((int) animation.getAnimatedValue());
                invalidate();
            }
        });

        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                waveRadius = (int) (outRadius * goldenSection);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }
        });
        valueAnimator.setDuration(800);
        valueAnimator.start();
    }


    /**
     * 滴滴风格缩放
     */
    public void startZoomDiDiStyleAnimation() {


    }


    /**
     * 摩拜风格缩放
     */
    public void startZoomMoBaiStyleAnimation() {


    }


    /**
     * 滴滴风格跳动
     */
    public void startJumpDiDiStyleAnimation() {


    }


    /**
     * 通用跳动
     */
    public void startJumpCommonAnimation() {


    }

}
