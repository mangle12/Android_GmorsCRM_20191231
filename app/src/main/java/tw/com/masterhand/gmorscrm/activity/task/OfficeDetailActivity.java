package tw.com.masterhand.gmorscrm.activity.task;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import tw.com.masterhand.gmorscrm.ApprovalListActivity;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.ParticipantListActivity;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.TokenManager;
import tw.com.masterhand.gmorscrm.enums.AlertType;
import tw.com.masterhand.gmorscrm.enums.TripType;
import tw.com.masterhand.gmorscrm.model.OfficeWithConfig;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.record.Participant;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.tools.TimeFormater;

public class OfficeDetailActivity extends WorkDetailActivity {
    protected OfficeWithConfig office;

    @Override
    protected void init() {
        super.init();
        companyCard.setVisibility(View.GONE);
        itemProject.setVisibility(View.GONE);
        btnContacter.setVisibility(View.GONE);
        containerReport.setVisibility(View.GONE);
        appbar.setTitle(getString(R.string.work_office) + getString(R.string
                .title_activity_work_detail));

        tripId = getIntent().getStringExtra(MyApplication.INTENT_KEY_TRIP);
        if (TextUtils.isEmpty(tripId)) {
            finish();
            return;
        }
        tripType = TripType.OFFICE;
    }

    @Override
    protected void onUserChecked() {
        super.onUserChecked();
        updateData();
    }

    @Override
    protected void updateData() {
        Logger.i(TAG, "tripId:" + tripId);
        mDisposable.add(DatabaseHelper.getInstance(this).getOfficeByTrip(tripId).observeOn
                (AndroidSchedulers
                        .mainThread())
                .subscribe(new Consumer<OfficeWithConfig>() {
                    @Override
                    public void accept(OfficeWithConfig result) throws Exception {
                        office = result;
                        approvalRequire = office.getTrip().getApprovalRequired();
                        approvalStatus = office.getTrip().getApproval();
                        taskId = office.getOffice().getId();
                        /*2018-1-17新增:只有建立者可以修改*/
                        if (TokenManager.getInstance().getUser().getId().equals(result.getTrip()
                                .getUser_id())) {
                            appbar.addFunction(R.mipmap.common_edit, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    // 編輯
                                    Intent intent = new Intent(view.getContext(),
                                            OfficeCreateActivity.class);
                                    intent.putExtra(MyApplication.INTENT_KEY_TRIP, office
                                            .getOffice().getTrip_id());
                                    startActivityForResult(intent, MyApplication.REQUEST_EDIT);
                                }
                            });
                        }
                        tvTitle.setText(office.getTrip().getName());
                        tvTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, office
                                .getTrip().getType().getIcon(), 0);
                        if (!TextUtils.isEmpty(office.getTrip().getDescription()))
                            tvNote.setText(office.getTrip().getDescription());
                        if (office.getTrip().getDate_type()) {
                            tvStartTime.setText(TimeFormater.getInstance().toDateFormat(office
                                    .getTrip()
                                    .getFrom_date()));
                            tvEndTime.setText(TimeFormater.getInstance().toDateFormat(office
                                    .getTrip()
                                    .getTo_date()));
                        } else {
                            tvStartTime.setText(TimeFormater.getInstance().toDateTimeFormat
                                    (office.getTrip()
                                            .getFrom_date()));
                            tvEndTime.setText(TimeFormater.getInstance().toDateTimeFormat(office
                                    .getTrip()
                                    .getTo_date()));
                        }
                        tvStartWeek.setText(TimeFormater.getInstance().toWeekFormat(office
                                .getTrip().getFrom_date
                                        ()));
                        tvEndWeek.setText(TimeFormater.getInstance().toWeekFormat(office.getTrip
                                ().getTo_date
                                ()));
                        AlertType alertType = AlertType.getTypeByTime(office.getTrip()
                                .getNotification());
                        tvAlert.setText(alertType.getTitle());
                        if (office.getParticipants().size() > 0) {
                            int count = office.getParticipants().size();
                            if (office.getTrip().getUser_id().equals(TokenManager.getInstance()
                                    .getUser().getId()))
                                count -= 1;
                            btnParticipant.setText(getString(R.string.participant) + " " + count);
                            btnParticipant.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(v.getContext(),
                                            ParticipantListActivity.class);
                                    intent.putExtra(MyApplication.INTENT_KEY_PARENT, office
                                            .getOffice().getId());
                                    startActivity(intent);
                                }
                            });
                            if (office.getTrip().getUser_id().equals(viewerId)) {
                                for (Participant participant : office
                                        .getParticipants()) {
                                    if (participant.getUser_id().equals(viewerId)) {
                                        viewer = participant;
                                        break;
                                    }
                                }
                                itemTripStatus.setVisibility(View.VISIBLE);
                                itemTripStatus.setTrip(office.getTrip(), viewer);
                            } else {
                                for (Participant participant : office
                                        .getParticipants()) {
                                    if (participant.getUser_id().equals(viewerId)) {
                                        viewer = participant;
                                        itemTripAccept.setVisibility(View.VISIBLE);
                                        itemTripAccept.setParticipant(participant);
                                        break;
                                    }
                                }
                            }
                            updateSignStatus();
                        } else {
                            containerSign.setVisibility(View.GONE);
                            btnParticipant.setVisibility(View.GONE);
                        }
                        tvLocation.setText(office.getOffice().getAddress().getShowAddress());
                        getBuilder(office.getTrip().getUser_id());
                        updatePhoto(office.getFiles());
                        updateConversation();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ErrorHandler.getInstance().setException(OfficeDetailActivity.this,
                                throwable);
                    }
                }));
    }

    @Override
    protected void onDataLoaded() {

    }

    @OnClick(R.id.btnApprover)
    void showApprover() {
        Intent intent = new Intent(this, ApprovalListActivity.class);
        intent.putExtra(MyApplication.INTENT_KEY_TRIP, office.getOffice().getTrip_id());
        startActivity(intent);
    }
}
