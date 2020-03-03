package tw.com.masterhand.gmorscrm.activity.sample;

import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.URLUtil;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import tw.com.masterhand.gmorscrm.BaseWebCheckActivity;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.stl.STLObject;
import tw.com.masterhand.stl.STLView;


public class Sample3DActivity extends BaseWebCheckActivity {
    @BindView(R.id.container)
    FrameLayout container;

    private STLView stlView = null;
    private STLObject stlObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
        setContentView(R.layout.activity_sample3d);
        final String fileUrl = getIntent().getStringExtra(MyApplication.INTENT_KEY_FILE_URL);
        if (TextUtils.isEmpty(fileUrl)) {
            finish();
            return;
        }
        startProgressDialog();
        // 檢查快取
        File file = new File(getCacheDir(), URLUtil.guessFileName(fileUrl, null, null));
        if (file.exists()) {
            Logger.e(TAG, "get sample from cache:" + URLUtil.guessFileName(fileUrl, null, null));
            int size = (int) file.length();
            byte[] result = new byte[size];
            try {
                BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
                buf.read(result, 0, result.length);
                buf.close();
                stlObject = new STLObject(result, Sample3DActivity.this, new
                        FinishSTL());
                stopProgressDialog();
            } catch (Exception e) {
                Logger.e(TAG, "Exception:" + e.getMessage());
                getSampleFromUrl(fileUrl);
            }
        } else {
            getSampleFromUrl(fileUrl);
        }

    }

    private void getSampleFromUrl(final String fileUrl) {
        Logger.e(TAG, "get sample from fileUrl:" + fileUrl);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(fileUrl).build();
        client.newCall(request).enqueue(new Callback() {
            public void onFailure(Call call, IOException e) {
                Logger.e(TAG, "IOException:" + e.getMessage());
                Toast.makeText(Sample3DActivity.this, R.string.error_msg_unknown, Toast
                        .LENGTH_LONG).show();
                finish();
            }

            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    byte[] result = response.body().bytes();
                    stlObject = new STLObject(result, Sample3DActivity.this, new
                            FinishSTL());
                    File file = new File(getCacheDir(), URLUtil.guessFileName(fileUrl, null,
                            null));
                    FileOutputStream outputStream = new FileOutputStream(file);
                    try {
                        outputStream.write(result);
                    } catch (Exception e) {
                        Toast.makeText(Sample3DActivity.this, R.string.error_msg_unknown, Toast
                                .LENGTH_LONG).show();
                    }finally {
                        outputStream.close();
                    }
                } else {
                    Toast.makeText(Sample3DActivity.this, R.string.error_msg_unknown, Toast
                            .LENGTH_LONG).show();
                    finish();
                }
                stopProgressDialog();
            }
        });
    }

    @Override
    protected void onUserChecked() {

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (stlView != null && stlObject != null) {
            container.removeAllViews();
            stlView = new STLView(Sample3DActivity.this, stlObject);
            container.addView(stlView);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.e(TAG, "onRestoreInstanceState");
        byte[] stlBytes = savedInstanceState.getByteArray("stlBytes");
        if (stlBytes != null) {
            stlObject = new STLObject(stlBytes, this, new FinishSTL());
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (stlView != null) {
            outState.putByteArray("stlBytes", stlObject.getStlBytes());
        }
    }

    class FinishSTL implements STLObject.IFinishCallBack {

        @Override
        public void readstlfinish() {
            if (stlObject != null) {
                if (stlView == null) {
                    stlView = new STLView(Sample3DActivity.this, stlObject);
                    container.addView(stlView);
                } else {
                    stlView.setNewSTLObject(stlObject);
                }
            }
        }
    }

}
