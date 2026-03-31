import * as XLSX from 'xlsx';
import { Injectable } from '@angular/core';
import { saveAs } from 'file-saver';
import { Workbook } from 'exceljs';

@Injectable({ providedIn: 'root' })
export class ExcelService {
    // Export JSON array to Excel (no API call)
    exportAsExcelFile(json: any[], fileName: string): void {
        const worksheet: XLSX.WorkSheet = XLSX.utils.json_to_sheet(json);
        const workbook: XLSX.WorkBook = {
            Sheets: { data: worksheet },
            SheetNames: ['data']
        };
        const excelBuffer: any = XLSX.write(workbook, { bookType: 'xlsx', type: 'array' });
        const blob = new Blob([excelBuffer], { type: 'application/octet-stream' });
        saveAs(blob, `${fileName}.xlsx`);
    }

    // Import (client-side)
    importFromExcel(file: File, callback: (data: any[]) => void): void {
        const reader = new FileReader();
        reader.onload = (e: any) => {
            const workbook = XLSX.read(e.target.result, { type: 'binary' });
            const sheetName = workbook.SheetNames[0];
            const data = XLSX.utils.sheet_to_json(workbook.Sheets[sheetName]);
            callback(data);
        };
        reader.readAsBinaryString(file);
    }

    formatDate(dateStr: string | null): string | null {
        if (!dateStr) return null;
        const d = new Date(dateStr);
        return d.toLocaleDateString("en-GB", { day: "2-digit", month: "short", year: "numeric" });
    }

    formatMonthYear(dateStr: string | null): string | null {
        if (!dateStr) return null;
        const d = new Date(dateStr);
        return d.toLocaleDateString("en-GB", { month: "short", year: "numeric" });
    }

    flattenBillingIndent(indent: any): any[] {
        const rows: any[] = [];

        if (indent.employeeDetails && indent.employeeDetails.length > 0) {
            indent.employeeDetails.forEach((emp: any) => {
                rows.push({
                    salesForTheMonth: this.formatMonthYear(indent.salesForTheMonth),
                    practice: indent.practiceType,
                    type: indent.type,
                    projectCode: indent.projectCode,
                    projectName: indent.projectName,
                    customerCode: indent.customerCode,
                    customerName: indent.customerName,
                    customerAddress: indent.customerAddress,
                    clientManager: indent.customerManager,
                    projectType: indent.projectType,
                    proposalNo: indent.proposalSowNo,
                    poRefDate: this.formatDate(indent.poRefDate),
                    billingMilestoneMonth: indent.billingMilestoneMonth,
                    employeeId: emp.employeeCode,
                    employeeName: emp.firstName,
                    effort: emp.hours,
                    uom: emp.billingType,
                    rate: emp.billingRate,
                    rateUom: emp.rateUnit,
                    currency: emp.billingCurrency,
                    revenue: emp.amount,
                    totalRevenueInInr: emp.totalRevenue,
                    billable: emp.billable ? "Yes" : "No",
                    invoiceNumber: indent.invoiceNumber,
                    remarks: indent.invoiceRemarks
                });
            });
        }

        return rows;
    }

