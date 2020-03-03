package tw.com.masterhand.gmorscrm;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dgreenhalgh.android.simpleitemdecoration.linear.DividerItemDecoration;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import tw.com.masterhand.gmorscrm.adapter.CustomerSelectAdapter;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.record.Customer;
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.view.CustomerSelectCard;

public class CustomerSelectActivity extends BaseUserCheckActivity implements CustomerSelectAdapter.CustomerSelectListener {
    @BindView(R.id.btnBack)
    ImageButton btnBack;
    @BindView(R.id.btnSearch)
    ImageButton btnSearch;
    @BindView(R.id.etSearch)
    EditText etSearch;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.container)
    RecyclerView container;

    boolean shouldCheck = false;
    boolean enableEmpty = false;
    CustomerSelectAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_select);
        init();
    }

    @Override
    protected void onUserChecked() {
        getData();
    }

    private void init() {
        tvTitle.setText(getString(R.string.select_customer));
        shouldCheck = getIntent().getBooleanExtra(MyApplication.INTENT_KEY_CUSTOMER_CHECK, false);
        enableEmpty = getIntent().getBooleanExtra(MyApplication.INTENT_KEY_ENABLE, false);

        container.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration divider = new DividerItemDecoration(ContextCompat.getDrawable(container.getContext(), R.drawable.divider_gray_v));
        container.addItemDecoration(divider);
        adapter = new CustomerSelectAdapter(this);
        adapter.setListener(this);
        container.setAdapter(adapter);
    }

    /**
     * 鍵盤點擊搜尋
     */
    @OnEditorAction(R.id.etSearch)
    boolean imeSearch(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            getSearchData();
            return true;
        }
        return false;
    }

    /**
     * 點擊搜尋鈕
     */
    @OnClick(R.id.btnSearch)
    void search() {
        if (etSearch.getVisibility() == View.GONE) {
            etSearch.setVisibility(View.VISIBLE);
            tvTitle.setVisibility(View.GONE);
            btnBack.setImageResource(R.mipmap.common_close);
        } else {
            getSearchData();
        }
    }

    /**
     * 點擊返回/關閉鈕
     */
    @OnClick(R.id.btnBack)
    @Override
    public void onBackPressed() {
        if (etSearch.getVisibility() == View.GONE) {
            super.onBackPressed();
        } else {
            etSearch.setVisibility(View.GONE);
            tvTitle.setVisibility(View.VISIBLE);
            btnBack.setImageResource(R.mipmap.common_left_black);
            InputMethodManager imm = (InputMethodManager) getSystemService(Context
                    .INPUT_METHOD_SERVICE);
            if (imm != null)
                imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
            getData();
        }
    }

    /**
     * 取得所有客戶列表
     */
    void getData() {
        mDisposable.add(DatabaseHelper.getInstance(this).getCustomer(TokenManager.getInstance()
                .getUser().getId()
        ).toList().observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<Customer>>() {

            @Override
            public void accept(List<Customer> customers) throws Exception {
                updateList(customers);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                adapter.showEmpty();
                Toast.makeText(CustomerSelectActivity.this, throwable.getMessage(), Toast
                        .LENGTH_LONG).show();
            }
        }));
    }

    /**
     * 取得搜尋客戶列表
     */
    void getSearchData() {
        String keyword = etSearch.getText().toString();
        if (TextUtils.isEmpty(keyword)) {
            container.addView(getEmptyImageView(null));
            return;
        }
        keyword = "%" + keyword + "%";
        Logger.e(TAG, "search keyword:" + keyword);
        mDisposable.add(DatabaseHelper.getInstance(this).searchCustomer(keyword, TokenManager
                .getInstance().getUser().getId()).toList().observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Customer>>() {
                    @Override
                    public void accept(List<Customer> customers) throws Exception {
                        updateList(customers);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        adapter.showEmpty();
                        Toast.makeText(CustomerSelectActivity.this, throwable.getMessage(), Toast
                                .LENGTH_LONG).show();
                    }
                }));
    }

    /**
     * 刷新客戶列表
     */
    void updateList(List<Customer> customers) {
        adapter.clear();
        adapter.setCustomers(customers);
    }

    @Override
    public void onCustomerSelected(Customer customer) {
        if (checkStatus(customer)) {
            Intent intent = new Intent();
            intent.putExtra(MyApplication.INTENT_KEY_CUSTOMER, customer.getId());
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    /**
     * 檢查客戶開通狀態
     */
    private boolean checkStatus(Customer customer) {
        if (!shouldCheck)
            return true;
        return customer.getStatus();
    }
}
