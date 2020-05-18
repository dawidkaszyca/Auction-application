import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Category} from '../models/category';
import {NewAuction} from '../models/new-auction';
import {AuctionBaseField} from '../models/auction-base-field';

@Injectable({
  providedIn: 'root'
})
export class AuctionService {

  private DOMAIN_URL = 'http://localhost:8082';
  private BASE_URL = this.DOMAIN_URL + '/api';
  private CATEGORIES = this.BASE_URL + '/categories';
  private AUCTIONS = this.BASE_URL + '/auctions';

  constructor(private http: HttpClient) {
  }

  getCategories(): Observable<string[]> {
    return this.http.get<string[]>(this.CATEGORIES);
  }

  getCategoryAttributes(category: string): Observable<Category> {
    return this.http.get<Category>(this.CATEGORIES + '/' + category);
  }

  saveAuction(auction: NewAuction) {
    return this.http.post<NewAuction>(this.AUCTIONS, auction);
  }

  getAuctions(pageSize: number, page: number, category: string): Observable<AuctionBaseField[]> {
    let httpParams = new HttpParams();
    httpParams = httpParams.append('pageSize', pageSize.toString());
    httpParams = httpParams.append('page', page.toString());
    httpParams = httpParams.append('category', category.toString());
    return this.http.get<AuctionBaseField[]>(this.AUCTIONS, {params: httpParams});
  }

  getAuctionForm() {
    return this.http.get<any>(this.AUCTIONS + '/' + 'form');
  }
}
