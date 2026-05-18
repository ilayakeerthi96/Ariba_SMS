

// // // package com.itti.leadcapturing.service;

// // // import com.itti.leadcapturing.model.*;
// // // import com.itti.leadcapturing.repo.*;
// // // import org.apache.poi.ss.usermodel.*;
// // // import org.apache.poi.ss.util.CellRangeAddress;
// // // import org.apache.poi.xssf.usermodel.*;
// // // import org.springframework.beans.factory.annotation.Autowired;
// // // import org.springframework.stereotype.Service;
// // // import org.springframework.transaction.annotation.Transactional;

// // // import java.io.ByteArrayOutputStream;
// // // import java.math.BigDecimal;
// // // import java.time.LocalDateTime;
// // // import java.time.format.DateTimeFormatter;
// // // import java.util.*;
// // // import java.util.stream.Collectors;


// // // @Service
// // // public class RFQReportService {

// // //     @Autowired private RFQRepository              rfqRepository;
// // //     @Autowired private RFQItemRepository          rfqItemRepository;
// // //     @Autowired private RFQSupplierRepository      rfqSupplierRepository;
// // //     @Autowired private SupplierQuoteItemRepository quoteItemRepository;
// // //     @Autowired private DynamicRFQApprovalRepository approvalRepository;
// // //     @Autowired private InvoiceRepository          invoiceRepository;
// // //     @Autowired private PurchaseOrderRepository    poRepository;
// // //     @Autowired private GRNRepository              grnRepository;
// // //     @Autowired private ThreeWayMatchRepository    matchRepository;

// // //     private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
// // //     private static final DateTimeFormatter DTTM_FMT = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm");

// // //     // =========================================================================
// // //     //  PO SUMMARY EXCEL  — standalone single-PO report
// // //     // =========================================================================
// // //     @Transactional(readOnly = true)
// // //     public byte[] generatePOSummaryExcel(Long poId) throws Exception {

// // //         PurchaseOrder po = poRepository.findById(poId)
// // //                 .orElseThrow(() -> new RuntimeException("Purchase Order not found: " + poId));

// // //         List<POLineItem> lineItems = po.getLineItems();

// // //         try (XSSFWorkbook wb = new XSSFWorkbook();
// // //              ByteArrayOutputStream out = new ByteArrayOutputStream()) {

// // //             CellStyle titleStyle  = makeTitleStyle(wb);
// // //             CellStyle hdrNavy    = makeHeaderStyle(wb, rgb(0x1e, 0x40, 0x88));
// // //             CellStyle hdrGreen   = makeHeaderStyle(wb, rgb(0x1b, 0x5e, 0x20));
// // //             CellStyle labelStyle  = makeLabelStyle(wb);
// // //             CellStyle valueStyle  = makeValueStyle(wb);
// // //             CellStyle amtStyle    = makeAmountStyle(wb);
// // //             CellStyle altStyle    = makeAltRowStyle(wb);
// // //             CellStyle totalStyle  = makeTotalStyle(wb);
// // //             CellStyle statusStyle = makeStatusStyle(wb);

// // //             // ── Sheet 1: PO Overview ──────────────────────────────────────
// // //             XSSFSheet s1 = wb.createSheet("PO Overview");
// // //             s1.setColumnWidth(0, 6000); s1.setColumnWidth(1, 9000);
// // //             s1.setColumnWidth(2, 6000); s1.setColumnWidth(3, 9000);

// // //             int row = 0;
// // //             row = addBanner(s1, row, "PURCHASE ORDER", titleStyle,
// // //                     "PO: " + s(po.getPoNumber())
// // //                     + "  |  Status: " + (po.getStatus() != null ? po.getStatus().name() : "—")
// // //                     + "  |  Generated: " + LocalDateTime.now().format(DTTM_FMT), 4);

// // //             row = addSectionHeader(s1, row, "PO INFORMATION", hdrNavy, 4);
// // //             row = addLV(s1, row, labelStyle, valueStyle,
// // //                     "PO Number",         s(po.getPoNumber()),
// // //                     "PO Date",           fmt(po.getPoDate()));
// // //             row = addLV(s1, row, labelStyle, valueStyle,
// // //                     "Status",            po.getStatus() != null ? po.getStatus().name() : "—",
// // //                     "Approval Status",   po.getApprovalStatus() != null ? po.getApprovalStatus().name() : "—");
// // //             row = addLV(s1, row, labelStyle, valueStyle,
// // //                     "Reference Quote",   s(po.getReferenceQuoteNo()),
// // //                     "Currency",          s(po.getCurrencyCode()) + " " + s(po.getCurrencySymbol()));
// // //             row = addLV(s1, row, labelStyle, valueStyle,
// // //                     "Payment Terms",     s(po.getPaymentTerms()),
// // //                     "Delivery Terms",    s(po.getDeliveryTerms()));
// // //             row = addLV(s1, row, labelStyle, valueStyle,
// // //                     "Mode of Payment",   s(po.getModeOfPayment()),
// // //                     "Dispatched Through",s(po.getDispatchedThrough()));
// // //             row = addLV(s1, row, labelStyle, valueStyle,
// // //                     "Destination",       s(po.getDestination()),
// // //                     "Amount in Words",   s(po.getAmountInWords()));
// // //             if (po.getBuyerRemarks() != null)
// // //                 row = addLV(s1, row, labelStyle, valueStyle,
// // //                         "Buyer Remarks", s(po.getBuyerRemarks()), "", "");
// // //             row++;

// // //             if (po.getBuyer() != null) {
// // //                 Buyer b = po.getBuyer();
// // //                 row = addSectionHeader(s1, row, "BUYER INFORMATION", hdrNavy, 4);
// // //                 row = addLV(s1, row, labelStyle, valueStyle,
// // //                         "Company",   b.getCompanyName(),           "Type",    s(b.getCompanyType()));
// // //                 row = addLV(s1, row, labelStyle, valueStyle,
// // //                         "Contact",   b.getContactPersonName(),     "Email",   s(b.getContactPersonEmail()));
// // //                 row = addLV(s1, row, labelStyle, valueStyle,
// // //                         "Phone",     s(b.getContactPersonPhone()), "GST",     s(b.getGstNumber()));
// // //                 row++;
// // //             }

// // //             if (po.getSupplier() != null) {
// // //                 Supplier sup = po.getSupplier();
// // //                 row = addSectionHeader(s1, row, "SUPPLIER INFORMATION", hdrNavy, 4);
// // //                 row = addLV(s1, row, labelStyle, valueStyle,
// // //                         "Company",   s(sup.getCompanyName()),          "Type",  s(sup.getCompanyType()));
// // //                 row = addLV(s1, row, labelStyle, valueStyle,
// // //                         "Contact",   s(sup.getContactPersonName()),    "Email", s(sup.getContactPersonEmail()));
// // //                 row = addLV(s1, row, labelStyle, valueStyle,
// // //                         "Phone",     s(sup.getContactPersonPhone()),   "Country", s(sup.getCountry()));
// // //                 row++;
// // //             }

// // //             if (po.getDeliveryLocation() != null) {
// // //                 Location dl = po.getDeliveryLocation();
// // //                 row = addSectionHeader(s1, row, "DELIVERY LOCATION", hdrNavy, 4);
// // //                 row = addLV(s1, row, labelStyle, valueStyle,
// // //                         "Location Name", dl.getLocationName(), "City", s(dl.getCity()));
// // //                 row = addLV(s1, row, labelStyle, valueStyle,
// // //                         "State",         s(dl.getState()),     "Country", s(dl.getCountry()));
// // //                 row = addLV(s1, row, labelStyle, valueStyle,
// // //                         "Address",       s(dl.getAddressLine1()), "Contact", s(dl.getLocationContactName()));
// // //                 row++;
// // //             }

// // //             if (po.getRfq() != null) {
// // //                 row = addSectionHeader(s1, row, "RFQ REFERENCE", hdrNavy, 4);
// // //                 row = addLV(s1, row, labelStyle, valueStyle,
// // //                         "RFQ Number", s(po.getRfq().getRfqNumber()),
// // //                         "RFQ Title",  s(po.getRfq().getRfqTitle()));
// // //                 row++;
// // //             }

// // //             if (po.getApprovedByName() != null) {
// // //                 row = addSectionHeader(s1, row, "APPROVAL INFORMATION", hdrNavy, 4);
// // //                 row = addLV(s1, row, labelStyle, valueStyle,
// // //                         "Approved By",    s(po.getApprovedByName()),
// // //                         "Designation",    s(po.getApprovedByDesignation()));
// // //                 addLV(s1, row, labelStyle, valueStyle,
// // //                         "Approval Date",  fmt(po.getApprovalDate()), "", "");
// // //             }

// // //             // ── Sheet 2: Line Items ───────────────────────────────────────
// // //             XSSFSheet s2 = wb.createSheet("Line Items");
// // //             setWidths(s2, new int[]{1200, 3000, 7000, 2500, 2000, 2500, 4000, 2000, 2000, 4500, 4000});

// // //             row = 0;
// // //             row = addBanner(s2, row, "LINE ITEMS", titleStyle,
// // //                     s(po.getPoNumber()) + "  [" + lineItems.size() + " items]"
// // //                     + "  |  Currency: " + s(po.getCurrencyCode()), 11);

// // //             String[] cols = {"#", "Item Code", "Description", "Specifications", "UOM",
// // //                              "PO Qty", "Unit Price", "Disc %", "Tax %", "Line Total", "Remarks"};
// // //             addHeaderRow(s2, row++, hdrGreen, cols);

// // //             int sl = 1;
// // //             for (POLineItem li : lineItems) {
// // //                 Row r = s2.createRow(row++);
// // //                 CellStyle cs = alt(sl, valueStyle, altStyle);
// // //                 str(r, 0,  String.valueOf(sl++), cs);
// // //                 str(r, 1,  s(li.getItemCode()), cs);
// // //                 str(r, 2,  s(li.getItemDescription()), cs);
// // //                 str(r, 3,  s(li.getSpecifications()), cs);
// // //                 str(r, 4,  s(li.getUom()), cs);
// // //                 num(r, 5,  li.getQuantity(), amtStyle);
// // //                 num(r, 6,  li.getUnitRate(), amtStyle);
// // //                 num(r, 7,  li.getDiscountPercentage(), amtStyle);
// // //                 num(r, 8,  li.getTaxPercentage(), amtStyle);
// // //                 num(r, 9,  li.getLineTotal(), amtStyle);
// // //                 str(r, 10, s(li.getBrandMakeModel()), cs);
// // //             }

// // //             row++;
// // //             Object[][] totals = {
// // //                 {"Subtotal (Pre-tax)", po.getSubtotal()},
// // //                 {"Tax Amount",         po.getTaxAmount()},
// // //                 {"GRAND TOTAL",        po.getGrandTotal()}
// // //             };
// // //             for (int t = 0; t < totals.length; t++) {
// // //                 Row tr = s2.createRow(row++);
// // //                 boolean isGrand = (t == totals.length - 1);
// // //                 Cell lc = tr.createCell(9);
// // //                 lc.setCellValue((String) totals[t][0]);
// // //                 lc.setCellStyle(isGrand ? totalStyle : labelStyle);
// // //                 Cell vc = tr.createCell(10);
// // //                 if (totals[t][1] instanceof BigDecimal)
// // //                     vc.setCellValue(((BigDecimal) totals[t][1]).doubleValue());
// // //                 else
// // //                     vc.setCellValue(0);
// // //                 vc.setCellStyle(isGrand ? totalStyle : amtStyle);
// // //             }

// // //             if (po.getAmountInWords() != null) {
// // //                 row++;
// // //                 Row amtRow = s2.createRow(row++);
// // //                 Cell lbl = amtRow.createCell(0);
// // //                 lbl.setCellValue("Amount in Words:");
// // //                 lbl.setCellStyle(labelStyle);
// // //                 Cell val = amtRow.createCell(1);
// // //                 val.setCellValue(po.getAmountInWords());
// // //                 val.setCellStyle(valueStyle);
// // //                 s2.addMergedRegion(new CellRangeAddress(row - 1, row - 1, 1, 10));
// // //             }

// // //             wb.write(out);
// // //             return out.toByteArray();
// // //         }
// // //     }

// // //     // =========================================================================
// // //     //  GRN EXCEL  — standalone single-GRN report
// // //     // =========================================================================
// // //     @Transactional(readOnly = true)
// // //     public byte[] generateGRNExcel(Long grnId) throws Exception {

// // //         GRN grn = grnRepository.findByIdWithLineItems(grnId)
// // //                 .orElseThrow(() -> new RuntimeException("GRN not found: " + grnId));

// // //         List<GRNLineItem> lineItems = grn.getLineItems();

// // //         try (XSSFWorkbook wb = new XSSFWorkbook();
// // //              ByteArrayOutputStream out = new ByteArrayOutputStream()) {

// // //             CellStyle titleStyle  = makeTitleStyle(wb);
// // //             CellStyle hdrNavy    = makeHeaderStyle(wb, rgb(0x1e, 0x40, 0x88));
// // //             CellStyle hdrGreen   = makeHeaderStyle(wb, rgb(0x1b, 0x5e, 0x20));
// // //             CellStyle hdrOrange  = makeHeaderStyle(wb, rgb(0xe6, 0x55, 0x00));
// // //             CellStyle labelStyle  = makeLabelStyle(wb);
// // //             CellStyle valueStyle  = makeValueStyle(wb);
// // //             CellStyle amtStyle    = makeAmountStyle(wb);
// // //             CellStyle altStyle    = makeAltRowStyle(wb);
// // //             CellStyle totalStyle  = makeTotalStyle(wb);
// // //             CellStyle statusStyle = makeStatusStyle(wb);

// // //             XSSFSheet s1 = wb.createSheet("GRN Overview");
// // //             s1.setColumnWidth(0, 6000); s1.setColumnWidth(1, 9000);
// // //             s1.setColumnWidth(2, 6000); s1.setColumnWidth(3, 9000);

// // //             int row = 0;
// // //             row = addBanner(s1, row, "GOODS RECEIPT NOTE", titleStyle,
// // //                     "GRN: " + s(grn.getGrnNumber())
// // //                     + "  |  Status: " + (grn.getStatus() != null ? grn.getStatus().name() : "—")
// // //                     + "  |  Generated: " + LocalDateTime.now().format(DTTM_FMT), 4);

// // //             row = addSectionHeader(s1, row, "GRN INFORMATION", hdrNavy, 4);
// // //             row = addLV(s1, row, labelStyle, valueStyle,
// // //                     "GRN Number",    s(grn.getGrnNumber()),
// // //                     "Status",        grn.getStatus() != null ? grn.getStatus().name() : "—");
// // //             row = addLV(s1, row, labelStyle, valueStyle,
// // //                     "Received Date", fmt(grn.getReceivedDate()),
// // //                     "PO Number",     s(grn.getPoNumber()));
// // //             row = addLV(s1, row, labelStyle, valueStyle,
// // //                     "Invoice Number", s(grn.getInvoiceNumber()),
// // //                     "Supplier",       s(grn.getSupplierName()));
// // //             row++;

// // //             row = addSectionHeader(s1, row, "DELIVERY DETAILS", hdrNavy, 4);
// // //             row = addLV(s1, row, labelStyle, valueStyle,
// // //                     "Delivery Challan", s(grn.getDeliveryChallanNumber()),
// // //                     "LR Number",        s(grn.getLrNumber()));
// // //             row = addLV(s1, row, labelStyle, valueStyle,
// // //                     "Transporter",      s(grn.getTransporterName()),
// // //                     "Vehicle No.",      s(grn.getVehicleNumber()));
// // //             row = addLV(s1, row, labelStyle, valueStyle,
// // //                     "Delivery Location", s(grn.getDeliveryLocation()),
// // //                     "Received By",       s(grn.getReceivedByName()));
// // //             row++;

// // //             row = addSectionHeader(s1, row, "QA INFORMATION", hdrOrange, 4);
// // //             row = addLV(s1, row, labelStyle, valueStyle,
// // //                     "QA Inspector",  s(grn.getInspectedByName()),
// // //                     "Approved By",   s(grn.getApprovedByName()));
// // //             row = addLV(s1, row, labelStyle, valueStyle,
// // //                     "Approved At",   fmt(grn.getApprovedAt()),
// // //                     "Remarks",       s(grn.getRemarks()));
// // //             row++;

// // //             row = addSectionHeader(s1, row, "VALUE SUMMARY", hdrNavy, 4);
// // //             row = addLV(s1, row, labelStyle, valueStyle,
// // //                     "Total Ordered Value",  s(grn.getTotalOrderedValue()),
// // //                     "Total Received Value", s(grn.getTotalReceivedValue()));
// // //             addLV(s1, row, labelStyle, valueStyle,
// // //                     "Total Rejected Value", s(grn.getTotalRejectedValue()), "", "");

// // //             XSSFSheet s2 = wb.createSheet("Line Items");
// // //             setWidths(s2, new int[]{
// // //                 1200, 3000, 7000, 2500, 2000,
// // //                 3000, 3000, 3000, 3000, 4000,
// // //                 4000, 4000, 2500, 5000
// // //             });

// // //             row = 0;
// // //             row = addBanner(s2, row, "LINE ITEMS", titleStyle,
// // //                     s(grn.getGrnNumber()) + "  [" + lineItems.size() + " items]", 14);

// // //             Row ph1 = s2.createRow(row++);
// // //             ph1.setHeightInPoints(16);
// // //             str(ph1, 0,  "#",             hdrNavy);
// // //             str(ph1, 1,  "Item Code",     hdrNavy);
// // //             str(ph1, 2,  "Description",   hdrNavy);
// // //             str(ph1, 3,  "UOM",           hdrNavy);
// // //             str(ph1, 4,  "Ordered",       hdrNavy);
// // //             str(ph1, 5,  "Received",      hdrNavy);
// // //             str(ph1, 6,  "Defective",     hdrOrange);
// // //             str(ph1, 7,  "Rejected",      hdrOrange);
// // //             str(ph1, 8,  "Accepted",      hdrGreen);
// // //             str(ph1, 9,  "PO Rate",       hdrNavy);
// // //             str(ph1, 10, "PO Line Total", hdrNavy);
// // //             str(ph1, 11, "Accepted Value",hdrGreen);
// // //             str(ph1, 12, "Condition",     hdrOrange);
// // //             str(ph1, 13, "QA Remarks",    hdrOrange);

// // //             int sl = 1;
// // //             for (GRNLineItem li : lineItems) {
// // //                 Row r = s2.createRow(row++);
// // //                 CellStyle cs = alt(sl, valueStyle, altStyle);
// // //                 str(r, 0,  String.valueOf(sl++), cs);
// // //                 str(r, 1,  s(li.getItemCode()), cs);
// // //                 str(r, 2,  s(li.getItemDescription()), cs);
// // //                 str(r, 3,  s(li.getUom()), cs);
// // //                 num(r, 4,  li.getOrderedQuantity(), amtStyle);
// // //                 num(r, 5,  li.getReceivedQuantity(), amtStyle);
// // //                 num(r, 6,  li.getDefectiveQuantity(), amtStyle);
// // //                 num(r, 7,  li.getRejectedQuantity(), amtStyle);
// // //                 num(r, 8,  li.getAcceptedQuantity(), amtStyle);
// // //                 num(r, 9,  li.getPoUnitRate(), amtStyle);
// // //                 num(r, 10, li.getPoLineTotal(), amtStyle);
// // //                 num(r, 11, li.getAcceptedValue(), amtStyle);
// // //                 str(r, 12, li.getItemCondition() != null ? li.getItemCondition().name() : "GOOD", statusStyle);
// // //                 str(r, 13, s(li.getQaRemarks()), cs);
// // //             }

// // //             row++;
// // //             String[][] grnTotals = {
// // //                 {"Total Ordered Value",   grn.getTotalOrderedValue()  != null ? grn.getTotalOrderedValue().toPlainString()  : "0"},
// // //                 {"Total Received Value",  grn.getTotalReceivedValue() != null ? grn.getTotalReceivedValue().toPlainString() : "0"},
// // //                 {"Total Rejected Value",  grn.getTotalRejectedValue() != null ? grn.getTotalRejectedValue().toPlainString() : "0"}
// // //             };
// // //             for (int t = 0; t < grnTotals.length; t++) {
// // //                 Row tr = s2.createRow(row++);
// // //                 boolean isLast = (t == grnTotals.length - 1);
// // //                 Cell lc = tr.createCell(10); lc.setCellValue(grnTotals[t][0]); lc.setCellStyle(isLast ? totalStyle : labelStyle);
// // //                 Cell vc = tr.createCell(11);
// // //                 try { vc.setCellValue(Double.parseDouble(grnTotals[t][1])); } catch (Exception e) { vc.setCellValue(0); }
// // //                 vc.setCellStyle(isLast ? totalStyle : amtStyle);
// // //             }

// // //             wb.write(out);
// // //             return out.toByteArray();
// // //         }
// // //     }

// // //     // =========================================================================
// // //     //  THREE-WAY MATCH EXCEL  — standalone single-match report
// // //     // =========================================================================
// // //     @Transactional(readOnly = true)
// // //     public byte[] generateThreeWayMatchExcel(Long matchId) throws Exception {

// // //         ThreeWayMatch match = matchRepository.findByIdWithLineResults(matchId)
// // //                 .orElseThrow(() -> new RuntimeException("3-Way Match not found: " + matchId));

// // //         List<MatchLineResult> lineResults = match.getLineResults();

// // //         try (XSSFWorkbook wb = new XSSFWorkbook();
// // //              ByteArrayOutputStream out = new ByteArrayOutputStream()) {

// // //             CellStyle titleStyle  = makeTitleStyle(wb);
// // //             CellStyle hdrNavy    = makeHeaderStyle(wb, rgb(0x1e, 0x40, 0x88));
// // //             CellStyle hdrGreen   = makeHeaderStyle(wb, rgb(0x1b, 0x5e, 0x20));
// // //             CellStyle hdrRed     = makeHeaderStyle(wb, rgb(0xb7, 0x1c, 0x1c));
// // //             CellStyle hdrOrange  = makeHeaderStyle(wb, rgb(0xe6, 0x55, 0x00));
// // //             CellStyle labelStyle  = makeLabelStyle(wb);
// // //             CellStyle valueStyle  = makeValueStyle(wb);
// // //             CellStyle amtStyle    = makeAmountStyle(wb);
// // //             CellStyle altStyle    = makeAltRowStyle(wb);
// // //             CellStyle totalStyle  = makeTotalStyle(wb);
// // //             CellStyle statusStyle = makeStatusStyle(wb);
// // //             CellStyle lowestStyle = makeLowestStyle(wb);
// // //             CellStyle warnStyle   = makeWarnStyle(wb);

// // //             XSSFSheet s1 = wb.createSheet("Match Summary");
// // //             s1.setColumnWidth(0, 6000); s1.setColumnWidth(1, 9000);
// // //             s1.setColumnWidth(2, 6000); s1.setColumnWidth(3, 9000);

// // //             int row = 0;

// // //             boolean isMatched  = "MATCHED".equals(match.getMatchStatus() != null ? match.getMatchStatus().name() : "");
// // //             boolean isFailed   = match.getMatchStatus() != null &&
// // //                     (match.getMatchStatus() == ThreeWayMatchStatus.FAILED
// // //                   || match.getMatchStatus() == ThreeWayMatchStatus.ITEM_MISMATCH
// // //                   || match.getMatchStatus() == ThreeWayMatchStatus.DISPUTED);

// // //             CellStyle bannerStyle = isMatched ? makeBannerMatchStyle(wb)
// // //                                   : isFailed  ? makeBannerFailStyle(wb)
// // //                                                : makeBannerWarnStyle(wb);

// // //             row = addBannerCustomStyle(s1, row, "3-WAY MATCH REPORT", bannerStyle,
// // //                     "Match #" + match.getId()
// // //                     + "  v" + match.getMatchVersion()
// // //                     + "  |  Status: " + (match.getMatchStatus() != null ? match.getMatchStatus().name() : "—")
// // //                     + "  |  Generated: " + LocalDateTime.now().format(DTTM_FMT), 4);

// // //             row = addSectionHeader(s1, row, "DOCUMENT REFERENCES", hdrNavy, 4);
// // //             row = addLV(s1, row, labelStyle, valueStyle,
// // //                     "PO Number",      s(match.getPoNumber()),
// // //                     "GRN Number",     s(match.getGrnNumber()));
// // //             row = addLV(s1, row, labelStyle, valueStyle,
// // //                     "Invoice Number", s(match.getInvoiceNumber()),
// // //                     "Match Version",  String.valueOf(match.getMatchVersion()));
// // //             row = addLV(s1, row, labelStyle, valueStyle,
// // //                     "Tolerance %",    s(match.getTolerancePercentage()),
// // //                     "Performed By",   s(match.getPerformedByName()));
// // //             row = addLV(s1, row, labelStyle, valueStyle,
// // //                     "Performed At",   fmt(match.getPerformedAt()),
// // //                     "Updated At",     fmt(match.getUpdatedAt()));
// // //             row++;

// // //             row = addSectionHeader(s1, row, "FINANCIAL COMPARISON", hdrNavy, 4);
// // //             row = addLV(s1, row, labelStyle, valueStyle,
// // //                     "PO Total Value",      s(match.getPoTotalValue()),
// // //                     "GRN Accepted Value",  s(match.getGrnAcceptedValue()));
// // //             row = addLV(s1, row, labelStyle, valueStyle,
// // //                     "Invoice Total Value", s(match.getInvoiceTotalValue()),
// // //                     "Variance Amount",     s(match.getVarianceAmount()));
// // //             row = addLV(s1, row, labelStyle, valueStyle,
// // //                     "Variance %",          s(match.getVariancePercentage()),
// // //                     "Approved Payment",    s(match.getApprovedPaymentAmount()));
// // //             row++;

// // //             row = addSectionHeader(s1, row, "MATCH FLAGS", hdrNavy, 4);
// // //             row = addLV(s1, row, labelStyle, valueStyle,
// // //                     "Quantity Mismatch",  bool(match.getHasQuantityMismatch()),
// // //                     "Price Mismatch",     bool(match.getHasPriceMismatch()));
// // //             row = addLV(s1, row, labelStyle, valueStyle,
// // //                     "Item Mismatch",      bool(match.getHasItemMismatch()),
// // //                     "Excess Delivery",    bool(match.getHasExcessDelivery()));
// // //             row = addLV(s1, row, labelStyle, valueStyle,
// // //                     "Matched Lines",      String.valueOf(match.getTotalMatchedLines()),
// // //                     "Mismatch Lines",     String.valueOf(match.getTotalMismatchLines()));
// // //             row++;

// // //             if (match.getResolution() != null) {
// // //                 row = addSectionHeader(s1, row, "RESOLUTION", hdrNavy, 4);
// // //                 row = addLV(s1, row, labelStyle, valueStyle,
// // //                         "Resolution",         match.getResolution().name(),
// // //                         "Resolved By",        s(match.getResolvedByName()));
// // //                 row = addLV(s1, row, labelStyle, valueStyle,
// // //                         "Resolved At",        fmt(match.getResolvedAt()),
// // //                         "Resolution Remarks", s(match.getResolutionRemarks()));
// // //             }

// // //             if (match.getMatchSummary() != null) {
// // //                 row++;
// // //                 Row summaryRow = s1.createRow(row++);
// // //                 summaryRow.setHeightInPoints(30);
// // //                 s1.addMergedRegion(new CellRangeAddress(row - 1, row - 1, 0, 3));
// // //                 Cell sc = summaryRow.createCell(0);
// // //                 sc.setCellValue(match.getMatchSummary());
// // //                 sc.setCellStyle(isMatched ? lowestStyle : warnStyle);
// // //             }

// // //             XSSFSheet s2 = wb.createSheet("Line Results");
// // //             setWidths(s2, new int[]{
// // //                 1200, 3000, 7000, 2000,
// // //                 3000, 3000, 3000,
// // //                 3500, 3500, 3000,
// // //                 3500, 3500, 2500,
// // //                 4500, 4500, 4500,
// // //                 3000, 8000
// // //             });

// // //             row = 0;
// // //             row = addBanner(s2, row, "LINE-BY-LINE MATCH RESULTS", titleStyle,
// // //                     s(match.getInvoiceNumber()) + " ↔ " + s(match.getGrnNumber())
// // //                     + "  |  " + lineResults.size() + " lines", 18);

// // //             String[] cols = {
// // //                 "#", "Item Code", "Item Description", "UOM",
// // //                 "PO Qty", "GRN Accepted", "Inv Qty",
// // //                 "Qty Variance", "Qty Var%", "Within Tol?",
// // //                 "PO Rate", "Inv Price", "Price Var%",
// // //                 "PO Line Value", "GRN Acc. Value", "Inv Line Value",
// // //                 "Line Status", "Mismatch Notes"
// // //             };
// // //             addHeaderRow(s2, row++, hdrNavy, cols);

// // //             int sl = 1;
// // //             for (MatchLineResult lr : lineResults) {
// // //                 Row r = s2.createRow(row++);
// // //                 boolean lineMatched = lr.getLineMatchStatus() == MatchLineResult.LineMatchStatus.MATCHED
// // //                         || lr.getLineMatchStatus() == MatchLineResult.LineMatchStatus.PARTIALLY_MATCHED;
// // //                 boolean lineFailed  = lr.getLineMatchStatus() == MatchLineResult.LineMatchStatus.NOT_IN_PO
// // //                         || lr.getLineMatchStatus() == MatchLineResult.LineMatchStatus.NOT_RECEIVED;

// // //                 CellStyle cs  = lineFailed ? warnStyle : alt(sl, valueStyle, altStyle);
// // //                 CellStyle ams = lineFailed ? warnStyle : amtStyle;
// // //                 CellStyle gs  = lineMatched ? lowestStyle : ams;

// // //                 str(r, 0,  String.valueOf(sl++), cs);
// // //                 str(r, 1,  s(lr.getItemCode()), cs);
// // //                 str(r, 2,  s(lr.getItemDescription()), cs);
// // //                 str(r, 3,  s(lr.getUom()), cs);
// // //                 num(r, 4,  lr.getPoOrderedQuantity(), ams);
// // //                 num(r, 5,  lr.getGrnAcceptedQuantity(), gs);
// // //                 num(r, 6,  lr.getInvoiceQuantity(), ams);
// // //                 num(r, 7,  lr.getQuantityVariance(), ams);
// // //                 num(r, 8,  lr.getQuantityVariancePct(), amtStyle);
// // //                 str(r, 9,  Boolean.TRUE.equals(lr.getWithinTolerance()) ? "✓ Yes" : "✗ No", statusStyle);
// // //                 num(r, 10, lr.getPoUnitRate(), ams);
// // //                 num(r, 11, lr.getInvoiceUnitPrice(), ams);
// // //                 num(r, 12, lr.getPriceVariancePct(), amtStyle);
// // //                 num(r, 13, lr.getPoLineValue(), ams);
// // //                 num(r, 14, lr.getGrnAcceptedValue(), gs);
// // //                 num(r, 15, lr.getInvoiceLineValue(), ams);
// // //                 str(r, 16, lr.getLineMatchStatus() != null ? lr.getLineMatchStatus().name() : "—", statusStyle);
// // //                 str(r, 17, s(lr.getMismatchNotes()), cs);
// // //             }

// // //             row++;
// // //             BigDecimal totPOVal  = lineResults.stream().filter(lr -> lr.getPoLineValue()      != null).map(MatchLineResult::getPoLineValue).reduce(BigDecimal.ZERO, BigDecimal::add);
// // //             BigDecimal totGRNVal = lineResults.stream().filter(lr -> lr.getGrnAcceptedValue() != null).map(MatchLineResult::getGrnAcceptedValue).reduce(BigDecimal.ZERO, BigDecimal::add);
// // //             BigDecimal totInvVal = lineResults.stream().filter(lr -> lr.getInvoiceLineValue() != null).map(MatchLineResult::getInvoiceLineValue).reduce(BigDecimal.ZERO, BigDecimal::add);
// // //             BigDecimal totVarVal = totInvVal.subtract(totGRNVal);

// // //             Row totRow = s2.createRow(row++);
// // //             for (int c = 0; c < 13; c++) { Cell nc = totRow.createCell(c); nc.setCellStyle(totalStyle); if (c == 0) nc.setCellValue("TOTALS"); }
// // //             num(totRow, 13, totPOVal,  totalStyle);
// // //             num(totRow, 14, totGRNVal, totalStyle);
// // //             num(totRow, 15, totInvVal, totalStyle);
// // //             for (int c = 16; c < 18; c++) {
// // //                 Cell nc = totRow.createCell(c); nc.setCellStyle(totalStyle);
// // //                 if (c == 16) nc.setCellValue("VARIANCE: " + (totVarVal.compareTo(BigDecimal.ZERO) >= 0 ? "+" : "") + totVarVal.toPlainString());
// // //             }

// // //             wb.write(out);
// // //             return out.toByteArray();
// // //         }
// // //     }

// // //     // =========================================================================
// // //     //  1a.  RFQ SUMMARY EXCEL  —  BUYER version  (all sheets)
// // //     // =========================================================================
// // //     @Transactional(readOnly = true)
// // //     public byte[] generateRFQSummaryExcel(Long rfqId) throws Exception {
// // //         return generateRFQSummaryExcel(rfqId, false);
// // //     }

// // //     // =========================================================================
// // //     //  1b.  RFQ SUMMARY EXCEL  —  SUPPLIER version  (Overview + Line Items only)
// // //     // =========================================================================
// // //     @Transactional(readOnly = true)
// // //     public byte[] generateRFQSummaryExcelForSupplier(Long rfqId) throws Exception {
// // //         return generateRFQSummaryExcel(rfqId, true);
// // //     }

// // //     /**
// // //      * Core implementation.
// // //      *
// // //      * supplierMode = true  → 2 sheets only: RFQ Overview + Line Items
// // //      * supplierMode = false → 12 sheets: RFQ Overview, Line Items, Invited Suppliers,
// // //      *                        Approval History, Purchase Order, PO Line Items,
// // //      *                        Invoice, Invoice Line Items, GRN, GRN Line Items,
// // //      *                        3-Way Match, Match Line Results
// // //      */
// // //     @Transactional(readOnly = true)
// // //     public byte[] generateRFQSummaryExcel(Long rfqId, boolean supplierMode) throws Exception {

// // //         RFQ rfq = rfqRepository.findByIdWithAllDetails(rfqId)
// // //                 .orElseThrow(() -> new RuntimeException("RFQ not found: " + rfqId));
// // //         rfqRepository.findByIdWithItems(rfqId).ifPresent(r -> r.getItems().size());
// // //         rfqRepository.findByIdWithSuppliers(rfqId).ifPresent(r -> r.getSelectedSuppliers().size());

// // //         List<RFQItem>            items     = rfqItemRepository.findByRfqId(rfqId);
// // //         List<RFQSupplier>        suppliers = supplierMode ? Collections.emptyList()
// // //                                                           : rfqSupplierRepository.findByRFQId(rfqId);
// // //         List<DynamicRFQApproval> approvals = supplierMode ? Collections.emptyList()
// // //                                                           : approvalRepository.findByRfqIdOrderBySequenceOrderAsc(rfqId);

// // //         // ── Extra data for buyer sheets ────────────────────────────────────
// // //         List<PurchaseOrder> pos     = Collections.emptyList();
// // //         List<Invoice>       invList = Collections.emptyList();
// // //         List<GRN>           grns    = Collections.emptyList();
// // //         List<ThreeWayMatch> matches = Collections.emptyList();

// // //         if (!supplierMode) {
// // //             try { pos = poRepository.findByRfqId(rfqId); } catch (Exception ignored) {}
// // //             if (!pos.isEmpty()) {
// // //                 Long poId = pos.get(0).getId();
// // //                 try { invList = invoiceRepository.findByPoId(poId); } catch (Exception ignored) {}
// // //                 try { grns    = grnRepository.findByPurchaseOrderId(poId); } catch (Exception ignored) {}
// // //                 if (!invList.isEmpty()) {
// // //                     Long invId = invList.get(0).getId();
// // //                     try { matches = matchRepository.findByInvoiceId(invId); } catch (Exception ignored) {}
// // //                 }
// // //             }
// // //         }

// // //         try (XSSFWorkbook wb = new XSSFWorkbook();
// // //              ByteArrayOutputStream out = new ByteArrayOutputStream()) {

// // //             CellStyle titleStyle  = makeTitleStyle(wb);
// // //             CellStyle hdrNavy    = makeHeaderStyle(wb, rgb(0x1e, 0x40, 0x88));
// // //             CellStyle hdrGreen   = makeHeaderStyle(wb, rgb(0x2e, 0x7d, 0x32));
// // //             CellStyle hdrOrange  = makeHeaderStyle(wb, rgb(0xe6, 0x55, 0x00));
// // //             CellStyle hdrPurple  = makeHeaderStyle(wb, rgb(0x6a, 0x0d, 0xad));
// // //             CellStyle labelStyle  = makeLabelStyle(wb);
// // //             CellStyle valueStyle  = makeValueStyle(wb);
// // //             CellStyle amtStyle    = makeAmountStyle(wb);
// // //             CellStyle altStyle    = makeAltRowStyle(wb);
// // //             CellStyle statusStyle = makeStatusStyle(wb);
// // //             CellStyle lowestStyle = makeLowestStyle(wb);
// // //             CellStyle totalStyle  = makeTotalStyle(wb);
// // //             CellStyle warnStyle   = makeWarnStyle(wb);

// // //             // ═══════════════════════════════════════════════════════════════
// // //             //  SHEET 1: RFQ Overview
// // //             // ═══════════════════════════════════════════════════════════════
// // //             XSSFSheet s1 = wb.createSheet("RFQ Overview");
// // //             s1.setColumnWidth(0, 6000); s1.setColumnWidth(1, 9000);
// // //             s1.setColumnWidth(2, 6000); s1.setColumnWidth(3, 9000);

// // //             int row = 0;
// // //             String bannerSub = "RFQ: " + rfq.getRfqNumber()
// // //                     + (supplierMode ? "   |   Supplier Copy" : "")
// // //                     + "   |   Generated: " + LocalDateTime.now().format(DTTM_FMT);
// // //             row = addBanner(s1, row, "RFQ SUMMARY REPORT", titleStyle, bannerSub, 4);

// // //             row = addSectionHeader(s1, row, "RFQ INFORMATION", hdrNavy, 4);
// // //             row = addLV(s1, row, labelStyle, valueStyle, "RFQ Number",    rfq.getRfqNumber(),              "RFQ Title",       rfq.getRfqTitle());
// // //             row = addLV(s1, row, labelStyle, valueStyle, "Status",        en(rfq.getStatus()),              "Priority",        en(rfq.getPriority()));
// // //             row = addLV(s1, row, labelStyle, valueStyle, "Issue Date",    fmt(rfq.getIssueDate()),          "Due Date",        fmt(rfq.getDueDate()));
// // //             row = addLV(s1, row, labelStyle, valueStyle, "Item Req Date", fmt(rfq.getItemRequiredDate()),   "Approval Req",    bool(rfq.getApprovalRequired()));
// // //             row = addLV(s1, row, labelStyle, valueStyle, "Payment Terms", s(rfq.getPaymentTerms()),        "Delivery Terms",  s(rfq.getDeliveryTerms()));
// // //             row = addLV(s1, row, labelStyle, valueStyle, "Tax %",         s(rfq.getTaxPercentage()),       "Justification",   s(rfq.getJustification()));
// // //             row = addLV(s1, row, labelStyle, valueStyle, "Cost Center",   s(rfq.getCostCenterCode()),     "Project Code",    s(rfq.getProjectCode()));
// // //             row++;

// // //             if (rfq.getBuyer() != null) {
// // //                 Buyer b = rfq.getBuyer();
// // //                 row = addSectionHeader(s1, row, "BUYER INFORMATION", hdrNavy, 4);
// // //                 row = addLV(s1, row, labelStyle, valueStyle, "Company",    b.getCompanyName(),           "Type",    s(b.getCompanyType()));
// // //                 row = addLV(s1, row, labelStyle, valueStyle, "Contact",    b.getContactPersonName(),     "Email",   s(b.getContactPersonEmail()));
// // //                 row = addLV(s1, row, labelStyle, valueStyle, "Phone",      s(b.getContactPersonPhone()), "GST",     s(b.getGstNumber()));
// // //                 row = addLV(s1, row, labelStyle, valueStyle, "City/State", s(b.getCity()) + ", " + s(b.getState()), "Country", s(b.getCountry()));
// // //                 row++;
// // //             }

// // //             if (rfq.getLocation() != null) {
// // //                 Location l = rfq.getLocation();
// // //                 row = addSectionHeader(s1, row, "DELIVERY LOCATION", hdrNavy, 4);
// // //                 row = addLV(s1, row, labelStyle, valueStyle, "Location Name", l.getLocationName(),    "Type",    s(l.getLocationType()));
// // //                 row = addLV(s1, row, labelStyle, valueStyle, "Address",       s(l.getAddressLine1()), "City",    s(l.getCity()));
// // //                 row = addLV(s1, row, labelStyle, valueStyle, "State",         s(l.getState()),        "Country", s(l.getCountry()));
// // //                 row = addLV(s1, row, labelStyle, valueStyle, "Currency",      s(l.getCurrencyCode()), "Contact", s(l.getLocationContactName()));
// // //                 row++;
// // //             }

// // //             if (rfq.getCreatedByUser() != null && !supplierMode) {
// // //                 User u = rfq.getCreatedByUser();
// // //                 row = addSectionHeader(s1, row, "CREATED BY", hdrNavy, 4);
// // //                 addLV(s1, row, labelStyle, valueStyle,
// // //                         "Name", u.getFirstName() + " " + u.getLastName(), "Email", s(u.getEmail()));
// // //             }

// // //             // ═══════════════════════════════════════════════════════════════
// // //             //  SHEET 2: Line Items
// // //             // ═══════════════════════════════════════════════════════════════
// // //             XSSFSheet s2 = wb.createSheet("Line Items");
// // //             setWidths(s2, new int[]{1200, 3500, 8000, 7000, 6000, 2500, 3000, 3500, 4000});
// // //             row = 0;
// // //             row = addBanner(s2, row, "LINE ITEMS", titleStyle,
// // //                     rfq.getRfqNumber() + "  —  " + rfq.getRfqTitle() + "  [" + items.size() + " items]", 9);
// // //             String[] itCols = {"#","Item Code","Description","Detailed Description","Specifications","Qty","UOM","Unit Price","Required Date"};
// // //             addHeaderRow(s2, row++, hdrGreen, itCols);
// // //             int sl = 1;
// // //             for (RFQItem it : items) {
// // //                 Row r = s2.createRow(row++);
// // //                 CellStyle cs = alt(sl, valueStyle, altStyle);
// // //                 str(r, 0, String.valueOf(sl++), cs);
// // //                 str(r, 1, s(it.getItemCode()), cs);
// // //                 str(r, 2, s(it.getItemDescription()), cs);
// // //                 str(r, 3, s(it.getItemDescriptionDetailed()), cs);
// // //                 str(r, 4, s(it.getSpecifications()), cs);
// // //                 num(r, 5, it.getQuantity(), amtStyle);
// // //                 str(r, 6, s(it.getUom()), cs);
// // //                 num(r, 7, it.getUnitPrice(), amtStyle);
// // //                 str(r, 8, fmt(it.getItemRequiredDate()), cs);
// // //             }

// // //             // ═══════════════════════════════════════════════════════════════
// // //             //  SHEET 3: Invited Suppliers (buyer only)
// // //             // ═══════════════════════════════════════════════════════════════
// // //             // if (!supplierMode) {
// // //             //     XSSFSheet s3 = wb.createSheet("Invited Suppliers");
// // //             //     setWidths(s3, new int[]{1200, 5500, 4000, 5000, 4000, 3500, 4000, 4000});
// // //             //     row = 0;
// // //             //     row = addBanner(s3, row, "INVITED SUPPLIERS", titleStyle,
// // //             //             rfq.getRfqNumber() + "  [" + suppliers.size() + " suppliers]", 8);
// // //             //     String[] supCols = {"#","Company","Contact Person","Email","Phone","Status","Responded At","Quote Amount"};
// // //             //     addHeaderRow(s3, row++, hdrNavy, supCols);
// // //             //     sl = 1;
// // //             //     for (RFQSupplier rs : suppliers) {
// // //             //         Supplier sup = rs.getSupplier();
// // //             //         Row r = s3.createRow(row++);
// // //             //         CellStyle cs = alt(sl, valueStyle, altStyle);
// // //             //         str(r, 0, String.valueOf(sl++), cs);
// // //             //         str(r, 1, sup != null ? s(sup.getCompanyName())        : "N/A", cs);
// // //             //         str(r, 2, sup != null ? s(sup.getContactPersonName())  : "N/A", cs);
// // //             //         str(r, 3, sup != null ? s(sup.getContactPersonEmail()) : "N/A", cs);
// // //             //         str(r, 4, sup != null ? s(sup.getContactPersonPhone()) : "N/A", cs);
// // //             //         str(r, 5, s(rs.getStatus()), statusStyle);
// // //             //         str(r, 6, fmt(rs.getRespondedAt()), cs);
// // //             //         num(r, 7, rs.getQuoteAmount(), amtStyle);
// // //             //     }
// // //             // }
// // //                         if (!supplierMode) {
// // //                 XSSFSheet s3 = wb.createSheet("Invited Suppliers");
// // //                 setWidths(s3, new int[]{1200, 5500, 4000, 5000, 4000, 3500, 4000, 4000, 3800});
 
// // //                 row = 0;
// // //                 row = addBanner(s3, row, "INVITED SUPPLIERS", titleStyle,
// // //                         rfq.getRfqNumber() + "  [" + suppliers.size() + " suppliers]", 9);
 
// // //                 // ── Determine the awarded supplier (from first PO, if any) ─
// // //                 Long   awardedSupplierId   = null;
// // //                 String awardedPoNumber     = null;
// // //                 if (!pos.isEmpty() && pos.get(0).getSupplier() != null) {
// // //                     awardedSupplierId = pos.get(0).getSupplier().getId();
// // //                     awardedPoNumber   = pos.get(0).getPoNumber();
// // //                 }
// // //                 final Long   finalAwardedId = awardedSupplierId;
// // //                 final String finalPoNumber  = awardedPoNumber;
 
// // //                 // ── Awarded-row styles ────────────────────────────────────
// // //                 CellStyle awardRowStyle = makeAwardedRowStyle(wb);
// // //                 CellStyle awardAmtStyle = makeAwardedAmtStyle(wb);
 
// // //                 // ── Header ───────────────────────────────────────────────
// // //                 String[] supCols = {"#","Company","Contact Person","Email","Phone",
// // //                                     "Status","Responded At","Quote Amount","Award Status"};
// // //                 addHeaderRow(s3, row++, hdrNavy, supCols);
 
// // //                 // Freeze pane so header stays visible when scrolling
// // //                 s3.createFreezePane(0, row);
 
// // //                 sl = 1;
// // //                 for (RFQSupplier rs : suppliers) {
// // //                     Supplier sup = rs.getSupplier();
// // //                     Long supId   = sup != null ? sup.getId() : null;
// // //                     boolean isAwarded = finalAwardedId != null && finalAwardedId.equals(supId);
 
// // //                     Row r    = s3.createRow(row++);
// // //                     CellStyle cs  = isAwarded ? awardRowStyle : alt(sl, valueStyle, altStyle);
// // //                     CellStyle ams = isAwarded ? awardAmtStyle : amtStyle;
 
// // //                     str(r, 0, String.valueOf(sl++), cs);
// // //                     str(r, 1, sup != null ? s(sup.getCompanyName())        : "N/A", cs);
// // //                     str(r, 2, sup != null ? s(sup.getContactPersonName())  : "N/A", cs);
// // //                     str(r, 3, sup != null ? s(sup.getContactPersonEmail()) : "N/A", cs);
// // //                     str(r, 4, sup != null ? s(sup.getContactPersonPhone()) : "N/A", cs);
// // //                     str(r, 5, s(rs.getStatus()), isAwarded ? cs : statusStyle);
// // //                     str(r, 6, fmt(rs.getRespondedAt()), cs);
// // //                     num(r, 7, rs.getQuoteAmount(), ams);
 
// // //                     // Award Status column
// // //                     if (isAwarded) {
// // //                         String awardLabel = finalPoNumber != null
// // //                             ? "★ AWARDED  |  PO: " + finalPoNumber
// // //                             : "★ AWARDED";
// // //                         str(r, 8, awardLabel, cs);
// // //                     } else {
// // //                         str(r, 8, "—", cs);
// // //                     }
// // //                 }
 
// // //                 // ── Legend note at the bottom ─────────────────────────────
// // //                 if (finalAwardedId != null) {
// // //                     row++;
// // //                     Row legendRow = s3.createRow(row++);
// // //                     s3.addMergedRegion(new CellRangeAddress(row - 1, row - 1, 0, 8));
// // //                     Cell lc = legendRow.createCell(0);
// // //                     lc.setCellValue(
// // //                         "★  Gold row = supplier awarded the Purchase Order" +
// // //                         (finalPoNumber != null ? "  (PO: " + finalPoNumber + ")" : ""));
// // //                     lc.setCellStyle(awardRowStyle);
// // //                 }
// // //             }

// // //             // ═══════════════════════════════════════════════════════════════
// // //             //  SHEET 4: Approval History (buyer only)
// // //             // ═══════════════════════════════════════════════════════════════
// // //             if (!supplierMode) {
// // //                 XSSFSheet s4 = wb.createSheet("Approval History");
// // //                 setWidths(s4, new int[]{1200, 4000, 4000, 5000, 3500, 3500, 4000, 7000});
// // //                 row = 0;
// // //                 row = addBanner(s4, row, "APPROVAL HISTORY", titleStyle, rfq.getRfqNumber(), 8);
// // //                 String[] apCols = {"#","Level","Level Order","Approver","Email","Status","Action Date","Comments / Remarks"};
// // //                 addHeaderRow(s4, row++, hdrNavy, apCols);
// // //                 sl = 1;
// // //                 for (DynamicRFQApproval ap : approvals) {
// // //                     Row r = s4.createRow(row++);
// // //                     CellStyle cs = alt(sl, valueStyle, altStyle);
// // //                     str(r, 0, String.valueOf(sl++), cs);
// // //                     str(r, 1, ap.getHierarchyLevel() != null ? ap.getHierarchyLevel().getLevelName()           : "N/A", cs);
// // //                     str(r, 2, ap.getHierarchyLevel() != null ? String.valueOf(ap.getHierarchyLevel().getLevelOrder()) : "N/A", cs);
// // //                     str(r, 3, ap.getApproverUser()   != null ? ap.getApproverUser().getFullName()               : "N/A", cs);
// // //                     str(r, 4, ap.getApproverUser()   != null ? s(ap.getApproverUser().getEmail())               : "N/A", cs);
// // //                     str(r, 5, ap.getStatus()          != null ? ap.getStatus().name()                           : "N/A", statusStyle);
// // //                     str(r, 6, fmt(ap.getActionDate()), cs);
// // //                     String extra = s(ap.getComments());
// // //                     if (ap.getHoldRemarks()    != null) extra += " | HOLD: "    + ap.getHoldRemarks();
// // //                     if (ap.getRejectRemarks()  != null) extra += " | REJECT: "  + ap.getRejectRemarks();
// // //                     if (ap.getReleaseRemarks() != null) extra += " | RELEASE: " + ap.getReleaseRemarks();
// // //                     str(r, 7, extra, cs);
// // //                 }
// // //                 if (approvals.isEmpty()) {
// // //                     Row r = s4.createRow(row);
// // //                     str(r, 0, "No approval history recorded yet.", valueStyle);
// // //                 }
// // //             }

// // //             // ═══════════════════════════════════════════════════════════════
// // //             //  SHEET 5: Purchase Order overview (buyer only)
// // //             // ═══════════════════════════════════════════════════════════════
// // //             if (!supplierMode) {
// // //                 XSSFSheet s5 = wb.createSheet("Purchase Order");
// // //                 s5.setColumnWidth(0, 6000); s5.setColumnWidth(1, 9000);
// // //                 s5.setColumnWidth(2, 6000); s5.setColumnWidth(3, 9000);
// // //                 row = 0;

// // //                 if (pos.isEmpty()) {
// // //                     row = addBanner(s5, row, "PURCHASE ORDER", titleStyle,
// // //                             "No PO raised for this RFQ yet.", 4);
// // //                 } else {
// // //                     PurchaseOrder po = pos.get(0);
// // //                     row = addBanner(s5, row, "PURCHASE ORDER", titleStyle,
// // //                             "PO: " + s(po.getPoNumber())
// // //                             + "  |  Status: " + (po.getStatus() != null ? po.getStatus().name() : "—")
// // //                             + "  |  RFQ: " + rfq.getRfqNumber(), 4);

// // //                     row = addSectionHeader(s5, row, "PO INFORMATION", hdrGreen, 4);
// // //                     row = addLV(s5, row, labelStyle, valueStyle, "PO Number",      s(po.getPoNumber()),      "PO Date",           fmt(po.getPoDate()));
// // //                     row = addLV(s5, row, labelStyle, valueStyle, "Status",         po.getStatus() != null ? po.getStatus().name() : "—",
// // //                                                                                     "Approval Status",       po.getApprovalStatus() != null ? po.getApprovalStatus().name() : "—");
// // //                     row = addLV(s5, row, labelStyle, valueStyle, "Currency",       s(po.getCurrencyCode()) + " " + s(po.getCurrencySymbol()),
// // //                                                                                     "Amount in Words",       s(po.getAmountInWords()));
// // //                     row = addLV(s5, row, labelStyle, valueStyle, "Payment Terms",  s(po.getPaymentTerms()),  "Delivery Terms",    s(po.getDeliveryTerms()));
// // //                     row = addLV(s5, row, labelStyle, valueStyle, "Mode of Payment",s(po.getModeOfPayment()), "Dispatched Through",s(po.getDispatchedThrough()));
// // //                     row = addLV(s5, row, labelStyle, valueStyle, "Destination",    s(po.getDestination()),   "Reference Quote",   s(po.getReferenceQuoteNo()));
// // //                     row = addLV(s5, row, labelStyle, valueStyle, "Approved By",    s(po.getApprovedByName()),"Designation",       s(po.getApprovedByDesignation()));
// // //                     row = addLV(s5, row, labelStyle, valueStyle, "Approval Date",  fmt(po.getApprovalDate()),"Buyer Remarks",     s(po.getBuyerRemarks()));
// // //                     row++;

// // //                     if (po.getSupplier() != null) {
// // //                         Supplier sup = po.getSupplier();
// // //                         row = addSectionHeader(s5, row, "SUPPLIER", hdrGreen, 4);
// // //                         row = addLV(s5, row, labelStyle, valueStyle, "Company", s(sup.getCompanyName()),       "Email",   s(sup.getContactPersonEmail()));
// // //                         row = addLV(s5, row, labelStyle, valueStyle, "Contact", s(sup.getContactPersonName()), "Phone",   s(sup.getContactPersonPhone()));
// // //                         row++;
// // //                     }

// // //                     if (po.getDeliveryLocation() != null) {
// // //                         Location dl = po.getDeliveryLocation();
// // //                         row = addSectionHeader(s5, row, "DELIVERY LOCATION", hdrGreen, 4);
// // //                         row = addLV(s5, row, labelStyle, valueStyle, "Name",  dl.getLocationName(), "City",    s(dl.getCity()));
// // //                         row = addLV(s5, row, labelStyle, valueStyle, "State", s(dl.getState()),     "Country", s(dl.getCountry()));
// // //                         row++;
// // //                     }

// // //                     row = addSectionHeader(s5, row, "FINANCIAL SUMMARY", hdrGreen, 4);
// // //                     row = addLV(s5, row, labelStyle, valueStyle, "Subtotal",    s(po.getSubtotal()),    "Tax Amount",  s(po.getTaxAmount()));
// // //                     row = addLV(s5, row, labelStyle, valueStyle, "Grand Total", s(po.getGrandTotal()),  "Tax %",       s(po.getTaxPercentage()));
// // //                 }
// // //             }

// // //             // ═══════════════════════════════════════════════════════════════
// // //             //  SHEET 6: PO Line Items (buyer only — only if PO exists)
// // //             // ═══════════════════════════════════════════════════════════════
// // //             if (!supplierMode && !pos.isEmpty()) {
// // //                 PurchaseOrder po = pos.get(0);
// // //                 List<POLineItem> poLines = po.getLineItems();
// // //                 XSSFSheet s6 = wb.createSheet("PO Line Items");
// // //                 setWidths(s6, new int[]{1200, 3000, 7000, 2500, 2000, 2500, 4000, 2000, 2000, 4500, 4000});
// // //                 row = 0;
// // //                 row = addBanner(s6, row, "PO LINE ITEMS", titleStyle,
// // //                         s(po.getPoNumber()) + "  [" + poLines.size() + " items]  |  Currency: " + s(po.getCurrencyCode()), 11);
// // //                 String[] plCols = {"#","Item Code","Description","Specifications","UOM","Qty","Unit Rate","Disc %","Tax %","Line Total","Brand/Make/Model"};
// // //                 addHeaderRow(s6, row++, hdrGreen, plCols);
// // //                 sl = 1;
// // //                 for (POLineItem li : poLines) {
// // //                     Row r = s6.createRow(row++);
// // //                     CellStyle cs = alt(sl, valueStyle, altStyle);
// // //                     str(r, 0,  String.valueOf(sl++), cs);
// // //                     str(r, 1,  s(li.getItemCode()), cs);
// // //                     str(r, 2,  s(li.getItemDescription()), cs);
// // //                     str(r, 3,  s(li.getSpecifications()), cs);
// // //                     str(r, 4,  s(li.getUom()), cs);
// // //                     num(r, 5,  li.getQuantity(), amtStyle);
// // //                     num(r, 6,  li.getUnitRate(), amtStyle);
// // //                     num(r, 7,  li.getDiscountPercentage(), amtStyle);
// // //                     num(r, 8,  li.getTaxPercentage(), amtStyle);
// // //                     num(r, 9,  li.getLineTotal(), amtStyle);
// // //                     str(r, 10, s(li.getBrandMakeModel()), cs);
// // //                 }
// // //                 row++;
// // //                 Object[][] poTots = {
// // //                     {"Subtotal",    po.getSubtotal()},
// // //                     {"Tax Amount",  po.getTaxAmount()},
// // //                     {"GRAND TOTAL", po.getGrandTotal()}
// // //                 };
// // //                 for (int t = 0; t < poTots.length; t++) {
// // //                     Row tr = s6.createRow(row++);
// // //                     boolean ig = (t == poTots.length - 1);
// // //                     Cell lc = tr.createCell(9);  lc.setCellValue((String) poTots[t][0]); lc.setCellStyle(ig ? totalStyle : labelStyle);
// // //                     Cell vc = tr.createCell(10);
// // //                     if (poTots[t][1] instanceof BigDecimal) vc.setCellValue(((BigDecimal) poTots[t][1]).doubleValue()); else vc.setCellValue(0);
// // //                     vc.setCellStyle(ig ? totalStyle : amtStyle);
// // //                 }
// // //             }

// // //             // ═══════════════════════════════════════════════════════════════
// // //             //  SHEET 7: Invoice overview (buyer only)
// // //             // ═══════════════════════════════════════════════════════════════
// // //             if (!supplierMode) {
// // //                 XSSFSheet s7 = wb.createSheet("Invoice");
// // //                 s7.setColumnWidth(0, 6000); s7.setColumnWidth(1, 9000);
// // //                 s7.setColumnWidth(2, 6000); s7.setColumnWidth(3, 9000);
// // //                 row = 0;

// // //                 if (invList.isEmpty()) {
// // //                     row = addBanner(s7, row, "INVOICE", titleStyle,
// // //                             "No invoice submitted yet for this RFQ.", 4);
// // //                 } else {
// // //                     Invoice invoice = invList.get(0);
// // //                     row = addBanner(s7, row, "INVOICE", titleStyle,
// // //                             "Invoice: " + s(invoice.getInvoiceNumber())
// // //                             + "  |  Status: " + (invoice.getStatus() != null ? invoice.getStatus().name() : "—")
// // //                             + "  |  RFQ: " + rfq.getRfqNumber(), 4);

// // //                     row = addSectionHeader(s7, row, "INVOICE DETAILS", hdrPurple, 4);
// // //                     row = addLV(s7, row, labelStyle, valueStyle, "Invoice Number", s(invoice.getInvoiceNumber()),   "Status",          invoice.getStatus() != null ? invoice.getStatus().name() : "—");
// // //                     row = addLV(s7, row, labelStyle, valueStyle, "Invoice Date",   fmt(invoice.getInvoiceDate()),   "Due Date",        fmt(invoice.getDueDate()));
// // //                     row = addLV(s7, row, labelStyle, valueStyle, "PO Number",      s(invoice.getPoNumber()),        "Currency",        s(invoice.getCurrency()));
// // //                     row = addLV(s7, row, labelStyle, valueStyle, "Supplier",       s(invoice.getSupplierName()),    "Supplier Email",  s(invoice.getSupplierEmail()));
// // //                     row = addLV(s7, row, labelStyle, valueStyle, "Buyer Company",  s(invoice.getBuyerCompanyName()),"Payment Terms",   s(invoice.getPaymentTerms()));
// // //                     row = addLV(s7, row, labelStyle, valueStyle, "Subtotal",       s(invoice.getSubtotal()),        "Tax Amount",      s(invoice.getTaxAmount()));
// // //                     row = addLV(s7, row, labelStyle, valueStyle, "Total Amount",   s(invoice.getTotalAmount()),     "Approved By",     s(invoice.getApprovedRejectedBy()));
// // //                     row = addLV(s7, row, labelStyle, valueStyle, "Approval Date",  fmt(invoice.getApprovedRejectedAt()), "Approval Remarks", s(invoice.getApprovalRemarks()));
// // //                     row++;

// // //                     if (invoice.getBankName() != null) {
// // //                         row = addSectionHeader(s7, row, "BANK DETAILS", hdrPurple, 4);
// // //                         row = addLV(s7, row, labelStyle, valueStyle, "Bank Name",      s(invoice.getBankName()),          "Account Name",   s(invoice.getBankAccountName()));
// // //                         row = addLV(s7, row, labelStyle, valueStyle, "Account Number", s(invoice.getBankAccountNumber()), "IFSC Code",      s(invoice.getBankIfscCode()));
// // //                         row = addLV(s7, row, labelStyle, valueStyle, "SWIFT Code",     s(invoice.getBankSwiftCode()),     "Notes",          s(invoice.getNotes()));
// // //                         row++;
// // //                     }
// // //                 }
// // //             }

// // //             // ═══════════════════════════════════════════════════════════════
// // //             //  SHEET 8: Invoice Line Items (buyer only — only if invoice exists)
// // //             // ═══════════════════════════════════════════════════════════════
// // //             if (!supplierMode && !invList.isEmpty()) {
// // //                 Invoice invoice = invList.get(0);
// // //                 List<InvoiceLineItem> invLines = invoice.getLineItems();
// // //                 XSSFSheet s8 = wb.createSheet("Invoice Line Items");
// // //                 setWidths(s8, new int[]{1200, 3000, 7000, 2500, 2000, 2500, 4000, 2000, 2000, 4500});
// // //                 row = 0;
// // //                 row = addBanner(s8, row, "INVOICE LINE ITEMS", titleStyle,
// // //                         s(invoice.getInvoiceNumber()) + "  [" + invLines.size() + " items]", 10);
// // //                 String[] ilCols = {"#","Item Code","Description","HSN/SAC","UOM","Quantity","Unit Price","Disc %","Tax %","Line Total"};
// // //                 addHeaderRow(s8, row++, hdrPurple, ilCols);
// // //                 sl = 1;
// // //                 for (InvoiceLineItem li : invLines) {
// // //                     Row r = s8.createRow(row++);
// // //                     CellStyle cs = alt(sl, valueStyle, altStyle);
// // //                     str(r, 0, String.valueOf(sl++), cs);
// // //                     str(r, 1, s(li.getItemCode()), cs);
// // //                     str(r, 2, s(li.getItemDescription()), cs);
// // //                     str(r, 3, s(li.getHsnSacCode()), cs);
// // //                     str(r, 4, s(li.getUom()), cs);
// // //                     num(r, 5, li.getQuantity(), amtStyle);
// // //                     num(r, 6, li.getUnitPrice(), amtStyle);
// // //                     num(r, 7, li.getDiscountPercentage(), amtStyle);
// // //                     num(r, 8, li.getTaxPercentage(), amtStyle);
// // //                     num(r, 9, li.getLineTotal(), amtStyle);
// // //                 }
// // //                 row++;
// // //                 Object[][] invTots = {
// // //                     {"Subtotal",      invoice.getSubtotal()},
// // //                     {"Tax Amount",    invoice.getTaxAmount()},
// // //                     {"TOTAL AMOUNT",  invoice.getTotalAmount()}
// // //                 };
// // //                 for (int t = 0; t < invTots.length; t++) {
// // //                     Row tr = s8.createRow(row++);
// // //                     boolean ig = (t == invTots.length - 1);
// // //                     Cell lc = tr.createCell(8);  lc.setCellValue((String) invTots[t][0]); lc.setCellStyle(ig ? totalStyle : labelStyle);
// // //                     Cell vc = tr.createCell(9);
// // //                     if (invTots[t][1] instanceof BigDecimal) vc.setCellValue(((BigDecimal) invTots[t][1]).doubleValue()); else vc.setCellValue(0);
// // //                     vc.setCellStyle(ig ? totalStyle : amtStyle);
// // //                 }
// // //             }

// // //             // ═══════════════════════════════════════════════════════════════
// // //             //  SHEET 9: GRN overview (buyer only)
// // //             // ═══════════════════════════════════════════════════════════════
// // //             if (!supplierMode) {
// // //                 XSSFSheet s9 = wb.createSheet("GRN");
// // //                 s9.setColumnWidth(0, 6000); s9.setColumnWidth(1, 9000);
// // //                 s9.setColumnWidth(2, 6000); s9.setColumnWidth(3, 9000);
// // //                 row = 0;

// // //                 if (grns.isEmpty()) {
// // //                     row = addBanner(s9, row, "GOODS RECEIPT NOTE", titleStyle,
// // //                             "No GRN created yet for this RFQ.", 4);
// // //                 } else {
// // //                     GRN grn = grns.get(0);
// // //                     row = addBanner(s9, row, "GOODS RECEIPT NOTE", titleStyle,
// // //                             "GRN: " + s(grn.getGrnNumber())
// // //                             + "  |  Status: " + (grn.getStatus() != null ? grn.getStatus().name() : "—")
// // //                             + "  |  RFQ: " + rfq.getRfqNumber(), 4);

// // //                     row = addSectionHeader(s9, row, "GRN INFORMATION", hdrOrange, 4);
// // //                     row = addLV(s9, row, labelStyle, valueStyle, "GRN Number",    s(grn.getGrnNumber()),      "Status",         grn.getStatus() != null ? grn.getStatus().name() : "—");
// // //                     row = addLV(s9, row, labelStyle, valueStyle, "Received Date", fmt(grn.getReceivedDate()), "PO Number",      s(grn.getPoNumber()));
// // //                     row = addLV(s9, row, labelStyle, valueStyle, "Supplier",      s(grn.getSupplierName()),   "Invoice Number", s(grn.getInvoiceNumber()));
// // //                     row = addLV(s9, row, labelStyle, valueStyle, "Received By",   s(grn.getReceivedByName()), "QA Inspector",   s(grn.getInspectedByName()));
// // //                     row = addLV(s9, row, labelStyle, valueStyle, "Approved By",   s(grn.getApprovedByName()), "Approved At",    fmt(grn.getApprovedAt()));
// // //                     row = addLV(s9, row, labelStyle, valueStyle, "Transporter",   s(grn.getTransporterName()),"Vehicle No.",    s(grn.getVehicleNumber()));
// // //                     row = addLV(s9, row, labelStyle, valueStyle, "Challan No.",   s(grn.getDeliveryChallanNumber()), "LR Number", s(grn.getLrNumber()));
// // //                     row = addLV(s9, row, labelStyle, valueStyle, "Total Ordered", s(grn.getTotalOrderedValue()),  "Total Received", s(grn.getTotalReceivedValue()));
// // //                     row = addLV(s9, row, labelStyle, valueStyle, "Total Rejected",s(grn.getTotalRejectedValue()), "Remarks",        s(grn.getRemarks()));
// // //                     row++;
// // //                 }
// // //             }

// // //             // ═══════════════════════════════════════════════════════════════
// // //             //  SHEET 10: GRN Line Items (buyer only — only if GRN exists)
// // //             // ═══════════════════════════════════════════════════════════════
// // //             if (!supplierMode && !grns.isEmpty()) {
// // //                 GRN grn = grns.get(0);
// // //                 List<GRNLineItem> grnLines = grn.getLineItems();
// // //                 XSSFSheet s10 = wb.createSheet("GRN Line Items");
// // //                 setWidths(s10, new int[]{1200, 3000, 6500, 2000, 2500, 2500, 2500, 2500, 2500, 4000, 3500, 5000});
// // //                 row = 0;
// // //                 row = addBanner(s10, row, "GRN LINE ITEMS — QA RESULTS", titleStyle,
// // //                         s(grn.getGrnNumber()) + "  [" + grnLines.size() + " items]", 12);
// // //                 String[] glCols = {"#","Item Code","Description","UOM","Ordered","Received","Defective","Rejected","Accepted","PO Rate","Accepted Value","QA Remarks"};
// // //                 addHeaderRow(s10, row++, hdrOrange, glCols);
// // //                 sl = 1;
// // //                 for (GRNLineItem li : grnLines) {
// // //                     Row r = s10.createRow(row++);
// // //                     boolean hasRej = li.getRejectedQuantity() != null && li.getRejectedQuantity().compareTo(BigDecimal.ZERO) > 0;
// // //                     CellStyle cs  = hasRej ? warnStyle : alt(sl, valueStyle, altStyle);
// // //                     CellStyle ams = hasRej ? warnStyle : amtStyle;
// // //                     str(r, 0,  String.valueOf(sl++), cs);
// // //                     str(r, 1,  s(li.getItemCode()), cs);
// // //                     str(r, 2,  s(li.getItemDescription()), cs);
// // //                     str(r, 3,  s(li.getUom()), cs);
// // //                     num(r, 4,  li.getOrderedQuantity(), ams);
// // //                     num(r, 5,  li.getReceivedQuantity(), ams);
// // //                     num(r, 6,  li.getDefectiveQuantity(), ams);
// // //                     num(r, 7,  li.getRejectedQuantity(), ams);
// // //                     num(r, 8,  li.getAcceptedQuantity(), ams);
// // //                     num(r, 9,  li.getPoUnitRate(), ams);
// // //                     num(r, 10, li.getAcceptedValue(), ams);
// // //                     str(r, 11, s(li.getQaRemarks()), cs);
// // //                 }
// // //             }

// // //             // ═══════════════════════════════════════════════════════════════
// // //             //  SHEET 11: 3-Way Match overview (buyer only)
// // //             // ═══════════════════════════════════════════════════════════════
// // //             if (!supplierMode) {
// // //                 XSSFSheet s11 = wb.createSheet("3-Way Match");
// // //                 s11.setColumnWidth(0, 6000); s11.setColumnWidth(1, 9000);
// // //                 s11.setColumnWidth(2, 6000); s11.setColumnWidth(3, 9000);
// // //                 row = 0;

// // //                 if (matches.isEmpty()) {
// // //                     row = addBanner(s11, row, "3-WAY MATCH", titleStyle,
// // //                             "3-Way Match not yet performed for this RFQ.", 4);
// // //                 } else {
// // //                     ThreeWayMatch match = matches.get(0);
// // //                     boolean isFullMatch = match.getMatchStatus() == ThreeWayMatchStatus.MATCHED
// // //                             || match.getMatchStatus() == ThreeWayMatchStatus.OVERRIDDEN_APPROVED;

// // //                     CellStyle matchBanner = isFullMatch ? makeBannerMatchStyle(wb) : makeBannerWarnStyle(wb);

// // //                     row = addBannerCustomStyle(s11, row, "3-WAY MATCH REPORT", matchBanner,
// // //                             "Match #" + match.getId() + "  v" + match.getMatchVersion()
// // //                             + "  |  Status: " + (match.getMatchStatus() != null ? match.getMatchStatus().name() : "—")
// // //                             + "  |  RFQ: " + rfq.getRfqNumber(), 4);

// // //                     row = addSectionHeader(s11, row, "MATCH SUMMARY", hdrNavy, 4);
// // //                     row = addLV(s11, row, labelStyle, valueStyle, "Match Status",   match.getMatchStatus() != null ? match.getMatchStatus().name() : "—",
// // //                                                                                     "Match Version",    String.valueOf(match.getMatchVersion()));
// // //                     row = addLV(s11, row, labelStyle, valueStyle, "Tolerance %",    s(match.getTolerancePercentage()),  "Variance Amount",  s(match.getVarianceAmount()));
// // //                     row = addLV(s11, row, labelStyle, valueStyle, "Variance %",     s(match.getVariancePercentage()),   "Matched Lines",    String.valueOf(match.getTotalMatchedLines()));
// // //                     row = addLV(s11, row, labelStyle, valueStyle, "Mismatch Lines", String.valueOf(match.getTotalMismatchLines()), "Approved Payment", s(match.getApprovedPaymentAmount()));
// // //                     row = addLV(s11, row, labelStyle, valueStyle, "PO Total Value", s(match.getPoTotalValue()),         "GRN Accepted Value",s(match.getGrnAcceptedValue()));
// // //                     row = addLV(s11, row, labelStyle, valueStyle, "Invoice Total",  s(match.getInvoiceTotalValue()),    "Resolution",       match.getResolution() != null ? match.getResolution().name() : "PENDING");
// // //                     row = addLV(s11, row, labelStyle, valueStyle, "Resolved By",    s(match.getResolvedByName()),       "Resolved At",      fmt(match.getResolvedAt()));
// // //                     if (match.getResolutionRemarks() != null)
// // //                         row = addLV(s11, row, labelStyle, valueStyle, "Resolution Remarks", s(match.getResolutionRemarks()), "", "");
// // //                     if (match.getMatchSummary() != null) {
// // //                         row++;
// // //                         Row sumRow = s11.createRow(row++);
// // //                         s11.addMergedRegion(new CellRangeAddress(row - 1, row - 1, 0, 3));
// // //                         Cell sc = sumRow.createCell(0);
// // //                         sc.setCellValue(match.getMatchSummary());
// // //                         sc.setCellStyle(isFullMatch ? lowestStyle : warnStyle);
// // //                     }
// // //                 }
// // //             }

// // //             // ═══════════════════════════════════════════════════════════════
// // //             //  SHEET 12: Match Line Results (buyer only — only if match exists)
// // //             // ═══════════════════════════════════════════════════════════════
// // //             if (!supplierMode && !matches.isEmpty()) {
// // //                 ThreeWayMatch match = matches.get(0);
// // //                 List<MatchLineResult> lineResults = match.getLineResults();
// // //                 XSSFSheet s12 = wb.createSheet("Match Line Results");
// // //                 setWidths(s12, new int[]{1200, 3000, 6500, 2000, 2500, 2500, 2500, 3000, 3000, 3000, 3000, 3000, 3500, 6000});
// // //                 row = 0;
// // //                 row = addBanner(s12, row, "3-WAY MATCH — LINE RESULTS", titleStyle,
// // //                         "Invoice: " + s(match.getInvoiceNumber()) + " ↔ GRN: " + s(match.getGrnNumber())
// // //                         + "  |  " + lineResults.size() + " lines", 14);
// // //                 String[] mCols = {"#","Item Code","Description","UOM",
// // //                                   "PO Qty","GRN Accepted","Inv Qty","Qty Var%",
// // //                                   "PO Rate","Inv Price","Price Var%",
// // //                                   "GRN Value","Inv Value","Status"};
// // //                 addHeaderRow(s12, row++, hdrNavy, mCols);
// // //                 sl = 1;
// // //                 for (MatchLineResult lr : lineResults) {
// // //                     Row r = s12.createRow(row++);
// // //                     boolean lineOk   = lr.getLineMatchStatus() == MatchLineResult.LineMatchStatus.MATCHED;
// // //                     boolean lineFail = lr.getLineMatchStatus() == MatchLineResult.LineMatchStatus.NOT_IN_PO
// // //                             || lr.getLineMatchStatus() == MatchLineResult.LineMatchStatus.NOT_RECEIVED;
// // //                     CellStyle cs  = lineFail ? warnStyle : lineOk ? lowestStyle : alt(sl, valueStyle, altStyle);
// // //                     CellStyle ams = lineFail ? warnStyle : lineOk ? lowestStyle : amtStyle;
// // //                     str(r, 0,  String.valueOf(sl++), cs);
// // //                     str(r, 1,  s(lr.getItemCode()), cs);
// // //                     str(r, 2,  s(lr.getItemDescription()), cs);
// // //                     str(r, 3,  s(lr.getUom()), cs);
// // //                     num(r, 4,  lr.getPoOrderedQuantity(), ams);
// // //                     num(r, 5,  lr.getGrnAcceptedQuantity(), ams);
// // //                     num(r, 6,  lr.getInvoiceQuantity(), ams);
// // //                     num(r, 7,  lr.getQuantityVariancePct(), ams);
// // //                     num(r, 8,  lr.getPoUnitRate(), ams);
// // //                     num(r, 9,  lr.getInvoiceUnitPrice(), ams);
// // //                     num(r, 10, lr.getPriceVariancePct(), ams);
// // //                     num(r, 11, lr.getGrnAcceptedValue(), ams);
// // //                     num(r, 12, lr.getInvoiceLineValue(), ams);
// // //                     str(r, 13, lr.getLineMatchStatus() != null ? lr.getLineMatchStatus().name() : "—", statusStyle);
// // //                 }
// // //             }

// // //             wb.write(out);
// // //             return out.toByteArray();
// // //         }
// // //     }

// // //     // =========================================================================
// // //     //  2.  RFQ LIST REPORT
// // //     // =========================================================================
// // //     @Transactional(readOnly = true)
// // //     public byte[] generateRFQListExcel(Long buyerId, String statusFilter) throws Exception {

// // //         List<RFQ> rfqs = rfqRepository.findByBuyerId(buyerId);
// // //         if (statusFilter != null && !statusFilter.isBlank() && !statusFilter.equalsIgnoreCase("ALL")) {
// // //             final String sf = statusFilter.trim().toUpperCase();
// // //             rfqs = rfqs.stream()
// // //                     .filter(r -> sf.equals(r.getStatus() != null ? r.getStatus().name() : ""))
// // //                     .collect(Collectors.toList());
// // //         }

// // //         try (XSSFWorkbook wb = new XSSFWorkbook();
// // //              ByteArrayOutputStream out = new ByteArrayOutputStream()) {

// // //             CellStyle titleStyle  = makeTitleStyle(wb);
// // //             CellStyle hdrNavy    = makeHeaderStyle(wb, rgb(0x1e, 0x40, 0x88));
// // //             CellStyle valueStyle  = makeValueStyle(wb);
// // //             CellStyle altStyle    = makeAltRowStyle(wb);
// // //             CellStyle amtStyle    = makeAmountStyle(wb);
// // //             CellStyle statusStyle = makeStatusStyle(wb);
// // //             CellStyle statLbl    = makeStatLabelStyle(wb);
// // //             CellStyle statVal    = makeStatValueStyle(wb);

// // //             XSSFSheet sheet = wb.createSheet("RFQ List");
// // //             setWidths(sheet, new int[]{1200, 3500, 7000, 3500, 3000, 3000, 3000, 2500, 2500, 3500, 5000});

// // //             int row = 0;
// // //             String filterDesc = (statusFilter != null && !statusFilter.isBlank() && !statusFilter.equalsIgnoreCase("ALL"))
// // //                     ? "  |  Status: " + statusFilter : "  |  All Statuses";
// // //             row = addBanner(sheet, row, "RFQ LIST REPORT", titleStyle,
// // //                     "Generated: " + LocalDateTime.now().format(DTTM_FMT) + filterDesc, 11);

// // //             long cntDraft     = rfqs.stream().filter(r -> "DRAFT".equals(en(r.getStatus()))).count();
// // //             long cntAwaiting  = rfqs.stream().filter(r -> "AWAITING_APPROVAL".equals(en(r.getStatus()))).count();
// // //             long cntPublished = rfqs.stream().filter(r -> "PUBLISHED".equals(en(r.getStatus()))).count();
// // //             long cntClosed    = rfqs.stream().filter(r -> "CLOSED".equals(en(r.getStatus()))).count();
// // //             long cntHold      = rfqs.stream().filter(r -> "HOLD".equals(en(r.getStatus()))).count();

// // //             Row sr = sheet.createRow(row++);
// // //             sr.setHeightInPoints(22);
// // //             str(sr, 0,  "Total RFQs: " + rfqs.size(), statLbl);
// // //             str(sr, 2,  "Draft: "      + cntDraft,     statVal);
// // //             str(sr, 4,  "Awaiting: "   + cntAwaiting,  statVal);
// // //             str(sr, 6,  "Published: "  + cntPublished,  statVal);
// // //             str(sr, 8,  "Closed: "     + cntClosed,    statVal);
// // //             str(sr, 10, "On Hold: "    + cntHold,      statVal);
// // //             row++;

// // //             String[] cols = {"#","RFQ Number","Title","Status","Priority",
// // //                     "Issue Date","Due Date","Suppliers","Items","Approval Status","Created By"};
// // //             addHeaderRow(sheet, row++, hdrNavy, cols);
// // //             sheet.setAutoFilter(new CellRangeAddress(row - 1, row - 1, 0, cols.length - 1));

// // //             int sl = 1;
// // //             for (RFQ rfq : rfqs) {
// // //                 Row r = sheet.createRow(row++);
// // //                 CellStyle cs = alt(sl, valueStyle, altStyle);
// // //                 str(r, 0, String.valueOf(sl++), cs);
// // //                 str(r, 1, s(rfq.getRfqNumber()), cs);
// // //                 str(r, 2, s(rfq.getRfqTitle()), cs);
// // //                 str(r, 3, en(rfq.getStatus()), statusStyle);
// // //                 str(r, 4, en(rfq.getPriority()), cs);
// // //                 str(r, 5, fmt(rfq.getIssueDate()), cs);
// // //                 str(r, 6, fmt(rfq.getDueDate()), cs);
// // //                 int supCnt = 0; try { supCnt = rfq.getSelectedSuppliers() != null ? rfq.getSelectedSuppliers().size() : 0; } catch (Exception ignored) {}
// // //                 int itmCnt = 0; try { itmCnt = rfq.getItems() != null ? rfq.getItems().size() : 0; } catch (Exception ignored) {}
// // //                 str(r, 7, String.valueOf(supCnt), cs);
// // //                 str(r, 8, String.valueOf(itmCnt), cs);
// // //                 str(r, 9, en(rfq.getApprovalStatus()), statusStyle);
// // //                 str(r, 10, rfq.getCreatedByUser() != null
// // //                         ? rfq.getCreatedByUser().getFirstName() + " " + rfq.getCreatedByUser().getLastName()
// // //                         : "N/A", cs);
// // //             }

// // //             wb.write(out);
// // //             return out.toByteArray();
// // //         }
// // //     }

// // //     // =========================================================================
// // //     //  3.  QUOTE COMPARISON EXCEL
// // //     // =========================================================================
// // //     @Transactional(readOnly = true)
// // //     public byte[] generateQuoteComparisonExcel(Long rfqId) throws Exception {

// // //         RFQ rfq = rfqRepository.findByIdWithAllDetails(rfqId)
// // //                 .orElseThrow(() -> new RuntimeException("RFQ not found: " + rfqId));

// // //         List<RFQItem>           items     = rfqItemRepository.findByRfqId(rfqId);
// // //         List<RFQSupplier>       suppliers = rfqSupplierRepository.findByRFQAndStatus(rfqId, "RESPONDED");
// // //         List<SupplierQuoteItem> quotes    = quoteItemRepository.findByRfqId(rfqId);
// // //         String currency = rfq.getLocation() != null ? rfq.getLocation().getCurrencyCode() : "INR";

// // //         try (XSSFWorkbook wb = new XSSFWorkbook();
// // //              ByteArrayOutputStream out = new ByteArrayOutputStream()) {

// // //             CellStyle titleStyle  = makeTitleStyle(wb);
// // //             CellStyle hdrNavy    = makeHeaderStyle(wb, rgb(0x1e, 0x40, 0x88));
// // //             CellStyle hdrGreen   = makeHeaderStyle(wb, rgb(0x1b, 0x5e, 0x20));
// // //             CellStyle valueStyle  = makeValueStyle(wb);
// // //             CellStyle altStyle    = makeAltRowStyle(wb);
// // //             CellStyle amtStyle    = makeAmountStyle(wb);
// // //             CellStyle lowestStyle = makeLowestStyle(wb);
// // //             CellStyle totalStyle  = makeTotalStyle(wb);
// // //             CellStyle metaStyle   = makeMetaStyle(wb);

// // //             XSSFSheet sheet = wb.createSheet("Quote Comparison");

// // //             int numSup  = suppliers.size();
// // //             int fixedCols = 6;
// // //             int supCols   = 7;
// // //             int totalCols = fixedCols + numSup * supCols;

// // //             sheet.setColumnWidth(0, 1200); sheet.setColumnWidth(1, 3500);
// // //             sheet.setColumnWidth(2, 7000); sheet.setColumnWidth(3, 6000);
// // //             sheet.setColumnWidth(4, 2500); sheet.setColumnWidth(5, 2500);
// // //             for (int s = 0; s < numSup; s++) {
// // //                 int b = fixedCols + s * supCols;
// // //                 sheet.setColumnWidth(b,     3000); sheet.setColumnWidth(b + 1, 4500);
// // //                 sheet.setColumnWidth(b + 2, 2500); sheet.setColumnWidth(b + 3, 3000);
// // //                 sheet.setColumnWidth(b + 4, 3000); sheet.setColumnWidth(b + 5, 4000);
// // //                 sheet.setColumnWidth(b + 6, 5000);
// // //             }

// // //             int row = 0;
// // //             row = addBanner(sheet, row, "QUOTE COMPARISON MATRIX", titleStyle,
// // //                     rfq.getRfqNumber() + "  —  " + rfq.getRfqTitle()
// // //                             + "  |  Currency: " + currency
// // //                             + "  |  Suppliers: " + numSup
// // //                             + "  |  Generated: " + LocalDateTime.now().format(DTTM_FMT), totalCols);

// // //             Row meta = sheet.createRow(row++);
// // //             str(meta, 0, "Buyer: " + (rfq.getBuyer() != null ? rfq.getBuyer().getCompanyName() : "N/A"), metaStyle);
// // //             str(meta, 3, "Due Date: " + fmt(rfq.getDueDate()), metaStyle);
// // //             str(meta, 6, "Items: " + items.size(), metaStyle);
// // //             row++;

// // //             Row supHdrRow = sheet.createRow(row++);
// // //             str(supHdrRow, 0, "Sl No",       hdrNavy); str(supHdrRow, 1, "Item Code",     hdrNavy);
// // //             str(supHdrRow, 2, "Item Description", hdrNavy); str(supHdrRow, 3, "Specifications", hdrNavy);
// // //             str(supHdrRow, 4, "Qty",          hdrNavy); str(supHdrRow, 5, "UOM",           hdrNavy);
// // //             for (int s = 0; s < numSup; s++) {
// // //                 RFQSupplier rs = suppliers.get(s);
// // //                 String sName = rs.getSupplier() != null ? rs.getSupplier().getCompanyName() : "Supplier " + (s + 1);
// // //                 String sCurr = rs.getSupplier() != null ? determineSupplierCurrency(rfq, rs.getSupplier()) : currency;
// // //                 int b = fixedCols + s * supCols;
// // //                 Cell sc = supHdrRow.createCell(b);
// // //                 sc.setCellValue(sName + "  [" + sCurr + "]");
// // //                 sc.setCellStyle(hdrGreen);
// // //                 sheet.addMergedRegion(new CellRangeAddress(row - 1, row - 1, b, b + supCols - 1));
// // //             }

// // //             Row subRow = sheet.createRow(row++);
// // //             for (int c = 0; c < fixedCols; c++) subRow.createCell(c).setCellStyle(hdrNavy);
// // //             for (int s = 0; s < numSup; s++) {
// // //                 int b = fixedCols + s * supCols;
// // //                 String[] sub = {"Unit Rate","Grand Total","Tax %","Delivery (d)","Warranty (m)","Brand/Make/Model","Remarks"};
// // //                 for (int c = 0; c < sub.length; c++) {
// // //                     Cell cell = subRow.createCell(b + c);
// // //                     cell.setCellValue(sub[c]);
// // //                     cell.setCellStyle(hdrNavy);
// // //                 }
// // //             }

// // //             int sl = 1;
// // //             for (RFQItem item : items) {
// // //                 double lowestRate = Double.MAX_VALUE; Long lowestSupId = null;
// // //                 for (RFQSupplier rs : suppliers) {
// // //                     if (rs.getSupplier() == null) continue;
// // //                     Optional<SupplierQuoteItem> qOpt = findQuote(quotes, item.getId(), rs.getSupplier().getId());
// // //                     if (qOpt.isPresent() && qOpt.get().getUnitRate() != null
// // //                             && qOpt.get().getUnitRate().doubleValue() < lowestRate) {
// // //                         lowestRate = qOpt.get().getUnitRate().doubleValue();
// // //                         lowestSupId = rs.getSupplier().getId();
// // //                     }
// // //                 }
// // //                 final Long lowestId = lowestSupId;

// // //                 Row r = sheet.createRow(row++);
// // //                 CellStyle cs = alt(sl, valueStyle, altStyle);
// // //                 str(r, 0, String.valueOf(sl++), cs); str(r, 1, s(item.getItemCode()), cs);
// // //                 str(r, 2, s(item.getItemDescription()), cs); str(r, 3, s(item.getSpecifications()), cs);
// // //                 num(r, 4, item.getQuantity(), amtStyle); str(r, 5, s(item.getUom()), cs);

// // //                 for (int idx = 0; idx < numSup; idx++) {
// // //                     RFQSupplier rs = suppliers.get(idx);
// // //                     if (rs.getSupplier() == null) continue;
// // //                     Long sid = rs.getSupplier().getId();
// // //                     int  b   = fixedCols + idx * supCols;
// // //                     boolean isLowest = sid.equals(lowestId);
// // //                     CellStyle qs = isLowest ? lowestStyle : amtStyle;
// // //                     CellStyle ts = isLowest ? lowestStyle : valueStyle;

// // //                     Optional<SupplierQuoteItem> qOpt = findQuote(quotes, item.getId(), sid);
// // //                     if (qOpt.isPresent()) {
// // //                         SupplierQuoteItem qi = qOpt.get();
// // //                         num(r, b,     qi.getUnitRate(), qs);   num(r, b + 1, qi.getGrandTotal(), qs);
// // //                         num(r, b + 2, qi.getTaxPercentage(), ts);
// // //                         str(r, b + 3, qi.getDeliveryDays()   != null ? qi.getDeliveryDays()   + "d" : "—", ts);
// // //                         str(r, b + 4, qi.getWarrantyMonths() != null ? qi.getWarrantyMonths() + "m" : "—", ts);
// // //                         str(r, b + 5, joinNonNull("/", qi.getBrandOffered(), qi.getMakeModel()), ts);
// // //                         str(r, b + 6, s(qi.getRemarks()), ts);
// // //                     } else {
// // //                         for (int c = 0; c < supCols; c++) { Cell nc = r.createCell(b + c); nc.setCellValue("—"); nc.setCellStyle(valueStyle); }
// // //                     }
// // //                 }
// // //             }

// // //             Row totRow = sheet.createRow(row++);
// // //             Cell totLbl = totRow.createCell(0); totLbl.setCellValue("GRAND TOTAL"); totLbl.setCellStyle(totalStyle);
// // //             sheet.addMergedRegion(new CellRangeAddress(row - 1, row - 1, 0, fixedCols - 1));
// // //             for (int c = 1; c < fixedCols; c++) totRow.createCell(c).setCellStyle(totalStyle);

// // //             Long lowestTotalSid = null; double lowestTotalAmt = Double.MAX_VALUE;
// // //             for (RFQSupplier rs : suppliers) {
// // //                 if (rs.getSupplier() == null) continue;
// // //                 double tot = sumGrandTotal(quotes, rs.getSupplier().getId());
// // //                 if (tot > 0 && tot < lowestTotalAmt) { lowestTotalAmt = tot; lowestTotalSid = rs.getSupplier().getId(); }
// // //             }
// // //             final Long lowestTotId = lowestTotalSid;

// // //             for (int idx = 0; idx < numSup; idx++) {
// // //                 RFQSupplier rs = suppliers.get(idx);
// // //                 if (rs.getSupplier() == null) continue;
// // //                 Long sid = rs.getSupplier().getId();
// // //                 int b = fixedCols + idx * supCols;
// // //                 boolean isLowest = sid.equals(lowestTotId);
// // //                 CellStyle fs = isLowest ? lowestStyle : totalStyle;
// // //                 double total = sumGrandTotal(quotes, sid);
// // //                 for (int c = 0; c < supCols; c++) {
// // //                     Cell nc = totRow.createCell(b + c);
// // //                     if (c == 1) nc.setCellValue(total);
// // //                     else if (c == 0) nc.setCellValue(isLowest ? "★ LOWEST" : "");
// // //                     nc.setCellStyle(fs);
// // //                 }
// // //             }

// // //             sheet.setAutoFilter(new CellRangeAddress(5, row - 2, 0, totalCols - 1));
// // //             wb.write(out);
// // //             return out.toByteArray();
// // //         }
// // //     }

// // //     // =========================================================================
// // //     //  4.  INVOICE EXCEL  — standalone single-invoice report
// // //     // =========================================================================
// // //     @Transactional(readOnly = true)
// // //     public byte[] generateInvoiceExcel(Long invoiceId) throws Exception {

// // //         Invoice invoice = invoiceRepository.findById(invoiceId)
// // //                 .orElseThrow(() -> new RuntimeException("Invoice not found: " + invoiceId));

// // //         List<InvoiceLineItem> lineItems = invoice.getLineItems();

// // //         try (XSSFWorkbook wb = new XSSFWorkbook();
// // //              ByteArrayOutputStream out = new ByteArrayOutputStream()) {

// // //             CellStyle titleStyle = makeTitleStyle(wb);
// // //             CellStyle hdrNavy   = makeHeaderStyle(wb, rgb(0x1e, 0x40, 0x88));
// // //             CellStyle hdrGreen  = makeHeaderStyle(wb, rgb(0x1b, 0x5e, 0x20));
// // //             CellStyle labelStyle = makeLabelStyle(wb);
// // //             CellStyle valueStyle = makeValueStyle(wb);
// // //             CellStyle amtStyle  = makeAmountStyle(wb);
// // //             CellStyle altStyle  = makeAltRowStyle(wb);
// // //             CellStyle totalStyle = makeTotalStyle(wb);

// // //             XSSFSheet sheet = wb.createSheet("Invoice");
// // //             setWidths(sheet, new int[]{6000, 9000, 6000, 9000});

// // //             int row = 0;
// // //             row = addBanner(sheet, row, "INVOICE", titleStyle,
// // //                     s(invoice.getInvoiceNumber()) + "  |  Generated: " + LocalDateTime.now().format(DTTM_FMT), 4);

// // //             row = addSectionHeader(sheet, row, "INVOICE DETAILS", hdrNavy, 4);
// // //             row = addLV(sheet, row, labelStyle, valueStyle,
// // //                     "Invoice Number", invoice.getInvoiceNumber(),
// // //                     "Status",         invoice.getStatus() != null ? invoice.getStatus().name() : "—");
// // //             row = addLV(sheet, row, labelStyle, valueStyle,
// // //                     "Invoice Date",   fmt(invoice.getInvoiceDate()),
// // //                     "Due Date",       fmt(invoice.getDueDate()));
// // //             row = addLV(sheet, row, labelStyle, valueStyle,
// // //                     "PO Number",      s(invoice.getPoNumber()),
// // //                     "RFQ Number",     s(invoice.getRfqNumber()));
// // //             row = addLV(sheet, row, labelStyle, valueStyle,
// // //                     "Currency",       s(invoice.getCurrency()),
// // //                     "Payment Terms",  s(invoice.getPaymentTerms()));
// // //             row++;

// // //             row = addSectionHeader(sheet, row, "SUPPLIER (FROM)", hdrNavy, 4);
// // //             row = addLV(sheet, row, labelStyle, valueStyle,
// // //                     "Supplier Name",  s(invoice.getSupplierName()),
// // //                     "Email",          s(invoice.getSupplierEmail()));
// // //             row++;

// // //             row = addSectionHeader(sheet, row, "BUYER (BILL TO)", hdrNavy, 4);
// // //             row = addLV(sheet, row, labelStyle, valueStyle,
// // //                     "Buyer Company",  s(invoice.getBuyerCompanyName()),
// // //                     "RFQ Creator",    s(invoice.getRfqCreatorName()));
// // //             row++;

// // //             setWidths(sheet, new int[]{1200, 3000, 7000, 2500, 2000, 2500, 4000, 2000, 2000, 4500});
// // //             row = addSectionHeader(sheet, row, "LINE ITEMS (" + lineItems.size() + ")", hdrGreen, 4);
// // //             String[] cols = {"#", "Item Code", "Description", "HSN/SAC", "UOM",
// // //                              "Quantity", "Unit Price", "Disc %", "Tax %", "Line Total"};
// // //             addHeaderRow(sheet, row++, hdrNavy, cols);

// // //             int sl = 1;
// // //             for (InvoiceLineItem li : lineItems) {
// // //                 Row r = sheet.createRow(row++);
// // //                 CellStyle cs = alt(sl, valueStyle, altStyle);
// // //                 str(r, 0, String.valueOf(sl++), cs);
// // //                 str(r, 1, s(li.getItemCode()), cs);
// // //                 str(r, 2, s(li.getItemDescription()), cs);
// // //                 str(r, 3, s(li.getHsnSacCode()), cs);
// // //                 str(r, 4, s(li.getUom()), cs);
// // //                 num(r, 5, li.getQuantity(), amtStyle);
// // //                 num(r, 6, li.getUnitPrice(), amtStyle);
// // //                 num(r, 7, li.getDiscountPercentage(), amtStyle);
// // //                 num(r, 8, li.getTaxPercentage(), amtStyle);
// // //                 num(r, 9, li.getLineTotal(), amtStyle);
// // //             }

// // //             row++;
// // //             Object[][] totals = {
// // //                 {"Subtotal (Pre-tax)", invoice.getSubtotal()},
// // //                 {"Total Tax",          invoice.getTaxAmount()},
// // //                 {"Grand Total",        invoice.getTotalAmount()}
// // //             };
// // //             for (int t = 0; t < totals.length; t++) {
// // //                 Row tr  = sheet.createRow(row++);
// // //                 boolean isGrand = (t == totals.length - 1);
// // //                 Cell lc = tr.createCell(8);
// // //                 lc.setCellValue((String) totals[t][0]);
// // //                 lc.setCellStyle(isGrand ? totalStyle : labelStyle);
// // //                 Cell vc = tr.createCell(9);
// // //                 if (totals[t][1] instanceof BigDecimal)
// // //                     vc.setCellValue(((BigDecimal) totals[t][1]).doubleValue());
// // //                 else
// // //                     vc.setCellValue(0);
// // //                 vc.setCellStyle(isGrand ? totalStyle : amtStyle);
// // //             }

// // //             if (invoice.getBankName() != null) {
// // //                 row++;
// // //                 row = addSectionHeader(sheet, row, "BANK DETAILS", hdrNavy, 4);
// // //                 row = addLV(sheet, row, labelStyle, valueStyle,
// // //                         "Bank Name",       s(invoice.getBankName()),
// // //                         "Account Name",    s(invoice.getBankAccountName()));
// // //                 row = addLV(sheet, row, labelStyle, valueStyle,
// // //                         "Account Number",  s(invoice.getBankAccountNumber()),
// // //                         "IFSC Code",       s(invoice.getBankIfscCode()));
// // //                 addLV(sheet, row, labelStyle, valueStyle,
// // //                         "SWIFT Code",      s(invoice.getBankSwiftCode()), "", "");
// // //             }

// // //             if (invoice.getPaymentTerms() != null || invoice.getNotes() != null) {
// // //                 row++;
// // //                 row = addSectionHeader(sheet, row, "TERMS & NOTES", hdrNavy, 4);
// // //                 row = addLV(sheet, row, labelStyle, valueStyle,
// // //                         "Payment Terms", s(invoice.getPaymentTerms()),
// // //                         "Notes",         s(invoice.getNotes()));
// // //                 if (invoice.getTermsAndConditions() != null)
// // //                     addLV(sheet, row, labelStyle, valueStyle,
// // //                             "Terms & Conditions", s(invoice.getTermsAndConditions()), "", "");
// // //             }

// // //             if (invoice.getApprovedRejectedBy() != null) {
// // //                 row++;
// // //                 row = addSectionHeader(sheet, row, "APPROVAL INFORMATION", hdrNavy, 4);
// // //                 row = addLV(sheet, row, labelStyle, valueStyle,
// // //                         "Action By",   s(invoice.getApprovedRejectedBy()),
// // //                         "Action Date", fmt(invoice.getApprovedRejectedAt()));
// // //                 if (invoice.getApprovalRemarks() != null)
// // //                     addLV(sheet, row, labelStyle, valueStyle,
// // //                             "Remarks", s(invoice.getApprovalRemarks()), "", "");
// // //             }

// // //             if (invoice.getStatus() == Invoice.InvoiceStatus.PAID) {
// // //                 row++;
// // //                 row = addSectionHeader(sheet, row, "PAYMENT INFORMATION", hdrNavy, 4);
// // //                 addLV(sheet, row, labelStyle, valueStyle,
// // //                         "Paid By",   s(invoice.getPaidBy()),
// // //                         "Paid At",   fmt(invoice.getPaidAt()));
// // //                 if (invoice.getPaymentReference() != null)
// // //                     addLV(sheet, row + 1, labelStyle, valueStyle,
// // //                             "Payment Reference", s(invoice.getPaymentReference()), "", "");
// // //             }

// // //             wb.write(out);
// // //             return out.toByteArray();
// // //         }
// // //     }

// // //     // =========================================================================
// // //     //  5.  HTML generators (PDF) — keep existing implementations
// // //     // =========================================================================
// // //     @Transactional(readOnly = true)
// // //     public String generateRFQSummaryHTML(Long rfqId) {
// // //         throw new UnsupportedOperationException("Keep existing generateRFQSummaryHTML implementation");
// // //     }

// // //     @Transactional(readOnly = true)
// // //     public String generateQuoteComparisonHTML(Long rfqId) {
// // //         throw new UnsupportedOperationException("Keep existing generateQuoteComparisonHTML implementation");
// // //     }

// // //     // =========================================================================
// // //     //  PRIVATE: cell style factory methods
// // //     // =========================================================================

// // //     private XSSFCellStyle makeTitleStyle(XSSFWorkbook wb) {
// // //         XSSFCellStyle s = wb.createCellStyle();
// // //         XSSFFont f = wb.createFont(); f.setBold(true); f.setFontHeightInPoints((short) 14);
// // //         f.setColor(new XSSFColor(new byte[]{(byte)0xff,(byte)0xff,(byte)0xff}, null));
// // //         s.setFont(f);
// // //         s.setFillForegroundColor(new XSSFColor(rgb(0x1e, 0x40, 0x88), null));
// // //         s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
// // //         s.setAlignment(HorizontalAlignment.CENTER); s.setVerticalAlignment(VerticalAlignment.CENTER);
// // //         s.setWrapText(true);
// // //         return s;
// // //     }

// // //     private XSSFCellStyle makeHeaderStyle(XSSFWorkbook wb, byte[] rgb) {
// // //         XSSFCellStyle s = wb.createCellStyle();
// // //         XSSFFont f = wb.createFont(); f.setBold(true); f.setFontHeightInPoints((short) 10);
// // //         f.setColor(new XSSFColor(new byte[]{(byte)0xff,(byte)0xff,(byte)0xff}, null));
// // //         s.setFont(f);
// // //         s.setFillForegroundColor(new XSSFColor(rgb, null)); s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
// // //         s.setBorderBottom(BorderStyle.THIN); s.setBorderTop(BorderStyle.THIN);
// // //         s.setBorderLeft(BorderStyle.THIN);   s.setBorderRight(BorderStyle.THIN);
// // //         s.setWrapText(true);
// // //         return s;
// // //     }

// // //     private XSSFCellStyle makeLabelStyle(XSSFWorkbook wb) {
// // //         XSSFCellStyle s = wb.createCellStyle();
// // //         XSSFFont f = wb.createFont(); f.setBold(true); f.setFontHeightInPoints((short) 9); s.setFont(f);
// // //         s.setFillForegroundColor(new XSSFColor(rgb(0xf0, 0xf4, 0xff), null));
// // //         s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
// // //         s.setBorderBottom(BorderStyle.THIN); s.setBorderRight(BorderStyle.THIN);
// // //         return s;
// // //     }

// // //     private XSSFCellStyle makeValueStyle(XSSFWorkbook wb) {
// // //         XSSFCellStyle s = wb.createCellStyle(); s.setBorderBottom(BorderStyle.THIN); s.setWrapText(true); return s;
// // //     }

// // //     private XSSFCellStyle makeAltRowStyle(XSSFWorkbook wb) {
// // //         XSSFCellStyle s = wb.createCellStyle();
// // //         s.setFillForegroundColor(new XSSFColor(rgb(0xf5, 0xf7, 0xfb), null));
// // //         s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
// // //         s.setBorderBottom(BorderStyle.THIN); s.setWrapText(true); return s;
// // //     }

// // //     private XSSFCellStyle makeAmountStyle(XSSFWorkbook wb) {
// // //         XSSFCellStyle s = wb.createCellStyle(); s.setAlignment(HorizontalAlignment.RIGHT);
// // //         s.setDataFormat(wb.createDataFormat().getFormat("#,##0.00")); s.setBorderBottom(BorderStyle.THIN); return s;
// // //     }

// // //     private XSSFCellStyle makeLowestStyle(XSSFWorkbook wb) {
// // //         XSSFCellStyle s = wb.createCellStyle();
// // //         XSSFFont f = wb.createFont(); f.setBold(true); f.setFontHeightInPoints((short) 9);
// // //         f.setColor(new XSSFColor(rgb(0x1a, 0x5c, 0x1a), null)); s.setFont(f);
// // //         s.setFillForegroundColor(new XSSFColor(rgb(0xd4, 0xed, 0xda), null));
// // //         s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
// // //         s.setAlignment(HorizontalAlignment.RIGHT);
// // //         s.setDataFormat(wb.createDataFormat().getFormat("#,##0.00")); s.setBorderBottom(BorderStyle.THIN); return s;
// // //     }

// // //     private XSSFCellStyle makeTotalStyle(XSSFWorkbook wb) {
// // //         XSSFCellStyle s = wb.createCellStyle();
// // //         XSSFFont f = wb.createFont(); f.setBold(true); f.setFontHeightInPoints((short) 11);
// // //         f.setColor(new XSSFColor(new byte[]{(byte)0xff,(byte)0xff,(byte)0xff}, null)); s.setFont(f);
// // //         s.setFillForegroundColor(new XSSFColor(rgb(0x1e, 0x40, 0x88), null));
// // //         s.setFillPattern(FillPatternType.SOLID_FOREGROUND); s.setAlignment(HorizontalAlignment.RIGHT);
// // //         s.setDataFormat(wb.createDataFormat().getFormat("#,##0.00")); return s;
// // //     }

// // //     private XSSFCellStyle makeStatusStyle(XSSFWorkbook wb) {
// // //         XSSFCellStyle s = wb.createCellStyle();
// // //         XSSFFont f = wb.createFont(); f.setBold(true); f.setFontHeightInPoints((short) 9); s.setFont(f);
// // //         s.setAlignment(HorizontalAlignment.CENTER); s.setBorderBottom(BorderStyle.THIN); return s;
// // //     }

// // //     private XSSFCellStyle makeStatLabelStyle(XSSFWorkbook wb) {
// // //         XSSFCellStyle s = wb.createCellStyle();
// // //         XSSFFont f = wb.createFont(); f.setBold(true); f.setFontHeightInPoints((short) 10); s.setFont(f);
// // //         s.setFillForegroundColor(new XSSFColor(rgb(0xe8, 0xf0, 0xfe), null));
// // //         s.setFillPattern(FillPatternType.SOLID_FOREGROUND); return s;
// // //     }

// // //     private XSSFCellStyle makeStatValueStyle(XSSFWorkbook wb) {
// // //         XSSFCellStyle s = wb.createCellStyle();
// // //         XSSFFont f = wb.createFont(); f.setBold(true); f.setFontHeightInPoints((short) 10);
// // //         f.setColor(new XSSFColor(rgb(0x1e, 0x40, 0x88), null)); s.setFont(f); return s;
// // //     }

// // //     private XSSFCellStyle makeMetaStyle(XSSFWorkbook wb) {
// // //         XSSFCellStyle s = wb.createCellStyle();
// // //         XSSFFont f = wb.createFont(); f.setItalic(true); f.setFontHeightInPoints((short) 9);
// // //         f.setColor(new XSSFColor(rgb(0x44, 0x44, 0x44), null)); s.setFont(f); return s;
// // //     }

// // //     private XSSFCellStyle makeWarnStyle(XSSFWorkbook wb) {
// // //         XSSFCellStyle s = wb.createCellStyle();
// // //         s.setFillForegroundColor(new XSSFColor(rgb(0xff, 0xf9, 0xc4), null));
// // //         s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
// // //         s.setBorderBottom(BorderStyle.THIN); s.setWrapText(true);
// // //         return s;
// // //     }

// // //     private XSSFCellStyle makeBannerMatchStyle(XSSFWorkbook wb) {
// // //         XSSFCellStyle s = wb.createCellStyle();
// // //         XSSFFont f = wb.createFont(); f.setBold(true); f.setFontHeightInPoints((short) 14);
// // //         f.setColor(new XSSFColor(new byte[]{(byte)0xff,(byte)0xff,(byte)0xff}, null)); s.setFont(f);
// // //         s.setFillForegroundColor(new XSSFColor(rgb(0x1b, 0x5e, 0x20), null));
// // //         s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
// // //         s.setAlignment(HorizontalAlignment.CENTER); s.setVerticalAlignment(VerticalAlignment.CENTER);
// // //         s.setWrapText(true); return s;
// // //     }

// // //     private XSSFCellStyle makeBannerWarnStyle(XSSFWorkbook wb) {
// // //         XSSFCellStyle s = wb.createCellStyle();
// // //         XSSFFont f = wb.createFont(); f.setBold(true); f.setFontHeightInPoints((short) 14);
// // //         f.setColor(new XSSFColor(new byte[]{(byte)0xff,(byte)0xff,(byte)0xff}, null)); s.setFont(f);
// // //         s.setFillForegroundColor(new XSSFColor(rgb(0xe6, 0x55, 0x00), null));
// // //         s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
// // //         s.setAlignment(HorizontalAlignment.CENTER); s.setVerticalAlignment(VerticalAlignment.CENTER);
// // //         s.setWrapText(true); return s;
// // //     }

// // //     private XSSFCellStyle makeBannerFailStyle(XSSFWorkbook wb) {
// // //         XSSFCellStyle s = wb.createCellStyle();
// // //         XSSFFont f = wb.createFont(); f.setBold(true); f.setFontHeightInPoints((short) 14);
// // //         f.setColor(new XSSFColor(new byte[]{(byte)0xff,(byte)0xff,(byte)0xff}, null)); s.setFont(f);
// // //         s.setFillForegroundColor(new XSSFColor(rgb(0xb7, 0x1c, 0x1c), null));
// // //         s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
// // //         s.setAlignment(HorizontalAlignment.CENTER); s.setVerticalAlignment(VerticalAlignment.CENTER);
// // //         s.setWrapText(true); return s;
// // //     }

// // //         private XSSFCellStyle makeAwardedRowStyle(XSSFWorkbook wb) {
// // //         XSSFCellStyle s = wb.createCellStyle();
// // //         XSSFFont f = wb.createFont();
// // //         f.setBold(true);
// // //         f.setFontHeightInPoints((short) 9);
// // //         f.setColor(new XSSFColor(rgb(0x7d, 0x4a, 0x00), null));
// // //         s.setFont(f);
// // //         s.setFillForegroundColor(new XSSFColor(rgb(0xff, 0xf3, 0xcd), null));
// // //         s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
// // //         s.setBorderBottom(BorderStyle.THIN);
// // //         s.setBorderTop(BorderStyle.THIN);
// // //         s.setBorderLeft(BorderStyle.THIN);
// // //         s.setBorderRight(BorderStyle.THIN);
// // //         s.setWrapText(true);
// // //         return s;
// // //     }
 
// // //     /**
// // //      * Gold row style for the Quote Amount cell of the awarded supplier.
// // //      * Same gold background, right-aligned, number formatted.
// // //      */
// // //     private XSSFCellStyle makeAwardedAmtStyle(XSSFWorkbook wb) {
// // //         XSSFCellStyle s = wb.createCellStyle();
// // //         XSSFFont f = wb.createFont();
// // //         f.setBold(true);
// // //         f.setFontHeightInPoints((short) 9);
// // //         f.setColor(new XSSFColor(rgb(0x7d, 0x4a, 0x00), null));
// // //         s.setFont(f);
// // //         s.setFillForegroundColor(new XSSFColor(rgb(0xff, 0xf3, 0xcd), null));
// // //         s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
// // //         s.setAlignment(HorizontalAlignment.RIGHT);
// // //         s.setDataFormat(wb.createDataFormat().getFormat("#,##0.00"));
// // //         s.setBorderBottom(BorderStyle.THIN);
// // //         s.setBorderTop(BorderStyle.THIN);
// // //         s.setBorderLeft(BorderStyle.THIN);
// // //         s.setBorderRight(BorderStyle.THIN);
// // //         return s;
// // //     }

// // //     // =========================================================================
// // //     //  PRIVATE: layout helpers
// // //     // =========================================================================

// // //     private int addBanner(XSSFSheet sheet, int row, String title, CellStyle ts, String sub, int cols) {
// // //         sheet.addMergedRegion(new CellRangeAddress(row, row, 0, cols - 1));
// // //         Row r1 = sheet.createRow(row++); r1.setHeightInPoints(30);
// // //         Cell c1 = r1.createCell(0); c1.setCellValue(title); c1.setCellStyle(ts);
// // //         sheet.addMergedRegion(new CellRangeAddress(row, row, 0, cols - 1));
// // //         Row r2 = sheet.createRow(row++); r2.setHeightInPoints(18);
// // //         Cell c2 = r2.createCell(0); c2.setCellValue(sub); c2.setCellStyle(ts);
// // //         return row;
// // //     }

// // //     private int addBannerCustomStyle(XSSFSheet sheet, int row, String title, CellStyle ts, String sub, int cols) {
// // //         sheet.addMergedRegion(new CellRangeAddress(row, row, 0, cols - 1));
// // //         Row r1 = sheet.createRow(row++); r1.setHeightInPoints(30);
// // //         Cell c1 = r1.createCell(0); c1.setCellValue(title); c1.setCellStyle(ts);
// // //         sheet.addMergedRegion(new CellRangeAddress(row, row, 0, cols - 1));
// // //         Row r2 = sheet.createRow(row++); r2.setHeightInPoints(18);
// // //         Cell c2 = r2.createCell(0); c2.setCellValue(sub); c2.setCellStyle(ts);
// // //         return row;
// // //     }

// // //     private int addSectionHeader(XSSFSheet sheet, int row, String text, CellStyle cs, int cols) {
// // //         sheet.addMergedRegion(new CellRangeAddress(row, row, 0, cols - 1));
// // //         Row r = sheet.createRow(row++); r.setHeightInPoints(20);
// // //         Cell c = r.createCell(0); c.setCellValue(text); c.setCellStyle(cs);
// // //         return row;
// // //     }

// // //     private int addLV(XSSFSheet sheet, int row, CellStyle lc, CellStyle vc,
// // //                       String l1, Object v1, String l2, Object v2) {
// // //         Row r = sheet.createRow(row++); r.setHeightInPoints(16);
// // //         str(r, 0, l1, lc); str(r, 1, s(v1), vc);
// // //         str(r, 2, l2, lc); str(r, 3, s(v2), vc);
// // //         return row;
// // //     }

// // //     private void addHeaderRow(XSSFSheet sheet, int row, CellStyle cs, String[] cols) {
// // //         Row r = sheet.createRow(row); r.setHeightInPoints(18);
// // //         for (int c = 0; c < cols.length; c++) {
// // //             Cell cell = r.createCell(c); cell.setCellValue(cols[c]); cell.setCellStyle(cs);
// // //         }
// // //     }

// // //     private void setWidths(XSSFSheet sheet, int[] widths) {
// // //         for (int c = 0; c < widths.length; c++) sheet.setColumnWidth(c, widths[c]);
// // //     }

// // //     private void str(Row row, int col, String value, CellStyle cs) {
// // //         Cell c = row.createCell(col); c.setCellValue(value != null ? value : ""); c.setCellStyle(cs);
// // //     }

// // //     /** Generic num() — dispatches on type (BigDecimal, Number, or zero) */
// // //     private void num(Row row, int col, Object value, CellStyle cs) {
// // //         Cell c = row.createCell(col);
// // //         if (value instanceof BigDecimal) c.setCellValue(((BigDecimal) value).doubleValue());
// // //         else if (value instanceof Number) c.setCellValue(((Number) value).doubleValue());
// // //         else c.setCellValue(0);
// // //         c.setCellStyle(cs);
// // //     }

// // //     /** Typed num() overload for BigDecimal — avoids Object boxing ambiguity */
// // //     private void num(Row row, int col, BigDecimal value, CellStyle cs) {
// // //         Cell c = row.createCell(col);
// // //         c.setCellValue(value != null ? value.doubleValue() : 0);
// // //         c.setCellStyle(cs);
// // //     }

// // //     private void info(StringBuilder h, String label, String value) {
// // //         h.append("<div class='info-item'>")
// // //          .append("<div class='info-label'>").append(label).append("</div>")
// // //          .append("<div class='info-value'>").append(value != null && !value.equals("—") ? value : "—").append("</div>")
// // //          .append("</div>");
// // //     }

// // //     // =========================================================================
// // //     //  PRIVATE: small utilities
// // //     // =========================================================================

// // //     private byte[] rgb(int r, int g, int b)          { return new byte[]{(byte) r, (byte) g, (byte) b}; }
// // //     private CellStyle alt(int sl, CellStyle n, CellStyle a) { return (sl % 2 == 0) ? a : n; }
// // //     private String s(Object o)                        { return o != null ? o.toString() : "—"; }
// // //     private String en(Object o)                       { return o != null ? o.toString() : "N/A"; }
// // //     private String bool(Boolean b)                    { return Boolean.TRUE.equals(b) ? "Yes" : "No"; }
// // //     private String fmt(LocalDateTime dt)              { return dt != null ? dt.format(DATE_FMT) : "—"; }
// // //     private String fmtAmt(BigDecimal v)               { return v != null ? String.format("%,.2f", v.doubleValue()) : "—"; }
// // //     private String joinNonNull(String sep, String... parts) {
// // //         return Arrays.stream(parts).filter(p -> p != null && !p.isBlank()).collect(Collectors.joining(" " + sep + " "));
// // //     }

// // //     private Optional<SupplierQuoteItem> findQuote(List<SupplierQuoteItem> quotes, Long itemId, Long supplierId) {
// // //         return quotes.stream()
// // //                 .filter(q -> q.getRfqItem() != null && q.getRfqItem().getId().equals(itemId)
// // //                           && q.getRfqSupplier() != null && q.getRfqSupplier().getSupplier() != null
// // //                           && q.getRfqSupplier().getSupplier().getId().equals(supplierId))
// // //                 .findFirst();
// // //     }

// // //     private double sumGrandTotal(List<SupplierQuoteItem> quotes, Long supplierId) {
// // //         return quotes.stream()
// // //                 .filter(q -> q.getRfqSupplier() != null && q.getRfqSupplier().getSupplier() != null
// // //                           && q.getRfqSupplier().getSupplier().getId().equals(supplierId)
// // //                           && q.getGrandTotal() != null)
// // //                 .mapToDouble(q -> q.getGrandTotal().doubleValue()).sum();
// // //     }

// // //     private String determineSupplierCurrency(RFQ rfq, Supplier supplier) {
// // //         if (rfq.getLocation() == null || supplier == null) return "INR";
// // //         String buyerCountry = rfq.getLocation().getCountry();
// // //         String supCountry   = supplier.getCountry();
// // //         if (buyerCountry != null && supCountry != null
// // //                 && buyerCountry.trim().equalsIgnoreCase(supCountry.trim())) {
// // //             return rfq.getLocation().getCurrencyCode() != null ? rfq.getLocation().getCurrencyCode() : "INR";
// // //         }
// // //         return "USD";
// // //     }
// // // }


// // package com.itti.leadcapturing.service;

// // import com.itti.leadcapturing.model.*;
// // import com.itti.leadcapturing.repo.*;
// // import org.apache.poi.ss.usermodel.*;
// // import org.apache.poi.ss.util.CellRangeAddress;
// // import org.apache.poi.xssf.usermodel.*;
// // import org.springframework.beans.factory.annotation.Autowired;
// // import org.springframework.stereotype.Service;
// // import org.springframework.transaction.annotation.Transactional;

// // import java.io.ByteArrayOutputStream;
// // import java.math.BigDecimal;
// // import java.time.LocalDateTime;
// // import java.time.format.DateTimeFormatter;
// // import java.util.*;
// // import java.util.stream.Collectors;


// // @Service
// // public class RFQReportService {

// //     @Autowired private RFQRepository              rfqRepository;
// //     @Autowired private RFQItemRepository          rfqItemRepository;
// //     @Autowired private RFQSupplierRepository      rfqSupplierRepository;
// //     @Autowired private SupplierQuoteItemRepository quoteItemRepository;
// //     @Autowired private DynamicRFQApprovalRepository approvalRepository;
// //     @Autowired private InvoiceRepository          invoiceRepository;
// //     @Autowired private PurchaseOrderRepository    poRepository;
// //     @Autowired private GRNRepository              grnRepository;
// //     @Autowired private ThreeWayMatchRepository    matchRepository;

// //     private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
// //     private static final DateTimeFormatter DTTM_FMT = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm");

// //     // =========================================================================
// //     //  PO SUMMARY EXCEL  — standalone single-PO report
// //     // =========================================================================
// //     @Transactional(readOnly = true)
// //     public byte[] generatePOSummaryExcel(Long poId) throws Exception {

// //         PurchaseOrder po = poRepository.findById(poId)
// //                 .orElseThrow(() -> new RuntimeException("Purchase Order not found: " + poId));

// //         List<POLineItem> lineItems = po.getLineItems();

// //         try (XSSFWorkbook wb = new XSSFWorkbook();
// //              ByteArrayOutputStream out = new ByteArrayOutputStream()) {

// //             CellStyle titleStyle  = makeTitleStyle(wb);
// //             CellStyle hdrNavy    = makeHeaderStyle(wb, rgb(0x1e, 0x40, 0x88));
// //             CellStyle hdrGreen   = makeHeaderStyle(wb, rgb(0x1b, 0x5e, 0x20));
// //             CellStyle labelStyle  = makeLabelStyle(wb);
// //             CellStyle valueStyle  = makeValueStyle(wb);
// //             CellStyle amtStyle    = makeAmountStyle(wb);
// //             CellStyle altStyle    = makeAltRowStyle(wb);
// //             CellStyle totalStyle  = makeTotalStyle(wb);
// //             CellStyle statusStyle = makeStatusStyle(wb);

// //             // ── Sheet 1: PO Overview ──────────────────────────────────────
// //             XSSFSheet s1 = wb.createSheet("PO Overview");
// //             s1.setColumnWidth(0, 6000); s1.setColumnWidth(1, 9000);
// //             s1.setColumnWidth(2, 6000); s1.setColumnWidth(3, 9000);

// //             int row = 0;
// //             row = addBanner(s1, row, "PURCHASE ORDER", titleStyle,
// //                     "PO: " + s(po.getPoNumber())
// //                     + "  |  Status: " + (po.getStatus() != null ? po.getStatus().name() : "—")
// //                     + "  |  Generated: " + LocalDateTime.now().format(DTTM_FMT), 4);

// //             row = addSectionHeader(s1, row, "PO INFORMATION", hdrNavy, 4);
// //             row = addLV(s1, row, labelStyle, valueStyle,
// //                     "PO Number",         s(po.getPoNumber()),
// //                     "PO Date",           fmt(po.getPoDate()));
// //             row = addLV(s1, row, labelStyle, valueStyle,
// //                     "Status",            po.getStatus() != null ? po.getStatus().name() : "—",
// //                     "Approval Status",   po.getApprovalStatus() != null ? po.getApprovalStatus().name() : "—");
// //             row = addLV(s1, row, labelStyle, valueStyle,
// //                     "Reference Quote",   s(po.getReferenceQuoteNo()),
// //                     "Currency",          s(po.getCurrencyCode()) + " " + s(po.getCurrencySymbol()));
// //             row = addLV(s1, row, labelStyle, valueStyle,
// //                     "Payment Terms",     s(po.getPaymentTerms()),
// //                     "Delivery Terms",    s(po.getDeliveryTerms()));
// //             row = addLV(s1, row, labelStyle, valueStyle,
// //                     "Mode of Payment",   s(po.getModeOfPayment()),
// //                     "Dispatched Through",s(po.getDispatchedThrough()));
// //             row = addLV(s1, row, labelStyle, valueStyle,
// //                     "Destination",       s(po.getDestination()),
// //                     "Amount in Words",   s(po.getAmountInWords()));
// //             if (po.getBuyerRemarks() != null)
// //                 row = addLV(s1, row, labelStyle, valueStyle,
// //                         "Buyer Remarks", s(po.getBuyerRemarks()), "", "");
// //             row++;

// //             if (po.getBuyer() != null) {
// //                 Buyer b = po.getBuyer();
// //                 row = addSectionHeader(s1, row, "BUYER INFORMATION", hdrNavy, 4);
// //                 row = addLV(s1, row, labelStyle, valueStyle,
// //                         "Company",   b.getCompanyName(),           "Type",    s(b.getCompanyType()));
// //                 row = addLV(s1, row, labelStyle, valueStyle,
// //                         "Contact",   b.getContactPersonName(),     "Email",   s(b.getContactPersonEmail()));
// //                 row = addLV(s1, row, labelStyle, valueStyle,
// //                         "Phone",     s(b.getContactPersonPhone()), "GST",     s(b.getGstNumber()));
// //                 row++;
// //             }

// //             if (po.getSupplier() != null) {
// //                 Supplier sup = po.getSupplier();
// //                 row = addSectionHeader(s1, row, "SUPPLIER INFORMATION", hdrNavy, 4);
// //                 row = addLV(s1, row, labelStyle, valueStyle,
// //                         "Company",   s(sup.getCompanyName()),          "Type",  s(sup.getCompanyType()));
// //                 row = addLV(s1, row, labelStyle, valueStyle,
// //                         "Contact",   s(sup.getContactPersonName()),    "Email", s(sup.getContactPersonEmail()));
// //                 row = addLV(s1, row, labelStyle, valueStyle,
// //                         "Phone",     s(sup.getContactPersonPhone()),   "Country", s(sup.getCountry()));
// //                 row++;
// //             }

// //             if (po.getDeliveryLocation() != null) {
// //                 Location dl = po.getDeliveryLocation();
// //                 row = addSectionHeader(s1, row, "DELIVERY LOCATION", hdrNavy, 4);
// //                 row = addLV(s1, row, labelStyle, valueStyle,
// //                         "Location Name", dl.getLocationName(), "City", s(dl.getCity()));
// //                 row = addLV(s1, row, labelStyle, valueStyle,
// //                         "State",         s(dl.getState()),     "Country", s(dl.getCountry()));
// //                 row = addLV(s1, row, labelStyle, valueStyle,
// //                         "Address",       s(dl.getAddressLine1()), "Contact", s(dl.getLocationContactName()));
// //                 row++;
// //             }

// //             if (po.getRfq() != null) {
// //                 row = addSectionHeader(s1, row, "RFQ REFERENCE", hdrNavy, 4);
// //                 row = addLV(s1, row, labelStyle, valueStyle,
// //                         "RFQ Number", s(po.getRfq().getRfqNumber()),
// //                         "RFQ Title",  s(po.getRfq().getRfqTitle()));
// //                 row++;
// //             }

// //             if (po.getApprovedByName() != null) {
// //                 row = addSectionHeader(s1, row, "APPROVAL INFORMATION", hdrNavy, 4);
// //                 row = addLV(s1, row, labelStyle, valueStyle,
// //                         "Approved By",    s(po.getApprovedByName()),
// //                         "Designation",    s(po.getApprovedByDesignation()));
// //                 addLV(s1, row, labelStyle, valueStyle,
// //                         "Approval Date",  fmt(po.getApprovalDate()), "", "");
// //             }

// //             // ── Sheet 2: Line Items ───────────────────────────────────────
// //             XSSFSheet s2 = wb.createSheet("Line Items");
// //             setWidths(s2, new int[]{1200, 3000, 7000, 2500, 2000, 2500, 4000, 2000, 2000, 4500, 4000});

// //             row = 0;
// //             row = addBanner(s2, row, "LINE ITEMS", titleStyle,
// //                     s(po.getPoNumber()) + "  [" + lineItems.size() + " items]"
// //                     + "  |  Currency: " + s(po.getCurrencyCode()), 11);

// //             String[] cols = {"#", "Item Code", "Description", "Specifications", "UOM",
// //                              "PO Qty", "Unit Price", "Disc %", "Tax %", "Line Total", "Remarks"};
// //             addHeaderRow(s2, row++, hdrGreen, cols);

// //             int sl = 1;
// //             for (POLineItem li : lineItems) {
// //                 Row r = s2.createRow(row++);
// //                 CellStyle cs = alt(sl, valueStyle, altStyle);
// //                 str(r, 0,  String.valueOf(sl++), cs);
// //                 str(r, 1,  s(li.getItemCode()), cs);
// //                 str(r, 2,  s(li.getItemDescription()), cs);
// //                 str(r, 3,  s(li.getSpecifications()), cs);
// //                 str(r, 4,  s(li.getUom()), cs);
// //                 num(r, 5,  li.getQuantity(), amtStyle);
// //                 num(r, 6,  li.getUnitRate(), amtStyle);
// //                 num(r, 7,  li.getDiscountPercentage(), amtStyle);
// //                 num(r, 8,  li.getTaxPercentage(), amtStyle);
// //                 num(r, 9,  li.getLineTotal(), amtStyle);
// //                 str(r, 10, s(li.getBrandMakeModel()), cs);
// //             }

// //             row++;
// //             Object[][] totals = {
// //                 {"Subtotal (Pre-tax)", po.getSubtotal()},
// //                 {"Tax Amount",         po.getTaxAmount()},
// //                 {"GRAND TOTAL",        po.getGrandTotal()}
// //             };
// //             for (int t = 0; t < totals.length; t++) {
// //                 Row tr = s2.createRow(row++);
// //                 boolean isGrand = (t == totals.length - 1);
// //                 Cell lc = tr.createCell(9);
// //                 lc.setCellValue((String) totals[t][0]);
// //                 lc.setCellStyle(isGrand ? totalStyle : labelStyle);
// //                 Cell vc = tr.createCell(10);
// //                 if (totals[t][1] instanceof BigDecimal)
// //                     vc.setCellValue(((BigDecimal) totals[t][1]).doubleValue());
// //                 else
// //                     vc.setCellValue(0);
// //                 vc.setCellStyle(isGrand ? totalStyle : amtStyle);
// //             }

// //             if (po.getAmountInWords() != null) {
// //                 row++;
// //                 Row amtRow = s2.createRow(row++);
// //                 Cell lbl = amtRow.createCell(0);
// //                 lbl.setCellValue("Amount in Words:");
// //                 lbl.setCellStyle(labelStyle);
// //                 Cell val = amtRow.createCell(1);
// //                 val.setCellValue(po.getAmountInWords());
// //                 val.setCellStyle(valueStyle);
// //                 s2.addMergedRegion(new CellRangeAddress(row - 1, row - 1, 1, 10));
// //             }

// //             wb.write(out);
// //             return out.toByteArray();
// //         }
// //     }

// //     // =========================================================================
// //     //  GRN EXCEL  — standalone single-GRN report
// //     // =========================================================================
// //     @Transactional(readOnly = true)
// //     public byte[] generateGRNExcel(Long grnId) throws Exception {

// //         GRN grn = grnRepository.findByIdWithLineItems(grnId)
// //                 .orElseThrow(() -> new RuntimeException("GRN not found: " + grnId));

// //         List<GRNLineItem> lineItems = grn.getLineItems();

// //         try (XSSFWorkbook wb = new XSSFWorkbook();
// //              ByteArrayOutputStream out = new ByteArrayOutputStream()) {

// //             CellStyle titleStyle  = makeTitleStyle(wb);
// //             CellStyle hdrNavy    = makeHeaderStyle(wb, rgb(0x1e, 0x40, 0x88));
// //             CellStyle hdrGreen   = makeHeaderStyle(wb, rgb(0x1b, 0x5e, 0x20));
// //             CellStyle hdrOrange  = makeHeaderStyle(wb, rgb(0xe6, 0x55, 0x00));
// //             CellStyle labelStyle  = makeLabelStyle(wb);
// //             CellStyle valueStyle  = makeValueStyle(wb);
// //             CellStyle amtStyle    = makeAmountStyle(wb);
// //             CellStyle altStyle    = makeAltRowStyle(wb);
// //             CellStyle totalStyle  = makeTotalStyle(wb);
// //             CellStyle statusStyle = makeStatusStyle(wb);

// //             XSSFSheet s1 = wb.createSheet("GRN Overview");
// //             s1.setColumnWidth(0, 6000); s1.setColumnWidth(1, 9000);
// //             s1.setColumnWidth(2, 6000); s1.setColumnWidth(3, 9000);

// //             int row = 0;
// //             row = addBanner(s1, row, "GOODS RECEIPT NOTE", titleStyle,
// //                     "GRN: " + s(grn.getGrnNumber())
// //                     + "  |  Status: " + (grn.getStatus() != null ? grn.getStatus().name() : "—")
// //                     + "  |  Generated: " + LocalDateTime.now().format(DTTM_FMT), 4);

// //             row = addSectionHeader(s1, row, "GRN INFORMATION", hdrNavy, 4);
// //             row = addLV(s1, row, labelStyle, valueStyle,
// //                     "GRN Number",    s(grn.getGrnNumber()),
// //                     "Status",        grn.getStatus() != null ? grn.getStatus().name() : "—");
// //             row = addLV(s1, row, labelStyle, valueStyle,
// //                     "Received Date", fmt(grn.getReceivedDate()),
// //                     "PO Number",     s(grn.getPoNumber()));
// //             row = addLV(s1, row, labelStyle, valueStyle,
// //                     "Invoice Number", s(grn.getInvoiceNumber()),
// //                     "Supplier",       s(grn.getSupplierName()));
// //             row++;

// //             row = addSectionHeader(s1, row, "DELIVERY DETAILS", hdrNavy, 4);
// //             row = addLV(s1, row, labelStyle, valueStyle,
// //                     "Delivery Challan", s(grn.getDeliveryChallanNumber()),
// //                     "LR Number",        s(grn.getLrNumber()));
// //             row = addLV(s1, row, labelStyle, valueStyle,
// //                     "Transporter",      s(grn.getTransporterName()),
// //                     "Vehicle No.",      s(grn.getVehicleNumber()));
// //             row = addLV(s1, row, labelStyle, valueStyle,
// //                     "Delivery Location", s(grn.getDeliveryLocation()),
// //                     "Received By",       s(grn.getReceivedByName()));
// //             row++;

// //             row = addSectionHeader(s1, row, "QA INFORMATION", hdrOrange, 4);
// //             row = addLV(s1, row, labelStyle, valueStyle,
// //                     "QA Inspector",  s(grn.getInspectedByName()),
// //                     "Approved By",   s(grn.getApprovedByName()));
// //             row = addLV(s1, row, labelStyle, valueStyle,
// //                     "Approved At",   fmt(grn.getApprovedAt()),
// //                     "Remarks",       s(grn.getRemarks()));
// //             row++;

// //             row = addSectionHeader(s1, row, "VALUE SUMMARY", hdrNavy, 4);
// //             row = addLV(s1, row, labelStyle, valueStyle,
// //                     "Total Ordered Value",  s(grn.getTotalOrderedValue()),
// //                     "Total Received Value", s(grn.getTotalReceivedValue()));
// //             addLV(s1, row, labelStyle, valueStyle,
// //                     "Total Rejected Value", s(grn.getTotalRejectedValue()), "", "");

// //             XSSFSheet s2 = wb.createSheet("Line Items");
// //             setWidths(s2, new int[]{
// //                 1200, 3000, 7000, 2500, 2000,
// //                 3000, 3000, 3000, 3000, 4000,
// //                 4000, 4000, 2500, 5000
// //             });

// //             row = 0;
// //             row = addBanner(s2, row, "LINE ITEMS", titleStyle,
// //                     s(grn.getGrnNumber()) + "  [" + lineItems.size() + " items]", 14);

// //             Row ph1 = s2.createRow(row++);
// //             ph1.setHeightInPoints(16);
// //             str(ph1, 0,  "#",             hdrNavy);
// //             str(ph1, 1,  "Item Code",     hdrNavy);
// //             str(ph1, 2,  "Description",   hdrNavy);
// //             str(ph1, 3,  "UOM",           hdrNavy);
// //             str(ph1, 4,  "Ordered",       hdrNavy);
// //             str(ph1, 5,  "Received",      hdrNavy);
// //             str(ph1, 6,  "Defective",     hdrOrange);
// //             str(ph1, 7,  "Rejected",      hdrOrange);
// //             str(ph1, 8,  "Accepted",      hdrGreen);
// //             str(ph1, 9,  "PO Rate",       hdrNavy);
// //             str(ph1, 10, "PO Line Total", hdrNavy);
// //             str(ph1, 11, "Accepted Value",hdrGreen);
// //             str(ph1, 12, "Condition",     hdrOrange);
// //             str(ph1, 13, "QA Remarks",    hdrOrange);

// //             int sl = 1;
// //             for (GRNLineItem li : lineItems) {
// //                 Row r = s2.createRow(row++);
// //                 CellStyle cs = alt(sl, valueStyle, altStyle);
// //                 str(r, 0,  String.valueOf(sl++), cs);
// //                 str(r, 1,  s(li.getItemCode()), cs);
// //                 str(r, 2,  s(li.getItemDescription()), cs);
// //                 str(r, 3,  s(li.getUom()), cs);
// //                 num(r, 4,  li.getOrderedQuantity(), amtStyle);
// //                 num(r, 5,  li.getReceivedQuantity(), amtStyle);
// //                 num(r, 6,  li.getDefectiveQuantity(), amtStyle);
// //                 num(r, 7,  li.getRejectedQuantity(), amtStyle);
// //                 num(r, 8,  li.getAcceptedQuantity(), amtStyle);
// //                 num(r, 9,  li.getPoUnitRate(), amtStyle);
// //                 num(r, 10, li.getPoLineTotal(), amtStyle);
// //                 num(r, 11, li.getAcceptedValue(), amtStyle);
// //                 str(r, 12, li.getItemCondition() != null ? li.getItemCondition().name() : "GOOD", statusStyle);
// //                 str(r, 13, s(li.getQaRemarks()), cs);
// //             }

// //             row++;
// //             String[][] grnTotals = {
// //                 {"Total Ordered Value",   grn.getTotalOrderedValue()  != null ? grn.getTotalOrderedValue().toPlainString()  : "0"},
// //                 {"Total Received Value",  grn.getTotalReceivedValue() != null ? grn.getTotalReceivedValue().toPlainString() : "0"},
// //                 {"Total Rejected Value",  grn.getTotalRejectedValue() != null ? grn.getTotalRejectedValue().toPlainString() : "0"}
// //             };
// //             for (int t = 0; t < grnTotals.length; t++) {
// //                 Row tr = s2.createRow(row++);
// //                 boolean isLast = (t == grnTotals.length - 1);
// //                 Cell lc = tr.createCell(10); lc.setCellValue(grnTotals[t][0]); lc.setCellStyle(isLast ? totalStyle : labelStyle);
// //                 Cell vc = tr.createCell(11);
// //                 try { vc.setCellValue(Double.parseDouble(grnTotals[t][1])); } catch (Exception e) { vc.setCellValue(0); }
// //                 vc.setCellStyle(isLast ? totalStyle : amtStyle);
// //             }

// //             wb.write(out);
// //             return out.toByteArray();
// //         }
// //     }

// //     // =========================================================================
// //     //  THREE-WAY MATCH EXCEL  — standalone single-match report
// //     // =========================================================================
// //     @Transactional(readOnly = true)
// //     public byte[] generateThreeWayMatchExcel(Long matchId) throws Exception {

// //         ThreeWayMatch match = matchRepository.findByIdWithLineResults(matchId)
// //                 .orElseThrow(() -> new RuntimeException("3-Way Match not found: " + matchId));

// //         List<MatchLineResult> lineResults = match.getLineResults();

// //         try (XSSFWorkbook wb = new XSSFWorkbook();
// //              ByteArrayOutputStream out = new ByteArrayOutputStream()) {

// //             CellStyle titleStyle  = makeTitleStyle(wb);
// //             CellStyle hdrNavy    = makeHeaderStyle(wb, rgb(0x1e, 0x40, 0x88));
// //             CellStyle hdrGreen   = makeHeaderStyle(wb, rgb(0x1b, 0x5e, 0x20));
// //             CellStyle hdrRed     = makeHeaderStyle(wb, rgb(0xb7, 0x1c, 0x1c));
// //             CellStyle hdrOrange  = makeHeaderStyle(wb, rgb(0xe6, 0x55, 0x00));
// //             CellStyle labelStyle  = makeLabelStyle(wb);
// //             CellStyle valueStyle  = makeValueStyle(wb);
// //             CellStyle amtStyle    = makeAmountStyle(wb);
// //             CellStyle altStyle    = makeAltRowStyle(wb);
// //             CellStyle totalStyle  = makeTotalStyle(wb);
// //             CellStyle statusStyle = makeStatusStyle(wb);
// //             CellStyle lowestStyle = makeLowestStyle(wb);
// //             CellStyle warnStyle   = makeWarnStyle(wb);

// //             XSSFSheet s1 = wb.createSheet("Match Summary");
// //             s1.setColumnWidth(0, 6000); s1.setColumnWidth(1, 9000);
// //             s1.setColumnWidth(2, 6000); s1.setColumnWidth(3, 9000);

// //             int row = 0;

// //             boolean isMatched  = "MATCHED".equals(match.getMatchStatus() != null ? match.getMatchStatus().name() : "");
// //             boolean isFailed   = match.getMatchStatus() != null &&
// //                     (match.getMatchStatus() == ThreeWayMatchStatus.FAILED
// //                   || match.getMatchStatus() == ThreeWayMatchStatus.ITEM_MISMATCH
// //                   || match.getMatchStatus() == ThreeWayMatchStatus.DISPUTED);

// //             CellStyle bannerStyle = isMatched ? makeBannerMatchStyle(wb)
// //                                   : isFailed  ? makeBannerFailStyle(wb)
// //                                                : makeBannerWarnStyle(wb);

// //             row = addBannerCustomStyle(s1, row, "3-WAY MATCH REPORT", bannerStyle,
// //                     "Match #" + match.getId()
// //                     + "  v" + match.getMatchVersion()
// //                     + "  |  Status: " + (match.getMatchStatus() != null ? match.getMatchStatus().name() : "—")
// //                     + "  |  Generated: " + LocalDateTime.now().format(DTTM_FMT), 4);

// //             row = addSectionHeader(s1, row, "DOCUMENT REFERENCES", hdrNavy, 4);
// //             row = addLV(s1, row, labelStyle, valueStyle,
// //                     "PO Number",      s(match.getPoNumber()),
// //                     "GRN Number",     s(match.getGrnNumber()));
// //             row = addLV(s1, row, labelStyle, valueStyle,
// //                     "Invoice Number", s(match.getInvoiceNumber()),
// //                     "Match Version",  String.valueOf(match.getMatchVersion()));
// //             row = addLV(s1, row, labelStyle, valueStyle,
// //                     "Tolerance %",    s(match.getTolerancePercentage()),
// //                     "Performed By",   s(match.getPerformedByName()));
// //             row = addLV(s1, row, labelStyle, valueStyle,
// //                     "Performed At",   fmt(match.getPerformedAt()),
// //                     "Updated At",     fmt(match.getUpdatedAt()));
// //             row++;

// //             row = addSectionHeader(s1, row, "FINANCIAL COMPARISON", hdrNavy, 4);
// //             row = addLV(s1, row, labelStyle, valueStyle,
// //                     "PO Total Value",      s(match.getPoTotalValue()),
// //                     "GRN Accepted Value",  s(match.getGrnAcceptedValue()));
// //             row = addLV(s1, row, labelStyle, valueStyle,
// //                     "Invoice Total Value", s(match.getInvoiceTotalValue()),
// //                     "Variance Amount",     s(match.getVarianceAmount()));
// //             row = addLV(s1, row, labelStyle, valueStyle,
// //                     "Variance %",          s(match.getVariancePercentage()),
// //                     "Approved Payment",    s(match.getApprovedPaymentAmount()));
// //             row++;

// //             row = addSectionHeader(s1, row, "MATCH FLAGS", hdrNavy, 4);
// //             row = addLV(s1, row, labelStyle, valueStyle,
// //                     "Quantity Mismatch",  bool(match.getHasQuantityMismatch()),
// //                     "Price Mismatch",     bool(match.getHasPriceMismatch()));
// //             row = addLV(s1, row, labelStyle, valueStyle,
// //                     "Item Mismatch",      bool(match.getHasItemMismatch()),
// //                     "Excess Delivery",    bool(match.getHasExcessDelivery()));
// //             row = addLV(s1, row, labelStyle, valueStyle,
// //                     "Matched Lines",      String.valueOf(match.getTotalMatchedLines()),
// //                     "Mismatch Lines",     String.valueOf(match.getTotalMismatchLines()));
// //             row++;

// //             if (match.getResolution() != null) {
// //                 row = addSectionHeader(s1, row, "RESOLUTION", hdrNavy, 4);
// //                 row = addLV(s1, row, labelStyle, valueStyle,
// //                         "Resolution",         match.getResolution().name(),
// //                         "Resolved By",        s(match.getResolvedByName()));
// //                 row = addLV(s1, row, labelStyle, valueStyle,
// //                         "Resolved At",        fmt(match.getResolvedAt()),
// //                         "Resolution Remarks", s(match.getResolutionRemarks()));
// //             }

// //             if (match.getMatchSummary() != null) {
// //                 row++;
// //                 Row summaryRow = s1.createRow(row++);
// //                 summaryRow.setHeightInPoints(30);
// //                 s1.addMergedRegion(new CellRangeAddress(row - 1, row - 1, 0, 3));
// //                 Cell sc = summaryRow.createCell(0);
// //                 sc.setCellValue(match.getMatchSummary());
// //                 sc.setCellStyle(isMatched ? lowestStyle : warnStyle);
// //             }

// //             XSSFSheet s2 = wb.createSheet("Line Results");
// //             setWidths(s2, new int[]{
// //                 1200, 3000, 7000, 2000,
// //                 3000, 3000, 3000,
// //                 3500, 3500, 3000,
// //                 3500, 3500, 2500,
// //                 4500, 4500, 4500,
// //                 3000, 8000
// //             });

// //             row = 0;
// //             row = addBanner(s2, row, "LINE-BY-LINE MATCH RESULTS", titleStyle,
// //                     s(match.getInvoiceNumber()) + " ↔ " + s(match.getGrnNumber())
// //                     + "  |  " + lineResults.size() + " lines", 18);

// //             String[] cols = {
// //                 "#", "Item Code", "Item Description", "UOM",
// //                 "PO Qty", "GRN Accepted", "Inv Qty",
// //                 "Qty Variance", "Qty Var%", "Within Tol?",
// //                 "PO Rate", "Inv Price", "Price Var%",
// //                 "PO Line Value", "GRN Acc. Value", "Inv Line Value",
// //                 "Line Status", "Mismatch Notes"
// //             };
// //             addHeaderRow(s2, row++, hdrNavy, cols);

// //             int sl = 1;
// //             for (MatchLineResult lr : lineResults) {
// //                 Row r = s2.createRow(row++);
// //                 boolean lineMatched = lr.getLineMatchStatus() == MatchLineResult.LineMatchStatus.MATCHED
// //                         || lr.getLineMatchStatus() == MatchLineResult.LineMatchStatus.PARTIALLY_MATCHED;
// //                 boolean lineFailed  = lr.getLineMatchStatus() == MatchLineResult.LineMatchStatus.NOT_IN_PO
// //                         || lr.getLineMatchStatus() == MatchLineResult.LineMatchStatus.NOT_RECEIVED;

// //                 CellStyle cs  = lineFailed ? warnStyle : alt(sl, valueStyle, altStyle);
// //                 CellStyle ams = lineFailed ? warnStyle : amtStyle;
// //                 CellStyle gs  = lineMatched ? lowestStyle : ams;

// //                 str(r, 0,  String.valueOf(sl++), cs);
// //                 str(r, 1,  s(lr.getItemCode()), cs);
// //                 str(r, 2,  s(lr.getItemDescription()), cs);
// //                 str(r, 3,  s(lr.getUom()), cs);
// //                 num(r, 4,  lr.getPoOrderedQuantity(), ams);
// //                 num(r, 5,  lr.getGrnAcceptedQuantity(), gs);
// //                 num(r, 6,  lr.getInvoiceQuantity(), ams);
// //                 num(r, 7,  lr.getQuantityVariance(), ams);
// //                 num(r, 8,  lr.getQuantityVariancePct(), amtStyle);
// //                 str(r, 9,  Boolean.TRUE.equals(lr.getWithinTolerance()) ? "✓ Yes" : "✗ No", statusStyle);
// //                 num(r, 10, lr.getPoUnitRate(), ams);
// //                 num(r, 11, lr.getInvoiceUnitPrice(), ams);
// //                 num(r, 12, lr.getPriceVariancePct(), amtStyle);
// //                 num(r, 13, lr.getPoLineValue(), ams);
// //                 num(r, 14, lr.getGrnAcceptedValue(), gs);
// //                 num(r, 15, lr.getInvoiceLineValue(), ams);
// //                 str(r, 16, lr.getLineMatchStatus() != null ? lr.getLineMatchStatus().name() : "—", statusStyle);
// //                 str(r, 17, s(lr.getMismatchNotes()), cs);
// //             }

// //             row++;
// //             BigDecimal totPOVal  = lineResults.stream().filter(lr -> lr.getPoLineValue()      != null).map(MatchLineResult::getPoLineValue).reduce(BigDecimal.ZERO, BigDecimal::add);
// //             BigDecimal totGRNVal = lineResults.stream().filter(lr -> lr.getGrnAcceptedValue() != null).map(MatchLineResult::getGrnAcceptedValue).reduce(BigDecimal.ZERO, BigDecimal::add);
// //             BigDecimal totInvVal = lineResults.stream().filter(lr -> lr.getInvoiceLineValue() != null).map(MatchLineResult::getInvoiceLineValue).reduce(BigDecimal.ZERO, BigDecimal::add);
// //             BigDecimal totVarVal = totInvVal.subtract(totGRNVal);

// //             Row totRow = s2.createRow(row++);
// //             for (int c = 0; c < 13; c++) { Cell nc = totRow.createCell(c); nc.setCellStyle(totalStyle); if (c == 0) nc.setCellValue("TOTALS"); }
// //             num(totRow, 13, totPOVal,  totalStyle);
// //             num(totRow, 14, totGRNVal, totalStyle);
// //             num(totRow, 15, totInvVal, totalStyle);
// //             for (int c = 16; c < 18; c++) {
// //                 Cell nc = totRow.createCell(c); nc.setCellStyle(totalStyle);
// //                 if (c == 16) nc.setCellValue("VARIANCE: " + (totVarVal.compareTo(BigDecimal.ZERO) >= 0 ? "+" : "") + totVarVal.toPlainString());
// //             }

// //             wb.write(out);
// //             return out.toByteArray();
// //         }
// //     }

// //     // =========================================================================
// //     //  1a.  RFQ SUMMARY EXCEL  —  BUYER version  (all sheets)
// //     // =========================================================================
// //     @Transactional(readOnly = true)
// //     public byte[] generateRFQSummaryExcel(Long rfqId) throws Exception {
// //         return generateRFQSummaryExcel(rfqId, false);
// //     }

// //     // =========================================================================
// //     //  1b.  RFQ SUMMARY EXCEL  —  SUPPLIER version  (Overview + Line Items only)
// //     // =========================================================================
// //     @Transactional(readOnly = true)
// //     public byte[] generateRFQSummaryExcelForSupplier(Long rfqId) throws Exception {
// //         return generateRFQSummaryExcel(rfqId, true);
// //     }

// //     /**
// //      * Core implementation.
// //      *
// //      * supplierMode = true  → 2 sheets only: RFQ Overview + Line Items
// //      * supplierMode = false → 12 sheets: RFQ Overview, Line Items, Invited Suppliers,
// //      *                        Approval History, Purchase Order, PO Line Items,
// //      *                        Invoice, Invoice Line Items,
// //      *                        GRN (all batches), GRN Line Items (all batches),
// //      *                        3-Way Match, Match Line Results
// //      *
// //      * KEY CHANGE: GRN sheets now show ALL GRN delivery batches for the PO,
// //      * not just the first one. Each batch gets its own section header and
// //      * subtotals so partial deliveries (e.g. 30 received first, 10 later)
// //      * are fully visible in the report.
// //      */
// //     @Transactional(readOnly = true)
// //     public byte[] generateRFQSummaryExcel(Long rfqId, boolean supplierMode) throws Exception {

// //         RFQ rfq = rfqRepository.findByIdWithAllDetails(rfqId)
// //                 .orElseThrow(() -> new RuntimeException("RFQ not found: " + rfqId));
// //         rfqRepository.findByIdWithItems(rfqId).ifPresent(r -> r.getItems().size());
// //         rfqRepository.findByIdWithSuppliers(rfqId).ifPresent(r -> r.getSelectedSuppliers().size());

// //         List<RFQItem>            items     = rfqItemRepository.findByRfqId(rfqId);
// //         List<RFQSupplier>        suppliers = supplierMode ? Collections.emptyList()
// //                                                           : rfqSupplierRepository.findByRFQId(rfqId);
// //         List<DynamicRFQApproval> approvals = supplierMode ? Collections.emptyList()
// //                                                           : approvalRepository.findByRfqIdOrderBySequenceOrderAsc(rfqId);

// //         // ── Extra data for buyer sheets ────────────────────────────────────
// //         List<PurchaseOrder> pos     = Collections.emptyList();
// //         List<Invoice>       invList = Collections.emptyList();
// //         List<GRN>           grns    = Collections.emptyList();   // ALL GRN batches for the PO
// //         List<ThreeWayMatch> matches = Collections.emptyList();

// //         if (!supplierMode) {
// //             try { pos = poRepository.findByRfqId(rfqId); } catch (Exception ignored) {}
// //             if (!pos.isEmpty()) {
// //                 Long poId = pos.get(0).getId();
// //                 // ── CHANGED: load ALL GRNs for the PO, not just the first ──
// //                 try { grns = grnRepository.findByPurchaseOrderId(poId); } catch (Exception ignored) {}
// //                 try { invList = invoiceRepository.findByPoId(poId); } catch (Exception ignored) {}
// //                 if (!invList.isEmpty()) {
// //                     Long invId = invList.get(0).getId();
// //                     try { matches = matchRepository.findByInvoiceId(invId); } catch (Exception ignored) {}
// //                 }
// //             }
// //         }

// //         try (XSSFWorkbook wb = new XSSFWorkbook();
// //              ByteArrayOutputStream out = new ByteArrayOutputStream()) {

// //             CellStyle titleStyle  = makeTitleStyle(wb);
// //             CellStyle hdrNavy    = makeHeaderStyle(wb, rgb(0x1e, 0x40, 0x88));
// //             CellStyle hdrGreen   = makeHeaderStyle(wb, rgb(0x2e, 0x7d, 0x32));
// //             CellStyle hdrOrange  = makeHeaderStyle(wb, rgb(0xe6, 0x55, 0x00));
// //             CellStyle hdrPurple  = makeHeaderStyle(wb, rgb(0x6a, 0x0d, 0xad));
// //             CellStyle labelStyle  = makeLabelStyle(wb);
// //             CellStyle valueStyle  = makeValueStyle(wb);
// //             CellStyle amtStyle    = makeAmountStyle(wb);
// //             CellStyle altStyle    = makeAltRowStyle(wb);
// //             CellStyle statusStyle = makeStatusStyle(wb);
// //             CellStyle lowestStyle = makeLowestStyle(wb);
// //             CellStyle totalStyle  = makeTotalStyle(wb);
// //             CellStyle warnStyle   = makeWarnStyle(wb);

// //             // ═══════════════════════════════════════════════════════════════
// //             //  SHEET 1: RFQ Overview
// //             // ═══════════════════════════════════════════════════════════════
// //             XSSFSheet s1 = wb.createSheet("RFQ Overview");
// //             s1.setColumnWidth(0, 6000); s1.setColumnWidth(1, 9000);
// //             s1.setColumnWidth(2, 6000); s1.setColumnWidth(3, 9000);

// //             int row = 0;
// //             String bannerSub = "RFQ: " + rfq.getRfqNumber()
// //                     + (supplierMode ? "   |   Supplier Copy" : "")
// //                     + "   |   Generated: " + LocalDateTime.now().format(DTTM_FMT);
// //             row = addBanner(s1, row, "RFQ SUMMARY REPORT", titleStyle, bannerSub, 4);

// //             row = addSectionHeader(s1, row, "RFQ INFORMATION", hdrNavy, 4);
// //             row = addLV(s1, row, labelStyle, valueStyle, "RFQ Number",    rfq.getRfqNumber(),              "RFQ Title",       rfq.getRfqTitle());
// //             row = addLV(s1, row, labelStyle, valueStyle, "Status",        en(rfq.getStatus()),              "Priority",        en(rfq.getPriority()));
// //             row = addLV(s1, row, labelStyle, valueStyle, "Issue Date",    fmt(rfq.getIssueDate()),          "Due Date",        fmt(rfq.getDueDate()));
// //             row = addLV(s1, row, labelStyle, valueStyle, "Item Req Date", fmt(rfq.getItemRequiredDate()),   "Approval Req",    bool(rfq.getApprovalRequired()));
// //             row = addLV(s1, row, labelStyle, valueStyle, "Payment Terms", s(rfq.getPaymentTerms()),        "Delivery Terms",  s(rfq.getDeliveryTerms()));
// //             row = addLV(s1, row, labelStyle, valueStyle, "Tax %",         s(rfq.getTaxPercentage()),       "Justification",   s(rfq.getJustification()));
// //             row = addLV(s1, row, labelStyle, valueStyle, "Cost Center",   s(rfq.getCostCenterCode()),     "Project Code",    s(rfq.getProjectCode()));
// //             row++;

// //             if (rfq.getBuyer() != null) {
// //                 Buyer b = rfq.getBuyer();
// //                 row = addSectionHeader(s1, row, "BUYER INFORMATION", hdrNavy, 4);
// //                 row = addLV(s1, row, labelStyle, valueStyle, "Company",    b.getCompanyName(),           "Type",    s(b.getCompanyType()));
// //                 row = addLV(s1, row, labelStyle, valueStyle, "Contact",    b.getContactPersonName(),     "Email",   s(b.getContactPersonEmail()));
// //                 row = addLV(s1, row, labelStyle, valueStyle, "Phone",      s(b.getContactPersonPhone()), "GST",     s(b.getGstNumber()));
// //                 row = addLV(s1, row, labelStyle, valueStyle, "City/State", s(b.getCity()) + ", " + s(b.getState()), "Country", s(b.getCountry()));
// //                 row++;
// //             }

// //             if (rfq.getLocation() != null) {
// //                 Location l = rfq.getLocation();
// //                 row = addSectionHeader(s1, row, "DELIVERY LOCATION", hdrNavy, 4);
// //                 row = addLV(s1, row, labelStyle, valueStyle, "Location Name", l.getLocationName(),    "Type",    s(l.getLocationType()));
// //                 row = addLV(s1, row, labelStyle, valueStyle, "Address",       s(l.getAddressLine1()), "City",    s(l.getCity()));
// //                 row = addLV(s1, row, labelStyle, valueStyle, "State",         s(l.getState()),        "Country", s(l.getCountry()));
// //                 row = addLV(s1, row, labelStyle, valueStyle, "Currency",      s(l.getCurrencyCode()), "Contact", s(l.getLocationContactName()));
// //                 row++;
// //             }

// //             if (rfq.getCreatedByUser() != null && !supplierMode) {
// //                 User u = rfq.getCreatedByUser();
// //                 row = addSectionHeader(s1, row, "CREATED BY", hdrNavy, 4);
// //                 addLV(s1, row, labelStyle, valueStyle,
// //                         "Name", u.getFirstName() + " " + u.getLastName(), "Email", s(u.getEmail()));
// //             }

// //             // ═══════════════════════════════════════════════════════════════
// //             //  SHEET 2: Line Items
// //             // ═══════════════════════════════════════════════════════════════
// //             XSSFSheet s2 = wb.createSheet("Line Items");
// //             setWidths(s2, new int[]{1200, 3500, 8000, 7000, 6000, 2500, 3000, 3500, 4000});
// //             row = 0;
// //             row = addBanner(s2, row, "LINE ITEMS", titleStyle,
// //                     rfq.getRfqNumber() + "  —  " + rfq.getRfqTitle() + "  [" + items.size() + " items]", 9);
// //             String[] itCols = {"#","Item Code","Description","Detailed Description","Specifications","Qty","UOM","Unit Price","Required Date"};
// //             addHeaderRow(s2, row++, hdrGreen, itCols);
// //             int sl = 1;
// //             for (RFQItem it : items) {
// //                 Row r = s2.createRow(row++);
// //                 CellStyle cs = alt(sl, valueStyle, altStyle);
// //                 str(r, 0, String.valueOf(sl++), cs);
// //                 str(r, 1, s(it.getItemCode()), cs);
// //                 str(r, 2, s(it.getItemDescription()), cs);
// //                 str(r, 3, s(it.getItemDescriptionDetailed()), cs);
// //                 str(r, 4, s(it.getSpecifications()), cs);
// //                 num(r, 5, it.getQuantity(), amtStyle);
// //                 str(r, 6, s(it.getUom()), cs);
// //                 num(r, 7, it.getUnitPrice(), amtStyle);
// //                 str(r, 8, fmt(it.getItemRequiredDate()), cs);
// //             }

// //             // ═══════════════════════════════════════════════════════════════
// //             //  SHEET 3: Invited Suppliers (buyer only)
// //             // ═══════════════════════════════════════════════════════════════
// //             if (!supplierMode) {
// //                 XSSFSheet s3 = wb.createSheet("Invited Suppliers");
// //                 setWidths(s3, new int[]{1200, 5500, 4000, 5000, 4000, 3500, 4000, 4000, 3800});

// //                 row = 0;
// //                 row = addBanner(s3, row, "INVITED SUPPLIERS", titleStyle,
// //                         rfq.getRfqNumber() + "  [" + suppliers.size() + " suppliers]", 9);

// //                 Long   awardedSupplierId   = null;
// //                 String awardedPoNumber     = null;
// //                 if (!pos.isEmpty() && pos.get(0).getSupplier() != null) {
// //                     awardedSupplierId = pos.get(0).getSupplier().getId();
// //                     awardedPoNumber   = pos.get(0).getPoNumber();
// //                 }
// //                 final Long   finalAwardedId = awardedSupplierId;
// //                 final String finalPoNumber  = awardedPoNumber;

// //                 CellStyle awardRowStyle = makeAwardedRowStyle(wb);
// //                 CellStyle awardAmtStyle = makeAwardedAmtStyle(wb);

// //                 String[] supCols = {"#","Company","Contact Person","Email","Phone",
// //                                     "Status","Responded At","Quote Amount","Award Status"};
// //                 addHeaderRow(s3, row++, hdrNavy, supCols);
// //                 s3.createFreezePane(0, row);

// //                 sl = 1;
// //                 for (RFQSupplier rs : suppliers) {
// //                     Supplier sup = rs.getSupplier();
// //                     Long supId   = sup != null ? sup.getId() : null;
// //                     boolean isAwarded = finalAwardedId != null && finalAwardedId.equals(supId);

// //                     Row r    = s3.createRow(row++);
// //                     CellStyle cs  = isAwarded ? awardRowStyle : alt(sl, valueStyle, altStyle);
// //                     CellStyle ams = isAwarded ? awardAmtStyle : amtStyle;

// //                     str(r, 0, String.valueOf(sl++), cs);
// //                     str(r, 1, sup != null ? s(sup.getCompanyName())        : "N/A", cs);
// //                     str(r, 2, sup != null ? s(sup.getContactPersonName())  : "N/A", cs);
// //                     str(r, 3, sup != null ? s(sup.getContactPersonEmail()) : "N/A", cs);
// //                     str(r, 4, sup != null ? s(sup.getContactPersonPhone()) : "N/A", cs);
// //                     str(r, 5, s(rs.getStatus()), isAwarded ? cs : statusStyle);
// //                     str(r, 6, fmt(rs.getRespondedAt()), cs);
// //                     num(r, 7, rs.getQuoteAmount(), ams);

// //                     if (isAwarded) {
// //                         String awardLabel = finalPoNumber != null
// //                             ? "★ AWARDED  |  PO: " + finalPoNumber
// //                             : "★ AWARDED";
// //                         str(r, 8, awardLabel, cs);
// //                     } else {
// //                         str(r, 8, "—", cs);
// //                     }
// //                 }

// //                 if (finalAwardedId != null) {
// //                     row++;
// //                     Row legendRow = s3.createRow(row++);
// //                     s3.addMergedRegion(new CellRangeAddress(row - 1, row - 1, 0, 8));
// //                     Cell lc = legendRow.createCell(0);
// //                     lc.setCellValue(
// //                         "★  Gold row = supplier awarded the Purchase Order" +
// //                         (finalPoNumber != null ? "  (PO: " + finalPoNumber + ")" : ""));
// //                     lc.setCellStyle(awardRowStyle);
// //                 }
// //             }

// //             // ═══════════════════════════════════════════════════════════════
// //             //  SHEET 4: Approval History (buyer only)
// //             // ═══════════════════════════════════════════════════════════════
// //             if (!supplierMode) {
// //                 XSSFSheet s4 = wb.createSheet("Approval History");
// //                 setWidths(s4, new int[]{1200, 4000, 4000, 5000, 3500, 3500, 4000, 7000});
// //                 row = 0;
// //                 row = addBanner(s4, row, "APPROVAL HISTORY", titleStyle, rfq.getRfqNumber(), 8);
// //                 String[] apCols = {"#","Level","Level Order","Approver","Email","Status","Action Date","Comments / Remarks"};
// //                 addHeaderRow(s4, row++, hdrNavy, apCols);
// //                 sl = 1;
// //                 for (DynamicRFQApproval ap : approvals) {
// //                     Row r = s4.createRow(row++);
// //                     CellStyle cs = alt(sl, valueStyle, altStyle);
// //                     str(r, 0, String.valueOf(sl++), cs);
// //                     str(r, 1, ap.getHierarchyLevel() != null ? ap.getHierarchyLevel().getLevelName()           : "N/A", cs);
// //                     str(r, 2, ap.getHierarchyLevel() != null ? String.valueOf(ap.getHierarchyLevel().getLevelOrder()) : "N/A", cs);
// //                     str(r, 3, ap.getApproverUser()   != null ? ap.getApproverUser().getFullName()               : "N/A", cs);
// //                     str(r, 4, ap.getApproverUser()   != null ? s(ap.getApproverUser().getEmail())               : "N/A", cs);
// //                     str(r, 5, ap.getStatus()          != null ? ap.getStatus().name()                           : "N/A", statusStyle);
// //                     str(r, 6, fmt(ap.getActionDate()), cs);
// //                     String extra = s(ap.getComments());
// //                     if (ap.getHoldRemarks()    != null) extra += " | HOLD: "    + ap.getHoldRemarks();
// //                     if (ap.getRejectRemarks()  != null) extra += " | REJECT: "  + ap.getRejectRemarks();
// //                     if (ap.getReleaseRemarks() != null) extra += " | RELEASE: " + ap.getReleaseRemarks();
// //                     str(r, 7, extra, cs);
// //                 }
// //                 if (approvals.isEmpty()) {
// //                     Row r = s4.createRow(row);
// //                     str(r, 0, "No approval history recorded yet.", valueStyle);
// //                 }
// //             }

// //             // ═══════════════════════════════════════════════════════════════
// //             //  SHEET 5: Purchase Order overview (buyer only)
// //             // ═══════════════════════════════════════════════════════════════
// //             if (!supplierMode) {
// //                 XSSFSheet s5 = wb.createSheet("Purchase Order");
// //                 s5.setColumnWidth(0, 6000); s5.setColumnWidth(1, 9000);
// //                 s5.setColumnWidth(2, 6000); s5.setColumnWidth(3, 9000);
// //                 row = 0;

// //                 if (pos.isEmpty()) {
// //                     row = addBanner(s5, row, "PURCHASE ORDER", titleStyle,
// //                             "No PO raised for this RFQ yet.", 4);
// //                 } else {
// //                     PurchaseOrder po = pos.get(0);
// //                     row = addBanner(s5, row, "PURCHASE ORDER", titleStyle,
// //                             "PO: " + s(po.getPoNumber())
// //                             + "  |  Status: " + (po.getStatus() != null ? po.getStatus().name() : "—")
// //                             + "  |  RFQ: " + rfq.getRfqNumber(), 4);

// //                     row = addSectionHeader(s5, row, "PO INFORMATION", hdrGreen, 4);
// //                     row = addLV(s5, row, labelStyle, valueStyle, "PO Number",      s(po.getPoNumber()),      "PO Date",           fmt(po.getPoDate()));
// //                     row = addLV(s5, row, labelStyle, valueStyle, "Status",         po.getStatus() != null ? po.getStatus().name() : "—",
// //                                                                                     "Approval Status",       po.getApprovalStatus() != null ? po.getApprovalStatus().name() : "—");
// //                     row = addLV(s5, row, labelStyle, valueStyle, "Currency",       s(po.getCurrencyCode()) + " " + s(po.getCurrencySymbol()),
// //                                                                                     "Amount in Words",       s(po.getAmountInWords()));
// //                     row = addLV(s5, row, labelStyle, valueStyle, "Payment Terms",  s(po.getPaymentTerms()),  "Delivery Terms",    s(po.getDeliveryTerms()));
// //                     row = addLV(s5, row, labelStyle, valueStyle, "Mode of Payment",s(po.getModeOfPayment()), "Dispatched Through",s(po.getDispatchedThrough()));
// //                     row = addLV(s5, row, labelStyle, valueStyle, "Destination",    s(po.getDestination()),   "Reference Quote",   s(po.getReferenceQuoteNo()));
// //                     row = addLV(s5, row, labelStyle, valueStyle, "Approved By",    s(po.getApprovedByName()),"Designation",       s(po.getApprovedByDesignation()));
// //                     row = addLV(s5, row, labelStyle, valueStyle, "Approval Date",  fmt(po.getApprovalDate()),"Buyer Remarks",     s(po.getBuyerRemarks()));
// //                     row++;

// //                     if (po.getSupplier() != null) {
// //                         Supplier sup = po.getSupplier();
// //                         row = addSectionHeader(s5, row, "SUPPLIER", hdrGreen, 4);
// //                         row = addLV(s5, row, labelStyle, valueStyle, "Company", s(sup.getCompanyName()),       "Email",   s(sup.getContactPersonEmail()));
// //                         row = addLV(s5, row, labelStyle, valueStyle, "Contact", s(sup.getContactPersonName()), "Phone",   s(sup.getContactPersonPhone()));
// //                         row++;
// //                     }

// //                     if (po.getDeliveryLocation() != null) {
// //                         Location dl = po.getDeliveryLocation();
// //                         row = addSectionHeader(s5, row, "DELIVERY LOCATION", hdrGreen, 4);
// //                         row = addLV(s5, row, labelStyle, valueStyle, "Name",  dl.getLocationName(), "City",    s(dl.getCity()));
// //                         row = addLV(s5, row, labelStyle, valueStyle, "State", s(dl.getState()),     "Country", s(dl.getCountry()));
// //                         row++;
// //                     }

// //                     row = addSectionHeader(s5, row, "FINANCIAL SUMMARY", hdrGreen, 4);
// //                     row = addLV(s5, row, labelStyle, valueStyle, "Subtotal",    s(po.getSubtotal()),    "Tax Amount",  s(po.getTaxAmount()));
// //                     row = addLV(s5, row, labelStyle, valueStyle, "Grand Total", s(po.getGrandTotal()),  "Tax %",       s(po.getTaxPercentage()));
// //                 }
// //             }

// //             // ═══════════════════════════════════════════════════════════════
// //             //  SHEET 6: PO Line Items (buyer only — only if PO exists)
// //             // ═══════════════════════════════════════════════════════════════
// //             if (!supplierMode && !pos.isEmpty()) {
// //                 PurchaseOrder po = pos.get(0);
// //                 List<POLineItem> poLines = po.getLineItems();
// //                 XSSFSheet s6 = wb.createSheet("PO Line Items");
// //                 setWidths(s6, new int[]{1200, 3000, 7000, 2500, 2000, 2500, 4000, 2000, 2000, 4500, 4000});
// //                 row = 0;
// //                 row = addBanner(s6, row, "PO LINE ITEMS", titleStyle,
// //                         s(po.getPoNumber()) + "  [" + poLines.size() + " items]  |  Currency: " + s(po.getCurrencyCode()), 11);
// //                 String[] plCols = {"#","Item Code","Description","Specifications","UOM","Qty","Unit Rate","Disc %","Tax %","Line Total","Brand/Make/Model"};
// //                 addHeaderRow(s6, row++, hdrGreen, plCols);
// //                 sl = 1;
// //                 for (POLineItem li : poLines) {
// //                     Row r = s6.createRow(row++);
// //                     CellStyle cs = alt(sl, valueStyle, altStyle);
// //                     str(r, 0,  String.valueOf(sl++), cs);
// //                     str(r, 1,  s(li.getItemCode()), cs);
// //                     str(r, 2,  s(li.getItemDescription()), cs);
// //                     str(r, 3,  s(li.getSpecifications()), cs);
// //                     str(r, 4,  s(li.getUom()), cs);
// //                     num(r, 5,  li.getQuantity(), amtStyle);
// //                     num(r, 6,  li.getUnitRate(), amtStyle);
// //                     num(r, 7,  li.getDiscountPercentage(), amtStyle);
// //                     num(r, 8,  li.getTaxPercentage(), amtStyle);
// //                     num(r, 9,  li.getLineTotal(), amtStyle);
// //                     str(r, 10, s(li.getBrandMakeModel()), cs);
// //                 }
// //                 row++;
// //                 Object[][] poTots = {
// //                     {"Subtotal",    po.getSubtotal()},
// //                     {"Tax Amount",  po.getTaxAmount()},
// //                     {"GRAND TOTAL", po.getGrandTotal()}
// //                 };
// //                 for (int t = 0; t < poTots.length; t++) {
// //                     Row tr = s6.createRow(row++);
// //                     boolean ig = (t == poTots.length - 1);
// //                     Cell lc = tr.createCell(9);  lc.setCellValue((String) poTots[t][0]); lc.setCellStyle(ig ? totalStyle : labelStyle);
// //                     Cell vc = tr.createCell(10);
// //                     if (poTots[t][1] instanceof BigDecimal) vc.setCellValue(((BigDecimal) poTots[t][1]).doubleValue()); else vc.setCellValue(0);
// //                     vc.setCellStyle(ig ? totalStyle : amtStyle);
// //                 }
// //             }

// //             // ═══════════════════════════════════════════════════════════════
// //             //  SHEET 7: Invoice overview (buyer only)
// //             // ═══════════════════════════════════════════════════════════════
// //             if (!supplierMode) {
// //                 XSSFSheet s7 = wb.createSheet("Invoice");
// //                 s7.setColumnWidth(0, 6000); s7.setColumnWidth(1, 9000);
// //                 s7.setColumnWidth(2, 6000); s7.setColumnWidth(3, 9000);
// //                 row = 0;

// //                 if (invList.isEmpty()) {
// //                     row = addBanner(s7, row, "INVOICE", titleStyle,
// //                             "No invoice submitted yet for this RFQ.", 4);
// //                 } else {
// //                     Invoice invoice = invList.get(0);
// //                     row = addBanner(s7, row, "INVOICE", titleStyle,
// //                             "Invoice: " + s(invoice.getInvoiceNumber())
// //                             + "  |  Status: " + (invoice.getStatus() != null ? invoice.getStatus().name() : "—")
// //                             + "  |  RFQ: " + rfq.getRfqNumber(), 4);

// //                     row = addSectionHeader(s7, row, "INVOICE DETAILS", hdrPurple, 4);
// //                     row = addLV(s7, row, labelStyle, valueStyle, "Invoice Number", s(invoice.getInvoiceNumber()),   "Status",          invoice.getStatus() != null ? invoice.getStatus().name() : "—");
// //                     row = addLV(s7, row, labelStyle, valueStyle, "Invoice Date",   fmt(invoice.getInvoiceDate()),   "Due Date",        fmt(invoice.getDueDate()));
// //                     row = addLV(s7, row, labelStyle, valueStyle, "PO Number",      s(invoice.getPoNumber()),        "Currency",        s(invoice.getCurrency()));
// //                     row = addLV(s7, row, labelStyle, valueStyle, "Supplier",       s(invoice.getSupplierName()),    "Supplier Email",  s(invoice.getSupplierEmail()));
// //                     row = addLV(s7, row, labelStyle, valueStyle, "Buyer Company",  s(invoice.getBuyerCompanyName()),"Payment Terms",   s(invoice.getPaymentTerms()));
// //                     row = addLV(s7, row, labelStyle, valueStyle, "Subtotal",       s(invoice.getSubtotal()),        "Tax Amount",      s(invoice.getTaxAmount()));
// //                     row = addLV(s7, row, labelStyle, valueStyle, "Total Amount",   s(invoice.getTotalAmount()),     "Approved By",     s(invoice.getApprovedRejectedBy()));
// //                     row = addLV(s7, row, labelStyle, valueStyle, "Approval Date",  fmt(invoice.getApprovedRejectedAt()), "Approval Remarks", s(invoice.getApprovalRemarks()));
// //                     row++;

// //                     if (invoice.getBankName() != null) {
// //                         row = addSectionHeader(s7, row, "BANK DETAILS", hdrPurple, 4);
// //                         row = addLV(s7, row, labelStyle, valueStyle, "Bank Name",      s(invoice.getBankName()),          "Account Name",   s(invoice.getBankAccountName()));
// //                         row = addLV(s7, row, labelStyle, valueStyle, "Account Number", s(invoice.getBankAccountNumber()), "IFSC Code",      s(invoice.getBankIfscCode()));
// //                         row = addLV(s7, row, labelStyle, valueStyle, "SWIFT Code",     s(invoice.getBankSwiftCode()),     "Notes",          s(invoice.getNotes()));
// //                         row++;
// //                     }
// //                 }
// //             }

// //             // ═══════════════════════════════════════════════════════════════
// //             //  SHEET 8: Invoice Line Items (buyer only — only if invoice exists)
// //             // ═══════════════════════════════════════════════════════════════
// //             if (!supplierMode && !invList.isEmpty()) {
// //                 Invoice invoice = invList.get(0);
// //                 List<InvoiceLineItem> invLines = invoice.getLineItems();
// //                 XSSFSheet s8 = wb.createSheet("Invoice Line Items");
// //                 setWidths(s8, new int[]{1200, 3000, 7000, 2500, 2000, 2500, 4000, 2000, 2000, 4500});
// //                 row = 0;
// //                 row = addBanner(s8, row, "INVOICE LINE ITEMS", titleStyle,
// //                         s(invoice.getInvoiceNumber()) + "  [" + invLines.size() + " items]", 10);
// //                 String[] ilCols = {"#","Item Code","Description","HSN/SAC","UOM","Quantity","Unit Price","Disc %","Tax %","Line Total"};
// //                 addHeaderRow(s8, row++, hdrPurple, ilCols);
// //                 sl = 1;
// //                 for (InvoiceLineItem li : invLines) {
// //                     Row r = s8.createRow(row++);
// //                     CellStyle cs = alt(sl, valueStyle, altStyle);
// //                     str(r, 0, String.valueOf(sl++), cs);
// //                     str(r, 1, s(li.getItemCode()), cs);
// //                     str(r, 2, s(li.getItemDescription()), cs);
// //                     str(r, 3, s(li.getHsnSacCode()), cs);
// //                     str(r, 4, s(li.getUom()), cs);
// //                     num(r, 5, li.getQuantity(), amtStyle);
// //                     num(r, 6, li.getUnitPrice(), amtStyle);
// //                     num(r, 7, li.getDiscountPercentage(), amtStyle);
// //                     num(r, 8, li.getTaxPercentage(), amtStyle);
// //                     num(r, 9, li.getLineTotal(), amtStyle);
// //                 }
// //                 row++;
// //                 Object[][] invTots = {
// //                     {"Subtotal",      invoice.getSubtotal()},
// //                     {"Tax Amount",    invoice.getTaxAmount()},
// //                     {"TOTAL AMOUNT",  invoice.getTotalAmount()}
// //                 };
// //                 for (int t = 0; t < invTots.length; t++) {
// //                     Row tr = s8.createRow(row++);
// //                     boolean ig = (t == invTots.length - 1);
// //                     Cell lc = tr.createCell(8);  lc.setCellValue((String) invTots[t][0]); lc.setCellStyle(ig ? totalStyle : labelStyle);
// //                     Cell vc = tr.createCell(9);
// //                     if (invTots[t][1] instanceof BigDecimal) vc.setCellValue(((BigDecimal) invTots[t][1]).doubleValue()); else vc.setCellValue(0);
// //                     vc.setCellStyle(ig ? totalStyle : amtStyle);
// //                 }
// //             }

// //             // ═══════════════════════════════════════════════════════════════
// //             //  SHEET 9: GRN — ALL DELIVERY BATCHES (buyer only)
// //             //
// //             //  CHANGED: Now loops over all GRNs in `grns` list.
// //             //  Shows a batch summary table at the top, then one detailed
// //             //  section per GRN batch (Batch 1, Batch 2 …).
// //             // ═══════════════════════════════════════════════════════════════
// //             if (!supplierMode) {
// //                 XSSFSheet s9 = wb.createSheet("GRN");
// //                 s9.setColumnWidth(0, 6000); s9.setColumnWidth(1, 9000);
// //                 s9.setColumnWidth(2, 6000); s9.setColumnWidth(3, 9000);
// //                 row = 0;

// //                 if (grns.isEmpty()) {
// //                     row = addBanner(s9, row, "GOODS RECEIPT NOTE", titleStyle,
// //                             "No GRN created yet for this RFQ.", 4);
// //                 } else {
// //                     // ── Banner ────────────────────────────────────────────
// //                     row = addBanner(s9, row, "GOODS RECEIPT NOTE — ALL DELIVERY BATCHES", titleStyle,
// //                             rfq.getRfqNumber()
// //                             + "  |  Total GRN Batches: " + grns.size()
// //                             + "  |  PO: " + (pos.isEmpty() ? "—" : s(pos.get(0).getPoNumber())), 4);

// //                     // ── Quick summary table across all batches ────────────
// //                     row = addSectionHeader(s9, row, "DELIVERY BATCH SUMMARY", hdrOrange, 4);

// //                     Row sumHdr = s9.createRow(row++);
// //                     sumHdr.setHeightInPoints(18);
// //                     str(sumHdr, 0, "Batch #",        hdrOrange);
// //                     str(sumHdr, 1, "GRN Number",     hdrOrange);
// //                     str(sumHdr, 2, "Received Date",  hdrOrange);
// //                     str(sumHdr, 3, "Status",         hdrOrange);

// //                     int batchNum = 1;
// //                     for (GRN grn : grns) {
// //                         Row sumRow = s9.createRow(row++);
// //                         CellStyle cs = alt(batchNum, valueStyle, altStyle);
// //                         str(sumRow, 0, "Batch " + batchNum++, cs);
// //                         str(sumRow, 1, s(grn.getGrnNumber()), cs);
// //                         str(sumRow, 2, fmt(grn.getReceivedDate()), cs);
// //                         str(sumRow, 3, grn.getStatus() != null ? grn.getStatus().name() : "—", statusStyle);
// //                     }
// //                     row++;

// //                     // ── Detailed section per batch ────────────────────────
// //                     batchNum = 1;
// //                     for (GRN grn : grns) {
// //                         row = addSectionHeader(s9, row,
// //                                 "BATCH " + batchNum + " — " + s(grn.getGrnNumber())
// //                                 + "  |  Status: " + (grn.getStatus() != null ? grn.getStatus().name() : "—"),
// //                                 hdrOrange, 4);

// //                         row = addLV(s9, row, labelStyle, valueStyle, "GRN Number",    s(grn.getGrnNumber()),      "Status",         grn.getStatus() != null ? grn.getStatus().name() : "—");
// //                         row = addLV(s9, row, labelStyle, valueStyle, "Received Date", fmt(grn.getReceivedDate()), "PO Number",      s(grn.getPoNumber()));
// //                         row = addLV(s9, row, labelStyle, valueStyle, "Supplier",      s(grn.getSupplierName()),   "Invoice Number", s(grn.getInvoiceNumber()));
// //                         row = addLV(s9, row, labelStyle, valueStyle, "Received By",   s(grn.getReceivedByName()), "QA Inspector",   s(grn.getInspectedByName()));
// //                         row = addLV(s9, row, labelStyle, valueStyle, "Approved By",   s(grn.getApprovedByName()), "Approved At",    fmt(grn.getApprovedAt()));
// //                         row = addLV(s9, row, labelStyle, valueStyle, "Transporter",   s(grn.getTransporterName()),"Vehicle No.",    s(grn.getVehicleNumber()));
// //                         row = addLV(s9, row, labelStyle, valueStyle, "Challan No.",   s(grn.getDeliveryChallanNumber()), "LR Number", s(grn.getLrNumber()));
// //                         row = addLV(s9, row, labelStyle, valueStyle, "Total Ordered", s(grn.getTotalOrderedValue()),  "Total Received", s(grn.getTotalReceivedValue()));
// //                         row = addLV(s9, row, labelStyle, valueStyle, "Total Rejected",s(grn.getTotalRejectedValue()), "Remarks",        s(grn.getRemarks()));
// //                         row++;
// //                         batchNum++;
// //                     }
// //                 }
// //             }

// //             // ═══════════════════════════════════════════════════════════════
// //             //  SHEET 10: GRN Line Items — ALL BATCHES (buyer only)
// //             //
// //             //  CHANGED: Shows every GRN batch in sequence.
// //             //  Each batch has a coloured separator row, then its line items,
// //             //  then a subtotal row. A grand-total row appears at the end.
// //             // ═══════════════════════════════════════════════════════════════
// //             if (!supplierMode && !grns.isEmpty()) {
// //                 XSSFSheet s10 = wb.createSheet("GRN Line Items");
// //                 setWidths(s10, new int[]{1200, 1800, 3000, 6500, 2000, 2500, 2500, 2500, 2500, 2500, 4000, 3500, 5000});
// //                 row = 0;

// //                 int totalLineCount = grns.stream()
// //                         .mapToInt(g -> g.getLineItems() != null ? g.getLineItems().size() : 0).sum();

// //                 row = addBanner(s10, row, "GRN LINE ITEMS — ALL DELIVERY BATCHES", titleStyle,
// //                         rfq.getRfqNumber()
// //                         + "  |  " + grns.size() + " GRN Batch(es)"
// //                         + "  |  " + totalLineCount + " Total Line Item(s)", 13);

// //                 // Frozen column header row
// //                 String[] glCols = {"#","Batch","Item Code","Description","UOM",
// //                         "Ordered","Received","Defective","Rejected","Accepted",
// //                         "PO Rate","Accepted Value","QA Remarks"};
// //                 addHeaderRow(s10, row++, hdrOrange, glCols);
// //                 s10.createFreezePane(0, row);

// //                 // Styles for batch separator and subtotals
// //                 CellStyle batchSubLabelStyle = makeBatchSubtotalLabelStyle(wb);
// //                 CellStyle batchSubAmtStyle   = makeBatchSubtotalAmtStyle(wb);

// //                 int batchNum = 1;
// //                 int lineNum  = 1;

// //                 for (GRN grn : grns) {
// //                     List<GRNLineItem> grnLines = grn.getLineItems();
// //                     if (grnLines == null || grnLines.isEmpty()) { batchNum++; continue; }

// //                     // ── Batch separator row ──────────────────────────────
// //                     Row batchHdrRow = s10.createRow(row++);
// //                     batchHdrRow.setHeightInPoints(20);
// //                     s10.addMergedRegion(new CellRangeAddress(row - 1, row - 1, 0, 12));
// //                     Cell batchCell = batchHdrRow.createCell(0);
// //                     batchCell.setCellValue(
// //                             "▶  BATCH " + batchNum
// //                             + "  |  GRN: " + s(grn.getGrnNumber())
// //                             + "  |  Received: " + fmt(grn.getReceivedDate())
// //                             + "  |  Status: " + (grn.getStatus() != null ? grn.getStatus().name() : "—")
// //                             + "  |  " + grnLines.size() + " item(s)");
// //                     batchCell.setCellStyle(makeBatchSeparatorStyle(wb, batchNum));

// //                     // ── Line items for this batch ────────────────────────
// //                     for (GRNLineItem li : grnLines) {
// //                         Row r = s10.createRow(row++);
// //                         boolean hasRej = li.getRejectedQuantity() != null
// //                                 && li.getRejectedQuantity().compareTo(BigDecimal.ZERO) > 0;
// //                         CellStyle cs  = hasRej ? warnStyle : alt(lineNum, valueStyle, altStyle);
// //                         CellStyle ams = hasRej ? warnStyle : amtStyle;

// //                         str(r, 0,  String.valueOf(lineNum++), cs);
// //                         str(r, 1,  "Batch " + batchNum, cs);
// //                         str(r, 2,  s(li.getItemCode()), cs);
// //                         str(r, 3,  s(li.getItemDescription()), cs);
// //                         str(r, 4,  s(li.getUom()), cs);
// //                         num(r, 5,  li.getOrderedQuantity(), ams);
// //                         num(r, 6,  li.getReceivedQuantity(), ams);
// //                         num(r, 7,  li.getDefectiveQuantity(), ams);
// //                         num(r, 8,  li.getRejectedQuantity(), ams);
// //                         num(r, 9,  li.getAcceptedQuantity(), ams);
// //                         num(r, 10, li.getPoUnitRate(), ams);
// //                         num(r, 11, li.getAcceptedValue(), ams);
// //                         str(r, 12, s(li.getQaRemarks()), cs);
// //                     }

// //                     // ── Batch subtotal row ───────────────────────────────
// //                     BigDecimal batchAccepted = grnLines.stream()
// //                             .filter(li -> li.getAcceptedValue() != null)
// //                             .map(GRNLineItem::getAcceptedValue)
// //                             .reduce(BigDecimal.ZERO, BigDecimal::add);
// //                     BigDecimal batchRejected = grnLines.stream()
// //                             .filter(li -> li.getRejectedValue() != null)
// //                             .map(GRNLineItem::getRejectedValue)
// //                             .reduce(BigDecimal.ZERO, BigDecimal::add);

// //                     Row subTotRow = s10.createRow(row++);
// //                     for (int c = 0; c < 13; c++) {
// //                         Cell nc = subTotRow.createCell(c);
// //                         nc.setCellStyle(batchSubLabelStyle);
// //                     }
// //                     subTotRow.getCell(0).setCellValue("Batch " + batchNum + " Subtotals →");
// //                     subTotRow.getCell(5).setCellValue(
// //                             "Ordered Val: " + (grn.getTotalOrderedValue() != null ? grn.getTotalOrderedValue().toPlainString() : "0"));

// //                     Cell accCell = subTotRow.createCell(11);
// //                     accCell.setCellValue(batchAccepted.doubleValue());
// //                     accCell.setCellStyle(batchSubAmtStyle);

// //                     Cell rejCell = subTotRow.createCell(8);
// //                     rejCell.setCellValue(batchRejected.doubleValue());
// //                     rejCell.setCellStyle(batchSubAmtStyle);

// //                     row++; // blank gap between batches
// //                     batchNum++;
// //                 }

// //                 // ── Grand total across ALL batches ───────────────────────
// //                 BigDecimal grandAccepted = grns.stream()
// //                         .filter(g -> g.getTotalReceivedValue() != null)
// //                         .map(GRN::getTotalReceivedValue)
// //                         .reduce(BigDecimal.ZERO, BigDecimal::add);
// //                 BigDecimal grandRejected = grns.stream()
// //                         .filter(g -> g.getTotalRejectedValue() != null)
// //                         .map(GRN::getTotalRejectedValue)
// //                         .reduce(BigDecimal.ZERO, BigDecimal::add);

// //                 Row grandRow = s10.createRow(row++);
// //                 for (int c = 0; c < 13; c++) {
// //                     Cell nc = grandRow.createCell(c);
// //                     nc.setCellStyle(totalStyle);
// //                 }
// //                 grandRow.getCell(0).setCellValue("GRAND TOTAL — All " + grns.size() + " Batch(es)");
// //                 grandRow.getCell(11).setCellValue(grandAccepted.doubleValue());
// //                 grandRow.getCell(8).setCellValue(grandRejected.doubleValue());
// //             }

// //             // ═══════════════════════════════════════════════════════════════
// //             //  SHEET 11: 3-Way Match overview (buyer only)
// //             // ═══════════════════════════════════════════════════════════════
// //             if (!supplierMode) {
// //                 XSSFSheet s11 = wb.createSheet("3-Way Match");
// //                 s11.setColumnWidth(0, 6000); s11.setColumnWidth(1, 9000);
// //                 s11.setColumnWidth(2, 6000); s11.setColumnWidth(3, 9000);
// //                 row = 0;

// //                 if (matches.isEmpty()) {
// //                     row = addBanner(s11, row, "3-WAY MATCH", titleStyle,
// //                             "3-Way Match not yet performed for this RFQ.", 4);
// //                 } else {
// //                     ThreeWayMatch match = matches.get(0);
// //                     boolean isFullMatch = match.getMatchStatus() == ThreeWayMatchStatus.MATCHED
// //                             || match.getMatchStatus() == ThreeWayMatchStatus.OVERRIDDEN_APPROVED;

// //                     CellStyle matchBanner = isFullMatch ? makeBannerMatchStyle(wb) : makeBannerWarnStyle(wb);

// //                     row = addBannerCustomStyle(s11, row, "3-WAY MATCH REPORT", matchBanner,
// //                             "Match #" + match.getId() + "  v" + match.getMatchVersion()
// //                             + "  |  Status: " + (match.getMatchStatus() != null ? match.getMatchStatus().name() : "—")
// //                             + "  |  RFQ: " + rfq.getRfqNumber(), 4);

// //                     row = addSectionHeader(s11, row, "MATCH SUMMARY", hdrNavy, 4);
// //                     row = addLV(s11, row, labelStyle, valueStyle, "Match Status",   match.getMatchStatus() != null ? match.getMatchStatus().name() : "—",
// //                                                                                     "Match Version",    String.valueOf(match.getMatchVersion()));
// //                     row = addLV(s11, row, labelStyle, valueStyle, "Tolerance %",    s(match.getTolerancePercentage()),  "Variance Amount",  s(match.getVarianceAmount()));
// //                     row = addLV(s11, row, labelStyle, valueStyle, "Variance %",     s(match.getVariancePercentage()),   "Matched Lines",    String.valueOf(match.getTotalMatchedLines()));
// //                     row = addLV(s11, row, labelStyle, valueStyle, "Mismatch Lines", String.valueOf(match.getTotalMismatchLines()), "Approved Payment", s(match.getApprovedPaymentAmount()));
// //                     row = addLV(s11, row, labelStyle, valueStyle, "PO Total Value", s(match.getPoTotalValue()),         "GRN Accepted Value",s(match.getGrnAcceptedValue()));
// //                     row = addLV(s11, row, labelStyle, valueStyle, "Invoice Total",  s(match.getInvoiceTotalValue()),    "Resolution",       match.getResolution() != null ? match.getResolution().name() : "PENDING");
// //                     row = addLV(s11, row, labelStyle, valueStyle, "Resolved By",    s(match.getResolvedByName()),       "Resolved At",      fmt(match.getResolvedAt()));
// //                     if (match.getResolutionRemarks() != null)
// //                         row = addLV(s11, row, labelStyle, valueStyle, "Resolution Remarks", s(match.getResolutionRemarks()), "", "");
// //                     if (match.getMatchSummary() != null) {
// //                         row++;
// //                         Row sumRow = s11.createRow(row++);
// //                         s11.addMergedRegion(new CellRangeAddress(row - 1, row - 1, 0, 3));
// //                         Cell sc = sumRow.createCell(0);
// //                         sc.setCellValue(match.getMatchSummary());
// //                         sc.setCellStyle(isFullMatch ? lowestStyle : warnStyle);
// //                     }
// //                 }
// //             }

// //             // ═══════════════════════════════════════════════════════════════
// //             //  SHEET 12: Match Line Results (buyer only — only if match exists)
// //             // ═══════════════════════════════════════════════════════════════
// //             if (!supplierMode && !matches.isEmpty()) {
// //                 ThreeWayMatch match = matches.get(0);
// //                 List<MatchLineResult> lineResults = match.getLineResults();
// //                 XSSFSheet s12 = wb.createSheet("Match Line Results");
// //                 setWidths(s12, new int[]{1200, 3000, 6500, 2000, 2500, 2500, 2500, 3000, 3000, 3000, 3000, 3000, 3500, 6000});
// //                 row = 0;
// //                 row = addBanner(s12, row, "3-WAY MATCH — LINE RESULTS", titleStyle,
// //                         "Invoice: " + s(match.getInvoiceNumber()) + " ↔ GRN: " + s(match.getGrnNumber())
// //                         + "  |  " + lineResults.size() + " lines", 14);
// //                 String[] mCols = {"#","Item Code","Description","UOM",
// //                                   "PO Qty","GRN Accepted","Inv Qty","Qty Var%",
// //                                   "PO Rate","Inv Price","Price Var%",
// //                                   "GRN Value","Inv Value","Status"};
// //                 addHeaderRow(s12, row++, hdrNavy, mCols);
// //                 sl = 1;
// //                 for (MatchLineResult lr : lineResults) {
// //                     Row r = s12.createRow(row++);
// //                     boolean lineOk   = lr.getLineMatchStatus() == MatchLineResult.LineMatchStatus.MATCHED;
// //                     boolean lineFail = lr.getLineMatchStatus() == MatchLineResult.LineMatchStatus.NOT_IN_PO
// //                             || lr.getLineMatchStatus() == MatchLineResult.LineMatchStatus.NOT_RECEIVED;
// //                     CellStyle cs  = lineFail ? warnStyle : lineOk ? lowestStyle : alt(sl, valueStyle, altStyle);
// //                     CellStyle ams = lineFail ? warnStyle : lineOk ? lowestStyle : amtStyle;
// //                     str(r, 0,  String.valueOf(sl++), cs);
// //                     str(r, 1,  s(lr.getItemCode()), cs);
// //                     str(r, 2,  s(lr.getItemDescription()), cs);
// //                     str(r, 3,  s(lr.getUom()), cs);
// //                     num(r, 4,  lr.getPoOrderedQuantity(), ams);
// //                     num(r, 5,  lr.getGrnAcceptedQuantity(), ams);
// //                     num(r, 6,  lr.getInvoiceQuantity(), ams);
// //                     num(r, 7,  lr.getQuantityVariancePct(), ams);
// //                     num(r, 8,  lr.getPoUnitRate(), ams);
// //                     num(r, 9,  lr.getInvoiceUnitPrice(), ams);
// //                     num(r, 10, lr.getPriceVariancePct(), ams);
// //                     num(r, 11, lr.getGrnAcceptedValue(), ams);
// //                     num(r, 12, lr.getInvoiceLineValue(), ams);
// //                     str(r, 13, lr.getLineMatchStatus() != null ? lr.getLineMatchStatus().name() : "—", statusStyle);
// //                 }
// //             }

// //             wb.write(out);
// //             return out.toByteArray();
// //         }
// //     }

// //     // =========================================================================
// //     //  2.  RFQ LIST REPORT
// //     // =========================================================================
// //     @Transactional(readOnly = true)
// //     public byte[] generateRFQListExcel(Long buyerId, String statusFilter) throws Exception {

// //         List<RFQ> rfqs = rfqRepository.findByBuyerId(buyerId);
// //         if (statusFilter != null && !statusFilter.isBlank() && !statusFilter.equalsIgnoreCase("ALL")) {
// //             final String sf = statusFilter.trim().toUpperCase();
// //             rfqs = rfqs.stream()
// //                     .filter(r -> sf.equals(r.getStatus() != null ? r.getStatus().name() : ""))
// //                     .collect(Collectors.toList());
// //         }

// //         try (XSSFWorkbook wb = new XSSFWorkbook();
// //              ByteArrayOutputStream out = new ByteArrayOutputStream()) {

// //             CellStyle titleStyle  = makeTitleStyle(wb);
// //             CellStyle hdrNavy    = makeHeaderStyle(wb, rgb(0x1e, 0x40, 0x88));
// //             CellStyle valueStyle  = makeValueStyle(wb);
// //             CellStyle altStyle    = makeAltRowStyle(wb);
// //             CellStyle amtStyle    = makeAmountStyle(wb);
// //             CellStyle statusStyle = makeStatusStyle(wb);
// //             CellStyle statLbl    = makeStatLabelStyle(wb);
// //             CellStyle statVal    = makeStatValueStyle(wb);

// //             XSSFSheet sheet = wb.createSheet("RFQ List");
// //             setWidths(sheet, new int[]{1200, 3500, 7000, 3500, 3000, 3000, 3000, 2500, 2500, 3500, 5000});

// //             int row = 0;
// //             String filterDesc = (statusFilter != null && !statusFilter.isBlank() && !statusFilter.equalsIgnoreCase("ALL"))
// //                     ? "  |  Status: " + statusFilter : "  |  All Statuses";
// //             row = addBanner(sheet, row, "RFQ LIST REPORT", titleStyle,
// //                     "Generated: " + LocalDateTime.now().format(DTTM_FMT) + filterDesc, 11);

// //             long cntDraft     = rfqs.stream().filter(r -> "DRAFT".equals(en(r.getStatus()))).count();
// //             long cntAwaiting  = rfqs.stream().filter(r -> "AWAITING_APPROVAL".equals(en(r.getStatus()))).count();
// //             long cntPublished = rfqs.stream().filter(r -> "PUBLISHED".equals(en(r.getStatus()))).count();
// //             long cntClosed    = rfqs.stream().filter(r -> "CLOSED".equals(en(r.getStatus()))).count();
// //             long cntHold      = rfqs.stream().filter(r -> "HOLD".equals(en(r.getStatus()))).count();

// //             Row sr = sheet.createRow(row++);
// //             sr.setHeightInPoints(22);
// //             str(sr, 0,  "Total RFQs: " + rfqs.size(), statLbl);
// //             str(sr, 2,  "Draft: "      + cntDraft,     statVal);
// //             str(sr, 4,  "Awaiting: "   + cntAwaiting,  statVal);
// //             str(sr, 6,  "Published: "  + cntPublished,  statVal);
// //             str(sr, 8,  "Closed: "     + cntClosed,    statVal);
// //             str(sr, 10, "On Hold: "    + cntHold,      statVal);
// //             row++;

// //             String[] cols = {"#","RFQ Number","Title","Status","Priority",
// //                     "Issue Date","Due Date","Suppliers","Items","Approval Status","Created By"};
// //             addHeaderRow(sheet, row++, hdrNavy, cols);
// //             sheet.setAutoFilter(new CellRangeAddress(row - 1, row - 1, 0, cols.length - 1));

// //             int sl = 1;
// //             for (RFQ rfq : rfqs) {
// //                 Row r = sheet.createRow(row++);
// //                 CellStyle cs = alt(sl, valueStyle, altStyle);
// //                 str(r, 0, String.valueOf(sl++), cs);
// //                 str(r, 1, s(rfq.getRfqNumber()), cs);
// //                 str(r, 2, s(rfq.getRfqTitle()), cs);
// //                 str(r, 3, en(rfq.getStatus()), statusStyle);
// //                 str(r, 4, en(rfq.getPriority()), cs);
// //                 str(r, 5, fmt(rfq.getIssueDate()), cs);
// //                 str(r, 6, fmt(rfq.getDueDate()), cs);
// //                 int supCnt = 0; try { supCnt = rfq.getSelectedSuppliers() != null ? rfq.getSelectedSuppliers().size() : 0; } catch (Exception ignored) {}
// //                 int itmCnt = 0; try { itmCnt = rfq.getItems() != null ? rfq.getItems().size() : 0; } catch (Exception ignored) {}
// //                 str(r, 7, String.valueOf(supCnt), cs);
// //                 str(r, 8, String.valueOf(itmCnt), cs);
// //                 str(r, 9, en(rfq.getApprovalStatus()), statusStyle);
// //                 str(r, 10, rfq.getCreatedByUser() != null
// //                         ? rfq.getCreatedByUser().getFirstName() + " " + rfq.getCreatedByUser().getLastName()
// //                         : "N/A", cs);
// //             }

// //             wb.write(out);
// //             return out.toByteArray();
// //         }
// //     }

// //     // =========================================================================
// //     //  3.  QUOTE COMPARISON EXCEL
// //     // =========================================================================
// //     @Transactional(readOnly = true)
// //     public byte[] generateQuoteComparisonExcel(Long rfqId) throws Exception {

// //         RFQ rfq = rfqRepository.findByIdWithAllDetails(rfqId)
// //                 .orElseThrow(() -> new RuntimeException("RFQ not found: " + rfqId));

// //         List<RFQItem>           items     = rfqItemRepository.findByRfqId(rfqId);
// //         List<RFQSupplier>       suppliers = rfqSupplierRepository.findByRFQAndStatus(rfqId, "RESPONDED");
// //         List<SupplierQuoteItem> quotes    = quoteItemRepository.findByRfqId(rfqId);
// //         String currency = rfq.getLocation() != null ? rfq.getLocation().getCurrencyCode() : "INR";

// //         try (XSSFWorkbook wb = new XSSFWorkbook();
// //              ByteArrayOutputStream out = new ByteArrayOutputStream()) {

// //             CellStyle titleStyle  = makeTitleStyle(wb);
// //             CellStyle hdrNavy    = makeHeaderStyle(wb, rgb(0x1e, 0x40, 0x88));
// //             CellStyle hdrGreen   = makeHeaderStyle(wb, rgb(0x1b, 0x5e, 0x20));
// //             CellStyle valueStyle  = makeValueStyle(wb);
// //             CellStyle altStyle    = makeAltRowStyle(wb);
// //             CellStyle amtStyle    = makeAmountStyle(wb);
// //             CellStyle lowestStyle = makeLowestStyle(wb);
// //             CellStyle totalStyle  = makeTotalStyle(wb);
// //             CellStyle metaStyle   = makeMetaStyle(wb);

// //             XSSFSheet sheet = wb.createSheet("Quote Comparison");

// //             int numSup  = suppliers.size();
// //             int fixedCols = 6;
// //             int supCols   = 7;
// //             int totalCols = fixedCols + numSup * supCols;

// //             sheet.setColumnWidth(0, 1200); sheet.setColumnWidth(1, 3500);
// //             sheet.setColumnWidth(2, 7000); sheet.setColumnWidth(3, 6000);
// //             sheet.setColumnWidth(4, 2500); sheet.setColumnWidth(5, 2500);
// //             for (int s = 0; s < numSup; s++) {
// //                 int b = fixedCols + s * supCols;
// //                 sheet.setColumnWidth(b,     3000); sheet.setColumnWidth(b + 1, 4500);
// //                 sheet.setColumnWidth(b + 2, 2500); sheet.setColumnWidth(b + 3, 3000);
// //                 sheet.setColumnWidth(b + 4, 3000); sheet.setColumnWidth(b + 5, 4000);
// //                 sheet.setColumnWidth(b + 6, 5000);
// //             }

// //             int row = 0;
// //             row = addBanner(sheet, row, "QUOTE COMPARISON MATRIX", titleStyle,
// //                     rfq.getRfqNumber() + "  —  " + rfq.getRfqTitle()
// //                             + "  |  Currency: " + currency
// //                             + "  |  Suppliers: " + numSup
// //                             + "  |  Generated: " + LocalDateTime.now().format(DTTM_FMT), totalCols);

// //             Row meta = sheet.createRow(row++);
// //             str(meta, 0, "Buyer: " + (rfq.getBuyer() != null ? rfq.getBuyer().getCompanyName() : "N/A"), metaStyle);
// //             str(meta, 3, "Due Date: " + fmt(rfq.getDueDate()), metaStyle);
// //             str(meta, 6, "Items: " + items.size(), metaStyle);
// //             row++;

// //             Row supHdrRow = sheet.createRow(row++);
// //             str(supHdrRow, 0, "Sl No",       hdrNavy); str(supHdrRow, 1, "Item Code",     hdrNavy);
// //             str(supHdrRow, 2, "Item Description", hdrNavy); str(supHdrRow, 3, "Specifications", hdrNavy);
// //             str(supHdrRow, 4, "Qty",          hdrNavy); str(supHdrRow, 5, "UOM",           hdrNavy);
// //             for (int s = 0; s < numSup; s++) {
// //                 RFQSupplier rs = suppliers.get(s);
// //                 String sName = rs.getSupplier() != null ? rs.getSupplier().getCompanyName() : "Supplier " + (s + 1);
// //                 String sCurr = rs.getSupplier() != null ? determineSupplierCurrency(rfq, rs.getSupplier()) : currency;
// //                 int b = fixedCols + s * supCols;
// //                 Cell sc = supHdrRow.createCell(b);
// //                 sc.setCellValue(sName + "  [" + sCurr + "]");
// //                 sc.setCellStyle(hdrGreen);
// //                 sheet.addMergedRegion(new CellRangeAddress(row - 1, row - 1, b, b + supCols - 1));
// //             }

// //             Row subRow = sheet.createRow(row++);
// //             for (int c = 0; c < fixedCols; c++) subRow.createCell(c).setCellStyle(hdrNavy);
// //             for (int s = 0; s < numSup; s++) {
// //                 int b = fixedCols + s * supCols;
// //                 String[] sub = {"Unit Rate","Grand Total","Tax %","Delivery (d)","Warranty (m)","Brand/Make/Model","Remarks"};
// //                 for (int c = 0; c < sub.length; c++) {
// //                     Cell cell = subRow.createCell(b + c);
// //                     cell.setCellValue(sub[c]);
// //                     cell.setCellStyle(hdrNavy);
// //                 }
// //             }

// //             int sl = 1;
// //             for (RFQItem item : items) {
// //                 double lowestRate = Double.MAX_VALUE; Long lowestSupId = null;
// //                 for (RFQSupplier rs : suppliers) {
// //                     if (rs.getSupplier() == null) continue;
// //                     Optional<SupplierQuoteItem> qOpt = findQuote(quotes, item.getId(), rs.getSupplier().getId());
// //                     if (qOpt.isPresent() && qOpt.get().getUnitRate() != null
// //                             && qOpt.get().getUnitRate().doubleValue() < lowestRate) {
// //                         lowestRate = qOpt.get().getUnitRate().doubleValue();
// //                         lowestSupId = rs.getSupplier().getId();
// //                     }
// //                 }
// //                 final Long lowestId = lowestSupId;

// //                 Row r = sheet.createRow(row++);
// //                 CellStyle cs = alt(sl, valueStyle, altStyle);
// //                 str(r, 0, String.valueOf(sl++), cs); str(r, 1, s(item.getItemCode()), cs);
// //                 str(r, 2, s(item.getItemDescription()), cs); str(r, 3, s(item.getSpecifications()), cs);
// //                 num(r, 4, item.getQuantity(), amtStyle); str(r, 5, s(item.getUom()), cs);

// //                 for (int idx = 0; idx < numSup; idx++) {
// //                     RFQSupplier rs = suppliers.get(idx);
// //                     if (rs.getSupplier() == null) continue;
// //                     Long sid = rs.getSupplier().getId();
// //                     int  b   = fixedCols + idx * supCols;
// //                     boolean isLowest = sid.equals(lowestId);
// //                     CellStyle qs = isLowest ? lowestStyle : amtStyle;
// //                     CellStyle ts = isLowest ? lowestStyle : valueStyle;

// //                     Optional<SupplierQuoteItem> qOpt = findQuote(quotes, item.getId(), sid);
// //                     if (qOpt.isPresent()) {
// //                         SupplierQuoteItem qi = qOpt.get();
// //                         num(r, b,     qi.getUnitRate(), qs);   num(r, b + 1, qi.getGrandTotal(), qs);
// //                         num(r, b + 2, qi.getTaxPercentage(), ts);
// //                         str(r, b + 3, qi.getDeliveryDays()   != null ? qi.getDeliveryDays()   + "d" : "—", ts);
// //                         str(r, b + 4, qi.getWarrantyMonths() != null ? qi.getWarrantyMonths() + "m" : "—", ts);
// //                         str(r, b + 5, joinNonNull("/", qi.getBrandOffered(), qi.getMakeModel()), ts);
// //                         str(r, b + 6, s(qi.getRemarks()), ts);
// //                     } else {
// //                         for (int c = 0; c < supCols; c++) { Cell nc = r.createCell(b + c); nc.setCellValue("—"); nc.setCellStyle(valueStyle); }
// //                     }
// //                 }
// //             }

// //             Row totRow = sheet.createRow(row++);
// //             Cell totLbl = totRow.createCell(0); totLbl.setCellValue("GRAND TOTAL"); totLbl.setCellStyle(totalStyle);
// //             sheet.addMergedRegion(new CellRangeAddress(row - 1, row - 1, 0, fixedCols - 1));
// //             for (int c = 1; c < fixedCols; c++) totRow.createCell(c).setCellStyle(totalStyle);

// //             Long lowestTotalSid = null; double lowestTotalAmt = Double.MAX_VALUE;
// //             for (RFQSupplier rs : suppliers) {
// //                 if (rs.getSupplier() == null) continue;
// //                 double tot = sumGrandTotal(quotes, rs.getSupplier().getId());
// //                 if (tot > 0 && tot < lowestTotalAmt) { lowestTotalAmt = tot; lowestTotalSid = rs.getSupplier().getId(); }
// //             }
// //             final Long lowestTotId = lowestTotalSid;

// //             for (int idx = 0; idx < numSup; idx++) {
// //                 RFQSupplier rs = suppliers.get(idx);
// //                 if (rs.getSupplier() == null) continue;
// //                 Long sid = rs.getSupplier().getId();
// //                 int b = fixedCols + idx * supCols;
// //                 boolean isLowest = sid.equals(lowestTotId);
// //                 CellStyle fs = isLowest ? lowestStyle : totalStyle;
// //                 double total = sumGrandTotal(quotes, sid);
// //                 for (int c = 0; c < supCols; c++) {
// //                     Cell nc = totRow.createCell(b + c);
// //                     if (c == 1) nc.setCellValue(total);
// //                     else if (c == 0) nc.setCellValue(isLowest ? "★ LOWEST" : "");
// //                     nc.setCellStyle(fs);
// //                 }
// //             }

// //             sheet.setAutoFilter(new CellRangeAddress(5, row - 2, 0, totalCols - 1));
// //             wb.write(out);
// //             return out.toByteArray();
// //         }
// //     }

// //     // =========================================================================
// //     //  4.  INVOICE EXCEL  — standalone single-invoice report
// //     // =========================================================================
// //     @Transactional(readOnly = true)
// //     public byte[] generateInvoiceExcel(Long invoiceId) throws Exception {

// //         Invoice invoice = invoiceRepository.findById(invoiceId)
// //                 .orElseThrow(() -> new RuntimeException("Invoice not found: " + invoiceId));

// //         List<InvoiceLineItem> lineItems = invoice.getLineItems();

// //         try (XSSFWorkbook wb = new XSSFWorkbook();
// //              ByteArrayOutputStream out = new ByteArrayOutputStream()) {

// //             CellStyle titleStyle = makeTitleStyle(wb);
// //             CellStyle hdrNavy   = makeHeaderStyle(wb, rgb(0x1e, 0x40, 0x88));
// //             CellStyle hdrGreen  = makeHeaderStyle(wb, rgb(0x1b, 0x5e, 0x20));
// //             CellStyle labelStyle = makeLabelStyle(wb);
// //             CellStyle valueStyle = makeValueStyle(wb);
// //             CellStyle amtStyle  = makeAmountStyle(wb);
// //             CellStyle altStyle  = makeAltRowStyle(wb);
// //             CellStyle totalStyle = makeTotalStyle(wb);

// //             XSSFSheet sheet = wb.createSheet("Invoice");
// //             setWidths(sheet, new int[]{6000, 9000, 6000, 9000});

// //             int row = 0;
// //             row = addBanner(sheet, row, "INVOICE", titleStyle,
// //                     s(invoice.getInvoiceNumber()) + "  |  Generated: " + LocalDateTime.now().format(DTTM_FMT), 4);

// //             row = addSectionHeader(sheet, row, "INVOICE DETAILS", hdrNavy, 4);
// //             row = addLV(sheet, row, labelStyle, valueStyle,
// //                     "Invoice Number", invoice.getInvoiceNumber(),
// //                     "Status",         invoice.getStatus() != null ? invoice.getStatus().name() : "—");
// //             row = addLV(sheet, row, labelStyle, valueStyle,
// //                     "Invoice Date",   fmt(invoice.getInvoiceDate()),
// //                     "Due Date",       fmt(invoice.getDueDate()));
// //             row = addLV(sheet, row, labelStyle, valueStyle,
// //                     "PO Number",      s(invoice.getPoNumber()),
// //                     "RFQ Number",     s(invoice.getRfqNumber()));
// //             row = addLV(sheet, row, labelStyle, valueStyle,
// //                     "Currency",       s(invoice.getCurrency()),
// //                     "Payment Terms",  s(invoice.getPaymentTerms()));
// //             row++;

// //             row = addSectionHeader(sheet, row, "SUPPLIER (FROM)", hdrNavy, 4);
// //             row = addLV(sheet, row, labelStyle, valueStyle,
// //                     "Supplier Name",  s(invoice.getSupplierName()),
// //                     "Email",          s(invoice.getSupplierEmail()));
// //             row++;

// //             row = addSectionHeader(sheet, row, "BUYER (BILL TO)", hdrNavy, 4);
// //             row = addLV(sheet, row, labelStyle, valueStyle,
// //                     "Buyer Company",  s(invoice.getBuyerCompanyName()),
// //                     "RFQ Creator",    s(invoice.getRfqCreatorName()));
// //             row++;

// //             setWidths(sheet, new int[]{1200, 3000, 7000, 2500, 2000, 2500, 4000, 2000, 2000, 4500});
// //             row = addSectionHeader(sheet, row, "LINE ITEMS (" + lineItems.size() + ")", hdrGreen, 4);
// //             String[] cols = {"#", "Item Code", "Description", "HSN/SAC", "UOM",
// //                              "Quantity", "Unit Price", "Disc %", "Tax %", "Line Total"};
// //             addHeaderRow(sheet, row++, hdrNavy, cols);

// //             int sl = 1;
// //             for (InvoiceLineItem li : lineItems) {
// //                 Row r = sheet.createRow(row++);
// //                 CellStyle cs = alt(sl, valueStyle, altStyle);
// //                 str(r, 0, String.valueOf(sl++), cs);
// //                 str(r, 1, s(li.getItemCode()), cs);
// //                 str(r, 2, s(li.getItemDescription()), cs);
// //                 str(r, 3, s(li.getHsnSacCode()), cs);
// //                 str(r, 4, s(li.getUom()), cs);
// //                 num(r, 5, li.getQuantity(), amtStyle);
// //                 num(r, 6, li.getUnitPrice(), amtStyle);
// //                 num(r, 7, li.getDiscountPercentage(), amtStyle);
// //                 num(r, 8, li.getTaxPercentage(), amtStyle);
// //                 num(r, 9, li.getLineTotal(), amtStyle);
// //             }

// //             row++;
// //             Object[][] totals = {
// //                 {"Subtotal (Pre-tax)", invoice.getSubtotal()},
// //                 {"Total Tax",          invoice.getTaxAmount()},
// //                 {"Grand Total",        invoice.getTotalAmount()}
// //             };
// //             for (int t = 0; t < totals.length; t++) {
// //                 Row tr  = sheet.createRow(row++);
// //                 boolean isGrand = (t == totals.length - 1);
// //                 Cell lc = tr.createCell(8);
// //                 lc.setCellValue((String) totals[t][0]);
// //                 lc.setCellStyle(isGrand ? totalStyle : labelStyle);
// //                 Cell vc = tr.createCell(9);
// //                 if (totals[t][1] instanceof BigDecimal)
// //                     vc.setCellValue(((BigDecimal) totals[t][1]).doubleValue());
// //                 else
// //                     vc.setCellValue(0);
// //                 vc.setCellStyle(isGrand ? totalStyle : amtStyle);
// //             }

// //             if (invoice.getBankName() != null) {
// //                 row++;
// //                 row = addSectionHeader(sheet, row, "BANK DETAILS", hdrNavy, 4);
// //                 row = addLV(sheet, row, labelStyle, valueStyle,
// //                         "Bank Name",       s(invoice.getBankName()),
// //                         "Account Name",    s(invoice.getBankAccountName()));
// //                 row = addLV(sheet, row, labelStyle, valueStyle,
// //                         "Account Number",  s(invoice.getBankAccountNumber()),
// //                         "IFSC Code",       s(invoice.getBankIfscCode()));
// //                 addLV(sheet, row, labelStyle, valueStyle,
// //                         "SWIFT Code",      s(invoice.getBankSwiftCode()), "", "");
// //             }

// //             if (invoice.getPaymentTerms() != null || invoice.getNotes() != null) {
// //                 row++;
// //                 row = addSectionHeader(sheet, row, "TERMS & NOTES", hdrNavy, 4);
// //                 row = addLV(sheet, row, labelStyle, valueStyle,
// //                         "Payment Terms", s(invoice.getPaymentTerms()),
// //                         "Notes",         s(invoice.getNotes()));
// //                 if (invoice.getTermsAndConditions() != null)
// //                     addLV(sheet, row, labelStyle, valueStyle,
// //                             "Terms & Conditions", s(invoice.getTermsAndConditions()), "", "");
// //             }

// //             if (invoice.getApprovedRejectedBy() != null) {
// //                 row++;
// //                 row = addSectionHeader(sheet, row, "APPROVAL INFORMATION", hdrNavy, 4);
// //                 row = addLV(sheet, row, labelStyle, valueStyle,
// //                         "Action By",   s(invoice.getApprovedRejectedBy()),
// //                         "Action Date", fmt(invoice.getApprovedRejectedAt()));
// //                 if (invoice.getApprovalRemarks() != null)
// //                     addLV(sheet, row, labelStyle, valueStyle,
// //                             "Remarks", s(invoice.getApprovalRemarks()), "", "");
// //             }

// //             if (invoice.getStatus() == Invoice.InvoiceStatus.PAID) {
// //                 row++;
// //                 row = addSectionHeader(sheet, row, "PAYMENT INFORMATION", hdrNavy, 4);
// //                 addLV(sheet, row, labelStyle, valueStyle,
// //                         "Paid By",   s(invoice.getPaidBy()),
// //                         "Paid At",   fmt(invoice.getPaidAt()));
// //                 if (invoice.getPaymentReference() != null)
// //                     addLV(sheet, row + 1, labelStyle, valueStyle,
// //                             "Payment Reference", s(invoice.getPaymentReference()), "", "");
// //             }

// //             wb.write(out);
// //             return out.toByteArray();
// //         }
// //     }

// //     // =========================================================================
// //     //  5.  HTML generators (PDF) — keep existing implementations
// //     // =========================================================================
// //     @Transactional(readOnly = true)
// //     public String generateRFQSummaryHTML(Long rfqId) {
// //         throw new UnsupportedOperationException("Keep existing generateRFQSummaryHTML implementation");
// //     }

// //     @Transactional(readOnly = true)
// //     public String generateQuoteComparisonHTML(Long rfqId) {
// //         throw new UnsupportedOperationException("Keep existing generateQuoteComparisonHTML implementation");
// //     }

// //     // =========================================================================
// //     //  PRIVATE: NEW — batch-specific cell styles for multi-GRN sheets
// //     // =========================================================================

// //     /**
// //      * Alternating colour for batch separator rows:
// //      * odd batches = orange, even batches = teal.
// //      */
// //     private XSSFCellStyle makeBatchSeparatorStyle(XSSFWorkbook wb, int batchNum) {
// //         XSSFCellStyle s = wb.createCellStyle();
// //         XSSFFont f = wb.createFont();
// //         f.setBold(true);
// //         f.setFontHeightInPoints((short) 10);
// //         f.setColor(new XSSFColor(new byte[]{(byte)0xff,(byte)0xff,(byte)0xff}, null));
// //         s.setFont(f);
// //         byte[] bg = (batchNum % 2 == 1) ? rgb(0xe6, 0x55, 0x00) : rgb(0x00, 0x6d, 0x77);
// //         s.setFillForegroundColor(new XSSFColor(bg, null));
// //         s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
// //         s.setVerticalAlignment(VerticalAlignment.CENTER);
// //         return s;
// //     }

// //     private XSSFCellStyle makeBatchSubtotalLabelStyle(XSSFWorkbook wb) {
// //         XSSFCellStyle s = wb.createCellStyle();
// //         XSSFFont f = wb.createFont();
// //         f.setBold(true);
// //         f.setFontHeightInPoints((short) 9);
// //         f.setColor(new XSSFColor(rgb(0x1e, 0x40, 0x88), null));
// //         s.setFont(f);
// //         s.setFillForegroundColor(new XSSFColor(rgb(0xe8, 0xf0, 0xfe), null));
// //         s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
// //         s.setBorderTop(BorderStyle.MEDIUM);
// //         return s;
// //     }

// //     private XSSFCellStyle makeBatchSubtotalAmtStyle(XSSFWorkbook wb) {
// //         XSSFCellStyle s = wb.createCellStyle();
// //         XSSFFont f = wb.createFont();
// //         f.setBold(true);
// //         f.setFontHeightInPoints((short) 9);
// //         f.setColor(new XSSFColor(rgb(0x1e, 0x40, 0x88), null));
// //         s.setFont(f);
// //         s.setFillForegroundColor(new XSSFColor(rgb(0xe8, 0xf0, 0xfe), null));
// //         s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
// //         s.setAlignment(HorizontalAlignment.RIGHT);
// //         s.setDataFormat(wb.createDataFormat().getFormat("#,##0.00"));
// //         s.setBorderTop(BorderStyle.MEDIUM);
// //         return s;
// //     }

// //     // =========================================================================
// //     //  PRIVATE: cell style factory methods
// //     // =========================================================================

// //     private XSSFCellStyle makeTitleStyle(XSSFWorkbook wb) {
// //         XSSFCellStyle s = wb.createCellStyle();
// //         XSSFFont f = wb.createFont(); f.setBold(true); f.setFontHeightInPoints((short) 14);
// //         f.setColor(new XSSFColor(new byte[]{(byte)0xff,(byte)0xff,(byte)0xff}, null));
// //         s.setFont(f);
// //         s.setFillForegroundColor(new XSSFColor(rgb(0x1e, 0x40, 0x88), null));
// //         s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
// //         s.setAlignment(HorizontalAlignment.CENTER); s.setVerticalAlignment(VerticalAlignment.CENTER);
// //         s.setWrapText(true);
// //         return s;
// //     }

// //     private XSSFCellStyle makeHeaderStyle(XSSFWorkbook wb, byte[] rgb) {
// //         XSSFCellStyle s = wb.createCellStyle();
// //         XSSFFont f = wb.createFont(); f.setBold(true); f.setFontHeightInPoints((short) 10);
// //         f.setColor(new XSSFColor(new byte[]{(byte)0xff,(byte)0xff,(byte)0xff}, null));
// //         s.setFont(f);
// //         s.setFillForegroundColor(new XSSFColor(rgb, null)); s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
// //         s.setBorderBottom(BorderStyle.THIN); s.setBorderTop(BorderStyle.THIN);
// //         s.setBorderLeft(BorderStyle.THIN);   s.setBorderRight(BorderStyle.THIN);
// //         s.setWrapText(true);
// //         return s;
// //     }

// //     private XSSFCellStyle makeLabelStyle(XSSFWorkbook wb) {
// //         XSSFCellStyle s = wb.createCellStyle();
// //         XSSFFont f = wb.createFont(); f.setBold(true); f.setFontHeightInPoints((short) 9); s.setFont(f);
// //         s.setFillForegroundColor(new XSSFColor(rgb(0xf0, 0xf4, 0xff), null));
// //         s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
// //         s.setBorderBottom(BorderStyle.THIN); s.setBorderRight(BorderStyle.THIN);
// //         return s;
// //     }

// //     private XSSFCellStyle makeValueStyle(XSSFWorkbook wb) {
// //         XSSFCellStyle s = wb.createCellStyle(); s.setBorderBottom(BorderStyle.THIN); s.setWrapText(true); return s;
// //     }

// //     private XSSFCellStyle makeAltRowStyle(XSSFWorkbook wb) {
// //         XSSFCellStyle s = wb.createCellStyle();
// //         s.setFillForegroundColor(new XSSFColor(rgb(0xf5, 0xf7, 0xfb), null));
// //         s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
// //         s.setBorderBottom(BorderStyle.THIN); s.setWrapText(true); return s;
// //     }

// //     private XSSFCellStyle makeAmountStyle(XSSFWorkbook wb) {
// //         XSSFCellStyle s = wb.createCellStyle(); s.setAlignment(HorizontalAlignment.RIGHT);
// //         s.setDataFormat(wb.createDataFormat().getFormat("#,##0.00")); s.setBorderBottom(BorderStyle.THIN); return s;
// //     }

// //     private XSSFCellStyle makeLowestStyle(XSSFWorkbook wb) {
// //         XSSFCellStyle s = wb.createCellStyle();
// //         XSSFFont f = wb.createFont(); f.setBold(true); f.setFontHeightInPoints((short) 9);
// //         f.setColor(new XSSFColor(rgb(0x1a, 0x5c, 0x1a), null)); s.setFont(f);
// //         s.setFillForegroundColor(new XSSFColor(rgb(0xd4, 0xed, 0xda), null));
// //         s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
// //         s.setAlignment(HorizontalAlignment.RIGHT);
// //         s.setDataFormat(wb.createDataFormat().getFormat("#,##0.00")); s.setBorderBottom(BorderStyle.THIN); return s;
// //     }

// //     private XSSFCellStyle makeTotalStyle(XSSFWorkbook wb) {
// //         XSSFCellStyle s = wb.createCellStyle();
// //         XSSFFont f = wb.createFont(); f.setBold(true); f.setFontHeightInPoints((short) 11);
// //         f.setColor(new XSSFColor(new byte[]{(byte)0xff,(byte)0xff,(byte)0xff}, null)); s.setFont(f);
// //         s.setFillForegroundColor(new XSSFColor(rgb(0x1e, 0x40, 0x88), null));
// //         s.setFillPattern(FillPatternType.SOLID_FOREGROUND); s.setAlignment(HorizontalAlignment.RIGHT);
// //         s.setDataFormat(wb.createDataFormat().getFormat("#,##0.00")); return s;
// //     }

// //     private XSSFCellStyle makeStatusStyle(XSSFWorkbook wb) {
// //         XSSFCellStyle s = wb.createCellStyle();
// //         XSSFFont f = wb.createFont(); f.setBold(true); f.setFontHeightInPoints((short) 9); s.setFont(f);
// //         s.setAlignment(HorizontalAlignment.CENTER); s.setBorderBottom(BorderStyle.THIN); return s;
// //     }

// //     private XSSFCellStyle makeStatLabelStyle(XSSFWorkbook wb) {
// //         XSSFCellStyle s = wb.createCellStyle();
// //         XSSFFont f = wb.createFont(); f.setBold(true); f.setFontHeightInPoints((short) 10); s.setFont(f);
// //         s.setFillForegroundColor(new XSSFColor(rgb(0xe8, 0xf0, 0xfe), null));
// //         s.setFillPattern(FillPatternType.SOLID_FOREGROUND); return s;
// //     }

// //     private XSSFCellStyle makeStatValueStyle(XSSFWorkbook wb) {
// //         XSSFCellStyle s = wb.createCellStyle();
// //         XSSFFont f = wb.createFont(); f.setBold(true); f.setFontHeightInPoints((short) 10);
// //         f.setColor(new XSSFColor(rgb(0x1e, 0x40, 0x88), null)); s.setFont(f); return s;
// //     }

// //     private XSSFCellStyle makeMetaStyle(XSSFWorkbook wb) {
// //         XSSFCellStyle s = wb.createCellStyle();
// //         XSSFFont f = wb.createFont(); f.setItalic(true); f.setFontHeightInPoints((short) 9);
// //         f.setColor(new XSSFColor(rgb(0x44, 0x44, 0x44), null)); s.setFont(f); return s;
// //     }

// //     private XSSFCellStyle makeWarnStyle(XSSFWorkbook wb) {
// //         XSSFCellStyle s = wb.createCellStyle();
// //         s.setFillForegroundColor(new XSSFColor(rgb(0xff, 0xf9, 0xc4), null));
// //         s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
// //         s.setBorderBottom(BorderStyle.THIN); s.setWrapText(true);
// //         return s;
// //     }

// //     private XSSFCellStyle makeBannerMatchStyle(XSSFWorkbook wb) {
// //         XSSFCellStyle s = wb.createCellStyle();
// //         XSSFFont f = wb.createFont(); f.setBold(true); f.setFontHeightInPoints((short) 14);
// //         f.setColor(new XSSFColor(new byte[]{(byte)0xff,(byte)0xff,(byte)0xff}, null)); s.setFont(f);
// //         s.setFillForegroundColor(new XSSFColor(rgb(0x1b, 0x5e, 0x20), null));
// //         s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
// //         s.setAlignment(HorizontalAlignment.CENTER); s.setVerticalAlignment(VerticalAlignment.CENTER);
// //         s.setWrapText(true); return s;
// //     }

// //     private XSSFCellStyle makeBannerWarnStyle(XSSFWorkbook wb) {
// //         XSSFCellStyle s = wb.createCellStyle();
// //         XSSFFont f = wb.createFont(); f.setBold(true); f.setFontHeightInPoints((short) 14);
// //         f.setColor(new XSSFColor(new byte[]{(byte)0xff,(byte)0xff,(byte)0xff}, null)); s.setFont(f);
// //         s.setFillForegroundColor(new XSSFColor(rgb(0xe6, 0x55, 0x00), null));
// //         s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
// //         s.setAlignment(HorizontalAlignment.CENTER); s.setVerticalAlignment(VerticalAlignment.CENTER);
// //         s.setWrapText(true); return s;
// //     }

// //     private XSSFCellStyle makeBannerFailStyle(XSSFWorkbook wb) {
// //         XSSFCellStyle s = wb.createCellStyle();
// //         XSSFFont f = wb.createFont(); f.setBold(true); f.setFontHeightInPoints((short) 14);
// //         f.setColor(new XSSFColor(new byte[]{(byte)0xff,(byte)0xff,(byte)0xff}, null)); s.setFont(f);
// //         s.setFillForegroundColor(new XSSFColor(rgb(0xb7, 0x1c, 0x1c), null));
// //         s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
// //         s.setAlignment(HorizontalAlignment.CENTER); s.setVerticalAlignment(VerticalAlignment.CENTER);
// //         s.setWrapText(true); return s;
// //     }

// //     private XSSFCellStyle makeAwardedRowStyle(XSSFWorkbook wb) {
// //         XSSFCellStyle s = wb.createCellStyle();
// //         XSSFFont f = wb.createFont();
// //         f.setBold(true);
// //         f.setFontHeightInPoints((short) 9);
// //         f.setColor(new XSSFColor(rgb(0x7d, 0x4a, 0x00), null));
// //         s.setFont(f);
// //         s.setFillForegroundColor(new XSSFColor(rgb(0xff, 0xf3, 0xcd), null));
// //         s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
// //         s.setBorderBottom(BorderStyle.THIN);
// //         s.setBorderTop(BorderStyle.THIN);
// //         s.setBorderLeft(BorderStyle.THIN);
// //         s.setBorderRight(BorderStyle.THIN);
// //         s.setWrapText(true);
// //         return s;
// //     }

// //     private XSSFCellStyle makeAwardedAmtStyle(XSSFWorkbook wb) {
// //         XSSFCellStyle s = wb.createCellStyle();
// //         XSSFFont f = wb.createFont();
// //         f.setBold(true);
// //         f.setFontHeightInPoints((short) 9);
// //         f.setColor(new XSSFColor(rgb(0x7d, 0x4a, 0x00), null));
// //         s.setFont(f);
// //         s.setFillForegroundColor(new XSSFColor(rgb(0xff, 0xf3, 0xcd), null));
// //         s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
// //         s.setAlignment(HorizontalAlignment.RIGHT);
// //         s.setDataFormat(wb.createDataFormat().getFormat("#,##0.00"));
// //         s.setBorderBottom(BorderStyle.THIN);
// //         s.setBorderTop(BorderStyle.THIN);
// //         s.setBorderLeft(BorderStyle.THIN);
// //         s.setBorderRight(BorderStyle.THIN);
// //         return s;
// //     }

// //     // =========================================================================
// //     //  PRIVATE: layout helpers
// //     // =========================================================================

// //     private int addBanner(XSSFSheet sheet, int row, String title, CellStyle ts, String sub, int cols) {
// //         sheet.addMergedRegion(new CellRangeAddress(row, row, 0, cols - 1));
// //         Row r1 = sheet.createRow(row++); r1.setHeightInPoints(30);
// //         Cell c1 = r1.createCell(0); c1.setCellValue(title); c1.setCellStyle(ts);
// //         sheet.addMergedRegion(new CellRangeAddress(row, row, 0, cols - 1));
// //         Row r2 = sheet.createRow(row++); r2.setHeightInPoints(18);
// //         Cell c2 = r2.createCell(0); c2.setCellValue(sub); c2.setCellStyle(ts);
// //         return row;
// //     }

// //     private int addBannerCustomStyle(XSSFSheet sheet, int row, String title, CellStyle ts, String sub, int cols) {
// //         sheet.addMergedRegion(new CellRangeAddress(row, row, 0, cols - 1));
// //         Row r1 = sheet.createRow(row++); r1.setHeightInPoints(30);
// //         Cell c1 = r1.createCell(0); c1.setCellValue(title); c1.setCellStyle(ts);
// //         sheet.addMergedRegion(new CellRangeAddress(row, row, 0, cols - 1));
// //         Row r2 = sheet.createRow(row++); r2.setHeightInPoints(18);
// //         Cell c2 = r2.createCell(0); c2.setCellValue(sub); c2.setCellStyle(ts);
// //         return row;
// //     }

// //     private int addSectionHeader(XSSFSheet sheet, int row, String text, CellStyle cs, int cols) {
// //         sheet.addMergedRegion(new CellRangeAddress(row, row, 0, cols - 1));
// //         Row r = sheet.createRow(row++); r.setHeightInPoints(20);
// //         Cell c = r.createCell(0); c.setCellValue(text); c.setCellStyle(cs);
// //         return row;
// //     }

// //     private int addLV(XSSFSheet sheet, int row, CellStyle lc, CellStyle vc,
// //                       String l1, Object v1, String l2, Object v2) {
// //         Row r = sheet.createRow(row++); r.setHeightInPoints(16);
// //         str(r, 0, l1, lc); str(r, 1, s(v1), vc);
// //         str(r, 2, l2, lc); str(r, 3, s(v2), vc);
// //         return row;
// //     }

// //     private void addHeaderRow(XSSFSheet sheet, int row, CellStyle cs, String[] cols) {
// //         Row r = sheet.createRow(row); r.setHeightInPoints(18);
// //         for (int c = 0; c < cols.length; c++) {
// //             Cell cell = r.createCell(c); cell.setCellValue(cols[c]); cell.setCellStyle(cs);
// //         }
// //     }

// //     private void setWidths(XSSFSheet sheet, int[] widths) {
// //         for (int c = 0; c < widths.length; c++) sheet.setColumnWidth(c, widths[c]);
// //     }

// //     private void str(Row row, int col, String value, CellStyle cs) {
// //         Cell c = row.createCell(col); c.setCellValue(value != null ? value : ""); c.setCellStyle(cs);
// //     }

// //     private void num(Row row, int col, Object value, CellStyle cs) {
// //         Cell c = row.createCell(col);
// //         if (value instanceof BigDecimal) c.setCellValue(((BigDecimal) value).doubleValue());
// //         else if (value instanceof Number) c.setCellValue(((Number) value).doubleValue());
// //         else c.setCellValue(0);
// //         c.setCellStyle(cs);
// //     }

// //     private void num(Row row, int col, BigDecimal value, CellStyle cs) {
// //         Cell c = row.createCell(col);
// //         c.setCellValue(value != null ? value.doubleValue() : 0);
// //         c.setCellStyle(cs);
// //     }

// //     private void info(StringBuilder h, String label, String value) {
// //         h.append("<div class='info-item'>")
// //          .append("<div class='info-label'>").append(label).append("</div>")
// //          .append("<div class='info-value'>").append(value != null && !value.equals("—") ? value : "—").append("</div>")
// //          .append("</div>");
// //     }

// //     // =========================================================================
// //     //  PRIVATE: small utilities
// //     // =========================================================================

// //     private byte[] rgb(int r, int g, int b)          { return new byte[]{(byte) r, (byte) g, (byte) b}; }
// //     private CellStyle alt(int sl, CellStyle n, CellStyle a) { return (sl % 2 == 0) ? a : n; }
// //     private String s(Object o)                        { return o != null ? o.toString() : "—"; }
// //     private String en(Object o)                       { return o != null ? o.toString() : "N/A"; }
// //     private String bool(Boolean b)                    { return Boolean.TRUE.equals(b) ? "Yes" : "No"; }
// //     private String fmt(LocalDateTime dt)              { return dt != null ? dt.format(DATE_FMT) : "—"; }
// //     private String fmtAmt(BigDecimal v)               { return v != null ? String.format("%,.2f", v.doubleValue()) : "—"; }
// //     private String joinNonNull(String sep, String... parts) {
// //         return Arrays.stream(parts).filter(p -> p != null && !p.isBlank()).collect(Collectors.joining(" " + sep + " "));
// //     }

// //     private Optional<SupplierQuoteItem> findQuote(List<SupplierQuoteItem> quotes, Long itemId, Long supplierId) {
// //         return quotes.stream()
// //                 .filter(q -> q.getRfqItem() != null && q.getRfqItem().getId().equals(itemId)
// //                           && q.getRfqSupplier() != null && q.getRfqSupplier().getSupplier() != null
// //                           && q.getRfqSupplier().getSupplier().getId().equals(supplierId))
// //                 .findFirst();
// //     }

// //     private double sumGrandTotal(List<SupplierQuoteItem> quotes, Long supplierId) {
// //         return quotes.stream()
// //                 .filter(q -> q.getRfqSupplier() != null && q.getRfqSupplier().getSupplier() != null
// //                           && q.getRfqSupplier().getSupplier().getId().equals(supplierId)
// //                           && q.getGrandTotal() != null)
// //                 .mapToDouble(q -> q.getGrandTotal().doubleValue()).sum();
// //     }

// //     private String determineSupplierCurrency(RFQ rfq, Supplier supplier) {
// //         if (rfq.getLocation() == null || supplier == null) return "INR";
// //         String buyerCountry = rfq.getLocation().getCountry();
// //         String supCountry   = supplier.getCountry();
// //         if (buyerCountry != null && supCountry != null
// //                 && buyerCountry.trim().equalsIgnoreCase(supCountry.trim())) {
// //             return rfq.getLocation().getCurrencyCode() != null ? rfq.getLocation().getCurrencyCode() : "INR";
// //         }
// //         return "USD";
// //     }
// // }


// package com.itti.leadcapturing.service;

// import com.itti.leadcapturing.model.*;
// import com.itti.leadcapturing.repo.*;
// import org.apache.poi.ss.usermodel.*;
// import org.apache.poi.ss.util.CellRangeAddress;
// import org.apache.poi.xssf.usermodel.*;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// import java.io.ByteArrayOutputStream;
// import java.math.BigDecimal;
// import java.time.LocalDateTime;
// import java.time.format.DateTimeFormatter;
// import java.util.*;
// import java.util.stream.Collectors;


// @Service
// public class RFQReportService {

//     @Autowired private RFQRepository               rfqRepository;
//     @Autowired private RFQItemRepository           rfqItemRepository;
//     @Autowired private RFQSupplierRepository       rfqSupplierRepository;
//     @Autowired private SupplierQuoteItemRepository quoteItemRepository;
//     @Autowired private DynamicRFQApprovalRepository approvalRepository;
//     @Autowired private InvoiceRepository           invoiceRepository;
//     @Autowired private PurchaseOrderRepository     poRepository;
//     @Autowired private GRNRepository               grnRepository;
//     @Autowired private ThreeWayMatchRepository     matchRepository;
//     // NOTE: UserRepository removed — we no longer query User for hierarchy info.
//     //       All viewer info comes from frontend as URL params.

//     private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
//     private static final DateTimeFormatter DTTM_FMT = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm");

//     // =========================================================================
//     //  PO SUMMARY EXCEL
//     // =========================================================================
//     @Transactional(readOnly = true)
//     public byte[] generatePOSummaryExcel(Long poId) throws Exception {

//         PurchaseOrder po = poRepository.findById(poId)
//                 .orElseThrow(() -> new RuntimeException("Purchase Order not found: " + poId));
//         List<POLineItem> lineItems = po.getLineItems();

//         try (XSSFWorkbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

//             CellStyle titleStyle = makeTitleStyle(wb);
//             CellStyle hdrNavy   = makeHeaderStyle(wb, rgb(0x1e, 0x40, 0x88));
//             CellStyle hdrGreen  = makeHeaderStyle(wb, rgb(0x1b, 0x5e, 0x20));
//             CellStyle labelStyle = makeLabelStyle(wb);
//             CellStyle valueStyle = makeValueStyle(wb);
//             CellStyle amtStyle  = makeAmountStyle(wb);
//             CellStyle altStyle  = makeAltRowStyle(wb);
//             CellStyle totalStyle = makeTotalStyle(wb);
//             CellStyle statusStyle = makeStatusStyle(wb);

//             XSSFSheet s1 = wb.createSheet("PO Overview");
//             s1.setColumnWidth(0, 6000); s1.setColumnWidth(1, 9000);
//             s1.setColumnWidth(2, 6000); s1.setColumnWidth(3, 9000);

//             int row = 0;
//             row = addBanner(s1, row, "PURCHASE ORDER", titleStyle,
//                     "PO: " + s(po.getPoNumber()) + "  |  Status: " + (po.getStatus() != null ? po.getStatus().name() : "—")
//                     + "  |  Generated: " + LocalDateTime.now().format(DTTM_FMT), 4);

//             row = addSectionHeader(s1, row, "PO INFORMATION", hdrNavy, 4);
//             row = addLV(s1, row, labelStyle, valueStyle, "PO Number", s(po.getPoNumber()), "PO Date", fmt(po.getPoDate()));
//             row = addLV(s1, row, labelStyle, valueStyle, "Status", po.getStatus() != null ? po.getStatus().name() : "—", "Approval Status", po.getApprovalStatus() != null ? po.getApprovalStatus().name() : "—");
//             row = addLV(s1, row, labelStyle, valueStyle, "Reference Quote", s(po.getReferenceQuoteNo()), "Currency", s(po.getCurrencyCode()) + " " + s(po.getCurrencySymbol()));
//             row = addLV(s1, row, labelStyle, valueStyle, "Payment Terms", s(po.getPaymentTerms()), "Delivery Terms", s(po.getDeliveryTerms()));
//             row = addLV(s1, row, labelStyle, valueStyle, "Mode of Payment", s(po.getModeOfPayment()), "Dispatched Through", s(po.getDispatchedThrough()));
//             row = addLV(s1, row, labelStyle, valueStyle, "Destination", s(po.getDestination()), "Amount in Words", s(po.getAmountInWords()));
//             if (po.getBuyerRemarks() != null) row = addLV(s1, row, labelStyle, valueStyle, "Buyer Remarks", s(po.getBuyerRemarks()), "", "");
//             row++;

//             if (po.getBuyer() != null) {
//                 Buyer b = po.getBuyer();
//                 row = addSectionHeader(s1, row, "BUYER INFORMATION", hdrNavy, 4);
//                 row = addLV(s1, row, labelStyle, valueStyle, "Company", b.getCompanyName(), "Type", s(b.getCompanyType()));
//                 row = addLV(s1, row, labelStyle, valueStyle, "Contact", b.getContactPersonName(), "Email", s(b.getContactPersonEmail()));
//                 row = addLV(s1, row, labelStyle, valueStyle, "Phone", s(b.getContactPersonPhone()), "GST", s(b.getGstNumber()));
//                 row++;
//             }
//             if (po.getSupplier() != null) {
//                 Supplier sup = po.getSupplier();
//                 row = addSectionHeader(s1, row, "SUPPLIER INFORMATION", hdrNavy, 4);
//                 row = addLV(s1, row, labelStyle, valueStyle, "Company", s(sup.getCompanyName()), "Type", s(sup.getCompanyType()));
//                 row = addLV(s1, row, labelStyle, valueStyle, "Contact", s(sup.getContactPersonName()), "Email", s(sup.getContactPersonEmail()));
//                 row = addLV(s1, row, labelStyle, valueStyle, "Phone", s(sup.getContactPersonPhone()), "Country", s(sup.getCountry()));
//                 row++;
//             }
//             if (po.getDeliveryLocation() != null) {
//                 Location dl = po.getDeliveryLocation();
//                 row = addSectionHeader(s1, row, "DELIVERY LOCATION", hdrNavy, 4);
//                 row = addLV(s1, row, labelStyle, valueStyle, "Location Name", dl.getLocationName(), "City", s(dl.getCity()));
//                 row = addLV(s1, row, labelStyle, valueStyle, "State", s(dl.getState()), "Country", s(dl.getCountry()));
//                 row = addLV(s1, row, labelStyle, valueStyle, "Address", s(dl.getAddressLine1()), "Contact", s(dl.getLocationContactName()));
//                 row++;
//             }
//             if (po.getRfq() != null) {
//                 row = addSectionHeader(s1, row, "RFQ REFERENCE", hdrNavy, 4);
//                 row = addLV(s1, row, labelStyle, valueStyle, "RFQ Number", s(po.getRfq().getRfqNumber()), "RFQ Title", s(po.getRfq().getRfqTitle()));
//                 row++;
//             }
//             if (po.getApprovedByName() != null) {
//                 row = addSectionHeader(s1, row, "APPROVAL INFORMATION", hdrNavy, 4);
//                 row = addLV(s1, row, labelStyle, valueStyle, "Approved By", s(po.getApprovedByName()), "Designation", s(po.getApprovedByDesignation()));
//                 addLV(s1, row, labelStyle, valueStyle, "Approval Date", fmt(po.getApprovalDate()), "", "");
//             }

//             XSSFSheet s2 = wb.createSheet("Line Items");
//             setWidths(s2, new int[]{1200, 3000, 7000, 2500, 2000, 2500, 4000, 2000, 2000, 4500, 4000});
//             row = 0;
//             row = addBanner(s2, row, "LINE ITEMS", titleStyle, s(po.getPoNumber()) + "  [" + lineItems.size() + " items]  |  Currency: " + s(po.getCurrencyCode()), 11);
//             String[] cols = {"#", "Item Code", "Description", "Specifications", "UOM", "PO Qty", "Unit Price", "Disc %", "Tax %", "Line Total", "Remarks"};
//             addHeaderRow(s2, row++, hdrGreen, cols);
//             int sl = 1;
//             for (POLineItem li : lineItems) {
//                 Row r = s2.createRow(row++);
//                 CellStyle cs = alt(sl, valueStyle, altStyle);
//                 str(r, 0, String.valueOf(sl++), cs); str(r, 1, s(li.getItemCode()), cs);
//                 str(r, 2, s(li.getItemDescription()), cs); str(r, 3, s(li.getSpecifications()), cs);
//                 str(r, 4, s(li.getUom()), cs); num(r, 5, li.getQuantity(), amtStyle);
//                 num(r, 6, li.getUnitRate(), amtStyle); num(r, 7, li.getDiscountPercentage(), amtStyle);
//                 num(r, 8, li.getTaxPercentage(), amtStyle); num(r, 9, li.getLineTotal(), amtStyle);
//                 str(r, 10, s(li.getBrandMakeModel()), cs);
//             }
//             row++;
//             Object[][] totals = {{"Subtotal (Pre-tax)", po.getSubtotal()}, {"Tax Amount", po.getTaxAmount()}, {"GRAND TOTAL", po.getGrandTotal()}};
//             for (int t = 0; t < totals.length; t++) {
//                 Row tr = s2.createRow(row++); boolean isGrand = (t == totals.length - 1);
//                 Cell lc = tr.createCell(9); lc.setCellValue((String) totals[t][0]); lc.setCellStyle(isGrand ? totalStyle : labelStyle);
//                 Cell vc = tr.createCell(10); if (totals[t][1] instanceof BigDecimal) vc.setCellValue(((BigDecimal) totals[t][1]).doubleValue()); else vc.setCellValue(0); vc.setCellStyle(isGrand ? totalStyle : amtStyle);
//             }
//             if (po.getAmountInWords() != null) {
//                 row++; Row amtRow = s2.createRow(row++);
//                 Cell lbl = amtRow.createCell(0); lbl.setCellValue("Amount in Words:"); lbl.setCellStyle(labelStyle);
//                 Cell val = amtRow.createCell(1); val.setCellValue(po.getAmountInWords()); val.setCellStyle(valueStyle);
//                 s2.addMergedRegion(new CellRangeAddress(row - 1, row - 1, 1, 10));
//             }
//             wb.write(out);
//             return out.toByteArray();
//         }
//     }

//     // =========================================================================
//     //  GRN EXCEL
//     // =========================================================================
//     @Transactional(readOnly = true)
//     public byte[] generateGRNExcel(Long grnId) throws Exception {
//         GRN grn = grnRepository.findByIdWithLineItems(grnId).orElseThrow(() -> new RuntimeException("GRN not found: " + grnId));
//         List<GRNLineItem> lineItems = grn.getLineItems();

//         try (XSSFWorkbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
//             CellStyle titleStyle = makeTitleStyle(wb);
//             CellStyle hdrNavy   = makeHeaderStyle(wb, rgb(0x1e, 0x40, 0x88));
//             CellStyle hdrGreen  = makeHeaderStyle(wb, rgb(0x1b, 0x5e, 0x20));
//             CellStyle hdrOrange = makeHeaderStyle(wb, rgb(0xe6, 0x55, 0x00));
//             CellStyle labelStyle = makeLabelStyle(wb); CellStyle valueStyle = makeValueStyle(wb);
//             CellStyle amtStyle = makeAmountStyle(wb); CellStyle altStyle = makeAltRowStyle(wb);
//             CellStyle totalStyle = makeTotalStyle(wb); CellStyle statusStyle = makeStatusStyle(wb);

//             XSSFSheet s1 = wb.createSheet("GRN Overview");
//             s1.setColumnWidth(0, 6000); s1.setColumnWidth(1, 9000); s1.setColumnWidth(2, 6000); s1.setColumnWidth(3, 9000);
//             int row = 0;
//             row = addBanner(s1, row, "GOODS RECEIPT NOTE", titleStyle, "GRN: " + s(grn.getGrnNumber()) + "  |  Status: " + (grn.getStatus() != null ? grn.getStatus().name() : "—") + "  |  Generated: " + LocalDateTime.now().format(DTTM_FMT), 4);
//             row = addSectionHeader(s1, row, "GRN INFORMATION", hdrNavy, 4);
//             row = addLV(s1, row, labelStyle, valueStyle, "GRN Number", s(grn.getGrnNumber()), "Status", grn.getStatus() != null ? grn.getStatus().name() : "—");
//             row = addLV(s1, row, labelStyle, valueStyle, "Received Date", fmt(grn.getReceivedDate()), "PO Number", s(grn.getPoNumber()));
//             row = addLV(s1, row, labelStyle, valueStyle, "Invoice Number", s(grn.getInvoiceNumber()), "Supplier", s(grn.getSupplierName()));
//             row++;
//             row = addSectionHeader(s1, row, "DELIVERY DETAILS", hdrNavy, 4);
//             row = addLV(s1, row, labelStyle, valueStyle, "Delivery Challan", s(grn.getDeliveryChallanNumber()), "LR Number", s(grn.getLrNumber()));
//             row = addLV(s1, row, labelStyle, valueStyle, "Transporter", s(grn.getTransporterName()), "Vehicle No.", s(grn.getVehicleNumber()));
//             row = addLV(s1, row, labelStyle, valueStyle, "Delivery Location", s(grn.getDeliveryLocation()), "Received By", s(grn.getReceivedByName()));
//             row++;
//             row = addSectionHeader(s1, row, "QA INFORMATION", hdrOrange, 4);
//             row = addLV(s1, row, labelStyle, valueStyle, "QA Inspector", s(grn.getInspectedByName()), "Approved By", s(grn.getApprovedByName()));
//             row = addLV(s1, row, labelStyle, valueStyle, "Approved At", fmt(grn.getApprovedAt()), "Remarks", s(grn.getRemarks()));
//             row++;
//             row = addSectionHeader(s1, row, "VALUE SUMMARY", hdrNavy, 4);
//             row = addLV(s1, row, labelStyle, valueStyle, "Total Ordered Value", s(grn.getTotalOrderedValue()), "Total Received Value", s(grn.getTotalReceivedValue()));
//             addLV(s1, row, labelStyle, valueStyle, "Total Rejected Value", s(grn.getTotalRejectedValue()), "", "");

//             XSSFSheet s2 = wb.createSheet("Line Items");
//             setWidths(s2, new int[]{1200, 3000, 7000, 2500, 2000, 3000, 3000, 3000, 3000, 4000, 4000, 4000, 2500, 5000});
//             row = 0;
//             row = addBanner(s2, row, "LINE ITEMS", titleStyle, s(grn.getGrnNumber()) + "  [" + lineItems.size() + " items]", 14);
//             Row ph1 = s2.createRow(row++); ph1.setHeightInPoints(16);
//             str(ph1, 0, "#", hdrNavy); str(ph1, 1, "Item Code", hdrNavy); str(ph1, 2, "Description", hdrNavy);
//             str(ph1, 3, "UOM", hdrNavy); str(ph1, 4, "Ordered", hdrNavy); str(ph1, 5, "Received", hdrNavy);
//             str(ph1, 6, "Defective", hdrOrange); str(ph1, 7, "Rejected", hdrOrange); str(ph1, 8, "Accepted", hdrGreen);
//             str(ph1, 9, "PO Rate", hdrNavy); str(ph1, 10, "PO Line Total", hdrNavy); str(ph1, 11, "Accepted Value", hdrGreen);
//             str(ph1, 12, "Condition", hdrOrange); str(ph1, 13, "QA Remarks", hdrOrange);
//             int sl = 1;
//             for (GRNLineItem li : lineItems) {
//                 Row r = s2.createRow(row++); CellStyle cs = alt(sl, valueStyle, altStyle);
//                 str(r, 0, String.valueOf(sl++), cs); str(r, 1, s(li.getItemCode()), cs); str(r, 2, s(li.getItemDescription()), cs);
//                 str(r, 3, s(li.getUom()), cs); num(r, 4, li.getOrderedQuantity(), amtStyle); num(r, 5, li.getReceivedQuantity(), amtStyle);
//                 num(r, 6, li.getDefectiveQuantity(), amtStyle); num(r, 7, li.getRejectedQuantity(), amtStyle); num(r, 8, li.getAcceptedQuantity(), amtStyle);
//                 num(r, 9, li.getPoUnitRate(), amtStyle); num(r, 10, li.getPoLineTotal(), amtStyle); num(r, 11, li.getAcceptedValue(), amtStyle);
//                 str(r, 12, li.getItemCondition() != null ? li.getItemCondition().name() : "GOOD", statusStyle); str(r, 13, s(li.getQaRemarks()), cs);
//             }
//             row++;
//             String[][] grnTotals = {
//                 {"Total Ordered Value", grn.getTotalOrderedValue() != null ? grn.getTotalOrderedValue().toPlainString() : "0"},
//                 {"Total Received Value", grn.getTotalReceivedValue() != null ? grn.getTotalReceivedValue().toPlainString() : "0"},
//                 {"Total Rejected Value", grn.getTotalRejectedValue() != null ? grn.getTotalRejectedValue().toPlainString() : "0"}
//             };
//             for (int t = 0; t < grnTotals.length; t++) {
//                 Row tr = s2.createRow(row++); boolean isLast = (t == grnTotals.length - 1);
//                 Cell lc = tr.createCell(10); lc.setCellValue(grnTotals[t][0]); lc.setCellStyle(isLast ? totalStyle : labelStyle);
//                 Cell vc = tr.createCell(11); try { vc.setCellValue(Double.parseDouble(grnTotals[t][1])); } catch (Exception e) { vc.setCellValue(0); } vc.setCellStyle(isLast ? totalStyle : amtStyle);
//             }
//             wb.write(out);
//             return out.toByteArray();
//         }
//     }

//     // =========================================================================
//     //  THREE-WAY MATCH EXCEL
//     // =========================================================================
//     @Transactional(readOnly = true)
//     public byte[] generateThreeWayMatchExcel(Long matchId) throws Exception {
//         ThreeWayMatch match = matchRepository.findByIdWithLineResults(matchId).orElseThrow(() -> new RuntimeException("3-Way Match not found: " + matchId));
//         List<MatchLineResult> lineResults = match.getLineResults();

//         try (XSSFWorkbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
//             CellStyle titleStyle = makeTitleStyle(wb); CellStyle hdrNavy = makeHeaderStyle(wb, rgb(0x1e, 0x40, 0x88));
//             CellStyle hdrGreen = makeHeaderStyle(wb, rgb(0x1b, 0x5e, 0x20)); CellStyle hdrOrange = makeHeaderStyle(wb, rgb(0xe6, 0x55, 0x00));
//             CellStyle labelStyle = makeLabelStyle(wb); CellStyle valueStyle = makeValueStyle(wb);
//             CellStyle amtStyle = makeAmountStyle(wb); CellStyle altStyle = makeAltRowStyle(wb);
//             CellStyle totalStyle = makeTotalStyle(wb); CellStyle statusStyle = makeStatusStyle(wb);
//             CellStyle lowestStyle = makeLowestStyle(wb); CellStyle warnStyle = makeWarnStyle(wb);

//             XSSFSheet s1 = wb.createSheet("Match Summary");
//             s1.setColumnWidth(0, 6000); s1.setColumnWidth(1, 9000); s1.setColumnWidth(2, 6000); s1.setColumnWidth(3, 9000);
//             int row = 0;
//             boolean isMatched = "MATCHED".equals(match.getMatchStatus() != null ? match.getMatchStatus().name() : "");
//             boolean isFailed = match.getMatchStatus() != null && (match.getMatchStatus() == ThreeWayMatchStatus.FAILED || match.getMatchStatus() == ThreeWayMatchStatus.ITEM_MISMATCH || match.getMatchStatus() == ThreeWayMatchStatus.DISPUTED);
//             CellStyle bannerStyle = isMatched ? makeBannerMatchStyle(wb) : isFailed ? makeBannerFailStyle(wb) : makeBannerWarnStyle(wb);
//             row = addBannerCustomStyle(s1, row, "3-WAY MATCH REPORT", bannerStyle, "Match #" + match.getId() + "  v" + match.getMatchVersion() + "  |  Status: " + (match.getMatchStatus() != null ? match.getMatchStatus().name() : "—") + "  |  Generated: " + LocalDateTime.now().format(DTTM_FMT), 4);
//             row = addSectionHeader(s1, row, "DOCUMENT REFERENCES", hdrNavy, 4);
//             row = addLV(s1, row, labelStyle, valueStyle, "PO Number", s(match.getPoNumber()), "GRN Number", s(match.getGrnNumber()));
//             row = addLV(s1, row, labelStyle, valueStyle, "Invoice Number", s(match.getInvoiceNumber()), "Match Version", String.valueOf(match.getMatchVersion()));
//             row = addLV(s1, row, labelStyle, valueStyle, "Tolerance %", s(match.getTolerancePercentage()), "Performed By", s(match.getPerformedByName()));
//             row = addLV(s1, row, labelStyle, valueStyle, "Performed At", fmt(match.getPerformedAt()), "Updated At", fmt(match.getUpdatedAt()));
//             row++;
//             row = addSectionHeader(s1, row, "FINANCIAL COMPARISON", hdrNavy, 4);
//             row = addLV(s1, row, labelStyle, valueStyle, "PO Total Value", s(match.getPoTotalValue()), "GRN Accepted Value", s(match.getGrnAcceptedValue()));
//             row = addLV(s1, row, labelStyle, valueStyle, "Invoice Total Value", s(match.getInvoiceTotalValue()), "Variance Amount", s(match.getVarianceAmount()));
//             row = addLV(s1, row, labelStyle, valueStyle, "Variance %", s(match.getVariancePercentage()), "Approved Payment", s(match.getApprovedPaymentAmount()));
//             row++;
//             row = addSectionHeader(s1, row, "MATCH FLAGS", hdrNavy, 4);
//             row = addLV(s1, row, labelStyle, valueStyle, "Quantity Mismatch", bool(match.getHasQuantityMismatch()), "Price Mismatch", bool(match.getHasPriceMismatch()));
//             row = addLV(s1, row, labelStyle, valueStyle, "Item Mismatch", bool(match.getHasItemMismatch()), "Excess Delivery", bool(match.getHasExcessDelivery()));
//             row = addLV(s1, row, labelStyle, valueStyle, "Matched Lines", String.valueOf(match.getTotalMatchedLines()), "Mismatch Lines", String.valueOf(match.getTotalMismatchLines()));
//             row++;
//             if (match.getResolution() != null) {
//                 row = addSectionHeader(s1, row, "RESOLUTION", hdrNavy, 4);
//                 row = addLV(s1, row, labelStyle, valueStyle, "Resolution", match.getResolution().name(), "Resolved By", s(match.getResolvedByName()));
//                 row = addLV(s1, row, labelStyle, valueStyle, "Resolved At", fmt(match.getResolvedAt()), "Resolution Remarks", s(match.getResolutionRemarks()));
//             }
//             if (match.getMatchSummary() != null) {
//                 row++; Row summaryRow = s1.createRow(row++); summaryRow.setHeightInPoints(30);
//                 s1.addMergedRegion(new CellRangeAddress(row - 1, row - 1, 0, 3));
//                 Cell sc = summaryRow.createCell(0); sc.setCellValue(match.getMatchSummary()); sc.setCellStyle(isMatched ? lowestStyle : warnStyle);
//             }

//             XSSFSheet s2 = wb.createSheet("Line Results");
//             setWidths(s2, new int[]{1200, 3000, 7000, 2000, 3000, 3000, 3000, 3500, 3500, 3000, 3500, 3500, 2500, 4500, 4500, 4500, 3000, 8000});
//             row = 0;
//             row = addBanner(s2, row, "LINE-BY-LINE MATCH RESULTS", titleStyle, s(match.getInvoiceNumber()) + " ↔ " + s(match.getGrnNumber()) + "  |  " + lineResults.size() + " lines", 18);
//             String[] cols = {"#", "Item Code", "Item Description", "UOM", "PO Qty", "GRN Accepted", "Inv Qty", "Qty Variance", "Qty Var%", "Within Tol?", "PO Rate", "Inv Price", "Price Var%", "PO Line Value", "GRN Acc. Value", "Inv Line Value", "Line Status", "Mismatch Notes"};
//             addHeaderRow(s2, row++, hdrNavy, cols);
//             int sl = 1;
//             for (MatchLineResult lr : lineResults) {
//                 Row r = s2.createRow(row++);
//                 boolean lineMatched = lr.getLineMatchStatus() == MatchLineResult.LineMatchStatus.MATCHED || lr.getLineMatchStatus() == MatchLineResult.LineMatchStatus.PARTIALLY_MATCHED;
//                 boolean lineFailed = lr.getLineMatchStatus() == MatchLineResult.LineMatchStatus.NOT_IN_PO || lr.getLineMatchStatus() == MatchLineResult.LineMatchStatus.NOT_RECEIVED;
//                 CellStyle cs = lineFailed ? warnStyle : alt(sl, valueStyle, altStyle);
//                 CellStyle ams = lineFailed ? warnStyle : amtStyle;
//                 CellStyle gs = lineMatched ? lowestStyle : ams;
//                 str(r, 0, String.valueOf(sl++), cs); str(r, 1, s(lr.getItemCode()), cs); str(r, 2, s(lr.getItemDescription()), cs);
//                 str(r, 3, s(lr.getUom()), cs); num(r, 4, lr.getPoOrderedQuantity(), ams); num(r, 5, lr.getGrnAcceptedQuantity(), gs);
//                 num(r, 6, lr.getInvoiceQuantity(), ams); num(r, 7, lr.getQuantityVariance(), ams); num(r, 8, lr.getQuantityVariancePct(), amtStyle);
//                 str(r, 9, Boolean.TRUE.equals(lr.getWithinTolerance()) ? "✓ Yes" : "✗ No", statusStyle);
//                 num(r, 10, lr.getPoUnitRate(), ams); num(r, 11, lr.getInvoiceUnitPrice(), ams); num(r, 12, lr.getPriceVariancePct(), amtStyle);
//                 num(r, 13, lr.getPoLineValue(), ams); num(r, 14, lr.getGrnAcceptedValue(), gs); num(r, 15, lr.getInvoiceLineValue(), ams);
//                 str(r, 16, lr.getLineMatchStatus() != null ? lr.getLineMatchStatus().name() : "—", statusStyle); str(r, 17, s(lr.getMismatchNotes()), cs);
//             }
//             row++;
//             BigDecimal totPOVal = lineResults.stream().filter(lr -> lr.getPoLineValue() != null).map(MatchLineResult::getPoLineValue).reduce(BigDecimal.ZERO, BigDecimal::add);
//             BigDecimal totGRNVal = lineResults.stream().filter(lr -> lr.getGrnAcceptedValue() != null).map(MatchLineResult::getGrnAcceptedValue).reduce(BigDecimal.ZERO, BigDecimal::add);
//             BigDecimal totInvVal = lineResults.stream().filter(lr -> lr.getInvoiceLineValue() != null).map(MatchLineResult::getInvoiceLineValue).reduce(BigDecimal.ZERO, BigDecimal::add);
//             BigDecimal totVarVal = totInvVal.subtract(totGRNVal);
//             Row totRow = s2.createRow(row++);
//             for (int c = 0; c < 13; c++) { Cell nc = totRow.createCell(c); nc.setCellStyle(totalStyle); if (c == 0) nc.setCellValue("TOTALS"); }
//             num(totRow, 13, totPOVal, totalStyle); num(totRow, 14, totGRNVal, totalStyle); num(totRow, 15, totInvVal, totalStyle);
//             for (int c = 16; c < 18; c++) { Cell nc = totRow.createCell(c); nc.setCellStyle(totalStyle); if (c == 16) nc.setCellValue("VARIANCE: " + (totVarVal.compareTo(BigDecimal.ZERO) >= 0 ? "+" : "") + totVarVal.toPlainString()); }

//             wb.write(out);
//             return out.toByteArray();
//         }
//     }

//     // =========================================================================
//     //  RFQ SUMMARY EXCEL — backward-compat overloads
//     // =========================================================================
//     @Transactional(readOnly = true)
//     public byte[] generateRFQSummaryExcel(Long rfqId) throws Exception {
//         return generateRFQSummaryExcel(rfqId, false, null, null, null, null, null);
//     }

//     @Transactional(readOnly = true)
//     public byte[] generateRFQSummaryExcelForSupplier(Long rfqId) throws Exception {
//         return generateRFQSummaryExcel(rfqId, true, null, null, null, null, null);
//     }

//     // =========================================================================
//     //  RFQ SUMMARY EXCEL — CORE IMPLEMENTATION
//     //
//     //  supplierMode = true  → 2 sheets only (Overview + Line Items)
//     //  supplierMode = false → 12 sheets (all existing)
//     //                       + Sheet 13 "Hierarchy Approval View"
//     //                         when userId != null AND levelOrder != null
//     //
//     //  NO calls to User.getHierarchyLevel() or User.getFullName()
//     //  All viewer info arrives as plain String/Integer params from frontend.
//     // =========================================================================
//     @Transactional(readOnly = true)
//     public byte[] generateRFQSummaryExcel(
//             Long rfqId,
//             boolean supplierMode,
//             Long userId,
//             Integer levelOrder,
//             String levelName,
//             String viewerName,
//             String viewerEmail) throws Exception {

//         RFQ rfq = rfqRepository.findByIdWithAllDetails(rfqId)
//                 .orElseThrow(() -> new RuntimeException("RFQ not found: " + rfqId));
//         rfqRepository.findByIdWithItems(rfqId).ifPresent(r -> r.getItems().size());
//         rfqRepository.findByIdWithSuppliers(rfqId).ifPresent(r -> r.getSelectedSuppliers().size());

//         List<RFQItem>            items     = rfqItemRepository.findByRfqId(rfqId);
//         List<RFQSupplier>        suppliers = supplierMode ? Collections.emptyList() : rfqSupplierRepository.findByRFQId(rfqId);
//         List<DynamicRFQApproval> approvals = supplierMode ? Collections.emptyList() : approvalRepository.findByRfqIdOrderBySequenceOrderAsc(rfqId);

//         List<PurchaseOrder> pos     = Collections.emptyList();
//         List<Invoice>       invList = Collections.emptyList();
//         List<GRN>           grns    = Collections.emptyList();
//         List<ThreeWayMatch> matches = Collections.emptyList();

//         if (!supplierMode) {
//             try { pos = poRepository.findByRfqId(rfqId); } catch (Exception ignored) {}
//             if (!pos.isEmpty()) {
//                 Long poId = pos.get(0).getId();
//                 try { grns = grnRepository.findByPurchaseOrderId(poId); } catch (Exception ignored) {}
//                 try { invList = invoiceRepository.findByPoId(poId); } catch (Exception ignored) {}
//                 if (!invList.isEmpty()) {
//                     Long invId = invList.get(0).getId();
//                     try { matches = matchRepository.findByInvoiceId(invId); } catch (Exception ignored) {}
//                 }
//             }
//         }

//         // Determine whether to add hierarchy sheet
//         // Requires userId, levelOrder both present and not in supplier mode
//         boolean addHierarchySheet = !supplierMode && userId != null && levelOrder != null;

//         try (XSSFWorkbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

//             CellStyle titleStyle = makeTitleStyle(wb);
//             CellStyle hdrNavy   = makeHeaderStyle(wb, rgb(0x1e, 0x40, 0x88));
//             CellStyle hdrGreen  = makeHeaderStyle(wb, rgb(0x2e, 0x7d, 0x32));
//             CellStyle hdrOrange = makeHeaderStyle(wb, rgb(0xe6, 0x55, 0x00));
//             CellStyle hdrPurple = makeHeaderStyle(wb, rgb(0x6a, 0x0d, 0xad));
//             CellStyle labelStyle = makeLabelStyle(wb);
//             CellStyle valueStyle = makeValueStyle(wb);
//             CellStyle amtStyle  = makeAmountStyle(wb);
//             CellStyle altStyle  = makeAltRowStyle(wb);
//             CellStyle statusStyle = makeStatusStyle(wb);
//             CellStyle lowestStyle = makeLowestStyle(wb);
//             CellStyle totalStyle  = makeTotalStyle(wb);
//             CellStyle warnStyle   = makeWarnStyle(wb);

//             // ── SHEET 1: RFQ Overview ────────────────────────────────────
//             XSSFSheet s1 = wb.createSheet("RFQ Overview");
//             s1.setColumnWidth(0, 6000); s1.setColumnWidth(1, 9000); s1.setColumnWidth(2, 6000); s1.setColumnWidth(3, 9000);
//             int row = 0;
//             String bannerSub = "RFQ: " + rfq.getRfqNumber() + (supplierMode ? "   |   Supplier Copy" : "") + "   |   Generated: " + LocalDateTime.now().format(DTTM_FMT);
//             row = addBanner(s1, row, "RFQ SUMMARY REPORT", titleStyle, bannerSub, 4);
//             row = addSectionHeader(s1, row, "RFQ INFORMATION", hdrNavy, 4);
//             row = addLV(s1, row, labelStyle, valueStyle, "RFQ Number", rfq.getRfqNumber(), "RFQ Title", rfq.getRfqTitle());
//             row = addLV(s1, row, labelStyle, valueStyle, "Status", en(rfq.getStatus()), "Priority", en(rfq.getPriority()));
//             row = addLV(s1, row, labelStyle, valueStyle, "Issue Date", fmt(rfq.getIssueDate()), "Due Date", fmt(rfq.getDueDate()));
//             row = addLV(s1, row, labelStyle, valueStyle, "Item Req Date", fmt(rfq.getItemRequiredDate()), "Approval Req", bool(rfq.getApprovalRequired()));
//             row = addLV(s1, row, labelStyle, valueStyle, "Payment Terms", s(rfq.getPaymentTerms()), "Delivery Terms", s(rfq.getDeliveryTerms()));
//             row = addLV(s1, row, labelStyle, valueStyle, "Tax %", s(rfq.getTaxPercentage()), "Justification", s(rfq.getJustification()));
//             row = addLV(s1, row, labelStyle, valueStyle, "Cost Center", s(rfq.getCostCenterCode()), "Project Code", s(rfq.getProjectCode()));
//             row++;
//             if (rfq.getBuyer() != null) {
//                 Buyer b = rfq.getBuyer();
//                 row = addSectionHeader(s1, row, "BUYER INFORMATION", hdrNavy, 4);
//                 row = addLV(s1, row, labelStyle, valueStyle, "Company", b.getCompanyName(), "Type", s(b.getCompanyType()));
//                 row = addLV(s1, row, labelStyle, valueStyle, "Contact", b.getContactPersonName(), "Email", s(b.getContactPersonEmail()));
//                 row = addLV(s1, row, labelStyle, valueStyle, "Phone", s(b.getContactPersonPhone()), "GST", s(b.getGstNumber()));
//                 row = addLV(s1, row, labelStyle, valueStyle, "City/State", s(b.getCity()) + ", " + s(b.getState()), "Country", s(b.getCountry()));
//                 row++;
//             }
//             if (rfq.getLocation() != null) {
//                 Location l = rfq.getLocation();
//                 row = addSectionHeader(s1, row, "DELIVERY LOCATION", hdrNavy, 4);
//                 row = addLV(s1, row, labelStyle, valueStyle, "Location Name", l.getLocationName(), "Type", s(l.getLocationType()));
//                 row = addLV(s1, row, labelStyle, valueStyle, "Address", s(l.getAddressLine1()), "City", s(l.getCity()));
//                 row = addLV(s1, row, labelStyle, valueStyle, "State", s(l.getState()), "Country", s(l.getCountry()));
//                 row = addLV(s1, row, labelStyle, valueStyle, "Currency", s(l.getCurrencyCode()), "Contact", s(l.getLocationContactName()));
//                 row++;
//             }
//             if (rfq.getCreatedByUser() != null && !supplierMode) {
//                 User u = rfq.getCreatedByUser();
//                 row = addSectionHeader(s1, row, "CREATED BY", hdrNavy, 4);
//                 addLV(s1, row, labelStyle, valueStyle, "Name", u.getFirstName() + " " + u.getLastName(), "Email", s(u.getEmail()));
//             }

//             // ── SHEET 2: Line Items ──────────────────────────────────────
//             XSSFSheet s2 = wb.createSheet("Line Items");
//             setWidths(s2, new int[]{1200, 3500, 8000, 7000, 6000, 2500, 3000, 3500, 4000});
//             row = 0;
//             row = addBanner(s2, row, "LINE ITEMS", titleStyle, rfq.getRfqNumber() + "  —  " + rfq.getRfqTitle() + "  [" + items.size() + " items]", 9);
//             String[] itCols = {"#", "Item Code", "Description", "Detailed Description", "Specifications", "Qty", "UOM", "Unit Price", "Required Date"};
//             addHeaderRow(s2, row++, hdrGreen, itCols);
//             int sl = 1;
//             for (RFQItem it : items) {
//                 Row r = s2.createRow(row++); CellStyle cs = alt(sl, valueStyle, altStyle);
//                 str(r, 0, String.valueOf(sl++), cs); str(r, 1, s(it.getItemCode()), cs);
//                 str(r, 2, s(it.getItemDescription()), cs); str(r, 3, s(it.getItemDescriptionDetailed()), cs);
//                 str(r, 4, s(it.getSpecifications()), cs); num(r, 5, it.getQuantity(), amtStyle);
//                 str(r, 6, s(it.getUom()), cs); num(r, 7, it.getUnitPrice(), amtStyle);
//                 str(r, 8, fmt(it.getItemRequiredDate()), cs);
//             }

//             // ── SHEET 3: Invited Suppliers ───────────────────────────────
//             if (!supplierMode) {
//                 XSSFSheet s3 = wb.createSheet("Invited Suppliers");
//                 setWidths(s3, new int[]{1200, 5500, 4000, 5000, 4000, 3500, 4000, 4000, 3800});
//                 row = 0;
//                 row = addBanner(s3, row, "INVITED SUPPLIERS", titleStyle, rfq.getRfqNumber() + "  [" + suppliers.size() + " suppliers]", 9);
//                 Long awardedSupplierId = null; String awardedPoNumber = null;
//                 if (!pos.isEmpty() && pos.get(0).getSupplier() != null) { awardedSupplierId = pos.get(0).getSupplier().getId(); awardedPoNumber = pos.get(0).getPoNumber(); }
//                 final Long finalAwardedId = awardedSupplierId; final String finalPoNumber = awardedPoNumber;
//                 CellStyle awardRowStyle = makeAwardedRowStyle(wb); CellStyle awardAmtStyle = makeAwardedAmtStyle(wb);
//                 String[] supCols = {"#", "Company", "Contact Person", "Email", "Phone", "Status", "Responded At", "Quote Amount", "Award Status"};
//                 addHeaderRow(s3, row++, hdrNavy, supCols); s3.createFreezePane(0, row);
//                 sl = 1;
//                 for (RFQSupplier rs : suppliers) {
//                     Supplier sup = rs.getSupplier(); Long supId = sup != null ? sup.getId() : null;
//                     boolean isAwarded = finalAwardedId != null && finalAwardedId.equals(supId);
//                     Row r = s3.createRow(row++); CellStyle cs = isAwarded ? awardRowStyle : alt(sl, valueStyle, altStyle); CellStyle ams = isAwarded ? awardAmtStyle : amtStyle;
//                     str(r, 0, String.valueOf(sl++), cs); str(r, 1, sup != null ? s(sup.getCompanyName()) : "N/A", cs);
//                     str(r, 2, sup != null ? s(sup.getContactPersonName()) : "N/A", cs); str(r, 3, sup != null ? s(sup.getContactPersonEmail()) : "N/A", cs);
//                     str(r, 4, sup != null ? s(sup.getContactPersonPhone()) : "N/A", cs); str(r, 5, s(rs.getStatus()), isAwarded ? cs : statusStyle);
//                     str(r, 6, fmt(rs.getRespondedAt()), cs); num(r, 7, rs.getQuoteAmount(), ams);
//                     str(r, 8, isAwarded ? (finalPoNumber != null ? "★ AWARDED  |  PO: " + finalPoNumber : "★ AWARDED") : "—", cs);
//                 }
//                 if (finalAwardedId != null) {
//                     row++; Row legendRow = s3.createRow(row++); s3.addMergedRegion(new CellRangeAddress(row - 1, row - 1, 0, 8));
//                     Cell lc = legendRow.createCell(0); lc.setCellValue("★  Gold row = supplier awarded the Purchase Order" + (finalPoNumber != null ? "  (PO: " + finalPoNumber + ")" : "")); lc.setCellStyle(awardRowStyle);
//                 }
//             }

//             // ── SHEET 4: Approval History ────────────────────────────────
//             if (!supplierMode) {
//                 XSSFSheet s4 = wb.createSheet("Approval History");
//                 setWidths(s4, new int[]{1200, 4000, 4000, 5000, 3500, 3500, 4000, 7000});
//                 row = 0;
//                 row = addBanner(s4, row, "APPROVAL HISTORY", titleStyle, rfq.getRfqNumber(), 8);
//                 String[] apCols = {"#", "Level", "Level Order", "Approver", "Email", "Status", "Action Date", "Comments / Remarks"};
//                 addHeaderRow(s4, row++, hdrNavy, apCols);
//                 sl = 1;
//                 for (DynamicRFQApproval ap : approvals) {
//                     Row r = s4.createRow(row++); CellStyle cs = alt(sl, valueStyle, altStyle);
//                     str(r, 0, String.valueOf(sl++), cs);
//                     str(r, 1, ap.getHierarchyLevel() != null ? ap.getHierarchyLevel().getLevelName() : "N/A", cs);
//                     str(r, 2, ap.getHierarchyLevel() != null ? String.valueOf(ap.getHierarchyLevel().getLevelOrder()) : "N/A", cs);
//                     str(r, 3, ap.getApproverUser() != null ? ap.getApproverUser().getFullName() : "N/A", cs);
//                     str(r, 4, ap.getApproverUser() != null ? s(ap.getApproverUser().getEmail()) : "N/A", cs);
//                     str(r, 5, ap.getStatus() != null ? ap.getStatus().name() : "N/A", statusStyle);
//                     str(r, 6, fmt(ap.getActionDate()), cs);
//                     String extra = s(ap.getComments());
//                     if (ap.getHoldRemarks() != null) extra += " | HOLD: " + ap.getHoldRemarks();
//                     if (ap.getRejectRemarks() != null) extra += " | REJECT: " + ap.getRejectRemarks();
//                     if (ap.getReleaseRemarks() != null) extra += " | RELEASE: " + ap.getReleaseRemarks();
//                     str(r, 7, extra, cs);
//                 }
//                 if (approvals.isEmpty()) { Row r = s4.createRow(row); str(r, 0, "No approval history recorded yet.", valueStyle); }
//             }

//             // ── SHEET 5: Purchase Order ──────────────────────────────────
//             if (!supplierMode) {
//                 XSSFSheet s5 = wb.createSheet("Purchase Order");
//                 s5.setColumnWidth(0, 6000); s5.setColumnWidth(1, 9000); s5.setColumnWidth(2, 6000); s5.setColumnWidth(3, 9000);
//                 row = 0;
//                 if (pos.isEmpty()) {
//                     row = addBanner(s5, row, "PURCHASE ORDER", titleStyle, "No PO raised for this RFQ yet.", 4);
//                 } else {
//                     PurchaseOrder po = pos.get(0);
//                     row = addBanner(s5, row, "PURCHASE ORDER", titleStyle, "PO: " + s(po.getPoNumber()) + "  |  Status: " + (po.getStatus() != null ? po.getStatus().name() : "—") + "  |  RFQ: " + rfq.getRfqNumber(), 4);
//                     row = addSectionHeader(s5, row, "PO INFORMATION", hdrGreen, 4);
//                     row = addLV(s5, row, labelStyle, valueStyle, "PO Number", s(po.getPoNumber()), "PO Date", fmt(po.getPoDate()));
//                     row = addLV(s5, row, labelStyle, valueStyle, "Status", po.getStatus() != null ? po.getStatus().name() : "—", "Approval Status", po.getApprovalStatus() != null ? po.getApprovalStatus().name() : "—");
//                     row = addLV(s5, row, labelStyle, valueStyle, "Currency", s(po.getCurrencyCode()) + " " + s(po.getCurrencySymbol()), "Amount in Words", s(po.getAmountInWords()));
//                     row = addLV(s5, row, labelStyle, valueStyle, "Payment Terms", s(po.getPaymentTerms()), "Delivery Terms", s(po.getDeliveryTerms()));
//                     row = addLV(s5, row, labelStyle, valueStyle, "Approved By", s(po.getApprovedByName()), "Designation", s(po.getApprovedByDesignation()));
//                     row = addLV(s5, row, labelStyle, valueStyle, "Approval Date", fmt(po.getApprovalDate()), "Buyer Remarks", s(po.getBuyerRemarks()));
//                     row++;
//                     if (po.getSupplier() != null) {
//                         Supplier sup = po.getSupplier();
//                         row = addSectionHeader(s5, row, "SUPPLIER", hdrGreen, 4);
//                         row = addLV(s5, row, labelStyle, valueStyle, "Company", s(sup.getCompanyName()), "Email", s(sup.getContactPersonEmail()));
//                         row = addLV(s5, row, labelStyle, valueStyle, "Contact", s(sup.getContactPersonName()), "Phone", s(sup.getContactPersonPhone()));
//                         row++;
//                     }
//                     if (po.getDeliveryLocation() != null) {
//                         Location dl = po.getDeliveryLocation();
//                         row = addSectionHeader(s5, row, "DELIVERY LOCATION", hdrGreen, 4);
//                         row = addLV(s5, row, labelStyle, valueStyle, "Name", dl.getLocationName(), "City", s(dl.getCity()));
//                         row = addLV(s5, row, labelStyle, valueStyle, "State", s(dl.getState()), "Country", s(dl.getCountry()));
//                         row++;
//                     }
//                     row = addSectionHeader(s5, row, "FINANCIAL SUMMARY", hdrGreen, 4);
//                     row = addLV(s5, row, labelStyle, valueStyle, "Subtotal", s(po.getSubtotal()), "Tax Amount", s(po.getTaxAmount()));
//                     row = addLV(s5, row, labelStyle, valueStyle, "Grand Total", s(po.getGrandTotal()), "Tax %", s(po.getTaxPercentage()));
//                 }
//             }

//             // ── SHEET 6: PO Line Items ───────────────────────────────────
//             if (!supplierMode && !pos.isEmpty()) {
//                 PurchaseOrder po = pos.get(0); List<POLineItem> poLines = po.getLineItems();
//                 XSSFSheet s6 = wb.createSheet("PO Line Items");
//                 setWidths(s6, new int[]{1200, 3000, 7000, 2500, 2000, 2500, 4000, 2000, 2000, 4500, 4000});
//                 row = 0;
//                 row = addBanner(s6, row, "PO LINE ITEMS", titleStyle, s(po.getPoNumber()) + "  [" + poLines.size() + " items]  |  Currency: " + s(po.getCurrencyCode()), 11);
//                 String[] plCols = {"#", "Item Code", "Description", "Specifications", "UOM", "Qty", "Unit Rate", "Disc %", "Tax %", "Line Total", "Brand/Make/Model"};
//                 addHeaderRow(s6, row++, hdrGreen, plCols); sl = 1;
//                 for (POLineItem li : poLines) {
//                     Row r = s6.createRow(row++); CellStyle cs = alt(sl, valueStyle, altStyle);
//                     str(r, 0, String.valueOf(sl++), cs); str(r, 1, s(li.getItemCode()), cs); str(r, 2, s(li.getItemDescription()), cs);
//                     str(r, 3, s(li.getSpecifications()), cs); str(r, 4, s(li.getUom()), cs); num(r, 5, li.getQuantity(), amtStyle);
//                     num(r, 6, li.getUnitRate(), amtStyle); num(r, 7, li.getDiscountPercentage(), amtStyle);
//                     num(r, 8, li.getTaxPercentage(), amtStyle); num(r, 9, li.getLineTotal(), amtStyle); str(r, 10, s(li.getBrandMakeModel()), cs);
//                 }
//                 row++;
//                 Object[][] poTots = {{"Subtotal", po.getSubtotal()}, {"Tax Amount", po.getTaxAmount()}, {"GRAND TOTAL", po.getGrandTotal()}};
//                 for (int t = 0; t < poTots.length; t++) {
//                     Row tr = s6.createRow(row++); boolean ig = (t == poTots.length - 1);
//                     Cell lc = tr.createCell(9); lc.setCellValue((String) poTots[t][0]); lc.setCellStyle(ig ? totalStyle : labelStyle);
//                     Cell vc = tr.createCell(10); if (poTots[t][1] instanceof BigDecimal) vc.setCellValue(((BigDecimal) poTots[t][1]).doubleValue()); else vc.setCellValue(0); vc.setCellStyle(ig ? totalStyle : amtStyle);
//                 }
//             }

//             // ── SHEET 7: Invoice ─────────────────────────────────────────
//             if (!supplierMode) {
//                 XSSFSheet s7 = wb.createSheet("Invoice");
//                 s7.setColumnWidth(0, 6000); s7.setColumnWidth(1, 9000); s7.setColumnWidth(2, 6000); s7.setColumnWidth(3, 9000);
//                 row = 0;
//                 if (invList.isEmpty()) {
//                     row = addBanner(s7, row, "INVOICE", titleStyle, "No invoice submitted yet for this RFQ.", 4);
//                 } else {
//                     Invoice invoice = invList.get(0);
//                     row = addBanner(s7, row, "INVOICE", titleStyle, "Invoice: " + s(invoice.getInvoiceNumber()) + "  |  Status: " + (invoice.getStatus() != null ? invoice.getStatus().name() : "—") + "  |  RFQ: " + rfq.getRfqNumber(), 4);
//                     row = addSectionHeader(s7, row, "INVOICE DETAILS", hdrPurple, 4);
//                     row = addLV(s7, row, labelStyle, valueStyle, "Invoice Number", s(invoice.getInvoiceNumber()), "Status", invoice.getStatus() != null ? invoice.getStatus().name() : "—");
//                     row = addLV(s7, row, labelStyle, valueStyle, "Invoice Date", fmt(invoice.getInvoiceDate()), "Due Date", fmt(invoice.getDueDate()));
//                     row = addLV(s7, row, labelStyle, valueStyle, "PO Number", s(invoice.getPoNumber()), "Currency", s(invoice.getCurrency()));
//                     row = addLV(s7, row, labelStyle, valueStyle, "Supplier", s(invoice.getSupplierName()), "Supplier Email", s(invoice.getSupplierEmail()));
//                     row = addLV(s7, row, labelStyle, valueStyle, "Buyer Company", s(invoice.getBuyerCompanyName()), "Payment Terms", s(invoice.getPaymentTerms()));
//                     row = addLV(s7, row, labelStyle, valueStyle, "Subtotal", s(invoice.getSubtotal()), "Tax Amount", s(invoice.getTaxAmount()));
//                     row = addLV(s7, row, labelStyle, valueStyle, "Total Amount", s(invoice.getTotalAmount()), "Approved By", s(invoice.getApprovedRejectedBy()));
//                     row = addLV(s7, row, labelStyle, valueStyle, "Approval Date", fmt(invoice.getApprovedRejectedAt()), "Approval Remarks", s(invoice.getApprovalRemarks()));
//                     row++;
//                     if (invoice.getBankName() != null) {
//                         row = addSectionHeader(s7, row, "BANK DETAILS", hdrPurple, 4);
//                         row = addLV(s7, row, labelStyle, valueStyle, "Bank Name", s(invoice.getBankName()), "Account Name", s(invoice.getBankAccountName()));
//                         row = addLV(s7, row, labelStyle, valueStyle, "Account Number", s(invoice.getBankAccountNumber()), "IFSC Code", s(invoice.getBankIfscCode()));
//                         row = addLV(s7, row, labelStyle, valueStyle, "SWIFT Code", s(invoice.getBankSwiftCode()), "Notes", s(invoice.getNotes()));
//                         row++;
//                     }
//                 }
//             }

//             // ── SHEET 8: Invoice Line Items ──────────────────────────────
//             if (!supplierMode && !invList.isEmpty()) {
//                 Invoice invoice = invList.get(0); List<InvoiceLineItem> invLines = invoice.getLineItems();
//                 XSSFSheet s8 = wb.createSheet("Invoice Line Items");
//                 setWidths(s8, new int[]{1200, 3000, 7000, 2500, 2000, 2500, 4000, 2000, 2000, 4500});
//                 row = 0;
//                 row = addBanner(s8, row, "INVOICE LINE ITEMS", titleStyle, s(invoice.getInvoiceNumber()) + "  [" + invLines.size() + " items]", 10);
//                 String[] ilCols = {"#", "Item Code", "Description", "HSN/SAC", "UOM", "Quantity", "Unit Price", "Disc %", "Tax %", "Line Total"};
//                 addHeaderRow(s8, row++, hdrPurple, ilCols); sl = 1;
//                 for (InvoiceLineItem li : invLines) {
//                     Row r = s8.createRow(row++); CellStyle cs = alt(sl, valueStyle, altStyle);
//                     str(r, 0, String.valueOf(sl++), cs); str(r, 1, s(li.getItemCode()), cs); str(r, 2, s(li.getItemDescription()), cs);
//                     str(r, 3, s(li.getHsnSacCode()), cs); str(r, 4, s(li.getUom()), cs); num(r, 5, li.getQuantity(), amtStyle);
//                     num(r, 6, li.getUnitPrice(), amtStyle); num(r, 7, li.getDiscountPercentage(), amtStyle);
//                     num(r, 8, li.getTaxPercentage(), amtStyle); num(r, 9, li.getLineTotal(), amtStyle);
//                 }
//                 row++;
//                 Object[][] invTots = {{"Subtotal", invoice.getSubtotal()}, {"Tax Amount", invoice.getTaxAmount()}, {"TOTAL AMOUNT", invoice.getTotalAmount()}};
//                 for (int t = 0; t < invTots.length; t++) {
//                     Row tr = s8.createRow(row++); boolean ig = (t == invTots.length - 1);
//                     Cell lc = tr.createCell(8); lc.setCellValue((String) invTots[t][0]); lc.setCellStyle(ig ? totalStyle : labelStyle);
//                     Cell vc = tr.createCell(9); if (invTots[t][1] instanceof BigDecimal) vc.setCellValue(((BigDecimal) invTots[t][1]).doubleValue()); else vc.setCellValue(0); vc.setCellStyle(ig ? totalStyle : amtStyle);
//                 }
//             }

//             // ── SHEET 9: GRN ─────────────────────────────────────────────
//             if (!supplierMode) {
//                 XSSFSheet s9 = wb.createSheet("GRN");
//                 s9.setColumnWidth(0, 6000); s9.setColumnWidth(1, 9000); s9.setColumnWidth(2, 6000); s9.setColumnWidth(3, 9000);
//                 row = 0;
//                 if (grns.isEmpty()) {
//                     row = addBanner(s9, row, "GOODS RECEIPT NOTE", titleStyle, "No GRN created yet for this RFQ.", 4);
//                 } else {
//                     row = addBanner(s9, row, "GOODS RECEIPT NOTE — ALL DELIVERY BATCHES", titleStyle, rfq.getRfqNumber() + "  |  Total GRN Batches: " + grns.size() + "  |  PO: " + (pos.isEmpty() ? "—" : s(pos.get(0).getPoNumber())), 4);
//                     row = addSectionHeader(s9, row, "DELIVERY BATCH SUMMARY", hdrOrange, 4);
//                     Row sumHdr = s9.createRow(row++); sumHdr.setHeightInPoints(18);
//                     str(sumHdr, 0, "Batch #", hdrOrange); str(sumHdr, 1, "GRN Number", hdrOrange); str(sumHdr, 2, "Received Date", hdrOrange); str(sumHdr, 3, "Status", hdrOrange);
//                     int batchNum = 1;
//                     for (GRN grn : grns) {
//                         Row sumRow = s9.createRow(row++); CellStyle cs = alt(batchNum, valueStyle, altStyle);
//                         str(sumRow, 0, "Batch " + batchNum++, cs); str(sumRow, 1, s(grn.getGrnNumber()), cs); str(sumRow, 2, fmt(grn.getReceivedDate()), cs); str(sumRow, 3, grn.getStatus() != null ? grn.getStatus().name() : "—", statusStyle);
//                     }
//                     row++;
//                     batchNum = 1;
//                     for (GRN grn : grns) {
//                         row = addSectionHeader(s9, row, "BATCH " + batchNum + " — " + s(grn.getGrnNumber()) + "  |  Status: " + (grn.getStatus() != null ? grn.getStatus().name() : "—"), hdrOrange, 4);
//                         row = addLV(s9, row, labelStyle, valueStyle, "GRN Number", s(grn.getGrnNumber()), "Status", grn.getStatus() != null ? grn.getStatus().name() : "—");
//                         row = addLV(s9, row, labelStyle, valueStyle, "Received Date", fmt(grn.getReceivedDate()), "PO Number", s(grn.getPoNumber()));
//                         row = addLV(s9, row, labelStyle, valueStyle, "Supplier", s(grn.getSupplierName()), "Invoice Number", s(grn.getInvoiceNumber()));
//                         row = addLV(s9, row, labelStyle, valueStyle, "Received By", s(grn.getReceivedByName()), "QA Inspector", s(grn.getInspectedByName()));
//                         row = addLV(s9, row, labelStyle, valueStyle, "Approved By", s(grn.getApprovedByName()), "Approved At", fmt(grn.getApprovedAt()));
//                         row = addLV(s9, row, labelStyle, valueStyle, "Transporter", s(grn.getTransporterName()), "Vehicle No.", s(grn.getVehicleNumber()));
//                         row = addLV(s9, row, labelStyle, valueStyle, "Challan No.", s(grn.getDeliveryChallanNumber()), "LR Number", s(grn.getLrNumber()));
//                         row = addLV(s9, row, labelStyle, valueStyle, "Total Ordered", s(grn.getTotalOrderedValue()), "Total Received", s(grn.getTotalReceivedValue()));
//                         row = addLV(s9, row, labelStyle, valueStyle, "Total Rejected", s(grn.getTotalRejectedValue()), "Remarks", s(grn.getRemarks()));
//                         row++; batchNum++;
//                     }
//                 }
//             }

//             // ── SHEET 10: GRN Line Items ─────────────────────────────────
//             if (!supplierMode && !grns.isEmpty()) {
//                 XSSFSheet s10 = wb.createSheet("GRN Line Items");
//                 setWidths(s10, new int[]{1200, 1800, 3000, 6500, 2000, 2500, 2500, 2500, 2500, 2500, 4000, 3500, 5000});
//                 row = 0;
//                 int totalLineCount = grns.stream().mapToInt(g -> g.getLineItems() != null ? g.getLineItems().size() : 0).sum();
//                 row = addBanner(s10, row, "GRN LINE ITEMS — ALL DELIVERY BATCHES", titleStyle, rfq.getRfqNumber() + "  |  " + grns.size() + " GRN Batch(es)  |  " + totalLineCount + " Total Line Item(s)", 13);
//                 String[] glCols = {"#", "Batch", "Item Code", "Description", "UOM", "Ordered", "Received", "Defective", "Rejected", "Accepted", "PO Rate", "Accepted Value", "QA Remarks"};
//                 addHeaderRow(s10, row++, hdrOrange, glCols); s10.createFreezePane(0, row);
//                 CellStyle batchSubLabelStyle = makeBatchSubtotalLabelStyle(wb); CellStyle batchSubAmtStyle = makeBatchSubtotalAmtStyle(wb);
//                 int batchNum = 1; int lineNum = 1;
//                 for (GRN grn : grns) {
//                     List<GRNLineItem> grnLines = grn.getLineItems();
//                     if (grnLines == null || grnLines.isEmpty()) { batchNum++; continue; }
//                     Row batchHdrRow = s10.createRow(row++); batchHdrRow.setHeightInPoints(20);
//                     s10.addMergedRegion(new CellRangeAddress(row - 1, row - 1, 0, 12));
//                     Cell batchCell = batchHdrRow.createCell(0);
//                     batchCell.setCellValue("▶  BATCH " + batchNum + "  |  GRN: " + s(grn.getGrnNumber()) + "  |  Received: " + fmt(grn.getReceivedDate()) + "  |  Status: " + (grn.getStatus() != null ? grn.getStatus().name() : "—") + "  |  " + grnLines.size() + " item(s)");
//                     batchCell.setCellStyle(makeBatchSeparatorStyle(wb, batchNum));
//                     for (GRNLineItem li : grnLines) {
//                         Row r = s10.createRow(row++);
//                         boolean hasRej = li.getRejectedQuantity() != null && li.getRejectedQuantity().compareTo(BigDecimal.ZERO) > 0;
//                         CellStyle cs = hasRej ? warnStyle : alt(lineNum, valueStyle, altStyle); CellStyle ams = hasRej ? warnStyle : amtStyle;
//                         str(r, 0, String.valueOf(lineNum++), cs); str(r, 1, "Batch " + batchNum, cs); str(r, 2, s(li.getItemCode()), cs);
//                         str(r, 3, s(li.getItemDescription()), cs); str(r, 4, s(li.getUom()), cs);
//                         num(r, 5, li.getOrderedQuantity(), ams); num(r, 6, li.getReceivedQuantity(), ams); num(r, 7, li.getDefectiveQuantity(), ams);
//                         num(r, 8, li.getRejectedQuantity(), ams); num(r, 9, li.getAcceptedQuantity(), ams); num(r, 10, li.getPoUnitRate(), ams);
//                         num(r, 11, li.getAcceptedValue(), ams); str(r, 12, s(li.getQaRemarks()), cs);
//                     }
//                     BigDecimal batchAccepted = grnLines.stream().filter(li -> li.getAcceptedValue() != null).map(GRNLineItem::getAcceptedValue).reduce(BigDecimal.ZERO, BigDecimal::add);
//                     BigDecimal batchRejected = grnLines.stream().filter(li -> li.getRejectedValue() != null).map(GRNLineItem::getRejectedValue).reduce(BigDecimal.ZERO, BigDecimal::add);
//                     Row subTotRow = s10.createRow(row++);
//                     for (int c = 0; c < 13; c++) { Cell nc = subTotRow.createCell(c); nc.setCellStyle(batchSubLabelStyle); }
//                     subTotRow.getCell(0).setCellValue("Batch " + batchNum + " Subtotals →");
//                     subTotRow.getCell(5).setCellValue("Ordered Val: " + (grn.getTotalOrderedValue() != null ? grn.getTotalOrderedValue().toPlainString() : "0"));
//                     Cell accCell = subTotRow.createCell(11); accCell.setCellValue(batchAccepted.doubleValue()); accCell.setCellStyle(batchSubAmtStyle);
//                     Cell rejCell = subTotRow.createCell(8); rejCell.setCellValue(batchRejected.doubleValue()); rejCell.setCellStyle(batchSubAmtStyle);
//                     row++; batchNum++;
//                 }
//                 BigDecimal grandAccepted = grns.stream().filter(g -> g.getTotalReceivedValue() != null).map(GRN::getTotalReceivedValue).reduce(BigDecimal.ZERO, BigDecimal::add);
//                 BigDecimal grandRejected = grns.stream().filter(g -> g.getTotalRejectedValue() != null).map(GRN::getTotalRejectedValue).reduce(BigDecimal.ZERO, BigDecimal::add);
//                 Row grandRow = s10.createRow(row++);
//                 for (int c = 0; c < 13; c++) { Cell nc = grandRow.createCell(c); nc.setCellStyle(totalStyle); }
//                 grandRow.getCell(0).setCellValue("GRAND TOTAL — All " + grns.size() + " Batch(es)");
//                 grandRow.getCell(11).setCellValue(grandAccepted.doubleValue());
//                 grandRow.getCell(8).setCellValue(grandRejected.doubleValue());
//             }

//             // ── SHEET 11: 3-Way Match ────────────────────────────────────
//             if (!supplierMode) {
//                 XSSFSheet s11 = wb.createSheet("3-Way Match");
//                 s11.setColumnWidth(0, 6000); s11.setColumnWidth(1, 9000); s11.setColumnWidth(2, 6000); s11.setColumnWidth(3, 9000);
//                 row = 0;
//                 if (matches.isEmpty()) {
//                     row = addBanner(s11, row, "3-WAY MATCH", titleStyle, "3-Way Match not yet performed for this RFQ.", 4);
//                 } else {
//                     ThreeWayMatch match = matches.get(0);
//                     boolean isFullMatch = match.getMatchStatus() == ThreeWayMatchStatus.MATCHED || match.getMatchStatus() == ThreeWayMatchStatus.OVERRIDDEN_APPROVED;
//                     CellStyle matchBanner = isFullMatch ? makeBannerMatchStyle(wb) : makeBannerWarnStyle(wb);
//                     row = addBannerCustomStyle(s11, row, "3-WAY MATCH REPORT", matchBanner, "Match #" + match.getId() + "  v" + match.getMatchVersion() + "  |  Status: " + (match.getMatchStatus() != null ? match.getMatchStatus().name() : "—") + "  |  RFQ: " + rfq.getRfqNumber(), 4);
//                     row = addSectionHeader(s11, row, "MATCH SUMMARY", hdrNavy, 4);
//                     row = addLV(s11, row, labelStyle, valueStyle, "Match Status", match.getMatchStatus() != null ? match.getMatchStatus().name() : "—", "Match Version", String.valueOf(match.getMatchVersion()));
//                     row = addLV(s11, row, labelStyle, valueStyle, "Tolerance %", s(match.getTolerancePercentage()), "Variance Amount", s(match.getVarianceAmount()));
//                     row = addLV(s11, row, labelStyle, valueStyle, "Variance %", s(match.getVariancePercentage()), "Matched Lines", String.valueOf(match.getTotalMatchedLines()));
//                     row = addLV(s11, row, labelStyle, valueStyle, "Mismatch Lines", String.valueOf(match.getTotalMismatchLines()), "Approved Payment", s(match.getApprovedPaymentAmount()));
//                     row = addLV(s11, row, labelStyle, valueStyle, "PO Total Value", s(match.getPoTotalValue()), "GRN Accepted Value", s(match.getGrnAcceptedValue()));
//                     row = addLV(s11, row, labelStyle, valueStyle, "Invoice Total", s(match.getInvoiceTotalValue()), "Resolution", match.getResolution() != null ? match.getResolution().name() : "PENDING");
//                     row = addLV(s11, row, labelStyle, valueStyle, "Resolved By", s(match.getResolvedByName()), "Resolved At", fmt(match.getResolvedAt()));
//                     if (match.getResolutionRemarks() != null) row = addLV(s11, row, labelStyle, valueStyle, "Resolution Remarks", s(match.getResolutionRemarks()), "", "");
//                     if (match.getMatchSummary() != null) {
//                         row++; Row sumRow = s11.createRow(row++); s11.addMergedRegion(new CellRangeAddress(row - 1, row - 1, 0, 3));
//                         Cell sc = sumRow.createCell(0); sc.setCellValue(match.getMatchSummary()); sc.setCellStyle(isFullMatch ? lowestStyle : warnStyle);
//                     }
//                 }
//             }

//             // ── SHEET 12: Match Line Results ─────────────────────────────
//             if (!supplierMode && !matches.isEmpty()) {
//                 ThreeWayMatch match = matches.get(0); List<MatchLineResult> lineResults = match.getLineResults();
//                 XSSFSheet s12 = wb.createSheet("Match Line Results");
//                 setWidths(s12, new int[]{1200, 3000, 6500, 2000, 2500, 2500, 2500, 3000, 3000, 3000, 3000, 3000, 3500, 6000});
//                 row = 0;
//                 row = addBanner(s12, row, "3-WAY MATCH — LINE RESULTS", titleStyle, "Invoice: " + s(match.getInvoiceNumber()) + " ↔ GRN: " + s(match.getGrnNumber()) + "  |  " + lineResults.size() + " lines", 14);
//                 String[] mCols = {"#", "Item Code", "Description", "UOM", "PO Qty", "GRN Accepted", "Inv Qty", "Qty Var%", "PO Rate", "Inv Price", "Price Var%", "GRN Value", "Inv Value", "Status"};
//                 addHeaderRow(s12, row++, hdrNavy, mCols); sl = 1;
//                 for (MatchLineResult lr : lineResults) {
//                     Row r = s12.createRow(row++);
//                     boolean lineOk = lr.getLineMatchStatus() == MatchLineResult.LineMatchStatus.MATCHED;
//                     boolean lineFail = lr.getLineMatchStatus() == MatchLineResult.LineMatchStatus.NOT_IN_PO || lr.getLineMatchStatus() == MatchLineResult.LineMatchStatus.NOT_RECEIVED;
//                     CellStyle cs = lineFail ? warnStyle : lineOk ? lowestStyle : alt(sl, valueStyle, altStyle);
//                     CellStyle ams = lineFail ? warnStyle : lineOk ? lowestStyle : amtStyle;
//                     str(r, 0, String.valueOf(sl++), cs); str(r, 1, s(lr.getItemCode()), cs); str(r, 2, s(lr.getItemDescription()), cs);
//                     str(r, 3, s(lr.getUom()), cs); num(r, 4, lr.getPoOrderedQuantity(), ams); num(r, 5, lr.getGrnAcceptedQuantity(), ams);
//                     num(r, 6, lr.getInvoiceQuantity(), ams); num(r, 7, lr.getQuantityVariancePct(), ams); num(r, 8, lr.getPoUnitRate(), ams);
//                     num(r, 9, lr.getInvoiceUnitPrice(), ams); num(r, 10, lr.getPriceVariancePct(), ams); num(r, 11, lr.getGrnAcceptedValue(), ams);
//                     num(r, 12, lr.getInvoiceLineValue(), ams); str(r, 13, lr.getLineMatchStatus() != null ? lr.getLineMatchStatus().name() : "—", statusStyle);
//                 }
//             }

//             // ── SHEET 13: HIERARCHY APPROVAL VIEW ───────────────────────
//             // Added ONLY when hierarchy user downloads (userId + levelOrder present)
//             if (addHierarchySheet) {
//                 addHierarchyApprovalViewSheet(
//                         wb, rfq, items, approvals,
//                         userId, levelOrder,
//                         levelName, viewerName, viewerEmail,
//                         titleStyle, hdrNavy, hdrGreen, hdrOrange, hdrPurple,
//                         labelStyle, valueStyle, amtStyle, altStyle,
//                         statusStyle, lowestStyle, totalStyle, warnStyle);
//             }

//             wb.write(out);
//             return out.toByteArray();
//         }
//     }

//     // =========================================================================
//     //  SHEET 13: HIERARCHY APPROVAL VIEW
//     //
//     //  All viewer info (levelOrder, levelName, viewerName, viewerEmail) comes
//     //  as plain params — NO User model methods called here at all.
//     // =========================================================================
//     private void addHierarchyApprovalViewSheet(
//             XSSFWorkbook wb,
//             RFQ rfq,
//             List<RFQItem> items,
//             List<DynamicRFQApproval> allApprovals,
//             Long userId,
//             int userLevelOrder,
//             String levelName,
//             String viewerName,
//             String viewerEmail,
//             CellStyle titleStyle,
//             CellStyle hdrNavy, CellStyle hdrGreen, CellStyle hdrOrange, CellStyle hdrPurple,
//             CellStyle labelStyle, CellStyle valueStyle, CellStyle amtStyle,
//             CellStyle altStyle, CellStyle statusStyle,
//             CellStyle lowestStyle, CellStyle totalStyle, CellStyle warnStyle) {

//         // Resolve display values safely
//         String resolvedLevelName  = (levelName  != null && !levelName.isBlank())  ? levelName  : "Hierarchy Level";
//         String resolvedViewerName = (viewerName != null && !viewerName.isBlank()) ? viewerName : "Hierarchy User";
//         String resolvedViewerEmail = (viewerEmail != null && !viewerEmail.isBlank()) ? viewerEmail : "N/A";

//         // Extra styles for this sheet only
//         CellStyle hdrBlue2   = makeHeaderStyle(wb, rgb(0x18, 0x5f, 0xa5));
//         CellStyle hdrAmber   = makeHeaderStyle(wb, rgb(0xba, 0x75, 0x17));
//         CellStyle hdrTeal    = makeHeaderStyle(wb, rgb(0x00, 0x6d, 0x77));
//         CellStyle creatorBg  = makeFilledStyle(wb, rgb(0xe6, 0xf1, 0xfb), rgb(0x0c, 0x44, 0x7c), false);
//         CellStyle prevBg     = makeFilledStyle(wb, rgb(0xfa, 0xee, 0xda), rgb(0x63, 0x38, 0x06), false);
//         CellStyle youBg      = makeFilledStyle(wb, rgb(0xe8, 0xf0, 0xfe), rgb(0x1e, 0x40, 0x88), true);
//         CellStyle nextBg     = makeFilledStyle(wb, rgb(0xf3, 0xe5, 0xf5), rgb(0x4a, 0x00, 0x72), false);
//         CellStyle approvedBg = makeFilledStyle(wb, rgb(0xd4, 0xed, 0xda), rgb(0x1a, 0x5c, 0x1a), false);
//         CellStyle rejectedBg = makeFilledStyle(wb, rgb(0xf8, 0xd7, 0xda), rgb(0x72, 0x1c, 0x24), false);
//         CellStyle holdBg     = makeFilledStyle(wb, rgb(0xff, 0xf3, 0xcd), rgb(0x85, 0x64, 0x04), false);
//         CellStyle pendingBg  = makeFilledStyle(wb, rgb(0xe2, 0xe3, 0xe5), rgb(0x38, 0x3d, 0x41), false);
//         CellStyle noteBg     = makeFilledStyle(wb, rgb(0xff, 0xf9, 0xc4), rgb(0x44, 0x44, 0x44), false);
//         CellStyle finalBg    = makeFilledStyle(wb, rgb(0xd4, 0xed, 0xda), rgb(0x1a, 0x5c, 0x1a), true);

//         XSSFSheet sheet = wb.createSheet("Hierarchy Approval View");
//         sheet.setColumnWidth(0, 4000); sheet.setColumnWidth(1, 7000); sheet.setColumnWidth(2, 4500);
//         sheet.setColumnWidth(3, 7000); sheet.setColumnWidth(4, 3500); sheet.setColumnWidth(5, 4000); sheet.setColumnWidth(6, 9000);

//         int row = 0;

//         // Banner
//         row = addBanner(sheet, row, "HIERARCHY APPROVAL VIEW", titleStyle,
//                 "RFQ: " + rfq.getRfqNumber() + "  ·  " + s(rfq.getRfqTitle())
//                 + "  |  Viewed by: " + resolvedViewerName
//                 + "  (" + resolvedLevelName + " · Level " + userLevelOrder + ")"
//                 + "  |  Generated: " + LocalDateTime.now().format(DTTM_FMT), 7);

//         // Note bar
//         Row noteRow = sheet.createRow(row++);
//         sheet.addMergedRegion(new CellRangeAddress(row - 1, row - 1, 0, 6));
//         Cell noteCell = noteRow.createCell(0);
//         noteCell.setCellValue("  NOTE: This sheet shows only approval levels up to your level (" + resolvedLevelName + " · Level " + userLevelOrder + "). Higher authority levels are not visible in this view.");
//         noteCell.setCellStyle(noteBg);
//         row++;

//         // Sort approvals by levelOrder ascending
//         List<DynamicRFQApproval> sorted = allApprovals.stream()
//                 .filter(a -> a.getHierarchyLevel() != null)
//                 .sorted(Comparator.comparingInt(a -> a.getHierarchyLevel().getLevelOrder()))
//                 .collect(Collectors.toList());

//         // Visible chain: levels <= user's levelOrder
//         List<DynamicRFQApproval> visibleChain = sorted.stream()
//                 .filter(a -> a.getHierarchyLevel().getLevelOrder() <= userLevelOrder)
//                 .collect(Collectors.toList());

//         // Previous approver: highest level strictly BELOW user
//         DynamicRFQApproval previousApprover = visibleChain.stream()
//                 .filter(a -> a.getHierarchyLevel().getLevelOrder() < userLevelOrder)
//                 .max(Comparator.comparingInt(a -> a.getHierarchyLevel().getLevelOrder()))
//                 .orElse(null);

//         // User's own slot (if they have already actioned)
//         DynamicRFQApproval mySlot = sorted.stream()
//                 .filter(a -> a.getHierarchyLevel().getLevelOrder() == userLevelOrder)
//                 .findFirst().orElse(null);

//         // Next approver: lowest level strictly ABOVE user
//         DynamicRFQApproval nextApprover = sorted.stream()
//                 .filter(a -> a.getHierarchyLevel().getLevelOrder() > userLevelOrder)
//                 .min(Comparator.comparingInt(a -> a.getHierarchyLevel().getLevelOrder()))
//                 .orElse(null);

//         // ── SECTION A: RFQ OVERVIEW ─────────────────────────────────────
//         row = addSectionHeader(sheet, row, "SECTION A  ·  RFQ OVERVIEW", hdrNavy, 7);
//         row = addLV7(sheet, row, labelStyle, creatorBg, "RFQ Number", rfq.getRfqNumber(), "RFQ Title", s(rfq.getRfqTitle()));
//         row = addLV7(sheet, row, labelStyle, creatorBg, "Status", en(rfq.getStatus()), "Priority", en(rfq.getPriority()));
//         row = addLV7(sheet, row, labelStyle, creatorBg, "Issue Date", fmt(rfq.getIssueDate()), "Due Date", fmt(rfq.getDueDate()));
//         row = addLV7(sheet, row, labelStyle, creatorBg, "Payment Terms", s(rfq.getPaymentTerms()), "Delivery Terms", s(rfq.getDeliveryTerms()));
//         row = addLV7(sheet, row, labelStyle, creatorBg, "Suppliers Invited", String.valueOf(rfq.getSelectedSuppliers() != null ? rfq.getSelectedSuppliers().size() : 0), "Items Count", String.valueOf(items.size()));
//         if (rfq.getJustification() != null && !rfq.getJustification().isBlank()) {
//             Row jr = sheet.createRow(row++);
//             Cell jl = jr.createCell(0); jl.setCellValue("Justification"); jl.setCellStyle(labelStyle);
//             Cell jv = jr.createCell(1); jv.setCellValue(s(rfq.getJustification())); jv.setCellStyle(creatorBg);
//             sheet.addMergedRegion(new CellRangeAddress(row - 1, row - 1, 1, 6));
//         }
//         row++;

//         // ── SECTION B: RFQ CREATOR DETAILS ─────────────────────────────
//         row = addSectionHeader(sheet, row, "SECTION B  ·  RFQ CREATOR DETAILS", hdrBlue2, 7);
//         if (rfq.getCreatedByUser() != null) {
//             User creator = rfq.getCreatedByUser();
//             String creatorName = ((creator.getFirstName() != null ? creator.getFirstName() : "") + " " + (creator.getLastName() != null ? creator.getLastName() : "")).trim();
//             row = addLV7(sheet, row, labelStyle, creatorBg, "Creator Name", creatorName.isEmpty() ? "N/A" : creatorName, "Email", s(creator.getEmail()));
//             row = addLV7(sheet, row, labelStyle, creatorBg, "Phone", s(creator.getPhone()), "Company", rfq.getBuyer() != null ? rfq.getBuyer().getCompanyName() : "N/A");
//             row = addLV7(sheet, row, labelStyle, creatorBg, "Created On", fmt(rfq.getCreatedAt()), "Location", rfq.getLocation() != null ? rfq.getLocation().getLocationName() : "N/A");
//         } else {
//             Row noCreatorRow = sheet.createRow(row++);
//             sheet.addMergedRegion(new CellRangeAddress(row - 1, row - 1, 0, 6));
//             Cell nc = noCreatorRow.createCell(0); nc.setCellValue("Creator information not available."); nc.setCellStyle(noteBg);
//         }
//         row++;

//         // ── SECTION C: APPROVAL CHAIN (levels <= user's level) ──────────
//         row = addSectionHeader(sheet, row, "SECTION C  ·  APPROVAL CHAIN  (Levels visible to you: up to Level " + userLevelOrder + " — " + resolvedLevelName + ")", hdrNavy, 7);
//         if (visibleChain.isEmpty()) {
//             Row emptyRow = sheet.createRow(row++);
//             sheet.addMergedRegion(new CellRangeAddress(row - 1, row - 1, 0, 6));
//             Cell ec = emptyRow.createCell(0); ec.setCellValue("No approval actions recorded yet for levels visible to you."); ec.setCellStyle(noteBg);
//         } else {
//             Row chainHdr = sheet.createRow(row++); chainHdr.setHeightInPoints(20);
//             String[] chainCols = {"#", "Level Name", "Level Order", "Approver Name", "Email", "Status", "Action Date & Remarks"};
//             for (int c = 0; c < chainCols.length; c++) { Cell hc = chainHdr.createCell(c); hc.setCellValue(chainCols[c]); hc.setCellStyle(hdrNavy); }
//             sheet.createFreezePane(0, row);
//             int chainSl = 1;
//             for (DynamicRFQApproval ap : visibleChain) {
//                 Row r = sheet.createRow(row++); r.setHeightInPoints(24);
//                 CellStyle rowBg = pickStatusStyle(ap.getStatus(), approvedBg, rejectedBg, holdBg, pendingBg, chainSl, valueStyle, altStyle);
//                 str(r, 0, String.valueOf(chainSl++), rowBg);
//                 str(r, 1, ap.getHierarchyLevel() != null ? ap.getHierarchyLevel().getLevelName() : "N/A", rowBg);
//                 str(r, 2, ap.getHierarchyLevel() != null ? String.valueOf(ap.getHierarchyLevel().getLevelOrder()) : "N/A", rowBg);
//                 str(r, 3, ap.getApproverUser() != null ? ap.getApproverUser().getFullName() : "N/A", rowBg);
//                 str(r, 4, ap.getApproverUser() != null ? s(ap.getApproverUser().getEmail()) : "N/A", rowBg);
//                 str(r, 5, ap.getStatus() != null ? ap.getStatus().name() : "PENDING", statusStyle);
//                 String dateAndRemarks = fmt(ap.getActionDate() != null ? ap.getActionDate() : ap.getHoldDate());
//                 String remarks = buildApprovalRemarks(ap);
//                 if (!remarks.isEmpty()) dateAndRemarks += "  |  " + remarks;
//                 str(r, 6, dateAndRemarks, rowBg);
//             }
//         }
//         row++;

//         // ── SECTION D: PREVIOUS APPROVER ────────────────────────────────
//         row = addSectionHeader(sheet, row, "SECTION D  ·  PREVIOUS APPROVER", hdrAmber, 7);
//         if (previousApprover == null) {
//             Row noRow = sheet.createRow(row++);
//             sheet.addMergedRegion(new CellRangeAddress(row - 1, row - 1, 0, 6));
//             Cell nc = noRow.createCell(0); nc.setCellValue("No previous approver — you are the first approver in this workflow."); nc.setCellStyle(noteBg);
//         } else {
//             row = addLV7(sheet, row, labelStyle, prevBg, "Approver Name", previousApprover.getApproverUser() != null ? previousApprover.getApproverUser().getFullName() : "N/A", "Email", previousApprover.getApproverUser() != null ? s(previousApprover.getApproverUser().getEmail()) : "N/A");
//             row = addLV7(sheet, row, labelStyle, prevBg, "Hierarchy Level", previousApprover.getHierarchyLevel() != null ? previousApprover.getHierarchyLevel().getLevelName() : "N/A", "Level Order", previousApprover.getHierarchyLevel() != null ? String.valueOf(previousApprover.getHierarchyLevel().getLevelOrder()) : "N/A");
//             row = addLV7(sheet, row, labelStyle, prevBg, "Decision", previousApprover.getStatus() != null ? previousApprover.getStatus().name() : "PENDING", "Action Date", fmt(previousApprover.getActionDate() != null ? previousApprover.getActionDate() : previousApprover.getHoldDate()));
//             String prevRemarks = buildApprovalRemarks(previousApprover);
//             if (!prevRemarks.isEmpty()) {
//                 Row remRow = sheet.createRow(row++);
//                 Cell rl = remRow.createCell(0); rl.setCellValue("Remarks"); rl.setCellStyle(labelStyle);
//                 Cell rv = remRow.createCell(1); rv.setCellValue(prevRemarks); rv.setCellStyle(prevBg);
//                 sheet.addMergedRegion(new CellRangeAddress(row - 1, row - 1, 1, 6));
//             }
//         }
//         row++;

//         // ── SECTION E: YOUR POSITION ─────────────────────────────────────
//         row = addSectionHeader(sheet, row, "SECTION E  ·  YOUR POSITION IN THIS APPROVAL", hdrTeal, 7);
//         row = addLV7(sheet, row, labelStyle, youBg, "Your Name", resolvedViewerName, "Email", resolvedViewerEmail);
//         row = addLV7(sheet, row, labelStyle, youBg, "Hierarchy Level", resolvedLevelName, "Level Order", String.valueOf(userLevelOrder));
//         if (mySlot != null) {
//             row = addLV7(sheet, row, labelStyle, youBg, "Your Decision", mySlot.getStatus() != null ? mySlot.getStatus().name() : "PENDING", "Action Date", fmt(mySlot.getActionDate() != null ? mySlot.getActionDate() : mySlot.getHoldDate()));
//             String myRemarks = buildApprovalRemarks(mySlot);
//             if (!myRemarks.isEmpty()) {
//                 Row myRemRow = sheet.createRow(row++);
//                 Cell rl = myRemRow.createCell(0); rl.setCellValue("Your Remarks"); rl.setCellStyle(labelStyle);
//                 Cell rv = myRemRow.createCell(1); rv.setCellValue(myRemarks); rv.setCellStyle(youBg);
//                 sheet.addMergedRegion(new CellRangeAddress(row - 1, row - 1, 1, 6));
//             }
//         } else {
//             Row pendRow = sheet.createRow(row++);
//             sheet.addMergedRegion(new CellRangeAddress(row - 1, row - 1, 0, 6));
//             Cell pc = pendRow.createCell(0); pc.setCellValue("  ACTION PENDING  —  This RFQ is awaiting your review and decision."); pc.setCellStyle(noteBg);
//         }
//         row++;

//         // ── SECTION F: NEXT APPROVER ─────────────────────────────────────
//         row = addSectionHeader(sheet, row, "SECTION F  ·  NEXT APPROVER", hdrPurple, 7);
//         if (nextApprover == null) {
//             Row finalRow = sheet.createRow(row++);
//             sheet.addMergedRegion(new CellRangeAddress(row - 1, row - 1, 0, 6));
//             Cell fc = finalRow.createCell(0); fc.setCellValue("  FINAL APPROVER  —  You are the last approver in this chain. No higher levels exist."); fc.setCellStyle(finalBg);
//         } else {
//             row = addLV7(sheet, row, labelStyle, nextBg, "Next Approver", nextApprover.getApproverUser() != null ? nextApprover.getApproverUser().getFullName() : "N/A", "Email", nextApprover.getApproverUser() != null ? s(nextApprover.getApproverUser().getEmail()) : "N/A");
//             row = addLV7(sheet, row, labelStyle, nextBg, "Hierarchy Level", nextApprover.getHierarchyLevel() != null ? nextApprover.getHierarchyLevel().getLevelName() : "N/A", "Level Order", nextApprover.getHierarchyLevel() != null ? String.valueOf(nextApprover.getHierarchyLevel().getLevelOrder()) : "N/A");
//             row = addLV7(sheet, row, labelStyle, nextBg, "Current Status", nextApprover.getStatus() != null ? nextApprover.getStatus().name() : "AWAITING_YOUR_APPROVAL", "Note", "Acts after your approval is given");
//         }
//         row++;

//         // ── SECTION G: RFQ LINE ITEMS ────────────────────────────────────
//         row = addSectionHeader(sheet, row, "SECTION G  ·  RFQ LINE ITEMS  [" + items.size() + " item" + (items.size() == 1 ? "" : "s") + "]", hdrGreen, 7);
//         if (items.isEmpty()) {
//             Row emptyRow2 = sheet.createRow(row++);
//             sheet.addMergedRegion(new CellRangeAddress(row - 1, row - 1, 0, 6));
//             Cell ec2 = emptyRow2.createCell(0); ec2.setCellValue("No line items found for this RFQ."); ec2.setCellStyle(noteBg);
//         } else {
//             String[] itemCols = {"#", "Item Code", "Description", "Specifications", "Qty", "UOM", "Unit Price  |  Required Date"};
//             Row itemHdr = sheet.createRow(row++); itemHdr.setHeightInPoints(18);
//             for (int c = 0; c < itemCols.length; c++) { Cell hc = itemHdr.createCell(c); hc.setCellValue(itemCols[c]); hc.setCellStyle(hdrGreen); }
//             int itemSl = 1;
//             for (RFQItem item : items) {
//                 Row r = sheet.createRow(row++); CellStyle cs = alt(itemSl, valueStyle, altStyle);
//                 str(r, 0, String.valueOf(itemSl++), cs); str(r, 1, s(item.getItemCode()), cs);
//                 str(r, 2, s(item.getItemDescription()), cs); str(r, 3, s(item.getSpecifications()), cs);
//                 num(r, 4, item.getQuantity(), amtStyle); str(r, 5, s(item.getUom()), cs);
//                 StringBuilder combined = new StringBuilder();
//                 if (item.getUnitPrice() != null && item.getUnitPrice().compareTo(BigDecimal.ZERO) > 0) combined.append("Rate: ").append(item.getUnitPrice().toPlainString());
//                 if (item.getItemRequiredDate() != null) { if (combined.length() > 0) combined.append("  |  "); combined.append("Req: ").append(fmt(item.getItemRequiredDate())); }
//                 str(r, 6, combined.length() > 0 ? combined.toString() : "—", cs);
//             }
//         }
//         sheet.createFreezePane(0, 4);
//     }

//     // =========================================================================
//     //  RFQ LIST REPORT
//     // =========================================================================
//     @Transactional(readOnly = true)
//     public byte[] generateRFQListExcel(Long buyerId, String statusFilter) throws Exception {
//         List<RFQ> rfqs = rfqRepository.findByBuyerId(buyerId);
//         if (statusFilter != null && !statusFilter.isBlank() && !statusFilter.equalsIgnoreCase("ALL")) {
//             final String sf = statusFilter.trim().toUpperCase();
//             rfqs = rfqs.stream().filter(r -> sf.equals(r.getStatus() != null ? r.getStatus().name() : "")).collect(Collectors.toList());
//         }
//         try (XSSFWorkbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
//             CellStyle titleStyle = makeTitleStyle(wb); CellStyle hdrNavy = makeHeaderStyle(wb, rgb(0x1e, 0x40, 0x88));
//             CellStyle valueStyle = makeValueStyle(wb); CellStyle altStyle = makeAltRowStyle(wb);
//             CellStyle amtStyle = makeAmountStyle(wb); CellStyle statusStyle = makeStatusStyle(wb);
//             CellStyle statLbl = makeStatLabelStyle(wb); CellStyle statVal = makeStatValueStyle(wb);
//             XSSFSheet sheet = wb.createSheet("RFQ List");
//             setWidths(sheet, new int[]{1200, 3500, 7000, 3500, 3000, 3000, 3000, 2500, 2500, 3500, 5000});
//             int row = 0;
//             String filterDesc = (statusFilter != null && !statusFilter.isBlank() && !statusFilter.equalsIgnoreCase("ALL")) ? "  |  Status: " + statusFilter : "  |  All Statuses";
//             row = addBanner(sheet, row, "RFQ LIST REPORT", titleStyle, "Generated: " + LocalDateTime.now().format(DTTM_FMT) + filterDesc, 11);
//             long cntDraft = rfqs.stream().filter(r -> "DRAFT".equals(en(r.getStatus()))).count();
//             long cntAwaiting = rfqs.stream().filter(r -> "AWAITING_APPROVAL".equals(en(r.getStatus()))).count();
//             long cntPublished = rfqs.stream().filter(r -> "PUBLISHED".equals(en(r.getStatus()))).count();
//             long cntClosed = rfqs.stream().filter(r -> "CLOSED".equals(en(r.getStatus()))).count();
//             long cntHold = rfqs.stream().filter(r -> "HOLD".equals(en(r.getStatus()))).count();
//             Row sr = sheet.createRow(row++); sr.setHeightInPoints(22);
//             str(sr, 0, "Total RFQs: " + rfqs.size(), statLbl); str(sr, 2, "Draft: " + cntDraft, statVal);
//             str(sr, 4, "Awaiting: " + cntAwaiting, statVal); str(sr, 6, "Published: " + cntPublished, statVal);
//             str(sr, 8, "Closed: " + cntClosed, statVal); str(sr, 10, "On Hold: " + cntHold, statVal);
//             row++;
//             String[] cols = {"#", "RFQ Number", "Title", "Status", "Priority", "Issue Date", "Due Date", "Suppliers", "Items", "Approval Status", "Created By"};
//             addHeaderRow(sheet, row++, hdrNavy, cols);
//             sheet.setAutoFilter(new CellRangeAddress(row - 1, row - 1, 0, cols.length - 1));
//             int sl = 1;
//             for (RFQ rfq : rfqs) {
//                 Row r = sheet.createRow(row++); CellStyle cs = alt(sl, valueStyle, altStyle);
//                 str(r, 0, String.valueOf(sl++), cs); str(r, 1, s(rfq.getRfqNumber()), cs); str(r, 2, s(rfq.getRfqTitle()), cs);
//                 str(r, 3, en(rfq.getStatus()), statusStyle); str(r, 4, en(rfq.getPriority()), cs);
//                 str(r, 5, fmt(rfq.getIssueDate()), cs); str(r, 6, fmt(rfq.getDueDate()), cs);
//                 int supCnt = 0; try { supCnt = rfq.getSelectedSuppliers() != null ? rfq.getSelectedSuppliers().size() : 0; } catch (Exception ignored) {}
//                 int itmCnt = 0; try { itmCnt = rfq.getItems() != null ? rfq.getItems().size() : 0; } catch (Exception ignored) {}
//                 str(r, 7, String.valueOf(supCnt), cs); str(r, 8, String.valueOf(itmCnt), cs);
//                 str(r, 9, en(rfq.getApprovalStatus()), statusStyle);
//                 str(r, 10, rfq.getCreatedByUser() != null ? rfq.getCreatedByUser().getFirstName() + " " + rfq.getCreatedByUser().getLastName() : "N/A", cs);
//             }
//             wb.write(out);
//             return out.toByteArray();
//         }
//     }

//     // =========================================================================
//     //  QUOTE COMPARISON EXCEL
//     // =========================================================================
//     @Transactional(readOnly = true)
//     public byte[] generateQuoteComparisonExcel(Long rfqId) throws Exception {
//         RFQ rfq = rfqRepository.findByIdWithAllDetails(rfqId).orElseThrow(() -> new RuntimeException("RFQ not found: " + rfqId));
//         List<RFQItem> items = rfqItemRepository.findByRfqId(rfqId);
//         List<RFQSupplier> suppliers = rfqSupplierRepository.findByRFQAndStatus(rfqId, "RESPONDED");
//         List<SupplierQuoteItem> quotes = quoteItemRepository.findByRfqId(rfqId);
//         String currency = rfq.getLocation() != null ? rfq.getLocation().getCurrencyCode() : "INR";

//         try (XSSFWorkbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
//             CellStyle titleStyle = makeTitleStyle(wb); CellStyle hdrNavy = makeHeaderStyle(wb, rgb(0x1e, 0x40, 0x88));
//             CellStyle hdrGreen = makeHeaderStyle(wb, rgb(0x1b, 0x5e, 0x20)); CellStyle valueStyle = makeValueStyle(wb);
//             CellStyle altStyle = makeAltRowStyle(wb); CellStyle amtStyle = makeAmountStyle(wb);
//             CellStyle lowestStyle = makeLowestStyle(wb); CellStyle totalStyle = makeTotalStyle(wb);
//             CellStyle metaStyle = makeMetaStyle(wb);
//             XSSFSheet sheet = wb.createSheet("Quote Comparison");
//             int numSup = suppliers.size(); int fixedCols = 6; int supCols = 7; int totalCols = fixedCols + numSup * supCols;
//             sheet.setColumnWidth(0, 1200); sheet.setColumnWidth(1, 3500); sheet.setColumnWidth(2, 7000);
//             sheet.setColumnWidth(3, 6000); sheet.setColumnWidth(4, 2500); sheet.setColumnWidth(5, 2500);
//             for (int s = 0; s < numSup; s++) {
//                 int b = fixedCols + s * supCols;
//                 sheet.setColumnWidth(b, 3000); sheet.setColumnWidth(b + 1, 4500); sheet.setColumnWidth(b + 2, 2500);
//                 sheet.setColumnWidth(b + 3, 3000); sheet.setColumnWidth(b + 4, 3000); sheet.setColumnWidth(b + 5, 4000); sheet.setColumnWidth(b + 6, 5000);
//             }
//             int row = 0;
//             row = addBanner(sheet, row, "QUOTE COMPARISON MATRIX", titleStyle, rfq.getRfqNumber() + "  —  " + rfq.getRfqTitle() + "  |  Currency: " + currency + "  |  Suppliers: " + numSup + "  |  Generated: " + LocalDateTime.now().format(DTTM_FMT), totalCols);
//             Row meta = sheet.createRow(row++);
//             str(meta, 0, "Buyer: " + (rfq.getBuyer() != null ? rfq.getBuyer().getCompanyName() : "N/A"), metaStyle);
//             str(meta, 3, "Due Date: " + fmt(rfq.getDueDate()), metaStyle); str(meta, 6, "Items: " + items.size(), metaStyle);
//             row++;
//             Row supHdrRow = sheet.createRow(row++);
//             str(supHdrRow, 0, "Sl No", hdrNavy); str(supHdrRow, 1, "Item Code", hdrNavy);
//             str(supHdrRow, 2, "Item Description", hdrNavy); str(supHdrRow, 3, "Specifications", hdrNavy);
//             str(supHdrRow, 4, "Qty", hdrNavy); str(supHdrRow, 5, "UOM", hdrNavy);
//             for (int s = 0; s < numSup; s++) {
//                 RFQSupplier rs = suppliers.get(s);
//                 String sName = rs.getSupplier() != null ? rs.getSupplier().getCompanyName() : "Supplier " + (s + 1);
//                 String sCurr = rs.getSupplier() != null ? determineSupplierCurrency(rfq, rs.getSupplier()) : currency;
//                 int b = fixedCols + s * supCols;
//                 Cell sc = supHdrRow.createCell(b); sc.setCellValue(sName + "  [" + sCurr + "]"); sc.setCellStyle(hdrGreen);
//                 sheet.addMergedRegion(new CellRangeAddress(row - 1, row - 1, b, b + supCols - 1));
//             }
//             Row subRow = sheet.createRow(row++);
//             for (int c = 0; c < fixedCols; c++) subRow.createCell(c).setCellStyle(hdrNavy);
//             for (int s = 0; s < numSup; s++) {
//                 int b = fixedCols + s * supCols;
//                 String[] sub = {"Unit Rate", "Grand Total", "Tax %", "Delivery (d)", "Warranty (m)", "Brand/Make/Model", "Remarks"};
//                 for (int c = 0; c < sub.length; c++) { Cell cell = subRow.createCell(b + c); cell.setCellValue(sub[c]); cell.setCellStyle(hdrNavy); }
//             }
//             int sl = 1;
//             for (RFQItem item : items) {
//                 double lowestRate = Double.MAX_VALUE; Long lowestSupId = null;
//                 for (RFQSupplier rs : suppliers) {
//                     if (rs.getSupplier() == null) continue;
//                     Optional<SupplierQuoteItem> qOpt = findQuote(quotes, item.getId(), rs.getSupplier().getId());
//                     if (qOpt.isPresent() && qOpt.get().getUnitRate() != null && qOpt.get().getUnitRate().doubleValue() < lowestRate) { lowestRate = qOpt.get().getUnitRate().doubleValue(); lowestSupId = rs.getSupplier().getId(); }
//                 }
//                 final Long lowestId = lowestSupId;
//                 Row r = sheet.createRow(row++); CellStyle cs = alt(sl, valueStyle, altStyle);
//                 str(r, 0, String.valueOf(sl++), cs); str(r, 1, s(item.getItemCode()), cs);
//                 str(r, 2, s(item.getItemDescription()), cs); str(r, 3, s(item.getSpecifications()), cs);
//                 num(r, 4, item.getQuantity(), amtStyle); str(r, 5, s(item.getUom()), cs);
//                 for (int idx = 0; idx < numSup; idx++) {
//                     RFQSupplier rs = suppliers.get(idx); if (rs.getSupplier() == null) continue;
//                     Long sid = rs.getSupplier().getId(); int b = fixedCols + idx * supCols;
//                     boolean isLowest = sid.equals(lowestId); CellStyle qs = isLowest ? lowestStyle : amtStyle; CellStyle ts = isLowest ? lowestStyle : valueStyle;
//                     Optional<SupplierQuoteItem> qOpt = findQuote(quotes, item.getId(), sid);
//                     if (qOpt.isPresent()) {
//                         SupplierQuoteItem qi = qOpt.get();
//                         num(r, b, qi.getUnitRate(), qs); num(r, b + 1, qi.getGrandTotal(), qs); num(r, b + 2, qi.getTaxPercentage(), ts);
//                         str(r, b + 3, qi.getDeliveryDays() != null ? qi.getDeliveryDays() + "d" : "—", ts);
//                         str(r, b + 4, qi.getWarrantyMonths() != null ? qi.getWarrantyMonths() + "m" : "—", ts);
//                         str(r, b + 5, joinNonNull("/", qi.getBrandOffered(), qi.getMakeModel()), ts); str(r, b + 6, s(qi.getRemarks()), ts);
//                     } else { for (int c = 0; c < supCols; c++) { Cell nc = r.createCell(b + c); nc.setCellValue("—"); nc.setCellStyle(valueStyle); } }
//                 }
//             }
//             Row totRow = sheet.createRow(row++);
//             Cell totLbl = totRow.createCell(0); totLbl.setCellValue("GRAND TOTAL"); totLbl.setCellStyle(totalStyle);
//             sheet.addMergedRegion(new CellRangeAddress(row - 1, row - 1, 0, fixedCols - 1));
//             for (int c = 1; c < fixedCols; c++) totRow.createCell(c).setCellStyle(totalStyle);
//             Long lowestTotalSid = null; double lowestTotalAmt = Double.MAX_VALUE;
//             for (RFQSupplier rs : suppliers) { if (rs.getSupplier() == null) continue; double tot = sumGrandTotal(quotes, rs.getSupplier().getId()); if (tot > 0 && tot < lowestTotalAmt) { lowestTotalAmt = tot; lowestTotalSid = rs.getSupplier().getId(); } }
//             final Long lowestTotId = lowestTotalSid;
//             for (int idx = 0; idx < numSup; idx++) {
//                 RFQSupplier rs = suppliers.get(idx); if (rs.getSupplier() == null) continue;
//                 Long sid = rs.getSupplier().getId(); int b = fixedCols + idx * supCols;
//                 boolean isLowest = sid.equals(lowestTotId); CellStyle fs = isLowest ? lowestStyle : totalStyle;
//                 double total = sumGrandTotal(quotes, sid);
//                 for (int c = 0; c < supCols; c++) { Cell nc = totRow.createCell(b + c); if (c == 1) nc.setCellValue(total); else if (c == 0) nc.setCellValue(isLowest ? "★ LOWEST" : ""); nc.setCellStyle(fs); }
//             }
//             sheet.setAutoFilter(new CellRangeAddress(5, row - 2, 0, totalCols - 1));
//             wb.write(out);
//             return out.toByteArray();
//         }
//     }

//     // =========================================================================
//     //  INVOICE EXCEL
//     // =========================================================================
//     @Transactional(readOnly = true)
//     public byte[] generateInvoiceExcel(Long invoiceId) throws Exception {
//         Invoice invoice = invoiceRepository.findById(invoiceId).orElseThrow(() -> new RuntimeException("Invoice not found: " + invoiceId));
//         List<InvoiceLineItem> lineItems = invoice.getLineItems();
//         try (XSSFWorkbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
//             CellStyle titleStyle = makeTitleStyle(wb); CellStyle hdrNavy = makeHeaderStyle(wb, rgb(0x1e, 0x40, 0x88));
//             CellStyle hdrGreen = makeHeaderStyle(wb, rgb(0x1b, 0x5e, 0x20)); CellStyle labelStyle = makeLabelStyle(wb);
//             CellStyle valueStyle = makeValueStyle(wb); CellStyle amtStyle = makeAmountStyle(wb);
//             CellStyle altStyle = makeAltRowStyle(wb); CellStyle totalStyle = makeTotalStyle(wb);
//             XSSFSheet sheet = wb.createSheet("Invoice");
//             setWidths(sheet, new int[]{6000, 9000, 6000, 9000});
//             int row = 0;
//             row = addBanner(sheet, row, "INVOICE", titleStyle, s(invoice.getInvoiceNumber()) + "  |  Generated: " + LocalDateTime.now().format(DTTM_FMT), 4);
//             row = addSectionHeader(sheet, row, "INVOICE DETAILS", hdrNavy, 4);
//             row = addLV(sheet, row, labelStyle, valueStyle, "Invoice Number", invoice.getInvoiceNumber(), "Status", invoice.getStatus() != null ? invoice.getStatus().name() : "—");
//             row = addLV(sheet, row, labelStyle, valueStyle, "Invoice Date", fmt(invoice.getInvoiceDate()), "Due Date", fmt(invoice.getDueDate()));
//             row = addLV(sheet, row, labelStyle, valueStyle, "PO Number", s(invoice.getPoNumber()), "RFQ Number", s(invoice.getRfqNumber()));
//             row = addLV(sheet, row, labelStyle, valueStyle, "Currency", s(invoice.getCurrency()), "Payment Terms", s(invoice.getPaymentTerms()));
//             row++;
//             row = addSectionHeader(sheet, row, "SUPPLIER (FROM)", hdrNavy, 4);
//             row = addLV(sheet, row, labelStyle, valueStyle, "Supplier Name", s(invoice.getSupplierName()), "Email", s(invoice.getSupplierEmail()));
//             row++;
//             row = addSectionHeader(sheet, row, "BUYER (BILL TO)", hdrNavy, 4);
//             row = addLV(sheet, row, labelStyle, valueStyle, "Buyer Company", s(invoice.getBuyerCompanyName()), "RFQ Creator", s(invoice.getRfqCreatorName()));
//             row++;
//             setWidths(sheet, new int[]{1200, 3000, 7000, 2500, 2000, 2500, 4000, 2000, 2000, 4500});
//             row = addSectionHeader(sheet, row, "LINE ITEMS (" + lineItems.size() + ")", hdrGreen, 4);
//             String[] cols = {"#", "Item Code", "Description", "HSN/SAC", "UOM", "Quantity", "Unit Price", "Disc %", "Tax %", "Line Total"};
//             addHeaderRow(sheet, row++, hdrNavy, cols);
//             int sl = 1;
//             for (InvoiceLineItem li : lineItems) {
//                 Row r = sheet.createRow(row++); CellStyle cs = alt(sl, valueStyle, altStyle);
//                 str(r, 0, String.valueOf(sl++), cs); str(r, 1, s(li.getItemCode()), cs); str(r, 2, s(li.getItemDescription()), cs);
//                 str(r, 3, s(li.getHsnSacCode()), cs); str(r, 4, s(li.getUom()), cs); num(r, 5, li.getQuantity(), amtStyle);
//                 num(r, 6, li.getUnitPrice(), amtStyle); num(r, 7, li.getDiscountPercentage(), amtStyle);
//                 num(r, 8, li.getTaxPercentage(), amtStyle); num(r, 9, li.getLineTotal(), amtStyle);
//             }
//             row++;
//             Object[][] totals = {{"Subtotal (Pre-tax)", invoice.getSubtotal()}, {"Total Tax", invoice.getTaxAmount()}, {"Grand Total", invoice.getTotalAmount()}};
//             for (int t = 0; t < totals.length; t++) {
//                 Row tr = sheet.createRow(row++); boolean isGrand = (t == totals.length - 1);
//                 Cell lc = tr.createCell(8); lc.setCellValue((String) totals[t][0]); lc.setCellStyle(isGrand ? totalStyle : labelStyle);
//                 Cell vc = tr.createCell(9); if (totals[t][1] instanceof BigDecimal) vc.setCellValue(((BigDecimal) totals[t][1]).doubleValue()); else vc.setCellValue(0); vc.setCellStyle(isGrand ? totalStyle : amtStyle);
//             }
//             if (invoice.getBankName() != null) {
//                 row++;
//                 row = addSectionHeader(sheet, row, "BANK DETAILS", hdrNavy, 4);
//                 row = addLV(sheet, row, labelStyle, valueStyle, "Bank Name", s(invoice.getBankName()), "Account Name", s(invoice.getBankAccountName()));
//                 row = addLV(sheet, row, labelStyle, valueStyle, "Account Number", s(invoice.getBankAccountNumber()), "IFSC Code", s(invoice.getBankIfscCode()));
//                 addLV(sheet, row, labelStyle, valueStyle, "SWIFT Code", s(invoice.getBankSwiftCode()), "", "");
//             }
//             wb.write(out);
//             return out.toByteArray();
//         }
//     }

//     // =========================================================================
//     //  HTML generators — keep your existing implementations
//     // =========================================================================
//     @Transactional(readOnly = true)
//     public String generateRFQSummaryHTML(Long rfqId) {
//         throw new UnsupportedOperationException("Keep existing generateRFQSummaryHTML implementation");
//     }

//     @Transactional(readOnly = true)
//     public String generateQuoteComparisonHTML(Long rfqId) {
//         throw new UnsupportedOperationException("Keep existing generateQuoteComparisonHTML implementation");
//     }

//     // =========================================================================
//     //  PRIVATE: hierarchy sheet helpers
//     // =========================================================================

//     private XSSFCellStyle makeFilledStyle(XSSFWorkbook wb, byte[] bgRgb, byte[] textRgb, boolean bold) {
//         XSSFCellStyle s = wb.createCellStyle(); XSSFFont f = wb.createFont();
//         f.setBold(bold); f.setFontHeightInPoints((short) 10); f.setColor(new XSSFColor(textRgb, null)); s.setFont(f);
//         s.setFillForegroundColor(new XSSFColor(bgRgb, null)); s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//         s.setBorderBottom(BorderStyle.THIN); s.setBorderRight(BorderStyle.THIN); s.setWrapText(true);
//         return s;
//     }

//     private CellStyle pickStatusStyle(Object statusEnum, CellStyle approvedBg, CellStyle rejectedBg, CellStyle holdBg, CellStyle pendingBg, int sl, CellStyle valueStyle, CellStyle altStyle) {
//         if (statusEnum == null) return alt(sl, valueStyle, altStyle);
//         switch (statusEnum.toString()) {
//             case "APPROVED":  return approvedBg;
//             case "REJECTED":  return rejectedBg;
//             case "HOLD":      return holdBg;
//             case "PENDING":   return pendingBg;
//             default:          return alt(sl, valueStyle, altStyle);
//         }
//     }

//     private String buildApprovalRemarks(DynamicRFQApproval ap) {
//         StringBuilder sb = new StringBuilder();
//         if (ap.getComments()       != null && !ap.getComments().isBlank())       sb.append(ap.getComments().trim());
//         if (ap.getHoldRemarks()    != null && !ap.getHoldRemarks().isBlank())    appendRemark(sb, "HOLD: "    + ap.getHoldRemarks().trim());
//         if (ap.getRejectRemarks()  != null && !ap.getRejectRemarks().isBlank())  appendRemark(sb, "REJECT: "  + ap.getRejectRemarks().trim());
//         if (ap.getReleaseRemarks() != null && !ap.getReleaseRemarks().isBlank()) appendRemark(sb, "RELEASE: " + ap.getReleaseRemarks().trim());
//         return sb.toString();
//     }

//     private void appendRemark(StringBuilder sb, String text) { if (sb.length() > 0) sb.append("  |  "); sb.append(text); }

//     private int addLV7(XSSFSheet sheet, int row, CellStyle lc, CellStyle vc, String l1, String v1, String l2, String v2) {
//         Row r = sheet.createRow(row++); r.setHeightInPoints(18);
//         Cell c0 = r.createCell(0); c0.setCellValue(l1); c0.setCellStyle(lc);
//         Cell c1 = r.createCell(1); c1.setCellValue(v1); c1.setCellStyle(vc);
//         sheet.addMergedRegion(new CellRangeAddress(row - 1, row - 1, 1, 2));
//         Cell c3 = r.createCell(3); c3.setCellValue(l2); c3.setCellStyle(lc);
//         Cell c4 = r.createCell(4); c4.setCellValue(v2); c4.setCellStyle(vc);
//         sheet.addMergedRegion(new CellRangeAddress(row - 1, row - 1, 4, 6));
//         return row;
//     }

//     // =========================================================================
//     //  PRIVATE: batch GRN styles
//     // =========================================================================
//     private XSSFCellStyle makeBatchSeparatorStyle(XSSFWorkbook wb, int batchNum) {
//         XSSFCellStyle s = wb.createCellStyle(); XSSFFont f = wb.createFont(); f.setBold(true); f.setFontHeightInPoints((short) 10);
//         f.setColor(new XSSFColor(new byte[]{(byte) 0xff, (byte) 0xff, (byte) 0xff}, null)); s.setFont(f);
//         byte[] bg = (batchNum % 2 == 1) ? rgb(0xe6, 0x55, 0x00) : rgb(0x00, 0x6d, 0x77);
//         s.setFillForegroundColor(new XSSFColor(bg, null)); s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//         s.setVerticalAlignment(VerticalAlignment.CENTER); return s;
//     }

//     private XSSFCellStyle makeBatchSubtotalLabelStyle(XSSFWorkbook wb) {
//         XSSFCellStyle s = wb.createCellStyle(); XSSFFont f = wb.createFont(); f.setBold(true); f.setFontHeightInPoints((short) 9);
//         f.setColor(new XSSFColor(rgb(0x1e, 0x40, 0x88), null)); s.setFont(f);
//         s.setFillForegroundColor(new XSSFColor(rgb(0xe8, 0xf0, 0xfe), null)); s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//         s.setBorderTop(BorderStyle.MEDIUM); return s;
//     }

//     private XSSFCellStyle makeBatchSubtotalAmtStyle(XSSFWorkbook wb) {
//         XSSFCellStyle s = wb.createCellStyle(); XSSFFont f = wb.createFont(); f.setBold(true); f.setFontHeightInPoints((short) 9);
//         f.setColor(new XSSFColor(rgb(0x1e, 0x40, 0x88), null)); s.setFont(f);
//         s.setFillForegroundColor(new XSSFColor(rgb(0xe8, 0xf0, 0xfe), null)); s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//         s.setAlignment(HorizontalAlignment.RIGHT); s.setDataFormat(wb.createDataFormat().getFormat("#,##0.00")); s.setBorderTop(BorderStyle.MEDIUM); return s;
//     }

//     // =========================================================================
//     //  PRIVATE: cell style factories
//     // =========================================================================
//     private XSSFCellStyle makeTitleStyle(XSSFWorkbook wb) {
//         XSSFCellStyle s = wb.createCellStyle(); XSSFFont f = wb.createFont(); f.setBold(true); f.setFontHeightInPoints((short) 14);
//         f.setColor(new XSSFColor(new byte[]{(byte) 0xff, (byte) 0xff, (byte) 0xff}, null)); s.setFont(f);
//         s.setFillForegroundColor(new XSSFColor(rgb(0x1e, 0x40, 0x88), null)); s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//         s.setAlignment(HorizontalAlignment.CENTER); s.setVerticalAlignment(VerticalAlignment.CENTER); s.setWrapText(true); return s;
//     }

//     private XSSFCellStyle makeHeaderStyle(XSSFWorkbook wb, byte[] rgb) {
//         XSSFCellStyle s = wb.createCellStyle(); XSSFFont f = wb.createFont(); f.setBold(true); f.setFontHeightInPoints((short) 10);
//         f.setColor(new XSSFColor(new byte[]{(byte) 0xff, (byte) 0xff, (byte) 0xff}, null)); s.setFont(f);
//         s.setFillForegroundColor(new XSSFColor(rgb, null)); s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//         s.setBorderBottom(BorderStyle.THIN); s.setBorderTop(BorderStyle.THIN); s.setBorderLeft(BorderStyle.THIN); s.setBorderRight(BorderStyle.THIN);
//         s.setWrapText(true); return s;
//     }

//     private XSSFCellStyle makeLabelStyle(XSSFWorkbook wb) {
//         XSSFCellStyle s = wb.createCellStyle(); XSSFFont f = wb.createFont(); f.setBold(true); f.setFontHeightInPoints((short) 9); s.setFont(f);
//         s.setFillForegroundColor(new XSSFColor(rgb(0xf0, 0xf4, 0xff), null)); s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//         s.setBorderBottom(BorderStyle.THIN); s.setBorderRight(BorderStyle.THIN); return s;
//     }

//     private XSSFCellStyle makeValueStyle(XSSFWorkbook wb) { XSSFCellStyle s = wb.createCellStyle(); s.setBorderBottom(BorderStyle.THIN); s.setWrapText(true); return s; }

//     private XSSFCellStyle makeAltRowStyle(XSSFWorkbook wb) {
//         XSSFCellStyle s = wb.createCellStyle();
//         s.setFillForegroundColor(new XSSFColor(rgb(0xf5, 0xf7, 0xfb), null)); s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//         s.setBorderBottom(BorderStyle.THIN); s.setWrapText(true); return s;
//     }

//     private XSSFCellStyle makeAmountStyle(XSSFWorkbook wb) {
//         XSSFCellStyle s = wb.createCellStyle(); s.setAlignment(HorizontalAlignment.RIGHT);
//         s.setDataFormat(wb.createDataFormat().getFormat("#,##0.00")); s.setBorderBottom(BorderStyle.THIN); return s;
//     }

//     private XSSFCellStyle makeLowestStyle(XSSFWorkbook wb) {
//         XSSFCellStyle s = wb.createCellStyle(); XSSFFont f = wb.createFont(); f.setBold(true); f.setFontHeightInPoints((short) 9);
//         f.setColor(new XSSFColor(rgb(0x1a, 0x5c, 0x1a), null)); s.setFont(f);
//         s.setFillForegroundColor(new XSSFColor(rgb(0xd4, 0xed, 0xda), null)); s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//         s.setAlignment(HorizontalAlignment.RIGHT); s.setDataFormat(wb.createDataFormat().getFormat("#,##0.00")); s.setBorderBottom(BorderStyle.THIN); return s;
//     }

//     private XSSFCellStyle makeTotalStyle(XSSFWorkbook wb) {
//         XSSFCellStyle s = wb.createCellStyle(); XSSFFont f = wb.createFont(); f.setBold(true); f.setFontHeightInPoints((short) 11);
//         f.setColor(new XSSFColor(new byte[]{(byte) 0xff, (byte) 0xff, (byte) 0xff}, null)); s.setFont(f);
//         s.setFillForegroundColor(new XSSFColor(rgb(0x1e, 0x40, 0x88), null)); s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//         s.setAlignment(HorizontalAlignment.RIGHT); s.setDataFormat(wb.createDataFormat().getFormat("#,##0.00")); return s;
//     }

//     private XSSFCellStyle makeStatusStyle(XSSFWorkbook wb) {
//         XSSFCellStyle s = wb.createCellStyle(); XSSFFont f = wb.createFont(); f.setBold(true); f.setFontHeightInPoints((short) 9); s.setFont(f);
//         s.setAlignment(HorizontalAlignment.CENTER); s.setBorderBottom(BorderStyle.THIN); return s;
//     }

//     private XSSFCellStyle makeStatLabelStyle(XSSFWorkbook wb) {
//         XSSFCellStyle s = wb.createCellStyle(); XSSFFont f = wb.createFont(); f.setBold(true); f.setFontHeightInPoints((short) 10); s.setFont(f);
//         s.setFillForegroundColor(new XSSFColor(rgb(0xe8, 0xf0, 0xfe), null)); s.setFillPattern(FillPatternType.SOLID_FOREGROUND); return s;
//     }

//     private XSSFCellStyle makeStatValueStyle(XSSFWorkbook wb) {
//         XSSFCellStyle s = wb.createCellStyle(); XSSFFont f = wb.createFont(); f.setBold(true); f.setFontHeightInPoints((short) 10);
//         f.setColor(new XSSFColor(rgb(0x1e, 0x40, 0x88), null)); s.setFont(f); return s;
//     }

//     private XSSFCellStyle makeMetaStyle(XSSFWorkbook wb) {
//         XSSFCellStyle s = wb.createCellStyle(); XSSFFont f = wb.createFont(); f.setItalic(true); f.setFontHeightInPoints((short) 9);
//         f.setColor(new XSSFColor(rgb(0x44, 0x44, 0x44), null)); s.setFont(f); return s;
//     }

//     private XSSFCellStyle makeWarnStyle(XSSFWorkbook wb) {
//         XSSFCellStyle s = wb.createCellStyle();
//         s.setFillForegroundColor(new XSSFColor(rgb(0xff, 0xf9, 0xc4), null)); s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//         s.setBorderBottom(BorderStyle.THIN); s.setWrapText(true); return s;
//     }

//     private XSSFCellStyle makeBannerMatchStyle(XSSFWorkbook wb) {
//         XSSFCellStyle s = wb.createCellStyle(); XSSFFont f = wb.createFont(); f.setBold(true); f.setFontHeightInPoints((short) 14);
//         f.setColor(new XSSFColor(new byte[]{(byte) 0xff, (byte) 0xff, (byte) 0xff}, null)); s.setFont(f);
//         s.setFillForegroundColor(new XSSFColor(rgb(0x1b, 0x5e, 0x20), null)); s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//         s.setAlignment(HorizontalAlignment.CENTER); s.setVerticalAlignment(VerticalAlignment.CENTER); s.setWrapText(true); return s;
//     }

//     private XSSFCellStyle makeBannerWarnStyle(XSSFWorkbook wb) {
//         XSSFCellStyle s = wb.createCellStyle(); XSSFFont f = wb.createFont(); f.setBold(true); f.setFontHeightInPoints((short) 14);
//         f.setColor(new XSSFColor(new byte[]{(byte) 0xff, (byte) 0xff, (byte) 0xff}, null)); s.setFont(f);
//         s.setFillForegroundColor(new XSSFColor(rgb(0xe6, 0x55, 0x00), null)); s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//         s.setAlignment(HorizontalAlignment.CENTER); s.setVerticalAlignment(VerticalAlignment.CENTER); s.setWrapText(true); return s;
//     }

//     private XSSFCellStyle makeBannerFailStyle(XSSFWorkbook wb) {
//         XSSFCellStyle s = wb.createCellStyle(); XSSFFont f = wb.createFont(); f.setBold(true); f.setFontHeightInPoints((short) 14);
//         f.setColor(new XSSFColor(new byte[]{(byte) 0xff, (byte) 0xff, (byte) 0xff}, null)); s.setFont(f);
//         s.setFillForegroundColor(new XSSFColor(rgb(0xb7, 0x1c, 0x1c), null)); s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//         s.setAlignment(HorizontalAlignment.CENTER); s.setVerticalAlignment(VerticalAlignment.CENTER); s.setWrapText(true); return s;
//     }

//     private XSSFCellStyle makeAwardedRowStyle(XSSFWorkbook wb) {
//         XSSFCellStyle s = wb.createCellStyle(); XSSFFont f = wb.createFont(); f.setBold(true); f.setFontHeightInPoints((short) 9);
//         f.setColor(new XSSFColor(rgb(0x7d, 0x4a, 0x00), null)); s.setFont(f);
//         s.setFillForegroundColor(new XSSFColor(rgb(0xff, 0xf3, 0xcd), null)); s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//         s.setBorderBottom(BorderStyle.THIN); s.setBorderTop(BorderStyle.THIN); s.setBorderLeft(BorderStyle.THIN); s.setBorderRight(BorderStyle.THIN);
//         s.setWrapText(true); return s;
//     }

//     private XSSFCellStyle makeAwardedAmtStyle(XSSFWorkbook wb) {
//         XSSFCellStyle s = wb.createCellStyle(); XSSFFont f = wb.createFont(); f.setBold(true); f.setFontHeightInPoints((short) 9);
//         f.setColor(new XSSFColor(rgb(0x7d, 0x4a, 0x00), null)); s.setFont(f);
//         s.setFillForegroundColor(new XSSFColor(rgb(0xff, 0xf3, 0xcd), null)); s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//         s.setAlignment(HorizontalAlignment.RIGHT); s.setDataFormat(wb.createDataFormat().getFormat("#,##0.00"));
//         s.setBorderBottom(BorderStyle.THIN); s.setBorderTop(BorderStyle.THIN); s.setBorderLeft(BorderStyle.THIN); s.setBorderRight(BorderStyle.THIN);
//         return s;
//     }

//     // =========================================================================
//     //  PRIVATE: layout helpers
//     // =========================================================================
//     private int addBanner(XSSFSheet sheet, int row, String title, CellStyle ts, String sub, int cols) {
//         sheet.addMergedRegion(new CellRangeAddress(row, row, 0, cols - 1));
//         Row r1 = sheet.createRow(row++); r1.setHeightInPoints(30); Cell c1 = r1.createCell(0); c1.setCellValue(title); c1.setCellStyle(ts);
//         sheet.addMergedRegion(new CellRangeAddress(row, row, 0, cols - 1));
//         Row r2 = sheet.createRow(row++); r2.setHeightInPoints(18); Cell c2 = r2.createCell(0); c2.setCellValue(sub); c2.setCellStyle(ts);
//         return row;
//     }

//     private int addBannerCustomStyle(XSSFSheet sheet, int row, String title, CellStyle ts, String sub, int cols) {
//         sheet.addMergedRegion(new CellRangeAddress(row, row, 0, cols - 1));
//         Row r1 = sheet.createRow(row++); r1.setHeightInPoints(30); Cell c1 = r1.createCell(0); c1.setCellValue(title); c1.setCellStyle(ts);
//         sheet.addMergedRegion(new CellRangeAddress(row, row, 0, cols - 1));
//         Row r2 = sheet.createRow(row++); r2.setHeightInPoints(18); Cell c2 = r2.createCell(0); c2.setCellValue(sub); c2.setCellStyle(ts);
//         return row;
//     }

//     private int addSectionHeader(XSSFSheet sheet, int row, String text, CellStyle cs, int cols) {
//         sheet.addMergedRegion(new CellRangeAddress(row, row, 0, cols - 1));
//         Row r = sheet.createRow(row++); r.setHeightInPoints(20); Cell c = r.createCell(0); c.setCellValue(text); c.setCellStyle(cs);
//         return row;
//     }

//     private int addLV(XSSFSheet sheet, int row, CellStyle lc, CellStyle vc, String l1, Object v1, String l2, Object v2) {
//         Row r = sheet.createRow(row++); r.setHeightInPoints(16);
//         str(r, 0, l1, lc); str(r, 1, s(v1), vc); str(r, 2, l2, lc); str(r, 3, s(v2), vc);
//         return row;
//     }

//     private void addHeaderRow(XSSFSheet sheet, int row, CellStyle cs, String[] cols) {
//         Row r = sheet.createRow(row); r.setHeightInPoints(18);
//         for (int c = 0; c < cols.length; c++) { Cell cell = r.createCell(c); cell.setCellValue(cols[c]); cell.setCellStyle(cs); }
//     }

//     private void setWidths(XSSFSheet sheet, int[] widths) { for (int c = 0; c < widths.length; c++) sheet.setColumnWidth(c, widths[c]); }

//     private void str(Row row, int col, String value, CellStyle cs) { Cell c = row.createCell(col); c.setCellValue(value != null ? value : ""); c.setCellStyle(cs); }

//     private void num(Row row, int col, Object value, CellStyle cs) {
//         Cell c = row.createCell(col);
//         if (value instanceof BigDecimal) c.setCellValue(((BigDecimal) value).doubleValue());
//         else if (value instanceof Number) c.setCellValue(((Number) value).doubleValue());
//         else c.setCellValue(0);
//         c.setCellStyle(cs);
//     }

//     private void num(Row row, int col, BigDecimal value, CellStyle cs) { Cell c = row.createCell(col); c.setCellValue(value != null ? value.doubleValue() : 0); c.setCellStyle(cs); }

//     // =========================================================================
//     //  PRIVATE: utilities
//     // =========================================================================
//     private byte[] rgb(int r, int g, int b) { return new byte[]{(byte) r, (byte) g, (byte) b}; }
//     private CellStyle alt(int sl, CellStyle n, CellStyle a) { return (sl % 2 == 0) ? a : n; }
//     private String s(Object o) { return o != null ? o.toString() : "—"; }
//     private String en(Object o) { return o != null ? o.toString() : "N/A"; }
//     private String bool(Boolean b) { return Boolean.TRUE.equals(b) ? "Yes" : "No"; }
//     private String fmt(LocalDateTime dt) { return dt != null ? dt.format(DATE_FMT) : "—"; }

//     private String joinNonNull(String sep, String... parts) {
//         return Arrays.stream(parts).filter(p -> p != null && !p.isBlank()).collect(Collectors.joining(" " + sep + " "));
//     }

//     private Optional<SupplierQuoteItem> findQuote(List<SupplierQuoteItem> quotes, Long itemId, Long supplierId) {
//         return quotes.stream().filter(q -> q.getRfqItem() != null && q.getRfqItem().getId().equals(itemId) && q.getRfqSupplier() != null && q.getRfqSupplier().getSupplier() != null && q.getRfqSupplier().getSupplier().getId().equals(supplierId)).findFirst();
//     }

//     private double sumGrandTotal(List<SupplierQuoteItem> quotes, Long supplierId) {
//         return quotes.stream().filter(q -> q.getRfqSupplier() != null && q.getRfqSupplier().getSupplier() != null && q.getRfqSupplier().getSupplier().getId().equals(supplierId) && q.getGrandTotal() != null).mapToDouble(q -> q.getGrandTotal().doubleValue()).sum();
//     }

//     private String determineSupplierCurrency(RFQ rfq, Supplier supplier) {
//         if (rfq.getLocation() == null || supplier == null) return "INR";
//         String buyerCountry = rfq.getLocation().getCountry(); String supCountry = supplier.getCountry();
//         if (buyerCountry != null && supCountry != null && buyerCountry.trim().equalsIgnoreCase(supCountry.trim())) return rfq.getLocation().getCurrencyCode() != null ? rfq.getLocation().getCurrencyCode() : "INR";
//         return "USD";
//     }
// }

package com.itti.leadcapturing.service;

import com.itti.leadcapturing.model.*;
import com.itti.leadcapturing.repo.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RFQReportService {

    @Autowired private RFQRepository               rfqRepository;
    @Autowired private RFQItemRepository           rfqItemRepository;
    @Autowired private RFQSupplierRepository       rfqSupplierRepository;
    @Autowired private SupplierQuoteItemRepository quoteItemRepository;
    @Autowired private DynamicRFQApprovalRepository approvalRepository;
    @Autowired private InvoiceRepository           invoiceRepository;
    @Autowired private PurchaseOrderRepository     poRepository;
    @Autowired private GRNRepository               grnRepository;
    @Autowired private ThreeWayMatchRepository     matchRepository;
    // NOTE: UserRepository NOT needed — viewer info comes as URL params from frontend

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
    private static final DateTimeFormatter DTTM_FMT = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm");

    // =========================================================================
    //  PO SUMMARY EXCEL
    // =========================================================================
    @Transactional(readOnly = true)
    public byte[] generatePOSummaryExcel(Long poId) throws Exception {
        PurchaseOrder po = poRepository.findById(poId)
                .orElseThrow(() -> new RuntimeException("PO not found: " + poId));
        List<POLineItem> lineItems = po.getLineItems();

        try (XSSFWorkbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            CellStyle title   = makeTitleStyle(wb);
            CellStyle hdrNavy = makeHeaderStyle(wb, rgb(0x1e,0x40,0x88));
            CellStyle hdrGrn  = makeHeaderStyle(wb, rgb(0x1b,0x5e,0x20));
            CellStyle lbl     = makeLabelStyle(wb);
            CellStyle val     = makeValueStyle(wb);
            CellStyle amt     = makeAmountStyle(wb);
            CellStyle alt     = makeAltRowStyle(wb);
            CellStyle tot     = makeTotalStyle(wb);
            CellStyle sts     = makeStatusStyle(wb);

            XSSFSheet s1 = wb.createSheet("PO Overview");
            s1.setColumnWidth(0,6000); s1.setColumnWidth(1,9000); s1.setColumnWidth(2,6000); s1.setColumnWidth(3,9000);
            int row = 0;
            row = addBanner(s1, row, "PURCHASE ORDER", title,
                    "PO: "+s(po.getPoNumber())+"  |  Status: "+(po.getStatus()!=null?po.getStatus().name():"—")+"  |  Generated: "+LocalDateTime.now().format(DTTM_FMT), 4);
            row = addSectionHeader(s1, row, "PO INFORMATION", hdrNavy, 4);
            row = addLV(s1,row,lbl,val,"PO Number",s(po.getPoNumber()),"PO Date",fmt(po.getPoDate()));
            row = addLV(s1,row,lbl,val,"Status",po.getStatus()!=null?po.getStatus().name():"—","Approval Status",po.getApprovalStatus()!=null?po.getApprovalStatus().name():"—");
            row = addLV(s1,row,lbl,val,"Reference Quote",s(po.getReferenceQuoteNo()),"Currency",s(po.getCurrencyCode())+" "+s(po.getCurrencySymbol()));
            row = addLV(s1,row,lbl,val,"Payment Terms",s(po.getPaymentTerms()),"Delivery Terms",s(po.getDeliveryTerms()));
            row = addLV(s1,row,lbl,val,"Mode of Payment",s(po.getModeOfPayment()),"Dispatched Through",s(po.getDispatchedThrough()));
            row = addLV(s1,row,lbl,val,"Destination",s(po.getDestination()),"Amount in Words",s(po.getAmountInWords()));
            if (po.getBuyerRemarks()!=null) row = addLV(s1,row,lbl,val,"Buyer Remarks",s(po.getBuyerRemarks()),"","");
            row++;
            if (po.getBuyer()!=null){ Buyer b=po.getBuyer(); row=addSectionHeader(s1,row,"BUYER INFORMATION",hdrNavy,4); row=addLV(s1,row,lbl,val,"Company",b.getCompanyName(),"Type",s(b.getCompanyType())); row=addLV(s1,row,lbl,val,"Contact",b.getContactPersonName(),"Email",s(b.getContactPersonEmail())); row=addLV(s1,row,lbl,val,"Phone",s(b.getContactPersonPhone()),"GST",s(b.getGstNumber())); row++; }
            if (po.getSupplier()!=null){ Supplier sup=po.getSupplier(); row=addSectionHeader(s1,row,"SUPPLIER INFORMATION",hdrNavy,4); row=addLV(s1,row,lbl,val,"Company",s(sup.getCompanyName()),"Type",s(sup.getCompanyType())); row=addLV(s1,row,lbl,val,"Contact",s(sup.getContactPersonName()),"Email",s(sup.getContactPersonEmail())); row=addLV(s1,row,lbl,val,"Phone",s(sup.getContactPersonPhone()),"Country",s(sup.getCountry())); row++; }
            if (po.getDeliveryLocation()!=null){ Location dl=po.getDeliveryLocation(); row=addSectionHeader(s1,row,"DELIVERY LOCATION",hdrNavy,4); row=addLV(s1,row,lbl,val,"Location Name",dl.getLocationName(),"City",s(dl.getCity())); row=addLV(s1,row,lbl,val,"State",s(dl.getState()),"Country",s(dl.getCountry())); row=addLV(s1,row,lbl,val,"Address",s(dl.getAddressLine1()),"Contact",s(dl.getLocationContactName())); row++; }
            if (po.getRfq()!=null){ row=addSectionHeader(s1,row,"RFQ REFERENCE",hdrNavy,4); row=addLV(s1,row,lbl,val,"RFQ Number",s(po.getRfq().getRfqNumber()),"RFQ Title",s(po.getRfq().getRfqTitle())); row++; }
            if (po.getApprovedByName()!=null){ row=addSectionHeader(s1,row,"APPROVAL INFORMATION",hdrNavy,4); row=addLV(s1,row,lbl,val,"Approved By",s(po.getApprovedByName()),"Designation",s(po.getApprovedByDesignation())); addLV(s1,row,lbl,val,"Approval Date",fmt(po.getApprovalDate()),"",""); }

            XSSFSheet s2 = wb.createSheet("Line Items");
            setWidths(s2, new int[]{1200,3000,7000,2500,2000,2500,4000,2000,2000,4500,4000});
            row = 0;
            row = addBanner(s2,row,"LINE ITEMS",title,s(po.getPoNumber())+"  ["+lineItems.size()+" items]  |  Currency: "+s(po.getCurrencyCode()),11);
            addHeaderRow(s2,row++,hdrGrn,new String[]{"#","Item Code","Description","Specifications","UOM","PO Qty","Unit Price","Disc %","Tax %","Line Total","Remarks"});
            int sl=1;
            for (POLineItem li:lineItems){ Row r=s2.createRow(row++); CellStyle cs=altRow(sl,val,alt); str(r,0,String.valueOf(sl++),cs); str(r,1,s(li.getItemCode()),cs); str(r,2,s(li.getItemDescription()),cs); str(r,3,s(li.getSpecifications()),cs); str(r,4,s(li.getUom()),cs); num(r,5,li.getQuantity(),amt); num(r,6,li.getUnitRate(),amt); num(r,7,li.getDiscountPercentage(),amt); num(r,8,li.getTaxPercentage(),amt); num(r,9,li.getLineTotal(),amt); str(r,10,s(li.getBrandMakeModel()),cs); }
            row++;
            Object[][] totals = {{"Subtotal (Pre-tax)",po.getSubtotal()},{"Tax Amount",po.getTaxAmount()},{"GRAND TOTAL",po.getGrandTotal()}};
            for (int t=0;t<totals.length;t++){ Row tr=s2.createRow(row++); boolean ig=(t==totals.length-1); Cell lc=tr.createCell(9); lc.setCellValue((String)totals[t][0]); lc.setCellStyle(ig?tot:lbl); Cell vc=tr.createCell(10); if(totals[t][1] instanceof BigDecimal) vc.setCellValue(((BigDecimal)totals[t][1]).doubleValue()); else vc.setCellValue(0); vc.setCellStyle(ig?tot:amt); }
            if (po.getAmountInWords()!=null){ row++; Row ar=s2.createRow(row++); Cell albl=ar.createCell(0); albl.setCellValue("Amount in Words:"); albl.setCellStyle(lbl); Cell aval=ar.createCell(1); aval.setCellValue(po.getAmountInWords()); aval.setCellStyle(val); s2.addMergedRegion(new CellRangeAddress(row-1,row-1,1,10)); }
            wb.write(out); return out.toByteArray();
        }
    }

    // =========================================================================
    //  GRN EXCEL
    // =========================================================================
    @Transactional(readOnly = true)
    public byte[] generateGRNExcel(Long grnId) throws Exception {
        GRN grn = grnRepository.findByIdWithLineItems(grnId)
                .orElseThrow(() -> new RuntimeException("GRN not found: "+grnId));
        List<GRNLineItem> lineItems = grn.getLineItems();
        try (XSSFWorkbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            CellStyle title    = makeTitleStyle(wb);
            CellStyle hdrNavy  = makeHeaderStyle(wb,rgb(0x1e,0x40,0x88));
            CellStyle hdrGrn   = makeHeaderStyle(wb,rgb(0x1b,0x5e,0x20));
            CellStyle hdrOrng  = makeHeaderStyle(wb,rgb(0xe6,0x55,0x00));
            CellStyle lbl      = makeLabelStyle(wb); CellStyle val=makeValueStyle(wb);
            CellStyle amt      = makeAmountStyle(wb); CellStyle alt=makeAltRowStyle(wb);
            CellStyle tot      = makeTotalStyle(wb);  CellStyle sts=makeStatusStyle(wb);

            XSSFSheet s1 = wb.createSheet("GRN Overview");
            s1.setColumnWidth(0,6000); s1.setColumnWidth(1,9000); s1.setColumnWidth(2,6000); s1.setColumnWidth(3,9000);
            int row=0;
            row=addBanner(s1,row,"GOODS RECEIPT NOTE",title,"GRN: "+s(grn.getGrnNumber())+"  |  Status: "+(grn.getStatus()!=null?grn.getStatus().name():"—")+"  |  Generated: "+LocalDateTime.now().format(DTTM_FMT),4);
            row=addSectionHeader(s1,row,"GRN INFORMATION",hdrNavy,4);
            row=addLV(s1,row,lbl,val,"GRN Number",s(grn.getGrnNumber()),"Status",grn.getStatus()!=null?grn.getStatus().name():"—");
            row=addLV(s1,row,lbl,val,"Received Date",fmt(grn.getReceivedDate()),"PO Number",s(grn.getPoNumber()));
            row=addLV(s1,row,lbl,val,"Invoice Number",s(grn.getInvoiceNumber()),"Supplier",s(grn.getSupplierName())); row++;
            row=addSectionHeader(s1,row,"DELIVERY DETAILS",hdrNavy,4);
            row=addLV(s1,row,lbl,val,"Delivery Challan",s(grn.getDeliveryChallanNumber()),"LR Number",s(grn.getLrNumber()));
            row=addLV(s1,row,lbl,val,"Transporter",s(grn.getTransporterName()),"Vehicle No.",s(grn.getVehicleNumber()));
            row=addLV(s1,row,lbl,val,"Delivery Location",s(grn.getDeliveryLocation()),"Received By",s(grn.getReceivedByName())); row++;
            row=addSectionHeader(s1,row,"QA INFORMATION",hdrOrng,4);
            row=addLV(s1,row,lbl,val,"QA Inspector",s(grn.getInspectedByName()),"Approved By",s(grn.getApprovedByName()));
            row=addLV(s1,row,lbl,val,"Approved At",fmt(grn.getApprovedAt()),"Remarks",s(grn.getRemarks())); row++;
            row=addSectionHeader(s1,row,"VALUE SUMMARY",hdrNavy,4);
            row=addLV(s1,row,lbl,val,"Total Ordered Value",s(grn.getTotalOrderedValue()),"Total Received Value",s(grn.getTotalReceivedValue()));
            addLV(s1,row,lbl,val,"Total Rejected Value",s(grn.getTotalRejectedValue()),"","");

            XSSFSheet s2 = wb.createSheet("Line Items");
            setWidths(s2,new int[]{1200,3000,7000,2500,2000,3000,3000,3000,3000,4000,4000,4000,2500,5000});
            row=0; row=addBanner(s2,row,"LINE ITEMS",title,s(grn.getGrnNumber())+"  ["+lineItems.size()+" items]",14);
            Row ph=s2.createRow(row++); ph.setHeightInPoints(16);
            str(ph,0,"#",hdrNavy); str(ph,1,"Item Code",hdrNavy); str(ph,2,"Description",hdrNavy); str(ph,3,"UOM",hdrNavy); str(ph,4,"Ordered",hdrNavy); str(ph,5,"Received",hdrNavy); str(ph,6,"Defective",hdrOrng); str(ph,7,"Rejected",hdrOrng); str(ph,8,"Accepted",hdrGrn); str(ph,9,"PO Rate",hdrNavy); str(ph,10,"PO Line Total",hdrNavy); str(ph,11,"Accepted Value",hdrGrn); str(ph,12,"Condition",hdrOrng); str(ph,13,"QA Remarks",hdrOrng);
            int sl=1;
            for (GRNLineItem li:lineItems){ Row r=s2.createRow(row++); CellStyle cs=altRow(sl,val,alt); str(r,0,String.valueOf(sl++),cs); str(r,1,s(li.getItemCode()),cs); str(r,2,s(li.getItemDescription()),cs); str(r,3,s(li.getUom()),cs); num(r,4,li.getOrderedQuantity(),amt); num(r,5,li.getReceivedQuantity(),amt); num(r,6,li.getDefectiveQuantity(),amt); num(r,7,li.getRejectedQuantity(),amt); num(r,8,li.getAcceptedQuantity(),amt); num(r,9,li.getPoUnitRate(),amt); num(r,10,li.getPoLineTotal(),amt); num(r,11,li.getAcceptedValue(),amt); str(r,12,li.getItemCondition()!=null?li.getItemCondition().name():"GOOD",sts); str(r,13,s(li.getQaRemarks()),cs); }
            row++;
            String[][] grnTots = {{"Total Ordered Value",grn.getTotalOrderedValue()!=null?grn.getTotalOrderedValue().toPlainString():"0"},{"Total Received Value",grn.getTotalReceivedValue()!=null?grn.getTotalReceivedValue().toPlainString():"0"},{"Total Rejected Value",grn.getTotalRejectedValue()!=null?grn.getTotalRejectedValue().toPlainString():"0"}};
            for (int t=0;t<grnTots.length;t++){ Row tr=s2.createRow(row++); boolean il=(t==grnTots.length-1); Cell lc=tr.createCell(10); lc.setCellValue(grnTots[t][0]); lc.setCellStyle(il?tot:lbl); Cell vc=tr.createCell(11); try{vc.setCellValue(Double.parseDouble(grnTots[t][1]));}catch(Exception e){vc.setCellValue(0);} vc.setCellStyle(il?tot:amt); }
            wb.write(out); return out.toByteArray();
        }
    }

    // =========================================================================
    //  THREE-WAY MATCH EXCEL
    // =========================================================================
    @Transactional(readOnly = true)
    public byte[] generateThreeWayMatchExcel(Long matchId) throws Exception {
        ThreeWayMatch match = matchRepository.findByIdWithLineResults(matchId)
                .orElseThrow(() -> new RuntimeException("3-Way Match not found: "+matchId));
        List<MatchLineResult> lineResults = match.getLineResults();
        try (XSSFWorkbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            CellStyle title    = makeTitleStyle(wb);
            CellStyle hdrNavy  = makeHeaderStyle(wb,rgb(0x1e,0x40,0x88));
            CellStyle hdrGrn   = makeHeaderStyle(wb,rgb(0x1b,0x5e,0x20));
            CellStyle hdrOrng  = makeHeaderStyle(wb,rgb(0xe6,0x55,0x00));
            CellStyle lbl      = makeLabelStyle(wb); CellStyle val=makeValueStyle(wb);
            CellStyle amt      = makeAmountStyle(wb); CellStyle alt=makeAltRowStyle(wb);
            CellStyle tot      = makeTotalStyle(wb);  CellStyle sts=makeStatusStyle(wb);
            CellStyle low      = makeLowestStyle(wb); CellStyle warn=makeWarnStyle(wb);

            XSSFSheet s1 = wb.createSheet("Match Summary");
            s1.setColumnWidth(0,6000); s1.setColumnWidth(1,9000); s1.setColumnWidth(2,6000); s1.setColumnWidth(3,9000);
            int row=0;
            boolean isMatched="MATCHED".equals(match.getMatchStatus()!=null?match.getMatchStatus().name():"");
            boolean isFailed=match.getMatchStatus()!=null&&(match.getMatchStatus()==ThreeWayMatchStatus.FAILED||match.getMatchStatus()==ThreeWayMatchStatus.ITEM_MISMATCH||match.getMatchStatus()==ThreeWayMatchStatus.DISPUTED);
            CellStyle banSty=isMatched?makeBannerMatchStyle(wb):isFailed?makeBannerFailStyle(wb):makeBannerWarnStyle(wb);
            row=addBannerCustomStyle(s1,row,"3-WAY MATCH REPORT",banSty,"Match #"+match.getId()+"  v"+match.getMatchVersion()+"  |  Status: "+(match.getMatchStatus()!=null?match.getMatchStatus().name():"—")+"  |  Generated: "+LocalDateTime.now().format(DTTM_FMT),4);
            row=addSectionHeader(s1,row,"DOCUMENT REFERENCES",hdrNavy,4);
            row=addLV(s1,row,lbl,val,"PO Number",s(match.getPoNumber()),"GRN Number",s(match.getGrnNumber()));
            row=addLV(s1,row,lbl,val,"Invoice Number",s(match.getInvoiceNumber()),"Match Version",String.valueOf(match.getMatchVersion()));
            row=addLV(s1,row,lbl,val,"Tolerance %",s(match.getTolerancePercentage()),"Performed By",s(match.getPerformedByName()));
            row=addLV(s1,row,lbl,val,"Performed At",fmt(match.getPerformedAt()),"Updated At",fmt(match.getUpdatedAt())); row++;
            row=addSectionHeader(s1,row,"FINANCIAL COMPARISON",hdrNavy,4);
            row=addLV(s1,row,lbl,val,"PO Total Value",s(match.getPoTotalValue()),"GRN Accepted Value",s(match.getGrnAcceptedValue()));
            row=addLV(s1,row,lbl,val,"Invoice Total Value",s(match.getInvoiceTotalValue()),"Variance Amount",s(match.getVarianceAmount()));
            row=addLV(s1,row,lbl,val,"Variance %",s(match.getVariancePercentage()),"Approved Payment",s(match.getApprovedPaymentAmount())); row++;
            row=addSectionHeader(s1,row,"MATCH FLAGS",hdrNavy,4);
            row=addLV(s1,row,lbl,val,"Quantity Mismatch",bool(match.getHasQuantityMismatch()),"Price Mismatch",bool(match.getHasPriceMismatch()));
            row=addLV(s1,row,lbl,val,"Item Mismatch",bool(match.getHasItemMismatch()),"Excess Delivery",bool(match.getHasExcessDelivery()));
            row=addLV(s1,row,lbl,val,"Matched Lines",String.valueOf(match.getTotalMatchedLines()),"Mismatch Lines",String.valueOf(match.getTotalMismatchLines())); row++;
            if(match.getResolution()!=null){row=addSectionHeader(s1,row,"RESOLUTION",hdrNavy,4);row=addLV(s1,row,lbl,val,"Resolution",match.getResolution().name(),"Resolved By",s(match.getResolvedByName()));row=addLV(s1,row,lbl,val,"Resolved At",fmt(match.getResolvedAt()),"Resolution Remarks",s(match.getResolutionRemarks()));}
            if(match.getMatchSummary()!=null){row++;Row sr=s1.createRow(row++);sr.setHeightInPoints(30);s1.addMergedRegion(new CellRangeAddress(row-1,row-1,0,3));Cell sc=sr.createCell(0);sc.setCellValue(match.getMatchSummary());sc.setCellStyle(isMatched?low:warn);}

            XSSFSheet s2 = wb.createSheet("Line Results");
            setWidths(s2,new int[]{1200,3000,7000,2000,3000,3000,3000,3500,3500,3000,3500,3500,2500,4500,4500,4500,3000,8000});
            row=0; row=addBanner(s2,row,"LINE-BY-LINE MATCH RESULTS",title,s(match.getInvoiceNumber())+" ↔ "+s(match.getGrnNumber())+"  |  "+lineResults.size()+" lines",18);
            addHeaderRow(s2,row++,hdrNavy,new String[]{"#","Item Code","Item Description","UOM","PO Qty","GRN Accepted","Inv Qty","Qty Variance","Qty Var%","Within Tol?","PO Rate","Inv Price","Price Var%","PO Line Value","GRN Acc. Value","Inv Line Value","Line Status","Mismatch Notes"});
            int sl=1;
            for(MatchLineResult lr:lineResults){
                Row r=s2.createRow(row++);
                boolean lm=lr.getLineMatchStatus()==MatchLineResult.LineMatchStatus.MATCHED||lr.getLineMatchStatus()==MatchLineResult.LineMatchStatus.PARTIALLY_MATCHED;
                boolean lf=lr.getLineMatchStatus()==MatchLineResult.LineMatchStatus.NOT_IN_PO||lr.getLineMatchStatus()==MatchLineResult.LineMatchStatus.NOT_RECEIVED;
                CellStyle cs=lf?warn:altRow(sl,val,alt); CellStyle ams=lf?warn:amt; CellStyle gs=lm?low:ams;
                str(r,0,String.valueOf(sl++),cs); str(r,1,s(lr.getItemCode()),cs); str(r,2,s(lr.getItemDescription()),cs); str(r,3,s(lr.getUom()),cs);
                num(r,4,lr.getPoOrderedQuantity(),ams); num(r,5,lr.getGrnAcceptedQuantity(),gs); num(r,6,lr.getInvoiceQuantity(),ams);
                num(r,7,lr.getQuantityVariance(),ams); num(r,8,lr.getQuantityVariancePct(),amt); str(r,9,Boolean.TRUE.equals(lr.getWithinTolerance())?"✓ Yes":"✗ No",sts);
                num(r,10,lr.getPoUnitRate(),ams); num(r,11,lr.getInvoiceUnitPrice(),ams); num(r,12,lr.getPriceVariancePct(),amt);
                num(r,13,lr.getPoLineValue(),ams); num(r,14,lr.getGrnAcceptedValue(),gs); num(r,15,lr.getInvoiceLineValue(),ams);
                str(r,16,lr.getLineMatchStatus()!=null?lr.getLineMatchStatus().name():"—",sts); str(r,17,s(lr.getMismatchNotes()),cs);
            }
            row++;
            BigDecimal tPO=lineResults.stream().filter(lr->lr.getPoLineValue()!=null).map(MatchLineResult::getPoLineValue).reduce(BigDecimal.ZERO,BigDecimal::add);
            BigDecimal tGRN=lineResults.stream().filter(lr->lr.getGrnAcceptedValue()!=null).map(MatchLineResult::getGrnAcceptedValue).reduce(BigDecimal.ZERO,BigDecimal::add);
            BigDecimal tInv=lineResults.stream().filter(lr->lr.getInvoiceLineValue()!=null).map(MatchLineResult::getInvoiceLineValue).reduce(BigDecimal.ZERO,BigDecimal::add);
            BigDecimal tVar=tInv.subtract(tGRN);
            Row tr2=s2.createRow(row++); for(int c=0;c<13;c++){Cell nc=tr2.createCell(c);nc.setCellStyle(tot);if(c==0)nc.setCellValue("TOTALS");}
            num(tr2,13,tPO,tot); num(tr2,14,tGRN,tot); num(tr2,15,tInv,tot);
            for(int c=16;c<18;c++){Cell nc=tr2.createCell(c);nc.setCellStyle(tot);if(c==16)nc.setCellValue("VARIANCE: "+(tVar.compareTo(BigDecimal.ZERO)>=0?"+":"")+tVar.toPlainString());}
            wb.write(out); return out.toByteArray();
        }
    }

    // =========================================================================
    //  BACKWARD-COMPAT OVERLOADS
    // =========================================================================
    @Transactional(readOnly = true)
    public byte[] generateRFQSummaryExcel(Long rfqId) throws Exception {
        return generateRFQSummaryExcel(rfqId, false, null, null, null, null, null);
    }

    @Transactional(readOnly = true)
    public byte[] generateRFQSummaryExcelForSupplier(Long rfqId) throws Exception {
        return generateRFQSummaryExcel(rfqId, true, null, null, null, null, null);
    }

    // =========================================================================
    //  CORE: RFQ SUMMARY EXCEL
    //
    //  supplierMode = true           → 2 sheets: Overview + Line Items
    //  hierarchyMode (userId+level)  → 4 sheets ONLY:
    //      Sheet 1: RFQ Overview
    //      Sheet 2: Line Items
    //      Sheet 3: Invited Suppliers
    //      Sheet 4: Approval History  ← full hierarchy detail inside
    //  buyer mode (no userId)        → 12 sheets: full report
    // =========================================================================
    @Transactional(readOnly = true)
    public byte[] generateRFQSummaryExcel(
            Long rfqId,
            boolean supplierMode,
            Long userId,
            Integer levelOrder,
            String levelName,
            String viewerName,
            String viewerEmail) throws Exception {

        RFQ rfq = rfqRepository.findByIdWithAllDetails(rfqId)
                .orElseThrow(() -> new RuntimeException("RFQ not found: "+rfqId));
        rfqRepository.findByIdWithItems(rfqId).ifPresent(r -> r.getItems().size());
        rfqRepository.findByIdWithSuppliers(rfqId).ifPresent(r -> r.getSelectedSuppliers().size());

        List<RFQItem>            items     = rfqItemRepository.findByRfqId(rfqId);
        List<RFQSupplier>        suppliers = supplierMode ? Collections.emptyList() : rfqSupplierRepository.findByRFQId(rfqId);
        List<DynamicRFQApproval> approvals = supplierMode ? Collections.emptyList() : approvalRepository.findByRfqIdOrderBySequenceOrderAsc(rfqId);

        boolean hierarchyMode = !supplierMode && userId != null && levelOrder != null;

        List<PurchaseOrder> pos     = Collections.emptyList();
        List<Invoice>       invList = Collections.emptyList();
        List<GRN>           grns    = Collections.emptyList();
        List<ThreeWayMatch> matches = Collections.emptyList();

        if (!supplierMode && !hierarchyMode) {
            try { pos = poRepository.findByRfqId(rfqId); } catch (Exception ignored) {}
            if (!pos.isEmpty()) {
                Long poId = pos.get(0).getId();
                try { grns    = grnRepository.findByPurchaseOrderId(poId); }   catch (Exception ignored) {}
                try { invList = invoiceRepository.findByPoId(poId); }          catch (Exception ignored) {}
                if (!invList.isEmpty()) {
                    Long invId = invList.get(0).getId();
                    try { matches = matchRepository.findByInvoiceId(invId); }  catch (Exception ignored) {}
                }
            }
        }

        try (XSSFWorkbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            CellStyle title   = makeTitleStyle(wb);
            CellStyle hdrNavy = makeHeaderStyle(wb,rgb(0x1e,0x40,0x88));
            CellStyle hdrGrn  = makeHeaderStyle(wb,rgb(0x2e,0x7d,0x32));
            CellStyle hdrOrng = makeHeaderStyle(wb,rgb(0xe6,0x55,0x00));
            CellStyle hdrPrpl = makeHeaderStyle(wb,rgb(0x6a,0x0d,0xad));
            CellStyle lbl     = makeLabelStyle(wb);
            CellStyle val     = makeValueStyle(wb);
            CellStyle amt     = makeAmountStyle(wb);
            CellStyle alt     = makeAltRowStyle(wb);
            CellStyle sts     = makeStatusStyle(wb);
            CellStyle low     = makeLowestStyle(wb);
            CellStyle tot     = makeTotalStyle(wb);
            CellStyle warn    = makeWarnStyle(wb);

            // ── SHEET 1: RFQ Overview ────────────────────────────────────
            XSSFSheet s1 = wb.createSheet("RFQ Overview");
            s1.setColumnWidth(0,6000); s1.setColumnWidth(1,9000); s1.setColumnWidth(2,6000); s1.setColumnWidth(3,9000);
            int row=0;
            String sub = "RFQ: "+rfq.getRfqNumber()
                    +(supplierMode?"   |   Supplier Copy":"")
                    +(hierarchyMode?"   |   Hierarchy View — "+(levelName!=null?levelName:"")+" (Level "+levelOrder+")":"")
                    +"   |   Generated: "+LocalDateTime.now().format(DTTM_FMT);
            row=addBanner(s1,row,"RFQ SUMMARY REPORT",title,sub,4);
            row=addSectionHeader(s1,row,"RFQ INFORMATION",hdrNavy,4);
            row=addLV(s1,row,lbl,val,"RFQ Number",rfq.getRfqNumber(),"RFQ Title",rfq.getRfqTitle());
            row=addLV(s1,row,lbl,val,"Status",en(rfq.getStatus()),"Priority",en(rfq.getPriority()));
            row=addLV(s1,row,lbl,val,"Issue Date",fmt(rfq.getIssueDate()),"Due Date",fmt(rfq.getDueDate()));
            row=addLV(s1,row,lbl,val,"Item Req Date",fmt(rfq.getItemRequiredDate()),"Approval Req",bool(rfq.getApprovalRequired()));
            row=addLV(s1,row,lbl,val,"Payment Terms",s(rfq.getPaymentTerms()),"Delivery Terms",s(rfq.getDeliveryTerms()));
            row=addLV(s1,row,lbl,val,"Tax %",s(rfq.getTaxPercentage()),"Justification",s(rfq.getJustification()));
            row=addLV(s1,row,lbl,val,"Cost Center",s(rfq.getCostCenterCode()),"Project Code",s(rfq.getProjectCode())); row++;
            if(rfq.getBuyer()!=null){Buyer b=rfq.getBuyer();row=addSectionHeader(s1,row,"BUYER INFORMATION",hdrNavy,4);row=addLV(s1,row,lbl,val,"Company",b.getCompanyName(),"Type",s(b.getCompanyType()));row=addLV(s1,row,lbl,val,"Contact",b.getContactPersonName(),"Email",s(b.getContactPersonEmail()));row=addLV(s1,row,lbl,val,"Phone",s(b.getContactPersonPhone()),"GST",s(b.getGstNumber()));row=addLV(s1,row,lbl,val,"City/State",s(b.getCity())+", "+s(b.getState()),"Country",s(b.getCountry()));row++;}
            if(rfq.getLocation()!=null){Location l=rfq.getLocation();row=addSectionHeader(s1,row,"DELIVERY LOCATION",hdrNavy,4);row=addLV(s1,row,lbl,val,"Location Name",l.getLocationName(),"Type",s(l.getLocationType()));row=addLV(s1,row,lbl,val,"Address",s(l.getAddressLine1()),"City",s(l.getCity()));row=addLV(s1,row,lbl,val,"State",s(l.getState()),"Country",s(l.getCountry()));row=addLV(s1,row,lbl,val,"Currency",s(l.getCurrencyCode()),"Contact",s(l.getLocationContactName()));row++;}
            if(rfq.getCreatedByUser()!=null&&!supplierMode){User u=rfq.getCreatedByUser();row=addSectionHeader(s1,row,"CREATED BY",hdrNavy,4);addLV(s1,row,lbl,val,"Name",u.getFirstName()+" "+u.getLastName(),"Email",s(u.getEmail()));}

            // ── SHEET 2: Line Items ──────────────────────────────────────
            XSSFSheet s2 = wb.createSheet("Line Items");
            setWidths(s2,new int[]{1200,3500,8000,7000,6000,2500,3000,3500,4000});
            row=0; row=addBanner(s2,row,"LINE ITEMS",title,rfq.getRfqNumber()+"  —  "+rfq.getRfqTitle()+"  ["+items.size()+" items]",9);
            addHeaderRow(s2,row++,hdrGrn,new String[]{"#","Item Code","Description","Detailed Description","Specifications","Qty","UOM","Unit Price","Required Date"});
            int sl=1;
            for(RFQItem it:items){Row r=s2.createRow(row++);CellStyle cs=altRow(sl,val,alt);str(r,0,String.valueOf(sl++),cs);str(r,1,s(it.getItemCode()),cs);str(r,2,s(it.getItemDescription()),cs);str(r,3,s(it.getItemDescriptionDetailed()),cs);str(r,4,s(it.getSpecifications()),cs);num(r,5,it.getQuantity(),amt);str(r,6,s(it.getUom()),cs);num(r,7,it.getUnitPrice(),amt);str(r,8,fmt(it.getItemRequiredDate()),cs);}

            // ── SHEET 3: Invited Suppliers (buyer + hierarchy) ────────────
            if (!supplierMode) {
                XSSFSheet s3 = wb.createSheet("Invited Suppliers");
                setWidths(s3,new int[]{1200,5500,4000,5000,4000,3500,4000,4000,3800});
                row=0; row=addBanner(s3,row,"INVITED SUPPLIERS",title,rfq.getRfqNumber()+"  ["+suppliers.size()+" suppliers]",9);
                Long awardedId=null; String awardedPO=null;
                if(!pos.isEmpty()&&pos.get(0).getSupplier()!=null){awardedId=pos.get(0).getSupplier().getId();awardedPO=pos.get(0).getPoNumber();}
                final Long fAwardId=awardedId; final String fAwardPO=awardedPO;
                CellStyle awardRow=makeAwardedRowStyle(wb); CellStyle awardAmt=makeAwardedAmtStyle(wb);
                addHeaderRow(s3,row++,hdrNavy,new String[]{"#","Company","Contact Person","Email","Phone","Status","Responded At","Quote Amount","Award Status"});
                s3.createFreezePane(0,row); sl=1;
                for(RFQSupplier rs:suppliers){Supplier sup=rs.getSupplier();Long sid=sup!=null?sup.getId():null;boolean aw=fAwardId!=null&&fAwardId.equals(sid);Row r=s3.createRow(row++);CellStyle cs=aw?awardRow:altRow(sl,val,alt);CellStyle ams=aw?awardAmt:amt;str(r,0,String.valueOf(sl++),cs);str(r,1,sup!=null?s(sup.getCompanyName()):"N/A",cs);str(r,2,sup!=null?s(sup.getContactPersonName()):"N/A",cs);str(r,3,sup!=null?s(sup.getContactPersonEmail()):"N/A",cs);str(r,4,sup!=null?s(sup.getContactPersonPhone()):"N/A",cs);str(r,5,s(rs.getStatus()),aw?cs:sts);str(r,6,fmt(rs.getRespondedAt()),cs);num(r,7,rs.getQuoteAmount(),ams);str(r,8,aw?(fAwardPO!=null?"★ AWARDED  |  PO: "+fAwardPO:"★ AWARDED"):"—",cs);}
                if(fAwardId!=null){row++;Row lr=s3.createRow(row++);s3.addMergedRegion(new CellRangeAddress(row-1,row-1,0,8));Cell lc=lr.createCell(0);lc.setCellValue("★  Gold row = supplier awarded the Purchase Order"+(fAwardPO!=null?"  (PO: "+fAwardPO+")":""));lc.setCellStyle(awardRow);}
            }

            // ── SHEET 4: APPROVAL HISTORY ────────────────────────────────
            // Hierarchy mode: full rich approval view
            // Buyer mode:     standard flat table
            if (!supplierMode) {
                if (hierarchyMode) {
                    buildHierarchyApprovalSheet(wb, rfq, items, approvals, suppliers,
                            levelOrder, levelName, viewerName, viewerEmail,
                            title, hdrNavy, hdrGrn, hdrOrng, hdrPrpl,
                            lbl, val, amt, alt, sts, low, tot, warn);
                } else {
                    XSSFSheet s4 = wb.createSheet("Approval History");
                    setWidths(s4,new int[]{1200,4000,4000,5000,3500,3500,4000,7000});
                    row=0; row=addBanner(s4,row,"APPROVAL HISTORY",title,rfq.getRfqNumber(),8);
                    addHeaderRow(s4,row++,hdrNavy,new String[]{"#","Level","Level Order","Approver","Email","Status","Action Date","Comments / Remarks"});
                    sl=1;
                    for(DynamicRFQApproval ap:approvals){
                        Row r=s4.createRow(row++); CellStyle cs=altRow(sl,val,alt);
                        str(r,0,String.valueOf(sl++),cs);
                        str(r,1,ap.getHierarchyLevel()!=null?ap.getHierarchyLevel().getLevelName():"N/A",cs);
                        str(r,2,ap.getHierarchyLevel()!=null?String.valueOf(ap.getHierarchyLevel().getLevelOrder()):"N/A",cs);
                        str(r,3,ap.getApproverUser()!=null?ap.getApproverUser().getFullName():"N/A",cs);
                        str(r,4,ap.getApproverUser()!=null?s(ap.getApproverUser().getEmail()):"N/A",cs);
                        str(r,5,ap.getStatus()!=null?ap.getStatus().name():"N/A",sts);
                        str(r,6,fmt(ap.getActionDate()),cs);
                        String rem=s(ap.getComments());
                        if(ap.getHoldRemarks()!=null)    rem+=" | HOLD: "+ap.getHoldRemarks();
                        if(ap.getRejectRemarks()!=null)  rem+=" | REJECT: "+ap.getRejectRemarks();
                        if(ap.getReleaseRemarks()!=null) rem+=" | RELEASE: "+ap.getReleaseRemarks();
                        str(r,7,rem,cs);
                    }
                    if(approvals.isEmpty()){Row r=s4.createRow(row);str(r,0,"No approval history recorded yet.",val);}
                }
            }

            // ── SHEETS 5-12: Full buyer report only ──────────────────────
            if (!supplierMode && !hierarchyMode) {
                buildBuyerOnlySheets(wb, rfq, pos, invList, grns, matches,
                        title, hdrNavy, hdrGrn, hdrOrng, hdrPrpl,
                        lbl, val, amt, alt, sts, low, tot, warn);
            }

            wb.write(out);
            return out.toByteArray();
        }
    }

    // =========================================================================
    //  HIERARCHY APPROVAL HISTORY SHEET
    //  (This IS Sheet 4 "Approval History" for hierarchy users)
    //
    //  SECTION A — RFQ Creator Details
    //  SECTION B — Full Approval Chain (levels <= user's level, colour coded)
    //  SECTION C — Previous Approver detail
    //  SECTION D — Your Position
    //  SECTION E — Next Approver (name + level only)
    //  SECTION F — RFQ Line Items summary
    // =========================================================================
    private void buildHierarchyApprovalSheet(
            XSSFWorkbook wb, RFQ rfq, List<RFQItem> items,
            List<DynamicRFQApproval> allApprovals, List<RFQSupplier> suppliers,
            int userLevelOrder, String levelName, String viewerName, String viewerEmail,
            CellStyle title, CellStyle hdrNavy, CellStyle hdrGrn, CellStyle hdrOrng, CellStyle hdrPrpl,
            CellStyle lbl, CellStyle val, CellStyle amt, CellStyle alt,
            CellStyle sts, CellStyle low, CellStyle tot, CellStyle warn) {

        String lvlName    = (levelName   != null && !levelName.isBlank())   ? levelName   : "Hierarchy Level";
        String vwrName    = (viewerName  != null && !viewerName.isBlank())  ? viewerName  : "Hierarchy User";
        String vwrEmail   = (viewerEmail != null && !viewerEmail.isBlank()) ? viewerEmail : "N/A";

        // Extra header colours
        CellStyle hdrBlue2  = makeHeaderStyle(wb, rgb(0x18,0x5f,0xa5));
        CellStyle hdrAmber  = makeHeaderStyle(wb, rgb(0xba,0x75,0x17));
        CellStyle hdrTeal   = makeHeaderStyle(wb, rgb(0x00,0x6d,0x77));
        CellStyle hdrDkGrn  = makeHeaderStyle(wb, rgb(0x1b,0x5e,0x20));

        // Row fill styles
        CellStyle creatorBg  = filled(wb, rgb(0xe6,0xf1,0xfb), rgb(0x0c,0x44,0x7c), false);
        CellStyle prevBg     = filled(wb, rgb(0xfa,0xee,0xda), rgb(0x63,0x38,0x06), false);
        CellStyle youBg      = filled(wb, rgb(0xe8,0xf0,0xfe), rgb(0x1e,0x40,0x88), true);
        CellStyle nextBg     = filled(wb, rgb(0xf3,0xe5,0xf5), rgb(0x4a,0x00,0x72), false);
        CellStyle approvedBg = filled(wb, rgb(0xd4,0xed,0xda), rgb(0x1a,0x5c,0x1a), false);
        CellStyle rejectedBg = filled(wb, rgb(0xf8,0xd7,0xda), rgb(0x72,0x1c,0x24), false);
        CellStyle holdBg     = filled(wb, rgb(0xff,0xf3,0xcd), rgb(0x85,0x64,0x04), false);
        CellStyle pendingBg  = filled(wb, rgb(0xe2,0xe3,0xe5), rgb(0x38,0x3d,0x41), false);
        CellStyle noteBg     = filled(wb, rgb(0xff,0xf9,0xc4), rgb(0x44,0x44,0x44), false);
        CellStyle finalBg    = filled(wb, rgb(0xd4,0xed,0xda), rgb(0x1a,0x5c,0x1a), true);
        CellStyle pendActBg  = filled(wb, rgb(0xfe,0xf3,0xcd), rgb(0x7d,0x4a,0x00), true);

        // Sheet: 7 columns
        XSSFSheet sheet = wb.createSheet("Approval History");
        sheet.setColumnWidth(0,4500); sheet.setColumnWidth(1,7500); sheet.setColumnWidth(2,5000);
        sheet.setColumnWidth(3,7500); sheet.setColumnWidth(4,4000);
        sheet.setColumnWidth(5,4500); sheet.setColumnWidth(6,10000);

        int row = 0;

        // ── MAIN BANNER ────────────────────────────────────────────────
        row = addBanner(sheet, row, "APPROVAL HISTORY  —  HIERARCHY VIEW", title,
                "RFQ: "+rfq.getRfqNumber()+"  ·  "+s(rfq.getRfqTitle())
                +"  |  Reviewer: "+vwrName+" ("+lvlName+" · Level "+userLevelOrder+")"
                +"  |  Generated: "+LocalDateTime.now().format(DTTM_FMT), 7);

        // ── VISIBILITY NOTE ────────────────────────────────────────────
        Row noteRow = sheet.createRow(row++);
        sheet.addMergedRegion(new CellRangeAddress(row-1,row-1,0,6));
        Cell noteCell = noteRow.createCell(0);
        noteCell.setCellValue("  VISIBILITY: This report shows your level ("+lvlName+" · Level "+userLevelOrder
                +") and all levels that approved before you. Levels with lower authority numbers (higher in chain) are not shown.");
        noteCell.setCellStyle(noteBg);
        row++;

        // Prepare approval data
        // IMPORTANT: Approval flow is DESCENDING — levelOrder 30 acts FIRST, then 20, then 10.
        // Higher levelOrder = lower authority = earlier in chain.
        // Lower levelOrder  = higher authority = later in chain (final approver).
        //
        // So:
        //   sorted          = descending (30, 20, 10) — order they actually act
        //   visible chain   = levels >= userLevelOrder (those who acted before current user)
        //   previousApprover = highest level strictly > userLevelOrder (acted just before user)
        //   nextApprover    = lowest level strictly < userLevelOrder  (acts after user)

        List<DynamicRFQApproval> sorted = allApprovals.stream()
                .filter(a -> a.getHierarchyLevel() != null)
                .sorted(Comparator.comparingInt((DynamicRFQApproval a) -> a.getHierarchyLevel().getLevelOrder()).reversed())
                .collect(Collectors.toList());

        // Visible = user's own level + all levels above user (higher levelOrder = acted first)
        List<DynamicRFQApproval> visible = sorted.stream()
                .filter(a -> a.getHierarchyLevel().getLevelOrder() >= userLevelOrder)
                .collect(Collectors.toList());

        // Previous approver = the one who acted just BEFORE the current user
        // = highest levelOrder strictly greater than user's level (closest predecessor in chain)
        DynamicRFQApproval prevApprover = sorted.stream()
                .filter(a -> a.getHierarchyLevel().getLevelOrder() > userLevelOrder)
                .min(Comparator.comparingInt(a -> a.getHierarchyLevel().getLevelOrder()))
                .orElse(null);

        DynamicRFQApproval mySlot = sorted.stream()
                .filter(a -> a.getHierarchyLevel().getLevelOrder() == userLevelOrder)
                .findFirst().orElse(null);

        // Next approver = the one who acts AFTER the current user
        // = lowest levelOrder strictly less than user's level (higher authority, acts next)
        DynamicRFQApproval nextApprover = sorted.stream()
                .filter(a -> a.getHierarchyLevel().getLevelOrder() < userLevelOrder)
                .max(Comparator.comparingInt(a -> a.getHierarchyLevel().getLevelOrder()))
                .orElse(null);

        // ══════════════════════════════════════════════════════════════
        //  SECTION A — RFQ CREATOR DETAILS
        // ══════════════════════════════════════════════════════════════
        row = addSectionHeader(sheet, row, "SECTION A  ·  RFQ CREATOR DETAILS", hdrBlue2, 7);

        if (rfq.getCreatedByUser() != null) {
            User c = rfq.getCreatedByUser();
            String cName = (safeStr(c.getFirstName())+" "+safeStr(c.getLastName())).trim();
            if (cName.isBlank()) cName = "N/A";

            row = addLV7(sheet,row,lbl,creatorBg,"Creator Name", cName,"Email",s(c.getEmail()));
            row = addLV7(sheet,row,lbl,creatorBg,"Phone",s(c.getPhone()),"Company",rfq.getBuyer()!=null?rfq.getBuyer().getCompanyName():"N/A");
            row = addLV7(sheet,row,lbl,creatorBg,"Created On",fmt(rfq.getCreatedAt()),"Location",rfq.getLocation()!=null?rfq.getLocation().getLocationName():"N/A");
            row = addLV7(sheet,row,lbl,creatorBg,"RFQ Number",rfq.getRfqNumber(),"RFQ Title",s(rfq.getRfqTitle()));
            row = addLV7(sheet,row,lbl,creatorBg,"RFQ Status",en(rfq.getStatus()),"Priority",en(rfq.getPriority()));
            row = addLV7(sheet,row,lbl,creatorBg,"Issue Date",fmt(rfq.getIssueDate()),"Due Date",fmt(rfq.getDueDate()));
            row = addLV7(sheet,row,lbl,creatorBg,"Suppliers Invited",String.valueOf(suppliers!=null?suppliers.size():0),"Line Items",String.valueOf(items.size()));
            row = addLV7(sheet,row,lbl,creatorBg,"Payment Terms",s(rfq.getPaymentTerms()),"Delivery Terms",s(rfq.getDeliveryTerms()));
            if (rfq.getJustification()!=null&&!rfq.getJustification().isBlank()) {
                Row jr=sheet.createRow(row++);
                Cell jl=jr.createCell(0);jl.setCellValue("Justification");jl.setCellStyle(lbl);
                Cell jv=jr.createCell(1);jv.setCellValue(s(rfq.getJustification()));jv.setCellStyle(creatorBg);
                sheet.addMergedRegion(new CellRangeAddress(row-1,row-1,1,6));
            }
        } else {
            Row nr=sheet.createRow(row++);sheet.addMergedRegion(new CellRangeAddress(row-1,row-1,0,6));
            Cell nc=nr.createCell(0);nc.setCellValue("Creator information not available.");nc.setCellStyle(noteBg);
        }
        row++;

        // ══════════════════════════════════════════════════════════════
        //  SECTION B — FULL APPROVAL CHAIN (levels <= user)
        // ══════════════════════════════════════════════════════════════
        row = addSectionHeader(sheet, row,
                "SECTION B  ·  APPROVAL CHAIN  (Approval flows: Level "+userLevelOrder+" → Level ... → Final Authority  ·  Levels below yours are hidden)",
                hdrNavy, 7);

        if (visible.isEmpty()) {
            Row er=sheet.createRow(row++);sheet.addMergedRegion(new CellRangeAddress(row-1,row-1,0,6));
            Cell ec=er.createCell(0);ec.setCellValue("No approval actions recorded yet for levels visible to you.");ec.setCellStyle(noteBg);
        } else {
            // Header row
            Row chainHdr = sheet.createRow(row++); chainHdr.setHeightInPoints(22);
            String[] chainCols={"#","Level Name","Level Order","Approver Name","Email","Status","Action Date  |  Remarks"};
            for(int c=0;c<chainCols.length;c++){Cell hc=chainHdr.createCell(c);hc.setCellValue(chainCols[c]);hc.setCellStyle(hdrNavy);}
            sheet.createFreezePane(0,row);

            // Creator marker row
            if (rfq.getCreatedByUser()!=null) {
                User c=rfq.getCreatedByUser();
                String cn=((safeStr(c.getFirstName()))+" "+(safeStr(c.getLastName()))).trim();
                Row cr=sheet.createRow(row++); cr.setHeightInPoints(20);
                str(cr,0,"—",creatorBg); str(cr,1,"RFQ Creator",creatorBg); str(cr,2,"—",creatorBg);
                str(cr,3,cn.isBlank()?"N/A":cn,creatorBg); str(cr,4,s(c.getEmail()),creatorBg);
                str(cr,5,"INITIATED",makeInitiatedSts(wb));
                str(cr,6,fmt(rfq.getCreatedAt())+"  |  RFQ created and submitted for approval",creatorBg);
            }

            // Approval level rows (colour coded by status)
            int chainSl=1;
            for (DynamicRFQApproval ap:visible) {
                Row r=sheet.createRow(row++); r.setHeightInPoints(22);
                CellStyle rowBg=statusRowBg(ap.getStatus(),approvedBg,rejectedBg,holdBg,pendingBg,chainSl,val,alt);
                CellStyle stsCel=buildStatusCell(wb,ap.getStatus());
                str(r,0,String.valueOf(chainSl++),rowBg);
                str(r,1,ap.getHierarchyLevel()!=null?ap.getHierarchyLevel().getLevelName():"N/A",rowBg);
                str(r,2,ap.getHierarchyLevel()!=null?String.valueOf(ap.getHierarchyLevel().getLevelOrder()):"N/A",rowBg);
                str(r,3,ap.getApproverUser()!=null?ap.getApproverUser().getFullName():"N/A",rowBg);
                str(r,4,ap.getApproverUser()!=null?s(ap.getApproverUser().getEmail()):"N/A",rowBg);
                str(r,5,ap.getStatus()!=null?ap.getStatus().name():"PENDING",stsCel);
                String dt=fmt(ap.getActionDate()!=null?ap.getActionDate():ap.getHoldDate());
                String rm=buildRemarks(ap);
                str(r,6,rm.isEmpty()?dt:dt+"  |  "+rm,rowBg);
            }

            // "You are here" row
            Row youRow=sheet.createRow(row++); youRow.setHeightInPoints(22);
            str(youRow,0,"►",youBg); str(youRow,1,lvlName,youBg);
            str(youRow,2,String.valueOf(userLevelOrder),youBg); str(youRow,3,vwrName,youBg);
            str(youRow,4,vwrEmail,youBg);
            str(youRow,5,mySlot!=null&&mySlot.getStatus()!=null?mySlot.getStatus().name():"PENDING",
                    mySlot!=null?buildStatusCell(wb,mySlot.getStatus()):pendingBg);
            String youRem="◄ YOUR LEVEL";
            if(mySlot!=null){String mr=buildRemarks(mySlot);youRem+=mr.isEmpty()?"  |  "+en(mySlot.getStatus()):"  |  "+mr;}
            else youRem+="  |  Action pending from you";
            str(youRow,6,youRem,youBg);
        }
        row++;

        // ══════════════════════════════════════════════════════════════
        //  SECTION C — PREVIOUS APPROVER
        // ══════════════════════════════════════════════════════════════
        row = addSectionHeader(sheet, row, "SECTION C  ·  PREVIOUS APPROVER  (Approved just before you — Level "+
                (prevApprover!=null&&prevApprover.getHierarchyLevel()!=null?String.valueOf(prevApprover.getHierarchyLevel().getLevelOrder()):"—")+")", hdrAmber, 7);

        if (prevApprover==null) {
            Row nr=sheet.createRow(row++);sheet.addMergedRegion(new CellRangeAddress(row-1,row-1,0,6));
            Cell nc=nr.createCell(0);nc.setCellValue("  No previous approver — you are the first approval level in this workflow.");nc.setCellStyle(noteBg);
        } else {
            row=addLV7(sheet,row,lbl,prevBg,"Approver Name",prevApprover.getApproverUser()!=null?prevApprover.getApproverUser().getFullName():"N/A","Email",prevApprover.getApproverUser()!=null?s(prevApprover.getApproverUser().getEmail()):"N/A");
            row=addLV7(sheet,row,lbl,prevBg,"Hierarchy Level",prevApprover.getHierarchyLevel()!=null?prevApprover.getHierarchyLevel().getLevelName():"N/A","Level Order",prevApprover.getHierarchyLevel()!=null?String.valueOf(prevApprover.getHierarchyLevel().getLevelOrder()):"N/A");
            row=addLV7(sheet,row,lbl,prevBg,"Decision",prevApprover.getStatus()!=null?prevApprover.getStatus().name():"PENDING","Action Date",fmt(prevApprover.getActionDate()!=null?prevApprover.getActionDate():prevApprover.getHoldDate()));
            String pr=buildRemarks(prevApprover);
            if(!pr.isEmpty()){Row rr=sheet.createRow(row++);Cell rl=rr.createCell(0);rl.setCellValue("Remarks / Comments");rl.setCellStyle(lbl);Cell rv=rr.createCell(1);rv.setCellValue(pr);rv.setCellStyle(prevBg);sheet.addMergedRegion(new CellRangeAddress(row-1,row-1,1,6));}
        }
        row++;

        // ══════════════════════════════════════════════════════════════
        //  SECTION D — YOUR POSITION
        // ══════════════════════════════════════════════════════════════
        row = addSectionHeader(sheet, row, "SECTION D  ·  YOUR POSITION IN THIS APPROVAL WORKFLOW", hdrTeal, 7);
        row=addLV7(sheet,row,lbl,youBg,"Your Name",vwrName,"Email",vwrEmail);
        row=addLV7(sheet,row,lbl,youBg,"Hierarchy Level",lvlName,"Level Order",String.valueOf(userLevelOrder));
        if (mySlot!=null) {
            row=addLV7(sheet,row,lbl,youBg,"Your Decision",mySlot.getStatus()!=null?mySlot.getStatus().name():"PENDING","Action Date",fmt(mySlot.getActionDate()!=null?mySlot.getActionDate():mySlot.getHoldDate()));
            String mr=buildRemarks(mySlot);
            if(!mr.isEmpty()){Row rr=sheet.createRow(row++);Cell rl=rr.createCell(0);rl.setCellValue("Your Remarks");rl.setCellStyle(lbl);Cell rv=rr.createCell(1);rv.setCellValue(mr);rv.setCellStyle(youBg);sheet.addMergedRegion(new CellRangeAddress(row-1,row-1,1,6));}
        } else {
            Row pr=sheet.createRow(row++);sheet.addMergedRegion(new CellRangeAddress(row-1,row-1,0,6));
            Cell pc=pr.createCell(0);pc.setCellValue("  ⏳ ACTION PENDING — This RFQ is awaiting your review and decision.");pc.setCellStyle(pendActBg);
        }
        row++;

        // ══════════════════════════════════════════════════════════════
        //  SECTION E — NEXT APPROVER (name + level only)
        // ══════════════════════════════════════════════════════════════
        row = addSectionHeader(sheet, row, "SECTION E  ·  NEXT APPROVER  (After your approval)", hdrPrpl, 7);

        if (nextApprover==null) {
            Row fr=sheet.createRow(row++);sheet.addMergedRegion(new CellRangeAddress(row-1,row-1,0,6));
            Cell fc=fr.createCell(0);fc.setCellValue("  ✅ FINAL APPROVER — You are the last level in this approval chain. No higher levels exist.");fc.setCellStyle(finalBg);
        } else {
            row=addLV7(sheet,row,lbl,nextBg,"Next Approver",nextApprover.getApproverUser()!=null?nextApprover.getApproverUser().getFullName():"N/A","Hierarchy Level",nextApprover.getHierarchyLevel()!=null?nextApprover.getHierarchyLevel().getLevelName():"N/A");
            row=addLV7(sheet,row,lbl,nextBg,"Level Order",nextApprover.getHierarchyLevel()!=null?String.valueOf(nextApprover.getHierarchyLevel().getLevelOrder()):"N/A","Note","Your approval must be given first before this level is notified");
        }
        row++;

        // ══════════════════════════════════════════════════════════════
        //  SECTION F — RFQ LINE ITEMS
        // ══════════════════════════════════════════════════════════════
        row = addSectionHeader(sheet, row, "SECTION F  ·  RFQ LINE ITEMS  ["+items.size()+" item"+(items.size()==1?"":"s")+"]", hdrDkGrn, 7);

        if (items.isEmpty()) {
            Row er=sheet.createRow(row++);sheet.addMergedRegion(new CellRangeAddress(row-1,row-1,0,6));
            Cell ec=er.createCell(0);ec.setCellValue("No line items found for this RFQ.");ec.setCellStyle(noteBg);
        } else {
            String[] iCols={"#","Item Code","Description","Specifications","Qty","UOM","Unit Price  |  Required Date"};
            Row iHdr=sheet.createRow(row++); iHdr.setHeightInPoints(20);
            for(int c=0;c<iCols.length;c++){Cell hc=iHdr.createCell(c);hc.setCellValue(iCols[c]);hc.setCellStyle(hdrDkGrn);}
            int iSl=1;
            for(RFQItem item:items){
                Row r=sheet.createRow(row++); CellStyle cs=altRow(iSl,val,alt);
                str(r,0,String.valueOf(iSl++),cs); str(r,1,s(item.getItemCode()),cs);
                str(r,2,s(item.getItemDescription()),cs); str(r,3,s(item.getSpecifications()),cs);
                num(r,4,item.getQuantity(),amt); str(r,5,s(item.getUom()),cs);
                StringBuilder cb=new StringBuilder();
                if(item.getUnitPrice()!=null&&item.getUnitPrice().compareTo(BigDecimal.ZERO)>0) cb.append("Rate: ").append(item.getUnitPrice().toPlainString());
                if(item.getItemRequiredDate()!=null){if(cb.length()>0)cb.append("  |  ");cb.append("Req: ").append(fmt(item.getItemRequiredDate()));}
                str(r,6,cb.length()>0?cb.toString():"—",cs);
            }
        }
        sheet.createFreezePane(0,5);
    }

    // =========================================================================
    //  BUYER-ONLY SHEETS 5-12  (PO, PO Lines, Invoice, Inv Lines, GRN,
    //                           GRN Lines, 3-Way Match, Match Lines)
    // =========================================================================
    private void buildBuyerOnlySheets(
            XSSFWorkbook wb, RFQ rfq,
            List<PurchaseOrder> pos, List<Invoice> invList,
            List<GRN> grns, List<ThreeWayMatch> matches,
            CellStyle title, CellStyle hdrNavy, CellStyle hdrGrn, CellStyle hdrOrng, CellStyle hdrPrpl,
            CellStyle lbl, CellStyle val, CellStyle amt, CellStyle alt,
            CellStyle sts, CellStyle low, CellStyle tot, CellStyle warn) throws Exception {

        int row; int sl;

        // ── SHEET 5: Purchase Order ──────────────────────────────────
        XSSFSheet s5 = wb.createSheet("Purchase Order");
        s5.setColumnWidth(0,6000);s5.setColumnWidth(1,9000);s5.setColumnWidth(2,6000);s5.setColumnWidth(3,9000);
        row=0;
        if(pos.isEmpty()){row=addBanner(s5,row,"PURCHASE ORDER",title,"No PO raised for this RFQ yet.",4);}
        else{
            PurchaseOrder po=pos.get(0);
            row=addBanner(s5,row,"PURCHASE ORDER",title,"PO: "+s(po.getPoNumber())+"  |  Status: "+(po.getStatus()!=null?po.getStatus().name():"—")+"  |  RFQ: "+rfq.getRfqNumber(),4);
            row=addSectionHeader(s5,row,"PO INFORMATION",hdrGrn,4);
            row=addLV(s5,row,lbl,val,"PO Number",s(po.getPoNumber()),"PO Date",fmt(po.getPoDate()));
            row=addLV(s5,row,lbl,val,"Status",po.getStatus()!=null?po.getStatus().name():"—","Approval Status",po.getApprovalStatus()!=null?po.getApprovalStatus().name():"—");
            row=addLV(s5,row,lbl,val,"Currency",s(po.getCurrencyCode())+" "+s(po.getCurrencySymbol()),"Amount in Words",s(po.getAmountInWords()));
            row=addLV(s5,row,lbl,val,"Payment Terms",s(po.getPaymentTerms()),"Delivery Terms",s(po.getDeliveryTerms()));
            row=addLV(s5,row,lbl,val,"Approved By",s(po.getApprovedByName()),"Designation",s(po.getApprovedByDesignation()));
            row=addLV(s5,row,lbl,val,"Approval Date",fmt(po.getApprovalDate()),"Buyer Remarks",s(po.getBuyerRemarks())); row++;
            if(po.getSupplier()!=null){Supplier sup=po.getSupplier();row=addSectionHeader(s5,row,"SUPPLIER",hdrGrn,4);row=addLV(s5,row,lbl,val,"Company",s(sup.getCompanyName()),"Email",s(sup.getContactPersonEmail()));row=addLV(s5,row,lbl,val,"Contact",s(sup.getContactPersonName()),"Phone",s(sup.getContactPersonPhone()));row++;}
            if(po.getDeliveryLocation()!=null){Location dl=po.getDeliveryLocation();row=addSectionHeader(s5,row,"DELIVERY LOCATION",hdrGrn,4);row=addLV(s5,row,lbl,val,"Name",dl.getLocationName(),"City",s(dl.getCity()));row=addLV(s5,row,lbl,val,"State",s(dl.getState()),"Country",s(dl.getCountry()));row++;}
            row=addSectionHeader(s5,row,"FINANCIAL SUMMARY",hdrGrn,4);
            row=addLV(s5,row,lbl,val,"Subtotal",s(po.getSubtotal()),"Tax Amount",s(po.getTaxAmount()));
            row=addLV(s5,row,lbl,val,"Grand Total",s(po.getGrandTotal()),"Tax %",s(po.getTaxPercentage()));
        }

        // ── SHEET 6: PO Line Items ───────────────────────────────────
        if(!pos.isEmpty()){
            PurchaseOrder po=pos.get(0); List<POLineItem> poLines=po.getLineItems();
            XSSFSheet s6=wb.createSheet("PO Line Items");
            setWidths(s6,new int[]{1200,3000,7000,2500,2000,2500,4000,2000,2000,4500,4000});
            row=0;row=addBanner(s6,row,"PO LINE ITEMS",title,s(po.getPoNumber())+"  ["+poLines.size()+" items]  |  Currency: "+s(po.getCurrencyCode()),11);
            addHeaderRow(s6,row++,hdrGrn,new String[]{"#","Item Code","Description","Specifications","UOM","Qty","Unit Rate","Disc %","Tax %","Line Total","Brand/Make/Model"});
            sl=1;
            for(POLineItem li:poLines){Row r=s6.createRow(row++);CellStyle cs=altRow(sl,val,alt);str(r,0,String.valueOf(sl++),cs);str(r,1,s(li.getItemCode()),cs);str(r,2,s(li.getItemDescription()),cs);str(r,3,s(li.getSpecifications()),cs);str(r,4,s(li.getUom()),cs);num(r,5,li.getQuantity(),amt);num(r,6,li.getUnitRate(),amt);num(r,7,li.getDiscountPercentage(),amt);num(r,8,li.getTaxPercentage(),amt);num(r,9,li.getLineTotal(),amt);str(r,10,s(li.getBrandMakeModel()),cs);}
            row++;
            Object[][] pTots={{"Subtotal",po.getSubtotal()},{"Tax Amount",po.getTaxAmount()},{"GRAND TOTAL",po.getGrandTotal()}};
            for(int t=0;t<pTots.length;t++){Row tr=s6.createRow(row++);boolean ig=(t==pTots.length-1);Cell lc=tr.createCell(9);lc.setCellValue((String)pTots[t][0]);lc.setCellStyle(ig?tot:lbl);Cell vc=tr.createCell(10);if(pTots[t][1] instanceof BigDecimal)vc.setCellValue(((BigDecimal)pTots[t][1]).doubleValue());else vc.setCellValue(0);vc.setCellStyle(ig?tot:amt);}
        }

        // ── SHEET 7: Invoice ─────────────────────────────────────────
        XSSFSheet s7=wb.createSheet("Invoice");
        s7.setColumnWidth(0,6000);s7.setColumnWidth(1,9000);s7.setColumnWidth(2,6000);s7.setColumnWidth(3,9000);
        row=0;
        if(invList.isEmpty()){row=addBanner(s7,row,"INVOICE",title,"No invoice submitted yet for this RFQ.",4);}
        else{
            Invoice inv=invList.get(0);
            row=addBanner(s7,row,"INVOICE",title,"Invoice: "+s(inv.getInvoiceNumber())+"  |  Status: "+(inv.getStatus()!=null?inv.getStatus().name():"—")+"  |  RFQ: "+rfq.getRfqNumber(),4);
            row=addSectionHeader(s7,row,"INVOICE DETAILS",hdrPrpl,4);
            row=addLV(s7,row,lbl,val,"Invoice Number",s(inv.getInvoiceNumber()),"Status",inv.getStatus()!=null?inv.getStatus().name():"—");
            row=addLV(s7,row,lbl,val,"Invoice Date",fmt(inv.getInvoiceDate()),"Due Date",fmt(inv.getDueDate()));
            row=addLV(s7,row,lbl,val,"PO Number",s(inv.getPoNumber()),"Currency",s(inv.getCurrency()));
            row=addLV(s7,row,lbl,val,"Supplier",s(inv.getSupplierName()),"Supplier Email",s(inv.getSupplierEmail()));
            row=addLV(s7,row,lbl,val,"Buyer Company",s(inv.getBuyerCompanyName()),"Payment Terms",s(inv.getPaymentTerms()));
            row=addLV(s7,row,lbl,val,"Subtotal",s(inv.getSubtotal()),"Tax Amount",s(inv.getTaxAmount()));
            row=addLV(s7,row,lbl,val,"Total Amount",s(inv.getTotalAmount()),"Approved By",s(inv.getApprovedRejectedBy()));
            row=addLV(s7,row,lbl,val,"Approval Date",fmt(inv.getApprovedRejectedAt()),"Approval Remarks",s(inv.getApprovalRemarks())); row++;
            if(inv.getBankName()!=null){row=addSectionHeader(s7,row,"BANK DETAILS",hdrPrpl,4);row=addLV(s7,row,lbl,val,"Bank Name",s(inv.getBankName()),"Account Name",s(inv.getBankAccountName()));row=addLV(s7,row,lbl,val,"Account Number",s(inv.getBankAccountNumber()),"IFSC Code",s(inv.getBankIfscCode()));row=addLV(s7,row,lbl,val,"SWIFT Code",s(inv.getBankSwiftCode()),"Notes",s(inv.getNotes()));row++;}
        }

        // ── SHEET 8: Invoice Line Items ──────────────────────────────
        if(!invList.isEmpty()){
            Invoice inv=invList.get(0); List<InvoiceLineItem> iLines=inv.getLineItems();
            XSSFSheet s8=wb.createSheet("Invoice Line Items");
            setWidths(s8,new int[]{1200,3000,7000,2500,2000,2500,4000,2000,2000,4500});
            row=0;row=addBanner(s8,row,"INVOICE LINE ITEMS",title,s(inv.getInvoiceNumber())+"  ["+iLines.size()+" items]",10);
            addHeaderRow(s8,row++,hdrPrpl,new String[]{"#","Item Code","Description","HSN/SAC","UOM","Quantity","Unit Price","Disc %","Tax %","Line Total"});
            sl=1;
            for(InvoiceLineItem li:iLines){Row r=s8.createRow(row++);CellStyle cs=altRow(sl,val,alt);str(r,0,String.valueOf(sl++),cs);str(r,1,s(li.getItemCode()),cs);str(r,2,s(li.getItemDescription()),cs);str(r,3,s(li.getHsnSacCode()),cs);str(r,4,s(li.getUom()),cs);num(r,5,li.getQuantity(),amt);num(r,6,li.getUnitPrice(),amt);num(r,7,li.getDiscountPercentage(),amt);num(r,8,li.getTaxPercentage(),amt);num(r,9,li.getLineTotal(),amt);}
            row++;
            Object[][] iTots={{"Subtotal",inv.getSubtotal()},{"Tax Amount",inv.getTaxAmount()},{"TOTAL AMOUNT",inv.getTotalAmount()}};
            for(int t=0;t<iTots.length;t++){Row tr=s8.createRow(row++);boolean ig=(t==iTots.length-1);Cell lc=tr.createCell(8);lc.setCellValue((String)iTots[t][0]);lc.setCellStyle(ig?tot:lbl);Cell vc=tr.createCell(9);if(iTots[t][1] instanceof BigDecimal)vc.setCellValue(((BigDecimal)iTots[t][1]).doubleValue());else vc.setCellValue(0);vc.setCellStyle(ig?tot:amt);}
        }

        // ── SHEET 9: GRN ─────────────────────────────────────────────
        XSSFSheet s9=wb.createSheet("GRN");
        s9.setColumnWidth(0,6000);s9.setColumnWidth(1,9000);s9.setColumnWidth(2,6000);s9.setColumnWidth(3,9000);
        row=0;
        if(grns.isEmpty()){row=addBanner(s9,row,"GOODS RECEIPT NOTE",title,"No GRN created yet for this RFQ.",4);}
        else{
            row=addBanner(s9,row,"GOODS RECEIPT NOTE — ALL DELIVERY BATCHES",title,rfq.getRfqNumber()+"  |  Total GRN Batches: "+grns.size()+"  |  PO: "+(pos.isEmpty()?"—":s(pos.get(0).getPoNumber())),4);
            row=addSectionHeader(s9,row,"DELIVERY BATCH SUMMARY",hdrOrng,4);
            Row sh=s9.createRow(row++);sh.setHeightInPoints(18);
            str(sh,0,"Batch #",hdrOrng);str(sh,1,"GRN Number",hdrOrng);str(sh,2,"Received Date",hdrOrng);str(sh,3,"Status",hdrOrng);
            int bn=1;
            for(GRN grn:grns){Row sr=s9.createRow(row++);CellStyle cs=altRow(bn,val,alt);str(sr,0,"Batch "+bn++,cs);str(sr,1,s(grn.getGrnNumber()),cs);str(sr,2,fmt(grn.getReceivedDate()),cs);str(sr,3,grn.getStatus()!=null?grn.getStatus().name():"—",sts);}
            row++;bn=1;
            for(GRN grn:grns){row=addSectionHeader(s9,row,"BATCH "+bn+" — "+s(grn.getGrnNumber())+"  |  Status: "+(grn.getStatus()!=null?grn.getStatus().name():"—"),hdrOrng,4);row=addLV(s9,row,lbl,val,"GRN Number",s(grn.getGrnNumber()),"Status",grn.getStatus()!=null?grn.getStatus().name():"—");row=addLV(s9,row,lbl,val,"Received Date",fmt(grn.getReceivedDate()),"PO Number",s(grn.getPoNumber()));row=addLV(s9,row,lbl,val,"Supplier",s(grn.getSupplierName()),"Invoice Number",s(grn.getInvoiceNumber()));row=addLV(s9,row,lbl,val,"Received By",s(grn.getReceivedByName()),"QA Inspector",s(grn.getInspectedByName()));row=addLV(s9,row,lbl,val,"Approved By",s(grn.getApprovedByName()),"Approved At",fmt(grn.getApprovedAt()));row=addLV(s9,row,lbl,val,"Transporter",s(grn.getTransporterName()),"Vehicle No.",s(grn.getVehicleNumber()));row=addLV(s9,row,lbl,val,"Challan No.",s(grn.getDeliveryChallanNumber()),"LR Number",s(grn.getLrNumber()));row=addLV(s9,row,lbl,val,"Total Ordered",s(grn.getTotalOrderedValue()),"Total Received",s(grn.getTotalReceivedValue()));row=addLV(s9,row,lbl,val,"Total Rejected",s(grn.getTotalRejectedValue()),"Remarks",s(grn.getRemarks()));row++;bn++;}
        }

        // ── SHEET 10: GRN Line Items ─────────────────────────────────
        if(!grns.isEmpty()){
            XSSFSheet s10=wb.createSheet("GRN Line Items");
            setWidths(s10,new int[]{1200,1800,3000,6500,2000,2500,2500,2500,2500,2500,4000,3500,5000});
            row=0;
            int tlc=grns.stream().mapToInt(g->g.getLineItems()!=null?g.getLineItems().size():0).sum();
            row=addBanner(s10,row,"GRN LINE ITEMS — ALL DELIVERY BATCHES",title,rfq.getRfqNumber()+"  |  "+grns.size()+" GRN Batch(es)  |  "+tlc+" Total Line Item(s)",13);
            addHeaderRow(s10,row++,hdrOrng,new String[]{"#","Batch","Item Code","Description","UOM","Ordered","Received","Defective","Rejected","Accepted","PO Rate","Accepted Value","QA Remarks"});
            s10.createFreezePane(0,row);
            CellStyle bsl=makeBatchSubtotalLabelStyle(wb); CellStyle bsa=makeBatchSubtotalAmtStyle(wb);
            int bn=1;int ln=1;
            for(GRN grn:grns){
                List<GRNLineItem> gLines=grn.getLineItems();
                if(gLines==null||gLines.isEmpty()){bn++;continue;}
                Row bhr=s10.createRow(row++);bhr.setHeightInPoints(20);
                s10.addMergedRegion(new CellRangeAddress(row-1,row-1,0,12));
                Cell bc=bhr.createCell(0);bc.setCellValue("▶  BATCH "+bn+"  |  GRN: "+s(grn.getGrnNumber())+"  |  Received: "+fmt(grn.getReceivedDate())+"  |  Status: "+(grn.getStatus()!=null?grn.getStatus().name():"—")+"  |  "+gLines.size()+" item(s)");bc.setCellStyle(makeBatchSepStyle(wb,bn));
                for(GRNLineItem li:gLines){Row r=s10.createRow(row++);boolean hr=li.getRejectedQuantity()!=null&&li.getRejectedQuantity().compareTo(BigDecimal.ZERO)>0;CellStyle cs=hr?warn:altRow(ln,val,alt);CellStyle ams=hr?warn:amt;str(r,0,String.valueOf(ln++),cs);str(r,1,"Batch "+bn,cs);str(r,2,s(li.getItemCode()),cs);str(r,3,s(li.getItemDescription()),cs);str(r,4,s(li.getUom()),cs);num(r,5,li.getOrderedQuantity(),ams);num(r,6,li.getReceivedQuantity(),ams);num(r,7,li.getDefectiveQuantity(),ams);num(r,8,li.getRejectedQuantity(),ams);num(r,9,li.getAcceptedQuantity(),ams);num(r,10,li.getPoUnitRate(),ams);num(r,11,li.getAcceptedValue(),ams);str(r,12,s(li.getQaRemarks()),cs);}
                BigDecimal bAcc=gLines.stream().filter(li->li.getAcceptedValue()!=null).map(GRNLineItem::getAcceptedValue).reduce(BigDecimal.ZERO,BigDecimal::add);
                BigDecimal bRej=gLines.stream().filter(li->li.getRejectedValue()!=null).map(GRNLineItem::getRejectedValue).reduce(BigDecimal.ZERO,BigDecimal::add);
                Row str2=s10.createRow(row++);for(int c=0;c<13;c++){Cell nc=str2.createCell(c);nc.setCellStyle(bsl);}
                str2.getCell(0).setCellValue("Batch "+bn+" Subtotals →");str2.getCell(5).setCellValue("Ordered Val: "+(grn.getTotalOrderedValue()!=null?grn.getTotalOrderedValue().toPlainString():"0"));
                Cell ac=str2.createCell(11);ac.setCellValue(bAcc.doubleValue());ac.setCellStyle(bsa);
                Cell rc=str2.createCell(8);rc.setCellValue(bRej.doubleValue());rc.setCellStyle(bsa);
                row++;bn++;
            }
            BigDecimal gAcc=grns.stream().filter(g->g.getTotalReceivedValue()!=null).map(GRN::getTotalReceivedValue).reduce(BigDecimal.ZERO,BigDecimal::add);
            BigDecimal gRej=grns.stream().filter(g->g.getTotalRejectedValue()!=null).map(GRN::getTotalRejectedValue).reduce(BigDecimal.ZERO,BigDecimal::add);
            Row gr=s10.createRow(row++);for(int c=0;c<13;c++){Cell nc=gr.createCell(c);nc.setCellStyle(tot);}
            gr.getCell(0).setCellValue("GRAND TOTAL — All "+grns.size()+" Batch(es)");gr.getCell(11).setCellValue(gAcc.doubleValue());gr.getCell(8).setCellValue(gRej.doubleValue());
        }

        // ── SHEET 11: 3-Way Match ────────────────────────────────────
        XSSFSheet s11=wb.createSheet("3-Way Match");
        s11.setColumnWidth(0,6000);s11.setColumnWidth(1,9000);s11.setColumnWidth(2,6000);s11.setColumnWidth(3,9000);
        row=0;
        if(matches.isEmpty()){row=addBanner(s11,row,"3-WAY MATCH",title,"3-Way Match not yet performed for this RFQ.",4);}
        else{
            ThreeWayMatch m=matches.get(0);
            boolean fm=m.getMatchStatus()==ThreeWayMatchStatus.MATCHED||m.getMatchStatus()==ThreeWayMatchStatus.OVERRIDDEN_APPROVED;
            CellStyle mb=fm?makeBannerMatchStyle(wb):makeBannerWarnStyle(wb);
            row=addBannerCustomStyle(s11,row,"3-WAY MATCH REPORT",mb,"Match #"+m.getId()+"  v"+m.getMatchVersion()+"  |  Status: "+(m.getMatchStatus()!=null?m.getMatchStatus().name():"—")+"  |  RFQ: "+rfq.getRfqNumber(),4);
            row=addSectionHeader(s11,row,"MATCH SUMMARY",hdrNavy,4);
            row=addLV(s11,row,lbl,val,"Match Status",m.getMatchStatus()!=null?m.getMatchStatus().name():"—","Match Version",String.valueOf(m.getMatchVersion()));
            row=addLV(s11,row,lbl,val,"Tolerance %",s(m.getTolerancePercentage()),"Variance Amount",s(m.getVarianceAmount()));
            row=addLV(s11,row,lbl,val,"Variance %",s(m.getVariancePercentage()),"Matched Lines",String.valueOf(m.getTotalMatchedLines()));
            row=addLV(s11,row,lbl,val,"Mismatch Lines",String.valueOf(m.getTotalMismatchLines()),"Approved Payment",s(m.getApprovedPaymentAmount()));
            row=addLV(s11,row,lbl,val,"PO Total Value",s(m.getPoTotalValue()),"GRN Accepted Value",s(m.getGrnAcceptedValue()));
            row=addLV(s11,row,lbl,val,"Invoice Total",s(m.getInvoiceTotalValue()),"Resolution",m.getResolution()!=null?m.getResolution().name():"PENDING");
            row=addLV(s11,row,lbl,val,"Resolved By",s(m.getResolvedByName()),"Resolved At",fmt(m.getResolvedAt()));
            if(m.getResolutionRemarks()!=null) row=addLV(s11,row,lbl,val,"Resolution Remarks",s(m.getResolutionRemarks()),"","");
            if(m.getMatchSummary()!=null){row++;Row sr=s11.createRow(row++);s11.addMergedRegion(new CellRangeAddress(row-1,row-1,0,3));Cell sc=sr.createCell(0);sc.setCellValue(m.getMatchSummary());sc.setCellStyle(fm?low:warn);}
        }

        // ── SHEET 12: Match Line Results ─────────────────────────────
        if(!matches.isEmpty()){
            ThreeWayMatch m=matches.get(0); List<MatchLineResult> lr=m.getLineResults();
            XSSFSheet s12=wb.createSheet("Match Line Results");
            setWidths(s12,new int[]{1200,3000,6500,2000,2500,2500,2500,3000,3000,3000,3000,3000,3500,6000});
            row=0;row=addBanner(s12,row,"3-WAY MATCH — LINE RESULTS",title,"Invoice: "+s(m.getInvoiceNumber())+" ↔ GRN: "+s(m.getGrnNumber())+"  |  "+lr.size()+" lines",14);
            addHeaderRow(s12,row++,hdrNavy,new String[]{"#","Item Code","Description","UOM","PO Qty","GRN Accepted","Inv Qty","Qty Var%","PO Rate","Inv Price","Price Var%","GRN Value","Inv Value","Status"});
            sl=1;
            for(MatchLineResult r:lr){
                Row row2=s12.createRow(row++);
                boolean lok=r.getLineMatchStatus()==MatchLineResult.LineMatchStatus.MATCHED;
                boolean lfail=r.getLineMatchStatus()==MatchLineResult.LineMatchStatus.NOT_IN_PO||r.getLineMatchStatus()==MatchLineResult.LineMatchStatus.NOT_RECEIVED;
                CellStyle cs=lfail?warn:lok?low:altRow(sl,val,alt); CellStyle ams=lfail?warn:lok?low:amt;
                str(row2,0,String.valueOf(sl++),cs);str(row2,1,s(r.getItemCode()),cs);str(row2,2,s(r.getItemDescription()),cs);str(row2,3,s(r.getUom()),cs);
                num(row2,4,r.getPoOrderedQuantity(),ams);num(row2,5,r.getGrnAcceptedQuantity(),ams);num(row2,6,r.getInvoiceQuantity(),ams);num(row2,7,r.getQuantityVariancePct(),ams);
                num(row2,8,r.getPoUnitRate(),ams);num(row2,9,r.getInvoiceUnitPrice(),ams);num(row2,10,r.getPriceVariancePct(),ams);num(row2,11,r.getGrnAcceptedValue(),ams);num(row2,12,r.getInvoiceLineValue(),ams);
                str(row2,13,r.getLineMatchStatus()!=null?r.getLineMatchStatus().name():"—",sts);
            }
        }
    }

    // =========================================================================
    //  RFQ LIST REPORT
    // =========================================================================
    @Transactional(readOnly = true)
    public byte[] generateRFQListExcel(Long buyerId, String statusFilter) throws Exception {
        List<RFQ> rfqs = rfqRepository.findByBuyerId(buyerId);
        if(statusFilter!=null&&!statusFilter.isBlank()&&!statusFilter.equalsIgnoreCase("ALL")){final String sf=statusFilter.trim().toUpperCase();rfqs=rfqs.stream().filter(r->sf.equals(r.getStatus()!=null?r.getStatus().name():"")).collect(Collectors.toList());}
        try(XSSFWorkbook wb=new XSSFWorkbook();ByteArrayOutputStream out=new ByteArrayOutputStream()){
            CellStyle title=makeTitleStyle(wb);CellStyle hdrNavy=makeHeaderStyle(wb,rgb(0x1e,0x40,0x88));CellStyle val=makeValueStyle(wb);CellStyle alt=makeAltRowStyle(wb);CellStyle amt=makeAmountStyle(wb);CellStyle sts=makeStatusStyle(wb);CellStyle sl2=makeStatLabelStyle(wb);CellStyle sv=makeStatValueStyle(wb);
            XSSFSheet sheet=wb.createSheet("RFQ List");
            setWidths(sheet,new int[]{1200,3500,7000,3500,3000,3000,3000,2500,2500,3500,5000});
            int row=0;String fd=(statusFilter!=null&&!statusFilter.isBlank()&&!statusFilter.equalsIgnoreCase("ALL"))?"  |  Status: "+statusFilter:"  |  All Statuses";
            row=addBanner(sheet,row,"RFQ LIST REPORT",title,"Generated: "+LocalDateTime.now().format(DTTM_FMT)+fd,11);
            long cD=rfqs.stream().filter(r->"DRAFT".equals(en(r.getStatus()))).count();long cA=rfqs.stream().filter(r->"AWAITING_APPROVAL".equals(en(r.getStatus()))).count();long cP=rfqs.stream().filter(r->"PUBLISHED".equals(en(r.getStatus()))).count();long cC=rfqs.stream().filter(r->"CLOSED".equals(en(r.getStatus()))).count();long cH=rfqs.stream().filter(r->"HOLD".equals(en(r.getStatus()))).count();
            Row sr=sheet.createRow(row++);sr.setHeightInPoints(22);str(sr,0,"Total RFQs: "+rfqs.size(),sl2);str(sr,2,"Draft: "+cD,sv);str(sr,4,"Awaiting: "+cA,sv);str(sr,6,"Published: "+cP,sv);str(sr,8,"Closed: "+cC,sv);str(sr,10,"On Hold: "+cH,sv);row++;
            addHeaderRow(sheet,row++,hdrNavy,new String[]{"#","RFQ Number","Title","Status","Priority","Issue Date","Due Date","Suppliers","Items","Approval Status","Created By"});
            sheet.setAutoFilter(new CellRangeAddress(row-1,row-1,0,10));
            int sl=1;
            for(RFQ rfq:rfqs){Row r=sheet.createRow(row++);CellStyle cs=altRow(sl,val,alt);str(r,0,String.valueOf(sl++),cs);str(r,1,s(rfq.getRfqNumber()),cs);str(r,2,s(rfq.getRfqTitle()),cs);str(r,3,en(rfq.getStatus()),sts);str(r,4,en(rfq.getPriority()),cs);str(r,5,fmt(rfq.getIssueDate()),cs);str(r,6,fmt(rfq.getDueDate()),cs);int sc=0;try{sc=rfq.getSelectedSuppliers()!=null?rfq.getSelectedSuppliers().size():0;}catch(Exception ignored){}int ic=0;try{ic=rfq.getItems()!=null?rfq.getItems().size():0;}catch(Exception ignored){}str(r,7,String.valueOf(sc),cs);str(r,8,String.valueOf(ic),cs);str(r,9,en(rfq.getApprovalStatus()),sts);str(r,10,rfq.getCreatedByUser()!=null?rfq.getCreatedByUser().getFirstName()+" "+rfq.getCreatedByUser().getLastName():"N/A",cs);}
            wb.write(out);return out.toByteArray();
        }
    }

    // =========================================================================
    //  QUOTE COMPARISON EXCEL
    // =========================================================================
    @Transactional(readOnly = true)
    public byte[] generateQuoteComparisonExcel(Long rfqId) throws Exception {
        RFQ rfq=rfqRepository.findByIdWithAllDetails(rfqId).orElseThrow(()->new RuntimeException("RFQ not found: "+rfqId));
        List<RFQItem> items=rfqItemRepository.findByRfqId(rfqId);
        List<RFQSupplier> suppliers=rfqSupplierRepository.findByRFQAndStatus(rfqId,"RESPONDED");
        List<SupplierQuoteItem> quotes=quoteItemRepository.findByRfqId(rfqId);
        String currency=rfq.getLocation()!=null?rfq.getLocation().getCurrencyCode():"INR";
        try(XSSFWorkbook wb=new XSSFWorkbook();ByteArrayOutputStream out=new ByteArrayOutputStream()){
            CellStyle title=makeTitleStyle(wb);CellStyle hdrNavy=makeHeaderStyle(wb,rgb(0x1e,0x40,0x88));CellStyle hdrGrn=makeHeaderStyle(wb,rgb(0x1b,0x5e,0x20));CellStyle val=makeValueStyle(wb);CellStyle alt=makeAltRowStyle(wb);CellStyle amt=makeAmountStyle(wb);CellStyle low=makeLowestStyle(wb);CellStyle tot=makeTotalStyle(wb);CellStyle meta=makeMetaStyle(wb);
            XSSFSheet sheet=wb.createSheet("Quote Comparison");
            int numSup=suppliers.size();int fCols=6;int sCols=7;int tCols=fCols+numSup*sCols;
            sheet.setColumnWidth(0,1200);sheet.setColumnWidth(1,3500);sheet.setColumnWidth(2,7000);sheet.setColumnWidth(3,6000);sheet.setColumnWidth(4,2500);sheet.setColumnWidth(5,2500);
            for(int i=0;i<numSup;i++){int b=fCols+i*sCols;sheet.setColumnWidth(b,3000);sheet.setColumnWidth(b+1,4500);sheet.setColumnWidth(b+2,2500);sheet.setColumnWidth(b+3,3000);sheet.setColumnWidth(b+4,3000);sheet.setColumnWidth(b+5,4000);sheet.setColumnWidth(b+6,5000);}
            int row=0;
            row=addBanner(sheet,row,"QUOTE COMPARISON MATRIX",title,rfq.getRfqNumber()+"  —  "+rfq.getRfqTitle()+"  |  Currency: "+currency+"  |  Suppliers: "+numSup+"  |  Generated: "+LocalDateTime.now().format(DTTM_FMT),tCols);
            Row meta2=sheet.createRow(row++);str(meta2,0,"Buyer: "+(rfq.getBuyer()!=null?rfq.getBuyer().getCompanyName():"N/A"),meta);str(meta2,3,"Due Date: "+fmt(rfq.getDueDate()),meta);str(meta2,6,"Items: "+items.size(),meta);row++;
            Row shRow=sheet.createRow(row++);str(shRow,0,"Sl No",hdrNavy);str(shRow,1,"Item Code",hdrNavy);str(shRow,2,"Item Description",hdrNavy);str(shRow,3,"Specifications",hdrNavy);str(shRow,4,"Qty",hdrNavy);str(shRow,5,"UOM",hdrNavy);
            for(int i=0;i<numSup;i++){RFQSupplier rs=suppliers.get(i);String sn=rs.getSupplier()!=null?rs.getSupplier().getCompanyName():"Supplier "+(i+1);String sc=rs.getSupplier()!=null?determineSupplierCurrency(rfq,rs.getSupplier()):currency;int b=fCols+i*sCols;Cell hc=shRow.createCell(b);hc.setCellValue(sn+"  ["+sc+"]");hc.setCellStyle(hdrGrn);sheet.addMergedRegion(new CellRangeAddress(row-1,row-1,b,b+sCols-1));}
            Row subRow=sheet.createRow(row++);for(int c=0;c<fCols;c++)subRow.createCell(c).setCellStyle(hdrNavy);
            for(int i=0;i<numSup;i++){int b=fCols+i*sCols;String[] sub={"Unit Rate","Grand Total","Tax %","Delivery (d)","Warranty (m)","Brand/Make/Model","Remarks"};for(int c=0;c<sub.length;c++){Cell hc=subRow.createCell(b+c);hc.setCellValue(sub[c]);hc.setCellStyle(hdrNavy);}}
            int sl=1;
            for(RFQItem item:items){
                double lRate=Double.MAX_VALUE;Long lSupId=null;
                for(RFQSupplier rs:suppliers){if(rs.getSupplier()==null)continue;Optional<SupplierQuoteItem> qo=findQuote(quotes,item.getId(),rs.getSupplier().getId());if(qo.isPresent()&&qo.get().getUnitRate()!=null&&qo.get().getUnitRate().doubleValue()<lRate){lRate=qo.get().getUnitRate().doubleValue();lSupId=rs.getSupplier().getId();}}
                final Long lid=lSupId;
                Row r=sheet.createRow(row++);CellStyle cs=altRow(sl,val,alt);str(r,0,String.valueOf(sl++),cs);str(r,1,s(item.getItemCode()),cs);str(r,2,s(item.getItemDescription()),cs);str(r,3,s(item.getSpecifications()),cs);num(r,4,item.getQuantity(),amt);str(r,5,s(item.getUom()),cs);
                for(int i=0;i<numSup;i++){RFQSupplier rs=suppliers.get(i);if(rs.getSupplier()==null)continue;Long sid=rs.getSupplier().getId();int b=fCols+i*sCols;boolean isl=sid.equals(lid);CellStyle qs=isl?low:amt;CellStyle ts=isl?low:val;Optional<SupplierQuoteItem> qo=findQuote(quotes,item.getId(),sid);if(qo.isPresent()){SupplierQuoteItem qi=qo.get();num(r,b,qi.getUnitRate(),qs);num(r,b+1,qi.getGrandTotal(),qs);num(r,b+2,qi.getTaxPercentage(),ts);str(r,b+3,qi.getDeliveryDays()!=null?qi.getDeliveryDays()+"d":"—",ts);str(r,b+4,qi.getWarrantyMonths()!=null?qi.getWarrantyMonths()+"m":"—",ts);str(r,b+5,joinNonNull("/",qi.getBrandOffered(),qi.getMakeModel()),ts);str(r,b+6,s(qi.getRemarks()),ts);}else{for(int c=0;c<sCols;c++){Cell nc=r.createCell(b+c);nc.setCellValue("—");nc.setCellStyle(val);}}}
            }
            Row tRow=sheet.createRow(row++);Cell tl=tRow.createCell(0);tl.setCellValue("GRAND TOTAL");tl.setCellStyle(tot);sheet.addMergedRegion(new CellRangeAddress(row-1,row-1,0,fCols-1));for(int c=1;c<fCols;c++)tRow.createCell(c).setCellStyle(tot);
            Long ltSid=null;double ltAmt=Double.MAX_VALUE;
            for(RFQSupplier rs:suppliers){if(rs.getSupplier()==null)continue;double t=sumGrandTotal(quotes,rs.getSupplier().getId());if(t>0&&t<ltAmt){ltAmt=t;ltSid=rs.getSupplier().getId();}}
            final Long ltId=ltSid;
            for(int i=0;i<numSup;i++){RFQSupplier rs=suppliers.get(i);if(rs.getSupplier()==null)continue;Long sid=rs.getSupplier().getId();int b=fCols+i*sCols;boolean isl=sid.equals(ltId);CellStyle fs=isl?low:tot;double total=sumGrandTotal(quotes,sid);for(int c=0;c<sCols;c++){Cell nc=tRow.createCell(b+c);if(c==1)nc.setCellValue(total);else if(c==0)nc.setCellValue(isl?"★ LOWEST":"");nc.setCellStyle(fs);}}
            sheet.setAutoFilter(new CellRangeAddress(5,row-2,0,tCols-1));
            wb.write(out);return out.toByteArray();
        }
    }

    // =========================================================================
    //  INVOICE EXCEL
    // =========================================================================
    @Transactional(readOnly = true)
    public byte[] generateInvoiceExcel(Long invoiceId) throws Exception {
        Invoice invoice=invoiceRepository.findById(invoiceId).orElseThrow(()->new RuntimeException("Invoice not found: "+invoiceId));
        List<InvoiceLineItem> lineItems=invoice.getLineItems();
        try(XSSFWorkbook wb=new XSSFWorkbook();ByteArrayOutputStream out=new ByteArrayOutputStream()){
            CellStyle title=makeTitleStyle(wb);CellStyle hdrNavy=makeHeaderStyle(wb,rgb(0x1e,0x40,0x88));CellStyle hdrGrn=makeHeaderStyle(wb,rgb(0x1b,0x5e,0x20));CellStyle lbl=makeLabelStyle(wb);CellStyle val=makeValueStyle(wb);CellStyle amt=makeAmountStyle(wb);CellStyle alt=makeAltRowStyle(wb);CellStyle tot=makeTotalStyle(wb);
            XSSFSheet sheet=wb.createSheet("Invoice");setWidths(sheet,new int[]{6000,9000,6000,9000});
            int row=0;row=addBanner(sheet,row,"INVOICE",title,s(invoice.getInvoiceNumber())+"  |  Generated: "+LocalDateTime.now().format(DTTM_FMT),4);
            row=addSectionHeader(sheet,row,"INVOICE DETAILS",hdrNavy,4);row=addLV(sheet,row,lbl,val,"Invoice Number",invoice.getInvoiceNumber(),"Status",invoice.getStatus()!=null?invoice.getStatus().name():"—");row=addLV(sheet,row,lbl,val,"Invoice Date",fmt(invoice.getInvoiceDate()),"Due Date",fmt(invoice.getDueDate()));row=addLV(sheet,row,lbl,val,"PO Number",s(invoice.getPoNumber()),"RFQ Number",s(invoice.getRfqNumber()));row=addLV(sheet,row,lbl,val,"Currency",s(invoice.getCurrency()),"Payment Terms",s(invoice.getPaymentTerms()));row++;
            row=addSectionHeader(sheet,row,"SUPPLIER (FROM)",hdrNavy,4);row=addLV(sheet,row,lbl,val,"Supplier Name",s(invoice.getSupplierName()),"Email",s(invoice.getSupplierEmail()));row++;
            row=addSectionHeader(sheet,row,"BUYER (BILL TO)",hdrNavy,4);row=addLV(sheet,row,lbl,val,"Buyer Company",s(invoice.getBuyerCompanyName()),"RFQ Creator",s(invoice.getRfqCreatorName()));row++;
            setWidths(sheet,new int[]{1200,3000,7000,2500,2000,2500,4000,2000,2000,4500});
            row=addSectionHeader(sheet,row,"LINE ITEMS ("+lineItems.size()+")",hdrGrn,4);addHeaderRow(sheet,row++,hdrNavy,new String[]{"#","Item Code","Description","HSN/SAC","UOM","Quantity","Unit Price","Disc %","Tax %","Line Total"});
            int sl=1;
            for(InvoiceLineItem li:lineItems){Row r=sheet.createRow(row++);CellStyle cs=altRow(sl,val,alt);str(r,0,String.valueOf(sl++),cs);str(r,1,s(li.getItemCode()),cs);str(r,2,s(li.getItemDescription()),cs);str(r,3,s(li.getHsnSacCode()),cs);str(r,4,s(li.getUom()),cs);num(r,5,li.getQuantity(),amt);num(r,6,li.getUnitPrice(),amt);num(r,7,li.getDiscountPercentage(),amt);num(r,8,li.getTaxPercentage(),amt);num(r,9,li.getLineTotal(),amt);}
            row++;Object[][] ts={{"Subtotal (Pre-tax)",invoice.getSubtotal()},{"Total Tax",invoice.getTaxAmount()},{"Grand Total",invoice.getTotalAmount()}};
            for(int t=0;t<ts.length;t++){Row tr=sheet.createRow(row++);boolean ig=(t==ts.length-1);Cell lc=tr.createCell(8);lc.setCellValue((String)ts[t][0]);lc.setCellStyle(ig?tot:lbl);Cell vc=tr.createCell(9);if(ts[t][1] instanceof BigDecimal)vc.setCellValue(((BigDecimal)ts[t][1]).doubleValue());else vc.setCellValue(0);vc.setCellStyle(ig?tot:amt);}
            if(invoice.getBankName()!=null){row++;row=addSectionHeader(sheet,row,"BANK DETAILS",hdrNavy,4);row=addLV(sheet,row,lbl,val,"Bank Name",s(invoice.getBankName()),"Account Name",s(invoice.getBankAccountName()));row=addLV(sheet,row,lbl,val,"Account Number",s(invoice.getBankAccountNumber()),"IFSC Code",s(invoice.getBankIfscCode()));addLV(sheet,row,lbl,val,"SWIFT Code",s(invoice.getBankSwiftCode()),"","");}
            wb.write(out);return out.toByteArray();
        }
    }

    // =========================================================================
    //  HTML stubs
    // =========================================================================
    @Transactional(readOnly = true)
    public String generateRFQSummaryHTML(Long rfqId) { throw new UnsupportedOperationException("Keep existing implementation"); }
    @Transactional(readOnly = true)
    public String generateQuoteComparisonHTML(Long rfqId) { throw new UnsupportedOperationException("Keep existing implementation"); }

    // =========================================================================
    //  PRIVATE: Hierarchy style helpers
    // =========================================================================
    private XSSFCellStyle filled(XSSFWorkbook wb, byte[] bg, byte[] txt, boolean bold) {
        XSSFCellStyle s=wb.createCellStyle();XSSFFont f=wb.createFont();f.setBold(bold);f.setFontHeightInPoints((short)10);f.setColor(new XSSFColor(txt,null));s.setFont(f);
        s.setFillForegroundColor(new XSSFColor(bg,null));s.setFillPattern(FillPatternType.SOLID_FOREGROUND);s.setBorderBottom(BorderStyle.THIN);s.setBorderRight(BorderStyle.THIN);s.setWrapText(true);return s;
    }

    private XSSFCellStyle buildStatusCell(XSSFWorkbook wb, Object status) {
        XSSFCellStyle s=wb.createCellStyle();XSSFFont f=wb.createFont();f.setBold(true);f.setFontHeightInPoints((short)9);
        if(status==null){f.setColor(new XSSFColor(rgb(0x38,0x3d,0x41),null));s.setFillForegroundColor(new XSSFColor(rgb(0xe2,0xe3,0xe5),null));}
        else{switch(status.toString()){
            case "APPROVED":f.setColor(new XSSFColor(rgb(0x1a,0x5c,0x1a),null));s.setFillForegroundColor(new XSSFColor(rgb(0xd4,0xed,0xda),null));break;
            case "REJECTED":f.setColor(new XSSFColor(rgb(0x72,0x1c,0x24),null));s.setFillForegroundColor(new XSSFColor(rgb(0xf8,0xd7,0xda),null));break;
            case "HOLD":    f.setColor(new XSSFColor(rgb(0x85,0x64,0x04),null));s.setFillForegroundColor(new XSSFColor(rgb(0xff,0xf3,0xcd),null));break;
            default:        f.setColor(new XSSFColor(rgb(0x38,0x3d,0x41),null));s.setFillForegroundColor(new XSSFColor(rgb(0xe2,0xe3,0xe5),null));break;
        }}
        s.setFont(f);s.setFillPattern(FillPatternType.SOLID_FOREGROUND);s.setAlignment(HorizontalAlignment.CENTER);s.setBorderBottom(BorderStyle.THIN);s.setWrapText(true);return s;
    }

    private XSSFCellStyle makeInitiatedSts(XSSFWorkbook wb) {
        XSSFCellStyle s=wb.createCellStyle();XSSFFont f=wb.createFont();f.setBold(true);f.setFontHeightInPoints((short)9);f.setColor(new XSSFColor(rgb(0x0c,0x44,0x7c),null));s.setFont(f);
        s.setFillForegroundColor(new XSSFColor(rgb(0xe6,0xf1,0xfb),null));s.setFillPattern(FillPatternType.SOLID_FOREGROUND);s.setAlignment(HorizontalAlignment.CENTER);s.setBorderBottom(BorderStyle.THIN);return s;
    }

    private CellStyle statusRowBg(Object e, CellStyle app, CellStyle rej, CellStyle hold, CellStyle pend, int sl, CellStyle v, CellStyle a) {
        if(e==null) return altRow(sl,v,a);
        switch(e.toString()){case "APPROVED":return app;case "REJECTED":return rej;case "HOLD":return hold;case "PENDING":return pend;default:return altRow(sl,v,a);}
    }

    private String buildRemarks(DynamicRFQApproval ap) {
        StringBuilder sb=new StringBuilder();
        if(ap.getComments()!=null&&!ap.getComments().isBlank())sb.append(ap.getComments().trim());
        if(ap.getHoldRemarks()!=null&&!ap.getHoldRemarks().isBlank()){if(sb.length()>0)sb.append("  |  ");sb.append("HOLD: ").append(ap.getHoldRemarks().trim());}
        if(ap.getRejectRemarks()!=null&&!ap.getRejectRemarks().isBlank()){if(sb.length()>0)sb.append("  |  ");sb.append("REJECT: ").append(ap.getRejectRemarks().trim());}
        if(ap.getReleaseRemarks()!=null&&!ap.getReleaseRemarks().isBlank()){if(sb.length()>0)sb.append("  |  ");sb.append("RELEASE: ").append(ap.getReleaseRemarks().trim());}
        return sb.toString();
    }

    private int addLV7(XSSFSheet sh,int row,CellStyle lc,CellStyle vc,String l1,String v1,String l2,String v2){
        Row r=sh.createRow(row++);r.setHeightInPoints(18);
        Cell c0=r.createCell(0);c0.setCellValue(l1);c0.setCellStyle(lc);
        Cell c1=r.createCell(1);c1.setCellValue(v1);c1.setCellStyle(vc);sh.addMergedRegion(new CellRangeAddress(row-1,row-1,1,2));
        Cell c3=r.createCell(3);c3.setCellValue(l2);c3.setCellStyle(lc);
        Cell c4=r.createCell(4);c4.setCellValue(v2);c4.setCellStyle(vc);sh.addMergedRegion(new CellRangeAddress(row-1,row-1,4,6));
        return row;
    }

    private String safeStr(String s) { return s != null ? s : ""; }

    // =========================================================================
    //  PRIVATE: GRN batch styles
    // =========================================================================
    private XSSFCellStyle makeBatchSepStyle(XSSFWorkbook wb,int bn){XSSFCellStyle s=wb.createCellStyle();XSSFFont f=wb.createFont();f.setBold(true);f.setFontHeightInPoints((short)10);f.setColor(new XSSFColor(new byte[]{(byte)0xff,(byte)0xff,(byte)0xff},null));s.setFont(f);byte[] bg=(bn%2==1)?rgb(0xe6,0x55,0x00):rgb(0x00,0x6d,0x77);s.setFillForegroundColor(new XSSFColor(bg,null));s.setFillPattern(FillPatternType.SOLID_FOREGROUND);s.setVerticalAlignment(VerticalAlignment.CENTER);return s;}
    private XSSFCellStyle makeBatchSubtotalLabelStyle(XSSFWorkbook wb){XSSFCellStyle s=wb.createCellStyle();XSSFFont f=wb.createFont();f.setBold(true);f.setFontHeightInPoints((short)9);f.setColor(new XSSFColor(rgb(0x1e,0x40,0x88),null));s.setFont(f);s.setFillForegroundColor(new XSSFColor(rgb(0xe8,0xf0,0xfe),null));s.setFillPattern(FillPatternType.SOLID_FOREGROUND);s.setBorderTop(BorderStyle.MEDIUM);return s;}
    private XSSFCellStyle makeBatchSubtotalAmtStyle(XSSFWorkbook wb){XSSFCellStyle s=wb.createCellStyle();XSSFFont f=wb.createFont();f.setBold(true);f.setFontHeightInPoints((short)9);f.setColor(new XSSFColor(rgb(0x1e,0x40,0x88),null));s.setFont(f);s.setFillForegroundColor(new XSSFColor(rgb(0xe8,0xf0,0xfe),null));s.setFillPattern(FillPatternType.SOLID_FOREGROUND);s.setAlignment(HorizontalAlignment.RIGHT);s.setDataFormat(wb.createDataFormat().getFormat("#,##0.00"));s.setBorderTop(BorderStyle.MEDIUM);return s;}

    // =========================================================================
    //  PRIVATE: cell style factories
    // =========================================================================
    private XSSFCellStyle makeTitleStyle(XSSFWorkbook wb){XSSFCellStyle s=wb.createCellStyle();XSSFFont f=wb.createFont();f.setBold(true);f.setFontHeightInPoints((short)14);f.setColor(new XSSFColor(new byte[]{(byte)0xff,(byte)0xff,(byte)0xff},null));s.setFont(f);s.setFillForegroundColor(new XSSFColor(rgb(0x1e,0x40,0x88),null));s.setFillPattern(FillPatternType.SOLID_FOREGROUND);s.setAlignment(HorizontalAlignment.CENTER);s.setVerticalAlignment(VerticalAlignment.CENTER);s.setWrapText(true);return s;}
    private XSSFCellStyle makeHeaderStyle(XSSFWorkbook wb,byte[] rgb){XSSFCellStyle s=wb.createCellStyle();XSSFFont f=wb.createFont();f.setBold(true);f.setFontHeightInPoints((short)10);f.setColor(new XSSFColor(new byte[]{(byte)0xff,(byte)0xff,(byte)0xff},null));s.setFont(f);s.setFillForegroundColor(new XSSFColor(rgb,null));s.setFillPattern(FillPatternType.SOLID_FOREGROUND);s.setBorderBottom(BorderStyle.THIN);s.setBorderTop(BorderStyle.THIN);s.setBorderLeft(BorderStyle.THIN);s.setBorderRight(BorderStyle.THIN);s.setWrapText(true);return s;}
    private XSSFCellStyle makeLabelStyle(XSSFWorkbook wb){XSSFCellStyle s=wb.createCellStyle();XSSFFont f=wb.createFont();f.setBold(true);f.setFontHeightInPoints((short)9);s.setFont(f);s.setFillForegroundColor(new XSSFColor(rgb(0xf0,0xf4,0xff),null));s.setFillPattern(FillPatternType.SOLID_FOREGROUND);s.setBorderBottom(BorderStyle.THIN);s.setBorderRight(BorderStyle.THIN);return s;}
    private XSSFCellStyle makeValueStyle(XSSFWorkbook wb){XSSFCellStyle s=wb.createCellStyle();s.setBorderBottom(BorderStyle.THIN);s.setWrapText(true);return s;}
    private XSSFCellStyle makeAltRowStyle(XSSFWorkbook wb){XSSFCellStyle s=wb.createCellStyle();s.setFillForegroundColor(new XSSFColor(rgb(0xf5,0xf7,0xfb),null));s.setFillPattern(FillPatternType.SOLID_FOREGROUND);s.setBorderBottom(BorderStyle.THIN);s.setWrapText(true);return s;}
    private XSSFCellStyle makeAmountStyle(XSSFWorkbook wb){XSSFCellStyle s=wb.createCellStyle();s.setAlignment(HorizontalAlignment.RIGHT);s.setDataFormat(wb.createDataFormat().getFormat("#,##0.00"));s.setBorderBottom(BorderStyle.THIN);return s;}
    private XSSFCellStyle makeLowestStyle(XSSFWorkbook wb){XSSFCellStyle s=wb.createCellStyle();XSSFFont f=wb.createFont();f.setBold(true);f.setFontHeightInPoints((short)9);f.setColor(new XSSFColor(rgb(0x1a,0x5c,0x1a),null));s.setFont(f);s.setFillForegroundColor(new XSSFColor(rgb(0xd4,0xed,0xda),null));s.setFillPattern(FillPatternType.SOLID_FOREGROUND);s.setAlignment(HorizontalAlignment.RIGHT);s.setDataFormat(wb.createDataFormat().getFormat("#,##0.00"));s.setBorderBottom(BorderStyle.THIN);return s;}
    private XSSFCellStyle makeTotalStyle(XSSFWorkbook wb){XSSFCellStyle s=wb.createCellStyle();XSSFFont f=wb.createFont();f.setBold(true);f.setFontHeightInPoints((short)11);f.setColor(new XSSFColor(new byte[]{(byte)0xff,(byte)0xff,(byte)0xff},null));s.setFont(f);s.setFillForegroundColor(new XSSFColor(rgb(0x1e,0x40,0x88),null));s.setFillPattern(FillPatternType.SOLID_FOREGROUND);s.setAlignment(HorizontalAlignment.RIGHT);s.setDataFormat(wb.createDataFormat().getFormat("#,##0.00"));return s;}
    private XSSFCellStyle makeStatusStyle(XSSFWorkbook wb){XSSFCellStyle s=wb.createCellStyle();XSSFFont f=wb.createFont();f.setBold(true);f.setFontHeightInPoints((short)9);s.setFont(f);s.setAlignment(HorizontalAlignment.CENTER);s.setBorderBottom(BorderStyle.THIN);return s;}
    private XSSFCellStyle makeStatLabelStyle(XSSFWorkbook wb){XSSFCellStyle s=wb.createCellStyle();XSSFFont f=wb.createFont();f.setBold(true);f.setFontHeightInPoints((short)10);s.setFont(f);s.setFillForegroundColor(new XSSFColor(rgb(0xe8,0xf0,0xfe),null));s.setFillPattern(FillPatternType.SOLID_FOREGROUND);return s;}
    private XSSFCellStyle makeStatValueStyle(XSSFWorkbook wb){XSSFCellStyle s=wb.createCellStyle();XSSFFont f=wb.createFont();f.setBold(true);f.setFontHeightInPoints((short)10);f.setColor(new XSSFColor(rgb(0x1e,0x40,0x88),null));s.setFont(f);return s;}
    private XSSFCellStyle makeMetaStyle(XSSFWorkbook wb){XSSFCellStyle s=wb.createCellStyle();XSSFFont f=wb.createFont();f.setItalic(true);f.setFontHeightInPoints((short)9);f.setColor(new XSSFColor(rgb(0x44,0x44,0x44),null));s.setFont(f);return s;}
    private XSSFCellStyle makeWarnStyle(XSSFWorkbook wb){XSSFCellStyle s=wb.createCellStyle();s.setFillForegroundColor(new XSSFColor(rgb(0xff,0xf9,0xc4),null));s.setFillPattern(FillPatternType.SOLID_FOREGROUND);s.setBorderBottom(BorderStyle.THIN);s.setWrapText(true);return s;}
    private XSSFCellStyle makeBannerMatchStyle(XSSFWorkbook wb){XSSFCellStyle s=wb.createCellStyle();XSSFFont f=wb.createFont();f.setBold(true);f.setFontHeightInPoints((short)14);f.setColor(new XSSFColor(new byte[]{(byte)0xff,(byte)0xff,(byte)0xff},null));s.setFont(f);s.setFillForegroundColor(new XSSFColor(rgb(0x1b,0x5e,0x20),null));s.setFillPattern(FillPatternType.SOLID_FOREGROUND);s.setAlignment(HorizontalAlignment.CENTER);s.setVerticalAlignment(VerticalAlignment.CENTER);s.setWrapText(true);return s;}
    private XSSFCellStyle makeBannerWarnStyle(XSSFWorkbook wb){XSSFCellStyle s=wb.createCellStyle();XSSFFont f=wb.createFont();f.setBold(true);f.setFontHeightInPoints((short)14);f.setColor(new XSSFColor(new byte[]{(byte)0xff,(byte)0xff,(byte)0xff},null));s.setFont(f);s.setFillForegroundColor(new XSSFColor(rgb(0xe6,0x55,0x00),null));s.setFillPattern(FillPatternType.SOLID_FOREGROUND);s.setAlignment(HorizontalAlignment.CENTER);s.setVerticalAlignment(VerticalAlignment.CENTER);s.setWrapText(true);return s;}
    private XSSFCellStyle makeBannerFailStyle(XSSFWorkbook wb){XSSFCellStyle s=wb.createCellStyle();XSSFFont f=wb.createFont();f.setBold(true);f.setFontHeightInPoints((short)14);f.setColor(new XSSFColor(new byte[]{(byte)0xff,(byte)0xff,(byte)0xff},null));s.setFont(f);s.setFillForegroundColor(new XSSFColor(rgb(0xb7,0x1c,0x1c),null));s.setFillPattern(FillPatternType.SOLID_FOREGROUND);s.setAlignment(HorizontalAlignment.CENTER);s.setVerticalAlignment(VerticalAlignment.CENTER);s.setWrapText(true);return s;}
    private XSSFCellStyle makeAwardedRowStyle(XSSFWorkbook wb){XSSFCellStyle s=wb.createCellStyle();XSSFFont f=wb.createFont();f.setBold(true);f.setFontHeightInPoints((short)9);f.setColor(new XSSFColor(rgb(0x7d,0x4a,0x00),null));s.setFont(f);s.setFillForegroundColor(new XSSFColor(rgb(0xff,0xf3,0xcd),null));s.setFillPattern(FillPatternType.SOLID_FOREGROUND);s.setBorderBottom(BorderStyle.THIN);s.setBorderTop(BorderStyle.THIN);s.setBorderLeft(BorderStyle.THIN);s.setBorderRight(BorderStyle.THIN);s.setWrapText(true);return s;}
    private XSSFCellStyle makeAwardedAmtStyle(XSSFWorkbook wb){XSSFCellStyle s=wb.createCellStyle();XSSFFont f=wb.createFont();f.setBold(true);f.setFontHeightInPoints((short)9);f.setColor(new XSSFColor(rgb(0x7d,0x4a,0x00),null));s.setFont(f);s.setFillForegroundColor(new XSSFColor(rgb(0xff,0xf3,0xcd),null));s.setFillPattern(FillPatternType.SOLID_FOREGROUND);s.setAlignment(HorizontalAlignment.RIGHT);s.setDataFormat(wb.createDataFormat().getFormat("#,##0.00"));s.setBorderBottom(BorderStyle.THIN);s.setBorderTop(BorderStyle.THIN);s.setBorderLeft(BorderStyle.THIN);s.setBorderRight(BorderStyle.THIN);return s;}

    // =========================================================================
    //  PRIVATE: layout helpers
    // =========================================================================
    private int addBanner(XSSFSheet sh,int row,String title,CellStyle ts,String sub,int cols){sh.addMergedRegion(new CellRangeAddress(row,row,0,cols-1));Row r1=sh.createRow(row++);r1.setHeightInPoints(30);Cell c1=r1.createCell(0);c1.setCellValue(title);c1.setCellStyle(ts);sh.addMergedRegion(new CellRangeAddress(row,row,0,cols-1));Row r2=sh.createRow(row++);r2.setHeightInPoints(18);Cell c2=r2.createCell(0);c2.setCellValue(sub);c2.setCellStyle(ts);return row;}
    private int addBannerCustomStyle(XSSFSheet sh,int row,String title,CellStyle ts,String sub,int cols){sh.addMergedRegion(new CellRangeAddress(row,row,0,cols-1));Row r1=sh.createRow(row++);r1.setHeightInPoints(30);Cell c1=r1.createCell(0);c1.setCellValue(title);c1.setCellStyle(ts);sh.addMergedRegion(new CellRangeAddress(row,row,0,cols-1));Row r2=sh.createRow(row++);r2.setHeightInPoints(18);Cell c2=r2.createCell(0);c2.setCellValue(sub);c2.setCellStyle(ts);return row;}
    private int addSectionHeader(XSSFSheet sh,int row,String text,CellStyle cs,int cols){sh.addMergedRegion(new CellRangeAddress(row,row,0,cols-1));Row r=sh.createRow(row++);r.setHeightInPoints(20);Cell c=r.createCell(0);c.setCellValue(text);c.setCellStyle(cs);return row;}
    private int addLV(XSSFSheet sh,int row,CellStyle lc,CellStyle vc,String l1,Object v1,String l2,Object v2){Row r=sh.createRow(row++);r.setHeightInPoints(16);str(r,0,l1,lc);str(r,1,s(v1),vc);str(r,2,l2,lc);str(r,3,s(v2),vc);return row;}
    private void addHeaderRow(XSSFSheet sh,int row,CellStyle cs,String[] cols){Row r=sh.createRow(row);r.setHeightInPoints(18);for(int c=0;c<cols.length;c++){Cell cell=r.createCell(c);cell.setCellValue(cols[c]);cell.setCellStyle(cs);}}
    private void setWidths(XSSFSheet sh,int[] w){for(int c=0;c<w.length;c++)sh.setColumnWidth(c,w[c]);}
    private void str(Row row,int col,String v,CellStyle cs){Cell c=row.createCell(col);c.setCellValue(v!=null?v:"");c.setCellStyle(cs);}
    private void num(Row row,int col,Object v,CellStyle cs){Cell c=row.createCell(col);if(v instanceof BigDecimal)c.setCellValue(((BigDecimal)v).doubleValue());else if(v instanceof Number)c.setCellValue(((Number)v).doubleValue());else c.setCellValue(0);c.setCellStyle(cs);}
    private void num(Row row,int col,BigDecimal v,CellStyle cs){Cell c=row.createCell(col);c.setCellValue(v!=null?v.doubleValue():0);c.setCellStyle(cs);}

    // =========================================================================
    //  PRIVATE: utilities
    // =========================================================================
    private byte[] rgb(int r,int g,int b){return new byte[]{(byte)r,(byte)g,(byte)b};}
    private CellStyle altRow(int sl,CellStyle n,CellStyle a){return(sl%2==0)?a:n;}
    private String s(Object o){return o!=null?o.toString():"—";}
    private String en(Object o){return o!=null?o.toString():"N/A";}
    private String bool(Boolean b){return Boolean.TRUE.equals(b)?"Yes":"No";}
    private String fmt(LocalDateTime dt){return dt!=null?dt.format(DATE_FMT):"—";}
    private String joinNonNull(String sep,String...parts){return Arrays.stream(parts).filter(p->p!=null&&!p.isBlank()).collect(Collectors.joining(" "+sep+" "));}

    private Optional<SupplierQuoteItem> findQuote(List<SupplierQuoteItem> quotes,Long itemId,Long supplierId){
        return quotes.stream().filter(q->q.getRfqItem()!=null&&q.getRfqItem().getId().equals(itemId)&&q.getRfqSupplier()!=null&&q.getRfqSupplier().getSupplier()!=null&&q.getRfqSupplier().getSupplier().getId().equals(supplierId)).findFirst();
    }

    private double sumGrandTotal(List<SupplierQuoteItem> quotes,Long supplierId){
        return quotes.stream().filter(q->q.getRfqSupplier()!=null&&q.getRfqSupplier().getSupplier()!=null&&q.getRfqSupplier().getSupplier().getId().equals(supplierId)&&q.getGrandTotal()!=null).mapToDouble(q->q.getGrandTotal().doubleValue()).sum();
    }

    private String determineSupplierCurrency(RFQ rfq,Supplier supplier){
        if(rfq.getLocation()==null||supplier==null)return "INR";
        String bc=rfq.getLocation().getCountry();String sc=supplier.getCountry();
        if(bc!=null&&sc!=null&&bc.trim().equalsIgnoreCase(sc.trim()))return rfq.getLocation().getCurrencyCode()!=null?rfq.getLocation().getCurrencyCode():"INR";
        return "USD";
    }
}