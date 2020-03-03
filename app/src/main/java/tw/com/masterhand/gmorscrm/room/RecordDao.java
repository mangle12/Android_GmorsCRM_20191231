package tw.com.masterhand.gmorscrm.room;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.Date;
import java.util.List;

import tw.com.masterhand.gmorscrm.room.local.SyncLog;
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
import tw.com.masterhand.gmorscrm.room.record.UserQuickMenu;
import tw.com.masterhand.gmorscrm.room.record.Visit;
import tw.com.masterhand.gmorscrm.room.setting.AuthorityCustomer;
import tw.com.masterhand.gmorscrm.room.setting.User;

@Dao
public interface RecordDao {
    /*------------------------------行程-------------------------------------*/
    @Query("SELECT * from Trip where deleted_at IS NULL and type IN (:types) and (name LIKE " +
            ":keyword or description LIKE :keyword) and from_date <= :end and to_date >= :start " +
            "order by updated_at desc")
    public List<Trip> searchTrip(List<Integer> types, String keyword, Date start, Date end);

    @Query("SELECT * from Trip where type IN (:types) and deleted_at IS NULL and from_date <= " +
            ":end and to_date >= :start and user_id IS NOT NULL order by " +
            "updated_at asc")
    public List<Trip> getTripByDateAndType(List<Integer> types, Date start, Date end);

    @Query("SELECT * from Trip where deleted_at IS NULL and from_date <= " +
            ":end and to_date >= :start order by " +
            "updated_at asc")
    public List<Trip> getTripByDate(Date start, Date end);

    @Query("SELECT Trip.* from Trip,User where Trip.deleted_at IS NULL and Trip.from_date <= " +
            ":end and Trip.to_date >= :start and Trip.user_id = :userId and Trip.user_id = User.id " +
            "order by Trip.updated_at asc")
    public List<Trip> getTripByDateAndUser(Date start, Date end, String userId);

    @Query("SELECT Trip.* from Trip,User,Participant where Trip.deleted_at IS NULL and Trip.from_date <= " +
            ":end and Trip.to_date >= :start and Trip.id = Participant.trip_id and Participant.user_id = :userId " +
            "and Participant.user_id = User.id order by Trip.updated_at asc")
    public List<Trip> getTripByDateAndParticipant(Date start, Date end, String userId);

    @Query("SELECT Trip.* from Trip,User where Trip.deleted_at IS NULL and Trip.from_date <= " +
            ":end and Trip.to_date >= :start and Trip.user_id = User.id and User.department_id = :departmentId " +
            "order by Trip.updated_at asc")
    public List<Trip> getTripByDateAndUserDepartment(Date start, Date end, String departmentId);

    @Query("SELECT Trip.* from Trip,User,Participant where Trip.deleted_at IS NULL and Trip.from_date <= " +
            ":end and Trip.to_date >= :start and Trip.id = Participant.trip_id and Participant.user_id = User.id " +
            "and User.department_id = :departmentId order by Trip.updated_at asc")
    public List<Trip> getTripByDateAndParticipantDepartment(Date start, Date end, String departmentId);

    @Query("SELECT Trip.* from Trip,User where Trip.deleted_at IS NULL and Trip.from_date <= " +
            ":end and Trip.to_date >= :start and Trip.user_id = User.id and User.company_id = :companyId " +
            "order by Trip.updated_at asc")
    public List<Trip> getTripByDateAndUserCompany(Date start, Date end, String companyId);

    @Query("SELECT Trip.* from Trip,User,Participant where Trip.deleted_at IS NULL and Trip.from_date <= " +
            ":end and Trip.to_date >= :start and Trip.id = Participant.trip_id and Participant.user_id = User.id " +
            "and User.company_id = :companyId order by Trip.updated_at asc")
    public List<Trip> getTripByDateAndParticipantCompany(Date start, Date end, String companyId);

    @Query("SELECT * from Trip where customer_id = :customerId and deleted_at IS NULL order by " +
            "updated_at desc")
    public List<Trip> getTripByCustomer(String customerId);

    @Query("SELECT * from Trip where project_id = :projectId and deleted_at IS NULL order by " +
            "updated_at desc")
    public List<Trip> getTripByProject(String projectId);

    @Query("SELECT * from Trip where submit_status = 0 and type IN (:types) and user_id = :userId" +
            " and deleted_at IS " +
            "NULL order by updated_at desc")
    public List<Trip> getNotSubmittedTrip(List<Integer> types, String userId);

    @Query("SELECT * from Trip where id = :tripId and " +
            "deleted_at IS NULL LIMIT 1")
    public Trip getTripById(String tripId);

    @Query("SELECT clock_id from Trip where deleted_at IS NULL order by clock_id desc LIMIT 1")
    public Integer getLastClockId();

