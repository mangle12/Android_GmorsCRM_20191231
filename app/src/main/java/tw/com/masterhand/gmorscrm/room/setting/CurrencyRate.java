package tw.com.masterhand.gmorscrm.room.setting;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Date;

@Entity(tableName = "CurrencyRate")
public class CurrencyRate extends BaseSetting {
    @NonNull
    @PrimaryKey
    public String id;

    public String config_currency_id;// 貨幣設定id
    public float rate;// 匯率

    public CurrencyRate() {
        super();
        id = "";
    }
    @NonNull
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getConfig_currency_id() {
        return config_currency_id;
    }

    public void setConfig_currency_id(String config_currency_id) {
        this.config_currency_id = config_currency_id;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }
}
