package br.com.rafaelgn.progressbargradient;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by rafaelneiva on 06/10/15.
 */
public class GradientTextView extends TextView {

    private int mTextWidth = 0;
    private int mTextHeight = 0;

    private int mTextStartX;
    private int mTextStartY;
    private float offset;
    private String mText;
    private int mDirection = DIRECTION_LEFT_TO_RIGHT;
    private static int DIRECTION_LEFT_TO_RIGHT = 0;
    private static int DIRECTION_RIGHT_TO_LEFT = 1;

    private int mTextLeftColor = 0xffffffff;
    private int mTextRightColor = 0xffFF4081;

    public GradientTextView(Context context) {
        super(context);
    }

    public GradientTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GradientTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public GradientTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setOffset(float offset) {
        this.offset = offset;
        invalidate();
    }

    public void setDirection(int mDirection) {
        this.mDirection = mDirection;
    }

    public int getDirection() {
        return mDirection;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        mTextWidth = (int) getPaint().measureText(getText().toString());
        mTextWidth = getMeasuredWidth();
        mTextHeight = getMeasuredHeight();

        Rect mTextRect = new Rect();
        getPaint().getTextBounds(getText().toString(), 0, getText().toString().length(), mTextRect);
        mTextHeight = mTextRect.height();

        mTextStartX = (getMeasuredWidth() - mTextWidth) / 2;
        mTextStartY = (getMeasuredHeight() - mTextHeight) / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int middle = (int) (mTextStartX + offset * mTextWidth);
        mText = getText().toString();
//        getPaint().setTextSize(40);
        if (mDirection == DIRECTION_LEFT_TO_RIGHT) {
            drawLeft(middle, mTextLeftColor, canvas);
            drawRight(middle, mTextRightColor, canvas);
        } else if (mDirection == DIRECTION_RIGHT_TO_LEFT) {
            middle = (int) (mTextStartX + (1 - offset) * mTextWidth);
            drawLeft(middle, mTextRightColor, canvas);
            drawRight(middle, mTextLeftColor, canvas);
        }
    }

    private void drawLeft(int middle, int mTextLeftColor, Canvas canvas) {
        getPaint().setColor(mTextLeftColor);

        canvas.save();
        canvas.clipRect(mTextStartX, 0, middle, getMeasuredHeight());

        canvas.drawText(mText, mTextStartX,
                getMeasuredHeight() / 2
                        - ((getPaint().descent() + getPaint().ascent()) / 2), getPaint());
        canvas.restore();
    }

    private void drawRight(int middle, int mTextRightColor, Canvas canvas) {
        getPaint().setColor(mTextRightColor);

        canvas.save();
        canvas.clipRect(middle, 0, mTextWidth + mTextStartX, getMeasuredHeight());
        canvas.drawText(mText, mTextStartX,
                getMeasuredHeight() / 2
                        - ((getPaint().descent() + getPaint().ascent()) / 2), getPaint());
        canvas.restore();
    }
}
