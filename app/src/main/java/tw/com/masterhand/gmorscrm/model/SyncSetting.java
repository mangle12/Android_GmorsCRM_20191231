package tw.com.masterhand.gmorscrm.model;

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
import tw.com.masterhand.gmorscrm.room.setting.ConfigCustomerSapType;
import tw.com.masterhand.gmorscrm.room.setting.ConfigCustomerSapGroup;
import tw.com.masterhand.gmorscrm.room.setting.ConfigCustomerSource;
import tw.com.masterhand.gmorscrm.room.setting.ConfigIndustry;
import tw.com.masterhand.gmorscrm.room.setting.ConfigInquiryProductType;
import tw.com.masterhand.gmorscrm.room.setting.ConfigNewProjectProduction;
import tw.com.masterhand.gmorscrm.room.setting.ConfigOffice;
import tw.com.masterhand.gmorscrm.room.setting.ConfigExpressDestination;
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
import tw.com.masterhand.gmorscrm.room.setting.ConfigTrend;
import tw.com.masterhand.gmorscrm.room.setting.ConfigYearPotential;
import tw.com.masterhand.gmorscrm.room.setting.ConfigTax;
import tw.com.masterhand.gmorscrm.room.setting.CurrencyRate;
import tw.com.masterhand.gmorscrm.room.setting.Department;
import tw.com.masterhand.gmorscrm.room.setting.DepartmentContractItem;
import tw.com.masterhand.gmorscrm.room.setting.DepartmentSalesOpportunity;
import tw.com.masterhand.gmorscrm.room.setting.DepartmentSalesOpportunitySub;
import tw.com.masterhand.gmorscrm.room.setting.User;
import tw.com.masterhand.gmorscrm.room.setting.UserLeave;

public class SyncSetting {
    List<Company> Company;
    List<ConfigCustomerLevel> ConfigCustomerLevel;
    List<ConfigCustomerSapType> ConfigCustomerSapType;
    List<ConfigIndustry> ConfigIndustry;
    List<ConfigCity> ConfigCity;
    List<ConfigCountry> ConfigCountry;
    List<ConfigCurrency> ConfigCurrency;
    List<CurrencyRate> CurrencyRate;
    List<ConfigReimbursementItem> ConfigReimbursementItem;
    List<Department> Department;
    List<DepartmentContractItem> DepartmentContractItem;
    List<DepartmentSalesOpportunity>
            DepartmentSalesOpportunity;
    List<DepartmentSalesOpportunitySub>
            DepartmentSalesOpportunitySub;
    List<User> User;
    List<UserLeave> UserLeave;
    List<ConfigQuotationProductCategory> ConfigQuotationProductCategory;
    List<ConfigQuotationProductReport> ConfigQuotationProductReport;
    List<ConfigSampleAmountRange> ConfigSampleAmountRange;
    List<ConfigSampleImportMethod> ConfigSampleImportMethod;
    List<ConfigSamplePaymentMethod> ConfigSamplePaymentMethod;
    List<ConfigSampleProductType> ConfigSampleProductType;
    List<ConfigSampleReason> ConfigSampleReason;
    List<ConfigSampleSource> ConfigSampleSource;
    List<ConfigSalesOpportunityType> ConfigSalesOpportunityType;
    List<ConfigSalesOpportunityLoseType> ConfigSalesOpportunityLoseType;
    List<ConfigSalesOpportunityWinType> ConfigSalesOpportunityWinType;
    List<ConfigProjectSource> ConfigProjectSource;
    List<ConfigCompanyHiddenField> ConfigCompanyHiddenField;
    List<ConfigCustomerSapGroup> ConfigCustomerSapGroup;
    List<ConfigCustomerSource> ConfigCustomerSource;
    List<ConfigNewProjectProduction> ConfigNewProjectProduction;
    List<ConfigOffice> ConfigOffice;
    List<ConfigExpressDestination> ConfigExpressDestination;
    List<ConfigYearPotential> ConfigYearPotential;
    List<ConfigInquiryProductType> ConfigInquiryProductType;
    List<ConfigCompetitiveStatus> ConfigCompetitiveStatus;
    List<ConfigSgbAdvantage> ConfigSgbAdvantage;
    List<ConfigSgbDisadvantage> ConfigSgbDisadvantage;
    List<ConfigTrend> ConfigTrend;
    List<ConfigSealedState> ConfigSealedState;
    List<ConfigTax> ConfigTax;
    List<AuthorityCustomer> AuthorizeCustomer;
    List<AuthorityProject> AuthorizeProject;
    List<Admin> Admin;
    List<ConfigProductCategorySub> ConfigProductCategorySub;


