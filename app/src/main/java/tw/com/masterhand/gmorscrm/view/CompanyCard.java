package tw.com.masterhand.gmorscrm.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.room.record.Customer;
import tw.com.masterhand.gmorscrm.tools.Base64Utils;
import tw.com.masterhand.gmorscrm.tools.ImageTools;

public class CompanyCard extends RelativeLayout {
    @BindView(R.id.textView_name)
    TextView tvName;
    @BindView(R.id.textView_area)
    TextView tvArea;
    @BindView(R.id.textView_address)
    TextView tvAddress;
    @BindView(R.id.textView_full_name)
    TextView tvFullName;
    @BindView(R.id.tvId)
    TextView tvId;
    @BindView(R.id.imageView_icon)
    ImageView ivIcon;
    @BindView(R.id.imageView_map)
    ImageView ivMap;

    Context context;
    Customer customer;

    public CompanyCard(Context context) {
        super(context);
        init(context);
    }

    public CompanyCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CompanyCard(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context mContext) {
        context = mContext;
        // 連接畫面
        View view = inflate(getContext(), R.layout.card_company, this);
        ButterKnife.bind(this, view);
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    /**
     * 開啟地圖
     */
    @OnClick(R.id.imageView_map)
    void openMap() {
        if (Locale.getDefault().getCountry().equals("CN")) {
//        if (Locale.getDefault().getLanguage().equals("zh")) {
            // 大陸地區使用百度地圖
            Uri gmmIntentUri = Uri.parse("baidumap://map/geocoder?src=" + context.getString(R.string.app_name) + "&address=" + customer.getAddress().getShowAddress());
            Intent mapIntent = new Intent();
            mapIntent.setData(gmmIntentUri);

            if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(mapIntent);
            } else {
                gmmIntentUri = Uri.parse("http://api.map.baidu.com/geocoder?address=" + customer.getAddress().getShowAddress() + "&output=html&src=" + context.getString(R.string.app_name));
                Intent webIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                context.startActivity(webIntent);
            }
        } else {
            // 其他地區使用google地圖
            Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + customer.getAddress().getShowAddress());
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(mapIntent);
            } else {
                gmmIntentUri = Uri.parse("https://www.google.com.tw/maps/search/" + customer.getAddress().getShowAddress().replace(" ",""));
                Intent webIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                context.startActivity(webIntent);
            }
        }
    }

    /**
     * 設定公司資料
     */
    public void setCustomer(Customer customer) {
        this.customer = customer;
        if (!TextUtils.isEmpty(customer.getName())) {
            tvName.setText(customer.getName());
        }
        if (customer.getAddress() != null) {
            tvAddress.setText(customer.getAddress().getShowAddress());
        }
        if (!TextUtils.isEmpty(customer.getFull_name())) {
            tvFullName.setText(customer.getFull_name());
        }
        if (!TextUtils.isEmpty(customer.getLogo())) {
            Bitmap bitmap = Base64Utils.decodeToBitmapFromString(customer.getLogo());
            ivIcon.setImageDrawable(ImageTools.getCircleDrawable(getResources(), bitmap));
        }
        if (!TextUtils.isEmpty(customer.getCode())) {
            tvId.setText("ID:" + customer.getCode());
        }
    }
}
