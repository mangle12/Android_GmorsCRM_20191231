package tw.com.masterhand.gmorscrm.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.activity.task.CancelTaskActivity;
import tw.com.masterhand.gmorscrm.activity.task.ContractDetailActivity;
import tw.com.masterhand.gmorscrm.activity.task.DelayTaskActivity;
import tw.com.masterhand.gmorscrm.enums.TripStatus;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.record.Participant;
import tw.com.masterhand.gmorscrm.room.record.Trip;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.tools.TimeFormater;

public class ItemTripStatus extends LinearLayout {
    @BindView(R.id.textView_reason)
    TextView tvReason;
    @BindView(R.id.textView_status)
    TextView tvStatus;
    @BindView(R.id.textView_complete_time)
    TextView tvCompleteTime;
    @BindView(R.id.button_cancel)
    Button btnCancel;
    @BindView(R.id.button_delay)
    Button btnDelay;
    @BindView(R.id.linearLayout_status_function)
    LinearLayout statusFunction;

    Activity context;
    Participant participant;
    Trip trip;

    public ItemTripStatus(Context context) {
        super(context);
        init(context);
    }

    public ItemTripStatus(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ItemTripStatus(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context mContext) {
        // 連接畫面
        View view = inflate(getContext(), R.layout.item_trip_status, this);
        ButterKnife.bind(this, view);
        if (isInEditMode())
            return;
        context = (Activity) mContext;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            String reason;
            switch (requestCode) {
                case MyApplication.REQUEST_CANCEL_TASK:
                    reason = data.getStringExtra(MyApplication.INTENT_KEY_REASON);
                    setWorkStatus(TripStatus.CANCEL, reason);
                    break;
                case MyApplication.REQUEST_DELAY_TASK:
                    reason = data.getStringExtra(MyApplication.INTENT_KEY_REASON);
                    setWorkStatus(TripStatus.DELAY, reason);
                    break;
            }
        }
    }

    public void setTrip(Trip trip, Participant participant) {
        this.trip = trip;
        this.participant = participant;
        setWorkStatus(trip.getStatus(), trip.getReason());
    }

    /**
     * 設定工作狀態
     */
    public void setWorkStatus(TripStatus status, String reason) {
        if (TextUtils.isEmpty(reason))
        {
            reason = context.getString(R.string.reason) + ":" + context.getString(R.string.empty_show);
        }
        else
        {
            reason = context.getString(R.string.reason) + ":" + reason;
        }

        Logger.e(getClass().getSimpleName(), "status:" + status.getValue() + ",reason:" + reason);
        tvReason.setText(reason);

        switch (status) {
            case NORMAL:
                if (participant.getSign_out_at() != null) {
                    // 已完成
                    Logger.e(getClass().getSimpleName(), "已完成");
                    tvCompleteTime.setVisibility(View.VISIBLE);
                    tvCompleteTime.setText(TimeFormater.getInstance().toDateTimeFormat(participant.getSign_out_at()));
                    tvStatus.setText(R.string.completed);
                    tvStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(R.mipmap.common_start, 0, 0, 0);
                } else {
                    // 未完成
                    Logger.e(getClass().getSimpleName(), "未完成");
                    setBackgroundColor(ContextCompat.getColor(context, R.color.gray_light));
                    statusFunction.setVisibility(View.VISIBLE);

                    if (trip.getFrom_date().compareTo(new Date()) > 0) {
                        tvStatus.setText(R.string.uncompleted);
                        tvStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(R.mipmap.common_pending, 0, 0, 0);
                    } else {
                        tvStatus.setText(R.string.not_start);
                        tvStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(R.mipmap.common_start_yet, 0, 0, 0);
                    }

                    tvStatus.setTextColor(ContextCompat.getColor(context, R.color.black));
                    tvReason.setVisibility(View.GONE);
                    btnCancel.setText(context.getString(R.string.cancel_task));
                    btnCancel.setTextColor(ContextCompat.getColor(context, R.color.red));
                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // 取消行程
                            Intent intent = new Intent(context, CancelTaskActivity.class);
                            intent.putExtra(MyApplication.INTENT_KEY_TRIP, trip.getId());
                            context.startActivityForResult(intent, MyApplication.REQUEST_CANCEL_TASK);
                        }
                    });

                    btnDelay.setTextColor(ContextCompat.getColor(context, R.color.orange));
                    btnDelay.setEnabled(true);
                    btnDelay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // 延後行程
                            Intent intent = new Intent(context, DelayTaskActivity.class);
                            intent.putExtra(MyApplication.INTENT_KEY_TRIP, trip.getId());
                            context.startActivityForResult(intent, MyApplication.REQUEST_DELAY_TASK);
                        }
                    });
                }
                break;
            // 取消
            case CANCEL:
                tvStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(R.mipmap.common_late, 0, 0, 0);
                tvStatus.setText(context.getString(R.string.canceled));
                setBackgroundColor(ContextCompat.getColor(context, R.color.red));
                statusFunction.setVisibility(View.VISIBLE);
                tvReason.setVisibility(View.VISIBLE);
                btnCancel.setText(context.getString(R.string.restart_task));
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        trip.setStatus(TripStatus.NORMAL);
                        trip.setReason("");
                        trip.setDeleted_at(null);
                        DatabaseHelper.getInstance(context).saveTrip(trip)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Consumer<String>() {
                            @Override
                            public void accept(String s) throws Exception { setWorkStatus(TripStatus.NORMAL, "");
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                ErrorHandler.getInstance().setException(context,throwable);
                            }
                        });
                    }
                });
                btnDelay.setTextColor(ContextCompat.getColor(context, R.color.gray_light));
                btnDelay.setEnabled(false);
                break;
            // 延後
            case DELAY:
                tvStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(R.mipmap.common_delay, 0, 0, 0);
                tvStatus.setText(context.getString(R.string.delayed));
                setBackgroundColor(ContextCompat.getColor(context, R.color.orange));
                statusFunction.setVisibility(View.VISIBLE);
                tvReason.setVisibility(View.VISIBLE);
                btnCancel.setText(context.getString(R.string.cancel_task));
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // 取消行程
                        Intent intent = new Intent(context, CancelTaskActivity.class);
                        intent.putExtra(MyApplication.INTENT_KEY_TRIP, trip.getId());
                        context.startActivityForResult(intent, MyApplication.REQUEST_CANCEL_TASK);
                    }
                });
                btnDelay.setTextColor(ContextCompat.getColor(context, R.color.gray_light));
                btnDelay.setEnabled(false);
                break;
        }
    }
}
