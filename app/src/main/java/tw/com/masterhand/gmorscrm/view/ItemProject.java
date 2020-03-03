package tw.com.masterhand.gmorscrm.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.TokenManager;
import tw.com.masterhand.gmorscrm.activity.project.ProjectActivity;
import tw.com.masterhand.gmorscrm.activity.project.ProjectHistoryActivity;
import tw.com.masterhand.gmorscrm.model.ProjectWithConfig;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.setting.DepartmentSalesOpportunity;
import tw.com.masterhand.gmorscrm.room.setting.User;
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.tools.NumberFormater;
import tw.com.masterhand.gmorscrm.tools.TimeFormater;

public class ItemProject extends RelativeLayout {
    @BindView(R.id.btnHistory)
    RelativeLayout btnHistory;
    @BindView(R.id.tvHistory)
    TextView tvHistory;
    @BindView(R.id.btnOpportunity)
    RelativeLayout btnOpportunity;
    @BindView(R.id.textView_time)
    TextView tvTime;
    @BindView(R.id.textView_index)
    TextView tvIndex;
    @BindView(R.id.tvProjectName)
    TextView tvTitle;
    @BindView(R.id.textView_amount)
    TextView tvAmount;
    @BindView(R.id.tvOpportunity)
    TextView tvOpportunity;
    @BindView(R.id.tvOpportunityTitle)
    TextView tvOpportunityTitle;
    @BindView(R.id.tvOpportunityPercent)
    TextView tvOpportunityPercent;
    @BindView(R.id.linearLayout_progress)
    LinearLayout containerProgress;
    @BindView(R.id.progress_gray)
    View progressBackground;
    @BindView(R.id.progress_black)
    View progressForground;

    Activity context;
    ProjectWithConfig project;
    String tripId;
    boolean isProgressShow = false;
    boolean isHistoryEnable = true;
    boolean isSalesEnable = true;
    int progress = 0;

    OnSelectListener listener;

    public interface OnSelectListener {
        void onHistorySelected();

        void onSaleSelected();
    }

    public ItemProject(Context context) {
        super(context);
        init(context);
    }

