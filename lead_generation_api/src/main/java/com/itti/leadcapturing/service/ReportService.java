package com.itti.leadcapturing.service;

import com.itti.leadcapturing.dto.POResponseDTO;
import com.itti.leadcapturing.model.Invoice;
import com.itti.leadcapturing.model.InvoiceLineItem;
import com.itti.leadcapturing.repo.InvoiceRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * ✅ Report Service — Professional Excel & PDF reports for PO and Invoice
 *
 * Colors match existing RFQ Summary Report style:
 *   Header bg : #1E4088  (dark navy blue)
 *   Label bg  : #F0F4FF  (light blue-grey)
 *   Table hdr : #1E4088
 *   Alt row   : #EEF2FF
 *   Accent    : #2ECC71 (green totals), #E74C3C (red discounts)
 */
@Service
public class ReportService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd-MMM-yyyy");

    @Autowired private PurchaseOrderService purchaseOrderService;
    @Autowired private InvoiceRepository invoiceRepository;

    // =========================================================================
    // PO EXCEL
    // =========================================================================

    public byte[] generatePOExcel(Long poId) throws Exception {
        POResponseDTO po = purchaseOrderService.getPOById(poId);

        XSSFWorkbook wb = new XSSFWorkbook();

        // Sheet 1: PO Overview
        buildPOOverviewSheet(wb, po);

        // Sheet 2: Line Items
        buildPOLineItemsSheet(wb, po);

        // Sheet 3: Financials Summary
        buildPOFinancialsSheet(wb, po);

        // Sheet 4: Approval & Terms
        buildPOTermsSheet(wb, po);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        wb.write(out);
        wb.close();
        return out.toByteArray();
    }

    private void buildPOOverviewSheet(XSSFWorkbook wb, POResponseDTO po) {
        XSSFSheet ws = wb.createSheet("PO Overview");
        ws.setColumnWidth(0, 7000); ws.setColumnWidth(1, 9000);
        ws.setColumnWidth(2, 7000); ws.setColumnWidth(3, 9000);

        int r = 0;

        // Title row
        XSSFRow titleRow = ws.createRow(r++);
        titleRow.setHeightInPoints(28);
        XSSFCell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("PURCHASE ORDER REPORT");
        titleCell.setCellStyle(titleStyle(wb));
        ws.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));

        // Subtitle
        XSSFRow subRow = ws.createRow(r++);
        subRow.setHeightInPoints(18);
        XSSFCell subCell = subRow.createCell(0);
        subCell.setCellValue("PO: " + safe(po.getPoNumber()) + "   |   Generated: " + java.time.LocalDate.now().format(DATE_FMT));
        subCell.setCellStyle(subtitleStyle(wb));
        ws.addMergedRegion(new CellRangeAddress(1, 1, 0, 3));

        r = addBlankRow(ws, r);

        // Section: PO Information
        r = addSectionHeader(ws, wb, r, "PO INFORMATION", 4);
        r = addInfoRow(ws, wb, r, "PO Number", safe(po.getPoNumber()), "PO Date", safe(po.getPoDate() != null ? po.getPoDate().format(DATE_FMT) : "—"));
        r = addInfoRow(ws, wb, r, "Status", safe(po.getStatus() != null ? po.getStatus().name() : "—"), "Approval Status", safe(po.getApprovalStatus() != null ? po.getApprovalStatus().name() : "—"));
        r = addInfoRow(ws, wb, r, "RFQ Number", safe(po.getRfqNumber()), "RFQ Title", safe(po.getRfqTitle()));
        r = addInfoRow(ws, wb, r, "Quote Reference", safe(po.getReferenceQuoteNo()), "Currency", safe(po.getCurrencyCode()) + " (" + safe(po.getCurrencySymbol()) + ")");
        r = addInfoRow(ws, wb, r, "Created At", safe(po.getCreatedAt() != null ? po.getCreatedAt().format(DATE_FMT) : "—"), "Approved By", safe(po.getApprovedByName()));
        r = addInfoRow(ws, wb, r, "Approved Designation", safe(po.getApprovedByDesignation()), "Approval Date", safe(po.getApprovalDate() != null ? po.getApprovalDate().format(DATE_FMT) : "—"));

        r = addBlankRow(ws, r);

        // Section: Buyer Information
        r = addSectionHeader(ws, wb, r, "BUYER INFORMATION", 4);
        r = addInfoRow(ws, wb, r, "Buyer Name", safe(po.getBuyerName()), "GSTIN", safe(po.getBuyerGstin()));
        r = addInfoRow(ws, wb, r, "PAN", safe(po.getBuyerPan()), "Email", safe(po.getBuyerEmail()));
        r = addInfoRow(ws, wb, r, "Contact Name", safe(po.getBuyerContactName()), "Contact Phone", safe(po.getBuyerContactPhone()));
        r = addInfoRow(ws, wb, r, "Invoice Address", safe(po.getBuyerLocationName()), "Location Address", safe(po.getBuyerLocationAddress()));
        r = addInfoRow(ws, wb, r, "Shipping Name", safe(po.getDeliveryLocationName()), "Shipping Address", safe(po.getDeliveryLocationAddress()));

        r = addBlankRow(ws, r);

        // Section: Supplier Information
        r = addSectionHeader(ws, wb, r, "SUPPLIER INFORMATION", 4);
        r = addInfoRow(ws, wb, r, "Supplier Name", safe(po.getSupplierName()), "GSTIN", safe(po.getSupplierGstin()));
        r = addInfoRow(ws, wb, r, "Contact Name", safe(po.getSupplierContactName()), "Email", safe(po.getSupplierContactEmail()));
        r = addInfoRow(ws, wb, r, "Phone", safe(po.getSupplierContactPhone()), "City / State", safe(po.getSupplierCity()) + (po.getSupplierState() != null ? ", " + po.getSupplierState() : ""));
        r = addInfoRow(ws, wb, r, "Address", safe(po.getSupplierAddress()), "", "");
    }

    private void buildPOLineItemsSheet(XSSFWorkbook wb, POResponseDTO po) {
        XSSFSheet ws = wb.createSheet("Line Items");

        int[] colWidths = {1500, 3000, 14000, 8000, 5000, 3500, 3500, 4500, 4000, 4500, 4000, 4000};
        String[] headers = {"#", "Item Code", "Description / Specs", "Brand / Make", "UOM", "Qty",
                "Unit Rate", "Discount %", "Tax %", "Tax Amt", "Pre-Tax Total", "Total (with Tax)"};

        int r = 0;

        // Title
        XSSFRow titleRow = ws.createRow(r++);
        titleRow.setHeightInPoints(24);
        XSSFCell tc = titleRow.createCell(0);
        tc.setCellValue("LINE ITEMS — " + safe(po.getPoNumber()));
        tc.setCellStyle(titleStyle(wb));
        ws.addMergedRegion(new CellRangeAddress(0, 0, 0, headers.length - 1));

        XSSFRow subRow = ws.createRow(r++);
        XSSFCell sc = subRow.createCell(0);
        sc.setCellValue(safe(po.getSupplierName()) + "   |   " + (po.getLineItems() != null ? po.getLineItems().size() : 0) + " items");
        sc.setCellStyle(subtitleStyle(wb));
        ws.addMergedRegion(new CellRangeAddress(1, 1, 0, headers.length - 1));

        r = addBlankRow(ws, r);

        for (int i = 0; i < colWidths.length; i++) ws.setColumnWidth(i, colWidths[i] * 2);

        // Headers
        XSSFRow hRow = ws.createRow(r++);
        hRow.setHeightInPoints(18);
        for (int i = 0; i < headers.length; i++) {
            XSSFCell cell = hRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(tableHeaderStyle(wb));
        }

        // Data rows
        List<POResponseDTO.POLineItemDTO> items = po.getLineItems();
        if (items != null) {
            for (int idx = 0; idx < items.size(); idx++) {
                POResponseDTO.POLineItemDTO item = items.get(idx);
                XSSFRow row = ws.createRow(r++);
                row.setHeightInPoints(18);
                boolean alt = idx % 2 == 1;

                setCell(row, 0, String.valueOf(idx + 1), altStyle(wb, alt, false));
                setCell(row, 1, safe(item.getItemCode()), altStyle(wb, alt, false));
                setCell(row, 2, buildItemDesc(item), altStyle(wb, alt, false));
                setCell(row, 3, safe(item.getBrandMakeModel()), altStyle(wb, alt, false));
                setCell(row, 4, safe(item.getUom()), altStyle(wb, alt, false));
                setCellNum(row, 5, item.getQuantity(), altStyle(wb, alt, false));
                setCellNum(row, 6, item.getUnitRate(), altNumStyle(wb, alt));
                setCellNum(row, 7, item.getDiscountPercentage(), altStyle(wb, alt, false));
                setCellNum(row, 8, item.getTaxPercentage(), altStyle(wb, alt, false));
                setCellNum(row, 9, item.getTaxAmount(), altNumStyle(wb, alt));
                setCellNum(row, 10, item.getLineTotal(), altNumStyle(wb, alt));
                setCellNum(row, 11, item.getLineTotalWithTax(), altNumStyle(wb, alt));
            }
        }

        r = addBlankRow(ws, r);

        // Totals footer
        XSSFRow subRow2 = ws.createRow(r++);
        XSSFCell lbl1 = subRow2.createCell(9); lbl1.setCellValue("Subtotal (Pre-Tax)"); lbl1.setCellStyle(totalLabelStyle(wb));
        XSSFCell val1 = subRow2.createCell(10); val1.setCellValue(po.getSubtotal() != null ? po.getSubtotal().doubleValue() : 0); val1.setCellStyle(totalValueStyle(wb));
        ws.addMergedRegion(new CellRangeAddress(r - 1, r - 1, 0, 8));

        XSSFRow taxRow = ws.createRow(r++);
        XSSFCell lbl2 = taxRow.createCell(9); lbl2.setCellValue("Total Tax Amount"); lbl2.setCellStyle(totalLabelStyle(wb));
        XSSFCell val2 = taxRow.createCell(10); val2.setCellValue(po.getTaxAmount() != null ? po.getTaxAmount().doubleValue() : 0); val2.setCellStyle(totalValueStyle(wb));
        ws.addMergedRegion(new CellRangeAddress(r - 1, r - 1, 0, 8));

        XSSFRow gtRow = ws.createRow(r++);
        gtRow.setHeightInPoints(22);
        XSSFCell gtLbl = gtRow.createCell(9); gtLbl.setCellValue("GRAND TOTAL (" + safe(po.getCurrencyCode()) + ")"); gtLbl.setCellStyle(grandTotalLabelStyle(wb));
        XSSFCell gtVal = gtRow.createCell(10); gtVal.setCellValue(po.getGrandTotal() != null ? po.getGrandTotal().doubleValue() : 0); gtVal.setCellStyle(grandTotalValueStyle(wb));
        ws.addMergedRegion(new CellRangeAddress(r - 1, r - 1, 0, 8));

        r = addBlankRow(ws, r);
        XSSFRow wordsRow = ws.createRow(r++);
        XSSFCell wordsCell = wordsRow.createCell(0);
        wordsCell.setCellValue("Amount in Words: " + safe(po.getAmountInWords()));
        wordsCell.setCellStyle(infoLabelStyle(wb));
        ws.addMergedRegion(new CellRangeAddress(r - 1, r - 1, 0, headers.length - 1));
    }

    private void buildPOFinancialsSheet(XSSFWorkbook wb, POResponseDTO po) {
        XSSFSheet ws = wb.createSheet("Financial Summary");
        ws.setColumnWidth(0, 8000); ws.setColumnWidth(1, 7000);
        ws.setColumnWidth(2, 6000); ws.setColumnWidth(3, 5000);

        int r = 0;
        XSSFRow titleRow = ws.createRow(r++); titleRow.setHeightInPoints(24);
        XSSFCell tc = titleRow.createCell(0);
        tc.setCellValue("FINANCIAL SUMMARY — " + safe(po.getPoNumber()));
        tc.setCellStyle(titleStyle(wb));
        ws.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));

        r = addBlankRow(ws, r);
        r = addSectionHeader(ws, wb, r, "FINANCIAL BREAKDOWN", 4);

        String sym = safe(po.getCurrencySymbol()) + " ";
        r = addInfoRow(ws, wb, r, "Currency", safe(po.getCurrencyCode()), "Symbol", safe(po.getCurrencySymbol()));
        r = addInfoRow(ws, wb, r, "Subtotal (Pre-Tax)", sym + fmt(po.getSubtotal()), "Tax %", fmt2(po.getTaxPercentage()) + "%");
        r = addInfoRow(ws, wb, r, "Tax Amount", sym + fmt(po.getTaxAmount()), "Total Tax Amount", sym + fmt(po.getTotalTaxAmount()));
        r = addInfoRow(ws, wb, r, "Grand Total", sym + fmt(po.getGrandTotal()), "Amount in Words", safe(po.getAmountInWords()));

        r = addBlankRow(ws, r);
        r = addSectionHeader(ws, wb, r, "LINE ITEM FINANCIAL SUMMARY", 4);

        List<POResponseDTO.POLineItemDTO> items = po.getLineItems();
        if (items != null) {
            boolean hasDiscount = items.stream().anyMatch(i -> i.getDiscountPercentage() != null && i.getDiscountPercentage().compareTo(BigDecimal.ZERO) > 0);
            boolean hasTax = items.stream().anyMatch(i -> i.getTaxPercentage() != null && i.getTaxPercentage().compareTo(BigDecimal.ZERO) > 0);

            if (hasDiscount) {
                r = addInfoRow(ws, wb, r, "Total Discount Applied", "Yes (see Line Items sheet)", "Items with Discount", String.valueOf(items.stream().filter(i -> i.getDiscountPercentage() != null && i.getDiscountPercentage().compareTo(BigDecimal.ZERO) > 0).count()));
            }
            if (hasTax) {
                r = addInfoRow(ws, wb, r, "Tax Applied", "Yes (per line item)", "Items with Tax", String.valueOf(items.stream().filter(i -> i.getTaxPercentage() != null && i.getTaxPercentage().compareTo(BigDecimal.ZERO) > 0).count()));
            }
        }
    }

    private void buildPOTermsSheet(XSSFWorkbook wb, POResponseDTO po) {
        XSSFSheet ws = wb.createSheet("Terms & Approval");
        ws.setColumnWidth(0, 7000); ws.setColumnWidth(1, 14000);

        int r = 0;
        XSSFRow titleRow = ws.createRow(r++); titleRow.setHeightInPoints(24);
        XSSFCell tc = titleRow.createCell(0);
        tc.setCellValue("TERMS & CONDITIONS — " + safe(po.getPoNumber()));
        tc.setCellStyle(titleStyle(wb));
        ws.addMergedRegion(new CellRangeAddress(0, 0, 0, 1));

        r = addBlankRow(ws, r);
        r = addSectionHeader(ws, wb, r, "COMMERCIAL TERMS", 2);

        r = addKVRow(ws, wb, r, "Payment Terms", safe(po.getPaymentTerms()));
        r = addKVRow(ws, wb, r, "Delivery Terms", safe(po.getDeliveryTerms()));
        r = addKVRow(ws, wb, r, "Other Terms", safe(po.getOtherTerms()));
        r = addKVRow(ws, wb, r, "Dispatched Through", safe(po.getDispatchedThrough()));
        r = addKVRow(ws, wb, r, "Mode of Payment", safe(po.getModeOfPayment()));
        r = addKVRow(ws, wb, r, "Destination", safe(po.getDestination()));
        r = addKVRow(ws, wb, r, "Buyer Remarks", safe(po.getBuyerRemarks()));
        r = addKVRow(ws, wb, r, "Internal Notes", safe(po.getInternalNotes()));

        r = addBlankRow(ws, r);
        r = addSectionHeader(ws, wb, r, "APPROVAL DETAILS", 2);
        r = addKVRow(ws, wb, r, "Approval Required", po.getApprovalRequired() != null && po.getApprovalRequired() ? "Yes" : "No");
        r = addKVRow(ws, wb, r, "Approval Status", safe(po.getApprovalStatus() != null ? po.getApprovalStatus().name() : "—"));
        r = addKVRow(ws, wb, r, "Approved By", safe(po.getApprovedByName()));
        r = addKVRow(ws, wb, r, "Designation", safe(po.getApprovedByDesignation()));
        r = addKVRow(ws, wb, r, "Approval Date", safe(po.getApprovalDate() != null ? po.getApprovalDate().format(DATE_FMT) : "—"));
    }

    // =========================================================================
    // INVOICE EXCEL
    // =========================================================================

    public byte[] generateInvoiceExcel(Long invoiceId) throws Exception {
        Invoice inv = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found: " + invoiceId));

        XSSFWorkbook wb = new XSSFWorkbook();

        buildInvoiceOverviewSheet(wb, inv);
        buildInvoiceLineItemsSheet(wb, inv);
        buildInvoiceFinancialsSheet(wb, inv);
        buildInvoiceTrackingSheet(wb, inv);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        wb.write(out);
        wb.close();
        return out.toByteArray();
    }

    private void buildInvoiceOverviewSheet(XSSFWorkbook wb, Invoice inv) {
        XSSFSheet ws = wb.createSheet("Invoice Overview");
        ws.setColumnWidth(0, 7000); ws.setColumnWidth(1, 9000);
        ws.setColumnWidth(2, 7000); ws.setColumnWidth(3, 9000);

        int r = 0;
        XSSFRow titleRow = ws.createRow(r++); titleRow.setHeightInPoints(28);
        XSSFCell tc = titleRow.createCell(0);
        tc.setCellValue("INVOICE REPORT");
        tc.setCellStyle(titleStyle(wb));
        ws.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));

        XSSFRow subRow = ws.createRow(r++); subRow.setHeightInPoints(18);
        XSSFCell sc = subRow.createCell(0);
        sc.setCellValue("Invoice: " + safe(inv.getInvoiceNumber()) + "   |   Generated: " + java.time.LocalDate.now().format(DATE_FMT));
        sc.setCellStyle(subtitleStyle(wb));
        ws.addMergedRegion(new CellRangeAddress(1, 1, 0, 3));

        r = addBlankRow(ws, r);
        r = addSectionHeader(ws, wb, r, "INVOICE INFORMATION", 4);
        r = addInfoRow(ws, wb, r, "Invoice Number", safe(inv.getInvoiceNumber()), "Status", safe(inv.getStatus() != null ? inv.getStatus().name() : "—"));
        r = addInfoRow(ws, wb, r, "Invoice Date", safe(inv.getInvoiceDate() != null ? inv.getInvoiceDate().format(DATE_FMT) : "—"), "Due Date", safe(inv.getDueDate() != null ? inv.getDueDate().format(DATE_FMT) : "—"));
        r = addInfoRow(ws, wb, r, "PO Number", safe(inv.getPoNumber()), "RFQ Number", safe(inv.getRfqNumber()));
        r = addInfoRow(ws, wb, r, "Currency", safe(inv.getCurrency()), "Resubmit Count", String.valueOf(inv.getResubmitCount() != null ? inv.getResubmitCount() : 0));

        r = addBlankRow(ws, r);
        r = addSectionHeader(ws, wb, r, "SUPPLIER INFORMATION", 4);
        r = addInfoRow(ws, wb, r, "Supplier Name", safe(inv.getSupplierName()), "Email", safe(inv.getSupplierEmail()));

        r = addBlankRow(ws, r);
        r = addSectionHeader(ws, wb, r, "BUYER INFORMATION", 4);
        r = addInfoRow(ws, wb, r, "Buyer Company", safe(inv.getBuyerCompanyName()), "RFQ Creator", safe(inv.getRfqCreatorName()));
        r = addInfoRow(ws, wb, r, "RFQ Creator Email", safe(inv.getRfqCreatorEmail()), "", "");

        r = addBlankRow(ws, r);
        r = addSectionHeader(ws, wb, r, "BANK DETAILS", 4);
        r = addInfoRow(ws, wb, r, "Bank Name", safe(inv.getBankName()), "Account Holder", safe(inv.getBankAccountName()));
        r = addInfoRow(ws, wb, r, "Account Number", safe(inv.getBankAccountNumber()), "IFSC Code", safe(inv.getBankIfscCode()));
        r = addInfoRow(ws, wb, r, "SWIFT Code", safe(inv.getBankSwiftCode()), "", "");
    }

    private void buildInvoiceLineItemsSheet(XSSFWorkbook wb, Invoice inv) {
        XSSFSheet ws = wb.createSheet("Line Items");

        int[] colWidths = {1500, 3000, 14000, 5000, 3500, 5000, 4000, 4000, 4500, 5000};
        String[] headers = {"#", "HSN/SAC", "Description", "UOM", "Qty", "Unit Price",
                "Discount %", "Tax %", "Tax Amt", "Line Total"};

        int r = 0;
        XSSFRow titleRow = ws.createRow(r++); titleRow.setHeightInPoints(24);
        XSSFCell tc = titleRow.createCell(0);
        tc.setCellValue("LINE ITEMS — " + safe(inv.getInvoiceNumber()));
        tc.setCellStyle(titleStyle(wb));
        ws.addMergedRegion(new CellRangeAddress(0, 0, 0, headers.length - 1));

        XSSFRow subRow = ws.createRow(r++);
        XSSFCell sc = subRow.createCell(0);
        sc.setCellValue(safe(inv.getSupplierName()) + "   |   " + (inv.getLineItems() != null ? inv.getLineItems().size() : 0) + " items");
        sc.setCellStyle(subtitleStyle(wb));
        ws.addMergedRegion(new CellRangeAddress(1, 1, 0, headers.length - 1));

        r = addBlankRow(ws, r);

        for (int i = 0; i < colWidths.length; i++) ws.setColumnWidth(i, colWidths[i] * 2);

        XSSFRow hRow = ws.createRow(r++); hRow.setHeightInPoints(18);
        for (int i = 0; i < headers.length; i++) {
            XSSFCell cell = hRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(tableHeaderStyle(wb));
        }

        List<InvoiceLineItem> items = inv.getLineItems();
        if (items != null) {
            for (int idx = 0; idx < items.size(); idx++) {
                InvoiceLineItem item = items.get(idx);
                XSSFRow row = ws.createRow(r++); row.setHeightInPoints(18);
                boolean alt = idx % 2 == 1;

                setCell(row, 0, String.valueOf(idx + 1), altStyle(wb, alt, false));
                setCell(row, 1, safe(item.getHsnSacCode()), altStyle(wb, alt, false));
                String desc = safe(item.getItemDescription());
                if (item.getItemDescriptionDetailed() != null && !item.getItemDescriptionDetailed().isBlank())
                    desc += "\n" + item.getItemDescriptionDetailed();
                if (item.getSpecifications() != null && !item.getSpecifications().isBlank())
                    desc += "\n" + item.getSpecifications();
                setCell(row, 2, desc, altStyle(wb, alt, false));
                setCell(row, 3, safe(item.getUom()), altStyle(wb, alt, false));
                setCellNum(row, 4, item.getQuantity(), altStyle(wb, alt, false));
                setCellNum(row, 5, item.getUnitPrice(), altNumStyle(wb, alt));
                setCellNum(row, 6, item.getDiscountPercentage(), altStyle(wb, alt, false));
                setCellNum(row, 7, item.getTaxPercentage(), altStyle(wb, alt, false));
                setCellNum(row, 8, item.getTaxAmount(), altNumStyle(wb, alt));
                setCellNum(row, 9, item.getLineTotal(), altNumStyle(wb, alt));
            }
        }

        r = addBlankRow(ws, r);

        String cur = safe(inv.getCurrency());
        XSSFRow s1 = ws.createRow(r++);
        XSSFCell l1 = s1.createCell(8); l1.setCellValue("Subtotal"); l1.setCellStyle(totalLabelStyle(wb));
        XSSFCell v1 = s1.createCell(9); v1.setCellValue(inv.getSubtotal() != null ? inv.getSubtotal().doubleValue() : 0); v1.setCellStyle(totalValueStyle(wb));
        ws.addMergedRegion(new CellRangeAddress(r - 1, r - 1, 0, 7));

        XSSFRow s2 = ws.createRow(r++);
        XSSFCell l2 = s2.createCell(8); l2.setCellValue("Tax Amount"); l2.setCellStyle(totalLabelStyle(wb));
        XSSFCell v2 = s2.createCell(9); v2.setCellValue(inv.getTaxAmount() != null ? inv.getTaxAmount().doubleValue() : 0); v2.setCellStyle(totalValueStyle(wb));
        ws.addMergedRegion(new CellRangeAddress(r - 1, r - 1, 0, 7));

        XSSFRow s3 = ws.createRow(r++); s3.setHeightInPoints(22);
        XSSFCell l3 = s3.createCell(8); l3.setCellValue("TOTAL AMOUNT (" + cur + ")"); l3.setCellStyle(grandTotalLabelStyle(wb));
        XSSFCell v3 = s3.createCell(9); v3.setCellValue(inv.getTotalAmount() != null ? inv.getTotalAmount().doubleValue() : 0); v3.setCellStyle(grandTotalValueStyle(wb));
        ws.addMergedRegion(new CellRangeAddress(r - 1, r - 1, 0, 7));
    }

    private void buildInvoiceFinancialsSheet(XSSFWorkbook wb, Invoice inv) {
        XSSFSheet ws = wb.createSheet("Financial Summary");
        ws.setColumnWidth(0, 8000); ws.setColumnWidth(1, 7000);
        ws.setColumnWidth(2, 6000); ws.setColumnWidth(3, 5000);

        int r = 0;
        XSSFRow titleRow = ws.createRow(r++); titleRow.setHeightInPoints(24);
        XSSFCell tc = titleRow.createCell(0);
        tc.setCellValue("FINANCIAL SUMMARY — " + safe(inv.getInvoiceNumber()));
        tc.setCellStyle(titleStyle(wb));
        ws.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));

        r = addBlankRow(ws, r);
        r = addSectionHeader(ws, wb, r, "FINANCIAL BREAKDOWN", 4);

        BigDecimal subtotal    = inv.getSubtotal()    != null ? inv.getSubtotal()    : BigDecimal.ZERO;
        BigDecimal taxAmount   = inv.getTaxAmount()   != null ? inv.getTaxAmount()   : BigDecimal.ZERO;
        BigDecimal totalAmount = inv.getTotalAmount() != null ? inv.getTotalAmount() : BigDecimal.ZERO;
        BigDecimal gross = subtotal.add(taxAmount);
        BigDecimal overallDiscount = gross.subtract(totalAmount);
        if (overallDiscount.compareTo(new BigDecimal("0.005")) < 0) overallDiscount = BigDecimal.ZERO;

        String cur = safe(inv.getCurrency());
        r = addInfoRow(ws, wb, r, "Currency", cur, "Supplier Name", safe(inv.getSupplierName()));
        r = addInfoRow(ws, wb, r, "Subtotal (Pre-Tax)", fmt(subtotal), "Tax Amount", fmt(taxAmount));
        r = addInfoRow(ws, wb, r, "Overall Discount", fmt(overallDiscount), "Net Payable", fmt(totalAmount));
        r = addInfoRow(ws, wb, r, "Grand Total (" + cur + ")", fmt(totalAmount), "Payment Terms", safe(inv.getPaymentTerms()));
    }

    private void buildInvoiceTrackingSheet(XSSFWorkbook wb, Invoice inv) {
        XSSFSheet ws = wb.createSheet("Status & Tracking");
        ws.setColumnWidth(0, 7000); ws.setColumnWidth(1, 14000);

        int r = 0;
        XSSFRow titleRow = ws.createRow(r++); titleRow.setHeightInPoints(24);
        XSSFCell tc = titleRow.createCell(0);
        tc.setCellValue("STATUS & TRACKING — " + safe(inv.getInvoiceNumber()));
        tc.setCellStyle(titleStyle(wb));
        ws.addMergedRegion(new CellRangeAddress(0, 0, 0, 1));

        r = addBlankRow(ws, r);
        r = addSectionHeader(ws, wb, r, "INVOICE STATUS", 2);
        r = addKVRow(ws, wb, r, "Current Status", safe(inv.getStatus() != null ? inv.getStatus().name() : "—"));
        r = addKVRow(ws, wb, r, "Invoice Number", safe(inv.getInvoiceNumber()));
        r = addKVRow(ws, wb, r, "Invoice Date", safe(inv.getInvoiceDate() != null ? inv.getInvoiceDate().format(DATE_FMT) : "—"));
        r = addKVRow(ws, wb, r, "Due Date", safe(inv.getDueDate() != null ? inv.getDueDate().format(DATE_FMT) : "—"));

        r = addBlankRow(ws, r);
        r = addSectionHeader(ws, wb, r, "APPROVAL / REJECTION", 2);
        r = addKVRow(ws, wb, r, "Action By", safe(inv.getApprovedRejectedBy()));
        r = addKVRow(ws, wb, r, "Action Date", safe(inv.getApprovedRejectedAt() != null ? inv.getApprovedRejectedAt().format(DATE_FMT) : "—"));
        r = addKVRow(ws, wb, r, "Remarks", safe(inv.getApprovalRemarks()));

        r = addBlankRow(ws, r);
        r = addSectionHeader(ws, wb, r, "RESUBMISSION TRACKING", 2);
        r = addKVRow(ws, wb, r, "Resubmit Count", String.valueOf(inv.getResubmitCount() != null ? inv.getResubmitCount() : 0));
        r = addKVRow(ws, wb, r, "Resubmit Remarks", safe(inv.getResubmitRemarks()));
        r = addKVRow(ws, wb, r, "Resubmitted At", safe(inv.getResubmittedAt() != null ? inv.getResubmittedAt().format(DATE_FMT) : "—"));

        r = addBlankRow(ws, r);
        r = addSectionHeader(ws, wb, r, "PAYMENT", 2);
        r = addKVRow(ws, wb, r, "Paid By", safe(inv.getPaidBy()));
        r = addKVRow(ws, wb, r, "Payment Reference", safe(inv.getPaymentReference()));
        r = addKVRow(ws, wb, r, "Paid At", safe(inv.getPaidAt() != null ? inv.getPaidAt().format(DATE_FMT) : "—"));

        r = addBlankRow(ws, r);
        r = addSectionHeader(ws, wb, r, "NOTES", 2);
        r = addKVRow(ws, wb, r, "Notes", safe(inv.getNotes()));
        r = addKVRow(ws, wb, r, "Terms & Conditions", safe(inv.getTermsAndConditions()));
    }

    // =========================================================================
    // PO PDF
    // =========================================================================

    public byte[] generatePOPdf(Long poId) throws Exception {
        POResponseDTO po = purchaseOrderService.getPOById(poId);
        return buildPOPdf(po);
    }

    private byte[] buildPOPdf(POResponseDTO po) throws Exception {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            com.itextpdf.text.Document doc = new com.itextpdf.text.Document(com.itextpdf.text.PageSize.A4, 36, 36, 54, 36);
            com.itextpdf.text.pdf.PdfWriter writer = com.itextpdf.text.pdf.PdfWriter.getInstance(doc, out);
            writer.setPageEvent(new POPdfPageEvents(po));
            doc.open();

            addPOContent(doc, po);

            doc.close();
            return out.toByteArray();
        }
    }

    private void addPOContent(com.itextpdf.text.Document doc, POResponseDTO po) throws Exception {
        com.itextpdf.text.Font titleFont   = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 18, com.itextpdf.text.Font.BOLD, com.itextpdf.text.BaseColor.WHITE);
        com.itextpdf.text.Font headFont    = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 10, com.itextpdf.text.Font.BOLD, com.itextpdf.text.BaseColor.WHITE);
        com.itextpdf.text.Font labelFont   = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 9,  com.itextpdf.text.Font.BOLD,   new com.itextpdf.text.BaseColor(30, 64, 136));
        com.itextpdf.text.Font valueFont   = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 9,  com.itextpdf.text.Font.NORMAL, com.itextpdf.text.BaseColor.DARK_GRAY);
        com.itextpdf.text.Font colHdrFont  = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 8,  com.itextpdf.text.Font.BOLD,   com.itextpdf.text.BaseColor.WHITE);
        com.itextpdf.text.Font cellFont    = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 8,  com.itextpdf.text.Font.NORMAL, com.itextpdf.text.BaseColor.DARK_GRAY);
        com.itextpdf.text.Font totalFont   = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 9,  com.itextpdf.text.Font.BOLD,   new com.itextpdf.text.BaseColor(30, 64, 136));
        com.itextpdf.text.Font gtFont      = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 10, com.itextpdf.text.Font.BOLD,   com.itextpdf.text.BaseColor.WHITE);

        com.itextpdf.text.BaseColor NAVY    = new com.itextpdf.text.BaseColor(30, 64, 136);
        com.itextpdf.text.BaseColor LBLUE   = new com.itextpdf.text.BaseColor(240, 244, 255);
        com.itextpdf.text.BaseColor ALTROW  = new com.itextpdf.text.BaseColor(238, 242, 255);
        com.itextpdf.text.BaseColor GREEN   = new com.itextpdf.text.BaseColor(39, 174, 96);

        // ── Header Banner ──
        com.itextpdf.text.pdf.PdfPTable banner = new com.itextpdf.text.pdf.PdfPTable(1);
        banner.setWidthPercentage(100);
        com.itextpdf.text.pdf.PdfPCell bannerCell = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase("PURCHASE ORDER", titleFont));
        bannerCell.setBackgroundColor(NAVY); bannerCell.setPadding(12); bannerCell.setBorder(0);
        bannerCell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
        banner.addCell(bannerCell);
        doc.add(banner);
        doc.add(new com.itextpdf.text.Paragraph(" "));

        // ── PO Meta 2-col ──
        com.itextpdf.text.pdf.PdfPTable meta = new com.itextpdf.text.pdf.PdfPTable(new float[]{1, 1});
        meta.setWidthPercentage(100);

        // Left: Supplier
        com.itextpdf.text.pdf.PdfPTable suppTable = new com.itextpdf.text.pdf.PdfPTable(1);
        suppTable.setWidthPercentage(100);
        addPdfSectionHeader(suppTable, "VENDOR DETAILS", NAVY, headFont);
        addPdfKV(suppTable, "Company", safe(po.getSupplierName()), labelFont, valueFont, LBLUE, com.itextpdf.text.BaseColor.WHITE);
        addPdfKV(suppTable, "Address", safe(po.getSupplierAddress()), labelFont, valueFont, com.itextpdf.text.BaseColor.WHITE, com.itextpdf.text.BaseColor.WHITE);
        addPdfKV(suppTable, "City / State", safe(po.getSupplierCity()) + (po.getSupplierState() != null ? ", " + po.getSupplierState() : ""), labelFont, valueFont, LBLUE, LBLUE);
        addPdfKV(suppTable, "Contact", safe(po.getSupplierContactName()), labelFont, valueFont, com.itextpdf.text.BaseColor.WHITE, com.itextpdf.text.BaseColor.WHITE);
        addPdfKV(suppTable, "Phone", safe(po.getSupplierContactPhone()), labelFont, valueFont, LBLUE, LBLUE);
        addPdfKV(suppTable, "GSTIN", safe(po.getSupplierGstin()), labelFont, valueFont, com.itextpdf.text.BaseColor.WHITE, com.itextpdf.text.BaseColor.WHITE);

        com.itextpdf.text.pdf.PdfPCell leftCell = new com.itextpdf.text.pdf.PdfPCell(suppTable);
        leftCell.setBorder(0); leftCell.setPadding(4);
        meta.addCell(leftCell);

        // Right: PO Info
        com.itextpdf.text.pdf.PdfPTable poInfoTable = new com.itextpdf.text.pdf.PdfPTable(1);
        poInfoTable.setWidthPercentage(100);
        addPdfSectionHeader(poInfoTable, "ORDER DETAILS", NAVY, headFont);
        addPdfKV(poInfoTable, "PO Number", safe(po.getPoNumber()), labelFont, valueFont, LBLUE, com.itextpdf.text.BaseColor.WHITE);
        addPdfKV(poInfoTable, "PO Date", safe(po.getPoDate() != null ? po.getPoDate().format(DATE_FMT) : "—"), labelFont, valueFont, com.itextpdf.text.BaseColor.WHITE, com.itextpdf.text.BaseColor.WHITE);
        addPdfKV(poInfoTable, "RFQ Number", safe(po.getRfqNumber()), labelFont, valueFont, LBLUE, LBLUE);
        addPdfKV(poInfoTable, "Quote Ref", safe(po.getReferenceQuoteNo()), labelFont, valueFont, com.itextpdf.text.BaseColor.WHITE, com.itextpdf.text.BaseColor.WHITE);
        addPdfKV(poInfoTable, "Status", safe(po.getStatus() != null ? po.getStatus().name() : "—"), labelFont, valueFont, LBLUE, LBLUE);
        addPdfKV(poInfoTable, "Currency", safe(po.getCurrencyCode()), labelFont, valueFont, com.itextpdf.text.BaseColor.WHITE, com.itextpdf.text.BaseColor.WHITE);

        com.itextpdf.text.pdf.PdfPCell rightCell = new com.itextpdf.text.pdf.PdfPCell(poInfoTable);
        rightCell.setBorder(0); rightCell.setPadding(4);
        meta.addCell(rightCell);

        doc.add(meta);
        doc.add(new com.itextpdf.text.Paragraph(" "));

        // ── Billing / Shipping ──
        com.itextpdf.text.pdf.PdfPTable addrTable = new com.itextpdf.text.pdf.PdfPTable(new float[]{1, 1});
        addrTable.setWidthPercentage(100);

        com.itextpdf.text.pdf.PdfPTable billTable = new com.itextpdf.text.pdf.PdfPTable(1);
        addPdfSectionHeader(billTable, "BILLING ADDRESS", NAVY, headFont);
        addPdfSimpleRow(billTable, safe(po.getBuyerName()), valueFont, LBLUE);
        addPdfSimpleRow(billTable, safe(po.getBuyerLocationAddress()), valueFont, com.itextpdf.text.BaseColor.WHITE);
        addPdfSimpleRow(billTable, "GSTIN: " + safe(po.getBuyerGstin()), valueFont, LBLUE);
        addPdfSimpleRow(billTable, "Email: " + safe(po.getBuyerEmail()), valueFont, com.itextpdf.text.BaseColor.WHITE);

        com.itextpdf.text.pdf.PdfPCell billCell = new com.itextpdf.text.pdf.PdfPCell(billTable);
        billCell.setBorder(0); billCell.setPadding(4);
        addrTable.addCell(billCell);

        com.itextpdf.text.pdf.PdfPTable shipTable = new com.itextpdf.text.pdf.PdfPTable(1);
        addPdfSectionHeader(shipTable, "SHIPPING ADDRESS", NAVY, headFont);
        addPdfSimpleRow(shipTable, safe(po.getDeliveryLocationName() != null ? po.getDeliveryLocationName() : po.getBuyerName()), valueFont, LBLUE);
        addPdfSimpleRow(shipTable, safe(po.getDeliveryLocationAddress() != null ? po.getDeliveryLocationAddress() : po.getBuyerLocationAddress()), valueFont, com.itextpdf.text.BaseColor.WHITE);

        com.itextpdf.text.pdf.PdfPCell shipCell = new com.itextpdf.text.pdf.PdfPCell(shipTable);
        shipCell.setBorder(0); shipCell.setPadding(4);
        addrTable.addCell(shipCell);

        doc.add(addrTable);
        doc.add(new com.itextpdf.text.Paragraph(" "));

        // ── Line Items Table ──
        com.itextpdf.text.pdf.PdfPTable lineTable = new com.itextpdf.text.pdf.PdfPTable(new float[]{0.8f, 3.5f, 1.2f, 1.5f, 1f, 1f, 1.8f});
        lineTable.setWidthPercentage(100);

        String[] cols = {"#", "Description", "UOM", "Unit Rate", "Disc%", "Tax%", "Total"};
        for (String col : cols) {
            com.itextpdf.text.pdf.PdfPCell cell = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(col, colHdrFont));
            cell.setBackgroundColor(NAVY); cell.setPadding(5); cell.setBorder(0);
            cell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            lineTable.addCell(cell);
        }

        List<POResponseDTO.POLineItemDTO> items = po.getLineItems();
        if (items != null) {
            for (int i = 0; i < items.size(); i++) {
                POResponseDTO.POLineItemDTO item = items.get(i);
                com.itextpdf.text.BaseColor rowBg = i % 2 == 0 ? com.itextpdf.text.BaseColor.WHITE : ALTROW;

                addPdfTableCell(lineTable, String.valueOf(i + 1), cellFont, rowBg, com.itextpdf.text.Element.ALIGN_CENTER);
                String desc = safe(item.getItemDescription());
                if (item.getBrandMakeModel() != null) desc += "\n" + item.getBrandMakeModel();
                addPdfTableCell(lineTable, desc, cellFont, rowBg, com.itextpdf.text.Element.ALIGN_LEFT);
                addPdfTableCell(lineTable, safe(item.getUom()), cellFont, rowBg, com.itextpdf.text.Element.ALIGN_CENTER);
                addPdfTableCell(lineTable, fmtCur(po.getCurrencySymbol(), item.getUnitRate()), cellFont, rowBg, com.itextpdf.text.Element.ALIGN_RIGHT);
                addPdfTableCell(lineTable, fmt2(item.getDiscountPercentage()) + "%", cellFont, rowBg, com.itextpdf.text.Element.ALIGN_CENTER);
                addPdfTableCell(lineTable, fmt2(item.getTaxPercentage()) + "%", cellFont, rowBg, com.itextpdf.text.Element.ALIGN_CENTER);
                addPdfTableCell(lineTable, fmtCur(po.getCurrencySymbol(), item.getLineTotalWithTax() != null ? item.getLineTotalWithTax() : item.getLineTotal()), cellFont, rowBg, com.itextpdf.text.Element.ALIGN_RIGHT);
            }
        }

        // Totals
        if (items != null && items.stream().anyMatch(i -> i.getTaxPercentage() != null && i.getTaxPercentage().compareTo(BigDecimal.ZERO) > 0)) {
            addPdfTotalRow(lineTable, "Subtotal (Pre-Tax)", fmtCur(po.getCurrencySymbol(), po.getSubtotal()), LBLUE, totalFont, 5);
            addPdfTotalRow(lineTable, "Total Tax", fmtCur(po.getCurrencySymbol(), po.getTaxAmount()), LBLUE, totalFont, 5);
        }
        addPdfGrandTotalRow(lineTable, "GRAND TOTAL (" + safe(po.getCurrencyCode()) + ")", fmtCur(po.getCurrencySymbol(), po.getGrandTotal()), GREEN, gtFont, 5);

        doc.add(lineTable);
        doc.add(new com.itextpdf.text.Paragraph(" "));

        // ── Amount in Words ──
        com.itextpdf.text.pdf.PdfPTable wordsTable = new com.itextpdf.text.pdf.PdfPTable(1);
        wordsTable.setWidthPercentage(100);
        com.itextpdf.text.pdf.PdfPCell wordsCell = new com.itextpdf.text.pdf.PdfPCell(
            new com.itextpdf.text.Phrase("Amount in Words: " + safe(po.getAmountInWords()), labelFont));
        wordsCell.setBackgroundColor(LBLUE); wordsCell.setPadding(6); wordsCell.setBorder(0);
        wordsTable.addCell(wordsCell);
        doc.add(wordsTable);
        doc.add(new com.itextpdf.text.Paragraph(" "));

        // ── Terms ──
        if (po.getPaymentTerms() != null || po.getDeliveryTerms() != null || po.getOtherTerms() != null) {
            com.itextpdf.text.pdf.PdfPTable termsTable = new com.itextpdf.text.pdf.PdfPTable(1);
            termsTable.setWidthPercentage(100);
            addPdfSectionHeader(termsTable, "TERMS & CONDITIONS", NAVY, headFont);
            if (po.getPaymentTerms() != null) addPdfKV(termsTable, "Payment Terms", safe(po.getPaymentTerms()), labelFont, valueFont, LBLUE, com.itextpdf.text.BaseColor.WHITE);
            if (po.getDeliveryTerms() != null) addPdfKV(termsTable, "Delivery Terms", safe(po.getDeliveryTerms()), labelFont, valueFont, com.itextpdf.text.BaseColor.WHITE, com.itextpdf.text.BaseColor.WHITE);
            if (po.getOtherTerms() != null) addPdfKV(termsTable, "Other Terms", safe(po.getOtherTerms()), labelFont, valueFont, LBLUE, LBLUE);
            if (po.getBuyerRemarks() != null) addPdfKV(termsTable, "Remarks", safe(po.getBuyerRemarks()), labelFont, valueFont, com.itextpdf.text.BaseColor.WHITE, com.itextpdf.text.BaseColor.WHITE);
            doc.add(termsTable);
            doc.add(new com.itextpdf.text.Paragraph(" "));
        }

        // ── Signatures ──
        com.itextpdf.text.pdf.PdfPTable sigTable = new com.itextpdf.text.pdf.PdfPTable(new float[]{1, 1});
        sigTable.setWidthPercentage(100);

        addPdfSignatureBlock(sigTable, "VENDOR SIGNATURE\nFor " + safe(po.getSupplierName()), valueFont, LBLUE);
        addPdfSignatureBlock(sigTable, "AUTHORISED SIGNATORY\nFor " + safe(po.getBuyerName()) +
                (po.getApprovedByName() != null ? "\n" + po.getApprovedByName() : "") +
                (po.getApprovedByDesignation() != null ? "\n" + po.getApprovedByDesignation() : ""), valueFont, LBLUE);

        doc.add(sigTable);
    }

    // =========================================================================
    // INVOICE PDF
    // =========================================================================

    public byte[] generateInvoicePdf(Long invoiceId) throws Exception {
        Invoice inv = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found: " + invoiceId));
        return buildInvoicePdf(inv);
    }

    private byte[] buildInvoicePdf(Invoice inv) throws Exception {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            com.itextpdf.text.Document doc = new com.itextpdf.text.Document(com.itextpdf.text.PageSize.A4, 36, 36, 54, 36);
            com.itextpdf.text.pdf.PdfWriter writer = com.itextpdf.text.pdf.PdfWriter.getInstance(doc, out);
            writer.setPageEvent(new InvoicePdfPageEvents(inv));
            doc.open();

            addInvoiceContent(doc, inv);

            doc.close();
            return out.toByteArray();
        }
    }

    private void addInvoiceContent(com.itextpdf.text.Document doc, Invoice inv) throws Exception {
        com.itextpdf.text.Font titleFont  = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 18, com.itextpdf.text.Font.BOLD, com.itextpdf.text.BaseColor.WHITE);
        com.itextpdf.text.Font headFont   = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 10, com.itextpdf.text.Font.BOLD, com.itextpdf.text.BaseColor.WHITE);
        com.itextpdf.text.Font labelFont  = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 9,  com.itextpdf.text.Font.BOLD,   new com.itextpdf.text.BaseColor(30, 64, 136));
        com.itextpdf.text.Font valueFont  = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 9,  com.itextpdf.text.Font.NORMAL, com.itextpdf.text.BaseColor.DARK_GRAY);
        com.itextpdf.text.Font colHdrFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 8,  com.itextpdf.text.Font.BOLD,   com.itextpdf.text.BaseColor.WHITE);
        com.itextpdf.text.Font cellFont   = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 8,  com.itextpdf.text.Font.NORMAL, com.itextpdf.text.BaseColor.DARK_GRAY);
        com.itextpdf.text.Font gtFont     = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 10, com.itextpdf.text.Font.BOLD,   com.itextpdf.text.BaseColor.WHITE);
        com.itextpdf.text.Font totalFont  = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 9,  com.itextpdf.text.Font.BOLD,   new com.itextpdf.text.BaseColor(30, 64, 136));

        com.itextpdf.text.BaseColor NAVY   = new com.itextpdf.text.BaseColor(30, 64, 136);
        com.itextpdf.text.BaseColor LBLUE  = new com.itextpdf.text.BaseColor(240, 244, 255);
        com.itextpdf.text.BaseColor ALTROW = new com.itextpdf.text.BaseColor(238, 242, 255);
        com.itextpdf.text.BaseColor GREEN  = new com.itextpdf.text.BaseColor(39, 174, 96);

        // ── Header ──
        com.itextpdf.text.pdf.PdfPTable banner = new com.itextpdf.text.pdf.PdfPTable(1);
        banner.setWidthPercentage(100);
        com.itextpdf.text.pdf.PdfPCell bannerCell = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase("INVOICE", titleFont));
        bannerCell.setBackgroundColor(NAVY); bannerCell.setPadding(12); bannerCell.setBorder(0);
        bannerCell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
        banner.addCell(bannerCell);
        doc.add(banner);
        doc.add(new com.itextpdf.text.Paragraph(" "));

        // ── Invoice Meta ──
        com.itextpdf.text.pdf.PdfPTable meta = new com.itextpdf.text.pdf.PdfPTable(new float[]{1, 1});
        meta.setWidthPercentage(100);

        // From (Supplier)
        com.itextpdf.text.pdf.PdfPTable fromTable = new com.itextpdf.text.pdf.PdfPTable(1);
        addPdfSectionHeader(fromTable, "FROM (SUPPLIER)", NAVY, headFont);
        addPdfSimpleRow(fromTable, safe(inv.getSupplierName()), valueFont, LBLUE);
        addPdfSimpleRow(fromTable, safe(inv.getSupplierEmail()), valueFont, com.itextpdf.text.BaseColor.WHITE);

        com.itextpdf.text.pdf.PdfPCell fromCell = new com.itextpdf.text.pdf.PdfPCell(fromTable);
        fromCell.setBorder(0); fromCell.setPadding(4);
        meta.addCell(fromCell);

        // Invoice Details
        com.itextpdf.text.pdf.PdfPTable invDetailsTable = new com.itextpdf.text.pdf.PdfPTable(1);
        addPdfSectionHeader(invDetailsTable, "INVOICE DETAILS", NAVY, headFont);
        addPdfKV(invDetailsTable, "Invoice No", safe(inv.getInvoiceNumber()), labelFont, valueFont, LBLUE, com.itextpdf.text.BaseColor.WHITE);
        addPdfKV(invDetailsTable, "Invoice Date", safe(inv.getInvoiceDate() != null ? inv.getInvoiceDate().format(DATE_FMT) : "—"), labelFont, valueFont, com.itextpdf.text.BaseColor.WHITE, com.itextpdf.text.BaseColor.WHITE);
        addPdfKV(invDetailsTable, "Due Date", safe(inv.getDueDate() != null ? inv.getDueDate().format(DATE_FMT) : "—"), labelFont, valueFont, LBLUE, LBLUE);
        addPdfKV(invDetailsTable, "PO Reference", safe(inv.getPoNumber()), labelFont, valueFont, com.itextpdf.text.BaseColor.WHITE, com.itextpdf.text.BaseColor.WHITE);
        addPdfKV(invDetailsTable, "RFQ Reference", safe(inv.getRfqNumber()), labelFont, valueFont, LBLUE, LBLUE);

        com.itextpdf.text.pdf.PdfPCell detailCell = new com.itextpdf.text.pdf.PdfPCell(invDetailsTable);
        detailCell.setBorder(0); detailCell.setPadding(4);
        meta.addCell(detailCell);

        doc.add(meta);
        doc.add(new com.itextpdf.text.Paragraph(" "));

        // ── Bill To ──
        com.itextpdf.text.pdf.PdfPTable billToTable = new com.itextpdf.text.pdf.PdfPTable(1);
        billToTable.setWidthPercentage(100);
        addPdfSectionHeader(billToTable, "BILL TO (BUYER)", NAVY, headFont);
        addPdfSimpleRow(billToTable, safe(inv.getBuyerCompanyName()), valueFont, LBLUE);
        if (inv.getRfqCreatorName() != null)
            addPdfSimpleRow(billToTable, "Attn: " + inv.getRfqCreatorName(), valueFont, com.itextpdf.text.BaseColor.WHITE);
        doc.add(billToTable);
        doc.add(new com.itextpdf.text.Paragraph(" "));

        // ── Line Items ──
        com.itextpdf.text.pdf.PdfPTable lineTable = new com.itextpdf.text.pdf.PdfPTable(new float[]{0.5f, 0.8f, 3.2f, 0.7f, 0.8f, 1.2f, 0.6f, 0.8f, 1.5f});
        lineTable.setWidthPercentage(100);

        String[] cols = {"#", "HSN", "Description", "UOM", "Qty", "Unit Price", "Tax%", "Tax Amt", "Line Total"};
        for (String col : cols) {
            com.itextpdf.text.pdf.PdfPCell cell = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(col, colHdrFont));
            cell.setBackgroundColor(NAVY); cell.setPadding(5); cell.setBorder(0);
            cell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            lineTable.addCell(cell);
        }

        List<InvoiceLineItem> items = inv.getLineItems();
        if (items != null) {
            for (int i = 0; i < items.size(); i++) {
                InvoiceLineItem item = items.get(i);
                com.itextpdf.text.BaseColor rowBg = i % 2 == 0 ? com.itextpdf.text.BaseColor.WHITE : ALTROW;

                addPdfTableCell(lineTable, String.valueOf(i + 1), cellFont, rowBg, com.itextpdf.text.Element.ALIGN_CENTER);
                addPdfTableCell(lineTable, safe(item.getHsnSacCode()), cellFont, rowBg, com.itextpdf.text.Element.ALIGN_CENTER);
                addPdfTableCell(lineTable, safe(item.getItemDescription()), cellFont, rowBg, com.itextpdf.text.Element.ALIGN_LEFT);
                addPdfTableCell(lineTable, safe(item.getUom()), cellFont, rowBg, com.itextpdf.text.Element.ALIGN_CENTER);
                addPdfTableCell(lineTable, item.getQuantity() != null ? item.getQuantity().stripTrailingZeros().toPlainString() : "—", cellFont, rowBg, com.itextpdf.text.Element.ALIGN_CENTER);
                addPdfTableCell(lineTable, fmtBD(item.getUnitPrice()), cellFont, rowBg, com.itextpdf.text.Element.ALIGN_RIGHT);
                addPdfTableCell(lineTable, fmt2(item.getTaxPercentage()) + "%", cellFont, rowBg, com.itextpdf.text.Element.ALIGN_CENTER);
                addPdfTableCell(lineTable, fmtBD(item.getTaxAmount()), cellFont, rowBg, com.itextpdf.text.Element.ALIGN_RIGHT);
                addPdfTableCell(lineTable, fmtBD(item.getLineTotal()), cellFont, rowBg, com.itextpdf.text.Element.ALIGN_RIGHT);
            }
        }

        BigDecimal subtotal    = inv.getSubtotal()    != null ? inv.getSubtotal()    : BigDecimal.ZERO;
        BigDecimal taxAmount   = inv.getTaxAmount()   != null ? inv.getTaxAmount()   : BigDecimal.ZERO;
        BigDecimal totalAmount = inv.getTotalAmount() != null ? inv.getTotalAmount() : BigDecimal.ZERO;

        addPdfTotalRow(lineTable, "Subtotal", fmtBD(subtotal), LBLUE, totalFont, 7);
        addPdfTotalRow(lineTable, "Tax Amount", fmtBD(taxAmount), LBLUE, totalFont, 7);
        addPdfGrandTotalRow(lineTable, "TOTAL AMOUNT (" + safe(inv.getCurrency()) + ")", fmtBD(totalAmount), GREEN, gtFont, 7);

        doc.add(lineTable);
        doc.add(new com.itextpdf.text.Paragraph(" "));

        // ── Bank Details ──
        if (inv.getBankName() != null) {
            com.itextpdf.text.pdf.PdfPTable bankTable = new com.itextpdf.text.pdf.PdfPTable(new float[]{1, 1});
            bankTable.setWidthPercentage(100);

            com.itextpdf.text.pdf.PdfPTable bankLeft = new com.itextpdf.text.pdf.PdfPTable(1);
            addPdfSectionHeader(bankLeft, "BANK DETAILS", NAVY, headFont);
            addPdfKV(bankLeft, "Bank Name", safe(inv.getBankName()), labelFont, valueFont, LBLUE, com.itextpdf.text.BaseColor.WHITE);
            addPdfKV(bankLeft, "Account Holder", safe(inv.getBankAccountName()), labelFont, valueFont, com.itextpdf.text.BaseColor.WHITE, com.itextpdf.text.BaseColor.WHITE);
            addPdfKV(bankLeft, "Account Number", safe(inv.getBankAccountNumber()), labelFont, valueFont, LBLUE, LBLUE);
            addPdfKV(bankLeft, "IFSC Code", safe(inv.getBankIfscCode()), labelFont, valueFont, com.itextpdf.text.BaseColor.WHITE, com.itextpdf.text.BaseColor.WHITE);

            com.itextpdf.text.pdf.PdfPCell bankLeftCell = new com.itextpdf.text.pdf.PdfPCell(bankLeft);
            bankLeftCell.setBorder(0); bankLeftCell.setPadding(4);
            bankTable.addCell(bankLeftCell);

            com.itextpdf.text.pdf.PdfPTable termsRight = new com.itextpdf.text.pdf.PdfPTable(1);
            addPdfSectionHeader(termsRight, "PAYMENT TERMS", NAVY, headFont);
            addPdfSimpleRow(termsRight, safe(inv.getPaymentTerms()), valueFont, LBLUE);
            if (inv.getNotes() != null) addPdfSimpleRow(termsRight, "Notes: " + inv.getNotes(), valueFont, com.itextpdf.text.BaseColor.WHITE);

            com.itextpdf.text.pdf.PdfPCell bankRightCell = new com.itextpdf.text.pdf.PdfPCell(termsRight);
            bankRightCell.setBorder(0); bankRightCell.setPadding(4);
            bankTable.addCell(bankRightCell);

            doc.add(bankTable);
            doc.add(new com.itextpdf.text.Paragraph(" "));
        }

        // ── Status banner ──
        if (inv.getStatus() != null) {
            com.itextpdf.text.BaseColor statusColor = switch (inv.getStatus()) {
                case APPROVED -> GREEN;
                case REJECTED, REJECTED_CLOSED -> new com.itextpdf.text.BaseColor(231, 76, 60);
                case PAID -> new com.itextpdf.text.BaseColor(39, 174, 96);
                default -> new com.itextpdf.text.BaseColor(52, 152, 219);
            };
            com.itextpdf.text.pdf.PdfPTable statusTable = new com.itextpdf.text.pdf.PdfPTable(1);
            statusTable.setWidthPercentage(100);
            com.itextpdf.text.pdf.PdfPCell statusCell = new com.itextpdf.text.pdf.PdfPCell(
                new com.itextpdf.text.Phrase("STATUS: " + inv.getStatus().name() +
                    (inv.getApprovedRejectedBy() != null ? "  |  By: " + inv.getApprovedRejectedBy() : "") +
                    (inv.getApprovedRejectedAt() != null ? "  |  On: " + inv.getApprovedRejectedAt().format(DATE_FMT) : ""), gtFont));
            statusCell.setBackgroundColor(statusColor); statusCell.setPadding(8); statusCell.setBorder(0);
            statusCell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            statusTable.addCell(statusCell);
            doc.add(statusTable);
        }
    }

    // =========================================================================
    // STYLE HELPERS — Excel
    // =========================================================================

    private XSSFCellStyle titleStyle(XSSFWorkbook wb) {
        XSSFCellStyle s = wb.createCellStyle();
        s.setFillForegroundColor(new XSSFColor(new byte[]{(byte)30, (byte)64, (byte)136}, null));
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        XSSFFont f = wb.createFont(); f.setBold(true); f.setFontHeightInPoints((short)16); f.setColor(new XSSFColor(new byte[]{(byte)255,(byte)255,(byte)255}, null));
        s.setFont(f); s.setAlignment(HorizontalAlignment.CENTER); s.setVerticalAlignment(VerticalAlignment.CENTER);
        return s;
    }

    private XSSFCellStyle subtitleStyle(XSSFWorkbook wb) {
        XSSFCellStyle s = wb.createCellStyle();
        s.setFillForegroundColor(new XSSFColor(new byte[]{(byte)30, (byte)64, (byte)136}, null));
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        XSSFFont f = wb.createFont(); f.setBold(true); f.setFontHeightInPoints((short)10); f.setColor(new XSSFColor(new byte[]{(byte)255,(byte)255,(byte)255}, null));
        s.setFont(f); s.setAlignment(HorizontalAlignment.CENTER); s.setVerticalAlignment(VerticalAlignment.CENTER);
        return s;
    }

    private XSSFCellStyle sectionHeaderStyle(XSSFWorkbook wb) {
        XSSFCellStyle s = wb.createCellStyle();
        s.setFillForegroundColor(new XSSFColor(new byte[]{(byte)30, (byte)64, (byte)136}, null));
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        XSSFFont f = wb.createFont(); f.setBold(true); f.setFontHeightInPoints((short)10); f.setColor(new XSSFColor(new byte[]{(byte)255,(byte)255,(byte)255}, null));
        s.setFont(f); s.setAlignment(HorizontalAlignment.LEFT);
        return s;
    }

    private XSSFCellStyle infoLabelStyle(XSSFWorkbook wb) {
        XSSFCellStyle s = wb.createCellStyle();
        s.setFillForegroundColor(new XSSFColor(new byte[]{(byte)240, (byte)244, (byte)255}, null));
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        XSSFFont f = wb.createFont(); f.setBold(true); f.setFontHeightInPoints((short)9);
        s.setFont(f); s.setAlignment(HorizontalAlignment.LEFT); s.setWrapText(true);
        setBorderThin(s);
        return s;
    }

    private XSSFCellStyle infoValueStyle(XSSFWorkbook wb) {
        XSSFCellStyle s = wb.createCellStyle();
        XSSFFont f = wb.createFont(); f.setFontHeightInPoints((short)9);
        s.setFont(f); s.setAlignment(HorizontalAlignment.LEFT); s.setWrapText(true);
        setBorderThin(s);
        return s;
    }

    private XSSFCellStyle tableHeaderStyle(XSSFWorkbook wb) {
        XSSFCellStyle s = wb.createCellStyle();
        s.setFillForegroundColor(new XSSFColor(new byte[]{(byte)30, (byte)64, (byte)136}, null));
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        XSSFFont f = wb.createFont(); f.setBold(true); f.setFontHeightInPoints((short)9); f.setColor(new XSSFColor(new byte[]{(byte)255,(byte)255,(byte)255}, null));
        s.setFont(f); s.setAlignment(HorizontalAlignment.CENTER); s.setWrapText(true);
        setBorderThin(s);
        return s;
    }

    private XSSFCellStyle altStyle(XSSFWorkbook wb, boolean alt, boolean bold) {
        XSSFCellStyle s = wb.createCellStyle();
        if (alt) { s.setFillForegroundColor(new XSSFColor(new byte[]{(byte)238,(byte)242,(byte)255}, null)); s.setFillPattern(FillPatternType.SOLID_FOREGROUND); }
        XSSFFont f = wb.createFont(); f.setFontHeightInPoints((short)9); if (bold) f.setBold(true);
        s.setFont(f); s.setAlignment(HorizontalAlignment.LEFT); s.setWrapText(true); setBorderThin(s);
        return s;
    }

    private XSSFCellStyle altNumStyle(XSSFWorkbook wb, boolean alt) {
        XSSFCellStyle s = altStyle(wb, alt, false);
        s.setAlignment(HorizontalAlignment.RIGHT);
        s.setDataFormat(wb.createDataFormat().getFormat("#,##0.00"));
        return s;
    }

    private XSSFCellStyle totalLabelStyle(XSSFWorkbook wb) {
        XSSFCellStyle s = wb.createCellStyle();
        s.setFillForegroundColor(new XSSFColor(new byte[]{(byte)240,(byte)244,(byte)255}, null));
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        XSSFFont f = wb.createFont(); f.setBold(true); f.setFontHeightInPoints((short)9); f.setColor(new XSSFColor(new byte[]{(byte)30,(byte)64,(byte)136}, null));
        s.setFont(f); s.setAlignment(HorizontalAlignment.RIGHT); setBorderThin(s);
        return s;
    }

    private XSSFCellStyle totalValueStyle(XSSFWorkbook wb) {
        XSSFCellStyle s = wb.createCellStyle();
        s.setFillForegroundColor(new XSSFColor(new byte[]{(byte)240,(byte)244,(byte)255}, null));
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        XSSFFont f = wb.createFont(); f.setBold(true); f.setFontHeightInPoints((short)9); f.setColor(new XSSFColor(new byte[]{(byte)30,(byte)64,(byte)136}, null));
        s.setFont(f); s.setAlignment(HorizontalAlignment.RIGHT);
        s.setDataFormat(wb.createDataFormat().getFormat("#,##0.00")); setBorderThin(s);
        return s;
    }

    private XSSFCellStyle grandTotalLabelStyle(XSSFWorkbook wb) {
        XSSFCellStyle s = wb.createCellStyle();
        s.setFillForegroundColor(new XSSFColor(new byte[]{(byte)30,(byte)64,(byte)136}, null));
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        XSSFFont f = wb.createFont(); f.setBold(true); f.setFontHeightInPoints((short)10); f.setColor(new XSSFColor(new byte[]{(byte)255,(byte)255,(byte)255}, null));
        s.setFont(f); s.setAlignment(HorizontalAlignment.RIGHT); setBorderThin(s);
        return s;
    }

    private XSSFCellStyle grandTotalValueStyle(XSSFWorkbook wb) {
        XSSFCellStyle s = wb.createCellStyle();
        s.setFillForegroundColor(new XSSFColor(new byte[]{(byte)39,(byte)174,(byte)96}, null));
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        XSSFFont f = wb.createFont(); f.setBold(true); f.setFontHeightInPoints((short)10); f.setColor(new XSSFColor(new byte[]{(byte)255,(byte)255,(byte)255}, null));
        s.setFont(f); s.setAlignment(HorizontalAlignment.RIGHT);
        s.setDataFormat(wb.createDataFormat().getFormat("#,##0.00")); setBorderThin(s);
        return s;
    }

    private void setBorderThin(XSSFCellStyle s) {
        s.setBorderTop(BorderStyle.THIN); s.setBorderBottom(BorderStyle.THIN);
        s.setBorderLeft(BorderStyle.THIN); s.setBorderRight(BorderStyle.THIN);
    }

    // =========================================================================
    // ROW BUILDER HELPERS — Excel
    // =========================================================================

    private int addBlankRow(XSSFSheet ws, int r) { ws.createRow(r++).setHeightInPoints(6); return r; }

    private int addSectionHeader(XSSFSheet ws, XSSFWorkbook wb, int r, String title, int cols) {
        XSSFRow row = ws.createRow(r++); row.setHeightInPoints(18);
        XSSFCell cell = row.createCell(0);
        cell.setCellValue(title); cell.setCellStyle(sectionHeaderStyle(wb));
        ws.addMergedRegion(new CellRangeAddress(r - 1, r - 1, 0, cols - 1));
        return r;
    }

    private int addInfoRow(XSSFSheet ws, XSSFWorkbook wb, int r, String lbl1, String val1, String lbl2, String val2) {
        XSSFRow row = ws.createRow(r++); row.setHeightInPoints(16);
        XSSFCell c0 = row.createCell(0); c0.setCellValue(lbl1); c0.setCellStyle(infoLabelStyle(wb));
        XSSFCell c1 = row.createCell(1); c1.setCellValue(val1); c1.setCellStyle(infoValueStyle(wb));
        XSSFCell c2 = row.createCell(2); c2.setCellValue(lbl2); c2.setCellStyle(infoLabelStyle(wb));
        XSSFCell c3 = row.createCell(3); c3.setCellValue(val2); c3.setCellStyle(infoValueStyle(wb));
        return r;
    }

    private int addKVRow(XSSFSheet ws, XSSFWorkbook wb, int r, String key, String value) {
        XSSFRow row = ws.createRow(r++); row.setHeightInPoints(16);
        XSSFCell c0 = row.createCell(0); c0.setCellValue(key); c0.setCellStyle(infoLabelStyle(wb));
        XSSFCell c1 = row.createCell(1); c1.setCellValue(value); c1.setCellStyle(infoValueStyle(wb));
        return r;
    }

    private void setCell(XSSFRow row, int col, String value, XSSFCellStyle style) {
        XSSFCell c = row.createCell(col); c.setCellValue(value); c.setCellStyle(style);
    }

    private void setCellNum(XSSFRow row, int col, BigDecimal value, XSSFCellStyle style) {
        XSSFCell c = row.createCell(col);
        c.setCellValue(value != null ? value.doubleValue() : 0);
        c.setCellStyle(style);
    }

    // =========================================================================
    // PDF HELPER METHODS
    // =========================================================================

    private void addPdfSectionHeader(com.itextpdf.text.pdf.PdfPTable table, String title,
            com.itextpdf.text.BaseColor bg, com.itextpdf.text.Font font) {
        com.itextpdf.text.pdf.PdfPCell cell = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(title, font));
        cell.setBackgroundColor(bg); cell.setPadding(6); cell.setBorder(0);
        table.addCell(cell);
    }

    private void addPdfKV(com.itextpdf.text.pdf.PdfPTable table, String key, String value,
            com.itextpdf.text.Font lFont, com.itextpdf.text.Font vFont,
            com.itextpdf.text.BaseColor lBg, com.itextpdf.text.BaseColor vBg) {
        com.itextpdf.text.pdf.PdfPCell kCell = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(key, lFont));
        kCell.setBackgroundColor(lBg); kCell.setPadding(4); kCell.setBorder(0);
        com.itextpdf.text.pdf.PdfPCell vCell = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(value, vFont));
        vCell.setBackgroundColor(vBg); vCell.setPadding(4); vCell.setBorder(0);
        table.addCell(kCell); table.addCell(vCell);
        // Since PdfPTable is 1-col, merge: use nested 2-col table
    }

    private void addPdfSimpleRow(com.itextpdf.text.pdf.PdfPTable table, String value,
            com.itextpdf.text.Font font, com.itextpdf.text.BaseColor bg) {
        com.itextpdf.text.pdf.PdfPCell cell = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(value, font));
        cell.setBackgroundColor(bg); cell.setPadding(4); cell.setBorder(0);
        table.addCell(cell);
    }

    private void addPdfTableCell(com.itextpdf.text.pdf.PdfPTable table, String value,
            com.itextpdf.text.Font font, com.itextpdf.text.BaseColor bg, int align) {
        com.itextpdf.text.pdf.PdfPCell cell = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(value, font));
        cell.setBackgroundColor(bg); cell.setPadding(4); cell.setBorder(0);
        cell.setHorizontalAlignment(align);
        table.addCell(cell);
    }

    private void addPdfTotalRow(com.itextpdf.text.pdf.PdfPTable table, String label, String value,
            com.itextpdf.text.BaseColor bg, com.itextpdf.text.Font font, int spanCols) {
        com.itextpdf.text.pdf.PdfPCell lCell = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(label, font));
        lCell.setBackgroundColor(bg); lCell.setPadding(5); lCell.setBorder(0);
        lCell.setColspan(spanCols); lCell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_RIGHT);
        table.addCell(lCell);
        com.itextpdf.text.pdf.PdfPCell vCell = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(value, font));
        vCell.setBackgroundColor(bg); vCell.setPadding(5); vCell.setBorder(0);
        vCell.setColspan(table.getNumberOfColumns() - spanCols); vCell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_RIGHT);
        table.addCell(vCell);
    }

    private void addPdfGrandTotalRow(com.itextpdf.text.pdf.PdfPTable table, String label, String value,
            com.itextpdf.text.BaseColor bg, com.itextpdf.text.Font font, int spanCols) {
        com.itextpdf.text.pdf.PdfPCell lCell = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(label, font));
        lCell.setBackgroundColor(bg); lCell.setPadding(7); lCell.setBorder(0);
        lCell.setColspan(spanCols); lCell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_RIGHT);
        table.addCell(lCell);
        com.itextpdf.text.pdf.PdfPCell vCell = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(value, font));
        vCell.setBackgroundColor(bg); vCell.setPadding(7); vCell.setBorder(0);
        vCell.setColspan(table.getNumberOfColumns() - spanCols); vCell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_RIGHT);
        table.addCell(vCell);
    }

    private void addPdfSignatureBlock(com.itextpdf.text.pdf.PdfPTable table, String text,
            com.itextpdf.text.Font font, com.itextpdf.text.BaseColor bg) {
        com.itextpdf.text.pdf.PdfPCell cell = new com.itextpdf.text.pdf.PdfPCell();
        cell.setBackgroundColor(bg); cell.setPadding(10); cell.setBorder(1); cell.setMinimumHeight(80);
        com.itextpdf.text.pdf.PdfPTable inner = new com.itextpdf.text.pdf.PdfPTable(1);
        try {
            inner.setWidthPercentage(100);
            com.itextpdf.text.pdf.PdfPCell tc = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(text, font));
            tc.setBorder(0); tc.setBackgroundColor(bg);
            inner.addCell(tc);
            com.itextpdf.text.pdf.PdfPCell sc = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase("Signature: _______________________", font));
            sc.setBorder(0); sc.setBackgroundColor(bg); sc.setPaddingTop(30);
            inner.addCell(sc);
        } catch (Exception ignored) {}
        cell.addElement(inner);
        table.addCell(cell);
    }

    // =========================================================================
    // FORMATTING UTILS
    // =========================================================================

    private String safe(Object val) { return val != null ? val.toString() : "—"; }

    private String fmt(BigDecimal v) {
        if (v == null) return "—";
        return String.format("%,.2f", v);
    }

    private String fmt2(BigDecimal v) {
        if (v == null) return "0.00";
        return String.format("%.2f", v);
    }

    private String fmtCur(String sym, BigDecimal v) {
        if (v == null) return (sym != null ? sym : "") + " 0.00";
        return (sym != null ? sym : "") + " " + String.format("%,.2f", v);
    }

    private String fmtBD(BigDecimal v) {
        if (v == null) return "0.00";
        return String.format("%,.2f", v);
    }

    private String buildItemDesc(POResponseDTO.POLineItemDTO item) {
        StringBuilder sb = new StringBuilder(safe(item.getItemDescription()));
        if (item.getSpecifications() != null && !item.getSpecifications().isBlank())
            sb.append("\n").append(item.getSpecifications());
        if (item.getDeliveryDays() != null) sb.append("\nDelivery: ").append(item.getDeliveryDays()).append(" days");
        if (item.getWarrantyMonths() != null) sb.append("  |  Warranty: ").append(item.getWarrantyMonths()).append(" months");
        return sb.toString();
    }

    // =========================================================================
    // PDF PAGE EVENTS (Header/Footer)
    // =========================================================================

    private static class POPdfPageEvents extends com.itextpdf.text.pdf.PdfPageEventHelper {
        private final POResponseDTO po;
        POPdfPageEvents(POResponseDTO po) { this.po = po; }

        @Override
        public void onEndPage(com.itextpdf.text.pdf.PdfWriter writer, com.itextpdf.text.Document document) {
            com.itextpdf.text.pdf.PdfContentByte cb = writer.getDirectContent();
            com.itextpdf.text.Font footerFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 8, com.itextpdf.text.Font.NORMAL, com.itextpdf.text.BaseColor.GRAY);
            try {
                com.itextpdf.text.pdf.PdfPTable footer = new com.itextpdf.text.pdf.PdfPTable(new float[]{2, 1, 1});
                footer.setTotalWidth(document.getPageSize().getWidth() - 72);
                com.itextpdf.text.pdf.PdfPCell c1 = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase("Purchase Order: " + (po.getPoNumber() != null ? po.getPoNumber() : ""), footerFont));
                c1.setBorder(com.itextpdf.text.Rectangle.TOP); c1.setPaddingTop(4);
                com.itextpdf.text.pdf.PdfPCell c2 = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase("Supplier: " + (po.getSupplierName() != null ? po.getSupplierName() : ""), footerFont));
                c2.setBorder(com.itextpdf.text.Rectangle.TOP); c2.setPaddingTop(4); c2.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                com.itextpdf.text.pdf.PdfPCell c3 = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase("Page " + writer.getPageNumber(), footerFont));
                c3.setBorder(com.itextpdf.text.Rectangle.TOP); c3.setPaddingTop(4); c3.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_RIGHT);
                footer.addCell(c1); footer.addCell(c2); footer.addCell(c3);
                footer.writeSelectedRows(0, -1, 36, document.bottomMargin() - 4, cb);
            } catch (Exception ignored) {}
        }
    }

    private static class InvoicePdfPageEvents extends com.itextpdf.text.pdf.PdfPageEventHelper {
        private final Invoice inv;
        InvoicePdfPageEvents(Invoice inv) { this.inv = inv; }

        @Override
        public void onEndPage(com.itextpdf.text.pdf.PdfWriter writer, com.itextpdf.text.Document document) {
            com.itextpdf.text.pdf.PdfContentByte cb = writer.getDirectContent();
            com.itextpdf.text.Font footerFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 8, com.itextpdf.text.Font.NORMAL, com.itextpdf.text.BaseColor.GRAY);
            try {
                com.itextpdf.text.pdf.PdfPTable footer = new com.itextpdf.text.pdf.PdfPTable(new float[]{2, 1, 1});
                footer.setTotalWidth(document.getPageSize().getWidth() - 72);
                com.itextpdf.text.pdf.PdfPCell c1 = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase("Invoice: " + (inv.getInvoiceNumber() != null ? inv.getInvoiceNumber() : ""), footerFont));
                c1.setBorder(com.itextpdf.text.Rectangle.TOP); c1.setPaddingTop(4);
                com.itextpdf.text.pdf.PdfPCell c2 = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase("Supplier: " + (inv.getSupplierName() != null ? inv.getSupplierName() : ""), footerFont));
                c2.setBorder(com.itextpdf.text.Rectangle.TOP); c2.setPaddingTop(4); c2.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                com.itextpdf.text.pdf.PdfPCell c3 = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase("Page " + writer.getPageNumber(), footerFont));
                c3.setBorder(com.itextpdf.text.Rectangle.TOP); c3.setPaddingTop(4); c3.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_RIGHT);
                footer.addCell(c1); footer.addCell(c2); footer.addCell(c3);
                footer.writeSelectedRows(0, -1, 36, document.bottomMargin() - 4, cb);
            } catch (Exception ignored) {}
        }
    }
}