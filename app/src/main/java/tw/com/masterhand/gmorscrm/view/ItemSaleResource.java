package tw.com.masterhand.gmorscrm.view;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import rebus.permissionutils.PermissionEnum;
import rebus.permissionutils.PermissionManager;
import rebus.permissionutils.PermissionUtils;
import rebus.permissionutils.SimpleCallback;
import tw.com.masterhand.gmorscrm.BuildConfig;
import tw.com.masterhand.gmorscrm.MyApplication;
import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.activity.sample.Sample3DActivity;
import tw.com.masterhand.gmorscrm.enums.DownloadStatus;
import tw.com.masterhand.gmorscrm.model.SaleResource;
import tw.com.masterhand.gmorscrm.room.DatabaseHelper;
import tw.com.masterhand.gmorscrm.room.local.DownloadLog;
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.tools.TimeFormater;

import static android.content.Context.DOWNLOAD_SERVICE;

public class ItemSaleResource extends RelativeLayout {
    Context context;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.tvStatus)
    TextView tvStatus;
    @BindView(R.id.tvSize)
    TextView tvSize;
    @BindView(R.id.tvDate)
    TextView tvDate;
    @BindView(R.id.tvType)
    TextView tvType;
    @BindView(R.id.ivIcon)
    ImageView ivIcon;

    DownloadManager downloadManager;
    SaleResource data;

    public ItemSaleResource(Context context) {
        super(context);
        init(context);
    }

    public ItemSaleResource(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ItemSaleResource(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context mContext) {
        context = mContext;
        downloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
        // 連接畫面
        View view = inflate(getContext(), R.layout.item_sale_resource, this);
        ButterKnife.bind(this, view);
    }

    public void setResource(SaleResource resource) {
        data = resource;
        tvTitle.setText(data.getName());
        DecimalFormat format = new DecimalFormat("0.0");
        float size = data.getSize();// MB
        String resourceSize = format.format(size) + "bytes";
        if (size / 1024 > 1) {
            resourceSize = format.format(size / 1024) + "kb";
        } else if (size / (1024 * 1024) > 1)
            resourceSize = format.format(size / (1024 * 1024)) + "Mb";
        tvSize.setText(resourceSize);
        tvDate.setText(TimeFormater.getInstance().toDateFormat(data.getUpdated_at()));
        tvType.setText(resource.getExtension());
        DatabaseHelper.getInstance(context).getDownloadLog(data.getId())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<DownloadLog>() {

            @Override
            public void accept(DownloadLog downloadLog) throws Exception {
                data.setDownloadId(downloadLog.getDownload_id());
                getStatus();
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                updateStatus();
            }
        });
    }

    public void getStatus() {
        if (data.getDownloadId() > 0) {
            DownloadManager.Query query = new DownloadManager.Query();
            Cursor cursor = downloadManager.query(query.setFilterById(data.getDownloadId()));
            if (cursor != null && cursor.moveToFirst()) {
                String uriPath = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                int statusCode = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                data.setStatus(DownloadStatus.getStatusByCode(statusCode));
                data.setLocalUri(uriPath);
                cursor.close();
            }
        }
        updateStatus();
    }

    public void updateStatus() {
        if (data.getStatus() != DownloadStatus.NONE) {
            tvType.setVisibility(VISIBLE);
            tvStatus.setText(context.getString(data.getStatus().getTitle()));
            switch (data.getStatus()) {
                case PAUSED:
                case PENDING:
                case RUNNING:
                    tvStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(R.mipmap.notice_reload_1, 0, 0, 0);
                    setOnClickListener(null);
                    break;
                case SUCCESSFUL:
                    tvType.setTextColor(ContextCompat.getColor(context, R.color.orange));
                    ivIcon.setImageResource(R.mipmap.notice_download);
                    tvStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(R.mipmap.notice_fail, 0, 0, 0);
                    tvStatus.setTextColor(ContextCompat.getColor(context, R.color.orange));
                    if (TextUtils.isEmpty(data.getLocalUri())) {
                        DownloadManager.Query query = new DownloadManager.Query();
                        Cursor cursor = downloadManager.query(query.setFilterById(data.getDownloadId()));
                        if (cursor != null && cursor.moveToFirst()) {
                            String uriPath = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                            data.setLocalUri(uriPath);
                            Logger.e(getClass().getSimpleName(), "open path:" + data.getLocalUri());
                            cursor.close();
                        }
                    }
                    setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // 點擊開啟檔案
                            File file = new File(Uri.parse(data.getLocalUri()).getPath());
                            if (file.exists()) {
                                Logger.e(ItemSaleResource.class.getSimpleName(), "file uri:" + file.getAbsolutePath());
                                Logger.e(ItemSaleResource.class.getSimpleName(), "file space:" + file.getTotalSpace());
                                Uri uri;
                                if (Build.VERSION.SDK_INT >= 24) {
                                    uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
                                } else {
                                    uri = Uri.fromFile(file);
                                }
                                try {
                                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                    intent.setDataAndType(uri, data.getType());
                                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                    context.startActivity(intent);
                                } catch (Exception e) {
                                    Toast.makeText(context, R.string.error_msg_no_app, Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Logger.e(ItemSaleResource.class.getSimpleName(), "file not exists");
                            }
                        }
                    });
                    break;
                case FAILED:
                    tvType.setVisibility(GONE);
                    ivIcon.setImageResource(R.mipmap.notice_reload);
                    tvStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(R.mipmap.notice_fail, 0, 0, 0);
                    tvStatus.setTextColor(ContextCompat.getColor(context, R.color.red));
                    setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // 點擊開始下載
                            startDownload();
                        }
                    });
                    break;
            }
        } else {
            setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    // 點擊開始下載
                    startDownload();
                }
            });
        }
    }

    private void startDownload() {
        if (data.getExtension().equals("stl")) {
            Intent intent = new Intent(context, Sample3DActivity.class);
            intent.putExtra(MyApplication.INTENT_KEY_FILE_URL, data.getUrl());
            context.startActivity(intent);
            return;
        }

        PermissionEnum[] permission = {PermissionEnum.WRITE_EXTERNAL_STORAGE};
        boolean granted = PermissionUtils.isGranted(context, permission);
        if (granted) {
            switch (data.getStatus()) {
                case FAILED:
                case NONE:
                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(data.getUrl()));
                    request.setTitle(context.getString(R.string.app_name));
                    request.setDescription("download " + data.getName());
                    request.allowScanningByMediaScanner();
                    File storageDir = context.getExternalFilesDir("downloads");
                    File file = new File(storageDir, data.getName() + "." + data.getType());
                    request.setDestinationUri(Uri.fromFile(file));
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    long requestId = downloadManager.enqueue(request);
                    data.setDownloadId(requestId);
                    data.setStatus(DownloadStatus.RUNNING);

                    DownloadLog log = new DownloadLog();
                    log.setParent_id(data.getId());
                    log.setDownload_id(requestId);
                    DatabaseHelper.getInstance(context).saveDownloadLog(log).observeOn(AndroidSchedulers.mainThread()).subscribe(new CompletableObserver() {

                        @Override
                        public void onSubscribe(@NonNull Disposable d) {

                        }

                        @Override
                        public void onComplete() {
                            updateStatus();
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            data.setDownloadId(0);
                            data.setStatus(DownloadStatus.NONE);
                            updateStatus();
                        }
                    });
            }
        } else {
            PermissionManager.with(context).permission(permission).askAgain(true)
                    .callback(new SimpleCallback() {
                        @Override
                        public void result(boolean allPermissionsGranted) {
                            if (allPermissionsGranted)
                                startDownload();
                            else
                                Toast.makeText(context, context.getString(R.string.error_msg_permission), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .ask();
        }

    }
}
