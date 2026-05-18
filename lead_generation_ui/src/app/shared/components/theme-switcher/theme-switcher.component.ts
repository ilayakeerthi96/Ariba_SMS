

// import { Component, inject, HostListener, ElementRef } from '@angular/core';
// import { CommonModule } from '@angular/common';
// import { ThemeService, Theme } from '../../service/theme.service';

// @Component({
//   selector: 'app-theme-switcher',
//   standalone: true,
//   imports: [CommonModule],
//   template: `
//     <div class="ts-root" [class.open]="open">

//       <!-- Trigger button -->
//       <button class="ts-btn" (click)="open = !open" title="Change theme">
//         <svg class="ts-palette-icon" viewBox="0 0 24 24" fill="none">
//           <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10c.83 0 1.5-.67 1.5-1.5 0-.39-.15-.74-.39-1.01-.23-.26-.38-.61-.38-.99 0-.83.67-1.5 1.5-1.5H16c2.76 0 5-2.24 5-5 0-4.42-4.03-8-9-8z"
//             fill="currentColor" opacity="0.15"/>
//           <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10c.83 0 1.5-.67 1.5-1.5 0-.39-.15-.74-.39-1.01-.23-.26-.38-.61-.38-.99 0-.83.67-1.5 1.5-1.5H16c2.76 0 5-2.24 5-5 0-4.42-4.03-8-9-8z"
//             stroke="currentColor" stroke-width="1.5" fill="none"/>
//           <circle cx="6.5"  cy="11.5" r="1.5" fill="#f47820"/>
//           <circle cx="9.5"  cy="7.5"  r="1.5" fill="#7b1fa2"/>
//           <circle cx="14.5" cy="7.5"  r="1.5" fill="#009688"/>
//           <circle cx="17.5" cy="11.5" r="1.5" fill="#3f51b5"/>
//         </svg>
//         <span class="ts-active-dot" [style.background]="activeColor"></span>
//         <svg class="ts-chevron" [class.flipped]="open" viewBox="0 0 20 20" fill="currentColor">
//           <path fill-rule="evenodd" d="M5.293 7.293a1 1 0 011.414 0L10 10.586l3.293-3.293a1 1 0 111.414 1.414l-4 4a1 1 0 01-1.414 0l-4-4a1 1 0 010-1.414z" clip-rule="evenodd"/>
//         </svg>
//       </button>

//       <!-- Dropdown panel — completely isolated from theme colors -->
//       <div class="ts-panel" *ngIf="open">

//         <div class="ts-panel-title">
//           <svg viewBox="0 0 24 24" fill="none" style="width:16px;height:16px;flex-shrink:0;">
//             <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10c.83 0 1.5-.67 1.5-1.5 0-.39-.15-.74-.39-1.01-.23-.26-.38-.61-.38-.99 0-.83.67-1.5 1.5-1.5H16c2.76 0 5-2.24 5-5 0-4.42-4.03-8-9-8z" fill="#667eea" opacity="0.25"/>
//             <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10c.83 0 1.5-.67 1.5-1.5 0-.39-.15-.74-.39-1.01-.23-.26-.38-.61-.38-.99 0-.83.67-1.5 1.5-1.5H16c2.76 0 5-2.24 5-5 0-4.42-4.03-8-9-8z" stroke="#667eea" stroke-width="1.5" fill="none"/>
//             <circle cx="6.5"  cy="11.5" r="1.5" fill="#f47820"/>
//             <circle cx="9.5"  cy="7.5"  r="1.5" fill="#7b1fa2"/>
//             <circle cx="14.5" cy="7.5"  r="1.5" fill="#009688"/>
//             <circle cx="17.5" cy="11.5" r="1.5" fill="#3f51b5"/>
//           </svg>
//           CHOOSE THEME
//         </div>

//         <div class="ts-grid">
//           @for (t of svc.themes; track t.name) {
//             <button
//               class="ts-card"
//               [class.ts-card-active]="svc.active === t.name"
//               (click)="select(t.name)"
//               [title]="t.label">

//               <span class="ts-swatch" [style.background]="getGradient(t)">
//                 @if (svc.active === t.name) {
//                   <svg viewBox="0 0 20 20" fill="white" style="width:16px;height:16px;">
//                     <path fill-rule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clip-rule="evenodd"/>
//                   </svg>
//                 }
//               </span>

