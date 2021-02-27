import {Component, OnDestroy, OnInit} from '@angular/core';
import {NavigationService} from '../../../../core/header/navigation/navigation.service';
import {User} from '../../../../shared/models/user';
import {AuthService} from '../../../../core/security/auth.service';
import {AttachmentService} from '../../../../shared/services/attachment.service';
import {MatDialog} from '@angular/material/dialog';
import {StatisticDialogComponent} from '../../../../shared/components/statistic-dialog/statistic-dialog.component';
import {ChangePassword} from '../../../../shared/models/change-password';
import {ChangePasswordComponent} from '../../../components/change-password/change-password.component';

@Component({
  selector: 'app-my-account',
  templateUrl: './my-account.component.html',
  styleUrls: ['./my-account.component.scss']
})
export class MyAccountComponent implements OnInit, OnDestroy {

  user = new User();
  isEditAble = false;
  userPhotoUrl: any;
  image: File;
  isPhotoEditAble = false;
  afterPhotoEdit = false;

  constructor(private navigationService: NavigationService, private authService: AuthService, private attachmentService: AttachmentService,
              public dialog: MatDialog) {
  }

  ngOnInit(): void {
    this.userPhotoUrl = null;
    this.navigationService.show = false;
    this.getUserData();
    this.getUserPhoto();
  }

  private getUserData() {
    this.authService.getUserData().subscribe(res => {
      this.user = res;
    });
  }

  ngOnDestroy(): void {
    this.navigationService.show = true;
  }

  changeUserData(): void {
    this.authService.updateUserData(this.user).subscribe(res => {
    });
    this.isEditAble = false;
  }

  private getUserPhoto() {
    this.attachmentService.getUserPhoto().subscribe(res => {
      this.userPhotoUrl = res[0];
    });
  }

  receiveImages($event: File[]) {
    this.image = $event[0];
    this.preview($event[0]);
    this.isPhotoEditAble = false;
    this.afterPhotoEdit = true;
  }

  private preview(file: File): void {
    const reader = new FileReader();
    reader.readAsDataURL(file);
    reader.onload = () => {
      this.userPhotoUrl = reader.result;
    };
  }

  saveUserPhoto() {
    const formData = new FormData();
    formData.append('files', this.image);
    this.attachmentService.saveUserPhoto(formData).subscribe(res => {
      this.isPhotoEditAble = false;
      this.afterPhotoEdit = false;
    });
  }

  changePassword() {
    this.dialog.open(ChangePasswordComponent, {});
  }
}
