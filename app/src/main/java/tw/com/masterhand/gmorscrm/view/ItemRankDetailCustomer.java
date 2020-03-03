package tw.com.masterhand.gmorscrm.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.model.SaleList;
import tw.com.masterhand.gmorscrm.model.SignData;
import tw.com.masterhand.gmorscrm.model.WinData;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.setting.ConfigIndustry;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.tools.NumberFormater;
import tw.com.masterhand.gmorscrm.tools.TimeFormater;

public class ItemRankDetailCustomer extends RelativeLayout {
    @BindView(R.id.rootView)
    View rootView;
    @BindView(R.id.textView_index)
    TextView tvIndex;
    @BindView(R.id.textView_created_at)
    TextView tvCreatedAt;
    @BindView(R.id.textView_name)
    TextView tvName;
    @BindView(R.id.textView_percent)
    TextView tvPercent;
    @BindView(R.id.linearLayout_industry)
    LinearLayout industry;
    @BindView(R.id.linearLayout_amount)
    LinearLayout amount;
    @BindView(R.id.linearLayout_customer_name)
    LinearLayout customerName;
    @BindView(R.id.linearLayout_sale)
    LinearLayout salePercent;

    Context context;

    public ItemRankDetailCustomer(Context context) {
        super(context);
        init(context);
    }

    public ItemRankDetailCustomer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ItemRankDetailCustomer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context mContext) {
        context = mContext;
        // 連接畫面
        View view = inflate(getContext(), R.layout.item_rank_detail_customer, this);
        ButterKnife.bind(this, view);
    }

    public void setIndex(int index) {
        tvIndex.setText(String.valueOf(index));
    }

    public void setSignData(SignData data) {
        tvCreatedAt.setText(context.getString(R.string.create_time) + " " + TimeFormater.getInstance().toDateFormat(data.getProject().getFrom_date()));
        tvName.setText(data.getProject().getName());
        final TextView tvIndustry = industry.findViewWithTag("content");
        if (!TextUtils.isEmpty(data.getCustomer().getIndustry_id())) {
            DatabaseHelper.getInstance(context).getConfigIndustryById(data.getCustomer().getIndustry_id())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<ConfigIndustry>() {
                @Override
                public void accept(ConfigIndustry configIndustry) throws Exception {
                    tvIndustry.setText(configIndustry.getName());
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    ErrorHandler.getInstance().setException(context, throwable);
                }
            });
        }
        ((TextView) amount.findViewWithTag("content")).setText(NumberFormater.getMoneyFormat(data.getProject().getExpect_amount()));
        ((TextView) customerName.findViewWithTag("content")).setText(data.getCustomer().getName());
        setSalePercent(data.getPercent());
    }

    public void setWinData(WinData data) {
        tvCreatedAt.setText(context.getString(R.string.create_time) + " " + TimeFormater.getInstance().toDateFormat(data.getCustomer().getCreated_at()));
        tvName.setText(data.getProject().getName());
        final TextView tvIndustry = industry.findViewWithTag("content");
        if (!TextUtils.isEmpty(data.getCustomer().getIndustry_id())) {
            DatabaseHelper.getInstance(context).getConfigIndustryById(data.getCustomer().getIndustry_id())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<ConfigIndustry>() {
                @Override
                public void accept(ConfigIndustry configIndustry) throws Exception {
                    tvIndustry.setText(configIndustry.getName());
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    ErrorHandler.getInstance().setException(context, throwable);
                }
            });
        }
        ((TextView) amount.findViewWithTag("content")).setText(NumberFormater.getMoneyFormat(data.getProject().getExpect_amount()));
        ((TextView) customerName.findViewWithTag("content")).setText(data.getCustomer().getName());
        salePercent.setVisibility(GONE);
    }

    public void setSaleList(SaleList data) {
        tvCreatedAt.setText(context.getString(R.string.create_time) + " " + TimeFormater.getInstance().toDateFormat(data.getProject().getCreated_at()));
        tvName.setText(data.getProject().getName());
        final TextView tvIndustry = industry.findViewWithTag("content");
        if (!TextUtils.isEmpty(data.getCustomer().getIndustry_id())) {
            DatabaseHelper.getInstance(context).getConfigIndustryById(data.getCustomer().getIndustry_id()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<ConfigIndustry>() {

                @Override
                public void accept(ConfigIndustry configIndustry) throws Exception {
                    tvIndustry.setText(configIndustry.getName());
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    ErrorHandler.getInstance().setException(context, throwable);
                }
            });
        }
        ((TextView) amount.findViewWithTag("content")).setText(NumberFormater.getMoneyFormat(data.getProject().getExpect_amount()));
        ((TextView) customerName.findViewWithTag("content")).setText(data.getCustomer().getName());
        salePercent.setVisibility(GONE);
    }

    public void setSalePercent(int percent) {
        tvPercent.setText(String.valueOf(percent));
    }

    @Override
    public void setBackgroundColor(int color) {
        rootView.setBackgroundColor(color);
        super.setBackgroundColor(color);
    }
}
