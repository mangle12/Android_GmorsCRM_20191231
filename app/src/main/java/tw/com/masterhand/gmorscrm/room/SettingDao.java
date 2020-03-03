package tw.com.masterhand.gmorscrm.room;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

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
import tw.com.masterhand.gmorscrm.room.setting.CurrencyRate;
import tw.com.masterhand.gmorscrm.room.setting.Department;
import tw.com.masterhand.gmorscrm.room.setting.DepartmentContractItem;
import tw.com.masterhand.gmorscrm.room.setting.DepartmentSalesOpportunity;
import tw.com.masterhand.gmorscrm.room.setting.DepartmentSalesOpportunitySub;
import tw.com.masterhand.gmorscrm.room.setting.User;
import tw.com.masterhand.gmorscrm.room.setting.UserLeave;

@Dao
public interface SettingDao {

    @Query("SELECT * from User where deleted_at IS NULL")
    public List<User> getUser();

    @Query("SELECT * from User where company_id = :companyId and deleted_at IS NULL")
    public List<User> getUserByCompany(String companyId);

    @Query("SELECT * from User where id = :userId LIMIT 1")
    public User getUserById(String userId);

    @Query("SELECT * from Admin where id = :id and status = 1 LIMIT 1")
    public Admin getAdminById(String id);

    @Query("SELECT * from Admin where company_id = :companyId and status = 1 and deleted_at IS NULL")
    public List<Admin> getAdminByCompany(String companyId);

    @Query("SELECT * from Company where deleted_at IS NULL")
    public List<Company> getCompany();

    @Query("SELECT * from Company where id = :id LIMIT 1")
    public Company getCompanyById(String id);

    @Query("SELECT * from Department where company_id = :companyId and deleted_at IS NULL")
    public List<Department> getDepartment(String companyId);

    @Query("SELECT * from Department where id = :id LIMIT 1")
    public Department getDepartmentById(String id);

    @Query("SELECT * from ConfigCustomerLevel where company_id = :companyId and deleted_at IS " +
            "NULL order by sort ASC")
    public List<ConfigCustomerLevel> getConfigCustomerLevel(String companyId);

    @Query("SELECT * from ConfigCustomerLevel where id = :id LIMIT 1")
    public ConfigCustomerLevel getConfigCustomerLevelById(String id);

    @Query("SELECT * from ConfigQuotationProductCategory where " +
            "deleted_at IS NULL order by sort ASC")
    public List<ConfigQuotationProductCategory> getConfigQuotationProductCategory();

    @Query("SELECT * from ConfigQuotationProductCategory where company_id = :companyId and " +
            "deleted_at IS NULL order by sort ASC")
    public List<ConfigQuotationProductCategory> getConfigQuotationProductCategoryByCompany(String companyId);

    @Query("SELECT * from ConfigQuotationProductCategory where id = :id " +
            "LIMIT 1")
    public ConfigQuotationProductCategory getConfigQuotationProductCategoryById(String id);

    @Query("SELECT * from ConfigQuotationProductReport where company_id = :companyId and " +
            "deleted_at IS NULL order by sort ASC")
    public List<ConfigQuotationProductReport> getConfigQuotationProductReport(String companyId);

    @Query("SELECT * from ConfigQuotationProductReport where id = :id " +
            "LIMIT 1")
    public ConfigQuotationProductReport getConfigQuotationProductReportById(String id);

    @Query("SELECT * from ConfigCustomerSapType where company_id = :companyId and deleted_at IS " +
            "NULL order by sort ASC")
    public List<ConfigCustomerSapType> getConfigCustomerSapType(String companyId);

    @Query("SELECT * from ConfigCustomerSapType where id = :id LIMIT 1")
    public ConfigCustomerSapType getConfigCustomerSapTypeById(String id);

    @Query("SELECT * from ConfigIndustry where company_id = :companyId and deleted_at IS NULL " +
            "order by sort ASC")
    public List<ConfigIndustry> getConfigIndustry(String companyId);

    @Query("SELECT * from ConfigIndustry where id = :id LIMIT 1")
    public ConfigIndustry getConfigIndustryById(String id);

