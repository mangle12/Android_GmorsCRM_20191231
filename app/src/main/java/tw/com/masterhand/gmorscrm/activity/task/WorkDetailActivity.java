package tw.com.masterhand.gmorscrm.activity.task;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;
import org.reactivestreams.Subscription;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.FlowableSubscriber;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import rebus.permissionutils.PermissionEnum;
import rebus.permissionutils.PermissionManager;
import rebus.permissionutils.PermissionUtils;
import rebus.permissionutils.SimpleCallback;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tw.com.masterhand.gmorscrm.BaseUserCheckActivity;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.TokenManager;
import tw.com.masterhand.gmorscrm.api.ApiHelper;
import tw.com.masterhand.gmorscrm.enums.ApprovalRequire;
import tw.com.masterhand.gmorscrm.enums.ApprovalStatus;
import tw.com.masterhand.gmorscrm.enums.SignStatus;
import tw.com.masterhand.gmorscrm.enums.TripStatus;
import tw.com.masterhand.gmorscrm.enums.TripType;
import tw.com.masterhand.gmorscrm.model.Conversation;
import tw.com.masterhand.gmorscrm.model.ValidLocation;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.record.File;
import tw.com.masterhand.gmorscrm.room.record.Participant;
import tw.com.masterhand.gmorscrm.room.setting.User;
import tw.com.masterhand.gmorscrm.tools.Base64Utils;
import tw.com.masterhand.gmorscrm.tools.Checker;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.tools.ImageHelper;
import tw.com.masterhand.gmorscrm.tools.ImageTools;
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.tools.TimeFormater;
import tw.com.masterhand.gmorscrm.view.Appbar;
import tw.com.masterhand.gmorscrm.view.CompanyCard;
import tw.com.masterhand.gmorscrm.view.ItemConversation;
import tw.com.masterhand.gmorscrm.view.ItemProject;
import tw.com.masterhand.gmorscrm.view.ItemTripAccept;
import tw.com.masterhand.gmorscrm.view.ItemTripStatus;

abstract public class WorkDetailActivity extends BaseUserCheckActivity {
    @BindView(R.id.appbar)
    protected Appbar appbar;
    @BindView(R.id.companyCard)
    protected CompanyCard companyCard;
    @BindView(R.id.itemProject)
    protected ItemProject itemProject;
    @BindView(R.id.textView_title)
    protected TextView tvTitle;
    @BindView(R.id.tvStartTime)
    protected TextView tvStartTime;
    @BindView(R.id.tvStartWeek)
    protected TextView tvStartWeek;
    @BindView(R.id.tvEndTime)
    protected TextView tvEndTime;
    @BindView(R.id.tvEndWeek)
    protected TextView tvEndWeek;
    @BindView(R.id.tvAlert)
    protected TextView tvAlert;
    @BindView(R.id.textView_place)
    protected TextView tvLocation;
    @BindView(R.id.tvSignIn)
    protected TextView tvSignIn;
    @BindView(R.id.tvSignOut)
    protected TextView tvSignOut;
    @BindView(R.id.btnContacter)
    protected Button btnContacter;
    @BindView(R.id.btnParticipant)
    protected Button btnParticipant;
    @BindView(R.id.btnApprover)
    protected Button btnApprover;
    @BindView(R.id.container_photo)
    protected LinearLayout containerPhoto;
    @BindView(R.id.containerReport)
    protected LinearLayout containerReport;
    @BindView(R.id.containerAddress)
    protected LinearLayout containerAddress;
    @BindView(R.id.tvReport)
    protected TextView tvReport;
    @BindView(R.id.tvNote)
    protected TextView tvNote;
    @BindView(R.id.tvBuilder)
    protected TextView tvBuilder;
    @BindView(R.id.container_conversation)
    protected LinearLayout containerConversation;
    @BindView(R.id.containerSign)
    protected RelativeLayout containerSign;
    @BindView(R.id.itemTripStatus)
    protected ItemTripStatus itemTripStatus;
    @BindView(R.id.itemTripAccept)
    protected ItemTripAccept itemTripAccept;
    @BindView(R.id.containerFunction)
    protected LinearLayout containerFunction;

    String taskId;
    String tripId;
    TripType tripType;
    protected String viewerId;

    protected Participant viewer;
    protected ImageHelper imageHelper;
    protected LocationManager locationManager;
    protected ApprovalRequire approvalRequire = ApprovalRequire.UNKNOWN;
    protected ApprovalStatus approvalStatus = ApprovalStatus.UNSIGN;

