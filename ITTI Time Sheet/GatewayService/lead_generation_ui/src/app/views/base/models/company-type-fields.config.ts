
// ==================== INTERFACES ====================

/**
 * Represents a single dynamic field
 */
export interface DynamicField {
  fieldName: string;                           // Unique identifier (camelCase)
  fieldLabel: string;                          // Display label for the field
  fieldType: 'text' | 'number' | 'select' | 'textarea' | 'date'; // Input type
  placeholder?: string;                        // Placeholder text
  options?: string[];                          // Options for select type
  maxLength?: number;                          // Max character length
  isCustom?: boolean;                          // NEW: Mark if this is a user-added custom field
}

/**
 * Represents configuration for a company type
 */
export interface CompanyTypeConfig {
  companyType: string;  // Name of the company type
  fields: DynamicField[];  // Array of dynamic fields for this company type
}

// ==================== CONFIGURATION DATA ====================

export const COMPANY_TYPE_FIELDS_CONFIG: CompanyTypeConfig[] = [
  
  // ==================== IT COMPANY ====================
  {
    companyType: 'IT',
    fields: [
      {
        fieldName: 'software',
        fieldLabel: 'Software/Application Name',
        fieldType: 'text',
        placeholder: 'e.g., Microsoft Office, Adobe Creative Suite',
        maxLength: 100
      },
      {
        fieldName: 'type',
        fieldLabel: 'Software Type',
        fieldType: 'select',
        options: ['System Software', 'Application Software', 'Programming Tools', 'Utility Software', 'Web Application']
      },
      {
        fieldName: 'version',
        fieldLabel: 'Version/Release',
        fieldType: 'text',
        placeholder: 'e.g., 2024.1, v8.5.2',
        maxLength: 50
      },
      {
        fieldName: 'product',
        fieldLabel: 'Product Name',
        fieldType: 'text',
        placeholder: 'e.g., Windows Server, Oracle Database',
        maxLength: 100
      },
      {
        fieldName: 'model',
        fieldLabel: 'Hardware Model',
        fieldType: 'text',
        placeholder: 'e.g., Dell PowerEdge R750, HP ProLiant',
        maxLength: 100
      },
      {
        fieldName: 'licenseType',
        fieldLabel: 'License Type',
        fieldType: 'select',
        options: ['Perpetual', 'Subscription', 'Trial', 'Open Source', 'SaaS']
      },
      {
        fieldName: 'processor',
        fieldLabel: 'Processor Requirement',
        fieldType: 'text',
        placeholder: 'e.g., Intel Xeon, ARM Cortex',
        maxLength: 100
      }
    ]
  },

  // ==================== MANUFACTURING ====================
  {
    companyType: 'Manufacturing',
    fields: [
      {
        fieldName: 'material',
        fieldLabel: 'Material/Raw Material',
        fieldType: 'text',
        placeholder: 'e.g., Stainless Steel, Aluminum, Plastic',
        maxLength: 100
      },
      {
        fieldName: 'thickness',
        fieldLabel: 'Thickness (mm)',
        fieldType: 'number',
        placeholder: 'e.g., 2.5'
      },
      {
        fieldName: 'weight',
        fieldLabel: 'Weight (kg)',
        fieldType: 'number',
        placeholder: 'e.g., 15.5'
      },
      {
        fieldName: 'color',
        fieldLabel: 'Color',
        fieldType: 'text',
        placeholder: 'e.g., Silver, Black, Red',
        maxLength: 50
      },
      {
        fieldName: 'dimensions',
        fieldLabel: 'Dimensions (L x W x H)',
        fieldType: 'text',
        placeholder: 'e.g., 100 x 50 x 25 cm',
        maxLength: 100
      },
      {
        fieldName: 'tolerance',
        fieldLabel: 'Tolerance Level',
        fieldType: 'text',
        placeholder: 'e.g., ±0.05mm',
        maxLength: 50
      },
      {
        fieldName: 'surfaceFinish',
        fieldLabel: 'Surface Finish',
        fieldType: 'select',
        options: ['Polished', 'Matte', 'Brushed', 'Textured', 'Anodized', 'Galvanized']
      },
      {
        fieldName: 'certification',
        fieldLabel: 'Certifications Required',
        fieldType: 'text',
        placeholder: 'e.g., ISO 9001, CE Mark',
        maxLength: 200
      }
    ]
  },

  // ==================== TRADING ====================
  {
    companyType: 'Trading',
    fields: [
      {
        fieldName: 'brand',
        fieldLabel: 'Brand Name',
        fieldType: 'text',
        placeholder: 'e.g., Nike, Sony, Samsung',
        maxLength: 100
      },
      {
        fieldName: 'origin',
        fieldLabel: 'Country of Origin',
        fieldType: 'select',
        options: ['India', 'China', 'USA', 'Germany', 'Japan', 'UAE', 'UK', 'Singapore', 'Others']
      },
      {
        fieldName: 'grade',
        fieldLabel: 'Grade/Quality Level',
        fieldType: 'select',
        options: ['Premium', 'Standard', 'Economy', 'Export Quality', 'Domestic']
      },
      {
        fieldName: 'packaging',
        fieldLabel: 'Packaging Type',
        fieldType: 'text',
        placeholder: 'e.g., Box, Carton, Pallet',
        maxLength: 100
      },
      {
        fieldName: 'warranty',
        fieldLabel: 'Warranty Period (Months)',
        fieldType: 'number',
        placeholder: 'e.g., 12'
      },
      {
        fieldName: 'hsn',
        fieldLabel: 'HSN/SAC Code',
        fieldType: 'text',
        placeholder: 'e.g., 8471 41 90',
        maxLength: 20
      },
      {
        fieldName: 'minOrderQuantity',
        fieldLabel: 'Minimum Order Quantity',
        fieldType: 'number',
        placeholder: 'e.g., 100'
      }
    ]
  },

  // ==================== SERVICES ====================
  {
    companyType: 'Services',
    fields: [
      {
        fieldName: 'serviceCategory',
        fieldLabel: 'Service Category',
        fieldType: 'select',
        options: ['Consulting', 'Maintenance', 'Support', 'Installation', 'Training', 'Audit', 'Testing', 'Development']
      },
      {
        fieldName: 'scope',
        fieldLabel: 'Scope of Service',
        fieldType: 'textarea',
        placeholder: 'Describe the detailed scope of services...',
        maxLength: 500
      },
      {
        fieldName: 'duration',
        fieldLabel: 'Service Duration (Days/Months)',
        fieldType: 'text',
        placeholder: 'e.g., 30 days, 3 months',
        maxLength: 50
      },
      {
        fieldName: 'serviceLevel',
        fieldLabel: 'Service Level Agreement (SLA)',
        fieldType: 'text',
        placeholder: 'e.g., 99.5% uptime, 4-hour response',
        maxLength: 200
      },
      {
        fieldName: 'manpowerRequired',
        fieldLabel: 'Manpower Required',
        fieldType: 'number',
        placeholder: 'Number of personnel needed'
      },
      {
        fieldName: 'skillSet',
        fieldLabel: 'Required Skill Set',
        fieldType: 'text',
        placeholder: 'e.g., Java, Spring Boot, AWS',
        maxLength: 200
      },
      {
        fieldName: 'availability',
        fieldLabel: 'Service Availability',
        fieldType: 'select',
        options: ['24/7', '9AM-6PM IST', '9AM-9PM IST', 'Business Hours', 'Custom']
      }
    ]
  },

  // ==================== DISTRIBUTION ====================
  {
    companyType: 'Distribution',
    fields: [
      {
        fieldName: 'distributionChannel',
        fieldLabel: 'Distribution Channel',
        fieldType: 'select',
        options: ['Retail', 'Wholesale', 'B2B', 'Direct', 'Online', 'Mixed']
      },
      {
        fieldName: 'stockKeepingUnit',
        fieldLabel: 'SKU Code',
        fieldType: 'text',
        placeholder: 'e.g., SKU-2024-001',
        maxLength: 50
      },
      {
        fieldName: 'warehouseCapacity',
        fieldLabel: 'Warehouse Capacity (sq ft)',
        fieldType: 'number',
        placeholder: 'e.g., 50000'
      },
      {
        fieldName: 'leadTime',
        fieldLabel: 'Lead Time (Days)',
        fieldType: 'number',
        placeholder: 'e.g., 7'
      },
      {
        fieldName: 'location',
        fieldLabel: 'Warehouse/Distribution Location',
        fieldType: 'text',
        placeholder: 'e.g., Mumbai, Delhi, Bangalore',
        maxLength: 100
      },
      {
        fieldName: 'returnPolicy',
        fieldLabel: 'Return Policy (Days)',
        fieldType: 'number',
        placeholder: 'e.g., 30'
      },
      {
        fieldName: 'certifications',
        fieldLabel: 'Certifications',
        fieldType: 'text',
        placeholder: 'e.g., ISO 9001, FMCG License',
        maxLength: 200
      }
    ]
  },

  // ==================== RETAIL ====================
  {
    companyType: 'Retail',
    fields: [
      {
        fieldName: 'productCategory',
        fieldLabel: 'Product Category',
        fieldType: 'select',
        options: ['Electronics', 'Clothing', 'Groceries', 'Home & Garden', 'Beauty', 'Sports', 'Toys', 'Books', 'Furniture', 'Others']
      },
      {
        fieldName: 'retailFormat',
        fieldLabel: 'Retail Format',
        fieldType: 'select',
        options: ['Supermarket', 'Specialty Store', 'Department Store', 'Convenience Store', 'Hypermarket']
      },
      {
        fieldName: 'storeSize',
        fieldLabel: 'Store Size (sq ft)',
        fieldType: 'number',
        placeholder: 'e.g., 10000'
      },
      {
        fieldName: 'salesChannels',
        fieldLabel: 'Sales Channels',
        fieldType: 'text',
        placeholder: 'e.g., Physical Store, E-commerce, Mobile App',
        maxLength: 200
      },
      {
        fieldName: 'targetMarket',
        fieldLabel: 'Target Market Segment',
        fieldType: 'select',
        options: ['Budget', 'Mid-Range', 'Premium', 'Luxury', 'Mass Market']
      },
      {
        fieldName: 'invoiceFrequency',
        fieldLabel: 'Invoice/Billing Frequency',
        fieldType: 'select',
        options: ['Weekly', 'Bi-weekly', 'Monthly', 'Quarterly']
      },
      {
        fieldName: 'shelfLife',
        fieldLabel: 'Product Shelf Life',
        fieldType: 'text',
        placeholder: 'e.g., 6 months, 2 years',
        maxLength: 100
      }
    ]
  },

  // ==================== OTHERS ====================
  {
    companyType: 'Others',
    fields: [
      {
        fieldName: 'description',
        fieldLabel: 'Detailed Description',
        fieldType: 'textarea',
        placeholder: 'Please provide details about the requirements...',
        maxLength: 500
      },
      {
        fieldName: 'customField1',
        fieldLabel: 'Custom Field 1',
        fieldType: 'text',
        placeholder: 'Additional information',
        maxLength: 200
      },
      {
        fieldName: 'customField2',
        fieldLabel: 'Custom Field 2',
        fieldType: 'text',
        placeholder: 'Additional information',
        maxLength: 200
      }
    ]
  }
];

