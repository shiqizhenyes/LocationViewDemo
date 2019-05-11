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
    private int during;
    private int DEFAULT_DURING = 1000;
    private Paint outPaint;
    private Paint insidePaint;
    private Paint linePaint;
    private Paint wavePaint;
    private int outRadius;
    private int circleHeigh;
    private int inSideRadius;
    private int waveRadius = 100;
    private int centerX;
    private int centerY;
    private int lineHeight = 150;


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
                case R.styleable.LocationView_during:
                    during = typedArray.getInt(i, DEFAULT_DURING);
                    break;
            }

        }

        initPaint();
    }


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

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        outRadius = w / 2;
        inSideRadius = outRadius / 4;
        centerX = w / 2;
        centerY = h / 2;
        circleHeigh = w / 2;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = measureWidth(getSuggestedMinimumWidth(), widthMeasureSpec);
        int height = measureHeight(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    private int measureWidth(int defaultWidth, int measereSpec) {
        int specMode = MeasureSpec.getMode(measereSpec);
        int specSize = MeasureSpec.getSize(measereSpec);
        switch (specMode) {
            case MeasureSpec.AT_MOST:
                defaultWidth = dip2px(getContext(), 150);
                break;
            case MeasureSpec.EXACTLY:
                defaultWidth = specSize;
                break;
            case MeasureSpec.UNSPECIFIED:
                defaultWidth = Math.max(defaultWidth, specSize);
                break;
        }
        return defaultWidth;
    }

    private int measureHeight(int defaultHeight, int measereSpec) {
        int specMode = MeasureSpec.getMode(measereSpec);
        int specSize = MeasureSpec.getSize(measereSpec);
        switch (specMode) {
            case MeasureSpec.AT_MOST:
                defaultHeight = dip2px(getContext(), 250);
                break;
            case MeasureSpec.EXACTLY:
                defaultHeight = specSize;
                break;
            case MeasureSpec.UNSPECIFIED:
                defaultHeight = Math.max(defaultHeight, specSize);
                break;
        }
        return defaultHeight;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawLine(canvas);
        canvas.drawCircle(centerX, circleHeigh, outRadius, outPaint);
        canvas.drawCircle(centerX, circleHeigh, inSideRadius, insidePaint);
        drawWave(canvas);



    }

    public int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    public int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    private void drawLine(Canvas canvas) {
        canvas.drawLine(centerX, circleHeigh * 2, centerX, circleHeigh * 2 + lineHeight, linePaint);

    }


    private void drawWave(Canvas canvas) {

        canvas.drawCircle(centerX, outRadius * 2 + lineHeight, waveRadius, wavePaint);

    }

    ValueAnimator valueAnimator;

    public void startAnimation() {
        if (valueAnimator!=null&&valueAnimator.isRunning()) {
            return;
        }
        valueAnimator = ValueAnimator.ofInt(outRadius / 4, outRadius - 10);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                inSideRadius = (int) animation.getAnimatedValue();
                invalidate();
                Log.d(LocationView.class.getSimpleName(), "动画的值 " + String.valueOf(animation.getAnimatedValue()));
            }

        });

        valueAnimator.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                startWaveAnimation();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                startInsideAnimation();

            }


        });
        ValueAnimator.setFrameDelay(during);
        valueAnimator.start();
    }

    private void startJump() {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(circleHeigh,circleHeigh - 25, circleHeigh);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                circleHeigh = (int) animation.getAnimatedValue();
                Log.d("deling", "circleHeigh动画的值 " + String.valueOf(animation.getAnimatedValue()));
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
            }
        });

        ValueAnimator.setFrameDelay(1000);
        valueAnimator.start();
    }

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
                startJump();
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }
        });

        ValueAnimator.setFrameDelay(1000);
        valueAnimator.start();

    }


    public void startWaveAnimation() {

        ValueAnimator valueAnimator = ValueAnimator.ofInt(waveRadius, waveRadius * 2);

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

        ValueAnimator.setFrameDelay(1000);
        valueAnimator.start();

    }

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
                waveRadius = 100;
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }
        });

        ValueAnimator.setFrameDelay(1000);
        valueAnimator.start();
    }


}
