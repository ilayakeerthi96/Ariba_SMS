// session-timer.component.ts
import { Component } from '@angular/core';
import { AuthService } from '../../shared/service/AuthService';
import { Observable } from 'rxjs/internal/Observable';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-session-timer',
  imports:[CommonModule],
  template: `
   <div class="alert alert-warning p-2 text-end small" style="float:right;">
  Session expires in: <strong>{{ time$ | async }}</strong>
</div>
  `
})
export class SessionTimerComponent {
  time$!: Observable<string>;
  constructor(private authService: AuthService) {}
   ngOnInit(): void {
    this.time$ = this.authService.remainingTime$;
  }
}
