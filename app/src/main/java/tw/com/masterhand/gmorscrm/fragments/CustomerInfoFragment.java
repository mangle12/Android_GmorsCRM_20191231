package tw.com.masterhand.gmorscrm.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.reactivestreams.Subscription;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.FlowableSubscriber;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.TokenManager;
import tw.com.masterhand.gmorscrm.activity.customer.ContactPersonCreateActivity;
import tw.com.masterhand.gmorscrm.model.Phone;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.record.ContactPerson;
import tw.com.masterhand.gmorscrm.room.record.Customer;
import tw.com.masterhand.gmorscrm.room.setting.ConfigCompanyHiddenField;
import tw.com.masterhand.gmorscrm.room.setting.ConfigCustomerLevel;
import tw.com.masterhand.gmorscrm.room.setting.ConfigCustomerSapGroup;
import tw.com.masterhand.gmorscrm.room.setting.ConfigCustomerSapType;
import tw.com.masterhand.gmorscrm.room.setting.ConfigCustomerSource;
import tw.com.masterhand.gmorscrm.room.setting.ConfigIndustry;
import tw.com.masterhand.gmorscrm.room.setting.User;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.tools.UnitChanger;
import tw.com.masterhand.gmorscrm.view.CompanyCard;
import tw.com.masterhand.gmorscrm.view.ContactCard;
import tw.com.masterhand.gmorscrm.view.HorizontalPageView;

public class CustomerInfoFragment extends BaseFragment {
    @BindView(R.id.companyCard)
    CompanyCard companyCard;
    @BindView(R.id.imageButton_add_contact)
    ImageButton btnAddContact;
    @BindView(R.id.textView_principal)
    TextView tvPrincipal;
    @BindView(R.id.textView_group)
    TextView tvGroup;
    @BindView(R.id.textView_industry)
    TextView tvIndustry;
    @BindView(R.id.textView_customer_type)
    TextView tvType;
    @BindView(R.id.textView_customer_level)
    TextView tvLevel;
    @BindView(R.id.textView_customer_team)
    TextView tvTeam;
    @BindView(R.id.textView_customer_source)
    TextView tvSource;
    @BindView(R.id.textView_product)
    TextView tvProduct;
    @BindView(R.id.textView_phone)
    TextView tvPhone;
    @BindView(R.id.textView_uniform_id)
    TextView tvUniformId;
    @BindView(R.id.textView_website)
    TextView tvWebsite;
    @BindView(R.id.textView_user)
    TextView tvUser;
    @BindView(R.id.textView_index)
    TextView tvIndex;
    @BindView(R.id.textView_total)
    TextView tvTotal;
    @BindView(R.id.tvImportant)
    TextView tvImportant;
    @BindView(R.id.container)
    LinearLayout container;
    @BindView(R.id.container_contact)
    LinearLayout containerContact;
    @BindView(R.id.containerGroup)
    LinearLayout containerGroup;
    @BindView(R.id.containerIndustry)
    LinearLayout containerIndustry;
    @BindView(R.id.containerCustomerType)
    LinearLayout containerCustomerType;
    @BindView(R.id.containerProduct)
    LinearLayout containerProduct;
    @BindView(R.id.scrollView_contact)
    HorizontalPageView svContact;


    private Customer customer;

