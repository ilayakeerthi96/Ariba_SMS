// shared/date-format.config.ts
import { MAT_DATE_FORMATS } from '@angular/material/core';

export const APP_DATE_FORMATS = {
  parse: {
    dateInput: 'input',
  },
  display: {
    dateInput: 'input',
    monthYearLabel: 'MMM yyyy',
    dateA11yLabel: 'dd MMM yyyy',
    monthYearA11yLabel: 'MMMM yyyy',
  },
};