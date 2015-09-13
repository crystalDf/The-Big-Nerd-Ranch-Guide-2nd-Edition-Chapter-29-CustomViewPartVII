package com.star.customviewpartvii;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

public class CustomVolumeControlBar extends View {

    private int mFirstColor;
    private int mSecondColor;
    private int mCircleWidth;
    private int mDotCount;
    private int mSpliteArcSize;
    private Bitmap mImage;

    private Paint mPaint;
    private RectF mOuterCircle;
    private RectF mInnerRectF;
    private int mCurrentCount;

    private float mYDown;
    private float mYUp;

    public CustomVolumeControlBar(Context context) {
        this(context, null);
    }

    public CustomVolumeControlBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomVolumeControlBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.CustomVolumeControlBar, defStyleAttr, 0);

        mFirstColor = typedArray.getColor(R.styleable.CustomVolumeControlBar_firstColor,
                Color.GREEN);
        mSecondColor = typedArray.getColor(R.styleable.CustomVolumeControlBar_secondColor,
                Color.RED);
        mCircleWidth = typedArray.getDimensionPixelSize(R.styleable.CustomVolumeControlBar_circleWidth,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20,
                        getResources().getDisplayMetrics()));
        mDotCount = typedArray.getInt(R.styleable.CustomVolumeControlBar_spliteArcSize, 20);
        mSpliteArcSize = typedArray.getInt(R.styleable.CustomVolumeControlBar_spliteArcSize, 20);
        mImage = BitmapFactory.decodeResource(getResources(),
                typedArray.getResourceId(R.styleable.CustomVolumeControlBar_image, 0));

        typedArray.recycle();

        mPaint = new Paint();
        mOuterCircle = new RectF();
        mInnerRectF = new RectF();
        mCurrentCount = 3;

    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(mCircleWidth);
        mPaint.setAntiAlias(true);

        int center = getMeasuredWidth() / 2;
        int radius = center - mCircleWidth / 2;
        int innerRadius = center - mCircleWidth;

        int innerRectHalf = (int) (innerRadius * Math.sqrt(2) / 2);

        drawVolumeCircle(canvas, center, radius);

        if (mImage.getWidth() < (innerRectHalf * 2)) {
            mInnerRectF.left = center - mImage.getWidth() / 2;
            mInnerRectF.top = center - mImage.getHeight() / 2;
            mInnerRectF.right = center + mImage.getWidth() / 2;
            mInnerRectF.bottom = center + mImage.getHeight() / 2;
        } else {
            mInnerRectF.left = center - innerRectHalf;
            mInnerRectF.top = center - innerRectHalf;
            mInnerRectF.right = center + innerRectHalf;
            mInnerRectF.bottom = center + innerRectHalf;
        }

        canvas.drawBitmap(mImage, null, mInnerRectF, mPaint);

    }

    private void drawVolumeCircle(Canvas canvas, int center, int radius) {

        float itemArcSize = (360 - mDotCount * mSpliteArcSize) / mDotCount;

        mOuterCircle.left = center - radius;
        mOuterCircle.top = center - radius;
        mOuterCircle.right = center + radius;
        mOuterCircle.bottom = center + radius;

        mPaint.setColor(mFirstColor);

        for (int i = 0; i < mCurrentCount; i++) {
            canvas.drawArc(mOuterCircle, i * (itemArcSize + mSpliteArcSize),
                    itemArcSize, false, mPaint);
        }

        mPaint.setColor(mSecondColor);

        for (int i = mCurrentCount; i < mDotCount; i++) {
            canvas.drawArc(mOuterCircle, i * (itemArcSize + mSpliteArcSize),
                    itemArcSize, false, mPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mYDown = event.getY();
                break;

            case MotionEvent.ACTION_UP:
                mYUp = event.getY();

                if (mYUp < mYDown && mCurrentCount < mDotCount) {
                    mCurrentCount++;
                } else if (mYUp > mYDown && mCurrentCount > 0) {
                    mCurrentCount--;
                }

                invalidate();
                break;
        }
        return true;
    }
}
