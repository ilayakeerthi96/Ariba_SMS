

package com.itti.leadcapturing.service;

import com.itti.leadcapturing.model.*;
import com.itti.leadcapturing.repo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SupplierDashboardService {

    private static final Logger logger = LoggerFactory.getLogger(SupplierDashboardService.class);

    @Autowired private RFQSupplierRepository rfqSupplierRepository;
    @Autowired private RFQRepository rfqRepository;
    @Autowired private RFQItemRepository rfqItemRepository;
    @Autowired private SupplierRepository supplierRepository;
    @Autowired private RFQAttachmentRepository rfqAttachmentRepository;
    @Autowired private RFQItemAttachmentRepository rfqItemAttachmentRepository;

    // =========================================================================
    // CORE EXPIRY LOGIC — single method, used everywhere
    //
    // An RFQ is "expired" for a supplier when ALL three conditions hold:
    //   1. The RFQ has a dueDate set
    //   2. The current server time is AFTER that dueDate
    //   3. The supplier has NOT yet responded (status is still PENDING / SENT)
    //
    // Suppliers who responded BEFORE the deadline are never locked out.
    // They can still VIEW their submitted quote in read-only mode.
    // =========================================================================
    private boolean isRFQExpiredForSupplier(RFQ rfq, RFQSupplier rfqSupplier) {
        if (rfq.getDueDate() == null) {
            // No deadline set by buyer — never expires
            return false;
        }

        boolean pastDeadline = LocalDateTime.now().isAfter(rfq.getDueDate());

        // Supplier who already responded is not "expired" — they made the deadline
        boolean alreadyActed = "RESPONDED".equals(rfqSupplier.getStatus())
                            || "SELECTED".equals(rfqSupplier.getStatus())
                            || "REJECTED".equals(rfqSupplier.getStatus());

        return pastDeadline && !alreadyActed;
    }

    // =========================================================================
    // CURRENCY RESOLUTION — same-country vs cross-border rule (unchanged)
    // =========================================================================
    private String[] resolveCurrency(Location buyerLocation, Supplier supplier) {
        if (buyerLocation == null || supplier == null) {
            return new String[]{"INR", "₹"};
        }
        String buyerCountry    = buyerLocation.getCountry();
        String supplierCountry = supplier.getCountry();
        boolean sameCountry    = buyerCountry != null
                              && supplierCountry != null
                              && buyerCountry.trim().equalsIgnoreCase(supplierCountry.trim());

        if (sameCountry) {
            String code   = buyerLocation.getCurrencyCode()   != null ? buyerLocation.getCurrencyCode()   : "INR";
            String symbol = buyerLocation.getCurrencySymbol() != null ? buyerLocation.getCurrencySymbol() : "₹";
            logger.info("  Currency [SAME COUNTRY] {} / {} → {} {}", buyerCountry, supplierCountry, code, symbol);
            return new String[]{code, symbol};
        } else {
            logger.info("  Currency [CROSS BORDER] {} / {} → USD $", buyerCountry, supplierCountry);
            return new String[]{"USD", "$"};
        }
    }

    // =========================================================================
    // DASHBOARD STATISTICS
    // Now includes expiredRFQs count and excludes expired from pendingRFQs
    // =========================================================================
    @Transactional(readOnly = true)
    public Map<String, Object> getSupplierDashboardStats(Long supplierId) {
        logger.info("📊 [SUPPLIER DASHBOARD STATS] Supplier ID: {}", supplierId);

        supplierRepository.findById(supplierId)
                .orElseThrow(() -> new RuntimeException("Supplier not found: " + supplierId));

        List<RFQSupplier> allLinks = rfqSupplierRepository.findBySupplier(supplierId);

        // Only count PUBLISHED RFQs — draft/awaiting approval are invisible to suppliers
        List<RFQSupplier> published = allLinks.stream()
                .filter(rs -> rs.getRfq() != null
                           && RFQStatus.PUBLISHED.equals(rs.getRfq().getStatus()))
                .collect(Collectors.toList());

        // Partition into expired vs active
        long expiredCount = published.stream()
                .filter(rs -> isRFQExpiredForSupplier(rs.getRfq(), rs))
                .count();

        // pendingRFQs excludes expired so the number reflects actionable items only
        long pendingCount = published.stream()
                .filter(rs -> ("PENDING".equals(rs.getStatus()) || "SENT".equals(rs.getStatus()))
                           && !isRFQExpiredForSupplier(rs.getRfq(), rs))
                .count();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRFQs",      published.size());
        stats.put("pendingRFQs",    pendingCount);
        stats.put("respondedRFQs",  published.stream().filter(rs -> "RESPONDED".equals(rs.getStatus())).count());
        stats.put("selectedRFQs",   published.stream().filter(rs -> "SELECTED".equals(rs.getStatus())).count());
        stats.put("rejectedRFQs",   published.stream().filter(rs -> "REJECTED".equals(rs.getStatus())).count());
        stats.put("expiredRFQs",    expiredCount);  // NEW — shown in the "Expired" stat card
        stats.put("statusBreakdown", published.stream()
                .collect(Collectors.groupingBy(RFQSupplier::getStatus, Collectors.counting())));

        logger.info("  Stats computed: total={}, pending={}, expired={}", published.size(), pendingCount, expiredCount);
        return stats;
    }

    // =========================================================================
    // GET RFQs FOR SUPPLIER
    // Returns all PUBLISHED RFQs with expiry flags injected per row.
    // The "EXPIRED" virtual status filter is handled here on the backend.
    // =========================================================================
    @Transactional(readOnly = true)
    public Map<String, Object> getSupplierRFQs(Long supplierId, Map<String, String> filters) {
        logger.info("📋 [GET SUPPLIER RFQs] Supplier ID: {}", supplierId);

        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new RuntimeException("Supplier not found: " + supplierId));

        List<RFQSupplier> allLinks = rfqSupplierRepository.findBySupplier(supplierId);

        // Only show PUBLISHED RFQs
        List<RFQSupplier> published = allLinks.stream()
                .filter(rs -> rs.getRfq() != null
                           && RFQStatus.PUBLISHED.equals(rs.getRfq().getStatus()))
                .collect(Collectors.toList());

        List<RFQSupplier> filtered = published;

        // --- Status filter ---------------------------------------------------
        // "EXPIRED" is a virtual status — it maps to the expiry check rather
        // than the actual rfqSupplier.status column value.
        String statusParam = filters.get("status");
        if (statusParam != null && !statusParam.equals("ALL")) {
            if ("EXPIRED".equals(statusParam)) {
                // Show only RFQs where the deadline has passed and supplier never responded
                filtered = filtered.stream()
                        .filter(rs -> isRFQExpiredForSupplier(rs.getRfq(), rs))
                        .collect(Collectors.toList());
            } else {
                // For all real statuses, exclude expired rows so they don't appear
                // under PENDING/SENT even though their column value says PENDING/SENT
                filtered = filtered.stream()
                        .filter(rs -> statusParam.equals(rs.getStatus())
                                   && !isRFQExpiredForSupplier(rs.getRfq(), rs))
                        .collect(Collectors.toList());
            }
        }

        // --- Search filter (unchanged) ---------------------------------------
        String search = filters.get("search");
        if (search != null && !search.trim().isEmpty()) {
            String lower = search.toLowerCase();
            filtered = filtered.stream()
                    .filter(rs -> rs.getRfq().getRfqNumber().toLowerCase().contains(lower)
                               || rs.getRfq().getRfqTitle().toLowerCase().contains(lower))
                    .collect(Collectors.toList());
        }

        final Supplier supplierRef = supplier;
        List<Map<String, Object>> rfqList = filtered.stream()
                .map(rs -> buildSupplierRFQSummary(rs, supplierRef))
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("total", filtered.size());
        response.put("rfqs",  rfqList);
        return response;
    }

    // =========================================================================
    // GET RFQ COMPLETE DETAILS (called when supplier clicks "View" or "Submit")
    // Expiry flags are injected here too so the detail page knows what to show.
    // =========================================================================
    @Transactional(readOnly = true)
    public Map<String, Object> getSupplierRFQDetails(Long supplierId, Long rfqId) {
        logger.info("🔍 [GET RFQ DETAILS] Supplier: {}, RFQ: {}", supplierId, rfqId);

        RFQSupplier rfqSupplier = rfqSupplierRepository.findByRFQAndSupplier(rfqId, supplierId)
                .orElseThrow(() -> new RuntimeException("RFQ not accessible to this supplier"));

        RFQ rfq = rfqSupplier.getRfq();

        if (!RFQStatus.PUBLISHED.equals(rfq.getStatus())) {
            throw new RuntimeException("RFQ is not published. Status: " + rfq.getStatus());
        }

        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new RuntimeException("Supplier not found: " + supplierId));

        // Compute expiry once and derive all permission flags from it
        boolean expired         = isRFQExpiredForSupplier(rfq, rfqSupplier);
        boolean alreadyResponded = "RESPONDED".equals(rfqSupplier.getStatus())
                                || "SELECTED".equals(rfqSupplier.getStatus());

        // Days remaining/overdue — negative means overdue
        Long daysUntilDue = null;
        if (rfq.getDueDate() != null) {
            daysUntilDue = ChronoUnit.DAYS.between(
                LocalDateTime.now().toLocalDate(),
                rfq.getDueDate().toLocalDate()
            );
        }

        Map<String, Object> details = new HashMap<>();

        // --- Core RFQ fields -------------------------------------------------
        details.put("rfqId",               rfq.getId());
        details.put("rfqNumber",           rfq.getRfqNumber());
        details.put("rfqTitle",            rfq.getRfqTitle());
        details.put("rfqDescription",      rfq.getRfqDescription());
        details.put("issueDate",           rfq.getIssueDate());
        details.put("dueDate",             rfq.getDueDate());
        details.put("itemRequiredDate",    rfq.getItemRequiredDate());
        details.put("status",              rfq.getStatus().toString());
        details.put("priority",            rfq.getPriority() != null ? rfq.getPriority().toString() : "MEDIUM");
        details.put("allowSupplierDownload", rfq.getAllowSupplierDownload() != null ? rfq.getAllowSupplierDownload() : true);

        // --- Expiry flags (consumed by frontend) -----------------------------
        details.put("isExpired",       expired);
        details.put("expiredAt",       rfq.getDueDate());        // exact moment it expired
        details.put("daysUntilDue",    daysUntilDue);            // negative = overdue
        details.put("canSubmitQuote",  !expired && canSubmitQuote(rfqSupplier.getStatus()));
        details.put("canEditQuote",    !expired && canEditQuote(rfqSupplier.getStatus()));
        details.put("canViewQuote",    alreadyResponded);         // view-only even post-expiry

        // --- Buyer info ------------------------------------------------------
        if (rfq.getBuyer() != null) {
            Map<String, Object> buyer = new HashMap<>();
            buyer.put("id",            rfq.getBuyer().getId());
            buyer.put("companyName",   rfq.getBuyer().getCompanyName());
            buyer.put("contactEmail",  rfq.getBuyer().getContactPersonEmail());
            buyer.put("contactPhone",  rfq.getBuyer().getContactPersonPhone());
            buyer.put("contactPerson", rfq.getBuyer().getContactPersonName());
            buyer.put("gstNumber",     rfq.getBuyer().getGstNumber());
            buyer.put("panNumber",     rfq.getBuyer().getPanNumber());
            buyer.put("address",       rfq.getBuyer().getAddressLine1());
            buyer.put("city",          rfq.getBuyer().getCity());
            buyer.put("state",         rfq.getBuyer().getState());
            buyer.put("country",       rfq.getBuyer().getCountry());
            details.put("buyer", buyer);
        }

        // --- Location info ---------------------------------------------------
        if (rfq.getLocation() != null) {
            Map<String, Object> loc = new HashMap<>();
            loc.put("id",             rfq.getLocation().getId());
            loc.put("locationName",   rfq.getLocation().getLocationName());
            loc.put("locationType",   rfq.getLocation().getLocationType());
            loc.put("addressLine1",   rfq.getLocation().getAddressLine1());
            loc.put("addressLine2",   rfq.getLocation().getAddressLine2());
            loc.put("city",           rfq.getLocation().getCity());
            loc.put("state",          rfq.getLocation().getState());
            loc.put("country",        rfq.getLocation().getCountry());
            loc.put("postalCode",     rfq.getLocation().getPostalCode());
            loc.put("contactName",    rfq.getLocation().getLocationContactName());
            loc.put("contactEmail",   rfq.getLocation().getLocationContactEmail());
            loc.put("contactPhone",   rfq.getLocation().getLocationContactPhone());
            loc.put("currencyCode",   rfq.getLocation().getCurrencyCode());
            loc.put("currencySymbol", rfq.getLocation().getCurrencySymbol());
            details.put("location", loc);
        }

        // --- Resolved currency -----------------------------------------------
        String[] resolved = resolveCurrency(rfq.getLocation(), supplier);
        details.put("resolvedCurrencyCode",   resolved[0]);
        details.put("resolvedCurrencySymbol", resolved[1]);
        details.put("currencyCode",           resolved[0]);
        details.put("currencySymbol",         resolved[1]);

        // --- Terms -----------------------------------------------------------
        details.put("paymentTerms",       rfq.getPaymentTerms());
        details.put("deliveryTerms",      rfq.getDeliveryTerms());
        details.put("justification",      rfq.getJustification());
        details.put("taxPercentage",      rfq.getTaxPercentage());
        details.put("costCenterCode",     rfq.getCostCenterCode());
        details.put("projectCode",        rfq.getProjectCode());
        details.put("allowSplitPO",       rfq.getAllowSplitPO());
        details.put("preferredVendorsOnly", rfq.getPreferredVendorsOnly());
        details.put("approvalRequired",   rfq.getApprovalRequired());

        // --- Items -----------------------------------------------------------
        List<RFQItem> items = rfqItemRepository.findByRfqId(rfqId);
        details.put("items",      items.stream().map(this::buildCompleteItemDetails).collect(Collectors.toList()));
        details.put("itemsCount", items.size());

        // --- Attachments (RFQ-level + item-level) ----------------------------
        List<Map<String, Object>> attachments = new ArrayList<>();
        rfqAttachmentRepository.findByRfqId(rfqId)
                .forEach(a -> attachments.add(buildAttachmentDetails(a)));
        for (RFQItem item : items) {
            rfqItemAttachmentRepository.findByItemId(item.getId())
                    .forEach(a -> attachments.add(buildItemAttachmentDetails(a, item)));
        }
        details.put("attachments",      attachments);
        details.put("attachmentsCount", attachments.size());

        // --- Supplier response state -----------------------------------------
        details.put("supplierStatus",      rfqSupplier.getStatus());
        details.put("supplierSentAt",      rfqSupplier.getSentAt());
        details.put("supplierRespondedAt", rfqSupplier.getRespondedAt());
        details.put("supplierQuoteAmount", rfqSupplier.getQuoteAmount());
        details.put("supplierNotes",       rfqSupplier.getNotes());

        // --- Creator info ----------------------------------------------------
        if (rfq.getCreatedByUser() != null) {
            Map<String, Object> creator = new HashMap<>();
            creator.put("name",        rfq.getCreatedByUser().getFirstName() + " " + rfq.getCreatedByUser().getLastName());
            creator.put("email",       rfq.getCreatedByUser().getEmail());
            creator.put("designation", rfq.getCreatedByUser().getDesignation());
            details.put("createdBy", creator);
        }

        details.put("createdAt", rfq.getCreatedAt());
        details.put("updatedAt", rfq.getUpdatedAt());

        logger.info("  Details built. expired={}, canSubmit={}, canView={}", expired, !expired && canSubmitQuote(rfqSupplier.getStatus()), alreadyResponded);
        return details;
    }

    // =========================================================================
    // SUBMIT QUOTE — backend is the authoritative guard
    // Frontend hides the button; backend rejects the call even if bypassed.
    // =========================================================================
    @Transactional
    public String submitQuote(Long supplierId, Long rfqId, Map<String, Object> quoteData) {
        logger.info("📝 [SUBMIT QUOTE] Supplier: {}, RFQ: {}", supplierId, rfqId);

        RFQSupplier rfqSupplier = rfqSupplierRepository.findByRFQAndSupplier(rfqId, supplierId)
                .orElseThrow(() -> new RuntimeException("RFQ not accessible to this supplier"));

        RFQ rfq = rfqSupplier.getRfq();

        if (!RFQStatus.PUBLISHED.equals(rfq.getStatus())) {
            throw new RuntimeException("Cannot submit quote — RFQ is not published.");
        }

        // AUTHORITATIVE EXPIRY CHECK
        // This is the real security gate. The frontend check is UX only.
        if (isRFQExpiredForSupplier(rfq, rfqSupplier)) {
            String deadline = rfq.getDueDate() != null ? rfq.getDueDate().toString() : "N/A";
            throw new RuntimeException(
                "The submission deadline for RFQ " + rfq.getRfqNumber()
                + " passed on " + deadline
                + ". No further quotes can be accepted."
            );
        }

        if ("RESPONDED".equals(rfqSupplier.getStatus())) {
            throw new RuntimeException("Quote already submitted. Use the update endpoint to modify.");
        }
        if ("SELECTED".equals(rfqSupplier.getStatus()) || "REJECTED".equals(rfqSupplier.getStatus())) {
            throw new RuntimeException("Cannot modify quote — RFQ has been finalised.");
        }

        Double quoteAmount = quoteData.get("quoteAmount") != null
                ? Double.parseDouble(quoteData.get("quoteAmount").toString()) : null;
        if (quoteAmount == null || quoteAmount <= 0) {
            throw new RuntimeException("A valid quote amount is required.");
        }

        rfqSupplier.setStatus("RESPONDED");
        rfqSupplier.setQuoteAmount(java.math.BigDecimal.valueOf(quoteAmount));
        rfqSupplier.setNotes(quoteData.get("notes") != null ? quoteData.get("notes").toString() : "");
        rfqSupplier.setRespondedAt(LocalDateTime.now());
        rfqSupplierRepository.save(rfqSupplier);

        logger.info("  Quote accepted. Amount: {}", quoteAmount);
        return "Quote submitted successfully.";
    }

    // =========================================================================
    // UPDATE QUOTE — also blocked after expiry
    // =========================================================================
    @Transactional
    public String updateQuote(Long supplierId, Long rfqId, Map<String, Object> quoteData) {
        RFQSupplier rfqSupplier = rfqSupplierRepository.findByRFQAndSupplier(rfqId, supplierId)
                .orElseThrow(() -> new RuntimeException("RFQ not accessible to this supplier"));

        RFQ rfq = rfqSupplier.getRfq();

        if (!RFQStatus.PUBLISHED.equals(rfq.getStatus())) {
            throw new RuntimeException("Cannot update quote — RFQ is not published.");
        }

        // Suppliers who responded before the deadline can no longer edit post-expiry
        if (isRFQExpiredForSupplier(rfq, rfqSupplier)) {
            throw new RuntimeException("The submission deadline has passed. Quotes can no longer be modified.");
        }

        if (!"RESPONDED".equals(rfqSupplier.getStatus())) {
            throw new RuntimeException("Can only update a quote that has already been submitted.");
        }

        if (quoteData.containsKey("quoteAmount")) {
            double amt = Double.parseDouble(quoteData.get("quoteAmount").toString());
            if (amt <= 0) throw new RuntimeException("Valid quote amount required.");
            rfqSupplier.setQuoteAmount(java.math.BigDecimal.valueOf(amt));
        }
        if (quoteData.containsKey("notes")) {
            rfqSupplier.setNotes(quoteData.get("notes").toString());
        }
        rfqSupplierRepository.save(rfqSupplier);

        return "Quote updated successfully.";
    }

    // =========================================================================
    // PRIVATE HELPERS
    // =========================================================================

    /**
     * Builds the summary row object sent to the dashboard list view.
     * Injects isExpired, daysUntilDue, canSubmitQuote so the frontend
     * can render the correct badge and button without an extra API call.
     */
    private Map<String, Object> buildSupplierRFQSummary(RFQSupplier rs, Supplier supplier) {
        Map<String, Object> row = new HashMap<>();
        RFQ rfq = rs.getRfq();

        boolean expired = isRFQExpiredForSupplier(rfq, rs);

        // Days remaining: positive = days left, 0 = due today, negative = overdue
        Long daysUntilDue = null;
        if (rfq.getDueDate() != null) {
            daysUntilDue = ChronoUnit.DAYS.between(
                LocalDateTime.now().toLocalDate(),
                rfq.getDueDate().toLocalDate()
            );
        }

        row.put("rfqId",           rfq.getId());
        row.put("rfqNumber",       rfq.getRfqNumber());
        row.put("rfqTitle",        rfq.getRfqTitle());
        row.put("rfqDescription",  rfq.getRfqDescription());
        row.put("issueDate",       rfq.getIssueDate());
        row.put("dueDate",         rfq.getDueDate());
        row.put("itemRequiredDate",rfq.getItemRequiredDate());
        row.put("rfqStatus",       rfq.getStatus().toString());
        row.put("priority",        rfq.getPriority() != null ? rfq.getPriority().toString() : "MEDIUM");

        if (rfq.getBuyer() != null) {
            row.put("buyerName",  rfq.getBuyer().getCompanyName());
            row.put("buyerEmail", rfq.getBuyer().getContactPersonEmail());
        }

        String[] resolved = resolveCurrency(rfq.getLocation(), supplier);
        row.put("currencyCode",           resolved[0]);
        row.put("currencySymbol",         resolved[1]);
        row.put("resolvedCurrencyCode",   resolved[0]);
        row.put("resolvedCurrencySymbol", resolved[1]);

        row.put("supplierStatus",  rs.getStatus());
        row.put("sentAt",          rs.getSentAt());
        row.put("respondedAt",     rs.getRespondedAt());
        row.put("quoteAmount",     rs.getQuoteAmount());
        row.put("hasResponded",    "RESPONDED".equals(rs.getStatus()));

        // Expiry fields consumed by the frontend
        row.put("isExpired",      expired);
        row.put("daysUntilDue",   daysUntilDue);
        row.put("canSubmitQuote", !expired && canSubmitQuote(rs.getStatus()));
        row.put("canEditQuote",   !expired && canEditQuote(rs.getStatus()));

        try {
            row.put("itemsCount", rfq.getItems() != null ? rfq.getItems().size() : 0);
        } catch (Exception e) {
            row.put("itemsCount", 0);
        }

        return row;
    }

    private Map<String, Object> buildCompleteItemDetails(RFQItem item) {
        Map<String, Object> d = new HashMap<>();
        d.put("id",                     item.getId());
        d.put("itemCode",               item.getItemCode());
        d.put("itemDescription",        item.getItemDescription());
        d.put("itemDescriptionDetailed",item.getItemDescriptionDetailed());
        d.put("quantity",               item.getQuantity());
        d.put("uom",                    item.getUom());
        d.put("unitPrice",              item.getUnitPrice());
        d.put("lineTotal",              item.getLineTotal());
        d.put("specifications",         item.getSpecifications());
        d.put("itemRequiredDate",       item.getItemRequiredDate());
        d.put("itemOrder",              item.getItemOrder());
        d.put("companyType",            item.getCompanyType());
        try {
            d.put("dynamicFields", (item.getDynamicFields() != null && !item.getDynamicFields().isEmpty())
                    ? item.getDynamicFieldsAsMap() : new HashMap<>());
        } catch (Exception e) {
            d.put("dynamicFields", new HashMap<>());
        }
        return d;
    }

    private Map<String, Object> buildAttachmentDetails(RFQAttachment a) {
        Map<String, Object> d = new HashMap<>();
        d.put("id",             a.getId());
        d.put("fileName",       a.getFileName());
        d.put("fileType",       a.getFileType());
        d.put("fileSize",       a.getFileSize());
        d.put("filePath",       a.getFilePath());
        d.put("description",    a.getDescription());
        d.put("attachmentType", a.getAttachmentType() != null ? a.getAttachmentType().toString() : "RFQ");
        d.put("uploadedBy",     a.getUploadedBy());
        d.put("createdAt",      a.getCreatedAt());
        d.put("downloadUrl",    "/api/rfq-attachment/" + a.getId() + "/download");
        return d;
    }

    private Map<String, Object> buildItemAttachmentDetails(RFQItemAttachment a, RFQItem item) {
        Map<String, Object> d = new HashMap<>();
        d.put("id",              a.getId());
        d.put("fileName",        a.getFileName());
        d.put("fileType",        a.getFileType());
        d.put("fileSize",        a.getFileSize());
        d.put("filePath",        a.getFilePath());
        d.put("description",     a.getDescription() != null ? a.getDescription() : "Item attachment");
        d.put("attachmentType",  "ITEM_ATTACHMENT");
        d.put("uploadedBy",      a.getUploadedBy());
        d.put("createdAt",       a.getCreatedAt());
        d.put("itemId",          item.getId());
        d.put("itemDescription", item.getItemDescription());
        d.put("downloadUrl",     "/api/rfq-item-attachment/" + a.getId() + "/download");
        return d;
    }

    private boolean canSubmitQuote(String status) {
        return "PENDING".equals(status) || "SENT".equals(status);
    }

    private boolean canEditQuote(String status) {
        return "RESPONDED".equals(status);
    }
}