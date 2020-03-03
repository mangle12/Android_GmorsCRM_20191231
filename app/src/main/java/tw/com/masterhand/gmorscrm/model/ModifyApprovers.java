package tw.com.masterhand.gmorscrm.model;

public class ModifyApprovers {
    public String trip_id;// TripID
    public String approval_stage_id;
    public String approval_stage_user_id;// 被更換的user_id
    public String user_id;// 更換的user_id

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

    public String getApproval_stage_user_id() {
        return approval_stage_user_id;
    }

    public void setApproval_stage_user_id(String approval_stage_user_id) {
        this.approval_stage_user_id = approval_stage_user_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

}
