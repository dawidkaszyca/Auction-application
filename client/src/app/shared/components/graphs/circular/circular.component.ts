import {Component, Input, OnInit} from '@angular/core';
import * as CanvasJS from 'src/assets/canvasjs.min';
import {Graph} from '../../../models/graph';

@Component({
  selector: 'app-circular',
  templateUrl: './circular.component.html',
  styleUrls: ['./circular.component.scss']
})
export class CircularComponent implements OnInit {

  @Input()
  title: string;
  @Input()
  data: Graph[];

  ngOnInit() {
    const chart = new CanvasJS.Chart('chartContainer', {
      theme: 'light2',
      animationEnabled: true,
      exportEnabled: true,
      title: {
        text: this.title
      },
      data: [{
        type: 'pie',
        showInLegend: true,
        toolTipContent: '<b>{name}</b>: ${y} (#percent%)',
        indexLabel: '{name} - #percent%',
        dataPoints: this.data
      }]
    });

    chart.render();
  }
}
