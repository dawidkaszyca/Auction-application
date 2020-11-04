import {Component, OnDestroy, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {UserViewModel} from '../../security/model/user-view-model';
import {AuthService} from '../../security/auth.service';
import {NavigationService} from '../../header/navigation/navigation.service';
import {DialogService} from '../../../shared/services/dialog.service';
import {DialogKey} from '../../../shared/config/enums';


@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent implements OnInit, OnDestroy {
  model: UserViewModel = {
    login: '',
    password: '',
    email: '',
    firstName: '',
    lastName: ''
  }

  constructor(private authService: AuthService, private router: Router, private navigationService: NavigationService,
              private dialogService: DialogService) {
    navigationService.show = false;
  }

  isRequestPending = false;

  ngOnInit() {
  }

  ngOnDestroy(): void {
    this.navigationService.show = true;
  }

  signIn(): void {
    this.isRequestPending = true;
    this.authService.signUp(this.model).subscribe(res => {
      this.isRequestPending = false;
      this.dialogService.openInfoDialog(DialogKey.AFTER_REGISTER, true, '/login');
    }, err => {
      this.isRequestPending = false;
      this.dialogService.openWarningDialog(DialogKey.AFTER_REGISTER_ERROR, false, null);
    });
  }
}


