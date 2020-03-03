package tw.com.masterhand.gmorscrm.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

public class ObservableHorizontalScrollView extends HorizontalScrollView {
    private Callbacks mCallbacks;

    public ObservableHorizontalScrollView(Context context) {
        super(context);
    }

    public ObservableHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                if (mCallbacks != null)
                    mCallbacks.onScrollStart();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mCallbacks != null)
                    mCallbacks.onScrollStop();
                break;
        }
        return super.onTouchEvent(ev);
    }

    public void setCallbacks(Callbacks listener) {
        mCallbacks = listener;
    }

    public interface Callbacks {
        void onScrollStart();

        void onScrollStop();
    }
}
