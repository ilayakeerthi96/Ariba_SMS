
// package com.itti.leadcapturing.web;

// import com.itti.leadcapturing.dto.*;
// import com.itti.leadcapturing.model.Buyer;
// import com.itti.leadcapturing.service.OrganizationAdminService;
// import com.itti.leadcapturing.service.BuyerService;
// import jakarta.validation.Valid;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.MediaType;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;
// import org.springframework.web.multipart.MultipartFile;

// import java.util.Map;
// import java.util.HashMap;
// import java.util.List;

// @RestController
// @RequestMapping("/api/organization-admin")
// @RequiredArgsConstructor
// @Slf4j
// @CrossOrigin(origins = "*", maxAge = 3600)
// public class OrganizationAdminController {

//     @Autowired
//     private OrganizationAdminService organizationAdminService;

//     @Autowired
//     private BuyerService buyerService;

    
//     // ============================================================
// // ADD THESE TWO ENDPOINTS to OrganizationAdminController.java
// // (paste alongside other endpoints, before the closing brace)
// // ============================================================

//     // SAVE ORG ADMIN THEME
//     @PutMapping("/{id}/theme")
//     public ResponseEntity<ApiResponse<String>> updateTheme(
//             @PathVariable Long id,
//             @RequestBody ThemeUpdateRequest request) {
//         log.info("🎨 Theme update for OrgAdmin ID: {}", id);
//         ApiResponse<String> response = organizationAdminService.updateOrgAdminTheme(id, request.getTheme());
//         return response.getSuccess()
//                 ? ResponseEntity.ok(response)
//                 : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//     }

//     // GET ORG ADMIN THEME
//     @GetMapping("/{id}/theme")
//     public ResponseEntity<ApiResponse<String>> getTheme(@PathVariable Long id) {
//         log.info("🎨 Get theme for OrgAdmin ID: {}", id);
//         ApiResponse<String> response = organizationAdminService.getOrgAdminTheme(id);
//         return response.getSuccess()
//                 ? ResponseEntity.ok(response)
//                 : ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
//     }

//     // 1. CREATE
//     @PostMapping
//     public ResponseEntity<ApiResponse<OrganizationAdminResponse>> createOrganizationAdmin(
//             @Valid @RequestBody OrganizationAdminCreateRequest request,
//             @RequestHeader(value = "X-Creator-Admin-Id", required = false) Long creatorAdminId) {
//         log.info("📥 Creating OrganizationAdmin: {}", request.getEmail());
//         ApiResponse<OrganizationAdminResponse> response =
//                 organizationAdminService.createOrganizationAdmin(request, creatorAdminId);
//         return response.getSuccess()
//                 ? ResponseEntity.status(HttpStatus.CREATED).body(response)
//                 : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//     }

//     // 2. LOGIN
//     @PostMapping("/auth/login")
//     public ResponseEntity<ApiResponse<OrganizationAdminResponse>> loginOrganizationAdmin(
//             @Valid @RequestBody OrganizationAdminLoginRequest request) {
//         log.info("🔐 OrganizationAdmin login attempt: {}", request.getEmail());
//         ApiResponse<OrganizationAdminResponse> response =
//                 organizationAdminService.loginOrganizationAdmin(request);
//         return response.getSuccess()
//                 ? ResponseEntity.ok(response)
//                 : ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
//     }

//     // 3. CHANGE PASSWORD
//     @PutMapping("/change-password/{adminId}")
//     public ResponseEntity<ApiResponse<String>> changePassword(
//             @PathVariable Long adminId,
//             @Valid @RequestBody ChangePasswordRequest request) {
//         log.info("🔐 Change password request for admin ID: {}", adminId);
//         ApiResponse<String> response = organizationAdminService.changePassword(adminId, request);
//         return response.getSuccess()
//                 ? ResponseEntity.ok(response)
//                 : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//     }

//     // 4. GET BY ID
//     @GetMapping("/{id}")
//     public ResponseEntity<ApiResponse<OrganizationAdminResponse>> getOrganizationAdminById(
//             @PathVariable Long id) {
//         log.info("📥 Fetching OrganizationAdmin: {}", id);
//         ApiResponse<OrganizationAdminResponse> response =
//                 organizationAdminService.getOrganizationAdminById(id);
//         return response.getSuccess()
//                 ? ResponseEntity.ok(response)
//                 : ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
//     }

