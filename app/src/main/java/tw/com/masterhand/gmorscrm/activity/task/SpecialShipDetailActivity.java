package tw.com.masterhand.gmorscrm.activity.task;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tw.com.masterhand.gmorscrm.ApprovalListActivity;
import tw.com.masterhand.gmorscrm.BaseUserCheckActivity;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.TokenManager;
import tw.com.masterhand.gmorscrm.activity.news.SaleResourceActivity;
import tw.com.masterhand.gmorscrm.api.ApiHelper;
import tw.com.masterhand.gmorscrm.enums.ApprovalStatus;
import tw.com.masterhand.gmorscrm.enums.SubmitStatus;
import tw.com.masterhand.gmorscrm.model.MultipleSubmit;
import tw.com.masterhand.gmorscrm.model.SpecialShipWithConfig;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.record.Customer;
import tw.com.masterhand.gmorscrm.room.record.File;
import tw.com.masterhand.gmorscrm.room.setting.Admin;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.tools.NumberFormater;
import tw.com.masterhand.gmorscrm.tools.TimeFormater;
import tw.com.masterhand.gmorscrm.view.Appbar;
import tw.com.masterhand.gmorscrm.view.CompanyCard;
import tw.com.masterhand.gmorscrm.view.Dropdown;
import tw.com.masterhand.gmorscrm.view.ItemApproval;

public class SpecialShipDetailActivity extends BaseUserCheckActivity {
    @BindView(R.id.appbar)
    Appbar appbar;
    @BindView(R.id.companyCard)
    protected CompanyCard companyCard;
    @BindView(R.id.tvTitle)
    protected TextView tvTitle;

    @BindView(R.id.tvDate)
    TextView tvDate;
    @BindView(R.id.tvShipAgain)
    TextView tvShipAgain;
    @BindView(R.id.tvAmount)
    TextView tvAmount;
    @BindView(R.id.tvCode)
    TextView tvCode;
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

    @BindView(R.id.tvNote)
    protected TextView tvNote;
    @BindView(R.id.tvAssistant)
    protected TextView tvAssistant;
    @BindView(R.id.tvApprovalDescription)
    protected TextView tvApprovalDescription;
    @BindView(R.id.btnApprover)
    protected Button btnApprover;
    @BindView(R.id.container_photo)
    protected LinearLayout containerPhoto;
    @BindView(R.id.itemApproval)
    protected ItemApproval itemApproval;

