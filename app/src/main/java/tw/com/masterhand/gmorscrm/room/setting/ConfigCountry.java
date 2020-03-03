package tw.com.masterhand.gmorscrm.room.setting;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Date;

@Entity(tableName = "ConfigCountry")
public class ConfigCountry extends BaseConfig {
    @NonNull
    @PrimaryKey
    public String id;

    public ConfigCountry() {
        super();
        id = "";
    }

    @NonNull
    public String getId() {
        return id;
    }

}
