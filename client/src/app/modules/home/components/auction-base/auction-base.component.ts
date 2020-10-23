import {Component, Input, OnInit} from '@angular/core';
import {AuctionBaseField} from '../../../shared/models/auction-base-field';
import {Router} from '@angular/router';

@Component({
  selector: 'app-auction-base',
  templateUrl: './auction-base.component.html',
  styleUrls: ['./auction-base.component.scss']
})
export class AuctionBaseComponent implements OnInit {

  @Input()
  auction: AuctionBaseField;

  constructor(private router: Router) {
  }

  ngOnInit(): void {
  }

  openAuctionPage() {
    this.router.navigate(['auction'],
      {queryParams: {'title': this.auction.title, 'category': this.auction.category, 'id': this.auction.id}});
  }
}
