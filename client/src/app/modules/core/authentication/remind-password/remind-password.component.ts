import {Component, OnDestroy, OnInit} from '@angular/core';
import {AuthService} from '../../security/auth.service';
import {Router} from '@angular/router';
import {NavigationService} from '../../header/navigation/navigation.service';
import {DialogService} from '../../../shared/services/dialog.service';

@Component({
  selector: 'app-remind-password',
  templateUrl: './remind-password.component.html',
  styleUrls: ['./remind-password.component.scss']
})
export class RemindPasswordComponent implements OnInit, OnDestroy {

  email: string;
  isRequestPending = false;

  constructor(private authService: AuthService, private router: Router, private navigationService: NavigationService,
              private dialogService: DialogService) {
    navigationService.show = false;
  }

  ngOnInit(): void {
    this.email = '';
  }

  ngOnDestroy(): void {
    this.navigationService.show = true;
  }

  remindPassword() {
    this.isRequestPending = true;
    this.authService.remindPassword(this.email).subscribe(res => {
      this.isRequestPending = false;
      this.dialogService.openInfoDialog('remind.password.success', false, null);
    }, err => {
      this.isRequestPending = false;
      if (err.status) {
        this.dialogService.openWarningDialog('remind.password.email-not-exist', false, null);
      } else {
        this.dialogService.openWarningDialog('remind.password.error', false, null);
      }
    });
  }
}
