package br.com.rafaelgn.progressbargradient;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;

/**
 * Created by rafaelneiva on 5/25/15.
 */
public class CustomProgressBar extends View implements ValueAnimator.AnimatorUpdateListener {

    /**
     * minimum value the bar can display
     */
    private float mMinVal = 0f;

    /**
     * maximum value the bar can display
     */
    private float mMaxVal = 100;

    /**
     * the value the bar currently displays
     */
    private float mValue = 0;

    /**
     * the interval in which values can be chosen and displayed
     */
    private float mInterval = 1f;

    private Path mTriangle;

    private Paint mBarPaint;
    private Paint mBorderPaint;
    private Paint mOverlayPaint;
    private TextPaint mUnderTextPaint;
    private TextPaint mAboveTextPaint;

    private ObjectAnimator mAnimator;
    private boolean mTouchEnabled = true;

    private BarColorFormatter mColorFormatter;
    private int mWidth;
    private int mHeight;

    public CustomProgressBar(Context context) {
        super(context);
        init();
    }

    public CustomProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * Do all preparations.
     */
    private void init() {

        this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        mBarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBarPaint.setStyle(Paint.Style.FILL);

        mTriangle = new Path();
        mTriangle.setFillType(Path.FillType.EVEN_ODD);
        mTriangle.lineTo(0, 100);
        mTriangle.lineTo(0, 100);
        mTriangle.lineTo(50, 50);
        mTriangle.lineTo(0, 0);
        mTriangle.lineTo(0, 0);
        mTriangle.close();

        mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setStrokeWidth(dpToPx(getResources(), 1f));

        mOverlayPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mOverlayPaint.setStyle(Paint.Style.FILL);
        mOverlayPaint.setColor(Color.WHITE);
        mOverlayPaint.setAlpha(120);

        mColorFormatter = new DefaultColorFormatter(getResources().getColor(R.color.colorAccent));

        mUnderTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mUnderTextPaint.setStyle(Paint.Style.FILL);
        mUnderTextPaint.setTextSize(36);

        mUnderTextPaint.setTextAlign(Paint.Align.CENTER);

        mAboveTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mAboveTextPaint.setStyle(Paint.Style.FILL);
        mAboveTextPaint.setTextSize(36);
        mAboveTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        prepareBarSize();

        mBarPaint.setColor(mColorFormatter.getColor(mValue, mMaxVal, mMinVal));
        mBorderPaint.setColor(mColorFormatter.getColor(mValue, mMaxVal, mMinVal));
        mUnderTextPaint.setColor(mColorFormatter.getColor(mValue, mMaxVal, mMinVal));
//        mUnderTextPaint.setColor(Color.RED);
        mAboveTextPaint.setColor(Color.WHITE);

        // draw the border
        canvas.drawRect(0f, 0f, mWidth - 0f, mHeight, mBorderPaint);

    }

    @Override
    protected void dispatchDraw(Canvas canvas) {

        prepareBarSize();

        // draw under text
        int xPos = (canvas.getWidth() / 2);
        int yPos = (int) ((canvas.getHeight() / 2) - ((mUnderTextPaint.descent() + mUnderTextPaint.ascent()) / 2));
        canvas.drawText(String.valueOf((int) mValue) + "% concluído", xPos, yPos, mUnderTextPaint);

        super.dispatchDraw(canvas);

        // draw triangle
        canvas.drawPath(mTriangle, mBarPaint);

        // draw above text
        int xPosA = (canvas.getWidth() / 2);
        int yPosA = (int) ((canvas.getHeight() / 2) - ((mAboveTextPaint.descent() + mAboveTextPaint.ascent()) / 2));
        canvas.drawText(String.valueOf((int) mValue) + "% concluído", xPosA, yPosA, mAboveTextPaint);
    }

    /**
     * Prepares the bar according to the current value.
     */
    private void prepareBarSize() {

        float length = ((float) getWidth() / (mMaxVal - mMinVal)) * (mValue - mMinVal);

        Path newPath = new Path();
        newPath.setFillType(Path.FillType.EVEN_ODD);
        newPath.lineTo(0, 100);
        newPath.lineTo(length, 100);
        newPath.lineTo(length + 50, 50);
        newPath.lineTo(length, 0);
        newPath.lineTo(0, 0);
        newPath.close();
        mTriangle.set(newPath);

        mAboveTextPaint.setColor(Color.argb((int) (mValue * 3) > 255 ? 255 : (int) (mValue * 3), 255, 255, 255));
    }

    /**
     * Sets the actual value the bar displays. Do not forget to set a minimum
     * and maximum value.
     *
     * @param value
     */
    public void setValue(float value) {
        mValue = value;

        if (mSelectionListener != null)
            mSelectionListener.onValueSelected(mValue, mMaxVal, mMinVal, this);
    }

