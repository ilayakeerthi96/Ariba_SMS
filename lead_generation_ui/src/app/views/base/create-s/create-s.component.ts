
// import { Component, OnInit } from '@angular/core';
// import { FormBuilder, FormGroup, Validators, FormArray } from '@angular/forms';
// import { Router, ActivatedRoute } from '@angular/router';
// import { CommonModule } from '@angular/common';
// import { ReactiveFormsModule } from '@angular/forms';
// import { SupplierService } from '../dashboard/supplier.service';
// import { MessageService } from '../../../shared/service/message.service';
// import { Supplier, SupplierLocation, SupplierDepartment, SupplierUser } from '../dashboard/supplier.model';

// // ✅ Import from NEW unified data file (same one used by buyer)
// import {
//   getAllCountryNames,
//   getStatesForCountry,
//   getCityNamesForState,
//   getPostalCodeForCity
// } from '../../../shared/data/country-state.data';

// @Component({
//   selector: 'app-create-s',
//   templateUrl: './create-s.component.html',
//   styleUrls: ['./create-s.component.css'],
//   standalone: true,
//   imports: [CommonModule, ReactiveFormsModule]
// })
// export class CreateSComponent implements OnInit {

//   supplierForm!: FormGroup;
//   currentStep = 1;
//   maxStep = 4;
//   isSubmitting = false;

//   mode: 'create' | 'edit' = 'create';
//   supplierId: number | null = null;

//   selectedLogoFile: File | null = null;
//   logoPreview: string | null = null;
//   maxLogoSize = 5 * 1024 * 1024;
//   allowedLogoTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif', 'image/svg+xml'];

//   email: string = '';
//   companyName: string = '';
//   fullName: string = '';

//   // ===================== CASCADE STATE =====================

//   allCountries: string[] = getAllCountryNames();

//   // HQ address
//   hqStates: string[] = [];
//   hqCities: string[] = [];

//   // Per-location (indexed by location index)
//   locationStates: string[][] = [];
//   locationCities: string[][] = [];

//   // ===================== DROPDOWN OPTIONS =====================

//   companyTypes = ['Manufacturing', 'Trading', 'Services', 'Distribution', 'Retail', 'Others'];
//   locationTypes = ['Head Office', 'Branch', 'Warehouse', 'Service Center', 'Others'];
//   industrySectors = ['IT', 'Logistics', 'Electrical', 'Construction', 'Healthcare',
//                      'Automotive', 'Textile', 'Food & Beverage', 'Others'];
//   categoryOfProducts = ['IT Equipment', 'Industrial Equipment', 'Software Services',
//                         'Hardware Supplies', 'Others'];

//   constructor(
//     private fb: FormBuilder,
//     private supplierService: SupplierService,
//     private messageService: MessageService,
//     private router: Router,
//     private route: ActivatedRoute
//   ) {}

//   ngOnInit(): void {
//     this.initializeForm();
//     this.loadUserHeaderInfo();
//     this.checkEditMode();
//     // Load HQ states for default country India
//     this.hqStates = getStatesForCountry('India');
//   }

//   private loadUserHeaderInfo(): void {
//     this.email = localStorage.getItem('email') || 'admin@gmail.com';
//     this.companyName = localStorage.getItem('companyName') || 'ITI Pvt Ltd Bangalore';
//     this.fullName = localStorage.getItem('fullName') || 'Admin';
//   }

//   // ===================== FORM INITIALIZATION =====================

//   private initializeForm(): void {
//     this.supplierForm = this.fb.group({
//       companyName: ['', [Validators.required, Validators.minLength(2)]],
//       companyType: ['', Validators.required],
//       otherCompanyType: [''],
//       industrySector: ['', Validators.required],
//       otherIndustrySector: [''],
//       logo: [null],
//       contactPersonName: ['', [Validators.required, Validators.minLength(2)]],
//       contactPersonDesignation: ['', Validators.required],
//       contactPersonEmail: ['', [Validators.required, Validators.email]],
//       contactPersonPhone: ['', [Validators.required, Validators.pattern(/^[0-9]{10}$/)]],
//       addressLine1: ['', [Validators.required, Validators.minLength(5)]],
//       addressLine2: [''],
//       city: ['', Validators.required],
//       state: ['', Validators.required],
//       postalCode: ['', [Validators.required, Validators.pattern(/^[A-Za-z0-9\s\-]{3,10}$/)]],
//       country: ['India', Validators.required],
//     gstNumber: ['', Validators.required],
// panNumber: ['', [Validators.required, Validators.pattern(/^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}$/)]],
//       tanNumber: [''],
//       website: [''],
//       locations: this.fb.array([], Validators.minLength(1)),
//       departments: this.fb.array([]),
//       users: this.fb.array([])
//     });

//     if (!this.supplierForm.get('locations')?.value?.length) {
//       this.addLocation();
//     }
//   }

//   // ===================== HQ COUNTRY → STATE → CITY → POSTAL =====================

//   onHQCountryChange(event: any): void {
//     const country = event.target.value;
//     this.hqStates = getStatesForCountry(country);
//     this.hqCities = [];
//     this.supplierForm.patchValue({ state: '', city: '', postalCode: '' });
//   }

//   onHQStateChange(event: any): void {
//     const country = this.supplierForm.get('country')?.value;
//     const state = event.target.value;
//     this.hqCities = getCityNamesForState(country, state);
//     this.supplierForm.patchValue({ city: '', postalCode: '' });
//   }

//   onHQCityChange(event: any): void {
//     const country = this.supplierForm.get('country')?.value;
//     const state = this.supplierForm.get('state')?.value;
//     const city = event.target.value;
//     const postal = getPostalCodeForCity(country, state, city);
//     if (postal) {
//       this.supplierForm.patchValue({ postalCode: postal });
//     }
//   }

//   // ===================== LOCATION COUNTRY → STATE → CITY → POSTAL =====================

//   onLocationCountryChange(event: any, idx: number): void {
//     const country = event.target.value;
//     this.locationStates[idx] = getStatesForCountry(country);
//     this.locationCities[idx] = [];
//     this.locations.at(idx).patchValue({ state: '', city: '', postalCode: '' });
//   }

//   onLocationStateChange(event: any, idx: number): void {
//     const country = this.locations.at(idx).get('country')?.value;
//     const state = event.target.value;
//     this.locationCities[idx] = getCityNamesForState(country, state);
//     this.locations.at(idx).patchValue({ city: '', postalCode: '' });
//   }

//   onLocationCityChange(event: any, idx: number): void {
//     const loc = this.locations.at(idx);
//     const country = loc.get('country')?.value;
//     const state = loc.get('state')?.value;
//     const city = event.target.value;
//     const postal = getPostalCodeForCity(country, state, city);
//     if (postal) {
//       loc.patchValue({ postalCode: postal });
//     }
//   }

//   getStatesForLocation(idx: number): string[] {
//     return this.locationStates[idx] || [];
//   }

//   getCitiesForLocation(idx: number): string[] {
//     return this.locationCities[idx] || [];
//   }

//   // ===================== LOGO =====================

