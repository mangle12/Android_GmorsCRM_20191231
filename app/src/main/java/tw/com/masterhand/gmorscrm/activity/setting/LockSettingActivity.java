package tw.com.masterhand.gmorscrm.activity.setting;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import tw.com.masterhand.gmorscrm.BaseUserCheckActivity;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.TokenManager;
import tw.com.masterhand.gmorscrm.view.MaterialLockView;

public class LockSettingActivity extends BaseUserCheckActivity {
    @BindView(R.id.pattern)
    protected MaterialLockView lockView;
    @BindView(R.id.textView_title)
    protected TextView tvTitle;

    String pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);
        init();
    }

    @Override
    protected void onUserChecked() {

    }

    protected void init() {
        lockView.setOnPatternListener(new MaterialLockView.OnPatternListener() {
            @Override
            public void onPatternStart() {
                Log.e(TAG, "onPatternStart");
                lockView.setDisplayMode(MaterialLockView.DisplayMode.Wrong);
                super.onPatternStart();
            }

            @Override
            public void onPatternDetected(List<MaterialLockView.Cell> pattern, String SimplePattern) {
                Log.e(TAG, "onPatternDetected:" + SimplePattern);
                if (pattern.size() >= MyApplication.LOCKER_MIN_SIZE) {
                    if (TextUtils.isEmpty(pwd)) {
                        Toast.makeText(LockSettingActivity.this, R.string.locker_input_again, Toast.LENGTH_SHORT).show();
                        pwd = SimplePattern;
                    } else {
                        if (pwd.equals(SimplePattern)) {
                            preferenceHelper.saveLock(TokenManager.getInstance().getUser().getId(), pwd);
                            finish();
                            Toast.makeText(LockSettingActivity.this, R.string
                                    .lock_setting_success, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LockSettingActivity.this, R.string
                                    .error_lock_setting_failed, Toast.LENGTH_SHORT).show();
                        }
                        pwd = null;
                    }
                } else {
                    Toast.makeText(LockSettingActivity.this, R.string.error_lock_least_point, Toast.LENGTH_SHORT).show();
                }
                lockView.clearPattern();
                super.onPatternDetected(pattern, SimplePattern);
            }

            @Override
            public void onPatternCellAdded(List<MaterialLockView.Cell> pattern, String SimplePattern) {
                if (pattern.size() >= MyApplication.LOCKER_MIN_SIZE && lockView.getDisplayMode() != MaterialLockView.DisplayMode.Correct) {
                    lockView.setDisplayMode(MaterialLockView.DisplayMode.Correct);
                }
                super.onPatternCellAdded(pattern, SimplePattern);
            }

            @Override
            public void onPatternCleared() {
                super.onPatternCleared();
            }
        });
    }

}
