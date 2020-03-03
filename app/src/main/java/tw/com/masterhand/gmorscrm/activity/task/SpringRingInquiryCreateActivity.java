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
import tw.com.masterhand.gmorscrm.model.SpringRingInquiryWithConfig;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.record.Customer;
import tw.com.masterhand.gmorscrm.room.record.SpringRingInquiry;
import tw.com.masterhand.gmorscrm.room.record.SpringRingInquiryProduct;
import tw.com.masterhand.gmorscrm.room.setting.Admin;
import tw.com.masterhand.gmorscrm.room.setting.ConfigCompetitiveStatus;
import tw.com.masterhand.gmorscrm.room.setting.ConfigInquiryProductType;
import tw.com.masterhand.gmorscrm.room.setting.ConfigSealedState;
import tw.com.masterhand.gmorscrm.room.setting.ConfigSgbAdvantage;
import tw.com.masterhand.gmorscrm.room.setting.ConfigSgbDisadvantage;
import tw.com.masterhand.gmorscrm.room.setting.ConfigTrend;
import tw.com.masterhand.gmorscrm.room.setting.ConfigYearPotential;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.tools.UnitChanger;
import tw.com.masterhand.gmorscrm.view.AppbarEdit;
import tw.com.masterhand.gmorscrm.view.Dropdown;
import tw.com.masterhand.gmorscrm.view.EditTextWithTitle;
import tw.com.masterhand.gmorscrm.view.ItemSelectCustomer;
import tw.com.masterhand.gmorscrm.view.ItemSelectPhoto;
import tw.com.masterhand.gmorscrm.view.ItemSelectProject;

