package tw.com.masterhand.gmorscrm.activity.approval;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tw.com.masterhand.gmorscrm.BaseWebCheckActivity;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.TokenManager;
import tw.com.masterhand.gmorscrm.api.ApiHelper;
import tw.com.masterhand.gmorscrm.enums.ApprovalStatus;
import tw.com.masterhand.gmorscrm.enums.TripType;
import tw.com.masterhand.gmorscrm.model.Approval;
import tw.com.masterhand.gmorscrm.model.Approve;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.setting.ConfigCompanyHiddenField;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.tools.GsonGMTDateAdapter;
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.view.Appbar;

public class ApprovalMenuActivity extends BaseWebCheckActivity {

    @BindView(R.id.appbar)
    Appbar appbar;
    @BindView(R.id.container_work)
    LinearLayout container_work;
    @BindView(R.id.container_apply)
    LinearLayout container_apply;
    @BindView(R.id.tvTitleApply)
    TextView tvTitleApply;
    @BindView(R.id.tvTitleWork)
    TextView tvTitleWork;

    boolean isRecordMode;

    List<TripType> hiddenTripType;

    List<Approval> absentList, reimbursementList, contractList, quotationList, sampleList,
            specialPriceList, productionList, nonStandardInquiryList, springRingInquiryList,
            expressList, travelList, specialShipList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approval_menu);
        isRecordMode = getIntent().getBooleanExtra(MyApplication.INTENT_KEY_MODE, false);
        Logger.e(TAG, "isRecordMode:" + isRecordMode);
        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        startProgressDialog();
    }

    @Override
    protected void onUserChecked() {
        appbar.invalidate();
        getHiddenField();
    }

    private void init() {
        gson = new GsonBuilder().registerTypeAdapter(Date.class, new GsonGMTDateAdapter()).create();

        if (isRecordMode) {
            appbar.setTitle(getString(R.string.main_menu_approval_record));
        } else {
            appbar.setTitle(getString(R.string.main_menu_approval_sign));
        }
    }

    protected void getHiddenField() {
        hiddenTripType = new ArrayList<>();
        mDisposable.add(DatabaseHelper.getInstance(this).getConfigCompanyHiddenField(TokenManager.getInstance().getUser().getCompany_id())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ConfigCompanyHiddenField>() {

            @Override
            public void accept(ConfigCompanyHiddenField configCompanyHiddenField) throws Exception {
                if (configCompanyHiddenField.getAdd_trip_hidden_absent()) {
                    hiddenTripType.add(TripType.ABSENT);
                }
                if (configCompanyHiddenField.getAdd_trip_hidden_contract()) {
                    hiddenTripType.add(TripType.CONTRACT);
                }
                if (configCompanyHiddenField.getAdd_trip_hidden_express()) {
                    hiddenTripType.add(TripType.EXPRESS);
                }
                if (configCompanyHiddenField.getAdd_trip_hidden_new_project_production()) {
                    hiddenTripType.add(TripType.PRODUCTION);
                }
                if (configCompanyHiddenField.getAdd_trip_hidden_nonstandard_inquiry()) {
                    hiddenTripType.add(TripType.NON_STANDARD_INQUIRY);
                }
                if (configCompanyHiddenField.getAdd_trip_hidden_office()) {
                    hiddenTripType.add(TripType.OFFICE);
                }
                if (configCompanyHiddenField.getAdd_trip_hidden_quotation()) {
                    hiddenTripType.add(TripType.QUOTATION);
                }
                if (configCompanyHiddenField.getAdd_trip_hidden_reimbursement()) {
                    hiddenTripType.add(TripType.REIMBURSEMENT);
                }
                if (configCompanyHiddenField.getAdd_trip_hidden_sample()) {
                    hiddenTripType.add(TripType.SAMPLE);
                }
                if (configCompanyHiddenField.getAdd_trip_hidden_special_price()) {
                    hiddenTripType.add(TripType.SPECIAL_PRICE);
                }
                if (configCompanyHiddenField.getAdd_trip_hidden_spring_ring_inquiry()) {
                    hiddenTripType.add(TripType.SPRING_RING_INQUIRY);
                }
                if (configCompanyHiddenField.getAdd_trip_hidden_task()) {
                    hiddenTripType.add(TripType.TASK);
                }
                if (configCompanyHiddenField.getAdd_trip_hidden_travel()) {
                    hiddenTripType.add(TripType.TRAVEL);
                }
                if (configCompanyHiddenField.getAdd_trip_hidden_visit()) {
                    hiddenTripType.add(TripType.VISIT);
                }
                getNeedApproval();

            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                stopProgressDialog();
                ErrorHandler.getInstance().setException(ApprovalMenuActivity.this, throwable);
                getNeedApproval();
            }
        }));
    }

    private void getNeedApproval() {
        ApiHelper.getInstance().getApprovalApi().getApprovalConfig(TokenManager.getInstance().getToken()).enqueue(new Callback<JSONObject>()
        {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                try {
                    switch (response.code()) {
                        case 200:
                            JSONObject result = response.body();
                            int success = result.getInt("success");
                            if (success == 1) {
                                String approvalList = result.getString("list");
                                Logger.e(TAG, "getApprovalConfig:" + approvalList);
                                JSONObject list = new JSONObject(approvalList);

                                String absentCode = String.valueOf(TripType.ABSENT.getValue());
                                if (list.has(absentCode) && list.optJSONArray(absentCode) == null) {
                                    hiddenTripType.add(TripType.ABSENT);
                                }

                                String reimbursementCode = String.valueOf(TripType.REIMBURSEMENT.getValue());
                                if (list.has(reimbursementCode) && list.optJSONArray(reimbursementCode) == null) {
                                    hiddenTripType.add(TripType.REIMBURSEMENT);
                                }

                                String contractCode = String.valueOf(TripType.CONTRACT.getValue());
                                if (list.has(contractCode) && list.optJSONArray(contractCode) == null) {
                                    hiddenTripType.add(TripType.CONTRACT);
                                }

                                String quotationCode = String.valueOf(TripType.QUOTATION.getValue());
                                if (list.has(quotationCode) && list.optJSONArray(quotationCode) == null) {
                                    hiddenTripType.add(TripType.QUOTATION);
                                }

                                String sampleCode = String.valueOf(TripType.SAMPLE.getValue());
                                if (list.has(sampleCode) && list.optJSONArray(sampleCode) == null) {
                                    hiddenTripType.add(TripType.SAMPLE);
                                }

                                String specialPriceCode = String.valueOf(TripType.SPECIAL_PRICE.getValue());
                                if (list.has(specialPriceCode) && list.optJSONArray(specialPriceCode) == null) {
                                    hiddenTripType.add(TripType.SPECIAL_PRICE);
                                }

                                String productionCode = String.valueOf(TripType.PRODUCTION.getValue());
                                if (list.has(productionCode) && list.optJSONArray(productionCode) == null) {
                                    hiddenTripType.add(TripType.PRODUCTION);
                                }

                                String nonStandardCode = String.valueOf(TripType.NON_STANDARD_INQUIRY.getValue());
                                if (list.has(nonStandardCode) && list.optJSONArray(nonStandardCode) == null) {
                                    hiddenTripType.add(TripType.NON_STANDARD_INQUIRY);
                                }

                                String springRingCode = String.valueOf(TripType.SPRING_RING_INQUIRY.getValue());
                                if (list.has(springRingCode) && list.optJSONArray(springRingCode) == null) {
                                    hiddenTripType.add(TripType.SPRING_RING_INQUIRY);
                                }

                                String expressCode = String.valueOf(TripType.EXPRESS.getValue());
                                if (list.has(expressCode) && list.optJSONArray(expressCode) == null) {
                                    hiddenTripType.add(TripType.EXPRESS);
                                }

                                String travelCode = String.valueOf(TripType.TRAVEL.getValue());
                                if (list.has(travelCode) && list.optJSONArray(travelCode) == null) {
                                    hiddenTripType.add(TripType.TRAVEL);
                                }

                                String specialShipCode = String.valueOf(TripType.SPECIAL_SHIP.getValue());
                                if (list.has(specialShipCode) && list.optJSONArray
                                        (specialShipCode) == null) {
                                    hiddenTripType.add(TripType.SPECIAL_SHIP);
                                }

                                if (isRecordMode) {
                                    getRecord();
                                } else {
                                    getApproval();
                                }
                            } else {
                                Toast.makeText(ApprovalMenuActivity.this, "get approval data " + "failed.", Toast.LENGTH_SHORT).show();
                                onNoData();
                            }
                            break;
                        default:
                            Toast.makeText(ApprovalMenuActivity.this, response.code() + ":" + response.message(), Toast.LENGTH_SHORT).show();
                            onNoData();
                            break;
                    }
                } catch (JSONException e) {
                    stopProgressDialog();
                    ErrorHandler.getInstance().setException(ApprovalMenuActivity.this, e);
                    onNoData();
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable throwable) {
                stopProgressDialog();
                ErrorHandler.getInstance().setException(ApprovalMenuActivity.this, throwable);
            }
        });
    }

    /**
     * 取得待審批項目
     */
    private void getApproval() {
        startProgressDialog();
        Call<JSONObject> call = ApiHelper.getInstance().getApprovalApi().getApprovable(TokenManager.getInstance().getToken());
        call.enqueue(callback);
    }

    /**
     * 取得審批紀錄
     */
    private void getRecord() {
        startProgressDialog();
        Call<JSONObject> call = ApiHelper.getInstance().getApprovalApi().getApprovalRecord(TokenManager.getInstance().getToken());
        call.enqueue(callback);
    }

    Callback callback = new Callback<JSONObject>() {
        @Override
        public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
            try {
                switch (response.code()) {
                    case 200:
                        JSONObject result = response.body();
                        if (result == null) {
                            onNoData();
                            return;
                        }
                        int success = result.getInt("success");
                        if (success == 1) {
                            String approvalList = result.getString("list");
                            Logger.e(TAG, "approvalList:" + approvalList);
                            JSONObject list = new JSONObject(approvalList);

                            String absentCode = String.valueOf(TripType.ABSENT.getValue());
                            if (list.has(absentCode)) {
                                JSONObject absent = list.getJSONObject(absentCode);
                                absentList = gson.fromJson(absent.getString("list"), new TypeToken<List<Approval>>() {}.getType());
                            } else {
                                absentList = new ArrayList<>();
                            }

                            String reimbursementCode = String.valueOf(TripType.REIMBURSEMENT.getValue());
                            if (list.has(reimbursementCode)) {
                                JSONObject reimbursement = list.getJSONObject(reimbursementCode);
                                reimbursementList = gson.fromJson(reimbursement.getString("list"), new TypeToken<List<Approval>>() {}.getType());
                            } else {
                                reimbursementList = new ArrayList<>();
                            }

                            String contractCode = String.valueOf(TripType.CONTRACT.getValue());
                            if (list.has(contractCode)) {
                                JSONObject contract = list.getJSONObject(contractCode);
                                contractList = gson.fromJson(contract.getString("list"), new TypeToken<List<Approval>>() {}.getType());
                            } else {
                                contractList = new ArrayList<>();
                            }

                            String quotationCode = String.valueOf(TripType.QUOTATION.getValue());
                            if (list.has(quotationCode)) {
                                JSONObject quotation = list.getJSONObject(quotationCode);
                                quotationList = gson.fromJson(quotation.getString("list"), new TypeToken<List<Approval>>() {}.getType());
                            } else {
                                quotationList = new ArrayList<>();
                            }

                            String sampleCode = String.valueOf(TripType.SAMPLE.getValue());
                            if (list.has(sampleCode)) {
                                JSONObject sample = list.getJSONObject(sampleCode);
                                sampleList = gson.fromJson(sample.getString("list"), new TypeToken<List<Approval>>() {}.getType());
                            } else {
                                sampleList = new ArrayList<>();
                            }

                            String specialPriceCode = String.valueOf(TripType.SPECIAL_PRICE.getValue());
                            if (list.has(specialPriceCode)) {
                                JSONObject specialPrice = list.getJSONObject(specialPriceCode);
                                specialPriceList = gson.fromJson(specialPrice.getString("list"), new TypeToken<List<Approval>>() {}.getType());
                            } else {
                                specialPriceList = new ArrayList<>();
                            }

                            String productionCode = String.valueOf(TripType.PRODUCTION.getValue());
                            if (list.has(productionCode)) {
                                JSONObject production = list.getJSONObject(productionCode);
                                productionList = gson.fromJson(production.getString("list"), new TypeToken<List<Approval>>() {}.getType());
                            } else {
                                productionList = new ArrayList<>();
                            }

                            String nonStandardCode = String.valueOf(TripType.NON_STANDARD_INQUIRY.getValue());
                            if (list.has(nonStandardCode)) {
                                JSONObject nonStandard = list.getJSONObject(nonStandardCode);
                                nonStandardInquiryList = gson.fromJson(nonStandard.getString("list"), new TypeToken<List<Approval>>() {}.getType());
                            } else {
                                nonStandardInquiryList = new ArrayList<>();
                            }

                            String springRingCode = String.valueOf(TripType.SPRING_RING_INQUIRY.getValue());
                            if (list.has(springRingCode)) {
                                JSONObject springRing = list.getJSONObject(springRingCode);
                                springRingInquiryList = gson.fromJson(springRing.getString("list"), new TypeToken<List<Approval>>() {}.getType());
                            } else {
                                springRingInquiryList = new ArrayList<>();
                            }

                            String expressCode = String.valueOf(TripType.EXPRESS.getValue());
                            if (list.has(expressCode)) {
                                JSONObject express = list.getJSONObject(expressCode);
                                expressList = gson.fromJson(express.getString("list"), new TypeToken<List<Approval>>() {}.getType());
                            } else {
                                expressList = new ArrayList<>();
                            }

                            String travelCode = String.valueOf(TripType.TRAVEL.getValue());
                            if (list.has(travelCode)) {
                                JSONObject travel = list.getJSONObject(travelCode);
                                travelList = gson.fromJson(travel.getString("list"), new TypeToken<List<Approval>>() {}.getType());
                            } else {
                                travelList = new ArrayList<>();
                            }

                            String specialShipCode = String.valueOf(TripType.SPECIAL_SHIP.getValue());
                            if (list.has(specialShipCode)) {
                                JSONObject specialShip = list.getJSONObject(specialShipCode);
                                Logger.e(TAG, "specialShip:" + specialShip.getString("list"));
                                specialShipList = gson.fromJson(specialShip.getString("list"), new TypeToken<List<Approval>>() {}.getType());
                            } else {
                                specialShipList = new ArrayList<>();
                            }
                            updateList();
                        } else {
                            Toast.makeText(ApprovalMenuActivity.this, "get approval data " + "failed.", Toast.LENGTH_SHORT).show();
                            onNoData();
                        }
                        break;
                    default:
                        Toast.makeText(ApprovalMenuActivity.this, response.code() + ":" + response.message(), Toast.LENGTH_SHORT).show();
                        onNoData();
                        break;
                }
            } catch (JSONException e) {
                ErrorHandler.getInstance().setException(ApprovalMenuActivity.this, e);
                onNoData();
            }
        }

        @Override
        public void onFailure(Call<JSONObject> call, Throwable t) {
            ErrorHandler.getInstance().setException(ApprovalMenuActivity.this, t);
            onNoData();
        }
    };

    private void onNoData() {
        stopProgressDialog();
        absentList = new ArrayList<>();
        reimbursementList = new ArrayList<>();
        contractList = new ArrayList<>();
        quotationList = new ArrayList<>();
        sampleList = new ArrayList<>();
        specialPriceList = new ArrayList<>();
        productionList = new ArrayList<>();
        nonStandardInquiryList = new ArrayList<>();
        springRingInquiryList = new ArrayList<>();
        expressList = new ArrayList<>();
        travelList = new ArrayList<>();
        specialShipList = new ArrayList<>();
        updateList();
    }

    private void updateList() {
        stopProgressDialog();
        int type = getIntent().getIntExtra(MyApplication.INTENT_KEY_TYPE, -1);
        if (type != -1) {
            /*來自推播*/
            jumpToTab(TripType.getTripTypeByValue(type));
        } else {
            container_apply.removeAllViews();
            container_work.removeAllViews();
            for (TripType workType : TripType.values()) {
                switch (workType) {
                    case ABSENT:
                    case SAMPLE:
                    case QUOTATION:
                    case REIMBURSEMENT:
                    case CONTRACT:
                    case SPECIAL_PRICE:
                    case PRODUCTION:
                    case NON_STANDARD_INQUIRY:
                    case SPRING_RING_INQUIRY:
                    case SPECIAL_SHIP:
                    case EXPRESS:
                    case TRAVEL:
                        generateItem(workType);
                        break;
                }
            }
            if (container_apply.getChildCount() > 0) {
                tvTitleApply.setVisibility(View.VISIBLE);
            } else {
                tvTitleApply.setVisibility(View.GONE);
            }
            if (container_work.getChildCount() > 0) {
                tvTitleWork.setVisibility(View.VISIBLE);
            } else {
                tvTitleWork.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 產生列表項目
     */
    private void generateItem(final TripType type) {
        if (hiddenTripType.contains(type))
            return;

        LinearLayout container = null;
        List<Approval> list = null;
        int count = 0;
        switch (type) {
            case ABSENT:
                list = absentList;
                container = container_work;
                break;
            case QUOTATION:
                list = quotationList;
                container = container_apply;
                break;
            case CONTRACT:
                list = contractList;
                container = container_apply;
                break;
            case REIMBURSEMENT:
                list = reimbursementList;
                container = container_apply;
                break;
            case SAMPLE:
                list = sampleList;
                container = container_apply;
                break;
            case SPECIAL_PRICE:
                list = specialPriceList;
                container = container_apply;
                break;
            case PRODUCTION:
                list = productionList;
                container = container_apply;
                break;
            case NON_STANDARD_INQUIRY:
                list = nonStandardInquiryList;
                container = container_apply;
                break;
            case SPRING_RING_INQUIRY:
                list = springRingInquiryList;
                container = container_apply;
                break;
            case SPECIAL_SHIP:
                list = specialShipList;
                container = container_apply;
                break;
            case EXPRESS:
                list = expressList;
                container = container_apply;
                break;
            case TRAVEL:
                list = travelList;
                container = container_apply;
                break;
            default:
                return;
        }

        if (isRecordMode)
        {
            for (Approval approval : list) {
                if (approval.getApproval() == ApprovalStatus.UNSIGN || approval.getApproval() == ApprovalStatus.PROCESS)
                    count++;
            }
        }
        else
        {
            for (Approval approval : list) {
                count += getUnapprovalCount(approval);
            }
        }

        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.item_list, container, false);
        TextView tvTitle = view.findViewById(R.id.textView_title);
        TextView tvCount = view.findViewById( R.id.textView_count);
        ImageView ivIcon = view.findViewById( R.id.imageView_icon);

        tvTitle.setText(type.getTitle());
        ivIcon.setImageResource(type.getIcon());
        tvCount.setText(String.valueOf(count));
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jumpToTab(type);
            }
        });
        container.addView(view);
    }

    int getUnapprovalCount(Approval approval) {
        if (approval == null || approval.getTrip_approvals() == null)
            return 0;
        if (approval.getApproval() == ApprovalStatus.UNSIGN || approval.getApproval() == ApprovalStatus.PROCESS) {
            String userId = TokenManager.getInstance().getUser().getId();
            for (Approve approve : approval.getTrip_approvals()) {
                if (approve.getUser_id().equals(userId)) {
                    return 0;
                }
            }
            return 1;
        }
        return 0;
    }

    void jumpToTab(TripType type) {
        final Intent intent = new Intent(ApprovalMenuActivity.this, ApprovalTabActivity.class);
        intent.putExtra(MyApplication.INTENT_KEY_MODE, isRecordMode);
        intent.putExtra(MyApplication.INTENT_KEY_TYPE, type.getValue());

        List<Approval> approvalList = null;
        switch (type) {
            case ABSENT:
                approvalList = absentList;
                break;
            case QUOTATION:
                approvalList = quotationList;
                break;
            case CONTRACT:
                approvalList = contractList;
                break;
            case REIMBURSEMENT:
                approvalList = reimbursementList;
                break;
            case SAMPLE:
                approvalList = sampleList;
                break;
            case SPECIAL_PRICE:
                approvalList = specialPriceList;
                break;
            case PRODUCTION:
                approvalList = productionList;
                break;
            case NON_STANDARD_INQUIRY:
                approvalList = nonStandardInquiryList;
                break;
            case SPRING_RING_INQUIRY:
                approvalList = springRingInquiryList;
                break;
            case SPECIAL_SHIP:
                approvalList = specialShipList;
                break;
            case EXPRESS:
                approvalList = expressList;
                break;
            case TRAVEL:
                approvalList = travelList;
                break;
        }
        if (approvalList != null) {
            for (Approval approval : approvalList) {
                if (approval.getCustomer() != null)
                    approval.getCustomer().setLogo("");
            }

            intent.putExtra(MyApplication.INTENT_KEY_APPROVAL, gson.toJson(approvalList));
            startActivity(intent);
        }
    }

}
