import {Injectable} from '@angular/core';
import * as SockJS from 'sockjs-client';
import * as Stomp from 'stompjs';
import {Status} from '../models/status';
import {BehaviorSubject} from 'rxjs';
import {Router} from '@angular/router';
import {WEB_SOCKET_URL} from '../../../app.constants';

@Injectable({
  providedIn: 'root'
})
export class WebsocketService {

  userName: string;
  webSocketEndPoint = `${WEB_SOCKET_URL}/portfolio`;
  subscribeChannel: string;
  privateChannel: string;
  stompClient: any;
  isConnected = false;
  notification: BehaviorSubject<number>;
  isReconnecting = false;
  token: string;

  newMessage: BehaviorSubject<Status>;

  constructor(private router: Router) {
    this.newMessage = new BehaviorSubject(new Status());
    this.notification = new BehaviorSubject<number>(0);
    this.isConnected = false;
  }

  _connect(login: string, token: string) {
    this.token = token;
    this.userName = login;
    this.subscribeChannel = '/queue/users/' + login;
    this.privateChannel = '/app/queue/users/' + login;
    if (this.userName != null && this.userName !== '') {
      console.log('Initialize WebSocket Connection');
      const ws = new SockJS(this.webSocketEndPoint);
      this.stompClient = Stomp.over(ws);
      const _this = this;
      _this.isConnected = true;
      _this.isReconnecting = false;
      _this.stompClient.connect({'X-Authorization': 'Bearer ' + token}, function(frame) {
        _this._sendMessage('getStatus');
        _this.stompClient.subscribe(_this.subscribeChannel, function(sdkEvent) {
          _this.onMessageReceived(sdkEvent);

        });
      }, this.errorCallBack.bind(_this));
    }
  }

  _disconnect() {
    this.userName = null;
    if (this.stompClient !== null) {
      this.stompClient.disconnect();
      this.isConnected = false;
    }
  }

  errorCallBack(error) {
    this.isReconnecting = true;
    this.isConnected = false;
    setTimeout(() => {
      this._connect(this.userName, this.token);
    }, 3000);
  }

  _sendMessage(message) {
    console.log('calling logout api via web socket');
    this.stompClient.send(this.privateChannel, {'X-Authorization': 'Bearer ' + this.token}, JSON.stringify(message));
  }

  onMessageReceived(message) {
    const object = JSON.parse(message.body);
    if (object.status || object.status === 0) {
      this.notification.next(object.status);
    } else {
      const obj: Status = JSON.parse(message.body);
      this.newMessage.next(obj);
      if (this.router.url !== 'messages') {
        this._sendMessage('getStatus');
      }
    }
  }
}
