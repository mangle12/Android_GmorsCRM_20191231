package tw.com.masterhand.gmorscrm.activity.task;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import tw.com.masterhand.gmorscrm.ContactPersonSelectActivity;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.TokenManager;
import tw.com.masterhand.gmorscrm.model.ContractWithConfig;
import tw.com.masterhand.gmorscrm.model.ProjectWithConfig;
import tw.com.masterhand.gmorscrm.model.QuotationWithConfig;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.record.ContactPerson;
import tw.com.masterhand.gmorscrm.room.record.Contacter;
import tw.com.masterhand.gmorscrm.room.record.Customer;
import tw.com.masterhand.gmorscrm.room.record.QuotationProduct;
import tw.com.masterhand.gmorscrm.room.setting.Admin;
import tw.com.masterhand.gmorscrm.room.setting.ConfigCompanyHiddenField;
import tw.com.masterhand.gmorscrm.room.setting.ConfigTax;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.tools.NumberFormater;
import tw.com.masterhand.gmorscrm.tools.TimeFormater;
import tw.com.masterhand.gmorscrm.view.Dropdown;
import tw.com.masterhand.gmorscrm.view.ItemSelectCustomer;

public class ContractCreateActivity extends QuotationCreateActivity implements ItemSelectCustomer.CustomerSelectListener {
    @BindView(R.id.tvReceiver)
    TextView tvReceiver;
    @BindView(R.id.tvDeliveryDate)
    TextView tvDeliveryDate;

    ContractWithConfig contract;

