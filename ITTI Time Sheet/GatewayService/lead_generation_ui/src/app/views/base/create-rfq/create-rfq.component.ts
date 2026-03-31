
// import { Component, OnInit, ChangeDetectionStrategy, ChangeDetectorRef,HostListener } from '@angular/core';
// import { FormBuilder, FormGroup, Validators, FormArray, FormControl } from '@angular/forms';
// import { Router, ActivatedRoute } from '@angular/router';
// import { CommonModule } from '@angular/common';
// import { ReactiveFormsModule, FormsModule } from '@angular/forms';

// import { RFQService } from '../models/rfq.service';
// import { BuyerService } from '../dashboard/buyer-b.service';
// import { SupplierService } from '../dashboard/supplier.service';
// import { MessageService } from '../../../shared/service/message.service';
// import { StorageService } from '../../../shared/service/StorageService';
// import { RFQItem, RFQCreatePayload } from '../models/rfq.model';

// import { 
//   getFieldsByCompanyType, 
//   DynamicField,
//   getAllCompanyTypes
// } from '../models/company-type-fields.config';

// import { 
//   filterSupplierCategories,
//   getAllowedSupplierCategories,
//   getCategoryMappingDescription
// } from '../models/buyer-supplier-category-mapping.config';

// @Component({
//   selector: 'app-create-rfq',
//   templateUrl: './create-rfq.component.html',
//   styleUrls: ['./create-rfq.component.css'],
//   standalone: true,
//   imports: [CommonModule, ReactiveFormsModule, FormsModule],
//   changeDetection: ChangeDetectionStrategy.OnPush
// })
// export class CreateRFQComponent implements OnInit {

//   // ==================== FORM & STATE ====================
//   rfqForm!: FormGroup;
//   isSubmitting = false;
//   isSavingDraft = false;

//   // ==================== MODE & ID ====================
//   mode: 'create' | 'edit' | 'view' = 'create';
//   rfqId: number | null = null;

//   // ==================== LOGGED-IN BUYER DATA ====================
//   loggedInBuyer: any = null;
//   loggedInBuyerId: number | null = null;
//   buyerCompanyType: string = '';
//   userLocation: any = null;
  
//   // ==================== DYNAMIC FIELDS ====================
//   dynamicFieldsForCurrentCompanyType: DynamicField[] = [];
//   allCompanyTypes: string[] = getAllCompanyTypes();

//   // ==================== CUSTOM FIELDS PER ITEM ====================
//   customFieldsByItem: Map<number, DynamicField[]> = new Map();
//   showAddFieldForm: { [itemIndex: number]: boolean } = {};
//   addFieldForm: { [itemIndex: number]: FormGroup } = {};

//   // ==================== SUPPLIER CATEGORY/DEPARTMENT FILTERING ====================
//   allDepartments: any[] = [];
//   selectedCategory: string = '';
//   filteredSuppliers: any[] = [];
//   suppliersByDepartment: Map<string, any[]> = new Map();
//   suppliers: any[] = [];
//   filteredDepartmentsByBuyerCategory: string[] = [];

//   // ==================== SUPPLIER SELECTION (UPDATED) ====================
//   selectedSupplierIds: number[] = [];
//   selectedSupplierToAdd: number | null = null; // NEW: For dropdown selection

//   // ==================== SUPPLIER PAGINATION & SEARCH (KEPT FOR COMPATIBILITY) ====================
//   supplierSearchTerm: string = '';
//   suppliersPerPage: number = 12;
//   currentSuppliersPage: number = 1;
//   totalSupplierPages: number = 1;

//     // ==================== NEW MULTI-SELECT DROPDOWN PROPERTIES ====================
//   supplierDropdownOpen: boolean = false;
//   supplierDropdownSearch: string = '';
//   filteredSuppliersForDropdown: any[] = [];

//   // ==================== DROPDOWNS ====================
//   priorities = ['LOW', 'MEDIUM', 'HIGH', 'URGENT'];
//   uoms = ['Qty', 'Kg', 'L', 'Box', 'Piece', 'Set', 'Meter', 'Pack'];

//   // ==================== DATA LOADING TRACKING ====================
//   private dataLoadingState = {
//     suppliersLoaded: false,
//     buyerLoaded: false,
//     rfqDataLoaded: false
//   };

//   constructor(
//     private fb: FormBuilder,
//     private rfqService: RFQService,
//     private buyerService: BuyerService,
//     private supplierService: SupplierService,
//     private messageService: MessageService,
//     private router: Router,
//     private route: ActivatedRoute,
//     private cdr: ChangeDetectorRef
//   ) {}

//     @HostListener('document:click', ['$event'])
//   onDocumentClick(event: MouseEvent) {
//     const target = event.target as HTMLElement;
//     if (!target.closest('.multi-select-dropdown')) {
//       this.closeSupplierDropdown();
//     }
//   }

//   ngOnInit(): void {
//     this.initializeForm();
//     this.detectMode();
//     this.loadLoggedInBuyer();
//     this.loadSuppliers();
//   }

//     /**
//    * Toggle dropdown open/close
//    */
//   toggleSupplierDropdown(): void {
//     this.supplierDropdownOpen = !this.supplierDropdownOpen;
    
//     if (this.supplierDropdownOpen) {
//       this.supplierDropdownSearch = '';
//       this.filteredSuppliersForDropdown = [...this.filteredSuppliers];
//     }
    
//     this.cdr.markForCheck();
//   }
//     /**
//    * Close dropdown
//    */
//   closeSupplierDropdown(): void {
//     if (this.supplierDropdownOpen) {
//       this.supplierDropdownOpen = false;
//       this.supplierDropdownSearch = '';
//       this.cdr.markForCheck();
//     }
//   }
//    /**
//    * Search within dropdown
//    */
//   onSupplierDropdownSearch(): void {
//     const searchTerm = this.supplierDropdownSearch.toLowerCase().trim();
    
//     if (!searchTerm) {
//       this.filteredSuppliersForDropdown = [...this.filteredSuppliers];
//     } else {
//       this.filteredSuppliersForDropdown = this.filteredSuppliers.filter((supplier: any) => {
//         const companyName = (supplier.companyName || '').toLowerCase();
//         const contactName = (supplier.contactPersonName || '').toLowerCase();
//         const email = (supplier.contactPersonEmail || '').toLowerCase();
        
//         return companyName.includes(searchTerm) || 
//                contactName.includes(searchTerm) || 
//                email.includes(searchTerm);
//       });
//     }
    
//     this.cdr.markForCheck();
//   }
//    /**
//    * Clear search input
//    */
//   clearSupplierSearch(): void {
//     this.supplierDropdownSearch = '';
//     this.onSupplierDropdownSearch();
//   }
//     /**
//    * Get filtered suppliers for dropdown display
//    */
//   getFilteredSuppliersForDropdown(): any[] {
//     return this.filteredSuppliersForDropdown;
//   }
//     /**
//    * Select all suppliers from filtered list
//    */
//   selectAllSuppliers(): void {
//     const allIds = this.filteredSuppliersForDropdown.map(s => s.id);
    
//     allIds.forEach(id => {
//       if (!this.selectedSupplierIds.includes(id)) {
//         this.selectedSupplierIds.push(id);
//       }
//     });
    
//     this.rfqForm.get('selectedSupplierIds')?.setValue(this.selectedSupplierIds);
    
//     this.messageService.showMessage(
//       'success', 
//       'Selected', 
//       `${allIds.length} supplier(s) selected`
//     );
    
//     this.cdr.markForCheck();
//   }
//     /**
//    * Deselect all suppliers from filtered list
//    */
//   deselectAllSuppliers(): void {
//     const filteredIds = this.filteredSuppliersForDropdown.map(s => s.id);
    
//     this.selectedSupplierIds = this.selectedSupplierIds.filter(
//       id => !filteredIds.includes(id)
//     );
    
//     this.rfqForm.get('selectedSupplierIds')?.setValue(this.selectedSupplierIds);
    
//     this.messageService.showMessage(
//       'info', 
//       'Cleared', 
//       'Filtered suppliers deselected'
//     );
    
//     this.cdr.markForCheck();
//   }
//     /**
//    * Get count of selected suppliers from filtered list
//    */
//   getSelectedFromFiltered(): any[] {
//     const filteredIds = this.filteredSuppliersForDropdown.map(s => s.id);
//     return this.selectedSupplierIds.filter(id => filteredIds.includes(id));
//   }
//    /**
//    * Toggle individual supplier selection in dropdown
//    */
//   toggleSupplierInDropdown(supplierId: number): void {
//     const index = this.selectedSupplierIds.indexOf(supplierId);
    
//     if (index > -1) {
//       this.selectedSupplierIds.splice(index, 1);
//     } else {
//       this.selectedSupplierIds.push(supplierId);
//     }
    
//     this.rfqForm.get('selectedSupplierIds')?.setValue(this.selectedSupplierIds);
//     this.cdr.markForCheck();
//   }
//     /**
//    * Get placeholder text for dropdown toggle
//    */
//   getDropdownPlaceholderText(): string {
//     const count = this.getSelectedSuppliersCount();
//     const total = this.getFilteredSuppliers().length;
    
//     if (count === 0) {
//       return 'Select suppliers...';
//     } else if (count === total) {
//       return `All suppliers selected (${count})`;
//     } else {
//       return `${count} supplier(s) selected`;
//     }
//   }

//   // ==================== LOAD LOGGED-IN BUYER ====================

//   private loadLoggedInBuyer(): void {
//     try {
//       const buyerData = StorageService.getBuyerDetails();
      
//       console.log('%c[STORAGE BUYER DATA]', 'color: #0066cc; font-weight: bold;', buyerData);

//       let buyerId: number | null = null;

//       if (buyerData?.id && buyerData.id !== 'N/A') {
//         buyerId = Number(buyerData.id);
//       } else if (buyerData?.buyerId) {
//         buyerId = Number(buyerData.buyerId);
//       } else {
//         const storedId = localStorage.getItem('buyerId') || localStorage.getItem('userId');
//         if (storedId && storedId !== 'N/A') {
//           buyerId = Number(storedId);
//         }
//       }

//       if (!buyerId) {
//         console.error('%c[ERROR] No buyer ID found', 'color: #cc0000; font-weight: bold;');
//         this.messageService.showMessage('warning', 'Warning', 'Please login as a buyer first');
//         this.router.navigate(['/buyer-login']);
//         return;
//       }

//       this.loggedInBuyerId = buyerId;

//       console.log('%c[FETCHING BUYER] ID:', 'color: #0066cc; font-weight: bold;', buyerId);

//       this.buyerService.getBuyerById(this.loggedInBuyerId).subscribe({
//         next: (response: any) => {
//           console.log('%c[BUYER API RESPONSE]', 'color: #00aa00; font-weight: bold;', response);

//           const buyerDetails = response?.data || response;

//           if (!buyerDetails || !buyerDetails.id) {
//             console.error('%c[ERROR] Invalid buyer data structure', 'color: #cc0000;', response);
//             this.messageService.showMessage('error', 'Error', 'Failed to load buyer details');
//             return;
//           }

