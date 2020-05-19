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



@NgModule({
  declarations: [
    DragDropComponent,
    MaterialElevationDirective,
    DragDropDirective,
    ImageComponent,
    ConfirmDialogComponent
  ],
  imports: [
    CommonModule,
    MatIconModule,
    MatProgressBarModule,
    TranslateModule,
    MatCardModule
  ],
  providers: [],
  exports: [
    MaterialElevationDirective,
    DragDropComponent,
    ImageComponent
  ],
  bootstrap: [AppComponent]
})
export class SharedModule { }
