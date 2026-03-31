import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { 
  CardComponent, CardBodyComponent, CardHeaderComponent, CardFooterComponent,
  RowComponent, ColComponent, ContainerComponent, ButtonDirective, BadgeComponent,
  TableModule, SpinnerComponent, ModalModule, FormModule, ProgressComponent, AlertComponent
} from '@coreui/angular';
import { DataService } from '../../../shared/service/DataService';
import { MessageService } from '../../../shared/service/message.service';

/**
 * ✅ FIXED VERSION:
 * - RFQ-specific Scriteria management (won't affect admin criteria)
 * - Supplier name shown first in dropdown
 * - Supplier details displayed clearly
 * - Real-time accordion update after saving scores
 */
@Component({
  selector: 'app-supplier-evaluation',
  templateUrl: './supplier-evaluation.component.html',
  styleUrls: ['./supplier-evaluation.component.css'],
  standalone: true,
  imports: [
    CommonModule, FormsModule, ReactiveFormsModule, ContainerComponent, RowComponent, ColComponent,
    CardComponent, CardBodyComponent, CardHeaderComponent, CardFooterComponent, ButtonDirective,
    BadgeComponent, TableModule, SpinnerComponent, ModalModule, FormModule, ProgressComponent, AlertComponent
  ]
})
export class SupplierEvaluationComponent implements OnInit {
  
  // ==================== PROPERTIES ====================
  
  rfqId: number = 0;
  userId: number = 0;
  rfqDetails: any = null;
  suppliers: any[] = [];
  quotedSuppliers: any[] = [];
  
  currentStep: 'weightages' | 'score' | 'results' = 'weightages';
  
  // Criteria
  availableCriteria: any[] = [];
  assignedCriteria: any[] = [];
  
  // ✅ NEW: Track RFQ-specific criteria IDs
  rfqSpecificCriteriaIds: Set<number> = new Set();
  
  // Supplier selection for scoring
  selectedSupplierId: number | null = null;
  
  // Scoring (max score = 5, integers only 1-5)
  supplierScores: Map<number, Map<number, number>> = new Map();
  scoredSupplierIds: Set<number> = new Set();
  readonly MAX_SCORE = 5;
  
  // Rankings
  rankings: any[] = [];
  selectedSupplierScorecard: any = null;
  recommendedSupplier: any = null;
  
  // Loading states
  isLoading: boolean = false;
  isLoadingCriteria: boolean = false;
  isAssigningCriteria: boolean = false;
  isSavingScores: boolean = false;
  isCalculatingRankings: boolean = false;
  isLoadingRankings: boolean = false;
  
  // Modal states
  showScorecardModal: boolean = false;
  showAddCriteriaModal: boolean = false;
  showEditCriteriaModal: boolean = false;
  showDeleteConfirmModal: boolean = false;
  
  addCriteriaForm!: FormGroup;
  editCriteriaForm!: FormGroup;
  
