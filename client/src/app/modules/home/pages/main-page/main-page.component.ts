import {Component, OnInit} from '@angular/core';
import {AuctionService} from '../../../../shared/services/auction.service';
import {Filter} from '../../../../shared/models/filter';
import {AuctionBaseField} from '../../../../shared/models/auction-base-field';
import {AttachmentService} from '../../../../shared/services/attachment.service';
import {MatSelectChange} from '@angular/material/select';

@Component({
  selector: 'app-main-page',
  templateUrl: './main-page.component.html',
  styleUrls: ['./main-page.component.scss']
})
export class MainPageComponent implements OnInit {

  private category;
  private filter: Filter;
  auctions: AuctionBaseField[];
  topAuctions: AuctionBaseField[];
  sortList: string[];
  sortValue = 'sort.newest';
  pageSizeList: number[];
  noContent: boolean;
  pageSize = 10;
  page = 1;
  inputValue: string;
  numberOfAuctions: number;
  amountOfPages: number;

  constructor(private auctionService: AuctionService, private attachmentService: AttachmentService) {
      this.loadMaps();
  }

  ngOnInit(): void {
    this.auctionService.filter.subscribe(data => {
      this.filter = data;
      this.filter.pageSize = this.pageSize;
      this.category = data.category;
      this.loadAuction();
    });
  }

  private loadAuction() {
    this.loadAuctionsData();
    this.loadTopAuction();
  }

  private loadAuctionsData() {
    this.auctionService.getAuctions(this.filter).subscribe(
      res => {
        this.auctions = res.auctionListBase;
        this.numberOfAuctions = res.numberOfAuctionByProvidedFilters;
        this.amountOfPages = Math.ceil(this.numberOfAuctions / this.filter.pageSize);
        this.inputValue = this.page + '/' + this.amountOfPages;
        this.noContent = false;
        if (res == null) {
          this.noContent = true;
        }
      },
      err => {
        alert('TODO');
      });
  }

  private loadTopAuction() {
    this.auctionService.loadTopAuction(this.filter.category).subscribe(
      res => {
        this.topAuctions = res;
        this.loadPhotos();
      },
      err => {
        alert('TODO');
      });
  }

  private loadPhotos() {
    this.attachmentService.getPhotos(this.getAuctionIdList()).subscribe(
      res => {
        const response = new Map(Object.entries(res));
        this.setImages(response);
      });
  }

  private setImages(response: Map<string, string>) {
    this.topAuctions.forEach(it => {
      if (response.has(it.id.toString())) {
        it.photoUrl = response.get(it.id.toString());
      }
    });
  }

  private getAuctionIdList() {
    const arrayId = [];
    this.topAuctions?.forEach(it => {
      arrayId.push(it.id);
    });
    return arrayId;
  }

  selectValue(event: MatSelectChange) {
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
    this.loadTopAuction();
  }

  changeInput(value: number) {
    this.page += value;
    this.filter.page = this.page - 1;
    this.inputValue = this.page + '/' + this.amountOfPages;
    this.loadAuction();
  }

  changePageSize($event: MatSelectChange) {
    this.pageSize = $event.value;
    this.filter.pageSize = this.pageSize;
    this.loadAuction();
  }

  private loadMaps() {
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
}
