package tw.com.masterhand.gmorscrm.activity.task;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import tw.com.masterhand.gmorscrm.BaseUserCheckActivity;
import tw.com.masterhand.gmorscrm.LocationSelectActivity;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.TokenManager;
import tw.com.masterhand.gmorscrm.enums.AcceptType;
import tw.com.masterhand.gmorscrm.model.ProjectWithConfig;
import tw.com.masterhand.gmorscrm.model.TravelWithConfig;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.record.Customer;
import tw.com.masterhand.gmorscrm.room.record.Participant;
import tw.com.masterhand.gmorscrm.room.record.Travel;
import tw.com.masterhand.gmorscrm.room.setting.Admin;
import tw.com.masterhand.gmorscrm.room.setting.ConfigCity;
import tw.com.masterhand.gmorscrm.room.setting.ConfigCountry;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.tools.TimeFormater;
import tw.com.masterhand.gmorscrm.view.AppbarEdit;
import tw.com.masterhand.gmorscrm.view.EditTextWithTitle;
import tw.com.masterhand.gmorscrm.view.ItemSelectCustomer;
import tw.com.masterhand.gmorscrm.view.ItemSelectLocation;
import tw.com.masterhand.gmorscrm.view.ItemSelectPeople;
import tw.com.masterhand.gmorscrm.view.ItemSelectPhoto;
import tw.com.masterhand.gmorscrm.view.ItemSelectProject;
import tw.com.masterhand.gmorscrm.view.ItemSelectTime;

