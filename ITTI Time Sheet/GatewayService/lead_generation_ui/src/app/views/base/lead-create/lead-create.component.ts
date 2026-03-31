import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { TEAMS } from '../dashboard/teams.model';
import { CardModule, FormModule, GridModule, ButtonModule, SpinnerModule } from '@coreui/angular';
import { LeadService } from '../dashboard/lead.service';
import { MessageService } from '../../../shared/service/message.service';
import { Router } from '@angular/router';
import { DownloadExcelComponent } from '../../../shared/download-excel/download-excel.component';

@Component({
  selector: 'app-lead-create',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    CardModule,
    FormModule,
    GridModule,
    ButtonModule,
    SpinnerModule,
    DownloadExcelComponent
  ],
  templateUrl: './lead-create.component.html',
  styleUrls: ['./lead-create.component.css']
})
export class LeadCreateComponent implements OnInit {

  @Input() mode: 'create' | 'edit' | 'view' = 'create';
  @Input() leadData: any = null;

  leadForm!: FormGroup;
  formSubmitted = false;
  isSubmitting = false;

  // API message variables
  apiMessage: string = '';
  apiMessageType: 'success' | 'error' | '' = '';
  isLoadingTestApi = false;

  teams = TEAMS;
  filteredAssignedToList: any[] = [];

  user: any;

  technologyOptions: string[] = [
    'Business Intelligence', 'Cloud Services', 'Professional Services', 'App Development',
    'ITeS', 'Management Consultancy', 'MOBILE APP', 'MS Dynamics - F & O',
    'MS DYNAMICS - BC', 'OPEN SYSTEM', 'QAD-ERP', 'SAP-ERP', 'Other' 
  ];
  requirementOptions: string[] = [
    'Implementation', 'Project Services / Fixed Cost', 'AMC',
    'Managed services', 'Staff Augmentation', 'License', 'BREP'
  ];

  constructor(
    private fb: FormBuilder,
    private leadService: LeadService,
    private messageService: MessageService,
    private router: Router
  ) {
    this.leadForm = this.fb.group({
        companyName: ['', Validators.required],
        firstName: ['', Validators.required],
        middleName: [''],
        lastName: ['', Validators.required], // FIX: Added required validator
        jobTitle: [''],
        email: ['', [Validators.required, Validators.email]],
        email1: [''],
        phone: [''],
        phone1: [''],
        mobile: [''],
        website: [''],
        address: [''],
        countryRegion: [''],
        source: ['', Validators.required],
        otherSource: [''],
        stage: ['', Validators.required],
        contactType: [''], 
        technology: [''], 
        otherTechnology: [''], 
        requirementType: [''],
        domain: [''],
        technicalSkills: [''],
        assignedTo: ['', Validators.required],
        remarks: [''], 
        newRemark: [''], 
        expectedValue: [''],
        expectedCurrencyValue: [''],
        expectedCloseDate: [''],
        probability: [null, [Validators.required, Validators.min(0), Validators.max(100)]],
    });
  }

  get currencyRequired() {
    return this.leadForm.get('expectedValue')?.value > 0;
  }

  ngOnInit() {
    const savedState = history.state;
    if (savedState?.mode) this.mode = savedState.mode;
    if (savedState?.lead) this.leadData = savedState.lead;

    this.user = this.getLoggedInUser();
    this.applyAssignedToFilter();

    // Fetch test API message on component load
    this.fetchTestApiMessage();

    if (this.mode === 'edit' || this.mode === 'view') {
      this.patchLeadData();
      this.applyFieldPermissions(); 
    }

    if (this.mode === 'view') {
      this.leadForm.disable();
    }
    
    this.setupExpectedCurrencyValueValidation();
    this.setupStageValidation();
    this.setupSourceValidation(); // FIX: Added source validation setup
  }

  // Fetch test API message
  fetchTestApiMessage() {
    this.isLoadingTestApi = true;
    this.leadService.getTestApi().subscribe({
      next: (res: any) => {
        // Handle different response formats
        if (typeof res === 'string') {
          this.apiMessage = res;
        } else if (res?.message) {
          this.apiMessage = res.message;
        } else {
          this.apiMessage = JSON.stringify(res);
        }
        this.apiMessageType = 'success';
        this.isLoadingTestApi = false;
      },
      error: (err: any) => {
        this.apiMessage = err?.error?.message || "Error: API is not reachable.";
        this.apiMessageType = 'error';
        this.isLoadingTestApi = false;
      }
    });
  }

