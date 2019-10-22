import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { VolatilityComponent } from './volatility/volatility.component';
import {HttpClientModule} from "@angular/common/http";
import {ChartsModule} from "ng2-charts";

@NgModule({
  declarations: [
    AppComponent,
    VolatilityComponent
  ],
  imports: [
    HttpClientModule,
    BrowserModule,
    AppRoutingModule,
    ChartsModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
