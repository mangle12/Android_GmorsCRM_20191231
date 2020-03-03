package tw.com.masterhand.gmorscrm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONObject;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.FlowableSubscriber;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tw.com.masterhand.gmorscrm.activity.approval.ApprovalMenuActivity;
import tw.com.masterhand.gmorscrm.activity.customer.ContactPersonCreateActivity;
import tw.com.masterhand.gmorscrm.activity.customer.CustomerActivity;
import tw.com.masterhand.gmorscrm.activity.customer.CustomerCreateActivity;
import tw.com.masterhand.gmorscrm.activity.news.NewsActivity;
import tw.com.masterhand.gmorscrm.activity.personal.PersonalActivity;
import tw.com.masterhand.gmorscrm.activity.reimbursement.ReimbursementRecordActivity;
import tw.com.masterhand.gmorscrm.activity.report.ReportActivity;
import tw.com.masterhand.gmorscrm.activity.sale.SaleActivity;
import tw.com.masterhand.gmorscrm.activity.setting.SettingActivity;
import tw.com.masterhand.gmorscrm.activity.statistic.StatisticActivity;
import tw.com.masterhand.gmorscrm.activity.task.CreateActivity;
import tw.com.masterhand.gmorscrm.activity.task.TaskCreateActivity;
import tw.com.masterhand.gmorscrm.api.ApiHelper;
import tw.com.masterhand.gmorscrm.enums.AcceptType;
import tw.com.masterhand.gmorscrm.enums.ApprovalRequire;
import tw.com.masterhand.gmorscrm.enums.MainMenu;
import tw.com.masterhand.gmorscrm.enums.ReportFilterType;
import tw.com.masterhand.gmorscrm.enums.SyncStatus;
import tw.com.masterhand.gmorscrm.enums.TripStatus;
import tw.com.masterhand.gmorscrm.model.MainTrip;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.local.SyncLog;
import tw.com.masterhand.gmorscrm.room.record.Participant;
import tw.com.masterhand.gmorscrm.tools.AlarmHelper;
import tw.com.masterhand.gmorscrm.tools.ErrorHandler;
import tw.com.masterhand.gmorscrm.tools.ImageHelper;
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.tools.TimeFormater;
import tw.com.masterhand.gmorscrm.tools.UnitChanger;
import tw.com.masterhand.gmorscrm.view.ExpandableCalendar;
import tw.com.masterhand.gmorscrm.view.ExpandableMain;
import tw.com.masterhand.gmorscrm.view.ItemMain;
import tw.com.masterhand.gmorscrm.view.ItemMainInvite;
import tw.com.masterhand.gmorscrm.view.ItemMainMenu;
import tw.com.masterhand.gmorscrm.view.ItemMainSmall;

public class MainActivity extends BaseUserCheckActivity implements ExpandableCalendar.OnExpandedListener, ExpandableCalendar.OnDateSelectedListener, SwipeRefreshLayout.OnRefreshListener,
        View.OnClickListener, ItemMainMenu.Callback, ItemMainInvite.AcceptListener {

    //測試GIT
    // 主選單
    boolean isMenuShow = false;
    MenuAdapter menuAdapter;
    @BindView(R.id.button_main_menu_toggle)
    ImageButton btnMenuToggle;// 主選單切換鈕
    @BindView(R.id.main_menu)
    RecyclerView mainMenu;// 主選單容器
    // appbar項目
    @BindView(R.id.imageView_logo)
    ImageView ivLogo;
    @BindView(R.id.button_search)
    ImageButton btnSearch;// 搜尋鈕
    @BindView(R.id.button_close)
    ImageButton btnClose;// 日曆關閉or主選單設定
    @BindView(R.id.relativeLayout_news)
    RelativeLayout btnNews;// 訊息鈕
    @BindView(R.id.textView_count)
    TextView tvNewsCount;// 新訊息提示
    // 日曆及列表
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.expandableCalendar)
    ExpandableCalendar expandableCalendar;// 日曆
    @BindView(R.id.linearLayout_container)
    LinearLayout container;// 行程列表
    @BindView(R.id.relativeLayout_top)
    RelativeLayout topbar;

    Subscription tripList;
    ExpandableMain expandableInvite, expandableMain, expandableTask;

    String viewer;
    int newsCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        connectLayout();
        createMainMenu();

        if (!TokenManager.getInstance().checkToken())
            reLogin();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelId  = "default_notification_channel_id";
            String channelName = "default_notification_channel_name";
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW));
        }

        //訂閱主題
        //FirebaseMessaging.getInstance().subscribeToTopic("test");
    }

