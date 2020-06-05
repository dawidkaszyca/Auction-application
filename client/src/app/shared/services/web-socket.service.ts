import {Injectable} from '@angular/core';
import * as SockJS from 'sockjs-client';
import * as Stomp from 'stompjs';
import {Status} from '../models/status';
import {BehaviorSubject} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class WebsocketService {

  userName: string;
  webSocketEndPoint = 'http://localhost:8082/portfolio';
  subscribeChannel: string;
  privateChannel: string;
  stompClient: any;
  isConnected = false;

  newMessage: BehaviorSubject<Status>;

  constructor() {
    this.newMessage = new BehaviorSubject(new Status());
    this.isConnected = false;
  }

  _connect(login: string) {
    this.userName = login;
    this.subscribeChannel = '/app/queue/user/' + login;
    this.privateChannel = '/queue/user/' + login;
    if (this.userName != null && this.userName !== '') {
      console.log('Initialize WebSocket Connection');
      const ws = new SockJS(this.webSocketEndPoint);
      this.stompClient = Stomp.over(ws);
      const _this = this;
      _this.isConnected = true;
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
      this.isConnected = false;
      this.stompClient.disconnect();
    }
    alert('Disconected');
    console.log('Disconnected');
  }

  // on error, schedule a reconnection attempt
  errorCallBack(error) {
    this.isConnected = false;
    console.log('errorCallBack -> ' + error);
    setTimeout(() => {
      this._connect(this.userName);
      alert("Reconnect");
    }, 1000);
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
    const obj: Status = JSON.parse(message.body);
    this.newMessage.next(obj);
  }

}
