import {Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {AuctionBaseField} from '../../../../shared/models/auction-base-field';
import {AuctionService} from '../../../../shared/services/auction.service';
import {AttachmentService} from '../../../../shared/services/attachment.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-auction-list',
  templateUrl: './auction-list.component.html',
  styleUrls: ['./auction-list.component.scss']
})
export class AuctionListComponent implements OnInit, OnChanges {

  @Input()
  auctions: AuctionBaseField[];

  constructor(private auctionService: AuctionService, private attachmentService: AttachmentService, private router: Router) {
  }

  ngOnInit(): void {
  }

  ngOnChanges(changes: SimpleChanges): void {
    this.loadPhotos();
  }

  private loadPhotos() {
    if(this.getAuctionIdList().length > 0) {
    this.attachmentService.getPhotos(this.getAuctionIdList()).subscribe(
      res => {
        const response = new Map(Object.entries(res));
        this.setImages(response);
      });
  }
  }

  private setImages(response: Map<string, string>) {
    this.auctions.forEach(it => {
      if (response.has(it.id.toString())) {
        it.photoUrl = response.get(it.id.toString());
      }
    });
  }

  private getAuctionIdList(): [] {
    const arrayId = [];
    this.auctions?.forEach(it => {
      arrayId.push(it.id);
    });
    return arrayId;
  }

  openAuctionPage(auction: AuctionBaseField) {
    this.router.navigate(['auction'], {queryParams: {'title': auction.title, 'category': auction.category, 'id': auction.id}});
  }
}
