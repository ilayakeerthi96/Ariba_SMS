


// import { Component, OnInit } from '@angular/core';
// import { FormBuilder, FormGroup, Validators, FormArray } from '@angular/forms';
// import { Router, ActivatedRoute } from '@angular/router';
// import { CommonModule } from '@angular/common';
// import { ReactiveFormsModule } from '@angular/forms';
// import { BuyerService } from '../dashboard/buyer-b.service';
// import { MessageService } from '../../../shared/service/message.service';
// import { Buyer, Location, Department, User } from '../dashboard/buyer-b.model';
// import { COUNTRY_STATE_DATA, getAllCountryNames, getStatesForCountry, CountryData } from '../../../shared/data/country-state.data';

// // ✅ World Currencies list
// export const WORLD_CURRENCIES = [
//   { code: 'AFN', symbol: '؋', name: 'Afghan Afghani' },
//   { code: 'ALL', symbol: 'L', name: 'Albanian Lek' },
//   { code: 'DZD', symbol: 'د.ج', name: 'Algerian Dinar' },
//   { code: 'AOA', symbol: 'Kz', name: 'Angolan Kwanza' },
//   { code: 'ARS', symbol: '$', name: 'Argentine Peso' },
//   { code: 'AMD', symbol: '֏', name: 'Armenian Dram' },
//   { code: 'AWG', symbol: 'ƒ', name: 'Aruban Florin' },
//   { code: 'AUD', symbol: 'A$', name: 'Australian Dollar' },
//   { code: 'AZN', symbol: '₼', name: 'Azerbaijani Manat' },
//   { code: 'BSD', symbol: 'B$', name: 'Bahamian Dollar' },
//   { code: 'BHD', symbol: '.د.ب', name: 'Bahraini Dinar' },
//   { code: 'BDT', symbol: '৳', name: 'Bangladeshi Taka' },
//   { code: 'BBD', symbol: 'Bds$', name: 'Barbadian Dollar' },
//   { code: 'BYN', symbol: 'Br', name: 'Belarusian Ruble' },
//   { code: 'BZD', symbol: 'BZ$', name: 'Belize Dollar' },
//   { code: 'BMD', symbol: 'BD$', name: 'Bermudian Dollar' },
//   { code: 'BTN', symbol: 'Nu', name: 'Bhutanese Ngultrum' },
//   { code: 'BOB', symbol: 'Bs.', name: 'Bolivian Boliviano' },
//   { code: 'BAM', symbol: 'KM', name: 'Bosnia-Herzegovina Convertible Mark' },
//   { code: 'BWP', symbol: 'P', name: 'Botswana Pula' },
//   { code: 'BRL', symbol: 'R$', name: 'Brazilian Real' },
//   { code: 'GBP', symbol: '£', name: 'British Pound Sterling' },
//   { code: 'BND', symbol: 'B$', name: 'Brunei Dollar' },
//   { code: 'BGN', symbol: 'лв', name: 'Bulgarian Lev' },
//   { code: 'BIF', symbol: 'Fr', name: 'Burundian Franc' },
//   { code: 'KHR', symbol: '៛', name: 'Cambodian Riel' },
//   { code: 'CAD', symbol: 'C$', name: 'Canadian Dollar' },
//   { code: 'CVE', symbol: '$', name: 'Cape Verdean Escudo' },
//   { code: 'KYD', symbol: 'CI$', name: 'Cayman Islands Dollar' },
//   { code: 'XAF', symbol: 'Fr', name: 'Central African CFA Franc' },
//   { code: 'CLP', symbol: '$', name: 'Chilean Peso' },
//   { code: 'CNY', symbol: '¥', name: 'Chinese Yuan' },
//   { code: 'COP', symbol: '$', name: 'Colombian Peso' },
//   { code: 'KMF', symbol: 'Fr', name: 'Comorian Franc' },
//   { code: 'CDF', symbol: 'Fr', name: 'Congolese Franc' },
//   { code: 'CRC', symbol: '₡', name: 'Costa Rican Colón' },
//   { code: 'HRK', symbol: 'kn', name: 'Croatian Kuna' },
//   { code: 'CUP', symbol: '$', name: 'Cuban Peso' },
//   { code: 'CZK', symbol: 'Kč', name: 'Czech Koruna' },
//   { code: 'DKK', symbol: 'kr', name: 'Danish Krone' },
//   { code: 'DJF', symbol: 'Fr', name: 'Djiboutian Franc' },
//   { code: 'DOP', symbol: 'RD$', name: 'Dominican Peso' },
//   { code: 'EGP', symbol: '£', name: 'Egyptian Pound' },
//   { code: 'ERN', symbol: 'Nfk', name: 'Eritrean Nakfa' },
//   { code: 'ETB', symbol: 'Br', name: 'Ethiopian Birr' },
//   { code: 'EUR', symbol: '€', name: 'Euro' },
//   { code: 'FJD', symbol: 'FJ$', name: 'Fijian Dollar' },
//   { code: 'GMD', symbol: 'D', name: 'Gambian Dalasi' },
//   { code: 'GEL', symbol: '₾', name: 'Georgian Lari' },
//   { code: 'GHS', symbol: '₵', name: 'Ghanaian Cedi' },
//   { code: 'GTQ', symbol: 'Q', name: 'Guatemalan Quetzal' },
//   { code: 'GNF', symbol: 'Fr', name: 'Guinean Franc' },
//   { code: 'GYD', symbol: 'G$', name: 'Guyanese Dollar' },
//   { code: 'HTG', symbol: 'G', name: 'Haitian Gourde' },
//   { code: 'HNL', symbol: 'L', name: 'Honduran Lempira' },
//   { code: 'HKD', symbol: 'HK$', name: 'Hong Kong Dollar' },
//   { code: 'HUF', symbol: 'Ft', name: 'Hungarian Forint' },
//   { code: 'ISK', symbol: 'kr', name: 'Icelandic Króna' },
//   { code: 'INR', symbol: '₹', name: 'Indian Rupee' },
//   { code: 'IDR', symbol: 'Rp', name: 'Indonesian Rupiah' },
//   { code: 'IRR', symbol: '﷼', name: 'Iranian Rial' },
//   { code: 'IQD', symbol: 'ع.د', name: 'Iraqi Dinar' },
//   { code: 'ILS', symbol: '₪', name: 'Israeli New Shekel' },
//   { code: 'JMD', symbol: 'J$', name: 'Jamaican Dollar' },
//   { code: 'JPY', symbol: '¥', name: 'Japanese Yen' },
//   { code: 'JOD', symbol: 'د.ا', name: 'Jordanian Dinar' },
//   { code: 'KZT', symbol: '₸', name: 'Kazakhstani Tenge' },
//   { code: 'KES', symbol: 'KSh', name: 'Kenyan Shilling' },
//   { code: 'KWD', symbol: 'د.ك', name: 'Kuwaiti Dinar' },
//   { code: 'KGS', symbol: 'с', name: 'Kyrgyzstani Som' },
//   { code: 'LAK', symbol: '₭', name: 'Laotian Kip' },
//   { code: 'LBP', symbol: 'ل.ل', name: 'Lebanese Pound' },
//   { code: 'LSL', symbol: 'L', name: 'Lesotho Loti' },
//   { code: 'LRD', symbol: 'L$', name: 'Liberian Dollar' },
//   { code: 'LYD', symbol: 'ل.د', name: 'Libyan Dinar' },
//   { code: 'MOP', symbol: 'P', name: 'Macanese Pataca' },
//   { code: 'MKD', symbol: 'ден', name: 'Macedonian Denar' },
//   { code: 'MGA', symbol: 'Ar', name: 'Malagasy Ariary' },
//   { code: 'MWK', symbol: 'MK', name: 'Malawian Kwacha' },
//   { code: 'MYR', symbol: 'RM', name: 'Malaysian Ringgit' },
//   { code: 'MVR', symbol: 'Rf', name: 'Maldivian Rufiyaa' },
//   { code: 'MRU', symbol: 'UM', name: 'Mauritanian Ouguiya' },
//   { code: 'MUR', symbol: '₨', name: 'Mauritian Rupee' },
//   { code: 'MXN', symbol: '$', name: 'Mexican Peso' },
//   { code: 'MDL', symbol: 'L', name: 'Moldovan Leu' },
//   { code: 'MNT', symbol: '₮', name: 'Mongolian Tögrög' },
//   { code: 'MAD', symbol: 'د.م.', name: 'Moroccan Dirham' },
//   { code: 'MZN', symbol: 'MT', name: 'Mozambican Metical' },
//   { code: 'MMK', symbol: 'K', name: 'Myanmar Kyat' },
//   { code: 'NAD', symbol: 'N$', name: 'Namibian Dollar' },
//   { code: 'NPR', symbol: '₨', name: 'Nepalese Rupee' },
//   { code: 'NZD', symbol: 'NZ$', name: 'New Zealand Dollar' },
//   { code: 'NIO', symbol: 'C$', name: 'Nicaraguan Córdoba' },
//   { code: 'NGN', symbol: '₦', name: 'Nigerian Naira' },
//   { code: 'NOK', symbol: 'kr', name: 'Norwegian Krone' },
//   { code: 'OMR', symbol: 'ر.ع.', name: 'Omani Rial' },
//   { code: 'PKR', symbol: '₨', name: 'Pakistani Rupee' },
//   { code: 'PAB', symbol: 'B/.', name: 'Panamanian Balboa' },
//   { code: 'PGK', symbol: 'K', name: 'Papua New Guinean Kina' },
//   { code: 'PYG', symbol: '₲', name: 'Paraguayan Guaraní' },
//   { code: 'PEN', symbol: 'S/.', name: 'Peruvian Sol' },
//   { code: 'PHP', symbol: '₱', name: 'Philippine Peso' },
//   { code: 'PLN', symbol: 'zł', name: 'Polish Złoty' },
//   { code: 'QAR', symbol: 'ر.ق', name: 'Qatari Riyal' },
//   { code: 'RON', symbol: 'lei', name: 'Romanian Leu' },
//   { code: 'RUB', symbol: '₽', name: 'Russian Ruble' },
//   { code: 'RWF', symbol: 'Fr', name: 'Rwandan Franc' },
//   { code: 'SAR', symbol: 'ر.س', name: 'Saudi Riyal' },
//   { code: 'RSD', symbol: 'дин.', name: 'Serbian Dinar' },
//   { code: 'SLL', symbol: 'Le', name: 'Sierra Leonean Leone' },
//   { code: 'SGD', symbol: 'S$', name: 'Singapore Dollar' },
//   { code: 'SOS', symbol: 'Sh', name: 'Somali Shilling' },
//   { code: 'ZAR', symbol: 'R', name: 'South African Rand' },
//   { code: 'KRW', symbol: '₩', name: 'South Korean Won' },
//   { code: 'SSP', symbol: '£', name: 'South Sudanese Pound' },
//   { code: 'LKR', symbol: '₨', name: 'Sri Lankan Rupee' },
//   { code: 'SDG', symbol: '£', name: 'Sudanese Pound' },
//   { code: 'SRD', symbol: '$', name: 'Surinamese Dollar' },
//   { code: 'SEK', symbol: 'kr', name: 'Swedish Krona' },
//   { code: 'CHF', symbol: 'Fr', name: 'Swiss Franc' },
//   { code: 'SYP', symbol: '£', name: 'Syrian Pound' },
//   { code: 'TWD', symbol: 'NT$', name: 'New Taiwan Dollar' },
//   { code: 'TJS', symbol: 'SM', name: 'Tajikistani Somoni' },
//   { code: 'TZS', symbol: 'Sh', name: 'Tanzanian Shilling' },
//   { code: 'THB', symbol: '฿', name: 'Thai Baht' },
//   { code: 'TOP', symbol: 'T$', name: 'Tongan Paʻanga' },
//   { code: 'TTD', symbol: 'TT$', name: 'Trinidad & Tobago Dollar' },
//   { code: 'TND', symbol: 'د.ت', name: 'Tunisian Dinar' },
//   { code: 'TRY', symbol: '₺', name: 'Turkish Lira' },
//   { code: 'TMT', symbol: 'T', name: 'Turkmenistani Manat' },
//   { code: 'UGX', symbol: 'Sh', name: 'Ugandan Shilling' },
//   { code: 'UAH', symbol: '₴', name: 'Ukrainian Hryvnia' },
//   { code: 'AED', symbol: 'د.إ', name: 'UAE Dirham' },
//   { code: 'USD', symbol: '$', name: 'US Dollar' },
//   { code: 'UYU', symbol: '$U', name: 'Uruguayan Peso' },
//   { code: 'UZS', symbol: 'лв', name: 'Uzbekistani Som' },
//   { code: 'VUV', symbol: 'Vt', name: 'Vanuatu Vatu' },
//   { code: 'VEF', symbol: 'Bs.F', name: 'Venezuelan Bolívar' },
//   { code: 'VND', symbol: '₫', name: 'Vietnamese Đồng' },
//   { code: 'YER', symbol: '﷼', name: 'Yemeni Rial' },
//   { code: 'ZMW', symbol: 'ZK', name: 'Zambian Kwacha' },
//   { code: 'ZWL', symbol: 'Z$', name: 'Zimbabwean Dollar' },
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

