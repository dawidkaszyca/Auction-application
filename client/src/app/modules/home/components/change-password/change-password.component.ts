import { Component, OnInit } from '@angular/core';
import {ChangePassword} from '../../../shared/models/change-password';

@Component({
  selector: 'app-change-password',
  templateUrl: './change-password.component.html',
  styleUrls: ['./change-password.component.scss']
})
export class ChangePasswordComponent implements OnInit {
  model: ChangePassword;
  repeatPassword: string;

  constructor() { }

  ngOnInit(): void {
  }

  changePassword() {
    return false;
  }
}