//   onLogoSelected(event: any): void {
//     const file = event.target.files[0];
//     if (!file) return;
//     if (!this.allowedLogoTypes.includes(file.type)) {
//       this.messageService.showMessage('error', 'Invalid File Type',
//         'Please select a valid image file (JPG, PNG, GIF, SVG)');
//       event.target.value = '';
//       return;
//     }
//     if (file.size > this.maxLogoSize) {
//       this.messageService.showMessage('error', 'File Too Large', 'Logo must be smaller than 5MB');
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
//     this.supplierForm.patchValue({ logo: null });
//     const fileInput = document.querySelector('input[type="file"]') as HTMLInputElement;
//     if (fileInput) fileInput.value = '';
//   }

//   private checkEditMode(): void {
//     this.route.paramMap.subscribe(params => {
//       const id = params.get('id');
//       if (id) {
//         this.mode = 'edit';
//         this.supplierId = +id;
//         this.currentStep = 1;
//         this.loadSupplierData(this.supplierId);
//       } else {
//         this.mode = 'create';
//         this.currentStep = 1;
//       }
//     });
//   }

//   // ===================== FORM ARRAYS =====================

//   get locations(): FormArray { return this.supplierForm.get('locations') as FormArray; }
//   get departments(): FormArray { return this.supplierForm.get('departments') as FormArray; }
//   get users(): FormArray { return this.supplierForm.get('users') as FormArray; }

//   // ===================== LOCATIONS =====================

//   addLocation(): void {
//     const locationGroup = this.fb.group({
//       id: [null],
//       locationName: ['', [Validators.required, Validators.minLength(2)]],
//       locationType: ['', Validators.required],
//       otherLocationType: [''],
//       locationContactName: ['', [Validators.required, Validators.minLength(2)]],
//       locationContactEmail: ['', [Validators.required, Validators.email]],
//       locationContactPhone: ['', [Validators.required, Validators.pattern(/^[0-9]{10}$/)]],
//       addressLine1: ['', [Validators.required, Validators.minLength(5)]],
//       addressLine2: [''],
//       city: ['', Validators.required],
//       state: ['', Validators.required],
//       postalCode: ['', [Validators.required, Validators.pattern(/^[A-Za-z0-9\s\-]{3,10}$/)]],
//       country: ['India', Validators.required],
//       landlineNumber: [''],
//       faxNumber: ['']
//     });

//     const newIndex = this.locations.length;
//     this.locations.push(locationGroup);
//     this.locationStates[newIndex] = getStatesForCountry('India');
//     this.locationCities[newIndex] = [];
//   }

//   removeLocation(index: number): void {
//     if (this.locations.length > 1) {
//       this.locations.removeAt(index);
//       this.locationStates.splice(index, 1);
//       this.locationCities.splice(index, 1);
//     } else {
//       this.messageService.showMessage('warning', 'Warning', 'At least one location is required');
//     }
//   }

//   // ===================== DEPARTMENTS =====================

//   addDepartment(): void {
//     const departmentGroup = this.fb.group({
//       id: [null],
//       locationIndex: ['', Validators.required],
//       departmentName: ['', [Validators.required, Validators.minLength(2)]],
//       departmentDescription: [''],
//       categoryOfProducts: [''],
//       otherCategoryType: ['']
//     });
//     this.departments.push(departmentGroup);
//   }

//   removeDepartment(deptIndex: number): void {
//     if (this.departments.length > 1) {
//       this.departments.removeAt(deptIndex);
//     } else {
//       this.messageService.showMessage('warning', 'Warning', 'At least one category is required');
//     }
//   }

//   // ===================== USERS =====================

//   addUser(userData?: Partial<SupplierUser>): void {
//     const userGroup = this.fb.group({
//       id: [userData?.id || null],
//       departmentIndex: [userData ? (userData as any).departmentIndex : '', Validators.required],
//       firstName: [userData?.firstName || '', [Validators.required, Validators.minLength(2)]],
//       lastName: [userData?.lastName || '', [Validators.required, Validators.minLength(2)]],
//       email: [userData?.email || '', [Validators.required, Validators.email]],
//       phone: [userData?.phone || '', [Validators.required, Validators.pattern(/^[0-9]{10}$/)]],
//       designation: [userData?.designation || '', Validators.required],
//       employeeId: [userData?.employeeId || '', Validators.required],
//       gender: [userData?.gender || ''],
//       dateOfBirth: [userData?.dateOfBirth || ''],
//       addressLine1: [userData?.addressLine1 || ''],
//       addressLine2: [userData?.addressLine2 || ''],
//       city: [userData?.city || ''],
//       state: [userData?.state || ''],
//       postalCode: [userData?.postalCode || ''],
//       password: [userData?.password || '', [Validators.required, Validators.minLength(6)]]
//     });
//     this.users.push(userGroup);
//   }

//   removeUser(userIndex: number): void {
//     if (this.users.length > 1) {
//       this.users.removeAt(userIndex);
//     } else {
//       this.messageService.showMessage('warning', 'Warning', 'At least one contact is required');
//     }
//   }

//   getLocationNameForDepartment(deptIndex: number): string {
//     const dept = this.departments.at(deptIndex);
//     const locIndex = dept?.get('locationIndex')?.value;
//     if (locIndex !== '' && locIndex !== null && locIndex !== undefined) {
//       const location = this.locations.at(locIndex);
//       return location?.get('locationName')?.value || `Location ${locIndex + 1}`;
//     }
//     return '';
//   }

//   // ===================== OTHERS HANDLING =====================

//   onCompanyTypeChange(event: any): void {
//     const customTypeControl = this.supplierForm.get('otherCompanyType');
//     if (event.target.value === 'Others') {
//       customTypeControl?.setValidators([Validators.required, Validators.minLength(2)]);
//       customTypeControl?.setValue('');
//     } else {
//       customTypeControl?.clearValidators();
//       customTypeControl?.setValue(null);
//     }
//     customTypeControl?.updateValueAndValidity();
//   }

//   onIndustrySectorChange(event: any): void {
//     const customTypeControl = this.supplierForm.get('otherIndustrySector');
//     if (event.target.value === 'Others') {
//       customTypeControl?.setValidators([Validators.required, Validators.minLength(2)]);
//       customTypeControl?.setValue('');
//     } else {
//       customTypeControl?.clearValidators();
//       customTypeControl?.setValue(null);
//     }
//     customTypeControl?.updateValueAndValidity();
//   }

//   onLocationTypeChange(event: any, locationIndex: number): void {
//     const locationGroup = this.locations.at(locationIndex) as FormGroup;
//     const customTypeControl = locationGroup.get('otherLocationType');
//     if (event.target.value === 'Others') {
//       customTypeControl?.setValidators([Validators.required, Validators.minLength(2)]);
//       customTypeControl?.setValue('');
//     } else {
//       customTypeControl?.clearValidators();
//       customTypeControl?.setValue(null);
//     }
//     customTypeControl?.updateValueAndValidity();
//   }

//   onCategoryTypeChange(event: any, deptIndex: number): void {
//     const departmentGroup = this.departments.at(deptIndex) as FormGroup;
//     const customTypeControl = departmentGroup.get('otherCategoryType');
//     if (event.target.value === 'Others') {
//       customTypeControl?.setValidators([Validators.required, Validators.minLength(2)]);
//       customTypeControl?.setValue('');
//     } else {
//       customTypeControl?.clearValidators();
//       customTypeControl?.setValue(null);
//     }
//     customTypeControl?.updateValueAndValidity();
//   }

