import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, FormArray } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { SupplierService } from '../dashboard/supplier.service';
import { MessageService } from '../../../shared/service/message.service';
import { Supplier, SupplierLocation, SupplierDepartment, SupplierUser } from '../dashboard/supplier.model';

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

  states = ['Andhra Pradesh', 'Arunachal Pradesh', 'Assam', 'Bihar', 'Chhattisgarh', 
            'Goa', 'Gujarat', 'Haryana', 'Himachal Pradesh', 'Jharkhand', 'Karnataka',
            'Kerala', 'Madhya Pradesh', 'Maharashtra', 'Manipur', 'Meghalaya', 'Mizoram',
            'Nagaland', 'Odisha', 'Punjab', 'Rajasthan', 'Sikkim', 'Tamil Nadu', 'Telangana',
            'Tripura', 'Uttar Pradesh', 'Uttarakhand', 'West Bengal'];

  companyTypes = ['Manufacturing', 'Trading', 'Services', 'Distribution', 'Retail', 'Others'];
  locationTypes = ['Head Office', 'Branch', 'Warehouse', 'Service Center', 'Others'];
  industrySectors = ['IT', 'Logistics', 'Electrical', 'Construction', 'Healthcare', 'Automotive', 'Textile', 'Food & Beverage', 'Others'];
  categoryOfProducts = ['IT Equipment', 'Industrial Equipment', 'Software Services', 'Hardware Supplies', 'Others'];

  constructor(
    private fb: FormBuilder,
    private supplierService: SupplierService,
    private messageService: MessageService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    console.log('CreateSupplierComponent initialized');
    this.initializeForm();
    this.checkEditMode();
  }

  // ==================== FORM INITIALIZATION ====================
  private initializeForm(): void {
    this.supplierForm = this.fb.group({
      companyName: ['', [Validators.required, Validators.minLength(2)]],
      companyType: ['', Validators.required],
      otherCompanyType: [''],
      industrySector: ['', Validators.required],
      otherIndustrySector: [''],
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

  private checkEditMode(): void {
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      if (id) {
        console.log('Edit mode detected for ID:', id);
        this.mode = 'edit';
        this.supplierId = +id;
        this.currentStep = 1;
        this.loadSupplierData(this.supplierId);
      } else {
        console.log('Create mode');
        this.mode = 'create';
        this.currentStep = 1;
      }
    });
  }

  // ==================== LOCATIONS ====================
  get locations(): FormArray {
    return this.supplierForm.get('locations') as FormArray;
  }

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

  // ==================== DEPARTMENTS (FLAT STRUCTURE) ====================
  get departments(): FormArray {
    return this.supplierForm.get('departments') as FormArray;
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

  // ==================== USERS (FLAT STRUCTURE) ====================
  get users(): FormArray {
    return this.supplierForm.get('users') as FormArray;
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

  // Helper to get location name for a department
  getLocationNameForDepartment(deptIndex: number): string {
    const dept = this.departments.at(deptIndex);
    const locIndex = dept?.get('locationIndex')?.value;
    if (locIndex !== '' && locIndex !== null && locIndex !== undefined) {
      const location = this.locations.at(locIndex);
      return location?.get('locationName')?.value || `Location ${locIndex + 1}`;
    }
    return '';
  }

  // ==================== OTHERS HANDLING ====================
  onCompanyTypeChange(event: any): void {
    const selectedValue = event.target.value;
    const customTypeControl = this.supplierForm.get('otherCompanyType');

    if (selectedValue === 'Others') {
      customTypeControl?.setValidators([Validators.required, Validators.minLength(2)]);
      customTypeControl?.setValue('');
    } else {
      customTypeControl?.clearValidators();
      customTypeControl?.setValue(null);
    }
    customTypeControl?.updateValueAndValidity();
  }

  onIndustrySectorChange(event: any): void {
    const selectedValue = event.target.value;
    const customTypeControl = this.supplierForm.get('otherIndustrySector');

    if (selectedValue === 'Others') {
      customTypeControl?.setValidators([Validators.required, Validators.minLength(2)]);
      customTypeControl?.setValue('');
    } else {
      customTypeControl?.clearValidators();
      customTypeControl?.setValue(null);
    }
    customTypeControl?.updateValueAndValidity();
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

  onCategoryTypeChange(event: any, deptIndex: number): void {
    const departmentGroup = this.departments.at(deptIndex) as FormGroup;
    const selectedValue = event.target.value;
    const customTypeControl = departmentGroup.get('otherCategoryType');

    if (selectedValue === 'Others') {
      customTypeControl?.setValidators([Validators.required, Validators.minLength(2)]);
      customTypeControl?.setValue('');
    } else {
      customTypeControl?.clearValidators();
      customTypeControl?.setValue(null);
    }
    customTypeControl?.updateValueAndValidity();
  }

  // ==================== LOAD DATA ====================
  private loadSupplierData(id: number): void {
    console.log('Loading supplier data for ID:', id);
    this.supplierService.getSupplierById(id).subscribe({
      next: (supplier: any) => {
        console.log('Supplier data loaded:', supplier);

        let companyTypeForForm = supplier.companyType;
        let otherCompanyType = null;
        let industrySectorForForm = supplier.industrySector;
        let otherIndustrySector = null;

        if (supplier.companyType && !this.companyTypes.includes(supplier.companyType)) {
          companyTypeForForm = 'Others';
          otherCompanyType = supplier.companyType;
        }

        if (supplier.industrySector && !this.industrySectors.includes(supplier.industrySector)) {
          industrySectorForForm = 'Others';
          otherIndustrySector = supplier.industrySector;
        }

        this.supplierForm.patchValue({
          companyName: supplier.companyName,
          companyType: companyTypeForForm,
          otherCompanyType: otherCompanyType,
          industrySector: industrySectorForForm,
          otherIndustrySector: otherIndustrySector,
          contactPersonName: supplier.contactPersonName,
          contactPersonDesignation: supplier.contactPersonDesignation,
          contactPersonEmail: supplier.contactPersonEmail,
          contactPersonPhone: supplier.contactPersonPhone,
          addressLine1: supplier.addressLine1,
          addressLine2: supplier.addressLine2,
          city: supplier.city,
          state: supplier.state,
          postalCode: supplier.postalCode,
          country: supplier.country,
          gstNumber: supplier.gstNumber,
          panNumber: supplier.panNumber,
          tanNumber: supplier.tanNumber,
          website: supplier.website
        });

        this.onCompanyTypeChange({ target: { value: companyTypeForForm } });
        this.onIndustrySectorChange({ target: { value: industrySectorForForm } });

        // CRITICAL FIX: Clear ALL FormArrays before repopulating
        console.log('Clearing FormArrays...');
        while (this.locations.length) {
          this.locations.removeAt(0);
        }
        while (this.departments.length) {
          this.departments.removeAt(0);
        }
        while (this.users.length) {
          this.users.removeAt(0);
        }

        if (supplier.locations && Array.isArray(supplier.locations)) {
          console.log('Populating locations:', supplier.locations.length);
          this.populateLocations(supplier.locations);
        }

        console.log('Data load complete');
      },
      error: (err: any) => {
        console.error('Error loading supplier data:', err);
        this.messageService.showMessage('error', 'Error', 'Failed to load supplier data');
        setTimeout(() => this.router.navigate(['/dashboard']), 2000);
      }
    });
  }

  // ==================== POPULATE LOCATIONS ====================
  private populateLocations(locations: SupplierLocation[]): void {
    locations.forEach((loc: any, locIndex: number) => {
      console.log(`Adding location ${locIndex + 1}:`, loc.locationName);
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

      // Populate departments with locationIndex reference
      if (loc.departments && loc.departments.length > 0) {
        console.log(`Location ${locIndex + 1} has ${loc.departments.length} departments`);
        loc.departments.forEach((dept: any) => {
          const deptIndex = this.departments.length;
          console.log(`Adding department ${deptIndex + 1}:`, dept.departmentName);
          this.addDepartment();

          let categoryForForm = dept.categoryOfProducts;
          let otherCategoryType = null;

          if (dept.categoryOfProducts && !this.categoryOfProducts.includes(dept.categoryOfProducts)) {
            categoryForForm = 'Others';
            otherCategoryType = dept.categoryOfProducts;
          }

          this.departments.at(deptIndex).patchValue({
            id: dept.id,
            locationIndex: locIndex,
            departmentName: dept.departmentName,
            departmentDescription: dept.departmentDescription,
            categoryOfProducts: categoryForForm,
            otherCategoryType: otherCategoryType
          });

          this.onCategoryTypeChange({ target: { value: categoryForForm } }, deptIndex);

          // Populate users with departmentIndex reference
          if (dept.users && dept.users.length > 0) {
            console.log(`Department ${deptIndex + 1} has ${dept.users.length} users`);
            dept.users.forEach((user: any) => {
              console.log(`Adding user:`, user.firstName, user.lastName);
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
                password: '' // Leave empty on edit - only update if user changes it
              });
            });
          }
        });
      }
    });

    console.log('All locations populated successfully');
  }

  // ==================== BUTTON METHODS ====================

  /**
   * Save Step - Validates current step and moves to next
   */
  saveStep(): void {
    if (!this.isCurrentStepValid()) {
      this.messageService.showMessage('warning', 'Validation Error', 'Please fill all required fields on this step');
      this.supplierForm.markAllAsTouched();
      return;
    }

    this.messageService.showMessage('success', 'Saved', `Step ${this.currentStep} saved successfully`);
    
    // Move to next step
    if (this.currentStep < this.maxStep) {
      this.currentStep++;
    }
  }

  /**
   * Previous Step - Goes back without validation
   */
  prevStep(): void {
    if (this.currentStep > 1) {
      this.currentStep--;
    }
  }

  /**
   * Next Step - Moves forward without validation
   */
  nextStep(): void {
    if (this.currentStep < this.maxStep) {
      this.currentStep++;
    }
  }

  // ==================== VALIDATION METHODS ====================
  private isCurrentStepValid(): boolean {
    switch (this.currentStep) {
      case 1:
        return this.isSupplierDetailsValid();
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

  private isSupplierDetailsValid(): boolean {
    const fields = ['companyName', 'companyType', 'industrySector', 'contactPersonName', 'contactPersonDesignation',
                   'contactPersonEmail', 'contactPersonPhone', 'addressLine1', 'city', 'state', 
                   'postalCode', 'country'];
    
    const standardFieldsValid = fields.every(field => this.supplierForm.get(field)?.valid);

    const companyTypeControl = this.supplierForm.get('companyType');
    const otherCompanyTypeControl = this.supplierForm.get('otherCompanyType');
    const industrySectorControl = this.supplierForm.get('industrySector');
    const otherIndustrySectorControl = this.supplierForm.get('otherIndustrySector');

    const companyOtherFieldValid: boolean = companyTypeControl?.value === 'Others'
      ? (otherCompanyTypeControl?.valid ?? false)
      : true;

    const industrySectorOtherFieldValid: boolean = industrySectorControl?.value === 'Others'
      ? (otherIndustrySectorControl?.valid ?? false)
      : true;

    return standardFieldsValid && companyOtherFieldValid && industrySectorOtherFieldValid;
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
    return this.departments.length > 0 && this.departments.controls.every(dept => {
      const departmentNameValid = dept.get('departmentName')?.valid;
      const locationIndexValid = dept.get('locationIndex')?.valid;

      const categoryOfProductsControl = dept.get('categoryOfProducts');
      const otherCategoryTypeControl = dept.get('otherCategoryType');

      const categoryOtherFieldValid: boolean = categoryOfProductsControl?.value === 'Others'
        ? (otherCategoryTypeControl?.valid ?? false)
        : true;

      return departmentNameValid && locationIndexValid && categoryOtherFieldValid;
    });
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

  // ==================== SUBMIT ====================
  // onSubmit(): void {
  //   if (this.supplierForm.invalid) {
  //     this.supplierForm.markAllAsTouched();
  //     this.messageService.showMessage('error', 'Validation Error', 'Please fill all required fields');
  //     return;
  //   }

  //   this.isSubmitting = true;
  //   const formValue = JSON.parse(JSON.stringify(this.supplierForm.value));

  //   // Transform company type
  //   const customCompanyType = formValue.companyType === 'Others' ? formValue.otherCompanyType : null;
  //   if (customCompanyType) {
  //     formValue.companyType = customCompanyType;
  //   }
  //   delete formValue.otherCompanyType;

  //   // Transform industry sector
  //   const customIndustrySector = formValue.industrySector === 'Others' ? formValue.otherIndustrySector : null;
  //   if (customIndustrySector) {
  //     formValue.industrySector = customIndustrySector;
  //   }
  //   delete formValue.otherIndustrySector;

  //   // Transform location types
  //   formValue.locations?.forEach((loc: any) => {
  //     const customLocType = loc.otherLocationType;
  //     if (loc.locationType === 'Others' && customLocType) {
  //       loc.locationType = customLocType;
  //     }
  //     delete loc.otherLocationType;
  //     loc.departments = [];  // Initialize empty departments array
  //   });

  //   // Organize departments under their respective locations
  //   formValue.departments?.forEach((dept: any) => {
  //     const locationIndex = dept.locationIndex;
  //     if (formValue.locations[locationIndex]) {
  //       const { locationIndex: _, ...deptData } = dept;
        
  //       // Transform category type
  //       const customCategoryType = deptData.categoryOfProducts === 'Others' ? deptData.otherCategoryType : null;
  //       if (customCategoryType) {
  //         deptData.categoryOfProducts = customCategoryType;
  //       }
  //       delete deptData.otherCategoryType;
        
  //       deptData.users = [];  // Initialize empty users array
  //       formValue.locations[locationIndex].departments.push(deptData);
  //     }
  //   });

  //   // Organize users under their respective departments
  //   formValue.users?.forEach((user: any) => {
  //     const deptIndex = user.departmentIndex;
  //     const dept = formValue.departments[deptIndex];
  //     if (dept) {
  //       const locationIndex = dept.locationIndex;
  //       if (formValue.locations[locationIndex]) {
  //         const targetDept = formValue.locations[locationIndex].departments.find(
  //           (d: any) => d.departmentName === dept.departmentName
  //         );
  //         if (targetDept) {
  //           const { departmentIndex: _, ...userData } = user;
            
  //           // CRITICAL FIX FOR EDIT MODE:
  //           // If in edit mode and password is empty, remove it from payload
  //           if (this.mode === 'edit' && (!userData.password || userData.password.trim() === '')) {
  //             delete userData.password;
  //           }
            
  //           targetDept.users.push(userData);
  //         }
  //       }
  //     }
  //   });

  //   delete formValue.departments;
  //   delete formValue.users;

  //   const payload: Supplier = formValue;

  //   if (this.mode === 'create') {
  //     this.supplierService.createCompleteHierarchy(payload).subscribe({
  //       next: () => {
  //         this.messageService.showMessage('success', 'Success', 'Supplier created successfully');
  //         setTimeout(() => this.router.navigate(['/dashboard']), 1500);
  //       },
  //       error: (err: any) => {
  //         const errorMsg = err.error?.message || 'Failed to create supplier';
  //         this.messageService.showMessage('error', 'Error', errorMsg);
  //         this.isSubmitting = false;
  //       }
  //     });
  //   } else {
  //     this.supplierService.updateCompleteHierarchy(this.supplierId!, payload).subscribe({
  //       next: () => {
  //         this.messageService.showMessage('success', 'Success', 'Supplier updated successfully');
  //         setTimeout(() => this.router.navigate(['/dashboard']), 1500);
  //       },
  //       error: (err: any) => {
  //         const errorMsg = err.error?.message || 'Failed to update supplier';
  //         this.messageService.showMessage('error', 'Error', errorMsg);
  //         this.isSubmitting = false;
  //       }
  //     });
  //   }
  // }
  // ==================== CRITICAL FIX: onSubmit() method ====================
// Replace your existing onSubmit() method with this:

onSubmit(): void {
  if (this.supplierForm.invalid) {
    this.supplierForm.markAllAsTouched();
    this.messageService.showMessage('error', 'Validation Error', 'Please fill all required fields');
    return;
  }

  this.isSubmitting = true;
  
  // Deep clone form value
  const formValue = JSON.parse(JSON.stringify(this.supplierForm.value));

  // ✅ FIX 1: Handle "Others" company type
  if (formValue.companyType === 'Others' && formValue.otherCompanyType) {
    formValue.companyType = formValue.otherCompanyType;
  }
  delete formValue.otherCompanyType;

  // ✅ FIX 2: Handle "Others" industry sector
  if (formValue.industrySector === 'Others' && formValue.otherIndustrySector) {
    formValue.industrySector = formValue.otherIndustrySector;
  }
  delete formValue.otherIndustrySector;

  // ✅ FIX 3: Process locations with proper structure
  const processedLocations: any[] = [];
  
  if (formValue.locations && Array.isArray(formValue.locations)) {
    formValue.locations.forEach((loc: any) => {
      // Handle "Others" location type
      if (loc.locationType === 'Others' && loc.otherLocationType) {
        loc.locationType = loc.otherLocationType;
      }
      delete loc.otherLocationType;
      
      // ✅ CRITICAL: Initialize departments array for each location
      loc.departments = [];
      processedLocations.push(loc);
    });
  }

  // ✅ FIX 4: Map departments to their respective locations
  if (formValue.departments && Array.isArray(formValue.departments)) {
    formValue.departments.forEach((dept: any) => {
      const locationIndex = dept.locationIndex;
      
      // Validate locationIndex exists
      if (locationIndex === '' || locationIndex === null || locationIndex === undefined) {
        console.warn('⚠️ Department missing location assignment:', dept);
        return;
      }

      const locationIdx = parseInt(locationIndex, 10);
      
      if (processedLocations[locationIdx]) {
        // ✅ CRITICAL: Remove locationIndex before adding to location
        const { locationIndex: _, ...deptData } = dept;
        
        // Handle "Others" category type
        if (deptData.categoryOfProducts === 'Others' && deptData.otherCategoryType) {
          deptData.categoryOfProducts = deptData.otherCategoryType;
        }
        delete deptData.otherCategoryType;
        
        // Initialize users array for department
        deptData.users = [];
        
        processedLocations[locationIdx].departments.push(deptData);
      } else {
        console.warn(`⚠️ Location index ${locationIdx} not found for department:`, dept);
      }
    });
  }

  // ✅ FIX 5: Map users to their respective departments
  if (formValue.users && Array.isArray(formValue.users)) {
    formValue.users.forEach((user: any) => {
      const deptIndex = user.departmentIndex;
      
      // Validate departmentIndex exists
      if (deptIndex === '' || deptIndex === null || deptIndex === undefined) {
        console.warn('⚠️ User missing department assignment:', user);
        return;
      }

      const deptIdx = parseInt(deptIndex, 10);
      const department = formValue.departments?.[deptIdx];
      
      if (!department) {
        console.warn(`⚠️ Department index ${deptIdx} not found for user:`, user);
        return;
      }

      const locationIndex = department.locationIndex;
      const locationIdx = parseInt(locationIndex, 10);

      if (processedLocations[locationIdx]) {
        // Find the target department in the location
        const targetDept = processedLocations[locationIdx].departments.find(
          (d: any) => d.departmentName === department.departmentName
        );

        if (targetDept) {
          // ✅ CRITICAL: Remove departmentIndex before adding to department
          const { departmentIndex: _, ...userData } = user;
          
          // ✅ FIX 6: Handle password for edit mode
          if (this.mode === 'edit' && (!userData.password || userData.password.trim() === '')) {
            delete userData.password; // Don't send empty password on update
          }
          
          targetDept.users.push(userData);
        } else {
          console.warn('⚠️ Target department not found for user:', user);
        }
      } else {
        console.warn(`⚠️ Location index ${locationIdx} not found for user:`, user);
      }
    });
  }

  // ✅ FIX 7: Replace locations with processed nested structure
  formValue.locations = processedLocations;
  
  // ✅ FIX 8: Remove flat departments and users arrays from root
  delete formValue.departments;
  delete formValue.users;

  const payload: Supplier = formValue;

  console.log('%c[FINAL PAYLOAD]', 'color: #ff6600; font-weight: bold; font-size: 14px;', 
    JSON.stringify(payload, null, 2));

  // ✅ FIX 9: Use correct service methods
  if (this.mode === 'create') {
    this.supplierService.createCompleteHierarchy(payload).subscribe({
      next: (response) => {
        console.log('✅ Create response:', response);
        this.messageService.showMessage('success', 'Success', 'Supplier created successfully');
        setTimeout(() => this.router.navigate(['/dashboard']), 1500);
      },
      error: (err: any) => {
        console.error('❌ Create error:', err);
        const errorMsg = err.error?.message || err.message || 'Failed to create supplier';
        this.messageService.showMessage('error', 'Error', errorMsg);
        this.isSubmitting = false;
      }
    });
  } else if (this.supplierId) {
    this.supplierService.updateCompleteHierarchy(this.supplierId, payload).subscribe({
      next: (response) => {
        console.log('✅ Update response:', response);
        this.messageService.showMessage('success', 'Success', 'Supplier updated successfully');
        setTimeout(() => this.router.navigate(['/dashboard']), 1500);
      },
      error: (err: any) => {
        console.error('❌ Update error:', err);
        const errorMsg = err.error?.message || err.message || 'Failed to update supplier';
        this.messageService.showMessage('error', 'Error', errorMsg);
        this.isSubmitting = false;
      }
    });
  }
}
}