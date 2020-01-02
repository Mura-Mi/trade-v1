import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {VolatilityComponent} from "./volatility/volatility.component";
import {GreeksComponent} from "./greeks/greeks.component";
import {GreeksComposeComponent} from "./greeks-compose/greeks-compose.component";
import {MenuComponent} from "./menu/menu.component";


const routes: Routes = [
  {path: '', component: MenuComponent},
  {path: 'vol', component: VolatilityComponent},
  {path: 'greeks/:delivery/:strike/:poc/:valuationDate', component: GreeksComponent},
  {path: 'greeks-compose/:delivery/:date', component: GreeksComposeComponent},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