    @Query("SELECT * from TripStatusLog where id = :id AND deleted_at IS NULL LIMIT 1")
    public TripStatusLog getTripStatusLogById(String id);

    /*------------------------------八大類-------------------------------------*/
    @Query("SELECT * from Visit where id = :id and deleted_at IS NULL LIMIT 1")
    public Visit getVisitById(String id);

    @Query("SELECT * from Visit where trip_id = :tripId and deleted_at IS NULL LIMIT 1")
    public Visit getVisitByTrip(String tripId);

    @Query("SELECT Visit.* from Trip,Visit where Trip.deleted_at IS NULL and Trip.user_id = " +
            ":userId and Trip.from_date <= " +
            ":end and Trip.to_date >= :start and Trip.type = 1 and Visit.trip_id = Trip.id order " +
            "by Trip.updated_at asc")
    public List<Visit> getVisitByDate(Date start, Date end, String userId);

    @Query("SELECT * from Office where id = :id and deleted_at IS NULL LIMIT 1")
    public Office getOfficeById(String id);

    @Query("SELECT * from Office where trip_id = :tripId and deleted_at IS NULL order by " +
            "updated_at desc LIMIT 1")
    public Office getOfficeByTrip(String tripId);

    @Query("SELECT * from Task where id = :id and deleted_at IS NULL LIMIT 1")
    public Task getTaskById(String id);

    @Query("SELECT * from Task where trip_id = :tripId and deleted_at IS NULL order by " +
            "updated_at desc LIMIT 1")
    public Task getTaskByTrip(String tripId);

    @Query("SELECT * from Absent where id = :id and deleted_at IS NULL LIMIT 1")
    public Absent getAbsentById(String id);

    @Query("SELECT * from Absent where trip_id = :tripId and deleted_at IS NULL order by " +
            "updated_at desc LIMIT 1")
    public Absent getAbsentByTrip(String tripId);

    @Query("SELECT Reimbursement.* from Reimbursement,Trip where Reimbursement.trip_id = Trip.id " +
            "and Trip.user_id = :userId and Reimbursement.deleted_at IS NULL order by " +
            "Reimbursement.created_at desc LIMIT 50")
    public List<Reimbursement> getReimbursement(String userId);

    @Query("SELECT * from Reimbursement where id = :id and deleted_at IS NULL LIMIT 1")
    public Reimbursement getReimbursementById(String id);

    @Query("SELECT * from Reimbursement where trip_id = :tripId and deleted_at IS NULL order by " +
            "updated_at desc LIMIT 1")
    public Reimbursement getReimbursementByTrip(String tripId);

    @Query("SELECT * from " +
            "ReimbursementItem where parent_id = :parentId and deleted_at IS NULL order by from_date desc")
    public List<ReimbursementItem> getReimbursementItemByParentId(String parentId);

    @Query("SELECT * from ReimbursementItem where id = :id and deleted_at IS NULL LIMIT 1")
    public ReimbursementItem getReimbursementItemById(String id);

    @Query("SELECT * from Contract where id = :id and deleted_at IS NULL LIMIT 1")
    public Contract getContractById(String id);

    @Query("SELECT * from Contract where trip_id = :tripId and deleted_at IS NULL order by " +
            "updated_at desc LIMIT 1")
    public Contract getContractByTrip(String tripId);

    @Query("SELECT Contract.* from Trip,Contract where Trip.customer_id = :customerId and Trip" +
            ".type = 6 and Trip.id = Contract.trip_id and Contract.deleted_at IS NULL")
    public List<Contract> getContractByCustomer(String customerId);

    @Query("SELECT * from QuotationProduct where parent_id = :contractId and deleted_at IS NULL")
    public List<QuotationProduct> getContractProductByContract(String contractId);

    @Query("SELECT * from Sample where id = :id and deleted_at IS NULL LIMIT 1")
    public Sample getSampleById(String id);

    @Query("SELECT * from Sample where trip_id = :tripId and deleted_at IS NULL order by " +
            "updated_at desc LIMIT 1")
    public Sample getSampleByTrip(String tripId);

    @Query("SELECT * from Sample where deleted_at IS NULL")
    public List<Sample> getSample();

    @Query("SELECT * from SampleProduct where id = :id and deleted_at IS NULL LIMIT 1")
    public SampleProduct getSampleProductById(String id);

    @Query("SELECT * from SampleProduct where parent_id = :sampleId and deleted_at IS NULL order " +
            "by updated_at desc")
    public List<SampleProduct> getSampleProductByParentId(String sampleId);

    @Query("SELECT * from Quotation where id = :id and deleted_at IS NULL LIMIT 1")
    public Quotation getQuotationById(String id);

