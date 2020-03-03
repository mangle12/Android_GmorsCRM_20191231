package tw.com.masterhand.gmorscrm.activity.setting;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.xiaomi.mipush.sdk.MiPushClient;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import tw.com.masterhand.gmorscrm.BaseUserCheckActivity;
import tw.com.masterhand.gmorscrm.BuildConfig;
import tw.com.masterhand.gmorscrm.MainActivity;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.TokenManager;
import tw.com.masterhand.gmorscrm.api.SyncManager;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.tools.AlarmHelper;
import tw.com.masterhand.gmorscrm.view.Appbar;

public class SettingActivity extends BaseUserCheckActivity implements Switch.OnCheckedChangeListener {

    @BindView(R.id.appbar)
    Appbar appbar;
    @BindView(R.id.switchLockEnable)
    Switch switchLockEnable;
    @BindView(R.id.tvVersion)
    TextView tvVersion;
    @BindView(R.id.btnLockSetting)
    Button btnLockSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        init();
    }

    @Override
    protected void onUserChecked() {
        appbar.invalidate();
        switchLockEnable.setChecked(preferenceHelper.isLockEnable());
        switchLockEnable.setOnCheckedChangeListener(this);

        if (TextUtils.isEmpty(preferenceHelper.getLock(TokenManager.getInstance().getUser().getId())))
            btnLockSetting.setText(R.string.setting_lock);//設定手勢
        else
            btnLockSetting.setText(R.string.setting_lock_change);//修改手勢
    }

    private void init() {
        appbar.setTitle(getString(R.string.title_activity_setting));
        tvVersion.setText(BuildConfig.VERSION_NAME);
    }

    //更改主題色系
    @OnClick(R.id.btnChangeColor)
    void changeColor() {
        Intent intent = new Intent(SettingActivity.this, ThemColorActivity.class);
        startActivity(intent);
    }

    //修改密碼
    @OnClick(R.id.btnChangePassword)
    void changePassword() {
        Intent intent = new Intent(SettingActivity.this, PasswordChangeActivity.class);
        startActivity(intent);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isLock) {
        if (isLock) {
            if (TextUtils.isEmpty(preferenceHelper.getLock(TokenManager.getInstance().getUser().getId()))) {
                /*尚未設定手勢鎖*/
                lockSetting();
            } else {
                preferenceHelper.enableLock(isLock);
            }
        } else {
            preferenceHelper.enableLock(isLock);
        }
    }

    //設定手勢
    @OnClick(R.id.btnLockSetting)
    void lockSetting() {
        Intent intent = new Intent(SettingActivity.this, LockSettingActivity.class);
        startActivityForResult(intent, MyApplication.REQUEST_LOCK_RESET);
    }

    //關於CRM
    @OnClick(R.id.btnAbout)
    void showAbout() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.gmors.com.tw"));
        startActivity(browserIntent);
    }

    //使用教學
    @OnClick(R.id.btnGuide)
    void showGuide() {
        Intent intent = new Intent(SettingActivity.this, GuideListActivity.class);
        startActivity(intent);
    }

    //登出
    @OnClick(R.id.btnLogout)
    void logout() {
        DatabaseHelper.getInstance(this).logout(TokenManager.getInstance().getUser().getId())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {

            @Override
            public void onSubscribe(Disposable d) {
                mDisposable.add(d);
            }

            @Override
            public void onComplete() {
                AlarmHelper.cancelReportCheckAlarm(getApplicationContext());
                MiPushClient.pausePush(getApplicationContext(), null);
                SyncManager.getInstance(SettingActivity.this).stopSync();
                TokenManager.getInstance().clearUserToken();
                preferenceHelper.clearLastSync();
                preferenceHelper.enableLock(false);
                Intent intent = new Intent(SettingActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }

            @Override
            public void onError(Throwable e) {

            }
        });
    }
}
