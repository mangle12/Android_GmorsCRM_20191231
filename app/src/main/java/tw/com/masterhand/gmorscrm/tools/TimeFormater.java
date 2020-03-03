package tw.com.masterhand.gmorscrm.tools;

import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class TimeFormater {
    public static final String DATABASE_DATE_TIME_STRING = "yyyy-MM-dd HH:mm:ss";
    // 資料庫時間格式
    private SimpleDateFormat DATABASE_DATE_TIME_FORMATER;
    private SimpleDateFormat DATABASE_DATE_FORMATER;

    // 月報時間格式
    private SimpleDateFormat MONTH_REPORT_FORMATER;
    // app顯示時間格式
    private SimpleDateFormat MONTH_DAY_FORMATER;

    private SimpleDateFormat DATE_TIME_FORMATER;

    private SimpleDateFormat DATE_FORMATER;
    private SimpleDateFormat WEEK_FORMATER;
    private SimpleDateFormat TIME_FORMATER;
    private SimpleDateFormat INVITE_TIME_FORMATER;

    static TimeFormater instance;
    private static final Object LOCK = new Object();

    public TimeFormater() {
        DATABASE_DATE_TIME_FORMATER = new SimpleDateFormat(
                DATABASE_DATE_TIME_STRING, Locale.getDefault());
        DATABASE_DATE_FORMATER = new SimpleDateFormat(
                "yyyy-MM-dd", Locale.getDefault());
        DATABASE_DATE_TIME_FORMATER.setTimeZone(TimeZone.getTimeZone("GMT"));
        DATABASE_DATE_FORMATER.setTimeZone(TimeZone.getTimeZone("GMT"));
        MONTH_REPORT_FORMATER = new SimpleDateFormat(
                "yyyy/MM", Locale.getDefault());
        MONTH_DAY_FORMATER = new SimpleDateFormat(
                "MM/dd", Locale.getDefault());
        DATE_TIME_FORMATER = new SimpleDateFormat(
                "yyyy/MM/dd HH:mm", Locale.getDefault());
        DATE_FORMATER = new SimpleDateFormat(
                "yyyy/MM/dd", Locale.getDefault());
        WEEK_FORMATER = new SimpleDateFormat(
                "EEE", Locale.getDefault());
        TIME_FORMATER = new SimpleDateFormat(
                "HH:mm", Locale.getDefault());
        INVITE_TIME_FORMATER = new SimpleDateFormat(
                "MM月dd日/EEE", Locale.getDefault());
    }

    public synchronized static TimeFormater getInstance() {
        if (instance == null)
            instance = new TimeFormater();
        return instance;
    }

    public SimpleDateFormat getTimeZoneFormater() {
        return new SimpleDateFormat(
                "HH:mm(zzz)", Locale.getDefault());
    }

    public String toMonthDayFormat(Date date) {
        MONTH_DAY_FORMATER = new SimpleDateFormat(
                "MM/dd", Locale.getDefault());
        return MONTH_DAY_FORMATER.format(date);
    }

    public String toDateTimeFormat(Date date) {
        DATE_TIME_FORMATER = new SimpleDateFormat(
                "yyyy/MM/dd HH:mm", Locale.getDefault());
        return DATE_TIME_FORMATER.format(date);
    }

    public String toInviteTimeFormat(Date date) {
        INVITE_TIME_FORMATER = new SimpleDateFormat(
                "MM月dd日/EEE", Locale.getDefault());
        return INVITE_TIME_FORMATER.format(date);
    }

    public String toTimeFormat(Date date) {
        TIME_FORMATER = new SimpleDateFormat(
                "HH:mm", Locale.getDefault());
        return TIME_FORMATER.format(date);
    }

    public String toDateFormat(Date date) {
        if (date == null)
            return "-";
        DATE_FORMATER = new SimpleDateFormat(
                "yyyy/MM/dd", Locale.getDefault());
        return DATE_FORMATER.format(date);
    }

    public String toWeekFormat(Date date) {
        WEEK_FORMATER = new SimpleDateFormat(
                "EEE", Locale.getDefault());
        return WEEK_FORMATER.format(date);
    }

    public String toDatabaseFormat(Date date) {
        if (date == null)
            return "-";
        DATABASE_DATE_TIME_FORMATER = new SimpleDateFormat(
                DATABASE_DATE_TIME_STRING, Locale.getDefault());
        DATABASE_DATE_TIME_FORMATER.setTimeZone(TimeZone.getTimeZone("GMT"));
        return DATABASE_DATE_TIME_FORMATER.format(date);
    }

    public Date fromDatabaseFormat(String dateString) throws ParseException {
        DATABASE_DATE_TIME_FORMATER = new SimpleDateFormat(
                DATABASE_DATE_TIME_STRING, Locale.getDefault());
        DATABASE_DATE_TIME_FORMATER.setTimeZone(TimeZone.getTimeZone("GMT"));
        return DATABASE_DATE_TIME_FORMATER.parse(dateString);
    }

    public String toMonthReportFormat(Date date) {
        MONTH_REPORT_FORMATER = new SimpleDateFormat(
                "yyyy/MM", Locale.getDefault());
        return MONTH_REPORT_FORMATER.format(date);
    }

    public Date fromMonthReportFormat(String dateString) throws ParseException {
        MONTH_REPORT_FORMATER = new SimpleDateFormat(
                "yyyy/MM", Locale.getDefault());
        return MONTH_REPORT_FORMATER.parse(dateString);
    }

    public String toPeriodFormat(Date start, Date end) {
        return new SimpleDateFormat(
                "MM/dd", Locale.getDefault()).format(start) + "-" + new SimpleDateFormat(
                "MM/dd", Locale.getDefault()).format(end);
    }

    public String toPeriodYearFormat(Date start, Date end) {
        DATE_FORMATER = new SimpleDateFormat(
                "yyyy/MM/dd", Locale.getDefault());
        return DATE_FORMATER.format(start) + "-" + DATE_FORMATER.format(end);
    }

    public String toPeriodTimeFormat(Date start, Date end) {
        TIME_FORMATER = new SimpleDateFormat(
                "HH:mm", Locale.getDefault());
        return TIME_FORMATER.format(start) + "-" + TIME_FORMATER.format(end);
    }

    public Date getEndOfDay(Date date) {
        return DateUtils.addMilliseconds(DateUtils.ceiling(date, Calendar.DATE), -1);
    }

    public Date getStartOfDay(Date date) {
        return DateUtils.truncate(date, Calendar.DATE);
    }

    public static int getCurrentTimeZone() {
        Calendar mCalendar = new GregorianCalendar();
        TimeZone mTimeZone = mCalendar.getTimeZone();
        int mGMTOffset = mTimeZone.getRawOffset();
        return (int) TimeUnit.HOURS.convert(mGMTOffset, TimeUnit
                .MILLISECONDS);
    }
}
