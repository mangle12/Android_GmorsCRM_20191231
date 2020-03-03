package tw.com.masterhand.gmorscrm.receivers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;

import com.xiaomi.mipush.sdk.ErrorCode;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.xiaomi.mipush.sdk.MiPushCommandMessage;
import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.PushMessageReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import tw.com.masterhand.gmorscrm.MainActivity;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.TokenManager;
import tw.com.masterhand.gmorscrm.activity.approval.ApprovalMenuActivity;
import tw.com.masterhand.gmorscrm.activity.news.NewsDetailActivity;
import tw.com.masterhand.gmorscrm.activity.task.AbsentDetailActivity;
import tw.com.masterhand.gmorscrm.activity.task.ContractDetailActivity;
import tw.com.masterhand.gmorscrm.activity.task.ExpressDetailActivity;
import tw.com.masterhand.gmorscrm.activity.task.NonStandardInquiryDetailActivity;
import tw.com.masterhand.gmorscrm.activity.task.OfficeDetailActivity;
import tw.com.masterhand.gmorscrm.activity.task.ProductionDetailActivity;
import tw.com.masterhand.gmorscrm.activity.task.QuotationDetailActivity;
import tw.com.masterhand.gmorscrm.activity.task.ReimbursementDetailActivity;
import tw.com.masterhand.gmorscrm.activity.task.RepaymentDetailActivity;
import tw.com.masterhand.gmorscrm.activity.task.SampleDetailActivity;
import tw.com.masterhand.gmorscrm.activity.task.SpecialPriceDetailActivity;
import tw.com.masterhand.gmorscrm.activity.task.SpecialShipDetailActivity;
import tw.com.masterhand.gmorscrm.activity.task.SpringRingInquiryDetailActivity;
import tw.com.masterhand.gmorscrm.activity.task.TaskDetailActivity;
import tw.com.masterhand.gmorscrm.activity.task.TravelDetailActivity;
import tw.com.masterhand.gmorscrm.activity.task.VisitDetailActivity;
import tw.com.masterhand.gmorscrm.enums.NewsType;
import tw.com.masterhand.gmorscrm.enums.NotificationType;
import tw.com.masterhand.gmorscrm.enums.TripType;
import tw.com.masterhand.gmorscrm.tools.Logger;

public class MessageReceiver extends PushMessageReceiver {
    final String TAG = getClass().getSimpleName();
    private String mRegId;
    private long mResultCode = -1;
    private String mReason;
    private String mCommand;
    private String mMessage;
    private String mTopic;
    private String mAlias;
    private String mUserAccount;
    private String mStartTime;
    private String mEndTime;

    @Override
    public void onReceivePassThroughMessage(Context context, MiPushMessage message) {
        mMessage = message.getContent();
        if (!TextUtils.isEmpty(message.getTopic())) {
            mTopic = message.getTopic();
        } else if (!TextUtils.isEmpty(message.getAlias())) {
            mAlias = message.getAlias();
        } else if (!TextUtils.isEmpty(message.getUserAccount())) {
            mUserAccount = message.getUserAccount();
        }
        Logger.e(TAG, "onReceivePassThroughMessage:" + mMessage);
    }

    @Override
    public void onNotificationMessageClicked(Context context, MiPushMessage message) {
        mMessage = message.getContent();
        if (!TextUtils.isEmpty(message.getTopic())) {
            mTopic = message.getTopic();
        } else if (!TextUtils.isEmpty(message.getAlias())) {
            mAlias = message.getAlias();
        } else if (!TextUtils.isEmpty(message.getUserAccount())) {
            mUserAccount = message.getUserAccount();
        }
        Logger.e(TAG, "onNotificationMessageClicked:" + mMessage);
        Intent intent = getNotificationIntent(context, message);
        if (intent != null)
            context.startActivity(intent);
    }

