package tw.com.masterhand.gmorscrm.enums;

import tw.com.masterhand.gmorscrm.R;

public enum ReportTotalType {
    PERFORMANCE(0, R.string.total_type_performance, R.color.green, R.drawable.bg_green_corner),//業績
    BEHAVIOR(1, R.string.total_type_behavior, R.color.blue, R.drawable.bg_blue_corner),//行為
    ADD(2, R.string.total_type_add, R.color.orange, R.drawable.bg_orange_corner);//新增

    private int value, title, color, background;

    ReportTotalType(int value, int title, int color, int background) {
        this.value = value;
        this.title = title;
        this.color = color;
        this.background = background;
    }

    public int getValue() {
        return value;
    }

    public int getTitle() {
        return title;
    }

    public int getColor() {
        return color;
    }

    public int getBackground() {
        return background;
    }
}
