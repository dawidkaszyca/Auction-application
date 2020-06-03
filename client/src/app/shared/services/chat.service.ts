import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Conversation} from '../models/conversation';
import {NewMessage} from '../models/new-message';
import {Message} from '../models/message';


@Injectable({
  providedIn: 'root'
})
export class ChatService {

  private DOMAIN_URL = 'http://localhost:8082';
  private BASE_URL = this.DOMAIN_URL + '/api';
  private MESSAGES = this.BASE_URL + '/messages';

  constructor(private http: HttpClient) {
  }

  getMessages(): Observable<Conversation[]> {
    return this.http.get<Conversation[]>(this.MESSAGES);
  }

  sendMessage(newMsg: NewMessage): Observable<Message> {
    return this.http.post<Message>(this.MESSAGES, newMsg);
  }
}
