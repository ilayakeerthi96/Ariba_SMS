import { Component, Inject, NgZone, OnInit } from '@angular/core';
import {MatDialogRef,MAT_DIALOG_DATA} from '@angular/material/dialog';
import { application } from '../../shared/application_properties/errormsg_text';
import { MatDialogModule } from '@angular/material/dialog'; 
import { AuthService } from '../../shared/service/AuthService';
@Component({
  selector: 'app-logout-dialogbox',
  imports:[MatDialogModule],
  templateUrl: './logout-dialogbox.component.html',
  styleUrls: ['./logout-dialogbox.component.css']
})
export class LogoutDialogboxComponent implements OnInit {

  constructor(public dialogRef: MatDialogRef<LogoutDialogboxComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any, private authService:AuthService
  ) { }
  areyouSue:any
  doYouWant:any
  ngOnInit(): void {
    this.areyouSue = application.logoutAreSureMsg;
    this.doYouWant = application.logoutDoYouRealyMsg;
  }
  

  logout(){
    this.authService.logout();
    this.dialogRef.close();
  }

  onNoClick(): void {
      this.dialogRef.close();
  }

}
