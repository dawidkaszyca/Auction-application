import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {Filter} from '../../../../shared/models/filter';
import {AuctionService} from '../../../../shared/services/auction.service';
import {AuctionBaseField} from '../../../../shared/models/auction-base-field';
import {Pagination} from '../../../../shared/models/pagination';

@Component({
  selector: 'app-user-auctions',
  templateUrl: './user-auctions.component.html',
  styleUrls: ['./user-auctions.component.scss']
})
export class UserAuctionsComponent implements OnInit {

  userId: number;
  auctions: AuctionBaseField[];
  numberOfAuctions: number;
  filter: Filter;
  pagination: Pagination;
  userName: string;

  constructor(private router: ActivatedRoute, private auctionService: AuctionService) {
  }

  ngOnInit(): void {
    this.filter = new Filter();
    this.pagination = new Pagination();
    this.userId = this.router.snapshot.queryParams.id;
    this.userName = this.router.snapshot.queryParams.name;
    this.loadUserAuctions();
    this.addPaginationListener();
  }

  private addPaginationListener() {
    this.auctionService.pagination.subscribe(data => {
      this.pagination = data;
      this.filter.page = data.page;
      this.filter.pageSize = data.pageSize;
      this.filter.sort = data.sort;
      this.filter.sortByFieldName = data.sortByFieldName;
      this.loadUserAuctions();
    });
  }

  private loadUserAuctions(): void {
    this.filter.userId = this.userId;
    this.auctionService.getAuctions(this.filter).subscribe(res => {
      this.auctions = res.auctionListBase;
      this.numberOfAuctions = res.numberOfAuctionByProvidedFilters;
    });
  }

}