//           this.loggedInBuyer = {
//             id: buyerDetails.id,
//             companyName: buyerDetails.companyName || 'N/A',
//             companyType: buyerDetails.companyType || 'Others',
//             email: buyerDetails.contactPersonEmail || localStorage.getItem('username') || 'N/A',
//             contactPersonName: buyerDetails.contactPersonName || localStorage.getItem('fullName') || 'N/A',
//             contactPersonPhone: buyerDetails.contactPersonPhone || localStorage.getItem('phone') || 'N/A'
//           };

//           this.buyerCompanyType = this.loggedInBuyer.companyType;
//           this.dynamicFieldsForCurrentCompanyType = getFieldsByCompanyType(this.buyerCompanyType);

//           console.log('%c[✅ BUYER LOADED SUCCESSFULLY]', 'color: #00aa00; font-weight: bold; font-size: 14px;');
//           console.log('  📌 Company:', this.loggedInBuyer.companyName);
//           console.log('  📌 Type:', this.buyerCompanyType);

//           this.loadBuyerLocations(buyerDetails);
//           this.updateDynamicFieldsForAllItems();

//           if (this.allDepartments.length > 0) {
//             console.log('%c[FILTERING DEPARTMENTS]', 'color: #9c27b0;', 'Departments already loaded, applying filter...');
//             this.filterDepartmentsByBuyerCategory();
//           }

//           this.dataLoadingState.buyerLoaded = true;
//           this.cdr.markForCheck();
//         },
//         error: (error: any) => {
//           console.error('%c[ERROR] Failed to load buyer', 'color: #cc0000; font-weight: bold;', error);
//           this.messageService.showMessage('error', 'Error', 'Failed to load buyer details');
//         }
//       });
//     } catch (error) {
//       console.error('%c[ERROR] Exception in loadLoggedInBuyer', 'color: #cc0000;', error);
//       this.messageService.showMessage('error', 'Error', 'Failed to load buyer details');
//     }
//   }

//   // ==================== LOAD BUYER LOCATIONS ====================

//   private loadBuyerLocations(buyerDetails: any): void {
//     if (!buyerDetails?.locations || !Array.isArray(buyerDetails.locations)) {
//       console.warn('%c[WARNING] No locations available', 'color: #ff9800;');
//       this.messageService.showMessage('warning', 'Warning', 'No locations available for this buyer');
//       return;
//     }

//     console.log('%c[LOCATIONS]', 'color: #0066cc;', `Found ${buyerDetails.locations.length} locations`);

//     const userLocationId = localStorage.getItem('locationId');
    
//     let selectedLoc = null;
    
//     if (userLocationId) {
//       selectedLoc = buyerDetails.locations.find((loc: any) => loc.id === Number(userLocationId));
//       console.log('  🔍 Looking for location ID:', userLocationId, selectedLoc ? '✅ Found' : '❌ Not found');
//     }
    
//     if (!selectedLoc && buyerDetails.locations.length > 0) {
//       selectedLoc = buyerDetails.locations[0];
//       console.log('  📍 Using first location as default');
//     }

//     if (selectedLoc) {
//       this.userLocation = {
//         id: selectedLoc.id,
//         locationName: selectedLoc.locationName || 'N/A',
//         city: selectedLoc.city || 'N/A',
//         state: selectedLoc.state || 'N/A',
//         postalCode: selectedLoc.postalCode || '',
//         country: selectedLoc.country || ''
//       };

//       this.rfqForm.patchValue({
//         locationId: this.userLocation.id
//       }, { emitEvent: false });

//       console.log('%c[✅ LOCATION SET]', 'color: #00aa00;', this.userLocation.locationName);
//     } else {
//       console.warn('%c[WARNING] Could not determine location', 'color: #ff9800;');
//       this.messageService.showMessage('warning', 'Warning', 'Could not determine your location');
//     }

//     this.cdr.markForCheck();
//   }

//   // ==================== FORM INITIALIZATION ====================

//   private initializeForm(): void {
//     this.rfqForm = this.fb.group({
//       rfqTitle: ['', [Validators.required, Validators.minLength(5)]],
//       rfqDescription: [''],
//       dueDate: ['', Validators.required],
//       itemRequiredDate: ['', Validators.required],
//       priority: ['MEDIUM', Validators.required],
//       locationId: ['', Validators.required],
//       itemsArray: this.fb.array([]),
//       selectedSupplierIds: [[], Validators.required],
//       paymentTerms: [''],
//       deliveryTerms: [''],
//       justification: [''],
//       allowSplitPO: [false],
//       preferredVendorsOnly: [false],
//       approvalRequired: [true, { nonNullable: true }]
//     });

//     if (this.itemsArray.length === 0) {
//       this.addItem();
//     }
//   }

//   // ==================== MODE DETECTION ====================

//   private detectMode(): void {
//     this.route.paramMap.subscribe(params => {
//       const id = params.get('id');
//       const mode = params.get('mode');

//       if (id) {
//         this.rfqId = +id;
//         this.mode = mode === 'view' ? 'view' : 'edit';
//       } else {
//         this.mode = 'create';
//       }
//       this.cdr.markForCheck();
//     });
//   }

//   // ==================== DATA LOADING ====================

//   private loadSuppliers(): void {
//     this.supplierService.getAllSuppliers().subscribe({
//       next: (response: any) => {
//         if (Array.isArray(response)) {
//           this.suppliers = response;
//         } else if (response?.data && Array.isArray(response.data)) {
//           this.suppliers = response.data;
//         }

//         this.extractDepartmentsFromSuppliers();
//         this.dataLoadingState.suppliersLoaded = true;

//         if (this.rfqId && !this.dataLoadingState.rfqDataLoaded) {
//           setTimeout(() => this.loadRFQData(this.rfqId!), 100);
//         }

//         this.cdr.markForCheck();
//       },
//       error: (error: any) => {
//         console.error('Load Suppliers Error:', error);
//         this.messageService.showMessage('error', 'Error', 'Failed to load suppliers');
//       }
//     });
//   }

//   private extractDepartmentsFromSuppliers(): void {
//     const departmentMap = new Map<string, any[]>();

//     this.suppliers.forEach((supplier: any) => {
//       if (supplier?.locations && Array.isArray(supplier.locations)) {
//         supplier.locations.forEach((location: any) => {
//           if (location?.departments && Array.isArray(location.departments)) {
//             location.departments.forEach((dept: any) => {
//               if (dept && !dept.isDeleted) {
//                 const deptName = dept.departmentName;

//                 if (!departmentMap.has(deptName)) {
//                   departmentMap.set(deptName, []);
//                 }

//                 const deptSuppliers = departmentMap.get(deptName) || [];
//                 if (!deptSuppliers.find((s: any) => s.id === supplier.id)) {
//                   deptSuppliers.push({
//                     id: supplier.id,
//                     companyName: supplier.companyName,
//                     contactPersonName: supplier.contactPersonName,
//                     contactPersonEmail: supplier.contactPersonEmail
//                   });
//                   departmentMap.set(deptName, deptSuppliers);
//                 }
//               }
//             });
//           }
//         });
//       }
//     });

//     this.allDepartments = Array.from(departmentMap.keys()).sort();
//     this.suppliersByDepartment = departmentMap;
//     this.filterDepartmentsByBuyerCategory();
//     this.cdr.markForCheck();
//   }

//   // ==================== FILTER DEPARTMENTS BY BUYER CATEGORY ====================

//    private filterDepartmentsByBuyerCategory(): void {
//     if (!this.buyerCompanyType) {
//       this.filteredDepartmentsByBuyerCategory = [...this.allDepartments];
//       return;
//     }

//     this.filteredDepartmentsByBuyerCategory = filterSupplierCategories(
//       this.buyerCompanyType,
//       this.allDepartments
//     );

//     this.cdr.markForCheck();
//   }

//   // ==================== UPDATE DYNAMIC FIELDS FOR ALL ITEMS ====================

//   private updateDynamicFieldsForAllItems(): void {
//     const itemsArray = this.rfqForm.get('itemsArray') as FormArray;
    
//     itemsArray.controls.forEach((itemGroup, index) => {
//       if (itemGroup instanceof FormGroup) {
//         let dynamicFieldsGroup = itemGroup.get('dynamicFields') as FormGroup;
        
//         if (!dynamicFieldsGroup) {
//           dynamicFieldsGroup = this.fb.group({});
//           itemGroup.addControl('dynamicFields', dynamicFieldsGroup);
//         }
        
//         Object.keys(dynamicFieldsGroup.controls).forEach(key => {
//           if (!key.startsWith('custom_')) {
//             dynamicFieldsGroup.removeControl(key);
//           }
//         });
        
//         this.dynamicFieldsForCurrentCompanyType.forEach(field => {
//           if (!dynamicFieldsGroup.get(field.fieldName)) {
//             dynamicFieldsGroup.addControl(field.fieldName, new FormControl(''));
//           }
//         });
        
//         itemGroup.patchValue({
//           companyType: this.buyerCompanyType || 'Others'
//         }, { emitEvent: false });
//       }
//     });
    
//     this.cdr.markForCheck();
//   }

//   // ==================== ITEMS MANAGEMENT ====================

//   get itemsArray(): FormArray {
//     return this.rfqForm.get('itemsArray') as FormArray;
//   }

//   addItem(item?: RFQItem): void {
//     const dynamicFieldsGroup: { [key: string]: any } = {};

//     this.dynamicFieldsForCurrentCompanyType.forEach(field => {
//       const fieldValue = item?.dynamicFields?.[field.fieldName] || '';
//       dynamicFieldsGroup[field.fieldName] = [fieldValue];
//     });

//     if (item?.dynamicFields) {
//       Object.keys(item.dynamicFields).forEach(key => {
//         if (key.startsWith('custom_') && item.dynamicFields) {
//           dynamicFieldsGroup[key] = [item.dynamicFields[key]];
//         }
//       });
//     }

//     const itemGroup = this.fb.group({
//       itemDescription: [item?.itemDescription || '', Validators.required],
//       quantity: [item?.quantity || 1, [Validators.required, Validators.min(1)]],
//       uom: [item?.uom || 'Qty', Validators.required],
//       specifications: [item?.specifications || ''],
//       dynamicFields: this.fb.group(dynamicFieldsGroup),
//       companyType: [item?.companyType || this.buyerCompanyType || 'Others']
//     });

//     this.itemsArray.push(itemGroup);

//     const itemIndex = this.itemsArray.length - 1;
//     this.customFieldsByItem.set(itemIndex, []);
//     this.showAddFieldForm[itemIndex] = false;

