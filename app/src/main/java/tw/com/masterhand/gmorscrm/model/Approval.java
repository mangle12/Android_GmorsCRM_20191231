package tw.com.masterhand.gmorscrm.model;

import java.util.Date;
import java.util.List;

import tw.com.masterhand.gmorscrm.enums.ApprovalStatus;
import tw.com.masterhand.gmorscrm.enums.TripStatus;
import tw.com.masterhand.gmorscrm.enums.TripType;
import tw.com.masterhand.gmorscrm.room.record.Absent;
import tw.com.masterhand.gmorscrm.room.record.Contract;
import tw.com.masterhand.gmorscrm.room.record.Customer;
import tw.com.masterhand.gmorscrm.room.record.Express;
import tw.com.masterhand.gmorscrm.room.record.NewProjectProduction;
import tw.com.masterhand.gmorscrm.room.record.NonStandardInquiry;
import tw.com.masterhand.gmorscrm.room.record.Quotation;
import tw.com.masterhand.gmorscrm.room.record.Reimbursement;
import tw.com.masterhand.gmorscrm.room.record.Repayment;
import tw.com.masterhand.gmorscrm.room.record.Sample;
import tw.com.masterhand.gmorscrm.room.record.SpecialPrice;
import tw.com.masterhand.gmorscrm.room.record.SpecialShip;
import tw.com.masterhand.gmorscrm.room.record.SpringRingInquiry;
import tw.com.masterhand.gmorscrm.room.record.Travel;
import tw.com.masterhand.gmorscrm.room.record.Trip;

public class Approval {
    public String id;

    public Date created_at;
    public Date updated_at;
    public Date deleted_at;

    public String project_id;// 工作項目ID
    public String customer_id;// 客戶ID
    public int status;// 目前狀態 1:正常 2:延後 3:取消
    public String name;// 名稱
    public int type;// 種類 1:拜訪 2:內勤 3:任務 4:請假 5: 報價 6:合約 7:報銷單 8:樣品
    public int approval;// 審批狀態 0:未審批 2:已通過 3:未通過
    public Date from_date;// 開始
    public Date to_date;// 結束
    public int date_type;// 是否為全天 1:是 0:否
    public int notification;// 提示 -1:不提示 >1分鐘數
    public int clock_id;// 鬧鐘ID
    public String reason;// 目前取消/延後原因
    public String description;// 備註
    public String user_id;// 使用者ID
    public String assistant_id;
    public int approval_stage;// 已通過的關卡數
    public int approval_required_stage;// 總關卡數

    public int approval_resend;// 是否為重新審批 1:是 0:否

    public List<Approve> trip_approvals;

    Absent absent;
    Reimbursement reimbursement;
    Contract contract;
    Quotation quotation;
    Sample sample;
    SpecialPrice special_price;
    NewProjectProduction new_project_production;
    NonStandardInquiry non_standard_inquiry;
    SpringRingInquiry spring_ring_inquiry;
    Express express;
    Travel travel;
    Repayment repayment;
    SpecialShip special_ship;


