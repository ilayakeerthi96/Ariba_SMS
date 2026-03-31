// // // // import { Component, OnInit } from '@angular/core';
// // // // import { FormBuilder, FormGroup, Validators, FormArray } from '@angular/forms';
// // // // import { Router, ActivatedRoute } from '@angular/router';
// // // // import { CommonModule } from '@angular/common';
// // // // import { ReactiveFormsModule } from '@angular/forms';
// // // // import { BuyerService } from '../dashboard/buyer-b.service';
// // // // import { MessageService } from '../../../shared/service/message.service';
// // // // import { Buyer, Location, Department, User } from '../dashboard/buyer-b.model';

// // // // @Component({
// // // //   selector: 'app-create-b',
// // // //   templateUrl: './create-b.component.html',
// // // //   styleUrls: ['./create-b.component.css'],
// // // //   standalone: true,
// // // //   imports: [CommonModule, ReactiveFormsModule]
// // // // })
// // // // export class CreateBComponent implements OnInit {

// // // //   buyerForm!: FormGroup;
// // // //   currentStep = 1;
// // // //   maxStep = 4;
// // // //   isSubmitting = false;

// // // //   mode: 'create' | 'edit' = 'create';
// // // //   buyerId: number | null = null;

// // // //   states = ['Andhra Pradesh', 'Arunachal Pradesh', 'Assam', 'Bihar', 'Chhattisgarh', 
// // // //             'Goa', 'Gujarat', 'Haryana', 'Himachal Pradesh', 'Jharkhand', 'Karnataka',
// // // //             'Kerala', 'Madhya Pradesh', 'Maharashtra', 'Manipur', 'Meghalaya', 'Mizoram',
// // // //             'Nagaland', 'Odisha', 'Punjab', 'Rajasthan', 'Sikkim', 'Tamil Nadu', 'Telangana',
// // // //             'Tripura', 'Uttar Pradesh', 'Uttarakhand', 'West Bengal'];

// // // //   companyTypes = ['Manufacturing', 'Trading', 'Services', 'Distribution', 'Retail', 'Others'];
// // // //   locationTypes = ['Branch', 'Regional', 'Warehouse', 'Service Center', 'Others'];

// // // //   constructor(
// // // //     private fb: FormBuilder,
// // // //     private buyerService: BuyerService,
// // // //     private messageService: MessageService,
// // // //     private router: Router,
// // // //     private route: ActivatedRoute
// // // //   ) {}

// // // //   ngOnInit(): void {
// // // //     this.initializeForm();
// // // //     this.checkEditMode();
// // // //   }

// // // //   private initializeForm(): void {
// // // //     this.buyerForm = this.fb.group({
// // // //       companyName: ['', [Validators.required, Validators.minLength(2)]],
// // // //       companyType: ['', Validators.required],
// // // //       otherCompanyType: [''],
// // // //       contactPersonName: ['', [Validators.required, Validators.minLength(2)]],
// // // //       contactPersonDesignation: ['', Validators.required],
// // // //       contactPersonEmail: ['', [Validators.required, Validators.email]],
// // // //       contactPersonPhone: ['', [Validators.required, Validators.pattern(/^[0-9]{10}$/)]],
// // // //       addressLine1: ['', [Validators.required, Validators.minLength(5)]],
// // // //       addressLine2: [''],
// // // //       city: ['', Validators.required],
// // // //       state: ['', Validators.required],
// // // //       postalCode: ['', [Validators.required, Validators.pattern(/^[0-9]{6}$/)]],
// // // //       country: ['India', Validators.required],
// // // //       gstNumber: [''],
// // // //       panNumber: [''],
// // // //       cinNumber: [''],
// // // //       website: [''],
// // // //       locations: this.fb.array([], Validators.minLength(1)),
// // // //       departments: this.fb.array([]),
// // // //       users: this.fb.array([])
// // // //     });

// // // //     if (!this.buyerForm.get('locations')?.value?.length) {
// // // //       this.addLocation();
// // // //     }
// // // //   }

// // // //   private checkEditMode(): void {
// // // //     this.route.paramMap.subscribe(params => {
// // // //       const id = params.get('id');
// // // //       if (id) {
// // // //         this.mode = 'edit';
// // // //         this.buyerId = +id;
// // // //         this.currentStep = 1;
// // // //         this.loadBuyerData(this.buyerId);
// // // //       } else {
// // // //         this.mode = 'create';
// // // //         this.currentStep = 1;
// // // //       }
// // // //     });
// // // //   }

// // // //   get locations(): FormArray {
// // // //     return this.buyerForm.get('locations') as FormArray;
// // // //   }

// // // //   get departments(): FormArray {
// // // //     return this.buyerForm.get('departments') as FormArray;
// // // //   }

// // // //   get users(): FormArray {
// // // //     return this.buyerForm.get('users') as FormArray;
// // // //   }

// // // //   // ==================== LOCATION METHODS ====================

// // // //   addLocation(): void {
// // // //     const locationGroup = this.fb.group({
// // // //       id: [null],
// // // //       locationName: ['', [Validators.required, Validators.minLength(2)]],
// // // //       locationType: ['', Validators.required],
// // // //       otherLocationType: [''],
// // // //       locationContactName: ['', [Validators.required, Validators.minLength(2)]],
// // // //       locationContactEmail: ['', [Validators.required, Validators.email]],
// // // //       locationContactPhone: ['', [Validators.required, Validators.pattern(/^[0-9]{10}$/)]],
// // // //       addressLine1: ['', [Validators.required, Validators.minLength(5)]],
// // // //       addressLine2: [''],
// // // //       city: ['', Validators.required],
// // // //       state: ['', Validators.required],
// // // //       postalCode: ['', [Validators.required, Validators.pattern(/^[0-9]{6}$/)]],
// // // //       country: ['India', Validators.required],
// // // //       landlineNumber: [''],
// // // //       faxNumber: ['']
// // // //     });
// // // //     this.locations.push(locationGroup);
// // // //   }

// // // //   removeLocation(index: number): void {
// // // //     if (this.locations.length > 1) {
// // // //       this.locations.removeAt(index);
// // // //     } else {
// // // //       this.messageService.showMessage('warning', 'Warning', 'At least one location is required');
// // // //     }
// // // //   }

// // // //   onLocationTypeChange(event: any, locationIndex: number): void {
// // // //     const locationGroup = this.locations.at(locationIndex) as FormGroup;
// // // //     const selectedValue = event.target.value;
// // // //     const customTypeControl = locationGroup.get('otherLocationType');

// // // //     if (selectedValue === 'Others') {
// // // //       customTypeControl?.setValidators([Validators.required, Validators.minLength(2)]);
// // // //       customTypeControl?.setValue('');
// // // //     } else {
// // // //       customTypeControl?.clearValidators();
// // // //       customTypeControl?.setValue(null);
// // // //     }
// // // //     customTypeControl?.updateValueAndValidity();
// // // //   }

// // // //   // ==================== DEPARTMENT METHODS ====================

// // // //   addDepartment(): void {
// // // //     const departmentGroup = this.fb.group({
// // // //       id: [null],
// // // //       locationIndex: ['', Validators.required],
// // // //       departmentName: ['', [Validators.required, Validators.minLength(2)]],
// // // //       departmentDescription: ['']
// // // //     });
// // // //     this.departments.push(departmentGroup);
// // // //   }

// // // //   removeDepartment(deptIndex: number): void {
// // // //     if (this.departments.length > 1) {
// // // //       this.departments.removeAt(deptIndex);
// // // //     } else {
// // // //       this.messageService.showMessage('warning', 'Warning', 'At least one department is required');
// // // //     }
// // // //   }

// // // //   getLocationNameForDepartment(deptIndex: number): string {
// // // //     const dept = this.departments.at(deptIndex);
// // // //     const locIndex = dept?.get('locationIndex')?.value;
// // // //     if (locIndex !== '' && locIndex !== null && locIndex !== undefined) {
// // // //       const location = this.locations.at(locIndex);
// // // //       return location?.get('locationName')?.value || `Location ${locIndex + 1}`;
// // // //     }
// // // //     return '';
// // // //   }

// // // //   // ==================== USER METHODS ====================

// // // //   addUser(userData?: Partial<User>): void {
// // // //     const userGroup = this.fb.group({
// // // //       id: [userData?.id || null],
// // // //       departmentIndex: [userData ? (userData as any).departmentIndex : '', Validators.required],
// // // //       firstName: [userData?.firstName || '', [Validators.required, Validators.minLength(2)]],
// // // //       lastName: [userData?.lastName || '', [Validators.required, Validators.minLength(1)]],
// // // //       email: [userData?.email || '', [Validators.required, Validators.email]],
// // // //       phone: [userData?.phone || '', [Validators.required, Validators.pattern(/^[0-9]{10}$/)]],
// // // //       designation: [userData?.designation || '', Validators.required],
// // // //       employeeId: [userData?.employeeId || '', Validators.required],
// // // //       gender: [userData?.gender || ''],
// // // //       dateOfBirth: [userData?.dateOfBirth || ''],
// // // //       addressLine1: [userData?.addressLine1 || ''],
// // // //       addressLine2: [userData?.addressLine2 || ''],
// // // //       city: [userData?.city || ''],
// // // //       state: [userData?.state || ''],
// // // //       postalCode: [userData?.postalCode || ''],
// // // //       password: [userData?.password || '', [Validators.required, Validators.minLength(6)]]
// // // //     });
// // // //     this.users.push(userGroup);
// // // //   }

// // // //   removeUser(userIndex: number): void {
// // // //     if (this.users.length > 1) {
// // // //       this.users.removeAt(userIndex);
// // // //     } else {
// // // //       this.messageService.showMessage('warning', 'Warning', 'At least one user is required');
// // // //     }
// // // //   }

// // // //   // ==================== COMPANY TYPE CHANGE ====================

// // // //   onCompanyTypeChange(event: any): void {
// // // //     const selectedValue = event.target.value;
// // // //     const customTypeControl = this.buyerForm.get('otherCompanyType');

// // // //     if (selectedValue === 'Others') {
// // // //       customTypeControl?.setValidators([Validators.required, Validators.minLength(2)]);
// // // //       customTypeControl?.setValue('');
// // // //     } else {
// // // //       customTypeControl?.clearValidators();
// // // //       customTypeControl?.setValue(null);
// // // //     }
// // // //     customTypeControl?.updateValueAndValidity();
// // // //   }

// // // //   // ==================== LOAD BUYER DATA (EDIT MODE) ====================

// // // //   private loadBuyerData(id: number): void {
// // // //     this.buyerService.getBuyerById(id).subscribe({
// // // //       next: (buyer: any) => {
// // // //         let companyTypeForForm = buyer.companyType;
// // // //         let otherCompanyType = null;

// // // //         if (buyer.companyType && !this.companyTypes.includes(buyer.companyType)) {
// // // //           companyTypeForForm = 'Others';
// // // //           otherCompanyType = buyer.companyType;
// // // //         }

// // // //         this.buyerForm.patchValue({
// // // //           companyName: buyer.companyName,
// // // //           companyType: companyTypeForForm,
// // // //           otherCompanyType: otherCompanyType,
// // // //           contactPersonName: buyer.contactPersonName,
// // // //           contactPersonDesignation: buyer.contactPersonDesignation,
// // // //           contactPersonEmail: buyer.contactPersonEmail,
// // // //           contactPersonPhone: buyer.contactPersonPhone,
// // // //           addressLine1: buyer.addressLine1,
// // // //           addressLine2: buyer.addressLine2,
// // // //           city: buyer.city,
// // // //           state: buyer.state,
// // // //           postalCode: buyer.postalCode,
// // // //           country: buyer.country,
// // // //           gstNumber: buyer.gstNumber,
// // // //           panNumber: buyer.panNumber,
// // // //           cinNumber: buyer.cinNumber,
// // // //           website: buyer.website
// // // //         });

// // // //         this.onCompanyTypeChange({ target: { value: companyTypeForForm } });

// // // //         // Clear arrays
// // // //         while (this.locations.length) {
// // // //           this.locations.removeAt(0);
// // // //         }
// // // //         while (this.departments.length) {
// // // //           this.departments.removeAt(0);
// // // //         }
// // // //         while (this.users.length) {
// // // //           this.users.removeAt(0);
// // // //         }

// // // //         if (buyer.locations && Array.isArray(buyer.locations)) {
// // // //           this.populateLocations(buyer.locations);
// // // //         }
// // // //       },
// // // //       error: (err: any) => {
// // // //         this.messageService.showMessage('error', 'Error', 'Failed to load buyer data');
// // // //         setTimeout(() => this.router.navigate(['/dashboard']), 2000);
// // // //       }
// // // //     });
// // // //   }

// // // //   private populateLocations(locations: Location[]): void {
// // // //     locations.forEach((loc: any, locIndex: number) => {
// // // //       this.addLocation();

// // // //       let locationTypeForForm = loc.locationType;
// // // //       let otherLocationType = null;

// // // //       if (loc.locationType && !this.locationTypes.includes(loc.locationType)) {
// // // //         locationTypeForForm = 'Others';
// // // //         otherLocationType = loc.locationType;
// // // //       }

// // // //       this.locations.at(locIndex).patchValue({
// // // //         id: loc.id,
// // // //         locationName: loc.locationName,
// // // //         locationType: locationTypeForForm,
// // // //         otherLocationType: otherLocationType,
// // // //         locationContactName: loc.locationContactName,
// // // //         locationContactEmail: loc.locationContactEmail,
// // // //         locationContactPhone: loc.locationContactPhone,
// // // //         addressLine1: loc.addressLine1,
// // // //         addressLine2: loc.addressLine2,
// // // //         city: loc.city,
// // // //         state: loc.state,
// // // //         postalCode: loc.postalCode,
// // // //         country: loc.country,
// // // //         landlineNumber: loc.landlineNumber,
// // // //         faxNumber: loc.faxNumber
// // // //       });

// // // //       this.onLocationTypeChange({ target: { value: locationTypeForForm } }, locIndex);

// // // //       if (loc.departments && loc.departments.length > 0) {
// // // //         loc.departments.forEach((dept: any) => {
// // // //           const deptIndex = this.departments.length;
// // // //           this.addDepartment();

