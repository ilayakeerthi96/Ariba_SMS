import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import {LogoutDialogboxComponent} from './components/logout-dialogbox/logout-dialogbox.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { CustomerPayListComponent } from './components/customer-pay-list/customer-pay-list.component';
import { CustomerPayTrackComponent } from './components/customer-pay-track/customer-pay-track.component';
import { EditCustomerPayTrackComponent } from './components/edit-customer-pay-track/edit-customer-pay-track.component';
import { PaymentTermsComponent } from './components/payment-terms/payment-terms.component';
import { CreateCustomerComponent } from './components/create-customer/create-customer.component';
import { CreateProjectComponent } from './components/create-project/create-project.component';
import { EditProjectComponent } from './components/edit-project/edit-project.component';
import { CreatePaymentTermsComponent } from './components/create-payment-terms/create-payment-terms.component';
import { EditPaymentTermsComponent } from './components/edit-payment-terms/edit-payment-terms.component';
import { RoleListComponent } from './components/role-list/role-list.component';
import { CreateRoleComponent } from './components/create-role/create-role.component';
import { EditRoleComponent } from './components/edit-role/edit-role.component';
import { PracticeListComponent } from './components/practice-list/practice-list.component';
import { CreatePracticeComponent } from './components/create-practice/create-practice.component';
import { EditPracticeComponent } from './components/edit-practice/edit-practice.component';
import { SummaryReportComponent } from './components/summary-report/summary-report.component';
import { DesignationListComponent } from './components/designation-list/designation-list.component';
import { CreateDesignationComponent } from './components/create-designation/create-designation.component';
import { EditDesignationComponent } from './components/edit-designation/edit-designation.component';
import { EditGradeComponent } from './components/edit-grade/edit-grade.component';
import { ProjectComponent } from './components/project/project.component';
import { BillingIndentComponent } from './components/billing-indent/billing-indent.component';
import { CreateBillingIndentComponent } from './components/create-billing-indent/create-billing-indent.component';
import { BillingIndentDetailsComponent } from './components/billing-indent-details/billing-indent-details.component';
import { FinancialBillingIndentComponent } from './components/financial-billing-indent/financial-billing-indent.component';
import { FinancialBillingIndentDetailsComponent } from './components/financial-billing-indent-details/financial-billing-indent-details.component';
import { EditCustomerComponent } from './components/edit-customer/edit-customer.component';
import { CustomersComponent } from './components/customers/customers.component';
import { AllEmployeesComponent } from './components/all-employees/all-employees.component';
import { CreateEmployeeComponent } from './components/create-employee/create-employee.component';
import { EditEmployeeComponent } from './components/edit-employee/edit-employee.component';
import { ForgetPasswordComponent } from './components/forget-password/forget-password.component';
import { ChangePasswordComponent } from './components/change-password/change-password.component';
import { ExchangeRateListComponent } from './components/exchange-rate/exchange-rate.component';
import { UploadItemComponent } from './components/upload-item/upload-item.component';
import { PurchaseOrderComponent } from './components/purchaseorder/purchaseorder.component';
import { EditPurchaseOrderComponent } from './components/edit-purchaseorder/edit-purchaseorder.component';
import { CreatePurchaseOrderComponent } from './components/create-purchaseorder/create-purchaseorder.component';
import { VarianceReportComponent } from './components/variance-report/variance-report.component';
import { CustomerRevenueReportComponent } from './components/customer-revenue-report/customer-revenue.component';
import { ProjectWiseRevenueReportComponent } from './components/projectwise-revenue-report/projectwise-revenue.component';
import { TeamUtilizationReportComponent } from './components/teamutilization-revenue-report/team-utilization.component';
import { UnbilledRevenueReportComponent } from './components/unbilled-revenue-report/unbilled-revenue.component';
import { ReviewComponent } from './components/review-indent/review.component';
import { MonthlyBillingReportComponent } from './components/monthly-summary-report/MonthlyBillingReportComponent';

export const routes: Routes = [
    { path: "", redirectTo: "/dashboard", pathMatch: "full" },
    { path: "CustomerPayList", component: CustomerPayListComponent },
    { path: "login", component: LoginComponent },
    { path: "dashboard", component: DashboardComponent },
    { path: "project", component: ProjectComponent},
    { path: "customer-payment-track", component: CustomerPayTrackComponent },
    { path: "edit-payment-track", component: EditCustomerPayTrackComponent },
    { path: "payment-terms", component: PaymentTermsComponent },
    { path: "create-customers", component: CreateCustomerComponent },
    { path: "create-project", component: CreateProjectComponent },
    { path: "edit-project", component: EditProjectComponent },
    { path: "create-payment-terms", component: CreatePaymentTermsComponent },
    { path: "edit-payment-terms", component: EditPaymentTermsComponent },
    { path: "role-list", component: RoleListComponent },
    { path: "create-role", component: CreateRoleComponent },
    { path: "edit-role", component: EditRoleComponent },
    { path: "practice", component: PracticeListComponent },
    { path: "create-practice", component: CreatePracticeComponent },
    { path: "edit-practice", component: EditPracticeComponent },
    { path: "report-summary", component: SummaryReportComponent },
    { path: "variance-summary", component: VarianceReportComponent },    
    { path: "designation-list", component: DesignationListComponent },
    { path: "create-designation", component: CreateDesignationComponent },
    { path: "edit-designation", component: EditDesignationComponent },
    { path: "edit-grade", component: EditGradeComponent },
    { path: "billing-indent", component: BillingIndentComponent },
    { path: "create-billing-indent", component: CreateBillingIndentComponent },
    { path: "billing-indent-deatils", component: BillingIndentDetailsComponent },
    { path: "edit-customers", component: EditCustomerComponent },
    { path: "customers", component: CustomersComponent },
    { path: "employees", component: AllEmployeesComponent },
    { path: "create-employees", component: CreateEmployeeComponent },
    { path: "edit-employees", component: EditEmployeeComponent },
    { path: "logout", component:LogoutDialogboxComponent},
    { path: "forget-password", component: ForgetPasswordComponent},
    {path:"change-password", component: ChangePasswordComponent},
    {path:"exchange-rate-list", component: ExchangeRateListComponent},
    {path:"upload-item", component: UploadItemComponent},
    {path:"purchase-order-list", component: PurchaseOrderComponent},
    {path:"edit-purchase-order", component: EditPurchaseOrderComponent},
    {path:"create-purchase-order", component: CreatePurchaseOrderComponent},
    { path: "financial-billing-indent", component: FinancialBillingIndentComponent },
    { path: "financial-billing-details", component: FinancialBillingIndentDetailsComponent },
    { path: "customer-revenue-report", component: CustomerRevenueReportComponent },
    { path: "project-revenue-report", component: ProjectWiseRevenueReportComponent },
    { path: "team-utilization-report", component: TeamUtilizationReportComponent },
    { path: "unbilled-revenue-report", component: UnbilledRevenueReportComponent },
    { path: "review-billing-indent", component: ReviewComponent },
    { path: "monthly-billing-report", component: MonthlyBillingReportComponent },
];
