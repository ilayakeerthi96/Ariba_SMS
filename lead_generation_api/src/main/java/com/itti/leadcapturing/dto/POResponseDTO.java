

// package com.itti.leadcapturing.dto;

// import com.itti.leadcapturing.model.POStatus;
// import com.itti.leadcapturing.model.ApprovalStatus;
// import java.math.BigDecimal;
// import java.time.LocalDateTime;
// import java.util.List;

// public class POResponseDTO {

//     private Long id;
//     private String poNumber;
//     private LocalDateTime poDate;
//     private String referenceQuoteNo;

//     // Buyer Info
//     private Long buyerId;
//     private String buyerName;
//     private String buyerGstin;
//     private String buyerPan;
//     private String buyerEmail;
//     private String buyerContactName;
//     private String buyerContactPhone;

//     // Buyer Location (Invoice To)
//     private Long buyerLocationId;
//     private String buyerLocationName;
//     private String buyerLocationAddress;

//     // Delivery Location (Ship To)
//     private Long deliveryLocationId;
//     private String deliveryLocationName;
//     private String deliveryLocationAddress;

//     // Supplier Info
//     private Long supplierId;
//     private String supplierName;
//     private String supplierGstin;
//     private String supplierContactName;
//     private String supplierContactEmail;
//     private String supplierContactPhone;
//     private String supplierAddress;
//     private String supplierCity;
//     private String supplierState;

//     // RFQ Reference
//     private Long rfqId;
//     private String rfqNumber;
//     private String rfqTitle;

//     private Long createdByUserId;

    

//     // Line Items
//     private List<POLineItemDTO> lineItems;

//     // Financial
//     private BigDecimal subtotal;
//     private BigDecimal taxPercentage;
//     private BigDecimal taxAmount;
//     private BigDecimal totalTaxAmount;
//     private BigDecimal grandTotal;
//     private String amountInWords;

//     // ✅ NEW: Currency — set from buyer's location during PO creation
//     private String currencyCode;    // e.g., INR, USD, EUR
//     private String currencySymbol;  // e.g., ₹, $, €

//     // Terms
//     private String paymentTerms;
//     private String deliveryTerms;
//     private String otherTerms;
//     private String dispatchedThrough;
//     private String modeOfPayment;
//     private String destination;

//     // Remarks
//     private String buyerRemarks;
//     private String internalNotes;

//     // Status
//     private POStatus status;
//     private Boolean approvalRequired;
//     private ApprovalStatus approvalStatus;
//     private Long approvedBy;
//     private LocalDateTime approvalDate;
//     private String approvedByName;
//     private String approvedByDesignation;

//     // Audit
//     private LocalDateTime createdAt;
//     private LocalDateTime updatedAt;

//     // ==================== LINE ITEM INNER DTO ====================

//     public static class POLineItemDTO {
//         private Long id;
//         private Integer slNo;
//         private String itemCode;
//         private String itemDescription;
//         private String specifications;
//         private String brandMakeModel;
//         private BigDecimal quantity;
//         private String uom;
//         private BigDecimal unitRate;
//         private BigDecimal lineTotal;
//         private BigDecimal originalQuotedPrice;
//         private BigDecimal discountPercentage;
//         private BigDecimal taxPercentage;
//         private BigDecimal taxAmount;
//         private BigDecimal lineTotalWithTax;
//         private Integer deliveryDays;
//         private Integer warrantyMonths;

