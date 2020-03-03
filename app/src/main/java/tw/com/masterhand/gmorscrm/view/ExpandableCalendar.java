package tw.com.masterhand.gmorscrm.view;

import android.content.Context;
import android.graphics.Point;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.GridLayout;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.apache.commons.lang3.time.DateUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import tw.com.masterhand.gmorscrm.MainActivity;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.TokenManager;
import tw.com.masterhand.gmorscrm.enums.AcceptType;
import tw.com.masterhand.gmorscrm.enums.TripType;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.record.Participant;
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.tools.TimeFormater;
import tw.com.masterhand.gmorscrm.tools.UnitChanger;
import tw.com.masterhand.gmorscrm.model.MainTrip;

public class ExpandableCalendar extends LinearLayout implements View.OnClickListener, ObservableHorizontalScrollView.Callbacks {
    final String TAG = getClass().getSimpleName();

    @BindView(R.id.textView_calendar_year)
    TextView tvYear;
    @BindView(R.id.textView_calendar_month)
    TextView tvMonth;
    @BindView(R.id.scrollView_calendar)
    ObservableHorizontalScrollView calendar;
    @BindView(R.id.relativeLayout_calendar_control)
    RelativeLayout controlContainer;
    @BindView(R.id.relativeLayout_calendar_expanded_control)
    RelativeLayout controlExpandedContainer;
    @BindView(R.id.linearLayout_calendar)
    LinearLayout calendarContainer;
    @BindView(R.id.gridLayout_calendar_expanded)
    GridLayout calendarExpandedContainer;
    @BindView(R.id.button_calendar_control_left)
    Button btnControlLeft;
    @BindView(R.id.button_calendar_control_right)
    Button btnControlRight;
    @BindView(R.id.button_calendar_expanded_control_today)
    Button btnControlExpandedToday;
    @BindView(R.id.button_calendar_expanded_control_left)
    ImageButton btnControlExpandedLeft;
    @BindView(R.id.button_calendar_expanded_control_right)
    ImageButton btnControlExpandedRight;
    // 監聽器
    OnExpandedListener onExpandedListener;
    OnDateSelectedListener onDateSelectedListener;
    GestureDetectorCompat mDetector;
    // 目前選擇日期
    Calendar currentDate;
    // 展開與否
    boolean isExpanded;
    // 螢幕寬
    int screenWidth, calendarCellWidth, calendarExpandedCellWidth;
    // 日期目前頁面
    int calendarIndex;

    public ExpandableCalendar(Context context) {
        super(context);
        init(context);
    }

