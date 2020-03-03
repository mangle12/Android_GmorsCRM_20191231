package tw.com.masterhand.gmorscrm.api;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.EOFException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tw.com.masterhand.gmorscrm.TokenManager;
import tw.com.masterhand.gmorscrm.enums.SyncStatus;
import tw.com.masterhand.gmorscrm.model.SyncRecord;
import tw.com.masterhand.gmorscrm.model.SyncSetting;
import tw.com.masterhand.gmorscrm.model.UploadRecord;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.local.SyncLog;
import tw.com.masterhand.gmorscrm.tools.GsonGMTDateAdapter;
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.tools.PreferenceHelper;
import tw.com.masterhand.gmorscrm.tools.TimeFormater;

public class SyncManager {
    private String TAG;
    private final int syncInterval = 120;// 同步頻率(秒)
    private final String platform = "android";

    private final CompositeDisposable mDisposable = new CompositeDisposable();

    private static SyncManager singleton;
    private OnSyncListener listener;
    private PreferenceHelper preferenceHelper;
    private Gson gson;
    private Context mContext;
    private boolean shouldSync = false;
    private boolean isRecordSyncing = false;
    private final int MAX_CHECK_TIMES = 10;
    private int checkTimes = 0;

    public interface OnSyncListener {
        void onSyncing();

        void onSyncFinished();

        void onSyncFailed(String msg);

        void onSyncFileFailed(String msg);
    }

    private SyncManager(Context context) {
        mContext = context.getApplicationContext();
        TAG = getClass().getSimpleName();
        gson = new GsonBuilder().registerTypeAdapter(Date.class, new GsonGMTDateAdapter()).create();
        preferenceHelper = new PreferenceHelper(mContext);
    }

    public static SyncManager getInstance(Context context) {
        if (singleton == null)
            singleton = new SyncManager(context);
        return singleton;
    }

    public boolean startSync() {
        Logger.e(TAG, "startSync");
        shouldSync = true;
        startLoop();
        startFileLoop();
        return true;
    }

    public SyncManager stopSync() {
        Logger.e(TAG, "stopSync");
        shouldSync = false;
        isRecordSyncing = false;
        mDisposable.clear();
        return this;
    }