// // // //           this.departments.at(deptIndex).patchValue({
// // // //             id: dept.id,
// // // //             locationIndex: locIndex,
// // // //             departmentName: dept.departmentName,
// // // //             departmentDescription: dept.departmentDescription
// // // //           });

// // // //           if (dept.users && dept.users.length > 0) {
// // // //             dept.users.forEach((user: any) => {
// // // //               this.addUser({
// // // //                 id: user.id,
// // // //                 departmentIndex: deptIndex,
// // // //                 firstName: user.firstName,
// // // //                 lastName: user.lastName,
// // // //                 email: user.email,
// // // //                 phone: user.phone,
// // // //                 designation: user.designation,
// // // //                 employeeId: user.employeeId,
// // // //                 gender: user.gender,
// // // //                 dateOfBirth: user.dateOfBirth,
// // // //                 addressLine1: user.addressLine1,
// // // //                 addressLine2: user.addressLine2,
// // // //                 city: user.city,
// // // //                 state: user.state,
// // // //                 postalCode: user.postalCode,
// // // //                 password: ''
// // // //               });
// // // //             });
// // // //           }
// // // //         });
// // // //       }
// // // //     });
// // // //   }

// // // //   // ==================== STEP VALIDATION ====================

// // // //   private isCurrentStepValid(): boolean {
// // // //     switch (this.currentStep) {
// // // //       case 1:
// // // //         return this.isBuyerDetailsValid();
// // // //       case 2:
// // // //         return this.locationsBasicsValid();
// // // //       case 3:
// // // //         return this.departmentsValid();
// // // //       case 4:
// // // //         return this.usersValid();
// // // //       default:
// // // //         return false;
// // // //     }
// // // //   }

// // // //   private isBuyerDetailsValid(): boolean {
// // // //     const fields = ['companyName', 'companyType', 'contactPersonName', 'contactPersonDesignation',
// // // //       'contactPersonEmail', 'contactPersonPhone', 'addressLine1', 'city', 'state', 
// // // //       'postalCode', 'country'];

// // // //     const standardFieldsValid = fields.every(field => this.buyerForm.get(field)?.valid);

// // // //     const companyTypeControl = this.buyerForm.get('companyType');
// // // //     const otherCompanyTypeControl = this.buyerForm.get('otherCompanyType');

// // // //     const otherFieldValid: boolean = companyTypeControl?.value === 'Others'
// // // //       ? (otherCompanyTypeControl?.valid ?? false)
// // // //       : true;

// // // //     return standardFieldsValid && otherFieldValid;
// // // //   }

// // // //   private locationsBasicsValid(): boolean {
// // // //     return this.locations.length > 0 && 
// // // //            this.locations.controls.every(loc => {
// // // //              const fields = ['locationName', 'locationType', 'locationContactName', 
// // // //                              'locationContactEmail', 'locationContactPhone', 'addressLine1',
// // // //                              'city', 'state', 'postalCode', 'country'];

// // // //              const standardFieldsValid = fields.every(field => loc.get(field)?.valid);

// // // //              const locationTypeControl = loc.get('locationType');
// // // //              const otherLocationTypeControl = loc.get('otherLocationType');

// // // //              const otherFieldValid: boolean = locationTypeControl?.value === 'Others'
// // // //                ? (otherLocationTypeControl?.valid ?? false)
// // // //                : true;

// // // //              return standardFieldsValid && otherFieldValid;
// // // //            });
// // // //   }

// // // //   private departmentsValid(): boolean {
// // // //     return this.departments.length > 0 && this.departments.controls.every(dept => 
// // // //       dept.get('departmentName')?.valid && dept.get('locationIndex')?.valid
// // // //     );
// // // //   }

// // // //   private usersValid(): boolean {
// // // //     return this.users.length > 0 && this.users.controls.every(user =>
// // // //       user.get('firstName')?.valid &&
// // // //       user.get('lastName')?.valid &&
// // // //       user.get('email')?.valid &&
// // // //       user.get('phone')?.valid &&
// // // //       user.get('designation')?.valid &&
// // // //       user.get('employeeId')?.valid &&
// // // //       user.get('departmentIndex')?.valid &&
// // // //       user.get('password')?.valid
// // // //     );
// // // //   }

// // // //   // ==================== STEP NAVIGATION ====================

// // // //   saveStep(): void {
// // // //     if (!this.isCurrentStepValid()) {
// // // //       this.messageService.showMessage('warning', 'Validation Error', 'Please fill all required fields on this step');
// // // //       this.buyerForm.markAllAsTouched();
// // // //       return;
// // // //     }

// // // //     this.messageService.showMessage('success', 'Saved', `Step ${this.currentStep} saved successfully`);
    
// // // //     if (this.currentStep < this.maxStep) {
// // // //       this.currentStep++;
// // // //     }
// // // //   }

// // // //   prevStep(): void {
// // // //     if (this.currentStep > 1) {
// // // //       this.currentStep--;
// // // //     }
// // // //   }

// // // //   nextStep(): void {
// // // //     if (this.currentStep < this.maxStep) {
// // // //       this.currentStep++;
// // // //     }
// // // //   }

// // // //   // ==================== FINAL SUBMIT ====================

// // // //   // onSubmit(): void {
// // // //   //   if (this.buyerForm.invalid) {
// // // //   //     this.buyerForm.markAllAsTouched();
// // // //   //     this.messageService.showMessage('error', 'Validation Error', 'Please fill all required fields');
// // // //   //     return;
// // // //   //   }

// // // //   //   this.isSubmitting = true;
    
// // // //   //   // Deep clone form value
// // // //   //   const formValue = JSON.parse(JSON.stringify(this.buyerForm.value));

// // // //   //   // Fix buyer company type
// // // //   //   if (formValue.companyType === 'Others' && formValue.otherCompanyType) {
// // // //   //     formValue.companyType = formValue.otherCompanyType;
// // // //   //   }
// // // //   //   delete formValue.otherCompanyType;

// // // //   //   // Process locations and their departments
// // // //   //   const processedLocations: any[] = [];
    
// // // //   //   if (formValue.locations && Array.isArray(formValue.locations)) {
// // // //   //     formValue.locations.forEach((loc: any) => {
// // // //   //       // Fix location type
// // // //   //       if (loc.locationType === 'Others' && loc.otherLocationType) {
// // // //   //         loc.locationType = loc.otherLocationType;
// // // //   //       }
// // // //   //       delete loc.otherLocationType;
        
// // // //   //       // Initialize departments for this location
// // // //   //       loc.departments = [];
// // // //   //       processedLocations.push(loc);
// // // //   //     });
// // // //   //   }

// // // //   //   // Map departments to their respective locations
// // // //   //   if (formValue.departments && Array.isArray(formValue.departments)) {
// // // //   //     formValue.departments.forEach((dept: any) => {
// // // //   //       const locationIndex = dept.locationIndex;
        
// // // //   //       // Validate locationIndex
// // // //   //       if (locationIndex === '' || locationIndex === null || locationIndex === undefined) {
// // // //   //         console.warn('Department missing location assignment:', dept);
// // // //   //         return;
// // // //   //       }

// // // //   //       const locationIdx = parseInt(locationIndex, 10);
        
// // // //   //       if (processedLocations[locationIdx]) {
// // // //   //         // Remove locationIndex from department before adding to location
// // // //   //         const { locationIndex: _, ...deptData } = dept;
// // // //   //         deptData.users = [];
// // // //   //         processedLocations[locationIdx].departments.push(deptData);
// // // //   //       }
// // // //   //     });
// // // //   //   }

// // // //   //   // Map users to their respective departments
// // // //   //   if (formValue.users && Array.isArray(formValue.users)) {
// // // //   //     formValue.users.forEach((user: any) => {
// // // //   //       const deptIndex = user.departmentIndex;
        
// // // //   //       // Validate departmentIndex
// // // //   //       if (deptIndex === '' || deptIndex === null || deptIndex === undefined) {
// // // //   //         console.warn('User missing department assignment:', user);
// // // //   //         return;
// // // //   //       }

// // // //   //       const deptIdx = parseInt(deptIndex, 10);
// // // //   //       const department = formValue.departments?.[deptIdx];
        
// // // //   //       if (!department) {
// // // //   //         console.warn('Department not found for user:', user);
// // // //   //         return;
// // // //   //       }

// // // //   //       const locationIndex = department.locationIndex;
// // // //   //       const locationIdx = parseInt(locationIndex, 10);

// // // //   //       if (processedLocations[locationIdx]) {
// // // //   //         // Find the target department in the location
// // // //   //         const targetDept = processedLocations[locationIdx].departments.find(
// // // //   //           (d: any) => d.departmentName === department.departmentName
// // // //   //         );

// // // //   //         if (targetDept) {
// // // //   //           // Remove departmentIndex from user before adding to department
// // // //   //           const { departmentIndex: _, ...userData } = user;
// // // //   //           targetDept.users.push(userData);
// // // //   //         }
// // // //   //       }
// // // //   //     });
// // // //   //   }

// // // //   //   // Replace locations with processed ones
// // // //   //   formValue.locations = processedLocations;
    
// // // //   //   // Remove departments and users arrays from root (they're now nested)
// // // //   //   delete formValue.departments;
// // // //   //   delete formValue.users;

// // // //   //   const payload: Buyer = formValue;

// // // //   //   console.log('%c[FINAL PAYLOAD]', 'color: #ff6600; font-weight: bold; font-size: 14px;', payload);

// // // //   //   if (this.mode === 'create') {
// // // //   //     this.buyerService.createCompleteHierarchy(payload).subscribe({
// // // //   //       next: () => {
// // // //   //         this.messageService.showMessage('success', 'Success', 'Buyer created successfully');
// // // //   //         setTimeout(() => this.router.navigate(['/dashboard']), 1500);
// // // //   //       },
// // // //   //       error: (err: any) => {
// // // //   //         console.error('Create error:', err);
// // // //   //         const errorMsg = err.error?.message || err.message || 'Failed to create buyer';
// // // //   //         this.messageService.showMessage('error', 'Error', errorMsg);
// // // //   //         this.isSubmitting = false;
// // // //   //       }
// // // //   //     });
// // // //   //   } else {
// // // //   //     this.buyerService.updateCompleteHierarchy(this.buyerId!, payload).subscribe({
// // // //   //       next: () => {
// // // //   //         this.messageService.showMessage('success', 'Success', 'Buyer updated successfully');
// // // //   //         setTimeout(() => this.router.navigate(['/dashboard']), 1500);
// // // //   //       },
// // // //   //       error: (err: any) => {
// // // //   //         console.error('Update error:', err);
// // // //   //         const errorMsg = err.error?.message || err.message || 'Failed to update buyer';
// // // //   //         this.messageService.showMessage('error', 'Error', errorMsg);
// // // //   //         this.isSubmitting = false;
// // // //   //       }
// // // //   //     });
// // // //   //   }
// // // //   // }

// // // //   // ==================== CRITICAL FIX: onSubmit() method ====================
// // // // // Replace your existing onSubmit() method with this:

// // // // onSubmit(): void {
// // // //   if (this.buyerForm.invalid) {
// // // //     this.buyerForm.markAllAsTouched();
// // // //     this.messageService.showMessage('error', 'Validation Error', 'Please fill all required fields');
// // // //     return;
// // // //   }

// // // //   this.isSubmitting = true;
  
// // // //   // Deep clone form value
// // // //   const formValue = JSON.parse(JSON.stringify(this.buyerForm.value));

// // // //   // ✅ FIX 1: Handle "Others" company type
// // // //   if (formValue.companyType === 'Others' && formValue.otherCompanyType) {
// // // //     formValue.companyType = formValue.otherCompanyType;
// // // //   }
// // // //   delete formValue.otherCompanyType;

// // // //   // ✅ FIX 2: Process locations with proper structure
// // // //   const processedLocations: any[] = [];
  
// // // //   if (formValue.locations && Array.isArray(formValue.locations)) {
// // // //     formValue.locations.forEach((loc: any) => {
// // // //       // Handle "Others" location type
// // // //       if (loc.locationType === 'Others' && loc.otherLocationType) {
// // // //         loc.locationType = loc.otherLocationType;
// // // //       }
// // // //       delete loc.otherLocationType;
      
// // // //       // ✅ CRITICAL: Initialize departments array for each location
// // // //       loc.departments = [];
// // // //       processedLocations.push(loc);
// // // //     });
// // // //   }

// // // //   // ✅ FIX 3: Map departments to their respective locations
// // // //   if (formValue.departments && Array.isArray(formValue.departments)) {
// // // //     formValue.departments.forEach((dept: any) => {
// // // //       const locationIndex = dept.locationIndex;
      
// // // //       // Validate locationIndex exists
// // // //       if (locationIndex === '' || locationIndex === null || locationIndex === undefined) {
// // // //         console.warn('⚠️ Department missing location assignment:', dept);
// // // //         return;
// // // //       }

// // // //       const locationIdx = parseInt(locationIndex, 10);
      
// // // //       if (processedLocations[locationIdx]) {
// // // //         // ✅ CRITICAL: Remove locationIndex before adding to location
// // // //         const { locationIndex: _, ...deptData } = dept;
        
// // // //         // Initialize users array for department
// // // //         deptData.users = [];
        
// // // //         processedLocations[locationIdx].departments.push(deptData);
// // // //       } else {
// // // //         console.warn(`⚠️ Location index ${locationIdx} not found for department:`, dept);
// // // //       }
// // // //     });
// // // //   }

// // // //   // ✅ FIX 4: Map users to their respective departments
// // // //   if (formValue.users && Array.isArray(formValue.users)) {
// // // //     formValue.users.forEach((user: any) => {
// // // //       const deptIndex = user.departmentIndex;
      
// // // //       // Validate departmentIndex exists
// // // //       if (deptIndex === '' || deptIndex === null || deptIndex === undefined) {
// // // //         console.warn('⚠️ User missing department assignment:', user);
// // // //         return;
// // // //       }

// // // //       const deptIdx = parseInt(deptIndex, 10);
// // // //       const department = formValue.departments?.[deptIdx];
      
// // // //       if (!department) {
// // // //         console.warn(`⚠️ Department index ${deptIdx} not found for user:`, user);
// // // //         return;
// // // //       }

// // // //       const locationIndex = department.locationIndex;
// // // //       const locationIdx = parseInt(locationIndex, 10);

