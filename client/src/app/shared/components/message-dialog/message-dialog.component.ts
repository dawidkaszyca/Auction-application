import {Component, Input, OnInit} from '@angular/core';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';
import {NewMessage} from '../../models/new-message';
import {ChatService} from '../../services/chat.service';
import {faTimes} from '@fortawesome/free-solid-svg-icons/faTimes';

@Component({
  selector: 'app-message-dialog',
  templateUrl: './message-dialog.component.html',
  styleUrls: ['./message-dialog.component.scss']
})
export class MessageDialogComponent implements OnInit {

  @Input()
  userId: number;
  content: string;
  faRemove = faTimes;

  constructor(private activeModal: NgbActiveModal, private chatService: ChatService) {
    this.content = '';
  }

  ngOnInit(): void {
  }

  public decline() {
    this.activeModal.close(false);
  }

  public save() {
    const message = new NewMessage();
    message.content = this.content;
    message.to = this.userId;
    if (message.content.trim() === '') {
      return;
    }
    this.chatService.sendMessage(message).subscribe(
      res => {
        this.activeModal.close(true);
      },
      err => {
        alert('TODO');
      });
  }

  public dismiss() {
    this.activeModal.dismiss();
  }
}
