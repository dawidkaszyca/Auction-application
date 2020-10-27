import {Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges} from '@angular/core';
import {Message} from '../../../../shared/models/message';
import {Conversation} from '../../../../shared/models/conversation';
import {WebsocketService} from '../../../../shared/services/web-socket.service';
import {timer} from 'rxjs';

@Component({
  selector: 'app-contact',
  templateUrl: './contact.component.html',
  styleUrls: ['./contact.component.css']
})
export class ContactComponent implements OnInit, OnChanges {

  @Input()
  contacts: Conversation[];
  @Output()
  selectedConversation: EventEmitter<Conversation> = new EventEmitter();
  selected: Conversation;
  _contactFilter: string;

  filteredContacts: Conversation[];


  constructor(private webSocket: WebsocketService) {
    this.contactFilter = '';
  }

  ngOnInit(): void {
    this.webSocket.newMessage.subscribe(data => {
      timer(50).subscribe(x => {
        this.setUnViewedMessage();
        this.sortContactsByNewestMessage();
      });
    });
    this.filteredContacts = this.contacts;
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.contacts.currentValue) {
      this.setUnViewedMessage();
    }
    this.sortContactsByNewestMessage();
    this.filteredContacts = this.contacts;
  }

  private sortContactsByNewestMessage() {
    const this_ = this;
    this.contacts.sort(function(a, b) {
      const aDate = this_.getLastMessageDate(a);
      const bDate = this_.getLastMessageDate(b);
      return aDate > bDate ? -1 : aDate < bDate ? 1 : 0;
    });
    if (!this.selected) {
      this.selected = this.contacts[0];
    }
  }

  public setUnViewedMessage() {
    this.contacts?.forEach(it => {
      let count = 0;
      it.partnerMessages.forEach(msg => {
        if (msg.displayed === false) {
          count++;
        }
        it.unViewed = count;
      });
    });
  }

  get contactFilter() {
    return this._contactFilter;
  }

  set contactFilter(newValue: string) {
    this._contactFilter = newValue;
    this.filteredContacts = this.contactFilter ? this.performFilter(this.contactFilter) : this.contacts;
  }

  performFilter(filterBy: string): Conversation[] {
    filterBy = filterBy.toLocaleLowerCase();
    return this.contacts.filter(
      (contact: Conversation) => contact.name.toLocaleLowerCase().indexOf(filterBy) > -1
    );
  }

  getLastMessage(conversation: Conversation): string {
    const message = this.getLastMessageObj(conversation);
    return message.content;
  }

  getLastMessageDate(conversation: Conversation) {
    if (conversation) {
      const message = this.getLastMessageObj(conversation);
      return message.sentDate;
    }
    return null;
  }

  private getLastMessageObj(conversation: Conversation): Message {
    const urMsg = conversation.yourMessages[conversation.yourMessages.length - 1];
    const partnerMsg = conversation.partnerMessages[conversation.partnerMessages.length - 1];
    if (!urMsg || !partnerMsg) {
      return urMsg || partnerMsg;
    }
    const yourLastMsgDate = new Date(urMsg.sentDate);
    const partnerLastMsgDate = new Date(partnerMsg.sentDate);
    if (yourLastMsgDate > partnerLastMsgDate) {
      return urMsg;
    }
    return partnerMsg;
  }

  selectContact(contact: Conversation) {
    this.selected = contact;
    this.selectedConversation.emit(contact);
  }
}
