import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {RegisterComponent} from './modules/core/authentication/register/register.component';
import {LoginComponent} from './modules/core/authentication/login/login.component';
import {ConfirmRegisterComponent} from './modules/core/authentication/confirm-register/confirm-register.component';
import {RemindPasswordComponent} from './modules/core/authentication/remind-password/remind-password.component';
import {ResetPasswordComponent} from './modules/core/authentication/reset-password/reset-password.component';


const routes: Routes = [
  {
    path: 'login',
    component: LoginComponent,
  },
  {
    path: 'register',
    component: RegisterComponent,
  },
  {
    path: 'account/activate',
    component: ConfirmRegisterComponent
  },
  {
    path: 'account/remind-password',
    component: RemindPasswordComponent
  },
  {
    path: 'account/reset-password',
    component: ResetPasswordComponent
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