//   // ===================== LOAD DATA (EDIT MODE) =====================

//   private loadSupplierData(id: number): void {
//     this.supplierService.getSupplierById(id).subscribe({
//       next: (supplier: any) => {
//         let companyTypeForForm = supplier.companyType;
//         let otherCompanyType = null;
//         let industrySectorForForm = supplier.industrySector;
//         let otherIndustrySector = null;

//         if (supplier.companyType && !this.companyTypes.includes(supplier.companyType)) {
//           companyTypeForForm = 'Others';
//           otherCompanyType = supplier.companyType;
//         }
//         if (supplier.industrySector && !this.industrySectors.includes(supplier.industrySector)) {
//           industrySectorForForm = 'Others';
//           otherIndustrySector = supplier.industrySector;
//         }

//         this.supplierForm.patchValue({
//           companyName: supplier.companyName,
//           companyType: companyTypeForForm,
//           otherCompanyType: otherCompanyType,
//           industrySector: industrySectorForForm,
//           otherIndustrySector: otherIndustrySector,
//           contactPersonName: supplier.contactPersonName,
//           contactPersonDesignation: supplier.contactPersonDesignation,
//           contactPersonEmail: supplier.contactPersonEmail,
//           contactPersonPhone: supplier.contactPersonPhone,
//           addressLine1: supplier.addressLine1,
//           addressLine2: supplier.addressLine2,
//           state: supplier.state,
//           postalCode: supplier.postalCode,
//           country: supplier.country,
//           gstNumber: supplier.gstNumber,
//           panNumber: supplier.panNumber,
//           tanNumber: supplier.tanNumber,
//           website: supplier.website
//         });

//         // ✅ Restore HQ state/city cascades
//         if (supplier.country) {
//           this.hqStates = getStatesForCountry(supplier.country);
//         }
//         if (supplier.country && supplier.state) {
//           this.hqCities = getCityNamesForState(supplier.country, supplier.state);
//         }
//         // Set city after cascades loaded
//         this.supplierForm.patchValue({ city: supplier.city });

//         this.onCompanyTypeChange({ target: { value: companyTypeForForm } });
//         this.onIndustrySectorChange({ target: { value: industrySectorForForm } });

//         if (supplier.id) {
//           this.supplierService.getSupplierLogoBase64(supplier.id).subscribe({
//             next: (logoData: any) => { if (logoData) this.logoPreview = logoData; },
//             error: () => {}
//           });
//         }

//         while (this.locations.length) this.locations.removeAt(0);
//         while (this.departments.length) this.departments.removeAt(0);
//         while (this.users.length) this.users.removeAt(0);
//         this.locationStates = [];
//         this.locationCities = [];

//         if (supplier.locations && Array.isArray(supplier.locations)) {
//           this.populateLocations(supplier.locations);
//         }
//       },
//       error: (err: any) => {
//         this.messageService.showMessage('error', 'Error', 'Failed to load supplier data');
//         setTimeout(() => this.router.navigate(['/dashboard']), 2000);
//       }
//     });
//   }

//   // ===================== POPULATE LOCATIONS (EDIT MODE) =====================

//   private populateLocations(locations: SupplierLocation[]): void {
//     locations.forEach((loc: any, locIndex: number) => {
//       this.addLocation();

//       let locationTypeForForm = loc.locationType;
//       let otherLocationType = null;
//       if (loc.locationType && !this.locationTypes.includes(loc.locationType)) {
//         locationTypeForForm = 'Others';
//         otherLocationType = loc.locationType;
//       }

//       // ✅ Restore state/city cascades for this location
//       if (loc.country) {
//         this.locationStates[locIndex] = getStatesForCountry(loc.country);
//       }
//       if (loc.country && loc.state) {
//         this.locationCities[locIndex] = getCityNamesForState(loc.country, loc.state);
//       }

//       this.locations.at(locIndex).patchValue({
//         id: loc.id,
//         locationName: loc.locationName,
//         locationType: locationTypeForForm,
//         otherLocationType: otherLocationType,
//         locationContactName: loc.locationContactName,
//         locationContactEmail: loc.locationContactEmail,
//         locationContactPhone: loc.locationContactPhone,
//         addressLine1: loc.addressLine1,
//         addressLine2: loc.addressLine2,
//         city: loc.city,
//         state: loc.state,
//         postalCode: loc.postalCode,
//         country: loc.country,
//         landlineNumber: loc.landlineNumber,
//         faxNumber: loc.faxNumber
//       });

//       this.onLocationTypeChange({ target: { value: locationTypeForForm } }, locIndex);

//       if (loc.departments && loc.departments.length > 0) {
//         loc.departments.forEach((dept: any) => {
//           const deptIndex = this.departments.length;
//           this.addDepartment();

//           let categoryForForm = dept.categoryOfProducts;
//           let otherCategoryType = null;
//           if (dept.categoryOfProducts && !this.categoryOfProducts.includes(dept.categoryOfProducts)) {
//             categoryForForm = 'Others';
//             otherCategoryType = dept.categoryOfProducts;
//           }

//           this.departments.at(deptIndex).patchValue({
//             id: dept.id,
//             locationIndex: locIndex,
//             departmentName: dept.departmentName,
//             departmentDescription: dept.departmentDescription,
//             categoryOfProducts: categoryForForm,
//             otherCategoryType: otherCategoryType
//           });

//           this.onCategoryTypeChange({ target: { value: categoryForForm } }, deptIndex);

//           if (dept.users && dept.users.length > 0) {
//             dept.users.forEach((user: any) => {
//               this.addUser({
//                 id: user.id,
//                 departmentIndex: deptIndex,
//                 firstName: user.firstName,
//                 lastName: user.lastName,
//                 email: user.email,
//                 phone: user.phone,
//                 designation: user.designation,
//                 employeeId: user.employeeId,
//                 gender: user.gender,
//                 dateOfBirth: user.dateOfBirth,
//                 addressLine1: user.addressLine1,
//                 addressLine2: user.addressLine2,
//                 city: user.city,
//                 state: user.state,
//                 postalCode: user.postalCode,
//                 password: ''
//               });
//             });
//           }
//         });
//       }
//     });
//   }

//   // ===================== STEP NAVIGATION =====================

//   saveStep(): void {
//     if (!this.isCurrentStepValid()) {
//       this.messageService.showMessage('warning', 'Validation Error', 'Please fill all required fields on this step');
//       this.supplierForm.markAllAsTouched();
//       return;
//     }
//     this.messageService.showMessage('success', 'Saved', `Step ${this.currentStep} saved successfully`);
//     if (this.currentStep < this.maxStep) this.currentStep++;
//   }

//   prevStep(): void { if (this.currentStep > 1) this.currentStep--; }
//   nextStep(): void { if (this.currentStep < this.maxStep) this.currentStep++; }

//   // ===================== VALIDATION =====================

//   private isCurrentStepValid(): boolean {
//     switch (this.currentStep) {
//       case 1: return this.isSupplierDetailsValid();
//       case 2: return this.locationsBasicsValid();
//       case 3: return this.departmentsValid();
//       case 4: return this.usersValid();
//       default: return false;
//     }
//   }

