
// package com.itti.leadcapturing.web;

// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.web.bind.annotation.*;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.HttpHeaders;
// import org.springframework.http.MediaType;
// import org.springframework.http.ResponseEntity;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;

// import com.itti.leadcapturing.model.Supplier;
// import com.itti.leadcapturing.service.SupplierService;

// import jakarta.validation.Valid;

// @RestController
// @RequestMapping("/api/supplier")
// @CrossOrigin(origins = "*")
// public class SupplierController {

//     private static final Logger logger = LoggerFactory.getLogger(SupplierController.class);

//     @Autowired
//     private SupplierService supplierService;

//     /**
//      * GET /api/supplier
//      * Get all suppliers
//      */
//     @GetMapping
//     public ResponseEntity<?> getAllSuppliers() {
//         try {
//             logger.info("[GET ALL SUPPLIERS] Request received");

//             List<Supplier> suppliers = supplierService.getAllSuppliers();

//             logger.info("[GET ALL SUPPLIERS] Success - Found {} suppliers", suppliers.size());

//             return ResponseEntity.ok(suppliers);

//         } catch (Exception e) {
//             logger.error("[GET ALL SUPPLIERS] Exception occurred:", e);
//             e.printStackTrace();

//             Map<String, Object> response = new HashMap<>();
//             response.put("success", false);
//             response.put("message", "Failed to fetch suppliers");
//             response.put("error", e.getClass().getSimpleName());
//             response.put("details", e.getMessage());

//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//         }
//     }

//     /**
//      * GET /api/supplier/{id}
//      * Get supplier by ID
//      */
//     @GetMapping("/{id}")
//     public ResponseEntity<?> getSupplierById(@PathVariable Long id) {
//         try {
//             logger.info("[GET SUPPLIER] Fetching supplier with ID: {}", id);

//             Supplier supplier = supplierService.getSupplierById(id);

//             logger.info("[GET SUPPLIER] Success - Supplier: {}", supplier.getCompanyName());

//             return ResponseEntity.ok(supplier);

//         } catch (RuntimeException e) {
//             logger.error("[GET SUPPLIER] RuntimeException for ID {}: {}", id, e.getMessage());

//             Map<String, Object> response = new HashMap<>();
//             response.put("success", false);
//             response.put("message", "Supplier not found");

//             return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);

//         } catch (Exception e) {
//             logger.error("[GET SUPPLIER] Exception for ID {}", id, e);
//             e.printStackTrace();

//             Map<String, Object> response = new HashMap<>();
//             response.put("success", false);
//             response.put("message", e.getMessage());

//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//         }
//     }

//     /**
//      * POST /api/supplier
//      * Create a new supplier (with logo + bcrypt password support)
//      */
//     @PostMapping
//     public ResponseEntity<?> createSupplier(@Valid @RequestBody Supplier supplier) {
//         try {
//             logger.info("[CREATE SUPPLIER] Creating supplier: {}", supplier.getCompanyName());

//             Supplier savedSupplier = supplierService.createSupplier(supplier);

//             logger.info("[CREATE SUPPLIER] Success - ID: {}", savedSupplier.getId());

//             Map<String, Object> response = new HashMap<>();
//             response.put("success", true);
//             response.put("message", "Supplier created successfully");
//             response.put("data", savedSupplier);

//             return ResponseEntity.status(HttpStatus.CREATED).body(response);

//         } catch (Exception e) {
//             logger.error("[CREATE SUPPLIER] Exception:", e);
//             e.printStackTrace();

//             Map<String, Object> response = new HashMap<>();
//             response.put("success", false);
//             response.put("message", e.getMessage());

//             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//         }
//     }

//     /**
//      * PUT /api/supplier/{id}
//      * Update supplier by ID
//      */
//     @PutMapping("/{id}")
//     public ResponseEntity<?> updateSupplier(
//             @PathVariable Long id,
//             @RequestBody Supplier supplierReq) {
//         try {
//             logger.info("[UPDATE SUPPLIER] Updating supplier ID: {}", id);

//             Supplier updated = supplierService.updateSupplier(id, supplierReq);

//             logger.info("[UPDATE SUPPLIER] Success - ID: {}", id);

//             Map<String, Object> response = new HashMap<>();
//             response.put("success", true);
//             response.put("message", "Supplier updated successfully");
//             response.put("data", updated);

//             return ResponseEntity.ok(response);

//         } catch (RuntimeException e) {
//             logger.error("[UPDATE SUPPLIER] RuntimeException for ID {}: {}", id, e.getMessage());

//             Map<String, Object> response = new HashMap<>();
//             response.put("success", false);
//             response.put("message", e.getMessage());

//             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//         }
//     }

