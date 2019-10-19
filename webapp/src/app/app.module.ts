import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { VolatilityComponent } from './volatility/volatility.component';
import {HttpClientModule} from "@angular/common/http";
import { ChartModule, AccumulationChartModule, RangeNavigatorModule, SparklineModule, SmithchartModule, StockChartModule } from '@syncfusion/ej2-angular-charts';

@NgModule({
  declarations: [
    AppComponent,
    VolatilityComponent
  ],
  imports: [
    HttpClientModule,
    BrowserModule,
    AppRoutingModule,
    ChartModule, AccumulationChartModule, RangeNavigatorModule, SparklineModule, SmithchartModule, StockChartModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
