import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {AppComponent} from '../../app.component';
import {DragDropComponent} from './components/drag-drop/drag-drop.component';
import {MatIconModule} from '@angular/material/icon';
import {MatProgressBarModule} from '@angular/material/progress-bar';
import { MaterialElevationDirective } from './directives/material-elevation.directive';
import {TranslateModule} from '@ngx-translate/core';
import { DragDropDirective } from './directives/drag-drop.directive';
import { ImageComponent } from './components/image/image.component';
import {MatCardModule} from '@angular/material/card';
import { ScrollToBottomDirective } from './directives/scroll-to-bottom.directive';
import { MapPreviewComponent } from './components/map-preview/map-preview.component';
import { SplitCurrencyPipe } from './pipes/split-currency.pipe';
import {FormsModule} from '@angular/forms';
import {FontAwesomeModule} from '@fortawesome/angular-fontawesome';
import {MatDialog, MatDialogModule} from '@angular/material/dialog';
import {StatisticDialogComponent} from './components/statistic-dialog/statistic-dialog.component';



@NgModule({
    declarations: [
        DragDropComponent,
        MaterialElevationDirective,
        DragDropDirective,
        ImageComponent,
        ScrollToBottomDirective,
        MapPreviewComponent,
        SplitCurrencyPipe,
        StatisticDialogComponent
    ],
  imports: [
    CommonModule,
    MatIconModule,
    MatProgressBarModule,
    TranslateModule,
    MatCardModule,
    FormsModule,
    FontAwesomeModule,
    MatDialogModule,
  ],
  providers: [],
  exports: [
    MaterialElevationDirective,
    DragDropComponent,
    ImageComponent,
    MapPreviewComponent,
    SplitCurrencyPipe,
  ],
  bootstrap: [AppComponent]
})
export class SharedModule { }
