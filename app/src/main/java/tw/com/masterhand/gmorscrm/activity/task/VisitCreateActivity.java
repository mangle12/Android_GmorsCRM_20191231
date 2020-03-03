package tw.com.masterhand.gmorscrm.activity.task;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.util.Date;

import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.TokenManager;
import tw.com.masterhand.gmorscrm.enums.AcceptType;
import tw.com.masterhand.gmorscrm.enums.VisitType;
import tw.com.masterhand.gmorscrm.model.ProjectWithConfig;
import tw.com.masterhand.gmorscrm.model.VisitWithConfig;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.record.Customer;
import tw.com.masterhand.gmorscrm.room.record.Participant;
import tw.com.masterhand.gmorscrm.room.record.Visit;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.tools.TimeFormater;

public class VisitCreateActivity extends WorkCreateActivity {

    VisitWithConfig visit;

    VisitType visitType = VisitType.NORMAL;

    @Override
    protected void onUserChecked() {
        super.onUserChecked();
        if (visit != null)
            return;
        String tripId = getIntent().getStringExtra(MyApplication.INTENT_KEY_TRIP);
        if (TextUtils.isEmpty(tripId)) {
            /*新建拜訪*/
            visit = new VisitWithConfig();
            visit.setVisit(new Visit());
            visit.getTrip().setFrom_date(new Date());
            visit.getTrip().setTo_date(new Date());
            Participant participant = new Participant();
            participant.setUser_id(TokenManager.getInstance().getUser().getId());
            participant.setAccept(AcceptType.YES);
            itemSelectPeople.addParticipant(participant);
            String customerId = getIntent().getStringExtra(MyApplication.INTENT_KEY_CUSTOMER);
            if (!TextUtils.isEmpty(customerId)) {
                mDisposable.add(DatabaseHelper.getInstance(this).getCustomerById(customerId)
                        .observeOn
                                (AndroidSchedulers.mainThread()).subscribe(new Consumer<Customer>
                                () {

                            @Override
                            public void accept(Customer customer) throws Exception {
                                itemSelectCustomer.setCustomer(customer);
                                itemSelectLocation.setAddress(customer.getAddress());
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                ErrorHandler.getInstance().setException(VisitCreateActivity.this,
                                        throwable);
                            }
                        }));
            }
            String projectId = getIntent().getStringExtra(MyApplication.INTENT_KEY_PROJECT);
            if (!TextUtils.isEmpty(projectId)) {
                mDisposable.add(DatabaseHelper.getInstance(this).getProjectById(projectId,
                        TokenManager
                                .getInstance().getUser().getDepartment_id(), TokenManager
                                .getInstance()
                                .getUser().getCompany_id()).observeOn
                        (AndroidSchedulers.mainThread()).subscribe(new Consumer<ProjectWithConfig>() {

                    @Override
                    public void accept(ProjectWithConfig project) throws Exception {
                        itemSelectProject.setProject(project);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ErrorHandler.getInstance().setException(VisitCreateActivity.this,
                                throwable);
                    }
                }));
            }
            String dateString = getIntent().getStringExtra(MyApplication.INTENT_KEY_DATE);
            if (!TextUtils.isEmpty(dateString)) {
                try {
                    Date date = TimeFormater.getInstance().fromDatabaseFormat(dateString);
                    itemSelectTime.setStart(date);
                    itemSelectTime.setEnd(DateUtils.addHours(date, 1));
                } catch (ParseException e) {
                    ErrorHandler.getInstance().setException(VisitCreateActivity.this,
                            e);
                }
            }
            itemSelectTime.updateTime();
        } else {
            /*編輯拜訪*/
            getVisit(tripId);
        }

    }