//     if (item?.dynamicFields) {
//       const customFields: DynamicField[] = [];
//       Object.keys(item.dynamicFields).forEach(key => {
//         if (key.startsWith('custom_')) {
//           customFields.push({
//             fieldName: key,
//             fieldLabel: key.replace('custom_', '').replace(/_/g, ' '),
//             fieldType: 'text',
//             isCustom: true
//           });
//         }
//       });
//       if (customFields.length > 0) {
//         this.customFieldsByItem.set(itemIndex, customFields);
//         console.log('%c[CUSTOM FIELDS RESTORED]', 'color: #00aa00;', {
//           item: itemIndex,
//           count: customFields.length
//         });
//       }
//     }
    
//     console.log('%c[ITEM ADDED]', 'color: #00aa00;', {
//       index: this.itemsArray.length,
//       companyType: this.buyerCompanyType,
//       dynamicFields: this.dynamicFieldsForCurrentCompanyType.length
//     });
    
//     this.cdr.markForCheck();
//   }

//   removeItem(index: number): void {
//     if (this.itemsArray.length > 1) {
//       this.itemsArray.removeAt(index);
//       this.customFieldsByItem.delete(index);
//       delete this.showAddFieldForm[index];
//       delete this.addFieldForm[index];
//       this.messageService.showMessage('success', 'Removed', 'Item removed successfully');
//       this.cdr.markForCheck();
//     } else {
//       this.messageService.showMessage('warning', 'Warning', 'At least one item is required');
//     }
//   }

//   // ==================== DYNAMIC FIELD HELPER METHODS ====================

//   getDynamicFieldsForItem(itemIndex: number): DynamicField[] {
//     return this.dynamicFieldsForCurrentCompanyType;
//   }

//   getAllFieldsForItem(itemIndex: number): DynamicField[] {
//     const predefinedFields = this.getDynamicFieldsForItem(itemIndex);
//     const customFields = this.customFieldsByItem.get(itemIndex) || [];
//     return [...predefinedFields, ...customFields];
//   }

//   getDynamicFieldValue(itemIndex: number, fieldName: string): any {
//     return this.itemsArray.at(itemIndex)?.get(`dynamicFields.${fieldName}`)?.value;
//   }

//   setDynamicFieldValue(itemIndex: number, fieldName: string, value: any): void {
//     this.itemsArray.at(itemIndex)?.get(`dynamicFields.${fieldName}`)?.setValue(value);
//   }

//   getFieldOptions(field: DynamicField): string[] {
//     return field.options || [];
//   }

//   // ==================== CUSTOM FIELDS FUNCTIONALITY ====================

//   initializeCustomFieldsForItem(itemIndex: number): void {
//     if (!this.customFieldsByItem.has(itemIndex)) {
//       this.customFieldsByItem.set(itemIndex, []);
//     }
//   }

//   openAddFieldModal(itemIndex: number): void {
//     this.showAddFieldForm[itemIndex] = true;
    
//     if (!this.addFieldForm[itemIndex]) {
//       this.addFieldForm[itemIndex] = this.fb.group({
//         fieldLabel: ['', [Validators.required, Validators.minLength(3)]],
//         fieldType: ['text', Validators.required],
//         placeholder: [''],
//         maxLength: [255],
//         option1: [''],
//         option2: [''],
//         option3: [''],
//         option4: ['']
//       });
//     }
    
//     this.cdr.markForCheck();
//   }

//   closeAddFieldModal(itemIndex: number): void {
//     this.showAddFieldForm[itemIndex] = false;
//     this.cdr.markForCheck();
//   }

//   addCustomFieldToItem(itemIndex: number): void {
//     const form = this.addFieldForm[itemIndex];
    
//     if (!form || !form.valid) {
//       console.warn('Form is invalid');
//       return;
//     }

//     const formValue = form.value;
//     const fieldType = formValue.fieldType;
    
//     let options: string[] | undefined = undefined;
//     if (fieldType === 'select') {
//       options = [formValue.option1, formValue.option2, formValue.option3, formValue.option4]
//         .filter((opt: string) => opt && opt.trim());
      
//       if (options.length === 0) {
//         this.messageService.showMessage('warning', 'Warning', 'Please add at least one option for select field');
//         return;
//       }
//     }

//     const newField: DynamicField = {
//       fieldName: 'custom_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9),
//       fieldLabel: formValue.fieldLabel,
//       fieldType: fieldType,
//       placeholder: formValue.placeholder,
//       options: options,
//       maxLength: fieldType === 'textarea' ? 500 : formValue.maxLength || 255,
//       isCustom: true
//     };

//     this.initializeCustomFieldsForItem(itemIndex);
//     const customFields = this.customFieldsByItem.get(itemIndex)!;
//     customFields.push(newField);

//     console.log('%c[CUSTOM FIELD ADDED]', 'color: #00aa00; font-weight: bold;', {
//       item: itemIndex,
//       field: newField.fieldLabel,
//       type: newField.fieldType
//     });

//     const itemGroup = this.itemsArray.at(itemIndex) as FormGroup;
//     const dynamicFieldsGroup = itemGroup.get('dynamicFields') as FormGroup;
    
//     if (dynamicFieldsGroup) {
//       dynamicFieldsGroup.addControl(newField.fieldName, new FormControl(''));
//     }

//     form.reset({
//       fieldLabel: '',
//       fieldType: 'text',
//       placeholder: '',
//       maxLength: 255,
//       option1: '',
//       option2: '',
//       option3: '',
//       option4: ''
//     });
    
//     this.closeAddFieldModal(itemIndex);
//     this.messageService.showMessage('success', 'Success', 'Custom field added successfully');
//     this.cdr.markForCheck();
//   }

//   removeCustomField(itemIndex: number, fieldName: string): void {
//     const customFields = this.customFieldsByItem.get(itemIndex);
    
//     if (customFields) {
//       const index = customFields.findIndex(f => f.fieldName === fieldName);
//       if (index > -1) {
//         customFields.splice(index, 1);
        
//         const itemGroup = this.itemsArray.at(itemIndex) as FormGroup;
//         const dynamicFieldsGroup = itemGroup.get('dynamicFields') as FormGroup;
        
//         if (dynamicFieldsGroup) {
//           dynamicFieldsGroup.removeControl(fieldName);
//         }
        
//         console.log('%c[CUSTOM FIELD REMOVED]', 'color: #ff6600;', fieldName);
//         this.messageService.showMessage('success', 'Removed', 'Custom field removed successfully');
//         this.cdr.markForCheck();
//       }
//     }
//   }

//   isCustomField(fieldName: string): boolean {
//     return fieldName.startsWith('custom_');
//   }

//   // ==================== CATEGORY CHANGE ====================

//  onCategoryChange(event: any): void {
//     this.selectedCategory = event.target.value;
//     this.selectedSupplierIds = [];
//     this.supplierDropdownOpen = false;
//     this.supplierDropdownSearch = '';

//     if (this.selectedCategory) {
//       this.filteredSuppliers = this.suppliersByDepartment.get(this.selectedCategory) || [];
//       this.filteredSuppliersForDropdown = [...this.filteredSuppliers];

//       console.log('%c[CATEGORY SELECTED]', 'color: #0066cc;', {
//         category: this.selectedCategory,
//         suppliersAvailable: this.filteredSuppliers.length
//       });
//     } else {
//       this.filteredSuppliers = [];
//       this.filteredSuppliersForDropdown = [];
//     }

//     this.rfqForm.get('selectedSupplierIds')?.setValue([]);
//     this.cdr.markForCheck();
//   }

//   private updateSupplierPagination(): void {
//     this.totalSupplierPages = Math.ceil(this.getFilteredSuppliers().length / this.suppliersPerPage);
//     if (this.totalSupplierPages === 0) {
//       this.totalSupplierPages = 1;
//     }
//   }

//   onSupplierSearch(searchTerm: string): void {
//     this.supplierSearchTerm = searchTerm.toLowerCase();
//     this.currentSuppliersPage = 1;
//     this.updateSupplierPagination();
//     this.cdr.markForCheck();
//   }

//    getFilteredSuppliers(): any[] {
//     return this.filteredSuppliers;
//   }

//   getPaginatedSuppliers(): any[] {
//     const filtered = this.getFilteredSuppliers();
//     const startIndex = (this.currentSuppliersPage - 1) * this.suppliersPerPage;
//     const endIndex = startIndex + this.suppliersPerPage;
//     return filtered.slice(startIndex, endIndex);
//   }

//   goToSupplierPage(page: number): void {
//     if (page >= 1 && page <= this.totalSupplierPages) {
//       this.currentSuppliersPage = page;
//       this.cdr.markForCheck();
//     }
//   }

//   getSupplierPageNumbers(): number[] {
//     const pages = [];
//     const maxVisible = 5;
//     let startPage = Math.max(1, this.currentSuppliersPage - Math.floor(maxVisible / 2));
//     let endPage = Math.min(this.totalSupplierPages, startPage + maxVisible - 1);

//     if (endPage - startPage + 1 < maxVisible) {
//       startPage = Math.max(1, endPage - maxVisible + 1);
//     }

//     for (let i = startPage; i <= endPage; i++) {
//       pages.push(i);
//     }
//     return pages;
//   }

//   // ==================== NEW SUPPLIER SELECTION METHODS ====================

//   /**
//    * Add supplier from dropdown to selected list
//    */
//   addSupplierToSelection(): void {
//     if (!this.selectedSupplierToAdd) {
//       return;
//     }

//     const supplierId = Number(this.selectedSupplierToAdd);

//     if (!this.selectedSupplierIds.includes(supplierId)) {
//       this.selectedSupplierIds.push(supplierId);
//       this.rfqForm.get('selectedSupplierIds')?.setValue(this.selectedSupplierIds);
      
//       console.log('%c[SUPPLIER ADDED]', 'color: #00aa00;', {
//         supplierId: supplierId,
//         totalSelected: this.selectedSupplierIds.length
//       });

//       this.messageService.showMessage('success', 'Added', 'Supplier added successfully');
//     }

//     // Reset dropdown
//     this.selectedSupplierToAdd = null;
//     this.cdr.markForCheck();
//   }

//   /**
//    * Remove supplier from selected list
//    */
//    removeSupplierFromSelection(supplierId: number): void {
//     const index = this.selectedSupplierIds.indexOf(supplierId);
    
//     if (index > -1) {
//       this.selectedSupplierIds.splice(index, 1);
//       this.rfqForm.get('selectedSupplierIds')?.setValue(this.selectedSupplierIds);
      
//       this.messageService.showMessage('info', 'Removed', 'Supplier removed from selection');
//       this.cdr.markForCheck();
//     }
//   }

//   /**
//    * Get supplier details by ID
//    */
//   getSupplierById(supplierId: number): any {
//     return this.filteredSuppliers.find(s => s.id === supplierId) || 
//            this.suppliers.find(s => s.id === supplierId);
//   }

//   /**
//    * TrackBy function for supplier list (performance optimization)
//    */
//   trackBySupplier(index: number, supplier: any): number {
//     return supplier.id;
//   }
//     /**
//    * TrackBy function for selected supplier IDs
//    */
//   trackBySupplierId(index: number, supplierId: number): number {
//     return supplierId;
//   }

