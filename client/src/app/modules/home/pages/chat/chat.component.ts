import {AfterViewChecked, Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {WebsocketService} from '../../../../shared/services/web-socket.service';
import {NavigationService} from '../../../../core/header/navigation/navigation.service';
import {ChatService} from '../../../../shared/services/chat.service';
import {Conversation} from '../../../../shared/models/conversation';
import {NewMessage} from '../../../../shared/models/new-message';
import {Message} from '../../../../shared/models/message';
import {ScrollToBottomDirective} from '../../../../shared/directives/scroll-to-bottom.directive';
import {ContactComponent} from '../../components/contact/contact.component';
import {Status} from '../../../../shared/models/status';


@Component({
  selector: 'app-chat',
  templateUrl: './chat.component.html',
  styleUrls: ['./chat.component.scss']
})
export class ChatComponent implements OnInit, OnDestroy, AfterViewChecked {

  @ViewChild(ScrollToBottomDirective)
  scroll: ScrollToBottomDirective;
  @ViewChild(ContactComponent)
  child: ContactComponent;
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
    this.getMessageFromServer();

    this.webSocket.newMessage.subscribe(data => {
      this.setDataAfterNewMessage(data);
    });
  }

  ngAfterViewChecked() {
    this.scrollToBottom();
  }

  ngOnDestroy(): void {
    this.navigationService.show = true;
  }

  private getMessageFromServer() {
    this.chatService.getMessages().subscribe(res => {
      this.conversations = res;
      this.selectFirst();
      this.isNewMSG = true;
    });
  }

  private selectFirst() {
    this.selected = this.conversations[0];
    this.updateDataAfterSelection();
  }

  private setDataAfterNewMessage(data: Status) {
    if (data.id) {
      const conversation = this.findConversationById(data.id);
      data.message.isYours = false;
      if (!conversation.partnerMessages) {
        conversation.partnerMessages = [];
      }
      conversation.partnerMessages.push(data.message);
      if (this.selected.id === data.id) {
        this.selectedMessages.push(data.message);
        this.isNewMSG = true;
      }
    }
  }

  private findConversationById(id: number): Conversation {
    return this.conversations?.filter(it => it.id === id)[0];
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
    this.updateDataAfterSelection();
    this.msg = '';
    this.isNewMSG = true;
  }

  updateDataAfterSelection(): void {
    this.selected.partnerMessages.forEach(it => it.displayed = true);
    this.createOneArrayFromMessages();
    this.updateDisplayed();
    this.child.setUnViewedMessage();
  }

  private updateDisplayed() {
    this.chatService.updateDisplayedById(this.selected.partnerId).subscribe(res => {
      this.webSocket._sendMessage('getStatus');
    });
  }

  private createOneArrayFromMessages() {
    this.selected.partnerMessages.forEach(it => it.isYours = false);
    this.selected.yourMessages.forEach(it => it.isYours = true);
    this.selectedMessages = [];
    this.selectedMessages.push(...this.selected.yourMessages);
    this.selectedMessages.push(...this.selected.partnerMessages);
    this.sortSelected();
  }

  private sortSelected() {
    this.selectedMessages.sort(function(a, b) {
      const aDate = new Date(a.sentDate);
      const bDate = new Date(b.sentDate);
      return aDate < bDate ? -1 : aDate > bDate ? 1 : 0;
    });
  }
}
