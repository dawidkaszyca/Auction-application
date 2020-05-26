import {CategoryAttributes} from './category-attributes';

export class Filter {

  sortByFieldName = 'createdDate';

  pageSize = 10;

  page = 0;

  sort = 'DESC';

  /**
   * Map for filters fields single select attributes
   * key - filter field name
   * value - list of filters value (OR)
   */
  filters: CategoryAttributes[];

  searchWords: string[];

  city: string;

  condition: string;

  category = 'all';

  priceFilter = false;

  minPrice: number;

  maxPrice: number;

  isSearchFilter = false;
}
