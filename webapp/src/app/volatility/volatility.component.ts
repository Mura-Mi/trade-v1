import {Component, OnInit, ViewChild} from '@angular/core';
import {VolatilityService} from "./volatility.service";
import {ChartDataSets} from 'chart.js';
import {BaseChartDirective, Label} from 'ng2-charts';
import {FormBuilder} from "@angular/forms";
import {Observable} from "rxjs";

@Component({
  selector: 'app-volatility',
  templateUrl: './volatility.component.html',
  styleUrls: ['./volatility.component.sass']
})
export class VolatilityComponent implements OnInit {

  constructor(private volatilityService: VolatilityService, private formBuilder: FormBuilder) {
    this.dateRangeForm = this.formBuilder.group({ from: '2019-01-01', to: '2019-12-31' })
  }

  labels: Label[];
  vol: ChartDataSets[];
  dateRangeForm;

  @ViewChild(BaseChartDirective, {static: true}) chart: BaseChartDirective;

  ngOnInit() {
    this.refresh()
  }

  refresh() {
    this.volatilityService.getHistoricalVolatility(this.dateRangeForm.value.from, this.dateRangeForm.value.to)
      .subscribe(vols => {
          this.vol = [{data: vols.map(d => d.vol), label: 'hoge'}];
          this.labels = vols.map(d => d.date);
          this.chart && this.chart.update();
        }
      )
  }

  onSubmit(v) {
    console.warn(v);
    this.refresh();
  }
}
