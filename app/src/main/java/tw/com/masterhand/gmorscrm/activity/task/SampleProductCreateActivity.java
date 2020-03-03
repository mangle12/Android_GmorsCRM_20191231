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
import tw.com.masterhand.gmorscrm.model.SampleProductWithConfig;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.setting.ConfigCompanyHiddenField;
import tw.com.masterhand.gmorscrm.room.setting.ConfigQuotationProductCategory;
import tw.com.masterhand.gmorscrm.room.setting.ConfigQuotationProductReport;
import tw.com.masterhand.gmorscrm.room.setting.ConfigSampleProductType;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.view.AppbarEdit;
import tw.com.masterhand.gmorscrm.view.Dropdown;
import tw.com.masterhand.gmorscrm.view.EditTextWithTitle;

public class SampleProductCreateActivity extends BaseUserCheckActivity {
    @BindView(R.id.appbar)
    AppbarEdit appbar;
    @BindView(R.id.dropdownCategory)
    Dropdown dropdownCategory;
    @BindView(R.id.dropdownType)
    Dropdown dropdownType;
    @BindView(R.id.dropdownReport)
    Dropdown dropdownReport;
//    @BindView(R.id.dropdownCompanyStock)
//    Dropdown dropdownCompanyStock;
//    @BindView(R.id.dropdownProviderStock)
//    Dropdown dropdownProviderStock;

    @BindView(R.id.etCount)
    EditTextWithTitle etCount;
    @BindView(R.id.etYearCount)
    EditTextWithTitle etYearCount;
    @BindView(R.id.etCode)
    EditTextWithTitle etCode;
    @BindView(R.id.etPrice)
    EditTextWithTitle etPrice;
    @BindView(R.id.etTotal)
    EditTextWithTitle etTotal;
    @BindView(R.id.etOther)
    EditText etOther;

