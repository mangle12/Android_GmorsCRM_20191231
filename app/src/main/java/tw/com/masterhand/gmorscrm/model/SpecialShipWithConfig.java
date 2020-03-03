package tw.com.masterhand.gmorscrm.model;

import java.util.ArrayList;
import java.util.List;

import tw.com.masterhand.gmorscrm.enums.TripType;
import tw.com.masterhand.gmorscrm.room.record.Contacter;
import tw.com.masterhand.gmorscrm.room.record.Express;
import tw.com.masterhand.gmorscrm.room.record.File;
import tw.com.masterhand.gmorscrm.room.record.SpecialShip;

public class SpecialShipWithConfig extends BaseTrip {
    SpecialShip specialShip;
    List<File> files;

    public SpecialShipWithConfig(SpecialShip specialShip) {
        super();
        trip.setType(TripType.SPECIAL_SHIP);
        this.specialShip = specialShip;
        files = new ArrayList<>();
    }

    public SpecialShip getSpecialShip() {
        return specialShip;
    }

    public void setSpecialShip(SpecialShip specialShip) {
        this.specialShip = specialShip;
    }

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }
}
