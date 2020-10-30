import {Component, OnDestroy, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {UserViewModel} from '../../security/model/user-view-model';
import {AuthService} from '../../security/auth.service';
import {NavigationService} from '../../header/navigation/navigation.service';


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
  };

  constructor(private authService: AuthService, private router: Router, private navigationService: NavigationService) {
    navigationService.show = false;
  }

  ngOnInit() {
  }

  ngOnDestroy(): void {
    this.navigationService.show = true;
  }

  signIn(): void {
    this.authService.signUp(this.model).subscribe();
  }
}


