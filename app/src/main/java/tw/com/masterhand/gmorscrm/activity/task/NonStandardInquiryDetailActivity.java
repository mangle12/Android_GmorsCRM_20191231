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
import tw.com.masterhand.gmorscrm.model.NonStandardInquiryWithConfig;
import tw.com.masterhand.gmorscrm.model.ProjectWithConfig;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.record.Customer;
import tw.com.masterhand.gmorscrm.room.record.File;
import tw.com.masterhand.gmorscrm.room.record.NonStandardInquiryProduct;
import tw.com.masterhand.gmorscrm.room.setting.Admin;
import tw.com.masterhand.gmorscrm.room.setting.ConfigOffice;
import tw.com.masterhand.gmorscrm.room.setting.User;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.view.Appbar;
import tw.com.masterhand.gmorscrm.view.CompanyCard;
import tw.com.masterhand.gmorscrm.view.Dropdown;
import tw.com.masterhand.gmorscrm.view.ItemApproval;
import tw.com.masterhand.gmorscrm.view.ItemProject;

public class NonStandardInquiryDetailActivity extends BaseUserCheckActivity {
    @BindView(R.id.appbar)
    Appbar appbar;
    @BindView(R.id.companyCard)
    protected CompanyCard companyCard;
    @BindView(R.id.itemProject)
    protected ItemProject itemProject;
    @BindView(R.id.tvTitle)
    protected TextView tvTitle;
    @BindView(R.id.tvCode)
    protected TextView tvCode;
    @BindView(R.id.tvNeedParity)
    protected TextView tvNeedParity;
    @BindView(R.id.tvOffice)
    protected TextView tvOffice;
    @BindView(R.id.tvSpecial)
    protected TextView tvSpecial;
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
    @BindView(R.id.containerItem)
    protected LinearLayout containerItem;
    @BindView(R.id.container_photo)
    protected LinearLayout containerPhoto;
    @BindView(R.id.itemApproval)
    protected ItemApproval itemApproval;