    public static CustomerInfoFragment newInstance(String customerId) {
        CustomerInfoFragment f = new CustomerInfoFragment();
        Bundle args = new Bundle();
        args.putString("customer", customerId);
        f.setArguments(args);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_info, container, false);
        unbinder = ButterKnife.bind(this, view);
        init();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (customer != null)
            updateContactList();
    }

    private void init() {
        final String customerId = getArguments().getString("customer");

        if (customerId == null)
            getActivity().finish();

        getHiddenField();
        mDisposable.add(DatabaseHelper.getInstance(getActivity()).getCustomerById(customerId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Customer>() {
                    @Override
                    public void accept(Customer result) throws Exception {
                        customer = result;
                        updateCustomerData();
                        updateContactList();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable t) throws Exception {
                        Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }));
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
                if (configCompanyHiddenField.getCustomer_hidden_enterprises()) {
                    containerGroup.setVisibility(View.GONE);
                }
                if (configCompanyHiddenField.getCustomer_hidden_industry_id()) {
                    containerIndustry.setVisibility(View.GONE);
                }
                if (configCompanyHiddenField.getCustomer_hidden_sap_type_id()) {
                    containerCustomerType.setVisibility(View.GONE);
                }
                if (configCompanyHiddenField.getCustomer_hidden_product()) {
                    containerProduct.setVisibility(View.GONE);
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                ErrorHandler.getInstance().setException(getActivity(), throwable);
            }
        }));
    }

    void updateCustomerData() {
        if (customer.isType())
            tvImportant.setVisibility(View.VISIBLE);
        else
            tvImportant.setVisibility(View.GONE);
        companyCard.setCustomer(customer);
        if (!TextUtils.isEmpty(customer.getOwner())) {
            tvPrincipal.setText(customer.getOwner());
        }
        if (customer.getTel() != null && customer.getTel().size() > 0) {
            // 取得電話資訊
            StringBuilder builder = new StringBuilder();
            for (Phone phone : customer.getTel()) {
                if (!TextUtils.isEmpty(builder.toString())) {
                    builder.append("\n");
                }
                builder.append(phone.getShowPhone());
            }
            String text = builder.toString();
            if (!TextUtils.isEmpty(text))
                tvPhone.setText(text);
        }
        if (!TextUtils.isEmpty(customer.getProduct())) {
            // 取得網站資訊
            tvProduct.setText(customer.getProduct());
        }
//        if (!TextUtils.isEmpty(customer.getInvoice_number())) {
//            // 取得統一編號
//            tvUniformId.setText(customer.getInvoice_number());
//        }
        if (!TextUtils.isEmpty(customer.getWebsite())) {
            // 取得網站資訊
            tvWebsite.setText(customer.getWebsite());
        }
        if (!TextUtils.isEmpty(customer.getUser_id())) {
            // 取得建立人
            mDisposable.add(DatabaseHelper.getInstance(getActivity()).getUserById(customer
                    .getUser_id()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<User>() {

                @Override
                public void accept(User user) throws Exception {
                    tvUser.setText(user.getShowName());
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    ErrorHandler.getInstance().setException(getActivity(), throwable);
                }
            }));
        }
        // 取得隸屬企業
        if (!TextUtils.isEmpty(customer.getEnterprise())) {
            mDisposable.add(DatabaseHelper.getInstance(getActivity()).getCustomerById(customer
                    .getEnterprise()).observeOn
                    (AndroidSchedulers.mainThread()).subscribe(new Consumer<Customer>() {

                @Override
                public void accept(Customer customer) throws Exception {
                    tvGroup.setText(customer.getFull_name());
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    ErrorHandler.getInstance().setException(getActivity(), throwable);
                }
            }));
        }
        // 行業別
        if (!TextUtils.isEmpty(customer.getIndustry_id())) {
            mDisposable.add(DatabaseHelper.getInstance(getActivity()).getConfigIndustryById(customer
                    .getIndustry_id()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<ConfigIndustry>() {

                @Override
                public void accept(ConfigIndustry config) throws Exception {
                    tvIndustry.setText(config.getName());
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    tvIndustry.setText(R.string.empty_show);
                    Logger.e(TAG, throwable.getMessage());
                }
            }));
        }
        // 客戶分類
        if (!TextUtils.isEmpty(customer.getSap_type_id())) {
            mDisposable.add(DatabaseHelper.getInstance(getActivity())
                    .getConfigCustomerSapTypeById(customer
                            .getSap_type_id()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<ConfigCustomerSapType>() {

                        @Override
                        public void accept(ConfigCustomerSapType config) throws Exception {
                            tvType.setText(config.getName());
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            tvType.setText(R.string.empty_show);
                            Logger.e(TAG, throwable.getMessage());
                        }
                    }));
        }
        // 客戶級別
        if (!TextUtils.isEmpty(customer.getLevel_id())) {
            mDisposable.add(DatabaseHelper.getInstance(getActivity()).getConfigCustomerLevelById
                    (customer
                            .getLevel_id()).observeOn(AndroidSchedulers.mainThread()).subscribe
                    (new Consumer<ConfigCustomerLevel>() {

                        @Override
                        public void accept(ConfigCustomerLevel config) throws Exception {
                            tvLevel.setText(config.getName());
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            tvLevel.setText(R.string.empty_show);
                            Logger.e(TAG, throwable.getMessage());
                        }
                    }));
        }
        // 客戶組
        if (!TextUtils.isEmpty(customer.getCustomer_sap_group_id())) {
            mDisposable.add(DatabaseHelper.getInstance(getActivity()).getConfigCustomerSapGroupById(
                    (customer.getCustomer_sap_group_id())).observeOn(AndroidSchedulers.mainThread
                    ()).subscribe(new Consumer<ConfigCustomerSapGroup>() {

                @Override
                public void accept(ConfigCustomerSapGroup configCustomerSapGroup) throws Exception {
                    tvTeam.setText(configCustomerSapGroup.getName());
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    Logger.e(TAG, throwable.getMessage());
                }
            }));
        }
        // 客戶來源
        if (!TextUtils.isEmpty(customer.getCustomer_source())) {
            mDisposable.add(DatabaseHelper.getInstance(getActivity()).getConfigCustomerSourceById(
                    (customer.getCustomer_source())).observeOn(AndroidSchedulers.mainThread
                    ()).subscribe(new Consumer<ConfigCustomerSource>() {

                @Override
                public void accept(ConfigCustomerSource configCustomerSource) throws Exception {
                    tvSource.setText(configCustomerSource.getName());
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    Logger.e(TAG, throwable.getMessage());
                }
            }));
        }
    }

    void updateContactList() {
        containerContact.removeAllViews();
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int width = displaymetrics.widthPixels;
        final int contactWidth = width * 9 / 10;
        DatabaseHelper.getInstance(getActivity()).getContactPersonByCustomer(customer.getId())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new FlowableSubscriber<ContactPerson>()
                {

            @Override
            public void onSubscribe(@NonNull Subscription s) {
                s.request(Integer.MAX_VALUE);
            }

            @Override
            public void onNext(final ContactPerson contactPerson) {
                Logger.e(TAG, "contactPerson:" + new Gson().toJson(contactPerson));
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(contactWidth,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                ContactCard contactCard = new ContactCard(getActivity());
                contactCard.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable
                        .bg_gray_light_corner));
                contactCard.setContact(contactPerson);
                contactCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ContactFragment contactFragment = ContactFragment.newInstance(contactPerson);
                        contactFragment.show(getActivity().getFragmentManager(), "contact");
                    }
                });
                containerContact.addView(contactCard, params);
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onComplete() {
                if (containerContact.getChildCount() > 0) {
                    svContact.setItemWidth(contactWidth + UnitChanger.dpToPx(3));
                    svContact.setOnPageChangeListener(new HorizontalPageView.OnPageChangeListener
                            () {
                        @Override
                        public void onPageChanged(int page) {
                            Logger.e(TAG, "page index:" + page);
                            tvIndex.setText(String.valueOf(page + 1));
                        }
                    });
                    tvIndex.setText(String.valueOf(1));
                    tvTotal.setText(String.valueOf(containerContact.getChildCount()));
                } else {
                    Logger.e(TAG, "contact size:0");
                    tvTotal.setText(String.valueOf(0));
                    tvIndex.setText(String.valueOf(0));
                }
            }
        });
    }

    @OnClick(R.id.imageButton_add_contact)
    void addContact() {
        Intent intent = new Intent(getActivity(), ContactPersonCreateActivity.class);
        intent.putExtra(MyApplication.INTENT_KEY_CUSTOMER, customer.getId());
        startActivityForResult(intent, MyApplication.REQUEST_ADD_CONTACT);
    }

}
