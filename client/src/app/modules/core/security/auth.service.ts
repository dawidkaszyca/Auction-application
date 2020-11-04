import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable} from 'rxjs';
import {LoginViewModel} from './model/login-view-model';
import {UserViewModel} from './model/user-view-model';
import {map} from 'rxjs/operators';
import {LocalStorageService, SessionStorageService} from 'ngx-webstorage';
import {SERVER_API_URL} from '../../../app.constants';
import {ActivatedRoute, Router} from '@angular/router';
import {JwtHelperService} from '@auth0/angular-jwt';
import {User} from '../../shared/models/user';
import {WebsocketService} from '../../shared/services/web-socket.service';
import {ResetPassword} from '../../shared/models/reset-password';

const httpOptions = {
  headers: new HttpHeaders({'Content-Type': 'application/json'})
};

interface JwtToken {
  id_token: string;
}

@Injectable({providedIn: 'root'})
export class AuthService {

  private CREATE_NEW_USER = `${SERVER_API_URL}/account/register`;
  private LOGIN = `${SERVER_API_URL}/authenticate`;
  private PROFILE = `${SERVER_API_URL}/account/profile`;
  private REMIND_PASSWORD = `${SERVER_API_URL}/account/password`;
  private CHECK_RESET_KEY = `${SERVER_API_URL}/account/check-reset-key/`;
  private RESET_PASSWORD = `${SERVER_API_URL}/account/password`;
  private CHANGE_PASSWORD = `${SERVER_API_URL}/account/password`;
  private returnUrl: string;

  constructor(
    private http: HttpClient,
    private localStorage: LocalStorageService,
    private sessionStorage: SessionStorageService,
    private route: ActivatedRoute,
    private router: Router,
    private websocketService: WebsocketService) {
  }

  login(credentials: LoginViewModel): Observable<void> {
    return this.http
      .post<JwtToken>(this.LOGIN, credentials, httpOptions)
      .pipe(map(response => this.authenticateSuccess(response, credentials.rememberMe)));
  }

  signUp(user: UserViewModel): Observable<string> {
    return this.http.post<string>(this.CREATE_NEW_USER, user, httpOptions);
  }

  getUserData(): Observable<User> {
    return this.http.get<User>(this.PROFILE);
  }

  updateUserData(user: User) {
    return this.http.put(this.PROFILE, user);
  }

  remindPassword(email: string) {
    return this.http.delete(this.REMIND_PASSWORD + '/' + email);
  }

  activateAccount() {
    const url = SERVER_API_URL + window.location.pathname + window.location.search;
    return this.http.get(url);
  }

  checkResetKey(key: string) {
    const url = this.CHECK_RESET_KEY + key;
    return this.http.get(url);
  }

  resetPassword(resetPassword: ResetPassword) {
    return this.http.post(this.RESET_PASSWORD, resetPassword);
  }

  private authenticateSuccess(response: JwtToken, rememberMe: boolean): void {
    const jwt = response.id_token;
    this.localStorage.clear('authenticationToken');
    this.sessionStorage.clear('authenticationToken');
    if (rememberMe) {
      this.localStorage.store('authenticationToken', jwt);
    } else {
      this.sessionStorage.store('authenticationToken', jwt);
    }
    this.websocketService._connect(this.getLoginFromToken(), this.getTokenFromStorage());
    this.returnUrl = this.route.snapshot.queryParams.returnUrl || '/';
    this.router.navigateByUrl(this.returnUrl);
  }

  logout(): void {
    this.localStorage.clear('authenticationToken');
    this.sessionStorage.clear('authenticationToken');
    this.websocketService._disconnect();
    this.websocketService.notification.next(0);
    this.router.navigateByUrl('/');
  }

  isLogged(): boolean {
    if (this.isAuthenticated()) {
      this.reConnectIfIsRequired();
      return true;
    }
    return false;
  }

  isAuthenticated(): boolean {
    const token = this.getTokenFromStorage();
    if (token != null) {
      const helper = new JwtHelperService();
      return !helper.isTokenExpired(token);
    }
    return false;
  }


  getTokenFromStorage(): string {
    return this.localStorage.retrieve('authenticationToken') || this.sessionStorage.retrieve('authenticationToken');
  }

  private reConnectIfIsRequired() {
    if (this.websocketService.isConnected === false && this.websocketService.isReconnecting === false) {
      this.websocketService._connect(this.getLoginFromToken(), this.getTokenFromStorage());
    }
  }

  getLoginFromToken(): string {
    const token = this.getTokenFromStorage();
    if (token != null) {
      const helper = new JwtHelperService();
      return helper.decodeToken(token).sub;
    }
    return null;
  }
}
