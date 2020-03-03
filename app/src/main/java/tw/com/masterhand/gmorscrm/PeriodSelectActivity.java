package tw.com.masterhand.gmorscrm;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import tw.com.masterhand.gmorscrm.tools.TimeFormater;
import tw.com.masterhand.gmorscrm.view.Appbar;

public class PeriodSelectActivity extends BaseUserCheckActivity {
    @BindView(R.id.appbar)
    Appbar appbar;
    @BindView(R.id.btnStart)
    Button btnStart;
    @BindView(R.id.btnEnd)
    Button btnEnd;

    Date start, end;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_period_select);
        appbar.setTitle(getString(R.string.title_activity_period_select));
        appbar.addFunction(getString(R.string.clear), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 清除
                init();
            }
        });
        init();
    }


    @Override
    protected void onUserChecked() {
        appbar.invalidate();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(MyApplication.INTENT_KEY_DATE_START, TimeFormater.getInstance().toDatabaseFormat(start));
        intent.putExtra(MyApplication.INTENT_KEY_DATE_END, TimeFormater.getInstance().toDatabaseFormat(end));
        setResult(RESULT_OK, intent);
        finish();
    }

    private void init() {
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        start = calendar.getTime();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        end = calendar.getTime();
        updateUI();
    }

    void updateUI() {
        btnStart.setText(TimeFormater.getInstance().toDateFormat(start));
        btnEnd.setText(TimeFormater.getInstance().toDateFormat(end));
    }

    @OnClick(R.id.btnStart)
    void changeStart() {
        final Calendar toBeChanged = Calendar.getInstance(Locale.getDefault());
        toBeChanged.setTime(start);
        DatePickerDialog datePicker = new DatePickerDialog(this, new DatePickerDialog
                .OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                toBeChanged.set(Calendar.YEAR, year);
                toBeChanged.set(Calendar.MONTH, month);
                toBeChanged.set(Calendar.DAY_OF_MONTH, day);
                start.setTime(toBeChanged.getTime().getTime());
                if (start.compareTo(end) > 0) {
                    // 結束時間比開始時間早，調整結束時間
                    toBeChanged.set(Calendar.HOUR_OF_DAY, 23);
                    toBeChanged.set(Calendar.MINUTE, 59);
                    toBeChanged.set(Calendar.SECOND, 59);
                    end.setTime(toBeChanged.getTime().getTime());
                }
                updateUI();
            }
        }, toBeChanged.get(Calendar.YEAR), toBeChanged.get(Calendar.MONTH), toBeChanged.get
                (Calendar.DAY_OF_MONTH));
        datePicker.show();
    }

    @OnClick(R.id.btnEnd)
    void changeEnd() {
        final Calendar toBeChanged = Calendar.getInstance(Locale.getDefault());
        toBeChanged.setTime(end);
        DatePickerDialog datePicker = new DatePickerDialog(this, new DatePickerDialog
                .OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                toBeChanged.set(Calendar.YEAR, year);
                toBeChanged.set(Calendar.MONTH, month);
                toBeChanged.set(Calendar.DAY_OF_MONTH, day);
                end.setTime(toBeChanged.getTime().getTime());
                if (start.compareTo(end) > 0) {
                    // 結束時間比開始時間早，調整結束時間
                    Toast.makeText(PeriodSelectActivity.this, R.string
                            .error_msg_end_time_before_start_time, Toast.LENGTH_LONG).show();
                    toBeChanged.setTime(start);
                    toBeChanged.set(Calendar.HOUR_OF_DAY, 23);
                    toBeChanged.set(Calendar.MINUTE, 59);
                    toBeChanged.set(Calendar.SECOND, 59);
                    end.setTime(toBeChanged.getTime().getTime());
                }
                updateUI();
            }
        }, toBeChanged.get(Calendar.YEAR), toBeChanged.get(Calendar.MONTH), toBeChanged.get
                (Calendar.DAY_OF_MONTH));
        datePicker.getDatePicker().setMinDate(start.getTime());
        datePicker.show();
    }

}
