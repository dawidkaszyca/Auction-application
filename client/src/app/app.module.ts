import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {TranslateLoader, TranslateModule} from '@ngx-translate/core';
import {TranslateHttpLoader} from '@ngx-translate/http-loader';
import {HttpClient, HttpClientModule} from '@angular/common/http';
import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {NavigationComponent} from './modules/core/header/navigation/navigation.component';
import {SearchComponent} from './modules/core/header/navigation/search/search.component';
import {CategoriesComponent} from './modules/core/header/navigation/categories/categories.component';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatSelectModule} from '@angular/material/select';
import {LoginComponent} from './modules/core/authentication/login/login.component';
import {ConfirmRegisterComponent} from './modules/core/authentication/confirm-register/confirm-register.component';
import {FormsModule} from '@angular/forms';
import {RegisterComponent} from './modules/core/authentication/register/register.component';
import {RouterModule, Routes} from '@angular/router';
import {MatMenuModule} from '@angular/material/menu';
import {MatIconModule} from '@angular/material/icon';
import {NgxWebstorageModule} from 'ngx-webstorage';
import {CoreModule} from './modules/core/core.module';
import {HomeModule} from './modules/home/home.module';
import {FontAwesomeModule} from '@fortawesome/angular-fontawesome';
import {GooglePlaceModule} from 'ngx-google-places-autocomplete';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {MatListModule} from '@angular/material/list';

export function HttpLoaderFactory(http: HttpClient) {
  return new TranslateHttpLoader(http);
}

@NgModule({
  declarations: [
    AppComponent,
    NavigationComponent,
    SearchComponent,
    CategoriesComponent,
    LoginComponent,
    RegisterComponent,
    ConfirmRegisterComponent
  ],
    imports: [
        BrowserModule,
        AppRoutingModule,
        HttpClientModule,
        NgxWebstorageModule.forRoot(),
        AppRoutingModule,
        TranslateModule.forRoot({
            loader: {
                provide: TranslateLoader,
                useFactory: HttpLoaderFactory,
                deps: [HttpClient]
            }
        }),
        BrowserAnimationsModule,
        MatFormFieldModule,
        MatSelectModule,
        FormsModule,
        MatIconModule,
        MatMenuModule,
        CoreModule,
        HomeModule,
        FontAwesomeModule,
        GooglePlaceModule,
        MatProgressSpinnerModule,
        MatListModule
    ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule {
}
