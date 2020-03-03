package tw.com.masterhand.gmorscrm.model;

public class StatisticTargetData {
    int current_percent;// 本期百分比(ex.9月)
    int prior_percent;// 去年同期百分比(ex.去年9月)
    float current;// 當前數值(依考核指標決定)
    float target;// 目標數值(依考核指標決定)

    public int getCurrent_percent() {
        return current_percent;
    }

    public int getPrior_percent() {
        return prior_percent;
    }

    public float getCurrent() {
        return current;
    }

    public float getTarget() {
        return target;
    }
}
