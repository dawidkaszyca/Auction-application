import {Injectable} from '@angular/core';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {InfoDialogComponent} from '../components/info-dialog/info-dialog.component';

@Injectable({
  providedIn: 'root'
})
export class InfoDialogService {

  constructor(private modalService: NgbModal) {
  }

  public confirm(
    message: string,
    dialogSize: 'sm' | 'lg' = 'sm'): Promise<boolean> {
    const modalRef = this.modalService.open(InfoDialogComponent, {size: dialogSize});
    modalRef.componentInstance.message = message;
    return modalRef.result;
  }

  public openConfirmationDialog(text: string) {
    this.confirm(text);
  }

}
