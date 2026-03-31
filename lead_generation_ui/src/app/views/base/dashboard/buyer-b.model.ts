
export interface User {
  id?: number;
  departmentIndex?: number;
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

export interface Department {
  id?: number;
  departmentName: string;
  departmentDescription?: string;
  users?: User[];
  isDeleted?: boolean;
  deletedAt?: string;
}

export interface Location {
  id?: number;
  locationName: string;
  locationType: string;
  otherLocationType?: string;
  locationContactName: string;
  locationContactEmail: string;
  locationContactPhone: string;
  addressLine1: string;
  addressLine2?: string;
  city: string;
  state: string;
  postalCode: string;
  // ✅ CRITICAL: country is used to compare with supplier country for currency determination
  // Rule: same country → buyer's own currency | different country → USD
  country: string;
  landlineNumber?: string;
  faxNumber?: string;
  // ✅ ADDED: currency fields per location
  // These are set during buyer creation (Step 2: Locations)
  // currencyCode/currencySymbol are persisted in the Location table (backend already has these fields)
  currencyCode?: string;    // e.g. 'INR', 'USD', 'EUR', 'GBP', etc.
  currencySymbol?: string;  // e.g. '₹', '$', '€', '£', etc.
  departments?: Department[];
  isDeleted?: boolean;
  deletedAt?: string;
}

export interface Buyer {
  id?: number;
  companyName: string;
  companyType: string;
  otherCompanyType?: string;

  // ✅ Logo fields
  logoFilename?: string;
  logoUrl?: string;
  logoBase64?: string;
  logoContentType?: string;
  logoFile?: File;

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

  organizationCompanyName?: string;
  createdByOrgAdminId?: number;

  locations?: Location[];
  isDeleted?: boolean;
  deletedAt?: string;
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data?: T;
}

export interface BuyerResponse extends ApiResponse<Buyer> {}
export interface LocationResponse extends ApiResponse<Location> {}
export interface DepartmentResponse extends ApiResponse<Department> {}
export interface UserResponse extends ApiResponse<User> {}