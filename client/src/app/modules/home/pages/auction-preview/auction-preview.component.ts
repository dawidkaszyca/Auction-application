import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Route, Router} from '@angular/router';
import {Auction} from '../../../../shared/models/auction';
import {AuctionService} from '../../../../shared/services/auction.service';
import {AttachmentService} from '../../../../shared/services/attachment.service';
import {AuctionDetails} from '../../../../shared/models/auction-details';

@Component({
    selector: 'app-auction-preview',
    templateUrl: './auction-preview.component.html',
    styleUrls: ['./auction-preview.component.scss']
})
export class AuctionPreviewComponent implements OnInit {
    auctionId: number;
    auctionDetails: AuctionDetails[];
    category: string;
    auction: Auction;
    photosUrl: string[];
    selected = 1;

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
                this.auctionDetails = this.auction.auctionDetails;
                this.auction.description = 'Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry\'s standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.';
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

    select(id: number) {
        this.selected = id;
    }
}
