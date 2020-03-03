package tw.com.masterhand.gmorscrm.activity.statistic;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tw.com.masterhand.gmorscrm.BaseWebCheckActivity;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.TokenManager;
import tw.com.masterhand.gmorscrm.activity.project.ProjectActivity;
import tw.com.masterhand.gmorscrm.api.ApiHelper;
import tw.com.masterhand.gmorscrm.model.SignData;
import tw.com.masterhand.gmorscrm.room.setting.Company;
import tw.com.masterhand.gmorscrm.room.setting.Department;
import tw.com.masterhand.gmorscrm.room.setting.User;
import tw.com.masterhand.gmorscrm.tools.Base64Utils;
import tw.com.masterhand.gmorscrm.tools.ImageTools;
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.view.Appbar;
import tw.com.masterhand.gmorscrm.view.FilterCompany;
import tw.com.masterhand.gmorscrm.view.FilterDepartment;
import tw.com.masterhand.gmorscrm.view.FilterIndustry;
import tw.com.masterhand.gmorscrm.view.FilterPersonal;
import tw.com.masterhand.gmorscrm.view.FilterTime;
import tw.com.masterhand.gmorscrm.view.FilterTopCount;
import tw.com.masterhand.gmorscrm.view.ItemRank;
import tw.com.masterhand.gmorscrm.view.ItemRankDetailCustomer;

public class StatisticSignActivity extends BaseWebCheckActivity implements FilterPersonal.OnSelectedListener, FilterDepartment.OnSelectedListener, FilterCompany.OnSelectedListener {

    @BindView(R.id.appbar)
    Appbar appbar;
    @BindView(R.id.button_tab_left)
    Button btnASC;
    @BindView(R.id.button_tab_right)
    Button btnDESC;
    @BindView(R.id.container)
    LinearLayout container;
    @BindView(R.id.container_detail)
    LinearLayout containerDetail;
    @BindView(R.id.scrollView)
    ScrollView scrollView;
    @BindView(R.id.tvCondition)
    TextView tvCondition;
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
    @BindView(R.id.filterTopCount)
    FilterTopCount filterTopCount;
    @BindView(R.id.containerFilter)
    LinearLayout containerFilter;
    @BindView(R.id.containerFilterItem)
    LinearLayout containerFilterItem;
    @BindView(R.id.containerEmpty)
    LinearLayout containerEmpty;