    @Query("SELECT * from Quotation where trip_id = :tripId and deleted_at IS NULL order by " +
            "updated_at desc LIMIT 1")
    public Quotation getQuotationByTrip(String tripId);

    @Query("SELECT Quotation.* from Trip,Quotation where Trip.customer_id = :customerId and Trip" +
            ".type = 5 and Trip.id = Quotation.trip_id and Quotation.deleted_at IS NULL")
    public List<Quotation> getQuotationByCustomer(String customerId);

    @Query("SELECT * from QuotationProduct where id = :id and deleted_at IS NULL LIMIT 1")
    public QuotationProduct getQuotationProductById(String id);

    @Query("SELECT * from QuotationProduct where parent_id = :quotationId and deleted_at IS NULL")
    public List<QuotationProduct> getQuotationProductByQuotation(String quotationId);

    @Query("SELECT * from SampleProduct where parent_id = :sampleId and deleted_at IS NULL")
    public List<SampleProduct> getSampleProductBySample(String sampleId);

    @Query("SELECT * from Express where trip_id = :tripId and deleted_at IS NULL " +
            "order by updated_at desc LIMIT 1")
    public Express getExpressByTrip(String tripId);

    @Query("SELECT * from Express where id = :id and deleted_at IS NULL LIMIT 1")
    public Express getExpressById(String id);

    @Query("SELECT * from NewProjectProduction where trip_id = :tripId and deleted_at IS NULL " +
            "order by " +
            "updated_at desc LIMIT 1")
    public NewProjectProduction getNewProjectProductionByTrip(String tripId);

    @Query("SELECT * from NewProjectProduction where id = :id and deleted_at IS NULL LIMIT 1")
    public NewProjectProduction getNewProjectProductionById(String id);

    @Query("SELECT * from SpecialPrice where id = :id and deleted_at IS NULL LIMIT 1")
    public SpecialPrice getSpecialPriceById(String id);

    @Query("SELECT * from SpecialPrice where trip_id = :tripId and deleted_at IS NULL order by " +
            "updated_at desc LIMIT 1")
    public SpecialPrice getSpecialPriceByTrip(String tripId);

    @Query("SELECT * from SpecialPriceProduct where id = :id and deleted_at IS NULL LIMIT 1")
    public SpecialPriceProduct getSpecialPriceProductById(String id);

    @Query("SELECT * from SpecialPriceProduct where parent_id = :parentId and deleted_at IS NULL")
    public List<SpecialPriceProduct> getSpecialPriceProductByParent(String parentId);

    @Query("SELECT * from Travel where trip_id = :tripId and deleted_at IS NULL " +
            "order by updated_at desc LIMIT 1")
    public Travel getTravelByTrip(String tripId);

    @Query("SELECT * from Travel where id = :id and deleted_at IS NULL LIMIT 1")
    public Travel getTravelById(String id);

    @Query("SELECT * from NonStandardInquiry where trip_id = :tripId and deleted_at IS NULL order" +
            " by " +
            "updated_at desc LIMIT 1")
    public NonStandardInquiry getNonStandardInquiryByTrip(String tripId);

    @Query("SELECT * from NonStandardInquiry where id = :id and deleted_at IS NULL LIMIT 1")
    public NonStandardInquiry getNonStandardInquiryById(String id);

    @Query("SELECT * from NonStandardInquiryProduct where id = :id and deleted_at IS NULL LIMIT 1")
    public NonStandardInquiryProduct getNonStandardInquiryProductById(String id);

    @Query("SELECT * from NonStandardInquiryProduct where parent_id = :parentId and deleted_at IS" +
            " NULL")
    public List<NonStandardInquiryProduct> getNonStandardInquiryProductByParent(String parentId);

    @Query("SELECT * from SpringRingInquiry where trip_id = :tripId and deleted_at IS NULL order" +
            " by " +
            "updated_at desc LIMIT 1")
    public SpringRingInquiry getSpringRingInquiryByTrip(String tripId);

    @Query("SELECT * from SpringRingInquiry where id = :id and deleted_at IS NULL LIMIT 1")
    public SpringRingInquiry getSpringRingInquiryById(String id);

    @Query("SELECT * from SpringRingInquiryProduct where id = :id and deleted_at IS NULL LIMIT 1")
    public SpringRingInquiryProduct getSpringRingInquiryProductById(String id);

    @Query("SELECT * from SpringRingInquiryProduct where parent_id = :parentId and deleted_at IS" +
            " NULL")
    public List<SpringRingInquiryProduct> getSpringRingInquiryProductByParent(String parentId);

    @Query("SELECT * from SpecialShip where trip_id = :tripId and deleted_at IS NULL " +
            "order by updated_at desc LIMIT 1")
    public SpecialShip getSpecialShipByTrip(String tripId);

