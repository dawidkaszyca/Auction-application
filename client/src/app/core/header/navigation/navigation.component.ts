import {Component, Injector, OnInit} from '@angular/core';
import {NavigationService} from './navigation.service';
import {AuthService} from '../../security/auth.service';

@Component({
  selector: 'app-navigation',
  templateUrl: './navigation.component.html',
  styleUrls: ['./navigation.component.scss']
})
export class NavigationComponent implements OnInit {

  constructor(public authService: AuthService, public navigationService: NavigationService) {}

  ngOnInit(): void {
  }

}
