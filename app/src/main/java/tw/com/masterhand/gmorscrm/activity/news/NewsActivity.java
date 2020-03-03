package tw.com.masterhand.gmorscrm.activity.news;

import android.content.Intent;
import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tw.com.masterhand.gmorscrm.BaseWebCheckActivity;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.TokenManager;
import tw.com.masterhand.gmorscrm.api.ApiHelper;
import tw.com.masterhand.gmorscrm.model.MainTrip;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.view.Appbar;
import tw.com.masterhand.gmorscrm.view.ItemNews;

public class NewsActivity extends BaseWebCheckActivity {
    @BindView(R.id.appbar)
    Appbar appbar;
    @BindView(R.id.itemNewsNotify)
    ItemNews itemNotify;
    @BindView(R.id.itemNewsAnnounce)
    ItemNews itemAnnounce;
    @BindView(R.id.itemNewsMission)
    ItemNews itemMission;
    @BindView(R.id.itemNewsResource)
    ItemNews itemResource;
    @BindView(R.id.itemNotSubmitted)
    ItemNews itemNotSubmitted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        connectLayout();
    }

    @Override
    protected void onUserChecked() {
        appbar.invalidate();
        updateList();
    }

    private void connectLayout() {
        appbar.setTitle(getString(R.string.title_activity_news));
    }

    /**
     * 刷新消息列表
     */
    private void updateList() {
        getInviteTrip();
        getNotSubmittedTrip();
        getNews();
    }

    /**
     * 取得消息ID列表
     */
    private void getNews() {
        ApiHelper.getInstance().getNewsApi().getNewsList(TokenManager.getInstance().getToken()).enqueue(new Callback<JSONObject>() {
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
                                        JSONArray notify = result.getJSONObject("list").getJSONArray("notify");
                                        JSONArray announce = result.getJSONObject("list").getJSONArray("announce");
//                                        JSONArray resource = result.getJSONObject("list")
//                                                .getJSONArray
//                                                        ("resource");
                                        itemNotify.setCount(notify.length());
                                        checkUnread(jsonArrayToList(notify), itemNotify);
                                        itemAnnounce.setCount(announce.length());
                                        checkUnread(jsonArrayToList(announce), itemAnnounce);
//                                        itemResource.setCount(resource.length());
//                                        checkUnread(jsonArrayToList(resource), itemResource);
                                    }
                                    break;
                                case ApiHelper.ERROR_TOKEN_EXPIRED:
                                    showLoginDialog();
                                    break;
                                default:
                            }
                        } catch (Exception e) {
                            ErrorHandler.getInstance().setException(NewsActivity.this, e);
                        }
                    }

                    @Override
                    public void onFailure(Call<JSONObject> call, Throwable t) {
                        ErrorHandler.getInstance().setException(NewsActivity.this, t);
                    }
                });
    }

    private List<String> jsonArrayToList(JSONArray array) {
        List<String> result = new ArrayList<>();
        try {
            for (int i = 0; i < array.length(); i++) {
                result.add(array.getString(i));
            }
        } catch (Exception e) {
            ErrorHandler.getInstance().setException(this, e);
        }
        return result;
    }

    /**
     * 取得受邀行程
     */
    private void getInviteTrip() {
        mDisposable.add(DatabaseHelper.getInstance(this).getInviteTrip(TokenManager.getInstance().getUser().getId(), new Date()).map(new Function<MainTrip, String>() {
            @Override
            public String apply(@NonNull MainTrip mainTrip) throws Exception {
                return mainTrip.getTrip().getId();
            }}).toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<String>>() {

            @Override
            public void accept(List<String> tripIdList) throws Exception {
                itemMission.setCount(tripIdList.size());
                checkUnread(tripIdList, itemMission);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                ErrorHandler.getInstance().setException(NewsActivity.this, throwable);
                itemMission.setCount(0);
            }
        }));
    }

    /**
     * 取得未提交行程
     */
    private void getNotSubmittedTrip() {
        mDisposable.add(DatabaseHelper.getInstance(this).getNotSunmittedTrip(TokenManager
                .getInstance().getUser().getId()).count()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long count) throws Exception {
                itemNotSubmitted.setCount(count.intValue());
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                ErrorHandler.getInstance().setException(NewsActivity.this, throwable);
            }
        }));
    }

    private void checkUnread(List<String> idList, final ItemNews item) {
        mDisposable.add(DatabaseHelper.getInstance(this).getUnreadCount(idList)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {

            @Override
            public void accept(Integer count) throws Exception {
                if (count > 0)
                    item.setSubtitle(String.valueOf(count) + getString(R.string.unread_msg));
                else
                    item.setSubtitle("");
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                ErrorHandler.getInstance().setException(NewsActivity.this, throwable);
                item.setSubtitle("");
            }
        }));
    }

    @OnClick(R.id.itemNewsMission)
    void showInviteTrip() {
        Intent intent = new Intent(this, InviteTripActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.itemNewsNotify)
    void showNotify() {
        Intent intent = new Intent(this, NotifyActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.itemNewsAnnounce)
    void showAnnounce() {
        Intent intent = new Intent(this, AnnounceActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.itemNewsResource)
    void showResource() {
        Intent intent = new Intent(this, SaleResourceDeptActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.itemNotSubmitted)
    void showNotSubmitted() {
        Intent intent = new Intent(this, NotSubmittedActivity.class);
        startActivity(intent);
    }
}