    @Query("SELECT * from SpecialShip where id = :id and deleted_at IS NULL LIMIT 1")
    public SpecialShip getSpecialShipById(String id);

    @Query("SELECT * from Repayment where trip_id = :tripId and deleted_at IS NULL " +
            "order by updated_at desc LIMIT 1")
    public Repayment getRepaymentByTrip(String tripId);

    @Query("SELECT * from Repayment where id = :id and deleted_at IS NULL LIMIT 1")
    public Repayment getRepaymentById(String id);


    /*------------------------------參與人-------------------------------------*/
    @Query("SELECT * from Participant where id = :id and " +
            "deleted_at IS NULL LIMIT 1")
    public Participant getParticipantById(String id);

    @Query("SELECT * from Participant where trip_id = :tripId and " +
            "deleted_at IS NULL")
    public List<Participant> getParticipantByTrip(String tripId);

    @Query("SELECT Participant.* from Participant,Project where Project.customer_id = :customerId" +
            " and Participant.parent_id = Project.id and " +
            "Project.deleted_at IS NULL group by Participant.user_id")
    public List<Participant> getParticipantByCustomer(String customerId);

    @Query("SELECT * from Participant where parent_id = :parentId" +
            " and " +
            "deleted_at IS NULL")
    public List<Participant> getParticipantByParent(String parentId);

    @Query("SELECT Trip.* from Participant,Trip where " +
            "Trip.to_date > :today" +
            " and " +
            "Participant.trip_id = Trip.id" +
            " and " +
            "Participant.user_id != Trip.user_id" +
            " and " +
            "Participant.user_id = :userId" +
            " and " +
            "Participant.deleted_at IS NULL")
    public List<Trip> getTripInvite(String userId, Date today);

    /*------------------------------聯絡人-------------------------------------*/
    @Query("SELECT * from ContactPerson where customer_id = :customerId" +
            " and " +
            "deleted_at IS NULL order by updated_at DESC")
    public List<ContactPerson> getContactPersonByCustomer(String customerId);

    @Query("SELECT * from Contacter where id = :id" +
            " and deleted_at IS NULL LIMIT 1")
    public Contacter getContacterById(String id);

    @Query("SELECT * from Contacter where parent_id = :parentId" +
            " and " +
            "deleted_at IS NULL")
    public List<Contacter> getContacterByParent(String parentId);

    /*------------------------------工作項目-------------------------------------*/
    @Query("SELECT * from Project where deleted_at IS NULL order by from_date DESC")
    public List<Project> getProject();

    @Query("SELECT Project.* FROM Project inner join AuthorityProject on AuthorityProject.deleted_at IS NULL and Project.id = AuthorityProject.project_id and AuthorityProject.user_id = :userId " +
            "inner join SalesOpportunity on Project.id = SalesOpportunity.project_id and SalesOpportunity.created_at between :start AND :end inner join user on Project.user_id = user.id " +
            "where Project.deleted_at IS NULL GROUP BY Project.id ORDER BY id DESC")
    public List<Project> getAuthorityProject(String userId, Date start, Date end);

    @Query("SELECT Project.* from Project,Customer where (Project.name LIKE :keyword or Customer" +
            ".name LIKE :keyword) and Project.customer_id = Customer.id and Project.deleted_at IS" +
            " " +
            "NULL")
    public List<Project> searchProject(String keyword);

    @Query("SELECT * from Project where customer_id = :customerId and " +
            "deleted_at IS NULL order by from_date DESC")
    public List<Project> getProjectByCustomer(String customerId);

    @Query("SELECT Project.* from Project,AuthorityProject where Project.customer_id = " +
            ":customerId and Project.id =" +
            " AuthorityProject.project_id and AuthorityProject.user_id = :userId and " +
            "Project.deleted_at IS NULL order by Project.from_date DESC")
    public List<Project> getAuthorityProjectByCustomer(String userId, String customerId);

    @Query("SELECT * from Project where id = :projectId and " +
            "deleted_at IS NULL LIMIT 1")
    public Project getProjectById(String projectId);

    @Query("SELECT Project.* from Project,AuthorityProject where Project.id = :projectId and " +
            "Project.id =" +
            " AuthorityProject.project_id and AuthorityProject.user_id = :userId and " +
            "Project.deleted_at IS NULL LIMIT 1")
    public Project getAuthorityProjectById(String userId, String projectId);


    /*------------------------------銷售機會-------------------------------------*/
    @Query("SELECT * from SalesOpportunity where project_id = :projectId and " +
            "deleted_at IS NULL order by updated_at DESC LIMIT 1")
    public SalesOpportunity getSalesOpportunityByProject(String projectId);