    /**
     * 開始資料同步循環
     */
    private void startLoop() {
        Logger.e(TAG, "startLoop");
        mDisposable.add(Observable.interval(0, syncInterval, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .map(new Function<Long, Boolean>() {
                    @Override
                    public Boolean apply(@NonNull Long aLong) throws Exception {
                        if (!TokenManager.getInstance().checkToken())
                            return false;
                        if (isRecordSyncing)
                            return false;
                        SyncLog log = DatabaseHelper.getInstance(mContext).getDatabase(mContext).localDao().getLastUncompletedSyncLog(0, TokenManager.getInstance().getUser().getId());
                        Logger.e(TAG, Calendar.getInstance().getTime()+"******************check last uncompleted sync log:" + gson.toJson(log) + "******************");
                        if (log == null) {
                            return true;
                        } else {
                            boolean isCompleted = false;
                            if (log.getSync_status() == SyncStatus.NONE) {
                                Call<JSONObject> call = ApiHelper.getInstance().getSyncApi().getSyncStatus(TokenManager.getInstance().getToken(), log.getId());
                                Response<JSONObject> response = call.execute();
                                switch (response.code()) {
                                    case 200:
                                        JSONObject result = response.body();
                                        Logger.e(TAG, "check sync status result:" + result.toString());
                                        try {
                                            int status = result.getInt("status");
                                            SyncStatus syncStatus = SyncStatus.getStatusByCode(status);
                                            switch (syncStatus) {
                                                case SUCCESS:
                                                case FAIL:
                                                    checkTimes = 0;
                                                    isCompleted = true;
                                                    break;
                                                case NONE:
                                                    checkTimes++;
                                                    if (checkTimes > MAX_CHECK_TIMES) {
                                                        syncStatus = SyncStatus.FAIL;
                                                        isCompleted = true;
                                                        checkTimes = 0;
                                                    }
                                                    if (listener != null)
                                                        listener.onSyncing();
                                            }
                                            log.setSync_status(syncStatus);
                                            log.setUpdated_at(new Date());
                                            if (DatabaseHelper.getInstance(mContext).getDatabase(mContext).localDao().saveSyncLog(log) == -1) {
                                                isCompleted = false;
                                                if (listener != null)
                                                    listener.onSyncFailed("can't save sync status");
                                            }
                                        } catch (JSONException e) {
                                            if (listener != null)
                                                listener.onSyncFailed("can't get sync status");
                                        }
                                        break;
                                    case ApiHelper.ERROR_TOKEN_EXPIRED:
                                        stopSync();
                                    default:
                                        if (response.body() != null) {
                                            Logger.e(TAG, "body:" + response.body().toString());
                                        }
                                        if (response.errorBody() != null) {
                                            try {
                                                Logger.e(TAG, "errorBody:" + response.errorBody().string());
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        if (listener != null)
                                            listener.onSyncFailed(response.code() + ":" + response.message());
                                        break;
                                }
                            } else {
                                isCompleted = true;
                            }
                            return isCompleted;
                        }
                    }
                })
                .retry(Long.MAX_VALUE)
                .subscribe
                        (new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean shouldSync) throws Exception {
                                if (shouldSync) {
                                    uploadRecord();
                                }
                            }
                        }));
    }

    /**
     * 開始檔案同步循環
     */
    private void startFileLoop() {
        Logger.e(TAG, "startFileLoop");
        mDisposable.add(Observable.interval(1000, syncInterval, TimeUnit.SECONDS)
                .delay(3, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .map(new Function<Long, Boolean>() {
                    @Override
                    public Boolean apply(@NonNull Long aLong) throws Exception {
                        if (!TokenManager.getInstance().checkToken())
                            return false;
                        SyncLog log = DatabaseHelper.getInstance(mContext).getDatabase(mContext).localDao()
                                .getLastUncompletedSyncLog(1, TokenManager.getInstance().getUser().getId());
                        Logger.e(TAG, "******************check last uncompleted sync file log:" + gson.toJson(log) + "******************");
                        if (log == null) {
                            return true;
                        } else {
                            boolean isCompleted = false;
                            if (log.getSync_status() == SyncStatus.NONE) {
                                Call<JSONObject> call = ApiHelper.getInstance().getSyncApi().getSyncStatus(TokenManager.getInstance().getToken(), log.getId());
                                Response<JSONObject> response = call.execute();
                                switch (response.code()) {
                                    case 200:
                                        JSONObject result = response.body();
                                        Logger.e(TAG, "check sync status result:" + result.toString());
                                        try {
                                            int status = result.getInt("status");
                                            SyncStatus syncStatus = SyncStatus.getStatusByCode(status);
                                            log.setSync_status(syncStatus);
                                            log.setUpdated_at(new Date());
                                            DatabaseHelper.getInstance(mContext).getDatabase(mContext).localDao().saveSyncLog(log);
                                            switch (syncStatus) {
                                                case SUCCESS:
                                                case FAIL:
                                                    isCompleted = true;
                                            }
                                        } catch (JSONException e) {
                                            if (listener != null)
                                                listener.onSyncFailed("can't get sync status");
                                        }
                                        break;
                                    case ApiHelper.ERROR_TOKEN_EXPIRED:
                                        stopSync();
                                    default:
                                        if (response.body() != null) {
                                            Logger.e(TAG, "body:" + response.body().toString());
                                        }
                                        if (response.errorBody() != null) {
                                            try {
                                                Logger.e(TAG, "errorBody:" + response.errorBody().string());
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        if (listener != null)
                                            listener.onSyncFailed(response.code() + ":" + response.message());
                                        break;
                                }
                            } else {
                                isCompleted = true;
                            }
                            return isCompleted;
                        }
                    }
                })
                .retry(Long.MAX_VALUE)
                .subscribe
                        (new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean shouldSync) throws Exception {
                                if (shouldSync) {
                                    uploadFile();
                                }
                            }
                        }));
    }

    public SyncManager setListener(OnSyncListener listener) {
        this.listener = listener;
        return this;
    }

    public SyncManager removeListener() {
        listener = null;
        return this;
    }

    /**
     * 取得設定
     */
    public void getSetting() {
        if (TokenManager.getInstance().getUser() == null) {
            stopSync();
            if (listener != null)
                listener.onSyncFailed("TokenManager:user is " + "null");
            return;
        }
        Date lastUpdate = preferenceHelper.getLastSync(TokenManager.getInstance().getUser().getId());
        // 2017/9/8 將最後同步時間往前推60分鐘，暫解同步資料不完全的問題
        long time = lastUpdate.getTime();
        time -= 1000 * 60 * 60;
        lastUpdate.setTime(time);
        Logger.e(TAG, "getSetting last update time:" + TimeFormater.getInstance().toDatabaseFormat(lastUpdate));
        mDisposable.add(ApiHelper.getInstance().getSyncApi().getSettingData(platform, TokenManager.getInstance().getToken(), TimeFormater.getInstance().toDatabaseFormat(lastUpdate))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<SyncSetting>() {
                    @Override
                    public void accept(SyncSetting syncSetting) throws Exception {
                        mDisposable.add(DatabaseHelper.getInstance(mContext).saveSetting(syncSetting).subscribe(new Action() {
                                    @Override
                                    public void run() throws Exception {
//                                            getRecord();
                                    }
                                }, new Consumer<Throwable>() {
                                    @Override
                                    public void accept(Throwable t) throws Exception {
                                        if (listener != null)
                                            listener.onSyncFailed("saveSetting error:" + t.getClass().getSimpleName() + ":" + t.getMessage());
                                    }
                                }));
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable t) throws Exception {
                        if (t instanceof EOFException) {
                            Logger.e(TAG, "EOFException:" + t.getMessage());
                            getSetting();
                        } else {
                            if (listener != null)
                                listener.onSyncFailed("getSyncSetting error:" + t.getClass().getSimpleName() + ":" + t.getMessage());
                        }
                    }
                })
        );
    }

    /**
     * 取得紀錄(同步伺服器上資料)
     */
    public void getRecord() {
        getSetting();
        if (TokenManager.getInstance().getUser() == null) {
            stopSync();
            if (listener != null)
                listener.onSyncFailed("TokenManager:user is " + "null");
            return;
        }

        final Date lastUpdate = preferenceHelper.getLastSync(TokenManager.getInstance().getUser().getId());
        // 2017/9/8 將最後同步時間往前推60分鐘，暫解同步資料不完全的問題
        long time = lastUpdate.getTime();
        time -= 1000 * 60 * 60;
        lastUpdate.setTime(time);
        Logger.e(TAG, "getRecord last update time:" + TimeFormater.getInstance().toDatabaseFormat(lastUpdate));
        isRecordSyncing = true;
        mDisposable.add(
                ApiHelper.getInstance().getSyncApi().getRecordData(platform, TokenManager.getInstance().getToken(), TimeFormater.getInstance().toDatabaseFormat(lastUpdate))
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new Consumer<SyncRecord>() {
                            @Override
                            public void accept(SyncRecord syncRecord) throws Exception {
                                isRecordSyncing = false;
                                if (!shouldSync) {
                                    return;
                                }

                                Logger.e(TAG, "getRecord response last time:" + TimeFormater.getInstance().toDatabaseFormat(lastUpdate));

                                //寫入從伺服器上下載的資料
                                mDisposable.add(DatabaseHelper.getInstance(mContext).saveRecord(syncRecord, lastUpdate)
                                        .subscribe(new Consumer<Date>() {
                                            @Override
                                            public void accept(Date date) throws Exception {
                                                Logger.e(TAG,"save complete");
                                                Logger.e(TAG, "save record last time:" + TimeFormater.getInstance().toDatabaseFormat(date));
                                                if (date.compareTo(new Date()) > 0) {
                                                    date.setTime(lastUpdate.getTime());
                                                }
                                                if (TokenManager.getInstance().getUser() == null) {
                                                    stopSync();
                                                    if (listener != null)
                                                        listener.onSyncFailed("TokenManager:user is " + "null");
                                                } else {
                                                    preferenceHelper.saveLastSync(date, TokenManager.getInstance().getUser().getId());
                                                    if (listener != null)
                                                        listener.onSyncFinished();
                                                }
                                            }
                                        }, new Consumer<Throwable>() {
                                            @Override
                                            public void accept(Throwable throwable) throws Exception {
                                                if (listener != null)
                                                    listener.onSyncFailed("saveRecord error:" + throwable.getClass().getSimpleName() + ":" + throwable.getMessage());
                                            }
                                        }));
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable t) throws Exception {
                                isRecordSyncing = false;
                                if (t instanceof EOFException) {
                                    Logger.e(TAG, "EOFException:" + t.getMessage());
                                    getRecord();
                                } else {
                                    if (listener != null)
                                        listener.onSyncFailed("get SyncRecord error:" + t.getClass().getSimpleName() + ":" + t.getMessage());
                                }
                            }
                        })
        );
    }

