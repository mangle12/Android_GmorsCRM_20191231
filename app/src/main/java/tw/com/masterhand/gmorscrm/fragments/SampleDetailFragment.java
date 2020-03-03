package tw.com.masterhand.gmorscrm.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tw.com.masterhand.gmorscrm.ApprovalListActivity;
import tw.com.masterhand.gmorscrm.ContacterListActivity;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.TokenManager;
import tw.com.masterhand.gmorscrm.activity.news.SaleResourceActivity;
import tw.com.masterhand.gmorscrm.activity.task.ConversationActivity;
import tw.com.masterhand.gmorscrm.activity.task.SampleProductCreateActivity;
import tw.com.masterhand.gmorscrm.api.ApiHelper;
import tw.com.masterhand.gmorscrm.model.Conversation;
import tw.com.masterhand.gmorscrm.model.ProjectWithConfig;
import tw.com.masterhand.gmorscrm.model.SampleProductWithConfig;
import tw.com.masterhand.gmorscrm.model.SampleWithConfig;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.record.ContactPerson;
import tw.com.masterhand.gmorscrm.room.record.Customer;
import tw.com.masterhand.gmorscrm.room.record.File;
import tw.com.masterhand.gmorscrm.room.setting.Admin;
import tw.com.masterhand.gmorscrm.room.setting.ConfigCompanyHiddenField;
import tw.com.masterhand.gmorscrm.room.setting.ConfigSampleAmountRange;
import tw.com.masterhand.gmorscrm.room.setting.ConfigSampleImportMethod;
import tw.com.masterhand.gmorscrm.room.setting.ConfigSamplePaymentMethod;
import tw.com.masterhand.gmorscrm.room.setting.ConfigSampleReason;
import tw.com.masterhand.gmorscrm.room.setting.ConfigSampleSource;
import tw.com.masterhand.gmorscrm.room.setting.User;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.tools.TimeFormater;
import tw.com.masterhand.gmorscrm.view.CompanyCard;
import tw.com.masterhand.gmorscrm.view.ItemConversation;
import tw.com.masterhand.gmorscrm.view.ItemProject;

public class SampleDetailFragment extends BaseFragment {
    @BindView(R.id.companyCard)
    protected CompanyCard companyCard;
    @BindView(R.id.itemProject)
    protected ItemProject itemProject;
    @BindView(R.id.tvTitle)
    protected TextView tvTitle;
    @BindView(R.id.tvStartDate)
    protected TextView tvStartDate;
    @BindView(R.id.tvDeliveryDate)
    protected TextView tvDeliveryDate;
    @BindView(R.id.tvPrice)
    protected TextView tvPrice;
    @BindView(R.id.tvReason)
    protected TextView tvReason;
    @BindView(R.id.tvSource)
    protected TextView tvSource;
    @BindView(R.id.tvImport)
    protected TextView tvImport;
    @BindView(R.id.tvDelivery)
    protected TextView tvDelivery;
    @BindView(R.id.tvIntro)
    protected TextView tvIntro;
    @BindView(R.id.tvPeriod)
    protected TextView tvPeriod;
    @BindView(R.id.tvBasis)
    protected TextView tvBasis;
    @BindView(R.id.tvReceiver)
    protected TextView tvReceiver;
    @BindView(R.id.tvAssistant)
    protected TextView tvAssistant;
    @BindView(R.id.tvBuilder)
    protected TextView tvBuilder;
    @BindView(R.id.tvApprovalDescription)
    TextView tvApprovalDescription;
    @BindView(R.id.tvAddress)
    protected TextView tvAddress;
    @BindView(R.id.tvPhone)
    protected TextView tvPhone;
    @BindView(R.id.tvNote)
    protected TextView tvNote;
    @BindView(R.id.containerItem)
    protected LinearLayout containerItem;
    @BindView(R.id.containerPrice)
    protected LinearLayout containerPrice;
    @BindView(R.id.containerImport)
    protected LinearLayout containerImport;
    @BindView(R.id.containerDelivery)
    protected LinearLayout containerDelivery;
    @BindView(R.id.containerReceiver)
    protected LinearLayout containerReceiver;
    @BindView(R.id.containerAddress)
    protected LinearLayout containerAddress;
    @BindView(R.id.containerPhone)
    protected LinearLayout containerPhone;
    @BindView(R.id.containerPeriod)
    protected LinearLayout containerPeriod;
    @BindView(R.id.containerBasis)
    protected LinearLayout containerBasis;
    @BindView(R.id.containerStartDate)
    protected LinearLayout containerStartDate;

