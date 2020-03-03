package tw.com.masterhand.gmorscrm.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import tw.com.masterhand.gmorscrm.R;

public class ItemNews extends RelativeLayout {
    Context context;
    @BindView(R.id.textView_count)
    TextView tvCount;
    @BindView(R.id.textView_title)
    TextView tvTitle;
    @BindView(R.id.textView_subtitle)
    TextView tvSubtitle;
    @BindView(R.id.imageView_icon)
    ImageView ivIcon;
    @BindView(R.id.imageView_next)
    ImageView ivNext;

    TypedArray typedArray;

    public ItemNews(Context context) {
        super(context);
        init(context);
    }

    public ItemNews(Context context, AttributeSet attrs) {
        super(context, attrs);
        typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ItemNews, 0, 0);
        init(context);
    }

    public ItemNews(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context mContext) {
        context = mContext;

        // 連接畫面
        View view = inflate(getContext(), R.layout.item_list, this);
        ButterKnife.bind(this, view);
        try {
            tvTitle.setText(typedArray.getString(R.styleable.ItemNews_text));
            ivIcon.setImageDrawable(typedArray.getDrawable(R.styleable.ItemNews_image));
        } finally {
            typedArray.recycle();
        }
    }

    public void setCount(int count) {
        if (count > 0) {
            tvCount.setVisibility(VISIBLE);
            tvCount.setText(String.valueOf(count));
        } else {
            tvCount.setVisibility(GONE);
        }
    }

    public void setSubtitle(String subtitle) {
        tvSubtitle.setText(subtitle);
    }


}
