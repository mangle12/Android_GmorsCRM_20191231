package tw.com.masterhand.gmorscrm.view;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.commons.lang3.time.DateUtils;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.R;
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
import tw.com.masterhand.gmorscrm.room.record.Trip;
import tw.com.masterhand.gmorscrm.tools.TimeFormater;

public class ItemMainSmall extends LinearLayout {
    @BindView(R.id.view_color)
    View vColor;
    @BindView(R.id.textView_title)
    TextView tvTitle;
    @BindView(R.id.textView_time)
    TextView tvTime;
    @BindView(R.id.rootView)
    LinearLayout rootView;

    Date date;
    Trip trip;
    Context context;

    public ItemMainSmall(Context context) {
        super(context);
        init(context);
    }

    public ItemMainSmall(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ItemMainSmall(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context mContext) {
        context = mContext;
        date = new Date();
        // 連接畫面
        View view = inflate(getContext(), R.layout.item_main_small, this);
        ButterKnife.bind(this, view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            rootView.setClipToOutline(true);
        }
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setTrip(Trip result) {
        trip = result;
        tvTitle.setText(trip.getName());
        Date start = trip.getFrom_date();
        Date end = trip.getTo_date();
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
        vColor.setBackgroundColor(ContextCompat.getColor(context, trip.getType().getColor()));
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
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
                    intent.putExtra(MyApplication.INTENT_KEY_TRIP, ItemMainSmall.this.trip.getId());
                    context.startActivity(intent);
                }
            }
        });

    }

}
