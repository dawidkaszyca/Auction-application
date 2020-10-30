import {Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {Pagination} from '../../../../shared/models/pagination';
import {AuctionService} from '../../../../shared/services/auction.service';
import {EnumsHelper, PageSize} from '../../../../shared/config/enums';

@Component({
  selector: 'app-pagination-short-bar',
  templateUrl: './pagination-short-bar.component.html',
  styleUrls: ['./pagination-short-bar.component.scss']
})
export class PaginationShortBarComponent implements OnInit, OnChanges {

  @Input()
  numberOfAuctions: number;
  amountOfPages: number;
  pagination: Pagination;
  pageSizeList: any;
  inputValue: any;

  constructor(private auctionService: AuctionService) {
    this.pagination = new Pagination();
  }

  ngOnInit(): void {
    this.pageSizeList = EnumsHelper.getValuesByEnumName(PageSize);
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

  changeInput(value: number) {
    this.pagination.page += value;
    this.inputValue = this.pagination.page + 1 + '/' + this.amountOfPages;
    this.auctionService.pagination.next(this.pagination);
  }
}
