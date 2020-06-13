import {Injectable} from '@angular/core';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {MessageDialogComponent} from '../components/message-dialog/message-dialog.component';
import {InfoDialogService} from './info-dialog.service';
import {TranslateService} from '@ngx-translate/core';

@Injectable({
  providedIn: 'root'
})
export class SendMessageService {

  constructor(private modalService: NgbModal, private infoDialogService: InfoDialogService, private translate: TranslateService) { }

  public confirm(
    userId: number,
    dialogSize: 'sm'|'lg' = 'sm'): Promise<boolean> {
    const modalRef = this.modalService.open(MessageDialogComponent, { size: dialogSize });
    modalRef.componentInstance.userId = userId;
    return modalRef.result;
  }

  public openConfirmationDialog(text: number) {
    this.confirm( text)
      .then((confirmed) => {
        if (confirmed === true) {
          const confirmMessage = this.translate.instant('dialog.sent');
          this.infoDialogService.openConfirmationDialog(confirmMessage);
        }
      })
      .catch(() => console.log('User dismissed the dialog (e.g., by using ESC, clicking the cross icon, or clicking outside the dialog)'));
  }
}
