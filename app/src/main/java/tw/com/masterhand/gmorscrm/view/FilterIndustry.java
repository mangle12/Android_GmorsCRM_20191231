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
import tw.com.masterhand.gmorscrm.room.setting.ConfigIndustry;

public class FilterIndustry extends RelativeLayout {
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.tvSelected)
    TextView tvSelected;

    Context context;
    ConfigIndustry config;
    OnSelectedListener listener;

    public interface OnSelectedListener {
        void onSelected(ConfigIndustry config);
    }

    public void setListener(OnSelectedListener listener) {
        this.listener = listener;
    }

    public FilterIndustry(Context context) {
        super(context);
        init(context);
    }

    public FilterIndustry(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FilterIndustry(Context context, AttributeSet attrs, int defStyle) {
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
        tvTitle.setText(R.string.filter_industry);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getIndustryList();
            }
        });
        clear();
    }

    private void getIndustryList() {
        DatabaseHelper.getInstance(context).getConfigIndustry().toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<ConfigIndustry>>() {
            @Override
            public void accept(final List<ConfigIndustry> configs) throws Exception {
                String[] items = new String[configs.size() + 1];
                items[0] = context.getString(R.string.sample_sort_all);
                for (int i = 0; i < configs.size(); i++) {
                    items[i + 1] = configs.get(i).getName();
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(R.string.filter_department);
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0)
                            setSelected(null);
                        else
                            setSelected(configs.get(which - 1));
                    }
                });
                builder.create().show();
            }
        });
    }

    public void setSelected(ConfigIndustry config) {
        if (config == null) {
            clear();
        } else {
            this.config = config;
            tvSelected.setText(config.getName());
        }
        if (listener != null)
            listener.onSelected(config);
    }

    public ConfigIndustry getSelected() {
        return config;
    }

    public void clear() {
        config = null;
        tvSelected.setText(R.string.sample_sort_all);
    }

}
