package tw.com.masterhand.gmorscrm.room.record;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Date;

import tw.com.masterhand.gmorscrm.enums.ApprovalRequire;
import tw.com.masterhand.gmorscrm.enums.ApprovalStatus;
import tw.com.masterhand.gmorscrm.enums.SubmitStatus;
import tw.com.masterhand.gmorscrm.enums.TripStatus;
import tw.com.masterhand.gmorscrm.enums.TripType;
import tw.com.masterhand.gmorscrm.room.converter.DateConverter;
import tw.com.masterhand.gmorscrm.tools.Logger;

@Entity(tableName = "Trip")
public class Trip extends BaseRecord {
    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Trip) {
            return ((Trip) obj).id.equals(id);
        }
        return super.equals(obj);
    }

    @NonNull
    @PrimaryKey
    public String id;

    public String project_id;// 工作項目ID
    public String customer_id;// 客戶ID
    public int status;// 目前狀態 1:正常 2:延後 3:取消
    public String name;// 工作項目名稱
    public int type;// 種類 1:拜訪 2:內勤 3:任務 4:請假 5: 報價 6:合約 7:報銷單 8:樣品 9:特價 10:新項目量產 11:非標詢價
    // 12:彈簧蓄能圈詢價 13:特批發貨 14:回款 15:快遞 16:出差
    public int approval;// 審批狀態 0:未審批 2:已通過 3:未通過
    public int approval_required;// 是否需要審批 0:未知 1:需要 2:不需要
    public int approval_required_stage;
    public int approval_stage;
    public Date from_date;// 開始
    public Date to_date;// 結束
    public int date_type;// 是否為全天 1:是 0:否
    public int notification;// 提示 -1:不提示 >1分鐘數
    public int clock_id;// 鬧鐘ID
    public String reason;// 目前取消/延後原因
    public String description;// 備註
    public String user_id;// 使用者ID
    public String department_id;// 使用者的部門ID

    /*2018/1/30新增欄位*/
    public int submit_status;// 提交狀態 0:未提交 1:已提交
    /*2018/2/5新增欄位*/
    public String assistant_id;// 銷售助理ID
    public int assistant_status;// 銷售助理處理狀態 0:未處理 1:已處理

    public Trip() {
        super();
        id = "";
        from_date = new Date();
        to_date = new Date();
        status = TripStatus.NORMAL.getValue();
        approval = ApprovalStatus.UNSIGN.getCode();
        approval_required = ApprovalRequire.UNKNOWN.getCode();
        name = "";
        approval = 0;
        date_type = 0;
        notification = -1;
        clock_id = 0;
        reason = "";
        description = "";
        submit_status = SubmitStatus.NONE.getCode();
        assistant_status = 0;
        user_id = "";
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProject_id() {
        return project_id;
    }

    public void setProject_id(String project_id) {
        this.project_id = project_id;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public TripStatus getStatus() {
        return TripStatus.getTripTypeByValue(status);
    }

    public void setStatus(TripStatus status) {
        this.status = status.getValue();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TripType getType() {
        return TripType.getTripTypeByValue(type);
    }

    public void setType(TripType type) {
        this.type = type.getValue();
    }

    public ApprovalStatus getApproval() {
        return ApprovalStatus.getStatusByCode(approval);
    }

    public void setApproval(ApprovalStatus approval) {
        this.approval = approval.getCode();
    }

    public ApprovalRequire getApprovalRequired() {
        return ApprovalRequire.getItemByCode(approval_required);
    }

    public void setApprovalRequired(ApprovalRequire require) {
        this.approval_required = require.getCode();
    }

    public Date getFrom_date() {
        return from_date;
    }

    public void setFrom_date(Date from_date) {
        this.from_date = from_date;
    }

    public Date getTo_date() {
        return to_date;
    }

    public void setTo_date(Date to_date) {
        this.to_date = to_date;
    }

    public boolean getDate_type() {
        return date_type == 1;
    }

    public void setDate_type(boolean date_type) {
        this.date_type = date_type ? 1 : 0;
    }

    public int getNotification() {
        return notification * 1000 * 60;
    }

    public void setNotification(int notification) {
        this.notification = notification / 1000 / 60;
    }

    public int getClock_id() {
        return clock_id;
    }

    public void setClock_id(int clock_id) {
        this.clock_id = clock_id;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUser_id() {
        return user_id != null ? user_id : "";
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getDepartment_id() {
        return department_id;
    }

    public void setDepartment_id(String department_id) {
        this.department_id = department_id;
    }

    public SubmitStatus getSubmit() {
        return SubmitStatus.getStatusByCode(submit_status);
    }

    public void setSubmit(SubmitStatus submit) {
        this.submit_status = submit.getCode();
    }

    public String getAssistant_id() {
        return assistant_id;
    }

    public void setAssistant_id(String assistant_id) {
        this.assistant_id = assistant_id;
    }
}
