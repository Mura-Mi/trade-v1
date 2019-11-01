import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {GreeksComposeComponent} from './greeks-compose.component';

describe('GreeksComposeComponent', () => {
  let component: GreeksComposeComponent;
  let fixture: ComponentFixture<GreeksComposeComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ GreeksComposeComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GreeksComposeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