// // // //       if (processedLocations[locationIdx]) {
// // // //         // Find the target department in the location
// // // //         const targetDept = processedLocations[locationIdx].departments.find(
// // // //           (d: any) => d.departmentName === department.departmentName
// // // //         );

// // // //         if (targetDept) {
// // // //           // ✅ CRITICAL: Remove departmentIndex before adding to department
// // // //           const { departmentIndex: _, ...userData } = user;
          
// // // //           // ✅ FIX 5: Handle password for edit mode
// // // //           if (this.mode === 'edit' && (!userData.password || userData.password.trim() === '')) {
// // // //             delete userData.password; // Don't send empty password on update
// // // //           }
          
// // // //           targetDept.users.push(userData);
// // // //         } else {
// // // //           console.warn('⚠️ Target department not found for user:', user);
// // // //         }
// // // //       } else {
// // // //         console.warn(`⚠️ Location index ${locationIdx} not found for user:`, user);
// // // //       }
// // // //     });
// // // //   }

// // // //   // ✅ FIX 6: Replace locations with processed nested structure
// // // //   formValue.locations = processedLocations;
  
// // // //   // ✅ FIX 7: Remove flat departments and users arrays from root
// // // //   delete formValue.departments;
// // // //   delete formValue.users;

// // // //   const payload: Buyer = formValue;

// // // //   console.log('%c[FINAL PAYLOAD]', 'color: #ff6600; font-weight: bold; font-size: 14px;', 
// // // //     JSON.stringify(payload, null, 2));

// // // //   // ✅ FIX 8: Use correct service methods
// // // //   if (this.mode === 'create') {
// // // //     this.buyerService.createCompleteHierarchy(payload).subscribe({
// // // //       next: (response) => {
// // // //         console.log('✅ Create response:', response);
// // // //         this.messageService.showMessage('success', 'Success', 'Buyer created successfully');
// // // //         setTimeout(() => this.router.navigate(['/dashboard']), 1500);
// // // //       },
// // // //       error: (err: any) => {
// // // //         console.error('❌ Create error:', err);
// // // //         const errorMsg = err.error?.message || err.message || 'Failed to create buyer';
// // // //         this.messageService.showMessage('error', 'Error', errorMsg);
// // // //         this.isSubmitting = false;
// // // //       }
// // // //     });
// // // //   } else if (this.buyerId) {
// // // //     this.buyerService.updateCompleteHierarchy(this.buyerId, payload).subscribe({
// // // //       next: (response) => {
// // // //         console.log('✅ Update response:', response);
// // // //         this.messageService.showMessage('success', 'Success', 'Buyer updated successfully');
// // // //         setTimeout(() => this.router.navigate(['/dashboard']), 1500);
// // // //       },
// // // //       error: (err: any) => {
// // // //         console.error('❌ Update error:', err);
// // // //         const errorMsg = err.error?.message || err.message || 'Failed to update buyer';
// // // //         this.messageService.showMessage('error', 'Error', errorMsg);
// // // //         this.isSubmitting = false;
// // // //       }
// // // //     });
// // // //   }
// // // // }
// // // // }


// // // import { Component, OnInit } from '@angular/core';
// // // import { FormBuilder, FormGroup, Validators, FormArray } from '@angular/forms';
// // // import { Router, ActivatedRoute } from '@angular/router';
// // // import { CommonModule } from '@angular/common';
// // // import { ReactiveFormsModule } from '@angular/forms';
// // // import { BuyerService } from '../dashboard/buyer-b.service';
// // // import { MessageService } from '../../../shared/service/message.service';
// // // import { Buyer, Location, Department, User } from '../dashboard/buyer-b.model';

// // // @Component({
// // //   selector: 'app-create-b',
// // //   templateUrl: './create-b.component.html',
// // //   styleUrls: ['./create-b.component.css'],
// // //   standalone: true,
// // //   imports: [CommonModule, ReactiveFormsModule]
// // // })
// // // export class CreateBComponent implements OnInit {

// // //   buyerForm!: FormGroup;
// // //   currentStep = 1;
// // //   maxStep = 4;
// // //   isSubmitting = false;

// // //   mode: 'create' | 'edit' = 'create';
// // //   buyerId: number | null = null;

// // //   states = ['Andhra Pradesh', 'Arunachal Pradesh', 'Assam', 'Bihar', 'Chhattisgarh', 
// // //             'Goa', 'Gujarat', 'Haryana', 'Himachal Pradesh', 'Jharkhand', 'Karnataka',
// // //             'Kerala', 'Madhya Pradesh', 'Maharashtra', 'Manipur', 'Meghalaya', 'Mizoram',
// // //             'Nagaland', 'Odisha', 'Punjab', 'Rajasthan', 'Sikkim', 'Tamil Nadu', 'Telangana',
// // //             'Tripura', 'Uttar Pradesh', 'Uttarakhand', 'West Bengal'];

// // //   companyTypes = ['Manufacturing', 'Trading', 'Services', 'Distribution', 'Retail', 'Others'];
// // //   locationTypes = ['Branch', 'Regional', 'Warehouse', 'Service Center', 'Others'];

// // //   constructor(
// // //     private fb: FormBuilder,
// // //     private buyerService: BuyerService,
// // //     private messageService: MessageService,
// // //     private router: Router,
// // //     private route: ActivatedRoute
// // //   ) {}

// // //   ngOnInit(): void {
// // //     this.initializeForm();
// // //     this.checkEditMode();
// // //   }

// // //   private initializeForm(): void {
// // //     this.buyerForm = this.fb.group({
// // //       companyName: ['', [Validators.required, Validators.minLength(2)]],
// // //       companyType: ['', Validators.required],
// // //       otherCompanyType: [''],
// // //       contactPersonName: ['', [Validators.required, Validators.minLength(2)]],
// // //       contactPersonDesignation: ['', Validators.required],
// // //       contactPersonEmail: ['', [Validators.required, Validators.email]],
// // //       contactPersonPhone: ['', [Validators.required, Validators.pattern(/^[0-9]{10}$/)]],
// // //       addressLine1: ['', [Validators.required, Validators.minLength(5)]],
// // //       addressLine2: [''],
// // //       city: ['', Validators.required],
// // //       state: ['', Validators.required],
// // //       postalCode: ['', [Validators.required, Validators.pattern(/^[0-9]{6}$/)]],
// // //       country: ['India', Validators.required],
// // //       gstNumber: [''],
// // //       panNumber: [''],
// // //       cinNumber: [''],
// // //       website: [''],
// // //       locations: this.fb.array([], Validators.minLength(1)),
// // //       departments: this.fb.array([]),
// // //       users: this.fb.array([])
// // //     });

// // //     if (!this.buyerForm.get('locations')?.value?.length) {
// // //       this.addLocation();
// // //     }
// // //   }

// // //   private checkEditMode(): void {
// // //     this.route.paramMap.subscribe(params => {
// // //       const id = params.get('id');
// // //       if (id) {
// // //         this.mode = 'edit';
// // //         this.buyerId = +id;
// // //         this.currentStep = 1;
// // //         this.loadBuyerData(this.buyerId);
// // //       } else {
// // //         this.mode = 'create';
// // //         this.currentStep = 1;
// // //       }
// // //     });
// // //   }

// // //   get locations(): FormArray {
// // //     return this.buyerForm.get('locations') as FormArray;
// // //   }

// // //   get departments(): FormArray {
// // //     return this.buyerForm.get('departments') as FormArray;
// // //   }

// // //   get users(): FormArray {
// // //     return this.buyerForm.get('users') as FormArray;
// // //   }

// // //   // ==================== LOCATION METHODS ====================

// // //   addLocation(): void {
// // //     const locationGroup = this.fb.group({
// // //       id: [null],
// // //       locationName: ['', [Validators.required, Validators.minLength(2)]],
// // //       locationType: ['', Validators.required],
// // //       otherLocationType: [''],
// // //       locationContactName: ['', [Validators.required, Validators.minLength(2)]],
// // //       locationContactEmail: ['', [Validators.required, Validators.email]],
// // //       locationContactPhone: ['', [Validators.required, Validators.pattern(/^[0-9]{10}$/)]],
// // //       addressLine1: ['', [Validators.required, Validators.minLength(5)]],
// // //       addressLine2: [''],
// // //       city: ['', Validators.required],
// // //       state: ['', Validators.required],
// // //       postalCode: ['', [Validators.required, Validators.pattern(/^[0-9]{6}$/)]],
// // //       country: ['India', Validators.required],
// // //       landlineNumber: [''],
// // //       faxNumber: ['']
// // //     });
// // //     this.locations.push(locationGroup);
// // //   }

// // //   removeLocation(index: number): void {
// // //     if (this.locations.length > 1) {
// // //       this.locations.removeAt(index);
// // //     } else {
// // //       this.messageService.showMessage('warning', 'Warning', 'At least one location is required');
// // //     }
// // //   }

// // //   onLocationTypeChange(event: any, locationIndex: number): void {
// // //     const locationGroup = this.locations.at(locationIndex) as FormGroup;
// // //     const selectedValue = event.target.value;
// // //     const customTypeControl = locationGroup.get('otherLocationType');

// // //     if (selectedValue === 'Others') {
// // //       customTypeControl?.setValidators([Validators.required, Validators.minLength(2)]);
// // //       customTypeControl?.setValue('');
// // //     } else {
// // //       customTypeControl?.clearValidators();
// // //       customTypeControl?.setValue(null);
// // //     }
// // //     customTypeControl?.updateValueAndValidity();
// // //   }

// // //   // ==================== DEPARTMENT METHODS ====================

// // //   addDepartment(): void {
// // //     const departmentGroup = this.fb.group({
// // //       id: [null],
// // //       locationIndex: ['', Validators.required],
// // //       departmentName: ['', [Validators.required, Validators.minLength(2)]],
// // //       departmentDescription: ['']
// // //     });
// // //     this.departments.push(departmentGroup);
// // //   }

// // //   removeDepartment(deptIndex: number): void {
// // //     if (this.departments.length > 1) {
// // //       this.departments.removeAt(deptIndex);
// // //     } else {
// // //       this.messageService.showMessage('warning', 'Warning', 'At least one department is required');
// // //     }
// // //   }

// // //   getLocationNameForDepartment(deptIndex: number): string {
// // //     const dept = this.departments.at(deptIndex);
// // //     const locIndex = dept?.get('locationIndex')?.value;
// // //     if (locIndex !== '' && locIndex !== null && locIndex !== undefined) {
// // //       const location = this.locations.at(locIndex);
// // //       return location?.get('locationName')?.value || `Location ${locIndex + 1}`;
// // //     }
// // //     return '';
// // //   }

// // //   // ==================== USER METHODS ====================

// // //   addUser(userData?: Partial<User>): void {
// // //     const userGroup = this.fb.group({
// // //       id: [userData?.id || null],
// // //       departmentIndex: [userData ? (userData as any).departmentIndex : '', Validators.required],
// // //       firstName: [userData?.firstName || '', [Validators.required, Validators.minLength(2)]],
// // //       lastName: [userData?.lastName || '', [Validators.required, Validators.minLength(1)]],
// // //       email: [userData?.email || '', [Validators.required, Validators.email]],
// // //       phone: [userData?.phone || '', [Validators.required, Validators.pattern(/^[0-9]{10}$/)]],
// // //       designation: [userData?.designation || '', Validators.required],
// // //       employeeId: [userData?.employeeId || '', Validators.required],
// // //       gender: [userData?.gender || ''],
// // //       dateOfBirth: [userData?.dateOfBirth || ''],
// // //       addressLine1: [userData?.addressLine1 || ''],
// // //       addressLine2: [userData?.addressLine2 || ''],
// // //       city: [userData?.city || ''],
// // //       state: [userData?.state || ''],
// // //       postalCode: [userData?.postalCode || ''],
// // //       password: [userData?.password || '', [Validators.required, Validators.minLength(6)]]
// // //     });
// // //     this.users.push(userGroup);
// // //   }

// // //   removeUser(userIndex: number): void {
// // //     if (this.users.length > 1) {
// // //       this.users.removeAt(userIndex);
// // //     } else {
// // //       this.messageService.showMessage('warning', 'Warning', 'At least one user is required');
// // //     }
// // //   }

// // //   // ==================== COMPANY TYPE CHANGE ====================

// // //   onCompanyTypeChange(event: any): void {
// // //     const selectedValue = event.target.value;
// // //     const customTypeControl = this.buyerForm.get('otherCompanyType');

// // //     if (selectedValue === 'Others') {
// // //       customTypeControl?.setValidators([Validators.required, Validators.minLength(2)]);
// // //       customTypeControl?.setValue('');
// // //     } else {
// // //       customTypeControl?.clearValidators();
// // //       customTypeControl?.setValue(null);
// // //     }
// // //     customTypeControl?.updateValueAndValidity();
// // //   }

// // //   // ==================== LOAD BUYER DATA (EDIT MODE) ====================

// // //   private loadBuyerData(id: number): void {
// // //     console.log('🔵 Loading buyer data for ID:', id);
    
// // //     this.buyerService.getBuyerById(id).subscribe({
// // //       next: (buyer: any) => {
// // //         console.log('✅ Buyer data loaded:', buyer);

// // //         let companyTypeForForm = buyer.companyType;
// // //         let otherCompanyType = null;

// // //         if (buyer.companyType && !this.companyTypes.includes(buyer.companyType)) {
// // //           companyTypeForForm = 'Others';
// // //           otherCompanyType = buyer.companyType;
// // //         }

// // //         this.buyerForm.patchValue({
// // //           companyName: buyer.companyName,
// // //           companyType: companyTypeForForm,
// // //           otherCompanyType: otherCompanyType,
// // //           contactPersonName: buyer.contactPersonName,
// // //           contactPersonDesignation: buyer.contactPersonDesignation,
// // //           contactPersonEmail: buyer.contactPersonEmail,
// // //           contactPersonPhone: buyer.contactPersonPhone,
// // //           addressLine1: buyer.addressLine1,
// // //           addressLine2: buyer.addressLine2,
// // //           city: buyer.city,
// // //           state: buyer.state,
// // //           postalCode: buyer.postalCode,
// // //           country: buyer.country,
// // //           gstNumber: buyer.gstNumber,
// // //           panNumber: buyer.panNumber,
// // //           cinNumber: buyer.cinNumber,
// // //           website: buyer.website
// // //         });

