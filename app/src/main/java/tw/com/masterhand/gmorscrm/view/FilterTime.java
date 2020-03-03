package tw.com.masterhand.gmorscrm.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import tw.com.masterhand.gmorscrm.R;

public class FilterTime extends RelativeLayout {
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.tvSelected)
    TextView tvSelected;

    Context context;
    String[] items;
    int selected;

    public FilterTime(Context context) {
        super(context);
        init(context);
    }

    public FilterTime(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FilterTime(Context context, AttributeSet attrs, int defStyle) {
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
        tvTitle.setText(R.string.filter_time);
        items = context.getResources().getStringArray(R.array.filter_time);
        selected = 0;
        tvSelected.setText(items[selected]);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(R.string.filter_time);
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selected = which;
                        tvSelected.setText(items[selected]);
                    }
                });
                builder.create().show();
            }
        });
    }

    /**
         * 移除前年選項(最後一項)
         */
    public void disableYearBeforeLast() {
        String[] newItems = new String[items.length - 1];
        System.arraycopy(items, 0, newItems, 0, newItems.length);
        items = newItems;
        selected = 0;
        tvSelected.setText(items[selected]);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(R.string.filter_time);
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selected = which;
                        tvSelected.setText(items[selected]);
                    }
                });
                builder.create().show();
            }
        });
    }

    public void setSelected(int index) {
        selected = index;
        tvSelected.setText(items[selected]);
    }

    public int getSelected() {
        return selected;
    }

    public String getSelectedText() {
        return items[selected];
    }
}
