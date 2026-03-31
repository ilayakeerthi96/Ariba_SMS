import { Injectable } from '@angular/core';
import { DatePipe } from '@angular/common';

@Injectable({
  providedIn: 'root'
})
export class DateUtil {
  constructor(private datePipe: DatePipe) {}

  convertDate(dateStr: string): string | null {
    if(!dateStr){
        return null;
    }
    const parts = dateStr.split('-');
    if (parts.length !== 3) return null;

    const [day, mon, year] = parts;

    const months: { [key: string]: number } = {
      Jan: 0, Feb: 1, Mar: 2, Apr: 3, May: 4, Jun: 5,
      Jul: 6, Aug: 7, Sep: 8, Oct: 9, Nov: 10, Dec: 11
    };

    const month = months[mon];
    const fullYear = Number(year) + 2000;

    const date = new Date(fullYear, month, +day);
     if (isNaN(date.getTime())) return null;
    return this.datePipe.transform(date, 'yyyy-MM-dd');
  }
}
