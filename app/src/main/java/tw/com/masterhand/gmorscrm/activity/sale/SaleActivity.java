package tw.com.masterhand.gmorscrm.activity.sale;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import tw.com.masterhand.gmorscrm.BaseUserCheckActivity;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.TokenManager;
import tw.com.masterhand.gmorscrm.model.ProjectWithConfig;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.setting.Company;
import tw.com.masterhand.gmorscrm.room.setting.Department;
import tw.com.masterhand.gmorscrm.room.setting.User;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.view.Appbar;
import tw.com.masterhand.gmorscrm.view.ExpandableSale;
import tw.com.masterhand.gmorscrm.view.FilterCompany;
import tw.com.masterhand.gmorscrm.view.FilterDepartment;
import tw.com.masterhand.gmorscrm.view.FilterPersonal;
import tw.com.masterhand.gmorscrm.view.FilterTime;
import tw.com.masterhand.gmorscrm.view.ItemSale;

public class SaleActivity extends BaseUserCheckActivity implements FilterPersonal
        .OnSelectedListener, FilterDepartment.OnSelectedListener, FilterCompany.OnSelectedListener {
    @BindView(R.id.appbar)
    Appbar appbar;
    @BindView(R.id.containerStage1)
    ExpandableSale containerStage1;
    @BindView(R.id.containerStage2)
    ExpandableSale containerStage2;
    @BindView(R.id.containerStage3)
    ExpandableSale containerStage3;
    @BindView(R.id.containerStage4)
    ExpandableSale containerStage4;
    @BindView(R.id.containerStage5)
    ExpandableSale containerStage5;
    @BindView(R.id.containerStage6)
    ExpandableSale containerStage6;

    @BindView(R.id.filterTime)
    FilterTime filterTime;
    @BindView(R.id.filterPersonal)
    FilterPersonal filterPersonal;
    @BindView(R.id.filterDepartment)
    FilterDepartment filterDepartment;
    @BindView(R.id.filterCompany)
    FilterCompany filterCompany;
    @BindView(R.id.containerFilter)
    LinearLayout containerFilter;
    @BindView(R.id.containerFilterItem)
    LinearLayout containerFilterItem;
    @BindView(R.id.tvCondition)
    TextView tvCondition;

    Date start = new Date();
    Date end = new Date();
    String userId = null;
    String departmentId = null;
    String companyId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale);
        init();
    }

    @Override
    protected void onUserChecked() {
        appbar.invalidate();
        filterPersonal.setSelected(TokenManager.getInstance().getUser());
        updateList();
    }

    private void init() {
        appbar.setTitle(getString(R.string.title_activity_sale));
        appbar.addFunction(getString(R.string.filter), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 篩選
                if (containerFilter.getVisibility() == View.VISIBLE)
                    containerFilter.setVisibility(View.GONE);
                else
                    containerFilter.setVisibility(View.VISIBLE);
            }
        });
        containerStage1.showRadioButton(false);
        containerStage1.disableRadioClick();
        containerStage1.hidePercent();
        containerStage1.setCountAndAmount(0, 0);
        containerStage2.showRadioButton(false);
        containerStage2.disableRadioClick();
        containerStage2.hidePercent();
        containerStage2.setCountAndAmount(0, 0);
        containerStage3.showRadioButton(false);
        containerStage3.disableRadioClick();
        containerStage3.hidePercent();
        containerStage3.setCountAndAmount(0, 0);
        containerStage4.showRadioButton(false);
        containerStage4.disableRadioClick();
        containerStage4.hidePercent();
        containerStage4.setCountAndAmount(0, 0);
        containerStage5.showRadioButton(false);
        containerStage5.disableRadioClick();
        containerStage5.setPercent(100);
        containerStage5.setCountAndAmount(0, 0);
        containerStage6.showRadioButton(false);
        containerStage6.disableRadioClick();
        containerStage6.setPercent(0);
        containerStage6.setCountAndAmount(0, 0);
        containerStage1.setTitle(getString(R.string.project_status_1));
        containerStage2.setTitle(getString(R.string.project_status_2));
        containerStage3.setTitle(getString(R.string.project_status_3));
        containerStage4.setTitle(getString(R.string.project_status_4));
        containerStage5.setTitle(getString(R.string.project_status_win));
        containerStage6.setTitle(getString(R.string.project_status_lose));

        filterTime.setSelected(4);
        filterPersonal.setListener(this);
        filterDepartment.setListener(this);
        filterCompany.setListener(this);
    }

    void updateCondition() {
        String condition = getString(R.string.condition);
        condition += ":";
        condition += filterTime.getSelectedText();
        if (filterPersonal.getSelected() != null)
            condition += "/" + filterPersonal.getSelected().getShowName();
        if (filterDepartment.getSelected() != null)
            condition += "/" + filterDepartment.getSelected().getName();
        if (filterCompany.getSelected() != null)
            condition += "/" + filterCompany.getSelected().getName();
        tvCondition.setText(condition);
    }

    private void updateList() {
        mDisposable.clear();
        updateCondition();
        userId = null;
        departmentId = null;
        companyId = null;

        if (filterPersonal.getSelected() != null)
            userId = filterPersonal.getSelected().getId();

        if (filterDepartment.getSelected() != null)
            departmentId = filterDepartment.getSelected().getId();

        if (filterCompany.getSelected() != null)
            companyId = filterCompany.getSelected().getId();

        if (filterDepartment.isSelectAll()) {
            companyId = TokenManager.getInstance().getUser().getCompany_id();
        }

        Calendar now = Calendar.getInstance(Locale.getDefault());
        int timeSelected = filterTime.getSelected();
        switch (timeSelected) {
            case 0:
                //本月
                now.set(Calendar.DAY_OF_MONTH, 1);
                start = now.getTime();
                now.add(Calendar.MONTH, 1);
                now.add(Calendar.DATE, -1);
                end = now.getTime();
                break;
            case 1:
                //上月
                now.add(Calendar.MONTH, -1);
                now.set(Calendar.DAY_OF_MONTH, 1);
                start = now.getTime();
                now.add(Calendar.MONTH, 1);
                now.add(Calendar.DATE, -1);
                end = now.getTime();
                break;
            case 2:
                //本季度
                switch (now.get(Calendar.MONTH)) {
                    case Calendar.JANUARY:
                    case Calendar.FEBRUARY:
                    case Calendar.MARCH:
                        now.set(Calendar.MONTH, Calendar.JANUARY);
                        break;
                    case Calendar.APRIL:
                    case Calendar.MAY:
                    case Calendar.JUNE:
                        now.set(Calendar.MONTH, Calendar.APRIL);
                        break;
                    case Calendar.JULY:
                    case Calendar.AUGUST:
                    case Calendar.SEPTEMBER:
                        now.set(Calendar.MONTH, Calendar.JULY);
                        break;
                    case Calendar.OCTOBER:
                    case Calendar.NOVEMBER:
                    case Calendar.DECEMBER:
                        now.set(Calendar.MONTH, Calendar.OCTOBER);
                        break;
                }
                now.set(Calendar.DAY_OF_MONTH, 1);
                start = now.getTime();
                now.add(Calendar.MONTH, 3);
                now.add(Calendar.DATE, -1);
                end = now.getTime();
                break;
            case 3:
                //上季度
                switch (now.get(Calendar.MONTH)) {
                    case Calendar.JANUARY:
                    case Calendar.FEBRUARY:
                    case Calendar.MARCH:
                        now.add(Calendar.YEAR, -1);
                        now.set(Calendar.MONTH, Calendar.OCTOBER);
                        break;
                    case Calendar.APRIL:
                    case Calendar.MAY:
                    case Calendar.JUNE:
                        now.set(Calendar.MONTH, Calendar.JANUARY);
                        break;
                    case Calendar.JULY:
                    case Calendar.AUGUST:
                    case Calendar.SEPTEMBER:
                        now.set(Calendar.MONTH, Calendar.APRIL);
                        break;
                    case Calendar.OCTOBER:
                    case Calendar.NOVEMBER:
                    case Calendar.DECEMBER:
                        now.set(Calendar.MONTH, Calendar.JULY);
                        break;
                }
                now.set(Calendar.DAY_OF_MONTH, 1);
                start = now.getTime();
                now.add(Calendar.MONTH, 3);
                now.add(Calendar.DATE, -1);
                end = now.getTime();
                break;
            case 4:
                //本年
                now.set(Calendar.DAY_OF_YEAR, 1);
                now.set(Calendar.HOUR_OF_DAY, 0);
                now.set(Calendar.MINUTE, 0);
                now.set(Calendar.SECOND, 0);
                start = now.getTime();
                now.add(Calendar.YEAR, 1);
                now.add(Calendar.DATE, -1);
                now.set(Calendar.HOUR_OF_DAY, 23);
                now.set(Calendar.MINUTE, 59);
                now.set(Calendar.SECOND, 59);
                end = now.getTime();
                break;
            case 5:
                //去年
                now.add(Calendar.YEAR, -1);
                now.set(Calendar.DAY_OF_YEAR, 1);
                start = now.getTime();
                now.add(Calendar.YEAR, 1);
                now.add(Calendar.DATE, -1);
                end = now.getTime();
                break;
            case 6:
                //前年
                now.add(Calendar.YEAR, -2);
                now.set(Calendar.DAY_OF_YEAR, 1);
                start = now.getTime();
                now.add(Calendar.YEAR, 1);
                now.add(Calendar.DATE, -1);
                end = now.getTime();
                break;
        }
        containerStage1.clearListView();
        containerStage2.clearListView();
        containerStage3.clearListView();
        containerStage4.clearListView();
        containerStage5.clearListView();
        containerStage6.clearListView();
        mDisposable.add(DatabaseHelper.getInstance(this).getProject(start, end).filter(new Predicate<ProjectWithConfig>() {
            @Override
            public boolean test(@NonNull ProjectWithConfig projectWithConfig) throws Exception {
                Logger.e(TAG, projectWithConfig.getProject().getId() + projectWithConfig.getProject().getName());
                boolean check = true;
                if (TextUtils.isEmpty
                        (projectWithConfig.getSalesOpportunity().getId())) {
                    check = false;
                } else if (!TextUtils.isEmpty(userId) && !userId.equals(projectWithConfig
                        .getProject().getUser_id())) {
                    check = false;
                } else if (!TextUtils.isEmpty(departmentId) && !departmentId.equals
                        (projectWithConfig
                                .getDepartment().getId())) {
                    check = false;
                } else if (!TextUtils.isEmpty(companyId) && !companyId.equals
                        (projectWithConfig
                                .getDepartment().getCompany_id())) {
                    check = false;
                }
                return check;
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<ProjectWithConfig>() {

            @Override
            public void accept(ProjectWithConfig project) throws Exception {
                Logger.e(TAG, "project status:" + getString(project.getSalesOpportunity().getStage()
                        .getTitle()));
                Logger.e(TAG, "get project:" + project.getProject().getName());
                Logger.e(TAG, "get project:" + project.getProject().getId());
                Logger.e(TAG, "project deleted:" + project.getProject().getDeleted_at());
                ItemSale item = new ItemSale(SaleActivity.this);
                item.setProject(project);
                ExpandableSale container = null;
                switch (project.getSalesOpportunity().getStage()) {
                    case DESIGN:
                        container = containerStage1;
                        break;
                    case QUOTE:
                        container = containerStage2;
                        break;
                    case SAMPLE:
                        container = containerStage3;
                        break;
                    case NEGOTIATION:
                        container = containerStage4;
                        break;
                    case WIN:
                        container = containerStage5;
                        break;
                    case LOSE:
                        container = containerStage6;
                        break;
                }
                if (container != null) {
                    container.addListView(item);
                    container.setCountAndAmount(container.getCount() + 1,
                            container.getAmount() + project.getProject()
                                    .getExpect_amount());
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                ErrorHandler.getInstance().setException(SaleActivity.this, throwable);
                finish();
            }
        }));
    }

    @OnClick(R.id.btnComplete)
    void filterComplete() {
        containerFilter.setVisibility(View.GONE);
        updateList();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        filterPersonal.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSelectedAllDepartment() {
        filterCompany.clear();
        filterPersonal.clear();
    }

    @Override
    public void onSelectedAllCompany() {
        filterDepartment.clear();
        filterPersonal.clear();
    }

    @Override
    public void onSelected(Company company) {
        if (company != null) {
            filterDepartment.clear();
            filterPersonal.clear();
        }
    }

    @Override
    public void onSelected(Department department) {
        if (department != null) {
            filterCompany.clear();
            filterPersonal.clear();
        }
    }

    @Override
    public void onSelected(User user) {
        filterCompany.clear();
        filterDepartment.clear();
    }
}
