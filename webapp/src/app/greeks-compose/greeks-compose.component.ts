import {Component, OnInit} from '@angular/core';
import {GreeksService} from "../greeks/greeks.service";
import {FormBuilder} from "@angular/forms";
import {ActivatedRoute} from "@angular/router";

@Component({
  selector: 'app-greeks-compose',
  templateUrl: './greeks-compose.component.html',
  styleUrls: ['./greeks-compose.component.sass']
})
export class GreeksComposeComponent implements OnInit {

  private delivery: string;

  private today: string;

  private optionValuations: OptionValuationSet[];

  private form;

  private data;

  constructor(private greeksService: GreeksService,
              formBuilder: FormBuilder,
              private route: ActivatedRoute) {
    route.paramMap.subscribe(params => {
        this.delivery = params.get('delivery');
        this.today = params.get('date');
      }
    );
    this.form = formBuilder.group({
      putOrCall: 'Put',
      strike: '22000'
    })
  }

  ngOnInit() {
  }

  addOption(v) {
    this.greeksService.getOptionInfo(this.delivery, v.value.strike, v.value.putOrCall, this.today)
      .subscribe(v => this.optionValuations.push(v))
  }

}
