package tw.com.masterhand.gmorscrm.tools;

import android.util.Log;

import tw.com.masterhand.gmorscrm.MyApplication;

public class Logger {
    public static void e(String tag, String msg) {
        if (MyApplication.isDebug) {
//            int maxLogSize = 500;
//            for (int i = 0; i <= msg.length() / maxLogSize; i++) {
//                int start = i * maxLogSize;
//                int end = (i + 1) * maxLogSize;
//                end = end > msg.length() ? msg.length() : end;
//                Log.e(tag, msg.substring(start, end));
//            }
            Log.e(tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (MyApplication.isDebug)
            Log.i(tag, msg);
    }
}
