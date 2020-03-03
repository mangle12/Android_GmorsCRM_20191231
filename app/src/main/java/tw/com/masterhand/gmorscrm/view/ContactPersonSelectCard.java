package tw.com.masterhand.gmorscrm.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.room.record.ContactPerson;
import tw.com.masterhand.gmorscrm.room.setting.User;

public class ContactPersonSelectCard extends RelativeLayout {
    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.tvJob)
    TextView tvJob;

    Context context;
    ContactPerson contactPerson;
    OnSelectListener listener;

    public void setSelectListner(OnSelectListener listener) {
        this.listener = listener;
    }

    public interface OnSelectListener {
        void onSelect(ContactPerson contactPerson);
        void onUnselect(ContactPerson contactPerson);
    }

    public ContactPersonSelectCard(Context context) {
        super(context);
        init(context);
    }

    public ContactPersonSelectCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ContactPersonSelectCard(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context mContext) {
        context = mContext;
        // 連接畫面
        View view = inflate(getContext(), R.layout.card_people_select, this);
        ButterKnife.bind(this, view);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setSelected(!v.isSelected());
                if (listener != null)
                {
                    if (v.isSelected())
                    {
                        listener.onSelect(contactPerson);
                    }
                    else
                    {
                        listener.onUnselect(contactPerson);
                    }
                }
            }
        });
    }

    public ContactPerson getContactPerson() {
        return contactPerson;
    }

    /**
     * 設定聯絡人資料
     */
    public void setContactPerson(ContactPerson contactPerson) {
        this.contactPerson = contactPerson;
        if (!TextUtils.isEmpty(contactPerson.getShowName())) {
            tvName.setText(contactPerson.getShowName());
        }
        if (!TextUtils.isEmpty(contactPerson.getTitle())) {
            tvJob.setText(contactPerson.getTitle());
        }
    }
}
