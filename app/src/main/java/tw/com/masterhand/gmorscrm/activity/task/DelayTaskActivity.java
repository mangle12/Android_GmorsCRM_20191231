package tw.com.masterhand.gmorscrm.activity.task;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.apache.commons.lang3.time.DateUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import tw.com.masterhand.gmorscrm.BaseUserCheckActivity;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.record.Trip;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.tools.TimeFormater;
import tw.com.masterhand.gmorscrm.view.AppbarEdit;

import static tw.com.masterhand.gmorscrm.MyApplication.INTENT_KEY_REASON;

public class DelayTaskActivity extends BaseUserCheckActivity {
    @BindView(R.id.appbar)
    AppbarEdit appbar;
    @BindView(R.id.container)
    LinearLayout container;
    @BindView(R.id.container_reason)
    LinearLayout containerReason;
    @BindView(R.id.editText_reason)
    EditText etReason;
    @BindView(R.id.linearLayout_end_time)
    LinearLayout btnEndTime;
    @BindView(R.id.linearLayout_start_time)
    LinearLayout btnStartTime;
    @BindView(R.id.textView_start_time)
    TextView tvStartTime;
    @BindView(R.id.textView_end_time)
    TextView tvEndTime;

    Date startTime, endTime;
    String[] reasons;
    String selected = "";// 選擇原因

