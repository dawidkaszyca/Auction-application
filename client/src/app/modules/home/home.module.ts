import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {MainPageComponent} from './pages/user/main-page/main-page.component';
import {BrowserModule} from '@angular/platform-browser';
import {HttpClientModule} from '@angular/common/http';
import {AuctionListComponent} from './components/auction-list/auction-list.component';
import {MatCardModule} from '@angular/material/card';
import {SharedModule} from '../shared/shared.module';
import {NewAuctionComponent} from './pages/user/new-auction/new-auction.component';
import {MatFormFieldModule} from '@angular/material/form-field';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {DynamicPanelComponent} from './components/dynamic-panel/dynamic-panel.component';
import {MatOptionModule} from '@angular/material/core';
import {MatSelectModule} from '@angular/material/select';
import {FilterBarComponent} from './components/filter-bar/filter-bar.component';
import {AuctionPreviewComponent} from './pages/user/auction-preview/auction-preview.component';
import {TextareaAutosizeModule} from 'ngx-textarea-autosize';
import {AuctionBaseComponent} from './components/auction-base/auction-base.component';
import {MyAccountComponent} from './pages/user/my-account/my-account.component';
import {FontAwesomeModule} from '@fortawesome/angular-fontawesome';
import {ReceivedMessageComponent} from './components/chat/received-message/received-message.component';
import {ContactComponent} from './components/chat/contact/contact.component';
import {SentMessageComponent} from './components/chat/sent-message/sent-message.component';
import {ChatComponent} from './pages/user/chat/chat.component';
import {GooglePlaceModule} from 'ngx-google-places-autocomplete';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {MyAuctionListComponent} from './components/my-auction-list/my-auction-list.component';
import {MatCheckboxModule} from '@angular/material/checkbox';
import {EditAuctionComponent} from './pages/user/edit-auction/edit-auction.component';
import {UserAuctionsComponent} from './pages/user/user-auctions/user-auctions.component';
import {PaginationBarComponent} from './components/pagination/pagination-bar/pagination-bar.component';
import {PaginationShortBarComponent} from './components/pagination/pagination-short-bar/pagination-short-bar.component';
import {FavoriteAuctionComponent} from './pages/user/favorite-auction/favorite-auction.component';
import {ReportAuctionComponent} from './components/report-auction/report-auction.component';
import {SendMessageComponent} from './components/send-message/send-message.component';
import {TranslateModule} from '@ngx-translate/core';
import {CoreModule} from '../core/core.module';
import {HomeRoutingModule} from './home-routing.module';
import { UserListComponent } from './pages/admin/user-list/user-list.component';
import {MatTableModule} from '@angular/material/table';
import {MatPaginatorModule} from '@angular/material/paginator';
import {MatSortModule} from '@angular/material/sort';
import { ReportedAuctionListComponent } from './pages/admin/reported-auction-list/reported-auction-list.component';
import { AdminStatisticComponent } from './pages/admin/admin-statistic/admin-statistic.component';
import { ChangePasswordComponent } from './components/change-password/change-password.component';

@NgModule({
  declarations: [
    MainPageComponent,
    AuctionListComponent,
    NewAuctionComponent,
    DynamicPanelComponent,
    FilterBarComponent,
    AuctionPreviewComponent,
    AuctionBaseComponent,
    MyAccountComponent,
    ChatComponent,
    ContactComponent,
    ReceivedMessageComponent,
    SentMessageComponent,
    MyAuctionListComponent,
    EditAuctionComponent,
    UserAuctionsComponent,
    PaginationBarComponent,
    PaginationShortBarComponent,
    FavoriteAuctionComponent,
    ReportAuctionComponent,
    SendMessageComponent,
    UserListComponent,
    ReportedAuctionListComponent,
    AdminStatisticComponent,
    ChangePasswordComponent
  ],
  imports: [
    CommonModule,
    HttpClientModule,
    TranslateModule,
    MatCardModule,
    SharedModule,
    MatFormFieldModule,
    FormsModule,
    ReactiveFormsModule,
    MatOptionModule,
    MatSelectModule,
    CoreModule,
    TextareaAutosizeModule,
    FontAwesomeModule,
    BrowserModule,
    HomeRoutingModule,
    FormsModule,
    GooglePlaceModule,
    MatProgressSpinnerModule,
    MatCheckboxModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
  ]
})
export class HomeModule {
}
