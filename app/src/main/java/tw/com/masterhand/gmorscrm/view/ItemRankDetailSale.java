package tw.com.masterhand.gmorscrm.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.model.Performance;
import tw.com.masterhand.gmorscrm.model.VisitData;
import tw.com.masterhand.gmorscrm.tools.NumberFormater;

public class ItemRankDetailSale extends LinearLayout {
    @BindView(R.id.rootView)
    View rootView;
    @BindView(R.id.textView_index)
    TextView tvIndex;
    @BindView(R.id.textView_name)
    TextView tvName;
    @BindView(R.id.linearLayout_count)
    LinearLayout visitCount;
    @BindView(R.id.linearLayout_date)
    LinearLayout visitDate;
    @BindView(R.id.linearLayout_current_sale)
    LinearLayout currentSale;
    @BindView(R.id.linearLayout_all_sale)
    LinearLayout allSale;

    Context context;

    public ItemRankDetailSale(Context context) {
        super(context);
        init(context);
    }

    public ItemRankDetailSale(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ItemRankDetailSale(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context mContext) {
        context = mContext;
        // 連接畫面
        View view = inflate(getContext(), R.layout.item_rank_detail_sale, this);
        ButterKnife.bind(this, view);
    }

    public void setIndex(int index) {
        tvIndex.setText(String.valueOf(index));
    }

    public void setName(String name) {
        tvName.setText(name);
    }

    public void setVisitInfo(VisitData data) {
        visitCount.setVisibility(VISIBLE);
        visitDate.setVisibility(VISIBLE);
        ((TextView) visitCount.findViewWithTag("content")).setText(NumberFormater.getNumberFormat(data.getCount()));
        ((TextView) visitDate.findViewWithTag("content")).setText(data.getLast_visit());
    }

    public void setSaleInfo(Performance data) {
        currentSale.setVisibility(VISIBLE);
        allSale.setVisibility(VISIBLE);

        ((TextView) currentSale.findViewWithTag("content")).setText(NumberFormater.getMoneyFormat(data.getAmount()));
        ((TextView) allSale.findViewWithTag("content")).setText(NumberFormater.getMoneyFormat(data.getYear_amount()));
    }

    @Override
    public void setBackgroundColor(int color) {
        rootView.setBackgroundColor(color);
        super.setBackgroundColor(color);
    }
}
