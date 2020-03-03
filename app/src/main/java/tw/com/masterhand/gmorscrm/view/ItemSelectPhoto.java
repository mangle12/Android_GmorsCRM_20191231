package tw.com.masterhand.gmorscrm.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.enums.FileType;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.record.File;
import tw.com.masterhand.gmorscrm.tools.Base64Utils;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.tools.ImageHelper;
import tw.com.masterhand.gmorscrm.tools.Logger;

import static android.app.Activity.RESULT_OK;

public class ItemSelectPhoto extends LinearLayout {
    final String TAG = getClass().getSimpleName();

    @BindView(R.id.btnAdd)
    ImageButton btnAdd;
    @BindView(R.id.containerPhoto)
    LinearLayout containerPhoto;

    Activity context;
    List<File> files;
    ImageHelper imageHelper;

    public ItemSelectPhoto(Context context) {
        super(context);
        init(context);
    }

    public ItemSelectPhoto(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ItemSelectPhoto(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context mContext) {
        // 連接畫面
        View view = inflate(getContext(), R.layout.item_select_photo, this);
        ButterKnife.bind(this, view);
        if (isInEditMode())
            return;
        context = (Activity) mContext;
        imageHelper = new ImageHelper(context);
        files = new ArrayList<>();
    }

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> input) {
        files = input;
        containerPhoto.removeAllViews();
        for (File file : files) {
            generatePhotoItem(file);
        }
    }

    /**
     * 新增相關圖片
     */
    @OnClick(R.id.btnAdd)
    void showPhotoDialog(View view) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // 有相機功能，詢問使用者是要拍攝相片還是開啟相簿
            AlertDialog.Builder builder = new AlertDialog.Builder(context, MyApplication.DIALOG_STYLE);
            builder.setTitle(R.string.relative_photo);//關聯照片
            builder.setItems(getResources().getStringArray(R.array.select_photo), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    imageHelper.takePhoto();//拍攝這片
                                    break;
                                case 1:
                                    imageHelper.selectPhoto();//選擇圖片
                                    break;
                            }
                        }
                    });
            builder.create().show();
        } else {
            imageHelper.selectPhoto();
        }
    }

    /**
     * 產生相關圖片項目
     */
    private void generatePhotoItem(final File item) {
        int photoSize = getResources().getDimensionPixelSize(R.dimen.photo_size);
        final ImageView ivPhoto = new ImageView(context);
        ivPhoto.setBackgroundColor(Color.WHITE);
        ivPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
        ivPhoto.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext(), MyApplication.DIALOG_STYLE);
                builder.setTitle(R.string.remove).setMessage(context.getString(R.string
                        .remove_confirm) + "?").setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        files.remove(item);
                                        containerPhoto.removeView(ivPhoto);
                                        if (!TextUtils.isEmpty(item.getId())) {
                                            Disposable disposable = DatabaseHelper.getInstance(context).deleteFileById(item.getId())
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .subscribe(new Consumer<Boolean>() {

                                                @Override
                                                public void accept(Boolean isDelete) throws
                                                        Exception {

                                                    if (!isDelete) {
                                                        Logger.e(TAG, "not delete photo.");
                                                    }
                                                }
                                            }, new Consumer<Throwable>() {
                                                @Override
                                                public void accept(Throwable throwable) throws
                                                        Exception {
                                                    ErrorHandler.getInstance().setException(context, throwable);
                                                }
                                            });
                                        }
                                    }
                                });
                builder.create().show();
                return false;
            }
        });

        containerPhoto.addView(ivPhoto, new LinearLayout.LayoutParams(photoSize, photoSize));
        if (!TextUtils.isEmpty(item.getFile())) {
            ivPhoto.setImageBitmap(Base64Utils.decodeToBitmapFromString(item.getFile()));
        } else {
            ImageLoader.getInstance().displayImage(item.getFileUrl(), ivPhoto);
        }
    }

    public void onActivityResult(final int requestCode, int resultCode, Intent data) {
        Uri photoUri;
        java.io.File file = null;
        Bitmap bitmap = null;
        if (resultCode == RESULT_OK) {
            int photoSize = getResources().getDimensionPixelSize(R.dimen.photo_upload_size);
            switch (requestCode) {
                // 選擇圖片
                case ImageHelper.REQUEST_SELECT_PICTURE:
                    try {
                        file = new java.io.File(imageHelper.getImageFilePath(context, data.getData()));
                        photoUri = Uri.fromFile(file);
                        Logger.e(TAG, "get from REQUEST_SELECT_PICTURE:" + photoUri.getPath());
                        bitmap = imageHelper.getBitmapFromUri(photoUri, photoSize, photoSize);
                    } catch (Exception e) {
                        Log.e(TAG, "Exception:" + e.getMessage());
                        Toast.makeText(context, "can't get picture:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    break;
                // 拍攝照片
                case ImageHelper.REQUEST_TAKE_PHOTO:
                    try {
                        file = imageHelper.getImageFile();
                        photoUri = Uri.fromFile(imageHelper.getImageFile());
                        Logger.e(TAG, "get from REQUEST_TAKE_PHOTO:" + photoUri.getPath());
                        bitmap = imageHelper.getBitmapFromUri(photoUri, photoSize, photoSize);
                    } catch (Exception e) {
                        Logger.e(TAG, "Exception:" + e.getMessage());
                        Toast.makeText(context, "can't get picture:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
            if (bitmap != null && file != null) {
                File item = new File();
                item.setFile(Base64Utils.encodeBitmapToString(bitmap));
                item.setType(FileType.PHOTO);
                item.setName(context.getString(R.string.relative_photo));
                files.add(item);
                generatePhotoItem(item);
            } else {
                Logger.e(TAG, "bitmap is null or file is null");
            }
        }
    }
}
