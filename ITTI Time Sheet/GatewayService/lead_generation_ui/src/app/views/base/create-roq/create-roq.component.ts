// // // // // File: src/app/features/requisition/components/create-roq/create-roq.component.ts
// // // // import { Component, OnInit, OnDestroy } from '@angular/core';
// // // // import { CommonModule } from '@angular/common';
// // // // import { FormBuilder, FormGroup, FormArray, Validators, ReactiveFormsModule } from '@angular/forms';
// // // // import { Router } from '@angular/router';
// // // // import { RequisitionService } from './requisition.service';
// // // // import { MessageService } from '../../../shared/service/message.service';
// // // // import { Subject } from 'rxjs';
// // // // import { takeUntil } from 'rxjs/operators';

// // // // @Component({
// // // //   selector: 'app-create-roq',
// // // //   standalone: true,
// // // //   imports: [CommonModule, ReactiveFormsModule],
// // // //   templateUrl: './create-roq.component.html',
// // // //   styleUrls: ['./create-roq.component.css']
// // // // })
// // // // export class CreateRoqComponent implements OnInit, OnDestroy {

// // // //   requisitionForm!: FormGroup;
  
// // // //   // Data
// // // //   departments: any[] = [];
// // // //   currentUser: any = null;
// // // //   currentRequisitionId: number | null = null;

// // // //   // UI States
// // // //   isLoading = false;
// // // //   isAddingItems = false;
// // // //   itemsAddedCount = 0;
// // // //   totalItemsToAdd = 0;

// // // //   // Currency and Category options
// // // //   currencies = ['USD', 'EUR', 'GBP', 'INR', 'JPY', 'AUD', 'CAD'];
// // // //   categories = ['Office Supplies', 'Equipment', 'Software', 'Services', 'Maintenance', 'Hardware'];

// // // //   // To manage subscriptions
// // // //   private destroy$ = new Subject<void>();

// // // //   constructor(
// // // //     private fb: FormBuilder,
// // // //     private requisitionService: RequisitionService,
// // // //     private messageService: MessageService,
// // // //     private router: Router
// // // //   ) {
// // // //     this.initializeForms();
// // // //   }

// // // //   ngOnInit(): void {
// // // //     console.log('🚀 CreateRoqComponent initialized');
// // // //     this.loadDepartments();
// // // //     this.loadCurrentUser();
// // // //   }

// // // //   ngOnDestroy(): void {
// // // //     this.destroy$.next();
// // // //     this.destroy$.complete();
// // // //   }

// // // //   /**
// // // //    * Initialize form groups
// // // //    */
// // // //   private initializeForms(): void {
// // // //     this.requisitionForm = this.fb.group({
// // // //       title: ['', [Validators.required, Validators.minLength(3)]],
// // // //       category: ['', Validators.required],
// // // //       description: [''],
// // // //       departmentId: ['', Validators.required],
// // // //       requestedById: ['', Validators.required],
// // // //       currency: ['USD', Validators.required],
// // // //       estimatedCost: ['', [Validators.required, Validators.min(0)]],
// // // //       deliveryDate: ['', Validators.required],
// // // //       deliveryLocation: ['', [Validators.required, Validators.minLength(3)]],
// // // //       items: this.fb.array([this.createItemGroup()])
// // // //     });
// // // //   }

// // // //   /**
// // // //    * Create item form group
// // // //    */
// // // //   private createItemGroup(): FormGroup {
// // // //     return this.fb.group({
// // // //       itemName: ['', [Validators.required, Validators.minLength(2)]],
// // // //       itemDescription: [''],
// // // //       quantity: ['', [Validators.required, Validators.min(1)]],
// // // //       price: ['', [Validators.required, Validators.min(0)]]
// // // //     });
// // // //   }

// // // //   /**
// // // //    * Get items FormArray
// // // //    */
// // // //   get itemsFormArray(): FormArray {
// // // //     return this.requisitionForm.get('items') as FormArray;
// // // //   }

// // // //   /**
// // // //    * Add new item
// // // //    */
// // // //   addItem(): void {
// // // //     this.itemsFormArray.push(this.createItemGroup());
// // // //     console.log('➕ Item added. Total items:', this.itemsFormArray.length);
// // // //   }

// // // //   /**
// // // //    * Remove item
// // // //    */
// // // //   removeItem(index: number): void {
// // // //     if (this.itemsFormArray.length > 1) {
// // // //       this.itemsFormArray.removeAt(index);
// // // //       console.log('🗑️ Item removed. Total items:', this.itemsFormArray.length);
// // // //     } else {
// // // //       this.messageService.showMessage('warning', 'Warning', 'At least one item is required');
// // // //     }
// // // //   }

// // // //   /**
// // // //    * Load departments - Update this to call your actual API
// // // //    */
// // // //   private loadDepartments(): void {
// // // //     console.log('📥 Loading departments...');
    
// // // //     // ⚠️ TODO: Replace with actual API call
// // // //     // For now, using mock data
// // // //     this.departments = [
// // // //       { id: 1, departmentName: 'Sales', departmentDescription: 'Sales Department' },
// // // //       { id: 2, departmentName: 'IT', departmentDescription: 'IT Department' },
// // // //       { id: 3, departmentName: 'HR', departmentDescription: 'Human Resources' },
// // // //       { id: 4, departmentName: 'Finance', departmentDescription: 'Finance Department' },
// // // //       { id: 5, departmentName: 'Operations', departmentDescription: 'Operations Department' }
// // // //     ];
    
// // // //     console.log('✅ Departments loaded:', this.departments.length);
// // // //   }

// // // //   /**
// // // //    * Load current user from localStorage
// // // //    */
// // // //   private loadCurrentUser(): void {
// // // //     console.log('📥 Loading current user...');
    
// // // //     const userStr = localStorage.getItem('currentUser');
// // // //     console.log('📦 CurrentUser from localStorage:', !!userStr);
    
// // // //     if (userStr) {
// // // //       try {
// // // //         this.currentUser = JSON.parse(userStr);
// // // //         if (this.currentUser && this.currentUser.id) {
// // // //           this.requisitionForm.patchValue({
// // // //             requestedById: this.currentUser.id
// // // //           });
// // // //           console.log('✅ User ID set:', this.currentUser.id);
// // // //         }
// // // //       } catch (error) {
// // // //         console.error('⚠️ Error parsing current user:', error);
// // // //         // Set default user ID if not available
// // // //         this.requisitionForm.patchValue({
// // // //           requestedById: 1 // ⬅️ CHANGE THIS TO MATCH YOUR DATABASE
// // // //         });
// // // //       }
// // // //     } else {
// // // //       console.warn('⚠️ No user in localStorage, using default ID: 1');
// // // //       this.requisitionForm.patchValue({
// // // //         requestedById: 1 // ⬅️ CHANGE THIS TO MATCH YOUR DATABASE
// // // //       });
// // // //     }
// // // //   }

// // // //   /**
// // // //    * Calculate total cost from items
// // // //    */
// // // //   getTotalItemsCost(): number {
// // // //     let total = 0;
// // // //     const itemsArray = this.itemsFormArray;
// // // //     for (let i = 0; i < itemsArray.length; i++) {
// // // //       const quantity = itemsArray.at(i).get('quantity')?.value || 0;
// // // //       const price = itemsArray.at(i).get('price')?.value || 0;
// // // //       total += (parseFloat(quantity) * parseFloat(price));
// // // //     }
// // // //     return total;
// // // //   }

// // // //   /**
// // // //    * Get item total for a specific item
// // // //    */
// // // //   getItemTotal(index: number): number {
// // // //     const item = this.itemsFormArray.at(index);
// // // //     const quantity = item.get('quantity')?.value || 0;
// // // //     const price = item.get('price')?.value || 0;
// // // //     return parseFloat(quantity) * parseFloat(price);
// // // //   }

// // // //   /**
// // // //    * ✅ MAIN ENTRY POINT - Create requisition with items
// // // //    * This handles the 2-step process:
// // // //    * Step 1: Create requisition
// // // //    * Step 2: Add items to requisition
// // // //    */
// // // //   createRequisition(): void {
// // // //     console.log('\n════════════════════════════════════════');
// // // //     console.log('🚀 CREATE REQUISITION STARTED');
// // // //     console.log('════════════════════════════════════════');
// // // //     console.log('Form valid:', this.requisitionForm.valid);

// // // //     // Validate form
// // // //     if (this.requisitionForm.invalid) {
// // // //       console.error('❌ Form is invalid');
// // // //       this.requisitionForm.markAllAsTouched();
// // // //       this.messageService.showMessage('warning', 'Validation Error', 'Please fill all required fields');
// // // //       return;
// // // //     }

// // // //     // Get items with names
// // // //     const itemsWithName = this.itemsFormArray.value.filter(
// // // //       (item: any) => item.itemName && item.itemName.trim()
// // // //     );

// // // //     if (itemsWithName.length === 0) {
// // // //       console.error('❌ No items with names');
// // // //       this.messageService.showMessage('warning', 'Validation Error', 'Please add at least one item with a name');
// // // //       return;
// // // //     }

// // // //     this.isLoading = true;

// // // //     // Get form value
// // // //     const formValue = this.requisitionForm.getRawValue();

// // // //     // Prepare requisition data using your CreateRequisitionRequest structure
// // // //     const requisitionData: any = {
// // // //       title: formValue.title,
// // // //       category: formValue.category,
// // // //       description: formValue.description || '',
// // // //       departmentId: parseInt(formValue.departmentId),
// // // //       requestedById: parseInt(formValue.requestedById),
// // // //       currency: formValue.currency,
// // // //       estimatedCost: parseFloat(formValue.estimatedCost),
// // // //       deliveryDate: formValue.deliveryDate,
// // // //       deliveryLocation: formValue.deliveryLocation
// // // //     };

