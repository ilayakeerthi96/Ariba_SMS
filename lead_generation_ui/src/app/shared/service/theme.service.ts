

// // import { Injectable, Inject } from '@angular/core';
// // import { DOCUMENT } from '@angular/common';
// // import { BehaviorSubject } from 'rxjs';
// // import { catchError } from 'rxjs/operators';
// // import { of } from 'rxjs';
// // import { DataService } from './DataService';

// // export type ThemeName = 'default' | 'orange' | 'indigo' | 'teal' | 'purple' | 'pink' | 'yellow' | 'green' | 'grey';

// // export interface Theme {
// //   name: ThemeName;
// //   label: string;
// //   color: string;
// // }

// // @Injectable({ providedIn: 'root' })
// // export class ThemeService {

// //   readonly themes: Theme[] = [
// //     { name: 'default', label: 'Default',     color: '#3c4b64' },
// //     { name: 'orange',  label: 'Orange',      color: '#f47820' },
// //     { name: 'indigo',  label: 'Indigo',      color: '#3f51b5' },
// //     { name: 'teal',    label: 'Teal',        color: '#009688' },
// //     { name: 'purple',  label: 'Purple',      color: '#7b1fa2' },
// //     { name: 'pink',    label: 'Baby Pink',   color: '#f48fb1' },
// //     { name: 'yellow',  label: 'Pale Yellow', color: '#f9d976' },
// //     { name: 'green',   label: 'Pale Green',  color: '#a8d5b5' },
// //     { name: 'grey',    label: 'Soft Grey',   color: '#607d8b' },
// //   ];

// //   private readonly STORAGE_KEY = 'app-theme';
// //   private readonly BACKEND_SAVE_ROLES = ['SUPER_ADMIN', 'ORGANIZATION_ADMIN'];

// //   private _active$ = new BehaviorSubject<ThemeName>(this.getSaved());
// //   readonly active$ = this._active$.asObservable();
// //   get active(): ThemeName { return this._active$.value; }

// //   constructor(
// //     @Inject(DOCUMENT) private doc: Document,
// //     private dataService: DataService
// //   ) {
// //     const saved = this.getSaved();
// //     if (saved && saved !== 'default') {
// //       this.applyToDOM(saved);
// //     }
// //   }

// //   loadThemeOnLogin(): void {
// //     const role   = localStorage.getItem('role') || '';
// //     const userId = localStorage.getItem('userId');

// //     if (role === 'SUPER_ADMIN' && userId) {
// //       this.dataService.getSuperAdminTheme(Number(userId))
// //         .pipe(catchError(() => of(null)))
// //         .subscribe((res: any) => {
// //           if (res?.success && res?.data) this.applyLocally(res.data as ThemeName);
// //         });
// //       return;
// //     }

// //     if (role === 'ORGANIZATION_ADMIN' && userId) {
// //       this.dataService.getOrgAdminTheme(Number(userId))
// //         .pipe(catchError(() => of(null)))
// //         .subscribe((res: any) => {
// //           if (res?.success && res?.data) this.applyLocally(res.data as ThemeName);
// //         });
// //       return;
// //     }

// //     if (role === 'ROLE_SUPPLIER') {
// //       const supplierId = localStorage.getItem('supplierId');
// //       if (supplierId) {
// //         this.dataService.getSupplierTheme(Number(supplierId))
// //           .pipe(catchError(() => of(null)))
// //           .subscribe((res: any) => {
// //             this.applyLocally(res?.success && res?.data ? res.data as ThemeName : this.getSaved());
// //           });
// //       } else {
// //         this.applyLocally(this.getSaved());
// //       }
// //       return;
// //     }

// //     const companyName = localStorage.getItem('companyName') || '';
// //     if (!companyName) { this.applyLocally(this.getSaved()); return; }

// //     this.dataService.getOrganizationAdminsByCompany(companyName)
// //       .pipe(catchError(() => of(null)))
// //       .subscribe((res: any) => {
// //         const list: any[] = Array.isArray(res?.data) ? res.data : Array.isArray(res) ? res : [];
// //         const admin = list.find((a: any) => a.isActive) ?? list[0];
// //         if (!admin?.id) { this.applyLocally(this.getSaved()); return; }

