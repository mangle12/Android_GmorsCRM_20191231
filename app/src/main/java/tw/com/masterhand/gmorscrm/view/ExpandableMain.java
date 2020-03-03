package tw.com.masterhand.gmorscrm.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tw.com.masterhand.gmorscrm.R;

public class ExpandableMain extends LinearLayout {

    final String TAG = getClass().getSimpleName();

    @BindView(R.id.linearLayout_expandable_container)
    LinearLayout container;
    @BindView(R.id.imageView_next)
    ImageView ivNext;
    @BindView(R.id.textView_title)
    TextView tvTitle;
    @BindView(R.id.relativeLayout_expandable_top)
    RelativeLayout top;

    OnToggleListener listener;

    public ExpandableMain(Context context) {
        super(context);
        init(context);
    }

    public ExpandableMain(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ExpandableMain(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        // 連接畫面
        View view = inflate(getContext(), R.layout.expandable_main, this);
        ButterKnife.bind(this, view);
    }

    @OnClick(R.id.relativeLayout_expandable_top)
    public void toggle() {
        if (container.getVisibility() == VISIBLE) {
            // 關閉列表
            container.setVisibility(GONE);
            ivNext.setImageResource(R.mipmap.arrow_down_thin);
            if (listener != null)
                listener.onToggle(GONE);
        } else {
            // 展開列表
            container.setVisibility(VISIBLE);
            ivNext.setImageResource(R.mipmap.arrow_up_thin);
            if (listener != null)
                listener.onToggle(VISIBLE);
        }
    }

    public void setOnToggleListener(OnToggleListener listener) {
        this.listener = listener;
    }

    public interface OnToggleListener {
        void onToggle(int visibility);
    }

    /**
     * 取得列表容器
     */
    public LinearLayout getContainer() {
        return container;
    }

    /**
     * 加入列表項目
     */
    public void addListView(View view) {
        container.addView(view);
    }

    /**
     * 清除所有列表項目
     */
    public void clearListView() {
        container.removeAllViews();
    }

    /**
     * 設定標題
     */
    public void setTitle(String title) {
        tvTitle.setText(title);
    }

    /**
     * 設定圖示
     */
    public void setIcon(int resId) {
        tvTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(resId, 0, 0, 0);
    }

    /**
     * 設定標題列背景顏色
     */
    public void setTitleBackground(int color) {
        top.setBackgroundColor(color);
    }
}
