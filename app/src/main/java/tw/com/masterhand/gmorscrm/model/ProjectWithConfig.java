package tw.com.masterhand.gmorscrm.model;

import java.util.List;

import tw.com.masterhand.gmorscrm.enums.ProjectStatus;
import tw.com.masterhand.gmorscrm.room.record.Contacter;
import tw.com.masterhand.gmorscrm.room.record.Customer;
import tw.com.masterhand.gmorscrm.room.record.File;
import tw.com.masterhand.gmorscrm.room.record.Participant;
import tw.com.masterhand.gmorscrm.room.record.Project;
import tw.com.masterhand.gmorscrm.room.record.SalesOpportunity;
import tw.com.masterhand.gmorscrm.room.setting.ConfigCurrency;
import tw.com.masterhand.gmorscrm.room.setting.CurrencyRate;
import tw.com.masterhand.gmorscrm.room.setting.Department;
import tw.com.masterhand.gmorscrm.room.setting.DepartmentSalesOpportunity;

public class ProjectWithConfig {
    public Project project;
    public Department department;
    public DepartmentSalesOpportunity departmentSalesOpportunity;
    public SalesOpportunity salesOpportunity;
    public ConfigCurrency configCurrency;
    public CurrencyRate currencyRate;
    public Customer customer;
    List<Participant> participants;
    List<Contacter> contacters;
    List<File> files;

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public SalesOpportunity getSalesOpportunity() {
        if (salesOpportunity == null) {
            salesOpportunity = new SalesOpportunity();
            salesOpportunity.setStage(ProjectStatus.START);
            salesOpportunity.setPercentage(0);
            salesOpportunity.setProject_id(project.getId());
            salesOpportunity.setDepartment_sales_opportunity(null);
        }
        return salesOpportunity;
    }

    public void setSalesOpportunity(SalesOpportunity salesOpportunity) {
        this.salesOpportunity = salesOpportunity;
    }

    public DepartmentSalesOpportunity getDepartmentSalesOpportunity() {
        return departmentSalesOpportunity;
    }

    public void setDepartmentSalesOpportunity(DepartmentSalesOpportunity
                                                      departmentSalesOpportunity) {
        this.departmentSalesOpportunity = departmentSalesOpportunity;
    }

    public ConfigCurrency getConfigCurrency() {
        return configCurrency;
    }

    public void setConfigCurrency(ConfigCurrency configCurrency) {
        this.configCurrency = configCurrency;
    }

    public CurrencyRate getCurrencyRate() {
        return currencyRate;
    }

    public void setCurrencyRate(CurrencyRate currencyRate) {
        this.currencyRate = currencyRate;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
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

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }
}
