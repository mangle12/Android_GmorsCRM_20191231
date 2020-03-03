package tw.com.masterhand.gmorscrm.fragments;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nostra13.universalimageloader.core.ImageLoader;

import butterknife.Unbinder;
import io.reactivex.disposables.CompositeDisposable;
import tw.com.masterhand.gmorscrm.BaseActivity;
import tw.com.masterhand.gmorscrm.ImageActivity;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.room.record.File;
import tw.com.masterhand.gmorscrm.tools.Base64Utils;
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.tools.PreferenceHelper;
import tw.com.masterhand.gmorscrm.tools.TimeFormater;

public class BaseFragment extends Fragment {
    String TAG;
    // 暫存設定檔
    PreferenceHelper preferenceHelper;

    public final CompositeDisposable mDisposable = new CompositeDisposable();
    public Unbinder unbinder;
    // Gson
    public Gson gson;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = getClass().getSimpleName();
        preferenceHelper = new PreferenceHelper(getActivity());
        gson = new GsonBuilder().setDateFormat(TimeFormater.DATABASE_DATE_TIME_STRING).create();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null)
            unbinder.unbind();
    }

    @Override
    public void onStop() {
        super.onStop();
        mDisposable.clear();
    }

    /**
     * 無資料時顯示畫面
     */
    protected View getEmptyTextView(String msg) {
        TextView tvEmpty = new TextView(getActivity());
        if (TextUtils.isEmpty(msg))
            tvEmpty.setText(getString(R.string.error_msg_empty));
        else
            tvEmpty.setText(msg);
        return tvEmpty;
    }

    /**
     * 產生相關圖片項目
     */
    public View generatePhotoItem(final File item) {
        int photoSize = getResources().getDimensionPixelSize(R.dimen.photo_size);
        final ImageView ivPhoto = new ImageView(getActivity());
        ivPhoto.setBackgroundColor(Color.WHITE);
        ivPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);

        if (!TextUtils.isEmpty(item.getFile())) {
            Logger.e(TAG, "load photo from base64");
            ivPhoto.setImageBitmap(Base64Utils.decodeToBitmapFromString(item.getFile()));
            ivPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), ImageActivity.class);
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
                    Intent intent = new Intent(getActivity(), ImageActivity.class);
                    intent.putExtra(MyApplication.INTENT_KEY_IMAGE_URL, item.getFileUrl());
                    startActivity(intent);
                }
            });
        }
        ivPhoto.setLayoutParams(new LinearLayout.LayoutParams(photoSize / 4 * 7,
                photoSize));
        return ivPhoto;
    }
}
