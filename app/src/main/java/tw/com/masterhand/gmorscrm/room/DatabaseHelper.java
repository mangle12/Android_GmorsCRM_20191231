package tw.com.masterhand.gmorscrm.room;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import tw.com.masterhand.gmorscrm.TokenManager;
import tw.com.masterhand.gmorscrm.enums.ParentType;
import tw.com.masterhand.gmorscrm.enums.ReportFilterType;
import tw.com.masterhand.gmorscrm.enums.SubmitStatus;
import tw.com.masterhand.gmorscrm.enums.SyncStatus;
import tw.com.masterhand.gmorscrm.enums.TripStatus;
import tw.com.masterhand.gmorscrm.enums.TripType;
import tw.com.masterhand.gmorscrm.model.AbsentWithConfig;
import tw.com.masterhand.gmorscrm.model.ConfigCurrencyWithRate;
import tw.com.masterhand.gmorscrm.model.ContacterWithPerson;
import tw.com.masterhand.gmorscrm.model.ContractWithConfig;
import tw.com.masterhand.gmorscrm.model.Conversation;
import tw.com.masterhand.gmorscrm.model.ExpressWithConfig;
import tw.com.masterhand.gmorscrm.model.MainTrip;
import tw.com.masterhand.gmorscrm.model.Message;
import tw.com.masterhand.gmorscrm.model.MultipleSubmit;
import tw.com.masterhand.gmorscrm.model.NonStandardInquiryWithConfig;
import tw.com.masterhand.gmorscrm.model.OfficeWithConfig;
import tw.com.masterhand.gmorscrm.model.ParticipantWithUser;
import tw.com.masterhand.gmorscrm.model.ProductionWithConfig;
import tw.com.masterhand.gmorscrm.model.ProjectWithConfig;
import tw.com.masterhand.gmorscrm.model.QuotationWithConfig;
import tw.com.masterhand.gmorscrm.model.ReimbursementItemWithConfig;
import tw.com.masterhand.gmorscrm.model.ReimbursementWithConfig;
import tw.com.masterhand.gmorscrm.model.ReportSummary;
import tw.com.masterhand.gmorscrm.model.SalesOpportunityWithConfig;
import tw.com.masterhand.gmorscrm.model.SampleProductWithConfig;
import tw.com.masterhand.gmorscrm.model.SampleWithConfig;
import tw.com.masterhand.gmorscrm.model.SpecialPriceWithConfig;
import tw.com.masterhand.gmorscrm.model.SpecialShipWithConfig;
import tw.com.masterhand.gmorscrm.model.SpringRingInquiryWithConfig;
import tw.com.masterhand.gmorscrm.model.SyncRecord;
import tw.com.masterhand.gmorscrm.model.SyncSetting;
import tw.com.masterhand.gmorscrm.model.TaskWithConfig;
import tw.com.masterhand.gmorscrm.model.TravelWithConfig;
import tw.com.masterhand.gmorscrm.model.UserWithConfig;
import tw.com.masterhand.gmorscrm.model.VisitWithConfig;
import tw.com.masterhand.gmorscrm.room.local.DownloadLog;
import tw.com.masterhand.gmorscrm.room.local.ReadLog;
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
import tw.com.masterhand.gmorscrm.room.record.Visit;
import tw.com.masterhand.gmorscrm.room.setting.Admin;
import tw.com.masterhand.gmorscrm.room.setting.AuthorityCustomer;
import tw.com.masterhand.gmorscrm.room.setting.AuthorityProject;
import tw.com.masterhand.gmorscrm.room.setting.Company;
import tw.com.masterhand.gmorscrm.room.setting.ConfigCity;
import tw.com.masterhand.gmorscrm.room.setting.ConfigCompanyHiddenField;
import tw.com.masterhand.gmorscrm.room.setting.ConfigCompetitiveStatus;
import tw.com.masterhand.gmorscrm.room.setting.ConfigCountry;
import tw.com.masterhand.gmorscrm.room.setting.ConfigCurrency;
import tw.com.masterhand.gmorscrm.room.setting.ConfigCustomerLevel;
import tw.com.masterhand.gmorscrm.room.setting.ConfigCustomerSapGroup;
import tw.com.masterhand.gmorscrm.room.setting.ConfigCustomerSapType;
import tw.com.masterhand.gmorscrm.room.setting.ConfigCustomerSource;
import tw.com.masterhand.gmorscrm.room.setting.ConfigExpressDestination;
import tw.com.masterhand.gmorscrm.room.setting.ConfigIndustry;
import tw.com.masterhand.gmorscrm.room.setting.ConfigInquiryProductType;
import tw.com.masterhand.gmorscrm.room.setting.ConfigNewProjectProduction;
import tw.com.masterhand.gmorscrm.room.setting.ConfigOffice;
import tw.com.masterhand.gmorscrm.room.setting.ConfigProductCategorySub;
import tw.com.masterhand.gmorscrm.room.setting.ConfigProjectSource;
import tw.com.masterhand.gmorscrm.room.setting.ConfigQuotationProductCategory;
import tw.com.masterhand.gmorscrm.room.setting.ConfigQuotationProductReport;
import tw.com.masterhand.gmorscrm.room.setting.ConfigReimbursementItem;
import tw.com.masterhand.gmorscrm.room.setting.ConfigSalesOpportunityLoseType;
import tw.com.masterhand.gmorscrm.room.setting.ConfigSalesOpportunityType;
import tw.com.masterhand.gmorscrm.room.setting.ConfigSalesOpportunityWinType;
import tw.com.masterhand.gmorscrm.room.setting.ConfigSampleAmountRange;
import tw.com.masterhand.gmorscrm.room.setting.ConfigSampleImportMethod;
import tw.com.masterhand.gmorscrm.room.setting.ConfigSamplePaymentMethod;
import tw.com.masterhand.gmorscrm.room.setting.ConfigSampleProductType;
import tw.com.masterhand.gmorscrm.room.setting.ConfigSampleReason;
import tw.com.masterhand.gmorscrm.room.setting.ConfigSampleSource;
import tw.com.masterhand.gmorscrm.room.setting.ConfigSealedState;
import tw.com.masterhand.gmorscrm.room.setting.ConfigSgbAdvantage;
import tw.com.masterhand.gmorscrm.room.setting.ConfigSgbDisadvantage;
import tw.com.masterhand.gmorscrm.room.setting.ConfigTax;
import tw.com.masterhand.gmorscrm.room.setting.ConfigTrend;
import tw.com.masterhand.gmorscrm.room.setting.ConfigYearPotential;
import tw.com.masterhand.gmorscrm.room.setting.Department;
import tw.com.masterhand.gmorscrm.room.setting.DepartmentContractItem;
import tw.com.masterhand.gmorscrm.room.setting.DepartmentSalesOpportunity;
import tw.com.masterhand.gmorscrm.room.setting.DepartmentSalesOpportunitySub;
import tw.com.masterhand.gmorscrm.room.setting.User;
import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.tools.PreferenceHelper;
import tw.com.masterhand.gmorscrm.tools.TimeFormater;

public class DatabaseHelper {
    final String TAG = getClass().getSimpleName();
    public static final BackpressureStrategy BACK_PRESSURE_STRATEGY = BackpressureStrategy.BUFFER;
    public static final Scheduler SCHEDULER = Schedulers.io();
    private static DatabaseHelper sInstance;
    private static PreferenceHelper preferenceHelper;
    private AppDatabase mDb;

    // For Singleton instantiation
    private static final Object LOCK = new Object();

    public synchronized static DatabaseHelper getInstance(Context context) {
        preferenceHelper = new PreferenceHelper(context.getApplicationContext());
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new DatabaseHelper(context.getApplicationContext());
            }
        } else {
            if (!sInstance.getDatabase(context).getOpenHelper().getDatabaseName().equals
                    (preferenceHelper.getAccount())) {
                sInstance = null;
                synchronized (LOCK) {
                    sInstance = new DatabaseHelper(context.getApplicationContext());
                }
            }
        }
        return sInstance;
    }

    private DatabaseHelper(Context context) {
        Logger.e(TAG, "init DatabaseHelper:" + preferenceHelper.getAccount());
        /*以使用者帳號作為資料庫名稱*/
        /*建立Sqlite*/
//        if (mDb != null && !mDb.getOpenHelper().getDatabaseName().equals(preferenceHelper
//                .getAccount()))
//            mDb.close();
        mDb = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, preferenceHelper.getAccount()).build();
    }

    public String getDatabaseName() {
        return mDb.getOpenHelper().getDatabaseName();
    }

