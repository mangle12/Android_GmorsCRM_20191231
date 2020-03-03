package tw.com.masterhand.gmorscrm.activity.news;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import tw.com.masterhand.gmorscrm.BaseUserCheckActivity;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.TokenManager;
import tw.com.masterhand.gmorscrm.adapter.InviteTripAdapter;
import tw.com.masterhand.gmorscrm.model.MainTrip;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.local.ReadLog;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.view.Appbar;
import tw.com.masterhand.gmorscrm.view.VerticalSpaceItemDecoration;

public class InviteTripActivity extends BaseUserCheckActivity {
    @BindView(R.id.appbar)
    Appbar appbar;
    @BindView(R.id.container)
    RecyclerView recyclerView;

    InviteTripAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_trip);
        init();
    }

    @Override
    protected void onUserChecked() {
        appbar.invalidate();
        updateList();
    }

    private void init() {
        appbar.setTitle(getString(R.string.news_mission));
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(3));
        adapter = new InviteTripAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void updateList() {
        mDisposable.add(DatabaseHelper.getInstance(this).getInviteTrip(TokenManager.getInstance()
                .getUser().getId(), new Date()).toList().observeOn(AndroidSchedulers.mainThread()
        ).subscribe(new Consumer<List<MainTrip>>() {

            @Override
            public void accept(List<MainTrip> tripList) throws Exception {
                List<ReadLog> readLogs = new ArrayList<>();
                for (MainTrip mainTrip : tripList) {
                    ReadLog log = new ReadLog();
                    log.setParent_id(mainTrip.getTrip().getId());
                    readLogs.add(log);
                }
                DatabaseHelper.getInstance(InviteTripActivity.this).saveReadLog
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
                adapter.setTrip(tripList);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                ErrorHandler.getInstance().setException(InviteTripActivity.this, throwable);
            }
        }));
    }
}
