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
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.TokenManager;
import tw.com.masterhand.gmorscrm.activity.news.SaleResourceActivity;
import tw.com.masterhand.gmorscrm.api.ApiHelper;
import tw.com.masterhand.gmorscrm.enums.ApprovalStatus;
import tw.com.masterhand.gmorscrm.enums.SubmitStatus;
import tw.com.masterhand.gmorscrm.model.MultipleSubmit;
import tw.com.masterhand.gmorscrm.model.ProjectWithConfig;
import tw.com.masterhand.gmorscrm.model.SpecialPriceWithConfig;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.record.Customer;
import tw.com.masterhand.gmorscrm.room.record.File;
import tw.com.masterhand.gmorscrm.room.record.SpecialPriceProduct;
import tw.com.masterhand.gmorscrm.room.setting.Admin;
import tw.com.masterhand.gmorscrm.room.setting.ConfigCompanyHiddenField;
import tw.com.masterhand.gmorscrm.room.setting.User;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.view.Appbar;
import tw.com.masterhand.gmorscrm.view.CompanyCard;
import tw.com.masterhand.gmorscrm.view.ItemApproval;
import tw.com.masterhand.gmorscrm.view.ItemProject;

public class SpecialPriceDetailActivity extends BaseUserCheckActivity {
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
    @BindView(R.id.tvStatus)
    protected TextView tvStatus;
    @BindView(R.id.tvApproval)
    protected TextView tvApproval;
    @BindView(R.id.containerApproval)
    protected LinearLayout containerApproval;
    @BindView(R.id.tvYearAmount)
    protected TextView tvYearAmount;
    @BindView(R.id.tvYearDiscount)
    protected TextView tvYearDiscount;
    @BindView(R.id.tvOffer)
    protected TextView tvOffer;
    @BindView(R.id.tvReason)
    protected TextView tvReason;
    @BindView(R.id.tvBuilder)
    protected TextView tvBuilder;

    @BindView(R.id.tvNote)
    protected TextView tvNote;
    @BindView(R.id.tvAssistant)
    protected TextView tvAssistant;
    @BindView(R.id.tvApprovalDescription)
    protected TextView tvApprovalDescription;
    @BindView(R.id.btnApprover)
    protected Button btnApprover;
    @BindView(R.id.containerStatus)
    protected LinearLayout containerStatus;
    @BindView(R.id.containerYearDiscount)
    protected LinearLayout containerYearDiscount;
    @BindView(R.id.containerYearAmount)
    protected LinearLayout containerYearAmount;
    @BindView(R.id.containerOffer)
    protected LinearLayout containerOffer;
    @BindView(R.id.containerItem)
    protected LinearLayout containerItem;
    @BindView(R.id.container_photo)
    protected LinearLayout containerPhoto;
    @BindView(R.id.itemApproval)
    protected ItemApproval itemApproval;

