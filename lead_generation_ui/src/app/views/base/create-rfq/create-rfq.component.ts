
import { Component, OnInit, OnDestroy, ChangeDetectionStrategy, ChangeDetectorRef, HostListener, SecurityContext } from '@angular/core';
import { FormBuilder, FormGroup, Validators, FormArray, FormControl } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { trigger, state, style, transition, animate } from '@angular/animations';
import { HttpEventType } from '@angular/common/http';
import { DomSanitizer } from '@angular/platform-browser';

import { RFQService } from '../models/rfq.service';
import { BuyerService } from '../dashboard/buyer-b.service';
import { SupplierService } from '../dashboard/supplier.service';
import { MessageService } from '../../../shared/service/message.service';
import { StorageService } from '../../../shared/service/StorageService';
import { RFQItem, RFQCreatePayload, RFQItemAttachment } from '../models/rfq.model';

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
export class CreateRFQComponent implements OnInit, OnDestroy {

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

  // ==================== SUPPLIER SELECTION ====================
  selectedSupplierIds: number[] = [];
  selectedSupplierToAdd: number | null = null;

  // ==================== SUPPLIER PAGINATION & SEARCH ====================
  supplierSearchTerm: string = '';
  suppliersPerPage: number = 12;
  currentSuppliersPage: number = 1;
  totalSupplierPages: number = 1;

  // ==================== MULTI-SELECT DROPDOWN ====================
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

  // ==================== ATTACHMENT PROPERTIES ====================
  selectedFiles: Map<number, File[]> = new Map();
  uploadingFiles: Map<number, boolean> = new Map();
  itemAttachments: Map<number, RFQItemAttachment[]> = new Map();
  uploadProgress: Map<number, number> = new Map();
  previewFile: RFQItemAttachment | null = null;
  previewUrl: any = null;

