import {Injectable} from '@angular/core';
import * as SockJS from 'sockjs-client';
import * as Stomp from 'stompjs';
import {AuthService} from '../../core/security/auth.service';

@Injectable({
  providedIn: 'root'
})
export class WebsocketService {


  userName: string;
  webSocketEndPoint = 'http://localhost:8082/portfolio';
  subscribeChannel: string
  privateChannel: string
  stompClient: any;

  constructor(private authService: AuthService) {
    this.userName = authService.getLoginFromToken();
    this.subscribeChannel = '/app/queue/user/' + this.userName;
    this.privateChannel = '/queue/user/' + this.userName;
  }

  _connect() {
    if(this.userName != null && this.userName !== '') {
      console.log('Initialize WebSocket Connection');
      const ws = new SockJS(this.webSocketEndPoint);
      this.stompClient = Stomp.over(ws);
      const _this = this;
      _this.stompClient.connect({}, function(frame) {
        _this.stompClient.subscribe(_this.privateChannel, function(sdkEvent) {
          _this.onMessageReceived(sdkEvent);
        });
        // _this.stompClient.reconnect_delay = 2000;
      }, this.errorCallBack);
    }
  }

  _disconnect() {
    if (this.stompClient !== null) {
      this.stompClient.disconnect();
    }
    console.log('Disconnected');
  }

  // on error, schedule a reconnection attempt
  errorCallBack(error) {
    console.log('errorCallBack -> ' + error);
    setTimeout(() => {
      this._connect();
    }, 5000);
  }

  /**
   * Send message to sever via web socket
   * @param {*} message
   */
  _sendMessage(message) {
    console.log('calling logout api via web socket');
    this.stompClient.send(this.subscribeChannel, {}, JSON.stringify(message));
  }

  onMessageReceived(message) {
    console.log('NewMessage Recieved from Server :: ' + message);
    alert(message);
  }

}
