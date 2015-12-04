package com.example.administrator.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

/**
 * Created by Administrator on 2015/11/19.
 */
public class Luckypan extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private SurfaceHolder mHolder;
    private Canvas mCanvas;
    private Thread t;
    private boolean isRunning;

    private String[] mStrs = new String[]{"单反相机", "IPAD", "恭喜发财", "IPHONE", "服装", "恭喜发财"};
    private int[] mImgs = new int[]{R.drawable.danfan, R.drawable.ipad, R.drawable.ipad, R.drawable.iphone, R.drawable.meizi, R.drawable.f015
    };

    private int[] mColor = new int[]{0x8C3D0423, 0xfff12e01, 0x8C3D0423, 0xfff12e01, 0x8C3D0423, 0xfff12e01};

    private int mItemCount = 6;
    private Bitmap[] mImgsBitmap;
    private RectF mRange = new RectF();
    private int mRadius;

    private Paint mArcPaint;
    private Paint mTextPant;
    private Context context;


    private double mSpeed = 0;

    private volatile float mStartAngle = 0;

    private boolean isshouldEnd;

    private int mCenter;

    private int mPaddding;

    private Bitmap mBgBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bg2);

    private float mTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 20, getResources().getDisplayMetrics());


    public Luckypan(Context context) {
        this(context, null);
    }

    public Luckypan(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        mHolder = getHolder();
        mHolder.addCallback(this);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setKeepScreenOn(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = Math.min(getMeasuredWidth(), getMeasuredHeight());
        mPaddding = getPaddingLeft();
        mRadius = width - mPaddding * 2;
        mCenter = width / 2;
        setMeasuredDimension(width, width);

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        mArcPaint = new Paint();
        mArcPaint.setAntiAlias(true);
        mArcPaint.setDither(true);

        mTextPant = new Paint();
        mTextPant.setColor(0xffffffff);
        mTextPant.setTextSize(mTextSize);
        mRange = new RectF(mPaddding, mPaddding, mPaddding + mRadius, mPaddding + mRadius);

        mImgsBitmap = new Bitmap[mItemCount];
        for (int i = 0; i < mItemCount; i++) {
            mImgsBitmap[i] = BitmapFactory.decodeResource(getResources(), mImgs[i]);

        }


        isRunning = true;
        t = new Thread(Luckypan.this);
        t.start();

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isRunning = false;
    }

    @Override
    public void run() {
        while (isRunning) {
            long start = System.currentTimeMillis();
            draw();
            long end = System.currentTimeMillis();
            if (end - start < 50) {
                try {
                    Thread.sleep(50 - (end - start));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private void draw() {

        try {
            mCanvas = mHolder.lockCanvas();
            if (mCanvas != null) {
                drawBg();
                float tempAngle = mStartAngle;
                float sweepAngle = 360 / mItemCount;
                for (int i = 0; i < mItemCount; i++) {
                    mArcPaint.setColor(mColor[i]);
                    mCanvas.drawArc(mRange, tempAngle, sweepAngle, true, mArcPaint);
                    drawText(tempAngle, sweepAngle, mStrs[i]);
                    drawIcon(tempAngle, mImgsBitmap[i]);
                    tempAngle += sweepAngle;
                }
                mStartAngle += mSpeed;
                if (isshouldEnd) {
                    mSpeed -= 1;
                }
                if (mSpeed <= 0) {
                    Log.d("tag", "speed <0");
                    mSpeed = 0;
                    isshouldEnd = false;


                }

            }
        } catch (Exception e) {
        } finally {
            if (mCanvas != null) {
                mHolder.unlockCanvasAndPost(mCanvas);
            }
        }
    }

    public double getspeed() {
        return mSpeed;
    }

    public void lukystart(int index) {
        float angle = 360 / mItemCount;
        float from = 270 - (index + 1) * angle;
        float end = from + angle;

        float targetFrom = 4 * 360 + from;
        float targetEnd = 4 * 360 + end;

        float v1 = (float) ((-1 + Math.sqrt(1 + 8 * targetFrom)) / 2);
        float v2 = (float) ((-1 + Math.sqrt(1 + 8 * targetEnd)) / 2);

        mSpeed = v1 + Math.random() * (v2 - v1);
//        mSpeed=v2;
        Log.d("tag", "mspeed" + mSpeed);
        isshouldEnd = false;
    }

    public void luckend() {
        mStartAngle = 0;
        isshouldEnd = true;
    }

    public boolean isStart() {
        return mSpeed != 0;
    }

    public boolean isshoudend() {
        return isshouldEnd;
    }


    private void drawIcon(float tempAngle, Bitmap bitmap) {

        int imgWidth = mRadius / 8;
        float angle = (float) ((tempAngle + 360 / mItemCount / 2) * Math.PI / 180);

        int x = (int) (mCenter + mRadius / 4 * Math.cos(angle));
        int y = (int) (mCenter + mRadius / 4 * Math.sin(angle));
        Rect rect = new Rect(x - imgWidth / 2, y - imgWidth / 2, x + imgWidth / 2, y + imgWidth / 2);
        mCanvas.drawBitmap(bitmap, null, rect, null);
    }


    private void drawBg() {
        mCanvas.drawColor(0xffffffff);
        mCanvas.drawBitmap(mBgBitmap, null, new RectF(mPaddding / 2, mPaddding / 2, getMeasuredWidth() - mPaddding / 2, getMeasuredHeight() - mPaddding / 2), null);
//        mCanvas.drawBitmap(mBgBitmap, null, new RectF(mPaddding , mPaddding , mPaddding+mRadius, mPaddding+mRadius), null);
    }

    private void drawText(float tempAngle, float sweepAngle, String mStr) {

        Path path = new Path();
        path.addArc(mRange, tempAngle, sweepAngle);
        float textwidh = mTextPant.measureText(mStr);
        int hoffset = (int) (mRadius * Math.PI / mItemCount / 2 - textwidh / 2);
        int voffset = mRadius / 2 / 6;

        mCanvas.drawTextOnPath(mStr, path, hoffset, voffset, mTextPant);
    }
}