  // Manual refresh button handler
  refreshTestApiMessage() {
    this.apiMessage = '';
    this.apiMessageType = '';
    this.fetchTestApiMessage();
  }

  // Close/dismiss the API message
  closeApiMessage() {
    this.apiMessage = '';
    this.apiMessageType = '';
  }

  // FIX: Setup reactive validation for source field
  setupSourceValidation() {
    const sourceControl = this.leadForm.get('source');
    const otherSourceControl = this.leadForm.get('otherSource');

    sourceControl?.valueChanges.subscribe(val => {
      if (val === 'Other') {
        otherSourceControl?.setValidators([Validators.required]);
      } else {
        otherSourceControl?.clearValidators();
        otherSourceControl?.setValue('');
      }
      otherSourceControl?.updateValueAndValidity();
    });
  }

  setupStageValidation() {
    const stageControl = this.leadForm.get('stage');
    const contactTypeControl = this.leadForm.get('contactType');

    stageControl?.valueChanges.subscribe(val => {
      if (val === 'Contact') {
        contactTypeControl?.setValidators([Validators.required]);
      } else {
        contactTypeControl?.clearValidators();
      }
      contactTypeControl?.updateValueAndValidity();
    });
  }

  applyFieldPermissions() {
    if (this.mode !== 'edit' || !this.leadData) return;

    // 1. GLOBAL LOCK: If Lead is Closed, it is READ-ONLY for EVERYONE.
    const currentStage = this.leadData.stage;
    if (currentStage === 'Closed Won' || currentStage === 'Closed Lost') {
      this.leadForm.disable(); 
      return;
    }

    const currentUserCode = (this.user.employeeCode || '').toString();
    const leadOwner = (this.leadData.employeeCode || '').toString();
    
    // 2. Handling Legacy Data: If no owner recorded, allow editing (except history)
    if (!leadOwner || leadOwner === 'null' || leadOwner === 'undefined') {
      this.leadForm.enable(); 
      this.leadForm.get('remarks')?.disable();
      return; 
    }

    // 3. OWNER LOGIC: Full access (except history)
    if (currentUserCode === leadOwner) {
       this.leadForm.enable();
       this.leadForm.get('remarks')?.disable();
       return; 
    }

    // 4. ASSIGNEE LOGIC (Fill-Once)
    const coreFields = [
      'companyName', 'firstName', 'middleName', 'lastName', 'jobTitle',
      'email', 'email1', 'phone', 'phone1', 'mobile',
      'website', 'countryRegion', 'address',
      'source', 'otherSource', 
      'technology', 'otherTechnology',
      'requirementType', 'domain', 'technicalSkills'
    ];

    const mutableFields = [
      'stage', 'remarks', 'probability', 
      'expectedCloseDate', 'expectedValue', 'expectedCurrencyValue', 
      'contactType', 'assignedTo',
      'newRemark'
    ];

    coreFields.forEach(field => {
      const existingValue = this.leadData[field];
      const control = this.leadForm.get(field);

      if (existingValue && existingValue.toString().trim() !== '') {
        control?.disable();
      } else {
        control?.enable();
      }
    });

    mutableFields.forEach(field => {
      this.leadForm.get(field)?.enable();
    });

    this.leadForm.get('remarks')?.disable();
  }

  setupExpectedCurrencyValueValidation() {
    const amount = this.leadForm.get('expectedValue');
    const currency = this.leadForm.get('expectedCurrencyValue');

    amount?.valueChanges.subscribe(val => {
      if (val && val > 0) {
        currency?.setValidators([Validators.required]);
      } else {
        currency?.clearValidators();
        if (!this.leadData) currency?.setValue('');
      }
      currency?.updateValueAndValidity({ emitEvent: false });
    });

    currency?.valueChanges.subscribe(val => {
      if (val) {
        amount?.setValidators([Validators.required, Validators.min(1)]);
      } else {
        amount?.clearValidators();
      }
      amount?.updateValueAndValidity({ emitEvent: false });
    });
  }

  f(controlName: string) {
    return this.leadForm.get(controlName);
  }

  getLoggedInUser() {
    return JSON.parse(localStorage.getItem('signinData') || '{}');
  }