  constructor(
    private fb: FormBuilder,
    private rfqService: RFQService,
    private buyerService: BuyerService,
    private supplierService: SupplierService,
    private messageService: MessageService,
    private router: Router,
    private route: ActivatedRoute,
    private cdr: ChangeDetectorRef,
    private sanitizer: DomSanitizer
  ) {}

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent) {
    const target = event.target as HTMLElement;
    if (!target.closest('.multi-select-dropdown')) {
      this.closeSupplierDropdown();
    }
  }

  // ngOnInit(): void {
  //   this.initializeForm();
  //   this.detectMode();
  //   this.loadLoggedInBuyer();
  //   this.loadSuppliers();
  // }

  // ==================== ATTACHMENT METHODS ====================

  onFileSelect(event: any, itemIndex: number): void {
    const files: FileList = event.target.files;
    
    if (files && files.length > 0) {
      const fileArray = Array.from(files);
      
      const allowedTypes = [
        'application/pdf',
        'application/msword',
        'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
        'application/vnd.ms-excel',
        'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
        'image/jpeg',
        'image/png',
        'image/gif',
        'application/zip'
      ];
      
      const invalidFiles = fileArray.filter(f => !allowedTypes.includes(f.type));
      
      if (invalidFiles.length > 0) {
        this.messageService.showMessage('error', 'Invalid Files', 
          `Some files have invalid types: ${invalidFiles.map(f => f.name).join(', ')}`);
        return;
      }
      
      const maxSize = 10 * 1024 * 1024;
      const oversizedFiles = fileArray.filter(f => f.size > maxSize);
      
      if (oversizedFiles.length > 0) {
        this.messageService.showMessage('error', 'Files Too Large', 
          `Some files exceed 10MB limit: ${oversizedFiles.map(f => f.name).join(', ')}`);
        return;
      }
      
      const existingFiles = this.selectedFiles.get(itemIndex) || [];
      const combinedFiles = [...existingFiles, ...fileArray];
      
      this.selectedFiles.set(itemIndex, combinedFiles);
      
      console.log('%c[FILES SELECTED]', 'color: #0066cc; font-weight: bold;', {
        itemIndex: itemIndex,
        newFiles: fileArray.length,
        totalFiles: combinedFiles.length,
        files: combinedFiles.map(f => f.name)
      });
      
      this.cdr.markForCheck();
    }
  }

  removeSelectedFile(itemIndex: number, fileIndex: number): void {
    const files = this.selectedFiles.get(itemIndex);
    
    if (files && fileIndex >= 0 && fileIndex < files.length) {
      const removedFile = files[fileIndex];
      files.splice(fileIndex, 1);
      
      if (files.length === 0) {
        this.selectedFiles.delete(itemIndex);
      } else {
        this.selectedFiles.set(itemIndex, files);
      }
      
      console.log('%c[FILE REMOVED]', 'color: #ff9800;', {
        itemIndex,
        fileName: removedFile.name,
        remainingFiles: files.length
      });
      
      this.cdr.markForCheck();
    }
  }

  async uploadFilesForItem(itemIndex: number, itemId: number): Promise<void> {
    const files = this.selectedFiles.get(itemIndex);
    
    if (!files || files.length === 0) {
      console.log('%c[NO FILES]', 'color: #ff9800;', 'No files selected for item', itemIndex);
      return;
    }
    
    if (!itemId) {
      this.messageService.showMessage('error', 'Error', 'Item must be saved before uploading files');
      return;
    }
    
    this.uploadingFiles.set(itemIndex, true);
    this.uploadProgress.set(itemIndex, 0);
    
    console.log('%c[UPLOADING FILES]', 'color: #0066cc; font-weight: bold;', {
      itemIndex,
      itemId,
      count: files.length
    });
    
    try {
      let uploaded = 0;
      
      for (const file of files) {
        await new Promise<void>((resolve, reject) => {
          this.rfqService.uploadItemAttachment(itemId, file).subscribe({
            next: (event: any) => {
              if (event.type === HttpEventType.UploadProgress) {
                const progress = Math.round((100 * event.loaded) / event.total);
                this.uploadProgress.set(itemIndex, progress);
                this.cdr.markForCheck();
              } else if (event.type === HttpEventType.Response) {
                uploaded++;
                console.log('%c[FILE UPLOADED]', 'color: #00aa00;', file.name);
                resolve();
              }
            },
            error: (error) => {
              console.error('%c[UPLOAD ERROR]', 'color: #cc0000;', error);
              reject(error);
            }
          });
        });
      }
      
      this.selectedFiles.delete(itemIndex);
      await this.loadItemAttachments(itemIndex, itemId);
      
      this.messageService.showMessage('success', 'Success', `${uploaded} file(s) uploaded successfully`);
      
    } catch (error) {
      console.error('%c[UPLOAD ERROR]', 'color: #cc0000;', error);
      this.messageService.showMessage('error', 'Error', 'Failed to upload some files');
    } finally {
      this.uploadingFiles.set(itemIndex, false);
      this.uploadProgress.delete(itemIndex);
      this.cdr.markForCheck();
    }
  }

  async loadItemAttachments(itemIndex: number, itemId: number): Promise<void> {
    if (!itemId) {
      console.log('%c[NO ITEM ID]', 'color: #ff9800;', 'Cannot load attachments without item ID');
      return;
    }
    
    try {
      const response = await this.rfqService.getItemAttachments(itemId).toPromise();
      
      const attachments = response?.data || [];
      this.itemAttachments.set(itemIndex, attachments);
      
      console.log('%c[ATTACHMENTS LOADED]', 'color: #00aa00;', {
        itemIndex,
        count: attachments.length
      });
      
      this.cdr.markForCheck();
    } catch (error) {
      console.error('%c[LOAD ATTACHMENTS ERROR]', 'color: #cc0000;', error);
    }
  }

  async deleteAttachment(itemIndex: number, itemId: number, attachmentId: number): Promise<void> {
    if (!confirm('Are you sure you want to delete this attachment?')) {
      return;
    }
    
    try {
      await this.rfqService.deleteItemAttachment(attachmentId).toPromise();
      
      this.messageService.showMessage('success', 'Deleted', 'Attachment deleted successfully');
      await this.loadItemAttachments(itemIndex, itemId);
      
    } catch (error) {
      console.error('%c[DELETE ERROR]', 'color: #cc0000;', error);
      this.messageService.showMessage('error', 'Error', 'Failed to delete attachment');
    }
  }

  downloadAttachment(attachmentId: number, fileName: string): void {
    this.rfqService.downloadItemAttachment(attachmentId, fileName).subscribe({
      next: () => {
        console.log('%c[DOWNLOAD SUCCESS]', 'color: #00aa00;', fileName);
      },
      error: (error) => {
        console.error('%c[DOWNLOAD ERROR]', 'color: #cc0000;', error);
        this.messageService.showMessage('error', 'Error', 'Failed to download attachment');
      }
    });
  }

  getFileIcon(fileType: string): string {
    if (fileType.includes('pdf')) return 'bi-file-pdf text-danger';
    if (fileType.includes('word') || fileType.includes('document')) return 'bi-file-word text-primary';
    if (fileType.includes('excel') || fileType.includes('sheet')) return 'bi-file-excel text-success';
    if (fileType.includes('image')) return 'bi-file-image text-info';
    if (fileType.includes('zip')) return 'bi-file-zip text-warning';
    return 'bi-file-earmark text-secondary';
  }

  formatFileSize(bytes: number): string {
    if (bytes === 0) return '0 Bytes';
    
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    
    return Math.round((bytes / Math.pow(k, i)) * 100) / 100 + ' ' + sizes[i];
  }

  getItemAttachmentsArray(itemIndex: number): RFQItemAttachment[] {
    return this.itemAttachments.get(itemIndex) || [];
  }

  hasSelectedFiles(itemIndex: number): boolean {
    const files = this.selectedFiles.get(itemIndex);
    return files !== undefined && files.length > 0;
  }

  getSelectedFiles(itemIndex: number): File[] {
    return this.selectedFiles.get(itemIndex) || [];
  }

  isUploading(itemIndex: number): boolean {
    return this.uploadingFiles.get(itemIndex) || false;
  }

  getUploadProgress(itemIndex: number): number {
    return this.uploadProgress.get(itemIndex) || 0;
  }

  getItemIdFromForm(itemIndex: number): number | null {
    return null;
  }

  // ==================== SUPPLIER DROPDOWN METHODS ====================

  toggleSupplierDropdown(): void {
    this.supplierDropdownOpen = !this.supplierDropdownOpen;
    
    if (this.supplierDropdownOpen) {
      this.supplierDropdownSearch = '';
      this.filteredSuppliersForDropdown = [...this.filteredSuppliers];
    }
    
    this.cdr.markForCheck();
  }

  closeSupplierDropdown(): void {
    if (this.supplierDropdownOpen) {
      this.supplierDropdownOpen = false;
      this.supplierDropdownSearch = '';
      this.cdr.markForCheck();
    }
  }

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

  clearSupplierSearch(): void {
    this.supplierDropdownSearch = '';
    this.onSupplierDropdownSearch();
  }

  getInitials(name: string): string {
    if (!name) return 'NA';
    const nameParts = name.trim().split(' ');
    if (nameParts.length >= 2) {
      return (nameParts[0][0] + nameParts[1][0]).toUpperCase();
    }
    return name.substring(0, 2).toUpperCase();
  }

  getFilteredSuppliersForDropdown(): any[] {
    return this.filteredSuppliersForDropdown;
  }

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

  getSelectedFromFiltered(): any[] {
    const filteredIds = this.filteredSuppliersForDropdown.map(s => s.id);
    return this.selectedSupplierIds.filter(id => filteredIds.includes(id));
  }

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
            //this.messageService.showMessage('error', 'Error', 'Failed to load buyer details');
            return;
          }

          const loggedInEmail = localStorage.getItem('username') || localStorage.getItem('email');
          const loggedInName = localStorage.getItem('fullName');
          const loggedInPhone = localStorage.getItem('phone');

          this.loggedInBuyer = {
            id: buyerDetails.id,
            companyName: buyerDetails.companyName || 'N/A',
            companyType: buyerDetails.companyType || 'Others',
            email: loggedInEmail || buyerDetails.contactPersonEmail || 'N/A',
            contactPersonName: loggedInName || buyerDetails.contactPersonName || 'N/A',
            contactPersonPhone: loggedInPhone || buyerDetails.contactPersonPhone || 'N/A'
          };

          this.buyerCompanyType = this.loggedInBuyer.companyType;
          this.dynamicFieldsForCurrentCompanyType = getFieldsByCompanyType(this.buyerCompanyType);

          console.log('%c[✅ BUYER LOADED SUCCESSFULLY]', 'color: #00aa00; font-weight: bold; font-size: 14px;');
          console.log('  📌 Company:', this.loggedInBuyer.companyName);
          console.log('  📌 Type:', this.buyerCompanyType);
          console.log('  📌 User Email:', this.loggedInBuyer.email);
          console.log('  📌 User Name:', this.loggedInBuyer.contactPersonName);
          console.log('  📌 User Phone:', this.loggedInBuyer.contactPersonPhone);

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
         // this.messageService.showMessage('error', 'Error', 'Failed to load buyer details');
        }
      });
    } catch (error) {
      console.error('%c[ERROR] Exception in loadLoggedInBuyer', 'color: #cc0000;', error);
     // this.messageService.showMessage('error', 'Error', 'Failed to load buyer details');
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
        
        console.log('%c[MODE DETECTED]', 'color: #0066cc;', {
          id: this.rfqId,
          mode: this.mode
        });
        
        if (this.rfqId) {
          this.loadRFQData(this.rfqId);
        }
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


// addItem(item?: RFQItem): void {
//   const dynamicFieldsGroup: { [key: string]: any } = {};

//   // Add predefined dynamic fields
//   this.dynamicFieldsForCurrentCompanyType.forEach(field => {
//     const fieldValue = item?.dynamicFields?.[field.fieldName] || '';
//     dynamicFieldsGroup[field.fieldName] = [fieldValue];
//   });

//   // Add custom fields
//   if (item?.dynamicFields) {
//     Object.keys(item.dynamicFields).forEach(key => {
//       if (key.startsWith('custom_') && item.dynamicFields) {
//         dynamicFieldsGroup[key] = [item.dynamicFields[key]];
//       }
//     });
//   }

//   // Create the item form group
//   const itemGroup = this.fb.group({
//     id: [item?.id || null],
//     itemDescription: [item?.itemDescription || '', Validators.required],
//     quantity: [item?.quantity || 1, [Validators.required, Validators.min(1)]],
//     uom: [item?.uom || 'Qty', Validators.required],
//     specifications: [item?.specifications || ''],
//     dynamicFields: this.fb.group(dynamicFieldsGroup),
//     companyType: [item?.companyType || this.buyerCompanyType || 'Others']
//   });

//   this.itemsArray.push(itemGroup);

//   const itemIndex = this.itemsArray.length - 1;
//   this.customFieldsByItem.set(itemIndex, []);
//   this.showAddFieldForm[itemIndex] = false;

//   // Restore custom fields if present
//   if (item?.dynamicFields) {
//     const customFields: DynamicField[] = [];
//     Object.keys(item.dynamicFields).forEach(key => {
//       if (key.startsWith('custom_')) {
//         customFields.push({
//           fieldName: key,
//           fieldLabel: key.replace('custom_', '').replace(/_/g, ' '),
//           fieldType: 'text',
//           isCustom: true
//         });
//       }
//     });
//     if (customFields.length > 0) {
//       this.customFieldsByItem.set(itemIndex, customFields);
//       console.log('%c[CUSTOM FIELDS RESTORED]', 'color: #00aa00;', {
//         item: itemIndex,
//         count: customFields.length
//       });
//     }
//   }
  
//   // Load attachments if item has an ID
//   if (item?.id) {
//     this.loadItemAttachments(itemIndex, item.id);
//   }
  
//   console.log('%c[ITEM ADDED]', 'color: #00aa00;', {
//     index: this.itemsArray.length,
//     companyType: this.buyerCompanyType,
//     dynamicFields: this.dynamicFieldsForCurrentCompanyType.length,
//     hasId: !!item?.id,
//     mode: this.mode
//   });
  
//   this.cdr.markForCheck();
// }

  removeItem(index: number): void {
    if (this.itemsArray.length > 1) {
      this.itemsArray.removeAt(index);
      this.customFieldsByItem.delete(index);
      delete this.showAddFieldForm[index];
      delete this.addFieldForm[index];
      this.selectedFiles.delete(index);
      this.uploadingFiles.delete(index);
      this.itemAttachments.delete(index);
      this.uploadProgress.delete(index);
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

  // ==================== SUPPLIER SELECTION METHODS ====================

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

    this.selectedSupplierToAdd = null;
    this.cdr.markForCheck();
  }

  removeSupplierFromSelection(supplierId: number): void {
    const index = this.selectedSupplierIds.indexOf(supplierId);
    
    if (index > -1) {
      this.selectedSupplierIds.splice(index, 1);
      this.rfqForm.get('selectedSupplierIds')?.setValue(this.selectedSupplierIds);
      
      this.messageService.showMessage('info', 'Removed', 'Supplier removed from selection');
      this.cdr.markForCheck();
    }
  }

  getSupplierById(supplierId: number): any {
    return this.filteredSuppliers.find(s => s.id === supplierId) || 
           this.suppliers.find(s => s.id === supplierId);
  }

  trackBySupplier(index: number, supplier: any): number {
    return supplier.id;
  }

  trackBySupplierId(index: number, supplierId: number): number {
    return supplierId;
  }

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

  // private loadRFQData(id: number): void {
  //   console.log('%c[LOADING RFQ DATA]', 'color: #0066cc; font-weight: bold;', {
  //     id: id,
  //     suppliersLoaded: this.dataLoadingState.suppliersLoaded,
  //     departmentsCount: this.suppliersByDepartment.size
  //   });

  //   if (!this.dataLoadingState.suppliersLoaded) {
  //     console.warn('%c[WAITING FOR SUPPLIERS]', 'color: #ff9800;');
  //     setTimeout(() => this.loadRFQData(id), 200);
  //     return;
  //   }

  //   this.rfqService.getRFQById(id).subscribe({
  //     next: (response: any) => {
  //       const rfq = response?.data;

  //       console.log('%c[RFQ DATA RECEIVED]', 'color: #00aa00;', {
  //         title: rfq?.rfqTitle,
  //         items: rfq?.items?.length || 0,
  //         suppliers: rfq?.selectedSuppliers?.length || 0,
  //         status: rfq?.status,
  //         fullRfq: rfq
  //       });

  //       if (!rfq) {
  //         this.messageService.showMessage('error', 'Error', 'RFQ not found');
  //         this.router.navigate(['/rfq-dashboard']);
  //         return;
  //       }

  //       // ✅ STEP 1: Set suppliers FIRST
  //       this.selectedCategory = '';
  //       this.selectedSupplierIds = [];
  //       this.filteredSuppliers = [];
  //       this.filteredSuppliersForDropdown = [];

  //       if (rfq.selectedSuppliers && Array.isArray(rfq.selectedSuppliers) && rfq.selectedSuppliers.length > 0) {
  //         console.log('%c[LOADING SUPPLIERS]', 'color: #0066cc;', rfq.selectedSuppliers);
          
  //         const supplierIds = rfq.selectedSuppliers.map((s: any) => s.id);
  //         const firstSupplierId = rfq.selectedSuppliers[0]?.id;
          
  //         console.log('%c[SUPPLIER IDS]', 'color: #9c27b0;', supplierIds);
  //         console.log('%c[FINDING CATEGORY FOR]', 'color: #0066cc;', firstSupplierId);
          
  //         let foundCategory = '';

  //         for (const [dept, suppliers] of this.suppliersByDepartment.entries()) {
  //           const found = suppliers.find((s: any) => s.id === firstSupplierId);
  //           if (found) {
  //             foundCategory = dept;
  //             console.log('%c[✓ CATEGORY FOUND]', 'color: #00aa00;', dept);
  //             break;
  //           }
  //         }

  //         if (foundCategory) {
  //           this.selectedCategory = foundCategory;
  //           this.filteredSuppliers = this.suppliersByDepartment.get(foundCategory) || [];
  //           this.filteredSuppliersForDropdown = [...this.filteredSuppliers];
  //           this.selectedSupplierIds = [...supplierIds];
            
  //           console.log('%c[✓ SUPPLIERS SET]', 'color: #00aa00;', {
  //             category: foundCategory,
  //             filteredSuppliers: this.filteredSuppliers.length,
  //             selectedIds: this.selectedSupplierIds
  //           });

  //           this.updateSupplierPagination();
  //         } else {
  //           console.warn('%c[WARNING] Category not found', 'color: #ff9800;', {
  //             firstSupplierId,
  //             availableDepts: Array.from(this.suppliersByDepartment.keys())
  //           });
            
  //           this.selectedSupplierIds = [...supplierIds];
  //         }
  //       } else {
  //         console.log('%c[NO SUPPLIERS IN RFQ]', 'color: #ff9800;');
  //       }

  //       // ✅ STEP 2: Patch form values
  //       this.rfqForm.patchValue({
  //         rfqTitle: rfq.rfqTitle || '',
  //         rfqDescription: rfq.rfqDescription || '',
  //         priority: rfq.priority || 'MEDIUM',
  //         locationId: rfq.location?.id || '',
  //         paymentTerms: rfq.paymentTerms || '',
  //         deliveryTerms: rfq.deliveryTerms || '',
  //         justification: rfq.justification || '',
  //         allowSplitPO: rfq.allowSplitPO || false,
  //         preferredVendorsOnly: rfq.preferredVendorsOnly || false,
  //         approvalRequired: true,
  //         selectedSupplierIds: this.selectedSupplierIds
  //       }, { emitEvent: false });

  //       if (rfq.location?.id) {
  //         this.userLocation = rfq.location;
  //       }

  //       // ✅ STEP 3: Load items
  //       console.log('%c[LOADING ITEMS]', 'color: #0066cc;', rfq.items?.length || 0, 'items');
        
  //       while (this.itemsArray.length > 0) {
  //         this.itemsArray.removeAt(0);
  //       }
        
  //       if (rfq.items && Array.isArray(rfq.items) && rfq.items.length > 0) {
  //         rfq.items.forEach((item: any, index: number) => {
  //           console.log('%c[LOADING ITEM]', 'color: #9c27b0;', index + 1, item);
            
  //           const rfqItem: any = {
  //             id: item.id,
  //             itemCode: item.itemCode || '',
  //             itemDescription: item.itemDescription || '',
  //             quantity: item.quantity || 1,
  //             uom: item.uom || 'Qty',
  //             unitPrice: item.unitPrice || 0,
  //             lineTotal: item.lineTotal || 0,
  //             itemRequiredDate: item.itemRequiredDate || null,
  //             itemDescriptionDetailed: item.itemDescriptionDetailed || '',
  //             specifications: item.specifications || '',
  //             itemOrder: item.itemOrder || (index + 1),
  //             companyType: item.companyType || this.buyerCompanyType,
  //             dynamicFields: item.dynamicFields || {}
  //           };
            
  //           this.addItem(rfqItem);
  //         });

  //         console.log('%c[✓ ITEMS LOADED]', 'color: #00aa00;', this.itemsArray.length);
  //       } else {
  //         console.log('%c[NO ITEMS]', 'color: #ff9800;', 'Adding default item');
  //         this.addItem();
  //       }

  //       // ✅ STEP 4: Disable form in view mode
  //       if (this.mode === 'view') {
  //         this.rfqForm.disable({ emitEvent: false });
  //       }

  //       // ✅ STEP 5: Mark as loaded
  //       this.dataLoadingState.rfqDataLoaded = true;
        
  //       // ✅ STEP 6: Force UI update
  //       setTimeout(() => {
  //         this.cdr.detectChanges();
          
  //         console.log('%c[✅ RFQ DATA FULLY LOADED & UI UPDATED]', 'color: #00aa00; font-weight: bold;', {
  //           items: this.itemsArray.length,
  //           suppliers: this.selectedSupplierIds.length,
  //           category: this.selectedCategory,
  //           filteredSuppliers: this.filteredSuppliers.length,
  //           formValue: this.rfqForm.value
  //         });
  //       }, 100);
  //     },
  //     error: (error: any) => {
  //       console.error('%c[LOAD RFQ ERROR]', 'color: #cc0000;', error);
  //       this.messageService.showMessage('error', 'Error', 'Failed to load RFQ');
  //       this.router.navigate(['/rfq-dashboard']);
  //     }
  //   });
  // }
  // ==================== CRITICAL FIX: LOAD RFQ DATA (FOR EDIT/VIEW) ====================
// Replace the loadRFQData method in your create-rfq.component.ts with this fixed version

// ==================== COMPLETE FIX FOR create-rfq.component.ts ====================
// This file contains ALL the methods you need to replace in your component

// ==================== 1. REPLACE THE ENTIRE loadRFQData METHOD ====================

private loadRFQData(id: number): void {
  console.log('%c[═══════════════════════════════════════]', 'color: #0066cc; font-weight: bold;');
  console.log('%c[LOADING RFQ DATA - ID: ' + id + ']', 'color: #0066cc; font-weight: bold;');
  console.log('%c[═══════════════════════════════════════]', 'color: #0066cc; font-weight: bold;');

  if (!this.dataLoadingState.suppliersLoaded) {
    console.warn('%c[⏳ WAITING FOR SUPPLIERS...]', 'color: #ff9800;');
    setTimeout(() => this.loadRFQData(id), 200);
    return;
  }

  this.rfqService.getRFQById(id).subscribe({
    next: (response: any) => {
      console.log('%c[📥 RAW API RESPONSE]', 'color: #9c27b0; font-weight: bold;', response);
      
      const rfq = response?.data || response;
      
      if (!rfq || !rfq.id) {
        console.error('%c[❌ NO RFQ DATA]', 'color: #cc0000; font-weight: bold;');
        this.messageService.showMessage('error', 'Error', 'RFQ not found');
        this.router.navigate(['/rfq-dashboard']);
        return;
      }

      console.log('%c[📊 RFQ OBJECT]', 'color: #00aa00; font-weight: bold;', {
        id: rfq.id,
        rfqNumber: rfq.rfqNumber,
        rfqTitle: rfq.rfqTitle,
        rfqDescription: rfq.rfqDescription,
        priority: rfq.priority,
        status: rfq.status,
        approvalRequired: rfq.approvalRequired,
        paymentTerms: rfq.paymentTerms,
        deliveryTerms: rfq.deliveryTerms,
        justification: rfq.justification,
        allowSplitPO: rfq.allowSplitPO,
        preferredVendorsOnly: rfq.preferredVendorsOnly,
        location: rfq.location,
        buyer: rfq.buyer,
        items: rfq.items?.length || 0,
        selectedSuppliers: rfq.selectedSuppliers?.length || 0
      });

      // ========================================
      // STEP 1: RESTORE LOCATION
      // ========================================
      console.log('%c[📍 STEP 1: LOADING LOCATION]', 'color: #0066cc; font-weight: bold;');
      
      if (rfq.location) {
        this.userLocation = {
          id: rfq.location.id,
          locationName: rfq.location.locationName || 'N/A',
          city: rfq.location.city || 'N/A',
          state: rfq.location.state || 'N/A',
          postalCode: rfq.location.postalCode || '',
          country: rfq.location.country || ''
        };
        console.log('%c[✅ Location Set]', 'color: #00aa00;', this.userLocation);
      } else {
        console.warn('%c[⚠️ No location in RFQ]', 'color: #ff9800;');
      }

      // ========================================
      // STEP 2: RESTORE SUPPLIERS
      // ========================================
      console.log('%c[🏢 STEP 2: LOADING SUPPLIERS]', 'color: #0066cc; font-weight: bold;');
      
      this.selectedCategory = '';
      this.selectedSupplierIds = [];
      this.filteredSuppliers = [];
      this.filteredSuppliersForDropdown = [];

      if (rfq.selectedSuppliers && Array.isArray(rfq.selectedSuppliers) && rfq.selectedSuppliers.length > 0) {
        console.log('%c[📋 Selected Suppliers from API]', 'color: #9c27b0;', rfq.selectedSuppliers);
        
        const supplierIds = rfq.selectedSuppliers.map((s: any) => s.id);
        const firstSupplierId = rfq.selectedSuppliers[0]?.id;
        
        console.log('%c[🔍 Finding category for supplier ID: ' + firstSupplierId + ']', 'color: #0066cc;');
        console.log('%c[Available Departments]', 'color: #9c27b0;', Array.from(this.suppliersByDepartment.keys()));
        
        let foundCategory = '';

        // Search for category
        for (const [dept, suppliers] of this.suppliersByDepartment.entries()) {
          const found = suppliers.find((s: any) => s.id === firstSupplierId);
          if (found) {
            foundCategory = dept;
            console.log('%c[✅ CATEGORY FOUND: ' + dept + ']', 'color: #00aa00; font-weight: bold;');
            break;
          }
        }

        if (foundCategory) {
          this.selectedCategory = foundCategory;
          this.filteredSuppliers = this.suppliersByDepartment.get(foundCategory) || [];
          this.filteredSuppliersForDropdown = [...this.filteredSuppliers];
          this.selectedSupplierIds = [...supplierIds];
          
          console.log('%c[✅ Suppliers Restored]', 'color: #00aa00;', {
            category: this.selectedCategory,
            filteredCount: this.filteredSuppliers.length,
            selectedIds: this.selectedSupplierIds,
            selectedCount: this.selectedSupplierIds.length
          });
          
          this.updateSupplierPagination();
        } else {
          console.warn('%c[⚠️ Category not found - setting IDs only]', 'color: #ff9800;');
          this.selectedSupplierIds = [...supplierIds];
        }
      } else {
        console.log('%c[ℹ️ No suppliers selected in RFQ]', 'color: #0066cc;');
      }

      // ========================================
      // STEP 3: PATCH FORM VALUES
      // ========================================
      console.log('%c[📝 STEP 3: PATCHING FORM VALUES]', 'color: #0066cc; font-weight: bold;');
      
      const formData = {
        rfqTitle: rfq.rfqTitle || '',
        rfqDescription: rfq.rfqDescription || '',
        priority: rfq.priority || 'MEDIUM',
        locationId: rfq.location?.id || '',
        paymentTerms: rfq.paymentTerms || '',
        deliveryTerms: rfq.deliveryTerms || '',
        justification: rfq.justification || '',
        allowSplitPO: rfq.allowSplitPO === true,
        preferredVendorsOnly: rfq.preferredVendorsOnly === true,
        approvalRequired: rfq.approvalRequired !== false,
        selectedSupplierIds: this.selectedSupplierIds
      };

      console.log('%c[Form Data to Patch]', 'color: #9c27b0;', formData);
      
      this.rfqForm.patchValue(formData, { emitEvent: false });
      
      console.log('%c[✅ Form Patched]', 'color: #00aa00;');
      console.log('%c[Current Form Value]', 'color: #9c27b0;', this.rfqForm.value);

      // ========================================
      // STEP 4: CLEAR AND LOAD ITEMS
      // ========================================
      console.log('%c[📦 STEP 4: LOADING ITEMS]', 'color: #0066cc; font-weight: bold;');
      
      // Clear existing items
      while (this.itemsArray.length > 0) {
        this.itemsArray.removeAt(0);
      }
      console.log('%c[🗑️ Cleared existing items]', 'color: #ff9800;');
      
      if (rfq.items && Array.isArray(rfq.items) && rfq.items.length > 0) {
        console.log('%c[📋 Processing ' + rfq.items.length + ' items]', 'color: #0066cc;');
        
        rfq.items.forEach((item: any, index: number) => {
          console.log('%c[Item ' + (index + 1) + ']', 'color: #9c27b0; font-weight: bold;', item);
          
          // Map ALL item fields
          const rfqItem: any = {
            id: item.id,
            itemCode: item.itemCode || '',
            itemDescription: item.itemDescription || '',
            quantity: item.quantity || 1,
            uom: item.uom || 'Qty',
            unitPrice: item.unitPrice || 0,
            lineTotal: item.lineTotal || 0,
            itemRequiredDate: item.itemRequiredDate || null,
            itemDescriptionDetailed: item.itemDescriptionDetailed || '',
            specifications: item.specifications || '',
            itemOrder: item.itemOrder || (index + 1),
            companyType: item.companyType || this.buyerCompanyType || 'Others',
            dynamicFields: item.dynamicFields || {}
          };
          
          console.log('%c[Mapped Item Data]', 'color: #00aa00;', rfqItem);
          
          // Add item using addItem method
          this.addItem(rfqItem);
        });

        console.log('%c[✅ All Items Loaded: ' + this.itemsArray.length + ']', 'color: #00aa00; font-weight: bold;');
      } else {
        console.log('%c[ℹ️ No items in RFQ - adding default item]', 'color: #0066cc;');
        this.addItem();
      }

      // ========================================
      // STEP 5: SET MODE (VIEW/EDIT)
      // ========================================
      console.log('%c[🔒 STEP 5: SETTING MODE]', 'color: #0066cc; font-weight: bold;');
      
      if (this.mode === 'view') {
        this.rfqForm.disable({ emitEvent: false });
        console.log('%c[✅ Form Disabled (VIEW MODE)]', 'color: #00aa00;');
      } else {
        console.log('%c[✅ Form Enabled (EDIT MODE)]', 'color: #00aa00;');
      }

      // ========================================
      // STEP 6: MARK AS LOADED
      // ========================================
      this.dataLoadingState.rfqDataLoaded = true;
      
      // ========================================
      // STEP 7: FINAL VERIFICATION & UI UPDATE
      // ========================================
      setTimeout(() => {
        console.log('%c[═══════════════════════════════════════]', 'color: #00aa00; font-weight: bold;');
        console.log('%c[✅ RFQ DATA LOADING COMPLETE]', 'color: #00aa00; font-weight: bold;');
        console.log('%c[═══════════════════════════════════════]', 'color: #00aa00; font-weight: bold;');
        console.log('%c[Final State]', 'color: #9c27b0; font-weight: bold;', {
          mode: this.mode,
          rfqId: rfq.id,
          rfqNumber: rfq.rfqNumber,
          itemsCount: this.itemsArray.length,
          suppliersCount: this.selectedSupplierIds.length,
          selectedCategory: this.selectedCategory,
          formValue: this.rfqForm.value,
          formValid: this.rfqForm.valid
        });
        
        this.cdr.detectChanges();
      }, 100);
    },
    error: (error: any) => {
      console.error('%c[❌ LOAD RFQ ERROR]', 'color: #cc0000; font-weight: bold;', error);
      this.messageService.showMessage('error', 'Error', 'Failed to load RFQ: ' + (error.message || 'Unknown error'));
      this.router.navigate(['/rfq-dashboard']);
    }
  });
}

// ==================== 2. ENHANCE THE addItem METHOD ====================

addItem(item?: RFQItem): void {
  console.log('%c[➕ ADDING ITEM]', 'color: #0066cc; font-weight: bold;', item);

  const dynamicFieldsGroup: { [key: string]: any } = {};

  // Add predefined dynamic fields
  this.dynamicFieldsForCurrentCompanyType.forEach(field => {
    const fieldValue = item?.dynamicFields?.[field.fieldName] || '';
    dynamicFieldsGroup[field.fieldName] = [fieldValue];
  });

  // Add custom fields
  if (item?.dynamicFields) {
    Object.keys(item.dynamicFields).forEach(key => {
      if (key.startsWith('custom_') && item.dynamicFields) {
        dynamicFieldsGroup[key] = [item.dynamicFields[key]];
      }
    });
  }

  console.log('%c[Dynamic Fields Group]', 'color: #9c27b0;', dynamicFieldsGroup);

  // Create the item form group
  const itemGroup = this.fb.group({
    id: [item?.id || null],
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

  // Restore custom fields if present
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
      console.log('%c[Custom Fields Restored]', 'color: #00aa00;', customFields);
    }
  }
  
  // Load attachments if item has an ID
  if (item?.id) {
    console.log('%c[📎 Loading attachments for item: ' + item.id + ']', 'color: #0066cc;');
    this.loadItemAttachments(itemIndex, item.id);
  }
  
  console.log('%c[✅ Item Added at index: ' + itemIndex + ']', 'color: #00aa00;', {
    itemDescription: item?.itemDescription,
    quantity: item?.quantity,
    uom: item?.uom,
    specifications: item?.specifications,
    dynamicFieldsCount: Object.keys(dynamicFieldsGroup).length,
    hasId: !!item?.id
  });
  
  this.cdr.markForCheck();
}

// ==================== 3. ADD THIS METHOD FOR DEBUGGING ====================

/**
 * Debug method - call this from browser console to check form state
 * Usage: In browser console type: window['rfqComponent'].debugFormState()
 */
debugFormState(): void {
  console.log('%c[═══════════════════════════════════════]', 'color: #0066cc; font-weight: bold;');
  console.log('%c[🐛 DEBUG FORM STATE]', 'color: #0066cc; font-weight: bold;');
  console.log('%c[═══════════════════════════════════════]', 'color: #0066cc; font-weight: bold;');
  
  console.log('%c[Mode]', 'color: #9c27b0; font-weight: bold;', this.mode);
  console.log('%c[RFQ ID]', 'color: #9c27b0; font-weight: bold;', this.rfqId);
  
  console.log('%c[Form Value]', 'color: #9c27b0; font-weight: bold;', this.rfqForm.value);
  console.log('%c[Form Valid]', 'color: #9c27b0; font-weight: bold;', this.rfqForm.valid);
  console.log('%c[Form Disabled]', 'color: #9c27b0; font-weight: bold;', this.rfqForm.disabled);
  
  console.log('%c[Items Count]', 'color: #9c27b0; font-weight: bold;', this.itemsArray.length);
  console.log('%c[Items Array]', 'color: #9c27b0; font-weight: bold;', this.itemsArray.value);
  
  console.log('%c[Selected Category]', 'color: #9c27b0; font-weight: bold;', this.selectedCategory);
  console.log('%c[Selected Supplier IDs]', 'color: #9c27b0; font-weight: bold;', this.selectedSupplierIds);
  console.log('%c[Filtered Suppliers Count]', 'color: #9c27b0; font-weight: bold;', this.filteredSuppliers.length);
  
  console.log('%c[User Location]', 'color: #9c27b0; font-weight: bold;', this.userLocation);
  console.log('%c[Buyer Company Type]', 'color: #9c27b0; font-weight: bold;', this.buyerCompanyType);
  
  console.log('%c[Data Loading State]', 'color: #9c27b0; font-weight: bold;', this.dataLoadingState);
}

// ==================== 4. ENHANCE ngOnInit ====================

ngOnInit(): void {
  console.log('%c[🚀 CREATE RFQ COMPONENT INITIALIZED]', 'color: #0066cc; font-weight: bold;');
  
  // Make component accessible from browser console for debugging
  (window as any)['rfqComponent'] = this;
  
  this.initializeForm();
  this.detectMode();
  this.loadLoggedInBuyer();
  this.loadSuppliers();
}

// ==================== 5. ADD HELPER METHOD FOR FORM VALIDATION ====================

isFormValid(): boolean {
  const valid = this.rfqForm.valid && 
                this.itemsArray.length > 0 && 
                this.getSelectedSuppliersCount() > 0;
  
  if (!valid) {
    console.log('%c[⚠️ FORM VALIDATION FAILED]', 'color: #ff9800; font-weight: bold;');
    console.log('Form Valid:', this.rfqForm.valid);
    console.log('Items Count:', this.itemsArray.length);
    console.log('Suppliers Count:', this.getSelectedSuppliersCount());
    
    // Log specific field errors
    Object.keys(this.rfqForm.controls).forEach(key => {
      const control = this.rfqForm.get(key);
      if (control && control.invalid) {
        console.log(`Field "${key}" errors:`, control.errors);
      }
    });
  }
  
  return valid;
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

  // isFormValid(): boolean {
  //   return this.rfqForm.valid && this.itemsArray.length > 0 && this.getSelectedSuppliersCount() > 0;
  // }

  areItemsValid(): boolean {
    return this.itemsArray.controls.every((ctrl: any) => ctrl.valid);
  }

  // ==================== BUILD PAYLOAD ====================

  private buildPayload(): RFQCreatePayload {
    const form = this.rfqForm.value;

    return {
      rfqTitle: form.rfqTitle?.trim() || '',
      rfqDescription: form.rfqDescription?.trim() || '',
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
      
      const errors = [];
      if (!this.rfqForm.get('rfqTitle')?.valid) errors.push('RFQ Title');
      if (this.itemsArray.length === 0) errors.push('At least one Item');
      if (this.selectedSupplierIds.length === 0) errors.push('At least one Supplier');
      
      this.messageService.showMessage('error', 'Validation Error',
        `Please fill required fields: ${errors.join(', ')}`);
      return;
    }

    let totalFilesToUpload = 0;
    this.selectedFiles.forEach((files) => {
      totalFilesToUpload += files.length;
    });

    console.log('%c[SUBMITTING RFQ]', 'color: #0066cc; font-weight: bold;', {
      mode: this.mode,
      rfqId: this.rfqId,
      itemsCount: this.itemsArray.length,
      suppliersCount: this.selectedSupplierIds.length,
      filesCount: totalFilesToUpload,
      formValue: this.rfqForm.value
    });

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
    console.log('%c[CREATE RFQ FLOW]', 'color: #0066cc; font-weight: bold;', {
      buyerId,
      locationId,
      userId,
      payload
    });

    this.rfqService.createRFQ(buyerId, locationId, userId, payload).subscribe({
      next: (response: any) => {
        const rfqId = response.data?.id;

        if (!rfqId) {
          console.error('%c[ERROR] No RFQ ID in response', 'color: #cc0000;', response);
          this.messageService.showMessage('error', 'Error', 'Failed to create RFQ');
          this.isSubmitting = false;
          return;
        }

        console.log('%c[✓ RFQ CREATED]', 'color: #00aa00; font-weight: bold;', {
          rfqId,
          rfqNumber: response.data?.rfqNumber
        });

        this.addItemsToRFQWithUploads(rfqId, this.rfqForm.get('itemsArray')?.value)
          .then(() => {
            console.log('%c[✓ ITEMS ADDED]', 'color: #00aa00;');
            return this.addSuppliersToRFQ(rfqId, this.selectedSupplierIds);
          })
          .then(() => {
            console.log('%c[✓ SUPPLIERS ADDED]', 'color: #00aa00;');
            
            this.messageService.showMessage('success', 'Success', 
              `${response.data?.rfqNumber} created successfully!`);
            
            setTimeout(() => this.router.navigate(['/rfq-dashboard']), 1500);
          })
          .catch((error: any) => {
            console.error('%c[SUBMISSION ERROR]', 'color: #cc0000;', error);
            this.messageService.showMessage('error', 'Error', 
              error.message || 'Failed to complete RFQ creation');
            this.isSubmitting = false;
          });
      },
      error: (error: any) => {
        console.error('%c[CREATE RFQ ERROR]', 'color: #cc0000;', error);
        const errorMsg = error.status === 400 ? 
          (error.error?.message || 'Invalid RFQ data') : 
          'Failed to create RFQ';
        this.messageService.showMessage('error', 'Error', errorMsg);
        this.isSubmitting = false;
      }
    });
  }

  private updateRFQFlow(rfqId: number, payload: RFQCreatePayload): void {
    console.log('%c[UPDATE RFQ FLOW]', 'color: #0066cc; font-weight: bold;', {
      rfqId,
      payload
    });

    this.rfqService.updateRFQ(rfqId, payload).subscribe({
      next: () => {
        console.log('%c[✓ RFQ UPDATED]', 'color: #00aa00; font-weight: bold;', rfqId);

        this.addItemsToRFQWithUploads(rfqId, this.rfqForm.get('itemsArray')?.value)
          .then(() => {
            console.log('%c[✓ ITEMS UPDATED]', 'color: #00aa00;');
            return this.addSuppliersToRFQ(rfqId, this.selectedSupplierIds);
          })
          .then(() => {
            console.log('%c[✓ SUPPLIERS UPDATED]', 'color: #00aa00;');
            
            this.messageService.showMessage('success', 'Success', 
              'RFQ updated successfully!');
            
            setTimeout(() => this.router.navigate(['/rfq-dashboard']), 1500);
          })
          .catch((error: any) => {
            console.error('%c[UPDATE ERROR]', 'color: #cc0000;', error);
            this.messageService.showMessage('error', 'Error', 
              error.message || 'Failed to update RFQ');
            this.isSubmitting = false;
          });
      },
      error: (error: any) => {
        console.error('%c[UPDATE ERROR]', 'color: #cc0000;', error);
        this.messageService.showMessage('error', 'Error', 
          error.error?.message || 'Failed to update RFQ');
        this.isSubmitting = false;
      }
    });
  }

  private addItemsToRFQWithUploads(rfqId: number, items: any[]): Promise<void> {
    return new Promise(async (resolve, reject) => {
      if (!items || items.length === 0) {
        console.warn('%c[WARNING] No items to add', 'color: #ff9800;');
        resolve();
        return;
      }

      try {
        console.log('%c[ADDING ITEMS WITH UPLOADS]', 'color: #0066cc; font-weight: bold;', {
          rfqId,
          itemCount: items.length,
          itemsWithFiles: Array.from(this.selectedFiles.keys()).length
        });

        const createdItemIds: number[] = [];

        for (let itemIndex = 0; itemIndex < items.length; itemIndex++) {
          const item = items[itemIndex];
          
          const itemPayload = {
            itemDescription: item.itemDescription,
            quantity: Number(item.quantity) || 1,
            uom: item.uom || 'Qty',
            specifications: item.specifications || '',
            dynamicFields: item.dynamicFields || {},
            companyType: item.companyType || this.buyerCompanyType || 'Others'
          };

          console.log(`%c[ADDING ITEM ${itemIndex + 1}/${items.length}]`, 'color: #0066cc;', {
            description: item.itemDescription,
            payload: itemPayload
          });

          try {
            const itemResponse = await this.rfqService.addItemToRFQ(rfqId, itemPayload).toPromise();
            
            console.log('%c[ITEM RESPONSE]', 'color: #00aa00;', itemResponse);
            
            const createdItemId = itemResponse?.data?.id || itemResponse?.id;

            if (!createdItemId) {
              console.error('%c[ERROR] No item ID returned', 'color: #cc0000;', itemResponse);
              throw new Error(`Failed to create item: ${item.itemDescription}`);
            }

            createdItemIds.push(createdItemId);

            const itemGroup = this.itemsArray.at(itemIndex) as FormGroup;
            if (itemGroup) {
              itemGroup.patchValue({ id: createdItemId }, { emitEvent: false });
            }

            console.log(`%c[✓ ITEM CREATED]`, 'color: #00aa00;', {
              itemIndex,
              itemId: createdItemId,
              description: item.itemDescription
            });

            const files = this.selectedFiles.get(itemIndex);
            
            if (files && files.length > 0) {
              console.log(`%c[UPLOADING FILES FOR ITEM]`, 'color: #0066cc;', {
                itemIndex,
                itemId: createdItemId,
                fileCount: files.length
              });

              for (const file of files) {
                try {
                  console.log(`  → Uploading: ${file.name}`);
                  
                  await new Promise<void>((resolveUpload, rejectUpload) => {
                    this.rfqService.uploadItemAttachment(createdItemId, file).subscribe({
                      next: (event: any) => {
                        if (event.type === HttpEventType.UploadProgress) {
                          const progress = Math.round((100 * event.loaded) / event.total);
                          console.log(`  ⏳ Progress: ${progress}% - ${file.name}`);
                        } else if (event.type === HttpEventType.Response) {
                          console.log(`  ✓ Uploaded: ${file.name}`);
                          resolveUpload();
                        }
                      },
                      error: (error) => {
                        console.error(`  ✗ Failed: ${file.name}`, error);
                        rejectUpload(error);
                      }
                    });
                  });
                } catch (uploadError) {
                  console.error(`%c[UPLOAD ERROR]`, 'color: #cc0000;', {
                    file: file.name,
                    error: uploadError
                  });
                }
              }

              console.log(`%c[✓ ALL FILES UPLOADED]`, 'color: #00aa00;', {
                itemIndex,
                itemId: createdItemId,
                filesUploaded: files.length
              });

              this.selectedFiles.delete(itemIndex);
            }

          } catch (itemError) {
            console.error(`%c[ERROR CREATING ITEM ${itemIndex + 1}]`, 'color: #cc0000;', itemError);
            this.messageService.showMessage('warning', 'Warning', 
              `Failed to create item: ${item.itemDescription}`);
          }
        }

        if (createdItemIds.length === 0) {
          console.error('%c[ERROR] No items were created', 'color: #cc0000;');
          reject(new Error('Failed to create any items'));
          return;
        }

        console.log('%c[✓ ITEMS CREATED]', 'color: #00aa00; font-weight: bold;', {
          total: items.length,
          successful: createdItemIds.length,
          failed: items.length - createdItemIds.length
        });
        
        resolve();

      } catch (error) {
        console.error('%c[ERROR IN ITEMS/UPLOADS]', 'color: #cc0000; font-weight: bold;', error);
        reject(error);
      }
    });
  }

  private addSuppliersToRFQ(rfqId: number, supplierIds: number[]): Promise<void> {
    return new Promise((resolve, reject) => {
      if (!supplierIds || supplierIds.length === 0) {
        console.warn('%c[WARNING] No suppliers to add', 'color: #ff9800;');
        resolve();
        return;
      }

      console.log('%c[ADDING SUPPLIERS]', 'color: #0066cc;', {
        rfqId,
        supplierIds,
        count: supplierIds.length
      });

      this.rfqService.addSuppliersToRFQ(rfqId, supplierIds).subscribe({
        next: (response) => {
          console.log('%c[✓ SUPPLIERS ADDED]', 'color: #00aa00;', response);
          resolve();
        },
        error: (error: any) => {
          console.error('%c[SUPPLIER ADD ERROR]', 'color: #cc0000;', error);
          reject(new Error('Failed to add suppliers'));
        }
      });
    });
  }

  private getCurrentUserId(): number {
    const userId = localStorage.getItem('userId');
    return userId ? Number(userId) : 1;
  }

  // ==================== PREVIEW & LIFECYCLE ====================

  previewAttachment(attachment: RFQItemAttachment): void {
    this.previewFile = attachment;
    
    const url = `${(this.rfqService as any)['API']}/rfq-item-attachment/download/${attachment.id}`;
    
    (this.rfqService as any)['http'].get(url, { 
      headers: (this.rfqService as any)['getAuthHeaders'](),
      responseType: 'blob'
    }).subscribe({
      next: (blob: Blob) => {
        const objectUrl = URL.createObjectURL(blob);
        this.previewUrl = this.sanitizer.sanitize(
          SecurityContext.RESOURCE_URL,
          this.sanitizer.bypassSecurityTrustResourceUrl(objectUrl)
        );
        
        const modalElement = document.getElementById('previewModal');
        if (modalElement) {
          const modal = new (window as any).bootstrap.Modal(modalElement);
          modal.show();
        }
        
        this.cdr.markForCheck();
      },
      error: (error: any) => {
        console.error('%c[PREVIEW ERROR]', 'color: #cc0000;', error);
        this.messageService.showMessage('error', 'Error', 'Failed to preview file');
      }
    });
  }

  trackByAttachmentId(index: number, attachment: RFQItemAttachment): number {
    return attachment.id || index;
  }

  ngOnDestroy(): void {
    if (this.previewUrl) {
      URL.revokeObjectURL(this.previewUrl);
    }
  }
}