    ItemRankDetailCustomer itemSelected;
    private List<SignData> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic_sign);

        appbar.setTitle(getString(R.string.statistic_menu_sign));
        init();
    }

    @Override
    protected void onUserChecked() {
        appbar.invalidate();
        getData();
    }

    protected void init() {
        list = new ArrayList<>();
        appbar.addFunction(getString(R.string.filter), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (containerFilter.getVisibility() == View.VISIBLE) {
                    containerFilter.setVisibility(View.GONE);
                    getData();
                } else
                    containerFilter.setVisibility(View.VISIBLE);
            }
        });

        filterPersonal.setSelected(TokenManager.getInstance().getUser());
        filterPersonal.setListener(this);
        filterDepartment.setListener(this);
        filterCompany.setListener(this);
        btnDESC.setSelected(true);
    }

    void getData() {
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

        if (filterCompany.isSelectAll())
            companyId = TokenManager.getInstance().getUser().getCompany_id();

        String industry = null;
        if (filterIndustry.getSelected() != null)
            industry = filterIndustry.getSelected().getId();

        ApiHelper.getInstance().getStatisticApi().getSaleSign(filterTime.getSelected(), userId, departmentId, companyId, industry, filterTopCount.getSelected(), TokenManager.getInstance()
                        .getToken()).enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                try {
                    switch (response.code()) {
                        case 200:
                            JSONObject result = response.body();
                            int success = result.getInt("success");
                            if (success == 1) {
                                Logger.e(TAG, "list:" + result.getString("list"));
                                list = gson.fromJson(result.getString("list"), new TypeToken<List<SignData>>() {}.getType());
                                updateList();
                            } else {
                                String errorMsg = result.getString("errorMsg");
                                if (TextUtils.isEmpty(errorMsg))
                                    errorMsg = "get statistic data failed.";
                                Toast.makeText(StatisticSignActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
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

    void updateList() {
        scrollView.setVisibility(View.VISIBLE);
        container.removeAllViews();
        containerEmpty.removeAllViews();
        containerDetail.removeAllViews();

        if (list.size() > 0) {
            float max;
            if (btnDESC.isSelected()) {
                // 降冪
                Collections.sort(list, new Comparator<SignData>() {
                    @Override
                    public int compare(SignData signData1, SignData signData2) {
                        return signData2.getProject().getExpect_amount() > signData1.getProject().getExpect_amount() ? 1 : -1;
                    }
                });
                max = list.get(0).getProject().getExpect_amount();
            } else {
                // 升冪
                Collections.sort(list, new Comparator<SignData>() {
                    @Override
                    public int compare(SignData signData1, SignData signData2) {
                        return signData1.getProject().getExpect_amount() > signData2.getProject().getExpect_amount() ? 1 : -1;
                    }
                });
                max = list.get(list.size() - 1).getProject().getExpect_amount();
            }

            for (int i = 0; i < list.size(); i++) {
                final SignData signData = list.get(i);
                final ItemRankDetailCustomer itemDetail = new ItemRankDetailCustomer(this);
                ItemRank item = new ItemRank(this);
                item.setName(signData.getProject().getName());

                if (!TextUtils.isEmpty(signData.getCustomer().getLogo()))
                    item.getIvHead().setImageDrawable(ImageTools.getCircleDrawable(getResources(), Base64Utils.decodeToBitmapFromString(signData.getCustomer().getLogo())));
                if (btnDESC.isSelected()) {
                    itemDetail.setIndex(i + 1);
                    item.setIndex(i + 1);
                } else {
                    itemDetail.setIndex(list.size() - i);
                    item.setIndex(list.size() - i);
                }

                item.setProgress(max, signData.getProject().getExpect_amount(), true);
                itemDetail.setSignData(signData);
                itemDetail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(StatisticSignActivity.this, ProjectActivity.class);
                        intent.putExtra(MyApplication.INTENT_KEY_PROJECT, signData.getProject().getId());
                        startActivity(intent);
                    }
                });

                container.addView(item);
                containerDetail.addView(itemDetail);
                item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (itemSelected != null)
                            itemSelected.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.gray_light));
                        itemSelected = itemDetail;
                        itemSelected.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.colorPrimary));
                        scrollToView(scrollView, itemDetail);
                    }
                });
            }
        } else {
            Logger.e(TAG, "list size:0");
            onNoData();
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

    void scrollToView(final ScrollView scrollViewParent, final View view) {
        // Get deepChild Offset
        Point childOffset = new Point();
        getDeepChildOffset(scrollViewParent, view.getParent(), view, childOffset);
        // Scroll to child.
        scrollViewParent.smoothScrollTo(0, childOffset.y);
    }

    void getDeepChildOffset(final ViewGroup mainParent, final ViewParent parent, final View child, final Point accumulatedOffset) {
        ViewGroup parentGroup = (ViewGroup) parent;
        accumulatedOffset.x += child.getLeft();
        accumulatedOffset.y += child.getTop();
        if (parentGroup.equals(mainParent)) {
            return;
        }
        getDeepChildOffset(mainParent, parentGroup.getParent(), parentGroup, accumulatedOffset);
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

    @OnClick(R.id.button_tab_left)
    void sortASC() {
        btnDESC.setSelected(false);
        btnASC.setSelected(true);
        updateList();
    }

    @OnClick(R.id.button_tab_right)
    void sortDESC() {
        btnDESC.setSelected(true);
        btnASC.setSelected(false);
        updateList();
    }

    @OnClick(R.id.btnComplete)
    void filterComplete() {
        containerFilter.setVisibility(View.GONE);
        getData();
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