    protected NonStandardInquiryWithConfig inquiry;
    protected String viewerId;
    protected String tripId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_non_standard_inquiry_detail);
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

        appbar.setTitle(getString(R.string.apply_non_standard_inquiry) + getString(R.string
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
        mDisposable.add(DatabaseHelper.getInstance(this).getNonStandardInquiryByTrip(tripId)
                .observeOn
                        (AndroidSchedulers.mainThread()).subscribe(new Consumer<NonStandardInquiryWithConfig>() {
                    @Override
                    public void accept(NonStandardInquiryWithConfig result) throws Exception {
                        inquiry = result;
                        /*2018-1-17新增:只有建立者可以修改*/
                        /*2018-2-8新增:未提交項目才可以修改*/
                        if (TokenManager.getInstance().getUser().getId().equals(result.getTrip()
                                .getUser_id()) && result.getTrip().getSubmit() == SubmitStatus
                                .NONE) {
                            appbar.removeFuction();
                            appbar.addFunction(getString(R.string.submit), new View
                                    .OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    submit();
                                }
                            });
                            appbar.addFunction(R.mipmap.common_edit, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(view.getContext(),
                                            NonStandardInquiryCreateActivity
                                                    .class);
                                    intent.putExtra(MyApplication.INTENT_KEY_TRIP, inquiry
                                            .getInquiry()
                                            .getTrip_id());
                                    startActivity(intent);
                                }
                            });
                        }
                        updateDetail();
                        updatePhoto(inquiry.getFiles());
                        getCustomer();
                        getProject();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(NonStandardInquiryDetailActivity.this, throwable
                                .getMessage(), Toast
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
                    ErrorHandler.getInstance().setException(NonStandardInquiryDetailActivity
                            .this, e);
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                ErrorHandler.getInstance().setException(NonStandardInquiryDetailActivity.this, t);
            }
        });
    }

    protected void submit() {
        final MultipleSubmit submit = new MultipleSubmit();
        submit.setTrip_ids(inquiry.getInquiry().getTrip_id());
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
                                        (NonStandardInquiryDetailActivity.this)
                                        .saveTripSubmit(submit)
                                        .observeOn
                                                (AndroidSchedulers
                                                        .mainThread())
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
                                            public void onError
                                                    (Throwable e) {
                                                Toast.makeText
                                                        (NonStandardInquiryDetailActivity
                                                                        .this,
                                                                e.getMessage(),
                                                                Toast.LENGTH_LONG)
                                                        .show();
                                            }
                                        });
                            } else {
                                Toast.makeText
                                        (NonStandardInquiryDetailActivity.this,
                                                result.getString
                                                        ("errorMsg"),
                                                Toast.LENGTH_LONG)
                                        .show();
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
                            (NonStandardInquiryDetailActivity.this, e);
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                Toast.makeText(NonStandardInquiryDetailActivity.this, t
                                .getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    @OnClick(R.id.btnApprover)
    void showApproval() {
        Intent intent = new Intent(this, ApprovalListActivity.class);
        intent.putExtra(MyApplication.INTENT_KEY_TRIP, inquiry.getInquiry().getTrip_id());
        startActivity(intent);
    }

    @OnClick(R.id.btnResource)
    void showResource() {
        Intent intent = new Intent(this, SaleResourceActivity.class);
        intent.putExtra(MyApplication.INTENT_KEY_ID, inquiry.getInquiry().getTrip_id());
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
        String[] have = getResources().getStringArray(R.array.have);
        tvTitle.setText(inquiry.getTrip().getName());
        if (!TextUtils.isEmpty(inquiry.getInquiry().getCode_number()))
            tvCode.setText(inquiry.getInquiry().getCode_number());
        if (inquiry.getInquiry().getNeed_parity() != Dropdown.VALUE_EMPTY)
            tvNeedParity.setText(have[inquiry.getInquiry().getNeed_parity()]);
        if (!TextUtils.isEmpty(inquiry.getInquiry().getOffice_id()))
            mDisposable.add(DatabaseHelper.getInstance(this).getConfigOfficeById(inquiry
                    .getInquiry().getOffice_id()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<ConfigOffice>() {

                        @Override
                        public void accept(ConfigOffice configOffice) throws Exception {
                            tvOffice.setText(configOffice.getName());
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            ErrorHandler.getInstance().setException
                                    (NonStandardInquiryDetailActivity.this, throwable);
                        }
                    }));
        if (!TextUtils.isEmpty(inquiry.getInquiry().getNote()))
            tvSpecial.setText(inquiry.getInquiry().getNote());

        if (inquiry.getProducts().size() > 0) {
            containerItem.removeAllViews();
            for (NonStandardInquiryProduct product : inquiry.getProducts()) {
                generateItem(product);
            }
        }
        mDisposable.add(DatabaseHelper.getInstance(this)
                .getAdminById(inquiry.getTrip().getAssistant_id())
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
        if (!TextUtils.isEmpty(inquiry.getTrip().getDescription()))
            tvNote.setText(inquiry.getTrip().getDescription());
        mDisposable.add(DatabaseHelper.getInstance(this).getUserById(inquiry.getTrip().getUser_id
                ()).observeOn
                (AndroidSchedulers.mainThread()).subscribe(new Consumer<User>() {

            @Override
            public void accept(User user) throws Exception {
                tvBuilder.setText(user.getShowName());
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                ErrorHandler.getInstance().setException(NonStandardInquiryDetailActivity.this,
                        throwable);
            }
        }));
    }

    private void generateItem(final NonStandardInquiryProduct product) {
        View view = getLayoutInflater().inflate(R.layout.item_quotation_detail, containerItem,
                false);
        TextView tvTitle = view.findViewById(R.id.textView_title);
        tvTitle.setText(product.getCode_number());
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(),
                        NonStandardInquiryProductCreateActivity.class);
                intent.putExtra(MyApplication.INTENT_KEY_PRODUCT, gson.toJson
                        (product));
                intent.putExtra(MyApplication.INTENT_KEY_ENABLE, false);
                startActivityForResult(intent, MyApplication.REQUEST_EDIT_PRODUCT);
            }
        });
        containerItem.addView(view, new LinearLayout.LayoutParams(LinearLayout.LayoutParams
                .MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.list_size)));
    }

    public void getCustomer() {
        mDisposable.add(DatabaseHelper.getInstance(this).getCustomerById(inquiry.getTrip()
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
                        ErrorHandler.getInstance().setException(NonStandardInquiryDetailActivity
                                        .this,
                                throwable);
                    }
                }));
    }

    public void getProject() {
        mDisposable.add(DatabaseHelper.getInstance(this).getProjectById(inquiry.getTrip()
                .getProject_id(), TokenManager.getInstance()
                .getUser().getDepartment_id(), TokenManager.getInstance().getUser().getCompany_id
                ()).observeOn
                (AndroidSchedulers.mainThread()).subscribe(new Consumer<ProjectWithConfig>() {

            @Override
            public void accept(ProjectWithConfig projectWithConfig) throws
                    Exception {
                itemProject.setProject(projectWithConfig);
                itemProject.setTripId(inquiry.getTrip().getId());
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                ErrorHandler.getInstance().setException(NonStandardInquiryDetailActivity.this,
                        throwable);
            }
        }));
    }

}
