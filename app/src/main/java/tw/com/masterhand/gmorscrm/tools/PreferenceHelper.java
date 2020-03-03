package tw.com.masterhand.gmorscrm.tools;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.enums.MainMenu;
import tw.com.masterhand.gmorscrm.room.setting.User;

public class PreferenceHelper {
    private final String TAG = getClass().getSimpleName();
    final byte[] AES_KEY = {0x44, 0x25, 0x7e, 0x52, 0x77, 0x63, 0x6d, 0x26, 0x6c,
            0x56, 0x40, 0x70, 0x67, 0x40, 0x32, 0x21,
            0x62, 0x47, 0x38, 0x7d, 0x67, 0x4d, 0x31, 0x26, 0x63, 0x43, 0x32, 0x5e, 0x67, 0x45,
            0x32, 0x5b};
    final byte[] AES_IV = {0x6e, 0x42, 0x31, 0x24, 0x69, 0x4f, 0x31, 0x2c, 0x69,
            0x59, 0x31, 0x7b, 0x68, 0x51, 0x32, 0x25};
    private final String PREFERENCE_SETTING = "preference_setting";
    // key
    private final String SETTING_ACCOUNT = "account", SETTING_PASSWORD = "password",
            SETTING_TOKEN = "token", SETTING_USER = "user",
            SETTING_LOCK = "lock", SETTING_REMEMBER = "remember", SETTING_MENU_SETTING = "menu_setting",
            SETTING_THEME_COLOR = "theme_color", SETTING_LAST_SYNC = "last_sync";
    // preference
    private SharedPreferences settingPreferences;
    private Gson gson;

    public PreferenceHelper(Context context) {
        settingPreferences = context.getSharedPreferences(PREFERENCE_SETTING, Activity.MODE_PRIVATE);
        gson = new GsonBuilder().setDateFormat(TimeFormater.DATABASE_DATE_TIME_STRING).create();
    }

    public void clearLastSync() {
        settingPreferences.edit().remove(SETTING_LAST_SYNC).apply();
    }

    public void saveLastSync(Date date, String userId) {
//        Logger.e(TAG, "saveLastSync:" + TimeFormater.getInstance().toDatabaseFormat
//                (date));
        settingPreferences.edit().putString(SETTING_LAST_SYNC + userId, TimeFormater.getInstance().toDatabaseFormat(date)).apply();
    }

    public void resetLastSync(Date date, String userId) {
//        Logger.e(TAG, "saveLastSync:" + TimeFormater.getInstance().toDatabaseFormat
//                (date));
        if (date.compareTo(getLastSync(userId)) < 0)
            settingPreferences.edit().putString(SETTING_LAST_SYNC + userId, TimeFormater.getInstance().toDatabaseFormat(date)).apply();
    }

    public Date getLastSync(String userId) {
        String lastSync = settingPreferences.getString(SETTING_LAST_SYNC + userId, "");
        if (TextUtils.isEmpty(lastSync)) {
            Calendar lastUpdateDate = Calendar.getInstance(Locale.getDefault());
            lastUpdateDate.set(1971, 1, 1, 0, 0, 0);
            return lastUpdateDate.getTime();
        } else {
            try {
                return TimeFormater.getInstance().fromDatabaseFormat(lastSync);
            } catch (ParseException e) {
                Calendar lastUpdateDate = Calendar.getInstance(Locale.getDefault());
                lastUpdateDate.set(1971, 1, 1, 0, 0, 0);
                return lastUpdateDate.getTime();
            }
        }
    }

    public boolean saveUser(String account, String password, String token, User user) {
        try {
            return settingPreferences.edit().putString(SETTING_ACCOUNT, account)
                    .putString(SETTING_PASSWORD, AESUtils.encrypt(password.getBytes("UTF-8"), AES_KEY, AES_IV))
                    .putString(SETTING_TOKEN, token)
                    .putString(SETTING_USER, gson.toJson(user))
                    .commit();
        } catch (UnsupportedEncodingException e) {
            Logger.e(TAG, "UnsupportedEncodingException:" + e.getMessage());
            return false;
        }
    }

    public void savePassword(String password) {
        try {
            settingPreferences.edit().putString(SETTING_PASSWORD, AESUtils.encrypt(password.getBytes("UTF-8"), AES_KEY, AES_IV)).apply();
        } catch (UnsupportedEncodingException e) {
            Logger.e(TAG, "UnsupportedEncodingException:" + e.getMessage());
        }
    }

