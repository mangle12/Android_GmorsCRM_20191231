package tw.com.masterhand.gmorscrm.activity.task;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.TokenManager;
import tw.com.masterhand.gmorscrm.enums.AlertType;
import tw.com.masterhand.gmorscrm.enums.TripType;
import tw.com.masterhand.gmorscrm.model.TaskWithConfig;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.tools.TimeFormater;

public class TaskDetailActivity extends WorkDetailActivity {
    protected TaskWithConfig task;

    @Override
    protected void init() {
        super.init();
        companyCard.setVisibility(View.GONE);
        itemProject.setVisibility(View.GONE);
        btnContacter.setVisibility(View.GONE);
        btnParticipant.setVisibility(View.GONE);
        btnApprover.setVisibility(View.GONE);
        containerSign.setVisibility(View.GONE);
        containerReport.setVisibility(View.GONE);
        containerAddress.setVisibility(View.GONE);
        itemTripStatus.setVisibility(View.GONE);
        appbar.setTitle(getString(R.string.work_task) + getString(R.string
                .title_activity_work_detail));

        tripId = getIntent().getStringExtra(MyApplication.INTENT_KEY_TRIP);
        if (TextUtils.isEmpty(tripId)) {
            finish();
            return;
        }
        tripType = TripType.TASK;

    }

    @Override
    protected void onUserChecked() {
        super.onUserChecked();
        updateData();
    }

    @Override
    protected void updateData() {
        Logger.i(TAG, "tripId:" + tripId);
        mDisposable.add(DatabaseHelper.getInstance(this).getTaskByTrip(tripId).observeOn
                (AndroidSchedulers
                        .mainThread())
                .subscribe(new Consumer<TaskWithConfig>() {
                    @Override
                    public void accept(TaskWithConfig result) throws Exception {
                        task = result;
                        approvalRequire = task.getTrip().getApprovalRequired();
                        approvalStatus = task.getTrip().getApproval();
                        taskId = task.getTask().getId();
                        /*2018-1-17新增:只有建立者可以修改*/
                        if (TokenManager.getInstance().getUser().getId().equals(result.getTrip()
                                .getUser_id())) {
                            appbar.removeFuction();
                            appbar.addFunction(R.mipmap.common_edit, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    // 編輯
                                    Intent intent = new Intent(view.getContext(),
                                            TaskCreateActivity.class);
                                    intent.putExtra(MyApplication.INTENT_KEY_TRIP, task.getTask()
                                            .getTrip_id());
                                    startActivityForResult(intent, MyApplication.REQUEST_EDIT);
                                }
                            });
                        }
                        tvTitle.setText(task.getTrip().getName());
                        tvTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, task
                                .getTrip().getType().getIcon(), 0);
                        if (!TextUtils.isEmpty(task.getTrip().getDescription()))
                            tvNote.setText(task.getTrip().getDescription());
                        if (task.getTrip().getDate_type()) {
                            tvStartTime.setText(TimeFormater.getInstance().toDateFormat(task
                                    .getTrip()
                                    .getFrom_date()));
                            tvEndTime.setText(TimeFormater.getInstance().toDateFormat(task.getTrip()
                                    .getTo_date()));
                        } else {
                            tvStartTime.setText(TimeFormater.getInstance().toDateTimeFormat(task
                                    .getTrip()
                                    .getFrom_date()));
                            tvEndTime.setText(TimeFormater.getInstance().toDateTimeFormat(task
                                    .getTrip()
                                    .getTo_date()));
                        }
                        tvStartWeek.setText(TimeFormater.getInstance().toWeekFormat(task.getTrip()
                                .getFrom_date
                                        ()));
                        tvEndWeek.setText(TimeFormater.getInstance().toWeekFormat(task.getTrip()
                                .getTo_date
                                        ()));
                        AlertType alertType = AlertType.getTypeByTime(task.getTrip()
                                .getNotification());
                        tvAlert.setText(alertType.getTitle());
                        getBuilder(task.getTrip().getUser_id());
                        updatePhoto(task.getFiles());
                        updateConversation();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ErrorHandler.getInstance().setException(TaskDetailActivity.this,
                                throwable);
                    }
                }));
    }

    @Override
    protected void onDataLoaded() {

    }
}
