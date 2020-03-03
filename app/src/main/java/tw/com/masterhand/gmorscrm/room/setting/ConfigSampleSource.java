package tw.com.masterhand.gmorscrm.room.setting;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Date;

// 樣品送樣來源
@Entity(tableName = "ConfigSampleSource")
public class ConfigSampleSource extends BaseConfig {
    @NonNull
    @PrimaryKey
    public String id;

    public ConfigSampleSource() {
        super();
        id = "";
    }

    @NonNull
    public String getId() {
        return id;
    }
}
