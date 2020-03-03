package tw.com.masterhand.gmorscrm.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.room.record.Customer;
import tw.com.masterhand.gmorscrm.tools.Base64Utils;
import tw.com.masterhand.gmorscrm.tools.ImageTools;

public class CustomerSelectCard extends RelativeLayout {
    @BindView(R.id.textView_name)
    TextView tvName;
    @BindView(R.id.textView_full_name)
    TextView tvFullName;
    @BindView(R.id.textView_area)
    TextView tvArea;
    @BindView(R.id.imageView_icon)
    ImageView ivIcon;
    @BindView(R.id.notActivated)
    RelativeLayout notActivated;

    Context context;
    Customer customer;

    public CustomerSelectCard(Context context) {
        super(context);
        init(context);
    }

    public CustomerSelectCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomerSelectCard(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context mContext) {
        context = mContext;
        // 連接畫面
        View view = inflate(getContext(), R.layout.card_customer_select, this);
        ButterKnife.bind(this, view);
    }

    /**
     * 設定公司資料
     */
    public void setCustomer(Customer customer) {
        this.customer = customer;
        if (!TextUtils.isEmpty(customer.getName())) {
            tvName.setText(customer.getName());
        }

        if (!TextUtils.isEmpty(customer.getFull_name())) {
            tvFullName.setText(customer.getFull_name());
        }

        if (!TextUtils.isEmpty(customer.getLogo())) {
            Bitmap bitmap = Base64Utils.decodeToBitmapFromString(customer.getLogo());
            ivIcon.setImageDrawable(ImageTools.getCircleDrawable(getResources(), bitmap));
        }
    }

    public void disable() {
        notActivated.setVisibility(VISIBLE);
    }
}
