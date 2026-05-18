// import { Component, OnInit } from '@angular/core';
// import { FormBuilder, FormGroup, Validators, AbstractControl, ReactiveFormsModule } from '@angular/forms';
// import { Router, RouterLink } from '@angular/router';
// import { CommonModule } from '@angular/common';
// import { HttpClient, HttpClientModule } from '@angular/common/http';
// import {
//   ContainerComponent,
//   RowComponent,
//   ColComponent,
//   CardComponent,
//   CardBodyComponent,
//   ButtonDirective
// } from '@coreui/angular';
// import { environment } from '../../../environments/environment';

// @Component({
//   selector: 'app-supplier-register',
//   templateUrl: './supplier-register.component.html',
//   styleUrls: ['./supplier-register.component.css'],
//   standalone: true,
//   imports: [
//     CommonModule,
//     ReactiveFormsModule,
//     HttpClientModule,
//     RouterLink,
//     ContainerComponent,
//     RowComponent,
//     ColComponent,
//     CardComponent,
//     CardBodyComponent,
//     ButtonDirective
//   ]
// })
// export class SupplierRegisterComponent implements OnInit {

//   registerForm!: FormGroup;
//   currentStep = 1;
//   maxStep = 3;
//   isSubmitting = false;
//   submitted = false;
//   errorMessage: string | null = null;
//   successMessage: string | null = null;
//   organizations: string[] = [];
//   isLoadingOrgs = false;

//   // ── Logo ──────────────────────────────────────────────────────
//   selectedLogoFile: File | null = null;
//   logoPreview: string | null = null;
//   maxLogoSize = 5 * 1024 * 1024;
//   allowedLogoTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif', 'image/svg+xml'];

//   companyTypes = [
//     'Manufacturing', 'Trading', 'Services', 'Distribution', 'Retail',
//     'IT / Software', 'Construction', 'Healthcare', 'Logistics', 'Others'
//   ];

//   industrySectors = [
//     'IT', 'Logistics', 'Electrical', 'Construction', 'Healthcare',
//     'Automotive', 'Textile', 'Food & Beverage', 'Mechanical',
//     'Chemical', 'Pharmaceuticals', 'FMCG', 'Others'
//   ];

//   constructor(
//     private fb: FormBuilder,
//     private router: Router,
//     private http: HttpClient
//   ) {}

//   ngOnInit(): void {
//     this.initForm();
//     this.loadOrganizations();
//   }

//   // ============================================================
//   // FORM INIT
//   // ============================================================
//   private initForm(): void {
//     this.registerForm = this.fb.group(
//       {
//         // ── Step 1 — Company Info ──────────────────────────────
//         companyName:             ['', [Validators.required, Validators.minLength(2)]],
//         companyType:             ['', Validators.required],
//         otherCompanyType:        [''],
//         industrySector:          ['', Validators.required],
//         otherIndustrySector:     [''],
//         website:                 [''],

//         // Primary contact person
//         contactPersonName:        ['', [Validators.required, Validators.minLength(2)]],
//         contactPersonDesignation: ['', Validators.required],
//         contactPersonEmail:       ['', [Validators.required, Validators.email]],
//         contactPersonPhone:       ['', [Validators.required, Validators.pattern(/^[0-9]{10}$/)]],

//         // Address
//         addressLine1:            ['', [Validators.required, Validators.minLength(5)]],
//         addressLine2:            [''],
//         city:                    ['', Validators.required],
//         state:                   ['', Validators.required],
//         postalCode:              ['', [Validators.required, Validators.pattern(/^[A-Za-z0-9\s\-]{3,10}$/)]],
//         country:                 ['India', Validators.required],

//         // Organization link
//         organizationCompanyName: ['', Validators.required],

//         // ── Step 2 — Logo & Registration ─────────────────────
//         logo:                    [null],
//         gstNumber:               [''],
//         panNumber:               [''],
//         tanNumber:               [''],

//         // ── Step 3 — Account Details ──────────────────────────
//         firstName:               ['', [Validators.required, Validators.minLength(2)]],
//         lastName:                ['', [Validators.required, Validators.minLength(2)]],
//         designation:             ['', Validators.required],
//         phone:                   ['', [Validators.required, Validators.pattern(/^[0-9]{10}$/)]],
//         email:                   ['', [Validators.required, Validators.email]],
//         password:                ['', [Validators.required, Validators.minLength(6)]],
//         confirmPassword:         ['', Validators.required]
//       },
//       { validators: this.passwordMatchValidator }
//     );
//   }