//         public Long getId() { return id; }
//         public void setId(Long id) { this.id = id; }
//         public Integer getSlNo() { return slNo; }
//         public void setSlNo(Integer slNo) { this.slNo = slNo; }
//         public String getItemCode() { return itemCode; }
//         public void setItemCode(String itemCode) { this.itemCode = itemCode; }
//         public String getItemDescription() { return itemDescription; }
//         public void setItemDescription(String v) { this.itemDescription = v; }
//         public String getSpecifications() { return specifications; }
//         public void setSpecifications(String v) { this.specifications = v; }
//         public String getBrandMakeModel() { return brandMakeModel; }
//         public void setBrandMakeModel(String v) { this.brandMakeModel = v; }
//         public BigDecimal getQuantity() { return quantity; }
//         public void setQuantity(BigDecimal v) { this.quantity = v; }
//         public String getUom() { return uom; }
//         public void setUom(String uom) { this.uom = uom; }
//         public BigDecimal getUnitRate() { return unitRate; }
//         public void setUnitRate(BigDecimal v) { this.unitRate = v; }
//         public BigDecimal getLineTotal() { return lineTotal; }
//         public void setLineTotal(BigDecimal v) { this.lineTotal = v; }
//         public BigDecimal getOriginalQuotedPrice() { return originalQuotedPrice; }
//         public void setOriginalQuotedPrice(BigDecimal v) { this.originalQuotedPrice = v; }
//         public BigDecimal getDiscountPercentage() { return discountPercentage; }
//         public void setDiscountPercentage(BigDecimal v) { this.discountPercentage = v; }
//         public BigDecimal getTaxPercentage() { return taxPercentage; }
//         public void setTaxPercentage(BigDecimal v) { this.taxPercentage = v; }
//         public BigDecimal getTaxAmount() { return taxAmount; }
//         public void setTaxAmount(BigDecimal v) { this.taxAmount = v; }
//         public BigDecimal getLineTotalWithTax() { return lineTotalWithTax; }
//         public void setLineTotalWithTax(BigDecimal v) { this.lineTotalWithTax = v; }
//         public Integer getDeliveryDays() { return deliveryDays; }
//         public void setDeliveryDays(Integer v) { this.deliveryDays = v; }
//         public Integer getWarrantyMonths() { return warrantyMonths; }
//         public void setWarrantyMonths(Integer v) { this.warrantyMonths = v; }
//     }

//     // ==================== GETTERS AND SETTERS ====================

