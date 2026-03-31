// session-expiry-dialog.component.ts
import {
  Component,
  OnInit,
  OnDestroy,
  Input,
  Output,
  EventEmitter
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { Subscription, interval } from 'rxjs';

@Component({
  selector: 'app-session-expiry-dialog',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './session-expiry-dialog.component.html',
  styleUrls: ['./session-expiry-dialog.component.css']
})
export class SessionExpiryDialogComponent implements OnInit, OnDestroy {

  /** Pass the expiry timestamp (ms) so the dialog can show its own live countdown */
  @Input() expiryMs: number = 0;

  @Output() extend = new EventEmitter<void>();
  @Output() logout = new EventEmitter<void>();

  isVisible = false;
  countdown = '05:00';
  progressPercent = 100;

  /** Total warning window = 5 minutes (300 seconds) */
  private readonly WARN_WINDOW_MS = 5 * 60 * 1000;

  private tickSub?: Subscription;

  ngOnInit(): void {
    this.isVisible = true;
    this.startCountdown();
  }

  ngOnDestroy(): void {
    this.tickSub?.unsubscribe();
  }

  private startCountdown(): void {
    this.tickSub = interval(1000).subscribe(() => {
      const diff = this.expiryMs - Date.now();

      if (diff <= 0) {
        this.countdown = '00:00';
        this.progressPercent = 0;
        this.tickSub?.unsubscribe();
        // Auto-logout when timer hits zero inside dialog
        this.logout.emit();
        return;
      }

      this.countdown = this.formatMs(diff);
      // Progress shrinks from 100 → 0 over WARN_WINDOW_MS
      this.progressPercent = Math.max(0, Math.min(100, (diff / this.WARN_WINDOW_MS) * 100));
    });
  }

  private formatMs(ms: number): string {
    const totalSecs = Math.floor(ms / 1000);
    const m = Math.floor(totalSecs / 60).toString().padStart(2, '0');
    const s = (totalSecs % 60).toString().padStart(2, '0');
    return `${m}:${s}`;
  }

  onExtend(): void {
    this.isVisible = false;
    this.tickSub?.unsubscribe();
    this.extend.emit();
  }

  onLogout(): void {
    this.isVisible = false;
    this.tickSub?.unsubscribe();
    this.logout.emit();
  }

  /** Prevent accidental close on overlay click */
  onOverlayClick(event: MouseEvent): void {
    // Only close if clicking the overlay itself, not the dialog box
    if ((event.target as HTMLElement).classList.contains('session-dialog-overlay')) {
      // Do nothing — user must explicitly choose an action
    }
  }
}