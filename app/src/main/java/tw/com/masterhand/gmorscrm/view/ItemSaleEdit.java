package tw.com.masterhand.gmorscrm.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.room.record.SalesOpportunitySub;
import tw.com.masterhand.gmorscrm.room.setting.DepartmentSalesOpportunitySub;

public class ItemSaleEdit extends LinearLayout {
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.tvPercent)
    TextView tvPercent;

    Context context;
    SalesOpportunitySub salesOpportunitySub;
    int totalPercent;

    OnCheckChangeListener listener;

    public interface OnCheckChangeListener {
        void onCheckChange(boolean isChecked);
    }

    public ItemSaleEdit(Context context) {
        super(context);
        init(context);
    }

    public ItemSaleEdit(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ItemSaleEdit(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context mContext) {
        context = mContext;
        totalPercent = 100;
        // 連接畫面
        View view = inflate(getContext(), R.layout.item_sale_edit, this);
        ButterKnife.bind(this, view);
    }

    public void setOnCheckChangeListener(OnCheckChangeListener onCheckChangeListener) {
        listener = onCheckChangeListener;
    }

    public void setSalesOpportunitySub(SalesOpportunitySub sub) {
        salesOpportunitySub = sub;
        tvTitle.setText(sub.getDepartment_sales_opportunity().getName());
        tvPercent.setText("" + sub.getDepartment_sales_opportunity().getPercentage() + "%");
        setSelected(sub.getSelected());
    }

    public SalesOpportunitySub getSalesOpportunitySub() {
        return salesOpportunitySub;
    }

    public float getPercent() {
        return (float) salesOpportunitySub.getDepartment_sales_opportunity().getPercentage() / totalPercent;
    }

    public void setTotalPercent(int total) {
        totalPercent = total;
    }

    public void setEditable(boolean editable) {
        setClickable(editable);
        if (editable) {
            setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    setSelected(!isSelected());
                    if (listener != null)
                        listener.onCheckChange(isSelected());
                }
            });
        }
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        salesOpportunitySub.setSelected(selected);
    }
}