    @Query("SELECT * from SalesOpportunity where project_id = :projectId and " +
            "deleted_at IS NULL order by updated_at DESC")
    public List<SalesOpportunity> getAllSalesOpportunityByProject(String projectId);

    @Query("SELECT * from SalesOpportunitySub where sales_opportunity_id = :id and " +
            "deleted_at IS NULL")
    public List<SalesOpportunitySub> getSalesOpportunitySubBySalesOpportunity(String id);

    @Query("SELECT * from SalesOpportunity where id = :id and " +
            "deleted_at IS NULL LIMIT 1")
    public SalesOpportunity getSalesOpportunityById(String id);

    @Query("SELECT * from SalesOpportunitySub where id = :id and " +
            "deleted_at IS NULL LIMIT 1")
    public SalesOpportunitySub getSalesOpportunitySubById(String id);

    @Query("SELECT * from SalesOpportunity where updated_at > :start and updated_at < :end and " +
            "deleted_at IS NULL order by updated_at DESC")
    public List<SalesOpportunity> getSalesOpportunityByPeriod(Date start, Date end);

    @Query("SELECT * from SalesOpportunity where updated_at > :start and updated_at < :end and " +
            "user_id = :userId and deleted_at IS NULL order by updated_at DESC")
    public List<SalesOpportunity> getSalesOpportunityByPeriod(Date start, Date end, String userId);

    @Query("SELECT SalesOpportunity.* from SalesOpportunity,User where SalesOpportunity.updated_at > :start and SalesOpportunity.updated_at < :end and " +
            "SalesOpportunity.user_id = User.id and User.department_id = :departmentId and SalesOpportunity.deleted_at IS NULL order by SalesOpportunity.updated_at DESC")
    public List<SalesOpportunity> getDepartemntSalesOpportunityByPeriod(Date start, Date end, String departmentId);

    @Query("SELECT SalesOpportunity.* from SalesOpportunity,User where SalesOpportunity.updated_at > :start and SalesOpportunity.updated_at < :end and " +
            "SalesOpportunity.user_id = User.id and User.company_id = :companyId and SalesOpportunity.deleted_at IS NULL order by SalesOpportunity.updated_at DESC")
    public List<SalesOpportunity> getCompanySalesOpportunityByPeriod(Date start, Date end, String companyId);

    /*------------------------------客戶-------------------------------------*/
    @Query("SELECT name from Customer where id = :customerId and " +
            "deleted_at IS NULL LIMIT 1")
    public String getCustomerNameById(String customerId);

    @Query("SELECT Customer.* from Customer,AuthorityCustomer where AuthorityCustomer.user_id = " +
            ":userId and AuthorityCustomer.customer_id" +
            " = Customer.id and Customer.deleted_at IS NULL order by Customer.created_at DESC")
    public List<Customer> getCustomer(String userId);

    @Query("SELECT * from Customer where id = :customerId and deleted_at IS NULL LIMIT 1")
    public Customer getCustomerById(String customerId);

    @Query("SELECT * from ContactPerson where id = :contactPersonId and deleted_at IS NULL LIMIT 1")
    public ContactPerson getContactPersonById(String contactPersonId);

    @Query("SELECT Customer.* from Customer,AuthorityCustomer where AuthorityCustomer.user_id = " +
            ":userId " +
            "and AuthorityCustomer.customer_id = " +
            "Customer.id and Customer.deleted_at IS NULL and " +
            "(Customer.name LIKE " +
            ":keyword or Customer.full_name LIKE :keyword or Customer.full_name_en LIKE :keyword " +
            "or Customer.description " +
            "LIKE :keyword) order by updated_at desc")
    public List<Customer> searchCustomer(String keyword, String userId);

    @Query("SELECT * from Customer where deleted_at IS NULL and full_name = :name")
    public Customer checkCustomerByName(String name);

    /*------------------------------使用者(業務)-------------------------------------*/
    @Query("SELECT * from User where id = :userId and " +
            "deleted_at IS NULL LIMIT 1")
    public User getUserById(String userId);

    /*------------------------------對話-------------------------------------*/
    @Query("SELECT * from TaskConversation where task_id = :parentId and " +
            "deleted_at IS NULL order by updated_at desc")
    public List<TaskConversation> getConversationByParent(String parentId);

    @Query("SELECT * from TaskConversation where id = :id and " +
            "deleted_at IS NULL LIMIT 1")
    public TaskConversation getConversationById(String id);

    @Query("SELECT * from TaskMessage where id = :id and " +
            "deleted_at IS NULL LIMIT 1")
    public TaskMessage getMessageById(String id);

    @Query("SELECT * from TaskMessage where conversation_id = :conversationId and " +
            "deleted_at IS NULL order by updated_at desc")
    public List<TaskMessage> getMessageByConversation(String conversationId);

