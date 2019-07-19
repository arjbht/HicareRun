package com.ab.hicarerun.utils.notifications;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringDef;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.ab.hicarerun.R;
import com.clock.scratch.ScratchView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Arjun Bhatt on 6/24/2019.
 */
public class ScratchRelativeLayout extends View {

    private final static String TAG = ScratchView.class.getSimpleName();
    private final static float DEFAULT_ERASER_SIZE = 60f;
    private final static int DEFAULT_MASKER_COLOR = 0xffcccccc;
    private final static int DEFAULT_PERCENT = 70;
    private final static int MAX_PERCENT = 100;
    private Paint mMaskPaint;
    private Bitmap mMaskBitmap;
    private Canvas mMaskCanvas;
    private Paint mBitmapPaint;
    private BitmapDrawable mWatermark;
    private Paint mErasePaint;
    private Path mErasePath;
    private float mStartX;
    private float mStartY;
    private int mTouchSlop;
    private boolean mIsCompleted = false;
    private int mMaxPercent = DEFAULT_PERCENT;
    private int mPercent = 0;
    private int mPixels[];
    private ScratchView.EraseStatusListener mEraseStatusListener;

    public ScratchRelativeLayout(Context context) {
        super(context);
        TypedArray typedArray = context.obtainStyledAttributes(R.styleable.ScratchView);
        init(typedArray);
    }

