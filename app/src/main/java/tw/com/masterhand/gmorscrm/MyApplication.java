package tw.com.masterhand.gmorscrm;

import android.Manifest;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.multidex.MultiDex;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.xiaomi.mipush.sdk.MiPushClient;

import java.util.List;

public class MyApplication extends Application {
    public static final String TAG = "MyApplication";
    public static final boolean isDebug = BuildConfig.IS_DEBUG;
    public static final int DIALOG_STYLE = R.style.DialogStyle;
    public static final int LOCKER_MIN_SIZE = 4;
    // REQUEST
    public static final int REQUEST_PLAY_SERVICE = 100,
            REQUEST_LOGIN = 101,
            REQUEST_LOCK = 102,
            REQUEST_MENU_SETTING = 300,
            REQUEST_LOCK_RESET = 301,
            REQUEST_LOCK_CHECK = 302,
            REQUEST_CANCEL_TASK = 303,
            REQUEST_DELAY_TASK = 304,
            REQUEST_SELECT_CUSTOMER = 400,
            REQUEST_SELECT_PROJECT = 401,
            REQUEST_SELECT_USER = 403,
            REQUEST_SELECT_CONTACTER = 404,
            REQUEST_SELECT_PARTICIPANT = 405,
            REQUEST_SELECT_PHONE_TYPE = 406,
            REQUEST_SELECT_TARGET = 407,
            REQUEST_SELECT_FILE = 408,
            REQUEST_SELECT_PERIOD = 409,
            REQUEST_SELECT_EXP = 410,
            REQUEST_SELECT_RECEIVER = 411,
            REQUEST_SELECT_COUNTRY = 412,
            REQUEST_SELECT_CITY = 413,
            REQUEST_EDIT_EXP = 500,
            REQUEST_EDIT_PRODUCT = 501,
            REQUEST_EDIT = 502,
            REQUEST_ADD_EXP = 503,
            REQUEST_QUOTATION_TO_CONTRACT = 504,
            REQUEST_ADD_CONTACT = 600,
            REQUEST_ADD_CONVERSATION = 601,
            REQUEST_ADD_PROJECT = 602,
            REQUEST_ADD_OPPORTUNITY = 603,
            REQUEST_ADD_WORK = 604;
    // INTENT
    public static final String INTENT_KEY_KEYWORD = "keyword",
            INTENT_KEY_CUSTOMER = "customer",
            INTENT_KEY_CUSTOMER_CHECK = "customer_check",
            INTENT_KEY_PEOPLE = "people",
            INTENT_KEY_ENABLE = "enable",
            INTENT_KEY_TYPE = "type",
            INTENT_KEY_ID = "id",
            INTENT_KEY_DEPARTMENT = "department",
            INTENT_KEY_COMPANY = "company",
            INTENT_KEY_CATEGORY = "category",
            INTENT_KEY_LIST = "list",
            INTENT_KEY_MODE = "mode",
            INTENT_KEY_REIMBURSEMENT = "exp",
            INTENT_KEY_REIMBURSEMENT_ITEM = "exp_item",
            INTENT_KEY_REIMBURSEMENT_CONFIG = "exp_config",
            INTENT_KEY_PROJECT = "project",
            INTENT_KEY_PARENT = "parent",
            INTENT_KEY_TRIP = "trip",
            INTENT_KEY_QUOTATION = "quotation",
            INTENT_KEY_PHONE_TYPE = "phone_type",
            INTENT_KEY_PRODUCT = "product",
            INTENT_KEY_CONVERSATION = "conversation",
            INTENT_KEY_TARGET = "target",
            INTENT_KEY_REASON = "reason",
            INTENT_KEY_APPROVAL = "approval",
            INTENT_KEY_APPROVER = "approver",
            INTENT_KEY_TOKEN = "token",
            INTENT_KEY_DATE = "date",
            INTENT_KEY_CONTACT = "contact",
            INTENT_KEY_SAMPLE = "sample",
            INTENT_KEY_IMAGE_URL = "image_url",
            INTENT_KEY_FILE_URL = "file_url",
            INTENT_KEY_IMAGE = "image",
            INTENT_KEY_VIEWER = "viewer",
            INTENT_KEY_DATE_START = "start",
            INTENT_KEY_DATE_END = "end",
            INTENT_KEY_USER = "user",
            INTENT_KEY_GUIDE = "guide",
            INTENT_KEY_STATISTIC_MENU = "statistic_menu";
    public final static String ACTION_RESERVE_ALARM = "tw.com.masterhand.gmorscrm.receivers" +
            ".ReserveAlarmReceiver", ACTION_REPORT_CHECK_ALARM = "tw.com.masterhand.gmorscrm" +
            ".receivers.ReportCheckAlarmReceiver";
    public final static int REQUEST_REPORT_CHECK_ALARM = 55;

    public static final String GOOGLE_KEY = "AIzaSyCIC4GC0M_bcg6DWS4_jqdhf2lVW37MpEY";

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("MyApplication", "isDebug:" + isDebug);
        if (shouldInit()) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) ==
                    PackageManager.PERMISSION_GRANTED)
                MiPushClient.registerPush(this, Constants.APP_ID, Constants.APP_KEY);
        }
        /*Android-Universal-Image-Loader*/
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100)
                .build();
        ImageLoader.getInstance().init(config);
    }

    private boolean shouldInit() {
        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        if (am == null)
            return false;
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getPackageName();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }
}
