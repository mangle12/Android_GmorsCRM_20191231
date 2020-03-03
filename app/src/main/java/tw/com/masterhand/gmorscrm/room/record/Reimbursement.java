package tw.com.masterhand.gmorscrm.room.record;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import tw.com.masterhand.gmorscrm.enums.ReimbursementApproval;

@Entity(tableName = "Reimbursement")
public class Reimbursement extends BaseRecord {
    @NonNull
    @PrimaryKey
    public String id;

    public String trip_id;// 行事曆ID
    public float amount;// 金額
    public int approval;// 1:一級審批 2:二級審批

    public Reimbursement() {
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

    public String getTrip_id() {
        return trip_id;
    }

    public void setTrip_id(String trip_id) {
        this.trip_id = trip_id;
    }


    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public ReimbursementApproval getApproval() {
        return ReimbursementApproval.getTypeByCode(approval);
    }

    public void setApproval(ReimbursementApproval approval) {
        this.approval = approval.getCode();
    }
}
