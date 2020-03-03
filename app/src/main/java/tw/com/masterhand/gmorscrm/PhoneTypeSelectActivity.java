package tw.com.masterhand.gmorscrm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import butterknife.BindView;
import tw.com.masterhand.gmorscrm.enums.PhoneType;
import tw.com.masterhand.gmorscrm.tools.UnitChanger;
import tw.com.masterhand.gmorscrm.view.AppbarEdit;

public class PhoneTypeSelectActivity extends BaseUserCheckActivity {
    @BindView(R.id.appbar)
    AppbarEdit appbar;
    @BindView(R.id.container)
    RadioGroup container;
    @BindView(R.id.etType)
    EditText etType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_phone_type);
        init();
    }

    @Override
    protected void onUserChecked() {
        appbar.invalidate();
    }

    private void init() {
        appbar.setTitle(getString(R.string.phone_type));
        appbar.setCompleteListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String type = getType();
                    Intent intent = new Intent();
                    intent.putExtra(MyApplication.INTENT_KEY_PHONE_TYPE, type);
                    setResult(Activity.RESULT_OK, intent);
                } catch (Exception e) {
                    if (e.getMessage() != null)
                        Toast.makeText(PhoneTypeSelectActivity.this, e.getMessage(), Toast
                                .LENGTH_LONG).show();
                    setResult(Activity.RESULT_CANCELED);
                } finally {
                    finish();
                }
            }
        });

        for (PhoneType type : PhoneType.values()) {
            container.addView(generateItem(type));
        }
    }

    private String getType() {
        String type;
        int code = container.getCheckedRadioButtonId();
        PhoneType phoneType = PhoneType.getPhoneTypeByCode(code);
        if (phoneType == PhoneType.OTHER) {
            type = etType.getText().toString();
            if (TextUtils.isEmpty(type))
                type = getString(phoneType.getTitle());
        } else {
            type = getString(phoneType.getTitle());
        }
        return type;
    }

    private View generateItem(final PhoneType type) {
        RadioButton item = new RadioButton(this);
        item.setButtonDrawable(ContextCompat.getDrawable(this, R.drawable.radio_button_selector));
        item.setId(type.getCode());
        item.setText(type.getTitle());
        item.setGravity(Gravity.CENTER_VERTICAL);
        item.setBackgroundColor(ContextCompat.getColor(this, R.color.gray_light));
        if (type == PhoneType.OTHER) {
            item.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    etType.setVisibility(isChecked ? View.VISIBLE : View.INVISIBLE);
                }
            });
        }
        int padding = UnitChanger.dpToPx(8);
        item.setPaddingRelative(padding, padding, padding, padding);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout
                .LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.btn_size));
        item.setLayoutParams(params);
        return item;
    }
}
