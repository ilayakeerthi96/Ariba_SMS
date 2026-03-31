// ==================== FILE: buyer-supplier-category-mapping.config.ts ====================
// ==================== PLACE THIS IN: src/app/modules/rfq/models/ ====================

/**
 * Configuration file for mapping buyer categories to allowed supplier categories
 * 
 * This defines which supplier department categories should be visible when creating
 * an RFQ based on the buyer's company type/category.
 * 
 * @example
 * If buyer is 'IT' company, they should only see supplier categories like:
 * 'IT', 'Software', 'Hardware', 'Technology', etc.
 */

export interface CategoryMapping {
  buyerCategory: string;
  allowedSupplierCategories: string[];
  description?: string;
}

/**
 * Main category mapping configuration
 * Add or modify mappings as per your business requirements
 */
export const BUYER_SUPPLIER_CATEGORY_MAPPINGS: CategoryMapping[] = [
  {
    buyerCategory: 'IT',
    allowedSupplierCategories: [
      'IT',
      'Information Technology',
      'Software',
      'Hardware',
      'Technology',
      'Electronics',
      'Computer',
      'Network',
      'Cloud Services',
      'SaaS',
      'Telecom'
    ],
    description: 'IT companies can source from IT and technology-related suppliers'
  },
  {
    buyerCategory: 'Manufacturing',
    allowedSupplierCategories: [
      'Manufacturing',
      'Raw Materials',
      'Industrial',
      'Engineering',
      'Production',
      'Machinery',
      'Tools',
      'Equipment',
      'Metals',
      'Plastics',
      'Chemicals',
      'Components'
    ],
    description: 'Manufacturing companies can source from industrial and material suppliers'
  },
  {
    buyerCategory: 'Trading',
    allowedSupplierCategories: [
      'Trading',
      'Wholesale',
      'Distribution',
      'Import-Export',
      'General Trading',
      'Commodities',
      'Merchants',
      'Bulk Goods'
    ],
    description: 'Trading companies can source from wholesale and distribution suppliers'
  },
  {
    buyerCategory: 'Services',
    allowedSupplierCategories: [
      'Services',
      'Consulting',
      'Professional Services',
      'Business Services',
      'Support',
      'Maintenance',
      'Outsourcing',
      'Staffing',
      'Training',
      'Facility Management'
    ],
    description: 'Service companies can source from service-based suppliers'
  },
  {
    buyerCategory: 'Distribution',
    allowedSupplierCategories: [
      'Distribution',
      'Logistics',
      'Warehousing',
      'Supply Chain',
      'Trading',
      'Transportation',
      'Freight',
      'Wholesale',
      'Storage'
    ],
    description: 'Distribution companies can source from logistics and supply chain suppliers'
  },
  {
    buyerCategory: 'Retail',
    allowedSupplierCategories: [
      'Retail',
      'Consumer Goods',
      'FMCG',
      'Wholesale',
      'Distribution',
      'Products',
      'Merchandise',
      'Branded Goods',
      'Trading'
    ],
    description: 'Retail companies can source from consumer goods and wholesale suppliers'
  },
  {
    buyerCategory: 'Others',
    allowedSupplierCategories: [], // Empty array means show ALL categories
    description: 'Others category shows all available supplier categories'
  }
];

/**
 * Get allowed supplier categories for a buyer category
 * 
 * @param buyerCategory - The buyer's company category
 * @returns Array of allowed supplier category names
 * 
 * @example
 * const allowed = getAllowedSupplierCategories('IT');
 * // Returns: ['IT', 'Software', 'Hardware', 'Technology', 'Electronics', ...]
 */
export function getAllowedSupplierCategories(buyerCategory: string): string[] {
  if (!buyerCategory) {
    return []; // Return empty to show all
  }

  const mapping = BUYER_SUPPLIER_CATEGORY_MAPPINGS.find(
    m => m.buyerCategory.toLowerCase() === buyerCategory.toLowerCase()
  );

  return mapping?.allowedSupplierCategories || [];
}

/**
 * Check if a supplier category is allowed for a buyer category
 * Uses case-insensitive partial matching
 * 
 * @param buyerCategory - The buyer's company category
 * @param supplierCategory - The supplier category to check
 * @returns true if supplier category is allowed
 * 
 * @example
 * isSupplierCategoryAllowed('IT', 'Software Solutions') // true
 * isSupplierCategoryAllowed('IT', 'Manufacturing') // false
 */
export function isSupplierCategoryAllowed(
  buyerCategory: string,
  supplierCategory: string
): boolean {
  const allowedCategories = getAllowedSupplierCategories(buyerCategory);
  
  // If empty array, allow all categories
  if (allowedCategories.length === 0) {
    return true;
  }

  const supplierLower = supplierCategory.toLowerCase();
  
  return allowedCategories.some(allowed => {
    const allowedLower = allowed.toLowerCase();
    // Check both ways for partial matching
    return supplierLower.includes(allowedLower) || allowedLower.includes(supplierLower);
  });
}

/**
 * Filter supplier categories based on buyer category
 * 
 * @param buyerCategory - The buyer's company category
 * @param allSupplierCategories - Array of all available supplier categories
 * @returns Filtered array of supplier categories
 * 
 * @example
 * const all = ['IT', 'Manufacturing', 'Software', 'Industrial'];
 * const filtered = filterSupplierCategories('IT', all);
 * // Returns: ['IT', 'Software']
 */
export function filterSupplierCategories(
  buyerCategory: string,
  allSupplierCategories: string[]
): string[] {
  const allowedCategories = getAllowedSupplierCategories(buyerCategory);
  
  // If empty array (like 'Others'), return all categories
  if (allowedCategories.length === 0) {
    return [...allSupplierCategories];
  }

  const filtered = allSupplierCategories.filter(supplierCat =>
    isSupplierCategoryAllowed(buyerCategory, supplierCat)
  );

  // If no matches found, return all to prevent blocking
  return filtered.length > 0 ? filtered : [...allSupplierCategories];
}

/**
 * Get description for a buyer category mapping
 * 
 * @param buyerCategory - The buyer's company category
 * @returns Description string or undefined
 */
export function getCategoryMappingDescription(buyerCategory: string): string | undefined {
  const mapping = BUYER_SUPPLIER_CATEGORY_MAPPINGS.find(
    m => m.buyerCategory.toLowerCase() === buyerCategory.toLowerCase()
  );
  
  return mapping?.description;
}

/**
 * Get all available buyer categories
 * 
 * @returns Array of all buyer category names
 */
export function getAllBuyerCategories(): string[] {
  return BUYER_SUPPLIER_CATEGORY_MAPPINGS.map(m => m.buyerCategory);
}