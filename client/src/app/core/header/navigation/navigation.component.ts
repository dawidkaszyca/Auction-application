import {Component, Injector, OnInit} from '@angular/core';
import {NavigationService} from './navigation.service';
import {AuthService} from '../../security/auth.service';
import {WebsocketService} from '../../../shared/services/web-socket.service';
import { faEnvelope } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-navigation',
  templateUrl: './navigation.component.html',
  styleUrls: ['./navigation.component.scss']
})
export class NavigationComponent implements OnInit {
  faEnvelope = faEnvelope;

  notification: number;
  constructor(public authService: AuthService, public navigationService: NavigationService, private webSocketService: WebsocketService) {}

  ngOnInit(): void {
    this.webSocketService.notification.subscribe(it => {
      this.notification = it;
    });
  }

}
