package tw.com.masterhand.gmorscrm.activity.task;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import tw.com.masterhand.gmorscrm.ContacterListActivity;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.TokenManager;
import tw.com.masterhand.gmorscrm.activity.news.SaleResourceActivity;
import tw.com.masterhand.gmorscrm.api.ApiHelper;
import tw.com.masterhand.gmorscrm.enums.ApprovalStatus;
import tw.com.masterhand.gmorscrm.enums.SubmitStatus;
import tw.com.masterhand.gmorscrm.model.Conversation;
import tw.com.masterhand.gmorscrm.model.MultipleSubmit;
import tw.com.masterhand.gmorscrm.model.ProjectWithConfig;
import tw.com.masterhand.gmorscrm.model.QuotationWithConfig;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.record.Customer;
import tw.com.masterhand.gmorscrm.room.record.File;
import tw.com.masterhand.gmorscrm.room.record.QuotationProduct;
import tw.com.masterhand.gmorscrm.room.setting.Admin;
import tw.com.masterhand.gmorscrm.room.setting.ConfigCompanyHiddenField;
import tw.com.masterhand.gmorscrm.room.setting.ConfigTax;
import tw.com.masterhand.gmorscrm.room.setting.User;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.tools.NumberFormater;
import tw.com.masterhand.gmorscrm.view.Appbar;
import tw.com.masterhand.gmorscrm.view.CompanyCard;
import tw.com.masterhand.gmorscrm.view.Dropdown;
import tw.com.masterhand.gmorscrm.view.ItemApproval;
import tw.com.masterhand.gmorscrm.view.ItemConversation;
import tw.com.masterhand.gmorscrm.view.ItemProject;

public class QuotationDetailActivity extends BaseUserCheckActivity {
    @BindView(R.id.appbar)
    Appbar appbar;
    @BindView(R.id.companyCard)
    protected CompanyCard companyCard;
    @BindView(R.id.itemProject)
    protected ItemProject itemProject;
    @BindView(R.id.tvTitle)
    protected TextView tvTitle;
    @BindView(R.id.tvPayment)
    protected TextView tvPayment;
    @BindView(R.id.tvUnitKeep)
    protected TextView tvUnitKeep;
    @BindView(R.id.tvTax)
    protected TextView tvTax;
    @BindView(R.id.tvTotal)
    protected TextView tvTotal;
    @BindView(R.id.tvNote)
    protected TextView tvNote;
    @BindView(R.id.tvAssistant)
    protected TextView tvAssistant;
    @BindView(R.id.tvBuilder)
    protected TextView tvBuilder;
    @BindView(R.id.tvApprovalDescription)
    protected TextView tvApprovalDescription;
    @BindView(R.id.btnContacter)
    protected Button btnContacter;
    @BindView(R.id.btnApprover)
    protected Button btnApprover;
    @BindView(R.id.containerUnitKeep)
    protected LinearLayout containerUnitKeep;
    @BindView(R.id.containerItem)
    protected LinearLayout containerItem;
    @BindView(R.id.container_photo)
    protected LinearLayout containerPhoto;
    @BindView(R.id.container_conversation)
    protected LinearLayout containerConversation;
    @BindView(R.id.containerReceiver)
    protected LinearLayout containerReceiver;
    @BindView(R.id.containerDeliveryDate)
    protected LinearLayout containerDeliveryDate;
    @BindView(R.id.containerTransfer)
    protected RelativeLayout containerTransfer;
    @BindView(R.id.itemApproval)
    protected ItemApproval itemApproval;

