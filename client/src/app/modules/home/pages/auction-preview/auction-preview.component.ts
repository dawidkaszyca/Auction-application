import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Route, Router} from '@angular/router';
import {Auction} from '../../../../shared/models/auction';
import {AuctionService} from '../../../../shared/services/auction.service';
import {AttachmentService} from '../../../../shared/services/attachment.service';

@Component({
  selector: 'app-auction-preview',
  templateUrl: './auction-preview.component.html',
  styleUrls: ['./auction-preview.component.scss']
})
export class AuctionPreviewComponent implements OnInit {
  auctionId: number;
  category: string;
  auction: Auction;
  photosUrl: string[];

  constructor(private router: ActivatedRoute, private auctionService: AuctionService, private attachmentService: AttachmentService) {
  }

  ngOnInit(): void {
    this.auctionId = this.router.snapshot.queryParams.id;
    this.category = this.router.snapshot.queryParams.category;
    this.loadAuction();
    this.loadAuctionPhotos();
  }

  private loadAuction() {
    this.auctionService.getAuctionWithDetailsById(this.auctionId).subscribe(
      res => {
        this.auction = res;
      },
      err => {
        alert('TODO');
      });
  }

  private loadAuctionPhotos() {
    this.attachmentService.getAuctionPhotosById(this.auctionId).subscribe(
      res => {
        this.photosUrl = res;
      },
      err => {
        alert('TODO');
      });
  }
}
