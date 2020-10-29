import {Component, Inject, OnInit} from '@angular/core';
import {ReportAuction} from '../../../shared/models/report-auction';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {AuctionService} from '../../../shared/services/auction.service';
import {faTimes} from '@fortawesome/free-solid-svg-icons/faTimes';

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
              private auctionService: AuctionService) {
  }

  ngOnInit(): void {
    this.reportAuction = new ReportAuction();
    this.reportAuction.auctionId = this.data.auctionId;
    this.reasons = [];
    this.reasons.push('Spam');
    this.reasons.push('Niedozwolona treść / przedmiot / usługa');
    this.reasons.push('Przeterminowane ogłoszenie / sprzedane');
    this.reasons.push('Oszustwo / podejrzenie oszustwa');
  }

  sendReport() {
    this.auctionService.sendNewReport(this.reportAuction).subscribe(it => {
      this.dialogRef.close();
    }, error => {

    });
  }
}
