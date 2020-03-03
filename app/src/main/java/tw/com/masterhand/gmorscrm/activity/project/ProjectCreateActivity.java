package tw.com.masterhand.gmorscrm.activity.project;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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
import tw.com.masterhand.gmorscrm.model.ProjectWithConfig;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.record.Customer;
import tw.com.masterhand.gmorscrm.room.record.Participant;
import tw.com.masterhand.gmorscrm.room.record.Project;
import tw.com.masterhand.gmorscrm.room.setting.ConfigCompanyHiddenField;
import tw.com.masterhand.gmorscrm.room.setting.ConfigProjectSource;
import tw.com.masterhand.gmorscrm.room.setting.ConfigQuotationProductCategory;
import tw.com.masterhand.gmorscrm.room.setting.ConfigSalesOpportunityType;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.tools.TimeFormater;
import tw.com.masterhand.gmorscrm.view.AppbarEdit;
import tw.com.masterhand.gmorscrm.view.Dropdown;
import tw.com.masterhand.gmorscrm.view.EditTextWithTitle;
import tw.com.masterhand.gmorscrm.view.ItemSelectCurrency;
import tw.com.masterhand.gmorscrm.view.ItemSelectCustomer;
import tw.com.masterhand.gmorscrm.view.ItemSelectPeople;
import tw.com.masterhand.gmorscrm.view.ItemSelectPhoto;

public class ProjectCreateActivity extends BaseUserCheckActivity {
    @BindView(R.id.appbar)
    AppbarEdit appbar;
    @BindView(R.id.itemSelectCustomer)
    ItemSelectCustomer itemSelectCustomer;
    @BindView(R.id.editText_title)
    EditTextWithTitle etTitle;
    @BindView(R.id.dropdownCategory)
    Dropdown dropdownCategory;
    @BindView(R.id.dropdownType)
    Dropdown dropdownType;
    @BindView(R.id.dropdownSource)
    Dropdown dropdownSource;
    @BindView(R.id.editText_amount)
    EditText etAmount;
    @BindView(R.id.editText_note)
    EditText etNote;
    @BindView(R.id.btnTime)
    Button btnTime;
    @BindView(R.id.btnCheckTime)
    Button btnCheckTime;
    @BindView(R.id.itemSelectPeople)
    ItemSelectPeople itemSelectPeople;
    @BindView(R.id.itemSelectCurrency)
    ItemSelectCurrency itemSelectCurrency;
    @BindView(R.id.itemSelectPhoto)
    ItemSelectPhoto itemSelectPhoto;

    ProjectWithConfig project;
    List<ConfigQuotationProductCategory> categories;
    List<ConfigSalesOpportunityType> salesTypes;
    List<ConfigProjectSource> sources;

