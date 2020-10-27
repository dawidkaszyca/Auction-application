import { Component, OnInit } from '@angular/core';
import {AuctionBaseField} from '../../../shared/models/auction-base-field';
import {Filter} from '../../../shared/models/filter';
import {Pagination} from '../../../shared/models/pagination';
import {ActivatedRoute} from '@angular/router';
import {AuctionService} from '../../../shared/services/auction.service';

@Component({
  selector: 'app-favorite-auction',
  templateUrl: './favorite-auction.component.html',
  styleUrls: ['./favorite-auction.component.scss']
})
export class FavoriteAuctionComponent implements OnInit {
  userId: number;
  auctions: AuctionBaseField[];
  numberOfAuctions: number;
  filter: Filter;
  pagination: Pagination;

  constructor(private router: ActivatedRoute, private auctionService: AuctionService) {
  }

  ngOnInit(): void {
    this.filter = new Filter();
    this.pagination = new Pagination();
    this.loadUserAuctions();
    this.addPaginationListener();
  }

  private addPaginationListener() {
    this.auctionService.pagination.subscribe(data => {
      this.pagination = data;
      this.filter.page = data.page;
      this.filter.pageSize = data.pageSize;
      this.loadUserAuctions();
    });
  }

  private loadUserAuctions(): void {
    this.auctionService.getFavoritesAuction(this.filter.page, this.filter.pageSize).subscribe(res => {
      this.auctions = res.auctionListBase;
      this.numberOfAuctions = res.numberOfAuctionByProvidedFilters;
    });
  }
}
