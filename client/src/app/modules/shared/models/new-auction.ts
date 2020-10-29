import {AuctionBaseField} from './auction-base-field';
import {CategoryAttributes} from './category-attributes';

export class NewAuction extends AuctionBaseField {
  id: number;
  attributes: CategoryAttributes[];
  description: string;
  phone: string;
}
