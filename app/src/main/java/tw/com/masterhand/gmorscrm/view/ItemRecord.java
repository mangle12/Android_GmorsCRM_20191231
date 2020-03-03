package tw.com.masterhand.gmorscrm.view;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import tw.com.masterhand.gmorscrm.activity.task.AbsentDetailActivity;
import tw.com.masterhand.gmorscrm.activity.task.ContractDetailActivity;
import tw.com.masterhand.gmorscrm.activity.task.ExpressDetailActivity;
import tw.com.masterhand.gmorscrm.activity.task.NonStandardInquiryDetailActivity;
import tw.com.masterhand.gmorscrm.activity.task.OfficeDetailActivity;
import tw.com.masterhand.gmorscrm.activity.task.ProductionDetailActivity;
import tw.com.masterhand.gmorscrm.activity.task.QuotationDetailActivity;
import tw.com.masterhand.gmorscrm.activity.task.ReimbursementDetailActivity;
import tw.com.masterhand.gmorscrm.activity.task.RepaymentDetailActivity;
import tw.com.masterhand.gmorscrm.activity.task.SampleDetailActivity;
import tw.com.masterhand.gmorscrm.activity.task.SpecialPriceDetailActivity;
import tw.com.masterhand.gmorscrm.activity.task.SpecialShipDetailActivity;
import tw.com.masterhand.gmorscrm.activity.task.SpringRingInquiryDetailActivity;
import tw.com.masterhand.gmorscrm.activity.task.TaskDetailActivity;
import tw.com.masterhand.gmorscrm.activity.task.TravelDetailActivity;
import tw.com.masterhand.gmorscrm.activity.task.VisitDetailActivity;
import tw.com.masterhand.gmorscrm.model.ProjectWithConfig;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.record.Trip;
import tw.com.masterhand.gmorscrm.room.setting.User;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.tools.TimeFormater;

public class ItemRecord extends RelativeLayout {
    @BindView(R.id.button_next)
    Button btnNext;
    @BindView(R.id.ivIcon)
    ImageView ivIcon;
    @BindView(R.id.leftBar)
    TextView leftBar;
    @BindView(R.id.textView_time)
    TextView tvTime;
    @BindView(R.id.textView_name)
    TextView tvName;
    @BindView(R.id.textView_job)
    TextView tvJob;
    @BindView(R.id.tvProject)
    TextView tvProject;
    @BindView(R.id.tvTrip)
    TextView tvTrip;

    Trip trip;
    Context context;

    public ItemRecord(Context context) {
        super(context);
        init(context);
    }

    public ItemRecord(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ItemRecord(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context mContext) {
        context = mContext;
        // 連接畫面
        View view = inflate(getContext(), R.layout.item_record, this);
        ButterKnife.bind(this, view);
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showDetail();
            }
        });
        btnNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showDetail();
            }
        });
        tvProject.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ProjectActivity.class);
                intent.putExtra(MyApplication.INTENT_KEY_PROJECT, trip.getProject_id());
                context.startActivity(intent);
            }
        });
    }

    private void showDetail() {
        if (trip != null) {
            Intent intent = null;
            switch (trip.getType()) {
                case VISIT:
                    intent = new Intent(context, VisitDetailActivity.class);
                    break;
                case OFFICE:
                    intent = new Intent(context, OfficeDetailActivity.class);
                    break;
                case TASK:
                    intent = new Intent(context, TaskDetailActivity.class);
                    break;
                case ABSENT:
                    intent = new Intent(context, AbsentDetailActivity.class);
                    break;
                case QUOTATION:
                    intent = new Intent(context, QuotationDetailActivity.class);
                    break;
                case CONTRACT:
                    intent = new Intent(context, ContractDetailActivity.class);
                    break;
                case REIMBURSEMENT:
                    intent = new Intent(context, ReimbursementDetailActivity.class);
                    break;
                case SAMPLE:
                    intent = new Intent(context, SampleDetailActivity.class);
                    break;
                case SPECIAL_PRICE:
                    intent = new Intent(context, SpecialPriceDetailActivity.class);
                    break;
                case PRODUCTION:
                    intent = new Intent(context, ProductionDetailActivity.class);
                    break;
                case NON_STANDARD_INQUIRY:
                    intent = new Intent(context, NonStandardInquiryDetailActivity.class);
                    break;
                case SPRING_RING_INQUIRY:
                    intent = new Intent(context, SpringRingInquiryDetailActivity.class);
                    break;
                case SPECIAL_SHIP:
                    intent = new Intent(context, SpecialShipDetailActivity.class);
                    break;
                case REPAYMENT:
                    intent = new Intent(context, RepaymentDetailActivity.class);
                    break;
                case EXPRESS:
                    intent = new Intent(context, ExpressDetailActivity.class);
                    break;
                case TRAVEL:
                    intent = new Intent(context, TravelDetailActivity.class);
                    break;
            }
            intent.putExtra(MyApplication.INTENT_KEY_TRIP, trip.getId
                    ());
            context.startActivity(intent);
        }
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
        if (trip.getUpdated_at() != null)
            tvTime.setText(TimeFormater.getInstance().toDateTimeFormat(trip.getUpdated_at()));
        if (!TextUtils.isEmpty(trip.getProject_id())) {
            DatabaseHelper.getInstance(context).getProjectById(trip.getProject_id(), TokenManager.getInstance().getUser().getDepartment_id(), TokenManager.getInstance().getUser().getCompany_id())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<ProjectWithConfig>() {

                @Override
                public void accept(ProjectWithConfig projectWithConfig) throws Exception {
                    tvProject.setText(context.getString(R.string.project) + ":" + projectWithConfig.getProject().getName());
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    tvProject.setVisibility(INVISIBLE);
                    ErrorHandler.getInstance().setException(context, throwable);
                }
            });
        } else {
            tvProject.setVisibility(INVISIBLE);
        }
        ivIcon.setBackgroundColor(ContextCompat.getColor(context, trip.getType().getColor()));
        ivIcon.setImageResource(trip.getType().getIcon());
        leftBar.setText(trip.getType().getTitle());
        tvTrip.setText(trip.getName());
        if (trip.getUser_id() != null)
            DatabaseHelper.getInstance(context).getUserById(trip.getUser_id())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<User>() {

                @Override
                public void accept(User user) throws Exception {
                    tvName.setText(user.getShowName());
                    tvJob.setText(user.getTitle());
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    ErrorHandler.getInstance().setException(context, throwable);
                }
            });

    }
}
