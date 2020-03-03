package tw.com.masterhand.gmorscrm.activity.customer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import tw.com.masterhand.gmorscrm.BaseUserCheckActivity;
import tw.com.masterhand.gmorscrm.CustomerSelectActivity;
import tw.com.masterhand.gmorscrm.LocationSelectActivity;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.TokenManager;
import tw.com.masterhand.gmorscrm.model.Phone;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.record.Customer;
import tw.com.masterhand.gmorscrm.room.setting.ConfigCity;
import tw.com.masterhand.gmorscrm.room.setting.ConfigCompanyHiddenField;
import tw.com.masterhand.gmorscrm.room.setting.ConfigCountry;
import tw.com.masterhand.gmorscrm.room.setting.ConfigCustomerLevel;
import tw.com.masterhand.gmorscrm.room.setting.ConfigCustomerSapGroup;
import tw.com.masterhand.gmorscrm.room.setting.ConfigCustomerSapType;
import tw.com.masterhand.gmorscrm.room.setting.ConfigCustomerSource;
import tw.com.masterhand.gmorscrm.room.setting.ConfigIndustry;
import tw.com.masterhand.gmorscrm.tools.Base64Utils;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.tools.ImageHelper;
import tw.com.masterhand.gmorscrm.tools.ImageTools;
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.view.AppbarEdit;
import tw.com.masterhand.gmorscrm.view.EditTextWithTitle;
import tw.com.masterhand.gmorscrm.view.ItemInputPhone;
import tw.com.masterhand.gmorscrm.view.ItemSelectLocation;

