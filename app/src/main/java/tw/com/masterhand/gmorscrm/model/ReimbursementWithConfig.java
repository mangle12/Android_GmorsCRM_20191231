package tw.com.masterhand.gmorscrm.model;

import java.util.ArrayList;
import java.util.List;

import tw.com.masterhand.gmorscrm.enums.TripType;
import tw.com.masterhand.gmorscrm.room.record.File;
import tw.com.masterhand.gmorscrm.room.record.Participant;
import tw.com.masterhand.gmorscrm.room.record.Reimbursement;
import tw.com.masterhand.gmorscrm.room.setting.ConfigCurrency;
import tw.com.masterhand.gmorscrm.room.setting.CurrencyRate;
import tw.com.masterhand.gmorscrm.room.setting.User;

public class ReimbursementWithConfig extends BaseTrip {
    Reimbursement reimbursement;
    String user;
//    List<Participant> participants;
    List<ReimbursementItemWithConfig> reimbursementItems;

    public ReimbursementWithConfig() {
        super();
        trip.setType(TripType.REIMBURSEMENT);
//        participants = new ArrayList<>();
        reimbursementItems = new ArrayList<>();
    }

    public Reimbursement getReimbursement() {
        return reimbursement;
    }

    public void setReimbursement(Reimbursement reimbursement) {
        this.reimbursement = reimbursement;
    }

//    public List<Participant> getParticipants() {
//        return participants;
//    }
//
//    public void setParticipants(List<Participant> participants) {
//        this.participants = participants;
//    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public List<ReimbursementItemWithConfig> getReimbursementItems() {
        return reimbursementItems;
    }

    public void setReimbursementItems(List<ReimbursementItemWithConfig> reimbursementItems) {
        this.reimbursementItems = reimbursementItems;
    }
}
