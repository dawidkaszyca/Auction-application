import {RouterModule, Routes} from '@angular/router';
import {NgModule} from '@angular/core';
import {MainPageComponent} from './pages/user/main-page/main-page.component';
import {NewAuctionComponent} from './pages/user/new-auction/new-auction.component';
import {CanActivateRouteGuard} from '../core/security/can-activate-route-guard';
import {AuctionPreviewComponent} from './pages/user/auction-preview/auction-preview.component';
import {MyAccountComponent} from './pages/user/my-account/my-account.component';
import {FavoriteAuctionComponent} from './pages/user/favorite-auction/favorite-auction.component';
import {ChatComponent} from './pages/user/chat/chat.component';
import {EditAuctionComponent} from './pages/user/edit-auction/edit-auction.component';
import {UserAuctionsComponent} from './pages/user/user-auctions/user-auctions.component';
import {UserListComponent} from './pages/admin/user-list/user-list.component';
import {AdminStatisticComponent} from './pages/admin/admin-statistic/admin-statistic.component';
import {ReportedAuctionListComponent} from './pages/admin/reported-auction-list/reported-auction-list.component';
import {CanActivateAdminGuardGuard} from '../core/security/can-activate-admin-guard.guard';

const routes: Routes = [
  {
    path: '',
    component: MainPageComponent,
  },
  {
    path: 'auction',
    component: AuctionPreviewComponent,
  },
  {
    path: 'auction-user',
    component: UserAuctionsComponent,
  },
  {
    path: 'new-auction',
    component: NewAuctionComponent,
    canActivate: [CanActivateRouteGuard]
  },
  {
    path: 'my-profile',
    component: MyAccountComponent,
    canActivate: [CanActivateRouteGuard]
  },
  {
    path: 'user-list',
    component: UserListComponent,
    canActivate: [CanActivateAdminGuardGuard]
  },
  {
    path: 'reported-auction-list',
    component: ReportedAuctionListComponent,
    canActivate: [CanActivateAdminGuardGuard]
  },
  {
    path: 'admin-statistic',
    component: AdminStatisticComponent,
    canActivate: [CanActivateAdminGuardGuard]
  },
  {
    path: 'favorite-auction',
    component: FavoriteAuctionComponent,
    canActivate: [CanActivateRouteGuard]
  },
  {
    path: 'messages',
    component: ChatComponent,
    canActivate: [CanActivateRouteGuard]
  },
  {
    path: 'edit-auction',
    component: EditAuctionComponent,
    canActivate: [CanActivateRouteGuard]
  },
  {
    path: '**',
    component: MainPageComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class HomeRoutingModule {

}
