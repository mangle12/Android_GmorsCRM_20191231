package tw.com.masterhand.gmorscrm.room.local;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Date;

@Entity(tableName = "DownloadLog")
public class DownloadLog {
    @NonNull
    @PrimaryKey
    public String parent_id;

    public long download_id;
    public Date created_at;

    public DownloadLog() {
        parent_id = "";
        created_at = new Date();
    }

    @NonNull
    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(@NonNull String parent_id) {
        this.parent_id = parent_id;
    }

    public long getDownload_id() {
        return download_id;
    }

    public void setDownload_id(long download_id) {
        this.download_id = download_id;
    }
}
