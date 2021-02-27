import {Component, OnDestroy, OnInit} from '@angular/core';
import {NavigationService} from '../../../../core/header/navigation/navigation.service';
import {StatisticService} from '../../../../shared/services/statistic.service';
import {Graph} from '../../../../shared/models/graph';
import {TranslateService} from '@ngx-translate/core';
import * as CanvasJS from 'src/assets/canvasjs.min';

@Component({
  selector: 'app-admin-statistic',
  templateUrl: './admin-statistic.component.html',
  styleUrls: ['./admin-statistic.component.scss']
})
export class AdminStatisticComponent implements OnInit, OnDestroy {

  week: number;
  statisticData: Statistic[];
  selectedStatistic: Statistic;
  graphData: Graph[];
  disabled: boolean;

  constructor(private navigationService: NavigationService, private statisticService: StatisticService, private translate: TranslateService) {
    this.graphData = [];
    this.disabled = false;
  }

  ngOnInit(): void {
    this.week = 0;
    this.navigationService.show = false;
    this.getStatisticData();
  }

  ngOnDestroy(): void {
    this.navigationService.show = true;
  }

  private getStatisticData() {
    this.statisticData = [];
    this.statisticService.getAdminData().subscribe(response => {
      const keys = Object.keys(response);
      keys.forEach(it => {
        const stat = new Statistic();
        stat.value = response[it];
        stat.serverKey = it;
        stat.translateKey = this.convertKey(it);
        this.statisticData.push(stat);
      });
      this.selectedStatistic = this.statisticData[0];
      this.loadGraphDataByKey();
    });
  }

  private convertKey(it: string) {
    let value = it.split('_').join('-');
    value = value.toLocaleLowerCase();
    value = 'admin.statistic.' + value;
    return value;
  }

  selectStatistic(stat: Statistic) {
    if (stat.serverKey !== 'CURRENT_LOGIN_USERS' && stat.serverKey !== 'TOTAL_ACTIVE_REPORTS') {
      this.week = 0;
      this.selectedStatistic = stat;
      this.loadGraphDataByKey();
    }
  }

  private loadGraphDataByKey() {
    this.statisticService.getStatisticByKey(this.selectedStatistic.serverKey, this.week).subscribe(it => {
      this.disabled = it?.length < 7;
      this.graphData = [];
      this.convertToData(it);
      this.renderChart(this.graphData, 'chartContainer1', this.translate.instant(this.selectedStatistic.translateKey));
    });
  }

  private convertToData(data: any[]) {
    if (data) {
      data.forEach(it => {
        const graph = new Graph();
        graph.y = it.value;
        graph.label = it.date;
        this.graphData.push(graph);
      });
    }
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

  newWeek(value: number) {
    this.week += value;
    this.loadGraphDataByKey();
  }
}


export class Statistic {
  translateKey: string;
  value: number;
  serverKey: string;
}
