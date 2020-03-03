package tw.com.masterhand.gmorscrm;

import android.os.Bundle;
import android.text.TextUtils;

import com.nostra13.universalimageloader.core.ImageLoader;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.record.File;
import tw.com.masterhand.gmorscrm.tools.Base64Utils;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.view.ScalableImageView;

public class ImageActivity extends BaseActivity {
    @BindView(R.id.ivImage)
    ScalableImageView ivImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
        setContentView(R.layout.activity_image);
        ivImage.setFit(true);
        String imageUrl = getIntent().getStringExtra(MyApplication.INTENT_KEY_IMAGE_URL);
        if (!TextUtils.isEmpty(imageUrl))
            ImageLoader.getInstance().displayImage(imageUrl, ivImage);
        else {
            String fileId = getIntent().getStringExtra(MyApplication.INTENT_KEY_IMAGE);
            if (!TextUtils.isEmpty(fileId))
                getFile(fileId);
        }
    }

    private void getFile(String fileId) {
        mDisposable.add(DatabaseHelper.getInstance(this).getFileById(fileId).observeOn
                (AndroidSchedulers.mainThread()).subscribe(new Consumer<File>() {

            @Override
            public void accept(File file) throws Exception {
                ivImage.setImageBitmap(Base64Utils.decodeToBitmapFromString(file.getFile()));
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                ErrorHandler.getInstance().setException(ImageActivity.this, throwable);
            }
        }));
    }
}
