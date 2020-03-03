package tw.com.masterhand.gmorscrm.view;

import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.tools.ImageTools;
import tw.com.masterhand.gmorscrm.tools.NumberFormater;

public class ItemStatisticPersonal extends RelativeLayout {
    @BindView(R.id.progress_reach)
    ProgressBar progressBar;
    @BindView(R.id.textView_goal_title)
    TextView tvGoalTitle;
    @BindView(R.id.textView_goal_count)
    TextView tvGoalCount;
    @BindView(R.id.textView_goal_unit)
    TextView tvGoalUnit;
    @BindView(R.id.textView_reach_title)
    TextView tvReachTitle;
    @BindView(R.id.textView_reach_count)
    TextView tvReachCount;
    @BindView(R.id.textView_reach_unit)
    TextView tvReachUnit;
    @BindView(R.id.textView_diff)
    TextView tvDiff;
    @BindView(R.id.rootView)
    LinearLayout rootView;

    Context context;

    public ItemStatisticPersonal(Context context) {
        super(context);
        init(context);
    }

    public ItemStatisticPersonal(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ItemStatisticPersonal(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context mContext) {
        context = mContext;
        // 連接畫面
        View view = inflate(getContext(), R.layout.item_statistic_personal, this);
        ButterKnife.bind(this, view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //Android 版本大於21 (5.0)
            rootView.setClipToOutline(true);
        }
    }

    /*
         * 目標金額
         */
    public void setGoal(String title, float count, String unit) {
        tvGoalTitle.setText(title);
        tvGoalCount.setText(NumberFormater.getMoneyFormat(count));
        tvGoalUnit.setText(unit);
    }

    /*
        *達成金額
         */
    public void setReach(String title, float count, String unit) {
        tvReachTitle.setText(title);
        tvReachCount.setText(NumberFormater.getMoneyFormat(count));
        tvReachUnit.setText(unit);
    }

    /*
        *前期相比
         */
    public void setDiff(int percent) {
        tvDiff.setText(String.valueOf(Math.abs(percent)) + "%");
        if (percent < 0) {
            tvDiff.setTextColor(ContextCompat.getColor(context, R.color.red));
            tvDiff.setCompoundDrawablesRelativeWithIntrinsicBounds(ImageTools.getColorDrawable(context, R.mipmap.common_tra_godown, R.color.red), null, null, null);
        } else {
            tvDiff.setTextColor(ContextCompat.getColor(context, R.color.green));
            tvDiff.setCompoundDrawablesRelativeWithIntrinsicBounds(ImageTools.getColorDrawable(context, R.mipmap.common_tra_goup, R.color.green), null, null, null);
        }
    }

    public void setProgress(float max, float progress) {
        progressBar.setMax(Math.round(max));
        progressBar.setProgress(Math.round(progress));
    }
}