//     // 5. GET BY COMPANY
//     @GetMapping("/company/{companyName}")
//     public ResponseEntity<ApiResponse<List<OrganizationAdminResponse>>> getAdminsByCompany(
//             @PathVariable String companyName) {
//         log.info("📥 Fetching admins for company: {}", companyName);
//         return ResponseEntity.ok(organizationAdminService.getAdminsByCompany(companyName));
//     }

//     // 6. GET ALL
//     @GetMapping("/all")
//     public ResponseEntity<ApiResponse<List<OrganizationAdminResponse>>> getAllOrganizationAdmins() {
//         log.info("📥 Fetching all OrganizationAdmins");
//         return ResponseEntity.ok(organizationAdminService.getAllOrganizationAdmins());
//     }

//     // 7. UPDATE
//     @PutMapping("/{id}")
//     public ResponseEntity<ApiResponse<OrganizationAdminResponse>> updateOrganizationAdmin(
//             @PathVariable Long id,
//             @Valid @RequestBody OrganizationAdminUpdateRequest request) {
//         log.info("✏️ Updating OrganizationAdmin: {}", id);
//         ApiResponse<OrganizationAdminResponse> response =
//                 organizationAdminService.updateOrganizationAdmin(id, request);
//         return response.getSuccess()
//                 ? ResponseEntity.ok(response)
//                 : ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
//     }

//     // 8. DEACTIVATE
//     @PutMapping("/{id}/deactivate")
//     public ResponseEntity<ApiResponse<String>> deactivateOrganizationAdmin(
//             @PathVariable Long id) {
//         log.info("🚫 Deactivating OrganizationAdmin: {}", id);
//         ApiResponse<String> response = organizationAdminService.deactivateOrganizationAdmin(id);
//         return response.getSuccess()
//                 ? ResponseEntity.ok(response)
//                 : ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
//     }

//     // 9. ACTIVATE
//     @PutMapping("/{id}/activate")
//     public ResponseEntity<ApiResponse<String>> activateOrganizationAdmin(
//             @PathVariable Long id) {
//         log.info("✅ Activating OrganizationAdmin: {}", id);
//         ApiResponse<String> response = organizationAdminService.activateOrganizationAdmin(id);
//         return response.getSuccess()
//                 ? ResponseEntity.ok(response)
//                 : ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
//     }

//     // 10. DELETE
//     @DeleteMapping("/{id}")
//     public ResponseEntity<ApiResponse<String>> deleteOrganizationAdmin(@PathVariable Long id) {
//         log.info("🗑️ Deleting OrganizationAdmin: {}", id);
//         ApiResponse<String> response = organizationAdminService.deleteOrganizationAdmin(id);
//         return response.getSuccess()
//                 ? ResponseEntity.ok(response)
//                 : ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
//     }

//     // 11. HEALTH CHECK
//     @GetMapping("/health")
//     public ResponseEntity<String> healthCheck() {
//         return ResponseEntity.ok("✅ OrganizationAdmin Service is running!");
//     }

//     // 12. CREATE BUYER
//     @PostMapping("/{adminId}/buyer")
//     public ResponseEntity<?> createBuyer(
//             @PathVariable Long adminId,
//             @RequestBody BuyerCreateByAdminDTO dto) {
//         try {
//             log.info("📥 ORG ADMIN {} CREATING BUYER — Company: {}", adminId, dto.getCompanyName());
//             Buyer createdBuyer = buyerService.createBuyerByOrganizationAdmin(dto, adminId);

//             Map<String, Object> response = new HashMap<>();
//             response.put("success", true);
//             response.put("message", "Buyer created successfully and linked to your organization");
//             response.put("data", createdBuyer);
//             return ResponseEntity.status(HttpStatus.CREATED).body(response);

//         } catch (RuntimeException e) {
//             log.error("❌ Runtime error creating buyer for admin {}: {}", adminId, e.getMessage());
//             Map<String, Object> response = new HashMap<>();
//             response.put("success", false);
//             response.put("message", e.getMessage());
//             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

