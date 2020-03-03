package tw.com.masterhand.gmorscrm.model;

import java.util.ArrayList;
import java.util.List;

import tw.com.masterhand.gmorscrm.room.record.Absent;
import tw.com.masterhand.gmorscrm.room.record.ContactPerson;
import tw.com.masterhand.gmorscrm.room.record.Contacter;
import tw.com.masterhand.gmorscrm.room.record.Contract;
import tw.com.masterhand.gmorscrm.room.record.Customer;
import tw.com.masterhand.gmorscrm.room.record.Express;
import tw.com.masterhand.gmorscrm.room.record.File;
import tw.com.masterhand.gmorscrm.room.record.FilePivot;
import tw.com.masterhand.gmorscrm.room.record.MonthReport;
import tw.com.masterhand.gmorscrm.room.record.NewProjectProduction;
import tw.com.masterhand.gmorscrm.room.record.NonStandardInquiry;
import tw.com.masterhand.gmorscrm.room.record.NonStandardInquiryProduct;
import tw.com.masterhand.gmorscrm.room.record.Office;
import tw.com.masterhand.gmorscrm.room.record.Participant;
import tw.com.masterhand.gmorscrm.room.record.Project;
import tw.com.masterhand.gmorscrm.room.record.Quotation;
import tw.com.masterhand.gmorscrm.room.record.QuotationProduct;
import tw.com.masterhand.gmorscrm.room.record.Reimbursement;
import tw.com.masterhand.gmorscrm.room.record.ReimbursementItem;
import tw.com.masterhand.gmorscrm.room.record.Repayment;
import tw.com.masterhand.gmorscrm.room.record.Report;
import tw.com.masterhand.gmorscrm.room.record.SalesOpportunity;
import tw.com.masterhand.gmorscrm.room.record.SalesOpportunitySub;
import tw.com.masterhand.gmorscrm.room.record.Sample;
import tw.com.masterhand.gmorscrm.room.record.SampleProduct;
import tw.com.masterhand.gmorscrm.room.record.SpecialPrice;
import tw.com.masterhand.gmorscrm.room.record.SpecialPriceProduct;
import tw.com.masterhand.gmorscrm.room.record.SpecialShip;
import tw.com.masterhand.gmorscrm.room.record.SpringRingInquiry;
import tw.com.masterhand.gmorscrm.room.record.SpringRingInquiryProduct;
import tw.com.masterhand.gmorscrm.room.record.Task;
import tw.com.masterhand.gmorscrm.room.record.TaskConversation;
import tw.com.masterhand.gmorscrm.room.record.TaskMessage;
import tw.com.masterhand.gmorscrm.room.record.Travel;
import tw.com.masterhand.gmorscrm.room.record.Trip;
import tw.com.masterhand.gmorscrm.room.record.TripStatusLog;
import tw.com.masterhand.gmorscrm.room.record.Visit;
import tw.com.masterhand.gmorscrm.room.setting.User;

public class SyncRecord {
    List<Absent> Absent;
    List<Project> Project;
    List<Contacter> Contacter;
    List<Participant> Participant;
    List<Contract> Contract;
    List<Visit> Visit;
    List<Office> Office;
    List<Quotation> Quotation;
    List<QuotationProduct> QuotationProduct;
    List<Reimbursement> Reimbursement;
    List<ReimbursementItem> ReimbursementItem;
    List<SalesOpportunity> SalesOpportunity;
    List<SalesOpportunitySub> SalesOpportunitySub;
    List<Sample> Sample;
    List<SampleProduct> SampleProduct;
    List<Task> Task;
    List<TaskConversation> TaskConversation;
    List<TaskMessage> TaskMessage;
    List<Trip> Trip;
    List<TripStatusLog> TripStatusLog;
    //    List<UserQuickMenu> UserQuickMenu;
    List<ContactPerson> ContactPerson;
    List<Customer> Customer;
    List<User> User;
    List<File> File;
    List<FilePivot> FilePivot;
    List<MonthReport> MonthReport;
    List<Express> Express;
    List<NewProjectProduction> NewProjectProduction;
    List<SpecialPrice> SpecialPrice;
    List<SpecialPriceProduct> SpecialPriceProduct;
    List<Travel> Travel;
    List<NonStandardInquiry> NonStandardInquiry;
    List<NonStandardInquiryProduct> NonStandardInquiryProduct;
    List<SpringRingInquiry> SpringRingInquiry;
    List<SpringRingInquiryProduct> SpringRingInquiryProduct;
    List<SpecialShip> SpecialShip;
    List<Repayment> Repayment;
    List<Report> Report;


    public SyncRecord() {
        User = new ArrayList<>();
        File = new ArrayList<>();
    }

    public int getFileCount() {
        return File.size();
    }

