import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {Auction} from '../../../../shared/models/auction';
import {AuctionService} from '../../../../shared/services/auction.service';
import {AttachmentService} from '../../../../shared/services/attachment.service';
import {AuctionDetails} from '../../../../shared/models/auction-details';
import {SendMessageService} from '../../../../shared/services/send-message.service';
import {AuthService} from '../../../../core/security/auth.service';
import {InfoDialogService} from '../../../../shared/services/info-dialog.service';
import {TranslateService} from '@ngx-translate/core';
import {Image} from '../../../../shared/models/image';

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
  images: Image[];
  selected: Image;
  call: any;
  userPhoto: any;

  constructor(private router: ActivatedRoute, private auctionService: AuctionService, private attachmentService: AttachmentService,
              private sendMessageService: SendMessageService, private authService: AuthService, private infoDialogService: InfoDialogService,
              private translate: TranslateService, private route: Router) {
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
        this.loadImages(res);
      },
      err => {
        alert('TODO');
      });
  }

  select(image: Image) {
    const url = image.url;
    this.images.forEach(it => {
      if (it.photoId === image.photoId) {
        it.url = this.selected.url;
      }
    });
    this.selected.url = url;
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
    this.route.navigate(['auction-user'],
      {queryParams: {'id': this.auction.userId, 'name': this.auction.userFirstName}});
  }

  private loadImages(res: Image[]) {
    this.selected = res.filter(it => it.mainPhoto === true)[0];
    this.images = res.filter(it => it.mainPhoto === false);
  }
}
