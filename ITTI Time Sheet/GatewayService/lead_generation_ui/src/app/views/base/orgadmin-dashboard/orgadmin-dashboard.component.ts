import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { DataService } from '../../../shared/service/DataService';
import {
  ContainerComponent, RowComponent, ColComponent, CardComponent,
  CardHeaderComponent, CardBodyComponent, ButtonDirective, BadgeComponent
} from '@coreui/angular';
import { IconDirective } from '@coreui/icons-angular';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-orgadmin-dashboard',
  templateUrl: './orgadmin-dashboard.component.html',
  styleUrls: ['./orgadmin-dashboard.component.css'],
  standalone: true,
  imports: [
    CommonModule, ContainerComponent, RowComponent, ColComponent,
    CardComponent, CardHeaderComponent, CardBodyComponent, ButtonDirective,
    IconDirective, BadgeComponent
  ]
})
export class OrgAdminDashboardComponent implements OnInit {

  // User Details
  fullName: string = '';
  email: string = '';
  companyName: string = '';
  phone: string = '';
  role: string = 'Organization Administrator';
  
  isLoading: boolean = false;

  // Statistics
  stats = {
    hierarchyLevels: 0,
    hierarchyUsers: 0,
    reportingLinks: 0,
    activeUsers: 0
  };

  // Data Arrays
  hierarchyLevels: any[] = [];
  allUsers: any[] = [];
  recentUsers: any[] = [];

  constructor(
    private router: Router,
    private dataService: DataService
  ) {}

  ngOnInit(): void {
    this.loadUserDetails();
    this.loadDashboardData();
  }

  loadUserDetails(): void {
    this.fullName = localStorage.getItem('fullName') || 'Admin';
    this.email = localStorage.getItem('email') || 'admin@company.com';
    this.companyName = localStorage.getItem('companyName') || 'Unknown Company';
    this.phone = localStorage.getItem('phone') || 'Not provided';
  }

  loadDashboardData(): void {
    this.isLoading = true;

    forkJoin({
      levels: this.dataService.getHierarchyLevelsByCompany(this.companyName),
      users: this.dataService.getHierarchyUsersByCompany(this.companyName)
    }).subscribe({
      next: (results: any) => {
        console.log('✅ Dashboard Data:', results);

        // Process Hierarchy Levels
        if (results.levels?.success && results.levels?.data) {
          this.hierarchyLevels = results.levels.data;
          this.stats.hierarchyLevels = this.hierarchyLevels.length;
        }

        // Process Users
        if (results.users?.success && results.users?.data) {
          this.allUsers = results.users.data;
          
          this.stats.hierarchyUsers = this.allUsers.length;
          this.stats.activeUsers = this.allUsers.filter((u: any) => u.isActive === true).length;
          
          // Reporting Links Count
          this.stats.reportingLinks = 0;
          this.allUsers.forEach((user: any) => {
            if (user.reportsTo && Array.isArray(user.reportsTo)) {
              this.stats.reportingLinks += user.reportsTo.length;
            } else if (user.reportsToId) {
              this.stats.reportingLinks += 1;
            }
          });

          // Get recent users (last 5)
          this.recentUsers = this.allUsers
            .sort((a: any, b: any) => {
              const dateA = new Date(a.createdAt).getTime();
              const dateB = new Date(b.createdAt).getTime();
              return dateB - dateA;
            })
            .slice(0, 5);

          console.log('📊 Dashboard Stats:', this.stats);
        }

        this.isLoading = false;
      },
      error: (error: any) => {
        console.error('❌ Error loading dashboard data:', error);
        this.isLoading = false;
        this.stats = {
          hierarchyLevels: 0,
          hierarchyUsers: 0,
          reportingLinks: 0,
          activeUsers: 0
        };
      }
    });
  }

  navigateTo(route: string): void {
    this.router.navigate([route]);
  }

  getInitials(name: string): string {
    if (!name) return 'NA';
    const parts = name.split(' ');
    if (parts.length >= 2) {
      return (parts[0][0] + parts[1][0]).toUpperCase();
    }
    return name.substring(0, 2).toUpperCase();
  }

  getActivePercentage(): number {
    if (this.stats.hierarchyUsers === 0) return 0;
    return Math.round((this.stats.activeUsers / this.stats.hierarchyUsers) * 100);
  }

  getUserCountByLevel(levelId: number): number {
    if (!this.allUsers || this.allUsers.length === 0) return 0;
    return this.allUsers.filter(u => u.hierarchyLevelId === levelId).length;
  }

  getLevelColor(levelOrder: number): string {
    if (levelOrder <= 10) return 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)';
    if (levelOrder <= 20) return 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)';
    if (levelOrder <= 30) return 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)';
    return 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)';
  }

  getLevelBadgeColor(order: number): string {
    if (order <= 10) return 'danger';
    if (order <= 20) return 'warning';
    if (order <= 30) return 'info';
    return 'secondary';
  }

  formatDate(dateStr: string): string {
    if (!dateStr) return 'N/A';
    
    try {
      const date = new Date(dateStr);
      if (isNaN(date.getTime())) return 'N/A';
      return date.toLocaleDateString('en-US', { 
        year: 'numeric', 
        month: 'short', 
        day: 'numeric' 
      });
    } catch (e) {
      return 'N/A';
    }
  }
}