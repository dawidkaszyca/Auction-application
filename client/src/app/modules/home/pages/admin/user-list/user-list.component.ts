import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {MatTableDataSource} from '@angular/material/table';
import {SelectionModel} from '@angular/cdk/collections';
import {User} from '../../../../shared/models/user';
import {AuthService} from '../../../../core/security/auth.service';
import {MatSort} from '@angular/material/sort';
import {MatPaginator} from '@angular/material/paginator';
import {NavigationService} from '../../../../core/header/navigation/navigation.service';
import {DialogService} from '../../../../shared/services/dialog.service';

@Component({
  selector: 'app-user-list',
  templateUrl: './user-list.component.html',
  styleUrls: ['./user-list.component.scss']
})
export class UserListComponent implements OnInit, OnDestroy {

  users: User[];
  dataSource: MatTableDataSource<any>;
  displayedColumns: string[] = ['login', 'firstName', 'lastName', 'email', 'role'];
  selection: SelectionModel<any>;
  selectedRecord: User;

  @ViewChild(MatSort, {static: true}) sort: MatSort;
  @ViewChild(MatPaginator, {static: true}) paginator: MatPaginator;

  constructor(private authService: AuthService, private navigationService: NavigationService, private dialogService: DialogService) {
  }

  ngOnInit(): void {
    this.navigationService.show = false;
    this.getAllUsers();
  }

  ngOnDestroy(): void {
    this.navigationService.show = true;
  }

  selectRecord(row: any) {
    this.selectedRecord = row;
  }

  private getAllUsers() {
    this.authService.getAllUsers().subscribe(it => {
      this.users = it;
      this.dataSource = new MatTableDataSource<User>(this.users);
      this.dataSource.sort = this.sort;
      this.dataSource.paginator = this.paginator;
      this.selection = new SelectionModel<User>(false, [this.users[0]]);
      this.selectedRecord = this.users[0];
    });

  }

  grantAdminPermission() {
    function callback(this: any) {
      this.authService.grantPermission(this.selectedRecord?.id).subscribe(result => {
        this.getAllUsers();
      });
    }

    this.openConfirmationDialog(callback, 'dialog.confirm.grant-permission');
  }

  public openConfirmationDialog(callback0: any, text: string) {
    this.dialogService.openConfirmDialog(text)
      .then((confirmed) => {
        if (confirmed === true) {
          callback0.call(this);
        }
      });
  }


  removeUser() {
    function callback(this: any) {
      this.authService.removeUser(this.selectedRecord?.id).subscribe(result => {
        this.getAllUsers();
      });
    }
    this.openConfirmationDialog(callback, 'dialog.confirm.remove-user');
  }

  getRole(element: User): string {
    return element.authorities.filter(it => it.includes('ADMIN')).length > 0 ? 'ADMIN' : 'USER';
  }

  revokeAdminPermission() {
    function callback(this: any) {
      this.authService.revokePermission(this.selectedRecord?.id).subscribe(result => {
        this.getAllUsers();
      });
    }

    this.openConfirmationDialog(callback, 'dialog.confirm.revoke-permission');
  }
}

