package tw.com.masterhand.gmorscrm.view;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.apache.commons.lang3.time.DateUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.enums.AlertType;
import tw.com.masterhand.gmorscrm.tools.TimeFormater;

public class ItemSelectTime extends LinearLayout {
    @BindView(R.id.switchAllDay)
    Switch switchAllDay;
    @BindView(R.id.btnStart)
    Button btnStart;
    @BindView(R.id.btnEnd)
    Button btnEnd;
    @BindView(R.id.tvStart)
    TextView tvStart;
    @BindView(R.id.tvEnd)
    TextView tvEnd;
    @BindView(R.id.tvAlert)
    TextView tvAlert;
    @BindView(R.id.containerAlert)
    RelativeLayout containerAlert;

    Context context;
    Calendar start, end;
    AlertType alertType;

    boolean isEditable = true;

    public ItemSelectTime(Context context) {
        super(context);
        init(context);
    }

    public ItemSelectTime(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ItemSelectTime(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context mContext) {
        context = mContext;
        // 連接畫面
        View view = inflate(getContext(), R.layout.item_select_time, this);
        ButterKnife.bind(this, view);

        start = Calendar.getInstance(Locale.getDefault());
        end = Calendar.getInstance(Locale.getDefault());
        end.add(Calendar.HOUR_OF_DAY, 1);

        //提示
        alertType = AlertType.ALERT_NO;
    }

    @OnCheckedChanged(R.id.switchAllDay)
    void changeAllDay() {
        if (isEditable)
            updateTime();
        else {
            switchAllDay.setChecked(!switchAllDay.isChecked());
        }
    }

    @OnClick(R.id.btnStart)
    void changeStart() {
        if (isEditable)
            showDatePicker(true);
    }

    @OnClick(R.id.btnEnd)
    void changeEnd() {
        if (isEditable)
            showDatePicker(false);
    }

    @OnClick(R.id.tvAlert)
    void changeAlert() {
        if (!isEditable)
            return;
        String[] items = new String[AlertType.values().length];
        int i = 0;
        for (AlertType type : AlertType.values()) {
            items[i] = context.getString(type.getTitle());
            i++;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context, MyApplication.DIALOG_STYLE);
        builder.setTitle(R.string.alert);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertType = AlertType.values()[which];
                tvAlert.setText(alertType.getTitle());
                if (alertType == AlertType.ALERT_NO) {
                    tvAlert.setTextColor(ContextCompat.getColor(context, R.color.gray));
                } else {
                    tvAlert.setTextColor(ContextCompat.getColor(context, R.color.black));
                }
            }
        });
        builder.create().show();
    }

    /**
     * 顯示日期選擇
     */
    private void showDatePicker(final boolean isStart) {
        final Calendar toBeChanged = Calendar.getInstance(Locale.getDefault());
        if (isStart)
        {
            toBeChanged.setTime(start.getTime());
        }
        else
        {
            toBeChanged.setTime(end.getTime());
        }

        DatePickerDialog datePicker = new DatePickerDialog(context, new DatePickerDialog
                .OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                toBeChanged.set(Calendar.YEAR, year);
                toBeChanged.set(Calendar.MONTH, month);
                toBeChanged.set(Calendar.DAY_OF_MONTH, day);
                if (isStart) {
                    start.setTime(toBeChanged.getTime());
                    if (start.getTime().compareTo(end.getTime()) > 0) {
                        // 結束時間比開始時間早，調整結束時間
                        end.setTime(toBeChanged.getTime());
                        end.add(Calendar.HOUR_OF_DAY, 1);
                    }
                } else {
                    end.setTime(toBeChanged.getTime());
                }
                if (switchAllDay.isChecked()) {
                    /*全天*/
                    if (start.getTime().compareTo(end.getTime()) > 0) {
                        end.setTime(start.getTime());
                        end.add(Calendar.HOUR_OF_DAY, 1);
                        Toast.makeText(context, context.getString(R.string
                                .error_msg_end_time_before_start_time), Toast.LENGTH_LONG).show();
                    }
                    updateTime();
                } else {
                    showTimePicker(toBeChanged, isStart);
                }
            }
        }, toBeChanged.get(Calendar.YEAR), toBeChanged.get(Calendar.MONTH), toBeChanged.get(Calendar.DAY_OF_MONTH));

        if (isStart) {
//            datePicker.getDatePicker().setMinDate(new Date().getTime());
        } else {
            datePicker.getDatePicker().setMinDate(start.getTime().getTime());
        }
        datePicker.show();
    }

    /**
     * 顯示時間選擇
     */
    private void showTimePicker(final Calendar toBeChanged, final boolean isStart) {
        TimePickerDialog timePicker = new TimePickerDialog(context, new TimePickerDialog
                .OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                toBeChanged.set(Calendar.MINUTE, minute);
                toBeChanged.set(Calendar.HOUR_OF_DAY, hour);
                if (isStart) {
                    start.setTime(toBeChanged.getTime());
                    if (start.getTime().compareTo(end.getTime()) > 0) {
                        // 結束時間比開始時間早，調整結束時間
                        end.setTime(toBeChanged.getTime());
                        end.add(Calendar.HOUR_OF_DAY, 1);
                    }
                } else {
                    end.setTime(toBeChanged.getTime());
                    if (DateUtils.isSameDay(start.getTime(), end.getTime())) {
                        if (start.getTime().compareTo(end.getTime()) > 0) {
                            // 結束時間比開始時間早，調整結束時間
                            end.setTime(start.getTime());
                            end.add(Calendar.HOUR_OF_DAY, 1);
                            Toast.makeText(context, context.getString(R.string
                                    .error_msg_end_time_before_start_time), Toast.LENGTH_LONG).show();
                        }
                    }
                }
                updateTime();
            }
        }, toBeChanged.get(Calendar.HOUR_OF_DAY), toBeChanged.get(Calendar.MINUTE), true);
        timePicker.show();
    }

    /**
     * 刷新顯示時間
     */
    public void updateTime() {
        if (switchAllDay.isChecked()) {
            btnStart.setText(TimeFormater.getInstance().toDateFormat(start.getTime()));
            btnEnd.setText(TimeFormater.getInstance().toDateFormat(end.getTime()));
        } else {
            btnStart.setText(TimeFormater.getInstance().toDateTimeFormat(start.getTime()));
            btnEnd.setText(TimeFormater.getInstance().toDateTimeFormat(end.getTime()));
        }
    }

    public void setStart(Date date) {
        start.setTime(date);
    }

    public void setEnd(Date date) {
        end.setTime(date);
    }

    public Calendar getStart() {
        if (switchAllDay.isChecked()) {
            Calendar dateWithoutTime = (Calendar) start.clone();
            dateWithoutTime.set(Calendar.HOUR_OF_DAY, 0);
            dateWithoutTime.set(Calendar.MINUTE, 0);
            dateWithoutTime.set(Calendar.SECOND, 0);
            return dateWithoutTime;
        } else {
            return start;
        }
    }

    public Calendar getEnd() {
        if (switchAllDay.isChecked()) {
            Calendar dateWithoutTime = (Calendar) end.clone();
            dateWithoutTime.set(Calendar.HOUR_OF_DAY, 23);
            dateWithoutTime.set(Calendar.MINUTE, 59);
            dateWithoutTime.set(Calendar.SECOND, 59);
            return dateWithoutTime;
        } else {
            return end;
        }
    }

    public AlertType getAlertType() {
        if (containerAlert.getVisibility() == GONE)
            return AlertType.ALERT_NO;
        return alertType;
    }

    public boolean isAllday() {
        if (switchAllDay.getVisibility() == GONE) {
            return false;
        }
        return switchAllDay.isChecked();
    }

    public void setAllday(boolean isAllday) {
        switchAllDay.setChecked(isAllday);
        updateTime();
    }

    public void setStartTitle(String title) {
        tvStart.setText(title);
    }

    public void setEndTitle(String title) {
        tvEnd.setText(title);
    }

    public void disableAlert() {
        containerAlert.setVisibility(GONE);
    }

    public void enableAlert() {
        containerAlert.setVisibility(VISIBLE);
    }

    public void disableAllday() {
        switchAllDay.setVisibility(GONE);
    }

    public void enableAllday() {
        switchAllDay.setVisibility(VISIBLE);
    }

    public void disableEdit() {
        isEditable = false;
    }
}
