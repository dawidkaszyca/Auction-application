import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatSelectModule} from '@angular/material/select';
import {FormsModule} from '@angular/forms';
import {MatIconModule} from '@angular/material/icon';
import {MatMenuModule} from '@angular/material/menu';
import {CommonModule} from '@angular/common';
import {SharedModule} from '../shared/shared.module';
import {MatSnackBar} from '@angular/material/snack-bar';
import {AuthExpiredInterceptor} from './security/interceptor/auth-expired.interceptor';
import {AuthInterceptor} from './security/interceptor/auth.interceptor';
import {FooterComponent} from './footer/footer.component';
import {TranslateModule} from '@ngx-translate/core';
import {HomeRoutingModule} from '../home/home-routing.module';
import { ResetPasswordComponent } from './authentication/reset-password/reset-password.component';
import { RemindPasswordComponent } from './authentication/remind-password/remind-password.component';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';

@NgModule({
  declarations: [
    FooterComponent,
    ResetPasswordComponent,
    RemindPasswordComponent
  ],
  imports: [
    BrowserModule,
    CommonModule,
    HttpClientModule,
    BrowserAnimationsModule,
    MatFormFieldModule,
    MatSelectModule,
    TranslateModule,
    FormsModule,
    HomeRoutingModule,
    MatIconModule,
    MatMenuModule,
    SharedModule,
    MatProgressSpinnerModule
  ],
  providers: [
    MatSnackBar,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthExpiredInterceptor,
      multi: true
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    },
  ]
})
export class CoreModule {
}