    @Query("SELECT * from ConfigCountry where deleted_at IS NULL " +
            "order by sort ASC")
    public List<ConfigCountry> getConfigCountry();

    @Query("SELECT * from ConfigCountry where id = :id LIMIT 1")
    public ConfigCountry getConfigCountryById(String id);

    @Query("SELECT * from ConfigCity where config_country_id = :countryId and deleted_at IS NULL " +
            "order by sort ASC")
    public List<ConfigCity> getConfigCityByCountry(String countryId);

    @Query("SELECT * from ConfigCity where id = :id LIMIT 1")
    public ConfigCity getConfigCityById(String id);

    @Query("SELECT * from DepartmentSalesOpportunity where parent_id = :parentId and " +
            "deleted_at IS NULL order by updated_at DESC LIMIT 1")
    public DepartmentSalesOpportunity getDepartmentSalesOpportunityByParent(String parentId);

    @Query("SELECT * from DepartmentSalesOpportunity where id = :id order by updated_at DESC " +
            "LIMIT 1")
    public DepartmentSalesOpportunity getDepartmentSalesOpportunityById(String id);

    @Query("SELECT * from DepartmentSalesOpportunitySub where department_sales_opportunity_id = " +
            ":saleId and " +
            "deleted_at IS NULL order by stage ASC")
    public List<DepartmentSalesOpportunitySub> getDepartmentSalesOpportunitySub(String saleId);

    @Query("SELECT * from ConfigSalesOpportunityType where company_id = :companyId and " +
            "deleted_at IS NULL order by sort ASC")
    public List<ConfigSalesOpportunityType> getConfigSalesOpportunityType(String companyId);

    @Query("SELECT * from ConfigSalesOpportunityType where id = :id LIMIT 1")
    public ConfigSalesOpportunityType getConfigSalesOpportunityTypeById(String id);

    @Query("SELECT * from ConfigSalesOpportunityLoseType where company_id = :companyId and " +
            "deleted_at IS NULL order by sort ASC")
    public List<ConfigSalesOpportunityLoseType> getConfigSalesOpportunityLoseType(String companyId);

    @Query("SELECT * from ConfigSalesOpportunityWinType where company_id = :companyId and " +
            "deleted_at IS NULL order by sort ASC")
    public List<ConfigSalesOpportunityWinType> getConfigSalesOpportunityWinType(String companyId);

    @Query("SELECT * from ConfigProjectSource where company_id = :companyId and " +
            "deleted_at IS NULL order by sort ASC")
    public List<ConfigProjectSource> getDepartmentProjectSource(String companyId);

    @Query("SELECT * from ConfigProjectSource where id = :id LIMIT 1")
    public ConfigProjectSource getDepartmentProjectSourceById(String id);

    @Query("SELECT * from ConfigCurrency where id = :id order by updated_at DESC LIMIT 1")
    public ConfigCurrency getConfigCurrencyById(String id);

    @Query("SELECT * from ConfigCurrency where company_id = :companyId and deleted_at IS NULL " +
            "order by sort ASC")
    public List<ConfigCurrency> getConfigCurrency(String companyId);

    @Query("SELECT * from CurrencyRate where id = :id order by updated_at DESC LIMIT 1")
    public CurrencyRate getCurrencyRateById(String id);

    @Query("SELECT * from CurrencyRate where config_currency_id = :configId and " +
            "deleted_at IS NULL order by updated_at DESC LIMIT 1")
    public CurrencyRate getCurrencyRateByConfig(String configId);

    @Query("SELECT * from ConfigReimbursementItem where company_id = :companyId and " +
            "deleted_at IS NULL order by sort ASC")
    public List<ConfigReimbursementItem> getConfigReimbursementItem(String companyId);

    @Query("SELECT * from ConfigReimbursementItem where id = :id LIMIT 1")
    public ConfigReimbursementItem getConfigReimbursementItemById(String id);

    @Query("SELECT * from DepartmentContractItem where department_id = :departmentId and " +
            "deleted_at IS NULL")
    public List<DepartmentContractItem> getDepartmentContractItemByDepartment(String departmentId);

