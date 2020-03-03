package tw.com.masterhand.gmorscrm;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import tw.com.masterhand.gmorscrm.tools.Checker;

abstract public class BaseWebCheckActivity extends BaseUserCheckActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!Checker.isNetworkConnected(this)) {
            Toast.makeText(this, R.string.error_msg_no_network, Toast.LENGTH_LONG).show();
            finish();
        }
    }

}
