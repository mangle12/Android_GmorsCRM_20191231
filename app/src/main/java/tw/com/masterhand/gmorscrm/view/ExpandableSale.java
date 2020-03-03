package tw.com.masterhand.gmorscrm.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.enums.ProjectStatus;
import tw.com.masterhand.gmorscrm.room.record.SalesOpportunitySub;
import tw.com.masterhand.gmorscrm.room.setting.DepartmentSalesOpportunity;
import tw.com.masterhand.gmorscrm.tools.NumberFormater;
import tw.com.masterhand.gmorscrm.tools.UnitChanger;

public class ExpandableSale extends LinearLayout implements ItemSaleEdit.OnCheckChangeListener {

    final String TAG = getClass().getSimpleName();

    @BindView(R.id.linearLayout_expandable_container)
    LinearLayout container;
    @BindView(R.id.imageView_next)
    ImageView ivNext;
    @BindView(R.id.textView_percent)
    TextView tvPercent;
    @BindView(R.id.textView_count)
    TextView tvCount;
    @BindView(R.id.tvTitle)
    TextView tvTitle;

    Context context;

    String opportunityName;
    int opportunityPercent;
    int prePercent;

    /*SaleActivity*/
    int count;
    float amount;

    OnCheckChangeListener listener;

    public interface OnCheckChangeListener {
        void onCheckChange(ExpandableSale item, boolean isChecked);
    }

    public ExpandableSale(Context context) {
        super(context);
        init(context);
    }