//   private passwordMatchValidator(group: AbstractControl) {
//     const pw  = group.get('password')?.value;
//     const cpw = group.get('confirmPassword')?.value;
//     return pw === cpw ? null : { passwordMismatch: true };
//   }

//   get f() { return this.registerForm.controls; }

//   // ============================================================
//   // OTHERS — company type / industry sector
//   // ============================================================
//   onCompanyTypeChange(event: any): void {
//     const ctrl = this.registerForm.get('otherCompanyType');
//     if (event.target.value === 'Others') {
//       ctrl?.setValidators([Validators.required, Validators.minLength(2)]);
//       ctrl?.setValue('');
//     } else {
//       ctrl?.clearValidators();
//       ctrl?.setValue(null);
//     }
//     ctrl?.updateValueAndValidity();
//   }

//   onIndustrySectorChange(event: any): void {
//     const ctrl = this.registerForm.get('otherIndustrySector');
//     if (event.target.value === 'Others') {
//       ctrl?.setValidators([Validators.required, Validators.minLength(2)]);
//       ctrl?.setValue('');
//     } else {
//       ctrl?.clearValidators();
//       ctrl?.setValue(null);
//     }
//     ctrl?.updateValueAndValidity();
//   }

//   // ============================================================
//   // LOGO HANDLING
//   // ============================================================
//   onLogoSelected(event: any): void {
//     const file: File = event.target.files[0];
//     if (!file) return;

//     if (!this.allowedLogoTypes.includes(file.type)) {
//       alert('Invalid file type. Please select a JPG, PNG, GIF or SVG image.');
//       event.target.value = '';
//       return;
//     }
//     if (file.size > this.maxLogoSize) {
//       alert('File too large. Logo must be smaller than 5 MB.');
//       event.target.value = '';
//       return;
//     }

//     this.selectedLogoFile = file;
//     const reader = new FileReader();
//     reader.onload = (e: any) => { this.logoPreview = e.target.result; };
//     reader.readAsDataURL(file);
//   }

//   removeLogo(): void {
//     this.selectedLogoFile = null;
//     this.logoPreview = null;
//     this.registerForm.patchValue({ logo: null });
//     const fileInput = document.getElementById('logoFileInput') as HTMLInputElement;
//     if (fileInput) fileInput.value = '';
//   }

//   private convertFileToBase64(file: File): Promise<string> {
//     return new Promise((resolve, reject) => {
//       const reader = new FileReader();
//       reader.onload = () => resolve(reader.result as string);
//       reader.onerror = error => reject(error);
//       reader.readAsDataURL(file);
//     });
//   }

//   // ============================================================
//   // LOAD ORGANIZATIONS
//   // ============================================================
//   loadOrganizations(): void {
//     this.isLoadingOrgs = true;
//     const url = `${environment.API_URL}leadcapture/api/supplier/register/organizations`;
//     console.log('📡 Loading organizations from:', url);

//     this.http.get<any>(url).subscribe({
//       next: (res) => {
//         this.isLoadingOrgs = false;
//         if (res?.data && Array.isArray(res.data)) {
//           this.organizations = res.data;
//           console.log('✅ Organizations loaded:', this.organizations);
//         } else {
//           console.warn('⚠️ Unexpected organizations response:', res);
//           this.organizations = [];
//         }
//       },
//       error: (err) => {
//         this.isLoadingOrgs = false;
//         console.error('❌ Could not load organizations:', err);
//         this.organizations = [];
//       }
//     });
//   }

//   // ============================================================
//   // STEP VALIDATION
//   // ============================================================
//   isStep1Valid(): boolean {
//     const step1Fields = [
//       'companyName', 'companyType', 'industrySector',
//       'contactPersonName', 'contactPersonDesignation',
//       'contactPersonEmail', 'contactPersonPhone',
//       'addressLine1', 'city', 'state', 'postalCode', 'country',
//       'organizationCompanyName'
//     ];

//     const standardValid = step1Fields.every(field => this.registerForm.get(field)?.valid);

//     const companyOtherValid =
//       this.registerForm.get('companyType')?.value === 'Others'
//         ? (this.registerForm.get('otherCompanyType')?.valid ?? false)
//         : true;

//     const industryOtherValid =
//       this.registerForm.get('industrySector')?.value === 'Others'
//         ? (this.registerForm.get('otherIndustrySector')?.valid ?? false)
//         : true;