    LocationListener gpsListener, networkListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_detail);
        init();
    }

    @Override
    protected void onUserChecked() {
        appbar.invalidate();
        viewerId = TokenManager.getInstance().getUser().getId();
        Logger.e(TAG, "viewerId:" + viewerId);
        requireUpdateLcation();
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            locationManager.removeUpdates(gpsListener);
            locationManager.removeUpdates(networkListener);
        } catch (Exception e) {
            Logger.e(TAG, "Exception:" + e.getMessage());
        }
    }

    protected void init() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        imageHelper = new ImageHelper(this);
        itemProject.hideIndex();
        itemProject.hideHistory();
    }

    abstract protected void updateData();

    /**
     * 基本資料載入完畢事件
     */
    abstract protected void onDataLoaded();

    /**
     * 更新對話
     */
    protected void updateConversation() {
        containerConversation.removeAllViews();
        if (taskId == null) {
            Logger.e(TAG, "taskId is null");
            return;
        }
        DatabaseHelper.getInstance(this).getConversationByParent(taskId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new FlowableSubscriber<Conversation>() {
            @Override
            public void onSubscribe(@NonNull Subscription s) {
                s.request(Integer.MAX_VALUE);
            }

            @Override
            public void onNext(Conversation conversation) {
                ItemConversation itemConversation = new ItemConversation(WorkDetailActivity.this);
                itemConversation.setConversation(conversation);
                itemConversation.getMainMessage().showIndex(false);
                containerConversation.addView(itemConversation);
            }

            @Override
            public void onError(Throwable t) {
                Logger.e(TAG, "error:" + t.getMessage());
            }

            @Override
            public void onComplete() {

            }
        });
    }

    /**
     * 新增討論
     */
    @OnClick(R.id.imageButton_add)
    void addConversation() {
        if (tripId == null) {
            Logger.e(TAG, "trip id is null");
            return;
        }
        Intent intent = new Intent(this, ConversationActivity.class);
        intent.putExtra(MyApplication.INTENT_KEY_TRIP, tripId);
        intent.putExtra(MyApplication.INTENT_KEY_ID, taskId);
        startActivityForResult(intent, MyApplication.REQUEST_ADD_CONVERSATION);
    }

    /**
     * 檢查審批狀態
     * (2018/1/19 決定暫不檢查審批狀態)
     */
    boolean checkApproval() {
//        boolean check = false;
//        String msg = "";
//        switch (approvalRequire) {
//            case NEED:
//                check = approvalStatus == ApprovalStatus.AGREE;
//                break;
//            case NOT_NEED:
//                check = true;
//                break;
//        }
//        if (!check) {
//            msg += getString(R.string.approval_require) + ":" + getString(approvalRequire
//                    .getTitle()) + "\n";
//            msg += getString(R.string.approval_status) + ":" + getString(approvalStatus
// .getTitle());
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setTitle(R.string.approval_status).setMessage(msg).setCancelable(true);
//            builder.create().show();
//        }
        return true;
    }

    void updateSignStatus() {
        if (viewer == null) {
            Logger.e(TAG, "updateSignStatus:viewer is null");
            containerSign.setVisibility(View.GONE);
            return;
        }

        Logger.e(TAG, "participant id:" + viewer.getId());
        Logger.e(TAG, "viewer sign status:" + viewer.getStatus().name());
        Logger.e(TAG, "updateSignStatus:" + viewer.getSign_in_at() + ":" + viewer.getSign_out_at());

        if (viewer.getStatus() == SignStatus.SIGN_IN && viewer.getSign_in_at() != null) {
            tvSignIn.setText(TimeFormater.getInstance().toDateTimeFormat(viewer.getSign_in_at()));
            enableSignOut();
        } else if (viewer.getStatus() == SignStatus.SIGN_OUT && viewer.getSign_out_at() != null && viewer.getSign_in_at() != null) {
            tvSignIn.setText(TimeFormater.getInstance().toDateTimeFormat(viewer.getSign_in_at()));
            tvSignOut.setText(TimeFormater.getInstance().toDateTimeFormat(viewer.getSign_out_at()));
        } else {
            tvSignIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 簽到(呼叫API檢查座標，無網路則跳出照片打卡dialog)
                    if (checkApproval())
                        gpsSignIn();
                }
            });
        }
    }

    public void enableSignOut() {
        tvSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 簽退
                if (checkApproval()) {
                    Date signOutDate = new Date();
                    viewer.setSign_out_at(signOutDate);
                    viewer.setStatus(SignStatus.SIGN_OUT);
                    mDisposable.add(DatabaseHelper.getInstance(v.getContext()).saveParticipant(viewer)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<String>() {
                                @Override
                                public void accept(String s) throws Exception {
                                    tvSignOut.setText(TimeFormater.getInstance().toDateTimeFormat(viewer.getSign_out_at()));
                                    tvSignOut.setEnabled(false);
                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) throws Exception {
                                    ErrorHandler.getInstance().setException(WorkDetailActivity.this, throwable);
                                }
                            }));
                }
            }
        });
    }

    void gpsSignIn() {
        //r檢查網路連線和定位
        if (Checker.isNetworkConnected(this) && Checker.isLocationEnabled(this)) {
            PermissionEnum[] permission = {PermissionEnum.ACCESS_FINE_LOCATION, PermissionEnum.ACCESS_COARSE_LOCATION};
            boolean granted = PermissionUtils.isGranted(this, permission);
            if (granted) {
                startGpsSignIn();
            }
            else {
                //要求權限
                PermissionManager.with(this).permission(permission).askAgain(true).callback(new SimpleCallback() {
                            @Override
                            public void result(boolean allPermissionsGranted) {
                                Log.e(TAG, "allPermissionsGranted:" + allPermissionsGranted);
                                if (allPermissionsGranted)
                                    startGpsSignIn();
                                else
                                    Toast.makeText(WorkDetailActivity.this, getString(R.string.error_msg_permission), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .ask();
            }
        } else {
            photoSignIn(getString(R.string.error_msg_location_permission));
        }
    }

    void startGpsSignIn() {
        if (approvalRequire == ApprovalRequire.UNKNOWN) {
            photoSignIn(getString(R.string.error_msg_not_sync));
            return;
        }
        startProgressDialog();
        Location lastLocation = getLastKnownLocation();//取得經緯度

        if (lastLocation == null) {
            photoSignIn(getString(R.string.error_msg_not_get_gps));
            return;
        }
        Logger.e(TAG, "latitude:" + lastLocation.getLatitude());
        Logger.e(TAG, "longitude:" + lastLocation.getLongitude());

        ValidLocation validLocation = new ValidLocation();
        validLocation.setId(tripId);
        validLocation.setType(tripType);
        validLocation.setLatitude(lastLocation.getLatitude());//經度
        validLocation.setLongitude(lastLocation.getLongitude());//緯度

        ApiHelper.getInstance().getTripApi().validLocation(TokenManager.getInstance().getToken(), validLocation).enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {

                stopProgressDialog();

                try {
                    Logger.e(TAG, "response code:" + response.code());
                    Logger.e(TAG, "response:" + response.body().toString());
                    switch (response.code()) {
                        case 200:
                            JSONObject result = response.body();
                            int success = result.getInt("success");
                            if (success == 1) {
                                int valid = result.getInt("valid");
                                if (valid == 1) {
                                    Logger.e(TAG, "location valid:true");
                                    participantSignIn();
                                } else {
                                    Logger.e(TAG, "location valid:false");
                                    photoSignIn(getString(R.string.error_msg_gps_validation));
                                }
                            } else {
                                //拍照打卡
                                photoSignIn(result.getString("errorMsg"));
                            }
                            break;
                        default:
                            photoSignIn(getString(R.string.error_msg_unknown));
                            break;
                    }
                } catch (Exception e) {
                    Logger.e(TAG, "Exception:" + e.getMessage());
                    photoSignIn(getString(R.string.error_msg_unknown));
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                stopProgressDialog();
                photoSignIn(getString(R.string.error_msg_unknown));
            }
        });
    }

    private void requireUpdateLcation() {
        if (locationManager == null)
        {
            return;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            return;
        }

        gpsListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        Date curDate = new Date(System.currentTimeMillis()) ; // 獲取當前時間
                        Logger.e(TAG, curDate+"location updated:GPS");
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }
                };

        //30秒監聽一次
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 0, gpsListener);

        networkListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        Logger.e(TAG, "location updated:network");
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }
                };
        //30秒監聽一次
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 30000, 0, networkListener);
    }

    /*
        * 取的經緯度
        */
    private Location getLastKnownLocation() {
        //ACCESS_FINE_LOCATION GPS權限: ACCESS_COARSE_LOCATION 網路定位
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            //未取得權限
            return null;
        }

        if (locationManager == null)
            return null;

        Location gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Location webLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if (webLocation != null) {
            if (gpsLocation != null && webLocation.getTime() < gpsLocation.getTime())
                return gpsLocation;
            Logger.e(TAG, "location from network");
            return webLocation;
        }

        Logger.e(TAG, "location from gps");
        return gpsLocation;
    }

    //拍照打卡
    void photoSignIn(String msg) {
        stopProgressDialog();
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, MyApplication.DIALOG_STYLE);
            builder.setTitle(R.string.error_msg_gps_sign_in);//無法透過GPS簽到，請透過拍照進行簽到

            if (!TextUtils.isEmpty(msg))
            {
                builder.setMessage(msg);
            }

            builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    imageHelper.takePhoto();
                }
            });
            builder.create().show();
        } else {
            // 無相機功能，無法進行簽到
            Toast.makeText(this, R.string.error_msg_no_camera, Toast.LENGTH_SHORT).show();
        }
    }

    //參加者登入
    void participantSignIn() {
        Logger.e(TAG, "participantSignIn");
        Date signInDate = new Date();
        viewer.setSign_in_at(signInDate);
        viewer.setStatus(SignStatus.SIGN_IN);
        mDisposable.add(DatabaseHelper.getInstance(this).saveParticipant(viewer)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        tvSignIn.setText(TimeFormater.getInstance().toDateTimeFormat(viewer.getSign_in_at()));
                        tvSignIn.setEnabled(false);
                        itemTripStatus.setVisibility(View.GONE);
                        enableSignOut();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ErrorHandler.getInstance().setException(WorkDetailActivity.this, throwable);
                    }
                }));
    }

    void updatePhoto(List<File> files) {
        containerPhoto.removeAllViews();
        if (files.size() > 0) {
            for (File file : files) {
                containerPhoto.addView(generatePhotoItem(file));
            }
        } else {
            containerPhoto.addView(getEmptyTextView(getString(R.string.error_msg_no_photo)));
        }
    }

    protected void getBuilder(final String userId) {
        mDisposable.add(DatabaseHelper.getInstance(this).getUserById(userId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<User>() {
            @Override
            public void accept(User user) throws Exception {
                tvBuilder.setText(user.getShowName());
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                ErrorHandler.getInstance().setException(WorkDetailActivity.this, throwable);
            }
        }));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            String reason;
            switch (requestCode) {
                case MyApplication.REQUEST_EDIT:
                    updateData();
                    break;
                case MyApplication.REQUEST_DELAY_TASK:
                    reason = data.getStringExtra(MyApplication.INTENT_KEY_REASON);
                    updateData();
                    itemTripStatus.setWorkStatus(TripStatus.DELAY, reason);
                    break;
                case MyApplication.REQUEST_CANCEL_TASK:
                    reason = data.getStringExtra(MyApplication.INTENT_KEY_REASON);
                    itemTripStatus.setWorkStatus(TripStatus.CANCEL, reason);
                    break;
                case MyApplication.REQUEST_ADD_CONVERSATION:
                    updateConversation();
                    break;
                case ImageHelper.REQUEST_TAKE_PHOTO:
                    // 拍照簽到
                    int photoSize = getResources().getDimensionPixelSize(R.dimen
                            .photo_upload_size_small);
                    try {
                        Uri photoUri = Uri.fromFile(imageHelper.getImageFile());
                        Logger.e(TAG, "get from REQUEST_TAKE_PHOTO:" + photoUri.getPath());
                        Bitmap bitmap = imageHelper.getBitmapFromUri(photoUri, photoSize, photoSize);
                        File file = new File();
                        file.setName(getString(R.string.sign_in));
                        file.setFile(Base64Utils.encodeBitmapToString(ImageTools.getCroppedBitmap(bitmap)));
                        mDisposable.add(DatabaseHelper.getInstance(this).saveSignInFile(file, viewer.getId())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Consumer<String>()
                                {
                                    @Override
                                    public void accept(String s) throws Exception {
                                        participantSignIn();
                                    }
                                }, new Consumer<Throwable>() {
                                    @Override
                                    public void accept(Throwable throwable) throws Exception {
                                        ErrorHandler.getInstance().setException(WorkDetailActivity.this, throwable);
                                    }
                                }));
                    } catch (Exception e) {
                        Logger.e(TAG, "Exception:" + e.getMessage());
                        participantSignIn();
                    }
                    break;
                default:
                    updateData();

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
