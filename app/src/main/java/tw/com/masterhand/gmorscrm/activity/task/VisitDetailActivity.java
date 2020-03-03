package tw.com.masterhand.gmorscrm.activity.task;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import tw.com.masterhand.gmorscrm.ApprovalListActivity;
import tw.com.masterhand.gmorscrm.ContacterListActivity;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.ParticipantListActivity;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.TokenManager;
import tw.com.masterhand.gmorscrm.enums.AlertType;
import tw.com.masterhand.gmorscrm.enums.SignStatus;
import tw.com.masterhand.gmorscrm.enums.TripType;
import tw.com.masterhand.gmorscrm.enums.VisitType;
import tw.com.masterhand.gmorscrm.model.ProjectWithConfig;
import tw.com.masterhand.gmorscrm.model.VisitWithConfig;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.record.Customer;
import tw.com.masterhand.gmorscrm.room.record.Participant;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.tools.TimeFormater;
import tw.com.masterhand.gmorscrm.tools.TranslateHelper;

public class VisitDetailActivity extends WorkDetailActivity {
    @BindView(R.id.containerType)
    LinearLayout containerType;
    @BindView(R.id.tvType)
    TextView tvType;
    @BindView(R.id.tvReportUpdated)
    TextView tvReportUpdated;
    @BindView(R.id.btnTranslation)
    ImageButton btnTranslation;

    protected VisitWithConfig visit;

    private String translation = "";
    private boolean isTranslationMode = false;

