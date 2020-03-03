package tw.com.masterhand.gmorscrm.activity.task;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tw.com.masterhand.gmorscrm.ApprovalListActivity;
import tw.com.masterhand.gmorscrm.BaseUserCheckActivity;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.ParticipantListActivity;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.TokenManager;
import tw.com.masterhand.gmorscrm.activity.news.SaleResourceActivity;
import tw.com.masterhand.gmorscrm.api.ApiHelper;
import tw.com.masterhand.gmorscrm.enums.ApprovalStatus;
import tw.com.masterhand.gmorscrm.enums.SubmitStatus;
import tw.com.masterhand.gmorscrm.model.MultipleSubmit;
import tw.com.masterhand.gmorscrm.model.ReimbursementItemWithConfig;
import tw.com.masterhand.gmorscrm.model.ReimbursementWithConfig;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.tools.NumberFormater;
import tw.com.masterhand.gmorscrm.tools.TimeFormater;
import tw.com.masterhand.gmorscrm.view.Appbar;
import tw.com.masterhand.gmorscrm.view.ItemApproval;

public class ReimbursementDetailActivity extends BaseUserCheckActivity {
    @BindView(R.id.appbar)
    Appbar appbar;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.tvApproval)
    TextView tvApproval;
    @BindView(R.id.tvCount)
    TextView tvCount;
    @BindView(R.id.tvTotal)
    TextView tvTotal;
    @BindView(R.id.tvApprovalDescription)
    TextView tvApprovalDescription;
    @BindView(R.id.container)
    LinearLayout container;

    @BindView(R.id.itemApproval)
    protected ItemApproval itemApproval;

    protected ReimbursementWithConfig reimbursement;
    protected String viewerId;

    String tripId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reimbursement_detail);
        init();
    }

    @Override
    protected void onUserChecked() {
        appbar.invalidate();
        viewerId = TokenManager.getInstance().getUser().getId();
        getData();
    }

    private void init() {
        appbar.setTitle(getString(R.string.apply_reimbursement) + getString(R.string.title_activity_work_detail));
        tripId = getIntent().getStringExtra(MyApplication.INTENT_KEY_TRIP);
        if (TextUtils.isEmpty(tripId))
            finish();
    }

    protected void getData() {
        mDisposable.add(DatabaseHelper.getInstance(this).getReimbursementByTrip(tripId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ReimbursementWithConfig>() {
            @Override
            public void accept(ReimbursementWithConfig result) throws Exception {
                reimbursement = result;

                /*2018-1-17新增:只有建立者可以修改*/
                /*2018-2-8新增:未提交項目才可以修改*/
                appbar.removeFuction();
                if (TokenManager.getInstance().getUser().getId().equals(result.getTrip().getUser_id()) && result.getTrip().getSubmit() == SubmitStatus.NONE) {
                    appbar.addFunction(getString(R.string.submit), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            submit();
                        }
                    });
                    appbar.addFunction(R.mipmap.common_edit, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // 編輯
                            Intent intent = new Intent(view.getContext(), ReimbursementCreateActivity.class);
                            intent.putExtra(MyApplication.INTENT_KEY_TRIP, reimbursement.getReimbursement().getTrip_id());
                            startActivityForResult(intent, MyApplication.REQUEST_EDIT);
                        }
                    });
                }

                updateDetail();
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                ErrorHandler.getInstance().setException(ReimbursementDetailActivity.this, throwable);
            }
        }));

        //批審備註
        ApiHelper.getInstance().getTripApi().getApprovalDescription(TokenManager.getInstance().getToken(), tripId).enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                try {
                    JSONObject result = response.body();
                    int success = result.getInt("success");
                    if (success == 1) {
                        String description = result.getString("description");
                        if (!TextUtils.isEmpty(description))
                            tvApprovalDescription.setText(description);
                    }
                } catch (Exception e) {
                    ErrorHandler.getInstance().setException(ReimbursementDetailActivity.this, e);
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                ErrorHandler.getInstance().setException(ReimbursementDetailActivity.this, t);
            }
        });
    }

    protected void submit() {
        final MultipleSubmit submit = new MultipleSubmit();
        submit.setTrip_ids(reimbursement.getReimbursement().getTrip_id());

        ApiHelper.getInstance().getSubmitApi().submit(TokenManager.getInstance().getToken(), submit).enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                try {
                    switch (response.code()) {
                        case 200:
                            JSONObject result = response.body();
                            int success = result.getInt("success");
                            if (success == 1) {
                                DatabaseHelper.getInstance(ReimbursementDetailActivity.this).saveTripSubmit(submit)
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new CompletableObserver() {

                                            @Override
                                            public void onSubscribe(Disposable d) {
                                                mDisposable.add(d);
                                            }

                                            @Override
                                            public void onComplete() {
                                                setResult(ApprovalStatus.UNSIGN.getCode());
                                                finish();
                                            }

                                            @Override
                                            public void onError(Throwable e) {
                                                Toast.makeText(ReimbursementDetailActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        });
                            } else {
                                Toast.makeText(ReimbursementDetailActivity.this, result.getString("errorMsg"), Toast.LENGTH_LONG).show();
                            }
                            break;
                        case ApiHelper.ERROR_TOKEN_EXPIRED:
                            showLoginDialog();
                            break;
                        default:
                            Logger.e(TAG, "submit failed");
                    }
                } catch (Exception e) {
                    ErrorHandler.getInstance().setException(ReimbursementDetailActivity.this, e);
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                Toast.makeText(ReimbursementDetailActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void updateDetail() {
        if (reimbursement == null)
            finish();

        tvTitle.setText(reimbursement.getTrip().getName());
        tvName.setText(reimbursement.getUser());

        if (reimbursement.getReimbursement().getApproval() != null)
            tvApproval.setText(reimbursement.getReimbursement().getApproval().getTitle());

        tvCount.setText(getString(R.string.exp_count1) + reimbursement.getReimbursementItems().size() + getString(R.string.exp_count2));
        container.removeAllViews();

        if (reimbursement.getReimbursementItems().size() > 0) {
            float amount = 0;

            for (final ReimbursementItemWithConfig item : reimbursement.getReimbursementItems()) {
                View view = getLayoutInflater().inflate(R.layout.item_exp_detail, container, false);
                TextView tvTitle = view.findViewById( R.id.tvTitle);
                TextView tvDate = view.findViewById(R.id.tvDate);
                TextView tvAmount = view.findViewById( R.id.tvAmount);
                tvDate.setText(TimeFormater.getInstance().toDateFormat(item.getItem().getFrom_date()));

                if (item.getConfig() != null)
                    tvTitle.setText(item.getConfig().getName());

                tvAmount.setText(NumberFormater.getMoneyFormat(item.getItem().getAmount()));
                amount += item.getItem().getAmount();

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ReimbursementDetailActivity.this, ReimbursementItemDetailActivity.class);
                        intent.putExtra(MyApplication.INTENT_KEY_REIMBURSEMENT_ITEM, gson.toJson(item));
                        startActivity(intent);
                    }
                });
                container.addView(view);
            }
            tvTotal.setText(NumberFormater.getMoneyFormat(amount));

        } else {
            tvTotal.setText("0");
        }
    }

    @OnClick(R.id.btnApprover)
    void showApprover() {
        Intent intent = new Intent(this, ApprovalListActivity.class);
        intent.putExtra(MyApplication.INTENT_KEY_TRIP, reimbursement.getTrip().getId());
        startActivity(intent);
    }

    @OnClick(R.id.btnResource)
    void showResource() {
        Intent intent = new Intent(this, SaleResourceActivity.class);
        intent.putExtra(MyApplication.INTENT_KEY_ID, reimbursement.getReimbursement().getTrip_id());
        startActivity(intent);
    }
}