    public List<Company> getCompany() {
        return Company;
    }

    public void setCompany(List<Company> company) {
        Company = company;
    }

    public List<tw.com.masterhand.gmorscrm.room.setting.ConfigCustomerLevel>
    getConfigCustomerLevel() {
        return ConfigCustomerLevel;
    }

    public void setConfigCustomerLevel(List<tw.com.masterhand.gmorscrm.room.setting
            .ConfigCustomerLevel> configCustomerLevel) {
        ConfigCustomerLevel = configCustomerLevel;
    }

    public List<tw.com.masterhand.gmorscrm.room.setting.ConfigCustomerSapType>
    getConfigCustomerSapType() {
        return ConfigCustomerSapType;
    }

    public void setConfigCustomerSapType(List<tw.com.masterhand.gmorscrm.room.setting
            .ConfigCustomerSapType> configCustomerSapType) {
        ConfigCustomerSapType = configCustomerSapType;
    }

    public List<tw.com.masterhand.gmorscrm.room.setting.ConfigIndustry> getConfigIndustry() {
        return ConfigIndustry;
    }

    public void setConfigIndustry(List<tw.com.masterhand.gmorscrm.room.setting.ConfigIndustry>
                                          configIndustry) {
        ConfigIndustry = configIndustry;
    }

    public List<ConfigCity> getConfigCity() {
        return ConfigCity;
    }

    public void setConfigCity(List<ConfigCity> configCity) {
        ConfigCity = configCity;
    }

    public List<ConfigCountry> getConfigCountry() {
        return ConfigCountry;
    }

    public void setConfigCountry(List<ConfigCountry>
                                         configCountry) {
        ConfigCountry = configCountry;
    }

    public List<ConfigCurrency> getConfigCurrency() {
        return ConfigCurrency;
    }

    public void setConfigCurrency(List<ConfigCurrency>
                                          configCurrency) {
        ConfigCurrency = configCurrency;
    }

    public List<CurrencyRate> getCurrencyRate() {
        return CurrencyRate;
    }

    public void setCurrencyRate(List<CurrencyRate>
                                        currencyRate) {
        CurrencyRate = currencyRate;
    }

    public List<ConfigReimbursementItem>
    getConfigReimbursementItem() {
        return ConfigReimbursementItem;
    }

    public void setConfigReimbursementItem(List<tw.com.masterhand.gmorscrm.room.setting
            .ConfigReimbursementItem> configReimbursementItem) {
        ConfigReimbursementItem = configReimbursementItem;
    }

    public List<Department> getDepartment() {
        return Department;
    }

    public void setDepartment(List<Department> department) {
        Department = department;
    }

    public List<DepartmentContractItem>
    getDepartmentContractItem() {
        return DepartmentContractItem;
    }

    public void setDepartmentContractItem(List<tw.com.masterhand.gmorscrm.room.setting
            .DepartmentContractItem> departmentContractItem) {
        DepartmentContractItem = departmentContractItem;
    }

    public List<DepartmentSalesOpportunity>
    getDepartmentSalesOpportunity() {
        return DepartmentSalesOpportunity;
    }

    public void setDepartmentSalesOpportunity(List<tw.com.masterhand.gmorscrm.room.setting
            .DepartmentSalesOpportunity> departmentSalesOpportunity) {
        DepartmentSalesOpportunity = departmentSalesOpportunity;
    }

    public List<DepartmentSalesOpportunitySub>
    getDepartmentSalesOpportunitySub() {
        return DepartmentSalesOpportunitySub;
    }