    public User getUser(String account, String password) {
        String saveAccount = getAccount();
        String savePassword = getPassword();
        if (account.equals(saveAccount) && password.equals(savePassword)) {
            String saveUser = settingPreferences.getString(SETTING_USER, "");
            if (TextUtils.isEmpty(saveUser))
                return null;
            return gson.fromJson(saveUser, User.class);
        } else {
            return null;
        }
    }

    /**
     * 取得TOKEN
     */
    public String getToken() {
        return settingPreferences.getString(SETTING_TOKEN, "");
    }

    /**
     * 取得帳號
     */
    public String getAccount() {
        return settingPreferences.getString(SETTING_ACCOUNT, "");
    }

    /**
     * 取得密碼
     */
    public String getPassword() {
        String encryptPassword = settingPreferences.getString(SETTING_PASSWORD, "");
        if (TextUtils.isEmpty(encryptPassword))
        {
            return "";
        }
        else{
            try
            {
                return new String(AESUtils.decrypt(encryptPassword, AES_KEY, AES_IV), "UTF-8");
            } catch (Exception e) {
                return "";
            }
        }
    }

    /**
     * 儲存手勢鎖
     *
     * @param userId  使用者ID
     * @param pattern 手勢鎖pattern
     */
    public void saveLock(String userId, String pattern) {
        settingPreferences.edit().putString(userId, pattern).putBoolean(SETTING_LOCK, true).apply();
    }

    /**
     * 手勢鎖是否啟用
     */
    public boolean isLockEnable() {
        return settingPreferences.getBoolean(SETTING_LOCK, false);
    }

    /**
     * 啟用或關閉手勢鎖
     */
    public void enableLock(boolean isEnable) {
        settingPreferences.edit().putBoolean(SETTING_LOCK, isEnable).apply();
    }

    /**
     * 取得手勢鎖
     *
     * @param userId 使用者ID
     * @return 手勢鎖
     */
    public String getLock(String userId) {
        return settingPreferences.getString(userId, null);
    }

    /**
     * 儲存主題顏色
     *
     * @param color rid
     */
    public void saveThemeColor(int color) {
        settingPreferences.edit().putInt(SETTING_THEME_COLOR, color).apply();
    }

    /**
     * 取得主題顏色
     *
     * @return color rid
     */
    public int getThemeColor() {
        return settingPreferences.getInt(SETTING_THEME_COLOR, Color.parseColor("#ffcf42"));
    }

    /**
     * 儲存是否記住帳密
     */
    public void saveRemember(boolean isRemember) {
        settingPreferences.edit().putBoolean(SETTING_REMEMBER, isRemember).apply();
    }

    /**
     * 是否記住帳密
     */
    public boolean isRemember() {
        return settingPreferences.getBoolean(SETTING_REMEMBER, true);
    }

    /**
     * 儲存主選單設定
     */
    public void saveMenuSetting(ArrayList<MainMenu> menus) {
        JSONArray addArray = new JSONArray();
        for (MainMenu menu : MainMenu.values())
        {
            if (menu.isFixed()) {
                addArray.put(menu.getValue());
            } else {
                if (menus.contains(menu)) {
                    addArray.put(menu.getValue());
                }
            }
        }
        settingPreferences.edit().putString(SETTING_MENU_SETTING, addArray.toString()).apply();
    }

    /**
     * 取得主選單設定
     */
    public ArrayList<MainMenu> getMenuSetting() {
        ArrayList<MainMenu> addList = new ArrayList<>();
        String setting = settingPreferences.getString(SETTING_MENU_SETTING, "");

        try {
            if (!TextUtils.isEmpty(setting))
            {
                JSONArray addArray = new JSONArray(setting);

                for (int i = 0; i < addArray.length(); i++) {
                    int value = addArray.getInt(i);
                    MainMenu menu = MainMenu.getMainMenuByValue(value);
                    if (menu != null)
                    {
                        addList.add(menu);
                    }
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "JSONException:" + e.getMessage());
        }
        if (addList.size() == 0) {
            for (MainMenu menu : MainMenu.values()) {
                if (menu.isFixed()) {
                    addList.add(menu);
                }
            }
        }
        return addList;
    }
}
