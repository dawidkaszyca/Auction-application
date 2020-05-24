import {Component, OnInit} from '@angular/core';
import {Filter} from '../../../../shared/models/filter';
import {AuctionService} from '../../../../shared/services/auction.service';
import {CategoryAttributes} from '../../../../shared/models/category-attributes';
import {MatSelectChange} from '@angular/material/select';
import {AttributesValues} from '../../../../shared/models/attributes-values';

@Component({
  selector: 'app-filter-bar',
  templateUrl: './filter-bar.component.html',
  styleUrls: ['./filter-bar.component.scss']
})
export class FilterBarComponent implements OnInit {

  filter: Filter;
  minPrice: number;
  maxPrice: number;
  selectedCondition: string;
  attributesFilters: CategoryAttributes[];
  category = 'all';
  attributes: CategoryAttributes[];
  conditions: string[];

  constructor(private auctionService: AuctionService) {
  }

  ngOnInit(): void {
    this.attributesFilters = [];
    this.getCondition();
    this.auctionService.filter.subscribe(data => {
      this.filter = data;
      if (this.category !== data.category && data.category !== 'all') {
        this.category = data.category;
        this.loadFiltersByCategory();
      }
    });
  }

  private loadFiltersByCategory() {
    this.auctionService.getCategoryAttributes(this.category).subscribe(res => {
        this.attributes = res.categoryAttributes;
      },
      err => {
        alert('TODO');
      });
  }

  private getCondition() {
    this.auctionService.getAuctionForm().subscribe(
      res => {
        this.conditions = res.condition;
        this.conditions.push('------------');
      },
      err => {
        alert('TODO');
      });
  }

  selectValues(event: MatSelectChange, attribute: string) {
    if (event.value.length === 0) {
      this.attributesFilters = this.removeObjByAttr(attribute);
    } else {
      if (event.value.length > 1) {
        this.attributesFilters = this.removeObjByAttr(attribute);
      }
      const obj = new CategoryAttributes();
      obj.attribute = attribute;
      obj.attributeValues = this.getValuesObj(event);
      this.attributesFilters.push(obj);
    }
    console.error(attribute);
  }

  private getValuesObj(event: MatSelectChange) {
    let array = [];
    const attributeValues = [];
    array = event.value;
    array.forEach(it => {
      const cat = new AttributesValues();
      cat.value = it;
      attributeValues.push(cat);
    });
    return attributeValues;
  }

  private removeObjByAttr(attribute: string) {
    return this.attributesFilters.filter(x => x.attribute !== attribute);
  }

  setFilters() {
    if (this.minPrice && this.minPrice !== 0) {
      this.filter.priceFilter = true;
      this.filter.minPrice = this.minPrice;
    } else {
      this.filter.minPrice = null;
    }
    if (this.maxPrice && this.maxPrice !== 0) {
      this.filter.priceFilter = true;
      this.filter.maxPrice = this.maxPrice;
    } else {
      this.filter.maxPrice = this.maxPrice;
      if (!this.minPrice || this.minPrice === 0) {
        this.filter.priceFilter = false;
      }
    }
    if (this.selectedCondition) {
      this.filter.condition = this.selectedCondition;
    } else {
      this.filter.condition = null;
    }
    if (this.attributesFilters.length > 0) {
      this.filter.filters = this.attributesFilters;
    } else {
      this.filter.filters = [];
    }
    this.auctionService.updateFilterObj(this.filter);
  }
}