    public void setDepartmentSalesOpportunitySub(List<tw.com.masterhand.gmorscrm.room.setting
            .DepartmentSalesOpportunitySub> departmentSalesOpportunitySub) {
        DepartmentSalesOpportunitySub = departmentSalesOpportunitySub;
    }

    public List<User> getUser() {
        return User;
    }

    public void setUser(List<User> user) {
        User = user;
    }

    public List<UserLeave> getUserLeave() {
        return UserLeave;
    }

    public void setUserLeave(List<UserLeave> userLeave) {
        UserLeave = userLeave;
    }

    public List<tw.com.masterhand.gmorscrm.room.setting.ConfigQuotationProductCategory>
    getConfigQuotationProductCategory() {
        return ConfigQuotationProductCategory;
    }

    public void setConfigQuotationProductCategory(List<tw.com.masterhand.gmorscrm.room.setting
            .ConfigQuotationProductCategory> configQuotationProductCategory) {
        ConfigQuotationProductCategory = configQuotationProductCategory;
    }

    public List<tw.com.masterhand.gmorscrm.room.setting.ConfigQuotationProductReport>
    getConfigQuotationProductReport() {
        return ConfigQuotationProductReport;
    }

    public void setConfigQuotationProductReport(List<tw.com.masterhand.gmorscrm.room.setting
            .ConfigQuotationProductReport> configQuotationProductReport) {
        ConfigQuotationProductReport = configQuotationProductReport;
    }

    public List<tw.com.masterhand.gmorscrm.room.setting.ConfigSampleAmountRange>
    getConfigSampleAmountRange() {
        return ConfigSampleAmountRange;
    }

    public void setConfigSampleAmountRange(List<tw.com.masterhand.gmorscrm.room.setting
            .ConfigSampleAmountRange> configSampleAmountRange) {
        ConfigSampleAmountRange = configSampleAmountRange;
    }

    public List<tw.com.masterhand.gmorscrm.room.setting.ConfigSampleImportMethod>
    getConfigSampleImportMethod() {
        return ConfigSampleImportMethod;
    }

    public void setConfigSampleImportMethod(List<tw.com.masterhand.gmorscrm.room.setting
            .ConfigSampleImportMethod> configSampleImportMethod) {
        ConfigSampleImportMethod = configSampleImportMethod;
    }

    public List<tw.com.masterhand.gmorscrm.room.setting.ConfigSamplePaymentMethod>
    getConfigSamplePaymentMethod() {
        return ConfigSamplePaymentMethod;
    }

    public void setConfigSamplePaymentMethod(List<tw.com.masterhand.gmorscrm.room.setting
            .ConfigSamplePaymentMethod> configSamplePaymentMethod) {
        ConfigSamplePaymentMethod = configSamplePaymentMethod;
    }

    public List<tw.com.masterhand.gmorscrm.room.setting.ConfigSampleProductType>
    getConfigSampleProductType() {
        return ConfigSampleProductType;
    }

    public void setConfigSampleProductType(List<tw.com.masterhand.gmorscrm.room.setting
            .ConfigSampleProductType> configSampleProductType) {
        ConfigSampleProductType = configSampleProductType;
    }

    public List<tw.com.masterhand.gmorscrm.room.setting.ConfigSampleReason> getConfigSampleReason
            () {
        return ConfigSampleReason;
    }

    public void setConfigSampleReason(List<tw.com.masterhand.gmorscrm.room.setting
            .ConfigSampleReason> configSampleReason) {
        ConfigSampleReason = configSampleReason;
    }

    public List<tw.com.masterhand.gmorscrm.room.setting.ConfigSampleSource> getConfigSampleSource
            () {
        return ConfigSampleSource;
    }

    public void setConfigSampleSource(List<tw.com.masterhand.gmorscrm.room.setting
            .ConfigSampleSource> configSampleSource) {
        ConfigSampleSource = configSampleSource;
    }

