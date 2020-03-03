package tw.com.masterhand.gmorscrm.activity.news;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
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
import tw.com.masterhand.gmorscrm.room.setting.Department;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.tools.UnitChanger;
import tw.com.masterhand.gmorscrm.view.Appbar;

public class SaleResourceDeptActivity extends BaseWebCheckActivity {
    @BindView(R.id.appbar)
    Appbar appbar;
    @BindView(R.id.container)
    LinearLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify);
        init();
    }

    @Override
    protected void onUserChecked() {
        appbar.invalidate();
        updateList();
    }

    private void init() {
        appbar.setTitle(getString(R.string.department));
    }

    private void updateList() {
        container.removeAllViews();
        startProgressDialog();
        ApiHelper.getInstance().getNewsApi().getResourceDepartment(TokenManager.getInstance()
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
                                    Department data = gson.fromJson(list.getString
                                            (i), Department.class);
                                    container.addView(generateItem(data));
                                }
                            }
                            break;
                        default:
                            Logger.e(TAG, "response code:" + response.code());
                    }
                } catch (Exception e) {
                    ErrorHandler.getInstance().setException(SaleResourceDeptActivity.this, e);
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                ErrorHandler.getInstance().setException(SaleResourceDeptActivity.this, t);
                stopProgressDialog();
            }
        });
    }

    private View generateItem(final Department data) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout
                .LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.btn_size));
        TextView item = new TextView(this);
        item.setLayoutParams(params);
        item.setGravity(Gravity.CENTER_VERTICAL);
        item.setPaddingRelative(UnitChanger.dpToPx(8), 0, 0, 0);
        item.setText(data.getName());
        item.setBackgroundColor(ContextCompat.getColor(this, R.color.gray_light));
        item.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.mipmap.common_right_black, 0);
//        View item = getLayoutInflater().inflate(R.layout.item_list, container, false);
//        ImageView ivIcon = ButterKnife.findById(item, R.id.imageView_icon);
//        TextView tvTitle = ButterKnife.findById(item, R.id.textView_title);
//        final TextView tvSubTitle = ButterKnife.findById(item, R.id.textView_subtitle);
//        TextView tvCount = ButterKnife.findById(item, R.id.textView_count);
//        ivIcon.setVisibility(View.GONE);
//        tvTitle.setText(data.getName());
//        tvCount.setText(String.valueOf(data.getResourceIds().size()));
//        DatabaseHelper.getInstance(this).getUnreadCount(data.getResourceIds()).observeOn
//                (AndroidSchedulers.mainThread()).subscribe(new Consumer<Integer>() {
//
//            @Override
//            public void accept(Integer count) throws Exception {
//                if (count > 0)
//                    tvSubTitle.setText(String.valueOf(count) + getString(R.string.unread_msg));
//            }
//        }, new Consumer<Throwable>() {
//            @Override
//            public void accept(Throwable throwable) throws Exception {
//                ErrorHandler.getInstance().setException(SaleResourceDeptActivity.this, throwable);
//            }
//        });
        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SaleResourceDeptActivity.this, SaleResourceListActivity
                        .class);
                intent.putExtra(MyApplication.INTENT_KEY_DEPARTMENT, gson.toJson(data));
                startActivity(intent);
            }
        });
        return item;
    }
}