    Intent getNotificationIntent(Context context, MiPushMessage message) {
        Intent intent = null;
        try {
            JSONObject result = new JSONObject(message.getContent());
            NotificationType type = NotificationType.getTypeByValue(result.getInt("type"));
            String tripId = null;
            if (result.has("trip_id"))
                tripId = result.getString("trip_id");
            TripType tripType = null;
            if (result.has("trip_type"))
                tripType = TripType.getTripTypeByValue(result.getInt("trip_type"));
            if (type == null) {
                Logger.e(TAG, "type is null");
                return null;
            }
            switch (type) {
                case PARTICIPANT:
                case CONVERSATION:
                case INVITATION:
                    if (tripType == null) {
                        Logger.e(TAG, "tripType is null");
                        return null;
                    }
                    switch (tripType) {
                        case VISIT:
                            intent = new Intent(context, VisitDetailActivity.class);
                            break;
                        case OFFICE:
                            intent = new Intent(context, OfficeDetailActivity.class);
                            break;
                        case TASK:
                            intent = new Intent(context, TaskDetailActivity.class);
                            break;
                        case ABSENT:
                            intent = new Intent(context, AbsentDetailActivity.class);
                            break;
                        case SAMPLE:
                            intent = new Intent(context, SampleDetailActivity.class);
                            break;
                        case REIMBURSEMENT:
                            intent = new Intent(context, ReimbursementDetailActivity.class);
                            break;
                        case CONTRACT:
                            intent = new Intent(context, ContractDetailActivity.class);
                            break;
                        case QUOTATION:
                            intent = new Intent(context, QuotationDetailActivity.class);
                            break;
                        case SPECIAL_PRICE:
                            intent = new Intent(context, SpecialPriceDetailActivity.class);
                            break;
                        case PRODUCTION:
                            intent = new Intent(context, ProductionDetailActivity.class);
                            break;
                        case NON_STANDARD_INQUIRY:
                            intent = new Intent(context, NonStandardInquiryDetailActivity.class);
                            break;
                        case SPRING_RING_INQUIRY:
                            intent = new Intent(context, SpringRingInquiryDetailActivity.class);
                            break;
                        case SPECIAL_SHIP:
                            intent = new Intent(context, SpecialShipDetailActivity.class);
                            break;
                        case REPAYMENT:
                            intent = new Intent(context, RepaymentDetailActivity.class);
                            break;
                        case EXPRESS:
                            intent = new Intent(context, ExpressDetailActivity.class);
                            break;
                        case TRAVEL:
                            intent = new Intent(context, TravelDetailActivity.class);
                            break;
                    }
                    intent.putExtra(MyApplication.INTENT_KEY_TRIP, tripId);
                    break;
                case APPROVAL:
                    intent = new Intent(context, ApprovalMenuActivity.class);
                    intent.putExtra(MyApplication.INTENT_KEY_MODE, true);
                    intent.putExtra(MyApplication.INTENT_KEY_TYPE, tripType);
                    break;
                case ANNOUNCE:
                    String announceId = result.getString("announce_id");
                    intent = new Intent(context, NewsDetailActivity.class);
                    intent.putExtra(MyApplication.INTENT_KEY_TYPE, NewsType.ANNOUNCE.getValue());
                    intent.putExtra(MyApplication.INTENT_KEY_ID, announceId);
                    break;
                case APPROVE:
                    intent = new Intent(context, ApprovalMenuActivity.class);
                    intent.putExtra(MyApplication.INTENT_KEY_MODE, false);
                    break;
            }
        } catch (JSONException e) {
            Logger.e(TAG, "JSONException:" + e.getMessage());
        }
        return intent;
    }