    boolean isLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_create);
        init();
    }

    @Override
    protected void onUserChecked() {
        if (isLoaded)
            return;
        appbar.invalidate();
        getHiddenField();
        String projectId = getIntent().getStringExtra(MyApplication.INTENT_KEY_PROJECT);
        if (!TextUtils.isEmpty(projectId)) {
            /*編輯工作項目*/
            getProject(projectId);
        } else {
            /*新增工作項目*/
            project = new ProjectWithConfig();
            project.setProject(new Project());
            project.getProject().setUser_id(TokenManager.getInstance().getUser().getId());
            if (!TextUtils.isEmpty(TokenManager.getInstance().getUser().getSupervisor_id()))
                project.getProject().setManager_id(TokenManager.getInstance().getUser()
                        .getSupervisor_id());
            Participant participant = new Participant();
            participant.setUser_id(TokenManager.getInstance().getUser().getId());
            itemSelectPeople.addParticipant(participant);
            getProductCategory();
            getSalesType();
            getSalesSource();
            isLoaded = true;
        }
        final String customerId = getIntent().getStringExtra(MyApplication.INTENT_KEY_CUSTOMER);
        if (!TextUtils.isEmpty(customerId)) {
            /*指定公司*/
            mDisposable.add(DatabaseHelper.getInstance(this).getCustomerById(customerId).observeOn
                    (AndroidSchedulers.mainThread()).subscribe(new Consumer<Customer>() {

                @Override
                public void accept(Customer customer) throws Exception {
                    itemSelectCustomer.setCustomer(customer);
                    itemSelectCustomer.disableSelectCustomer();
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    ErrorHandler.getInstance().setException(ProjectCreateActivity.this, throwable);
                }
            }));
        }
    }

    private void init() {
        appbar.setTitle(getString(R.string.title_activity_project_create));
        appbar.setCompleteListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*點擊完成*/
                if (checkInput()) {
                    startProgressDialog();
                    String title = etTitle.getText().toString();
                    String amount = etAmount.getText().toString();
                    String note = etNote.getText().toString();

                    List<String> productIdList = new ArrayList<>();
                    for (int i = 0; i < dropdownCategory.getMultiSelected().length; i++) {
                        if (dropdownCategory.getMultiSelected()[i]) {
                            productIdList.add(categories.get(i).getId());
                        }
                    }
                    project.getProject().setProduct_category_id(productIdList);

                    project.getProject().setName(title);
                    project.getProject().setExpect_amount(Float.valueOf(amount));
                    project.getProject().setDescription(note);
                    project.getProject().setCustomer_id(itemSelectCustomer
                            .getCustomer().getId());
                    project.getProject().setCurrency_id(itemSelectCurrency.getConfigCurrency()
                            .getConfigCurrency().getId());
                    if (itemSelectCurrency.getConfigCurrency().getCurrencyRate() != null)
                        project.getProject().setCurrency_rate_id(itemSelectCurrency
                                .getConfigCurrency
                                        ().getCurrencyRate().getId());
                    if (dropdownType.getSelected() != Dropdown.VALUE_EMPTY)
                        project.getProject().setSales_opportunity_type(salesTypes.get(dropdownType
                                .getSelected()).getId());
                    if (dropdownSource.getSelected() != Dropdown.VALUE_EMPTY)
                        project.getProject().setDepartment_project_source(sources.get(dropdownSource
                                .getSelected()).getId());
                    project.setParticipants(itemSelectPeople.getParticipants());
                    project.setContacters(itemSelectPeople.getContacters());
                    project.setFiles(itemSelectPhoto.getFiles());
                    saveProject();
                }
            }
        });
        etAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().equals(".")) {
                    etAmount.setText("0.");
                }
            }
        });
        itemSelectPeople.setItemSelectCustomer(itemSelectCustomer);
    }


    private void saveProject() {
        mDisposable.add(DatabaseHelper.getInstance(this).saveProject(project).observeOn
                (AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String id) throws Exception {
                        stopProgressDialog();
                        Logger.e(TAG, "project id:" + id);
                        Intent intent = new Intent();
                        intent.putExtra(MyApplication.INTENT_KEY_PROJECT, id);
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable t) throws Exception {
                        stopProgressDialog();
                        Toast.makeText(ProjectCreateActivity.this, t.getMessage(), Toast
                                .LENGTH_LONG).show();
                    }
                }));
    }

    public void getProject(final String projectId) {
        mDisposable.add(DatabaseHelper.getInstance(this).getProjectById(projectId, TokenManager
                .getInstance()
                .getUser().getDepartment_id(), TokenManager.getInstance().getUser().getCompany_id
                ()).observeOn
                (AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ProjectWithConfig>() {

                    @Override
                    public void accept(ProjectWithConfig projectWithConfig) throws Exception {
                        project = projectWithConfig;
                        itemSelectCustomer.setCustomer(project.getCustomer());
                        itemSelectCustomer.disableSelectCustomer();
                        etTitle.setText(project.getProject().getName());
                        etAmount.setText(String.valueOf(project.getProject().getExpect_amount()));
                        etNote.setText(project.getProject().getDescription());
                        itemSelectPeople.setParticipants(project.getParticipants());
                        itemSelectPeople.setContacters(project.getContacters());
                        if (project.getCurrencyRate() != null)
                            itemSelectCurrency.setConfigCurrency(project.getCurrencyRate().getId());
                        itemSelectPhoto.setFiles(project.getFiles());
                        if (project.getProject().getFrom_date() != null)
                            btnTime.setText(TimeFormater.getInstance().toDateFormat(project
                                    .getProject()
                                    .getFrom_date()));
                        if (project.getProject().getCheck_date() != null)
                            btnCheckTime.setText(TimeFormater.getInstance().toDateFormat(project
                                    .getProject()
                                    .getCheck_date()));
                        getProductCategory();
                        getSalesType();
                        getSalesSource();
                        isLoaded = true;
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ErrorHandler.getInstance().setException(ProjectCreateActivity.this,
                                throwable);
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
                if (configCompanyHiddenField.getProject_hidden_sales_opportunity_type())
                    dropdownType.setVisibility(View.GONE);
                if (configCompanyHiddenField.getProject_hidden_department_project_source())
                    dropdownSource.setVisibility(View.GONE);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                ErrorHandler.getInstance().setException(ProjectCreateActivity.this,
                        throwable);
            }
        }));
    }

    void getSalesSource() {
        mDisposable.add(DatabaseHelper.getInstance(this).getConfigProjectSource().toList().observeOn
                (AndroidSchedulers.mainThread()).subscribe(new Consumer<List<ConfigProjectSource>>() {

            @Override
            public void accept(List<ConfigProjectSource> configProjectSources) throws Exception {
                sources = configProjectSources;
                String[] items = new String[sources.size()];
                int index = Dropdown.VALUE_EMPTY;
                for (int i = 0; i < sources.size(); i++) {
                    items[i] = sources.get(i).getName();
                    if (sources.get(i).getId().equals(project.getProject()
                            .getDepartment_project_source()))
                        index = i;
                }
                dropdownSource.setItems(items);
                if (index != Dropdown.VALUE_EMPTY)
                    dropdownSource.select(index);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                ErrorHandler.getInstance().setException(ProjectCreateActivity.this,
                        throwable);
            }
        }));
    }

    void getSalesType() {
        mDisposable.add(DatabaseHelper.getInstance(this).getConfigSalesOpportunityType().toList()
                .observeOn
                        (AndroidSchedulers.mainThread()).subscribe(new Consumer<List<ConfigSalesOpportunityType>>() {


                    @Override
                    public void accept(List<ConfigSalesOpportunityType> configSalesOpportunityTypes)
                            throws Exception {
                        salesTypes = configSalesOpportunityTypes;
                        String[] items = new String[salesTypes.size()];
                        int index = Dropdown.VALUE_EMPTY;
                        for (int i = 0; i < salesTypes.size(); i++) {
                            items[i] = salesTypes.get(i).getName();
                            if (salesTypes.get(i).getId().equals(project.getProject()
                                    .getSales_opportunity_type()))
                                index = i;
                        }
                        dropdownType.setItems(items);
                        if (index != Dropdown.VALUE_EMPTY)
                            dropdownType.select(index);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ErrorHandler.getInstance().setException(ProjectCreateActivity.this,
                                throwable);
                    }
                }));
    }

    void getProductCategory() {
        mDisposable.add(DatabaseHelper.getInstance(this)
                .getConfigQuotationProductCategoryByCompany()
                .toList().observeOn
                        (AndroidSchedulers.mainThread()).subscribe(new Consumer<List<ConfigQuotationProductCategory>>() {

                    @Override
                    public void accept(List<ConfigQuotationProductCategory>
                                               configQuotationProductCategories) throws Exception {
                        categories = configQuotationProductCategories;
                        String[] items = new String[categories.size()];
                        boolean[] selected = new boolean[categories.size()];
                        for (int i = 0; i < categories.size(); i++) {
                            items[i] = categories.get(i).getName();
                            for (String selectedId : project.getProject().getProduct_category_id
                                    ()) {
                                if (selectedId.equals(categories.get(i).getId())) {
                                    selected[i] = true;
                                    break;
                                }
                            }
                        }
                        dropdownCategory.setItems(items);
                        dropdownCategory.enableMultiSelect();
                        dropdownCategory.multiSelect(selected);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ErrorHandler.getInstance().setException(ProjectCreateActivity.this,
                                throwable);
                    }
                }));
    }

    /**
     * 檢查輸入資訊
     */
    private boolean checkInput() {
        String required = getString(R.string.error_msg_required);

        String title = etTitle.getText().toString();
        String amount = etAmount.getText().toString();
        if (itemSelectCustomer.getCustomer() == null) {
            Toast.makeText(this, R.string.error_msg_no_customer, Toast.LENGTH_LONG).show();
            return false;
        }
        if (project.getProject().getFrom_date() == null) {
            Toast.makeText(this, required + getString(R.string.start_time), Toast.LENGTH_LONG)
                    .show();
            return false;
        }
        if (project.getProject().getCheck_date() == null) {
            Toast.makeText(this, required + getString(R.string.check_date), Toast.LENGTH_LONG)
                    .show();
            return false;
        }
        if (TextUtils.isEmpty(title)) {
            Toast.makeText(this, required + getString(R.string.project_name), Toast.LENGTH_LONG)
                    .show();
            return false;
        }
        if (TextUtils.isEmpty(amount)) {
            Toast.makeText(this, required + getString(R.string.estimate_amount), Toast
                    .LENGTH_LONG).show();
            return false;
        }
        if (dropdownType.getVisibility() == View.VISIBLE && dropdownType.getSelected() ==
                Dropdown.VALUE_EMPTY) {
            Toast.makeText(this, required + getString(R.string.sales_type), Toast
                    .LENGTH_LONG).show();
            return false;
        }
        if (dropdownSource.getVisibility() == View.VISIBLE && dropdownSource.getSelected() ==
                Dropdown.VALUE_EMPTY) {
            Toast.makeText(this, required + getString(R.string.sales_source), Toast
                    .LENGTH_LONG).show();
            return false;
        }
        boolean isCategorySelected = false;
        for (int i = 0; i < dropdownCategory.getMultiSelected().length; i++) {
            if (dropdownCategory.getMultiSelected()[i]) {
                isCategorySelected = true;
                break;
            }
        }
        if (!isCategorySelected) {
            Toast.makeText(this, required + getString(R.string.product_category), Toast
                    .LENGTH_LONG).show();
            return false;
        }
        return true;
    }


    /**
     * 變更開始時間
     */
    @OnClick(R.id.btnTime)
    void changeStartTime() {
        Calendar toBeChanged = Calendar.getInstance(Locale.getDefault());
        if (project.getProject().getFrom_date() != null)
            toBeChanged.setTime(project.getProject().getFrom_date());
        DatePickerDialog datePicker = new DatePickerDialog(this, new DatePickerDialog
                .OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                Calendar calendar = Calendar.getInstance(Locale.getDefault());
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);
                project.getProject().setFrom_date(calendar.getTime());
                btnTime.setText(TimeFormater.getInstance().toDateFormat(project.getProject()
                        .getFrom_date()));
                btnTime.setTextColor(ContextCompat.getColor(ProjectCreateActivity.this, R.color
                        .orange));
                if (project.getProject().getCheck_date() != null) {
                    if (project.getProject().getCheck_date().compareTo(project.getProject()
                            .getFrom_date()) < 0) {
                        project.getProject().setCheck_date(calendar.getTime());
                        btnCheckTime.setText(TimeFormater.getInstance().toDateFormat(project
                                .getProject()
                                .getCheck_date()));
                        btnCheckTime.setTextColor(ContextCompat.getColor(ProjectCreateActivity
                                .this, R.color
                                .orange));
                    }
                }
            }
        }, toBeChanged.get(Calendar.YEAR), toBeChanged.get(Calendar.MONTH), toBeChanged.get
                (Calendar.DAY_OF_MONTH));
        datePicker.show();
    }

    /**
     * 變更結單時間
     */
    @OnClick(R.id.btnCheckTime)
    void changeCheckTime() {
        Calendar toBeChanged = Calendar.getInstance(Locale.getDefault());
        if (project.getProject().getCheck_date() != null)
            toBeChanged.setTime(project.getProject().getCheck_date());
        DatePickerDialog datePicker = new DatePickerDialog(this, new DatePickerDialog
                .OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                Calendar calendar = Calendar.getInstance(Locale.getDefault());
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);
                project.getProject().setCheck_date(calendar.getTime());
                btnCheckTime.setText(TimeFormater.getInstance().toDateFormat(project.getProject()
                        .getCheck_date()));
                btnCheckTime.setTextColor(ContextCompat.getColor(ProjectCreateActivity.this, R.color
                        .orange));
            }
        }, toBeChanged.get(Calendar.YEAR), toBeChanged.get(Calendar.MONTH), toBeChanged.get
                (Calendar.DAY_OF_MONTH));
        if (project.getProject().getFrom_date() != null)
            datePicker.getDatePicker().setMinDate(project.getProject().getFrom_date().getTime());
        datePicker.show();
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            itemSelectCustomer.onActivityResult(requestCode, resultCode, data);
            itemSelectPeople.onActivityResult(requestCode, resultCode, data);
            itemSelectPhoto.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
