package tw.com.masterhand.gmorscrm.activity.sample;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tw.com.masterhand.gmorscrm.BaseWebCheckActivity;
import tw.com.masterhand.gmorscrm.ImageActivity;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.TokenManager;
import tw.com.masterhand.gmorscrm.api.ApiHelper;
import tw.com.masterhand.gmorscrm.enums.SampleFileType;
import tw.com.masterhand.gmorscrm.fragments.SampleDetailFragment;
import tw.com.masterhand.gmorscrm.model.SampleDetail;
import tw.com.masterhand.gmorscrm.model.SampleFile;
import tw.com.masterhand.gmorscrm.model.SampleWithConfig;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.view.Appbar;

public class SampleInfoActivity extends BaseWebCheckActivity {
    @BindView(R.id.appbar)
    Appbar appbar;
    @BindView(R.id.svImage)
    HorizontalScrollView svImage;
    @BindView(R.id.container3D)
    LinearLayout container3D;
    @BindView(R.id.containerPhoto)
    LinearLayout containerPhoto;
    @BindView(R.id.tv3D)
    TextView tv3D;
    @BindView(R.id.tvPhoto)
    TextView tvPhoto;

    List<SampleFile> imageList;
    SampleFile selected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_info);
        init();
    }

    @Override
    protected void onUserChecked() {

    }

    private void init() {
        Appbar appbar = findViewById(R.id.appbar);
        appbar.setTitle(getString(R.string.sample_detail));

        String sampleId = getIntent().getStringExtra(MyApplication.INTENT_KEY_SAMPLE);
        if (TextUtils.isEmpty(sampleId)) {
            finish();
            return;
        }
        mDisposable.add(DatabaseHelper.getInstance(this).getSampleById(sampleId).observeOn
                (AndroidSchedulers.mainThread()).subscribe(new Consumer<SampleWithConfig>() {

            @Override
            public void accept(SampleWithConfig sample) throws Exception {
                getSupportFragmentManager().beginTransaction().add(R.id.containerFragment,
                        SampleDetailFragment.newInstance(sample, TokenManager.getInstance().getUser().getId()))
                        .commit();
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                ErrorHandler.getInstance().setException(SampleInfoActivity.this, throwable);
            }
        }));
        Call<JSONObject> call = ApiHelper.getInstance().getSampleApi().getSample(sampleId);
        call.enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                try {
                    switch (response.code()) {
                        case 200:
                            JSONObject result = response.body();
                            Logger.e(TAG, "result:" + result.toString());
                            int success = result.getInt("success");
                            if (success == 1) {
                                SampleDetail sampleDetail = gson.fromJson(result.getString
                                        ("data"), SampleDetail.class);
                                imageList = sampleDetail.getFiles();
                                updateImageList();
                            } else {
                                onNoData();
                            }
                            break;
                        default:
                            Toast.makeText(SampleInfoActivity.this, response.code() + ":" +
                                            response.message(),
                                    Toast.LENGTH_SHORT).show();
                            onNoData();
                            break;
                    }
                } catch (Exception e) {
                    ErrorHandler.getInstance().setException(SampleInfoActivity.this, e);
                    onNoData();
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                ErrorHandler.getInstance().setException(SampleInfoActivity.this, t);
                onNoData();
            }
        });
    }

    void onNoData() {
        svImage.setVisibility(View.GONE);
    }

    private void updateImageList() {
        container3D.removeAllViews();
        containerPhoto.removeAllViews();
        if (imageList.size() == 0) {
            onNoData();
            return;
        }
        for (final SampleFile item : imageList) {
            ImageView ivItem = new ImageView(this);
            ivItem.setScaleType(ImageView.ScaleType.FIT_CENTER);
            ivItem.setAdjustViewBounds(true);

            ivItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selected = item;
                    expand();
                }
            });
            if (item.getType() == SampleFileType.FILE) {
                ImageLoader.getInstance().displayImage(item.getPreview_url(), ivItem);
                container3D.addView(ivItem);
            } else {
                ImageLoader.getInstance().displayImage(item.getFile_url(), ivItem);
                containerPhoto.addView(ivItem);
            }
        }
        if (container3D.getChildCount() == 0) {
            tv3D.setVisibility(View.GONE);
        } else {
            tv3D.setVisibility(View.VISIBLE);
        }
        if (containerPhoto.getChildCount() == 0) {
            tvPhoto.setVisibility(View.GONE);
        } else {
            tvPhoto.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        appbar.invalidate();
    }

    void expand() {
        if (selected.getType() == SampleFileType.FILE) {
            Intent intent = new Intent(this, Sample3DActivity.class);
            intent.putExtra(MyApplication.INTENT_KEY_FILE_URL, selected.getFile_url());
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, ImageActivity.class);
            intent.putExtra(MyApplication.INTENT_KEY_IMAGE_URL, selected.getFile_url());
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

}
