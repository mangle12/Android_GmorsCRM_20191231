package tw.com.masterhand.gmorscrm;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.xiaomi.mipush.sdk.MiPushClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.functions.Consumer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tw.com.masterhand.gmorscrm.api.ApiHelper;
import tw.com.masterhand.gmorscrm.api.SyncManager;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.setting.User;
import tw.com.masterhand.gmorscrm.tools.AlarmHelper;
import tw.com.masterhand.gmorscrm.tools.Checker;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.tools.ImageHelper;
import tw.com.masterhand.gmorscrm.tools.InputHelper;
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.tools.TimeFormater;

public class LoginActivity extends BaseActivity implements SyncManager.OnSyncListener {
    @BindView(R.id.textInputLayout_account)
    TextInputLayout inputAccount;
    @BindView(R.id.textInputLayout_password)
    TextInputLayout inputPassword;
    @BindView(R.id.switch_remember)
    Switch switchRemember;
    @BindView(R.id.ivLogo)
    ImageView ivLogo;

    EditText etAccount, etPassword;
    ImageHelper imageHelper;
    AlertDialog versionCheckDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        init();
        // 測試用
//        etAccount.setText("tester@gmors.com.tw");
//        etAccount.setText("jayda.johnston@bayer.com");
//        etAccount.setText("joechou@gmors.com.tw");
//        etAccount.setText("yinru@gmors.com.tw");
        etAccount.setText("wayne@masterhand.com.tw");
        etPassword.setText("123456");
    }

    @Override
    public void onBackPressed() {

    }

    /*檢查版本*/
    void checkVersion() {
        ApiHelper.getInstance().getSystemApi().versionCheck(2, BuildConfig.VERSION_NAME).enqueue(new Callback<JSONObject>() {
                    @Override
                    public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                        try {
                            switch (response.code()) {
                                case 200:
                                    JSONObject result = response.body();
                                    int success = result.getInt("success");
                                    if (success == 0) {
                                        //有新版本
                                        final String updateUrl = result.getString("url");

                                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                        builder.setTitle(R.string.app_name)
                                                .setMessage(result.getString("errorMsg"))
                                                .setCancelable(false)
                                                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                                        intent.setData(Uri.parse(updateUrl));
                                                        startActivity(intent);
                                                    }
                                                });
                                        versionCheckDialog = builder.create();
                                        versionCheckDialog.show();
                                    }
                                    break;
                            }
                        } catch (Exception e) {
                            ErrorHandler.getInstance().setException(LoginActivity.this, e);
                        }
                    }

                    @Override
                    public void onFailure(Call<JSONObject> call, Throwable t) {
                        ErrorHandler.getInstance().setException(LoginActivity.this, t);
                    }
                });
    }

    private void init() {
        imageHelper = new ImageHelper(this);

        // 記住帳號設定
        boolean isRemember = preferenceHelper.isRemember();
        switchRemember.setChecked(isRemember);
        if (inputAccount.getEditText() != null) {
            etAccount = inputAccount.getEditText();
            etAccount.setOnFocusChangeListener(clearErrorListener);

            if (isRemember) {
                String account = preferenceHelper.getAccount();
                if (!TextUtils.isEmpty(account))
                {
                    etAccount.setText(account);
                }
            }
        }

        if (inputPassword.getEditText() != null) {
            etPassword = inputPassword.getEditText();
            etPassword.setOnFocusChangeListener(clearErrorListener);
            if (isRemember) {
                String password = preferenceHelper.getPassword();
                if (!TextUtils.isEmpty(password))
                {
                    etPassword.setText(password);
                }
            }
        }

        ivLogo.setImageURI(imageHelper.getLogo());
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkVersion();
    }

    @Override
    protected void onPause() {
        if (versionCheckDialog != null && versionCheckDialog.isShowing()) {
            versionCheckDialog.dismiss();
            versionCheckDialog = null;
        }
        super.onPause();

        try {
            SyncManager.getInstance(this).removeListener();
        } catch (Exception e) {
            ErrorHandler.getInstance().setException(this, e);
        }
        stopProgressDialog();
    }

    @OnClick(R.id.button_login)
    public void onClick(View view) {
        switch (view.getId()) {
            // 登入
            case R.id.button_login:
                preferenceHelper.saveRemember(switchRemember.isChecked());
                login();
                break;
        }
    }

    View.OnFocusChangeListener clearErrorListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean isFocus) {
            // 使用者點擊輸入框後error訊息消失
            if (isFocus) {
                inputAccount.setError(null);
                inputPassword.setError(null);
            }
        }
    };

    @OnFocusChange(R.id.linearLayout_top)
    public void onFocusChanged(View view, boolean isFocus) {
        Log.e(TAG, "onFocusChanged");
        switch (view.getId()) {
            case R.id.linearLayout_top:
                if (isFocus) {
                    InputHelper.hideSoftInput(LoginActivity.this, view);
                }
                break;
        }
    }

    private void onLoginFailed(final String msg) {
        stopProgressDialog();
        Logger.e(TAG, "onLoginFailed:" + msg);
        SyncManager.getInstance(this).removeListener().stopSync();

        if (!TextUtils.isEmpty(msg)) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void onLoginSuccess() {
        Logger.e(TAG, "onLoginSuccess");
        SyncManager.getInstance(this).removeListener();
        TokenManager.getInstance().setLastLeavingTime(new Date().getTime());
        MiPushClient.registerPush(getApplicationContext(), Constants.APP_ID, Constants.APP_KEY);
        MiPushClient.resumePush(getApplicationContext(), null);
        setResult(RESULT_OK);
        AlarmHelper.setReportCheckAlarm(this.getApplicationContext());
        stopProgressDialog();
        finish();
    }

    /**
     * 登入
     */
    private void login() {
        Logger.e(TAG, "login");
        startProgressDialog();
        String account = etAccount.getText().toString();
        String password = etPassword.getText().toString();

        if (TextUtils.isEmpty(account)) {
            inputAccount.setError(getString(R.string.msg_input_empty));
            stopProgressDialog();
            return;
        } else if (TextUtils.isEmpty(password)) {
            inputPassword.setError(getString(R.string.msg_input_empty));
            stopProgressDialog();
            return;
        }

        if (!Checker.isNetworkConnected(this)) {
            /*無網路，檢查是否有上次登入資訊*/
            User user = preferenceHelper.getUser(account, password);
            String token = preferenceHelper.getToken();
            if (user != null && !TextUtils.isEmpty(token)) {
                TokenManager.getInstance().setUser(user);
                TokenManager.getInstance().setToken(token);
                onLoginSuccess();
            } else {
                onLoginFailed(getString(R.string.error_msg_login));
            }
            return;
        }

        Call<JSONObject> call = ApiHelper.getInstance().getUserApi().login(account, password);
        call.enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                try {
                    switch (response.code()) {
                        case 200:
                            JSONObject result = response.body();
                            if (result.has("token")) {
                                String token = result.getString("token");
                                TokenManager.getInstance().setToken(token);
                                getOption(token);
                            } else {
                                String errorMsg = result.getString("errorMsg");
                                onLoginFailed(errorMsg);
                            }
                            break;
                        default:
                            onLoginFailed(response.code() + ":" + response.message());
                            break;
                    }
                } catch (Exception e) {
                    onLoginFailed(e.getMessage());
                    ErrorHandler.getInstance().setException(LoginActivity.this, e);
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                onLoginFailed(t.getMessage());
                ErrorHandler.getInstance().setException(LoginActivity.this, t);
            }
        });
    }

    private void getOption(String token) {
        ApiHelper.getInstance().getOptionApi().afterLogin(token).enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                switch (response.code()) {
                    case 200:
                        try {
                            JSONObject result = response.body().getJSONObject("list");
                            Logger.e(TAG, "option:" + response.body().toString());
                            String logoUrl = result.getString("2");
                            if (!TextUtils.isEmpty(logoUrl)) {
                                ImageLoader.getInstance().loadImage(logoUrl, new SimpleImageLoadingListener() {
                                            @Override
                                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                                ivLogo.setImageBitmap(loadedImage);
                                                imageHelper.saveLogo(loadedImage);
                                                super.onLoadingComplete(imageUri, view, loadedImage);
                                            }
                                        });
                            }
                            String headerUrl = result.getString("3");
                            if (!TextUtils.isEmpty(headerUrl)) {
                                ImageLoader.getInstance().loadImage(headerUrl, new SimpleImageLoadingListener() {
                                            @Override
                                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                                imageHelper.saveHeader(loadedImage);
                                                super.onLoadingComplete(imageUri, view, loadedImage);
                                            }
                                        });
                            }

                            String color = result.getString("4");

                            if (!TextUtils.isEmpty(color))
                            {
                                preferenceHelper.saveThemeColor(Color.parseColor(color));
                            }
                        } catch (Exception e)
                        {
                            Logger.e(TAG, "Exception:" + e.getMessage());
                        }

                        getUserData();
                        break;
                    default:
                        onLoginFailed(response.code() + ":" + response.message());
                        Logger.e(TAG, "getOptionApi error:" + response.code());
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                onLoginFailed(t.getMessage());
                Logger.e(TAG, "get option failed:" + t.getMessage());
            }
        });
    }

    /**
     * 取得使用者資訊
     */
    private void getUserData() {
        Logger.e(TAG, "getUserData");
        /*登入成功取得使用者資訊*/
        Call<JSONObject> call = ApiHelper.getInstance().getUserApi().getUser(TokenManager.getInstance().getToken());
        call.enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                try {
                    switch (response.code()) {
                        case 200:
                            JSONObject result = response.body();
                            String userString = result.getString("user");
                            Logger.e(TAG, "user:" + userString);
                            User user = gson.fromJson(userString, User.class);
                            Logger.e(TAG, "photo:" + user.getPhoto());
                            user.setTime_difference(TimeFormater.getCurrentTimeZone());
                            TokenManager.getInstance().setUser(user);
                            String account = etAccount.getText().toString();
                            String password = etPassword.getText().toString();

                            if (switchRemember.isChecked()) {
                                preferenceHelper.saveRemember(true);
                            }
                            if (preferenceHelper.saveUser(account, password, TokenManager.getInstance().getToken(), user)) {
                                DatabaseHelper.getInstance(LoginActivity.this);
                                mDisposable.add(Single.create(new SingleOnSubscribe<Boolean>() {
                                    @Override
                                    public void subscribe(SingleEmitter<Boolean> e) throws Exception {
                                        e.onSuccess(SyncManager.getInstance(LoginActivity.this).setListener(LoginActivity.this).startSync());
                                    }
                                }).delay(5, TimeUnit.SECONDS).subscribe(new Consumer<Boolean>() {
                                    @Override
                                    public void accept(Boolean startSync) throws Exception {
                                        if (!startSync)
                                            onLoginFailed("can't start sync.");
                                    }
                                }));
                            } else {
                                onLoginFailed("can't start sync.");
                            }
                            break;
                        default:
                            onLoginFailed(response.code() + ":" + response.message());
                            break;
                    }

                } catch (JSONException e) {
                    onLoginFailed(e.getMessage());
                    ErrorHandler.getInstance().setException(LoginActivity.this, e);
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                onLoginFailed(t.getMessage());
                ErrorHandler.getInstance().setException(LoginActivity.this, t);
            }
        });
    }


    @Override
    public void onSyncing() {
        onLoginSuccess();
    }

    @Override
    public void onSyncFinished() {
        onLoginSuccess();
    }

    @Override
    public void onSyncFailed(String msg) {
        onLoginFailed(msg);
    }

    @Override
    public void onSyncFileFailed(String msg) {

    }
}
