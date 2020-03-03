package tw.com.masterhand.gmorscrm.view;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.enums.ProjectStatus;
import tw.com.masterhand.gmorscrm.model.Stage;
import tw.com.masterhand.gmorscrm.tools.UnitChanger;

public class ItemCount extends RelativeLayout {
    @BindView(R.id.icon)
    View icon;
    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.tvCount)
    TextView tvCount;

    final int MAX_WIDTH = UnitChanger.dpToPx(320), MIN_WIDTH = UnitChanger.dpToPx(50);

    Context context;
    ProjectStatus status;
    Stage stage;
    int max, progress;
    String name;

    public ItemCount(Context context) {
        super(context);
        init(context);
    }

    public ItemCount(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ItemCount(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context mContext) {
        context = mContext;
        // 連接畫面
        View view = inflate(getContext(), R.layout.item_count, this);
        ButterKnife.bind(this, view);
    }

    public void setProjectStatus(ProjectStatus status) {
        this.status = status;
        setProgressColor(ContextCompat.getColor(context, status.getColor()));
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setName(String name) {
        this.name = name;
        tvName.setText(name);
    }

    public String getName() {
        return name;
    }

    public Stage getStage() {
        return stage;
    }

    public void setProgress(int max, int progress) {
        this.max = max;
        this.progress = progress;
        LayoutParams params = (LayoutParams) tvCount.getLayoutParams();
        params.width = MIN_WIDTH + ((MAX_WIDTH - MIN_WIDTH) * progress / max);
        tvCount.setLayoutParams(params);
        tvCount.setText("" + progress + context.getString(R.string.unit));
    }

    public void setProgressColor(int color) {
        icon.getBackground().setColorFilter(color, PorterDuff.Mode.MULTIPLY);
        tvCount.getBackground().setColorFilter(color, PorterDuff.Mode.MULTIPLY);
    }

    public ProjectStatus getStatus() {
        return status;
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        if (selected) {
            tvName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);
            icon.setVisibility(VISIBLE);
        } else {
            tvName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f);
            icon.setVisibility(GONE);
        }
    }
}
