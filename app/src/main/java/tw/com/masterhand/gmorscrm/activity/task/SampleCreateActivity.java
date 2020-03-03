package tw.com.masterhand.gmorscrm.activity.task;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import tw.com.masterhand.gmorscrm.BaseUserCheckActivity;
import tw.com.masterhand.gmorscrm.ContactPersonSelectActivity;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.TokenManager;
import tw.com.masterhand.gmorscrm.model.ProjectWithConfig;
import tw.com.masterhand.gmorscrm.model.SampleProductWithConfig;
import tw.com.masterhand.gmorscrm.model.SampleWithConfig;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.record.ContactPerson;
import tw.com.masterhand.gmorscrm.room.record.Contacter;
import tw.com.masterhand.gmorscrm.room.record.Customer;
import tw.com.masterhand.gmorscrm.room.record.Sample;
import tw.com.masterhand.gmorscrm.room.setting.Admin;
import tw.com.masterhand.gmorscrm.room.setting.ConfigCompanyHiddenField;
import tw.com.masterhand.gmorscrm.room.setting.ConfigSampleAmountRange;
import tw.com.masterhand.gmorscrm.room.setting.ConfigSampleImportMethod;
import tw.com.masterhand.gmorscrm.room.setting.ConfigSamplePaymentMethod;
import tw.com.masterhand.gmorscrm.room.setting.ConfigSampleReason;
import tw.com.masterhand.gmorscrm.room.setting.ConfigSampleSource;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.tools.TimeFormater;
import tw.com.masterhand.gmorscrm.tools.UnitChanger;
import tw.com.masterhand.gmorscrm.view.AppbarEdit;
import tw.com.masterhand.gmorscrm.view.Dropdown;
import tw.com.masterhand.gmorscrm.view.EditTextWithTitle;
import tw.com.masterhand.gmorscrm.view.ItemSelectCustomer;
import tw.com.masterhand.gmorscrm.view.ItemSelectPeople;
import tw.com.masterhand.gmorscrm.view.ItemSelectPhoto;
import tw.com.masterhand.gmorscrm.view.ItemSelectProject;

public class SampleCreateActivity extends BaseUserCheckActivity implements ItemSelectCustomer.CustomerSelectListener {
    @BindView(R.id.appbar)
    AppbarEdit appbar;
    @BindView(R.id.itemSelectCustomer)
    ItemSelectCustomer itemSelectCustomer;
    @BindView(R.id.itemSelectProject)
    ItemSelectProject itemSelectProject;
    @BindView(R.id.itemSelectPeople)
    ItemSelectPeople itemSelectPeople;
    @BindView(R.id.itemSelectPhoto)
    ItemSelectPhoto itemSelectPhoto;
    @BindView(R.id.dropdownPrice)
    Dropdown dropdownPrice;
    @BindView(R.id.dropdownReason)
    Dropdown dropdownReason;
    @BindView(R.id.dropdownSource)
    Dropdown dropdownSource;
    @BindView(R.id.dropdownImport)
    Dropdown dropdownImport;
    @BindView(R.id.dropdownDelivery)
    Dropdown dropdownDelivery;
    @BindView(R.id.dropdownAssistant)
    Dropdown dropdownAssistant;
    @BindView(R.id.etName)
    EditTextWithTitle etName;
    @BindView(R.id.etTestPeriod)
    EditTextWithTitle etTestPeriod;
    @BindView(R.id.etTestBasis)
    EditTextWithTitle etTestBasis;
    @BindView(R.id.containerReceiver)
    RelativeLayout containerReceiver;
    @BindView(R.id.tvReceiver)
    TextView tvReceiver;
    @BindView(R.id.etReceiverAddress)
    EditTextWithTitle etReceiverAddress;
    @BindView(R.id.etReceiverPhone)
    EditTextWithTitle etReceiverPhone;
    @BindView(R.id.tvStartDate)
    TextView tvStartDate;
    @BindView(R.id.tvDeliveryDate)
    TextView tvDeliveryDate;
    @BindView(R.id.btnAddProduct)
    Button btnAddProduct;
    @BindView(R.id.containerStartDate)
    RelativeLayout containerStartDate;
    @BindView(R.id.container)
    LinearLayout container;
    @BindView(R.id.etIntro)
    EditText etIntro;
    @BindView(R.id.etNote)
    EditText etNote;

