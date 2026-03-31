// // ==================== USER MODEL ====================
// export interface User {
//   id?: number;
//   departmentIndex:number,
//   firstName: string;
//   lastName: string;
//   email: string;
//   phone: string;
//   designation: string;
//   employeeId?: string;
//   gender?: string;
//   dateOfBirth?: string;
//   addressLine1?: string;
//   addressLine2?: string;
//   city?: string;
//   state?: string;
//   postalCode?: string;
//   password?: string;
//   isDeleted?: boolean;
//   deletedAt?: string;
// }

// // ==================== DEPARTMENT MODEL ====================
// export interface Department {
//   id?: number;
//   departmentName: string;
//   departmentDescription?: string;
//   users?: User[];
//   isDeleted?: boolean;
//   deletedAt?: string;
// }

// // ==================== LOCATION MODEL ====================
// export interface Location {
//   id?: number;
//   locationName: string;
//   locationType: string;
//   otherLocationType?: string;  // ADD THIS - For custom location type
//   locationContactName: string;
//   locationContactEmail: string;
//   locationContactPhone: string;
//   addressLine1: string;
//   addressLine2?: string;
//   city: string;
//   state: string;
//   postalCode: string;
//   country: string;
//   landlineNumber?: string;
//   faxNumber?: string;
//   departments?: Department[];
//   isDeleted?: boolean;
//   deletedAt?: string;
// }

// // ==================== BUYER MODEL ====================
// export interface Buyer {
//   id?: number;
//   companyName: string;
//   companyType: string;
//   otherCompanyType?: string;  // ADD THIS - For custom company type
//   contactPersonName: string;
//   contactPersonDesignation: string;
//   contactPersonEmail: string;
//   contactPersonPhone: string;
//   addressLine1: string;
//   addressLine2?: string;
//   city: string;
//   state: string;
//   postalCode: string;
//   country: string;
//   gstNumber?: string;
//   panNumber?: string;
//   cinNumber?: string;
//   website?: string;
//   locations?: Location[];
//   isDeleted?: boolean;
//   deletedAt?: string;
//   organizationCompanyName?: string;  // ✅ CRITICAL for hierarchy linking
//   createdByOrgAdminId?: number;
// }

// // ==================== API RESPONSE MODELS ====================
// export interface ApiResponse<T> {
//   success: boolean;
//   message: string;
//   data?: T;
// }

// export interface BuyerResponse extends ApiResponse<Buyer> {}

// export interface LocationResponse extends ApiResponse<Location> {}

// export interface DepartmentResponse extends ApiResponse<Department> {}

// export interface UserResponse extends ApiResponse<User> {}



// ==================== USER MODEL ====================


export interface User {
  id?: number;
  departmentIndex?: number;  // ✅ CHANGED: Made optional (only used in frontend forms)
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  designation: string;
  employeeId?: string;
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

// ==================== DEPARTMENT MODEL ====================
export interface Department {
  id?: number;
  departmentName: string;
  departmentDescription?: string;
  users?: User[];
  isDeleted?: boolean;
  deletedAt?: string;
}

// ==================== LOCATION MODEL ====================
export interface Location {
  id?: number;
  locationName: string;
  locationType: string;
  otherLocationType?: string;  // ✅ For custom location type
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
  departments?: Department[];
  isDeleted?: boolean;
  deletedAt?: string;
}

// ==================== BUYER MODEL ====================
export interface Buyer {
  id?: number;
  companyName: string;
  companyType: string;
  otherCompanyType?: string;  // ✅ For custom company type
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
  cinNumber?: string;
  website?: string;
  
  // ✅ CRITICAL: Organization Admin linkage
  organizationCompanyName?: string;  // ✅ Links buyer to org hierarchy
  createdByOrgAdminId?: number;      // ✅ Tracks which admin created it
  
  locations?: Location[];
  isDeleted?: boolean;
  deletedAt?: string;
}

// ==================== API RESPONSE MODELS ====================
export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data?: T;
}

export interface BuyerResponse extends ApiResponse<Buyer> {}

export interface LocationResponse extends ApiResponse<Location> {}

export interface DepartmentResponse extends ApiResponse<Department> {}

export interface UserResponse extends ApiResponse<User> {}