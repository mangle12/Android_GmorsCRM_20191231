package tw.com.masterhand.gmorscrm.enums;

import tw.com.masterhand.gmorscrm.R;

public enum WorkStatus {
    UNCOMPLETED(0, R.string.uncompleted, R.mipmap.common_start_yet),
    COMPLETED(1, R.string.completed, R.mipmap.common_start),
    DELAYED(2, R.string.delayed, R.mipmap.common_delay),
    CANCELED(3, R.string.canceled, R.mipmap.common_delay);

    int value, title, icon;

    WorkStatus(int value, int title, int icon) {
        this.value = value;
        this.title = title;
        this.icon = icon;
    }

    public int getTitle() {
        return title;
    }

    public int getValue() {
        return value;
    }

    public int getIcon() {
        return icon;
    }
}
