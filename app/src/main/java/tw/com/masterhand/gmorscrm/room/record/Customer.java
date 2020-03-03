package tw.com.masterhand.gmorscrm.room.record;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import tw.com.masterhand.gmorscrm.enums.ApprovalStatus;
import tw.com.masterhand.gmorscrm.model.Address;
import tw.com.masterhand.gmorscrm.model.Phone;

@Entity(tableName = "Customer")
public class Customer extends BaseRecord {
    @NonNull
    @PrimaryKey
    public String id;

    public String name;// 名稱
    public int type;// 重點客戶
    public String full_name;// 全名
    public String full_name_en;// 全名(英文)
    public String address;// 地址
    public String invoice_number;// 統編
    public String owner;// 負責人
    public String tel;// 電話
    public String website;// 網站
    public String logo;// logo
    public int status;// 開通 0:不 1:是
    public String description;// 備註
    public float expense_quota;
    public float expense_left;
    public int approval;// 審批狀態 0:未審批 2:已通過 3:未通過 4:審批中
    /* 2017/9/29新增 */
    public String sap_type_id;// 客戶分類
    public String level_id;// 客戶級別
    public String industry_id;// 行業別
    public String product;// 產品
    /* 2017/11/20新增 */
    public String enterprise;// 集團
    /* 2018/1/4新增 */
    public String user_id;// 建立人的ID
    public String department_id;// 建立人的部門ID
    /* 2018/2/7新增 */
    public String customer_sap_group_id;// 客戶組id
    public String customer_type;// 客戶類型
    public String customer_source_id;// 客戶來源
    /* 2018/6/11 */
    public int sample_count;// 樣品總數
    public int quotation_count;// 報價組數
    public int contract_count;// 合同總數
    public float invoice_amount;// 發票金額
    public float contract_amount;// 合約金額

    public Customer() {
        super();
        id = "";
        description = "";
        address = "";
        tel = "";
        website = "";
        owner = "";
        enterprise = "";
        type = 0;
        status = 0;
        expense_left = 0;
        expense_quota = 0;
        sample_count = 0;
        quotation_count = 0;
        contract_count = 0;
        invoice_amount = 0;
        contract_amount = 0;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getName() {
        if (TextUtils.isEmpty(name))
            if (TextUtils.isEmpty(full_name))
                if (TextUtils.isEmpty(full_name_en))
                    return "";
                else
                    return full_name_en;
            else
                return full_name;
        else
            return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isType() {
        return type == 1;
    }

    public void setType(boolean type) {
        this.type = type ? 1 : 0;
    }

    public String getFull_name() {
        if (TextUtils.isEmpty(full_name))
            return "";
        else
            return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getFull_name_en() {
        if (TextUtils.isEmpty(full_name_en))
            return "";
        else
            return full_name_en;
    }

    public void setFull_name_en(String full_name_en) {
        this.full_name_en = full_name_en;
    }

    public Address getAddress() {
        if (TextUtils.isEmpty(address))
            return new Address();
        return new Gson().fromJson(address, Address.class);
    }

    public void setAddress(Address address) {
        if (address != null)
            this.address = new Gson().toJson(address);
    }

    public String getInvoice_number() {
        return invoice_number;
    }

    public void setInvoice_number(String invoice_number) {
        this.invoice_number = invoice_number;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public ArrayList<Phone> getTel() {
        if (TextUtils.isEmpty(tel))
            return new ArrayList<>();
        return new Gson().fromJson(tel, new TypeToken<ArrayList<Phone>>() {
        }.getType());
    }

    public void setTel(ArrayList<Phone> tel) {
        this.tel = new Gson().toJson(tel);
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public boolean getStatus() {
        return status == 1;
    }

    public void setStatus(boolean status) {
        this.status = status ? 1 : 0;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public ApprovalStatus getApproval() {
        return ApprovalStatus.getStatusByCode(approval);
    }

    public void setApproval(ApprovalStatus approval) {
        this.approval = approval.getCode();
    }

    public String getSap_type_id() {
        return sap_type_id;
    }

    public void setSap_type_id(String sap_type_id) {
        this.sap_type_id = sap_type_id;
    }

    public String getLevel_id() {
        return level_id;
    }

    public void setLevel_id(String level_id) {
        this.level_id = level_id;
    }

    public String getIndustry_id() {
        return industry_id;
    }

    public void setIndustry_id(String industry_id) {
        this.industry_id = industry_id;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getEnterprise() {
        return enterprise;
    }

    public void setEnterprise(String customerId) {
        enterprise = customerId;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getDepartment_id() {
        return department_id;
    }

    public void setDepartment_id(String department_id) {
        this.department_id = department_id;
    }

    public String getCustomer_sap_group_id() {
        return customer_sap_group_id;
    }

    public void setCustomer_sap_group_id(String customer_sap_group_id) {
        this.customer_sap_group_id = customer_sap_group_id;
    }

    public String getCustomer_type() {
        return customer_type;
    }

    public void setCustomer_type(String customer_type) {
        this.customer_type = customer_type;
    }

    public String getCustomer_source() {
        return customer_source_id;
    }

    public void setCustomer_source(String customer_source) {
        this.customer_source_id = customer_source;
    }

    public int getSample_count() {
        return sample_count;
    }

    public void setSample_count(int sample_count) {
        this.sample_count = sample_count;
    }

    public int getQuotation_count() {
        return quotation_count;
    }

    public void setQuotation_count(int quotation_count) {
        this.quotation_count = quotation_count;
    }

    public int getContract_count() {
        return contract_count;
    }

    public void setContract_count(int contract_count) {
        this.contract_count = contract_count;
    }

    public float getInvoice_amount() {
        return invoice_amount;
    }

    public void setInvoice_amount(float invoice_amount) {
        this.invoice_amount = invoice_amount;
    }

    public float getContract_amount() {
        return contract_amount;
    }

    public void setContract_amount(float contract_amount) {
        this.contract_amount = contract_amount;
    }
}
