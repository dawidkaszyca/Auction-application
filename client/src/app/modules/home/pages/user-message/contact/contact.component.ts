import {Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {Message} from '../../../../../shared/models/message';
import {Conversation} from '../../../../../shared/models/conversation';

@Component({
  selector: 'app-contact',
  templateUrl: './contact.component.html',
  styleUrls: ['./contact.component.css']
})
export class ContactComponent implements OnInit, OnChanges {

  @Input()
  contacts: Conversation[];
  filteredContacts: Conversation[];

  ngOnChanges(changes: SimpleChanges) {
    this.filteredContacts = this.contacts;
  }

  ngOnInit(): void {
    this.filteredContacts = this.contacts;
  }

  _contactFilter: string;
  get contactFilter() {
    return this._contactFilter;
  }

  set contactFilter(newValue: string) {
    this._contactFilter = newValue;
    this.filteredContacts = this.contactFilter ? this.performFilter(this.contactFilter) : this.contacts;
  }

  constructor() {
    this.contactFilter = '';
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

  private getLastMessageObj(conversation: Conversation): Message {
    const yourLastMsgDate = new Date(conversation.yourMessages[0].sentDate);
    const partnerLastMsgDate = new Date(conversation.yourMessages[0].sentDate);
    if (yourLastMsgDate > partnerLastMsgDate) {
      return conversation.yourMessages[0];
    }
    return conversation.partnerMessages[0];
  }

  getLastMessageDate(conversation: Conversation) {
    const message = this.getLastMessageObj(conversation);
    return message.sentDate;
  }
}
