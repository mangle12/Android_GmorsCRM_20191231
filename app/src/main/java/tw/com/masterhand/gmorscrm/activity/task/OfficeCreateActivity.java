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
import tw.com.masterhand.gmorscrm.TokenManager;
import tw.com.masterhand.gmorscrm.enums.AcceptType;
import tw.com.masterhand.gmorscrm.model.OfficeWithConfig;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.record.Office;
import tw.com.masterhand.gmorscrm.room.record.Participant;
import tw.com.masterhand.gmorscrm.room.setting.Company;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.tools.TimeFormater;

public class OfficeCreateActivity extends WorkCreateActivity {

    OfficeWithConfig office;

    @Override
    protected void onUserChecked() {
        super.onUserChecked();
        if (office != null)
            return;
        String tripId = getIntent().getStringExtra(MyApplication.INTENT_KEY_TRIP);
        if (TextUtils.isEmpty(tripId)) {
            /*新建內勤*/
            office = new OfficeWithConfig();
            office.setOffice(new Office());
            office.getTrip().setFrom_date(new Date());
            office.getTrip().setTo_date(new Date());
            Participant participant = new Participant();
            participant.setUser_id(TokenManager.getInstance().getUser().getId());
            participant.setAccept(AcceptType.YES);
            itemSelectPeople.addParticipant(participant);
            mDisposable.add(DatabaseHelper.getInstance(this).getCompanyById(TokenManager
                    .getInstance().getUser()
                    .getCompany_id()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Company>() {

                @Override
                public void accept(Company company) throws Exception {
                    itemSelectLocation.setAddress(company.getAddress());
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    ErrorHandler.getInstance().setException(OfficeCreateActivity.this, throwable);
                }
            }));
            String dateString = getIntent().getStringExtra(MyApplication.INTENT_KEY_DATE);
            if (!TextUtils.isEmpty(dateString)) {
                try {
                    Date date = TimeFormater.getInstance().fromDatabaseFormat(dateString);
                    itemSelectTime.setStart(date);
                    itemSelectTime.setEnd(DateUtils.addHours(date, 1));
                } catch (ParseException e) {
                    ErrorHandler.getInstance().setException(OfficeCreateActivity.this,
                            e);
                }
            }
            itemSelectTime.updateTime();
        } else {
            /*編輯內勤*/
            getOffice(tripId);
        }
    }

    @Override
    protected void init() {
        hideReport();
        etName.setTitle(getString(R.string.office_name));
        itemSelectCustomer.setVisibility(View.GONE);
        itemSelectProject.setVisibility(View.GONE);
        itemSelectPeople.disableContacter();
        appbar.setTitle(getString(R.string.main_menu_new_task) + "/" + getString(R.string
                .work_office));
        appbar.setCompleteListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 完成
                if (checkInput()) {
                    office.getTrip().setName(etName.getText().toString());
                    office.getTrip().setFrom_date(itemSelectTime.getStart().getTime());
                    office.getTrip().setTo_date(itemSelectTime.getEnd().getTime());
                    office.getTrip().setDate_type(itemSelectTime.isAllday());
                    office.getTrip().setNotification(itemSelectTime.getAlertType().getTime());
                    office.getTrip().setDescription(etNote.getText().toString());
                    office.getOffice().setAddress(itemSelectLocation.getAddress());
                    office.setParticipants(itemSelectPeople.getParticipants());
                    office.setFiles(itemSelectPhoto.getFiles());
                    setAlarm(office.getTrip());
                }
            }
        });


    }

    private void getOffice(String tripId) {
        mDisposable.add(DatabaseHelper.getInstance(this).getOfficeByTrip(tripId).observeOn
                (AndroidSchedulers
                        .mainThread())
                .subscribe(new Consumer<OfficeWithConfig>() {
                    @Override
                    public void accept(OfficeWithConfig result) throws Exception {
                        office = result;
                        if (!TextUtils.isEmpty(office.getTrip().getName()))
                            etName.setText(office.getTrip().getName());
                        if (!TextUtils.isEmpty(office.getTrip().getDescription()))
                            etNote.setText(office.getTrip().getDescription());
                        itemSelectTime.setStart(office.getTrip().getFrom_date());
                        itemSelectTime.setEnd(office.getTrip().getTo_date());
                        itemSelectTime.disableEdit();
                        itemSelectPeople.setParticipants(office.getParticipants());
                        itemSelectLocation.setAddress(office.getOffice().getAddress());
                        itemSelectPhoto.setFiles(office.getFiles());
                        itemSelectTime.updateTime();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ErrorHandler.getInstance().setException(OfficeCreateActivity.this,
                                throwable);
                        finish();
                    }
                }));
    }

    @Override
    protected void save() {
        mDisposable.add(DatabaseHelper.getInstance(this).saveOffice(office).observeOn
                (AndroidSchedulers
                        .mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Logger.e(TAG, "save office id:" + s);
                        stopProgressDialog();
                        setResult(Activity.RESULT_OK);
                        finish();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable t) throws Exception {
                        stopProgressDialog();
                        Toast.makeText(OfficeCreateActivity.this, t.getMessage(), Toast
                                .LENGTH_LONG).show();
                    }
                }));
    }

    private boolean checkInput() {
        if (TextUtils.isEmpty(etName.getText().toString())) {
            Logger.e(TAG, "name is empty");
            Toast.makeText(this, getString(R.string.error_msg_required) + getString(R.string
                    .office_name), Toast.LENGTH_LONG).show();
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