    @Override
    protected void init() {
        super.init();
        appbar.setTitle(getString(R.string.work_visit) + getString(R.string
                .title_activity_work_detail));

        tripId = getIntent().getStringExtra(MyApplication.INTENT_KEY_TRIP);
        if (TextUtils.isEmpty(tripId)) {
            finish();
            return;
        }
        tripType = TripType.VISIT;
        containerType.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onUserChecked() {
        super.onUserChecked();
        if (TokenManager.getInstance().getUser().getIs_translate())
            btnTranslation.setVisibility(View.VISIBLE);
        updateData();
    }

    @Override
    protected void updateData() {
        Logger.i(TAG, "tripId:" + tripId);
        mDisposable.add(DatabaseHelper.getInstance(this).getVisitByTrip(tripId).observeOn
                (AndroidSchedulers
                        .mainThread())
                .subscribe(new Consumer<VisitWithConfig>() {
                    @Override
                    public void accept(VisitWithConfig result) throws Exception {
                        visit = result;
                        approvalRequire = visit.getTrip().getApprovalRequired();
                        approvalStatus = visit.getTrip().getApproval();
                        taskId = visit.getVisit().getId();
                        Logger.e(TAG, "taskId:" + taskId);
                        Logger.e(TAG, "name:" + visit.getTrip().getName());
                        /*2018-1-17新增:只有建立者可以修改*/
                        if (TokenManager.getInstance().getUser().getId().equals(result.getTrip()
                                .getUser_id())) {
                            appbar.removeFuction();
                            appbar.addFunction(R.mipmap.common_edit, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    // 編輯
                                    Intent intent = new Intent(view.getContext(),
                                            VisitCreateActivity.class);
                                    intent.putExtra(MyApplication.INTENT_KEY_TRIP, visit.getVisit
                                            ().getTrip_id());
                                    startActivityForResult(intent, MyApplication.REQUEST_EDIT);
                                }
                            });
                        }
                        tvTitle.setText(visit.getTrip().getName());
                        tvTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, visit
                                .getTrip().getType().getIcon(), 0);
                        tvType.setText(visit.getVisit().getType().getTitle());

                        if (visit.getVisit().getAddress().getCountry() != null)
                            tvLocation.setText(visit.getVisit().getAddress().getShowAddress());
                        else
                            containerAddress.setVisibility(View.GONE);
                        if (!TextUtils.isEmpty(visit.getTrip().getDescription()))
                            tvNote.setText(visit.getTrip().getDescription());
                        if (visit.getTrip().getDate_type()) {
                            tvStartTime.setText(TimeFormater.getInstance().toDateFormat(visit
                                    .getTrip()
                                    .getFrom_date()));
                            tvEndTime.setText(TimeFormater.getInstance().toDateFormat(visit
                                    .getTrip()
                                    .getTo_date()));
                        } else {
                            tvStartTime.setText(TimeFormater.getInstance().toDateTimeFormat(visit
                                    .getTrip()
                                    .getFrom_date()));
                            tvEndTime.setText(TimeFormater.getInstance().toDateTimeFormat(visit
                                    .getTrip()
                                    .getTo_date()));
                        }
                        tvStartWeek.setText(TimeFormater.getInstance().toWeekFormat(visit.getTrip
                                ().getFrom_date
                                ()));
                        tvEndWeek.setText(TimeFormater.getInstance().toWeekFormat(visit.getTrip()
                                .getTo_date
                                        ()));
                        AlertType alertType = AlertType.getTypeByTime(visit.getTrip()
                                .getNotification());
                        tvAlert.setText(alertType.getTitle());
                        if (visit.getContacters().size() > 0) {
                            btnContacter.setText(getString(R.string.contact) + " " + visit
                                    .getContacters()
                                    .size());
                            btnContacter.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(v.getContext(),
                                            ContacterListActivity.class);
                                    intent.putExtra(MyApplication.INTENT_KEY_PARENT, visit
                                            .getVisit().getId());
                                    startActivity(intent);
                                }
                            });
                        } else {
                            btnContacter.setVisibility(View.GONE);
                        }
                        Logger.e(TAG, "participants size:" + visit.getParticipants().size());
                        if (visit.getParticipants().size() > 0) {
                            Logger.e(TAG, "result:" + gson.toJson(visit.getParticipants()));
                            int count = visit.getParticipants().size();
                            if (visit.getTrip().getUser_id().equals(TokenManager.getInstance()
                                    .getUser().getId()))
                                count -= 1;
                            btnParticipant.setText(getString(R.string.participant) + " " + count);
                            btnParticipant.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(v.getContext(),
                                            ParticipantListActivity.class);
                                    intent.putExtra(MyApplication.INTENT_KEY_PARENT, visit
                                            .getVisit().getId());
                                    startActivity(intent);
                                }
                            });
                            if (visit.getTrip().getUser_id().equals(viewerId)) {
                                boolean isUser = false;
                                boolean isSign = false;
                                for (Participant participant : visit
                                        .getParticipants()) {
                                    if (participant.getUser_id().equals(viewerId)) {
                                        isUser = true;
                                        viewer = participant;
                                    }
                                    if (participant.getStatus() != SignStatus.NONE)
                                        isSign = true;
                                }
                                if (isUser && !isSign) {
                                    itemTripStatus.setVisibility(View.VISIBLE);
                                    itemTripStatus.setTrip(visit.getTrip(), viewer);
                                } else {
                                    itemTripStatus.setVisibility(View.GONE);
                                }
                            } else {
                                for (Participant participant : visit
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
                            itemTripStatus.setVisibility(View.GONE);
                            containerSign.setVisibility(View.GONE);
                            btnParticipant.setVisibility(View.GONE);
                        }
                        if (visit.getReport() != null) {
                            Logger.e(TAG, "report content:" + visit.getReport().getContent());
                            tvReport.setText(visit.getReport().getContent());
                            tvReportUpdated.setText(TimeFormater.getInstance().toDateTimeFormat
                                    (visit.getReport
                                            ().getUpdated_at()) +
                                    " " + getString(R.string.report_updated_at));
                        } else {
                            tvReport.setText(R.string.empty_show);
                        }
                        if (visit.getVisit().getType() != VisitType.NORMAL) {
                            // 非外出拜訪
                            containerSign.setVisibility(View.GONE);
                            tvAlert.setVisibility(View.GONE);
                            itemTripStatus.setVisibility(View.GONE);
                        }
                        getBuilder(visit.getTrip().getUser_id());
                        getCustomer();
                        getProject();
                        updatePhoto(visit.getFiles());
                        updateConversation();
                        onDataLoaded();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ErrorHandler.getInstance().setException(VisitDetailActivity.this,
                                throwable);
                        finish();
                    }
                }));
    }

    @Override
    protected void onDataLoaded() {

    }

    @OnClick(R.id.btnTranslation)
    void translateReport() {
        if (isTranslationMode) {
            setTranslationMode(false);
            tvReport.setText(visit.getReport().getContent());
        } else {
            String input = tvReport.getText().toString();
            if (TextUtils.isEmpty(input))
                return;
            if (TextUtils.isEmpty(translation)) {
                TranslateHelper translateHelper = new TranslateHelper();
                translateHelper.startTranslate(input, new TranslateHelper.TranslateListener() {
                    @Override
                    public void onTranslateFinish(String result) {
                        translation = result;
                        setTranslationMode(true);
                        tvReport.setText(translation);
                    }

                    @Override
                    public void onTranslateError(String errorMsg) {
                        Toast.makeText(VisitDetailActivity.this, errorMsg, Toast.LENGTH_LONG)
                                .show();

                    }
                });
            } else {
                setTranslationMode(true);
                tvReport.setText(translation);
            }
        }
    }

    private void setTranslationMode(Boolean mode) {
        isTranslationMode = mode;
        if (isTranslationMode) {
            btnTranslation.setImageResource(R.mipmap.common_button_translate_eng);
        } else {
            btnTranslation.setImageResource(R.mipmap.common_button_translate_chi);
        }
    }

    @OnClick(R.id.btnApprover)
    void showApprover() {
        Intent intent = new Intent(this, ApprovalListActivity.class);
        intent.putExtra(MyApplication.INTENT_KEY_TRIP, visit.getVisit().getTrip_id());
        startActivity(intent);
    }

    private void getCustomer() {
        if (TextUtils.isEmpty(visit.getTrip().getCustomer_id())) {
            companyCard.setVisibility(View.GONE);
            return;
        }
        mDisposable.add(DatabaseHelper.getInstance(this).getCustomerById(visit.getTrip()
                .getCustomer_id()).observeOn
                (AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Customer>() {
                    @Override
                    public void accept(Customer customer) throws Exception {
                        companyCard.setCustomer(customer);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ErrorHandler.getInstance().setException(VisitDetailActivity.this,
                                throwable);
                    }
                }));
    }

    private void getProject() {
        if (TextUtils.isEmpty(visit.getTrip().getProject_id())) {
            itemProject.setVisibility(View.GONE);
            return;
        }
        mDisposable.add(DatabaseHelper.getInstance(this).getProjectById(visit.getTrip()
                .getProject_id(), TokenManager.getInstance()
                .getUser().getDepartment_id(), TokenManager.getInstance().getUser().getCompany_id
                ()).observeOn
                (AndroidSchedulers.mainThread()).subscribe(new Consumer<ProjectWithConfig>() {

            @Override
            public void accept(ProjectWithConfig projectWithConfig) throws
                    Exception {
                itemProject.setProject(projectWithConfig);
                itemProject.setTripId(visit.getTrip().getId());
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                ErrorHandler.getInstance().setException(VisitDetailActivity.this,
                        throwable);
            }
        }));
    }

}
