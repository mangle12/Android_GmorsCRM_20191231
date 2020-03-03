package tw.com.masterhand.gmorscrm.activity.task;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

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
import tw.com.masterhand.gmorscrm.room.record.SpecialPriceProduct;
import tw.com.masterhand.gmorscrm.room.setting.ConfigCompanyHiddenField;
import tw.com.masterhand.gmorscrm.room.setting.ConfigProductCategorySub;
import tw.com.masterhand.gmorscrm.room.setting.ConfigQuotationProductCategory;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.view.AppbarEdit;
import tw.com.masterhand.gmorscrm.view.Dropdown;
import tw.com.masterhand.gmorscrm.view.EditTextWithTitle;
import tw.com.masterhand.gmorscrm.view.ItemSelectDiscount;

public class SpecialPriceProductCreateActivity extends BaseUserCheckActivity {
    @BindView(R.id.appbar)
    AppbarEdit appbar;
    @BindView(R.id.dropdownCategory)
    Dropdown dropdownCategory;
    @BindView(R.id.dropdownProduct)
    Dropdown dropdownProduct;
    @BindView(R.id.itemSelectDiscount)
    ItemSelectDiscount itemSelectDiscount;
    @BindView(R.id.etCount)
    EditTextWithTitle etCount;
    @BindView(R.id.etAmount)
    EditTextWithTitle etAmount;
    @BindView(R.id.etCompetitor)
    EditTextWithTitle etCompetitor;
    @BindView(R.id.etDescription)
    EditText etDescription;
    @BindView(R.id.etNote)
    EditText etNote;
    @BindView(R.id.containerNote)
    LinearLayout containerNote;

    SpecialPriceProduct product;
    List<ConfigQuotationProductCategory> categories;
    List<ConfigProductCategorySub> products;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_special_price_product_create);
        init();
    }

    @Override
    protected void onUserChecked() {
        appbar.invalidate();
        getHiddenField();
        String productString = getIntent().getStringExtra(MyApplication.INTENT_KEY_PRODUCT);
        if (TextUtils.isEmpty(productString)) {
            product = new SpecialPriceProduct();
        } else {
            product = gson.fromJson(productString, SpecialPriceProduct.class);
            if (!TextUtils.isEmpty(product.getDescription()))
                etDescription.setText(product.getDescription());
            if (product.getCompetitor_amount() > 0)
                etCompetitor.setText(String.valueOf(product.getCompetitor_amount()));
            if (!TextUtils.isEmpty(product.getRemark()))
                etNote.setText(product.getRemark());
            if (product.getDiscount() > 0)
                itemSelectDiscount.setDiscount(product.getDiscount());
            if (product.getAmount() > 0)
                etAmount.setText(String.valueOf(product.getAmount()));
            if (product.getQty() > 0)
                etCount.setText(String.valueOf(product.getQty()));
        }
        if (!getIntent().getBooleanExtra(MyApplication.INTENT_KEY_ENABLE, true)) {
            appbar.disable();
            dropdownCategory.disable();
            dropdownProduct.disable();
            etCount.disable();
            etAmount.disable();
            etCompetitor.disable();
            itemSelectDiscount.disable();
            etDescription.setEnabled(false);
            etNote.setEnabled(false);
        }
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
                            if (categories.get(i).getId().equals(product.getProduct_category_id()))
                                index = i;
                        }
                        dropdownCategory.setItems(items);
                        if (index != Dropdown.VALUE_EMPTY)
                            dropdownCategory.select(index);
                    }
                }));
        mDisposable.add(DatabaseHelper.getInstance(this).getConfigProductCategorySub()
                .toList().observeOn
                        (AndroidSchedulers.mainThread()).subscribe(new Consumer<List<ConfigProductCategorySub>>() {

                    @Override
                    public void accept(List<ConfigProductCategorySub>
                                               configs) throws Exception {
                        products = configs;
                        String[] items = new String[products.size()];
                        int index = Dropdown.VALUE_EMPTY;
                        for (int i = 0; i < products.size(); i++) {
                            items[i] = products.get(i).getName();
                            if (products.get(i).getId().equals(product
                                    .getProduct_category_sub_id()))
                                index = i;
                        }
                        dropdownProduct.setItems(items);
                        if (index != Dropdown.VALUE_EMPTY)
                            dropdownProduct.select(index);
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
                    product.setProduct_category_id(categories.get(dropdownCategory
                            .getSelected()).getId());
                    if (dropdownProduct.getSelected() != Dropdown.VALUE_EMPTY)
                        product.setProduct_category_sub_id(products.get(dropdownProduct.getSelected
                                ()).getId());
                    product.setDescription(etDescription.getText().toString());
                    product.setRemark(etNote.getText().toString());
                    if (!TextUtils.isEmpty(etCount.getText().toString()))
                        product.setQty(Float.valueOf(etCount.getText().toString()));
                    if (!TextUtils.isEmpty(etAmount.getText().toString()))
                        product.setAmount(Float.parseFloat(etAmount.getText().toString()));
                    if (!TextUtils.isEmpty(etCompetitor.getText().toString()))
                        product.setCompetitor_amount(Float.parseFloat(etCompetitor.getText()
                                .toString()));
                    product.setDiscount(itemSelectDiscount.getDiscount());
                    product.setUpdated_at(new Date());
                    Intent intent = new Intent();
                    intent.putExtra(MyApplication.INTENT_KEY_PRODUCT, gson.toJson
                            (product));
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
                if (configCompanyHiddenField.getSpecial_price_product_hidden_remark()) {
                    containerNote.setVisibility(View.GONE);
                }
                if (configCompanyHiddenField.getSpecial_price_product_hidden_product_category
                        ()) {
                    dropdownProduct.setVisibility(View.GONE);
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                ErrorHandler.getInstance().setException(SpecialPriceProductCreateActivity.this,
                        throwable);
            }
        }));
    }

    private boolean checkInput() {
        String required = getString(R.string.error_msg_required);
        if (TextUtils.isEmpty(etDescription.getText().toString())) {
            Toast.makeText(this, required + getString(R.string.special_price_description), Toast
                    .LENGTH_LONG).show();
            return false;
        }
        if (TextUtils.isEmpty(etCount.getText().toString())) {
            Toast.makeText(this, required + getString(R.string.product_count), Toast.LENGTH_LONG)
                    .show();
            return false;
        }
        if (TextUtils.isEmpty(etAmount.getText().toString())) {
            Toast.makeText(this, required + getString(R.string.product_amount), Toast.LENGTH_LONG)
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
