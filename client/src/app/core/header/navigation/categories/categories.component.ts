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
    this.auctionService.filter.subscribe(data => {
      this.filter = data;
    });
  }

  sendSelectedCategory(category: string) {
    if (category !== this.filter?.category) {
      const name = this.filter.sortByFieldName;
      const sort = this.filter.sort;
      this.filter = new Filter();
      this.filter.sort = sort;
      this.filter.sortByFieldName = name;
      this.filter.category = category;
      this.auctionService.updateFilterObj(this.filter);
    }
  }

}
