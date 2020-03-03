package tw.com.masterhand.gmorscrm.activity.task;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import tw.com.masterhand.gmorscrm.BaseUserCheckActivity;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.TokenManager;
import tw.com.masterhand.gmorscrm.room.setting.User;
import tw.com.masterhand.gmorscrm.view.AppbarEdit;
import tw.com.masterhand.gmorscrm.view.ItemSelectPhoto;
import tw.com.masterhand.gmorscrm.view.ItemSelectTime;

public class TestCreateActivity extends BaseUserCheckActivity {
    @BindView(R.id.appbar)
    AppbarEdit appbar;
    @BindView(R.id.itemSelectTime)
    ItemSelectTime itemSelectTime;
    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.itemSelectPhoto)
    ItemSelectPhoto itemSelectPhoto;

    User User;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_create);
        init();
    }

    private void init() {
//        itemSelectTime.disableAlert();
        appbar.setTitle(getString(R.string.main_menu_new_task) + "/測試");
        appbar.setCompleteListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 完成
//                if (checkInput()) {
//                    absent.getTrip().setName(getString(absent.getAbsent().getType().getTitle()));
//                    absent.getTrip().setFrom_date(itemSelectTime.getStart().getTime());
//                    absent.getTrip().setTo_date(itemSelectTime.getEnd().getTime());
//                    absent.getTrip().setDate_type(itemSelectTime.isAllday());
//                    absent.getAbsent().setReason(etReason.getText().toString());
//                    absent.setFiles(itemSelectPhoto.getFiles());
//                    save();
//                }
            }
        });

        itemSelectTime.disableAlert();
        User= TokenManager.getInstance().getUser();
        tvName.setText(User.getLast_name() + User.getFirst_name());
    }

    @Override
    protected void onUserChecked() {
        appbar.invalidate();
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            itemSelectPhoto.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
