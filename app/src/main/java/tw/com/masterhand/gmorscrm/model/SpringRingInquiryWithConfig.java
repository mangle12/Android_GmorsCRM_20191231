package tw.com.masterhand.gmorscrm.model;

import java.util.ArrayList;
import java.util.List;

import tw.com.masterhand.gmorscrm.enums.TripType;
import tw.com.masterhand.gmorscrm.room.record.File;
import tw.com.masterhand.gmorscrm.room.record.SpringRingInquiry;
import tw.com.masterhand.gmorscrm.room.record.SpringRingInquiryProduct;

public class SpringRingInquiryWithConfig extends BaseTrip {
    SpringRingInquiry inquiry;
    List<File> files;
    List<SpringRingInquiryProduct> products;

    public SpringRingInquiryWithConfig(SpringRingInquiry inquiry) {
        super();
        trip.setType(TripType.SPRING_RING_INQUIRY);
        this.inquiry = inquiry;
        products = new ArrayList<>();
        files = new ArrayList<>();
    }

    public SpringRingInquiry getInquiry() {
        return inquiry;
    }

    public void setInquiry(SpringRingInquiry inquiry) {
        this.inquiry = inquiry;
    }

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }

    public List<SpringRingInquiryProduct> getProducts() {
        return products;
    }

    public void setProducts(List<SpringRingInquiryProduct> products) {
        this.products = products;
    }
}
