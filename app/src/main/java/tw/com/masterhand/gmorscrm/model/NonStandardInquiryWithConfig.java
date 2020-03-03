package tw.com.masterhand.gmorscrm.model;

import java.util.ArrayList;
import java.util.List;

import tw.com.masterhand.gmorscrm.enums.TripType;
import tw.com.masterhand.gmorscrm.room.record.File;
import tw.com.masterhand.gmorscrm.room.record.NonStandardInquiry;
import tw.com.masterhand.gmorscrm.room.record.NonStandardInquiryProduct;
import tw.com.masterhand.gmorscrm.room.record.SpecialPrice;
import tw.com.masterhand.gmorscrm.room.record.SpecialPriceProduct;

public class NonStandardInquiryWithConfig extends BaseTrip {
    NonStandardInquiry inquiry;
    List<File> files;
    List<NonStandardInquiryProduct> products;

    public NonStandardInquiryWithConfig(NonStandardInquiry inquiry) {
        super();
        trip.setType(TripType.NON_STANDARD_INQUIRY);
        this.inquiry = inquiry;
        products = new ArrayList<>();
        files = new ArrayList<>();
    }

    public NonStandardInquiry getInquiry() {
        return inquiry;
    }

    public void setInquiry(NonStandardInquiry inquiry) {
        this.inquiry = inquiry;
    }

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }

    public List<NonStandardInquiryProduct> getProducts() {
        return products;
    }

    public void setProducts(List<NonStandardInquiryProduct> products) {
        this.products = products;
    }
}