    public int getDataCount() {
        return Absent.size() +
                Project.size() +
                Contacter.size() +
                Participant.size() +
                Contract.size() +
                Visit.size() +
                Office.size() +
                Quotation.size() +
                QuotationProduct.size() +
                Reimbursement.size() +
                ReimbursementItem.size() +
                SalesOpportunity.size() +
                SalesOpportunitySub.size() +
                Sample.size() +
                SampleProduct.size() +
                Task.size() +
                TaskConversation.size() +
                TaskMessage.size() +
                Trip.size() +
                TripStatusLog.size() +
//                UserQuickMenu.size() +
                ContactPerson.size() +
                MonthReport.size() +
                Express.size() +
                NewProjectProduction.size() +
                SpecialPrice.size() +
                SpecialPriceProduct.size() +
                Travel.size() +
                NonStandardInquiry.size() +
                NonStandardInquiryProduct.size() +
                SpringRingInquiry.size() +
                SpringRingInquiryProduct.size() +
                SpecialShip.size() +
                Repayment.size() +
                Report.size() +
                Customer.size();
    }

    public List<Absent> getAbsent() {
        return Absent;
    }

    public void setAbsent(List<Absent> absent) {
        Absent = absent;
    }

    public List<Project> getProject() {
        return Project;
    }

    public void setProject(List<Project> project) {
        Project = project;
    }

    public List<Contacter> getContacter() {
        return Contacter;
    }

    public void setContacter(List<Contacter> contacter) {
        Contacter = contacter;
    }

    public List<Participant> getParticipant() {
        return Participant;
    }

    public void setParticipant(List<Participant>
                                       participant) {
        Participant = participant;
    }

    public List<Contract> getContract() {
        return Contract;
    }

    public void setContract(List<Contract> contract) {
        Contract = contract;
    }

    public List<Visit> getVisit() {
        return Visit;
    }

    public void setVisit(List<Visit> visit) {
        Visit = visit;
    }

    public List<Office> getOffice() {
        return Office;
    }

    public void setOffice(List<Office> office) {
        Office = office;
    }

    public List<Quotation> getQuotation() {
        return Quotation;
    }

    public void setQuotation(List<Quotation> quotation) {
        Quotation = quotation;
    }

    public List<QuotationProduct> getQuotationProduct() {
        return QuotationProduct;
    }

    public void setQuotationProduct(List<QuotationProduct>
                                            quotationProduct) {
        QuotationProduct = quotationProduct;
    }

    public List<Reimbursement> getReimbursement() {
        return Reimbursement;
    }

    public void setReimbursement(List<Reimbursement>
                                         reimbursement) {
        Reimbursement = reimbursement;
    }

    public List<ReimbursementItem> getReimbursementItem() {
        return ReimbursementItem;
    }

    public void setReimbursementItem(List<tw.com.masterhand.gmorscrm.room.record
            .ReimbursementItem> reimbursementItem) {
        ReimbursementItem = reimbursementItem;
    }

    public List<SalesOpportunity> getSalesOpportunity() {
        return SalesOpportunity;
    }

    public void setSalesOpportunity(List<SalesOpportunity>
                                            salesOpportunity) {
        SalesOpportunity = salesOpportunity;
    }

    public List<SalesOpportunitySub> getSalesOpportunitySub() {
        return SalesOpportunitySub;
    }

    public void setSalesOpportunitySub(List<SalesOpportunitySub>
                                               salesOpportunitySub) {
        SalesOpportunitySub = salesOpportunitySub;
    }

    public List<Sample> getSample() {
        return Sample;
    }

    public void setSample(List<Sample> sample) {
        Sample = sample;
    }

    public List<SampleProduct> getSampleProduct() {
        return SampleProduct;
    }

    public void setSampleProduct(List<SampleProduct>
                                         sampleProduct) {
        SampleProduct = sampleProduct;
    }

    public List<Task> getTask() {
        return Task;
    }

    public void setTask(List<Task> task) {
        Task = task;
    }

    public List<TaskConversation> getTaskConversation() {
        return TaskConversation;
    }

    public void setTaskConversation(List<TaskConversation>
                                            taskConversation) {
        TaskConversation = taskConversation;
    }

    public List<TaskMessage> getTaskMessage() {
        return TaskMessage;
    }

    public void setTaskMessage(List<TaskMessage>
                                       taskMessage) {
        TaskMessage = taskMessage;
    }

    public List<Trip> getTrip() {
        return Trip;
    }

    public void setTrip(List<Trip> trip) {
        Trip = trip;
    }

    public List<TripStatusLog> getTripStatusLog() {
        return TripStatusLog;
    }

    public void setTripStatusLog(List<TripStatusLog>
                                         tripStatusLog) {
        TripStatusLog = tripStatusLog;
    }

    public List<Customer> getCustomer() {
        return Customer;
    }

    public void setCustomer(List<Customer> customer) {
        Customer = customer;
    }

    public List<ContactPerson> getContactPerson() {
        return ContactPerson;
    }