//   /**
//    * LEGACY METHOD - kept for backward compatibility
//    * You can remove this if you're not using the old checkbox UI
//    */
//   toggleSupplierSelection(supplierId: number): void {
//     const index = this.selectedSupplierIds.indexOf(supplierId);

//     if (index > -1) {
//       this.selectedSupplierIds.splice(index, 1);
//     } else {
//       this.selectedSupplierIds.push(supplierId);
//     }

//     this.rfqForm.get('selectedSupplierIds')?.setValue(this.selectedSupplierIds);
//     this.cdr.markForCheck();
//   }

//   isSupplierSelected(supplierId: number): boolean {
//     return this.selectedSupplierIds.includes(supplierId);
//   }

//   getSelectedSuppliersCount(): number {
//     return this.selectedSupplierIds.length;
//   }

//   // ==================== LOAD RFQ DATA (FOR EDIT/VIEW) ====================

//   private loadRFQData(id: number): void {
//     console.log('%c[LOADING RFQ DATA]', 'color: #0066cc; font-weight: bold;', {
//       id: id,
//       suppliersLoaded: this.dataLoadingState.suppliersLoaded,
//       departmentsCount: this.suppliersByDepartment.size
//     });

//     if (!this.dataLoadingState.suppliersLoaded) {
//       console.warn('%c[WAITING FOR SUPPLIERS]', 'color: #ff9800;');
//       setTimeout(() => this.loadRFQData(id), 200);
//       return;
//     }

//     this.rfqService.getRFQById(id).subscribe({
//       next: (response: any) => {
//         const rfq = response?.data;

//         console.log('%c[RFQ DATA RECEIVED]', 'color: #00aa00;', {
//           title: rfq?.rfqTitle,
//           items: rfq?.items?.length || 0,
//           suppliers: rfq?.selectedSuppliers?.length || 0
//         });

//         if (!rfq) {
//           this.messageService.showMessage('error', 'Error', 'RFQ not found');
//           this.router.navigate(['/rfq-dashboard']);
//           return;
//         }

//         this.rfqForm.patchValue({
//           rfqTitle: rfq.rfqTitle || '',
//           rfqDescription: rfq.rfqDescription || '',
//           dueDate: rfq.dueDate ? this.formatDateForInput(rfq.dueDate) : '',
//           itemRequiredDate: rfq.itemRequiredDate ? this.formatDateForInput(rfq.itemRequiredDate) : '',
//           priority: rfq.priority || 'MEDIUM',
//           locationId: rfq.location?.id || '',
//           paymentTerms: rfq.paymentTerms || '',
//           deliveryTerms: rfq.deliveryTerms || '',
//           justification: rfq.justification || '',
//           allowSplitPO: rfq.allowSplitPO || false,
//           preferredVendorsOnly: rfq.preferredVendorsOnly || false,
//           approvalRequired: true
//         }, { emitEvent: false });

//         if (rfq.location?.id) {
//           this.userLocation = rfq.location;
//         }

//         if (rfq.items && Array.isArray(rfq.items) && rfq.items.length > 0) {
//           console.log('%c[LOADING ITEMS]', 'color: #0066cc;', rfq.items.length + ' items');
          
//           while (this.itemsArray.length > 0) {
//             this.itemsArray.removeAt(0);
//           }
          
//           rfq.items.forEach((item: any) => {
//             this.addItem(item);
//           });

//           console.log('%c[✅ ITEMS LOADED]', 'color: #00aa00;', this.itemsArray.length);
//         } else {
//           if (this.itemsArray.length === 0) {
//             this.addItem();
//           }
//         }

//         if (rfq.selectedSuppliers && Array.isArray(rfq.selectedSuppliers) && rfq.selectedSuppliers.length > 0) {
//           console.log('%c[LOADING SUPPLIERS]', 'color: #0066cc;', rfq.selectedSuppliers.length + ' suppliers');
          
//           const supplierIds = rfq.selectedSuppliers.map((s: any) => s.id);
//           this.selectedSupplierIds = [...supplierIds];

//           const firstSupplierId = rfq.selectedSuppliers[0]?.id;
//           let foundCategory = '';

//           for (const [dept, suppliers] of this.suppliersByDepartment.entries()) {
//             if (suppliers.find((s: any) => s.id === firstSupplierId)) {
//               foundCategory = dept;
//               break;
//             }
//           }

//           if (foundCategory) {
//             this.selectedCategory = foundCategory;
//             this.filteredSuppliers = this.suppliersByDepartment.get(foundCategory) || [];
//             this.rfqForm.get('selectedSupplierIds')?.setValue(supplierIds, { emitEvent: false });

//             console.log('%c[✅ SUPPLIERS LOADED]', 'color: #00aa00;', {
//               category: foundCategory,
//               suppliers: supplierIds.length,
//               availableInCategory: this.filteredSuppliers.length
//             });

//             this.updateSupplierPagination();
//           } else {
//             console.warn('%c[WARNING] Could not find supplier category', 'color: #ff9800;');
//             this.selectedSupplierIds = [];
//           }
//         } else {
//           this.selectedSupplierIds = [];
//         }

//         if (this.mode === 'view') {
//           this.rfqForm.disable({ emitEvent: false });
//         }

//         this.dataLoadingState.rfqDataLoaded = true;
//         this.cdr.markForCheck();

//         console.log('%c[✅ RFQ DATA FULLY LOADED]', 'color: #00aa00; font-weight: bold;');
//       },
//       error: (error: any) => {
//         console.error('Load RFQ Error:', error);
//         this.messageService.showMessage('error', 'Error', 'Failed to load RFQ');
//         this.router.navigate(['/rfq-dashboard']);
//       }
//     });
//   }

//   private formatDateForInput(dateString: string): string {
//     if (!dateString) return '';
//     try {
//       const date = new Date(dateString);
//       const year = date.getFullYear();
//       const month = String(date.getMonth() + 1).padStart(2, '0');
//       const day = String(date.getDate()).padStart(2, '0');
//       return `${year}-${month}-${day}`;
//     } catch (e) {
//       console.error('Date formatting error:', dateString);
//       return '';
//     }
//   }

//   // ==================== VALIDATION ====================

//   isFormValid(): boolean {
//     return this.rfqForm.valid && this.itemsArray.length > 0 && this.getSelectedSuppliersCount() > 0;
//   }

//   areItemsValid(): boolean {
//     return this.itemsArray.controls.every((ctrl: any) => ctrl.valid);
//   }

//   // ==================== BUILD PAYLOAD ====================

//   private buildPayload(): RFQCreatePayload {
//     const form = this.rfqForm.value;

//     const dueDate = this.formatDateToISO(form.dueDate);
//     const itemRequiredDate = this.formatDateToISO(form.itemRequiredDate);

//     return {
//       rfqTitle: form.rfqTitle?.trim() || '',
//       rfqDescription: form.rfqDescription?.trim() || '',
//       dueDate: dueDate,
//       itemRequiredDate: itemRequiredDate,
//       priority: form.priority || 'MEDIUM',
//       paymentTerms: form.paymentTerms?.trim() || null,
//       deliveryTerms: form.deliveryTerms?.trim() || null,
//       justification: form.justification?.trim() || null,
//       allowSplitPO: Boolean(form.allowSplitPO) || false,
//       preferredVendorsOnly: Boolean(form.preferredVendorsOnly) || false,
//       approvalRequired: true
//     };
//   }

//   private formatDateToISO(dateString: string): string {
//     if (!dateString) {
//       return new Date().toISOString();
//     }

//     if (dateString.includes('T')) {
//       return dateString;
//     }

//     const date = new Date(dateString + 'T00:00:00');

//     if (isNaN(date.getTime())) {
//       console.warn('Invalid date format:', dateString);
//       return new Date().toISOString();
//     }

//     return date.toISOString();
//   }

//   // ==================== SUBMIT & SAVE ====================

//   submitRFQ(): void {
//     if (!this.isFormValid()) {
//       this.rfqForm.markAllAsTouched();
//       this.messageService.showMessage('error', 'Validation Error',
//         'Please fill all required fields: RFQ Title, At least one Item, and At least one Supplier');
//       return;
//     }

//     this.isSubmitting = true;
//     const payload = this.buildPayload();
//     const locationId = Number(this.rfqForm.get('locationId')?.value);
//     const userId = this.getCurrentUserId();

//     if (this.mode === 'edit' && this.rfqId) {
//       this.updateRFQFlow(this.rfqId, payload);
//     } else {
//       this.createRFQFlow(this.loggedInBuyerId!, locationId, userId, payload);
//     }
//   }

//   private createRFQFlow(buyerId: number, locationId: number, userId: number, payload: RFQCreatePayload): void {
//     this.rfqService.createRFQ(buyerId, locationId, userId, payload).subscribe({
//       next: (response: any) => {
//         const rfqId = response.data?.id;

//         if (!rfqId) {
//           console.error('No RFQ ID in response:', response);
//           this.messageService.showMessage('error', 'Error', 'Failed to create RFQ');
//           this.isSubmitting = false;
//           return;
//         }

//         this.addItemsToRFQ(rfqId, this.rfqForm.get('itemsArray')?.value)
//           .then(() => this.addSuppliersToRFQ(rfqId, this.selectedSupplierIds))
//           .then(() => {
//             this.messageService.showMessage('success', 'Success', `${response.data?.rfqNumber} created successfully!`);
//             setTimeout(() => this.router.navigate(['/rfq-dashboard']), 1500);
//           })
//           .catch((error: any) => {
//             console.error('Submission Error:', error);
//             this.messageService.showMessage('error', 'Partial Error', 'RFQ created but failed to add items/suppliers.');
//             this.isSubmitting = false;
//           });
//       },
//       error: (error: any) => {
//         console.error('Create RFQ Error:', error);
//         const errorMsg = error.status === 400 ? (error.error?.message || 'Invalid RFQ data') : 'Failed to create RFQ';
//         this.messageService.showMessage('error', 'Error', errorMsg);
//         this.isSubmitting = false;
//       }
//     });
//   }

//   private updateRFQFlow(rfqId: number, payload: RFQCreatePayload): void {
//     this.rfqService.updateRFQ(rfqId, payload).subscribe({
//       next: () => {
//         this.addItemsToRFQ(rfqId, this.rfqForm.get('itemsArray')?.value)
//           .then(() => this.addSuppliersToRFQ(rfqId, this.selectedSupplierIds))
//           .then(() => {
//             this.messageService.showMessage('success', 'Success', 'RFQ updated successfully!');
//             setTimeout(() => this.router.navigate(['/rfq-dashboard']), 1500);
//           })
//           .catch((error: any) => {
//             console.error('Update Error:', error);
//             this.messageService.showMessage('error', 'Error', 'Failed to update RFQ');
//             this.isSubmitting = false;
//           });
//       },
//       error: (error: any) => {
//         console.error('Update Error:', error);
//         this.messageService.showMessage('error', 'Error', error.error?.message || 'Failed to update RFQ');
//         this.isSubmitting = false;
//       }
//     });
//   }