//     public Long getId() { return id; }
//     public void setId(Long id) { this.id = id; }
//     public String getPoNumber() { return poNumber; }
//     public void setPoNumber(String poNumber) { this.poNumber = poNumber; }
//     public LocalDateTime getPoDate() { return poDate; }
//     public void setPoDate(LocalDateTime poDate) { this.poDate = poDate; }
//     public String getReferenceQuoteNo() { return referenceQuoteNo; }
//     public void setReferenceQuoteNo(String referenceQuoteNo) { this.referenceQuoteNo = referenceQuoteNo; }
//     public Long getBuyerId() { return buyerId; }
//     public void setBuyerId(Long buyerId) { this.buyerId = buyerId; }
//     public String getBuyerName() { return buyerName; }
//     public void setBuyerName(String buyerName) { this.buyerName = buyerName; }
//     public String getBuyerGstin() { return buyerGstin; }
//     public void setBuyerGstin(String buyerGstin) { this.buyerGstin = buyerGstin; }
//     public String getBuyerPan() { return buyerPan; }
//     public void setBuyerPan(String buyerPan) { this.buyerPan = buyerPan; }
//     public String getBuyerEmail() { return buyerEmail; }
//     public void setBuyerEmail(String buyerEmail) { this.buyerEmail = buyerEmail; }
//     public String getBuyerContactName() { return buyerContactName; }
//     public void setBuyerContactName(String buyerContactName) { this.buyerContactName = buyerContactName; }
//     public String getBuyerContactPhone() { return buyerContactPhone; }
//     public void setBuyerContactPhone(String buyerContactPhone) { this.buyerContactPhone = buyerContactPhone; }
//     public Long getBuyerLocationId() { return buyerLocationId; }
//     public void setBuyerLocationId(Long buyerLocationId) { this.buyerLocationId = buyerLocationId; }
//     public String getBuyerLocationName() { return buyerLocationName; }
//     public void setBuyerLocationName(String buyerLocationName) { this.buyerLocationName = buyerLocationName; }
//     public String getBuyerLocationAddress() { return buyerLocationAddress; }
//     public void setBuyerLocationAddress(String buyerLocationAddress) { this.buyerLocationAddress = buyerLocationAddress; }
//     public Long getDeliveryLocationId() { return deliveryLocationId; }
//     public void setDeliveryLocationId(Long deliveryLocationId) { this.deliveryLocationId = deliveryLocationId; }
//     public String getDeliveryLocationName() { return deliveryLocationName; }
//     public void setDeliveryLocationName(String deliveryLocationName) { this.deliveryLocationName = deliveryLocationName; }
//     public String getDeliveryLocationAddress() { return deliveryLocationAddress; }
//     public void setDeliveryLocationAddress(String deliveryLocationAddress) { this.deliveryLocationAddress = deliveryLocationAddress; }
//     public Long getSupplierId() { return supplierId; }
//     public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }
//     public String getSupplierName() { return supplierName; }
//     public void setSupplierName(String supplierName) { this.supplierName = supplierName; }
//     public String getSupplierGstin() { return supplierGstin; }
//     public void setSupplierGstin(String supplierGstin) { this.supplierGstin = supplierGstin; }
//     public String getSupplierContactName() { return supplierContactName; }
//     public void setSupplierContactName(String supplierContactName) { this.supplierContactName = supplierContactName; }
//     public String getSupplierContactEmail() { return supplierContactEmail; }
//     public void setSupplierContactEmail(String supplierContactEmail) { this.supplierContactEmail = supplierContactEmail; }
//     public String getSupplierContactPhone() { return supplierContactPhone; }
//     public void setSupplierContactPhone(String supplierContactPhone) { this.supplierContactPhone = supplierContactPhone; }
//     public String getSupplierAddress() { return supplierAddress; }
//     public void setSupplierAddress(String supplierAddress) { this.supplierAddress = supplierAddress; }
//     public String getSupplierCity() { return supplierCity; }
//     public void setSupplierCity(String supplierCity) { this.supplierCity = supplierCity; }
//     public String getSupplierState() { return supplierState; }
//     public void setSupplierState(String supplierState) { this.supplierState = supplierState; }
//     public Long getRfqId() { return rfqId; }
//     public void setRfqId(Long rfqId) { this.rfqId = rfqId; }
//     public String getRfqNumber() { return rfqNumber; }
//     public void setRfqNumber(String rfqNumber) { this.rfqNumber = rfqNumber; }
//     public String getRfqTitle() { return rfqTitle; }
//     public void setRfqTitle(String rfqTitle) { this.rfqTitle = rfqTitle; }
//     public Long getCreatedByUserId() { return createdByUserId; }
//     public void setCreatedByUserId(Long createdByUserId) { this.createdByUserId = createdByUserId; }
//     public List<POLineItemDTO> getLineItems() { return lineItems; }
//     public void setLineItems(List<POLineItemDTO> lineItems) { this.lineItems = lineItems; }
//     public BigDecimal getSubtotal() { return subtotal; }
//     public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
//     public BigDecimal getTaxPercentage() { return taxPercentage; }
//     public void setTaxPercentage(BigDecimal taxPercentage) { this.taxPercentage = taxPercentage; }
//     public BigDecimal getTaxAmount() { return taxAmount; }
//     public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }
//     public BigDecimal getTotalTaxAmount() { return totalTaxAmount; }
//     public void setTotalTaxAmount(BigDecimal totalTaxAmount) { this.totalTaxAmount = totalTaxAmount; }
//     public BigDecimal getGrandTotal() { return grandTotal; }
//     public void setGrandTotal(BigDecimal grandTotal) { this.grandTotal = grandTotal; }
//     public String getAmountInWords() { return amountInWords; }
//     public void setAmountInWords(String amountInWords) { this.amountInWords = amountInWords; }

