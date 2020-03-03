package tw.com.masterhand.gmorscrm.room.setting;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Date;

// 樣品送樣理由
@Entity(tableName = "ConfigSampleReason")
public class ConfigSampleReason extends BaseConfig {
    @NonNull
    @PrimaryKey
    public String id;

    public ConfigSampleReason() {
        super();
        id = "";
    }

    @NonNull
    public String getId() {
        return id;
    }
}
