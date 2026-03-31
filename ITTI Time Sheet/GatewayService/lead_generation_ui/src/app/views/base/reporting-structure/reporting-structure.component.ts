// // // // import { Component, OnInit } from '@angular/core';
// // // // import { CommonModule } from '@angular/common';
// // // // import { FormsModule } from '@angular/forms';
// // // // import { RouterLink } from '@angular/router';
// // // // import { DataService } from '../../../shared/service/DataService';
// // // // import {
// // // //   ContainerComponent, RowComponent, ColComponent, CardComponent,
// // // //   CardHeaderComponent, CardBodyComponent, ButtonDirective,
// // // //   FormControlDirective, BadgeComponent
// // // // } from '@coreui/angular';
// // // // import { IconDirective } from '@coreui/icons-angular';

// // // // @Component({
// // // //   selector: 'app-reporting-structure',
// // // //   templateUrl: './reporting-structure.component.html',
// // // //   styleUrls: ['./reporting-structure.component.css'],
// // // //   standalone: true,
// // // //   imports: [
// // // //     CommonModule, FormsModule, RouterLink,
// // // //     ContainerComponent, RowComponent, ColComponent, CardComponent,
// // // //     CardHeaderComponent, CardBodyComponent, ButtonDirective,
// // // //     FormControlDirective, BadgeComponent, IconDirective
// // // //   ]
// // // // })
// // // // export class ReportingStructureComponent implements OnInit {

// // // //   hierarchyUsers: any[] = [];
// // // //   hierarchyTree: any[] = [];
// // // //   selectedManagers: { [userId: number]: string } = {};
// // // //   updatingUsers: { [userId: number]: boolean } = {};
// // // //   companyName: string = '';
// // // //   isLoading: boolean = false;

// // // //   constructor(private dataService: DataService) {}

// // // //   ngOnInit(): void {
// // // //     this.companyName = localStorage.getItem('companyName') || 'Unknown Company';
// // // //     this.loadHierarchyUsers();
// // // //   }

// // // //   // ============================================
// // // //   // LOAD HIERARCHY USERS
// // // //   // ============================================
// // // //   loadHierarchyUsers(): void {
// // // //     this.isLoading = true;

// // // //     this.dataService.getHierarchyUsersByCompany(this.companyName).subscribe({
// // // //       next: (response: any) => {
// // // //         console.log('✅ Hierarchy Users Response:', response);
// // // //         this.isLoading = false;

// // // //         if (response?.success && response?.data) {
// // // //           this.hierarchyUsers = response.data;
          
// // // //           // Initialize selectedManagers with current reporting
// // // //           this.hierarchyUsers.forEach(user => {
// // // //             this.selectedManagers[user.id] = user.reportsToId?.toString() || '';
// // // //             this.updatingUsers[user.id] = false;
// // // //           });

// // // //           // Build hierarchy tree
// // // //           this.buildHierarchyTree();
          
// // // //           console.log(`✅ Loaded ${this.hierarchyUsers.length} users`);
// // // //         } else {
// // // //           this.hierarchyUsers = [];
// // // //         }
// // // //       },
// // // //       error: (error: any) => {
// // // //         this.isLoading = false;
// // // //         console.error('❌ Error loading hierarchy users:', error);
// // // //       }
// // // //     });
// // // //   }

// // // //   // ============================================
// // // //   // GET POTENTIAL MANAGERS
// // // //   // Returns users at higher hierarchy levels
// // // //   // ============================================
// // // //   getPotentialManagers(user: any): any[] {
// // // //     return this.hierarchyUsers.filter(potentialManager => {
// // // //       // Cannot report to self
// // // //       if (potentialManager.id === user.id) {
// // // //         return false;
// // // //       }
      
// // // //       // Manager must be at a higher level (lower levelOrder number)
// // // //       return potentialManager.hierarchyLevelOrder < user.hierarchyLevelOrder;
// // // //     });
// // // //   }

// // // //   // ============================================
// // // //   // UPDATE REPORTING RELATIONSHIP
// // // //   // ============================================
// // // //   updateReporting(user: any): void {
// // // //     const newManagerId = this.selectedManagers[user.id];
    
// // // //     // Validate selection
// // // //     if (newManagerId === undefined) {
// // // //       alert('Please select a manager');
// // // //       return;
// // // //     }

// // // //     // Confirm update
// // // //     const managerName = newManagerId 
// // // //       ? this.hierarchyUsers.find(u => u.id.toString() === newManagerId)?.fullName 
// // // //       : 'No Manager';
    
// // // //     const confirmMsg = `Update reporting for ${user.fullName} to report to ${managerName}?`;
// // // //     if (!confirm(confirmMsg)) {
// // // //       return;
// // // //     }

// // // //     this.updatingUsers[user.id] = true;

// // // //     const updateData = {
// // // //       reportsToId: newManagerId ? parseInt(newManagerId) : null
// // // //     };

// // // //     this.dataService.updateHierarchyUser(user.id, updateData).subscribe({
// // // //       next: (response: any) => {
// // // //         console.log('✅ Reporting Updated:', response);
// // // //         this.updatingUsers[user.id] = false;

// // // //         if (response?.success) {
// // // //           alert('Reporting relationship updated successfully!');
// // // //           this.loadHierarchyUsers(); // Reload to refresh tree
// // // //         } else {
// // // //           alert(response?.message || 'Failed to update reporting');
// // // //         }
// // // //       },
// // // //       error: (error: any) => {
// // // //         this.updatingUsers[user.id] = false;
// // // //         console.error('❌ Error updating reporting:', error);
// // // //         alert(error.error?.message || 'Failed to update reporting');
// // // //       }
// // // //     });
// // // //   }

