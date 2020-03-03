package tw.com.masterhand.gmorscrm.room.setting;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Date;

// 樣品單總面額價金額範圍
@Entity(tableName = "ConfigSampleAmountRange")
public class ConfigSampleAmountRange extends BaseConfig {
    @NonNull
    @PrimaryKey
    public String id;

    public ConfigSampleAmountRange() {
        super();
        id = "";
    }

    @NonNull
    public String getId() {
        return id;
    }
}