//    /**
//     * 從版本1至2的處理
//     */
//    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
//        @Override
//        public void migrate(SupportSQLiteDatabase database) {
//            preferenceHelper.clearLastSync();
//        }
//    };
//    /**
//     * 從版本2至3的處理
//     */
//    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
//        @Override
//        public void migrate(SupportSQLiteDatabase database) {
//            preferenceHelper.clearLastSync();
//            database.execSQL("ALTER TABLE ContactPerson "
//                    + " ADD COLUMN wechat TEXT");
//        }
//    };

    public AppDatabase getDatabase(Context context) {
        if (!mDb.isOpen()) {
            mDb = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, preferenceHelper.getAccount()).build();
        }
        return mDb;
    }

    /**
     * 產生資料列表ID
     */
    public static String generateHashId(String tableName) {
        String input = tableName + String.valueOf(new Date().getTime());
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(input.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            Logger.e("generateHashId", "NoSuchAlgorithmException:" + e.getMessage());
            return "";
        } catch (UnsupportedEncodingException e) {
            Logger.e("generateHashId", "UnsupportedEncodingException:" + e.getMessage());
            return "";
        }
        byte[] byteArray = messageDigest.digest();
        StringBuilder md5StrBuff = new StringBuilder();
        for (byte aByteArray : byteArray) {
            if (Integer.toHexString(0xFF & aByteArray).length() == 1)
                md5StrBuff.append("0").append(Integer.toHexString(0xFF & aByteArray));
            else
                md5StrBuff.append(Integer.toHexString(0xFF & aByteArray));
        }
        String id = md5StrBuff.toString();
//        Logger.e("DatabaseHelper", "id:" + id);
        return id;
    }

    /*--------------------------------------客戶-------------------------------------------*/
    public Single<String> saveCustomer(final Customer customer, final String userId) {
        return Single.create(new SingleOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<String> emitter) throws Exception {
                String customerId = customer.getId();
                if (TextUtils.isEmpty(customerId)) {
                    customerId = generateHashId(customer.getClass().getSimpleName());
                    customer.setId(customerId);
                    AuthorityCustomer authorityCustomer = new AuthorityCustomer();
                    authorityCustomer.setCustomer_id(customerId);
                    authorityCustomer.setUser_id(userId);
                    mDb.settingDao().saveAuthorityCustomer(authorityCustomer);
                }
                customer.setUpdated_at(new Date());
                if (mDb.recordDao().saveCustomer(customer) != -1L) {
                    emitter.onSuccess(customerId);
                } else {
                    emitter.onError(new Throwable("save customer failed."));
                }
            }
        }).subscribeOn(Schedulers.single());
    }

    /**
     * 取得所有客戶
     */
    public Flowable<Customer> getCustomer(final String userId) {
        return Flowable.create(new FlowableOnSubscribe<Customer>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<Customer> emitter)
                    throws
                    Exception {
                List<Customer> customers = mDb.recordDao().getCustomer(userId);
                for (Customer customer : customers)
                    emitter.onNext(customer);
                emitter.onComplete();
            }
        }, BACK_PRESSURE_STRATEGY).subscribeOn(SCHEDULER);

    }

    /**
     * 依ID取得客戶
     */
    public Single<Customer> getCustomerById(final String customerId) {
        return Single.create(new SingleOnSubscribe<Customer>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<Customer> emitter) throws Exception {
                Customer customer = mDb.recordDao().getCustomerById(customerId);
                if (customer == null)
                    emitter.onError(new Throwable("get customer failed."));
                else
                    emitter.onSuccess(customer);
            }
        }).subscribeOn(Schedulers.single());
    }

    /**
     * 檢查客戶中文全名是否已存在
     */
    public Single<Boolean> checkCustomerByName(final String name) {
        return Single.create(new SingleOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<Boolean> emitter) throws Exception {
                Customer customer = mDb.recordDao().checkCustomerByName(name);
                if (customer == null)
                    emitter.onSuccess(true);
                else
                    emitter.onSuccess(false);
            }
        }).subscribeOn(Schedulers.single());
    }

    /**
     * 搜尋客戶
     */
    public Flowable<Customer> searchCustomer(final String keyword, final String userId) {
        return Flowable.create(new FlowableOnSubscribe<Customer>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<Customer> emitter) throws Exception {
                List<Customer> customers = mDb.recordDao().searchCustomer(keyword, userId);
                for (Customer customer : customers) {
                    emitter.onNext(customer);
                }
                emitter.onComplete();
            }
        }, BACK_PRESSURE_STRATEGY).subscribeOn(SCHEDULER);
    }

    /*--------------------------------------銷售機會-------------------------------------------*/
    public Single<String> saveSalesOpportunity(final SalesOpportunityWithConfig salesOpportunityWithConfig) {
        return Single.create(new SingleOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<String> emitter) throws Exception {
                SalesOpportunity salesOpportunity = salesOpportunityWithConfig.getSalesOpportunity();
                String salesOpportunityId = salesOpportunity.getId();
                if (TextUtils.isEmpty(salesOpportunityId)) {
                    salesOpportunityId = generateHashId(salesOpportunity.getClass().getSimpleName());
                    salesOpportunity.setId(salesOpportunityId);
                }
                salesOpportunity.setUpdated_at(new Date());
                if (mDb.recordDao().saveSalesOpportunity(salesOpportunity) == -1) {
                    emitter.onError(new Throwable("save sales opportunity failed."));
                } else {
                    /*儲存細項*/
                    for (SalesOpportunitySub sub : salesOpportunityWithConfig.getSalesOpportunitySubs()) {
                        if (TextUtils.isEmpty(sub.getId())) {
                            sub.setId(generateHashId(sub.getClass().getSimpleName()));
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        sub.setUpdated_at(new Date());
                        sub.setSales_opportunity_id(salesOpportunity.getId());
                    }
                    mDb.recordDao().saveSalesOpportunitySub(salesOpportunityWithConfig.getSalesOpportunitySubs());
                    /*同時刷新Project的updated_at*/
                    Project project = mDb.recordDao().getProjectById(salesOpportunity.getProject_id());
                    if (project != null) {
                        project.setUpdated_at(new Date());
                        mDb.recordDao().saveProject(project);
                    }
                    emitter.onSuccess(salesOpportunityId);
                }
            }
        }).subscribeOn(Schedulers.single());
    }

    public Single<SalesOpportunityWithConfig> getLastSalesOpportunityByProject(final String projectId) {
        return Single.create(new SingleOnSubscribe<SalesOpportunityWithConfig>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<SalesOpportunityWithConfig> emitter)
                    throws Exception {
                SalesOpportunityWithConfig result = new SalesOpportunityWithConfig();
                SalesOpportunity salesOpportunity = mDb.recordDao().getSalesOpportunityByProject(projectId);
                if (salesOpportunity == null)
                    emitter.onError(new Throwable("can't find salesOpportunity"));
                else {
                    result.setSalesOpportunity(salesOpportunity);
                    result.setSalesOpportunitySubs(mDb.recordDao().getSalesOpportunitySubBySalesOpportunity(salesOpportunity.getId()));
                    emitter.onSuccess(result);
                }
            }
        }).subscribeOn(Schedulers.single());
    }

    public Single<SalesOpportunityWithConfig> getSalesOpportunityWithConfig(final String salesOpportunityId) {
        return Single.create(new SingleOnSubscribe<SalesOpportunityWithConfig>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<SalesOpportunityWithConfig> emitter)
                    throws Exception {
                SalesOpportunityWithConfig result = new SalesOpportunityWithConfig();
                SalesOpportunity salesOpportunity = mDb.recordDao().getSalesOpportunityById(salesOpportunityId);
                if (salesOpportunity == null)
                    emitter.onError(new Throwable("can't find salesOpportunity"));
                else {
                    result.setSalesOpportunity(salesOpportunity);
                    result.setSalesOpportunitySubs(mDb.recordDao().getSalesOpportunitySubBySalesOpportunity(salesOpportunity.getId()));
                    emitter.onSuccess(result);
                }
            }
        }).subscribeOn(Schedulers.single());
    }

    /**
     * 依專案取得所有銷售機會
     */
    public Flowable<SalesOpportunity> getSalesOpportunityByProject(final String projectId) {
        return Flowable.create(new FlowableOnSubscribe<SalesOpportunity>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<SalesOpportunity> emitter)
                    throws
                    Exception {
                List<SalesOpportunity> opportunities = mDb.recordDao().getAllSalesOpportunityByProject(projectId);
                for (SalesOpportunity opportunity : opportunities)
                    emitter.onNext(opportunity);
                emitter.onComplete();
            }
        }, BACK_PRESSURE_STRATEGY).subscribeOn(SCHEDULER);

    }
    /*--------------------------------------工作項目-------------------------------------------*/

    public Single<String> saveProject(final ProjectWithConfig project) {
        return Single.create(new SingleOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<String> emitter) throws Exception {
                String projectId = project.getProject().getId();
                if (TextUtils.isEmpty(projectId)) {
                    projectId = generateHashId(project.getClass().getSimpleName());
                    project.getProject().setId(projectId);
                }
                project.getProject().setUpdated_at(new Date());
                if (mDb.recordDao().saveProject(project.getProject()) != -1L) {
                    AuthorityProject authorityProject = new AuthorityProject();
                    authorityProject.setUser_id(TokenManager.getInstance().getUser().getId());
                    authorityProject.setProject_id(projectId);
                    mDb.settingDao().saveAuthorityProject(authorityProject);
                    saveParticipant(null, projectId, ParentType.PROJECT, project.getParticipants());
                    saveContacter(projectId, ParentType.PROJECT, project.getContacters());
                    saveFile(projectId, ParentType.PROJECT, project.getFiles());
                    emitter.onSuccess(projectId);
                } else {
                    emitter.onError(new Throwable("save project failed."));
                }
            }
        }).subscribeOn(Schedulers.single());
    }

    /**
     * 依ID取得有授權的工作項目
     */
    public Single<ProjectWithConfig> getAuthorityProjectById(final String userId, final String projectId, final String departmentId, final String companyId) {
        return Single.create(new SingleOnSubscribe<ProjectWithConfig>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<ProjectWithConfig> emitter)
                    throws
                    Exception {
                Project project = mDb.recordDao().getAuthorityProjectById(userId, projectId);
                if (project == null) {
                    emitter.onError(new Throwable("this project is not authorized."));
                    return;
                }
                ProjectWithConfig projectWithConfig = new ProjectWithConfig();
                projectWithConfig.setProject(project);
                projectWithConfig.setDepartment(mDb.settingDao().getDepartmentById(mDb.recordDao().getUserById(project.getUser_id()).getDepartment_id()));
                projectWithConfig.setSalesOpportunity(mDb.recordDao().getSalesOpportunityByProject(project.getId()));
                if (projectWithConfig.getSalesOpportunity() != null && projectWithConfig.getSalesOpportunity().getDepartment_sales_opportunity() != null) {
                    projectWithConfig.setDepartmentSalesOpportunity(projectWithConfig.getSalesOpportunity().getDepartment_sales_opportunity());
                } else {
                    projectWithConfig.setDepartmentSalesOpportunity(getDepartmentSalesOpportunity(companyId));
                }
                projectWithConfig.setConfigCurrency(mDb.settingDao().getConfigCurrencyById(project.getCurrency_id()));
                projectWithConfig.setCurrencyRate(mDb.settingDao().getCurrencyRateById(project.getCurrency_rate_id()));
                projectWithConfig.setCustomer(mDb.recordDao().getCustomerById(project.getCustomer_id()));
                projectWithConfig.setParticipants(mDb.recordDao().getParticipantByParent(projectId));
                projectWithConfig.setContacters(mDb.recordDao().getContacterByParent(projectId));
                projectWithConfig.setFiles(mDb.recordDao().getFileByParent(project.getId()));
                emitter.onSuccess(projectWithConfig);
            }
        }).subscribeOn(Schedulers.single());
    }

    /**
     * 依ID取得工作項目
     */
    public Single<ProjectWithConfig> getProjectById(final String projectId, final String
            departmentId, final String companyId) {
        return Single.create(new SingleOnSubscribe<ProjectWithConfig>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<ProjectWithConfig> emitter)
                    throws
                    Exception {
                Project project = mDb.recordDao().getProjectById(projectId);
                if (project == null) {
                    emitter.onError(new Throwable("project is null"));
                    return;
                }
                ProjectWithConfig projectWithConfig = new ProjectWithConfig();
                projectWithConfig.setProject(project);
                User user = mDb.recordDao().getUserById(project.getUser_id());

                if (user != null)
                {
                    projectWithConfig.setDepartment(mDb.settingDao().getDepartmentById(user.getDepartment_id()));
                }

                projectWithConfig.setSalesOpportunity(mDb.recordDao().getSalesOpportunityByProject(project.getId()));

                if (projectWithConfig.getSalesOpportunity() != null && projectWithConfig.getSalesOpportunity().getDepartment_sales_opportunity() != null)
                {
                    projectWithConfig.setDepartmentSalesOpportunity(projectWithConfig.getSalesOpportunity().getDepartment_sales_opportunity());
                } else {
                    projectWithConfig.setDepartmentSalesOpportunity(getDepartmentSalesOpportunity(companyId));
                }

                projectWithConfig.setConfigCurrency(mDb.settingDao().getConfigCurrencyById(project.getCurrency_id()));
                projectWithConfig.setCurrencyRate(mDb.settingDao().getCurrencyRateById(project.getCurrency_rate_id()));
                projectWithConfig.setCustomer(mDb.recordDao().getCustomerById(project.getCustomer_id()));
                projectWithConfig.setParticipants(mDb.recordDao().getParticipantByParent(projectId));
                projectWithConfig.setContacters(mDb.recordDao().getContacterByParent(projectId));
                projectWithConfig.setFiles(mDb.recordDao().getFileByParent(project.getId()));
                emitter.onSuccess(projectWithConfig);
            }
        }).subscribeOn(Schedulers.single());
    }

    /**
     * 取得客戶的工作項目
     */
    public Flowable<ProjectWithConfig> getProjectByCustomer(final String customerId, final String departmentId, final String companyId) {
        Logger.e(TAG, "getProjectByCustomer");
        Logger.e(TAG, "customerId:" + customerId);
        Logger.e(TAG, "departmentId:" + departmentId);
        Logger.e(TAG, "companyId:" + companyId);
        return Flowable.create(new FlowableOnSubscribe<ProjectWithConfig>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<ProjectWithConfig> emitter)
                    throws
                    Exception {
                List<Project> projects = mDb.recordDao().getProjectByCustomer(customerId);
                for (Project project : projects) {
                    Logger.e(TAG, "project:" + new Gson().toJson(project));
                    ProjectWithConfig projectWithConfig = new ProjectWithConfig();
                    projectWithConfig.setProject(project);
                    projectWithConfig.setDepartment(mDb.settingDao().getDepartmentById(mDb.recordDao().getUserById(project.getUser_id()).getDepartment_id()));
                    projectWithConfig.setSalesOpportunity(mDb.recordDao().getSalesOpportunityByProject(project.getId()));

                    if (projectWithConfig.getSalesOpportunity() != null && projectWithConfig.getSalesOpportunity().getDepartment_sales_opportunity() != null)
                    {
                        projectWithConfig.setDepartmentSalesOpportunity(projectWithConfig.getSalesOpportunity().getDepartment_sales_opportunity());
                    } else {
                        projectWithConfig.setDepartmentSalesOpportunity(getDepartmentSalesOpportunity(companyId));
                    }

                    projectWithConfig.setConfigCurrency(mDb.settingDao().getConfigCurrencyById(project.getCurrency_id()));
                    projectWithConfig.setCurrencyRate(mDb.settingDao().getCurrencyRateById(project.getCurrency_rate_id()));
                    projectWithConfig.setCustomer(mDb.recordDao().getCustomerById(project.getCustomer_id()));
                    projectWithConfig.setParticipants(mDb.recordDao().getParticipantByParent(project.getId()));
                    projectWithConfig.setContacters(mDb.recordDao().getContacterByParent(project.getId()));
                    projectWithConfig.setFiles(mDb.recordDao().getFileByParent(project.getId()));
                    emitter.onNext(projectWithConfig);
                }
                emitter.onComplete();
            }
        }, BACK_PRESSURE_STRATEGY).subscribeOn(SCHEDULER);
    }

    /**
     * 依客戶取得有授權的工作項目
     */
    public Flowable<Project> getAuthorityProjectByCustomer(final String userId, final String customerId) {
        return Flowable.create(new FlowableOnSubscribe<Project>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<Project> emitter)
                    throws
                    Exception {
                List<Project> projects = mDb.recordDao().getAuthorityProjectByCustomer(userId, customerId);
                for (Project project : projects) {
                    emitter.onNext(project);
                }
                emitter.onComplete();
            }
        }, BACK_PRESSURE_STRATEGY).subscribeOn(SCHEDULER);
    }

    /**
     * 取得所有的工作項目
     */
    public Flowable<ProjectWithConfig> getProject(final Date start, final Date end) {
        return Flowable.create(new FlowableOnSubscribe<ProjectWithConfig>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<ProjectWithConfig> emitter)
                    throws
                    Exception {
                List<Project> projects = mDb.recordDao().getAuthorityProject(TokenManager.getInstance().getUser().getId(), start, end);
                for (Project project : projects) {
                    ProjectWithConfig projectWithConfig = new ProjectWithConfig();
                    projectWithConfig.setProject(project);
                    User user = mDb.recordDao().getUserById(project.getUser_id());
                    if (user == null) {
                        continue;
                    }

                    projectWithConfig.setDepartment(mDb.settingDao().getDepartmentById(user.getDepartment_id()));
                    projectWithConfig.setSalesOpportunity(mDb.recordDao().getSalesOpportunityByProject(project.getId()));

                    if (projectWithConfig.getSalesOpportunity() != null && projectWithConfig.getSalesOpportunity().getDepartment_sales_opportunity() != null)
                    {
                        projectWithConfig.setDepartmentSalesOpportunity(projectWithConfig.getSalesOpportunity().getDepartment_sales_opportunity());
                    } else {
                        projectWithConfig.setDepartmentSalesOpportunity(getDepartmentSalesOpportunity(user.getCompany_id()));
                    }
                    projectWithConfig.setConfigCurrency(mDb.settingDao().getConfigCurrencyById(project.getCurrency_id()));
                    projectWithConfig.setCurrencyRate(mDb.settingDao().getCurrencyRateById(project.getCurrency_rate_id()));
                    projectWithConfig.setCustomer(mDb.recordDao().getCustomerById(project.getCustomer_id()));
                    projectWithConfig.setParticipants(mDb.recordDao().getParticipantByParent(project.getId()));
                    projectWithConfig.setContacters(mDb.recordDao().getContacterByParent(project.getId()));
                    projectWithConfig.setFiles(mDb.recordDao().getFileByParent(project.getId()));
                    emitter.onNext(projectWithConfig);
                }
                emitter.onComplete();
            }
        }, BACK_PRESSURE_STRATEGY).subscribeOn(SCHEDULER);
    }

    /**
     * 搜尋工作項目
     */
    public Flowable<ProjectWithConfig> searchProject(final String departmentId, final String companyId, final String keyword) {
        return Flowable.create(new FlowableOnSubscribe<ProjectWithConfig>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<ProjectWithConfig> emitter)
                    throws
                    Exception {
                List<Project> projects = mDb.recordDao().searchProject(keyword);
                for (Project project : projects) {
                    ProjectWithConfig projectWithConfig = new ProjectWithConfig();
                    projectWithConfig.setProject(project);
                    projectWithConfig.setDepartment(mDb.settingDao().getDepartmentById(mDb.recordDao().getUserById(project.getUser_id()).getDepartment_id()));
                    projectWithConfig.setSalesOpportunity(mDb.recordDao().getSalesOpportunityByProject(project.getId()));

                    if (projectWithConfig.getSalesOpportunity() != null && projectWithConfig.getSalesOpportunity().getDepartment_sales_opportunity() != null)
                    {
                        projectWithConfig.setDepartmentSalesOpportunity(projectWithConfig.getSalesOpportunity().getDepartment_sales_opportunity());
                    } else {
                        projectWithConfig.setDepartmentSalesOpportunity(getDepartmentSalesOpportunity(companyId));
                    }

                    projectWithConfig.setConfigCurrency(mDb.settingDao().getConfigCurrencyById(project.getCurrency_id()));
                    projectWithConfig.setCurrencyRate(mDb.settingDao().getCurrencyRateById(project.getCurrency_rate_id()));
                    projectWithConfig.setCustomer(mDb.recordDao().getCustomerById(project.getCustomer_id()));
                    projectWithConfig.setParticipants(mDb.recordDao().getParticipantByParent(project.getId()));
                    projectWithConfig.setContacters(mDb.recordDao().getContacterByParent(project.getId()));
                    projectWithConfig.setFiles(mDb.recordDao().getFileByParent(project.getId()));
                    emitter.onNext(projectWithConfig);
                }
                emitter.onComplete();
            }
        }, BACK_PRESSURE_STRATEGY).subscribeOn(SCHEDULER);
    }

    /*--------------------------------------參與人-------------------------------------------*/
    public Single<String> saveParticipant(final Participant participant) {
        return Single.create(new SingleOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<String> emitter)
                    throws
                    Exception {
                String participantId = participant.getId();
                if (TextUtils.isEmpty(participantId)) {
                    participantId = generateHashId(Participant.class.getSimpleName());
                    participant.setId(participantId);
                }
                participant.setUpdated_at(new Date());
                if (mDb.recordDao().saveParticipant(participant) == -1L) {
                    emitter.onError(new Throwable("save participants failed."));
                } else {
                    emitter.onSuccess(participant.getId());
                }
            }
        }).subscribeOn(Schedulers.single());
    }

    private boolean saveParticipant(String tripId, String parentId, ParentType type, List<Participant> participants) {
        if (participants == null)
            return true;
        for (Participant participant : participants) {
            if (TextUtils.isEmpty(participant.getId())) {
                participant.setId(generateHashId(participant.getClass().getSimpleName()));
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            participant.setUpdated_at(new Date());
            participant.setTrip_id(tripId);
            participant.setParent_id(parentId);
            participant.setParent_type(type);

        }
        return !mDb.recordDao().saveParticipant(participants).contains(-1L);
    }

    /**
     * 取得客戶的參與人
     */
    public Flowable<ParticipantWithUser> getParticipantByCustomer(final String customerId) {
        return Flowable.create(new FlowableOnSubscribe<ParticipantWithUser>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<ParticipantWithUser> emitter) throws
                    Exception {
                List<Participant> participants = mDb.recordDao().getParticipantByCustomer(customerId);
                for (Participant participant : participants) {
                    ParticipantWithUser participantWithUser = new ParticipantWithUser();
                    participantWithUser.setParticipant(participant);
                    User user = mDb.settingDao().getUserById(participant.getUser_id());
                    if (user == null) {
                        Logger.e(TAG, "participant has null user::" + participant.getId());
                        Logger.e(TAG, "user_id:" + participant.getUser_id());
                    } else {
                        participantWithUser.setUser(user);
                        emitter.onNext(participantWithUser);
                    }
                }
                emitter.onComplete();
            }
        }, BACK_PRESSURE_STRATEGY).subscribeOn(SCHEDULER);
    }

    /**
     * 取得專案的參與人
     */
    public Flowable<ParticipantWithUser> getParticipantByParent(final String parentId) {
        return Flowable.create(new FlowableOnSubscribe<ParticipantWithUser>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<ParticipantWithUser> emitter) throws
                    Exception {
                List<Participant> participants = mDb.recordDao().getParticipantByParent(parentId);
                for (Participant participant : participants) {
                    ParticipantWithUser participantWithUser = new ParticipantWithUser();
                    participantWithUser.setParticipant(participant);
                    participantWithUser.setUser(mDb.settingDao().getUserById(participant.getUser_id()));
                    emitter.onNext(participantWithUser);
                }
                emitter.onComplete();
            }
        }, BACK_PRESSURE_STRATEGY).subscribeOn(SCHEDULER);
    }

    /*--------------------------------------聯絡人-------------------------------------------*/

    /**
         * 儲存客戶聯絡人
         */
    public Single<String> saveContactPerson(final ContactPerson contact, final List<File> files) {
        return Single.create(new SingleOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<String> emitter) throws Exception {
                String contactId = contact.getId();
                if (TextUtils.isEmpty(contactId)) {
                    contactId = generateHashId(contact.getClass().getSimpleName());
                    contact.setId(contactId);
                }
                contact.setUpdated_at(new Date());

                if (mDb.recordDao().saveContactPerson(contact) != -1L) {
                    saveFile(contactId, ParentType.CONTACT_PERSON, files);
                    emitter.onSuccess(contactId);
                } else {
                    emitter.onError(new Throwable("save contact person failed."));
                }
            }
        }).subscribeOn(Schedulers.single());
    }

    /**
     * 儲存聯絡人
     */
    private boolean saveContacter(String parentId, ParentType type, List<Contacter> contacters) {
        for (Contacter contacter : contacters) {
            if (TextUtils.isEmpty(contacter.getId())) {
                contacter.setId(generateHashId(contacter.getClass().getSimpleName()));
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            contacter.setUpdated_at(new Date());
            contacter.setParent_id(parentId);
            contacter.setParent_type(type);
        }
        return !mDb.recordDao().saveContacter(contacters).contains(-1L);
    }

    /**
     * 依客戶取得客戶聯絡人
     */
    public Single<ContactPerson> getContactPersonById(final String id) {
        return Single.create(new SingleOnSubscribe<ContactPerson>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<ContactPerson> emitter) throws
                    Exception {
                ContactPerson person = mDb.recordDao().getContactPersonById(id);
                if (person == null)
                    emitter.onError(new Throwable(""));
                else
                    emitter.onSuccess(person);
            }
        }).subscribeOn(SCHEDULER);
    }

    /**
     * 依客戶取得客戶聯絡人
     */
    public Flowable<ContactPerson> getContactPersonByCustomer(final String customerId) {
        return Flowable.create(new FlowableOnSubscribe<ContactPerson>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<ContactPerson> emitter) throws
                    Exception {
                List<ContactPerson> persons = mDb.recordDao().getContactPersonByCustomer(customerId);
                for (ContactPerson person : persons) {
                    emitter.onNext(person);
                }
                emitter.onComplete();
            }
        }, BACK_PRESSURE_STRATEGY).subscribeOn(SCHEDULER);
    }

    /**
     * 取得專案的聯絡人
     */
    public Flowable<ContacterWithPerson> getContacterByParent(final String parentId) {
        return Flowable.create(new FlowableOnSubscribe<ContacterWithPerson>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<ContacterWithPerson> emitter) throws
                    Exception {
                List<Contacter> contacters = mDb.recordDao().getContacterByParent(parentId);
                for (Contacter contacter : contacters) {
                    ContacterWithPerson contacterWithPerson = new ContacterWithPerson();
                    contacterWithPerson.setContacter(contacter);
                    contacterWithPerson.setContactPerson(mDb.recordDao().getContactPersonById(contacter.getUser_id()));
                    emitter.onNext(contacterWithPerson);
                }
                emitter.onComplete();
            }
        }, BACK_PRESSURE_STRATEGY).subscribeOn(SCHEDULER);
    }

    /*--------------------------------------行程-------------------------------------------*/

    /**
     * 儲存行程提交狀態
     */
    public Completable saveTripSubmit(final MultipleSubmit submit) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(@NonNull CompletableEmitter e) throws Exception {
                if (TextUtils.isEmpty(submit.getTrip_ids())) {
                    e.onError(new Throwable("save submit failed:trip_ids is null!"));
                } else {

                    List<String> tripIdList = Arrays.asList(StringUtils.split(submit.getTrip_ids(), ","));
                    for (String tripId : tripIdList) {
                        Trip trip = mDb.recordDao().getTripById(tripId);
                        if (trip != null) {
                            trip.setSubmit(SubmitStatus.SUBMITTED);
                            mDb.recordDao().saveTrip(trip);
                        }
                    }
                    e.onComplete();
                }
            }
        }).subscribeOn(SCHEDULER);
    }

    /**
     * 取消行程
     */
    public Single<String> cancelTrip(final Trip trip, final String reason) {
        return Single.create(new SingleOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<String> emitter) throws Exception {
                trip.setStatus(TripStatus.CANCEL);
                trip.setReason(reason);
                trip.setUpdated_at(new Date());
                if (mDb.recordDao().saveTrip(trip) == -1)
                {
                    emitter.onError(new Throwable("save trip failed."));
                } else {
                    TripStatusLog log = new TripStatusLog();
                    log.setId(generateHashId(log.getClass().getSimpleName()));
                    log.setUpdated_at(new Date());
                    log.setReason(reason);
                    log.setStatus(TripStatus.CANCEL.getValue());
                    log.setFrom_date(trip.getFrom_date());
                    log.setTo_date(trip.getTo_date());
                    log.setTrip_id(trip.getId());

                    if (mDb.recordDao().saveTripStatusLog(log) == -1)
                        emitter.onError(new Throwable("save TripStatusLog failed."));
                    else
                        emitter.onSuccess(reason);
                }
            }
        }).subscribeOn(Schedulers.single());
    }

    /**
     * 延後行程
     */
    public Single<String> delayTrip(final Trip trip, final String reason) {
        return Single.create(new SingleOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<String> emitter) throws Exception {
                trip.setUpdated_at(new Date());
                trip.setStatus(TripStatus.DELAY);
                trip.setReason(reason);

                if (mDb.recordDao().saveTrip(trip) == -1) {
                    emitter.onError(new Throwable("save trip failed."));
                } else {
                    TripStatusLog log = new TripStatusLog();
                    log.setId(generateHashId(log.getClass().getSimpleName()));
                    log.setUpdated_at(new Date());
                    log.setReason(reason);
                    log.setStatus(TripStatus.DELAY.getValue());
                    log.setFrom_date(trip.getFrom_date());
                    log.setTo_date(trip.getTo_date());
                    log.setTrip_id(trip.getId());
                    if (mDb.recordDao().saveTripStatusLog(log) == -1)
                        emitter.onError(new Throwable("save TripStatusLog failed."));
                    else
                        emitter.onSuccess(reason);
                }
            }
        }).subscribeOn(Schedulers.single());
    }

    public Single<String> saveTrip(final Trip trip) {
        return Single.create(new SingleOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<String> emitter) throws Exception {
                String tripId = trip.getId();
                if (TextUtils.isEmpty(tripId)) {
                    tripId = generateHashId(Trip.class.getSimpleName());
                    trip.setId(tripId);
                }
                trip.setUpdated_at(new Date());
                if (mDb.recordDao().saveTrip(trip) == -1)
                {
                    emitter.onError(new Throwable("save trip failed."));
                } else {
                    emitter.onSuccess(trip.getId());
                }
            }
        }).subscribeOn(Schedulers.single());
    }

    /**
     * 依ID取得行程
     */
    public Single<Trip> getTripById(final String id) {
        return Single.create(new SingleOnSubscribe<Trip>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<Trip> emitter) throws Exception {
                Trip trip = mDb.recordDao().getTripById(id);
                if (trip == null)
                    emitter.onError(new Throwable("can't get trip."));
                else
                    emitter.onSuccess(trip);
            }
        }).subscribeOn(Schedulers.single());
    }

    /**
     * 搜尋行程
     */
    public Flowable<MainTrip> searchTrip(final String keyword, final TripType type, final Date start, final Date end, final String departmentId) {
        return Flowable.create(new FlowableOnSubscribe<MainTrip>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<MainTrip> emitter) throws Exception {
                ArrayList<Integer> types = new ArrayList<>();
                if (type == null) {
                    types.add(TripType.VISIT.getValue());
                    types.add(TripType.OFFICE.getValue());
                    types.add(TripType.TASK.getValue());
                    types.add(TripType.ABSENT.getValue());
                    types.add(TripType.CONTRACT.getValue());
                    types.add(TripType.QUOTATION.getValue());
                    types.add(TripType.REIMBURSEMENT.getValue());
                    types.add(TripType.SAMPLE.getValue());
                    types.add(TripType.SPECIAL_PRICE.getValue());
                    types.add(TripType.PRODUCTION.getValue());
                    types.add(TripType.NON_STANDARD_INQUIRY.getValue());
                    types.add(TripType.SPRING_RING_INQUIRY.getValue());
                    types.add(TripType.SPECIAL_SHIP.getValue());
                    types.add(TripType.REPAYMENT.getValue());
                    types.add(TripType.EXPRESS.getValue());
                    types.add(TripType.TRAVEL.getValue());
                } else {
                    types.add(type.getValue());
                }
                List<Trip> trips = mDb.recordDao().searchTrip(types, keyword, start, end);
                for (Trip trip : trips) {
                    MainTrip mainTrip = new MainTrip();
                    mainTrip.setTrip(trip);
                    mainTrip.setParticipants(mDb.recordDao().getParticipantByTrip(trip.getId()));
                    mainTrip.setCustomerName(mDb.recordDao().getCustomerNameById(trip.getCustomer_id()));
                    User user = mDb.recordDao().getUserById(trip.getUser_id());

                    if (user != null)
                        if (TextUtils.isEmpty(departmentId)) {
                            mainTrip.setUserName(user.getShowName());
                            emitter.onNext(mainTrip);
                        } else {
                            if (user.getDepartment_id().equals(departmentId)) {
                                mainTrip.setUserName(user.getShowName());
                                emitter.onNext(mainTrip);
                            }
                        }
                }
                emitter.onComplete();
            }
        }, BACK_PRESSURE_STRATEGY).subscribeOn(SCHEDULER);
    }

    /**
     * 依客戶取得行程
     */
    public Flowable<Trip> getTripByCustomer(final String customerId) {
        return Flowable.create(new FlowableOnSubscribe<Trip>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<Trip> emitter) throws Exception {
                List<Trip> trips = mDb.recordDao().getTripByCustomer(customerId);
                for (Trip trip : trips) {
                    emitter.onNext(trip);
                }
                emitter.onComplete();
            }
        }, BACK_PRESSURE_STRATEGY).subscribeOn(SCHEDULER);
    }

    /**
     * 依工作項目取得行程
     */
    public Flowable<Trip> getTripByProject(final String projectId) {
        return Flowable.create(new FlowableOnSubscribe<Trip>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<Trip> emitter) throws Exception {
                List<Trip> trips = mDb.recordDao().getTripByProject(projectId);
                for (Trip trip : trips) {
                    emitter.onNext(trip);
                }
                emitter.onComplete();
            }
        }, BACK_PRESSURE_STRATEGY).subscribeOn(SCHEDULER);
    }

    public Flowable<MainTrip> getInviteTrip(final String userId, final Date date) {
        return Flowable.create(new FlowableOnSubscribe<MainTrip>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<MainTrip> emitter) throws Exception {
                List<Trip> tripList = mDb.recordDao().getTripInvite(userId, date);
                for (Trip trip : tripList) {
                    if (trip != null) {
                        MainTrip mainTrip = new MainTrip();
                        mainTrip.setTrip(trip);
                        mainTrip.setParticipants(mDb.recordDao().getParticipantByTrip(trip.getId()));
                        mainTrip.setCustomerName(mDb.recordDao().getCustomerNameById(trip.getCustomer_id()));
                        mainTrip.setUserName(mDb.recordDao().getUserById(trip.getUser_id()).getShowName());
                        emitter.onNext(mainTrip);
                    }
                }
                emitter.onComplete();
            }
        }, BACK_PRESSURE_STRATEGY).subscribeOn(SCHEDULER);
    }

    public Flowable<MainTrip> getNotSunmittedTrip(final String userId) {
        return Flowable.create(new FlowableOnSubscribe<MainTrip>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<MainTrip> emitter) throws Exception {
                ArrayList<Integer> types = new ArrayList<>();
                types.add(TripType.ABSENT.getValue());
                types.add(TripType.CONTRACT.getValue());
                types.add(TripType.QUOTATION.getValue());
                types.add(TripType.REIMBURSEMENT.getValue());
                types.add(TripType.SAMPLE.getValue());
                types.add(TripType.SPECIAL_PRICE.getValue());
                types.add(TripType.PRODUCTION.getValue());
                types.add(TripType.NON_STANDARD_INQUIRY.getValue());
                types.add(TripType.SPRING_RING_INQUIRY.getValue());
                types.add(TripType.EXPRESS.getValue());
                types.add(TripType.TRAVEL.getValue());
                types.add(TripType.SPECIAL_SHIP.getValue());
                List<Trip> trips = mDb.recordDao().getNotSubmittedTrip(types, userId);

                for (Trip trip : trips) {
                    if (trip == null)
                        continue;
                    MainTrip mainTrip = new MainTrip();
                    mainTrip.setTrip(trip);
                    mainTrip.setParticipants(mDb.recordDao().getParticipantByTrip(trip.getId()));
                    mainTrip.setCustomerName(mDb.recordDao().getCustomerNameById(trip.getCustomer_id()));
                    mainTrip.setUserName(mDb.recordDao().getUserById(trip.getUser_id()).getShowName());
                    emitter.onNext(mainTrip);
                }
                emitter.onComplete();
            }
        }, BACK_PRESSURE_STRATEGY).subscribeOn(SCHEDULER);
    }

    /**
     * 取得行事曆行程
     */
    public Flowable<MainTrip> getMainTrip(final Date date, final String parentId, final ReportFilterType type) {
        return Flowable.create(new FlowableOnSubscribe<MainTrip>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<MainTrip> emitter) throws Exception {
                Calendar calendar = Calendar.getInstance(Locale.getDefault());
                calendar.setTime(date);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                Date start = calendar.getTime();
                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.MINUTE, 59);
                calendar.set(Calendar.SECOND, 59);
                Date end = calendar.getTime();
                List<Trip> userTrips = null;
                List<Trip> participantTrips = null;
                switch (type) {
                    case PERSON: {
                        userTrips = mDb.recordDao().getTripByDateAndUser(start, end, parentId);
                        participantTrips = mDb.recordDao().getTripByDateAndParticipant(start, end, parentId);
                        break;
                    }
                    case DEPARTMENT: {
                        userTrips = mDb.recordDao().getTripByDateAndUserDepartment(start, end, parentId);
                        participantTrips = mDb.recordDao().getTripByDateAndParticipantDepartment(start, end, parentId);
                        break;
                    }
                    case COMPANY: {
                        if (parentId.equals("")) {
                            userTrips = mDb.recordDao().getTripByDate(start, end);
                            participantTrips = new ArrayList<>();
                        } else {
                            userTrips = mDb.recordDao().getTripByDateAndUserCompany(start, end, parentId);
                            participantTrips = mDb.recordDao().getTripByDateAndParticipantCompany(start, end, parentId);
                        }
                        break;
                    }
                }
                Set<Trip> tripSet = new HashSet<>(userTrips);
                tripSet.addAll(participantTrips);
                ArrayList<Trip> result = new ArrayList<>(tripSet);
                Collections.sort(result, new Comparator<Trip>() {
                    @Override
                    public int compare(Trip trip1, Trip trip2) {
                        return trip1.getFrom_date().compareTo(trip2.getFrom_date());
                    }
                });

                loop:

                for (Trip trip : result) {
                    switch (trip.getType()) {
                        case VISIT: {
                            if (mDb.recordDao().getVisitByTrip(trip.getId()) == null) {
                                continue loop;
                            }
                            break;
                        }
                        case OFFICE: {
                            if (mDb.recordDao().getOfficeByTrip(trip.getId()) == null) {
                                continue loop;
                            }
                            break;
                        }
                        case TASK: {
                            if (mDb.recordDao().getTaskByTrip(trip.getId()) == null) {
                                continue loop;
                            }
                            break;
                        }
                        case ABSENT: {
                            if (mDb.recordDao().getAbsentByTrip(trip.getId()) == null) {
                                continue loop;
                            }
                            break;
                        }
                        case QUOTATION: {
                            if (mDb.recordDao().getQuotationByTrip(trip.getId()) == null) {
                                continue loop;
                            }
                            break;
                        }
                        case CONTRACT: {
                            if (mDb.recordDao().getContractByTrip(trip.getId()) == null) {
                                continue loop;
                            }
                            break;
                        }
                        case REIMBURSEMENT: {
                            if (mDb.recordDao().getReimbursementByTrip(trip.getId()) == null) {
                                continue loop;
                            }
                            break;
                        }
                        case SAMPLE: {
                            if (mDb.recordDao().getSampleByTrip(trip.getId()) == null) {
                                continue loop;
                            }
                            break;
                        }
                        case SPECIAL_PRICE: {
                            if (mDb.recordDao().getSpecialPriceByTrip(trip.getId()) == null) {
                                continue loop;
                            }
                            break;
                        }
                        case PRODUCTION: {
                            if (mDb.recordDao().getNewProjectProductionByTrip(trip.getId()) == null) {
                                continue loop;
                            }
                            break;
                        }
                        case NON_STANDARD_INQUIRY: {
                            if (mDb.recordDao().getNonStandardInquiryByTrip(trip.getId()) == null) {
                                continue loop;
                            }
                            break;
                        }
                        case SPRING_RING_INQUIRY: {
                            if (mDb.recordDao().getSpringRingInquiryByTrip(trip.getId()) == null) {
                                continue loop;
                            }
                            break;
                        }
                        case EXPRESS: {
                            if (mDb.recordDao().getExpressByTrip(trip.getId()) == null) {
                                continue loop;
                            }
                            break;
                        }
                        case TRAVEL: {
                            if (mDb.recordDao().getTravelByTrip(trip.getId()) == null) {
                                continue loop;
                            }
                            break;
                        }
                    }
                    List<Participant> participants = mDb.recordDao().getParticipantByTrip(trip.getId());
                    MainTrip mainTrip = new MainTrip();
                    mainTrip.setTrip(trip);
                    mainTrip.setParticipants(participants);
                    mainTrip.setCustomerName(mDb.recordDao().getCustomerNameById(trip.getCustomer_id()));

                    if (!TextUtils.isEmpty(trip.getUser_id()))
                        mainTrip.setUserName(mDb.recordDao().getUserById(trip.getUser_id()).getShowName());
                    else
                        mainTrip.setUserName("");
                    emitter.onNext(mainTrip);
//                    }
                }
                emitter.onComplete();
            }
        }, BACK_PRESSURE_STRATEGY).subscribeOn(Schedulers.single());
    }

    public Single<Boolean> getHasEmptyReportByDate(final Date date, final String userId) {
        return Single.create(new SingleOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<Boolean> emitter) throws Exception {
                Calendar calendar = Calendar.getInstance(Locale.getDefault());
                calendar.setTime(date);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                Date start = calendar.getTime();
                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.MINUTE, 59);
                calendar.set(Calendar.SECOND, 59);
                Date end = calendar.getTime();

                List<Visit> visitList = mDb.recordDao().getVisitByDate(start, end, userId);
                boolean hasEmptyReport = false;
                for (Visit item : visitList) {
                    if (mDb.recordDao().getReportByParent(item.trip_id, userId) == null) {
                        hasEmptyReport = true;
                        break;
                    }
                }
                emitter.onSuccess(hasEmptyReport);
            }
        }).subscribeOn(Schedulers.single());
    }

    /**
     * 取得該日行程數量
     */
    public Single<Integer> getTripCount(final Date date, final String userId) {
        return Single.create(new SingleOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<Integer> emitter) throws Exception {
                Calendar calendar = Calendar.getInstance(Locale.getDefault());
                calendar.setTime(date);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                Date start = calendar.getTime();
                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.MINUTE, 59);
                calendar.set(Calendar.SECOND, 59);
                Date end = calendar.getTime();

                int count = 0;
                List<Trip> userTrips = mDb.recordDao().getTripByDateAndUser(start, end, userId);
                List<Trip> participantTrips = mDb.recordDao().getTripByDateAndParticipant(start, end, userId);
                Set<Trip> tripSet = new HashSet<>(userTrips);
                tripSet.addAll(participantTrips);

                loop:
                for (Trip trip : tripSet) {
                    switch (trip.getType()) {
                        case VISIT: {
                            if (mDb.recordDao().getVisitByTrip(trip.getId()) == null) {
                                continue loop;
                            }
                            break;
                        }
                        case OFFICE: {
                            if (mDb.recordDao().getOfficeByTrip(trip.getId()) == null) {
                                continue loop;
                            }
                            break;
                        }
                        case TASK: {
                            if (mDb.recordDao().getTaskByTrip(trip.getId()) == null) {
                                continue loop;
                            }
                            break;
                        }
                        case ABSENT: {
                            if (mDb.recordDao().getAbsentByTrip(trip.getId()) == null) {
                                continue loop;
                            }
                            break;
                        }
                        case QUOTATION: {
                            if (mDb.recordDao().getQuotationByTrip(trip.getId()) == null) {
                                continue loop;
                            }
                            break;
                        }
                        case CONTRACT: {
                            if (mDb.recordDao().getContractByTrip(trip.getId()) == null) {
                                continue loop;
                            }
                            break;
                        }
                        case REIMBURSEMENT: {
                            if (mDb.recordDao().getReimbursementByTrip(trip.getId()) == null) {
                                continue loop;
                            }
                            break;
                        }
                        case SAMPLE: {
                            if (mDb.recordDao().getSampleByTrip(trip.getId()) == null) {
                                continue loop;
                            }
                            break;
                        }
                        case SPECIAL_PRICE: {
                            if (mDb.recordDao().getSpecialPriceByTrip(trip.getId()) == null) {
                                continue loop;
                            }
                            break;
                        }
                        case PRODUCTION: {
                            if (mDb.recordDao().getNewProjectProductionByTrip(trip.getId()) == null) {
                                continue loop;
                            }
                            break;
                        }
                        case NON_STANDARD_INQUIRY: {
                            if (mDb.recordDao().getNonStandardInquiryByTrip(trip.getId()) == null) {
                                continue loop;
                            }
                            break;
                        }
                        case SPRING_RING_INQUIRY: {
                            if (mDb.recordDao().getSpringRingInquiryByTrip(trip.getId()) == null) {
                                continue loop;
                            }
                            break;
                        }
                        case EXPRESS: {
                            if (mDb.recordDao().getExpressByTrip(trip.getId()) == null) {
                                continue loop;
                            }
                            break;
                        }
                        case TRAVEL: {
                            if (mDb.recordDao().getTravelByTrip(trip.getId()) == null) {
                                continue loop;
                            }
                            break;
                        }
                    }
                    count++;
                }
                emitter.onSuccess(count);
            }
        }).subscribeOn(Schedulers.single());
    }

    /**
     * 取得該日行程數量
     */
    public Single<Integer> getTripCount(final Date date, final String parentId, final ReportFilterType type) {
        return Single.create(new SingleOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<Integer> emitter) throws Exception {
                Calendar calendar = Calendar.getInstance(Locale.getDefault());
                calendar.setTime(date);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                Date start = calendar.getTime();
                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.MINUTE, 59);
                calendar.set(Calendar.SECOND, 59);
                Date end = calendar.getTime();

                int count = 0;
                List<Trip> userTrips = null;
                List<Trip> participantTrips = null;
                switch (type) {
                    case PERSON: {
                        userTrips = mDb.recordDao().getTripByDateAndUser(start, end, parentId);
                        participantTrips = mDb.recordDao().getTripByDateAndParticipant(start, end, parentId);
                        break;
                    }
                    case DEPARTMENT: {
                        userTrips = mDb.recordDao().getTripByDateAndUserDepartment(start, end, parentId);
                        participantTrips = mDb.recordDao().getTripByDateAndParticipantDepartment(start, end, parentId);
                        break;
                    }
                    case COMPANY: {
                        if (parentId.equals("")) {
                            userTrips = mDb.recordDao().getTripByDate(start, end);
                            participantTrips = new ArrayList<>();
                        } else {
                            userTrips = mDb.recordDao().getTripByDateAndUserCompany(start, end, parentId);
                            participantTrips = mDb.recordDao().getTripByDateAndParticipantCompany(start, end, parentId);
                        }
                        break;
                    }
                }
                Set<Trip> tripSet = new HashSet<>(userTrips);
                tripSet.addAll(participantTrips);
                loop:
                for (Trip trip : tripSet) {
                    switch (trip.getType()) {
                        case VISIT: {
                            if (mDb.recordDao().getVisitByTrip(trip.getId()) == null) {
                                continue loop;
                            }
                            break;
                        }
                        case OFFICE: {
                            if (mDb.recordDao().getOfficeByTrip(trip.getId()) == null) {
                                continue loop;
                            }
                            break;
                        }
                        case TASK: {
                            if (mDb.recordDao().getTaskByTrip(trip.getId()) == null) {
                                continue loop;
                            }
                            break;
                        }
                        case ABSENT: {
                            if (mDb.recordDao().getAbsentByTrip(trip.getId()) == null) {
                                continue loop;
                            }
                            break;
                        }
                        case QUOTATION: {
                            if (mDb.recordDao().getQuotationByTrip(trip.getId()) == null) {
                                continue loop;
                            }
                            break;
                        }
                        case CONTRACT: {
                            if (mDb.recordDao().getContractByTrip(trip.getId()) == null) {
                                continue loop;
                            }
                            break;
                        }
                        case REIMBURSEMENT: {
                            if (mDb.recordDao().getReimbursementByTrip(trip.getId()) == null) {
                                continue loop;
                            }
                            break;
                        }
                        case SAMPLE: {
                            if (mDb.recordDao().getSampleByTrip(trip.getId()) == null) {
                                continue loop;
                            }
                            break;
                        }
                        case SPECIAL_PRICE: {
                            if (mDb.recordDao().getSpecialPriceByTrip(trip.getId()) == null) {
                                continue loop;
                            }
                            break;
                        }
                        case PRODUCTION: {
                            if (mDb.recordDao().getNewProjectProductionByTrip(trip.getId()) == null) {
                                continue loop;
                            }
                            break;
                        }
                        case NON_STANDARD_INQUIRY: {
                            if (mDb.recordDao().getNonStandardInquiryByTrip(trip.getId()) == null) {
                                continue loop;
                            }
                            break;
                        }
                        case SPRING_RING_INQUIRY: {
                            if (mDb.recordDao().getSpringRingInquiryByTrip(trip.getId()) == null) {
                                continue loop;
                            }
                            break;
                        }
                        case EXPRESS: {
                            if (mDb.recordDao().getExpressByTrip(trip.getId()) == null) {
                                continue loop;
                            }
                            break;
                        }
                        case TRAVEL: {
                            if (mDb.recordDao().getTravelByTrip(trip.getId()) == null) {
                                continue loop;
                            }
                            break;
                        }
                    }
                    count++;
                }
                emitter.onSuccess(count);
            }
        }).subscribeOn(Schedulers.single());
    }
    /*--------------------------------------工作報告-------------------------------------------*/

    /**
     * 取得月計畫
     */
    public Single<ReportSummary> getMonthReportSummary(final Date start, final Date end, final String userId) {
        return Single.create(new SingleOnSubscribe<ReportSummary>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<ReportSummary> emitter) throws Exception {
                List<Trip> trips = mDb.recordDao().getTripByDate(start, end);
                emitter.onSuccess(tripToReportSummary(trips, start, end, userId, ReportFilterType.PERSON));
            }
        }).subscribeOn(Schedulers.single());
    }

    /**
     * 取得週計畫
     */
    public Single<ReportSummary> getWeekReport(final Date date, final String parentId, final ReportFilterType type) {
        return Single.create(new SingleOnSubscribe<ReportSummary>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<ReportSummary> emitter) throws Exception {
                Calendar calendar = Calendar.getInstance(Locale.getDefault());
                calendar.setTime(date);
                calendar.set(Calendar.DAY_OF_WEEK, 1);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                Date start = calendar.getTime();
                calendar.set(Calendar.DAY_OF_WEEK, 7);
                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.MINUTE, 59);
                calendar.set(Calendar.SECOND, 59);
                Date end = calendar.getTime();

                List<Trip> userTrips = null;
                List<Trip> participantTrips = null;
                switch (type) {
                    case PERSON: {
                        userTrips = mDb.recordDao().getTripByDateAndUser(start, end, parentId);
                        participantTrips = mDb.recordDao().getTripByDateAndParticipant(start, end, parentId);
                        break;
                    }
                    case DEPARTMENT: {
                        userTrips = mDb.recordDao().getTripByDateAndUserDepartment(start, end, parentId);
                        participantTrips = mDb.recordDao().getTripByDateAndParticipantDepartment(start, end, parentId);
                        break;
                    }
                    case COMPANY: {
                        if (parentId.equals("")) {
                            userTrips = mDb.recordDao().getTripByDate(start, end);
                            participantTrips = new ArrayList<>();
                        } else {
                            userTrips = mDb.recordDao().getTripByDateAndUserCompany(start, end, parentId);
                            participantTrips = mDb.recordDao().getTripByDateAndParticipantCompany(start, end, parentId);
                        }
                        break;
                    }
                }
                Set<Trip> tripSet = new HashSet<>(userTrips);
                tripSet.addAll(participantTrips);
                emitter.onSuccess(tripToReportSummary(new ArrayList<>(tripSet), start, end, parentId, type));
            }
        }).subscribeOn(Schedulers.single());
    }

    private ReportSummary tripToReportSummary(List<Trip> trips, Date start, Date end, String parentId, ReportFilterType type) {
        int winAmount = 0;
        int contractAmount = 0;
        int lose = 0;
        int signIn = 0;
        int signOut = 0;
        int taskCreate = 0;
        int opportunityCreate = 0;

        List<SalesOpportunity> salesOpportunityList = null;
        switch (type) {
            case PERSON: {
                salesOpportunityList = mDb.recordDao().getSalesOpportunityByPeriod(start, end, parentId);
                break;
            }
            case DEPARTMENT: {
                salesOpportunityList = mDb.recordDao().getDepartemntSalesOpportunityByPeriod(start, end, parentId);
                break;
            }
            case COMPANY: {
                if (parentId.equals("")) {
                    salesOpportunityList = mDb.recordDao().getSalesOpportunityByPeriod(start, end);
                } else {
                    salesOpportunityList = mDb.recordDao().getCompanySalesOpportunityByPeriod(start, end, parentId);
                }
                break;
            }
        }
        if (salesOpportunityList != null) {
            opportunityCreate = salesOpportunityList.size();
            Logger.e(TAG, "銷售機會數量:" + opportunityCreate);
            String projectId = "";
            for (SalesOpportunity opportunity : salesOpportunityList) {
                if (opportunity != null) {
                    /*業績*/
                    if (!TextUtils.isEmpty(opportunity.getProject_id()) && !projectId.equals(opportunity.getProject_id())) {
                        projectId = opportunity.getProject_id();
                        if (opportunity.getPercentage() < 0) {
                            lose++;
                        } else if (opportunity.getPercentage() == 100) {
                            Project project = mDb.recordDao().getAuthorityProjectById(TokenManager.getInstance().getUser().getId(), opportunity.getProject_id());
                            if (project != null)
                                winAmount += project.getExpect_amount();
                        }
                    }
                }
            }
        }

        for (Trip trip : trips) {
            List<Participant> participants = mDb.recordDao().getParticipantByTrip(trip.getId());
            /*業績*/
            if (trip.getType() == TripType.CONTRACT) {
                Contract contract = mDb.recordDao().getContractByTrip(trip.getId());
                if (contract != null) {
                    contractAmount += contract.getAmount();
                }
            }
            /*行為*/
            if (participants != null)
                for (Participant item : participants) {
                    switch (type) {
                        case PERSON: {
                            if (item.getUser_id().equals(parentId)) {
                                if (item.getSign_in_at() != null && item.getSign_in_at().after(start) && item.getSign_in_at().before(end))
                                {
                                    signIn++;
                                }

                                if (item.getSign_out_at() != null && item.getSign_out_at().after(start) && item.getSign_out_at().before(end))
                                {
                                    signOut++;
                                }
                                break;
                            }
                            break;
                        }
                        case COMPANY:
                        case DEPARTMENT: {
                            if (item.getUser_id().equals(trip.getUser_id())) {
                                if (item.getSign_in_at() != null && item.getSign_in_at().after(start) && item.getSign_in_at().before(end))
                                {
                                    signIn++;
                                }

                                if (item.getSign_out_at() != null && item.getSign_out_at().after(start) && item.getSign_out_at().before(end))
                                {
                                    signOut++;
                                }
                                break;
                            }
                            break;
                        }
                    }
                }
            /*新增*/
            if (trip.getType() == TripType.TASK)
                taskCreate++;
        }
        ReportSummary report = new ReportSummary();
        report.setWinAmount(winAmount);
        report.setcontractAmount(contractAmount);
        report.setLose(lose);
        report.setSignIn(signIn);
        report.setSignOut(signOut);
        report.setTaskCreate(taskCreate);
        report.setOpportunityCreate(opportunityCreate);
        return report;
    }

    /*--------------------------------------八大類-------------------------------------------*/
    public Single<String> saveVisit(final VisitWithConfig visit) {
        return Single.create(new SingleOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<String> emitter) throws Exception {
                String tripId = visit.getTrip().getId();
                if (TextUtils.isEmpty(tripId)) {
                    tripId = generateHashId(Trip.class.getClass().getSimpleName());
                    visit.getTrip().setId(tripId);
                }
                visit.getTrip().setUpdated_at(new Date());
                if (mDb.recordDao().saveTrip(visit.getTrip()) == -1) {
                    emitter.onError(new Throwable("save trip failed."));
                } else {
                    visit.getVisit().setTrip_id(tripId);
                    String visitId = visit.getVisit().getId();
                    if (TextUtils.isEmpty(visitId)) {
                        visitId = generateHashId(Visit.class.getClass().getSimpleName());
                        visit.getVisit().setId(visitId);
                    }
                    visit.getVisit().setUpdated_at(new Date());
                    if (mDb.recordDao().saveVisit(visit.getVisit()) != -1L) {
                        visit.getReport().setParent_id(tripId);
                        visit.getReport().setUser_id(visit.getTrip().getUser_id());
                        if (!TextUtils.isEmpty(visit.getReport().getContent()))
                            saveReport(visit.getReport());
                        saveParticipant(tripId, visitId, ParentType.VISIT, visit.getParticipants());
                        saveContacter(visitId, ParentType.VISIT, visit.getContacters());
                        saveFile(visitId, ParentType.VISIT, visit.getFiles());
                        emitter.onSuccess(visitId);
                    } else {
                        emitter.onError(new Throwable("save visit failed."));
                    }
                }
            }
        }).subscribeOn(Schedulers.single());
    }

    public Single<String> saveOffice(final OfficeWithConfig office) {
        return Single.create(new SingleOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<String> emitter) throws Exception {
                String tripId = office.getTrip().getId();
                if (TextUtils.isEmpty(tripId)) {
                    tripId = generateHashId(Trip.class.getClass().getSimpleName());
                    office.getTrip().setId(tripId);
                }
                office.getTrip().setUpdated_at(new Date());
                if (mDb.recordDao().saveTrip(office.getTrip()) == -1) {
                    emitter.onError(new Throwable("save trip failed."));
                } else {
                    office.getOffice().setTrip_id(tripId);
                    String officeId = office.getOffice().getId();
                    if (TextUtils.isEmpty(officeId)) {
                        officeId = generateHashId(Office.class.getClass().getSimpleName());
                        office.getOffice().setId(officeId);
                    }
                    office.getOffice().setUpdated_at(new Date());
                    if (mDb.recordDao().saveOffice(office.getOffice()) != -1L) {
                        saveParticipant(tripId, officeId, ParentType.OFFICE, office.getParticipants());
                        saveFile(officeId, ParentType.OFFICE, office.getFiles());
                        emitter.onSuccess(officeId);
                    } else {
                        emitter.onError(new Throwable("save visit failed."));
                    }
                }
            }
        }).subscribeOn(Schedulers.single());
    }

    public Single<String> saveTask(final TaskWithConfig task) {
        return Single.create(new SingleOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<String> emitter) throws Exception {
                String tripId = task.getTrip().getId();
                if (TextUtils.isEmpty(tripId)) {
                    tripId = generateHashId(Trip.class.getClass().getSimpleName());
                    task.getTrip().setId(tripId);
                }
                task.getTrip().setUpdated_at(new Date());
                if (mDb.recordDao().saveTrip(task.getTrip()) == -1) {
                    emitter.onError(new Throwable("save trip failed."));
                } else {
                    task.getTask().setTrip_id(tripId);
                    String taskId = task.getTask().getId();
                    if (TextUtils.isEmpty(taskId)) {
                        taskId = generateHashId(Task.class.getClass().getSimpleName());
                        task.getTask().setId(taskId);
                    }
                    task.getTask().setUpdated_at(new Date());
                    if (mDb.recordDao().saveTask(task.getTask()) != -1L) {
                        saveFile(taskId, ParentType.TASK, task.getFiles());
                        emitter.onSuccess(taskId);
                    } else {
                        emitter.onError(new Throwable("save visit failed."));
                    }
                }
            }
        }).subscribeOn(Schedulers.single());
    }

    public Single<String> saveAbsent(final AbsentWithConfig absent) {
        return Single.create(new SingleOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<String> emitter) throws Exception {
                String tripId = absent.getTrip().getId();
                if (TextUtils.isEmpty(tripId)) {
                    tripId = generateHashId(Trip.class.getClass().getSimpleName());
                    absent.getTrip().setId(tripId);
                }
                absent.getTrip().setUpdated_at(new Date());
                if (mDb.recordDao().saveTrip(absent.getTrip()) == -1) {
                    emitter.onError(new Throwable("save trip failed."));
                } else {
                    absent.getAbsent().setTrip_id(tripId);
                    String absentId = absent.getAbsent().getId();
                    if (TextUtils.isEmpty(absentId)) {
                        absentId = generateHashId(Visit.class.getClass().getSimpleName());
                        absent.getAbsent().setId(absentId);
                    }
                    absent.getAbsent().setUpdated_at(new Date());
                    if (mDb.recordDao().saveAbsent(absent.getAbsent()) != -1L) {
                        saveFile(absentId, ParentType.ABSENT, absent.getFiles());
                        emitter.onSuccess(absentId);
                    } else {
                        emitter.onError(new Throwable("save visit failed."));
                    }
                }
            }
        }).subscribeOn(Schedulers.single());
    }

    public Single<String> saveContract(final ContractWithConfig contract) {
        return Single.create(new SingleOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<String> emitter) throws Exception {
                String tripId = contract.getTrip().getId();
                if (TextUtils.isEmpty(tripId)) {
                    tripId = generateHashId(Trip.class.getClass().getSimpleName());
                    contract.getTrip().setId(tripId);
                }
                contract.getTrip().setUpdated_at(new Date());
                if (mDb.recordDao().saveTrip(contract.getTrip()) == -1) {
                    emitter.onError(new Throwable("save trip failed."));
                } else {
                    contract.getContract().setTrip_id(tripId);
                    String contractId = contract.getContract().getId();
                    if (TextUtils.isEmpty(contractId)) {
                        contractId = generateHashId(Visit.class.getClass().getSimpleName());
                        contract.getContract().setId(contractId);
                    }
                    contract.getContract().setUpdated_at(new Date());
                    if (mDb.recordDao().saveContract(contract.getContract()) != -1L) {
                        saveQuotationProduct(contractId, contract.getProducts());
                        saveContacter(contractId, ParentType.CONTRACT, contract.getContacters());
                        saveFile(contractId, ParentType.CONTRACT, contract.getFiles());
                        emitter.onSuccess(contractId);
                    } else {
                        emitter.onError(new Throwable("save visit failed."));
                    }
                }
            }
        }).subscribeOn(Schedulers.single());
    }

    public Single<String> saveSample(final SampleWithConfig sample) {
        return Single.create(new SingleOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<String> emitter) throws Exception {
                String tripId = sample.getTrip().getId();
                if (TextUtils.isEmpty(tripId)) {
                    tripId = generateHashId(Trip.class.getClass().getSimpleName());
                    sample.getTrip().setId(tripId);
                }
                sample.getTrip().setUpdated_at(new Date());
                if (mDb.recordDao().saveTrip(sample.getTrip()) == -1) {
                    emitter.onError(new Throwable("save trip failed."));
                } else {
                    sample.getSample().setTrip_id(tripId);
                    String sampleId = sample.getSample().getId();
                    if (TextUtils.isEmpty(sampleId)) {
                        sampleId = generateHashId(Sample.class.getClass().getSimpleName());
                        sample.getSample().setId(sampleId);
                    }
                    sample.getSample().setUpdated_at(new Date());
                    if (mDb.recordDao().saveSample(sample.getSample()) != -1L) {
                        saveSampleProduct(sampleId, sample.getProducts());
                        saveContacter(sampleId, ParentType.SAMPLE, sample.getContacters());
                        saveFile(sampleId, ParentType.SAMPLE, sample.getFiles());
                        emitter.onSuccess(sampleId);
                    } else {
                        emitter.onError(new Throwable("save visit failed."));
                    }
                }
            }
        }).subscribeOn(Schedulers.single());
    }

    private boolean saveSampleProduct(String parentId, List<SampleProductWithConfig> items) {
        List<SampleProduct> input = new ArrayList<>();
        for (SampleProductWithConfig item : items) {
            SampleProduct product = item.getProduct();
            if (TextUtils.isEmpty(product.getId())) {
                product.setId(generateHashId(SampleProduct.class.getSimpleName()));
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            product.setUpdated_at(new Date());
            product.setParent_id(parentId);
            input.add(product);
        }
        return !mDb.recordDao().saveSampleProduct(input).contains(-1L);
    }

    public Single<String> saveReimbursement(final ReimbursementWithConfig reimbursement) {
        return Single.create(new SingleOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<String> emitter) throws Exception {
                String tripId = reimbursement.getTrip().getId();
                if (TextUtils.isEmpty(tripId)) {
                    tripId = generateHashId(Trip.class.getClass().getSimpleName());
                    reimbursement.getTrip().setId(tripId);
                }
                reimbursement.getTrip().setUpdated_at(new Date());
                if (mDb.recordDao().saveTrip(reimbursement.getTrip()) == -1) {
                    emitter.onError(new Throwable("save trip failed."));
                } else {
                    reimbursement.getReimbursement().setTrip_id(tripId);
                    String reimbursementId = reimbursement.getReimbursement().getId();

                    if (TextUtils.isEmpty(reimbursementId)) {
                        reimbursementId = generateHashId(Reimbursement.class.getClass().getSimpleName());
                        reimbursement.getReimbursement().setId(reimbursementId);
                    }
                    reimbursement.getReimbursement().setUpdated_at(new Date());

                    if (mDb.recordDao().saveReimbursement(reimbursement.getReimbursement()) != -1L) {
                        saveReimbursementItem(reimbursementId, reimbursement.getReimbursementItems());
                        emitter.onSuccess(reimbursementId);
                    } else {
                        emitter.onError(new Throwable("save reimbursement failed."));
                    }
                }
            }
        }).subscribeOn(Schedulers.single());
    }

    public Single<String> saveQuotation(final QuotationWithConfig quotation) {
        return Single.create(new SingleOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<String> emitter) throws Exception {
                String tripId = quotation.getTrip().getId();
                if (TextUtils.isEmpty(tripId)) {
                    tripId = generateHashId(Trip.class.getClass().getSimpleName());
                    quotation.getTrip().setId(tripId);
                }
                quotation.getTrip().setUpdated_at(new Date());
                if (mDb.recordDao().saveTrip(quotation.getTrip()) == -1) {
                    emitter.onError(new Throwable("save trip failed."));
                } else {
                    quotation.getQuotation().setTrip_id(tripId);
                    String quotationId = quotation.getQuotation().getId();
                    if (TextUtils.isEmpty(quotationId)) {
                        quotationId = generateHashId(Quotation.class.getClass().getSimpleName());
                        quotation.getQuotation().setId(quotationId);
                    }
                    quotation.getQuotation().setUpdated_at(new Date());
                    if (mDb.recordDao().saveQuotation(quotation.getQuotation()) != -1L) {
                        saveContacter(quotationId, ParentType.QUOTATION, quotation.getContacters());
                        saveQuotationProduct(quotationId, quotation.getProducts());
                        saveFile(quotationId, ParentType.QUOTATION, quotation.getFiles());
                        emitter.onSuccess(quotationId);
                    } else {
                        emitter.onError(new Throwable("save visit failed."));
                    }
                }
            }
        }).subscribeOn(Schedulers.single());
    }

    public Single<VisitWithConfig> getVisitByTrip(final String tripId) {
        return Single.create(new SingleOnSubscribe<VisitWithConfig>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<VisitWithConfig> emitter) throws
                    Exception {
                VisitWithConfig result = new VisitWithConfig();
                Visit visit = mDb.recordDao().getVisitByTrip(tripId);
                if (visit == null) {
                    emitter.onError(new Throwable("can't get visit."));
                } else {
                    result.setVisit(visit);
                    Trip trip = mDb.recordDao().getTripById(visit.getTrip_id());
                    result.setTrip(trip);
                    result.setParticipants(mDb.recordDao().getParticipantByParent(visit.getId()));
                    result.setContacters(mDb.recordDao().getContacterByParent(visit.getId()));
                    result.setFiles(mDb.recordDao().getFileByParent(visit.getId()));
                    Report report = mDb.recordDao().getReportByParent(tripId, trip.getUser_id());

                    if (report != null)
                        result.setReport(report);
                    emitter.onSuccess(result);
                }
            }
        }).subscribeOn(Schedulers.single());
    }

    public Single<List<Trip>> getVisitNotReport(final String userId) {
        return Single.create(new SingleOnSubscribe<List<Trip>>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<List<Trip>> emitter) throws Exception {
                List<String> tripIdList = mDb.recordDao().getTripNotReport(userId, new Date());
                ArrayList<Trip> trips=new ArrayList<>();

                for(String tripId : tripIdList)
                {
                    Trip trip=mDb.recordDao().getTripById(tripId);
                    if(trip!=null)
                        trips.add(trip);
                }

                if (trips.size()>0) {
                    emitter.onSuccess(trips);
                } else {
                    emitter.onError(new Throwable("no trip"));
                }
            }
        }).subscribeOn(Schedulers.single());
    }

    public Single<OfficeWithConfig> getOfficeByTrip(final String tripId) {
        return Single.create(new SingleOnSubscribe<OfficeWithConfig>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<OfficeWithConfig> emitter) throws
                    Exception {
                OfficeWithConfig result = new OfficeWithConfig();
                Office office = mDb.recordDao().getOfficeByTrip(tripId);
                if (office == null) {
                    emitter.onError(new Throwable("can't get office."));
                } else {
                    result.setOffice(office);
                    Trip trip = mDb.recordDao().getTripById(office.getTrip_id());
                    result.setTrip(trip);
                    result.setParticipants(mDb.recordDao().getParticipantByParent(office.getId()));
                    result.setFiles(mDb.recordDao().getFileByParent(office.getId()));
                    result.setUserName(mDb.settingDao().getUserById(trip.getUser_id()).getShowName());
                    emitter.onSuccess(result);
                }
            }
        }).subscribeOn(Schedulers.single());
    }

    public Single<TaskWithConfig> getTaskByTrip(final String tripId) {
        return Single.create(new SingleOnSubscribe<TaskWithConfig>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<TaskWithConfig> emitter) throws
                    Exception {
                TaskWithConfig result = new TaskWithConfig();
                Task task = mDb.recordDao().getTaskByTrip(tripId);
                if (task == null) {
                    emitter.onError(new Throwable("can't get task."));
                } else {
                    result.setTask(task);
                    Trip trip = mDb.recordDao().getTripById(task.getTrip_id());
                    result.setTrip(trip);
                    result.setFiles(mDb.recordDao().getFileByParent(task.getId()));
                    emitter.onSuccess(result);
                }
            }
        }).subscribeOn(Schedulers.single());
    }

    public Single<AbsentWithConfig> getAbsentByTrip(final String tripId) {
        return Single.create(new SingleOnSubscribe<AbsentWithConfig>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<AbsentWithConfig> emitter) throws
                    Exception {
                AbsentWithConfig result = new AbsentWithConfig();
                Absent absent = mDb.recordDao().getAbsentByTrip(tripId);
                if (absent == null)
                    emitter.onError(new Throwable("can't get absent"));
                else {
                    result.setAbsent(absent);
                    Trip trip = mDb.recordDao().getTripById(absent.getTrip_id());
                    result.setTrip(trip);
                    result.setFiles(mDb.recordDao().getFileByParent(absent.getId()));
                    result.setUserName(mDb.settingDao().getUserById(trip.getUser_id()).getShowName());
                    emitter.onSuccess(result);
                }
            }
        }).subscribeOn(Schedulers.single());
    }

    private List<ReimbursementItemWithConfig> getReimbursementItemWithConfig(String parentId) {
        List<ReimbursementItemWithConfig> resultItems = new ArrayList<>();
        List<ReimbursementItem> items = mDb.recordDao().getReimbursementItemByParentId(parentId);
        for (ReimbursementItem item : items)
        {
            ReimbursementItemWithConfig result = new ReimbursementItemWithConfig();
            result.setItem(item);
            List<File> files = mDb.recordDao().getFileByParent(item.getId());

            for (File file : files)
                result.getFiles().add(file.getId());
            result.setConfig(mDb.settingDao().getConfigReimbursementItemById(item.getConfig_reimbursement_item_id()));
            resultItems.add(result);
        }
        return resultItems;
    }

    public Flowable<ReimbursementWithConfig> getReimbursement() {
        return Flowable.create(new FlowableOnSubscribe<ReimbursementWithConfig>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<ReimbursementWithConfig> emitter)
                    throws Exception {
                List<Reimbursement> reimbursements = mDb.recordDao().getReimbursement(TokenManager.getInstance().getUser().getId());
                for (Reimbursement reimbursement : reimbursements) {
                    ReimbursementWithConfig result = new ReimbursementWithConfig();
                    result.setReimbursement(reimbursement);
                    Trip trip = mDb.recordDao().getTripById(reimbursement.getTrip_id());

                    if (trip != null) {
                        result.setTrip(trip);
                        User user = mDb.settingDao().getUserById(trip.getUser_id());
                        result.setUser(user.getShowName());
                        result.setReimbursementItems(getReimbursementItemWithConfig(reimbursement.getId()));
                        emitter.onNext(result);
                    }
                }
                emitter.onComplete();
            }
        }, BACK_PRESSURE_STRATEGY).subscribeOn(SCHEDULER);
    }

    public Single<ReimbursementWithConfig> getReimbursementByTrip(final String tripId) {
        return Single.create(new SingleOnSubscribe<ReimbursementWithConfig>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<ReimbursementWithConfig> emitter) throws
                    Exception {
                ReimbursementWithConfig result = new ReimbursementWithConfig();
                Reimbursement reimbursement = mDb.recordDao().getReimbursementByTrip(tripId);

                if (reimbursement == null)
                    emitter.onError(new Throwable("can't get reimbursement"));
                else {
                    result.setReimbursement(reimbursement);
                    Trip trip = mDb.recordDao().getTripById(tripId);
                    if (trip == null) {
                        emitter.onError(new Throwable("can't get reimbursement trip"));
                    } else {
                        result.setTrip(trip);
                        User user = mDb.settingDao().getUserById(trip.getUser_id());
                        result.setUser(user.getShowName());
                        result.setReimbursementItems(getReimbursementItemWithConfig(reimbursement.getId()));
                        emitter.onSuccess(result);
                    }
                }
            }
        }).subscribeOn(Schedulers.single());
    }

    public Single<ReimbursementWithConfig> getReimbursementById(final String id, final String user_id) {
        return Single.create(new SingleOnSubscribe<ReimbursementWithConfig>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<ReimbursementWithConfig> emitter) throws
                    Exception {
                ReimbursementWithConfig result = new ReimbursementWithConfig();
                Reimbursement reimbursement = mDb.recordDao().getReimbursementById(id);
                if (reimbursement == null)
                    emitter.onError(new Throwable("can't get reimbursement"));
                else {
                    result.setReimbursement(reimbursement);
                    Trip trip = mDb.recordDao().getTripById(reimbursement.getTrip_id());
                    result.setTrip(trip);
                    User user = mDb.settingDao().getUserById(user_id);
                    result.setUser(user.getShowName());
                    result.setReimbursementItems(getReimbursementItemWithConfig(reimbursement.getId()));
                    emitter.onSuccess(result);
                }
            }
        }).subscribeOn(Schedulers.single());
    }

    public Single<ContractWithConfig> getContractByTrip(final String tripId) {
        return Single.create(new SingleOnSubscribe<ContractWithConfig>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<ContractWithConfig> emitter) throws
                    Exception {
                ContractWithConfig result = new ContractWithConfig();
                Contract contract = mDb.recordDao().getContractByTrip(tripId);
                if (contract == null)
                    emitter.onError(new Throwable("can't get contract"));
                else {
                    result.setContract(contract);
                    Trip trip = mDb.recordDao().getTripById(tripId);
                    result.setTrip(trip);
                    result.setProducts(mDb.recordDao().getContractProductByContract(contract.getId()));
                    result.setContacters(mDb.recordDao().getContacterByParent(contract.getId()));
                    result.setFiles(mDb.recordDao().getFileByParent(contract.getId()));
                    emitter.onSuccess(result);
                }
            }
        }).subscribeOn(Schedulers.single());
    }

    public Single<ContractWithConfig> getContractById(final String id) {
        return Single.create(new SingleOnSubscribe<ContractWithConfig>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<ContractWithConfig> emitter) throws
                    Exception {
                ContractWithConfig result = new ContractWithConfig();
                Contract contract = mDb.recordDao().getContractById(id);
                result.setContract(contract);
                Trip trip = mDb.recordDao().getTripById(contract.getTrip_id());
                result.setTrip(trip);
                result.setProducts(mDb.recordDao().getContractProductByContract(contract.getId()));
                result.setContacters(mDb.recordDao().getContacterByParent(contract.getId()));
                result.setFiles(mDb.recordDao().getFileByParent(contract.getId()));
                emitter.onSuccess(result);
            }
        }).subscribeOn(Schedulers.single());
    }

    public Flowable<Contract> getContractByCustomer(final String customerId) {
        return Flowable.create(new FlowableOnSubscribe<Contract>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<Contract> emitter) throws
                    Exception {
                List<Contract> contracts = mDb.recordDao().getContractByCustomer(customerId);
                for (Contract contract : contracts)
                {
                    emitter.onNext(contract);
                }
                emitter.onComplete();
            }
        }, BACK_PRESSURE_STRATEGY).subscribeOn(SCHEDULER);
    }

    public Single<SampleWithConfig> getSampleByTrip(final String tripId) {
        return Single.create(new SingleOnSubscribe<SampleWithConfig>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<SampleWithConfig> emitter) throws
                    Exception {
                SampleWithConfig result = new SampleWithConfig();
                Sample sample = mDb.recordDao().getSampleByTrip(tripId);
                if (sample == null)
                    emitter.onError(new Throwable("can't get sample"));
                else {
                    result.setSample(sample);
                    result.setTrip(mDb.recordDao().getTripById(sample.getTrip_id()));
                    result.setProducts(getSampleProductBySample(sample.getId()));
                    result.setContacters(mDb.recordDao().getContacterByParent(sample.getId()));
                    result.setFiles(mDb.recordDao().getFileByParent(sample.getId()));
                    emitter.onSuccess(result);
                }
            }
        }).subscribeOn(Schedulers.single());
    }

    public Single<SampleWithConfig> getSampleById(final String id) {
        return Single.create(new SingleOnSubscribe<SampleWithConfig>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<SampleWithConfig> emitter) throws
                    Exception {
                SampleWithConfig result = new SampleWithConfig();
                Sample sample = mDb.recordDao().getSampleById(id);
                if (sample == null) {
                    emitter.onError(new Throwable("can't get sample by id."));
                } else {
                    result.setSample(sample);
                    result.setTrip(mDb.recordDao().getTripById(sample.getTrip_id()));
                    result.setProducts(getSampleProductBySample(sample.getId()));
                    result.setContacters(mDb.recordDao().getContacterByParent(sample.getId()));
                    result.setFiles(mDb.recordDao().getFileByParent(sample.getId()));
                    emitter.onSuccess(result);
                }
            }
        }).subscribeOn(Schedulers.single());
    }

    private List<SampleProductWithConfig> getSampleProductBySample(String sample) {
        List<SampleProductWithConfig> result = new ArrayList<>();
        List<SampleProduct> productList = mDb.recordDao().getSampleProductBySample(sample);
        for (SampleProduct product : productList) {
            SampleProductWithConfig resultItem = new SampleProductWithConfig();
            resultItem.setProduct(product);
            resultItem.setCategory(mDb.settingDao().getConfigQuotationProductCategoryById(product.getProduct_category_id()));
            resultItem.setType(mDb.settingDao().getConfigSampleProductTypeById(product.getProduct_type_id()));
            for (String id : product.getProduct_report_id())
            {
                resultItem.getReports().add(mDb.settingDao().getConfigQuotationProductReportById(id));
            }
            result.add(resultItem);
        }
        return result;
    }

    public Single<QuotationWithConfig> getQuotationByTrip(final String tripId) {
        return Single.create(new SingleOnSubscribe<QuotationWithConfig>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<QuotationWithConfig> emitter) throws
                    Exception {
                QuotationWithConfig result = new QuotationWithConfig();
                Quotation quotation = mDb.recordDao().getQuotationByTrip(tripId);

                if (quotation == null)
                    emitter.onError(new Throwable("can't get quotation"));
                else {
                    result.setQuotation(quotation);
                    result.setTrip(mDb.recordDao().getTripById(tripId));
                    result.setContacters(mDb.recordDao().getContacterByParent(quotation.getId()));
                    result.setFiles(mDb.recordDao().getFileByParent(quotation.getId()));
                    result.setProducts(mDb.recordDao().getQuotationProductByQuotation(quotation.getId()));
                    emitter.onSuccess(result);
                }
            }
        }).subscribeOn(Schedulers.single());
    }

    public Flowable<Quotation> getQuotationByCustomer(final String customerId) {
        return Flowable.create(new FlowableOnSubscribe<Quotation>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<Quotation> emitter) throws
                    Exception {
                List<Quotation> quotations = mDb.recordDao().getQuotationByCustomer(customerId);
                for (Quotation quotation : quotations) {
                    emitter.onNext(quotation);
                }
                emitter.onComplete();
            }
        }, BACK_PRESSURE_STRATEGY).subscribeOn(SCHEDULER);
    }

    public Single<QuotationWithConfig> getQuotationById(final String id) {
        return Single.create(new SingleOnSubscribe<QuotationWithConfig>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<QuotationWithConfig> emitter) throws
                    Exception {
                QuotationWithConfig result = new QuotationWithConfig();
                Quotation quotation = mDb.recordDao().getQuotationById(id);
                if (quotation == null)
                    emitter.onError(new Throwable("can't get quotation"));
                else {
                    result.setQuotation(quotation);
                    result.setTrip(mDb.recordDao().getTripById(quotation.getTrip_id()));
                    result.setContacters(mDb.recordDao().getContacterByParent(quotation.getId()));
                    result.setFiles(mDb.recordDao().getFileByParent(quotation.getId()));
                    result.setProducts(mDb.recordDao().getQuotationProductByQuotation(quotation.getId()));
                    emitter.onSuccess(result);
                }
            }
        }).subscribeOn(Schedulers.single());
    }

    private boolean saveQuotationProduct(String parentId, List<QuotationProduct> products) {
        for (QuotationProduct product : products) {
            product.setParent_id(parentId);
            if (TextUtils.isEmpty(product.getId())) {
                product.setId(generateHashId(product.getClass().getSimpleName()));
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            product.setUpdated_at(new Date());
        }
        return !mDb.recordDao().saveQuotationProduct(products).contains(-1L);
    }

    private boolean saveReimbursementItem(String parentId, List<ReimbursementItemWithConfig> items) {
        List<ReimbursementItem> input = new ArrayList<>();
        for (ReimbursementItemWithConfig item : items) {
            if (item.getItem().getAmount() > 0) {
                item.getItem().setParent_id(parentId);
                if (TextUtils.isEmpty(item.getItem().getId())) {
                    item.getItem().setId(generateHashId(item.getClass().getSimpleName()));
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                item.getItem().setUpdated_at(new Date());
                input.add(item.getItem());
                ArrayList<FilePivot> filePivots = new ArrayList<>();

                for (String fileId : item.getFiles()) {
                    FilePivot filePivot = mDb.recordDao().getFilePivot(fileId, item.getItem().getId());
                    if (filePivot == null)
                        filePivot = new FilePivot();
                    if (TextUtils.isEmpty(filePivot.getId())) {
                        filePivot.setId(generateHashId(filePivot.getClass().getSimpleName()));
                        try
                        {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    filePivot.setUpdated_at(new Date());
                    filePivot.setParent_id(item.getItem().getId());
                    filePivot.setFile_id(fileId);
                    filePivots.add(filePivot);
                }
                mDb.recordDao().saveFilePivot(filePivots);
            }
        }
        return !mDb.recordDao().saveReimbursementItem(input).contains(-1L);
    }

    public Single<ProductionWithConfig> getProductionByTrip(final String tripId) {
        return Single.create(new SingleOnSubscribe<ProductionWithConfig>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<ProductionWithConfig> emitter) throws
                    Exception {
                NewProjectProduction production = mDb.recordDao().getNewProjectProductionByTrip(tripId);

                if (production == null)
                    emitter.onError(new Throwable("can't get production"));
                else {
                    ProductionWithConfig result = new ProductionWithConfig(production);
                    result.setTrip(mDb.recordDao().getTripById(tripId));
                    result.setFiles(mDb.recordDao().getFileByParent(production.getId()));
                    emitter.onSuccess(result);
                }
            }
        }).subscribeOn(Schedulers.single());
    }

    public Single<String> saveProduction(final ProductionWithConfig production) {
        return Single.create(new SingleOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<String> emitter) throws Exception {
                String tripId = production.getTrip().getId();
                if (TextUtils.isEmpty(tripId)) {
                    tripId = generateHashId(Trip.class.getClass().getSimpleName());
                    production.getTrip().setId(tripId);
                }
                production.getTrip().setUpdated_at(new Date());
                if (mDb.recordDao().saveTrip(production.getTrip()) == -1) {
                    emitter.onError(new Throwable("save trip failed."));
                } else {
                    production.getProduction().setTrip_id(tripId);
                    String productionId = production.getProduction().getId();
                    if (TextUtils.isEmpty(productionId)) {
                        productionId = generateHashId(Quotation.class.getClass().getSimpleName());
                        production.getProduction().setId(productionId);
                    }
                    production.getProduction().setUpdated_at(new Date());
                    if (mDb.recordDao().saveNewProjectProduction(production.getProduction()) != -1L) {
                        saveFile(productionId, ParentType.PRODUCTION, production.getFiles());
                        emitter.onSuccess(productionId);
                    } else {
                        emitter.onError(new Throwable("save production failed."));
                    }
                }
            }
        }).subscribeOn(Schedulers.single());
    }

    public Single<NonStandardInquiryWithConfig> getNonStandardInquiryByTrip(final String tripId) {
        return Single.create(new SingleOnSubscribe<NonStandardInquiryWithConfig>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<NonStandardInquiryWithConfig> emitter)
                    throws
                    Exception {
                NonStandardInquiry inquiry = mDb.recordDao().getNonStandardInquiryByTrip(tripId);
                if (inquiry == null)
                    emitter.onError(new Throwable("can't get inquiry"));
                else {
                    NonStandardInquiryWithConfig result = new NonStandardInquiryWithConfig(inquiry);
                    result.setTrip(mDb.recordDao().getTripById(tripId));
                    result.setFiles(mDb.recordDao().getFileByParent(inquiry.getId()));
                    result.setProducts(mDb.recordDao().getNonStandardInquiryProductByParent(inquiry.getId()));
                    emitter.onSuccess(result);
                }
            }
        }).subscribeOn(Schedulers.single());
    }

    public Single<NonStandardInquiryWithConfig> getNonStandardInquiryById(final String id) {
        return Single.create(new SingleOnSubscribe<NonStandardInquiryWithConfig>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<NonStandardInquiryWithConfig> emitter)
                    throws
                    Exception {
                NonStandardInquiry inquiry = mDb.recordDao().getNonStandardInquiryById(id);
                if (inquiry == null)
                    emitter.onError(new Throwable("can't get inquiry"));
                else {
                    NonStandardInquiryWithConfig result = new NonStandardInquiryWithConfig(inquiry);
                    result.setTrip(mDb.recordDao().getTripById(inquiry.getTrip_id()));
                    result.setFiles(mDb.recordDao().getFileByParent(inquiry.getId()));
                    result.setProducts(mDb.recordDao().getNonStandardInquiryProductByParent(inquiry.getId()));
                    emitter.onSuccess(result);
                }
            }
        }).subscribeOn(Schedulers.single());
    }

    public Single<String> saveNonStandardInquiry(final NonStandardInquiryWithConfig inquiry) {
        return Single.create(new SingleOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<String> emitter) throws Exception {
                String tripId = inquiry.getTrip().getId();
                if (TextUtils.isEmpty(tripId)) {
                    tripId = generateHashId(Trip.class.getClass().getSimpleName());
                    inquiry.getTrip().setId(tripId);
                }
                inquiry.getTrip().setUpdated_at(new Date());
                if (mDb.recordDao().saveTrip(inquiry.getTrip()) == -1) {
                    emitter.onError(new Throwable("save trip failed."));
                } else {
                    inquiry.getInquiry().setTrip_id(tripId);
                    String inquiryId = inquiry.getInquiry().getId();
                    if (TextUtils.isEmpty(inquiryId)) {
                        inquiryId = generateHashId(Quotation.class.getClass().getSimpleName());
                        inquiry.getInquiry().setId(inquiryId);
                    }
                    inquiry.getInquiry().setUpdated_at(new Date());
                    if (mDb.recordDao().saveNonStandardInquiry(inquiry.getInquiry()) != -1L) {
                        saveNonStandardInquiryProduct(inquiryId, inquiry.getProducts());
                        saveFile(inquiryId, ParentType.NON_STANDARD_INQUIRY, inquiry.getFiles());
                        emitter.onSuccess(inquiryId);
                    } else {
                        emitter.onError(new Throwable("save inquiry failed."));
                    }
                }
            }
        }).subscribeOn(Schedulers.single());
    }

    private boolean saveNonStandardInquiryProduct(String parentId, List<NonStandardInquiryProduct> products) {
        for (NonStandardInquiryProduct product : products) {
            product.setParent_id(parentId);
            if (TextUtils.isEmpty(product.getId())) {
                product.setId(generateHashId(product.getClass().getSimpleName()));
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            product.setUpdated_at(new Date());
        }
        return !mDb.recordDao().saveNonStandardInquiryProduct(products).contains(-1L);
    }

    public Single<SpringRingInquiryWithConfig> getSpringRingInquiryByTrip(final String tripId) {
        return Single.create(new SingleOnSubscribe<SpringRingInquiryWithConfig>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<SpringRingInquiryWithConfig> emitter)
                    throws
                    Exception {
                SpringRingInquiry inquiry = mDb.recordDao().getSpringRingInquiryByTrip(tripId);
                if (inquiry == null)
                    emitter.onError(new Throwable("can't get inquiry"));
                else {
                    SpringRingInquiryWithConfig result = new SpringRingInquiryWithConfig(inquiry);
                    result.setTrip(mDb.recordDao().getTripById(tripId));
                    result.setFiles(mDb.recordDao().getFileByParent(inquiry.getId()));
                    result.setProducts(mDb.recordDao().getSpringRingInquiryProductByParent(inquiry.getId()));
                    emitter.onSuccess(result);
                }
            }
        }).subscribeOn(Schedulers.single());
    }

    public Single<SpringRingInquiryWithConfig> getSpringRingInquiryById(final String id) {
        return Single.create(new SingleOnSubscribe<SpringRingInquiryWithConfig>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<SpringRingInquiryWithConfig> emitter)
                    throws
                    Exception {
                SpringRingInquiry inquiry = mDb.recordDao().getSpringRingInquiryById(id);
                if (inquiry == null)
                    emitter.onError(new Throwable("can't get inquiry"));
                else {
                    SpringRingInquiryWithConfig result = new SpringRingInquiryWithConfig(inquiry);
                    result.setTrip(mDb.recordDao().getTripById(inquiry.getTrip_id()));
                    result.setFiles(mDb.recordDao().getFileByParent(inquiry.getId()));
                    result.setProducts(mDb.recordDao().getSpringRingInquiryProductByParent(inquiry.getId()));
                    emitter.onSuccess(result);
                }
            }
        }).subscribeOn(Schedulers.single());
    }

    public Single<String> saveSpringRingInquiry(final SpringRingInquiryWithConfig inquiry) {
        return Single.create(new SingleOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<String> emitter) throws Exception {
                String tripId = inquiry.getTrip().getId();
                if (TextUtils.isEmpty(tripId)) {
                    tripId = generateHashId(Trip.class.getClass().getSimpleName());
                    inquiry.getTrip().setId(tripId);
                }
                inquiry.getTrip().setUpdated_at(new Date());
                if (mDb.recordDao().saveTrip(inquiry.getTrip()) == -1) {
                    emitter.onError(new Throwable("save trip failed."));
                } else {
                    inquiry.getInquiry().setTrip_id(tripId);
                    String inquiryId = inquiry.getInquiry().getId();

                    if (TextUtils.isEmpty(inquiryId)) {
                        inquiryId = generateHashId(Quotation.class.getClass().getSimpleName());
                        inquiry.getInquiry().setId(inquiryId);
                    }
                    inquiry.getInquiry().setUpdated_at(new Date());

                    if (mDb.recordDao().saveSpringRingInquiry(inquiry.getInquiry()) != -1L)
                    {
                        saveSpringRingInquiryProduct(inquiryId, inquiry.getProducts());
                        saveFile(inquiryId, ParentType.SPRING_RING_INQUIRY, inquiry.getFiles());
                        emitter.onSuccess(inquiryId);
                    } else {
                        emitter.onError(new Throwable("save inquiry failed."));
                    }
                }
            }
        }).subscribeOn(Schedulers.single());
    }

    private boolean saveSpringRingInquiryProduct(String parentId, List<SpringRingInquiryProduct> products) {
        for (SpringRingInquiryProduct product : products) {
            product.setParent_id(parentId);
            if (TextUtils.isEmpty(product.getId())) {
                product.setId(generateHashId(product.getClass().getSimpleName()));
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            product.setUpdated_at(new Date());
        }
        return !mDb.recordDao().saveSpringRingInquiryProduct(products).contains(-1L);
    }

    public Single<SpecialPriceWithConfig> getSpecialPriceByTrip(final String tripId) {
        return Single.create(new SingleOnSubscribe<SpecialPriceWithConfig>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<SpecialPriceWithConfig> emitter) throws
                    Exception {
                SpecialPrice specialPrice = mDb.recordDao().getSpecialPriceByTrip(tripId);
                if (specialPrice == null)
                    emitter.onError(new Throwable("can't get specialPrice"));
                else {
                    SpecialPriceWithConfig result = new SpecialPriceWithConfig(specialPrice);
                    result.setTrip(mDb.recordDao().getTripById(tripId));
                    result.setFiles(mDb.recordDao().getFileByParent(specialPrice.getId()));
                    result.setProducts(mDb.recordDao().getSpecialPriceProductByParent(specialPrice.getId()));
                    emitter.onSuccess(result);
                }
            }
        }).subscribeOn(Schedulers.single());
    }

    public Single<SpecialPriceWithConfig> getSpecialPriceById(final String id) {
        return Single.create(new SingleOnSubscribe<SpecialPriceWithConfig>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<SpecialPriceWithConfig> emitter) throws
                    Exception {
                SpecialPrice specialPrice = mDb.recordDao().getSpecialPriceById(id);
                if (specialPrice == null)
                    emitter.onError(new Throwable("can't get specialPrice"));
                else {
                    SpecialPriceWithConfig result = new SpecialPriceWithConfig(specialPrice);
                    result.setTrip(mDb.recordDao().getTripById(specialPrice.getTrip_id()));
                    result.setFiles(mDb.recordDao().getFileByParent(specialPrice.getId()));
                    result.setProducts(mDb.recordDao().getSpecialPriceProductByParent(specialPrice.getId()));
                    emitter.onSuccess(result);
                }
            }
        }).subscribeOn(Schedulers.single());
    }

    public Single<String> saveSpecialPrice(final SpecialPriceWithConfig specialPrice) {
        return Single.create(new SingleOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<String> emitter) throws Exception {
                String tripId = specialPrice.getTrip().getId();
                if (TextUtils.isEmpty(tripId)) {
                    tripId = generateHashId(Trip.class.getClass().getSimpleName());
                    specialPrice.getTrip().setId(tripId);
                }
                specialPrice.getTrip().setUpdated_at(new Date());
                if (mDb.recordDao().saveTrip(specialPrice.getTrip()) == -1) {
                    emitter.onError(new Throwable("save trip failed."));
                } else {
                    specialPrice.getSpecialPrice().setTrip_id(tripId);
                    String specialPriceId = specialPrice.getSpecialPrice().getId();
                    if (TextUtils.isEmpty(specialPriceId)) {
                        specialPriceId = generateHashId(Quotation.class.getClass().getSimpleName());
                        specialPrice.getSpecialPrice().setId(specialPriceId);
                    }
                    specialPrice.getSpecialPrice().setUpdated_at(new Date());
                    if (mDb.recordDao().saveSpecialPrice(specialPrice.getSpecialPrice()) != -1L) {
                        saveSpecialPriceProduct(specialPriceId, specialPrice.getProducts());
                        saveFile(specialPriceId, ParentType.SPECIAL_PRICE, specialPrice.getFiles());
                        emitter.onSuccess(specialPriceId);
                    } else {
                        emitter.onError(new Throwable("save specialPrice failed."));
                    }
                }
            }
        }).subscribeOn(Schedulers.single());
    }

    private boolean saveSpecialPriceProduct(String parentId, List<SpecialPriceProduct> products) {
        for (SpecialPriceProduct product : products) {
            product.setParent_id(parentId);
            if (TextUtils.isEmpty(product.getId())) {
                product.setId(generateHashId(product.getClass().getSimpleName()));
                try
                {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            product.setUpdated_at(new Date());
        }
        return !mDb.recordDao().saveSpecialPriceProduct(products).contains(-1L);
    }

    public Single<TravelWithConfig> getTravelByTrip(final String tripId) {
        return Single.create(new SingleOnSubscribe<TravelWithConfig>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<TravelWithConfig> emitter) throws
                    Exception {
                Travel travel = mDb.recordDao().getTravelByTrip(tripId);
                if (travel == null)
                    emitter.onError(new Throwable("can't get travel"));
                else {
                    TravelWithConfig result = new TravelWithConfig(travel);
                    result.setTrip(mDb.recordDao().getTripById(tripId));
                    result.setParticipants(mDb.recordDao().getParticipantByParent(travel.getId()));
                    result.setFiles(mDb.recordDao().getFileByParent(travel.getId()));
                    emitter.onSuccess(result);
                }
            }
        }).subscribeOn(Schedulers.single());
    }

    public Single<String> saveTravel(final TravelWithConfig travel) {
        return Single.create(new SingleOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<String> emitter) throws Exception {
                String tripId = travel.getTrip().getId();
                if (TextUtils.isEmpty(tripId)) {
                    tripId = generateHashId(Trip.class.getClass().getSimpleName());
                    travel.getTrip().setId(tripId);
                }
                travel.getTrip().setUpdated_at(new Date());
                if (mDb.recordDao().saveTrip(travel.getTrip()) == -1) {
                    emitter.onError(new Throwable("save trip failed."));
                } else {
                    travel.getTravel().setTrip_id(tripId);
                    String travelId = travel.getTravel().getId();
                    if (TextUtils.isEmpty(travelId)) {
                        travelId = generateHashId(Office.class.getClass().getSimpleName());
                        travel.getTravel().setId(travelId);
                    }
                    travel.getTravel().setUpdated_at(new Date());
                    if (mDb.recordDao().saveTravel(travel.getTravel()) != -1L) {
                        saveParticipant(tripId, travelId, ParentType.TRAVEL, travel.getParticipants());
                        saveFile(travelId, ParentType.TRAVEL, travel.getFiles());
                        emitter.onSuccess(travelId);
                    } else {
                        emitter.onError(new Throwable("save travel failed."));
                    }
                }
            }
        }).subscribeOn(Schedulers.single());
    }

    public Single<ExpressWithConfig> getExpressByTrip(final String tripId) {
        return Single.create(new SingleOnSubscribe<ExpressWithConfig>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<ExpressWithConfig> emitter) throws
                    Exception {
                Express express = mDb.recordDao().getExpressByTrip(tripId);
                if (express == null)
                    emitter.onError(new Throwable("can't get express"));
                else {
                    ExpressWithConfig result = new ExpressWithConfig(express);
                    result.setTrip(mDb.recordDao().getTripById(tripId));
                    result.setContacters(mDb.recordDao().getContacterByParent(express.getId()));
                    result.setFiles(mDb.recordDao().getFileByParent(express.getId()));
                    emitter.onSuccess(result);
                }
            }
        }).subscribeOn(Schedulers.single());
    }

    public Single<String> saveExpress(final ExpressWithConfig express) {
        return Single.create(new SingleOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<String> emitter) throws Exception {
                String tripId = express.getTrip().getId();
                if (TextUtils.isEmpty(tripId)) {
                    tripId = generateHashId(Trip.class.getClass().getSimpleName());
                    express.getTrip().setId(tripId);
                }
                express.getTrip().setUpdated_at(new Date());
                if (mDb.recordDao().saveTrip(express.getTrip()) == -1) {
                    emitter.onError(new Throwable("save trip failed."));
                } else {
                    express.getExpress().setTrip_id(tripId);
                    String expressId = express.getExpress().getId();
                    if (TextUtils.isEmpty(expressId)) {
                        expressId = generateHashId(Office.class.getClass().getSimpleName());
                        express.getExpress().setId(expressId);
                    }
                    express.getExpress().setUpdated_at(new Date());
                    if (mDb.recordDao().saveExpress(express.getExpress()) != -1L) {
                        saveContacter(expressId, ParentType.EXPRESS, express.getContacters());
                        saveFile(expressId, ParentType.EXPRESS, express.getFiles());
                        emitter.onSuccess(expressId);
                    } else {
                        emitter.onError(new Throwable("save travel failed."));
                    }
                }
            }
        }).subscribeOn(Schedulers.single());
    }

    public Single<Repayment> getRepaymentByTrip(final String tripId) {
        return Single.create(new SingleOnSubscribe<Repayment>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<Repayment> emitter) throws
                    Exception {
                Repayment repayment = mDb.recordDao().getRepaymentByTrip(tripId);
                if (repayment == null)
                    emitter.onError(new Throwable("can't get repayment"));
                else {
                    emitter.onSuccess(repayment);
                }
            }
        }).subscribeOn(Schedulers.single());
    }

    public Single<SpecialShipWithConfig> getSpecialShipByTrip(final String tripId) {
        return Single.create(new SingleOnSubscribe<SpecialShipWithConfig>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<SpecialShipWithConfig> emitter) throws
                    Exception {
                SpecialShip specialShip = mDb.recordDao().getSpecialShipByTrip(tripId);
                if (specialShip == null)
                    emitter.onError(new Throwable("can't get specialShip"));
                else {
                    SpecialShipWithConfig result = new SpecialShipWithConfig(specialShip);
                    result.setTrip(mDb.recordDao().getTripById(tripId));
                    result.setFiles(mDb.recordDao().getFileByParent(specialShip.getId()));
                    emitter.onSuccess(result);
                }
            }
        }).subscribeOn(Schedulers.single());
    }

    public Single<String> saveSpecialShip(final SpecialShipWithConfig specialShip) {
        return Single.create(new SingleOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<String> emitter) throws Exception {
                specialShip.getTrip().setUpdated_at(new Date());
                if (mDb.recordDao().saveTrip(specialShip.getTrip()) == -1) {
                    emitter.onError(new Throwable("save trip failed."));
                } else {
                    specialShip.getSpecialShip().setUpdated_at(new Date());
                    if (mDb.recordDao().saveSpecialShip(specialShip.getSpecialShip()) != -1L) {
                        saveFile(specialShip.getSpecialShip().getId(), ParentType.SPECIAL_SHIP, specialShip.getFiles());
                        emitter.onSuccess(specialShip.getSpecialShip().getId());
                    } else {
                        emitter.onError(new Throwable("save specialShip failed."));
                    }
                }
            }
        }).subscribeOn(Schedulers.single());
    }

    /*--------------------------------------檔案-------------------------------------------*/
    public Flowable<File> getFileByParent(final String parentId) {
        return Flowable.create(new FlowableOnSubscribe<File>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<File> e) throws Exception {
                List<File> files = mDb.recordDao().getFileByParent(parentId);
                for (File file : files)
                    e.onNext(file);
                e.onComplete();
            }
        }, BACK_PRESSURE_STRATEGY).subscribeOn(SCHEDULER);
    }

    public Flowable<File> getFileById(final List<String> idList) {
        return Flowable.create(new FlowableOnSubscribe<File>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<File> e) throws Exception {
                if (idList == null || idList.size() == 0)
                    e.onComplete();
                else {
                    for (String id : idList)
                    {
                        File file = mDb.recordDao().getFileById(id);
                        if (file != null)
                            e.onNext(file);
                    }
                }
                e.onComplete();
            }
        }, BACK_PRESSURE_STRATEGY).subscribeOn(SCHEDULER);
    }

    public Single<File> getFileById(final String id) {
        return Single.create(new SingleOnSubscribe<File>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<File> e) throws Exception {
                File file = mDb.recordDao().getFileById(id);
                if (file == null)
                    e.onError(new Throwable("can't get file."));
                else
                    e.onSuccess(file);
            }
        }).subscribeOn(SCHEDULER);
    }

    private boolean saveFile(String parentId, ParentType type, List<File> files) {
        if (files == null)
            return true;
        for (File file : files) {
            if (TextUtils.isEmpty(file.getId())) {
                file.setId(generateHashId(file.getClass().getSimpleName()));
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            file.setParent_type(type);
            if (TextUtils.isEmpty(file.getUser_id()))
                file.setUser_id(TokenManager.getInstance().getUser().getId());
            file.setUpdated_at(new Date());
        }
        if (mDb.recordDao().saveFile(files).contains(-1L)) {
            return false;
        } else {
            ArrayList<FilePivot> filePivots = new ArrayList<>();
            for (File file : files) {
                String fileId = file.getId();
                FilePivot filePivot = mDb.recordDao().getFilePivot(fileId, parentId);
                if (filePivot == null)
                    filePivot = new FilePivot();
                if (TextUtils.isEmpty(filePivot.getId())) {
                    filePivot.setId(generateHashId(filePivot.getClass().getSimpleName()));
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                filePivot.setUpdated_at(new Date());
                filePivot.setParent_id(parentId);
                filePivot.setFile_id(fileId);
                filePivots.add(filePivot);
            }
            return !mDb.recordDao().saveFilePivot(filePivots).contains(-1L);
        }
    }

    /**
     * 儲存簽到照片
     *
     * @param parentId 參與人ID
     */
    public Flowable<String> saveSignInFile(final File file, final String parentId) {
        return Flowable.create(new FlowableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<String> e) throws Exception {
                if (file == null)
                    e.onError(new Throwable("no file"));
                else {
                    if (TextUtils.isEmpty(file.getId())) {
                        file.setId(generateHashId(file.getClass().getSimpleName()));
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException error) {
                            error.printStackTrace();
                        }
                    }
                    file.setParent_type(ParentType.PARTICIPANT);
                    if (TextUtils.isEmpty(file.getUser_id()))
                        file.setUser_id(TokenManager.getInstance().getUser().getId());
                    file.setUpdated_at(new Date());
                    if (mDb.recordDao().saveFile(file) == -1L) {
                        e.onError(new Throwable("can't save file."));
                    } else {
                        FilePivot pivot = new FilePivot();
                        pivot.setId(generateHashId(FilePivot.class.getSimpleName()));
                        pivot.setUpdated_at(new Date());
                        pivot.setFile_id(file.getId());
                        pivot.setParent_id(parentId);
                        mDb.recordDao().saveFilePivot(pivot);
                        e.onNext(file.getId());
                    }
                }
                e.onComplete();
            }
        }, BACK_PRESSURE_STRATEGY).subscribeOn(SCHEDULER);
    }

    public Flowable<String> saveReimbursementFile(final List<File> files) {
        return Flowable.create(new FlowableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<String> e) throws Exception {
                if (files == null)
                    e.onComplete();
                else {
                    for (File file : files) {
                        if (TextUtils.isEmpty(file.getId())) {
                            file.setId(generateHashId(file.getClass().getSimpleName()));
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException error) {
                                error.printStackTrace();
                            }
                        }
                        file.setParent_type(ParentType.REIMBURSEMENT);
                        if (TextUtils.isEmpty(file.getUser_id()))
                            file.setUser_id(TokenManager.getInstance().getUser().getId());
                        file.setUpdated_at(new Date());
                        if (mDb.recordDao().saveFile(file) == -1L) {
                            e.onError(new Throwable("can't save file."));
                        } else {
                            e.onNext(file.getId());
                        }
                    }
                }
                e.onComplete();
            }
        }, BACK_PRESSURE_STRATEGY).subscribeOn(SCHEDULER);
    }

    public Single<Boolean> deleteFileById(final String fileId) {
        return Single.create(new SingleOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<Boolean> emitter) throws Exception {
                if (deleteFile(fileId)) {
                    emitter.onSuccess(true);
                } else {
                    emitter.onError(new Throwable("can't delete file."));
                }
            }
        }).subscribeOn(Schedulers.single());

    }

    public Single<Boolean> deleteFileById(final List<String> fileIdList) {
        return Single.create(new SingleOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<Boolean> emitter) throws Exception {
                boolean isSuccess = true;
                for (String fileId : fileIdList) {
                    if (!deleteFile(fileId)) {
                        isSuccess = false;
                    }
                }
                emitter.onSuccess(isSuccess);
            }
        }).subscribeOn(Schedulers.single());
    }

    private boolean deleteFile(String fileId) {
        File file = mDb.recordDao().getFileById(fileId);
        if (file == null) {
            return true;
        } else {
            file.setUpdated_at(new Date());
            file.setDeleted_at(new Date());
            if (mDb.recordDao().saveFile(file) == -1)
                return false;
            else {
                List<FilePivot> pivots = mDb.recordDao().getFilePivot(fileId);
                for (FilePivot pivot : pivots) {
                    pivot.setUpdated_at(new Date());
                    pivot.setDeleted_at(new Date());
                }
                mDb.recordDao().saveFilePivot(pivots);
                return true;
            }
        }
    }

    /*--------------------------------------對話-------------------------------------------*/
    public Single<String> saveConversation(final Conversation conversation) {
        return Single.create(new SingleOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<String> emitter) throws Exception {
                String conversationId = conversation.getConversation().getId();
                if (TextUtils.isEmpty(conversationId)) {
                    conversationId = generateHashId(conversation.getConversation().getClass().getSimpleName());
                    conversation.getConversation().setId(conversationId);
                }
                conversation.getConversation().setUpdated_at(new Date());
                if (mDb.recordDao().saveTaskConversation(conversation.getConversation()) == -1L)
                    emitter.onError(new Throwable("save conversation failed."));

                for (Message message : conversation.getMessages()) {
                    if (TextUtils.isEmpty(message.getMessage().getId())) {
                        message.getMessage().setId(generateHashId(message.getMessage().getClass().getSimpleName()));
                    }
                    message.getMessage().setTrip_id(conversation.getConversation().getTrip_id());
                    message.getMessage().setUpdated_at(new Date());
                    message.getMessage().setConversation_id(conversationId);
                    if (mDb.recordDao().saveTaskMessage(message.getMessage()) == -1L)
                        emitter.onError(new Throwable("save message failed."));
                }
                emitter.onSuccess(conversationId);
            }
        }).subscribeOn(Schedulers.single());

    }

    /**
     * 取得對話
     */
    public Flowable<Conversation> getConversationByParent(final String parentId) {
        return Flowable.create(new FlowableOnSubscribe<Conversation>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<Conversation> emitter)
                    throws Exception {
                List<TaskConversation> conversations = mDb.recordDao().getConversationByParent(parentId);
                for (TaskConversation conversation : conversations) {
                    Conversation result = new Conversation();
                    result.setConversation(conversation);
                    result.setUser(mDb.settingDao().getUserById(conversation.getUser_id()));
                    List<Message> resultItem = new ArrayList<>();
                    List<TaskMessage> messages = mDb.recordDao().getMessageByConversation(conversation.getId());
                    for (TaskMessage message : messages) {
                        Message item = new Message();
                        item.setMessage(message);
                        item.setUser(mDb.settingDao().getUserById(message.getUser_id()));
                        resultItem.add(item);
                    }
                    result.setMessages(resultItem);
                    emitter.onNext(result);
                }
                emitter.onComplete();
            }
        }, BACK_PRESSURE_STRATEGY).subscribeOn(SCHEDULER);
    }

    /**
     * 依ID取得對話
     */
    public Single<Conversation> getConversationById(final String id) {
        return Single.create(new SingleOnSubscribe<Conversation>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<Conversation> emitter)
                    throws Exception {
                TaskConversation conversation = mDb.recordDao().getConversationById(id);
                if (conversation == null)
                    emitter.onError(new Throwable("can't get conversation by id"));
                else {
                    Conversation result = new Conversation();
                    result.setConversation(conversation);
                    result.setUser(mDb.settingDao().getUserById(conversation.getUser_id()));
                    List<Message> resultItem = new ArrayList<>();
                    List<TaskMessage> messages = mDb.recordDao().getMessageByConversation(conversation.getId());
                    for (TaskMessage message : messages) {
                        Message item = new Message();
                        item.setMessage(message);
                        item.setUser(mDb.settingDao().getUserById(message.getUser_id()));
                        resultItem.add(item);
                    }
                    result.setMessages(resultItem);
                    emitter.onSuccess(result);
                }
            }
        }).subscribeOn(SCHEDULER);
    }

    /*--------------------------------------設定-------------------------------------------*/

    public Single<String> saveSyncLog(final SyncLog syncLog) {
        return Single.create(new SingleOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<String> emitter) throws Exception {
                syncLog.setUpdated_at(new Date());
                if (mDb.localDao().saveSyncLog(syncLog) == -1) {
                    emitter.onError(new Throwable("save SyncLog failed."));
                } else {
                    emitter.onSuccess(syncLog.getId());
                }
            }
        }).subscribeOn(Schedulers.single());
    }

    public Single<String> resetSyncLog(final SyncLog syncLog) {
        return Single.create(new SingleOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<String> emitter) throws Exception {
                SyncLog lastLog = mDb.localDao().getLastSyncLog(syncLog.is_file, syncLog.user_id);
                if (lastLog == null || lastLog.getCreated_at().compareTo(syncLog.getCreated_at()) > 0) {
                    syncLog.setUpdated_at(new Date());
                    if (mDb.localDao().saveSyncLog(syncLog) == -1) {
                        emitter.onError(new Throwable("reset SyncLog failed."));
                    } else {
                        emitter.onSuccess(syncLog.getId());
                    }
                } else {
                    emitter.onSuccess(lastLog.getId());
                }
            }
        }).subscribeOn(Schedulers.single());
    }

    /**
     * 取得最後一筆鬧鐘ID
     */
    public Single<Integer> getLastClockId() {
        return Single.create(new SingleOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<Integer> e) throws Exception {
                Integer clockId = mDb.recordDao().getLastClockId();
                if (clockId == null)
                    e.onSuccess(0);
                else
                    e.onSuccess(clockId);
            }
        }).subscribeOn(Schedulers.single());
    }

    /**
     * 依匯率ID取得貨幣設定
     */
    public Single<ConfigCurrencyWithRate> getConfigCurrencyByRate(final String rateId) {
        return Single.create(new SingleOnSubscribe<ConfigCurrencyWithRate>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<ConfigCurrencyWithRate> emitter)
                    throws Exception {
                ConfigCurrencyWithRate result = new ConfigCurrencyWithRate();
                result.setCurrencyRate(mDb.settingDao().getCurrencyRateById(rateId));
                result.setConfigCurrency(mDb.settingDao().getConfigCurrencyById(result.getCurrencyRate().getConfig_currency_id()));
                emitter.onSuccess(result);
            }
        }).subscribeOn(Schedulers.single());
    }

    /**
     * 取得貨幣設定
     */
    public Flowable<ConfigCurrencyWithRate> getConfigCurrency() {
        return Flowable.create(new FlowableOnSubscribe<ConfigCurrencyWithRate>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<ConfigCurrencyWithRate> emitter)
                    throws Exception {
                List<ConfigCurrency> items = mDb.settingDao().getConfigCurrency(TokenManager.getInstance().getUser().getCompany_id());
                for (ConfigCurrency item : items) {
                    ConfigCurrencyWithRate result = new ConfigCurrencyWithRate();
                    result.setConfigCurrency(item);
                    result.setCurrencyRate(mDb.settingDao().getCurrencyRateByConfig(item.getId()));
                    emitter.onNext(result);
                }
                emitter.onComplete();
            }
        }, BACK_PRESSURE_STRATEGY).subscribeOn(SCHEDULER);
    }

    /**
     * 依ID取得行業別
     */
    public Single<ConfigIndustry> getConfigIndustryById(final String id) {
        return Single.create(new SingleOnSubscribe<ConfigIndustry>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<ConfigIndustry> emitter)
                    throws Exception {
                ConfigIndustry result = mDb.settingDao().getConfigIndustryById(id);
                if (result != null)
                    emitter.onSuccess(result);
                else
                    emitter.onError(new Throwable("can't get ConfigIndustry"));
            }
        }).subscribeOn(Schedulers.single());
    }

    /**
     * 取得銷售組織
     */
    public Flowable<ConfigOffice> getConfigOffice() {
        return Flowable.create(new FlowableOnSubscribe<ConfigOffice>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<ConfigOffice> emitter)
                    throws Exception {
                List<ConfigOffice> items = mDb.settingDao().getConfigOffice(TokenManager.getInstance().getUser().getCompany_id());
                for (ConfigOffice item : items) {
                    emitter.onNext(item);
                }
                emitter.onComplete();
            }
        }, BACK_PRESSURE_STRATEGY).subscribeOn(SCHEDULER);
    }

    /**
     * 依ID取得銷售組織
     */
    public Single<ConfigOffice> getConfigOfficeById(final String id) {
        return Single.create(new SingleOnSubscribe<ConfigOffice>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<ConfigOffice> emitter)
                    throws Exception {
                ConfigOffice result = mDb.settingDao().getConfigOfficeById(id);
                if (result != null)
                    emitter.onSuccess(result);
                else
                    emitter.onError(new Throwable("can't get ConfigOffice"));
            }
        }).subscribeOn(Schedulers.single());
    }

    /**
     * 取得銷售組織
     */
    public Flowable<ConfigExpressDestination> getConfigExpressDestination() {
        return Flowable.create(new FlowableOnSubscribe<ConfigExpressDestination>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<ConfigExpressDestination> emitter)
                    throws Exception {
                List<ConfigExpressDestination> items = mDb.settingDao().getConfigExpressDestination(TokenManager.getInstance().getUser().getCompany_id());
                for (ConfigExpressDestination item : items) {
                    emitter.onNext(item);
                }
                emitter.onComplete();
            }
        }, BACK_PRESSURE_STRATEGY).subscribeOn(SCHEDULER);
    }

    /**
     * 依ID取得銷售組織
     */
    public Single<ConfigExpressDestination> getConfigExpressDestinationById(final String id) {
        return Single.create(new SingleOnSubscribe<ConfigExpressDestination>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<ConfigExpressDestination> emitter)
                    throws Exception {
                ConfigExpressDestination result = mDb.settingDao().getConfigExpressDestinationById(id);
                if (result != null)
                    emitter.onSuccess(result);
                else
                    emitter.onError(new Throwable("can't get ConfigExpressDestination"));
            }
        }).subscribeOn(Schedulers.single());
    }

    /**
     * 取得密封狀態
     */
    public Flowable<ConfigSealedState> getConfigSealedState() {
        return Flowable.create(new FlowableOnSubscribe<ConfigSealedState>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<ConfigSealedState> emitter)
                    throws Exception {
                List<ConfigSealedState> items = mDb.settingDao().getConfigSealedState(TokenManager.getInstance().getUser().getCompany_id());
                for (ConfigSealedState item : items) {
                    emitter.onNext(item);
                }
                emitter.onComplete();
            }
        }, BACK_PRESSURE_STRATEGY).subscribeOn(SCHEDULER);
    }

    /**
     * 依ID取得密封狀態
     */
    public Single<ConfigSealedState> getConfigSealedStateById(final String id) {
        return Single.create(new SingleOnSubscribe<ConfigSealedState>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<ConfigSealedState> emitter)
                    throws Exception {
                ConfigSealedState result = mDb.settingDao().getConfigSealedStateById(id);
                if (result != null)
                    emitter.onSuccess(result);
                else
                    emitter.onError(new Throwable("can't get ConfigSealedState"));
            }
        }).subscribeOn(Schedulers.single());
    }

    /**
     * 取得趨勢設定
     */
    public Flowable<ConfigTrend> getConfigTrend() {
        return Flowable.create(new FlowableOnSubscribe<ConfigTrend>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<ConfigTrend> emitter)
                    throws Exception {
                List<ConfigTrend> items = mDb.settingDao().getConfigTrend(TokenManager.getInstance().getUser().getCompany_id());
                for (ConfigTrend item : items) {
                    emitter.onNext(item);
                }
                emitter.onComplete();
            }
        }, BACK_PRESSURE_STRATEGY).subscribeOn(SCHEDULER);
    }

    /**
     * 依ID取得趨勢設定
     */
    public Single<ConfigTrend> getConfigTrendById(final String id) {
        return Single.create(new SingleOnSubscribe<ConfigTrend>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<ConfigTrend> emitter)
                    throws Exception {
                ConfigTrend result = mDb.settingDao().getConfigTrendById(id);
                if (result != null)
                    emitter.onSuccess(result);
                else
                    emitter.onError(new Throwable("can't get ConfigTrend"));
            }
        }).subscribeOn(Schedulers.single());
    }

    /**
     * 取得SGB優勢設定
     */
    public Flowable<ConfigSgbAdvantage> getConfigSgbAdvantage() {
        return Flowable.create(new FlowableOnSubscribe<ConfigSgbAdvantage>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<ConfigSgbAdvantage> emitter)
                    throws Exception {
                List<ConfigSgbAdvantage> items = mDb.settingDao().getConfigSgbAdvantage(TokenManager.getInstance().getUser().getCompany_id());
                for (ConfigSgbAdvantage item : items) {
                    emitter.onNext(item);
                }
                emitter.onComplete();
            }
        }, BACK_PRESSURE_STRATEGY).subscribeOn(SCHEDULER);
    }

    /**
     * 依ID取得SGB優勢設定
     */
    public Single<ConfigSgbAdvantage> getConfigSgbAdvantageById(final String id) {
        return Single.create(new SingleOnSubscribe<ConfigSgbAdvantage>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<ConfigSgbAdvantage> emitter)
                    throws Exception {
                ConfigSgbAdvantage result = mDb.settingDao().getConfigSgbAdvantageById(id);
                if (result != null)
                    emitter.onSuccess(result);
                else
                    emitter.onError(new Throwable("can't get ConfigSgbAdvantage"));
            }
        }).subscribeOn(Schedulers.single());
    }

    /**
     * 取得SGB劣勢設定
     */
    public Flowable<ConfigSgbDisadvantage> getConfigSgbDisadvantage() {
        return Flowable.create(new FlowableOnSubscribe<ConfigSgbDisadvantage>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<ConfigSgbDisadvantage> emitter)
                    throws Exception {
                List<ConfigSgbDisadvantage> items = mDb.settingDao().getConfigSgbDisadvantage(TokenManager.getInstance().getUser().getCompany_id());
                for (ConfigSgbDisadvantage item : items) {
                    emitter.onNext(item);
                }
                emitter.onComplete();
            }
        }, BACK_PRESSURE_STRATEGY).subscribeOn(SCHEDULER);
    }

    /**
     * 依ID取得SGB劣勢設定
     */
    public Single<ConfigSgbDisadvantage> getConfigSgbDisadvantageById(final String id) {
        return Single.create(new SingleOnSubscribe<ConfigSgbDisadvantage>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<ConfigSgbDisadvantage> emitter)
                    throws Exception {
                ConfigSgbDisadvantage result = mDb.settingDao().getConfigSgbDisadvantageById(id);
                if (result != null)
                    emitter.onSuccess(result);
                else
                    emitter.onError(new Throwable("can't get ConfigSgbDisadvantage"));
            }
        }).subscribeOn(Schedulers.single());
    }

    /**
     * 取得競爭狀態設定
     */
    public Flowable<ConfigCompetitiveStatus> getConfigCompetitiveStatus() {
        return Flowable.create(new FlowableOnSubscribe<ConfigCompetitiveStatus>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<ConfigCompetitiveStatus> emitter)
                    throws Exception {
                List<ConfigCompetitiveStatus> items = mDb.settingDao().getConfigCompetitiveStatus(TokenManager.getInstance().getUser().getCompany_id());
                for (ConfigCompetitiveStatus item : items) {
                    emitter.onNext(item);
                }
                emitter.onComplete();
            }
        }, BACK_PRESSURE_STRATEGY).subscribeOn(SCHEDULER);
    }

    /**
     * 依ID取得競爭狀態設定
     */
    public Single<ConfigCompetitiveStatus> getConfigCompetitiveStatusById(final String id) {
        return Single.create(new SingleOnSubscribe<ConfigCompetitiveStatus>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<ConfigCompetitiveStatus> emitter)
                    throws Exception {
                ConfigCompetitiveStatus result = mDb.settingDao().getConfigCompetitiveStatusById(id);
                if (result != null)
                    emitter.onSuccess(result);
                else
                    emitter.onError(new Throwable("can't get ConfigCompetitiveStatus"));
            }
        }).subscribeOn(Schedulers.single());
    }

    /**
     * 取得彈簧蓄能圈產品設定
     */
    public Flowable<ConfigInquiryProductType> getConfigInquiryProductType() {
        return Flowable.create(new FlowableOnSubscribe<ConfigInquiryProductType>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<ConfigInquiryProductType> emitter)
                    throws Exception {
                List<ConfigInquiryProductType> items = mDb.settingDao().getConfigInquiryProductType(TokenManager.getInstance().getUser().getCompany_id());
                for (ConfigInquiryProductType item : items) {
                    emitter.onNext(item);
                }
                emitter.onComplete();
            }
        }, BACK_PRESSURE_STRATEGY).subscribeOn(SCHEDULER);
    }

    /**
     * 依ID取得彈簧蓄能圈產品設定
     */
    public Single<ConfigInquiryProductType> getConfigInquiryProductTypeById(final String id) {
        return Single.create(new SingleOnSubscribe<ConfigInquiryProductType>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<ConfigInquiryProductType> emitter)
                    throws Exception {
                ConfigInquiryProductType result = mDb.settingDao().getConfigInquiryProductTypeById(id);
                if (result != null)
                    emitter.onSuccess(result);
                else
                    emitter.onError(new Throwable("can't get ConfigInquiryProductType"));
            }
        }).subscribeOn(Schedulers.single());
    }

    /**
     * 取得全年潛在銷售額設定
     */
    public Flowable<ConfigYearPotential> getConfigYearPotential() {
        return Flowable.create(new FlowableOnSubscribe<ConfigYearPotential>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<ConfigYearPotential> emitter)
                    throws Exception {
                List<ConfigYearPotential> items = mDb.settingDao().getConfigYearPotential(TokenManager.getInstance().getUser().getCompany_id());
                for (ConfigYearPotential item : items) {
                    emitter.onNext(item);
                }
                emitter.onComplete();
            }
        }, BACK_PRESSURE_STRATEGY).subscribeOn(SCHEDULER);
    }

    /**
     * 依ID取得全年潛在銷售額設定
     */
    public Single<ConfigYearPotential> getConfigYearPotentialById(final String id) {
        return Single.create(new SingleOnSubscribe<ConfigYearPotential>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<ConfigYearPotential> emitter)
                    throws Exception {
                ConfigYearPotential result = mDb.settingDao().getConfigYearPotentialById(id);
                if (result != null)
                    emitter.onSuccess(result);
                else
                    emitter.onError(new Throwable("can't get ConfigYearPotential"));
            }
        }).subscribeOn(Schedulers.single());
    }

    /**
     * 取得行業別
     */
    public Flowable<ConfigIndustry> getConfigIndustry() {
        return Flowable.create(new FlowableOnSubscribe<ConfigIndustry>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<ConfigIndustry> emitter)
                    throws Exception {
                List<ConfigIndustry> items = mDb.settingDao().getConfigIndustry(TokenManager.getInstance().getUser().getCompany_id());
                for (ConfigIndustry item : items) {
                    emitter.onNext(item);
                }
                emitter.onComplete();
            }
        }, BACK_PRESSURE_STRATEGY).subscribeOn(SCHEDULER);
    }

    /**
     * 依ID取得客戶分類
     */
    public Single<ConfigCustomerSapType> getConfigCustomerSapTypeById(final String id) {
        return Single.create(new SingleOnSubscribe<ConfigCustomerSapType>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<ConfigCustomerSapType> emitter)
                    throws Exception {
                ConfigCustomerSapType result = mDb.settingDao().getConfigCustomerSapTypeById(id);
                if (result != null)
                    emitter.onSuccess(result);
                else
                    emitter.onError(new Throwable("can't get ConfigCustomerSapType"));
            }
        }).subscribeOn(Schedulers.single());
    }

    /**
     * 取得客戶分類
     */
    public Flowable<ConfigCustomerSapType> getConfigCustomerSapType() {
        return Flowable.create(new FlowableOnSubscribe<ConfigCustomerSapType>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<ConfigCustomerSapType> emitter)
                    throws Exception {
                List<ConfigCustomerSapType> items = mDb.settingDao().getConfigCustomerSapType(TokenManager.getInstance().getUser().getCompany_id());
                for (ConfigCustomerSapType item : items) {
                    emitter.onNext(item);
                }
                emitter.onComplete();
            }
        }, BACK_PRESSURE_STRATEGY).subscribeOn(SCHEDULER);
    }

    /**
     * 依公司取得報價產品大類
     */
    public Flowable<ConfigQuotationProductCategory> getConfigQuotationProductCategoryByCompany() {
        return Flowable.create(new FlowableOnSubscribe<ConfigQuotationProductCategory>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<ConfigQuotationProductCategory> emitter)
                    throws Exception {
                List<ConfigQuotationProductCategory> items = mDb.settingDao()
                        .getConfigQuotationProductCategoryByCompany(TokenManager.getInstance().getUser().getCompany_id());
                for (ConfigQuotationProductCategory item : items) {
                    emitter.onNext(item);
                }
                emitter.onComplete();
            }
        }, BACK_PRESSURE_STRATEGY).subscribeOn(SCHEDULER);
    }

    /**
     * 取得報價產品大類
     */
    public Flowable<ConfigQuotationProductCategory> getConfigQuotationProductCategory() {
        return Flowable.create(new FlowableOnSubscribe<ConfigQuotationProductCategory>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<ConfigQuotationProductCategory> emitter)
                    throws Exception {
                List<ConfigQuotationProductCategory> items = mDb.settingDao().getConfigQuotationProductCategory();
                for (ConfigQuotationProductCategory item : items) {
                    emitter.onNext(item);
                }
                emitter.onComplete();
            }
        }, BACK_PRESSURE_STRATEGY).subscribeOn(SCHEDULER);
    }

    /**
     * 依ID取得報價產品大類
     */
    public Single<ConfigQuotationProductCategory> getConfigQuotationProductCategoryById(final String id) {
        return Single.create(new SingleOnSubscribe<ConfigQuotationProductCategory>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<ConfigQuotationProductCategory> emitter)
                    throws Exception {
                ConfigQuotationProductCategory result = mDb.settingDao().getConfigQuotationProductCategoryById(id);
                if (result != null)
                    emitter.onSuccess(result);
                else
                    emitter.onError(new Throwable("can't get ConfigQuotationProductCategory"));
            }
        }).subscribeOn(Schedulers.single());
    }

    /**
     * 取得報價產品所需報告
     */
    public Flowable<ConfigQuotationProductReport> getConfigQuotationProductReport() {
        return Flowable.create(new FlowableOnSubscribe<ConfigQuotationProductReport>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<ConfigQuotationProductReport> emitter)
                    throws Exception {
                List<ConfigQuotationProductReport> items =
                        mDb.settingDao().getConfigQuotationProductReport(TokenManager.getInstance().getUser().getCompany_id());
                for (ConfigQuotationProductReport item : items) {
                    emitter.onNext(item);
                }
                emitter.onComplete();
            }
        }, BACK_PRESSURE_STRATEGY).subscribeOn(SCHEDULER);
    }

    /**
     * 依ID取得客戶級別
     */
    public Single<ConfigCustomerLevel> getConfigCustomerLevelById(final String id) {
        return Single.create(new SingleOnSubscribe<ConfigCustomerLevel>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<ConfigCustomerLevel> emitter)
                    throws Exception {
                ConfigCustomerLevel result = mDb.settingDao().getConfigCustomerLevelById(id);
                if (result != null)
                    emitter.onSuccess(result);
                else
                    emitter.onError(new Throwable("can't get ConfigCustomerLevel"));
            }
        }).subscribeOn(Schedulers.single());
    }

    /**
     * 取得客戶級別
     */
    public Flowable<ConfigCustomerLevel> getConfigCustomerLevel() {
        return Flowable.create(new FlowableOnSubscribe<ConfigCustomerLevel>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<ConfigCustomerLevel> emitter)
                    throws Exception {
                List<ConfigCustomerLevel> items = mDb.settingDao().getConfigCustomerLevel(TokenManager.getInstance().getUser().getCompany_id());
                for (ConfigCustomerLevel item : items) {
                    emitter.onNext(item);
                }
                emitter.onComplete();
            }
        }, BACK_PRESSURE_STRATEGY).subscribeOn(SCHEDULER);
    }

    /**
     * 取得國家列表
     */
    public Flowable<ConfigCountry> getConfigCountry() {
        return Flowable.create(new FlowableOnSubscribe<ConfigCountry>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<ConfigCountry> emitter)
                    throws Exception {
                List<ConfigCountry> items = mDb.settingDao().getConfigCountry();
                for (ConfigCountry item : items) {
                    emitter.onNext(item);
                }
                emitter.onComplete();
            }
        }, BACK_PRESSURE_STRATEGY).subscribeOn(SCHEDULER);
    }

    /**
     * 依國家取得城市列表
     */
    public Flowable<ConfigCity> getConfigCityByCountry(final String countryId) {
        return Flowable.create(new FlowableOnSubscribe<ConfigCity>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<ConfigCity> emitter)
                    throws Exception {
                List<ConfigCity> items = mDb.settingDao().getConfigCityByCountry(countryId);
                for (ConfigCity item : items) {
                    emitter.onNext(item);
                }
                emitter.onComplete();
            }
        }, BACK_PRESSURE_STRATEGY).subscribeOn(SCHEDULER);
    }

    /**
     * 依ID取得業務
     */
    public Single<UserWithConfig> getUserWithConfigById(final String userId) {
        return Single.create(new SingleOnSubscribe<UserWithConfig>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<UserWithConfig> emitter)
                    throws Exception {
                UserWithConfig result = new UserWithConfig();
                User user = mDb.settingDao().getUserById(userId);
                if (user == null) {
                    emitter.onError(new Throwable("can't get user with config."));
                } else {
                    result.setUser(user);
                    result.setDepartment(mDb.settingDao().getDepartmentById(user.getDepartment_id()));
                    if (result.getDepartment() != null)
                        result.setCompany(mDb.settingDao().getCompanyById(result.getDepartment().getCompany_id()));
                    emitter.onSuccess(result);
                }
            }
        }).subscribeOn(Schedulers.single());
    }

    /**
     * 依ID取得業務
     */
    public Single<User> getUserById(final String userId) {
        return Single.create(new SingleOnSubscribe<User>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<User> emitter)
                    throws Exception {
                User user = mDb.settingDao().getUserById(userId);
                if (user == null)
                    emitter.onError(new Throwable("get user failed"));
                else
                    emitter.onSuccess(user);
            }
        }).subscribeOn(Schedulers.single());
    }

    /**
     * 取得所有業務
     */
    public Flowable<User> getUserByCompany(final String companyId) {
        return Flowable.create(new FlowableOnSubscribe<User>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<User> emitter)
                    throws Exception {
                List<User> items = mDb.settingDao().getUserByCompany(companyId);
                for (User item : items) {
                    emitter.onNext(item);
                }
                emitter.onComplete();
            }
        }, BACK_PRESSURE_STRATEGY).subscribeOn(SCHEDULER);
    }

    /**
     * 取得所有業務
     */
    public Flowable<User> getUser() {
        return Flowable.create(new FlowableOnSubscribe<User>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<User> emitter)
                    throws Exception {
                List<User> items = mDb.settingDao().getUser();
                for (User item : items) {
                    emitter.onNext(item);
                }
                emitter.onComplete();
            }
        }, BACK_PRESSURE_STRATEGY).subscribeOn(SCHEDULER);
    }

    /**
     * 儲存業務
     */
    public Single<String> saveUser(final User user) {
        return Single.create(new SingleOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<String> emitter) throws Exception {
                String userId = user.getId();
                if (TextUtils.isEmpty(userId)) {
                    emitter.onError(new Throwable("save user failed."));
                }
                user.setUpdated_at(new Date());
                if (mDb.settingDao().saveUser(user) == -1) {
                    emitter.onError(new Throwable("save user failed."));
                } else {
                    emitter.onSuccess(user.getId());
                }
            }
        }).subscribeOn(Schedulers.single());
    }

    /**
     * 依ID取得部門
     */
    public Single<Department> getDepartmentById(final String departmentId) {
        return Single.create(new SingleOnSubscribe<Department>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<Department> emitter) throws Exception {
                emitter.onSuccess(mDb.settingDao().getDepartmentById(departmentId));
            }
        }).subscribeOn(Schedulers.single());
    }

    /**
     * 取得所有部門
     */
    public Flowable<Department> getDepartment(final String companyId) {
        return Flowable.create(new FlowableOnSubscribe<Department>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<Department> emitter)
                    throws Exception {
                List<Department> items = mDb.settingDao().getDepartment(companyId);
                for (Department item : items) {
                    emitter.onNext(item);
                }
                emitter.onComplete();
            }
        }, BACK_PRESSURE_STRATEGY).subscribeOn(SCHEDULER);
    }

    /**
     * 取得合約類型設定
     */
    public Flowable<DepartmentContractItem> getDepartmentContractItemByDepartment(final String departmentId) {
        return Flowable.create(new FlowableOnSubscribe<DepartmentContractItem>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<DepartmentContractItem> emitter)
                    throws Exception {
                List<DepartmentContractItem> items = mDb.settingDao().getDepartmentContractItemByDepartment(departmentId);
                for (DepartmentContractItem item : items) {
                    emitter.onNext(item);
                }
                emitter.onComplete();
            }
        }, BACK_PRESSURE_STRATEGY).subscribeOn(SCHEDULER);
    }

    /**
     * 取得銷售機會設定
     */
    public Single<DepartmentSalesOpportunity> getDepartmentSalesOpportunityByParent(final String companyId) {
        return Single.create(new SingleOnSubscribe<DepartmentSalesOpportunity>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<DepartmentSalesOpportunity> emitter)
                    throws Exception {
                DepartmentSalesOpportunity result = getDepartmentSalesOpportunity(companyId);
                if (result == null)
                    emitter.onError(new Throwable("can't get DepartmentSalesOpportunity."));
                else
                    emitter.onSuccess(result);
            }
        }).subscribeOn(Schedulers.single());
    }

    private DepartmentSalesOpportunity getDepartmentSalesOpportunityById(String id) {
        DepartmentSalesOpportunity result = mDb.settingDao().getDepartmentSalesOpportunityById(id);
        return result;
    }

    private DepartmentSalesOpportunity getDepartmentSalesOpportunity(String companyId) {
        DepartmentSalesOpportunity result = mDb.settingDao().getDepartmentSalesOpportunityByParent(companyId);
        return result;
    }

    /**
     * 取得銷售機會小項設定
     */
    public Flowable<DepartmentSalesOpportunitySub> getDepartmentSalesOpportunitySub(final String salesOpportunityId) {
        return Flowable.create(new FlowableOnSubscribe<DepartmentSalesOpportunitySub>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<DepartmentSalesOpportunitySub> emitter)
                    throws Exception {
                List<DepartmentSalesOpportunitySub> result = mDb.settingDao().getDepartmentSalesOpportunitySub(salesOpportunityId);
                if (result == null)
                    emitter.onError(new Throwable("can't get DepartmentSalesOpportunity."));
                else
                    for (DepartmentSalesOpportunitySub sub : result)
                    {
                        emitter.onNext(sub);
                    }
                emitter.onComplete();
            }
        }, BACK_PRESSURE_STRATEGY).subscribeOn(SCHEDULER);
    }

    /**
     * 取得機會類型設定
     */
    public Flowable<ConfigSalesOpportunityType> getConfigSalesOpportunityType() {
        return Flowable.create(new FlowableOnSubscribe<ConfigSalesOpportunityType>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<ConfigSalesOpportunityType> emitter)
                    throws Exception {
                List<ConfigSalesOpportunityType> items = mDb.settingDao().getConfigSalesOpportunityType(TokenManager.getInstance().getUser().getCompany_id());
                for (ConfigSalesOpportunityType item : items) {
                    emitter.onNext(item);
                }
                emitter.onComplete();
            }
        }, BACK_PRESSURE_STRATEGY).subscribeOn(SCHEDULER);
    }

    /**
     * 依ID取得機會類型設定
     */
    public Single<ConfigSalesOpportunityType> getConfigSalesOpportunityTypeById(final String id) {
        return Single.create(new SingleOnSubscribe<ConfigSalesOpportunityType>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<ConfigSalesOpportunityType> emitter)
                    throws Exception {
                ConfigSalesOpportunityType item = mDb.settingDao().getConfigSalesOpportunityTypeById(id);
                if (item == null) {
                    emitter.onError(new Throwable("can't get ConfigSalesOpportunityType"));
                } else {
                    emitter.onSuccess(item);
                }
            }
        }).subscribeOn(Schedulers.single());
    }

    /**
     * 取得輸單原因設定
     */
    public Flowable<ConfigSalesOpportunityLoseType> getConfigSalesOpportunityLoseType() {
        return Flowable.create(new FlowableOnSubscribe<ConfigSalesOpportunityLoseType>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<ConfigSalesOpportunityLoseType> emitter)
                    throws Exception {
                List<ConfigSalesOpportunityLoseType> items = mDb.settingDao().getConfigSalesOpportunityLoseType(TokenManager.getInstance().getUser().getCompany_id());
                for (ConfigSalesOpportunityLoseType item : items) {
                    emitter.onNext(item);
                }
                emitter.onComplete();
            }
        }, BACK_PRESSURE_STRATEGY).subscribeOn(SCHEDULER);
    }

    /**
     * 取得贏單原因設定
     */
    public Flowable<ConfigSalesOpportunityWinType> getConfigSalesOpportunityWinType() {
        return Flowable.create(new FlowableOnSubscribe<ConfigSalesOpportunityWinType>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<ConfigSalesOpportunityWinType> emitter)
                    throws Exception {
                List<ConfigSalesOpportunityWinType> items = mDb.settingDao().getConfigSalesOpportunityWinType(TokenManager.getInstance().getUser().getCompany_id());
                for (ConfigSalesOpportunityWinType item : items) {
                    emitter.onNext(item);
                }
                emitter.onComplete();
            }
        }, BACK_PRESSURE_STRATEGY).subscribeOn(SCHEDULER);
    }

    /**
     * 取得機會來源設定
     */
    public Flowable<ConfigProjectSource> getConfigProjectSource() {
        return Flowable.create(new FlowableOnSubscribe<ConfigProjectSource>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<ConfigProjectSource> emitter)
                    throws Exception {
                List<ConfigProjectSource> items = mDb.settingDao().getDepartmentProjectSource(TokenManager.getInstance().getUser().getCompany_id());
                for (ConfigProjectSource item : items) {
                    emitter.onNext(item);
                }
                emitter.onComplete();
            }
        }, BACK_PRESSURE_STRATEGY).subscribeOn(SCHEDULER);
    }

    /**
     * 依ID取得機會來源設定
     */
    public Single<ConfigProjectSource> getConfigProjectSourceById(final String id) {
        return Single.create(new SingleOnSubscribe<ConfigProjectSource>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<ConfigProjectSource> emitter)
                    throws Exception {
                ConfigProjectSource items = mDb.settingDao().getDepartmentProjectSourceById(id);
                if (items == null)
                    emitter.onError(new Throwable("can't get ConfigProjectSource"));
                else
                    emitter.onSuccess(items);
            }
        }).subscribeOn(Schedulers.single());
    }

    /**
     * 取得報銷項目設定
     */
    public Flowable<ConfigReimbursementItem> getConfigReimbursementItem() {
        return Flowable.create(new FlowableOnSubscribe<ConfigReimbursementItem>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<ConfigReimbursementItem> emitter)
                    throws Exception {
                List<ConfigReimbursementItem> items = mDb.settingDao().getConfigReimbursementItem(TokenManager.getInstance().getUser().getCompany_id());
                for (ConfigReimbursementItem item : items) {
                    emitter.onNext(item);
                }
                emitter.onComplete();
            }
        }, BACK_PRESSURE_STRATEGY).subscribeOn(SCHEDULER);
    }

    /**
     * 取得報銷項目設定
     */
    public Single<ConfigReimbursementItem> getConfigReimbursementItemById(final String id) {
        return Single.create(new SingleOnSubscribe<ConfigReimbursementItem>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<ConfigReimbursementItem> emitter)
                    throws Exception {
                ConfigReimbursementItem config = mDb.settingDao().getConfigReimbursementItemById(id);
                if (config == null)
                    emitter.onError(new Throwable("can't get ConfigReimbursementItem"));
                else
                    emitter.onSuccess(config);
            }
        }).subscribeOn(Schedulers.single());
    }

    /**
     * 取得樣品單總面價金額設定
     */
    public Flowable<ConfigSampleAmountRange> getConfigSampleAmountRange() {
        return Flowable.create(new FlowableOnSubscribe<ConfigSampleAmountRange>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<ConfigSampleAmountRange> emitter)
                    throws Exception {
                List<ConfigSampleAmountRange> items = mDb.settingDao().getConfigSampleAmountRange(TokenManager.getInstance().getUser().getCompany_id());
                for (ConfigSampleAmountRange item : items) {
                    emitter.onNext(item);
                }
                emitter.onComplete();
            }
        }, BACK_PRESSURE_STRATEGY).subscribeOn(SCHEDULER);
    }

    /**
     * 取得樣品單總面價金額設定
     */
    public Single<ConfigSampleAmountRange> getConfigSampleAmountRangeById(final String id) {
        return Single.create(new SingleOnSubscribe<ConfigSampleAmountRange>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<ConfigSampleAmountRange> emitter)
                    throws Exception {
                ConfigSampleAmountRange config = mDb.settingDao().getConfigSampleAmountRangeById(id);
                if (config == null)
                    emitter.onError(new Throwable("can't get ConfigSampleAmountRange"));
                else
                    emitter.onSuccess(config);
            }
        }).subscribeOn(Schedulers.single());
    }

    /**
     * 取得樣品進口方式設定
     */
    public Flowable<ConfigSampleImportMethod> getConfigSampleImportMethod() {
        return Flowable.create(new FlowableOnSubscribe<ConfigSampleImportMethod>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<ConfigSampleImportMethod> emitter)
                    throws Exception {
                List<ConfigSampleImportMethod> items = mDb.settingDao().getConfigSampleImportMethod(TokenManager.getInstance().getUser().getCompany_id());
                for (ConfigSampleImportMethod item : items) {
                    emitter.onNext(item);
                }
                emitter.onComplete();
            }
        }, BACK_PRESSURE_STRATEGY).subscribeOn(SCHEDULER);
    }

    /**
     * 取得樣品進口方式設定
     */
    public Single<ConfigSampleImportMethod> getConfigSampleImportMethodById(final String id) {
        return Single.create(new SingleOnSubscribe<ConfigSampleImportMethod>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<ConfigSampleImportMethod> emitter)
                    throws Exception {
                ConfigSampleImportMethod config = mDb.settingDao().getConfigSampleImportMethodById(id);
                if (config == null)
                    emitter.onError(new Throwable("can't get ConfigSampleImportMethod"));
                else
                    emitter.onSuccess(config);
            }
        }).subscribeOn(Schedulers.single());
    }

    /**
     * 取得樣品國內運輸設定
     */
    public Flowable<ConfigSamplePaymentMethod> getConfigSamplePaymentMethod() {
        return Flowable.create(new FlowableOnSubscribe<ConfigSamplePaymentMethod>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<ConfigSamplePaymentMethod> emitter)
                    throws Exception {
                List<ConfigSamplePaymentMethod> items = mDb.settingDao().getConfigSamplePaymentMethod(TokenManager.getInstance().getUser().getCompany_id());
                for (ConfigSamplePaymentMethod item : items) {
                    emitter.onNext(item);
                }
                emitter.onComplete();
            }
        }, BACK_PRESSURE_STRATEGY).subscribeOn(SCHEDULER);
    }

    /**
     * 取得樣品國內運輸設定
     */
    public Single<ConfigSamplePaymentMethod> getConfigSamplePaymentMethodById(final String id) {
        return Single.create(new SingleOnSubscribe<ConfigSamplePaymentMethod>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<ConfigSamplePaymentMethod> emitter)
                    throws Exception {
                ConfigSamplePaymentMethod config = mDb.settingDao().getConfigSamplePaymentMethodById(id);
                if (config == null)
                    emitter.onError(new Throwable("can't get ConfigSamplePaymentMethod"));
                else
                    emitter.onSuccess(config);
            }
        }).subscribeOn(Schedulers.single());
    }

    /**
     * 取得樣品種類設定
     */
    public Flowable<ConfigSampleProductType> getConfigSampleProductType() {
        return Flowable.create(new FlowableOnSubscribe<ConfigSampleProductType>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<ConfigSampleProductType> emitter)
                    throws Exception {
                List<ConfigSampleProductType> items = mDb.settingDao().getConfigSampleProductType(TokenManager.getInstance().getUser().getCompany_id());
                for (ConfigSampleProductType item : items) {
                    emitter.onNext(item);
                }
                emitter.onComplete();
            }
        }, BACK_PRESSURE_STRATEGY).subscribeOn(SCHEDULER);
    }

    /**
     * 取得送樣理由設定
     */
    public Flowable<ConfigSampleReason> getConfigSampleReason() {
        return Flowable.create(new FlowableOnSubscribe<ConfigSampleReason>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<ConfigSampleReason> emitter)
                    throws Exception {
                List<ConfigSampleReason> items = mDb.settingDao().getConfigSampleReason(TokenManager.getInstance().getUser().getCompany_id());
                for (ConfigSampleReason item : items) {
                    emitter.onNext(item);
                }
                emitter.onComplete();
            }
        }, BACK_PRESSURE_STRATEGY).subscribeOn(SCHEDULER);
    }

    /**
     * 取得送樣理由設定
     */
    public Single<ConfigSampleReason> getConfigSampleReasonById(final String id) {
        return Single.create(new SingleOnSubscribe<ConfigSampleReason>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<ConfigSampleReason> emitter)
                    throws Exception {
                ConfigSampleReason config = mDb.settingDao().getConfigSampleReasonById(id);
                if (config == null)
                    emitter.onError(new Throwable("can't get ConfigSampleReason"));
                else
                    emitter.onSuccess(config);
            }
        }).subscribeOn(Schedulers.single());
    }

    /**
     * 取得送樣來源設定
     */
    public Flowable<ConfigSampleSource> getConfigSampleSource() {
        return Flowable.create(new FlowableOnSubscribe<ConfigSampleSource>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<ConfigSampleSource> emitter)
                    throws Exception {
                List<ConfigSampleSource> items = mDb.settingDao().getConfigSampleSource(TokenManager.getInstance().getUser().getCompany_id());
                for (ConfigSampleSource item : items) {
                    emitter.onNext(item);
                }
                emitter.onComplete();
            }
        }, BACK_PRESSURE_STRATEGY).subscribeOn(SCHEDULER);
    }

    /**
     * 依ID取得送樣來源設定
     */
    public Single<ConfigSampleSource> getConfigSampleSourceById(final String id) {
        return Single.create(new SingleOnSubscribe<ConfigSampleSource>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<ConfigSampleSource> emitter)
                    throws Exception {
                ConfigSampleSource config = mDb.settingDao().getConfigSampleSourceById(id);
                if (config == null)
                    emitter.onError(new Throwable("can't get ConfigSampleSource"));
                else
                    emitter.onSuccess(config);
            }
        }).subscribeOn(Schedulers.single());
    }

    /**
     * 取得隱藏欄位設定
     */
    public Single<ConfigCompanyHiddenField> getConfigCompanyHiddenField(final String companyId) {
        return Single.create(new SingleOnSubscribe<ConfigCompanyHiddenField>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<ConfigCompanyHiddenField> emitter)
                    throws Exception {
                ConfigCompanyHiddenField config = mDb.settingDao().getConfigCompanyHiddenField(companyId);
                if (config == null)
                    emitter.onError(new Throwable("can't get ConfigCompanyHiddenField"));
                else
                    emitter.onSuccess(config);
            }
        }).subscribeOn(Schedulers.single());
    }

    /**
     * 取得銷售助理設定
     */
    public Flowable<Admin> getAdmin() {
        return Flowable.create(new FlowableOnSubscribe<Admin>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<Admin> emitter)
                    throws Exception {
                List<Admin> items = mDb.settingDao().getAdminByCompany(TokenManager.getInstance().getUser().getCompany_id());
                for (Admin item : items) {
                    emitter.onNext(item);
                }
                emitter.onComplete();
            }
        }, BACK_PRESSURE_STRATEGY).subscribeOn(SCHEDULER);
    }

    /**
     * 依ID取得銷售助理設定
     */
    public Single<Admin> getAdminById(final String id) {
        return Single.create(new SingleOnSubscribe<Admin>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<Admin> emitter)
                    throws Exception {
                Admin config = mDb.settingDao().getAdminById(id);
                if (config == null)
                    emitter.onError(new Throwable("can't get Admin"));
                else
                    emitter.onSuccess(config);
            }
        }).subscribeOn(Schedulers.single());
    }

    /**
     * 依ID取得公司設定
     */
    public Single<Company> getCompanyById(final String id) {
        return Single.create(new SingleOnSubscribe<Company>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<Company> emitter)
                    throws Exception {
                Company config = mDb.settingDao().getCompanyById(id);
                if (config == null)
                    emitter.onError(new Throwable("can't get Company"));
                else
                    emitter.onSuccess(config);
            }
        }).subscribeOn(Schedulers.single());
    }

    /**
     * 取得所有公司
     */
    public Flowable<Company> getCompany() {
        return Flowable.create(new FlowableOnSubscribe<Company>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<Company> emitter)
                    throws Exception {
                List<Company> items = mDb.settingDao().getCompany();
                for (Company item : items) {
                    emitter.onNext(item);
                }
                emitter.onComplete();
            }
        }, BACK_PRESSURE_STRATEGY).subscribeOn(SCHEDULER);
    }

    /**
     * 取得客戶組設定
     */
    public Flowable<ConfigCustomerSapGroup> getConfigCustomerSapGroup() {
        return Flowable.create(new FlowableOnSubscribe<ConfigCustomerSapGroup>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<ConfigCustomerSapGroup> emitter)
                    throws Exception {
                List<ConfigCustomerSapGroup> items = mDb.settingDao().getConfigCustomerSapGroup(TokenManager.getInstance().getUser().getCompany_id());
                for (ConfigCustomerSapGroup item : items) {
                    emitter.onNext(item);
                }
                emitter.onComplete();
            }
        }, BACK_PRESSURE_STRATEGY).subscribeOn(SCHEDULER);
    }

    /**
     * 依ID取得客戶組設定
     */
    public Single<ConfigCustomerSapGroup> getConfigCustomerSapGroupById(final String id) {
        return Single.create(new SingleOnSubscribe<ConfigCustomerSapGroup>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<ConfigCustomerSapGroup> emitter)
                    throws Exception {
                ConfigCustomerSapGroup config = mDb.settingDao().getConfigCustomerSapGroupById(id);
                if (config == null)
                    emitter.onError(new Throwable("can't get ConfigCustomerSapGroup"));
                else
                    emitter.onSuccess(config);
            }
        }).subscribeOn(Schedulers.single());
    }

    /**
     * 取得客戶來源設定
     */
    public Flowable<ConfigCustomerSource> getConfigCustomerSource() {
        return Flowable.create(new FlowableOnSubscribe<ConfigCustomerSource>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<ConfigCustomerSource> emitter)
                    throws Exception {
                List<ConfigCustomerSource> items = mDb.settingDao().getConfigCustomerSource(TokenManager.getInstance().getUser().getCompany_id());
                for (ConfigCustomerSource item : items) {
                    emitter.onNext(item);
                }
                emitter.onComplete();
            }
        }, BACK_PRESSURE_STRATEGY).subscribeOn(SCHEDULER);
    }

    /**
     * 依ID取得客戶來源設定
     */
    public Single<ConfigCustomerSource> getConfigCustomerSourceById(final String id) {
        return Single.create(new SingleOnSubscribe<ConfigCustomerSource>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<ConfigCustomerSource> emitter)
                    throws Exception {
                ConfigCustomerSource config = mDb.settingDao().getConfigCustomerSourceById(id);
                if (config == null)
                    emitter.onError(new Throwable("can't get ConfigCustomerSource"));
                else
                    emitter.onSuccess(config);
            }
        }).subscribeOn(Schedulers.single());
    }

    /**
     * 取得量產產品設定
     */
    public Flowable<ConfigNewProjectProduction> getConfigNewProjectProduction() {
        return Flowable.create(new FlowableOnSubscribe<ConfigNewProjectProduction>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<ConfigNewProjectProduction> emitter)
                    throws Exception {
                List<ConfigNewProjectProduction> items = mDb.settingDao().getConfigNewProjectProduction(TokenManager.getInstance().getUser().getCompany_id());
                for (ConfigNewProjectProduction item : items) {
                    emitter.onNext(item);
                }
                emitter.onComplete();
            }
        }, BACK_PRESSURE_STRATEGY).subscribeOn(SCHEDULER);
    }

    /**
     * 依ID取得量產產品設定
     */
    public Single<ConfigNewProjectProduction> getConfigNewProjectProductionById(final String id) {
        return Single.create(new SingleOnSubscribe<ConfigNewProjectProduction>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<ConfigNewProjectProduction> emitter)
                    throws Exception {
                ConfigNewProjectProduction config = mDb.settingDao().getConfigNewProjectProductionById(id);
                if (config == null)
                    emitter.onError(new Throwable("can't get ConfigCustomerSource"));
                else
                    emitter.onSuccess(config);
            }
        }).subscribeOn(Schedulers.single());
    }

    /**
     * 取得產品分類設定
     */
    public Flowable<ConfigProductCategorySub> getConfigProductCategorySub() {
        return Flowable.create(new FlowableOnSubscribe<ConfigProductCategorySub>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<ConfigProductCategorySub> emitter)
                    throws Exception {
                List<ConfigProductCategorySub> items = mDb.settingDao().getConfigProductCategorySub(TokenManager.getInstance().getUser().getCompany_id());
                for (ConfigProductCategorySub item : items) {
                    emitter.onNext(item);
                }
                emitter.onComplete();
            }
        }, BACK_PRESSURE_STRATEGY).subscribeOn(SCHEDULER);
    }

    /**
     * 依ID取得產品分類設定
     */
    public Single<ConfigProductCategorySub> getConfigProductCategorySubById(final String id) {
        return Single.create(new SingleOnSubscribe<ConfigProductCategorySub>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<ConfigProductCategorySub> emitter)
                    throws Exception {
                ConfigProductCategorySub config = mDb.settingDao().getConfigProductCategorySubById(id);
                if (config == null)
                    emitter.onError(new Throwable("can't get ConfigProductCategorySub"));
                else
                    emitter.onSuccess(config);
            }
        }).subscribeOn(Schedulers.single());
    }

    /**
     * 取得稅率設定
     */
    public Flowable<ConfigTax> getConfigTax() {
        return Flowable.create(new FlowableOnSubscribe<ConfigTax>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<ConfigTax> emitter)
                    throws Exception {
                List<ConfigTax> items = mDb.settingDao().getConfigTax(TokenManager.getInstance().getUser().getCompany_id());
                for (ConfigTax item : items) {
                    emitter.onNext(item);
                }
                emitter.onComplete();
            }
        }, BACK_PRESSURE_STRATEGY).subscribeOn(SCHEDULER);
    }

    /**
     * 依ID取得稅率設定
     */
    public Single<ConfigTax> getConfigTaxById(final String id) {
        return Single.create(new SingleOnSubscribe<ConfigTax>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<ConfigTax> emitter)
                    throws Exception {
                ConfigTax config = mDb.settingDao().getConfigTaxById(id);
                if (config == null)
                    emitter.onError(new Throwable("can't get ConfigTax"));
                else
                    emitter.onSuccess(config);
            }
        }).subscribeOn(Schedulers.single());
    }
    /*--------------------------------------下載紀錄-------------------------------------------*/

    /**
     * 儲存下載紀錄
     */
    public Completable saveDownloadLog(final DownloadLog log) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(@NonNull CompletableEmitter e) throws Exception {
                if (mDb.localDao().saveDownloadLog(log) == -1) {
                    e.onError(new Throwable("save ReadLog failed."));
                } else {
                    e.onComplete();
                }
            }
        }).subscribeOn(SCHEDULER);
    }

    /**
     * 取得下載紀錄
     */
    public Single<DownloadLog> getDownloadLog(final String parent_id) {
        return Single.create(new SingleOnSubscribe<DownloadLog>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<DownloadLog> emitter)
                    throws Exception {
                DownloadLog log = mDb.localDao().getDownloadLog(parent_id);
                if (log == null) {
                    emitter.onError(new Throwable("can't get ReadLog."));
                } else {
                    emitter.onSuccess(log);
                }
            }
        }).subscribeOn(Schedulers.single());
    }

    /*--------------------------------------已讀紀錄-------------------------------------------*/

    /**
     * 儲存已讀紀錄
     */
    public Completable saveReadLog(final List<ReadLog> readLog) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(@NonNull CompletableEmitter e) throws Exception {
                if (mDb.localDao().saveReadLog(readLog).contains(-1L)) {
                    e.onError(new Throwable("save ReadLog failed."));
                } else {
                    e.onComplete();
                }
            }
        }).subscribeOn(SCHEDULER);
    }

    /**
     * 取得傳入列表中的未讀數量
     */
    public Single<Integer> getUnreadCount(final List<String> parentIdList) {
        return Single.create(new SingleOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<Integer> emitter)
                    throws Exception {
                List<ReadLog> readLogList = mDb.localDao().getReadLog(parentIdList);
                if (readLogList == null) {
                    emitter.onError(new Throwable("can't get ReadLog."));
                } else {
                    int unreadCount = parentIdList.size() - readLogList.size();
                    emitter.onSuccess(unreadCount);
                }
            }
        }).subscribeOn(Schedulers.single());
    }

    /*--------------------------------------月報-------------------------------------------*/

    /**
     * 儲存月報
     */
    public Completable saveMonthReport(final MonthReport report) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(@NonNull CompletableEmitter e) throws Exception {
                if (TextUtils.isEmpty(report.getId())) {
                    String reportId = generateHashId(MonthReport.class.getClass().getSimpleName());
                    report.setId(reportId);
                }
                report.setUpdated_at(new Date());
                if (mDb.recordDao().saveMonthReport(report) == -1) {
                    e.onError(new Throwable("save MonthReport failed."));
                } else {
                    e.onComplete();
                }
            }
        }).subscribeOn(SCHEDULER);
    }

    /**
     * 取得月報
     */
    public Single<MonthReport> getMonthReport(final String time, final String userId) {
        return Single.create(new SingleOnSubscribe<MonthReport>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<MonthReport> emitter)
                    throws Exception {
                MonthReport report = mDb.recordDao().getMonthReportByTime(time, userId);
                if (report == null) {
                    emitter.onError(new Throwable("can't get MonthReport."));
                } else {
                    emitter.onSuccess(report);
                }
            }
        }).subscribeOn(Schedulers.single());
    }

    /**
     * 儲存報告
     */
    public Long saveReport(final Report report) {
        if (TextUtils.isEmpty(report.getId())) {
            String reportId = generateHashId(Report.class.getClass().getSimpleName());
            report.setId(reportId);
        }
        report.setUpdated_at(new Date());
        Logger.e(TAG, "saveReport:" + new Gson().toJson(report));
        return mDb.recordDao().saveReport(report);
    }

    /**
     * 取得報告
     */
    public Single<Report> getReport(final String parentId, final String userId) {
        return Single.create(new SingleOnSubscribe<Report>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<Report> emitter)
                    throws Exception {
                Report report = mDb.recordDao().getReportByParent(parentId, userId);
                if (report == null) {
                    emitter.onError(new Throwable("can't get Report."));
                } else {
                    emitter.onSuccess(report);
                }
            }
        }).subscribeOn(Schedulers.single());
    }

    /*--------------------------------------登出處理-------------------------------------------*/

    /**
     * 登出(清空)
     */
    public Completable logout(final String userId) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(@NonNull CompletableEmitter e) throws Exception {
//                mDb.settingDao().deleteAuthorityCustomer(mDb.settingDao().getAuthorityCustomer());
//                mDb.settingDao().deleteAuthorityProject(mDb.settingDao().getAuthorityProject());
//                mDb.localDao().deleteSyncLog(mDb.localDao().getSyncLog(userId));
                e.onComplete();
            }
        }).subscribeOn(SCHEDULER);
    }

    /*--------------------------------------儲存-------------------------------------------*/

    /**
     * 儲存設定資料
     */
    public Completable saveSetting(final SyncSetting syncSetting) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(@NonNull CompletableEmitter emitter) throws Exception {
                boolean result = true;
                if (syncSetting.getCompany() != null) {
                    List<Long> saveResult = mDb.settingDao().saveCompany(syncSetting.getCompany());
                    if (saveResult.contains(-1L)) {
                        result = false;
                    }
                }
                if (syncSetting.getConfigCity() != null) {
                    if (mDb.settingDao().saveConfigCity(syncSetting.getConfigCity()).contains(-1L))
                    {
                        result = false;
                    }
                }
                if (syncSetting.getConfigCustomerLevel() != null) {
                    if (mDb.settingDao().saveConfigCustomerLevel(syncSetting.getConfigCustomerLevel()).contains(-1L))
                    {
                        result = false;
                    }
                }
                if (syncSetting.getConfigCustomerSapType() != null) {
                    if (mDb.settingDao().saveConfigCustomerSapType(syncSetting.getConfigCustomerSapType()).contains(-1L))
                    {
                        result = false;
                    }
                }
                if (syncSetting.getConfigIndustry() != null) {
                    if (mDb.settingDao().saveConfigIndustry(syncSetting.getConfigIndustry()).contains(-1L))
                    {
                        result = false;
                    }
                }
                if (syncSetting.getConfigCountry() != null) {
                    if (mDb.settingDao().saveConfigCountry(syncSetting.getConfigCountry()).contains(-1L))
                    {
                        result = false;
                    }
                }
                if (syncSetting.getConfigCurrency() != null) {
                    if (mDb.settingDao().saveConfigCurrency(syncSetting.getConfigCurrency()).contains(-1L))
                    {
                        result = false;
                    }
                }
                if (syncSetting.getConfigReimbursementItem() != null) {
                    if (mDb.settingDao().saveConfigReimbursementItem(syncSetting.getConfigReimbursementItem()).contains(-1L))
                    {
                        result = false;
                    }
                }
                if (syncSetting.getCurrencyRate() != null) {
                    if (mDb.settingDao().saveCurrencyRate(syncSetting.getCurrencyRate()).contains(-1L))
                    {
                        result = false;
                    }
                }
                if (syncSetting.getDepartment() != null) {
                    if (mDb.settingDao().saveDepartment(syncSetting.getDepartment()).contains(-1L))
                    {
                        result = false;
                    }
                }
                if (syncSetting.getDepartmentContractItem() != null) {
                    if (mDb.settingDao().saveDepartmentContractItem(syncSetting.getDepartmentContractItem()).contains(-1L))
                    {
                        result = false;
                    }
                }
                if (syncSetting.getDepartmentSalesOpportunity() != null) {
                    if (mDb.settingDao().saveDepartmentSalesOpportunity(syncSetting.getDepartmentSalesOpportunity()).contains(-1L))
                    {
                        result = false;
                    }
                }
                if (syncSetting.getDepartmentSalesOpportunitySub() != null) {
                    if (mDb.settingDao().saveDepartmentSalesOpportunitySub(syncSetting.getDepartmentSalesOpportunitySub()).contains(-1L))
                    {
                        result = false;
                    }
                }
                if (syncSetting.getUser() != null) {
                    if (mDb.settingDao().saveUser(syncSetting.getUser()).contains(-1L))
                    {
                        result = false;
                    }
                }
                if (syncSetting.getUserLeave() != null) {
                    if (mDb.settingDao().saveUserLeave(syncSetting.getUserLeave()).contains(-1L))
                    {
                        result = false;
                    }
                }
                if (syncSetting.getConfigQuotationProductCategory() != null) {
                    if (mDb.settingDao().saveConfigQuotationProductCategory(syncSetting.getConfigQuotationProductCategory()).contains(-1L))
                    {
                        result = false;
                    }
                }
                if (syncSetting.getConfigQuotationProductReport() != null) {
                    if (mDb.settingDao().saveConfigQuotationProductReport(syncSetting.getConfigQuotationProductReport()).contains(-1L))
                    {
                        result = false;
                    }
                }
                if (syncSetting.getConfigSampleAmountRange() != null) {
                    if (mDb.settingDao().saveConfigSampleAmountRange(syncSetting.getConfigSampleAmountRange()).contains(-1L))
                    {
                        result = false;
                    }
                }
                if (syncSetting.getConfigSampleImportMethod() != null) {
                    if (mDb.settingDao().saveConfigSampleImportMethod(syncSetting.getConfigSampleImportMethod()).contains(-1L))
                    {
                        result = false;
                    }
                }
                if (syncSetting.getConfigSamplePaymentMethod() != null) {
                    if (mDb.settingDao().saveConfigSamplePaymentMethod(syncSetting.getConfigSamplePaymentMethod()).contains(-1L))
                    {
                        result = false;
                    }
                }
                if (syncSetting.getConfigSampleProductType() != null) {
                    if (mDb.settingDao().saveConfigSampleProductType(syncSetting.getConfigSampleProductType()).contains(-1L))
                    {
                        result = false;
                    }
                }
                if (syncSetting.getConfigSampleReason() != null) {
                    if (mDb.settingDao().saveConfigSampleReason(syncSetting.getConfigSampleReason()).contains(-1L))
                    {
                        result = false;
                    }
                }
                if (syncSetting.getConfigSampleSource() != null) {
                    if (mDb.settingDao().saveConfigSampleSource(syncSetting.getConfigSampleSource()).contains(-1L))
                    {
                        result = false;
                    }
                }
                if (syncSetting.getConfigSalesOpportunityType() != null) {
                    if (mDb.settingDao().saveConfigSalesOpportunityType(syncSetting.getConfigSalesOpportunityType()).contains(-1L))
                    {
                        result = false;
                    }
                }
                if (syncSetting.getConfigSalesOpportunityLoseType() != null) {
                    if (mDb.settingDao().saveConfigSalesOpportunityLoseType(syncSetting.getConfigSalesOpportunityLoseType()).contains(-1L))
                    {
                        result = false;
                    }
                }
                if (syncSetting.getConfigSalesOpportunityWinType() != null) {
                    if (mDb.settingDao().saveConfigSalesOpportunityWinType(syncSetting.getConfigSalesOpportunityWinType()).contains(-1L))
                    {
                        result = false;
                    }
                }
                if (syncSetting.getConfigProjectSource() != null) {
                    if (mDb.settingDao().saveDepartmentProjectSource(syncSetting.getConfigProjectSource()).contains(-1L))
                    {
                        result = false;
                    }
                }
                if (syncSetting.getAdmin() != null) {
                    if (mDb.settingDao().saveAdmin(syncSetting.getAdmin()).contains(-1L))
                    {
                        result = false;
                    }
                }
                if (syncSetting.getConfigCustomerSapGroup() != null) {
                    if (mDb.settingDao().saveConfigCustomerSapGroup(syncSetting.getConfigCustomerSapGroup()).contains(-1L))
                    {
                        result = false;
                    }
                }
                if (syncSetting.getConfigCustomerSource() != null) {
                    if (mDb.settingDao().saveConfigCustomerSource(syncSetting.getConfigCustomerSource()).contains(-1L))
                    {
                        result = false;
                    }
                }
                if (syncSetting.getConfigNewProjectProduction() != null) {
                    if (mDb.settingDao().saveConfigNewProjectProduction(syncSetting.getConfigNewProjectProduction()).contains(-1L))
                    {
                        result = false;
                    }
                }
                if (syncSetting.getConfigOffice() != null) {
                    if (mDb.settingDao().saveConfigOffice(syncSetting.getConfigOffice()).contains(-1L))
                    {
                        result = false;
                    }
                }
                if (syncSetting.getConfigExpressDestination() != null) {
                    if (mDb.settingDao().saveConfigExpressDestination(syncSetting.getConfigExpressDestination()).contains(-1L))
                    {
                        result = false;
                    }
                }
                if (syncSetting.getConfigYearPotential() != null) {
                    if (mDb.settingDao().saveConfigYearPotential(syncSetting.getConfigYearPotential()).contains(-1L))
                    {
                        result = false;
                    }
                }
                if (syncSetting.getConfigInquiryProductType() != null) {
                    if (mDb.settingDao().saveConfigInquiryProductType(syncSetting.getConfigInquiryProductType()).contains(-1L))
                    {
                        result = false;
                    }
                }
                if (syncSetting.getConfigCompetitiveStatus() != null) {
                    if (mDb.settingDao().saveConfigCompetitiveStatus(syncSetting.getConfigCompetitiveStatus()).contains(-1L))
                    {
                        result = false;
                    }
                }
                if (syncSetting.getConfigSgbAdvantage() != null) {
                    if (mDb.settingDao().saveConfigSgbAdvantage(syncSetting.getConfigSgbAdvantage()).contains(-1L))
                    {
                        result = false;
                    }
                }
                if (syncSetting.getConfigSgbDisadvantage() != null) {
                    if (mDb.settingDao().saveConfigSgbDisadvantage(syncSetting.getConfigSgbDisadvantage()).contains(-1L))
                    {
                        result = false;
                    }
                }
                if (syncSetting.getConfigTrend() != null) {
                    if (mDb.settingDao().saveConfigTrend(syncSetting.getConfigTrend()).contains(-1L))
                    {
                        result = false;
                    }
                }
                if (syncSetting.getConfigSealedState() != null) {
                    if (mDb.settingDao().saveConfigSealedState(syncSetting.getConfigSealedState()).contains(-1L))
                    {
                        result = false;
                    }
                }
                if (syncSetting.getConfigProductCategorySub() != null) {
                    if (mDb.settingDao().saveConfigProductCategorySub(syncSetting.getConfigProductCategorySub()).contains(-1L))
                    {
                        result = false;
                    }
                }
                if (syncSetting.getConfigTax() != null) {
                    if (mDb.settingDao().saveConfigTax(syncSetting.getConfigTax()).contains(-1L))
                    {
                        result = false;
                    }
                }
                if (syncSetting.getConfigCompanyHiddenField() != null) {

                    if (mDb.settingDao().getConfigCompanyHiddenField() != null)
                        mDb.settingDao().deleteConfigCompanyHiddenField(mDb.settingDao().getConfigCompanyHiddenField());

                    if (mDb.settingDao().saveConfigCompanyHiddenField(syncSetting.getConfigCompanyHiddenField()).contains(-1L))
                    {
                        result = false;
                    }
                }
                if (syncSetting.getAuthorizeCustomer() != null) {
                    if (mDb.settingDao().getAuthorityCustomer() != null)
                        mDb.settingDao().deleteAuthorityCustomer(mDb.settingDao().getAuthorityCustomer());

                    if (mDb.settingDao().saveAuthorityCustomer(syncSetting.getAuthorizeCustomer()).contains(-1L))
                    {
                        result = false;
                    }
                }
                if (syncSetting.getAuthorizeProject() != null) {
                    if (mDb.settingDao().getAuthorityProject() != null)
                        mDb.settingDao().deleteAuthorityProject(mDb.settingDao().getAuthorityProject());

                    if (mDb.settingDao().saveAuthorityProject(syncSetting.getAuthorizeProject()).contains(-1L))
                    {
                        result = false;
                    }
                }
                if (result)
                    emitter.onComplete();
                else
                    emitter.onError(new Throwable("can't sync setting."));
            }
        }).subscribeOn(SCHEDULER);
    }

    /**
     * 取得要上傳的資料
     */
    public Single<SyncRecord> getRecord(final String userId) {
        return Single.create(new SingleOnSubscribe<Date>() {

            @Override
            public void subscribe(@NonNull SingleEmitter<Date> e) throws Exception {
                SyncLog syncLog = mDb.localDao().getLastSyncLog(0, userId);
                if (syncLog == null) {
                    syncLog = new SyncLog();
                    syncLog.setId("00");
                    syncLog.setUser_id(TokenManager.getInstance().getUser().getId());
                    syncLog.setSync_status(SyncStatus.SUCCESS);
                    syncLog.setIsFile(false);
                    mDb.localDao().saveSyncLog(syncLog);
                    e.onSuccess(syncLog.getCreated_at());
                } else {
                    e.onSuccess(syncLog.getCreated_at());
                }
            }
        }).map(new Function<Date, SyncRecord>() {
            @Override
            public SyncRecord apply(@NonNull Date date) throws Exception {
                Logger.e(TAG, "last success sync record time:" + TimeFormater.getInstance().toDateTimeFormat(date));
                SyncRecord syncRecord = new SyncRecord();
                syncRecord.setAbsent(mDb.recordDao().getAbsentAfterDate(date, new Date()));
                syncRecord.setContacter(mDb.recordDao().getContacterAfterDate(date, new Date()));
                syncRecord.setContactPerson(mDb.recordDao().getContactPersonAfterDate(date, new Date()));
                syncRecord.setContract(mDb.recordDao().getContractAfterDate(date, new Date()));
                syncRecord.setCustomer(mDb.recordDao().getCustomerAfterDate(date, new Date()));
                syncRecord.setOffice(mDb.recordDao().getOfficeAfterDate(date, new Date()));
                syncRecord.setParticipant(mDb.recordDao().getParticipantAfterDate(date, new Date()));
                syncRecord.setProject(mDb.recordDao().getProjectAfterDate(date, new Date()));
                syncRecord.setQuotation(mDb.recordDao().getQuotationAfterDate(date, new Date()));
                syncRecord.setQuotationProduct(mDb.recordDao().getQuotationProductAfterDate(date, new Date()));
                syncRecord.setReimbursement(mDb.recordDao().getReimbursementAfterDate(date, new Date()));
                syncRecord.setReimbursementItem(mDb.recordDao().getReimbursementItemAfterDate(date, new Date()));
                syncRecord.setSalesOpportunity(mDb.recordDao().getSalesOpportunityAfterDate(date, new Date()));
                syncRecord.setSalesOpportunitySub(mDb.recordDao().getSalesOpportunitySubAfterDate(date, new Date()));
                syncRecord.setSample(mDb.recordDao().getSampleAfterDate(date, new Date()));
                syncRecord.setSampleProduct(mDb.recordDao().getSampleProductAfterDate(date, new Date()));
                syncRecord.setTask(mDb.recordDao().getTaskAfterDate(date, new Date()));
                syncRecord.setTaskConversation(mDb.recordDao().getTaskConversationAfterDate(date, new Date()));
                syncRecord.setTaskMessage(mDb.recordDao().getTaskMessageAfterDate(date, new Date()));
                syncRecord.setTrip(mDb.recordDao().getTripAfterDate(date, new Date()));
                syncRecord.setTripStatusLog(mDb.recordDao().getTripStatusLogAfterDate(date, new Date()));
                syncRecord.setVisit(mDb.recordDao().getVisitAfterDate(date, new Date()));
//                syncRecord.setUserQuickMenu(mDb.recordDao().getUserQuickMenuAfterDate(date));
                syncRecord.setMonthReport(mDb.recordDao().getMonthReportAfterDate(date, new Date()));
                syncRecord.setExpress(mDb.recordDao().getExpressAfterDate(date, new Date()));
                syncRecord.setNewProjectProduction(mDb.recordDao().getNewProjectProductionAfterDate(date, new Date()));
                syncRecord.setSpecialPrice(mDb.recordDao().getSpecialPriceAfterDate(date, new Date()));
                syncRecord.setSpecialPriceProduct(mDb.recordDao().getSpecialPriceProductAfterDate(date, new Date()));
                syncRecord.setTravel(mDb.recordDao().getTravelAfterDate(date, new Date()));
                syncRecord.setNonStandardInquiry(mDb.recordDao().getNonStandardInquiryAfterDate(date, new Date()));
                syncRecord.setNonStandardInquiryProduct(mDb.recordDao().getNonStandardInquiryProductAfterDate(date, new Date()));
                syncRecord.setSpringRingInquiry(mDb.recordDao().getSpringRingInquiryAfterDate(date, new Date()));
                syncRecord.setSpringRingInquiryProduct(mDb.recordDao().getSpringRingInquiryProductAfterDate(date, new Date()));
                syncRecord.setSpecialShip(mDb.recordDao().getSpecialShipAfterDate(date, new Date()));
                syncRecord.setRepayment(mDb.recordDao().getRepaymentAfterDate(date, new Date()));
                syncRecord.setReport(mDb.recordDao().getReportAfterDate(date, new Date()));
                return syncRecord;
            }
        }).subscribeOn(Schedulers.single());
    }

    /**
     * 取得要上傳的圖片
     */
    public Single<SyncRecord> getSyncFile(final String userId) {
        return Single.create(new SingleOnSubscribe<Date>() {

            @Override
            public void subscribe(@NonNull SingleEmitter<Date> e) throws Exception {
                SyncLog syncLog = mDb.localDao().getLastSyncLog(1, userId);
                if (syncLog == null) {
                    syncLog = new SyncLog();
                    syncLog.setId("01");
                    syncLog.setUser_id(TokenManager.getInstance().getUser().getId());
                    syncLog.setSync_status(SyncStatus.SUCCESS);
                    syncLog.setIsFile(true);
                    mDb.localDao().saveSyncLog(syncLog);
                    e.onSuccess(syncLog.getCreated_at());
                } else {
                    e.onSuccess(syncLog.getCreated_at());
                }
            }
        }).map(new Function<Date, SyncRecord>() {
            @Override
            public SyncRecord apply(@NonNull Date date) throws Exception {
                Logger.e(TAG, "last success sync file time:" + TimeFormater.getInstance().toDateTimeFormat(date));
                SyncRecord syncFile = new SyncRecord();
                List<String> fileIds = mDb.recordDao().getFileIdAfterDate(date, new Date(), TokenManager.getInstance().getUser().getId());
                if (fileIds.size() > 0)
                    for (String fileId : fileIds)
                    {
                        syncFile.addFile(mDb.recordDao().getFileById(fileId));
                    }
                syncFile.setFilePivot(mDb.recordDao().getFilePivotAfterDate(date, new Date()));
                return syncFile;
            }
        }).subscribeOn(Schedulers.single());
    }

    /**
     * 同步紀錄資料
     */
    public Single<Date> saveRecord(final SyncRecord syncRecord, final Date lastSyncDate) {
        return Single.create(new SingleOnSubscribe<Date>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<Date> emitter) throws Exception {
                boolean result = true;
                Date lastDate = lastSyncDate;
                if (syncRecord.getDataCount() == 0 && syncRecord.getFileCount() == 0) {
                    Logger.e(TAG, "no sync data");
                    emitter.onSuccess(lastDate);
                    return;
                }
                Date now = new Date();
                if (syncRecord.getOffice() != null) {
                    for (Office office : syncRecord.getOffice()) {
                        if (office.getSyncable() && office.getUpdated_at().compareTo(lastDate) > 0 && office.getUpdated_at().compareTo(now) < 0) {
                            lastDate = office.getUpdated_at();
                        }
                    }

                    if (mDb.recordDao().saveOffice(syncRecord.getOffice()).contains(-1L)) {
                        result = false;
                    }
                }
                if (syncRecord.getAbsent() != null) {
                    for (Absent absent : syncRecord.getAbsent()) {
                        if (absent.getSyncable() && absent.getUpdated_at().compareTo(lastDate) > 0 && absent.getUpdated_at().compareTo(now) < 0) {
                            lastDate = absent.getUpdated_at();
                        }
                    }
                    if (mDb.recordDao().saveAbsent(syncRecord.getAbsent()).contains(-1L)) {
                        result = false;
                    }
                }
                if (syncRecord.getContacter() != null) {
                    for (Contacter contacter : syncRecord.getContacter()) {
                        if (contacter.getSyncable() && contacter.getUpdated_at().compareTo(lastDate) > 0 && contacter.getUpdated_at().compareTo(now) < 0) {
                            lastDate = contacter.getUpdated_at();
                        }
                    }

                    if (mDb.recordDao().saveContacter(syncRecord.getContacter()).contains(-1L)) {
                        result = false;
                    }
                }
                if (syncRecord.getContract() != null) {
                    for (Contract contract : syncRecord.getContract())
                    {
                        if (contract.getSyncable() && contract.getUpdated_at().compareTo(lastDate) > 0 && contract.getUpdated_at().compareTo(now) < 0) {
                            lastDate = contract.getUpdated_at();
                        }
                    }
                    if (mDb.recordDao().saveContract(syncRecord.getContract()).contains(-1L)) {
                        result = false;
                    }
                }
                if (syncRecord.getParticipant() != null) {
                    for (Participant participant : syncRecord.getParticipant()) {
                        if (participant.getSyncable() && participant.getUpdated_at().compareTo(lastDate) > 0 && participant.getUpdated_at().compareTo(now) < 0) {
                            lastDate = participant.getUpdated_at();
                        }
                    }
                    if (mDb.recordDao().saveParticipant(syncRecord.getParticipant()).contains(-1L)) {
                        result = false;
                    }
                }
                if (syncRecord.getProject() != null) {
                    for (Project project : syncRecord.getProject()) {
                        if (project.getSyncable() && project.getUpdated_at().compareTo(lastDate) > 0 && project.getUpdated_at().compareTo(now) < 0) {
                            lastDate = project.getUpdated_at();
                        }
                    }
                    if (mDb.recordDao().saveProject(syncRecord.getProject()).contains(-1L)) {
                        result = false;
                    }
                }
                if (syncRecord.getQuotation() != null) {
                    for (Quotation quotation : syncRecord.getQuotation())
                    {
                        if (quotation.getSyncable() && quotation.getUpdated_at().compareTo(lastDate) > 0 && quotation.getUpdated_at().compareTo(now) < 0)
                        {
                            lastDate = quotation.getUpdated_at();
                        }
                    }
                    if (mDb.recordDao().saveQuotation(syncRecord.getQuotation()).contains(-1L)) {
                        result = false;
                    }
                }
                if (syncRecord.getQuotationProduct() != null) {
                    for (QuotationProduct product : syncRecord.getQuotationProduct()) {
                        if (product.getSyncable() && product.getUpdated_at().compareTo(lastDate) > 0 && product.getUpdated_at().compareTo(now) < 0) {
                            lastDate = product.getUpdated_at();
                        }
                    }
                    if (mDb.recordDao().saveQuotationProduct(syncRecord.getQuotationProduct()).contains(-1L)) {
                        result = false;
                    }
                }
                if (syncRecord.getReimbursement() != null) {
                    for (Reimbursement reimbursement : syncRecord.getReimbursement()) {
                        if (reimbursement.getSyncable() && reimbursement.getUpdated_at()
                                .compareTo(lastDate) > 0 && reimbursement.getUpdated_at()
                                .compareTo(now) < 0) {
                            lastDate = reimbursement.getUpdated_at();
                        }
                    }
                    if (mDb.recordDao().saveReimbursement(syncRecord.getReimbursement()).contains(-1L)) {
                        result = false;
                    }
                }
                if (syncRecord.getReimbursementItem() != null) {
                    for (ReimbursementItem reimbursementItem : syncRecord.getReimbursementItem()) {
                        if (reimbursementItem.getSyncable() && reimbursementItem.getUpdated_at()
                                .compareTo(lastDate) > 0 && reimbursementItem.getUpdated_at()
                                .compareTo(now) < 0) {
                            lastDate = reimbursementItem.getUpdated_at();
                        }
                    }
                    if (mDb.recordDao().saveReimbursementItem(syncRecord.getReimbursementItem()).contains(-1L)) {
                        result = false;
                    }
                }
                if (syncRecord.getSalesOpportunity() != null) {
                    for (SalesOpportunity opportunity : syncRecord.getSalesOpportunity()) {
                        if (opportunity.getSyncable() && opportunity.getUpdated_at().compareTo(lastDate) > 0 && opportunity.getUpdated_at().compareTo(now) < 0)
                        {
                            lastDate = opportunity.getUpdated_at();
                        }
                    }
                    if (mDb.recordDao().saveSalesOpportunity(syncRecord.getSalesOpportunity()).contains(-1L)) {
                        result = false;
                    }
                }
                if (syncRecord.getSalesOpportunitySub() != null) {
                    for (SalesOpportunitySub opportunity : syncRecord.getSalesOpportunitySub()) {
                        if (opportunity.getSyncable() && opportunity.getUpdated_at().compareTo(lastDate) > 0 && opportunity.getUpdated_at().compareTo(now) < 0)
                        {
                            lastDate = opportunity.getUpdated_at();
                        }
                    }
                    if (mDb.recordDao().saveSalesOpportunitySub(syncRecord.getSalesOpportunitySub()).contains(-1L))
                    {
                        result = false;
                    }
                }
                if (syncRecord.getSample() != null) {
                    for (Sample sample : syncRecord.getSample()) {
                        if (sample.getSyncable() && sample.getUpdated_at().compareTo(lastDate) > 0 && sample.getUpdated_at().compareTo(now) < 0) {
                            lastDate = sample.getUpdated_at();
                        }
                    }
                    if (mDb.recordDao().saveSample(syncRecord.getSample()).contains(-1L)) {
                        result = false;
                    }
                }
                if (syncRecord.getSampleProduct() != null) {
                    for (SampleProduct product : syncRecord.getSampleProduct()) {
                        if (product.getSyncable() && product.getUpdated_at().compareTo(lastDate) > 0 && product.getUpdated_at().compareTo(now) < 0)
                        {
                            lastDate = product.getUpdated_at();
                        }
                    }
                    if (mDb.recordDao().saveSampleProduct(syncRecord.getSampleProduct()).contains(-1L)) {
                        result = false;
                    }
                }
                if (syncRecord.getTask() != null) {
                    for (Task task : syncRecord.getTask()) {
                        if (task.getSyncable() && task.getUpdated_at().compareTo(lastDate) > 0 && task.getUpdated_at().compareTo(now) < 0) {
                            lastDate = task.getUpdated_at();
                        }
                    }
                    if (mDb.recordDao().saveTask(syncRecord.getTask()).contains(-1L)) {
                        result = false;
                    }
                }
                if (syncRecord.getTaskConversation() != null) {
                    for (TaskConversation conversation : syncRecord.getTaskConversation()) {
                        if (conversation.getSyncable() && conversation.getUpdated_at().compareTo(lastDate) > 0 && conversation.getUpdated_at().compareTo(now) < 0)
                        {
                            lastDate = conversation.getUpdated_at();
                        }
                    }
                    if (mDb.recordDao().saveTaskConversation(syncRecord.getTaskConversation()).contains(-1L))
                    {
                        result = false;
                    }
                }
                if (syncRecord.getTaskMessage() != null) {
                    for (TaskMessage message : syncRecord.getTaskMessage()) {
                        if (message.getSyncable() && message.getUpdated_at().compareTo(lastDate) > 0 && message.getUpdated_at().compareTo(now) < 0)
                        {
                            lastDate = message.getUpdated_at();
                        }
                    }
                    if (mDb.recordDao().saveTaskMessage(syncRecord.getTaskMessage()).contains(-1L)) {
                        result = false;
                    }
                }
                if (syncRecord.getTrip() != null) {
                    for (Trip trip : syncRecord.getTrip()) {
                        if (trip.getSyncable() && trip.getUpdated_at().compareTo(lastDate) > 0 && trip.getUpdated_at().compareTo(now) < 0) {
                            lastDate = trip.getUpdated_at();
                        }
                    }
                    if (mDb.recordDao().saveTrip(syncRecord.getTrip()).contains(-1L)) {
                        result = false;
                    }
                }
                if (syncRecord.getTripStatusLog() != null) {
                    for (TripStatusLog log : syncRecord.getTripStatusLog()) {
                        if (log.getSyncable() && log.getUpdated_at().compareTo(lastDate) > 0 && log.getUpdated_at().compareTo(now) < 0) {
                            lastDate = log.getUpdated_at();
                        }
                    }
                    if (mDb.recordDao().saveTripStatusLog(syncRecord.getTripStatusLog()).contains(-1L)) {
                        result = false;
                    }
                }
                if (syncRecord.getVisit() != null) {
                    for (Visit visit : syncRecord.getVisit())
                    {
                        if (visit.getSyncable() && visit.getUpdated_at().compareTo(lastDate) > 0 && visit.getUpdated_at().compareTo(now) < 0) {
                            lastDate = visit.getUpdated_at();
                        }
                    }
                    if (mDb.recordDao().saveVisit(syncRecord.getVisit()).contains(-1L))
                    {
                        result = false;
                    }
                }
//                if (syncRecord.getUserQuickMenu() != null) {
//                    for (UserQuickMenu menu : new ArrayList<>(syncRecord.getUserQuickMenu())) {
//                        if (menu.getSyncable() && menu.getUpdated_at().compareTo(lastDate) > 0 &&
//                                menu.getUpdated_at().compareTo(now) < 0) {
//                            lastDate = menu.getUpdated_at();
//                        }
//                    }
//                    if (mDb.recordDao().saveUserQuickMenu(syncRecord.getUserQuickMenu())
//                            .contains
//                                    (-1L)) {
//                        result = false;
//                    }
//                }
                if (syncRecord.getCustomer() != null) {
                    for (Customer customer : syncRecord.getCustomer()) {
                        if (customer.getSyncable() && customer.getUpdated_at().compareTo(lastDate) > 0 && customer.getUpdated_at().compareTo(now) < 0) {
                            lastDate = customer.getUpdated_at();
                        }
                    }
                    if (mDb.recordDao().saveCustomer(syncRecord.getCustomer()).contains(-1L)) {
                        result = false;
                    }
                }
                if (syncRecord.getContactPerson() != null) {
                    for (ContactPerson person : syncRecord.getContactPerson()) {
                        if (person.getSyncable() && person.getUpdated_at().compareTo(lastDate) > 0 && person.getUpdated_at().compareTo(now) < 0) {
                            lastDate = person.getUpdated_at();
                        }
                    }
                    if (mDb.recordDao().saveContactPerson(syncRecord.getContactPerson()).contains(-1L)) {
                        result = false;
                    }
                }
                if (syncRecord.getFile() != null) {
                    for (File file : syncRecord.getFile()) {
                        if (file.getSyncable() && file.getUpdated_at().compareTo(lastDate) > 0 && file.getUpdated_at().compareTo(now) < 0) {
                            lastDate = file.getUpdated_at();
                        }
                    }
                    if (mDb.recordDao().saveFile(syncRecord.getFile()).contains(-1L)) {
                        result = false;
                    }
                }
                if (syncRecord.getFilePivot() != null) {
                    for (FilePivot pivot : syncRecord.getFilePivot()) {
                        if (pivot.getSyncable() && pivot.getUpdated_at().compareTo(lastDate) > 0 && pivot.getUpdated_at().compareTo(now) < 0) {
                            lastDate = pivot.getUpdated_at();
                        }
                    }
                    if (mDb.recordDao().saveFilePivot(syncRecord.getFilePivot()).contains(-1L)) {
                        result = false;
                    }
                }
                if (syncRecord.getMonthReport() != null) {
                    for (MonthReport report : syncRecord.getMonthReport()) {
                        if (report.getSyncable() && report.getUpdated_at().compareTo(lastDate) > 0 && report.getUpdated_at().compareTo(now) < 0) {
                            lastDate = report.getUpdated_at();
                        }
                    }
                    if (mDb.recordDao().saveMonthReport(syncRecord.getMonthReport()).contains(-1L)) {
                        result = false;
                    }
                }
                if (syncRecord.getExpress() != null) {
                    for (Express express : syncRecord.getExpress()) {
                        if (express.getSyncable() && express.getUpdated_at().compareTo(lastDate) > 0 && express.getUpdated_at().compareTo(now) < 0) {
                            lastDate = express.getUpdated_at();
                        }
                    }
                    if (mDb.recordDao().saveExpress(syncRecord.getExpress()).contains(-1L)) {
                        result = false;
                    }
                }
                if (syncRecord.getNewProjectProduction() != null) {
                    for (NewProjectProduction production : syncRecord.getNewProjectProduction()) {
                        if (production.getSyncable() && production.getUpdated_at().compareTo(lastDate) > 0 && production.getUpdated_at().compareTo(now) < 0) {
                            lastDate = production.getUpdated_at();
                        }
                    }
                    if (mDb.recordDao().saveNewProjectProduction(syncRecord.getNewProjectProduction()).contains(-1L)) {
                        result = false;
                    }
                }
                if (syncRecord.getSpecialPrice() != null) {
                    for (SpecialPrice specialPrice : syncRecord.getSpecialPrice()) {
                        if (specialPrice.getSyncable() && specialPrice.getUpdated_at().compareTo(lastDate) > 0 && specialPrice.getUpdated_at().compareTo(now) < 0) {
                            lastDate = specialPrice.getUpdated_at();
                        }
                    }
                    if (mDb.recordDao().saveSpecialPrice(syncRecord.getSpecialPrice()).contains(-1L)) {
                        result = false;
                    }
                }
                if (syncRecord.getTravel() != null) {
                    for (Travel travel : syncRecord.getTravel()) {
                        if (travel.getSyncable() && travel.getUpdated_at().compareTo(lastDate) > 0 && travel.getUpdated_at().compareTo(now) < 0) {
                            lastDate = travel.getUpdated_at();
                        }
                    }
                    if (mDb.recordDao().saveTravel(syncRecord.getTravel()).contains(-1L)) {
                        result = false;
                    }
                }
                if (syncRecord.getNonStandardInquiry() != null) {
                    for (NonStandardInquiry inquiry : syncRecord.getNonStandardInquiry()) {
                        if (inquiry.getSyncable() && inquiry.getUpdated_at().compareTo(lastDate) > 0 && inquiry.getUpdated_at().compareTo(now) < 0) {
                            lastDate = inquiry.getUpdated_at();
                        }
                    }
                    if (mDb.recordDao().saveNonStandardInquiry(syncRecord.getNonStandardInquiry()).contains(-1L)) {
                        result = false;
                    }
                }
                if (syncRecord.getSpringRingInquiry() != null) {
                    for (SpringRingInquiry inquiry : syncRecord.getSpringRingInquiry()) {
                        if (inquiry.getSyncable() && inquiry.getUpdated_at().compareTo(lastDate) > 0 && inquiry.getUpdated_at().compareTo(now) < 0) {
                            lastDate = inquiry.getUpdated_at();
                        }
                    }
                    if (mDb.recordDao().saveSpringRingInquiry(syncRecord.getSpringRingInquiry()).contains(-1L)) {
                        result = false;
                    }
                }
                if (syncRecord.getSpecialShip() != null) {
                    for (SpecialShip specialShip : syncRecord.getSpecialShip()) {
                        if (specialShip.getSyncable() && specialShip.getUpdated_at().compareTo(lastDate) > 0 && specialShip.getUpdated_at().compareTo(now) < 0) {
                            lastDate = specialShip.getUpdated_at();
                        }
                    }
                    if (mDb.recordDao().saveSpecialShip(syncRecord.getSpecialShip()).contains(-1L)) {
                        result = false;
                    }
                }
                if (syncRecord.getRepayment() != null) {
                    for (Repayment repayment : syncRecord.getRepayment()) {
                        if (repayment.getSyncable() && repayment.getUpdated_at().compareTo(lastDate) > 0 && repayment.getUpdated_at().compareTo(now) < 0) {
                            lastDate = repayment.getUpdated_at();
                        }
                    }
                    if (mDb.recordDao().saveRepayment(syncRecord.getRepayment()).contains(-1L)) {
                        result = false;
                    }
                }
                if (syncRecord.getSpecialPriceProduct() != null) {
                    for (SpecialPriceProduct product : syncRecord.getSpecialPriceProduct()) {
                        if (product.getSyncable() && product.getUpdated_at().compareTo(lastDate) > 0 && product.getUpdated_at().compareTo(now) < 0) {
                            lastDate = product.getUpdated_at();
                        }
                    }
                    if (mDb.recordDao().saveSpecialPriceProduct(syncRecord.getSpecialPriceProduct()).contains(-1L)) {
                        result = false;
                    }
                }
                if (syncRecord.getNonStandardInquiryProduct() != null) {
                    for (NonStandardInquiryProduct product : syncRecord.getNonStandardInquiryProduct()) {
                        if (product.getSyncable() && product.getUpdated_at().compareTo(lastDate) > 0 && product.getUpdated_at().compareTo(now) < 0) {
                            lastDate = product.getUpdated_at();
                        }
                    }
                    if (mDb.recordDao().saveNonStandardInquiryProduct(syncRecord.getNonStandardInquiryProduct()).contains(-1L)) {
                        result = false;
                    }
                }
                if (syncRecord.getSpringRingInquiryProduct() != null) {
                    for (SpringRingInquiryProduct product : syncRecord.getSpringRingInquiryProduct()) {
                        if (product.getSyncable() && product.getUpdated_at().compareTo(lastDate) > 0 && product.getUpdated_at().compareTo(now) < 0) {
                            lastDate = product.getUpdated_at();
                        }
                    }
                    if (mDb.recordDao().saveSpringRingInquiryProduct(syncRecord.getSpringRingInquiryProduct()).contains(-1L)) {
                        result = false;
                    }
                }
                if (syncRecord.getReport() != null) {
                    for (Report report : syncRecord.getReport()) {
                        if (report.getSyncable() && report.getUpdated_at().compareTo(lastDate) > 0 && report.getUpdated_at().compareTo(now) < 0) {
                            lastDate = report.getUpdated_at();
                        }
                    }
                    if (mDb.recordDao().saveReport(syncRecord.getReport()).contains(-1L))
                    {
                        result = false;
                    }
                }
                if (result) {
                    emitter.onSuccess(lastDate);
                } else
                    emitter.onError(new Throwable("can't sync record."));
            }
        }).subscribeOn(SCHEDULER);
    }
}