    Trip trip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delay_task);
        init();
    }

    @Override
    protected void onUserChecked() {
        appbar.invalidate();
    }

    private void init() {
        appbar.setBackground(ContextCompat.getColor(this, R.color.orange));
        appbar.setTextColor(ContextCompat.getColor(this, R.color.white));
        appbar.setTitle(getString(R.string.title_activity_delay_task));
        appbar.setCompleteListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 點擊完成
                if (containerReason.getVisibility() == View.VISIBLE) {
                    selected = etReason.getText().toString();
                }
                if (TextUtils.isEmpty(selected)) {
                    Toast.makeText(DelayTaskActivity.this, getString(R.string.error_msg_empty),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                trip.setFrom_date(startTime);
                trip.setTo_date(endTime);
                DatabaseHelper.getInstance(DelayTaskActivity.this).delayTrip(trip, selected)
                        .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>
                        () {

                    @Override
                    public void accept(String reason) throws Exception {
                        Intent intent = new Intent();
                        intent.putExtra(INTENT_KEY_REASON, reason);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ErrorHandler.getInstance().setException(DelayTaskActivity.this, throwable);
                    }
                });
            }
        });

        // 延後理由
        reasons = getResources().getStringArray(R.array.cancel_reason);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout
                .LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen
                .list_size));
        for (String reason : reasons) {
            container.addView(generateItem(reason, params));
        }
        final String tripId = getIntent().getStringExtra(MyApplication.INTENT_KEY_TRIP);
        if (TextUtils.isEmpty(tripId)) {
            finish();
            return;
        }
        mDisposable.add(DatabaseHelper.getInstance(this).getTripById(tripId).observeOn
                (AndroidSchedulers
                .mainThread()).subscribe(new Consumer<Trip>() {

            @Override
            public void accept(Trip result) throws Exception {
                trip = result;
                // 任務時間
                startTime = trip.getFrom_date();
                endTime = trip.getTo_date();
                showTime(tvStartTime, startTime);
                showTime(tvEndTime, endTime);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                ErrorHandler.getInstance().setException(DelayTaskActivity.this, throwable);
            }
        }));
    }

    /**
     * 產生延後原因選項
     */
    private View generateItem(String reason, LinearLayout.LayoutParams params) {
        TextView tvItem = new TextView(this);
        tvItem.setText(reason);
        tvItem.setGravity(Gravity.CENTER);
        tvItem.setBackgroundResource(R.drawable.bg_delay_reason_selector);
        tvItem.setTextColor(ContextCompat.getColorStateList(this, R.color.text_cancel_reason));
        tvItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView tvItem = (TextView) view;
                for (int i = 0; i < container.getChildCount(); i++) {
                    container.getChildAt(i).setSelected(false);
                }
                selected = tvItem.getText().toString();
                tvItem.setSelected(true);
                if (tvItem.getText().toString().equals(reasons[reasons.length - 1])) {
                    containerReason.setVisibility(View.VISIBLE);
                } else {
                    containerReason.setVisibility(View.GONE);
                }
            }
        });
        tvItem.setLayoutParams(params);
        return tvItem;
    }


    @OnClick(R.id.linearLayout_start_time)
    void changeStartTime() {
        showDatePicker(startTime, true);
    }

    @OnClick(R.id.linearLayout_end_time)
    void changeEndTime() {
        showDatePicker(endTime, false);
    }

    /**
     * 顯示日期選擇
     */
    private void showDatePicker(Date date, final boolean isStart) {
        final Calendar toBeChanged = Calendar.getInstance(Locale.getDefault());
        toBeChanged.setTime(date);
        DatePickerDialog datePicker = new DatePickerDialog(this, new DatePickerDialog
                .OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                toBeChanged.set(Calendar.YEAR, year);
                toBeChanged.set(Calendar.MONTH, month);
                toBeChanged.set(Calendar.DAY_OF_MONTH, day);
                if (isStart) {
                    startTime = toBeChanged.getTime();
                    showTime(tvStartTime, startTime);
                    if (endTime.getTime() < startTime.getTime()) {
                        // 結束時間比開始時間早，調整結束時間
                        endTime.setTime(startTime.getTime());
                        endTime.setTime(endTime.getTime() + (60 * 60 * 1000));
                        showTime(tvEndTime, endTime);
                    }
                } else {
                    endTime = toBeChanged.getTime();
                    showTime(tvEndTime, endTime);
                }
                showTimePicker(toBeChanged.getTime(), isStart);
            }
        }, toBeChanged.get(Calendar.YEAR), toBeChanged.get(Calendar.MONTH), toBeChanged.get
                (Calendar.DAY_OF_MONTH));
        if (!isStart) {
            datePicker.getDatePicker().setMinDate(startTime.getTime());
        }
        datePicker.show();
    }

    /**
     * 顯示時間選擇
     */
    private void showTimePicker(Date date, final boolean isStart) {
        final Calendar toBeChanged = Calendar.getInstance(Locale.getDefault());
        toBeChanged.setTime(date);
        TimePickerDialog timePicker = new TimePickerDialog(this, new TimePickerDialog
                .OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                toBeChanged.set(Calendar.MINUTE, minute);
                toBeChanged.set(Calendar.HOUR_OF_DAY, hour);
                if (isStart) {
                    startTime = toBeChanged.getTime();
                    showTime(tvStartTime, startTime);
                    if (endTime.getTime() < startTime.getTime()) {
                        // 結束時間比開始時間早，調整結束時間
                        endTime.setTime(startTime.getTime());
                        endTime.setTime(endTime.getTime() + (60 * 60 * 1000));
                        showTime(tvEndTime, endTime);
                    }
                } else {
                    endTime = toBeChanged.getTime();
                    if (DateUtils.isSameDay(startTime, endTime)) {
                        if (endTime.getTime() < startTime.getTime()) {
                            // 結束時間比開始時間早，調整結束時間
                            endTime.setTime(startTime.getTime());
                            endTime.setTime(endTime.getTime() + (60 * 60 * 1000));
                            Toast.makeText(DelayTaskActivity.this, getString(R.string
                                    .error_msg_end_time_before_start_time), Toast.LENGTH_LONG)
                                    .show();
                        }
                    }
                    showTime(tvEndTime, endTime);
                }
            }
        }, toBeChanged.get(Calendar.HOUR_OF_DAY), toBeChanged.get
                (Calendar.MINUTE), true);
        timePicker.show();
    }

    /**
     * 顯示時間
     */
    private void showTime(TextView tvTime, Date calendar) {
        String show = TimeFormater.getInstance().toDateTimeFormat(calendar);
        tvTime.setText(show);
    }
}