//     return standardValid && companyOtherValid && industryOtherValid;
//   }

//   isStep2Valid(): boolean {
//     // Step 2 (logo + registration numbers) is fully optional — always valid
//     return true;
//   }

//   isStep3Valid(): boolean {
//     const step3Fields = ['firstName', 'lastName', 'designation', 'phone', 'email', 'password', 'confirmPassword'];
//     const fieldsValid = step3Fields.every(field => this.registerForm.get(field)?.valid);
//     const noMismatch = !this.registerForm.errors?.['passwordMismatch'];
//     return fieldsValid && noMismatch;
//   }

//   // ============================================================
//   // NAVIGATION
//   // ============================================================
//   nextStep(): void {
//     this.submitted = true;

//     if (this.currentStep === 1 && !this.isStep1Valid()) {
//       [
//         'companyName', 'companyType', 'otherCompanyType', 'industrySector', 'otherIndustrySector',
//         'contactPersonName', 'contactPersonDesignation', 'contactPersonEmail', 'contactPersonPhone',
//         'addressLine1', 'city', 'state', 'postalCode', 'country', 'organizationCompanyName'
//       ].forEach(f => this.registerForm.get(f)?.markAsTouched());
//       return;
//     }

//     if (this.currentStep === 2 && !this.isStep2Valid()) {
//       return;
//     }

//     this.submitted = false;
//     this.errorMessage = null;
//     if (this.currentStep < this.maxStep) {
//       this.currentStep++;
//       window.scrollTo({ top: 0, behavior: 'smooth' });
//     }
//   }

//   prevStep(): void {
//     this.errorMessage = null;
//     if (this.currentStep > 1) {
//       this.currentStep--;
//       window.scrollTo({ top: 0, behavior: 'smooth' });
//     }
//   }

//   goToLogin(): void {
//     this.router.navigate(['/login']);
//   }

//   // ============================================================
//   // SUBMIT
//   // POST /api/supplier/register
//   // ============================================================
//   async onSubmit(): Promise<void> {
//     this.submitted = true;
//     this.errorMessage = null;

//     if (!this.isStep3Valid()) {
//       ['firstName', 'lastName', 'designation', 'phone', 'email', 'password', 'confirmPassword']
//         .forEach(f => this.registerForm.get(f)?.markAsTouched());
//       if (this.registerForm.errors?.['passwordMismatch']) {
//         this.errorMessage = 'Passwords do not match. Please check and try again.';
//       }
//       return;
//     }

//     const v = this.registerForm.getRawValue();

//     // Resolve "Others" values
//     const companyType     = v.companyType     === 'Others' ? (v.otherCompanyType     || v.companyType)     : v.companyType;
//     const industrySector  = v.industrySector  === 'Others' ? (v.otherIndustrySector  || v.industrySector)  : v.industrySector;

//     const payload: any = {
//       companyName:              v.companyName?.trim(),
//       companyType,
//       industrySector,
//       website:                  v.website?.trim() || null,
//       gstNumber:                v.gstNumber?.trim() || null,
//       panNumber:                v.panNumber?.trim() || null,
//       tanNumber:                v.tanNumber?.trim() || null,
//       contactPersonName:        v.contactPersonName?.trim(),
//       contactPersonDesignation: v.contactPersonDesignation?.trim(),
//       contactPersonEmail:       v.contactPersonEmail?.trim().toLowerCase(),
//       contactPersonPhone:       v.contactPersonPhone,
//       addressLine1:             v.addressLine1?.trim(),
//       addressLine2:             v.addressLine2?.trim() || null,
//       city:                     v.city?.trim(),
//       state:                    v.state?.trim(),
//       postalCode:               v.postalCode?.trim(),
//       country:                  v.country,
//       organizationCompanyName:  v.organizationCompanyName,
//       firstName:                v.firstName?.trim(),
//       lastName:                 v.lastName?.trim(),
//       designation:              v.designation?.trim(),
//       phone:                    v.phone,
//       email:                    v.email?.trim().toLowerCase(),
//       password:                 v.password
//     };

//     // Attach logo if selected
//     if (this.selectedLogoFile) {
//       try {
//         const logoBase64 = await this.convertFileToBase64(this.selectedLogoFile);
//         payload.logoBase64       = logoBase64;
//         payload.logoFilename     = this.selectedLogoFile.name;
//         payload.logoContentType  = this.selectedLogoFile.type;
//       } catch (err) {
//         this.errorMessage = 'Failed to process logo. Please try again.';
//         return;
//       }
//     }

