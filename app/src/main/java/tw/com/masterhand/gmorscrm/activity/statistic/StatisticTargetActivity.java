package tw.com.masterhand.gmorscrm.activity.statistic;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tw.com.masterhand.gmorscrm.BaseWebCheckActivity;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.TokenManager;
import tw.com.masterhand.gmorscrm.api.ApiHelper;
import tw.com.masterhand.gmorscrm.model.StatisticTargetData;
import tw.com.masterhand.gmorscrm.room.setting.Company;
import tw.com.masterhand.gmorscrm.room.setting.Department;
import tw.com.masterhand.gmorscrm.room.setting.User;
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.tools.NumberFormater;
import tw.com.masterhand.gmorscrm.view.Appbar;
import tw.com.masterhand.gmorscrm.view.ArcProgressBar;
import tw.com.masterhand.gmorscrm.view.FilterCheckPoint;
import tw.com.masterhand.gmorscrm.view.FilterCompany;
import tw.com.masterhand.gmorscrm.view.FilterDepartment;
import tw.com.masterhand.gmorscrm.view.FilterPersonal;
import tw.com.masterhand.gmorscrm.view.FilterTime;
import tw.com.masterhand.gmorscrm.view.ItemStatisticPersonal;

public class StatisticTargetActivity extends BaseWebCheckActivity implements FilterPersonal.OnSelectedListener, FilterDepartment.OnSelectedListener, FilterCompany.OnSelectedListener {

    @BindView(R.id.appbar)
    Appbar appbar;
    @BindView(R.id.arcProgressBar)
    ArcProgressBar arcProgressBar;
    @BindView(R.id.filterTime)
    FilterTime filterTime;
    @BindView(R.id.filterPersonal)
    FilterPersonal filterPersonal;
    @BindView(R.id.filterDepartment)
    FilterDepartment filterDepartment;
    @BindView(R.id.filterCompany)
    FilterCompany filterCompany;
    @BindView(R.id.filterCheckPoint)
    FilterCheckPoint filterCheckPoint;
    @BindView(R.id.textView_percent)
    TextView tvPercent;
    @BindView(R.id.textView_percent_start)
    TextView tvPercentStart;
    @BindView(R.id.textView_percent_end)
    TextView tvPercentEnd;
    @BindView(R.id.textView_tag_current)
    TextView tvTagCurrent;
    @BindView(R.id.textView_tag_last)
    TextView tvTagLast;
    @BindView(R.id.textView_goal)
    TextView tvGoal;
    @BindView(R.id.textView_current)
    TextView tvCurrent;
    @BindView(R.id.tvCondition)
    TextView tvCondition;

    @BindView(R.id.containerFilter)
    LinearLayout containerFilter;
    @BindView(R.id.containerFilterItem)
    LinearLayout containerFilterItem;

    @BindView(R.id.container)
    LinearLayout container;

    int percentStart, percentEnd, percentCurrent, percentLast;

    StatisticTargetData data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic_target);
        init();
    }

    @Override
    protected void onUserChecked() {
        appbar.invalidate();
        updateDetail();
    }

    private void init() {
        percentStart = 0;
        percentEnd = 100;
        data = new StatisticTargetData();
        appbar.setTitle(getString(R.string.statistic_menu_target));
        appbar.addFunction(getString(R.string.filter), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (containerFilter.getVisibility() == View.VISIBLE) {
                    containerFilter.setVisibility(View.GONE);
                    updateDetail();
                } else
                    containerFilter.setVisibility(View.VISIBLE);
            }
        });

        filterPersonal.setSelected(TokenManager.getInstance().getUser());//預設帶登入者
        filterPersonal.setListener(this);
        filterDepartment.setListener(this);
        filterCompany.setListener(this);
        filterTime.disableYearBeforeLast();
    }

    void updateDetail() {
        container.removeAllViews();
        updateCondition();

        String userId = null;
        if (filterPersonal.getSelected() != null)
            userId = filterPersonal.getSelected().getId();

        String departmentId = null;
        if (filterDepartment.getSelected() != null)
            departmentId = filterDepartment.getSelected().getId();

        String companyId = null;
        if (filterCompany.getSelected() != null)
            companyId = filterCompany.getSelected().getId();

        if (filterDepartment.isSelectAll())
            companyId = TokenManager.getInstance().getUser().getCompany_id();

        ApiHelper.getInstance().getStatisticApi().getSaleTarget(filterTime.getSelected(), userId,
                departmentId, filterCheckPoint.getSelected(), TokenManager.getInstance().getToken())
                .enqueue(new Callback<JSONObject>() {

                    @Override
                    public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                        try {
                            switch (response.code()) {
                                case 200:
                                    Logger.e(TAG, "response:" + response.body().toString());
                                    JSONObject result = response.body();
                                    int success = result.getInt("success");
                                    if (success == 1) {
                                        data = new Gson().fromJson(result.getString("list"), StatisticTargetData.class);
                                        // 圖表
                                        percentCurrent = data.getCurrent_percent();
                                        percentLast = data.getPrior_percent();
                                        percentEnd = Math.max(percentCurrent, percentLast);

                                        if (percentEnd < 100)
                                            percentEnd = 100;

                                        updateArcProgress();

                                        // 其他資訊
                                        tvGoal.setText(NumberFormater.getMoneyFormat(data.getTarget()));//目標
                                        tvCurrent.setText(NumberFormater.getMoneyFormat(data.getCurrent()));//當前

                                        ItemStatisticPersonal itemGoal = new ItemStatisticPersonal(StatisticTargetActivity.this);
                                        itemGoal.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                        itemGoal.setGoal(getString(R.string.goal_amount), data.getTarget(), getString(R
                                                        .string.money_unit));//目標金額
                                        itemGoal.setReach(getString(R.string.reach_amount), data.getCurrent(), getString(R
                                                        .string.money_unit));//達成金額
                                        itemGoal.setDiff(data.getCurrent_percent() - data.getPrior_percent());//前期相比
                                        itemGoal.setProgress(data.getTarget(), data.getCurrent());
                                        container.addView(itemGoal);
                                    } else {
                                        String errorMsg = result.getString("errorMsg");
                                        if (TextUtils.isEmpty(errorMsg))
                                            errorMsg = "get statistic data failed.";

                                        Toast.makeText(StatisticTargetActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                                    }
                                    break;
                                case ApiHelper.ERROR_TOKEN_EXPIRED:
                                    showLoginDialog();
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
        * 條件
        */
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
        if (!TextUtils.isEmpty(filterCheckPoint.getSelectedText()))
            condition += "/" + filterCheckPoint.getSelectedText();

        tvCondition.setText(condition);
    }

    void updateArcProgress() {
        arcProgressBar.setProgress(percentEnd, percentCurrent, percentLast);
        tvPercent.setText(String.valueOf(percentCurrent));
        tvPercentStart.setText(String.valueOf(percentStart) + "%");
        tvPercentEnd.setText(String.valueOf(percentEnd) + "%");
        tvTagCurrent.setText(filterTime.getSelectedText() + String.valueOf(percentCurrent) + "%");
        tvTagLast.setText(getString(R.string.last_period) + String.valueOf(percentLast) + "%");
    }

    @OnClick(R.id.btnComplete)
    void filterComplete() {
        containerFilter.setVisibility(View.GONE);
        updateDetail();
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
