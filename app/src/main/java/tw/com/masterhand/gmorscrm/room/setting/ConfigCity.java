package tw.com.masterhand.gmorscrm.room.setting;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Date;

@Entity(tableName = "ConfigCity")
public class ConfigCity extends BaseConfig {
    @NonNull
    @PrimaryKey
    public String id;

    public String config_country_id;// 國家ID

    public ConfigCity() {
        super();
        id = "";
    }

    @NonNull
    public String getId() {
        return id;
    }

    public String getConfig_country_id() {
        return config_country_id;
    }

}
