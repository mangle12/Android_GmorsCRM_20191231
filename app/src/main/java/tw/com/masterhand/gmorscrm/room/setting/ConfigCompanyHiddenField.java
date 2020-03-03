package tw.com.masterhand.gmorscrm.room.setting;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "ConfigCompanyHiddenField")
public class ConfigCompanyHiddenField {
    @NonNull
    @PrimaryKey
    public String company_id;
    // 合約
    public int contract_hidden_total;
    // 合約產品
    public int contractProduct_hidden_non_standard_number;
    public int contractProduct_hidden_product_report_id;
    public int contractProduct_hidden_amount;
    public int contractProduct_hidden_description;
    // 報價
    public int quotation_hidden_unit_type;
    // 報價產品
    public int quotationProduct_hidden_non_standard_number;
    public int quotationProduct_hidden_product_report_id;
    public int quotationProduct_hidden_amount;
    public int quotationProduct_hidden_description;
    // 送樣
    public int sample_hidden_import_method_id;
    public int sample_hidden_address;
    public int sample_hidden_payment_method_id;
    public int sample_hidden_tel;
    public int sample_hidden_receiver;
    public int sample_hidden_amount_range_id;
    public int sample_hidden_test_cycle;
    public int sample_hidden_test_evaluation_basis;
    public int sample_hidden_test_from_date;
    // 送樣產品
    public int sampleProduct_hidden_product_report_id;
    // 客戶
    public int customer_hidden_sap_type_id;
    public int customer_hidden_industry_id;
    public int customer_hidden_type;
    public int customer_hidden_enterprises;
    public int customer_hidden_product;
    // 銷售機會
    public int project_hidden_department_project_source;
    public int project_hidden_sales_opportunity_type;
    // 月報
    public int monthReport_hidden_current_month_customer;
    public int monthReport_hidden_current_month_project;
    public int monthReport_hidden_current_month_question;
    public int monthReport_hidden_next_month_visit_plan;
    public int monthReport_hidden_next_month_sales_goal;
    public int monthReport_hidden_next_month_guide;
    public int monthReport_hidden_sale_goal;
    public int monthReport_hidden_current_goal;
    public int monthReport_hidden_important_situation;
    public int monthReport_hidden_potential_market;
    public int monthReport_hidden_compete_strategy;
    public int monthReport_hidden_resource_investment;
    // 特價
    public int special_price_hidden_approval;
    public int special_price_hidden_year_discount;
    public int special_price_hidden_project_status;
    public int special_price_hidden_offer;
    public int special_price_hidden_amount;
    // 特價產品
    public int special_price_product_hidden_remark;
    public int special_price_product_hidden_product_category;
    // 快遞
    public int express_hidden_type;
    public int express_hidden_model;
    public int express_hidden_expense;
    public int express_hidden_destination;
    public int express_hidden_product_category_id;
    public int express_hidden_origin;
    public int express_hidden_arrival_payment;
    public int express_hidden_repayment_date;
    // 新增行事曆項目
    public int add_trip_hidden_visit;
    public int add_trip_hidden_office;
    public int add_trip_hidden_task;
    public int add_trip_hidden_absent;
    public int add_trip_hidden_reimbursement;
    public int add_trip_hidden_contract;
    public int add_trip_hidden_quotation;
    public int add_trip_hidden_sample;
    public int add_trip_hidden_special_price;
    public int add_trip_hidden_new_project_production;
    public int add_trip_hidden_nonstandard_inquiry;
    public int add_trip_hidden_spring_ring_inquiry;
    public int add_trip_hidden_express;
    public int add_trip_hidden_travel;


