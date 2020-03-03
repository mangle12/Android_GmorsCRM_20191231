package tw.com.masterhand.gmorscrm.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tw.com.masterhand.gmorscrm.LoginActivity;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.api.ApiHelper;
import tw.com.masterhand.gmorscrm.enums.ApprovalStatus;
import tw.com.masterhand.gmorscrm.model.Approve;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.tools.TimeFormater;
import tw.com.masterhand.gmorscrm.model.Approval;
import tw.com.masterhand.gmorscrm.tools.UnitChanger;

public class ApprovalFragment extends DialogFragment {
    String TAG = getClass().getSimpleName();
    String tripId;
    ApprovalStatus status;
    String token;
    public SweetAlertDialog progressDialog;

    public static ApprovalFragment newInstance(String token, String tripId, ApprovalStatus
            status) {
        ApprovalFragment f = new ApprovalFragment();
        Bundle args = new Bundle();
        args.putString(MyApplication.INTENT_KEY_TRIP, tripId);
        args.putString(MyApplication.INTENT_KEY_TOKEN, token);
        args.putInt(MyApplication.INTENT_KEY_TYPE, status.getCode());
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tripId = getArguments().getString(MyApplication.INTENT_KEY_TRIP);
        status = ApprovalStatus.getStatusByCode(getArguments().getInt(MyApplication
                .INTENT_KEY_TYPE));
        token = getArguments().getString(MyApplication.INTENT_KEY_TOKEN);
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup
                .LayoutParams.WRAP_CONTENT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_approval, container, false);
        TextView tvTitle = v.findViewById(R.id.tvTitle);
        final EditText etReason = v.findViewById(R.id.etReason);
        v.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getDialog() != null)
                    getDialog().dismiss();
            }
        });
        v.findViewById(R.id.btnConfirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String reason = etReason.getText().toString();
                Approve approve = new Approve();
                approve.setTrip_id(tripId);
                approve.setApproval(status);
                approve.setReason(reason);
                approve(approve);
            }
        });
        String title = getString(R.string.approval);
        if (status == ApprovalStatus.AGREE) {
            title += getString(R.string.agree);
        } else {
            title += getString(R.string.disagree);
        }
        tvTitle.setText(title);
        return v;
    }

    private void approve(final Approve approve) {
        startProgressDialog();
        Logger.e(TAG, "send approve:" + new Gson().toJson(approve));
        Call<JSONObject> call = ApiHelper.getInstance().getApprovalApi().executeApprove(token,
                approve);
        call.enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                switch (response.code()) {
                    case 200:
                        stopProgressDialog();
                        try {
                            Logger.e(TAG, "response:" + response.body());
                            int success = response.body().getInt("success");
                            if (success == 1) {
                                Logger.e(TAG, "response:" + response.body().toString());
                                if (getDialog() != null)
                                    getDialog().dismiss();
                                getActivity().setResult(approve.getApproval().getCode());
                                getActivity().finish();
                            } else {
                                onApproveFailed("errorMsg:" + response.body().getString
                                        ("errorMsg"));
                            }
                        } catch (Exception e) {
                            onApproveFailed("Exception:" + e.getMessage());
                        }
                        break;
                    default:
                        onApproveFailed(response.code() + ":" + response.message());
                        break;
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                onApproveFailed(t.getMessage());
                ErrorHandler.getInstance().setException(getActivity(), t);
            }
        });
    }

    private void onApproveFailed(String msg) {
        stopProgressDialog();
        if (!TextUtils.isEmpty(msg)) {
            Logger.e(TAG, "onApproveFailed:" + msg);
            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
        }
        if (getDialog() != null)
            getDialog().dismiss();
    }

    protected void startProgressDialog() {
        try {
            if (progressDialog == null) {
                progressDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog
                        .PROGRESS_TYPE);
                progressDialog.setTitleText(getString(R.string.progress_msg));
                progressDialog.setCancelable(false);
            }
            if (!progressDialog.isShowing())
                progressDialog.show();
        } catch (Exception e) {
            Logger.e(TAG, "Exception:" + e.getMessage());
        }
    }

    protected void stopProgressDialog() {
        if (progressDialog != null) {
            try {
                progressDialog.dismiss();
            } finally {
                progressDialog = null;
            }
        }
    }
}