// // //         this.onCompanyTypeChange({ target: { value: companyTypeForForm } });

// // //         // Clear arrays
// // //         while (this.locations.length) {
// // //           this.locations.removeAt(0);
// // //         }
// // //         while (this.departments.length) {
// // //           this.departments.removeAt(0);
// // //         }
// // //         while (this.users.length) {
// // //           this.users.removeAt(0);
// // //         }

// // //         if (buyer.locations && Array.isArray(buyer.locations)) {
// // //           this.populateLocations(buyer.locations);
// // //         }
// // //       },
// // //       error: (err: any) => {
// // //         console.error('❌ Error loading buyer:', err);
// // //         this.messageService.showMessage('error', 'Error', 'Failed to load buyer data');
// // //         setTimeout(() => this.router.navigate(['/dashboard']), 2000);
// // //       }
// // //     });
// // //   }

// // //   private populateLocations(locations: Location[]): void {
// // //     locations.forEach((loc: any, locIndex: number) => {
// // //       this.addLocation();

// // //       let locationTypeForForm = loc.locationType;
// // //       let otherLocationType = null;

// // //       if (loc.locationType && !this.locationTypes.includes(loc.locationType)) {
// // //         locationTypeForForm = 'Others';
// // //         otherLocationType = loc.locationType;
// // //       }

// // //       this.locations.at(locIndex).patchValue({
// // //         id: loc.id,
// // //         locationName: loc.locationName,
// // //         locationType: locationTypeForForm,
// // //         otherLocationType: otherLocationType,
// // //         locationContactName: loc.locationContactName,
// // //         locationContactEmail: loc.locationContactEmail,
// // //         locationContactPhone: loc.locationContactPhone,
// // //         addressLine1: loc.addressLine1,
// // //         addressLine2: loc.addressLine2,
// // //         city: loc.city,
// // //         state: loc.state,
// // //         postalCode: loc.postalCode,
// // //         country: loc.country,
// // //         landlineNumber: loc.landlineNumber,
// // //         faxNumber: loc.faxNumber
// // //       });

// // //       this.onLocationTypeChange({ target: { value: locationTypeForForm } }, locIndex);

// // //       if (loc.departments && loc.departments.length > 0) {
// // //         loc.departments.forEach((dept: any) => {
// // //           const deptIndex = this.departments.length;
// // //           this.addDepartment();

// // //           this.departments.at(deptIndex).patchValue({
// // //             id: dept.id,
// // //             locationIndex: locIndex,
// // //             departmentName: dept.departmentName,
// // //             departmentDescription: dept.departmentDescription
// // //           });

// // //           if (dept.users && dept.users.length > 0) {
// // //             dept.users.forEach((user: any) => {
// // //               this.addUser({
// // //                 id: user.id,
// // //                 departmentIndex: deptIndex,
// // //                 firstName: user.firstName,
// // //                 lastName: user.lastName,
// // //                 email: user.email,
// // //                 phone: user.phone,
// // //                 designation: user.designation,
// // //                 employeeId: user.employeeId,
// // //                 gender: user.gender,
// // //                 dateOfBirth: user.dateOfBirth,
// // //                 addressLine1: user.addressLine1,
// // //                 addressLine2: user.addressLine2,
// // //                 city: user.city,
// // //                 state: user.state,
// // //                 postalCode: user.postalCode,
// // //                 password: ''
// // //               });
// // //             });
// // //           }
// // //         });
// // //       }
// // //     });

// // //     console.log('✅ All locations populated');
// // //   }

// // //   // ==================== STEP VALIDATION ====================

// // //   private isCurrentStepValid(): boolean {
// // //     switch (this.currentStep) {
// // //       case 1:
// // //         return this.isBuyerDetailsValid();
// // //       case 2:
// // //         return this.locationsBasicsValid();
// // //       case 3:
// // //         return this.departmentsValid();
// // //       case 4:
// // //         return this.usersValid();
// // //       default:
// // //         return false;
// // //     }
// // //   }

// // //   private isBuyerDetailsValid(): boolean {
// // //     const fields = ['companyName', 'companyType', 'contactPersonName', 'contactPersonDesignation',
// // //       'contactPersonEmail', 'contactPersonPhone', 'addressLine1', 'city', 'state', 
// // //       'postalCode', 'country'];

// // //     const standardFieldsValid = fields.every(field => this.buyerForm.get(field)?.valid);

// // //     const companyTypeControl = this.buyerForm.get('companyType');
// // //     const otherCompanyTypeControl = this.buyerForm.get('otherCompanyType');

// // //     const otherFieldValid: boolean = companyTypeControl?.value === 'Others'
// // //       ? (otherCompanyTypeControl?.valid ?? false)
// // //       : true;

// // //     return standardFieldsValid && otherFieldValid;
// // //   }

// // //   private locationsBasicsValid(): boolean {
// // //     return this.locations.length > 0 && 
// // //            this.locations.controls.every(loc => {
// // //              const fields = ['locationName', 'locationType', 'locationContactName', 
// // //                              'locationContactEmail', 'locationContactPhone', 'addressLine1',
// // //                              'city', 'state', 'postalCode', 'country'];

// // //              const standardFieldsValid = fields.every(field => loc.get(field)?.valid);

// // //              const locationTypeControl = loc.get('locationType');
// // //              const otherLocationTypeControl = loc.get('otherLocationType');

// // //              const otherFieldValid: boolean = locationTypeControl?.value === 'Others'
// // //                ? (otherLocationTypeControl?.valid ?? false)
// // //                : true;

// // //              return standardFieldsValid && otherFieldValid;
// // //            });
// // //   }

// // //   private departmentsValid(): boolean {
// // //     return this.departments.length > 0 && this.departments.controls.every(dept => 
// // //       dept.get('departmentName')?.valid && dept.get('locationIndex')?.valid
// // //     );
// // //   }

// // //   private usersValid(): boolean {
// // //     return this.users.length > 0 && this.users.controls.every(user =>
// // //       user.get('firstName')?.valid &&
// // //       user.get('lastName')?.valid &&
// // //       user.get('email')?.valid &&
// // //       user.get('phone')?.valid &&
// // //       user.get('designation')?.valid &&
// // //       user.get('employeeId')?.valid &&
// // //       user.get('departmentIndex')?.valid &&
// // //       user.get('password')?.valid
// // //     );
// // //   }

// // //   // ==================== STEP NAVIGATION ====================

// // //   saveStep(): void {
// // //     if (!this.isCurrentStepValid()) {
// // //       this.messageService.showMessage('warning', 'Validation Error', 'Please fill all required fields on this step');
// // //       this.buyerForm.markAllAsTouched();
// // //       return;
// // //     }

// // //     this.messageService.showMessage('success', 'Saved', `Step ${this.currentStep} saved successfully`);
    
// // //     if (this.currentStep < this.maxStep) {
// // //       this.currentStep++;
// // //     }
// // //   }

// // //   prevStep(): void {
// // //     if (this.currentStep > 1) {
// // //       this.currentStep--;
// // //     }
// // //   }

// // //   nextStep(): void {
// // //     if (this.currentStep < this.maxStep) {
// // //       this.currentStep++;
// // //     }
// // //   }

// // //   // ==================== ✅ COMPLETELY FIXED SUBMIT METHOD ====================

// // //   onSubmit(): void {
// // //     console.log('%c[SUBMIT] Starting...', 'color: #ff6600; font-weight: bold;');
    
// // //     if (this.buyerForm.invalid) {
// // //       this.buyerForm.markAllAsTouched();
// // //       this.messageService.showMessage('error', 'Validation Error', 'Please fill all required fields');
// // //       return;
// // //     }

// // //     this.isSubmitting = true;
    
// // //     // Deep clone form value
// // //     const formValue = JSON.parse(JSON.stringify(this.buyerForm.value));

// // //     // ✅ FIX 1: Handle "Others" company type
// // //     if (formValue.companyType === 'Others' && formValue.otherCompanyType) {
// // //       formValue.companyType = formValue.otherCompanyType;
// // //     }
// // //     delete formValue.otherCompanyType;

// // //     // ✅ FIX 2: Process locations with proper structure
// // //     const processedLocations: any[] = [];
    
// // //     if (formValue.locations && Array.isArray(formValue.locations)) {
// // //       formValue.locations.forEach((loc: any) => {
// // //         // Handle "Others" location type
// // //         if (loc.locationType === 'Others' && loc.otherLocationType) {
// // //           loc.locationType = loc.otherLocationType;
// // //         }
// // //         delete loc.otherLocationType;
        
// // //         // ✅ CRITICAL: Initialize departments array for each location
// // //         loc.departments = [];
// // //         processedLocations.push(loc);
// // //       });
// // //     }

// // //     // ✅ FIX 3: Map departments to their respective locations
// // //     if (formValue.departments && Array.isArray(formValue.departments)) {
// // //       formValue.departments.forEach((dept: any, deptIdx: number) => {
// // //         const locationIndex = dept.locationIndex;
        
// // //         // Validate locationIndex
// // //         if (locationIndex === '' || locationIndex === null || locationIndex === undefined) {
// // //           console.warn(`⚠️ Department ${deptIdx} missing location assignment`);
// // //           return;
// // //         }

// // //         const locationIdx = parseInt(locationIndex, 10);
        
// // //         if (isNaN(locationIdx)) {
// // //           console.error(`❌ Invalid location index for department ${deptIdx}:`, locationIndex);
// // //           return;
// // //         }
        
// // //         if (processedLocations[locationIdx]) {
// // //           // ✅ CRITICAL: Remove locationIndex before adding to location
// // //           const { locationIndex: _, ...deptData } = dept;
          
// // //           // Initialize users array for department
// // //           deptData.users = [];
          
// // //           processedLocations[locationIdx].departments.push(deptData);
// // //           console.log(`✅ Mapped department "${deptData.departmentName}" to location ${locationIdx}`);
// // //         } else {
// // //           console.error(`❌ Location index ${locationIdx} not found for department ${deptIdx}`);
// // //         }
// // //       });
// // //     }

// // //     // ✅ FIX 4: Map users to their respective departments
// // //     if (formValue.users && Array.isArray(formValue.users)) {
// // //       formValue.users.forEach((user: any, userIdx: number) => {
// // //         const deptIndex = user.departmentIndex;
        
// // //         // Validate departmentIndex
// // //         if (deptIndex === '' || deptIndex === null || deptIndex === undefined) {
// // //           console.warn(`⚠️ User ${userIdx} missing department assignment`);
// // //           return;
// // //         }

// // //         const deptIdx = parseInt(deptIndex, 10);
        
// // //         if (isNaN(deptIdx)) {
// // //           console.error(`❌ Invalid department index for user ${userIdx}:`, deptIndex);
// // //           return;
// // //         }
        
// // //         const department = formValue.departments?.[deptIdx];
        
// // //         if (!department) {
// // //           console.error(`❌ Department index ${deptIdx} not found for user ${userIdx}`);
// // //           return;
// // //         }

// // //         const locationIndex = department.locationIndex;
// // //         const locationIdx = parseInt(locationIndex, 10);

// // //         if (isNaN(locationIdx)) {
// // //           console.error(`❌ Invalid location index in department ${deptIdx}`);
// // //           return;
// // //         }

// // //         if (processedLocations[locationIdx]) {
// // //           // Find the target department in the location
// // //           const targetDept = processedLocations[locationIdx].departments.find(
// // //             (d: any) => d.departmentName === department.departmentName
// // //           );

// // //           if (targetDept) {
// // //             // ✅ CRITICAL: Remove departmentIndex before adding to department
// // //             const { departmentIndex: _, ...userData } = user;
            
// // //             // ✅ FIX 5: Handle password for edit mode
// // //             if (this.mode === 'edit' && (!userData.password || userData.password.trim() === '')) {
// // //               delete userData.password;
// // //             }
            
// // //             targetDept.users.push(userData);
// // //             console.log(`✅ Mapped user "${userData.firstName} ${userData.lastName}" to department "${targetDept.departmentName}"`);
// // //           } else {
// // //             console.error(`❌ Target department not found in location ${locationIdx} for user ${userIdx}`);
// // //           }
// // //         } else {
// // //           console.error(`❌ Location index ${locationIdx} not found for user ${userIdx}`);
// // //         }
// // //       });
// // //     }

// // //     // ✅ FIX 6: Replace locations with processed nested structure
// // //     formValue.locations = processedLocations;
    
// // //     // ✅ FIX 7: Remove flat departments and users arrays from root
// // //     delete formValue.departments;
// // //     delete formValue.users;

// // //     const payload: Buyer = formValue;

// // //     console.log('%c[FINAL PAYLOAD]', 'color: #00aa00; font-weight: bold; font-size: 14px;');
// // //     console.log(JSON.stringify(payload, null, 2));

// // //     // ✅ FIX 8: Submit to backend
// // //     if (this.mode === 'create') {
// // //       console.log('%c[CREATE] Submitting...', 'color: #0066cc;');
      
// // //       this.buyerService.createCompleteHierarchy(payload).subscribe({
// // //         next: (response) => {
// // //           console.log('✅ Create response:', response);
// // //           this.messageService.showMessage('success', 'Success', 'Buyer created successfully');
// // //           setTimeout(() => this.router.navigate(['/dashboard']), 1500);
// // //         },
// // //         error: (err: any) => {
// // //           console.error('❌ Create error:', err);
// // //           const errorMsg = err.error?.message || err.message || 'Failed to create buyer';
// // //           this.messageService.showMessage('error', 'Error', errorMsg);
// // //           this.isSubmitting = false;
// // //         }
// // //       });
// // //     } else if (this.buyerId) {
// // //       console.log('%c[UPDATE] Submitting...', 'color: #ff9800;');
      
// // //       this.buyerService.updateCompleteHierarchy(this.buyerId, payload).subscribe({
// // //         next: (response) => {
// // //           console.log('✅ Update response:', response);
// // //           this.messageService.showMessage('success', 'Success', 'Buyer updated successfully');
// // //           setTimeout(() => this.router.navigate(['/dashboard']), 1500);
// // //         },
// // //         error: (err: any) => {
// // //           console.error('❌ Update error:', err);
// // //           const errorMsg = err.error?.message || err.message || 'Failed to update buyer';
// // //           this.messageService.showMessage('error', 'Error', errorMsg);
// // //           this.isSubmitting = false;
// // //         }
// // //       });
// // //     }
// // //   }
// // // }

