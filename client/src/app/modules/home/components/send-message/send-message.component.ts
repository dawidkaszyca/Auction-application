import {Component, Input, OnInit} from '@angular/core';
import {ChatService} from '../../../shared/services/chat.service';
import {NewMessage} from '../../../shared/models/new-message';
import {AuthService} from '../../../core/security/auth.service';
import {TranslateService} from '@ngx-translate/core';
import {Router} from '@angular/router';
import {DialogService} from '../../../shared/services/dialog.service';
import {DialogKey} from '../../../shared/config/enums';

@Component({
  selector: 'app-send-message',
  templateUrl: './send-message.component.html',
  styleUrls: ['./send-message.component.scss']
})
export class SendMessageComponent implements OnInit {

  @Input()
  userId: number;
  content: string;

  constructor(private chatService: ChatService, private authService: AuthService, private translate: TranslateService,
              private router: Router, private dialogService: DialogService) {
  }

  ngOnInit(): void {
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
        this.content = this.translate.instant('dialog.after-sent');
      },
      err => {
        if (err.error === 'Cannot send message to Yourself!!!') {
          this.dialogService.openWarningDialog(DialogKey.AFTER_MESSAGE_SENT_TO_YOURSELF, false, null);
        } else {
          this.dialogService.openWarningDialog(DialogKey.AFTER_MESSAGE_ERROR, false, null);
        }
      });
  }


  loginUser() {
    if (!this.authService.isLogged()) {
      this.router.navigate(['/login'], {queryParams: {returnUrl: this.router.url}});
    }
  }
}
