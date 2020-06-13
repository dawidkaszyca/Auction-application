import {Component, Input, OnInit} from '@angular/core';
import {faTimes} from '@fortawesome/free-solid-svg-icons/faTimes';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';
import {ChatService} from '../../services/chat.service';
import {NewMessage} from '../../models/new-message';

@Component({
  selector: 'app-info-dialog',
  templateUrl: './info-dialog.component.html',
  styleUrls: ['./info-dialog.component.scss']
})
export class InfoDialogComponent implements OnInit {


  @Input()
  message: string;
  faRemove = faTimes;

  constructor(private activeModal: NgbActiveModal, private chatService: ChatService) {
  }

  ngOnInit(): void {
  }

  public decline() {
    this.activeModal.close(false);
  }

  public dismiss() {
    this.activeModal.dismiss();
  }
}
