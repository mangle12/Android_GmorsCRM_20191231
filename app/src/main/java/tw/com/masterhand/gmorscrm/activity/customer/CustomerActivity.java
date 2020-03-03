package tw.com.masterhand.gmorscrm.activity.customer;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.dgreenhalgh.android.simpleitemdecoration.linear.DividerItemDecoration;
import com.dgreenhalgh.android.simpleitemdecoration.linear.EndOffsetItemDecoration;
import com.dgreenhalgh.android.simpleitemdecoration.linear.StartOffsetItemDecoration;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import tw.com.masterhand.gmorscrm.BaseUserCheckActivity;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.TokenManager;
import tw.com.masterhand.gmorscrm.adapter.CustomerAreaListAdapter;
import tw.com.masterhand.gmorscrm.adapter.CustomerListAdapter;
import tw.com.masterhand.gmorscrm.enums.CustomerSort;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.record.Customer;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.tools.UnitChanger;
import tw.com.masterhand.gmorscrm.view.Appbar;

public class CustomerActivity extends BaseUserCheckActivity {
    @BindView(R.id.appbar)
    Appbar appbar;
    @BindView(R.id.container)
    RecyclerView container;
    @BindView(R.id.containerArea)
    RecyclerView containerArea;
    @BindView(R.id.linearLayout_sort)
    LinearLayout sortContainer;
    @BindView(R.id.button_sort_important)
    Button btnSortImportant;
    @BindView(R.id.button_sort_area)
    Button btnSortArea;
    @BindView(R.id.button_sort_word)
    Button btnSortWord;

    CustomerSort sort;
    List<Customer> importList;
    List<Customer> dateList;
    List<Customer> areaList;

    CustomerAreaListAdapter areaAdapter;
    CustomerListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);
        init();
    }

    @Override
    protected void onUserChecked() {
        appbar.invalidate();
        getCustomer();
    }

    private void init() {
        appbar.setTitle(getString(R.string.main_menu_customer));
        appbar.addFunction(R.mipmap.common_search, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 搜尋
                Intent intent = new Intent(v.getContext(), CustomerSearchActivity.class);
                startActivity(intent);
            }
        });
        appbar.addFunction(R.mipmap.common_add, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 新增客戶
                Intent intent = new Intent(v.getContext(), CustomerCreateActivity.class);
                startActivity(intent);
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        container.setLayoutManager(layoutManager);
        LinearLayoutManager areaLayoutManager = new LinearLayoutManager(this);
        areaLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        containerArea.setLayoutManager(areaLayoutManager);
        Drawable dividerDrawable = ContextCompat.getDrawable(this, R.drawable.divider_transparent_v_8dp);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(dividerDrawable);
        container.addItemDecoration(dividerItemDecoration);
        containerArea.addItemDecoration(dividerItemDecoration);
        container.addItemDecoration(new StartOffsetItemDecoration(UnitChanger.dpToPx(8)));
        container.addItemDecoration(new EndOffsetItemDecoration(UnitChanger.dpToPx(8)));
        adapter = new CustomerListAdapter(CustomerActivity.this);
    }

    private void getCustomer() {
        if (!TokenManager.getInstance().checkToken()) {
            finish();
        }
        startProgressDialog();
        importList = new ArrayList<>();
        dateList = new ArrayList<>();
        areaList = new ArrayList<>();

        mDisposable.add(DatabaseHelper.getInstance(this).getCustomer(TokenManager.getInstance().getUser().getId()).doOnNext(new Consumer<Customer>() {
            @Override
            public void accept(Customer customer) throws Exception {
                if (customer != null && customer.isType())
                    importList.add(customer);
            }}).toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Customer>>() {

            @Override
            public void accept(List<Customer> customers) throws Exception {

                dateList = customers;
                sort = CustomerSort.IMPORTANT;
                btnSortArea.setSelected(false);
                btnSortWord.setSelected(false);
                btnSortImportant.setSelected(true);
                btnSortArea.setText(getString(R.string.customer_sort_area) + "\n" + dateList.size());
                btnSortWord.setText(getString(R.string.customer_sort_time) + "\n" + dateList.size());
                btnSortImportant.setText(getString(R.string.customer_sort_important) + "\n" + importList.size());
                updateList();
                stopProgressDialog();
            }
        }));
    }

    private void prepareAreaData(final List<Customer> customers) {
        mDisposable.add(Single.create(new SingleOnSubscribe<List<Customer>>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<List<Customer>> emitter) throws Exception {
                areaList = new ArrayList<>();
                List<Customer> tempList = new ArrayList<>(customers);
                final Comparator comparatorArea = Collator.getInstance(Locale.TRADITIONAL_CHINESE);

                Collections.sort(tempList, new Comparator<Customer>() {
                    @Override
                    public int compare(Customer customer1, Customer customer2) {
                        return comparatorArea.compare(customer1.getAddress().getCountry().getId(), customer2.getAddress().getCountry().getId());
                    }
                });

                String countryId = "";
                if (tempList.size() > 0) {
                    for (Customer customer : tempList) {
                        if (TextUtils.isEmpty(countryId) || !countryId.equals(customer.getAddress().getCountry().getId())) {
                            countryId = customer.getAddress().getCountry().getId();
                            Customer labelCustomer = new Customer();
                            labelCustomer.setAddress(customer.getAddress());
                            areaList.add(labelCustomer);
                        }
                        areaList.add(customer);
                    }
                }
                emitter.onSuccess(areaList);
            }
        }).subscribeOn(Schedulers.single())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Customer>>() {
                    @Override
                    public void accept(List<Customer> customers) throws Exception {
                        areaAdapter = new CustomerAreaListAdapter(CustomerActivity.this, areaList);
                        containerArea.setAdapter(areaAdapter);
                        stopProgressDialog();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ErrorHandler.getInstance().setException(CustomerActivity.this, throwable);
                        stopProgressDialog();
                    }
                }));
    }

    /**
     * 更新列表
     */
    private void updateList() {
        containerArea.setVisibility(View.GONE);
        container.setVisibility(View.GONE);
        switch (sort) {
            // 重點客戶
            case IMPORTANT:
                container.setVisibility(View.VISIBLE);
                adapter.clear();
                adapter.setCustomers(importList);
                if (adapter.getItemCount() == 0)
                    adapter.showEmpty();
                container.setAdapter(adapter);
                break;
            // 地區排序
            case AREA:
                containerArea.setVisibility(View.VISIBLE);
                if (areaList.isEmpty()) {
                    startProgressDialog();
                    prepareAreaData(dateList);
                }
                break;
            // 筆畫排序
            case WORD:
                container.setVisibility(View.VISIBLE);
                adapter.clear();
                adapter.setCustomers(dateList);
                if (adapter.getItemCount() == 0)
                    adapter.showEmpty();
                container.setAdapter(adapter);
                break;
        }
    }

    @OnClick({R.id.button_sort_important, R.id.button_sort_area, R.id.button_sort_word})
    void onTabSelected(View view) {
        for (int i = 0; i < sortContainer.getChildCount(); i++) {
            sortContainer.getChildAt(i).setSelected(false);
        }
        view.setSelected(true);
        switch (view.getId()) {
            case R.id.button_sort_important:
                sort = CustomerSort.IMPORTANT;
                break;
            case R.id.button_sort_area:
                sort = CustomerSort.AREA;
                break;
            case R.id.button_sort_word:
                sort = CustomerSort.WORD;
                break;
        }
        updateList();
    }
}
