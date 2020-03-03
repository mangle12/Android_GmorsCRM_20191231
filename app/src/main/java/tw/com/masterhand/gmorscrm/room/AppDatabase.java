/*
 * Copyright 2017, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tw.com.masterhand.gmorscrm.room;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import tw.com.masterhand.gmorscrm.room.converter.DateConverter;
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
import tw.com.masterhand.gmorscrm.room.record.UserQuickMenu;
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
import tw.com.masterhand.gmorscrm.room.setting.CurrencyRate;
import tw.com.masterhand.gmorscrm.room.setting.Department;
import tw.com.masterhand.gmorscrm.room.setting.DepartmentContractItem;
import tw.com.masterhand.gmorscrm.room.setting.DepartmentSalesOpportunity;
import tw.com.masterhand.gmorscrm.room.setting.DepartmentSalesOpportunitySub;
import tw.com.masterhand.gmorscrm.room.setting.User;
import tw.com.masterhand.gmorscrm.room.setting.UserLeave;

@Database(entities = {Company.class, ConfigCity.class, ConfigCountry.class, ConfigCurrency.class,
        ConfigCustomerLevel.class, ConfigCustomerSapType.class, ConfigIndustry.class,
        ConfigQuotationProductCategory.class, ConfigQuotationProductReport.class,
        ConfigReimbursementItem.class, CurrencyRate.class, Department.class,
        DepartmentContractItem.class, DepartmentSalesOpportunity.class, ConfigProjectSource
        .class, ConfigSalesOpportunityType.class, ConfigSalesOpportunityLoseType.class,
        ConfigSalesOpportunityWinType.class, ConfigSampleAmountRange.class, ConfigSampleReason
        .class, ConfigSampleSource.class, ConfigSampleImportMethod.class,
        ConfigSamplePaymentMethod.class, ConfigCompanyHiddenField
        .class, ConfigCustomerSapGroup.class, ConfigCustomerSource.class,
        ConfigProductCategorySub.class, Report.class,
        ConfigSampleProductType.class, SampleProduct.class, MonthReport.class,
        DepartmentSalesOpportunitySub.class, User.class, UserLeave.class, UserQuickMenu.class,
        Customer.class, ContactPerson.class, Contacter.class, Participant
        .class, File.class, FilePivot.class, TaskConversation.class, TaskMessage.class, Absent
        .class, Contract.class, Office.class, Project.class, Quotation.class, QuotationProduct
        .class, Reimbursement.class, ReimbursementItem.class, SalesOpportunity.class,
        SalesOpportunitySub.class, Sample.class, Task.class, Trip.class, TripStatusLog.class,
        Admin.class, Visit.class, SyncLog.class, ReadLog.class, DownloadLog.class,
        AuthorityCustomer.class, AuthorityProject.class, Express.class, NewProjectProduction
        .class, ConfigNewProjectProduction.class, SpecialPrice.class, SpecialPriceProduct.class,
        Travel.class, NonStandardInquiry.class, NonStandardInquiryProduct.class, ConfigOffice
        .class, SpringRingInquiry.class, ConfigYearPotential.class, ConfigInquiryProductType
        .class, ConfigCompetitiveStatus.class, ConfigSgbAdvantage.class, ConfigSgbDisadvantage
        .class, ConfigTrend.class, ConfigSealedState.class, SpringRingInquiryProduct.class,
        ConfigExpressDestination.class,
        SpecialShip.class, Repayment.class, ConfigTax.class},
        version = 1)
@TypeConverters(DateConverter.class)
public abstract class AppDatabase extends RoomDatabase {

//    static final String DATABASE_NAME = "gmors";

    public abstract SettingDao settingDao();

    public abstract RecordDao recordDao();

    public abstract LocalDao localDao();
}