    @Query("SELECT * from DepartmentContractItem where id = :id order by updated_at DESC LIMIT 1")
    public DepartmentContractItem getDepartmentContractItemById(String id);

    @Query("SELECT * from ConfigSampleAmountRange where company_id = :companyId and " +
            "deleted_at IS NULL order by sort ASC")
    public List<ConfigSampleAmountRange> getConfigSampleAmountRange(String companyId);

    @Query("SELECT * from ConfigSampleAmountRange where id = :id LIMIT 1")
    public ConfigSampleAmountRange getConfigSampleAmountRangeById(String id);

    @Query("SELECT * from ConfigSampleImportMethod where company_id = :companyId and " +
            "deleted_at IS NULL order by sort ASC")
    public List<ConfigSampleImportMethod> getConfigSampleImportMethod(String companyId);

    @Query("SELECT * from ConfigCustomerSapGroup where company_id = :companyId and " +
            "deleted_at IS NULL order by sort ASC")
    public List<ConfigCustomerSapGroup> getConfigCustomerSapGroup(String companyId);

    @Query("SELECT * from ConfigCustomerSapGroup where id = :id LIMIT 1")
    public ConfigCustomerSapGroup getConfigCustomerSapGroupById(String id);

    @Query("SELECT * from ConfigCustomerSource where company_id = :companyId and " +
            "deleted_at IS NULL order by sort ASC")
    public List<ConfigCustomerSource> getConfigCustomerSource(String companyId);

    @Query("SELECT * from ConfigCustomerSource where id = :id LIMIT 1")
    public ConfigCustomerSource getConfigCustomerSourceById(String id);

    @Query("SELECT * from ConfigSampleImportMethod where id = :id LIMIT 1")
    public ConfigSampleImportMethod getConfigSampleImportMethodById(String id);

    @Query("SELECT * from ConfigSamplePaymentMethod where company_id = :companyId and " +
            "deleted_at IS NULL order by sort ASC")
    public List<ConfigSamplePaymentMethod> getConfigSamplePaymentMethod(String companyId);

    @Query("SELECT * from ConfigSamplePaymentMethod where id = :id LIMIT 1")
    public ConfigSamplePaymentMethod getConfigSamplePaymentMethodById(String id);

    @Query("SELECT * from ConfigSampleProductType where company_id = :companyId and " +
            "deleted_at IS NULL order by sort ASC")
    public List<ConfigSampleProductType> getConfigSampleProductType(String companyId);

    @Query("SELECT * from ConfigSampleProductType where id = :id LIMIT 1")
    public ConfigSampleProductType getConfigSampleProductTypeById(String id);

    @Query("SELECT * from ConfigSampleReason where company_id = :companyId and " +
            "deleted_at IS NULL order by sort ASC")
    public List<ConfigSampleReason> getConfigSampleReason(String companyId);

    @Query("SELECT * from ConfigSampleReason where id = :id LIMIT 1")
    public ConfigSampleReason getConfigSampleReasonById(String id);

    @Query("SELECT * from ConfigSampleSource where company_id = :companyId and " +
            "deleted_at IS NULL order by sort ASC")
    public List<ConfigSampleSource> getConfigSampleSource(String companyId);

    @Query("SELECT * from ConfigSampleSource where id = :id LIMIT 1")
    public ConfigSampleSource getConfigSampleSourceById(String id);

    @Query("SELECT * from AuthorityCustomer")
    public List<AuthorityCustomer> getAuthorityCustomer();

    @Query("SELECT * from ConfigCompanyHiddenField where company_id = :companyId LIMIT 1")
    public ConfigCompanyHiddenField getConfigCompanyHiddenField(String companyId);

    @Query("SELECT * from ConfigCompanyHiddenField")
    public List<ConfigCompanyHiddenField> getConfigCompanyHiddenField();

    @Query("SELECT * from AuthorityProject")
    public List<AuthorityProject> getAuthorityProject();

    @Query("SELECT * from ConfigNewProjectProduction where company_id = :companyId and " +
            "deleted_at IS NULL order by sort ASC")
    public List<ConfigNewProjectProduction> getConfigNewProjectProduction(String companyId);

