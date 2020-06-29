import {CategoryAttributes} from './category-attributes';
import {City} from './auction-base-field';
import {Pagination} from './pagination';

export class Filter extends Pagination {
  state = 'ACTIVE';
  userId;
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
