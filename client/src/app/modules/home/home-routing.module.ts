import {RouterModule, Routes} from '@angular/router';
import {NgModule} from '@angular/core';
import {MainPageComponent} from './pages/main-page/main-page.component';
import {NewAuctionComponent} from './pages/new-auction/new-auction.component';
import {CanActivateRouteGuard} from '../core/security/can-activate-route-guard';
import {AuctionPreviewComponent} from './pages/auction-preview/auction-preview.component';
import {MyAccountComponent} from './pages/my-account/my-account.component';
import {FavoriteAuctionComponent} from './pages/favorite-auction/favorite-auction.component';
import {ChatComponent} from './pages/chat/chat.component';
import {EditAuctionComponent} from './pages/edit-auction/edit-auction.component';
import {UserAuctionsComponent} from './pages/user-auctions/user-auctions.component';

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
