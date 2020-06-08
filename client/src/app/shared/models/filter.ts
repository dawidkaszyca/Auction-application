import {CategoryAttributes} from './category-attributes';
import {City} from './auction-base-field';

export class Filter {

  sortByFieldName = 'createdDate';
  pageSize = 10;
  page = 0;
  sort = 'DESC';
  searchWords: string[];
  city: City;
  kilometers: number;
  condition: string;
  category = 'all';
  priceFilter = false;
  minPrice: number;
  maxPrice: number;
  isSearchFilter = false;
  /**
   * Map for filters fields single select attributes
   * key - filter field name
   * value - list of filters value (OR)
   */
  filters: CategoryAttributes[];
}
