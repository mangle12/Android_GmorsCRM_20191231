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
import tw.com.masterhand.gmorscrm.ContacterListActivity;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.TokenManager;
import tw.com.masterhand.gmorscrm.activity.news.SaleResourceActivity;
import tw.com.masterhand.gmorscrm.api.ApiHelper;
import tw.com.masterhand.gmorscrm.enums.ApprovalStatus;
import tw.com.masterhand.gmorscrm.enums.SubmitStatus;
import tw.com.masterhand.gmorscrm.model.ExpressWithConfig;
import tw.com.masterhand.gmorscrm.model.MultipleSubmit;
import tw.com.masterhand.gmorscrm.model.ProjectWithConfig;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.record.Customer;
import tw.com.masterhand.gmorscrm.room.record.File;
import tw.com.masterhand.gmorscrm.room.setting.Admin;
import tw.com.masterhand.gmorscrm.room.setting.ConfigCompanyHiddenField;
import tw.com.masterhand.gmorscrm.room.setting.ConfigExpressDestination;
import tw.com.masterhand.gmorscrm.room.setting.ConfigQuotationProductCategory;
import tw.com.masterhand.gmorscrm.room.setting.User;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.tools.TimeFormater;
import tw.com.masterhand.gmorscrm.view.Appbar;
import tw.com.masterhand.gmorscrm.view.CompanyCard;
import tw.com.masterhand.gmorscrm.view.ItemApproval;
import tw.com.masterhand.gmorscrm.view.ItemProject;

public class ExpressDetailActivity extends BaseUserCheckActivity {
    @BindView(R.id.appbar)
    Appbar appbar;
    @BindView(R.id.companyCard)
    protected CompanyCard companyCard;
    @BindView(R.id.itemProject)
    protected ItemProject itemProject;
    @BindView(R.id.tvTitle)
    protected TextView tvTitle;

    @BindView(R.id.tvType)
    protected TextView tvType;
    @BindView(R.id.tvContract)
    protected TextView tvContract;
    @BindView(R.id.tvModel)
    protected TextView tvModel;
    @BindView(R.id.tvAmount)
    protected TextView tvAmount;
    @BindView(R.id.tvExpense)
    protected TextView tvExpense;
    @BindView(R.id.tvDestination)
    protected TextView tvDestination;
    @BindView(R.id.tvCategory)
    protected TextView tvCategory;
    @BindView(R.id.tvOrigin)
    protected TextView tvOrigin;
    @BindView(R.id.tvArrivalPayment)
    protected TextView tvArrivalPayment;
    @BindView(R.id.tvRepaymentDate)
    protected TextView tvRepaymentDate;


    @BindView(R.id.tvReason)
    protected TextView tvReason;
    @BindView(R.id.containerCategory)
    protected LinearLayout containerCategory;
    @BindView(R.id.containerOrigin)
    protected LinearLayout containerOrigin;
    @BindView(R.id.containerArrivalPayment)
    protected LinearLayout containerArrivalPayment;
    @BindView(R.id.containerRepaymentDate)
    protected LinearLayout containerRepaymentDate;
    @BindView(R.id.containerType)
    protected LinearLayout containerType;
    @BindView(R.id.containerModel)
    protected LinearLayout containerModel;
    @BindView(R.id.containerExpense)
    protected LinearLayout containerExpense;
    @BindView(R.id.containerDestination)
    protected LinearLayout containerDestination;

    @BindView(R.id.tvBuilder)
    protected TextView tvBuilder;
    @BindView(R.id.tvNote)
    protected TextView tvNote;
    @BindView(R.id.tvAssistant)
    protected TextView tvAssistant;
    @BindView(R.id.tvApprovalDescription)
    protected TextView tvApprovalDescription;
    @BindView(R.id.btnContacter)
    protected Button btnContacter;
    @BindView(R.id.btnApprover)
    protected Button btnApprover;
    @BindView(R.id.container_photo)
    protected LinearLayout containerPhoto;
    @BindView(R.id.itemApproval)
    protected ItemApproval itemApproval;

