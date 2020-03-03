package tw.com.masterhand.gmorscrm.activity.task;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tw.com.masterhand.gmorscrm.BaseUserCheckActivity;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.TokenManager;
import tw.com.masterhand.gmorscrm.api.ApiHelper;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.record.Customer;
import tw.com.masterhand.gmorscrm.room.record.Repayment;
import tw.com.masterhand.gmorscrm.room.record.Trip;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.tools.NumberFormater;
import tw.com.masterhand.gmorscrm.tools.TimeFormater;
import tw.com.masterhand.gmorscrm.view.Appbar;
import tw.com.masterhand.gmorscrm.view.CompanyCard;

public class RepaymentDetailActivity extends BaseUserCheckActivity {
    @BindView(R.id.appbar)
    Appbar appbar;
    @BindView(R.id.companyCard)
    protected CompanyCard companyCard;
    @BindView(R.id.tvTitle)
    protected TextView tvTitle;

    @BindView(R.id.tvType)
    protected TextView tvType;
    @BindView(R.id.tvAmount)
    protected TextView tvAmount;
    @BindView(R.id.tvDate)
    protected TextView tvDate;
    @BindView(R.id.tvApprovalDescription)
    protected TextView tvApprovalDescription;

    protected Repayment repayment;
    protected String viewerId;
    protected String tripId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repayment_detail);
        init();
    }

    @Override
    protected void onUserChecked() {
        appbar.invalidate();
        getData();
    }

    protected void init() {
        viewerId = TokenManager.getInstance().getUser().getId();
        Logger.e(TAG, "viewerId:" + viewerId);
        appbar.setTitle(getString(R.string.apply_repayment) + getString(R.string
                .title_activity_work_detail));

        tripId = getIntent().getStringExtra(MyApplication.INTENT_KEY_TRIP);
        if (TextUtils.isEmpty(tripId)) {
            finish();
        }
    }

    void getData() {
        if (TextUtils.isEmpty(tripId))
            return;
        Logger.i(TAG, "tripId:" + tripId);
        mDisposable.add(DatabaseHelper.getInstance(this).getRepaymentByTrip(tripId).observeOn
                (AndroidSchedulers.mainThread()).subscribe(new Consumer<Repayment>() {
            @Override
            public void accept(final Repayment result) throws Exception {
                repayment = result;
                updateDetail();
                getTrip();
                onDataLoaded();
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Toast.makeText(RepaymentDetailActivity.this, throwable.getMessage(), Toast
                        .LENGTH_LONG).show();
            }
        }));
    }

    protected void onDataLoaded() {
        // 由子層實作
    }

    private void updateDetail() {
        if (repayment.getType() != null)
            tvType.setText(repayment.getType().getTitle());
        if (repayment.getRepayment_date() != null)
            tvDate.setText(TimeFormater.getInstance().toDateFormat(repayment.getRepayment_date()));
        if (repayment.getAmount() > 0)
            tvAmount.setText(NumberFormater.getMoneyFormat(repayment.getAmount()));
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
                    ErrorHandler.getInstance().setException(RepaymentDetailActivity.this, e);
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                ErrorHandler.getInstance().setException(RepaymentDetailActivity.this, t);
            }
        });
    }

    private void getTrip() {
        mDisposable.add(DatabaseHelper.getInstance(this).getTripById(repayment.getTrip_id())
                .subscribe(new Consumer<Trip>() {

                    @Override
                    public void accept(Trip trip) throws Exception {
                        if (!TextUtils.isEmpty(trip.getName()))
                            tvTitle.setText(trip.getName());
                        getCustomer(trip.getCustomer_id());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                }));
    }

    private void getCustomer(String customerId) {
        if (TextUtils.isEmpty(customerId)) {
            Logger.e(TAG, "customerId is null");
            return;
        }
        mDisposable.add(DatabaseHelper.getInstance(this).getCustomerById(customerId).observeOn
                (AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Customer>() {
                    @Override
                    public void accept(Customer customer) throws Exception {
                        companyCard.setCustomer(customer);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ErrorHandler.getInstance().setException(RepaymentDetailActivity.this,
                                throwable);
                    }
                }));
    }

}
