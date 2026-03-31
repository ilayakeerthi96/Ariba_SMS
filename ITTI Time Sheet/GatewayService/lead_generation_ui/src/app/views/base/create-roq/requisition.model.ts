// ==================== RFQ STATUS ENUMS ====================
export enum RFQStatus {
  DRAFT = 'DRAFT',
  PUBLISHED = 'PUBLISHED',
  AWAITING_APPROVAL = 'AWAITING_APPROVAL',
  APPROVED = 'APPROVED',
  RESPONSES_RECEIVED = 'RESPONSES_RECEIVED',
  CLOSED = 'CLOSED',
  CANCELLED = 'CANCELLED'
}

export enum ApprovalStatus {
  PENDING = 'PENDING',
  APPROVED = 'APPROVED',
  REJECTED = 'REJECTED'
}

export enum Priority {
  LOW = 'LOW',
  MEDIUM = 'MEDIUM',
  HIGH = 'HIGH',
  URGENT = 'URGENT'
}

export enum AttachmentType {
  RFQ_DOCUMENT = 'RFQ_DOCUMENT',
  ITEM_ATTACHMENT = 'ITEM_ATTACHMENT',
  TECHNICAL_SPEC = 'TECHNICAL_SPEC',
  TERMS_CONDITIONS = 'TERMS_CONDITIONS'
}

// ==================== RFQ ITEM ====================
export interface RFQItem {
  id?: number;
  itemCode?: string;
  itemDescription: string;
  quantity: number;
  uom: string;
  unitPrice?: number;
  lineTotal?: number;
  itemRequiredDate?: string;
  itemDescriptionDetailed?: string;
  specifications?: string;
  itemOrder?: number;
}

// ==================== RFQ ATTACHMENT ====================
export interface RFQAttachment {
  id?: number;
  fileName: string;
  filePath: string;
  fileSize?: number;
  fileType: string;
  attachmentType: AttachmentType;
  description?: string;
  uploadedBy?: number;
  createdAt?: string;
}

// ==================== RFQ MAIN MODEL ====================
export interface RFQ {
  id?: number;
  rfqNumber?: string;
  rfqTitle: string;
  rfqDescription?: string;
  issueDate?: string;
  dueDate: string;
  itemRequiredDate?: string;
  status?: RFQStatus;
  approvalRequired: boolean;
  approvalStatus?: ApprovalStatus;
  approvedBy?: number;
  approvalDate?: string;
  approvalComments?: string;
  costCenterCode?: string;
  projectCode?: string;
  paymentTerms?: string;
  deliveryTerms?: string;
  taxPercentage?: number;
  justification?: string;
  priority: Priority;
  allowSplitPO: boolean;
  preferredVendorsOnly: boolean;
  
  // Relations
  buyerId?: number;
  locationId?: number;
  createdByUserId?: number;
  selectedSupplierIds?: number[];
  approverIds?: number[];
  
  // Collections
  items: RFQItem[];
  attachments?: RFQAttachment[];
  
  // Metadata
  createdAt?: string;
  updatedAt?: string;
  isDeleted?: boolean;
  deletedAt?: string;
}

// ==================== API RESPONSE MODELS ====================
export interface ApiResponse<T> {
  success: boolean;
  message?: string;
  count?: number;
  data?: T;
}

export interface RFQResponse extends ApiResponse<RFQ> {}
export interface RFQListResponse extends ApiResponse<RFQ[]> {}
export interface RFQItemResponse extends ApiResponse<RFQItem> {}
export interface RFQAttachmentResponse extends ApiResponse<RFQAttachment> {}