    Customer customer;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public Date getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }

    public Date getDeleted_at() {
        return deleted_at;
    }

    public void setDeleted_at(Date deleted_at) {
        this.deleted_at = deleted_at;
    }

    public String getProject_id() {
        return project_id;
    }

    public void setProject_id(String project_id) {
        this.project_id = project_id;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public TripStatus getStatus() {
        return TripStatus.getTripTypeByValue(status);
    }

    public void setStatus(TripStatus status) {
        this.status = status.getValue();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TripType getType() {
        return TripType.getTripTypeByValue(type);
    }

    public void setType(TripType type) {
        this.type = type.getValue();
    }

    public ApprovalStatus getApproval() {
        return ApprovalStatus.getStatusByCode(approval);
    }

    public void setApproval(ApprovalStatus approval) {
        this.approval = approval.getCode();
    }

    public Date getFrom_date() {
        return from_date;
    }

    public void setFrom_date(Date from_date) {
        this.from_date = from_date;
    }

    public Date getTo_date() {
        return to_date;
    }

    public void setTo_date(Date to_date) {
        this.to_date = to_date;
    }

    public boolean getDate_type() {
        return date_type == 1;
    }

    public void setDate_type(boolean date_type) {
        this.date_type = date_type ? 1 : 0;
    }

    public int getNotification() {
        return notification * 1000 * 60;
    }

    public void setNotification(int notification) {
        this.notification = notification / 1000 / 60;
    }

    public int getClock_id() {
        return clock_id;
    }

    public void setClock_id(int clock_id) {
        this.clock_id = clock_id;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getAssistant_id() {
        return assistant_id;
    }

    public void setAssistant_id(String assistant_id) {
        this.assistant_id = assistant_id;
    }

    public Absent getAbsent() {
        return absent;
    }

    public void setAbsent(Absent absent) {
        this.absent = absent;
    }

    public Reimbursement getReimbursement() {
        return reimbursement;
    }

    public void setReimbursement(Reimbursement reimbursement) {
        this.reimbursement = reimbursement;
    }

    public Contract getContract() {
        return contract;
    }

    public void setContract(Contract contract) {
        this.contract = contract;
    }

    public Quotation getQuotation() {
        return quotation;
    }

    public void setQuotation(Quotation quotation) {
        this.quotation = quotation;
    }

    public Sample getSample() {
        return sample;
    }

    public void setSample(Sample sample) {
        this.sample = sample;
    }

    public SpecialPrice getSpecialPrice() {
        return special_price;
    }

    public void setSpecialPrice(SpecialPrice specialPrice) {
        this.special_price = specialPrice;
    }

    public NewProjectProduction getProduction() {
        return new_project_production;
    }

    public void setProduction(NewProjectProduction production) {
        this.new_project_production = production;
    }

    public NonStandardInquiry getNonStandardInquiry() {
        return non_standard_inquiry;
    }

    public void setNonStandardInquiry(NonStandardInquiry nonStandardInquiry) {
        this.non_standard_inquiry = nonStandardInquiry;
    }

    public SpringRingInquiry getSpringRingInquiry() {
        return spring_ring_inquiry;
    }

    public void setSpringRingInquiry(SpringRingInquiry springRingInquiry) {
        this.spring_ring_inquiry = springRingInquiry;
    }

    public Express getExpress() {
        return express;
    }

    public void setExpress(Express express) {
        this.express = express;
    }

    public Travel getTravel() {
        return travel;
    }

    public void setTravel(Travel travel) {
        this.travel = travel;
    }

    public Repayment getRepayment() {
        return repayment;
    }

    public void setRepayment(Repayment repayment) {
        this.repayment = repayment;
    }

    public SpecialShip getSpecialShip() {
        return special_ship;
    }

    public void setSpecialShip(SpecialShip specialShip) {
        this.special_ship = specialShip;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public boolean getApproval_resend() {
        return approval_resend == 1;
    }

    public Approve getApprove() {
        Approve approve = new Approve();
        switch (getType()) {
            case ABSENT:
                approve.setTrip_id(getAbsent().getTrip_id());
                break;
            case QUOTATION:
                approve.setTrip_id(getQuotation().getTrip_id());
                break;
            case SAMPLE:
                approve.setTrip_id(getSample().getTrip_id());
                break;
            case CONTRACT:
                approve.setTrip_id(getContract().getTrip_id());
                break;
            case REIMBURSEMENT:
                approve.setTrip_id(getReimbursement().getTrip_id());
                break;
            case SPECIAL_PRICE:
                approve.setTrip_id(getSpecialPrice().getTrip_id());
                break;
            case PRODUCTION:
                approve.setTrip_id(getProduction().getTrip_id());
                break;
            case NON_STANDARD_INQUIRY:
                approve.setTrip_id(getNonStandardInquiry().getTrip_id());
                break;
            case SPRING_RING_INQUIRY:
                approve.setTrip_id(getSpringRingInquiry().getTrip_id());
                break;
            case EXPRESS:
                approve.setTrip_id(getExpress().getTrip_id());
                break;
            case TRAVEL:
                approve.setTrip_id(getTravel().getTrip_id());
                break;
            case REPAYMENT:
                approve.setTrip_id(getReimbursement().getTrip_id());
                break;
            case SPECIAL_SHIP:
                approve.setTrip_id(getSpecialShip().getTrip_id());
                break;

        }
        return approve;
    }

    public List<Approve> getTrip_approvals() {
        return trip_approvals;
    }

    public Trip getTrip() {
        Trip trip = new Trip();
        trip.setUser_id(getUser_id());
        trip.setId(getApprove().getTrip_id());
        trip.setName(getName());
        trip.setDate_type(getDate_type());
        trip.setTo_date(getTo_date());
        trip.setFrom_date(getFrom_date());
        trip.setAssistant_id(getAssistant_id());
        trip.setDescription(getDescription());
        trip.setCustomer_id(getCustomer_id());
        trip.setProject_id(getProject_id());
        return trip;
    }
}
