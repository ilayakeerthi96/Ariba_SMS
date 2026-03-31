// StorageService - Centralized access to stored user details

import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class StorageService {
  
  /**
   * Get user details from localStorage
   */
  static getUserDetails(): any {
    try {
      const user = localStorage.getItem('signinData');
      if (user) {
        const parsed = JSON.parse(user);
        return {
          fullName: parsed.fullName || localStorage.getItem('fullName') || 'N/A',
          email: parsed.email || localStorage.getItem('email') || 'N/A',
          userId: parsed.userId || localStorage.getItem('userId') || 'N/A'
        };
      }
      return {
        fullName: localStorage.getItem('fullName') || 'N/A',
        email: localStorage.getItem('email') || 'N/A',
        userId: localStorage.getItem('userId') || 'N/A'
      };
    } catch (e) {
      console.error('Error getting user details:', e);
      return {
        fullName: localStorage.getItem('fullName') || 'N/A',
        email: localStorage.getItem('email') || 'N/A',
        userId: localStorage.getItem('userId') || 'N/A'
      };
    }
  }

  /**
   * Get buyer details from localStorage
   */
  static getBuyerDetails(): any {
    try {
      const buyer = localStorage.getItem('buyer');
      if (buyer) {
        const parsed = JSON.parse(buyer);
        return {
          id: parsed.id || localStorage.getItem('buyerId') || 'N/A',
          name: parsed.name || localStorage.getItem('buyerName') || 'N/A',
          email: parsed.email || localStorage.getItem('buyerEmail') || 'N/A',
          companyName: parsed.name || localStorage.getItem('buyerName') || 'N/A'
        };
      }
      return {
        id: localStorage.getItem('buyerId') || 'N/A',
        name: localStorage.getItem('buyerName') || 'N/A',
        email: localStorage.getItem('buyerEmail') || 'N/A',
        companyName: localStorage.getItem('buyerName') || 'N/A'
      };
    } catch (e) {
      console.error('Error getting buyer details:', e);
      return {
        id: localStorage.getItem('buyerId') || 'N/A',
        name: localStorage.getItem('buyerName') || 'N/A',
        email: localStorage.getItem('buyerEmail') || 'N/A',
        companyName: localStorage.getItem('buyerName') || 'N/A'
      };
    }
  }

  /**
   * Get department details from localStorage
   */
  static getDepartmentDetails(): any {
    try {
      const dept = localStorage.getItem('department');
      if (dept) {
        const parsed = JSON.parse(dept);
        return {
          id: parsed.id || localStorage.getItem('departmentId') || 'N/A',
          name: parsed.name || localStorage.getItem('departmentName') || 'N/A',
          code: parsed.code || 'N/A',
          description: parsed.description || 'N/A'
        };
      }
      return {
        id: localStorage.getItem('departmentId') || 'N/A',
        name: localStorage.getItem('departmentName') || 'N/A',
        code: 'N/A',
        description: 'N/A'
      };
    } catch (e) {
      console.error('Error getting department details:', e);
      return {
        id: localStorage.getItem('departmentId') || 'N/A',
        name: localStorage.getItem('departmentName') || 'N/A',
        code: 'N/A',
        description: 'N/A'
      };
    }
  }

  /**
   * Get location details from localStorage
   */
  static getLocationDetails(): any {
    try {
      const location = localStorage.getItem('location');
      if (location) {
        const parsed = JSON.parse(location);
        return {
          id: parsed.id || localStorage.getItem('locationId') || 'N/A',
          city: parsed.city || localStorage.getItem('city') || 'N/A',
          state: parsed.state || localStorage.getItem('state') || 'N/A',
          postalCode: parsed.postalCode || localStorage.getItem('postalCode') || 'N/A',
          country: parsed.country || localStorage.getItem('country') || 'N/A',
          addressLine1: parsed.addressLine1 || 'N/A',
          addressLine2: parsed.addressLine2 || 'N/A'
        };
      }
      return {
        id: localStorage.getItem('locationId') || 'N/A',
        city: localStorage.getItem('city') || 'N/A',
        state: localStorage.getItem('state') || 'N/A',
        postalCode: localStorage.getItem('postalCode') || 'N/A',
        country: localStorage.getItem('country') || 'N/A',
        addressLine1: 'N/A',
        addressLine2: 'N/A'
      };
    } catch (e) {
      console.error('Error getting location details:', e);
      return {
        id: localStorage.getItem('locationId') || 'N/A',
        city: localStorage.getItem('city') || 'N/A',
        state: localStorage.getItem('state') || 'N/A',
        postalCode: localStorage.getItem('postalCode') || 'N/A',
        country: localStorage.getItem('country') || 'N/A',
        addressLine1: 'N/A',
        addressLine2: 'N/A'
      };
    }
  }

  /**
   * Clear all stored data
   */
  static clearAll(): void {
    localStorage.clear();
  }

  /**
   * Get token
   */
  static getToken(): string {
    return localStorage.getItem('token') || '';
  }

  /**
   * Check if user is buyer
   */
  static isBuyer(): boolean {
    const role = localStorage.getItem('role');
    return role === 'ROLE_BUYER';
  }
}