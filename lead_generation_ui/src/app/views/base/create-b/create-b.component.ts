

// import { Component, OnInit } from '@angular/core';
// import { FormBuilder, FormGroup, Validators, FormArray } from '@angular/forms';
// import { Router, ActivatedRoute } from '@angular/router';
// import { CommonModule } from '@angular/common';
// import { ReactiveFormsModule } from '@angular/forms';
// import { BuyerService } from '../dashboard/buyer-b.service';
// import { MessageService } from '../../../shared/service/message.service';
// import { Buyer, Location, Department, User } from '../dashboard/buyer-b.model';

// // ✅ Import from NEW unified data file
// import {
//   getAllCountryNames,
//   getStatesForCountry,
//   getCityNamesForState,
//   getPostalCodeForCity,
//   CityData
// } from '../../../shared/data/country-state.data';

// export const WORLD_CURRENCIES = [
//   { code: 'AFN', symbol: '؋', name: 'Afghan Afghani' },
//   { code: 'ALL', symbol: 'L', name: 'Albanian Lek' },
//   { code: 'DZD', symbol: 'د.ج', name: 'Algerian Dinar' },
//   { code: 'ARS', symbol: '$', name: 'Argentine Peso' },
//   { code: 'AMD', symbol: '֏', name: 'Armenian Dram' },
//   { code: 'AUD', symbol: 'A$', name: 'Australian Dollar' },
//   { code: 'AZN', symbol: '₼', name: 'Azerbaijani Manat' },
//   { code: 'BHD', symbol: '.د.ب', name: 'Bahraini Dinar' },
//   { code: 'BDT', symbol: '৳', name: 'Bangladeshi Taka' },
//   { code: 'BRL', symbol: 'R$', name: 'Brazilian Real' },
//   { code: 'GBP', symbol: '£', name: 'British Pound Sterling' },
//   { code: 'CAD', symbol: 'C$', name: 'Canadian Dollar' },
//   { code: 'CLP', symbol: '$', name: 'Chilean Peso' },
//   { code: 'CNY', symbol: '¥', name: 'Chinese Yuan' },
//   { code: 'COP', symbol: '$', name: 'Colombian Peso' },
//   { code: 'CZK', symbol: 'Kč', name: 'Czech Koruna' },
//   { code: 'DKK', symbol: 'kr', name: 'Danish Krone' },
//   { code: 'EGP', symbol: '£', name: 'Egyptian Pound' },
//   { code: 'ETB', symbol: 'Br', name: 'Ethiopian Birr' },
//   { code: 'EUR', symbol: '€', name: 'Euro' },
//   { code: 'GEL', symbol: '₾', name: 'Georgian Lari' },
//   { code: 'GHS', symbol: '₵', name: 'Ghanaian Cedi' },
//   { code: 'HUF', symbol: 'Ft', name: 'Hungarian Forint' },
//   { code: 'INR', symbol: '₹', name: 'Indian Rupee' },
//   { code: 'IDR', symbol: 'Rp', name: 'Indonesian Rupiah' },
//   { code: 'IRR', symbol: '﷼', name: 'Iranian Rial' },
//   { code: 'IQD', symbol: 'ع.د', name: 'Iraqi Dinar' },
//   { code: 'ILS', symbol: '₪', name: 'Israeli New Shekel' },
//   { code: 'JPY', symbol: '¥', name: 'Japanese Yen' },
//   { code: 'JOD', symbol: 'د.ا', name: 'Jordanian Dinar' },
//   { code: 'KZT', symbol: '₸', name: 'Kazakhstani Tenge' },
//   { code: 'KES', symbol: 'KSh', name: 'Kenyan Shilling' },
//   { code: 'KWD', symbol: 'د.ك', name: 'Kuwaiti Dinar' },
//   { code: 'MYR', symbol: 'RM', name: 'Malaysian Ringgit' },
//   { code: 'MXN', symbol: '$', name: 'Mexican Peso' },
//   { code: 'MAD', symbol: 'د.م.', name: 'Moroccan Dirham' },
//   { code: 'NPR', symbol: '₨', name: 'Nepalese Rupee' },
//   { code: 'NZD', symbol: 'NZ$', name: 'New Zealand Dollar' },
//   { code: 'NGN', symbol: '₦', name: 'Nigerian Naira' },
//   { code: 'NOK', symbol: 'kr', name: 'Norwegian Krone' },
//   { code: 'OMR', symbol: 'ر.ع.', name: 'Omani Rial' },
//   { code: 'PKR', symbol: '₨', name: 'Pakistani Rupee' },
//   { code: 'PHP', symbol: '₱', name: 'Philippine Peso' },
//   { code: 'PLN', symbol: 'zł', name: 'Polish Złoty' },
//   { code: 'QAR', symbol: 'ر.ق', name: 'Qatari Riyal' },
//   { code: 'RON', symbol: 'lei', name: 'Romanian Leu' },
//   { code: 'RUB', symbol: '₽', name: 'Russian Ruble' },
//   { code: 'SAR', symbol: 'ر.س', name: 'Saudi Riyal' },
//   { code: 'SGD', symbol: 'S$', name: 'Singapore Dollar' },
//   { code: 'ZAR', symbol: 'R', name: 'South African Rand' },
//   { code: 'KRW', symbol: '₩', name: 'South Korean Won' },
//   { code: 'LKR', symbol: '₨', name: 'Sri Lankan Rupee' },
//   { code: 'SEK', symbol: 'kr', name: 'Swedish Krona' },
//   { code: 'CHF', symbol: 'Fr', name: 'Swiss Franc' },
//   { code: 'TWD', symbol: 'NT$', name: 'New Taiwan Dollar' },
//   { code: 'THB', symbol: '฿', name: 'Thai Baht' },
//   { code: 'TRY', symbol: '₺', name: 'Turkish Lira' },
//   { code: 'AED', symbol: 'د.إ', name: 'UAE Dirham' },
//   { code: 'USD', symbol: '$', name: 'US Dollar' },
//   { code: 'UZS', symbol: 'лв', name: 'Uzbekistani Som' },
//   { code: 'VND', symbol: '₫', name: 'Vietnamese Đồng' },
//   { code: 'ZMW', symbol: 'ZK', name: 'Zambian Kwacha' },
// ];

// @Component({
//   selector: 'app-create-b',
//   templateUrl: './create-b.component.html',
//   styleUrls: ['./create-b.component.css'],
//   standalone: true,
//   imports: [CommonModule, ReactiveFormsModule]
// })
// export class CreateBComponent implements OnInit {

//   buyerForm!: FormGroup;
//   currentStep = 1;
//   maxStep = 4;
//   isSubmitting = false;

//   mode: 'create' | 'edit' = 'create';
//   buyerId: number | null = null;

//   isOrgAdminMode = false;
//   orgAdminId: number | null = null;
//   orgAdminCompanyName: string = '';

//   selectedLogoFile: File | null = null;
//   logoPreview: string | null = null;
//   maxLogoSize = 5 * 1024 * 1024;
//   allowedLogoTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif', 'image/svg+xml'];

//   worldCurrencies = WORLD_CURRENCIES;
//   allCountries: string[] = getAllCountryNames();

//   // ===== HQ cascades =====
//   hqStates: string[] = [];
//   hqCities: string[] = [];
//   hqCityIsOthers = false;          // ✅ true when "Others" selected for HQ city

//   // ===== Per-location cascades =====
//   locationStates: string[][] = [];
//   locationCities: string[][] = [];
//   locationCityIsOthers: boolean[] = [];  // ✅ per-location Others flag

//   // ===== Per-user cascades =====
//   userCities: string[][] = [];
//   userCityIsOthers: boolean[] = [];      // ✅ per-user Others flag

//   companyTypes = ['Manufacturing', 'Trading', 'Services', 'Distribution', 'Retail', 'Others'];
//   locationTypes = ['Branch', 'Regional', 'Warehouse', 'Service Center', 'Others'];

//   email: string = '';
//   companyName: string = '';
//   fullName: string = '';

//   constructor(
//     private fb: FormBuilder,
//     private buyerService: BuyerService,
//     private messageService: MessageService,
//     private router: Router,
//     private route: ActivatedRoute
//   ) {}

//   ngOnInit(): void {
//     this.checkOrgAdminMode();
//     this.loadUserHeaderInfo();
//     this.initializeForm();
//     this.checkEditMode();
//     this.hqStates = getStatesForCountry('India');
//   }

//   private loadUserHeaderInfo(): void {
//     this.email = localStorage.getItem('email') || 'admin@company.com';
//     this.companyName = localStorage.getItem('companyName') || 'Company Name';
//     this.fullName = localStorage.getItem('fullName') || 'Admin';
//   }

