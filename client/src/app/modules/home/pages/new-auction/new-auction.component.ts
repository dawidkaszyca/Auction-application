import {Component, OnChanges, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {NavigationService} from '../../../../core/navigation/navigation.service';
import {NewAuction} from '../../../../shared/models/new-auction';
import {AuctionService} from '../../../../shared/services/auction.service';
import {CategoryAttributes} from '../../../../shared/models/category-attributes';
import {Attachment} from '../../../../shared/models/attachment';
import {AttachmentService} from '../../../../shared/services/attachment.service';
import {ApiService} from '../../../../core/authentication/api.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-new-auction',
  templateUrl: './new-auction.component.html',
  styleUrls: ['./new-auction.component.scss']
})
export class NewAuctionComponent implements OnInit, OnDestroy {

  files: File[];
  public maxSizeOfImages: number;
  previewUrl: any[];
  selected: number;
  auction: NewAuction;
  categories: string[];
  countryCode: any;
  selectedValues: CategoryAttributes[];
  show = false;
  conditions: string[];
  name: string;

  constructor(private navigationService: NavigationService, private auctionService: AuctionService,
              private attachmentService: AttachmentService, private router: Router) {
    navigationService.show = false;
    this.maxSizeOfImages = 4;
  }

  ngOnInit(): void {
    this.previewUrl = [];
    this.files = [];
    this.selected = this.maxSizeOfImages;
    this.auction = new NewAuction();
    this.getCategories();
  }

  ngOnDestroy(): void {
    this.navigationService.show = true;
  }

  private getCategories() {
    this.auctionService.getAuctionForm().subscribe(
      res => {
        this.categories = res.category;
        this.conditions = res.condition;
        this.name = res.name[0];
        console.log(this.categories);
      },
      err => {
        alert('TODO');
      });
  }

  receiveImages($event) {
    this.files.push($event[0]);
    this.preview($event[0]);
  }

  private preview($event): void {
    this.previewUrl = [];
    for (const file of this.files) {
      const reader = new FileReader();
      reader.readAsDataURL(file);
      reader.onload = () => {
        this.previewUrl.push(reader.result);
        if (this.files.length === 1) {
          this.selectMainPhoto(0);
        }
      };
    }
  }

  selectMainPhoto(photoNumber: number) {
    if (this.previewUrl[photoNumber] != null) {
      this.selected = photoNumber;
    }
  }

  receiveSelectedData($event: CategoryAttributes[]) {
    this.selectedValues = $event;
  }

  saveAuction() {
    const attachment = new Attachment();
    attachment.isUserImagePhoto = false;
    attachment.mainPhotoId = this.selected;
    this.auction.attributes = this.selectedValues;
    this.auctionService.saveAuction(this.auction).subscribe(
      res => {
        attachment.auctionId = Number(res);
        this.saveAttachment(attachment);
        this.router.navigateByUrl("/");
      },
      err => {
        alert('TODO');
      });
  }

  saveAttachment(attachment: Attachment) {
    const formData = new FormData();
    this.files.forEach((file) => {
      formData.append('files', file);
    });
    formData.append('data', JSON.stringify(attachment));
    this.attachmentService.saveAttachment(formData).subscribe(
      res => {
        console.log('Zdjecia zapisano');
      },
      err => {
        alert('TODO');
      });
  }
}
