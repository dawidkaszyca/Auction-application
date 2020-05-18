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



@NgModule({
  declarations: [
    DragDropComponent,
    MaterialElevationDirective,
    DragDropDirective,
    ImageComponent
  ],
  imports: [
    CommonModule,
    MatIconModule,
    MatProgressBarModule,
    TranslateModule
  ],
  providers: [],
    exports: [
        MaterialElevationDirective,
        DragDropComponent
    ],
  bootstrap: [AppComponent]
})
export class SharedModule { }