public class SpringRingInquiryCreateActivity extends BaseUserCheckActivity implements
        ItemSelectCustomer
                .CustomerSelectListener {
    @BindView(R.id.appbar)
    AppbarEdit appbar;
    @BindView(R.id.itemSelectCustomer)
    ItemSelectCustomer itemSelectCustomer;
    @BindView(R.id.itemSelectProject)
    ItemSelectProject itemSelectProject;
    @BindView(R.id.etName)
    EditTextWithTitle etName;

    @BindView(R.id.etCode)
    EditTextWithTitle etCode;
    @BindView(R.id.etProjectName)
    EditTextWithTitle etProjectName;
    @BindView(R.id.etApply)
    EditTextWithTitle etApply;
    @BindView(R.id.dropdownPotential)
    Dropdown dropdownPotential;
    @BindView(R.id.dropdownNeedImage)
    Dropdown dropdownNeedImage;
    @BindView(R.id.dropdownNeedParity)
    Dropdown dropdownNeedParity;
    @BindView(R.id.dropdownCompetitive)
    Dropdown dropdownCompetitive;
    @BindView(R.id.dropdownProductType)
    Dropdown dropdownProductType;
    @BindView(R.id.dropdownSgbAdvantage)
    Dropdown dropdownSgbAdvantage;
    @BindView(R.id.dropdownSgbDisadvantage)
    Dropdown dropdownSgbDisadvantage;
    @BindView(R.id.dropdownTrend)
    Dropdown dropdownTrend;
    @BindView(R.id.dropdownSealed)
    Dropdown dropdownSealed;
    @BindView(R.id.etSpeed)
    EditTextWithTitle etSpeed;
    @BindView(R.id.etPressure)
    EditTextWithTitle etPressure;
    @BindView(R.id.etTemperature)
    EditTextWithTitle etTemperature;
    @BindView(R.id.etLifetime)
    EditTextWithTitle etLifttime;
    @BindView(R.id.etStaticMaterial)
    EditTextWithTitle etStaticMaterial;
    @BindView(R.id.etStaticHardness)
    EditTextWithTitle etStaticHardness;
    @BindView(R.id.etStaticSmoothness)
    EditTextWithTitle etStaticSmoothness;
    @BindView(R.id.etDynamicMaterial)
    EditTextWithTitle etDynamicMaterial;
    @BindView(R.id.etDynamicHardness)
    EditTextWithTitle etDynamicHardness;
    @BindView(R.id.etDynamicSmoothness)
    EditTextWithTitle etDynamicSmoothness;
    @BindView(R.id.etOther)
    EditTextWithTitle etOther;

    @BindView(R.id.container)
    LinearLayout container;
    @BindView(R.id.dropdownAssistant)
    Dropdown dropdownAssistant;
    @BindView(R.id.itemSelectPhoto)
    ItemSelectPhoto itemSelectPhoto;
    @BindView(R.id.etNote)
    EditText etNote;

    SpringRingInquiryWithConfig inquiry;

    SpringRingInquiryProduct selectedProduct;

    List<Admin> adminList;
    List<ConfigYearPotential> potentialList;
    List<ConfigInquiryProductType> productTypeList;
    List<ConfigCompetitiveStatus> statusList;
    List<ConfigSgbAdvantage> advantageList;
    List<ConfigSgbDisadvantage> disadvantageList;
    List<ConfigTrend> trendList;
    List<ConfigSealedState> sealedStateList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spring_ring_inquiry_create);
        init();
    }

    @Override
    protected void onUserChecked() {
        appbar.invalidate();
        if (inquiry != null)
            return;
        String tripId = getIntent().getStringExtra(MyApplication.INTENT_KEY_TRIP);
        if (TextUtils.isEmpty(tripId)) {
            /*新建彈簧蓄能圈詢價*/
            inquiry = new SpringRingInquiryWithConfig(new SpringRingInquiry());
            updateProductList();
            final String customerId = getIntent().getStringExtra(MyApplication.INTENT_KEY_CUSTOMER);
            if (!TextUtils.isEmpty(customerId)) {
                getCustomer(customerId);
            }
            String projectId = getIntent().getStringExtra(MyApplication.INTENT_KEY_PROJECT);
            if (!TextUtils.isEmpty(projectId)) {
                getProject(projectId);
            }
            getAssistant();
            getPotential();
            getProductType();
            getCompetitive();
            getSgbAdvantage();
            getSgnDisadvantage();
            getTrend();
            getSealed();
        } else {
            /*編輯彈簧蓄能圈詢價*/
            getInquiry(tripId);
        }
    }

    protected void init() {
        etCode.disable();
        itemSelectProject.setItemSelectCustomer(itemSelectCustomer);
        itemSelectCustomer.setShouldCheck(true);
        itemSelectCustomer.addCustomerSelectedListener(this);

        appbar.setTitle(getString(R.string.main_menu_new_task) + "/" + getString(R.string
                .apply_spring_ring_inquiry));
        appbar.setCompleteListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInput()) {
                    startProgressDialog();
                    inquiry.getTrip().setCustomer_id(itemSelectCustomer.getCustomer().getId());
                    inquiry.getTrip().setProject_id(itemSelectProject.getProject().getProject()
                            .getId());
                    inquiry.getTrip().setDate_type(true);
                    inquiry.getTrip().setName(etName.getText().toString());
                    inquiry.getTrip().setDescription(etNote.getText().toString());
                    if (adminList != null && dropdownAssistant.getSelected() != Dropdown
                            .VALUE_EMPTY) {
                        inquiry.getTrip().setAssistant_id(adminList.get(dropdownAssistant
                                .getSelected()).getId());
                    }
                    inquiry.setFiles(itemSelectPhoto.getFiles());

                    inquiry.getInquiry().setName(etProjectName.getText().toString());
                    inquiry.getInquiry().setApply(etApply.getText().toString());
                    inquiry.getInquiry().setSpeed(etSpeed.getText().toString());
                    inquiry.getInquiry().setPressure(etPressure.getText().toString());
                    inquiry.getInquiry().setTemperature(etTemperature.getText().toString());
                    inquiry.getInquiry().setLifetime(etLifttime.getText().toString());
                    inquiry.getInquiry().setStatic_a_material(etStaticMaterial.getText().toString
                            ());
                    inquiry.getInquiry().setStatic_a_hardness(etStaticHardness.getText().toString
                            ());
                    inquiry.getInquiry().setStatic_a_smoothness(etStaticSmoothness.getText()
                            .toString());
                    inquiry.getInquiry().setDynamic_b_material(etDynamicMaterial.getText()
                            .toString());
                    inquiry.getInquiry().setDynamic_b_hardness(etDynamicHardness.getText()
                            .toString());
                    inquiry.getInquiry().setDynamic_b_smoothness(etDynamicSmoothness.getText()
                            .toString());
                    inquiry.getInquiry().setOther(etOther.getText().toString());
                    inquiry.getInquiry().setYear_potential_id(potentialList.get(dropdownPotential
                            .getSelected()).getId());
                    inquiry.getInquiry().setNeed_image(dropdownNeedImage.getSelected());
                    inquiry.getInquiry().setNeed_parity(dropdownNeedParity.getSelected());
                    inquiry.getInquiry().setProduct_type_id(productTypeList.get
                            (dropdownProductType.getSelected()).getId());
                    inquiry.getInquiry().setCompetitive_status_id(statusList.get
                            (dropdownCompetitive.getSelected()).getId());
                    inquiry.getInquiry().setSgb_advantage_id(advantageList.get
                            (dropdownSgbAdvantage.getSelected()).getId());
                    inquiry.getInquiry().setSgb_disadvantage_id(disadvantageList.get
                            (dropdownSgbDisadvantage.getSelected()).getId());
                    inquiry.getInquiry().setTrend_id(trendList.get(dropdownTrend.getSelected())
                            .getId());
                    inquiry.getInquiry().setSealed_state(sealedStateList.get(dropdownSealed
                            .getSelected()).getId());

                    mDisposable.add(DatabaseHelper.getInstance(SpringRingInquiryCreateActivity.this)
                            .saveSpringRingInquiry(inquiry).observeOn(AndroidSchedulers
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
                                    Toast.makeText(SpringRingInquiryCreateActivity.this, t
                                                    .getMessage(),
                                            Toast
                                                    .LENGTH_LONG).show();
                                }
                            }));
                }
            }
        });

    }

    private void getInquiry(String tripId) {
        mDisposable.add(DatabaseHelper.getInstance(this).getSpringRingInquiryByTrip(tripId)
                .observeOn
                        (AndroidSchedulers.mainThread()).subscribe(new Consumer<SpringRingInquiryWithConfig>() {
                    @Override
                    public void accept(SpringRingInquiryWithConfig result) throws Exception {
                        inquiry = result;
                        getCustomer(inquiry.getTrip().getCustomer_id());
                        getProject(inquiry.getTrip().getProject_id());
                        if (!TextUtils.isEmpty(inquiry.getTrip().getName()))
                            etName.setText(inquiry.getTrip().getName());

                        if (!TextUtils.isEmpty(inquiry.getInquiry().getCode_number()))
                            etCode.setText(inquiry.getInquiry().getCode_number());
                        if (!TextUtils.isEmpty(inquiry.getInquiry().getName()))
                            etProjectName.setText(inquiry.getInquiry().getName());
                        if (!TextUtils.isEmpty(inquiry.getInquiry().getApply()))
                            etApply.setText(inquiry.getInquiry().getApply());
                        if (!TextUtils.isEmpty(inquiry.getInquiry().getSpeed()))
                            etSpeed.setText(inquiry.getInquiry().getSpeed());
                        if (!TextUtils.isEmpty(inquiry.getInquiry().getPressure()))
                            etPressure.setText(inquiry.getInquiry().getPressure());
                        if (!TextUtils.isEmpty(inquiry.getInquiry().getTemperature()))
                            etTemperature.setText(inquiry.getInquiry().getTemperature());
                        if (!TextUtils.isEmpty(inquiry.getInquiry().getLifetime()))
                            etLifttime.setText(inquiry.getInquiry().getLifetime());
                        if (!TextUtils.isEmpty(inquiry.getInquiry().getStatic_a_material()))
                            etStaticMaterial.setText(inquiry.getInquiry().getStatic_a_material());
                        if (!TextUtils.isEmpty(inquiry.getInquiry().getStatic_a_hardness()))
                            etStaticHardness.setText(inquiry.getInquiry().getStatic_a_hardness());
                        if (!TextUtils.isEmpty(inquiry.getInquiry().getStatic_a_smoothness()))
                            etStaticSmoothness.setText(inquiry.getInquiry()
                                    .getStatic_a_smoothness());
                        if (!TextUtils.isEmpty(inquiry.getInquiry().getDynamic_b_material()))
                            etDynamicMaterial.setText(inquiry.getInquiry().getDynamic_b_material());
                        if (!TextUtils.isEmpty(inquiry.getInquiry().getDynamic_b_hardness()))
                            etDynamicHardness.setText(inquiry.getInquiry().getDynamic_b_hardness());
                        if (!TextUtils.isEmpty(inquiry.getInquiry().getDynamic_b_smoothness()))
                            etDynamicSmoothness.setText(inquiry.getInquiry()
                                    .getDynamic_b_smoothness());
                        if (inquiry.getInquiry().getNeed_image() != Dropdown.VALUE_EMPTY)
                            dropdownNeedImage.select(inquiry.getInquiry().getNeed_image());
                        if (inquiry.getInquiry().getNeed_parity() != Dropdown.VALUE_EMPTY)
                            dropdownNeedParity.select(inquiry.getInquiry().getNeed_parity());

                        updateProductList();
                        if (!TextUtils.isEmpty(inquiry.getInquiry().getOther()))
                            etOther.setText(inquiry.getInquiry().getOther());
                        getAssistant();
                        getPotential();
                        getProductType();
                        getCompetitive();
                        getSgbAdvantage();
                        getSgnDisadvantage();
                        getTrend();
                        getSealed();
                        itemSelectPhoto.setFiles(inquiry.getFiles());
                        if (!TextUtils.isEmpty(inquiry.getTrip().getDescription()))
                            etNote.setText(inquiry.getTrip().getDescription());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(SpringRingInquiryCreateActivity.this, throwable
                                .getMessage(), Toast
                                .LENGTH_LONG).show();
                        finish();
                    }
                }));
    }

    void getPotential() {
        mDisposable.add(DatabaseHelper.getInstance(this).getConfigYearPotential().observeOn
                (AndroidSchedulers.mainThread()).toList().subscribe(new Consumer<List<ConfigYearPotential>>() {

            @Override
            public void accept(List<ConfigYearPotential> configs) throws Exception {
                potentialList = configs;
                String[] items = new String[potentialList.size()];
                int index = Dropdown.VALUE_EMPTY;
                for (int i = 0; i < potentialList.size(); i++) {
                    items[i] = potentialList.get(i).getName();
                    if (potentialList.get(i).getId().equals(inquiry.getInquiry()
                            .getYear_potential_id()))
                        index = i;
                }
                dropdownPotential.setItems(items);
                if (index != Dropdown.VALUE_EMPTY)
                    dropdownPotential.select(index);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                ErrorHandler.getInstance().setException(SpringRingInquiryCreateActivity.this,
                        throwable);
            }
        }));
    }

    void getProductType() {
        mDisposable.add(DatabaseHelper.getInstance(this).getConfigInquiryProductType().observeOn
                (AndroidSchedulers.mainThread()).toList().subscribe(new Consumer<List<ConfigInquiryProductType>>() {

            @Override
            public void accept(List<ConfigInquiryProductType> configs) throws Exception {
                productTypeList = configs;
                String[] items = new String[productTypeList.size()];
                int index = Dropdown.VALUE_EMPTY;
                for (int i = 0; i < productTypeList.size(); i++) {
                    items[i] = productTypeList.get(i).getName();
                    if (productTypeList.get(i).getId().equals(inquiry.getInquiry()
                            .getProduct_type_id()))
                        index = i;
                }
                dropdownProductType.setItems(items);
                if (index != Dropdown.VALUE_EMPTY)
                    dropdownProductType.select(index);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                ErrorHandler.getInstance().setException(SpringRingInquiryCreateActivity.this,
                        throwable);
            }
        }));
    }

    void getCompetitive() {
        mDisposable.add(DatabaseHelper.getInstance(this).getConfigCompetitiveStatus().observeOn
                (AndroidSchedulers.mainThread()).toList().subscribe(new Consumer<List<ConfigCompetitiveStatus>>() {

            @Override
            public void accept(List<ConfigCompetitiveStatus> configs) throws Exception {
                statusList = configs;
                String[] items = new String[statusList.size()];
                int index = Dropdown.VALUE_EMPTY;
                for (int i = 0; i < statusList.size(); i++) {
                    items[i] = statusList.get(i).getName();
                    if (statusList.get(i).getId().equals(inquiry.getInquiry()
                            .getCompetitive_status_id()))
                        index = i;
                }
                dropdownCompetitive.setItems(items);
                if (index != Dropdown.VALUE_EMPTY)
                    dropdownCompetitive.select(index);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                ErrorHandler.getInstance().setException(SpringRingInquiryCreateActivity.this,
                        throwable);
            }
        }));
    }

    void getSgbAdvantage() {
        mDisposable.add(DatabaseHelper.getInstance(this).getConfigSgbAdvantage().observeOn
                (AndroidSchedulers.mainThread()).toList().subscribe(new Consumer<List<ConfigSgbAdvantage>>() {

            @Override
            public void accept(List<ConfigSgbAdvantage> configs) throws Exception {
                advantageList = configs;
                String[] items = new String[advantageList.size()];
                int index = Dropdown.VALUE_EMPTY;
                for (int i = 0; i < advantageList.size(); i++) {
                    items[i] = advantageList.get(i).getName();
                    if (advantageList.get(i).getId().equals(inquiry.getInquiry()
                            .getSgb_advantage_id()))
                        index = i;
                }
                dropdownSgbAdvantage.setItems(items);
                if (index != Dropdown.VALUE_EMPTY)
                    dropdownSgbAdvantage.select(index);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                ErrorHandler.getInstance().setException(SpringRingInquiryCreateActivity.this,
                        throwable);
            }
        }));
    }

    void getSgnDisadvantage() {
        mDisposable.add(DatabaseHelper.getInstance(this).getConfigSgbDisadvantage().observeOn
                (AndroidSchedulers.mainThread()).toList().subscribe(new Consumer<List<ConfigSgbDisadvantage>>() {

            @Override
            public void accept(List<ConfigSgbDisadvantage> configs) throws Exception {
                disadvantageList = configs;
                String[] items = new String[disadvantageList.size()];
                int index = Dropdown.VALUE_EMPTY;
                for (int i = 0; i < disadvantageList.size(); i++) {
                    items[i] = disadvantageList.get(i).getName();
                    if (disadvantageList.get(i).getId().equals(inquiry.getInquiry()
                            .getSgb_disadvantage_id()))
                        index = i;
                }
                dropdownSgbDisadvantage.setItems(items);
                if (index != Dropdown.VALUE_EMPTY)
                    dropdownSgbDisadvantage.select(index);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                ErrorHandler.getInstance().setException(SpringRingInquiryCreateActivity.this,
                        throwable);
            }
        }));
    }

    void getTrend() {
        mDisposable.add(DatabaseHelper.getInstance(this).getConfigTrend().observeOn
                (AndroidSchedulers.mainThread()).toList().subscribe(new Consumer<List<ConfigTrend>>() {

            @Override
            public void accept(List<ConfigTrend> configTrends) throws Exception {
                trendList = configTrends;
                String[] items = new String[trendList.size()];
                int index = Dropdown.VALUE_EMPTY;
                for (int i = 0; i < trendList.size(); i++) {
                    items[i] = trendList.get(i).getName();
                    if (trendList.get(i).getId().equals(inquiry.getInquiry()
                            .getTrend_id()))
                        index = i;
                }
                dropdownTrend.setItems(items);
                if (index != Dropdown.VALUE_EMPTY)
                    dropdownTrend.select(index);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                ErrorHandler.getInstance().setException(SpringRingInquiryCreateActivity.this,
                        throwable);
            }
        }));
    }

    void getSealed() {
        mDisposable.add(DatabaseHelper.getInstance(this).getConfigSealedState().observeOn
                (AndroidSchedulers.mainThread()).toList().subscribe(new Consumer<List<ConfigSealedState>>() {

            @Override
            public void accept(List<ConfigSealedState> configSealedStates) throws Exception {
                sealedStateList = configSealedStates;
                String[] items = new String[sealedStateList.size()];
                int index = Dropdown.VALUE_EMPTY;
                for (int i = 0; i < sealedStateList.size(); i++) {
                    items[i] = sealedStateList.get(i).getName();
                    if (sealedStateList.get(i).getId().equals(inquiry.getInquiry()
                            .getSealed_state()))
                        index = i;
                }
                dropdownSealed.setItems(items);
                if (index != Dropdown.VALUE_EMPTY)
                    dropdownSealed.select(index);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                ErrorHandler.getInstance().setException(SpringRingInquiryCreateActivity.this,
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
                    if (!TextUtils.isEmpty(inquiry.getTrip().getAssistant_id())) {
                        assistantId = inquiry.getTrip().getAssistant_id();
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
                ErrorHandler.getInstance().setException(SpringRingInquiryCreateActivity.this,
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
                        ErrorHandler.getInstance().setException(SpringRingInquiryCreateActivity
                                        .this,
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
                ErrorHandler.getInstance().setException(SpringRingInquiryCreateActivity.this,
                        throwable);
            }
        }));
    }

    protected boolean checkInput() {
        String required = getString(R.string.error_msg_required);
        if (itemSelectCustomer.getCustomer() == null) {
            Toast.makeText(this, R.string.error_msg_no_customer, Toast.LENGTH_LONG).show();
            return false;
        }
        if (itemSelectProject.getProject() == null) {
            Toast.makeText(this, R.string.error_msg_no_project, Toast.LENGTH_LONG).show();
            return false;
        }
        if (inquiry.getProducts().size() == 0) {
            Toast.makeText(this, R.string.error_msg_no_product, Toast.LENGTH_LONG).show();
            return false;
        }
        if (TextUtils.isEmpty(etName.getText().toString())) {
            Toast.makeText(this, required + getString(R.string.spring_ring_inquiry_name), Toast
                    .LENGTH_LONG).show();
            return false;
        }
        if (TextUtils.isEmpty(etProjectName.getText().toString())) {
            Toast.makeText(this, required + getString(R.string.spring_ring_inquiry_project_name),
                    Toast
                            .LENGTH_LONG).show();
            return false;
        }
        if (TextUtils.isEmpty(etApply.getText().toString())) {
            Toast.makeText(this, required + getString(R.string.spring_ring_inquiry_apply), Toast
                    .LENGTH_LONG).show();
            return false;
        }
        if (TextUtils.isEmpty(etSpeed.getText().toString())) {
            Toast.makeText(this, required + getString(R.string.spring_ring_inquiry_speed), Toast
                    .LENGTH_LONG).show();
            return false;
        }
        if (TextUtils.isEmpty(etPressure.getText().toString())) {
            Toast.makeText(this, required + getString(R.string.spring_ring_inquiry_pressure), Toast
                    .LENGTH_LONG).show();
            return false;
        }
        if (TextUtils.isEmpty(etTemperature.getText().toString())) {
            Toast.makeText(this, required + getString(R.string.spring_ring_inquiry_temperature),
                    Toast
                            .LENGTH_LONG).show();
            return false;
        }
        if (TextUtils.isEmpty(etLifttime.getText().toString())) {
            Toast.makeText(this, required + getString(R.string.spring_ring_inquiry_lifetime), Toast
                    .LENGTH_LONG).show();
            return false;
        }
        if (TextUtils.isEmpty(etStaticMaterial.getText().toString())) {
            Toast.makeText(this, required + getString(R.string
                    .spring_ring_inquiry_static_material), Toast
                    .LENGTH_LONG).show();
            return false;
        }
        if (TextUtils.isEmpty(etStaticHardness.getText().toString())) {
            Toast.makeText(this, required + getString(R.string
                    .spring_ring_inquiry_static_hardness), Toast
                    .LENGTH_LONG).show();
            return false;
        }
        if (TextUtils.isEmpty(etStaticSmoothness.getText().toString())) {
            Toast.makeText(this, required + getString(R.string
                    .spring_ring_inquiry_static_smoothness), Toast
                    .LENGTH_LONG).show();
            return false;
        }
        if (TextUtils.isEmpty(etDynamicMaterial.getText().toString())) {
            Toast.makeText(this, required + getString(R.string
                    .spring_ring_inquiry_dynamic_material), Toast
                    .LENGTH_LONG).show();
            return false;
        }
        if (TextUtils.isEmpty(etDynamicHardness.getText().toString())) {
            Toast.makeText(this, required + getString(R.string
                    .spring_ring_inquiry_dynamic_hardness), Toast
                    .LENGTH_LONG).show();
            return false;
        }
        if (TextUtils.isEmpty(etDynamicSmoothness.getText().toString())) {
            Toast.makeText(this, required + getString(R.string
                    .spring_ring_inquiry_dynamic_smoothness), Toast
                    .LENGTH_LONG).show();
            return false;
        }
        if (TextUtils.isEmpty(etOther.getText().toString())) {
            Toast.makeText(this, required + getString(R.string
                    .spring_ring_inquiry_other), Toast
                    .LENGTH_LONG).show();
            return false;
        }
        if (dropdownPotential.getSelected() == Dropdown.VALUE_EMPTY) {
            Toast.makeText(this, required + getString(R.string.spring_ring_inquiry_potential),
                    Toast
                            .LENGTH_LONG).show();
            return false;
        }
        if (dropdownNeedParity.getSelected() == Dropdown.VALUE_EMPTY) {
            Toast.makeText(this, required + getString(R.string.spring_ring_inquiry_need_parity),
                    Toast
                            .LENGTH_LONG).show();
            return false;
        }
        if (dropdownNeedImage.getSelected() == Dropdown.VALUE_EMPTY) {
            Toast.makeText(this, required + getString(R.string.spring_ring_inquiry_need_image),
                    Toast
                            .LENGTH_LONG).show();
            return false;
        }
        if (dropdownProductType.getSelected() == Dropdown.VALUE_EMPTY) {
            Toast.makeText(this, required + getString(R.string.spring_ring_inquiry_product_type),
                    Toast
                            .LENGTH_LONG).show();
            return false;
        }
        if (dropdownCompetitive.getSelected() == Dropdown.VALUE_EMPTY) {
            Toast.makeText(this, required + getString(R.string
                            .spring_ring_inquiry_competitive_status),
                    Toast
                            .LENGTH_LONG).show();
            return false;
        }
        if (dropdownSgbAdvantage.getSelected() == Dropdown.VALUE_EMPTY) {
            Toast.makeText(this, required + getString(R.string.spring_ring_inquiry_sgb_advantage),
                    Toast
                            .LENGTH_LONG).show();
            return false;
        }
        if (dropdownSgbDisadvantage.getSelected() == Dropdown.VALUE_EMPTY) {
            Toast.makeText(this, required + getString(R.string
                            .spring_ring_inquiry_sgb_disadvantage),
                    Toast
                            .LENGTH_LONG).show();
            return false;
        }
        if (dropdownTrend.getSelected() == Dropdown.VALUE_EMPTY) {
            Toast.makeText(this, required + getString(R.string.spring_ring_inquiry_trend),
                    Toast
                            .LENGTH_LONG).show();
            return false;
        }
        if (dropdownSealed.getSelected() == Dropdown.VALUE_EMPTY) {
            Toast.makeText(this, required + getString(R.string.spring_ring_inquiry_sealed_state),
                    Toast
                            .LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    @OnClick(R.id.btnAddProduct)
    protected void addProduct() {
        Intent intent = new Intent(this, SpringRingInquiryProductCreateActivity.class);
        startActivityForResult(intent, MyApplication.REQUEST_EDIT_PRODUCT);
    }

    protected View generateProductItem(final SpringRingInquiryProduct product) {
        final Button btnProduct = new Button(this);
        btnProduct.setText(product.getPart_number());
        btnProduct.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        btnProduct.setPaddingRelative(UnitChanger.dpToPx(16), 0, 0, 0);
        btnProduct.setBackgroundResource(R.drawable.bg_spring_ring_inquiry_required);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            btnProduct.setStateListAnimator(null);
        }
        btnProduct.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.mipmap
                        .common_arrow_right,
                0);
        btnProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedProduct = product;
                Intent intent = new Intent(SpringRingInquiryCreateActivity.this,
                        SpringRingInquiryProductCreateActivity.class);
                intent.putExtra(MyApplication.INTENT_KEY_PRODUCT, gson.toJson
                        (product));
                startActivityForResult(intent, MyApplication.REQUEST_EDIT_PRODUCT);
            }
        });
        btnProduct.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext(),
                        MyApplication.DIALOG_STYLE);
                builder.setTitle(R.string.remove).setMessage(getString(R.string
                        .remove_confirm) +
                        "?")
                        .setPositiveButton(R.string.confirm,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        inquiry.getProducts().remove(product);
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
        for (SpringRingInquiryProduct product : inquiry.getProducts()) {
            container.addView(generateProductItem(product));
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case MyApplication.REQUEST_EDIT_PRODUCT:
                    SpringRingInquiryProduct product = gson.fromJson
                            (data.getStringExtra(MyApplication.INTENT_KEY_PRODUCT),
                                    SpringRingInquiryProduct.class);
                    if (selectedProduct != null) {
                        inquiry.getProducts().remove(selectedProduct);
                        selectedProduct = null;
                    }
                    inquiry.getProducts().add(product);
                    updateProductList();
                    updateName();
                    break;
                default:
                    itemSelectPhoto.onActivityResult(requestCode, resultCode, data);
                    itemSelectCustomer.onActivityResult(requestCode, resultCode, data);
                    itemSelectProject.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    @Override
    public void onCustomerSelected(Customer customer) {
        updateName();
    }

    protected void updateName() {
        StringBuilder builder = new StringBuilder();
        builder.append(getString(R.string.apply_spring_ring_inquiry));
        if (itemSelectCustomer.getCustomer() != null) {
            builder.append("-").append(itemSelectCustomer.getCustomer().getFull_name());
        }
        if (inquiry.getProducts().size() > 0) {
            builder.append("-").append(inquiry.getProducts().get(0).getPart_number());
        }
        etName.setText(builder.toString());
    }
}
