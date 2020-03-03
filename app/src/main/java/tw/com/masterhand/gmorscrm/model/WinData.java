package tw.com.masterhand.gmorscrm.model;

import tw.com.masterhand.gmorscrm.room.record.Customer;
import tw.com.masterhand.gmorscrm.room.record.Project;

public class WinData {
    Customer customer;
    Project project;

    public Project getProject() {
        return project;
    }

    public Customer getCustomer() {
        return customer;
    }
}
