package com.itti.gateway.config;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouterValidator {

    public static final List<String> openApiEndpoints = List.of(

         "leadcapture/uploads/logos", 

         "/api/v1/auth/signin",
    "/leadcapture/api/buyer/login",
    "leadcapture/api/superadmin/auth/register",
    "leadcapture/api/superadmin/auth/login",
    "/leadcapture/api/admin/auth/login",
    "leadcapture/api/hierarchy-levels",
    "leadcapture/api/hierarchy-users",
    "/leadcapture/api/hierarchy/auth/**",
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

    "leadcapture/api/hierarchy-levels",
    "leadcapture/api/hierarchy-levels/company/**",
    "leadcapture/api/hierarchy-levels/**",
    "/leadcapture/api/hierarchy-users",
    "/leadcapture/api/hierarchy-users/**",
    "/leadcapture/api/hierarchy-users/auth/login",
    "/leadcapture/api/rfq-approval/approve/**",
    "leadcapture/api/organization-admin/*/upload-logo",
    "leadcapture/api/organization-admin/**/upload-logo",
    

    "/leadcapture/api/rfq/**",
    "/leadcapture/api/rfq/buyer/**",
    "/leadcapture/api/rfq-item/**",

    "/leadcapture/api/buyer/**",
    "/leadcapture/api/location/**",
    "/leadcapture/api/supplier/**",

       "/leadcapture/api/buyer",
       "leadcapture/api/buyer/login",
         "/leadcapture/api/supplier",
         "/leadcapture/api/rfq",

         "leadcapture/api/quote-comparison/rfq/**",
         "leadcapture/api/purchase-order/create",
         "leadcapture/api/grn/*/start-qa-review",
         "leadcapture/api/grn/**/start-qa-review",
         "/leadcapture/api/grn/user/**",
         "/leadcapture/api/grn/*/qa-review",
         "/leadcapture/api/grn/**/qa-review",
         "/leadcapture/api/grn",
         "/leadcapture/api/grn/**",
         "/leadcapture/api/grn/*/revert-to-draft",
         "/leadcapture/api/grn/**/revert-to-draft",
         "/leadcapture/api/grn/**/link-invoice/**",
         "/leadcapture/api/grn/*/link-invoice/**",
         "/leadcapture/api/grn/*/submit",
         "/leadcapture/api/grn/**/submit",
         "/leadcapture/api/grn/**/approve",
         "/leadcapture/api/grn/*/approve",
         "/leadcapture/api/grn/*/cancel",
         "/leadcapture/api/grn/**/cancel",
         "/leadcapture/api/grn/**/close",
         "/leadcapture/api/grn/*/close",
         "/leadcapture/api/grn/**",
         "/leadcapture/api/grn/po/**",
         "/leadcapture/api/grn/po/**/all",
         "/leadcapture/api/grn/po/*/all",
         "/leadcapture/api/grn/po/*/invoice/**",
         "/leadcapture/api/grn/po/**/invoice/**",
         "/leadcapture/api/grn/invoice/**",

         "leadcapture/api/three-way-match/perform",
         "leadcapture/api/three-way-match/*/resolve",
         "leadcapture/api/three-way-match/**/resolve",
         "leadcapture/api/three-way-match/**",
         "leadcapture/api/three-way-match/invoice/**",
         "leadcapture/api/three-way-match/invoice/**/latest",
         "leadcapture/api/three-way-match/invoice/*/latest",
         "leadcapture/api/three-way-match/po/**",
         "leadcapture/api/three-way-match/pending",

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