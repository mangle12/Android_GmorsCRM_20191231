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
import tw.com.masterhand.gmorscrm.enums.SpecialPriceApproval;
import tw.com.masterhand.gmorscrm.enums.SpecialPriceProjectStatus;
import tw.com.masterhand.gmorscrm.enums.SpecialPriceType;
import tw.com.masterhand.gmorscrm.model.ProjectWithConfig;
import tw.com.masterhand.gmorscrm.model.SpecialPriceWithConfig;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.record.Customer;
import tw.com.masterhand.gmorscrm.room.record.SpecialPrice;
import tw.com.masterhand.gmorscrm.room.record.SpecialPriceProduct;
import tw.com.masterhand.gmorscrm.room.setting.Admin;
import tw.com.masterhand.gmorscrm.room.setting.ConfigCompanyHiddenField;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.tools.UnitChanger;
import tw.com.masterhand.gmorscrm.view.AppbarEdit;
import tw.com.masterhand.gmorscrm.view.Dropdown;
import tw.com.masterhand.gmorscrm.view.EditTextWithTitle;
import tw.com.masterhand.gmorscrm.view.ItemSelectCustomer;
import tw.com.masterhand.gmorscrm.view.ItemSelectDiscount;
import tw.com.masterhand.gmorscrm.view.ItemSelectPhoto;
import tw.com.masterhand.gmorscrm.view.ItemSelectProject;

