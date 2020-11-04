import {Injectable} from '@angular/core';
import {DialogInfoComponent} from '../components/dialog-info/dialog-info.component';
import {MatDialog} from '@angular/material/dialog';
import {Router} from '@angular/router';
import {TranslateService} from '@ngx-translate/core';

@Injectable({
  providedIn: 'root'
})
export class DialogService {

  constructor(public dialog: MatDialog, private router: Router, private translateService: TranslateService) {
  }

  public openWarningDialog(messageKey: string, navigateAfterClose: boolean, navigateLink: string): void {
    this.openDialog(messageKey, true, navigateAfterClose, navigateLink);
  }

  public openInfoDialog(messageKey: string, navigateAfterClose: boolean, navigateLink: string): void {
    this.openDialog(messageKey, false, navigateAfterClose, navigateLink);
  }

  private openDialog(messageKey: string, isWarning_: boolean, navigateAfterClose: boolean, navigateLink: string): void {
    const height_ = this.countHeight(messageKey);
    const dialogRef = this.dialog.open(DialogInfoComponent,
      {
        width: '400px',
        height: height_,
        data: {
          message: messageKey,
          isWarning: isWarning_
        }
      });
    if (navigateAfterClose) {
      dialogRef.afterClosed().subscribe(result => {
        this.router.navigateByUrl(navigateLink);
      });
    }
  }

  private countHeight(messageKey: string): string {
    let a = 200;
    const message = this.translateService.instant(messageKey)
    a += ((message.length / 39) * 18);
    return Math.round(a).toString() + 'px';
  }
}