    @Override
    protected void init() {
        btnVisitType.setVisibility(View.VISIBLE);
        appbar.setTitle(getString(R.string.main_menu_new_task) + "/" + getString(R.string
                .work_visit));
        etName.setTitle(getString(R.string.visit_name));
        appbar.setCompleteListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 完成
                if (checkInput()) {
                    if (itemSelectCustomer.getCustomer() != null)
                        visit.getTrip().setCustomer_id(itemSelectCustomer.getCustomer()
                                .getId());
                    if (itemSelectProject.getProject() != null)
                        visit.getTrip().setProject_id(itemSelectProject.getProject().getProject()
                                .getId());
                    visit.getTrip().setName(etName.getText().toString());
                    visit.getTrip().setFrom_date(itemSelectTime.getStart().getTime());
                    visit.getTrip().setTo_date(itemSelectTime.getEnd().getTime());
                    visit.getTrip().setDate_type(itemSelectTime.isAllday());
                    visit.getTrip().setNotification(itemSelectTime.getAlertType().getTime());
                    visit.getTrip().setDescription(etNote.getText().toString());
                    if (visitType == VisitType.NORMAL)
                        visit.getVisit().setAddress(itemSelectLocation.getAddress());
                    visit.getReport().setContent(etReport.getText().toString());
                    visit.setContacters(itemSelectPeople.getContacters());
                    visit.setParticipants(itemSelectPeople.getParticipants());
                    visit.setFiles(itemSelectPhoto.getFiles());
                    setAlarm(visit.getTrip());
                }
            }
        });
        itemSelectProject.setItemSelectCustomer(itemSelectCustomer);
        itemSelectPeople.setItemSelectCustomer(itemSelectCustomer);
    }

    private boolean checkInput() {
        String required = getString(R.string.error_msg_required);
        if (TextUtils.isEmpty(etName.getText().toString())) {
            Logger.e(TAG, "name is empty");
            Toast.makeText(this, required + getString(R.string.visit_name), Toast.LENGTH_LONG)
                    .show();
            return false;
        }
        if (visitType == VisitType.NORMAL && itemSelectCustomer.getVisibility() == View.VISIBLE
                && itemSelectCustomer.getCustomer
                () == null) {
            Logger.e(TAG, "customer is empty");
            Toast.makeText(this, R.string.error_msg_no_customer, Toast.LENGTH_LONG).show();
            return false;
        }
        if (visitType == VisitType.NORMAL && itemSelectLocation.getVisibility() == View.VISIBLE) {
            if (itemSelectLocation.getAddress().getCountry() == null) {
                Logger.e(TAG, "location is empty");
                Toast.makeText(this, R.string.error_msg_no_location, Toast.LENGTH_LONG).show();
                return false;
            }
        }
        if (visitType == VisitType.NORMAL && itemSelectProject.getVisibility() == View.VISIBLE &&
                itemSelectProject.getProject()
                        == null) {
            Logger.e(TAG, "project is empty");
            Toast.makeText(this, R.string.error_msg_no_project, Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private void getVisit(String tripId) {
        mDisposable.add(DatabaseHelper.getInstance(this).getVisitByTrip(tripId).observeOn
                (AndroidSchedulers
                        .mainThread())
                .subscribe(new Consumer<VisitWithConfig>() {
                    @Override
                    public void accept(VisitWithConfig result) throws Exception {
                        visit = result;
                        btnVisitType.setText(visit.getVisit().getType().getTitle());
                        switch (visit.getVisit().getType()) {
                            case PHONE:
                            case EMAIL:
                            case FAST:
                                itemSelectLocation.setVisibility(View.GONE);
                                itemSelectTime.disableAlert();
                                itemSelectTime.disableAllday();
                                break;
                        }
                        if (!TextUtils.isEmpty(visit.getTrip().getCustomer_id()))
                            getCustomer(visit.getTrip().getCustomer_id());
                        if (!TextUtils.isEmpty(visit.getTrip().getProject_id()))
                            getProject(visit.getTrip().getProject_id());
                        if (!TextUtils.isEmpty(visit.getTrip().getName()))
                            etName.setText(visit.getTrip().getName());
                        if (!TextUtils.isEmpty(visit.getTrip().getDescription()))
                            etNote.setText(visit.getTrip().getDescription());
                        if (visit.getReport() != null)
                            etReport.setText(visit.getReport().getContent());
                        itemSelectTime.setStart(visit.getTrip().getFrom_date());
                        itemSelectTime.setEnd(visit.getTrip().getTo_date());
                        itemSelectTime.disableEdit();
                        itemSelectPeople.setParticipants(visit.getParticipants());
                        itemSelectPeople.setContacters(visit.getContacters());
                        itemSelectLocation.setAddress(visit.getVisit().getAddress());
                        itemSelectPhoto.setFiles(visit.getFiles());
                        itemSelectTime.updateTime();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ErrorHandler.getInstance().setException(VisitCreateActivity.this,
                                throwable);
                        finish();
                    }
                }));
    }

    @OnClick(R.id.btnVisitType)
    void changeVisitType() {
        String[] types = new String[VisitType.values().length];
        types[0] = getString(VisitType.NORMAL.getTitle());
        types[1] = getString(VisitType.PHONE.getTitle());
        types[2] = getString(VisitType.EMAIL.getTitle());
        types[3] = getString(VisitType.FAST.getTitle());
        AlertDialog.Builder builder = new AlertDialog.Builder(this, MyApplication
                .DIALOG_STYLE);
        builder.setTitle(R.string.visit_type).setItems(types, new DialogInterface
                .OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        visitType = VisitType.NORMAL;
                        break;
                    case 1:
                        visitType = VisitType.PHONE;
                        break;
                    case 2:
                        visitType = VisitType.EMAIL;
                        break;
                    case 3:
                        visitType = VisitType.FAST;
                        break;
                }
                if (which != 0) {
                    itemSelectLocation.setVisibility(View.GONE);
                    itemSelectTime.disableAlert();
                    itemSelectTime.disableAllday();
                } else {
                    itemSelectLocation.setVisibility(View.VISIBLE);
                    itemSelectTime.enableAlert();
                    itemSelectTime.enableAllday();
                }
                visit.getVisit().setType(VisitType.values()[which]);
                btnVisitType.setText(visitType.getTitle());
            }
        });
        builder.create().show();
    }

    @Override
    protected void save() {
        mDisposable.add(DatabaseHelper.getInstance(this).saveVisit(visit)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
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
                        Toast.makeText(VisitCreateActivity.this, t.getMessage(), Toast
                                .LENGTH_LONG).show();
                    }
                }));
    }

}