// // // //   // ============================================
// // // //   // BUILD HIERARCHY TREE
// // // //   // Creates a nested tree structure for visualization
// // // //   // ============================================
// // // //   buildHierarchyTree(): void {
// // // //     // Create a map of users by ID for quick lookup
// // // //     const userMap = new Map<number, any>();
// // // //     this.hierarchyUsers.forEach(user => {
// // // //       userMap.set(user.id, { ...user, reports: [] });
// // // //     });

// // // //     // Build parent-child relationships
// // // //     this.hierarchyUsers.forEach(user => {
// // // //       if (user.reportsToId) {
// // // //         const manager = userMap.get(user.reportsToId);
// // // //         if (manager) {
// // // //           manager.reports.push(userMap.get(user.id));
// // // //         }
// // // //       }
// // // //     });

// // // //     // Find top-level users (no manager)
// // // //     this.hierarchyTree = this.hierarchyUsers
// // // //       .filter(user => !user.reportsToId)
// // // //       .map(user => userMap.get(user.id))
// // // //       .filter(user => user !== undefined)
// // // //       .sort((a, b) => a.hierarchyLevelOrder - b.hierarchyLevelOrder);

// // // //     console.log('✅ Hierarchy Tree Built:', this.hierarchyTree);
// // // //   }

// // // //   // ============================================
// // // //   // UTILITY METHODS
// // // //   // ============================================
// // // //   getLevelBadgeColor(order: number): string {
// // // //     if (order === 1) return 'danger';
// // // //     if (order === 2) return 'warning';
// // // //     if (order === 3) return 'info';
// // // //     return 'secondary';
// // // //   }
// // // // }

// // // import { Component, OnInit } from '@angular/core';
// // // import { CommonModule } from '@angular/common';
// // // import { FormsModule } from '@angular/forms';
// // // import { RouterLink } from '@angular/router';
// // // import { DataService } from '../../../shared/service/DataService';
// // // import {
// // //   ContainerComponent, RowComponent, ColComponent, CardComponent,
// // //   CardHeaderComponent, CardBodyComponent, ButtonDirective,
// // //   FormControlDirective, BadgeComponent
// // // } from '@coreui/angular';
// // // import { IconDirective } from '@coreui/icons-angular';

// // // @Component({
// // //   selector: 'app-reporting-structure',
// // //   templateUrl: './reporting-structure.component.html',
// // //   styleUrls: ['./reporting-structure.component.css'],
// // //   standalone: true,
// // //   imports: [
// // //     CommonModule, FormsModule, RouterLink,
// // //     ContainerComponent, RowComponent, ColComponent, CardComponent,
// // //     CardHeaderComponent, CardBodyComponent, ButtonDirective,
// // //     FormControlDirective, BadgeComponent, IconDirective
// // //   ]
// // // })
// // // export class ReportingStructureComponent implements OnInit {

// // //   hierarchyUsers: any[] = [];
// // //   hierarchyTree: any[] = [];
// // //   selectedManagers: { [userId: number]: number[] } = {};  // ✅ Changed to array
// // //   updatingUsers: { [userId: number]: boolean } = {};
// // //   companyName: string = '';
// // //   isLoading: boolean = false;

// // //   constructor(private dataService: DataService) {}

// // //   ngOnInit(): void {
// // //     this.companyName = localStorage.getItem('companyName') || 'Unknown Company';
// // //     this.loadHierarchyUsers();
// // //   }

// // //   // ============================================
// // //   // LOAD HIERARCHY USERS
// // //   // ============================================
// // //   loadHierarchyUsers(): void {
// // //     this.isLoading = true;

// // //     this.dataService.getHierarchyUsersByCompany(this.companyName).subscribe({
// // //       next: (response: any) => {
// // //         console.log('✅ Hierarchy Users Response:', response);
// // //         this.isLoading = false;

// // //         if (response?.success && response?.data) {
// // //           this.hierarchyUsers = response.data;
          
// // //           // ✅ Initialize selectedManagers with current reporting (array of IDs)
// // //           this.hierarchyUsers.forEach(user => {
// // //             if (user.reportsTo && Array.isArray(user.reportsTo)) {
// // //               // Multiple managers
// // //               this.selectedManagers[user.id] = user.reportsTo.map((m: any) => m.id);
// // //             } else if (user.reportsToId) {
// // //               // Backward compatibility: single manager
// // //               this.selectedManagers[user.id] = [user.reportsToId];
// // //             } else {
// // //               this.selectedManagers[user.id] = [];
// // //             }
// // //             this.updatingUsers[user.id] = false;
// // //           });

// // //           // Build hierarchy tree
// // //           this.buildHierarchyTree();
          
// // //           console.log(`✅ Loaded ${this.hierarchyUsers.length} users`);
// // //         } else {
// // //           this.hierarchyUsers = [];
// // //         }
// // //       },
// // //       error: (error: any) => {
// // //         this.isLoading = false;
// // //         console.error('❌ Error loading hierarchy users:', error);
// // //       }
// // //     });
// // //   }

// // //   // ============================================
// // //   // GET POTENTIAL MANAGERS
// // //   // ✅ Returns users at higher hierarchy levels
// // //   // ============================================
// // //   getPotentialManagers(user: any): any[] {
// // //     return this.hierarchyUsers.filter(potentialManager => {
// // //       // Cannot report to self
// // //       if (potentialManager.id === user.id) {
// // //         return false;
// // //       }
      
