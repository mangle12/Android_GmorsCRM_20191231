package tw.com.masterhand.gmorscrm.activity.sample;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.OnEditorAction;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tw.com.masterhand.gmorscrm.BaseUserCheckActivity;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.api.ApiHelper;
import tw.com.masterhand.gmorscrm.model.SampleList;
import tw.com.masterhand.gmorscrm.tools.Base64Utils;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.tools.ImageTools;
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.tools.TimeFormater;

public class SampleSearchActivity extends BaseUserCheckActivity implements TextView
        .OnEditorActionListener {
    @BindView(R.id.editText_search)
    protected EditText etSearch;// 搜尋輸入框
    @BindView(R.id.container)
    protected LinearLayout container;// 搜尋結果列表

    List<SampleList> sampleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        etSearch.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onUserChecked() {

    }

    /**
     * 點擊鍵盤搜尋鈕
     */
    @OnEditorAction(R.id.editText_search)
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            performSearch();
            return true;
        }
        return false;
    }


    /**
     * 開始搜尋
     */
    protected void performSearch() {
        container.removeAllViews();
        String keyword = etSearch.getText().toString();
        if (TextUtils.isEmpty(keyword)) {
            container.addView(getEmptyImageView(null));
            return;
        }
        Logger.e(TAG, "search keyword:" + keyword);
        Call<JSONObject> call = ApiHelper.getInstance().getSampleApi().getSampleList(keyword);
        call.enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                try {
                    switch (response.code()) {
                        case 200:
                            JSONObject result = response.body();
                            int success = result.getInt("success");
                            if (success == 1) {
                                sampleList = gson.fromJson(result.getJSONObject("pagination")
                                        .getString("data"), new TypeToken<List<SampleList>>() {
                                }.getType());
                                updateList();
                            } else {
                                onNoData();
                            }
                            break;
                        case 401:
                        case 500:
                            Toast.makeText(SampleSearchActivity.this, response.code() + ":" +
                                            response.message(),
                                    Toast.LENGTH_SHORT).show();
                            onNoData();
                            break;
                    }
                } catch (JSONException e) {
                    ErrorHandler.getInstance().setException(SampleSearchActivity.this, e);
                    onNoData();
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                ErrorHandler.getInstance().setException(SampleSearchActivity.this, t);
                onNoData();
            }
        });


    }

    void onNoData() {
        container.addView(getEmptyImageView(null));
    }

    void updateList() {
        for (SampleList sample : sampleList) {
            container.addView(generateItem(sample));
        }
        if (container.getChildCount() == 0)
            onNoData();
    }

    View generateItem(final SampleList sample) {
        View v = LayoutInflater.from(this).inflate(R.layout.item_sample, container, false);
        ImageView ivSample = v.findViewById(R.id.imageView_sample);
        ImageView ivIcon = v.findViewById(R.id.ivIcon);
        TextView tvDate = v.findViewById(R.id.tvDate);
        TextView tvCustomer = v.findViewById(R.id.tvCustomer);
        TextView tvProject = v.findViewById(R.id.tvProject);
        TextView tvTitle = v.findViewById(R.id.tvTitle);
        ImageLoader.getInstance().displayImage(sample.getDefault_photo().getFile_url(), ivSample);
        tvDate.setText(TimeFormater.getInstance().toDateFormat(sample.getUpdated_at()));
        if (sample.getTrip().getCustomer() != null) {
            tvCustomer.setText(sample.getTrip().getCustomer().getName());
            if (!TextUtils.isEmpty(sample.getTrip().getCustomer().getLogo())) {
                Bitmap bitmap = Base64Utils.decodeToBitmapFromString(sample.getTrip().getCustomer
                        ().getLogo
                        ());
                ivIcon.setImageDrawable(ImageTools
                        .getCircleDrawable(getResources(), bitmap));
            } else {
                ivIcon.setImageResource(R.mipmap.common_small_customer_logo);
            }
        }
        if (sample.getTrip().getProject() != null)
            tvProject.setText(sample.getTrip().getProject().getName());
        tvTitle.setText(sample.getTrip().getName());
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SampleSearchActivity.this, SampleInfoActivity.class);
                intent.putExtra(MyApplication.INTENT_KEY_SAMPLE, sample.getId());
                startActivity(intent);
            }
        });
        return v;
    }
}
