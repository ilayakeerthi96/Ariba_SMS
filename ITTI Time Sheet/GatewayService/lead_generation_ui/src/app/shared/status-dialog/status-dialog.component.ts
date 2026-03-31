import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatDialogModule } from '@angular/material/dialog';
import { CommonModule } from '@angular/common';
@Component({
  selector: 'app-status-dialog',
  imports: [CommonModule,MatDialogModule],
  templateUrl: './status-dialog.component.html',
  styleUrls: ['./status-dialog.component.scss']
})
export class StatusDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<StatusDialogComponent>,
    @Inject(MAT_DIALOG_DATA)
    public data: { title: string; message: string; status: 'success' | 'error' }
  ) {}
}
