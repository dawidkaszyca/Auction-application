import {Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {MatSelectChange} from '@angular/material/select';
import {Pagination} from '../../../../shared/models/pagination';
import {AuctionService} from '../../../../shared/services/auction.service';
import {EnumsHelper, Order, PageSize, SortField, SortKey} from '../../../../shared/config/enums';

@Component({
  selector: 'app-pagination-bar',
  templateUrl: './pagination-bar.component.html',
  styleUrls: ['./pagination-bar.component.scss']
})
export class PaginationBarComponent implements OnInit, OnChanges {

  @Input()
  numberOfAuctions: number;
  @Input()
  isSortDisabled: false;
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
    this.setDataAfterChanges();
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

  selectSort(event: MatSelectChange) {
    this.sortValue = event.value;
    if (event.value === SortKey.MIN_PRICE_KEY) {
      this.pagination.sort = Order.DESC;
      this.pagination.sortByFieldName = SortField.PRICE;
    } else if (event.value === SortKey.MAX_PRICE_KEY) {
      this.pagination.sort = Order.ASC;
      this.pagination.sortByFieldName = SortField.PRICE;
    } else if (event.value === SortKey.POPULARITY_KEY) {
      this.pagination.sort = Order.DESC;
      this.pagination.sortByFieldName = SortField.VIEWERS;
    } else if (event.value === SortKey.NEWEST_KEY) {
      this.pagination.sort = Order.DESC;
      this.pagination.sortByFieldName = SortField.CREATED_DATE;
    } else if (event.value === SortKey.LATEST_KEY) {
      this.pagination.sort = Order.ASC;
      this.pagination.sortByFieldName = SortField.CREATED_DATE;
    }
    this.auctionService.pagination.next(this.pagination);
  }

  changeInput(value: number) {
    this.pagination.page += value;
    this.inputValue = this.pagination.page + 1 + '/' + this.amountOfPages;
    this.auctionService.pagination.next(this.pagination);
  }

  private loadMaps() {
    this.sortList = EnumsHelper.getValuesByEnumName(SortKey);
    this.pageSizeList = EnumsHelper.getValuesByEnumName(PageSize);
  }
}