// ==================== HELPER FUNCTIONS ====================

/**
 * Get dynamic fields for a specific company type
 * 
 * @param companyType - The company type to get fields for
 * @returns Array of DynamicField objects for the company type
 * 
 * @example
 * const itFields = getFieldsByCompanyType('IT');
 * const manufacturingFields = getFieldsByCompanyType('Manufacturing');
 */
export function getFieldsByCompanyType(companyType: string): DynamicField[] {
  if (!companyType) {
    return getFieldsByCompanyType('Others');
  }

  const config = COMPANY_TYPE_FIELDS_CONFIG.find(
    c => c.companyType.toLowerCase() === companyType.toLowerCase()
  );

  return config?.fields || getFieldsByCompanyType('Others');
}

/**
 * Get all available company types
 * 
 * @returns Array of all company type names
 * 
 * @example
 * const types = getAllCompanyTypes(); // ['IT', 'Manufacturing', 'Trading', ...]
 */
export function getAllCompanyTypes(): string[] {
  return COMPANY_TYPE_FIELDS_CONFIG.map(config => config.companyType);
}

/**
 * Check if a company type exists
 * 
 * @param companyType - The company type to check
 * @returns true if company type exists, false otherwise
 */
export function isValidCompanyType(companyType: string): boolean {
  return COMPANY_TYPE_FIELDS_CONFIG.some(
    c => c.companyType.toLowerCase() === companyType.toLowerCase()
  );
}

