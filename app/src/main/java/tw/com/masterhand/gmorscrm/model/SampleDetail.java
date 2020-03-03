package tw.com.masterhand.gmorscrm.model;

import java.util.List;

import tw.com.masterhand.gmorscrm.room.record.Customer;
import tw.com.masterhand.gmorscrm.room.record.Project;
import tw.com.masterhand.gmorscrm.room.record.Sample;
import tw.com.masterhand.gmorscrm.room.record.Trip;

public class SampleDetail extends Sample {
    List<SampleFile> files;
    SampleTrip trip;

    public void setFiles(List<SampleFile> files) {
        this.files = files;
    }

    public List<SampleFile> getFiles() {
        return files;
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
