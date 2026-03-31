 
  import { Component } from '@angular/core';
  import { Router, RouterOutlet } from '@angular/router';
  import { CommonModule, DatePipe, DOCUMENT } from '@angular/common';
  import { LoginComponent } from './components/login/login.component';
  import { SidebarComponent } from './components/sidebar/sidebar.component';
  import { AuthService } from './service/AuthService';
  import { message } from '../application_properties/errormsg_text';
   import { MatDialog } from '@angular/material/dialog';
import { MessageService } from './service/message.service';
  @Component({
      selector: 'app-root',
      imports: [RouterOutlet, LoginComponent, CommonModule, SidebarComponent],
      templateUrl: './app.component.html',
      styleUrl: './app.component.css'
  })
  export class AppComponent {
    title = 'customer_payment_track';
  
    login:boolean = false;
    loginStatus:boolean = false;
    showHead: boolean = false;
    userType:any;
    private sessionCheckInterval: any;

    constructor(private authService: AuthService,
      private dialog:MatDialog,
      private messageService: MessageService,
    private router:Router) {}
  
  
    ngOnInit(): void {
      this.authService.loginStatus$.subscribe(status => {
        this.loginStatus = status;
      });
     
      const status = localStorage.getItem("loginStatus");
      if (status !== null) {
        this.loginStatus = (status === "true");
      } else {
        this.loginStatus = false;
        localStorage.setItem("loginStatus", "false");
        //this.router.navigate(['/login']);
      } 
      // this.sessionCheckInterval = setInterval(() => {
      //   if (this.authService.isSessionExpired()) {
      //     clearInterval(this.sessionCheckInterval);
      //     localStorage.clear();
      //     this.loginStatus = false;
      //     this.messageService.showMessage('warning',message.warning,message.sessionTimeout,'/login');          
      //   }
      // }, 30000); // Check every 30 seconds
    }
 
  }