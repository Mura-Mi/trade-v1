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
    this.dateRangeForm = this.formBuilder.group({
      from: '',
      to: ''
    })
  }

  labels: Label[];
  vol: ChartDataSets[];
  dateRangeForm;
  from: Observable<string>;
  to: Observable<string>;

  @ViewChild(BaseChartDirective, {static: true}) chart: BaseChartDirective;

  ngOnInit() {
    this.from.pipe(a =>
      a.subscribe(aa => this.volatilityService.getHistoricalVolatility(aa.from, '2019-09-30'))
    ).subscribe(vols => {
        this.vol = [{data: vols.map(d => d.vol), label: 'hoge'}];
        this.labels = vols.map(d => d.date);
        this.chart && this.chart.update();
      }
    )
  }

  onSubmit(v) {
    console.warn(v)
  }

}
