import {Component, Inject, OnInit} from '@angular/core';
import {ReportAuction} from '../../../shared/models/report-auction';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {AuctionService} from '../../../shared/services/auction.service';
import {faTimes} from '@fortawesome/free-solid-svg-icons/faTimes';
import {DialogKey, EnumsHelper, ReasonKey} from '../../../shared/config/enums';
import {DialogService} from '../../../shared/services/dialog.service';

@Component({
  selector: 'app-report-auction',
  templateUrl: './report-auction.component.html',
  styleUrls: ['./report-auction.component.scss']
})
export class ReportAuctionComponent implements OnInit {

  reportAuction: ReportAuction;
  reasons: string[];
  faRemove = faTimes;

  constructor(public dialogRef: MatDialogRef<ReportAuctionComponent>, @Inject(MAT_DIALOG_DATA) public data: any,
              private auctionService: AuctionService, private dialogService: DialogService) {
  }

  ngOnInit(): void {
    this.reportAuction = new ReportAuction();
    this.reportAuction.auctionId = this.data.auctionId;
    this.reasons = EnumsHelper.getValuesByEnumName(ReasonKey);
  }

  sendReport() {
    this.auctionService.sendNewReport(this.reportAuction).subscribe(it => {
      this.dialogRef.close();
      this.dialogService.openInfoDialog(DialogKey.AFTER_REPORT, false, null);
    });
  }
}
