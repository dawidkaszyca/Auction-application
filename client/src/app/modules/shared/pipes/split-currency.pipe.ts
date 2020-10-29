import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'splitCurrency'
})
export class SplitCurrencyPipe implements PipeTransform {
  transform(items: any, value: string): string {
    return items.substr(2) + ' ' + items.substr(0, 2);
  }
}
