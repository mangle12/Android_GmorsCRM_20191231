package tw.com.masterhand.gmorscrm.tools;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;


public class Checker {
    /**
     * 檢查網路連線
     */
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * 檢查定位是否開啟
     */
    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(
                        context.getContentResolver(),
                        Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                ErrorHandler.getInstance().setException(context, e);
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        } else {
            locationProviders = Settings.Secure.getString(
                    context.getContentResolver(),
                    Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }

    }

    /**
     * 檢查密碼是否為英數組合
     */
    public static boolean checkPassword(String password) {
        char[] temC = password.toCharArray();
        int number = 0, letter = 0;
        for (int i = 0; i < temC.length; i++) {
            // 數字
            if (temC[i] >= 48 && temC[i] <= 57) {
                number++;
            }
            // 大寫字母
            if (temC[i] >= 65 && temC[i] <= 90) {
                letter++;
            }
            // 小寫字母
            if (temC[i] >= 97 && temC[i] <= 122) {
                letter++;
            }
        }
        return (number != 0 && letter != 0) ? true : false;

    }

    /**
     * 檢查E-mail格式
     *
     * @return 是否為mail
     */
    public static boolean isValidEmail(CharSequence mail) {
        return !TextUtils.isEmpty(mail) && android.util.Patterns.EMAIL_ADDRESS.matcher(mail)
                .matches();
    }
}
