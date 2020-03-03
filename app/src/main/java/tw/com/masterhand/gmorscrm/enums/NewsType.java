package tw.com.masterhand.gmorscrm.enums;

import tw.com.masterhand.gmorscrm.R;

public enum NewsType {
    NOTIFY(0, R.mipmap.notice_note, R.string.news_notify),
    ANNOUNCE(1, R.mipmap.notice_public, R.string.news_announce),
    TASK(2, R.mipmap.notice_mission, R.string.news_mission),
    RESOURCE(3, R.mipmap.notice_resources, R.string.news_resource);

    int value, icon, title;

    NewsType(int value, int icon, int title) {
        this.value = value;
        this.icon = icon;
        this.title = title;
    }

    public int getTitle() {
        return title;
    }

    public int getIcon() {
        return icon;
    }

    public int getValue() {
        return value;
    }

    public static NewsType getNewsTypeByValue(int value) {
        for (NewsType type : NewsType.values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return null;
    }
}
