package tw.com.masterhand.gmorscrm.activity.setting;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tw.com.masterhand.gmorscrm.BaseUserCheckActivity;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.TokenManager;
import tw.com.masterhand.gmorscrm.api.ApiHelper;
import tw.com.masterhand.gmorscrm.model.UserPassword;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.view.AppbarEdit;

public class PasswordChangeActivity extends BaseUserCheckActivity {
    @BindView(R.id.appbar)
    AppbarEdit appbar;
    @BindView(R.id.etOld)
    EditText etOld;
    @BindView(R.id.etNew)
    EditText etNew;
    @BindView(R.id.etConfirm)
    EditText etConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_change);
        init();
    }

    @Override
    protected void onUserChecked() {
        appbar.invalidate();
    }

    private void init() {
        appbar.setTitle(getString(R.string.setting_password_change));
        appbar.setCompleteListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkInput()) {
                    String userId = TokenManager.getInstance().getUser().getId();
                    String password = etNew.getText().toString();
                    UserPassword userPassword = new UserPassword();
                    userPassword.setId(userId);
                    userPassword.setPassword(password);
                    updatePassword(userPassword);
                }
            }
        });
    }

    private void updatePassword(final UserPassword password) {
        Logger.e(TAG, "userPassword:" + gson.toJson(password));

        Call<JSONObject> call = ApiHelper.getInstance().getUserApi()
                .updatePassword(TokenManager.getInstance().getToken(), password);
        call.enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                switch (response.code()) {
                    case 200:
                        preferenceHelper.savePassword(password.getPassword());
                        setResult(Activity.RESULT_OK);
                        finish();
                        break;
                    default:
                        if (response.body() != null) {
                            Logger.e(TAG, "body:" + response.body().toString());
                        }
                        if (response.errorBody() != null) {
                            try {
                                Logger.e(TAG, "errorBody:" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                ErrorHandler.getInstance().setException(PasswordChangeActivity.this, t);
            }
        });
    }

    private boolean checkInput() {
        boolean checkOk = true;

        String pOld = etOld.getText().toString();
        String pNew = etNew.getText().toString();
        String pConfirm = etConfirm.getText().toString();

        if (TextUtils.isEmpty(pOld)) {
            Toast.makeText(this, R.string.hint_password_old, Toast.LENGTH_SHORT).show();
            checkOk = false;
        } else if (TextUtils.isEmpty(pNew)) {
            Toast.makeText(this, R.string.hint_password_new, Toast.LENGTH_SHORT).show();
            checkOk = false;
        } else if (TextUtils.isEmpty(pConfirm)) {
            Toast.makeText(this, R.string.hint_password_confirm, Toast.LENGTH_SHORT).show();
            checkOk = false;
        } else if (!pOld.equals(preferenceHelper.getPassword())) {
            Toast.makeText(this, R.string.error_msg_password_wrong, Toast.LENGTH_SHORT).show();
            etOld.setText("");
            checkOk = false;
        } else if (!pNew.equals(pConfirm)) {
            Toast.makeText(this, R.string.error_msg_password_confirm, Toast.LENGTH_SHORT).show();
            etConfirm.setText("");
            checkOk = false;
        }
        return checkOk;
    }
}
