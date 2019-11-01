import {Component, OnInit} from '@angular/core';
import {GreeksService} from "../greeks/greeks.service";

@Component({
  selector: 'app-greeks-compose',
  templateUrl: './greeks-compose.component.html',
  styleUrls: ['./greeks-compose.component.sass']
})
export class GreeksComposeComponent implements OnInit {

  private optionValuations: OptionValuationSet[];

  constructor(private greeksService: GreeksService) { }

  ngOnInit() {
  }

}
