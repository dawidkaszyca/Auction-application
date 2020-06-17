import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {Auction} from '../../../../shared/models/auction';
import {AuctionService} from '../../../../shared/services/auction.service';
import {AttachmentService} from '../../../../shared/services/attachment.service';
import {AuctionDetails} from '../../../../shared/models/auction-details';
import {SendMessageService} from '../../../../shared/services/send-message.service';
import {AuthService} from '../../../../core/security/auth.service';
import {InfoDialogService} from '../../../../shared/services/info-dialog.service';
import {TranslateService} from '@ngx-translate/core';

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
  photosUrl: Map<string, string>;
  selected = 1;
  call: any;
  userPhoto: any;

  constructor(private router: ActivatedRoute, private auctionService: AuctionService, private attachmentService: AttachmentService,
              private sendMessageService: SendMessageService, private authService: AuthService,
              private infoDialogService: InfoDialogService, private translate: TranslateService) {
    this.call = false;
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
        window.scroll(0, 0);
        this.loadUserPhoto();
      },
      err => {
        alert('TODO');
      });
  }

  private loadAuctionPhotos() {
    this.attachmentService.getAuctionPhotosById(this.auctionId).subscribe(
      res => {
        this.photosUrl = new Map(Object.entries(res));
      },
      err => {
        alert('TODO');
      });
  }

  select(id: number) {
    this.selected = id;
  }

  sendMessage() {
    if (this.authService.isLogged()) {
      this.sendMessageService.openConfirmationDialog(this.auction.userId);
    } else {
      const message = this.translate.instant('dialog.login');
      this.infoDialogService.openConfirmationDialog(message);
    }
  }

  private loadUserPhoto() {
    this.attachmentService.getUserPhotoById(this.auction.userId).subscribe(res => {
      this.userPhoto = res[0];
    });
  }

  routeToUserClassFields() {

  }
}
