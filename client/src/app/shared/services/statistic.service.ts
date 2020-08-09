import {Injectable} from '@angular/core';
import {SERVER_API_URL} from '../../app.constants';
import {HttpClient} from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class StatisticService {

  private STATISTIC_BASE_CONTROLLER_URL = `${SERVER_API_URL}/statistics`;
  private AUCTION_STATISTIC = this.STATISTIC_BASE_CONTROLLER_URL + '/auctions';

  constructor(private http: HttpClient) {
  }

  getAuctionStatisticById(id: number) {
    return this.http.get(this.AUCTION_STATISTIC + '/' + id);
  }
}
