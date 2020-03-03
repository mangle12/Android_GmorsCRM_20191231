package tw.com.masterhand.gmorscrm.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

public class HorizontalPageView extends HorizontalScrollView {
    public interface OnPageChangeListener {
        void onPageChanged(int page);
    }

    OnPageChangeListener onPageChangeListener;
    int itemWidth = 0;

    public HorizontalPageView(Context context) {
        super(context);
        init();
    }

    public HorizontalPageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HorizontalPageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setVerticalScrollBarEnabled(false);
        setHorizontalScrollBarEnabled(false);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            if (onPageChangeListener != null)
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int pg = (getScrollX() + itemWidth / 2) / itemWidth;
                        smoothScrollTo(pg * itemWidth, 0);
                        onPageChangeListener.onPageChanged(pg);
                    }
                }, 100);
        }
        return super.onTouchEvent(ev);
    }

    public void setItemWidth(int itemWidth) {
        this.itemWidth = itemWidth;
    }

    public void setOnPageChangeListener(OnPageChangeListener listener) {
        onPageChangeListener = listener;
    }

    @Override
    protected float getLeftFadingEdgeStrength() {
        return 0.0f;
    }

    @Override
    protected float getRightFadingEdgeStrength() {
        return 0.0f;
    }
}
