package com.itti.gateway.config;

import java.util.List;
import java.util.function.Predicate;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

@Component
public class RouterValidator {

    public static final List<String> openApiEndpoints = List.of(

         "/api/v1/auth/signin",
    "leadcapture/api/buyer/login",
    "leadcapture/api/superadmin/auth/register",
    "leadcapture/api/superadmin/auth/login",
    "leadcapture/api/admin/auth/login",
    "leadcapture/api/hierarchy-levels",
    "leadcapture/api/hierarchy-users",
    "leadcapture/api/hierarchy/auth/**",
    "leadcapture/api/hierarchy/auth/login",
    "leadcapture/api/organization-admin/auth/login",
    "leadcapture/api/superadmin/create-org-admin",
    "leadcapture/api/superadmin/org-admins",
    // "leadcapture/api/organization-admin/change-password/**",
    "leadcapture/api/dynamic-rfq-approval",
    "leadcapture/api/dynamic-rfq-approval/initiate",
    "leadcapture/api/dynamic-rfq-approval/pending/user/**",
    "leadcapture/api/dynamic-rfq-approval/approve",
    "leadcapture/api/dynamic-rfq-approval/reject",
    "leadcapture/api/dynamic-rfq-approval/resubmit",
    "leadcapture/api/dynamic-rfq-approval/history/**",
    "leadcapture/api/dynamic-rfq-approval/current/**",
    "leadcapture/api/dynamic-rfq-approval/pending/user/*/count",
    "leadcapture/api/organization-admin/*/buyer",
    "leadcapture/api/organization-admin/*/buyers",
    "leadcapture/api/dynamic-rfq-approval/return-for-revision",
    "leadcapture/api/quote-comparison/rfq/**",
    "leadcapture/api/purchase-order/create",
    "leadcapture/api/purchase-order/company/**",
    "leadcapture/api/purchase-order/buyer/**",

    "leadcapture/api/hierarchy-levels",
    "leadcapture/api/hierarchy-levels/company/**",
    "leadcapture/api/hierarchy-levels/**",
    "leadcapture/api/hierarchy-users",
    "leadcapture/api/hierarchy-users/**",
    "leadcapture/api/hierarchy-users/auth/login",
    "leadcapture/api/rfq-approval/approve/**",
   "leadcapture/api/po-approval/initiate",
   "leadcapture/api/po-approval/pending/user/**",
   "leadcapture/api/po-approval/pending/user/*/count",
   "leadcapture/api/po-approval/reject",
   "leadcapture/api/purchase-order/**",
   "leadcapture/api/po-approval/hold/user/*/count",
   "leadcapture/api/po-approval/history/**",
   "leadcapture/api/po-approval/hold",
   "leadcapture/api/po-approval/release-hold",
   "leadcapture/api/po-approval/return-for-revision",
   "leadcapture/api/po-approval/approve",
   "leadcapture/api/purchase-order/company/**",
   "leadcapture/api/purchase-order/buyer/**",
   "leadcapture/api/invoice/supplier/*/approved-pos",
   "leadcapture/api/invoice/po-details/**",
   "leadcapture/api/invoice/po-details/*/**",
   "leadcapture/api/invoice/create/**",
   "leadcapture/api/invoice/create/*/**",
   "leadcapture/api/invoice/create/**/**",
   "leadcapture/api/invoice/*/submit/**",
   "leadcapture/api/invoice/supplier/**",
   "leadcapture/api/invoice/**",
   "leadcapture/api/invoice/buyer/company/**",
   "leadcapture/api/invoice/buyer/**",
   "leadcapture/api/invoice/po/**",
   "leadcapture/api/invoice/*/approve",
   "leadcapture/api/invoice/*/reject",
   "leadcapture/api/invoice/*/mark-paid",
   "leadcapture/api/invoice/*/request-resubmission",
   "leadcapture/api/invoice/*/validate-match/**",
   "leadcapture/api/invoice/*/validate-match/*",
   "leadcapture/api/invoice/*/resubmit/**",
   "leadcapture/api/invoice/*/resubmit/*",
   "leadcapture/api/invoice/*/reject-close",
   "leadcapture/api/invoice/*/update/**",
   "leadcapture/api/invoice/*/update/*",
   "leadcapture/api/supplier-selection/rfq/*/status",
   "leadcapture/api/supplier-selection/select",
   "leadcapture/api/supplier-selection/rfq/**",
   "leadcapture/api/supplier-selection/rfq/*/exists",
   "leadcapture/api/po-negotiation/init",
   "leadcapture/api/po-negotiation/**",
   "leadcapture/api/po-negotiation/*/create-po",
   "leadcapture/api/po-negotiation/rfq/**",
   "leadcapture/api/po-pdf/**",
   "leadcapture/api/po-pdf/*/preview",
   "leadcapture/api/grn",
   "leadcapture/api/three-way-match",
    
  //const url = environment.API_URL +  `leadcapture/api/supplier-selection/rfq/${rfqId}/status`;
 
    "leadcapture/api/rfq/**",
    "leadcapture/api/rfq/buyer/**",
    "leadcapture/api/rfq-item/**",

    "leadcapture/api/buyer/**",
    "leadcapture/api/location/**",
    "leadcapture/api/supplier/**",

       "leadcapture/api/buyer",
       "leadcapture/api/buyer/login",
         "leadcapture/api/supplier",
         "leadcapture/api/rfq",

    "/subscribe/**"
        
        // "api/v1/auth/signin",
        //  "/leadcapture/api/buyer/login",
        //  "/leadcapture/api/rfq/{buyerId}/{locationId}/{userId}",//create rfq
        //  "/leadcapture/api/rfq/buyer/{buyerId}",//get all rfq for a buyer
        // "/leadcapture/api/rfq-item/{rfqId}",// ADD ITEMS TO RFQ
        // "leadcapture/api/rfq-item/rfq/{rfqId}",//GET ITEMS FOR AN RFQ
        // "/leadcapture/api/rfq/{rfqId}/suppliers",//add supplier to rfq
        // "/leadcapture/api/rfq/{rfqId}/approvers",//ADD APPROVERS TO RFQ
        // "/leadcapture/api/rfq/{rfqId}/publish",// PUBLISH RFQ (Submit for Approval)
        // "/leadcapture/api/rfq/{rfqId}/approve?approverId={approverId}&comments={comments}",//APPROVE RFQ
        // "/leadcapture/api/rfq/{rfqId}/reject?rejectedBy={userId}&comments={comments}",//reject
        // "/leadcapture/api/rfq/{rfqId}",//update
        // "/leadcapture/api/rfq/{rfqId}",//get single rfq
        // "/leadcapture/api/rfq/buyer/{buyerId}/status/{status}",//get rfq by status
        //  "/leadcapture/api/buyer",
        //  "/leadcapture/api/supplier",
        //  "/leadcapture/api/rfq",
        // "subscribe/"
    );

    public Predicate<ServerHttpRequest> isSecured =
            request -> openApiEndpoints
                    .stream()
                    .noneMatch(uri -> request.getURI().getPath().contains(uri));

}