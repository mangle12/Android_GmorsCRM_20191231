package tw.com.masterhand.gmorscrm.activity.personal;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.OnClick;
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
import tw.com.masterhand.gmorscrm.model.Phone;
import tw.com.masterhand.gmorscrm.model.UserWithConfig;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.setting.User;
import tw.com.masterhand.gmorscrm.tools.Base64Utils;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.tools.ImageHelper;
import tw.com.masterhand.gmorscrm.tools.ImageTools;
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.tools.TimeFormater;
import tw.com.masterhand.gmorscrm.view.AppbarEdit;
import tw.com.masterhand.gmorscrm.view.ItemInputEmail;
import tw.com.masterhand.gmorscrm.view.ItemInputPhone;

public class PersonalEditActivity extends BaseWebCheckActivity implements ItemInputPhone
        .SelectPhoneTypeListener {
    @BindView(R.id.appbar)
    AppbarEdit appbar;
    @BindView(R.id.tvBossName)
    TextView tvBossName;
    @BindView(R.id.tvBossTitle)
    TextView tvBossTitle;
    @BindView(R.id.tvBossTime)
    TextView tvBossTime;
    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.tvTime)
    TextView tvTime;
    @BindView(R.id.imageView_in_charge)
    ImageView ivInCharge;
    @BindView(R.id.imageView_myself)
    ImageView ivMyself;

    @BindView(R.id.itemInputEmail)
    ItemInputEmail itemInputEmail;
    @BindView(R.id.containerPhone)
    LinearLayout containerPhone;
    @BindView(R.id.linearLayout_in_charge)
    LinearLayout containerInCharge;
    @BindView(R.id.etProfile)
    EditText etProfile;
    @BindView(R.id.etNote)
    EditText etNote;

    ImageHelper imageHelper;
    ItemInputPhone selectedItemInputPhone = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_edit);
        init();
    }

    @Override
    protected void onUserChecked() {
        appbar.invalidate();
        getUserDetail();
        getSupervisorDetail();
    }

    private void init() {
        imageHelper = new ImageHelper(this);
        appbar.setTitle(getString(R.string.title_activity_personal));
        appbar.setCompleteListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkInput()) {
                    User user = TokenManager.getInstance().getUser();
                    user.setEmail(itemInputEmail.getEmail());
                    ArrayList<Phone> phoneList = new ArrayList<>();
                    for (int i = 0; i < containerPhone.getChildCount(); i++) {
                        ItemInputPhone itemInputPhone = (ItemInputPhone) containerPhone
                                .getChildAt(i);
                        Phone phone = itemInputPhone.getPhone();
                        if (!TextUtils.isEmpty(phone.getType()) && !TextUtils.isEmpty(phone
                                .getTel
                                        ())) {
                            phoneList.add(phone);
                        }
                    }
                    user.setTel(phoneList);
                    user.setPersional_history(etProfile.getText().toString());
                    user.setMisc(etNote.getText().toString());
                    uploadUser(user);
                }
            }
        });

    }

    private void uploadUser(final User user) {
        startProgressDialog();
        if (TextUtils.isEmpty(user.getPhoto()))
            user.setPhoto(null);
        Logger.e(TAG, "user:" + gson.toJson(user));
        Call<JSONObject> call = ApiHelper.getInstance().getUserApi().updateUser
                (TokenManager.getInstance().getToken(), user);
        call.enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject>
                    response) {
                stopProgressDialog();
                switch (response.code()) {
                    case 200:
                        TokenManager.getInstance().setUser(user);
                        DatabaseHelper.getInstance(PersonalEditActivity.this).saveUser(user)
                                .subscribe(new Consumer<String>() {

                                    @Override
                                    public void accept(String s) throws Exception {
                                        setResult(Activity.RESULT_OK);
                                        finish();
                                    }
                                }, new Consumer<Throwable>() {
                                    @Override
                                    public void accept(Throwable throwable) throws Exception {
                                        ErrorHandler.getInstance().setException(PersonalEditActivity
                                                .this, throwable);
                                    }
                                });
                        break;
                    default:
                        Logger.e(TAG, "response code:" + response.code());
                        if (response.body() != null) {
                            Logger.e(TAG, "body:" + response.body().toString());
                        }
                        if (response.errorBody() != null) {
                            try {
                                Logger.e(TAG, "errorBody:" + response.errorBody()
                                        .string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                stopProgressDialog();
                ErrorHandler.getInstance().setException(PersonalEditActivity.this, t);
            }
        });
    }

    private boolean checkInput() {
        if (itemInputEmail.getEmail().size() == 0) {
            Toast.makeText(this, getString(R.string.error_msg_required) + getString(R.string
                    .email), Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    @Override
    public void onSelectPhoneType(ItemInputPhone item) {
        selectedItemInputPhone = item;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Uri photoUri = null;
            Bitmap bitmap = null;
            int photoSize = getResources().getDimensionPixelSize(R.dimen.photo_upload_size_small);
            switch (requestCode) {
                case MyApplication.REQUEST_EDIT:
                    getUserDetail();
                    break;
                // 選擇電話類型
                case MyApplication.REQUEST_SELECT_PHONE_TYPE:
                    String type = data.getStringExtra(MyApplication.INTENT_KEY_PHONE_TYPE);
                    selectedItemInputPhone.setType(type);
                    break;
                // 選擇圖片
                case ImageHelper.REQUEST_SELECT_PICTURE:
                    File file = new File(imageHelper.getImageFilePath(this, data.getData()));
                    photoUri = Uri.fromFile(file);
                    bitmap = imageHelper.getBitmapFromUri(photoUri, photoSize, photoSize);
                    ivMyself.setImageDrawable(ImageTools.getCircleDrawable(getResources(), bitmap));
                    TokenManager.getInstance().getUser().setPhoto(Base64Utils
                            .encodeBitmapToString(ImageTools
                                    .getCroppedBitmap
                                            (bitmap)));
                    break;
                // 拍攝照片
                case ImageHelper.REQUEST_TAKE_PHOTO:
                    photoUri = Uri.fromFile(imageHelper.getImageFile());
                    bitmap = imageHelper.getBitmapFromUri(photoUri, photoSize, photoSize);
                    ivMyself.setImageDrawable(ImageTools.getCircleDrawable(getResources(), bitmap));
                    TokenManager.getInstance().getUser().setPhoto(Base64Utils
                            .encodeBitmapToString(ImageTools
                                    .getCroppedBitmap
                                            (bitmap)));
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void getUserDetail() {
        mDisposable.add(DatabaseHelper.getInstance(this).getUserWithConfigById(TokenManager
                .getInstance().getUser
                        ().getId())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<UserWithConfig>
                        () {

                    @Override
                    public void accept(UserWithConfig user) throws Exception {
                        if (!TextUtils.isEmpty(user.getUser().getPhoto())) {
                            Bitmap bitmap = Base64Utils.decodeToBitmapFromString(TokenManager
                                    .getInstance
                                            ().getUser()
                                    .getPhoto());
                            ivMyself.setImageDrawable(ImageTools.getCircleDrawable(getResources()
                                    , bitmap));
                        }
                        tvName.setText(user.getUser().getShowName());
                        tvTitle.setText(user.getCompany().getName() + "/" + user.getDepartment()
                                .getName
                                        () + "/" + user.getUser().getTitle());
                        SimpleDateFormat dateFormat = TimeFormater.getInstance()
                                .getTimeZoneFormater();
                        if (user.getUser().getTime_difference() > 0) {
                            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+" + user.getUser()
                                    .getTime_difference()));
                        } else {
                            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT" + user.getUser()
                                    .getTime_difference()));
                        }
                        tvTime.setText(getString(R.string.local_time) + " " + dateFormat.format(new
                                Date()));
                        itemInputEmail.clearAllEmail();
                        if (user.getUser().getEmail() != null && user.getUser().getEmail().size()
                                > 0) {
                            for (String email : user.getUser().getEmail()) {
                                itemInputEmail.addEmail(email);
                            }
                        } else {
                            itemInputEmail.addEmail("");
                        }
                        containerPhone.removeAllViews();
                        if (user.getUser().getTel() != null && user.getUser().getTel().size() > 0) {
                            for (Phone phone : user.getUser().getTel()) {
                                addPhone(phone, containerPhone.getChildCount() == 0);
                            }
                        } else {
                            addPhone(null, true);
                        }
                        if (!TextUtils.isEmpty(user.getUser().getPersional_history())) {
                            etProfile.setText(user.getUser().getPersional_history());
                        }
                        if (!TextUtils.isEmpty(user.getUser().getMisc())) {
                            etNote.setText(user.getUser().getMisc());
                        }
                        if (!TextUtils.isEmpty(user.getUser().getPhoto())) {
                            Bitmap bitmap = Base64Utils.decodeToBitmapFromString(user.getUser()
                                    .getPhoto());
                            ivMyself.setImageDrawable(ImageTools.getCircleDrawable(getResources()
                                    , bitmap));
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        showLoginDialog();
                        ErrorHandler.getInstance().setException(PersonalEditActivity.this,
                                throwable);
                    }
                }));
    }

    /**
     * 新增電話
     */
    private void addPhone(Phone phone, boolean isAdd) {
        final ItemInputPhone itemInputPhone = new ItemInputPhone(this);
        if (phone != null)
            itemInputPhone.setPhone(phone);
        itemInputPhone.setSelectPhoneTypeListener(this);
        if (isAdd) {
            itemInputPhone.setFunctionListener(R.mipmap.common_add, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addPhone(null, false);
                }
            });
        } else {
            itemInputPhone.setFunctionListener(R.mipmap.common_removeitem, new View
                    .OnClickListener() {
                @Override
                public void onClick(View v) {
                    containerPhone.removeView(itemInputPhone);
                }
            });
        }
        containerPhone.addView(itemInputPhone);
    }

    private void getSupervisorDetail() {
        mDisposable.add(DatabaseHelper.getInstance(this).getUserWithConfigById(TokenManager
                .getInstance().getUser()
                .getSupervisor_id())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<UserWithConfig>
                        () {

                    @Override
                    public void accept(UserWithConfig user) throws Exception {
                        tvBossName.setText(user.getUser().getShowName());
                        tvBossTitle.setText(user.getCompany().getName() + "/" + user
                                .getDepartment().getName
                                        () + "/" + user.getUser().getTitle());
                        SimpleDateFormat dateFormat = TimeFormater.getInstance()
                                .getTimeZoneFormater();
                        if (user.getUser().getTime_difference() > 0) {
                            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+" + user.getUser()
                                    .getTime_difference()));
                        } else {
                            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT" + user.getUser()
                                    .getTime_difference()));
                        }
                        tvBossTime.setText(getString(R.string.local_time) + " " + dateFormat
                                .format(new
                                        Date()));
                        if (!TextUtils.isEmpty(user.getUser().getPhoto())) {
                            Bitmap bitmap = Base64Utils.decodeToBitmapFromString(user.getUser()
                                    .getPhoto());
                            ivInCharge.setImageDrawable(ImageTools.getCircleDrawable(getResources(),
                                    bitmap));
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ErrorHandler.getInstance().setException(PersonalEditActivity.this,
                                throwable);
                        containerInCharge.setVisibility(View.GONE);
                    }
                }));
    }

    @OnClick(R.id.imageView_myself)
    void showPhotoDialog() {
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // 有相機功能，詢問使用者是要拍攝相片還是開啟相簿
            AlertDialog.Builder builder = new AlertDialog.Builder(this, MyApplication.DIALOG_STYLE);
            builder.setTitle(R.string.select_photo_title);
            builder.setItems(getResources().getStringArray(R.array.select_photo), new
                    DialogInterface
                            .OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    imageHelper.takePhoto();
                                    break;
                                case 1:
                                    imageHelper.selectPhoto();
                                    break;
                            }
                        }
                    });
            builder.create().show();
        } else {
            imageHelper.selectPhoto();
        }
    }
}