    public ConfigCompanyHiddenField() {
        contract_hidden_total = 0;
        contractProduct_hidden_non_standard_number = 0;
        contractProduct_hidden_product_report_id = 0;
        contractProduct_hidden_amount = 0;
        contractProduct_hidden_description = 0;
        quotation_hidden_unit_type = 0;
        quotationProduct_hidden_non_standard_number = 0;
        quotationProduct_hidden_product_report_id = 0;
        quotationProduct_hidden_amount = 0;
        quotationProduct_hidden_description = 0;
        sample_hidden_import_method_id = 0;
        sample_hidden_address = 0;
        sample_hidden_payment_method_id = 0;
        sample_hidden_tel = 0;
        sample_hidden_receiver = 0;
        sample_hidden_amount_range_id = 0;
        sample_hidden_test_cycle = 0;
        sample_hidden_test_evaluation_basis = 0;
        sample_hidden_test_from_date = 0;
        sampleProduct_hidden_product_report_id = 0;
        customer_hidden_sap_type_id = 0;
        customer_hidden_industry_id = 0;
        customer_hidden_type = 0;
        customer_hidden_enterprises = 0;
        customer_hidden_product = 0;
        project_hidden_department_project_source = 0;
        project_hidden_sales_opportunity_type = 0;
        monthReport_hidden_current_month_customer = 0;
        monthReport_hidden_current_month_project = 0;
        monthReport_hidden_current_month_question = 0;
        monthReport_hidden_next_month_visit_plan = 0;
        monthReport_hidden_next_month_sales_goal = 0;
        monthReport_hidden_next_month_guide = 0;
        monthReport_hidden_sale_goal = 0;
        monthReport_hidden_current_goal = 0;
        monthReport_hidden_important_situation = 0;
        monthReport_hidden_potential_market = 0;
        monthReport_hidden_compete_strategy = 0;
        monthReport_hidden_resource_investment = 0;
        add_trip_hidden_visit = 0;
        add_trip_hidden_office = 0;
        add_trip_hidden_task = 0;
        add_trip_hidden_absent = 0;
        add_trip_hidden_reimbursement = 0;
        add_trip_hidden_contract = 0;
        add_trip_hidden_quotation = 0;
        add_trip_hidden_sample = 0;
        add_trip_hidden_special_price = 0;
        add_trip_hidden_new_project_production = 0;
        add_trip_hidden_nonstandard_inquiry = 0;
        add_trip_hidden_spring_ring_inquiry = 0;
        add_trip_hidden_express = 0;
        add_trip_hidden_travel = 0;
        special_price_hidden_approval = 0;
        special_price_hidden_year_discount = 0;
        special_price_hidden_project_status = 0;
        special_price_hidden_offer = 0;
        special_price_product_hidden_remark = 0;
        special_price_product_hidden_product_category = 0;
        express_hidden_type = 0;
        express_hidden_model = 0;
        express_hidden_expense = 0;
        express_hidden_destination = 0;
        express_hidden_product_category_id = 0;
        express_hidden_origin = 0;
        express_hidden_arrival_payment = 0;
        express_hidden_repayment_date = 0;
    }

    @NonNull
    public String getCompany_id() {
        return company_id;
    }

    public boolean getContractProduct_hidden_non_standard_number() {
        return contractProduct_hidden_non_standard_number == 1;
    }

    public boolean getContractProduct_hidden_product_report_id() {
        return contractProduct_hidden_product_report_id == 1;
    }

    public boolean getContractProduct_hidden_amount() {
        return contractProduct_hidden_amount == 1;
    }

    public boolean getContractProduct_hidden_description() {
        return contractProduct_hidden_description == 1;
    }

    public boolean getQuotation_hidden_unit_type() {
        return quotation_hidden_unit_type == 1;
    }

    public boolean getQuotationProduct_hidden_non_standard_number() {
        return quotationProduct_hidden_non_standard_number == 1;
    }

    public boolean getQuotationProduct_hidden_product_report_id() {
        return quotationProduct_hidden_product_report_id == 1;
    }

    public boolean getQuotationProduct_hidden_amount() {
        return quotationProduct_hidden_amount == 1;
    }

    public boolean getQuotationProduct_hidden_description() {
        return quotationProduct_hidden_description == 1;
    }

    public boolean getSample_hidden_import_method_id() {
        return sample_hidden_import_method_id == 1;
    }

    public boolean getSample_hidden_address() {
        return sample_hidden_address == 1;
    }

    public boolean getSample_hidden_payment_method_id() {
        return sample_hidden_payment_method_id == 1;
    }

    public boolean getSample_hidden_tel() {
        return sample_hidden_tel == 1;
    }

    public boolean getSample_hidden_receiver() {
        return sample_hidden_receiver == 1;
    }

    public boolean getSample_hidden_amount_range_id() {
        return sample_hidden_amount_range_id == 1;
    }

    public boolean getSample_hidden_test_cycle() {
        return sample_hidden_test_cycle == 1;
    }

    public boolean getSample_hidden_test_evaluation_basis() {
        return sample_hidden_test_evaluation_basis == 1;
    }

    public boolean getSample_hidden_test_from_date() {
        return sample_hidden_test_from_date == 1;
    }

    public boolean getSampleProduct_hidden_product_report_id() {
        return sampleProduct_hidden_product_report_id == 1;
    }

    public boolean getCustomer_hidden_sap_type_id() {
        return customer_hidden_sap_type_id == 1;
    }

    public boolean getCustomer_hidden_industry_id() {
        return customer_hidden_industry_id == 1;
    }

    public boolean getCustomer_hidden_type() {
        return customer_hidden_type == 1;
    }

    public boolean getCustomer_hidden_enterprises() {
        return customer_hidden_enterprises == 1;
    }

    public boolean getCustomer_hidden_product() {
        return customer_hidden_product == 1;
    }

    public boolean getProject_hidden_department_project_source() {
        return project_hidden_department_project_source == 1;
    }

    public boolean getProject_hidden_sales_opportunity_type() {
        return project_hidden_sales_opportunity_type == 1;
    }