    @Query("SELECT * from ConfigNewProjectProduction where id = :id LIMIT 1")
    public ConfigNewProjectProduction getConfigNewProjectProductionById(String id);

    @Query("SELECT * from ConfigOffice where company_id = :companyId and " +
            "deleted_at IS NULL order by sort ASC")
    public List<ConfigOffice> getConfigOffice(String companyId);

    @Query("SELECT * from ConfigOffice where id = :id LIMIT 1")
    public ConfigOffice getConfigOfficeById(String id);

    @Query("SELECT * from ConfigExpressDestination where company_id = :companyId and " +
            "deleted_at IS NULL order by sort ASC")
    public List<ConfigExpressDestination> getConfigExpressDestination(String companyId);

    @Query("SELECT * from ConfigExpressDestination where id = :id LIMIT 1")
    public ConfigExpressDestination getConfigExpressDestinationById(String id);

    @Query("SELECT * from ConfigSealedState where company_id = :companyId and " +
            "deleted_at IS NULL order by sort ASC")
    public List<ConfigSealedState> getConfigSealedState(String companyId);

    @Query("SELECT * from ConfigSealedState where id = :id LIMIT 1")
    public ConfigSealedState getConfigSealedStateById(String id);

    @Query("SELECT * from ConfigTrend where company_id = :companyId and " +
            "deleted_at IS NULL order by sort ASC")
    public List<ConfigTrend> getConfigTrend(String companyId);

    @Query("SELECT * from ConfigTrend where id = :id LIMIT 1")
    public ConfigTrend getConfigTrendById(String id);

    @Query("SELECT * from ConfigSgbAdvantage where company_id = :companyId and " +
            "deleted_at IS NULL order by sort ASC")
    public List<ConfigSgbAdvantage> getConfigSgbAdvantage(String companyId);

    @Query("SELECT * from ConfigSgbAdvantage where id = :id LIMIT 1")
    public ConfigSgbAdvantage getConfigSgbAdvantageById(String id);

    @Query("SELECT * from ConfigSgbDisadvantage where company_id = :companyId and " +
            "deleted_at IS NULL order by sort ASC")
    public List<ConfigSgbDisadvantage> getConfigSgbDisadvantage(String companyId);

    @Query("SELECT * from ConfigSgbDisadvantage where id = :id LIMIT 1")
    public ConfigSgbDisadvantage getConfigSgbDisadvantageById(String id);

    @Query("SELECT * from ConfigCompetitiveStatus where company_id = :companyId and " +
            "deleted_at IS NULL order by sort ASC")
    public List<ConfigCompetitiveStatus> getConfigCompetitiveStatus(String companyId);

    @Query("SELECT * from ConfigCompetitiveStatus where id = :id LIMIT 1")
    public ConfigCompetitiveStatus getConfigCompetitiveStatusById(String id);

    @Query("SELECT * from ConfigInquiryProductType where company_id = :companyId and " +
            "deleted_at IS NULL order by sort ASC")
    public List<ConfigInquiryProductType> getConfigInquiryProductType(String companyId);

    @Query("SELECT * from ConfigInquiryProductType where id = :id LIMIT 1")
    public ConfigInquiryProductType getConfigInquiryProductTypeById(String id);

    @Query("SELECT * from ConfigYearPotential where company_id = :companyId and " +
            "deleted_at IS NULL order by sort ASC")
    public List<ConfigYearPotential> getConfigYearPotential(String companyId);

    @Query("SELECT * from ConfigYearPotential where id = :id LIMIT 1")
    public ConfigYearPotential getConfigYearPotentialById(String id);

    @Query("SELECT * from ConfigProductCategorySub where company_id = :companyId and " +
            "deleted_at IS NULL order by sort ASC")
    public List<ConfigProductCategorySub> getConfigProductCategorySub(String companyId);

    @Query("SELECT * from ConfigProductCategorySub where id = :id LIMIT 1")
    public ConfigProductCategorySub getConfigProductCategorySubById(String id);

    @Query("SELECT * from ConfigTax where company_id = :companyId and " +
            "deleted_at IS NULL order by sort ASC")
    public List<ConfigTax> getConfigTax(String companyId);