    public ItemProject(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ItemProject(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context mContext) {
        // 連接畫面
        View view = inflate(getContext(), R.layout.item_project, this);
        ButterKnife.bind(this, view);
        if (isInEditMode())
            return;
        context = (Activity) mContext;
    }

    public void setTripId(String id) {
        tripId = id;
    }

    public void setProject(final ProjectWithConfig project, OnSelectListener mListener, boolean isHistory) {
        this.listener = mListener;
        this.project = project;
        updateProject();
        if (isHistory)
            onHistorySelected();
        else
            onSaleSelected();
        btnHistory.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isHistoryEnable)
                    return;
                onHistorySelected();
                if (listener != null) {
                    listener.onHistorySelected();
                }
            }
        });
        btnOpportunity.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isSalesEnable)
                    return;
                onSaleSelected();
                if (listener != null) {
                    listener.onSaleSelected();
                }
            }
        });
    }

    void onHistorySelected() {
        tvOpportunity.setTextColor(ContextCompat.getColor(context, R.color.black));
        tvOpportunityTitle.setTextColor(ContextCompat.getColor(context, R.color.black));
        tvOpportunityPercent.setTextColor(ContextCompat.getColor(context, R.color.black));
        tvHistory.setTextColor(ContextCompat.getColor(context, R.color.orange));
        tvHistory.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, R.mipmap.common_calendar_tab);
    }

    void onSaleSelected() {
        tvOpportunity.setTextColor(ContextCompat.getColor(context, R.color.orange));
        tvOpportunityTitle.setTextColor(ContextCompat.getColor(context, R.color.orange));
        tvOpportunityPercent.setTextColor(ContextCompat.getColor(context, R.color.orange));
        tvHistory.setTextColor(ContextCompat.getColor(context, R.color.black));
        tvHistory.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, R.mipmap.ommon_calendar);
    }

    public void setProject(final ProjectWithConfig project) {
        this.project = project;
        updateProject();
        User user = TokenManager.getInstance().getUser();
        if (user.id.equals(project.getProject().getUser_id()))
            setupListener(project);
        else
            DatabaseHelper.getInstance(context).getAuthorityProjectById(user.getId(), project
                    .getProject().getId(), user.getDepartment_id(), user.getCompany_id())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<ProjectWithConfig>() {

                @Override
                public void accept(ProjectWithConfig projectWithConfig) throws Exception {
                    setupListener(projectWithConfig);
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    Logger.e(getClass().getSimpleName(), "setProject error:" + throwable.getMessage());
                    ItemProject.this.setAlpha(0.5F);
                }
            });
    }

    void setupListener(ProjectWithConfig projectWithConfig) {
        ItemProject.this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ProjectActivity.class);
                intent.putExtra(MyApplication.INTENT_KEY_PROJECT, project.getProject().getId());
                context.startActivity(intent);
            }
        });
        btnHistory.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isHistoryEnable)
                    return;
                Intent intent = new Intent(context, ProjectHistoryActivity.class);
                intent.putExtra(MyApplication.INTENT_KEY_PROJECT, project.getProject().getId());
                intent.putExtra(MyApplication.INTENT_KEY_TYPE, true);
                context.startActivity(intent);
            }
        });
        btnOpportunity.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isSalesEnable)
                    return;
                Intent intent = new Intent(context, ProjectHistoryActivity.class);
                intent.putExtra(MyApplication.INTENT_KEY_PROJECT, project.getProject().getId());
                intent.putExtra(MyApplication.INTENT_KEY_TYPE, false);
                context.startActivityForResult(intent, MyApplication.REQUEST_ADD_OPPORTUNITY);
            }
        });
    }

    void updateProject() {
        tvTime.setText(TimeFormater.getInstance().toDateFormat(project.getProject().getFrom_date()));
        tvTitle.setText(project.getProject().getName());
        StringBuilder builder = new StringBuilder(context.getString(R.string.estimate_amount));
        builder.append(":");
        if (project.getConfigCurrency() != null) {
            builder.append(project.getConfigCurrency().getName());
        }
        builder.append(" ").append(NumberFormater.getMoneyFormat(project.getProject().getExpect_amount())).append(context
                .getString(R
                        .string.money_unit));
        tvAmount.setText(builder.toString());
        if (project.getSalesOpportunity().getPercentage() < 0) {
            tvOpportunity.setText("0");
        } else {
            tvOpportunity.setText(String.valueOf(project.getSalesOpportunity().getPercentage()));
            setProgress(project.getSalesOpportunity().getStage().getValue());
        }
        DepartmentSalesOpportunity config = project.getDepartmentSalesOpportunity();
        if (config != null)
            for (int i = 0; i < 4; i++) {
                TextView tvTab = (TextView) containerProgress.getChildAt(i);
                String name = "";
                switch (i) {
                    case 0:
                        name = config.getStage_1_name();
                        break;
                    case 1:
                        name = config.getStage_2_name();
                        break;
                    case 2:
                        name = config.getStage_3_name();
                        break;
                    case 3:
                        name = config.getStage_4_name();
                        break;
                }
                tvTab.setText(name);
            }
    }

    public void setIndex(String index) {
        if (TextUtils.isEmpty(index)) {
            tvIndex.setVisibility(GONE);
        } else {
            tvIndex.setText(index);
        }
    }

    public void setProgress(int progress) {
        if (progress > 5 || progress < 1)
            return;
        this.progress = progress;
        isProgressShow = false;
        invalidate();
    }

    public void disableOpportunity() {
        isSalesEnable = false;
    }

    public void disableHistory() {
        isHistoryEnable = false;
    }

    public void hideIndex() {
        tvIndex.setVisibility(INVISIBLE);
    }

    public void hideHistory() {
        btnHistory.setVisibility(GONE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (!isProgressShow) {
            isProgressShow = true;
            Logger.e(getClass().getSimpleName(), "width:" + getMeasuredWidth());
            int progressWidthUnit = containerProgress.getMeasuredWidth() / 5 / 2;
            RelativeLayout.LayoutParams backgroundParams = (RelativeLayout.LayoutParams) progressBackground.getLayoutParams();
            backgroundParams.width = containerProgress.getMeasuredWidth() - progressWidthUnit;
            progressBackground.setLayoutParams(backgroundParams);
            RelativeLayout.LayoutParams foregroundParams = (RelativeLayout.LayoutParams) progressForground.getLayoutParams();
            foregroundParams.width = progressWidthUnit * (1 + (progress - 1) * 2);
            progressForground.setLayoutParams(foregroundParams);
        }
    }
}
