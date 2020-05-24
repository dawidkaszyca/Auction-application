import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {MainPageComponent} from './pages/main-page/main-page.component';
import {RouterModule, Routes} from '@angular/router';
import {BrowserModule} from '@angular/platform-browser';
import {AppRoutingModule} from '../../app-routing.module';
import {HttpClient, HttpClientModule} from '@angular/common/http';
import {TranslateLoader, TranslateModule} from '@ngx-translate/core';
import {HttpLoaderFactory} from '../../app.module';
import {AuctionListComponent} from './components/auction-list/auction-list.component';
import {MatCardModule} from '@angular/material/card';
import {SharedModule} from '../../shared/shared.module';
import {NewAuctionComponent} from './pages/new-auction/new-auction.component';
import {MatFormFieldModule} from '@angular/material/form-field';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {DynamicPanelComponent} from './components/dynamic-panel/dynamic-panel.component';
import {MatOptionModule} from '@angular/material/core';
import {MatSelectModule} from '@angular/material/select';
import {FilterBarComponent} from './components/filter-bar/filter-bar.component';
import {AuctionPreviewComponent} from './pages/auction-preview/auction-preview.component';
import {TextareaAutosizeModule} from 'ngx-textarea-autosize';
import {CanActivateRouteGuard} from '../../core/security/can-activate-route-guard';

const appRoutes: Routes = [
  {
    path: '',
    component: MainPageComponent,
  },
  {
    path: 'new-auction',
    component: NewAuctionComponent,
    canActivate: [CanActivateRouteGuard]
  },
  {
    path: 'auction',
    component: AuctionPreviewComponent,
  },
];

@NgModule({
  declarations: [MainPageComponent, AuctionListComponent, NewAuctionComponent, DynamicPanelComponent, FilterBarComponent, AuctionPreviewComponent],
  imports: [
    CommonModule,
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useFactory: HttpLoaderFactory,
        deps: [HttpClient]
      }
    }),
    RouterModule.forRoot(appRoutes, {enableTracing: true}),
    MatCardModule,
    SharedModule,
    MatFormFieldModule,
    FormsModule,
    ReactiveFormsModule,
    MatOptionModule,
    MatSelectModule,
    TextareaAutosizeModule
  ]
})
export class HomeModule {
}
