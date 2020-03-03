package tw.com.masterhand.gmorscrm.view;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.apache.commons.lang3.time.DateUtils;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.TokenManager;
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
import tw.com.masterhand.gmorscrm.enums.ApprovalRequire;
import tw.com.masterhand.gmorscrm.enums.SubmitStatus;
import tw.com.masterhand.gmorscrm.enums.TripStatus;
import tw.com.masterhand.gmorscrm.enums.TripType;
import tw.com.masterhand.gmorscrm.model.MainTrip;
import tw.com.masterhand.gmorscrm.model.VisitWithConfig;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.record.Report;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.tools.TimeFormater;

public class ItemMain extends RelativeLayout {
    @BindView(R.id.ivIcon)
    ImageView ivIcon;
    @BindView(R.id.leftBar)
    TextView leftBar;
    @BindView(R.id.textView_title)
    TextView tvTitle;
    @BindView(R.id.textView_subtitle)
    TextView tvSubtitle;
    @BindView(R.id.textView_time)
    TextView tvTime;
    @BindView(R.id.textView_name)
    TextView tvName;
    @BindView(R.id.tvSubmit)
    TextView tvSubmit;
    @BindView(R.id.tvReportNotComplete)
    TextView tvReportNotComplete;
    @BindView(R.id.ivSync)
    ImageView ivSync;

    Date date;
    MainTrip trip;
    Context context;

    public ItemMain(Context context) {
        super(context);
        init(context);
    }

