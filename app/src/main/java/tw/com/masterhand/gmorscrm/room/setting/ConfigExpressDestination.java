package tw.com.masterhand.gmorscrm.room.setting;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "ConfigExpressDestination")
public class ConfigExpressDestination extends BaseConfig {
    @NonNull
    @PrimaryKey
    public String id;

    public ConfigExpressDestination() {
        super();
        id = "";
    }

    @NonNull
    public String getId() {
        return id;
    }
}