    @Override
    public void onNotificationMessageArrived(Context context, MiPushMessage message) {
        if (TextUtils.isEmpty(message.getDescription())) {
            MiPushClient.clearNotification(context, message.getNotifyId());
            return;
        }
        mMessage = message.getContent();
        if (!TextUtils.isEmpty(message.getTopic())) {
            mTopic = message.getTopic();
            Logger.e(TAG, "onNotificationMessageArrived Topic:" + mTopic);
        } else if (!TextUtils.isEmpty(message.getAlias())) {
            mAlias = message.getAlias();
        } else if (!TextUtils.isEmpty(message.getUserAccount())) {
            mUserAccount = message.getUserAccount();
        }
        Logger.e(TAG, "onNotificationMessageArrived mUserAccount:" + mUserAccount);
        Logger.e(TAG, "onNotificationMessageArrived Title:" + message.getTitle());
        Logger.e(TAG, "onNotificationMessageArrived Description:" + message.getDescription());
        Logger.e(TAG, "onNotificationMessageArrived Content:" + mMessage);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent resultIntent = getNotificationIntent(context, message);
            if (resultIntent != null) {
                NotificationManager mNotificationManager = (NotificationManager) context
                        .getSystemService(Context.NOTIFICATION_SERVICE);
                if (mNotificationManager != null) {
                    String channelId = "GMORS";
                    NotificationChannel channel = new NotificationChannel(
                            channelId,
                            context.getString(R.string.app_name),
                            NotificationManager.IMPORTANCE_HIGH);
                    channel.setDescription(context.getString(R.string.app_name));
                    channel.enableLights(true);
                    channel.enableVibration(true);
                    mNotificationManager.createNotificationChannel(channel);
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                    stackBuilder.addParentStack(MainActivity.class);
                    stackBuilder.addNextIntent(resultIntent);
                    PendingIntent resultPendingIntent =
                            stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(context, message.getMessageId())
                                    .setContentTitle(message.getTitle())
                                    .setChannelId(channelId)
                                    .setContentText(message.getDescription())
                                    .setContentIntent(resultPendingIntent);
                    mNotificationManager.notify(message.getNotifyId(), mBuilder.build());
                } else
                    Logger.e(TAG, "mNotificationManager is null");
            } else {
                Logger.e(TAG, "intent is null");
            }
        }
    }

    @Override
    public void onCommandResult(Context context, MiPushCommandMessage message) {
        String command = message.getCommand();
        List<String> arguments = message.getCommandArguments();
        String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);
        String cmdArg2 = ((arguments != null && arguments.size() > 1) ? arguments.get(1) : null);
        if (MiPushClient.COMMAND_REGISTER.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mRegId = cmdArg1;
            }
        } else if (MiPushClient.COMMAND_SET_ACCOUNT.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mUserAccount = cmdArg1;
                Logger.e(TAG, "set user account:" + mUserAccount);
            } else {
                Logger.e(TAG, "set user account error:" + message.getReason());
            }
        } else if (MiPushClient.COMMAND_SET_ALIAS.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mAlias = cmdArg1;
            }
        } else if (MiPushClient.COMMAND_UNSET_ALIAS.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mAlias = cmdArg1;
            }
        } else if (MiPushClient.COMMAND_SUBSCRIBE_TOPIC.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mTopic = cmdArg1;
            }
        } else if (MiPushClient.COMMAND_UNSUBSCRIBE_TOPIC.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mTopic = cmdArg1;
            }
        } else if (MiPushClient.COMMAND_SET_ACCEPT_TIME.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mStartTime = cmdArg1;
                mEndTime = cmdArg2;
            }
        }
    }

    @Override
    public void onReceiveRegisterResult(Context context, MiPushCommandMessage message) {
        String command = message.getCommand();
        List<String> arguments = message.getCommandArguments();
        String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);
        String cmdArg2 = ((arguments != null && arguments.size() > 1) ? arguments.get(1) : null);
        if (MiPushClient.COMMAND_REGISTER.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mRegId = cmdArg1;
                Logger.e(TAG, "mRegId:" + mRegId);
                if (TokenManager.getInstance().getUser() != null)
                    MiPushClient.setUserAccount(context, TokenManager.getInstance().getUser()
                            .getAccount(), null);
            } else {
                Logger.e(TAG, "Register error code:" + message.getResultCode());
                Logger.e(TAG, "Register error reason:" + message.getReason());
            }
        }
    }
}
