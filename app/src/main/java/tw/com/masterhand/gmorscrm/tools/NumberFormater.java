package tw.com.masterhand.gmorscrm.tools;

import java.text.DecimalFormat;

public class NumberFormater {
    // 金額顯示格式
    private final static DecimalFormat MONEY_FORMATER = new DecimalFormat(
            "###,###,##0.00");
    private final static DecimalFormat NUMBER_FORMATER = new DecimalFormat(
            "###,###,##0.##");

    public static String getMoneyFormat(float input) {
        return MONEY_FORMATER.format(input);
    }

    public static String getNumberFormat(float input) {
        return NUMBER_FORMATER.format(input);
    }
}