    exportStyledBillingIndent(indents: any[], rates: any, month: string, year: number) {
        const workbook = new Workbook();
        const sheet = workbook.addWorksheet('Billing Indent');

        // Helper: safely convert various inputs to number
        const toNumber = (v: any) => {
            if (v === null || v === undefined) return 0;
            const s = String(v).replace(/[, ]+/g, '').trim(); // remove commas and spaces
            const n = parseFloat(s);
            return Number.isFinite(n) ? n : 0;
        };

        // --------------------
        // Header Row (Merged Title)
        sheet.mergeCells('A1:E1');
        const titleCell = sheet.getCell('A1');
        titleCell.value = `Billing Indent for the month of ${month} ${year}`;
        titleCell.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: '1F4E78' } };
        titleCell.font = { bold: true, color: { argb: 'FFFFFFFF' }, size: 14 };
        titleCell.alignment = { vertical: 'middle', horizontal: 'center' };

        // -----------------------------
        // Currency Conversion Block
        const startCol = 23;
        sheet.getCell(2, startCol).value = "Currency conversion to INR:";
        sheet.getCell(2, startCol).fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: 'FFC000' } };
        sheet.getCell(2, startCol).font = { bold: true };
        sheet.getCell(2, startCol + 2).value = "Note: Please don’t change this conversion rate without informing Finance.";
        sheet.getCell(2, startCol + 2).font = { bold: true, color: { argb: 'FF0000' } };

        const currencies = Object.keys(rates || {});
        const rateMap = rates || {};

        function writeCurrencyRow(rowNumber: number, currencyList: string[]) {
            let col = startCol;
            currencyList.forEach(cur => {
                const codeCell = sheet.getCell(rowNumber, col);
                codeCell.value = cur;
                codeCell.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: 'BDD7EE' } };
                codeCell.alignment = { horizontal: 'center' };

                const rateCell = sheet.getCell(rowNumber, col + 1);
                rateCell.value = toNumber(rateMap[cur]);
                rateCell.numFmt = '0.00';
                rateCell.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: 'FCE4D6' } };
                rateCell.alignment = { horizontal: 'center' };

                col += 2;
            });
        }
        writeCurrencyRow(3, currencies.slice(0, 3));
        writeCurrencyRow(4, currencies.slice(3));

        // --------------------
        // Table Headers
        const headers = [
            "Sl. No.",
            "Sales for the Month",
            "Practice",
            "Type",
            "Project Code",
            "Project Name",
            "Customer Code",
            "Customer Name",
            "Customer Address",
            "Client Application Manager",
            "Project Type",
            "Proposal / SOW No.",
            "PO No. / Agreement No. and Date",
            "PO / Agreement / SOW / MSA / Quotation / Contract Type",
            "PO / Agreement / SOW / MSA / Quotation / Contract No",
            "Date of PO / Agreement / SOW / MSA / Quotation Contract",
            "Billing Milestone / Month",
            "Employee ID",
            "Project Team",
            "Effort",
            "UOM",
            "Rate",
            "Rate UOM",
            "Currency",
            "Revenue",
            "Total Revenue in INR",
            "Billable",
            "Invoice to be Raised to Customer",
            "Remarks"
        ];

        const headerRow = sheet.addRow(headers);
        headerRow.eachCell((cell: any, i: any) => {
            cell.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: 'FFFF00' } };
            cell.font = { bold: true };
            cell.alignment = { horizontal: 'center', vertical: 'middle' };
            sheet.getColumn(i).width = Math.max((headers[i - 1]?.length || 10) + 5, 12);
        });

        // Freeze header row
        sheet.views = [{ state: 'frozen', ySplit: headerRow.number }];

        // --------------------
        // Data Rows with alternate colouring and numeric formatting
        let slNo = 1;
        let totalEffort = 0;
        let totalRevenue = 0;
        let totalRevenueInInr = 0;

        indents.forEach(indent => {
            if (indent.employeeDetails) {
                indent.employeeDetails.forEach((emp: any) => {
                    // convert numeric values
                    const hoursVal = toNumber(emp.hours);
                    const revenueVal = toNumber(emp.amount);
                    const totalRevInInrVal = toNumber(emp.totalRevenue);

                    // Prepare date values safely
                    // salesForTheMonth might be a date string or month identifier; try Date parsing fallback to null
                    let salesMonthDate: Date | null = null;
                    if (indent.salesForTheMonth) {
                        const parsed = new Date(indent.salesForTheMonth);
                        salesMonthDate = isNaN(parsed.getTime()) ? null : parsed;
                    }

                    let sourceDocDate: Date | null = null;
                    if (indent.sourceDocumentDate) {
                        const parsed = new Date(indent.sourceDocumentDate);
                        sourceDocDate = isNaN(parsed.getTime()) ? null : parsed;
                    }

                    // Add the row with numeric values (not strings)
                    const rowValues = [
                        slNo++,
                        salesMonthDate,
                        indent.practiceType,
                        indent.type,
                        indent.projectCode,
                        indent.projectName,
                        indent.customerCode,
                        indent.customerName,
                        indent.customerAddress,
                        indent.customerManager,
                        indent.projectType,
                        indent.proposalSowNo,
                        indent.poRefDate,
                        indent.sourceDocumentType,
                        indent.sourceDocumentNo,
                        sourceDocDate,
                        indent.billingMilestoneMonth,
                        emp.employeeCode,
                        emp.firstName,
                        hoursVal,            // Effort (numeric)
                        emp.billingType,
                        toNumber(emp.billingRate),
                        emp.rateUnit,
                        emp.billingCurrency,
                        revenueVal,          // Revenue (numeric)
                        totalRevInInrVal,    // Total Revenue in INR (numeric)
                        emp.billable ? "Y" : "N",
                        toNumber(emp.invoiceAmount),
                        emp.remark
                    ];

                    const row = sheet.addRow(rowValues);

                    // alternate row fill (skip header)
                    if ((row.number % 2) === 0) {
                        row.eachCell((cell: any) => {
                            cell.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: 'FFFDE9D9' } };
                        });
                    }

                    // set numeric formatting & alignment for numeric columns:
                    // Effort -> col 20, Revenue -> col 25, TotalRevenueInINR -> col 26, Invoice -> col 28
                    row.getCell(20).numFmt = '#,##0.00';
                    row.getCell(20).alignment = { horizontal: 'right' };

                    row.getCell(25).numFmt = '#,##0.00';
                    row.getCell(25).alignment = { horizontal: 'right' };

                    row.getCell(26).numFmt = '#,##0.00';
                    row.getCell(26).alignment = { horizontal: 'right' };

                    row.getCell(28).numFmt = '#,##0.00';
                    row.getCell(28).alignment = { horizontal: 'right' };

                    // date formatting
                    if (row.getCell(2).value instanceof Date) {
                        row.getCell(2).numFmt = 'mmm-yy'; // Sales for the Month
                        row.getCell(2).alignment = { horizontal: 'center' };
                    }
                    if (row.getCell(16).value instanceof Date) {
                        row.getCell(16).numFmt = 'dd-mmm-yy';
                        row.getCell(16).alignment = { horizontal: 'center' };
                    }

                    // accumulate totals
                    totalEffort += hoursVal;
                    totalRevenue += revenueVal;
                    totalRevenueInInr += totalRevInInrVal;
                });
            }
        });

        // --------------------
        // Total row (place "Total" label and numeric totals under Effort, Revenue, Total Revenue)
        const totalRowValues: any[] = new Array(headers.length).fill(null);
        // put a label somewhere to indicate totals - choose column 15 (index 14, adjust as needed)
        totalRowValues[14] = 'Total'; // zero-based index, will appear under column 15
        totalRowValues[19] = totalEffort;         // Effort -> column 20
        totalRowValues[24] = totalRevenue;        // Revenue -> column 25
        totalRowValues[25] = totalRevenueInInr;   // Total Revenue in INR -> column 26

        const totalRow = sheet.addRow(totalRowValues);

        // style totals row
        totalRow.eachCell((cell: any, colNumber: number) => {
            cell.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: 'FFFFFF00' } }; // yellow
            cell.font = { bold: true };
            // apply number formats to the numeric cells specifically
            if ([20, 25, 26].includes(colNumber)) {
                cell.numFmt = '#,##0.00';
                cell.alignment = { horizontal: 'right' };
            }
        });

        // --------------------
        // Build summary maps
        const currencySummary: any = {};
        const projectTypeSummary: any = {};

        indents.forEach(indent => {
            if (indent.employeeDetails) {
                indent.employeeDetails.forEach((emp: any) => {
                    const currency = emp.billingCurrency || "Others";
                    const projType = indent.type || "Others";

                    const revenue = toNumber(emp.amount);
                    const totalInr = toNumber(emp.totalRevenue);

                    // Currency summary
                    if (!currencySummary[currency]) currencySummary[currency] = { revenue: 0, inr: 0 };
                    currencySummary[currency].revenue += revenue;
                    currencySummary[currency].inr += totalInr;

                    // Project Type summary
                    if (!projectTypeSummary[projType]) projectTypeSummary[projType] = { revenue: 0, inr: 0 };
                    projectTypeSummary[projType].revenue += revenue;
                    projectTypeSummary[projType].inr += totalInr;
                });
            }
        });

        // --------------------
        // Currency Summary Table
        sheet.addRow([]); // blank row
        let currencyHeader = sheet.addRow(["Currency", "Revenue", "Total Revenue in INR"]);
        currencyHeader.eachCell((cell: any) => {
            cell.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: 'FFFF00' } }; // yellow
            cell.font = { bold: true };
        });

        // Fill rows
        Object.keys(currencySummary).forEach(cur => {
            const row = sheet.addRow([
                cur,
                currencySummary[cur].revenue,
                currencySummary[cur].inr
            ]);
            row.getCell(2).numFmt = "#,##0.00";
            row.getCell(3).numFmt = "#,##0.00";
        });

        // Currency total row
        const curTotalRow = sheet.addRow(["Total in INR", "", Object.values(currencySummary).reduce((s, v: any) => s + v.inr, 0)]);
        curTotalRow.eachCell((cell: any) => {
            cell.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: 'FF92D050' } }; // green
            cell.font = { bold: true };
        });
        curTotalRow.getCell(3).numFmt = "#,##0.00";

        // --------------------
        // Project Type Summary Table
        sheet.addRow([]); // blank row
        let projHeader = sheet.addRow(["Project Type", "Revenue", "Total Revenue in INR"]);
        projHeader.eachCell((cell: any) => {
            cell.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: 'FFFF00' } }; // yellow
            cell.font = { bold: true };
        });

        // Fill rows
        Object.keys(projectTypeSummary).forEach(pt => {
            const row = sheet.addRow([
                pt,
                projectTypeSummary[pt].revenue,
                projectTypeSummary[pt].inr
            ]);
            row.getCell(2).numFmt = "#,##0.00";
            row.getCell(3).numFmt = "#,##0.00";
        });

        // Project type total row
        const ptTotalRow = sheet.addRow(["Total in INR", "", Object.values(projectTypeSummary).reduce((s, v: any) => s + v.inr, 0)]);
        ptTotalRow.eachCell((cell: any) => {
            cell.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: 'FF92D050' } }; // green
            cell.font = { bold: true };
        });
        ptTotalRow.getCell(3).numFmt = "#,##0.00";

        // auto-width small tweak (optional)
        sheet.columns.forEach(col => {
            if (!col.width) col.width = 12;
        });



        workbook.xlsx.writeBuffer().then((buf) => {
            saveAs(new Blob([buf]), `Billing_Indent_${month}_${year}.xlsx`);
        });
    }



}

