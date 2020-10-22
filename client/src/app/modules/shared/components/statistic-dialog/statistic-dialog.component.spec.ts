import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { StatisticDialogComponent } from './statistic-dialog.component';

describe('StatisticDialogComponent', () => {
  let component: StatisticDialogComponent;
  let fixture: ComponentFixture<StatisticDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ StatisticDialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(StatisticDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