// // //       // Manager must be at a higher level (lower levelOrder number)
// // //       return potentialManager.hierarchyLevelOrder < user.hierarchyLevelOrder;
// // //     });
// // //   }

// // //   // ============================================
// // //   // ✅ UPDATED: UPDATE REPORTING RELATIONSHIP
// // //   // Now handles multiple managers
// // //   // ============================================
// // //   updateReporting(user: any): void {
// // //     const newManagerIds = this.selectedManagers[user.id] || [];
    
// // //     // Get manager names for confirmation
// // //     let managerNames = 'No Managers';
// // //     if (newManagerIds.length > 0) {
// // //       const managers = this.hierarchyUsers.filter(u => newManagerIds.includes(u.id));
// // //       managerNames = managers.map(m => m.fullName).join(', ');
// // //     }
    
// // //     const confirmMsg = `Update reporting for ${user.fullName} to report to:\n${managerNames}?`;
// // //     if (!confirm(confirmMsg)) {
// // //       return;
// // //     }

// // //     this.updatingUsers[user.id] = true;

// // //     // ✅ Send array of manager IDs
// // //     const updateData = {
// // //       reportsToIds: newManagerIds.length > 0 ? newManagerIds : []
// // //     };

// // //     console.log('📤 Updating reporting:', updateData);

// // //     this.dataService.updateHierarchyUser(user.id, updateData).subscribe({
// // //       next: (response: any) => {
// // //         console.log('✅ Reporting Updated:', response);
// // //         this.updatingUsers[user.id] = false;

// // //         if (response?.success) {
// // //           alert('Reporting relationship updated successfully!');
// // //           this.loadHierarchyUsers(); // Reload to refresh tree
// // //         } else {
// // //           alert(response?.message || 'Failed to update reporting');
// // //         }
// // //       },
// // //       error: (error: any) => {
// // //         this.updatingUsers[user.id] = false;
// // //         console.error('❌ Error updating reporting:', error);
// // //         alert(error.error?.message || 'Failed to update reporting');
// // //       }
// // //     });
// // //   }

// // //   // ============================================
// // //   // BUILD HIERARCHY TREE
// // //   // Creates a nested tree structure for visualization
// // //   // ============================================
// // //   buildHierarchyTree(): void {
// // //     // Create a map of users by ID for quick lookup
// // //     const userMap = new Map<number, any>();
// // //     this.hierarchyUsers.forEach(user => {
// // //       userMap.set(user.id, { ...user, reports: [] });
// // //     });

// // //     // ✅ Build parent-child relationships (handling multiple managers)
// // //     this.hierarchyUsers.forEach(user => {
// // //       if (user.reportsTo && Array.isArray(user.reportsTo) && user.reportsTo.length > 0) {
// // //         // Add this user as a report to ALL their managers
// // //         user.reportsTo.forEach((manager: any) => {
// // //           const managerNode = userMap.get(manager.id);
// // //           if (managerNode) {
// // //             const userNode = userMap.get(user.id);
// // //             if (userNode && !managerNode.reports.some((r: any) => r.id === user.id)) {
// // //               managerNode.reports.push(userNode);
// // //             }
// // //           }
// // //         });
// // //       } else if (user.reportsToId) {
// // //         // Backward compatibility: single manager
// // //         const manager = userMap.get(user.reportsToId);
// // //         if (manager) {
// // //           manager.reports.push(userMap.get(user.id));
// // //         }
// // //       }
// // //     });

// // //     // Find top-level users (no manager or all managers are outside current list)
// // //     this.hierarchyTree = this.hierarchyUsers
// // //       .filter(user => {
// // //         if (!user.reportsTo || user.reportsTo.length === 0) {
// // //           return !user.reportsToId; // No reporting relationship at all
// // //         }
// // //         return false;
// // //       })
// // //       .map(user => userMap.get(user.id))
// // //       .filter(user => user !== undefined)
// // //       .sort((a, b) => a.hierarchyLevelOrder - b.hierarchyLevelOrder);

// // //     console.log('✅ Hierarchy Tree Built:', this.hierarchyTree);
// // //   }

// // //   // ============================================
// // //   // ✅ NEW: Check if manager is selected
// // //   // ============================================
// // //   isManagerSelected(userId: number, managerId: number): boolean {
// // //     return this.selectedManagers[userId]?.includes(managerId) || false;
// // //   }

// // //   // ============================================
// // //   // ✅ NEW: Toggle manager selection
// // //   // ============================================
// // //   toggleManager(userId: number, managerId: number): void {
// // //     if (!this.selectedManagers[userId]) {
// // //       this.selectedManagers[userId] = [];
// // //     }

// // //     const index = this.selectedManagers[userId].indexOf(managerId);
// // //     if (index > -1) {
// // //       // Remove manager
// // //       this.selectedManagers[userId].splice(index, 1);
// // //     } else {
// // //       // Add manager
// // //       this.selectedManagers[userId].push(managerId);
// // //     }
// // //   }

// // //   // ============================================
// // //   // UTILITY METHODS
// // //   // ============================================
// // //   getLevelBadgeColor(order: number): string {
// // //     if (order <= 10) return 'danger';
// // //     if (order <= 20) return 'warning';
// // //     if (order <= 30) return 'info';
// // //     return 'secondary';
// // //   }

// // //   // ✅ Format current managers display
// // //   formatCurrentManagers(user: any): string {
// // //     if (user.reportsTo && Array.isArray(user.reportsTo) && user.reportsTo.length > 0) {
// // //       return user.reportsTo.map((m: any) => `${m.fullName} (${m.designation})`).join(', ');
// // //     } else if (user.reportsToName) {
// // //       return `${user.reportsToName} (${user.reportsToDesignation})`;
// // //     }
// // //     return 'No manager assigned';
// // //   }
// // // }