//   private isSupplierDetailsValid(): boolean {
//     const fields =['companyName', 'companyType', 'industrySector', 'contactPersonName',
//                 'contactPersonDesignation', 'contactPersonEmail', 'contactPersonPhone',
//                 'addressLine1', 'city', 'state', 'postalCode', 'country',
//                 'gstNumber', 'panNumber'];
                
//     const standardFieldsValid = fields.every(field => this.supplierForm.get(field)?.valid);
//     const companyOtherValid: boolean = this.supplierForm.get('companyType')?.value === 'Others'
//       ? (this.supplierForm.get('otherCompanyType')?.valid ?? false) : true;
//     const industryOtherValid: boolean = this.supplierForm.get('industrySector')?.value === 'Others'
//       ? (this.supplierForm.get('otherIndustrySector')?.valid ?? false) : true;
//     return standardFieldsValid && companyOtherValid && industryOtherValid;
//   }

//   private locationsBasicsValid(): boolean {
//     return this.locations.length > 0 &&
//       this.locations.controls.every(loc => {
//         const fields = ['locationName', 'locationType', 'locationContactName',
//                         'locationContactEmail', 'locationContactPhone', 'addressLine1',
//                         'city', 'state', 'postalCode', 'country'];
//         const standardFieldsValid = fields.every(field => loc.get(field)?.valid);
//         const otherValid: boolean = loc.get('locationType')?.value === 'Others'
//           ? (loc.get('otherLocationType')?.valid ?? false) : true;
//         return standardFieldsValid && otherValid;
//       });
//   }

//   private departmentsValid(): boolean {
//     return this.departments.length > 0 && this.departments.controls.every(dept => {
//       const categoryOtherValid: boolean = dept.get('categoryOfProducts')?.value === 'Others'
//         ? (dept.get('otherCategoryType')?.valid ?? false) : true;
//       return dept.get('departmentName')?.valid && dept.get('locationIndex')?.valid && categoryOtherValid;
//     });
//   }

//   private usersValid(): boolean {
//     return this.users.length > 0 && this.users.controls.every(user =>
//       user.get('firstName')?.valid &&
//       user.get('lastName')?.valid &&
//       user.get('email')?.valid &&
//       user.get('phone')?.valid &&
//       user.get('designation')?.valid &&
//       user.get('employeeId')?.valid &&
//       user.get('departmentIndex')?.valid &&
//       user.get('password')?.valid
//     );
//   }

//   // ===================== SUBMIT =====================

//   async onSubmit(): Promise<void> {
//     if (this.supplierForm.invalid) {
//       this.supplierForm.markAllAsTouched();
//       this.messageService.showMessage('error', 'Validation Error', 'Please fill all required fields');
//       return;
//     }

//     this.isSubmitting = true;
//     const formValue = this.supplierForm.getRawValue();

//     if (formValue.companyType === 'Others' && formValue.otherCompanyType) {
//       formValue.companyType = formValue.otherCompanyType;
//     }
//     delete formValue.otherCompanyType;

//     if (formValue.industrySector === 'Others' && formValue.otherIndustrySector) {
//       formValue.industrySector = formValue.otherIndustrySector;
//     }
//     delete formValue.otherIndustrySector;

//     const processedLocations: any[] = [];

//     if (formValue.locations && Array.isArray(formValue.locations)) {
//       formValue.locations.forEach((loc: any) => {
//         if (loc.locationType === 'Others' && loc.otherLocationType) {
//           loc.locationType = loc.otherLocationType;
//         }
//         delete loc.otherLocationType;
//         loc.departments = [];
//         processedLocations.push(loc);
//       });
//     }

//     if (formValue.departments && Array.isArray(formValue.departments)) {
//       formValue.departments.forEach((dept: any) => {
//         const locationIdx = parseInt(dept.locationIndex, 10);
//         if (isNaN(locationIdx) || !processedLocations[locationIdx]) return;
//         const { locationIndex: _, ...deptData } = dept;
//         if (deptData.categoryOfProducts === 'Others' && deptData.otherCategoryType) {
//           deptData.categoryOfProducts = deptData.otherCategoryType;
//         }
//         delete deptData.otherCategoryType;
//         deptData.users = [];
//         processedLocations[locationIdx].departments.push(deptData);
//       });
//     }

//     if (formValue.users && Array.isArray(formValue.users)) {
//       formValue.users.forEach((user: any) => {
//         const deptIdx = parseInt(user.departmentIndex, 10);
//         const department = formValue.departments?.[deptIdx];
//         if (!department) return;
//         const locationIdx = parseInt(department.locationIndex, 10);
//         if (!processedLocations[locationIdx]) return;
//         const targetDept = processedLocations[locationIdx].departments.find(
//           (d: any) => d.departmentName === department.departmentName
//         );
//         if (targetDept) {
//           const { departmentIndex: _, ...userData } = user;
//           if (this.mode === 'edit' && (!userData.password || !userData.password.trim())) {
//             delete userData.password;
//           }
//           targetDept.users.push(userData);
//         }
//       });
//     }

//     const payload: any = { ...formValue, locations: processedLocations };
//     delete payload.departments;
//     delete payload.users;
//     delete payload.logo;

//     if (this.selectedLogoFile) {
//       try {
//         const logoBase64 = await this.supplierService.convertFileToBase64(this.selectedLogoFile);
//         payload.logoBase64 = logoBase64;
//         payload.logoFilename = this.selectedLogoFile.name;
//         payload.logoContentType = this.selectedLogoFile.type;
//       } catch (error) {
//         this.messageService.showMessage('error', 'Error', 'Failed to process logo');
//         this.isSubmitting = false;
//         return;
//       }
//     }

//     if (this.mode === 'create') {
//       this.supplierService.createCompleteHierarchy(payload).subscribe({
//         next: () => {
//           this.messageService.showMessage('success', 'Success', 'Supplier created successfully');
//           this.isSubmitting = false;
//           setTimeout(() => this.router.navigate(['/dashboard']), 1500);
//         },
//         error: (err: any) => {
//           this.messageService.showMessage('error', 'Error', err.error?.message || err.message || 'Failed to create supplier');
//           this.isSubmitting = false;
//         }
//       });
//     } else if (this.supplierId) {
//       this.supplierService.updateCompleteHierarchy(this.supplierId, payload).subscribe({
//         next: () => {
//           this.messageService.showMessage('success', 'Success', 'Supplier updated successfully');
//           this.isSubmitting = false;
//           setTimeout(() => this.router.navigate(['/dashboard']), 1500);
//         },
//         error: (err: any) => {
//           this.messageService.showMessage('error', 'Error', err.error?.message || err.message || 'Failed to update supplier');
//           this.isSubmitting = false;
//         }
//       });
//     }
//   }
// }


import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, FormArray } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { SupplierService } from '../dashboard/supplier.service';
import { MessageService } from '../../../shared/service/message.service';
import { Supplier, SupplierLocation, SupplierDepartment, SupplierUser } from '../dashboard/supplier.model';

import {
  getAllCountryNames,
  getStatesForCountry,
  getCityNamesForState,
  getPostalCodeForCity
} from '../../../shared/data/country-state.data';

@Component({
  selector: 'app-create-s',
  templateUrl: './create-s.component.html',
  styleUrls: ['./create-s.component.css'],
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule]
})
export class CreateSComponent implements OnInit {

  supplierForm!: FormGroup;
  currentStep = 1;
  maxStep = 4;
  isSubmitting = false;

