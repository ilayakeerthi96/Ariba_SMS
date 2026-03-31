export interface Lead {
  id: number;
  companyName: string;
  address: string;
  // Legacy field
  
  // 👇 THESE MUST BE HERE FOR VIEW MODE TO WORK
  firstName: string;
  middleName: string;
  lastName: string;
  
  jobTitle: string;
  email: string;
  email1: string;
  phone: string;
  phone1: string;
  mobile: string;
  website: string;
  countryRegion: string;
  source: string;
  stage: string;
  technology: string;
  domain: string;
  technicalSkills: string;
  assignedTo: string;
  remarks: string;
  isDeleted: boolean;
  expectedCloseDate: string;
  expectedValue: number;
  expectedCurrencyValue: string;
  probability: number;
  createdOn: string;
  employeeCode: string;
  otherSource?: string;
}

export interface User {
  code: string;
  name: string;
  role: 'sales' | 'manager' | 'management';
  team?: string[]; 
  otherSource?: string;
}