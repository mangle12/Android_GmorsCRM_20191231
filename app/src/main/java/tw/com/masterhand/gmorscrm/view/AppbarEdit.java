package tw.com.masterhand.gmorscrm.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.tools.PreferenceHelper;

public class AppbarEdit extends RelativeLayout {
    final String TAG = getClass().getSimpleName();
    @BindView(R.id.appbar_title)
    TextView tvTitle;
    @BindView(R.id.button_cancel)
    Button btnCancel;
    @BindView(R.id.button_complete)
    Button btnComplete;
    @BindView(R.id.appbar_background)
    RelativeLayout background;

    Context mContext;

    public AppbarEdit(Context context) {
        super(context);
        init(context);
    }

    public AppbarEdit(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AppbarEdit(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(final Context context) {
        mContext = context;
        // 連接畫面
        View view = inflate(getContext(), R.layout.appbar_edit, this);
        ButterKnife.bind(this, view);
    }

    @OnClick(R.id.button_cancel)
    void onBack(View view) {
        if (mContext instanceof Activity)
            ((Activity) mContext).onBackPressed();
    }

    /**
     * 設定標題
     */
    public void setTitle(String title) {
        tvTitle.setText(title);
    }

    /**
     * 設定完成監聽
     */
    public void setCompleteListener(OnClickListener listener) {
        btnComplete.setOnClickListener(listener);
    }

    public void setBackground(int color) {
        background.setBackgroundColor(color);
    }

    public void setTextColor(int color) {
        tvTitle.setTextColor(color);
        btnComplete.setTextColor(color);
        btnCancel.setTextColor(color);
    }

    public void disable() {
        btnComplete.setVisibility(GONE);
        btnCancel.setText(R.string.back);
    }

    @Override
    public void invalidate() {
        super.invalidate();
        int bgColor = new PreferenceHelper(mContext).getThemeColor();
        setBackgroundColor(bgColor);
    }
}
