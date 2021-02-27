import {Injectable} from '@angular/core';
import {SERVER_API_URL} from '../../../app.constants';
import {HttpClient, HttpParams} from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class StatisticService {

  private STATISTIC_BASE_CONTROLLER_URL = `${SERVER_API_URL}/statistics`;
  private AUCTION_STATISTIC = this.STATISTIC_BASE_CONTROLLER_URL + '/auctions';
  private ADMIN_STATISTIC = this.STATISTIC_BASE_CONTROLLER_URL + '/admin';
  private STATISTIC_BY_KEY = this.STATISTIC_BASE_CONTROLLER_URL + '/admin/key';
  constructor(private http: HttpClient) {
  }

  getAuctionStatisticById(id: number) {
    return this.http.get(this.AUCTION_STATISTIC + '/' + id);
  }

  getAdminData() {
    return this.http.get(this.ADMIN_STATISTIC);
  }

  getStatisticByKey(serverKey: string, week: number) {
    let httpParams = new HttpParams();
    const obj = new Statistic();
    obj.key = serverKey;
    obj.week = week;
    httpParams = httpParams.append('data', JSON.stringify(obj));
    return this.http.get<any[]>(this.STATISTIC_BY_KEY, {params: httpParams});
  }
}

export class Statistic {
  key: string;
  week: number;
}
