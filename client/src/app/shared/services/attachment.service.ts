import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Attachment} from '../models/attachment';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AttachmentService {

  private DOMAIN_URL = 'http://localhost:8082';
  private BASE_URL = this.DOMAIN_URL + '/api';
  private SAVE_AUCTION_ATTACHMENT = this.BASE_URL + '/attachments';

  constructor(private http: HttpClient) {
  }

  saveAttachment(formData: FormData) {
    return this.http.post<Attachment>(this.SAVE_AUCTION_ATTACHMENT, formData);
  }

  getPhotos(data: any): Observable<any> {
    let idList = '';
    for (const id of data) {
      idList += id + ',';
    }
    idList = idList.substring(0, idList.length - 1);
    return this.http.get<any>(this.SAVE_AUCTION_ATTACHMENT + '/' + idList);
  }
}
