package tw.com.masterhand.gmorscrm.receivers;


import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;

import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.room.record.Trip;
import tw.com.masterhand.gmorscrm.tools.TimeFormater;

public class ReserveAlarmReceiver extends BaseReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        String tripString = intent.getStringExtra(MyApplication.INTENT_KEY_TRIP);
        if (TextUtils.isEmpty(tripString)) {
            Log.e(TAG, "data is null");
            return;
        }
        if (action == null || !action.equals(MyApplication.ACTION_RESERVE_ALARM))
            return;
        Gson gson = new GsonBuilder().setDateFormat(TimeFormater.DATABASE_DATE_TIME_STRING)
                .create();
        Trip trip = gson.fromJson(tripString, Trip.class);
        Log.e(TAG, "received action = " + action + ", trip = " + tripString);
        String msg = "[" + context.getString(trip.getType().getTitle()) + "]" + trip.getName() +
                context.getString(R.string.alert_msg1) +
                TimeFormater.getInstance().toDateTimeFormat(trip.getFrom_date()) + context
                .getString(R.string
                .alert_msg2);
        Date now = new Date();
        long diff = trip.getFrom_date().getTime() - now.getTime();
        if (diff > 0) {
            int minute = (int) diff / 1000 / 60;
            if (minute < 60) {
                msg += String.valueOf(minute) + context.getString(R.string.unit_minute);
            } else {
                int hour = minute / 60;
                minute = minute % 60;
                if (hour < 24) {
                    msg += String.valueOf(hour) + context.getString(R.string.unit_hour) + String
                            .valueOf(minute) + context.getString(R.string.unit_minute);
                } else {
                    int day = hour / 24;
                    hour = hour % 24;
                    msg += String.valueOf(day) + context.getString(R.string.unit_day) + String
                            .valueOf(hour) + context.getString(R.string.unit_hour) + String
                            .valueOf(minute) + context.getString(R.string.unit_minute);
                }
            }
        } else {
            msg += "0" + context.getString(R.string.unit_minute);
        }
        sendNotification(context, "Reservation", trip.getNotification(), msg);
    }

}
