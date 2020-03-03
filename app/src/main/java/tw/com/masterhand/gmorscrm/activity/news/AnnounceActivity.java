package tw.com.masterhand.gmorscrm.activity.news;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.CompletableObserver;
import io.reactivex.disposables.Disposable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tw.com.masterhand.gmorscrm.BaseWebCheckActivity;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.TokenManager;
import tw.com.masterhand.gmorscrm.api.ApiHelper;
import tw.com.masterhand.gmorscrm.enums.NewsType;
import tw.com.masterhand.gmorscrm.model.Announce;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.local.ReadLog;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.tools.TimeFormater;
import tw.com.masterhand.gmorscrm.view.Appbar;

public class AnnounceActivity extends BaseWebCheckActivity {
    @BindView(R.id.appbar)
    Appbar appbar;
    @BindView(R.id.container)
    LinearLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announce);
        init();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onUserChecked() {
        appbar.invalidate();
        updateList();
    }

    private void init() {
        appbar.setTitle(getString(R.string.news_announce));
    }

    private void updateList() {
        container.removeAllViews();
        ApiHelper.getInstance().getNewsApi().getAnnounceList(TokenManager.getInstance().getToken
                ()).enqueue(new Callback<JSONObject>() {

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
                                List<Announce> list = gson.fromJson(result.getString("list"), new
                                        TypeToken<List<Announce>>() {
                                        }.getType());
                                Collections.sort(list, new Comparator<Announce>() {
                                    @Override
                                    public int compare(Announce o1, Announce o2) {
                                        return o2.getUpdated_at().compareTo(o1.getUpdated_at());
                                    }
                                });
                                List<ReadLog> readLogs = new ArrayList<>();
                                for (Announce announce : list) {
                                    container.addView(generateItem(announce));
                                    ReadLog log = new ReadLog();
                                    log.setParent_id(announce.getId());
                                    readLogs.add(log);
                                }
                                DatabaseHelper.getInstance(AnnounceActivity.this).saveReadLog
                                        (readLogs).subscribe(new CompletableObserver() {
                                    @Override
                                    public void onSubscribe(Disposable d) {
                                        mDisposable.add(d);
                                    }

                                    @Override
                                    public void onComplete() {
                                        Logger.e(TAG, "save readLog");
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        Logger.e(TAG, "save readLog error:" + e.getMessage());
                                    }
                                });
                            }
                            break;
                        case ApiHelper.ERROR_TOKEN_EXPIRED:
                            showLoginDialog();
                            break;
                        default:
                    }
                } catch (Exception e) {
                    ErrorHandler.getInstance().setException(AnnounceActivity.this, e);
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                ErrorHandler.getInstance().setException(AnnounceActivity.this, t);
            }
        });
    }

    private View generateItem(final Announce announce) {
        View item = getLayoutInflater().inflate(R.layout.item_announce, container, false);
        ImageView ivIcon = item.findViewById(R.id.imageView_icon);
        TextView tvTitle = item.findViewById(R.id.textView_title);
        TextView tvDate = item.findViewById(R.id.textView_date);
        ImageLoader.getInstance().displayImage(announce.getUrl(), ivIcon);
        tvTitle.setText(announce.getTitle());
        tvDate.setText(TimeFormater.getInstance().toDateFormat(announce.getUpdated_at()));
        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AnnounceActivity.this, NewsDetailActivity.class);
                intent.putExtra(MyApplication.INTENT_KEY_TYPE, NewsType.ANNOUNCE.getValue());
                intent.putExtra(MyApplication.INTENT_KEY_ID, announce.getId());
                startActivity(intent);
            }
        });
        return item;
    }


}