// //         this.dataService.getOrgAdminTheme(admin.id)
// //           .pipe(catchError(() => of(null)))
// //           .subscribe((tr: any) => {
// //             this.applyLocally(tr?.success && tr?.data ? tr.data as ThemeName : this.getSaved());
// //           });
// //       });
// //   }

// //   setTheme(name: ThemeName): void {
// //     this.applyLocally(name);
// //     this.saveToBackend(name);
// //   }

// //   canChangeTheme(): boolean { return true; }

// //   private applyLocally(name: ThemeName): void {
// //     this.applyToDOM(name);
// //     this._active$.next(name);
// //     localStorage.setItem(this.STORAGE_KEY, name);
// //   }

// //   private applyToDOM(name: ThemeName): void {
// //     if (!name || name === 'default') {
// //       this.doc.body.removeAttribute('data-theme');
// //     } else {
// //       this.doc.body.setAttribute('data-theme', name);
// //     }
// //   }

// //   private getSaved(): ThemeName {
// //     return (localStorage.getItem(this.STORAGE_KEY) as ThemeName) || 'default';
// //   }

// //   private saveToBackend(name: ThemeName): void {
// //     const role   = localStorage.getItem('role') || '';
// //     const userId = localStorage.getItem('userId');
// //     if (!userId || !this.BACKEND_SAVE_ROLES.includes(role)) return;

// //     const obs = role === 'SUPER_ADMIN'
// //       ? this.dataService.saveSuperAdminTheme(Number(userId), name)
// //       : this.dataService.saveOrgAdminTheme(Number(userId), name);

// //     obs.pipe(catchError(() => of(null))).subscribe();
// //   }
// // }


// import { Injectable, Inject } from '@angular/core';
// import { DOCUMENT } from '@angular/common';
// import { BehaviorSubject } from 'rxjs';
// import { catchError } from 'rxjs/operators';
// import { of } from 'rxjs';
// import { DataService } from './DataService';

// export type ThemeName = 'default' | 'orange' | 'indigo' | 'teal' | 'purple' | 'pink' | 'yellow' | 'green' | 'grey';

// export interface Theme {
//   name: ThemeName;
//   label: string;
//   color: string;
// }

// @Injectable({ providedIn: 'root' })
// export class ThemeService {

//   readonly themes: Theme[] = [
//     { name: 'default', label: 'Default',     color: '#3c4b64' },
//     { name: 'orange',  label: 'Orange',      color: '#f47820' },
//     { name: 'indigo',  label: 'Indigo',      color: '#3f51b5' },
//     { name: 'teal',    label: 'Teal',        color: '#009688' },
//     { name: 'purple',  label: 'Purple',      color: '#7b1fa2' },
//     { name: 'pink',    label: 'Baby Pink',   color: '#f48fb1' },
//     { name: 'yellow',  label: 'Pale Yellow', color: '#f9d976' },
//     { name: 'green',   label: 'Pale Green',  color: '#a8d5b5' },
//     { name: 'grey',    label: 'Soft Grey',   color: '#607d8b' },
//   ];

//   private readonly STORAGE_KEY = 'app-theme';

//   // ✅ ONLY these two roles can see & use the theme switcher
//   private readonly ADMIN_ROLES = ['SUPER_ADMIN', 'ORGANIZATION_ADMIN'];

//   private _active$ = new BehaviorSubject<ThemeName>(this.getSaved());
//   readonly active$ = this._active$.asObservable();
//   get active(): ThemeName { return this._active$.value; }

//   constructor(
//     @Inject(DOCUMENT) private doc: Document,
//     private dataService: DataService
//   ) {
//     const saved = this.getSaved();
//     if (saved && saved !== 'default') {
//       this.applyToDOM(saved);
//     }
//   }

//   // ============================================================
//   // Called after every login — loads correct theme per role
//   // ============================================================
//   loadThemeOnLogin(): void {
//     const role   = localStorage.getItem('role') || '';
//     const userId = localStorage.getItem('userId');

