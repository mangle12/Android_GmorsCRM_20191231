package tw.com.masterhand.gmorscrm.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.room.record.Project;

public class ProjectSelectCard extends RelativeLayout {
    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.tvAmount)
    TextView tvAmount;

    Context context;
    Project project;

    public ProjectSelectCard(Context context) {
        super(context);
        init(context);
    }

    public ProjectSelectCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ProjectSelectCard(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context mContext) {
        context = mContext;
        // 連接畫面
        View view = inflate(getContext(), R.layout.card_project_select, this);
        ButterKnife.bind(this, view);
    }

    /**
     * 設定工作項目資料
     */
    public void setProject(Project project) {
        this.project = project;
        if (!TextUtils.isEmpty(project.getName())) {
            tvName.setText(project.getName());
        }
        tvAmount.setText(context.getString(R.string.estimate_amount) + ":" + project.getExpect_amount() + context.getString(R.string.money_unit));
    }

    public Project getProject() {
        return project;
    }
}
