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
import tw.com.masterhand.gmorscrm.ParticipantListActivity;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.TokenManager;
import tw.com.masterhand.gmorscrm.activity.news.SaleResourceActivity;
import tw.com.masterhand.gmorscrm.api.ApiHelper;
import tw.com.masterhand.gmorscrm.enums.ApprovalStatus;
import tw.com.masterhand.gmorscrm.enums.SignStatus;
import tw.com.masterhand.gmorscrm.enums.SubmitStatus;
import tw.com.masterhand.gmorscrm.model.MultipleSubmit;
import tw.com.masterhand.gmorscrm.model.ProjectWithConfig;
import tw.com.masterhand.gmorscrm.model.TravelWithConfig;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.record.Customer;
import tw.com.masterhand.gmorscrm.room.record.File;
import tw.com.masterhand.gmorscrm.room.record.Participant;
import tw.com.masterhand.gmorscrm.room.setting.User;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.tools.TimeFormater;
import tw.com.masterhand.gmorscrm.view.Appbar;
import tw.com.masterhand.gmorscrm.view.CompanyCard;
import tw.com.masterhand.gmorscrm.view.ItemApproval;
import tw.com.masterhand.gmorscrm.view.ItemProject;
import tw.com.masterhand.gmorscrm.view.ItemTripAccept;
import tw.com.masterhand.gmorscrm.view.ItemTripStatus;

public class TravelDetailActivity extends BaseUserCheckActivity {
    @BindView(R.id.appbar)
    Appbar appbar;
    @BindView(R.id.companyCard)
    protected CompanyCard companyCard;
    @BindView(R.id.itemProject)
    protected ItemProject itemProject;
    @BindView(R.id.tvTitle)
    protected TextView tvTitle;

    @BindView(R.id.tvStartTime)
    protected TextView tvStartTime;
    @BindView(R.id.tvStartWeek)
    protected TextView tvStartWeek;
    @BindView(R.id.tvEndTime)
    protected TextView tvEndTime;
    @BindView(R.id.tvEndWeek)
    protected TextView tvEndWeek;
    @BindView(R.id.tvAddress)
    protected TextView tvAddress;
    @BindView(R.id.tvInfo)
    protected TextView tvInfo;
    @BindView(R.id.tvReason)
    protected TextView tvReason;
    @BindView(R.id.tvSupport)
    protected TextView tvSupport;
    @BindView(R.id.tvCustomerBackground)
    protected TextView tvCustomerBackground;
    @BindView(R.id.tvCustomerTech)
    protected TextView tvCustomerTech;

    @BindView(R.id.tvBuilder)
    protected TextView tvBuilder;
    @BindView(R.id.tvNote)
    protected TextView tvNote;
    @BindView(R.id.btnParticipant)
    protected Button btnParticipant;
    @BindView(R.id.btnApprover)
    protected Button btnApprover;
    @BindView(R.id.tvApprovalDescription)
    protected TextView tvApprovalDescription;
    @BindView(R.id.container_photo)
    protected LinearLayout containerPhoto;
    @BindView(R.id.itemApproval)
    protected ItemApproval itemApproval;
    @BindView(R.id.itemTripStatus)
    protected ItemTripStatus itemTripStatus;
    @BindView(R.id.itemTripAccept)
    protected ItemTripAccept itemTripAccept;