// // import { Component, OnInit } from '@angular/core';
// // import { CommonModule } from '@angular/common';
// // import { FormsModule } from '@angular/forms';
// // import { RouterLink } from '@angular/router';
// // import { DataService } from '../../../shared/service/DataService';
// // import {
// //   ContainerComponent, RowComponent, ColComponent, CardComponent,
// //   CardHeaderComponent, CardBodyComponent, ButtonDirective,
// //   FormControlDirective, BadgeComponent
// // } from '@coreui/angular';
// // import { IconDirective } from '@coreui/icons-angular';

// // @Component({
// //   selector: 'app-reporting-structure',
// //   templateUrl: './reporting-structure.component.html',
// //   styleUrls: ['./reporting-structure.component.css'],
// //   standalone: true,
// //   imports: [
// //     CommonModule, FormsModule, RouterLink,
// //     ContainerComponent, RowComponent, ColComponent, CardComponent,
// //     CardHeaderComponent, CardBodyComponent, ButtonDirective,
// //     FormControlDirective, BadgeComponent, IconDirective
// //   ]
// // })
// // export class ReportingStructureComponent implements OnInit {

// //   hierarchyUsers: any[] = [];
// //   hierarchyTree: any[] = [];
// //   selectedManagers: { [userId: number]: number[] } = {};  // Array of selected manager IDs
// //   updatingUsers: { [userId: number]: boolean } = {};
// //   companyName: string = '';
// //   isLoading: boolean = false;

// //   constructor(private dataService: DataService) {}

// //   ngOnInit(): void {
// //     this.companyName = localStorage.getItem('companyName') || 'Unknown Company';
// //     this.loadHierarchyUsers();
// //   }

// //   // ============================================
// //   // LOAD HIERARCHY USERS
// //   // ============================================
// //   // loadHierarchyUsers(): void {
// //   //   this.isLoading = true;

// //   //   this.dataService.getHierarchyUsersByCompany(this.companyName).subscribe({
// //   //     next: (response: any) => {
// //   //       console.log('✅ Hierarchy Users Response:', response);
// //   //       this.isLoading = false;

// //   //       if (response?.success && response?.data) {
// //   //         this.hierarchyUsers = response.data;
          
// //   //         // ✅ Initialize selectedManagers with current reporting
// //   //         this.hierarchyUsers.forEach(user => {
// //   //           if (user.reportsTo && Array.isArray(user.reportsTo)) {
// //   //             // Multiple managers - extract IDs
// //   //             this.selectedManagers[user.id] = user.reportsTo.map((m: any) => m.id);
// //   //           } else if (user.reportsToId) {
// //   //             // Backward compatibility: single manager
// //   //             this.selectedManagers[user.id] = [user.reportsToId];
// //   //           } else {
// //   //             this.selectedManagers[user.id] = [];
// //   //           }
// //   //           this.updatingUsers[user.id] = false;
// //   //         });

// //   //         console.log('✅ Initialized selected managers:', this.selectedManagers);

// //   //         // Build hierarchy tree
// //   //         this.buildHierarchyTree();
          
// //   //         console.log(`✅ Loaded ${this.hierarchyUsers.length} users`);
// //   //       } else {
// //   //         this.hierarchyUsers = [];
// //   //       }
// //   //     },
// //   //     error: (error: any) => {
// //   //       this.isLoading = false;
// //   //       console.error('❌ Error loading hierarchy users:', error);
// //   //     }
// //   //   });
// //   // }

// //   loadHierarchyUsers(): void {
// //   this.isLoading = true;

// //   this.dataService.getHierarchyUsersByCompany(this.companyName).subscribe({
// //     next: (response: any) => {
// //       console.log('✅ Reporting Structure - Full Response:', response);
// //       this.isLoading = false;

// //       if (response?.success && response?.data) {
// //         this.hierarchyUsers = response.data;
        
// //         // ✅ DEBUG: Log manager data for each user
// //         this.hierarchyUsers.forEach(user => {
// //           console.log(`👤 ${user.fullName}:`);
// //           console.log('   Current Managers (reportsTo):', user.reportsTo);
          
// //           // Initialize selected managers
// //           if (user.reportsTo && Array.isArray(user.reportsTo)) {
// //             this.selectedManagers[user.id] = user.reportsTo.map((m: any) => m.id);
// //             console.log('   ✅ Initialized selected managers:', this.selectedManagers[user.id]);
// //           } else if (user.reportsToId) {
// //             this.selectedManagers[user.id] = [user.reportsToId];
// //             console.log('   ✅ Initialized from reportsToId:', this.selectedManagers[user.id]);
// //           } else {
// //             this.selectedManagers[user.id] = [];
// //             console.log('   ℹ️ No managers assigned');
// //           }
          
// //           this.updatingUsers[user.id] = false;
// //         });

// //         console.log('✅ Final selectedManagers object:', this.selectedManagers);

// //         // Build hierarchy tree
// //         this.buildHierarchyTree();
        
// //         console.log(`✅ Loaded ${this.hierarchyUsers.length} users`);
// //       } else {
// //         this.hierarchyUsers = [];
// //       }
// //     },
// //     error: (error: any) => {
// //       this.isLoading = false;
// //       console.error('❌ Error loading hierarchy users:', error);
// //     }
// //   });
// // }
// //   // ============================================
// //   // GET POTENTIAL MANAGERS
// //   // Returns users at higher hierarchy levels
// //   // ============================================
// //   getPotentialManagers(user: any): any[] {
// //     return this.hierarchyUsers.filter(potentialManager => {
// //       // Cannot report to self
// //       if (potentialManager.id === user.id) {
// //         return false;
// //       }
      
