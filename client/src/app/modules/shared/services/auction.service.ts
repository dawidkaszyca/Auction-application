import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders, HttpParams} from '@angular/common/http';
import {BehaviorSubject, Observable} from 'rxjs';
import {Category} from '../models/category';
import {NewAuction} from '../models/new-auction';
import {AuctionBaseField} from '../models/auction-base-field';
import {Filter} from '../models/filter';
import {SERVER_API_URL} from '../../../app.constants';
import {Auction} from '../models/auction';
import {Pagination} from '../models/pagination';
import {ReportAuction} from '../models/report-auction';

@Injectable({
  providedIn: 'root'
})
export class AuctionService {

  private CATEGORIES = `${SERVER_API_URL}/categories`;
  private AUCTIONS = `${SERVER_API_URL}/auctions`;
  private AUCTIONS_TO_EDIT = this.AUCTIONS + '/edit';
  private FAVORITE_AUCTIONS = `${SERVER_API_URL}/auction` + '/favorites';
  private REPORT_AUCTION = `${SERVER_API_URL}/report` + '/auctions';

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

  getFavoritesAuction(page: number, pageSize: number) {
    let httpParams = new HttpParams();
    httpParams = httpParams.append('page', page.toString());
    httpParams = httpParams.append('pageSize', pageSize.toString());
    return this.http.get<any>(this.FAVORITE_AUCTIONS, {params: httpParams});
  }

  addToFavorite(auctionId: number) {
    let headers = new HttpHeaders();
    headers = headers.append('content-type', 'application/json');
    return this.http.post<JSON>(this.FAVORITE_AUCTIONS, auctionId.toString(), {headers});
  }

  sendNewReport(reportAuction: ReportAuction) {
    return this.http.post<ReportAuction>(this.REPORT_AUCTION, reportAuction);
  }
}
