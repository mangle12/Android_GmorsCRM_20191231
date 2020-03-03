package tw.com.masterhand.gmorscrm.activity.statistic;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.TokenManager;
import tw.com.masterhand.gmorscrm.activity.customer.CustomerInfoActivity;
import tw.com.masterhand.gmorscrm.activity.project.ProjectActivity;
import tw.com.masterhand.gmorscrm.api.ApiHelper;
import tw.com.masterhand.gmorscrm.model.WinData;
import tw.com.masterhand.gmorscrm.tools.Base64Utils;
import tw.com.masterhand.gmorscrm.tools.ImageTools;
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.view.ItemRank;
import tw.com.masterhand.gmorscrm.view.ItemRankDetailCustomer;

public class StatisticWinActivity extends StatisticSignActivity{

    private List<WinData> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appbar.setTitle(getString(R.string.statistic_menu_win));
    }

    @Override
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
        String industry = null;
        if (filterIndustry.getSelected() != null)
            industry = filterIndustry.getSelected().getId();
        ApiHelper.getInstance().getStatisticApi().getSaleWin(filterTime.getSelected(), userId,
                departmentId, companyId, industry, filterTopCount.getSelected(), TokenManager
                        .getInstance().getToken()).enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                Logger.e(TAG, "get response:" + response.toString());
                try {
                    switch (response.code()) {
                        case 200:
                            JSONObject result = response.body();
                            int success = result.getInt("success");
                            if (success == 1) {
                                Logger.e(TAG, "list:" + result.getString("list"));
                                list = gson.fromJson(result.getString
                                        ("list"), new TypeToken<List<WinData>>() {
                                }.getType());
                                updateList();
                            } else {
                                String errorMsg = result.getString("errorMsg");
                                if (TextUtils.isEmpty(errorMsg))
                                    errorMsg = "get statistic data failed.";
                                Toast.makeText(StatisticWinActivity.this, errorMsg,
                                        Toast.LENGTH_SHORT).show();
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
                } catch (JSONException e) {
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
        Logger.e(TAG, "updateList");
        scrollView.setVisibility(View.VISIBLE);
        container.removeAllViews();
        containerEmpty.removeAllViews();
        containerDetail.removeAllViews();
        if (list.size() > 0) {
            float max;
            if (btnDESC.isSelected()) {
                // 降冪
                Collections.sort(list, new Comparator<WinData>() {
                    @Override
                    public int compare(WinData signData1, WinData signData2) {
                        return signData2.getProject().getExpect_amount() > signData1.getProject()
                                .getExpect_amount() ? 1 : -1;
                    }
                });
                max = list.get(0).getProject().getExpect_amount();
            } else {
                // 升冪
                Collections.sort(list, new Comparator<WinData>() {
                    @Override
                    public int compare(WinData signData1, WinData signData2) {
                        return signData1.getProject().getExpect_amount() > signData2.getProject()
                                .getExpect_amount() ? 1 : -1;
                    }
                });
                max = list.get(list.size() - 1).getProject().getExpect_amount();
            }
            for (int i = 0; i < list.size(); i++) {
                final WinData data = list.get(i);
                final ItemRankDetailCustomer itemDetail = new ItemRankDetailCustomer(this);
                ItemRank item = new ItemRank(this);
                item.setName(data.getProject().getName());
                if (!TextUtils.isEmpty(data.getCustomer().getLogo()))
                    item.getIvHead().setImageDrawable(ImageTools.getCircleDrawable(getResources(),
                            Base64Utils.decodeToBitmapFromString(data
                                    .getCustomer().getLogo())));
                if (btnDESC.isSelected()) {
                    itemDetail.setIndex(i + 1);
                    item.setIndex(i + 1);
                } else {
                    itemDetail.setIndex(list.size() - i);
                    item.setIndex(list.size() - i);
                }
                item.setProgress(max, data.getProject().getExpect_amount(), true);
                itemDetail.setWinData(data);
                itemDetail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(StatisticWinActivity.this, ProjectActivity
                                .class);
                        intent.putExtra(MyApplication.INTENT_KEY_PROJECT, data.getProject().getId
                                ());
                        startActivity(intent);
                    }
                });
                container.addView(item);
                containerDetail.addView(itemDetail);
                item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (itemSelected != null)
                            itemSelected.setBackgroundColor(ContextCompat.getColor
                                    (view.getContext(), R.color.gray_light));
                        itemSelected = itemDetail;
                        itemSelected.setBackgroundColor(ContextCompat.getColor
                                (view.getContext(), R.color.colorPrimary));
                        scrollToView(scrollView, itemDetail);
                    }
                });
            }
        } else {
            Logger.e(TAG, "list size:0");
            onNoData();
        }
    }


}
