package tw.com.masterhand.gmorscrm.view;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.enums.ReportFilterType;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.tools.TimeFormater;

public class ItemReport extends RelativeLayout {
    @BindView(R.id.textView_week_mark)
    TextView tvWeekMark;
    @BindView(R.id.textView_week)
    TextView tvWeek;
    @BindView(R.id.textView_year)
    TextView tvYear;
    @BindView(R.id.textView_period)
    TextView tvPeriod;
    @BindView(R.id.textView_count)
    TextView tvCount;
    @BindView(R.id.textView_monday)
    TextView tvMonday;
    @BindView(R.id.textView_tuesday)
    TextView tvTuesday;
    @BindView(R.id.textView_wednesday)
    TextView tvWednesday;
    @BindView(R.id.textView_thursday)
    TextView tvThursday;
    @BindView(R.id.textView_friday)
    TextView tvFriday;
    @BindView(R.id.textView_saturday)
    TextView tvSaturday;
    @BindView(R.id.textView_sunday)
    TextView tvSunday;
    @BindView(R.id.pointSunday)
    View pointSunday;
    @BindView(R.id.pointMonday)
    View pointMonday;
    @BindView(R.id.pointTuesday)
    View pointTuesday;
    @BindView(R.id.pointWednesday)
    View pointWednesday;
    @BindView(R.id.pointThursday)
    View pointThursday;
    @BindView(R.id.pointFriday)
    View pointFriday;
    @BindView(R.id.pointSaturday)
    View pointSaturday;
    @BindView(R.id.linearLayout_week)
    LinearLayout containerWeek;

    Context context;
    int sunday = 0;
    int monday = 0;
    int tuesday = 0;
    int wednesday = 0;
    int thursday = 0;
    int friday = 0;
    int saturday = 0;

    String parentId;
    ReportFilterType type;

    public ItemReport(Context context, String parentId, ReportFilterType type) {
        super(context);
        this.parentId = parentId;
        this.type = type;
        init(context);
    }

