package tw.com.masterhand.gmorscrm.model;

import java.util.ArrayList;
import java.util.List;

import tw.com.masterhand.gmorscrm.enums.TripType;
import tw.com.masterhand.gmorscrm.room.record.Contacter;
import tw.com.masterhand.gmorscrm.room.record.File;
import tw.com.masterhand.gmorscrm.room.record.Participant;
import tw.com.masterhand.gmorscrm.room.record.Report;
import tw.com.masterhand.gmorscrm.room.record.Visit;

public class VisitWithConfig extends BaseTrip {
    Visit visit;
    Report report;
    List<File> files;
    List<Participant> participants;
    List<Contacter> contacters;

    public VisitWithConfig() {
        super();
        trip.setType(TripType.VISIT);
        participants = new ArrayList<>();
        contacters = new ArrayList<>();
        files = new ArrayList<>();
        report = new Report();
    }

    public Visit getVisit() {
        return visit;
    }

    public void setVisit(Visit visit) {
        this.visit = visit;
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

    public List<Contacter> getContacters() {
        return contacters;
    }

    public void setContacters(List<Contacter> contacters) {
        this.contacters = contacters;
    }

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        if (report == null)
            report = new Report();
        this.report = report;
    }
}