    protected SpecialPriceWithConfig specialPrice;
    protected String viewerId;
    protected String tripId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_special_price_detail);
        init();
    }

    @Override
    protected void onUserChecked() {
        appbar.invalidate();
        viewerId = TokenManager.getInstance().getUser().getId();
        Logger.e(TAG, "viewerId:" + viewerId);
        getHiddenField();
        getData();
    }

    protected void init() {

        appbar.setTitle(getString(R.string.apply_special_price) + getString(R.string
                .title_activity_work_detail));

        itemProject.hideIndex();
        itemProject.hideHistory();
        tripId = getIntent().getStringExtra(MyApplication.INTENT_KEY_TRIP);
        if (TextUtils.isEmpty(tripId)) {
            finish();
        }
    }

    protected void getData() {
        if (TextUtils.isEmpty(tripId))
            return;
        Logger.i(TAG, "tripId:" + tripId);
        mDisposable.add(DatabaseHelper.getInstance(this).getSpecialPriceByTrip(tripId).observeOn
                (AndroidSchedulers.mainThread()).subscribe(new Consumer<SpecialPriceWithConfig>() {
            @Override
            public void accept(SpecialPriceWithConfig result) throws Exception {
                specialPrice = result;
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
                            Intent intent = new Intent(view.getContext(), SpecialPriceCreateActivity
                                    .class);
                            intent.putExtra(MyApplication.INTENT_KEY_TRIP, specialPrice
                                    .getSpecialPrice()
                                    .getTrip_id());
                            startActivity(intent);
                        }
                    });
                }
                updateDetail();
                updatePhoto(specialPrice.getFiles());
                getCustomer();
                getProject();
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Toast.makeText(SpecialPriceDetailActivity.this, throwable.getMessage(), Toast
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
                    ErrorHandler.getInstance().setException(SpecialPriceDetailActivity.this, e);
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                ErrorHandler.getInstance().setException(SpecialPriceDetailActivity.this, t);
            }
        });
    }

    protected void submit() {
        final MultipleSubmit submit = new MultipleSubmit();
        submit.setTrip_ids(specialPrice.getSpecialPrice().getTrip_id());
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
                                        (SpecialPriceDetailActivity.this)
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
                                                        (SpecialPriceDetailActivity
                                                                        .this,
                                                                e.getMessage(),
                                                                Toast.LENGTH_LONG)
                                                        .show();
                                            }
                                        });
                            } else {
                                Toast.makeText(SpecialPriceDetailActivity.this,
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
                            (SpecialPriceDetailActivity.this, e);
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                Toast.makeText(SpecialPriceDetailActivity.this, t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    @OnClick(R.id.btnApprover)
    void showApproval() {
        Intent intent = new Intent(this, ApprovalListActivity.class);
        intent.putExtra(MyApplication.INTENT_KEY_TRIP, specialPrice.getSpecialPrice().getTrip_id());
        startActivity(intent);
    }

    @OnClick(R.id.btnResource)
    void showResource() {
        Intent intent = new Intent(this, SaleResourceActivity.class);
        intent.putExtra(MyApplication.INTENT_KEY_ID, specialPrice.getSpecialPrice().getTrip_id());
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
        tvTitle.setText(specialPrice.getTrip().getName());
        if (specialPrice.getSpecialPrice().getType() != null)
            tvType.setText(specialPrice.getSpecialPrice().getType().getTitle());
        if (specialPrice.getSpecialPrice().getProject_status() != null)
            tvStatus.setText(specialPrice.getSpecialPrice().getProject_status().getTitle());
        if (specialPrice.getSpecialPrice().getApproval() != null)
            tvApproval.setText(specialPrice.getSpecialPrice().getApproval().getTitle());
        if (specialPrice.getSpecialPrice().getYear_amount() > 0)
            tvYearAmount.setText(String.valueOf(specialPrice.getSpecialPrice().getYear_amount()));
        if (specialPrice.getSpecialPrice().getYear_discount() > 0)
            tvYearDiscount.setText(String.valueOf(specialPrice.getSpecialPrice().getYear_discount
                    ()));
        if (!TextUtils.isEmpty(specialPrice.getSpecialPrice().getOffer()))
            tvOffer.setText(specialPrice.getSpecialPrice().getOffer());
        if (!TextUtils.isEmpty(specialPrice.getSpecialPrice().getReason()))
            tvReason.setText(specialPrice.getSpecialPrice().getReason());

        if (specialPrice.getProducts().size() > 0) {
            containerItem.removeAllViews();
            for (SpecialPriceProduct product : specialPrice.getProducts()) {
                generateItem(product);
            }
        }
        mDisposable.add(DatabaseHelper.getInstance(this)
                .getAdminById(specialPrice.getTrip().getAssistant_id())
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
        if (!TextUtils.isEmpty(specialPrice.getTrip().getDescription()))
            tvNote.setText(specialPrice.getTrip().getDescription());
        mDisposable.add(DatabaseHelper.getInstance(this).getUserById(specialPrice.getTrip()
                .getUser_id()).observeOn
                (AndroidSchedulers.mainThread()).subscribe(new Consumer<User>() {

            @Override
            public void accept(User user) throws Exception {
                tvBuilder.setText(user.getShowName());
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                ErrorHandler.getInstance().setException(SpecialPriceDetailActivity.this, throwable);
            }
        }));
    }

    private void generateItem(final SpecialPriceProduct product) {
        View view = getLayoutInflater().inflate(R.layout.item_quotation_detail, containerItem,
                false);
        TextView tvTitle = view.findViewById( R.id.textView_title);
        tvTitle.setText(product.getDescription());
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(),
                        SpecialPriceProductCreateActivity.class);
                intent.putExtra(MyApplication.INTENT_KEY_PRODUCT, gson.toJson
                        (product));
                intent.putExtra(MyApplication.INTENT_KEY_ENABLE, false);
                startActivityForResult(intent, MyApplication.REQUEST_EDIT_PRODUCT);
            }
        });
        containerItem.addView(view, new LinearLayout.LayoutParams(LinearLayout.LayoutParams
                .MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.list_size)));
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
                if (configCompanyHiddenField.getSpecial_price_hidden_approval()) {
                    containerApproval.setVisibility(View.GONE);
                }
                if (configCompanyHiddenField.getSpecial_price_hidden_project_status()) {
                    containerStatus.setVisibility(View.GONE);
                }
                if (configCompanyHiddenField.getSpecial_price_hidden_year_discount()) {
                    containerYearDiscount.setVisibility(View.GONE);
                }
                if (configCompanyHiddenField.getSpecial_price_hidden_offer()) {
                    containerOffer.setVisibility(View.GONE);
                }
                if (configCompanyHiddenField.getSpecial_price_hidden_amount()) {
                    containerYearAmount.setVisibility(View.GONE);
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                ErrorHandler.getInstance().setException(SpecialPriceDetailActivity.this,
                        throwable);
            }
        }));
    }

    public void getCustomer() {
        mDisposable.add(DatabaseHelper.getInstance(this).getCustomerById(specialPrice.getTrip()
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
                        ErrorHandler.getInstance().setException(SpecialPriceDetailActivity.this,
                                throwable);
                    }
                }));
    }

    public void getProject() {
        mDisposable.add(DatabaseHelper.getInstance(this).getProjectById(specialPrice.getTrip()
                .getProject_id(), TokenManager.getInstance()
                .getUser().getDepartment_id(), TokenManager.getInstance().getUser().getCompany_id
                ()).observeOn
                (AndroidSchedulers.mainThread()).subscribe(new Consumer<ProjectWithConfig>() {

            @Override
            public void accept(ProjectWithConfig projectWithConfig) throws
                    Exception {
                itemProject.setProject(projectWithConfig);
                itemProject.setTripId(specialPrice.getTrip().getId());
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                ErrorHandler.getInstance().setException(SpecialPriceDetailActivity.this,
                        throwable);
            }
        }));
    }

}