//         } catch (Exception e) {
//             log.error("❌ Unexpected error creating buyer for admin {}: {}", adminId, e.getMessage());
//             Map<String, Object> response = new HashMap<>();
//             response.put("success", false);
//             response.put("message", "Failed to create buyer: " + e.getMessage());
//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//         }
//     }

//     // 13. GET BUYERS BY ADMIN
//     @GetMapping("/{adminId}/buyers")
//     public ResponseEntity<?> getBuyersCreatedByAdmin(@PathVariable Long adminId) {
//         try {
//             log.info("📥 Get Buyers by Admin ID: {}", adminId);
//             if (adminId == null || adminId <= 0) {
//                 Map<String, Object> response = new HashMap<>();
//                 response.put("success", false);
//                 response.put("message", "Invalid admin ID");
//                 return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//             }
//             List<Buyer> buyers = buyerService.getBuyersByOrganizationAdmin(adminId);
//             Map<String, Object> response = new HashMap<>();
//             response.put("success", true);
//             response.put("message", "Buyers retrieved successfully");
//             response.put("adminId", adminId);
//             response.put("count", buyers.size());
//             response.put("data", buyers);
//             return ResponseEntity.ok(response);

//         } catch (RuntimeException e) {
//             Map<String, Object> response = new HashMap<>();
//             response.put("success", false);
//             response.put("message", e.getMessage());
//             return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);

//         } catch (Exception e) {
//             Map<String, Object> response = new HashMap<>();
//             response.put("success", false);
//             response.put("message", "Failed to fetch buyers");
//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//         }
//     }

//     // 14. GET BUYERS BY ORGANIZATION COMPANY
//     @GetMapping("/company/{companyName}/buyers")
//     public ResponseEntity<?> getBuyersByOrganization(@PathVariable String companyName) {
//         try {
//             log.info("📥 Fetching buyers for organization: {}", companyName);
//             List<Buyer> buyers = buyerService.getBuyersByOrganizationCompanyName(companyName);
//             Map<String, Object> response = new HashMap<>();
//             response.put("success", true);
//             response.put("message", "Buyers retrieved successfully");
//             response.put("organization", companyName);
//             response.put("count", buyers.size());
//             response.put("data", buyers);
//             return ResponseEntity.ok(response);

//         } catch (Exception e) {
//             log.error("❌ Error fetching buyers for organization {}: {}", companyName, e.getMessage());
//             Map<String, Object> response = new HashMap<>();
//             response.put("success", false);
//             response.put("message", "Failed to fetch buyers: " + e.getMessage());
//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//         }
//     }

//     // 15. UPLOAD LOGO
//     @PostMapping(value = "/{id}/upload-logo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//     public ResponseEntity<ApiResponse<OrganizationAdminResponse>> uploadLogo(
//             @PathVariable Long id,
//             @RequestParam("logo") MultipartFile logoFile) {
//         log.info("🖼️ Logo upload for OrgAdmin ID: {}", id);
//         ApiResponse<OrganizationAdminResponse> response =
//                 organizationAdminService.uploadLogo(id, logoFile);
//         return response.getSuccess()
//                 ? ResponseEntity.ok(response)
//                 : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//     }

//     // 16. GET LOGO (base64)
//     @GetMapping("/{id}/logo")
//     public ResponseEntity<ApiResponse<String>> getOrganizationAdminLogo(@PathVariable Long id) {
//         log.info("🖼️ Get logo for OrgAdmin ID: {}", id);
//         ApiResponse<String> response = organizationAdminService.getOrganizationAdminLogo(id);
//         return response.getSuccess()
//                 ? ResponseEntity.ok(response)
//                 : ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
//     }
// }


package com.itti.leadcapturing.web;

import com.itti.leadcapturing.dto.*;
import com.itti.leadcapturing.model.Buyer;
import com.itti.leadcapturing.service.OrganizationAdminService;
import com.itti.leadcapturing.service.BuyerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

/**
 * MODIFIED:
 * - createBuyer: no longer accepts logo from frontend; DTO has no logo fields.
 *   The buyer will automatically use the admin's logo and company name.
 */