    SampleWithConfig sample;
    SampleProductWithConfig selectedProduct;

    List<ConfigSampleAmountRange> configSampleAmountRanges;
    List<ConfigSampleImportMethod> configSampleImportMethods;
    List<ConfigSamplePaymentMethod> configSamplePaymentMethods;
    List<ConfigSampleReason> configSampleReasons;
    List<ConfigSampleSource> configSampleSources;

    List<Admin> adminList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_create);
        init();
    }

    @Override
    protected void onUserChecked() {
        appbar.invalidate();
        getHiddenField();
        if (sample != null)
            return;

        String tripId = getIntent().getStringExtra(MyApplication.INTENT_KEY_TRIP);
        if (TextUtils.isEmpty(tripId)) {
            /*新建送樣*/
            sample = new SampleWithConfig();
            sample.setSample(new Sample());
            getDropdownItem();

            String customerId = getIntent().getStringExtra(MyApplication.INTENT_KEY_CUSTOMER);
            if (!TextUtils.isEmpty(customerId)) {
                mDisposable.add(DatabaseHelper.getInstance(this).getCustomerById(customerId)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Customer>
                                () {

                            @Override
                            public void accept(Customer customer) throws Exception {
                                //選擇客戶
                                itemSelectCustomer.setCustomer(customer);
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                ErrorHandler.getInstance().setException(SampleCreateActivity.this, throwable);
                            }
                        }));
            }
        } else {
            /*編輯送樣*/
            getSample(tripId);
        }
    }

    private void init() {
        itemSelectPeople.disableParticipant();
        itemSelectPeople.setItemSelectCustomer(itemSelectCustomer);
        itemSelectCustomer.setShouldCheck(true);
        itemSelectCustomer.addCustomerSelectedListener(this);
        itemSelectProject.setItemSelectCustomer(itemSelectCustomer);
        appbar.setTitle(getString(R.string.main_menu_new_task) + "/" + getString(R.string
                .apply_sample));
        appbar.setCompleteListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 完成
                if (checkInput()) {
                    sample.getTrip().setCustomer_id(itemSelectCustomer.getCustomer().getId());
                    sample.getTrip().setProject_id(itemSelectProject.getProject().getProject().getId());
                    sample.getTrip().setName(etName.getText().toString());
                    sample.getTrip().setDate_type(true);
                    sample.getTrip().setDescription(etNote.getText().toString());
                    sample.getSample().setBrief(etIntro.getText().toString());
                    sample.getSample().setTest_cycle(etTestPeriod.getText().toString());
                    sample.getSample().setTest_evaluation_basis(etTestBasis.getText().toString());
                    sample.getSample().setAddress(etReceiverAddress.getText().toString());
                    sample.getSample().setTel(etReceiverPhone.getText().toString());

                    if (dropdownPrice.getSelected() != Dropdown.VALUE_EMPTY)
                        sample.getSample().setAmount_range_id(configSampleAmountRanges.get(dropdownPrice.getSelected()).getId());
                    if (dropdownReason.getSelected() != Dropdown.VALUE_EMPTY)
                        sample.getSample().setReason_id(configSampleReasons.get(dropdownReason.getSelected()).getId());
                    if (dropdownSource.getSelected() != Dropdown.VALUE_EMPTY)
                        sample.getSample().setSource_id(configSampleSources.get(dropdownSource.getSelected()).getId());
                    if (dropdownImport.getSelected() != Dropdown.VALUE_EMPTY)
                        sample.getSample().setImport_method_id(configSampleImportMethods.get(dropdownImport.getSelected()).getId());
                    if (dropdownDelivery.getSelected() != Dropdown.VALUE_EMPTY)
                        sample.getSample().setPayment_method_id(configSamplePaymentMethods.get(dropdownDelivery.getSelected()).getId());
                    if (adminList != null && dropdownAssistant.getSelected() != Dropdown.VALUE_EMPTY) {
                        sample.getTrip().setAssistant_id(adminList.get(dropdownAssistant.getSelected()).getId());
                    }

                    sample.setContacters(itemSelectPeople.getContacters());
                    sample.setFiles(itemSelectPhoto.getFiles());
                    save();
                }
            }
        });

    }

    /**
         * 隱藏欄位
         */
    void getHiddenField() {
        mDisposable.add(DatabaseHelper.getInstance(this).getConfigCompanyHiddenField(TokenManager.getInstance().getUser().getCompany_id())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ConfigCompanyHiddenField>() {

            @Override
            public void accept(ConfigCompanyHiddenField configCompanyHiddenField) throws Exception {
                if (configCompanyHiddenField.getSample_hidden_import_method_id()) {
                    dropdownImport.setVisibility(View.GONE);
                }
                if (configCompanyHiddenField.getSample_hidden_payment_method_id()) {
                    dropdownDelivery.setVisibility(View.GONE);
                }
                if (configCompanyHiddenField.getSample_hidden_receiver()) {
                    containerReceiver.setVisibility(View.GONE);
                }
                if (configCompanyHiddenField.getSample_hidden_address()) {
                    etReceiverAddress.setVisibility(View.GONE);
                }
                if (configCompanyHiddenField.getSample_hidden_tel()) {
                    etReceiverPhone.setVisibility(View.GONE);
                }
                if (configCompanyHiddenField.getSample_hidden_amount_range_id()) {
                    dropdownPrice.setVisibility(View.GONE);
                }
                if (configCompanyHiddenField.getSample_hidden_test_cycle()) {
                    etTestPeriod.setVisibility(View.GONE);
                }
                if (configCompanyHiddenField.getSample_hidden_test_evaluation_basis()) {
                    etTestBasis.setVisibility(View.GONE);
                }
                if (configCompanyHiddenField.getSample_hidden_test_from_date()) {
                    containerStartDate.setVisibility(View.GONE);
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                ErrorHandler.getInstance().setException(SampleCreateActivity.this, throwable);
            }
        }));
    }

    private void getDropdownItem() {
        //樣品單總面價金額設定
        mDisposable.add(DatabaseHelper.getInstance(this).getConfigSampleAmountRange().toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<ConfigSampleAmountRange>>() {
                    @Override
                    public void accept(List<ConfigSampleAmountRange> configs) throws Exception {
                        if (configs == null)
                            return;

                        int index = Dropdown.VALUE_EMPTY;
                        configSampleAmountRanges = configs;
                        String[] items = new String[configSampleAmountRanges.size()];
                        for (int i = 0; i < items.length; i++) {
                            ConfigSampleAmountRange config = configSampleAmountRanges.get(i);
                            items[i] = config.getName();
                            if (config.getId().equals(sample.getSample().getAmount_range_id()))
                                index = i;
                        }

                        dropdownPrice.setItems(items);
                        if (index != Dropdown.VALUE_EMPTY)
                        {
                            dropdownPrice.select(index);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ErrorHandler.getInstance().setException(SampleCreateActivity.this, throwable);
                    }
                }));

        //取得進口方式
        mDisposable.add(DatabaseHelper.getInstance(this).getConfigSampleImportMethod().toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<ConfigSampleImportMethod>>() {
                    @Override
                    public void accept(List<ConfigSampleImportMethod> configs) throws Exception {
                        if (configs == null)
                            return;

                        int index = Dropdown.VALUE_EMPTY;
                        configSampleImportMethods = new ArrayList<>();
                        ConfigSampleImportMethod empty = new ConfigSampleImportMethod();
                        configSampleImportMethods.add(0, empty);
                        configSampleImportMethods.addAll(configs);
                        String[] items = new String[configSampleImportMethods.size()];
                        for (int i = 0; i < items.length; i++) {
                            ConfigSampleImportMethod config = configSampleImportMethods.get(i);
                            items[i] = config.getName();
                            if (config.getId().equals(sample.getSample().getImport_method_id()))
                                index = i;
                        }

                        dropdownImport.setItems(items);
                        if (index != Dropdown.VALUE_EMPTY)
                        {
                            dropdownImport.select(index);
                        }

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ErrorHandler.getInstance().setException(SampleCreateActivity.this, throwable);
                    }
                }));

        //取得樣品國內運輸設定
        mDisposable.add(DatabaseHelper.getInstance(this).getConfigSamplePaymentMethod().toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<ConfigSamplePaymentMethod>>() {
                    @Override
                    public void accept(List<ConfigSamplePaymentMethod> configs) throws Exception {
                        if (configs == null)
                            return;

                        int index = Dropdown.VALUE_EMPTY;
                        configSamplePaymentMethods = new ArrayList<>();
                        ConfigSamplePaymentMethod empty = new ConfigSamplePaymentMethod();
                        configSamplePaymentMethods.add(0, empty);
                        configSamplePaymentMethods.addAll(configs);
                        String[] items = new String[configSamplePaymentMethods.size()];
                        for (int i = 0; i < items.length; i++) {
                            ConfigSamplePaymentMethod config = configSamplePaymentMethods.get(i);
                            items[i] = config.getName();
                            if (config.getId().equals(sample.getSample().getPayment_method_id()))
                                index = i;
                        }

                        dropdownDelivery.setItems(items);
                        if (index != Dropdown.VALUE_EMPTY)
                        {
                            dropdownDelivery.select(index);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ErrorHandler.getInstance().setException(SampleCreateActivity.this, throwable);
                    }
                }));

        //取得送樣理由
        mDisposable.add(DatabaseHelper.getInstance(this).getConfigSampleReason().toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<ConfigSampleReason>>() {
                    @Override
                    public void accept(List<ConfigSampleReason> configs) throws Exception {
                        if (configs == null)
                            return;

                        int index = Dropdown.VALUE_EMPTY;
                        configSampleReasons = configs;
                        String[] items = new String[configSampleReasons.size()];
                        for (int i = 0; i < items.length; i++) {
                            ConfigSampleReason config = configSampleReasons.get(i);
                            items[i] = config.getName();
                            if (config.getId().equals(sample.getSample().getReason_id()))
                                index = i;
                        }

                        dropdownReason.setItems(items);
                        if (index != Dropdown.VALUE_EMPTY)
                        {
                            dropdownReason.select(index);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ErrorHandler.getInstance().setException(SampleCreateActivity.this, throwable);
                    }
                }));

        //取得樣品來源
        mDisposable.add(DatabaseHelper.getInstance(this).getConfigSampleSource().toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<ConfigSampleSource>>() {

                    @Override
                    public void accept(List<ConfigSampleSource> configs) throws Exception {
                        if (configs == null)
                            return;

                        int index = Dropdown.VALUE_EMPTY;
                        configSampleSources = configs;
                        String[] items = new String[configSampleSources.size()];

                        for (int i = 0; i < items.length; i++) {
                            ConfigSampleSource config = configSampleSources.get(i);
                            items[i] = config.getName();
                            if (config.getId().equals(sample.getSample().getSource_id()))
                                index = i;
                        }

                        dropdownSource.setItems(items);
                        if (index != Dropdown.VALUE_EMPTY)
                            dropdownSource.select(index);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ErrorHandler.getInstance().setException(SampleCreateActivity.this, throwable);
                    }
                }));

        //取得銷售助理設定
        mDisposable.add(DatabaseHelper.getInstance(this).getAdmin()
                .observeOn(AndroidSchedulers.mainThread()).toList()
                .subscribe(new Consumer<List<Admin>>() {
            @Override
            public void accept(List<Admin> admins) throws Exception {
                adminList = admins;

                if (admins.size() > 0) {
                    String assistantId = TokenManager.getInstance().getUser().getAssistant_id();
                    Logger.e(TAG, "adminList:" + gson.toJson(adminList));
                    Logger.e(TAG, "default assistant id:" + assistantId);
                    Logger.e(TAG, "trip assistant id:" + sample.getTrip().getAssistant_id());
                    if (!TextUtils.isEmpty(sample.getTrip().getAssistant_id())) {
                        assistantId = sample.getTrip().getAssistant_id();
                    }
                    int select = -1;
                    String[] items = new String[admins.size()];
                    for (int i = 0; i < items.length; i++) {
                        items[i] = admins.get(i).getName();
                        if (admins.get(i).getId().equals(assistantId)) {
                            select = i;
                        }
                    }

                    dropdownAssistant.setItems(items);
                    dropdownAssistant.select(select);
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                ErrorHandler.getInstance().setException(SampleCreateActivity.this, throwable);
            }
        }));
    }

    /**
         * 取得送樣內容
         */
    private void getSample(String tripId) {
        mDisposable.add(DatabaseHelper.getInstance(this).getSampleByTrip(tripId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<SampleWithConfig>() {
            @Override
            public void accept(SampleWithConfig result) throws Exception {
                sample = result;

                getCustomer();//取得客戶
                getProject(sample.getTrip().getProject_id());//取得工作項目
                getDropdownItem();

                if (!TextUtils.isEmpty(sample.getTrip().getName()))
                    etName.setText(sample.getTrip().getName());//送樣名稱
                if (!TextUtils.isEmpty(sample.getSample().getTest_cycle()))
                    etTestPeriod.setText(sample.getSample().getTest_cycle());//測試週期
                if (!TextUtils.isEmpty(sample.getSample().getTest_evaluation_basis()))
                    etTestBasis.setText(sample.getSample().getTest_evaluation_basis());//測試評估依據
                if (!TextUtils.isEmpty(sample.getSample().getReceiver()))
                    DatabaseHelper.getInstance(SampleCreateActivity.this).getContactPersonById(sample.getSample().getReceiver())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<ContactPerson>() {

                                @Override
                                public void accept(ContactPerson contactPerson) throws Exception {
                                    tvReceiver.setText(contactPerson.getShowName());//收貨人
                                    tvReceiver.setTextColor(ContextCompat.getColor(SampleCreateActivity.this, R.color.black));
                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) throws Exception {
                                    ErrorHandler.getInstance().setException(SampleCreateActivity.this, throwable);
                                }
                            });

                if (!TextUtils.isEmpty(sample.getSample().getAddress()))
                    etReceiverAddress.setText(sample.getSample().getAddress());//收貨人地址
                if (!TextUtils.isEmpty(sample.getSample().getTel()))
                    etReceiverPhone.setText(sample.getSample().getTel());//收貨人電話
                if (sample.getSample().getTest_from_date() != null) {
                    tvStartDate.setText(TimeFormater.getInstance().toDateFormat(sample.getSample().getTest_from_date()));
                    tvStartDate.setTextColor(ContextCompat.getColor(SampleCreateActivity.this, R.color.orange));
                }
                if (sample.getSample().getShipping_date() != null) {
                    tvDeliveryDate.setText(TimeFormater.getInstance().toDateFormat(sample.getSample().getShipping_date()));//要求發貨時間
                    tvDeliveryDate.setTextColor(ContextCompat.getColor(SampleCreateActivity.this, R.color.orange));
                }

                updateProductList();

                if (!TextUtils.isEmpty(sample.getSample().getBrief()))
                    etIntro.setText(sample.getSample().getBrief());//簡述
                if (!TextUtils.isEmpty(sample.getTrip().getDescription()))
                    etNote.setText(sample.getTrip().getDescription());//備註

                itemSelectPeople.setContacters(sample.getContacters());//聯絡人
                itemSelectPhoto.setFiles(sample.getFiles());
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Toast.makeText(SampleCreateActivity.this, throwable.getMessage(), Toast.LENGTH_LONG).show();
                finish();
            }
        }));
    }

    /**
         * 完成
         */
    protected void save() {
        mDisposable.add(DatabaseHelper.getInstance(this).saveSample(sample)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                stopProgressDialog();
                setResult(RESULT_OK);
                finish();
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable t) throws Exception {
                stopProgressDialog();
                Toast.makeText(SampleCreateActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        }));
    }

    protected View generateProductItem(final SampleProductWithConfig product) {
        final Button btnProduct = new Button(this);
        btnProduct.setText(product.getProduct().getProduct_model());
        btnProduct.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        btnProduct.setPaddingRelative(UnitChanger.dpToPx(16), 0, 0, 0);
        btnProduct.setBackgroundResource(R.drawable.bg_sample_required);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            btnProduct.setStateListAnimator(null);
        }
        btnProduct.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.mipmap.common_arrow_right, 0);
        btnProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedProduct = product;
                Intent intent = new Intent(SampleCreateActivity.this, SampleProductCreateActivity.class);
                intent.putExtra(MyApplication.INTENT_KEY_PRODUCT, gson.toJson(product));
                startActivityForResult(intent, MyApplication.REQUEST_EDIT_PRODUCT);
            }
        });
        btnProduct.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext(), MyApplication.DIALOG_STYLE);
                builder.setTitle(R.string.remove).setMessage(getString(R.string
                        .remove_confirm) + "?")
                        .setPositiveButton(R.string.confirm,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        sample.getProducts().remove(product);
                                        container.removeView(btnProduct);
                                    }
                                });
                builder.create().show();
                return true;
            }
        });
        return btnProduct;
    }

    protected void updateProductList() {
        container.removeAllViews();
        for (SampleProductWithConfig product : sample.getProducts()) {
            container.addView(generateProductItem(product));
        }
    }

    /**
         * 取得客戶
         */
    private void getCustomer() {
        mDisposable.add(DatabaseHelper.getInstance(this).getCustomerById(sample.getTrip().getCustomer_id())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Customer>() {
                    @Override
                    public void accept(Customer customer) throws Exception {
                        //取得客戶
                        itemSelectCustomer.setCustomer(customer);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ErrorHandler.getInstance().setException(SampleCreateActivity.this, throwable);
                    }
                }));
    }

    /**
         * 取得工作項目*
         */
    protected void getProject(String projectId) {
        if (TextUtils.isEmpty(projectId))
            return;

        mDisposable.add(DatabaseHelper.getInstance(this).getProjectById(projectId, TokenManager
                .getInstance().getUser().getDepartment_id(), TokenManager.getInstance().getUser().getCompany_id())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ProjectWithConfig>() {

            @Override
            public void accept(ProjectWithConfig projectWithConfig) throws Exception {
                //取得工作項目
                itemSelectProject.setProject(projectWithConfig);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                ErrorHandler.getInstance().setException(SampleCreateActivity.this, throwable);
            }
        }));
    }

    /**
         * 確認輸入資料
         */
    protected boolean checkInput() {
        String required = getString(R.string.error_msg_required);
        if (containerStartDate.getVisibility() == View.VISIBLE && sample.getSample().getTest_from_date() == null) {
            Toast.makeText(this, required + getString(R.string.sample_test_start_time), Toast.LENGTH_LONG).show();
            return false;
        }
        if (sample.getSample().getShipping_date() == null) {
            Toast.makeText(this, required + getString(R.string.sample_delivery_time), Toast.LENGTH_LONG).show();
            return false;
        }
        if (itemSelectCustomer.getCustomer() == null) {
            Toast.makeText(this, R.string.error_msg_no_customer, Toast.LENGTH_LONG).show();
            return false;
        }
        if (itemSelectProject.getProject() == null) {
            Toast.makeText(this, R.string.error_msg_no_project, Toast.LENGTH_LONG).show();
            return false;
        }
        if (sample.getProducts().size() == 0) {
            Toast.makeText(this, R.string.error_msg_no_product, Toast.LENGTH_LONG).show();
            return false;
        }
        if (TextUtils.isEmpty(etName.getText().toString())) {
            Toast.makeText(this, required + getString(R.string.sample_name), Toast.LENGTH_LONG).show();
            return false;
        }
        if (etTestPeriod.getVisibility() == View.VISIBLE && TextUtils.isEmpty(etTestPeriod.getText().toString())) {
            Toast.makeText(this, required + getString(R.string.sample_test_period), Toast.LENGTH_LONG).show();
            return false;
        }
        if (etTestBasis.getVisibility() == View.VISIBLE && TextUtils.isEmpty(etTestBasis.getText().toString())) {
            Toast.makeText(this, required + getString(R.string.sample_test_basis), Toast.LENGTH_LONG).show();
            return false;
        }
        if (TextUtils.isEmpty(etIntro.getText().toString())) {
            Toast.makeText(this, required + getString(R.string.sample_intro), Toast.LENGTH_LONG).show();
            return false;
        }
        if (dropdownPrice.getVisibility() == View.VISIBLE && dropdownPrice.getSelected() == Dropdown.VALUE_EMPTY) {
            Toast.makeText(this, required + getString(R.string.sample_price), Toast.LENGTH_LONG).show();
            return false;
        }
        if (dropdownReason.getSelected() == Dropdown.VALUE_EMPTY) {
            Toast.makeText(this, required + getString(R.string.sample_reason), Toast.LENGTH_LONG).show();
            return false;
        }
        if (dropdownSource.getSelected() == Dropdown.VALUE_EMPTY) {
            Toast.makeText(this, required + getString(R.string.sample_source), Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    //收貨人
    @OnClick(R.id.tvReceiver)
    void changeReceiver() {
        if (itemSelectCustomer.getCustomer() == null) {
            Toast.makeText(this, R.string.error_msg_no_customer, Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(this, ContactPersonSelectActivity.class);
        intent.putExtra(MyApplication.INTENT_KEY_CUSTOMER, itemSelectCustomer.getCustomer().getId());
        intent.putExtra(MyApplication.INTENT_KEY_ENABLE, false);
        startActivityForResult(intent, MyApplication.REQUEST_SELECT_RECEIVER);
    }

    //測試開始時間
    @OnClick(R.id.tvStartDate)
    void changeStartDate() {
        final Calendar toBeChanged = Calendar.getInstance(Locale.getDefault());
        if (sample.getSample().getTest_from_date() != null)
        {
            toBeChanged.setTime(sample.getSample().getTest_from_date());
        }

        DatePickerDialog datePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                toBeChanged.set(Calendar.YEAR, year);
                toBeChanged.set(Calendar.MONTH, month);
                toBeChanged.set(Calendar.DAY_OF_MONTH, day);
                sample.getSample().setTest_from_date(toBeChanged.getTime());
                tvStartDate.setText(TimeFormater.getInstance().toDateFormat(sample.getSample().getTest_from_date()));
                tvStartDate.setTextColor(ContextCompat.getColor(SampleCreateActivity.this, R.color.orange));
            }
        }, toBeChanged.get(Calendar.YEAR), toBeChanged.get(Calendar.MONTH), toBeChanged.get(Calendar.DAY_OF_MONTH));
        datePicker.show();
    }

    //要求發貨時間
    @OnClick(R.id.tvDeliveryDate)
    void changeDeliveryDate() {
        final Calendar toBeChanged = Calendar.getInstance(Locale.getDefault());
        if (sample.getSample().getShipping_date() != null)
        {
            toBeChanged.setTime(sample.getSample().getShipping_date());
        }

        DatePickerDialog datePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                toBeChanged.set(Calendar.YEAR, year);
                toBeChanged.set(Calendar.MONTH, month);
                toBeChanged.set(Calendar.DAY_OF_MONTH, day);
                sample.getSample().setShipping_date(toBeChanged.getTime());
                tvDeliveryDate.setText(TimeFormater.getInstance().toDateFormat(sample.getSample().getShipping_date()));
                tvDeliveryDate.setTextColor(ContextCompat.getColor(SampleCreateActivity.this, R.color.orange));
            }
        }, toBeChanged.get(Calendar.YEAR), toBeChanged.get(Calendar.MONTH), toBeChanged.get(Calendar.DAY_OF_MONTH));
        datePicker.show();
    }

    /**
         * 新增產品
         */
    @OnClick(R.id.btnAddProduct)
    protected void addProduct() {
        Intent intent = new Intent(this, SampleProductCreateActivity.class);
        startActivityForResult(intent, MyApplication.REQUEST_EDIT_PRODUCT);
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case MyApplication.REQUEST_EDIT_PRODUCT:
                    SampleProductWithConfig product = gson.fromJson(data.getStringExtra(MyApplication.INTENT_KEY_PRODUCT), SampleProductWithConfig.class);
                    if (selectedProduct != null) {
                        sample.getProducts().remove(selectedProduct);
                        selectedProduct = null;
                    }
                    sample.getProducts().add(product);
                    updateProductList();
                    updateName();
                    break;
                case MyApplication.REQUEST_SELECT_RECEIVER:
                    ArrayList<Contacter> contacters = gson.fromJson(data.getStringExtra(MyApplication.INTENT_KEY_PEOPLE), new TypeToken<ArrayList<Contacter>>() {
                    }.getType());
                    if (contacters.size() > 0) {
                        Contacter contacter = contacters.get(0);
                        sample.getSample().setReceiver(contacter.getUser_id());
                        mDisposable.add(DatabaseHelper.getInstance(SampleCreateActivity.this)
                                .getContactPersonById(sample.getSample().getReceiver())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Consumer<ContactPerson>() {

                                    @Override
                                    public void accept(ContactPerson contactPerson) throws Exception {
                                        tvReceiver.setText(contactPerson.getShowName());
                                        tvReceiver.setTextColor(ContextCompat.getColor(SampleCreateActivity.this, R.color.black));
                                    }
                                }, new Consumer<Throwable>() {
                                    @Override
                                    public void accept(Throwable throwable) throws Exception {
                                        ErrorHandler.getInstance().setException(SampleCreateActivity.this, throwable);
                                    }
                                }));
                    }
                    break;
                default:
                    itemSelectPhoto.onActivityResult(requestCode, resultCode, data);
                    itemSelectCustomer.onActivityResult(requestCode, resultCode, data);
                    itemSelectPeople.onActivityResult(requestCode, resultCode, data);
                    itemSelectProject.onActivityResult(requestCode, resultCode, data);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    protected void updateName() {
        StringBuilder builder = new StringBuilder();
        builder.append(getString(R.string.apply_sample));
        if (itemSelectCustomer.getCustomer() != null) {
            builder.append("-").append(itemSelectCustomer.getCustomer().getFull_name());
        }
        if (sample.getProducts().size() > 0) {
            builder.append("-").append(sample.getProducts().get(0).getProduct().product_model);
        }
        etName.setText(builder.toString());
    }

    @Override
    public void onCustomerSelected(Customer customer) {
        updateName();
        if (customer.getAddress() != null)
            etReceiverAddress.setText(customer.getAddress().getShowAddress());
    }
}
