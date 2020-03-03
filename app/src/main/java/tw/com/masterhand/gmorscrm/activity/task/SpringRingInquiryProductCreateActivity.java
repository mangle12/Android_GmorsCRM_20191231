package tw.com.masterhand.gmorscrm.activity.task;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import tw.com.masterhand.gmorscrm.BaseUserCheckActivity;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.room.record.SpringRingInquiryProduct;
import tw.com.masterhand.gmorscrm.tools.TimeFormater;
import tw.com.masterhand.gmorscrm.view.AppbarEdit;
import tw.com.masterhand.gmorscrm.view.Dropdown;
import tw.com.masterhand.gmorscrm.view.EditTextWithTitle;

public class SpringRingInquiryProductCreateActivity extends BaseUserCheckActivity {
    @BindView(R.id.appbar)
    AppbarEdit appbar;

    @BindView(R.id.dropdownBackPressure)
    Dropdown dropdownBackPressure;
    @BindView(R.id.etMedium)
    EditTextWithTitle etMedium;
    @BindView(R.id.etEquipment)
    EditTextWithTitle etEquipment;
    @BindView(R.id.etPart)
    EditTextWithTitle etPart;
    @BindView(R.id.etSealedType)
    EditTextWithTitle etSealedType;
    @BindView(R.id.etProductType)
    EditTextWithTitle etProductType;
    @BindView(R.id.etGrooveDiameter)
    EditTextWithTitle etGrooveDiameter;
    @BindView(R.id.etGrooveOuterDiameter)
    EditTextWithTitle etGrooveOuterDiameter;
    @BindView(R.id.etGrooveWidth)
    EditTextWithTitle etGrooveWidth;
    @BindView(R.id.etMaterial)
    EditTextWithTitle etMaterial;
    @BindView(R.id.etCount)
    EditTextWithTitle etCount;
    @BindView(R.id.tvDate)
    TextView tvDate;

    @BindView(R.id.etOther)
    EditTextWithTitle etOther;
    @BindView(R.id.etNote)
    EditText etNote;

