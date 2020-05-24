import {Component, OnInit} from '@angular/core';
import {AuctionBaseField} from '../../../../shared/models/auction-base-field';
import {AuctionService} from '../../../../shared/services/auction.service';
import {AttachmentService} from '../../../../shared/services/attachment.service';
import {Router} from '@angular/router';
import {Filter} from '../../../../shared/models/filter';

@Component({
  selector: 'app-auction-list',
  templateUrl: './auction-list.component.html',
  styleUrls: ['./auction-list.component.scss']
})
export class AuctionListComponent implements OnInit {

  private category;
  private pageSize = 10;
  private page = 0;
  private filter: Filter;
  auctions: AuctionBaseField[];

  constructor(private auctionService: AuctionService, private attachmentService: AttachmentService, private router: Router) {
  }

  ngOnInit(): void {
    this.auctionService.filter.subscribe(data => {
      this.filter = data;
      this.category = data.category;
      this.loadAuctionsData();
    });
  }

  private loadAuctionsData() {
    this.auctionService.getAuctions(this.filter).subscribe(
      res => {
        this.auctions = res;
        this.loadPhotos();
      },
      err => {
        alert('TODO');
      });
  }

  private loadPhotos() {
    this.attachmentService.getPhotos(this.getAuctionIdList()).subscribe(
      res => {
        const response = new Map(Object.entries(res));
        this.setImages(response);
      });
  }

  private setImages(response: Map<string, string>) {
    this.auctions.forEach(it => {
      if (response.has(it.id.toString())) {
        it.photoUrl = response.get(it.id.toString());
      }
    });
  }

  private getAuctionIdList() {
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
