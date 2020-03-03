package tw.com.masterhand.gmorscrm.activity.task;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import tw.com.masterhand.gmorscrm.BaseUserCheckActivity;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.TokenManager;
import tw.com.masterhand.gmorscrm.enums.ExpressType;
import tw.com.masterhand.gmorscrm.model.ExpressWithConfig;
import tw.com.masterhand.gmorscrm.model.ProjectWithConfig;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.record.Customer;
import tw.com.masterhand.gmorscrm.room.record.Express;
import tw.com.masterhand.gmorscrm.room.setting.Admin;
import tw.com.masterhand.gmorscrm.room.setting.ConfigCompanyHiddenField;
import tw.com.masterhand.gmorscrm.room.setting.ConfigExpressDestination;
import tw.com.masterhand.gmorscrm.room.setting.ConfigQuotationProductCategory;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.tools.TimeFormater;
import tw.com.masterhand.gmorscrm.view.AppbarEdit;
import tw.com.masterhand.gmorscrm.view.Dropdown;
import tw.com.masterhand.gmorscrm.view.EditTextWithTitle;
import tw.com.masterhand.gmorscrm.view.ItemSelectCustomer;
import tw.com.masterhand.gmorscrm.view.ItemSelectPeople;
import tw.com.masterhand.gmorscrm.view.ItemSelectPhoto;
import tw.com.masterhand.gmorscrm.view.ItemSelectProject;