//     console.log('📤 Submitting supplier registration:', { ...payload, password: '***', logoBase64: payload.logoBase64 ? '<<base64>>' : undefined });

//     this.isSubmitting = true;

//     const url = `${environment.API_URL}leadcapture/api/supplier/register`;
//     this.http.post<any>(url, payload).subscribe({
//       next: (res) => {
//         this.isSubmitting = false;
//         console.log('✅ Registration response:', res);

//         if (res?.success) {
//           this.successMessage =
//             'Registration successful! Your account is pending approval. ' +
//             'You will be able to log in once an administrator approves your company.';
//           this.errorMessage = null;
//           setTimeout(() => this.router.navigate(['/login']), 5000);
//         } else {
//           this.errorMessage = res?.message || 'Registration failed. Please try again.';
//         }
//       },
//       error: (err) => {
//         this.isSubmitting = false;
//         console.error('❌ Registration error:', err);
//         this.errorMessage =
//           err?.error?.message || err?.message || 'Registration failed. Please try again.';
//       }
//     });
//   }
// }

import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, AbstractControl, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import {
  ContainerComponent,
  RowComponent,
  ColComponent,
  CardComponent,
  CardBodyComponent,
  ButtonDirective
} from '@coreui/angular';
import { environment } from '../../../environments/environment';
import {
  getAllCountryNames,
  getStatesForCountry,
  getCityNamesForState,
  getPostalCodeForCity
} from '../../../shared/data/country-state.data';

@Component({
  selector: 'app-supplier-register',
  templateUrl: './supplier-register.component.html',
  styleUrls: ['./supplier-register.component.css'],
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    HttpClientModule,
    RouterLink,
    ContainerComponent,
    RowComponent,
    ColComponent,
    CardComponent,
    CardBodyComponent,
    ButtonDirective
  ]
})
export class SupplierRegisterComponent implements OnInit {

  registerForm!: FormGroup;
  currentStep = 1;
  maxStep = 3;
  isSubmitting = false;
  submitted = false;
  errorMessage: string | null = null;
  successMessage: string | null = null;
  organizations: string[] = [];
  isLoadingOrgs = false;

  // ── Logo ──────────────────────────────────────────────────────
  selectedLogoFile: File | null = null;
  logoPreview: string | null = null;
  maxLogoSize = 5 * 1024 * 1024;
  allowedLogoTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif', 'image/svg+xml'];

  // ── Document uploads (GST / PAN / TAN) ───────────────────────
  gstDocFile: File | null = null;
  panDocFile: File | null = null;
  tanDocFile: File | null = null;
  allowedDocTypes = ['application/pdf', 'image/jpeg', 'image/jpg', 'image/png'];
  maxDocSize = 5 * 1024 * 1024; // 5 MB

  // ── Country / State / City cascade ───────────────────────────
  allCountries: string[] = [];
  availableStates: string[] = [];
  availableCities: string[] = [];
  cityIsOthers = false;

  // ── Company type → Sector mapping ────────────────────────────
  /** Master list of all sectors (displayed when no type selected) */
  readonly allIndustrySectors: string[] = [
    'IT', 'Software', 'Logistics', 'Electrical', 'Construction',
    'Healthcare', 'Automotive', 'Textile', 'Food & Beverage', 'Mechanical',
    'Chemical', 'Pharmaceuticals', 'FMCG', 'Retail', 'Media & Entertainment',
    'Banking & Finance', 'Consulting', 'Real Estate', 'Agriculture', 'Others'
  ];

  /** Filtered sectors shown based on selected company type */
  filteredIndustrySectors: string[] = [];

