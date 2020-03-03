package tw.com.masterhand.gmorscrm.view;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import tw.com.masterhand.gmorscrm.tools.UnitChanger;

public class BaselineTextView extends AppCompatTextView {
    public BaselineTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int yOffset = getHeight() - getBaseline()- UnitChanger.dpToPx(3);
        canvas.translate(0, yOffset);
        super.onDraw(canvas);
    }
}
