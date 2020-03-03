package tw.com.masterhand.gmorscrm.model;

import java.util.ArrayList;
import java.util.List;

import tw.com.masterhand.gmorscrm.enums.TripType;
import tw.com.masterhand.gmorscrm.room.record.File;
import tw.com.masterhand.gmorscrm.room.record.NewProjectProduction;
import tw.com.masterhand.gmorscrm.room.record.Participant;
import tw.com.masterhand.gmorscrm.room.record.Travel;

public class TravelWithConfig extends BaseTrip {
    Travel travel;
    List<File> files;
    List<Participant> participants;

    public TravelWithConfig(Travel travel) {
        super();
        trip.setType(TripType.TRAVEL);
        this.travel = travel;
        files = new ArrayList<>();
        participants = new ArrayList<>();
    }

    public Travel getTravel() {
        return travel;
    }

    public void setTravel(Travel travel) {
        this.travel = travel;
    }

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Participant> participants) {
        this.participants = participants;
    }
}