public class SpecialPriceCreateActivity extends BaseUserCheckActivity implements ItemSelectCustomer
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
    @BindView(R.id.dropdownStatus)
    Dropdown dropdownStatus;
    @BindView(R.id.dropdownApproval)
    Dropdown dropdownApproval;
    @BindView(R.id.etYearAmount)
    EditTextWithTitle etYearAmount;
    @BindView(R.id.itemSelectDiscount)
    ItemSelectDiscount itemSelectDiscount;
    @BindView(R.id.etOffer)
    EditTextWithTitle etOffer;
    @BindView(R.id.container)
    LinearLayout container;
    @BindView(R.id.etReason)
    EditText etReason;
    @BindView(R.id.dropdownAssistant)
    Dropdown dropdownAssistant;
    @BindView(R.id.itemSelectPhoto)
    ItemSelectPhoto itemSelectPhoto;
    @BindView(R.id.etNote)
    EditText etNote;

    SpecialPriceWithConfig specialPrice;

    SpecialPriceProduct selectedProduct;

    List<Admin> adminList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_special_price_create);
        init();
    }

    @Override
    protected void onUserChecked() {
        appbar.invalidate();
        itemSelectDiscount.setTitle(getString(R.string.special_price_year_discount));
        getHiddenField();
        if (specialPrice != null)
            return;
        String tripId = getIntent().getStringExtra(MyApplication.INTENT_KEY_TRIP);
        if (TextUtils.isEmpty(tripId)) {
            /*新建特價*/
            specialPrice = new SpecialPriceWithConfig(new SpecialPrice());
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
        } else {
            /*編輯特價*/
            getSpecialPrice(tripId);
        }

    }

    protected void init() {
        itemSelectProject.setItemSelectCustomer(itemSelectCustomer);
        itemSelectCustomer.setShouldCheck(true);
        itemSelectCustomer.addCustomerSelectedListener(this);
        String[] types = new String[SpecialPriceType.values().length];
        for (int i = 0; i < types.length; i++) {
            types[i] = getString(SpecialPriceType.values()[i].getTitle());
        }
        dropdownType.setItems(types);
        String[] statusList = new String[SpecialPriceProjectStatus.values().length];
        for (int i = 0; i < statusList.length; i++) {
            statusList[i] = getString(SpecialPriceProjectStatus.values()[i].getTitle());
        }
        dropdownStatus.setItems(statusList);
        String[] approvals = new String[SpecialPriceApproval.values().length];
        for (int i = 0; i < approvals.length; i++) {
            approvals[i] = getString(SpecialPriceApproval.values()[i].getTitle());
        }
        dropdownApproval.setItems(approvals);


        appbar.setTitle(getString(R.string.main_menu_new_task) + "/" + getString(R.string
                .apply_special_price));
        appbar.setCompleteListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInput()) {
                    startProgressDialog();
                    specialPrice.getTrip().setCustomer_id(itemSelectCustomer.getCustomer().getId());
                    specialPrice.getTrip().setProject_id(itemSelectProject.getProject().getProject()
                            .getId());
                    specialPrice.getTrip().setDate_type(true);
                    specialPrice.getTrip().setName(etName.getText().toString());
                    specialPrice.getTrip().setDescription(etNote.getText().toString());
                    if (adminList != null && dropdownAssistant.getSelected() != Dropdown
                            .VALUE_EMPTY) {
                        specialPrice.getTrip().setAssistant_id(adminList.get(dropdownAssistant
                                .getSelected()).getId());
                    }
                    specialPrice.getSpecialPrice().setType(SpecialPriceType.getTypeByCode
                            (dropdownType.getSelected()));
                    if (dropdownStatus.getSelected() != Dropdown.VALUE_EMPTY)
                        specialPrice.getSpecialPrice().setProject_status(SpecialPriceProjectStatus
                                .getTypeByCode(dropdownStatus.getSelected()));
                    if (dropdownApproval.getSelected() != Dropdown.VALUE_EMPTY) {
                        specialPrice.getSpecialPrice().setApproval(SpecialPriceApproval
                                .getTypeByCode
                                        (dropdownApproval.getSelected() + 1));
                    } else {
                        specialPrice.getSpecialPrice().setApproval(SpecialPriceApproval
                                .getTypeByCode(1));
                    }
                    if (!TextUtils.isEmpty(etYearAmount.getText().toString()))
                        specialPrice.getSpecialPrice().setYear_amount(Float.parseFloat
                                (etYearAmount.getText().toString()));
                    specialPrice.getSpecialPrice().setYear_discount(itemSelectDiscount
                            .getDiscount());
                    specialPrice.getSpecialPrice().setOffer(etOffer.getText().toString());
                    specialPrice.getSpecialPrice().setReason(etReason.getText().toString());

                    specialPrice.setFiles(itemSelectPhoto.getFiles());
                    mDisposable.add(DatabaseHelper.getInstance(SpecialPriceCreateActivity.this)
                            .saveSpecialPrice
                                    (specialPrice).observeOn(AndroidSchedulers
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
                                    Toast.makeText(SpecialPriceCreateActivity.this, t.getMessage(),
                                            Toast
                                                    .LENGTH_LONG).show();
                                }
                            }));
                }
            }
        });

    }

    private void getSpecialPrice(String tripId) {
        mDisposable.add(DatabaseHelper.getInstance(this).getSpecialPriceByTrip(tripId).observeOn
                (AndroidSchedulers.mainThread()).subscribe(new Consumer<SpecialPriceWithConfig>() {
            @Override
            public void accept(SpecialPriceWithConfig result) throws Exception {
                specialPrice = result;
                getCustomer(specialPrice.getTrip().getCustomer_id());
                getProject(specialPrice.getTrip().getProject_id());
                if (!TextUtils.isEmpty(specialPrice.getTrip().getName()))
                    etName.setText(specialPrice.getTrip().getName());
                if (specialPrice.getSpecialPrice().getYear_amount() > 0)
                    etYearAmount.setText(String.valueOf(specialPrice.getSpecialPrice()
                            .getYear_amount()));
                if (specialPrice.getSpecialPrice().getYear_discount() > 0)
                    itemSelectDiscount.setDiscount(specialPrice.getSpecialPrice()
                            .getYear_discount());
                if (!TextUtils.isEmpty(specialPrice.getSpecialPrice().getOffer()))
                    etOffer.setText(specialPrice.getSpecialPrice().getOffer());
                if (!TextUtils.isEmpty(specialPrice.getSpecialPrice().getReason()))
                    etReason.setText(specialPrice.getSpecialPrice().getReason());
                if (!TextUtils.isEmpty(specialPrice.getTrip().getDescription()))
                    etNote.setText(specialPrice.getTrip().getDescription());
                dropdownType.select(specialPrice.getSpecialPrice().getType().getCode());
                SpecialPriceProjectStatus status = specialPrice.getSpecialPrice()
                        .getProject_status();
                if (status != null)
                    dropdownStatus.select(status.getCode());
                if (specialPrice.getSpecialPrice().getApproval() != null)
                    dropdownApproval.select(specialPrice.getSpecialPrice().getApproval().getCode
                            () - 1);
                itemSelectPhoto.setFiles(specialPrice.getFiles());
                updateProductList();
                getAssistant();
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Toast.makeText(SpecialPriceCreateActivity.this, throwable.getMessage(), Toast
                        .LENGTH_LONG).show();
                finish();
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
                    if (!TextUtils.isEmpty(specialPrice.getTrip().getAssistant_id())) {
                        assistantId = specialPrice.getTrip().getAssistant_id();
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
                ErrorHandler.getInstance().setException(SpecialPriceCreateActivity.this, throwable);
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
                        ErrorHandler.getInstance().setException(SpecialPriceCreateActivity.this,
                                throwable);
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
                if (configCompanyHiddenField.getSpecial_price_hidden_approval()) {
                    dropdownApproval.setVisibility(View.GONE);
                }
                if (configCompanyHiddenField.getSpecial_price_hidden_year_discount()) {
                    itemSelectDiscount.setVisibility(View.GONE);
                }
                if (configCompanyHiddenField.getSpecial_price_hidden_offer()) {
                    etOffer.setVisibility(View.GONE);
                }
                if (configCompanyHiddenField.getSpecial_price_hidden_project_status()) {
                    dropdownStatus.setVisibility(View.GONE);
                }
                if (configCompanyHiddenField.getSpecial_price_hidden_amount()) {
                    etYearAmount.setVisibility(View.GONE);
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                ErrorHandler.getInstance().setException(SpecialPriceCreateActivity.this,
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
                ErrorHandler.getInstance().setException(SpecialPriceCreateActivity.this,
                        throwable);
            }
        }));
    }

    protected boolean checkInput() {
        String required = getString(R.string.error_msg_required);
        if (TextUtils.isEmpty(etName.getText().toString())) {
            Toast.makeText(this, required + getString(R.string.special_price_name), Toast
                    .LENGTH_LONG).show();
            return false;
        }
        if (TextUtils.isEmpty(etReason.getText().toString())) {
            Toast.makeText(this, required + getString(R.string.special_price_reason), Toast
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
        if (specialPrice.getProducts().size() == 0) {
            Toast.makeText(this, R.string.error_msg_no_product, Toast.LENGTH_LONG).show();
            return false;
        }
        if (dropdownType.getSelected() == Dropdown.VALUE_EMPTY) {
            Toast.makeText(this, required + getString(R.string.special_price_type), Toast
                    .LENGTH_LONG).show();
            return false;
        }
        if (dropdownStatus.getVisibility() == View.VISIBLE && dropdownStatus.getSelected() ==
                Dropdown.VALUE_EMPTY) {
            Toast.makeText(this, required + getString(R.string.special_price_status), Toast
                    .LENGTH_LONG).show();
            return false;
        }
        if (dropdownApproval.getVisibility() == View.VISIBLE && dropdownApproval.getSelected() ==
                Dropdown.VALUE_EMPTY) {
            Toast.makeText(this, required + getString(R.string.special_price_approval), Toast
                    .LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    @OnClick(R.id.btnAddProduct)
    protected void addProduct() {
        Intent intent = new Intent(this, SpecialPriceProductCreateActivity.class);
        startActivityForResult(intent, MyApplication.REQUEST_EDIT_PRODUCT);
    }

    protected View generateProductItem(final SpecialPriceProduct product) {
        final Button btnProduct = new Button(this);
        btnProduct.setText(product.getDescription());
        btnProduct.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        btnProduct.setPaddingRelative(UnitChanger.dpToPx(16), 0, 0, 0);
        btnProduct.setBackgroundResource(R.drawable.bg_special_price_required);
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
                Intent intent = new Intent(SpecialPriceCreateActivity.this,
                        SpecialPriceProductCreateActivity.class);
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
                                        specialPrice.getProducts().remove(product);
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
        for (SpecialPriceProduct product : specialPrice.getProducts()) {
            container.addView(generateProductItem(product));
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case MyApplication.REQUEST_EDIT_PRODUCT:
                    SpecialPriceProduct product = gson.fromJson
                            (data.getStringExtra(MyApplication.INTENT_KEY_PRODUCT),
                                    SpecialPriceProduct.class);
                    if (selectedProduct != null) {
                        specialPrice.getProducts().remove(selectedProduct);
                        selectedProduct = null;
                    }
                    specialPrice.getProducts().add(product);
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
        builder.append(getString(R.string.apply_special_price));
        if (itemSelectCustomer.getCustomer() != null) {
            builder.append("-").append(itemSelectCustomer.getCustomer().getFull_name());
        }
        if (specialPrice.getProducts().size() > 0) {
            builder.append("-").append(specialPrice.getProducts().get(0).getDescription());
        }
        etName.setText(builder.toString());
    }
}