    public ExpandableCalendar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ExpandableCalendar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        mDetector = new GestureDetectorCompat(context, new ExpandedCalendarGestureListener());
        Point size = new Point();
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getSize(size);
        screenWidth = size.x;
        calendarCellWidth = (screenWidth - UnitChanger.dpToPx(6)) / 7;
        calendarExpandedCellWidth = screenWidth / 7;
        Logger.e(TAG, "screen width:" + screenWidth);
        Logger.e(TAG, "cell width:" + calendarCellWidth);
        // 連接畫面
        View view = inflate(getContext(), R.layout.expandable_calendar, this);
        ButterKnife.bind(this, view);
        // 預設
        isExpanded = false;
        Calendar today = Calendar.getInstance(Locale.getDefault());
        currentDate = today;
        // 監聽日曆
        calendar.setCallbacks(this);
    }

    @OnClick({R.id.button_calendar_control_right, R.id.button_calendar_expanded_control_left, R
            .id.button_calendar_expanded_control_right, R.id.button_calendar_control_left, R.id
            .button_calendar_expanded_control_today})
    public void onClick(View view) {
        switch (view.getId()) {
            // 更多
            case R.id.button_calendar_control_right:
                calendarExpand();
                break;

            // 上個月
            case R.id.button_calendar_expanded_control_left:
                moveCalendar(-1);
                break;

            // 下個月
            case R.id.button_calendar_expanded_control_right:
                moveCalendar(1);
                break;

            // 今天
            case R.id.button_calendar_control_left:
            case R.id.button_calendar_expanded_control_today:
                Calendar today = Calendar.getInstance(Locale.getDefault());
                displayCalendar(today);
                setCurrentDate(today);
                break;
        }
    }

    /**
     * 變更日曆月份
     *
     * @param calMonth 增減月數
     */
    private void moveCalendar(int calMonth) {
        CalendarCell cell = (CalendarCell) calendarExpandedContainer.getChildAt(15);
        Calendar changeDate = (Calendar) cell.getDate().clone();
        changeDate.add(Calendar.MONTH, calMonth);
        displayCalendar(changeDate);
    }

    /**
     * 顯示日曆
     *
     * @param date 顯示日期
     */
    public void displayCalendar(Calendar date) {
        // 顯示年月
        tvMonth.setText(new SimpleDateFormat("MM", Locale.getDefault()).format(date.getTime()));
        tvYear.setText(new SimpleDateFormat("yyyy", Locale.getDefault()).format(date.getTime()));
        // 顯示日曆
        Calendar tempDate = (Calendar) date.clone();
        if (isExpanded) {
            // 展開
            int dayAfter = 0;
            Calendar endDate = (Calendar) tempDate.clone();
            endDate.add(Calendar.MONTH, 1);
            endDate.set(Calendar.DAY_OF_MONTH, 1);
            switch (endDate.get(Calendar.DAY_OF_WEEK)) {
                case Calendar.MONDAY:
                    dayAfter = 0;
                    break;
                case Calendar.SUNDAY:
                    dayAfter = 1;
                    break;
                default:
                    dayAfter = 7 - endDate.get(Calendar.DAY_OF_WEEK) + 2;
                    break;
            }
            tempDate.set(Calendar.DAY_OF_MONTH, 1);
            int dayBefore = 0;
            switch (tempDate.get(Calendar.DAY_OF_WEEK)) {
                case Calendar.SUNDAY:
                    dayBefore = 6;
                    break;
                default:
                    dayBefore = tempDate.get(Calendar.DAY_OF_WEEK) - 2;
                    break;
            }
            tempDate.add(Calendar.DAY_OF_YEAR, dayBefore * -1);
            calendarExpandedContainer.removeAllViews();
            SimpleDateFormat weekFormat = new SimpleDateFormat("EEEEE", Locale.getDefault());
            int index = 1, c = 0, r = 1;
            do {
                if (index < 8) {
                    // 顯示星期列
                    TextView tvTitle = new TextView(getContext());
                    tvTitle.setText(weekFormat.format(tempDate.getTime()));
                    tvTitle.setGravity(Gravity.CENTER);
                    tvTitle.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
                    GridLayout.LayoutParams gridParams = new GridLayout.LayoutParams();
                    gridParams.height = UnitChanger.dpToPx(24);
                    gridParams.width = UnitChanger.dpToPx(50);
                    gridParams.setGravity(Gravity.CENTER);
                    gridParams.columnSpec = GridLayout.spec(c);
                    gridParams.rowSpec = GridLayout.spec(0);
                    calendarExpandedContainer.addView(tvTitle, gridParams);
                }

                GridLayout.LayoutParams gridParams = new GridLayout.LayoutParams();
                gridParams.height = calendarExpandedCellWidth;
                gridParams.width = calendarExpandedCellWidth;
                gridParams.setGravity(Gravity.CENTER);
                gridParams.columnSpec = GridLayout.spec(c);
                gridParams.rowSpec = GridLayout.spec(r);
                final CalendarCell cell = createCell(tempDate, false);
                cell.disableShadow();
                cell.setBackgroundResource(R.drawable.bg_calendar_expanded_cell_selector);
                cell.setDateColor(ContextCompat.getColorStateList(getContext(), R.color.calendar_expanded_cell_date));

                if (tempDate.get(Calendar.MONTH) != date.get(Calendar.MONTH)) {
                    cell.setEnabled(false);
                } else {
                    if (DateUtils.isSameDay(tempDate, currentDate))
                        cell.setSelected(true);
                }
                cell.setOnTouchListener(new OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        mDetector.onTouchEvent(motionEvent);
                        return false;
                    }
                });
                DatabaseHelper.getInstance(getContext()).getTripCount(tempDate.getTime(), TokenManager.getInstance().getUser().getId())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Integer>() {

                    @Override
                    public void accept(Integer integer) throws Exception {
                        if (integer > 0)
                        {
                            cell.hasItem();
                        }
                    }
                });

                calendarExpandedContainer.addView(cell, gridParams);
                tempDate.add(Calendar.DAY_OF_YEAR, 1);
                index++;
                c++;
                if (index % 7 == 1) {
                    c = 0;
                    r++;
                }
            }
            while (index <= dayBefore + date.getActualMaximum(Calendar.DAY_OF_MONTH) + dayAfter);
        } else {
            // 未展開
            tempDate.add(Calendar.DAY_OF_WEEK, -10);
            LayoutParams params = new LayoutParams(calendarCellWidth, getResources()
                    .getDimensionPixelSize(R
                            .dimen.calender_cell_height));
            calendarContainer.setVisibility(INVISIBLE);
            calendarContainer.removeAllViews();
            for (int i = 0; i < 21; i++) {
                final CalendarCell cell = createCell(tempDate, true);
                if (cell.getDate().get(Calendar.DAY_OF_MONTH) == 1) {
                    cell.showMonth();
                }
                DatabaseHelper.getInstance(getContext()).getTripCount(tempDate.getTime(), TokenManager.getInstance().getUser().getId())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Integer>() {

                    @Override
                    public void accept(Integer integer) throws Exception {
                        if (integer > 0)
                            cell.hasItem();
                    }
                });
                calendarContainer.addView(cell, params);
                tempDate.add(Calendar.DAY_OF_WEEK, 1);
            }
            calendarContainer.postDelayed(new Runnable() {
                @Override
                public void run() {
                    calendar.scrollTo(screenWidth, 0);
                    calendarContainer.setVisibility(VISIBLE);
                }
            }, 500);
        }
    }

    public Calendar getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(Calendar calendar) {
        calendarIndex = 1;
        currentDate = (Calendar) calendar.clone();
        if (isExpanded) {
            // 展開
            for (int i = 0; i < calendarExpandedContainer.getChildCount(); i++) {
                if (calendarExpandedContainer.getChildAt(i) instanceof CalendarCell) {
                    CalendarCell cell = (CalendarCell) calendarExpandedContainer.getChildAt(i);
                    cell.setSelected(cell.isSameDate(currentDate));
                }
            }
        } else {
            // 未展開
            for (int i = 0; i < calendarContainer.getChildCount(); i++) {
                CalendarCell cell = (CalendarCell) calendarContainer.getChildAt(i);
                cell.setSelected(cell.isSameDate(currentDate));
            }
        }
        if (onDateSelectedListener != null)
            onDateSelectedListener.onDateSelected(currentDate);
    }

    // 產生日曆項目
    private CalendarCell createCell(Calendar calendar, boolean isWeekShow) {
        final CalendarCell cell = new CalendarCell(getContext());
        cell.setDate(calendar).showWeek(isWeekShow);
        // 點擊日曆項目
        cell.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setCurrentDate(cell.getDate());
            }
        });
        return cell;
    }

    /**
     * 展開日曆
     */
    public void calendarExpand() {
        isExpanded = true;
        calendar.setVisibility(GONE);
        calendarExpandedContainer.setVisibility(VISIBLE);
        controlContainer.setVisibility(GONE);
        controlExpandedContainer.setVisibility(VISIBLE);
        displayCalendar(currentDate);
        setCurrentDate(currentDate);
        if (onExpandedListener != null) {
            onExpandedListener.onExpanded();
        }
    }

    /**
     * 收起日曆
     */
    public void calendarCollapse() {
        isExpanded = false;
        calendar.setVisibility(VISIBLE);
        calendarExpandedContainer.setVisibility(GONE);
        controlContainer.setVisibility(VISIBLE);
        controlExpandedContainer.setVisibility(GONE);
        displayCalendar(currentDate);
        setCurrentDate(currentDate);
        if (onExpandedListener != null) {
            onExpandedListener.onCollapse();
        }
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    /**
     * 設定日曆展開監聽器
     *
     * @param listener OnExpandedListener
     */
    public void setOnExpandedListener(OnExpandedListener listener) {
        onExpandedListener = listener;
    }

    /**
     * 設定日期選擇監聽器
     *
     * @param listener OnDateSelectedListener
     */
    public void setOnDateSelectedListener(OnDateSelectedListener listener) {
        onDateSelectedListener = listener;
    }

    @Override
    public void onScrollStart() {

    }

    @Override
    public void onScrollStop() {
        float x = calendar.getScrollX();
        float diff = calendarCellWidth * .8f;
        switch (calendarIndex) {
            case 0:
                if (x > diff) {
                    calendarIndex = 1;
                }
                break;
            case 1:
                if (x < screenWidth - diff) {
                    calendarIndex = 0;
                } else if (x > screenWidth + diff) {
                    calendarIndex = 2;
                }
                break;
            case 2:
                if (x < screenWidth * 2 - diff) {
                    calendarIndex = 1;
                }
                break;
        }
        calendar.post(new Runnable() {
            @Override
            public void run() {
                calendar.smoothScrollTo(screenWidth * calendarIndex, 0);
            }
        });
    }

    // 日曆展開監聽器
    public interface OnExpandedListener {
        void onExpanded();

        void onCollapse();
    }

    // 日期選擇監聽器
    public interface OnDateSelectedListener {
        void onDateSelected(Calendar selectDate);
    }

    class ExpandedCalendarGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            boolean result = false;
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) >
                            SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            onSwipeRight();
                        } else {
                            onSwipeLeft();
                        }
                    }
                    result = true;
                } else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) >
                        SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        onSwipeBottom();
                    } else {
                        onSwipeTop();
                    }
                }
                result = true;

            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return result;
        }
    }

    public void onSwipeRight() {
        Logger.e(TAG, "onSwipeRight");
        moveCalendar(-1);
    }

    public void onSwipeLeft() {
        Logger.e(TAG, "onSwipeLeft");
        moveCalendar(1);
    }

    public void onSwipeTop() {
        Logger.e(TAG, "onSwipeTop");
        calendarCollapse();
    }

    public void onSwipeBottom() {
        Logger.e(TAG, "onSwipeBottom");
    }
}