  mode: 'create' | 'edit' = 'create';
  supplierId: number | null = null;

  selectedLogoFile: File | null = null;
  logoPreview: string | null = null;
  maxLogoSize = 5 * 1024 * 1024;
  allowedLogoTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif', 'image/svg+xml'];

  email: string = '';
  companyName: string = '';
  fullName: string = '';

  allCountries: string[] = getAllCountryNames();

  // ===== HQ cascades =====
  hqStates: string[] = [];
  hqCities: string[] = [];
  hqCityIsOthers = false;              // ✅

  // ===== Per-location cascades =====
  locationStates: string[][] = [];
  locationCities: string[][] = [];
  locationCityIsOthers: boolean[] = []; // ✅

  companyTypes = ['Manufacturing', 'Trading', 'Services', 'Distribution', 'Retail', 'Others'];
  locationTypes = ['Head Office', 'Branch', 'Warehouse', 'Service Center', 'Others'];
  industrySectors = ['IT', 'Logistics', 'Electrical', 'Construction', 'Healthcare',
                     'Automotive', 'Textile', 'Food & Beverage', 'Others'];
  categoryOfProducts = ['IT Equipment', 'Industrial Equipment', 'Software Services',
                        'Hardware Supplies', 'Others'];

  constructor(
    private fb: FormBuilder,
    private supplierService: SupplierService,
    private messageService: MessageService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.initializeForm();
    this.loadUserHeaderInfo();
    this.checkEditMode();
    this.hqStates = getStatesForCountry('India');
  }

  private loadUserHeaderInfo(): void {
    this.email = localStorage.getItem('email') || 'admin@gmail.com';
    this.companyName = localStorage.getItem('companyName') || 'ITI Pvt Ltd Bangalore';
    this.fullName = localStorage.getItem('fullName') || 'Admin';
  }

  private initializeForm(): void {
    this.supplierForm = this.fb.group({
      companyName: ['', [Validators.required, Validators.minLength(2)]],
      companyType: ['', Validators.required],
      otherCompanyType: [''],
      industrySector: ['', Validators.required],
      otherIndustrySector: [''],
      logo: [null],
      contactPersonName: ['', [Validators.required, Validators.minLength(2)]],
      contactPersonDesignation: ['', Validators.required],
      contactPersonEmail: ['', [Validators.required, Validators.email]],
      contactPersonPhone: ['', [Validators.required, Validators.pattern(/^[0-9]{10}$/)]],
      addressLine1: ['', [Validators.required, Validators.minLength(5)]],
      addressLine2: [''],
      city: ['', Validators.required],
      otherCity: [''],                 // ✅ HQ custom city
      state: ['', Validators.required],
      postalCode: ['', [Validators.required, Validators.pattern(/^[A-Za-z0-9\s\-]{3,10}$/)]],
      country: ['India', Validators.required],
      gstNumber: [''],
      panNumber: [''],
      tanNumber: [''],
      website: [''],
      locations: this.fb.array([], Validators.minLength(1)),
      departments: this.fb.array([]),
      users: this.fb.array([])
    });

    if (!this.supplierForm.get('locations')?.value?.length) {
      this.addLocation();
    }
  }

  // ======================================================================
  // HQ: COUNTRY → STATE → CITY (+ Others) → POSTAL
  // ======================================================================

  onHQCountryChange(event: any): void {
    const country = event.target.value;
    this.hqStates = getStatesForCountry(country);
    this.hqCities = [];
    this.hqCityIsOthers = false;
    this.supplierForm.get('otherCity')?.clearValidators();
    this.supplierForm.get('otherCity')?.setValue('');
    this.supplierForm.get('otherCity')?.updateValueAndValidity();
    this.supplierForm.patchValue({ state: '', city: '', postalCode: '' });
  }

  onHQStateChange(event: any): void {
    const country = this.supplierForm.get('country')?.value;
    const state = event.target.value;
    this.hqCities = getCityNamesForState(country, state);
    this.hqCityIsOthers = false;
    this.supplierForm.get('otherCity')?.clearValidators();
    this.supplierForm.get('otherCity')?.setValue('');
    this.supplierForm.get('otherCity')?.updateValueAndValidity();
    this.supplierForm.patchValue({ city: '', postalCode: '' });
  }

  onHQCityChange(event: any): void {
    const selected = event.target.value;
    if (selected === '__others__') {
      this.hqCityIsOthers = true;
      this.supplierForm.patchValue({ city: '__others__', otherCity: '', postalCode: '' });
      this.supplierForm.get('otherCity')?.setValidators([Validators.required, Validators.minLength(2)]);
      this.supplierForm.get('otherCity')?.updateValueAndValidity();
    } else {
      this.hqCityIsOthers = false;
      this.supplierForm.get('otherCity')?.clearValidators();
      this.supplierForm.get('otherCity')?.setValue('');
      this.supplierForm.get('otherCity')?.updateValueAndValidity();
      const country = this.supplierForm.get('country')?.value;
      const state = this.supplierForm.get('state')?.value;
      const postal = getPostalCodeForCity(country, state, selected);
      this.supplierForm.patchValue({ city: selected, postalCode: postal || '' });
    }
  }

  // ======================================================================
  // LOCATION: COUNTRY → STATE → CITY (+ Others) → POSTAL
  // ======================================================================

  onLocationCountryChange(event: any, idx: number): void {
    const country = event.target.value;
    this.locationStates[idx] = getStatesForCountry(country);
    this.locationCities[idx] = [];
    this.locationCityIsOthers[idx] = false;
    const loc = this.locations.at(idx);
    loc.get('otherCity')?.clearValidators();
    loc.get('otherCity')?.setValue('');
    loc.get('otherCity')?.updateValueAndValidity();
    loc.patchValue({ state: '', city: '', postalCode: '' });
  }

  onLocationStateChange(event: any, idx: number): void {
    const country = this.locations.at(idx).get('country')?.value;
    const state = event.target.value;
    this.locationCities[idx] = getCityNamesForState(country, state);
    this.locationCityIsOthers[idx] = false;
    const loc = this.locations.at(idx);
    loc.get('otherCity')?.clearValidators();
    loc.get('otherCity')?.setValue('');
    loc.get('otherCity')?.updateValueAndValidity();
    loc.patchValue({ city: '', postalCode: '' });
  }

  onLocationCityChange(event: any, idx: number): void {
    const selected = event.target.value;
    const loc = this.locations.at(idx);
    if (selected === '__others__') {
      this.locationCityIsOthers[idx] = true;
      loc.patchValue({ city: '__others__', otherCity: '', postalCode: '' });
      loc.get('otherCity')?.setValidators([Validators.required, Validators.minLength(2)]);
      loc.get('otherCity')?.updateValueAndValidity();
    } else {
      this.locationCityIsOthers[idx] = false;
      loc.get('otherCity')?.clearValidators();
      loc.get('otherCity')?.setValue('');
      loc.get('otherCity')?.updateValueAndValidity();
      const country = loc.get('country')?.value;
      const state = loc.get('state')?.value;
      const postal = getPostalCodeForCity(country, state, selected);
      loc.patchValue({ city: selected, postalCode: postal || '' });
    }
  }

