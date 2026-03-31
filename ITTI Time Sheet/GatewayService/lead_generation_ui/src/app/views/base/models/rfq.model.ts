// ==================== FILE: src/app/modules/rfq/models/rfq.model.ts ====================

// ==================== RFQ ITEM MODEL ====================
export interface RFQItem {
  id?: number;
  itemDescription: string;
  quantity: number;
  uom: string;
  specifications?: string;
  itemOrder?: number;
  isDeleted?: boolean;
  
  // ==================== DYNAMIC FIELDS ====================
  // These will be populated based on buyer's company type
  dynamicFields?: {
    [key: string]: any; // Key: fieldName, Value: field value
  };
  
  // Metadata
  companyType?: string; // Store the company type this item was created for
}

// ==================== RFQ ATTACHMENT MODEL ====================
export interface RFQAttachment {
  id?: number;
  fileName: string;
  filePath: string;
  fileSize?: number;
  fileType: string;
  attachmentType: 'RFQ_DOCUMENT' | 'ITEM_ATTACHMENT' | 'TECHNICAL_SPEC' | 'TERMS_CONDITIONS';
  description?: string;
  uploadedBy?: number;
  createdAt?: string;
}

// ==================== SUPPLIER IN RFQ ====================
export interface RFQSupplier {
  id: number;
  companyName: string;
  contactPersonName: string;
  contactPersonEmail: string;
  contactPersonPhone: string;
}

// ==================== APPROVER IN RFQ ====================
export interface RFQApprover {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
}

// ==================== BUYER IN RFQ ====================
export interface RFQBuyer {
  id: number;
  companyName: string;
  contactPersonName: string;
  contactPersonEmail: string;
  companyType?: string; // Added to store company type
}

// ==================== LOCATION IN RFQ ====================
export interface RFQLocation {
  id: number;
  locationName: string;
  city: string;
  state: string;
}

// ==================== USER IN RFQ ====================
export interface RFQUser {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
}

// ==================== RFQ MAIN MODEL ====================
export interface RFQ {
  id?: number;
  rfqNumber?: string;
  rfqTitle: string;
  rfqDescription?: string;
  issueDate?: string;
  dueDate?: string;
  itemRequiredDate?: string;
  
  // Status fields
  status?: 'DRAFT' | 'AWAITING_APPROVAL' | 'PUBLISHED' | 'RESPONSES_RECEIVED' | 'CLOSED' | 'CANCELLED';
  approvalRequired?: boolean;
  approvalStatus?: 'PENDING' | 'APPROVED' | 'REJECTED';
  approvedBy?: number;
  approvalDate?: string;
  approvalComments?: string;
  
  // Cost & Terms
  paymentTerms?: string;
  deliveryTerms?: string;
  
  // Business logic
  justification?: string;
  priority?: 'LOW' | 'MEDIUM' | 'HIGH' | 'URGENT';
  allowSplitPO?: boolean;
  preferredVendorsOnly?: boolean;
  
  // Relationships
  buyerId?: number;
  buyer?: RFQBuyer;
  locationId?: number;
  location?: RFQLocation;
  createdByUserId?: number;
  createdByUser?: RFQUser;
  
  // Collections
  selectedSuppliers?: RFQSupplier[];
  selectedSupplierIds?: number[];
  approvers?: RFQApprover[];
  approverIds?: number[];
  items?: RFQItem[];
  attachments?: RFQAttachment[];
  
  // Metadata
  createdAt?: string;
  updatedAt?: string;
  isDeleted?: boolean;
  deletedAt?: string;
}

// ==================== RFQ FORM MODEL ====================
export interface RFQFormData {
  rfqTitle: string;
  rfqDescription: string;
  dueDate: string;
  itemRequiredDate: string;
  priority: 'LOW' | 'MEDIUM' | 'HIGH' | 'URGENT';
  
  buyerId: number;
  locationId: number;
  
  items: RFQItem[];
  
  selectedSupplierIds: number[];
  
  approvalRequired: boolean;
  approverIds: number[];
  paymentTerms: string;
  deliveryTerms: string;
  justification: string;
  allowSplitPO: boolean;
  preferredVendorsOnly: boolean;
}

// ==================== API RESPONSE MODELS ====================
export interface RFQResponse {
  success: boolean;
  message: string;
  data?: RFQ;
  count?: number;
}

export interface RFQListResponse {
  success: boolean;
  message: string;
  data?: RFQ[];
  count?: number;
}

// ==================== CREATE/UPDATE PAYLOAD ====================
export interface RFQCreatePayload {
  rfqTitle: string;
  rfqDescription?: string;
  dueDate: string;
  itemRequiredDate?: string;
  priority?: 'LOW' | 'MEDIUM' | 'HIGH' | 'URGENT';
  paymentTerms?: string | null;
  deliveryTerms?: string | null;
  justification?: string | null;
  allowSplitPO?: boolean;
  preferredVendorsOnly?: boolean;
  approvalRequired?: boolean;
}

// ==================== RFQ ITEM PAYLOAD ====================
export interface RFQItemPayload {
  itemDescription: string;
  quantity: number;
  uom: string;
  specifications?: string;
  itemCode?: string;
  unitPrice?: number;
  itemRequiredDate?: string;
  itemDescriptionDetailed?: string;
  
  // ==================== DYNAMIC FIELDS PAYLOAD ====================
  dynamicFields?: {
    [key: string]: any;
  };
  companyType?: string;
}

// ==================== FILTER MODEL ====================
export interface RFQFilter {
  status?: string;
  priority?: string;
  buyerId?: number;
  locationId?: number;
  dateFrom?: string;
  dateTo?: string;
  searchTerm?: string;
}