import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {BehaviorSubject, Observable} from 'rxjs';
import {Category} from '../models/category';
import {NewAuction} from '../models/new-auction';
import {AuctionBaseField} from '../models/auction-base-field';
import {Filter} from '../models/filter';
import {SERVER_API_URL} from '../../../app.constants';
import {Auction} from '../models/auction';
import {Pagination} from '../models/pagination';

@Injectable({
  providedIn: 'root'
})
export class AuctionService {

  private CATEGORIES = `${SERVER_API_URL}/categories`;
  private AUCTIONS = `${SERVER_API_URL}/auctions`;
  private AUCTIONS_TO_EDIT = this.AUCTIONS + '/edit';

  filter: BehaviorSubject<Filter>;
  pagination: BehaviorSubject<Pagination>;

  constructor(private http: HttpClient) {
    this.filter = new BehaviorSubject(new Filter());
    this.pagination = new BehaviorSubject(new Pagination());
  }

  updateFilterObj(filter: Filter) {
    this.filter.next(filter);
  }

  updatePaginationObj(pagination: Pagination) {
    this.pagination.next(pagination);
  }

  getCategoryAttributes(category: string): Observable<Category> {
    return this.http.get<Category>(this.CATEGORIES + '/' + category);
  }

  saveAuction(auction: NewAuction) {
    return this.http.post<NewAuction>(this.AUCTIONS, auction);
  }


  getAuctions(filter: Filter): Observable<any> {
    let httpParams = new HttpParams();
    httpParams = httpParams.append('criteria', JSON.stringify(filter));
    return this.http.get<any>(this.AUCTIONS, {params: httpParams});
  }

  getAuctionWithDetailsById(id: number): Observable<Auction> {
    return this.http.get<Auction>(this.AUCTIONS + '/' + id);
  }

  getAuctionForm() {
    return this.http.get<any>(this.AUCTIONS + '/' + 'form');
  }

  loadTopAuction(category: string) {
    let httpParams = new HttpParams();
    httpParams = httpParams.append('category', category);
    return this.http.get<AuctionBaseField[]>(this.AUCTIONS + '/top', {params: httpParams});
  }

  removeByIds(selectedAuctions: number[]) {
    let httpParams = new HttpParams();
    httpParams = httpParams.append('ids', selectedAuctions.toString());
    const options = {params: httpParams};
    return this.http.delete(this.AUCTIONS, options);
  }

  getAuctionWithDetailsToEditById(id: number) {
    return this.http.get<Auction>(this.AUCTIONS_TO_EDIT + '/' + id);
  }

  updateAuction(auction: NewAuction) {
    return this.http.put<NewAuction>(this.AUCTIONS, auction);
  }

  extendAuctionTime(id: number) {
    return this.http.put<string>(this.AUCTIONS + '/' + id, null);
  }
}