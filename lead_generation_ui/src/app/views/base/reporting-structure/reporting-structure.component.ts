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