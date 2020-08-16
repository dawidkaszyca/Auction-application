import {Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {AuctionBaseField} from '../../../../shared/models/auction-base-field';
import {AuctionService} from '../../../../shared/services/auction.service';
import {AttachmentService} from '../../../../shared/services/attachment.service';
import {Router} from '@angular/router';
import {Filter} from '../../../../shared/models/filter';
import {MatSelectChange} from '@angular/material/select';
import {MatDialog} from '@angular/material/dialog';
import {StatisticDialogComponent} from '../../../../shared/components/statistic-dialog/statistic-dialog.component';
import {EditAuctionComponent} from '../../pages/edit-auction/edit-auction.component';

@Component({
  selector: 'app-my-auction-list',
  templateUrl: './my-auction-list.component.html',
  styleUrls: ['./my-auction-list.component.scss'],
})
export class MyAuctionListComponent implements OnInit, OnChanges {

  @Input()
  userId: number;

  private category;
  private filter: Filter;
  auctions: AuctionBaseField[];
  sortList: string[];
  sortValue = 'sort.newest';
  pageSizeList: number[];
  noContent: boolean;
  pageSize = 10;
  page = 1;
  inputValue: string;
  numberOfAuctions: number;
  amountOfPages: number;
  selectedState: string[];
  states: string[];
  selectedAuctions: number[];

  constructor(private auctionService: AuctionService, private attachmentService: AttachmentService, private router: Router,
              public dialog: MatDialog) {
    this.selectedAuctions = [];
    this.selectedState = [];
  }

  ngOnInit(): void {
    this.loadMaps();
    this.auctionService.filter.subscribe(data => {
      this.filter = data;
      this.filter.pageSize = this.pageSize;
      this.filter.page = 0;
      this.page = 1;
      this.category = data.category;
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (this.userId) {
      this.filter = new Filter();
      this.filter.userId = this.userId;
      this.loadAuctionsData();
    }
  }

  private loadAuctionsData() {
    this.auctionService.getAuctions(this.filter).subscribe(
      res => {
        this.auctions = res.auctionListBase;
        this.numberOfAuctions = res.numberOfAuctionByProvidedFilters;
        this.amountOfPages = Math.ceil(this.numberOfAuctions / this.filter.pageSize);
        if (this.amountOfPages === 0) {
          this.amountOfPages = 1;
        }
        this.inputValue = this.page + '/' + this.amountOfPages;
        this.noContent = false;
        if (res == null) {
          this.noContent = true;
        }
        this.loadPhotos();
      },
      err => {
        alert('TODO');
      });
  }

  private loadPhotos() {
    if (this.getAuctionIdList().length > 0) {
      this.attachmentService.getPhotos(this.getAuctionIdList()).subscribe(
        res => {
          const response = new Map(Object.entries(res));
          this.setImages(response);
        });
    }
  }

  private setImages(response: Map<string, string>) {
    this.auctions.forEach(it => {
      if (response.has(it.id.toString())) {
        it.photoUrl = response.get(it.id.toString());
      }
    });
  }

  private getAuctionIdList(): any[] {
    const arrayId = [];
    this.auctions?.forEach(it => {
      arrayId.push(it.id);
    });
    return arrayId;
  }

  selectSort(event: MatSelectChange) {
    this.sortValue = event.value;
    if (event.value === 'sort.minPrice') {
      this.filter.sort = 'DESC';
      this.filter.sortByFieldName = 'price';
    } else if (event.value === 'sort.maxPrice') {
      this.filter.sort = 'ASC';
      this.filter.sortByFieldName = 'price';
    } else if (event.value === 'sort.popularity') {
      this.filter.sort = 'DESC';
      this.filter.sortByFieldName = 'viewers';
    } else if (event.value === 'sort.newest') {
      this.filter.sort = 'DESC';
      this.filter.sortByFieldName = 'createdDate';
    } else if (event.value === 'sort.latest') {
      this.filter.sort = 'ASC';
      this.filter.sortByFieldName = 'createdDate';
    }
    this.loadAuctionsData();
  }

  changeInput(value: number) {
    this.page += value;
    this.filter.page = this.page - 1;
    this.inputValue = this.page + '/' + this.amountOfPages;
    this.loadAuctionsData();
  }

  private loadMaps() {
    this.states = [];
    this.selectedState.push('myAuction.active');
    this.states.push('myAuction.active');
    this.states.push('myAuction.inactive');
    this.sortList = [];
    this.sortList.push('sort.minPrice');
    this.sortList.push('sort.maxPrice');
    this.sortList.push('sort.newest');
    this.sortList.push('sort.latest');
    this.sortList.push('sort.popularity');
    this.pageSizeList = [];
    this.pageSizeList.push(10);
    this.pageSizeList.push(15);
    this.pageSizeList.push(20);
    this.pageSizeList.push(25);
    this.pageSizeList.push(30);
  }

  openAuctionPage(auction: AuctionBaseField) {
    this.router.navigate(['auction'], {queryParams: {'title': auction.title, 'category': auction.category, 'id': auction.id}});
  }

  editAuction(auction: AuctionBaseField) {
    const dialogRef = this.dialog.open(EditAuctionComponent,
      {
        width: '60%',
        height: '80%',
        data: {
          auctionData: auction
        }
      });
    dialogRef.afterClosed().subscribe(it => {
      this.loadAuctionsData();
    });
  }

  selectAuction(id: number) {
    const index = this.selectedAuctions.indexOf(id);
    if (index !== -1) {
      this.selectedAuctions.splice(index, 1);
    } else {
      this.selectedAuctions.push(id);
    }
  }

  selectAllAuctions() {
    if (this.selectedAuctions.length === this.auctions.length) {
      this.selectedAuctions = [];
    } else {
      this.auctions.forEach(it => {
        if (!this.selectedAuctions.includes(it.id)) {
          this.selectedAuctions.push(it.id);
        }
      });
    }
  }

  removeSelectedAuctions() {
    this.auctionService.removeByIds(this.selectedAuctions).subscribe(res => {
      this.selectedAuctions.forEach(it => {
        this.auctions = this.auctions.filter(auction => auction.id !== it);
      });
      this.selectedAuctions = [];
      this.loadAuctionsData();
    });
  }


  selectState(event: MatSelectChange) {
    if (event.value.length === 2) {
      this.filter.state = 'ALL';
    } else if (event.value[0] === 'myAuction.inactive') {
      this.filter.state = 'INACTIVE';
    } else {
      this.filter.state = 'ACTIVE';
    }
    this.loadAuctionsData();
  }

  extendAuctionTime(auction: AuctionBaseField) {
    this.auctionService.extendAuctionTime(auction.id).subscribe(res => {
      auction.expiredDate = res;
    });
  }

  getStatisticDialog(auction: AuctionBaseField) {
    this.dialog.open(StatisticDialogComponent,
      {
        width: '80%',
        height: '80%',
        data: {
          auctionData: auction
        }
      });
  }
}
