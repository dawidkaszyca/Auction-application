import {Component, Input, OnInit} from '@angular/core';
import * as CanvasJS from 'src/assets/canvasjs.min';
import {Graph} from '../../../models/graph';

@Component({
  selector: 'app-column',
  templateUrl: './column.component.html',
  styleUrls: ['./column.component.scss']
})
export class ColumnComponent implements OnInit {

  @Input()
  title: string;
  @Input()
  data: Graph[];

  ngOnInit() {
    const chart = new CanvasJS.Chart('chartContainer', {
      animationEnabled: true,
      exportEnabled: true,
      title: {
        text: this.title
      },
      data: [{
        type: 'column',
        dataPoints: this.data
      }]
    });

    chart.render();
  }
}
