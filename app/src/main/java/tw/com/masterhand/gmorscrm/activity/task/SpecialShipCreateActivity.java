package tw.com.masterhand.gmorscrm.activity.task;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import tw.com.masterhand.gmorscrm.BaseUserCheckActivity;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.TokenManager;
import tw.com.masterhand.gmorscrm.model.SpecialShipWithConfig;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.record.Customer;
import tw.com.masterhand.gmorscrm.room.setting.Admin;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.tools.NumberFormater;
import tw.com.masterhand.gmorscrm.tools.TimeFormater;
import tw.com.masterhand.gmorscrm.view.AppbarEdit;
import tw.com.masterhand.gmorscrm.view.Dropdown;
import tw.com.masterhand.gmorscrm.view.ItemSelectCustomer;
import tw.com.masterhand.gmorscrm.view.ItemSelectPhoto;

public class SpecialShipCreateActivity extends BaseUserCheckActivity {
    @BindView(R.id.appbar)
    AppbarEdit appbar;
    @BindView(R.id.itemSelectCustomer)
    ItemSelectCustomer itemSelectCustomer;

    @BindView(R.id.tvDate)
    TextView tvDate;
    @BindView(R.id.dropdownShipAgain)
    Dropdown dropdownShipAgain;
    @BindView(R.id.tvCode)
    TextView tvCode;
    @BindView(R.id.tvAmount)
    TextView tvAmount;
    @BindView(R.id.tvNotBillAmount)
    TextView tvNotBillAmount;
    @BindView(R.id.tvNotExpireAmount)
    TextView tvNotExpireAmount;
    @BindView(R.id.tvExpireAmount)
    TextView tvExpireAmount;
    @BindView(R.id.tvExpireDay)
    TextView tvExpireDay;
    @BindView(R.id.tvBiggestExpireDay)
    TextView tvBiggestExpireDay;
    @BindView(R.id.tvPrepayment)
    TextView tvPrepayment;
    @BindView(R.id.tvCredit)
    TextView tvCredit;
    @BindView(R.id.tvCreditRisk)
    TextView tvCreditRisk;
    @BindView(R.id.tvLessThan500)
    TextView tvLessThan500;
    @BindView(R.id.tvTotal)
    TextView tvTotal;
    @BindView(R.id.tvExceedQuota)
    TextView tvExceedQuota;

    @BindView(R.id.dropdownAssistant)
    Dropdown dropdownAssistant;
    @BindView(R.id.itemSelectPhoto)
    ItemSelectPhoto itemSelectPhoto;
    @BindView(R.id.etNote)
    EditText etNote;

    SpecialShipWithConfig specialShip;

