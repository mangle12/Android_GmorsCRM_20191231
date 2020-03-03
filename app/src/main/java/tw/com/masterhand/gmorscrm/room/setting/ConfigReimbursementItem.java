package tw.com.masterhand.gmorscrm.room.setting;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Date;

import tw.com.masterhand.gmorscrm.enums.ReimbursementTypeLimit;

@Entity(tableName = "ConfigReimbursementItem")
public class ConfigReimbursementItem extends BaseConfig {
    @NonNull
    @PrimaryKey
    public String id;

    public int type;// 扣除對象設定(0:都可以 1:只有自己 2:只有部門 3:不用扣除對象)
    public int limit_amount;// 報銷項目金額上限

    public ConfigReimbursementItem() {
        super();
        id = "";
    }

    public String getId() {
        return id;
    }

    public ReimbursementTypeLimit getType() {
        return ReimbursementTypeLimit.getTypeByValue(type);
    }

    public int getLimit() {
        return limit_amount;
    }
}
