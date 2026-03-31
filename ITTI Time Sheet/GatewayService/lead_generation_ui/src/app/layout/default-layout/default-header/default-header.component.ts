import { NgTemplateOutlet } from '@angular/common';
import { Component, computed, inject, input } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialog, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { RouterLink, RouterLinkActive } from '@angular/router';

import {
  AvatarComponent,
  BreadcrumbComponent,
  BreadcrumbRouterComponent,
  ColorModeService,
  ContainerComponent,
  DropdownComponent,
  DropdownDividerDirective,
  DropdownHeaderDirective,
  DropdownItemDirective,
  DropdownMenuDirective,
  DropdownToggleDirective,
  HeaderComponent,
  HeaderNavComponent,
  HeaderTogglerDirective,
  NavItemComponent,
  NavLinkDirective,
  SidebarToggleDirective
} from '@coreui/angular';

import { IconDirective } from '@coreui/icons-angular';
import { LogoutDialogboxComponent } from '../../../shared/logout-dialogbox/logout-dialogbox.component';
import { SessionTimerComponent } from '../../../views/session-timer/session-timer-component';

@Component({
    selector: 'app-default-header',
    templateUrl: './default-header.component.html',
  imports: [ContainerComponent, HeaderTogglerDirective, SidebarToggleDirective, IconDirective, HeaderNavComponent, RouterLink, NgTemplateOutlet,  DropdownComponent, DropdownToggleDirective, AvatarComponent, DropdownMenuDirective, DropdownItemDirective,MatDialogModule,SessionTimerComponent,BreadcrumbRouterComponent]
})
export class DefaultHeaderComponent extends HeaderComponent {

  readonly #colorModeService = inject(ColorModeService);
  readonly colorMode = this.#colorModeService.colorMode;

  userName:any ;
  designation:any;

  ngOnInit(): void {
    var signInData = localStorage.getItem("signinData")??"";
    var signInDataObj = JSON.parse(signInData);
    this.userName = signInDataObj.firstName;
    this.designation = signInDataObj.designation;
  }

  readonly colorModes = [
    { name: 'light', text: 'Light', icon: 'cilSun' },
    { name: 'dark', text: 'Dark', icon: 'cilMoon' },
    { name: 'auto', text: 'Auto', icon: 'cilContrast' }
  ];

  readonly icons = computed(() => {
    const currentMode = this.colorMode();
    return this.colorModes.find(mode => mode.name === currentMode)?.icon ?? 'cilSun';
  });

  constructor(private dialog:MatDialog) {
    super();
  }

  sidebarId = input('sidebar1');

  public newMessages = [
   
  ];

  public newNotifications = [
  
  ];

  public newStatus = [
    
  ];

  public newTasks = [
    
  ];

  logout() { 
    console.log("inside logout ===============");
    this.dialog.open(LogoutDialogboxComponent, { 
      width: '250px'
    });     
  } 

}