    @Override
    protected void onUserChecked() {
        appbar.invalidate();
        getHiddenField();

        if (contract != null)
            return;

        String tripId = getIntent().getStringExtra(MyApplication.INTENT_KEY_TRIP);
        if (TextUtils.isEmpty(tripId)) {
            String quatationId = getIntent().getStringExtra(MyApplication.INTENT_KEY_QUOTATION);
            if (TextUtils.isEmpty(quatationId)) {
                /*新建合約*/
                contract = new ContractWithConfig();
                tvDeliveryDate.setText(TimeFormater.getInstance().toDateFormat(contract.getContract().getDelivery_date()));

                String customerId = getIntent().getStringExtra(MyApplication.INTENT_KEY_CUSTOMER);
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
                                    ErrorHandler.getInstance().setException(ContractCreateActivity.this, throwable);
                                }
                            }));
                }

                String projectId = getIntent().getStringExtra(MyApplication.INTENT_KEY_PROJECT);
                if (!TextUtils.isEmpty(projectId)) {
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
                            ErrorHandler.getInstance().setException(ContractCreateActivity.this, throwable);
                        }
                    }));
                }

                getAssistant();//取得銷售助理
                getTax();//取得稅率設定
            } else {
                /*報價轉合約*/
                getQuotation(quatationId);
            }
        } else {
            /*編輯合約*/
            getContract(tripId);
        }
        itemSelectProject.setItemSelectCustomer(itemSelectCustomer);
        itemSelectPeople.setItemSelectCustomer(itemSelectCustomer);
    }

    @Override
    protected void init() {
        bg_res = R.drawable.bg_contract_required;
        etName.setTitle(getString(R.string.contract_name));
        tvAmountTitle.setText(R.string.contract_amount);
        etName.setBackgroundResource(bg_res);
        etPayment.setBackgroundResource(bg_res);
        dropdownTax.setColor(ContextCompat.getColor(this, R.color.contract));
        dropdownUnit.setColor(ContextCompat.getColor(this, R.color.contract));
        dropdownAssistant.setColor(ContextCompat.getColor(this, R.color.contract));

        itemSelectPeople.disableParticipant();
        itemSelectPeople.setItemSelectCustomer(itemSelectCustomer);
        itemSelectProject.setItemSelectCustomer(itemSelectCustomer);
        itemSelectCustomer.setShouldCheck(true);
        itemSelectCustomer.addCustomerSelectedListener(this);

        appbar.setTitle(getString(R.string.main_menu_new_task) + "/" + getString(R.string.apply_contract));

        appbar.setCompleteListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 完成
                if (checkInput()) {
                    startProgressDialog();
                    contract.getTrip().setCustomer_id(itemSelectCustomer.getCustomer().getId());
                    contract.getTrip().setProject_id(itemSelectProject.getProject().getProject().getId());
                    contract.getTrip().setDate_type(true);
                    contract.getTrip().setName(etName.getText().toString());
                    contract.getTrip().setDescription(etNote.getText().toString());

                    if (adminList != null && dropdownAssistant.getSelected() != Dropdown.VALUE_EMPTY) {
                        contract.getTrip().setAssistant_id(adminList.get(dropdownAssistant.getSelected()).getId());
                    }

                    contract.getContract().setPayment_method(etPayment.getText().toString());
                    contract.getContract().setUnit_type(dropdownUnit.getSelected());
                    contract.getContract().setTax(taxList.get(dropdownTax.getSelected()).getId());
                    contract.setContacters(itemSelectPeople.getContacters());
                    contract.setFiles(itemSelectPhoto.getFiles());
                    mDisposable.add(DatabaseHelper.getInstance(ContractCreateActivity.this).saveContract(contract)
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
                                    Toast.makeText(ContractCreateActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }));
                }
            }
        });

    }

    /*
         *隱藏欄位
         */
    @Override
    protected void getHiddenField() {
        super.getHiddenField();
        mDisposable.add(DatabaseHelper.getInstance(this).getConfigCompanyHiddenField(TokenManager.getInstance().getUser().getCompany_id())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ConfigCompanyHiddenField>
                () {

            @Override
            public void accept(ConfigCompanyHiddenField configCompanyHiddenField) throws
                    Exception {
                if (configCompanyHiddenField.getQuotation_hidden_unit_type()) {
                    dropdownUnit.setVisibility(View.GONE);
                }
                if (configCompanyHiddenField.getContract_hidden_total()) {
                    containerAmount.setVisibility(View.GONE);
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                ErrorHandler.getInstance().setException(ContractCreateActivity.this, throwable);
            }
        }));
    }

    private void getQuotation(String quatationId) {
        mDisposable.add(DatabaseHelper.getInstance(this).getQuotationById(quatationId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<QuotationWithConfig>() {

            @Override
            public void accept(QuotationWithConfig quotationWithConfig) throws Exception {
                contract = quotationWithConfig.transferToContract();
                updateDetail();
                getAssistant();
                getTax();
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                ErrorHandler.getInstance().setException(ContractCreateActivity.this, throwable);
            }
        }));
    }

    /*
         *取得銷售助理
         */
    @Override
    protected void getAssistant() {
        mDisposable.add(DatabaseHelper.getInstance(this).getAdmin()
                .observeOn(AndroidSchedulers.mainThread()).toList()
                .subscribe(new Consumer<List<Admin>>() {
            @Override
            public void accept(List<Admin> admins) throws Exception {
                adminList = admins;
                if (admins.size() > 0) {
                    String assistantId = TokenManager.getInstance().getUser().getAssistant_id();
                    if (!TextUtils.isEmpty(contract.getTrip().getAssistant_id())) {
                        assistantId = contract.getTrip().getAssistant_id();
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
                ErrorHandler.getInstance().setException(ContractCreateActivity.this, throwable);
            }
        }));
    }

    /*
         * 取得稅率設定
         */
    @Override
    protected void getTax() {
        mDisposable.add(DatabaseHelper.getInstance(this).getConfigTax()
                .observeOn(AndroidSchedulers.mainThread()).toList()
                .subscribe(new Consumer<List<ConfigTax>>() {
            @Override
            public void accept(List<ConfigTax> config) throws Exception {
                taxList = config;
                if (taxList.size() > 0) {
                    String taxId = "";
                    if (!TextUtils.isEmpty(contract.getContract().getTax())) {
                        taxId = contract.getContract().getTax();
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
                ErrorHandler.getInstance().setException(ContractCreateActivity.this, throwable);
            }
        }));
    }

    /*
        *取得合同
        */
    private void getContract(String tripId) {
        mDisposable.add(DatabaseHelper.getInstance(this).getContractByTrip(tripId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ContractWithConfig>() {
            @Override
            public void accept(ContractWithConfig result) throws Exception {
                contract = result;
                updateDetail();
                getAssistant();
                getTax();
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Toast.makeText(ContractCreateActivity.this, throwable.getMessage(), Toast.LENGTH_LONG).show();
                finish();
            }
        }));
    }

    private void updateDetail() {
        getCustomer(contract.getTrip().getCustomer_id());
        getProject(contract.getTrip().getProject_id());

        if (!TextUtils.isEmpty(contract.getTrip().getName()))
            etName.setText(contract.getTrip().getName());
        if (!TextUtils.isEmpty(contract.getContract().getPayment_method()))
            etPayment.setText(contract.getContract().getPayment_method());

        dropdownUnit.select(contract.getContract().getUnit_type());

        updateProductList();
        if (!TextUtils.isEmpty(contract.getContract().getReceiver())) {
            mDisposable.add(DatabaseHelper.getInstance(this).getContactPersonById(contract.getContract().getReceiver())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<ContactPerson>() {

                        @Override
                        public void accept(ContactPerson contactPerson) throws Exception {
                            tvReceiver.setText(contactPerson.getShowName());
                            tvReceiver.setTextColor(ContextCompat.getColor(ContractCreateActivity.this, R.color.black));
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            ErrorHandler.getInstance().setException(ContractCreateActivity.this, throwable);
                        }
                    }));
        }

        tvAmount.setText(NumberFormater.getMoneyFormat(contract.getContract().getAmount()));
        tvDeliveryDate.setText(TimeFormater.getInstance().toDateFormat(contract.getContract().getDelivery_date()));
        itemSelectPeople.setContacters(contract.getContacters());
        itemSelectPhoto.setFiles(contract.getFiles());

        if (!TextUtils.isEmpty(contract.getTrip().getDescription()))
            etNote.setText(contract.getTrip().getDescription());
    }

    /*
         *產品列表
         */
    @Override
    protected void updateProductList() {
        container.removeAllViews();
        float amount = 0;
        for (QuotationProduct product : contract.getProducts()) {
            amount += product.getTotal();
            container.addView(generateProductItem(product));
        }
        tvAmount.setText(NumberFormater.getMoneyFormat(amount));
        contract.getContract().setAmount(amount);
    }

    @Override
    protected boolean checkInput() {
        String required = getString(R.string.error_msg_required);
        if (TextUtils.isEmpty(etName.getText().toString())) {
            Toast.makeText(this, required + getString(R.string.contract_name), Toast.LENGTH_LONG).show();
            return false;
        }
        if (TextUtils.isEmpty(contract.getContract().getReceiver())) {
            Toast.makeText(this, required + getString(R.string.receiver), Toast.LENGTH_LONG).show();
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
        if (contract.getProducts().size() == 0) {
            Toast.makeText(this, R.string.error_msg_no_product, Toast.LENGTH_LONG).show();
            return false;
        }
        if (dropdownUnit.getVisibility() == View.VISIBLE && dropdownUnit.getSelected() == Dropdown.VALUE_EMPTY) {
            Toast.makeText(this, required + getString(R.string.unit_keep), Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case MyApplication.REQUEST_EDIT_PRODUCT:
                    QuotationProduct quotationProduct = gson.fromJson(data.getStringExtra(MyApplication.INTENT_KEY_PRODUCT), QuotationProduct.class);

                    if (selectedProduct != null) {
                        contract.getProducts().remove(selectedProduct);
                        selectedProduct = null;
                    }
                    contract.getProducts().add(quotationProduct);
                    updateProductList();
                    updateName();
                    break;
                case MyApplication.REQUEST_SELECT_RECEIVER:
                    ArrayList<Contacter> contacters = gson.fromJson(data.getStringExtra(MyApplication.INTENT_KEY_PEOPLE), new TypeToken<ArrayList<Contacter>>() {
                    }.getType());

                    if (contacters.size() > 0) {
                        Contacter contacter = contacters.get(0);
                        contract.getContract().setReceiver(contacter.getUser_id());
                        updateDetail();
                    }
                    break;
                default:
                    itemSelectPhoto.onActivityResult(requestCode, resultCode, data);
                    itemSelectCustomer.onActivityResult(requestCode, resultCode, data);
                    itemSelectProject.onActivityResult(requestCode, resultCode, data);
                    itemSelectPeople.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    /*
        *收貨人
        */
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

    /**
         * 要求交貨日
         */
    @OnClick(R.id.tvDeliveryDate)
    void changeDeliveryDate() {
        final Calendar toBeChanged = Calendar.getInstance(Locale.getDefault());
        toBeChanged.setTime(contract.getContract().getDelivery_date());

        DatePickerDialog datePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                toBeChanged.set(Calendar.YEAR, year);
                toBeChanged.set(Calendar.MONTH, month);
                toBeChanged.set(Calendar.DAY_OF_MONTH, day);
                contract.getContract().setDelivery_date(toBeChanged.getTime());
                tvDeliveryDate.setText(TimeFormater.getInstance().toDateFormat(contract.getContract().getDelivery_date()));
            }
        }, toBeChanged.get(Calendar.YEAR), toBeChanged.get(Calendar.MONTH), toBeChanged.get(Calendar.DAY_OF_MONTH));

        datePicker.getDatePicker().setMinDate(new Date().getTime());
        datePicker.show();
    }

    @Override
    protected void updateName() {
        StringBuilder builder = new StringBuilder();
        builder.append(getString(R.string.apply_contract));
        if (itemSelectCustomer.getCustomer() != null) {
            builder.append("-").append(itemSelectCustomer.getCustomer().getFull_name());
        }
        if (contract.getProducts().size() > 0) {
            builder.append("-").append(contract.getProducts().get(0).getProduct_model());
        }
        etName.setText(builder.toString());
    }
}