//               <!-- ✅ Inline style forces color — not affected by any theme CSS -->
//               <span style="font-size:12px; font-weight:600; color:#374151 !important; text-align:center; display:block; line-height:1.3;">
//                 {{ t.label }}
//               </span>

//               @if (svc.active === t.name) {
//                 <span class="ts-active-badge">✓</span>
//               }

//             </button>
//           }
//         </div>

//         <div class="ts-footer">
//           Click any colour to apply instantly
//         </div>
//       </div>

//     </div>
//   `,
//   styles: [`
//     /* ── Root ── */
//     .ts-root { position: relative; display: inline-block; }

//     /* ── Trigger ── */
//     .ts-btn {
//       display: flex;
//       align-items: center;
//       gap: 5px;
//       background: rgba(255,255,255,0.18);
//       border: 1.5px solid rgba(255,255,255,0.30);
//       border-radius: 10px;
//       padding: 6px 10px;
//       cursor: pointer;
//       color: var(--navbar-text, #fff);
//       transition: background 0.2s, transform 0.15s;
//       backdrop-filter: blur(6px);
//     }

//     .ts-btn:hover {
//       background: rgba(255,255,255,0.28);
//       transform: translateY(-1px);
//     }

//     .ts-palette-icon { width: 20px; height: 20px; flex-shrink: 0; }

//     .ts-active-dot {
//       width: 8px; height: 8px;
//       border-radius: 50%;
//       border: 1.5px solid rgba(255,255,255,0.7);
//       flex-shrink: 0;
//     }

//     .ts-chevron {
//       width: 14px; height: 14px;
//       opacity: 0.85;
//       transition: transform 0.2s;
//       flex-shrink: 0;
//     }
//     .ts-chevron.flipped { transform: rotate(180deg); }

//     /* ── Panel — FULLY ISOLATED: all colors hardcoded ── */
//     .ts-panel {
//       position: absolute;
//       top: calc(100% + 10px);
//       right: 0;
//       width: 320px;
//       /* ✅ Force all text to dark regardless of any theme */
//       background-color: #ffffff;
//       color: #374151;
//       border-radius: 16px;
//       box-shadow: 0 20px 60px rgba(0,0,0,0.18), 0 4px 16px rgba(0,0,0,0.10);
//       border: 1px solid rgba(0,0,0,0.06);
//       overflow: hidden;
//       z-index: 99999;
//       animation: tsSlide 0.18s ease;
//       /* ✅ Prevent any inherited color from parent */
//       all: initial;
//       /* Re-apply needed properties after all:initial */
//       display: block;
//       position: absolute;
//       top: calc(100% + 10px);
//       right: 0;
//       width: 320px;
//       background-color: #ffffff !important;
//       color: #374151 !important;
//       border-radius: 16px;
//       box-shadow: 0 20px 60px rgba(0,0,0,0.18), 0 4px 16px rgba(0,0,0,0.10);
//       border: 1px solid rgba(0,0,0,0.06);
//       overflow: hidden;
//       z-index: 99999;
//       font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
//       animation: tsSlide 0.18s ease;
//     }

//     @keyframes tsSlide {
//       from { opacity: 0; transform: translateY(-8px) scale(0.97); }
//       to   { opacity: 1; transform: translateY(0) scale(1); }
//     }

//     /* ── Title ── */
//     .ts-panel-title {
//       display: flex;
//       align-items: center;
//       gap: 8px;
//       padding: 14px 16px 10px;
//       font-size: 11px;
//       font-weight: 700;
//       text-transform: uppercase;
//       letter-spacing: 0.9px;
//       color: #6b7280 !important;
//       border-bottom: 1px solid #f3f4f6;
//       background: #ffffff;
//     }

//     /* ── Grid ── */
//     .ts-grid {
//       display: grid;
//       grid-template-columns: 1fr 1fr 1fr;
//       gap: 8px;
//       padding: 12px;
//       background: #ffffff;
//     }

//     /* ── Each card ── */
//     .ts-card {
//       display: flex;
//       flex-direction: column;
//       align-items: center;
//       gap: 7px;
//       padding: 14px 8px 10px;
//       background: #f9fafb !important;
//       border: 2px solid transparent;
//       border-radius: 12px;
//       cursor: pointer;
//       transition: all 0.2s ease;
//       position: relative;
//       /* ✅ Force text color */
//       color: #374151 !important;
//       font-family: inherit;
//     }

