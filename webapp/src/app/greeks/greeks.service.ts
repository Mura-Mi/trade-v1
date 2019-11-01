import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable} from "rxjs";
import {PutOrCall} from "../model/product/PutOrCall";

@Injectable({
  providedIn: 'root'
})
export class GreeksService {

  constructor(private http: HttpClient) {
  }

  getOptionInfo(
    delivery: String,
    strike: String,
    poc: PutOrCall,
    valuationDate: String): Observable<OptionValuationSet> {
    const httpOptions = {
      headers: new HttpHeaders({'Content-Type': 'application/json'})
    };
    return this.http.get<OptionValuationSet>(
      `http://localhost:8080/greeks/${delivery}/${strike}/${poc}?date=${valuationDate}`,
      httpOptions)
  }
}
