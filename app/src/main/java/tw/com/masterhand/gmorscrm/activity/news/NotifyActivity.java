package tw.com.masterhand.gmorscrm.activity.news;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;

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
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.TokenManager;
import tw.com.masterhand.gmorscrm.api.ApiHelper;
import tw.com.masterhand.gmorscrm.model.Notify;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.local.ReadLog;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.tools.TimeFormater;
import tw.com.masterhand.gmorscrm.view.Appbar;

public class NotifyActivity extends BaseWebCheckActivity {
    @BindView(R.id.appbar)
    Appbar appbar;
    @BindView(R.id.container)
    LinearLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify);
        init();

    }

    @Override
    protected void onUserChecked() {
        appbar.invalidate();
        updateList();
    }

    private void init() {
        appbar.setTitle(getString(R.string.news_notify));
    }

    private void updateList() {
        startProgressDialog();
        container.removeAllViews();
        ApiHelper.getInstance().getNewsApi().getNotifyList(TokenManager.getInstance().getToken())
                .enqueue(new Callback<JSONObject>() {

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
                                        List<Notify> list = gson.fromJson(result.getString
                                                ("list"), new
                                                TypeToken<List<Notify>>() {
                                                }.getType());
                                        Collections.sort(list, new Comparator<Notify>() {
                                            @Override
                                            public int compare(Notify o1, Notify o2) {
                                                return o2.getUpdated_at().compareTo
                                                        (o1.getUpdated_at());
                                            }
                                        });
                                        List<ReadLog> readLogs = new ArrayList<>();
                                        for (Notify notify : list) {
                                            container.addView(generateItem(notify));
                                            ReadLog log = new ReadLog();
                                            log.setParent_id(notify.getId());
                                            readLogs.add(log);
                                        }
                                        DatabaseHelper.getInstance(NotifyActivity.this).saveReadLog
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
                                                Logger.e(TAG, "save readLog error:" + e
                                                        .getMessage());
                                            }
                                        });
                                    }
                                    break;
                                case ApiHelper.ERROR_TOKEN_EXPIRED:
                                    showLoginDialog();
                                    break;
                                default:
                                    Logger.e(TAG, "response code:" + response.code());
                            }
                        } catch (Exception e) {
                            ErrorHandler.getInstance().setException(NotifyActivity.this, e);
                        }
                        stopProgressDialog();
                    }

                    @Override
                    public void onFailure(Call<JSONObject> call, Throwable t) {
                        ErrorHandler.getInstance().setException(NotifyActivity.this, t);
                        stopProgressDialog();
                    }
                });
    }

    private View generateItem(final Notify notify) {
        View item = getLayoutInflater().inflate(R.layout.item_notify, container, false);
        TextView tvFrom = item.findViewById( R.id.textView_from);
        TextView tvTitle = item.findViewById( R.id.textView_title);
        TextView tvDate = item.findViewById(R.id.textView_date);
        tvTitle.setText(notify.getTitle());
        tvFrom.setText(notify.getFrom());
        tvDate.setText(TimeFormater.getInstance().toDateFormat(notify.getUpdated_at()));
//        item.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(NotifyActivity.this, NewsDetailActivity.class);
//                intent.putExtra(MyApplication.INTENT_KEY_TYPE, NewsType.NOTIFY.getValue());
//                intent.putExtra(MyApplication.INTENT_KEY_ID, notify.getId());
//                startActivity(intent);
//            }
//        });
        return item;
    }

}
