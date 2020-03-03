package tw.com.masterhand.gmorscrm.view;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.tools.UnitChanger;

public class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration {

    private final int mVerticalSpaceHeight;

    public VerticalSpaceItemDecoration(int mVerticalSpaceHeight) {
        this.mVerticalSpaceHeight = UnitChanger.dpToPx(mVerticalSpaceHeight);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        outRect.top = mVerticalSpaceHeight;
    }
}