//   private checkOrgAdminMode(): void {
//     const role = localStorage.getItem('role');
//     if (role === 'ORGANIZATION_ADMIN') {
//       this.isOrgAdminMode = true;
//       this.orgAdminId = Number(localStorage.getItem('userId'));
//       this.orgAdminCompanyName = localStorage.getItem('companyName') || '';
//     }
//   }

//   private initializeForm(): void {
//     this.buyerForm = this.fb.group({
//       companyName: ['', [Validators.required, Validators.minLength(2)]],
//       companyType: ['', Validators.required],
//       otherCompanyType: [''],
//       logo: [null],
//       contactPersonName: ['', [Validators.required, Validators.minLength(2)]],
//       contactPersonDesignation: ['', Validators.required],
//       contactPersonEmail: ['', [Validators.required, Validators.email]],
//       contactPersonPhone: ['', [Validators.required, Validators.pattern(/^[0-9]{10}$/)]],
//       addressLine1: ['', [Validators.required, Validators.minLength(5)]],
//       addressLine2: [''],
//       city: ['', Validators.required],
//       otherCity: [''],                 // ✅ HQ custom city text
//       state: ['', Validators.required],
//       postalCode: ['', [Validators.required, Validators.pattern(/^[A-Za-z0-9\s\-]{3,10}$/)]],
//       country: ['India', Validators.required],
//       gstNumber: [''],
//       panNumber: [''],
//       cinNumber: [''],
//       website: [''],
//       locations: this.fb.array([], Validators.minLength(1)),
//       departments: this.fb.array([]),
//       users: this.fb.array([])
//     });

//     if (!this.buyerForm.get('locations')?.value?.length) {
//       this.addLocation();
//     }
//   }

//   // ======================================================================
//   // HQ: COUNTRY → STATE → CITY (+ Others) → POSTAL
//   // ======================================================================

//   onHQCountryChange(event: any): void {
//     const country = event.target.value;
//     this.hqStates = getStatesForCountry(country);
//     this.hqCities = [];
//     this.hqCityIsOthers = false;
//     this.buyerForm.get('otherCity')?.clearValidators();
//     this.buyerForm.get('otherCity')?.setValue('');
//     this.buyerForm.get('otherCity')?.updateValueAndValidity();
//     this.buyerForm.patchValue({ state: '', city: '', postalCode: '' });
//   }

//   onHQStateChange(event: any): void {
//     const country = this.buyerForm.get('country')?.value;
//     const state = event.target.value;
//     this.hqCities = getCityNamesForState(country, state);
//     this.hqCityIsOthers = false;
//     this.buyerForm.get('otherCity')?.clearValidators();
//     this.buyerForm.get('otherCity')?.setValue('');
//     this.buyerForm.get('otherCity')?.updateValueAndValidity();
//     this.buyerForm.patchValue({ city: '', postalCode: '' });
//   }

//   onHQCityChange(event: any): void {
//     const selected = event.target.value;
//     if (selected === '__others__') {
//       // ✅ Show custom city input — postal must be typed manually
//       this.hqCityIsOthers = true;
//       this.buyerForm.patchValue({ city: '__others__', otherCity: '', postalCode: '' });
//       this.buyerForm.get('otherCity')?.setValidators([Validators.required, Validators.minLength(2)]);
//       this.buyerForm.get('otherCity')?.updateValueAndValidity();
//     } else {
//       this.hqCityIsOthers = false;
//       this.buyerForm.get('otherCity')?.clearValidators();
//       this.buyerForm.get('otherCity')?.setValue('');
//       this.buyerForm.get('otherCity')?.updateValueAndValidity();
//       const country = this.buyerForm.get('country')?.value;
//       const state = this.buyerForm.get('state')?.value;
//       const postal = getPostalCodeForCity(country, state, selected);
//       this.buyerForm.patchValue({ city: selected, postalCode: postal || '' });
//     }
//   }

//   // ======================================================================
//   // LOCATION: COUNTRY → STATE → CITY (+ Others) → POSTAL
//   // ======================================================================

//   onLocationCountryChange(event: any, idx: number): void {
//     const country = event.target.value;
//     this.locationStates[idx] = getStatesForCountry(country);
//     this.locationCities[idx] = [];
//     this.locationCityIsOthers[idx] = false;
//     const loc = this.locations.at(idx);
//     loc.get('otherCity')?.clearValidators();
//     loc.get('otherCity')?.setValue('');
//     loc.get('otherCity')?.updateValueAndValidity();
//     loc.patchValue({ state: '', city: '', postalCode: '' });
//   }

//   onLocationStateChange(event: any, idx: number): void {
//     const country = this.locations.at(idx).get('country')?.value;
//     const state = event.target.value;
//     this.locationCities[idx] = getCityNamesForState(country, state);
//     this.locationCityIsOthers[idx] = false;
//     const loc = this.locations.at(idx);
//     loc.get('otherCity')?.clearValidators();
//     loc.get('otherCity')?.setValue('');
//     loc.get('otherCity')?.updateValueAndValidity();
//     loc.patchValue({ city: '', postalCode: '' });
//   }

//   onLocationCityChange(event: any, idx: number): void {
//     const selected = event.target.value;
//     const loc = this.locations.at(idx);
//     if (selected === '__others__') {
//       this.locationCityIsOthers[idx] = true;
//       loc.patchValue({ city: '__others__', otherCity: '', postalCode: '' });
//       loc.get('otherCity')?.setValidators([Validators.required, Validators.minLength(2)]);
//       loc.get('otherCity')?.updateValueAndValidity();
//     } else {
//       this.locationCityIsOthers[idx] = false;
//       loc.get('otherCity')?.clearValidators();
//       loc.get('otherCity')?.setValue('');
//       loc.get('otherCity')?.updateValueAndValidity();
//       const country = loc.get('country')?.value;
//       const state = loc.get('state')?.value;
//       const postal = getPostalCodeForCity(country, state, selected);
//       loc.patchValue({ city: selected, postalCode: postal || '' });
//     }
//   }

//   getStatesForLocation(idx: number): string[] { return this.locationStates[idx] || []; }
//   getCitiesForLocation(idx: number): string[] { return this.locationCities[idx] || []; }
//   isLocationCityOthers(idx: number): boolean { return this.locationCityIsOthers[idx] || false; }

//   // ======================================================================
//   // USER: STATE → CITY (+ Others) → POSTAL
//   // ======================================================================

//   onUserStateChange(event: any, userIdx: number): void {
//     const userGroup = this.users.at(userIdx);
//     const country = userGroup.get('country')?.value || 'India';
//     const state = event.target.value;
//     this.userCities[userIdx] = getCityNamesForState(country, state);
//     this.userCityIsOthers[userIdx] = false;
//     userGroup.get('otherCity')?.clearValidators();
//     userGroup.get('otherCity')?.setValue('');
//     userGroup.get('otherCity')?.updateValueAndValidity();
//     userGroup.patchValue({ city: '', postalCode: '' });
//   }

//   onUserCityChange(event: any, userIdx: number): void {
//     const selected = event.target.value;
//     const userGroup = this.users.at(userIdx);
//     if (selected === '__others__') {
//       this.userCityIsOthers[userIdx] = true;
//       userGroup.patchValue({ city: '__others__', otherCity: '', postalCode: '' });
//       userGroup.get('otherCity')?.setValidators([Validators.required, Validators.minLength(2)]);
//       userGroup.get('otherCity')?.updateValueAndValidity();
//     } else {
//       this.userCityIsOthers[userIdx] = false;
//       userGroup.get('otherCity')?.clearValidators();
//       userGroup.get('otherCity')?.setValue('');
//       userGroup.get('otherCity')?.updateValueAndValidity();
//       const country = userGroup.get('country')?.value || 'India';
//       const state = userGroup.get('state')?.value || '';
//       const postal = getPostalCodeForCity(country, state, selected);
//       userGroup.patchValue({ city: selected, postalCode: postal || '' });
//     }
//   }

//   getUserCities(userIdx: number): string[] { return this.userCities[userIdx] || []; }
//   isUserCityOthers(userIdx: number): boolean { return this.userCityIsOthers[userIdx] || false; }

//   getStatesForCountryDynamic(countryName: string): string[] {
//     if (!countryName?.trim()) return [];
//     return getStatesForCountry(countryName);
//   }

//   // ======================================================================
//   // CURRENCY
//   // ======================================================================

