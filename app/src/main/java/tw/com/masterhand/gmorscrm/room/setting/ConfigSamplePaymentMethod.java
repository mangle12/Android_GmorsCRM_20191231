package tw.com.masterhand.gmorscrm.room.setting;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Date;

// 樣品國內運輸 - 付款方式
@Entity(tableName = "ConfigSamplePaymentMethod")
public class ConfigSamplePaymentMethod extends BaseConfig {
    @NonNull
    @PrimaryKey
    public String id;

    public ConfigSamplePaymentMethod() {
        super();
        id = "";
    }

    @NonNull
    public String getId() {
        return id;
    }
}
