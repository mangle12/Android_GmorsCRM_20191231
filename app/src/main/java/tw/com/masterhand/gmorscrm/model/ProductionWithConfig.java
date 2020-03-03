package tw.com.masterhand.gmorscrm.model;

import java.util.ArrayList;
import java.util.List;

import tw.com.masterhand.gmorscrm.enums.TripType;
import tw.com.masterhand.gmorscrm.room.record.File;
import tw.com.masterhand.gmorscrm.room.record.NewProjectProduction;
import tw.com.masterhand.gmorscrm.room.record.SpecialPrice;
import tw.com.masterhand.gmorscrm.room.record.SpecialPriceProduct;

public class ProductionWithConfig extends BaseTrip {
    NewProjectProduction production;
    List<File> files;

    public ProductionWithConfig(NewProjectProduction production) {
        super();
        trip.setType(TripType.PRODUCTION);
        this.production = production;
        files = new ArrayList<>();
    }

    public NewProjectProduction getProduction() {
        return production;
    }

    public void setProduction(NewProjectProduction production) {
        this.production = production;
    }

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }
}
