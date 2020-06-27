import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Attachment} from '../models/attachment';
import {Observable} from 'rxjs';
import {SERVER_API_URL} from '../../app.constants';
import {Image} from '../models/image';

@Injectable({
  providedIn: 'root'
})
export class AttachmentService {

  private ATTACHMENT = `${SERVER_API_URL}/attachments`;
  private USER = this.ATTACHMENT + '/user';

  constructor(private http: HttpClient) {
  }

  saveAttachment(formData: FormData) {
    return this.http.post<Attachment>(this.ATTACHMENT, formData);
  }

  getPhotos(data: any): Observable<any> {
    let idList = '';
    for (const id of data) {
      idList += id + ',';
    }
    idList = idList.substring(0, idList.length - 1);
    return this.http.get<any>(this.ATTACHMENT + '/' + idList);
  }

  getAuctionPhotosById(auctionId: number): Observable<Image[]> {
    let httpParams = new HttpParams();
    httpParams = httpParams.append('id', auctionId.toString());
    return this.http.get<Image[]>(this.ATTACHMENT, {params: httpParams});
  }

  getUserPhoto() {
    return this.http.get<string[]>(this.USER);
  }

  saveUserPhoto(image: FormData) {
    return this.http.post<string[]>(this.USER, image);
  }

  getUserPhotoById(userId: number) {
    return this.http.get<string[]>(this.USER + '/' + userId);
  }

  updateAttachment(formData: FormData) {
    return this.http.put<Attachment>(this.ATTACHMENT, formData);
  }
}
