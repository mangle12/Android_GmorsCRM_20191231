package tw.com.masterhand.gmorscrm.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.tools.Logger;

public class Dropdown extends RelativeLayout {
    public final static int VALUE_EMPTY = -1;

    Context context;
    @BindView(R.id.view_color)
    View vColor;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.tvSelected)
    TextView tvSelected;
    @BindView(R.id.ivIcon)
    ImageView ivIcon;

    TypedArray typedArray;

    OnSelectListener listener;

    String[] items;
    int selected;
    boolean isRequired;
    boolean[] multiSelected;

    public interface OnSelectListener {
        void onSelected(int position);
    }

    public Dropdown(Context context) {
        super(context);
        init(context);
    }

    public Dropdown(Context context, AttributeSet attrs) {
        super(context, attrs);
        typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.Dropdown, 0, 0);
        init(context);
    }

    public Dropdown(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.Dropdown, 0, 0);
        init(context);
    }

    private void init(Context mContext) {
        context = mContext;
        selected = VALUE_EMPTY;
        // 連接畫面
        View view = inflate(getContext(), R.layout.dropdown, this);
        ButterKnife.bind(this, view);
        try {
            tvTitle.setText(typedArray.getString(R.styleable.Dropdown_dropdown_title));
            vColor.setBackgroundColor(typedArray.getColor(R.styleable.Dropdown_dropdown_color, ContextCompat.getColor(context, R.color.gray)));
            isRequired = typedArray.getBoolean(R.styleable.Dropdown_dropdown_required, false);
            int arrayId = typedArray.getResourceId(R.styleable.Dropdown_dropdown_item, 0);

            if (arrayId != 0)
            {
                setItems(getResources().getStringArray(arrayId));
            }

            int iconId = typedArray.getResourceId(R.styleable.Dropdown_dropdown_icon, 0);
            if (iconId != 0)
            {
                ivIcon.setImageResource(iconId);
            }

        } finally {
            typedArray.recycle();
        }
    }

    public void setColor(int color) {
        vColor.setBackgroundColor(color);
    }

    public void setItems(String[] data) {
        items = data;
        multiSelected = new boolean[items.length];
        if (isRequired) {
            tvSelected.setText(R.string.hint_required);
            tvSelected.setTextColor(ContextCompat.getColor(context, R.color.red));
        } else {
            tvSelected.setText(R.string.hint_not_required);
            tvSelected.setTextColor(ContextCompat.getColor(context, R.color.gray));
        }
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isEnabled())
                    return;

                AlertDialog.Builder builder = new AlertDialog.Builder(context, MyApplication.DIALOG_STYLE);
                builder.setTitle(tvTitle.getText().toString());
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selected = which;
                        tvSelected.setText(items[selected]);
                        tvSelected.setTextColor(ContextCompat.getColor(context, R.color.orange));
                        if (listener != null)
                        {
                            listener.onSelected(selected);
                        }
                    }
                });
                builder.create().show();
            }
        });
    }

    public void enableMultiSelect() {
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isEnabled())
                    return;
                AlertDialog.Builder builder = new AlertDialog.Builder(context, MyApplication.DIALOG_STYLE);
                builder.setTitle(tvTitle.getText().toString());
                builder.setMultiChoiceItems(items, multiSelected, new DialogInterface
                        .OnMultiChoiceClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        multiSelected[which] = isChecked;
                        updateMultiSelected();
                    }
                });
                builder.create().show();
            }
        });
    }

    public void disable() {
        setEnabled(false);
        tvSelected.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
    }

    public void select(int position) {
        selected = position;
        if (selected == VALUE_EMPTY) {
            if (isRequired) {
                tvSelected.setText(R.string.hint_required);
                tvSelected.setTextColor(ContextCompat.getColor(context, R.color.red));
            } else {
                tvSelected.setText(R.string.hint_not_required);
                tvSelected.setTextColor(ContextCompat.getColor(context, R.color.gray));
            }
        } else if (items != null) {
            tvSelected.setText(items[selected]);
            tvSelected.setTextColor(ContextCompat.getColor(context, R.color.orange));
        }
    }

    public void multiSelect(boolean[] select) {
        multiSelected = select;
        if (items != null) {
            updateMultiSelected();
        }
    }

    private void updateMultiSelected() {
        tvSelected.setText("");
        for (int i = 0; i < multiSelected.length; i++) {
            if (multiSelected[i]) {
                if (!TextUtils.isEmpty(tvSelected.getText().toString()))
                {
                    tvSelected.append("+");
                }
                tvSelected.append(items[i]);
            }
        }
        if (TextUtils.isEmpty(tvSelected.getText().toString())) {
            if (isRequired) {
                tvSelected.setText(R.string.hint_required);
                tvSelected.setTextColor(ContextCompat.getColor(context, R.color.red));
            } else {
                tvSelected.setText(R.string.hint_not_required);
                tvSelected.setTextColor(ContextCompat.getColor(context, R.color.gray));
            }
        } else {
            tvSelected.setTextColor(ContextCompat.getColor(context, R.color.orange));
        }
    }

    public int getSelected() {
        return selected;
    }

    public void setText(String show) {
        Logger.e(getClass().getSimpleName(), "setText:" + show);
        if (TextUtils.isEmpty(show))
            return;
        selected = 0;
        tvSelected.setText(show);
        tvSelected.setTextColor(ContextCompat.getColor(context, R.color.orange));
    }

    public String getText() {
        return tvSelected.getText().toString();
    }

    public boolean[] getMultiSelected() {
        return multiSelected;
    }


    public int getIndexByName(String name) {
        int index = 0;
        for (String item : items) {
            if (item.equals(name))
                return index;
            index++;
        }
        return index;
    }

    public void setOnSelectListener(OnSelectListener listener) {
        this.listener = listener;
    }


}