    public List<tw.com.masterhand.gmorscrm.room.setting.ConfigSalesOpportunityType>
    getConfigSalesOpportunityType() {
        return ConfigSalesOpportunityType;
    }

    public List<tw.com.masterhand.gmorscrm.room.setting.ConfigSalesOpportunityLoseType>
    getConfigSalesOpportunityLoseType() {
        return ConfigSalesOpportunityLoseType;
    }

    public List<tw.com.masterhand.gmorscrm.room.setting.ConfigSalesOpportunityWinType>
    getConfigSalesOpportunityWinType() {
        return ConfigSalesOpportunityWinType;
    }

    public List<ConfigProjectSource>
    getConfigProjectSource() {
        return ConfigProjectSource;
    }

    public List<tw.com.masterhand.gmorscrm.room.setting.ConfigCompanyHiddenField>
    getConfigCompanyHiddenField() {
        return ConfigCompanyHiddenField;
    }

    public List<tw.com.masterhand.gmorscrm.room.setting.ConfigCustomerSapGroup>
    getConfigCustomerSapGroup() {
        return ConfigCustomerSapGroup;
    }

    public List<tw.com.masterhand.gmorscrm.room.setting.ConfigCustomerSource>
    getConfigCustomerSource() {
        return ConfigCustomerSource;
    }

    public List<AuthorityCustomer> getAuthorizeCustomer() {
        return AuthorizeCustomer;
    }

    public void setAuthorizeCustomer(List<AuthorityCustomer> authorizeCustomer) {
        AuthorizeCustomer = authorizeCustomer;
    }

    public List<AuthorityProject> getAuthorizeProject() {
        return AuthorizeProject;
    }

    public void setAuthorizeProject(List<AuthorityProject> authorizeProject) {
        AuthorizeProject = authorizeProject;
    }

    public List<tw.com.masterhand.gmorscrm.room.setting.Admin> getAdmin() {
        return Admin;
    }

    public void setAdmin(List<tw.com.masterhand.gmorscrm.room.setting.Admin> admin) {
        Admin = admin;
    }

    public List<tw.com.masterhand.gmorscrm.room.setting.ConfigNewProjectProduction>
    getConfigNewProjectProduction() {
        return ConfigNewProjectProduction;
    }

    public List<tw.com.masterhand.gmorscrm.room.setting.ConfigOffice> getConfigOffice() {
        return ConfigOffice;
    }

    public List<tw.com.masterhand.gmorscrm.room.setting.ConfigExpressDestination>
    getConfigExpressDestination() {
        return ConfigExpressDestination;
    }

    public List<tw.com.masterhand.gmorscrm.room.setting.ConfigYearPotential>
    getConfigYearPotential() {
        return ConfigYearPotential;
    }

    public List<tw.com.masterhand.gmorscrm.room.setting.ConfigInquiryProductType>
    getConfigInquiryProductType() {
        return ConfigInquiryProductType;
    }

    public List<tw.com.masterhand.gmorscrm.room.setting.ConfigCompetitiveStatus>
    getConfigCompetitiveStatus() {
        return ConfigCompetitiveStatus;
    }

    public List<tw.com.masterhand.gmorscrm.room.setting.ConfigSgbAdvantage> getConfigSgbAdvantage
            () {
        return ConfigSgbAdvantage;
    }

    public List<tw.com.masterhand.gmorscrm.room.setting.ConfigSgbDisadvantage>
    getConfigSgbDisadvantage() {
        return ConfigSgbDisadvantage;
    }

    public List<tw.com.masterhand.gmorscrm.room.setting.ConfigTrend> getConfigTrend() {
        return ConfigTrend;
    }

    public List<tw.com.masterhand.gmorscrm.room.setting.ConfigSealedState> getConfigSealedState() {
        return ConfigSealedState;
    }

    public List<tw.com.masterhand.gmorscrm.room.setting.ConfigProductCategorySub>
    getConfigProductCategorySub() {
        return ConfigProductCategorySub;
    }

    public List<tw.com.masterhand.gmorscrm.room.setting.ConfigTax> getConfigTax() {
        return ConfigTax;
    }
}