//     /**
//      * DELETE /api/supplier/{id}
//      * Delete supplier
//      */
//     @DeleteMapping("/{id}")
//     public ResponseEntity<?> deleteSupplier(@PathVariable Long id) {
//         try {
//             logger.info("[DELETE SUPPLIER] Deleting supplier ID: {}", id);

//             supplierService.deleteSupplier(id);

//             logger.info("[DELETE SUPPLIER] Success - ID: {}", id);

//             Map<String, Object> response = new HashMap<>();
//             response.put("success", true);
//             response.put("message", "Supplier deleted successfully!");

//             return ResponseEntity.ok(response);

//         } catch (RuntimeException e) {
//             logger.error("[DELETE SUPPLIER] RuntimeException for ID {}: {}", id, e.getMessage());

//             Map<String, Object> response = new HashMap<>();
//             response.put("success", false);
//             response.put("message", e.getMessage());

//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//         }
//     }

//     // ============================================
//     // ✅ NEW: GET SUPPLIER LOGO AS BASE64
//     // GET /api/supplier/{id}/logo/base64
//     // ============================================

//     @GetMapping("/{id}/logo/base64")
//     public ResponseEntity<String> getSupplierLogoBase64(@PathVariable Long id) {
//         try {
//             logger.info("📥 GET SUPPLIER LOGO BASE64: {}", id);

//             String logoBase64 = supplierService.getSupplierLogoBase64(id);

//             if (logoBase64 == null || logoBase64.trim().isEmpty()) {
//                 logger.warn("⚠️ No logo data for supplier: {}", id);
//                 return ResponseEntity.noContent().build(); // 204
//             }

//             logger.info("✅ Returning logo — {} chars", logoBase64.length());

//             // ✅ Use header() not contentType() — avoids Spring/Jackson JSON-encoding the string
//             return ResponseEntity.ok()
//                     .header("Content-Type", "text/plain;charset=UTF-8")
//                     .header("Cache-Control", "public, max-age=3600")
//                     .body(logoBase64);

//         } catch (RuntimeException e) {
//             logger.error("❌ Supplier not found: {}", id);
//             return ResponseEntity.noContent().build();
//         } catch (Exception e) {
//             logger.error("❌ Error: {}", e.getMessage());
//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//         }
//     }

//     // ============================================
//     // ✅ NEW: GET SUPPLIER LOGO AS RAW BYTES
//     // GET /api/supplier/{id}/logo
//     // ============================================

//     @GetMapping("/{id}/logo")
//     public ResponseEntity<byte[]> getSupplierLogo(@PathVariable Long id) {
//         try {
//             logger.info("📥 GET SUPPLIER LOGO (RAW): {}", id);

//             Supplier supplier = supplierService.getSupplierById(id);

//             if (supplier.getLogoData() == null || supplier.getLogoData().length == 0) {
//                 logger.info("⚠️ No logo data for supplier: {}", id);
//                 return ResponseEntity.notFound().build();
//             }

//             HttpHeaders headers = new HttpHeaders();

//             String contentType = supplier.getLogoContentType();
//             if (contentType == null || contentType.isEmpty()) {
//                 contentType = "image/png";
//             }

//             headers.setContentType(MediaType.parseMediaType(contentType));
//             headers.setContentLength(supplier.getLogoData().length);

//             logger.info("✅ Logo returned. Size: {} bytes, Type: {}",
//                         supplier.getLogoData().length, contentType);

//             return new ResponseEntity<>(supplier.getLogoData(), headers, HttpStatus.OK);

//         } catch (RuntimeException e) {
//             logger.error("❌ Supplier not found: {}", id);
//             return ResponseEntity.notFound().build();

//         } catch (Exception e) {
//             logger.error("❌ Error getting logo for supplier: {}", id);
//             logger.error("Error: {}", e.getMessage());
//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//         }
//     }
//     // ============================================================
// // 3. ADD to SupplierController.java
// // ============================================================

//     @GetMapping("/{id}/theme")
//     public ResponseEntity<?> getSupplierTheme(@PathVariable Long id) {
//         try {
//             String theme = supplierService.getTheme(id);
//             Map<String, Object> response = new HashMap<>();
//             response.put("success", true);
//             response.put("data", theme);
//             return ResponseEntity.ok(response);
//         } catch (Exception e) {
//             Map<String, Object> response = new HashMap<>();
//             response.put("success", false);
//             response.put("message", e.getMessage());
//             return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
//         }
//     }

