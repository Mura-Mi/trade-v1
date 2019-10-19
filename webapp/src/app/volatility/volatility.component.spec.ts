import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { VolatilityComponent } from './volatility.component';

describe('VolatilityComponent', () => {
  let component: VolatilityComponent;
  let fixture: ComponentFixture<VolatilityComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ VolatilityComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(VolatilityComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