//   getCurrencySymbolForCode(code: string): string {
//     const found = this.worldCurrencies.find(c => c.code === code);
//     return found ? found.symbol : '₹';
//   }

//   onCurrencyChange(event: any, locationIndex: number): void {
//     const symbol = this.getCurrencySymbolForCode(event.target.value);
//     (this.locations.at(locationIndex) as FormGroup).patchValue({ currencySymbol: symbol });
//   }

//   // ======================================================================
//   // LOGO
//   // ======================================================================

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
//     this.buyerForm.patchValue({ logo: null });
//     const fileInput = document.querySelector('input[type="file"]') as HTMLInputElement;
//     if (fileInput) fileInput.value = '';
//   }

//   private checkEditMode(): void {
//     this.route.paramMap.subscribe(params => {
//       const id = params.get('id');
//       if (id) {
//         this.mode = 'edit';
//         this.buyerId = +id;
//         this.currentStep = 1;
//         this.loadBuyerData(this.buyerId);
//       } else {
//         this.mode = 'create';
//         this.currentStep = 1;
//       }
//     });
//   }

//   get locations(): FormArray { return this.buyerForm.get('locations') as FormArray; }
//   get departments(): FormArray { return this.buyerForm.get('departments') as FormArray; }
//   get users(): FormArray { return this.buyerForm.get('users') as FormArray; }

//   // ======================================================================
//   // LOCATION FORM ARRAY
//   // ======================================================================

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
//       otherCity: [''],               // ✅ custom city per location
//       state: ['', Validators.required],
//       postalCode: ['', [Validators.required, Validators.pattern(/^[A-Za-z0-9\s\-]{3,10}$/)]],
//       country: ['India', Validators.required],
//       landlineNumber: [''],
//       faxNumber: [''],
//       currencyCode: ['INR', Validators.required],
//       currencySymbol: ['₹']
//     });

//     const newIndex = this.locations.length;
//     this.locations.push(locationGroup);
//     this.locationStates[newIndex] = getStatesForCountry('India');
//     this.locationCities[newIndex] = [];
//     this.locationCityIsOthers[newIndex] = false;
//   }

//   removeLocation(index: number): void {
//     if (this.locations.length > 1) {
//       this.locations.removeAt(index);
//       this.locationStates.splice(index, 1);
//       this.locationCities.splice(index, 1);
//       this.locationCityIsOthers.splice(index, 1);
//     } else {
//       this.messageService.showMessage('warning', 'Warning', 'At least one location is required');
//     }
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

//   // ======================================================================
//   // DEPARTMENT FORM ARRAY
//   // ======================================================================

//   addDepartment(): void {
//     const departmentGroup = this.fb.group({
//       id: [null],
//       locationIndex: ['', Validators.required],
//       departmentName: ['', [Validators.required, Validators.minLength(2)]],
//       departmentDescription: ['']
//     });
//     this.departments.push(departmentGroup);
//   }

//   removeDepartment(deptIndex: number): void {
//     if (this.departments.length > 1) {
//       this.departments.removeAt(deptIndex);
//     } else {
//       this.messageService.showMessage('warning', 'Warning', 'At least one department is required');
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

//   // ======================================================================
//   // USER FORM ARRAY
//   // ======================================================================

//   addUser(userData?: any): void {
//     const userGroup = this.fb.group({
//       id: [userData?.id || null],
//       departmentIndex: [userData?.departmentIndex ?? '', Validators.required],
//       firstName: [userData?.firstName || '', [Validators.required, Validators.minLength(2)]],
//       lastName: [userData?.lastName || '', [Validators.required, Validators.minLength(1)]],
//       email: [userData?.email || '', [Validators.required, Validators.email]],
//       phone: [userData?.phone || '', [Validators.required, Validators.pattern(/^[0-9]{10}$/)]],
//       designation: [userData?.designation || '', Validators.required],
//       employeeId: [userData?.employeeId || '', Validators.required],
//       gender: [userData?.gender || ''],
//       dateOfBirth: [userData?.dateOfBirth || ''],
//       addressLine1: [userData?.addressLine1 || ''],
//       addressLine2: [userData?.addressLine2 || ''],
//       country: [userData?.country || 'India'],
//       state: [userData?.state || ''],
//       city: [userData?.city || ''],
//       otherCity: [''],               // ✅ custom city per user
//       postalCode: [userData?.postalCode || ''],
//       password: [userData?.password || '', [Validators.required, Validators.minLength(6)]]
//     });

//     const newIndex = this.users.length;
//     this.users.push(userGroup);
//     this.userCities[newIndex] = [];
//     this.userCityIsOthers[newIndex] = false;
//   }

//   removeUser(userIndex: number): void {
//     if (this.users.length > 1) {
//       this.users.removeAt(userIndex);
//       this.userCities.splice(userIndex, 1);
//       this.userCityIsOthers.splice(userIndex, 1);
//     } else {
//       this.messageService.showMessage('warning', 'Warning', 'At least one user is required');
//     }
//   }

//   onCompanyTypeChange(event: any): void {
//     const customTypeControl = this.buyerForm.get('otherCompanyType');
//     if (event.target.value === 'Others') {
//       customTypeControl?.setValidators([Validators.required, Validators.minLength(2)]);
//       customTypeControl?.setValue('');
//     } else {
//       customTypeControl?.clearValidators();
//       customTypeControl?.setValue(null);
//     }
//     customTypeControl?.updateValueAndValidity();
//   }

//   // ======================================================================
//   // EDIT MODE — LOAD BUYER DATA
//   // ======================================================================

//   private loadBuyerData(id: number): void {
//     this.buyerService.getBuyerById(id).subscribe({
//       next: (buyer: any) => {
//         let companyTypeForForm = buyer.companyType;
//         let otherCompanyType = null;
//         if (buyer.companyType && !this.companyTypes.includes(buyer.companyType)) {
//           companyTypeForForm = 'Others';
//           otherCompanyType = buyer.companyType;
//         }
//         this.buyerForm.patchValue({
//           companyName: buyer.companyName,
//           companyType: companyTypeForForm,
//           otherCompanyType: otherCompanyType,
//           contactPersonName: buyer.contactPersonName,
//           contactPersonDesignation: buyer.contactPersonDesignation,
//           contactPersonEmail: buyer.contactPersonEmail,
//           contactPersonPhone: buyer.contactPersonPhone,
//           addressLine1: buyer.addressLine1,
//           addressLine2: buyer.addressLine2,
//           state: buyer.state,
//           postalCode: buyer.postalCode,
//           country: buyer.country,
//           gstNumber: buyer.gstNumber,
//           panNumber: buyer.panNumber,
//           cinNumber: buyer.cinNumber,
//           website: buyer.website
//         });

//         if (buyer.country) this.hqStates = getStatesForCountry(buyer.country);
//         if (buyer.country && buyer.state) {
//           this.hqCities = getCityNamesForState(buyer.country, buyer.state);
//         }

//         // ✅ If city not in known list → treat as Others
//         const knownCities = this.hqCities;
//         if (buyer.city && knownCities.length > 0 && !knownCities.includes(buyer.city)) {
//           this.hqCityIsOthers = true;
//           this.buyerForm.patchValue({ city: '__others__', otherCity: buyer.city });
//           this.buyerForm.get('otherCity')?.setValidators([Validators.required, Validators.minLength(2)]);
//           this.buyerForm.get('otherCity')?.updateValueAndValidity();
//         } else {
//           this.hqCityIsOthers = false;
//           this.buyerForm.patchValue({ city: buyer.city });
//         }

//         this.onCompanyTypeChange({ target: { value: companyTypeForForm } });

//         if (buyer.id) {
//           this.buyerService.getBuyerLogoBase64(buyer.id).subscribe({
//             next: (logoData: any) => { if (logoData) this.logoPreview = logoData; },
//             error: () => {}
//           });
//         }

//         while (this.locations.length) this.locations.removeAt(0);
//         while (this.departments.length) this.departments.removeAt(0);
//         while (this.users.length) this.users.removeAt(0);
//         this.locationStates = [];
//         this.locationCities = [];
//         this.locationCityIsOthers = [];
//         this.userCities = [];
//         this.userCityIsOthers = [];

//         if (buyer.locations && Array.isArray(buyer.locations)) {
//           this.populateLocations(buyer.locations);
//         }
//       },
//       error: () => {
//         this.messageService.showMessage('error', 'Error', 'Failed to load buyer data');
//         setTimeout(() => this.router.navigate(['/dashboard']), 2000);
//       }
//     });
//   }

//   private populateLocations(locations: Location[]): void {
//     locations.forEach((loc: any, locIndex: number) => {
//       this.addLocation();

