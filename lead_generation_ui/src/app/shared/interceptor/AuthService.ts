import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private expiryKey = 'sessionExpiry';

  login(expiryMinutes: number) {
    const expiryTime = new Date().getTime() + expiryMinutes * 60 * 1000;
    localStorage.setItem(this.expiryKey, expiryTime.toString());
  }

  logout() {
    localStorage.removeItem(this.expiryKey);
  }

  isSessionActive(): boolean {
    const expiry = localStorage.getItem(this.expiryKey);
    if (!expiry) return false;

    return new Date().getTime() < Number(expiry);
  }
}