    /*------------------------------檔案-------------------------------------*/
    @Query("SELECT File.* from File,FilePivot where FilePivot.parent_id = :parentId and FilePivot" +
            ".file_id = File.id and FilePivot.deleted_at IS NULL and " +
            "File.deleted_at IS NULL")
    public List<File> getFileByParent(String parentId);

    @Query("SELECT * from File where id = :fileId and deleted_at IS NULL LIMIT 1")
    public File getFileById(String fileId);

    @Query("SELECT * from FilePivot where id = :id AND deleted_at IS NULL LIMIT 1")
    public FilePivot getFilePivotById(String id);

    @Query("SELECT * from FilePivot where file_id = :fileId and parent_id = :parentId and " +
            "deleted_at IS NULL LIMIT 1")
    public FilePivot getFilePivot(String fileId, String parentId);

    @Query("SELECT * from FilePivot where file_id = :fileId and " +
            "deleted_at IS NULL")
    public List<FilePivot> getFilePivot(String fileId);

    /*------------------------------設定-------------------------------------*/
    @Query("SELECT * from UserQuickMenu where id = :id AND deleted_at IS NULL LIMIT 1")
    public UserQuickMenu getUserQuickMenuById(String id);

    /*------------------------------月報-------------------------------------*/
    @Query("SELECT * from MonthReport where id = :id and deleted_at IS NULL LIMIT 1")
    public MonthReport getMonthReportById(String id);

    @Query("SELECT * from MonthReport where time = :time and user_id = :userId and deleted_at IS " +
            "NULL ORDER BY updated_at desc LIMIT 1")
    public MonthReport getMonthReportByTime(String time, String userId);

    @Query("SELECT * from Report where parent_id = :parentId and user_id = :userId and deleted_at" +
            " IS NULL ORDER BY updated_at desc LIMIT 1")
    public Report getReportByParent(String parentId, String userId);

    @Query("SELECT id from Trip where type = 1 and to_date < :date" +
            " and Trip.user_id = :userId and Trip.deleted_at IS NULL EXCEPT SELECT Report.parent_id from Trip,Report where Trip.type = 1 and Trip.to_date < :date and Trip" +
            ".id == Report.parent_id and Trip.user_id = " +
            ":userId and Trip.deleted_at IS NULL")
    public List<String> getTripNotReport(String userId, Date date);

    /*-------------------------------儲存-----------------------------------*/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> saveCustomer(List<Customer> input);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public Long saveCustomer(Customer input);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public Long saveContactPerson(ContactPerson input);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> saveContactPerson(List<ContactPerson> input);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public Long saveAbsent(Absent input);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> saveAbsent(List<Absent> input);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public Long saveContacter(Contacter input);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> saveContacter(List<Contacter> input);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public Long saveContract(Contract input);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> saveContract(List<Contract> input);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> saveFile(List<File> input);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public Long saveFile(File input);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> saveFilePivot(List<FilePivot> input);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public Long saveFilePivot(FilePivot input);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public Long saveOffice(Office input);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> saveOffice(List<Office> input);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public Long saveParticipant(Participant input);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> saveParticipant(List<Participant> input);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public Long saveProject(Project input);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> saveProject(List<Project> input);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public Long saveQuotation(Quotation input);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> saveQuotation(List<Quotation> input);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> saveQuotationProduct(List<QuotationProduct> input);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public Long saveReimbursement(Reimbursement input);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> saveReimbursement(List<Reimbursement> input);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> saveReimbursementItem(List<ReimbursementItem> input);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public Long saveSalesOpportunity(SalesOpportunity input);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> saveSalesOpportunity(List<SalesOpportunity> input);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public Long saveSalesOpportunitySub(SalesOpportunitySub input);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> saveSalesOpportunitySub(List<SalesOpportunitySub> input);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public Long saveSample(Sample input);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> saveSample(List<Sample> input);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> saveSampleProduct(List<SampleProduct> input);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public Long saveTask(Task input);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> saveTask(List<Task> input);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public Long saveTaskConversation(TaskConversation input);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> saveTaskConversation(List<TaskConversation> input);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public Long saveTaskMessage(TaskMessage input);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> saveTaskMessage(List<TaskMessage> input);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public Long saveTrip(Trip input);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> saveTrip(List<Trip> input);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public Long saveTripStatusLog(TripStatusLog input);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> saveTripStatusLog(List<TripStatusLog> input);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> saveUserCustomer(List<AuthorityCustomer> input);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> saveVisit(List<Visit> input);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public Long saveVisit(Visit input);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public Long saveSyncLog(SyncLog input);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> saveMonthReport(List<MonthReport> input);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public Long saveMonthReport(MonthReport input);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> saveUserQuickMenu(List<UserQuickMenu> input);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> saveExpress(List<Express> input);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public Long saveExpress(Express input);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> saveNewProjectProduction(List<NewProjectProduction> input);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public Long saveNewProjectProduction(NewProjectProduction input);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> saveSpecialPrice(List<SpecialPrice> input);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public Long saveSpecialPrice(SpecialPrice input);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> saveSpecialPriceProduct(List<SpecialPriceProduct> input);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> saveTravel(List<Travel> input);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public Long saveTravel(Travel input);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> saveNonStandardInquiry(List<NonStandardInquiry> input);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public Long saveNonStandardInquiry(NonStandardInquiry input);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> saveNonStandardInquiryProduct(List<NonStandardInquiryProduct> input);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> saveSpringRingInquiry(List<SpringRingInquiry> input);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public Long saveSpringRingInquiry(SpringRingInquiry input);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> saveSpringRingInquiryProduct(List<SpringRingInquiryProduct> input);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> saveSpecialShip(List<SpecialShip> input);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public Long saveSpecialShip(SpecialShip input);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> saveRepayment(List<Repayment> input);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public Long saveRepayment(Repayment input);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> saveReport(List<Report> input);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public Long saveReport(Report input);