//       let locationTypeForForm = loc.locationType;
//       let otherLocationType = null;
//       if (loc.locationType && !this.locationTypes.includes(loc.locationType)) {
//         locationTypeForForm = 'Others';
//         otherLocationType = loc.locationType;
//       }

//       if (loc.country) this.locationStates[locIndex] = getStatesForCountry(loc.country);
//       if (loc.country && loc.state) {
//         this.locationCities[locIndex] = getCityNamesForState(loc.country, loc.state);
//       }

//       // ✅ Detect custom city
//       const knownCities = this.locationCities[locIndex] || [];
//       let cityValue = loc.city;
//       let otherCityValue = '';
//       if (loc.city && knownCities.length > 0 && !knownCities.includes(loc.city)) {
//         this.locationCityIsOthers[locIndex] = true;
//         cityValue = '__others__';
//         otherCityValue = loc.city;
//       } else {
//         this.locationCityIsOthers[locIndex] = false;
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
//         city: cityValue,
//         otherCity: otherCityValue,
//         state: loc.state,
//         postalCode: loc.postalCode,
//         country: loc.country,
//         landlineNumber: loc.landlineNumber,
//         faxNumber: loc.faxNumber,
//         currencyCode: loc.currencyCode || 'INR',
//         currencySymbol: loc.currencySymbol || '₹'
//       });

//       if (this.locationCityIsOthers[locIndex]) {
//         this.locations.at(locIndex).get('otherCity')
//           ?.setValidators([Validators.required, Validators.minLength(2)]);
//         this.locations.at(locIndex).get('otherCity')?.updateValueAndValidity();
//       }

//       this.onLocationTypeChange({ target: { value: locationTypeForForm } }, locIndex);

//       if (loc.departments && loc.departments.length > 0) {
//         loc.departments.forEach((dept: any) => {
//           const deptIndex = this.departments.length;
//           this.addDepartment();
//           this.departments.at(deptIndex).patchValue({
//             id: dept.id,
//             locationIndex: locIndex,
//             departmentName: dept.departmentName,
//             departmentDescription: dept.departmentDescription
//           });

//           if (dept.users && dept.users.length > 0) {
//             dept.users.forEach((user: any) => {
//               const userIdx = this.users.length;
//               this.addUser({
//                 id: user.id,
//                 departmentIndex: deptIndex,
//                 firstName: user.firstName, lastName: user.lastName,
//                 email: user.email, phone: user.phone,
//                 designation: user.designation, employeeId: user.employeeId,
//                 gender: user.gender, dateOfBirth: user.dateOfBirth,
//                 addressLine1: user.addressLine1, addressLine2: user.addressLine2,
//                 country: user.country || 'India',
//                 state: user.state, city: user.city,
//                 postalCode: user.postalCode, password: ''
//               });

//               if (user.state) {
//                 const uCountry = user.country || 'India';
//                 this.userCities[userIdx] = getCityNamesForState(uCountry, user.state);
//                 const knownUserCities = this.userCities[userIdx];
//                 if (user.city && knownUserCities.length > 0 && !knownUserCities.includes(user.city)) {
//                   this.userCityIsOthers[userIdx] = true;
//                   this.users.at(userIdx).patchValue({ city: '__others__', otherCity: user.city });
//                   this.users.at(userIdx).get('otherCity')
//                     ?.setValidators([Validators.required, Validators.minLength(2)]);
//                   this.users.at(userIdx).get('otherCity')?.updateValueAndValidity();
//                 }
//               }
//             });
//           }
//         });
//       }
//     });
//   }

//   // ======================================================================
//   // STEP VALIDATION
//   // ======================================================================

//   private isCurrentStepValid(): boolean {
//     switch (this.currentStep) {
//       case 1: return this.isBuyerDetailsValid();
//       case 2: return this.locationsBasicsValid();
//       case 3: return this.departmentsValid();
//       case 4: return this.usersValid();
//       default: return false;
//     }
//   }

//   private isBuyerDetailsValid(): boolean {
//     const fields = ['companyName', 'companyType', 'contactPersonName', 'contactPersonDesignation',
//       'contactPersonEmail', 'contactPersonPhone', 'addressLine1', 'city', 'state',
//       'postalCode', 'country'];
//     const standardValid = fields.every(f => this.buyerForm.get(f)?.valid);
//     const otherCompanyValid: boolean = this.buyerForm.get('companyType')?.value === 'Others'
//       ? (this.buyerForm.get('otherCompanyType')?.valid ?? false) : true;
//     const otherCityValid: boolean = this.hqCityIsOthers
//       ? (this.buyerForm.get('otherCity')?.valid ?? false) : true;
//     return standardValid && otherCompanyValid && otherCityValid;
//   }

//   private locationsBasicsValid(): boolean {
//     return this.locations.length > 0 &&
//       this.locations.controls.every((loc, idx) => {
//         const fields = ['locationName', 'locationType', 'locationContactName',
//           'locationContactEmail', 'locationContactPhone', 'addressLine1',
//           'city', 'state', 'postalCode', 'country', 'currencyCode'];
//         const standardValid = fields.every(f => loc.get(f)?.valid);
//         const otherTypeValid: boolean = loc.get('locationType')?.value === 'Others'
//           ? (loc.get('otherLocationType')?.valid ?? false) : true;
//         const otherCityValid: boolean = this.locationCityIsOthers[idx]
//           ? (loc.get('otherCity')?.valid ?? false) : true;
//         return standardValid && otherTypeValid && otherCityValid;
//       });
//   }

//   private departmentsValid(): boolean {
//     return this.departments.length > 0 && this.departments.controls.every(dept =>
//       dept.get('departmentName')?.valid && dept.get('locationIndex')?.valid
//     );
//   }

//   private usersValid(): boolean {
//     return this.users.length > 0 && this.users.controls.every(user =>
//       user.get('firstName')?.valid && user.get('lastName')?.valid &&
//       user.get('email')?.valid && user.get('phone')?.valid &&
//       user.get('designation')?.valid && user.get('employeeId')?.valid &&
//       user.get('departmentIndex')?.valid && user.get('password')?.valid
//     );
//   }

//   saveStep(): void {
//     if (!this.isCurrentStepValid()) {
//       this.messageService.showMessage('warning', 'Validation Error', 'Please fill all required fields on this step');
//       this.buyerForm.markAllAsTouched();
//       return;
//     }
//     this.messageService.showMessage('success', 'Saved', `Step ${this.currentStep} saved successfully`);
//     if (this.currentStep < this.maxStep) this.currentStep++;
//   }

//   prevStep(): void { if (this.currentStep > 1) this.currentStep--; }
//   nextStep(): void { if (this.currentStep < this.maxStep) this.currentStep++; }

//   // ======================================================================
//   // SUBMIT — resolve all "Others" values before sending to API
//   // ======================================================================

//   async onSubmit(): Promise<void> {
//     if (this.buyerForm.invalid) {
//       this.buyerForm.markAllAsTouched();
//       this.messageService.showMessage('error', 'Validation Error', 'Please fill all required fields');
//       return;
//     }
//     if (this.isOrgAdminMode && !this.orgAdminId) {
//       this.messageService.showMessage('error', 'Error', 'Organization Admin ID not found. Please login again.');
//       return;
//     }

//     this.isSubmitting = true;
//     const formValue = this.buyerForm.getRawValue();

//     // ✅ Resolve HQ Others city
//     if (formValue.city === '__others__' && formValue.otherCity) {
//       formValue.city = formValue.otherCity;
//     }
//     delete formValue.otherCity;

//     if (formValue.companyType === 'Others' && formValue.otherCompanyType) {
//       formValue.companyType = formValue.otherCompanyType;
//     }
//     delete formValue.otherCompanyType;

//     const processedLocations: any[] = [];
//     if (formValue.locations && Array.isArray(formValue.locations)) {
//       formValue.locations.forEach((loc: any) => {
//         // ✅ Resolve location Others city
//         if (loc.city === '__others__' && loc.otherCity) loc.city = loc.otherCity;
//         delete loc.otherCity;
//         if (loc.locationType === 'Others' && loc.otherLocationType) loc.locationType = loc.otherLocationType;
//         delete loc.otherLocationType;
//         loc.departments = [];
//         processedLocations.push(loc);
//       });
//     }

//     if (formValue.departments && Array.isArray(formValue.departments)) {
//       formValue.departments.forEach((dept: any) => {
//         const locationIndex = parseInt(dept.locationIndex, 10);
//         if (!isNaN(locationIndex) && locationIndex >= 0 && locationIndex < processedLocations.length) {
//           const { locationIndex: _, ...deptData } = dept;
//           deptData.users = [];
//           processedLocations[locationIndex].departments.push(deptData);
//         }
//       });
//     }