// //       // Manager must be at a higher level (lower levelOrder number)
// //       return potentialManager.hierarchyLevelOrder < user.hierarchyLevelOrder;
// //     });
// //   }

// //   // ============================================
// //   // ✅ NEW: Check if manager is selected
// //   // ============================================
// //   isManagerSelected(userId: number, managerId: number): boolean {
// //     if (!this.selectedManagers[userId]) {
// //       return false;
// //     }
// //     return this.selectedManagers[userId].includes(managerId);
// //   }

// //   // ============================================
// //   // ✅ NEW: Toggle manager selection (checkbox style)
// //   // ============================================
// //   toggleManager(userId: number, managerId: number): void {
// //     if (!this.selectedManagers[userId]) {
// //       this.selectedManagers[userId] = [];
// //     }

// //     const index = this.selectedManagers[userId].indexOf(managerId);
// //     if (index > -1) {
// //       // Remove manager
// //       this.selectedManagers[userId].splice(index, 1);
// //       console.log(`✅ Removed manager ${managerId} from user ${userId}`);
// //     } else {
// //       // Add manager
// //       this.selectedManagers[userId].push(managerId);
// //       console.log(`✅ Added manager ${managerId} to user ${userId}`);
// //     }

// //     console.log('✅ Current selection:', this.selectedManagers[userId]);
// //   }

// //   // ============================================
// //   // UPDATE REPORTING RELATIONSHIP
// //   // ============================================
// //   updateReporting(user: any): void {
// //     const newManagerIds = this.selectedManagers[user.id] || [];
    
// //     // Get manager names for confirmation
// //     let managerNames = 'No Managers';
// //     if (newManagerIds.length > 0) {
// //       const managers = this.hierarchyUsers.filter(u => newManagerIds.includes(u.id));
// //       managerNames = managers.map(m => m.fullName).join(', ');
// //     }
    
// //     const confirmMsg = `Update reporting for ${user.fullName}?\n\nReports to:\n${managerNames}`;
// //     if (!confirm(confirmMsg)) {
// //       return;
// //     }

// //     this.updatingUsers[user.id] = true;

// //     // Send array of manager IDs
// //     const updateData = {
// //       reportsToIds: newManagerIds
// //     };

// //     console.log('📤 Updating reporting:', updateData);

// //     this.dataService.updateHierarchyUser(user.id, updateData).subscribe({
// //       next: (response: any) => {
// //         console.log('✅ Reporting Updated:', response);
// //         this.updatingUsers[user.id] = false;

// //         if (response?.success) {
// //           alert('✅ Reporting relationship updated successfully!');
// //           this.loadHierarchyUsers(); // Reload to refresh tree
// //         } else {
// //           alert('❌ ' + (response?.message || 'Failed to update reporting'));
// //         }
// //       },
// //       error: (error: any) => {
// //         this.updatingUsers[user.id] = false;
// //         console.error('❌ Error updating reporting:', error);
// //         alert('❌ ' + (error.error?.message || 'Failed to update reporting'));
// //       }
// //     });
// //   }

// //   // ============================================
// //   // BUILD HIERARCHY TREE
// //   // ============================================
// //   buildHierarchyTree(): void {
// //     // Create a map of users by ID for quick lookup
// //     const userMap = new Map<number, any>();
// //     this.hierarchyUsers.forEach(user => {
// //       userMap.set(user.id, { ...user, reports: [] });
// //     });

// //     // Build parent-child relationships (handling multiple managers)
// //     this.hierarchyUsers.forEach(user => {
// //       if (user.reportsTo && Array.isArray(user.reportsTo) && user.reportsTo.length > 0) {
// //         // Add this user as a report to ALL their managers
// //         user.reportsTo.forEach((manager: any) => {
// //           const managerNode = userMap.get(manager.id);
// //           if (managerNode) {
// //             const userNode = userMap.get(user.id);
// //             if (userNode && !managerNode.reports.some((r: any) => r.id === user.id)) {
// //               managerNode.reports.push(userNode);
// //             }
// //           }
// //         });
// //       } else if (user.reportsToId) {
// //         // Backward compatibility
// //         const manager = userMap.get(user.reportsToId);
// //         if (manager) {
// //           manager.reports.push(userMap.get(user.id));
// //         }
// //       }
// //     });

// //     // Find top-level users (no manager)
// //     this.hierarchyTree = this.hierarchyUsers
// //       .filter(user => {
// //         if (!user.reportsTo || user.reportsTo.length === 0) {
// //           return !user.reportsToId;
// //         }
// //         return false;
// //       })
// //       .map(user => userMap.get(user.id))
// //       .filter(user => user !== undefined)
// //       .sort((a, b) => a.hierarchyLevelOrder - b.hierarchyLevelOrder);

// //     console.log('✅ Hierarchy Tree Built:', this.hierarchyTree);
// //   }

// //   // ============================================
// //   // UTILITY METHODS
// //   // ============================================
// //   getLevelBadgeColor(order: number): string {
// //     if (order <= 10) return 'danger';   // CEO
// //     if (order <= 20) return 'warning';  // COO
// //     if (order <= 30) return 'info';     // Manager
// //     return 'secondary';
// //   }
// // }