// // import { Component, OnInit } from '@angular/core';
// // import { FormBuilder, FormGroup, Validators, FormArray } from '@angular/forms';
// // import { Router, ActivatedRoute } from '@angular/router';
// // import { CommonModule } from '@angular/common';
// // import { ReactiveFormsModule } from '@angular/forms';
// // import { BuyerService } from '../dashboard/buyer-b.service';
// // import { MessageService } from '../../../shared/service/message.service';
// // import { Buyer, Location, Department, User } from '../dashboard/buyer-b.model';

// // @Component({
// //   selector: 'app-create-b',
// //   templateUrl: './create-b.component.html',
// //   styleUrls: ['./create-b.component.css'],
// //   standalone: true,
// //   imports: [CommonModule, ReactiveFormsModule]
// // })
// // export class CreateBComponent implements OnInit {

// //   buyerForm!: FormGroup;
// //   currentStep = 1;
// //   maxStep = 4;
// //   isSubmitting = false;

// //   mode: 'create' | 'edit' = 'create';
// //   buyerId: number | null = null;

// //   states = ['Andhra Pradesh', 'Arunachal Pradesh', 'Assam', 'Bihar', 'Chhattisgarh', 
// //             'Goa', 'Gujarat', 'Haryana', 'Himachal Pradesh', 'Jharkhand', 'Karnataka',
// //             'Kerala', 'Madhya Pradesh', 'Maharashtra', 'Manipur', 'Meghalaya', 'Mizoram',
// //             'Nagaland', 'Odisha', 'Punjab', 'Rajasthan', 'Sikkim', 'Tamil Nadu', 'Telangana',
// //             'Tripura', 'Uttar Pradesh', 'Uttarakhand', 'West Bengal'];

// //   companyTypes = ['Manufacturing', 'Trading', 'Services', 'Distribution', 'Retail', 'Others'];
// //   locationTypes = ['Branch', 'Regional', 'Warehouse', 'Service Center', 'Others'];

// //   constructor(
// //     private fb: FormBuilder,
// //     private buyerService: BuyerService,
// //     private messageService: MessageService,
// //     private router: Router,
// //     private route: ActivatedRoute
// //   ) {}

// //   ngOnInit(): void {
// //     this.initializeForm();
// //     this.checkEditMode();
// //   }

// //   private initializeForm(): void {
// //     this.buyerForm = this.fb.group({
// //       companyName: ['', [Validators.required, Validators.minLength(2)]],
// //       companyType: ['', Validators.required],
// //       otherCompanyType: [''],
// //       contactPersonName: ['', [Validators.required, Validators.minLength(2)]],
// //       contactPersonDesignation: ['', Validators.required],
// //       contactPersonEmail: ['', [Validators.required, Validators.email]],
// //       contactPersonPhone: ['', [Validators.required, Validators.pattern(/^[0-9]{10}$/)]],
// //       addressLine1: ['', [Validators.required, Validators.minLength(5)]],
// //       addressLine2: [''],
// //       city: ['', Validators.required],
// //       state: ['', Validators.required],
// //       postalCode: ['', [Validators.required, Validators.pattern(/^[0-9]{6}$/)]],
// //       country: ['India', Validators.required],
// //       gstNumber: [''],
// //       panNumber: [''],
// //       cinNumber: [''],
// //       website: [''],
// //       locations: this.fb.array([], Validators.minLength(1)),
// //       departments: this.fb.array([]),
// //       users: this.fb.array([])
// //     });

// //     if (!this.buyerForm.get('locations')?.value?.length) {
// //       this.addLocation();
// //     }
// //   }

// //   private checkEditMode(): void {
// //     this.route.paramMap.subscribe(params => {
// //       const id = params.get('id');
// //       if (id) {
// //         this.mode = 'edit';
// //         this.buyerId = +id;
// //         this.currentStep = 1;
// //         this.loadBuyerData(this.buyerId);
// //       } else {
// //         this.mode = 'create';
// //         this.currentStep = 1;
// //       }
// //     });
// //   }

// //   get locations(): FormArray {
// //     return this.buyerForm.get('locations') as FormArray;
// //   }

// //   get departments(): FormArray {
// //     return this.buyerForm.get('departments') as FormArray;
// //   }

// //   get users(): FormArray {
// //     return this.buyerForm.get('users') as FormArray;
// //   }

// //   // ==================== LOCATION METHODS ====================

// //   addLocation(): void {
// //     const locationGroup = this.fb.group({
// //       id: [null],
// //       locationName: ['', [Validators.required, Validators.minLength(2)]],
// //       locationType: ['', Validators.required],
// //       otherLocationType: [''],
// //       locationContactName: ['', [Validators.required, Validators.minLength(2)]],
// //       locationContactEmail: ['', [Validators.required, Validators.email]],
// //       locationContactPhone: ['', [Validators.required, Validators.pattern(/^[0-9]{10}$/)]],
// //       addressLine1: ['', [Validators.required, Validators.minLength(5)]],
// //       addressLine2: [''],
// //       city: ['', Validators.required],
// //       state: ['', Validators.required],
// //       postalCode: ['', [Validators.required, Validators.pattern(/^[0-9]{6}$/)]],
// //       country: ['India', Validators.required],
// //       landlineNumber: [''],
// //       faxNumber: ['']
// //     });
// //     this.locations.push(locationGroup);
// //   }

// //   removeLocation(index: number): void {
// //     if (this.locations.length > 1) {
// //       this.locations.removeAt(index);
// //     } else {
// //       this.messageService.showMessage('warning', 'Warning', 'At least one location is required');
// //     }
// //   }

// //   onLocationTypeChange(event: any, locationIndex: number): void {
// //     const locationGroup = this.locations.at(locationIndex) as FormGroup;
// //     const selectedValue = event.target.value;
// //     const customTypeControl = locationGroup.get('otherLocationType');

// //     if (selectedValue === 'Others') {
// //       customTypeControl?.setValidators([Validators.required, Validators.minLength(2)]);
// //       customTypeControl?.setValue('');
// //     } else {
// //       customTypeControl?.clearValidators();
// //       customTypeControl?.setValue(null);
// //     }
// //     customTypeControl?.updateValueAndValidity();
// //   }

// //   // ==================== DEPARTMENT METHODS ====================

// //   addDepartment(): void {
// //     const departmentGroup = this.fb.group({
// //       id: [null],
// //       locationIndex: ['', Validators.required],
// //       departmentName: ['', [Validators.required, Validators.minLength(2)]],
// //       departmentDescription: ['']
// //     });
// //     this.departments.push(departmentGroup);
// //   }

// //   removeDepartment(deptIndex: number): void {
// //     if (this.departments.length > 1) {
// //       this.departments.removeAt(deptIndex);
// //     } else {
// //       this.messageService.showMessage('warning', 'Warning', 'At least one department is required');
// //     }
// //   }

// //   getLocationNameForDepartment(deptIndex: number): string {
// //     const dept = this.departments.at(deptIndex);
// //     const locIndex = dept?.get('locationIndex')?.value;
// //     if (locIndex !== '' && locIndex !== null && locIndex !== undefined) {
// //       const location = this.locations.at(locIndex);
// //       return location?.get('locationName')?.value || `Location ${locIndex + 1}`;
// //     }
// //     return '';
// //   }

// //   // ==================== USER METHODS ====================

// //   addUser(userData?: Partial<User>): void {
// //     const userGroup = this.fb.group({
// //       id: [userData?.id || null],
// //       departmentIndex: [userData ? (userData as any).departmentIndex : '', Validators.required],
// //       firstName: [userData?.firstName || '', [Validators.required, Validators.minLength(2)]],
// //       lastName: [userData?.lastName || '', [Validators.required, Validators.minLength(1)]],
// //       email: [userData?.email || '', [Validators.required, Validators.email]],
// //       phone: [userData?.phone || '', [Validators.required, Validators.pattern(/^[0-9]{10}$/)]],
// //       designation: [userData?.designation || '', Validators.required],
// //       employeeId: [userData?.employeeId || '', Validators.required],
// //       gender: [userData?.gender || ''],
// //       dateOfBirth: [userData?.dateOfBirth || ''],
// //       addressLine1: [userData?.addressLine1 || ''],
// //       addressLine2: [userData?.addressLine2 || ''],
// //       city: [userData?.city || ''],
// //       state: [userData?.state || ''],
// //       postalCode: [userData?.postalCode || ''],
// //       password: [userData?.password || '', [Validators.required, Validators.minLength(6)]]
// //     });
// //     this.users.push(userGroup);
// //   }

// //   removeUser(userIndex: number): void {
// //     if (this.users.length > 1) {
// //       this.users.removeAt(userIndex);
// //     } else {
// //       this.messageService.showMessage('warning', 'Warning', 'At least one user is required');
// //     }
// //   }

// //   // ==================== COMPANY TYPE CHANGE ====================

// //   onCompanyTypeChange(event: any): void {
// //     const selectedValue = event.target.value;
// //     const customTypeControl = this.buyerForm.get('otherCompanyType');

// //     if (selectedValue === 'Others') {
// //       customTypeControl?.setValidators([Validators.required, Validators.minLength(2)]);
// //       customTypeControl?.setValue('');
// //     } else {
// //       customTypeControl?.clearValidators();
// //       customTypeControl?.setValue(null);
// //     }
// //     customTypeControl?.updateValueAndValidity();
// //   }

// //   // ==================== LOAD BUYER DATA (EDIT MODE) ====================

// //   private loadBuyerData(id: number): void {
// //     console.log('🔵 Loading buyer data for ID:', id);
    
// //     this.buyerService.getBuyerById(id).subscribe({
// //       next: (buyer: any) => {
// //         console.log('✅ Buyer data loaded:', buyer);

// //         let companyTypeForForm = buyer.companyType;
// //         let otherCompanyType = null;

// //         if (buyer.companyType && !this.companyTypes.includes(buyer.companyType)) {
// //           companyTypeForForm = 'Others';
// //           otherCompanyType = buyer.companyType;
// //         }

// //         this.buyerForm.patchValue({
// //           companyName: buyer.companyName,
// //           companyType: companyTypeForForm,
// //           otherCompanyType: otherCompanyType,
// //           contactPersonName: buyer.contactPersonName,
// //           contactPersonDesignation: buyer.contactPersonDesignation,
// //           contactPersonEmail: buyer.contactPersonEmail,
// //           contactPersonPhone: buyer.contactPersonPhone,
// //           addressLine1: buyer.addressLine1,
// //           addressLine2: buyer.addressLine2,
// //           city: buyer.city,
// //           state: buyer.state,
// //           postalCode: buyer.postalCode,
// //           country: buyer.country,
// //           gstNumber: buyer.gstNumber,
// //           panNumber: buyer.panNumber,
// //           cinNumber: buyer.cinNumber,
// //           website: buyer.website
// //         });

// //         this.onCompanyTypeChange({ target: { value: companyTypeForForm } });

// //         // Clear arrays
// //         while (this.locations.length) {
// //           this.locations.removeAt(0);
// //         }
// //         while (this.departments.length) {
// //           this.departments.removeAt(0);
// //         }
// //         while (this.users.length) {
// //           this.users.removeAt(0);
// //         }

// //         if (buyer.locations && Array.isArray(buyer.locations)) {
// //           this.populateLocations(buyer.locations);
// //         }
// //       },
// //       error: (err: any) => {
// //         console.error('❌ Error loading buyer:', err);
// //         this.messageService.showMessage('error', 'Error', 'Failed to load buyer data');
// //         setTimeout(() => this.router.navigate(['/dashboard']), 2000);
// //       }
// //     });
// //   }

// //   private populateLocations(locations: Location[]): void {
// //     locations.forEach((loc: any, locIndex: number) => {
// //       this.addLocation();

// //       let locationTypeForForm = loc.locationType;
// //       let otherLocationType = null;

// //       if (loc.locationType && !this.locationTypes.includes(loc.locationType)) {
// //         locationTypeForForm = 'Others';
// //         otherLocationType = loc.locationType;
// //       }

// //       this.locations.at(locIndex).patchValue({
// //         id: loc.id,
// //         locationName: loc.locationName,
// //         locationType: locationTypeForForm,
// //         otherLocationType: otherLocationType,
// //         locationContactName: loc.locationContactName,
// //         locationContactEmail: loc.locationContactEmail,
// //         locationContactPhone: loc.locationContactPhone,
// //         addressLine1: loc.addressLine1,
// //         addressLine2: loc.addressLine2,
// //         city: loc.city,
// //         state: loc.state,
// //         postalCode: loc.postalCode,
// //         country: loc.country,
// //         landlineNumber: loc.landlineNumber,
// //         faxNumber: loc.faxNumber
// //       });

// //       this.onLocationTypeChange({ target: { value: locationTypeForForm } }, locIndex);

// //       if (loc.departments && loc.departments.length > 0) {
// //         loc.departments.forEach((dept: any) => {
// //           const deptIndex = this.departments.length;
// //           this.addDepartment();

// //           this.departments.at(deptIndex).patchValue({
// //             id: dept.id,
// //             locationIndex: locIndex,
// //             departmentName: dept.departmentName,
// //             departmentDescription: dept.departmentDescription
// //           });

// //           if (dept.users && dept.users.length > 0) {
// //             dept.users.forEach((user: any) => {
// //               this.addUser({
// //                 id: user.id,
// //                 departmentIndex: deptIndex,
// //                 firstName: user.firstName,
// //                 lastName: user.lastName,
// //                 email: user.email,
// //                 phone: user.phone,
// //                 designation: user.designation,
// //                 employeeId: user.employeeId,
// //                 gender: user.gender,
// //                 dateOfBirth: user.dateOfBirth,
// //                 addressLine1: user.addressLine1,
// //                 addressLine2: user.addressLine2,
// //                 city: user.city,
// //                 state: user.state,
// //                 postalCode: user.postalCode,
// //                 password: ''
// //               });
// //             });
// //           }
// //         });
// //       }
// //     });

// //     console.log('✅ All locations populated');
// //   }

// //   // ==================== STEP VALIDATION ====================

// //   private isCurrentStepValid(): boolean {
// //     switch (this.currentStep) {
// //       case 1:
// //         return this.isBuyerDetailsValid();
// //       case 2:
// //         return this.locationsBasicsValid();
// //       case 3:
// //         return this.departmentsValid();
// //       case 4:
// //         return this.usersValid();
// //       default:
// //         return false;
// //     }
// //   }

