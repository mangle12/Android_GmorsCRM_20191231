package tw.com.masterhand.gmorscrm.view;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;

public class HeaderBehavior extends CoordinatorLayout.Behavior<RelativeLayout> {
    public HeaderBehavior(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, RelativeLayout child, View directTargetChild, View target, int nestedScrollAxes) {
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, RelativeLayout child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        if (dyConsumed > 0) {
            // 往下滑動，隱藏
            setAnimateTranslationY(child, -child.getHeight());
        } else if (dyConsumed < 0) {
            // 往上滑動，顯示
            setAnimateTranslationY(child, 0);
        }
    }

    private void setAnimateTranslationY(View view, int y) {
        view.animate().translationY(y).setInterpolator(new LinearInterpolator()).start();
    }

}