//     @PutMapping("/{id}/theme")
//     public ResponseEntity<?> updateSupplierTheme(
//             @PathVariable Long id,
//             @RequestBody Map<String, String> body) {
//         try {
//             String theme = body.get("theme");
//             supplierService.updateTheme(id, theme);
//             Map<String, Object> response = new HashMap<>();
//             response.put("success", true);
//             response.put("data", theme);
//             return ResponseEntity.ok(response);
//         } catch (Exception e) {
//             Map<String, Object> response = new HashMap<>();
//             response.put("success", false);
//             response.put("message", e.getMessage());
//             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//         }
//     }
// }




package com.itti.leadcapturing.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itti.leadcapturing.model.Supplier;
import com.itti.leadcapturing.service.SupplierService;
import com.itti.leadcapturing.service.SupplierApprovalService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/supplier")
@CrossOrigin(origins = "*")
public class SupplierController {

    private static final Logger logger = LoggerFactory.getLogger(SupplierController.class);

    @Autowired
private com.itti.leadcapturing.repo.OrganizationAdminRepository organizationAdminRepository;

    @Autowired
    private SupplierService supplierService;

    // ✅ Injected to auto-initiate approval after supplier creation
    @Autowired
    private SupplierApprovalService supplierApprovalService;

    /**
 * GET /api/supplier/register/organizations
 * PUBLIC — returns list of active organization company names for the registration dropdown.
 */
@GetMapping("/register/organizations")
public ResponseEntity<?> getRegistrationOrganizations() {
    try {
        List<String> names = organizationAdminRepository.findByIsActive(true)
                .stream()
                .map(admin -> admin.getCompanyName())
                .distinct()
                .sorted()
                .collect(java.util.stream.Collectors.toList());
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", names);
        return ResponseEntity.ok(response);
    } catch (Exception e) {
        logger.error("[REGISTER ORGS] Error: {}", e.getMessage());
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}

/**
 * POST /api/supplier/register
 * PUBLIC — supplier self-registration. Creates supplier as PENDING and initiates approval.
 */
@PostMapping("/register")
public ResponseEntity<?> selfRegisterSupplier(
        @Valid @RequestBody com.itti.leadcapturing.dto.SupplierRegistrationRequest request) {
    try {
        logger.info("[SUPPLIER SELF-REGISTER] Company: {} | Email: {} | Org: {}",
                request.getCompanyName(), request.getEmail(), request.getOrganizationCompanyName());

        Supplier saved = supplierService.registerSupplier(request);
        logger.info("[SUPPLIER SELF-REGISTER] Saved ID: {}", saved.getId());

        String approvalMessage;
        try {
            approvalMessage = supplierApprovalService.initiateApprovalWorkflow(
                    saved.getId(), request.getOrganizationCompanyName());
        } catch (Exception e) {
            logger.warn("[SUPPLIER SELF-REGISTER] Approval init failed: {}", e.getMessage());
            approvalMessage = "Registration received. Awaiting administrator setup.";
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Registration successful! Your account is pending approval. " + approvalMessage);
        response.put("supplierId", saved.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    } catch (Exception e) {
        logger.error("[SUPPLIER SELF-REGISTER] Error: {}", e.getMessage(), e);
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}


    /**
     * GET /api/supplier
     */
    @GetMapping
    public ResponseEntity<?> getAllSuppliers() {
        try {
            logger.info("[GET ALL SUPPLIERS] Request received");
            List<Supplier> suppliers = supplierService.getAllSuppliers();
            logger.info("[GET ALL SUPPLIERS] Success - Found {} suppliers", suppliers.size());
            return ResponseEntity.ok(suppliers);
        } catch (Exception e) {
            logger.error("[GET ALL SUPPLIERS] Exception occurred:", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to fetch suppliers");
            response.put("error", e.getClass().getSimpleName());
            response.put("details", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/supplier/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getSupplierById(@PathVariable Long id) {
        try {
            logger.info("[GET SUPPLIER] Fetching supplier with ID: {}", id);
            Supplier supplier = supplierService.getSupplierById(id);
            logger.info("[GET SUPPLIER] Success - Supplier: {}", supplier.getCompanyName());
            return ResponseEntity.ok(supplier);
        } catch (RuntimeException e) {
            logger.error("[GET SUPPLIER] RuntimeException for ID {}: {}", id, e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Supplier not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            logger.error("[GET SUPPLIER] Exception for ID {}", id, e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * POST /api/supplier
     * ✅ UPDATED: Auto-initiates approval workflow after creation.
     * Frontend must send createdByCompanyName and createdByUserId in payload.
     */
    @PostMapping
    public ResponseEntity<?> createSupplier(@Valid @RequestBody Supplier supplier) {
        try {
            logger.info("[CREATE SUPPLIER] Creating supplier: {}", supplier.getCompanyName());
            logger.info("[CREATE SUPPLIER] createdByCompanyName: {}", supplier.getCreatedByCompanyName());
            logger.info("[CREATE SUPPLIER] createdByUserId: {}", supplier.getCreatedByUserId());

            Supplier savedSupplier = supplierService.createSupplier(supplier);
            logger.info("[CREATE SUPPLIER] Saved - ID: {}", savedSupplier.getId());

            // ✅ AUTO-INITIATE APPROVAL WORKFLOW
            String approvalMessage = "Supplier created. Approval workflow could not be started — check hierarchy setup.";
            if (savedSupplier.getCreatedByCompanyName() != null
                    && !savedSupplier.getCreatedByCompanyName().isBlank()) {
                try {
                    approvalMessage = supplierApprovalService.initiateApprovalWorkflow(
                            savedSupplier.getId(),
                            savedSupplier.getCreatedByCompanyName()
                    );
                    logger.info("[CREATE SUPPLIER] Approval workflow initiated: {}", approvalMessage);
                } catch (Exception approvalEx) {
                    // Don't fail supplier creation — just log the warning
                    logger.warn("[CREATE SUPPLIER] Approval workflow initiation failed: {}", approvalEx.getMessage());
                    approvalMessage = "Supplier created but approval workflow could not be started: " + approvalEx.getMessage();
                }
            } else {
                logger.warn("[CREATE SUPPLIER] createdByCompanyName is blank — skipping approval workflow.");
                approvalMessage = "Supplier created. No approval workflow started (company name missing).";
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Supplier created successfully. " + approvalMessage);
            response.put("data", savedSupplier);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            logger.error("[CREATE SUPPLIER] Exception:", e);
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * PUT /api/supplier/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateSupplier(@PathVariable Long id, @RequestBody Supplier supplierReq) {
        try {
            logger.info("[UPDATE SUPPLIER] Updating supplier ID: {}", id);
            Supplier updated = supplierService.updateSupplier(id, supplierReq);
            logger.info("[UPDATE SUPPLIER] Success - ID: {}", id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Supplier updated successfully");
            response.put("data", updated);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            logger.error("[UPDATE SUPPLIER] RuntimeException for ID {}: {}", id, e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * DELETE /api/supplier/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSupplier(@PathVariable Long id) {
        try {
            logger.info("[DELETE SUPPLIER] Deleting supplier ID: {}", id);
            supplierService.deleteSupplier(id);
            logger.info("[DELETE SUPPLIER] Success - ID: {}", id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Supplier deleted successfully!");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            logger.error("[DELETE SUPPLIER] RuntimeException for ID {}: {}", id, e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/supplier/{id}/logo/base64
     */
    @GetMapping("/{id}/logo/base64")
    public ResponseEntity<String> getSupplierLogoBase64(@PathVariable Long id) {
        try {
            logger.info("📥 GET SUPPLIER LOGO BASE64: {}", id);
            String logoBase64 = supplierService.getSupplierLogoBase64(id);
            if (logoBase64 == null || logoBase64.trim().isEmpty()) {
                logger.warn("⚠️ No logo data for supplier: {}", id);
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok()
                    .header("Content-Type", "text/plain;charset=UTF-8")
                    .header("Cache-Control", "public, max-age=3600")
                    .body(logoBase64);
        } catch (RuntimeException e) {
            logger.error("❌ Supplier not found: {}", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("❌ Error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * GET /api/supplier/{id}/logo
     */
    @GetMapping("/{id}/logo")
    public ResponseEntity<byte[]> getSupplierLogo(@PathVariable Long id) {
        try {
            logger.info("📥 GET SUPPLIER LOGO (RAW): {}", id);
            Supplier supplier = supplierService.getSupplierById(id);
            if (supplier.getLogoData() == null || supplier.getLogoData().length == 0) {
                return ResponseEntity.notFound().build();
            }
            HttpHeaders headers = new HttpHeaders();
            String contentType = supplier.getLogoContentType();
            if (contentType == null || contentType.isEmpty()) contentType = "image/png";
            headers.setContentType(MediaType.parseMediaType(contentType));
            headers.setContentLength(supplier.getLogoData().length);
            return new ResponseEntity<>(supplier.getLogoData(), headers, HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * GET /api/supplier/{id}/theme
     */
    @GetMapping("/{id}/theme")
    public ResponseEntity<?> getSupplierTheme(@PathVariable Long id) {
        try {
            String theme = supplierService.getTheme(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", theme);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * PUT /api/supplier/{id}/theme
     */
    @PutMapping("/{id}/theme")
    public ResponseEntity<?> updateSupplierTheme(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            String theme = body.get("theme");
            supplierService.updateTheme(id, theme);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", theme);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}