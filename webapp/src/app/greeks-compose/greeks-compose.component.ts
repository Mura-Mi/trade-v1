import {Component, OnInit, ViewChild} from '@angular/core';
import {GreeksService} from "../greeks/greeks.service";
import {FormBuilder} from "@angular/forms";
import {ActivatedRoute} from "@angular/router";
import {ChartDataSets} from "chart.js";
import {BaseChartDirective} from "ng2-charts";

@Component({
  selector: 'app-greeks-compose',
  templateUrl: './greeks-compose.component.html',
  styleUrls: ['./greeks-compose.component.sass']
})
export class GreeksComposeComponent implements OnInit {

  private delivery: string;

  private today: string;

  private optionValuations: { delivery: string, poc: string, strike: string, v: OptionValuationSet }[] = [];

  private form;

  @ViewChild(BaseChartDirective, {static: true}) chart: BaseChartDirective;

  private data: ChartDataSets[];

  constructor(private greeksService: GreeksService,
              formBuilder: FormBuilder,
              private route: ActivatedRoute) {
    route.paramMap.subscribe(params => {
        this.delivery = params.get('delivery');
        this.today = params.get('date');
      }
    );
    this.form = formBuilder.group({
      putOrCall: 'Put',
      strike: '22000'
    })
  }

  ngOnInit() {
  }

  addOption(v) {
    this.greeksService.getOptionInfo(this.delivery, v.value.strike, v.value.putOrCall, this.today)
      .subscribe(valuation => this.optionValuations.push({
        delivery: this.delivery,
        poc: v.value.putOrCall,
        strike: v.value.strike,
        v: valuation
      }));
    this.refresh();
  }

  refresh() {
    this.data = [];
    console.warn(this.optionValuations);
    this.chart.update();
  }

}
