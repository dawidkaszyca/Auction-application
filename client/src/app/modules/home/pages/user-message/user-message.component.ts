import {AfterViewChecked, Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {WebsocketService} from '../../../../shared/services/web-socket.service';
import {NavigationService} from '../../../../core/header/navigation/navigation.service';
import {ChatService} from '../../../../shared/services/chat.service';
import {Conversation} from '../../../../shared/models/conversation';
import {NewMessage} from '../../../../shared/models/new-message';
import {Message} from '../../../../shared/models/message';
import {ScrollToBottomDirective} from '../../../../shared/directives/scroll-to-bottom.directive';


@Component({
  selector: 'app-user-message',
  templateUrl: './user-message.component.html',
  styleUrls: ['./user-message.component.scss']
})
export class UserMessageComponent implements OnInit, OnDestroy, AfterViewChecked {

  @ViewChild(ScrollToBottomDirective)
  scroll: ScrollToBottomDirective;
  conversations: Conversation[];
  selected: Conversation;
  selectedMessages: Message[];
  msg: string;
  container: HTMLElement;
  private isNewMSG: boolean;

  constructor(private webSocket: WebsocketService, private navigationService: NavigationService, private chatService: ChatService) {
  }

  ngOnInit(): void {
    this.msg = '';
    this.navigationService.show = false;
    this.chatService.getMessages().subscribe(res => {
      this.conversations = res;
      this.selectFirst();
      this.isNewMSG = true;
    });

    this.webSocket.newMessage.subscribe(data => {
      if (data.id) {
        const conversation = this.findConversationById(data.id);
        data.message.isYours = false;
        conversation.partnerMessages.push(data.message);
        if (this.selected.id === data.id) {
          this.selectedMessages.push(data.message);
          this.isNewMSG = true;
        }
      }
    });
  }

  ngAfterViewChecked() {
    this.scrollToBottom();
  }

  scrollToBottom(): void {
    try {
      if (this.isNewMSG) {
        this.container = document.getElementById('scroller');
        this.container.scrollTop = this.container.scrollHeight;
        this.isNewMSG = false;
      }
    } catch (err) {
    }
  }

  private findConversationById(id: number): Conversation {
    return this.conversations.filter(it => it.id === id)[0];
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
    if (this.msg.trim() === '') {
      return;
    }
    const newMsg = new NewMessage();
    newMsg.content = this.msg;
    newMsg.to = this.selected.partnerId;
    this.chatService.sendMessage(newMsg).subscribe(res => {
      res.isYours = true;
      this.selectedMessages.push(res);
      this.selected.yourMessages.push(res);
      this.msg = '';
      this.isNewMSG = true;
    });
  }

  receiveSelectedConversation($event: Conversation) {
    this.selected = $event;
    this.createOneArrayFromMessages();
    this.isNewMSG = true;
  }
}