// // // //     console.log('📋 Requisition data:', requisitionData);
// // // //     console.log('📦 Items to add:', itemsWithName);

// // // //     // STEP 1: Create requisition first
// // // //     this.requisitionService.createRequisition(requisitionData)
// // // //       .pipe(takeUntil(this.destroy$))
// // // //       .subscribe({
// // // //         next: (response: any) => {
// // // //           console.log('✅ Step 1 Complete - Requisition created:', response);
          
// // // //           // Extract requisition ID from response
// // // //           // Handle different response structures
// // // //           let requisitionId: number | null = null;
          
// // // //           if (response.data && response.data.id) {
// // // //             requisitionId = response.data.id;
// // // //           } else if (response.id) {
// // // //             requisitionId = response.id;
// // // //           }
          
// // // //           if (!requisitionId) {
// // // //             console.error('❌ No requisition ID in response:', response);
// // // //             this.messageService.showMessage('error', 'Error', 'Could not get requisition ID from response');
// // // //             this.isLoading = false;
// // // //             return;
// // // //           }

// // // //           this.currentRequisitionId = requisitionId;
// // // //           console.log('🎯 Requisition ID:', requisitionId);
          
// // // //           this.messageService.showMessage('success', 'Success', 'Requisition created successfully');

// // // //           // STEP 2: Add items to the created requisition
// // // //           this.addItemsToRequisition(requisitionId, itemsWithName);
// // // //         },
// // // //         error: (error: any) => {
// // // //           console.error('❌ Step 1 Failed - Error creating requisition:', error);
// // // //           this.isLoading = false;
          
// // // //           const errorMsg = error.error?.message || error.message || 'Failed to create requisition';
// // // //           this.messageService.showMessage('error', 'Error', errorMsg);
// // // //         }
// // // //       });
// // // //   }

// // // //   /**
// // // //    * ✅ STEP 2: Add items to requisition sequentially
// // // //    */
// // // //   private addItemsToRequisition(requisitionId: number, items: any[]): void {
// // // //     console.log('\n════════════════════════════════════════');
// // // //     console.log(`📦 STEP 2 - ADDING ITEMS TO REQUISITION ${requisitionId}`);
// // // //     console.log('════════════════════════════════════════');
// // // //     console.log(`Total items to add: ${items.length}`);

// // // //     this.isAddingItems = true;
// // // //     this.totalItemsToAdd = items.length;
// // // //     this.itemsAddedCount = 0;

// // // //     let successCount = 0;
// // // //     let failureCount = 0;
// // // //     const failedItems: string[] = [];

// // // //     // Add items sequentially (one after another)
// // // //     const addItemSequentially = (index: number) => {
// // // //       if (index >= items.length) {
// // // //         // All items processed
// // // //         this.isAddingItems = false;
// // // //         this.isLoading = false;

// // // //         console.log('\n════════════════════════════════════════');
// // // //         console.log('✅ ITEMS ADDITION COMPLETE');
// // // //         console.log('════════════════════════════════════════');
// // // //         console.log(`Success: ${successCount}, Failed: ${failureCount}`);

// // // //         if (failureCount === 0) {
// // // //           // All items added successfully
// // // //           this.messageService.showMessage(
// // // //             'success',
// // // //             'Success',
// // // //             `All ${successCount} items added successfully! Redirecting...`
// // // //           );
          
// // // //           // Reset form
// // // //           this.requisitionForm.reset({ currency: 'USD' });
// // // //           this.requisitionForm.setControl('items', this.fb.array([this.createItemGroup()]));
          
// // // //           // Navigate to dashboard after 1.5 seconds
// // // //           setTimeout(() => {
// // // //             this.router.navigate(['/buyer-dashboard']);
// // // //           }, 1500);
// // // //         } else if (successCount > 0) {
// // // //           // Some items added
// // // //           this.messageService.showMessage(
// // // //             'warning',
// // // //             'Partial Success',
// // // //             `Added ${successCount}/${items.length} items. Failed: ${failedItems.join(', ')}`
// // // //           );
// // // //         } else {
// // // //           // All items failed
// // // //           this.messageService.showMessage(
// // // //             'error',
// // // //             'Error',
// // // //             `Failed to add all items. Requisition created but items not added.`
// // // //           );
// // // //         }
// // // //         return;
// // // //       }

// // // //       const item = items[index];
// // // //       console.log(`\n➕ Adding item ${index + 1}/${items.length}: "${item.itemName}"`);

// // // //       // Prepare item data using your AddRequisitionItemRequest structure
// // // //       const itemData: any = {
// // // //         itemName: item.itemName,
// // // //         itemDescription: item.itemDescription || '',
// // // //         quantity: parseInt(item.quantity),
// // // //         price: parseFloat(item.price)
// // // //       };

// // // //       this.requisitionService.addItemToRequisition(requisitionId, itemData)
// // // //         .pipe(takeUntil(this.destroy$))
// // // //         .subscribe({
// // // //           next: (response: any) => {
// // // //             console.log(`✅ Item ${index + 1} added successfully:`, response);
// // // //             successCount++;
// // // //             this.itemsAddedCount++;
// // // //             addItemSequentially(index + 1); // Add next item
// // // //           },
// // // //           error: (error: any) => {
// // // //             console.error(`❌ Item ${index + 1} failed:`, error);
// // // //             failureCount++;
// // // //             failedItems.push(item.itemName);
// // // //             this.itemsAddedCount++;
// // // //             addItemSequentially(index + 1); // Continue to next item even if this one failed
// // // //           }
// // // //         });
// // // //     };

// // // //     // Start adding items from index 0
// // // //     addItemSequentially(0);
// // // //   }

// // // //   /**
// // // //    * Check if form is valid
// // // //    */
// // // //   get isFormValid(): boolean {
// // // //     return this.requisitionForm.valid && this.itemsFormArray.length > 0;
// // // //   }

// // // //   /**
// // // //    * Get form control for error messages
// // // //    */
// // // //   getControl(controlName: string) {
// // // //     return this.requisitionForm.get(controlName);
// // // //   }

// // // //   /**
// // // //    * Check if control has error
// // // //    */
// // // //   hasError(controlName: string): boolean {
// // // //     const control = this.getControl(controlName);
// // // //     return !!(control && control.invalid && (control.dirty || control.touched));
// // // //   }

// // // //   /**
// // // //    * Get error message
// // // //    */
// // // //   getErrorMessage(controlName: string): string {
// // // //     const control = this.getControl(controlName);
// // // //     if (!control || !control.errors) {
// // // //       return '';
// // // //     }

// // // //     if (control.errors['required']) {
// // // //       return `${controlName} is required`;
// // // //     }
// // // //     if (control.errors['minlength']) {
// // // //       const minLength = control.errors['minlength'].requiredLength;
// // // //       return `${controlName} must be at least ${minLength} characters`;
// // // //     }
// // // //     if (control.errors['min']) {
// // // //       const minValue = control.errors['min'].min;
// // // //       return `${controlName} must be at least ${minValue}`;
// // // //     }
// // // //     return 'Invalid field';
// // // //   }

// // // //   /**
// // // //    * Get item control for error messages
// // // //    */
// // // //   getItemControl(index: number, controlName: string) {
// // // //     return this.itemsFormArray.at(index).get(controlName);
// // // //   }

// // // //   /**
// // // //    * Check if item control has error
// // // //    */
// // // //   itemHasError(index: number, controlName: string): boolean {
// // // //     const control = this.getItemControl(index, controlName);
// // // //     return !!(control && control.invalid && (control.dirty || control.touched));
// // // //   }
// // // // }

// // // // File: src/app/features/requisition/components/create-roq/create-roq.component.ts
// // // import { Component, OnInit, OnDestroy } from '@angular/core';
// // // import { CommonModule } from '@angular/common';
// // // import { FormBuilder, FormGroup, FormArray, Validators, ReactiveFormsModule } from '@angular/forms';
// // // import { Router } from '@angular/router';
// // // import { RequisitionService } from './requisition.service';
// // // import { MessageService } from '../../../shared/service/message.service';
// // // import { Subject } from 'rxjs';
// // // import { takeUntil } from 'rxjs/operators';

// // // @Component({
// // //   selector: 'app-create-roq',
// // //   standalone: true,
// // //   imports: [CommonModule, ReactiveFormsModule],
// // //   templateUrl: './create-roq.component.html',
// // //   styleUrls: ['./create-roq.component.css']
// // // })
// // // export class CreateRoqComponent implements OnInit, OnDestroy {

// // //   requisitionForm!: FormGroup;

// // //   // Data
// // //   departments: any[] = [];
// // //   currentUser: any = null;
// // //   currentRequisitionId: number | null = null;

// // //   // UI States
// // //   isLoading = false;
// // //   isAddingItems = false;
// // //   itemsAddedCount = 0;
// // //   totalItemsToAdd = 0;

// // //   // Currency and Category options
// // //   currencies = ['USD', 'EUR', 'GBP', 'INR', 'JPY', 'AUD', 'CAD'];
// // //   categories = ['Office Supplies', 'Equipment', 'Software', 'Services', 'Maintenance', 'Hardware'];

// // //   // To manage subscriptions
// // //   private destroy$ = new Subject<void>();

// // //   // Reactive form valid flag
// // //   isFormValidFlag = false;

// // //   constructor(
// // //     private fb: FormBuilder,
// // //     private requisitionService: RequisitionService,
// // //     private messageService: MessageService,
// // //     private router: Router
// // //   ) {
// // //     this.initializeForms();
// // //   }

// // //   ngOnInit(): void {
// // //     console.log('🚀 CreateRoqComponent initialized');
// // //     this.loadDepartments();
// // //     this.loadCurrentUser();

