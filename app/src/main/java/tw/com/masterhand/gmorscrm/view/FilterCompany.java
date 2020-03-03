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
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.setting.Company;

public class FilterCompany extends RelativeLayout {
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.tvSelected)
    TextView tvSelected;

    Context context;

    Company company;

    OnSelectedListener listener;

    boolean isSelectAll = false;

    public interface OnSelectedListener {
        void onSelectedAllCompany();
        void onSelected(Company company);
    }

    public void setListener(OnSelectedListener listener) {
        this.listener = listener;
    }

    public FilterCompany(Context context) {
        super(context);
        init(context);
    }

    public FilterCompany(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FilterCompany(Context context, AttributeSet attrs, int defStyle) {
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
        tvTitle.setText(R.string.filter_company);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getCompanyList();
            }
        });
        clear();
    }

    private void getCompanyList() {
        DatabaseHelper.getInstance(context).getCompany().toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Company>>() {

            @Override
            public void accept(final List<Company> companies) throws Exception {
                String[] items = new String[companies.size() + 1];
                items[0] = context.getString(R.string.sample_sort_all);
                for (int i = 0; i < companies.size(); i++) {
                    items[i + 1] = companies.get(i).getName();
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(R.string.filter_company);
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            company = null;
                            tvSelected.setText(R.string.sample_sort_all);
                            isSelectAll = true;
                            if (listener != null)
                                listener.onSelectedAllCompany();
                        } else
                            setSelected(companies.get(which - 1));
                    }
                });
                builder.create().show();
            }
        });
    }

    public void setSelected(Company company) {
        isSelectAll = false;
        this.company = company;
        tvSelected.setText(company.getName());
        if (listener != null)
            listener.onSelected(company);
    }

    public Company getSelected() {
        return company;
    }

    public boolean isSelectAll() {
        return isSelectAll;
    }

    public void clear() {
        isSelectAll = false;
        company = null;
        tvSelected.setText(R.string.empty_show);
    }

}