// import { Component, OnInit } from '@angular/core';
// import { CommonModule } from '@angular/common';
// import { FormsModule } from '@angular/forms';
// import { RouterLink } from '@angular/router';
// import { DataService } from '../../../shared/service/DataService';
// import {
//   ContainerComponent, RowComponent, ColComponent, CardComponent,
//   CardHeaderComponent, CardBodyComponent, ButtonDirective,
//   FormControlDirective, BadgeComponent
// } from '@coreui/angular';
// import { IconDirective } from '@coreui/icons-angular';

// @Component({
//   selector: 'app-reporting-structure',
//   templateUrl: './reporting-structure.component.html',
//   styleUrls: ['./reporting-structure.component.css'],
//   standalone: true,
//   imports: [
//     CommonModule, FormsModule, RouterLink,
//     ContainerComponent, RowComponent, ColComponent, CardComponent,
//     CardHeaderComponent, CardBodyComponent, ButtonDirective,
//     FormControlDirective, BadgeComponent, IconDirective
//   ]
// })
// export class ReportingStructureComponent implements OnInit {

//   hierarchyUsers: any[] = [];
//   hierarchyTree: any[] = [];
//   selectedManagers: { [userId: number]: number[] } = {};  // Array of selected manager IDs
//   updatingUsers: { [userId: number]: boolean } = {};
//   companyName: string = '';
//   isLoading: boolean = false;

//   constructor(private dataService: DataService) {}

//   ngOnInit(): void {
//     this.companyName = localStorage.getItem('companyName') || 'Unknown Company';
//     this.loadHierarchyUsers();
//   }

//   // ============================================
//   // LOAD HIERARCHY USERS
//   // ============================================
//   loadHierarchyUsers(): void {
//     this.isLoading = true;

//     this.dataService.getHierarchyUsersByCompany(this.companyName).subscribe({
//       next: (response: any) => {
//         console.log('✅ Reporting Structure - Full Response:', response);
//         this.isLoading = false;

//         if (response?.success && response?.data) {
//           this.hierarchyUsers = response.data;
          
//           // DEBUG: Log manager data for each user
//           this.hierarchyUsers.forEach(user => {
//             console.log(`👤 ${user.fullName}:`);
//             console.log('   Current Managers (reportsTo):', user.reportsTo);
            
//             // Initialize selected managers
//             if (user.reportsTo && Array.isArray(user.reportsTo)) {
//               this.selectedManagers[user.id] = user.reportsTo.map((m: any) => Number(m.id)); // ✅ Convert to number
//               console.log('   ✅ Initialized selected managers:', this.selectedManagers[user.id]);
//             } else if (user.reportsToId) {
//               this.selectedManagers[user.id] = [Number(user.reportsToId)]; // ✅ Convert to number
//               console.log('   ✅ Initialized from reportsToId:', this.selectedManagers[user.id]);
//             } else {
//               this.selectedManagers[user.id] = [];
//               console.log('   ℹ️ No managers assigned');
//             }
            
//             this.updatingUsers[user.id] = false;
//           });

//           console.log('✅ Final selectedManagers object:', this.selectedManagers);

//           // Build hierarchy tree
//           this.buildHierarchyTree();
          
//           console.log(`✅ Loaded ${this.hierarchyUsers.length} users`);
//         } else {
//           this.hierarchyUsers = [];
//         }
//       },
//       error: (error: any) => {
//         this.isLoading = false;
//         console.error('❌ Error loading hierarchy users:', error);
//       }
//     });
//   }

//   // ============================================
//   // GET POTENTIAL MANAGERS
//   // ============================================
//   getPotentialManagers(user: any): any[] {
//     return this.hierarchyUsers.filter(potentialManager => {
//       // Cannot report to self
//       if (potentialManager.id === user.id) {
//         return false;
//       }
      
//       // Manager must be at a higher level (lower levelOrder number)
//       return potentialManager.hierarchyLevelOrder < user.hierarchyLevelOrder;
//     });
//   }

//   // ============================================
//   // ✅ Check if manager is selected
//   // ============================================
//   isManagerSelected(userId: number, managerId: number): boolean {
//     if (!this.selectedManagers[userId]) {
//       return false;
//     }
//     // ✅ Convert to number for comparison
//     return this.selectedManagers[userId].includes(Number(managerId));
//   }

//   // ============================================
//   // ✅ FIXED: Toggle manager selection
//   // ============================================
//   toggleManager(userId: number, managerId: number): void {
//     if (!this.selectedManagers[userId]) {
//       this.selectedManagers[userId] = [];
//     }

//     // ✅ Convert to number
//     const managerIdNumber = Number(managerId);
//     const index = this.selectedManagers[userId].indexOf(managerIdNumber);
    
//     if (index > -1) {
//       // Remove manager
//       this.selectedManagers[userId].splice(index, 1);
//       console.log(`✅ Removed manager ${managerIdNumber} from user ${userId}`);
//     } else {
//       // Add manager (as number)
//       this.selectedManagers[userId].push(managerIdNumber);
//       console.log(`✅ Added manager ${managerIdNumber} to user ${userId}`);
//     }

//     console.log('✅ Current selection:', this.selectedManagers[userId]);
//     console.log('   Types:', this.selectedManagers[userId].map(id => typeof id));
//   }

//   // ============================================
//   // ✅ FIXED: UPDATE REPORTING RELATIONSHIP
//   // ============================================
//   updateReporting(user: any): void {
//     const newManagerIds = this.selectedManagers[user.id] || [];
    
//     // ✅ Ensure all IDs are numbers
//     const managerIdsAsNumbers = newManagerIds.map(id => Number(id));
    