public class ExpressCreateActivity extends BaseUserCheckActivity implements ItemSelectCustomer
        .CustomerSelectListener {
    @BindView(R.id.appbar)
    AppbarEdit appbar;
    @BindView(R.id.itemSelectCustomer)
    ItemSelectCustomer itemSelectCustomer;
    @BindView(R.id.itemSelectProject)
    ItemSelectProject itemSelectProject;
    @BindView(R.id.etName)
    EditTextWithTitle etName;

    @BindView(R.id.dropdownType)
    Dropdown dropdownType;
    @BindView(R.id.dropdownCategory)
    Dropdown dropdownCategory;
    @BindView(R.id.dropdownArrivalPayment)
    Dropdown dropdownArrivalPayment;
    @BindView(R.id.etOrigin)
    EditTextWithTitle etOrigin;
    @BindView(R.id.tvDate)
    TextView tvDate;
    @BindView(R.id.containerDate)
    RelativeLayout containerDate;

    @BindView(R.id.etContract)
    EditTextWithTitle etContract;
    @BindView(R.id.etModel)
    EditText etModel;
    @BindView(R.id.etAmount)
    EditTextWithTitle etAmount;
    @BindView(R.id.etExpense)
    EditTextWithTitle etExpense;
    @BindView(R.id.dropdownDestination)
    Dropdown dropdownDestination;
    @BindView(R.id.etReason)
    EditText etReason;
    @BindView(R.id.itemSelectPeople)
    ItemSelectPeople itemSelectPeople;

    @BindView(R.id.dropdownAssistant)
    Dropdown dropdownAssistant;
    @BindView(R.id.itemSelectPhoto)
    ItemSelectPhoto itemSelectPhoto;
    @BindView(R.id.etNote)
    EditText etNote;

    ExpressWithConfig express;

    List<Admin> adminList;
    List<ConfigQuotationProductCategory> categories;
    List<ConfigExpressDestination> destinations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_express_create);
        init();
    }

    @Override
    protected void onUserChecked() {
        appbar.invalidate();
        getHiddenField();
        if (express != null)
            return;
        String tripId = getIntent().getStringExtra(MyApplication.INTENT_KEY_TRIP);
        if (TextUtils.isEmpty(tripId)) {
            /*新建出差*/
            express = new ExpressWithConfig(new Express());
            final String customerId = getIntent().getStringExtra(MyApplication.INTENT_KEY_CUSTOMER);
            if (!TextUtils.isEmpty(customerId)) {
                mDisposable.add(DatabaseHelper.getInstance(this).getCustomerById(customerId)
                        .observeOn
                                (AndroidSchedulers.mainThread()).subscribe(new Consumer<Customer>
                                () {

                            @Override
                            public void accept(Customer customer) throws Exception {
                                itemSelectCustomer.setCustomer(customer);
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                ErrorHandler.getInstance().setException(ExpressCreateActivity.this,
                                        throwable);
                            }
                        }));
            }
            String projectId = getIntent().getStringExtra(MyApplication.INTENT_KEY_PROJECT);
            if (!TextUtils.isEmpty(projectId)) {
                mDisposable.add(DatabaseHelper.getInstance(this).getProjectById(projectId,
                        TokenManager
                                .getInstance().getUser().getDepartment_id(), TokenManager
                                .getInstance()
                                .getUser().getCompany_id()).observeOn
                        (AndroidSchedulers.mainThread()).subscribe(new Consumer<ProjectWithConfig>() {

                    @Override
                    public void accept(ProjectWithConfig project) throws Exception {
                        itemSelectProject.setProject(project);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ErrorHandler.getInstance().setException(ExpressCreateActivity.this,
                                throwable);
                    }
                }));
            }
            getAssistant();
            getProductCategory();
            getDestination();
        } else {
            /*編輯出差*/
            getExpress(tripId);
        }
    }

    protected void init() {
        itemSelectProject.setItemSelectCustomer(itemSelectCustomer);
        itemSelectCustomer.setShouldCheck(true);
        itemSelectCustomer.addCustomerSelectedListener(this);
        itemSelectPeople.disableParticipant();
        itemSelectPeople.setItemSelectCustomer(itemSelectCustomer);

        appbar.setTitle(getString(R.string.main_menu_new_task) + "/" + getString(R.string
                .apply_express));
        appbar.setCompleteListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInput()) {
                    startProgressDialog();
                    express.getTrip().setCustomer_id(itemSelectCustomer.getCustomer().getId());
                    express.getTrip().setProject_id(itemSelectProject.getProject().getProject()
                            .getId());
                    express.getTrip().setName(etName.getText().toString());
                    express.getTrip().setDescription(etNote.getText().toString());
                    if (adminList != null && dropdownAssistant.getSelected() != Dropdown
                            .VALUE_EMPTY) {
                        express.getTrip().setAssistant_id(adminList.get(dropdownAssistant
                                .getSelected()).getId());
                    }
                    if (dropdownType.getSelected() != Dropdown.VALUE_EMPTY)
                        express.getExpress().setType(ExpressType.getTypeByCode(dropdownType
                                .getSelected()));
                    express.getExpress().setContract_number(etContract.getText().toString());
                    express.getExpress().setModel(etModel.getText().toString());
                    if (!TextUtils.isEmpty(etAmount.getText().toString()))
                        express.getExpress().setAmount(Float.parseFloat(etAmount.getText()
                                .toString()));
                    if (!TextUtils.isEmpty(etExpense.getText().toString()))
                        express.getExpress().setExpense(Float.parseFloat(etExpense.getText()
                                .toString()));
                    if (dropdownDestination.getSelected() != Dropdown.VALUE_EMPTY)
                        express.getExpress().setDestination(destinations.get(dropdownDestination
                                .getSelected()).getId());
                    if (dropdownCategory.getSelected() != Dropdown.VALUE_EMPTY)
                        express.getExpress().setProduct_category_id(categories.get
                                (dropdownCategory.getSelected()).getId());
                    express.getExpress().setOrigin(etOrigin.getText().toString());
                    if (dropdownArrivalPayment.getSelected() != Dropdown.VALUE_EMPTY)
                        express.getExpress().setArrival_payment(dropdownArrivalPayment
                                .getSelected());
                    express.getExpress().setReason(etReason.getText().toString());

                    express.setContacters(itemSelectPeople.getContacters());
                    express.setFiles(itemSelectPhoto.getFiles());
                    mDisposable.add(DatabaseHelper.getInstance(ExpressCreateActivity.this)
                            .saveExpress(express)
                            .observeOn(AndroidSchedulers
                                    .mainThread())
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
                                    Toast.makeText(ExpressCreateActivity.this, t.getMessage(),
                                            Toast.LENGTH_LONG).show();
                                }
                            }));
                }
            }
        });

    }

    private void getExpress(String tripId) {
        mDisposable.add(DatabaseHelper.getInstance(this).getExpressByTrip(tripId).observeOn
                (AndroidSchedulers.mainThread()).subscribe(new Consumer<ExpressWithConfig>() {
            @Override
            public void accept(ExpressWithConfig result) throws Exception {
                express = result;
                getCustomer(express.getTrip().getCustomer_id());
                getProject(express.getTrip().getProject_id());
                if (!TextUtils.isEmpty(express.getTrip().getName()))
                    etName.setText(express.getTrip().getName());
                dropdownType.select(express.getExpress().getType().getCode());
                if (!TextUtils.isEmpty(express.getExpress().getContract_number()))
                    etContract.setText(express.getExpress().getContract_number());
                if (!TextUtils.isEmpty(express.getExpress().getModel()))
                    etModel.setText(express.getExpress().getModel());
                if (express.getExpress().getAmount() > 0)
                    etAmount.setText(String.valueOf(express.getExpress().getAmount()));
                if (express.getExpress().getExpense() > 0)
                    etExpense.setText(String.valueOf(express.getExpress().getExpense()));
                if (!TextUtils.isEmpty(express.getExpress().getReason()))
                    etReason.setText(express.getExpress().getReason());

                itemSelectPeople.setContacters(express.getContacters());
                itemSelectPhoto.setFiles(express.getFiles());
                if (!TextUtils.isEmpty(express.getTrip().getDescription()))
                    etNote.setText(express.getTrip().getDescription());
                getAssistant();
                getProductCategory();
                getDestination();
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Toast.makeText(ExpressCreateActivity.this, throwable.getMessage(), Toast
                        .LENGTH_LONG).show();
                finish();
            }
        }));
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
                if (configCompanyHiddenField.getExpress_hidden_product_category_id()) {
                    dropdownCategory.setVisibility(View.GONE);
                }
                if (configCompanyHiddenField.getExpress_hidden_origin()) {
                    etOrigin.setVisibility(View.GONE);
                }
                if (configCompanyHiddenField.getExpress_hidden_arrival_payment()) {
                    dropdownArrivalPayment.setVisibility(View.GONE);
                }
                if (configCompanyHiddenField.getExpress_hidden_repayment_date()) {
                    containerDate.setVisibility(View.GONE);
                }
                if (configCompanyHiddenField.getExpress_hidden_type()) {
                    dropdownType.setVisibility(View.GONE);
                }
                if (configCompanyHiddenField.getExpress_hidden_model()) {
                    etModel.setVisibility(View.GONE);
                }
                if (configCompanyHiddenField.getExpress_hidden_expense()) {
                    etExpense.setVisibility(View.GONE);
                }
                if (configCompanyHiddenField.getExpress_hidden_destination()) {
                    dropdownDestination.setVisibility(View.GONE);
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                ErrorHandler.getInstance().setException(ExpressCreateActivity.this,
                        throwable);
            }
        }));
    }

    protected void getAssistant() {
        mDisposable.add(DatabaseHelper.getInstance(this).getAdmin().observeOn(AndroidSchedulers
                .mainThread()).toList().subscribe(new Consumer<List<Admin>>() {
            @Override
            public void accept(List<Admin> admins) throws Exception {
                adminList = admins;
                if (admins.size() > 0) {
                    String assistantId = TokenManager.getInstance().getUser().getAssistant_id();
                    if (!TextUtils.isEmpty(express.getTrip().getAssistant_id())) {
                        assistantId = express.getTrip().getAssistant_id();
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
                ErrorHandler.getInstance().setException(ExpressCreateActivity.this, throwable);
            }
        }));
    }

    protected void getProductCategory() {
        mDisposable.add(DatabaseHelper.getInstance(this)
                .getConfigQuotationProductCategoryByCompany()
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<ConfigQuotationProductCategory>>() {

                    @Override
                    public void accept(List<ConfigQuotationProductCategory>
                                               configQuotationProductCategories) throws Exception {
                        categories = configQuotationProductCategories;
                        String[] items = new String[categories.size()];
                        int index = Dropdown.VALUE_EMPTY;
                        for (int i = 0; i < categories.size(); i++) {
                            items[i] = categories.get(i).getName();
                            if (categories.get(i).getId().equals(express.getExpress()
                                    .getProduct_category_id()))
                                index = i;
                        }
                        dropdownCategory.setItems(items);
                        if (index != Dropdown.VALUE_EMPTY)
                            dropdownCategory.select(index);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        categories = new ArrayList<>();
                    }
                }));
    }

    protected void getDestination() {
        mDisposable.add(DatabaseHelper.getInstance(this)
                .getConfigExpressDestination()
                .toList().observeOn
                        (AndroidSchedulers.mainThread()).subscribe(new Consumer<List<ConfigExpressDestination>>() {

                    @Override
                    public void accept(List<ConfigExpressDestination>
                                               configExpressDestinationList) throws Exception {
                        destinations = configExpressDestinationList;
                        String[] items = new String[destinations.size()];
                        int index = Dropdown.VALUE_EMPTY;
                        for (int i = 0; i < destinations.size(); i++) {
                            items[i] = destinations.get(i).getName();
                            if (destinations.get(i).getId().equals(express.getExpress()
                                    .getDestination()))
                                index = i;
                        }
                        dropdownDestination.setItems(items);
                        if (index != Dropdown.VALUE_EMPTY)
                            dropdownDestination.select(index);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        destinations = new ArrayList<>();
                    }
                }));
    }

    protected void getCustomer(String customerId) {
        if (TextUtils.isEmpty(customerId))
            return;
        mDisposable.add(DatabaseHelper.getInstance(this).getCustomerById(customerId).observeOn
                (AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Customer>() {
                    @Override
                    public void accept(Customer customer) throws Exception {
                        itemSelectCustomer.setCustomer(customer);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ErrorHandler.getInstance().setException(ExpressCreateActivity.this,
                                throwable);
                    }
                }));
    }

    protected void getProject(String projectId) {
        if (TextUtils.isEmpty(projectId))
            return;
        mDisposable.add(DatabaseHelper.getInstance(this).getProjectById(projectId, TokenManager
                .getInstance()
                .getUser().getDepartment_id(), TokenManager.getInstance().getUser().getCompany_id
                ()).observeOn
                (AndroidSchedulers.mainThread()).subscribe(new Consumer<ProjectWithConfig>() {

            @Override
            public void accept(ProjectWithConfig projectWithConfig) throws
                    Exception {
                itemSelectProject.setProject(projectWithConfig);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                ErrorHandler.getInstance().setException(ExpressCreateActivity.this,
                        throwable);
            }
        }));
    }

    protected boolean checkInput() {
        String required = getString(R.string.error_msg_required);
        if (TextUtils.isEmpty(etName.getText().toString())) {
            Toast.makeText(this, required + getString(R.string.production_name), Toast
                    .LENGTH_LONG).show();
            return false;
        }
        if (dropdownType.getVisibility() == View.VISIBLE && dropdownType.getSelected() ==
                Dropdown.VALUE_EMPTY) {
            Toast.makeText(this, required + getString(R.string.express_type), Toast
                    .LENGTH_LONG).show();
            return false;
        }
        if (etContract.getVisibility() == View.VISIBLE && TextUtils.isEmpty(etContract.getText()
                .toString())) {
            Toast.makeText(this, required + getString(R.string.express_contract), Toast
                    .LENGTH_LONG).show();
            return false;
        }
        if (etModel.getVisibility() == View.VISIBLE && TextUtils.isEmpty(etModel.getText()
                .toString())) {
            Toast.makeText(this, required + getString(R.string.express_model), Toast
                    .LENGTH_LONG).show();
            return false;
        }
        if (etAmount.getVisibility() == View.VISIBLE && TextUtils.isEmpty(etAmount.getText()
                .toString())) {
            Toast.makeText(this, required + getString(R.string.express_amount), Toast
                    .LENGTH_LONG).show();
            return false;
        }
        if (etExpense.getVisibility() == View.VISIBLE && TextUtils.isEmpty(etExpense.getText()
                .toString())) {
            Toast.makeText(this, required + getString(R.string.express_expense), Toast
                    .LENGTH_LONG).show();
            return false;
        }
        if (dropdownDestination.getVisibility() == View.VISIBLE && dropdownDestination
                .getSelected() == Dropdown.VALUE_EMPTY) {
            Toast.makeText(this, required + getString(R.string.express_destination), Toast
                    .LENGTH_LONG).show();
            return false;
        }
        if (etReason.getVisibility() == View.VISIBLE && TextUtils.isEmpty(etReason.getText()
                .toString())) {
            Toast.makeText(this, required + getString(R.string.express_reason), Toast
                    .LENGTH_LONG).show();
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
        if (dropdownCategory.getVisibility() == View.VISIBLE && dropdownCategory.getSelected() ==
                Dropdown.VALUE_EMPTY) {
            Toast.makeText(this, required + getString(R.string.product_category), Toast
                    .LENGTH_LONG).show();
            return false;
        }
        if (etOrigin.getVisibility() == View.VISIBLE && TextUtils.isEmpty(etOrigin.getText()
                .toString())) {
            Toast.makeText(this, required + getString(R.string.express_origin), Toast
                    .LENGTH_LONG).show();
            return false;
        }
        if (dropdownArrivalPayment.getVisibility() == View.VISIBLE && dropdownArrivalPayment
                .getSelected() == Dropdown.VALUE_EMPTY) {
            Toast.makeText(this, required + getString(R.string.express_arrival_payment), Toast
                    .LENGTH_LONG).show();
            return false;
        }
        if (containerDate.getVisibility() == View.VISIBLE && express.getExpress()
                .getRepayment_date() == null) {
            Toast.makeText(this, required + getString(R.string.express_repayment_date), Toast
                    .LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    @OnClick(R.id.tvDate)
    void changeDate() {
        final Calendar toBeChanged = Calendar.getInstance(Locale.getDefault());
        if (express.getExpress().getRepayment_date() != null)
            toBeChanged.setTime(express.getExpress().getRepayment_date());
        DatePickerDialog datePicker = new DatePickerDialog(this, new DatePickerDialog
                .OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                toBeChanged.set(Calendar.YEAR, year);
                toBeChanged.set(Calendar.MONTH, month);
                toBeChanged.set(Calendar.DAY_OF_MONTH, day);
                express.getExpress().setRepayment_date(toBeChanged.getTime());
                tvDate.setText(TimeFormater.getInstance().toDateFormat(express.getExpress()
                        .getRepayment_date()));
                tvDate.setTextColor(ContextCompat.getColor(ExpressCreateActivity.this, R
                        .color.orange));
            }
        }, toBeChanged.get(Calendar.YEAR), toBeChanged.get(Calendar.MONTH), toBeChanged.get
                (Calendar.DAY_OF_MONTH));
        datePicker.show();
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            itemSelectPhoto.onActivityResult(requestCode, resultCode, data);
            itemSelectCustomer.onActivityResult(requestCode, resultCode, data);
            itemSelectProject.onActivityResult(requestCode, resultCode, data);
            itemSelectPeople.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onCustomerSelected(Customer customer) {
        updateName();
    }

    protected void updateName() {
        StringBuilder builder = new StringBuilder();
        builder.append(getString(R.string.apply_express));
        if (itemSelectCustomer.getCustomer() != null) {
            builder.append("-").append(itemSelectCustomer.getCustomer().getFull_name());
        }
        etName.setText(builder.toString());
    }
}
