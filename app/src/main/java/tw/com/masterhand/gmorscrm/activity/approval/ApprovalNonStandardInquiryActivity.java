package tw.com.masterhand.gmorscrm.activity.approval;

import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.TokenManager;
import tw.com.masterhand.gmorscrm.activity.task.NonStandardInquiryCreateActivity;
import tw.com.masterhand.gmorscrm.activity.task.NonStandardInquiryDetailActivity;
import tw.com.masterhand.gmorscrm.enums.ApprovalStatus;
import tw.com.masterhand.gmorscrm.fragments.ApprovalFragment;
import tw.com.masterhand.gmorscrm.model.Approval;
import tw.com.masterhand.gmorscrm.model.NonStandardInquiryWithConfig;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.tools.Checker;
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.view.ItemApproval;

public class ApprovalNonStandardInquiryActivity extends NonStandardInquiryDetailActivity
        implements ItemApproval
        .OnApprovalSelectListener {
    Approval approval;

    @Override
    protected void getData() {
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
        inquiry = new NonStandardInquiryWithConfig(approval.getNonStandardInquiry());
        inquiry.setTrip(approval.getTrip());
        mDisposable.add(DatabaseHelper.getInstance(this).getNonStandardInquiryById(approval
                .getNonStandardInquiry().getId()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<NonStandardInquiryWithConfig>() {

                    @Override
                    public void accept(NonStandardInquiryWithConfig nonStandardInquiryWithConfig)
                            throws Exception {
                        inquiry.setFiles(nonStandardInquiryWithConfig.getFiles());
                        inquiry.setProducts(nonStandardInquiryWithConfig.getProducts());
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
        Intent intent = new Intent(this, NonStandardInquiryCreateActivity.class);
        intent.putExtra(MyApplication.INTENT_KEY_TRIP, inquiry.getInquiry().getTrip_id());
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
