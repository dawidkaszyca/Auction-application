import {Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {MatSelectChange} from '@angular/material/select';
import {Pagination} from '../../../../../shared/models/pagination';
import {AuctionService} from '../../../../../shared/services/auction.service';

@Component({
  selector: 'app-pagination-bar',
  templateUrl: './pagination-bar.component.html',
  styleUrls: ['./pagination-bar.component.scss']
})
export class PaginationBarComponent implements OnInit, OnChanges {
  @Input()
  numberOfAuctions: number;
  sortValue = 'sort.newest';
  amountOfPages: number;
  pagination: Pagination;
  pageSizeList: any;
  sortList: any;
  inputValue: any;

  constructor(private auctionService: AuctionService) {
    this.pagination = new Pagination();
  }

  ngOnInit(): void {
    this.loadMaps();
    this.addPaginationListener();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (this.numberOfAuctions) {
      this.setDataAfterChanges();
    }
  }

  private addPaginationListener() {
    this.auctionService.pagination.subscribe(data => {
      this.pagination.page = data.page;
      this.setDataAfterChanges();
    });
  }

  public resetPage(): void {
    this.pagination.page = 0;
    this.setDataAfterChanges();
  }

  private setDataAfterChanges() {
    this.amountOfPages = Math.ceil(this.numberOfAuctions / this.pagination.pageSize);
    if (this.amountOfPages === 0) {
      this.amountOfPages = 1;
    }
    this.inputValue = this.pagination.page + 1 + '/' + this.amountOfPages;
  }

  changePageSize($event: MatSelectChange) {
    this.pagination.pageSize = $event.value;
    this.auctionService.pagination.next(this.pagination);
    this.setDataAfterChanges();
  }

  selectValue(event: MatSelectChange) {
    this.sortValue = event.value;
    if (event.value === 'sort.minPrice') {
      this.pagination.sort = 'DESC';
      this.pagination.sortByFieldName = 'price';
    } else if (event.value === 'sort.maxPrice') {
      this.pagination.sort = 'ASC';
      this.pagination.sortByFieldName = 'price';
    } else if (event.value === 'sort.popularity') {
      this.pagination.sort = 'DESC';
      this.pagination.sortByFieldName = 'viewers';
    } else if (event.value === 'sort.newest') {
      this.pagination.sort = 'DESC';
      this.pagination.sortByFieldName = 'createdDate';
    } else if (event.value === 'sort.latest') {
      this.pagination.sort = 'ASC';
      this.pagination.sortByFieldName = 'createdDate';
    }
    this.auctionService.pagination.next(this.pagination);
  }

  changeInput(value: number) {
    this.pagination.page += value;
    this.inputValue = this.pagination.page + 1 + '/' + this.amountOfPages;
    this.auctionService.pagination.next(this.pagination);
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
