package tw.com.masterhand.gmorscrm.enums;

import android.app.DownloadManager;

import tw.com.masterhand.gmorscrm.R;

// 樣品排序方式
public enum DownloadStatus {
    NONE(-1, R.string.empty_show),
    FAILED(DownloadManager.STATUS_FAILED, R.string.download_failed),
    PAUSED(DownloadManager.STATUS_PAUSED, R.string.download_paused),
    PENDING(DownloadManager.STATUS_PENDING, R.string.download_pending),
    RUNNING(DownloadManager.STATUS_RUNNING, R.string.download_running),
    SUCCESSFUL(DownloadManager.STATUS_SUCCESSFUL, R.string.download_success);

    private int code, title;

    DownloadStatus(int code, int title) {
        this.code = code;
        this.title = title;
    }

    public int getCode() {
        return code;
    }

    public int getTitle() {
        return title;
    }

    public static DownloadStatus getStatusByCode(int code) {
        for (DownloadStatus status : DownloadStatus.values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        return DownloadStatus.NONE;
    }
}
