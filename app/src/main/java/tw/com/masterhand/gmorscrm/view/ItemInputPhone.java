package tw.com.masterhand.gmorscrm.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.PhoneTypeSelectActivity;
import tw.com.masterhand.gmorscrm.model.Phone;

public class ItemInputPhone extends LinearLayout {
    @BindView(R.id.btnType)
    Button btnType;
    @BindView(R.id.btnFunction)
    ImageButton btnFunction;
    @BindView(R.id.etNumber)
    EditText etNumber;
    @BindView(R.id.etExtension)
    EditText etExtension;

    Activity context;
    String type;
    SelectPhoneTypeListener selectPhoneTypeListener;

    public ItemInputPhone(Context context) {
        super(context);
        init(context);
    }

    public ItemInputPhone(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ItemInputPhone(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context mContext) {
        // 連接畫面
        View view = inflate(getContext(), R.layout.item_input_phone, this);
        ButterKnife.bind(this, view);
        if (isInEditMode())
            return;
        context = (Activity) mContext;
    }

    @OnClick(R.id.btnType)
    void selectType() {
        if (selectPhoneTypeListener != null)
            selectPhoneTypeListener.onSelectPhoneType(this);
        Intent intent = new Intent(context, PhoneTypeSelectActivity.class);
        context.startActivityForResult(intent, MyApplication.REQUEST_SELECT_PHONE_TYPE);
    }

    public Phone getPhone() {
        Phone phone = new Phone();
        phone.setType(type);
        phone.setTel(etNumber.getText().toString());
        phone.setExt(etExtension.getText().toString());
        return phone;
    }

    public void setPhone(Phone phone) {
        setType(phone.getType());
        etNumber.setText(phone.getTel());
        etExtension.setText(phone.getExt());
    }

    public void setType(String type) {
        this.type = type;
        btnType.setText(type);
        btnType.setTextColor(Color.BLACK);
    }

    public void setFunctionListener(int rid, OnClickListener listener) {
        btnFunction.setImageResource(rid);
        btnFunction.setOnClickListener(listener);
    }

    public void setSelectPhoneTypeListener(SelectPhoneTypeListener listener) {
        selectPhoneTypeListener = listener;
    }

    public interface SelectPhoneTypeListener {
        void onSelectPhoneType(ItemInputPhone item);
    }
}