/**
 * Get field by company type and field name
 * 
 * @param companyType - The company type
 * @param fieldName - The field name to find
 * @returns The DynamicField object or undefined
 */
export function getFieldByName(companyType: string, fieldName: string): DynamicField | undefined {
  const fields = getFieldsByCompanyType(companyType);
  return fields.find(f => f.fieldName === fieldName);
}

/**
 * Create a custom field with validation
 * Used for user-added custom fields
 * 
 * @param fieldLabel - The display label for the field
 * @param fieldType - The type of field
 * @param options - Options for select type (optional)
 * @param placeholder - Placeholder text (optional)
 * @returns A new DynamicField object marked as custom
 * 
 * @example
 * const customField = createCustomField('Brand Color', 'text', undefined, 'Enter color...');
 * const selectField = createCustomField('Grade', 'select', ['A', 'B', 'C']);
 */
export function createCustomField(
  fieldLabel: string,
  fieldType: 'text' | 'number' | 'select' | 'textarea' | 'date',
  options?: string[],
  placeholder?: string
): DynamicField {
  return {
    fieldName: 'custom_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9),
    fieldLabel: fieldLabel,
    fieldType: fieldType,
    placeholder: placeholder,
    options: options,
    maxLength: fieldType === 'textarea' ? 500 : 255,
    isCustom: true
  };
}

/**
 * Validate custom field configuration
 * 
 * @param field - The custom field to validate
 * @returns Array of validation errors (empty if valid)
 */
export function validateCustomField(field: DynamicField): string[] {
  const errors: string[] = [];

  if (!field.fieldLabel || field.fieldLabel.trim().length === 0) {
    errors.push('Field label is required');
  }

  if (field.fieldLabel && field.fieldLabel.trim().length < 3) {
    errors.push('Field label must be at least 3 characters');
  }

  if (!field.fieldType) {
    errors.push('Field type is required');
  }

  if (field.fieldType === 'select' && (!field.options || field.options.length === 0)) {
    errors.push('Select field must have at least one option');
  }

  if (field.maxLength && (field.maxLength < 10 || field.maxLength > 1000)) {
    errors.push('Max length must be between 10 and 1000');
  }

  return errors;
}