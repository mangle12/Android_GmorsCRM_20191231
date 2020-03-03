package tw.com.masterhand.gmorscrm.activity.task;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import tw.com.masterhand.gmorscrm.BaseUserCheckActivity;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.TokenManager;
import tw.com.masterhand.gmorscrm.model.ProjectWithConfig;
import tw.com.masterhand.gmorscrm.model.QuotationWithConfig;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.record.Customer;
import tw.com.masterhand.gmorscrm.room.record.Quotation;
import tw.com.masterhand.gmorscrm.room.record.QuotationProduct;
import tw.com.masterhand.gmorscrm.room.setting.Admin;
import tw.com.masterhand.gmorscrm.room.setting.ConfigCompanyHiddenField;
import tw.com.masterhand.gmorscrm.room.setting.ConfigTax;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.tools.NumberFormater;
import tw.com.masterhand.gmorscrm.tools.UnitChanger;
import tw.com.masterhand.gmorscrm.view.AppbarEdit;
import tw.com.masterhand.gmorscrm.view.Dropdown;
import tw.com.masterhand.gmorscrm.view.EditTextWithTitle;
import tw.com.masterhand.gmorscrm.view.ItemSelectCustomer;
import tw.com.masterhand.gmorscrm.view.ItemSelectPeople;
import tw.com.masterhand.gmorscrm.view.ItemSelectPhoto;
import tw.com.masterhand.gmorscrm.view.ItemSelectProject;

public class QuotationCreateActivity extends BaseUserCheckActivity implements ItemSelectCustomer.CustomerSelectListener {
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
    @BindView(R.id.etNote)
    EditText etNote;
    @BindView(R.id.etName)
    EditTextWithTitle etName;
    @BindView(R.id.etPayment)
    EditTextWithTitle etPayment;
    @BindView(R.id.tvAmountTitle)
    TextView tvAmountTitle;
    @BindView(R.id.tvAmount)
    TextView tvAmount;
    @BindView(R.id.dropdownUnit)
    Dropdown dropdownUnit;
    @BindView(R.id.dropdownTax)
    Dropdown dropdownTax;
    @BindView(R.id.dropdownAssistant)
    Dropdown dropdownAssistant;
    @BindView(R.id.containerDelivery)
    LinearLayout containerDelivery;
    @BindView(R.id.containerAmount)
    RelativeLayout containerAmount;

    @BindView(R.id.container)
    LinearLayout container;
    @BindView(R.id.btnAddProduct)
    Button btnAddProduct;

    QuotationWithConfig quotation;//報價單

    QuotationProduct selectedProduct;

    List<Admin> adminList;
    List<ConfigTax> taxList;