//     // ✅ NEW: Currency getters/setters
//     public String getCurrencyCode() { return currencyCode != null ? currencyCode : "INR"; }
//     public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }
//     public String getCurrencySymbol() { return currencySymbol != null ? currencySymbol : "₹"; }
//     public void setCurrencySymbol(String currencySymbol) { this.currencySymbol = currencySymbol; }

//     public String getPaymentTerms() { return paymentTerms; }
//     public void setPaymentTerms(String paymentTerms) { this.paymentTerms = paymentTerms; }
//     public String getDeliveryTerms() { return deliveryTerms; }
//     public void setDeliveryTerms(String deliveryTerms) { this.deliveryTerms = deliveryTerms; }
//     public String getOtherTerms() { return otherTerms; }
//     public void setOtherTerms(String otherTerms) { this.otherTerms = otherTerms; }
//     public String getDispatchedThrough() { return dispatchedThrough; }
//     public void setDispatchedThrough(String dispatchedThrough) { this.dispatchedThrough = dispatchedThrough; }
//     public String getModeOfPayment() { return modeOfPayment; }
//     public void setModeOfPayment(String modeOfPayment) { this.modeOfPayment = modeOfPayment; }
//     public String getDestination() { return destination; }
//     public void setDestination(String destination) { this.destination = destination; }
//     public String getBuyerRemarks() { return buyerRemarks; }
//     public void setBuyerRemarks(String buyerRemarks) { this.buyerRemarks = buyerRemarks; }
//     public String getInternalNotes() { return internalNotes; }
//     public void setInternalNotes(String internalNotes) { this.internalNotes = internalNotes; }
//     public POStatus getStatus() { return status; }
//     public void setStatus(POStatus status) { this.status = status; }
//     public Boolean getApprovalRequired() { return approvalRequired; }
//     public void setApprovalRequired(Boolean approvalRequired) { this.approvalRequired = approvalRequired; }
//     public ApprovalStatus getApprovalStatus() { return approvalStatus; }
//     public void setApprovalStatus(ApprovalStatus approvalStatus) { this.approvalStatus = approvalStatus; }
//     public Long getApprovedBy() { return approvedBy; }
//     public void setApprovedBy(Long approvedBy) { this.approvedBy = approvedBy; }
//     public LocalDateTime getApprovalDate() { return approvalDate; }
//     public void setApprovalDate(LocalDateTime approvalDate) { this.approvalDate = approvalDate; }
//     public String getApprovedByName() { return approvedByName; }
//     public void setApprovedByName(String approvedByName) { this.approvedByName = approvedByName; }
//     public String getApprovedByDesignation() { return approvedByDesignation; }
//     public void setApprovedByDesignation(String approvedByDesignation) { this.approvedByDesignation = approvedByDesignation; }
//     public LocalDateTime getCreatedAt() { return createdAt; }
//     public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
//     public LocalDateTime getUpdatedAt() { return updatedAt; }
//     public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
// }


package com.itti.leadcapturing.dto;

