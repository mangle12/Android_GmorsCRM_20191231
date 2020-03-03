package tw.com.masterhand.gmorscrm.room.local;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Date;

import tw.com.masterhand.gmorscrm.enums.SyncStatus;

@Entity(tableName = "ReadLog")
public class ReadLog {
    @NonNull
    @PrimaryKey
    public String parent_id;

    public Date created_at;

    public ReadLog() {
        parent_id = "";
        created_at = new Date();
    }

    public void setParent_id(@NonNull String parent_id) {
        this.parent_id = parent_id;
    }
}
