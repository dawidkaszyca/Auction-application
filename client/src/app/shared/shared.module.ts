import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {AppComponent} from '../app.component';
import {DragDropComponent} from './components/drag-drop/drag-drop.component';
import {MatIconModule} from '@angular/material/icon';
import {MatProgressBarModule} from '@angular/material/progress-bar';
import { MaterialElevationDirective } from './directives/material-elevation.directive';
import {TranslateModule} from '@ngx-translate/core';
import { DragDropDirective } from './directives/drag-drop.directive';
import { ImageComponent } from './components/image/image.component';
import { ConfirmDialogComponent } from './components/confirm-dialog/confirm-dialog.component';
import {MatCardModule} from '@angular/material/card';
import { ScrollToBottomDirective } from './directives/scroll-to-bottom.directive';
import { MapPreviewComponent } from './components/map-preview/map-preview.component';
import { SplitCurrencyPipe } from './pipes/split-currency.pipe';
import { MessageDialogComponent } from './components/message-dialog/message-dialog.component';
import {FormsModule} from '@angular/forms';
import {FontAwesomeModule} from '@fortawesome/angular-fontawesome';
import { InfoDialogComponent } from './components/info-dialog/info-dialog.component';
import { ColumnComponent } from './components/graphs/column/column.component';
import { CircularComponent } from './components/graphs/circular/circular.component';



@NgModule({
    declarations: [
        DragDropComponent,
        MaterialElevationDirective,
        DragDropDirective,
        ImageComponent,
        ConfirmDialogComponent,
        ScrollToBottomDirective,
        MapPreviewComponent,
        SplitCurrencyPipe,
        MessageDialogComponent,
        InfoDialogComponent,
        ColumnComponent,
        CircularComponent
    ],
  imports: [
    CommonModule,
    MatIconModule,
    MatProgressBarModule,
    TranslateModule,
    MatCardModule,
    FormsModule,
    FontAwesomeModule
  ],
  providers: [],
  exports: [
    MaterialElevationDirective,
    DragDropComponent,
    ImageComponent,
    MapPreviewComponent,
    SplitCurrencyPipe,
    CircularComponent
  ],
  bootstrap: [AppComponent]
})
export class SharedModule { }
