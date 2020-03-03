package tw.com.masterhand.gmorscrm.model;

import java.util.ArrayList;
import java.util.List;

import tw.com.masterhand.gmorscrm.enums.TripType;
import tw.com.masterhand.gmorscrm.room.record.Participant;
import tw.com.masterhand.gmorscrm.room.record.Reimbursement;
import tw.com.masterhand.gmorscrm.room.record.SalesOpportunity;
import tw.com.masterhand.gmorscrm.room.record.SalesOpportunitySub;

public class SalesOpportunityWithConfig extends BaseTrip {
    private SalesOpportunity salesOpportunity;
    private List<SalesOpportunitySub> salesOpportunitySubs;

    public SalesOpportunityWithConfig() {
        super();
        salesOpportunity = new SalesOpportunity();
        salesOpportunitySubs = new ArrayList<>();
    }

    public SalesOpportunity getSalesOpportunity() {
        return salesOpportunity;
    }

    public void setSalesOpportunity(SalesOpportunity salesOpportunity) {
        this.salesOpportunity = salesOpportunity;
    }

    public List<SalesOpportunitySub> getSalesOpportunitySubs() {
        return salesOpportunitySubs;
    }

    public void setSalesOpportunitySubs(List<SalesOpportunitySub> salesOpportunitySubs) {
        this.salesOpportunitySubs = salesOpportunitySubs;
    }
}
