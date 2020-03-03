package tw.com.masterhand.gmorscrm.view;

import android.content.Context;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import tw.com.masterhand.gmorscrm.R;

public class ItemInputEmail extends RelativeLayout {
    @BindView(R.id.container)
    LinearLayout container;

    Context context;

    public ItemInputEmail(Context context) {
        super(context);
        init(context);
    }

    public ItemInputEmail(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ItemInputEmail(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context mContext) {
        context = mContext;
        // 連接畫面
        View view = inflate(getContext(), R.layout.item_input_email, this);
        ButterKnife.bind(this, view);
    }

    public void clearAllEmail() {
        container.removeAllViews();
    }

    public int getContainerChildCount() {
        return container.getChildCount();
    }

    public void addEmail(String mail) {
        final ItemTextInput input = new ItemTextInput(context);
        if (container.getChildCount() > 0) {
            input.setFunctionListener(R.mipmap.common_removeitem, new OnClickListener() {
                @Override
                public void onClick(View v) {
                    container.removeView(input);
                }
            });
        } else {
            input.setFunctionListener(R.mipmap.common_add, new OnClickListener() {
                @Override
                public void onClick(View v) {
                    addEmail("");
                }
            });
        }
        input.setText(mail);
        container.addView(input);
    }

    public ArrayList<String> getEmail() {
        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < container.getChildCount(); i++) {
            ItemTextInput item = (ItemTextInput) container.getChildAt(i);
            if (!TextUtils.isEmpty(item.getText())) {
                result.add(item.getText());
            }
        }
        return result;
    }

    class ItemTextInput extends LinearLayout {
        @BindView(R.id.etText)
        EditText etText;
        @BindView(R.id.btnFunction)
        ImageButton btnFunction;

        public ItemTextInput(Context context) {
            super(context);
            init(context);
        }

        public ItemTextInput(Context context, AttributeSet attrs) {
            super(context, attrs);
            init(context);
        }

        public ItemTextInput(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            init(context);
        }

        private void init(Context mContext) {
            context = mContext;
            // 連接畫面
            View view = inflate(getContext(), R.layout.item_input_text, this);
            ButterKnife.bind(this, view);
            etText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            etText.setHint(context.getString(R.string.hint_email));
        }

        public String getText() {
            return etText.getText().toString();
        }

        public void setText(String email) {
            etText.setText(email);
        }

        public void setFunctionListener(int rid, OnClickListener listener) {
            btnFunction.setImageResource(rid);
            btnFunction.setOnClickListener(listener);
        }
    }
}
