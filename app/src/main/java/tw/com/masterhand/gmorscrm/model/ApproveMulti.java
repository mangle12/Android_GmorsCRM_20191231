package tw.com.masterhand.gmorscrm.model;

import tw.com.masterhand.gmorscrm.enums.ApprovalStatus;

public class ApproveMulti {
    String content;//複數審批的內容，Json array
    int approval;
    String reason;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ApprovalStatus getApproval() {
        return ApprovalStatus.getStatusByCode(approval);
    }

    public void setApproval(ApprovalStatus approval) {
        this.approval = approval.getCode();
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