    protected SpecialShipWithConfig specialShip;
    protected String viewerId;
    protected String tripId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_special_ship_detail);
        init();
    }

    @Override
    protected void onUserChecked() {
        appbar.invalidate();
        viewerId = TokenManager.getInstance().getUser().getId();
        Logger.e(TAG, "viewerId:" + viewerId);
        getData();
    }

    protected void init() {

        appbar.setTitle(getString(R.string.apply_special_ship) + getString(R.string
                .title_activity_work_detail));

        tripId = getIntent().getStringExtra(MyApplication.INTENT_KEY_TRIP);
        if (TextUtils.isEmpty(tripId)) {
            finish();
        }
    }

    protected void getData() {
        if (TextUtils.isEmpty(tripId))
            return;
        Logger.i(TAG, "tripId:" + tripId);
        mDisposable.add(DatabaseHelper.getInstance(this).getSpecialShipByTrip(tripId).observeOn
                (AndroidSchedulers.mainThread()).subscribe(new Consumer<SpecialShipWithConfig>() {
            @Override
            public void accept(SpecialShipWithConfig result) throws Exception {
                specialShip = result;
                /*2018-1-17新增:只有建立者可以修改*/
                /*2018-2-8新增:未提交項目才可以修改*/
                if (TokenManager.getInstance().getUser().getId().equals(result.getTrip()
                        .getUser_id()) && result.getTrip().getSubmit() == SubmitStatus.NONE) {
                    appbar.removeFuction();
                    appbar.addFunction(getString(R.string.submit), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            submit();
                        }
                    });
                    appbar.addFunction(R.mipmap.common_edit, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(view.getContext(), SpecialShipCreateActivity
                                    .class);
                            intent.putExtra(MyApplication.INTENT_KEY_TRIP, specialShip
                                    .getSpecialShip()
                                    .getTrip_id());
                            startActivity(intent);
                        }
                    });
                }
                updateDetail();
                updatePhoto(specialShip.getFiles());
                getCustomer();
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Toast.makeText(SpecialShipDetailActivity.this, throwable.getMessage(), Toast
                        .LENGTH_LONG).show();
            }
        }));
        ApiHelper.getInstance().getTripApi().getApprovalDescription(TokenManager.getInstance()
                .getToken(), tripId).enqueue(new Callback<JSONObject>() {

            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                try {
                    JSONObject result = response.body();
                    int success = result.getInt("success");
                    if (success == 1) {
                        String description = result.getString("description");
                        if (!TextUtils.isEmpty(description))
                            tvApprovalDescription.setText(description);
                    }
                } catch (Exception e) {
                    ErrorHandler.getInstance().setException(SpecialShipDetailActivity.this, e);
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                ErrorHandler.getInstance().setException(SpecialShipDetailActivity.this, t);
            }
        });
    }

    protected void submit() {
        final MultipleSubmit submit = new MultipleSubmit();
        submit.setTrip_ids(specialShip.getSpecialShip().getTrip_id());
        ApiHelper.getInstance().getSubmitApi().submit(TokenManager
                .getInstance().getToken(), submit).enqueue(new Callback<JSONObject>() {

            @Override
            public void onResponse(Call<JSONObject> call,
                                   Response<JSONObject> response) {
                try {
                    switch (response.code()) {
                        case 200:
                            JSONObject result = response.body();
                            int success = result.getInt("success");
                            if (success == 1) {
                                DatabaseHelper.getInstance
                                        (SpecialShipDetailActivity.this)
                                        .saveTripSubmit(submit).observeOn
                                        (AndroidSchedulers.mainThread())
                                        .subscribe(new CompletableObserver() {

                                            @Override
                                            public void onSubscribe
                                                    (Disposable d) {
                                                mDisposable.add(d);
                                            }

                                            @Override
                                            public void onComplete() {
                                                setResult(ApprovalStatus.UNSIGN.getCode());
                                                finish();
                                            }

                                            @Override
                                            public void onError(Throwable e) {
                                                Toast.makeText
                                                        (SpecialShipDetailActivity
                                                                        .this,
                                                                e.getMessage(),
                                                                Toast.LENGTH_LONG)
                                                        .show();
                                            }
                                        });
                            } else {
                                Toast.makeText(SpecialShipDetailActivity.this,
                                        result.getString("errorMsg"),
                                        Toast.LENGTH_LONG).show();
                            }
                            break;
                        case ApiHelper.ERROR_TOKEN_EXPIRED:
                            showLoginDialog();
                            break;
                        default:
                            Logger.e(TAG, "submit failed");
                    }
                } catch (Exception e) {
                    ErrorHandler.getInstance().setException
                            (SpecialShipDetailActivity.this, e);
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                Toast.makeText(SpecialShipDetailActivity.this, t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    @OnClick(R.id.btnApprover)
    void showApproval() {
        Intent intent = new Intent(this, ApprovalListActivity.class);
        intent.putExtra(MyApplication.INTENT_KEY_TRIP, specialShip.getSpecialShip().getTrip_id());
        startActivity(intent);
    }

    @OnClick(R.id.btnResource)
    void showResource() {
        Intent intent = new Intent(this, SaleResourceActivity.class);
        intent.putExtra(MyApplication.INTENT_KEY_ID, specialShip.getSpecialShip().getTrip_id());
        startActivity(intent);
    }

    void updatePhoto(List<File> files) {
        containerPhoto.removeAllViews();
        for (File file : files) {
            containerPhoto.addView(generatePhotoItem(file));
        }
        if (containerPhoto.getChildCount() == 0)
            containerPhoto.addView(getEmptyTextView(getString(R.string.error_msg_no_photo)));
    }

    public void updateDetail() {
        String[] have = getResources().getStringArray(R.array.have);
        if (!TextUtils.isEmpty(specialShip.getTrip().getName()))
            tvTitle.setText(specialShip.getTrip().getName());
        if (specialShip.getSpecialShip().getRepayment_date() != null)
            tvDate.setText(TimeFormater.getInstance().toDateFormat(specialShip
                    .getSpecialShip().getRepayment_date()));
        if (specialShip.getSpecialShip().getShip_again() != Dropdown.VALUE_EMPTY)
            tvShipAgain.setText(have[specialShip.getSpecialShip().getShip_again()]);
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
        tvLessThan500.setText(have[specialShip.getSpecialShip().getLess_than_500()]);
        tvTotal.setText(NumberFormater.getMoneyFormat(specialShip.getSpecialShip()
                .getTotal()));
        tvExceedQuota.setText(NumberFormater.getMoneyFormat(specialShip.getSpecialShip()
                .getExceed_quota()));

        mDisposable.add(DatabaseHelper.getInstance(this)
                .getAdminById(specialShip.getTrip().getAssistant_id())
                .observeOn(AndroidSchedulers.mainThread
                        ()).subscribe(new Consumer<Admin>() {

                    @Override
                    public void accept(Admin config) throws Exception {
                        tvAssistant.setText(config.getName());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        tvAssistant.setText(R.string.empty_show);
                    }
                }));
        if (!TextUtils.isEmpty(specialShip.getTrip().getDescription()))
            tvNote.setText(specialShip.getTrip().getDescription());
    }

    public void getCustomer() {
        mDisposable.add(DatabaseHelper.getInstance(this).getCustomerById(specialShip.getTrip()
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
                        ErrorHandler.getInstance().setException(SpecialShipDetailActivity.this,
                                throwable);
                    }
                }));
    }

}
