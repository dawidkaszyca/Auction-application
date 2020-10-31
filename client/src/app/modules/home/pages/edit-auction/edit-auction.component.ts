import {Component, Inject, OnDestroy, OnInit} from '@angular/core';
import {CategoryAttributes} from '../../../shared/models/category-attributes';
import {NewAuction} from '../../../shared/models/new-auction';
import {City} from '../../../shared/models/auction-base-field';
import {faTimes} from '@fortawesome/free-solid-svg-icons/faTimes';
import {NavigationService} from '../../../core/header/navigation/navigation.service';
import {AuctionService} from '../../../shared/services/auction.service';
import {AttachmentService} from '../../../shared/services/attachment.service';
import {ActivatedRoute, Router} from '@angular/router';
import {AttachmentToUpdate} from '../../../shared/models/attachment';
import {timer} from 'rxjs';
import {AuctionDetails} from '../../../shared/models/auction-details';
import {AttributesValues} from '../../../shared/models/attributes-values';
import {Auction} from '../../../shared/models/auction';
import {Image} from '../../../shared/models/image';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {DialogService} from '../../../shared/services/dialog.service';
import {DialogKey} from '../../../shared/config/enums';

@Component({
  selector: 'app-edit-auction',
  templateUrl: './edit-auction.component.html',
  styleUrls: ['./edit-auction.component.scss']
})
export class EditAuctionComponent implements OnInit, OnDestroy {

  auctionId: number;

  selectedValues: CategoryAttributes[];
  files: File[];
  public maxSizeOfImages: number;
  previewUrl: string[];
  selected: number;
  auction: NewAuction;
  categories: string[];
  conditions: string[];
  attachmentToUpdate: AttachmentToUpdate;
  images: Image[];
  name: string;
  city: City;
  cityName: any;
  email: string;
  isAfterRemoved: boolean;
  firstSelectedId: number;
  options = {
    types: ['(regions)'],
    componentRestrictions: {
      country: ['PL']
    }
  };
  faRemove = faTimes;
  isSaving: boolean;

  constructor(private navigationService: NavigationService, private auctionService: AuctionService,
              @Inject(MAT_DIALOG_DATA) public data: any, private attachmentService: AttachmentService,
              private activatedRoute: ActivatedRoute, private router: Router, private dialogRef: MatDialogRef<EditAuctionComponent>,
              private dialogService: DialogService) {
    navigationService.show = false;
    this.maxSizeOfImages = 4;
    this.isSaving = false;
  }

  ngOnInit(): void {
    this.auctionId = this.data.auctionData.id;
    this.previewUrl = [];
    this.files = [];
    this.selected = this.maxSizeOfImages;
    this.auction = new NewAuction();
    this.attachmentToUpdate = new AttachmentToUpdate();
    this.attachmentToUpdate.auctionId = this.auctionId;
    this.attachmentToUpdate.idsToRemoved = [];
    this.loadAuction();
    this.setCategories();
    this.loadAuctionPhotos();
  }

  ngOnDestroy(): void {
    this.navigationService.show = true;
  }

  handleAddressChange(address: any): void {
    this.city = new City();
    this.city.name = address.name;
    this.city.longitude = address.geometry.location.lng();
    this.city.latitude = address.geometry.location.lat();
    this.auction.city = this.city;
    this.cityName = address.name;
  }

  loadAuction() {
    this.auctionService.getAuctionWithDetailsToEditById(this.auctionId).subscribe(
      res => {
        this.setSelectedAttributes(res.auctionDetails);
        this.setData(res);
        window.scroll(0, 0);
      });
  }

  setSelectedAttributes(auctionDetails: AuctionDetails[]): void {
    this.selectedValues = [];
    auctionDetails.forEach(it => {
      const categoryAttributes = new CategoryAttributes();
      categoryAttributes.attributeValues = [];
      const attributesValues = new AttributesValues();
      attributesValues.value = it.attributeValue;
      categoryAttributes.attributeValues.push(attributesValues);
      categoryAttributes.attribute = it.categoryAttribute;
      categoryAttributes.isSingleSelect = true;
      this.selectedValues.push(categoryAttributes);
    });
  }

  setCategories(): void {
    this.auctionService.getAuctionForm().subscribe(
      res => {
        this.categories = res.category;
        this.conditions = res.condition;
        this.email = res.email[0];
        this.name = res.name[0];
      });
  }

  receiveImages($event: File[]): void {
    $event.forEach(it => {
      if (this.files.length < this.maxSizeOfImages) {
        this.files.push(it);
        this.preview(it);
      }
    });
  }

