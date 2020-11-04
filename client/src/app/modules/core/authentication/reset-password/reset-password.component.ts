import {Component, OnDestroy, OnInit} from '@angular/core';
import {AuthService} from '../../security/auth.service';
import {Router} from '@angular/router';
import {NavigationService} from '../../header/navigation/navigation.service';
import {ResetPassword} from '../../../shared/models/reset-password';
import {DialogService} from '../../../shared/services/dialog.service';
import {DialogKey} from '../../../shared/config/enums';

@Component({
  selector: 'app-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.scss']
})
export class ResetPasswordComponent implements OnInit, OnDestroy {

  resetPassword: ResetPassword;
  repeatedPassword: string;
  isKeyCorrect = false;
  isRequestPending = false;

  constructor(private authService: AuthService, private router: Router, private navigationService: NavigationService,
              private dialogService: DialogService) {
    navigationService.show = false;
    this.resetPassword = new ResetPassword();
  }

  ngOnInit(): void {
    this.resetPassword.resetKey = window.location.search.replace('?key=', '');
    this.checkPasswordKey();
  }

  ngOnDestroy(): void {
    this.navigationService.show = true;
  }

  changePassword() {
    this.isRequestPending = true;
    this.authService.resetPassword(this.resetPassword).subscribe(res => {
      this.isRequestPending = false;
      this.dialogService.openInfoDialog(DialogKey.AFTER_PASSWORD_CHANGED, true, '/login');
    }, err => {
      this.isRequestPending = false;
      this.dialogService.openInfoDialog(DialogKey.AFTER_PASSWORD_CHANGED_ERROR, false, null);
    });
    return false;
  }

  private checkPasswordKey() {
    this.authService.checkResetKey(this.resetPassword.resetKey).subscribe(res => {
      this.isKeyCorrect = true;
    }, err => {
      this.isKeyCorrect = false;
    });
  }
}