//     .ts-card:hover {
//       background: #f3f4f6 !important;
//       border-color: #e5e7eb;
//       transform: translateY(-2px);
//       box-shadow: 0 4px 12px rgba(0,0,0,0.08);
//     }

//     .ts-card-active {
//       border-color: #667eea !important;
//       background: #f0f2ff !important;
//       box-shadow: 0 4px 16px rgba(102,126,234,0.20) !important;
//     }

//     /* ── Colour swatch ── */
//     .ts-swatch {
//       width: 38px; height: 38px;
//       border-radius: 50%;
//       display: flex;
//       align-items: center;
//       justify-content: center;
//       border: 3px solid rgba(255,255,255,0.6);
//       box-shadow: 0 3px 10px rgba(0,0,0,0.18);
//       transition: transform 0.2s;
//       flex-shrink: 0;
//     }

//     .ts-card:hover .ts-swatch,
//     .ts-card-active .ts-swatch { transform: scale(1.1); }

//     /* ── Active badge ── */
//     .ts-active-badge {
//       position: absolute;
//       top: 7px; right: 7px;
//       width: 17px; height: 17px;
//       border-radius: 50%;
//       background: #667eea;
//       color: #fff !important;
//       font-size: 9px;
//       display: flex;
//       align-items: center;
//       justify-content: center;
//       font-weight: 700;
//     }

//     /* ── Footer ── */
//     .ts-footer {
//       padding: 8px 16px 12px;
//       font-size: 10.5px;
//       color: #9ca3af !important;
//       text-align: center;
//       border-top: 1px solid #f3f4f6;
//       background: #ffffff;
//     }
//   `]
// })
// export class ThemeSwitcherComponent {
//   svc = inject(ThemeService);
//   open = false;

//   get activeColor(): string {
//     return this.svc.themes.find(t => t.name === this.svc.active)?.color ?? '#3c4b64';
//   }

//   getGradient(t: Theme): string {
//     const g: Record<string, string> = {
//       default: 'linear-gradient(135deg, #3c4b64, #2c3e50)',
//       orange:  'linear-gradient(135deg, #f47820, #c95e10)',
//       indigo:  'linear-gradient(135deg, #3f51b5, #283593)',
//       teal:    'linear-gradient(135deg, #009688, #00695c)',
//       purple:  'linear-gradient(135deg, #7b1fa2, #4a148c)',
//       pink:    'linear-gradient(135deg, #f48fb1, #e91e8c)',
//       yellow:  'linear-gradient(135deg, #ffd54f, #f59f00)',
//       green:   'linear-gradient(135deg, #a8d5b5, #2e7d5e)',
//       grey:    'linear-gradient(135deg, #90a4ae, #546e7a)',
//     };
//     return g[t.name] ?? t.color;
//   }

//   select(name: any): void {
//     this.svc.setTheme(name);
//     this.open = false;
//   }

//   @HostListener('document:click', ['$event'])
//   onOutside(e: Event): void {
//     if (!this._el.nativeElement.contains(e.target)) this.open = false;
//   }

//   constructor(private _el: ElementRef) {}
// }


import { Component, inject, HostListener, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ThemeService, Theme } from '../../service/theme.service';

