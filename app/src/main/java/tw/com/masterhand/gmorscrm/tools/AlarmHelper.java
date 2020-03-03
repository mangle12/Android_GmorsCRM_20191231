package tw.com.masterhand.gmorscrm.tools;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.TokenManager;
import tw.com.masterhand.gmorscrm.room.record.Trip;

public class AlarmHelper {
    private static void setAlarm(Context context, long time, PendingIntent pending) {
        Logger.e("AlarmHelper", "set alarm:" + TimeFormater.getInstance().toDateTimeFormat(new Date(time)));
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context
                .ALARM_SERVICE);
        if (alarmManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { //MARSHMALLOW OR ABOVE
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pending);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { //LOLLIPOP 21 OR ABOVE
                AlarmManager.AlarmClockInfo alarmClockInfo = new AlarmManager.AlarmClockInfo(time, pending);
                alarmManager.setAlarmClock(alarmClockInfo, pending);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { //KITKAT 19 OR ABOVE
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, pending);
            } else { //FOR BELOW KITKAT ALL DEVICES
                alarmManager.set(AlarmManager.RTC_WAKEUP, time, pending);
            }
        }
    }

    public static void testReportCheckAlarm(Context context) {
        Calendar alarmTime = Calendar.getInstance(Locale.getDefault());
        alarmTime.add(Calendar.SECOND, 10);
        try {
            Intent alarmIntent = new Intent();
            alarmIntent.setAction(MyApplication.ACTION_REPORT_CHECK_ALARM);
            alarmIntent.putExtra(MyApplication.INTENT_KEY_ID, TokenManager.getInstance()
                    .getUser().getId());
            alarmIntent.putExtra(MyApplication.INTENT_KEY_USER, TokenManager.getInstance()
                    .getUser().getAccount());
            PendingIntent pending = PendingIntent.getBroadcast(context, MyApplication
                            .REQUEST_REPORT_CHECK_ALARM,
                    alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            setAlarm(context, alarmTime.getTime().getTime(), pending);
        } catch (Exception e) {
            Log.e(context.getClass().getSimpleName(), "Exception:" + e.getMessage());
        }
    }

    public static void setReportCheckAlarm(Context context) {
        Calendar alarmTime = Calendar.getInstance(Locale.getDefault());
        // 設定12點及21點執行
        alarmTime.set(Calendar.HOUR_OF_DAY, 12);
        alarmTime.set(Calendar.MINUTE, 0);
        alarmTime.set(Calendar.SECOND, 0);
        alarmTime.set(Calendar.MILLISECOND, 0);
        if (alarmTime.getTime().compareTo(new Date()) < 0) {
            alarmTime.set(Calendar.HOUR_OF_DAY, 21);
            alarmTime.set(Calendar.MINUTE, 0);
            if (alarmTime.getTime().compareTo(new Date()) < 0) {
                alarmTime.set(Calendar.HOUR_OF_DAY, 12);
                alarmTime.set(Calendar.MINUTE, 0);
                alarmTime.add(Calendar.DATE, 1);
            }
        }
        try {
            Intent alarmIntent = new Intent();
            alarmIntent.setAction(MyApplication.ACTION_REPORT_CHECK_ALARM);
            alarmIntent.putExtra(MyApplication.INTENT_KEY_ID, TokenManager.getInstance()
                    .getUser().getId());
            alarmIntent.putExtra(MyApplication.INTENT_KEY_USER, TokenManager.getInstance()
                    .getUser().getAccount());
            PendingIntent pending = PendingIntent.getBroadcast(context, MyApplication
                            .REQUEST_REPORT_CHECK_ALARM,
                    alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            setAlarm(context, alarmTime.getTime().getTime(), pending);
        } catch (Exception e) {
            Log.e(context.getClass().getSimpleName(), "Exception:" + e.getMessage());
        }
    }

    public static void cancelReportCheckAlarm(Context context) {
        try {
            AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent alarmIntent = new Intent();
            alarmIntent.setAction(MyApplication.ACTION_REPORT_CHECK_ALARM);
            alarmIntent.putExtra(MyApplication.INTENT_KEY_ID, TokenManager.getInstance()
                    .getUser().getId());
            alarmIntent.putExtra(MyApplication.INTENT_KEY_USER, TokenManager.getInstance()
                    .getUser().getAccount());
            PendingIntent pending = PendingIntent.getBroadcast(context, MyApplication
                            .REQUEST_REPORT_CHECK_ALARM,
                    alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            if (alarm != null && pending != null) {
                Log.e(context.getClass().getSimpleName(), "start cancel");
                alarm.cancel(pending);
            } else {
                Log.e(context.getClass().getSimpleName(), "cancel fail");
            }
        } catch (Exception e) {
            Log.e(context.getClass().getSimpleName(), "Exception:" + e.getMessage());
        }
    }

    /**
     * 設定預約鬧鐘
     */
    public static void setReserveAlarm(Context context, Trip trip) {
        try {
            AlarmManager alarm = (AlarmManager) context.getSystemService(Context
                    .ALARM_SERVICE);
            Intent alarmIntent = new Intent();
            alarmIntent.setAction(MyApplication.ACTION_RESERVE_ALARM);
            Gson gson = new GsonBuilder().setDateFormat(TimeFormater.DATABASE_DATE_TIME_STRING)
                    .create();
            alarmIntent.putExtra(MyApplication.INTENT_KEY_TRIP, gson.toJson(trip));
            PendingIntent pending = PendingIntent.getBroadcast(context, trip
                            .getClock_id(),
                    alarmIntent, PendingIntent.FLAG_ONE_SHOT);
            setAlarm(context, trip.getFrom_date().getTime() - trip
                    .getNotification(), pending);

            Log.e(context.getClass().getSimpleName(), "setReserveAlarm:" + trip
                    .getClock_id() + ":" +
                    TimeFormater.getInstance().toDateTimeFormat(trip.getFrom_date()) + "-" + trip
                    .getNotification());
        } catch (Exception e) {
            Log.e(context.getClass().getSimpleName(), "Exception:" + e.getMessage());
        }
    }

    /**
     * 取消預約鬧鐘
     */
    public static void cancelReserveAlarm(Context context, Trip trip) {
        if (trip.getClock_id() == 0)
            return;
        try {
            Log.e(context.getClass().getSimpleName(), "cancelReserveAlarm:" + trip.getClock_id());
            AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent alarmIntent = new Intent();
            alarmIntent.setAction(MyApplication.ACTION_RESERVE_ALARM);
            PendingIntent pending = PendingIntent.getBroadcast(context, trip.getClock_id(),
                    alarmIntent, PendingIntent
                            .FLAG_ONE_SHOT);
            if (alarm != null && pending != null) {
                Log.e(context.getClass().getSimpleName(), "start cancel");
                alarm.cancel(pending);
            } else {
                Log.e(context.getClass().getSimpleName(), "cancel fail");
            }
        } catch (Exception e) {
            Log.e(context.getClass().getSimpleName(), "Exception:" + e.getMessage());
        }
    }
}
