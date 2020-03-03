package tw.com.masterhand.gmorscrm.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.model.Address;
import tw.com.masterhand.gmorscrm.room.setting.ConfigCity;
import tw.com.masterhand.gmorscrm.room.setting.ConfigCountry;

public class ItemSelectLocation extends RelativeLayout {
    @BindView(R.id.btnCountry)
    Button btnCountry;
    @BindView(R.id.btnCity)
    Button btnCity;

    @BindView(R.id.etAddress)
    EditTextWithTitle etAddress;

    Context context;
    Address address;
    OnSelectListener listener;

    public interface OnSelectListener {
        void onSelectCountry();
        void onSelectCity(String countryId);
    }

    public ItemSelectLocation(Context context) {
        super(context);
        init(context);
    }

    public ItemSelectLocation(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ItemSelectLocation(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context mContext) {
        context = mContext;
        address = new Address();
        // 連接畫面
        View view = inflate(getContext(), R.layout.item_select_location, this);
        ButterKnife.bind(this, view);
    }

    @OnClick(R.id.btnCountry)
    void onCountrySelect() {
        listener.onSelectCountry();
    }

    @OnClick(R.id.btnCity)
    void onCitySelect() {
        if (address.getCountry() == null) {
            listener.onSelectCountry();
        } else {
            listener.onSelectCity(address.getCountry().getId());
        }
    }

    public void setOnSelectListener(OnSelectListener listener) {
        this.listener = listener;
    }

    public void selectCountry(ConfigCountry config) {
        address.setCountry(config);
        address.setCity(null);
        btnCountry.setText(address.getCountry().getName());
        btnCity.setText(R.string.hint_city);
    }

    public void selectCity(ConfigCity config) {
        address.setCity(config);
        btnCity.setText(address.getCity().getName());
    }

    public void setAddress(Address address) {
        if (address == null)
            return;
        this.address = address;
        if (address.getCountry() != null)
            btnCountry.setText(address.getCountry().getName());
        if (address.getCity() != null)
            btnCity.setText(address.getCity().getName());
        if (!TextUtils.isEmpty(address.getAddress()))
            etAddress.setText(address.getAddress());
    }

    public Address getAddress() {
        address.setAddress(etAddress.getText().toString());
        return address;
    }

    public void disableAddress() {
        etAddress.setVisibility(GONE);
    }
}
