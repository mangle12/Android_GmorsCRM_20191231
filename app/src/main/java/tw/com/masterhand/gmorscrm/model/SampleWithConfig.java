package tw.com.masterhand.gmorscrm.model;

import java.util.ArrayList;
import java.util.List;

import tw.com.masterhand.gmorscrm.enums.TripType;
import tw.com.masterhand.gmorscrm.room.record.Contacter;
import tw.com.masterhand.gmorscrm.room.record.File;
import tw.com.masterhand.gmorscrm.room.record.Sample;
import tw.com.masterhand.gmorscrm.room.record.SampleProduct;

public class SampleWithConfig extends BaseTrip {
    Sample sample;
    List<File> files;
    List<Contacter> contacters;
    List<SampleProductWithConfig> products;

    public SampleWithConfig() {
        super();
        trip.setType(TripType.SAMPLE);
        contacters = new ArrayList<>();
        products = new ArrayList<>();
        files = new ArrayList<>();
    }

    public Sample getSample() {
        return sample;
    }

    public void setSample(Sample sample) {
        this.sample = sample;
    }

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }

    public List<Contacter> getContacters() {
        return contacters;
    }

    public void setContacters(List<Contacter> contacters) {
        this.contacters = contacters;
    }

    public List<SampleProductWithConfig> getProducts() {
        return products;
    }

    public void setProducts(List<SampleProductWithConfig> products) {
        this.products = products;
    }
}
