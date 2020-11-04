import {Component, OnInit} from '@angular/core';
import {AuthService} from '../../security/auth.service';

@Component({
  selector: 'app-confirm-register',
  templateUrl: './confirm-register.component.html',
  styleUrls: ['./confirm-register.component.scss']
})
export class ConfirmRegisterComponent implements OnInit {
  correctActive = false;

  constructor(private authService: AuthService) {
  }

  ngOnInit() {
    this.getActivateStatus();
  }

  public getActivateStatus() {
    this.authService.activateAccount().subscribe(
      res => {
        this.correctActive = true;
      });
  }
}
