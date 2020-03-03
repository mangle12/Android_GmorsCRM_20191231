package tw.com.masterhand.gmorscrm.activity.statistic;

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
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.TokenManager;
import tw.com.masterhand.gmorscrm.api.ApiHelper;
import tw.com.masterhand.gmorscrm.model.VisitData;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.setting.Company;
import tw.com.masterhand.gmorscrm.room.setting.Department;
import tw.com.masterhand.gmorscrm.room.setting.User;
import tw.com.masterhand.gmorscrm.tools.Base64Utils;
import tw.com.masterhand.gmorscrm.tools.ImageTools;
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.view.Appbar;
import tw.com.masterhand.gmorscrm.view.FilterCheckPoint;
import tw.com.masterhand.gmorscrm.view.FilterCompany;
import tw.com.masterhand.gmorscrm.view.FilterDepartment;
import tw.com.masterhand.gmorscrm.view.FilterTime;
import tw.com.masterhand.gmorscrm.view.FilterTopCount;
import tw.com.masterhand.gmorscrm.view.ItemRank;
import tw.com.masterhand.gmorscrm.view.ItemRankDetailSale;

public class StatisticVisitActivity extends BaseWebCheckActivity implements FilterCompany.OnSelectedListener, FilterDepartment.OnSelectedListener {

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
    @BindView(R.id.filterDepartment)
    FilterDepartment filterDepartment;
    @BindView(R.id.filterCompany)
    FilterCompany filterCompany;
    @BindView(R.id.filterTopCount)
    FilterTopCount filterTopCount;
    @BindView(R.id.filterCheckPoint)
    FilterCheckPoint filterCheckPoint;
    @BindView(R.id.containerFilter)
    LinearLayout containerFilter;
    @BindView(R.id.containerFilterItem)
    LinearLayout containerFilterItem;
    @BindView(R.id.containerEmpty)
    LinearLayout containerEmpty;

    ItemRankDetailSale itemSelected;
    List<VisitData> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic_visit);

        appbar.setTitle(getString(R.string.statistic_menu_visit));
        filterCheckPoint.setVisibility(View.GONE);
        filterDepartment.setListener(this);
        filterCompany.setListener(this);
        init();
    }

    @Override
    protected void onUserChecked() {
        appbar.invalidate();
        getData();
    }

    protected void init() {
        Logger.e(TAG, "init");

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

        btnDESC.setSelected(true);
    }

    protected void getData() {
        updateCondition();
        Call<JSONObject> call;
        Logger.e(TAG, "period:" + filterTime.getSelected());
        Logger.e(TAG, "count:" + filterTopCount.getSelected());

        String departmentId = null;
        if (filterDepartment.getSelected() != null)
            departmentId = filterDepartment.getSelected().getId();

        String companyId = null;
        if (filterCompany.getSelected() != null)
            companyId = filterCompany.getSelected().getId();

        call = ApiHelper.getInstance().getStatisticApi().getVisitRank(filterTime.getSelected(), departmentId, companyId,
                filterTopCount.getSelected(), TokenManager.getInstance().getToken());
        call.enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                try {
                    switch (response.code()) {
                        case 200:
                            JSONObject result = response.body();
                            int success = result.getInt("success");
                            if (success == 1) {
                                list = new Gson().fromJson(result.getString("list"), new TypeToken<List<VisitData>>() {}.getType());
                                updateList();
                            } else {
                                String errorMsg = result.getString("errorMsg");
                                if (TextUtils.isEmpty(errorMsg))
                                    errorMsg = "get statistic data failed.";
                                Toast.makeText(StatisticVisitActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                                onNoData();
                            }
                            break;
                        case ApiHelper.ERROR_TOKEN_EXPIRED:
                            showLoginDialog();
                            break;
                        default:
                            Logger.e(TAG, "response code:" + response.code());
                            Logger.e(TAG, "response message:" + response.message());
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
            int max;
            if (btnDESC.isSelected()) {
                // 降冪
                Collections.sort(list, new Comparator<VisitData>() {
                    @Override
                    public int compare(VisitData data1, VisitData data2) {
                        return data2.getCount() > data1.getCount() ? 1 : -1;
                    }
                });
                max = list.get(0).getCount();
            } else {
                // 升冪
                Collections.sort(list, new Comparator<VisitData>() {
                    @Override
                    public int compare(VisitData data1, VisitData data2) {
                        return data1.getCount() > data2.getCount() ? 1 : -1;
                    }
                });
                max = list.get(list.size() - 1).getCount();
            }

            for (int i = 0; i < list.size(); i++) {
                VisitData data = list.get(i);
                final ItemRankDetailSale itemDetail = new ItemRankDetailSale(this);
                final ItemRank item = new ItemRank(this);

                if (btnDESC.isSelected()) {
                    itemDetail.setIndex(i + 1);
                    item.setIndex(i + 1);
                } else {
                    itemDetail.setIndex(list.size() - i);
                    item.setIndex(list.size() - i);
                }

                itemDetail.setVisitInfo(data);
                item.setProgress(max, data.getCount(), false);
                mDisposable.add(DatabaseHelper.getInstance(this).getUserById(data.getUser_id())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<User>() {

                            @Override
                            public void accept(User user) throws Exception {
                                itemDetail.setName(user.getShowName());
                                item.setName(user.getShowName());
                                if (!TextUtils.isEmpty(user.getPhoto()))
                                    item.getIvHead().setImageDrawable(ImageTools.getCircleDrawable(getResources(), Base64Utils.decodeToBitmapFromString(user.getPhoto())));
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                item.setName(getString(R.string.empty_show));
                                item.setName(getString(R.string.empty_show));
                            }
                        }));

                container.addView(item);
                containerDetail.addView(itemDetail);
                item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (itemSelected != null)
                            itemSelected.setBackgroundColor(ContextCompat.getColor(StatisticVisitActivity.this, R.color.gray_light));
                        itemSelected = itemDetail;
                        itemSelected.setBackgroundColor(ContextCompat.getColor(StatisticVisitActivity.this, R.color.colorPrimary));
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

    protected void scrollToView(final ScrollView scrollViewParent, final View view) {
        // Get deepChild Offset
        Point childOffset = new Point();
        getDeepChildOffset(scrollViewParent, view.getParent(), view, childOffset);
        // Scroll to child.
        scrollViewParent.smoothScrollTo(0, childOffset.y);
    }

    protected void getDeepChildOffset(final ViewGroup mainParent, final ViewParent parent, final View child, final Point accumulatedOffset) {
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

        if (filterDepartment.getSelected() != null)
            condition += "/" + filterDepartment.getSelected().getName();
        if (filterCompany.getSelected() != null)
            condition += "/" + filterCompany.getSelected().getName();

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
    public void onSelectedAllDepartment() {
        filterCompany.clear();
    }

    @Override
    public void onSelectedAllCompany() {
        filterDepartment.clear();
    }

    @Override
    public void onSelected(Company company) {
        if (company != null) {
            filterDepartment.clear();
        }
    }

    @Override
    public void onSelected(Department department) {
        if (department != null) {
            filterCompany.clear();
        }
    }
}
