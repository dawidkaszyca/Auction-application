import { Component, OnInit } from '@angular/core';
import {Router} from '@angular/router';
import {ApiService} from '../api.service';

@Component({
  selector: 'app-confirm-register',
  templateUrl: './confirm-register.component.html',
  styleUrls: ['./confirm-register.component.scss']
})
export class ConfirmRegisterComponent implements OnInit {
  correctActive: boolean;

  constructor(private apiService: ApiService, private router: Router) {
  }

  ngOnInit() {
    this.getActivateStatus();
  }

  public getActivateStatus() {
    this.apiService.getActivateStatus().subscribe(
      res => {
        this.correctActive = true;
      },
      err => {
        this.router.navigateByUrl('/**');
      }
    )
    ;
  }
}
