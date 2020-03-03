package tw.com.masterhand.gmorscrm.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.gson.Gson;

import org.reactivestreams.Subscription;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.FlowableSubscriber;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.model.ConfigCurrencyWithRate;
import tw.com.masterhand.gmorscrm.tools.Logger;

public class ItemSelectCurrency extends RelativeLayout {
    @BindView(R.id.btnCurrency)
    Button btnCurrency;

    Context context;

    ArrayList<ConfigCurrencyWithRate> configList;
    ConfigCurrencyWithRate selected;

    public ItemSelectCurrency(Context context) {
        super(context);
        init(context);
    }

    public ItemSelectCurrency(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ItemSelectCurrency(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context mContext) {
        context = mContext;
        configList = new ArrayList<>();
        // 連接畫面
        View view = inflate(getContext(), R.layout.item_select_currency, this);
        ButterKnife.bind(this, view);
        if (isInEditMode())
            return;
        DatabaseHelper.getInstance(context).getConfigCurrency()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new FlowableSubscriber<ConfigCurrencyWithRate>() {

            @Override
            public void onSubscribe(@NonNull Subscription s) {
                s.request(Integer.MAX_VALUE);
            }

            @Override
            public void onNext(ConfigCurrencyWithRate configCurrency) {
                configList.add(configCurrency);
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onComplete() {
                if (configList.size() > 0) {
                    Logger.e("ItemSelectCurrency", "ConfigCurrencyWithRate:" + new Gson().toJson(configList));
                    setConfigCurrency(configList.get(0));
                }
            }
        });
    }

    @OnClick(R.id.btnCurrency)
    void selectCurrency() {
        showDialog();
    }

    private void showDialog() {
        btnCurrency.setEnabled(false);
        String[] items = new String[configList.size()];
        for (int i = 0; i < items.length; i++) {
            items[i] = configList.get(i).getConfigCurrency().getName();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context, MyApplication.DIALOG_STYLE);
        builder.setCancelable(false).setTitle(R.string.currency_used).setItems(items, new
                DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setConfigCurrency(configList.get(which));
                    }
                });
        builder.create().show();
    }

    public void setConfigCurrency(String rateId) {
        DatabaseHelper.getInstance(context).getConfigCurrencyByRate(rateId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ConfigCurrencyWithRate>() {
                    @Override
                    public void accept(ConfigCurrencyWithRate configCurrency) throws Exception {
                        setConfigCurrency(configCurrency);
                    }
                });
    }

    public void setConfigCurrency(ConfigCurrencyWithRate configCurrency) {
        selected = configCurrency;
        btnCurrency.setText(selected.getConfigCurrency().getName());
        btnCurrency.setEnabled(true);
    }

    public ConfigCurrencyWithRate getConfigCurrency() {
        return selected;
    }

    public void disable() {
        btnCurrency.setEnabled(false);
    }
}
