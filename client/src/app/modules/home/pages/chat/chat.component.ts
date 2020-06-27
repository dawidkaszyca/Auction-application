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
import {timer} from 'rxjs';


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
  private isRequestPending: boolean;

  constructor(private webSocket: WebsocketService, private navigationService: NavigationService, private chatService: ChatService) {
    this.conversations = [];
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
    this.isRequestPending = true;
    this.chatService.getMessages().subscribe(res => {
        if (res) {
          this.conversations = res;
          this.selectFirst();
          this.isNewMSG = true;
        }
        this.isRequestPending = false;
      },
      error => {
        this.isRequestPending = false;
      });
  }

  private selectFirst() {
    if (this.conversations) {
      this.selected = this.conversations[0];
      this.createOneArrayFromMessages();
    }
  }

  private setDataAfterNewMessage(data: Status) {
    if (data.id) {
      const conversation = this.findConversationById(data.id);
      data.message.isYours = false;
      if (!conversation) {
        if (!this.isRequestPending) {
          this.getConversationById(data);
        } else {
          timer(100).subscribe(x => {
            this.setDataAfterNewMessage(data);
          });
        }
        return 0;
      } else if (conversation && !conversation.partnerMessages) {
        conversation.partnerMessages = [];
      }
      if (!this.checkIfMessageAllReadyExist(conversation, data)) {
        conversation.partnerMessages.push(data.message);
      }
      if (this.selected?.id === data.id) {
        this.createOneArrayFromMessages();
        if (!this.checkIfMessageAllReadyExist(this.selected, data)) {
          this.selectedMessages.push(data.message);
          this.isNewMSG = true;
        }
      }
    }
  }

  private checkIfMessageAllReadyExist(conversation: Conversation, data: Status): boolean {
    return !!conversation.partnerMessages
      .filter(it => it.content === data.message.content)
      .filter(it => it.sentDate === data.message.sentDate)[0];
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
    let unDisplayed = 0;
    this.selected.partnerMessages.forEach(it => {
      if (it.displayed === false) {
        it.displayed = true;
        unDisplayed++;
      }
    });
    this.createOneArrayFromMessages();
    if (unDisplayed > 0) {
      this.updateDisplayed();
    }
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
    this.isNewMSG = true;
  }

  private sortSelected() {
    this.selectedMessages.sort(function(a, b) {
      const aDate = new Date(a.sentDate);
      const bDate = new Date(b.sentDate);
      return aDate < bDate ? -1 : aDate > bDate ? 1 : 0;
    });
  }

  private getConversationById(conversationToLoad: Status) {
    this.chatService.getConversationById(conversationToLoad.id).subscribe(
      res => {
        this.conversations.push(res);
        if (!this.selected) {
          this.selectFirst();
        }
      });
  }
}
