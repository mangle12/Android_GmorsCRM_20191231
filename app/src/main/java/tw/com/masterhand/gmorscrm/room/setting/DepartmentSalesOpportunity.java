package tw.com.masterhand.gmorscrm.room.setting;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.content.Context;
import android.support.annotation.NonNull;

import java.util.Date;

import tw.com.masterhand.gmorscrm.enums.ProjectStatus;

@Entity(tableName = "DepartmentSalesOpportunity")
public class DepartmentSalesOpportunity extends BaseSetting {
    @NonNull
    @PrimaryKey
    public String id;

    public String parent_id;// 父層ID
    public int type;// 類別
    public int stage_1_percentage;// 設計選型%
    public int stage_2_percentage;// 提交報價%
    public int stage_3_percentage;// 送樣測試%
    public int stage_4_percentage;// 商務談判%
    public String stage_1_name;// 設計選型
    public String stage_2_name;// 提交報價
    public String stage_3_name;// 送樣測試
    public String stage_4_name;// 商務談判
    public String stage_5_name;// 贏單
    public String stage_6_name;// 丟單

    public DepartmentSalesOpportunity() {
        super();
        id = "";
    }
    @NonNull
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStage_1_percentage() {
        return stage_1_percentage;
    }

    public void setStage_1_percentage(int stage_1_percentage) {
        this.stage_1_percentage = stage_1_percentage;
    }

    public int getStage_2_percentage() {
        return stage_2_percentage;
    }

    public void setStage_2_percentage(int stage_2_percentage) {
        this.stage_2_percentage = stage_2_percentage;
    }

    public int getStage_3_percentage() {
        return stage_3_percentage;
    }

    public void setStage_3_percentage(int stage_3_percentage) {
        this.stage_3_percentage = stage_3_percentage;
    }

    public int getStage_4_percentage() {
        return stage_4_percentage;
    }

    public void setStage_4_percentage(int stage_4_percentage) {
        this.stage_4_percentage = stage_4_percentage;
    }

    public String getStage_1_name() {
        return stage_1_name;
    }

    public void setStage_1_name(String stage_1_name) {
        this.stage_1_name = stage_1_name;
    }

    public String getStage_2_name() {
        return stage_2_name;
    }

    public void setStage_2_name(String stage_2_name) {
        this.stage_2_name = stage_2_name;
    }

    public String getStage_3_name() {
        return stage_3_name;
    }

    public void setStage_3_name(String stage_3_name) {
        this.stage_3_name = stage_3_name;
    }

    public String getStage_4_name() {
        return stage_4_name;
    }

    public void setStage_4_name(String stage_4_name) {
        this.stage_4_name = stage_4_name;
    }

    public String getStage_5_name() {
        return stage_5_name;
    }

    public void setStage_5_name(String stage_5_name) {
        this.stage_5_name = stage_5_name;
    }

    public String getStage_6_name() {
        return stage_6_name;
    }

    public void setStage_6_name(String stage_6_name) {
        this.stage_6_name = stage_6_name;
    }

    public String getNameByStage(Context context, ProjectStatus status) {
        switch (status) {
            case DESIGN:
                return stage_1_name;
            case QUOTE:
                return stage_2_name;
            case SAMPLE:
                return stage_3_name;
            case NEGOTIATION:
                return stage_4_name;
            case WIN:
                return stage_5_name;
            case LOSE:
                return stage_6_name;
        }
        return context.getString(ProjectStatus.START.getTitle());
    }
}
