package tw.com.masterhand.gmorscrm.enums;

import android.content.Context;

import tw.com.masterhand.gmorscrm.R;
import tw.com.masterhand.gmorscrm.room.setting.DepartmentSalesOpportunity;

public enum ProjectStatus {
    START(0, R.string.project_status_start, R.color.gray),
    DESIGN(1, R.string.project_status_design, R.color.orange),
    QUOTE(2, R.string.project_status_quote, R.color.orange),
    SAMPLE(3, R.string.project_status_sample, R.color.orange),
    NEGOTIATION(4, R.string.project_status_negotiation, R.color.orange),
    WIN(5, R.string.project_status_win, R.color.red),
    LOSE(6, R.string.project_status_lose, R.color.black);

    int value, title, color;

    ProjectStatus(int value, int title, int color) {
        this.value = value;
        this.title = title;
        this.color = color;
    }

    public int getTitle() {
        return title;
    }

    public int getValue() {
        return value;
    }

    public int getColor() {
        return color;
    }

    public static ProjectStatus getProjectStatusByCode(int code) {
        for (ProjectStatus type : ProjectStatus.values()) {
            if (code == type.getValue())
                return type;
        }
        return null;
    }

    public static ProjectStatus getProjectStatusByPercent(int percent, DepartmentSalesOpportunity
            opportunity) {
        ProjectStatus status = START;
        if (percent < 0)
            status = LOSE;
        else if (percent == 100)
            status = WIN;
        else if (percent >= opportunity.getStage_4_percentage())
            status = NEGOTIATION;
        else if (percent >= opportunity.getStage_3_percentage())
            status = SAMPLE;
        else if (percent >= opportunity.getStage_2_percentage())
            status = QUOTE;
        else if (percent >= opportunity.getStage_1_percentage())
            status = DESIGN;
        return status;
    }

    public static int getPercent(ProjectStatus status, DepartmentSalesOpportunity
            opportunity) {
        int percent = 0;
        switch (status) {
            case DESIGN:
                percent = opportunity.getStage_1_percentage();
                break;
            case QUOTE:
                percent = opportunity.getStage_2_percentage();
                break;
            case SAMPLE:
                percent = opportunity.getStage_3_percentage();
                break;
            case NEGOTIATION:
                percent = opportunity.getStage_4_percentage();
                break;
            case WIN:
                percent = 100;
                break;
            case LOSE:
                percent = 0;
                break;
        }
        return percent;
    }

    public static String getName(Context context, ProjectStatus status, DepartmentSalesOpportunity
            opportunity) {
        String name = "none";
        if (opportunity != null)
            switch (status) {
                case DESIGN:
                    name = opportunity.getStage_1_name();
                    break;
                case QUOTE:
                    name = opportunity.getStage_2_name();
                    break;
                case SAMPLE:
                    name = opportunity.getStage_3_name();
                    break;
                case NEGOTIATION:
                    name = opportunity.getStage_4_name();
                    break;
                case WIN:
                    name = context.getString(R.string.project_status_win);
                    break;
                case LOSE:
                    name = context.getString(R.string.project_status_lose);
                    break;
            }
        return name;
    }
}
