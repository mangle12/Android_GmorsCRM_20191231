package tw.com.masterhand.gmorscrm.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.room.setting.ConfigReimbursementItem;

public class ItemExp extends RelativeLayout {
    @BindView(R.id.imageButton_function)
    ImageButton btnFunc;
    @BindView(R.id.textView_title)
    TextView tvTitle;

    Context context;
    ConfigReimbursementItem config;

    public ItemExp(Context context) {
        super(context);
        init(context);
    }

    public ItemExp(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ItemExp(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context mContext) {
        context = mContext;
        // 連接畫面
        View view = inflate(getContext(), R.layout.item_exp, this);
        ButterKnife.bind(this, view);
    }

    public void setBtnFuncListenser(OnClickListener listenser) {
        btnFunc.setOnClickListener(listenser);
    }

    public boolean isSelected() {
        return btnFunc.isSelected();
    }

    public void setSelected(boolean isSelected) {
        btnFunc.setSelected(isSelected);
    }

    public void setConfigReimbursementItem(ConfigReimbursementItem config) {
        this.config = config;
        tvTitle.setText(config.getName());
    }

    public ConfigReimbursementItem getConfigReimbursementItem() {
        return config;
    }
}