    /**
     * Animates the bar from a specific value to a specific value.
     *
     * @param from
     * @param to
     * @param durationMillis
     */
    public void animate(float from, float to, int durationMillis) {

        if (from < mMinVal)
            from = mMinVal;
        if (from > mMaxVal)
            from = mMaxVal;

        if (to < mMinVal)
            to = mMinVal;
        if (to > mMaxVal)
            to = mMaxVal;

        mValue = from;
        mAnimator = ObjectAnimator.ofFloat(this, "value", mValue, to);
        mAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mAnimator.setDuration(durationMillis);
        mAnimator.addUpdateListener(this);
        mAnimator.start();
    }

    /**
     * Animates the bar up from it's minimum value to the specified value.
     *
     * @param to
     * @param durationMillis
     */
    public void animateUp(float to, int durationMillis) {

        if (to > mMaxVal)
            to = mMaxVal;

        mAnimator = ObjectAnimator.ofFloat(this, "value", mValue, to);
        mAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mAnimator.setDuration(durationMillis);
        mAnimator.addUpdateListener(this);
        mAnimator.start();
    }

    /**
     * Animates the bar down from it's current value to the specified value.
     *
     * @param to
     * @param durationMillis
     */
    public void animateDown(float to, int durationMillis) {

        if (to < mMinVal)
            to = mMinVal;

        mAnimator = ObjectAnimator.ofFloat(this, "value", mValue, to);
        mAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mAnimator.setDuration(durationMillis);
        mAnimator.addUpdateListener(this);
        mAnimator.start();
    }

    /**
     * Sets the color the ValueBar should have.
     *
     * @param color
     */
    public void setColor(int color) {
        mColorFormatter = new DefaultColorFormatter(color);
    }

    /**
     * Sets a selectionlistener for callbacks when selecting values on the
     * ValueBar.
     *
     * @param l
     */
    public void setValueBarSelectionListener(ValueBarSelectionListener l) {
        mSelectionListener = l;
    }

    /**
     * listener called when a value has been selected on touch
     */
    private ValueBarSelectionListener mSelectionListener;

    /**
     * gesturedetector for recognizing single-taps
     */
    private GestureDetector mGestureDetector;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (mTouchEnabled) {

            if (mSelectionListener == null)
                Log.w("ValueBar",
                        "No SelectionListener specified. Use setSelectionListener(...) to set a listener for callbacks when selecting values.");

            // if the detector recognized a gesture, consume it
            if (mGestureDetector != null && mGestureDetector.onTouchEvent(e))
                return true;

            float x = e.getX();
            float y = e.getY();

            switch (e.getAction()) {

                case MotionEvent.ACTION_DOWN:
                    updateValue(x, y);
                    invalidate();
                case MotionEvent.ACTION_MOVE:
                    updateValue(x, y);
                    invalidate();
                    if (mSelectionListener != null)
                        mSelectionListener.onSelectionUpdate(mValue, mMaxVal, mMinVal, this);
                    break;
                case MotionEvent.ACTION_UP:
                    updateValue(x, y);
                    invalidate();
                    if (mSelectionListener != null)
                        mSelectionListener.onValueSelected(mValue, mMaxVal, mMinVal, this);
                    break;
                case MotionEvent.ACTION_CANCEL:
                    updateValue(x, y);
                    invalidate();
                    if (mSelectionListener != null)
                        mSelectionListener.onValueSelected(mValue, mMaxVal, mMinVal, this);
                    break;
            }

            return true;
        } else
            return super.onTouchEvent(e);
    }

    /**
     * Updates the value on the ValueBar depending on the touch position.
     *
     * @param x
     * @param y
     */
    private void updateValue(float x, float y) {

        float newVal = 0f;

        if (x <= 0)
            newVal = mMinVal;
        else if (x > getWidth())
            newVal = mMaxVal;
        else {
            float factor = x / getWidth();

            newVal = (mMaxVal - mMinVal) * factor + mMinVal;
        }

        if (mInterval > 0f) {

            float remainder = newVal % mInterval;

            // check if the new value is closer to the next, or the previous
            if (remainder <= mInterval / 2f) {

                newVal = newVal - remainder;
            } else {
                newVal = newVal - remainder + mInterval;
            }
        }

        mValue = newVal;
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        invalidate();
    }

    /**
     * Default BarColorFormatter class that supports a single color.
     *
     * @author Philipp Jahoda
     */
    private class DefaultColorFormatter implements BarColorFormatter {

        private int mColor;

        public DefaultColorFormatter(int color) {
            mColor = color;
        }

        @Override
        public int getColor(float value, float maxVal, float minVal) {
            return mColor;
        }
    }

    /**
     * @param resources The application resources
     * @param dp        Density pixels
     * @return
     */
    public static float dpToPx(Resources resources, float dp) {
        final float scale = resources.getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }
}
