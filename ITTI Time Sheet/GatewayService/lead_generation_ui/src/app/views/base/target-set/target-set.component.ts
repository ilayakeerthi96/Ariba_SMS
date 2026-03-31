import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormArray, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
// ⭐ FIX: Import Router here
import { Router } from '@angular/router'; 

import { TEAMS } from '../dashboard/teams.model';
import { LeadService } from '../dashboard/lead.service';
import { MessageService } from '../../../shared/service/message.service';

@Component({
  selector: 'app-target-set',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './target-set.component.html'
})
export class TargetSetComponent implements OnInit {

  user: any;
  teams = TEAMS;
  filteredTeams: any[] = [];

  targetForm!: FormGroup;
  formSubmitted = false;
  isSaving = false;

  allTargets: any[] = [];  // store original API data
  years: number[] = [];
  
  uniqueEmployees: any[] = [];

  // Track deleted IDs
  deletedTargetIds: number[] = []; 

  constructor(
    private fb: FormBuilder,
    private leadService: LeadService,
    private messageService: MessageService,
    private router: Router // ⭐ Inject Router
  ) {}

  ngOnInit() {
    this.user = this.getLoggedInUser();

    // ⭐ SECURITY CHECK: If user is Vaibhaw (20303), kick them out!
    if (this.user.employeeCode === '20303') {
       this.router.navigate(['/dashboard']);
       return; // Stop execution
    }

    this.filterTeamsBasedOnRole();
    this.buildForm();
    this.generateYears();
    
    // 1. Identify unique employees first
    this.uniqueEmployees = this.getUniqueEmployeesFromTeams();
    
    // 2. Load data
    this.loadExistingTargets(); 
  }

  loadExistingTargets() {
    this.leadService.getTargets().subscribe({
      next: (data: any[]) => {
        this.allTargets = data;        
        this.updateTargetTable();      
      }
    });
  }

  getUniqueEmployeesFromTeams() {
    const map = new Map<string, any>();

    TEAMS.forEach(team => {
      team.members.forEach((m: any) => {
        if (!map.has(m.employeeCode)) {
          map.set(m.employeeCode, {
            employeeCode: m.employeeCode,
            employeeName: m.name,
            designation: m.designation,
            teamName: team.teamName
          });
        }
      });
    });

    return Array.from(map.values());
  }

  updateTargetTable() {
    this.targets.clear();
    this.deletedTargetIds = []; 

    const filteredDBTargets = this.applyTargetFilters(this.allTargets);

    this.uniqueEmployees.forEach(emp => {
      
      const empTargets = filteredDBTargets.filter(t => t.employeeCode === emp.employeeCode);

      if (empTargets.length > 0) {
        empTargets.forEach(existing => {
          this.targets.push(this.createTargetRow(emp, existing));
        });
      } else {
        this.targets.push(this.createTargetRow(emp, null));
      }
    });
  }

  createTargetRow(emp: any, existing: any = null): FormGroup {
    
    const unit = existing?.targetUnit || 'AMOUNT';
    const isLeads = unit === 'LEADS';

    return this.fb.group({
      employeeCode: [emp.employeeCode],
      employeeName: [emp.employeeName],
      teamName: [emp.teamName],

      targetUnit: [unit, Validators.required],

      currency: [
        { value: existing?.currency || 'INR', disabled: isLeads },
        isLeads ? [] : Validators.required
      ],

      targetAmount: [
        existing?.targetAmount || '',
        [Validators.min(0)]
      ],

      targetId: [existing?.id || null]
    });
  }

  addTargetRow(index: number) {
    const currentRow = this.targets.at(index).value;
    
    const newRow = this.createTargetRow({
      employeeCode: currentRow.employeeCode,
      employeeName: currentRow.employeeName,
      teamName: currentRow.teamName
    }, null);

    this.targets.insert(index + 1, newRow);
  }

  removeTargetRow(index: number) {
    const rowToRemove = this.targets.at(index).value;

    if (rowToRemove.targetId) {
      this.deletedTargetIds.push(rowToRemove.targetId);
    }

    const empRows = this.targets.controls.filter(c => c.value.employeeCode === rowToRemove.employeeCode);
    
    if (empRows.length === 1) {
       this.messageService.showMessage('warning', 'Warning', 'Employee must have at least one row.');
       return; 
    }

    this.targets.removeAt(index);
  }