@Component({
  selector: 'app-theme-switcher',
  standalone: true,
  imports: [CommonModule],
  template: `
    <!-- ✅ ONLY renders for SUPER_ADMIN and ORGANIZATION_ADMIN -->
    <div class="ts-root" *ngIf="svc.canChangeTheme()">

      <!-- Trigger button -->
      <button class="ts-btn" (click)="open = !open" title="Change theme">
        <svg class="ts-palette-icon" viewBox="0 0 24 24" fill="none">
          <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10c.83 0 1.5-.67 1.5-1.5 0-.39-.15-.74-.39-1.01-.23-.26-.38-.61-.38-.99 0-.83.67-1.5 1.5-1.5H16c2.76 0 5-2.24 5-5 0-4.42-4.03-8-9-8z"
            fill="currentColor" opacity="0.15"/>
          <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10c.83 0 1.5-.67 1.5-1.5 0-.39-.15-.74-.39-1.01-.23-.26-.38-.61-.38-.99 0-.83.67-1.5 1.5-1.5H16c2.76 0 5-2.24 5-5 0-4.42-4.03-8-9-8z"
            stroke="currentColor" stroke-width="1.5" fill="none"/>
          <circle cx="6.5"  cy="11.5" r="1.5" fill="#f47820"/>
          <circle cx="9.5"  cy="7.5"  r="1.5" fill="#7b1fa2"/>
          <circle cx="14.5" cy="7.5"  r="1.5" fill="#009688"/>
          <circle cx="17.5" cy="11.5" r="1.5" fill="#3f51b5"/>
        </svg>
        <span class="ts-active-dot" [style.background]="activeColor"></span>
        <svg class="ts-chevron" [class.flipped]="open" viewBox="0 0 20 20" fill="currentColor">
          <path fill-rule="evenodd" d="M5.293 7.293a1 1 0 011.414 0L10 10.586l3.293-3.293a1 1 0 111.414 1.414l-4 4a1 1 0 01-1.414 0l-4-4a1 1 0 010-1.414z" clip-rule="evenodd"/>
        </svg>
      </button>

      <!-- Dropdown panel -->
      <div class="ts-panel" *ngIf="open">

        <div class="ts-panel-title">
          <svg viewBox="0 0 24 24" fill="none" style="width:16px;height:16px;flex-shrink:0;">
            <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10c.83 0 1.5-.67 1.5-1.5 0-.39-.15-.74-.39-1.01-.23-.26-.38-.61-.38-.99 0-.83.67-1.5 1.5-1.5H16c2.76 0 5-2.24 5-5 0-4.42-4.03-8-9-8z" fill="#667eea" opacity="0.25"/>
            <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10c.83 0 1.5-.67 1.5-1.5 0-.39-.15-.74-.39-1.01-.23-.26-.38-.61-.38-.99 0-.83.67-1.5 1.5-1.5H16c2.76 0 5-2.24 5-5 0-4.42-4.03-8-9-8z" stroke="#667eea" stroke-width="1.5" fill="none"/>
            <circle cx="6.5"  cy="11.5" r="1.5" fill="#f47820"/>
            <circle cx="9.5"  cy="7.5"  r="1.5" fill="#7b1fa2"/>
            <circle cx="14.5" cy="7.5"  r="1.5" fill="#009688"/>
            <circle cx="17.5" cy="11.5" r="1.5" fill="#3f51b5"/>
          </svg>
          CHOOSE THEME
        </div>

        <div class="ts-grid">
          @for (t of svc.themes; track t.name) {
            <button
              class="ts-card"
              [class.ts-card-active]="svc.active === t.name"
              (click)="select(t.name)"
              [title]="t.label">

              <span class="ts-swatch" [style.background]="getGradient(t)">
                @if (svc.active === t.name) {
                  <svg viewBox="0 0 20 20" fill="white" style="width:16px;height:16px;">
                    <path fill-rule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clip-rule="evenodd"/>
                  </svg>
                }
              </span>

              <span style="font-size:12px;font-weight:600;color:#374151 !important;text-align:center;display:block;line-height:1.3;">
                {{ t.label }}
              </span>

              @if (svc.active === t.name) {
                <span class="ts-active-badge">✓</span>
              }

            </button>
          }
        </div>

        <div class="ts-footer">
          Click any colour to apply instantly
        </div>
      </div>

    </div>
  `,
  styles: [`
    .ts-root { position: relative; display: inline-block; }

    .ts-btn {
      display: flex;
      align-items: center;
      gap: 5px;
      background: rgba(255,255,255,0.18);
      border: 1.5px solid rgba(255,255,255,0.30);
      border-radius: 10px;
      padding: 6px 10px;
      cursor: pointer;
      color: var(--navbar-text, #fff);
      transition: background 0.2s, transform 0.15s;
      backdrop-filter: blur(6px);
    }

    .ts-btn:hover {
      background: rgba(255,255,255,0.28);
      transform: translateY(-1px);
    }

    .ts-palette-icon { width: 20px; height: 20px; flex-shrink: 0; }

    .ts-active-dot {
      width: 8px; height: 8px;
      border-radius: 50%;
      border: 1.5px solid rgba(255,255,255,0.7);
      flex-shrink: 0;
    }

    .ts-chevron {
      width: 14px; height: 14px;
      opacity: 0.85;
      transition: transform 0.2s;
      flex-shrink: 0;
    }
    .ts-chevron.flipped { transform: rotate(180deg); }

    /* Panel — fully isolated */
    .ts-panel {
      all: initial;
      display: block;
      position: absolute;
      top: calc(100% + 10px);
      right: 0;
      width: 320px;
      background-color: #ffffff !important;
      color: #374151 !important;
      border-radius: 16px;
      box-shadow: 0 20px 60px rgba(0,0,0,0.18), 0 4px 16px rgba(0,0,0,0.10);
      border: 1px solid rgba(0,0,0,0.06);
      overflow: hidden;
      z-index: 99999;
      font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
      animation: tsSlide 0.18s ease;
    }

    @keyframes tsSlide {
      from { opacity: 0; transform: translateY(-8px) scale(0.97); }
      to   { opacity: 1; transform: translateY(0) scale(1); }
    }

    .ts-panel-title {
      display: flex;
      align-items: center;
      gap: 8px;
      padding: 14px 16px 10px;
      font-size: 11px;
      font-weight: 700;
      text-transform: uppercase;
      letter-spacing: 0.9px;
      color: #6b7280 !important;
      border-bottom: 1px solid #f3f4f6;
      background: #ffffff;
    }

    .ts-grid {
      display: grid;
      grid-template-columns: 1fr 1fr 1fr;
      gap: 8px;
      padding: 12px;
      background: #ffffff;
    }

    .ts-card {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 7px;
      padding: 14px 8px 10px;
      background: #f9fafb !important;
      border: 2px solid transparent;
      border-radius: 12px;
      cursor: pointer;
      transition: all 0.2s ease;
      position: relative;
      color: #374151 !important;
      font-family: inherit;
    }

    .ts-card:hover {
      background: #f3f4f6 !important;
      border-color: #e5e7eb;
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(0,0,0,0.08);
    }

    .ts-card-active {
      border-color: #667eea !important;
      background: #f0f2ff !important;
      box-shadow: 0 4px 16px rgba(102,126,234,0.20) !important;
    }

    .ts-swatch {
      width: 38px; height: 38px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      border: 3px solid rgba(255,255,255,0.6);
      box-shadow: 0 3px 10px rgba(0,0,0,0.18);
      transition: transform 0.2s;
      flex-shrink: 0;
    }

    .ts-card:hover .ts-swatch,
    .ts-card-active .ts-swatch { transform: scale(1.1); }

    .ts-active-badge {
      position: absolute;
      top: 7px; right: 7px;
      width: 17px; height: 17px;
      border-radius: 50%;
      background: #667eea;
      color: #fff !important;
      font-size: 9px;
      display: flex;
      align-items: center;
      justify-content: center;
      font-weight: 700;
    }

    .ts-footer {
      padding: 8px 16px 12px;
      font-size: 10.5px;
      color: #9ca3af !important;
      text-align: center;
      border-top: 1px solid #f3f4f6;
      background: #ffffff;
    }
  `]
})
export class ThemeSwitcherComponent {
  svc = inject(ThemeService);
  open = false;

  get activeColor(): string {
    return this.svc.themes.find(t => t.name === this.svc.active)?.color ?? '#3c4b64';
  }

  getGradient(t: Theme): string {
    const g: Record<string, string> = {
      default: 'linear-gradient(135deg, #3c4b64, #2c3e50)',
      orange:  'linear-gradient(135deg, #f47820, #c95e10)',
      indigo:  'linear-gradient(135deg, #3f51b5, #283593)',
      teal:    'linear-gradient(135deg, #009688, #00695c)',
      purple:  'linear-gradient(135deg, #7b1fa2, #4a148c)',
      pink:    'linear-gradient(135deg, #f48fb1, #e91e8c)',
      yellow:  'linear-gradient(135deg, #ffd54f, #f59f00)',
      green:   'linear-gradient(135deg, #a8d5b5, #2e7d5e)',
      grey:    'linear-gradient(135deg, #90a4ae, #546e7a)',
    };
    return g[t.name] ?? t.color;
  }

  select(name: any): void {
    this.svc.setTheme(name);
    this.open = false;
  }

  @HostListener('document:click', ['$event'])
  onOutside(e: Event): void {
    if (!this._el.nativeElement.contains(e.target)) this.open = false;
  }

  constructor(private _el: ElementRef) {}
}