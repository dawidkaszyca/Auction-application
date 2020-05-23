import {Component, OnInit} from '@angular/core';
import {AuctionService} from '../../../../shared/services/auction.service';
import {Filter} from '../../../../shared/models/filter';

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.scss']
})
export class SearchComponent implements OnInit {

  filter: Filter;
  search: string;
  city: string;

  constructor(private auctionService: AuctionService) {
    this.clearVariable();
  }

  ngOnInit(): void {
    this.auctionService.filter.subscribe(cat => {
      this.filter = cat;
      console.error('zmiana filtru');
    });
  }

  updateFilter() {
    if (this.isNonEmptyString(this.city)) {
      this.filter.city = this.city;
    }
    if (this.isNonEmptyString(this.search)) {
      this.filter.searchWords = this.search.split(' ').filter(Boolean);
    }
    this.auctionService.updateFilterObj(this.filter);
    this.clearVariable();
  }

  isNonEmptyString(str: string) {
    return str && str.trim().length > 0;
  }

  clearVariable() {
    this.search = '';
    this.city = '';
  }
}
