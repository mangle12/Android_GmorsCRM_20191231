package tw.com.masterhand.gmorscrm.activity.task;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import tw.com.masterhand.gmorscrm.BaseUserCheckActivity;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.TokenManager;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.record.QuotationProduct;
import tw.com.masterhand.gmorscrm.room.setting.ConfigCompanyHiddenField;
import tw.com.masterhand.gmorscrm.room.setting.ConfigQuotationProductCategory;
import tw.com.masterhand.gmorscrm.room.setting.ConfigQuotationProductReport;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.view.AppbarEdit;
import tw.com.masterhand.gmorscrm.view.Dropdown;
import tw.com.masterhand.gmorscrm.view.EditTextWithTitle;
import tw.com.masterhand.gmorscrm.view.ItemSelectDiscount;

public class QuotationProductCreateActivity extends BaseUserCheckActivity {
    @BindView(R.id.appbar)
    AppbarEdit appbar;
    @BindView(R.id.dropdownCategory)
    Dropdown dropdownCategory;
    @BindView(R.id.dropdownReport)
    Dropdown dropdownReport;
    @BindView(R.id.itemSelectDiscount)
    ItemSelectDiscount itemSelectDiscount;
    @BindView(R.id.etModel)
    EditTextWithTitle etModel;
    @BindView(R.id.etCount)
    EditTextWithTitle etCount;
    @BindView(R.id.etDescription)
    EditTextWithTitle etDescription;
    @BindView(R.id.etAmount)
    EditTextWithTitle etAmount;
    @BindView(R.id.etOffer)
    EditTextWithTitle etOffer;
    @BindView(R.id.etNote)
    EditText etNote;

