import {Component, OnDestroy, OnInit} from '@angular/core';
import {WebsocketService} from '../../../../shared/services/web-socket.service';
import {NavigationService} from '../../../../core/header/navigation/navigation.service';
import {ChatService} from '../../../../shared/services/chat.service';
import {Conversation} from '../../../../shared/models/conversation';
import {NewMessage} from '../../../../shared/models/new-message';
import {Message} from '../../../../shared/models/message';

@Component({
  selector: 'app-user-message',
  templateUrl: './user-message.component.html',
  styleUrls: ['./user-message.component.scss']
})
export class UserMessageComponent implements OnInit, OnDestroy {

  conversations: Conversation[];
  selected: Conversation;
  selectedMessages: Message[];
  msg: any;

  constructor(private webSocket: WebsocketService, private navigationService: NavigationService, private chatService: ChatService) {
  }


  ngOnInit(): void {
    this.msg = '';
    this.navigationService.show = false;
    this.chatService.getMessages().subscribe(res => {
      this.conversations = res;
      this.selectFirst();
    });
  }

  private selectFirst() {
    this.selected = this.conversations[0];
    this.createOneArrayFromMessages();
  }

  private createOneArrayFromMessages() {
    this.selected.partnerMessages.forEach(it => it.isYours = false);
    this.selected.yourMessages.forEach(it => it.isYours = true);
    this.selectedMessages = [];
    this.selectedMessages.push(...this.selected.yourMessages);
    this.selectedMessages.push(...this.selected.partnerMessages);
    this.selectedMessages.sort(function(a, b) {
      const aDate = new Date(a.sentDate);
      const bDate = new Date(b.sentDate);
      return aDate < bDate ? -1 : aDate > bDate ? 1 : 0;
    });
  }

  ngOnDestroy(): void {
    this.navigationService.show = true;
  }

  sendMessage() {
    const newMsg = new NewMessage();
    newMsg.content = this.msg;
    newMsg.to = this.selected.partnerId;
    this.chatService.sendMessage(newMsg).subscribe(res => {
      this.selectedMessages.push(res);
      this.msg = '';
    });
  }
}
