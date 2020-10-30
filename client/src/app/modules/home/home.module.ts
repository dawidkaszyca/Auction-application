import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {MainPageComponent} from './pages/main-page/main-page.component';
import {BrowserModule} from '@angular/platform-browser';
import {HttpClientModule} from '@angular/common/http';
import {AuctionListComponent} from './components/auction-list/auction-list.component';
import {MatCardModule} from '@angular/material/card';
import {SharedModule} from '../shared/shared.module';
import {NewAuctionComponent} from './pages/new-auction/new-auction.component';
import {MatFormFieldModule} from '@angular/material/form-field';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {DynamicPanelComponent} from './components/dynamic-panel/dynamic-panel.component';
import {MatOptionModule} from '@angular/material/core';
import {MatSelectModule} from '@angular/material/select';
import {FilterBarComponent} from './components/filter-bar/filter-bar.component';
import {AuctionPreviewComponent} from './pages/auction-preview/auction-preview.component';
import {TextareaAutosizeModule} from 'ngx-textarea-autosize';
import {AuctionBaseComponent} from './components/auction-base/auction-base.component';
import {MyAccountComponent} from './pages/my-account/my-account.component';
import {FontAwesomeModule} from '@fortawesome/angular-fontawesome';
import {ReceivedMessageComponent} from './components/chat/received-message/received-message.component';
import {ContactComponent} from './components/chat/contact/contact.component';
import {SentMessageComponent} from './components/chat/sent-message/sent-message.component';
import {ChatComponent} from './pages/chat/chat.component';
import {GooglePlaceModule} from 'ngx-google-places-autocomplete';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {MyAuctionListComponent} from './components/my-auction-list/my-auction-list.component';
import {MatCheckboxModule} from '@angular/material/checkbox';
import {EditAuctionComponent} from './pages/edit-auction/edit-auction.component';
import {UserAuctionsComponent} from './pages/user-auctions/user-auctions.component';
import {PaginationBarComponent} from './components/pagination/pagination-bar/pagination-bar.component';
import {PaginationShortBarComponent} from './components/pagination/pagination-short-bar/pagination-short-bar.component';
import {FavoriteAuctionComponent} from './pages/favorite-auction/favorite-auction.component';
import {ReportAuctionComponent} from './components/report-auction/report-auction.component';
import {SendMessageComponent} from './components/send-message/send-message.component';
import {TranslateModule} from '@ngx-translate/core';
import {CoreModule} from '../core/core.module';
import {HomeRoutingModule} from './home-routing.module';

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
    SendMessageComponent
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
    MatCheckboxModule
  ]
})
export class HomeModule {
}
