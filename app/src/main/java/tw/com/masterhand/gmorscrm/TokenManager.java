package tw.com.masterhand.gmorscrm;

import android.text.TextUtils;
import android.util.Log;

import tw.com.masterhand.gmorscrm.room.setting.User;
import tw.com.masterhand.gmorscrm.tools.Logger;

public class TokenManager {
    private static TokenManager singleton;

    private String token;
    // 使用者資料
    private User user;

    private long lastLeavingTime = 0;

    private TokenManager() {
    }

    public static TokenManager getInstance() {
        if (singleton == null)
            singleton = new TokenManager();
        return singleton;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public String getToken() {
        return token;
    }

    public void setLastLeavingTime(long time) {
        lastLeavingTime = time;
    }

    public boolean checkLastLeaving(long time) {
//        Logger.e(getClass().getSimpleName(), "time:" + time);
//        Logger.e(getClass().getSimpleName(), "lastLeavingTime:" + lastLeavingTime);
        boolean isInApp = true;
        if (time - lastLeavingTime > 10000) {
            Logger.e(getClass().getSimpleName(), "checkLastLeaving:false");
            isInApp = false;
        }
        setLastLeavingTime(time);
        return isInApp;
    }

    /**
     * 檢查token是否有效
     */
    public boolean checkToken() {
        boolean isSuccess = true;
        if (TextUtils.isEmpty(token)) {
            isSuccess = false;
            Logger.e(getClass().getSimpleName(), "token is empty.");
        }
        if (user == null) {
            isSuccess = false;
            Logger.e(getClass().getSimpleName(), "user is null.");
        }
        return isSuccess;
    }

    /**
     * 清除token
     */
    public void clearUserToken() {
        user = null;
        token = null;
    }
}