import com.itti.leadcapturing.model.POStatus;
import com.itti.leadcapturing.model.ApprovalStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class POResponseDTO {

    private Long id;
    private String poNumber;
    private LocalDateTime poDate;
    private String referenceQuoteNo;

    // Buyer Info
    private Long buyerId;
    private String buyerName;
    private String buyerGstin;
    private String buyerPan;
    private String buyerEmail;
    private String buyerContactName;
    private String buyerContactPhone;

    // ✅ Buyer logo — populated in convertToDTO() from buyer.logoData or buyer.logoUrl
    // Contains either a URL string, or a base64 data-URI: "data:image/png;base64,..."
    // Frontend <img [src]="po.buyerLogoUrl"> renders both formats natively
    private String buyerLogoUrl;

    // Buyer Location (Invoice To)
    private Long buyerLocationId;
    private String buyerLocationName;
    private String buyerLocationAddress;

    // Delivery Location (Ship To)
    private Long deliveryLocationId;
    private String deliveryLocationName;
    private String deliveryLocationAddress;

    // Supplier Info
    private Long supplierId;
    private String supplierName;
    private String supplierGstin;
    private String supplierContactName;
    private String supplierContactEmail;
    private String supplierContactPhone;
    private String supplierAddress;
    private String supplierCity;
    private String supplierState;

    // RFQ Reference
    private Long rfqId;
    private String rfqNumber;
    private String rfqTitle;

    private Long createdByUserId;

    // Line Items
    private List<POLineItemDTO> lineItems;

    // Financial
    private BigDecimal subtotal;
    private BigDecimal taxPercentage;
    private BigDecimal taxAmount;
    private BigDecimal totalTaxAmount;
    private BigDecimal grandTotal;
    private String amountInWords;

    // Currency
    private String currencyCode;
    private String currencySymbol;

    // Terms
    private String paymentTerms;
    private String deliveryTerms;
    private String otherTerms;
    private String dispatchedThrough;
    private String modeOfPayment;
    private String destination;

    // Remarks
    private String buyerRemarks;
    private String internalNotes;

    // Status
    private POStatus status;
    private Boolean approvalRequired;
    private ApprovalStatus approvalStatus;
    private Long approvedBy;
    private LocalDateTime approvalDate;
    private String approvedByName;
    private String approvedByDesignation;

    // Audit
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ==================== LINE ITEM INNER DTO ====================

    public static class POLineItemDTO {
        private Long id;
        private Integer slNo;
        private String itemCode;
        private String itemDescription;
        private String specifications;
        private String brandMakeModel;
        private BigDecimal quantity;
        private String uom;
        private BigDecimal unitRate;
        private BigDecimal lineTotal;
        private BigDecimal originalQuotedPrice;
        private BigDecimal discountPercentage;
        private BigDecimal taxPercentage;
        private BigDecimal taxAmount;
        private BigDecimal lineTotalWithTax;
        private Integer deliveryDays;
        private Integer warrantyMonths;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Integer getSlNo() { return slNo; }
        public void setSlNo(Integer slNo) { this.slNo = slNo; }
        public String getItemCode() { return itemCode; }
        public void setItemCode(String itemCode) { this.itemCode = itemCode; }
        public String getItemDescription() { return itemDescription; }
        public void setItemDescription(String v) { this.itemDescription = v; }
        public String getSpecifications() { return specifications; }
        public void setSpecifications(String v) { this.specifications = v; }
        public String getBrandMakeModel() { return brandMakeModel; }
        public void setBrandMakeModel(String v) { this.brandMakeModel = v; }
        public BigDecimal getQuantity() { return quantity; }
        public void setQuantity(BigDecimal v) { this.quantity = v; }
        public String getUom() { return uom; }
        public void setUom(String uom) { this.uom = uom; }
        public BigDecimal getUnitRate() { return unitRate; }
        public void setUnitRate(BigDecimal v) { this.unitRate = v; }
        public BigDecimal getLineTotal() { return lineTotal; }
        public void setLineTotal(BigDecimal v) { this.lineTotal = v; }
        public BigDecimal getOriginalQuotedPrice() { return originalQuotedPrice; }
        public void setOriginalQuotedPrice(BigDecimal v) { this.originalQuotedPrice = v; }
        public BigDecimal getDiscountPercentage() { return discountPercentage; }
        public void setDiscountPercentage(BigDecimal v) { this.discountPercentage = v; }
        public BigDecimal getTaxPercentage() { return taxPercentage; }
        public void setTaxPercentage(BigDecimal v) { this.taxPercentage = v; }
        public BigDecimal getTaxAmount() { return taxAmount; }
        public void setTaxAmount(BigDecimal v) { this.taxAmount = v; }
        public BigDecimal getLineTotalWithTax() { return lineTotalWithTax; }
        public void setLineTotalWithTax(BigDecimal v) { this.lineTotalWithTax = v; }
        public Integer getDeliveryDays() { return deliveryDays; }
        public void setDeliveryDays(Integer v) { this.deliveryDays = v; }
        public Integer getWarrantyMonths() { return warrantyMonths; }
        public void setWarrantyMonths(Integer v) { this.warrantyMonths = v; }
    }

    // ==================== GETTERS AND SETTERS ====================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPoNumber() { return poNumber; }
    public void setPoNumber(String poNumber) { this.poNumber = poNumber; }
    public LocalDateTime getPoDate() { return poDate; }
    public void setPoDate(LocalDateTime poDate) { this.poDate = poDate; }
    public String getReferenceQuoteNo() { return referenceQuoteNo; }
    public void setReferenceQuoteNo(String v) { this.referenceQuoteNo = v; }
    public Long getBuyerId() { return buyerId; }
    public void setBuyerId(Long buyerId) { this.buyerId = buyerId; }
    public String getBuyerName() { return buyerName; }
    public void setBuyerName(String buyerName) { this.buyerName = buyerName; }
    public String getBuyerGstin() { return buyerGstin; }
    public void setBuyerGstin(String buyerGstin) { this.buyerGstin = buyerGstin; }
    public String getBuyerPan() { return buyerPan; }
    public void setBuyerPan(String buyerPan) { this.buyerPan = buyerPan; }
    public String getBuyerEmail() { return buyerEmail; }
    public void setBuyerEmail(String buyerEmail) { this.buyerEmail = buyerEmail; }
    public String getBuyerContactName() { return buyerContactName; }
    public void setBuyerContactName(String v) { this.buyerContactName = v; }
    public String getBuyerContactPhone() { return buyerContactPhone; }
    public void setBuyerContactPhone(String v) { this.buyerContactPhone = v; }

    // ✅ Buyer logo getter/setter
    public String getBuyerLogoUrl() { return buyerLogoUrl; }
    public void setBuyerLogoUrl(String buyerLogoUrl) { this.buyerLogoUrl = buyerLogoUrl; }

    public Long getBuyerLocationId() { return buyerLocationId; }
    public void setBuyerLocationId(Long v) { this.buyerLocationId = v; }
    public String getBuyerLocationName() { return buyerLocationName; }
    public void setBuyerLocationName(String v) { this.buyerLocationName = v; }
    public String getBuyerLocationAddress() { return buyerLocationAddress; }
    public void setBuyerLocationAddress(String v) { this.buyerLocationAddress = v; }
    public Long getDeliveryLocationId() { return deliveryLocationId; }
    public void setDeliveryLocationId(Long v) { this.deliveryLocationId = v; }
    public String getDeliveryLocationName() { return deliveryLocationName; }
    public void setDeliveryLocationName(String v) { this.deliveryLocationName = v; }
    public String getDeliveryLocationAddress() { return deliveryLocationAddress; }
    public void setDeliveryLocationAddress(String v) { this.deliveryLocationAddress = v; }
    public Long getSupplierId() { return supplierId; }
    public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }
    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }
    public String getSupplierGstin() { return supplierGstin; }
    public void setSupplierGstin(String v) { this.supplierGstin = v; }
    public String getSupplierContactName() { return supplierContactName; }
    public void setSupplierContactName(String v) { this.supplierContactName = v; }
    public String getSupplierContactEmail() { return supplierContactEmail; }
    public void setSupplierContactEmail(String v) { this.supplierContactEmail = v; }
    public String getSupplierContactPhone() { return supplierContactPhone; }
    public void setSupplierContactPhone(String v) { this.supplierContactPhone = v; }
    public String getSupplierAddress() { return supplierAddress; }
    public void setSupplierAddress(String supplierAddress) { this.supplierAddress = supplierAddress; }
    public String getSupplierCity() { return supplierCity; }
    public void setSupplierCity(String supplierCity) { this.supplierCity = supplierCity; }
    public String getSupplierState() { return supplierState; }
    public void setSupplierState(String supplierState) { this.supplierState = supplierState; }
    public Long getRfqId() { return rfqId; }
    public void setRfqId(Long rfqId) { this.rfqId = rfqId; }
    public String getRfqNumber() { return rfqNumber; }
    public void setRfqNumber(String rfqNumber) { this.rfqNumber = rfqNumber; }
    public String getRfqTitle() { return rfqTitle; }
    public void setRfqTitle(String rfqTitle) { this.rfqTitle = rfqTitle; }
    public Long getCreatedByUserId() { return createdByUserId; }
    public void setCreatedByUserId(Long v) { this.createdByUserId = v; }
    public List<POLineItemDTO> getLineItems() { return lineItems; }
    public void setLineItems(List<POLineItemDTO> lineItems) { this.lineItems = lineItems; }
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    public BigDecimal getTaxPercentage() { return taxPercentage; }
    public void setTaxPercentage(BigDecimal taxPercentage) { this.taxPercentage = taxPercentage; }
    public BigDecimal getTaxAmount() { return taxAmount; }
    public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }
    public BigDecimal getTotalTaxAmount() { return totalTaxAmount; }
    public void setTotalTaxAmount(BigDecimal totalTaxAmount) { this.totalTaxAmount = totalTaxAmount; }
    public BigDecimal getGrandTotal() { return grandTotal; }
    public void setGrandTotal(BigDecimal grandTotal) { this.grandTotal = grandTotal; }
    public String getAmountInWords() { return amountInWords; }
    public void setAmountInWords(String amountInWords) { this.amountInWords = amountInWords; }
    public String getCurrencyCode() { return currencyCode != null ? currencyCode : "INR"; }
    public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }
    public String getCurrencySymbol() { return currencySymbol != null ? currencySymbol : "₹"; }
    public void setCurrencySymbol(String currencySymbol) { this.currencySymbol = currencySymbol; }
    public String getPaymentTerms() { return paymentTerms; }
    public void setPaymentTerms(String paymentTerms) { this.paymentTerms = paymentTerms; }
    public String getDeliveryTerms() { return deliveryTerms; }
    public void setDeliveryTerms(String deliveryTerms) { this.deliveryTerms = deliveryTerms; }
    public String getOtherTerms() { return otherTerms; }
    public void setOtherTerms(String otherTerms) { this.otherTerms = otherTerms; }
    public String getDispatchedThrough() { return dispatchedThrough; }
    public void setDispatchedThrough(String v) { this.dispatchedThrough = v; }
    public String getModeOfPayment() { return modeOfPayment; }
    public void setModeOfPayment(String modeOfPayment) { this.modeOfPayment = modeOfPayment; }
    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }
    public String getBuyerRemarks() { return buyerRemarks; }
    public void setBuyerRemarks(String buyerRemarks) { this.buyerRemarks = buyerRemarks; }
    public String getInternalNotes() { return internalNotes; }
    public void setInternalNotes(String internalNotes) { this.internalNotes = internalNotes; }
    public POStatus getStatus() { return status; }
    public void setStatus(POStatus status) { this.status = status; }
    public Boolean getApprovalRequired() { return approvalRequired; }
    public void setApprovalRequired(Boolean approvalRequired) { this.approvalRequired = approvalRequired; }
    public ApprovalStatus getApprovalStatus() { return approvalStatus; }
    public void setApprovalStatus(ApprovalStatus approvalStatus) { this.approvalStatus = approvalStatus; }
    public Long getApprovedBy() { return approvedBy; }
    public void setApprovedBy(Long approvedBy) { this.approvedBy = approvedBy; }
    public LocalDateTime getApprovalDate() { return approvalDate; }
    public void setApprovalDate(LocalDateTime approvalDate) { this.approvalDate = approvalDate; }
    public String getApprovedByName() { return approvedByName; }
    public void setApprovedByName(String approvedByName) { this.approvedByName = approvedByName; }
    public String getApprovedByDesignation() { return approvedByDesignation; }
    public void setApprovedByDesignation(String v) { this.approvedByDesignation = v; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}