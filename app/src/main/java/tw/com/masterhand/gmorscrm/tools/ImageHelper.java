package tw.com.masterhand.gmorscrm.tools;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import rebus.permissionutils.PermissionEnum;
import rebus.permissionutils.PermissionManager;
import rebus.permissionutils.PermissionUtils;
import rebus.permissionutils.SimpleCallback;
import tw.com.masterhand.gmorscrm.BuildConfig;
import tw.com.masterhand.gmorscrm.R;

public class ImageHelper {

    final String TAG = getClass().getSimpleName();
    public static final int REQUEST_TAKE_PHOTO = 800, REQUEST_SELECT_PICTURE = 801;

    Activity context;
    private File photoFile;

    public ImageHelper(Activity activity) {
        this.context = activity;
    }

    public Uri getLogo() {
        File logoFile = new File(context.getFilesDir(), "logo.png");
        return Uri.fromFile(logoFile);
    }

    public boolean saveLogo(Bitmap image) {
        File logoFile = new File(context.getFilesDir(), "logo.png");
        return saveBitmap(logoFile, image);
    }

    public Uri getHeader() {
        File hearderFile = new File(context.getFilesDir(), "header.png");
        return Uri.fromFile(hearderFile);
    }

    public boolean saveHeader(Bitmap image) {
        File hearderFile = new File(context.getFilesDir(), "header.png");
        return saveBitmap(hearderFile, image);
    }

    private boolean saveBitmap(File dst, Bitmap image) {
        if (dst == null) {
            Log.d(TAG, "Error creating media file, check storage permissions: ");// e.getMessage());
            return false;
        }
        try {
            FileOutputStream fos = new FileOutputStream(dst);
            image.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            return true;
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
            return false;
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
            return false;
        }
    }

    public File getImageFile() {
        if (photoFile == null) {
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                Log.e(TAG, "IOException:" + e.getMessage());
            }
        }
        return photoFile;
    }

    public File createImageFile() throws IOException {
        String imageFileName = "JPEG_";
        File storageDir = context.getExternalFilesDir("downloads");
        File file = new File(storageDir, imageFileName + new Date().getTime() + ".jpg");
        return file;
    }

    public Bitmap getBitmapFromUri(Uri uri, int mWidth, int mHeight) {
        BitmapUtils.BitmapSampled decodeResult = BitmapUtils.decodeSampledBitmap(context, uri, mWidth, mHeight);
        BitmapUtils.RotateBitmapResult rotateResult = BitmapUtils.rotateBitmapByExif(decodeResult.bitmap, context, uri);
        return rotateResult.bitmap;
    }

    public String getImageFilePath(Context context, Uri photoUri) {
        String imagePath = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (DocumentsContract.isDocumentUri(context, photoUri)) {
                String docId = DocumentsContract.getDocumentId(photoUri);
                if ("com.android.providers.media.documents".equals(photoUri.getAuthority())) {
                    //Log.d(TAG, uri.toString());
                    String id = docId.split(":")[1];
                    String selection = MediaStore.Images.Media._ID + "=" + id;
                    imagePath = getImagePath(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
                } else if ("com.android.providers.downloads.documents".equals(photoUri.getAuthority())) {
                    //Log.d(TAG, uri.toString());
                    Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.parseLong(docId));
                    imagePath = getImagePath(context, contentUri, null);
                }
            } else if ("content".equalsIgnoreCase(photoUri.getScheme())) {
                //Log.d(TAG, "content: " + uri.toString());
                imagePath = getImagePath(context, photoUri, null);
            }
        } else {
            imagePath = getImagePath(context, photoUri, null);
        }
        return imagePath;
    }

    private String getImagePath(Context context, Uri uri, String selection) {
        String path = null;
        Cursor cursor = context.getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    public Bitmap getBitmapFromFile(String filePath) {
        try {
            Bitmap bitmap;
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            bitmap = BitmapFactory.decodeFile(filePath);
            return compressImage(bitmap);
        } catch (Exception e) {
            Log.e(TAG, "Exception" + e.getMessage());
            return null;
        }
    }

    public int getRotationDegree(Uri uri) {
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(uri.getPath());
            int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
                return 90;
            } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
                return 180;
            } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
                return 270;
            }
            return 0;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 1000) {  //循环判断如果压缩后图片是否大于1000kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            //第一个参数 ：图片格式 ，第二个参数： 图片质量，100为最高，0为最差  ，第三个参数：保存压缩后的数据的流
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        //把压缩后的数据baos存放到ByteArrayInputStream中
        return BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
    }

    /**
         * 選擇照片
         */
    public void selectPhoto() {
        PermissionEnum[] permission = {PermissionEnum.WRITE_EXTERNAL_STORAGE};
        boolean granted = PermissionUtils.isGranted(context, permission);//是否具有權限
        if (granted) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivityForResult(intent, REQUEST_SELECT_PICTURE);
            } else {
                Toast.makeText(context, context.getString(R.string.error_msg_handle_intent), Toast.LENGTH_SHORT).show();
            }
        } else {
            PermissionManager.with(context).permission(permission).askAgain(true).callback(new SimpleCallback() {
                        @Override
                        public void result(boolean allPermissionsGranted) {
                            Log.e(TAG, "allPermissionsGranted:" + allPermissionsGranted);
                            if (allPermissionsGranted)
                                selectPhoto();
                            else
                                Toast.makeText(context, context.getString(R.string.error_msg_permission), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .ask();
        }
    }

    /**
         * 拍攝照片
         */
    public void takePhoto() {
        PermissionEnum[] permission = {PermissionEnum.WRITE_EXTERNAL_STORAGE, PermissionEnum.CAMERA};
        boolean granted = PermissionUtils.isGranted(context, permission);//是否具有權限
        Log.e(TAG, "isGranted:" + granted);
        if (granted) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//開啟相機介面
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                try {
                    photoFile = createImageFile();
                } catch (Exception ex) {
                    Log.e(TAG, "Exception:" + ex.getMessage());
                    Toast.makeText(context, "can't get picture:" + ex.getMessage(), Toast.LENGTH_SHORT).show();
                }

                if (photoFile != null) {
                    Uri photoUri;

                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                        //Android 版本小於24 (7.0)
                        photoUri = Uri.fromFile(photoFile);
                    } else {
                        photoUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", photoFile);
                    }

                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    context.startActivityForResult(intent, REQUEST_TAKE_PHOTO);
                } else {
                    Toast.makeText(context, "can't get picture:photoFile is null", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, context.getString(R.string.error_msg_handle_intent), Toast.LENGTH_SHORT).show();
            }
        } else {
            //請求權限
            PermissionManager.with(context).permission(permission).askAgain(true).callback(new SimpleCallback() {
                        @Override
                        public void result(boolean allPermissionsGranted) {
                            Log.e(TAG, "allPermissionsGranted:" + allPermissionsGranted);
                            if (allPermissionsGranted)
                                takePhoto();
                            else
                                Toast.makeText(context, context.getString(R.string.error_msg_permission), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .ask();
        }
    }
}
