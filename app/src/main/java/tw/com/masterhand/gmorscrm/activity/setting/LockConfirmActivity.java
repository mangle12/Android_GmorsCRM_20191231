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
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.view.MaterialLockView;

public class LockConfirmActivity extends BaseUserCheckActivity {

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
        pwd = preferenceHelper.getLock(TokenManager.getInstance().getUser().getId());
        if (TextUtils.isEmpty(pwd)) {
            Logger.e(TAG, "pwd is null");
            finish();
        }
    }

    protected void init() {
        tvTitle.setText(R.string.locker_unlock);
        lockView.setOnPatternListener(new MaterialLockView.OnPatternListener() {
            @Override
            public void onPatternStart() {
                Log.e(TAG, "onPatternStart");
                lockView.setDisplayMode(MaterialLockView.DisplayMode.Wrong);
                super.onPatternStart();
            }

            @Override
            public void onPatternDetected(List<MaterialLockView.Cell> pattern, String
                    SimplePattern) {
                Log.e(TAG, "onPatternDetected:" + SimplePattern);
                if (pattern.size() >= MyApplication.LOCKER_MIN_SIZE) {
                    if (pwd.equals(SimplePattern)) {
                        setResult(RESULT_OK);
                        finish();
                    }
                } else {
                    Toast.makeText(LockConfirmActivity.this, R.string.error_lock_least_point,
                            Toast.LENGTH_SHORT).show();
                }
                lockView.clearPattern();
                super.onPatternDetected(pattern, SimplePattern);
            }

            @Override
            public void onPatternCellAdded(List<MaterialLockView.Cell> pattern, String
                    SimplePattern) {
                if (pattern.size() >= MyApplication.LOCKER_MIN_SIZE && lockView.getDisplayMode()
                        != MaterialLockView
                        .DisplayMode.Correct) {
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
