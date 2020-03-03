package tw.com.masterhand.gmorscrm.activity.task;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import tw.com.masterhand.gmorscrm.BaseUserCheckActivity;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.TokenManager;
import tw.com.masterhand.gmorscrm.enums.AbsentType;
import tw.com.masterhand.gmorscrm.model.AbsentWithConfig;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.record.Absent;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.tools.TimeFormater;
import tw.com.masterhand.gmorscrm.view.AppbarEdit;
import tw.com.masterhand.gmorscrm.view.ItemSelectPhoto;
import tw.com.masterhand.gmorscrm.view.ItemSelectTime;

public class AbsentCreateActivity extends BaseUserCheckActivity {
    @BindView(R.id.appbar)
    AppbarEdit appbar;
    @BindView(R.id.btnType)
    Button btnType;
    @BindView(R.id.etReason)
    EditText etReason;
    @BindView(R.id.itemSelectTime)
    ItemSelectTime itemSelectTime;
    @BindView(R.id.itemSelectPhoto)
    ItemSelectPhoto itemSelectPhoto;
    @BindView(R.id.tvName)
    TextView tvName;

    AbsentWithConfig absent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_absent_create);
        init();
    }

    @Override
    protected void onUserChecked() {
        appbar.invalidate();
        tvName.setText(TokenManager.getInstance().getUser().getShowName());
        if (absent != null)
            return;
        String tripId = getIntent().getStringExtra(MyApplication.INTENT_KEY_TRIP);
        if (TextUtils.isEmpty(tripId)) {
            /*新建請假*/
            absent = new AbsentWithConfig();
            absent.setAbsent(new Absent());
            String dateString = getIntent().getStringExtra(MyApplication.INTENT_KEY_DATE);
            if (!TextUtils.isEmpty(dateString)) {
                try {
                    Date date = TimeFormater.getInstance().fromDatabaseFormat(dateString);
                    itemSelectTime.setStart(date);
                    itemSelectTime.setEnd(DateUtils.addHours(date, 1));
                } catch (ParseException e) {
                    ErrorHandler.getInstance().setException(AbsentCreateActivity.this,
                            e);
                }
            }
            itemSelectTime.updateTime();
        } else {
            /*編輯請假*/
            getAbsent(tripId);
        }
    }

    private void init() {
        itemSelectTime.disableAlert();
        appbar.setTitle(getString(R.string.main_menu_new_task) + "/" + getString(R.string
                .work_absent));
        appbar.setCompleteListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 完成
                if (checkInput()) {
                    absent.getTrip().setName(getString(absent.getAbsent().getType().getTitle()));
                    absent.getTrip().setFrom_date(itemSelectTime.getStart().getTime());
                    absent.getTrip().setTo_date(itemSelectTime.getEnd().getTime());
                    absent.getTrip().setDate_type(itemSelectTime.isAllday());
                    absent.getAbsent().setReason(etReason.getText().toString());
                    absent.setFiles(itemSelectPhoto.getFiles());
                    save();
                }
            }
        });

    }

    private void getAbsent(String tripId) {
        mDisposable.add(DatabaseHelper.getInstance(this).getAbsentByTrip(tripId).observeOn
                (AndroidSchedulers.mainThread()).subscribe(new Consumer<AbsentWithConfig>() {
            @Override
            public void accept(AbsentWithConfig result) throws Exception {
                absent = result;
                itemSelectTime.setStart(absent.getTrip().getFrom_date());
                itemSelectTime.setEnd(absent.getTrip().getTo_date());
                itemSelectTime.setAllday(absent.getTrip().getDate_type());
                if (!TextUtils.isEmpty(absent.getAbsent().getReason()))
                    etReason.setText(absent.getAbsent().getReason());
                if (absent.getAbsent().getType() != null)
                    btnType.setText(absent.getAbsent().getType().getTitle());
                itemSelectPhoto.setFiles(absent.getFiles());
                itemSelectTime.updateTime();
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Toast.makeText(AbsentCreateActivity.this, throwable.getMessage(), Toast.LENGTH_LONG).show();
                finish();
            }
        }));
    }

    protected void save() {
        mDisposable.add(DatabaseHelper.getInstance(this).saveAbsent(absent).
                observeOn(AndroidSchedulers.mainThread()).
                subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        stopProgressDialog();
                        setResult(Activity.RESULT_OK);
                        finish();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable t) throws Exception {
                        stopProgressDialog();
                        Toast.makeText(AbsentCreateActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }));
    }

    @OnClick(R.id.btnType)
    void selectType() {
        String[] items = new String[AbsentType.values().length];
        int i = 0;
        for (AbsentType type : AbsentType.values()) {
            items[i] = getString(type.getTitle());
            i++;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this, MyApplication.DIALOG_STYLE);
        builder.setTitle(R.string.absent_type);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AbsentType type = AbsentType.values()[which];
                btnType.setText(type.getTitle());
                absent.getAbsent().setType(type);
            }
        });
        builder.create().show();
    }

    protected boolean checkInput() {
        String required = getString(R.string.error_msg_required);
        if (absent.getAbsent().getType() == null) {
            Toast.makeText(this, required + getString(R.string.absent_type), Toast.LENGTH_LONG)
                    .show();
            return false;
        }
        if (TextUtils.isEmpty(etReason.getText().toString())) {
            Toast.makeText(this, required + getString(R.string.absent_reason), Toast.LENGTH_LONG)
                    .show();
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            itemSelectPhoto.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
