package tw.com.masterhand.gmorscrm.activity.statistic;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tw.com.masterhand.gmorscrm.BaseWebCheckActivity;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.TokenManager;
import tw.com.masterhand.gmorscrm.activity.project.ProjectActivity;
import tw.com.masterhand.gmorscrm.api.ApiHelper;
import tw.com.masterhand.gmorscrm.enums.ProjectStatus;
import tw.com.masterhand.gmorscrm.model.SaleCount;
import tw.com.masterhand.gmorscrm.model.SaleList;
import tw.com.masterhand.gmorscrm.model.Stage;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.setting.Company;
import tw.com.masterhand.gmorscrm.room.setting.Department;
import tw.com.masterhand.gmorscrm.room.setting.DepartmentSalesOpportunity;
import tw.com.masterhand.gmorscrm.room.setting.User;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.tools.NumberFormater;
import tw.com.masterhand.gmorscrm.view.Appbar;
import tw.com.masterhand.gmorscrm.view.FilterCompany;
import tw.com.masterhand.gmorscrm.view.FilterDepartment;
import tw.com.masterhand.gmorscrm.view.FilterIndustry;
import tw.com.masterhand.gmorscrm.view.FilterPersonal;
import tw.com.masterhand.gmorscrm.view.FilterProductCategory;
import tw.com.masterhand.gmorscrm.view.FilterTime;
import tw.com.masterhand.gmorscrm.view.ItemCount;
import tw.com.masterhand.gmorscrm.view.ItemRankDetailCustomer;

public class StatisticSaleActivity extends BaseWebCheckActivity implements FilterPersonal.OnSelectedListener, FilterDepartment.OnSelectedListener, FilterCompany.OnSelectedListener {

    @BindView(R.id.appbar)
    Appbar appbar;
    @BindView(R.id.button_tab_left)
    Button btnAmount;
    @BindView(R.id.button_tab_right)
    Button btnTime;
    @BindView(R.id.container)
    LinearLayout container;
    @BindView(R.id.container_detail)
    LinearLayout containerDetail;
    @BindView(R.id.scrollView)
    ScrollView scrollView;
    @BindView(R.id.tvCondition)
    TextView tvCondition;
    @BindView(R.id.tvStage)
    TextView tvStage;
    @BindView(R.id.iconStage)
    View iconStage;
    @BindView(R.id.tvTotal)
    TextView tvTotal;
    @BindView(R.id.filterTime)
    FilterTime filterTime;
    @BindView(R.id.filterPersonal)
    FilterPersonal filterPersonal;
    @BindView(R.id.filterDepartment)
    FilterDepartment filterDepartment;
    @BindView(R.id.filterCompany)
    FilterCompany filterCompany;
    @BindView(R.id.filterIndustry)
    FilterIndustry filterIndustry;
    @BindView(R.id.containerFilter)
    LinearLayout containerFilter;
    @BindView(R.id.containerFilterItem)
    LinearLayout containerFilterItem;
    @BindView(R.id.containerEmpty)
    LinearLayout containerEmpty;