// // //     // Subscribe to form status changes to update isFormValidFlag
// // //     this.requisitionForm.statusChanges
// // //       .pipe(takeUntil(this.destroy$))
// // //       .subscribe(() => {
// // //         this.isFormValidFlag = this.requisitionForm.valid && this.itemsFormArray.length > 0;
// // //       });
// // //   }

// // //   ngOnDestroy(): void {
// // //     this.destroy$.next();
// // //     this.destroy$.complete();
// // //   }

// // //   /**
// // //    * Initialize form groups
// // //    */
// // //   private initializeForms(): void {
// // //     this.requisitionForm = this.fb.group({
// // //       title: ['', [Validators.required, Validators.minLength(3)]],
// // //       category: ['', Validators.required],
// // //       description: [''],
// // //       departmentId: ['', Validators.required],
// // //       requestedById: ['', Validators.required],
// // //       currency: ['USD', Validators.required],
// // //       estimatedCost: ['', [Validators.required, Validators.min(0)]],
// // //       deliveryDate: ['', Validators.required],
// // //       deliveryLocation: ['', [Validators.required, Validators.minLength(3)]],
// // //       items: this.fb.array([this.createItemGroup()])
// // //     });
// // //   }

// // //   /**
// // //    * Create item form group
// // //    */
// // //   private createItemGroup(): FormGroup {
// // //     return this.fb.group({
// // //       itemName: ['', [Validators.required, Validators.minLength(2)]],
// // //       itemDescription: [''],
// // //       quantity: ['', [Validators.required, Validators.min(1)]],
// // //       price: ['', [Validators.required, Validators.min(0)]]
// // //     });
// // //   }

// // //   /**
// // //    * Get items FormArray
// // //    */
// // //   get itemsFormArray(): FormArray {
// // //     return this.requisitionForm.get('items') as FormArray;
// // //   }

// // //   /**
// // //    * Add new item
// // //    */
// // //   addItem(): void {
// // //     this.itemsFormArray.push(this.createItemGroup());
// // //     console.log('➕ Item added. Total items:', this.itemsFormArray.length);
// // //     this.updateFormValidity();
// // //   }

// // //   /**
// // //    * Remove item
// // //    */
// // //   removeItem(index: number): void {
// // //     if (this.itemsFormArray.length > 1) {
// // //       this.itemsFormArray.removeAt(index);
// // //       console.log('🗑️ Item removed. Total items:', this.itemsFormArray.length);
// // //       this.updateFormValidity();
// // //     } else {
// // //       this.messageService.showMessage('warning', 'Warning', 'At least one item is required');
// // //     }
// // //   }

// // //   /**
// // //    * Update reactive isFormValidFlag
// // //    */
// // //   private updateFormValidity(): void {
// // //     this.isFormValidFlag = this.requisitionForm.valid && this.itemsFormArray.length > 0;
// // //   }

// // //   /**
// // //    * Load departments - mock data
// // //    */
// // //   private loadDepartments(): void {
// // //     this.departments = [
// // //       { id: 1, departmentName: 'Sales', departmentDescription: 'Sales Department' },
// // //       { id: 2, departmentName: 'IT', departmentDescription: 'IT Department' },
// // //       { id: 3, departmentName: 'HR', departmentDescription: 'Human Resources' },
// // //       { id: 4, departmentName: 'Finance', departmentDescription: 'Finance Department' },
// // //       { id: 5, departmentName: 'Operations', departmentDescription: 'Operations Department' }
// // //     ];
// // //   }

// // //   /**
// // //    * Load current user from localStorage
// // //    */
// // //   private loadCurrentUser(): void {
// // //     const userStr = localStorage.getItem('currentUser');
// // //     if (userStr) {
// // //       try {
// // //         this.currentUser = JSON.parse(userStr);
// // //         this.requisitionForm.patchValue({
// // //           requestedById: this.currentUser?.id || 1
// // //         });
// // //       } catch {
// // //         this.requisitionForm.patchValue({ requestedById: 1 });
// // //       }
// // //     } else {
// // //       this.requisitionForm.patchValue({ requestedById: 1 });
// // //     }
// // //   }

// // //   /**
// // //    * Calculate total cost from items
// // //    */
// // //   getTotalItemsCost(): number {
// // //     let total = 0;
// // //     const itemsArray = this.itemsFormArray;
// // //     for (let i = 0; i < itemsArray.length; i++) {
// // //       const quantity = itemsArray.at(i).get('quantity')?.value || 0;
// // //       const price = itemsArray.at(i).get('price')?.value || 0;
// // //       total += (parseFloat(quantity) * parseFloat(price));
// // //     }
// // //     return total;
// // //   }

// // //   /**
// // //    * Get item total for a specific item
// // //    */
// // //   getItemTotal(index: number): number {
// // //     const item = this.itemsFormArray.at(index);
// // //     const quantity = item.get('quantity')?.value || 0;
// // //     const price = item.get('price')?.value || 0;
// // //     return parseFloat(quantity) * parseFloat(price);
// // //   }

// // //   /**
// // //    * Create requisition
// // //    */
// // //   createRequisition(): void {
// // //     if (this.requisitionForm.invalid) {
// // //       this.requisitionForm.markAllAsTouched();
// // //       this.messageService.showMessage('warning', 'Validation Error', 'Please fill all required fields');
// // //       return;
// // //     }

// // //     const itemsWithName = this.itemsFormArray.value.filter(
// // //       (item: any) => item.itemName && item.itemName.trim()
// // //     );

// // //     if (itemsWithName.length === 0) {
// // //       this.messageService.showMessage('warning', 'Validation Error', 'Please add at least one item with a name');
// // //       return;
// // //     }

// // //     this.isLoading = true;

// // //     const formValue = this.requisitionForm.getRawValue();
// // //     const requisitionData: any = {
// // //       title: formValue.title,
// // //       category: formValue.category,
// // //       description: formValue.description || '',
// // //       departmentId: parseInt(formValue.departmentId),
// // //       requestedById: parseInt(formValue.requestedById),
// // //       currency: formValue.currency,
// // //       estimatedCost: parseFloat(formValue.estimatedCost),
// // //       deliveryDate: formValue.deliveryDate,
// // //       deliveryLocation: formValue.deliveryLocation
// // //     };

// // //     // STEP 1: Create requisition
// // //     this.requisitionService.createRequisition(requisitionData)
// // //       .pipe(takeUntil(this.destroy$))
// // //       .subscribe({
// // //         next: (response: any) => {
// // //           let requisitionId: number | null = response.data?.id || response.id || null;
// // //           if (!requisitionId) {
// // //             this.messageService.showMessage('error', 'Error', 'Could not get requisition ID from response');
// // //             this.isLoading = false;
// // //             return;
// // //           }
// // //           this.currentRequisitionId = requisitionId;
// // //           this.messageService.showMessage('success', 'Success', 'Requisition created successfully');
// // //           this.addItemsToRequisition(requisitionId, itemsWithName);
// // //         },
// // //         error: (error: any) => {
// // //           this.isLoading = false;
// // //           const errorMsg = error.error?.message || error.message || 'Failed to create requisition';
// // //           this.messageService.showMessage('error', 'Error', errorMsg);
// // //         }
// // //       });
// // //   }

// // //   /**
// // //    * Add items sequentially
// // //    */
// // //   private addItemsToRequisition(requisitionId: number, items: any[]): void {
// // //     this.isAddingItems = true;
// // //     this.totalItemsToAdd = items.length;
// // //     this.itemsAddedCount = 0;

// // //     let successCount = 0;
// // //     let failureCount = 0;
// // //     const failedItems: string[] = [];

// // //     const addItemSequentially = (index: number) => {
// // //       if (index >= items.length) {
// // //         this.isAddingItems = false;
// // //         this.isLoading = false;
// // //         if (failureCount === 0) {
// // //           this.messageService.showMessage('success', 'Success', `All ${successCount} items added successfully! Redirecting...`);
// // //           this.requisitionForm.reset({ currency: 'USD' });
// // //           this.requisitionForm.setControl('items', this.fb.array([this.createItemGroup()]));
// // //           setTimeout(() => this.router.navigate(['/buyer-dashboard']), 1500);
// // //         } else if (successCount > 0) {
// // //           this.messageService.showMessage('warning', 'Partial Success', `Added ${successCount}/${items.length} items. Failed: ${failedItems.join(', ')}`);
// // //         } else {
// // //           this.messageService.showMessage('error', 'Error', `Failed to add all items.`);
// // //         }
// // //         this.updateFormValidity();
// // //         return;
// // //       }

// // //       const item = items[index];
// // //       const itemData: any = {
// // //         itemName: item.itemName,
// // //         itemDescription: item.itemDescription || '',
// // //         quantity: parseInt(item.quantity),
// // //         price: parseFloat(item.price)
// // //       };

// // //       this.requisitionService.addItemToRequisition(requisitionId, itemData)
// // //         .pipe(takeUntil(this.destroy$))
// // //         .subscribe({
// // //           next: () => {
// // //             successCount++;
// // //             this.itemsAddedCount++;
// // //             addItemSequentially(index + 1);
// // //           },
// // //           error: () => {
// // //             failureCount++;
// // //             failedItems.push(item.itemName);
// // //             this.itemsAddedCount++;
// // //             addItemSequentially(index + 1);
// // //           }
// // //         });
// // //     };

// // //     addItemSequentially(0);
// // //   }

// // //   /**
// // //    * Get form control
// // //    */
// // //   getControl(controlName: string) {
// // //     return this.requisitionForm.get(controlName);
// // //   }

// // //   /**
// // //    * Check if control has error
// // //    */
// // //   hasError(controlName: string): boolean {
// // //     const control = this.getControl(controlName);
// // //     return !!(control && control.invalid && (control.dirty || control.touched));
// // //   }

