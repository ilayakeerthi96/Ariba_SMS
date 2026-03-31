// // import { Component, OnInit } from '@angular/core';
// // import { CommonModule } from '@angular/common';
// // import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
// // import { Router } from '@angular/router';
// // import { CardModule, GridModule } from '@coreui/angular';

// // @Component({
// //   selector: 'app-create-supplier',   // FIXED
// //   standalone: true,
// //   imports: [
// //     CommonModule,
// //     ReactiveFormsModule,
// //     CardModule,
// //     GridModule
// //   ],
// //   templateUrl: './create-supplier.component.html',
// //   styleUrls: ['./create-supplier.component.css']
// // })
// // export class CreateSupplierComponent implements OnInit {

// //   supplierForm!: FormGroup;

// //   constructor(
// //     private fb: FormBuilder,
// //     private router: Router
// //   ) {}

// //   ngOnInit(): void {
// //     this.supplierForm = this.fb.group({
// //       companyName: ['', Validators.required],
// //       companyAddress: ['', Validators.required],
// //       name: ['', Validators.required],
// //       email: ['', [Validators.required, Validators.email]],
// //       password: ['']
// //     });
// //   }

// //   onSubmit() {
// //     if (this.supplierForm.invalid) {
// //       this.supplierForm.markAllAsTouched();
// //       return;
// //     }

// //     console.log("Supplier Form Data:", this.supplierForm.value);

// //     // TODO: API service can be added here
// //     alert("Supplier created successfully!");
// //   }
// // }

// import { Component, OnInit, Input } from '@angular/core';
// import { CommonModule } from '@angular/common';
// import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
// import { Router } from '@angular/router';
// import { CardModule, GridModule, ButtonModule } from '@coreui/angular';
// import { SupplierService } from '../dashboard/supplier.service';
// import { MessageService } from '../../../shared/service/message.service';

// @Component({
//   selector: 'app-create-supplier',
//   standalone: true,
//   imports: [
//     CommonModule,
//     ReactiveFormsModule,
//     CardModule,
//     GridModule,
//     ButtonModule
//   ],
//   templateUrl: './create-supplier.component.html',
//   styleUrls: ['./create-supplier.component.css']
// })
// export class CreateSupplierComponent implements OnInit {

//   @Input() mode: 'create' | 'edit' = 'create';
//   @Input() supplierData: any = null;

//   supplierForm!: FormGroup;
//   isSubmitting = false;
//   formSubmitted = false;
// showPassword = false;

//   constructor(
//     private fb: FormBuilder,
//     private supplierService:SupplierService,
//     private messageService: MessageService,
//     private router: Router
//   ) {}

//   ngOnInit(): void {

//     const savedState = history.state;
//     if (savedState?.mode) this.mode = savedState.mode;
//     if (savedState?.supplier) this.supplierData = savedState.supplier;

//     this.supplierForm = this.fb.group({
//       companyName: ['', Validators.required],
//       companyAddress: ['', Validators.required],
//       name: ['', Validators.required],
//       email: ['', [Validators.required, Validators.email]],
//       password: ['']     // leave empty for edit mode
//     });

//     if (this.mode === 'edit') {
//       this.patchSupplierData();
//     }
//   }

//   patchSupplierData() {
//     this.supplierForm.patchValue({
//       companyName: this.supplierData.companyName,
//       companyAddress: this.supplierData.companyAddress,
//       name: this.supplierData.name,
//       email: this.supplierData.email
//     });

//     // password remains empty
//   }

//   onSubmit(): void {
//     this.formSubmitted = true;
//     if (this.supplierForm.invalid) {
//       this.supplierForm.markAllAsTouched();
//       return;
//     }

//     this.isSubmitting = true;

//     const payload = {
//       ...this.supplierForm.value,
//       password: this.supplierForm.value.password || ''  // backend handles empty!!
//     };

//     if (this.mode === 'create') {
//       this.createSupplier(payload);
//     } else {
//       this.updateSupplier(payload);
//     }
//   }

// togglePassword() {
//   this.showPassword = !this.showPassword;
// }

//   createSupplier(payload: any) {
//     this.supplierService.createSupplier(payload).subscribe({
//       next: () => {
//         this.messageService.showMessage('success', 'Success', 'Supplier Created Successfully');
//         this.isSubmitting = false;
//         this.router.navigate(['/dashboard']);
//       },
//       error: (err) => {
//         this.messageService.showMessage('error', 'Error', err.error || 'Failed to create buyer');
//         this.isSubmitting = false;
//       }
//     });
//   }

//   updateSupplier(payload: any) {
//     this.supplierService.updateSupplier(this.supplierData.id, payload).subscribe({
//       next: () => {
//         this.messageService.showMessage('success', 'Success', 'Supplier Updated Successfully');
//         this.isSubmitting = false;
//         this.router.navigate(['/dashboard']);
//       },
//       error: (err) => {
//         this.messageService.showMessage('error', 'Error', err.error || 'Failed to update buyer');
//         this.isSubmitting = false;
//       }
//     });
//   }

//   cancel() {
//     this.router.navigate(['/create-supplier']);
//   }
// }