    public ItemReport(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ItemReport(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context mContext) {
        context = mContext;
        // 連接畫面
        View view = inflate(getContext(), R.layout.item_report, this);
        ButterKnife.bind(this, view);
    }

    public void setUserId(String userId) {
        type = ReportFilterType.PERSON;
        this.parentId = userId;
    }

    public void setDepartmentId(String departmentId) {
        type = ReportFilterType.DEPARTMENT;
        this.parentId = departmentId;
    }

    public void setCompanyId(String companyId) {
        type = ReportFilterType.COMPANY;
        this.parentId = companyId;
    }

    private void updateCount() {
        tvSunday.setText(String.valueOf(sunday));
        tvMonday.setText(String.valueOf(monday));
        tvTuesday.setText(String.valueOf(tuesday));
        tvWednesday.setText(String.valueOf(wednesday));
        tvThursday.setText(String.valueOf(thursday));
        tvFriday.setText(String.valueOf(friday));
        tvSaturday.setText(String.valueOf(saturday));
        if (sunday > 0) {
            tvSunday.setBackground(ContextCompat.getDrawable(context, R.drawable.oval_orange));
        } else {
            tvSunday.setBackground(ContextCompat.getDrawable(context, R.drawable.oval_gray));
        }
        if (monday > 0) {
            tvMonday.setBackground(ContextCompat.getDrawable(context, R.drawable.oval_orange));
        } else {
            tvMonday.setBackground(ContextCompat.getDrawable(context, R.drawable.oval_gray));
        }
        if (tuesday > 0) {
            tvTuesday.setBackground(ContextCompat.getDrawable(context, R.drawable.oval_orange));
        } else {
            tvTuesday.setBackground(ContextCompat.getDrawable(context, R.drawable.oval_gray));
        }
        if (wednesday > 0) {
            tvWednesday.setBackground(ContextCompat.getDrawable(context, R.drawable.oval_orange));
        } else {
            tvWednesday.setBackground(ContextCompat.getDrawable(context, R.drawable.oval_gray));
        }
        if (thursday > 0) {
            tvThursday.setBackground(ContextCompat.getDrawable(context, R.drawable.oval_orange));
        } else {
            tvThursday.setBackground(ContextCompat.getDrawable(context, R.drawable.oval_gray));
        }
        if (friday > 0) {
            tvFriday.setBackground(ContextCompat.getDrawable(context, R.drawable.oval_orange));
        } else {
            tvFriday.setBackground(ContextCompat.getDrawable(context, R.drawable.oval_gray));
        }
        if (saturday > 0) {
            tvSaturday.setBackground(ContextCompat.getDrawable(context, R.drawable.oval_orange));
        } else {
            tvSaturday.setBackground(ContextCompat.getDrawable(context, R.drawable.oval_gray));
        }
    }

    public void setTime(Calendar input, CompositeDisposable mDisposable) {
        final Calendar calendar = (Calendar) input.clone();
        Calendar today = Calendar.getInstance(Locale.getDefault());
        tvYear.setText(String.valueOf(calendar.get(Calendar.YEAR)));
        tvWeek.setText(String.valueOf(calendar.get(Calendar.WEEK_OF_YEAR)));
        if (today.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) && today.get(Calendar.WEEK_OF_YEAR) == calendar.get(Calendar.WEEK_OF_YEAR)) {
            tvWeekMark.setText(context.getString(R.string.this_week));
            tvWeekMark.setTextColor(ContextCompat.getColor(context, R.color.orange));
            containerWeek.setBackgroundResource(R.drawable.bg_white_corner);
        } else {
            tvWeekMark.setText(context.getString(R.string.week_unit));
            tvWeekMark.setTextColor(ContextCompat.getColor(context, R.color.black));
            containerWeek.setBackgroundResource(R.drawable.bg_transparent);
        }

        String period = "";
        calendar.set(Calendar.DAY_OF_WEEK, 1);
        period += TimeFormater.getInstance().toMonthDayFormat(calendar.getTime());
        period += "-";
        calendar.set(Calendar.DAY_OF_WEEK, 7);
        period += TimeFormater.getInstance().toMonthDayFormat(calendar.getTime());
        tvPeriod.setText(period);
        for (int i = 1; i < 8; i++) {
            final int dayOfWeek = i;
            calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
            if (type == ReportFilterType.PERSON)
                mDisposable.add(DatabaseHelper.getInstance(getContext()).getHasEmptyReportByDate(calendar.getTime(),
                        parentId).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>() {


                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean)
                            switch (dayOfWeek) {
                                case 1:
                                    pointSunday.setVisibility(VISIBLE);
                                    break;
                                case 2:
                                    pointMonday.setVisibility(VISIBLE);
                                    break;
                                case 3:
                                    pointTuesday.setVisibility(VISIBLE);
                                    break;
                                case 4:
                                    pointWednesday.setVisibility(VISIBLE);
                                    break;
                                case 5:
                                    pointThursday.setVisibility(VISIBLE);
                                    break;
                                case 6:
                                    pointFriday.setVisibility(VISIBLE);
                                    break;
                                case 7:
                                    pointSaturday.setVisibility(VISIBLE);
                                    break;
                            }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ErrorHandler.getInstance().setException(getContext(), throwable);
                    }
                }));

            mDisposable.add(DatabaseHelper.getInstance(getContext()).getTripCount(calendar.getTime(), parentId, type)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Integer>() {

                @Override
                public void accept(Integer count) throws Exception {
                    switch (dayOfWeek) {
                        case 1:
                            sunday = count;
                            break;
                        case 2:
                            monday = count;
                            break;
                        case 3:
                            tuesday = count;
                            break;
                        case 4:
                            wednesday = count;
                            break;
                        case 5:
                            thursday = count;
                            break;
                        case 6:
                            friday = count;
                            break;
                        case 7:
                            saturday = count;
                            break;
                    }
                    updateCount();
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    ErrorHandler.getInstance().setException(getContext(), throwable);
                }
            }));
        }
    }

}
