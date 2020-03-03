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
import tw.com.masterhand.gmorscrm.model.NonStandardInquiryWithConfig;
import tw.com.masterhand.gmorscrm.model.ProjectWithConfig;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.record.Customer;
import tw.com.masterhand.gmorscrm.room.record.NonStandardInquiry;
import tw.com.masterhand.gmorscrm.room.record.NonStandardInquiryProduct;
import tw.com.masterhand.gmorscrm.room.setting.Admin;
import tw.com.masterhand.gmorscrm.room.setting.ConfigOffice;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.tools.UnitChanger;
import tw.com.masterhand.gmorscrm.view.AppbarEdit;
import tw.com.masterhand.gmorscrm.view.Dropdown;
import tw.com.masterhand.gmorscrm.view.EditTextWithTitle;
import tw.com.masterhand.gmorscrm.view.ItemSelectCustomer;
import tw.com.masterhand.gmorscrm.view.ItemSelectPhoto;
import tw.com.masterhand.gmorscrm.view.ItemSelectProject;

public class NonStandardInquiryCreateActivity extends BaseUserCheckActivity implements
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
    @BindView(R.id.dropdownNeedParity)
    Dropdown dropdownNeedParity;
    @BindView(R.id.dropdownOffice)
    Dropdown dropdownOffice;
    @BindView(R.id.etSpecial)
    EditText etSpecial;
    @BindView(R.id.container)
    LinearLayout container;
    @BindView(R.id.dropdownAssistant)
    Dropdown dropdownAssistant;
    @BindView(R.id.itemSelectPhoto)
    ItemSelectPhoto itemSelectPhoto;
    @BindView(R.id.etNote)
    EditText etNote;

    NonStandardInquiryWithConfig inquiry;

    NonStandardInquiryProduct selectedProduct;

    List<Admin> adminList;
    List<ConfigOffice> officeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_non_standard_inquiry_create);
        init();
    }

    @Override
    protected void onUserChecked() {
        appbar.invalidate();
        if (inquiry != null)
            return;
        String tripId = getIntent().getStringExtra(MyApplication.INTENT_KEY_TRIP);
        if (TextUtils.isEmpty(tripId)) {
            /*新建非標詢價*/
            inquiry = new NonStandardInquiryWithConfig(new NonStandardInquiry());
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
            getOffice();
        } else {
            /*編輯非標詢價*/
            getInquiry(tripId);
        }
    }

    protected void init() {
        etCode.disable();
        itemSelectProject.setItemSelectCustomer(itemSelectCustomer);
        itemSelectCustomer.setShouldCheck(true);
        itemSelectCustomer.addCustomerSelectedListener(this);

        appbar.setTitle(getString(R.string.main_menu_new_task) + "/" + getString(R.string
                .apply_non_standard_inquiry));
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

                    inquiry.getInquiry().setNeed_parity(dropdownNeedParity.getSelected());
                    inquiry.getInquiry().setOffice_id(officeList.get(dropdownOffice
                            .getSelected()).getId());
                    inquiry.getInquiry().setNote(etSpecial.getText().toString());
                    mDisposable.add(DatabaseHelper.getInstance(NonStandardInquiryCreateActivity
                            .this)
                            .saveNonStandardInquiry
                                    (inquiry).observeOn(AndroidSchedulers
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
                                    Toast.makeText(NonStandardInquiryCreateActivity.this, t
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
        mDisposable.add(DatabaseHelper.getInstance(this).getNonStandardInquiryByTrip(tripId)
                .observeOn
                        (AndroidSchedulers.mainThread()).subscribe(new Consumer<NonStandardInquiryWithConfig>() {
                    @Override
                    public void accept(NonStandardInquiryWithConfig result) throws Exception {
                        inquiry = result;
                        getCustomer(inquiry.getTrip().getCustomer_id());
                        getProject(inquiry.getTrip().getProject_id());
                        if (!TextUtils.isEmpty(inquiry.getTrip().getName()))
                            etName.setText(inquiry.getTrip().getName());
                        if (!TextUtils.isEmpty(inquiry.getInquiry().getCode_number()))
                            etCode.setText(inquiry.getInquiry().getCode_number());
                        if (inquiry.getInquiry().getNeed_parity() != Dropdown.VALUE_EMPTY)
                            dropdownNeedParity.select(inquiry.getInquiry().getNeed_parity());
                        if (!TextUtils.isEmpty(inquiry.getInquiry().getNote()))
                            etSpecial.setText(inquiry.getInquiry().getNote());
                        if (!TextUtils.isEmpty(inquiry.getTrip().getDescription()))
                            etNote.setText(inquiry.getTrip().getDescription());
                        itemSelectPhoto.setFiles(inquiry.getFiles());
                        updateProductList();
                        getAssistant();
                        getOffice();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(NonStandardInquiryCreateActivity.this, throwable
                                .getMessage(), Toast
                                .LENGTH_LONG).show();
                        finish();
                    }
                }));
    }

    protected void getOffice() {
        mDisposable.add(DatabaseHelper.getInstance(this).getConfigOffice().toList().subscribe(new Consumer<List<ConfigOffice>>() {

            @Override
            public void accept(List<ConfigOffice> configOffices) throws Exception {
                officeList = configOffices;
                String[] items = new String[officeList.size()];
                int index = Dropdown.VALUE_EMPTY;
                for (int i = 0; i < officeList.size(); i++) {
                    items[i] = officeList.get(i).getName();
                    if (officeList.get(i).getId().equals(inquiry.getInquiry().getOffice_id()))
                        index = i;
                }
                dropdownOffice.setItems(items);
                if (index != Dropdown.VALUE_EMPTY)
                    dropdownOffice.select(index);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                ErrorHandler.getInstance().setException(NonStandardInquiryCreateActivity.this,
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
                ErrorHandler.getInstance().setException(NonStandardInquiryCreateActivity.this,
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
                        ErrorHandler.getInstance().setException(NonStandardInquiryCreateActivity
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
                ErrorHandler.getInstance().setException(NonStandardInquiryCreateActivity.this,
                        throwable);
            }
        }));
    }

    protected boolean checkInput() {
        String required = getString(R.string.error_msg_required);
        if (TextUtils.isEmpty(etName.getText().toString())) {
            Toast.makeText(this, required + getString(R.string.non_standard_inquiry_name), Toast
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
        if (inquiry.getProducts().size() == 0) {
            Toast.makeText(this, R.string.error_msg_no_product, Toast.LENGTH_LONG).show();
            return false;
        }
        if (dropdownNeedParity.getSelected() == Dropdown.VALUE_EMPTY) {
            Toast.makeText(this, required + getString(R.string.non_standard_inquiry_need_parity),
                    Toast
                            .LENGTH_LONG).show();
            return false;
        }
        if (dropdownOffice.getSelected() == Dropdown.VALUE_EMPTY) {
            Toast.makeText(this, required + getString(R.string.non_standard_inquiry_office), Toast
                    .LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    @OnClick(R.id.btnAddProduct)
    protected void addProduct() {
        Intent intent = new Intent(this, NonStandardInquiryProductCreateActivity.class);
        startActivityForResult(intent, MyApplication.REQUEST_EDIT_PRODUCT);
    }

    protected View generateProductItem(final NonStandardInquiryProduct product) {
        final Button btnProduct = new Button(this);
        btnProduct.setText(product.getCode_number());
        btnProduct.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        btnProduct.setPaddingRelative(UnitChanger.dpToPx(16), 0, 0, 0);
        btnProduct.setBackgroundResource(R.drawable.bg_non_standard_inquiry_required);
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
                Intent intent = new Intent(NonStandardInquiryCreateActivity.this,
                        NonStandardInquiryProductCreateActivity.class);
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
        for (NonStandardInquiryProduct product : inquiry.getProducts()) {
            container.addView(generateProductItem(product));
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case MyApplication.REQUEST_EDIT_PRODUCT:
                    NonStandardInquiryProduct product = gson.fromJson
                            (data.getStringExtra(MyApplication.INTENT_KEY_PRODUCT),
                                    NonStandardInquiryProduct.class);
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
        builder.append(getString(R.string.apply_non_standard_inquiry));
        if (itemSelectCustomer.getCustomer() != null) {
            builder.append("-").append(itemSelectCustomer.getCustomer().getFull_name());
        }
        if (inquiry.getProducts().size() > 0) {
            builder.append("-").append(inquiry.getProducts().get(0).getName());
        }
        etName.setText(builder.toString());
    }
}