public class TravelCreateActivity extends BaseUserCheckActivity implements ItemSelectCustomer
        .CustomerSelectListener, ItemSelectLocation.OnSelectListener {
    @BindView(R.id.appbar)
    AppbarEdit appbar;
    @BindView(R.id.itemSelectCustomer)
    ItemSelectCustomer itemSelectCustomer;
    @BindView(R.id.itemSelectProject)
    ItemSelectProject itemSelectProject;
    @BindView(R.id.etName)
    EditTextWithTitle etName;
    @BindView(R.id.etInfo)
    EditTextWithTitle etInfo;
    @BindView(R.id.etReason)
    EditTextWithTitle etReason;
    @BindView(R.id.etSupport)
    EditTextWithTitle etSupport;
    @BindView(R.id.etCustomerBackground)
    EditTextWithTitle etCustomerBackground;
    @BindView(R.id.etCustomerTech)
    EditTextWithTitle etCustomerTech;
    @BindView(R.id.itemSelectLocation)
    ItemSelectLocation itemSelectLocation;
    @BindView(R.id.itemSelectTime)
    ItemSelectTime itemSelectTime;
    @BindView(R.id.itemSelectPeople)
    ItemSelectPeople itemSelectPeople;

    @BindView(R.id.itemSelectPhoto)
    ItemSelectPhoto itemSelectPhoto;
    @BindView(R.id.etNote)
    EditText etNote;

    TravelWithConfig travel;

    List<Admin> adminList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_create);
        init();
    }

    @Override
    protected void onUserChecked() {
        appbar.invalidate();
        if (travel != null)
            return;
        String tripId = getIntent().getStringExtra(MyApplication.INTENT_KEY_TRIP);
        if (TextUtils.isEmpty(tripId)) {
            /*新建出差*/
            travel = new TravelWithConfig(new Travel());
            Participant participant = new Participant();
            participant.setUser_id(TokenManager.getInstance().getUser().getId());
            participant.setAccept(AcceptType.YES);
            itemSelectPeople.addParticipant(participant);
            final String customerId = getIntent().getStringExtra(MyApplication.INTENT_KEY_CUSTOMER);
            if (!TextUtils.isEmpty(customerId)) {
                mDisposable.add(DatabaseHelper.getInstance(this).getCustomerById(customerId)
                        .observeOn
                                (AndroidSchedulers.mainThread()).subscribe(new Consumer<Customer>
                                () {

                            @Override
                            public void accept(Customer customer) throws Exception {
                                itemSelectCustomer.setCustomer(customer);
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                ErrorHandler.getInstance().setException(TravelCreateActivity.this,
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
                        ErrorHandler.getInstance().setException(TravelCreateActivity.this,
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
                    ErrorHandler.getInstance().setException(TravelCreateActivity.this,
                            e);
                }
            }
            itemSelectTime.updateTime();
        } else {
            /*編輯出差*/
            getTravel(tripId);
        }
    }

    protected void init() {
        itemSelectProject.setItemSelectCustomer(itemSelectCustomer);
        itemSelectCustomer.addCustomerSelectedListener(this);
        itemSelectPeople.disableContacter();
        itemSelectLocation.setOnSelectListener(this);

        appbar.setTitle(getString(R.string.main_menu_new_task) + "/" + getString(R.string
                .apply_travel));
        appbar.setCompleteListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInput()) {
                    startProgressDialog();
                    travel.getTrip().setCustomer_id(itemSelectCustomer.getCustomer().getId());
                    travel.getTrip().setProject_id(itemSelectProject.getProject().getProject()
                            .getId());
                    travel.getTrip().setFrom_date(itemSelectTime.getStart().getTime());
                    travel.getTrip().setTo_date(itemSelectTime.getEnd().getTime());
                    travel.getTrip().setDate_type(itemSelectTime.isAllday());
                    travel.getTrip().setName(etName.getText().toString());
                    travel.getTrip().setDescription(etNote.getText().toString());
                    travel.getTravel().setAddress(itemSelectLocation.getAddress());
                    travel.getTravel().setInfo(etInfo.getText().toString());
                    travel.getTravel().setReason(etReason.getText().toString());
                    travel.getTravel().setSupport(etSupport.getText().toString());
                    travel.getTravel().setCustomer_background(etCustomerBackground.getText()
                            .toString());
                    travel.getTravel().setCustomer_tech(etCustomerTech.getText().toString());

                    travel.setParticipants(itemSelectPeople.getParticipants());
                    travel.setFiles(itemSelectPhoto.getFiles());
                    mDisposable.add(DatabaseHelper.getInstance(TravelCreateActivity.this).saveTravel
                            (travel).observeOn(AndroidSchedulers
                            .mainThread())
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
                                    Toast.makeText(TravelCreateActivity.this, t.getMessage(),
                                            Toast.LENGTH_LONG).show();
                                }
                            }));
                }
            }
        });

    }

    private void getTravel(String tripId) {
        mDisposable.add(DatabaseHelper.getInstance(this).getTravelByTrip(tripId).observeOn
                (AndroidSchedulers.mainThread()).subscribe(new Consumer<TravelWithConfig>() {
            @Override
            public void accept(TravelWithConfig result) throws Exception {
                travel = result;
                getCustomer(travel.getTrip().getCustomer_id());
                getProject(travel.getTrip().getProject_id());
                if (!TextUtils.isEmpty(travel.getTrip().getName()))
                    etName.setText(travel.getTrip().getName());
                if (!TextUtils.isEmpty(travel.getTravel().getInfo()))
                    etInfo.setText(travel.getTravel().getInfo());
                if (!TextUtils.isEmpty(travel.getTravel().getReason()))
                    etReason.setText(travel.getTravel().getReason());
                if (!TextUtils.isEmpty(travel.getTravel().getSupport()))
                    etSupport.setText(travel.getTravel().getSupport());
                if (!TextUtils.isEmpty(travel.getTravel().getCustomer_background()))
                    etCustomerBackground.setText(travel.getTravel().getCustomer_background());
                if (!TextUtils.isEmpty(travel.getTravel().getCustomer_tech()))
                    etCustomerTech.setText(travel.getTravel().getCustomer_tech());
                itemSelectTime.setStart(travel.getTrip().getFrom_date());
                itemSelectTime.setEnd(travel.getTrip().getTo_date());
                itemSelectLocation.setAddress(travel.getTravel().getAddress());
                itemSelectPeople.setParticipants(travel.getParticipants());
                itemSelectPhoto.setFiles(travel.getFiles());
                if (!TextUtils.isEmpty(travel.getTrip().getDescription()))
                    etNote.setText(travel.getTrip().getDescription());
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Toast.makeText(TravelCreateActivity.this, throwable.getMessage(), Toast
                        .LENGTH_LONG).show();
                finish();
            }
        }));
    }

    protected void getCustomer(String customerId) {
        if (TextUtils.isEmpty(customerId))
            return;
        mDisposable.add(DatabaseHelper.getInstance(this).getCustomerById(customerId).observeOn
                (AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Customer>() {
                    @Override
                    public void accept(Customer customer) throws Exception {
                        itemSelectCustomer.setCustomer(customer);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ErrorHandler.getInstance().setException(TravelCreateActivity.this,
                                throwable);
                    }
                }));
    }

    protected void getProject(String projectId) {
        if (TextUtils.isEmpty(projectId))
            return;
        mDisposable.add(DatabaseHelper.getInstance(this).getProjectById(projectId, TokenManager
                .getInstance()
                .getUser().getDepartment_id(), TokenManager.getInstance().getUser().getCompany_id
                ()).observeOn
                (AndroidSchedulers.mainThread()).subscribe(new Consumer<ProjectWithConfig>() {

            @Override
            public void accept(ProjectWithConfig projectWithConfig) throws
                    Exception {
                itemSelectProject.setProject(projectWithConfig);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                ErrorHandler.getInstance().setException(TravelCreateActivity.this,
                        throwable);
            }
        }));
    }

    protected boolean checkInput() {
        String required = getString(R.string.error_msg_required);
        if (TextUtils.isEmpty(etName.getText().toString())) {
            Toast.makeText(this, required + getString(R.string.production_name), Toast
                    .LENGTH_LONG).show();
            return false;
        }
        if (TextUtils.isEmpty(etInfo.getText().toString())) {
            Toast.makeText(this, required + getString(R.string.travel_info), Toast
                    .LENGTH_LONG).show();
            return false;
        }
        if (TextUtils.isEmpty(etReason.getText().toString())) {
            Toast.makeText(this, required + getString(R.string.travel_reason), Toast
                    .LENGTH_LONG).show();
            return false;
        }
        if (TextUtils.isEmpty(etSupport.getText().toString())) {
            Toast.makeText(this, required + getString(R.string.travel_support), Toast
                    .LENGTH_LONG).show();
            return false;
        }
        if (TextUtils.isEmpty(etCustomerBackground.getText().toString())) {
            Toast.makeText(this, required + getString(R.string.travel_customer_background), Toast
                    .LENGTH_LONG).show();
            return false;
        }
        if (TextUtils.isEmpty(etCustomerTech.getText().toString())) {
            Toast.makeText(this, required + getString(R.string.travel_customer_tech), Toast
                    .LENGTH_LONG).show();
            return false;
        }
        if (itemSelectLocation.getAddress().getCountry() == null) {
            Toast.makeText(this, R.string.error_msg_no_location, Toast.LENGTH_LONG).show();
            return false;
        }
        if (itemSelectCustomer.getCustomer() == null) {
            Toast.makeText(this, R.string.error_msg_no_customer, Toast.LENGTH_LONG).show();
            return false;
        }
        if (itemSelectProject.getProject() == null) {
            Toast.makeText(this, R.string.error_msg_no_project, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    @Override
    public void onSelectCountry() {
        Intent intent = new Intent(this, LocationSelectActivity.class);
        startActivityForResult(intent, MyApplication.REQUEST_SELECT_COUNTRY);
    }

    @Override
    public void onSelectCity(String countryId) {
        Intent intent = new Intent(this, LocationSelectActivity.class);
        intent.putExtra(LocationSelectActivity.KEY_COUNTRY, countryId);
        startActivityForResult(intent, MyApplication.REQUEST_SELECT_CITY);
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            itemSelectPhoto.onActivityResult(requestCode, resultCode, data);
            itemSelectCustomer.onActivityResult(requestCode, resultCode, data);
            itemSelectProject.onActivityResult(requestCode, resultCode, data);
            itemSelectPeople.onActivityResult(requestCode, resultCode, data);
            switch (requestCode) {
                // 選擇國家
                case MyApplication.REQUEST_SELECT_COUNTRY:
                    ConfigCountry config = gson.fromJson(data.getStringExtra(LocationSelectActivity.KEY_RESULT), ConfigCountry.class);
                    itemSelectLocation.selectCountry(config);
                    break;
                // 選擇城市
                case MyApplication.REQUEST_SELECT_CITY:
                    ConfigCity configCity = gson.fromJson(data.getStringExtra(LocationSelectActivity.KEY_RESULT), ConfigCity.class);
                    itemSelectLocation.selectCity(configCity);
                    break;
            }
        }
    }

    @Override
    public void onCustomerSelected(Customer customer) {
        updateName();
        if (customer.getAddress() != null)
            itemSelectLocation.setAddress(customer.getAddress());
    }

    protected void updateName() {
        StringBuilder builder = new StringBuilder();
        builder.append(getString(R.string.apply_travel));
        if (itemSelectCustomer.getCustomer() != null) {
            builder.append("-").append(itemSelectCustomer.getCustomer().getFull_name());
        }
        etName.setText(builder.toString());
    }
}
