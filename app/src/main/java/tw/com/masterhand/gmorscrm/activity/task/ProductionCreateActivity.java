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
import android.widget.TextView;
import android.widget.Toast;

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
import tw.com.masterhand.gmorscrm.model.ProductionWithConfig;
import tw.com.masterhand.gmorscrm.model.ProjectWithConfig;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.record.Customer;
import tw.com.masterhand.gmorscrm.room.record.NewProjectProduction;
import tw.com.masterhand.gmorscrm.room.setting.ConfigIndustry;
import tw.com.masterhand.gmorscrm.room.setting.ConfigNewProjectProduction;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.tools.TimeFormater;
import tw.com.masterhand.gmorscrm.view.AppbarEdit;
import tw.com.masterhand.gmorscrm.view.Dropdown;
import tw.com.masterhand.gmorscrm.view.EditTextWithTitle;
import tw.com.masterhand.gmorscrm.view.ItemSelectCustomer;
import tw.com.masterhand.gmorscrm.view.ItemSelectDiscount;
import tw.com.masterhand.gmorscrm.view.ItemSelectPhoto;
import tw.com.masterhand.gmorscrm.view.ItemSelectProject;

public class ProductionCreateActivity extends BaseUserCheckActivity implements ItemSelectCustomer
        .CustomerSelectListener {
    @BindView(R.id.appbar)
    AppbarEdit appbar;
    @BindView(R.id.itemSelectCustomer)
    ItemSelectCustomer itemSelectCustomer;
    @BindView(R.id.itemSelectProject)
    ItemSelectProject itemSelectProject;
    @BindView(R.id.etName)
    EditTextWithTitle etName;
    @BindView(R.id.dropdownIndustry)
    Dropdown dropdownIndustry;
    @BindView(R.id.dropdownPSW)
    Dropdown dropdownPSW;
    @BindView(R.id.dropdownCategory)
    Dropdown dropdownCategory;
    @BindView(R.id.etYearAmount)
    EditTextWithTitle etYearAmount;
    @BindView(R.id.etModel)
    EditTextWithTitle etModel;
    @BindView(R.id.tvDate)
    TextView tvDate;
    @BindView(R.id.etAmount)
    EditTextWithTitle etAmount;
    @BindView(R.id.itemSelectDiscount)
    ItemSelectDiscount itemSelectDiscount;
    @BindView(R.id.itemSelectPhoto)
    ItemSelectPhoto itemSelectPhoto;
    @BindView(R.id.etNote)
    EditText etNote;

    ProductionWithConfig production;

    List<ConfigNewProjectProduction> configNewProjectProductions;
    List<ConfigIndustry> configIndustries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_production_create);
        init();
    }

    @Override
    protected void onUserChecked() {
        appbar.invalidate();
        if (production != null)
            return;
        String tripId = getIntent().getStringExtra(MyApplication.INTENT_KEY_TRIP);
        if (TextUtils.isEmpty(tripId)) {
            /*新建量產*/
            production = new ProductionWithConfig(new NewProjectProduction());
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
                                ErrorHandler.getInstance().setException(ProductionCreateActivity
                                                .this,
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
                        ErrorHandler.getInstance().setException(ProductionCreateActivity.this,
                                throwable);
                    }
                }));
            }
            getIndustry();
            getProduct();
        } else {
            /*編輯量產*/
            getProduction(tripId);
        }
    }

    protected void init() {
        itemSelectProject.setItemSelectCustomer(itemSelectCustomer);
        itemSelectCustomer.setShouldCheck(true);
        itemSelectCustomer.addCustomerSelectedListener(this);
        itemSelectDiscount.setTitle(getString(R.string.discount));

        appbar.setTitle(getString(R.string.main_menu_new_task) + "/" + getString(R.string
                .apply_production));
        appbar.setCompleteListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInput()) {
                    startProgressDialog();
                    production.getTrip().setCustomer_id(itemSelectCustomer.getCustomer().getId());
                    production.getTrip().setProject_id(itemSelectProject.getProject().getProject()
                            .getId());
                    production.getTrip().setDate_type(true);
                    production.getTrip().setName(etName.getText().toString());
                    production.getTrip().setDescription(etNote.getText().toString());
                    if (dropdownIndustry.getSelected() != Dropdown.VALUE_EMPTY)
                        production.getProduction().setIndustry_id(configIndustries.get
                                (dropdownIndustry.getSelected()).getId());
                    if (dropdownPSW.getSelected() != Dropdown.VALUE_EMPTY)
                        production.getProduction().setPsw(dropdownPSW.getSelected());
                    if (dropdownCategory.getSelected() != Dropdown.VALUE_EMPTY)
                        production.getProduction().setProduct(configNewProjectProductions.get
                                (dropdownCategory.getSelected()).getId());
                    if (!TextUtils.isEmpty(etYearAmount.getText().toString()))
                        production.getProduction().setYear_amount(Float.parseFloat(etYearAmount
                                .getText().toString()));
                    production.getProduction().setModel(etModel.getText().toString());
                    if (!TextUtils.isEmpty(etAmount.getText().toString()))
                        production.getProduction().setAmount(Float.parseFloat(etAmount.getText()
                                .toString()));
                    production.getProduction().setDiscount(itemSelectDiscount.getDiscount());

                    production.setFiles(itemSelectPhoto.getFiles());
                    mDisposable.add(DatabaseHelper.getInstance(ProductionCreateActivity.this)
                            .saveProduction
                                    (production).observeOn(AndroidSchedulers
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
                                    Toast.makeText(ProductionCreateActivity.this, t.getMessage(),
                                            Toast
                                                    .LENGTH_LONG).show();
                                }
                            }));
                }
            }
        });

    }

    private void getProduction(String tripId) {
        mDisposable.add(DatabaseHelper.getInstance(this).getProductionByTrip(tripId).observeOn
                (AndroidSchedulers.mainThread()).subscribe(new Consumer<ProductionWithConfig>() {
            @Override
            public void accept(ProductionWithConfig result) throws Exception {
                production = result;
                getCustomer(production.getTrip().getCustomer_id());
                getProject(production.getTrip().getProject_id());
                if (!TextUtils.isEmpty(production.getTrip().getName()))
                    etName.setText(production.getTrip().getName());
                if (production.getProduction().getYear_amount() > 0)
                    etYearAmount.setText(String.valueOf(production.getProduction()
                            .getYear_amount()));
                if (production.getProduction().getAmount() > 0)
                    etAmount.setText(String.valueOf(production.getProduction().getAmount()));
                if (!TextUtils.isEmpty(production.getProduction().getModel()))
                    etModel.setText(production.getProduction().getModel());
                if (production.getProduction().getDate() != null)
                    tvDate.setText(TimeFormater.getInstance().toDateFormat(production
                            .getProduction().getDate()));
                itemSelectDiscount.setDiscount(production.getProduction().getDiscount());
                dropdownPSW.select(production.getProduction().getPsw());
                itemSelectPhoto.setFiles(production.getFiles());
                getIndustry();
                getProduct();
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Toast.makeText(ProductionCreateActivity.this, throwable.getMessage(), Toast
                        .LENGTH_LONG).show();
                finish();
            }
        }));
    }

    void getIndustry() {
        mDisposable.add(DatabaseHelper.getInstance(this).getConfigIndustry().toList().observeOn
                (AndroidSchedulers.mainThread()).subscribe(new Consumer<List<ConfigIndustry>>() {

            @Override
            public void accept(List<ConfigIndustry> configs) throws Exception {
                if (configs == null)
                    return;
                int index = Dropdown.VALUE_EMPTY;
                configIndustries = configs;
                String[] items = new String[configIndustries.size()];
                for (int i = 0; i < items.length; i++) {
                    ConfigIndustry config = configIndustries.get(i);
                    items[i] = config.getName();
                    if (config.getId().equals(production.getProduction().getIndustry_id()))
                        index = i;
                }
                dropdownIndustry.setItems(items);
                if (index != Dropdown.VALUE_EMPTY)
                    dropdownIndustry.select(index);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                ErrorHandler.getInstance().setException(ProductionCreateActivity.this,
                        throwable);
            }
        }));
    }

    void getProduct() {
        mDisposable.add(DatabaseHelper.getInstance(this).getConfigNewProjectProduction().toList()
                .observeOn
                        (AndroidSchedulers.mainThread()).subscribe(new Consumer<List<ConfigNewProjectProduction>>() {

                    @Override
                    public void accept(List<ConfigNewProjectProduction> configs) throws Exception {
                        if (configs == null)
                            return;
                        int index = Dropdown.VALUE_EMPTY;
                        configNewProjectProductions = configs;
                        String[] items = new String[configNewProjectProductions.size()];
                        for (int i = 0; i < items.length; i++) {
                            ConfigNewProjectProduction config = configNewProjectProductions.get(i);
                            items[i] = config.getName();
                            if (config.getId().equals(production.getProduction().getProduct()))
                                index = i;
                        }
                        dropdownCategory.setItems(items);
                        if (index != Dropdown.VALUE_EMPTY)
                            dropdownCategory.select(index);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ErrorHandler.getInstance().setException(ProductionCreateActivity.this,
                                throwable);
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
                        ErrorHandler.getInstance().setException(ProductionCreateActivity.this,
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
                ErrorHandler.getInstance().setException(ProductionCreateActivity.this,
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
        if (TextUtils.isEmpty(etYearAmount.getText().toString())) {
            Toast.makeText(this, required + getString(R.string.production_year_amount), Toast
                    .LENGTH_LONG).show();
            return false;
        }
        if (TextUtils.isEmpty(etModel.getText().toString())) {
            Toast.makeText(this, required + getString(R.string.production_model), Toast
                    .LENGTH_LONG).show();
            return false;
        }
        if (production.getProduction().getDate() == null) {
            Toast.makeText(this, required + getString(R.string.production_date), Toast
                    .LENGTH_LONG).show();
            return false;
        }
        if (TextUtils.isEmpty(etAmount.getText().toString())) {
            Toast.makeText(this, required + getString(R.string.production_amount), Toast
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
        if (dropdownIndustry.getSelected() == Dropdown.VALUE_EMPTY) {
            Toast.makeText(this, required + getString(R.string.industry), Toast
                    .LENGTH_LONG).show();
            return false;
        }
        if (dropdownPSW.getSelected() == Dropdown.VALUE_EMPTY) {
            Toast.makeText(this, required + getString(R.string.production_psw), Toast
                    .LENGTH_LONG).show();
            return false;
        }
        if (dropdownCategory.getSelected() == Dropdown.VALUE_EMPTY) {
            Toast.makeText(this, required + getString(R.string.product_category), Toast
                    .LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    @OnClick(R.id.tvDate)
    void changeDate() {
        final Calendar toBeChanged = Calendar.getInstance(Locale.getDefault());
        if (production.getProduction().getDate() != null)
            toBeChanged.setTime(production.getProduction().getDate());
        DatePickerDialog datePicker = new DatePickerDialog(this, new DatePickerDialog
                .OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                toBeChanged.set(Calendar.YEAR, year);
                toBeChanged.set(Calendar.MONTH, month);
                toBeChanged.set(Calendar.DAY_OF_MONTH, day);
                production.getProduction().setDate(toBeChanged.getTime());
                tvDate.setText(TimeFormater.getInstance().toDateFormat(production.getProduction()
                        .getDate()));
                tvDate.setTextColor(ContextCompat.getColor(ProductionCreateActivity.this, R
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
        }
    }

    @Override
    public void onCustomerSelected(Customer customer) {
        updateName();
    }

    protected void updateName() {
        StringBuilder builder = new StringBuilder();
        builder.append(getString(R.string.apply_production));
        if (itemSelectCustomer.getCustomer() != null) {
            builder.append("-").append(itemSelectCustomer.getCustomer().getFull_name());
        }
        etName.setText(builder.toString());
    }
}
