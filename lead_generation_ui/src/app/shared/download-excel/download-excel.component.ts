import { Component, Input } from '@angular/core';
import * as XLSX from 'xlsx-js-style'; // Using the styling library
import { saveAs } from 'file-saver';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-download-excel',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div (click)="exportToExcel()" style="display: inline-block; cursor: pointer;">
      <ng-content></ng-content>
    </div>
  `
})
export class DownloadExcelComponent {
  @Input() dataSource?: any[];
  @Input() fileName: string = 'FilteredData.xlsx';
  @Input() columnOrder?: string[];
  @Input() columnHeaders?: { [key: string]: string };
  @Input() totalRowColumns: string[] = [];

  // List of columns to ALWAYS hide
  private excludedColumns = ['isDeleted', 'deleted', 'password', 'otp']; 

  exportToExcel(): void {
    if (!this.dataSource || (Array.isArray(this.dataSource) && this.dataSource.length === 0)) {
      console.warn('No data available.');
      return;
    }

    let dataToExport: any[] = Array.isArray(this.dataSource) ? this.dataSource : (this.dataSource as any).data;

    if (!dataToExport || dataToExport.length === 0) {
      alert('No data available to export.');
      return;
    }

    // Get initial columns
    let columns = this.columnHeaders ? Object.keys(this.columnHeaders) : Object.keys(dataToExport[0]);

    // 1. Remove 'isDeleted' and other unwanted columns
    columns = columns.filter(col => !this.excludedColumns.includes(col));

    const reorderedData = dataToExport.map(row => {
      const formattedRow: any = {};
      columns.forEach(col => {
        let value = row[col];

        if (col.toLowerCase().includes('date') && value) {
          const parsedDate = new Date(value);
          if (!isNaN(parsedDate.getTime())) {
            value = parsedDate;
          }
        }

        // 2. Determine Header Name (Custom OR Auto-Formatted)
        let headerName = '';
        if (this.columnHeaders && this.columnHeaders[col]) {
           headerName = this.columnHeaders[col];
        } else {
           // Auto-format: "companyName" -> "Company Name"
           headerName = this.formatHeader(col);
        }

        formattedRow[headerName] = value;
      });
      return formattedRow;
    });

    // Total Row Logic
    if (this.totalRowColumns?.length) {
      const totalRow: any = {};
      // We need to map the total row keys to the NEW formatted headers
      const headerMap: any = {};
      columns.forEach(col => {
         const hName = (this.columnHeaders && this.columnHeaders[col]) ? this.columnHeaders[col] : this.formatHeader(col);
         headerMap[col] = hName;
      });

      columns.forEach((col, index) => {
        const finalHeader = headerMap[col];

        if (index === 0) {
          totalRow[finalHeader] = 'Total';
        } else if (this.totalRowColumns.includes(col)) {
          const currencyTotals: { [currency: string]: number } = {};
          dataToExport.forEach(row => {
            const currency = row['currency'] || 'N/A';
            const value = +row[col] || 0;
            currencyTotals[currency] = (currencyTotals[currency] || 0) + value;
          });
          totalRow[finalHeader] = Object.entries(currencyTotals)
            .map(([curr, total]) => `${curr} ${total.toLocaleString(undefined, { minimumFractionDigits: 2 })}`)
            .join(', ');
        } else {
          totalRow[finalHeader] = '';
        }
      });
      reorderedData.push(totalRow);
    }

    // Create Sheet
    const worksheet: XLSX.WorkSheet = XLSX.utils.json_to_sheet(reorderedData);

    // Set Column Widths
    const columnWidths = Object.keys(reorderedData[0]).map(() => ({ wch: 20 }));
    worksheet['!cols'] = columnWidths;

    // Apply Styling
    const range = XLSX.utils.decode_range(worksheet['!ref']!);
    
    for (let R = range.s.r; R <= range.e.r; R++) {
      for (let C = range.s.c; C <= range.e.c; C++) {
        const cellAddress = XLSX.utils.encode_cell({ r: R, c: C });
        const cell = worksheet[cellAddress];

        if (!cell) continue;

        // Check Header Name for Alignment Logic
        const headerAddress = XLSX.utils.encode_cell({ r: 0, c: C });
        const headerCell = worksheet[headerAddress];
        const headerName = headerCell ? headerCell.v.toString().toLowerCase() : '';

        if (!cell.s) cell.s = {};
        if (!cell.s.alignment) cell.s.alignment = {};

        // --- HEADER ROW ---
        if (R === 0) {
          cell.s.font = { bold: true, color: { rgb: "FFFFFF" } };
          cell.s.fill = { fgColor: { rgb: "4F81BD" } }; 
          cell.s.alignment = { horizontal: 'center', vertical: 'center' };
        } 
        // --- DATA ROWS ---
        else {
          // Center Align ID
          if (headerName.includes('id') || headerName.includes('sl_no')) {
            cell.s.alignment.horizontal = 'center';
          }
          // Right Align Numbers
          else if (cell.t === 'n') { 
            cell.s.alignment.horizontal = 'right';
          }
          // Left Align Text
          else {
            cell.s.alignment.horizontal = 'left';
          }
        }
      }
    }

    const workbook: XLSX.WorkBook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(workbook, worksheet, 'Sheet1');

    const excelBuffer: any = XLSX.write(workbook, { bookType: 'xlsx', type: 'array' });
    const file = new Blob([excelBuffer], { type: 'application/octet-stream' });
    saveAs(file, this.fileName);
  }

  // Helper function to capitalize headers (e.g., "companyName" -> "Company Name")
  private formatHeader(str: string): string {
    if (!str) return '';
    // 1. Add space before capital letters
    // 2. Capitalize the first letter
    return str
        .replace(/([A-Z])/g, ' $1') 
        .replace(/^./, (s) => s.toUpperCase()) 
        .trim();
  }
}