package tw.com.masterhand.gmorscrm.activity.personal;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import tw.com.masterhand.gmorscrm.BaseUserCheckActivity;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.TokenManager;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.tools.Base64Utils;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.tools.ImageTools;
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.tools.TimeFormater;
import tw.com.masterhand.gmorscrm.model.Phone;
import tw.com.masterhand.gmorscrm.model.UserWithConfig;
import tw.com.masterhand.gmorscrm.view.Appbar;

public class PersonalActivity extends BaseUserCheckActivity {
    @BindView(R.id.appbar)
    Appbar appbar;
    @BindView(R.id.container_email)
    LinearLayout containerMail;
    @BindView(R.id.container_phone)
    LinearLayout containerPhone;
    @BindView(R.id.linearLayout_in_charge)
    LinearLayout containerInCharge;
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
    @BindView(R.id.textView_profile)
    TextView tvProfile;
    @BindView(R.id.textView_other)
    TextView tvOther;
    @BindView(R.id.imageView_in_charge)
    ImageView ivInCharge;
    @BindView(R.id.imageView_myself)
    ImageView ivMyself;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);
        init();
    }

    @Override
    protected void onUserChecked() {
        appbar.invalidate();
        getUserDetail();
        getSupervisorDetail();
    }

    private void init() {
        appbar.setTitle(getString(R.string.title_activity_personal));
        appbar.addFunction(R.mipmap.common_edit, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PersonalActivity.this, PersonalEditActivity.class);
                startActivityForResult(intent, MyApplication.REQUEST_EDIT);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case MyApplication.REQUEST_EDIT:
                    getUserDetail();
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void getUserDetail() {
        Logger.e(TAG, "user:" + gson.toJson(TokenManager.getInstance().getUser()));
        mDisposable.add(DatabaseHelper.getInstance(this).getUserWithConfigById(TokenManager
                .getInstance().getUser().getId())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<UserWithConfig>
                        () {

                    @Override
                    public void accept(UserWithConfig user) throws Exception {
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
                        if (user.getUser().getEmail() != null && user.getUser().getEmail().size()
                                > 0) {
                            containerMail.removeAllViews();
                            for (String mail : user.getUser().getEmail()) {
                                if (TextUtils.isEmpty(mail))
                                    continue;
                                TextView tvMail = new TextView(PersonalActivity.this);
                                tvMail.setText(mail);
                                tvMail.setAutoLinkMask(Linkify.EMAIL_ADDRESSES);
                                tvMail.setMovementMethod(LinkMovementMethod.getInstance());
                                containerMail.addView(tvMail);
                            }
                        }
                        if (user.getUser().getTel() != null && user.getUser().getTel().size() > 0) {
                            containerPhone.removeAllViews();
                            for (Phone phone : user.getUser().getTel()) {
                                TextView tvPhone = new TextView(PersonalActivity.this);
                                tvPhone.setText(phone.getShowPhone());
                                tvPhone.setAutoLinkMask(Linkify.PHONE_NUMBERS);
                                tvPhone.setMovementMethod(LinkMovementMethod.getInstance());
                                containerPhone.addView(tvPhone);
                            }
                        }
                        if (!TextUtils.isEmpty(user.getUser().getPersional_history())) {
                            tvProfile.setText(user.getUser().getPersional_history());
                        }
                        if (!TextUtils.isEmpty(user.getUser().getMisc())) {
                            tvOther.setText(user.getUser().getMisc());
                        }
                        if (!TextUtils.isEmpty(TokenManager.getInstance().getUser()
                                .getPhoto())) {
                            Bitmap bitmap = Base64Utils.decodeToBitmapFromString(TokenManager
                                    .getInstance().getUser()
                                    .getPhoto());
                            ivMyself.setImageDrawable(ImageTools.getCircleDrawable(getResources()
                                    , bitmap));
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        showLoginDialog();
                        ErrorHandler.getInstance().setException(PersonalActivity.this, throwable);
                    }
                }));
    }

    private void getSupervisorDetail() {
        if (!TextUtils.isEmpty(TokenManager.getInstance().getUser().getSupervisor_id())) {
            mDisposable.add(DatabaseHelper.getInstance(this).getUserWithConfigById(TokenManager
                    .getInstance()
                    .getUser()
                    .getSupervisor_id())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<UserWithConfig>
                            () {

                        @Override
                        public void accept(UserWithConfig user) throws Exception {
                            tvBossName.setText(user.getUser().getShowName());
                            tvBossTitle.setText(user.getCompany().getName() + "/" + user
                                    .getDepartment()
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
                            tvBossTime.setText(getString(R.string.local_time) + " " + dateFormat
                                    .format(new
                                            Date()));
                            if (!TextUtils.isEmpty(user.getUser().getPhoto())) {
                                Bitmap bitmap = Base64Utils.decodeToBitmapFromString(user.getUser()
                                        .getPhoto());
                                ivInCharge.setImageDrawable(ImageTools.getCircleDrawable
                                        (getResources(),
                                                bitmap));
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            containerInCharge.setVisibility(View.GONE);
                            ErrorHandler.getInstance().setException(PersonalActivity.this,
                                    throwable);
                        }
                    }));
        } else {
            containerInCharge.setVisibility(View.GONE);
        }
    }
}
