import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { DataService } from '../../../shared/service/DataService';
import { MessageService } from '../../../shared/service/message.service';
import {
  ContainerComponent, RowComponent, ColComponent, CardComponent,
  CardHeaderComponent, CardBodyComponent, CardFooterComponent,
  ButtonDirective, FormControlDirective, AlertComponent
} from '@coreui/angular';
import { IconDirective } from '@coreui/icons-angular';

@Component({
  selector: 'app-orgadmin-profile',
  templateUrl: './orgadmin-profile.component.html',
  standalone: true,
  imports: [
    CommonModule, ReactiveFormsModule,
    ContainerComponent, RowComponent, ColComponent,
    CardComponent, CardHeaderComponent, CardBodyComponent, CardFooterComponent,
    ButtonDirective, FormControlDirective, AlertComponent, IconDirective
  ]
})
export class OrgAdminProfileComponent implements OnInit {

  profileForm!: FormGroup;
  adminId!: number;

  logoFile: File | null = null;
  logoPreview: string | null = null;

  isSaving = false;
  isUploadingLogo = false;
  errorMessage: string | null = null;
  successMessage: string | null = null;

  constructor(
    private fb: FormBuilder,
    private dataService: DataService,
    private messageService: MessageService
  ) {}

  ngOnInit(): void {
    this.adminId = Number(localStorage.getItem('userId'));
    this.initForm();
    this.loadCurrentData();
  }

  private initForm(): void {
    this.profileForm = this.fb.group({
      fullName: [''],
      phone: [''],
      companyName: [''],
      organizationName: [''],
      password: ['']
    });
  }

  private loadCurrentData(): void {
    this.dataService.getOrganizationAdminById(this.adminId).subscribe({
      next: (res: any) => {
        if (res?.success && res?.data) {
          const d = res.data;
          this.profileForm.patchValue({
            fullName: d.fullName || '',
            phone: d.phone || '',
            companyName: d.companyName || '',
            organizationName: d.organizationName || ''
          });
          if (d.logoBase64) {
            this.logoPreview = d.logoBase64;
          }
        }
      },
      error: (err: any) => console.error('Error loading profile', err)
    });
  }

  onLogoSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files[0]) {
      this.logoFile = input.files[0];
      const reader = new FileReader();
      reader.onload = (e: any) => { this.logoPreview = e.target.result; };
      reader.readAsDataURL(this.logoFile);
    }
  }

  saveProfile(): void {
    this.isSaving = true;
    this.errorMessage = null;
    this.successMessage = null;

    const payload: any = {};
    const v = this.profileForm.value;
    if (v.fullName?.trim()) payload.fullName = v.fullName.trim();
    if (v.phone?.trim()) payload.phone = v.phone.trim();
    if (v.companyName?.trim()) payload.companyName = v.companyName.trim();
    if (v.organizationName?.trim()) payload.organizationName = v.organizationName.trim();
    if (v.password?.trim()) payload.password = v.password.trim();

    this.dataService.updateOrgAdminProfile(this.adminId, payload).subscribe({
      next: (res: any) => {
        this.isSaving = false;
        if (res?.success) {
          this.successMessage = 'Profile updated successfully!';
          // Update localStorage
          if (payload.fullName) localStorage.setItem('fullName', payload.fullName);
          if (payload.companyName) localStorage.setItem('companyName', payload.companyName);
          if (payload.phone) localStorage.setItem('phone', payload.phone);
          this.messageService.showMessage('success', 'Success', 'Profile updated!');
        } else {
          this.errorMessage = res?.message || 'Update failed';
        }
      },
      error: (err: any) => {
        this.isSaving = false;
        this.errorMessage = err?.error?.message || 'Update failed';
      }
    });
  }

  uploadLogo(): void {
    if (!this.logoFile) return;
    this.isUploadingLogo = true;
    this.errorMessage = null;

    this.dataService.uploadOrgAdminLogo(this.adminId, this.logoFile).subscribe({
      next: (res: any) => {
        this.isUploadingLogo = false;
        if (res?.success) {
          this.successMessage = 'Logo uploaded successfully!';
          this.logoFile = null;
          this.messageService.showMessage('success', 'Success', 'Logo uploaded!');
        } else {
          this.errorMessage = res?.message || 'Upload failed';
        }
      },
      error: (err: any) => {
        this.isUploadingLogo = false;
        this.errorMessage = err?.error?.message || 'Upload failed';
      }
    });
  }
}