    int bg_res = R.drawable.bg_quotation_required;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quotation_contract_create);
        init();
    }

    @Override
    protected void onUserChecked() {
        appbar.invalidate();
        getHiddenField();

        if (quotation != null)
            return;

        String tripId = getIntent().getStringExtra(MyApplication.INTENT_KEY_TRIP);
        if (TextUtils.isEmpty(tripId)) {
            /*新建報價*/
            quotation = new QuotationWithConfig();
            quotation.setQuotation(new Quotation());

            updateProductList();
            final String customerId = getIntent().getStringExtra(MyApplication.INTENT_KEY_CUSTOMER);
            if (!TextUtils.isEmpty(customerId)) {
                mDisposable.add(DatabaseHelper.getInstance(this).getCustomerById(customerId)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Customer>() {
                            @Override
                            public void accept(Customer customer) throws Exception {
                                //選擇客戶
                                itemSelectCustomer.setCustomer(customer);
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                ErrorHandler.getInstance().setException(QuotationCreateActivity.this, throwable);
                            }
                        }));
            }

            String projectId = getIntent().getStringExtra(MyApplication.INTENT_KEY_PROJECT);
            if (!TextUtils.isEmpty(projectId))
            {
                mDisposable.add(DatabaseHelper.getInstance(this).getProjectById(projectId,
                        TokenManager.getInstance().getUser().getDepartment_id(), TokenManager.getInstance().getUser().getCompany_id())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<ProjectWithConfig>() {
                    @Override
                    public void accept(ProjectWithConfig project) throws Exception {
                        //選擇工作項目
                        itemSelectProject.setProject(project);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ErrorHandler.getInstance().setException(QuotationCreateActivity.this, throwable);
                    }
                }));
            }

            getAssistant();//取得銷售助理
            getTax();//取得稅率設定

        } else {
            /*編輯報價*/
            getQuotation(tripId);
        }
    }

    protected void init() {
        containerDelivery.setVisibility(View.GONE);

        itemSelectPeople.disableParticipant();
        itemSelectPeople.setItemSelectCustomer(itemSelectCustomer);
        itemSelectProject.setItemSelectCustomer(itemSelectCustomer);
        itemSelectCustomer.setShouldCheck(true);
        itemSelectCustomer.addCustomerSelectedListener(this);

        appbar.setTitle(getString(R.string.main_menu_new_task) + "/" + getString(R.string
                .apply_quotation));
        appbar.setCompleteListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInput()) {
                    startProgressDialog();
                    quotation.getTrip().setCustomer_id(itemSelectCustomer.getCustomer().getId());
                    quotation.getTrip().setProject_id(itemSelectProject.getProject().getProject().getId());
                    quotation.getTrip().setDate_type(true);
                    quotation.getTrip().setName(etName.getText().toString());
                    quotation.getTrip().setDescription(etNote.getText().toString());

                    if (adminList != null && dropdownAssistant.getSelected() != Dropdown.VALUE_EMPTY)
                    {
                        quotation.getTrip().setAssistant_id(adminList.get(dropdownAssistant.getSelected()).getId());
                    }
                    quotation.getQuotation().setPayment_method(etPayment.getText().toString());
                    quotation.getQuotation().setUnit_type(dropdownUnit.getSelected());
                    quotation.getQuotation().setTax(taxList.get(dropdownTax.getSelected()).getId());
                    quotation.setContacters(itemSelectPeople.getContacters());
                    quotation.setFiles(itemSelectPhoto.getFiles());
                    mDisposable.add(DatabaseHelper.getInstance(QuotationCreateActivity.this).saveQuotation(quotation)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<String>() {
                                @Override
                                public void accept(String s) throws Exception {
                                    stopProgressDialog();
                                    setResult(Activity.RESULT_OK);
                                    finish();
                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable t) throws Exception {
                                    stopProgressDialog();
                                    Toast.makeText(QuotationCreateActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }));
                }
            }
        });
    }

    private void getQuotation(String tripId) {
        mDisposable.add(DatabaseHelper.getInstance(this).getQuotationByTrip(tripId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<QuotationWithConfig>() {
            @Override
            public void accept(QuotationWithConfig result) throws Exception {
                quotation = result;
                getCustomer(quotation.getTrip().getCustomer_id());
                getProject(quotation.getTrip().getProject_id());

                if (!TextUtils.isEmpty(quotation.getTrip().getName()))
                    etName.setText(quotation.getTrip().getName());
                if (!TextUtils.isEmpty(quotation.getQuotation().getPayment_method()))
                    etPayment.setText(quotation.getQuotation().getPayment_method());
                if (!TextUtils.isEmpty(quotation.getTrip().getDescription()))
                    etNote.setText(quotation.getTrip().getDescription());

                dropdownUnit.select(quotation.getQuotation().getUnit_type());
                itemSelectPeople.setContacters(quotation.getContacters());
                itemSelectPhoto.setFiles(quotation.getFiles());
                updateProductList();
                getAssistant();
                getTax();

            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Toast.makeText(QuotationCreateActivity.this, throwable.getMessage(), Toast.LENGTH_LONG).show();
                finish();
            }
        }));
    }

    /*
        *隱藏欄位
        */
    protected void getHiddenField() {
        mDisposable.add(DatabaseHelper.getInstance(this).getConfigCompanyHiddenField
                (TokenManager.getInstance().getUser().getCompany_id())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ConfigCompanyHiddenField>() {
            @Override
            public void accept(ConfigCompanyHiddenField configCompanyHiddenField) throws Exception {
                if (configCompanyHiddenField.getQuotation_hidden_unit_type()) {
                    //隱藏單價保留位數
                    dropdownUnit.setVisibility(View.GONE);
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                ErrorHandler.getInstance().setException(QuotationCreateActivity.this, throwable);
            }
        }));
    }

    /*
        *取得銷售助理
        */
    protected void getAssistant() {
        mDisposable.add(DatabaseHelper.getInstance(this).getAdmin()
                .observeOn(AndroidSchedulers.mainThread()).toList()
                .subscribe(new Consumer<List<Admin>>() {
            @Override
            public void accept(List<Admin> admins) throws Exception {
                adminList = admins;

                if (admins.size() > 0) {
                    String assistantId = TokenManager.getInstance().getUser().getAssistant_id();
                    if (!TextUtils.isEmpty(quotation.getTrip().getAssistant_id())) {
                        assistantId = quotation.getTrip().getAssistant_id();
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
                ErrorHandler.getInstance().setException(QuotationCreateActivity.this, throwable);
            }
        }));
    }

    /**
         * 取得稅率設定
         */
    protected void getTax() {
        mDisposable.add(DatabaseHelper.getInstance(this).getConfigTax()
                .observeOn(AndroidSchedulers.mainThread()).toList()
                .subscribe(new Consumer<List<ConfigTax>>() {
            @Override
            public void accept(List<ConfigTax> config) throws Exception {
                taxList = config;
                if (taxList.size() > 0) {
                    String taxId = "";
                    if (!TextUtils.isEmpty(quotation.getQuotation().getTax())) {
                        taxId = quotation.getQuotation().getTax();
                    }

                    int select = -1;
                    String[] items = new String[taxList.size()];
                    for (int i = 0; i < items.length; i++) {
                        items[i] = taxList.get(i).getName();
                        if (taxList.get(i).getId().equals(taxId)) {
                            select = i;
                        }
                    }

                    dropdownTax.setItems(items);
                    dropdownTax.select(select);
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                ErrorHandler.getInstance().setException(QuotationCreateActivity.this, throwable);
            }
        }));
    }

    /*
        *取得客戶
        */
    protected void getCustomer(String customerId) {
        if (TextUtils.isEmpty(customerId))
        {
            return;
        }

        mDisposable.add(DatabaseHelper.getInstance(this).getCustomerById(customerId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Customer>() {
                    @Override
                    public void accept(Customer customer) throws Exception {
                        itemSelectCustomer.setCustomer(customer);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ErrorHandler.getInstance().setException(QuotationCreateActivity.this, throwable);
                    }
                }));
    }

    /*
        *取得工作項目
        */
    protected void getProject(String projectId) {
        if (TextUtils.isEmpty(projectId))
        {
            return;
        }

        mDisposable.add(DatabaseHelper.getInstance(this).getProjectById(projectId,
                TokenManager.getInstance().getUser().getDepartment_id(), TokenManager.getInstance().getUser().getCompany_id())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ProjectWithConfig>() {

            @Override
            public void accept(ProjectWithConfig projectWithConfig) throws Exception {
                itemSelectProject.setProject(projectWithConfig);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                ErrorHandler.getInstance().setException(QuotationCreateActivity.this, throwable);
            }
        }));
    }

    protected boolean checkInput() {
        String required = getString(R.string.error_msg_required);
        if (TextUtils.isEmpty(etName.getText().toString())) {
            Toast.makeText(this, required + getString(R.string.quotation_name), Toast.LENGTH_LONG).show();
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
        if (quotation.getProducts().size() == 0) {
            Toast.makeText(this, R.string.error_msg_no_product, Toast.LENGTH_LONG).show();
            return false;
        }
        if (dropdownUnit.getVisibility() == View.VISIBLE && dropdownUnit.getSelected() == Dropdown.VALUE_EMPTY) {
            Toast.makeText(this, required + getString(R.string.unit_keep), Toast.LENGTH_LONG).show();
            return false;
        }
        if (dropdownTax.getVisibility() == View.VISIBLE && dropdownTax.getSelected() == Dropdown.VALUE_EMPTY) {
            Toast.makeText(this, getString(R.string.no_select) + getString(R.string.tax_include)+"/"+getString(R.string.tax_type_none), Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    /*新增產品*/
    @OnClick(R.id.btnAddProduct)
    protected void addProduct() {
        Intent intent = new Intent(this, QuotationProductCreateActivity.class);
        startActivityForResult(intent, MyApplication.REQUEST_EDIT_PRODUCT);
    }

    protected View generateProductItem(final QuotationProduct product) {
        final Button btnProduct = new Button(this);
        btnProduct.setText(product.getProduct_model());
        btnProduct.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        btnProduct.setPaddingRelative(UnitChanger.dpToPx(16), 0, 0, 0);
        btnProduct.setBackgroundResource(bg_res);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            btnProduct.setStateListAnimator(null);
        }

        btnProduct.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.mipmap.common_arrow_right, 0);
        btnProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedProduct = product;
                Intent intent = new Intent(QuotationCreateActivity.this, QuotationProductCreateActivity.class);
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
                        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        quotation.getProducts().remove(product);
                                        container.removeView(btnProduct);
                                    }
                                });
                builder.create().show();
                return true;
            }
        });
        return btnProduct;
    }

    /*
        *產品列表
        */
    protected void updateProductList() {
        container.removeAllViews();
        float amount = 0;
        for (QuotationProduct product : quotation.getProducts()) {
            amount += product.getTotal();
            container.addView(generateProductItem(product));
        }
        tvAmount.setText(NumberFormater.getMoneyFormat(amount));
        quotation.getQuotation().setAmount(amount);
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode)
            {
                case MyApplication.REQUEST_EDIT_PRODUCT:
                    QuotationProduct quotationProduct = gson.fromJson(data.getStringExtra(MyApplication.INTENT_KEY_PRODUCT), QuotationProduct.class);
                    if (selectedProduct != null) {
                        quotation.getProducts().remove(selectedProduct);
                        selectedProduct = null;
                    }
                    quotation.getProducts().add(quotationProduct);
                    updateProductList();
                    updateName();
                    break;
                default:
                    itemSelectPhoto.onActivityResult(requestCode, resultCode, data);
                    itemSelectCustomer.onActivityResult(requestCode, resultCode, data);
                    itemSelectProject.onActivityResult(requestCode, resultCode, data);
                    itemSelectPeople.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    @Override
    public void onCustomerSelected(Customer customer) {
        updateName();
    }

    protected void updateName() {
        StringBuilder builder = new StringBuilder();
        builder.append(getString(R.string.apply_quotation));
        if (itemSelectCustomer.getCustomer() != null) {
            builder.append("-").append(itemSelectCustomer.getCustomer().getFull_name());
        }
        if (quotation.getProducts().size() > 0) {
            builder.append("-").append(quotation.getProducts().get(0).getProduct_model());
        }
        etName.setText(builder.toString());
    }
}
