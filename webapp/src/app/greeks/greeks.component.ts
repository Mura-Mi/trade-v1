import {Component, OnInit, ViewChild} from '@angular/core';
import {GreeksService} from "./greeks.service";
import {BaseChartDirective, Label} from "ng2-charts";
import {ChartDataSets} from "chart.js";
import {ActivatedRoute} from "@angular/router";
import {PutOrCall} from "../model/product/PutOrCall"

@Component({
  selector: 'app-greeks',
  templateUrl: './greeks.component.html',
  styleUrls: ['./greeks.component.sass']
})
export class GreeksComponent implements OnInit {

  constructor(private route: ActivatedRoute, private service: GreeksService) {
  }

  greeks: Greeks;
  labels: Label[];
  data: ChartDataSets[];
  @ViewChild(BaseChartDirective, {static: true}) chart: BaseChartDirective;

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      this.service.getOptionInfo(
          params.get('delivery'),
          params.get('strike'),
          params.get('poc') == 'P' ? PutOrCall.PUT : PutOrCall.CALL,
          params.get('valuationDate'),
        ).subscribe(optionValuationSet => {
          this.greeks = optionValuationSet.greeks;
          this.labels = optionValuationSet.payoff.map(p => p.atUnderlying);
          this.data = [{
            data: optionValuationSet.payoff.map(p => ({x: p.atUnderlying, y: p.intrinsic})),
            label: 'intrinsic value'
          },{
            data: optionValuationSet.payoff.map(p => ({x: p.atUnderlying, y: p.optionValue})),
            label: 'total option value'
          },{
            data: optionValuationSet.payoff.map(p => ({x: p.atUnderlying, y: p.optionValue - p.intrinsic})),
            label: 'time value'
          }]
        })
      }
    )
  }

}