//     // ── SUPER ADMIN: fetch own saved theme ────────────────────
//     if (role === 'SUPER_ADMIN' && userId) {
//       this.dataService.getSuperAdminTheme(Number(userId))
//         .pipe(catchError(() => of(null)))
//         .subscribe((res: any) => {
//           if (res?.success && res?.data) {
//             this.applyLocally(res.data as ThemeName);
//           }
//         });
//       return;
//     }

//     // ── ORGANIZATION ADMIN: fetch own saved theme ─────────────
//     if (role === 'ORGANIZATION_ADMIN' && userId) {
//       this.dataService.getOrgAdminTheme(Number(userId))
//         .pipe(catchError(() => of(null)))
//         .subscribe((res: any) => {
//           if (res?.success && res?.data) {
//             this.applyLocally(res.data as ThemeName);
//           }
//         });
//       return;
//     }

//     // ── ALL OTHER ROLES (Buyer, Supplier, Hierarchy) ──────────
//     // These users CANNOT change theme.
//     // They always inherit from their Org Admin's saved theme.
//     const companyName = localStorage.getItem('companyName') || '';
//     if (companyName) {
//       this._loadOrgAdminThemeByCompany(companyName, role);
//     } else if (role === 'ROLE_SUPPLIER') {
//       // Fallback: try supplier-specific theme if no companyName
//       const supplierId = localStorage.getItem('supplierId');
//       if (supplierId) {
//         this.dataService.getSupplierTheme(Number(supplierId))
//           .pipe(catchError(() => of(null)))
//           .subscribe((res: any) => {
//             this.applyLocally(res?.success && res?.data ? res.data as ThemeName : 'default');
//           });
//       } else {
//         this.applyLocally('default');
//       }
//     } else {
//       this.applyLocally('default');
//     }
//   }

//   // ── Fetch org admin's theme and apply ────────────────────────
//   private _loadOrgAdminThemeByCompany(companyName: string, role: string): void {
//     this.dataService.getOrganizationAdminsByCompany(companyName)
//       .pipe(catchError(() => of(null)))
//       .subscribe((res: any) => {
//         const list: any[] = Array.isArray(res?.data)
//           ? res.data
//           : Array.isArray(res) ? res : [];

//         const activeAdmin = list.find((a: any) => a.isActive === true) ?? list[0];

//         if (!activeAdmin?.id) {
//           console.warn('⚠️ [THEME] No active OrgAdmin for company:', companyName);
//           this.applyLocally('default');
//           return;
//         }

//         this.dataService.getOrgAdminTheme(activeAdmin.id)
//           .pipe(catchError(() => of(null)))
//           .subscribe((tr: any) => {
//             if (tr?.success && tr?.data) {
//               console.log('🎨 [THEME] Inherited org theme for', role, ':', tr.data);
//               this.applyLocally(tr.data as ThemeName);
//             } else {
//               this.applyLocally('default');
//             }
//           });
//       });
//   }

//   // ── Called by ThemeSwitcherComponent (admins only) ──────────
//   setTheme(name: ThemeName): void {
//     this.applyLocally(name);
//     this.saveToBackend(name);
//   }

//   // ✅ ONLY SUPER_ADMIN and ORGANIZATION_ADMIN see the switcher
//   // Buyers, Suppliers, Hierarchy → switcher is completely hidden
//   canChangeTheme(): boolean {
//     const role = localStorage.getItem('role') || '';
//     return this.ADMIN_ROLES.includes(role);
//   }

//   // ── Private helpers ─────────────────────────────────────────
//   private applyLocally(name: ThemeName): void {
//     this.applyToDOM(name);
//     this._active$.next(name);
//     localStorage.setItem(this.STORAGE_KEY, name);
//   }

//   private applyToDOM(name: ThemeName): void {
//     if (!name || name === 'default') {
//       this.doc.body.removeAttribute('data-theme');
//     } else {
//       this.doc.body.setAttribute('data-theme', name);
//     }
//   }

//   private getSaved(): ThemeName {
//     return (localStorage.getItem(this.STORAGE_KEY) as ThemeName) || 'default';
//   }

//   // ── Save to backend — admins only ───────────────────────────
//   private saveToBackend(name: ThemeName): void {
//     const role   = localStorage.getItem('role') || '';
//     const userId = localStorage.getItem('userId');
//     if (!userId) return;

