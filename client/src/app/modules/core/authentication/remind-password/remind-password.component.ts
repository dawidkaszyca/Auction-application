import {Component, OnDestroy, OnInit} from '@angular/core';
import {AuthService} from '../../security/auth.service';
import {Router} from '@angular/router';
import {NavigationService} from '../../header/navigation/navigation.service';

@Component({
  selector: 'app-remind-password',
  templateUrl: './remind-password.component.html',
  styleUrls: ['./remind-password.component.scss']
})
export class RemindPasswordComponent implements OnInit, OnDestroy {

  email: string;

  constructor(private authService: AuthService, private router: Router, private navigationService: NavigationService) {
    navigationService.show = false;
  }

  ngOnInit(): void {
    this.email = '';
  }

  ngOnDestroy(): void {
    this.navigationService.show = true;
  }

  remindPassword() {
    this.authService.remindPassword(this.email).subscribe(res => {

    }, err => {

    });
  }
}
