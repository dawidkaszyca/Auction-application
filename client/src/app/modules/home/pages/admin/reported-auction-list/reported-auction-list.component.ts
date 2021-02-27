import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {NavigationService} from '../../../../core/header/navigation/navigation.service';
import {MatTableDataSource} from '@angular/material/table';
import {SelectionModel} from '@angular/cdk/collections';
import {MatSort} from '@angular/material/sort';
import {MatPaginator} from '@angular/material/paginator';
import {AuthService} from '../../../../core/security/auth.service';
import {AuctionService} from '../../../../shared/services/auction.service';
import {ReportAuction} from '../../../../shared/models/report-auction';
import {MatDialog} from '@angular/material/dialog';
import {AuctionPreviewComponent} from '../../user/auction-preview/auction-preview.component';

@Component({
  selector: 'app-reported-auction-list',
  templateUrl: './reported-auction-list.component.html',
  styleUrls: ['./reported-auction-list.component.scss']
})
export class ReportedAuctionListComponent implements OnInit, OnDestroy {

  reportAuctions: ReportAuction[];
  dataSource: MatTableDataSource<ReportAuction>;
  displayedColumns: string[] = ['id', 'reason'];
  selection: SelectionModel<ReportAuction>;
  selectedRecord: ReportAuction;

  @ViewChild(MatSort, {static: true}) sort: MatSort;
  @ViewChild(MatPaginator, {static: true}) paginator: MatPaginator;
  magicString = '      ';
  substantiation = this.magicString;

  constructor(private authService: AuthService, private navigationService: NavigationService, private auctionService: AuctionService,
              public dialog: MatDialog) {
    this.getAllActiveReports();
  }

  ngOnInit(): void {
    this.navigationService.show = false;
  }

  ngOnDestroy(): void {
    this.navigationService.show = true;
  }

  private getAllActiveReports() {
    this.auctionService.getAllReports().subscribe(it => {
      if (it != null) {
      this.reportAuctions = it;
      this.dataSource = new MatTableDataSource<ReportAuction>(this.reportAuctions);
      this.dataSource.sort = this.sort;
      this.dataSource.paginator = this.paginator;
      this.selection = new SelectionModel<ReportAuction>(false, [this.reportAuctions[0]]);
      this.selectRecord(this.reportAuctions[0]);
      }
    });
  }

  selectRecord(row: any) {
    this.selectedRecord = row;
    if (!this.selectedRecord.description.startsWith(this.magicString)) {
      this.selectedRecord.description = this.magicString + this.selectedRecord.description;
    }
  }

  showAuction() {
    this.dialog.open(AuctionPreviewComponent,
      {
        width: '60%',
        height: '80%',
        data: {
          auctionData: this.selectedRecord.auctionId
        }
      });
  }

  sendDecision(decision: boolean) {
    this.selectedRecord.decision = decision;
    this.selectedRecord.substantiation = this.substantiation.replace(this.magicString, '');
    this.selectedRecord.description = this.selectedRecord.description.replace(this.magicString, '');
    this.auctionService.sendReportDecision(this.selectedRecord).subscribe(it => {
      this.substantiation = this.magicString;
      this.getAllActiveReports();
    });
  }
}