    QuotationProduct quotationProduct;
    List<ConfigQuotationProductCategory> categories;
    List<ConfigQuotationProductReport> reports;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quotation_product_create);
        init();
    }

    @Override
    protected void onUserChecked() {
        appbar.invalidate();
        getHiddenField();
        String productString = getIntent().getStringExtra(MyApplication.INTENT_KEY_PRODUCT);
        if (TextUtils.isEmpty(productString)) {
            quotationProduct = new QuotationProduct();
        } else {
            quotationProduct = gson.fromJson(productString, QuotationProduct.class);
            if (!TextUtils.isEmpty(quotationProduct.getProduct_model()))
                etModel.setText(quotationProduct.getProduct_model());
            if (!TextUtils.isEmpty(quotationProduct.getDescription()))
                etDescription.setText(quotationProduct.getDescription());
            if (!TextUtils.isEmpty(quotationProduct.getNon_standard_number()))
                etOffer.setText(quotationProduct.getNon_standard_number());
            if (!TextUtils.isEmpty(quotationProduct.getRemark()))
                etNote.setText(quotationProduct.getRemark());
            if (quotationProduct.getDiscount() > 0)
                itemSelectDiscount.setDiscount(quotationProduct.getDiscount());
            if (quotationProduct.getAmount() > 0)
                etAmount.setText(String.valueOf(quotationProduct.getAmount()));
            if (quotationProduct.getQty() > 0)
                etCount.setText(String.valueOf(quotationProduct.getQty()));
        }
        if (!getIntent().getBooleanExtra(MyApplication.INTENT_KEY_ENABLE, true)) {
            appbar.disable();
            dropdownCategory.disable();
            dropdownReport.disable();
            etModel.disable();
            etCount.disable();
            etDescription.disable();
            etAmount.disable();
            itemSelectDiscount.disable();
            etOffer.disable();
            etNote.setEnabled(false);
        }
        mDisposable.add(DatabaseHelper.getInstance(this)
                .getConfigQuotationProductCategoryByCompany().toList().observeOn
                        (AndroidSchedulers.mainThread()).subscribe(new Consumer<List<ConfigQuotationProductCategory>>() {

                    @Override
                    public void accept(List<ConfigQuotationProductCategory>
                                               configQuotationProductCategories) throws Exception {
                        categories = configQuotationProductCategories;
                        String[] items = new String[categories.size()];
                        int index = Dropdown.VALUE_EMPTY;
                        for (int i = 0; i < categories.size(); i++) {
                            items[i] = categories.get(i).getName();
                            if (categories.get(i).getId().equals(quotationProduct.getProduct_category_id()))
                                index = i;
                        }
                        dropdownCategory.setItems(items);
                        if (index != Dropdown.VALUE_EMPTY)
                            dropdownCategory.select(index);
                    }
                }));
        mDisposable.add(DatabaseHelper.getInstance(this).getConfigQuotationProductReport().toList
                ().observeOn
                (AndroidSchedulers.mainThread()).subscribe(new Consumer<List<ConfigQuotationProductReport>>() {

            @Override
            public void accept(List<ConfigQuotationProductReport> configQuotationProductReports)
                    throws Exception {
                reports = configQuotationProductReports;
                String[] items = new String[reports.size()];
                boolean[] selected = new boolean[reports.size()];
                for (int i = 0; i < reports.size(); i++) {
                    items[i] = reports.get(i).getName();
                    for (String selectedId : quotationProduct.getProduct_report_id()) {
                        if (selectedId.equals(reports.get(i).getId())) {
                            selected[i] = true;
                            break;
                        }
                    }
                }
                dropdownReport.setItems(items);
                dropdownReport.enableMultiSelect();
                dropdownReport.multiSelect(selected);
            }
        }));
    }

    private void init() {
        appbar.setTitle(getString(R.string.add_product));
        appbar.setCompleteListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 完成
                if (checkInput()) {
                    quotationProduct.setProduct_category_id(categories.get(dropdownCategory
                            .getSelected()).getId());

                    List<String> reportIdList = new ArrayList<>();
                    for (int i = 0; i < dropdownReport.getMultiSelected().length; i++) {
                        if (dropdownReport.getMultiSelected()[i]) {
                            reportIdList.add(reports.get(i).getId());
                        }
                    }
                    quotationProduct.setProduct_report_id(reportIdList);

                    quotationProduct.setProduct_model(etModel.getText().toString());
                    quotationProduct.setDescription(etDescription.getText().toString());
                    quotationProduct.setRemark(etNote.getText().toString());
                    quotationProduct.setNon_standard_number(etOffer.getText().toString());
                    if (!TextUtils.isEmpty(etCount.getText().toString()))
                        quotationProduct.setQty(Float.valueOf(etCount.getText().toString()));
                    if (!TextUtils.isEmpty(etAmount.getText().toString()))
                        quotationProduct.setAmount(Float.parseFloat(etAmount.getText().toString()));
                    quotationProduct.setDiscount(itemSelectDiscount.getDiscount());
                    quotationProduct.setUpdated_at(new Date());
                    Intent intent = new Intent();
                    intent.putExtra(MyApplication.INTENT_KEY_PRODUCT, gson.toJson
                            (quotationProduct));
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            }
        });
        etCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String input = etCount.getText().toString();
                if (!TextUtils.isEmpty(input) && input.substring(0, 1).equals("0"))
                    etCount.setText(input.substring(1));
            }
        });

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
                if (configCompanyHiddenField.getQuotationProduct_hidden_amount()) {
                    etAmount.setVisibility(View.GONE);
                }
                if (configCompanyHiddenField.getQuotationProduct_hidden_description()) {
                    etDescription.setVisibility(View.GONE);
                }
                if (configCompanyHiddenField.getQuotationProduct_hidden_non_standard_number()) {
                    etOffer.setVisibility(View.GONE);
                }
                if (configCompanyHiddenField.getQuotationProduct_hidden_product_report_id()) {
                    dropdownReport.setVisibility(View.GONE);
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                ErrorHandler.getInstance().setException(QuotationProductCreateActivity.this,
                        throwable);
            }
        }));
    }

    private boolean checkInput() {
        String required = getString(R.string.error_msg_required);
        if (TextUtils.isEmpty(etModel.getText().toString())) {
            Toast.makeText(this, required + getString(R.string.product_model), Toast.LENGTH_LONG)
                    .show();
            return false;
        }
        if (TextUtils.isEmpty(etCount.getText().toString())) {
            Toast.makeText(this, required + getString(R.string.product_count), Toast.LENGTH_LONG)
                    .show();
            return false;
        }
        if (dropdownCategory.getSelected() == Dropdown.VALUE_EMPTY) {
            Toast.makeText(this, required + getString(R.string.product_category), Toast
                    .LENGTH_LONG).show();
            return false;
        }
        return true;
    }

}
