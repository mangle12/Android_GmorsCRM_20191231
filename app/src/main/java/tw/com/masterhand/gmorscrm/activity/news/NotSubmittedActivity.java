package tw.com.masterhand.gmorscrm.activity.news;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tw.com.masterhand.gmorscrm.BaseUserCheckActivity;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.TokenManager;
import tw.com.masterhand.gmorscrm.adapter.NotSubmittedAdapter;
import tw.com.masterhand.gmorscrm.api.ApiHelper;
import tw.com.masterhand.gmorscrm.enums.SubmitStatus;
import tw.com.masterhand.gmorscrm.model.MainTrip;
import tw.com.masterhand.gmorscrm.model.MultipleSubmit;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.view.VerticalSpaceItemDecoration;

public class NotSubmittedActivity extends BaseUserCheckActivity {

    @BindView(R.id.btnBack)
    ImageButton btnBack;
    @BindView(R.id.btnSearch)
    ImageButton btnSearch;
    @BindView(R.id.btnSelectAll)
    Button btnSelectAll;
    @BindView(R.id.btnSend)
    RelativeLayout btnSend;
    @BindView(R.id.etSearch)
    EditText etSearch;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.container)
    RecyclerView recyclerView;

    NotSubmittedAdapter adapter;
    List<MainTrip> tripList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_not_submitted);
        init();
    }

    @Override
    protected void onUserChecked() {
        getData();

        if (tripList != null)
            updateList();
    }

    private void init() {
        tvTitle.setText(R.string.all_not_submitted);//未提交項目

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(8));//分隔線
        adapter = new NotSubmittedAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    void getData() {
        mDisposable.add(DatabaseHelper.getInstance(this).getNotSunmittedTrip(TokenManager.getInstance().getUser().getId()).toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<MainTrip>>() {

            @Override
            public void accept(List<MainTrip> result) throws Exception {
                tripList = result;
                adapter.setTrip(tripList);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                ErrorHandler.getInstance().setException(NotSubmittedActivity.this, throwable);
            }
        }));
    }

    private void updateList() {
        adapter.setTrip(tripList);
    }

    private void updateSearchList() {
        String keyword = etSearch.getText().toString();
        if (TextUtils.isEmpty(keyword)) {
            adapter.setTrip(new ArrayList<MainTrip>());
            return;
        }

        List<MainTrip> searchResult = new ArrayList<>();
        for (MainTrip trip : tripList) {
            if (trip.getTrip().getName() != null && trip.getTrip().getName().contains(keyword)) {
                searchResult.add(trip);
            } else if (trip.getTrip().getDescription() != null && trip.getTrip().getDescription().contains(keyword)) {
                searchResult.add(trip);
            }
        }
        adapter.setTrip(searchResult);
    }

    /**
     * 鍵盤點擊搜尋
     */
    @OnEditorAction(R.id.etSearch)
    boolean imeSearch(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            updateSearchList();
            return true;
        }
        return false;
    }

    /**
     * 全選
     */
    @OnClick(R.id.btnSelectAll)
    void selectAll() {
        List<MainTrip> trips = adapter.getTrip();
        boolean isAllSelect = true;
        for (MainTrip trip : trips) {
            if (trip.getTrip().getSubmit() == SubmitStatus.NONE) {
                isAllSelect = false;
                break;
            }
        }
        if (isAllSelect) {
            for (MainTrip trip : trips) {
                trip.getTrip().setSubmit(SubmitStatus.NONE);
            }
        } else {
            for (MainTrip trip : trips) {
                trip.getTrip().setSubmit(SubmitStatus.SUBMITTED);
            }
        }
        adapter.setTrip(trips);
    }

    /**
     * 點擊搜尋鈕
     */
    @OnClick(R.id.btnSearch)
    void search() {
        if (etSearch.getVisibility() == View.GONE) {
            etSearch.setVisibility(View.VISIBLE);
            tvTitle.setVisibility(View.GONE);
            btnSelectAll.setVisibility(View.GONE);
            btnBack.setImageResource(R.mipmap.common_close);
        } else {
            updateSearchList();
        }
    }

    /**
     * 點擊返回/關閉鈕
     */
    @OnClick(R.id.btnBack)
    @Override
    public void onBackPressed() {
        if (etSearch.getVisibility() == View.GONE) {
            super.onBackPressed();
        } else {
            etSearch.setVisibility(View.GONE);
            tvTitle.setVisibility(View.VISIBLE);
            btnSelectAll.setVisibility(View.VISIBLE);
            btnBack.setImageResource(R.mipmap.common_left_black);
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null)
                imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
            updateList();
        }
    }

    /**
     * 點擊提交鈕
     */
    @OnClick(R.id.btnSend)
    void send() {
        List<MainTrip> trips = adapter.getTrip();
        List<String> selected = new ArrayList<>();

        //已選取
        for (MainTrip trip : trips) {
            if (trip.getTrip().getSubmit() == SubmitStatus.SUBMITTED) {
                selected.add(trip.getTrip().getId());
            }
        }

        if (selected.size() > 0) {
            String params = StringUtils.join(selected, ",");

            // 提交
            final MultipleSubmit submit = new MultipleSubmit();
            submit.setTrip_ids(params);

            ApiHelper.getInstance().getSubmitApi().submit(TokenManager.getInstance().getToken(), submit).enqueue(new Callback<JSONObject>() {
                @Override
                public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                    try {
                        switch (response.code()) {
                            case 200:
                                JSONObject result = response.body();
                                int success = result.getInt("success");
                                if (success == 1) {
                                    DatabaseHelper.getInstance(NotSubmittedActivity.this).saveTripSubmit(submit)
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(new CompletableObserver() {

                                                @Override
                                                public void onSubscribe(Disposable d) {
                                                    mDisposable.add(d);
                                                }

                                                @Override
                                                public void onComplete() {
                                                    finish();
                                                }

                                                @Override
                                                public void onError(Throwable e) {
                                                    Toast.makeText(NotSubmittedActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                                }
                                            });
                                } else {
                                    Toast.makeText(NotSubmittedActivity.this, result.getString("errorMsg"), Toast.LENGTH_LONG).show();
                                }
                                break;
                            case ApiHelper.ERROR_TOKEN_EXPIRED:
                                showLoginDialog();
                                break;
                            default:
                                Logger.e(TAG, "submit failed");
                        }
                    } catch (Exception e) {
                        ErrorHandler.getInstance().setException(NotSubmittedActivity.this, e);
                    }
                }

                @Override
                public void onFailure(Call<JSONObject> call, Throwable t) {
                    Toast.makeText(NotSubmittedActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(this, R.string.error_msg_no_submit_selected, Toast.LENGTH_LONG).show();
        }
    }
}
