// loading-spinner.component.ts
import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-loading-spinner',
  imports:[CommonModule],
  templateUrl: './loading-spinner.component.html',
  styleUrls: ['./loading-spinner.component.css'],
  standalone: true
})
export class LoadingSpinnerComponent {
    @Input() isLoading = false; 
}