    /*-------------------------------同步-----------------------------------*/

    @Query("SELECT * from Absent where updated_at > :syncDate and updated_at < :now and syncable " +
            "= 1")
    public List<Absent> getAbsentAfterDate(Date syncDate, Date now);

    @Query("SELECT * from Contacter where updated_at > :syncDate and updated_at < :now and " +
            "syncable = 1")
    public List<Contacter> getContacterAfterDate(Date syncDate, Date now);

    @Query("SELECT * from ContactPerson where updated_at > :syncDate and updated_at < :now and " +
            "syncable = 1")
    public List<ContactPerson> getContactPersonAfterDate(Date syncDate, Date now);

    @Query("SELECT * from Contract where updated_at > :syncDate and updated_at < :now and " +
            "syncable = 1")
    public List<Contract> getContractAfterDate(Date syncDate, Date now);

    @Query("SELECT * from Customer where updated_at > :syncDate and updated_at < :now and " +
            "syncable = 1")
    public List<Customer> getCustomerAfterDate(Date syncDate, Date now);

    @Query("SELECT * from Office where updated_at > :syncDate and updated_at < :now and syncable " +
            "= 1")
    public List<Office> getOfficeAfterDate(Date syncDate, Date now);

    @Query("SELECT * from Participant where updated_at > :syncDate and updated_at < :now and " +
            "syncable = 1")
    public List<Participant> getParticipantAfterDate(Date syncDate, Date now);

    @Query("SELECT * from Project where updated_at > :syncDate and updated_at < :now and syncable" +
            " = 1")
    public List<Project> getProjectAfterDate(Date syncDate, Date now);

    @Query("SELECT * from Quotation where updated_at > :syncDate and updated_at < :now and " +
            "syncable = 1")
    public List<Quotation> getQuotationAfterDate(Date syncDate, Date now);

    @Query("SELECT * from QuotationProduct where updated_at > :syncDate and updated_at < :now and" +
            " syncable = 1")
    public List<QuotationProduct> getQuotationProductAfterDate(Date syncDate, Date now);

    @Query("SELECT * from Reimbursement where updated_at > :syncDate and updated_at < :now and " +
            "syncable = 1")
    public List<Reimbursement> getReimbursementAfterDate(Date syncDate, Date now);

    @Query("SELECT * from ReimbursementItem where updated_at > :syncDate and updated_at < :now " +
            "and syncable = 1")
    public List<ReimbursementItem> getReimbursementItemAfterDate(Date syncDate, Date now);

    @Query("SELECT * from SalesOpportunity where updated_at > :syncDate and updated_at < :now and" +
            " syncable = 1")
    public List<SalesOpportunity> getSalesOpportunityAfterDate(Date syncDate, Date now);

    @Query("SELECT * from SalesOpportunitySub where updated_at > :syncDate and updated_at < :now " +
            "and syncable = 1")
    public List<SalesOpportunitySub> getSalesOpportunitySubAfterDate(Date syncDate, Date now);

    @Query("SELECT * from Sample where updated_at > :syncDate and updated_at < :now and syncable " +
            "= 1")
    public List<Sample> getSampleAfterDate(Date syncDate, Date now);

    @Query("SELECT * from SampleProduct where updated_at > :syncDate and updated_at < :now and " +
            "syncable = 1")
    public List<SampleProduct> getSampleProductAfterDate(Date syncDate, Date now);

