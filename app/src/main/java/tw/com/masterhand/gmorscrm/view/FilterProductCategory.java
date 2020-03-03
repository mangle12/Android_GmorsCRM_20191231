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
import tw.com.masterhand.gmorscrm.room.setting.ConfigQuotationProductCategory;

public class FilterProductCategory extends RelativeLayout {
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.tvSelected)
    TextView tvSelected;

    Context context;
    ConfigQuotationProductCategory category;
    OnSelectedListener listener;

    public interface OnSelectedListener {
        void onSelected(ConfigQuotationProductCategory category);
    }

    public void setListener(OnSelectedListener listener) {
        this.listener = listener;
    }

    public FilterProductCategory(Context context) {
        super(context);
        init(context);
    }

    public FilterProductCategory(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FilterProductCategory(Context context, AttributeSet attrs, int defStyle) {
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
        tvTitle.setText(R.string.filter_product);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getConfigQuotationProductCategory();
            }
        });
        clear();
    }

    private void getConfigQuotationProductCategory() {
        DatabaseHelper.getInstance(context).getConfigQuotationProductCategoryByCompany().toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<ConfigQuotationProductCategory>>() {

            @Override
            public void accept(final List<ConfigQuotationProductCategory> categories) throws
                    Exception {
                String[] items = new String[categories.size() + 1];
                items[0] = context.getString(R.string.sample_sort_all);
                for (int i = 0; i < categories.size(); i++) {
                    items[i + 1] = categories.get(i).getName();
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(R.string.filter_product);
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0)
                            setSelected(null);
                        else
                            setSelected(categories.get(which - 1));
                    }
                });
                builder.create().show();
            }
        });
    }

    public void setSelected(ConfigQuotationProductCategory product) {
        if (product == null) {
            clear();
        } else {
            this.category = product;
            tvSelected.setText(category.getName());
        }
        if (listener != null)
            listener.onSelected(category);
    }

    public ConfigQuotationProductCategory getSelected() {
        return category;
    }

    public void clear() {
        category = null;
        tvSelected.setText(R.string.sample_sort_all);
    }

}
