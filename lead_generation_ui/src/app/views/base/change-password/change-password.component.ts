
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { DataService } from '../../../shared/service/DataService';
import { MessageService } from '../../../shared/service/message.service';
import {
  ContainerComponent, RowComponent, ColComponent, CardComponent,
  CardHeaderComponent, CardBodyComponent, FormDirective, FormLabelDirective,
  FormControlDirective, ButtonDirective, SpinnerComponent
} from '@coreui/angular';
import { IconDirective } from '@coreui/icons-angular';
import { IconSetService } from '@coreui/icons-angular';



@Component({
  selector: 'app-change-password',
  templateUrl: './change-password.component.html',
  styleUrls: ['./change-password.component.css'],
  standalone: true,
  imports: [
    CommonModule, ContainerComponent, RowComponent, ColComponent,
    CardComponent, CardHeaderComponent, CardBodyComponent, FormDirective,
    FormLabelDirective, FormControlDirective, ButtonDirective, IconDirective,
    ReactiveFormsModule, SpinnerComponent
  ]
})
export class ChangePasswordComponent implements OnInit {
  changePasswordForm!: FormGroup;
  isLoading: boolean = false;
  showOldPassword: boolean = false;
  showNewPassword: boolean = false;
  showConfirmPassword: boolean = false;

  userId!: number;
  userRole!: string;
  userName!: string;
  mustChangePassword: boolean = false;

  constructor(
    private fb: FormBuilder,
    private dataService: DataService,
    private messageService: MessageService,
    private router: Router,
    private iconSetService: IconSetService  
  )
   {}

  ngOnInit(): void {
    this.initForm();
    this.loadUserDetails();
  }

  initForm(): void {
    this.changePasswordForm = this.fb.group({
      currentPassword: ['', [Validators.required]],
      newPassword: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', [Validators.required]]
    }, {
      validators: this.passwordMatchValidator
    });
  }

  loadUserDetails(): void {
    this.userId = parseInt(localStorage.getItem('userId') || '0');
    this.userRole = localStorage.getItem('role') || '';
    this.userName = localStorage.getItem('fullName') || 'User';
    this.mustChangePassword = localStorage.getItem('mustChangePassword') === 'true';

    console.log('✅ User Details:', {
      userId: this.userId,
      userRole: this.userRole,
      userName: this.userName,
      mustChangePassword: this.mustChangePassword
    });

    if (!this.userId || !this.userRole) {
      this.messageService.showMessage('error', 'Error', 'User session not found');
      this.router.navigate(['/login']);
    }
  }

  passwordMatchValidator(group: FormGroup) {
    const newPassword = group.get('newPassword')?.value;
    const confirmPassword = group.get('confirmPassword')?.value;
    return newPassword === confirmPassword ? null : { passwordMismatch: true };
  }

  togglePasswordVisibility(field: string): void {
    switch(field) {
      case 'old':
        this.showOldPassword = !this.showOldPassword;
        break;
      case 'new':
        this.showNewPassword = !this.showNewPassword;
        break;
      case 'confirm':
        this.showConfirmPassword = !this.showConfirmPassword;
        break;
    }
  }

  onSubmit(): void {
    if (this.changePasswordForm.invalid) {
      Object.keys(this.changePasswordForm.controls).forEach(key => {
        this.changePasswordForm.get(key)?.markAsTouched();
      });
      return;
    }

    this.isLoading = true;
    
    // ✅ Correct payload matching backend DTO
    const passwordData = {
      currentPassword: this.changePasswordForm.value.currentPassword,
      newPassword: this.changePasswordForm.value.newPassword,
      confirmPassword: this.changePasswordForm.value.confirmPassword
    };

    console.log('🔵 Sending password change request:', {
      userId: this.userId,
      userRole: this.userRole,
      payload: passwordData
    });

    // Determine which API to call based on user role
    let apiCall;
    if (this.userRole === 'ORGANIZATION_ADMIN') {
      apiCall = this.dataService.changeOrganizationAdminPassword(this.userId, passwordData);
    } else {
      // For hierarchy users
      apiCall = this.dataService.changeHierarchyUserPassword(this.userId, passwordData);
    }

    apiCall.subscribe({
      next: (response: any) => {
        this.isLoading = false;
        console.log('✅ Password change response:', response);
        
        if (response.success) {
          this.messageService.showMessage('success', 'Success', response.message || 'Password changed successfully');
          this.changePasswordForm.reset();
          
          // Clear mustChangePassword flag
          localStorage.setItem('mustChangePassword', 'false');
          
          // Navigate to dashboard after successful change
          setTimeout(() => {
            const dashboardRoute = this.getDashboardRoute();
            this.router.navigate([dashboardRoute]);
          }, 1500);
        } else {
          this.messageService.showMessage('error', 'Error', response.message || 'Failed to change password');
        }
      },
      error: (error: any) => {
        this.isLoading = false;
        console.error('❌ Error changing password:', error);
        
        let errorMessage = 'Failed to change password. Please try again.';
        
        if (error.error?.message) {
          errorMessage = error.error.message;
        } else if (error.error?.error) {
          errorMessage = error.error.error;
        } else if (error.status === 400) {
          errorMessage = 'Invalid password data. Please check your inputs.';
        } else if (error.status === 401) {
          errorMessage = 'Current password is incorrect.';
        } else if (error.status === 404) {
          errorMessage = 'User not found.';
        }
        
        this.messageService.showMessage('error', 'Error', errorMessage);
      }
    });
  }

  getDashboardRoute(): string {
    switch(this.userRole) {
      case 'SUPER_ADMIN': return '/superadmin-dashboard';
      case 'ORGANIZATION_ADMIN': return '/orgadmin-dashboard';
      case 'ROLE_BUYER': return '/rfq-dashboard';
      case 'CEO': return '/ceo-dashboard';
      case 'COO': return '/coo-dashboard';
      case 'PROCUREMENT': return '/procurement-dashboard';
      case 'FINANCE': return '/finance-dashboard';
      case 'MANAGER': return '/manager-dashboard';
      default: return '/dashboard';
    }
  }

  cancel(): void {
    // If must change password, prevent cancel
    if (this.mustChangePassword) {
      this.messageService.showMessage('warning', 'Action Required', 
        'You must change your password before accessing the system.');
      return;
    }
    this.router.navigate([this.getDashboardRoute()]);
  }

  // Form validation helpers
  get currentPassword() { return this.changePasswordForm.get('currentPassword'); }
  get newPassword() { return this.changePasswordForm.get('newPassword'); }
  get confirmPassword() { return this.changePasswordForm.get('confirmPassword'); }
}