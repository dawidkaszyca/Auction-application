export class Filter {

  sortByFieldName: string;

  pageSize: number;

  page: number;

  sort = 'NONE';

  /**
   * Map for filters fields single select attributes
   * key - filter field name
   * value - list of filters value (OR)
   */
  filters: Map<string, []>;

  searchWords: string[];

  city: string;

  condition: string;

  priceFilter = false;

  minPrice: number;

  maxPrice: number;
}
