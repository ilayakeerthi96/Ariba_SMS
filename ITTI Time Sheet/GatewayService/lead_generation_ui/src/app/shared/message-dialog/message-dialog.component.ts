import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatDialogModule } from '@angular/material/dialog';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-message-dialog',
  standalone: true,
  imports: [MatIconModule, MatDialogModule, CommonModule],
  templateUrl: './message-dialog.component.html',
  styleUrls: ['./message-dialog.component.css']
})
export class MessageDialogComponent {
  iconMap = {
    success: 'check_circle',
    error: 'error',
    warning: 'warning',
    info: 'info'
  };

  constructor(
    public dialogRef: MatDialogRef<MessageDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { type: 'success' | 'error' | 'warning' | 'info'; title: string; message: string;showConfirmButtons:boolean }
  ) {}

  closeDialog(): void {
    this.dialogRef.close();
  }

   onConfirm(): void {
    this.dialogRef.close('confirm');
  }

  onCancel(): void {
    this.dialogRef.close('cancel');
  }
}
