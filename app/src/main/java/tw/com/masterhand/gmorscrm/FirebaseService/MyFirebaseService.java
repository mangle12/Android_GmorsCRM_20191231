package tw.com.masterhand.gmorscrm.FirebaseService;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import tw.com.masterhand.gmorscrm.MainActivity;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.activity.task.CreateActivity;

public class MyFirebaseService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        /*Notification推播訊息*/
        if (remoteMessage.getNotification() != null) {
            Log.i("MyFirebaseService","title "+remoteMessage.getNotification().getTitle());
            Log.i("MyFirebaseService","body "+remoteMessage.getNotification().getBody());
            sendNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
        }
        else/*data推播訊息*/
        {
            sendNotification("背景推播標題", "背景推播內容");
        }

        //取得推播傳入的值(msg)
        final Bundle bundle = remoteMessage.toIntent().getExtras();
        if (bundle != null) {
            for (String key : bundle.keySet()) {
                if(key.equals("msg"))
                {
                    Object value = bundle.get(key);
                    if (value == null) {
                        continue;
                    }
                    Log.i("MyFirebaseService", String.format("key:%s value:%s valueClass:(%s)", key, value.toString(), value.getClass().getName()));
                }
            }
        }
    }

    /*紀錄token*/
    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.i("MyFirebaseService","token "+s);
    }

    /*手機上顯示通知*/
    private void sendNotification(String messageTitle,String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);//開啟通知後導向哪個頁面
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        String channelId = "default_notification_channel_id";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setContentTitle(messageTitle)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Channel human readable title", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0, notificationBuilder.build());
    }
}

