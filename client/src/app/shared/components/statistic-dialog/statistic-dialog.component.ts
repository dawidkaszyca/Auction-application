import {Component, Inject, OnInit} from '@angular/core';
import {faTimes} from '@fortawesome/free-solid-svg-icons/faTimes';
import {MAT_DIALOG_DATA} from '@angular/material/dialog';
import {StatisticService} from '../../services/statistic.service';
import {Graph} from '../../models/graph';
import * as CanvasJS from 'src/assets/canvasjs.min';

@Component({
  selector: 'app-statistic-dialog',
  templateUrl: './statistic-dialog.component.html',
  styleUrls: ['./statistic-dialog.component.scss']
})
export class StatisticDialogComponent implements OnInit {
  faRemove = faTimes;
  dailyViewsKey = 'DAILY_AUCTION_VIEWS_BY_ID';
  dailyViewData: Graph[];

  constructor(private statisticService: StatisticService, @Inject(MAT_DIALOG_DATA) public data: any) {
    this.dailyViewData = [];
    this.getData();
  }

  ngOnInit(): void {
  }

  private getData() {
    this.statisticService.getAuctionStatisticById(this.data.auctionData.id).subscribe(it => {
      this.convertToData(it[this.dailyViewsKey]);
    });
  }

  private convertToData(data: any[]) {
    data.forEach(it => {
      const graph = new Graph();
      graph.y = it.value;
      graph.label = it.date;
      this.dailyViewData.push(graph);
    });
    this.renderChart('chartContainer1', 'daily');
    this.renderChart('chartContainer2', 'Other');
  }

  private renderChart(containerName: string, title: string) {
    const chart = new CanvasJS.Chart(containerName, {
      animationEnabled: true,
      exportEnabled: false,
      title: {
        text: title
      },
      data: [{
        type: 'column',
        dataPoints: this.dailyViewData
      }]
    });

    chart.render();
  }
}
