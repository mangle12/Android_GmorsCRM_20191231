package tw.com.masterhand.gmorscrm.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.tools.NumberFormater;
import tw.com.masterhand.gmorscrm.tools.UnitChanger;

public class ItemRank extends RelativeLayout {
    @BindView(R.id.textView_index)
    TextView tvIndex;
    @BindView(R.id.textView_name)
    TextView tvName;
    @BindView(R.id.textView_count)
    TextView tvCount;
    @BindView(R.id.imageView_head)
    ImageView ivHead;
    @BindView(R.id.progress)
    View progressBar;

    final int MAX_WIDTH = UnitChanger.dpToPx(320), MIN_WIDTH = UnitChanger.dpToPx(70);

    Context context;
    float max, progress;

    public ItemRank(Context context) {
        super(context);
        init(context);
    }

    public ItemRank(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ItemRank(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context mContext) {
        context = mContext;
        // 連接畫面
        View view = inflate(getContext(), R.layout.item_rank, this);
        ButterKnife.bind(this, view);
    }

    public void setIndex(int index) {
        tvIndex.setText(String.valueOf(index));
    }

    public void setName(String name) {
        tvName.setText(name);
    }

    public void setProgress(float max, float progress, boolean isMoney) {
        this.max = max;
        this.progress = progress;
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) progressBar.getLayoutParams();
        params.width = MIN_WIDTH + Math.round((MAX_WIDTH - MIN_WIDTH) * progress / max);
        progressBar.setLayoutParams(params);
        if (isMoney) {
            tvCount.setText(NumberFormater.getMoneyFormat(progress));
        } else {
            tvCount.setText(NumberFormater.getNumberFormat(progress));
        }
    }

    public ImageView getIvHead() {
        return ivHead;
    }
}
