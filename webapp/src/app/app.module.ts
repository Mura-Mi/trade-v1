import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {VolatilityComponent} from './volatility/volatility.component';
import {HttpClientModule} from "@angular/common/http";
import {ChartsModule} from "ng2-charts";
import {GreeksComponent} from './greeks/greeks.component';
import {ReactiveFormsModule} from "@angular/forms";
import {GreeksComposeComponent} from './greeks-compose/greeks-compose.component';
import { MenuComponent } from './menu/menu.component';

@NgModule({
  declarations: [
    AppComponent,
    VolatilityComponent,
    GreeksComponent,
    GreeksComposeComponent,
    MenuComponent
  ],
  imports: [
    HttpClientModule,
    BrowserModule,
    AppRoutingModule,
    ChartsModule,
    ReactiveFormsModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
