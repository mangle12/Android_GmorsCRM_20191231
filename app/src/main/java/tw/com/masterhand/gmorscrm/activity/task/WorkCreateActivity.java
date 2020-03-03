package tw.com.masterhand.gmorscrm.activity.task;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import tw.com.masterhand.gmorscrm.BaseUserCheckActivity;
import tw.com.masterhand.gmorscrm.LocationSelectActivity;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.TokenManager;
import tw.com.masterhand.gmorscrm.enums.AlertType;
import tw.com.masterhand.gmorscrm.model.ProjectWithConfig;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.record.Customer;
import tw.com.masterhand.gmorscrm.room.record.Trip;
import tw.com.masterhand.gmorscrm.room.setting.ConfigCity;
import tw.com.masterhand.gmorscrm.room.setting.ConfigCountry;
import tw.com.masterhand.gmorscrm.tools.AlarmHelper;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.view.AppbarEdit;
import tw.com.masterhand.gmorscrm.view.EditTextWithTitle;
import tw.com.masterhand.gmorscrm.view.ItemSelectCustomer;
import tw.com.masterhand.gmorscrm.view.ItemSelectLocation;
import tw.com.masterhand.gmorscrm.view.ItemSelectPeople;
import tw.com.masterhand.gmorscrm.view.ItemSelectPhoto;
import tw.com.masterhand.gmorscrm.view.ItemSelectProject;
import tw.com.masterhand.gmorscrm.view.ItemSelectTime;

abstract public class WorkCreateActivity extends BaseUserCheckActivity implements ItemSelectLocation.OnSelectListener {
    @BindView(R.id.appbar)
    AppbarEdit appbar;
    @BindView(R.id.itemSelectCustomer)
    ItemSelectCustomer itemSelectCustomer;
    @BindView(R.id.itemSelectProject)
    ItemSelectProject itemSelectProject;
    @BindView(R.id.itemSelectLocation)
    ItemSelectLocation itemSelectLocation;
    @BindView(R.id.itemSelectTime)
    ItemSelectTime itemSelectTime;
    @BindView(R.id.itemSelectPeople)
    ItemSelectPeople itemSelectPeople;
    @BindView(R.id.itemSelectPhoto)
    ItemSelectPhoto itemSelectPhoto;
    @BindView(R.id.containerReport)
    LinearLayout containerReport;
    @BindView(R.id.etReport)
    EditText etReport;
    @BindView(R.id.etNote)
    EditText etNote;
    @BindView(R.id.etName)
    EditTextWithTitle etName;
    @BindView(R.id.btnVisitType)
    Button btnVisitType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_create);
        itemSelectLocation.setOnSelectListener(this);
        init();
    }

    abstract protected void init();

    @Override
    protected void onUserChecked() {
        appbar.invalidate();
    }

    abstract protected void save();

    protected void hideReport() {
        containerReport.setVisibility(View.GONE);
    }

    protected void setAlarm(final Trip trip) {
        startProgressDialog();
        /*先取消舊的鬧鐘*/
        AlarmHelper.cancelReserveAlarm(this, trip);
        if (itemSelectTime.getAlertType() != AlertType.ALERT_NO) {
            /*在設定新的鬧鐘*/
            mDisposable.add(DatabaseHelper.getInstance(this).getLastClockId()
                    .subscribe(new Consumer<Integer>() {
                        @Override
                        public void accept(Integer result) throws Exception {
                            trip.setClock_id(result + 1);
                            AlarmHelper.setReserveAlarm(getApplicationContext(), trip);
                            save();
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Logger.e(TAG, "onError");
                            stopProgressDialog();
                        }
                    }));
        } else {
            save();
        }
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
        if (resultCode == RESULT_OK) {
            itemSelectCustomer.onActivityResult(requestCode, resultCode, data);
            itemSelectProject.onActivityResult(requestCode, resultCode, data);
            itemSelectPeople.onActivityResult(requestCode, resultCode, data);
            itemSelectPhoto.onActivityResult(requestCode, resultCode, data);
            switch (requestCode) {
                // 選擇客戶
                case MyApplication.REQUEST_SELECT_CUSTOMER:
                    String customerId = data.getStringExtra(MyApplication.INTENT_KEY_CUSTOMER);
                    if (!TextUtils.isEmpty(customerId)) {
                        mDisposable.add(DatabaseHelper.getInstance(this).getCustomerById
                                (customerId).observeOn
                                (AndroidSchedulers.mainThread()).subscribe(new Consumer<Customer>
                                () {

                            @Override
                            public void accept(Customer customer) throws Exception {
                                itemSelectLocation.setAddress(customer.getAddress());
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                ErrorHandler.getInstance().setException(WorkCreateActivity.this,
                                        throwable);
                            }
                        }));
                    }
                    break;
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
        super.onActivityResult(requestCode, resultCode, data);
    }

    protected void getCustomer(String customerId) {
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
                        ErrorHandler.getInstance().setException(WorkCreateActivity.this, throwable);
                    }
                }));
    }

    protected void getProject(String projectId) {
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
                ErrorHandler.getInstance().setException(WorkCreateActivity.this,
                        throwable);
            }
        }));
    }

}
