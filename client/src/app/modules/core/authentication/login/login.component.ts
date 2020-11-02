import {Component, OnDestroy, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {NavigationService} from '../../header/navigation/navigation.service';
import {AuthService} from '../../security/auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit, OnDestroy {
  model: LoginViewModel = {
    username: '',
    password: '',
    rememberMe: false
  };

  invalidSignIn = false;
  accountNotActivated = false;

  constructor(private authService: AuthService, private router: Router, private navigationService: NavigationService) {
    navigationService.show = false;
  }

  ngOnInit() {

  }

  ngOnDestroy(): void {
    this.navigationService.show = true;
  }

  login(): void {
    this.authService.login(this.model).subscribe(res => {

    }, err => {
      if (err.error === 'Niepoprawne dane uwierzytelniające') {
        this.invalidSignIn = true;
      } else {
        this.invalidSignIn = false;
        this.accountNotActivated = true;
      }
    });
  }

  navigateToRegister($event: MouseEvent) {
    if ($event.detail !== 0) {
      this.router.navigate(['/register']);
    }
  }
}

export interface LoginViewModel {
  username: string;
  password: string;
  rememberMe: boolean;
}