//   // ✅ World currencies for location dropdown
//   worldCurrencies = WORLD_CURRENCIES;

//   // ✅ All countries list (for dropdowns)
//   allCountries: string[] = getAllCountryNames();

//   // ✅ HQ address states — dynamically loaded based on selected country
//   hqStates: string[] = [];

//   // ✅ Per-location states — array indexed by location index
//   locationStates: string[][] = [];

//   companyTypes = ['Manufacturing', 'Trading', 'Services', 'Distribution', 'Retail', 'Others'];
//   locationTypes = ['Branch', 'Regional', 'Warehouse', 'Service Center', 'Others'];

//   constructor(
//     private fb: FormBuilder,
//     private buyerService: BuyerService,
//     private messageService: MessageService,
//     private router: Router,
//     private route: ActivatedRoute
//   ) {}

//   email: string = '';
//   companyName: string = '';
//   fullName: string = '';

//   ngOnInit(): void {
//     this.checkOrgAdminMode();
//     this.loadUserHeaderInfo();
//     this.initializeForm();
//     this.checkEditMode();
//     // ✅ Load HQ states for default country (India)
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
//       // ✅ HQ state — free text fallback, but driven by dropdown
//       state: ['', Validators.required],
//       postalCode: ['', [Validators.required, Validators.pattern(/^[A-Za-z0-9\s\-]{3,10}$/)]],
//       // ✅ HQ country — dropdown
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

