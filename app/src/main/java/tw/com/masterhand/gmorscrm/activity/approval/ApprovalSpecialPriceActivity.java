package tw.com.masterhand.gmorscrm.activity.approval;

import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.TokenManager;
import tw.com.masterhand.gmorscrm.activity.task.SpecialPriceCreateActivity;
import tw.com.masterhand.gmorscrm.activity.task.SpecialPriceDetailActivity;
import tw.com.masterhand.gmorscrm.enums.ApprovalStatus;
import tw.com.masterhand.gmorscrm.fragments.ApprovalFragment;
import tw.com.masterhand.gmorscrm.model.Approval;
import tw.com.masterhand.gmorscrm.model.SpecialPriceWithConfig;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.tools.Checker;
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.view.ItemApproval;

public class ApprovalSpecialPriceActivity extends SpecialPriceDetailActivity implements ItemApproval
        .OnApprovalSelectListener {
    Approval approval;

    @Override
    public void getData() {
        itemApproval.setVisibility(View.VISIBLE);
        approval = gson.fromJson(getIntent().getStringExtra(MyApplication
                .INTENT_KEY_APPROVAL), Approval.class);
        boolean showStatus = getIntent().getBooleanExtra(MyApplication.INTENT_KEY_ENABLE, false);
        Logger.e(TAG, "showStatus:" + showStatus);
        if (showStatus) {
            itemApproval.setStatus(approval.getApproval(), this);
        } else {
            itemApproval.setApproval(approval, this);
        }
        specialPrice = new
                SpecialPriceWithConfig(approval
                .getSpecialPrice());
        specialPrice.setTrip(approval.getTrip());
        mDisposable.add(DatabaseHelper.getInstance(this).getSpecialPriceById(approval
                .getSpecialPrice().getTrip_id()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<SpecialPriceWithConfig>() {

                    @Override
                    public void accept(SpecialPriceWithConfig specialPriceWithConfig) throws
                            Exception {
                        specialPrice.setFiles(specialPriceWithConfig.getFiles());
                        specialPrice.setProducts(specialPriceWithConfig.getProducts());
                        updateDetail();
                        getCustomer();
                        getProject();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        updateDetail();
                        getCustomer();
                        getProject();
                    }
                }));

    }

    @Override
    public void onApprovalSelect(String tripId, ApprovalStatus status) {
        ApprovalFragment.newInstance(TokenManager.getInstance().getToken(), tripId, status).show
                (getFragmentManager(), MyApplication
                        .INTENT_KEY_APPROVAL);
    }

    @Override
    public void resend() {
        if (!Checker.isNetworkConnected(this)) {
            Toast.makeText(this, R.string.error_msg_no_network, Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(this, SpecialPriceCreateActivity.class);
        intent.putExtra(MyApplication.INTENT_KEY_TRIP, specialPrice.getSpecialPrice().getTrip_id());
        startActivityForResult(intent, MyApplication.REQUEST_EDIT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case MyApplication.REQUEST_EDIT:
                    submit();
                    break;
                default:

            }
        }
    }
}