    protected QuotationWithConfig quotation;
    protected String viewerId;
    protected String tripId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quotation_detail);
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
        containerReceiver.setVisibility(View.GONE);
        containerDeliveryDate.setVisibility(View.GONE);

        appbar.setTitle(getString(R.string.apply_quotation) + getString(R.string
                .title_activity_work_detail));

        itemProject.hideIndex();
        itemProject.hideHistory();
        getHiddenField();
        tripId = getIntent().getStringExtra(MyApplication.INTENT_KEY_TRIP);
        if (TextUtils.isEmpty(tripId)) {
            finish();
        }
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
                if (configCompanyHiddenField.getQuotation_hidden_unit_type()) {
                    containerUnitKeep.setVisibility(View.GONE);
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                ErrorHandler.getInstance().setException(QuotationDetailActivity.this,
                        throwable);
            }
        }));
    }

    protected void getData() {
        if (TextUtils.isEmpty(tripId))
            return;
        Logger.i(TAG, "tripId:" + tripId);
        mDisposable.add(DatabaseHelper.getInstance(this).getQuotationByTrip(tripId).observeOn
                (AndroidSchedulers.mainThread()).subscribe(new Consumer<QuotationWithConfig>() {
            @Override
            public void accept(QuotationWithConfig result) throws Exception {
                quotation = result;
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
                            Intent intent = new Intent(view.getContext(), QuotationCreateActivity
                                    .class);
                            intent.putExtra(MyApplication.INTENT_KEY_TRIP, quotation.getQuotation()
                                    .getTrip_id());
                            startActivity(intent);
                        }
                    });
                }
                updateDetail();
                updatePhoto(quotation.getFiles());
                updateConversation();
                getCustomer();
                getProject();
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Toast.makeText(QuotationDetailActivity.this, throwable.getMessage(), Toast
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
                    ErrorHandler.getInstance().setException(QuotationDetailActivity.this, e);
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                ErrorHandler.getInstance().setException(QuotationDetailActivity.this, t);
            }
        });
    }

    protected void submit() {
        final MultipleSubmit submit = new MultipleSubmit();
        submit.setTrip_ids(quotation.getQuotation().getTrip_id());
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
                                        (QuotationDetailActivity.this)
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
                                                        (QuotationDetailActivity
                                                                        .this,
                                                                e.getMessage(),
                                                                Toast.LENGTH_LONG)
                                                        .show();
                                            }
                                        });
                            } else {
                                Toast.makeText(QuotationDetailActivity.this,
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
                            (QuotationDetailActivity.this, e);
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                Toast.makeText(QuotationDetailActivity.this, t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    @OnClick(R.id.btnApprover)
    void showApproval() {
        Intent intent = new Intent(this, ApprovalListActivity.class);
        intent.putExtra(MyApplication.INTENT_KEY_TRIP, quotation.getQuotation().getTrip_id());
        startActivity(intent);
    }

    @OnClick(R.id.btnResource)
    void showResource() {
        Intent intent = new Intent(this, SaleResourceActivity.class);
        intent.putExtra(MyApplication.INTENT_KEY_ID, quotation.getQuotation().getTrip_id());
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
        tvTitle.setText(quotation.getTrip().getName());
        tvPayment.setText(quotation.getQuotation().getPayment_method());
        if (quotation.getQuotation().getUnit_type() != Dropdown.VALUE_EMPTY)
            tvUnitKeep.setText(getResources().getStringArray(R.array.unit_type)[quotation
                    .getQuotation().getUnit_type()]);
        if (!TextUtils.isEmpty(quotation.getQuotation().getTax())) {
            mDisposable.add(DatabaseHelper.getInstance(this).getConfigTaxById(quotation
                    .getQuotation().getTax())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<ConfigTax>() {
                        @Override
                        public void accept(ConfigTax configTax) throws Exception {
                            tvTax.setText(configTax.getName());
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            ErrorHandler.getInstance().setException(QuotationDetailActivity.this,
                                    throwable);
                        }
                    }));
        }
        tvTotal.setText(NumberFormater.getMoneyFormat(quotation.getQuotation().getAmount()) +
                getString(R.string.money_unit));
        if (quotation.getProducts().size() > 0) {
            containerItem.removeAllViews();
            for (QuotationProduct product : quotation.getProducts()) {
                generateItem(product);
            }
        }
        if (quotation.getContacters().size() > 0) {
            btnContacter.setText(getString(R.string.contact) + " " + quotation
                    .getContacters()
                    .size());
            btnContacter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(),
                            ContacterListActivity.class);
                    intent.putExtra(MyApplication.INTENT_KEY_PARENT, quotation
                            .getQuotation().getId());
                    startActivity(intent);
                }
            });
        } else {
            btnContacter.setVisibility(View.GONE);
        }
        mDisposable.add(DatabaseHelper.getInstance(this)
                .getAdminById(quotation.getTrip().getAssistant_id())
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
        if (!TextUtils.isEmpty(quotation.getTrip().getDescription()))
            tvNote.setText(quotation.getTrip().getDescription());
        mDisposable.add(DatabaseHelper.getInstance(this).getUserById(quotation.getTrip().getUser_id
                ()).observeOn
                (AndroidSchedulers.mainThread()).subscribe(new Consumer<User>() {

            @Override
            public void accept(User user) throws Exception {
                tvBuilder.setText(user.getShowName());
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                ErrorHandler.getInstance().setException(QuotationDetailActivity.this, throwable);
            }
        }));
    }

    private void generateItem(final QuotationProduct product) {
        View view = getLayoutInflater().inflate(R.layout.item_quotation_detail, containerItem,
                false);
        TextView tvTitle = view.findViewById(R.id.textView_title);
        tvTitle.setText(product.getProduct_model());
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(),
                        QuotationProductCreateActivity.class);
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
        mDisposable.add(DatabaseHelper.getInstance(this).getCustomerById(quotation.getTrip()
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
                        ErrorHandler.getInstance().setException(QuotationDetailActivity.this,
                                throwable);
                    }
                }));
    }

    public void getProject() {
        mDisposable.add(DatabaseHelper.getInstance(this).getProjectById(quotation.getTrip()
                .getProject_id(), TokenManager.getInstance()
                .getUser().getDepartment_id(), TokenManager.getInstance().getUser().getCompany_id
                ()).observeOn
                (AndroidSchedulers.mainThread()).subscribe(new Consumer<ProjectWithConfig>() {

            @Override
            public void accept(ProjectWithConfig projectWithConfig) throws
                    Exception {
                itemProject.setProject(projectWithConfig);
                itemProject.setTripId(quotation.getTrip().getId());
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                ErrorHandler.getInstance().setException(QuotationDetailActivity.this,
                        throwable);
            }
        }));
    }

    /**
     * 更新對話
     */
    protected void updateConversation() {
        containerConversation.removeAllViews();
        if (quotation == null) {
            Logger.e(TAG, "quotation is null");
            return;
        }
        mDisposable.add(
                DatabaseHelper.getInstance(this).getConversationByParent(quotation.getQuotation()
                        .getId())
                        .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Conversation>() {
                    @Override
                    public void accept(Conversation conversation) throws Exception {
                        ItemConversation itemConversation = new ItemConversation
                                (QuotationDetailActivity
                                        .this);
                        itemConversation.setConversation(conversation);
                        itemConversation.getMainMessage().showIndex(false);
                        containerConversation.addView(itemConversation);
                    }
                }));
    }

    @OnClick(R.id.btnTransfer)
    void transferToContract() {
        Intent intent = new Intent(this, ContractCreateActivity.class);
        intent.putExtra(MyApplication.INTENT_KEY_QUOTATION, quotation.getQuotation().getId());
        startActivityForResult(intent, MyApplication.REQUEST_QUOTATION_TO_CONTRACT);
    }

    /**
     * 新增討論
     */
    @OnClick(R.id.imageButton_add)
    void addConversation() {
        if (quotation == null) {
            Logger.e(TAG, "quotation is null");
            return;
        }
        Intent intent = new Intent(this, ConversationActivity.class);
        intent.putExtra(MyApplication.INTENT_KEY_TRIP, quotation.getQuotation().getTrip_id());
        intent.putExtra(MyApplication.INTENT_KEY_ID, quotation.getQuotation().getId());
        startActivityForResult(intent, MyApplication.REQUEST_ADD_CONVERSATION);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case MyApplication.REQUEST_ADD_CONVERSATION:
                    updateConversation();
                    break;
                case MyApplication.REQUEST_QUOTATION_TO_CONTRACT:
                    finish();
                    break;
                default:

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
