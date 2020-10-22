import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {AuthService} from '../security/auth.service';

@Injectable({
  providedIn: 'root'
})
export class ApiService {

  private DOMAIN_URL = 'http://localhost:8082';
  private BASE_URL = this.DOMAIN_URL + '/api';

  constructor(private http: HttpClient, private  authService: AuthService) {
  }


  isAuthenticated(): boolean {
    return this.authService.checkIfTokenIsNotExpired();
  }

  getActivateStatus() {
    const url = this.DOMAIN_URL + window.location.pathname;
    return this.http.get(url);
  }
}