//   2020-02-19  取得手機Token
//    private void getToken() {
//        new Handler().postDelayed(new Runnable() {
//            String deviceToken = null;
//
//            @Override
//            public void run() {
//                FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
//                    @Override
//                    public void onSuccess(InstanceIdResult instanceIdResult) {
//                        deviceToken = instanceIdResult.getToken();
//                        Log.i("MainActivityToken","token "+deviceToken);
//                    }
//                });
//
//                if (null == deviceToken) {
//                    getToken();
//                } else {
//                    //Next scenario
//                }
//            }
//        }, 1000);
//    }

    @Override
    protected void onUserChecked() {
        viewer = TokenManager.getInstance().getUser().getId();
        int bgColor = preferenceHelper.getThemeColor();
        topbar.setBackgroundColor(bgColor);
        expandableCalendar.displayCalendar(expandableCalendar.getCurrentDate());
        expandableCalendar.setCurrentDate(expandableCalendar.getCurrentDate());
//        getData(expandableCalendar.getCurrentDate());
        updateMenuShow();
        newsCount = 0;
        checkNews();
        checkInvitedCount();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (tripList != null)
            tripList.cancel();
    }

    private void connectLayout() {
        // 日曆
        expandableCalendar.setOnExpandedListener(this);
        expandableCalendar.setOnDateSelectedListener(this);
        swipeRefreshLayout.setOnRefreshListener(this);

        ivLogo.setImageURI(new ImageHelper(this).getHeader());
    }

    /**
     * 檢查新消息數量
     */
    private void checkNews() {
        ApiHelper.getInstance().getNewsApi().getNewsList(TokenManager.getInstance().getToken()).enqueue(new Callback<JSONObject>() {
                    @Override
                    public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                        try {
//                            Logger.e(TAG, "response code:" + response.code());
                            switch (response.code()) {
                                case 200:
//                                    Logger.e(TAG, "response:" + response.body().toString());
                                    JSONObject result = response.body();
                                    int success = result.getInt("success");
                                    if (success == 1) {
                                        JSONArray notify = result.getJSONObject("list").getJSONArray("notify");
                                        JSONArray announce = result.getJSONObject("list").getJSONArray("announce");
                                        JSONArray resource = result.getJSONObject("list").getJSONArray("resource");
                                        List<String> idList = new ArrayList<>();
                                        for (int i = 0; i < notify.length(); i++) {
                                            idList.add(notify.getString(i));
                                        }
                                        for (int i = 0; i < announce.length(); i++) {
                                            idList.add(announce.getString(i));
                                        }
                                        for (int i = 0; i < resource.length(); i++) {
                                            idList.add(resource.getString(i));
                                        }
                                        if (idList.size() > 0)
                                            DatabaseHelper.getInstance(MainActivity.this).getUnreadCount(idList)
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .subscribe(new Consumer<Integer>() {

                                                        @Override
                                                        public void accept(Integer integer) throws
                                                                Exception {
                                                            newsCount += integer;
                                                            updateUnreadCount();
                                                        }
                                                    }, new Consumer<Throwable>() {
                                                        @Override
                                                        public void accept(Throwable throwable)
                                                                throws
                                                                Exception {
                                                            ErrorHandler.getInstance().setException(MainActivity.this, throwable);
                                                        }
                                                    });
                                    }
                                    break;
                                case ApiHelper.ERROR_TOKEN_EXPIRED:
                                    showLoginDialog();
                                    break;
                                default:
                                    Logger.e(TAG, "checkNews failed");
                            }
                        } catch (Exception e) {
                            ErrorHandler.getInstance().setException(MainActivity.this, e);
                        }
                    }

                    @Override
                    public void onFailure(Call<JSONObject> call, Throwable t) {
                        ErrorHandler.getInstance().setException(MainActivity.this, t);
                    }
                });
    }

    /**
     * 檢查被邀請行程數量
     */
    private void checkInvitedCount() {
        mDisposable.add(DatabaseHelper.getInstance(this).getInviteTrip(TokenManager.getInstance().getUser().getId(),
                new Date()).map(new Function<MainTrip, String>() {
            @Override
            public String apply(@NonNull MainTrip mainTrip) throws Exception {
                return mainTrip.getTrip().getId();
            }
        }).toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<String>>() {

            @Override
            public void accept(List<String> tripIdList) throws Exception {
                mDisposable.add(DatabaseHelper.getInstance(MainActivity.this).getUnreadCount(tripIdList)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Integer>() {

                            @Override
                            public void accept(Integer integer) throws
                                    Exception {
//                                Logger.e(TAG, "checkInvitedCount:" + integer);
                                newsCount += integer;
                                updateUnreadCount();
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws
                                    Exception {
                                ErrorHandler.getInstance().setException(MainActivity.this, throwable);
                            }
                        }));
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                ErrorHandler.getInstance().setException(MainActivity.this, throwable);
            }
        }));
    }

    /**
     * 刷新未讀數量
     */
    private void updateUnreadCount() {
        if (newsCount > 0) {
            tvNewsCount.setVisibility(View.VISIBLE);
            if (newsCount > 99) {
                tvNewsCount.setText("N");
            } else {
                tvNewsCount.setText(String.valueOf(newsCount));
            }
        } else {
            tvNewsCount.setVisibility(View.GONE);
        }
    }

    private void createMainMenu() {
        mainMenu.setHasFixedSize(true);
        mainMenu.setLayoutManager(new GridLayoutManager(this, 3));
        menuAdapter = new MenuAdapter(preferenceHelper.getMenuSetting());
        mainMenu.setAdapter(menuAdapter);
    }

    /**
     * 主選單Adapter
     */
    class MenuAdapter extends RecyclerView.Adapter<MainActivity.MenuAdapter.ViewHolder> {
        private ArrayList<MainMenu> mDataset;

        class ViewHolder extends RecyclerView.ViewHolder {
            ItemMainMenu item;

            public ViewHolder(ItemMainMenu item) {
                super(item);
                this.item = item;
            }
        }

        public MenuAdapter(ArrayList<MainMenu> myDataset) {
            mDataset = myDataset;
        }

        void setDataSet(ArrayList<MainMenu> dataset) {
            mDataset = dataset;
        }

        @Override
        public MainActivity.MenuAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ItemMainMenu item = new ItemMainMenu(parent.getContext());
            return new MainActivity.MenuAdapter.ViewHolder(item);
        }

        @Override
        public void onBindViewHolder(MainActivity.MenuAdapter.ViewHolder holder, int position) {
            holder.item.setMainMenu(mDataset.get(position));
            holder.item.setCallback(MainActivity.this);
        }

        @Override
        public int getItemCount() {
            return mDataset.size();
        }
    }


    /**
     * 更新行程列表
     */
    public void updateList(final Calendar date) {
        Logger.i(TAG, "getData");
        if (!swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(true);
        }
        container.removeAllViews();
        expandableMain = new ExpandableMain(MainActivity.this);
        expandableTask = new ExpandableMain(MainActivity.this);
        expandableInvite = new ExpandableMain(MainActivity.this);
        expandableInvite.setTitleBackground(ContextCompat.getColor(MainActivity.this, R.color.orange));

        DatabaseHelper.getInstance(this).getMainTrip(date.getTime(), TokenManager.getInstance().getUser().getId(), ReportFilterType.PERSON)
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterNext(new Consumer<MainTrip>() {
            @Override
            public void accept(MainTrip mainTrip) throws Exception {
                if (expandableCalendar.isExpanded()) {
                    int padding = UnitChanger.dpToPx(8);
                    container.setPadding(padding, padding, padding, padding);
                } else {
                    container.setPadding(0, 0, 0, 0);
                }
            }
        }).subscribe(new FlowableSubscriber<MainTrip>() {

            @Override
            public void onSubscribe(Subscription s) {
                if (tripList != null)
                    tripList.cancel();
                tripList = s;
                s.request(Integer.MAX_VALUE);
            }

            @Override
            public void onNext(MainTrip mainTrip) {
//                Logger.e(TAG, "MainTrip:" + gson.toJson(mainTrip));
                List<Participant> participants = mainTrip.getParticipants();
                Participant participant = null;// user
                for (Participant item : participants) {
                    if (item.getUser_id().equals(viewer)) {
                        participant = item;
                        break;
                    }
                }
                if (expandableCalendar.isExpanded()) {
                    if (participant == null || participant.getAccept() != AcceptType.NO) {
                        // 不參加則不顯示
                        ItemMainSmall smallItem = new ItemMainSmall(MainActivity.this);
                        smallItem.setDate(date.getTime());
                        smallItem.setTrip(mainTrip.getTrip());
                        container.addView(smallItem);
                    }
                } else {
                    if (mainTrip.getTrip().getStatus() == TripStatus.CANCEL) {
                        Logger.e(TAG, mainTrip.getTrip().getId() + " is cancel");
                        ItemMain itemMain = new ItemMain(MainActivity.this);
                        itemMain.setDate(date.getTime());
                        itemMain.setTrip(mainTrip);
                        expandableMain.addListView(itemMain);
                    } else if (participant != null && participant.getAccept() == AcceptType.NONE) {
                        ItemMainInvite invite = new ItemMainInvite(MainActivity.this);
                        invite.setDate(date.getTime());
                        invite.setTrip(mainTrip, participant);
                        invite.setAcceptListener(MainActivity.this);
                        expandableInvite.addListView(invite);
                    } else if (participant == null || participant.getAccept() != AcceptType.NO) {
                        if (mainTrip.getTrip().getApprovalRequired() == ApprovalRequire.UNKNOWN && mainTrip.trip.getUpdated_at() != null) {
                            // 發現未同步資料，重設最後同步時間
                            Date lastDate = new Date(mainTrip.trip.getUpdated_at().getTime() - (30 * 60 * 1000));
                            SyncLog log = new SyncLog();
                            log.setId("00");
                            log.setUser_id(TokenManager.getInstance().getUser().getId());
                            log.setSync_status(SyncStatus.SUCCESS);
                            log.setIsFile(false);
                            log.setCreated_at(lastDate);

                            mDisposable.add(DatabaseHelper.getInstance(MainActivity.this).resetSyncLog(log).subscribe(new Consumer<String>() {
                                @Override
                                public void accept(String s) throws Exception {
                                    Logger.i(TAG, "saveSyncLog:" + s);
                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) throws Exception {
                                    Logger.i(TAG, "saveSyncLog error:" + throwable.getMessage());
                                }
                            }));

                            preferenceHelper.resetLastSync(mainTrip.trip.getUpdated_at(), TokenManager.getInstance().getUser().getId());
                        }

                        //卡片
                        ItemMain itemMain = new ItemMain(MainActivity.this);
                        itemMain.setDate(date.getTime());
                        itemMain.setTrip(mainTrip);
                        switch (mainTrip.getTrip().getType()) {
                            case TASK:
                                expandableTask.addListView(itemMain);
                                break;
                            default:
                                expandableMain.addListView(itemMain);
                        }
                    } else {
                        Logger.e(TAG, "participant accept:" + participant.getAccept().name());
                    }
                }

            }

            @Override
            public void onError(Throwable t) {
                Logger.e(TAG, "getData error:" + t.getMessage());
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onComplete() {
                if (expandableInvite != null && expandableInvite.getContainer().getChildCount() > 0) {
                    expandableInvite.setTitle(getString(R.string
                            .scheduled_invite) + "(" + expandableInvite.getContainer().getChildCount() + ")");
                    expandableInvite.setIcon(R.mipmap.common_pre_work);
                    expandableInvite.toggle();
                    container.addView(expandableInvite);
                }
                if (expandableMain != null && expandableMain.getContainer().getChildCount() > 0) {
                    expandableMain.setTitle(getString(R.string.scheduled_trip) + "(" + expandableMain.getContainer().getChildCount() + ")");
                    expandableMain.setIcon(R.mipmap.common_pre_work);
                    expandableMain.toggle();
                    container.addView(expandableMain);
                }
                if (expandableTask != null && expandableTask.getContainer().getChildCount() > 0) {
                    expandableTask.setTitle(getString(R.string.scheduled_task) + "(" + expandableTask.getContainer().getChildCount() + ")");
                    expandableTask.setIcon(R.mipmap.common_pre_mission);
                    expandableTask.toggle();
                    container.addView(expandableTask);
                }
                if (container.getChildCount() == 0) {
                    Logger.e(TAG, "no data");
                    container.addView(getEmptyImageView(null));
                }
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    /**
     * 更新主選單顯示
     */
    public void updateMenuShow() {
        if (isMenuShow) {
            btnSearch.setVisibility(View.GONE);
            btnClose.setImageResource(R.mipmap.common_add_menu);
            btnClose.setVisibility(View.VISIBLE);
            btnMenuToggle.animate().rotation(405);
            btnMenuToggle.getDrawable().setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
            ((ImageView) findViewById(R.id.bg_main_menu_toggle)).setImageResource(R.drawable.oval_black);
            mainMenu.setVisibility(View.VISIBLE);
        } else {
            if (expandableCalendar.isExpanded()) {
                btnSearch.setVisibility(View.GONE);
                btnClose.setImageResource(R.mipmap.common_close);
                btnClose.setVisibility(View.VISIBLE);
            } else {
                btnSearch.setVisibility(View.VISIBLE);
                btnClose.setVisibility(View.GONE);
            }
            btnMenuToggle.animate().rotation(0);
            btnMenuToggle.getDrawable().setColorFilter(ContextCompat.getColor(this, R.color.black), PorterDuff.Mode.MULTIPLY);
            ((ImageView) findViewById(R.id.bg_main_menu_toggle)).setImageResource(R.drawable.oval_color_primary);
            mainMenu.setVisibility(View.GONE);
        }
    }

    /**
     * 顯示主選單與否
     */
    protected void toggleMenu() {
        isMenuShow = !isMenuShow;
        updateMenuShow();
    }

    /**
     * 顯示主選單按鈕與否
     */
    public void showMenuButton(boolean isShow) {
        if (isShow) {
            findViewById(R.id.bg_main_menu_toggle).setVisibility(View.VISIBLE);
            btnMenuToggle.setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.bg_main_menu_toggle).setVisibility(View.GONE);
            btnMenuToggle.setVisibility(View.GONE);
        }
    }

    /*----------------------------------callback------------------------------------*/

    /**
     * 日曆展開
     */
    @Override
    public void onExpanded() {
        Logger.i(TAG, "onExpanded");
        btnClose.setImageResource(R.mipmap.common_close);
        btnClose.setVisibility(View.VISIBLE);
        btnSearch.setVisibility(View.GONE);
        showMenuButton(false);
    }

    /**
     * 日曆關閉
     */
    @Override
    public void onCollapse() {
        Logger.i(TAG, "onCollapse");
        btnClose.setVisibility(View.GONE);
        btnSearch.setVisibility(View.VISIBLE);
        showMenuButton(true);
    }

    /**
     * 日曆日期選擇
     */
    @Override
    public void onDateSelected(Calendar selectDate) {
        Logger.i(TAG, "onDateSelected:" + TimeFormater.getInstance().toDatabaseFormat(selectDate.getTime()));
        updateList(selectDate);
    }

    /**
     * 下拉刷新
     */
    @Override
    public void onRefresh() {
        Logger.i(TAG, "onRefresh");
        updateList(expandableCalendar.getCurrentDate());
    }

    @OnClick({R.id.button_main_menu_toggle, R.id.button_close, R.id.button_search, R.id.relativeLayout_news})
    public void onClick(View view) {
        switch (view.getId()) {
            // 主選單顯示鈕
            case R.id.button_main_menu_toggle:
                toggleMenu();
                break;
            // 關閉日曆or主選單快捷設定
            case R.id.button_close:
                if (isMenuShow) {
                    Intent quickIntent = new Intent(MainActivity.this, MenuSettingActivity.class);
                    startActivityForResult(quickIntent, MyApplication.REQUEST_MENU_SETTING);
                } else {
                    expandableCalendar.calendarCollapse();
                }
                break;
            // 搜尋
            case R.id.button_search:
                Intent intent = new Intent(MainActivity.this, SearchResultActivity.class);
                startActivity(intent);
                break;
            // 訊息
            case R.id.relativeLayout_news:
                Intent msgIntent = new Intent(MainActivity.this, NewsActivity.class);
                startActivity(msgIntent);
                break;
        }
    }

    //主選單
    @Override
    public void onMenuSelected(MainMenu menu) {
        toggleMenu();
        Intent intent = null;
        switch (menu) {
            case NEW_TASK://新建工作
                intent = new Intent(this, CreateActivity.class);
                intent.putExtra(MyApplication.INTENT_KEY_DATE, TimeFormater.getInstance().toDatabaseFormat(expandableCalendar.getCurrentDate().getTime()));
                break;
            case REPORT://工作報告
                intent = new Intent(this, ReportActivity.class);
                break;
            case CUSTOMER://客戶
                intent = new Intent(this, CustomerActivity.class);
                break;
            case APPROVAL_RECORD://審批申請明細
                intent = new Intent(this, ApprovalMenuActivity.class);
                intent.putExtra(MyApplication.INTENT_KEY_MODE, true);
                break;
            case APPROVAL_SIGN://審批執行
                intent = new Intent(this, ApprovalMenuActivity.class);
                intent.putExtra(MyApplication.INTENT_KEY_MODE, false);
                break;
//            case SAMPLE:
//                intent = new Intent(this, SampleListActivity.class);
//                break;
            case EXP_RECORD://報銷紀錄
                intent = new Intent(this, ReimbursementRecordActivity.class);
                break;
            case SALES://銷售機會
                intent = new Intent(this, SaleActivity.class);
                break;
            case STATISTIC://統計報表
                intent = new Intent(this, StatisticActivity.class);
                break;
            case PERSONAL://個人
                intent = new Intent(this, PersonalActivity.class);
                break;
            case SETTING://設定
                intent = new Intent(this, SettingActivity.class);
                break;
            case NEW_CUSTOMER://新建客戶
                intent = new Intent(this, CustomerCreateActivity.class);
                break;
            case NEW_CONTACT_PERSON://新建客戶聯絡人
                intent = new Intent(this, ContactPersonCreateActivity.class);
                break;
            case NEW_MISSION://新建任務
                intent = new Intent(this, TaskCreateActivity.class);
                break;
        }
        startActivity(intent);
    }

    @Override
    public void onAcceptItemSelected(AcceptType type) {
        updateList(expandableCalendar.getCurrentDate());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == MyApplication.REQUEST_MENU_SETTING) {
                menuAdapter.setDataSet(preferenceHelper.getMenuSetting());
                menuAdapter.notifyDataSetChanged();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //2020-02-24 測試未讀訊息顯示數量
//    public static final String NOTIFICATION_CHANNEL_ID = "10001" ;
//    private final static String default_notification_channel_id = "default" ;
//
//    public void createNotification () {
//        int count =10 ;
//        Intent notificationIntent = new Intent(getApplicationContext() , MainActivity. class ) ;
//        notificationIntent.putExtra( "fromNotification" , true ) ;
//        notificationIntent.setFlags(Intent. FLAG_ACTIVITY_CLEAR_TOP | Intent. FLAG_ACTIVITY_SINGLE_TOP ) ;
//        PendingIntent pendingIntent = PendingIntent. getActivity ( this, 0 , notificationIntent , 0 ) ;
//        NotificationManager mNotificationManager = (NotificationManager) getSystemService( NOTIFICATION_SERVICE ) ;
//
//        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext() , default_notification_channel_id ) ;
//        mBuilder.setContentTitle( "My Notification" ) ;
//        mBuilder.setContentIntent(pendingIntent) ;
//        mBuilder.setContentText( "Notification Listener Service Example" ) ;
//        mBuilder.setSmallIcon(R.mipmap.ic_launcher ) ;
//        mBuilder.setAutoCancel( true ) ;
////        mBuilder.setBadgeIconType( BADGE_ICON_SMALL ) ;
//        mBuilder.setNumber( count ) ;
//
//        if (android.os.Build.VERSION. SDK_INT >= android.os.Build.VERSION_CODES. O ) {
//            int importance = NotificationManager. IMPORTANCE_HIGH ;
//            NotificationChannel notificationChannel = new NotificationChannel( NOTIFICATION_CHANNEL_ID , "NOTIFICATION_CHANNEL_NAME" , importance) ;
//            mBuilder.setChannelId( NOTIFICATION_CHANNEL_ID ) ;
//            notificationChannel.setShowBadge(false);
//            assert mNotificationManager != null;
//            mNotificationManager.createNotificationChannel(notificationChannel) ;
//        }
//        assert mNotificationManager != null;
//        mNotificationManager.notify(( int ) System. currentTimeMillis () , mBuilder.build()) ;
//    }
}
