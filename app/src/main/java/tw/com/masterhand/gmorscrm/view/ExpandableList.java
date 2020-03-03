package tw.com.masterhand.gmorscrm.view;

import android.content.Context;
import android.graphics.Point;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.GridLayout;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.apache.commons.lang3.time.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.tools.UnitChanger;

public class ExpandableList extends LinearLayout {

    final String TAG = getClass().getSimpleName();

    @BindView(R.id.linearLayout_expandable_container)
    LinearLayout container;
    @BindView(R.id.imageView_mark)
    ImageView ivMark;
    @BindView(R.id.imageView_icon)
    ImageView ivIcon;
    @BindView(R.id.imageView_next)
    ImageView ivNext;
    @BindView(R.id.textView_title)
    TextView tvTitle;
    @BindView(R.id.textView_subtitle)
    TextView tvSubtitle;
    @BindView(R.id.textView_count)
    TextView tvCount;

    public ExpandableList(Context context) {
        super(context);
        init(context);
    }

    public ExpandableList(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ExpandableList(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        // 連接畫面
        View view = inflate(getContext(), R.layout.expandable_list, this);
        ButterKnife.bind(this, view);
    }

    @OnClick(R.id.relativeLayout_expandable_top)
    void toggle() {
        if (container.getVisibility() == VISIBLE) {
            // 關閉列表
            container.setVisibility(GONE);
            ivNext.setImageResource(R.mipmap.arrow_down_thin);
        } else {
            // 展開列表
            container.setVisibility(VISIBLE);
            ivNext.setImageResource(R.mipmap.arrow_up_thin);
        }
    }

    /**
     * 取得列表容器
     */
    public ViewGroup getContainer() {
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
     * 設定副標題
     */
    public void setSubtitle(String subtitle) {
        tvSubtitle.setText(subtitle);
    }

    /**
     * 設定標題
     */
    public void setTitle(String title) {
        tvTitle.setText(title);
    }

    /**
     * 顯示標記
     */
    public void showMark(boolean isShow) {
        if (isShow)
            ivMark.setVisibility(VISIBLE);
        else
            ivMark.setVisibility(INVISIBLE);
    }

    /**
     * 設定數量顯示
     */
    public void setCount(String count) {
        tvCount.setText(count);
    }

    /**
     * 設定圖示
     */
    public void setIcon(int resId) {
        ivIcon.setImageResource(resId);
    }
}
