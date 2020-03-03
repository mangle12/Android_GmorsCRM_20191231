package tw.com.masterhand.gmorscrm.activity.statistic;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.TokenManager;
import tw.com.masterhand.gmorscrm.api.ApiHelper;
import tw.com.masterhand.gmorscrm.model.Performance;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.setting.User;
import tw.com.masterhand.gmorscrm.tools.Base64Utils;
import tw.com.masterhand.gmorscrm.tools.ImageTools;
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.view.ItemRank;
import tw.com.masterhand.gmorscrm.view.ItemRankDetailSale;

public class StatisticPerformanceActivity extends StatisticVisitActivity {
    List<Performance> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appbar.setTitle(getString(R.string.statistic_menu_performance));
        filterCheckPoint.setVisibility(View.VISIBLE);
    }

    @Override
    protected void getData() {
        updateCondition();
        Call<JSONObject> call;

        String departmentId = null;
        if (filterDepartment.getSelected() != null)
            departmentId = filterDepartment.getSelected().getId();

        String companyId = null;
        if (filterCompany.getSelected() != null)
            companyId = filterCompany.getSelected().getId();

        call = ApiHelper.getInstance().getStatisticApi().getPerformance(filterTime.getSelected(), departmentId, companyId, filterTopCount.getSelected(),
                filterCheckPoint.getSelected(), TokenManager.getInstance().getToken());
        call.enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                try {
                    switch (response.code()) {
                        case 200:
                            JSONObject result = response.body();
                            int success = result.getInt("success");
                            if (success == 1) {
                                list = new Gson().fromJson(result.getString("list"), new TypeToken<List<Performance>>() {}.getType());
                                updateList();
                            } else {
                                String errorMsg = result.getString("errorMsg");
                                if (TextUtils.isEmpty(errorMsg))
                                    errorMsg = "get statistic data failed.";
                                Toast.makeText(StatisticPerformanceActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
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

    @Override
    void updateList() {
        scrollView.setVisibility(View.VISIBLE);
        container.removeAllViews();
        containerEmpty.removeAllViews();
        containerDetail.removeAllViews();

        if (list.size() > 0) {
            float max;
            if (btnDESC.isSelected()) {
                // 降冪
                Collections.sort(list, new Comparator<Performance>() {
                    @Override
                    public int compare(Performance data1, Performance data2) {
                        return data2.getAmount() > data1.getAmount() ? 1 : -1;
                    }
                });
                max = list.get(0).getAmount();
            } else {
                // 升冪
                Collections.sort(list, new Comparator<Performance>() {
                    @Override
                    public int compare(Performance data1, Performance data2) {
                        return data1.getAmount() > data2.getAmount() ? 1 : -1;
                    }
                });
                max = list.get(list.size() - 1).getAmount();
            }

            for (int i = 0; i < list.size(); i++) {
                Performance data = list.get(i);
                final ItemRankDetailSale itemDetail = new ItemRankDetailSale(this);
                final ItemRank item = new ItemRank(this);
                if (btnDESC.isSelected()) {
                    itemDetail.setIndex(i + 1);
                    item.setIndex(i + 1);
                } else {
                    itemDetail.setIndex(list.size() - i);
                    item.setIndex(list.size() - i);
                }

                itemDetail.setSaleInfo(data);
                item.setProgress(max, data.getAmount(), true);
                mDisposable.add(DatabaseHelper.getInstance(this).getUserById(data.getUser_id())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<User>() {

                            @Override
                            public void accept(User user) throws Exception {
                                item.setName(user.getShowName());
                                itemDetail.setName(user.getShowName());
                                if (!TextUtils.isEmpty(user.getPhoto()))
                                    item.getIvHead().setImageDrawable(ImageTools.getCircleDrawable(getResources(), Base64Utils.decodeToBitmapFromString(user.getPhoto())));
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                item.setName(getString(R.string.empty_show));
                                itemDetail.setName(getString(R.string.empty_show));
                            }
                        }));
                container.addView(item);
                containerDetail.addView(itemDetail);
                item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (itemSelected != null)
                            itemSelected.setBackgroundColor(ContextCompat.getColor(StatisticPerformanceActivity.this, R.color.gray_light));
                        itemSelected = itemDetail;
                        itemSelected.setBackgroundColor(ContextCompat.getColor(StatisticPerformanceActivity.this, R.color.colorPrimary));
                        scrollToView(scrollView, itemDetail);
                    }
                });
            }
        } else {
            onNoData();
        }
    }

    @Override
    void updateCondition() {
        String condition = getString(R.string.condition);
        condition += ":";
        condition += filterTime.getSelectedText();

        if (filterDepartment.getSelected() != null)
            condition += "/" + filterDepartment.getSelected().getName();

        if (filterCompany.getSelected() != null)
            condition += "/" + filterCompany.getSelected().getName();

        if (!TextUtils.isEmpty(filterCheckPoint.getSelectedText()))
            condition += "/" + filterCheckPoint.getSelectedText();

        tvCondition.setText(condition);
    }
}
