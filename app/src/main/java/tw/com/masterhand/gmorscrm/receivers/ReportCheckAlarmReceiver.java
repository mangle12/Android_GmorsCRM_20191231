package tw.com.masterhand.gmorscrm.receivers;


import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.record.Trip;
import tw.com.masterhand.gmorscrm.tools.AlarmHelper;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.tools.TimeFormater;

public class ReportCheckAlarmReceiver extends BaseReceiver {

    private Disposable disposable;

    @Override
    public void onReceive(final Context context, Intent intent) {
        String action = intent.getAction();
        Logger.e(TAG, "received action = " + action);
        if (action == null || !action.equals(MyApplication.ACTION_REPORT_CHECK_ALARM))
            return;
        String userAccount = intent.getStringExtra(MyApplication.INTENT_KEY_USER);
        String userId = intent.getStringExtra(MyApplication.INTENT_KEY_ID);
        String databaseName = DatabaseHelper.getInstance(context).getDatabaseName();
        if (TextUtils.isEmpty(databaseName) || !databaseName.equals(userAccount)) {
            return;
        }
        disposable = DatabaseHelper.getInstance(context).getVisitNotReport(userId).subscribe(new Consumer<List<Trip>>() {
            @Override
            public void accept(List<Trip> trips) throws Exception {
                StringBuilder msg = new StringBuilder();
                for (Trip trip : trips) {
                    msg.append(TimeFormater.getInstance().toDateFormat(trip.getFrom_date()))
                            .append("「")
                            .append(trip.getName())
                            .append("」")
                            .append(context.getString(R.string.error_msg_no_report))
                            .append("\n");
                }
                sendNotification(context, "Report Check", MyApplication
                        .REQUEST_REPORT_CHECK_ALARM, msg.toString());
                AlarmHelper.setReportCheckAlarm(context.getApplicationContext());
                if (disposable != null) {
                    disposable.dispose();
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                ErrorHandler.getInstance().setException(context, throwable);
            }
        });
    }


}