// // //   /**
// // //    * Get error message
// // //    */
// // //   getErrorMessage(controlName: string): string {
// // //     const control = this.getControl(controlName);
// // //     if (!control || !control.errors) return '';
// // //     if (control.errors['required']) return `${controlName} is required`;
// // //     if (control.errors['minlength']) return `${controlName} must be at least ${control.errors['minlength'].requiredLength} characters`;
// // //     if (control.errors['min']) return `${controlName} must be at least ${control.errors['min'].min}`;
// // //     return 'Invalid field';
// // //   }

// // //   getItemControl(index: number, controlName: string) {
// // //     return this.itemsFormArray.at(index).get(controlName);
// // //   }

// // //   itemHasError(index: number, controlName: string): boolean {
// // //     const control = this.getItemControl(index, controlName);
// // //     return !!(control && control.invalid && (control.dirty || control.touched));
// // //   }
// // // }



// // // // File: src/app/views/base/create-roq/create-roq.component.ts
// // // import { Component, OnInit, OnDestroy } from '@angular/core';
// // // import { CommonModule } from '@angular/common';
// // // import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
// // // import { Router, ActivatedRoute } from '@angular/router';
// // // import { RFQService } from '../rfq.service'; // Your RFQ service
// // // import { RequisitionService } from '../requisition.service'; // Your Requisition service
// // // import { MessageService } from '../../../../shared/service/message.service';
// // // import { Subject } from 'rxjs';
// // // import { takeUntil } from 'rxjs/operators';
// // // import { FormsModule } from '@angular/forms';

// // // @Component({
// // //   selector: 'app-create-rfq',
// // //   standalone: true,
// // //   imports: [CommonModule, ReactiveFormsModule, FormsModule],
// // //   templateUrl: './create-roq.component.html',
// // //   styleUrls: ['./create-roq.component.css']
// // // })
// // // export class CreateRFQComponent implements OnInit, OnDestroy {

// // //   // ==================== FORMS ====================
// // //   rfqForm!: FormGroup;
  
// // //   // ==================== DATA ====================
// // //   requisitionId: number | null = null;
// // //   requisition: any = null;
// // //   suppliers: any[] = [];
// // //   filteredSuppliers: any[] = [];
// // //   selectedSuppliers: Map<number, {
// // //     supplierId: number;
// // //     departmentId: number;
// // //     departmentName: string;
// // //     supplierName: string;
// // //   }> = new Map();

// // //   // ==================== UI STATE ====================
// // //   currentStep = 1;
// // //   maxSteps = 3;
// // //   isLoading = false;
// // //   isSubmitting = false;
  
// // //   // Supplier selection
// // //   showSupplierSelector = false;
// // //   searchSuppliersText = '';

// // //   // ==================== SUBSCRIPTIONS ====================
// // //   private destroy$ = new Subject<void>();

// // //   constructor(
// // //     private fb: FormBuilder,
// // //     private rfqService: RFQService,
// // //     private requisitionService: RequisitionService,
// // //     private messageService: MessageService,
// // //     private router: Router,
// // //     private route: ActivatedRoute
// // //   ) {}

// // //   ngOnInit(): void {
// // //     console.log('🚀 CreateRFQComponent initialized');
// // //     this.initializeForm();
// // //     this.getRequisitionIdFromRoute();
// // //     this.loadSuppliers();
// // //   }

// // //   ngOnDestroy(): void {
// // //     this.destroy$.next();
// // //     this.destroy$.complete();
// // //   }

// // //   /**
// // //    * ==================== STEP 0: GET REQUISITION ID FROM URL ====================
// // //    * This gets the :requisitionId parameter from the URL
// // //    * Example: /buyer-req/5 → requisitionId = 5
// // //    */
// // //   private getRequisitionIdFromRoute(): void {
// // //     console.log('📍 Getting requisition ID from route...');
    
// // //     this.route.paramMap
// // //       .pipe(takeUntil(this.destroy$))
// // //       .subscribe(params => {
// // //         const idParam = params.get('requisitionId');
        
// // //         if (!idParam) {
// // //           console.error('❌ No requisitionId in route');
// // //           this.messageService.showMessage('error', 'Error', 
// // //             'No requisition ID provided. Cannot create RFQ without a requisition.');
// // //           this.router.navigate(['/buyer-dashboard']);
// // //           return;
// // //         }

// // //         this.requisitionId = Number(idParam);
// // //         console.log('✅ Requisition ID:', this.requisitionId);
// // //         this.loadRequisitionData();
// // //       });
// // //   }

// // //   /**
// // //    * ==================== STEP 1: INITIALIZE EMPTY FORM ====================
// // //    * Create form structure with validators
// // //    */
// // //   private initializeForm(): void {
// // //     console.log('📋 Initializing RFQ form...');
    
// // //     this.rfqForm = this.fb.group({
// // //       title: ['', [Validators.required, Validators.minLength(3)]],
// // //       category: ['', Validators.required],
// // //       description: [''],
// // //       notes: [''],
// // //       currency: ['USD', Validators.required],
// // //       deliveryDate: ['', Validators.required],
// // //       deliveryLocation: ['', [Validators.required, Validators.minLength(3)]]
// // //     });
// // //   }

// // //   /**
// // //    * ==================== STEP 2: LOAD REQUISITION DATA ====================
// // //    * Fetch real requisition data from API using the ID from URL
// // //    * API: GET /api/requisition/:id
// // //    */
// // //   private loadRequisitionData(): void {
// // //     if (!this.requisitionId) {
// // //       console.error('❌ Cannot load requisition: ID is null');
// // //       return;
// // //     }

// // //     this.isLoading = true;
// // //     console.log(`📥 Loading requisition data for ID: ${this.requisitionId}...`);

// // //     this.requisitionService.getRequisitionById(this.requisitionId)
// // //       .pipe(takeUntil(this.destroy$))
// // //       .subscribe({
// // //         next: (response: any) => {
// // //           console.log('✅ Requisition loaded:', response);
          
// // //           // Extract requisition data
// // //           this.requisition = response.data || response;
          
// // //           // Check if requisition has submitted status
// // //           if (this.requisition.status !== 'SUBMITTED') {
// // //             console.warn('⚠️ Requisition is not in SUBMITTED status');
// // //             this.messageService.showMessage('warning', 'Warning',
// // //               'This requisition must be in SUBMITTED status to create an RFQ');
// // //           }

// // //           // Pre-fill form with requisition data
// // //           this.populateFormFromRequisition();
// // //           this.isLoading = false;
// // //           console.log('✅ Form pre-filled with requisition data');
// // //         },
// // //         error: (err: any) => {
// // //           console.error('❌ Error loading requisition:', err);
// // //           this.isLoading = false;
          
// // //           const errorMsg = err.error?.message || 'Failed to load requisition';
// // //           this.messageService.showMessage('error', 'Error', errorMsg);
          
// // //           // Go back to dashboard on error
// // //           setTimeout(() => {
// // //             this.router.navigate(['/buyer-dashboard']);
// // //           }, 2000);
// // //         }
// // //       });
// // //   }

// // //   /**
// // //    * ==================== STEP 3: PRE-FILL FORM ====================
// // //    * Populate form fields with data from loaded requisition
// // //    */
// // //   private populateFormFromRequisition(): void {
// // //     if (!this.requisition) {
// // //       console.warn('⚠️ No requisition data to populate form');
// // //       return;
// // //     }

// // //     console.log('📝 Populating form with requisition data...');

// // //     this.rfqForm.patchValue({
// // //       title: this.requisition.title || '',
// // //       category: this.requisition.category || '',
// // //       description: this.requisition.description || '',
// // //       currency: this.requisition.currency || 'USD',
// // //       deliveryDate: this.formatDateForInput(this.requisition.deliveryDate),
// // //       deliveryLocation: this.requisition.deliveryLocation || ''
// // //     });

// // //     console.log('✅ Form values set:');
// // //     console.log('  - Title:', this.requisition.title);
// // //     console.log('  - Category:', this.requisition.category);
// // //     console.log('  - Currency:', this.requisition.currency);
// // //     console.log('  - Items Count:', this.requisition.items?.length);
// // //   }

// // //   /**
// // //    * Helper: Format date for HTML date input (YYYY-MM-DD)
// // //    */
// // //   private formatDateForInput(dateString: string): string {
// // //     if (!dateString) return '';
// // //     try {
// // //       const date = new Date(dateString);
// // //       return date.toISOString().split('T')[0];
// // //     } catch {
// // //       return '';
// // //     }
// // //   }

// // //   /**
// // //    * ==================== STEP 4: LOAD SUPPLIERS ====================
// // //    * Fetch all suppliers from API
// // //    * API: GET /api/supplier
// // //    */
// // //   private loadSuppliers(): void {
// // //     console.log('👥 Loading suppliers from API...');
    
// // //     this.rfqService.getAllSuppliers()
// // //       .pipe(takeUntil(this.destroy$))
// // //       .subscribe({
// // //         next: (response: any) => {
// // //           this.suppliers = response.data || response || [];
// // //           this.filteredSuppliers = this.suppliers;
          
// // //           console.log('✅ Suppliers loaded:', this.suppliers.length);
// // //           this.suppliers.forEach(s => {
// // //             console.log(`  - ${s.companyName} (${s.industrySector})`);
// // //           });

// // //           if (this.suppliers.length === 0) {
// // //             console.warn('⚠️ No suppliers found in database');
// // //             this.messageService.showMessage('warning', 'Warning',
// // //               'No suppliers available. Please add suppliers first.');
// // //           }
// // //         },
// // //         error: (err: any) => {
// // //           console.error('❌ Error loading suppliers:', err);
// // //           this.messageService.showMessage('error', 'Error', 
// // //             'Failed to load suppliers');
// // //         }
// // //       });
// // //   }