    List<Admin> adminList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_special_ship_create);
        init();
    }

    @Override
    protected void onUserChecked() {
        appbar.invalidate();
        if (specialShip != null)
            return;
        String tripId = getIntent().getStringExtra(MyApplication.INTENT_KEY_TRIP);
        if (TextUtils.isEmpty(tripId)) {
            finish();
        } else {
            /*編輯特批發貨*/
            getSpecialShip(tripId);
        }

    }

    protected void init() {
        itemSelectCustomer.setShouldCheck(true);
        itemSelectCustomer.disableSelectCustomer();

        appbar.setTitle(getString(R.string.main_menu_new_task) + "/" + getString(R.string
                .apply_special_ship));
        appbar.setCompleteListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInput()) {
                    startProgressDialog();
                    specialShip.getSpecialShip().setShip_again(dropdownShipAgain.getSelected());
                    if (dropdownAssistant.getSelected() != Dropdown.VALUE_EMPTY)
                        specialShip.getTrip().setAssistant_id(adminList.get(dropdownAssistant
                                .getSelected()).getId());
                    specialShip.setFiles(itemSelectPhoto.getFiles());
                    specialShip.getTrip().setDescription(etNote.getText().toString());
                    mDisposable.add(DatabaseHelper.getInstance(SpecialShipCreateActivity.this)
                            .saveSpecialShip
                                    (specialShip).observeOn(AndroidSchedulers
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
                                    Toast.makeText(SpecialShipCreateActivity.this, t.getMessage(),
                                            Toast.LENGTH_LONG).show();
                                }
                            }));
                }
            }
        });

    }

    private void getSpecialShip(String tripId) {
        mDisposable.add(DatabaseHelper.getInstance(this).getSpecialShipByTrip(tripId).observeOn
                (AndroidSchedulers.mainThread()).subscribe(new Consumer<SpecialShipWithConfig>() {
            @Override
            public void accept(SpecialShipWithConfig result) throws Exception {
                specialShip = result;
                getCustomer(specialShip.getTrip().getCustomer_id());
                if (specialShip.getSpecialShip().getRepayment_date() != null)
                    tvDate.setText(TimeFormater.getInstance().toDateFormat(specialShip
                            .getSpecialShip().getRepayment_date()));
                if (specialShip.getSpecialShip().getShip_again() != Dropdown.VALUE_EMPTY)
                    dropdownShipAgain.select(specialShip.getSpecialShip().getShip_again());
                if (!TextUtils.isEmpty(specialShip.getSpecialShip().getShip_code()))
                    tvCode.setText(specialShip.getSpecialShip().getShip_code());
                if (specialShip.getSpecialShip().getShip_amount() > 0)
                    tvAmount.setText(NumberFormater.getMoneyFormat(specialShip.getSpecialShip()
                            .getShip_amount()));
                if (specialShip.getSpecialShip().not_billing_amount > 0)
                    tvNotBillAmount.setText(NumberFormater.getMoneyFormat(specialShip
                            .getSpecialShip().getNot_billing_amount()));
                if (specialShip.getSpecialShip().getNot_expire_amount() > 0)
                    tvNotExpireAmount.setText(NumberFormater.getMoneyFormat(specialShip
                            .getSpecialShip().getNot_expire_amount()));
                tvExpireDay.setText(String.valueOf(specialShip.getSpecialShip().getExpire_day()));
                if (specialShip.getSpecialShip().getExpire_amount() > 0)
                    tvExpireAmount.setText(NumberFormater.getMoneyFormat(specialShip
                            .getSpecialShip().getExpire_amount()));
                tvBiggestExpireDay.setText(String.valueOf(specialShip.getSpecialShip()
                        .getBiggest_expire_day()));
                if (specialShip.getSpecialShip().getPrepayment() > 0)
                    tvPrepayment.setText(NumberFormater.getMoneyFormat(specialShip.getSpecialShip
                            ().getPrepayment()));
                if (specialShip.getSpecialShip().getCredit() > 0)
                    tvCredit.setText(NumberFormater.getMoneyFormat(specialShip.getSpecialShip()
                            .getPrepayment()));
                if (specialShip.getSpecialShip().getCredit_risk() > 0)
                    tvCreditRisk.setText(NumberFormater.getMoneyFormat(specialShip.getSpecialShip
                            ().getCredit_risk()));
                String[] have = getResources().getStringArray(R.array.have);
                tvLessThan500.setText(have[specialShip.getSpecialShip().getLess_than_500()]);
                tvTotal.setText(NumberFormater.getMoneyFormat(specialShip.getSpecialShip()
                        .getTotal()));
                tvExceedQuota.setText(NumberFormater.getMoneyFormat(specialShip.getSpecialShip()
                        .getExceed_quota()));
                itemSelectPhoto.setFiles(specialShip.getFiles());
                if (!TextUtils.isEmpty(specialShip.getTrip().getDescription()))
                    etNote.setText(specialShip.getTrip().getDescription());
                getAssistant();
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Toast.makeText(SpecialShipCreateActivity.this, throwable.getMessage(), Toast
                        .LENGTH_LONG).show();
                finish();
            }
        }));
    }


    protected void getAssistant() {
        mDisposable.add(DatabaseHelper.getInstance(this).getAdmin().observeOn(AndroidSchedulers
                .mainThread()).toList().subscribe(new Consumer<List<Admin>>() {
            @Override
            public void accept(List<Admin> admins) throws Exception {
                adminList = admins;
                if (admins.size() > 0) {
                    String assistantId = TokenManager.getInstance().getUser().getAssistant_id();
                    if (!TextUtils.isEmpty(specialShip.getTrip().getAssistant_id())) {
                        assistantId = specialShip.getTrip().getAssistant_id();
                    }
                    int select = -1;
                    String[] items = new String[admins.size()];
                    for (int i = 0; i < items.length; i++) {
                        items[i] = admins.get(i).getName();
                        if (admins.get(i).getId().equals(assistantId)) {
                            select = i;
                        }
                    }
                    dropdownAssistant.setItems(items);
                    dropdownAssistant.select(select);
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                ErrorHandler.getInstance().setException(SpecialShipCreateActivity.this, throwable);
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
                        ErrorHandler.getInstance().setException(SpecialShipCreateActivity.this,
                                throwable);
                    }
                }));
    }

    protected boolean checkInput() {
        String required = getString(R.string.error_msg_required);
        if (itemSelectCustomer.getCustomer() == null) {
            Toast.makeText(this, R.string.error_msg_no_customer, Toast.LENGTH_LONG).show();
            return false;
        }
        if (specialShip.getSpecialShip().getRepayment_date() == null) {
            Toast.makeText(this, required + getString(R.string.special_ship_repayment_date),
                    Toast.LENGTH_LONG).show();
            return false;
        }
        if (dropdownShipAgain.getSelected() == Dropdown.VALUE_EMPTY) {
            Toast.makeText(this, required + getString(R.string.special_ship_ship_again),
                    Toast.LENGTH_LONG).show();
            return false;
        }
        if (dropdownAssistant.getSelected() == Dropdown.VALUE_EMPTY) {
            Toast.makeText(this, required + getString(R.string.assistant),
                    Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    @OnClick(R.id.tvDate)
    void changeDate() {
        final Calendar toBeChanged = Calendar.getInstance(Locale.getDefault());
        if (specialShip.getSpecialShip().getRepayment_date() != null)
            toBeChanged.setTime(specialShip.getSpecialShip().getRepayment_date());
        DatePickerDialog datePicker = new DatePickerDialog(this, new DatePickerDialog
                .OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                toBeChanged.set(Calendar.YEAR, year);
                toBeChanged.set(Calendar.MONTH, month);
                toBeChanged.set(Calendar.DAY_OF_MONTH, day);
                specialShip.getSpecialShip().setRepayment_date(toBeChanged.getTime());
                tvDate.setText(TimeFormater.getInstance().toDateFormat(specialShip.getSpecialShip
                        ().getRepayment_date()));
                tvDate.setTextColor(ContextCompat.getColor(SpecialShipCreateActivity.this, R
                        .color.orange));
            }
        }, toBeChanged.get(Calendar.YEAR), toBeChanged.get(Calendar.MONTH), toBeChanged.get
                (Calendar.DAY_OF_MONTH));
        datePicker.show();
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            itemSelectCustomer.onActivityResult(requestCode, resultCode, data);
            itemSelectPhoto.onActivityResult(requestCode, resultCode, data);
        }
    }

}
