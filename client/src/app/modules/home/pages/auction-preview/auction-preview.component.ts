import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {Auction} from '../../../shared/models/auction';
import {AuctionService} from '../../../shared/services/auction.service';
import {AttachmentService} from '../../../shared/services/attachment.service';
import {AuctionDetails} from '../../../shared/models/auction-details';
import {AuthService} from '../../../core/security/auth.service';
import {TranslateService} from '@ngx-translate/core';
import {Image} from '../../../shared/models/image';
import {MatDialog} from '@angular/material/dialog';
import {ReportAuctionComponent} from '../../components/report-auction/report-auction.component';
import {DialogService} from '../../../shared/services/dialog.service';
import {DialogKey} from '../../../shared/config/enums';

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
              private authService: AuthService, private translate: TranslateService, private route: Router, public dialog: MatDialog,
              private dialogService: DialogService) {
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
      });
  }

  private loadAuctionPhotos() {
    this.attachmentService.getAuctionPhotosById(this.auctionId).subscribe(
      res => {
        if (res) {
          this.loadImages(res);
        }
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

  private loadUserPhoto() {
    this.attachmentService.getUserPhotoById(this.auction.userId).subscribe(res => {
      this.userPhoto = res[0];
    });
  }

  routeToUserClassFields() {
    this.route.navigate(['auction-user'],
      {queryParams: {id: this.auction.userId, name: this.auction.userFirstName}});
  }

  private loadImages(res: Image[]) {
    this.selected = res.filter(it => it.mainPhoto === true)[0];
    this.images = res.filter(it => it.mainPhoto === false);
  }

  addToFavorite() {
    if (this.authService.isLogged()) {
      this.auctionService.addToFavorite(this.auctionId).subscribe(it => {
        this.dialogService.openInfoDialog(DialogKey.AFTER_FAVORITE, false, null);
      }, error => {
        this.dialogService.openWarningDialog(DialogKey.AFTER_FAVORITE_ERROR, false, null);
      });
    } else {
      this.route.navigate(['/login'], {queryParams: {returnUrl: this.route.url}});
    }
  }

  openReportDialog() {
    this.dialog.open(ReportAuctionComponent,
      {
        width: '750px',
        height: '530px',
        data: {
          auctionId: this.auctionId
        }
      });
  }

  incrementCall(incrementToStatistic: boolean) {
    this.call = !this.call;
    if (incrementToStatistic) {
      this.auctionService.incrementPhoneClick(this.auctionId).subscribe();
    }
  }
}
