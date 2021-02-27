import {Component, Inject, OnInit} from '@angular/core';
import {faTimes} from '@fortawesome/free-solid-svg-icons/faTimes';
import {MAT_DIALOG_DATA} from '@angular/material/dialog';
import {StatisticService} from '../../services/statistic.service';
import {Graph} from '../../models/graph';
import * as CanvasJS from 'src/assets/canvasjs.min';
import {TranslateService} from '@ngx-translate/core';

@Component({
  selector: 'app-statistic-dialog',
  templateUrl: './statistic-dialog.component.html',
  styleUrls: ['./statistic-dialog.component.scss']
})
export class StatisticDialogComponent implements OnInit {

  faRemove = faTimes;
  dailyViewsKey = 'DAILY_AUCTION_VIEWS_BY_ID';
  dailyPhoneClickKey = 'DAILY_AUCTION_PHONE_CLICKS_BY_ID';
  dailyViewData: Graph[];
  dailyPhoneClick: Graph[];

  constructor(private statisticService: StatisticService, @Inject(MAT_DIALOG_DATA) public data: any, private translate: TranslateService) {
    this.dailyViewData = [];
    this.dailyPhoneClick = [];
    this.getData();
  }

  ngOnInit(): void {
  }

  private getData() {
    this.statisticService.getAuctionStatisticById(this.data.auctionData.id).subscribe(it => {
      this.convertToData(it[this.dailyViewsKey], this.dailyViewData);
      this.convertToData(it[this.dailyPhoneClickKey], this.dailyPhoneClick);
      this.renderChart(this.dailyViewData, 'chartContainer1', this.translate.instant('dialog.daily-views'));
      this.renderChart(this.dailyPhoneClick, 'chartContainer2', this.translate.instant('dialog.daily-phone'));
    });
  }

  private convertToData(data: any[], list: Graph[]) {
    data.forEach(it => {
      const graph = new Graph();
      graph.y = it.value;
      graph.label = it.date;
      list.push(graph);
    });
  }

  private renderChart(data: any, containerName: string, title: string) {
    const chart = new CanvasJS.Chart(containerName, {
      animationEnabled: true,
      exportEnabled: false,
      title: {
        text: title
      },
      data: [{
        type: 'column',
        dataPoints: data
      }]
    });

    chart.render();
  }
}
