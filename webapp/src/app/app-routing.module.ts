import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {VolatilityComponent} from "./volatility/volatility.component";
import {GreeksComponent} from "./greeks/greeks.component";


const routes: Routes = [
  {path: 'vol', component: VolatilityComponent},
  {path: 'greeks/:delivery/:strike/:poc/:valuationDate', component: GreeksComponent},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
