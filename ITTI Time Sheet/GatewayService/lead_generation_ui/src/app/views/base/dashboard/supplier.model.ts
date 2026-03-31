// ==================== SUPPLIER USER MODEL ====================
export interface SupplierUser {
  id?: number;
  departmentIndex?: number;  // Used in form only, not sent to backend
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  designation: string;
  employeeId: string;
  gender?: string;
  dateOfBirth?: string;
  addressLine1?: string;
  addressLine2?: string;
  city?: string;
  state?: string;
  postalCode?: string;
  password?: string;
  isDeleted?: boolean;
  deletedAt?: string;
}

// ==================== SUPPLIER DEPARTMENT MODEL ====================
export interface SupplierDepartment {
  id?: number;
  locationIndex?: number;  // Used in form only, not sent to backend
  departmentName: string;
  departmentDescription?: string;
  categoryOfProducts?: string;
  otherCategoryType?: string;  // Used in form only for custom category
  users?: SupplierUser[];
  isDeleted?: boolean;
  deletedAt?: string;
}

// ==================== SUPPLIER LOCATION MODEL ====================
export interface SupplierLocation {
  id?: number;
  locationName: string;
  locationType: string;
  otherLocationType?: string;  // Used in form only for custom location type
  locationContactName: string;
  locationContactEmail: string;
  locationContactPhone: string;
  addressLine1: string;
  addressLine2?: string;
  city: string;
  state: string;
  postalCode: string;
  country: string;
  landlineNumber?: string;
  faxNumber?: string;
  departments?: SupplierDepartment[];
  isDeleted?: boolean;
  deletedAt?: string;
}

// ==================== SUPPLIER MODEL ====================
export interface Supplier {
  id?: number;
  companyName: string;
  companyType: string;
  otherCompanyType?: string;  // Used in form only for custom company type
  industrySector: string;
  otherIndustrySector?: string;  // Used in form only for custom industry sector
  contactPersonName: string;
  contactPersonDesignation: string;
  contactPersonEmail: string;
  contactPersonPhone: string;
  addressLine1: string;
  addressLine2?: string;
  city: string;
  state: string;
  postalCode: string;
  country: string;
  gstNumber?: string;
  panNumber?: string;
  tanNumber?: string;
  website?: string;
  locations?: SupplierLocation[];
  isDeleted?: boolean;
  deletedAt?: string;
}

// ==================== API RESPONSE MODELS ====================
export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data?: T;
}

export interface SupplierResponse extends ApiResponse<Supplier> {}

export interface SupplierLocationResponse extends ApiResponse<SupplierLocation> {}

export interface SupplierDepartmentResponse extends ApiResponse<SupplierDepartment> {}

export interface SupplierUserResponse extends ApiResponse<SupplierUser> {}