  /**
   * Mapping: Company Type → relevant Industry Sectors
   * Each bucket ends with 'Others' so the user can always specify something custom.
   */
  private readonly sectorMap: Record<string, string[]> = {
    'Manufacturing': [
      'Automotive', 'Chemical', 'Mechanical', 'Electrical',
      'Food & Beverage', 'Pharmaceuticals', 'Textile', 'Others'
    ],
    'Trading': [
      'FMCG', 'Retail', 'Food & Beverage', 'Electrical', 'Chemical', 'Others'
    ],
    'Services': [
      'Healthcare', 'Consulting', 'Banking & Finance',
      'Media & Entertainment', 'Real Estate', 'Others'
    ],
    'Distribution': [
      'Logistics', 'FMCG', 'Pharmaceuticals', 'Food & Beverage', 'Others'
    ],
    'Retail': [
      'FMCG', 'Retail', 'Food & Beverage', 'Textile', 'Others'
    ],
    'IT / Software': [
      'IT', 'Software', 'Consulting', 'Media & Entertainment', 'Others'
    ],
    'Construction': [
      'Construction', 'Electrical', 'Mechanical', 'Real Estate', 'Others'
    ],
    'Healthcare': [
      'Healthcare', 'Pharmaceuticals', 'Chemical', 'Others'
    ],
    'Logistics': [
      'Logistics', 'Automotive', 'Others'
    ],
    'Others': this.allIndustrySectors   // show everything for "Others"
  };

