package tw.com.masterhand.gmorscrm.model;

import java.util.Date;
import java.util.List;

import tw.com.masterhand.gmorscrm.enums.ApprovalStatus;
import tw.com.masterhand.gmorscrm.enums.TripStatus;
import tw.com.masterhand.gmorscrm.enums.TripType;
import tw.com.masterhand.gmorscrm.room.record.Absent;
import tw.com.masterhand.gmorscrm.room.record.Contract;
import tw.com.masterhand.gmorscrm.room.record.Customer;
import tw.com.masterhand.gmorscrm.room.record.Office;
import tw.com.masterhand.gmorscrm.room.record.Quotation;
import tw.com.masterhand.gmorscrm.room.record.Reimbursement;
import tw.com.masterhand.gmorscrm.room.record.Sample;
import tw.com.masterhand.gmorscrm.room.record.Task;
import tw.com.masterhand.gmorscrm.room.record.Trip;
import tw.com.masterhand.gmorscrm.room.record.Visit;
import tw.com.masterhand.gmorscrm.room.setting.User;

public class ApprovalList {
    String id;

    Date created_at;
    Date updated_at;
    Date deleted_at;

    String trip_id;
    String approval_stage_id;
    int stage;
    int approval;// 審批狀態 0:未審批 2:已通過 3:未通過
    String getApproval_stage_user_id;
    String user_id;
    String reason;
    ApprovalStage approval_stage;
    Trip trip;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public Date getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }

    public Date getDeleted_at() {
        return deleted_at;
    }

    public void setDeleted_at(Date deleted_at) {
        this.deleted_at = deleted_at;
    }

    public String getTrip_id() {
        return trip_id;
    }

    public void setTrip_id(String trip_id) {
        this.trip_id = trip_id;
    }

    public String getApproval_stage_id() {
        return approval_stage_id;
    }

    public void setApproval_stage_id(String approval_stage_id) {
        this.approval_stage_id = approval_stage_id;
    }

    public int getStage() {
        return stage;
    }

    public void setStage(int stage) {
        this.stage = stage;
    }

    public ApprovalStatus getApproval() {
        return ApprovalStatus.getStatusByCode(approval);
    }

    public void setApproval(ApprovalStatus approval) {
        this.approval = approval.getCode();
    }

    public String getGetApproval_stage_user_id() {
        return getApproval_stage_user_id;
    }

    public void setGetApproval_stage_user_id(String getApproval_stage_user_id) {
        this.getApproval_stage_user_id = getApproval_stage_user_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public ApprovalStage getApproval_stage() {
        return approval_stage;
    }

    public void setApproval_stage(ApprovalStage approval_stage) {
        this.approval_stage = approval_stage;
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    public static class ApprovalStage {
        String id;
        String customer_id;
        String project_id;
        String project_trip_approval_id;
        int stage;
        Date created_at;
        Date updated_at;
        Date deleted_at;
        List<ApprovalUser> users;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCustomer_id() {
            return customer_id;
        }

        public void setCustomer_id(String customer_id) {
            this.customer_id = customer_id;
        }

        public String getProject_id() {
            return project_id;
        }

        public void setProject_id(String project_id) {
            this.project_id = project_id;
        }

        public String getProject_trip_approval_id() {
            return project_trip_approval_id;
        }

        public void setProject_trip_approval_id(String project_trip_approval_id) {
            this.project_trip_approval_id = project_trip_approval_id;
        }

        public int getStage() {
            return stage;
        }

        public void setStage(int stage) {
            this.stage = stage;
        }

        public Date getCreated_at() {
            return created_at;
        }

        public void setCreated_at(Date created_at) {
            this.created_at = created_at;
        }

        public Date getUpdated_at() {
            return updated_at;
        }

        public void setUpdated_at(Date updated_at) {
            this.updated_at = updated_at;
        }

        public Date getDeleted_at() {
            return deleted_at;
        }

        public void setDeleted_at(Date deleted_at) {
            this.deleted_at = deleted_at;
        }

        public List<ApprovalUser> getUsers() {
            return users;
        }

        public void setUsers(List<ApprovalUser> users) {
            this.users = users;
        }
    }

    public static class ApprovalUser {
        String id;
        String approval_id;
        String user_id;
        Date created_at;
        Date updated_at;
        Date deleted_at;
        User user;
        User default_user;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getApproval_id() {
            return approval_id;
        }

        public void setApproval_id(String approval_id) {
            this.approval_id = approval_id;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public Date getCreated_at() {
            return created_at;
        }

        public void setCreated_at(Date created_at) {
            this.created_at = created_at;
        }

        public Date getUpdated_at() {
            return updated_at;
        }

        public void setUpdated_at(Date updated_at) {
            this.updated_at = updated_at;
        }

        public Date getDeleted_at() {
            return deleted_at;
        }

        public void setDeleted_at(Date deleted_at) {
            this.deleted_at = deleted_at;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public User getDefault_user() {
            return default_user;
        }

        public void setDefault_user(User default_user) {
            this.default_user = default_user;
        }
    }
}
