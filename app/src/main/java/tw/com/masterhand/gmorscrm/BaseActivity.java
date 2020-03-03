package tw.com.masterhand.gmorscrm;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nostra13.universalimageloader.core.ImageLoader;

import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.fabric.sdk.android.Fabric;
import io.reactivex.disposables.CompositeDisposable;
import rebus.permissionutils.PermissionManager;
import rebus.permissionutils.PermissionUtils;
import tw.com.masterhand.gmorscrm.room.record.File;
import tw.com.masterhand.gmorscrm.tools.Base64Utils;
import tw.com.masterhand.gmorscrm.tools.DialogBuilder;
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.tools.PreferenceHelper;
import tw.com.masterhand.gmorscrm.tools.TimeFormater;
import tw.com.masterhand.gmorscrm.tools.UnitChanger;

public class BaseActivity extends AppCompatActivity {
    public String TAG;
    // 暫存設定檔
    public PreferenceHelper preferenceHelper;
    // 處理中顯示
    public SweetAlertDialog progressDialog;
    // 圖片載入器
    public ImageLoader imageLoader;
    // Gson
    public Gson gson;

    public final CompositeDisposable mDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        TAG = getClass().getSimpleName();
        preferenceHelper = new PreferenceHelper(this);
        imageLoader = ImageLoader.getInstance();
        gson = new GsonBuilder().setDateFormat(TimeFormater.DATABASE_DATE_TIME_STRING).create();
        // 設定螢幕方向
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getCallingActivity() != null) {
            Logger.e(TAG, "come from:" + getCallingActivity().getClassName());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopProgressDialog();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mDisposable.clear();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.handleResult(requestCode, permissions, grantResults);
    }

    /**
     * 權限被拒絕後顯示
     */
    protected void showPermissionsDeniedDialog() {
        DialogBuilder.create(this, getString(R.string.app_name), getString(R.string
                        .msg_permission_denied)
                , getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        BaseActivity.this.finish();
                    }
                }, null, null).show();
    }

    /**
     * 權限永被拒絕先詢問是否前往設定頁開啟權限
     */
    protected void showPermissionsDeniedForeverDialog() {
        DialogBuilder.create(this, getString(R.string.app_name), getString(R.string
                        .msg_permission_denied_forever)
                , getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        PermissionUtils.openApplicationSettings(BaseActivity.this, R.class
                                .getPackage().getName());
                    }
                }, getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        BaseActivity.this.finish();
                    }
                }).show();
    }

    /**
     * 無資料時顯示畫面
     */
    protected View getEmptyImageView(String msg) {
        TextView tvEmpty = new TextView(this);
        if (TextUtils.isEmpty(msg))
            tvEmpty.setText(getString(R.string.error_msg_empty));
        else
            tvEmpty.setText(msg);
        tvEmpty.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.mipmap.common_nodata, 0,
                0);
        tvEmpty.setGravity(Gravity.CENTER);
        tvEmpty.setCompoundDrawablePadding(UnitChanger.dpToPx(8));
        tvEmpty.setPadding(0, UnitChanger.dpToPx(50), 0, UnitChanger.dpToPx(50));
        return tvEmpty;
    }

    /**
     * 無資料時顯示畫面
     */
    protected View getEmptyTextView(String msg) {
        TextView tvEmpty = new TextView(this);
        if (TextUtils.isEmpty(msg))
            tvEmpty.setText(getString(R.string.error_msg_empty));
        else
            tvEmpty.setText(msg);
        return tvEmpty;
    }

    /**
     * 產生人員項目
     */
    protected View generatePeopleItem(String name) {
        TextView tvPeople = new TextView(this);
        tvPeople.setText(name);
        tvPeople.setPaddingRelative(UnitChanger.dpToPx(5), UnitChanger.dpToPx(3), UnitChanger
                .dpToPx(5), UnitChanger.dpToPx(3));
        tvPeople.setBackgroundResource(R.drawable.bg_gray_corner);
        return tvPeople;
    }

    /**
     * 產生相關圖片項目
     */
    public View generatePhotoItem(final File item) {
        int photoSize = getResources().getDimensionPixelSize(R.dimen.photo_size);
        final ImageView ivPhoto = new ImageView(this);
        ivPhoto.setBackgroundColor(Color.WHITE);
        ivPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);

        if (!TextUtils.isEmpty(item.getFile())) {
            Logger.e(TAG, "load photo from base64");
            ivPhoto.setImageBitmap(Base64Utils.decodeToBitmapFromString(item.getFile()));
            ivPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(BaseActivity.this, ImageActivity.class);
                    intent.putExtra(MyApplication.INTENT_KEY_IMAGE, item.getId());
                    startActivity(intent);
                }
            });
        } else {
            Logger.e(TAG, "load photo from url");
            ImageLoader.getInstance().displayImage(item.getFileUrl(), ivPhoto);
            ivPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(BaseActivity.this, ImageActivity.class);
                    intent.putExtra(MyApplication.INTENT_KEY_IMAGE_URL, item.getFileUrl());
                    startActivity(intent);
                }
            });
        }
        ivPhoto.setLayoutParams(new LinearLayout.LayoutParams(photoSize / 4 * 7,
                photoSize));
        return ivPhoto;
    }

    /**
     * 產生文件項目
     */
    protected View generateFileItem(final File file) {
        TextView tvDoc = new TextView(this);
        tvDoc.setText(file.getName());
        tvDoc.setPaddingRelative(UnitChanger.dpToPx(5), UnitChanger.dpToPx(3), UnitChanger
                .dpToPx(5), UnitChanger.dpToPx(3));
        tvDoc.setBackgroundResource(R.drawable.bg_gray_corner);
        tvDoc.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams
                .WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        return tvDoc;
    }

    protected void startProgressDialog() {
        try {
            if (progressDialog == null) {
                progressDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
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

    protected void showUncompleteDialog() {
        Toast.makeText(this, "此功能尚未開啟", Toast.LENGTH_SHORT).show();
    }

}