  preview(file: File): void {
    const reader = new FileReader();
    reader.readAsDataURL(file);
    reader.onload = () => {
      this.previewUrl.push(reader.result as string);
      if (this.previewUrl.length === 1) {
        this.selectMainPhoto(0);
      }
    };
  }

  selectMainPhoto(photoNumber: number): void {
    if (this.previewUrl[photoNumber] != null && !this.isAfterRemoved) {
      this.setIsImageAllReadySaved(photoNumber);
      this.selected = photoNumber;
    }
    this.isAfterRemoved = false;
  }

  receiveSelectedData($event: CategoryAttributes[]): void {
    this.selectedValues = $event;
  }

  loadAuctionPhotos(): void {
    this.attachmentService.getAuctionPhotosById(this.auctionId).subscribe(
      res => {
        if (!res) {
          this.images = [];
          return;
        }
        this.images = res;
        this.previewUrl = [];
        res.forEach(it => {
          this.previewUrl.push(it.url);
          if (it.mainPhoto === true) {
            this.selected = this.previewUrl.length - 1;
            this.firstSelectedId = it.photoId;
          }
        });
        this.maxSizeOfImages = this.maxSizeOfImages - this.previewUrl.length;
      });
  }

  updateAuction(): void {
    this.isSaving = true;
    this.auction.attributes = this.getNotEmptyValues();
    this.auctionService.updateAuction(this.auction).subscribe(
      res => {
        if (this.checkIfAttachmentsChanged()) {
          this.updateAttachments();
        } else {
          this.isSaving = false;
          this.closeDialog();
        }
      },
      err => {
        this.isSaving = false;
        this.dialogService.openWarningDialog(DialogKey.UPDATE_AUCTION_ERROR);
      });
  }

  private checkIfAttachmentsChanged(): boolean {
    return this.isNewAttachmentsOrToDelete() || this.onlyAuctionIdChanged();
  }

  isNewAttachmentsOrToDelete(): boolean {
    return this.attachmentToUpdate.idsToRemoved.length > 0 || this.files.length > 0;
  }

  onlyAuctionIdChanged(): boolean {
    return this.attachmentToUpdate.isMainPhotoAllReadySaved && this.firstSelectedId !== this.attachmentToUpdate.mainPhotoId;
  }

  getNotEmptyValues() {
    return this.selectedValues.filter(it => it.attributeValues[0].value !== '');
  }

  updateAttachments(): void {
    const formData = new FormData();
    this.files.forEach((file) => {
      formData.append('files', file);
    });
    formData.append('data', JSON.stringify(this.attachmentToUpdate));
    this.attachmentService.updateAttachment(formData).subscribe(
      res => {
        timer(1500).subscribe(x => {
          this.isSaving = false;
          this.closeDialog();
        });
      },
      err => {
        this.isSaving = false;
        this.dialogService.openWarningDialog(DialogKey.UPDATE_ATTACHMENT_ERROR);
      });
  }

  closeDialog(): void {
    this.dialogRef.close();
  }

  checkName(): boolean {
    return this.cityName === this.city?.name;
  }

  removeImage(index: number): void {
    const url = this.previewUrl[index];
    const image = this.images.filter(it => it.url === url)[0];
    this.images = this.images.filter(it => it !== image);
    if (image) {
      this.attachmentToUpdate.idsToRemoved.push(image.photoId);
    } else {
      this.files.splice(index, 1);
    }
    this.previewUrl.splice(index, 1);
    if (this.previewUrl.length === 0) {
      this.selected = this.maxSizeOfImages;
    } else if (index === this.selected) {
      this.selected = 0;
      this.setIsImageAllReadySaved(this.selected);
    } else if (index < this.selected) {
      this.selected--;
      this.setIsImageAllReadySaved(this.selected);
    }
    this.isAfterRemoved = true;
    this.maxSizeOfImages = 4 - this.previewUrl.length;
  }

  setIsImageAllReadySaved(index: number): void {
    const image = this.images.filter(it => it.url === this.previewUrl[index])[0];
    if (image) {
      this.attachmentToUpdate.isMainPhotoAllReadySaved = true;
      this.attachmentToUpdate.mainPhotoId = image.photoId;
    } else {
      this.attachmentToUpdate.isMainPhotoAllReadySaved = false;
      this.attachmentToUpdate.mainPhotoId = index;
    }
  }

  setData(res: Auction): void {
    this.auction.description = res.description;
    this.auction.phone = res.phone;
    this.auction.id = res.id;
    this.auction.price = res.price;
    this.city = res.city;
    this.cityName = this.city.name;
    this.auction.category = res.category;
    this.auction.city = res.city;
    this.auction.condition = res.condition;
    this.auction.title = res.title;
  }
}