    @Query("SELECT * from ConfigTax where id = :id LIMIT 1")
    public ConfigTax getConfigTaxById(String id);

    /*-------------------------------儲存-----------------------------------*/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> saveCompany(List<Company> companies);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> saveConfigCustomerLevel(List<ConfigCustomerLevel> configs);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> saveConfigCustomerSapType(List<ConfigCustomerSapType> configs);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> saveConfigIndustry(List<ConfigIndustry> configs);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> saveConfigCity(List<ConfigCity> configs);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> saveConfigCountry(List<ConfigCountry> configs);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> saveConfigCurrency(List<ConfigCurrency> configs);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> saveConfigReimbursementItem(List<ConfigReimbursementItem> configs);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> saveConfigQuotationProductCategory(List<ConfigQuotationProductCategory>
                                                                 configs);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> saveConfigQuotationProductReport(List<ConfigQuotationProductReport> configs);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> saveConfigSampleAmountRange(List<ConfigSampleAmountRange>
                                                          configs);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> saveConfigSampleImportMethod(List<ConfigSampleImportMethod> configs);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> saveConfigSamplePaymentMethod(List<ConfigSamplePaymentMethod>
                                                            configs);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> saveConfigSampleProductType(List<ConfigSampleProductType> configs);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> saveConfigSampleReason(List<ConfigSampleReason>
                                                     configs);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> saveConfigSampleSource(List<ConfigSampleSource> configs);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> saveCurrencyRate(List<CurrencyRate> configs);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> saveConfigSalesOpportunityType(List<ConfigSalesOpportunityType> configs);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> saveConfigSalesOpportunityLoseType(List<ConfigSalesOpportunityLoseType>
                                                                 configs);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> saveConfigSalesOpportunityWinType(List<ConfigSalesOpportunityWinType>
                                                                configs);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> saveDepartmentProjectSource(List<ConfigProjectSource> configs);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> saveConfigCompanyHiddenField(List<ConfigCompanyHiddenField> configs);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> saveDepartment(List<Department> configs);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> saveDepartmentContractItem(List<DepartmentContractItem> configs);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> saveDepartmentSalesOpportunity(List<DepartmentSalesOpportunity> configs);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> saveDepartmentSalesOpportunitySub(List<DepartmentSalesOpportunitySub>
                                                                configs);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> saveUser(List<User> users);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public Long saveUser(User user);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> saveUserLeave(List<UserLeave> configs);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> saveAuthorityCustomer(List<AuthorityCustomer> configs);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public Long saveAuthorityCustomer(AuthorityCustomer config);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> saveAuthorityProject(List<AuthorityProject> configs);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public Long saveAuthorityProject(AuthorityProject config);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> saveAdmin(List<Admin> configs);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> saveConfigCustomerSapGroup(List<ConfigCustomerSapGroup> configs);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> saveConfigCustomerSource(List<ConfigCustomerSource> configs);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> saveConfigNewProjectProduction(List<ConfigNewProjectProduction> configs);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> saveConfigOffice(List<ConfigOffice> configs);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> saveConfigExpressDestination(List<ConfigExpressDestination> configs);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> saveConfigYearPotential(List<ConfigYearPotential> configs);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> saveConfigInquiryProductType(List<ConfigInquiryProductType> configs);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> saveConfigCompetitiveStatus(List<ConfigCompetitiveStatus> configs);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> saveConfigSgbAdvantage(List<ConfigSgbAdvantage> configs);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> saveConfigSgbDisadvantage(List<ConfigSgbDisadvantage> configs);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> saveConfigTrend(List<ConfigTrend> configs);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> saveConfigSealedState(List<ConfigSealedState> configs);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> saveConfigProductCategorySub(List<ConfigProductCategorySub> configs);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> saveConfigTax(List<ConfigTax> configs);


    /*-------------------------------刪除-----------------------------------*/
    @Delete
    public void deleteAuthorityCustomer(List<AuthorityCustomer> configs);

    @Delete
    public void deleteConfigCompanyHiddenField(List<ConfigCompanyHiddenField> configs);

    @Delete
    public void deleteAuthorityProject(List<AuthorityProject> configs);
}