    SpringRingInquiryProduct product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spring_ring_inquiry_product_create);
        init();
    }


    @Override
    protected void onUserChecked() {
        appbar.invalidate();
    }

    private void init() {
        appbar.setTitle(getString(R.string.add_product));
        appbar.setCompleteListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 完成
                if (checkInput()) {
                    product.setBack_pressure(dropdownBackPressure.getSelected());
                    product.setMedium(etMedium.getText().toString());
                    product.setEquipment(etEquipment.getText().toString());
                    product.setPart_number(etPart.getText().toString());
                    product.setSealed_type(etSealedType.getText().toString());
                    product.setProduct_type(etProductType.getText().toString());
                    product.setGroove_diameter(etGrooveDiameter.getText().toString());
                    product.setGroove_outer_diameter(etGrooveOuterDiameter.getText().toString());
                    product.setGroove_width(etGrooveWidth.getText().toString());
                    product.setMaterial_number(etMaterial.getText().toString());
                    product.setCount(etCount.getText().toString());
                    product.setOther(etOther.getText().toString());
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
        String productString = getIntent().getStringExtra(MyApplication.INTENT_KEY_PRODUCT);
        if (TextUtils.isEmpty(productString)) {
            product = new SpringRingInquiryProduct();
        } else {
            product = gson.fromJson(productString, SpringRingInquiryProduct.class);
            if (product.getBack_pressure() != Dropdown.VALUE_EMPTY)
                dropdownBackPressure.select(product.getBack_pressure());
            if (!TextUtils.isEmpty(product.getMedium()))
                etMedium.setText(product.getMedium());
            if (!TextUtils.isEmpty(product.getEquipment()))
                etEquipment.setText(product.getEquipment());
            if (!TextUtils.isEmpty(product.getPart_number()))
                etPart.setText(product.getPart_number());
            if (!TextUtils.isEmpty(product.getSealed_type()))
                etSealedType.setText(product.getSealed_type());
            if (!TextUtils.isEmpty(product.getProduct_type()))
                etProductType.setText(product.getProduct_type());
            if (!TextUtils.isEmpty(product.getGroove_diameter()))
                etGrooveDiameter.setText(product.getGroove_diameter());
            if (!TextUtils.isEmpty(product.getGroove_outer_diameter()))
                etGrooveOuterDiameter.setText(product.getGroove_outer_diameter());
            if (!TextUtils.isEmpty(product.getGroove_width()))
                etGrooveWidth.setText(product.getGroove_width());
            if (!TextUtils.isEmpty(product.getMaterial_number()))
                etMaterial.setText(product.getMaterial_number());
            if (!TextUtils.isEmpty(product.getCount()))
                etCount.setText(product.getCount());
            if (product.getDelivery_date() != null)
                tvDate.setText(TimeFormater.getInstance().toDateFormat(product.getDelivery_date()));
            if (!TextUtils.isEmpty(product.getOther()))
                etOther.setText(product.getOther());
            if (!TextUtils.isEmpty(product.getRemark()))
                etNote.setText(product.getRemark());
        }
        if (!getIntent().getBooleanExtra(MyApplication.INTENT_KEY_ENABLE, true)) {
            appbar.disable();
            dropdownBackPressure.disable();
            etMedium.disable();
            etEquipment.disable();
            etPart.disable();
            etSealedType.disable();
            etProductType.disable();
            etGrooveDiameter.disable();
            etGrooveOuterDiameter.disable();
            etGrooveWidth.disable();
            etMaterial.disable();
            etCount.disable();
            etOther.disable();
            etNote.setEnabled(false);
        } else {
            tvDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Calendar toBeChanged = Calendar.getInstance(Locale.getDefault());
                    if (product.getDelivery_date() != null)
                        toBeChanged.setTime(product.getDelivery_date());
                    DatePickerDialog datePicker = new DatePickerDialog
                            (SpringRingInquiryProductCreateActivity.this, new DatePickerDialog
                                    .OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker datePicker, int year, int month,
                                                      int day) {
                                    toBeChanged.set(Calendar.YEAR, year);
                                    toBeChanged.set(Calendar.MONTH, month);
                                    toBeChanged.set(Calendar.DAY_OF_MONTH, day);
                                    product.setDelivery_date(toBeChanged.getTime());
                                    tvDate.setText(TimeFormater.getInstance().toDateFormat(product
                                            .getDelivery_date()));
                                    tvDate.setTextColor(ContextCompat.getColor
                                            (SpringRingInquiryProductCreateActivity
                                                    .this, R.color.orange));
                                }
                            }, toBeChanged.get(Calendar.YEAR), toBeChanged.get(Calendar.MONTH),
                                    toBeChanged.get
                                            (Calendar.DAY_OF_MONTH));
                    datePicker.show();
                }
            });
        }
    }

    private boolean checkInput() {
        String required = getString(R.string.error_msg_required);
        if (TextUtils.isEmpty(etMedium.getText().toString())) {
            Toast.makeText(this, required + getString(R.string.spring_ring_inquiry_product_medium)
                    , Toast
                            .LENGTH_LONG).show();
            return false;
        }
        if (TextUtils.isEmpty(etPart.getText().toString())) {
            Toast.makeText(this, required + getString(R.string.spring_ring_inquiry_product_part)
                    , Toast
                            .LENGTH_LONG).show();
            return false;
        }
        if (TextUtils.isEmpty(etSealedType.getText().toString())) {
            Toast.makeText(this, required + getString(R.string
                    .spring_ring_inquiry_product_sealed_type), Toast.LENGTH_LONG)
                    .show();
            return false;
        }
        if (TextUtils.isEmpty(etGrooveDiameter.getText().toString())) {
            Toast.makeText(this, required + getString(R.string
                    .spring_ring_inquiry_product_groove_diameter), Toast.LENGTH_LONG)
                    .show();
            return false;
        }
        if (TextUtils.isEmpty(etGrooveOuterDiameter.getText().toString())) {
            Toast.makeText(this, required + getString(R.string
                    .spring_ring_inquiry_product_groove_outer_diameter), Toast.LENGTH_LONG)
                    .show();
            return false;
        }
        if (TextUtils.isEmpty(etGrooveWidth.getText().toString())) {
            Toast.makeText(this, required + getString(R.string
                    .spring_ring_inquiry_product_groove_width), Toast.LENGTH_LONG)
                    .show();
            return false;
        }
        if (TextUtils.isEmpty(etMaterial.getText().toString())) {
            Toast.makeText(this, required + getString(R.string
                    .spring_ring_inquiry_product_material), Toast.LENGTH_LONG)
                    .show();
            return false;
        }
        if (TextUtils.isEmpty(etCount.getText().toString())) {
            Toast.makeText(this, required + getString(R.string.spring_ring_inquiry_product_count)
                    , Toast.LENGTH_LONG)
                    .show();
            return false;
        }
        if (product.getDelivery_date() == null) {
            Toast.makeText(this, required + getString(R.string.spring_ring_inquiry_product_date),
                    Toast.LENGTH_LONG).show();
            return false;
        }
        if (dropdownBackPressure.getSelected() == Dropdown.VALUE_EMPTY) {
            Toast.makeText(this, required + getString(R.string
                            .spring_ring_inquiry_product_back_pressure),
                    Toast
                            .LENGTH_LONG).show();
            return false;
        }
        return true;
    }

}
