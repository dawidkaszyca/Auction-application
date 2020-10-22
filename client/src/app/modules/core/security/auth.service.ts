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

const httpOptions = {
  headers: new HttpHeaders({'Content-Type': 'application/json'})
};

interface JwtToken {
  id_token: string;
}

@Injectable({providedIn: 'root'})
export class AuthService {

  private CREATE_NEW_USER = `${SERVER_API_URL}/register`;
  private LOGIN = `${SERVER_API_URL}/authenticate`;
  private PROFILE = `${SERVER_API_URL}/profile`;
  private UPDATE = `${SERVER_API_URL}/update`;
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

  logout(): void {
    this.localStorage.clear('authenticationToken');
    this.sessionStorage.clear('authenticationToken');
    this.websocketService._disconnect();
    this.websocketService.notification.next(0);
    this.router.navigateByUrl('/');
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
    this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/';
    this.router.navigateByUrl(this.returnUrl);
  }

  getTokenFromStorage(): string {
    return this.localStorage.retrieve('authenticationToken') || this.sessionStorage.retrieve('authenticationToken');
  }

  checkIfTokenIsNotExpired(): boolean {
    const token = this.getTokenFromStorage();
    if (token != null) {
      const helper = new JwtHelperService();
      return !helper.isTokenExpired(token);
    }
    return false;
  }

  getLoginFromToken(): string {
    const token = this.getTokenFromStorage();
    if (token != null) {
      const helper = new JwtHelperService();
      return helper.decodeToken(token).sub;
    }
    return null;
  }

  isLogged(): boolean {
    if (this.checkIfTokenIsNotExpired()) {
      this.reConnectIfIsRequired();
      return true;
    }
    return false;
  }

  private reConnectIfIsRequired() {
    if (this.websocketService.isConnected === false && this.websocketService.isReconnecting === false) {
      this.websocketService._connect(this.getLoginFromToken(), this.getTokenFromStorage());
    }
  }

  getUserData(): Observable<User> {
    return this.http.get<User>(this.PROFILE);
  }

  updateUserData(user: User) {
    return this.http.post(this.UPDATE, user);
  }
}
