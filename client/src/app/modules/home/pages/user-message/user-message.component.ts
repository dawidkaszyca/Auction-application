import { Component, OnInit } from '@angular/core';
import {WebsocketService} from '../../../../shared/services/web-socket.service';

@Component({
  selector: 'app-user-message',
  templateUrl: './user-message.component.html',
  styleUrls: ['./user-message.component.scss']
})
export class UserMessageComponent implements OnInit {

  constructor(private webSocket: WebsocketService) { }

  ngOnInit(): void {
    this.webSocket._connect();
  }

  send() {
    this.webSocket._sendMessage('testowa wiadomośc');
  }
}