    public void setContactPerson(List<ContactPerson>
                                         contactPerson) {
        ContactPerson = contactPerson;
    }

//    public List<tw.com.masterhand.gmorscrm.room.record.UserQuickMenu> getUserQuickMenu() {
//        return UserQuickMenu;
//    }
//
//    public void setUserQuickMenu(List<tw.com.masterhand.gmorscrm.room.record.UserQuickMenu>
//                                         userQuickMenu) {
//        UserQuickMenu = userQuickMenu;
//    }

    public List<User> getUser() {
        return User;
    }

    public void setUser(List<User> user) {
        User = user;
    }

    public List<File> getFile() {
        return File;
    }

    public void setFile(List<File> file) {
        File = file;
    }

    public void addFile(File file) {
        File.add(file);
    }

    public List<FilePivot> getFilePivot() {
        return FilePivot;
    }

    public void setFilePivot(List<FilePivot> filePivot) {
        FilePivot = filePivot;
    }

    public List<tw.com.masterhand.gmorscrm.room.record.MonthReport> getMonthReport() {
        return MonthReport;
    }

    public void setMonthReport(List<tw.com.masterhand.gmorscrm.room.record.MonthReport>
                                       monthReport) {
        MonthReport = monthReport;
    }

    public List<tw.com.masterhand.gmorscrm.room.record.Express> getExpress() {
        return Express;
    }

    public void setExpress(List<tw.com.masterhand.gmorscrm.room.record.Express> express) {
        Express = express;
    }

    public List<tw.com.masterhand.gmorscrm.room.record.NewProjectProduction>
    getNewProjectProduction() {
        return NewProjectProduction;
    }

    public void setNewProjectProduction(List<tw.com.masterhand.gmorscrm.room.record
            .NewProjectProduction> newProjectProduction) {
        NewProjectProduction = newProjectProduction;
    }

    public List<tw.com.masterhand.gmorscrm.room.record.SpecialPrice> getSpecialPrice() {
        return SpecialPrice;
    }

    public void setSpecialPrice(List<tw.com.masterhand.gmorscrm.room.record.SpecialPrice>
                                        specialPrice) {
        SpecialPrice = specialPrice;
    }

    public List<tw.com.masterhand.gmorscrm.room.record.SpecialPriceProduct>
    getSpecialPriceProduct() {
        return SpecialPriceProduct;
    }

    public void setSpecialPriceProduct(List<tw.com.masterhand.gmorscrm.room.record
            .SpecialPriceProduct> specialPriceProduct) {
        SpecialPriceProduct = specialPriceProduct;
    }

    public List<tw.com.masterhand.gmorscrm.room.record.Travel> getTravel() {
        return Travel;
    }

    public void setTravel(List<tw.com.masterhand.gmorscrm.room.record.Travel> travel) {
        Travel = travel;
    }

    public List<tw.com.masterhand.gmorscrm.room.record.NonStandardInquiry> getNonStandardInquiry() {
        return NonStandardInquiry;
    }

    public void setNonStandardInquiry(List<tw.com.masterhand.gmorscrm.room.record
            .NonStandardInquiry> nonStandardInquiry) {
        NonStandardInquiry = nonStandardInquiry;
    }

    public List<tw.com.masterhand.gmorscrm.room.record.NonStandardInquiryProduct>
    getNonStandardInquiryProduct() {
        return NonStandardInquiryProduct;
    }

    public void setNonStandardInquiryProduct(List<tw.com.masterhand.gmorscrm.room.record
            .NonStandardInquiryProduct> nonStandardInquiryProduct) {
        NonStandardInquiryProduct = nonStandardInquiryProduct;
    }

    public List<tw.com.masterhand.gmorscrm.room.record.SpringRingInquiry> getSpringRingInquiry() {
        return SpringRingInquiry;
    }

    public void setSpringRingInquiry(List<tw.com.masterhand.gmorscrm.room.record
            .SpringRingInquiry> springRingInquiry) {
        SpringRingInquiry = springRingInquiry;
    }

    public List<tw.com.masterhand.gmorscrm.room.record.SpringRingInquiryProduct>
    getSpringRingInquiryProduct() {
        return SpringRingInquiryProduct;
    }

    public void setSpringRingInquiryProduct(List<tw.com.masterhand.gmorscrm.room.record
            .SpringRingInquiryProduct> springRingInquiryProduct) {
        SpringRingInquiryProduct = springRingInquiryProduct;
    }

    public List<tw.com.masterhand.gmorscrm.room.record.SpecialShip> getSpecialShip() {
        return SpecialShip;
    }

    public void setSpecialShip(List<tw.com.masterhand.gmorscrm.room.record.SpecialShip>
                                       specialShip) {
        SpecialShip = specialShip;
    }

    public List<tw.com.masterhand.gmorscrm.room.record.Repayment> getRepayment() {
        return Repayment;
    }

    public void setRepayment(List<tw.com.masterhand.gmorscrm.room.record.Repayment> repayment) {
        Repayment = repayment;
    }

    public List<tw.com.masterhand.gmorscrm.room.record.Report> getReport() {
        return Report;
    }

    public void setReport(List<tw.com.masterhand.gmorscrm.room.record.Report> report) {
        Report = report;
    }
}