    protected TravelWithConfig travel;
    protected String viewerId;
    protected String tripId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_detail);
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

        appbar.setTitle(getString(R.string.apply_travel) + getString(R.string
                .title_activity_work_detail));

        itemProject.hideIndex();
        itemProject.hideHistory();
        tripId = getIntent().getStringExtra(MyApplication.INTENT_KEY_TRIP);
        if (TextUtils.isEmpty(tripId)) {
            finish();
        }
    }

    protected void getData() {
        if (TextUtils.isEmpty(tripId))
            return;
        Logger.i(TAG, "tripId:" + tripId);
        mDisposable.add(DatabaseHelper.getInstance(this).getTravelByTrip(tripId).observeOn
                (AndroidSchedulers.mainThread()).subscribe(new Consumer<TravelWithConfig>() {
            @Override
            public void accept(TravelWithConfig result) throws Exception {
                travel = result;
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
                            Intent intent = new Intent(view.getContext(), TravelCreateActivity
                                    .class);
                            intent.putExtra(MyApplication.INTENT_KEY_TRIP, travel
                                    .getTravel()
                                    .getTrip_id());
                            startActivity(intent);
                        }
                    });
                }
                updateDetail();
                updatePhoto(travel.getFiles());
                getCustomer();
                getProject();
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Toast.makeText(TravelDetailActivity.this, throwable.getMessage(), Toast
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
                    ErrorHandler.getInstance().setException(TravelDetailActivity.this, e);
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                ErrorHandler.getInstance().setException(TravelDetailActivity.this, t);
            }
        });
    }

    protected void submit() {
        final MultipleSubmit submit = new MultipleSubmit();
        submit.setTrip_ids(travel.getTravel().getTrip_id());
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
                                        (TravelDetailActivity.this)
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
                                                        (TravelDetailActivity
                                                                        .this,
                                                                e.getMessage(),
                                                                Toast.LENGTH_LONG)
                                                        .show();
                                            }
                                        });
                            } else {
                                Toast.makeText(TravelDetailActivity.this,
                                        result.getString("errorMsg"),
                                        Toast.LENGTH_LONG).show();
                            }
                            break;
                        case ApiHelper.ERROR_TOKEN_EXPIRED:
                            showLoginDialog();
                            break;
                        default:
                            Logger.e(TAG, "submit failed:" + response.code());
                    }
                } catch (Exception e) {
                    ErrorHandler.getInstance().setException
                            (TravelDetailActivity.this, e);
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                Toast.makeText(TravelDetailActivity.this, t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    @OnClick(R.id.btnParticipant)
    void showParticipant() {
        if (travel.getParticipants().size() > 0) {
            Intent intent = new Intent(this, ParticipantListActivity.class);
            intent.putExtra(MyApplication.INTENT_KEY_PARENT, travel.getTravel().getId());
            startActivity(intent);
        }
    }

    @OnClick(R.id.btnApprover)
    void showApproval() {
        Intent intent = new Intent(this, ApprovalListActivity.class);
        intent.putExtra(MyApplication.INTENT_KEY_TRIP, travel.getTravel().getTrip_id());
        startActivity(intent);
    }

    @OnClick(R.id.btnResource)
    void showResource() {
        Intent intent = new Intent(this, SaleResourceActivity.class);
        intent.putExtra(MyApplication.INTENT_KEY_ID, travel.getTravel().getTrip_id());
        startActivity(intent);
    }

    public void updatePhoto(List<File> files) {
        containerPhoto.removeAllViews();
        for (File file : files) {
            containerPhoto.addView(generatePhotoItem(file));
        }
        if (containerPhoto.getChildCount() == 0)
            containerPhoto.addView(getEmptyTextView(getString(R.string.error_msg_no_photo)));
    }

    public void updateDetail() {
        tvTitle.setText(travel.getTrip().getName());
        if (travel.getTrip().getDate_type()) {
            tvStartTime.setText(TimeFormater.getInstance().toDateFormat(travel.getTrip()
                    .getFrom_date()));
            tvEndTime.setText(TimeFormater.getInstance().toDateFormat(travel.getTrip()
                    .getTo_date()));
        } else {
            tvStartTime.setText(TimeFormater.getInstance().toDateTimeFormat(travel.getTrip()
                    .getFrom_date()));
            tvEndTime.setText(TimeFormater.getInstance().toDateTimeFormat(travel.getTrip()
                    .getTo_date()));
        }
        tvStartWeek.setText(TimeFormater.getInstance().toWeekFormat(travel.getTrip().getFrom_date
                ()));
        tvEndWeek.setText(TimeFormater.getInstance().toWeekFormat(travel.getTrip().getTo_date
                ()));
        if (travel.getTravel().getAddress() != null)
            tvAddress.setText(travel.getTravel().getAddress().getShowAddress());
        if (!TextUtils.isEmpty(travel.getTravel().getInfo()))
            tvInfo.setText(travel.getTravel().getInfo());
        if (!TextUtils.isEmpty(travel.getTravel().getReason()))
            tvReason.setText(travel.getTravel().getReason());
        if (!TextUtils.isEmpty(travel.getTravel().getSupport()))
            tvSupport.setText(travel.getTravel().getSupport());
        if (!TextUtils.isEmpty(travel.getTravel().getCustomer_background()))
            tvCustomerBackground.setText(travel.getTravel().getCustomer_background());
        if (!TextUtils.isEmpty(travel.getTravel().getCustomer_tech()))
            tvCustomerTech.setText(travel.getTravel().getCustomer_tech());
        if (travel.getParticipants().size() > 0) {
            btnParticipant.setText(getString(R.string.travel_participant) + " " + travel
                    .getParticipants
                            ().size());
            if (travel.getTrip().getUser_id().equals(viewerId)) {
                boolean isUser = false;
                boolean isSign = false;
                Participant viewer = null;
                for (Participant participant : travel
                        .getParticipants()) {
                    if (participant.getUser_id().equals(viewerId)) {
                        isUser = true;
                        viewer = participant;
                    }
                    if (participant.getStatus() != SignStatus.NONE)
                        isSign = true;
                }
                if (isUser && !isSign) {
                    itemTripStatus.setVisibility(View.VISIBLE);
                    itemTripStatus.setTrip(travel.getTrip(), viewer);
                } else {
                    itemTripStatus.setVisibility(View.GONE);
                }
            } else {
                for (Participant participant : travel
                        .getParticipants()) {
                    if (participant.getUser_id().equals(viewerId)) {
                        itemTripAccept.setVisibility(View.VISIBLE);
                        itemTripAccept.setParticipant(participant);
                        break;
                    }
                }
            }
        } else {
            itemTripStatus.setVisibility(View.GONE);
            btnParticipant.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(travel.getTrip().getDescription()))
            tvNote.setText(travel.getTrip().getDescription());
        mDisposable.add(DatabaseHelper.getInstance(this).getUserById(travel.getTrip().getUser_id
                ()).observeOn
                (AndroidSchedulers.mainThread()).subscribe(new Consumer<User>() {

            @Override
            public void accept(User user) throws Exception {
                tvBuilder.setText(user.getShowName());
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                ErrorHandler.getInstance().setException(TravelDetailActivity.this, throwable);
            }
        }));
    }

    public void getCustomer() {
        mDisposable.add(DatabaseHelper.getInstance(this).getCustomerById(travel.getTrip()
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
                        ErrorHandler.getInstance().setException(TravelDetailActivity.this,
                                throwable);
                    }
                }));
    }

    public void getProject() {
        mDisposable.add(DatabaseHelper.getInstance(this).getProjectById(travel.getTrip()
                .getProject_id(), TokenManager.getInstance()
                .getUser().getDepartment_id(), TokenManager.getInstance().getUser().getCompany_id
                ()).observeOn
                (AndroidSchedulers.mainThread()).subscribe(new Consumer<ProjectWithConfig>() {

            @Override
            public void accept(ProjectWithConfig projectWithConfig) throws
                    Exception {
                itemProject.setProject(projectWithConfig);
                itemProject.setTripId(travel.getTrip().getId());
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                ErrorHandler.getInstance().setException(TravelDetailActivity.this,
                        throwable);
            }
        }));
    }
}
