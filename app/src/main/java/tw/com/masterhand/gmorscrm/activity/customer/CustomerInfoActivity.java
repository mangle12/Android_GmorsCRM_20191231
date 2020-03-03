package tw.com.masterhand.gmorscrm.activity.customer;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import tw.com.masterhand.gmorscrm.BaseUserCheckActivity;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.fragments.CustomerInfoFragment;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.record.Customer;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.view.Appbar;

public class CustomerInfoActivity extends BaseUserCheckActivity {
    @BindView(R.id.appbar)
    Appbar appbar;
    @BindView(R.id.container)
    FrameLayout container;

    String customerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_info);
        init();
    }

    @Override
    protected void onUserChecked() {
        appbar.invalidate();
        mDisposable.add(DatabaseHelper.getInstance(this).getCustomerById(customerId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Customer>() {

            @Override
            public void accept(Customer customer) throws Exception {

                // 2018/4/11新增#169:未開通客戶才可進行修改
                appbar.removeFuction();
                if (!customer.getStatus())
                    appbar.addFunction(R.mipmap.common_edit, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(CustomerInfoActivity.this, CustomerCreateActivity.class);
                            intent.putExtra(MyApplication.INTENT_KEY_CUSTOMER, customerId);
                            startActivityForResult(intent, MyApplication.REQUEST_EDIT);
                        }
                    });
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                ErrorHandler.getInstance().setException(CustomerInfoActivity.this, throwable);
            }
        }));
    }

    private void init() {
        appbar.setTitle(getString(R.string.title_activity_customer_info));
        customerId = getIntent().getStringExtra(MyApplication.INTENT_KEY_CUSTOMER);

        if (TextUtils.isEmpty(customerId))
        {
            finish();
        }

        getSupportFragmentManager().beginTransaction().add(R.id.container, CustomerInfoFragment.newInstance(customerId)).commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case MyApplication.REQUEST_EDIT:
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, CustomerInfoFragment.newInstance(customerId)).commit();
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