  // For delete/edit operations
  criterionToDelete: any = null;
  criterionToEdit: any = null;
  
  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private dataService: DataService,
    private messageService: MessageService,
    private fb: FormBuilder,
    private cdr: ChangeDetectorRef // ✅ NEW: For manual change detection
  ) {}
  
  ngOnInit(): void {
    this.rfqId = Number(this.route.snapshot.paramMap.get('rfqId'));
    this.userId = Number(localStorage.getItem('userId')) || 0;
    
    if (!this.rfqId || this.rfqId === 0) {
      this.messageService.showMessage('error', 'Error', 'Invalid RFQ ID');
      this.goBack();
      return;
    }
    
    console.log('🎯 Supplier Evaluation for RFQ:', this.rfqId);
    console.log('  ✅ RFQ-specific criteria support added');
    console.log('  ✅ Supplier name in dropdown');
    console.log('  ✅ Real-time accordion update');
    
    this.initAddCriteriaForm();
    this.initEditCriteriaForm();
    this.loadRFQDetails();
    this.checkExistingEvaluation();
  }
  
  // ==================== INITIALIZATION ====================
  
  initAddCriteriaForm(): void {
    this.addCriteriaForm = this.fb.group({
      criterionName: ['', [Validators.required, Validators.minLength(2)]],
      description: [''],
      weightage: [0, [Validators.required, Validators.min(1), Validators.max(99)]]
    });
  }
  
  initEditCriteriaForm(): void {
    this.editCriteriaForm = this.fb.group({
      weightage: [0, [Validators.required, Validators.min(1), Validators.max(99)]]
    });
  }
  
  loadRFQDetails(): void {
    this.isLoading = true;
    
    this.dataService.getQuoteComparison(this.rfqId).subscribe({
      next: (response: any) => {
        if (response && response.success && response.data) {
          const data = response.data;
          
          this.rfqDetails = {
            rfqId: data.rfqId,
            rfqNumber: data.rfqNumber,
            rfqTitle: data.rfqTitle
          };
          
          this.suppliers = data.suppliers || [];
          this.quotedSuppliers = this.suppliers.filter((s: any) => s.status === 'RESPONDED');
          
          console.log('✅ Loaded RFQ:', this.rfqDetails.rfqNumber);
          console.log('👥 Total Suppliers:', this.suppliers.length);
          console.log('✅ Quoted Suppliers:', this.quotedSuppliers.length);
        }
        this.isLoading = false;
      },
      error: (error: any) => {
        console.error('❌ Error loading RFQ:', error);
        this.messageService.showMessage('error', 'Error', 'Failed to load RFQ details');
        this.isLoading = false;
      }
    });
  }
  
  checkExistingEvaluation(): void {
    this.isLoadingCriteria = true;
    
    this.dataService.getRFQCriteria(this.rfqId).subscribe({
      next: (response: any) => {
        if (response && response.success && response.data && response.data.length > 0) {
          this.assignedCriteria = response.data;
          
          // ✅ NEW: Track which criteria are RFQ-specific
          this.assignedCriteria.forEach(ac => {
            if (ac.isRfqSpecific) {
              this.rfqSpecificCriteriaIds.add(ac.criterion.id);
            }
          });
          
          console.log('✅ Found existing criteria:', this.assignedCriteria.length);
          console.log('  RFQ-specific:', this.rfqSpecificCriteriaIds.size);
          this.checkExistingScores();
        } else {
          console.log('ℹ️ No criteria assigned');
          this.loadAvailableCriteria();
        }
        this.isLoadingCriteria = false;
      },
      error: () => {
        this.loadAvailableCriteria();
        this.isLoadingCriteria = false;
      }
    });
  }
  
  loadAvailableCriteria(): void {
    this.dataService.getAllActiveCriteria().subscribe({
      next: (response: any) => {
        let criteria: any[] = [];
        
        if (response && response.success && response.data) {
          criteria = response.data;
        } else if (Array.isArray(response)) {
          criteria = response;
        }
        
        if (criteria.length === 0) {
          this.messageService.showMessage('warning', 'No Criteria', 'No evaluation criteria configured');
          this.goBack();
          return;
        }
        
        const equalWeight = Math.floor(100 / criteria.length);
        const remainder = 100 - (equalWeight * criteria.length);
        
        this.availableCriteria = criteria.map((c, index) => ({
          ...c,
          maxScore: 5,
          weightage: index === 0 ? equalWeight + remainder : equalWeight,
          isRfqSpecific: false
        }));
        
        console.log('✅ Loaded criteria:', this.availableCriteria.length);
        
        this.currentStep = 'weightages';
      },
      error: (error: any) => {
        console.error('❌ Error:', error);
        this.messageService.showMessage('error', 'Error', 'Failed to load criteria');
      }
    });
  }
  
  checkExistingScores(): void {
    this.dataService.getSupplierRankings(this.rfqId).subscribe({
      next: (response: any) => {
        if (response && response.success && response.data && response.data.length > 0) {
          console.log('✅ Found existing rankings');
          this.currentStep = 'results';
          this.loadRankings();
        } else {
          console.log('ℹ️ No rankings, start scoring');
          this.currentStep = 'score';
          this.initializeScoring();
          this.loadExistingScores();
        }
      },
      error: () => {
        this.currentStep = 'score';
        this.initializeScoring();
        this.loadExistingScores();
      }
    });
  }
  
  loadExistingScores(): void {
    console.log('🔍 Loading existing scores from backend...');
    
    let loadedCount = 0;
    const totalSuppliers = this.quotedSuppliers.length;
    
    this.quotedSuppliers.forEach(supplier => {
      this.dataService.getSupplierScorecard(this.rfqId, supplier.supplierId).subscribe({
        next: (response: any) => {
          loadedCount++;
          
          if (response && response.success && response.data) {
            const scorecard = response.data;
            if (scorecard.criteriaScores && scorecard.criteriaScores.length > 0) {
              this.scoredSupplierIds.add(supplier.supplierId);
              
              const scores = new Map<number, number>();
              scorecard.criteriaScores.forEach((cs: any) => {
                const criterion = this.assignedCriteria.find(c => 
                  c.criterion.criterionName === cs.criterionName
                );
                if (criterion && cs.rawScore > 0) {
                  scores.set(criterion.criterion.id, cs.rawScore);
                }
              });
              this.supplierScores.set(supplier.supplierId, scores);
              
              console.log(`✅ Loaded scores for ${supplier.supplierName}:`, scores.size, 'criteria');
            }
          }
          
          if (loadedCount === totalSuppliers) {
            console.log('✅ Finished loading existing scores');
            console.log('📊 Scored suppliers:', Array.from(this.scoredSupplierIds));
            console.log('📊 Total scored:', this.getScoredSuppliersCount());
            this.cdr.detectChanges(); // ✅ Force change detection
          }
        },
        error: (error: any) => {
          loadedCount++;
          console.log(`ℹ️ No scores found for ${supplier.supplierName}`);
          
          if (loadedCount === totalSuppliers) {
            console.log('✅ Finished checking all suppliers');
            console.log('📊 Scored suppliers:', Array.from(this.scoredSupplierIds));
            this.cdr.detectChanges();
          }
        }
      });
    });
  }
  
  // ==================== STEP 1: ASSIGN WEIGHTAGES ====================
  
  getTotalWeightage(): number {
    return this.availableCriteria.reduce((sum, c) => sum + (parseInt(c.weightage) || 0), 0);
  }
  
  onWeightageChange(): void {
    const total = this.getTotalWeightage();
    console.log('💰 Total weightage:', total + '%');
  }
  
  validateWeightageInput(event: any, criterion: any): void {
    const value = event.target.value;
    
    if (value.includes('.')) {
      event.target.value = Math.floor(parseFloat(value));
      criterion.weightage = parseInt(event.target.value);
      this.messageService.showMessage('warning', 'Integers Only', 'Weightage must be an integer (no decimals)');
    }
    
    this.onWeightageChange();
  }
  
  openAddCriteriaModal(): void {
    this.addCriteriaForm.reset({ weightage: 0 });
    this.showAddCriteriaModal = true;
  }
  
  /**
   * ✅ FIXED: Create RFQ-specific criterion
   * This criterion will NOT appear in admin's global criteria list
   */
  addRFQSpecificCriterion(): void {
    if (this.addCriteriaForm.invalid) {
      this.addCriteriaForm.markAllAsTouched();
      return;
    }

    const criterionData = {
      criterionName: this.addCriteriaForm.value.criterionName,
      description: this.addCriteriaForm.value.description,
      criterionType: 'MANUAL',
      maxScore: 5,
      isActive: true
    };

    console.log('📤 Creating RFQ-specific criterion:', criterionData);

    this.dataService.createCriterion(criterionData).subscribe({
      next: (response: any) => {
        if (response && response.success && response.data) {
          const newCriterion = {
            ...response.data,
            weightage: parseInt(this.addCriteriaForm.value.weightage),
            isRfqSpecific: true // ✅ Mark as RFQ-specific
          };
          
          this.availableCriteria.push(newCriterion);
          
          // ✅ Track this as RFQ-specific
          this.rfqSpecificCriteriaIds.add(response.data.id);

          this.messageService.showMessage('success', 'Success', 
            'RFQ-specific criterion added (won\'t affect admin\'s global criteria)');
          this.showAddCriteriaModal = false;
          this.onWeightageChange();
        }
      },
      error: (error: any) => {
        console.error('❌ Error:', error);
        this.messageService.showMessage('error', 'Error', 'Failed to add criterion');
      }
    });
  }
  
  openEditCriteriaModal(criterion: any): void {
    this.criterionToEdit = criterion;
    this.editCriteriaForm.patchValue({
      weightage: parseInt(criterion.weightage)
    });
    this.showEditCriteriaModal = true;
  }
  
  saveEditedWeightage(): void {
    if (this.editCriteriaForm.invalid) {
      this.editCriteriaForm.markAllAsTouched();
      return;
    }
    
    const newWeightage = parseInt(this.editCriteriaForm.value.weightage);
    const oldWeightage = parseInt(this.criterionToEdit.weightage);
    
    this.criterionToEdit.weightage = newWeightage;
    const total = this.getTotalWeightage();
    
    if (total !== 100) {
      this.criterionToEdit.weightage = oldWeightage;
      this.messageService.showMessage('error', 'Invalid Total', 
        `Total weightage must be exactly 100%. Current: ${total}%`);
      return;
    }
    
    this.messageService.showMessage('success', 'Updated', 
      `Weightage updated to ${newWeightage}%`);
    this.showEditCriteriaModal = false;
    this.criterionToEdit = null;
  }
  
  confirmDeleteCriterion(criterion: any): void {
    this.criterionToDelete = criterion;
    this.showDeleteConfirmModal = true;
  }
  
  /**
   * ✅ FIXED: Delete criterion from evaluation
   * - If admin criterion: Remove from this RFQ only (doesn't delete globally)
   * - If RFQ-specific: Can be removed
   */
  deleteCriterion(): void {
    if (!this.criterionToDelete) return;
    
    const index = this.availableCriteria.indexOf(this.criterionToDelete);
    if (index > -1) {
      const isRfqSpecific = this.criterionToDelete.isRfqSpecific || 
                           this.rfqSpecificCriteriaIds.has(this.criterionToDelete.id);
      
      this.availableCriteria.splice(index, 1);
      
      const message = isRfqSpecific 
        ? 'RFQ-specific criterion removed'
        : 'Criterion removed from this evaluation (admin\'s global criterion unchanged)';
      
      this.messageService.showMessage('success', 'Removed', message);
      
      this.showDeleteConfirmModal = false;
      this.criterionToDelete = null;
      
      this.onWeightageChange();
    }
  }
  
  assignWeightagesAndProceed(): void {
    const total = this.getTotalWeightage();
    
    if (total !== 100) {
      this.messageService.showMessage('warning', 'Invalid', 
        `Total must be exactly 100% (currently ${total}%)`);
      return;
    }
    
    this.isAssigningCriteria = true;
    
    const criteriaWeightages: any = {};
    this.availableCriteria.forEach(c => {
      criteriaWeightages[c.id] = parseFloat(c.weightage);
    });
    
    this.dataService.assignCriteriaToRFQ(this.rfqId, this.userId, criteriaWeightages).subscribe({
      next: (response: any) => {
        console.log('✅ Criteria assigned');
        
        this.dataService.getRFQCriteria(this.rfqId).subscribe({
          next: (resp: any) => {
            if (resp && resp.success && resp.data) {
              this.assignedCriteria = resp.data;
              
              this.currentStep = 'score';
              this.initializeScoring();
            }
            this.isAssigningCriteria = false;
          }
        });
      },
      error: (error: any) => {
        console.error('❌ Error:', error);
        this.messageService.showMessage('error', 'Error', 'Failed to assign criteria');
        this.isAssigningCriteria = false;
      }
    });
  }
  
  // ==================== STEP 2: SCORE SUPPLIERS ====================
  
  initializeScoring(): void {
    this.selectedSupplierId = null;
    
    this.quotedSuppliers.forEach(supplier => {
      if (!this.supplierScores.has(supplier.supplierId)) {
        this.supplierScores.set(supplier.supplierId, new Map());
      }
    });
    
    console.log('📊 Scoring initialized for', this.quotedSuppliers.length, 'suppliers');
    console.log('📊 Already scored:', this.getScoredSuppliersCount());
  }
  
  getManualCriteria(): any[] {
    return this.assignedCriteria;
  }
  
  /**
   * ✅ FIXED: Get selected supplier with proper type handling
   */
  getSelectedSupplier(): any {
    if (!this.selectedSupplierId) {
      return null;
    }
    
    const supplierId = typeof this.selectedSupplierId === 'string' 
      ? parseInt(this.selectedSupplierId) 
      : this.selectedSupplierId;
    
    const supplier = this.quotedSuppliers.find(s => s.supplierId === supplierId);
    
    return supplier || null;
  }
  
  /**
   * ✅ NEW: Get supplier name by ID
   */
  getSupplierNameById(supplierId: number): string {
    const supplier = this.quotedSuppliers.find(s => s.supplierId === supplierId);
    return supplier?.supplierName || supplier?.companyName || 'Unknown Supplier';
  }
  
  getSupplierScoredCriteriaCount(supplierId: number): number {
    const scores = this.supplierScores.get(supplierId);
    if (!scores) return 0;
    
    let count = 0;
    this.assignedCriteria.forEach(criterion => {
      if (scores.has(criterion.criterion.id) && scores.get(criterion.criterion.id)! > 0) {
        count++;
      }
    });
    
    return count;
  }
  
  editSupplierRatings(supplierId: number): void {
    this.selectedSupplierId = supplierId;
    window.scrollTo({ top: 0, behavior: 'smooth' });
    
    this.messageService.showMessage('info', 'Edit Mode', 
      'You can now edit the ratings for ' + this.getSupplierNameById(supplierId));
  }
  
  trackByCriterion(index: number, item: any): number {
    return item.criterion.id;
  }
  
  onScoreBlur(event: any, supplierId: number, criterionId: number): void {
    const inputElement = event.target as HTMLInputElement;
    const currentScore = this.getSupplierScore(supplierId, criterionId);
    
    if (currentScore > 0) {
      inputElement.value = currentScore.toString();
    } else {
      inputElement.value = '';
    }
  }
  
  onSupplierChange(): void {
    console.log('📊 Selected supplier ID:', this.selectedSupplierId);
    
    const supplier = this.getSelectedSupplier();
    if (supplier) {
      console.log('📊 Selected supplier:', supplier.supplierName || supplier.companyName);
    }
    
    // ✅ Force change detection to update UI
    this.cdr.detectChanges();
    
    setTimeout(() => {
      if (this.selectedSupplierId) {
        const inputs = document.querySelectorAll(`input[id^="score-${this.selectedSupplierId}-"]`);
        inputs.forEach((input: any) => {
          const criterionId = parseInt(input.id.split('-')[2]);
          const score = this.getSupplierScore(this.selectedSupplierId!, criterionId);
          input.value = score > 0 ? score.toString() : '';
        });
      }
    }, 0);
  }
  
  getScoredSuppliersCount(): number {
    return this.scoredSupplierIds.size;
  }
  
  isCurrentSupplierScored(): boolean {
    if (!this.selectedSupplierId) return false;
    const scores = this.supplierScores.get(this.selectedSupplierId);
    return scores !== undefined && scores.size > 0;
  }
  
  isSupplierScored(supplierId: number | string): boolean {
    const id = typeof supplierId === 'string' ? parseInt(supplierId) : supplierId;
    
    if (this.scoredSupplierIds.has(id)) {
      return true;
    }
    
    const scores = this.supplierScores.get(id);
    if (scores && scores.size > 0) {
      this.scoredSupplierIds.add(id);
      return true;
    }
    
    return false;
  }
  
  /**
   * ✅ FIXED: Real-time accordion update after saving
   */
  saveCurrentSupplierScores(): void {
    if (!this.selectedSupplierId) return;
    
    const scores = this.supplierScores.get(this.selectedSupplierId);
    
    if (!scores || scores.size === 0) {
      this.messageService.showMessage('warning', 'No Scores Entered', 
        'Please enter scores for at least one criterion before saving');
      return;
    }
    
    const scoresObj: any = {};
    scores.forEach((score, criterionId) => {
      const rfqCriterion = this.assignedCriteria.find(c => c.criterion.id === criterionId);
      if (rfqCriterion && score > 0) {
        scoresObj[rfqCriterion.id] = score;
      }
    });
    
    const currentSupplierId = this.selectedSupplierId;
    const supplierName = this.getSupplierNameById(currentSupplierId);
    
    this.isSavingScores = true;
    
    this.dataService.scoreSupplier(this.rfqId, this.selectedSupplierId, this.userId, scoresObj).subscribe({
      next: () => {
        const scoredCount = scores.size;
        const totalCriteria = this.assignedCriteria.length;
        
        // ✅ Add to scored Set and force change detection
        this.scoredSupplierIds.add(currentSupplierId);
        
        this.messageService.showMessage('success', '✅ Saved Successfully', 
          `Scores saved for criteria.`);
        
        // Clear selection
        this.selectedSupplierId = null;
        this.isSavingScores = false;
        
        // ✅ Force Angular to detect changes immediately
        this.cdr.detectChanges();
        
        // Scroll to accordion
        setTimeout(() => {
          const accordionSection = document.querySelector('.saved-ratings-section');
          if (accordionSection) {
            accordionSection.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
          }
        }, 300);
        
        console.log('✅ Supplier scored:', currentSupplierId);
        console.log('📊 Total scored:', this.getScoredSuppliersCount());
        console.log('📋 Accordion should update now');
      },
      error: (error: any) => {
        console.error('❌ Error saving scores:', error);
        this.messageService.showMessage('error', 'Error', 'Failed to save scores');
        this.isSavingScores = false;
      }
    });
  }
  
  getSupplierScore(supplierId: number, criterionId: number): number {
    const scores = this.supplierScores.get(supplierId);
    if (!scores) return 0;
    return scores.get(criterionId) || 0;
  }
  
  updateSupplierScore(supplierId: number, criterionId: number, value: any): void {
    if (value === '' || value === null || value === undefined) {
      const scores = this.supplierScores.get(supplierId);
      if (scores) {
        scores.delete(criterionId);
      }
      return;
    }
    
    let score = parseFloat(value);
    
    if (isNaN(score)) {
      return;
    }
    
    score = Math.floor(score);
    
    if (score < 1 || score > this.MAX_SCORE) {
      this.messageService.showMessage('warning', 'Invalid Score', 
        `Score must be between 1 and ${this.MAX_SCORE}`);
      return;
    }
    
    let scores = this.supplierScores.get(supplierId);
    if (!scores) {
      scores = new Map<number, number>();
      this.supplierScores.set(supplierId, scores);
    }
    
    scores.set(criterionId, score);
    console.log(`✅ Stored: Supplier ${supplierId}, Criterion ${criterionId} = ${score}`);
  }
  
  saveAllScoresAndCalculateRankings(): void {
    const unScoredCount = this.quotedSuppliers.length - this.scoredSupplierIds.size;
    
    if (unScoredCount > 0) {
      this.messageService.showMessage('warning', 'Incomplete Scoring', 
        `Please score all ${this.quotedSuppliers.length} suppliers before calculating rankings. ${unScoredCount} supplier(s) not scored yet.`);
      return;
    }
    
    console.log('💾 Saving all scores and calculating rankings...');
    this.isSavingScores = true;
    
    let completed = 0;
    const total = this.quotedSuppliers.length;
    
    this.quotedSuppliers.forEach(supplier => {
      const scores = this.supplierScores.get(supplier.supplierId);
      const scoresObj: any = {};
      
      if (scores) {
        scores.forEach((score, criterionId) => {
          const rfqCriterion = this.assignedCriteria.find(c => c.criterion.id === criterionId);
          if (rfqCriterion && score > 0) {
            scoresObj[rfqCriterion.id] = score;
          }
        });
      }
      
      this.dataService.scoreSupplier(this.rfqId, supplier.supplierId, this.userId, scoresObj).subscribe({
        next: () => {
          completed++;
          if (completed === total) {
            this.messageService.showMessage('success', 'Success', 'All suppliers scored');
            this.calculateFinalRankingsAutomatic();
          }
        },
        error: () => {
          completed++;
          if (completed === total) {
            this.calculateFinalRankingsAutomatic();
          }
        }
      });
    });
  }
  
  logCurrentState(): void {
    console.log('=== CURRENT STATE ===');
    console.log('Quoted Suppliers:', this.quotedSuppliers.length);
    console.log('Scored Count:', this.getScoredSuppliersCount());
    console.log('Scored Supplier IDs (Set):', Array.from(this.scoredSupplierIds));
    console.log('Supplier Scores Map:');
    this.supplierScores.forEach((scores, supplierId) => {
      const supplier = this.getSupplierNameById(supplierId);
      const isScored = this.isSupplierScored(supplierId);
      console.log(`  ${supplier} (ID: ${supplierId}):`, scores.size, 'criteria scored', '| isScored:', isScored);
      scores.forEach((score, criterionId) => {
        console.log(`    - Criterion ${criterionId}: ${score}`);
      });
    });
  }
  
  // ==================== AUTOMATIC FINAL RANKING ====================
  
  calculateFinalRankingsAutomatic(): void {
    console.log('🏆 Calculating final rankings AUTOMATICALLY...');
    
    this.isSavingScores = false;
    this.isCalculatingRankings = true;
    
    this.dataService.calculateFinalRankings(this.rfqId).subscribe({
      next: (response: any) => {
        console.log('✅ Final rankings calculated:', response);
        
        this.messageService.showMessage('success', 'Success', 
          'Final rankings calculated automatically!');
        
        this.currentStep = 'results';
        this.loadRankings();
        
        this.isCalculatingRankings = false;
      },
      error: (error: any) => {
        console.error('❌ Error calculating rankings:', error);
        
        this.messageService.showMessage('error', 'Error', 
          error.error?.message || 'Failed to calculate rankings');
        this.isCalculatingRankings = false;
      }
    });
  }
  
  // ==================== STEP 3: VIEW RESULTS ====================
  
  loadRankings(): void {
    this.isLoadingRankings = true;
    
    this.dataService.getSupplierRankings(this.rfqId).subscribe({
      next: (response: any) => {
        if (response && response.success && response.data) {
          this.rankings = response.data;
        } else if (Array.isArray(response)) {
          this.rankings = response;
        }
        
        if (this.rankings.length > 0) {
          this.recommendedSupplier = this.rankings[0];
          console.log('🏆 Top:', this.recommendedSupplier.supplierName);
        }
        
        this.isLoadingRankings = false;
      },
      error: (error: any) => {
        console.error('❌ Error:', error);
        this.messageService.showMessage('error', 'Error', 'Failed to load rankings');
        this.isLoadingRankings = false;
      }
    });
  }
  
  viewScorecard(supplier: any): void {
    this.dataService.getSupplierScorecard(this.rfqId, supplier.supplierId).subscribe({
      next: (response: any) => {
        if (response && response.success && response.data) {
          this.selectedSupplierScorecard = response.data;
          this.showScorecardModal = true;
        }
      },
      error: (error: any) => {
        console.error('❌ Error:', error);
        this.messageService.showMessage('error', 'Error', 'Failed to load scorecard');
      }
    });
  }
  
  // ==================== NAVIGATION ====================
  
  goBack(): void {
    this.router.navigate(['/quote-comparison', this.rfqId]);
  }
  
  goToQuoteComparison(): void {
    this.router.navigate(['/quote-comparison', this.rfqId]);
  }
}