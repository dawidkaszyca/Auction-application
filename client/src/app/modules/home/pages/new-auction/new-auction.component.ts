import {Component, OnDestroy, OnInit} from '@angular/core';
import {NavigationService} from '../../../core/header/navigation/navigation.service';
import {NewAuction} from '../../../shared/models/new-auction';
import {AuctionService} from '../../../shared/services/auction.service';
import {CategoryAttributes} from '../../../shared/models/category-attributes';
import {Attachment} from '../../../shared/models/attachment';
import {AttachmentService} from '../../../shared/services/attachment.service';
import {Router} from '@angular/router';
import {City} from '../../../shared/models/auction-base-field';
import {faTimes} from '@fortawesome/free-solid-svg-icons/faTimes';
import {timer} from 'rxjs';
import {DialogService} from '../../../shared/services/dialog.service';
import {DialogKey} from '../../../shared/config/enums';


@Component({
  selector: 'app-new-auction',
  templateUrl: './new-auction.component.html',
  styleUrls: ['./new-auction.component.scss']
})
export class NewAuctionComponent implements OnInit, OnDestroy {

  selectedValues: CategoryAttributes[];
  files: File[];
  public maxSizeOfImages: number;
  previewUrl: any[];
  selected: number;
  auction: NewAuction;
  categories: string[];
  conditions: string[];
  name: string;
  city: City;
  cityName: any;
  email: string;
  isAfterRemoved: boolean;
  options = {
    types: ['(regions)'],
    componentRestrictions: {
      country: ['PL']
    }
  };
  faRemove = faTimes;
  isSaving: any;


  public handleAddressChange(address: any) {
    this.city = new City();
    this.city.name = address.name;
    this.city.longitude = address.geometry.location.lng();
    this.city.latitude = address.geometry.location.lat();
    this.auction.city = this.city;
    this.cityName = address.name;
  }

  constructor(private navigationService: NavigationService, private auctionService: AuctionService,
              private attachmentService: AttachmentService, private router: Router, private dialogService: DialogService) {
    navigationService.show = false;
    this.maxSizeOfImages = 4;
    this.isSaving = false;
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
        this.email = res.email[0];
        console.log(this.categories);
      },
      err => {
      });
  }

  receiveImages($event: File[]) {
    $event.forEach(it => {
      if (this.files.length < this.maxSizeOfImages) {
        this.files.push(it);
        this.preview(it);
      }
    });
  }

  private preview(file: File): void {
    const reader = new FileReader();
    reader.readAsDataURL(file);
    reader.onload = () => {
      this.previewUrl.push(reader.result);
      if (this.previewUrl.length === 1) {
        this.selectMainPhoto(0);
      }
    };
  }

  selectMainPhoto(photoNumber: number) {
    if (this.previewUrl[photoNumber] != null && !this.isAfterRemoved) {
      this.selected = photoNumber;
    }
    this.isAfterRemoved = false;
  }

  receiveSelectedData($event: CategoryAttributes[]) {
    this.selectedValues = $event;
  }

  saveAuction() {
    this.isSaving = true;
    const attachment = new Attachment();
    attachment.isUserImagePhoto = false;
    attachment.mainPhotoId = this.selected;
    this.auction.attributes = this.getNotEmptyValues();
    this.auctionService.saveAuction(this.auction).subscribe(
      res => {
        attachment.auctionId = Number(res);
        this.saveAttachment(attachment);
      },
      err => {
        this.isSaving = false;
        this.dialogService.openWarningDialog(DialogKey.SAVE_AUCTION_ERROR, false, null);
      });
  }

  private getNotEmptyValues() {
    return this.selectedValues.filter(it => it.attributeValues[0].value !== '');
  }

  saveAttachment(attachment: Attachment) {
    const formData = new FormData();
    this.files.forEach((file) => {
      formData.append('files', file);
    });
    formData.append('data', JSON.stringify(attachment));
    this.attachmentService.saveAttachment(formData).subscribe(
      res => {
        timer(1500).subscribe(x => {
          this.isSaving = false;
          this.openAuctionPage(attachment.auctionId);
        });
      }, error => {
        this.dialogService.openWarningDialog(DialogKey.SAVE_ATTACHMENT_ERROR, false, null);
      });
  }

  openAuctionPage(id: number) {
    this.router.navigate(['auction'], {queryParams: {title: this.auction.title, category: this.auction.category, id: id}});
  }

  checkName(): boolean {
    return this.cityName === this.city?.name;
  }

  removeImage(index: number) {
    this.files.splice(index, 1);
    this.previewUrl.splice(index, 1);
    if (this.files.length === 0) {
      this.selected = this.maxSizeOfImages;
    } else if (index === this.selected) {
      this.selected = 0;
    } else if (index < this.selected) {
      this.selected--;
    }
    this.isAfterRemoved = true;
  }
}
