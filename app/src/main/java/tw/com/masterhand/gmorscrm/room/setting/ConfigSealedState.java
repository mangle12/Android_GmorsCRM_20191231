package tw.com.masterhand.gmorscrm.room.setting;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "ConfigSealedState")
public class ConfigSealedState extends BaseConfig {
    @NonNull
    @PrimaryKey
    public String id;

    public ConfigSealedState() {
        super();
        id = "";
    }

    @NonNull
    public String getId() {
        return id;
    }
}
