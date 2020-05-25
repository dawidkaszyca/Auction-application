import {Component, OnInit} from '@angular/core';
import {AuctionService} from '../../../../shared/services/auction.service';
import {Filter} from '../../../../shared/models/filter';
import {AuctionBaseField} from '../../../../shared/models/auction-base-field';

@Component({
  selector: 'app-main-page',
  templateUrl: './main-page.component.html',
  styleUrls: ['./main-page.component.scss']
})
export class MainPageComponent implements OnInit {

  private category;
  private filter: Filter;
  auctions: AuctionBaseField[];

  constructor(private auctionService: AuctionService) {
  }

  ngOnInit(): void {
    this.auctionService.filter.subscribe(data => {
      this.filter = data;
      this.category = data.category;
      this.loadAuctionsData();
    });
  }

  private loadAuctionsData() {
    this.auctionService.getAuctions(this.filter).subscribe(
      res => {
        this.auctions = res;
      },
      err => {
        alert('TODO');
      });
  }
}
