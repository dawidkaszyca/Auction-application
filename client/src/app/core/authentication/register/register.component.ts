import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {UserViewModel} from '../../security/model/user-view-model';
import {AuthService} from '../../security/auth.service';
import {NavigationService} from '../../navigation/navigation.service';


@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent implements OnInit {
  model: UserViewModel = {
    login: '',
    password: '',
    email: '',
    firstName: '',
    lastName: ''
  };
  isSignedUp = false;
  isSignUpFailed = false;

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


