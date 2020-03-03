package tw.com.masterhand.gmorscrm.model;

import tw.com.masterhand.gmorscrm.TokenManager;
import tw.com.masterhand.gmorscrm.enums.ApprovalStatus;
import tw.com.masterhand.gmorscrm.enums.TripStatus;
import tw.com.masterhand.gmorscrm.room.record.Trip;
import tw.com.masterhand.gmorscrm.tools.Logger;

public class BaseTrip {
    Trip trip;

    public BaseTrip() {
        trip = new Trip();
        trip.setStatus(TripStatus.NORMAL);
        trip.setApproval(ApprovalStatus.UNSIGN);
        trip.setUser_id(TokenManager.getInstance().getUser().getId());
        trip.setDepartment_id(TokenManager.getInstance().getUser().getDepartment_id());
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }
}
