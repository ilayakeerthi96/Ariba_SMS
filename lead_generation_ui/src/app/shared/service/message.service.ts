import { Injectable } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { MessageDialogComponent } from '../../shared//message-dialog/message-dialog.component';

@Injectable({
  providedIn: 'root'
})
export class MessageService {
  private isDialogOpen = false; // ✅ Prevent multiple dialogs

  constructor(private dialog: MatDialog, private router: Router) {}

  showMessage(
  type: 'success' | 'error' | 'warning' | 'info',
  title: string,
  message: string,
  redirectUrl?: string
) {
  if (this.isDialogOpen) return; // Prevent multiple calls

  this.isDialogOpen = true;

  const dialogRef = this.dialog.open(MessageDialogComponent, {
    width: '350px',
    data: { type, title, message }
  });

  dialogRef.afterClosed().subscribe(() => {
    this.isDialogOpen = false;

    if (!redirectUrl) return;

    // If redirecting to login, use full reload
    if (redirectUrl === '/login') {
      window.location.href = redirectUrl;
      return;
    }

    // If already on the same route, force reload
    if (this.router.url === redirectUrl) {
      this.router.navigateByUrl('/', { skipLocationChange: true }).then(() => {
        this.router.navigate([redirectUrl]);
      });
    } else {
      this.router.navigate([redirectUrl]);
    }
  });
}

}
