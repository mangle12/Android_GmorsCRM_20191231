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
import tw.com.masterhand.gmorscrm.model.SpringRingInquiryWithConfig;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.record.Customer;
import tw.com.masterhand.gmorscrm.room.record.File;
import tw.com.masterhand.gmorscrm.room.record.SpringRingInquiryProduct;
import tw.com.masterhand.gmorscrm.room.setting.Admin;
import tw.com.masterhand.gmorscrm.room.setting.ConfigCompetitiveStatus;
import tw.com.masterhand.gmorscrm.room.setting.ConfigInquiryProductType;
import tw.com.masterhand.gmorscrm.room.setting.ConfigSealedState;
import tw.com.masterhand.gmorscrm.room.setting.ConfigSgbAdvantage;
import tw.com.masterhand.gmorscrm.room.setting.ConfigSgbDisadvantage;
import tw.com.masterhand.gmorscrm.room.setting.ConfigTrend;
import tw.com.masterhand.gmorscrm.room.setting.ConfigYearPotential;
import tw.com.masterhand.gmorscrm.room.setting.User;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.view.Appbar;
import tw.com.masterhand.gmorscrm.view.CompanyCard;
import tw.com.masterhand.gmorscrm.view.Dropdown;
import tw.com.masterhand.gmorscrm.view.ItemApproval;
import tw.com.masterhand.gmorscrm.view.ItemProject;

public class SpringRingInquiryDetailActivity extends BaseUserCheckActivity {
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
    @BindView(R.id.tvInquiryName)
    protected TextView tvInquiryName;
    @BindView(R.id.tvApply)
    protected TextView tvApply;
    @BindView(R.id.tvPotential)
    protected TextView tvPotential;
    @BindView(R.id.tvNeedImage)
    protected TextView tvNeedImage;
    @BindView(R.id.tvNeedParity)
    protected TextView tvNeedParity;
    @BindView(R.id.tvProductType)
    protected TextView tvProductType;
    @BindView(R.id.tvCompetitive)
    protected TextView tvCompetitive;
    @BindView(R.id.tvAdvantage)
    protected TextView tvAdvantage;
    @BindView(R.id.tvDisadvantage)
    protected TextView tvDisadvantage;
    @BindView(R.id.tvTrend)
    protected TextView tvTrend;
    @BindView(R.id.tvSealed)
    protected TextView tvSealed;
    @BindView(R.id.tvSpeed)
    protected TextView tvSpeed;
    @BindView(R.id.tvPressure)
    protected TextView tvPressure;
    @BindView(R.id.tvTemperature)
    protected TextView tvTemperature;
    @BindView(R.id.tvLifetime)
    protected TextView tvLifetime;
    @BindView(R.id.tvStaticMaterial)
    protected TextView tvStaticMaterial;
    @BindView(R.id.tvStaticHardness)
    protected TextView tvStaticHardness;
    @BindView(R.id.tvStaticSmoothness)
    protected TextView tvStaticSmoothness;
    @BindView(R.id.tvDynamicMaterial)
    protected TextView tvDynamicMaterial;
    @BindView(R.id.tvDynamicHardness)
    protected TextView tvDynamicHardness;
    @BindView(R.id.tvDynamicSmoothness)
    protected TextView tvDynamicSmoothness;
    @BindView(R.id.tvOther)
    protected TextView tvOther;
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

