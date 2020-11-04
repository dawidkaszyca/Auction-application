import {Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges} from '@angular/core';
import {AuctionService} from '../../../shared/services/auction.service';
import {CategoryAttributes} from '../../../shared/models/category-attributes';
import {AttributesValues} from '../../../shared/models/attributes-values';
import {MatSelectChange} from '@angular/material/select';

@Component({
  selector: 'app-dynamic-panel',
  templateUrl: './dynamic-panel.component.html',
  styleUrls: ['./dynamic-panel.component.scss']
})
export class DynamicPanelComponent implements OnInit, OnChanges {

  @Input()
  private category: string;
  attributes: CategoryAttributes[];
  @Input()
  selectedValues: CategoryAttributes[];
  @Output()
  selectEmitter: EventEmitter<CategoryAttributes[]> = new EventEmitter();
  show = false;

  constructor(private auctionService: AuctionService) {
  }

  ngOnInit(): void {
    this.getData();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.category?.previousValue != null && changes.category?.previousValue !== changes.category?.currentValue) {
      this.selectedValues = null;
      this.ngOnInit();
    }
    this.show = !!(changes.selectedValues && changes.selectedValues.currentValue.length > 0);
  }

  getData(): void {
    this.auctionService.getCategoryAttributes(this.category).subscribe(
      res => {
        this.attributes = res.categoryAttributes;
        this.prepareObjectToReturn();
      });
  }

  private prepareObjectToReturn() {
    if (!this.selectedValues) {
      this.selectedValues = [];
    }
    for (const attribute of this.attributes) {
      if (!this.checkIfAttributeAlreadyExist(attribute)) {
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
    }
    this.show = this.attributes.length > 0;
    this.selectEmitter.emit(this.selectedValues);
  }

  private checkIfAttributeAlreadyExist(attribute: CategoryAttributes) {
    for (const obj of this.selectedValues) {
      if (obj.attribute === attribute.attribute) {
        return true;
      }
    }
    return false;
  }

  selectValue($event: MatSelectChange, id: string) {
    for (const attribute of this.selectedValues) {
      if (attribute.attribute === id) {
        attribute.attributeValues[0].value = $event.value;
        break;
      }
    }
  }

  getSelectedValue(attribute: CategoryAttributes): string {
    if (!this.selectedValues) {
      return '';
    }
    const attr = this.selectedValues.filter(it => it.attribute === attribute.attribute)[0];
    if (attr?.attributeValues) {
      return attr.attributeValues[0].value;
    }
    return '';
  }
}

