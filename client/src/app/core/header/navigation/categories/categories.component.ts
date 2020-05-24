import {Component, OnInit} from '@angular/core';
import {AuctionService} from '../../../../shared/services/auction.service';
import {Filter} from '../../../../shared/models/filter';

@Component({
  selector: 'app-categories',
  templateUrl: './categories.component.html',
  styleUrls: ['./categories.component.scss']
})
export class CategoriesComponent implements OnInit {
  filter: Filter;

  constructor(private auctionService: AuctionService) {
  }

  ngOnInit(): void {

  }

  sendSelectedCategory(category: string) {
    if (category !== this.filter?.category) {
      this.filter = new Filter();
      this.filter.category = category;
      this.auctionService.updateFilterObj(this.filter);
    }
  }

}
