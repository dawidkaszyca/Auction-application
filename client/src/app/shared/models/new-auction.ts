import {AuctionBaseField} from './auction-base-field';
import {CategoryAttributes} from './category-attributes';

export class NewAuction extends AuctionBaseField {
  attributes: CategoryAttributes[];
  description: string;
  email: string;
  phone: string;
}
