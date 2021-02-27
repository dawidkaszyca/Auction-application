import {Component, OnInit, ViewChild} from '@angular/core';
import {AuctionService} from '../../../../shared/services/auction.service';
import {Filter} from '../../../../shared/models/filter';
import {AuctionBaseField} from '../../../../shared/models/auction-base-field';
import {AttachmentService} from '../../../../shared/services/attachment.service';
import {PaginationBarComponent} from '../../../components/pagination/pagination-bar/pagination-bar.component';
import {Pagination} from '../../../../shared/models/pagination';
import {PaginationShortBarComponent} from '../../../components/pagination/pagination-short-bar/pagination-short-bar.component';

@Component({
  selector: 'app-main-page',
  templateUrl: './main-page.component.html',
  styleUrls: ['./main-page.component.scss']
})
export class MainPageComponent implements OnInit {

  @ViewChild(PaginationBarComponent)
  paginationBarChild: PaginationBarComponent;
  @ViewChild(PaginationShortBarComponent)
  shortPaginationChild: PaginationShortBarComponent;
  private category;
  private filter: Filter;
  auctions: AuctionBaseField[];
  topAuctions: AuctionBaseField[];
  noContent: boolean;
  pagination: Pagination;
  numberOfAuctions: number;

  constructor(private auctionService: AuctionService, private attachmentService: AttachmentService) {
  }

  ngOnInit(): void {
    this.addFilterListener();
    this.addPaginationListener();
  }

  private addFilterListener() {
    this.auctionService.filter.subscribe(data => {
      this.filter = data;
      if (!this.pagination) {
        this.pagination = new Pagination();
      } else {
        this.paginationBarChild.resetPage();
        this.shortPaginationChild.resetPage();
      }
      this.filter.pageSize = this.pagination.pageSize;
      this.filter.page = 0;
      this.category = data.category;
      if (this.filter.priceFilter || this.filter?.condition || this.filter.filters?.length > 0) {
        this.loadAuction(false);
      } else {
        this.loadAuction(true);
      }
    });
  }

  private addPaginationListener() {
    this.auctionService.pagination.subscribe(data => {
      this.pagination = data;
      this.filter.page = data.page;
      this.filter.pageSize = data.pageSize;
      this.filter.sort = data.sort;
      this.filter.sortByFieldName = data.sortByFieldName;
      this.loadAuction(false);
    });
  }

  private loadAuction(loadTopAuction: boolean) {
    this.loadAuctionsData();
    if (loadTopAuction) {
      this.loadTopAuction();
    }
  }

  private loadAuctionsData() {
    this.auctionService.getAuctions(this.filter).subscribe(
      res => {
        this.auctions = res.auctionListBase;
        this.numberOfAuctions = res.numberOfAuctionByProvidedFilters;
        this.noContent = false;
        if (res == null) {
          this.noContent = true;
        }
      });
  }

  private loadTopAuction() {
    this.auctionService.loadTopAuction(this.filter.category).subscribe(
      res => {
        this.topAuctions = res;
        this.loadPhotos();
      });
  }

  private loadPhotos() {
    if (this.getAuctionIdList().length > 0) {
      this.attachmentService.getPhotos(this.getAuctionIdList()).subscribe(
        res => {
          if (res) {
            const response = new Map(Object.entries(res));
            this.setImages(response);
          }
        });
    }
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
}