//   private addItemsToRFQ(rfqId: number, items: any[]): Promise<void> {
//     return new Promise((resolve, reject) => {
//       if (!items || items.length === 0) {
//         resolve();
//         return;
//       }

//       let completed = 0;
//       const total = items.length;

//       items.forEach((item: any) => {
//         const itemPayload = {
//           itemDescription: item.itemDescription,
//           quantity: Number(item.quantity),
//           uom: item.uom,
//           specifications: item.specifications,
//           dynamicFields: item.dynamicFields || {},
//           companyType: item.companyType || this.buyerCompanyType
//         };

//         this.rfqService.addItemToRFQ(rfqId, itemPayload).subscribe({
//           next: () => {
//             completed++;
//             if (completed === total) {
//               resolve();
//             }
//           },
//           error: (error: any) => {
//             console.error('Item Error:', error);
//             reject(error);
//           }
//         });
//       });
//     });
//   }

//   private addSuppliersToRFQ(rfqId: number, supplierIds: number[]): Promise<void> {
//     return new Promise((resolve, reject) => {
//       if (!supplierIds || supplierIds.length === 0) {
//         resolve();
//         return;
//       }

//       this.rfqService.addSuppliersToRFQ(rfqId, supplierIds).subscribe({
//         next: () => resolve(),
//         error: (error: any) => reject(error)
//       });
//     });
//   }

//   private getCurrentUserId(): number {
//     const userId = localStorage.getItem('userId');
//     return userId ? Number(userId) : 1;
//   }
// }



import { Component, OnInit, ChangeDetectionStrategy, ChangeDetectorRef, HostListener } from '@angular/core';
import { FormBuilder, FormGroup, Validators, FormArray, FormControl } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { trigger, state, style, transition, animate } from '@angular/animations';

import { RFQService } from '../models/rfq.service';
import { BuyerService } from '../dashboard/buyer-b.service';
import { SupplierService } from '../dashboard/supplier.service';
import { MessageService } from '../../../shared/service/message.service';
import { StorageService } from '../../../shared/service/StorageService';
import { RFQItem, RFQCreatePayload } from '../models/rfq.model';

import { 
  getFieldsByCompanyType, 
  DynamicField,
  getAllCompanyTypes
} from '../models/company-type-fields.config';

import { 
  filterSupplierCategories,
  getAllowedSupplierCategories,
  getCategoryMappingDescription
} from '../models/buyer-supplier-category-mapping.config';

@Component({
  selector: 'app-create-rfq',
  templateUrl: './create-rfq.component.html',
  styleUrls: ['./create-rfq.component.css'],
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
  // ✅ ADDED: Animation for dropdown
  animations: [
    trigger('dropdownAnimation', [
      state('void', style({
        opacity: 0,
        transform: 'translateY(-10px)'
      })),
      transition(':enter', [
        animate('200ms ease-out', style({
          opacity: 1,
          transform: 'translateY(0)'
        }))
      ]),
      transition(':leave', [
        animate('150ms ease-in', style({
          opacity: 0,
          transform: 'translateY(-10px)'
        }))
      ])
    ])
  ]
})
export class CreateRFQComponent implements OnInit {

  // ==================== FORM & STATE ====================
  rfqForm!: FormGroup;
  isSubmitting = false;
  isSavingDraft = false;

  // ==================== MODE & ID ====================
  mode: 'create' | 'edit' | 'view' = 'create';
  rfqId: number | null = null;

  // ==================== LOGGED-IN BUYER DATA ====================
  loggedInBuyer: any = null;
  loggedInBuyerId: number | null = null;
  buyerCompanyType: string = '';
  userLocation: any = null;
  
  // ==================== DYNAMIC FIELDS ====================
  dynamicFieldsForCurrentCompanyType: DynamicField[] = [];
  allCompanyTypes: string[] = getAllCompanyTypes();

  // ==================== CUSTOM FIELDS PER ITEM ====================
  customFieldsByItem: Map<number, DynamicField[]> = new Map();
  showAddFieldForm: { [itemIndex: number]: boolean } = {};
  addFieldForm: { [itemIndex: number]: FormGroup } = {};

  // ==================== SUPPLIER CATEGORY/DEPARTMENT FILTERING ====================
  allDepartments: any[] = [];
  selectedCategory: string = '';
  filteredSuppliers: any[] = [];
  suppliersByDepartment: Map<string, any[]> = new Map();
  suppliers: any[] = [];
  filteredDepartmentsByBuyerCategory: string[] = [];

  // ==================== SUPPLIER SELECTION (UPDATED) ====================
  selectedSupplierIds: number[] = [];
  selectedSupplierToAdd: number | null = null;

  // ==================== SUPPLIER PAGINATION & SEARCH ====================
  supplierSearchTerm: string = '';
  suppliersPerPage: number = 12;
  currentSuppliersPage: number = 1;
  totalSupplierPages: number = 1;

  // ==================== NEW MULTI-SELECT DROPDOWN PROPERTIES ====================
  supplierDropdownOpen: boolean = false;
  supplierDropdownSearch: string = '';
  filteredSuppliersForDropdown: any[] = [];

  // ==================== DROPDOWNS ====================
  priorities = ['LOW', 'MEDIUM', 'HIGH', 'URGENT'];
  uoms = ['Qty', 'Kg', 'L', 'Box', 'Piece', 'Set', 'Meter', 'Pack'];

  // ==================== DATA LOADING TRACKING ====================
  private dataLoadingState = {
    suppliersLoaded: false,
    buyerLoaded: false,
    rfqDataLoaded: false
  };

