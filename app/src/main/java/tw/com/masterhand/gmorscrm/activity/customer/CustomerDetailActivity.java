package tw.com.masterhand.gmorscrm.activity.customer;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import org.reactivestreams.Subscription;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.FlowableSubscriber;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import tw.com.masterhand.gmorscrm.BaseUserCheckActivity;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.TokenManager;
import tw.com.masterhand.gmorscrm.activity.project.ProjectCreateActivity;
import tw.com.masterhand.gmorscrm.enums.ProjectStatus;
import tw.com.masterhand.gmorscrm.model.ProjectWithConfig;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.record.Customer;
import tw.com.masterhand.gmorscrm.room.record.Trip;
import tw.com.masterhand.gmorscrm.room.setting.DepartmentSalesOpportunity;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.tools.UnitChanger;
import tw.com.masterhand.gmorscrm.view.Appbar;
import tw.com.masterhand.gmorscrm.view.CustomerCard;
import tw.com.masterhand.gmorscrm.view.ItemProject;
import tw.com.masterhand.gmorscrm.view.ItemRecord;

public class CustomerDetailActivity extends BaseUserCheckActivity implements TabLayout.OnTabSelectedListener {
    @BindView(R.id.appbar)
    Appbar appbar;
    @BindView(R.id.relativeLayout_top)
    RelativeLayout containerTop;
    @BindView(R.id.container)
    LinearLayout container;
    @BindView(R.id.button_record)
    Button btnRecord;
    @BindView(R.id.button_item)
    Button btnItem;
    @BindView(R.id.imageButton_add)
    ImageButton btnAdd;
    @BindView(R.id.tabLayout)
    TabLayout tabSale;
    @BindView(R.id.scrollView)
    NestedScrollView scrollView;

