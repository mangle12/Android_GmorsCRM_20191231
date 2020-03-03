package tw.com.masterhand.gmorscrm.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.room.record.ContactPerson;

public class ItemContacterList extends LinearLayout {
    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.tvLocation)
    TextView tvLocation;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.tvDepartment)
    TextView tvDepartment;

    Context context;
    ContactPerson contactPerson;

    public ItemContacterList(Context context) {
        super(context);
        init(context);
    }

    public ItemContacterList(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ItemContacterList(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context mContext) {
        context = mContext;
        // 連接畫面
        View view = inflate(getContext(), R.layout.item_contacter_list, this);
        ButterKnife.bind(this, view);
    }

    public void setContactPerson(ContactPerson contact) {
        contactPerson = contact;
        tvName.setText(contactPerson.getShowName());
        tvDepartment.setText(contactPerson.getCustomer_department_name());
        tvLocation.setText(contactPerson.getAddress().getArea());
        tvTitle.setText(contactPerson.getTitle());
    }
}