  constructor(
    private fb: FormBuilder,
    private rfqService: RFQService,
    private buyerService: BuyerService,
    private supplierService: SupplierService,
    private messageService: MessageService,
    private router: Router,
    private route: ActivatedRoute,
    private cdr: ChangeDetectorRef
  ) {}

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent) {
    const target = event.target as HTMLElement;
    if (!target.closest('.multi-select-dropdown')) {
      this.closeSupplierDropdown();
    }
  }

  ngOnInit(): void {
    this.initializeForm();
    this.detectMode();
    this.loadLoggedInBuyer();
    this.loadSuppliers();
  }

  /**
   * Toggle dropdown open/close
   */
  toggleSupplierDropdown(): void {
    this.supplierDropdownOpen = !this.supplierDropdownOpen;
    
    if (this.supplierDropdownOpen) {
      this.supplierDropdownSearch = '';
      this.filteredSuppliersForDropdown = [...this.filteredSuppliers];
    }
    
    this.cdr.markForCheck();
  }

  /**
   * Close dropdown
   */
  closeSupplierDropdown(): void {
    if (this.supplierDropdownOpen) {
      this.supplierDropdownOpen = false;
      this.supplierDropdownSearch = '';
      this.cdr.markForCheck();
    }
  }

  /**
   * Search within dropdown
   */
  onSupplierDropdownSearch(): void {
    const searchTerm = this.supplierDropdownSearch.toLowerCase().trim();
    
    if (!searchTerm) {
      this.filteredSuppliersForDropdown = [...this.filteredSuppliers];
    } else {
      this.filteredSuppliersForDropdown = this.filteredSuppliers.filter((supplier: any) => {
        const companyName = (supplier.companyName || '').toLowerCase();
        const contactName = (supplier.contactPersonName || '').toLowerCase();
        const email = (supplier.contactPersonEmail || '').toLowerCase();
        
        return companyName.includes(searchTerm) || 
               contactName.includes(searchTerm) || 
               email.includes(searchTerm);
      });
    }
    
    this.cdr.markForCheck();
  }

  /**
   * Clear search input
   */
  clearSupplierSearch(): void {
    this.supplierDropdownSearch = '';
    this.onSupplierDropdownSearch();
  }

  /**
   * Get filtered suppliers for dropdown display
   */
  getFilteredSuppliersForDropdown(): any[] {
    return this.filteredSuppliersForDropdown;
  }

  /**
   * Select all suppliers from filtered list
   */
  selectAllSuppliers(): void {
    const allIds = this.filteredSuppliersForDropdown.map(s => s.id);
    
    allIds.forEach(id => {
      if (!this.selectedSupplierIds.includes(id)) {
        this.selectedSupplierIds.push(id);
      }
    });
    
    this.rfqForm.get('selectedSupplierIds')?.setValue(this.selectedSupplierIds);
    
    this.messageService.showMessage(
      'success', 
      'Selected', 
      `${allIds.length} supplier(s) selected`
    );
    
    this.cdr.markForCheck();
  }

  /**
   * Deselect all suppliers from filtered list
   */
  deselectAllSuppliers(): void {
    const filteredIds = this.filteredSuppliersForDropdown.map(s => s.id);
    
    this.selectedSupplierIds = this.selectedSupplierIds.filter(
      id => !filteredIds.includes(id)
    );
    
    this.rfqForm.get('selectedSupplierIds')?.setValue(this.selectedSupplierIds);
    
    this.messageService.showMessage(
      'info', 
      'Cleared', 
      'Filtered suppliers deselected'
    );
    
    this.cdr.markForCheck();
  }

  /**
   * Get count of selected suppliers from filtered list
   */
  getSelectedFromFiltered(): any[] {
    const filteredIds = this.filteredSuppliersForDropdown.map(s => s.id);
    return this.selectedSupplierIds.filter(id => filteredIds.includes(id));
  }

  /**
   * Toggle individual supplier selection in dropdown
   */
  toggleSupplierInDropdown(supplierId: number): void {
    const index = this.selectedSupplierIds.indexOf(supplierId);
    
    if (index > -1) {
      this.selectedSupplierIds.splice(index, 1);
    } else {
      this.selectedSupplierIds.push(supplierId);
    }
    
    this.rfqForm.get('selectedSupplierIds')?.setValue(this.selectedSupplierIds);
    this.cdr.markForCheck();
  }

  /**
   * Get placeholder text for dropdown toggle
   */
  getDropdownPlaceholderText(): string {
    const count = this.getSelectedSuppliersCount();
    const total = this.getFilteredSuppliers().length;
    
    if (count === 0) {
      return 'Select suppliers...';
    } else if (count === total) {
      return `All suppliers selected (${count})`;
    } else {
      return `${count} supplier(s) selected`;
    }
  }

  // ==================== LOAD LOGGED-IN BUYER ====================

  private loadLoggedInBuyer(): void {
    try {
      const buyerData = StorageService.getBuyerDetails();
      
      console.log('%c[STORAGE BUYER DATA]', 'color: #0066cc; font-weight: bold;', buyerData);

      let buyerId: number | null = null;

      if (buyerData?.id && buyerData.id !== 'N/A') {
        buyerId = Number(buyerData.id);
      } else if (buyerData?.buyerId) {
        buyerId = Number(buyerData.buyerId);
      } else {
        const storedId = localStorage.getItem('buyerId') || localStorage.getItem('userId');
        if (storedId && storedId !== 'N/A') {
          buyerId = Number(storedId);
        }
      }

      if (!buyerId) {
        console.error('%c[ERROR] No buyer ID found', 'color: #cc0000; font-weight: bold;');
        this.messageService.showMessage('warning', 'Warning', 'Please login as a buyer first');
        this.router.navigate(['/login']);
        return;
      }

      this.loggedInBuyerId = buyerId;

      console.log('%c[FETCHING BUYER] ID:', 'color: #0066cc; font-weight: bold;', buyerId);

      this.buyerService.getBuyerById(this.loggedInBuyerId).subscribe({
        next: (response: any) => {
          console.log('%c[BUYER API RESPONSE]', 'color: #00aa00; font-weight: bold;', response);

          const buyerDetails = response?.data || response;

          if (!buyerDetails || !buyerDetails.id) {
            console.error('%c[ERROR] Invalid buyer data structure', 'color: #cc0000;', response);
            this.messageService.showMessage('error', 'Error', 'Failed to load buyer details');
            return;
          }

          this.loggedInBuyer = {
            id: buyerDetails.id,
            companyName: buyerDetails.companyName || 'N/A',
            companyType: buyerDetails.companyType || 'Others',
            email: buyerDetails.contactPersonEmail || localStorage.getItem('username') || 'N/A',
            contactPersonName: buyerDetails.contactPersonName || localStorage.getItem('fullName') || 'N/A',
            contactPersonPhone: buyerDetails.contactPersonPhone || localStorage.getItem('phone') || 'N/A'
          };

          this.buyerCompanyType = this.loggedInBuyer.companyType;
          this.dynamicFieldsForCurrentCompanyType = getFieldsByCompanyType(this.buyerCompanyType);

          console.log('%c[✅ BUYER LOADED SUCCESSFULLY]', 'color: #00aa00; font-weight: bold; font-size: 14px;');
          console.log('  📌 Company:', this.loggedInBuyer.companyName);
          console.log('  📌 Type:', this.buyerCompanyType);

          this.loadBuyerLocations(buyerDetails);
          this.updateDynamicFieldsForAllItems();

          if (this.allDepartments.length > 0) {
            console.log('%c[FILTERING DEPARTMENTS]', 'color: #9c27b0;', 'Departments already loaded, applying filter...');
            this.filterDepartmentsByBuyerCategory();
          }

          this.dataLoadingState.buyerLoaded = true;
          this.cdr.markForCheck();
        },
        error: (error: any) => {
          console.error('%c[ERROR] Failed to load buyer', 'color: #cc0000; font-weight: bold;', error);
          this.messageService.showMessage('error', 'Error', 'Failed to load buyer details');
        }
      });
    } catch (error) {
      console.error('%c[ERROR] Exception in loadLoggedInBuyer', 'color: #cc0000;', error);
      this.messageService.showMessage('error', 'Error', 'Failed to load buyer details');
    }
  }

  // ==================== LOAD BUYER LOCATIONS ====================

  private loadBuyerLocations(buyerDetails: any): void {
    if (!buyerDetails?.locations || !Array.isArray(buyerDetails.locations)) {
      console.warn('%c[WARNING] No locations available', 'color: #ff9800;');
      this.messageService.showMessage('warning', 'Warning', 'No locations available for this buyer');
      return;
    }

    console.log('%c[LOCATIONS]', 'color: #0066cc;', `Found ${buyerDetails.locations.length} locations`);

    const userLocationId = localStorage.getItem('locationId');
    
    let selectedLoc = null;
    
    if (userLocationId) {
      selectedLoc = buyerDetails.locations.find((loc: any) => loc.id === Number(userLocationId));
      console.log('  🔍 Looking for location ID:', userLocationId, selectedLoc ? '✅ Found' : '❌ Not found');
    }
    
    if (!selectedLoc && buyerDetails.locations.length > 0) {
      selectedLoc = buyerDetails.locations[0];
      console.log('  📍 Using first location as default');
    }

    if (selectedLoc) {
      this.userLocation = {
        id: selectedLoc.id,
        locationName: selectedLoc.locationName || 'N/A',
        city: selectedLoc.city || 'N/A',
        state: selectedLoc.state || 'N/A',
        postalCode: selectedLoc.postalCode || '',
        country: selectedLoc.country || ''
      };

      this.rfqForm.patchValue({
        locationId: this.userLocation.id
      }, { emitEvent: false });

      console.log('%c[✅ LOCATION SET]', 'color: #00aa00;', this.userLocation.locationName);
    } else {
      console.warn('%c[WARNING] Could not determine location', 'color: #ff9800;');
      this.messageService.showMessage('warning', 'Warning', 'Could not determine your location');
    }

    this.cdr.markForCheck();
  }

  // ==================== FORM INITIALIZATION ====================

  private initializeForm(): void {
    this.rfqForm = this.fb.group({
      rfqTitle: ['', [Validators.required, Validators.minLength(5)]],
      rfqDescription: [''],
      dueDate: ['', Validators.required],
      itemRequiredDate: ['', Validators.required],
      priority: ['MEDIUM', Validators.required],
      locationId: ['', Validators.required],
      itemsArray: this.fb.array([]),
      selectedSupplierIds: [[], Validators.required],
      paymentTerms: [''],
      deliveryTerms: [''],
      justification: [''],
      allowSplitPO: [false],
      preferredVendorsOnly: [false],
      approvalRequired: [true, { nonNullable: true }]
    });

    if (this.itemsArray.length === 0) {
      this.addItem();
    }
  }

  // ==================== MODE DETECTION ====================

  private detectMode(): void {
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      const mode = params.get('mode');

      if (id) {
        this.rfqId = +id;
        this.mode = mode === 'view' ? 'view' : 'edit';
      } else {
        this.mode = 'create';
      }
      this.cdr.markForCheck();
    });
  }

  // ==================== DATA LOADING ====================

  private loadSuppliers(): void {
    this.supplierService.getAllSuppliers().subscribe({
      next: (response: any) => {
        if (Array.isArray(response)) {
          this.suppliers = response;
        } else if (response?.data && Array.isArray(response.data)) {
          this.suppliers = response.data;
        }

        this.extractDepartmentsFromSuppliers();
        this.dataLoadingState.suppliersLoaded = true;

        if (this.rfqId && !this.dataLoadingState.rfqDataLoaded) {
          setTimeout(() => this.loadRFQData(this.rfqId!), 100);
        }

        this.cdr.markForCheck();
      },
      error: (error: any) => {
        console.error('Load Suppliers Error:', error);
        this.messageService.showMessage('error', 'Error', 'Failed to load suppliers');
      }
    });
  }

  private extractDepartmentsFromSuppliers(): void {
    const departmentMap = new Map<string, any[]>();

    this.suppliers.forEach((supplier: any) => {
      if (supplier?.locations && Array.isArray(supplier.locations)) {
        supplier.locations.forEach((location: any) => {
          if (location?.departments && Array.isArray(location.departments)) {
            location.departments.forEach((dept: any) => {
              if (dept && !dept.isDeleted) {
                const deptName = dept.departmentName;

                if (!departmentMap.has(deptName)) {
                  departmentMap.set(deptName, []);
                }

                const deptSuppliers = departmentMap.get(deptName) || [];
                if (!deptSuppliers.find((s: any) => s.id === supplier.id)) {
                  deptSuppliers.push({
                    id: supplier.id,
                    companyName: supplier.companyName,
                    contactPersonName: supplier.contactPersonName,
                    contactPersonEmail: supplier.contactPersonEmail
                  });
                  departmentMap.set(deptName, deptSuppliers);
                }
              }
            });
          }
        });
      }
    });

    this.allDepartments = Array.from(departmentMap.keys()).sort();
    this.suppliersByDepartment = departmentMap;
    this.filterDepartmentsByBuyerCategory();
    this.cdr.markForCheck();
  }

  // ==================== FILTER DEPARTMENTS BY BUYER CATEGORY ====================

  private filterDepartmentsByBuyerCategory(): void {
    if (!this.buyerCompanyType) {
      this.filteredDepartmentsByBuyerCategory = [...this.allDepartments];
      return;
    }

    this.filteredDepartmentsByBuyerCategory = filterSupplierCategories(
      this.buyerCompanyType,
      this.allDepartments
    );

    this.cdr.markForCheck();
  }

  // ==================== UPDATE DYNAMIC FIELDS FOR ALL ITEMS ====================

  private updateDynamicFieldsForAllItems(): void {
    const itemsArray = this.rfqForm.get('itemsArray') as FormArray;
    
    itemsArray.controls.forEach((itemGroup, index) => {
      if (itemGroup instanceof FormGroup) {
        let dynamicFieldsGroup = itemGroup.get('dynamicFields') as FormGroup;
        
        if (!dynamicFieldsGroup) {
          dynamicFieldsGroup = this.fb.group({});
          itemGroup.addControl('dynamicFields', dynamicFieldsGroup);
        }
        
        Object.keys(dynamicFieldsGroup.controls).forEach(key => {
          if (!key.startsWith('custom_')) {
            dynamicFieldsGroup.removeControl(key);
          }
        });
        
        this.dynamicFieldsForCurrentCompanyType.forEach(field => {
          if (!dynamicFieldsGroup.get(field.fieldName)) {
            dynamicFieldsGroup.addControl(field.fieldName, new FormControl(''));
          }
        });
        
        itemGroup.patchValue({
          companyType: this.buyerCompanyType || 'Others'
        }, { emitEvent: false });
      }
    });
    
    this.cdr.markForCheck();
  }

  // ==================== ITEMS MANAGEMENT ====================

  get itemsArray(): FormArray {
    return this.rfqForm.get('itemsArray') as FormArray;
  }

  addItem(item?: RFQItem): void {
    const dynamicFieldsGroup: { [key: string]: any } = {};

    this.dynamicFieldsForCurrentCompanyType.forEach(field => {
      const fieldValue = item?.dynamicFields?.[field.fieldName] || '';
      dynamicFieldsGroup[field.fieldName] = [fieldValue];
    });

    if (item?.dynamicFields) {
      Object.keys(item.dynamicFields).forEach(key => {
        if (key.startsWith('custom_') && item.dynamicFields) {
          dynamicFieldsGroup[key] = [item.dynamicFields[key]];
        }
      });
    }

    const itemGroup = this.fb.group({
      itemDescription: [item?.itemDescription || '', Validators.required],
      quantity: [item?.quantity || 1, [Validators.required, Validators.min(1)]],
      uom: [item?.uom || 'Qty', Validators.required],
      specifications: [item?.specifications || ''],
      dynamicFields: this.fb.group(dynamicFieldsGroup),
      companyType: [item?.companyType || this.buyerCompanyType || 'Others']
    });

    this.itemsArray.push(itemGroup);

    const itemIndex = this.itemsArray.length - 1;
    this.customFieldsByItem.set(itemIndex, []);
    this.showAddFieldForm[itemIndex] = false;

    if (item?.dynamicFields) {
      const customFields: DynamicField[] = [];
      Object.keys(item.dynamicFields).forEach(key => {
        if (key.startsWith('custom_')) {
          customFields.push({
            fieldName: key,
            fieldLabel: key.replace('custom_', '').replace(/_/g, ' '),
            fieldType: 'text',
            isCustom: true
          });
        }
      });
      if (customFields.length > 0) {
        this.customFieldsByItem.set(itemIndex, customFields);
        console.log('%c[CUSTOM FIELDS RESTORED]', 'color: #00aa00;', {
          item: itemIndex,
          count: customFields.length
        });
      }
    }
    
    console.log('%c[ITEM ADDED]', 'color: #00aa00;', {
      index: this.itemsArray.length,
      companyType: this.buyerCompanyType,
      dynamicFields: this.dynamicFieldsForCurrentCompanyType.length
    });
    
    this.cdr.markForCheck();
  }

  removeItem(index: number): void {
    if (this.itemsArray.length > 1) {
      this.itemsArray.removeAt(index);
      this.customFieldsByItem.delete(index);
      delete this.showAddFieldForm[index];
      delete this.addFieldForm[index];
      this.messageService.showMessage('success', 'Removed', 'Item removed successfully');
      this.cdr.markForCheck();
    } else {
      this.messageService.showMessage('warning', 'Warning', 'At least one item is required');
    }
  }

  // ==================== DYNAMIC FIELD HELPER METHODS ====================

  getDynamicFieldsForItem(itemIndex: number): DynamicField[] {
    return this.dynamicFieldsForCurrentCompanyType;
  }

  getAllFieldsForItem(itemIndex: number): DynamicField[] {
    const predefinedFields = this.getDynamicFieldsForItem(itemIndex);
    const customFields = this.customFieldsByItem.get(itemIndex) || [];
    return [...predefinedFields, ...customFields];
  }

  getDynamicFieldValue(itemIndex: number, fieldName: string): any {
    return this.itemsArray.at(itemIndex)?.get(`dynamicFields.${fieldName}`)?.value;
  }

  setDynamicFieldValue(itemIndex: number, fieldName: string, value: any): void {
    this.itemsArray.at(itemIndex)?.get(`dynamicFields.${fieldName}`)?.setValue(value);
  }

  getFieldOptions(field: DynamicField): string[] {
    return field.options || [];
  }

  // ==================== CUSTOM FIELDS FUNCTIONALITY ====================

  initializeCustomFieldsForItem(itemIndex: number): void {
    if (!this.customFieldsByItem.has(itemIndex)) {
      this.customFieldsByItem.set(itemIndex, []);
    }
  }

  openAddFieldModal(itemIndex: number): void {
    this.showAddFieldForm[itemIndex] = true;
    
    if (!this.addFieldForm[itemIndex]) {
      this.addFieldForm[itemIndex] = this.fb.group({
        fieldLabel: ['', [Validators.required, Validators.minLength(3)]],
        fieldType: ['text', Validators.required],
        placeholder: [''],
        maxLength: [255],
        option1: [''],
        option2: [''],
        option3: [''],
        option4: ['']
      });
    }
    
    this.cdr.markForCheck();
  }

  closeAddFieldModal(itemIndex: number): void {
    this.showAddFieldForm[itemIndex] = false;
    this.cdr.markForCheck();
  }

  addCustomFieldToItem(itemIndex: number): void {
    const form = this.addFieldForm[itemIndex];
    
    if (!form || !form.valid) {
      console.warn('Form is invalid');
      return;
    }

    const formValue = form.value;
    const fieldType = formValue.fieldType;
    
    let options: string[] | undefined = undefined;
    if (fieldType === 'select') {
      options = [formValue.option1, formValue.option2, formValue.option3, formValue.option4]
        .filter((opt: string) => opt && opt.trim());
      
      if (options.length === 0) {
        this.messageService.showMessage('warning', 'Warning', 'Please add at least one option for select field');
        return;
      }
    }

    const newField: DynamicField = {
      fieldName: 'custom_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9),
      fieldLabel: formValue.fieldLabel,
      fieldType: fieldType,
      placeholder: formValue.placeholder,
      options: options,
      maxLength: fieldType === 'textarea' ? 500 : formValue.maxLength || 255,
      isCustom: true
    };

    this.initializeCustomFieldsForItem(itemIndex);
    const customFields = this.customFieldsByItem.get(itemIndex)!;
    customFields.push(newField);

    console.log('%c[CUSTOM FIELD ADDED]', 'color: #00aa00; font-weight: bold;', {
      item: itemIndex,
      field: newField.fieldLabel,
      type: newField.fieldType
    });

    const itemGroup = this.itemsArray.at(itemIndex) as FormGroup;
    const dynamicFieldsGroup = itemGroup.get('dynamicFields') as FormGroup;
    
    if (dynamicFieldsGroup) {
      dynamicFieldsGroup.addControl(newField.fieldName, new FormControl(''));
    }

    form.reset({
      fieldLabel: '',
      fieldType: 'text',
      placeholder: '',
      maxLength: 255,
      option1: '',
      option2: '',
      option3: '',
      option4: ''
    });
    
    this.closeAddFieldModal(itemIndex);
    this.messageService.showMessage('success', 'Success', 'Custom field added successfully');
    this.cdr.markForCheck();
  }

  removeCustomField(itemIndex: number, fieldName: string): void {
    const customFields = this.customFieldsByItem.get(itemIndex);
    
    if (customFields) {
      const index = customFields.findIndex(f => f.fieldName === fieldName);
      if (index > -1) {
        customFields.splice(index, 1);
        
        const itemGroup = this.itemsArray.at(itemIndex) as FormGroup;
        const dynamicFieldsGroup = itemGroup.get('dynamicFields') as FormGroup;
        
        if (dynamicFieldsGroup) {
          dynamicFieldsGroup.removeControl(fieldName);
        }
        
        console.log('%c[CUSTOM FIELD REMOVED]', 'color: #ff6600;', fieldName);
        this.messageService.showMessage('success', 'Removed', 'Custom field removed successfully');
        this.cdr.markForCheck();
      }
    }
  }

  isCustomField(fieldName: string): boolean {
    return fieldName.startsWith('custom_');
  }

  // ==================== CATEGORY CHANGE ====================

  onCategoryChange(event: any): void {
    this.selectedCategory = event.target.value;
    this.selectedSupplierIds = [];
    this.supplierDropdownOpen = false;
    this.supplierDropdownSearch = '';

    if (this.selectedCategory) {
      this.filteredSuppliers = this.suppliersByDepartment.get(this.selectedCategory) || [];
      this.filteredSuppliersForDropdown = [...this.filteredSuppliers];

      console.log('%c[CATEGORY SELECTED]', 'color: #0066cc;', {
        category: this.selectedCategory,
        suppliersAvailable: this.filteredSuppliers.length
      });
    } else {
      this.filteredSuppliers = [];
      this.filteredSuppliersForDropdown = [];
    }

    this.rfqForm.get('selectedSupplierIds')?.setValue([]);
    this.cdr.markForCheck();
  }

  private updateSupplierPagination(): void {
    this.totalSupplierPages = Math.ceil(this.getFilteredSuppliers().length / this.suppliersPerPage);
    if (this.totalSupplierPages === 0) {
      this.totalSupplierPages = 1;
    }
  }

  onSupplierSearch(searchTerm: string): void {
    this.supplierSearchTerm = searchTerm.toLowerCase();
    this.currentSuppliersPage = 1;
    this.updateSupplierPagination();
    this.cdr.markForCheck();
  }

  getFilteredSuppliers(): any[] {
    return this.filteredSuppliers;
  }

  getPaginatedSuppliers(): any[] {
    const filtered = this.getFilteredSuppliers();
    const startIndex = (this.currentSuppliersPage - 1) * this.suppliersPerPage;
    const endIndex = startIndex + this.suppliersPerPage;
    return filtered.slice(startIndex, endIndex);
  }

  goToSupplierPage(page: number): void {
    if (page >= 1 && page <= this.totalSupplierPages) {
      this.currentSuppliersPage = page;
      this.cdr.markForCheck();
    }
  }

  getSupplierPageNumbers(): number[] {
    const pages = [];
    const maxVisible = 5;
    let startPage = Math.max(1, this.currentSuppliersPage - Math.floor(maxVisible / 2));
    let endPage = Math.min(this.totalSupplierPages, startPage + maxVisible - 1);

    if (endPage - startPage + 1 < maxVisible) {
      startPage = Math.max(1, endPage - maxVisible + 1);
    }

    for (let i = startPage; i <= endPage; i++) {
      pages.push(i);
    }
    return pages;
  }

  // ==================== NEW SUPPLIER SELECTION METHODS ====================

  /**
   * Add supplier from dropdown to selected list
   */
  addSupplierToSelection(): void {
    if (!this.selectedSupplierToAdd) {
      return;
    }

    const supplierId = Number(this.selectedSupplierToAdd);

    if (!this.selectedSupplierIds.includes(supplierId)) {
      this.selectedSupplierIds.push(supplierId);
      this.rfqForm.get('selectedSupplierIds')?.setValue(this.selectedSupplierIds);
      
      console.log('%c[SUPPLIER ADDED]', 'color: #00aa00;', {
        supplierId: supplierId,
        totalSelected: this.selectedSupplierIds.length
      });

      this.messageService.showMessage('success', 'Added', 'Supplier added successfully');
    }

    // Reset dropdown
    this.selectedSupplierToAdd = null;
    this.cdr.markForCheck();
  }

  /**
   * Remove supplier from selected list
   */
  removeSupplierFromSelection(supplierId: number): void {
    const index = this.selectedSupplierIds.indexOf(supplierId);
    
    if (index > -1) {
      this.selectedSupplierIds.splice(index, 1);
      this.rfqForm.get('selectedSupplierIds')?.setValue(this.selectedSupplierIds);
      
      this.messageService.showMessage('info', 'Removed', 'Supplier removed from selection');
      this.cdr.markForCheck();
    }
  }

  /**
   * Get supplier details by ID
   */
  getSupplierById(supplierId: number): any {
    return this.filteredSuppliers.find(s => s.id === supplierId) || 
           this.suppliers.find(s => s.id === supplierId);
  }

  /**
   * TrackBy function for supplier list (performance optimization)
   */
  trackBySupplier(index: number, supplier: any): number {
    return supplier.id;
  }

  /**
   * TrackBy function for selected supplier IDs
   */
  trackBySupplierId(index: number, supplierId: number): number {
    return supplierId;
  }

  /**
   * LEGACY METHOD - kept for backward compatibility
   * You can remove this if you're not using the old checkbox UI
   */
  toggleSupplierSelection(supplierId: number): void {
    const index = this.selectedSupplierIds.indexOf(supplierId);

    if (index > -1) {
      this.selectedSupplierIds.splice(index, 1);
    } else {
      this.selectedSupplierIds.push(supplierId);
    }

    this.rfqForm.get('selectedSupplierIds')?.setValue(this.selectedSupplierIds);
    this.cdr.markForCheck();
  }

  isSupplierSelected(supplierId: number): boolean {
    return this.selectedSupplierIds.includes(supplierId);
  }

  getSelectedSuppliersCount(): number {
    return this.selectedSupplierIds.length;
  }

  // ==================== LOAD RFQ DATA (FOR EDIT/VIEW) ====================

  private loadRFQData(id: number): void {
    console.log('%c[LOADING RFQ DATA]', 'color: #0066cc; font-weight: bold;', {
      id: id,
      suppliersLoaded: this.dataLoadingState.suppliersLoaded,
      departmentsCount: this.suppliersByDepartment.size
    });

    if (!this.dataLoadingState.suppliersLoaded) {
      console.warn('%c[WAITING FOR SUPPLIERS]', 'color: #ff9800;');
      setTimeout(() => this.loadRFQData(id), 200);
      return;
    }

    this.rfqService.getRFQById(id).subscribe({
      next: (response: any) => {
        const rfq = response?.data;

        console.log('%c[RFQ DATA RECEIVED]', 'color: #00aa00;', {
          title: rfq?.rfqTitle,
          items: rfq?.items?.length || 0,
          suppliers: rfq?.selectedSuppliers?.length || 0
        });

        if (!rfq) {
          this.messageService.showMessage('error', 'Error', 'RFQ not found');
          this.router.navigate(['/rfq-dashboard']);
          return;
        }

        this.rfqForm.patchValue({
          rfqTitle: rfq.rfqTitle || '',
          rfqDescription: rfq.rfqDescription || '',
          dueDate: rfq.dueDate ? this.formatDateForInput(rfq.dueDate) : '',
          itemRequiredDate: rfq.itemRequiredDate ? this.formatDateForInput(rfq.itemRequiredDate) : '',
          priority: rfq.priority || 'MEDIUM',
          locationId: rfq.location?.id || '',
          paymentTerms: rfq.paymentTerms || '',
          deliveryTerms: rfq.deliveryTerms || '',
          justification: rfq.justification || '',
          allowSplitPO: rfq.allowSplitPO || false,
          preferredVendorsOnly: rfq.preferredVendorsOnly || false,
          approvalRequired: true
        }, { emitEvent: false });

        if (rfq.location?.id) {
          this.userLocation = rfq.location;
        }

        if (rfq.items && Array.isArray(rfq.items) && rfq.items.length > 0) {
          console.log('%c[LOADING ITEMS]', 'color: #0066cc;', rfq.items.length + ' items');
          
          while (this.itemsArray.length > 0) {
            this.itemsArray.removeAt(0);
          }
          
          rfq.items.forEach((item: any) => {
            this.addItem(item);
          });

          console.log('%c[✅ ITEMS LOADED]', 'color: #00aa00;', this.itemsArray.length);
        } else {
          if (this.itemsArray.length === 0) {
            this.addItem();
          }
        }

        if (rfq.selectedSuppliers && Array.isArray(rfq.selectedSuppliers) && rfq.selectedSuppliers.length > 0) {
          console.log('%c[LOADING SUPPLIERS]', 'color: #0066cc;', rfq.selectedSuppliers.length + ' suppliers');
          
          const supplierIds = rfq.selectedSuppliers.map((s: any) => s.id);
          this.selectedSupplierIds = [...supplierIds];

          const firstSupplierId = rfq.selectedSuppliers[0]?.id;
          let foundCategory = '';

          for (const [dept, suppliers] of this.suppliersByDepartment.entries()) {
            if (suppliers.find((s: any) => s.id === firstSupplierId)) {
              foundCategory = dept;
              break;
            }
          }

          if (foundCategory) {
            this.selectedCategory = foundCategory;
            this.filteredSuppliers = this.suppliersByDepartment.get(foundCategory) || [];
            this.rfqForm.get('selectedSupplierIds')?.setValue(supplierIds, { emitEvent: false });

            console.log('%c[✅ SUPPLIERS LOADED]', 'color: #00aa00;', {
              category: foundCategory,
              suppliers: supplierIds.length,
              availableInCategory: this.filteredSuppliers.length
            });

            this.updateSupplierPagination();
          } else {
            console.warn('%c[WARNING] Could not find supplier category', 'color: #ff9800;');
            this.selectedSupplierIds = [];
          }
        } else {
          this.selectedSupplierIds = [];
        }

        if (this.mode === 'view') {
          this.rfqForm.disable({ emitEvent: false });
        }

        this.dataLoadingState.rfqDataLoaded = true;
        this.cdr.markForCheck();

        console.log('%c[✅ RFQ DATA FULLY LOADED]', 'color: #00aa00; font-weight: bold;');
      },
      error: (error: any) => {
        console.error('Load RFQ Error:', error);
        this.messageService.showMessage('error', 'Error', 'Failed to load RFQ');
        this.router.navigate(['/rfq-dashboard']);
      }
    });
  }

  private formatDateForInput(dateString: string): string {
    if (!dateString) return '';
    try {
      const date = new Date(dateString);
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      return `${year}-${month}-${day}`;
    } catch (e) {
      console.error('Date formatting error:', dateString);
      return '';
    }
  }

  // ==================== VALIDATION ====================

  isFormValid(): boolean {
    return this.rfqForm.valid && this.itemsArray.length > 0 && this.getSelectedSuppliersCount() > 0;
  }

  areItemsValid(): boolean {
    return this.itemsArray.controls.every((ctrl: any) => ctrl.valid);
  }

  // ==================== BUILD PAYLOAD ====================

  private buildPayload(): RFQCreatePayload {
    const form = this.rfqForm.value;

    const dueDate = this.formatDateToISO(form.dueDate);
    const itemRequiredDate = this.formatDateToISO(form.itemRequiredDate);

    return {
      rfqTitle: form.rfqTitle?.trim() || '',
      rfqDescription: form.rfqDescription?.trim() || '',
      dueDate: dueDate,
      itemRequiredDate: itemRequiredDate,
      priority: form.priority || 'MEDIUM',
      paymentTerms: form.paymentTerms?.trim() || null,
      deliveryTerms: form.deliveryTerms?.trim() || null,
      justification: form.justification?.trim() || null,
      allowSplitPO: Boolean(form.allowSplitPO) || false,
      preferredVendorsOnly: Boolean(form.preferredVendorsOnly) || false,
      approvalRequired: true
    };
  }

  private formatDateToISO(dateString: string): string {
    if (!dateString) {
      return new Date().toISOString();
    }

    if (dateString.includes('T')) {
      return dateString;
    }

    const date = new Date(dateString + 'T00:00:00');

    if (isNaN(date.getTime())) {
      console.warn('Invalid date format:', dateString);
      return new Date().toISOString();
    }

    return date.toISOString();
  }

  // ==================== SUBMIT & SAVE ====================

  submitRFQ(): void {
    if (!this.isFormValid()) {
      this.rfqForm.markAllAsTouched();
      this.messageService.showMessage('error', 'Validation Error',
        'Please fill all required fields: RFQ Title, At least one Item, and At least one Supplier');
      return;
    }

    this.isSubmitting = true;
    const payload = this.buildPayload();
    const locationId = Number(this.rfqForm.get('locationId')?.value);
    const userId = this.getCurrentUserId();

    if (this.mode === 'edit' && this.rfqId) {
      this.updateRFQFlow(this.rfqId, payload);
    } else {
      this.createRFQFlow(this.loggedInBuyerId!, locationId, userId, payload);
    }
  }

  private createRFQFlow(buyerId: number, locationId: number, userId: number, payload: RFQCreatePayload): void {
    this.rfqService.createRFQ(buyerId, locationId, userId, payload).subscribe({
      next: (response: any) => {
        const rfqId = response.data?.id;

        if (!rfqId) {
          console.error('No RFQ ID in response:', response);
          this.messageService.showMessage('error', 'Error', 'Failed to create RFQ');
          this.isSubmitting = false;
          return;
        }

        this.addItemsToRFQ(rfqId, this.rfqForm.get('itemsArray')?.value)
          .then(() => this.addSuppliersToRFQ(rfqId, this.selectedSupplierIds))
          .then(() => {
            this.messageService.showMessage('success', 'Success', `${response.data?.rfqNumber} created successfully!`);
            setTimeout(() => this.router.navigate(['/rfq-dashboard']), 1500);
          })
          .catch((error: any) => {
            console.error('Submission Error:', error);
            this.messageService.showMessage('error', 'Partial Error', 'RFQ created but failed to add items/suppliers.');
            this.isSubmitting = false;
          });
      },
      error: (error: any) => {
        console.error('Create RFQ Error:', error);
        const errorMsg = error.status === 400 ? (error.error?.message || 'Invalid RFQ data') : 'Failed to create RFQ';
        this.messageService.showMessage('error', 'Error', errorMsg);
        this.isSubmitting = false;
      }
    });
  }

  private updateRFQFlow(rfqId: number, payload: RFQCreatePayload): void {
    this.rfqService.updateRFQ(rfqId, payload).subscribe({
      next: () => {
        this.addItemsToRFQ(rfqId, this.rfqForm.get('itemsArray')?.value)
          .then(() => this.addSuppliersToRFQ(rfqId, this.selectedSupplierIds))
          .then(() => {
            this.messageService.showMessage('success', 'Success', 'RFQ updated successfully!');
            setTimeout(() => this.router.navigate(['/rfq-dashboard']), 1500);
          })
          .catch((error: any) => {
            console.error('Update Error:', error);
            this.messageService.showMessage('error', 'Error', 'Failed to update RFQ');
            this.isSubmitting = false;
          });
      },
      error: (error: any) => {
        console.error('Update Error:', error);
        this.messageService.showMessage('error', 'Error', error.error?.message || 'Failed to update RFQ');
        this.isSubmitting = false;
      }
    });
  }

  private addItemsToRFQ(rfqId: number, items: any[]): Promise<void> {
    return new Promise((resolve, reject) => {
      if (!items || items.length === 0) {
        resolve();
        return;
      }

      let completed = 0;
      const total = items.length;

      items.forEach((item: any) => {
        const itemPayload = {
          itemDescription: item.itemDescription,
          quantity: Number(item.quantity),
          uom: item.uom,
          specifications: item.specifications,
          dynamicFields: item.dynamicFields || {},
          companyType: item.companyType || this.buyerCompanyType
        };

        this.rfqService.addItemToRFQ(rfqId, itemPayload).subscribe({
          next: () => {
            completed++;
            if (completed === total) {
              resolve();
            }
          },
          error: (error: any) => {
            console.error('Item Error:', error);
            reject(error);
          }
        });
      });
    });
  }

  private addSuppliersToRFQ(rfqId: number, supplierIds: number[]): Promise<void> {
    return new Promise((resolve, reject) => {
      if (!supplierIds || supplierIds.length === 0) {
        resolve();
        return;
      }

      this.rfqService.addSuppliersToRFQ(rfqId, supplierIds).subscribe({
        next: () => resolve(),
        error: (error: any) => reject(error)
      });
    });
  }

  private getCurrentUserId(): number {
    const userId = localStorage.getItem('userId');
    return userId ? Number(userId) : 1;
  }
}