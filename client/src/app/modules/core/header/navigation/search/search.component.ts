import {Component, OnInit} from '@angular/core';
import {AuctionService} from '../../../../shared/services/auction.service';
import {Filter} from '../../../../shared/models/filter';
import {Router} from '@angular/router';
import {City} from '../../../../shared/models/auction-base-field';
import {EnumsHelper, Kilometer} from '../../../../shared/config/enums';

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.scss']
})
export class SearchComponent implements OnInit {

  filter: Filter;
  search: string;
  city: City;
  options = {
    types: ['(regions)'],
    componentRestrictions: {
      country: ['PL']
    }
  };
  cityName: any;
  kilometers: number;
  kilometersToSelect: number[];


  constructor(private auctionService: AuctionService, private router: Router) {
    this.clearVariable();
    this.kilometersToSelect = EnumsHelper.getValuesByEnumName(Kilometer);
  }

  ngOnInit(): void {
    this.auctionService.filter.subscribe(cat => {
      this.filter = cat;
    });
  }

  public handleAddressChange(address: any) {
    this.city = new City();
    this.city.name = address.name;
    this.city.longitude = address.geometry.location.lng();
    this.city.latitude = address.geometry.location.lat();
    this.filter.city = this.city;
  }

  updateFilter() {
    const name = this.filter.sortByFieldName;
    const sort = this.filter.sort;
    this.filter = new Filter();
    this.filter.kilometers = this.kilometers;
    this.filter.sort = sort;
    this.filter.sortByFieldName = name;
    if (this.isNonEmptyString(this.city?.name)) {
      this.filter.city = this.city;
      this.filter.kilometers = this.kilometers;
    }
    if (this.isNonEmptyString(this.search)) {
      this.filter.searchWords = this.search.split(' ').filter(Boolean);
    }
    this.filter.isSearchFilter = !this.filter.isSearchFilter;
    this.auctionService.updateFilterObj(this.filter);
    if (this.router.url !== '/') {
      this.router.navigateByUrl('/');
    }
  }

  isNonEmptyString(str: string) {
    return str && str.trim().length > 0;
  }

  clearVariable() {
    this.search = '';
    this.kilometers = 0;
    this.city = new City();
    this.cityName = '';
  }
}
