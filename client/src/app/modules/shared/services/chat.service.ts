import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Conversation} from '../models/conversation';
import {NewMessage} from '../models/new-message';
import {Message} from '../models/message';
import {SERVER_API_URL} from '../../../app.constants';


@Injectable({
  providedIn: 'root'
})
export class ChatService {

  private MESSAGES = `${SERVER_API_URL}/messages`;

  constructor(private http: HttpClient) {
  }

  getMessages(): Observable<Conversation[]> {
    return this.http.get<Conversation[]>(this.MESSAGES);
  }

  sendMessage(newMsg: NewMessage): Observable<Message> {
    return this.http.post<Message>(this.MESSAGES, newMsg);
  }

  updateDisplayedById(partnerId: number) {
    return this.http.put(this.MESSAGES, partnerId);
  }

  getConversationById(id: number): Observable<Conversation> {
    return this.http.get<Conversation>(this.MESSAGES + '/' + id);
  }
}
