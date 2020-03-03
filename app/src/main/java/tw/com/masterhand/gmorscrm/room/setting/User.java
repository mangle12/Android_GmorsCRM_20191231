package tw.com.masterhand.gmorscrm.room.setting;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Date;

import tw.com.masterhand.gmorscrm.model.Address;
import tw.com.masterhand.gmorscrm.model.Phone;

@Entity(tableName = "User")
public class User extends BaseSetting {
    @NonNull
    @PrimaryKey
    public String id;

    public String account;// 帳號
    public String company_id;// 公司ID
    public String department_id;// 部門ID
    public String supervisor_id;// 直屬主管ID
    public String first_name;// 名字
    public String last_name;// 姓氏getShowName
    public String title;// 職稱
    public String address;// 地址
    public String birthday;// 生日
    public String tel;// 電話
    public String email;// Email
    public String photo;// 大頭照
    public String persional_history;// 個人經歷
    public String misc;// 其他
    public String lock_code;// 解鎖碼
    public int time_difference;// 時差
    public float expense_quota;// 差旅費額度
    public float expense_left;// 差旅費剩餘
    /*2018/2/5新增欄位*/
    public String assistant_id;// 銷售助理ID
    /*2018/5/3新增欄位*/
    public Date start_working_date;// 到職日
    /*2018/8/22新增欄位*/
    public int is_translate;// 可否使用翻譯功能

    public User() {
        super();
        id = "";
        department_id = "";
        company_id = "";
        is_translate = 0;
    }

    public String getShowName() {
        String showName = "";
        if (!TextUtils.isEmpty(last_name))
            showName += last_name;
        if (!TextUtils.isEmpty(first_name))
            showName += first_name;
        return showName;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getCompany_id() {
        return company_id;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }

    public String getDepartment_id() {
        return department_id;
    }

    public void setDepartment_id(String department_id) {
        this.department_id = department_id;
    }

    public String getSupervisor_id() {
        return supervisor_id;
    }

    public void setSupervisor_id(String supervisor_id) {
        this.supervisor_id = supervisor_id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Address getAddress() {
        if (TextUtils.isEmpty(address))
            return new Address();
        return new Gson().fromJson(address, Address.class);
    }

    public void setAddress(Address address) {
        this.address = new Gson().toJson(address);
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public ArrayList<Phone> getTel() {
        return new Gson().fromJson(tel, new TypeToken<ArrayList<Phone>>() {
        }.getType());
    }

    public void setTel(ArrayList<Phone> tel) {
        this.tel = new Gson().toJson(tel);
    }

    public ArrayList<String> getEmail() {
        return new Gson().fromJson(email, new TypeToken<ArrayList<String>>() {
        }.getType());
    }

    public void setEmail(ArrayList<String> email) {
        this.email = new Gson().toJson(email);
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getPersional_history() {
        return persional_history;
    }

    public void setPersional_history(String persional_history) {
        this.persional_history = persional_history;
    }

    public String getMisc() {
        return misc;
    }

    public void setMisc(String misc) {
        this.misc = misc;
    }

    public String getLock_code() {
        return lock_code;
    }

    public void setLock_code(String lock_code) {
        this.lock_code = lock_code;
    }

    public int getTime_difference() {
        return time_difference;
    }

    public void setTime_difference(int time_difference) {
        this.time_difference = time_difference;
    }

    public float getExpense_quota() {
        return expense_quota;
    }

    public void setExpense_quota(float expense_quota) {
        this.expense_quota = expense_quota;
    }

    public float getExpense_left() {
        return expense_left;
    }

    public void setExpense_left(float expense_left) {
        this.expense_left = expense_left;
    }

    public String getAssistant_id() {
        return assistant_id;
    }

    public void setAssistant_id(String assistant_id) {
        this.assistant_id = assistant_id;
    }

    public Date getStart_working_date() {
        return start_working_date;
    }

    public boolean getIs_translate() {
        return is_translate == 1;
    }
}
