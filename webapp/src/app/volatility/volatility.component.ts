import {Component, OnInit, ViewChild} from '@angular/core';
import {VolatilityService} from "./volatility.service";
import {ChartDataSets} from 'chart.js';
import {BaseChartDirective, Label} from 'ng2-charts';

@Component({
  selector: 'app-volatility',
  templateUrl: './volatility.component.html',
  styleUrls: ['./volatility.component.sass']
})
export class VolatilityComponent implements OnInit {

  constructor(private volatilityService: VolatilityService) {
  }

  labels: Label[];
  vol: ChartDataSets[];
  @ViewChild(BaseChartDirective, {static: true}) chart: BaseChartDirective;

  ngOnInit() {
    this.volatilityService.getHistoricalVolatility()
      .subscribe(vols => {
          this.vol = [{data: vols.map(d => d.vol), label: 'hoge'}];
          this.labels = vols.map(d => d.date);
          this.chart && this.chart.update();
        }
      )
  }

}
