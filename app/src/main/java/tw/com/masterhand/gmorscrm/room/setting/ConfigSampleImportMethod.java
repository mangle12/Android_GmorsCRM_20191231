package tw.com.masterhand.gmorscrm.room.setting;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Date;

// 樣品進口方式
@Entity(tableName = "ConfigSampleImportMethod")
public class ConfigSampleImportMethod extends BaseConfig {
    @NonNull
    @PrimaryKey
    public String id;

    public ConfigSampleImportMethod() {
        super();
        id = "";
    }

    @NonNull
    public String getId() {
        return id;
    }
}
