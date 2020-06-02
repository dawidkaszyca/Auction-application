import {Component, OnDestroy, OnInit} from '@angular/core';
import {WebsocketService} from '../../../../shared/services/web-socket.service';
import {NavigationService} from '../../../../core/header/navigation/navigation.service';

@Component({
  selector: 'app-user-message',
  templateUrl: './user-message.component.html',
  styleUrls: ['./user-message.component.scss']
})
export class UserMessageComponent implements OnInit, OnDestroy {

  constructor(private webSocket: WebsocketService, private navigationService: NavigationService) { }


  ngOnInit(): void {
    this.webSocket._connect();
    this.navigationService.show = false;
  }

  ngOnDestroy(): void {
    this.navigationService.show = true;
  }

  send() {
    this.webSocket._sendMessage('testowa wiadomośc');
  }
}
