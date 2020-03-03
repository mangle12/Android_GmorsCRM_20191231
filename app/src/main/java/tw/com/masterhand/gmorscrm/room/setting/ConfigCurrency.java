package tw.com.masterhand.gmorscrm.room.setting;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Date;

@Entity(tableName = "ConfigCurrency")
public class ConfigCurrency extends BaseConfig {
    @NonNull
    @PrimaryKey
    public String id;

    public ConfigCurrency() {
        super();
        id = "";
    }

    @NonNull
    public String getId() {
        return id;
    }
}
