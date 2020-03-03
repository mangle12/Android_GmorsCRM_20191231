package tw.com.masterhand.gmorscrm.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import tw.com.masterhand.gmorscrm.CustomerSelectActivity;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.activity.task.ContractDetailActivity;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.record.Customer;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.tools.TimeFormater;

import static android.app.Activity.RESULT_OK;

public class ItemSelectCustomer extends RelativeLayout {
    @BindView(R.id.btnAdd)
    Button btnAdd;

    Activity context;
    Customer customer;

    boolean shouldCheck = false;

    public interface CustomerSelectListener {
        void onCustomerSelected(Customer customer);
    }

    ArrayList<CustomerSelectListener> listeners;

    public ItemSelectCustomer(Context context) {
        super(context);
        init(context);
    }

    public ItemSelectCustomer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ItemSelectCustomer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context mContext) {
        // 連接畫面
        View view = inflate(getContext(), R.layout.item_select_customer, this);
        ButterKnife.bind(this, view);
        if (isInEditMode())
            return;
        context = (Activity) mContext;
        listeners = new ArrayList<>();
    }

    public void setShouldCheck(boolean shouldCheck) {
        this.shouldCheck = shouldCheck;
    }

    @OnClick(R.id.btnAdd)
    void selectCustomer() {
        Intent intent = new Intent(context, CustomerSelectActivity.class);
        intent.putExtra(MyApplication.INTENT_KEY_CUSTOMER_CHECK, shouldCheck);
        context.startActivityForResult(intent, MyApplication.REQUEST_SELECT_CUSTOMER);
    }

    public void disableSelectCustomer() {
        btnAdd.setEnabled(false);
        btnAdd.setOnClickListener(null);
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
        removeAllViews();
        CompanyCard companyCard = new CompanyCard(context);
        companyCard.setCustomer(customer);
        companyCard.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectCustomer();
            }
        });
        companyCard.ivMap.setVisibility(GONE);
        addView(companyCard, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));

        if (listeners.size() > 0) {
            for (CustomerSelectListener listener : listeners)
                listener.onCustomerSelected(customer);
        }
    }

    public void addCustomerSelectedListener(CustomerSelectListener listener) {
        listeners.add(listener);
    }

    public Customer getCustomer() {
        return customer;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                // 選擇客戶
                case MyApplication.REQUEST_SELECT_CUSTOMER:
                    String customerId = data.getStringExtra(MyApplication.INTENT_KEY_CUSTOMER);
                    if (!TextUtils.isEmpty(customerId)) {
                        DatabaseHelper.getInstance(context).getCustomerById(customerId)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Consumer<Customer>() {

                            @Override
                            public void accept(Customer customer) throws Exception {
                                setCustomer(customer);
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                ErrorHandler.getInstance().setException(context, throwable);
                            }
                        });
                    }
                    break;
            }
        }
    }
}
