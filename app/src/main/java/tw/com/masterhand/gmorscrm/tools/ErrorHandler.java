package tw.com.masterhand.gmorscrm.tools;

import android.content.Context;

import org.json.JSONException;

public class ErrorHandler {
    private String TAG = getClass().getSimpleName();
    public final static ErrorHandler mInstance = new ErrorHandler();

    public ErrorHandler() {
    }

    public static ErrorHandler getInstance() {
        return mInstance;
    }

    /**
     * 設定要處理的例外事件
     *
     * @param e JSONException
     */
    public void setException(Context context, Exception e) {
        Logger.e(TAG, "Exception from:" + context.getClass().getSimpleName());
        if (e instanceof JSONException) {
            JSONException jsonException = (JSONException) e;
            Logger.e(TAG, "JSONException:" + jsonException.getMessage());
        } else {
            Logger.e(TAG, "Exception:" + e.getMessage());
        }
    }

    public void setException(Context context, Throwable e) {
//        Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        Logger.e(TAG, "Exception from:" + context.getClass().getSimpleName());
        Logger.e(TAG, "Throwable:" + e.getClass().getSimpleName() + "-" + e.getMessage());
    }
}
