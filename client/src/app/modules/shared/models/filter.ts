import {CategoryAttributes} from './category-attributes';
import {City} from './auction-base-field';
import {Pagination} from './pagination';
import {State} from '../config/enums';

export class Filter extends Pagination {
  state = State.ACTIVE;
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