    public ItemMain(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ItemMain(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context mContext) {
        context = mContext;
        // 連接畫面
        View view = inflate(getContext(), R.layout.item_main, this);
        ButterKnife.bind(this, view);

    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void showDate(Date date) {
        if (DateUtils.isSameDay(new Date(), date)) {
            if (trip != null && trip.getTrip().getDate_type())
                tvTime.setText(context.getString(R.string.today) + " " + context.getString(R
                        .string.all_day));
            else
                tvTime.setText(context.getString(R.string.today) + " " + TimeFormater.getInstance().toTimeFormat(date));
        } else {
            if (trip != null && trip.getTrip().getDate_type())
                tvTime.setText(TimeFormater.getInstance().toInviteTimeFormat(date) + " " + context.getString(R.string.all_day));
            else
                tvTime.setText(TimeFormater.getInstance().toInviteTimeFormat(date) + " " + TimeFormater.getInstance().toTimeFormat(date));
        }
    }

    public void setTrip(final MainTrip mainTrip) {
        this.trip = mainTrip;
        tvTitle.setText(trip.getTrip().getName());
        if (mainTrip.getTrip().getApprovalRequired() != ApprovalRequire.UNKNOWN) {
            ivSync.setBackgroundResource(R.drawable.oval_green);
        }
        if (mainTrip.getCustomerName() != null) {
            tvName.setText(mainTrip.getCustomerName());
        } else {
            if (mainTrip.getUserName() != null)
            {
                tvName.setText(mainTrip.getUserName());
            }
        }
        if (mainTrip.getTrip().getType() == TripType.VISIT) {
            // 針對活動類進行處理
            DatabaseHelper.getInstance(context).getVisitByTrip(mainTrip.getTrip().getId())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<VisitWithConfig>() {

                @Override
                public void accept(VisitWithConfig visitWithConfig) throws Exception {
                    tvSubtitle.setText(visitWithConfig.getVisit().getType().getTitle());
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    ErrorHandler.getInstance().setException(context, throwable);
                }
            });

            if (mainTrip.getTrip().getStatus() != TripStatus.CANCEL) {
                DatabaseHelper.getInstance(context).getReport(mainTrip.getTrip().getId(), mainTrip.getTrip().getUser_id())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Report>() {


                    @Override
                    public void accept(Report report) throws Exception {
                        if (TextUtils.isEmpty(report.content)) {
                            tvReportNotComplete.setVisibility(VISIBLE);
                        } else {
                            tvReportNotComplete.setVisibility(INVISIBLE);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        tvReportNotComplete.setVisibility(VISIBLE);
                    }
                });
            }
        }
        if (mainTrip.getTrip().getStatus() == TripStatus.CANCEL) {
            tvReportNotComplete.setVisibility(VISIBLE);
            tvReportNotComplete.setText(R.string.canceled);
        }
        if (mainTrip.getTrip().getDate_type()) {
            tvTime.setText(context.getString(R.string.all_day));
        } else {
            try {
                Date start = trip.getTrip().getFrom_date();
                Date end = trip.getTrip().getTo_date();
                if (DateUtils.isSameDay(start, date)) {
                    if (DateUtils.isSameDay(end, date)) {
                        tvTime.setText(TimeFormater.getInstance().toPeriodTimeFormat(start, end));
                    } else {
                        String show = TimeFormater.getInstance().toTimeFormat(start);
                        show += "-24:00";
                        tvTime.setText(show);
                    }
                } else if (DateUtils.isSameDay(end, date)) {
                    String show = "00:00-";
                    show += TimeFormater.getInstance().toTimeFormat(end);
                    tvTime.setText(show);
                } else {
                    tvTime.setText(context.getString(R.string.all_day));
                }
            } catch (Exception e) {
                ErrorHandler.getInstance().setException(context, e);
                tvTime.setText(context.getString(R.string.empty_show));
            }
        }
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = null;
                switch (trip.getTrip().getType()) {
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
                    case SAMPLE:
                        intent = new Intent(context, SampleDetailActivity.class);
                        break;
                    case REIMBURSEMENT:
                        intent = new Intent(context, ReimbursementDetailActivity.class);
                        break;
                    case CONTRACT:
                        intent = new Intent(context, ContractDetailActivity.class);
                        break;
                    case QUOTATION:
                        intent = new Intent(context, QuotationDetailActivity.class);
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
                    default:
                }
                if (intent != null) {
                    intent.putExtra(MyApplication.INTENT_KEY_TRIP, trip.getTrip().getId());
                    context.startActivity(intent);
                }
            }
        });
        ivIcon.setImageResource(trip.getTrip().getType().getIcon());
        ivIcon.setBackgroundColor(ContextCompat.getColor(context, trip.getTrip().getType().getColor()));
        leftBar.setText(trip.getTrip().getType().getTitle());

        // 申請類顯示提交狀態
        switch (trip.getTrip().getType()) {
            case ABSENT:
            case SAMPLE:
            case REIMBURSEMENT:
            case CONTRACT:
            case QUOTATION:
            case SPECIAL_PRICE:
            case PRODUCTION:
            case NON_STANDARD_INQUIRY:
            case SPRING_RING_INQUIRY:
            case SPECIAL_SHIP:
            case EXPRESS:
            case TRAVEL:
                tvSubmit.setVisibility(VISIBLE);
                tvSubmit.setText(trip.getTrip().getSubmit().getTitle());
                tvSubmit.setCompoundDrawablesRelativeWithIntrinsicBounds(trip.getTrip().getSubmit().getIcon(), 0, 0, 0);
                break;
        }
    }

    public void setSelectMode(OnClickListener listener) {
        tvSubmit.setVisibility(VISIBLE);
        Date date = trip.getTrip().getFrom_date();
        if (DateUtils.isSameDay(new Date(), date)) {
            if (trip.getTrip() != null && trip.getTrip().getDate_type())
                tvTime.setText(context.getString(R.string.today) + " " + context.getString(R
                        .string.all_day));
            else
                tvTime.setText(context.getString(R.string.today) + " " + TimeFormater.getInstance().toTimeFormat(date));
        } else {
            if (trip.getTrip() != null && trip.getTrip().getDate_type())
                tvTime.setText(TimeFormater.getInstance().toInviteTimeFormat(date) + " " + context.getString(R.string.all_day));
            else
                tvTime.setText(TimeFormater.getInstance().toInviteTimeFormat(date) + " " + TimeFormater.getInstance().toTimeFormat(date));
        }
        if (trip.getTrip().getSubmit() == SubmitStatus.NONE) {
            unselected();
        } else {
            selected();
        }
        this.setOnClickListener(listener);
    }

    public void selected() {
        trip.getTrip().setSubmit(SubmitStatus.SUBMITTED);
        tvSubmit.setText(R.string.selected);
        tvSubmit.setCompoundDrawablesRelativeWithIntrinsicBounds(R.mipmap.common_small_vpicked, 0, 0, 0);
    }

    public void unselected() {
        trip.getTrip().setSubmit(SubmitStatus.NONE);
        tvSubmit.setText(R.string.not_selected);
        tvSubmit.setCompoundDrawablesRelativeWithIntrinsicBounds(R.mipmap.common_small_pick, 0, 0, 0);
    }
}
