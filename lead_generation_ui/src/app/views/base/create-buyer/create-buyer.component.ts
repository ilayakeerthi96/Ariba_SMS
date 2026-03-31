// // import { Component, OnInit } from '@angular/core';
// // import { CommonModule } from '@angular/common';
// // import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
// // import { Router } from '@angular/router';
// // import { CardModule, GridModule } from '@coreui/angular';

// // @Component({
// //   selector: 'app-create-buyer',
// //   standalone: true,
// //   imports: [
// //     CommonModule,
// //     ReactiveFormsModule,
// //     CardModule,
// //     GridModule
// //   ],
// //   templateUrl: './create-buyer.component.html',
// //   styleUrls: ['./create-buyer.component.css']
// // })
// // export class CreateBuyerComponent implements OnInit {

// //   buyerForm!: FormGroup;

// //   constructor(
// //     private fb: FormBuilder,
// //     private router: Router
// //   ) {}

// //   ngOnInit(): void {
// //     this.buyerForm = this.fb.group({
// //       companyName: ['', Validators.required],
// //       companyAddress: ['', Validators.required],
// //       name: ['', Validators.required],
// //       email: ['', [Validators.required, Validators.email]],
// //       password: ['']
// //     });
// //   }

// //   // Submit Method
// //   onSubmit(): void {
// //     if (this.buyerForm.invalid) {
// //       this.buyerForm.markAllAsTouched();
// //       return;
// //     }

// //     const payload = this.buyerForm.value;
// //     console.log("Buyer Form Data:", payload);

// //     // TODO: integrate with API
// //     // this.buyerService.createBuyer(payload).subscribe({
// //     //   next: (res) => {
// //     //     alert("Buyer created successfully!");
// //     //     this.router.navigate(['/buyers']);
// //     //   },
// //     //   error: (err) => console.error(err)
// //     // });

// //     alert("Buyer created successfully!");
// //   }
// // }



// import { Component, OnInit, Input } from '@angular/core';
// import { CommonModule } from '@angular/common';
// import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
// import { Router,ActivatedRoute } from '@angular/router';
// import { CardModule, GridModule, ButtonModule } from '@coreui/angular';
// import { BuyerService } from '../dashboard/buyer.service';
// import { MessageService } from '../../../shared/service/message.service';

// @Component({
//   selector: 'app-create-buyer',
//   standalone: true,
//   imports: [
//     CommonModule,
//     ReactiveFormsModule,
//     CardModule,
//     GridModule,
//     ButtonModule
//   ],
//   templateUrl: './create-buyer.component.html',
//   styleUrls: ['./create-buyer.component.css']
// })
// export class CreateBuyerComponent implements OnInit {
//   buyerForm!: FormGroup;
//   isSubmitting = false;
//   showPassword = false;
//   mode: 'create' | 'edit' = 'create';
//   buyerId: number | null = null;

//   constructor(
//     private fb: FormBuilder,
//     private buyerService: BuyerService,
//     private messageService: MessageService,
//     private router: Router,
//     private route: ActivatedRoute
//   ) {}

//   ngOnInit(): void {
//     this.buyerForm = this.fb.group({
//       companyName: ['', Validators.required],
//       companyAddress: ['', Validators.required],
//       name: ['', Validators.required],
//       email: ['', [Validators.required, Validators.email]],
//       password: ['']
//     });

//     this.route.paramMap.subscribe(params => {
//       const id = params.get('id');
//       if (id) {
//         this.mode = 'edit';
//         this.buyerId = +id;
//         this.loadBuyerData(this.buyerId);
//       }
//     });
//   }

//   loadBuyerData(id: number) {
//     this.buyerService.getBuyerById(id).subscribe({
//       next: (buyer: any) => {
//         this.buyerForm.patchValue({
//           companyName: buyer.companyName,
//           companyAddress: buyer.companyAddress,
//           name: buyer.name,
//           email: buyer.email
//         });
//       },
//       error: (err) => {
//         this.messageService.showMessage('error', 'Error', 'Failed to load buyer data');
//       }
//     });
//   }

//     togglePassword() {
//     this.showPassword = !this.showPassword;
//   }

//   onSubmit(): void {
//     if (this.buyerForm.invalid) {
//       this.buyerForm.markAllAsTouched();
//       return;
//     }

//     const payload = { ...this.buyerForm.value };

//      if (this.mode === 'edit' && (!payload.password || payload.password.trim() === '')) {
//     delete payload.password;
//   }

//     this.isSubmitting = true;

//     if (this.mode === 'create') {
//       this.buyerService.createBuyer(payload).subscribe({
//         next: () => {
//           this.messageService.showMessage('success', 'Success', 'Buyer Created Successfully');
//           this.router.navigate(['/dashboard']);
//         },
//         error: (err) => {
//           this.messageService.showMessage('error', 'Error', err.error || 'Failed to create buyer');
//           this.isSubmitting = false;
//         }
//       });
//     } else {
//       this.buyerService.updateBuyer(this.buyerId!, payload).subscribe({
//         next: () => {
//           this.messageService.showMessage('success', 'Success', 'Buyer Updated Successfully');
//           this.router.navigate(['/dashboard']);
//         },
//         error: (err) => {
//           this.messageService.showMessage('error', 'Error', err.error || 'Failed to update buyer');
//           this.isSubmitting = false;
//         }
//       });
//     }
//   }
//     cancel() {
//     this.router.navigate(['/dashboard']);
//   }
// }