    public ScratchRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ScratchView);
        init(typedArray);
    }

    public ScratchRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ScratchView, defStyleAttr, 0);
        init(typedArray);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ScratchRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ScratchView, defStyleAttr, defStyleRes);
        init(typedArray);
    }

    private void init(TypedArray typedArray) {
        int maskColor = typedArray.getColor(R.styleable.ScratchView_maskColor, DEFAULT_MASKER_COLOR);
        int watermarkResId = typedArray.getResourceId(R.styleable.ScratchView_watermark, -1);
        float eraseSize = typedArray.getFloat(R.styleable.ScratchView_eraseSize, DEFAULT_ERASER_SIZE);
        mMaxPercent = typedArray.getInt(R.styleable.ScratchView_maxPercent, DEFAULT_PERCENT);
        typedArray.recycle();

        mMaskPaint = new Paint();
        mMaskPaint.setAntiAlias(true);//抗锯齿
        mMaskPaint.setDither(true);//防抖
        setMaskColor(maskColor);

        mBitmapPaint = new Paint();
        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setDither(true);

        setWatermark(watermarkResId);

        mErasePaint = new Paint();
        mErasePaint.setAntiAlias(true);
        mErasePaint.setDither(true);
        mErasePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        mErasePaint.setStyle(Paint.Style.STROKE);
        mErasePaint.setStrokeCap(Paint.Cap.ROUND);
        setEraserSize(eraseSize);

        mErasePath = new Path();

        ViewConfiguration viewConfiguration = ViewConfiguration.get(getContext());
        mTouchSlop = viewConfiguration.getScaledTouchSlop();

    }

    /**
     * @param eraserSize
     */
    public void setEraserSize(float eraserSize) {
        mErasePaint.setStrokeWidth(eraserSize);
    }

    /**
     * @param color 0xffff0000
     */
    public void setMaskColor(int color) {
        mMaskPaint.setColor(color);
    }

    /**
     * @param max 100
     */
    public void setMaxPercent(int max) {
        if (max > 100 || max <= 0) {
            return;
        }
        this.mMaxPercent = max;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measuredWidth = measureSize(widthMeasureSpec);
        int measuredHeight = measureSize(heightMeasureSpec);
        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(mMaskBitmap, 0, 0, mBitmapPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                startErase(event.getX(), event.getY());
                invalidate();
                return true;
            case MotionEvent.ACTION_MOVE:
                erase(event.getX(), event.getY());
                invalidate();
                return true;
            case MotionEvent.ACTION_UP:
                stopErase();
                invalidate();
                return true;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        createMasker(w, h);
    }

    /**
     * @param width
     * @param height
     */
    private void createMasker(int width, int height) {
        mMaskBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mMaskCanvas = new Canvas(mMaskBitmap);
        Rect rect = new Rect(0, 0, width, height);
        mMaskCanvas.drawRect(rect, mMaskPaint);//Bitmap

        if (mWatermark != null) {
            Rect bounds = new Rect(rect);
            mWatermark.setBounds(bounds);
            mWatermark.draw(mMaskCanvas);
        }

        mPixels = new int[width * height];
    }

    private int measureSize(int measureSpec) {
        int size = 0;
        int specMode = View.MeasureSpec.getMode(measureSpec);
        int specSize = View.MeasureSpec.getSize(measureSpec);
        if (specMode == View.MeasureSpec.EXACTLY) {
            size = specSize;
        } else {
            if (specMode == View.MeasureSpec.AT_MOST) {
                size = Math.min(size, specSize);
            }
        }
        return size;
    }

    /**
     * @param x
     * @param y
     */
    private void startErase(float x, float y) {
        mErasePath.reset();
        mErasePath.moveTo(x, y);
        this.mStartX = x;
        this.mStartY = y;
    }

    /**
     * 擦除
     *
     * @param x
     * @param y
     */
    private void erase(float x, float y) {
        int dx = (int) Math.abs(x - mStartX);
        int dy = (int) Math.abs(y - mStartY);
        if (dx >= mTouchSlop || dy >= mTouchSlop) {
            this.mStartX = x;
            this.mStartY = y;

            mErasePath.lineTo(x, y);
            mMaskCanvas.drawPath(mErasePath, mErasePaint);
            onErase();

            mErasePath.reset();
            mErasePath.moveTo(mStartX, mStartY);
        }
    }

    private void onErase() {
        int width = getWidth();
        int height = getHeight();
        new AsyncTask<Integer, Integer, Boolean>() {

            @Override
            protected Boolean doInBackground(Integer... params) {
                int width = params[0];
                int height = params[1];
                mMaskBitmap.getPixels(mPixels, 0, width, 0, 0, width, height);

                float erasePixelCount = 0;
                float totalPixelCount = width * height;

                for (int pos = 0; pos < totalPixelCount; pos++) {
                    if (mPixels[pos] == 0) {
                        erasePixelCount++;
                    }
                }

                int percent = 0;
                if (erasePixelCount >= 0 && totalPixelCount > 0) {
                    percent = Math.round(erasePixelCount * 100 / totalPixelCount);
                    publishProgress(percent);
                }

                return percent >= mMaxPercent;
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
                mPercent = values[0];
                onPercentUpdate();
            }

            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                if (result && !mIsCompleted) {
                    mIsCompleted = true;
                    if (mEraseStatusListener != null) {
                        mEraseStatusListener.onCompleted(ScratchRelativeLayout.this);
                    }
                }
            }

        }.execute(width, height);
    }

    private void stopErase() {
        this.mStartX = 0;
        this.mStartY = 0;
        mErasePath.reset();
    }

    private void onPercentUpdate() {
        if (mEraseStatusListener != null) {
            mEraseStatusListener.onProgress(mPercent);
        }
    }

    /**
     * @param listener
     */
    public void setEraseStatusListener(ScratchView.EraseStatusListener listener) {
        this.mEraseStatusListener = listener;
    }

    /**
     * @param resId
     */
    public void setWatermark(int resId) {
        if (resId == -1) {
            mWatermark = null;
        } else {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resId);
            mWatermark = new BitmapDrawable(bitmap);
//            mWatermark.setTileModeXY(Shader.TileMode.MIRROR, Shader.TileMode.MIRROR);
        }
    }

    public void reset() {
        mIsCompleted = false;

        int width = getWidth();
        int height = getHeight();
        createMasker(width, height);
        invalidate();

        onErase();
    }

    public void clear() {
        int width = getWidth();
        int height = getHeight();
        mMaskBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mMaskCanvas = new Canvas(mMaskBitmap);
        Rect rect = new Rect(0, 0, width, height);
        mMaskCanvas.drawRect(rect, mErasePaint);
        invalidate();

        onErase();
    }


    public static interface EraseStatusListener {

        /**
         * @param percent 0，100；
         */
        public void onProgress(int percent);

        /**
         * @param view
         */
        public void onCompleted(View view);
    }
}
