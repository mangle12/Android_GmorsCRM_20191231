package tw.com.masterhand.gmorscrm.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.tools.PreferenceHelper;

public class Appbar extends RelativeLayout {
    final String TAG = getClass().getSimpleName();
    @BindView(R.id.appbar_title)
    TextView tvTitle;
    @BindView(R.id.appbar_container_left)
    LinearLayout leftContainer;
    @BindView(R.id.appbar_container_right)
    LinearLayout rightContainer;
    @BindView(R.id.appbar_back)
    ImageButton btnBack;

    Context mContext;

    public Appbar(Context context) {
        super(context);
        init(context);
    }

    public Appbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public Appbar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(final Context context) {
        mContext = context;
        // 連接畫面
        View view = inflate(getContext(), R.layout.appbar, this);
        ButterKnife.bind(this, view);
    }

    @Override
    public void invalidate() {
        super.invalidate();
        int bgColor = new PreferenceHelper(mContext).getThemeColor();
        setBackgroundColor(bgColor);
    }

    @OnClick(R.id.appbar_back)
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
     * 加入功能選單(文字)
     */
    public void addFunction(String text, OnClickListener listener) {
        int btn_size = getResources().getDimensionPixelSize(R.dimen
                .btn_size);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout
                .LayoutParams.WRAP_CONTENT, LinearLayout
                .LayoutParams.MATCH_PARENT);
        TextView tvFunc = new TextView(mContext);
        tvFunc.setGravity(Gravity.CENTER);
        tvFunc.setTypeface(Typeface.DEFAULT_BOLD);
        tvFunc.setMinWidth(btn_size);
        tvFunc.setText(text);
        tvFunc.setOnClickListener(listener);
        rightContainer.addView(tvFunc, params);
    }

    /**
     * 加入功能選單(圖片)
     */
    public void addFunction(int rid, OnClickListener listener) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout
                .LayoutParams.WRAP_CONTENT, LinearLayout
                .LayoutParams.MATCH_PARENT);
        ImageButton btn = new ImageButton(mContext);
        btn.setBackgroundResource(R.color.transparent);
        btn.setImageResource(rid);
        btn.setOnClickListener(listener);
        rightContainer.addView(btn, params);
    }

    /**
     * 清除功能選單
     */
    public void removeFuction() {
        rightContainer.removeAllViews();
    }
}
