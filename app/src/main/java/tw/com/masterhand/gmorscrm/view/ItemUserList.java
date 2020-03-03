package tw.com.masterhand.gmorscrm.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.record.ContactPerson;
import tw.com.masterhand.gmorscrm.room.setting.Department;
import tw.com.masterhand.gmorscrm.room.setting.User;

public class ItemUserList extends LinearLayout {
    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.tvLocation)
    TextView tvLocation;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.tvDepartment)
    TextView tvDepartment;

    Context context;
    User user;

    public ItemUserList(Context context) {
        super(context);
        init(context);
    }

    public ItemUserList(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ItemUserList(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context mContext) {
        context = mContext;
        // 連接畫面
        View view = inflate(getContext(), R.layout.item_contacter_list, this);
        ButterKnife.bind(this, view);
    }

    public void setUser(User user) {
        if (user == null)
            return;
        this.user = user;
        if (!TextUtils.isEmpty(user.getShowName()))
            tvName.setText(user.getShowName());
        if (!TextUtils.isEmpty(user.getTitle()))
            tvTitle.setText(user.getTitle());
        if (user.getAddress() != null)
            tvLocation.setText(user.getAddress().getArea());
        if (!TextUtils.isEmpty(user.getDepartment_id()))
            DatabaseHelper.getInstance(context).getDepartmentById(user.getDepartment_id())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Department>() {

                @Override
                public void accept(Department department) throws Exception {
                    tvDepartment.setText(department.getName());
                }
            });
    }

    public void setContactPerson(ContactPerson contactPerson) {
        if (!TextUtils.isEmpty(contactPerson.getShowName()))
            tvName.setText(contactPerson.getShowName());
        if (!TextUtils.isEmpty(contactPerson.getTitle()))
            tvTitle.setText(contactPerson.getTitle());
        if (contactPerson.getAddress() != null)
            tvLocation.setText(contactPerson.getAddress().getArea());
        if (!TextUtils.isEmpty(contactPerson.getCustomer_department_name()))
            tvDepartment.setText(contactPerson.getCustomer_department_name());
    }
}