    public ExpandableSale(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ExpandableSale(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        // 連接畫面
        View view = inflate(getContext(), R.layout.expandable_sale, this);
        ButterKnife.bind(this, view);
        if (isInEditMode())
            return;
        showListView(false);
    }

    @OnClick(R.id.relativeLayout_expandable_top)
    void toggle() {
        if (container.getVisibility() == VISIBLE) {
            // 關閉列表
            showListView(false);
        } else {
            // 展開列表
            showListView(true);
        }
    }

    @OnClick(R.id.tvTitle)
    void onRadioClick() {
        changeSelected(!tvTitle.isSelected());
        if (listener != null)
            listener.onCheckChange(this, tvTitle.isSelected());
    }

    public void disableRadioClick() {
        tvTitle.setOnClickListener(null);
    }

    public int getResultPercent() {
        if (container.getChildCount() > 0) {
            int base = getPercent();
            int diff = base - prePercent;
            float result = 0;
            for (int i = 0; i < container.getChildCount(); i++) {
                ItemSaleEdit btnSelect = (ItemSaleEdit) container.getChildAt(i);
                if (btnSelect.isSelected()) {
                    result += diff * btnSelect.getPercent();
                }
            }
            return Math.round(result);
        } else {
            return getPercent();
        }
    }

    public List<SalesOpportunitySub> getSalesOpportunitySub() {
        List<SalesOpportunitySub> list = new ArrayList<>();
        if (container.getChildCount() > 0) {
            for (int i = 0; i < container.getChildCount(); i++) {
                ItemSaleEdit btnSelect = (ItemSaleEdit) container.getChildAt(i);
                list.add(btnSelect.getSalesOpportunitySub());
            }
        }
        return list;
    }

    void changeSelected(boolean isChanged) {
        tvTitle.setSelected(isChanged);
        if (isChanged) {
            setCount(context.getString(R.string.completed));
            if (container.getVisibility() == VISIBLE)
                toggle();
            // 完成後全選子選項
            allSelected(true);
        } else {
            setCount(context.getString(R.string.uncompleted));
            if (isAllSelected()) {
                // 取消全部子選項
                allSelected(false);
            }
        }
    }

    private void allSelected(boolean shouldSelected) {
        for (int i = 0; i < container.getChildCount(); i++) {
            ItemSaleEdit btnSelect = (ItemSaleEdit) container.getChildAt(i);
            if (btnSelect != null) {
                btnSelect.setSelected(shouldSelected);
            }
        }
    }

    public void showListView(boolean isShow) {
        if (isShow) {
            container.setVisibility(VISIBLE);
            Drawable icon = ContextCompat.getDrawable(context, R.mipmap.common_tra_goup);
            icon.setColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY);
            ivNext.setImageDrawable(icon);
        } else {
            container.setVisibility(GONE);
            Drawable icon = ContextCompat.getDrawable(context, R.mipmap.common_tra_godown);
            icon.setColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY);
            ivNext.setImageDrawable(icon);
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
    public void addListView(ItemSaleEdit item) {
        item.setOnCheckChangeListener(this);
        container.addView(item);
    }

    public void addListView(ItemSale item) {
        container.addView(item);
    }

    @Override
    public void onCheckChange(boolean isChecked) {
        setRadioCheck(isAllSelected());
        if (listener != null)
            listener.onCheckChange(this, tvTitle.isSelected());
    }

    private boolean isAllSelected() {
        boolean isAllSelected = true;
        for (int i = 0; i < container.getChildCount(); i++) {
            ItemSaleEdit btnSelect = (ItemSaleEdit) container.getChildAt(i);
            if (!btnSelect.isSelected()) {
                isAllSelected = false;
                break;
            }
        }
        return isAllSelected;
    }

    /**
     * 清除所有列表項目
     */
    public void clearListView() {
        container.removeAllViews();
        setCountAndAmount(0, 0);
    }


    /**
     * 設定標題
     */
    public void setTitle(String title) {
        tvTitle.setText(title);
    }

    /**
     * 設定數量顯示/完成狀態
     */
    public void setCount(String count) {
        tvCount.setText(count);
    }

    public void setCountAndAmount(int count, float amount) {
        this.count = count;
        this.amount = amount;
        tvCount.setText(NumberFormater.getNumberFormat(amount) + context.getString(R.string
                .money_unit) + "/" + String.valueOf(count) + context.getString(R.string.unit));
    }

    public int getCount() {
        return count;
    }

    public float getAmount() {
        return amount;
    }

    public void setPercent(int percent) {
        tvPercent.setText(String.valueOf(percent) + "%");
        if (percent == 0) {
            tvPercent.setBackgroundResource(R.drawable.bg_gray_corner);
        } else if (percent == 100) {
            tvPercent.setBackgroundResource(R.drawable.bg_red_corner);
        }
    }

    public int getPercent() {
        return opportunityPercent;
    }

    public void hidePercent() {
        tvPercent.setVisibility(INVISIBLE);
    }

    public void setRadioCheck(boolean isChecked) {
        changeSelected(isChecked);
    }

    public boolean isRadioCheck() {
        return tvTitle.isSelected();
    }

    public void showRadioButton(boolean isShow) {
        if (!isShow)
            tvTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
    }

    public void showChildPadding() {
        int padding = UnitChanger.dpToPx(8);
        container.setPaddingRelative(padding, padding, padding, padding);
        container.setDividerDrawable(ContextCompat.getDrawable(context, R.drawable
                .divider_transparent_v_8dp));
        container.setShowDividers(SHOW_DIVIDER_MIDDLE);
    }

    public void setOnCheckChangeListener(OnCheckChangeListener onCheckChangeListener) {
        listener = onCheckChangeListener;
    }

    public void setDepartmentSalesOpportunity(ProjectStatus status, DepartmentSalesOpportunity
            opportunity) {
        switch (status) {
            case DESIGN:
                opportunityName = opportunity.getStage_1_name();
                opportunityPercent = opportunity.getStage_1_percentage();
                prePercent = 0;
                break;
            case QUOTE:
                opportunityName = opportunity.getStage_2_name();
                opportunityPercent = opportunity.getStage_2_percentage();
                prePercent = opportunity.getStage_1_percentage();
                break;
            case SAMPLE:
                opportunityName = opportunity.getStage_3_name();
                opportunityPercent = opportunity.getStage_3_percentage();
                prePercent = opportunity.getStage_2_percentage();
                break;
            case NEGOTIATION:
                opportunityName = opportunity.getStage_4_name();
                opportunityPercent = opportunity.getStage_4_percentage();
                prePercent = opportunity.getStage_3_percentage();
                break;
            case WIN:
                opportunityName = opportunity.getStage_5_name();
                opportunityPercent = 100;
                break;
            case LOSE:
                opportunityName = opportunity.getStage_6_name();
                opportunityPercent = 0;
                break;
        }
        setTitle(opportunityName);
        setPercent(opportunityPercent);
    }
}