// // //   /**
// // //    * ==================== STEP 5A: SEARCH SUPPLIERS ====================
// // //    * Filter suppliers based on user search input
// // //    */
// // //   searchSuppliers(): void {
// // //     const term = this.searchSuppliersText.toLowerCase().trim();
// // //     console.log(`🔍 Searching suppliers for: "${term}"`);

// // //     if (term) {
// // //       this.filteredSuppliers = this.suppliers.filter(s =>
// // //         (s.companyName || '').toLowerCase().includes(term) ||
// // //         (s.industrySector || '').toLowerCase().includes(term)
// // //       );
// // //       console.log(`✅ Found ${this.filteredSuppliers.length} suppliers matching "${term}"`);
// // //     } else {
// // //       this.filteredSuppliers = this.suppliers;
// // //       console.log('✅ Showing all suppliers');
// // //     }
// // //   }

// // //   /**
// // //    * ==================== STEP 5B: TOGGLE SUPPLIER SELECTOR ====================
// // //    */
// // //   toggleSupplierSelector(): void {
// // //     this.showSupplierSelector = !this.showSupplierSelector;
// // //     if (!this.showSupplierSelector) {
// // //       this.searchSuppliersText = '';
// // //       this.filteredSuppliers = this.suppliers;
// // //     }
// // //   }

// // //   /**
// // //    * ==================== STEP 5C: GET SUPPLIER DEPARTMENTS ====================
// // //    * Extract departments for a specific supplier
// // //    */
// // //   getSupplierDepartments(supplierId: number): any[] {
// // //     const supplier = this.suppliers.find(s => s.id === supplierId);
// // //     if (!supplier || !supplier.locations) {
// // //       return [];
// // //     }

// // //     const departments: any[] = [];
// // //     supplier.locations.forEach((location: any) => {
// // //       if (location.departments) {
// // //         location.departments.forEach((dept: any) => {
// // //           departments.push({
// // //             id: dept.id,
// // //             name: dept.departmentName,
// // //             locationName: location.locationName
// // //           });
// // //         });
// // //       }
// // //     });
// // //     return departments;
// // //   }

// // //   /**
// // //    * ==================== STEP 5D: SELECT SUPPLIER & DEPARTMENT ====================
// // //    * Add selected supplier to selectedSuppliers Map
// // //    */
// // //   selectSupplierDepartment(supplierId: number, departmentId: number, departmentName: string): void {
// // //     const supplier = this.suppliers.find(s => s.id === supplierId);
// // //     if (supplier) {
// // //       this.selectedSuppliers.set(supplierId, {
// // //         supplierId,
// // //         departmentId,
// // //         departmentName,
// // //         supplierName: supplier.companyName
// // //       });
      
// // //       console.log(`✅ Selected: ${supplier.companyName} - ${departmentName}`);
// // //       this.messageService.showMessage('success', 'Success', 
// // //         `${supplier.companyName} - ${departmentName} selected`);
// // //     }
// // //   }

// // //   /**
// // //    * ==================== STEP 5E: REMOVE SELECTED SUPPLIER ====================
// // //    */
// // //   removeSelectedSupplier(supplierId: number): void {
// // //     const supplier = this.selectedSuppliers.get(supplierId);
// // //     if (supplier) {
// // //       console.log(`❌ Removed: ${supplier.supplierName}`);
// // //     }
// // //     this.selectedSuppliers.delete(supplierId);
// // //   }

// // //   /**
// // //    * Get selected suppliers as array for display
// // //    */
// // //   get selectedSuppliersArray(): any[] {
// // //     return Array.from(this.selectedSuppliers.values());
// // //   }

// // //   /**
// // //    * ==================== STEP 6A: NAVIGATE TO NEXT STEP ====================
// // //    * Validate current step before moving to next
// // //    */
// // //   nextStep(): void {
// // //     if (this.currentStep >= this.maxSteps) {
// // //       return;
// // //     }

// // //     // Validate STEP 1: Form must be valid
// // //     if (this.currentStep === 1) {
// // //       if (this.rfqForm.invalid) {
// // //         console.warn('⚠️ Step 1 validation failed');
// // //         this.messageService.showMessage('warning', 'Validation Error', 
// // //           'Please fill all required fields in Step 1');
// // //         this.rfqForm.markAllAsTouched();
// // //         return;
// // //       }
// // //       console.log('✅ Step 1 validation passed');
// // //     }

// // //     // Validate STEP 2: At least 1 supplier selected
// // //     if (this.currentStep === 2) {
// // //       if (this.selectedSuppliers.size === 0) {
// // //         console.warn('⚠️ Step 2 validation failed');
// // //         this.messageService.showMessage('warning', 'Validation Error', 
// // //           'Please select at least one supplier in Step 2');
// // //         return;
// // //       }
// // //       console.log(`✅ Step 2 validation passed (${this.selectedSuppliers.size} suppliers selected)`);
// // //     }

// // //     this.currentStep++;
// // //     console.log(`📍 Moving to Step ${this.currentStep}`);
// // //   }

// // //   /**
// // //    * ==================== STEP 6B: NAVIGATE TO PREVIOUS STEP ====================
// // //    */
// // //   prevStep(): void {
// // //     if (this.currentStep > 1) {
// // //       this.currentStep--;
// // //       console.log(`📍 Moving back to Step ${this.currentStep}`);
// // //     }
// // //   }

// // //   /**
// // //    * ==================== STEP 7: CREATE RFQ WORKFLOW ====================
// // //    * Complete workflow:
// // //    * 1. Create RFQ from requisition
// // //    * 2. Add suppliers to RFQ
// // //    * 3. Submit RFQ
// // //    * 4. Send RFQ to suppliers
// // //    */
// // //   createRFQ(): void {
// // //     console.log('\n════════════════════════════════════════');
// // //     console.log('🚀 CREATE RFQ WORKFLOW STARTED');
// // //     console.log('════════════════════════════════════════');

// // //     // Final validation
// // //     if (this.rfqForm.invalid || this.selectedSuppliers.size === 0) {
// // //       console.error('❌ Final validation failed');
// // //       this.messageService.showMessage('warning', 'Validation Error', 
// // //         'Please complete all required steps');
// // //       return;
// // //     }

// // //     if (!this.requisitionId) {
// // //       console.error('❌ Requisition ID missing');
// // //       this.messageService.showMessage('error', 'Error', 
// // //         'Requisition ID is required');
// // //       return;
// // //     }

// // //     this.isSubmitting = true;
// // //     console.log(`📝 Creating RFQ from Requisition ID: ${this.requisitionId}`);

// // //     // Step 1: Create RFQ from requisition
// // //     this.rfqService.createRFQFromRequisition(this.requisitionId)
// // //       .pipe(takeUntil(this.destroy$))
// // //       .subscribe({
// // //         next: (response: any) => {
// // //           const rfqId = response.data?.id || response.id;
// // //           if (!rfqId) {
// // //             console.error('❌ No RFQ ID in response');
// // //             this.messageService.showMessage('error', 'Error', 'Failed to create RFQ');
// // //             this.isSubmitting = false;
// // //             return;
// // //           }
// // //           console.log(`✅ Step 1 Complete - RFQ created with ID: ${rfqId}`);
// // //           this.addSuppliersToRFQ(rfqId);
// // //         },
// // //         error: (err: any) => {
// // //           this.isSubmitting = false;
// // //           console.error('❌ Step 1 Failed:', err);
// // //           const errorMsg = err.error?.message || 'Failed to create RFQ';
// // //           this.messageService.showMessage('error', 'Error', errorMsg);
// // //         }
// // //       });
// // //   }

// // //   /**
// // //    * Step 2: Add suppliers to RFQ
// // //    */
// // //   private addSuppliersToRFQ(rfqId: number): void {
// // //     const supplierSelections = Array.from(this.selectedSuppliers.values()).map(s => ({
// // //       supplierId: s.supplierId,
// // //       departmentId: s.departmentId
// // //     }));

// // //     console.log(`➕ Step 2 - Adding ${supplierSelections.length} suppliers to RFQ ${rfqId}`);

// // //     this.rfqService.addSuppliersToRFQ(rfqId, supplierSelections)
// // //       .pipe(takeUntil(this.destroy$))
// // //       .subscribe({
// // //         next: (response: any) => {
// // //           console.log('✅ Step 2 Complete - Suppliers added to RFQ');
// // //           this.submitRFQ(rfqId);
// // //         },
// // //         error: (err: any) => {
// // //           this.isSubmitting = false;
// // //           console.error('❌ Step 2 Failed:', err);
// // //           const errorMsg = err.error?.message || 'Failed to add suppliers';
// // //           this.messageService.showMessage('error', 'Error', errorMsg);
// // //         }
// // //       });
// // //   }

// // //   /**
// // //    * Step 3: Submit RFQ
// // //    */
// // //   private submitRFQ(rfqId: number): void {
// // //     console.log(`📤 Step 3 - Submitting RFQ ${rfqId}`);

// // //     this.rfqService.submitRFQ(rfqId)
// // //       .pipe(takeUntil(this.destroy$))
// // //       .subscribe({
// // //         next: (response: any) => {
// // //           console.log('✅ Step 3 Complete - RFQ submitted');
// // //           this.sendRFQToSuppliers(rfqId);
// // //         },
// // //         error: (err: any) => {
// // //           this.isSubmitting = false;
// // //           console.error('❌ Step 3 Failed:', err);
// // //           const errorMsg = err.error?.message || 'Failed to submit RFQ';
// // //           this.messageService.showMessage('error', 'Error', errorMsg);
// // //         }
// // //       });
// // //   }

// // //   /**
// // //    * Step 4: Send RFQ to suppliers
// // //    */
// // //   private sendRFQToSuppliers(rfqId: number): void {
// // //     const userId = this.getUserIdFromLocalStorage();
// // //     console.log(`📨 Step 4 - Sending RFQ ${rfqId} to suppliers`);
    