    ItemCount itemSelected;
    List<SaleList> list;
    DepartmentSalesOpportunity config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic_sale);
        appbar.setTitle(getString(R.string.statistic_menu_sale));
        init();
    }

    @Override
    protected void onUserChecked() {
        appbar.invalidate();
        mDisposable.add(DatabaseHelper.getInstance(this).getDepartmentSalesOpportunityByParent(TokenManager.getInstance().getUser().getCompany_id())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<DepartmentSalesOpportunity>() {
            @Override
            public void accept(DepartmentSalesOpportunity departmentSalesOpportunity) throws Exception {
                config = departmentSalesOpportunity;
                updateCount();
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                ErrorHandler.getInstance().setException(StatisticSaleActivity.this, throwable);
            }
        }));
    }

    protected void init() {
        list = new ArrayList<>();
        appbar.addFunction(getString(R.string.filter), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (containerFilter.getVisibility() == View.VISIBLE) {
                    containerFilter.setVisibility(View.GONE);
                    updateCount();
                } else
                    containerFilter.setVisibility(View.VISIBLE);
            }
        });

        filterPersonal.setSelected(TokenManager.getInstance().getUser());
        filterPersonal.setListener(this);
        filterDepartment.setListener(this);
        filterCompany.setListener(this);
        btnAmount.setSelected(true);
    }

    /**
     * 更新銷售階段總數
     */
    void updateCount() {
        updateCondition();
        scrollView.setVisibility(View.VISIBLE);
        container.removeAllViews();
        containerEmpty.removeAllViews();

        String userId = null;
        if (filterPersonal.getSelected() != null)
            userId = filterPersonal.getSelected().getId();

        String departmentId = null;
        if (filterDepartment.getSelected() != null)
            departmentId = filterDepartment.getSelected().getId();

        String companyId = null;
        if (filterCompany.getSelected() != null)
            companyId = filterCompany.getSelected().getId();

        String industryId = null;
        if (filterIndustry.getSelected() != null)
            industryId = filterIndustry.getSelected().getId();

        if (filterDepartment.isSelectAll()) {
            companyId = TokenManager.getInstance().getUser().getCompany_id();
        }

        ApiHelper.getInstance().getStatisticApi().getSaleCount(filterTime.getSelected(), userId, departmentId, companyId, industryId, TokenManager.getInstance().getToken())
                .enqueue(new Callback<JSONObject>() {

                    @Override
                    public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                        try {
                            switch (response.code()) {
                                case 200:
                                    JSONObject result = response.body();
                                    int success = result.getInt("success");
                                    if (success == 1) {
                                        SaleCount saleCount = new Gson().fromJson(result.getString("list"), SaleCount.class);
                                        int max = 0;// 最大數量
                                        for (Stage stage : saleCount.getAllStage()) {
                                            max = Math.max(max, stage.getCount());
                                        }
                                        if (max == 0) {
                                            Logger.e(TAG, "max count = 0");
                                            onNoData();
                                            return;
                                        }
                                        int i = 1;
                                        for (Stage stage : saleCount.getAllStage())
                                        {
                                            final ItemCount itemCount = new ItemCount(StatisticSaleActivity.this);
                                            itemCount.setProjectStatus(ProjectStatus.getProjectStatusByCode(i));
                                            if (i > 4) {
                                                itemCount.setName(config.getNameByStage(StatisticSaleActivity.this, itemCount.getStatus()));
                                            } else {
                                                itemCount.setName(getString(R.string.project_status) + i + "(" + config.getNameByStage(StatisticSaleActivity.this, itemCount.getStatus()) + ")");
                                            }

                                            itemCount.setStage(stage);
                                            itemCount.setProgress(max, stage.getCount());
                                            itemCount.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    selectItemCount(itemCount);
                                                }
                                            });

                                            container.addView(itemCount);

                                            if (i == 1) {
                                                selectItemCount(itemCount);
                                            }
                                            i++;
                                        }
                                    } else {
                                        String errorMsg = result.getString("errorMsg");
                                        if (TextUtils.isEmpty(errorMsg))
                                            errorMsg = "get statistic data failed.";

                                        Toast.makeText(StatisticSaleActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                                        onNoData();
                                    }
                                    break;
                                case ApiHelper.ERROR_TOKEN_EXPIRED:
                                    showLoginDialog();
                                    break;
                                default:
                                    Logger.e(TAG, "response code:" + response.code());
                                    onNoData();
                            }
                        } catch (Exception e) {
                            Logger.e(TAG, "Exception:" + e.getMessage());
                            onNoData();
                        }
                    }

                    @Override
                    public void onFailure(Call<JSONObject> call, Throwable t) {
                        Logger.e(TAG, "onFailure:" + t.getMessage());
                        onNoData();
                    }
                });
    }

    private void selectItemCount(ItemCount itemCount) {
        if (itemSelected != null)
            itemSelected.setSelected(false);

        itemSelected = itemCount;
        itemSelected.setSelected(true);
        iconStage.getBackground().setColorFilter(ContextCompat.getColor(StatisticSaleActivity.this, itemSelected.getStatus().getColor()), PorterDuff.Mode.MULTIPLY);
        tvStage.setText(itemSelected.getName());
        tvTotal.setText(NumberFormater.getMoneyFormat(itemSelected.getStage().getTotal()) + getString(R.string.money_unit));
        getData(itemSelected.getStatus());
    }

    void getData(ProjectStatus projectStatus) {
        containerDetail.removeAllViews();

        String userId = null;
        if (filterPersonal.getSelected() != null)
            userId = filterPersonal.getSelected().getId();

        String departmentId = null;
        if (filterDepartment.getSelected() != null)
            departmentId = filterDepartment.getSelected().getId();

        String companyId = null;
        if (filterCompany.getSelected() != null)
            companyId = filterCompany.getSelected().getId();

        if (filterDepartment.isSelectAll()) {
            companyId = TokenManager.getInstance().getUser().getCompany_id();
        }

        String industryId = null;
        if (filterIndustry.getSelected() != null)
            industryId = filterIndustry.getSelected().getId();

        ApiHelper.getInstance().getStatisticApi().getSaleList(filterTime.getSelected(), userId, departmentId, companyId, industryId, projectStatus.getValue(), TokenManager.getInstance().getToken())
                .enqueue(new Callback<JSONObject>() {

                    @Override
                    public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                        try {
                            switch (response.code()) {
                                case 200:
                                    JSONObject result = response.body();
                                    int success = result.getInt("success");
                                    if (success == 1) {
                                        list = gson.fromJson(result.getString("list"), new TypeToken<List<SaleList>>() {}.getType());
                                        updateList();
                                    } else {
                                        Toast.makeText(StatisticSaleActivity.this, "get statistic" + " data " + "failed.", Toast.LENGTH_SHORT).show();
                                    }
                                    break;
                            }
                        } catch (Exception e) {
                            Logger.e(TAG, "Exception:" + e.getMessage());
                        }
                    }

                    @Override
                    public void onFailure(Call<JSONObject> call, Throwable t) {
                        Logger.e(TAG, "onFailure:" + t.getMessage());
                    }
                });
    }

    /**
     * 更新銷售階段列表
     */
    void updateList() {
        containerDetail.removeAllViews();

        //按時間排序
        if (btnTime.isSelected()) {
            Collections.sort(list, new Comparator<SaleList>() {
                @Override
                public int compare(SaleList o1, SaleList o2) {
                    return o2.getProject().getFrom_date().compareTo(o1.getProject().getFrom_date());
                }
            });
        }
        //按金額排序
        else {
            Collections.sort(list, new Comparator<SaleList>() {
                @Override
                public int compare(SaleList o1, SaleList o2) {
                    return o2.getProject().getExpect_amount() > o1.getProject().getExpect_amount() ? 1 : -1;
                }
            });
        }

        int i = 0;
        for (final SaleList data : list) {
            final ItemRankDetailCustomer itemDetail = new ItemRankDetailCustomer(this);
            itemDetail.setIndex(i + 1);
            itemDetail.setSaleList(data);
            itemDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(StatisticSaleActivity.this, ProjectActivity.class);
                    intent.putExtra(MyApplication.INTENT_KEY_PROJECT, data.getProject().getId());
                    startActivity(intent);
                }
            });
            containerDetail.addView(itemDetail);
            i++;
        }
    }

    /**
     * 無資料時的顯示
     */
    void onNoData() {
        scrollView.setVisibility(View.GONE);
        containerEmpty.removeAllViews();
        containerEmpty.addView(getEmptyImageView(null));
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
        if (filterIndustry.getSelected() != null)
            condition += "/" + filterIndustry.getSelected().getName();
        tvCondition.setText(condition);
    }

    //按金額排序
    @OnClick(R.id.button_tab_left)
    void sortByAmount() {
        btnTime.setSelected(false);
        btnAmount.setSelected(true);
        updateList();
    }

    //按時間排序
    @OnClick(R.id.button_tab_right)
    void sortByTime() {
        btnTime.setSelected(true);
        btnAmount.setSelected(false);
        updateList();
    }

    @OnClick(R.id.btnComplete)
    void filterComplete() {
        containerFilter.setVisibility(View.GONE);
        updateCount();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        filterPersonal.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSelectedAllDepartment() {
        filterPersonal.clear();
        filterCompany.clear();
    }

    @Override
    public void onSelectedAllCompany() {
        filterPersonal.clear();
        filterDepartment.clear();
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
