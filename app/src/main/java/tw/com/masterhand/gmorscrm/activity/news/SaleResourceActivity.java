package tw.com.masterhand.gmorscrm.activity.news;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.CompletableObserver;
import io.reactivex.disposables.Disposable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tw.com.masterhand.gmorscrm.BaseWebCheckActivity;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.TokenManager;
import tw.com.masterhand.gmorscrm.adapter.SaleResourceAdapter;
import tw.com.masterhand.gmorscrm.api.ApiHelper;
import tw.com.masterhand.gmorscrm.model.SaleResource;
import tw.com.masterhand.gmorscrm.model.SaleResourceList;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.local.ReadLog;
import tw.com.masterhand.gmorscrm.room.setting.Department;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.view.Appbar;
import tw.com.masterhand.gmorscrm.view.VerticalSpaceItemDecoration;

public class SaleResourceActivity extends BaseWebCheckActivity {
    @BindView(R.id.appbar)
    Appbar appbar;
    @BindView(R.id.container)
    RecyclerView recyclerView;

    SaleResourceAdapter adapter;
    CompleteReceiver completeReceiver;

    SaleResourceList saleResourceList;
    Department saleResourceDept;
    List<SaleResource> resourceList;
    String id;
    String category;
    String dept;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_resource);
        init();
    }

    @Override
    protected void onUserChecked() {
        appbar.invalidate();
        if (TextUtils.isEmpty(id)) {
            saleResourceDept = gson.fromJson(dept, Department.class);
            saleResourceList = gson.fromJson(category, SaleResourceList.class);
            appbar.setTitle(saleResourceList.getName());
            Logger.e(TAG, "department id:" + saleResourceDept.getId());
            Logger.e(TAG, "category id:" + saleResourceList.getId());
            ApiHelper.getInstance().getNewsApi().getResourceById(saleResourceDept.getId(),
                    saleResourceList.getId(), TokenManager.getInstance()
                            .getToken()).enqueue(new Callback<JSONObject>() {

                @Override
                public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                    try {
                        switch (response.code()) {
                            case 200:
                                JSONObject result = response.body();
                                int success = result.getInt("success");
                                if (success == 1) {
                                    resourceList = new ArrayList<>();
                                    JSONArray list = result.getJSONArray("list");
                                    for (int i = 0; i < list.length(); i++) {
                                        SaleResource data = gson.fromJson(list.getString
                                                (i), SaleResource.class);
                                        resourceList.add(data);
                                    }
                                    List<ReadLog> readLogs = new ArrayList<>();
                                    for (SaleResource resource : resourceList) {
                                        ReadLog log = new ReadLog();
                                        log.setParent_id(resource.getId());
                                        readLogs.add(log);
                                    }
                                    DatabaseHelper.getInstance(SaleResourceActivity.this)
                                            .saveReadLog(readLogs).subscribe(new CompletableObserver() {
                                        @Override
                                        public void onSubscribe(Disposable d) {
                                            mDisposable.add(d);
                                        }

                                        @Override
                                        public void onComplete() {
                                            Logger.e(TAG, "save readLog");
                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            Logger.e(TAG, "save readLog error:" + e.getMessage());
                                        }
                                    });
                                    updateList();
                                }
                                break;
                            default:
                                Logger.e(TAG, "response code:" + response.code());
                        }
                    } catch (Exception e) {
                        ErrorHandler.getInstance().setException(SaleResourceActivity.this, e);
                    }
                }

                @Override
                public void onFailure(Call<JSONObject> call, Throwable t) {
                    ErrorHandler.getInstance().setException(SaleResourceActivity.this, t);
                }
            });
        } else {
            startProgressDialog();
            appbar.setTitle(getString(R.string.online_resource));
            Logger.e(TAG, "id:" + id);
            ApiHelper.getInstance().getOnlineResourceApi().getResource(TokenManager.getInstance()
                    .getToken(), id).enqueue(new Callback<JSONObject>() {

                @Override
                public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                    stopProgressDialog();
                    Logger.e(TAG, "response code:" + response.code());
                    Logger.e(TAG, "response body:" + response.body().toString());
                    try {
                        switch (response.code()) {
                            case 200:
                                JSONObject result = response.body();
                                int success = result.getInt("success");
                                if (success == 1) {
                                    JSONArray list = result.getJSONArray("list");
                                    Logger.e(TAG, "list:" + list.toString());
                                    saleResourceList = new SaleResourceList();
                                    saleResourceList.setId("online_resource");
                                    saleResourceList.setName(getString(R.string.online_resource));
                                    for (int i = 0; i < list.length(); i++) {
                                        SaleResource data = gson.fromJson(list.getString
                                                (i), SaleResource.class);
                                        resourceList.add(data);
                                    }
                                    updateList();
                                } else {
                                    Logger.e(TAG, "success:" + success);
                                }
                                break;
                            default:
                                Logger.e(TAG, "response code:" + response.code());
                        }
                    } catch (Exception e) {
                        ErrorHandler.getInstance().setException(SaleResourceActivity.this, e);
                    }
                }

                @Override
                public void onFailure(Call<JSONObject> call, Throwable t) {
                    stopProgressDialog();
                    ErrorHandler.getInstance().setException(SaleResourceActivity.this, t);
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        completeReceiver = new CompleteReceiver();
        registerReceiver(completeReceiver, new IntentFilter(DownloadManager
                .ACTION_DOWNLOAD_COMPLETE));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(completeReceiver);
    }

    private void init() {
        category = getIntent().getStringExtra(MyApplication.INTENT_KEY_LIST);
        dept = getIntent().getStringExtra(MyApplication.INTENT_KEY_DEPARTMENT);
        id = getIntent().getStringExtra(MyApplication.INTENT_KEY_ID);
        if (TextUtils.isEmpty(category) && TextUtils.isEmpty(id)) {
            finish();
            return;
        }
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(3));
        adapter = new SaleResourceAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        startProgressDialog();

    }

    private void updateList() {
        Logger.e(TAG, "updateList:" + gson.toJson(resourceList));
        adapter.clear();
        if (resourceList.size() > 0) {
            for (SaleResource resource : resourceList) {
                adapter.addData(resource);
            }
        }
        stopProgressDialog();
    }

    class CompleteReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //获取文件下载id,就是requestId
            long completeDownLoadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            Logger.e(TAG, "completeDownLoadId:" + completeDownLoadId);
            adapter.complete(completeDownLoadId);
        }
    }
}