//     // Get manager names for confirmation
//     let managerNames = 'No Managers';
//     if (managerIdsAsNumbers.length > 0) {
//       const managers = this.hierarchyUsers.filter(u => managerIdsAsNumbers.includes(u.id));
//       managerNames = managers.map(m => m.fullName).join(', ');
//     }
    
//     const confirmMsg = `Update reporting for ${user.fullName}?\n\nReports to:\n${managerNames}`;
//     if (!confirm(confirmMsg)) {
//       return;
//     }

//     this.updatingUsers[user.id] = true;

//     // ✅ Send array of manager IDs (as numbers)
//     const updateData = {
//       reportsToIds: managerIdsAsNumbers
//     };

//     console.log('📤 Updating reporting:', updateData);
//     console.log('   ID types:', updateData.reportsToIds.map(id => typeof id));

//     this.dataService.updateHierarchyUser(user.id, updateData).subscribe({
//       next: (response: any) => {
//         console.log('✅ Reporting Updated:', response);
//         this.updatingUsers[user.id] = false;

//         if (response?.success) {
//           alert('✅ Reporting relationship updated successfully!');
//           this.loadHierarchyUsers(); // Reload to refresh tree
//         } else {
//           alert('❌ ' + (response?.message || 'Failed to update reporting'));
//         }
//       },
//       error: (error: any) => {
//         this.updatingUsers[user.id] = false;
//         console.error('❌ Error updating reporting:', error);
//         console.error('   Error details:', error.error);
//         alert('❌ ' + (error.error?.message || error.error?.error || 'Failed to update reporting'));
//       }
//     });
//   }

//   // ============================================
//   // BUILD HIERARCHY TREE
//   // ============================================
//   buildHierarchyTree(): void {
//     // Create a map of users by ID for quick lookup
//     const userMap = new Map<number, any>();
//     this.hierarchyUsers.forEach(user => {
//       userMap.set(user.id, { ...user, reports: [] });
//     });

//     // Build parent-child relationships (handling multiple managers)
//     this.hierarchyUsers.forEach(user => {
//       if (user.reportsTo && Array.isArray(user.reportsTo) && user.reportsTo.length > 0) {
//         // Add this user as a report to ALL their managers
//         user.reportsTo.forEach((manager: any) => {
//           const managerNode = userMap.get(manager.id);
//           if (managerNode) {
//             const userNode = userMap.get(user.id);
//             if (userNode && !managerNode.reports.some((r: any) => r.id === user.id)) {
//               managerNode.reports.push(userNode);
//             }
//           }
//         });
//       } else if (user.reportsToId) {
//         // Backward compatibility
//         const manager = userMap.get(user.reportsToId);
//         if (manager) {
//           manager.reports.push(userMap.get(user.id));
//         }
//       }
//     });

//     // Find top-level users (no manager)
//     this.hierarchyTree = this.hierarchyUsers
//       .filter(user => {
//         if (!user.reportsTo || user.reportsTo.length === 0) {
//           return !user.reportsToId;
//         }
//         return false;
//       })
//       .map(user => userMap.get(user.id))
//       .filter(user => user !== undefined)
//       .sort((a, b) => a.hierarchyLevelOrder - b.hierarchyLevelOrder);

//     console.log('✅ Hierarchy Tree Built:', this.hierarchyTree);
//   }

//   // ============================================
//   // UTILITY METHODS
//   // ============================================
//   getLevelBadgeColor(order: number): string {
//     if (order <= 10) return 'danger';   // CEO
//     if (order <= 20) return 'warning';  // COO
//     if (order <= 30) return 'info';     // Manager
//     return 'secondary';
//   }
// }

import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { DataService } from '../../../shared/service/DataService';
import {
  ContainerComponent, RowComponent, ColComponent, CardComponent,
  CardHeaderComponent, CardBodyComponent, ButtonDirective,
  FormControlDirective, BadgeComponent
} from '@coreui/angular';
import { IconDirective } from '@coreui/icons-angular';

@Component({
  selector: 'app-reporting-structure',
  templateUrl: './reporting-structure.component.html',
  styleUrls: ['./reporting-structure.component.css'],
  standalone: true,
  imports: [
    CommonModule, FormsModule, RouterLink,
    ContainerComponent, RowComponent, ColComponent, CardComponent,
    CardHeaderComponent, CardBodyComponent, ButtonDirective,
    FormControlDirective, BadgeComponent, IconDirective
  ]
})
export class ReportingStructureComponent implements OnInit {

  hierarchyUsers: any[] = [];
  hierarchyTree: any[] = [];
  selectedManagers: { [userId: number]: number[] } = {};
  updatingUsers: { [userId: number]: boolean } = {};
  companyName: string = '';
  fullName: string = '';
  email: string = '';
  role: string = 'Super Administrator';
  isLoading: boolean = false;

  constructor(private dataService: DataService) {}

  ngOnInit(): void {
    this.companyName = localStorage.getItem('companyName') || 'Unknown Company';
    this.fullName = localStorage.getItem('fullName') || 'SuperAdmin';
    this.email = localStorage.getItem('email') || 'admin@company.com';
    this.loadHierarchyUsers();
  }