  patchLeadData() {
    if (!this.leadData) return;
    
    let techValue = this.leadData.technology;
    let otherTechValue = '';

    if (techValue && !this.technologyOptions.includes(techValue)) {
        otherTechValue = techValue;
        techValue = 'Other';
    }

    this.leadForm.patchValue({
      companyName: this.leadData.companyName,
      firstName: this.leadData.firstName,
      middleName: this.leadData.middleName,
      lastName: this.leadData.lastName,
      address: this.leadData.address,
      jobTitle: this.leadData.jobTitle,
      email: this.leadData.email,
      email1: this.leadData.email1,
      phone: this.leadData.phone,
      phone1: this.leadData.phone1,
      mobile: this.leadData.mobile,
      website: this.leadData.website,
      countryRegion: this.leadData.countryRegion,
      source: this.leadData.source,
      stage: this.leadData.stage,
      contactType: this.leadData.contactType,
      requirementType: this.leadData.requirementType,
      technology: techValue, 
      otherTechnology: otherTechValue, 
      domain: this.leadData.domain,
      technicalSkills: this.leadData.technicalSkills,
      assignedTo: this.leadData.assignedTo,
      remarks: this.leadData.remarks,
      probability: this.leadData.probability,
      expectedCloseDate: this.leadData.expectedCloseDate,
      expectedValue: this.leadData.expectedValue,
      expectedCurrencyValue: this.leadData.expectedCurrencyValue,
      otherSource: this.leadData.otherSource
    });
  }

  applyAssignedToFilter() {
    this.filteredAssignedToList = this.teams;
  }

  submitLead() {
    this.formSubmitted = true;
    if (this.leadForm.invalid) {
      this.leadForm.markAllAsTouched();
      return;
    }

    this.isSubmitting = true;
    const formVal = this.leadForm.getRawValue(); 
    
    let finalTechnology = formVal.technology;
    if (formVal.technology === 'Other') {
        finalTechnology = formVal.otherTechnology;
    }

    // Append New Remark with Timestamp & Name
    let updatedRemarks = formVal.remarks || '';
    const newNote = formVal.newRemark;

    if (newNote && newNote.trim() !== '') {
        const timestamp = new Date().toLocaleString('en-GB', { 
          day: 'numeric', 
          month: 'short', 
          hour: '2-digit', 
          minute: '2-digit' 
        });
        const userName = this.user.firstName || this.user.name || this.user.employeeName || 'User';
        
        const formattedEntry = `[${timestamp}] ${userName}: ${newNote}`;
        updatedRemarks = updatedRemarks ? `${updatedRemarks}\n${formattedEntry}` : formattedEntry;
    }
    
    const payload = {
      ...formVal,
      technology: finalTechnology,      
      contactPerson: `${formVal.firstName} ${formVal.lastName}`.trim(), 
      updatedOn: new Date().toISOString(),
      updatedBy: this.user.employeeCode,
      employeeCode: this.leadData?.employeeCode ? this.leadData.employeeCode : this.user.employeeCode,
      remarks: updatedRemarks 
    };

    delete payload.otherTechnology;
    delete payload.newRemark;

    if (this.mode === 'create') {
      this.createLead(payload);
    } else if (this.mode === 'edit') {
      this.updateLead(payload);
    }
  }

  createLead(payload: any) {
    payload.createdOn = new Date().toISOString();
    payload.createdBy = { id: this.user.employeeCode };

    this.leadService.saveLead(payload).subscribe({
      next: () => {
        this.messageService.showMessage('success', 'Success', 'Lead Created Successfully');
        this.isSubmitting = false;
        this.leadForm.reset();
        this.formSubmitted = false;
      },
      error: (err: any) => {
        this.messageService.showMessage('error', 'Error', err?.error?.message || 'Failed to create lead');
        this.isSubmitting = false;
      }
    });
  }

  updateLead(payload: any) {
    this.leadService.updateLead(this.leadData.id, payload).subscribe({
      next: () => {
        this.messageService.showMessage('success', 'Success', 'Lead Updated Successfully');
        this.isSubmitting = false;
      },
      error: (err: any) => {
        this.messageService.showMessage('error', 'Error', err?.error?.message || 'Failed to update lead');
        this.isSubmitting = false;
      }
    });
  }

  closeView() {
    window.history.back();
  }
  
  cancel() {
    this.router.navigate(['/dashboard']);
  }
}