    @Query("SELECT * from Task where updated_at > :syncDate and updated_at < :now and syncable = 1")
    public List<Task> getTaskAfterDate(Date syncDate, Date now);

    @Query("SELECT * from TaskConversation where updated_at > :syncDate and updated_at < :now and" +
            " syncable = 1")
    public List<TaskConversation> getTaskConversationAfterDate(Date syncDate, Date now);

    @Query("SELECT * from TaskMessage where updated_at > :syncDate and updated_at < :now and " +
            "syncable = 1")
    public List<TaskMessage> getTaskMessageAfterDate(Date syncDate, Date now);

    @Query("SELECT * from Trip where updated_at > :syncDate and updated_at < :now and syncable = 1")
    public List<Trip> getTripAfterDate(Date syncDate, Date now);

    @Query("SELECT * from TripStatusLog where updated_at > :syncDate and updated_at < :now and " +
            "syncable = 1")
    public List<TripStatusLog> getTripStatusLogAfterDate(Date syncDate, Date now);

    @Query("SELECT * from Visit where updated_at > :syncDate and updated_at < :now and syncable =" +
            " 1")
    public List<Visit> getVisitAfterDate(Date syncDate, Date now);

    @Query("SELECT * from UserQuickMenu where updated_at > :syncDate and updated_at < :now and " +
            "syncable = 1")
    public List<UserQuickMenu> getUserQuickMenuAfterDate(Date syncDate, Date now);

    @Query("SELECT File.id from File where updated_at > :syncDate and updated_at < :now and " +
            "syncable = 1 and user_id = " +
            ":userId")
    public List<String> getFileIdAfterDate(Date syncDate, Date now, String userId);

    @Query("SELECT * from FilePivot where updated_at > :syncDate and updated_at < :now and " +
            "syncable = 1")
    public List<FilePivot> getFilePivotAfterDate(Date syncDate, Date now);

    @Query("SELECT * from MonthReport where updated_at > :syncDate and updated_at < :now and " +
            "syncable = 1")
    public List<MonthReport> getMonthReportAfterDate(Date syncDate, Date now);

    @Query("SELECT * from Express where updated_at > :syncDate and updated_at < :now and syncable" +
            " = 1")
    public List<Express> getExpressAfterDate(Date syncDate, Date now);

    @Query("SELECT * from NewProjectProduction where updated_at > :syncDate and updated_at < :now" +
            " and syncable = 1")
    public List<NewProjectProduction> getNewProjectProductionAfterDate(Date syncDate, Date now);

    @Query("SELECT * from SpecialPrice where updated_at > :syncDate and updated_at < :now and " +
            "syncable = 1")
    public List<SpecialPrice> getSpecialPriceAfterDate(Date syncDate, Date now);

    @Query("SELECT * from SpecialPriceProduct where updated_at > :syncDate and updated_at < :now " +
            "and syncable = 1")
    public List<SpecialPriceProduct> getSpecialPriceProductAfterDate(Date syncDate, Date now);

    @Query("SELECT * from Travel where updated_at > :syncDate and updated_at < :now and syncable " +
            "= 1")
    public List<Travel> getTravelAfterDate(Date syncDate, Date now);

    @Query("SELECT * from NonStandardInquiry where updated_at > :syncDate and updated_at < :now " +
            "and syncable = 1")
    public List<NonStandardInquiry> getNonStandardInquiryAfterDate(Date syncDate, Date now);

    @Query("SELECT * from NonStandardInquiryProduct where updated_at > :syncDate and updated_at <" +
            " :now and syncable = 1")
    public List<NonStandardInquiryProduct> getNonStandardInquiryProductAfterDate(Date syncDate,
                                                                                 Date now);

    @Query("SELECT * from SpringRingInquiry where updated_at > :syncDate and updated_at < :now " +
            "and syncable = 1")
    public List<SpringRingInquiry> getSpringRingInquiryAfterDate(Date syncDate, Date now);

    @Query("SELECT * from SpringRingInquiryProduct where updated_at > :syncDate and updated_at < " +
            ":now and syncable = 1")
    public List<SpringRingInquiryProduct> getSpringRingInquiryProductAfterDate(Date syncDate,
                                                                               Date now);

    @Query("SELECT * from SpecialShip where updated_at > :syncDate and updated_at < :now and " +
            "syncable = 1")
    public List<SpecialShip> getSpecialShipAfterDate(Date syncDate, Date now);

    @Query("SELECT * from Repayment where updated_at > :syncDate and updated_at < :now and " +
            "syncable = 1")
    public List<Repayment> getRepaymentAfterDate(Date syncDate, Date now);

    @Query("SELECT * from Report where updated_at > :syncDate and updated_at < :now and syncable " +
            "= 1")
    public List<Report> getReportAfterDate(Date syncDate, Date now);

}
