package tw.com.masterhand.gmorscrm.view;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.commons.lang3.time.DateUtils;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.activity.task.OfficeDetailActivity;
import tw.com.masterhand.gmorscrm.activity.task.VisitDetailActivity;
import tw.com.masterhand.gmorscrm.enums.AcceptType;
import tw.com.masterhand.gmorscrm.model.MainTrip;
import tw.com.masterhand.gmorscrm.room.record.Participant;
import tw.com.masterhand.gmorscrm.room.record.Trip;
import tw.com.masterhand.gmorscrm.tools.TimeFormater;

public class ItemMainInvite extends LinearLayout implements ItemTripAccept.OnSelectListener {
    @BindView(R.id.ivIcon)
    ImageView ivIcon;
    @BindView(R.id.leftBar)
    TextView leftBar;
    @BindView(R.id.itemTripAccept)
    ItemTripAccept itemTripAccept;
    @BindView(R.id.textView_title)
    TextView tvTitle;
    @BindView(R.id.textView_subtitle)
    TextView tvSubtitle;
    @BindView(R.id.textView_time)
    TextView tvTime;

    Date date;
    Trip trip;
    AcceptListener acceptListener;
    Participant participant;

    Context context;

    public ItemMainInvite(Context context) {
        super(context);
        init(context);
    }

    public ItemMainInvite(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ItemMainInvite(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context mContext) {
        context = mContext;
        date = new Date();
        // 連接畫面
        View view = inflate(getContext(), R.layout.item_main_invite, this);
        ButterKnife.bind(this, view);
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setTrip(final MainTrip mainTrip, Participant participant) {
        this.trip = mainTrip.getTrip();
        this.participant = participant;
        itemTripAccept.setParticipant(participant);
        itemTripAccept.setOnSelectListener(this);
        tvTitle.setText(trip.getName());
        if (mainTrip.getCustomerName() != null)
            tvSubtitle.setText(context.getString(R.string.target) + ":" + mainTrip.getCustomerName());
        else
            tvSubtitle.setText(context.getString(R.string.target) + ":" + context.getString(R
                    .string.empty_show));
        if (!TextUtils.isEmpty(trip.getDescription()))
            tvSubtitle.setText(trip.getDescription());
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
                }
                if (intent != null) {
                    intent.putExtra(MyApplication.INTENT_KEY_TRIP, ItemMainInvite.this.trip.getId());
                    context.startActivity(intent);
                }
            }
        });

        ivIcon.setBackgroundColor(ContextCompat.getColor(context, trip.getType().getColor()));
        ivIcon.setImageResource(trip.getType().getIcon());
        leftBar.setText(trip.getType().getTitle());
    }

    @Override
    public void onItemSelected(ItemTripAccept item, AcceptType acceptType) {
        if (acceptListener != null)
            acceptListener.onAcceptItemSelected(acceptType);
    }

    public interface AcceptListener {
        void onAcceptItemSelected(AcceptType type);
    }

    public void setAcceptListener(AcceptListener listener) {
        acceptListener = listener;
    }

    public void showDate(Date date) {
        if (DateUtils.isSameDay(new Date(), date)) {
            if (trip != null && trip.getDate_type())
                tvTime.setText(context.getString(R.string.today) + " " + context.getString(R
                        .string.all_day));
            else
                tvTime.setText(context.getString(R.string.today) + " " + TimeFormater.getInstance().toTimeFormat(date));
        } else {
            if (trip != null && trip.getDate_type())
                tvTime.setText(TimeFormater.getInstance().toInviteTimeFormat(date) + " " + context.getString(R.string.all_day));
            else
                tvTime.setText(TimeFormater.getInstance().toInviteTimeFormat(date) + " " + TimeFormater.getInstance().toTimeFormat(date));
        }
    }
}