    protected SpringRingInquiryWithConfig inquiry;
    protected String viewerId;
    protected String tripId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spring_ring_inquiry_detail);
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

        appbar.setTitle(getString(R.string.apply_spring_ring_inquiry) + getString(R.string
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
        mDisposable.add(DatabaseHelper.getInstance(this).getSpringRingInquiryByTrip(tripId)
                .observeOn
                        (AndroidSchedulers.mainThread()).subscribe(new Consumer<SpringRingInquiryWithConfig>() {
                    @Override
                    public void accept(SpringRingInquiryWithConfig result) throws Exception {
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
                                            SpringRingInquiryCreateActivity
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
                        Toast.makeText(SpringRingInquiryDetailActivity.this, throwable
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
                    ErrorHandler.getInstance().setException(SpringRingInquiryDetailActivity.this,
                            e);
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                ErrorHandler.getInstance().setException(SpringRingInquiryDetailActivity.this, t);
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
                                        (SpringRingInquiryDetailActivity.this)
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
                                                        (SpringRingInquiryDetailActivity
                                                                        .this,
                                                                e.getMessage(),
                                                                Toast.LENGTH_LONG)
                                                        .show();
                                            }
                                        });
                            } else {
                                Toast.makeText
                                        (SpringRingInquiryDetailActivity.this,
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
                            (SpringRingInquiryDetailActivity.this, e);
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                Toast.makeText(SpringRingInquiryDetailActivity.this, t
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
        if (!TextUtils.isEmpty(inquiry.getInquiry().getName())) {
            Logger.e(TAG, "Project Name:" + inquiry.getInquiry().getName());
            tvInquiryName.setText(inquiry.getInquiry().getName());
        } else {
            Logger.e(TAG, "Project Name is empty");
        }
        if (!TextUtils.isEmpty(inquiry.getInquiry().getApply()))
            tvApply.setText(inquiry.getInquiry().getApply());
        if (!TextUtils.isEmpty(inquiry.getInquiry().getYear_potential_id()))
            mDisposable.add(DatabaseHelper.getInstance(this).getConfigYearPotentialById(inquiry
                    .getInquiry().getYear_potential_id()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<ConfigYearPotential>() {

                        @Override
                        public void accept(ConfigYearPotential config) throws Exception {
                            tvPotential.setText(config.getName());
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            ErrorHandler.getInstance().setException
                                    (SpringRingInquiryDetailActivity.this, throwable);
                        }
                    }));
        if (inquiry.getInquiry().getNeed_image() != Dropdown.VALUE_EMPTY)
            tvNeedImage.setText(have[inquiry.getInquiry().getNeed_image()]);
        if (inquiry.getInquiry().getNeed_parity() != Dropdown.VALUE_EMPTY)
            tvNeedParity.setText(have[inquiry.getInquiry().getNeed_parity()]);
        if (!TextUtils.isEmpty(inquiry.getInquiry().getProduct_type_id()))
            mDisposable.add(DatabaseHelper.getInstance(this).getConfigInquiryProductTypeById(inquiry
                    .getInquiry().getProduct_type_id()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<ConfigInquiryProductType>() {

                        @Override
                        public void accept(ConfigInquiryProductType config) throws Exception {
                            tvProductType.setText(config.getName());
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            ErrorHandler.getInstance().setException
                                    (SpringRingInquiryDetailActivity.this, throwable);
                        }
                    }));
        if (!TextUtils.isEmpty(inquiry.getInquiry().getCompetitive_status_id()))
            mDisposable.add(DatabaseHelper.getInstance(this).getConfigCompetitiveStatusById(inquiry
                    .getInquiry().getCompetitive_status_id()).observeOn(AndroidSchedulers
                    .mainThread())
                    .subscribe(new Consumer<ConfigCompetitiveStatus>() {

                        @Override
                        public void accept(ConfigCompetitiveStatus config) throws Exception {
                            tvCompetitive.setText(config.getName());
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            ErrorHandler.getInstance().setException
                                    (SpringRingInquiryDetailActivity.this, throwable);
                        }
                    }));
        if (!TextUtils.isEmpty(inquiry.getInquiry().getSgb_advantage_id()))
            mDisposable.add(DatabaseHelper.getInstance(this).getConfigSgbAdvantageById(inquiry
                    .getInquiry().getSgb_advantage_id()).observeOn(AndroidSchedulers
                    .mainThread())
                    .subscribe(new Consumer<ConfigSgbAdvantage>() {

                        @Override
                        public void accept(ConfigSgbAdvantage config) throws Exception {
                            tvAdvantage.setText(config.getName());
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            ErrorHandler.getInstance().setException
                                    (SpringRingInquiryDetailActivity.this, throwable);
                        }
                    }));
        if (!TextUtils.isEmpty(inquiry.getInquiry().getSgb_disadvantage_id()))
            mDisposable.add(DatabaseHelper.getInstance(this).getConfigSgbDisadvantageById(inquiry
                    .getInquiry().getSgb_disadvantage_id()).observeOn(AndroidSchedulers
                    .mainThread())
                    .subscribe(new Consumer<ConfigSgbDisadvantage>() {

                        @Override
                        public void accept(ConfigSgbDisadvantage config) throws Exception {
                            tvDisadvantage.setText(config.getName());
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            ErrorHandler.getInstance().setException
                                    (SpringRingInquiryDetailActivity.this, throwable);
                        }
                    }));
        if (!TextUtils.isEmpty(inquiry.getInquiry().getTrend_id()))
            mDisposable.add(DatabaseHelper.getInstance(this).getConfigTrendById(inquiry
                    .getInquiry().getTrend_id()).observeOn(AndroidSchedulers
                    .mainThread())
                    .subscribe(new Consumer<ConfigTrend>() {

                        @Override
                        public void accept(ConfigTrend config) throws Exception {
                            tvTrend.setText(config.getName());
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            ErrorHandler.getInstance().setException
                                    (SpringRingInquiryDetailActivity.this, throwable);
                        }
                    }));
        if (!TextUtils.isEmpty(inquiry.getInquiry().getSealed_state()))
            mDisposable.add(DatabaseHelper.getInstance(this).getConfigSealedStateById(inquiry
                    .getInquiry().getSealed_state()).observeOn(AndroidSchedulers
                    .mainThread())
                    .subscribe(new Consumer<ConfigSealedState>() {

                        @Override
                        public void accept(ConfigSealedState config) throws Exception {
                            tvSealed.setText(config.getName());
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            ErrorHandler.getInstance().setException
                                    (SpringRingInquiryDetailActivity.this, throwable);
                        }
                    }));
        if (!TextUtils.isEmpty(inquiry.getInquiry().getSpeed()))
            tvSpeed.setText(inquiry.getInquiry().getSpeed());
        if (!TextUtils.isEmpty(inquiry.getInquiry().getPressure()))
            tvPressure.setText(inquiry.getInquiry().getPressure());
        if (!TextUtils.isEmpty(inquiry.getInquiry().getTemperature()))
            tvTemperature.setText(inquiry.getInquiry().getTemperature());
        if (!TextUtils.isEmpty(inquiry.getInquiry().getLifetime()))
            tvLifetime.setText(inquiry.getInquiry().getLifetime());
        if (!TextUtils.isEmpty(inquiry.getInquiry().getStatic_a_material()))
            tvStaticMaterial.setText(inquiry.getInquiry().getStatic_a_material());
        if (!TextUtils.isEmpty(inquiry.getInquiry().getStatic_a_hardness()))
            tvStaticHardness.setText(inquiry.getInquiry().getStatic_a_hardness());
        if (!TextUtils.isEmpty(inquiry.getInquiry().getStatic_a_smoothness()))
            tvStaticSmoothness.setText(inquiry.getInquiry().getStatic_a_smoothness());
        if (!TextUtils.isEmpty(inquiry.getInquiry().getDynamic_b_material()))
            tvDynamicMaterial.setText(inquiry.getInquiry().getDynamic_b_material());
        if (!TextUtils.isEmpty(inquiry.getInquiry().getDynamic_b_hardness()))
            tvDynamicHardness.setText(inquiry.getInquiry().getDynamic_b_hardness());
        if (!TextUtils.isEmpty(inquiry.getInquiry().getDynamic_b_smoothness()))
            tvDynamicSmoothness.setText(inquiry.getInquiry().getDynamic_b_smoothness());

        if (inquiry.getProducts().size() > 0) {
            containerItem.removeAllViews();
            for (SpringRingInquiryProduct product : inquiry.getProducts()) {
                generateItem(product);
            }
        }
        if (!TextUtils.isEmpty(inquiry.getInquiry().getOther()))
            tvOther.setText(inquiry.getInquiry().getOther());
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
                ErrorHandler.getInstance().setException(SpringRingInquiryDetailActivity.this,
                        throwable);
            }
        }));
    }

    private void generateItem(final SpringRingInquiryProduct product) {
        View view = getLayoutInflater().inflate(R.layout.item_quotation_detail, containerItem,
                false);
        TextView tvTitle = view.findViewById(R.id.textView_title);
        tvTitle.setText(product.getPart_number());
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(),
                        SpringRingInquiryProductCreateActivity.class);
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
                        ErrorHandler.getInstance().setException(SpringRingInquiryDetailActivity
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
                ErrorHandler.getInstance().setException(SpringRingInquiryDetailActivity.this,
                        throwable);
            }
        }));
    }

}