    protected ExpressWithConfig express;
    protected String viewerId;
    protected String tripId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_express_detail);
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

        appbar.setTitle(getString(R.string.apply_express) + getString(R.string
                .title_activity_work_detail));

        itemProject.hideIndex();
        itemProject.hideHistory();
        tripId = getIntent().getStringExtra(MyApplication.INTENT_KEY_TRIP);
        if (TextUtils.isEmpty(tripId)) {
            finish();
        }
        getHiddenField();
    }

    void getHiddenField() {
        mDisposable.add(DatabaseHelper.getInstance(this).getConfigCompanyHiddenField
                (TokenManager
                        .getInstance().getUser().getCompany_id()).observeOn
                (AndroidSchedulers.mainThread()).subscribe(new Consumer<ConfigCompanyHiddenField>
                () {

            @Override
            public void accept(ConfigCompanyHiddenField configCompanyHiddenField) throws
                    Exception {
                if (configCompanyHiddenField.getExpress_hidden_product_category_id()) {
                    containerCategory.setVisibility(View.GONE);
                }
                if (configCompanyHiddenField.getExpress_hidden_origin()) {
                    containerOrigin.setVisibility(View.GONE);
                }
                if (configCompanyHiddenField.getExpress_hidden_arrival_payment()) {
                    containerArrivalPayment.setVisibility(View.GONE);
                }
                if (configCompanyHiddenField.getExpress_hidden_repayment_date()) {
                    containerRepaymentDate.setVisibility(View.GONE);
                }
                if (configCompanyHiddenField.getExpress_hidden_type()) {
                    containerType.setVisibility(View.GONE);
                }
                if (configCompanyHiddenField.getExpress_hidden_model()) {
                    containerModel.setVisibility(View.GONE);
                }
                if (configCompanyHiddenField.getExpress_hidden_expense()) {
                    containerExpense.setVisibility(View.GONE);
                }
                if (configCompanyHiddenField.getExpress_hidden_destination()) {
                    containerDestination.setVisibility(View.GONE);
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                ErrorHandler.getInstance().setException(ExpressDetailActivity.this,
                        throwable);
            }
        }));
    }

    protected void getData() {
        if (TextUtils.isEmpty(tripId))
            return;
        Logger.i(TAG, "tripId:" + tripId);
        mDisposable.add(DatabaseHelper.getInstance(this).getExpressByTrip(tripId).observeOn
                (AndroidSchedulers.mainThread()).subscribe(new Consumer<ExpressWithConfig>() {
            @Override
            public void accept(ExpressWithConfig result) throws Exception {
                express = result;
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
                            Intent intent = new Intent(view.getContext(), ExpressCreateActivity
                                    .class);
                            intent.putExtra(MyApplication.INTENT_KEY_TRIP, express
                                    .getExpress()
                                    .getTrip_id());
                            startActivity(intent);
                        }
                    });
                }
                updateDetail();
                updatePhoto(express.getFiles());
                getCustomer();
                getProject();
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Toast.makeText(ExpressDetailActivity.this, throwable.getMessage(), Toast
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
                    ErrorHandler.getInstance().setException(ExpressDetailActivity.this, e);
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                ErrorHandler.getInstance().setException(ExpressDetailActivity.this, t);
            }
        });
    }

    protected void submit() {
        final MultipleSubmit submit = new MultipleSubmit();
        submit.setTrip_ids(express.getExpress().getTrip_id());
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
                                        (ExpressDetailActivity.this)
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
                                                        (ExpressDetailActivity
                                                                        .this,
                                                                e.getMessage(),
                                                                Toast.LENGTH_LONG)
                                                        .show();
                                            }
                                        });
                            } else {
                                Toast.makeText(ExpressDetailActivity.this,
                                        result.getString("errorMsg"),
                                        Toast.LENGTH_LONG).show();
                            }
                            break;
                        case ApiHelper.ERROR_TOKEN_EXPIRED:
                            showLoginDialog();
                            break;
                        default:
                            Logger.e(TAG, "submit failed:" + response.code());
                    }
                } catch (Exception e) {
                    ErrorHandler.getInstance().setException
                            (ExpressDetailActivity.this, e);
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                Toast.makeText(ExpressDetailActivity.this, t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    @OnClick(R.id.btnContacter)
    void showContacter() {
        if (express.getContacters().size() > 0) {
            Intent intent = new Intent(this, ContacterListActivity.class);
            intent.putExtra(MyApplication.INTENT_KEY_PARENT, express.getExpress().getId());
            startActivity(intent);
        }
    }

    @OnClick(R.id.btnApprover)
    void showApproval() {
        Intent intent = new Intent(this, ApprovalListActivity.class);
        intent.putExtra(MyApplication.INTENT_KEY_TRIP, express.getExpress().getTrip_id());
        startActivity(intent);
    }

    @OnClick(R.id.btnResource)
    void showResource() {
        Intent intent = new Intent(this, SaleResourceActivity.class);
        intent.putExtra(MyApplication.INTENT_KEY_ID, express.getExpress().getTrip_id());
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
        tvTitle.setText(express.getTrip().getName());
        if (express.getExpress().getType() != null)
            tvType.setText(express.getExpress().getType().getTitle());
        if (!TextUtils.isEmpty(express.getExpress().getContract_number()))
            tvContract.setText(express.getExpress().getContract_number());
        if (!TextUtils.isEmpty(express.getExpress().getModel()))
            tvModel.setText(express.getExpress().getModel());
        if (express.getExpress().getAmount() > 0)
            tvAmount.setText(String.valueOf(express.getExpress().getAmount()));
        if (express.getExpress().getExpense() > 0)
            tvExpense.setText(String.valueOf(express.getExpress().getExpense()));
        if (!TextUtils.isEmpty(express.getExpress().getProduct_category_id()))
            mDisposable.add(DatabaseHelper.getInstance(this)
                    .getConfigQuotationProductCategoryById(express.getExpress()
                            .getProduct_category_id())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<ConfigQuotationProductCategory>() {

                        @Override
                        public void accept(ConfigQuotationProductCategory
                                                   configQuotationProductCategory) throws
                                Exception {
                            tvCategory.setText(configQuotationProductCategory.getName());
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            ErrorHandler.getInstance().setException(ExpressDetailActivity.this,
                                    throwable);
                        }
                    }));
        if (!TextUtils.isEmpty(express.getExpress().getOrigin()))
            tvOrigin.setText(express.getExpress().getOrigin());
        if (express.getExpress().getArrival_payment() != -1)
            tvArrivalPayment.setText(getResources().getTextArray(R.array.have)[express.getExpress
                    ().getArrival_payment()]);
        if (express.getExpress().getRepayment_date() != null)
            tvRepaymentDate.setText(TimeFormater.getInstance().toDateFormat(express.getExpress()
                    .getRepayment_date()));
        if (!TextUtils.isEmpty(express.getExpress().getReason()))
            tvReason.setText(express.getExpress().getReason());
        if (express.getContacters().size() > 0) {
            btnContacter.setText(getString(R.string.contact) + " " + express.getContacters().size
                    ());
        } else {
            btnContacter.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(express.getExpress().getDestination()))
            mDisposable.add(DatabaseHelper.getInstance(this)
                    .getConfigExpressDestinationById(express.getExpress().getDestination())
                    .observeOn(AndroidSchedulers.mainThread
                            ()).subscribe(new Consumer<ConfigExpressDestination>() {

                        @Override
                        public void accept(ConfigExpressDestination config) throws Exception {
                            tvDestination.setText(config.getName());
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            tvDestination.setText(R.string.empty_show);
                        }
                    }));
        if (!TextUtils.isEmpty(express.getTrip().getAssistant_id()))
            mDisposable.add(DatabaseHelper.getInstance(this)
                    .getAdminById(express.getTrip().getAssistant_id())
                    .observeOn(AndroidSchedulers.mainThread
                            ()).subscribe(new Consumer<Admin>() {

                        @Override
                        public void accept(Admin config) throws Exception {
                            tvAssistant.setText(config.getName());
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            tvAssistant.setText(R.string.empty_show);
                        }
                    }));
        if (!TextUtils.isEmpty(express.getTrip().getDescription()))
            tvNote.setText(express.getTrip().getDescription());
        mDisposable.add(DatabaseHelper.getInstance(this).getUserById(express.getTrip().getUser_id
                ()).observeOn
                (AndroidSchedulers.mainThread()).subscribe(new Consumer<User>() {

            @Override
            public void accept(User user) throws Exception {
                tvBuilder.setText(user.getShowName());
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                ErrorHandler.getInstance().setException(ExpressDetailActivity.this, throwable);
            }
        }));
    }

    public void getCustomer() {
        mDisposable.add(DatabaseHelper.getInstance(this).getCustomerById(express.getTrip()
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
                        ErrorHandler.getInstance().setException(ExpressDetailActivity.this,
                                throwable);
                    }
                }));
    }

    public void getProject() {
        mDisposable.add(DatabaseHelper.getInstance(this).getProjectById(express.getTrip()
                .getProject_id(), TokenManager.getInstance()
                .getUser().getDepartment_id(), TokenManager.getInstance().getUser().getCompany_id
                ()).observeOn
                (AndroidSchedulers.mainThread()).subscribe(new Consumer<ProjectWithConfig>() {

            @Override
            public void accept(ProjectWithConfig projectWithConfig) throws
                    Exception {
                itemProject.setProject(projectWithConfig);
                itemProject.setTripId(express.getTrip().getId());
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                ErrorHandler.getInstance().setException(ExpressDetailActivity.this,
                        throwable);
            }
        }));
    }
}
