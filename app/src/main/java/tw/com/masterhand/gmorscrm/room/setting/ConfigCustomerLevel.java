package tw.com.masterhand.gmorscrm.room.setting;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Date;

@Entity(tableName = "ConfigCustomerLevel")
public class ConfigCustomerLevel extends BaseConfig {
    @NonNull
    @PrimaryKey
    public String id;

    public ConfigCustomerLevel() {
        super();
        id = "";
    }

    @NonNull
    public String getId() {
        return id;
    }
}