//     if (formValue.users && Array.isArray(formValue.users)) {
//       formValue.users.forEach((user: any) => {
//         // ✅ Resolve user Others city
//         if (user.city === '__others__' && user.otherCity) user.city = user.otherCity;
//         delete user.otherCity;

//         const deptIdx = parseInt(user.departmentIndex, 10);
//         let targetDepartment: any = null;
//         let counter = 0;
//         for (let location of processedLocations) {
//           for (let dept of location.departments) {
//             if (counter === deptIdx) { targetDepartment = dept; break; }
//             counter++;
//           }
//           if (targetDepartment) break;
//         }
//         if (targetDepartment) {
//           const { departmentIndex: _, country: _c, ...userData } = user;
//           if (this.mode === 'edit' && (!userData.password || !userData.password.trim())) {
//             delete userData.password;
//           }
//           targetDepartment.users.push(userData);
//         }
//       });
//     }

//     const payload: any = { ...formValue, locations: processedLocations };
//     delete payload.departments;
//     delete payload.users;
//     delete payload.logo;

//     if (this.selectedLogoFile) {
//       try {
//         const logoBase64 = await this.buyerService.convertFileToBase64(this.selectedLogoFile);
//         payload.logoBase64 = logoBase64;
//         payload.logoFilename = this.selectedLogoFile.name;
//         payload.logoContentType = this.selectedLogoFile.type;
//       } catch (error) {
//         this.messageService.showMessage('error', 'Error', 'Failed to process logo');
//         this.isSubmitting = false;
//         return;
//       }
//     }

//     if (this.isOrgAdminMode) payload.organizationCompanyName = this.orgAdminCompanyName;

//     if (this.mode === 'create') {
//       this.buyerService.createCompleteHierarchy(
//         payload,
//         this.isOrgAdminMode ? this.orgAdminId! : undefined
//       ).subscribe({
//         next: () => {
//           this.messageService.showMessage('success', 'Success',
//             this.isOrgAdminMode ? 'Buyer created and linked to your organization' : 'Buyer created successfully');
//           this.isSubmitting = false;
//           setTimeout(() => this.router.navigate(['/dashboard']), 1500);
//         },
//         error: (err: any) => {
//           this.messageService.showMessage('error', 'Error', err.error?.message || err.message || 'Failed to create buyer');
//           this.isSubmitting = false;
//         }
//       });
//     } else if (this.buyerId) {
//       this.buyerService.updateCompleteHierarchy(this.buyerId, payload).subscribe({
//         next: () => {
//           this.messageService.showMessage('success', 'Success', 'Buyer updated');
//           this.isSubmitting = false;
//           setTimeout(() => this.router.navigate(['/dashboard']), 1500);
//         },
//         error: (err: any) => {
//           this.messageService.showMessage('error', 'Error', err.error?.message || err.message || 'Update failed');
//           this.isSubmitting = false;
//         }
//       });
//     }
//   }
// }

// create-b.component.ts
// MODIFIED:
// - Removed selectedLogoFile, logoPreview, logo-related fields and methods.
// - Removed onLogoSelected(), removeLogo(), convertFileToBase64() calls.
// - onSubmit(): no longer attaches logo to payload — payload has no logo fields.
// - initializeForm(): removed 'logo' form control.
// - Step 1 no longer shows the logo upload section.



// create-b.component.ts
// FIXED:
// - companyName has NO validators (hidden field, auto-set from OrgAdmin at submit time)
// - isBuyerDetailsValid() does NOT check companyName
// - onSubmit() sets companyName = orgAdminCompanyName BEFORE validity check
// - Safety net: if companyName still empty after above, fallback to orgAdminCompanyName

import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, FormArray } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { BuyerService } from '../dashboard/buyer-b.service';
import { MessageService } from '../../../shared/service/message.service';
import { Buyer, Location, Department, User } from '../dashboard/buyer-b.model';

import {
  getAllCountryNames,
  getStatesForCountry,
  getCityNamesForState,
  getPostalCodeForCity,
  CityData
} from '../../../shared/data/country-state.data';

export const WORLD_CURRENCIES = [
  { code: 'AFN', symbol: '؋', name: 'Afghan Afghani' },
  { code: 'ALL', symbol: 'L', name: 'Albanian Lek' },
  { code: 'DZD', symbol: 'د.ج', name: 'Algerian Dinar' },
  { code: 'ARS', symbol: '$', name: 'Argentine Peso' },
  { code: 'AMD', symbol: '֏', name: 'Armenian Dram' },
  { code: 'AUD', symbol: 'A$', name: 'Australian Dollar' },
  { code: 'AZN', symbol: '₼', name: 'Azerbaijani Manat' },
  { code: 'BHD', symbol: '.د.ب', name: 'Bahraini Dinar' },
  { code: 'BDT', symbol: '৳', name: 'Bangladeshi Taka' },
  { code: 'BRL', symbol: 'R$', name: 'Brazilian Real' },
  { code: 'GBP', symbol: '£', name: 'British Pound Sterling' },
  { code: 'CAD', symbol: 'C$', name: 'Canadian Dollar' },
  { code: 'CLP', symbol: '$', name: 'Chilean Peso' },
  { code: 'CNY', symbol: '¥', name: 'Chinese Yuan' },
  { code: 'COP', symbol: '$', name: 'Colombian Peso' },
  { code: 'CZK', symbol: 'Kč', name: 'Czech Koruna' },
  { code: 'DKK', symbol: 'kr', name: 'Danish Krone' },
  { code: 'EGP', symbol: '£', name: 'Egyptian Pound' },
  { code: 'ETB', symbol: 'Br', name: 'Ethiopian Birr' },
  { code: 'EUR', symbol: '€', name: 'Euro' },
  { code: 'GEL', symbol: '₾', name: 'Georgian Lari' },
  { code: 'GHS', symbol: '₵', name: 'Ghanaian Cedi' },
  { code: 'HUF', symbol: 'Ft', name: 'Hungarian Forint' },
  { code: 'INR', symbol: '₹', name: 'Indian Rupee' },
  { code: 'IDR', symbol: 'Rp', name: 'Indonesian Rupiah' },
  { code: 'IRR', symbol: '﷼', name: 'Iranian Rial' },
  { code: 'IQD', symbol: 'ع.د', name: 'Iraqi Dinar' },
  { code: 'ILS', symbol: '₪', name: 'Israeli New Shekel' },
  { code: 'JPY', symbol: '¥', name: 'Japanese Yen' },
  { code: 'JOD', symbol: 'د.ا', name: 'Jordanian Dinar' },
  { code: 'KZT', symbol: '₸', name: 'Kazakhstani Tenge' },
  { code: 'KES', symbol: 'KSh', name: 'Kenyan Shilling' },
  { code: 'KWD', symbol: 'د.ك', name: 'Kuwaiti Dinar' },
  { code: 'MYR', symbol: 'RM', name: 'Malaysian Ringgit' },
  { code: 'MXN', symbol: '$', name: 'Mexican Peso' },
  { code: 'MAD', symbol: 'د.م.', name: 'Moroccan Dirham' },
  { code: 'NPR', symbol: '₨', name: 'Nepalese Rupee' },
  { code: 'NZD', symbol: 'NZ$', name: 'New Zealand Dollar' },
  { code: 'NGN', symbol: '₦', name: 'Nigerian Naira' },
  { code: 'NOK', symbol: 'kr', name: 'Norwegian Krone' },
  { code: 'OMR', symbol: 'ر.ع.', name: 'Omani Rial' },
  { code: 'PKR', symbol: '₨', name: 'Pakistani Rupee' },
  { code: 'PHP', symbol: '₱', name: 'Philippine Peso' },
  { code: 'PLN', symbol: 'zł', name: 'Polish Złoty' },
  { code: 'QAR', symbol: 'ر.ق', name: 'Qatari Riyal' },
  { code: 'RON', symbol: 'lei', name: 'Romanian Leu' },
  { code: 'RUB', symbol: '₽', name: 'Russian Ruble' },
  { code: 'SAR', symbol: 'ر.س', name: 'Saudi Riyal' },
  { code: 'SGD', symbol: 'S$', name: 'Singapore Dollar' },
  { code: 'ZAR', symbol: 'R', name: 'South African Rand' },
  { code: 'KRW', symbol: '₩', name: 'South Korean Won' },
  { code: 'LKR', symbol: '₨', name: 'Sri Lankan Rupee' },
  { code: 'SEK', symbol: 'kr', name: 'Swedish Krona' },
  { code: 'CHF', symbol: 'Fr', name: 'Swiss Franc' },
  { code: 'TWD', symbol: 'NT$', name: 'New Taiwan Dollar' },
  { code: 'THB', symbol: '฿', name: 'Thai Baht' },
  { code: 'TRY', symbol: '₺', name: 'Turkish Lira' },
  { code: 'AED', symbol: 'د.إ', name: 'UAE Dirham' },
  { code: 'USD', symbol: '$', name: 'US Dollar' },
  { code: 'UZS', symbol: 'лв', name: 'Uzbekistani Som' },
  { code: 'VND', symbol: '₫', name: 'Vietnamese Đồng' },
  { code: 'ZMW', symbol: 'ZK', name: 'Zambian Kwacha' },
];