//     if (role === 'SUPER_ADMIN') {
//       this.dataService.saveSuperAdminTheme(Number(userId), name)
//         .pipe(catchError(() => of(null)))
//         .subscribe(res => {
//           if (res?.success) console.log('✅ [THEME] SuperAdmin theme saved:', name);
//         });

//     } else if (role === 'ORGANIZATION_ADMIN') {
//       // Saves to DB + bulk-updates all suppliers under this org
//       // (handled by backend: SupplierRepository.updateThemeByCompanyName)
//       this.dataService.saveOrgAdminTheme(Number(userId), name)
//         .pipe(catchError(() => of(null)))
//         .subscribe(res => {
//           if (res?.success) console.log('✅ [THEME] OrgAdmin theme saved + synced to suppliers:', name);
//         });
//     }
//     // Non-admins: never save — they always load from org admin on login
//   }
// }


import { Injectable, Inject } from '@angular/core';
import { DOCUMENT } from '@angular/common';
import { BehaviorSubject } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { of } from 'rxjs';
import { DataService } from './DataService';

export type ThemeName = 'default' | 'orange' | 'indigo' | 'teal' | 'purple' | 'pink' | 'yellow' | 'green' | 'grey';

export interface Theme {
  name: ThemeName;
  label: string;
  color: string;
}

@Injectable({ providedIn: 'root' })
export class ThemeService {

  readonly themes: Theme[] = [
    { name: 'default', label: 'Default',     color: '#3c4b64' },
    { name: 'orange',  label: 'Orange',      color: '#f47820' },
    { name: 'indigo',  label: 'Indigo',      color: '#3f51b5' },
    { name: 'teal',    label: 'Teal',        color: '#009688' },
    { name: 'purple',  label: 'Purple',      color: '#7b1fa2' },
    { name: 'pink',    label: 'Baby Pink',   color: '#f48fb1' },
    { name: 'yellow',  label: 'Pale Yellow', color: '#f9d976' },
    { name: 'green',   label: 'Pale Green',  color: '#a8d5b5' },
    { name: 'grey',    label: 'Soft Grey',   color: '#607d8b' },
  ];

  private readonly STORAGE_KEY  = 'app-theme';
  private readonly ADMIN_ROLES  = ['SUPER_ADMIN', 'ORGANIZATION_ADMIN'];
  private readonly VALID_THEMES: ThemeName[] = [
    'default','orange','indigo','teal','purple','pink','yellow','green','grey'
  ];

  private _active$ = new BehaviorSubject<ThemeName>(this.getSaved());
  readonly active$ = this._active$.asObservable();
  get active(): ThemeName { return this._active$.value; }

  constructor(
    @Inject(DOCUMENT) private doc: Document,
    private dataService: DataService
  ) {
    const saved = this.getSaved();
    if (saved && saved !== 'default') {
      this.applyToDOM(saved);
    }
  }

  // ============================================================
  // Called after every login — localStorage must be fully set first
  // ============================================================
  loadThemeOnLogin(): void {
    const role   = localStorage.getItem('role') || '';
    const userId = localStorage.getItem('userId');

    console.log('🎨 [THEME] loadThemeOnLogin — role:', role);

    // ── SUPER ADMIN ───────────────────────────────────────────
    if (role === 'SUPER_ADMIN' && userId) {
      this.dataService.getSuperAdminTheme(Number(userId))
        .pipe(catchError(() => of(null)))
        .subscribe((res: any) => {
          this.applyLocally(this._extract(res));
        });
      return;
    }

    // ── ORGANIZATION ADMIN ────────────────────────────────────
    if (role === 'ORGANIZATION_ADMIN' && userId) {
      this.dataService.getOrgAdminTheme(Number(userId))
        .pipe(catchError(() => of(null)))
        .subscribe((res: any) => {
          this.applyLocally(this._extract(res));
        });
      return;
    }

    // ── SUPPLIER ──────────────────────────────────────────────
    // ✅ Theme is stored directly in localStorage as 'supplierTheme'
    // set by handleSupplierLogin() from the login response
    if (role === 'ROLE_SUPPLIER') {
      const supplierTheme = localStorage.getItem('supplierTheme');
      if (supplierTheme && this.VALID_THEMES.includes(supplierTheme as ThemeName)) {
        console.log('✅ [THEME] Supplier theme from login response:', supplierTheme);
        this.applyLocally(supplierTheme as ThemeName);
      } else {
        console.log('ℹ️ [THEME] No supplier theme — applying default');
        this.applyLocally('default');
      }
      return;
    }

    // ── BUYER ─────────────────────────────────────────────────
    // Buyer belongs to org admin's company — fetch by companyName
    if (role === 'ROLE_BUYER') {
      const companyName = localStorage.getItem('companyName') || '';
      if (companyName) {
        console.log('🎨 [THEME] Buyer — fetching org admin theme by company:', companyName);
        this._fetchByCompany(companyName, role);
      } else {
        this.applyLocally('default');
      }
      return;
    }

    // ── HIERARCHY ─────────────────────────────────────────────
    // Hierarchy users also belong to org admin's company
    const companyName = localStorage.getItem('companyName') || '';
    if (companyName) {
      console.log('🎨 [THEME] Hierarchy — fetching org admin theme by company:', companyName);
      this._fetchByCompany(companyName, role);
    } else {
      this.applyLocally('default');
    }
  }