    Customer customer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_detail);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onUserChecked() {
        appbar.invalidate();
        if (tabSale.getTabCount() < 2)
            mDisposable.add(DatabaseHelper.getInstance(this).getDepartmentSalesOpportunityByParent(TokenManager.getInstance().getUser().getCompany_id())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<DepartmentSalesOpportunity>() {
                        @Override
                        public void accept(DepartmentSalesOpportunity departmentSalesOpportunity)
                                throws
                                Exception {
                            Logger.e(TAG, "DepartmentSalesOpportunity:" + new Gson().toJson(departmentSalesOpportunity));
                            for (ProjectStatus status : ProjectStatus.values()) {
                                if (status != ProjectStatus.START) {
                                    String name = getString(status.getTitle());
                                    switch (status) {
                                        case DESIGN:
                                            name = departmentSalesOpportunity.stage_1_name;
                                            break;
                                        case QUOTE:
                                            name = departmentSalesOpportunity.stage_2_name;
                                            break;
                                        case SAMPLE:
                                            name = departmentSalesOpportunity.stage_3_name;
                                            break;
                                        case NEGOTIATION:
                                            name = departmentSalesOpportunity.stage_4_name;
                                            break;
                                    }
                                    int percent = ProjectStatus.getPercent(status, departmentSalesOpportunity);

                                    if (percent < 0)
                                    {
                                        percent = 0;
                                    }

                                    tabSale.addTab(generateTab(status, null, name, String.valueOf(String.valueOf(percent) + "%")));
                                }
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            ErrorHandler.getInstance().setException(CustomerDetailActivity.this, throwable);
                        }
                    }));

        loadCustomerData();
    }

    private void init() {
        // 工作階段TAB
        Drawable icon = ContextCompat.getDrawable(this, R.mipmap.common_tra_gonext);
        if (icon != null)
        {
            icon.setColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY);
        }

        tabSale.addTab(generateTab(null, icon, getString(R.string.project_status_all), ""));//全部項目
        tabSale.addOnTabSelectedListener(this);

        // 預設選擇組員記錄
        btnRecord.setSelected(true);
    }

    void loadCustomerData() {
        if (customer != null)
            return;
        // 取得客戶基本資訊
        String customerId = getIntent().getStringExtra(MyApplication.INTENT_KEY_CUSTOMER);
        if (TextUtils.isEmpty(customerId)) {
            finish();
        }
        Logger.e(TAG, "" + customerId);

        mDisposable.add(DatabaseHelper.getInstance(this).getCustomerById(customerId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Customer>() {
                    @Override
                    public void accept(Customer result) throws Exception {
                        customer = result;
                        appbar.setTitle(customer.getName());
                        CustomerCard customerCard = new CustomerCard(CustomerDetailActivity.this);
                        customerCard.setCustomer(customer);
                        customerCard.setCardListener(null);//取消事件
                        containerTop.addView(customerCard);
                        containerTop.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View view, MotionEvent motionEvent) {
                                return true;
                            }
                        });
                        // 刷新列表
                        updateList();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ErrorHandler.getInstance().setException(CustomerDetailActivity.this, throwable);
                    }
                }));
    }

    /**
         * 產生工作階段tab項目
         */
    private TabLayout.Tab generateTab(ProjectStatus tag, Drawable icon, String title, String subtitle) {
        TabLayout.Tab tab = tabSale.newTab();
        View view = getLayoutInflater().inflate(R.layout.tab, tabSale, false);
        TextView tvTitle = view.findViewById(R.id.textView_title);
        TextView tvSubtitle = view.findViewById(R.id.textView_subtitle);
        tvTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(icon, null, null, null);
        tvTitle.setText(title);

        if (tag != ProjectStatus.LOSE)
        {
            tvSubtitle.setText(subtitle);
        }

        tab.setCustomView(view);
        tab.setTag(tag);
        return tab;
    }

    /**
         * 刷新資料列表
         */
    private void updateList() {
        containerTop.animate().translationY(0).setInterpolator(new LinearInterpolator()).start();
        container.removeAllViews();
        if (btnRecord.isSelected()) {
            // 組員記錄
            DatabaseHelper.getInstance(this).getTripByCustomer(customer.getId())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new FlowableSubscriber<Trip>() {

                @Override
                public void onSubscribe(@NonNull Subscription s) {
                    s.request(Integer.MAX_VALUE);
                }

                @Override
                public void onNext(Trip trip) {
                    ItemRecord record = new ItemRecord(CustomerDetailActivity.this);
                    record.setTrip(trip);
                    container.addView(record);
                }

                @Override
                public void onError(Throwable t) {
                    ErrorHandler.getInstance().setException(CustomerDetailActivity.this, t);
                }

                @Override
                public void onComplete() {
                    if (container.getChildCount() == 0) {
                        container.addView(getEmptyImageView(null));
                    }
                }
            });
        } else {
            // 工作項目
            DatabaseHelper.getInstance(this).getProjectByCustomer(customer.getId(),
                    TokenManager.getInstance().getUser().getDepartment_id(), TokenManager.getInstance().getUser().getCompany_id())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new FlowableSubscriber<ProjectWithConfig>() {

                int index = 1;
                ProjectStatus status;

                @Override
                public void onSubscribe(@NonNull Subscription s) {
                    s.request(Integer.MAX_VALUE);
                    TabLayout.Tab tab = tabSale.getTabAt(tabSale.getSelectedTabPosition());
                    status = null;
                    if (tab.getTag() != null) {
                        status = (ProjectStatus) tab.getTag();
                        Logger.e(TAG, "tab selected:" + getString(status.getTitle()));
                        switch (status) {
                            case DESIGN:
                                break;
                            case QUOTE:
                                break;
                            case SAMPLE:
                                break;
                            case NEGOTIATION:
                                break;
                            case WIN:
                                break;
                            case LOSE:
                                break;
                        }
                    } else {
                        Logger.e(TAG, "tab selected:" + getString(R.string.project_status_all));
                    }
                }

                @Override
                public void onNext(ProjectWithConfig project) {
                    if (status == null || status == project.getSalesOpportunity().getStage()) {
                        ItemProject item = new ItemProject(CustomerDetailActivity.this);
                        item.setIndex(String.valueOf(index));
                        item.setProject(project);
                        container.addView(item);
                        index++;
                    }
                }

                @Override
                public void onError(Throwable t) {
                    ErrorHandler.getInstance().setException(CustomerDetailActivity.this, t);
                }

                @Override
                public void onComplete() {
                    if (container.getChildCount() == 0)
                        container.addView(getEmptyImageView(null));
                }
            });
        }
    }

    //組員紀錄、工作項目
    @OnClick({R.id.button_record, R.id.button_item})
    void onMainTabSelected(View view) {
        btnRecord.setSelected(false);
        btnItem.setSelected(false);
        view.setSelected(true);
        containerTop.animate().translationY(0).setInterpolator(new LinearInterpolator()).start();
        int padding = UnitChanger.dpToPx(8);
        LinearLayout.LayoutParams selected, unselected;

        switch (view.getId()) {
            // 組員記錄
            case R.id.button_record:
                selected = (LinearLayout.LayoutParams) btnRecord.getLayoutParams();
                selected.weight = 2;//比例
                btnRecord.setLayoutParams(selected);
                unselected = (LinearLayout.LayoutParams) btnItem.getLayoutParams();
                unselected.weight = 1;
                btnItem.setLayoutParams(unselected);
                btnAdd.setVisibility(View.GONE);
                tabSale.setVisibility(View.GONE);
                scrollView.setPadding(padding, UnitChanger.dpToPx(350), padding, padding);
                break;
            // 工作項目
            case R.id.button_item:
                selected = (LinearLayout.LayoutParams) btnItem.getLayoutParams();
                selected.weight = 2;//比例
                btnItem.setLayoutParams(selected);
                unselected = (LinearLayout.LayoutParams) btnRecord.getLayoutParams();
                unselected.weight = 1;
                btnRecord.setLayoutParams(unselected);
                btnAdd.setVisibility(View.VISIBLE);
                tabSale.setVisibility(View.VISIBLE);
                scrollView.setPadding(padding, UnitChanger.dpToPx(400), padding, padding);
                break;
        }
        updateList();
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        updateList();
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    /**
         * 新增工作項目
         */
    @OnClick(R.id.imageButton_add)
    void addItem() {
        Intent intent = new Intent(this, ProjectCreateActivity.class);
        intent.putExtra(MyApplication.INTENT_KEY_CUSTOMER, customer.getId());
        startActivityForResult(intent, MyApplication.REQUEST_ADD_PROJECT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case MyApplication.REQUEST_ADD_OPPORTUNITY: {
                    updateList();
                    break;
                }
                case MyApplication.REQUEST_ADD_PROJECT: {
                    String projectId = data.getStringExtra(MyApplication.INTENT_KEY_PROJECT);
                    mDisposable.add(DatabaseHelper.getInstance(this).getProjectById(projectId,
                                    TokenManager.getInstance().getUser().getDepartment_id(),
                                    TokenManager.getInstance().getUser().getCompany_id())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<ProjectWithConfig>() {
                                @Override
                                public void accept(ProjectWithConfig projectWithConfig) throws Exception {
                                    TabLayout.Tab tab = tabSale.getTabAt(tabSale.getSelectedTabPosition());
                                    ProjectStatus status = (ProjectStatus) tab.getTag();
                                    if (status == null || status == projectWithConfig.getSalesOpportunity().getStage()) {
                                        ItemProject item = new ItemProject(CustomerDetailActivity.this);
                                        item.setIndex(String.valueOf(container.getChildCount() + 1));
                                        item.setProject(projectWithConfig);
                                        container.addView(item);
                                    }
                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) throws Exception {
                                    Logger.e(TAG, "error:" + throwable.getMessage());
                                }
                            }));
                    break;
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
