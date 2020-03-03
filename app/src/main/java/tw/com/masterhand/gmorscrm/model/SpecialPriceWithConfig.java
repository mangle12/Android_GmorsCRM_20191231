package tw.com.masterhand.gmorscrm.model;

import java.util.ArrayList;
import java.util.List;

import tw.com.masterhand.gmorscrm.enums.TripType;
import tw.com.masterhand.gmorscrm.room.record.Contacter;
import tw.com.masterhand.gmorscrm.room.record.File;
import tw.com.masterhand.gmorscrm.room.record.Quotation;
import tw.com.masterhand.gmorscrm.room.record.QuotationProduct;
import tw.com.masterhand.gmorscrm.room.record.SpecialPrice;
import tw.com.masterhand.gmorscrm.room.record.SpecialPriceProduct;

public class SpecialPriceWithConfig extends BaseTrip {
    SpecialPrice specialPrice;
    List<File> files;
    List<SpecialPriceProduct> products;

    public SpecialPriceWithConfig(SpecialPrice specialPrice) {
        super();
        trip.setType(TripType.SPECIAL_PRICE);
        this.specialPrice = specialPrice;
        products = new ArrayList<>();
        files = new ArrayList<>();
    }

    public SpecialPrice getSpecialPrice() {
        return specialPrice;
    }

    public void setSpecialPrice(SpecialPrice specialPrice) {
        this.specialPrice = specialPrice;
    }

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }

    public List<SpecialPriceProduct> getProducts() {
        return products;
    }

    public void setProducts(List<SpecialPriceProduct> products) {
        this.products = products;
    }
}