  // ── Fetch org admin theme by company name ─────────────────────
  private _fetchByCompany(companyName: string, role: string): void {
    this.dataService.getOrganizationAdminsByCompany(companyName)
      .pipe(catchError(e => {
        console.warn('⚠️ [THEME] getOrgAdminsByCompany error:', e);
        return of(null);
      }))
      .subscribe((res: any) => {
        let list: any[] = [];
        if (Array.isArray(res?.data))       list = res.data;
        else if (Array.isArray(res))        list = res;
        else if (res?.success && res?.data) list = Array.isArray(res.data) ? res.data : [res.data];

        console.log('🎨 [THEME] OrgAdmins for', companyName, ':', list.length);

        if (!list.length) { this.applyLocally('default'); return; }

        const admin = list.find((a: any) => a.isActive === true) ?? list[0];
        if (!admin?.id) { this.applyLocally('default'); return; }

        this.dataService.getOrgAdminTheme(admin.id)
          .pipe(catchError(() => of(null)))
          .subscribe((tr: any) => {
            console.log('✅ [THEME] OrgAdmin theme for', role, ':', tr);
            this.applyLocally(this._extract(tr));
          });
      });
  }

  // ── Extract valid ThemeName from API response ─────────────────
  private _extract(res: any): ThemeName {
    const raw = res?.data ?? res?.theme ?? null;
    if (raw && this.VALID_THEMES.includes(raw as ThemeName)) {
      return raw as ThemeName;
    }
    return 'default';
  }

  // ── Called by ThemeSwitcherComponent ─────────────────────────
  setTheme(name: ThemeName): void {
    this.applyLocally(name);
    this.saveToBackend(name);
  }

  canChangeTheme(): boolean {
    return this.ADMIN_ROLES.includes(localStorage.getItem('role') || '');
  }

  // ── Private helpers ───────────────────────────────────────────
  private applyLocally(name: ThemeName): void {
    this.applyToDOM(name);
    this._active$.next(name);
    localStorage.setItem(this.STORAGE_KEY, name);
    console.log('✅ [THEME] Applied:', name);
  }

  private applyToDOM(name: ThemeName): void {
    if (!name || name === 'default') {
      this.doc.body.removeAttribute('data-theme');
    } else {
      this.doc.body.setAttribute('data-theme', name);
    }
  }

  private getSaved(): ThemeName {
    return (localStorage.getItem(this.STORAGE_KEY) as ThemeName) || 'default';
  }

  private saveToBackend(name: ThemeName): void {
    const role   = localStorage.getItem('role') || '';
    const userId = localStorage.getItem('userId');
    if (!userId) return;

    if (role === 'SUPER_ADMIN') {
      this.dataService.saveSuperAdminTheme(Number(userId), name)
        .pipe(catchError(() => of(null))).subscribe();
    } else if (role === 'ORGANIZATION_ADMIN') {
      this.dataService.saveOrgAdminTheme(Number(userId), name)
        .pipe(catchError(() => of(null))).subscribe();
    }
  }
}