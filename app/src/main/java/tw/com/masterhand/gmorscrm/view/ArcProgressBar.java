package tw.com.masterhand.gmorscrm.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;

import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.tools.UnitChanger;

public class ArcProgressBar extends View {

    Paint paint;
    int mBackgroundColor;
    int mLastYearColor;
    int mCurrentYearColor;

    float strokeWidth = UnitChanger.dpToPx(40);
    int min = 0, max = 100, currentValue = 0, lastValue = 0;
    RectF oval;// 弧形範圍(全圓)
    Interpolator mInterpolator;

    boolean isCurrentUp = true;

    public ArcProgressBar(final Context context) {
        this(context, null);
    }

    public ArcProgressBar(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArcProgressBar(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // Always draw
        setWillNotDraw(false);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ArcProgressBar);

        try {
            mBackgroundColor = typedArray.getColor(R.styleable.ArcProgressBar_BACKGROUND_COLOR, ContextCompat.getColor(context, R.color.gray_light));
            mLastYearColor = typedArray.getColor(R.styleable.ArcProgressBar_LAST_YEAR_COLOR, ContextCompat.getColor(context, R.color.colorPrimary));
            mCurrentYearColor = typedArray.getColor(R.styleable.ArcProgressBar_CURRENT_YEAR_COLOR, ContextCompat.getColor(context, R.color.orange));
        } finally {
            typedArray.recycle();
        }

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStyle(Paint.Style.STROKE);

        oval = new RectF();
        mInterpolator = AnimationUtils.loadInterpolator(context, android.R.interpolator.linear);
    }

    public void setProgress(int max, int current, int last) {
        this.max = max;
        int speed = 1000;// 跑到100%所需的時間
        if (current > last) {
            isCurrentUp = false;
        } else {
            isCurrentUp = true;
        }
        startAnimation(min, last, speed * last / max, mInterpolator, false);
        startAnimation(min, current, speed * current / max, mInterpolator, true);
    }

    private void startAnimation(int start, int end, long duration, Interpolator interpolator, final boolean isCurrent) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(start, end);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        if (isCurrent)
                            ArcProgressBar.this.currentValue = (int) animation.getAnimatedValue();
                        else
                            ArcProgressBar.this.lastValue = (int) animation.getAnimatedValue();
                        invalidate();
                    }

                });
        valueAnimator.setInterpolator(interpolator);
        valueAnimator.setDuration(duration);
        valueAnimator.start();
    }

    public int getProgressMax() {
        return max;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Try for a width based on our minimum
        int minw = getPaddingLeft() + getPaddingRight() + getSuggestedMinimumWidth();
        int w = resolveSizeAndState(minw, widthMeasureSpec, 1);

        // Whatever the width ends up being, ask for a height that would let the pie
        // get as big as it can
        int minh = MeasureSpec.getSize(w) / 2 + getPaddingBottom() + getPaddingTop();
        int h = resolveSizeAndState(minh, heightMeasureSpec, 0);

        setMeasuredDimension(w, h);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setStrokeWidth(strokeWidth);

        float height = getMeasuredWidth() - (strokeWidth / 2);
        float width = getMeasuredWidth() - (strokeWidth / 2);
        oval.left = strokeWidth / 2;
        oval.top = strokeWidth / 2;
        oval.right = width;
        oval.bottom = height;
        // 背景
        paint.setColor(mBackgroundColor);
        canvas.drawArc(oval, -180, 180, false, paint);
        if (isCurrentUp) {
            // 上月
            paint.setColor(mLastYearColor);
            canvas.drawArc(oval, -180, 180 * lastValue / max, false, paint);
            // 本月
            paint.setColor(mCurrentYearColor);
            canvas.drawArc(oval, -180, 180 * currentValue / max, false, paint);
        } else {
            // 本月
            paint.setColor(mCurrentYearColor);
            canvas.drawArc(oval, -180, 180 * currentValue / max, false, paint);
            // 上月
            paint.setColor(mLastYearColor);
            canvas.drawArc(oval, -180, 180 * lastValue / max, false, paint);
        }

    }
}
