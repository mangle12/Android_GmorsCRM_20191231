package tw.com.masterhand.gmorscrm.model;

import tw.com.masterhand.gmorscrm.room.record.Customer;
import tw.com.masterhand.gmorscrm.room.record.Project;
import tw.com.masterhand.gmorscrm.room.record.Sample;
import tw.com.masterhand.gmorscrm.room.record.Trip;

public class SampleList extends Sample {
    SampleFile default_photo;
    SampleTrip trip;

    public SampleFile getDefault_photo() {
        return default_photo;
    }

    public void setDefault_photo(SampleFile default_photo) {
        this.default_photo = default_photo;
    }

    public SampleTrip getTrip() {
        return trip;
    }

    public void setTrip(SampleTrip trip) {
        this.trip = trip;
    }

    public static class SampleTrip extends Trip {
        Customer customer;
        Project project;

        public Customer getCustomer() {
            return customer;
        }

        public void setCustomer(Customer customer) {
            this.customer = customer;
        }

        public Project getProject() {
            return project;
        }

        public void setProject(Project project) {
            this.project = project;
        }
    }
}
