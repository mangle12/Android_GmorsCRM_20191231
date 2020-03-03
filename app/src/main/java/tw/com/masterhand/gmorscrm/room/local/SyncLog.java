package tw.com.masterhand.gmorscrm.room.local;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Date;

import tw.com.masterhand.gmorscrm.enums.SyncStatus;

@Entity(tableName = "SyncLog")
public class SyncLog {
    @NonNull
    @PrimaryKey
    public String id;

    public Date created_at;// 同步時間
    public Date updated_at;
    public Date deleted_at;

    public int sync_status;// 同步狀態 0:尚未寫入後台資料庫 1:成功寫入後台資料庫 2:同步失敗

    public int is_file;// 0:紀錄 1:檔案

    public String user_id;// 使用者ID

    public SyncLog() {
        id = "";
        created_at = new Date();
        updated_at = new Date();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
        this.updated_at=created_at;
    }

    public Date getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }

    public Date getDeleted_at() {
        return deleted_at;
    }

    public void setDeleted_at(Date deleted_at) {
        this.deleted_at = deleted_at;
    }

    public SyncStatus getSync_status() {
        return SyncStatus.getStatusByCode(sync_status);
    }

    public void setSync_status(SyncStatus sync_status) {
        this.sync_status = sync_status.getCode();
    }

    public boolean getIsFile() {
        return is_file == 1;
    }

    public void setIsFile(boolean isFile) {
        this.is_file = isFile ? 1 : 0;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