  companyTypes = [
    'Manufacturing', 'Trading', 'Services', 'Distribution', 'Retail',
    'IT / Software', 'Construction', 'Healthcare', 'Logistics', 'Others'
  ];

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private http: HttpClient
  ) {}

  ngOnInit(): void {
    this.allCountries = getAllCountryNames();
    this.filteredIndustrySectors = [...this.allIndustrySectors];
    this.initForm();
    this.loadOrganizations();
  }

  // ============================================================
  // FORM INIT
  // ============================================================
  private initForm(): void {
    this.registerForm = this.fb.group(
      {
        // ── Step 1 — Company Info ──────────────────────────────
        companyName:             ['', [Validators.required, Validators.minLength(2)]],
        companyType:             ['', Validators.required],
        otherCompanyType:        [''],
        industrySector:          ['', Validators.required],
        otherIndustrySector:     [''],
        website:                 [''],

        // Primary contact
        contactPersonName:        ['', [Validators.required, Validators.minLength(2)]],
        contactPersonDesignation: ['', Validators.required],
        contactPersonEmail:       ['', [Validators.required, Validators.email]],
        contactPersonPhone:       ['', [Validators.required, Validators.pattern(/^[0-9]{10}$/)]],

        // Address — cascading dropdowns
        addressLine1:            ['', [Validators.required, Validators.minLength(5)]],
        addressLine2:            [''],
        country:                 ['India', Validators.required],
        state:                   ['', Validators.required],
        city:                    ['', Validators.required],
        otherCity:               [''],           // shown when city === '__others__'
        postalCode:              ['', [Validators.required, Validators.pattern(/^[A-Za-z0-9\s\-]{3,10}$/)]],

        // Organization link
        organizationCompanyName: ['', Validators.required],

        // ── Step 2 — Logo & Registration ─────────────────────
        logo:                    [null],
        gstNumber:               [''],
        panNumber:               [''],
        tanNumber:               [''],

        // ── Step 3 — Account Details ──────────────────────────
        firstName:               ['', [Validators.required, Validators.minLength(2)]],
        lastName:                ['', [Validators.required, Validators.minLength(2)]],
        designation:             ['', Validators.required],
        phone:                   ['', [Validators.required, Validators.pattern(/^[0-9]{10}$/)]],
        email:                   ['', [Validators.required, Validators.email]],
        password:                ['', [Validators.required, Validators.minLength(6)]],
        confirmPassword:         ['', Validators.required]
      },
      { validators: this.passwordMatchValidator }
    );

    // Pre-load states for default country "India"
    this.availableStates = getStatesForCountry('India');
  }

  private passwordMatchValidator(group: AbstractControl) {
    const pw  = group.get('password')?.value;
    const cpw = group.get('confirmPassword')?.value;
    return pw === cpw ? null : { passwordMismatch: true };
  }

  get f() { return this.registerForm.controls; }

  // ============================================================
  // COMPANY TYPE → INDUSTRY SECTOR FILTER
  // ============================================================
  onCompanyTypeChange(event: any): void {
    const selected = event.target.value;

    // Update "otherCompanyType" validators
    const ctrl = this.registerForm.get('otherCompanyType');
    if (selected === 'Others') {
      ctrl?.setValidators([Validators.required, Validators.minLength(2)]);
      ctrl?.setValue('');
    } else {
      ctrl?.clearValidators();
      ctrl?.setValue(null);
    }
    ctrl?.updateValueAndValidity();

    // Reset sector selection and apply filter
    this.registerForm.get('industrySector')?.setValue('');
    this.registerForm.get('otherIndustrySector')?.clearValidators();
    this.registerForm.get('otherIndustrySector')?.setValue(null);
    this.registerForm.get('otherIndustrySector')?.updateValueAndValidity();

    if (selected && this.sectorMap[selected]) {
      this.filteredIndustrySectors = this.sectorMap[selected];
    } else {
      this.filteredIndustrySectors = [...this.allIndustrySectors];
    }
  }

  onIndustrySectorChange(event: any): void {
    const ctrl = this.registerForm.get('otherIndustrySector');
    if (event.target.value === 'Others') {
      ctrl?.setValidators([Validators.required, Validators.minLength(2)]);
      ctrl?.setValue('');
    } else {
      ctrl?.clearValidators();
      ctrl?.setValue(null);
    }
    ctrl?.updateValueAndValidity();
  }

  // ============================================================
  // COUNTRY → STATE → CITY → POSTAL CODE CASCADE
  // ============================================================
  onCountryChange(event: any): void {
    const country = event.target.value;
    this.availableStates = country ? getStatesForCountry(country) : [];
    this.availableCities = [];
    this.cityIsOthers = false;

    this.registerForm.get('otherCity')?.clearValidators();
    this.registerForm.get('otherCity')?.setValue('');
    this.registerForm.get('otherCity')?.updateValueAndValidity();
    this.registerForm.patchValue({ state: '', city: '', postalCode: '' });
  }

  onStateChange(event: any): void {
    const country = this.registerForm.get('country')?.value;
    const state = event.target.value;
    this.availableCities = state ? getCityNamesForState(country, state) : [];
    this.cityIsOthers = false;

    this.registerForm.get('otherCity')?.clearValidators();
    this.registerForm.get('otherCity')?.setValue('');
    this.registerForm.get('otherCity')?.updateValueAndValidity();
    this.registerForm.patchValue({ city: '', postalCode: '' });
  }

  onCityChange(event: any): void {
    const selected = event.target.value;
    if (selected === '__others__') {
      this.cityIsOthers = true;
      this.registerForm.patchValue({ city: '__others__', otherCity: '', postalCode: '' });
      this.registerForm.get('otherCity')?.setValidators([Validators.required, Validators.minLength(2)]);
      this.registerForm.get('otherCity')?.updateValueAndValidity();
    } else {
      this.cityIsOthers = false;
      this.registerForm.get('otherCity')?.clearValidators();
      this.registerForm.get('otherCity')?.setValue('');
      this.registerForm.get('otherCity')?.updateValueAndValidity();

      const country = this.registerForm.get('country')?.value;
      const state   = this.registerForm.get('state')?.value;
      const postal  = getPostalCodeForCity(country, state, selected);
      this.registerForm.patchValue({ city: selected, postalCode: postal || '' });
    }
  }

  // ============================================================
  // LOGO HANDLING
  // ============================================================
  onLogoSelected(event: any): void {
    const file: File = event.target.files[0];
    if (!file) return;

    if (!this.allowedLogoTypes.includes(file.type)) {
      alert('Invalid file type. Please select a JPG, PNG, GIF or SVG image.');
      event.target.value = '';
      return;
    }
    if (file.size > this.maxLogoSize) {
      alert('File too large. Logo must be smaller than 5 MB.');
      event.target.value = '';
      return;
    }

    this.selectedLogoFile = file;
    const reader = new FileReader();
    reader.onload = (e: any) => { this.logoPreview = e.target.result; };
    reader.readAsDataURL(file);
  }

  removeLogo(): void {
    this.selectedLogoFile = null;
    this.logoPreview = null;
    this.registerForm.patchValue({ logo: null });
    const fileInput = document.getElementById('logoFileInput') as HTMLInputElement;
    if (fileInput) fileInput.value = '';
  }

  private convertFileToBase64(file: File): Promise<string> {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.onload = () => resolve(reader.result as string);
      reader.onerror = error => reject(error);
      reader.readAsDataURL(file);
    });
  }

  // ============================================================
  // DOCUMENT UPLOAD HANDLING (GST / PAN / TAN)
  // ============================================================
  onDocSelected(event: any, type: 'gst' | 'pan' | 'tan'): void {
    const file: File = event.target.files[0];
    if (!file) return;

    if (!this.allowedDocTypes.includes(file.type)) {
      alert('Invalid file type. Please select a PDF, JPG, or PNG file.');
      event.target.value = '';
      return;
    }
    if (file.size > this.maxDocSize) {
      alert('File too large. Document must be smaller than 5 MB.');
      event.target.value = '';
      return;
    }

    if (type === 'gst') this.gstDocFile = file;
    if (type === 'pan') this.panDocFile = file;
    if (type === 'tan') this.tanDocFile = file;
  }

  removeDoc(type: 'gst' | 'pan' | 'tan'): void {
    if (type === 'gst') {
      this.gstDocFile = null;
      (document.getElementById('gstDocInput') as HTMLInputElement).value = '';
    }
    if (type === 'pan') {
      this.panDocFile = null;
      (document.getElementById('panDocInput') as HTMLInputElement).value = '';
    }
    if (type === 'tan') {
      this.tanDocFile = null;
      (document.getElementById('tanDocInput') as HTMLInputElement).value = '';
    }
  }

  onGstInput(event: any): void {
    // Auto-uppercase GST
    event.target.value = event.target.value.toUpperCase();
    this.registerForm.get('gstNumber')?.setValue(event.target.value, { emitEvent: false });
  }

  /**
   * Upload a single document to the backend.
   * Endpoint: POST /api/supplier/documents/upload
   * Multipart: file + type (gst|pan|tan) + supplierId
   */
  private async uploadDocument(file: File, type: string, supplierId: number): Promise<void> {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('documentType', type);
    formData.append('supplierId', supplierId.toString());

    const url = `${environment.API_URL}leadcapture/api/supplier/documents/upload`;
    await this.http.post(url, formData).toPromise();
  }

  // ============================================================
  // LOAD ORGANIZATIONS
  // ============================================================
  loadOrganizations(): void {
    this.isLoadingOrgs = true;
    const url = `${environment.API_URL}leadcapture/api/supplier/register/organizations`;

    this.http.get<any>(url).subscribe({
      next: (res) => {
        this.isLoadingOrgs = false;
        this.organizations = res?.data && Array.isArray(res.data) ? res.data : [];
      },
      error: () => {
        this.isLoadingOrgs = false;
        this.organizations = [];
      }
    });
  }

  // ============================================================
  // STEP VALIDATION
  // ============================================================
  isStep1Valid(): boolean {
    const step1Fields = [
      'companyName', 'companyType', 'industrySector',
      'contactPersonName', 'contactPersonDesignation',
      'contactPersonEmail', 'contactPersonPhone',
      'addressLine1', 'country', 'state', 'city', 'postalCode',
      'organizationCompanyName'
    ];

    const standardValid = step1Fields.every(field => this.registerForm.get(field)?.valid);

    const companyOtherValid =
      this.registerForm.get('companyType')?.value === 'Others'
        ? (this.registerForm.get('otherCompanyType')?.valid ?? false) : true;

    const industryOtherValid =
      this.registerForm.get('industrySector')?.value === 'Others'
        ? (this.registerForm.get('otherIndustrySector')?.valid ?? false) : true;

    const otherCityValid = this.cityIsOthers
      ? (this.registerForm.get('otherCity')?.valid ?? false) : true;

    return standardValid && companyOtherValid && industryOtherValid && otherCityValid;
  }

  isStep2Valid(): boolean {
    // Step 2 is fully optional — always valid
    return true;
  }

  isStep3Valid(): boolean {
    const step3Fields = ['firstName', 'lastName', 'designation', 'phone', 'email', 'password', 'confirmPassword'];
    const fieldsValid = step3Fields.every(field => this.registerForm.get(field)?.valid);
    const noMismatch = !this.registerForm.errors?.['passwordMismatch'];
    return fieldsValid && noMismatch;
  }

  // ============================================================
  // NAVIGATION
  // ============================================================
  nextStep(): void {
    this.submitted = true;

    if (this.currentStep === 1 && !this.isStep1Valid()) {
      [
        'companyName', 'companyType', 'otherCompanyType', 'industrySector', 'otherIndustrySector',
        'contactPersonName', 'contactPersonDesignation', 'contactPersonEmail', 'contactPersonPhone',
        'addressLine1', 'country', 'state', 'city', 'postalCode', 'organizationCompanyName'
      ].forEach(f => this.registerForm.get(f)?.markAsTouched());
      return;
    }

    this.submitted = false;
    this.errorMessage = null;
    if (this.currentStep < this.maxStep) {
      this.currentStep++;
      window.scrollTo({ top: 0, behavior: 'smooth' });
    }
  }

  prevStep(): void {
    this.errorMessage = null;
    if (this.currentStep > 1) {
      this.currentStep--;
      window.scrollTo({ top: 0, behavior: 'smooth' });
    }
  }

  goToLogin(): void {
    this.router.navigate(['/login']);
  }

  // ============================================================
  // SUBMIT
  // ============================================================
  async onSubmit(): Promise<void> {
    this.submitted = true;
    this.errorMessage = null;

    if (!this.isStep3Valid()) {
      ['firstName', 'lastName', 'designation', 'phone', 'email', 'password', 'confirmPassword']
        .forEach(f => this.registerForm.get(f)?.markAsTouched());
      if (this.registerForm.errors?.['passwordMismatch']) {
        this.errorMessage = 'Passwords do not match. Please check and try again.';
      }
      return;
    }

    const v = this.registerForm.getRawValue();

    // Resolve "Others" values
    const companyType    = v.companyType    === 'Others' ? (v.otherCompanyType    || v.companyType)    : v.companyType;
    const industrySector = v.industrySector === 'Others' ? (v.otherIndustrySector || v.industrySector) : v.industrySector;

    // Resolve city: if "Others" chosen, use the typed value
    const resolvedCity = (v.city === '__others__' && v.otherCity) ? v.otherCity.trim() : v.city;

    const payload: any = {
      companyName:              v.companyName?.trim(),
      companyType,
      industrySector,
      website:                  v.website?.trim() || null,
      gstNumber:                v.gstNumber?.trim() || null,
      panNumber:                v.panNumber?.trim().toUpperCase() || null,
      tanNumber:                v.tanNumber?.trim() || null,
      contactPersonName:        v.contactPersonName?.trim(),
      contactPersonDesignation: v.contactPersonDesignation?.trim(),
      contactPersonEmail:       v.contactPersonEmail?.trim().toLowerCase(),
      contactPersonPhone:       v.contactPersonPhone,
      addressLine1:             v.addressLine1?.trim(),
      addressLine2:             v.addressLine2?.trim() || null,
      country:                  v.country,
      state:                    v.state,
      city:                     resolvedCity,
      postalCode:               v.postalCode?.trim(),
      organizationCompanyName:  v.organizationCompanyName,
      firstName:                v.firstName?.trim(),
      lastName:                 v.lastName?.trim(),
      designation:              v.designation?.trim(),
      phone:                    v.phone,
      email:                    v.email?.trim().toLowerCase(),
      password:                 v.password
    };

    // Attach logo if selected
    if (this.selectedLogoFile) {
      try {
        payload.logoBase64      = await this.convertFileToBase64(this.selectedLogoFile);
        payload.logoFilename    = this.selectedLogoFile.name;
        payload.logoContentType = this.selectedLogoFile.type;
      } catch {
        this.errorMessage = 'Failed to process logo. Please try again.';
        return;
      }
    }

    this.isSubmitting = true;

    const url = `${environment.API_URL}leadcapture/api/supplier/register`;
    this.http.post<any>(url, payload).subscribe({
      next: async (res) => {
        if (res?.success) {
          const supplierId: number = res.supplierId;

          // Upload documents if any were selected
          const docUploads: Promise<void>[] = [];
          if (this.gstDocFile && supplierId) {
            docUploads.push(this.uploadDocument(this.gstDocFile, 'gst', supplierId));
          }
          if (this.panDocFile && supplierId) {
            docUploads.push(this.uploadDocument(this.panDocFile, 'pan', supplierId));
          }
          if (this.tanDocFile && supplierId) {
            docUploads.push(this.uploadDocument(this.tanDocFile, 'tan', supplierId));
          }

          if (docUploads.length > 0) {
            try {
              await Promise.all(docUploads);
            } catch (docErr) {
              console.warn('Document upload partially failed — registration still succeeded.', docErr);
            }
          }

          this.isSubmitting = false;
          this.successMessage =
            'Registration successful! Your account is pending approval. ' +
            'You will be able to log in once an administrator approves your company.';
          this.errorMessage = null;
          setTimeout(() => this.router.navigate(['/login']), 5000);
        } else {
          this.isSubmitting = false;
          this.errorMessage = res?.message || 'Registration failed. Please try again.';
        }
      },
      error: (err) => {
        this.isSubmitting = false;
        this.errorMessage =
          err?.error?.message || err?.message || 'Registration failed. Please try again.';
      }
    });
  }
}