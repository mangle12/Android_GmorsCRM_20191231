package tw.com.masterhand.gmorscrm.view;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lb.auto_fit_textview.AutoResizeTextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.enums.ReportTotalType;
import tw.com.masterhand.gmorscrm.model.ReportSummary;
import tw.com.masterhand.gmorscrm.tools.NumberFormater;

public class ItemReportTotal extends RelativeLayout {
    @BindView(R.id.textView_title)
    TextView tvTitle;
    @BindView(R.id.textView_item1_title)
    TextView tvTitle1;
    @BindView(R.id.textView_item2_title)
    TextView tvTitle2;
    @BindView(R.id.textView_item3_title)
    TextView tvTitle3;
    @BindView(R.id.textView_item1)
    AutoResizeTextView tvItem1;
    @BindView(R.id.textView_item2)
    AutoResizeTextView tvItem2;
    @BindView(R.id.textView_item3)
    AutoResizeTextView tvItem3;

    Context context;
    int color;

    public ItemReportTotal(Context context) {
        super(context);
        init(context);
    }

    public ItemReportTotal(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ItemReportTotal(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context mContext) {
        context = mContext;
        // 連接畫面
        View view = inflate(getContext(), R.layout.item_report_total, this);
        ButterKnife.bind(this, view);
    }

    public void setReportTotalType(ReportTotalType type, ReportSummary report) {
        tvTitle.setText(context.getString(type.getTitle()));
        color = type.getColor();
        tvTitle.setBackgroundResource(type.getBackground());
        tvItem1.setTextColor(ContextCompat.getColor(context, color));
        tvItem2.setTextColor(ContextCompat.getColor(context, color));
        tvItem3.setTextColor(ContextCompat.getColor(context, color));
        switch (type) {
            case PERFORMANCE://業績
                setItem(report.getWinAmount(), context.getString(R.string.report_win_amount), 1);
                setItem(report.getcontractAmount(), context.getString(R.string.report_contract_amount), 2);
                setItem(report.getLose(), context.getString(R.string.report_lose), 3);
                break;
            case BEHAVIOR://行為
                setItem(report.getSignIn(), context.getString(R.string.report_sign_in), 1);
                setItem(report.getSignOut(), context.getString(R.string.report_sign_out), 2);
                break;
            case ADD://新增
                setItem(report.getTaskCreate(), context.getString(R.string.report_new_task), 1);
                setItem(report.getOpportunityCreate(), context.getString(R.string.report_new_opportunity), 2);
                break;
        }
    }

    private void setItem(int count, String title, int index) {
        TextView tvItem = null, tvTitle = null;
        switch (index) {
            case 1:
                tvItem = tvItem1;
                tvTitle = tvTitle1;
                break;
            case 2:
                tvItem = tvItem2;
                tvTitle = tvTitle2;
                break;
            case 3:
                tvItem = tvItem3;
                tvTitle = tvTitle3;
                break;
        }
        if (tvItem == null || tvTitle == null)
            return;
        tvItem.setText(NumberFormater.getNumberFormat(count));
        if (TextUtils.isEmpty(title)) {
            tvItem.setVisibility(GONE);
            tvTitle.setText(context.getString(R.string.empty_show));
        } else {
            tvTitle.setText(title);
        }
    }


}
