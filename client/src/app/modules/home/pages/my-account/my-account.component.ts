import {Component, OnDestroy, OnInit} from '@angular/core';
import {NavigationService} from '../../../../core/header/navigation/navigation.service';
import {User} from '../../../../shared/models/user';
import {AuthService} from '../../../../core/security/auth.service';

@Component({
  selector: 'app-my-account',
  templateUrl: './my-account.component.html',
  styleUrls: ['./my-account.component.scss']
})
export class MyAccountComponent implements OnInit, OnDestroy {

  user = new User();
  isEditAble = false;

  constructor(private navigationService: NavigationService, private authService: AuthService) {
  }

  ngOnInit(): void {
    this.navigationService.show = false;
    this.getUserData();
  }

  private getUserData() {
    this.authService.getUserData().subscribe(res => {
      this.user = res;
    });
  }

  ngOnDestroy(): void {
    this.navigationService.show = true;
  }

  changeUserData(): void {
    this.authService.updateUserData(this.user).subscribe(res => {
    });
    this.isEditAble = false;
  }
}
