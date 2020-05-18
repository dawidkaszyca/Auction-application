import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable} from 'rxjs';
import {LoginViewModel} from './model/login-view-model';
import {UserViewModel} from './model/user-view-model';
import {map} from 'rxjs/operators';
import {LocalStorageService, SessionStorageService} from 'ngx-webstorage';
import {SERVER_API_URL} from '../../app.constants';
import {Router} from '@angular/router';

const httpOptions = {
  headers: new HttpHeaders({'Content-Type': 'application/json'})
};

interface JwtToken {
  id_token: string;
}

@Injectable({providedIn: 'root'})
export class AuthService {

  private logged = false;

  private CREATE_NEW_USER = `${SERVER_API_URL}/register`;
  private LOGIN = `${SERVER_API_URL}/authenticate`;

  constructor(
    private http: HttpClient,
    private localStorage: LocalStorageService,
    private sessionStorage: SessionStorageService,
    private router: Router) {
  }

  login(credentials: LoginViewModel): Observable<void> {
    return this.http
      .post<JwtToken>(this.LOGIN, credentials, httpOptions)
      .pipe(map(response => this.authenticateSuccess(response, credentials.rememberMe)));
  }

  signUp(user: UserViewModel): Observable<string> {
    return this.http.post<string>(this.CREATE_NEW_USER, user, httpOptions);
  }

  logout(): void {
      this.localStorage.clear('authenticationToken');
      this.sessionStorage.clear('authenticationToken');
      this.logged = false;
    }

  private authenticateSuccess(response: JwtToken, rememberMe: boolean): void {
    const jwt = response.id_token;
    this.logged = true;
    if (rememberMe) {
      this.localStorage.store('authenticationToken', jwt);
    } else {
      this.sessionStorage.store('authenticationToken', jwt);
    }
    this.router.navigateByUrl('/');
  }

  getTokenFromStorage(): string {
    return this.localStorage.retrieve('authenticationToken') || this.sessionStorage.retrieve('authenticationToken');
  }

  checkIfTokenIsNotExpired(): boolean {
    const token = this.getTokenFromStorage();
    return (token) ? true : false;
  }

  isLogged(): boolean {
    return this.logged || this.getTokenFromStorage() != null;
  }
}
