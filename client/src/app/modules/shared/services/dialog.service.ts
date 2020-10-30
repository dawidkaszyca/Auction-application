import {Injectable} from '@angular/core';
import {DialogInfoComponent} from '../components/dialog-info/dialog-info.component';
import {MatDialog} from '@angular/material/dialog';

@Injectable({
  providedIn: 'root'
})
export class DialogService {

  constructor(public dialog: MatDialog) {
  }

  public openWarningDialog(messageKey: string): void {
    this.openDialog(messageKey, true);
  }

  public openInfoDialog(messageKey: string): void {
    this.openDialog(messageKey, false);
  }

  private openDialog(messageKey: string, isWarning_: boolean): void {
    this.dialog.open(DialogInfoComponent,
      {
        width: '400px',
        height: '250px',
        data: {
          message: messageKey,
          isWarning: isWarning_
        }
      });
  }
}
