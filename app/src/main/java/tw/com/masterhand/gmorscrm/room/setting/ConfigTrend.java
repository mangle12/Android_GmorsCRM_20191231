package tw.com.masterhand.gmorscrm.room.setting;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "ConfigTrend")
public class ConfigTrend extends BaseConfig {
    @NonNull
    @PrimaryKey
    public String id;

    public ConfigTrend() {
        super();
        id = "";
    }

    @NonNull
    public String getId() {
        return id;
    }
}