//   // ===================== HQ COUNTRY / STATE CHANGE =====================

//   // ✅ When HQ country changes, reload HQ states
//   onHQCountryChange(event: any): void {
//     const selectedCountry = event.target.value;
//     this.hqStates = getStatesForCountry(selectedCountry);
//     // Reset state when country changes
//     this.buyerForm.patchValue({ state: '' });
//   }

//   // ===================== LOCATION COUNTRY / STATE CHANGE =====================

//   // ✅ When location country changes, reload that location's states
//   onLocationCountryChange(event: any, locationIndex: number): void {
//     const selectedCountry = event.target.value;
//     this.locationStates[locationIndex] = getStatesForCountry(selectedCountry);
//     // Reset state for this location
//     const locationGroup = this.locations.at(locationIndex);
//     locationGroup.patchValue({ state: '' });
//   }

//   // ✅ Get states for a specific location index
//   getStatesForLocation(locationIndex: number): string[] {
//     return this.locationStates[locationIndex] || [];
//   }

//   // ===================== CURRENCY METHODS =====================

//   getCurrencySymbolForCode(code: string): string {
//     const found = this.worldCurrencies.find(c => c.code === code);
//     return found ? found.symbol : '₹';
//   }

//   onCurrencyChange(event: any, locationIndex: number): void {
//     const selectedCode = event.target.value;
//     const symbol = this.getCurrencySymbolForCode(selectedCode);
//     const locationGroup = this.locations.at(locationIndex) as FormGroup;
//     locationGroup.patchValue({ currencySymbol: symbol });
//   }

//   // ===================== LOGO METHODS =====================

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

//   // ===================== LOCATION METHODS =====================

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
//       // ✅ state — text driven by dropdown
//       state: ['', Validators.required],
//       postalCode: ['', [Validators.required, Validators.pattern(/^[A-Za-z0-9\s\-]{3,10}$/)]],
//       // ✅ country — dropdown, default India
//       country: ['India', Validators.required],
//       landlineNumber: [''],
//       faxNumber: [''],
//       // ✅ Currency fields per location
//       currencyCode: ['INR', Validators.required],
//       currencySymbol: ['₹']
//     });

//     const newIndex = this.locations.length;
//     this.locations.push(locationGroup);

//     // ✅ Initialize states for this new location (default India)
//     this.locationStates[newIndex] = getStatesForCountry('India');
//   }

//   removeLocation(index: number): void {
//     if (this.locations.length > 1) {
//       this.locations.removeAt(index);
//       this.locationStates.splice(index, 1);
//     } else {
//       this.messageService.showMessage('warning', 'Warning', 'At least one location is required');
//     }
//   }

//   onLocationTypeChange(event: any, locationIndex: number): void {
//     const locationGroup = this.locations.at(locationIndex) as FormGroup;
//     const selectedValue = event.target.value;
//     const customTypeControl = locationGroup.get('otherLocationType');
//     if (selectedValue === 'Others') {
//       customTypeControl?.setValidators([Validators.required, Validators.minLength(2)]);
//       customTypeControl?.setValue('');
//     } else {
//       customTypeControl?.clearValidators();
//       customTypeControl?.setValue(null);
//     }
//     customTypeControl?.updateValueAndValidity();
//   }

//   // ===================== DEPARTMENT METHODS =====================

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

//   // ===================== USER METHODS =====================

//   addUser(userData?: Partial<User>): void {
//     const userGroup = this.fb.group({
//       id: [userData?.id || null],
//       departmentIndex: [userData ? (userData as any).departmentIndex : '', Validators.required],
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
//       this.messageService.showMessage('warning', 'Warning', 'At least one user is required');
//     }
//   }

//   onCompanyTypeChange(event: any): void {
//     const selectedValue = event.target.value;
//     const customTypeControl = this.buyerForm.get('otherCompanyType');
//     if (selectedValue === 'Others') {
//       customTypeControl?.setValidators([Validators.required, Validators.minLength(2)]);
//       customTypeControl?.setValue('');
//     } else {
//       customTypeControl?.clearValidators();
//       customTypeControl?.setValue(null);
//     }
//     customTypeControl?.updateValueAndValidity();
//   }

//   // ===================== LOAD BUYER DATA (EDIT MODE) =====================

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
//           city: buyer.city,
//           state: buyer.state,
//           postalCode: buyer.postalCode,
//           country: buyer.country,
//           gstNumber: buyer.gstNumber,
//           panNumber: buyer.panNumber,
//           cinNumber: buyer.cinNumber,
//           website: buyer.website
//         });

