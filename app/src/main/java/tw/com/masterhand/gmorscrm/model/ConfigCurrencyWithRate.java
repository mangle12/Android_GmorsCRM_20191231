package tw.com.masterhand.gmorscrm.model;

import tw.com.masterhand.gmorscrm.room.setting.ConfigCurrency;
import tw.com.masterhand.gmorscrm.room.setting.CurrencyRate;

public class ConfigCurrencyWithRate {
    ConfigCurrency configCurrency;
    CurrencyRate currencyRate;

    public ConfigCurrency getConfigCurrency() {
        return configCurrency;
    }

    public void setConfigCurrency(ConfigCurrency configCurrency) {
        this.configCurrency = configCurrency;
    }

    public CurrencyRate getCurrencyRate() {
        return currencyRate;
    }

    public void setCurrencyRate(CurrencyRate currencyRate) {
        this.currencyRate = currencyRate;
    }
}
