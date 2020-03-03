package tw.com.masterhand.gmorscrm.activity.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tw.com.masterhand.gmorscrm.BaseWebCheckActivity;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.adapter.SampleListAdapter;
import tw.com.masterhand.gmorscrm.api.ApiHelper;
import tw.com.masterhand.gmorscrm.model.SampleList;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.view.Appbar;
import tw.com.masterhand.gmorscrm.view.VerticalSpaceItemDecoration;

public class SampleListActivity extends BaseWebCheckActivity implements
        SampleListAdapter.OnItemClickListener {
    @BindView(R.id.appbar)
    Appbar appbar;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.tvEmpty)
    TextView tvEmpty;

    List<SampleList> sampleList;
    SampleListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_list);
        init();
    }

    @Override
    protected void onUserChecked() {
        appbar.invalidate();
    }

    private void init() {
        appbar.setTitle(getString(R.string.main_menu_sample));
        appbar.addFunction(R.mipmap.common_search, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 搜尋
                Intent intent = new Intent(v.getContext(), SampleSearchActivity.class);
                intent.putExtra(MyApplication.INTENT_KEY_SAMPLE, gson.toJson(sampleList));
                startActivity(intent);
            }
        });

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(3));
        adapter = new SampleListAdapter(this);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        Call<JSONObject> call = ApiHelper.getInstance().getSampleApi().getSampleList("");
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
                            Toast.makeText(SampleListActivity.this, response.code() + ":" +
                                            response.message(),
                                    Toast.LENGTH_SHORT).show();
                            onNoData();
                            break;
                    }
                } catch (JSONException e) {
                    ErrorHandler.getInstance().setException(SampleListActivity.this, e);
                    onNoData();
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                ErrorHandler.getInstance().setException(SampleListActivity.this, t);
                onNoData();
            }
        });
    }

    void onNoData() {
        sampleList = new ArrayList<>();
        updateList();
    }

    void updateList() {
        if (sampleList.size() == 0) {
            tvEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
            return;
        }
        tvEmpty.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        adapter.clear();
        for (SampleList sample : sampleList) {
            adapter.addData(sample);
        }
    }

    @Override
    public void onItemClick(SampleList sample) {
        Intent intent = new Intent(this, SampleInfoActivity.class);
        intent.putExtra(MyApplication.INTENT_KEY_SAMPLE, sample.getId());
        startActivity(intent);
    }
}
