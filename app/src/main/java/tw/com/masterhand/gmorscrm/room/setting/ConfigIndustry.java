package tw.com.masterhand.gmorscrm.room.setting;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Date;

@Entity(tableName = "ConfigIndustry")
public class ConfigIndustry extends BaseConfig {
    @NonNull
    @PrimaryKey
    public String id;

    public ConfigIndustry() {
        super();
        id = "";
    }

    @NonNull
    public String getId() {
        return id;
    }
}