    SampleProductWithConfig product;
    List<ConfigQuotationProductCategory> categories;
    List<ConfigSampleProductType> types;
    List<ConfigQuotationProductReport> reports;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_product_create);
        init();
    }

    @Override
    protected void onUserChecked() {
        appbar.invalidate();
        getHiddenField();
        final String productString = getIntent().getStringExtra(MyApplication.INTENT_KEY_PRODUCT);
        if (TextUtils.isEmpty(productString)) {
            product = new SampleProductWithConfig();
        } else {
            product = gson.fromJson(productString, SampleProductWithConfig.class);
//            dropdownCompanyStock.select(product.getProduct().getInventory_factory());
//            dropdownProviderStock.select(product.getProduct().getInventory_supplier());
            etCount.setText(String.valueOf(product.getProduct().getQty_apply()));
            etYearCount.setText(String.valueOf(product.getProduct().getQty_annual()));
            etCode.setText(product.getProduct().getProduct_model());
            etPrice.setText(String.valueOf(product.getProduct().getAmount()));
            etTotal.setText(String.valueOf(product.getProduct().getTotal()));
            etOther.setText(product.getProduct().getDescription());
        }
        if (!getIntent().getBooleanExtra(MyApplication.INTENT_KEY_ENABLE, true)) {
            appbar.disable();
            dropdownCategory.disable();
            dropdownType.disable();
            dropdownReport.disable();
//            dropdownCompanyStock.disable();
//            dropdownProviderStock.disable();
            etCount.disable();
            etYearCount.disable();
            etCode.disable();
            etOther.setEnabled(false);
            etPrice.disable();
        } else {
            etPrice.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    updateTotal();
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
                    updateTotal();
                }
            });
        }
        etTotal.setText(String.valueOf(product.getProduct().getTotal()));
        mDisposable.add(DatabaseHelper.getInstance(this)
                .getConfigQuotationProductCategoryByCompany()
                .toList().observeOn
                        (AndroidSchedulers.mainThread()).subscribe(new Consumer<List<ConfigQuotationProductCategory>>() {

                    @Override
                    public void accept(List<ConfigQuotationProductCategory>
                                               configQuotationProductCategories) throws Exception {
                        categories = configQuotationProductCategories;
                        String[] items = new String[categories.size()];
                        int index = Dropdown.VALUE_EMPTY;
                        for (int i = 0; i < categories.size(); i++) {
                            items[i] = categories.get(i).getName();
                            if (categories.get(i).getId().equals(product.getProduct()
                                    .getProduct_category_id()))
                                index = i;
                        }
                        dropdownCategory.setItems(items);
                        if (index != Dropdown.VALUE_EMPTY)
                            dropdownCategory.select(index);
                    }
                }));
        mDisposable.add(DatabaseHelper.getInstance(this).getConfigSampleProductType().toList()
                .observeOn
                        (AndroidSchedulers.mainThread()).subscribe(new Consumer<List<ConfigSampleProductType>>() {

                    @Override
                    public void accept(List<ConfigSampleProductType>
                                               configs) throws Exception {
                        types = configs;
                        String[] items = new String[types.size()];
                        int index = Dropdown.VALUE_EMPTY;
                        for (int i = 0; i < types.size(); i++) {
                            items[i] = types.get(i).getName();
                            if (types.get(i).getId().equals(product.getProduct()
                                    .getProduct_type_id()))
                                index = i;
                        }
                        dropdownType.setItems(items);
                        if (index != Dropdown.VALUE_EMPTY)
                            dropdownType.select(index);
                    }
                }));
        mDisposable.add(DatabaseHelper.getInstance(this).getConfigQuotationProductReport().toList
                ().observeOn
                (AndroidSchedulers.mainThread()).subscribe(new Consumer<List<ConfigQuotationProductReport>>() {

            @Override
            public void accept(List<ConfigQuotationProductReport> configQuotationProductReports)
                    throws Exception {
                reports = configQuotationProductReports;
                Logger.e(TAG, "report id list:" + product.getProduct().getProduct_report_id()
                        .toString());
                String[] items = new String[reports.size()];
                boolean[] selected = new boolean[reports.size()];
                for (int i = 0; i < reports.size(); i++) {
                    items[i] = reports.get(i).getName();
                    for (String selectedId : product.getProduct().getProduct_report_id()) {
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
        etTotal.disable();
        appbar.setTitle(getString(R.string.add_product));
        appbar.setCompleteListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 完成
                if (checkInput()) {
                    product.getProduct().setProduct_category_id(categories.get(dropdownCategory
                            .getSelected()).getId());
                    product.setCategory(categories.get(dropdownCategory.getSelected()));

                    product.getProduct().setProduct_type_id(types.get(dropdownType.getSelected())
                            .getId());
                    product.setType(types.get(dropdownType.getSelected()));

                    List<String> reportIdList = new ArrayList<>();
                    for (int i = 0; i < dropdownReport.getMultiSelected().length; i++) {
                        if (dropdownReport.getMultiSelected()[i]) {
                            product.getReports().add(reports.get(i));
                            reportIdList.add(reports.get(i).getId());
                        }
                    }
                    product.getProduct().setProduct_report_id(reportIdList);

//                    product.getProduct().setInventory_supplier(dropdownProviderStock
// .getSelected());
//                    product.getProduct().setInventory_factory(dropdownCompanyStock.getSelected());
                    product.getProduct().setQty_apply(Float.valueOf(etCount.getText().toString
                            ()));
                    if (!TextUtils.isEmpty(etYearCount.getText().toString()))
                        product.getProduct().setQty_annual(Float.valueOf(etYearCount.getText()
                                .toString()));
                    product.getProduct().setProduct_model(etCode.getText().toString());
                    if (!TextUtils.isEmpty(etPrice.getText().toString()))
                        product.getProduct().setAmount(Float.valueOf(etPrice.getText().toString()));
                    product.getProduct().setDescription(etOther.getText().toString());
                    product.getProduct().setUpdated_at(new Date());
                    Intent intent = new Intent();
                    intent.putExtra(MyApplication.INTENT_KEY_PRODUCT, gson.toJson
                            (product));
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            }
        });

    }

    void updateTotal() {
        float count = 0;
        float amount = 0;
        try {
            if (!TextUtils.isEmpty(etPrice.getText().toString()))
                amount = Float.valueOf(etPrice.getText().toString());
            if (!TextUtils.isEmpty(etCount.getText().toString()))
                count = Float.valueOf(etCount.getText().toString());
        } catch (Exception e) {
            Logger.e(TAG, "Exception:" + e.getMessage());
        }
        float total = count * amount;
        etTotal.setText(String.valueOf(total));
    }

    void getHiddenField() {
        mDisposable.add(DatabaseHelper.getInstance(this).getConfigCompanyHiddenField(TokenManager
                .getInstance().getUser().getCompany_id()).observeOn
                (AndroidSchedulers.mainThread()).subscribe(new Consumer<ConfigCompanyHiddenField>
                () {

            @Override
            public void accept(ConfigCompanyHiddenField configCompanyHiddenField) throws
                    Exception {
                if (configCompanyHiddenField.getSampleProduct_hidden_product_report_id()) {
                    dropdownReport.setVisibility(View.GONE);
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                ErrorHandler.getInstance().setException(SampleProductCreateActivity.this,
                        throwable);
            }
        }));
    }

    private boolean checkInput() {
        String required = getString(R.string.error_msg_required);
        if (TextUtils.isEmpty(etCount.getText().toString())) {
            Toast.makeText(this, required + getString(R.string.sample_item_count), Toast
                    .LENGTH_LONG).show();
            return false;
        }
        if (TextUtils.isEmpty(etCode.getText().toString())) {
            Toast.makeText(this, required + getString(R.string.sample_item_code), Toast
                    .LENGTH_LONG).show();
            return false;
        }
        if (dropdownCategory.getSelected() == Dropdown.VALUE_EMPTY) {
            Toast.makeText(this, required + getString(R.string.sample_item_category), Toast
                    .LENGTH_LONG).show();
            return false;
        }
        if (dropdownType.getSelected() == Dropdown.VALUE_EMPTY) {
            Toast.makeText(this, required + getString(R.string.sample_item_type), Toast
                    .LENGTH_LONG).show();
            return false;
        }
//        if (dropdownCompanyStock.getSelected() == Dropdown.VALUE_EMPTY) {
//            Toast.makeText(this, required + getString(R.string.sample_item_company_stock), Toast
//                    .LENGTH_LONG).show();
//            return false;
//        }
        return true;
    }

}