  getStatesForLocation(idx: number): string[] { return this.locationStates[idx] || []; }
  getCitiesForLocation(idx: number): string[] { return this.locationCities[idx] || []; }
  isLocationCityOthers(idx: number): boolean { return this.locationCityIsOthers[idx] || false; }

  // ======================================================================
  // LOGO
  // ======================================================================

  onLogoSelected(event: any): void {
    const file = event.target.files[0];
    if (!file) return;
    if (!this.allowedLogoTypes.includes(file.type)) {
      this.messageService.showMessage('error', 'Invalid File Type',
        'Please select a valid image file (JPG, PNG, GIF, SVG)');
      event.target.value = '';
      return;
    }
    if (file.size > this.maxLogoSize) {
      this.messageService.showMessage('error', 'File Too Large', 'Logo must be smaller than 5MB');
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
    this.supplierForm.patchValue({ logo: null });
    const fileInput = document.querySelector('input[type="file"]') as HTMLInputElement;
    if (fileInput) fileInput.value = '';
  }

  private checkEditMode(): void {
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      if (id) {
        this.mode = 'edit';
        this.supplierId = +id;
        this.currentStep = 1;
        this.loadSupplierData(this.supplierId);
      } else {
        this.mode = 'create';
        this.currentStep = 1;
      }
    });
  }

  get locations(): FormArray { return this.supplierForm.get('locations') as FormArray; }
  get departments(): FormArray { return this.supplierForm.get('departments') as FormArray; }
  get users(): FormArray { return this.supplierForm.get('users') as FormArray; }

  // ======================================================================
  // FORM ARRAYS
  // ======================================================================

  addLocation(): void {
    const locationGroup = this.fb.group({
      id: [null],
      locationName: ['', [Validators.required, Validators.minLength(2)]],
      locationType: ['', Validators.required],
      otherLocationType: [''],
      locationContactName: ['', [Validators.required, Validators.minLength(2)]],
      locationContactEmail: ['', [Validators.required, Validators.email]],
      locationContactPhone: ['', [Validators.required, Validators.pattern(/^[0-9]{10}$/)]],
      addressLine1: ['', [Validators.required, Validators.minLength(5)]],
      addressLine2: [''],
      city: ['', Validators.required],
      otherCity: [''],               // ✅ custom city per location
      state: ['', Validators.required],
      postalCode: ['', [Validators.required, Validators.pattern(/^[A-Za-z0-9\s\-]{3,10}$/)]],
      country: ['India', Validators.required],
      landlineNumber: [''],
      faxNumber: ['']
    });

    const newIndex = this.locations.length;
    this.locations.push(locationGroup);
    this.locationStates[newIndex] = getStatesForCountry('India');
    this.locationCities[newIndex] = [];
    this.locationCityIsOthers[newIndex] = false;
  }

  removeLocation(index: number): void {
    if (this.locations.length > 1) {
      this.locations.removeAt(index);
      this.locationStates.splice(index, 1);
      this.locationCities.splice(index, 1);
      this.locationCityIsOthers.splice(index, 1);
    } else {
      this.messageService.showMessage('warning', 'Warning', 'At least one location is required');
    }
  }

  addDepartment(): void {
    const departmentGroup = this.fb.group({
      id: [null],
      locationIndex: ['', Validators.required],
      departmentName: ['', [Validators.required, Validators.minLength(2)]],
      departmentDescription: [''],
      categoryOfProducts: [''],
      otherCategoryType: ['']
    });
    this.departments.push(departmentGroup);
  }

  removeDepartment(deptIndex: number): void {
    if (this.departments.length > 1) {
      this.departments.removeAt(deptIndex);
    } else {
      this.messageService.showMessage('warning', 'Warning', 'At least one category is required');
    }
  }

  addUser(userData?: Partial<SupplierUser>): void {
    const userGroup = this.fb.group({
      id: [userData?.id || null],
      departmentIndex: [userData ? (userData as any).departmentIndex : '', Validators.required],
      firstName: [userData?.firstName || '', [Validators.required, Validators.minLength(2)]],
      lastName: [userData?.lastName || '', [Validators.required, Validators.minLength(2)]],
      email: [userData?.email || '', [Validators.required, Validators.email]],
      phone: [userData?.phone || '', [Validators.required, Validators.pattern(/^[0-9]{10}$/)]],
      designation: [userData?.designation || '', Validators.required],
      employeeId: [userData?.employeeId || '', Validators.required],
      gender: [userData?.gender || ''],
      dateOfBirth: [userData?.dateOfBirth || ''],
      addressLine1: [userData?.addressLine1 || ''],
      addressLine2: [userData?.addressLine2 || ''],
      city: [userData?.city || ''],
      state: [userData?.state || ''],
      postalCode: [userData?.postalCode || ''],
      password: [userData?.password || '', [Validators.required, Validators.minLength(6)]]
    });
    this.users.push(userGroup);
  }

  removeUser(userIndex: number): void {
    if (this.users.length > 1) {
      this.users.removeAt(userIndex);
    } else {
      this.messageService.showMessage('warning', 'Warning', 'At least one contact is required');
    }
  }

  getLocationNameForDepartment(deptIndex: number): string {
    const dept = this.departments.at(deptIndex);
    const locIndex = dept?.get('locationIndex')?.value;
    if (locIndex !== '' && locIndex !== null && locIndex !== undefined) {
      const location = this.locations.at(locIndex);
      return location?.get('locationName')?.value || `Location ${locIndex + 1}`;
    }
    return '';
  }

  // ======================================================================
  // OTHERS HANDLING
  // ======================================================================

  onCompanyTypeChange(event: any): void {
    const ctrl = this.supplierForm.get('otherCompanyType');
    if (event.target.value === 'Others') {
      ctrl?.setValidators([Validators.required, Validators.minLength(2)]);
      ctrl?.setValue('');
    } else { ctrl?.clearValidators(); ctrl?.setValue(null); }
    ctrl?.updateValueAndValidity();
  }

  onIndustrySectorChange(event: any): void {
    const ctrl = this.supplierForm.get('otherIndustrySector');
    if (event.target.value === 'Others') {
      ctrl?.setValidators([Validators.required, Validators.minLength(2)]);
      ctrl?.setValue('');
    } else { ctrl?.clearValidators(); ctrl?.setValue(null); }
    ctrl?.updateValueAndValidity();
  }

  onLocationTypeChange(event: any, locationIndex: number): void {
    const locationGroup = this.locations.at(locationIndex) as FormGroup;
    const ctrl = locationGroup.get('otherLocationType');
    if (event.target.value === 'Others') {
      ctrl?.setValidators([Validators.required, Validators.minLength(2)]);
      ctrl?.setValue('');
    } else { ctrl?.clearValidators(); ctrl?.setValue(null); }
    ctrl?.updateValueAndValidity();
  }

  onCategoryTypeChange(event: any, deptIndex: number): void {
    const departmentGroup = this.departments.at(deptIndex) as FormGroup;
    const ctrl = departmentGroup.get('otherCategoryType');
    if (event.target.value === 'Others') {
      ctrl?.setValidators([Validators.required, Validators.minLength(2)]);
      ctrl?.setValue('');
    } else { ctrl?.clearValidators(); ctrl?.setValue(null); }
    ctrl?.updateValueAndValidity();
  }

  // ======================================================================
  // EDIT MODE — LOAD SUPPLIER DATA
  // ======================================================================

  private loadSupplierData(id: number): void {
    this.supplierService.getSupplierById(id).subscribe({
      next: (supplier: any) => {
        let companyTypeForForm = supplier.companyType;
        let otherCompanyType = null;
        let industrySectorForForm = supplier.industrySector;
        let otherIndustrySector = null;

        if (supplier.companyType && !this.companyTypes.includes(supplier.companyType)) {
          companyTypeForForm = 'Others'; otherCompanyType = supplier.companyType;
        }
        if (supplier.industrySector && !this.industrySectors.includes(supplier.industrySector)) {
          industrySectorForForm = 'Others'; otherIndustrySector = supplier.industrySector;
        }

        this.supplierForm.patchValue({
          companyName: supplier.companyName,
          companyType: companyTypeForForm, otherCompanyType,
          industrySector: industrySectorForForm, otherIndustrySector,
          contactPersonName: supplier.contactPersonName,
          contactPersonDesignation: supplier.contactPersonDesignation,
          contactPersonEmail: supplier.contactPersonEmail,
          contactPersonPhone: supplier.contactPersonPhone,
          addressLine1: supplier.addressLine1, addressLine2: supplier.addressLine2,
          state: supplier.state, postalCode: supplier.postalCode,
          country: supplier.country,
          gstNumber: supplier.gstNumber, panNumber: supplier.panNumber,
          tanNumber: supplier.tanNumber, website: supplier.website
        });

        if (supplier.country) this.hqStates = getStatesForCountry(supplier.country);
        if (supplier.country && supplier.state) {
          this.hqCities = getCityNamesForState(supplier.country, supplier.state);
        }

        // ✅ Detect custom city on load
        const knownCities = this.hqCities;
        if (supplier.city && knownCities.length > 0 && !knownCities.includes(supplier.city)) {
          this.hqCityIsOthers = true;
          this.supplierForm.patchValue({ city: '__others__', otherCity: supplier.city });
          this.supplierForm.get('otherCity')?.setValidators([Validators.required, Validators.minLength(2)]);
          this.supplierForm.get('otherCity')?.updateValueAndValidity();
        } else {
          this.hqCityIsOthers = false;
          this.supplierForm.patchValue({ city: supplier.city });
        }

        this.onCompanyTypeChange({ target: { value: companyTypeForForm } });
        this.onIndustrySectorChange({ target: { value: industrySectorForForm } });

        if (supplier.id) {
          this.supplierService.getSupplierLogoBase64(supplier.id).subscribe({
            next: (logoData: any) => { if (logoData) this.logoPreview = logoData; },
            error: () => {}
          });
        }

        while (this.locations.length) this.locations.removeAt(0);
        while (this.departments.length) this.departments.removeAt(0);
        while (this.users.length) this.users.removeAt(0);
        this.locationStates = [];
        this.locationCities = [];
        this.locationCityIsOthers = [];

        if (supplier.locations && Array.isArray(supplier.locations)) {
          this.populateLocations(supplier.locations);
        }
      },
      error: () => {
        this.messageService.showMessage('error', 'Error', 'Failed to load supplier data');
        setTimeout(() => this.router.navigate(['/dashboard']), 2000);
      }
    });
  }

  private populateLocations(locations: SupplierLocation[]): void {
    locations.forEach((loc: any, locIndex: number) => {
      this.addLocation();

      let locationTypeForForm = loc.locationType;
      let otherLocationType = null;
      if (loc.locationType && !this.locationTypes.includes(loc.locationType)) {
        locationTypeForForm = 'Others'; otherLocationType = loc.locationType;
      }

      if (loc.country) this.locationStates[locIndex] = getStatesForCountry(loc.country);
      if (loc.country && loc.state) {
        this.locationCities[locIndex] = getCityNamesForState(loc.country, loc.state);
      }

      // ✅ Detect custom city per location
      const knownCities = this.locationCities[locIndex] || [];
      let cityValue = loc.city;
      let otherCityValue = '';
      if (loc.city && knownCities.length > 0 && !knownCities.includes(loc.city)) {
        this.locationCityIsOthers[locIndex] = true;
        cityValue = '__others__';
        otherCityValue = loc.city;
      } else {
        this.locationCityIsOthers[locIndex] = false;
      }

      this.locations.at(locIndex).patchValue({
        id: loc.id,
        locationName: loc.locationName, locationType: locationTypeForForm,
        otherLocationType, locationContactName: loc.locationContactName,
        locationContactEmail: loc.locationContactEmail,
        locationContactPhone: loc.locationContactPhone,
        addressLine1: loc.addressLine1, addressLine2: loc.addressLine2,
        city: cityValue, otherCity: otherCityValue,
        state: loc.state, postalCode: loc.postalCode,
        country: loc.country, landlineNumber: loc.landlineNumber, faxNumber: loc.faxNumber
      });

      if (this.locationCityIsOthers[locIndex]) {
        this.locations.at(locIndex).get('otherCity')
          ?.setValidators([Validators.required, Validators.minLength(2)]);
        this.locations.at(locIndex).get('otherCity')?.updateValueAndValidity();
      }

      this.onLocationTypeChange({ target: { value: locationTypeForForm } }, locIndex);

      if (loc.departments && loc.departments.length > 0) {
        loc.departments.forEach((dept: any) => {
          const deptIndex = this.departments.length;
          this.addDepartment();
          let categoryForForm = dept.categoryOfProducts;
          let otherCategoryType = null;
          if (dept.categoryOfProducts && !this.categoryOfProducts.includes(dept.categoryOfProducts)) {
            categoryForForm = 'Others'; otherCategoryType = dept.categoryOfProducts;
          }
          this.departments.at(deptIndex).patchValue({
            id: dept.id, locationIndex: locIndex,
            departmentName: dept.departmentName, departmentDescription: dept.departmentDescription,
            categoryOfProducts: categoryForForm, otherCategoryType
          });
          this.onCategoryTypeChange({ target: { value: categoryForForm } }, deptIndex);

          if (dept.users && dept.users.length > 0) {
            dept.users.forEach((user: any) => {
              this.addUser({
                id: user.id, departmentIndex: deptIndex,
                firstName: user.firstName, lastName: user.lastName,
                email: user.email, phone: user.phone,
                designation: user.designation, employeeId: user.employeeId,
                gender: user.gender, dateOfBirth: user.dateOfBirth,
                addressLine1: user.addressLine1, addressLine2: user.addressLine2,
                city: user.city, state: user.state,
                postalCode: user.postalCode, password: ''
              });
            });
          }
        });
      }
    });
  }

  // ======================================================================
  // STEP VALIDATION
  // ======================================================================

  private isCurrentStepValid(): boolean {
    switch (this.currentStep) {
      case 1: return this.isSupplierDetailsValid();
      case 2: return this.locationsBasicsValid();
      case 3: return this.departmentsValid();
      case 4: return this.usersValid();
      default: return false;
    }
  }

  private isSupplierDetailsValid(): boolean {
    const fields = ['companyName', 'companyType', 'industrySector', 'contactPersonName',
                    'contactPersonDesignation', 'contactPersonEmail', 'contactPersonPhone',
                    'addressLine1', 'city', 'state', 'postalCode', 'country'];
    const standardValid = fields.every(f => this.supplierForm.get(f)?.valid);
    const companyOtherValid: boolean = this.supplierForm.get('companyType')?.value === 'Others'
      ? (this.supplierForm.get('otherCompanyType')?.valid ?? false) : true;
    const industryOtherValid: boolean = this.supplierForm.get('industrySector')?.value === 'Others'
      ? (this.supplierForm.get('otherIndustrySector')?.valid ?? false) : true;
    const otherCityValid: boolean = this.hqCityIsOthers
      ? (this.supplierForm.get('otherCity')?.valid ?? false) : true;
    return standardValid && companyOtherValid && industryOtherValid && otherCityValid;
  }

  private locationsBasicsValid(): boolean {
    return this.locations.length > 0 &&
      this.locations.controls.every((loc, idx) => {
        const fields = ['locationName', 'locationType', 'locationContactName',
                        'locationContactEmail', 'locationContactPhone', 'addressLine1',
                        'city', 'state', 'postalCode', 'country'];
        const standardValid = fields.every(f => loc.get(f)?.valid);
        const otherTypeValid: boolean = loc.get('locationType')?.value === 'Others'
          ? (loc.get('otherLocationType')?.valid ?? false) : true;
        const otherCityValid: boolean = this.locationCityIsOthers[idx]
          ? (loc.get('otherCity')?.valid ?? false) : true;
        return standardValid && otherTypeValid && otherCityValid;
      });
  }

  private departmentsValid(): boolean {
    return this.departments.length > 0 && this.departments.controls.every(dept => {
      const catOtherValid: boolean = dept.get('categoryOfProducts')?.value === 'Others'
        ? (dept.get('otherCategoryType')?.valid ?? false) : true;
      return dept.get('departmentName')?.valid && dept.get('locationIndex')?.valid && catOtherValid;
    });
  }

  private usersValid(): boolean {
    return this.users.length > 0 && this.users.controls.every(user =>
      user.get('firstName')?.valid && user.get('lastName')?.valid &&
      user.get('email')?.valid && user.get('phone')?.valid &&
      user.get('designation')?.valid && user.get('employeeId')?.valid &&
      user.get('departmentIndex')?.valid && user.get('password')?.valid
    );
  }

  saveStep(): void {
    if (!this.isCurrentStepValid()) {
      this.messageService.showMessage('warning', 'Validation Error', 'Please fill all required fields on this step');
      this.supplierForm.markAllAsTouched();
      return;
    }
    this.messageService.showMessage('success', 'Saved', `Step ${this.currentStep} saved successfully`);
    if (this.currentStep < this.maxStep) this.currentStep++;
  }

  prevStep(): void { if (this.currentStep > 1) this.currentStep--; }
  nextStep(): void { if (this.currentStep < this.maxStep) this.currentStep++; }

  // ======================================================================
  // SUBMIT — resolve all Others values
  // ======================================================================

  async onSubmit(): Promise<void> {
    if (this.supplierForm.invalid) {
      this.supplierForm.markAllAsTouched();
      this.messageService.showMessage('error', 'Validation Error', 'Please fill all required fields');
      return;
    }

    this.isSubmitting = true;
    const formValue = this.supplierForm.getRawValue();

    // ✅ Resolve HQ Others city
    if (formValue.city === '__others__' && formValue.otherCity) formValue.city = formValue.otherCity;
    delete formValue.otherCity;

    if (formValue.companyType === 'Others' && formValue.otherCompanyType) formValue.companyType = formValue.otherCompanyType;
    delete formValue.otherCompanyType;

    if (formValue.industrySector === 'Others' && formValue.otherIndustrySector) formValue.industrySector = formValue.otherIndustrySector;
    delete formValue.otherIndustrySector;

    const processedLocations: any[] = [];
    if (formValue.locations && Array.isArray(formValue.locations)) {
      formValue.locations.forEach((loc: any) => {
        // ✅ Resolve location Others city
        if (loc.city === '__others__' && loc.otherCity) loc.city = loc.otherCity;
        delete loc.otherCity;
        if (loc.locationType === 'Others' && loc.otherLocationType) loc.locationType = loc.otherLocationType;
        delete loc.otherLocationType;
        loc.departments = [];
        processedLocations.push(loc);
      });
    }

    if (formValue.departments && Array.isArray(formValue.departments)) {
      formValue.departments.forEach((dept: any) => {
        const locationIdx = parseInt(dept.locationIndex, 10);
        if (isNaN(locationIdx) || !processedLocations[locationIdx]) return;
        const { locationIndex: _, ...deptData } = dept;
        if (deptData.categoryOfProducts === 'Others' && deptData.otherCategoryType) {
          deptData.categoryOfProducts = deptData.otherCategoryType;
        }
        delete deptData.otherCategoryType;
        deptData.users = [];
        processedLocations[locationIdx].departments.push(deptData);
      });
    }

    if (formValue.users && Array.isArray(formValue.users)) {
      formValue.users.forEach((user: any) => {
        const deptIdx = parseInt(user.departmentIndex, 10);
        const department = formValue.departments?.[deptIdx];
        if (!department) return;
        const locationIdx = parseInt(department.locationIndex, 10);
        if (!processedLocations[locationIdx]) return;
        const targetDept = processedLocations[locationIdx].departments.find(
          (d: any) => d.departmentName === department.departmentName
        );
        if (targetDept) {
          const { departmentIndex: _, ...userData } = user;
          if (this.mode === 'edit' && (!userData.password || !userData.password.trim())) {
            delete userData.password;
          }
          targetDept.users.push(userData);
        }
      });
    }

    const payload: any = { ...formValue, locations: processedLocations };
    delete payload.departments;
    delete payload.users;
    delete payload.logo;
    // ✅ ADD THESE — needed for approval workflow routing
payload.createdByCompanyName = localStorage.getItem('companyName') || '';
payload.createdByUserId = Number(localStorage.getItem('userId')) || null;

    if (this.selectedLogoFile) {
      try {
        const logoBase64 = await this.supplierService.convertFileToBase64(this.selectedLogoFile);
        payload.logoBase64 = logoBase64;
        payload.logoFilename = this.selectedLogoFile.name;
        payload.logoContentType = this.selectedLogoFile.type;
      } catch (error) {
        this.messageService.showMessage('error', 'Error', 'Failed to process logo');
        this.isSubmitting = false;
        return;
      }
    }

    if (this.mode === 'create') {
      this.supplierService.createCompleteHierarchy(payload).subscribe({
        next: () => {
          this.messageService.showMessage('success', 'Success', 'Supplier created successfully');
          this.isSubmitting = false;
          setTimeout(() => this.router.navigate(['/dashboard']), 1500);
        },
        error: (err: any) => {
          this.messageService.showMessage('error', 'Error', err.error?.message || err.message || 'Failed to create supplier');
          this.isSubmitting = false;
        }
      });
    } else if (this.supplierId) {
      this.supplierService.updateCompleteHierarchy(this.supplierId, payload).subscribe({
        next: () => {
          this.messageService.showMessage('success', 'Success', 'Supplier updated successfully');
          this.isSubmitting = false;
          setTimeout(() => this.router.navigate(['/dashboard']), 1500);
        },
        error: (err: any) => {
          this.messageService.showMessage('error', 'Error', err.error?.message || err.message || 'Failed to update supplier');
          this.isSubmitting = false;
        }
      });
    }
  }
}