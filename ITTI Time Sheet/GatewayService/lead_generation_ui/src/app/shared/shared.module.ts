// shared/shared.module.ts
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MAT_DATE_FORMATS, DateAdapter, MAT_DATE_LOCALE } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatNativeDateModule } from '@angular/material/core';

import { APP_DATE_FORMATS } from './date-format.config';

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    MatDatepickerModule,
    MatFormFieldModule,
    MatInputModule,
    MatNativeDateModule
  ],
  exports: [
    MatDatepickerModule,
    MatFormFieldModule,
    MatInputModule,
    MatNativeDateModule
  ],
  providers: [
    { provide: MAT_DATE_FORMATS, useValue: APP_DATE_FORMATS },
    { provide: MAT_DATE_LOCALE, useValue: 'en-GB' } // Ensures dd-MM-yyyy parsing
  ]
})
export class SharedModule {
  constructor(private adapter: DateAdapter<any>) {
    this.adapter.setLocale('en-GB'); // Optional: force locale globally
  }
}
