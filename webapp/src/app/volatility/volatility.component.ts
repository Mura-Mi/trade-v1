import { Component, OnInit } from '@angular/core';
import {VolatilityService} from "./volatility.service";
import {DailyVolatility} from "./DailyVolatility";

@Component({
  selector: 'app-volatility',
  templateUrl: './volatility.component.html',
  styleUrls: ['./volatility.component.sass']
})
export class VolatilityComponent implements OnInit {

  constructor(private volatilityService: VolatilityService) { }

  vol: DailyVolatility[];

  ngOnInit() {
     this.volatilityService.getHistoricalVolatility().subscribe(vols => this.vol = vols)
  }

}
