import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { TEAMS } from './teams.model';
// 👇 1. Add this Import
import { forkJoin } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class LeadService {

  constructor(private http: HttpClient) { }

  //   getTestApi(): any {
  //   var url = environment.API_URL + "leadcapture/api/leads/test";
  //   return this.invokeGetAPI(url);
  // }

  getTestApi(): any {
  var url = environment.API_URL + "leadcapture/api/leads/test";
  var reqHeader = new HttpHeaders({
    'Authorization': 'Bearer ' + localStorage.getItem("token") || '{}'
  });
  return this.http.get(url, { headers: reqHeader, responseType: 'text' });
}


  saveTargets(payload: any): any {
    var url = environment.API_URL + "leadcapture/api/targets";
    return this.invokePostAPI(url, payload);
  }

  // ⭐ FIXED DELETE FUNCTION ⭐
  // Instead of sending one big POST (which failed), we send multiple DELETE requests.
  deleteTargets(ids: any[]): any {
    var reqHeader = new HttpHeaders({
      'Authorization': 'Bearer ' + localStorage.getItem("token") || '{}'
    });

    // Create a list of delete requests for every ID
    const deleteCalls = ids.map(id => 
      this.http.delete(environment.API_URL + "leadcapture/api/targets/" + id, { headers: reqHeader })
    );

    // Execute them all in parallel
    return forkJoin(deleteCalls);
  }
  // ---------------------------

  getTargets(): any {
    var url = environment.API_URL + "leadcapture/api/targets";
    return this.invokeGetAPI(url);
  }

  invokeGetAPI(url: any) {
    var reqHeader = new HttpHeaders({
      'Authorization': 'Bearer ' + localStorage.getItem("token") || '{}'
    });
    return this.http.get(url, { headers: reqHeader });
  }

  invokePostAPI(url: any, input: any) {
    var reqHeader = new HttpHeaders({
      'Authorization': 'Bearer ' + localStorage.getItem("token") || '{}'
    });
    return this.http.post<{ result: string; input: any, data: any }>(url, input, { 'headers': reqHeader });
  }

  invokePutAPI(url: any, input: any) {
    var reqHeader = new HttpHeaders({
      'Authorization': 'Bearer ' + localStorage.getItem("token") || '{}'
    });
    return this.http.put<{ result: string; input: any, data: any }>(url, input, { 'headers': reqHeader });
  }

  getLeads(): any {
    var url = environment.API_URL + "leadcapture/api/leads";
    return this.invokeGetAPI(url);
  }

  saveLead(input: any): any {
    var url = environment.API_URL + "leadcapture/api/leads";
    return this.invokePostAPI(url, input);
  }

  updateLead(id: any, input: any): any {
    var url = environment.API_URL + "leadcapture/api/leads/" + id;
    return this.invokePutAPI(url, input);
  }

  isMgmtUserPractice() {
    var getLoginData = JSON.parse(localStorage.getItem("signinData") || '{}');
    if (getLoginData.practiceName == "MGMT" || getLoginData.practiceName == "Management") {
      return true;
    }
    return false;
  }
  
  isSalesTeam() {
    var getLoginData = JSON.parse(localStorage.getItem("signinData") || '{}');
    if (getLoginData.practiceName == "Business Development" || getLoginData.practiceName == "Professional Services") {
      return true;
    }
    return false;
  }

  isProfessionalServiceTeam() {
    var getLoginData = JSON.parse(localStorage.getItem("signinData") || '{}');
    if (getLoginData.practiceName == "Professional Services") {
      return true;
    }
    return false;
  }

  isSalesManager() {
    const loginData = JSON.parse(localStorage.getItem("signinData") || '{}');
    const accessArray = loginData.userRoleAccess;

    if (!Array.isArray(accessArray)) return false;

    //const managerAliases = ["sales_manager", "sales manager", "sales-manager", "sm"];
   // const managerAliases = ["role_supplier", "role supplier", "role-supplier", "rs"," supplier",];
    const managerAliases = ["role_buyer", "role buyer", "role-buyer", "rb"," buyer",];
    
    for (const entry of accessArray) {
      let roles = entry.userRoles;
      if (!roles) continue;
      if (typeof roles === 'string') roles = roles.split(',').map(r => r.trim().toLowerCase());

      if (Array.isArray(roles)) {
        const normalized = roles.map(r => r.toLowerCase());
        if (normalized.some(r => managerAliases.includes(r))) return true;
      }
    }
    return false;
  }

  // getRevenueTrendData(user: any, year: number) {
  //   let body: any = { role: user.role, year: year };

  //   if (user.role === 'sales') {
  //     body.employeeCode = user.code;
  //   }

  //   if (user.role === 'manager') {
  //     const team = TEAMS.find(t => t.manager === user.code);
  //     body.employeeCode = user.code;
  //     body.teamMembers = team ? team.members.map(m => m.employeeCode) : [];
  //   }
  //   var url = environment.API_URL + "leadcapture/api/graphs/trend";
  //   return this.invokePostAPI(url, body);
  // }
  getRevenueTrendData(user: any, year: number, selectedEmployeeOverride?: string) {
    let body: any = { role: user.role, year: year };

    // 1. If a specific employee is selected from dropdown (and it's not 'all')
    if (selectedEmployeeOverride && selectedEmployeeOverride !== 'all') {
      body.role = 'sales'; // Force API to treat it as single sales person view
      body.employeeCode = selectedEmployeeOverride;
    }
    // 2. Standard Sales Role
    else if (user.role === 'sales') {
      body.employeeCode = user.code;
    }
    // 3. Manager/Management viewing "All Team"
    else if (user.role === 'manager' || user.role === 'management') {
      const team = TEAMS.find(t => t.manager === user.code);
      body.employeeCode = user.code;
      
      // If management, we might not want to send specific team members to get global view, 
      // but sticking to your existing logic:
      if(user.role === 'manager') {
         body.teamMembers = team ? team.members.map(m => m.employeeCode) : [];
      }
    }

    var url = environment.API_URL + "leadcapture/api/graphs/trend";
    return this.invokePostAPI(url, body);
  }
}