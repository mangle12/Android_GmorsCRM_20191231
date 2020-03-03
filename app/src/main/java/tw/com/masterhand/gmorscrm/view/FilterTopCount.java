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

public class FilterTopCount extends RelativeLayout {
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.tvSelected)
    TextView tvSelected;

    Context context;
    int count;

    public FilterTopCount(Context context) {
        super(context);
        init(context);
    }

    public FilterTopCount(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FilterTopCount(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context mContext) {
        context = mContext;
        count = 10;
        // 連接畫面
        View view = inflate(getContext(), R.layout.item_statistic_filter, this);
        ButterKnife.bind(this, view);
        if (isInEditMode())
            return;
        tvTitle.setText(R.string.filter_top_count);
        tvSelected.setText(String.valueOf(count));
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getCountList();
            }
        });
    }

    private void getCountList() {
        String[] items = new String[5];
        for (int i = 0; i < items.length; i++) {
            int number = (i + 1) * 10;
            items[i] = String.valueOf(number);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.filter_top_count);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int number = (which + 1) * 10;
                setSelected(number);
            }
        });
        builder.create().show();
    }

    public void setSelected(int count) {
        if (count > 0) {
            this.count = count;
            tvSelected.setText(String.valueOf(count));
        }
    }

    public int getSelected() {
        return count;
    }

}
