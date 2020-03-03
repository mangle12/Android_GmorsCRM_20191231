package tw.com.masterhand.gmorscrm.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.TokenManager;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.setting.Department;

public class FilterDepartment extends RelativeLayout {
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.tvSelected)
    TextView tvSelected;

    Context context;

    Department department;

    OnSelectedListener listener;

    boolean isSelectAll = false;

    public interface OnSelectedListener {
        void onSelectedAllDepartment();
        void onSelected(Department department);
    }

    public void setListener(OnSelectedListener listener) {
        this.listener = listener;
    }

    public FilterDepartment(Context context) {
        super(context);
        init(context);
    }

    public FilterDepartment(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FilterDepartment(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context mContext) {
        context = mContext;
        // 連接畫面
        View view = inflate(getContext(), R.layout.item_statistic_filter, this);
        ButterKnife.bind(this, view);
        if (isInEditMode())
            return;
        tvTitle.setText(R.string.filter_department);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getDepartmentList();
            }
        });
        clear();
    }

    private void getDepartmentList() {
        DatabaseHelper.getInstance(context).getDepartment(TokenManager.getInstance().getUser().getCompany_id()).toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Department>>() {

            @Override
            public void accept(final List<Department> departments) throws Exception {
                String[] items = new String[departments.size() + 1];
                items[0] = context.getString(R.string.sample_sort_all);
                for (int i = 0; i < departments.size(); i++) {
                    items[i + 1] = departments.get(i).getName();
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(R.string.filter_department);
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            department = null;
                            isSelectAll = true;
                            tvSelected.setText(R.string.sample_sort_all);
                            if (listener != null)
                                listener.onSelectedAllDepartment();
                        } else {
                            setSelected(departments.get(which - 1));
                        }
                    }
                });
                builder.create().show();
            }
        });
    }

    public void setSelected(Department department) {
        isSelectAll=false;
        this.department = department;
        tvSelected.setText(department.getName());
        if (listener != null)
            listener.onSelected(department);
    }

    public Department getSelected() {
        return department;
    }

    public boolean isSelectAll() {
        return isSelectAll;
    }

    public void clear() {
        isSelectAll=false;
        department = null;
        tvSelected.setText(R.string.empty_show);
    }

}