//         // ✅ Load HQ states for the buyer's country
//         if (buyer.country) {
//           this.hqStates = getStatesForCountry(buyer.country);
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

//         if (buyer.locations && Array.isArray(buyer.locations)) {
//           this.populateLocations(buyer.locations);
//         }
//       },
//       error: (err: any) => {
//         this.messageService.showMessage('error', 'Error', 'Failed to load buyer data');
//         setTimeout(() => this.router.navigate(['/dashboard']), 2000);
//       }
//     });
//   }

//   private populateLocations(locations: Location[]): void {
//     locations.forEach((loc: any, locIndex: number) => {
//       this.addLocation(); // this also initializes locationStates[locIndex] to India states

//       let locationTypeForForm = loc.locationType;
//       let otherLocationType = null;
//       if (loc.locationType && !this.locationTypes.includes(loc.locationType)) {
//         locationTypeForForm = 'Others';
//         otherLocationType = loc.locationType;
//       }

//       // ✅ Reload states for the location's actual country
//       if (loc.country) {
//         this.locationStates[locIndex] = getStatesForCountry(loc.country);
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
//         faxNumber: loc.faxNumber,
//         // ✅ Restore saved currency
//         currencyCode: loc.currencyCode || 'INR',
//         currencySymbol: loc.currencySymbol || '₹'
//       });

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

//   // ===================== STEP VALIDATION =====================

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
//     const standardFieldsValid = fields.every(field => this.buyerForm.get(field)?.valid);
//     const companyTypeControl = this.buyerForm.get('companyType');
//     const otherCompanyTypeControl = this.buyerForm.get('otherCompanyType');
//     const otherFieldValid: boolean = companyTypeControl?.value === 'Others'
//       ? (otherCompanyTypeControl?.valid ?? false) : true;
//     return standardFieldsValid && otherFieldValid;
//   }

//   private locationsBasicsValid(): boolean {
//     return this.locations.length > 0 &&
//            this.locations.controls.every(loc => {
//              const fields = ['locationName', 'locationType', 'locationContactName',
//                              'locationContactEmail', 'locationContactPhone', 'addressLine1',
//                              'city', 'state', 'postalCode', 'country', 'currencyCode'];
//              const standardFieldsValid = fields.every(field => loc.get(field)?.valid);
//              const locationTypeControl = loc.get('locationType');
//              const otherLocationTypeControl = loc.get('otherLocationType');
//              const otherFieldValid: boolean = locationTypeControl?.value === 'Others'
//                ? (otherLocationTypeControl?.valid ?? false) : true;
//              return standardFieldsValid && otherFieldValid;
//            });
//   }

//   private departmentsValid(): boolean {
//     return this.departments.length > 0 && this.departments.controls.every(dept =>
//       dept.get('departmentName')?.valid && dept.get('locationIndex')?.valid
//     );
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

//   // ===================== STEP NAVIGATION =====================

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

//   // ===================== SUBMIT =====================

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
//     if (this.isOrgAdminMode && !this.orgAdminCompanyName) {
//       this.messageService.showMessage('error', 'Error', 'Organization Company Name not found. Please login again.');
//       return;
//     }

//     this.isSubmitting = true;
//     const formValue = this.buyerForm.getRawValue();

//     if (formValue.companyType === 'Others' && formValue.otherCompanyType) {
//       formValue.companyType = formValue.otherCompanyType;
//     }
//     delete formValue.otherCompanyType;

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
//         const deptIdx = parseInt(user.departmentIndex, 10);
//         let targetDepartment: any = null;
//         let globalDeptCounter = 0;
//         for (let location of processedLocations) {
//           for (let dept of location.departments) {
//             if (globalDeptCounter === deptIdx) { targetDepartment = dept; break; }
//             globalDeptCounter++;
//           }
//           if (targetDepartment) break;
//         }
//         if (targetDepartment) {
//           const { departmentIndex: _, ...userData } = user;
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

//     if (this.isOrgAdminMode) {
//       payload.organizationCompanyName = this.orgAdminCompanyName;
//     }

//     if (this.mode === 'create') {
//       this.buyerService.createCompleteHierarchy(
//         payload,
//         this.isOrgAdminMode ? this.orgAdminId! : undefined
//       ).subscribe({
//         next: (response) => {
//           this.messageService.showMessage('success', 'Success',
//             this.isOrgAdminMode
//               ? 'Buyer created and linked to your organization'
//               : 'Buyer created successfully');
//           this.isSubmitting = false;
//           setTimeout(() => this.router.navigate(['/dashboard']), 1500);
//         },
//         error: (err: any) => {
//           const errorMsg = err.error?.message || err.message || 'Failed to create buyer';
//           this.messageService.showMessage('error', 'Error', errorMsg);
//           this.isSubmitting = false;
//         }
//       });
//     } else if (this.buyerId) {
//       this.buyerService.updateCompleteHierarchy(this.buyerId, payload).subscribe({
//         next: (response) => {
//           this.messageService.showMessage('success', 'Success', 'Buyer updated');
//           this.isSubmitting = false;
//           setTimeout(() => this.router.navigate(['/dashboard']), 1500);
//         },
//         error: (err: any) => {
//           const errorMsg = err.error?.message || err.message || 'Update failed';
//           this.messageService.showMessage('error', 'Error', errorMsg);
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
import { BuyerService } from '../dashboard/buyer-b.service';
import { MessageService } from '../../../shared/service/message.service';
import { Buyer, Location, Department, User } from '../dashboard/buyer-b.model';

// ✅ Import from the NEW unified data file
import {
  getAllCountryNames,
  getStatesForCountry,
  getCityNamesForState,
  getPostalCodeForCity,
  CityData
} from '../../../shared/data/country-state.data';

