package tw.com.masterhand.gmorscrm.model;

import tw.com.masterhand.gmorscrm.room.record.Customer;
import tw.com.masterhand.gmorscrm.room.record.Project;

public class SaleList {
    Project project;
    Customer customer;
    int percent;

    public Project getProject() {
        return project;
    }

    public Customer getCustomer() {
        return customer;
    }

    public int getPercent() {
        return percent;
    }
}