@Component({
  selector: 'app-create-b',
  templateUrl: './create-b.component.html',
  styleUrls: ['./create-b.component.css'],
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule]
})
export class CreateBComponent implements OnInit {

  buyerForm!: FormGroup;
  currentStep = 1;
  maxStep = 4;
  isSubmitting = false;

  mode: 'create' | 'edit' = 'create';
  buyerId: number | null = null;

  isOrgAdminMode = false;
  orgAdminId: number | null = null;
  orgAdminCompanyName: string = '';

  worldCurrencies = WORLD_CURRENCIES;
  allCountries: string[] = getAllCountryNames();

  // HQ cascades
  hqStates: string[] = [];
  hqCities: string[] = [];
  hqCityIsOthers = false;

  // Per-location cascades
  locationStates: string[][] = [];
  locationCities: string[][] = [];
  locationCityIsOthers: boolean[] = [];

  // Per-user cascades
  userCities: string[][] = [];
  userCityIsOthers: boolean[] = [];

  companyTypes = ['Manufacturing', 'Trading', 'Services', 'Distribution', 'Retail', 'Others'];
  locationTypes = ['Branch', 'Regional', 'Warehouse', 'Service Center', 'Others'];

  email: string = '';
  companyName: string = '';
  fullName: string = '';

  constructor(
    private fb: FormBuilder,
    private buyerService: BuyerService,
    private messageService: MessageService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.checkOrgAdminMode();
    this.loadUserHeaderInfo();
    this.initializeForm();
    this.checkEditMode();
    this.hqStates = getStatesForCountry('India');
  }

  private loadUserHeaderInfo(): void {
    this.email = localStorage.getItem('email') || 'admin@company.com';
    this.companyName = localStorage.getItem('companyName') || 'Company Name';
    this.fullName = localStorage.getItem('fullName') || 'Admin';
  }

  private checkOrgAdminMode(): void {
    const role = localStorage.getItem('role');
    if (role === 'ORGANIZATION_ADMIN') {
      this.isOrgAdminMode = true;
      this.orgAdminId = Number(localStorage.getItem('userId'));
      this.orgAdminCompanyName = localStorage.getItem('companyName') || '';
    }
  }