// //   private isBuyerDetailsValid(): boolean {
// //     const fields = ['companyName', 'companyType', 'contactPersonName', 'contactPersonDesignation',
// //       'contactPersonEmail', 'contactPersonPhone', 'addressLine1', 'city', 'state', 
// //       'postalCode', 'country'];

// //     const standardFieldsValid = fields.every(field => this.buyerForm.get(field)?.valid);

// //     const companyTypeControl = this.buyerForm.get('companyType');
// //     const otherCompanyTypeControl = this.buyerForm.get('otherCompanyType');

// //     const otherFieldValid: boolean = companyTypeControl?.value === 'Others'
// //       ? (otherCompanyTypeControl?.valid ?? false)
// //       : true;

// //     return standardFieldsValid && otherFieldValid;
// //   }

// //   private locationsBasicsValid(): boolean {
// //     return this.locations.length > 0 && 
// //            this.locations.controls.every(loc => {
// //              const fields = ['locationName', 'locationType', 'locationContactName', 
// //                              'locationContactEmail', 'locationContactPhone', 'addressLine1',
// //                              'city', 'state', 'postalCode', 'country'];

// //              const standardFieldsValid = fields.every(field => loc.get(field)?.valid);

// //              const locationTypeControl = loc.get('locationType');
// //              const otherLocationTypeControl = loc.get('otherLocationType');

// //              const otherFieldValid: boolean = locationTypeControl?.value === 'Others'
// //                ? (otherLocationTypeControl?.valid ?? false)
// //                : true;

// //              return standardFieldsValid && otherFieldValid;
// //            });
// //   }

// //   private departmentsValid(): boolean {
// //     return this.departments.length > 0 && this.departments.controls.every(dept => 
// //       dept.get('departmentName')?.valid && dept.get('locationIndex')?.valid
// //     );
// //   }

// //   private usersValid(): boolean {
// //     return this.users.length > 0 && this.users.controls.every(user =>
// //       user.get('firstName')?.valid &&
// //       user.get('lastName')?.valid &&
// //       user.get('email')?.valid &&
// //       user.get('phone')?.valid &&
// //       user.get('designation')?.valid &&
// //       user.get('employeeId')?.valid &&
// //       user.get('departmentIndex')?.valid &&
// //       user.get('password')?.valid
// //     );
// //   }

// //   // ==================== STEP NAVIGATION ====================

// //   saveStep(): void {
// //     if (!this.isCurrentStepValid()) {
// //       this.messageService.showMessage('warning', 'Validation Error', 'Please fill all required fields on this step');
// //       this.buyerForm.markAllAsTouched();
// //       return;
// //     }

// //     this.messageService.showMessage('success', 'Saved', `Step ${this.currentStep} saved successfully`);
    
// //     if (this.currentStep < this.maxStep) {
// //       this.currentStep++;
// //     }
// //   }

// //   prevStep(): void {
// //     if (this.currentStep > 1) {
// //       this.currentStep--;
// //     }
// //   }

// //   nextStep(): void {
// //     if (this.currentStep < this.maxStep) {
// //       this.currentStep++;
// //     }
// //   }

// //   // ==================== ✅ COMPLETELY FIXED SUBMIT METHOD ====================

// //   onSubmit(): void {
// //     console.log('========================================');
// //     console.log('🔵 SUBMITTING BUYER FORM');
// //     console.log('========================================');
    
// //     if (this.buyerForm.invalid) {
// //       console.error('❌ Form is invalid!');
// //       this.buyerForm.markAllAsTouched();
// //       this.messageService.showMessage('error', 'Validation Error', 'Please fill all required fields');
// //       return;
// //     }

// //     this.isSubmitting = true;
    
// //     // Get raw form value
// //     const formValue = this.buyerForm.getRawValue();
    
// //     console.log('📋 Raw form value:', formValue);
// //     console.log('');

// //     // ✅ STEP 1: Handle "Others" company type
// //     if (formValue.companyType === 'Others' && formValue.otherCompanyType) {
// //       formValue.companyType = formValue.otherCompanyType;
// //     }
// //     delete formValue.otherCompanyType;

// //     // ✅ STEP 2: Process locations and initialize empty departments arrays
// //     const processedLocations: any[] = [];
    
// //     console.log('📍 Processing locations...');
// //     if (formValue.locations && Array.isArray(formValue.locations)) {
// //       formValue.locations.forEach((loc: any, idx: number) => {
// //         console.log(`   Location ${idx}: ${loc.locationName}`);
        
// //         // Handle "Others" for location type
// //         if (loc.locationType === 'Others' && loc.otherLocationType) {
// //           loc.locationType = loc.otherLocationType;
// //         }
// //         delete loc.otherLocationType;
        
// //         // ✅ CRITICAL: Initialize empty departments array
// //         loc.departments = [];
        
// //         processedLocations.push(loc);
// //         console.log(`      ✅ Added with empty departments array`);
// //       });
// //     }

// //     console.log('');
// //     console.log('📂 Processing departments...');
    
// //     // ✅ STEP 3: Nest departments inside their locations
// //     if (formValue.departments && Array.isArray(formValue.departments)) {
// //       formValue.departments.forEach((dept: any, deptIdx: number) => {
// //         console.log(`   Department ${deptIdx}: ${dept.departmentName}`);
        
// //         const locationIndex = dept.locationIndex;
        
// //         // Validate location index
// //         if (locationIndex === '' || locationIndex === null || locationIndex === undefined) {
// //           console.warn(`      ⚠️  No location assignment - SKIPPING!`);
// //           return;
// //         }

// //         const locationIdx = parseInt(locationIndex, 10);
        
// //         if (isNaN(locationIdx)) {
// //           console.error(`      ❌ Invalid location index: ${locationIndex} - SKIPPING!`);
// //           return;
// //         }
        
// //         if (locationIdx < 0 || locationIdx >= processedLocations.length) {
// //           console.error(`      ❌ Location index ${locationIdx} out of range - SKIPPING!`);
// //           return;
// //         }

// //         // Remove locationIndex and create department with empty users array
// //         const { locationIndex: _, ...deptData } = dept;
// //         deptData.users = [];  // ✅ CRITICAL: Initialize empty users array
        
// //         // Add department to the correct location
// //         processedLocations[locationIdx].departments.push(deptData);
// //         console.log(`      ✅ Added to location ${locationIdx} (${processedLocations[locationIdx].locationName})`);
// //       });
// //     }

// //     console.log('');
// //     console.log('👥 Processing users...');
    
// //     // ✅ STEP 4: Nest users inside their departments
// //     if (formValue.users && Array.isArray(formValue.users)) {
// //       formValue.users.forEach((user: any, userIdx: number) => {
// //         console.log(`   User ${userIdx}: ${user.firstName} ${user.lastName}`);
        
// //         const deptIdx = parseInt(user.departmentIndex, 10);
        
// //         if (isNaN(deptIdx)) {
// //           console.error(`      ❌ Invalid department index - SKIPPING!`);
// //           return;
// //         }

// //         // Find the department across all locations
// //         let targetDepartment: any = null;
// //         let targetLocationName = '';
// //         let deptCounter = 0;

// //         for (const location of processedLocations) {
// //           for (const dept of location.departments) {
// //             if (deptCounter === deptIdx) {
// //               targetDepartment = dept;
// //               targetLocationName = location.locationName;
// //               break;
// //             }
// //             deptCounter++;
// //           }
// //           if (targetDepartment) break;
// //         }

// //         if (targetDepartment) {
// //           // Remove departmentIndex from user data
// //           const { departmentIndex: _, ...userData } = user;
          
// //           // ✅ Handle password for edit mode
// //           if (this.mode === 'edit' && (!userData.password || userData.password.trim() === '')) {
// //             delete userData.password;
// //           }
          
// //           // ✅ CRITICAL: Add user to the department's users array
// //           targetDepartment.users.push(userData);
// //           console.log(`      ✅ Added to department '${targetDepartment.departmentName}' in '${targetLocationName}'`);
// //         } else {
// //           console.error(`      ❌ Could not find department with index ${deptIdx} - SKIPPING!`);
// //         }
// //       });
// //     }

// //     // ✅ STEP 5: Build final payload with properly nested structure
// //     const payload = {
// //       ...formValue,
// //       locations: processedLocations
// //     };
    
// //     // Remove flat arrays from payload (they're now nested)
// //     delete payload.departments;
// //     delete payload.users;

// //     console.log('');
// //     console.log('========================================');
// //     console.log('📦 FINAL PAYLOAD TO BE SENT:');
// //     console.log('========================================');
// //     console.log(JSON.stringify(payload, null, 2));
// //     console.log('');
    
// //     // Verify structure
// //     console.log('🔍 PAYLOAD VERIFICATION:');
// //     console.log(`   Total Locations: ${payload.locations.length}`);
    
// //     let totalDepts = 0;
// //     let totalUsers = 0;
    
// //     payload.locations.forEach((loc: any, i: number) => {
// //       const deptCount = loc.departments ? loc.departments.length : 0;
// //       totalDepts += deptCount;
      
// //       console.log(`   Location ${i}: "${loc.locationName}"`);
// //       console.log(`      Departments: ${deptCount}`);
      
// //       if (loc.departments) {
// //         loc.departments.forEach((dept: any, j: number) => {
// //           const userCount = dept.users ? dept.users.length : 0;
// //           totalUsers += userCount;
          
// //           console.log(`         Department ${j}: "${dept.departmentName}"`);
// //           console.log(`            Users: ${userCount}`);
          
// //           if (dept.users && dept.users.length > 0) {
// //             dept.users.forEach((usr: any, k: number) => {
// //               console.log(`               User ${k}: ${usr.firstName} ${usr.lastName} (${usr.email})`);
// //             });
// //           }
// //         });
// //       }
// //     });
    
// //     console.log('');
// //     console.log(`📊 TOTALS: ${payload.locations.length} locations, ${totalDepts} departments, ${totalUsers} users`);
// //     console.log('========================================');

// //     // ✅ STEP 6: Send to backend
// //     if (this.mode === 'create') {
// //       console.log('➕ Creating new buyer...');
      
// //       this.buyerService.createCompleteHierarchy(payload).subscribe({
// //         next: (response) => {
// //           console.log('✅ Buyer created successfully:', response);
// //           this.messageService.showMessage('success', 'Success', 'Buyer created successfully');
// //           this.isSubmitting = false;
// //           setTimeout(() => this.router.navigate(['/dashboard']), 1500);
// //         },
// //         error: (err: any) => {
// //           console.error('❌ Error creating buyer:', err);
// //           const errorMsg = err.error?.message || err.message || 'Failed to create buyer';
// //           this.messageService.showMessage('error', 'Error', errorMsg);
// //           this.isSubmitting = false;
// //         }
// //       });
// //     } else if (this.buyerId) {
// //       console.log(`🔄 Updating buyer ID: ${this.buyerId}...`);
      
// //       this.buyerService.updateCompleteHierarchy(this.buyerId, payload).subscribe({
// //         next: (response) => {
// //           console.log('✅ Buyer updated successfully:', response);
// //           this.messageService.showMessage('success', 'Success', 'Buyer updated successfully');
// //           this.isSubmitting = false;
// //           setTimeout(() => this.router.navigate(['/dashboard']), 1500);
// //         },
// //         error: (err: any) => {
// //           console.error('❌ Error updating buyer:', err);
// //           const errorMsg = err.error?.message || err.message || 'Failed to update buyer';
// //           this.messageService.showMessage('error', 'Error', errorMsg);
// //           this.isSubmitting = false;
// //         }
// //       });
// //     }
// //   }
// // }



// import { Component, OnInit } from '@angular/core';
// import { FormBuilder, FormGroup, Validators, FormArray } from '@angular/forms';
// import { Router, ActivatedRoute } from '@angular/router';
// import { CommonModule } from '@angular/common';
// import { ReactiveFormsModule } from '@angular/forms';
// import { BuyerService } from '../dashboard/buyer-b.service';
// import { MessageService } from '../../../shared/service/message.service';
// import { Buyer, Location, Department, User } from '../dashboard/buyer-b.model';

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

//   states = ['Andhra Pradesh', 'Arunachal Pradesh', 'Assam', 'Bihar', 'Chhattisgarh', 
//             'Goa', 'Gujarat', 'Haryana', 'Himachal Pradesh', 'Jharkhand', 'Karnataka',
//             'Kerala', 'Madhya Pradesh', 'Maharashtra', 'Manipur', 'Meghalaya', 'Mizoram',
//             'Nagaland', 'Odisha', 'Punjab', 'Rajasthan', 'Sikkim', 'Tamil Nadu', 'Telangana',
//             'Tripura', 'Uttar Pradesh', 'Uttarakhand', 'West Bengal'];

//   companyTypes = ['Manufacturing', 'Trading', 'Services', 'Distribution', 'Retail', 'Others'];
//   locationTypes = ['Branch', 'Regional', 'Warehouse', 'Service Center', 'Others'];

//   constructor(
//     private fb: FormBuilder,
//     private buyerService: BuyerService,
//     private messageService: MessageService,
//     private router: Router,
//     private route: ActivatedRoute
//   ) {}

//   ngOnInit(): void {
//     this.initializeForm();
//     this.checkEditMode();
//   }

//   private initializeForm(): void {
//     this.buyerForm = this.fb.group({
//       companyName: ['', [Validators.required, Validators.minLength(2)]],
//       companyType: ['', Validators.required],
//       otherCompanyType: [''],
//       contactPersonName: ['', [Validators.required, Validators.minLength(2)]],
//       contactPersonDesignation: ['', Validators.required],
//       contactPersonEmail: ['', [Validators.required, Validators.email]],
//       contactPersonPhone: ['', [Validators.required, Validators.pattern(/^[0-9]{10}$/)]],
//       addressLine1: ['', [Validators.required, Validators.minLength(5)]],
//       addressLine2: [''],
//       city: ['', Validators.required],
//       state: ['', Validators.required],
//       postalCode: ['', [Validators.required, Validators.pattern(/^[0-9]{6}$/)]],
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

//   get locations(): FormArray {
//     return this.buyerForm.get('locations') as FormArray;
//   }

//   get departments(): FormArray {
//     return this.buyerForm.get('departments') as FormArray;
//   }

