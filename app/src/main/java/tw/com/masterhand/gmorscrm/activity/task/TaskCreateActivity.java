package tw.com.masterhand.gmorscrm.activity.task;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.util.Date;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.model.TaskWithConfig;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.record.Task;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.tools.TimeFormater;

public class TaskCreateActivity extends WorkCreateActivity {

    TaskWithConfig task;

    @Override
    protected void onUserChecked() {
        super.onUserChecked();
        if (task != null)
            return;
        String tripId = getIntent().getStringExtra(MyApplication.INTENT_KEY_TRIP);
        if (TextUtils.isEmpty(tripId)) {
            /*新建內勤*/
            task = new TaskWithConfig();
            task.setTask(new Task());
            task.getTrip().setFrom_date(new Date());
            task.getTrip().setTo_date(new Date());
            String dateString = getIntent().getStringExtra(MyApplication.INTENT_KEY_DATE);
            if (!TextUtils.isEmpty(dateString)) {
                try {
                    Date date = TimeFormater.getInstance().fromDatabaseFormat(dateString);
                    itemSelectTime.setStart(date);
                    itemSelectTime.setEnd(DateUtils.addHours(date, 1));
                } catch (ParseException e) {
                    ErrorHandler.getInstance().setException(TaskCreateActivity.this,
                            e);
                }
            }
            itemSelectTime.updateTime();
        } else {
            /*編輯內勤*/
            getTask(tripId);
        }
    }

    @Override
    protected void init() {
        etName.setTitle(getString(R.string.task_name));
        itemSelectCustomer.setVisibility(View.GONE);
        itemSelectProject.setVisibility(View.GONE);
        itemSelectLocation.setVisibility(View.GONE);
        itemSelectPeople.setVisibility(View.GONE);
        hideReport();
        appbar.setTitle(getString(R.string.main_menu_new_task) + "/" + getString(R.string
                .work_task));
        appbar.setCompleteListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 完成
                if (checkInput()) {
                    task.getTrip().setName(etName.getText().toString());
                    task.getTrip().setFrom_date(itemSelectTime.getStart().getTime());
                    task.getTrip().setTo_date(itemSelectTime.getEnd().getTime());
                    task.getTrip().setDate_type(itemSelectTime.isAllday());
                    task.getTrip().setNotification(itemSelectTime.getAlertType().getTime());
                    task.getTrip().setDescription(etNote.getText().toString());
                    task.setFiles(itemSelectPhoto.getFiles());
                    setAlarm(task.getTrip());
                }
            }
        });

    }

    private void getTask(String tripId) {
        mDisposable.add(DatabaseHelper.getInstance(this).getTaskByTrip(tripId).observeOn
                (AndroidSchedulers
                        .mainThread())
                .subscribe(new Consumer<TaskWithConfig>() {
                    @Override
                    public void accept(TaskWithConfig result) throws Exception {
                        task = result;
                        if (!TextUtils.isEmpty(task.getTrip().getName()))
                            etName.setText(task.getTrip().getName());
                        if (!TextUtils.isEmpty(task.getTrip().getDescription()))
                            etNote.setText(task.getTrip().getDescription());
                        itemSelectTime.setStart(task.getTrip().getFrom_date());
                        itemSelectTime.setEnd(task.getTrip().getTo_date());
                        itemSelectTime.setAllday(task.getTrip().getDate_type());
                        itemSelectPhoto.setFiles(task.getFiles());
                        itemSelectTime.updateTime();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ErrorHandler.getInstance().setException(TaskCreateActivity.this,
                                throwable);
                        finish();
                    }
                }));
    }

    @Override
    protected void save() {
        mDisposable.add(DatabaseHelper.getInstance(this).saveTask(task).observeOn
                (AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Logger.e(TAG, "save task id:" + s);
                        stopProgressDialog();
                        setResult(Activity.RESULT_OK);
                        finish();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable t) throws Exception {
                        stopProgressDialog();
                        Toast.makeText(TaskCreateActivity.this, t.getMessage(), Toast
                                .LENGTH_LONG).show();
                    }
                }));
    }

    private boolean checkInput() {
        if (TextUtils.isEmpty(etName.getText().toString())) {
            Logger.e(TAG, "name is empty");
            Toast.makeText(this, getString(R.string.error_msg_required) + getString(R.string
                    .task_name), Toast.LENGTH_LONG).show();
            return false;
        }
        if (itemSelectCustomer.getVisibility() == View.VISIBLE && itemSelectCustomer.getCustomer
                () == null) {
            Logger.e(TAG, "customer is empty");
            Toast.makeText(this, R.string.error_msg_no_customer, Toast.LENGTH_LONG).show();
            return false;
        }
        if (itemSelectLocation.getVisibility() == View.VISIBLE) {
            if (itemSelectLocation.getAddress().getCountry() == null) {
                Logger.e(TAG, "location is empty");
                Toast.makeText(this, R.string.error_msg_no_location, Toast.LENGTH_LONG).show();
                return false;
            }
        }
        if (itemSelectProject.getVisibility() == View.VISIBLE && itemSelectProject.getProject()
                == null) {
            Logger.e(TAG, "project is empty");
            Toast.makeText(this, R.string.error_msg_no_project, Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }
}