// // //     this.rfqService.sendRFQToSuppliers(rfqId, userId)
// // //       .pipe(takeUntil(this.destroy$))
// // //       .subscribe({
// // //         next: (response: any) => {
// // //           const rfqData = response.data;
// // //           console.log(`✅ Step 4 Complete - RFQ sent to suppliers`);
// // //           console.log(`✅ RFQ Number: ${rfqData.rfqNumber}`);
          
// // //           this.messageService.showMessage('success', 'Success',
// // //             `RFQ created and sent to ${this.selectedSuppliers.size} suppliers! RFQ#: ${rfqData.rfqNumber}`);

// // //           this.isSubmitting = false;

// // //           // Redirect to dashboard
// // //           console.log('🔄 Redirecting to buyer dashboard...');
// // //           setTimeout(() => {
// // //             this.router.navigate(['/buyer-dashboard']);
// // //           }, 1500);
// // //         },
// // //         error: (err: any) => {
// // //           this.isSubmitting = false;
// // //           console.error('❌ Step 4 Failed:', err);
// // //           const errorMsg = err.error?.message || 'Failed to send RFQ to suppliers';
// // //           this.messageService.showMessage('error', 'Error', errorMsg);
// // //         }
// // //       });
// // //   }

// // //   /**
// // //    * Helper: Get user ID from localStorage
// // //    */
// // //   private getUserIdFromLocalStorage(): number {
// // //     try {
// // //       const userStr = localStorage.getItem('currentUser');
// // //       if (userStr) {
// // //         const user = JSON.parse(userStr);
// // //         return user.id || 1;
// // //       }
// // //     } catch (error) {
// // //       console.error('Error parsing user from localStorage:', error);
// // //     }
// // //     return 1;
// // //   }

// // //   // ==================== VALIDATION HELPERS ====================

// // //   get isStep1Valid(): boolean {
// // //     return this.rfqForm.valid;
// // //   }

// // //   get isStep2Valid(): boolean {
// // //     return this.selectedSuppliers.size > 0;
// // //   }

// // //   get isStep3Valid(): boolean {
// // //     return this.isStep1Valid && this.isStep2Valid;
// // //   }

// // //   // ==================== FORM CONTROL HELPERS ====================

// // //   getControl(controlName: string) {
// // //     return this.rfqForm.get(controlName);
// // //   }

// // //   hasError(controlName: string): boolean {
// // //     const control = this.getControl(controlName);
// // //     return !!(control && control.invalid && (control.dirty || control.touched));
// // //   }

// // //   getErrorMessage(controlName: string): string {
// // //     const control = this.getControl(controlName);
// // //     if (!control || !control.errors) return '';
    
// // //     if (control.errors['required']) return `${controlName} is required`;
// // //     if (control.errors['minlength']) {
// // //       const minLength = control.errors['minlength'].requiredLength;
// // //       return `${controlName} must be at least ${minLength} characters`;
// // //     }
// // //     return 'Invalid field';
// // //   }
// // // }


// // // File: src/app/views/base/create-roq/create-roq.component.ts
// // import { Component, OnInit, OnDestroy } from '@angular/core';
// // import { CommonModule } from '@angular/common';
// // import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
// // import { Router, ActivatedRoute } from '@angular/router';
// // import { RFQService } from './requisition.service'; // Only one service
// // import { MessageService } from '../../../shared/service/message.service';
// // import { Subject } from 'rxjs';
// // import { takeUntil } from 'rxjs/operators';
// // import { FormsModule } from '@angular/forms';

// // @Component({
// //   selector: 'app-create-rfq',
// //   standalone: true,
// //   imports: [CommonModule, ReactiveFormsModule, FormsModule],
// //   templateUrl: './create-roq.component.html',
// //   styleUrls: ['./create-roq.component.css']
// // })
// // export class CreateRFQComponent implements OnInit, OnDestroy {

// //   // ==================== FORMS ====================
// //   rfqForm!: FormGroup;
  
// //   // ==================== DATA ====================
// //   requisitionId: number | null = null;
// //   requisition: any = null;
// //   suppliers: any[] = [];
// //   filteredSuppliers: any[] = [];
// //   selectedSuppliers: Map<number, {
// //     supplierId: number;
// //     departmentId: number;
// //     departmentName: string;
// //     supplierName: string;
// //   }> = new Map();

// //   // ==================== UI STATE ====================
// //   currentStep = 1;
// //   maxSteps = 3;
// //   isLoading = false;
// //   isSubmitting = false;
  
// //   // Supplier selection
// //   showSupplierSelector = false;
// //   searchSuppliersText = '';

// //   // ==================== SUBSCRIPTIONS ====================
// //   private destroy$ = new Subject<void>();

// //   constructor(
// //     private fb: FormBuilder,
// //     private rfqService: RFQService, // Only RFQService
// //     private messageService: MessageService,
// //     private router: Router,
// //     private route: ActivatedRoute
// //   ) {}

// //   ngOnInit(): void {
// //     console.log('🚀 CreateRFQComponent initialized');
// //     this.initializeForm();
// //     this.getRequisitionIdFromRoute();
// //     this.loadSuppliers();
// //   }

// //   ngOnDestroy(): void {
// //     this.destroy$.next();
// //     this.destroy$.complete();
// //   }

// //   /**
// //    * ==================== STEP 0: GET REQUISITION ID FROM URL ====================
// //    */
// //   private getRequisitionIdFromRoute(): void {
// //     console.log('📍 Getting requisition ID from route...');
    
// //     this.route.paramMap
// //       .pipe(takeUntil(this.destroy$))
// //       .subscribe(params => {
// //         const idParam = params.get('requisitionId');
        
// //         // if (!idParam) {
// //         //   console.error('❌ No requisitionId in route');
// //         //   this.messageService.showMessage('error', 'Error', 
// //         //     'No requisition ID provided. Cannot create RFQ without a requisition.');
// //         //   this.router.navigate(['/buyer-dashboard']);
// //         //   return;
// //         // }

// //         this.requisitionId = Number(idParam);
// //         console.log('✅ Requisition ID:', this.requisitionId);
// //         this.loadRequisitionData();
// //       });
// //   }

// //   /**
// //    * ==================== STEP 1: INITIALIZE EMPTY FORM ====================
// //    */
// //   private initializeForm(): void {
// //     console.log('📋 Initializing RFQ form...');
    
// //     this.rfqForm = this.fb.group({
// //       title: ['', [Validators.required, Validators.minLength(3)]],
// //       category: ['', Validators.required],
// //       description: [''],
// //       notes: [''],
// //       currency: ['USD', Validators.required],
// //       deliveryDate: ['', Validators.required],
// //       deliveryLocation: ['', [Validators.required, Validators.minLength(3)]]
// //     });
// //   }

// //   /**
// //    * ==================== STEP 2: LOAD REQUISITION DATA ====================
// //    */
// //   private loadRequisitionData(): void {
// //     if (!this.requisitionId) {
// //       console.error('❌ Cannot load requisition: ID is null');
// //       return;
// //     }

// //     this.isLoading = true;
// //     console.log(`📥 Loading requisition data for ID: ${this.requisitionId}...`);

// //     // Using RFQService to get requisition data
// //     this.rfqService.getRequisitionById(this.requisitionId)
// //       .pipe(takeUntil(this.destroy$))
// //       .subscribe({
// //         next: (response: any) => {
// //           console.log('✅ Requisition loaded:', response);
          
// //           this.requisition = response.data || response;
          
// //           if (this.requisition.status !== 'SUBMITTED') {
// //             console.warn('⚠️ Requisition is not in SUBMITTED status');
// //             this.messageService.showMessage('warning', 'Warning',
// //               'This requisition must be in SUBMITTED status to create an RFQ');
// //           }

// //           this.populateFormFromRequisition();
// //           this.isLoading = false;
// //           console.log('✅ Form pre-filled with requisition data');
// //         },
// //         error: (err: any) => {
// //           console.error('❌ Error loading requisition:', err);
// //           this.isLoading = false;
          
// //           const errorMsg = err.error?.message || 'Failed to load requisition';
// //           this.messageService.showMessage('error', 'Error', errorMsg);
          
// //           setTimeout(() => {
// //             this.router.navigate(['/buyer-dashboard']);
// //           }, 2000);
// //         }
// //       });
// //   }

// //   /**
// //    * ==================== STEP 3: PRE-FILL FORM ====================
// //    */
// //   private populateFormFromRequisition(): void {
// //     if (!this.requisition) {
// //       console.warn('⚠️ No requisition data to populate form');
// //       return;
// //     }

// //     console.log('📝 Populating form with requisition data...');

// //     this.rfqForm.patchValue({
// //       title: this.requisition.title || '',
// //       category: this.requisition.category || '',
// //       description: this.requisition.description || '',
// //       currency: this.requisition.currency || 'USD',
// //       deliveryDate: this.formatDateForInput(this.requisition.deliveryDate),
// //       deliveryLocation: this.requisition.deliveryLocation || ''
// //     });

// //     console.log('✅ Form populated with data');
// //   }

// //   /**
// //    * Helper: Format date for HTML date input
// //    */
// //   private formatDateForInput(dateString: string): string {
// //     if (!dateString) return '';
// //     try {
// //       const date = new Date(dateString);
// //       return date.toISOString().split('T')[0];
// //     } catch {
// //       return '';
// //     }
// //   }

// //   /**
// //    * ==================== STEP 4: LOAD SUPPLIERS ====================
// //    */
// //   private loadSuppliers(): void {
// //     console.log('👥 Loading suppliers from API...');
    
// //     this.rfqService.getAllSuppliers()
// //       .pipe(takeUntil(this.destroy$))
// //       .subscribe({
// //         next: (response: any) => {
// //           this.suppliers = response.data || response || [];
// //           this.filteredSuppliers = this.suppliers;
          
