package com.curvelayout;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.RelativeLayout;

/**
 * Created by filipenunes on 3/2/17.
 */

public class CurveContainer extends RelativeLayout {

    private Context mContext;

    private Path mClipPath;
    private Path mOutlinePath;

    private int width = 0;
    private int height = 0;

    /**
     * @param curvatureHeight changes the amount of curve. Default is 50.
     */
    private int curvatureHeight = 50;

    private Paint mPaint;
    private PorterDuffXfermode porterDuffXfermode;

    public CurveContainer(Context context) {
        super(context);
        init(context, null);
    }

    public CurveContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mContext = context;

        porterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.WHITE);

        mClipPath = new Path();
        mOutlinePath = new Path();

        TypedArray styledAttributes = mContext.obtainStyledAttributes(attrs, R.styleable.CrescentoImageView, 0, 0);
        if (styledAttributes.hasValue(R.styleable.CrescentoImageView_curvature)) {
            curvatureHeight = (int) styledAttributes.getDimension(R.styleable.CrescentoImageView_curvature, getDpForPixel(curvatureHeight));
        }

        styledAttributes.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        width = getMeasuredWidth();
        height = getMeasuredHeight();

        mClipPath = PathProvider.getClipPath(width, height, curvatureHeight, 0, 0,
                getPaddingTop(), getPaddingBottom(), getPaddingLeft(), getPaddingRight());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ViewCompat.setElevation(this, ViewCompat.getElevation(this));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                setOutlineProvider(getOutlineProvider());
            } catch (Exception e) {
                String TAG = "CRESCENTO_IMAGE_VIEW";
                Log.d(TAG, e.getMessage());
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public ViewOutlineProvider getOutlineProvider() {
        return new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setConvexPath(PathProvider.getOutlinePath(width, height, curvatureHeight, 0, 0,
                        getPaddingTop(), getPaddingBottom(), getPaddingLeft(), getPaddingRight()));
            }
        };
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        int saveCount = canvas.saveLayer(0, 0, getWidth(), getHeight(), null, Canvas.ALL_SAVE_FLAG);
        super.dispatchDraw(canvas);
        mPaint.setXfermode(porterDuffXfermode);
        canvas.drawPath(mClipPath, mPaint);
        canvas.restoreToCount(saveCount);
        mPaint.setXfermode(null);
    }

    private int getDpForPixel (int pixel) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pixel, mContext.getResources().getDisplayMetrics());
    }
}

