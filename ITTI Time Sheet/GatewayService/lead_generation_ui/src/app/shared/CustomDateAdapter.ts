import { Injectable } from "@angular/core";
import { NativeDateAdapter } from "@angular/material/core";

@Injectable()
export class CustomDateAdapter extends NativeDateAdapter {
  override format(date: Date, displayFormat: any): string {
    if (displayFormat === 'input') {
      const day = date.getDate().toString().padStart(2, '0');
      const month = date.toLocaleString('en-US', { month: 'short' }); // 'Jan', 'Feb'
      const year = date.getFullYear();
      return `${day} ${month} ${year}`;
    }
    return super.format(date, displayFormat);
  }
}