@RestController
@RequestMapping("/api/organization-admin")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class OrganizationAdminController {

    @Autowired
    private OrganizationAdminService organizationAdminService;

    @Autowired
    private BuyerService buyerService;

    // ============================================================
    // THEME ENDPOINTS
    // ============================================================

    @PutMapping("/{id}/theme")
    public ResponseEntity<ApiResponse<String>> updateTheme(
            @PathVariable Long id,
            @RequestBody ThemeUpdateRequest request) {
        log.info("🎨 Theme update for OrgAdmin ID: {}", id);
        ApiResponse<String> response = organizationAdminService.updateOrgAdminTheme(id, request.getTheme());
        return response.getSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @GetMapping("/{id}/theme")
    public ResponseEntity<ApiResponse<String>> getTheme(@PathVariable Long id) {
        log.info("🎨 Get theme for OrgAdmin ID: {}", id);
        ApiResponse<String> response = organizationAdminService.getOrgAdminTheme(id);
        return response.getSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // ============================================================
    // 1. CREATE ORG ADMIN
    // ============================================================
    @PostMapping
    public ResponseEntity<ApiResponse<OrganizationAdminResponse>> createOrganizationAdmin(
            @Valid @RequestBody OrganizationAdminCreateRequest request,
            @RequestHeader(value = "X-Creator-Admin-Id", required = false) Long creatorAdminId) {
        log.info("📥 Creating OrganizationAdmin: {}", request.getEmail());
        ApiResponse<OrganizationAdminResponse> response =
                organizationAdminService.createOrganizationAdmin(request, creatorAdminId);
        return response.getSuccess()
                ? ResponseEntity.status(HttpStatus.CREATED).body(response)
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // ============================================================
    // 2. LOGIN
    // ============================================================
    @PostMapping("/auth/login")
    public ResponseEntity<ApiResponse<OrganizationAdminResponse>> loginOrganizationAdmin(
            @Valid @RequestBody OrganizationAdminLoginRequest request) {
        log.info("🔐 OrganizationAdmin login attempt: {}", request.getEmail());
        ApiResponse<OrganizationAdminResponse> response =
                organizationAdminService.loginOrganizationAdmin(request);
        return response.getSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    // ============================================================
    // 3. CHANGE PASSWORD
    // ============================================================
    @PutMapping("/change-password/{adminId}")
    public ResponseEntity<ApiResponse<String>> changePassword(
            @PathVariable Long adminId,
            @Valid @RequestBody ChangePasswordRequest request) {
        log.info("🔐 Change password request for admin ID: {}", adminId);
        ApiResponse<String> response = organizationAdminService.changePassword(adminId, request);
        return response.getSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // ============================================================
    // 4. GET BY ID
    // ============================================================
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrganizationAdminResponse>> getOrganizationAdminById(
            @PathVariable Long id) {
        log.info("📥 Fetching OrganizationAdmin: {}", id);
        ApiResponse<OrganizationAdminResponse> response =
                organizationAdminService.getOrganizationAdminById(id);
        return response.getSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // ============================================================
    // 5. GET BY COMPANY
    // ============================================================
    @GetMapping("/company/{companyName}")
    public ResponseEntity<ApiResponse<List<OrganizationAdminResponse>>> getAdminsByCompany(
            @PathVariable String companyName) {
        log.info("📥 Fetching admins for company: {}", companyName);
        return ResponseEntity.ok(organizationAdminService.getAdminsByCompany(companyName));
    }

    // ============================================================
    // 6. GET ALL
    // ============================================================
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<OrganizationAdminResponse>>> getAllOrganizationAdmins() {
        log.info("📥 Fetching all OrganizationAdmins");
        return ResponseEntity.ok(organizationAdminService.getAllOrganizationAdmins());
    }

    // ============================================================
    // 7. UPDATE
    // ============================================================
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<OrganizationAdminResponse>> updateOrganizationAdmin(
            @PathVariable Long id,
            @Valid @RequestBody OrganizationAdminUpdateRequest request) {
        log.info("✏️ Updating OrganizationAdmin: {}", id);
        ApiResponse<OrganizationAdminResponse> response =
                organizationAdminService.updateOrganizationAdmin(id, request);
        return response.getSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // ============================================================
    // 8. DEACTIVATE
    // ============================================================
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<String>> deactivateOrganizationAdmin(
            @PathVariable Long id) {
        log.info("🚫 Deactivating OrganizationAdmin: {}", id);
        ApiResponse<String> response = organizationAdminService.deactivateOrganizationAdmin(id);
        return response.getSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // ============================================================
    // 9. ACTIVATE
    // ============================================================
    @PutMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<String>> activateOrganizationAdmin(
            @PathVariable Long id) {
        log.info("✅ Activating OrganizationAdmin: {}", id);
        ApiResponse<String> response = organizationAdminService.activateOrganizationAdmin(id);
        return response.getSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // ============================================================
    // 10. DELETE
    // ============================================================
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteOrganizationAdmin(@PathVariable Long id) {
        log.info("🗑️ Deleting OrganizationAdmin: {}", id);
        ApiResponse<String> response = organizationAdminService.deleteOrganizationAdmin(id);
        return response.getSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // ============================================================
    // 11. HEALTH CHECK
    // ============================================================
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("✅ OrganizationAdmin Service is running!");
    }

    // ============================================================
    // 12. CREATE BUYER (MODIFIED: no logo accepted — admin's logo is used)
    // ============================================================
    @PostMapping("/{adminId}/buyer")
    public ResponseEntity<?> createBuyer(
            @PathVariable Long adminId,
            @RequestBody BuyerCreateByAdminDTO dto) {
        try {
            log.info("📥 ORG ADMIN {} CREATING BUYER — Buyer Company: {}", adminId, dto.getCompanyName());
            log.info("    → Logo & company brand will be inherited from OrgAdmin ID: {}", adminId);

            Buyer createdBuyer = buyerService.createBuyerByOrganizationAdmin(dto, adminId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Buyer created successfully and linked to your organization. " +
                                    "Company logo and name are inherited from your admin profile.");
            response.put("data", createdBuyer);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (RuntimeException e) {
            log.error("❌ Runtime error creating buyer for admin {}: {}", adminId, e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

        } catch (Exception e) {
            log.error("❌ Unexpected error creating buyer for admin {}: {}", adminId, e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to create buyer: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ============================================================
    // 13. GET BUYERS BY ADMIN
    // ============================================================
    @GetMapping("/{adminId}/buyers")
    public ResponseEntity<?> getBuyersCreatedByAdmin(@PathVariable Long adminId) {
        try {
            log.info("📥 Get Buyers by Admin ID: {}", adminId);
            if (adminId == null || adminId <= 0) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Invalid admin ID");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            List<Buyer> buyers = buyerService.getBuyersByOrganizationAdmin(adminId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Buyers retrieved successfully");
            response.put("adminId", adminId);
            response.put("count", buyers.size());
            response.put("data", buyers);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to fetch buyers");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ============================================================
    // 14. GET BUYERS BY ORGANIZATION COMPANY
    // ============================================================
    @GetMapping("/company/{companyName}/buyers")
    public ResponseEntity<?> getBuyersByOrganization(@PathVariable String companyName) {
        try {
            log.info("📥 Fetching buyers for organization: {}", companyName);
            List<Buyer> buyers = buyerService.getBuyersByOrganizationCompanyName(companyName);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Buyers retrieved successfully");
            response.put("organization", companyName);
            response.put("count", buyers.size());
            response.put("data", buyers);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error fetching buyers for organization {}: {}", companyName, e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to fetch buyers: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ============================================================
    // 15. UPLOAD LOGO (admin-level — applies to all buyers under this admin)
    // ============================================================
    @PostMapping(value = "/{id}/upload-logo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<OrganizationAdminResponse>> uploadLogo(
            @PathVariable Long id,
            @RequestParam("logo") MultipartFile logoFile) {
        log.info("🖼️ Logo upload for OrgAdmin ID: {} — will apply to ALL buyers under this admin", id);
        ApiResponse<OrganizationAdminResponse> response =
                organizationAdminService.uploadLogo(id, logoFile);
        return response.getSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // ============================================================
    // 16. GET LOGO (base64) — admin-level logo
    // ============================================================
    @GetMapping("/{id}/logo")
    public ResponseEntity<ApiResponse<String>> getOrganizationAdminLogo(@PathVariable Long id) {
        log.info("🖼️ Get logo for OrgAdmin ID: {}", id);
        ApiResponse<String> response = organizationAdminService.getOrganizationAdminLogo(id);
        return response.getSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
}