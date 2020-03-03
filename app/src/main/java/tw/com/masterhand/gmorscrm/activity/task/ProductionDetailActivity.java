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

import java.util.List;

import butterknife.BindView;
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
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.TokenManager;
import tw.com.masterhand.gmorscrm.activity.news.SaleResourceActivity;
import tw.com.masterhand.gmorscrm.api.ApiHelper;
import tw.com.masterhand.gmorscrm.enums.ApprovalStatus;
import tw.com.masterhand.gmorscrm.enums.SubmitStatus;
import tw.com.masterhand.gmorscrm.model.MultipleSubmit;
import tw.com.masterhand.gmorscrm.model.ProductionWithConfig;
import tw.com.masterhand.gmorscrm.model.ProjectWithConfig;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.record.Customer;
import tw.com.masterhand.gmorscrm.room.record.File;
import tw.com.masterhand.gmorscrm.room.setting.ConfigIndustry;
import tw.com.masterhand.gmorscrm.room.setting.ConfigNewProjectProduction;
import tw.com.masterhand.gmorscrm.room.setting.User;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.tools.NumberFormater;
import tw.com.masterhand.gmorscrm.tools.TimeFormater;
import tw.com.masterhand.gmorscrm.view.Appbar;
import tw.com.masterhand.gmorscrm.view.CompanyCard;
import tw.com.masterhand.gmorscrm.view.ItemApproval;
import tw.com.masterhand.gmorscrm.view.ItemProject;

public class ProductionDetailActivity extends BaseUserCheckActivity {
    @BindView(R.id.appbar)
    Appbar appbar;
    @BindView(R.id.companyCard)
    protected CompanyCard companyCard;
    @BindView(R.id.itemProject)
    protected ItemProject itemProject;
    @BindView(R.id.tvTitle)
    protected TextView tvTitle;
    @BindView(R.id.tvIndustry)
    protected TextView tvIndustry;
    @BindView(R.id.tvPSW)
    protected TextView tvPSW;
    @BindView(R.id.tvCategory)
    protected TextView tvCategory;
    @BindView(R.id.tvYearAmount)
    protected TextView tvYearAmount;
    @BindView(R.id.tvModel)
    protected TextView tvModel;
    @BindView(R.id.tvDate)
    protected TextView tvDate;
    @BindView(R.id.tvAmount)
    protected TextView tvAmount;
    @BindView(R.id.tvDiscount)
    protected TextView tvDiscount;
    @BindView(R.id.tvBuilder)
    protected TextView tvBuilder;

    @BindView(R.id.tvApprovalDescription)
    protected TextView tvApprovalDescription;
    @BindView(R.id.tvNote)
    protected TextView tvNote;
    @BindView(R.id.btnApprover)
    protected Button btnApprover;
    @BindView(R.id.container_photo)
    protected LinearLayout containerPhoto;
    @BindView(R.id.itemApproval)
    protected ItemApproval itemApproval;

