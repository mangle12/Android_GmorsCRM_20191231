package tw.com.masterhand.gmorscrm.enums;

import tw.com.masterhand.gmorscrm.R;

public enum StatisticMenu {
    TARGET(0, R.string.statistic_menu_target, R.mipmap.charts_dashboard),
    VISIT(1, R.string.statistic_menu_visit, R.mipmap.charts_top),
    PERFORMANCE(2, R.string.statistic_menu_performance, R.mipmap.charts_top),
    SIGN(3, R.string.statistic_menu_sign, R.mipmap.charts_top),
    WIN(4, R.string.statistic_menu_win, R.mipmap.charts_top),
    SALE(4, R.string.statistic_menu_sale, R.mipmap.charts_top);

    int value;
    int title;// 顯示的標題
    int image;// 顯示的icon

    StatisticMenu(int value, int title, int image) {
        this.value = value;
        this.title = title;
        this.image = image;
    }

    public int getValue() {
        return value;
    }

    public int getTitle() {
        return title;
    }

    public int getImage() {
        return image;
    }

    /**
     * 依value取得StatisticMenu
     */
    public static StatisticMenu getStatisticMenuByValue(int value) {
        StatisticMenu[] menus = StatisticMenu.values();
        for (StatisticMenu menu : menus) {
            if (menu.getValue() == value)
                return menu;
        }
        return null;
    }

}
