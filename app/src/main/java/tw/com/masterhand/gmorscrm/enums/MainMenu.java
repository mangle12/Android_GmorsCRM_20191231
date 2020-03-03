package tw.com.masterhand.gmorscrm.enums;

import tw.com.masterhand.gmorscrm.R;

public enum MainMenu {
    NEW_TASK(0, R.string.main_menu_new_task, R.mipmap.menu_create, R.mipmap.quick_create,
            true),
    REPORT(1, R.string.main_menu_report, R.mipmap.menu_reimbursement, R.mipmap.quick_reimbursemen,
            true),
    CUSTOMER(2, R.string.main_menu_customer, R.mipmap.menu_client, R.mipmap.quick_client,
            true),
    APPROVAL_RECORD(3, R.string.main_menu_approval_record, R.mipmap.menu_approvalrecord, R.mipmap
            .quick_approvalrecord, true),
    APPROVAL_SIGN(4, R.string.main_menu_approval_sign, R.mipmap.menu_sign, R.mipmap
            .quick_sign, true),
//    SAMPLE(5, R.string.main_menu_sample, R.mipmap.menu_mockup_display, R.mipmap
//            .qucik_display, true),
    SALES(6, R.string.main_menu_sales, R.mipmap.menu_opportunity, R.mipmap.quick_opportunity, true),
    STATISTIC(7, R.string.main_menu_statistic, R.mipmap.menu_chart, R.mipmap.quick_chart,
            true),
    EXP_RECORD(8, R.string.main_menu_exp_record, R.mipmap.menu_reimbursingrecord, R.mipmap
            .quick_reimbursingrecord, true),
    PERSONAL(9, R.string.main_menu_personal, R.mipmap.menu_personal, R.mipmap.quick_personal,
            true),
    SETTING(10, R.string.main_menu_setting, R.mipmap.menu_setting, R.mipmap.quick_setting,
            true),
    NEW_CUSTOMER(11, R.string.main_menu_new_customer, R.mipmap.menu_addclient, R.mipmap
            .quick_addclient, true),
    NEW_CONTACT_PERSON(200, R.string.main_menu_new_contact_person, R.mipmap.menu_add_contact, R
            .mipmap.quick_add_contact,
            true),
    NEW_MISSION(202, R.string.main_menu_new_mission, R.mipmap.menu_add_mission, R.mipmap
            .quick_add_mission, false);

    int value;
    int title;// 顯示的標題
    int image;// 主選單的icon
    int icon;// 快捷選單的icon
    boolean isFixed;// 是否為固定欄位

    MainMenu(int value, int title, int image, int icon, boolean isFixed) {
        this.value = value;
        this.title = title;
        this.image = image;
        this.icon = icon;
        this.isFixed = isFixed;
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

    public int getIcon() {
        return icon;
    }

    public boolean isFixed() {
        return isFixed;
    }

    /**
     * 依value取得MainMenu
     */
    public static MainMenu getMainMenuByValue(int value) {
        MainMenu[] menus = MainMenu.values();
        for (MainMenu menu :
                menus) {
            if (menu.getValue() == value)
                return menu;
        }
        return null;
    }
}
