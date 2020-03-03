package tw.com.masterhand.gmorscrm.model;

import tw.com.masterhand.gmorscrm.room.record.Customer;
import tw.com.masterhand.gmorscrm.room.record.Project;

public class SignData {
    Project project;
    Customer customer;
    int percentage;// 銷售階段百分比

    public Project getProject() {
        return project;
    }

    public Customer getCustomer() {
        return customer;
    }

    public int getPercent() {
        return percentage;
    }
}
