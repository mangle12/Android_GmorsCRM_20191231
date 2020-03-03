package tw.com.masterhand.gmorscrm;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;

import org.json.JSONObject;

import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tw.com.masterhand.gmorscrm.activity.setting.LockConfirmActivity;
import tw.com.masterhand.gmorscrm.api.ApiHelper;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.tools.Logger;

public abstract class BaseUserCheckActivity extends BaseActivity {

    Dialog loginDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!TokenManager.getInstance().checkToken()) {
            Logger.e(TAG, "onResume:token is null");
            showLoginDialog();
            return;
        } else {
            hideLoginDialog();
            onUserChecked();
        }
        Logger.i(TAG, "user id:" + TokenManager.getInstance().getUser().getId());
        Logger.i(TAG, "user name:" + TokenManager.getInstance().getUser().getShowName());
        checkLastLeaving();
    }

    protected abstract void onUserChecked();

    private boolean checkLastLeaving() {
        if (TokenManager.getInstance().checkToken()) {
            if (!TokenManager.getInstance().checkLastLeaving(new Date().getTime())) {
                /*離開app太久，檢查手勢鎖是否啟用*/
                if (preferenceHelper.isLockEnable() && !TextUtils.isEmpty(preferenceHelper
                        .getLock(TokenManager.getInstance().getUser().getId()))) {
                    goUnlock();
                    return false;
                }
            }
        }
        return true;
    }



    /**
     * 顯示重新登入對話框
     */
    public void showLoginDialog() {
        hideLoginDialog();
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.app_name).setMessage(R.string.error_msg_token_expired)
                    .setCancelable(false).setPositiveButton(R.string
                    .confirm, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    reLogin();
                }
            });
            loginDialog = builder.create();
            loginDialog.show();
        } catch (Exception e) {
            Logger.e(TAG, "Exception:" + e.getMessage());
        }
    }

    public void hideLoginDialog() {
        if (loginDialog != null) {
            if (loginDialog.isShowing())
                loginDialog.dismiss();
            loginDialog = null;
        }
    }

    /**
     * 跳轉至登入頁重新登入
     */
    public void reLogin() {
        Logger.e(TAG, "reLogin");
        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, MyApplication.REQUEST_LOGIN);
    }

    public void goUnlock() {
        Intent intent = new Intent(this, LockConfirmActivity.class);
        startActivityForResult(intent, MyApplication.REQUEST_LOCK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case MyApplication.REQUEST_LOGIN:
                if (resultCode != RESULT_OK)
                    finish();
                break;
            case MyApplication.REQUEST_LOCK:
                if (resultCode != RESULT_OK)
                    goUnlock();
                break;
        }
    }

    @Override
    protected void onPause() {
        TokenManager.getInstance().setLastLeavingTime(new Date().getTime());
        hideLoginDialog();
        super.onPause();
    }

    @Override
    protected void onUserLeaveHint() {
        Logger.i(TAG, "onUserLeaveHint");
        TokenManager.getInstance().setLastLeavingTime(new Date().getTime());
        super.onUserLeaveHint();
    }
}
