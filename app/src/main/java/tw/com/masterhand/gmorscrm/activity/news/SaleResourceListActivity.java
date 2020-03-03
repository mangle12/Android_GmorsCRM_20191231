package tw.com.masterhand.gmorscrm.activity.news;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tw.com.masterhand.gmorscrm.BaseWebCheckActivity;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.TokenManager;
import tw.com.masterhand.gmorscrm.api.ApiHelper;
import tw.com.masterhand.gmorscrm.model.SaleResourceList;
import tw.com.masterhand.gmorscrm.room.setting.Department;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.tools.UnitChanger;
import tw.com.masterhand.gmorscrm.view.Appbar;

public class SaleResourceListActivity extends BaseWebCheckActivity {
    @BindView(R.id.appbar)
    Appbar appbar;
    @BindView(R.id.container)
    LinearLayout container;

    Department saleResourceDept;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify);
        init();
    }

    @Override
    protected void onUserChecked() {
        updateList();
    }

    private void init() {
        String data = getIntent().getStringExtra(MyApplication.INTENT_KEY_DEPARTMENT);
        if (TextUtils.isEmpty(data)) {
            finish();
            return;
        }
        saleResourceDept = gson.fromJson(data, Department.class);
        appbar.setTitle(saleResourceDept.getName());
    }

    private void updateList() {
        container.removeAllViews();
        startProgressDialog();
        ApiHelper.getInstance().getNewsApi().getResourceCategories(TokenManager.getInstance()
                .getToken()).enqueue(new Callback<JSONObject>() {

            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                stopProgressDialog();
                try {
//                    Logger.e(TAG, "response code:" + response.code());
//                    Logger.e(TAG, "response:" + response.body().toString());
                    switch (response.code()) {
                        case 200:
                            JSONObject result = response.body();
                            int success = result.getInt("success");
                            if (success == 1) {
                                JSONArray list = result.getJSONArray("list");
                                for (int i = 0; i < list.length(); i++) {
                                    SaleResourceList data = gson.fromJson(list.getString
                                            (i), SaleResourceList.class);
                                    container.addView(generateItem(data));
                                }
                            }
                            break;
                        default:
                            Logger.e(TAG, "response code:" + response.code());
                    }
                } catch (Exception e) {
                    ErrorHandler.getInstance().setException(SaleResourceListActivity.this, e);
                    container.addView(getEmptyImageView(null));
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                stopProgressDialog();
                ErrorHandler.getInstance().setException(SaleResourceListActivity.this, t);
                container.addView(getEmptyImageView(null));
            }
        });
    }

    private View generateItem(final SaleResourceList data) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout
                .LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.btn_size));
        TextView item = new TextView(this);
        item.setLayoutParams(params);
        item.setText(data.getName());
        item.setGravity(Gravity.CENTER_VERTICAL);
        item.setPaddingRelative(UnitChanger.dpToPx(8), 0, 0, 0);
        item.setBackgroundColor(ContextCompat.getColor(this, R.color.gray_light));
        item.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.mipmap.common_right_black, 0);
        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SaleResourceListActivity.this, SaleResourceActivity
                        .class);
                intent.putExtra(MyApplication.INTENT_KEY_DEPARTMENT, gson.toJson(saleResourceDept));
                intent.putExtra(MyApplication.INTENT_KEY_LIST, gson.toJson(data));
                startActivity(intent);
            }
        });
        return item;
    }
}