// //           console.log('✅ Suppliers loaded:', this.suppliers.length);

// //           if (this.suppliers.length === 0) {
// //             console.warn('⚠️ No suppliers found in database');
// //             this.messageService.showMessage('warning', 'Warning',
// //               'No suppliers available. Please add suppliers first.');
// //           }
// //         },
// //         error: (err: any) => {
// //           console.error('❌ Error loading suppliers:', err);
// //           this.messageService.showMessage('error', 'Error', 
// //             'Failed to load suppliers');
// //         }
// //       });
// //   }

// //   /**
// //    * ==================== SEARCH & SELECT SUPPLIERS ====================
// //    */
// //   searchSuppliers(): void {
// //     const term = this.searchSuppliersText.toLowerCase().trim();
    
// //     if (term) {
// //       this.filteredSuppliers = this.suppliers.filter(s =>
// //         (s.companyName || '').toLowerCase().includes(term) ||
// //         (s.industrySector || '').toLowerCase().includes(term)
// //       );
// //     } else {
// //       this.filteredSuppliers = this.suppliers;
// //     }
// //   }

// //   toggleSupplierSelector(): void {
// //     this.showSupplierSelector = !this.showSupplierSelector;
// //     if (!this.showSupplierSelector) {
// //       this.searchSuppliersText = '';
// //       this.filteredSuppliers = this.suppliers;
// //     }
// //   }

// //   getSupplierDepartments(supplierId: number): any[] {
// //     const supplier = this.suppliers.find(s => s.id === supplierId);
// //     if (!supplier || !supplier.locations) {
// //       return [];
// //     }

// //     const departments: any[] = [];
// //     supplier.locations.forEach((location: any) => {
// //       if (location.departments) {
// //         location.departments.forEach((dept: any) => {
// //           departments.push({
// //             id: dept.id,
// //             name: dept.departmentName,
// //             locationName: location.locationName
// //           });
// //         });
// //       }
// //     });
// //     return departments;
// //   }

// //   selectSupplierDepartment(supplierId: number, departmentId: number, departmentName: string): void {
// //     const supplier = this.suppliers.find(s => s.id === supplierId);
// //     if (supplier) {
// //       this.selectedSuppliers.set(supplierId, {
// //         supplierId,
// //         departmentId,
// //         departmentName,
// //         supplierName: supplier.companyName
// //       });
      
// //       console.log(`✅ Selected: ${supplier.companyName} - ${departmentName}`);
// //       this.messageService.showMessage('success', 'Success', 
// //         `${supplier.companyName} - ${departmentName} selected`);
// //     }
// //   }

// //   removeSelectedSupplier(supplierId: number): void {
// //     const supplier = this.selectedSuppliers.get(supplierId);
// //     if (supplier) {
// //       console.log(`❌ Removed: ${supplier.supplierName}`);
// //     }
// //     this.selectedSuppliers.delete(supplierId);
// //   }

// //   get selectedSuppliersArray(): any[] {
// //     return Array.from(this.selectedSuppliers.values());
// //   }

// //   /**
// //    * ==================== STEP NAVIGATION ====================
// //    */
// //   nextStep(): void {
// //     if (this.currentStep >= this.maxSteps) return;

// //     if (this.currentStep === 1) {
// //       if (this.rfqForm.invalid) {
// //         this.messageService.showMessage('warning', 'Validation Error', 
// //           'Please fill all required fields');
// //         this.rfqForm.markAllAsTouched();
// //         return;
// //       }
// //     }

// //     if (this.currentStep === 2) {
// //       if (this.selectedSuppliers.size === 0) {
// //         this.messageService.showMessage('warning', 'Validation Error', 
// //           'Please select at least one supplier');
// //         return;
// //       }
// //     }

// //     this.currentStep++;
// //   }

// //   prevStep(): void {
// //     if (this.currentStep > 1) {
// //       this.currentStep--;
// //     }
// //   }

// //   /**
// //    * ==================== CREATE RFQ WORKFLOW ====================
// //    */
// //   createRFQ(): void {
// //     console.log('\n════════════════════════════════════════');
// //     console.log('🚀 CREATE RFQ WORKFLOW STARTED');
// //     console.log('════════════════════════════════════════');

// //     if (this.rfqForm.invalid || this.selectedSuppliers.size === 0) {
// //       this.messageService.showMessage('warning', 'Validation Error', 
// //         'Please complete all required steps');
// //       return;
// //     }

// //     if (!this.requisitionId) {
// //       this.messageService.showMessage('error', 'Error', 
// //         'Requisition ID is required');
// //       return;
// //     }

// //     this.isSubmitting = true;
// //     console.log(`📝 Creating RFQ from Requisition ID: ${this.requisitionId}`);

// //     this.rfqService.createRFQFromRequisition(this.requisitionId)
// //       .pipe(takeUntil(this.destroy$))
// //       .subscribe({
// //         next: (response: any) => {
// //           const rfqId = response.data?.id || response.id;
// //           if (!rfqId) {
// //             this.messageService.showMessage('error', 'Error', 'Failed to create RFQ');
// //             this.isSubmitting = false;
// //             return;
// //           }
// //           console.log(`✅ RFQ created with ID: ${rfqId}`);
// //           this.addSuppliersToRFQ(rfqId);
// //         },
// //         error: (err: any) => {
// //           this.isSubmitting = false;
// //           console.error('❌ Error creating RFQ:', err);
// //           const errorMsg = err.error?.message || 'Failed to create RFQ';
// //           this.messageService.showMessage('error', 'Error', errorMsg);
// //         }
// //       });
// //   }

// //   private addSuppliersToRFQ(rfqId: number): void {
// //     const supplierSelections = Array.from(this.selectedSuppliers.values()).map(s => ({
// //       supplierId: s.supplierId,
// //       departmentId: s.departmentId
// //     }));

// //     this.rfqService.addSuppliersToRFQ(rfqId, supplierSelections)
// //       .pipe(takeUntil(this.destroy$))
// //       .subscribe({
// //         next: () => {
// //           console.log('✅ Suppliers added');
// //           this.submitRFQ(rfqId);
// //         },
// //         error: (err: any) => {
// //           this.isSubmitting = false;
// //           const errorMsg = err.error?.message || 'Failed to add suppliers';
// //           this.messageService.showMessage('error', 'Error', errorMsg);
// //         }
// //       });
// //   }

// //   private submitRFQ(rfqId: number): void {
// //     this.rfqService.submitRFQ(rfqId)
// //       .pipe(takeUntil(this.destroy$))
// //       .subscribe({
// //         next: () => {
// //           console.log('✅ RFQ submitted');
// //           this.sendRFQToSuppliers(rfqId);
// //         },
// //         error: (err: any) => {
// //           this.isSubmitting = false;
// //           const errorMsg = err.error?.message || 'Failed to submit RFQ';
// //           this.messageService.showMessage('error', 'Error', errorMsg);
// //         }
// //       });
// //   }

// //   private sendRFQToSuppliers(rfqId: number): void {
// //     const userId = this.getUserIdFromLocalStorage();
    
// //     this.rfqService.sendRFQToSuppliers(rfqId, userId)
// //       .pipe(takeUntil(this.destroy$))
// //       .subscribe({
// //         next: (response: any) => {
// //           const rfqData = response.data;
// //           console.log('✅ RFQ sent successfully');
          
// //           this.messageService.showMessage('success', 'Success',
// //             `RFQ created and sent to ${this.selectedSuppliers.size} suppliers! RFQ#: ${rfqData.rfqNumber}`);

// //           this.isSubmitting = false;

// //           setTimeout(() => {
// //             this.router.navigate(['/buyer-dashboard']);
// //           }, 1500);
// //         },
// //         error: (err: any) => {
// //           this.isSubmitting = false;
// //           const errorMsg = err.error?.message || 'Failed to send RFQ';
// //           this.messageService.showMessage('error', 'Error', errorMsg);
// //         }
// //       });
// //   }

// //   private getUserIdFromLocalStorage(): number {
// //     try {
// //       const userStr = localStorage.getItem('currentUser');
// //       if (userStr) {
// //         const user = JSON.parse(userStr);
// //         return user.id || 1;
// //       }
// //     } catch (error) {
// //       console.error('Error parsing user from localStorage:', error);
// //     }
// //     return 1;
// //   }

// //   // ==================== VALIDATION HELPERS ====================

// //   get isStep1Valid(): boolean {
// //     return this.rfqForm.valid;
// //   }

// //   get isStep2Valid(): boolean {
// //     return this.selectedSuppliers.size > 0;
// //   }

// //   get isStep3Valid(): boolean {
// //     return this.isStep1Valid && this.isStep2Valid;
// //   }

// //   getControl(controlName: string) {
// //     return this.rfqForm.get(controlName);
// //   }

// //   hasError(controlName: string): boolean {
// //     const control = this.getControl(controlName);
// //     return !!(control && control.invalid && (control.dirty || control.touched));
// //   }

// //   getErrorMessage(controlName: string): string {
// //     const control = this.getControl(controlName);
// //     if (!control || !control.errors) return '';
    
// //     if (control.errors['required']) return `${controlName} is required`;
// //     if (control.errors['minlength']) {
// //       const minLength = control.errors['minlength'].requiredLength;
// //       return `${controlName} must be at least ${minLength} characters`;
// //     }
// //     return 'Invalid field';
// //   }
// // }




// import { Component, OnInit } from '@angular/core';
// import { CommonModule } from '@angular/common';
// import { FormsModule } from '@angular/forms';
// import { Router } from '@angular/router';
// import { RFQService } from './requisition.service';
// import { MessageService } from '../../../shared/service/message.service';
// import { BuyerService } from '../dashboard/buyer-b.service';
// import { RFQ, RFQStatus, Priority } from './requisition.model';
// import { Buyer } from '../dashboard/buyer-b.model';

