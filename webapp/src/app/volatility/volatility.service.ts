import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {DailyVolatility} from "./DailyVolatility";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class VolatilityService {

  constructor(private http: HttpClient) {
  }

  getHistoricalVolatility(): Observable<DailyVolatility[]> {
    const httpOptions = {
      headers: new HttpHeaders({ 'Content-Type': 'application/json' })
    };
    return this.http.get<DailyVolatility[]>("http://localhost:8080/vol.json", httpOptions)
  }
}
