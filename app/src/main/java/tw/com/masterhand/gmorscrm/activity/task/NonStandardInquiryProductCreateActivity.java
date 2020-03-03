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

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import tw.com.masterhand.gmorscrm.BaseUserCheckActivity;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.record.NonStandardInquiryProduct;
import tw.com.masterhand.gmorscrm.room.setting.ConfigQuotationProductCategory;
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.view.AppbarEdit;
import tw.com.masterhand.gmorscrm.view.Dropdown;
import tw.com.masterhand.gmorscrm.view.EditTextWithTitle;

public class NonStandardInquiryProductCreateActivity extends BaseUserCheckActivity {
    @BindView(R.id.appbar)
    AppbarEdit appbar;
    @BindView(R.id.dropdownCategory)
    Dropdown dropdownCategory;

    @BindView(R.id.etName)
    EditTextWithTitle etName;
    @BindView(R.id.etCount)
    EditTextWithTitle etCount;
    @BindView(R.id.etCode)
    EditTextWithTitle etCode;
    @BindView(R.id.etSize)
    EditTextWithTitle etSize;
    @BindView(R.id.etTolerance)
    EditTextWithTitle etTolerance;
    @BindView(R.id.etMaterial)
    EditTextWithTitle etMaterial;
    @BindView(R.id.etHardness)
    EditTextWithTitle etHardness;
    @BindView(R.id.etColor)
    EditTextWithTitle etColor;
    @BindView(R.id.etCertification)
    EditTextWithTitle etCertification;
    @BindView(R.id.etWrapping)
    EditTextWithTitle etWrapping;
    @BindView(R.id.etReference)
    EditTextWithTitle etReference;
    @BindView(R.id.etMedium)
    EditTextWithTitle etMedium;
    @BindView(R.id.etTemperature)
    EditTextWithTitle etTemperature;
    @BindView(R.id.etPressure)
    EditTextWithTitle etPressure;
    @BindView(R.id.etStatus)
    EditTextWithTitle etStatus;
    @BindView(R.id.etSpeed)
    EditTextWithTitle etSpeed;
    @BindView(R.id.etYearCount)
    EditTextWithTitle etYearCount;

    @BindView(R.id.etNote)
    EditText etNote;

    NonStandardInquiryProduct product;
    List<ConfigQuotationProductCategory> categories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_non_standard_inquiry_product_create);
        init();
    }

    @Override
    protected void onUserChecked() {
        appbar.invalidate();
        String productString = getIntent().getStringExtra(MyApplication.INTENT_KEY_PRODUCT);
        if (TextUtils.isEmpty(productString)) {
            product = new NonStandardInquiryProduct();
        } else {
            product = gson.fromJson(productString, NonStandardInquiryProduct.class);
            if (!TextUtils.isEmpty(product.getName()))
                etName.setText(product.getName());
            if (!TextUtils.isEmpty(product.getCount()))
                etCount.setText(product.getCount());
            if (!TextUtils.isEmpty(product.getCode_number()))
                etCode.setText(product.getCode_number());
            if (!TextUtils.isEmpty(product.getSize()))
                etSize.setText(product.getSize());
            if (!TextUtils.isEmpty(product.getTolerance()))
                etTolerance.setText(product.getTolerance());
            if (!TextUtils.isEmpty(product.getMaterial()))
                etMaterial.setText(product.getMaterial());
            if (!TextUtils.isEmpty(product.getHardness()))
                etHardness.setText(product.getHardness());
            if (!TextUtils.isEmpty(product.getColor()))
                etColor.setText(product.getColor());
            if (!TextUtils.isEmpty(product.getCertification()))
                etCertification.setText(product.getCertification());
            if (!TextUtils.isEmpty(product.getWrapping()))
                etWrapping.setText(product.getWrapping());
            if (!TextUtils.isEmpty(product.getReference_number()))
                etReference.setText(product.getReference_number());
            if (!TextUtils.isEmpty(product.getMedium()))
                etMedium.setText(product.getMedium());
            if (!TextUtils.isEmpty(product.getTemperature()))
                etTemperature.setText(product.getTemperature());
            if (!TextUtils.isEmpty(product.getPressure()))
                etPressure.setText(product.getPressure());
            if (!TextUtils.isEmpty(product.getStatus()))
                etStatus.setText(product.getStatus());
            if (!TextUtils.isEmpty(product.getSpeed()))
                etSpeed.setText(product.getSpeed());
            if (product.getYear_count() > 0)
                etYearCount.setText(String.valueOf(product.getYear_count()));
            if (!TextUtils.isEmpty(product.getRemark()))
                etNote.setText(product.getRemark());
        }
        if (!getIntent().getBooleanExtra(MyApplication.INTENT_KEY_ENABLE, true)) {
            appbar.disable();
            dropdownCategory.disable();
            etName.disable();
            etCount.disable();
            etCode.disable();
            etSize.disable();
            etTolerance.disable();
            etMaterial.disable();
            etHardness.disable();
            etColor.disable();
            etCertification.disable();
            etWrapping.disable();
            etReference.disable();
            etMedium.disable();
            etTemperature.disable();
            etPressure.disable();
            etStatus.disable();
            etSpeed.disable();
            etYearCount.disable();
            etNote.setEnabled(false);
        }
        mDisposable.add(DatabaseHelper.getInstance(this)
                .getConfigQuotationProductCategoryByCompany()
                .toList().observeOn
                        (AndroidSchedulers.mainThread()).subscribe(new Consumer<List<ConfigQuotationProductCategory>>() {

                    @Override
                    public void accept(List<ConfigQuotationProductCategory>
                                               configQuotationProductCategories) {
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
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        Logger.e(TAG, "error:" + throwable.getMessage());
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
                    product.setName(etName.getText().toString());
                    product.setCount(etCount.getText().toString());
                    product.setCode_number(etCode.getText().toString());
                    product.setSize(etSize.getText().toString());
                    product.setTolerance(etTolerance.getText().toString());
                    product.setMaterial(etMaterial.getText().toString());
                    product.setHardness(etHardness.getText().toString());
                    product.setColor(etColor.getText().toString());
                    product.setCertification(etCertification.getText().toString());
                    product.setWrapping(etWrapping.getText().toString());
                    product.setReference_number(etReference.getText().toString());
                    product.setMedium(etMedium.getText().toString());
                    product.setTemperature(etTemperature.getText().toString());
                    product.setPressure(etPressure.getText().toString());
                    product.setStatus(etStatus.getText().toString());
                    product.setSpeed(etSpeed.getText().toString());
                    if (!TextUtils.isEmpty(etYearCount.getText().toString()))
                        product.setYear_count(Float.parseFloat(etYearCount.getText().toString()));
                    product.setRemark(etNote.getText().toString());
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
        etYearCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String input = etYearCount.getText().toString();
                if (!TextUtils.isEmpty(input) && input.substring(0, 1).equals("0"))
                    etYearCount.setText(input.substring(1));
            }
        });

    }

    private boolean checkInput() {
        String required = getString(R.string.error_msg_required);
        if (TextUtils.isEmpty(etCode.getText().toString())) {
            Toast.makeText(this, required + getString(R.string.product_code)
                    , Toast.LENGTH_LONG).show();
            return false;
        }
        if (TextUtils.isEmpty(etName.getText().toString())) {
            Toast.makeText(this, required + getString(R.string.non_standard_inquiry_product_name)
                    , Toast.LENGTH_LONG).show();
            return false;
        }
        if (TextUtils.isEmpty(etCount.getText().toString())) {
            Toast.makeText(this, required + getString(R.string.inquiry_count), Toast.LENGTH_LONG)
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