//   get users(): FormArray {
//     return this.buyerForm.get('users') as FormArray;
//   }

//   // ==================== LOCATION METHODS ====================

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
//       postalCode: ['', [Validators.required, Validators.pattern(/^[0-9]{6}$/)]],
//       country: ['India', Validators.required],
//       landlineNumber: [''],
//       faxNumber: ['']
//     });
//     this.locations.push(locationGroup);
//   }

//   removeLocation(index: number): void {
//     if (this.locations.length > 1) {
//       this.locations.removeAt(index);
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

//   // ==================== DEPARTMENT METHODS ====================

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

//   // ==================== USER METHODS ====================

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

//   // ==================== COMPANY TYPE CHANGE ====================

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

//   // ==================== LOAD BUYER DATA (EDIT MODE) ====================

//   private loadBuyerData(id: number): void {
//     console.log('🔵 Loading buyer data for ID:', id);
    
//     this.buyerService.getBuyerById(id).subscribe({
//       next: (buyer: any) => {
//         console.log('✅ Buyer data loaded:', buyer);

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

//         this.onCompanyTypeChange({ target: { value: companyTypeForForm } });

//         // Clear arrays
//         while (this.locations.length) {
//           this.locations.removeAt(0);
//         }
//         while (this.departments.length) {
//           this.departments.removeAt(0);
//         }
//         while (this.users.length) {
//           this.users.removeAt(0);
//         }

//         if (buyer.locations && Array.isArray(buyer.locations)) {
//           this.populateLocations(buyer.locations);
//         }
//       },
//       error: (err: any) => {
//         console.error('❌ Error loading buyer:', err);
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

//     console.log('✅ All locations populated');
//   }

//   // ==================== STEP VALIDATION ====================

//   private isCurrentStepValid(): boolean {
//     switch (this.currentStep) {
//       case 1:
//         return this.isBuyerDetailsValid();
//       case 2:
//         return this.locationsBasicsValid();
//       case 3:
//         return this.departmentsValid();
//       case 4:
//         return this.usersValid();
//       default:
//         return false;
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
//       ? (otherCompanyTypeControl?.valid ?? false)
//       : true;

//     return standardFieldsValid && otherFieldValid;
//   }

//   private locationsBasicsValid(): boolean {
//     return this.locations.length > 0 && 
//            this.locations.controls.every(loc => {
//              const fields = ['locationName', 'locationType', 'locationContactName', 
//                              'locationContactEmail', 'locationContactPhone', 'addressLine1',
//                              'city', 'state', 'postalCode', 'country'];

//              const standardFieldsValid = fields.every(field => loc.get(field)?.valid);

//              const locationTypeControl = loc.get('locationType');
//              const otherLocationTypeControl = loc.get('otherLocationType');

//              const otherFieldValid: boolean = locationTypeControl?.value === 'Others'
//                ? (otherLocationTypeControl?.valid ?? false)
//                : true;

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

//   // ==================== STEP NAVIGATION ====================

//   saveStep(): void {
//     if (!this.isCurrentStepValid()) {
//       this.messageService.showMessage('warning', 'Validation Error', 'Please fill all required fields on this step');
//       this.buyerForm.markAllAsTouched();
//       return;
//     }

//     this.messageService.showMessage('success', 'Saved', `Step ${this.currentStep} saved successfully`);
    
//     if (this.currentStep < this.maxStep) {
//       this.currentStep++;
//     }
//   }

//   prevStep(): void {
//     if (this.currentStep > 1) {
//       this.currentStep--;
//     }
//   }

//   nextStep(): void {
//     if (this.currentStep < this.maxStep) {
//       this.currentStep++;
//     }
//   }

//   // ==================== ✅ COMPLETELY FIXED SUBMIT METHOD ====================

//   onSubmit(): void {
//     console.log('========================================');
//     console.log('🔵 SUBMITTING BUYER FORM');
//     console.log('========================================');
    
//     if (this.buyerForm.invalid) {
//       console.error('❌ Form is invalid!');
//       this.buyerForm.markAllAsTouched();
//       this.messageService.showMessage('error', 'Validation Error', 'Please fill all required fields');
//       return;
//     }

//     this.isSubmitting = true;
    
//     // Get raw form value
//     const formValue = this.buyerForm.getRawValue();
    
//     console.log('📋 Raw form value:', formValue);
//     console.log('');

//     // ✅ STEP 1: Handle "Others" company type
//     if (formValue.companyType === 'Others' && formValue.otherCompanyType) {
//       formValue.companyType = formValue.otherCompanyType;
//     }
//     delete formValue.otherCompanyType;

//     // ✅ STEP 2: Process locations and initialize empty departments arrays
//     const processedLocations: any[] = [];
    
//     console.log('📍 Processing locations...');
//     if (formValue.locations && Array.isArray(formValue.locations)) {
//       formValue.locations.forEach((loc: any, idx: number) => {
//         console.log(`   Location ${idx}: ${loc.locationName}`);
        
//         // Handle "Others" for location type
//         if (loc.locationType === 'Others' && loc.otherLocationType) {
//           loc.locationType = loc.otherLocationType;
//         }
//         delete loc.otherLocationType;
        
//         // ✅ CRITICAL: Initialize empty departments array
//         loc.departments = [];
        
//         processedLocations.push(loc);
//         console.log(`      ✅ Added with empty departments array`);
//       });
//     }

//     console.log('');
//     console.log('📂 Processing departments...');
    
//     // ✅ STEP 3: Nest departments inside their locations
//     if (formValue.departments && Array.isArray(formValue.departments)) {
//       formValue.departments.forEach((dept: any, deptIdx: number) => {
//         console.log(`   Department ${deptIdx}: ${dept.departmentName}`);
        
//         const locationIndex = dept.locationIndex;
        
//         // ✅ FIX: Validate location index exists and is valid
//         if (locationIndex === '' || locationIndex === null || locationIndex === undefined) {
//           console.error(`      ❌ No location assignment for department "${dept.departmentName}" - SKIPPING!`);
//           this.messageService.showMessage('error', 'Error', `Department "${dept.departmentName}" has no location assigned`);
//           return;
//         }

//         const locationIdx = parseInt(locationIndex, 10);
        
//         if (isNaN(locationIdx)) {
//           console.error(`      ❌ Invalid location index: ${locationIndex} for department "${dept.departmentName}"`);
//           return;
//         }
        
//         if (locationIdx < 0 || locationIdx >= processedLocations.length) {
//           console.error(`      ❌ Location index ${locationIdx} out of range for department "${dept.departmentName}"`);
//           return;
//         }

//         // ✅ Remove locationIndex and create department with empty users array
//         const { locationIndex: _, ...deptData } = dept;
//         deptData.users = [];  // ✅ CRITICAL: Initialize empty users array
        
//         // ✅ Add department to the correct location
//         processedLocations[locationIdx].departments.push(deptData);
//         console.log(`      ✅ Added to location ${locationIdx} (${processedLocations[locationIdx].locationName})`);
//       });
//     }

//     console.log('');
//     console.log('👥 Processing users...');
    
//     // ✅ STEP 4: Nest users inside their departments
//     if (formValue.users && Array.isArray(formValue.users)) {
//       formValue.users.forEach((user: any, userIdx: number) => {
//         console.log(`   User ${userIdx}: ${user.firstName} ${user.lastName}`);
        
//         const departmentIndex = user.departmentIndex;
        
//         // ✅ FIX: Validate department index
//         if (departmentIndex === '' || departmentIndex === null || departmentIndex === undefined) {
//           console.error(`      ❌ No department assignment for user "${user.firstName} ${user.lastName}" - SKIPPING!`);
//           this.messageService.showMessage('error', 'Error', `User "${user.firstName} ${user.lastName}" has no department assigned`);
//           return;
//         }

//         const deptIdx = parseInt(departmentIndex, 10);
        
//         if (isNaN(deptIdx)) {
//           console.error(`      ❌ Invalid department index for user "${user.firstName} ${user.lastName}"`);
//           return;
//         }

//         // ✅ FIX: Find the department across all locations using the flat department index
//         let targetDepartment: any = null;
//         let targetLocationName = '';
//         let globalDeptCounter = 0;  // This counts across ALL locations

//         // ✅ CRITICAL FIX: Iterate through all departments in all locations
//         for (let locIdx = 0; locIdx < processedLocations.length; locIdx++) {
//           const location = processedLocations[locIdx];
          
//           for (let dIdx = 0; dIdx < location.departments.length; dIdx++) {
//             const dept = location.departments[dIdx];
            
//             // ✅ Check if this is the department we're looking for
//             if (globalDeptCounter === deptIdx) {
//               targetDepartment = dept;
//               targetLocationName = location.locationName;
//               console.log(`      🎯 Found target department at location ${locIdx}, dept ${dIdx}`);
//               break;
//             }
            
//             globalDeptCounter++;
//           }
          
//           if (targetDepartment) break;
//         }

//         if (targetDepartment) {
//           // ✅ Remove departmentIndex from user data
//           const { departmentIndex: _, ...userData } = user;
          
//           // ✅ Handle password for edit mode
//           if (this.mode === 'edit' && (!userData.password || userData.password.trim() === '')) {
//             delete userData.password;
//           }
          
//           // ✅ CRITICAL: Add user to the department's users array
//           targetDepartment.users.push(userData);
//           console.log(`      ✅ Added to department '${targetDepartment.departmentName}' in '${targetLocationName}'`);
//         } else {
//           console.error(`      ❌ Could not find department with index ${deptIdx} for user "${user.firstName} ${user.lastName}"`);
//           this.messageService.showMessage('error', 'Error', `Could not find department for user "${user.firstName} ${user.lastName}"`);
//         }
//       });
//     }

//     // ✅ STEP 5: Build final payload with properly nested structure
//     const payload = {
//       ...formValue,
//       locations: processedLocations
//     };
    
//     // Remove flat arrays from payload (they're now nested)
//     delete payload.departments;
//     delete payload.users;

//     console.log('');
//     console.log('========================================');
//     console.log('📦 FINAL PAYLOAD TO BE SENT:');
//     console.log('========================================');
//     console.log(JSON.stringify(payload, null, 2));
//     console.log('');
    
//     // ✅ Verify structure before sending
//     console.log('🔍 PAYLOAD VERIFICATION:');
//     console.log(`   Total Locations: ${payload.locations.length}`);
    
//     let totalDepts = 0;
//     let totalUsers = 0;
//     let hasErrors = false;
    
//     payload.locations.forEach((loc: any, i: number) => {
//       const deptCount = loc.departments ? loc.departments.length : 0;
//       totalDepts += deptCount;
      
//       console.log(`   Location ${i}: "${loc.locationName}"`);
//       console.log(`      Departments: ${deptCount}`);
      
//       if (deptCount === 0) {
//         console.warn(`      ⚠️  WARNING: Location "${loc.locationName}" has NO departments!`);
//       }
      
//       if (loc.departments) {
//         loc.departments.forEach((dept: any, j: number) => {
//           const userCount = dept.users ? dept.users.length : 0;
//           totalUsers += userCount;
          
//           console.log(`         Department ${j}: "${dept.departmentName}"`);
//           console.log(`            Users: ${userCount}`);
          
//           if (userCount === 0) {
//             console.warn(`            ⚠️  WARNING: Department "${dept.departmentName}" has NO users!`);
//           }
          
//           if (dept.users && dept.users.length > 0) {
//             dept.users.forEach((usr: any, k: number) => {
//               console.log(`               User ${k}: ${usr.firstName} ${usr.lastName} (${usr.email})`);
              
//               // ✅ Validate user has required fields
//               if (!usr.password && this.mode === 'create') {
//                 console.error(`               ❌ ERROR: User "${usr.firstName} ${usr.lastName}" has no password!`);
//                 hasErrors = true;
//               }
//             });
//           }
//         });
//       }
//     });
    
//     console.log('');
//     console.log(`📊 TOTALS: ${payload.locations.length} locations, ${totalDepts} departments, ${totalUsers} users`);
//     console.log('========================================');

//     // ✅ Stop if there are validation errors
//     if (hasErrors) {
//       console.error('❌ Payload has validation errors. Please fix and try again.');
//       this.messageService.showMessage('error', 'Validation Error', 'Some users are missing required fields');
//       this.isSubmitting = false;
//       return;
//     }

//     // ✅ Warn if no departments or users
//     if (totalDepts === 0) {
//       console.warn('⚠️  WARNING: No departments in payload!');
//       this.messageService.showMessage('warning', 'Warning', 'No departments added. Are you sure you want to continue?');
//     }
    
//     if (totalUsers === 0) {
//       console.warn('⚠️  WARNING: No users in payload!');
//       this.messageService.showMessage('warning', 'Warning', 'No users added. Are you sure you want to continue?');
//     }

//     // ✅ STEP 6: Send to backend
//     if (this.mode === 'create') {
//       console.log('➕ Creating new buyer...');
      
//       this.buyerService.createCompleteHierarchy(payload).subscribe({
//         next: (response) => {
//           console.log('✅ Buyer created successfully:', response);
//           this.messageService.showMessage('success', 'Success', 'Buyer created successfully with all locations, departments, and users');
//           this.isSubmitting = false;
//           setTimeout(() => this.router.navigate(['/dashboard']), 1500);
//         },
//         error: (err: any) => {
//           console.error('❌ Error creating buyer:', err);
//           const errorMsg = err.error?.message || err.message || 'Failed to create buyer';
//           this.messageService.showMessage('error', 'Error', errorMsg);
//           this.isSubmitting = false;
//         }
//       });
//     } else if (this.buyerId) {
//       console.log(`🔄 Updating buyer ID: ${this.buyerId}...`);
      
