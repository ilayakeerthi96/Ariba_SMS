import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment.prod';
import { HttpClient } from '@angular/common/http';
import { of } from 'rxjs';
import { tap } from 'rxjs/operators';
@Injectable({
  providedIn: 'root'
})
export class ServicesService {

  constructor( private http: HttpClient) { }
 
  opts = [];
  getReportingId(){
    var url = environment.API_URL + "project/getActivePracticeType";
    return this.opts.length ?
    of(this.opts) :
    this.http.get<any>(url).pipe(tap(data => this.opts = data))
  }
}

