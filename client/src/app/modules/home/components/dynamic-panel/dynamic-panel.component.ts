import {Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges} from '@angular/core';
import {AuctionService} from '../../../../shared/services/auction.service';
import {CategoryAttributes} from '../../../../shared/models/category-attributes';
import {AttributesValues} from '../../../../shared/models/attributes-values';
import {MatSelectChange} from '@angular/material/select';

@Component({
  selector: 'app-dynamic-panel',
  templateUrl: './dynamic-panel.component.html',
  styleUrls: ['./dynamic-panel.component.scss']
})
export class DynamicPanelComponent implements OnInit, OnChanges {


  @Output()
  selectEmitter: EventEmitter<CategoryAttributes[]> = new EventEmitter();
  @Input()
  private category: string;
  attributes: CategoryAttributes[];
  selectedValues: CategoryAttributes[];
  show = false;

  constructor(private auctionService: AuctionService) {
  }

  ngOnInit(): void {
    this.getData();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.category.previousValue != null && changes.category.previousValue !== changes.category.currentValue) {
      this.ngOnInit();
    }
  }

  getData(): void {
    this.auctionService.getCategoryAttributes(this.category).subscribe(
      res => {
        this.attributes = res.categoryAttributes;
        this.prepareObjectToReturn();
      },
      err => {
        alert('TODO');
      });
  }

  private prepareObjectToReturn() {
    this.selectedValues = [];
    for (const attribute of this.attributes) {
      const categoryObj = new CategoryAttributes();
      categoryObj.attribute = attribute.attribute;
      categoryObj.isSingleSelect = attribute.isSingleSelect;
      if (attribute.isSingleSelect) {
        categoryObj.attributeValues = [];
        const values = new AttributesValues();
        values.id = 1;
        values.value = '';
        categoryObj.attributeValues.push(values);
      }
      this.selectedValues.push(categoryObj);
    }
    this.show = this.attributes.length > 0;
    this.selectEmitter.emit(this.selectedValues);
  }

  doSomething($event: MatSelectChange, id: string) {
    for (const attribute of this.selectedValues) {
      if (attribute.attribute === id) {
        attribute.attributeValues[0].value = $event.value;
        break;
      }
    }
  }
}