    @BindView(R.id.btnContacter)
    protected Button btnContacter;
    @BindView(R.id.btnApproval)
    protected Button btnApproval;
    @BindView(R.id.container_photo)
    protected LinearLayout containerPhoto;
    @BindView(R.id.container_conversation)
    protected LinearLayout containerConversation;

    protected SampleWithConfig sample;
    protected String viewerId;

    public static SampleDetailFragment newInstance(SampleWithConfig sample, String viewerId) {
        SampleDetailFragment f = new SampleDetailFragment();
        Bundle args = new Bundle();
        Gson gson = new GsonBuilder().setDateFormat(TimeFormater.DATABASE_DATE_TIME_STRING)
                .create();
        args.putString(MyApplication.INTENT_KEY_SAMPLE, gson.toJson(sample));
        args.putString(MyApplication.INTENT_KEY_VIEWER, viewerId);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sample_detail, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    public SampleWithConfig getSample() {
        return sample;
    }

    private void init() {
        String sampleString = getArguments().getString(MyApplication.INTENT_KEY_SAMPLE);
        viewerId = getArguments().getString(MyApplication.INTENT_KEY_VIEWER);
        Logger.e(TAG, "viewerId:" + viewerId);
        if (TextUtils.isEmpty(sampleString)) {
            getActivity().finish();
            return;
        }
        getHiddenField();
        sample = gson.fromJson(sampleString, SampleWithConfig.class);
        updateDetail();
        updatePhoto(sample.getFiles());
        updateConversation();
        getCustomer();
        getProject();
    }

    void getHiddenField() {
        mDisposable.add(DatabaseHelper.getInstance(getActivity()).getConfigCompanyHiddenField
                (TokenManager
                        .getInstance().getUser().getCompany_id()).observeOn
                (AndroidSchedulers.mainThread()).subscribe(new Consumer<ConfigCompanyHiddenField>
                () {

            @Override
            public void accept(ConfigCompanyHiddenField configCompanyHiddenField) throws
                    Exception {
                if (configCompanyHiddenField.getSample_hidden_import_method_id()) {
                    containerImport.setVisibility(View.GONE);
                }
                if (configCompanyHiddenField.getSample_hidden_payment_method_id()) {
                    containerDelivery.setVisibility(View.GONE);
                }
                if (configCompanyHiddenField.getSample_hidden_receiver()) {
                    containerReceiver.setVisibility(View.GONE);
                }
                if (configCompanyHiddenField.getSample_hidden_address()) {
                    containerAddress.setVisibility(View.GONE);
                }
                if (configCompanyHiddenField.getSample_hidden_tel()) {
                    containerPhone.setVisibility(View.GONE);
                }
                if (configCompanyHiddenField.getSample_hidden_amount_range_id()) {
                    containerPrice.setVisibility(View.GONE);
                }
                if (configCompanyHiddenField.getSample_hidden_test_cycle()) {
                    containerPeriod.setVisibility(View.GONE);
                }
                if (configCompanyHiddenField.getSample_hidden_test_evaluation_basis()) {
                    containerBasis.setVisibility(View.GONE);
                }
                if (configCompanyHiddenField.getSample_hidden_test_from_date()) {
                    containerStartDate.setVisibility(View.GONE);
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                ErrorHandler.getInstance().setException(getActivity(),
                        throwable);
            }
        }));
    }

    @OnClick(R.id.btnApproval)
    void showApproval() {
        Intent intent = new Intent(getActivity(), ApprovalListActivity.class);
        intent.putExtra(MyApplication.INTENT_KEY_TRIP, sample.getSample().getTrip_id());
        startActivity(intent);
    }

    @OnClick(R.id.btnResource)
    void showResource() {
        Intent intent = new Intent(getActivity(), SaleResourceActivity.class);
        intent.putExtra(MyApplication.INTENT_KEY_ID, sample.getSample().getTrip_id());
        startActivity(intent);
    }

    void updatePhoto(List<File> files) {
        containerPhoto.removeAllViews();
        for (File file : files) {
            containerPhoto.addView(generatePhotoItem(file));
        }
        if (containerPhoto.getChildCount() == 0)
            containerPhoto.addView(getEmptyTextView(null));
    }

    private void updateDetail() {
        tvTitle.setText(sample.getTrip().getName());
        if (sample.getSample().getTest_from_date() != null)
            tvStartDate.setText(TimeFormater.getInstance().toDateFormat(sample.getSample()
                    .getTest_from_date()));
        if (sample.getSample().getShipping_date() != null)
            tvDeliveryDate.setText(TimeFormater.getInstance().toDateFormat(sample.getSample()
                    .getShipping_date
                            ()));
        mDisposable.add(DatabaseHelper.getInstance(getActivity()).getConfigSampleAmountRangeById
                (sample.getSample().getAmount_range_id()).observeOn(AndroidSchedulers.mainThread
                ()).subscribe(new Consumer<ConfigSampleAmountRange>() {

            @Override
            public void accept(ConfigSampleAmountRange config) throws Exception {
                tvPrice.setText(config.getName());
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                tvPrice.setText(R.string.empty_show);
            }
        }));
        mDisposable.add(DatabaseHelper.getInstance(getActivity()).getConfigSampleReasonById
                (sample.getSample().getReason_id())
                .observeOn(AndroidSchedulers.mainThread
                        ()).subscribe(new Consumer<ConfigSampleReason>() {

                    @Override
                    public void accept(ConfigSampleReason config) throws Exception {
                        tvReason.setText(config.getName());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        tvReason.setText(R.string.empty_show);
                    }
                }));
        mDisposable.add(DatabaseHelper.getInstance(getActivity()).getConfigSampleSourceById
                (sample.getSample().getSource_id())
                .observeOn(AndroidSchedulers.mainThread
                        ()).subscribe(new Consumer<ConfigSampleSource>() {
                    @Override
                    public void accept(ConfigSampleSource config) throws Exception {
                        tvSource.setText(config.getName());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        tvSource.setText(R.string.empty_show);
                    }
                }));
        mDisposable.add(DatabaseHelper.getInstance(getActivity()).getConfigSampleImportMethodById
                (sample.getSample().getImport_method_id())
                .observeOn(AndroidSchedulers.mainThread
                        ()).subscribe(new Consumer<ConfigSampleImportMethod>() {

                    @Override
                    public void accept(ConfigSampleImportMethod config) throws Exception {
                        tvImport.setText(config.getName());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        tvImport.setText(R.string.empty_show);
                    }
                }));
        mDisposable.add(DatabaseHelper.getInstance(getActivity())
                .getConfigSamplePaymentMethodById(sample.getSample().getPayment_method_id())
                .observeOn(AndroidSchedulers.mainThread
                        ()).subscribe(new Consumer<ConfigSamplePaymentMethod>() {

                    @Override
                    public void accept(ConfigSamplePaymentMethod config) throws Exception {
                        tvDelivery.setText(config.getName());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        tvDelivery.setText(R.string.empty_show);
                    }
                }));
        mDisposable.add(DatabaseHelper.getInstance(getActivity())
                .getAdminById(sample.getTrip().getAssistant_id())
                .observeOn(AndroidSchedulers.mainThread
                        ()).subscribe(new Consumer<Admin>() {

                    @Override
                    public void accept(Admin config) throws Exception {
                        tvAssistant.setText(config.getName());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ErrorHandler.getInstance().setException(getActivity(), throwable);
                        tvAssistant.setText(R.string.empty_show);
                    }
                }));
        tvIntro.setText(sample.getSample().getBrief());
        if (!TextUtils.isEmpty(sample.getSample().getTest_cycle()))
            tvPeriod.setText(sample.getSample().getTest_cycle());
        if (!TextUtils.isEmpty(sample.getSample().getTest_evaluation_basis()))
            tvBasis.setText(sample.getSample().getTest_evaluation_basis());
        if (!TextUtils.isEmpty(sample.getSample().getReceiver()))
            mDisposable.add(DatabaseHelper.getInstance(getActivity()).getContactPersonById(sample
                    .getSample().getReceiver()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<ContactPerson>() {

                        @Override
                        public void accept(ContactPerson contactPerson) throws Exception {
                            tvReceiver.setText(contactPerson.getShowName());
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            ErrorHandler.getInstance().setException(getActivity(),
                                    throwable);
                        }
                    }));
        tvAddress.setText(sample.getSample().getAddress());
        tvPhone.setText(sample.getSample().getTel());
        tvNote.setText(sample.getTrip().getDescription());

        if (sample.getProducts().size() > 0) {
            containerItem.removeAllViews();
            for (SampleProductWithConfig product : sample.getProducts()) {
                generateItem(product);
            }
        }

        if (sample.getContacters().size() > 0) {
            btnContacter.setText(getString(R.string.contact) + " " + sample
                    .getContacters()
                    .size());
            btnContacter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(),
                            ContacterListActivity.class);
                    intent.putExtra(MyApplication.INTENT_KEY_PARENT, sample
                            .getSample().getId());
                    startActivity(intent);
                }
            });
        } else {
            btnContacter.setVisibility(View.GONE);
        }
        ApiHelper.getInstance().getTripApi().getApprovalDescription(TokenManager.getInstance()
                .getToken(), sample.getTrip().getId()).enqueue(new Callback<JSONObject>() {

            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                try {
                    JSONObject result = response.body();
                    int success = result.getInt("success");
                    if (success == 1) {
                        String description = result.getString("description");
                        tvApprovalDescription.setText(description);
                    }
                } catch (Exception e) {
                    ErrorHandler.getInstance().setException(getActivity(), e);
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                ErrorHandler.getInstance().setException(getActivity(), t);
            }
        });
        if (sample.getTrip().getUser_id() != null)
            mDisposable.add(DatabaseHelper.getInstance(getActivity()).getUserById(sample.getTrip()
                    .getUser_id()).observeOn
                    (AndroidSchedulers.mainThread()).subscribe(new Consumer<User>() {

                @Override
                public void accept(User user) throws Exception {
                    tvBuilder.setText(user.getShowName());
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    ErrorHandler.getInstance().setException(getActivity(), throwable);
                }
            }));
    }

    private void generateItem(final SampleProductWithConfig product) {
        View view = getLayoutInflater().inflate(R.layout.item_quotation_detail, containerItem,
                false);
        TextView tvTitle = view.findViewById( R.id.textView_title);
        tvTitle.setText(product.getProduct().getProduct_model());
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(),
                        SampleProductCreateActivity.class);
                intent.putExtra(MyApplication.INTENT_KEY_PRODUCT, gson.toJson
                        (product));
                intent.putExtra(MyApplication.INTENT_KEY_ENABLE, false);
                startActivityForResult(intent, MyApplication.REQUEST_EDIT_PRODUCT);
            }
        });
        containerItem.addView(view, new LinearLayout.LayoutParams(LinearLayout.LayoutParams
                .MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.list_size)));
    }

    private void getCustomer() {
        mDisposable.add(DatabaseHelper.getInstance(getActivity()).getCustomerById(sample.getTrip()
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
                        ErrorHandler.getInstance().setException(getActivity(), throwable);
                    }
                }));
    }

    private void getProject() {
        mDisposable.add(DatabaseHelper.getInstance(getActivity()).getProjectById(sample.getTrip()
                .getProject_id(), TokenManager.getInstance()
                .getUser().getDepartment_id(), TokenManager.getInstance().getUser().getCompany_id
                ()).observeOn
                (AndroidSchedulers.mainThread()).subscribe(new Consumer<ProjectWithConfig>() {

            @Override
            public void accept(ProjectWithConfig projectWithConfig) throws
                    Exception {
                itemProject.setProject(projectWithConfig);
                itemProject.setTripId(sample.getTrip().getId());
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                ErrorHandler.getInstance().setException(getActivity(),
                        throwable);
            }
        }));
    }

    /**
     * 更新對話
     */
    protected void updateConversation() {
        containerConversation.removeAllViews();
        if (sample == null) {
            Logger.e(TAG, "sample is null");
            return;
        }
        mDisposable.add(
                DatabaseHelper.getInstance(getActivity()).getConversationByParent(sample.getSample()
                        .getId())
                        .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Conversation>() {
                    @Override
                    public void accept(Conversation conversation) throws Exception {
                        ItemConversation itemConversation = new ItemConversation
                                (getActivity());
                        itemConversation.setConversation(conversation);
                        itemConversation.getMainMessage().showIndex(false);
                        containerConversation.addView(itemConversation);
                    }
                }));
    }


    /**
     * 新增討論
     */
    @OnClick(R.id.imageButton_add)
    void addConversation() {
        if (sample == null) {
            Logger.e(TAG, "sample is null");
            return;
        }
        Intent intent = new Intent(getActivity(), ConversationActivity.class);
        intent.putExtra(MyApplication.INTENT_KEY_TRIP, sample.getSample().getTrip_id());
        intent.putExtra(MyApplication.INTENT_KEY_ID, sample.getSample().getId());
        startActivityForResult(intent, MyApplication.REQUEST_ADD_CONVERSATION);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case MyApplication.REQUEST_ADD_CONVERSATION:
                    updateConversation();
                    break;
                default:
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
