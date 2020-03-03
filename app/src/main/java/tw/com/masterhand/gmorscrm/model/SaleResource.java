package tw.com.masterhand.gmorscrm.model;

import android.webkit.MimeTypeMap;

import java.util.Date;

import tw.com.masterhand.gmorscrm.enums.DownloadStatus;

public class SaleResource {
    String id;
    String name;// 名稱
    String file_url;// 檔案URL
    long size;// 檔案大小
    Date updated_at;// 更新日期
    String file;// 檔名

    long downloadId;// 下載ID(DownloadManager用)
    int status;// 下載狀態(DownloadManager用)
    String localUri;// app本地路徑(DownloadManager用)

    public SaleResource() {
        status = DownloadStatus.NONE.getCode();
        file = "";
        size = 0;
        name = "";
        id = "";
        updated_at = new Date();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getFile() {
        return file;
    }

    public String getUrl() {
        return file_url;
    }

    public long getSize() {
        return size;
    }

    public Date getUpdated_at() {
        return updated_at;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFile_url(String file_url) {
        this.file_url = file_url;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getExtension() {
        return MimeTypeMap.getFileExtensionFromUrl(file_url);
    }

    public String getType() {
        String type = null;
        String extension = getExtension();
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    public long getDownloadId() {
        return downloadId;
    }

    public void setDownloadId(long downloadId) {
        this.downloadId = downloadId;
    }

    public DownloadStatus getStatus() {
        return DownloadStatus.getStatusByCode(status);
    }

    public void setStatus(DownloadStatus status) {
        this.status = status.getCode();
    }

    public String getLocalUri() {
        return localUri;
    }

    public void setLocalUri(String localUri) {
        this.localUri = localUri;
    }
}