    public boolean getMonthReport_hidden_current_month_customer() {
        return monthReport_hidden_current_month_customer == 1;
    }

    public boolean getMonthReport_hidden_current_month_project() {
        return monthReport_hidden_current_month_project == 1;
    }

    public boolean getMonthReport_hidden_current_month_question() {
        return monthReport_hidden_current_month_question == 1;
    }

    public boolean getMonthReport_hidden_next_month_visit_plan() {
        return monthReport_hidden_next_month_visit_plan == 1;
    }

    public boolean getMonthReport_hidden_next_month_sales_goal() {
        return monthReport_hidden_next_month_sales_goal == 1;
    }

    public boolean getMonthReport_hidden_next_month_guide() {
        return monthReport_hidden_next_month_guide == 1;
    }

    public boolean getMonthReport_hidden_sale_goal() {
        return monthReport_hidden_sale_goal == 1;
    }

    public boolean getMonthReport_hidden_current_goal() {
        return monthReport_hidden_current_goal == 1;
    }

    public boolean getMonthReport_hidden_important_situation() {
        return monthReport_hidden_important_situation == 1;
    }

    public boolean getMonthReport_hidden_potential_market() {
        return monthReport_hidden_potential_market == 1;
    }

    public boolean getMonthReport_hidden_compete_strategy() {
        return monthReport_hidden_compete_strategy == 1;
    }

    public boolean getMonthReport_hidden_resource_investment() {
        return monthReport_hidden_resource_investment == 1;
    }

    public boolean getSpecial_price_hidden_approval() {
        return special_price_hidden_approval == 1;
    }

    public boolean getContract_hidden_total() {
        return contract_hidden_total == 1;
    }

    public boolean getSpecial_price_hidden_year_discount() {
        return special_price_hidden_year_discount == 1;
    }

    public boolean getSpecial_price_hidden_amount() {
        return special_price_hidden_amount == 1;
    }

    public boolean getAdd_trip_hidden_visit() {
        return add_trip_hidden_visit == 1;
    }

    public boolean getAdd_trip_hidden_office() {
        return add_trip_hidden_office == 1;
    }

    public boolean getAdd_trip_hidden_task() {
        return add_trip_hidden_task == 1;
    }

    public boolean getAdd_trip_hidden_absent() {
        return add_trip_hidden_absent == 1;
    }

    public boolean getAdd_trip_hidden_reimbursement() {
        return add_trip_hidden_reimbursement == 1;
    }

    public boolean getAdd_trip_hidden_contract() {
        return add_trip_hidden_contract == 1;
    }

    public boolean getAdd_trip_hidden_quotation() {
        return add_trip_hidden_quotation == 1;
    }

    public boolean getAdd_trip_hidden_sample() {
        return add_trip_hidden_sample == 1;
    }

    public boolean getAdd_trip_hidden_special_price() {
        return add_trip_hidden_special_price == 1;
    }

    public boolean getAdd_trip_hidden_new_project_production() {
        return add_trip_hidden_new_project_production == 1;
    }

    public boolean getAdd_trip_hidden_nonstandard_inquiry() {
        return add_trip_hidden_nonstandard_inquiry == 1;
    }

    public boolean getAdd_trip_hidden_spring_ring_inquiry() {
        return add_trip_hidden_spring_ring_inquiry == 1;
    }

    public boolean getAdd_trip_hidden_express() {
        return add_trip_hidden_express == 1;
    }

    public boolean getAdd_trip_hidden_travel() {
        return add_trip_hidden_travel == 1;
    }

    public boolean getSpecial_price_hidden_project_status() {
        return special_price_hidden_project_status == 1;
    }

    public boolean getSpecial_price_product_hidden_product_category() {
        return special_price_product_hidden_product_category == 1;
    }

    public boolean getSpecial_price_hidden_offer() {
        return special_price_hidden_offer == 1;
    }

    public boolean getSpecial_price_product_hidden_remark() {
        return special_price_product_hidden_remark == 1;
    }


    public boolean getExpress_hidden_type() {
        return express_hidden_type == 1;
    }

    public boolean getExpress_hidden_model() {
        return express_hidden_model == 1;
    }

    public boolean getExpress_hidden_expense() {
        return express_hidden_expense == 1;
    }

    public boolean getExpress_hidden_destination() {
        return express_hidden_destination == 1;
    }

    public boolean getExpress_hidden_product_category_id() {
        return express_hidden_product_category_id == 1;
    }

    public boolean getExpress_hidden_origin() {
        return express_hidden_origin == 1;
    }

    public boolean getExpress_hidden_arrival_payment() {
        return express_hidden_arrival_payment == 1;
    }

    public boolean getExpress_hidden_repayment_date() {
        return express_hidden_repayment_date == 1;
    }
}