//       this.buyerService.updateCompleteHierarchy(this.buyerId, payload).subscribe({
//         next: (response) => {
//           console.log('✅ Buyer updated successfully:', response);
//           this.messageService.showMessage('success', 'Success', 'Buyer updated successfully');
//           this.isSubmitting = false;
//           setTimeout(() => this.router.navigate(['/dashboard']), 1500);
//         },
//         error: (err: any) => {
//           console.error('❌ Error updating buyer:', err);
//           const errorMsg = err.error?.message || err.message || 'Failed to update buyer';
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

  // ✅ NEW: Organization Admin Mode
  isOrgAdminMode = false;
  orgAdminId: number | null = null;
  orgAdminCompanyName: string = '';

  states = ['Andhra Pradesh', 'Arunachal Pradesh', 'Assam', 'Bihar', 'Chhattisgarh', 
            'Goa', 'Gujarat', 'Haryana', 'Himachal Pradesh', 'Jharkhand', 'Karnataka',
            'Kerala', 'Madhya Pradesh', 'Maharashtra', 'Manipur', 'Meghalaya', 'Mizoram',
            'Nagaland', 'Odisha', 'Punjab', 'Rajasthan', 'Sikkim', 'Tamil Nadu', 'Telangana',
            'Tripura', 'Uttar Pradesh', 'Uttarakhand', 'West Bengal','Dubai'];

  companyTypes = ['Manufacturing', 'Trading', 'Services', 'Distribution', 'Retail', 'Others'];
  locationTypes = ['Branch', 'Regional', 'Warehouse', 'Service Center', 'Others'];

  constructor(
    private fb: FormBuilder,
    private buyerService: BuyerService,
    private messageService: MessageService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.checkOrgAdminMode();
    this.initializeForm();
    this.checkEditMode();
  }

  // ✅ NEW: Check if Organization Admin is creating buyer
  private checkOrgAdminMode(): void {
    const role = localStorage.getItem('role');
    
    if (role === 'ORGANIZATION_ADMIN') {
      this.isOrgAdminMode = true;
      this.orgAdminId = Number(localStorage.getItem('userId'));
      this.orgAdminCompanyName = localStorage.getItem('companyName') || '';
      
      console.log('✅ Organization Admin Mode Enabled');
      console.log('   Admin ID:', this.orgAdminId);
      console.log('   Company:', this.orgAdminCompanyName);
    }
  }

  private initializeForm(): void {
    this.buyerForm = this.fb.group({
      companyName: ['', [Validators.required, Validators.minLength(2)]],
      companyType: ['', Validators.required],
      otherCompanyType: [''],
      contactPersonName: ['', [Validators.required, Validators.minLength(2)]],
      contactPersonDesignation: ['', Validators.required],
      contactPersonEmail: ['', [Validators.required, Validators.email]],
      contactPersonPhone: ['', [Validators.required, Validators.pattern(/^[0-9]{10}$/)]],
      addressLine1: ['', [Validators.required, Validators.minLength(5)]],
      addressLine2: [''],
      city: ['', Validators.required],
      state: ['', Validators.required],
      postalCode: ['', [Validators.required, Validators.pattern(/^[0-9]{6}$/)]],
      country: ['India', Validators.required],
      gstNumber: [''],
      panNumber: [''],
      cinNumber: [''],
      website: [''],
      // ✅ NEW: Not in form, added in payload
      // organizationCompanyName will be added programmatically in onSubmit()
      locations: this.fb.array([], Validators.minLength(1)),
      departments: this.fb.array([]),
      users: this.fb.array([])
    });

    if (!this.buyerForm.get('locations')?.value?.length) {
      this.addLocation();
    }
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

  get locations(): FormArray {
    return this.buyerForm.get('locations') as FormArray;
  }

  get departments(): FormArray {
    return this.buyerForm.get('departments') as FormArray;
  }

  get users(): FormArray {
    return this.buyerForm.get('users') as FormArray;
  }

  // ==================== LOCATION METHODS ====================

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
      postalCode: ['', [Validators.required, Validators.pattern(/^[0-9]{6}$/)]],
      country: ['India', Validators.required],
      landlineNumber: [''],
      faxNumber: ['']
    });
    this.locations.push(locationGroup);
  }

  removeLocation(index: number): void {
    if (this.locations.length > 1) {
      this.locations.removeAt(index);
    } else {
      this.messageService.showMessage('warning', 'Warning', 'At least one location is required');
    }
  }

  onLocationTypeChange(event: any, locationIndex: number): void {
    const locationGroup = this.locations.at(locationIndex) as FormGroup;
    const selectedValue = event.target.value;
    const customTypeControl = locationGroup.get('otherLocationType');

    if (selectedValue === 'Others') {
      customTypeControl?.setValidators([Validators.required, Validators.minLength(2)]);
      customTypeControl?.setValue('');
    } else {
      customTypeControl?.clearValidators();
      customTypeControl?.setValue(null);
    }
    customTypeControl?.updateValueAndValidity();
  }

  // ==================== DEPARTMENT METHODS ====================

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

  // ==================== USER METHODS ====================

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
      this.messageService.showMessage('warning', 'Warning', 'At least one user is required');
    }
  }

  // ==================== COMPANY TYPE CHANGE ====================

  onCompanyTypeChange(event: any): void {
    const selectedValue = event.target.value;
    const customTypeControl = this.buyerForm.get('otherCompanyType');

    if (selectedValue === 'Others') {
      customTypeControl?.setValidators([Validators.required, Validators.minLength(2)]);
      customTypeControl?.setValue('');
    } else {
      customTypeControl?.clearValidators();
      customTypeControl?.setValue(null);
    }
    customTypeControl?.updateValueAndValidity();
  }

  // ==================== LOAD BUYER DATA (EDIT MODE) ====================

  private loadBuyerData(id: number): void {
    console.log('🔵 Loading buyer data for ID:', id);
    
    this.buyerService.getBuyerById(id).subscribe({
      next: (buyer: any) => {
        console.log('✅ Buyer data loaded:', buyer);

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
          city: buyer.city,
          state: buyer.state,
          postalCode: buyer.postalCode,
          country: buyer.country,
          gstNumber: buyer.gstNumber,
          panNumber: buyer.panNumber,
          cinNumber: buyer.cinNumber,
          website: buyer.website
        });

        this.onCompanyTypeChange({ target: { value: companyTypeForForm } });

        // Clear arrays
        while (this.locations.length) {
          this.locations.removeAt(0);
        }
        while (this.departments.length) {
          this.departments.removeAt(0);
        }
        while (this.users.length) {
          this.users.removeAt(0);
        }

        if (buyer.locations && Array.isArray(buyer.locations)) {
          this.populateLocations(buyer.locations);
        }
      },
      error: (err: any) => {
        console.error('❌ Error loading buyer:', err);
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
        faxNumber: loc.faxNumber
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
            });
          }
        });
      }
    });

    console.log('✅ All locations populated');
  }

  // ==================== STEP VALIDATION ====================

  private isCurrentStepValid(): boolean {
    switch (this.currentStep) {
      case 1:
        return this.isBuyerDetailsValid();
      case 2:
        return this.locationsBasicsValid();
      case 3:
        return this.departmentsValid();
      case 4:
        return this.usersValid();
      default:
        return false;
    }
  }

  private isBuyerDetailsValid(): boolean {
    const fields = ['companyName', 'companyType', 'contactPersonName', 'contactPersonDesignation',
      'contactPersonEmail', 'contactPersonPhone', 'addressLine1', 'city', 'state', 
      'postalCode', 'country'];

    const standardFieldsValid = fields.every(field => this.buyerForm.get(field)?.valid);

    const companyTypeControl = this.buyerForm.get('companyType');
    const otherCompanyTypeControl = this.buyerForm.get('otherCompanyType');

    const otherFieldValid: boolean = companyTypeControl?.value === 'Others'
      ? (otherCompanyTypeControl?.valid ?? false)
      : true;

    return standardFieldsValid && otherFieldValid;
  }

  private locationsBasicsValid(): boolean {
    return this.locations.length > 0 && 
           this.locations.controls.every(loc => {
             const fields = ['locationName', 'locationType', 'locationContactName', 
                             'locationContactEmail', 'locationContactPhone', 'addressLine1',
                             'city', 'state', 'postalCode', 'country'];

             const standardFieldsValid = fields.every(field => loc.get(field)?.valid);

             const locationTypeControl = loc.get('locationType');
             const otherLocationTypeControl = loc.get('otherLocationType');

             const otherFieldValid: boolean = locationTypeControl?.value === 'Others'
               ? (otherLocationTypeControl?.valid ?? false)
               : true;

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

  // ==================== STEP NAVIGATION ====================

  saveStep(): void {
    if (!this.isCurrentStepValid()) {
      this.messageService.showMessage('warning', 'Validation Error', 'Please fill all required fields on this step');
      this.buyerForm.markAllAsTouched();
      return;
    }

    this.messageService.showMessage('success', 'Saved', `Step ${this.currentStep} saved successfully`);
    
    if (this.currentStep < this.maxStep) {
      this.currentStep++;
    }
  }

  prevStep(): void {
    if (this.currentStep > 1) {
      this.currentStep--;
    }
  }

  nextStep(): void {
    if (this.currentStep < this.maxStep) {
      this.currentStep++;
    }
  }

  // ==================== ✅ COMPLETELY FIXED SUBMIT METHOD WITH ORG ADMIN SUPPORT ====================

onSubmit(): void {
  console.log('========================================');
  console.log('🔵 SUBMITTING BUYER FORM');
  console.log('========================================');
  console.log('   Mode:', this.mode);
  console.log('   Org Admin Mode:', this.isOrgAdminMode);
  console.log('   Org Admin ID:', this.orgAdminId);
  console.log('   Org Company:', this.orgAdminCompanyName);
  
  // ✅ Validate form
  if (this.buyerForm.invalid) {
    console.error('❌ Form is invalid!');
    this.buyerForm.markAllAsTouched();
    this.messageService.showMessage('error', 'Validation Error', 
      'Please fill all required fields');
    return;
  }

  // ✅ Validate Organization Admin mode
  if (this.isOrgAdminMode && !this.orgAdminId) {
    console.error('❌ Organization Admin ID missing!');
    this.messageService.showMessage('error', 'Error', 
      'Organization Admin ID not found. Please login again.');
    return;
  }

  if (this.isOrgAdminMode && !this.orgAdminCompanyName) {
    console.error('❌ Organization Company Name missing!');
    this.messageService.showMessage('error', 'Error', 
      'Organization Company Name not found. Please login again.');
    return;
  }

  this.isSubmitting = true;
  
  const formValue = this.buyerForm.getRawValue();
  console.log('📋 Raw form value:', formValue);

  // ✅ Handle "Others" company type
  if (formValue.companyType === 'Others' && formValue.otherCompanyType) {
    formValue.companyType = formValue.otherCompanyType;
  }
  delete formValue.otherCompanyType;

  // ✅ Process locations (same as before)
  const processedLocations: any[] = [];
  
  if (formValue.locations && Array.isArray(formValue.locations)) {
    formValue.locations.forEach((loc: any, idx: number) => {
      if (loc.locationType === 'Others' && loc.otherLocationType) {
        loc.locationType = loc.otherLocationType;
      }
      delete loc.otherLocationType;
      loc.departments = [];
      processedLocations.push(loc);
    });
  }

  // ✅ Process departments
  if (formValue.departments && Array.isArray(formValue.departments)) {
    formValue.departments.forEach((dept: any) => {
      const locationIndex = parseInt(dept.locationIndex, 10);
      if (!isNaN(locationIndex) && locationIndex >= 0 && 
          locationIndex < processedLocations.length) {
        const { locationIndex: _, ...deptData } = dept;
        deptData.users = [];
        processedLocations[locationIndex].departments.push(deptData);
      }
    });
  }

  // ✅ Process users
  if (formValue.users && Array.isArray(formValue.users)) {
    formValue.users.forEach((user: any) => {
      const deptIdx = parseInt(user.departmentIndex, 10);
      let targetDepartment: any = null;
      let globalDeptCounter = 0;

      for (let location of processedLocations) {
        for (let dept of location.departments) {
          if (globalDeptCounter === deptIdx) {
            targetDepartment = dept;
            break;
          }
          globalDeptCounter++;
        }
        if (targetDepartment) break;
      }

      if (targetDepartment) {
        const { departmentIndex: _, ...userData } = user;
        if (this.mode === 'edit' && (!userData.password || !userData.password.trim())) {
          delete userData.password;
        }
        targetDepartment.users.push(userData);
      }
    });
  }

  // ✅ Build final payload
  const payload = {
    ...formValue,
    locations: processedLocations
  };
  
  delete payload.departments;
  delete payload.users;

  // ✅ CRITICAL: Add organizationCompanyName if Organization Admin mode
  if (this.isOrgAdminMode) {
    payload.organizationCompanyName = this.orgAdminCompanyName;
    console.log('✅ Added organizationCompanyName:', this.orgAdminCompanyName);
  }

  console.log('========================================');
  console.log('📦 FINAL PAYLOAD:');
  console.log('========================================');
  console.log(JSON.stringify(payload, null, 2));
  console.log('========================================');

  // ✅ Send to backend
  if (this.mode === 'create') {
    console.log('➕ Creating new buyer...');
    console.log('   Using adminId:', this.isOrgAdminMode ? this.orgAdminId : undefined);
    
    this.buyerService.createCompleteHierarchy(
      payload, 
      this.isOrgAdminMode ? this.orgAdminId! : undefined
    ).subscribe({
      next: (response) => {
        console.log('✅ SUCCESS RESPONSE:', response);
        this.messageService.showMessage('success', 'Success', 
          this.isOrgAdminMode 
            ? 'Buyer created and linked to your organization'
            : 'Buyer created successfully');
        this.isSubmitting = false;
        setTimeout(() => this.router.navigate(['/dashboard']), 1500);
      },
      error: (err: any) => {
        console.error('❌ ERROR RESPONSE:', err);
        console.error('   Status:', err.status);
        console.error('   Error Object:', err.error);
        console.error('   Message:', err.error?.message || err.message);
        
        const errorMsg = err.error?.message || err.message || 'Failed to create buyer';
        this.messageService.showMessage('error', 'Error', errorMsg);
        this.isSubmitting = false;
      }
    });
  } else if (this.buyerId) {
    // Update flow
    this.buyerService.updateCompleteHierarchy(this.buyerId, payload).subscribe({
      next: (response) => {
        console.log('✅ Buyer updated:', response);
        this.messageService.showMessage('success', 'Success', 'Buyer updated');
        this.isSubmitting = false;
        setTimeout(() => this.router.navigate(['/dashboard']), 1500);
      },
      error: (err: any) => {
        console.error('❌ Update error:', err);
        const errorMsg = err.error?.message || err.message || 'Update failed';
        this.messageService.showMessage('error', 'Error', errorMsg);
        this.isSubmitting = false;
      }
    });
  }
}
}