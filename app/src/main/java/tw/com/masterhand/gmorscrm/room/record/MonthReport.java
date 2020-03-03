package tw.com.masterhand.gmorscrm.room.record;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Date;

@Entity(tableName = "MonthReport")
public class MonthReport extends BaseRecord {
    @NonNull
    @PrimaryKey
    public String id;

    public String time;// 日期格式yyyy/MM
    public String report;// 報告內容
    public int submit;// 提交狀態 0:未提交 1:已提交
    public String user_id;// 建立者id
    /*胤舜and振豪共有欄位*/
    public String next_month_plan;// 下月計劃
    /*2018/2/13新增(振豪欄位)*/
    public String current_month_customer;// 本月總結-新客戶開發
    public String current_month_project;// 本月總結-新項目跟進
    public String current_month_question;// 本月總結-問題
    public String next_month_visit_plan;// 下月計劃-拜訪計劃
    public String next_month_sales_goal;// 下月計劃-銷售工作目標
    public String next_month_guide;// 下月計劃-需要的指導
    /*2018/3/2新增(胤舜欄位)*/
    public String sale_goal;// 銷售額目標達成率
    public String current_goal;// 當月目標達成情況
    public String important_situation;// 重點行業動態，重點項目或銷售機會狀態變化情況
    public String potential_market;// 淺在目標市場
    public String compete_strategy;// 競爭與產品策略建議
    public String resource_investment;// 資源投入北碚與建設，須協調支持情況

    public MonthReport() {
        super();
        id = "";
        time = "";
        report = "";
        submit = 0;
        current_month_customer = "";
        current_month_project = "";
        current_month_question = "";
        next_month_plan = "";
        next_month_visit_plan = "";
        next_month_sales_goal = "";
        next_month_guide = "";
        sale_goal = "";
        current_goal = "";
        important_situation = "";
        potential_market = "";
        compete_strategy = "";
        resource_investment = "";
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }

    public String getCurrent_month_customer() {
        return current_month_customer;
    }

    public void setCurrent_month_customer(String current_month_customer) {
        this.current_month_customer = current_month_customer;
    }

    public String getCurrent_month_project() {
        return current_month_project;
    }

    public void setCurrent_month_project(String current_month_project) {
        this.current_month_project = current_month_project;
    }

    public String getCurrent_month_question() {
        return current_month_question;
    }

    public void setCurrent_month_question(String current_month_question) {
        this.current_month_question = current_month_question;
    }

    public String getNext_month_plan() {
        return next_month_plan;
    }

    public void setNext_month_plan(String next_month_plan) {
        this.next_month_plan = next_month_plan;
    }

    public String getNext_month_visit_plan() {
        return next_month_visit_plan;
    }

    public void setNext_month_visit_plan(String next_month_visit_plan) {
        this.next_month_visit_plan = next_month_visit_plan;
    }

    public String getNext_month_sales_goal() {
        return next_month_sales_goal;
    }

    public void setNext_month_sales_goal(String next_month_sales_goal) {
        this.next_month_sales_goal = next_month_sales_goal;
    }

    public String getNext_month_guide() {
        return next_month_guide;
    }

    public void setNext_month_guide(String next_month_guide) {
        this.next_month_guide = next_month_guide;
    }

    public String getSale_goal() {
        return sale_goal;
    }

    public void setSale_goal(String sale_goal) {
        this.sale_goal = sale_goal;
    }

    public String getCurrent_goal() {
        return current_goal;
    }

    public void setCurrent_goal(String current_goal) {
        this.current_goal = current_goal;
    }

    public String getImportant_situation() {
        return important_situation;
    }

    public void setImportant_situation(String important_situation) {
        this.important_situation = important_situation;
    }

    public String getPotential_market() {
        return potential_market;
    }

    public void setPotential_market(String potential_market) {
        this.potential_market = potential_market;
    }

    public String getCompete_strategy() {
        return compete_strategy;
    }

    public void setCompete_strategy(String compete_strategy) {
        this.compete_strategy = compete_strategy;
    }

    public String getResource_investment() {
        return resource_investment;
    }

    public void setResource_investment(String resource_investment) {
        this.resource_investment = resource_investment;
    }

    public boolean getSubmit() {
        return submit == 1;
    }

    public void setSubmit(boolean submit) {
        this.submit = submit ? 1 : 0;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