// @Component({
//   selector: 'app-rfq-list',
//   standalone: true,
//   imports: [CommonModule, FormsModule],
//   templateUrl: './rfq-list.component.html',
//   styleUrls: ['./rfq-list.component.css']
// })
// export class RFQListComponent implements OnInit {

//   // Data
//   rfqs: RFQ[] = [];
//   buyers: Buyer[] = [];
  
//   // Display & Pagination
//   displayRFQs: any[] = [];
//   pagedRFQs: any[] = [];
//   searchText = '';
//   selectedBuyer: number | null = null;
//   selectedStatus: string = '';
//   currentPage = 1;
//   pageSize = 10;
  
//   // Status & Loading
//   isLoading = false;
  
//   // Expose to template
//   Math = Math;
//   RFQStatus = RFQStatus;
//   Priority = Priority;

//   // Status options for filter
//   statusOptions = [
//     { value: '', label: 'All Statuses' },
//     { value: RFQStatus.DRAFT, label: 'Draft' },
//     { value: RFQStatus.PUBLISHED, label: 'Published' },
//     { value: RFQStatus.AWAITING_APPROVAL, label: 'Awaiting Approval' },
//     { value: RFQStatus.APPROVED, label: 'Approved' },
//     { value: RFQStatus.RESPONSES_RECEIVED, label: 'Responses Received' },
//     { value: RFQStatus.CLOSED, label: 'Closed' },
//     { value: RFQStatus.CANCELLED, label: 'Cancelled' }
//   ];

//   constructor(
//     private rfqService: RFQService,
//     private buyerService: BuyerService,
//     private messageService: MessageService,
//     private router: Router
//   ) {}

//   ngOnInit(): void {
//     this.loadBuyers();
//     this.loadRFQs();
//   }

//   // ==================== DATA LOADING ====================

//   /**
//    * Load all buyers for dropdown
//    */
//   loadBuyers(): void {
//     this.buyerService.getAllBuyers().subscribe({
//       next: (buyers: Buyer[]) => {
//         this.buyers = buyers || [];
//         console.log('Buyers loaded:', this.buyers.length);
//       },
//       error: (err) => {
//         console.error('Error loading buyers:', err);
//         this.messageService.showMessage('error', 'Error', 'Failed to load buyers');
//       }
//     });
//   }

//   /**
//    * Load all RFQs
//    */
//   loadRFQs(): void {
//     this.isLoading = true;
//     console.log('%c[LOAD RFQs]', 'color: #0066cc; font-weight: bold;');
    
//     this.rfqService.getAllRFQs().subscribe({
//       next: (rfqs: RFQ[]) => {
//         console.log('%c[✓ RFQs Loaded]', 'color: #00aa00;', `Total: ${rfqs?.length}`);
//         this.rfqs = rfqs || [];
//         this.applyFilters();
//         this.isLoading = false;
//       },
//       error: (err) => {
//         console.error('Error loading RFQs:', err);
//         this.messageService.showMessage('error', 'Error', 'Failed to load RFQs');
//         this.isLoading = false;
//       }
//     });
//   }

//   // ==================== FILTERING & SEARCH ====================

//   /**
//    * Apply search and filter
//    */
//   applyFilters(): void {
//     let filtered = [...this.rfqs];

//     // Filter by buyer
//     if (this.selectedBuyer) {
//       filtered = filtered.filter(rfq => rfq.buyerId === this.selectedBuyer);
//     }

//     // Filter by status
//     if (this.selectedStatus) {
//       filtered = filtered.filter(rfq => rfq.status === this.selectedStatus);
//     }

//     // Search by title, number, or description
//     const term = this.searchText.toLowerCase().trim();
//     if (term) {
//       filtered = filtered.filter(rfq =>
//         (rfq.rfqTitle || '').toLowerCase().includes(term) ||
//         (rfq.rfqNumber || '').toLowerCase().includes(term) ||
//         (rfq.rfqDescription || '').toLowerCase().includes(term)
//       );
//     }

//     this.displayRFQs = filtered;
//     this.currentPage = 1;
//     this.applyPagination();
//   }

//   /**
//    * Apply pagination
//    */
//   applyPagination(): void {
//     const start = (this.currentPage - 1) * this.pageSize;
//     this.pagedRFQs = this.displayRFQs.slice(start, start + this.pageSize);
//   }

//   /**
//    * Search handler
//    */
//   onSearch(): void {
//     this.applyFilters();
//   }

//   /**
//    * Buyer filter change
//    */
//   onBuyerChange(): void {
//     this.applyFilters();
//   }

//   /**
//    * Status filter change
//    */
//   onStatusChange(): void {
//     this.applyFilters();
//   }

//   // ==================== ACTIONS ====================

//   /**
//    * Create new RFQ
//    */
//   createRFQ(): void {
//     this.router.navigate(['/rfq/create']);
//   }

//   /**
//    * View RFQ details
//    */
//   viewRFQ(rfq: RFQ): void {
//     if (rfq.id) {
//       this.router.navigate([`/rfq/view/${rfq.id}`]);
//     }
//   }

//   /**
//    * Edit RFQ (only DRAFT)
//    */
//   editRFQ(rfq: RFQ): void {
//     if (rfq.status !== RFQStatus.DRAFT) {
//       this.messageService.showMessage('warning', 'Warning', 'Only DRAFT RFQs can be edited');
//       return;
//     }
//     if (rfq.id) {
//       this.router.navigate([`/rfq/edit/${rfq.id}`]);
//     }
//   }

//   /**
//    * Publish RFQ
//    */
//   publishRFQ(rfq: RFQ): void {
//     if (!rfq.id) return;

//     const confirm = window.confirm(`Publish RFQ "${rfq.rfqTitle}"?\n\nThis will send it to selected suppliers.`);
//     if (!confirm) return;

//     this.rfqService.publishRFQ(rfq.id).subscribe({
//       next: () => {
//         this.messageService.showMessage('success', 'Success', 'RFQ published successfully');
//         this.loadRFQs();
//       },
//       error: (err) => {
//         this.messageService.showMessage('error', 'Error', err.error?.message || 'Failed to publish RFQ');
//       }
//     });
//   }

//   /**
//    * Close RFQ
//    */
//   closeRFQ(rfq: RFQ): void {
//     if (!rfq.id) return;

//     const confirm = window.confirm(`Close RFQ "${rfq.rfqTitle}"?\n\nNo further responses will be accepted.`);
//     if (!confirm) return;

//     this.rfqService.closeRFQ(rfq.id).subscribe({
//       next: () => {
//         this.messageService.showMessage('success', 'Success', 'RFQ closed successfully');
//         this.loadRFQs();
//       },
//       error: (err) => {
//         this.messageService.showMessage('error', 'Error', err.error?.message || 'Failed to close RFQ');
//       }
//     });
//   }

//   /**
//    * Delete RFQ (only DRAFT)
//    */
//   deleteRFQ(rfq: RFQ): void {
//     if (rfq.status !== RFQStatus.DRAFT) {
//       this.messageService.showMessage('warning', 'Warning', 'Only DRAFT RFQs can be deleted');
//       return;
//     }
//     if (!rfq.id) return;

//     const confirm = window.confirm(`Delete RFQ "${rfq.rfqTitle}"?\n\nThis action cannot be undone.`);
//     if (!confirm) return;

//     this.rfqService.deleteRFQ(rfq.id).subscribe({
//       next: () => {
//         this.messageService.showMessage('success', 'Success', 'RFQ deleted successfully');
//         this.loadRFQs();
//       },
//       error: (err) => {
//         this.messageService.showMessage('error', 'Error', err.error?.message || 'Failed to delete RFQ');
//       }
//     });
//   }

//   // ==================== HELPER METHODS ====================

//   /**
//    * Get buyer name by ID
//    */
//   getBuyerName(buyerId: number | undefined): string {
//     if (!buyerId) return 'N/A';
//     const buyer = this.buyers.find(b => b.id === buyerId);
//     return buyer?.companyName || 'N/A';
//   }

//   /**
//    * Get status badge class
//    */
//   getStatusBadgeClass(status: RFQStatus | string | undefined): string {
//     return this.rfqService.getStatusBadgeClass(status || '');
//   }

//   /**
//    * Get priority badge class
//    */
//   getPriorityBadgeClass(priority: Priority | string | undefined): string {
//     return this.rfqService.getPriorityBadgeClass(priority || '');
//   }

//   /**
//    * Check if RFQ can be edited
//    */
//   canEdit(rfq: RFQ): boolean {
//     return rfq.status === RFQStatus.DRAFT;
//   }

//   /**
//    * Check if RFQ can be deleted
//    */
//   canDelete(rfq: RFQ): boolean {
//     return rfq.status === RFQStatus.DRAFT;
//   }

//   /**
//    * Check if RFQ can be published
//    */
//   canPublish(rfq: RFQ): boolean {
//     return rfq.status === RFQStatus.DRAFT;
//   }

//   /**
//    * Get total pages
//    */
//   get totalPages(): number {
//     return Math.ceil(this.displayRFQs.length / this.pageSize);
//   }

//   /**
//    * Pagination handler
//    */
//   goToPage(page: number): void {
//     if (page >= 1 && page <= this.totalPages) {
//       this.currentPage = page;
//       this.applyPagination();
//     }
//   }

//   /**
//    * Page size change
//    */
//   onPageSizeChange(): void {
//     this.currentPage = 1;
//     this.applyPagination();
//   }

//   /**
//    * Format date
//    */
//   formatDate(date: string | undefined): string {
//     if (!date) return 'N/A';
//     return new Date(date).toLocaleDateString();
//   }
// }