package tw.com.masterhand.gmorscrm.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import tw.com.masterhand.gmorscrm.R;

public class ItemSelectDiscount extends RelativeLayout {
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.tvDiscount)
    TextView tvDiscount;

    Context context;

    float discount = 1.00F;
    int max = 50;
    DecimalFormat formater = new DecimalFormat(
            "0.00");

    public ItemSelectDiscount(Context context) {
        super(context);
        init(context);
    }

    public ItemSelectDiscount(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ItemSelectDiscount(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context mContext) {
        context = mContext;
        // 連接畫面
        View view = inflate(getContext(), R.layout.item_select_discount, this);
        ButterKnife.bind(this, view);
        if (isInEditMode())
            return;
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
    }

    private void showDialog() {
        final NumberPicker picker = new NumberPicker(context);
        String[] display = new String[max * 100];
        for (int i = 0; i < display.length; i++) {
            float value = (i + 1) / 100F;
            display[i] = formater.format(value);
        }
        picker.setDisplayedValues(display);
        picker.setMinValue(1);
        picker.setMaxValue(max * 100);
        picker.setValue(Math.round(discount * 100));

        final FrameLayout layout = new FrameLayout(context);
        layout.addView(picker, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER));

        new AlertDialog.Builder(context)
                .setView(layout)
                .setTitle(tvTitle.getText().toString())
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        discount = picker.getValue() / 100F;
                        tvDiscount.setText(formater.format(discount));
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    public void setDiscount(float discount) {
        this.discount = discount;
        tvDiscount.setText(formater.format(discount));
    }

    public float getDiscount() {
        return discount;
    }

    public void disable() {
        setOnClickListener(null);
    }

    public void setTitle(String title) {
        tvTitle.setText(title);
    }
}
