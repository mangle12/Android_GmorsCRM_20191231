package tw.com.masterhand.gmorscrm.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import tw.com.masterhand.gmorscrm.R;

public class EditTextWithTitle extends RelativeLayout {
    Context context;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.etInput)
    EditText etInput;

    TypedArray typedArray;

    public EditTextWithTitle(Context context) {
        super(context);
        init(context);
    }

    public EditTextWithTitle(Context context, AttributeSet attrs) {
        super(context, attrs);
        typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.EditTextWithTitle, 0, 0);
        init(context);
    }

    public EditTextWithTitle(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.EditTextWithTitle, 0, 0);
        init(context);
    }

    private void init(Context mContext) {
        context = mContext;

        // 連接畫面
        View view = inflate(getContext(), R.layout.edittext_with_title, this);
        ButterKnife.bind(this, view);
        try {
            tvTitle.setText(typedArray.getString(R.styleable.EditTextWithTitle_title));
            etInput.setInputType(typedArray.getInteger(R.styleable.EditTextWithTitle_android_inputType, EditorInfo.TYPE_NULL));
            etInput.setMaxLines(typedArray.getInteger(R.styleable.EditTextWithTitle_android_maxLines, Integer.MAX_VALUE));
            etInput.setHint(typedArray.getString(R.styleable.EditTextWithTitle_android_hint));
            String digits = typedArray.getString(R.styleable.EditTextWithTitle_android_digits);

            if (!TextUtils.isEmpty(digits))
                etInput.setKeyListener(DigitsKeyListener.getInstance(digits));

            etInput.setHintTextColor(typedArray.getColor(R.styleable.EditTextWithTitle_android_textColorHint, ContextCompat.getColor(context, R.color.gray)));
        } finally {
            typedArray.recycle();
        }
    }

    public void setTitle(String title) {
        tvTitle.setText(title);
    }

    public void setText(String text) {
        etInput.setText(text);
    }

    public Editable getText() {
        return etInput.getText();
    }

    public void disable() {
        etInput.setEnabled(false);
    }

    public void addTextChangedListener(TextWatcher watcher) {
        etInput.addTextChangedListener(watcher);
    }
}