  onUnitChange(index: number) {
    const row = this.targets.at(index);
    const unit = row.get('targetUnit')?.value;
    const currencyControl = row.get('currency');

    if (unit === 'LEADS') {
      currencyControl?.setValue(null);
      currencyControl?.clearValidators();
      currencyControl?.disable();
    } else {
      currencyControl?.enable();
      currencyControl?.setValidators(Validators.required);
      if (!currencyControl?.value) {
        currencyControl?.setValue('INR'); 
      }
    }
    currencyControl?.updateValueAndValidity();
  }


  applyTargetFilters(allTargets: any[]) {
    const type = this.targetForm.get('targetType')?.value;
    const selectedYear = this.targetForm.get('year')?.value;

    let filtered = allTargets;

    if (type === 'ANNUAL') {
      filtered = filtered.filter(t => t.targetType === 'ANNUAL'); 
      if (selectedYear) {
        filtered = filtered.filter(t => {
          const sd = new Date(t.startDate);
          return sd.getFullYear() === Number(selectedYear);
        });
      }
    }

    return filtered;
  }

  getLoggedInUser() {
    return JSON.parse(localStorage.getItem("signinData") || '{}');
  }

  filterTeamsBasedOnRole() {
    if (this.leadService.isMgmtUserPractice()) {
      this.filteredTeams = this.teams;   
      return;
    }

    if (this.leadService.isSalesManager()) {
      const userCode = this.user.code?.toLowerCase();
      this.filteredTeams = this.teams.filter(team => {
        const manager = team.manager;
        if (typeof manager === 'string') {
          return manager.toLowerCase() === userCode;
        }
        if (Array.isArray(manager)) {
          return manager.some(m => m.toLowerCase() === userCode);
        }
        return false;
      });
      return;
    }
    this.filteredTeams = [];
  }

  buildForm() {
    const now = new Date();
    const currentMonth = now.toISOString().slice(0, 7);
    const currentYear = now.getFullYear();

    this.targetForm = this.fb.group({
      targetType: ['ANNUAL', Validators.required], 
      month: [currentMonth],   
      year: [currentYear, Validators.required],
      startDate: [''],
      endDate: [''],
      targets: this.fb.array([])
    });
  }

  generateYears() {
    const current = new Date().getFullYear();
    for (let i = current - 5; i <= current + 5; i++) {
      this.years.push(i);
    }
  }

  onTargetTypeChange() {
    const type = this.targetForm.get('targetType')?.value;

    this.targetForm.get('month')?.clearValidators();
    this.targetForm.get('year')?.clearValidators();
    this.targetForm.get('startDate')?.clearValidators();
    this.targetForm.get('endDate')?.clearValidators();

    if (type === 'ANNUAL') {
      this.targetForm.get('year')?.setValidators([Validators.required]);
      const selectedYear = this.targetForm.get('year')?.value;
      if (!selectedYear) {
        const currentYear = new Date().getFullYear();
        this.targetForm.patchValue({ year: currentYear });
      }
    }

    if (type === 'CUSTOM') {
      this.targetForm.get('startDate')?.setValidators([Validators.required]);
      this.targetForm.get('endDate')?.setValidators([Validators.required]);
    }

    this.targetForm.get('month')?.updateValueAndValidity();
    this.targetForm.get('year')?.updateValueAndValidity();
    this.targetForm.get('startDate')?.updateValueAndValidity();
    this.targetForm.get('endDate')?.updateValueAndValidity();

    setTimeout(() => {
      this.updateTargetTable();
    }, 0);
  }

  get targets(): FormArray {
    return this.targetForm.get('targets') as FormArray;
  }

  submitTargets() {
    this.formSubmitted = true;

    if (this.targetForm.invalid) {
      this.targetForm.markAllAsTouched();
      return;
    }

    this.isSaving = true;
    
    if (this.deletedTargetIds.length > 0) {
      this.leadService.deleteTargets(this.deletedTargetIds).subscribe({
        next: () => {
          this.deletedTargetIds = []; 
          this.saveActiveTargets();   
        },
        error: (err:any) => {
          this.isSaving = false;
          this.messageService.showMessage('error', 'Error', 'Failed to delete removed targets.');
        }
      });
    } else {
      this.saveActiveTargets();
    }
  }

  saveActiveTargets() {
    const payload = this.targetForm.getRawValue(); 

    this.leadService.saveTargets(payload).subscribe({
      next: (res: any) => {
       this.messageService.showMessage(
        'success',
        'Success',
        'Target Saved Successfully'
      );
        this.isSaving = false;
        this.loadExistingTargets(); 
      },
      error: (err: any) => {
        this.messageService.showMessage(
        'error',
        'error',
        'Unable to save targets. Please try again later.'
      );
        this.isSaving = false;
      }
    });
  }
}