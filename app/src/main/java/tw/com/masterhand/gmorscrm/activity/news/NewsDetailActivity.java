package tw.com.masterhand.gmorscrm.activity.news;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONObject;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tw.com.masterhand.gmorscrm.BaseWebCheckActivity;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.TokenManager;
import tw.com.masterhand.gmorscrm.api.ApiHelper;
import tw.com.masterhand.gmorscrm.enums.NewsType;
import tw.com.masterhand.gmorscrm.model.Image;
import tw.com.masterhand.gmorscrm.model.NewsDetail;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.tools.TimeFormater;
import tw.com.masterhand.gmorscrm.view.Appbar;

public class NewsDetailActivity extends BaseWebCheckActivity {
    @BindView(R.id.appbar)
    Appbar appbar;
    @BindView(R.id.textView_title)
    TextView tvTitle;
    @BindView(R.id.textView_content)
    TextView tvContent;
    @BindView(R.id.textView_date)
    TextView tvDate;
    @BindView(R.id.textView_editor)
    TextView tvEditor;
    @BindView(R.id.container_photo)
    LinearLayout containerPhoto;

    NewsType type;
    String id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        init();
    }

    @Override
    protected void onUserChecked() {
        appbar.invalidate();
        switch (type) {
            case ANNOUNCE:
                ApiHelper.getInstance().getNewsApi().getAnnounceDetail(id, TokenManager
                        .getInstance().getToken())
                        .enqueue(callback);
                break;
            case NOTIFY:
                ApiHelper.getInstance().getNewsApi().getNotifyDetail(id, TokenManager.getInstance
                        ().getToken())
                        .enqueue(callback);
                break;
        }
    }

    private void init() {
        int typeValue = getIntent().getIntExtra(MyApplication.INTENT_KEY_TYPE, NewsType.ANNOUNCE
                .getValue());
        type = NewsType.getNewsTypeByValue(typeValue);
        id = getIntent().getStringExtra(MyApplication.INTENT_KEY_ID);
        if (TextUtils.isEmpty(id) || type == null) {
            finish();
            return;
        }
        appbar.setTitle(getString(type.getTitle()));
    }

    Callback<JSONObject> callback = new Callback<JSONObject>() {

        @Override
        public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
            try {
//                    Logger.e(TAG, "response code:" + response.code());
//                    Logger.e(TAG, "response:" + response.body().toString());
                switch (response.code()) {
                    case 200:
                        JSONObject result = response.body();
                        int success = result.getInt("success");
                        if (success == 1) {
                            NewsDetail detail = gson.fromJson(result.getString("row"),
                                    NewsDetail.class);
                            tvTitle.setText(detail.getTitle());
                            tvDate.setText(TimeFormater.getInstance().toDateFormat(detail
                                    .getUpdated_at()));
                            tvEditor.setText(getString(R.string.editor) + ":" +
                                    detail.getFrom());
                            tvContent.setText(detail.getMsg());
                            for (Image image : detail.getImages()) {
                                ImageView iv = generatePhoto();
                                imageLoader.displayImage(image.getFile_url(), iv);
                            }
                        }
                        break;
                    default:
                        Logger.e(TAG, "response code:" + response.code());
                        Logger.e(TAG, "response message:" + response.message());
                }
            } catch (Exception e) {
                ErrorHandler.getInstance().setException(NewsDetailActivity.this, e);
            }
        }

        @Override
        public void onFailure(Call<JSONObject> call, Throwable t) {
            ErrorHandler.getInstance().setException(NewsDetailActivity.this, t);
        }
    };

    private ImageView generatePhoto() {
        ImageView iv = new ImageView(this);
        containerPhoto.addView(iv);
        return iv;
    }

}
