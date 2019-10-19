import {NgModule} from '@angular/core';
import {Routes, RouterModule} from '@angular/router';
import {VolatilityComponent} from "./volatility/volatility.component";


const routes: Routes = [{path: 'vol', component: VolatilityComponent}];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