    /**
     * 上傳紀錄
     */
    private void uploadRecord() {
        Logger.e(TAG, "uploadRecord");
        if (TokenManager.getInstance().getUser() == null) {
            stopSync();
            if (listener != null)
                listener.onSyncFailed("TokenManager:user is " + "null");
            return;
        }
        isRecordSyncing = true;
        mDisposable.add(DatabaseHelper.getInstance(mContext).getRecord(TokenManager.getInstance().getUser().getId())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<SyncRecord>() {
                    @Override
                    public void accept(SyncRecord syncRecord) throws Exception {
                        if (syncRecord.getDataCount() == 0) {
                            /*無資料時不上傳*/
                            Logger.e(TAG, "no record upload");
                            isRecordSyncing = false;
                            getRecord();
                            return;
                        }
                        final UploadRecord uploadRecord = new UploadRecord();
                        uploadRecord.setData(syncRecord);
                        Logger.e(TAG, "uploadRecord:" + gson.toJson(uploadRecord.getData()));
                        final SyncLog log = new SyncLog();
                        log.setUser_id(TokenManager.getInstance().getUser().getId());
                        log.setIsFile(false);
                        Call<JSONObject> call = ApiHelper.getInstance().getSyncApi().postData(platform, TokenManager.getInstance().getToken(), uploadRecord);
                        call.enqueue(new Callback<JSONObject>() {
                            @Override
                            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                                isRecordSyncing = false;
                                switch (response.code()) {
                                    case 200:
                                        JSONObject result = response.body();
                                        Logger.e(TAG, "upload record result:" + result.toString());
                                        try {
                                            String syncId = result.getString("sync_id");
                                            log.setId(syncId);
                                            log.setSync_status(SyncStatus.NONE);
                                            saveSyncLog(log);
                                        } catch (Exception e) {
                                            if (listener != null)
                                                listener.onSyncFailed("can't get sync_id");
                                        }
                                        break;
                                    case 401:
                                        stopSync();
                                    default:
                                        if (response.body() != null) {
                                            Logger.e(TAG, "body:" + response.body().toString());
                                        }
                                        if (response.errorBody() != null) {
                                            try {
                                                Logger.e(TAG, "errorBody:" + response.errorBody().string());
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        if (listener != null)
                                            listener.onSyncFailed(response.code() + ":" + response.message());
                                        break;
                                }
                            }

                            @Override
                            public void onFailure(Call<JSONObject> call, Throwable t) {
                                isRecordSyncing = false;
                                if (listener != null)
                                    listener.onSyncFailed(t.getMessage());
                            }
                        });
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable t) throws Exception {
                        isRecordSyncing = false;
                        if (listener != null)
                            listener.onSyncFailed(t.getMessage());
                    }
                }));
    }

