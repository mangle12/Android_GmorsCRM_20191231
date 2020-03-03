package tw.com.masterhand.gmorscrm.view;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import org.apache.commons.lang3.time.DateUtils;

import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.tools.UnitChanger;

public class CalendarCell extends RelativeLayout {
    Calendar calendar;
    @BindView(R.id.textView_date)
    TextView tvDate;
    @BindView(R.id.textView_week)
    TextView tvWeek;
    @BindView(R.id.textView_month)
    TextView tvMonth;
    @BindView(R.id.ivShadow)
    ImageView ivShadow;
    SimpleDateFormat dateFormat = new SimpleDateFormat("d", Locale.getDefault());
    SimpleDateFormat monthFormat = new SimpleDateFormat("M", Locale.getDefault());
    SimpleDateFormat weekFormat = new SimpleDateFormat("EEEEE", Locale.getDefault());

    public CalendarCell(Context context) {
        super(context);
        init(context);
    }

    public CalendarCell(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CalendarCell(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        // 連接畫面
        View view = inflate(getContext(), R.layout.calendar_cell, this);
        ButterKnife.bind(this, view);
    }

    public CalendarCell setDate(Calendar date) {
        calendar = (Calendar) date.clone();
        tvDate.setText(dateFormat.format(calendar.getTime()));
        tvWeek.setText(weekFormat.format(calendar.getTime()));
        return this;
    }

    public Calendar getDate() {
        return calendar;
    }

    public CalendarCell showWeek(boolean isShow) {
        if (isShow) {
            tvWeek.setVisibility(VISIBLE);
        } else {
            tvWeek.setVisibility(GONE);
            tvDate.setPaddingRelative(0, 0, 0, UnitChanger.dpToPx(5));
        }
        return this;
    }

    public CalendarCell hasItem() {
        tvDate.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.indicate_calendar_cell, 0, 0);
        return this;
    }

    public boolean isSameDate(Calendar date) {
        return DateUtils.isSameDay(calendar, date);
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (!enabled)
            tvDate.setTextColor(ContextCompat.getColor(getContext(), R.color.gray_dark));
        super.setEnabled(enabled);
    }

    public void setDateColor(ColorStateList color) {
        tvDate.setTextColor(color);
    }

    public void setDateColor(int color) {
        tvDate.setTextColor(color);
    }

    public void setWeekColor(int color) {
        tvWeek.setTextColor(color);
    }

    public void showMonth() {
        tvMonth.setText(monthFormat.format(calendar.getTime()));
    }

    public void disableShadow() {
        ivShadow.setVisibility(INVISIBLE);
    }
}
