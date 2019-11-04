import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {VolatilityComponent} from "./volatility/volatility.component";
import {GreeksComponent} from "./greeks/greeks.component";
import {GreeksComposeComponent} from "./greeks-compose/greeks-compose.component";


const routes: Routes = [
  {path: 'vol', component: VolatilityComponent},
  {path: 'greeks/:delivery/:strike/:poc/:valuationDate', component: GreeksComponent},
  {path: 'greeks-compose/:delivery/:valuationDate', component: GreeksComposeComponent},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