public class CustomerCreateActivity extends BaseUserCheckActivity implements ItemInputPhone
        .SelectPhoneTypeListener, ItemSelectLocation.OnSelectListener {
    @BindView(R.id.appbar)
    AppbarEdit appbar;
    @BindView(R.id.btnLogo)
    Button btnLogo;
    @BindView(R.id.btnIndustry)
    Button btnIndustry;
    @BindView(R.id.btnCustomerType)
    Button btnCustomerType;
    @BindView(R.id.btnLevel)
    Button btnLevel;
    @BindView(R.id.btnSource)
    Button btnSource;
    @BindView(R.id.btnTeam)
    Button btnTeam;
    @BindView(R.id.btnGroup)
    Button btnGroup;
    @BindView(R.id.editText_show_name)
    EditText etShowName;
    @BindView(R.id.editText_name_chinese)
    EditText etNameChinese;
    @BindView(R.id.editText_name_english)
    EditText etNameEnglish;
    @BindView(R.id.editText_principal)
    EditText etPrincipal;
    @BindView(R.id.etProduct)
    EditText etProduct;
    @BindView(R.id.itemSelectLocation)
    ItemSelectLocation itemSelectLocation;
    @BindView(R.id.etWebsite)
    EditTextWithTitle etWebsite;
    @BindView(R.id.etInvoiceNumber)
    EditText etInvoiceNumber;
    @BindView(R.id.etNote)
    EditText etNote;
    @BindView(R.id.switchType)
    Switch switchType;
    @BindView(R.id.containerPhone)
    LinearLayout containerPhone;

    ImageHelper imageHelper;
    ItemInputPhone selectedItemInputPhone = null;

    Customer customer;
    boolean isLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_create);
        init();
    }

    @Override
    protected void onUserChecked() {
        appbar.invalidate();
        if (!isLoaded) {
            getHiddenField();
            final String customerId = getIntent().getStringExtra(MyApplication.INTENT_KEY_CUSTOMER);
            if (!TextUtils.isEmpty(customerId)) {
                mDisposable.add(DatabaseHelper.getInstance(this).getCustomerById(customerId)
                        .observeOn
                                (AndroidSchedulers.mainThread()).subscribe(new Consumer<Customer>
                                () {

                            @Override
                            public void accept(Customer mCustomer) throws Exception {
                                appbar.setTitle(getString(R.string.title_activity_customer_edit));
                                customer = mCustomer;
                                loadCustomerData();
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                ErrorHandler.getInstance().setException(CustomerCreateActivity.this, throwable);
                                customer = new Customer();
                                customer.setUser_id(TokenManager.getInstance().getUser().getId());
                                customer.setDepartment_id(TokenManager.getInstance().getUser().getDepartment_id());
                                loadCustomerData();
                            }
                        }));
            } else {
                customer = new Customer();
                customer.setUser_id(TokenManager.getInstance().getUser().getId());
                customer.setDepartment_id(TokenManager.getInstance().getUser().getDepartment_id());
                loadCustomerData();
            }
        }
    }

    private void init() {
        imageHelper = new ImageHelper(this);
        itemSelectLocation.setOnSelectListener(this);
        appbar.setTitle(getString(R.string.title_activity_customer_create));
        appbar.setCompleteListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 點擊完成
                String customerName = etNameChinese.getText().toString();
                if (!TextUtils.isEmpty(customerName) && TextUtils.isEmpty(customer.getId())) {
                    mDisposable.add(DatabaseHelper.getInstance(CustomerCreateActivity.this).checkCustomerByName(customerName)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<Boolean>() {

                                @Override
                                public void accept(Boolean aBoolean) throws Exception {
                                    if (aBoolean) {
                                        saveCustomer();
                                    } else {
                                        // 該客戶名已存在
                                        Toast.makeText(CustomerCreateActivity.this, R.string
                                                .error_msg_customer_duplicate, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }));
                } else {
                    saveCustomer();
                }
            }
        });
    }

    void getHiddenField() {
        mDisposable.add(DatabaseHelper.getInstance(this).getConfigCompanyHiddenField(TokenManager.getInstance().getUser().getCompany_id())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ConfigCompanyHiddenField>() {

            @Override
            public void accept(ConfigCompanyHiddenField configCompanyHiddenField) throws Exception {
                if (configCompanyHiddenField.getCustomer_hidden_type()) {
                    switchType.setVisibility(View.GONE);
                }
                if (configCompanyHiddenField.getCustomer_hidden_enterprises()) {
                    btnGroup.setVisibility(View.GONE);
                }
                if (configCompanyHiddenField.getCustomer_hidden_industry_id()) {
                    btnIndustry.setVisibility(View.GONE);
                }
                if (configCompanyHiddenField.getCustomer_hidden_sap_type_id()) {
                    btnCustomerType.setVisibility(View.GONE);
                }
                if (configCompanyHiddenField.getCustomer_hidden_product()) {
                    etProduct.setVisibility(View.GONE);
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                ErrorHandler.getInstance().setException(CustomerCreateActivity.this, throwable);
            }
        }));
    }

    private void loadCustomerData() {
        btnLevel.setText("*" + getString(R.string.customer_level));
        btnSource.setText("*" + getString(R.string.customer_source));
        btnTeam.setText("*" + getString(R.string.customer_team));
        etShowName.setText(customer.getName());
        etNameChinese.setText(customer.getFull_name());
        etNameEnglish.setText(customer.getFull_name_en());
        etPrincipal.setText(customer.getOwner());
        etWebsite.setText(customer.getWebsite());
//        etInvoiceNumber.setText(customer.getInvoice_number());
        etNote.setText(customer.getDescription());
        etProduct.setText(customer.getProduct());
        if (!TextUtils.isEmpty(customer.getIndustry_id())) {
            mDisposable.add(DatabaseHelper.getInstance(this).getConfigIndustryById(customer.getIndustry_id())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<ConfigIndustry>() {

                @Override
                public void accept(ConfigIndustry config) throws Exception {
                    btnIndustry.setText(config.getName());
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    ErrorHandler.getInstance().setException(CustomerCreateActivity.this, throwable);
                }
            }));
        }
        if (!TextUtils.isEmpty(customer.getSap_type_id())) {
            mDisposable.add(DatabaseHelper.getInstance(this).getConfigCustomerSapTypeById(customer.getSap_type_id())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<ConfigCustomerSapType>() {

                @Override
                public void accept(ConfigCustomerSapType config) throws Exception {
                    btnCustomerType.setText(config.getName());
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    ErrorHandler.getInstance().setException(CustomerCreateActivity.this, throwable);
                }
            }));
        }
        if (!TextUtils.isEmpty(customer.getLevel_id())) {
            mDisposable.add(DatabaseHelper.getInstance(this).getConfigCustomerLevelById(customer.getLevel_id())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<ConfigCustomerLevel>() {

                @Override
                public void accept(ConfigCustomerLevel config) throws Exception {
                    btnLevel.setText(config.getName());
                    btnLevel.setTextColor(ContextCompat.getColor(CustomerCreateActivity.this, R.color.black));
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    ErrorHandler.getInstance().setException(CustomerCreateActivity.this, throwable);
                }
            }));
        }
        if (!TextUtils.isEmpty(customer.getCustomer_sap_group_id())) {
            mDisposable.add(DatabaseHelper.getInstance(this).getConfigCustomerSapGroupById(customer.getCustomer_sap_group_id())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<ConfigCustomerSapGroup>() {

                        @Override
                        public void accept(ConfigCustomerSapGroup config) throws Exception {
                            btnTeam.setText(config.getName());
                            btnTeam.setTextColor(ContextCompat.getColor(CustomerCreateActivity.this, R.color.black));
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            ErrorHandler.getInstance().setException(CustomerCreateActivity.this, throwable);
                        }
                    }));
        }
        if (!TextUtils.isEmpty(customer.getCustomer_source())) {
            mDisposable.add(DatabaseHelper.getInstance(this).getConfigCustomerSourceById(customer.getCustomer_source())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<ConfigCustomerSource>() {

                        @Override
                        public void accept(ConfigCustomerSource config) throws Exception {
                            btnSource.setText(config.getName());
                            btnSource.setTextColor(ContextCompat.getColor(CustomerCreateActivity.this, R.color.black));
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            ErrorHandler.getInstance().setException(CustomerCreateActivity.this, throwable);
                        }
                    }));
        }
        if (!TextUtils.isEmpty(customer.getEnterprise())) {
            mDisposable.add(DatabaseHelper.getInstance(this).getCustomerById(customer.getEnterprise())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Customer>() {

                @Override
                public void accept(Customer customer) throws Exception {
                    btnGroup.setText(customer.getFull_name());
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    ErrorHandler.getInstance().setException(CustomerCreateActivity.this, throwable);
                }
            }));
        }
        switchType.setChecked(customer.isType());
        itemSelectLocation.setAddress(customer.getAddress());
        if (customer.getTel().size() > 0) {
            for (Phone phone : customer.getTel()) {
                if (containerPhone.getChildCount() > 0) {
                    addPhone(phone, false);
                } else {
                    addPhone(phone, true);
                }
            }
        } else {
            addPhone(null, true);
        }
        if (!TextUtils.isEmpty(customer.getLogo())) {
            Bitmap bitmap = Base64Utils.decodeToBitmapFromString(customer.getLogo());
            btnLogo.setBackground(ImageTools.getCircleDrawable(getResources(), bitmap));
            btnLogo.setText("");
        }
        isLoaded = true;
    }

    /**
     * 檢查必填欄位
     */
    private boolean checkInput() {
        boolean isSuccess = true;
        String required = getString(R.string.error_msg_required);
        ArrayList<Phone> phoneList = new ArrayList<>();
        for (int i = 0; i < containerPhone.getChildCount(); i++) {
            ItemInputPhone itemInputPhone = (ItemInputPhone) containerPhone.getChildAt(i);
            Phone phone = itemInputPhone.getPhone();
            if (!TextUtils.isEmpty(phone.getType()) && !TextUtils.isEmpty(phone.getTel())) {
                phoneList.add(phone);
            }
        }
        if (TextUtils.isEmpty(etNameChinese.getText().toString()) && TextUtils.isEmpty(etNameEnglish.getText().toString())) {
            Toast.makeText(this, required + getString(R.string.customer_name), Toast.LENGTH_LONG).show();
            isSuccess = false;
        } else if (itemSelectLocation.getAddress().getCountry() == null) {
            Toast.makeText(this, R.string.error_msg_no_location, Toast.LENGTH_LONG).show();
            isSuccess = false;
        } else if (phoneList.size() == 0) {
            Toast.makeText(this, required + getString(R.string.phone), Toast.LENGTH_LONG).show();
            isSuccess = false;
        } else if (TextUtils.isEmpty(customer.getLevel_id())) {
            Toast.makeText(this, required + getString(R.string.customer_level), Toast.LENGTH_LONG).show();
            isSuccess = false;
        } else if (TextUtils.isEmpty(customer.getCustomer_sap_group_id())) {
            Toast.makeText(this, required + getString(R.string.customer_team), Toast.LENGTH_LONG).show();
            isSuccess = false;
        } else if (TextUtils.isEmpty(customer.getCustomer_source())) {
            Toast.makeText(this, required + getString(R.string.customer_source), Toast.LENGTH_LONG).show();
            isSuccess = false;
        }
        return isSuccess;
    }

    @Override
    public void onSelectPhoneType(ItemInputPhone item) {
        selectedItemInputPhone = item;
    }

    private void addPhone(Phone phone, boolean isAdd) {
        final ItemInputPhone itemInputPhone = new ItemInputPhone(this);
        if (phone != null)
            itemInputPhone.setPhone(phone);
        itemInputPhone.setSelectPhoneTypeListener(this);
        if (isAdd) {
            itemInputPhone.setFunctionListener(R.mipmap.common_add, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addPhone(null, false);
                }
            });
        } else {
            itemInputPhone.setFunctionListener(R.mipmap.common_removeitem, new View
                    .OnClickListener() {
                @Override
                public void onClick(View v) {
                    containerPhone.removeView(itemInputPhone);
                }
            });
        }
        containerPhone.addView(itemInputPhone);
    }

    private void saveCustomer() {
        if (!checkInput()) {
            return;
        }
        customer.setName(etShowName.getText().toString());
        customer.setType(switchType.isChecked());
        customer.setFull_name(etNameChinese.getText().toString());
        customer.setFull_name_en(etNameEnglish.getText().toString());
        customer.setAddress(itemSelectLocation.getAddress());
        customer.setWebsite(etWebsite.getText().toString());
//                customer.setInvoice_number(etInvoiceNumber.getText().toString());
        customer.setOwner(etPrincipal.getText().toString());
        customer.setDescription(etNote.getText().toString());
        customer.setProduct(etProduct.getText().toString());
        ArrayList<Phone> phoneList = new ArrayList<>();

        for (int i = 0; i < containerPhone.getChildCount(); i++) {
            ItemInputPhone itemInputPhone = (ItemInputPhone) containerPhone.getChildAt(i);
            Phone phone = itemInputPhone.getPhone();
            if (!TextUtils.isEmpty(phone.getType()) && !TextUtils.isEmpty(phone.getTel())) {
                phoneList.add(phone);
            }
        }
        customer.setTel(phoneList);
        mDisposable.add(DatabaseHelper.getInstance(this).saveCustomer(customer, TokenManager
                .getInstance()
                .getUser().getId())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String result) throws Exception {
                        Logger.e(TAG, "customer id:" + result);
                        Intent intent = new Intent(CustomerCreateActivity.this, CustomerInfoActivity.class);
                        intent.putExtra(MyApplication.INTENT_KEY_CUSTOMER, result);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(CustomerCreateActivity.this, throwable.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }));
    }

    /**
     * 顯示照片取得方式對話框
     */
    @OnClick(R.id.btnLogo)
    void showPhotoDialog() {
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // 有相機功能，詢問使用者是要拍攝相片還是開啟相簿
            AlertDialog.Builder builder = new AlertDialog.Builder(this, MyApplication.DIALOG_STYLE);
            builder.setTitle(R.string.select_photo_title);
            builder.setItems(getResources().getStringArray(R.array.select_photo), new
                    DialogInterface
                            .OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    imageHelper.takePhoto();
                                    break;
                                case 1:
                                    imageHelper.selectPhoto();
                                    break;
                            }
                        }
                    });
            builder.create().show();
        } else {
            imageHelper.selectPhoto();
        }
    }

    @OnClick(R.id.btnGroup)
    void selectGroup() {
        Intent intent = new Intent(this, CustomerSelectActivity.class);
        intent.putExtra(MyApplication.INTENT_KEY_ENABLE, true);
        startActivityForResult(intent, MyApplication.REQUEST_SELECT_CUSTOMER);
    }

    @OnClick(R.id.btnIndustry)
    void selectIndustry() {
        mDisposable.add(DatabaseHelper.getInstance(this).getConfigIndustry().toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<ConfigIndustry>>() {

            @Override
            public void accept(final List<ConfigIndustry> configIndustries) throws Exception {
                String[] items = new String[configIndustries.size() + 1];
                int index = 0;
                items[index] = "";
                index++;
                for (ConfigIndustry config : configIndustries) {
                    items[index] = config.getName();
                    index++;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(CustomerCreateActivity.this, MyApplication.DIALOG_STYLE);
                builder.setTitle(R.string.industry).setItems(items, new DialogInterface
                        .OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i > 0) {
                            ConfigIndustry config = configIndustries.get(i - 1);
                            customer.setIndustry_id(config.getId());
                            btnIndustry.setText(config.getName());
                        } else {
                            customer.setIndustry_id("");
                            btnIndustry.setText(getString(R.string.industry));
                        }
                    }
                });
                builder.create().show();
            }
        }));
    }

    @OnClick(R.id.btnCustomerType)
    void selectCustomerType() {
        mDisposable.add(DatabaseHelper.getInstance(this).getConfigCustomerSapType().toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<ConfigCustomerSapType>>() {

                    @Override
                    public void accept(final List<ConfigCustomerSapType> configs) throws Exception {
                        String[] items = new String[configs.size() + 1];
                        int index = 0;
                        items[index] = "";
                        index++;
                        for (ConfigCustomerSapType config : configs) {
                            items[index] = config.getName();
                            index++;
                        }
                        AlertDialog.Builder builder = new AlertDialog.Builder(CustomerCreateActivity
                                .this, MyApplication.DIALOG_STYLE);
                        builder.setTitle(R.string.customer_type).setItems(items, new DialogInterface
                                .OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (i > 0) {
                                    ConfigCustomerSapType config = configs.get(i - 1);
                                    customer.setSap_type_id(config.getId());
                                    btnCustomerType.setText(config.getName());
                                } else {
                                    customer.setSap_type_id("");
                                    btnCustomerType.setText(R.string.customer_type);
                                }
                            }
                        });
                        builder.create().show();
                    }
                }));
    }

    @OnClick(R.id.btnLevel)
    void selectLevel() {
        mDisposable.add(DatabaseHelper.getInstance(this).getConfigCustomerLevel().toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<ConfigCustomerLevel>>() {

            @Override
            public void accept(final List<ConfigCustomerLevel> configs) throws Exception {
                String[] items = new String[configs.size()];
                int index = 0;
                for (ConfigCustomerLevel config : configs) {
                    items[index] = config.getName();
                    index++;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(CustomerCreateActivity.this, MyApplication.DIALOG_STYLE);
                builder.setTitle(R.string.customer_level).setItems(items, new DialogInterface
                        .OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ConfigCustomerLevel config = configs.get(i);
                        customer.setLevel_id(config.getId());
                        btnLevel.setText(config.getName());
                        btnLevel.setTextColor(ContextCompat.getColor(CustomerCreateActivity.this, R.color.black));
                    }
                });
                builder.create().show();
            }
        }));
    }

    @OnClick(R.id.btnTeam)
    void selectTeam() {
        mDisposable.add(DatabaseHelper.getInstance(this).getConfigCustomerSapGroup().toList()
                .observeOn
                        (AndroidSchedulers
                                .mainThread()).subscribe(new Consumer<List<ConfigCustomerSapGroup>>() {

                    @Override
                    public void accept(final List<ConfigCustomerSapGroup> configs) throws
                            Exception {
                        String[] items = new String[configs.size()];
                        int index = 0;
                        for (ConfigCustomerSapGroup config : configs) {
                            items[index] = config.getName();
                            index++;
                        }
                        AlertDialog.Builder builder = new AlertDialog.Builder(CustomerCreateActivity
                                .this, MyApplication.DIALOG_STYLE);
                        builder.setTitle(R.string.customer_team).setItems(items, new DialogInterface
                                .OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ConfigCustomerSapGroup config = configs.get(i);
                                customer.setCustomer_sap_group_id(config.getId());
                                btnTeam.setText(config.getName());
                                btnTeam.setTextColor(ContextCompat.getColor
                                        (CustomerCreateActivity.this, R
                                                .color.black));
                            }
                        });
                        builder.create().show();
                    }
                }));
    }

    @OnClick(R.id.btnSource)
    void selectSource() {
        mDisposable.add(DatabaseHelper.getInstance(this).getConfigCustomerSource().toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<ConfigCustomerSource>>
                        () {

                    @Override
                    public void accept(final List<ConfigCustomerSource> configs) throws
                            Exception {
                        String[] items = new String[configs.size()];
                        int index = 0;
                        for (ConfigCustomerSource config : configs) {
                            items[index] = config.getName();
                            index++;
                        }
                        AlertDialog.Builder builder = new AlertDialog.Builder(CustomerCreateActivity.this, MyApplication.DIALOG_STYLE);
                        builder.setTitle(R.string.customer_source).setItems(items, new
                                DialogInterface
                                        .OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        ConfigCustomerSource config = configs.get(i);
                                        customer.setCustomer_source(config.getId());
                                        btnSource.setText(config.getName());
                                        btnSource.setTextColor(ContextCompat.getColor(CustomerCreateActivity.this, R.color.black));
                                    }
                                });
                        builder.create().show();
                    }
                }));
    }

    @Override
    public void onSelectCountry() {
        Intent intent = new Intent(this, LocationSelectActivity.class);
        startActivityForResult(intent, MyApplication.REQUEST_SELECT_COUNTRY);
    }

    @Override
    public void onSelectCity(String countryId) {
        Intent intent = new Intent(this, LocationSelectActivity.class);
        intent.putExtra(LocationSelectActivity.KEY_COUNTRY, countryId);
        startActivityForResult(intent, MyApplication.REQUEST_SELECT_CITY);
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Uri photoUri;
            Bitmap bitmap;
            int photoSize = getResources().getDimensionPixelSize(R.dimen.photo_upload_size_small);
            switch (requestCode) {
                // 選擇圖片
                case ImageHelper.REQUEST_SELECT_PICTURE:
                    File file = new File(imageHelper.getImageFilePath(this, data.getData()));
                    photoUri = Uri.fromFile(file);
                    bitmap = imageHelper.getBitmapFromUri(photoUri, photoSize, photoSize);
                    btnLogo.setBackground(ImageTools.getCircleDrawable(getResources(), bitmap));
                    btnLogo.setText("");
                    customer.setLogo(Base64Utils.encodeBitmapToString(ImageTools.getCroppedBitmap(bitmap)));
                    break;
                // 拍攝照片
                case ImageHelper.REQUEST_TAKE_PHOTO:
                    photoUri = Uri.fromFile(imageHelper.getImageFile());
                    bitmap = imageHelper.getBitmapFromUri(photoUri, photoSize, photoSize);
                    btnLogo.setBackground(ImageTools.getCircleDrawable(getResources(), bitmap));
                    btnLogo.setText("");
                    customer.setLogo(Base64Utils.encodeBitmapToString(ImageTools.getCroppedBitmap(bitmap)));
                    break;
                // 選擇電話類型
                case MyApplication.REQUEST_SELECT_PHONE_TYPE:
                    String type = data.getStringExtra(MyApplication.INTENT_KEY_PHONE_TYPE);
                    selectedItemInputPhone.setType(type);
                    break;
                // 選擇集團
                case MyApplication.REQUEST_SELECT_CUSTOMER:
                    final String customerId = data.getStringExtra(MyApplication.INTENT_KEY_CUSTOMER);

                    if (!TextUtils.isEmpty(customer.getId()) && customer.getId().equals(customerId)) {
                        // 不可選擇自己為隸屬集團
                        Toast.makeText(this, R.string.error_msg_self_group, Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (!TextUtils.isEmpty(customerId)) {
                        DatabaseHelper.getInstance(this).getCustomerById(customerId).observeOn
                                (AndroidSchedulers.mainThread()).subscribe(new Consumer<Customer>
                                () {

                            @Override
                            public void accept(Customer customer) throws Exception {
                                CustomerCreateActivity.this.customer.setEnterprise(customer.getId());
                                btnGroup.setText(customer.getFull_name());
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                ErrorHandler.getInstance().setException(CustomerCreateActivity.this, throwable);
                            }
                        });
                    } else {
                        CustomerCreateActivity.this.customer.setEnterprise("");
                        btnGroup.setText(getString(R.string.group));
                    }
                    break;
                // 選擇國家
                case MyApplication.REQUEST_SELECT_COUNTRY:
                    ConfigCountry config = gson.fromJson(data.getStringExtra(LocationSelectActivity.KEY_RESULT), ConfigCountry.class);
                    itemSelectLocation.selectCountry(config);
                    break;
                // 選擇城市
                case MyApplication.REQUEST_SELECT_CITY:
                    ConfigCity configCity = gson.fromJson(data.getStringExtra(LocationSelectActivity.KEY_RESULT), ConfigCity.class);
                    itemSelectLocation.selectCity(configCity);
                    break;
                default:

                    break;

            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
