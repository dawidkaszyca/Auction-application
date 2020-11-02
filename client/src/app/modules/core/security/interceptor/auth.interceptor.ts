import {Injectable} from '@angular/core';
import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {Observable} from 'rxjs';
import {AuthService} from '../auth.service';
import {TranslateService} from '@ngx-translate/core';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor(private authService: AuthService, private translate: TranslateService) {
  }

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const token = this.authService.getTokenFromStorage();
    const language = this.translate.getBrowserLang();
    if (token != null) {
      request = request.clone({
        setHeaders: {
          Authorization: 'Bearer ' + token,
          Language: language
        }
      });
    } else {
      request = request.clone({
        setHeaders: {
          Language: language
        }
      });
    }
    return next.handle(request);
  }
}
