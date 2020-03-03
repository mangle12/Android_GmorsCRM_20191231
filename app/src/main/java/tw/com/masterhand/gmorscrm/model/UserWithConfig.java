package tw.com.masterhand.gmorscrm.model;

import tw.com.masterhand.gmorscrm.room.setting.Company;
import tw.com.masterhand.gmorscrm.room.setting.Department;
import tw.com.masterhand.gmorscrm.room.setting.User;

public class UserWithConfig {
    User user;
    Company company;
    Department department;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

}