    /**
     * 上傳圖片
     */
    private void uploadFile() {
        Logger.e(TAG, "uploadFile");
        if (TokenManager.getInstance().getUser() == null) {
            stopSync();
            if (listener != null)
                listener.onSyncFailed("TokenManager:user is " + "null");
            return;
        }
        mDisposable.add(DatabaseHelper.getInstance(mContext).getSyncFile(TokenManager.getInstance().getUser().getId())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<SyncRecord>() {
                    @Override
                    public void accept(SyncRecord syncFile) throws Exception {
                        if (syncFile.getFileCount() == 0) {
                            /*無資料時不上傳*/
                            Logger.e(TAG, "no file upload");
                            return;
                        }
                        final UploadRecord uploadFile = new UploadRecord();
                        uploadFile.setData(syncFile);
                        Logger.e(TAG, "uploadFile:" + gson.toJson(syncFile));
                        final SyncLog log = new SyncLog();
                        log.setUser_id(TokenManager.getInstance().getUser().getId());
                        log.setIsFile(true);
                        Call<JSONObject> call = ApiHelper.getInstance().getSyncApi().postData(platform, TokenManager.getInstance().getToken(), uploadFile);
                        call.enqueue(new Callback<JSONObject>() {
                            @Override
                            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                                switch (response.code()) {
                                    case 200:
                                        JSONObject result = response.body();
                                        Logger.e(TAG, "upload file result:" + result.toString());
                                        try {
                                            String syncId = result.getString("sync_id");
                                            log.setId(syncId);
                                            log.setSync_status(SyncStatus.NONE);
                                            saveSyncLog(log);
                                        } catch (JSONException e) {
                                            if (listener != null)
                                                listener.onSyncFileFailed("can't get sync_id");
                                        }
                                        break;
                                    case 401:
                                        stopSync();
                                    case 500:
                                        if (response.body() != null) {
                                            Logger.e(TAG, "body:" + response.body().toString());
                                        }
                                        if (response.errorBody() != null) {
                                            try {
                                                Logger.e(TAG, "errorBody:" + response.errorBody().string());
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        if (listener != null)
                                            listener.onSyncFileFailed(response.code() + ":" + response.message());
                                        break;
                                }
                            }

                            @Override
                            public void onFailure(Call<JSONObject> call, Throwable t) {
                                if (listener != null)
                                    listener.onSyncFileFailed(t.getMessage());
                            }
                        });
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable t) throws Exception {
                        if (listener != null)
                            listener.onSyncFileFailed(t.getMessage());
                    }
                }));
    }

    /**
     * 儲存同步紀錄
     */
    public void saveSyncLog(final SyncLog log) {
        mDisposable.add(DatabaseHelper.getInstance(mContext).saveSyncLog(log)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Logger.e(TAG, "save sync log completed." + (log.getIsFile() ? "file" : "record") + ":" + TimeFormater.getInstance().toDateTimeFormat(log.getCreated_at()));
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable t) throws Exception {
                if (listener != null) {
                    if (log.getIsFile())
                        listener.onSyncFileFailed(t.getMessage());
                    else
                        listener.onSyncFailed(t.getMessage());
                }
            }
        }));
    }

}