// ✅ World Currencies list
export const WORLD_CURRENCIES = [
  { code: 'AFN', symbol: '؋', name: 'Afghan Afghani' },
  { code: 'ALL', symbol: 'L', name: 'Albanian Lek' },
  { code: 'DZD', symbol: 'د.ج', name: 'Algerian Dinar' },
  { code: 'AOA', symbol: 'Kz', name: 'Angolan Kwanza' },
  { code: 'ARS', symbol: '$', name: 'Argentine Peso' },
  { code: 'AMD', symbol: '֏', name: 'Armenian Dram' },
  { code: 'AWG', symbol: 'ƒ', name: 'Aruban Florin' },
  { code: 'AUD', symbol: 'A$', name: 'Australian Dollar' },
  { code: 'AZN', symbol: '₼', name: 'Azerbaijani Manat' },
  { code: 'BSD', symbol: 'B$', name: 'Bahamian Dollar' },
  { code: 'BHD', symbol: '.د.ب', name: 'Bahraini Dinar' },
  { code: 'BDT', symbol: '৳', name: 'Bangladeshi Taka' },
  { code: 'BBD', symbol: 'Bds$', name: 'Barbadian Dollar' },
  { code: 'BYN', symbol: 'Br', name: 'Belarusian Ruble' },
  { code: 'BZD', symbol: 'BZ$', name: 'Belize Dollar' },
  { code: 'BMD', symbol: 'BD$', name: 'Bermudian Dollar' },
  { code: 'BTN', symbol: 'Nu', name: 'Bhutanese Ngultrum' },
  { code: 'BOB', symbol: 'Bs.', name: 'Bolivian Boliviano' },
  { code: 'BAM', symbol: 'KM', name: 'Bosnia-Herzegovina Convertible Mark' },
  { code: 'BWP', symbol: 'P', name: 'Botswana Pula' },
  { code: 'BRL', symbol: 'R$', name: 'Brazilian Real' },
  { code: 'GBP', symbol: '£', name: 'British Pound Sterling' },
  { code: 'BND', symbol: 'B$', name: 'Brunei Dollar' },
  { code: 'BGN', symbol: 'лв', name: 'Bulgarian Lev' },
  { code: 'BIF', symbol: 'Fr', name: 'Burundian Franc' },
  { code: 'KHR', symbol: '៛', name: 'Cambodian Riel' },
  { code: 'CAD', symbol: 'C$', name: 'Canadian Dollar' },
  { code: 'CVE', symbol: '$', name: 'Cape Verdean Escudo' },
  { code: 'KYD', symbol: 'CI$', name: 'Cayman Islands Dollar' },
  { code: 'XAF', symbol: 'Fr', name: 'Central African CFA Franc' },
  { code: 'CLP', symbol: '$', name: 'Chilean Peso' },
  { code: 'CNY', symbol: '¥', name: 'Chinese Yuan' },
  { code: 'COP', symbol: '$', name: 'Colombian Peso' },
  { code: 'KMF', symbol: 'Fr', name: 'Comorian Franc' },
  { code: 'CDF', symbol: 'Fr', name: 'Congolese Franc' },
  { code: 'CRC', symbol: '₡', name: 'Costa Rican Colón' },
  { code: 'HRK', symbol: 'kn', name: 'Croatian Kuna' },
  { code: 'CUP', symbol: '$', name: 'Cuban Peso' },
  { code: 'CZK', symbol: 'Kč', name: 'Czech Koruna' },
  { code: 'DKK', symbol: 'kr', name: 'Danish Krone' },
  { code: 'DJF', symbol: 'Fr', name: 'Djiboutian Franc' },
  { code: 'DOP', symbol: 'RD$', name: 'Dominican Peso' },
  { code: 'EGP', symbol: '£', name: 'Egyptian Pound' },
  { code: 'ERN', symbol: 'Nfk', name: 'Eritrean Nakfa' },
  { code: 'ETB', symbol: 'Br', name: 'Ethiopian Birr' },
  { code: 'EUR', symbol: '€', name: 'Euro' },
  { code: 'FJD', symbol: 'FJ$', name: 'Fijian Dollar' },
  { code: 'GMD', symbol: 'D', name: 'Gambian Dalasi' },
  { code: 'GEL', symbol: '₾', name: 'Georgian Lari' },
  { code: 'GHS', symbol: '₵', name: 'Ghanaian Cedi' },
  { code: 'GTQ', symbol: 'Q', name: 'Guatemalan Quetzal' },
  { code: 'GNF', symbol: 'Fr', name: 'Guinean Franc' },
  { code: 'GYD', symbol: 'G$', name: 'Guyanese Dollar' },
  { code: 'HTG', symbol: 'G', name: 'Haitian Gourde' },
  { code: 'HNL', symbol: 'L', name: 'Honduran Lempira' },
  { code: 'HKD', symbol: 'HK$', name: 'Hong Kong Dollar' },
  { code: 'HUF', symbol: 'Ft', name: 'Hungarian Forint' },
  { code: 'ISK', symbol: 'kr', name: 'Icelandic Króna' },
  { code: 'INR', symbol: '₹', name: 'Indian Rupee' },
  { code: 'IDR', symbol: 'Rp', name: 'Indonesian Rupiah' },
  { code: 'IRR', symbol: '﷼', name: 'Iranian Rial' },
  { code: 'IQD', symbol: 'ع.د', name: 'Iraqi Dinar' },
  { code: 'ILS', symbol: '₪', name: 'Israeli New Shekel' },
  { code: 'JMD', symbol: 'J$', name: 'Jamaican Dollar' },
  { code: 'JPY', symbol: '¥', name: 'Japanese Yen' },
  { code: 'JOD', symbol: 'د.ا', name: 'Jordanian Dinar' },
  { code: 'KZT', symbol: '₸', name: 'Kazakhstani Tenge' },
  { code: 'KES', symbol: 'KSh', name: 'Kenyan Shilling' },
  { code: 'KWD', symbol: 'د.ك', name: 'Kuwaiti Dinar' },
  { code: 'KGS', symbol: 'с', name: 'Kyrgyzstani Som' },
  { code: 'LAK', symbol: '₭', name: 'Laotian Kip' },
  { code: 'LBP', symbol: 'ل.ل', name: 'Lebanese Pound' },
  { code: 'LSL', symbol: 'L', name: 'Lesotho Loti' },
  { code: 'LRD', symbol: 'L$', name: 'Liberian Dollar' },
  { code: 'LYD', symbol: 'ل.د', name: 'Libyan Dinar' },
  { code: 'MOP', symbol: 'P', name: 'Macanese Pataca' },
  { code: 'MKD', symbol: 'ден', name: 'Macedonian Denar' },
  { code: 'MGA', symbol: 'Ar', name: 'Malagasy Ariary' },
  { code: 'MWK', symbol: 'MK', name: 'Malawian Kwacha' },
  { code: 'MYR', symbol: 'RM', name: 'Malaysian Ringgit' },
  { code: 'MVR', symbol: 'Rf', name: 'Maldivian Rufiyaa' },
  { code: 'MRU', symbol: 'UM', name: 'Mauritanian Ouguiya' },
  { code: 'MUR', symbol: '₨', name: 'Mauritian Rupee' },
  { code: 'MXN', symbol: '$', name: 'Mexican Peso' },
  { code: 'MDL', symbol: 'L', name: 'Moldovan Leu' },
  { code: 'MNT', symbol: '₮', name: 'Mongolian Tögrög' },
  { code: 'MAD', symbol: 'د.م.', name: 'Moroccan Dirham' },
  { code: 'MZN', symbol: 'MT', name: 'Mozambican Metical' },
  { code: 'MMK', symbol: 'K', name: 'Myanmar Kyat' },
  { code: 'NAD', symbol: 'N$', name: 'Namibian Dollar' },
  { code: 'NPR', symbol: '₨', name: 'Nepalese Rupee' },
  { code: 'NZD', symbol: 'NZ$', name: 'New Zealand Dollar' },
  { code: 'NIO', symbol: 'C$', name: 'Nicaraguan Córdoba' },
  { code: 'NGN', symbol: '₦', name: 'Nigerian Naira' },
  { code: 'NOK', symbol: 'kr', name: 'Norwegian Krone' },
  { code: 'OMR', symbol: 'ر.ع.', name: 'Omani Rial' },
  { code: 'PKR', symbol: '₨', name: 'Pakistani Rupee' },
  { code: 'PAB', symbol: 'B/.', name: 'Panamanian Balboa' },
  { code: 'PGK', symbol: 'K', name: 'Papua New Guinean Kina' },
  { code: 'PYG', symbol: '₲', name: 'Paraguayan Guaraní' },
  { code: 'PEN', symbol: 'S/.', name: 'Peruvian Sol' },
  { code: 'PHP', symbol: '₱', name: 'Philippine Peso' },
  { code: 'PLN', symbol: 'zł', name: 'Polish Złoty' },
  { code: 'QAR', symbol: 'ر.ق', name: 'Qatari Riyal' },
  { code: 'RON', symbol: 'lei', name: 'Romanian Leu' },
  { code: 'RUB', symbol: '₽', name: 'Russian Ruble' },
  { code: 'RWF', symbol: 'Fr', name: 'Rwandan Franc' },
  { code: 'SAR', symbol: 'ر.س', name: 'Saudi Riyal' },
  { code: 'RSD', symbol: 'дин.', name: 'Serbian Dinar' },
  { code: 'SLL', symbol: 'Le', name: 'Sierra Leonean Leone' },
  { code: 'SGD', symbol: 'S$', name: 'Singapore Dollar' },
  { code: 'SOS', symbol: 'Sh', name: 'Somali Shilling' },
  { code: 'ZAR', symbol: 'R', name: 'South African Rand' },
  { code: 'KRW', symbol: '₩', name: 'South Korean Won' },
  { code: 'SSP', symbol: '£', name: 'South Sudanese Pound' },
  { code: 'LKR', symbol: '₨', name: 'Sri Lankan Rupee' },
  { code: 'SDG', symbol: '£', name: 'Sudanese Pound' },
  { code: 'SRD', symbol: '$', name: 'Surinamese Dollar' },
  { code: 'SEK', symbol: 'kr', name: 'Swedish Krona' },
  { code: 'CHF', symbol: 'Fr', name: 'Swiss Franc' },
  { code: 'SYP', symbol: '£', name: 'Syrian Pound' },
  { code: 'TWD', symbol: 'NT$', name: 'New Taiwan Dollar' },
  { code: 'TJS', symbol: 'SM', name: 'Tajikistani Somoni' },
  { code: 'TZS', symbol: 'Sh', name: 'Tanzanian Shilling' },
  { code: 'THB', symbol: '฿', name: 'Thai Baht' },
  { code: 'TOP', symbol: 'T$', name: 'Tongan Paʻanga' },
  { code: 'TTD', symbol: 'TT$', name: 'Trinidad & Tobago Dollar' },
  { code: 'TND', symbol: 'د.ت', name: 'Tunisian Dinar' },
  { code: 'TRY', symbol: '₺', name: 'Turkish Lira' },
  { code: 'TMT', symbol: 'T', name: 'Turkmenistani Manat' },
  { code: 'UGX', symbol: 'Sh', name: 'Ugandan Shilling' },
  { code: 'UAH', symbol: '₴', name: 'Ukrainian Hryvnia' },
  { code: 'AED', symbol: 'د.إ', name: 'UAE Dirham' },
  { code: 'USD', symbol: '$', name: 'US Dollar' },
  { code: 'UYU', symbol: '$U', name: 'Uruguayan Peso' },
  { code: 'UZS', symbol: 'лв', name: 'Uzbekistani Som' },
  { code: 'VUV', symbol: 'Vt', name: 'Vanuatu Vatu' },
  { code: 'VEF', symbol: 'Bs.F', name: 'Venezuelan Bolívar' },
  { code: 'VND', symbol: '₫', name: 'Vietnamese Đồng' },
  { code: 'YER', symbol: '﷼', name: 'Yemeni Rial' },
  { code: 'ZMW', symbol: 'ZK', name: 'Zambian Kwacha' },
  { code: 'ZWL', symbol: 'Z$', name: 'Zimbabwean Dollar' },
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

  selectedLogoFile: File | null = null;
  logoPreview: string | null = null;
  maxLogoSize = 5 * 1024 * 1024;
  allowedLogoTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif', 'image/svg+xml'];

  worldCurrencies = WORLD_CURRENCIES;
  allCountries: string[] = getAllCountryNames();

  // ========== HQ address cascades ==========
  hqStates: string[] = [];
  hqCities: string[] = [];

  // ========== Per-location cascades ==========
  locationStates: string[][] = [];
  locationCities: string[][] = [];

  // ========== Per-user cascades (Step 4 residential) ==========
  userCities: string[][] = [];

  companyTypes = ['Manufacturing', 'Trading', 'Services', 'Distribution', 'Retail', 'Others'];
  locationTypes = ['Branch', 'Regional', 'Warehouse', 'Service Center', 'Others'];

  constructor(
    private fb: FormBuilder,
    private buyerService: BuyerService,
    private messageService: MessageService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  email: string = '';
  companyName: string = '';
  fullName: string = '';

  ngOnInit(): void {
    this.checkOrgAdminMode();
    this.loadUserHeaderInfo();
    this.initializeForm();
    this.checkEditMode();
    // Load HQ states + cities for default country India
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
    this.buyerForm = this.fb.group({
      companyName: ['', [Validators.required, Validators.minLength(2)]],
      companyType: ['', Validators.required],
      otherCompanyType: [''],
      logo: [null],
      contactPersonName: ['', [Validators.required, Validators.minLength(2)]],
      contactPersonDesignation: ['', Validators.required],
      contactPersonEmail: ['', [Validators.required, Validators.email]],
      contactPersonPhone: ['', [Validators.required, Validators.pattern(/^[0-9]{10}$/)]],
      addressLine1: ['', [Validators.required, Validators.minLength(5)]],
      addressLine2: [''],
      city: ['', Validators.required],
      state: ['', Validators.required],
      postalCode: ['', [Validators.required, Validators.pattern(/^[A-Za-z0-9\s\-]{3,10}$/)]],
      country: ['India', Validators.required],
     gstNumber: ['', Validators.required],
panNumber: ['', [Validators.required, Validators.pattern(/^[A-Z]{5}[0-9]{4}[A-Z]{1}$/)]],
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

  // ===================== HQ COUNTRY → STATE → CITY → POSTAL =====================

  onHQCountryChange(event: any): void {
    const country = event.target.value;
    this.hqStates = getStatesForCountry(country);
    this.hqCities = [];
    this.buyerForm.patchValue({ state: '', city: '', postalCode: '' });
  }

  onHQStateChange(event: any): void {
    const country = this.buyerForm.get('country')?.value;
    const state = event.target.value;
    this.hqCities = getCityNamesForState(country, state);
    this.buyerForm.patchValue({ city: '', postalCode: '' });
  }

  onHQCityChange(event: any): void {
    const country = this.buyerForm.get('country')?.value;
    const state = this.buyerForm.get('state')?.value;
    const city = event.target.value;
    const postal = getPostalCodeForCity(country, state, city);
    if (postal) {
      this.buyerForm.patchValue({ postalCode: postal });
    }
  }

  // ===================== LOCATION COUNTRY → STATE → CITY → POSTAL =====================

  onLocationCountryChange(event: any, idx: number): void {
    const country = event.target.value;
    this.locationStates[idx] = getStatesForCountry(country);
    this.locationCities[idx] = [];
    const loc = this.locations.at(idx);
    loc.patchValue({ state: '', city: '', postalCode: '' });
  }

  onLocationStateChange(event: any, idx: number): void {
    const country = this.locations.at(idx).get('country')?.value;
    const state = event.target.value;
    this.locationCities[idx] = getCityNamesForState(country, state);
    this.locations.at(idx).patchValue({ city: '', postalCode: '' });
  }

  onLocationCityChange(event: any, idx: number): void {
    const loc = this.locations.at(idx);
    const country = loc.get('country')?.value;
    const state = loc.get('state')?.value;
    const city = event.target.value;
    const postal = getPostalCodeForCity(country, state, city);
    if (postal) {
      loc.patchValue({ postalCode: postal });
    }
  }

  getStatesForLocation(idx: number): string[] {
    return this.locationStates[idx] || [];
  }

  getCitiesForLocation(idx: number): string[] {
    return this.locationCities[idx] || [];
  }

  // ===================== USER RESIDENTIAL COUNTRY → CITY → POSTAL =====================

  onUserStateChange(event: any, userIdx: number): void {
    // User address has a free-text country field, but we can derive cities
    // from the country field if it's set
    const userGroup = this.users.at(userIdx);
    const country = userGroup.get('country')?.value || '';
    const state = event.target.value;
    this.userCities[userIdx] = getCityNamesForState(country, state);
    userGroup.patchValue({ city: '', postalCode: '' });
  }

  onUserCityChange(event: any, userIdx: number): void {
    const userGroup = this.users.at(userIdx);
    const country = userGroup.get('country')?.value || '';
    const state = userGroup.get('state')?.value || '';
    const city = event.target.value;
    const postal = getPostalCodeForCity(country, state, city);
    if (postal) {
      userGroup.patchValue({ postalCode: postal });
    }
  }

  getUserCities(userIdx: number): string[] {
    return this.userCities[userIdx] || [];
  }

  /** Used in the HTML template for user residential state dropdown */
  getStatesForCountryDynamic(countryName: string): string[] {
    if (!countryName?.trim()) return [];
    return getStatesForCountry(countryName);
  }

  // ===================== CURRENCY =====================

  getCurrencySymbolForCode(code: string): string {
    const found = this.worldCurrencies.find(c => c.code === code);
    return found ? found.symbol : '₹';
  }

  onCurrencyChange(event: any, locationIndex: number): void {
    const symbol = this.getCurrencySymbolForCode(event.target.value);
    (this.locations.at(locationIndex) as FormGroup).patchValue({ currencySymbol: symbol });
  }

  // ===================== LOGO =====================

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
    this.buyerForm.patchValue({ logo: null });
    const fileInput = document.querySelector('input[type="file"]') as HTMLInputElement;
    if (fileInput) fileInput.value = '';
  }

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

  // ===================== LOCATION METHODS =====================

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
  }

  removeLocation(index: number): void {
    if (this.locations.length > 1) {
      this.locations.removeAt(index);
      this.locationStates.splice(index, 1);
      this.locationCities.splice(index, 1);
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

  // ===================== DEPARTMENT METHODS =====================

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

  // ===================== USER METHODS =====================

  addUser(userData?: Partial<User>): void {
    const userGroup = this.fb.group({
      id: [userData?.id || null],
      departmentIndex: [userData ? (userData as any).departmentIndex : '', Validators.required],
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
      country: ['India'],   // ✅ country field for user residential address
      state: [userData?.state || ''],
      city: [userData?.city || ''],
      postalCode: [userData?.postalCode || ''],
      password: [userData?.password || '', [Validators.required, Validators.minLength(6)]]
    });

    const newIndex = this.users.length;
    this.users.push(userGroup);
    this.userCities[newIndex] = [];
  }

  removeUser(userIndex: number): void {
    if (this.users.length > 1) {
      this.users.removeAt(userIndex);
      this.userCities.splice(userIndex, 1);
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

  // ===================== LOAD BUYER DATA (EDIT MODE) =====================

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

        // ✅ Reload HQ state/city cascades for buyer's country & state
        if (buyer.country) {
          this.hqStates = getStatesForCountry(buyer.country);
        }
        if (buyer.country && buyer.state) {
          this.hqCities = getCityNamesForState(buyer.country, buyer.state);
        }
        // Set city after cascades are loaded
        this.buyerForm.patchValue({ city: buyer.city });

        this.onCompanyTypeChange({ target: { value: companyTypeForForm } });

        if (buyer.id) {
          this.buyerService.getBuyerLogoBase64(buyer.id).subscribe({
            next: (logoData: any) => { if (logoData) this.logoPreview = logoData; },
            error: () => {}
          });
        }

        while (this.locations.length) this.locations.removeAt(0);
        while (this.departments.length) this.departments.removeAt(0);
        while (this.users.length) this.users.removeAt(0);
        this.locationStates = [];
        this.locationCities = [];

        if (buyer.locations && Array.isArray(buyer.locations)) {
          this.populateLocations(buyer.locations);
        }
      },
      error: (err: any) => {
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

      // ✅ Reload state/city cascades for this location
      if (loc.country) {
        this.locationStates[locIndex] = getStatesForCountry(loc.country);
      }
      if (loc.country && loc.state) {
        this.locationCities[locIndex] = getCityNamesForState(loc.country, loc.state);
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
        city: loc.city,
        state: loc.state,
        postalCode: loc.postalCode,
        country: loc.country,
        landlineNumber: loc.landlineNumber,
        faxNumber: loc.faxNumber,
        currencyCode: loc.currencyCode || 'INR',
        currencySymbol: loc.currencySymbol || '₹'
      });

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
                firstName: user.firstName,
                lastName: user.lastName,
                email: user.email,
                phone: user.phone,
                designation: user.designation,
                employeeId: user.employeeId,
                gender: user.gender,
                dateOfBirth: user.dateOfBirth,
                addressLine1: user.addressLine1,
                addressLine2: user.addressLine2,
                city: user.city,
                state: user.state,
                postalCode: user.postalCode,
                password: ''
              });
              // Restore user city cascade if state is set
              if (user.state) {
                this.userCities[userIdx] = getCityNamesForState('India', user.state);
              }
            });
          }
        });
      }
    });
  }

  // ===================== STEP VALIDATION =====================

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
    const fields = ['companyName', 'companyType', 'contactPersonName', 'contactPersonDesignation',
  'contactPersonEmail', 'contactPersonPhone', 'addressLine1', 'city', 'state',
  'postalCode', 'country', 'gstNumber', 'panNumber'];
  
    const standardFieldsValid = fields.every(field => this.buyerForm.get(field)?.valid);
    const otherFieldValid: boolean = this.buyerForm.get('companyType')?.value === 'Others'
      ? (this.buyerForm.get('otherCompanyType')?.valid ?? false) : true;
    return standardFieldsValid && otherFieldValid;
  }

  private locationsBasicsValid(): boolean {
    return this.locations.length > 0 &&
      this.locations.controls.every(loc => {
        const fields = ['locationName', 'locationType', 'locationContactName',
          'locationContactEmail', 'locationContactPhone', 'addressLine1',
          'city', 'state', 'postalCode', 'country', 'currencyCode'];
        const standardFieldsValid = fields.every(field => loc.get(field)?.valid);
        const otherFieldValid: boolean = loc.get('locationType')?.value === 'Others'
          ? (loc.get('otherLocationType')?.valid ?? false) : true;
        return standardFieldsValid && otherFieldValid;
      });
  }

  private departmentsValid(): boolean {
    return this.departments.length > 0 && this.departments.controls.every(dept =>
      dept.get('departmentName')?.valid && dept.get('locationIndex')?.valid
    );
  }

  private usersValid(): boolean {
    return this.users.length > 0 && this.users.controls.every(user =>
      user.get('firstName')?.valid &&
      user.get('lastName')?.valid &&
      user.get('email')?.valid &&
      user.get('phone')?.valid &&
      user.get('designation')?.valid &&
      user.get('employeeId')?.valid &&
      user.get('departmentIndex')?.valid &&
      user.get('password')?.valid
    );
  }

  // ===================== STEP NAVIGATION =====================

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

  // ===================== SUBMIT =====================

  async onSubmit(): Promise<void> {
    if (this.buyerForm.invalid) {
      this.buyerForm.markAllAsTouched();
      this.messageService.showMessage('error', 'Validation Error', 'Please fill all required fields');
      return;
    }
    if (this.isOrgAdminMode && !this.orgAdminId) {
      this.messageService.showMessage('error', 'Error', 'Organization Admin ID not found. Please login again.');
      return;
    }

    this.isSubmitting = true;
    const formValue = this.buyerForm.getRawValue();

    if (formValue.companyType === 'Others' && formValue.otherCompanyType) {
      formValue.companyType = formValue.otherCompanyType;
    }
    delete formValue.otherCompanyType;

    const processedLocations: any[] = [];
    if (formValue.locations && Array.isArray(formValue.locations)) {
      formValue.locations.forEach((loc: any) => {
        if (loc.locationType === 'Others' && loc.otherLocationType) {
          loc.locationType = loc.otherLocationType;
        }
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
        const deptIdx = parseInt(user.departmentIndex, 10);
        let targetDepartment: any = null;
        let globalDeptCounter = 0;
        for (let location of processedLocations) {
          for (let dept of location.departments) {
            if (globalDeptCounter === deptIdx) { targetDepartment = dept; break; }
            globalDeptCounter++;
          }
          if (targetDepartment) break;
        }
        if (targetDepartment) {
          const { departmentIndex: _, country: _country, ...userData } = user;
          if (this.mode === 'edit' && (!userData.password || !userData.password.trim())) {
            delete userData.password;
          }
          targetDepartment.users.push(userData);
        }
      });
    }

    const payload: any = { ...formValue, locations: processedLocations };
    delete payload.departments;
    delete payload.users;
    delete payload.logo;

    if (this.selectedLogoFile) {
      try {
        const logoBase64 = await this.buyerService.convertFileToBase64(this.selectedLogoFile);
        payload.logoBase64 = logoBase64;
        payload.logoFilename = this.selectedLogoFile.name;
        payload.logoContentType = this.selectedLogoFile.type;
      } catch (error) {
        this.messageService.showMessage('error', 'Error', 'Failed to process logo');
        this.isSubmitting = false;
        return;
      }
    }

    if (this.isOrgAdminMode) {
      payload.organizationCompanyName = this.orgAdminCompanyName;
    }

    if (this.mode === 'create') {
      this.buyerService.createCompleteHierarchy(
        payload,
        this.isOrgAdminMode ? this.orgAdminId! : undefined
      ).subscribe({
        next: () => {
          this.messageService.showMessage('success', 'Success',
            this.isOrgAdminMode ? 'Buyer created and linked to your organization' : 'Buyer created successfully');
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