package tw.com.masterhand.gmorscrm.room.setting;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Date;

// 樣品產品種類
@Entity(tableName = "ConfigSampleProductType")
public class ConfigSampleProductType extends BaseConfig {
    @NonNull
    @PrimaryKey
    public String id;

    public ConfigSampleProductType() {
        super();
        id = "";
    }

    @NonNull
    public String getId() {
        return id;
    }
}
