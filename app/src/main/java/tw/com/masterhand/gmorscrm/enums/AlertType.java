package tw.com.masterhand.gmorscrm.enums;

import tw.com.masterhand.gmorscrm.R;

public enum AlertType {
    ALERT_NO(-1, R.string.alert_no),
    ALERT_CURRENT(0, R.string.alert_current),
    ALERT_5M(1000 * 60 * 5, R.string.alert_5M),
    ALERT_15M(1000 * 60 * 15, R.string.alert_15M),
    ALERT_30M(1000 * 60 * 30, R.string.alert_30M),
    ALERT_1H(1000 * 60 * 60, R.string.alert_1H),
    ALERT_2H(1000 * 60 * 60 * 2, R.string.alert_2H),
    ALERT_1D(1000 * 60 * 60 * 24, R.string.alert_1D),
    ALERT_2D(1000 * 60 * 60 * 24 * 2, R.string.alert_2D),
    ALERT_1W(1000 * 60 * 60 * 24 * 7, R.string.alert_1W);

    private int title, time;

    AlertType(int time, int title) {
        this.title = title;
        this.time = time;
    }

    public int getTitle() {
        return title;
    }

    public int getTime() {
        return time;
    }

    public static AlertType getTypeByTime(int time) {
        for (AlertType type : AlertType.values()) {
            if (type.getTime() == time)
                return type;
        }
        return ALERT_NO;
    }
}