  loadHierarchyUsers(): void {
    this.isLoading = true;

    this.dataService.getHierarchyUsersByCompany(this.companyName).subscribe({
      next: (response: any) => {
        console.log('✅ Reporting Structure - Full Response:', response);
        this.isLoading = false;

        if (response?.success && response?.data) {
          this.hierarchyUsers = response.data;
          
          this.hierarchyUsers.forEach(user => {
            console.log(`👤 ${user.fullName}:`);
            console.log('   Current Managers (reportsTo):', user.reportsTo);
            
            if (user.reportsTo && Array.isArray(user.reportsTo)) {
              this.selectedManagers[user.id] = user.reportsTo.map((m: any) => Number(m.id));
              console.log('   ✅ Initialized selected managers:', this.selectedManagers[user.id]);
            } else if (user.reportsToId) {
              this.selectedManagers[user.id] = [Number(user.reportsToId)];
              console.log('   ✅ Initialized from reportsToId:', this.selectedManagers[user.id]);
            } else {
              this.selectedManagers[user.id] = [];
              console.log('   ℹ️ No managers assigned');
            }
            
            this.updatingUsers[user.id] = false;
          });

          console.log('✅ Final selectedManagers object:', this.selectedManagers);

          this.buildHierarchyTree();
          
          console.log(`✅ Loaded ${this.hierarchyUsers.length} users`);
        } else {
          this.hierarchyUsers = [];
        }
      },
      error: (error: any) => {
        this.isLoading = false;
        console.error('❌ Error loading hierarchy users:', error);
      }
    });
  }

  getPotentialManagers(user: any): any[] {
    return this.hierarchyUsers.filter(potentialManager => {
      if (potentialManager.id === user.id) {
        return false;
      }
      return potentialManager.hierarchyLevelOrder < user.hierarchyLevelOrder;
    });
  }

  isManagerSelected(userId: number, managerId: number): boolean {
    if (!this.selectedManagers[userId]) {
      return false;
    }
    return this.selectedManagers[userId].includes(Number(managerId));
  }

  toggleManager(userId: number, managerId: number): void {
    if (!this.selectedManagers[userId]) {
      this.selectedManagers[userId] = [];
    }

    const managerIdNumber = Number(managerId);
    const index = this.selectedManagers[userId].indexOf(managerIdNumber);
    
    if (index > -1) {
      this.selectedManagers[userId].splice(index, 1);
      console.log(`✅ Removed manager ${managerIdNumber} from user ${userId}`);
    } else {
      this.selectedManagers[userId].push(managerIdNumber);
      console.log(`✅ Added manager ${managerIdNumber} to user ${userId}`);
    }

    console.log('✅ Current selection:', this.selectedManagers[userId]);
  }

  updateReporting(user: any): void {
    const newManagerIds = this.selectedManagers[user.id] || [];
    const managerIdsAsNumbers = newManagerIds.map(id => Number(id));
    
    let managerNames = 'No Managers';
    if (managerIdsAsNumbers.length > 0) {
      const managers = this.hierarchyUsers.filter(u => managerIdsAsNumbers.includes(u.id));
      managerNames = managers.map(m => m.fullName).join(', ');
    }
    
    const confirmMsg = `Update reporting for ${user.fullName}?\n\nReports to:\n${managerNames}`;
    if (!confirm(confirmMsg)) {
      return;
    }

    this.updatingUsers[user.id] = true;

    const updateData = {
      reportsToIds: managerIdsAsNumbers
    };

    console.log('📤 Updating reporting:', updateData);

    this.dataService.updateHierarchyUser(user.id, updateData).subscribe({
      next: (response: any) => {
        console.log('✅ Reporting Updated:', response);
        this.updatingUsers[user.id] = false;

        if (response?.success) {
          alert('✅ Reporting relationship updated successfully!');
          this.loadHierarchyUsers();
        } else {
          alert('❌ ' + (response?.message || 'Failed to update reporting'));
        }
      },
      error: (error: any) => {
        this.updatingUsers[user.id] = false;
        console.error('❌ Error updating reporting:', error);
        alert('❌ ' + (error.error?.message || error.error?.error || 'Failed to update reporting'));
      }
    });
  }

  buildHierarchyTree(): void {
    const userMap = new Map<number, any>();
    this.hierarchyUsers.forEach(user => {
      userMap.set(user.id, { ...user, reports: [] });
    });

    this.hierarchyUsers.forEach(user => {
      if (user.reportsTo && Array.isArray(user.reportsTo) && user.reportsTo.length > 0) {
        user.reportsTo.forEach((manager: any) => {
          const managerNode = userMap.get(manager.id);
          if (managerNode) {
            const userNode = userMap.get(user.id);
            if (userNode && !managerNode.reports.some((r: any) => r.id === user.id)) {
              managerNode.reports.push(userNode);
            }
          }
        });
      } else if (user.reportsToId) {
        const manager = userMap.get(user.reportsToId);
        if (manager) {
          manager.reports.push(userMap.get(user.id));
        }
      }
    });

    this.hierarchyTree = this.hierarchyUsers
      .filter(user => {
        if (!user.reportsTo || user.reportsTo.length === 0) {
          return !user.reportsToId;
        }
        return false;
      })
      .map(user => userMap.get(user.id))
      .filter(user => user !== undefined)
      .sort((a, b) => a.hierarchyLevelOrder - b.hierarchyLevelOrder);

    console.log('✅ Hierarchy Tree Built:', this.hierarchyTree);
  }

  getLevelBadgeColor(order: number): string {
    if (order <= 10) return 'danger';
    if (order <= 20) return 'warning';
    if (order <= 30) return 'info';
    return 'secondary';
  }

  getInitials(name: string): string {
    if (!name) return 'NA';
    const parts = name.split(' ');
    if (parts.length >= 2) {
      return (parts[0][0] + parts[1][0]).toUpperCase();
    }
    return name.substring(0, 2).toUpperCase();
  }
}