  private initializeForm(): void {
    // ✅ FIX: companyName has NO validators — it is hidden from the buyer creation form.
    // It is auto-populated from orgAdminCompanyName in onSubmit() before sending to backend.
    // The backend also has a safety-net fallback in BuyerService.java.
    this.buyerForm = this.fb.group({
      companyName: [''],   // ← NO Validators.required — field is hidden, set programmatically
      companyType: ['', Validators.required],
      otherCompanyType: [''],
      contactPersonName: ['', [Validators.required, Validators.minLength(2)]],
      contactPersonDesignation: ['', Validators.required],
      contactPersonEmail: ['', [Validators.required, Validators.email]],
      contactPersonPhone: ['', [Validators.required, Validators.pattern(/^[0-9]{10}$/)]],
      addressLine1: ['', [Validators.required, Validators.minLength(5)]],
      addressLine2: [''],
      city: ['', Validators.required],
      otherCity: [''],
      state: ['', Validators.required],
      postalCode: ['', [Validators.required, Validators.pattern(/^[A-Za-z0-9\s\-]{3,10}$/)]],
      country: ['India', Validators.required],
      gstNumber: [''],
      panNumber: [''],
      cinNumber: [''],
      website: [''],
      locations: this.fb.array([], Validators.minLength(1)),
      departments: this.fb.array([]),
      users: this.fb.array([])
    });

    if (!this.buyerForm.get('locations')?.value?.length) {
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
    this.buyerForm.get('otherCity')?.clearValidators();
    this.buyerForm.get('otherCity')?.setValue('');
    this.buyerForm.get('otherCity')?.updateValueAndValidity();
    this.buyerForm.patchValue({ state: '', city: '', postalCode: '' });
  }

  onHQStateChange(event: any): void {
    const country = this.buyerForm.get('country')?.value;
    const state = event.target.value;
    this.hqCities = getCityNamesForState(country, state);
    this.hqCityIsOthers = false;
    this.buyerForm.get('otherCity')?.clearValidators();
    this.buyerForm.get('otherCity')?.setValue('');
    this.buyerForm.get('otherCity')?.updateValueAndValidity();
    this.buyerForm.patchValue({ city: '', postalCode: '' });
  }

  onHQCityChange(event: any): void {
    const selected = event.target.value;
    if (selected === '__others__') {
      this.hqCityIsOthers = true;
      this.buyerForm.patchValue({ city: '__others__', otherCity: '', postalCode: '' });
      this.buyerForm.get('otherCity')?.setValidators([Validators.required, Validators.minLength(2)]);
      this.buyerForm.get('otherCity')?.updateValueAndValidity();
    } else {
      this.hqCityIsOthers = false;
      this.buyerForm.get('otherCity')?.clearValidators();
      this.buyerForm.get('otherCity')?.setValue('');
      this.buyerForm.get('otherCity')?.updateValueAndValidity();
      const country = this.buyerForm.get('country')?.value;
      const state = this.buyerForm.get('state')?.value;
      const postal = getPostalCodeForCity(country, state, selected);
      this.buyerForm.patchValue({ city: selected, postalCode: postal || '' });
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
  // USER: STATE → CITY (+ Others) → POSTAL
  // ======================================================================

  onUserStateChange(event: any, userIdx: number): void {
    const userGroup = this.users.at(userIdx);
    const country = userGroup.get('country')?.value || 'India';
    const state = event.target.value;
    this.userCities[userIdx] = getCityNamesForState(country, state);
    this.userCityIsOthers[userIdx] = false;
    userGroup.get('otherCity')?.clearValidators();
    userGroup.get('otherCity')?.setValue('');
    userGroup.get('otherCity')?.updateValueAndValidity();
    userGroup.patchValue({ city: '', postalCode: '' });
  }

  onUserCityChange(event: any, userIdx: number): void {
    const selected = event.target.value;
    const userGroup = this.users.at(userIdx);
    if (selected === '__others__') {
      this.userCityIsOthers[userIdx] = true;
      userGroup.patchValue({ city: '__others__', otherCity: '', postalCode: '' });
      userGroup.get('otherCity')?.setValidators([Validators.required, Validators.minLength(2)]);
      userGroup.get('otherCity')?.updateValueAndValidity();
    } else {
      this.userCityIsOthers[userIdx] = false;
      userGroup.get('otherCity')?.clearValidators();
      userGroup.get('otherCity')?.setValue('');
      userGroup.get('otherCity')?.updateValueAndValidity();
      const country = userGroup.get('country')?.value || 'India';
      const state = userGroup.get('state')?.value || '';
      const postal = getPostalCodeForCity(country, state, selected);
      userGroup.patchValue({ city: selected, postalCode: postal || '' });
    }
  }

  getUserCities(userIdx: number): string[] { return this.userCities[userIdx] || []; }
  isUserCityOthers(userIdx: number): boolean { return this.userCityIsOthers[userIdx] || false; }

  getStatesForCountryDynamic(countryName: string): string[] {
    if (!countryName?.trim()) return [];
    return getStatesForCountry(countryName);
  }

  // ======================================================================
  // CURRENCY
  // ======================================================================

  getCurrencySymbolForCode(code: string): string {
    const found = this.worldCurrencies.find(c => c.code === code);
    return found ? found.symbol : '₹';
  }

  onCurrencyChange(event: any, locationIndex: number): void {
    const symbol = this.getCurrencySymbolForCode(event.target.value);
    (this.locations.at(locationIndex) as FormGroup).patchValue({ currencySymbol: symbol });
  }

  // ======================================================================
  // EDIT MODE — CHECK ROUTE PARAM
  // ======================================================================

  private checkEditMode(): void {
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      if (id) {
        this.mode = 'edit';
        this.buyerId = +id;
        this.currentStep = 1;
        this.loadBuyerData(this.buyerId);
      } else {
        this.mode = 'create';
        this.currentStep = 1;
      }
    });
  }

  get locations(): FormArray { return this.buyerForm.get('locations') as FormArray; }
  get departments(): FormArray { return this.buyerForm.get('departments') as FormArray; }
  get users(): FormArray { return this.buyerForm.get('users') as FormArray; }

  // ======================================================================
  // LOCATION FORM ARRAY
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
      otherCity: [''],
      state: ['', Validators.required],
      postalCode: ['', [Validators.required, Validators.pattern(/^[A-Za-z0-9\s\-]{3,10}$/)]],
      country: ['India', Validators.required],
      landlineNumber: [''],
      faxNumber: [''],
      currencyCode: ['INR', Validators.required],
      currencySymbol: ['₹']
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

  onLocationTypeChange(event: any, locationIndex: number): void {
    const locationGroup = this.locations.at(locationIndex) as FormGroup;
    const customTypeControl = locationGroup.get('otherLocationType');
    if (event.target.value === 'Others') {
      customTypeControl?.setValidators([Validators.required, Validators.minLength(2)]);
      customTypeControl?.setValue('');
    } else {
      customTypeControl?.clearValidators();
      customTypeControl?.setValue(null);
    }
    customTypeControl?.updateValueAndValidity();
  }

  // ======================================================================
  // DEPARTMENT FORM ARRAY
  // ======================================================================

  addDepartment(): void {
    const departmentGroup = this.fb.group({
      id: [null],
      locationIndex: ['', Validators.required],
      departmentName: ['', [Validators.required, Validators.minLength(2)]],
      departmentDescription: ['']
    });
    this.departments.push(departmentGroup);
  }

  removeDepartment(deptIndex: number): void {
    if (this.departments.length > 1) {
      this.departments.removeAt(deptIndex);
    } else {
      this.messageService.showMessage('warning', 'Warning', 'At least one department is required');
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
  // USER FORM ARRAY
  // ======================================================================

  addUser(userData?: any): void {
    const userGroup = this.fb.group({
      id: [userData?.id || null],
      departmentIndex: [userData?.departmentIndex ?? '', Validators.required],
      firstName: [userData?.firstName || '', [Validators.required, Validators.minLength(2)]],
      lastName: [userData?.lastName || '', [Validators.required, Validators.minLength(1)]],
      email: [userData?.email || '', [Validators.required, Validators.email]],
      phone: [userData?.phone || '', [Validators.required, Validators.pattern(/^[0-9]{10}$/)]],
      designation: [userData?.designation || '', Validators.required],
      employeeId: [userData?.employeeId || '', Validators.required],
      gender: [userData?.gender || ''],
      dateOfBirth: [userData?.dateOfBirth || ''],
      addressLine1: [userData?.addressLine1 || ''],
      addressLine2: [userData?.addressLine2 || ''],
      country: [userData?.country || 'India'],
      state: [userData?.state || ''],
      city: [userData?.city || ''],
      otherCity: [''],
      postalCode: [userData?.postalCode || ''],
      password: [userData?.password || '', [Validators.required, Validators.minLength(6)]]
    });

    const newIndex = this.users.length;
    this.users.push(userGroup);
    this.userCities[newIndex] = [];
    this.userCityIsOthers[newIndex] = false;
  }

  removeUser(userIndex: number): void {
    if (this.users.length > 1) {
      this.users.removeAt(userIndex);
      this.userCities.splice(userIndex, 1);
      this.userCityIsOthers.splice(userIndex, 1);
    } else {
      this.messageService.showMessage('warning', 'Warning', 'At least one user is required');
    }
  }

  onCompanyTypeChange(event: any): void {
    const customTypeControl = this.buyerForm.get('otherCompanyType');
    if (event.target.value === 'Others') {
      customTypeControl?.setValidators([Validators.required, Validators.minLength(2)]);
      customTypeControl?.setValue('');
    } else {
      customTypeControl?.clearValidators();
      customTypeControl?.setValue(null);
    }
    customTypeControl?.updateValueAndValidity();
  }

  // ======================================================================
  // EDIT MODE — LOAD BUYER DATA
  // ======================================================================

  private loadBuyerData(id: number): void {
    this.buyerService.getBuyerById(id).subscribe({
      next: (buyer: any) => {
        let companyTypeForForm = buyer.companyType;
        let otherCompanyType = null;
        if (buyer.companyType && !this.companyTypes.includes(buyer.companyType)) {
          companyTypeForForm = 'Others';
          otherCompanyType = buyer.companyType;
        }
        this.buyerForm.patchValue({
          companyName: buyer.companyName,
          companyType: companyTypeForForm,
          otherCompanyType: otherCompanyType,
          contactPersonName: buyer.contactPersonName,
          contactPersonDesignation: buyer.contactPersonDesignation,
          contactPersonEmail: buyer.contactPersonEmail,
          contactPersonPhone: buyer.contactPersonPhone,
          addressLine1: buyer.addressLine1,
          addressLine2: buyer.addressLine2,
          state: buyer.state,
          postalCode: buyer.postalCode,
          country: buyer.country,
          gstNumber: buyer.gstNumber,
          panNumber: buyer.panNumber,
          cinNumber: buyer.cinNumber,
          website: buyer.website
        });

        if (buyer.country) this.hqStates = getStatesForCountry(buyer.country);
        if (buyer.country && buyer.state) {
          this.hqCities = getCityNamesForState(buyer.country, buyer.state);
        }

        const knownCities = this.hqCities;
        if (buyer.city && knownCities.length > 0 && !knownCities.includes(buyer.city)) {
          this.hqCityIsOthers = true;
          this.buyerForm.patchValue({ city: '__others__', otherCity: buyer.city });
          this.buyerForm.get('otherCity')?.setValidators([Validators.required, Validators.minLength(2)]);
          this.buyerForm.get('otherCity')?.updateValueAndValidity();
        } else {
          this.hqCityIsOthers = false;
          this.buyerForm.patchValue({ city: buyer.city });
        }

        this.onCompanyTypeChange({ target: { value: companyTypeForForm } });

        while (this.locations.length) this.locations.removeAt(0);
        while (this.departments.length) this.departments.removeAt(0);
        while (this.users.length) this.users.removeAt(0);
        this.locationStates = [];
        this.locationCities = [];
        this.locationCityIsOthers = [];
        this.userCities = [];
        this.userCityIsOthers = [];

        if (buyer.locations && Array.isArray(buyer.locations)) {
          this.populateLocations(buyer.locations);
        }
      },
      error: () => {
        this.messageService.showMessage('error', 'Error', 'Failed to load buyer data');
        setTimeout(() => this.router.navigate(['/dashboard']), 2000);
      }
    });
  }

  private populateLocations(locations: Location[]): void {
    locations.forEach((loc: any, locIndex: number) => {
      this.addLocation();

      let locationTypeForForm = loc.locationType;
      let otherLocationType = null;
      if (loc.locationType && !this.locationTypes.includes(loc.locationType)) {
        locationTypeForForm = 'Others';
        otherLocationType = loc.locationType;
      }

      if (loc.country) this.locationStates[locIndex] = getStatesForCountry(loc.country);
      if (loc.country && loc.state) {
        this.locationCities[locIndex] = getCityNamesForState(loc.country, loc.state);
      }

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
        locationName: loc.locationName,
        locationType: locationTypeForForm,
        otherLocationType: otherLocationType,
        locationContactName: loc.locationContactName,
        locationContactEmail: loc.locationContactEmail,
        locationContactPhone: loc.locationContactPhone,
        addressLine1: loc.addressLine1,
        addressLine2: loc.addressLine2,
        city: cityValue,
        otherCity: otherCityValue,
        state: loc.state,
        postalCode: loc.postalCode,
        country: loc.country,
        landlineNumber: loc.landlineNumber,
        faxNumber: loc.faxNumber,
        currencyCode: loc.currencyCode || 'INR',
        currencySymbol: loc.currencySymbol || '₹'
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
          this.departments.at(deptIndex).patchValue({
            id: dept.id,
            locationIndex: locIndex,
            departmentName: dept.departmentName,
            departmentDescription: dept.departmentDescription
          });

          if (dept.users && dept.users.length > 0) {
            dept.users.forEach((user: any) => {
              const userIdx = this.users.length;
              this.addUser({
                id: user.id,
                departmentIndex: deptIndex,
                firstName: user.firstName, lastName: user.lastName,
                email: user.email, phone: user.phone,
                designation: user.designation, employeeId: user.employeeId,
                gender: user.gender, dateOfBirth: user.dateOfBirth,
                addressLine1: user.addressLine1, addressLine2: user.addressLine2,
                country: user.country || 'India',
                state: user.state, city: user.city,
                postalCode: user.postalCode, password: ''
              });

              if (user.state) {
                const uCountry = user.country || 'India';
                this.userCities[userIdx] = getCityNamesForState(uCountry, user.state);
                const knownUserCities = this.userCities[userIdx];
                if (user.city && knownUserCities.length > 0 && !knownUserCities.includes(user.city)) {
                  this.userCityIsOthers[userIdx] = true;
                  this.users.at(userIdx).patchValue({ city: '__others__', otherCity: user.city });
                  this.users.at(userIdx).get('otherCity')
                    ?.setValidators([Validators.required, Validators.minLength(2)]);
                  this.users.at(userIdx).get('otherCity')?.updateValueAndValidity();
                }
              }
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
      case 1: return this.isBuyerDetailsValid();
      case 2: return this.locationsBasicsValid();
      case 3: return this.departmentsValid();
      case 4: return this.usersValid();
      default: return false;
    }
  }

  private isBuyerDetailsValid(): boolean {
    // ✅ FIX: 'companyName' is intentionally NOT in this list.
    // It is a hidden field auto-set from the OrgAdmin at submit time.
    // Including it would permanently block Step 1 since the user never fills it.
    const fields = [
      'companyType', 'contactPersonName', 'contactPersonDesignation',
      'contactPersonEmail', 'contactPersonPhone', 'addressLine1', 'city', 'state',
      'postalCode', 'country'
    ];

    const standardValid = fields.every(f => {
      const control = this.buyerForm.get(f);
      if (!control?.valid) {
        console.warn(`[Step1 Validation] Failed field: "${f}"`, control?.errors, '| Value:', control?.value);
      }
      return control?.valid;
    });

    const otherCompanyValid: boolean = this.buyerForm.get('companyType')?.value === 'Others'
      ? (this.buyerForm.get('otherCompanyType')?.valid ?? false) : true;

    const otherCityValid: boolean = this.hqCityIsOthers
      ? (this.buyerForm.get('otherCity')?.valid ?? false) : true;

    return standardValid && otherCompanyValid && otherCityValid;
  }

  private locationsBasicsValid(): boolean {
    return this.locations.length > 0 &&
      this.locations.controls.every((loc, idx) => {
        const fields = ['locationName', 'locationType', 'locationContactName',
          'locationContactEmail', 'locationContactPhone', 'addressLine1',
          'city', 'state', 'postalCode', 'country', 'currencyCode'];
        const standardValid = fields.every(f => loc.get(f)?.valid);
        const otherTypeValid: boolean = loc.get('locationType')?.value === 'Others'
          ? (loc.get('otherLocationType')?.valid ?? false) : true;
        const otherCityValid: boolean = this.locationCityIsOthers[idx]
          ? (loc.get('otherCity')?.valid ?? false) : true;
        return standardValid && otherTypeValid && otherCityValid;
      });
  }

  private departmentsValid(): boolean {
    return this.departments.length > 0 && this.departments.controls.every(dept =>
      dept.get('departmentName')?.valid && dept.get('locationIndex')?.valid
    );
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
      this.buyerForm.markAllAsTouched();
      return;
    }
    this.messageService.showMessage('success', 'Saved', `Step ${this.currentStep} saved successfully`);
    if (this.currentStep < this.maxStep) this.currentStep++;
  }

  prevStep(): void { if (this.currentStep > 1) this.currentStep--; }
  nextStep(): void { if (this.currentStep < this.maxStep) this.currentStep++; }

  // ======================================================================
  // SUBMIT
  // ======================================================================

  async onSubmit(): Promise<void> {

    if (this.isOrgAdminMode && !this.orgAdminId) {
      this.messageService.showMessage('error', 'Error', 'Organization Admin ID not found. Please login again.');
      return;
    }

    // ✅ KEY FIX: Set companyName from OrgAdmin BEFORE the form validity check.
    // The companyName field is hidden in the form so the user never types in it.
    // We inject the OrgAdmin's company name here so the backend receives a valid value.
    if (this.isOrgAdminMode && this.orgAdminCompanyName) {
      this.buyerForm.get('companyName')?.setValue(this.orgAdminCompanyName);
      console.log('✅ companyName auto-set from OrgAdmin:', this.orgAdminCompanyName);
    }

    if (this.buyerForm.invalid) {
      this.buyerForm.markAllAsTouched();
      this.messageService.showMessage('error', 'Validation Error', 'Please fill all required fields');
      return;
    }

    this.isSubmitting = true;
    const formValue = this.buyerForm.getRawValue();

    // ✅ SAFETY NET: If companyName is still empty for any reason, use orgAdminCompanyName
    if (!formValue.companyName || !formValue.companyName.trim()) {
      formValue.companyName = this.orgAdminCompanyName || 'Unknown Company';
      console.warn('⚠️ companyName was still empty after setValue — applied fallback:', formValue.companyName);
    }

    // Resolve HQ Others city
    if (formValue.city === '__others__' && formValue.otherCity) {
      formValue.city = formValue.otherCity;
    }
    delete formValue.otherCity;

    if (formValue.companyType === 'Others' && formValue.otherCompanyType) {
      formValue.companyType = formValue.otherCompanyType;
    }
    delete formValue.otherCompanyType;

    const processedLocations: any[] = [];
    if (formValue.locations && Array.isArray(formValue.locations)) {
      formValue.locations.forEach((loc: any) => {
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
        const locationIndex = parseInt(dept.locationIndex, 10);
        if (!isNaN(locationIndex) && locationIndex >= 0 && locationIndex < processedLocations.length) {
          const { locationIndex: _, ...deptData } = dept;
          deptData.users = [];
          processedLocations[locationIndex].departments.push(deptData);
        }
      });
    }

    if (formValue.users && Array.isArray(formValue.users)) {
      formValue.users.forEach((user: any) => {
        if (user.city === '__others__' && user.otherCity) user.city = user.otherCity;
        delete user.otherCity;

        const deptIdx = parseInt(user.departmentIndex, 10);
        let targetDepartment: any = null;
        let counter = 0;
        for (let location of processedLocations) {
          for (let dept of location.departments) {
            if (counter === deptIdx) { targetDepartment = dept; break; }
            counter++;
          }
          if (targetDepartment) break;
        }
        if (targetDepartment) {
          const { departmentIndex: _, country: _c, ...userData } = user;
          if (this.mode === 'edit' && (!userData.password || !userData.password.trim())) {
            delete userData.password;
          }
          targetDepartment.users.push(userData);
        }
      });
    }

    // Build final payload — no logo fields
    const payload: any = { ...formValue, locations: processedLocations };
    delete payload.departments;
    delete payload.users;

    // ✅ Always set organizationCompanyName from OrgAdmin
    if (this.isOrgAdminMode) {
      payload.organizationCompanyName = this.orgAdminCompanyName;
    }

    console.log('========================================');
    console.log('📦 FINAL SUBMIT PAYLOAD');
    console.log('companyName:', payload.companyName);
    console.log('organizationCompanyName:', payload.organizationCompanyName);
    console.log('locationsCount:', processedLocations.length);
    console.log('========================================');

    if (this.mode === 'create') {
      this.buyerService.createCompleteHierarchy(
        payload,
        this.isOrgAdminMode ? this.orgAdminId! : undefined
      ).subscribe({
        next: () => {
          this.messageService.showMessage('success', 'Success',
            this.isOrgAdminMode
              ? 'Buyer created and linked to your organization. Company logo will be shared automatically.'
              : 'Buyer created successfully');
          this.isSubmitting = false;
          setTimeout(() => this.router.navigate(['/dashboard']), 1500);
        },
        error: (err: any) => {
          this.messageService.showMessage('error', 'Error', err.error?.message || err.message || 'Failed to create buyer');
          this.isSubmitting = false;
        }
      });
    } else if (this.buyerId) {
      this.buyerService.updateCompleteHierarchy(this.buyerId, payload).subscribe({
        next: () => {
          this.messageService.showMessage('success', 'Success', 'Buyer updated');
          this.isSubmitting = false;
          setTimeout(() => this.router.navigate(['/dashboard']), 1500);
        },
        error: (err: any) => {
          this.messageService.showMessage('error', 'Error', err.error?.message || err.message || 'Update failed');
          this.isSubmitting = false;
        }
      });
    }
  }
}