    protected ProductionWithConfig production;
    protected String viewerId;
    protected String tripId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_production_detail);
        init();
    }

    @Override
    protected void onUserChecked() {
        appbar.invalidate();
        viewerId = TokenManager.getInstance().getUser().getId();
        Logger.e(TAG, "viewerId:" + viewerId);
        getData();
    }

    protected void init() {

        appbar.setTitle(getString(R.string.apply_production) + getString(R.string
                .title_activity_work_detail));

        itemProject.hideIndex();
        itemProject.hideHistory();
        tripId = getIntent().getStringExtra(MyApplication.INTENT_KEY_TRIP);
        if (TextUtils.isEmpty(tripId)) {
            finish();
        }
    }

    void getData() {
        if (TextUtils.isEmpty(tripId))
            return;
        Logger.i(TAG, "tripId:" + tripId);
        mDisposable.add(DatabaseHelper.getInstance(this).getProductionByTrip(tripId).observeOn
                (AndroidSchedulers.mainThread()).subscribe(new Consumer<ProductionWithConfig>() {
            @Override
            public void accept(ProductionWithConfig result) throws Exception {
                production = result;
                /*2018-1-17新增:只有建立者可以修改*/
                /*2018-2-8新增:未提交項目才可以修改*/
                if (TokenManager.getInstance().getUser().getId().equals(result.getTrip()
                        .getUser_id()) && result.getTrip().getSubmit() == SubmitStatus.NONE) {
                    appbar.removeFuction();
                    appbar.addFunction(getString(R.string.submit), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            submit();
                        }
                    });
                    appbar.addFunction(R.mipmap.common_edit, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(view.getContext(), ProductionCreateActivity
                                    .class);
                            intent.putExtra(MyApplication.INTENT_KEY_TRIP, production
                                    .getProduction()
                                    .getTrip_id());
                            startActivity(intent);
                        }
                    });
                }
                updateDetail();
                updatePhoto(production.getFiles());
                getCustomer();
                getProject();
                onDataLoaded();
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Toast.makeText(ProductionDetailActivity.this, throwable.getMessage(), Toast
                        .LENGTH_LONG).show();
            }
        }));
        ApiHelper.getInstance().getTripApi().getApprovalDescription(TokenManager.getInstance()
                .getToken(), tripId).enqueue(new Callback<JSONObject>() {

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
                    ErrorHandler.getInstance().setException(ProductionDetailActivity.this, e);
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                ErrorHandler.getInstance().setException(ProductionDetailActivity.this, t);
            }
        });
    }

    protected void submit() {
        final MultipleSubmit submit = new MultipleSubmit();
        submit.setTrip_ids(production.getProduction().getTrip_id());
        ApiHelper.getInstance().getSubmitApi().submit(TokenManager
                .getInstance().getToken(), submit).enqueue(new Callback<JSONObject>() {

            @Override
            public void onResponse(Call<JSONObject> call,
                                   Response<JSONObject> response) {
                try {
                    switch (response.code()) {
                        case 200:
                            JSONObject result = response.body();
                            int success = result.getInt("success");
                            if (success == 1) {
                                DatabaseHelper.getInstance
                                        (ProductionDetailActivity.this)
                                        .saveTripSubmit(submit).observeOn
                                        (AndroidSchedulers.mainThread())
                                        .subscribe(new CompletableObserver() {

                                            @Override
                                            public void onSubscribe
                                                    (Disposable d) {
                                                mDisposable.add(d);
                                            }

                                            @Override
                                            public void onComplete() {
                                                setResult(ApprovalStatus.UNSIGN.getCode());
                                                finish();
                                            }

                                            @Override
                                            public void onError(Throwable e) {
                                                Toast.makeText
                                                        (ProductionDetailActivity
                                                                        .this,
                                                                e.getMessage(),
                                                                Toast.LENGTH_LONG)
                                                        .show();
                                            }
                                        });
                            } else {
                                Toast.makeText(ProductionDetailActivity.this,
                                        result.getString("errorMsg"),
                                        Toast.LENGTH_LONG).show();
                            }
                            break;
                        case ApiHelper.ERROR_TOKEN_EXPIRED:
                            showLoginDialog();
                            break;
                        default:
                            Logger.e(TAG, "submit failed");
                    }
                } catch (Exception e) {
                    ErrorHandler.getInstance().setException
                            (ProductionDetailActivity.this, e);
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                Toast.makeText(ProductionDetailActivity.this, t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    protected void onDataLoaded() {
        // 由子層實作
    }

    @OnClick(R.id.btnApprover)
    void showApproval() {
        Intent intent = new Intent(this, ApprovalListActivity.class);
        intent.putExtra(MyApplication.INTENT_KEY_TRIP, production.getProduction().getTrip_id());
        startActivity(intent);
    }

    @OnClick(R.id.btnResource)
    void showResource() {
        Intent intent = new Intent(this, SaleResourceActivity.class);
        intent.putExtra(MyApplication.INTENT_KEY_ID, production.getProduction().getTrip_id());
        startActivity(intent);
    }

    void updatePhoto(List<File> files) {
        containerPhoto.removeAllViews();
        for (File file : files) {
            containerPhoto.addView(generatePhotoItem(file));
        }
        if (containerPhoto.getChildCount() == 0)
            containerPhoto.addView(getEmptyTextView(getString(R.string.error_msg_no_photo)));
    }

    public void updateDetail() {
        tvTitle.setText(production.getTrip().getName());
        if (!TextUtils.isEmpty(production.getProduction().getIndustry_id()))
            mDisposable.add(DatabaseHelper.getInstance(this).getConfigIndustryById(production
                    .getProduction().getIndustry_id()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<ConfigIndustry>() {

                        @Override
                        public void accept(ConfigIndustry configIndustry) throws Exception {
                            tvIndustry.setText(configIndustry.getName());
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            ErrorHandler.getInstance().setException(ProductionDetailActivity
                                    .this, throwable);
                        }
                    }));
        String[] have = getResources().getStringArray(R.array.have);
        if (production.getProduction().getPsw() != -1)
            tvPSW.setText(have[production.getProduction().getPsw()]);
        if (!TextUtils.isEmpty(production.getProduction().getProduct()))
            mDisposable.add(DatabaseHelper.getInstance(this).getConfigNewProjectProductionById
                    (production.getProduction().getProduct()).observeOn(AndroidSchedulers
                    .mainThread()).subscribe(new Consumer<ConfigNewProjectProduction>() {

                @Override
                public void accept(ConfigNewProjectProduction configNewProjectProduction) throws
                        Exception {
                    tvCategory.setText(configNewProjectProduction.getName());
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    ErrorHandler.getInstance().setException(ProductionDetailActivity
                            .this, throwable);
                }
            }));
        if (production.getProduction().getYear_amount() > 0)
            tvYearAmount.setText(NumberFormater.getMoneyFormat(production.getProduction()
                    .getYear_amount()));
        if (!TextUtils.isEmpty(production.getProduction().getModel()))
            tvModel.setText(production.getProduction().getModel());
        if (production.getProduction().getDate() != null)
            tvDate.setText(TimeFormater.getInstance().toDateFormat(production.getProduction()
                    .getDate()));
        if (production.getProduction().getAmount() > 0)
            tvAmount.setText(NumberFormater.getMoneyFormat(production.getProduction().getAmount()));
        tvDiscount.setText(String.valueOf(production.getProduction().getDiscount()));

        if (!TextUtils.isEmpty(production.getTrip().getDescription()))
            tvNote.setText(production.getTrip().getDescription());
        mDisposable.add(DatabaseHelper.getInstance(this).getUserById(production.getTrip()
                .getUser_id()).observeOn
                (AndroidSchedulers.mainThread()).subscribe(new Consumer<User>() {

            @Override
            public void accept(User user) throws Exception {
                tvBuilder.setText(user.getShowName());
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                ErrorHandler.getInstance().setException(ProductionDetailActivity.this, throwable);
            }
        }));
    }

    public void getCustomer() {
        mDisposable.add(DatabaseHelper.getInstance(this).getCustomerById(production.getTrip()
                .getCustomer_id()).observeOn
                (AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Customer>() {
                    @Override
                    public void accept(Customer customer) throws Exception {
                        companyCard.setCustomer(customer);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ErrorHandler.getInstance().setException(ProductionDetailActivity.this,
                                throwable);
                    }
                }));
    }

    public void getProject() {
        mDisposable.add(DatabaseHelper.getInstance(this).getProjectById(production.getTrip()
                .getProject_id(), TokenManager.getInstance()
                .getUser().getDepartment_id(), TokenManager.getInstance().getUser().getCompany_id
                ()).observeOn
                (AndroidSchedulers.mainThread()).subscribe(new Consumer<ProjectWithConfig>() {

            @Override
            public void accept(ProjectWithConfig projectWithConfig) throws
                    Exception {
                itemProject.setProject(projectWithConfig);
                itemProject.setTripId(production.getTrip().getId());
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                ErrorHandler.getInstance().setException(ProductionDetailActivity.this,
                        throwable);
            }
        }));
    }

}
